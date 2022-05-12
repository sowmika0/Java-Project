package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import junit.framework.TestCase;


public class XmlCryptographyTest extends TestCase {
    static final String UNENCRYPTED_DATA_VAL = "foo";
    
    static final String ENCRYPT_XML = XmlWrapper.ARGS_START    
                                    + "<" + XmlCryptographyService.UNENCRYPTED_DATA + ">"
                                    + UNENCRYPTED_DATA_VAL
                                    + "</" + XmlCryptographyService.UNENCRYPTED_DATA + ">"
                                + XmlWrapper.ARGS_END;
    static final String ENCRYPT_RETURN_START = XmlWrapper.ARGS_START 
                                                + "<" + XmlCryptographyService.ENCRYPTED_DATA + ">";
    static final String ENCRYPT_RETURN_END = "</" + XmlCryptographyService.ENCRYPTED_DATA + ">"
                                                + "<" + XmlWrapper.RETURN_STATE + ">" 
                                                + XmlWrapper.SUCCESS_STATE 
                                                + "</" + XmlWrapper.RETURN_STATE + ">" 
                                       + XmlWrapper.ARGS_END;
    
    static final String UNENCRYPT_XML_START = XmlWrapper.ARGS_START    
                                    + "<" + XmlCryptographyService.ENCRYPTED_DATA + ">";
    static final String UNENCRYPT_XML_END = "</" + XmlCryptographyService.ENCRYPTED_DATA + ">"
                                    + XmlWrapper.ARGS_END;
                                    
    XmlCryptographyService toTest;
    
    
    public XmlCryptographyTest() {
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/static.config");
        toTest = new XmlCryptographyService();
    }
    
    public void testEncrypt() {
        String returnXml = toTest.encrypt(ENCRYPT_XML);
        System.out.println("return XML from encryption: " + returnXml);
        
        // find the encrypted value
        String encrypted = XmlWrapper.getArg(returnXml, XmlCryptographyService.ENCRYPTED_DATA);
        assertFalse(UNENCRYPTED_DATA_VAL.equals(encrypted));
        
        // decrypt again
        returnXml = toTest.decrypt(UNENCRYPT_XML_START + encrypted + UNENCRYPT_XML_END);
        String decrypted = XmlWrapper.getArg(returnXml, XmlCryptographyService.UNENCRYPTED_DATA);
        assertEquals(UNENCRYPTED_DATA_VAL, decrypted);
    }

}
