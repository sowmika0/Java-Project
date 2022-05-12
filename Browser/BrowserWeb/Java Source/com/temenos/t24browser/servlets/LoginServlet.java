package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Servlet implementation class for Servlet: LoginServlet This servlet.
 */
public class LoginServlet extends HttpServlet implements Servlet {

	private static final CommonServletUtil util = CommonServletUtil
			.getInstance();

	/** The Constant ERROR_PAGE. */
	public static final String ERROR_PAGE = "error.html";

	/** The Constant GET_PASSWORD_SEEDS_PAGE. */
	public static final String GET_PASSWORD_SEEDS_PAGE = "/modelbank/unprotected/passwordMemWord.jsp";

	/** The Constant CHANGE_PASSWORD_PAGE. */
	public static final String CHANGE_PASSWORD_PAGE = "/modelbank/unprotected/changePassword.jsp";

	/** The Constant SECURITY_CHECK_PAGE. */
	public static final String SECURITY_CHECK_PAGE = "/modelbank/unprotected/securityCheck.jsp";

	/** The Constant Authentication Error Page. */
	public static String AUTH_ERROR_PAGE;

	/** The Constant USER_ID. */
	public static final String USER_ID = "UserId";

	/** The Constant PASSWORD. */
	public static final String PASSWORD = "Password";

	/** The Constant MEMWORD_CHARACTERS. */
	public static final String MEMWORD_CHARACTERS = "Character";

	/** The Constant NEW_PASSWORD1. */
	public static final String NEW_PASSWORD1 = "NewPassword";

	/** The Constant NEW_PASSWORD2. */
	public static final String NEW_PASSWORD2 = "ConfirmPassword";

	/** The Constant SEED_POSITIONS. */
	public static final String SEED_POSITIONS = "seedPositions";

	/** The Constant MEMWORD_STRING. */
	public static final String MEMWORD_STRING = "MemWordString";

	/** The Constant PASSPHRASE. */
	public static final String PASSPHRASE = "Passphrase";

	/**
	 * The Constants for AuthenticationFailure and Memorable Word locked
	 * messages
	 */
	public static final String AUTH_ERROR_MESSAGE = "ERROR_MESSAGE";
	public static final String AUTHENTICATOR_LOCKED = "AUTHENTICATOR_LOCKED";
	public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

	/** The pw config. */
	private static AuthenticationServerConfiguration pwConfig;

	/** The mw config. */
	public static AuthenticationServerConfiguration mwConfig;

	/** The home page. */
	private static String homePage;

	/**
	 * Instantiates a new login servlet.
	 */
	public LoginServlet() {
		super();
		pwConfig = util.getConfig(0);
		mwConfig = util.getConfig(1);
		homePage = pwConfig
				.getConfigValue(AuthenticationServerConfiguration.ARC_HOME_PAGE);

		if (pwConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_ERROR_PAGE) == null) {
			AUTH_ERROR_PAGE = "/modelbank/unprotected/auth_error.jsp";
		} else {
			AUTH_ERROR_PAGE = pwConfig
					.getConfigValue(AuthenticationServerConfiguration.AUTH_ERROR_PAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entering doPost...");
		/*
		 * There are 3 possible points at which this method of the servlet could
		 * be called. 1. userId is entered and we need to get the seed positions
		 * 2. password/seed characters have been entered and either submit to
		 * JAAS or goto change password screen if expired password or 3. If
		 * password needs to be changed after expiry/first login then need to
		 * update password before submitting to JAAS
		 */
		String memWordString = request.getParameter(MEMWORD_CHARACTERS + "0");
		if (null != memWordString) {
			boolean moreCharacters = true;
			int i = 1;
			while (moreCharacters) {
				final String parameter = request
						.getParameter(MEMWORD_CHARACTERS + Integer.toString(i));
				if (null != parameter) {
					memWordString = memWordString + parameter;
					i++;
				} else {
					moreCharacters = false;
				}
			}
			// memWordChars = memWordString.toCharArray();
			logger.info("mem word string: " + memWordString);
		} else {
			memWordString = request.getParameter(MEMWORD_STRING);
			logger.info("mem word string: " + memWordString);
		}

		logger.info("User id: " + getUserId(request));
		logger.info("password: " + request.getParameter(PASSWORD));
		logger.info("password1: " + request.getParameter(NEW_PASSWORD1));
		logger.info("password2: " + request.getParameter(NEW_PASSWORD2));

		if (null != getUserId(request)) {
			// clear the session attributes from a failed login
			removeAttributes(request);

			if (checkNewMemWordAuthenticator(request, response)) {
				util.forwardRequest(
						request,
						response,
						mwConfig.getConfigValue(AuthenticationServerConfiguration.ACTIVIDENTITY_PAGE));
				return;
			}

			String seedPositionString = getSeedPositions(request, response);
			// If we have the seed positions then...
			request.getSession().setAttribute(SEED_POSITIONS,
					seedPositionString);
			request.getSession().setAttribute(USER_ID, getUserId(request));
			util.forwardRequest(
					request,
					response,
					mwConfig.getConfigValue(AuthenticationServerConfiguration.PWMWSEEDED_PAGE));
		} else if (null != request.getParameter(PASSWORD)
				&& null != memWordString) {
			/*
			 * By this point we should have userId, password and characters from
			 * the mem word We now need to pass these to 4TRESS for
			 * authentication 1. Perform authentication with password 2. If
			 * password is expired or first login then redirect to change
			 * password page 3. Otherwise forward to j_security_check in order
			 * to go through JAAS
			 */
			checkPasswordMemWord(request, response, memWordString);
		} else if (null != request.getParameter(NEW_PASSWORD1)
				&& null != request.getParameter(NEW_PASSWORD2)) {
			/*
			 * Now set the new password. If the new password is valid then go
			 * ahead and change the password and then submit to JAAS. If not
			 * return to the change password jsp.
			 */
			logger.info("Entering change password stage");
			// get the userid, password, memword and seed string from the
			// session
			memWordString = (String) request.getSession().getAttribute(
					MEMWORD_STRING);

			checkNewPassword(request, response, memWordString);
		} else {
			logger.error("Error getting seed positions.");
			util.forwardRequest(request, response, ERROR_PAGE);
		}

	}

	/**
	 * @param request
	 */
	private void removeAttributes(HttpServletRequest request) {
		request.getSession().removeAttribute(USER_ID);
		request.getSession().removeAttribute(PASSWORD);
		request.getSession().removeAttribute(MEMWORD_CHARACTERS);
		request.getSession().removeAttribute(NEW_PASSWORD1);
		request.getSession().removeAttribute(NEW_PASSWORD2);
		request.getSession().removeAttribute(SEED_POSITIONS);
		request.getSession().removeAttribute(MEMWORD_STRING);
		request.getSession().removeAttribute(PASSPHRASE);
		request.getSession().removeAttribute(AUTH_ERROR_MESSAGE);
		request.getSession().removeAttribute(AUTHENTICATOR_LOCKED);
		request.getSession().removeAttribute(AUTHENTICATION_FAILED);
	}

	/**
	 * Check new password.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param memWordString
	 *            the mem word string
	 * 
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void checkNewPassword(HttpServletRequest request,
			HttpServletResponse response, String memWordString)
			throws ServletException, IOException {
		if (util.isPasswordValid(request.getParameter(NEW_PASSWORD1),
				request.getParameter(NEW_PASSWORD2))) {
			logger.info("Password is valid");
			// change password
			FtressHelpers70.setConfig(pwConfig);
			if (FtressHelpers70.getInstance().changeOwnExpiredPassword(
					(String) request.getSession().getAttribute(USER_ID),
					(String) request.getSession().getAttribute(PASSWORD),
					request.getParameter(NEW_PASSWORD1))) {
				logger.info("Going to JAAS");
				// construct the form
				String passphrase = createPassphrase(
						request.getParameter(NEW_PASSWORD1),
						memWordString,
						(String) request.getSession().getAttribute(
								SEED_POSITIONS));
				logger.info("passphrase: " + passphrase);
				request.getSession().setAttribute(PASSPHRASE, passphrase);
				util.forwardRequest(request, response, homePage);
			} else {
				logger.info("change password failed, returning to jsp");
				request.getSession()
						.setAttribute(MEMWORD_STRING, memWordString);
				util.forwardRequest(request, response, CHANGE_PASSWORD_PAGE);
			}

		} else {
			logger.info("new password is invalid, returning to jsp");
			request.getSession().setAttribute(MEMWORD_STRING, memWordString);
			util.forwardRequest(request, response, CHANGE_PASSWORD_PAGE);
		}
	}

	/**
	 * Check password mem word.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param memWordString
	 *            the mem word string
	 * 
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void checkPasswordMemWord(HttpServletRequest request,
			HttpServletResponse response, String memWordString)
			throws ServletException, IOException {
		boolean passwordAuthenticationSuccess = false;
		try {
			FtressHelpers70.setConfig(pwConfig);
			FtressHelpers70.getInstance().authenticatePassWord(
					(String) request.getSession().getAttribute(USER_ID),
					request.getParameter(PASSWORD).toCharArray());
			passwordAuthenticationSuccess = true;

			if (passwordAuthenticationSuccess) {
				doMemWordAuthAndFormatPwd(request, response, memWordString);
			}

		} catch (AccountExpiredException e) {
			// set the below values only if account is expired for Password.
			// Show error page is the memword is expired
			if (!passwordAuthenticationSuccess) {
				logger.info("password expired");
				request.getSession().setAttribute(PASSWORD,
						request.getParameter(PASSWORD));
				request.getSession()
						.setAttribute(MEMWORD_STRING, memWordString);
				// If the password has expired then call the change password
				// screen
				util.forwardRequest(request, response, CHANGE_PASSWORD_PAGE);
			} else {
				logger.error("Error in login process", e);
				util.forwardRequest(request, response, ERROR_PAGE);
			}
		} catch (AccountLockedException e) {
			logger.error("AccountLockedException : Error in login process", e);
			request.getSession().setAttribute(AUTHENTICATOR_LOCKED, "true");
			request.getSession().setAttribute(AUTH_ERROR_MESSAGE,
					"User Locked : Error in login process");
			util.forwardRequest(request, response, AUTH_ERROR_PAGE);
		} catch (FailedLoginException e) {
			request.getSession().setAttribute(AUTHENTICATION_FAILED, "true");
			request.getSession().setAttribute(AUTH_ERROR_MESSAGE,
					"Authentication Error : User credentials mismatch");
			util.forwardRequest(request, response, AUTH_ERROR_PAGE);
		} catch (ArcAuthenticationServerException e) {
			logger.error(
					"ArcAuthenticationServerException : Error in login process",
					e);
		} catch (Exception e) {
			logger.error("Error in login process", e);
			util.forwardRequest(request, response, ERROR_PAGE);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param memWordString
	 * @throws AccountExpiredException
	 * @throws AccountLockedException
	 * @throws FailedLoginException
	 * @throws ArcAuthenticationServerException
	 * @throws IOException
	 */
	private void doMemWordAuthAndFormatPwd(HttpServletRequest request,
			HttpServletResponse response, String memWordString)
			throws AccountExpiredException, AccountLockedException,
			FailedLoginException, ArcAuthenticationServerException, IOException {
		FtressHelpers70.setConfig(mwConfig);
		String memwordPassphrase;
		memwordPassphrase = (String) request.getSession().getAttribute(
				SEED_POSITIONS)
				+ mwConfig
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM)
				+ memWordString;
		logger.info("Memword passphrase string is : " + memwordPassphrase);
		FtressHelpers70.getInstance().authenticateMemWord(
				(String) request.getSession().getAttribute(USER_ID),
				memwordPassphrase.toCharArray());
		// set config back to password
		FtressHelpers70.setConfig(pwConfig);

		// If the password has not expired then go to JAAS
		// Construct the password in the correct format
		String passphrase = createPassphrase(request.getParameter(PASSWORD),
				memWordString,
				(String) request.getSession().getAttribute(SEED_POSITIONS));
		request.getSession().setAttribute(PASSPHRASE, passphrase);
		response.sendRedirect(request.getContextPath() + homePage);
	}

	/**
	 * Gets the seed positions.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * 
	 * @return the seed positions
	 * 
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String getSeedPositions(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// First check the uid and get the seed positions
		FtressHelpers70.setConfig(mwConfig);
		int[] seedPositions = FtressHelpers70.getInstance()
				.getMemWordSeedPositions(getUserId(request));
		String seedPositionString = "";
		for (int i = 0; i < seedPositions.length; i++) {
			seedPositionString = seedPositionString + seedPositions[i];
			if (i < seedPositions.length - 1) {
				seedPositionString = seedPositionString
						+ mwConfig
								.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM);
			}
		}
		logger.info("seed positions: " + seedPositionString);
		return seedPositionString;
	}

	/**
	 * @param request
	 * @return String - the User Id 
	 */
	private String getUserId(HttpServletRequest request) {
		return request.getParameter(USER_ID);
	}

	/**
	 * Creates the passphrase.
	 * 
	 * @param password
	 *            the password
	 * @param memWordString
	 *            the mem word string
	 * @param seedPositions
	 *            the seed positions
	 * 
	 * @return the string
	 */
	private String createPassphrase(String password, String memWordString,
			String seedPositions) {
		return password
				+ pwConfig.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER)
				+ seedPositions
				+ mwConfig
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM)
				+ memWordString;
	}

	/**
	 * This method returns the allowed values for the characters of a
	 * password/mem word.
	 * 
	 * @return the allowed characters
	 */
	public char[] getAllowedCharacters() {
		char[] allowed = new char[62];
		char x = 'a';
		for (int i = 0; i < 26; i++) {
			allowed[i] = x++;
		}
		x = 'A';
		for (int i = 26; i < 52; i++) {
			allowed[i] = x++;
		}
		int y = 0;
		for (int i = 52; i < 62; i++) {
			allowed[i] = Character.forDigit(y++, 10);
		}
		return allowed;
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean checkNewMemWordAuthenticator(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean returnValue = false;
		String newMemWordAuthType = mwConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_NEWMEMWORD);
		if (newMemWordAuthType != null) {
			try {
				FtressHelpers70.setConfig(mwConfig);

				returnValue = FtressHelpers70.getInstance()
						.isAuthenticationTypeExists(null, newMemWordAuthType,
								getUserId(request));
			} catch (AccountExpiredException e) {
				logger.error("Error in login process: Failed Login", e);
				util.forwardRequest(request, response, ERROR_PAGE);
				returnValue = false;
				e.printStackTrace();
			} catch (FailedLoginException e) {
				logger.error("Error in login process: Failed Login", e);
				util.forwardRequest(request, response, ERROR_PAGE);
				returnValue = false;
				e.printStackTrace();
			}
		}
		return returnValue;
	}

}