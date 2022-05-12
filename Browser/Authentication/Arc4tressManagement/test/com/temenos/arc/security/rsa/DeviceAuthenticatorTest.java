package com.temenos.arc.security.rsa;

import java.util.Map;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;

import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.rsa.DeviceAuthenticator;
import com.temenos.arc.security.jaas.JaasConfiguration;

public class DeviceAuthenticatorTest extends TestCase {
    JaasConfiguration config;
    String name="vraghavan";
    String otp="125412374969";
    String falseotp="8753495730";
    
	protected void setUp() throws Exception {
		super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/rsa.device.arc_jaas.config");
		AuthenticationServerConfiguration tempConfig = AuthenticationServerConfiguration.getStatic();
		Map configMap = tempConfig.getMap();
		config = new JaasConfiguration(configMap);
	}
    
    public void testLoginFailure() {
        NameCallback nameC = new NameCallback("ignored");
        nameC.setName(name);
        boolean dummy = false;
        PasswordCallback passwordC = new PasswordCallback("ignored", dummy);
        passwordC.setPassword(falseotp.toCharArray());
        
        // Instantiate Authenticator
        DeviceAuthenticator auth = null;
        try {
            auth = new DeviceAuthenticator(nameC, passwordC, config);
        } catch (ArcAuthenticationServerException e) {
            fail(e.getMessage());
        }
        try {
        	auth.authenticate();
        } catch (AccountExpiredException e) {
        	System.out.println("Account expired");
        	fail("Account expired");
        } catch (FailedLoginException e) {
        	System.out.println("Login Failed");
        	return;
        }
        fail("Successful login with incorrect passcode");
    }

    public void testLogin() {
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
        try {
        	auth.authenticate();
        } catch (AccountExpiredException e) {
        	System.out.println("Account expired");
        	fail("Account expired");
        } catch (FailedLoginException e) {
        	System.out.println("Login Failed");
        	fail("Login Failed");
        }
    	
    }

}