package com.temenos.t24browser.ofs;

import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * Class dealing with OFS decryption of user name and passwords
 */

public class OfsCrypto
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsCrypto.class);
	
	/** Crypto Service variables **/
	private Map[] ivConfigMap = null;
	CryptographyService ivCryptoService = null;
	
	/** Configuration parameters from ofs-server.config **/
	private String ivEncryptedUser = "";
	private String ivEncryptedPassword = "";
	private String ivUnencryptedUser = "";
	private String ivUnencryptedPassword = "";
	
	
	/**
	 * Stores the user name and password for decryption
	 */
	public OfsCrypto( OfsServerConfig config )
	{
		// Set up the crypto service
    	ivConfigMap = config.getConfigMap();
    	AuthenticationServerConfiguration authConfig = new AuthenticationServerConfiguration( ivConfigMap[0] );

    	ivCryptoService = CryptographyService.getInstance( authConfig );

		ivEncryptedUser = config.getEncryptedUser();
		ivEncryptedPassword = config.getEncryptedPassword();
		
		// Decrypt the data
		ivUnencryptedUser = decrypt( ivEncryptedUser );
		ivUnencryptedPassword = decrypt( ivEncryptedPassword );
	}
	
	/**
	 * Returns the unencrypted user name
	 * 
	 * @return String The unencrypted user name
	 * 
	 */
	public String getUnencryptedUser()
	{
		return ivUnencryptedUser;
	}
	
	/**
	 * Returns the unencrypted password
	 * 
	 * @return String The unencrypted password
	 * 
	 */	
	public String getUnencryptedPassword()
	{
		return ivUnencryptedPassword;
	}
	
	/**
	 * Returns the unencrypted string of the specified encrypted parameter.
	 * 
	 * @return String
	 */
	private String decrypt( String encryptedValue )
	{
		String unencryptedValue = ivCryptoService.decrypt( encryptedValue, true );
		return unencryptedValue;
	}
}
