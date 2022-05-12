package com.temenos.arc.security.authenticationserver.common;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * Subclass of {@link CryptographyService} that implements {@link #encrypt(String, boolean)}
 * and {@link #decrypt(String, boolean)} using the secret key specified by the passed in config.  
 * @author jannadani
 *
 */
public class AESCryptographyService extends CryptographyService {
    
	protected String keystorePassword;
    protected String keystorePath;
    protected String keyPassword;
    protected String alias;
    private static final int paddingLength=5;
    
    AESCryptographyService(AuthenticationServerConfiguration config) {
        keyPassword = config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_KEY_PASSWORD);
        alias = config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_KEY_ALIAS);
        keystorePassword = config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_KEYSTORE_PASSWORD);
        keystorePath = config.getConfigValue(AuthenticationServerConfiguration.CRYPTO_KEYSTORE);
		String maskedKP = maskPassword(keyPassword);
		logger.debug("kp: " + maskedKP);
		logger.debug("alias: " + alias);
		String maskedKSP = maskPassword(keystorePassword);
		logger.debug("ksp: " + maskedKSP);
		logger.debug("kspath: " + keystorePath);
		logger.debug("init keystore" );
        initKeyStore();
		logger.debug("finished init keystore" );
	}
	
	protected byte[] cryptHelper(String data, int mode, boolean decode) {
		byte [] result = null;
		try {
	    	logger.debug("get the key");
			Key secretKey = store.getKey(alias, keyPassword.toCharArray());
	    	logger.debug("About to encode");
			Cipher cipher = Cipher.getInstance("AES");
	    	if (Cipher.ENCRYPT_MODE==mode) 
	    		logger.debug("About to encrypt");
	    	else
	    		logger.debug("About to decrypt");
	    		
			cipher.init(mode, secretKey);
			if (decode) {
				Base64 decoder = new Base64(); 
				result = cipher.doFinal(decoder.decode(data.getBytes()));
	    		logger.debug("finished decrypt");
			} else {
				result = cipher.doFinal(data.getBytes("UTF-8"));
	    		logger.debug("finished encrypt");
			}
		} catch (Exception e) {
    		logger.debug("error in encrypt/decrypt");
            logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		logger.debug("returning result");
		return result;
	}

    public String encrypt(String data, boolean useIv) {
    	if (logger.isDebugEnabled()) logger.debug("Entering encrypt()");
    	// Add an initialisation vector:
    	if (useIv) logger.debug("Using padding()");
    	String initVector = useIv ? StringGenerator.getRandomAlphaNumericString(paddingLength) : "";
        int mode = Cipher.ENCRYPT_MODE;
    	logger.debug("About to go to crypt helper");
        byte[] encrypted = cryptHelper(initVector + data, mode, false);
    	logger.debug("Returned from crypt helper");
		Base64 encoder = new Base64();
    	logger.debug("About to encode");
		String encoded = new String(encoder.encode(encrypted));
    	logger.debug("Completed encoding");
       	return encoded;
    }

    public String decrypt(String data, boolean useIv) {
		logger.debug("entering decrypt()");
        if (logger.isDebugEnabled()) logger.info("Entering decrypt()");
        int mode = Cipher.DECRYPT_MODE;
        byte[] decrypted = cryptHelper(data, mode, true);
		logger.debug("finished decrypt...");
        try {
        	String returnString = new String(decrypted, "UTF-8"); 
    		logger.debug("convert to string");
        	// Now we need to remove the initialisation vector;
        	if (useIv) {
	    		logger.debug("remove padding");
                returnString = returnString.substring(paddingLength);
            }
            return returnString; 
        } catch (UnsupportedEncodingException e) {
    		logger.debug("error decoding: " + e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }
    }

    private void initKeyStore() {
        try {
    		logger.debug("getting keystore");
            store = KeyStore.getInstance("JCEKS");
    		logger.debug("got keystore");
            FileInputStream fis = new FileInputStream(keystorePath);
    		logger.debug("got file input stream");
            store.load(fis, keystorePassword.toCharArray());
    		logger.debug("loaded keystore");
        } catch (Exception e) {
    		logger.debug("error getting keystore: " + e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }
    }
    
    private String maskPassword(String password) {
		String maskedPW = "";
		char maskChar = '*';
    	
		if (null != password && !password.equals("")) { 
			// mask the pw
			for (int i=0; i<password.length(); i++) {
				maskedPW = maskedPW + maskChar;
			}
		} 
    	
    	return maskedPW;
    }
    @Override
	public void close() {
		logger.info("AESCryptographyService getting closed.");
	}

}
