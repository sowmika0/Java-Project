package com.temenos.arc.security.authenticationserver.ftress.server;

/**
 * This class Authenticates the User input in Ftress server.
 * @author Cerun
 */
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationResponse;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.server.UserManagement;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.AccountExpiredException;

public class AuthenticationManager
		implements
		com.temenos.arc.security.authenticationserver.server.AuthenticationManager {

	private AuthenticationServerConfiguration serverConfig;

	private Logger logger;

	private AuthenticationResponse respObj;

	public AuthenticationManager(AuthenticationServerConfiguration serverConfig) {
		logger = LoggerFactory.getLogger(AuthenticationManager.class);
		AuthenticationManagerHelper.setConfig(serverConfig);
		this.serverConfig = serverConfig;
	}

	/**
	 * This method does password authentication in Ftress.
	 */
	public AuthenticationResponse authenticatePassword(String userId,
			String password) {
		logger
				.debug("Entering authenticatePassword() method in ftress AuthenticationManager");
		String authType = this.serverConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD);
		String ftressUserId = this.getUserId(userId);
		this.doAuthentication(ftressUserId, "PW", password, "", "", authType);
		return respObj;
	}

	/**
	 * This method does MemWord authentication in Ftress.
	 */
	public AuthenticationResponse authenticateMemorableWord(String userId,
			String memorableWord) {
		logger
				.debug("Entering authenticateMemorableWord() method in ftress AuthenticationManager");
		String authType = this.serverConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD);
		String ftressUserId = this.getUserId(userId);
		this.doAuthentication(ftressUserId, "MW", memorableWord, "", "",
				authType);
		logger
				.debug("Returning from authenticateMemorableWord() method in ftress AuthenticationManager");
		return respObj;
	}

	/**
	 * This method does OTP authentication in Ftress.
	 */
	public AuthenticationResponse authenticateOneTimePassword(String userId,
			String oneTimePassword) {
		logger
				.debug("Entering authenticateOneTimePassword() method in ftress AuthenticationManager");
		String authType = this.serverConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE);
		String ftressUserId = this.getUserId(userId);
		this.doAuthentication(ftressUserId, "OTP", oneTimePassword, "", "",
				authType);
		logger
				.debug("Returning from authenticateOneTimePassword() method in ftress AuthenticationManager");
		return respObj;
	}

	/**
	 * This method does ChallengeResponse(OTP- ASYNC) authentication in Ftress.
	 */
	public AuthenticationResponse authenticateChallengeResponse(String userId,
			String challenge, String response) {
		logger
				.debug("Entering authenticateChallengeResponse() method in ftress AuthenticationManager");
		String authType = this.serverConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE);
		String ftressUserId = this.getUserId(userId);
		this.doAuthentication(ftressUserId, "CR", "", challenge, response,
				authType);
		logger
				.debug("Returning authenticateChallengeResponse() method in ftress AuthenticationManager");
		return respObj;
	}

	/**
	 * This method invokes authenticate() method of AuthenticationManagerHelper
	 * for authenticating the user i/p.
	 * 
	 * @param ftressUserId
	 * @param requestType
	 * @param password
	 * @param challenge
	 * @param response
	 * @param authType
	 */

	private void doAuthentication(String ftressUserId, String requestType,
			String password, String challenge, String response, String authType) {

		try {
			String code;
			String message;
			com.aspace.ftress.interfaces.ftress.DTO.AuthenticationResponse ftressResponse = AuthenticationManagerHelper
					.authenticate(ftressUserId, requestType, password,
							challenge, response, authType);
			if (ftressResponse.getResponse() == 1) {
				code = "0"; // indicating Success to T24
				message = "Successful Authentication";
				logger.debug("User Successfully Authenticated.");
			} else {
				code = Integer.toString(ftressResponse.getResponse());
				message = ftressResponse.getMessage();
				logger.debug("Authentication failed with error code:" + code
						+ ". Error message is " + message);
			}
			this.createAuthResponseObject(code, message);
		} catch (ArcAuthenticationServerException e) {
			logger
					.debug("Exception raised during Authentication. Error message is "
							+ e.getMessage());
			this.createAuthResponseObject("1", e.getMessage());
		} catch (FailedLoginException e) {
			logger
					.debug("Exception raised during Authentication. Error message is "
							+ e.getMessage());
			this.createAuthResponseObject("1", e.getMessage());
		} catch (AccountExpiredException e) {
			logger
					.debug("Exception raised during Authentication. Error message is "
							+ e.getMessage());
			this.createAuthResponseObject("1", e.getMessage());
		}
	}

	/**
	 * This method invokes UserManagement for getting the Ftress user from T24
	 * user name.
	 * 
	 * @param userId
	 * @return
	 */

	private String getUserId(String userId) {
		logger.debug("Creating object for UserManagement.");
		UserManagement userMgmt = new UserManagement();
		logger.debug("Trying to get Ftress userId using T24 userName");
		logger.debug("T24 userId is" + userId);
		String ftressUserId = userMgmt.getArcUserId(userId);
		logger.debug("Corresponding Ftress userId is" + ftressUserId);
		return ftressUserId;
	}

	/**
	 * This method creates authenticationResponse object for returning to
	 * XmlAuthenticationManager.
	 * 
	 * @param code
	 * @param message
	 * @return
	 */
	private void createAuthResponseObject(String code, String message) {
		this.respObj = new AuthenticationResponse();
		this.respObj.setCode(code);
		this.respObj.setReason(message);
	}

}
