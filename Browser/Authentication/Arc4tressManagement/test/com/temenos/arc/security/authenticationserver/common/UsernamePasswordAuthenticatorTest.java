package com.temenos.arc.security.authenticationserver.common;

public class UsernamePasswordAuthenticatorTest extends 
AbstractUsernamePasswordAuthenticatorTest {

	protected void setUp() throws Exception {
		super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/static.config");
		config = AuthenticationServerConfiguration.getStatic();
	}
}
