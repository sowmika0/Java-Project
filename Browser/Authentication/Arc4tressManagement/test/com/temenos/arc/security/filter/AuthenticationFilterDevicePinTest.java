package com.temenos.arc.security.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.web.AuthenticationByPinConfigurationFactory;
import com.temenos.arc.security.authenticationserver.web.TestUserManagement;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.AuthenticationByDeviceConfigurationFactory;
import com.temenos.arc.security.jaas.FilePathhelper;
import com.temenos.arc.security.jaas.NullCallbackHandler;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.OtpFromFile;
import com.temenos.arc.security.listener.AuthenticationListener;

/** Tests login process from LoginModules through to generation of the impersonation 
 * login request for Browser.
 * @author jannadani
 *
 */ 
public class AuthenticationFilterDevicePinTest extends TestCase {
    ArcLoginModule deviceModule = new ArcLoginModule();
    ArcLoginModule pinModule = new ArcLoginModule();
    Subject subject = new Subject();
    JaasConfiguration deviceConfig;
    JaasConfiguration pinConfig;
    Map sharedState = new HashMap();
    protected AuthenticationServerConfiguration configs[];
    
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("java.security.auth.login.config", "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/no_crypto.config");
        String filePath = FilePathhelper.getPathToFilter() + "authenticationFilterDevicePinTest.config";
        Map configMaps [] = new ConfigurationFileParser(filePath, "TEST").parse();
        configs = new AuthenticationServerConfiguration[configMaps.length];
        deviceModule = new ArcLoginModule();
        pinModule = new ArcLoginModule();
        for (int i = 0; i < configMaps.length; ++i) {
            configs[i] = new JaasConfiguration(configMaps[i]);
        }        
    }
    
    public void testFilter() {
    	String userId = "786786786";
    	// First add the device authenticator to the user
    	TestUserManagement userManTest = new TestUserManagement();
    	userManTest.setConfiguration();
		userManTest.addDeviceToUser(userId);
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

        userManTest.unBind("0", userId);
    }
    
    private void initLoginModules() {
        initDeviceConfig();
        initPinConfig();
        deviceModule.initialize(subject, 
                                // this simulates the container's j_username & j_password callback handler 
                                new AutomatedOtpPinCallbackHandler(deviceConfig, pinConfig),      
                                sharedState, 
                                deviceConfig.getMap());
        pinModule.initialize(subject, 
                            new NullCallbackHandler(), 
                            sharedState, 
                            pinConfig.getMap());                
    }
    
    /** 
     * Simulates Jaas/LoginContext calls 
     */
    private void login() {
        try {
            assertTrue(deviceModule.login());
            assertTrue(pinModule.login());
            assertTrue(deviceModule.commit());
            assertTrue(pinModule.commit());
        } catch (LoginException e) {
            e.printStackTrace();
            fail("Login exception: " + e.getMessage());
        }
    }
    
    private void initDeviceConfig() {
        deviceConfig = (JaasConfiguration) AuthenticationByDeviceConfigurationFactory.getInstance().getConfiguration();
        Map properties = deviceConfig.getMap();
        properties.put(JaasConfiguration.AUTHENTICATION_DELEGATE,
                "com.temenos.arc.security.authenticationserver.ftress.DeviceAuthenticator");
        properties.put(JaasConfiguration.AUTHENTICATION_COMMITTER,
                "com.temenos.arc.security.jaas.JaasCommitter");
        properties.put(JaasConfiguration.CALLBACK_OVERRIDE,"");
    }
    private void initPinConfig() {
        pinConfig = (JaasConfiguration) AuthenticationByPinConfigurationFactory.getInstance().getConfiguration();
        Map properties = pinConfig.getMap();
        properties.put(JaasConfiguration.AUTHENTICATION_DELEGATE,
                "com.temenos.arc.security.authenticationserver.ftress.JaasUsernamePasswordAuthenticator");
        properties.put(JaasConfiguration.AUTHENTICATION_COMMITTER,
                "com.temenos.arc.security.authenticationserver.ftress.T24AttributeCommitter");
        properties.put(JaasConfiguration.CALLBACK_OVERRIDE,"");
        properties.put(AuthenticationServerConfiguration.CREATE_SESSION, "false");
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
class AutomatedOtpPinCallbackHandler implements CallbackHandler {

    private final JaasConfiguration deviceConfig;
    private final JaasConfiguration pinConfig;
    private OtpFromFile otpHelper = new OtpFromFile();

    public AutomatedOtpPinCallbackHandler(final JaasConfiguration deviceConfig,
                                         final JaasConfiguration pinConfig) {
        this.deviceConfig = deviceConfig;
        this.pinConfig = pinConfig;        
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        boolean usernameHandled = false;
        boolean passwordHandled = false;
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                String username = deviceConfig.getConfigValue(JaasConfiguration.UP_AUTH_USER);
                ((NameCallback) callbacks[i]).setName(username);
                usernameHandled = true;
            }
            if (callbacks[i] instanceof PasswordCallback) {
                String password = otpHelper.getOtp();
                String pin = pinConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
                String concatenatedPassword = password + deviceConfig.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER) + pin;
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

/**
 * Simulates the HttpServletRequest passed to us by the servlet container.
 * We only meaningfully implement those methods that are used by the 
 * com.temenos.arc.security.jaas and com.temenos.arc.security.filter packages. 
 * @author jannadani
 *
 */
class MockRequest implements HttpServletRequest {
    private Principal authenticatedUser;
    private Map parameterMap = new HashMap();
    private MockSession session = null; //new MockSession();
    
    public MockRequest(Principal principal, HttpSession session) {
        authenticatedUser = principal;
        this.session = (MockSession) session;
    }
    
    public MockRequest(Subject subject) {
        Set principals = subject.getPrincipals(ArcUserPrincipal.class);
        if (principals == null
          || principals.size() != 1) {
            throw new IllegalStateException("Subject not in correct state for MockRequest.");
        }
        authenticatedUser = (Principal) principals.iterator().next();
    }
    
    public String getAuthType() {        
        return HttpServletRequest.FORM_AUTH;
    }

    public String getContextPath() {
        return null;
    }

    public Cookie[] getCookies() {
        return null;
    }

    public long getDateHeader(String arg0) {
        return 0;
    }

    public String getHeader(String arg0) {
        return null;
    }

    public Enumeration getHeaderNames() {
        return null;
    }

    public Enumeration getHeaders(String arg0) {
        return null;
    }

    public int getIntHeader(String arg0) {
        return 0;
    }

    public String getMethod() {
        return null;
    }

    public String getPathInfo() {
        return null;
    }

    public String getPathTranslated() {
        return null;
    }

    public String getQueryString() {
        return null;
    }

    public String getRemoteUser() {
        return null;
    }

    public String getRequestedSessionId() {
        return null;
    }

    public String getRequestURI() {
        return null;
    }

    public StringBuffer getRequestURL() {
        return null;
    }

    public String getServletPath() {
        return null;
    }

    public HttpSession getSession() {
        return session;
    }

    public HttpSession getSession(boolean create) {
        if (session == null) {            
            session = new MockSession();
        }
        return session;
    }

    public Principal getUserPrincipal() {        
        return authenticatedUser;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean isRequestedSessionIdValid() {
        return false;
    }

    public boolean isUserInRole(String arg0) {
        return false;
    }

    public Object getAttribute(String arg0) {
        return null;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public int getContentLength() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration getLocales() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }

    public String getParameter(String key) {
        return (String) parameterMap.get(key);
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public Enumeration getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String arg0) {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public String getRealPath(String arg0) {
        return null;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public int getRemotePort() {
        return 0;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public boolean isSecure() {
        return false;
    }

    public void removeAttribute(String arg0) {
    }

    public void setAttribute(String arg0, Object arg1) {
    }

    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {        
    }      
}

/** 
 * No-op ServletResponse mock object
 */ 
class MockResponse implements HttpServletResponse {

    public void addCookie(Cookie arg0) {
	}

	public void addDateHeader(String arg0, long arg1) {
	}

	public void addHeader(String arg0, String arg1) {
	}

	public void addIntHeader(String arg0, int arg1) {
	}

	public boolean containsHeader(String arg0) {
		return false;
	}

	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	public String encodeRedirectURL(String arg0) {
		return null;
	}

	public String encodeUrl(String arg0) {
		return null;
	}

	public String encodeURL(String arg0) {
		return null;
	}

	public void sendError(int arg0, String arg1) throws IOException {
	}

	public void sendError(int arg0) throws IOException {
	}

	public void sendRedirect(String arg0) throws IOException {
	}

	public void setDateHeader(String arg0, long arg1) {
	}

	public void setHeader(String arg0, String arg1) {
	}

	public void setIntHeader(String arg0, int arg1) {
	}

	public void setStatus(int arg0, String arg1) {
	}

	public void setStatus(int arg0) {
	}

	public void flushBuffer() throws IOException {
    }

    public int getBufferSize() {
        return 0;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public String getContentType() {
        return null;
    }

    public Locale getLocale() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    public PrintWriter getWriter() throws IOException {
        return null;
    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize(int arg0) {
    }

    public void setCharacterEncoding(String arg0) {
    }

    public void setContentLength(int arg0) {
    }

    public void setContentType(String arg0) {
    }

    public void setLocale(Locale arg0) {
    }    
}

/** 
 * Captures the parameters in the request object that would be passed to BrowserServlet (or 
 * the next filter in the chain).
 * @author jannadani
 *
 */
class MockFilterChain implements FilterChain {
    private Map parameters = new HashMap();
    public Map getParameters() {
        return parameters;
    }
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        parameters = request.getParameterMap();
    }
    
}

/**
 * Minimal mock HttpSession implementation that captures the attributes added in by the security packages, 
 * and simulates the sessionDestroyed event when invalidated.  
 * @author jannadani
 *
 */
class MockSession implements HttpSession {
    Map attributes = new HashMap();
    private boolean isValid = true;
    private AuthenticationListener listener;

    public void setListener(AuthenticationListener listener) {        
        this.listener = listener;
    }
    
    public MockSession() {
        System.out.println("creating mock session: " + this);
    }
    public boolean isValid() {
        return isValid;
    }

    public Object getAttribute(String key) {        
        return attributes.get(key);
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public long getCreationTime() {
        return 0;
    }

    public String getId() {
        return null;
    }

    public long getLastAccessedTime() {
        return 0;
    }

    public int getMaxInactiveInterval() {
        return 0;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public HttpSessionContext getSessionContext() {
        return null;
    }

    public Object getValue(String arg0) {
        return null;
    }

    public String[] getValueNames() {
        return null;
    }

    public void invalidate() {
        System.out.println("invalidating MockSession: " + this);
        listener.sessionDestroyed(new HttpSessionEvent(this));
        isValid = false;
    }

    public boolean isNew() {
        return false;
    }

    public void putValue(String arg0, Object arg1) {
    }

    public void removeAttribute(String arg0) {
    }

    public void removeValue(String arg0) {
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void setMaxInactiveInterval(int arg0) {
    }    
}