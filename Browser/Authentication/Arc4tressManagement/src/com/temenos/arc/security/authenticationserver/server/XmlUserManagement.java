package com.temenos.arc.security.authenticationserver.server;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.ftress.server.UserManagement;

/**
 * XML wrapper entry point for CALLJ - delegates to UserManagement
 * 
 * @author jannadani
 * 
 */
public class XmlUserManagement extends XmlWrapper {
	public static final String T24_USER_NAME = "t24UserName";
	public static final String T24_PASSWORD = "t24Password";
	public static final String ARC_USER_ID = "ArcUserId";
	public static final String MEMORABLE_DATA = "MemorableData";
	public static final String START_DATE_YEAR = "StartDateYear";
	public static final String START_DATE_MONTH = "StartDateMonth";
	public static final String START_DATE_DAY = "StartDateDay";
	public static final String FROM_DATE="from-date";
	public static final String TO_DATE="to-date";
	public static final String AUTH_TYPE = "auth-type";
	public static final String STATUS="status";
	public static final String AUTH_SERVER_USER_ID="AuthServerUserId";
	 
	
	public static final String UNEXPECTED_USER_EXISTENCE_STATE = "2";
	public static String[] loginMode = new String[10];

	private ArcUserManagement delegate;

	/**
	 * No-arg ctor that will initialise to use UserManagement as its delegate
	 * 
	 */
	public XmlUserManagement() {
		checkClassLoader();
		// System.setProperty("com.temenos.t24.commons.logging.LoggerFactory",
		// "com.temenos.t24.commons.logging.impl.Log4jLoggerFactory");
		logger.debug("Entering XmlUserManagement()");
		logger.debug("Properties set.");
		AuthenticationServerConfiguration config = AuthenticationServerConfiguration
				.getStatic();
		logger.debug("got config");
		// Get the parameter that designates which Authentication Server we are
		// using
		String authServer = config
				.getConfigValue(AuthenticationServerConfiguration.ARC_AUTHENTICATION_SERVER);
		if (null == authServer || authServer.equals("")) {
			logger.error("Authentication Server not specified in config");
			throw new ArcAuthenticationServerException(
					"Authentication Server not specified in config");
		} else if (authServer.equals(AuthenticationServerConfiguration.RSA)) {
			delegate = new com.temenos.arc.security.authenticationserver.rsa.server.UserManagement();
		} else if (authServer.equals(AuthenticationServerConfiguration.FTRESS)) {
			delegate = new com.temenos.arc.security.authenticationserver.ftress.server.UserManagement();
		} else {
			logger.error("Authentication Server not specified in config");
			throw new ArcAuthenticationServerException(
					"Authentication Server not specified in config");

		}
	}

	/**
	 * ctor only used for testing
	 * 
	 * @param delegate
	 */
	public XmlUserManagement(ArcUserManagement delegate) {
		checkClassLoader();
		this.delegate = delegate;
	}

	/**
	 * Wraps {@link UserManagement#addUser(String, String, Calendar)}.
	 * 
	 * @param xmlArgs
	 *          <args><t24UserName>t24UserId</t24UserName>
	 *            <authServerUserId>Authentication Server User Id</authServerUserId>
	 *            <MemorableData>encryptedMemData</MemorableData>
	 *            <StartDateYear>2006</StartDateYear>
	 *            <StartDateMonth>1</StartDateMonth>
	 *            <StartDateDay>1</StartDateDay>
	 *            <AuthType>AuthType</AuthType>
	 *            <Customer>
	 *            <NAME.1>Andrew</NAME.1>
	 *            <NAME.2>Davis</NAME.2>
	 *            <STREET>3 Church Street</STREET>
	 *            <ADDRESS>Edgware</ADDRESS>
	 *            <TOWN.COUNTRY>London</TOWN.COUNTRY>
	 *            <POST.CODE>HA1 CHS</POST.CODE>
	 *            <COUNTRY>United Kingdom</COUNTRY>
	 *            </Customer>
	 *            </args>
	 * @return if success,
	 * 
	 *         <code>&ltargs&gt&ltt24Password&gtthisIsAnUnencryptedgeneratedpassword&lt/t24Password&gt&ltreturnState&gt0&lt/returnState&gt&ltargs&gt</code>
	 * <br>
	 *         if user already exists,
	 *         <code>&ltargs&gt&ltreturnState&gt2&lt/returnState&gt&ltargs&gt</code>
	 * <br>
	 *         if other failure,
	 *         <code>&ltargs&gt&ltreturnState&gt1&lt/returnState&gt&ltargs&gt</code>
	 */

	public String addUser(String xmlArgs) {
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering addUser");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		logger.info("Calling checkAuthMode method");
		checkAuthMode(xmlArgs);
		logger.info("The args size is :" + args.size());
		/*if (args.size() != 8) {
			return createReturnXml(FAILURE_STATE);
		}*/

		Calendar cal = Calendar.getInstance();
		try {
			int year = Integer.parseInt((String) args.get(START_DATE_YEAR));
			int month = Integer.parseInt((String) args.get(START_DATE_MONTH)) - 1;
			int day = Integer.parseInt((String) args.get(START_DATE_DAY));
			cal.set(year, month, day);
		} catch (NumberFormatException e) {
			return createReturnXml(FAILURE_STATE);
		}
		String t24Password = null;
		try {
			String t24UserName = (String) args.get(T24_USER_NAME);
		    String memorableWord = (String)args.get(MEMORABLE_DATA);
			Map custInfo = (Map)args.get("Customer");
		    t24Password = delegate.addUser(t24UserName, memorableWord, cal,custInfo);
			logger.info("Back to XmlUserMgmt");
		} catch (ArcUserStateException e) {
			return createReturnXml(UNEXPECTED_USER_EXISTENCE_STATE);
		} catch (ArcAuthenticationServerException e) {
			return createReturnXml(FAILURE_STATE);
		} catch (Exception e) {
			logger.debug("Exception in addUser(): " +e.getMessage(), e);
			return createReturnXml(FAILURE_STATE);
		}
		logger.info("Before returnString in XmlUserMgmt");
		String returnString = createReturnXml(t24Password, SUCCESS_STATE);
		if (logger.isDebugEnabled())
			logger.info("returning from addUser");
		if (logger.isDebugEnabled())
			logger.info("return value is" + returnString);
		// Now create the return
		return returnString;
	}

	public String[] checkAuthMode(String xmlArgs) {
		// Method to form the AuthType from the xmlArgs passed from T24
		logger.info("Inside checkAuthMode method");
		int sTag = xmlArgs.indexOf("<AuthType>");	
		int eTag = xmlArgs.indexOf("</AuthType>");
		logger.info("The Start Tag is :"+sTag);
		logger.info("The End Tag is :"+eTag);
		sTag = sTag + 10;
		String tVal = "";
		int arr = 0;
		for (int i = sTag; i < eTag; i++) {
			if (xmlArgs.charAt(i) != '$') {
				tVal = tVal + xmlArgs.charAt(i);
			} else {
				if (tVal != null) {
					logger.info("The AuthValue contains :"+tVal);
					loginMode[arr] = tVal;
					tVal = "";
					arr++;
				}
			}
		}
		logger.info("Coming out of checkAuthMode");
		return loginMode;
	}
	

	/**
	 * Wraps {@link UserManagement#removeUser(String)}.
	 * 
	 * @param xmlArgs
	 *            e.g.
	 *            <code>&ltargs&gt&ltt24UserName&gtuserToRemove&lt/t24UserName&gt&lt/args&gt</code>
	 * @return if success,
	 *         <code>&ltargs&gt&ltreturnState&gt0&lt/returnState&gt&ltargs&gt</code>
	 * <br>
	 *         if user does not exist,
	 *         <code>&ltargs&gt&ltreturnState&gt2&lt/returnState&gt&ltargs&gt</code>
	 * <br>
	 *         if other failure,
	 *         <code>&ltargs&gt&ltreturnState&gt1&lt/returnState&gt&ltargs&gt</code>
	 */
	public String removeUser(String xmlArgs) {
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering removeUser");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		if (args.size() != 1) {
			return createReturnXml(FAILURE_STATE);
		}

		try {
			String t24UserName = (String) args.get(T24_USER_NAME);
			logger.info("The Login Type is " + loginMode);
			delegate.removeUser(t24UserName);
		} catch (ArcUserStateException e) {
			return createReturnXml(UNEXPECTED_USER_EXISTENCE_STATE);
		} catch (ArcAuthenticationServerException e) {
			// TODO want to convey error info to (non Java) client
			return createReturnXml(FAILURE_STATE);
		} catch (IllegalStateException e) {
			return createReturnXml(FAILURE_STATE);
		}
		String returnString = createReturnXml(SUCCESS_STATE);
		if (logger.isDebugEnabled())
			logger.info("returning from removeUser");
		if (logger.isDebugEnabled())
			logger.info("return value is" + returnString);
		// Now create the return
		return returnString;
	}

	/**
	 * Wraps {@link UserManagement#userExists(String)}. Not currently used.
	 * 
	 * @deprecated
	 * @param xmlArgs
	 *            e.g.
	 *            <code>&ltargs&gt&ltt24UserName&gtuserToSearchFor&lt/t24UserName&gt&lt/args&gt</code>
	 * @return "true" if user exists in auth server, "false" if not or there was
	 *         a failure.
	 */
	public String userExists(String xmlArgs) {
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering userExists");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		if (args.size() != 1) {
			return Boolean.FALSE.toString();
		}

		try {
			return new Boolean(delegate.userExists((String) args
					.get(T24_USER_NAME))).toString();

		} catch (ArcAuthenticationServerException e) {
			return Boolean.FALSE.toString();
		}
	}

	/**
	 * Wraps {@link UserManagement#getUserFailureCounts(String)}.
	 * 
	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *            </args>
     *
	 * * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error       
	 *          
	 */
	
	
	public String getUserFailureCounts(String xmlArgs){
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering getUserFailureCounts");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		Map map1 = null;
		try{
			String t24UserName = (String)args.get(T24_USER_NAME);
			map1 = delegate.getUserFailureCounts(t24UserName);
			System.out.println(" Map in XML USer "+map1);
			
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in addUser(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
		
		String returnedString = createFailureCountXml(map1,SUCCESS_STATE);
		return returnedString;
		
	}
	/**
	 * Method used to construct the xml tag for the response.
	 * Reads the configuration file and look for Authentication Type code and 
	 * stores the Key as AuthTypeCode 
	 * stores the value as xmlTag.
	 * @return Map 
	 */	
	public Map initialiseMap(){
		String configFile = System.getProperty("ARC_CONFIG_PATH");
        if(configFile != null && !configFile.equals("")){
			logger.debug("Config file path retrived : " + configFile);
		}else{
			logger.error("ARC_CONFIG_PATH not set in System Property");
		}
		
		String appName = System.getProperty("ARC_CONFIG_APP_NAME");
		if(appName != null && !appName.equals("")){
			logger.debug("Application name retrived : " + appName);
		}else{
			appName ="ARC";
			logger.error("ARC_CONFIG_APP_NAME not set in System Property");
		}
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		logger.debug("parser object created from configfile");
		Map[] configMap = parser.parse();
		logger.debug("ConfigMap is created by parsing the configfile");
		AuthenticationServerConfiguration config = new AuthenticationServerConfiguration(configMap[0]);
	       
	    if (config == null){
	    	System.out.println(" Authentication Server Configuration  config is null ");
	    }
	    
	    HashMap failureCountXml = new HashMap();
        failureCountXml.put(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE),"otp-failure-count");
		if (config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_LOGINMEMWORD) != null) {
	        	failureCountXml.put(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_LOGINMEMWORD),"memword-failure-count");
	    }
	    else {
	    	failureCountXml.put(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD),"memword-failure-count");
	    }
		failureCountXml.put(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD),"password-failure-count");
		failureCountXml.put(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN),"pin-failure-count");
					
		return failureCountXml;
	}
	/**
	 * Wraps {@link UserManagement#resetFailureCounts(String)}.
	 * 	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *            </args>
     *
	 * * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	
	public String resetFailureCounts(String xmlArgs){
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering resetFailureCounts");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
	
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = null;
		Map fCount = null;
		try{
		   String t24UserName = (String)args.get(T24_USER_NAME);
		   fCount = delegate.resetFailureCounts(t24UserName);
		   
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in resetFailureCounts(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
		StringBuffer sb = new StringBuffer();
		
		returnedString = createFailureCountXml(fCount,SUCCESS_STATE);
		return returnedString;
	}

	/**
	 * Wraps {@link UserManagement#resetUserPassword(String)}.
	 * 	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *            </args>
     *
	 * * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	public String resetUserPassword(String xmlArgs){
	
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering resetUserPassword");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = null;
		try{
			String t24UserName =(String)args.get(T24_USER_NAME);
			status = delegate.resetUserPassword(t24UserName);
			
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in resetUserPassword(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
		StringBuffer sb = new StringBuffer();
		returnedString = super.createReturnXml(status,sb);
		return returnedString;
	}
	
	/**
	 * Wraps {@link UserManagement#resetUserPin(String)}.
	 * 	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *            </args>
     *
	 * * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure,
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	
	public String resetUserPin(String xmlArgs){
	
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering resetPin");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = null;
		
		try{
			String t24UserName =(String)args.get(T24_USER_NAME);
			status = delegate.resetUserPin(t24UserName);
			
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in resetUserPin(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
	
		StringBuffer sb = new StringBuffer();
		returnedString = super.createReturnXml(status,sb);
		return returnedString;
	}
	
	/**
	 * Wraps {@link UserManagement#getAuditLogs(String)}.
	 * 	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *            </args>
     *
	 * * @return if success,
	 *         <args>
	 *           <returnState>0</returnState>
	 *            <audit-record>
	 *	            <id>id</id>
	 *	            <timestamp>timestamp</timestamp>
	 *	            <message>message</message>
	 *	            <action>action</action>
	 *	            <parameters>parameters</parameters>
	 *	            <direct-user-id>direct-user-id</direct-user-id>
	 *              <target-user-id>target-user-id</target-user-id>
	 *	            <status>status</status>
	 *	            <tainted>true/false</tainted>
	 *	            <response>response</response>
	 *	            <channel>channel</channel>
	 *             </audit-record>
	 *             <audit-record>
	 *	             ...
	 *             </audit-record>
     *          <args>	
     * <br>  if failure,
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	
	
	public String getAuditLogs(String xmlArgs){
		System.out.println(" entering getAuditLogs ");
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("Entering getAuditLogs");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = "0";
		AuditLog auditLog[] = null;
		try{
			String t24UserName = (String)args.get(T24_USER_NAME);
			String fromDate = (String)args.get(FROM_DATE);
			String toDate = (String)args.get(TO_DATE);
			auditLog = delegate.getAuditLogs(t24UserName, fromDate, toDate);
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in getAuditLogs(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
    	returnedString = createAuditLogXml(auditLog,status);
   	    return returnedString;
	}
	/**
	 * Wraps {@link UserManagement#updateMemeorableWord(String)}.
	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
	 *             <memWord>MemorableWord</memWord>
     *           </args>
     *
	 * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure,
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	
	
	public String updateMemorableWord(String xmlArgs){
		
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("updateMemorableWord");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = null;
		
		try{
			String t24UserName =(String)args.get(T24_USER_NAME);
			String memWord = (String)args.get(MEMORABLE_DATA);
			status = delegate.updateMemorableWord(t24UserName,memWord);
			
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in resetUserPin(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
		StringBuffer sb = new StringBuffer();
		returnedString = super.createReturnXml(status,sb);
		return returnedString;
		
	}
	/**
	 * Wraps {@link UserManagement#updateAuthServerUserId(String)}.
	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
	 *             <AuthServerUserID>AuthServerUserID</AuthServerUserID>
     *           </args>
	 * @return if success,
	 *         <args><returnstate>0</returnState></args>
	 * <br>  if failure,
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */
	
    public String updateAuthServerUserId(String xmlArgs){
		
		checkClassLoader();
		if (logger.isDebugEnabled())
			logger.info("updateAuthServerUserId");
		if (logger.isDebugEnabled())
			logger.info("parameter is: " + xmlArgs);
		Map args = parseXmlIntoArgs(xmlArgs);
		String returnedString = null;
		String status = null;
		
		try{
			String t24UserName =(String)args.get(T24_USER_NAME);
			String authServerUserId = (String)args.get(AUTH_SERVER_USER_ID);
			status = delegate.updateAuthServerUserId(t24UserName,authServerUserId);
			
		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in resetUserPin(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
		StringBuffer sb = new StringBuffer();
		returnedString = super.createReturnXml(status,sb);
		return returnedString;
		
	}
    
    /**
	 * Wraps {@link UserManagement#getAuthenticatorStatus(String)}.
	 * @param xmlArgs
	 *            e.g.
	 *           <args>
	 *             <t24UserName>t24-user-id</t24UserName> 
     *           </args>
     * @return if success,
	 *        <args>
     *          <returnState>0</returnState>
     *          <server-activity>
     *          <auth-type>authtype1</auth-type>
     *          <start-date>startdate1</start-date>
     *          <expiry-date>expirydate1</expiry-date>
     *          <status>active</status>
     *          </server-activity>
     *          <server-activity>
     *          ...
     *          </server-activity>
     *          </args>
     *            
	 * <br>  if failure,
	 *         <args><returnstate>1 or 2 or 3</returnState></args>
	 *
	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
	 */

     public String getAuthenticatorStatus(String xmlArgs){
    	checkClassLoader();
 		if (logger.isDebugEnabled())
 			logger.info("getAuthenticatorStatus");
 		if (logger.isDebugEnabled())
 			logger.info("parameter is: " + xmlArgs);
 		Map args = parseXmlIntoArgs(xmlArgs);
 		Map map = null;
 		String returnedString = null;
 		String status=SUCCESS_STATE;
 		
 		try{
 			String t24UserName =(String)args.get(T24_USER_NAME);
 			map = delegate.getAuthenticatorStatus(t24UserName);
 		}catch(ArcUserStateException e){
			return createReturnXml(FAILURE_STATE);
		}catch(ArcAuthenticationServerConnectionException e){
			return createReturnXml(COMMUNICATION_ERROR);
		}catch(ArcAuthenticationServerException e){
			return createReturnXml(OTHER_ERROR);
		}catch(Exception e){
			logger.debug("Exception in getAuthenticatorStatus(): " +e.getMessage(), e);
			return createReturnXml(OTHER_ERROR);
		}
 		
 		returnedString = this.createAuthenticatorStatusXml(map, status);
 		 		
	   return returnedString;
	
     }
     
     public String updateCustomerData(String xmlArgs){
    	checkClassLoader();
   		if (logger.isDebugEnabled())
   			logger.info("updateAuthenticatorStatus");
   		if (logger.isDebugEnabled())
   			logger.info("parameter is: " + xmlArgs);
   		Map args = parseXmlIntoArgs(xmlArgs);
   		String returnedString = null;
   		String status = null;
   		try{
   			String t24UserName = (String)args.get(T24_USER_NAME);
   			HashMap customerData = (HashMap)args.get("Customer");
   			status = delegate.updateCustomerData(t24UserName,customerData);
   			
   		}catch(ArcUserStateException e){
 			return createReturnXml(FAILURE_STATE);
 		}catch(ArcAuthenticationServerConnectionException e){
 			return createReturnXml(COMMUNICATION_ERROR);
 		}catch(ArcAuthenticationServerException e){
 			return createReturnXml(OTHER_ERROR);
 		}catch(Exception e){
 			logger.debug("Exception in getAuthenticatorStatus(): " +e.getMessage(), e);
 			return createReturnXml(OTHER_ERROR);
 		}
   		StringBuffer sb = new StringBuffer();
 		returnedString = super.createReturnXml(status, sb);
   		return returnedString;
   		 
     }

     /**
 	 * Wraps {@link UserManagement#updateAuthenticatorStatus(String)}.
 	 * @param xmlArgs
 	 *            e.g.
 	 *           <args>
 	 *             <t24UserName>t24-user-id</t24UserName> 
 	 *             <authenticator>authenticator</authenticator>
 	 *             <status>status</status>
 	 *            </args>
     * @return if success,
 	 *        <args>
 	 *        <returnState>0</returnState>
 	 *        <args>            
 	 * <br>  if failure,
 	 *         <args><returnstate>1 or 2 or 3</returnState></args>
 	 *
 	 *  1 – User does not exist
     *  2 – Cannot communicate with Authentication Server
     *  3 – Other error
     *  
 	 */
     public String updateAuthenticatorStatus(String xmlArgs){
     	checkClassLoader();
  		if (logger.isDebugEnabled())
  			logger.info("updateAuthenticatorStatus");
  		if (logger.isDebugEnabled())
  			logger.info("parameter is: " + xmlArgs);
  		Map args = parseXmlIntoArgs(xmlArgs);
  		Map map = null;
  		String returnedString = null;
  		String status=SUCCESS_STATE;
  		
  		try{
  			String t24UserName =(String)args.get(T24_USER_NAME);
  			String authType = (String)args.get(AUTH_TYPE);
  			String authStatus = (String)args.get(STATUS);
  			map = delegate.updateAuthenticatorStatus(t24UserName,authType,authStatus);
  		}catch(ArcUserStateException e){
 			return createReturnXml(FAILURE_STATE);
 		}catch(ArcAuthenticationServerConnectionException e){
 			return createReturnXml(COMMUNICATION_ERROR);
 		}catch(ArcAuthenticationServerException e){
 			return createReturnXml(OTHER_ERROR);
 		}catch(Exception e){
 			logger.debug("Exception in getAuthenticatorStatus(): " +e.getMessage(), e);
 			return createReturnXml(OTHER_ERROR);
 		}
  		
  		returnedString = this.createAuthenticatorStatusXml(map, status);
  		 		
 	    return returnedString;
 	
      }
   
     /**
	 * Wraps {@link UserManagement#getArcUserId(String)}. Not currently used.
	 * TODO get rid?
	 */
		
	public String getArcUserId(String xmlArgs) {
		checkClassLoader();
		Map args = parseXmlIntoArgs(xmlArgs);
		if (args.size() != 1) {
			return "";
		}

		try {
			return delegate.getArcUserId((String) args.get(T24_USER_NAME));
		} catch (ArcAuthenticationServerException e) {
			return "";
		} catch (IllegalStateException e) {
			return "";
		}

	}

	String createReturnXml(String status) {
		return createReturnXml(null, status);
	}

	String createReturnXml(String t24Password, String status) {
		//vivek
		StringBuffer toAdd = getReturnArgs(t24Password);
		return createReturnXml(status, toAdd);
	}

	/** createAuditLogXml(AuditLog,status)
	 *  method create the xmlresponse which will sent to the T24 server.
	 *  method will be invoked by getAuditLogs()
	 * @param record
	 * @param status
	 * @return xmlResponse to T24 Server.
	 */
	public String createAuditLogXml(AuditLog record[],String status){
		logger.info("Entering createAuditLogXml()");
		StringBuffer sb = new StringBuffer();
		if (record == null  || record.length == 0){
			return createReturnXml(status,sb);
		}
		for(int i =0;i < record.length;i++){
			sb.append("<audit-record>");
			if (record[i].getTimestamp() != null){
			  sb.append("<timestamp>"+record[i].getTimestamp()+"</timestamp>");
			}else{
			  sb.append("<timestamp> </timestamp>");
			}
			if(record[i].getMessage() != null){
			  sb.append("<message>"+record[i].getMessage()+"</message>");	
			}else{
			  sb.append("<message> </message>");
			}
			if(record[i].getAction() != null){
			  sb.append("<action>"+record[i].getAction()+"</action>");
			}else{
			  sb.append("<action> </action>");	
			}
			if(record[i].getParameter() != null){
			  sb.append("<parameters>"+record[i].getParameter()+"</parameters>");
			}else{
			  sb.append("<parameters> </parameters>");
			}
			if(record[i].getTarget_user() != null){
			  sb.append("<target-user-id>"+record[i].getTarget_user()+"</target-user-id>");	
			}else{
			  sb.append("<target-user-id> </target-user-id>");	
			}
			
			if(record[i].getStatus() != null){
			  sb.append("<status>"+record[i].getStatus()+"</status>");	
			}else{
			  sb.append("<status> </status>");	
			}
			
			sb.append("<tainted>"+record[i].isTainted()+"</tainted>");
			
			if(record[i].getResponse() != null){
			  sb.append("<response>"+record[i].getResponse()+"</response>");
			}else{
			  sb.append("<response> </response>");
			}
			
			if(record[i].getChannel() != null){
    		  sb.append("<channel>"+record[i].getChannel()+"</channel>");	
			}else{
			  sb.append("<channel> </channel>");
			}
			
			sb.append("</audit-record>");
		}
		logger.info("AuditLog Record  "+sb);
		return createReturnXml(status,sb);
	}

	/** createFailureCountXml(Map,status)
	 *  method create the xmlresponse which will sent to the T24 server.
	 *  method will be invoked by getFailureCount() and resetFailureCount()
	 * @param record
	 * @param status
	 * @return xmlResponse to T24 Server.
	 */
    public String createFailureCountXml(Map map,String status){
		logger.info("Entering createFailureCountXml()");
		StringBuffer xmlTag = new StringBuffer();
		HashMap mapping  = (HashMap)map;
	    HashMap xmlElement = (HashMap)initialiseMap();
        Set mapset = xmlElement.entrySet();
        Iterator it = mapset.iterator();
        while(it.hasNext()){
        	Map.Entry me = (Map.Entry)it.next();
        	String Key =(String) me.getKey();
           	String argname  = (String)me.getValue();
           	String argvalue =(String)mapping.get(Key);
           	if (argvalue == null){
        		argvalue= "NA";
        	}
        	xmlTag = xmlTag.append(getReturnArg(argvalue,argname).toString());
       }
        logger.info("createReturnXML() "+xmlTag);
        return createReturnXml(status,xmlTag);
	}
	
    /** createAuthenticatorStatusXml(Map,status)
	 *  method create the xmlresponse which will sent to the T24 server.
	 *  method will be invoked getAuthenticatorStatus() and updateAuthenticatorStatus()
	 * @param map
	 * @param status
	 * @return xmlResponse to T24 Server.
	 */
    
	public String createAuthenticatorStatusXml(Map map,String status){
		logger.info(" Entering createAuthenticatorStatusXML() ");
		HashMap authenticatorStatus= (HashMap)map;
		StringBuffer xmlTag = new StringBuffer();
		String tag [] = {"status","start-date","end-date","auth-type"};
		Set mapset = authenticatorStatus.entrySet();
	    Iterator it = mapset.iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			String Key = (String)me.getKey();
			xmlTag.append("<server-activity>");
	    	String value[] = ((String)me.getValue()).split(":");
			for(int i = 0; i < value.length;i++){
			xmlTag.append("<"+tag[i]+">"+value[i]+"</"+tag[i]+">");
			}
			xmlTag.append("</server-activity>");
		}
		logger.info("createAuthenticatorStatusXML() "+xmlTag);	
		return createReturnXml(status,xmlTag);
    }
	
	private StringBuffer getReturnArgs(String t24Password) {
		StringBuffer toAdd = new StringBuffer();
		if (null != t24Password) {
			toAdd.append("<" + XmlUserManagement.T24_PASSWORD + ">");
			toAdd.append(t24Password);
			toAdd.append("</" + XmlUserManagement.T24_PASSWORD + ">");
		}
		return toAdd;
	}
}
