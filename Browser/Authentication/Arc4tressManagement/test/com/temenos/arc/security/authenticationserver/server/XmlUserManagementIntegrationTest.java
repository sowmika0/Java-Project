package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.ftress.server.UserManagement;
import com.temenos.arc.security.authenticationserver.server.ArcUserManagement;
import com.temenos.arc.security.authenticationserver.server.XmlUserManagement;

public class XmlUserManagementIntegrationTest extends AbstractXmlUserManagementTest{
    ArcUserManagement userManagement;
    
    public XmlUserManagementIntegrationTest() {
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "INTEG");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/no_crypto.config");
        userManagement = new UserManagement();    
    }
    protected void setUp() throws Exception {
        super.setUp();
        toTest = new XmlUserManagement(userManagement);
    }
    
}
