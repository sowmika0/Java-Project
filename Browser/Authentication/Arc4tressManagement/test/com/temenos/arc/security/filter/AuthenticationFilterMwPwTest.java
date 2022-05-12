package com.temenos.arc.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.FilePathhelper;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.NullCallbackHandler;
import com.temenos.arc.security.jaas.OtpFromFile;
import com.temenos.arc.security.listener.AuthenticationListener;

/** Tests login process from LoginModules through to generation of the impersonation 
 * login request for Browser.
 * @author jannadani
 *
 */ 
public class AuthenticationFilterMwPwTest extends TestCase {
    ArcLoginModule pwModule = new ArcLoginModule();
    ArcLoginModule mwModule = new ArcLoginModule();
    Subject subject = new Subject();
    JaasConfiguration pwConfig;
    JaasConfiguration mwConfig;
    Map sharedState = new HashMap();
    protected AuthenticationServerConfiguration configs[];
    
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("java.security.auth.login.config", "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/no_crypto.config");
        String filePath = FilePathhelper.getPathToFilter() + "authenticationFilterMwPwTest.config";
        Map configMaps [] = new ConfigurationFileParser(filePath, "TEST").parse();
        configs = new AuthenticationServerConfiguration[configMaps.length];
        pwModule = new ArcLoginModule();
        mwModule = new ArcLoginModule();
        for (int i = 0; i < configMaps.length; ++i) {
            configs[i] = new JaasConfiguration(configMaps[i]);
        }        
    }
    
    public void testFilter() {
    	String userId = "786786786";
    	// First add the device authenticator to the user
//    	TestUserManagement userManTest = new TestUserManagement();
//    	userManTest.setConfiguration();
//		userManTest.addDeviceToUser(userId);
        initLoginModules();
        
        login();
        
        HttpServletRequest request = new MockRequest(subject);
        ServletResponse response = new MockResponse();
        MockFilterChain filterChain = new MockFilterChain();

        AuthenticationFilter toTest = new AuthenticationFilter();
        try {
            toTest.doFilter(request, response, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception in doFilter: " + e.getMessage());
        }
        
        // check the generated request that has been captured in the MockFilterChain 
        Map parameters = filterChain.getParameters();
        assertTrue(parameters.containsKey(LoginParameterisedRequest.COMMAND));
        String value = ((String[]) parameters.get(LoginParameterisedRequest.COMMAND))[0];
        assertEquals("login", value);

        assertTrue(parameters.containsKey(LoginParameterisedRequest.REQUEST_TYPE));
        value = ((String[]) parameters.get(LoginParameterisedRequest.REQUEST_TYPE))[0];
        assertEquals("CREATE.SESSION", value);

        assertTrue(parameters.containsKey(LoginParameterisedRequest.SIGNON));
        value = ((String[]) parameters.get(LoginParameterisedRequest.SIGNON))[0];
        assertEquals("TONYC1", value);

        assertTrue(parameters.containsKey(LoginParameterisedRequest.PASSWORD));
        value = ((String[]) parameters.get(LoginParameterisedRequest.PASSWORD))[0];
        assertEquals("456789", value);
        
        assertTrue(parameters.containsKey(LoginParameterisedRequest.COUNTER));
        value = ((String[]) parameters.get(LoginParameterisedRequest.COUNTER))[0];
        assertEquals("0", value);
		
        // test a normal browser request (refresh)
        request = new MockRequest(request.getUserPrincipal(), request.getSession());
        // Add a dummy value to check that it goes into the filter chain afterwards.
        request.getParameterMap().put("hello", "world");
        response = new MockResponse();
        filterChain = new MockFilterChain();
        try {
            toTest.doFilter(request, response, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception in doFilter: " + e.getMessage());
        }
        
        // Now check return data - There should only be one value which is the one we added earlier
        parameters = filterChain.getParameters();
        assertTrue(parameters.containsKey("hello"));
        value = (String) parameters.get("hello");
        assertEquals("world", value);
        
        
        // Test logging out 
        HttpServletRequest logoutRequest = getLogoutRequest(request);
        try {
            toTest.doFilter(logoutRequest, response, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Filter exception on logout: " + e.getMessage());
        }
        assertFalse(((MockSession)logoutRequest.getSession(false)).isValid());
        
        //TODO check Subject & principals have been tidied up

//        userManTest.unBind("0", userId);
    }
    
    private void initLoginModules() {
        initPasswordConfig();
        initMemWordConfig();
        pwModule.initialize(subject, 
                                // this simulates the container's j_username & j_password callback handler 
                                new AutomatedPwMwCallbackHandler(pwConfig, mwConfig),      
                                sharedState, 
                                pwConfig.getMap());
        mwModule.initialize(subject, 
                            new NullCallbackHandler(), 
                            sharedState, 
                            mwConfig.getMap());                
    }
    
    /** 
     * Simulates Jaas/LoginContext calls 
     */
    private void login() {
        try {
            assertTrue(pwModule.login());
            assertTrue(mwModule.login());
            assertTrue(pwModule.commit());
            assertTrue(mwModule.commit());
        } catch (LoginException e) {
            e.printStackTrace();
            fail("Login exception: " + e.getMessage());
        }
    }
    
    private void initPasswordConfig() {
        pwConfig = (JaasConfiguration) configs[0];
        Map properties = pwConfig.getMap();
//        properties.put(AuthenticationServerConfiguration.UP_AUTH_PASSWORD, "memData");
    }
    private void initMemWordConfig() {
        mwConfig = (JaasConfiguration) configs[1];
        Map properties = mwConfig.getMap();
    }

    /**
     * @param request HttpServletRequest that was used to successfully login
     * @return an HttpServletRequest that can be used to logout  
     */
    private HttpServletRequest getLogoutRequest(HttpServletRequest request) {
        MockSession session = (MockSession) request.getSession(false);

        session.setListener(new AuthenticationListener());
        
        // now construct a logout request
        MockRequest logoutRequest = new MockRequest(request.getUserPrincipal(), session);
        logoutRequest.getParameterMap().put(LoginParameterisedRequest.COMMAND, "globusCommand");
        logoutRequest.getParameterMap().put("application", "SIGN.OFF");
        assertTrue(session.isValid());
        return logoutRequest;
    }
}

/** 
 * Callback handler that uses the automated device Otp and a deviceConfig defined PIN
 * to simulate the credentials and passed to our login modules by JAAS 
 * @author jannadani
 *
 */
class AutomatedPwMwCallbackHandler implements CallbackHandler {

    private final JaasConfiguration pwConfig;
    private final JaasConfiguration mwConfig;
    private OtpFromFile otpHelper = new OtpFromFile();

    public AutomatedPwMwCallbackHandler(final JaasConfiguration pwConfig,
                                         final JaasConfiguration mwConfig) {
        this.pwConfig = pwConfig;
        this.mwConfig = mwConfig;        
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        boolean usernameHandled = false;
        boolean passwordHandled = false;
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                String username = pwConfig.getConfigValue(JaasConfiguration.UP_AUTH_USER);
                ((NameCallback) callbacks[i]).setName(username);
                usernameHandled = true;
            }
            if (callbacks[i] instanceof PasswordCallback) {
                String password = pwConfig.getConfigValue(JaasConfiguration.UP_AUTH_PASSWORD);;
                String pin = mwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
                String concatenatedPassword = password + pwConfig.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER) + pin;
                System.out.println(concatenatedPassword);
                ((PasswordCallback) callbacks[i]).setPassword(concatenatedPassword.toCharArray());
                passwordHandled = true;
            }
        }
        if (!(usernameHandled && passwordHandled)) {
            throw new IOException("Failed to find necessary credentials to handle callbacks");
        }
    }
}

