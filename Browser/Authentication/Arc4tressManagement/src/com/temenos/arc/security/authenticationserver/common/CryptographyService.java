package com.temenos.arc.security.authenticationserver.common;

import java.security.KeyStore;


import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This class defines the encrypt/decrypt interfaces, and provides a factory method for 
 * constructing concrete instances.
 * @author jannadani
 *
 */
public abstract class CryptographyService {
    protected Logger logger = LoggerFactory.getLogger(CryptographyService.class);
    protected KeyStore store = null;

    /**
     * @param data  plaintext to encrypt
     * @param useIv <code>true </code> if caller desires an initialisation vector to be used.  
     * This should only be <code>false</code> if the encrypted data needs to be predictable (e.g. for searched against)      
     * @return ciphertext
     */
    abstract public String encrypt(String data, boolean useIv);

    /**
     * @param data  ciphertext to decrypt
     * @param useIv <code>true </code> if the ciphertext was encrypted using an initialisation vector.  
     * The initialisation vector length is currently hardcoded in the subclasses 
     * @return ciphertext
     */
    abstract public String decrypt(String data, boolean useIv);
    /** 
     * This is the finalise method which will destroy initialised objects, implemented to support netHSM module
     *
     */
    abstract public void close();

    public static final CryptographyService getInstance(AuthenticationServerConfiguration config) {
        //
    	String className = config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_SERVICE_CLASS);
    	if (null == className || className.equals("")) {
    		className = PlainTextCryptographyService.class.getName();    	
    	}
        Object obj = CryptographyServiceFactory.create(className, new Class[] { AuthenticationServerConfiguration.class },
                new Object[] { config });
        if (!CryptographyService.class.isAssignableFrom(obj.getClass())) {
            throw new ArcAuthenticationServerException("Expected interface not implemented by " + className);
        }
        return (CryptographyService) obj;
    }

    protected CryptographyService() {
        super();
    }
}
