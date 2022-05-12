package com.temenos.arc.security.authenticationserver.server;

import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;

/**
 * Entry point for CALLJ invocations of cryptography methods.  Responsible for parsing 
 * XML parameter string passed by CALLJ, delegating to the {@link CryptographyService},
 * mapping exceptions to success/failure codes and creating the return XML string.   
 * @author jannadani
 *
 */
public class XmlCryptographyService extends XmlWrapper {
    public static final String UNENCRYPTED_DATA = "unencryptedData";
    public static final String ENCRYPTED_DATA = "encryptedData";

    private CryptographyService delegate;
   
    /**
     * Overload that uses default config 
     */
    public XmlCryptographyService() {
        checkClassLoader();
    	System.setProperty("com.temenos.t24.commons.logging.LoggerFactory", "com.temenos.t24.commons.logging.impl.Log4jLoggerFactory");
        delegate = CryptographyService.getInstance(AuthenticationServerConfiguration.getStatic());
    }
    
    /**
     * Overload that uses specified crypto service 
     */
    public XmlCryptographyService(AuthenticationServerConfiguration config) {
        checkClassLoader();
    	System.setProperty("com.temenos.t24.commons.logging.LoggerFactory", "com.temenos.t24.commons.logging.impl.Log4jLoggerFactory");
        delegate = CryptographyService.getInstance(config);
    }

    /**
     * Wraps encryption.   Always uses an initialisation vector.
     * @param xmlArgs   of the form: <code>&ltargs&gt&ltunencryptedData&gtplaintext&lt/unencryptedData&gt&lt/args&gt</code>
     * @return  if success, <code>&ltargs&gt&ltencryptedData&gtciphertext&lt/encryptedData&gt&ltreturnState&gt0&lt/returnState&gt&lt/args&gt</code>
     *          <p>if failure, <code>&ltargs&gt&ltreturnState&gt1&lt/returnState&gt&lt/args&gt</code>
     */
    public String encrypt(String xmlArgs) {
        checkClassLoader();
        try {
            Map args = parseXmlIntoArgs(xmlArgs);
            String toEncrypt = (String) args.get(UNENCRYPTED_DATA);
            checkNotEmpty(toEncrypt);
            String encrypted = delegate.encrypt(toEncrypt, true);            
            return createReturnXml(SUCCESS_STATE, getReturnArg(encrypted, ENCRYPTED_DATA));
        } catch(Exception e) {
            // TODO remove? log?
            e.printStackTrace();
            return createReturnXml(FAILURE_STATE, new StringBuffer(e.getMessage()));
        }
    }
    
    /**
     * Wraps decryption.   Always uses an initialisation vector.
     * @param xmlArgs   of the form: <code>&ltargs&gt&ltencryptedData&gtciphertext&lt/encryptedData&gt&lt/args&gt</code>
     * @return  if success, <code>&ltargs&gt&ltunencryptedData&gtplaintext&lt/unencryptedData&gt&ltreturnState&gt0&lt/returnState&gt&lt/args&gt</code>
     *          <p>if failure, <code>&ltargs&gt&ltreturnState&gt1&lt/returnState&gt&lt/args&gt</code>
     */
    public String decrypt(String xmlArgs) {
        checkClassLoader();
        try {
            Map args = parseXmlIntoArgs(xmlArgs);
            String toDecrypt = (String) args.get(ENCRYPTED_DATA);
            checkNotEmpty(toDecrypt);
            String decrypted = delegate.decrypt(toDecrypt, true);
            return createReturnXml(SUCCESS_STATE, getReturnArg(decrypted, UNENCRYPTED_DATA));
        } catch(Exception e) {
            // TODO remove? log?
            e.printStackTrace();
            return createReturnXml(FAILURE_STATE, new StringBuffer(e.getMessage()));
        }
    }
}