package com.temenos.arc.security.authenticationserver.server;

import java.util.Calendar;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.ftress.server.UserManagement;

import junit.framework.TestCase;

/**
 * Tests UserManagement class - will result in calls to authentication server 
 * @author jannadani
 *
 */
public class UserManagementTest extends TestCase {
	
	UserManagement um = null;
    final String T24_USER = "IvanaP";
    final String ARC_USER = "DUMMY_ARC_USER";
    static AuthenticationServerConfiguration noEncryptionConfig; 
    static AuthenticationServerConfiguration encryptionConfig;
    static AuthenticationServerConfiguration noEncSharedUserConfig; 
    
    static {
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/static.config");
        encryptionConfig = AuthenticationServerConfiguration.getStatic();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "INTEG");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/no_crypto.config");
        noEncryptionConfig = AuthenticationServerConfiguration.getStatic();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "INTEG");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/no_crypto_shared_userid.config");
        noEncSharedUserConfig = AuthenticationServerConfiguration.getStatic();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testUserExists() {        
        um = new UserManagement(noEncryptionConfig);
    	userExistsHelper();
    }

    public void testAddUser() {
        um = new UserManagement(noEncryptionConfig);
        addUserHelper(noEncryptionConfig);
    }

    public void testGetArcUserId() {
        um = new UserManagement(noEncryptionConfig);
        assertTrue(um.getArcUserId(T24_USER).matches("[0-9]*"));
    }

    public void testRemoveUser() {
        um = new UserManagement(noEncryptionConfig);
        removeUserHelper();
    }

    /**
     * assumes that the encrypted form of "TONYC1" is in the auth server
     * using the current key, this means that the t24User attribute must be "kkFTsyG8VtP1e6Ij08NYxA=="
     *
     */
    public void testUserExistsWithEncryption() {        
        um = new UserManagement(encryptionConfig);
        userExistsHelper();
    }
    
    public void testAddUserWithEncryption() {        
        um = new UserManagement(encryptionConfig);
        addUserHelper(encryptionConfig);
    }

    public void testGetArcUserIdWithEncryption() {        
        um = new UserManagement(encryptionConfig);
        assertTrue(um.getArcUserId(T24_USER).matches("[0-9]*"));
    }

    public void testRemoveUserWithEncryption() {        
        um = new UserManagement(encryptionConfig);
        removeUserHelper();
    }
    
    public void testAddUserSharedUser() {
        um = new UserManagement(noEncSharedUserConfig);
        addUserHelper(noEncSharedUserConfig);
    }

    public void testGetArcUserIdSharedUser() {
    	// Make sure the T24 and ARC user ids are the same
        um = new UserManagement(noEncSharedUserConfig);
        String userId = um.getArcUserId(T24_USER);
        assertTrue(userId.equals(T24_USER));
    }

    public void testRemoveUserSharedUser() {
        um = new UserManagement(noEncSharedUserConfig);
        removeUserHelper();
    }

    private void userExistsHelper() {
        // success
        assertTrue(um.userExists("TONYC1"));
        // failure
        assertFalse(um.userExists("garbageId"));
    }
    
    private void addUserHelper(AuthenticationServerConfiguration config) {
        assertFalse(um.userExists(T24_USER));
        
        String secret = "petsname";
        // encrypt the secret
        CryptographyService cs = CryptographyService.getInstance(config);
        String encSecret = cs.encrypt(secret, true);
        um.addUser(T24_USER, encSecret, Calendar.getInstance(),null);
        
        assertTrue(um.userExists(T24_USER));
    }

    private void removeUserHelper() {
        assertTrue(um.userExists(T24_USER));
               
        um.removeUser(T24_USER);
        
        assertFalse(um.userExists(T24_USER));
    }

}
