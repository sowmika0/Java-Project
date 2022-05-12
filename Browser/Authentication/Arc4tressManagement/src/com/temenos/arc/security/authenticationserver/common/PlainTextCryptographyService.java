package com.temenos.arc.security.authenticationserver.common;


/**
 * No-op implementation of {@link CryptographyService} [NullObject pattern]
 * @author jannadani
 *
 */
public class PlainTextCryptographyService extends CryptographyService {
    
    PlainTextCryptographyService(AuthenticationServerConfiguration config) {}
    
    public String encrypt(String data, boolean useIv) {
    	if (logger.isDebugEnabled()) logger.info("Entering encrypt()");
    	return data;
    }

    public String decrypt(String data, boolean useIv) {
        if (logger.isDebugEnabled()) logger.info("Entering decrypt()");
        return data;
    }
	@Override
	public void close() {
		logger.info("AESCryptographyService getting closed.");	
	}


}
