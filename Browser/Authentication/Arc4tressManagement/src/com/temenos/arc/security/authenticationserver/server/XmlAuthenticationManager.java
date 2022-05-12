    package com.temenos.arc.security.authenticationserver.server;

/**
 * This class is an entry point for ChallengeResponse/password/Memword/OTP authentication.
 * This is called from T24 using CALLJ
 * @author Cerun
 */
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.server.XmlWrapper;
import com.temenos.arc.security.authenticationserver.common.AuthenticationResponse;
import java.util.Map;

public class XmlAuthenticationManager extends XmlWrapper {
	/** 
	 */
	// The Server configuration.
	private static AuthenticationServerConfiguration serverConfig;
    // contains the t24 username passed in.
	private String t24UserName;
	// contains the memword/password/otp
	private String password;
	// contains the Authentication Type.
	private String authType;
	//Contains the challenge posted to user.
	private String challenge;
	// Contains the user response.
	private String response;
	
	private AuthenticationManager delegate;
	
	AuthenticationResponse authResp;

	/**
	 * 
	 */
	public XmlAuthenticationManager() {
		logger.debug("Entering XmlAuthenticationManager()");
		serverConfig = AuthenticationServerConfiguration.getStatic();
		logger.debug("got configuration ");
	}

	/**
	 * This method is only used for testing
	 * 
	 * @param delegate
	 */
	public XmlAuthenticationManager(AuthenticationServerConfiguration conf) {
		checkClassLoader();
		serverConfig = conf;
	}

	/**
	 * @param xmlArgument
	 * @return
	 */
	public String authenticate(String xmlArgument) {
		logger.debug("Entering authenticate method()");
		try {
			Map args = parseXmlIntoArgs(xmlArgument);
			if(args != null && !args.equals("")){
				this.parseXmlArguments(args);				
			}else{
				logger.debug("Authentication Failed.Argument is Null");
				this.createResponse("3", "Argument is Null");
				return this.createReturnXml();
			}			
		} catch (Exception e) {
			logger.debug("Parsing Failed. Exception in parsing");
			this.createResponse("3", e.getMessage());
			return this.createReturnXml();
		}
		try{
			doAuthentication();
		}catch(Exception e){
			this.createResponse("3", e.getMessage());
			return this.createReturnXml();
		}		
		// Create the return Xml
		return this.createReturnXml();
	}
	
	/**
	 * @param userName
	 */
	private void setT24UserName(String userName) {
		this.t24UserName = userName;
	}
	/**
	 * 
	 */
	private void setAuthenticationType(String authType) {
		this.authType = authType;
	}

	/**
	 * 
	 * @param password
	 */
	private void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @param challenge
	 */
	private void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	/**
	 * 
	 * @param response
	 */
	private void setResponse(String response) {
		this.response = response;
	}
  /**
   * This method filters the request based on the Authentication type present in xml input.
   *
   */  
	private void doAuthentication(){
		logger.debug("Entering doAuthentication method()");
		String authServer = this.findServer();
		logger.debug("authentication server is : " + authServer);
		this.checkValid(this.getAuthenticationType());	
		if (authServer.equals(AuthenticationServerConfiguration.FTRESS)){
			this.delegate = new com.temenos.arc.security.authenticationserver.ftress.server.AuthenticationManager(serverConfig);
		}else{
			throw new ArcAuthenticationServerException("Authentication server is invalid");
		}	
		if(this.authType.equals("CR")){
			this.doChallengeResponse();
		}else if(this.authType.equals("PW")){
			this.doPassword();
		}else if(this.authType.equals("MW")){
			this.doMemWord();
		}else if(this.authType.equals("OTP")){
			this.doOneTimePassword();
		}else{			
			throw new ArcAuthenticationServerException("No Authentication Type specified in xml data");
		}
		if(this.authResp == null || this.authResp.equals("")){
			throw new ArcAuthenticationServerException("Authentication Response is null");						
		}		
	}
	
	private String findServer(){
		String server= null;
		server = serverConfig.getConfigValue(AuthenticationServerConfiguration.ARC_AUTHENTICATION_SERVER);
		if (server == null || server.equals("")){
			throw new ArcAuthenticationServerException("Authentication Server not specified in config");
		}
		return server;
	}
/**
 * This generic method validates the incoming value for null
 * @param value
 */	
	private void checkValid(String value){
		if (value == null || value.equals("")){
			throw new ArcAuthenticationServerException("Invalid argument passed into XmlAuthentication Manager");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String getT24UserName() {
		return this.t24UserName;
	}
	/**
	 * @return
	 */
	private String getAuthenticationType() {
		return this.authType;
	}

	private String getPassword() {
		return this.password;
	}

	private String getChallenge() {
		return this.challenge;
	}
	/**
	 * @return
	 */
	private String getResponse() {
		return this.response;
	}
/**
 * This method invokes the implementation method for CR in AuthenticationManager.
 *
 */	
	private void doChallengeResponse(){
		logger.debug("Entering doChallengeResponse method()");
		checkValid(this.getChallenge());
		checkValid(this.getResponse());
		checkValid(this.getT24UserName());
		this.authResp = delegate.authenticateChallengeResponse(this.getT24UserName(), this.getChallenge(), this.getResponse());
	}
/**
 * This method invokes the implementation method for pwd in AuthenticationManager.
 *
 */	
	private void doPassword(){
		logger.debug("Entering doPassword method()");
		checkValid(this.getT24UserName());
		checkValid(this.getPassword());
		this.authResp = delegate.authenticatePassword(this.getT24UserName(), this.getPassword());
	}
/**
 * This method invokes the implementation method for MemWord in AuthenticationManager.
 *
 */	
	private void doMemWord(){
		logger.debug("Entering doPassword method()");
		checkValid(this.getPassword());
		checkValid(this.getT24UserName());
		this.authResp = delegate.authenticateMemorableWord(this.getT24UserName(), this.getPassword());
	}
/**
 * This method invokes the implementation method for OTP in AuthenticationManager.
 *
 */	
	private void doOneTimePassword(){
		logger.debug("Entering doOneTimePassword method()");
		checkValid(this.getPassword());
		checkValid(this.getT24UserName());
		this.authResp = delegate.authenticateOneTimePassword(this.getT24UserName(), this.getPassword());
	}
/**
 * This method parse the incoming xml argument. 
* @param args
*/
	private void parseXmlArguments(Map args) {
		logger.debug("Entering parseXmlArgument method()");
		this.setT24UserName((String) args
				.get(XmlAuthenticationManagerConstants.userName));
		this.setAuthenticationType((String) args
				.get(XmlAuthenticationManagerConstants.authenticationType));
		this.setPassword((String) args
				.get(XmlAuthenticationManagerConstants.password));
		this.setChallenge((String) args
				.get(XmlAuthenticationManagerConstants.challenge));
		this.setResponse((String) args
				.get(XmlAuthenticationManagerConstants.response));
		logger.debug("XmlArgument parsed successfully");
	}
/**
 * This methos creates the return XML message for T24.
 * @return
 */
	private String createReturnXml() {
		
		if(this.authResp == null || this.authResp.equals("")){
			this.createResponse("1","Authentication failed");
		}
		StringBuffer toAdd = new StringBuffer();
		toAdd.append("<" + XmlAuthenticationManagerConstants.args +">");
		toAdd.append("<" + XmlAuthenticationManagerConstants.returnState
				+ ">");
		toAdd.append(this.authResp.getCode());
		toAdd.append("</" + XmlAuthenticationManagerConstants.returnState
				+ ">");
		toAdd.append("<" + XmlAuthenticationManagerConstants.returnComment
				+ ">");
		toAdd.append(this.authResp.getReason());
		toAdd.append("</" + XmlAuthenticationManagerConstants.returnComment
				+ ">");
		toAdd.append("</" + XmlAuthenticationManagerConstants.args +">");
		logger.debug("The return Xml is " + toAdd.toString());
		return toAdd.toString();
	}
/**
 * This method creates the return AuthenticationResponse object if in case it is not created in AuthenticationManager.  
 * @param code
 * @param message
 */	
	private void createResponse(String code,String message){
		com.temenos.arc.security.authenticationserver.common.AuthenticationResponse authResp = new AuthenticationResponse();
		authResp.setCode(code);
		authResp.setReason(message);
		this.authResp = authResp;
	}
	
}