package com.temenos.arc.security.jaas;

import com.temenos.arc.security.authenticationserver.common.AbstractUsernamePasswordAuthenticatorTest;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;

/**
 * config currently requires user 786786786 to have AT_CUSTMW setup 
 * with "g0rmenghast" as mem word 
 * @author jannadani
 *
 */
public class CustomerMemWordTest extends AbstractUsernamePasswordAuthenticatorTest {

    public CustomerMemWordTest() {
        super(true);
    }
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/custmw.config");
        config = new AuthenticationServerConfiguration(new ConfigurationFileParser().parse()[0]);
    }
}
