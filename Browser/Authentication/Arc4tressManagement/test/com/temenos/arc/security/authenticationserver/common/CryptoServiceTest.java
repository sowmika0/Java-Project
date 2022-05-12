package com.temenos.arc.security.authenticationserver.common;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import junit.framework.TestCase;


public class CryptoServiceTest extends TestCase {    

    static final String cleartext = "CLEARTEXT"; 
    CryptographyService toTest = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/static.config");        
    }

    public void testEncrypt() {
        String text = cleartext;
        toTest = CryptographyService.getInstance(AuthenticationServerConfiguration.getStatic());
        encryptAndDecrypt(text, false);
        encryptAndDecrypt("other text", true);
        encryptAndDecrypt("even more text", true);
        toTest = CryptographyService.getInstance(getPlaintextConfig());
        encryptAndDecrypt(text, false);
        encryptAndDecrypt("other text", false);
        encryptAndDecrypt("even more text", false);
    }

    private void encryptAndDecrypt(String text, boolean useIv) {
        long start = System.currentTimeMillis();        
        String cipher = toTest.encrypt(text, useIv);
        long end = System.currentTimeMillis();
        System.out.println(cipher);
        if (!(toTest instanceof PlainTextCryptographyService)) {
            assertFalse(cipher.equals(text));
        } else {
            assertTrue(cipher.equals(text));
        }
        System.out.println("Time for encrypt: " + (end-start));
        
        start = System.currentTimeMillis();
        String decryptedText = toTest.decrypt(cipher, useIv);
        end = System.currentTimeMillis();
        System.out.println(decryptedText);
        assertTrue(text.equals(decryptedText));
        System.out.println("Time for decrypt: " + (end-start));
    }
    
    private AuthenticationServerConfiguration getPlaintextConfig() {
        Map args = new HashMap(); 
        return new AuthenticationServerConfiguration(args);
    }
    
    public void testEncoding() {
    	String testString = "TONYC1";
    	Base64 encoder = new Base64();
    	byte[] encoded = encoder.encode(testString.getBytes());
    	System.out.println(new String(encoded));
    }
    
    public void testStuff() {
        toTest = CryptographyService.getInstance(AuthenticationServerConfiguration.getStatic());
    	String testuser = "T24SYSLOG";
    	String testpass = "g0rmenghast";
    	System.out.println(toTest.encrypt(testuser, true));
    	System.out.println(toTest.encrypt(testpass, true));
    }
}
