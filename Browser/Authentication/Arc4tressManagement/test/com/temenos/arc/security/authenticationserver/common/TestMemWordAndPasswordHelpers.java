package com.temenos.arc.security.authenticationserver.common;

import java.util.Map;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;

import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers;

import junit.framework.TestCase;

public class TestMemWordAndPasswordHelpers extends TestCase {
    AuthenticationServerConfiguration memConfig;
    AuthenticationServerConfiguration pwConfig;
    static final String memWordValue = "memData";    
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "TEST");
        String path = FilePathHelper.getPathToSecurityDir() + "authenticationserver/common/testHelpers.config";
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, path);
        Map props[] = new ConfigurationFileParser().parse();
        memConfig = new AuthenticationServerConfiguration(props[0]);
        pwConfig = new AuthenticationServerConfiguration(props[1]);
    }

    /**
     * Requires ARC_AUTO_DEVICE_USER to have Customer Mem Word authenticator enabled, and set to <code>memWordValue</code>,
     * so seed positions shoudl be in the range 1...7 
     *
     */
    public void testGetMemWordSeedPositions() {
    	FtressHelpers.setConfig(memConfig);
        int pos[] = FtressHelpers.getMemWordSeedPositions(memConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER));
        int numSeeds = Integer.parseInt(memConfig.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_NUM_SEEDS));
        assertEquals(numSeeds, pos.length);
        for (int i = 0; i < numSeeds; ++i) {
            System.out.println("Seed position: " + pos[i]);
            assertTrue(0 < pos[i]);
            assertTrue( pos[i] <= memWordValue.length());
        }
    }
    
    public void testChangePassword() {
        String newPassword = "myNewMemData"; 
        FtressHelpers.setConfig(pwConfig);
        String oldPassword = pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
        AbstractAuthenticator auth = UsernamePasswordAuthenticatorTest.initialiseUPAuthenticator(
                pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER), 
                pwConfig, 
                false); 
        try {
            auth.authenticate();
            // success!
        }catch (FailedLoginException e) {            
            fail();
        }catch (AccountExpiredException e) {
            fail();
        }
        ArcSession session = auth.getArcSession();
        assertNotNull(session.toString());
        FtressHelpers.changeOwnPassword(pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER),oldPassword, newPassword);
        auth.logoff();

        // old password shouldn't work anymore
        try {
            auth.authenticate();
            fail();
        }catch (FailedLoginException e) {            
            // success!
        }catch (AccountExpiredException e) {
            fail();
        }
        
        // now try the new password
        pwConfig.getMap().put(AuthenticationServerConfiguration.UP_AUTH_PASSWORD, newPassword);
        assertEquals(newPassword, pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD));
        assertNotSame(oldPassword, newPassword);
        auth = UsernamePasswordAuthenticatorTest.initialiseUPAuthenticator(
                            pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER), 
                            pwConfig, 
                            false); 
        try {
            auth.authenticate();
            // success!
        }catch (FailedLoginException e) {            
            fail();
        }catch (AccountExpiredException e) {
            fail();
        }
        session = auth.getArcSession();
        assertNotNull(session.toString());
        
        // now change back to the old password
        FtressHelpers.changeOwnPassword(pwConfig.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER), newPassword, oldPassword);
        auth.logoff();        
    }
    public void testPasswordExpiry() {
    	FtressHelpers.setConfig(pwConfig);
        String userId="TEST_EXPIRY";
        String password = "memData";
        boolean hasExpired = FtressHelpers.hasPasswordExpired(userId, password);
        assertTrue(hasExpired);
        
        userId = "ARC_AUTO_DEVICE_USER";
        hasExpired = FtressHelpers.hasPasswordExpired(userId, password);
        assertFalse(hasExpired);
    }
}
