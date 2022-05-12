package com.temenos.arc.security.authenticationserver.rsa.server;

import java.io.File;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;

import junit.framework.TestCase;

public class UserManagementTest extends TestCase {
	UserManagement um;
    static AuthenticationServerConfiguration encryptionConfig;

	static {
		System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
		System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/rsa-server.config");
        encryptionConfig = AuthenticationServerConfiguration.getStatic();
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		um = new UserManagement(encryptionConfig);
	}

	public void testAddUser() {
		// Delete the users file before test so that we can test creation of the file
		File userDetailsFile = new File(encryptionConfig.getConfigValue(AuthenticationServerConfiguration.ARC_USER_DETAILS_FILE));
		if (userDetailsFile.exists()) {
			userDetailsFile.delete();
		}
		// test adding user when no file exists
		um.addUser("testUser", null, null,null);
		// test adding user when file exists
		um.addUser("testUser2", null, null,null);

		// negative cases
		try {
			// blank user
			um.addUser("", "blah", null,null);
		} catch (ArcAuthenticationServerException e) {
			try {
				// null user
				um.addUser("", "blah", null,null);
			} catch (ArcAuthenticationServerException e2) {
				return;
			}
			fail("Did not throw exception when passing in null user id.");
			return;
		}
		fail("Did not throw exception when passing in blank user id.");
	}

	public void testGetArcUserId() {
		try {
			um.getArcUserId("testUser");
		} catch (UnsupportedOperationException e) {
			return;
		}
		fail("Did not throw an exception");
	}

	public void testRemoveUser() {
		um.removeUser("testUser");
	}

	public void testUserExists() {
		try {
			um.userExists("testUser");
		} catch (UnsupportedOperationException e) {
			return;
		}
		fail("Did not throw an exception");
	}

}
