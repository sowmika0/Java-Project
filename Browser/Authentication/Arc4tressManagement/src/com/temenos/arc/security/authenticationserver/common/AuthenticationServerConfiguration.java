package com.temenos.arc.security.authenticationserver.common;

import java.util.Map;

/**
 * Encapsulates configuration parameters necessary to connect to the authentication server, 
 * initialise the cryptography service, and so on.
 * @author jannadani
 *
 */
public class AuthenticationServerConfiguration {
    /** the 4TRESS attribute against which the T24 password is stored for an ARC user, 
     * set if this config is for a user authentication */
    public static final String ATTRIBUTE_T24PASS = "temenos.arc.security.ftress.user.attribute.t24pass";
    /** the 4TRESS attribute against which the T24 username is stored for an ARC user,
     * set if this config is for a user authentication */
    public static final String ATTRIBUTE_T24USER = "temenos.arc.security.ftress.user.attribute.t24user";
    /** the 4TRESS attribute against which the customer password is stored (encrypted) if this is a
     *  password / memorable word authentication */
    public static final String ATTRIBUTE_ARC_PASSWORD = "temenos.arc.security.ftress.user.attribute.arcpass";
    /** the 4TRESS user, set if this config is for a system login */
    public static final String UP_AUTH_USER = "temenos.arc.security.ftress.upauth.user";
    /** the 4TRESS password, set if this config is for a system login */
    public static final String UP_AUTH_PASSWORD = "temenos.arc.security.ftress.upauth.password";
    /** the 4TRESS authentication type for the authentication associated with this config */
    public static final String AUTHENTICATION_TYPE = "temenos.arc.security.ftress.authtype";   
    /** the 4TRESS authentication type for the system authentication using system user id and password */
    public static final String AUTHENTICATION_TYPE_SYSTEM = "temenos.arc.security.ftress.authtype.system";
    /** the 4TRESS authentication type password associated with this config**/
    public static final String AUTH_TYPE_PASSWORD = "temenos.arc.security.ftress.authtype.password";
    /** the 4TRESS authentication type pin associated with this config**/
    public static final String AUTH_TYPE_PIN = "temenos.arc.security.ftress.authtype.pin";
    /** the 4TRESS authentication type device associated with this config**/
    public static final String AUTH_TYPE_DEVICE = "temenos.arc.security.ftress.authtype.device";
    /** the 4TRESS authentication type memorable word associated with this config**/
    public static final String AUTH_TYPE_MEMWORD = "temenos.arc.security.ftress.authtype.memword";
    /** the 4TRESS authentication type login memorable word associated with this config**/
    public static final String AUTH_TYPE_LOGINMEMWORD = "temenos.arc.security.ftress.authtype.loginmemword";
    /** the 4TRESS channel type otp-pin associated with this config**/
    public static final String CHANNEL_TYPE_OTPPIN = "temenos.arc.security.channel.otppin";
    /** the Browser channel type otp associated with this config**/
    public static final String CHANNEL_TYPE_OTP = "temenos.arc.security.channel.otp";
    /** the Browser channel type password memword associated with this config**/
    public static final String CHANNEL_TYPE_PWMW = "temenos.arc.security.channel.pwmw";
    /** the Browser channel type password associated with this config**/
    public static final String CHANNEL_TYPE_PW = "temenos.arc.security.channel.pw";
    /** the 4TRESS authentication type for OutOfBand SMS/EMAIL associated with this config**/
    public static final String AUTH_TYPE_OOB = "temenos.arc.security.ftress.authtype.oob";
    /** the 4TRESS authentication type for OutOfBand SMS/EMAIL associated with this config**/
    public static final String AUTH_TYPE_OOBTEMPLATE = "temenos.arc.security.ftress.authtype.oobtemplate";
    /** the 4TRESS authentication type memorable word associated with this config**/
    public static final String AUTH_TYPE_NEWMEMWORD = "temenos.arc.security.ftress.authtype.newmemword";
    /** Allowing the donotimpersonate */
    public static final String IMPERSONATE_NOT_ALLOWED = "temenos.arc.security.ftress.donotimpersonate";

    /** the 4TRESS actividentity designed page path will be specified under the below configuration entry**/
    public static final String ACTIVIDENTITY_PAGE = "temenos.arc.actividentitypage";
    
    /** the 4TRESS actividentity designed page path will be specified under the below configuration entry**/
    public static final String PWMWSEEDED_PAGE = "temenos.arc.passwordMemwordpage";
    
    /** the 4TRESS device mode if this config is for user authentication */   
    public static final String DEVICE_MODE = "temenos.arc.security.ftress.device.mode";
    /** the 4TRESS channel for the authentications associated with this config  */
    public static final String CHANNEL = "temenos.arc.security.ftress.channel";
    /** this doesn't vary across authentications */
    public static final String CHARSET = "temenos.arc.security.charset";
    /** this doesn't vary across authentications */
    public static final String DOMAIN = "temenos.arc.security.ftress.domain";
    /** set if this config is for an authentication that should create a session in 4TRESS */
    public static final String CREATE_SESSION = "temenos.arc.security.ftress.create.session";
    /** the password length set in 4TRESS if this is for a user authentication */
    public static final String T24_PASSWORD_LENGTH = "temenos.arc.security.t24.password.length";
    /** the user id length set in if this is for a user authentication */
    public static final String T24_USER_ID_MIN_LENGTH = "temenos.arc.security.t24.userid.minlength";
    /** the ARC password length of the initial password if this is a password / memorable word login */
    public static final String ARC_PASSWORD_LENGTH = "temenos.arc.security.ftress.password.length";
    /** the ARC userid length set in 4TRESS if this is for a user authentication */
    public static final String ARC_USER_ID_LENGTH = "temenos.arc.security.ftress.userid.length";
    /** Make the ARC userid the same as the external user id. Value should be "true" if is the same 
     *  Defaults to false.  */
    public static final String ARC_USER_ID_SHARED = "temenos.arc.security.ftress.userid.shared";
    /** the delimiting character used to separate positions and characters */
    public static final String MEM_WORD_SEED_DELIM = "temenos.arc.security.ftress.seeddelimiter";
    /** Flag stating if seeding is required */
    public static final String MEM_WORD_IS_SEEDED = "temenos.arc.security.ftress.isseeded";
    /** the number of seeds required in order to authenticate */
    public static final String MEM_WORD_NUM_SEEDS = "temenos.arc.security.ftress.numseeds";
    /** the number of seeds required in order to authenticate */
    public static final String ARC_HOME_PAGE = "temenos.arc.homepage";
    /** the file to export user details to */
    public static final String ARC_USER_DETAILS_FILE = "temenos.arc.security.rsa.user.file";
    /** The authentication server being used */
    public static final String ARC_AUTHENTICATION_SERVER = "temenos.arc.security.authserver";
    /** potential values of above param */
    
    public static final String ARC_AUTH_URL_DELIMETER = "temenos.arc.security.ftressurl.delimeter";
    
    public static final String ARC_AUTH_SERVER_URL = "temenos.arc.security.authserver.namingurl";
    
    public static final String ARC_AUTH_SERVER_NAMINGPACKAGE ="temenos.arc.security.authserver.namingpackage";
    
    public static final String ARC_AUTH_SERVER_CONTEXTFACTORY ="temenos.arc.security.authserver.contextfactory";
    
    public static final String ARC_AUTH_SERVER_SOAPURL = "temenos.arc.security.authserver.soapurl";
        
    public static final String RSA = "RSA";
    
    public static final String FTRESS = "4TRESS";
    /** The authentication server being used */
    public static final String ARC_AUTHENTICATION_SERVER_CONFIG = "temenos.arc.security.rsa.configpath";
    
    /**the NetHsm app Name */ 
    public static final String NETHSM_APP_NAME = "temenos.arc.security.crypto.nethsm.appname";
    /**the NetHsm identity */ 
    public static final String NETHSM_IDENTITY = "temenos.arc.security.crypto.nethsm.identity";
        
    /** the secret key alias in the keystore for cryptography */
    public static final String CRYPTO_KEY_ALIAS = "temenos.arc.security.crypto.key.alias";
    /** the secret key password for cryptography */
    public static final String CRYPTO_KEY_PASSWORD = "temenos.arc.security.crypto.key.password";
    /** the keystore path for cryptography */
    public static final String CRYPTO_KEYSTORE = "temenos.arc.security.crypto.keystore";
    /** the keystore password for cryptography */
    public static final String CRYPTO_KEYSTORE_PASSWORD = "temenos.arc.security.crypto.keystore.password";
    /** the class to be instantiated for cryptography operations */
    public static final String CRYPTO_SERVICE_CLASS = "temenos.arc.security.crypto.class";
    
    public static final String AUTH_ERROR_PAGE = "temenos.arc.auth.errorpage";
    
    /** Transaction authentication error and locked page */
    public static final String TRANSACTION_ERROR_PAGE = "temenos.arc.transaction.errorpage";

    /** the Algorithm to be user for cryptography operations */
    public static final String CRYPTO_ALGORITHM = "temenos.arc.security.crypto.algorithm";
    
    /** Name of the provider to be used in encrpytion and decryption */
    public static final String CRYPTO_PROVIDER = "temenos.arc.security.crypto.provider";
    
    /** the secret key alias in the keystore for SIGNATURE cryptography */
    public static final String SIGNATURE_KEY_PATH = "temenos.arc.security.signature.keypath";
    /** the Algorithm to be user for signature operations */
    public static final String SIGNATURE_ALGORITHM = "temenos.arc.security.signature.algorithm";
    /** the distinguish name for ldap */
    public static final String DN_PATTERN = "temenos.arc.security.dn.pattern";
    /**Location of the XML schema for Authentication Ticket*/
    public static final String XML_SCHEMA = "temenos.arc.security.authticket.xmlschema";
    
    /**Initial Context Factory*/
    public static final String INITIAL_CONTEXT_FACTORY = "temenos.ldap.security.initial.context.factory";
    /**Provider URL*/
    public static final String PROVIDER_URL = "temenos.ldap.security.provider.url";
    /**Security Authentication*/
    public static final String SECURITY_AUTHENTICATION = "temenos.ldap.security.authentication";
    /**Security principal*/
    public static final String SECURITY_PRINCIPAL = "temenos.ldap.security.principal";
    /**Security credentials*/
    public static final String SECURITY_CREDENTIALS = "temenos.ldap.security.credentials";
    /**DN for searching the user id*/
    public static final String SEARCH_DN = "temenos.ldap.security.dn.search";
    /**Cookie name which contains the userDN*/
    public static final String COOKIE_NAME = "temenos.ldap.security.dn.cookie";
    /**Cookie name which contains the value from the external system*/
    public static final String COOKIE = "temenos.arc.security.cookiename";
    /**request header name which contains the User ID value from the external system*/
    public static final String SSO_HEADER_NAME = "temenos.arc.security.headername";
    
    /**Password field in the ldap*/
    public static final String PASSWORD_FIELD = "temenos.ldap.security.password.field";
    
    public static final String DELETE_MEMWORD_AUTHENTICATOR  = "temenos.arc.security.ftress.deletememword";
    
    /** Customer Attribute Fields in ftress Server */
    public static final String ARC_CUSTOMER_ATTRIBUTE = "temenos.arc.security.ftress.cust.attribute";    
    
    /** Security Method **/
    public static final String SECURITY_METHOD = "temenos.ldap.security.method.impersonate";
    
    /** Resetting user password on creating EXTERNAL user records for 4Tress **/
    public static final String RESET_PASSWORD="temenos.arc.security.ftress.reset.password";
    
    /** the list contains the authenticators to be deleted */
    public static final String DELETE_AUTHENTICATORS_LIST ="temenos.arc.security.ftress.authtype.deleteauthenticators";
    
    /**Header name to retrieve the username*/
    public static final String HEADER_NAME = "temenos.ldap.security.header.name";

    /** the 4TRESS authentication type device (to be used only for Signing) associated with this config**/
    public static final String AUTH_TYPE_SIGNDEVICE = "temenos.arc.security.ftress.authtype.signdevice";
    
    public static final String AUTH_SERVER_COMMUNICATION_TYPE = "temenos.arc.security.ftress.communication.protocol";

	private Map properties; 

    /**
     * Overload used directly when the config params are already in a map,  
     * or internally by {@link #fromConfigFile(String)}. 
     * @param properties
     */
	public AuthenticationServerConfiguration(Map properties) {
		this.properties = properties;
	}
    
    /**
     * Factory method that reads config from a file.  Delegates the actual reading to {@link ConfigurationFileParser}.
     * Assumes only the first section is relevant.
     * @param filePath  
     * @return a newly instantiated <code>AuthenticationServerConfiguration</code> populated from the file 
     */
    public static AuthenticationServerConfiguration fromConfigFile(final String filePath) {
    	String cachedFilePath = System.getProperty(ConfigurationFileParser.FILE_PATH_KEY);
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, filePath);
        ConfigurationFileParser parser = new ConfigurationFileParser();
        AuthenticationServerConfiguration config = new AuthenticationServerConfiguration(parser.parse()[0]);
        if (cachedFilePath != null) {
            System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, cachedFilePath);
        }
        return config;
    }

    /**
     * Access the value specified by the passed in key.
     * @param key
     * @return
     */
	public String getConfigValue(String key) {
		return (String)properties.get(key);
	}

    /**
     * Used to propagate config values to another configuration object 
     * @return
     */
	public Map getMap() {
		return properties;
	}
    
    public boolean dontCreateSession() {
        String shouldCreate = getConfigValue(AuthenticationServerConfiguration.CREATE_SESSION);
        if (shouldCreate != null  
            && "false".compareToIgnoreCase(shouldCreate) == 0) {
            return true;
        }
       return false;
    }

    public static AuthenticationServerConfiguration getStatic() {
        return new AuthenticationServerConfiguration(new ConfigurationFileParser().parse()[0]);        
    }
    
}
