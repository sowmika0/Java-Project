package com.temenos.arc.security.authenticationserver.ftress;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.DeviceAuthenticator;
import com.temenos.arc.security.authenticationserver.ftress.UsernamePasswordAuthenticator;
import com.temenos.arc.security.authenticationserver.web.TestUserManagement;
import com.temenos.arc.security.jaas.AuthenticationByDeviceConfigurationFactory;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.OtpFromFile;

/** This test relies on the configuration specified in AuthenticationByDeviceConfigurationFactory. 
 * Specifically, the user specified therein must have a device authenticator configured to use OE
 * device id 10359. This device's file (test/data/testOATHSDB.sdb) should be imported into 4TRESS
 * and then the checked in test/data/testotps.txt file will be valid from the beginning.  
 *  The test consumes OTPs from a copy of this text file that stored in the same directory as this source file.
 *  Note that this initial copy is performed by this test class.      
 */
public class DeviceAuthenticatorTest extends TestCase {
    JaasConfiguration config;
    static OtpFromFile otpHelper = null; 
    
	protected void setUp() throws Exception {
		super.setUp();
        AuthenticationServerConfiguration asc = AuthenticationByDeviceConfigurationFactory.getInstance().getConfiguration();
        if (asc instanceof JaasConfiguration) {
            config = (JaasConfiguration) asc;
        } else {
            throw new IllegalStateException("configuration error in test case");
        }
        if (otpHelper == null) {
            otpHelper = new OtpFromFile();
        }
	}
    
    public void setConfig(JaasConfiguration config) {
        this.config = config;
    }
    
    public void setOtpHelper() {
        if (otpHelper == null) {
            otpHelper = new OtpFromFile();
        }
    }
    
    public void testLoginFailure() {
        AbstractAuthenticator auth = initialiseAuthenticator("garbageId", "fakeotp");
        try {
            auth.authenticate();
            fail("should not login");
        } catch(FailedLoginException e) {           
        } catch (ArcAuthenticationServerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (AccountExpiredException e) {
            fail(e.getMessage());
        }       
    }

    public void testLogin() {
    	// Create the device authenticator first
    	TestUserManagement testUserMgmt = new TestUserManagement();
    	testUserMgmt.addDeviceToUser(config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER));
    	
        doLogin();
        testUserMgmt.unBind("0", config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER));
    }

	public void doLogin() {
		AbstractAuthenticator auth = initialiseAuthenticator(
                            config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER), 
                                                        otpHelper.getOtp());
        try {
            auth.authenticate();
        } catch(FailedLoginException e) {
            fail(e.getMessage());
        } catch (ArcAuthenticationServerException e) {
            fail(e.getMessage());
        } catch (AccountExpiredException e) {
            fail(e.getMessage());
        }       
        
        ArcSession sessionId = auth.getArcSession(); 
        assertNotNull(sessionId);       
        assertNotNull(sessionId.toString());
        
        Authenticatable auth2 = null;
        try {
            auth2 = new UsernamePasswordAuthenticator(sessionId, config); 
        } catch (ArcAuthenticationServerException e) {
            fail(e.getMessage());
        }
                
        // logout
        auth2.logoff();
	}

    private DeviceAuthenticator initialiseAuthenticator(final String name, final String otp) {
        NameCallback nameC = new NameCallback("ignored");
        nameC.setName(name);
        boolean dummy = false;
        PasswordCallback passwordC = new PasswordCallback("ignored", dummy);
        passwordC.setPassword(otp.toCharArray());
        
        // Instantiate Authenticator
        DeviceAuthenticator auth = null;
        try {
            auth = new DeviceAuthenticator(nameC, passwordC, config);
        } catch (ArcAuthenticationServerException e) {
            fail(e.getMessage());
        }        
        assertNull(auth.getArcSession());
        return auth;
    }       
}