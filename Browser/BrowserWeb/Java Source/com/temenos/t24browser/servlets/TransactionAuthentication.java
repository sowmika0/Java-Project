package com.temenos.t24browser.servlets;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This class will perform OTP and challenge response authentication This class
 * will interact with 4tress server and perform authentication This class was
 * developed based on the AuthenticationManager class.
 * 
 * @author karuppiahdas
 * 
 */

public final class TransactionAuthentication {

	private static AuthenticationServerConfiguration config;
	private static final Logger logger = LoggerFactory
			.getLogger(TransactionAuthentication.class);

	private static final int OTP_CHALENGE_AUTH_SUCCESS = 0;
	private static final int OTP_CHALLENGE_AUTH_LOCKED = 2;
	private static final int OTP_CHALLENGE_AUTH_ERROR = 3;

	public static final String TRANS_AUTHENTICATOR_LOCKED = "TRANS_AUTHENTICATOR_LOCKED";
	public static final String TRANS_AUTHENTICATOR_ERROR = "TRANS_AUTHENTICATOR_ERROR";
	public static final String TRANS_ERROR_PAGE = "TRANS_ERROR_PAGE";

	static {
		config = CommonServletUtil.getInstance().getConfig(0);
	}

	/**
	 * Authenticates password value using device authenticators. All the three
	 * options OTP, CHRES, SIGN supported Returns true in following cases; If no
	 * condition tags are defined If all conditions are satisfied
	 * 
	 * Call helpers methods to autheticate password using device authenticator
	 * username - User Name value for which divice is linked password - one time
	 * password or response (for challenge) challengeValue - 4tress generated
	 * challenge Or user configured challenge (sign) - will be set only of CHRES
	 * option signMode -- boolean flag, will be set only challenge value is
	 * preconfigured.
	 * 
	 * @param String
	 *            username, String password, HttpSession session
	 * 
	 * @return Boolean (true on Success, false on Failure)
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * 
	 */
	public boolean doOTPAuthentication(String username, String password,
			HttpSession session) throws AccountExpiredException,
			FailedLoginException, ArcAuthenticationServerException {

		boolean signMode = false;
		logger.info("doOTPAuthentication Called");
		FtressHelpers70.setConfig(config);
		// Extract challenge value from session variable
		String challengeValue = ((String) session.getAttribute("CHALLENGE"));
		if ((challengeValue != null) && (challengeValue.trim() != "")) {
			// check SIGNCHALLENGE session attribute and set signMode
			// boolean flag.
			String signChallenge = ((String) session
					.getAttribute("SIGNCHALLENGE"));
			if ("1".equalsIgnoreCase(signChallenge)) {
				signMode = true;
			}
		}
		int otpAuthenticationStatus = FtressHelpers70.getInstance()
				.doOTPChallengeAuthentication(username, password,
						challengeValue, signMode);

		boolean otpAuthenticationSuccess = false;
		if (otpAuthenticationStatus == OTP_CHALENGE_AUTH_SUCCESS) {
			otpAuthenticationSuccess = true;
		} else {
			// On failure, get the configured transaction error page
			String transErrorPage = getTransactionErrorPage();
			session.setAttribute(TRANS_ERROR_PAGE, transErrorPage);
			if (otpAuthenticationStatus == OTP_CHALLENGE_AUTH_LOCKED) {
				// Authenticated locked, maximum failure count reached.. set
				// session variables.
				session.setAttribute(TRANS_AUTHENTICATOR_LOCKED, "true");
				logger.info("User OOB OTP authentication failed, maximum failure count reached");
			} else if (otpAuthenticationStatus == OTP_CHALLENGE_AUTH_ERROR) {
				// Unexpected Error occurred during transaction authentication
				session.setAttribute(TRANS_AUTHENTICATOR_ERROR, "true");
			}
		}
		return otpAuthenticationSuccess;
	}

	/**
	 * @param UserName
	 * @param oldPassword
	 * @param newPassword
	 * @return boolean
	 */
	public boolean changeExpiredPassword(String UserName, String oldPassword,
			String newPassword) {
		return FtressHelpers70
				.getInstance()
				.changeOwnExpiredPassword(
						UserName,
						oldPassword,
						newPassword,
						config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD));
	}

	/**
	 * @param username
	 * @param password
	 * @return genericResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountLockedException
	 * @throws AccountExpiredException
	 */
	public static GenericAuthenticationResponse doPassAuthentication(
			String username, String password) throws AccountExpiredException,
			AccountLockedException, FailedLoginException,
			ArcAuthenticationServerException {
		return FtressHelpers70.getInstance().authenticatePassWord(username,
				password.toCharArray());
	}

	/**
	 * @param username
	 * @return chanllengeValue
	 */
	public String getAuthenticationChallenge(String username) {
		FtressHelpers70.setConfig(config);
		return FtressHelpers70.getInstance().getAuthenticationChallenge(
				username);
	}

	/**
	 * @return transErrorPage
	 */
	private String getTransactionErrorPage() {
		String transErrorPage = "/jsps/transaction_error.jsp";
		if (config
				.getConfigValue(AuthenticationServerConfiguration.TRANSACTION_ERROR_PAGE) != null) {
			transErrorPage = config
					.getConfigValue(AuthenticationServerConfiguration.TRANSACTION_ERROR_PAGE);
		}
		return transErrorPage;

	}

}
