package com.temenos.arc.security.authenticationserver.common;

import java.util.Map;
import com.temenos.arc.security.jaas.JaasConfiguration;
import junit.framework.TestCase;

/**
 * This test requires the ConfigurationFileParser.FILE_PATH and 
 * ConfigurationFileParser.APP_NAME parameters to be set.
 *  
 * @author jannadani
 *
 */
public class ConfigurationFileParserTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "APPNAME");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/test/com/temenos/arc/security/authenticationserver/common/success.config");
    }

    public void testParseSuccess() {
        Map[] results = new ConfigurationFileParser().parse();
        assertEquals(2, results.length);
        Map firstSection = results[0];
        assertEquals("com.temenos.arc.security.authenticationserver.ftress.DeviceAuthenticator", firstSection.get(JaasConfiguration.AUTHENTICATION_DELEGATE));
        assertEquals("com.temenos.arc.security.jaas.JaasCommitter", firstSection.get(JaasConfiguration.AUTHENTICATION_COMMITTER));
        assertEquals("", firstSection.get(JaasConfiguration.CALLBACK_OVERRIDE));
        assertEquals("UTF-8", firstSection.get(AuthenticationServerConfiguration.CHARSET));
        assertEquals("", firstSection.get(AuthenticationServerConfiguration.ATTRIBUTE_T24USER));
        Map secondSection = results[1];
        assertEquals("com.temenos.arc.security.jaas.SomethingElse", secondSection.get(JaasConfiguration.AUTHENTICATION_DELEGATE));
        assertEquals("com.temenos.arc.security.jaas.DifferentCommitter", secondSection.get(JaasConfiguration.AUTHENTICATION_COMMITTER));
        assertEquals("", secondSection.get(JaasConfiguration.CALLBACK_OVERRIDE));
        assertEquals("UTF-8", secondSection.get(AuthenticationServerConfiguration.CHARSET));
        assertEquals("AHA!", secondSection.get(AuthenticationServerConfiguration.ATTRIBUTE_T24USER));        
    }

    public void testParseFail() {
        replaceFilePathProperty("success", "failure");
        
        Map[] results = new ConfigurationFileParser().parse();
        Map firstSection = results[0];
        assertEquals(0, firstSection.size());
    }

    private void replaceFilePathProperty(String oldVal, String newVal) {
        String filePath = System.getProperty(ConfigurationFileParser.FILE_PATH_KEY);
        String failurePath = filePath.replaceAll(oldVal, newVal);
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, failurePath);
    }
    public void testParseFail2() {
        replaceFilePathProperty("success", "failure2");
        
        Map[] results = new ConfigurationFileParser().parse();
        Map firstSection = results[0];
        assertEquals(0, firstSection.size());
    }
}
