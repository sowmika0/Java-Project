package com.temenos.arc.security.filter;

/**
 * Global Constants class for RSA authentication and Transaction Signing
 *
 * @Author Sara (6363)
 */
public class Constant {
	
	public static String RSA_AUTH_JSP = "RSAAuthentication.jsp";
	public static String RSA_NEWPIN_JSP = "RSANewPin.jsp";
	public static String RSA_NEXTTOKEN_JSP = "RSANextToken.jsp";

	// Headers
	public static String SESSION_ATTR_USERNAME = "username";
	public static String SESSION_ATTR_PASSWORD ="password";
	public static String SAVED_REQUEST = "SavedRequest";
	public static String SAVED_REQUEST_URL = "SavedRequestUrl";
	
    // RSA Session Related attributes
	public static String SESSION_ATTR_CURRENT_STEP = "step";
    public static String SESSION_ATTR_AUTH_RSA_TOKEN = "token";
    public static String SESSION_ATTR_AUTH_RSA_SMS = "sms";
    public static String SESSION_ATTR_AUTH_RSA_TYPE = "authtype";
    public static String SESSION_RSA_USERID = "rsa_userid";
    
    // RSA Request Related attributes
    public static String PARAM_RSA_PASSCODE = "rsa_passcode";
    public static String PARAM_RSA_PIN = "rsa_pin";;
    public static String PARAM_RSA_SMS_TOKEN = "rsa_sms_token";
    public static String PARAM_RSA_NEWPIN ="newpin";
    public static String PARAM_RSA_CONFIRM_NEWPIN ="confirm_newpin";
    public static String PARAM_RSA_NEXT_TOKEN ="nexttoken";
    
    // Steps
    public static String STEP_RSA_SMS_NEWPIN ="newpin_sms";
    public static String STEP_RSA_SMS_TOKEN ="sms_tokencode";
    public static String STEP_RSA_SECUREID_NEWPIN_NEXTTOKEN = "secureid_newpin_passcode";

    //////////////////////////////////////
    // Constants for LDAP Access
    //////////////////////////////////////
    public static String LDAP_URL = "stvtampolicy01";
    public static String LDAP_PORT = "389";
	public static String LDAP_BASE_DN = "O=QTC,C=AU";
	public static String LDAP_ATTR_UID = "uid";
	public static String LDAP_ATTR_PASSWORD ="userPassword";
	public static String LDAP_ADMIN_ID = "cn=root";
	public static String LDAP_ADMIN_PASSWORD = "Password2";

}
