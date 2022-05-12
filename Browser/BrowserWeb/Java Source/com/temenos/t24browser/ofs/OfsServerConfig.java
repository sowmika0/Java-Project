package com.temenos.t24browser.ofs;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletContext;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Class dealing with the 'ofs-server.config' file
 */

public class OfsServerConfig implements Serializable
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsServerConfig.class);
	
	/** Configuration file details. **/
	private static final String CONFIG_FILE = "ofs-server.config";
	private static final String CONFIG_FILE_NODE = "OFS";
	private Map[] ivConfigMap = null;
	
	/** Configuration file contents. **/
    public static final String CONFIG_DEFAULT_APPLICATION = "temenos.t24browser.security.ofs.defaultApplication";
    public static final String CONFIG_DEFAULT_VERSION = "temenos.t24browser.security.ofs.defaultVersion";
    public static final String CONFIG_DEFAULT_TRANSACTION_ID = "temenos.t24browser.security.ofs.defaultTransactionId";
    public static final String CONFIG_DEFAULT_SUCCESS_PAGE = "temenos.t24browser.security.ofs.defaultSuccessPage";
    public static final String CONFIG_DEFAULT_FAILURE_PAGE = "temenos.t24browser.security.ofs.defaultFailurePage";
    public static final String CONFIG_DEFAULT_CAPTCHA_FAILURE_PAGE = "temenos.t24browser.security.ofs.defaultCaptchaFailurePage";
    public static final String CONFIG_USER = "temenos.t24browser.security.ofs.user";
    public static final String CONFIG_PASSWORD = "temenos.t24browser.security.ofs.password";
    public static final String CONFIG_KEYSTORE_PASSWORD = "temenos.arc.security.crypto.keystore.password";
    public static final String CONFIG_KEYSTORE = "temenos.arc.security.crypto.keystore";
    public static final String CONFIG_KEY_PASSWORD = "temenos.arc.security.crypto.key.password";
    public static final String CONFIG_KEY_ALIAS = "temenos.arc.security.crypto.key.alias";
    public static final String CONFIG_CRYPTO_CLASS = "temenos.arc.security.crypto.class";
	
	/** Configuration parameters from ofs-server.config **/
	private String ivDefaultApplication = "";
	private String ivDefaultVersion = "";
	private String ivDefaultTransactionId = "";
	private String ivDefaultSuccessPage = "";
	private String ivDefaultFailurePage = "";
	private String ivDefaultCaptchaFailurePage = "";
	private String ivEncryptedUser = "";
	private String ivEncryptedPassword = "";
	private String ivKeyStorePassword = "";
	private String ivKeyStore = "";
	private String ivKeyPassword = "";
	private String ivKeyAlias = "";
	private String ivCryptoClass = "";

	/**
	 * Sets up the OFS server config by reading the file.
	 */
	public OfsServerConfig( ServletContext context )
	{
		String configFileName = getConfigFileName( context );
		ConfigurationFileParser parser = new ConfigurationFileParser( configFileName, CONFIG_FILE_NODE );
		ivConfigMap = parser.parse();
		AuthenticationServerConfiguration config = new AuthenticationServerConfiguration( ivConfigMap[0] );
		
		if (config == null)
		{
			LOGGER.debug( "OFS Server Configuration - config is null");
		}

		ivDefaultApplication = config.getConfigValue(CONFIG_DEFAULT_APPLICATION);
		ivDefaultVersion = config.getConfigValue(CONFIG_DEFAULT_VERSION);
		ivDefaultTransactionId = config.getConfigValue(CONFIG_DEFAULT_TRANSACTION_ID);
		ivDefaultSuccessPage = config.getConfigValue(CONFIG_DEFAULT_SUCCESS_PAGE);
		ivDefaultFailurePage = config.getConfigValue(CONFIG_DEFAULT_FAILURE_PAGE);
		ivDefaultCaptchaFailurePage = config.getConfigValue(CONFIG_DEFAULT_CAPTCHA_FAILURE_PAGE);
		ivEncryptedUser = config.getConfigValue(CONFIG_USER);
		ivEncryptedPassword = config.getConfigValue(CONFIG_PASSWORD);
		ivKeyStorePassword = config.getConfigValue(CONFIG_KEYSTORE_PASSWORD);
		ivKeyStore = config.getConfigValue(CONFIG_KEYSTORE);
		ivKeyPassword = config.getConfigValue(CONFIG_KEY_PASSWORD);
		ivKeyAlias = config.getConfigValue(CONFIG_KEY_ALIAS);
		ivCryptoClass = config.getConfigValue(CONFIG_CRYPTO_CLASS);
	}
		
		
	/**
	 * Returns the default application.
	 * 
	 * @return String
	 */
	public String getDefaultApplication()
	{
		return this.ivDefaultApplication;
	}
	
	/**
	 * Returns the default version.
	 * 
	 * @return String
	 */
	public String getDefaultVersion()
	{
		return this.ivDefaultVersion;
	}
	
	/**
	 * Returns the default transaction Id.
	 * 
	 * @return String
	 */
	public String getDefaultTransactionId()
	{
		return this.ivDefaultTransactionId;
	}

	/**
	 * Returns the default success page.
	 * 
	 * @return String
	 */
	public String getDefaultSuccessPage()
	{
		return this.ivDefaultSuccessPage;
	}

	/**
	 * Returns the default failure page.
	 * 
	 * @return String
	 */
	public String getDefaultFailurePage()
	{
		return this.ivDefaultFailurePage;
	}
	
	/**
	 * Returns the default CAPTCHA failure page.
	 * 
	 * @return String
	 */
	public String getDefaultCaptchaFailurePage()
	{
		return this.ivDefaultCaptchaFailurePage;
	}
	
	/**
	 * Returns the encrypted user.
	 * 
	 * @return String
	 */
	public String getEncryptedUser()
	{
		return this.ivEncryptedUser;
	}

	/**
	 * Returns the encrypted password.
	 * 
	 * @return String
	 */
	public String getEncryptedPassword()
	{
		return this.ivEncryptedPassword;
	}
	
	/**
	 * Returns the key store password.
	 * 
	 * @return String
	 */
	public String getKeyStorePassword()
	{
		return this.ivKeyStorePassword;
	}
	
	/**
	 * Returns the key store.
	 * 
	 * @return String
	 */
	public String getKeyStore()
	{
		return this.ivKeyStore;
	}
	
	/**
	 * Returns the key password.
	 * 
	 * @return String
	 */
	public String getKeyPassword()
	{
		return this.ivKeyPassword;
	}
	
	/**
	 * Returns the key alias.
	 * 
	 * @return String
	 */
	public String getKeyAliasd()
	{
		return this.ivKeyAlias;
	}
	
	/**
	 * Returns the crypto class.
	 * 
	 * @return String
	 */
	public String getCryptoClass()
	{
		return this.ivCryptoClass;
	}
	
	/**
	 * Returns the crypto class.
	 * 
	 * @return String
	 */
	public Map[] getConfigMap()
	{
		return this.ivConfigMap;
	}
	

	/**
	 * Returns the OFS Server Config object as a String.
	 * 
	 * @return String
	 */
	public String toString()
	{
		String sConfig = "Default Application : '" + ivDefaultVersion + "', ";
		sConfig += "Default Version : '" + ivDefaultVersion + "', ";
		sConfig += "Default TransactionId : '" + ivDefaultTransactionId + "', ";
		sConfig += "Default Success Page : '" + ivDefaultSuccessPage + "', ";
		sConfig += "Default Failure Page : '" + ivDefaultFailurePage + "', ";
		sConfig += "Default CAPTCHA Failure Page : '" + ivDefaultCaptchaFailurePage + "', ";
		sConfig += "Encrypted User : '" + ivEncryptedUser + "', ";
		sConfig += "Encrypted Password : '" + ivEncryptedPassword + "', ";
		sConfig += "Key Store Password : '" + ivKeyStorePassword + "', ";
		sConfig += "Key Store : '" + ivKeyStore + "', ";
		sConfig += "Key Password : '" + ivKeyPassword + "', ";
		sConfig += "Key Alias : '" + ivKeyAlias + "', ";
		sConfig += "Crypto Class : '" + ivCryptoClass + "'";
		
		return sConfig;
	}
	
	private String getConfigFileName( ServletContext context )
	{
		String sFileSeparator = System.getProperty("file.separator");
		String sFileName = context.getRealPath("") + sFileSeparator;
		sFileName += "WEB-INF" + sFileSeparator + "conf" + sFileSeparator;
		sFileName += CONFIG_FILE;
		return sFileName;
	}
}
