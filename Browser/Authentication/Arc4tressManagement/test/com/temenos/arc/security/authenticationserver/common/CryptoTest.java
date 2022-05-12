package com.temenos.arc.security.authenticationserver.common;


public class CryptoTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
        if (args.length != 3) {
            System.out.println("need MODE, TEXT and USEIV arguments");
            return;
        }
        String mode = args[0];
        String text = args[1];
        boolean useIv = (args[2].toUpperCase().startsWith("Y")
                        || args[2].toUpperCase().startsWith("T"));
                
        
        System.out.println("*** Invoking with mode: " + mode + ", text: " + text 
                            + "\n config: " + System.getProperty(ConfigurationFileParser.FILE_PATH_KEY) + ", section: " + System.getProperty(ConfigurationFileParser.FILE_APP_NAME_KEY) 
                            + "\n ***************************");
        AuthenticationServerConfiguration config = AuthenticationServerConfiguration.getStatic();
        System.out.println("*** got config **** keystore: " + config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_KEYSTORE));
        System.out.println("constructing AESCryptograhyService");
        CryptographyService service = CryptographyService.getInstance(config);
        
        String result = null;
        if (mode.toUpperCase().startsWith("E")) {
            result = service.encrypt(text, useIv);
        } else {
            result = service.decrypt(text, useIv);
        }
        System.out.println("RESULT: " + result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
