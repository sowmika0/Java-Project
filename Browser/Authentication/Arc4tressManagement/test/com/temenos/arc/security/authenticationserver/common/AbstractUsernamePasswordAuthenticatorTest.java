package com.temenos.arc.security.authenticationserver.common;


import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;

import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.UsernamePasswordAuthenticator;

import junit.framework.TestCase;

public abstract class AbstractUsernamePasswordAuthenticatorTest extends TestCase {

    protected AuthenticationServerConfiguration config;
    private boolean shouldUseEncryption;

    public AbstractUsernamePasswordAuthenticatorTest() {
        super();
        shouldUseEncryption = true;
    }
    public AbstractUsernamePasswordAuthenticatorTest(boolean dontUseEncryption) {
        super();
        shouldUseEncryption = !dontUseEncryption;
    }

    public void testLoginFailure() {
        // this is encrypted garbageId
    	AbstractAuthenticator auth = initialiseUPAuthenticator("okpRVm5Gm62EXgOaz83TuA==", config, shouldUseEncryption);
    	try {
    		auth.authenticate();
    		fail("should not login");
    	} catch(FailedLoginException e) {			
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	} catch (AccountExpiredException e) {
    		fail(e.getMessage());
    	}		
    }

    public void testLogin() {
    	String name = config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER);
    	AbstractAuthenticator auth = initialiseUPAuthenticator(name, config, shouldUseEncryption);
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

    static AbstractAuthenticator initialiseUPAuthenticator(String name, AuthenticationServerConfiguration config, boolean shouldUseEncryption) {
        boolean dummy = false;
    	NameCallback nameC = new NameCallback("foo");
        PasswordCallback passwordC = new PasswordCallback("bar", dummy);

        String decName = shouldUseEncryption ? 
                                CryptographyService.getInstance(config).decrypt(name, true)
                                : name;
        String password = config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
        String decPassword = shouldUseEncryption ?
                            CryptographyService.getInstance(config).decrypt(password, true)
                            : password; 
                            
        
    	passwordC.setPassword(decPassword.toCharArray());
        nameC.setName(decName);
    	
        System.out.println(decName + ":" + decPassword);
        
    	// Instantiate UPAuthenticator
    	AbstractAuthenticator auth = null;
    	try {
    		auth = new UsernamePasswordAuthenticator(nameC, passwordC, config);
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	}
    	
    	assertNull(auth.getArcSession());
    	return auth;
    }
}
