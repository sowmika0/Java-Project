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
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Servlet implementation class for Servlet: FtressLoginServlet This servlet.
 */

public class FtressLoginServlet extends HttpServlet implements Servlet {

	private static final CommonServletUtil util = CommonServletUtil
			.getInstance();

	private static final String ARC_CONFIG_APP_NAME = "ARC_CONFIG_APP_NAME";

	/** The Constant ERROR_PAGE. */
	public static final String ERROR_PAGE = "/modelbank/unprotected/auth_error.jsp";

	public static final String ERROR_400_PAGE = "/modelbank/unprotected/400.jsp";

	public static final String ERROR_503_PAGE = "/modelbank/unprotected/503.jsp";

	/** The Constant CHANGE_PASSWORD_PAGE. */
	public static final String CHANGE_PASSWORD_PAGE = "/modelbank/unprotected/FtresschangePassword.jsp";

	/** The Constant CHANGE_PASSWORD_PAGE. */
	public static final String CHANGE_PIN_PAGE = "/modelbank/unprotected/FtressChangePin.jsp";

	public static final String CHANGE_SUCCESS_PAGE = "/modelbank/unprotected/change_success.jsp";

	/** The Constant USER_ID. */
	public static final String USER_ID = "UserId";

	public static final String CHANNEL = "channel";

	/** The Constant PASSWORD. */
	public static final String PASSWORD = "Password";

	/** The Constant PASSWORD. */
	public static final String PASSWORD_C = "Password_combination";

	/** The Constant Old Password. */
	public static final String OLD_PASSWORD = "OldPassword";

	/** The Constant Old Pin. */
	public static final String OLD_PASSWORD_PIN = "OldPin";

	/** The Constant NEW_PASSWORD1. */
	public static final String NEW_PASSWORD1 = "NewPassword";

	/** The Constant NEW_PASSWORD2. */
	public static final String NEW_PASSWORD2 = "ConfirmPassword";

	/** The Constant NEW_PASSWORD1. */
	public static final String NEW_PASSWORD_PIN1 = "NewPin";

	/** The Constant NEW_PASSWORD2. */
	public static final String NEW_PASSWORD_PIN2 = "ConfirmPin";

	/** The Constant MEMWORD_STRING. */
	public static final String MEMWORD_STRING = "MemWordString";

	/** The Constant otp. */
	public static final String PASSWORD_OTP = "OneTimePassword";

	/** The Constant PASSWORD_OTP in combination with Pin */
	public static final String PASSWORD_OTP_C = "OneTimePassword_combination";

	/** The Constant Pin */
	public static final String PASSWORD_PIN = "Pin";

	/** The Constant channel Type */
	public static final String CHANNEL_TYPE = "ChannelType";

	/** The Constant PASSPHRASE. */
	public static final String PASSPHRASE = "Passphrase";

	/** The Constant AUTH TYPE. */
	public static final String AUTH_CODE = "authTypeCode";

	/** The Constant J_PASSWORD * */
	public static final String j_password = "p_password";

	/** The constant J-username * */
	public static final String j_username = "p_username";

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(FtressLoginServlet.class);

	/** The config. */
	private static AuthenticationServerConfiguration authTypeConfig;

	/** The home page. */
	private static String homePage = "/modelbank/unprotected/Ftressformlogin.jsp";

	/**
	 * The Constants for AuthenticationFailure and Memorable Word locked
	 * messages
	 */
	public static final String AUTH_ERROR_MESSAGE = "ERROR_MESSAGE";
	public static final String AUTHENTICATOR_LOCKED = "AUTHENTICATOR_LOCKED";
	public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";

	/** Authentication type code */
	private String authCode;

	/** Channel type * */
	private String channelType;

	/** Go back to client * */
	private boolean promptUser;

	/** variable hold the state */
	public boolean changed;

	public FtressLoginServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entering doPost...");

		// Checks the session is valid, if not forward the request to the
		// 400.jsp and will not proceed with any validations.
		boolean isValidSession = request.isRequestedSessionIdValid();

		if (!isValidSession) {
			logger.info("request.isRequestedSessionIdValid() " + isValidSession
					+ " session timed out");
			forwardRequest(request, response, ERROR_400_PAGE, false);
			return;
		}

		String appName = ARC_CONFIG_APP_NAME;
		if (null == System.getProperty(appName)) {
			String appNameValue = getServletConfig().getInitParameter(appName);
			if (null != appNameValue) {
				System.setProperty(appName, appNameValue);
			}
			logger.info("app name: " + appNameValue);
		}
		authTypeConfig = util.getConfig(0);

		request.getSession().removeAttribute("USER_ID");
		request.getSession().removeAttribute("PASSPHRASE");

		promptUser = false;
		String passwdCombination1 = null;
		String passwdCombination2 = null;
		checkChannelType(request, response);
		String userId = util.getUserId(request);

		request.getSession().setAttribute("FTRESS_USER_ID", userId);
		if (isNullOrEmpty(userId) || isNullOrEmpty(channelType)) {
			logger.info("Cannot authenticate User. UserId/channel is null");
			forwardRequest(request, response, ERROR_PAGE, false);
			return;
		}
		if (request.getParameter(USER_ID) != null) {
			if (checkNewMemWordAuthenticator(request, response)) {
				forwardRequest(
						request,
						response,
						authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.ACTIVIDENTITY_PAGE),
						false);
				return;
			}
			if (channelType
					.equals(authTypeConfig
							.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_OTPPIN))
					&& promptUser == false) {
				passwdCombination2 = request
						.getParameter(PASSWORD_OTP_C);
				passwdCombination1 = request
						.getParameter(PASSWORD_PIN);
				if (!checkPassword(
						request,
						response,
						passwdCombination1,
						passwdCombination2,
						authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN))) {
					passwdCombination1 = null;
					return;
				}
			}
			if (channelType
					.equals(authTypeConfig
							.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_OTP))) {
				passwdCombination1 = request
						.getParameter(PASSWORD_OTP);
				FtressHelpers70.setConfig(authTypeConfig);
			}
			if (channelType
					.equals(authTypeConfig
							.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PWMW))
					&& promptUser == false) {
				passwdCombination2 = request.getParameter(MEMWORD_STRING);
				passwdCombination1 = request.getParameter(PASSWORD_C);
				if (!checkPassword(
						request,
						response,
						passwdCombination1,
						passwdCombination2,
						authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))) {
					passwdCombination1 = null;
					return;
				}
			}
			if (channelType
					.equals(authTypeConfig
							.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PW))
					&& promptUser == false) {
				passwdCombination1 = request.getParameter(PASSWORD);
				if (!checkPassword(
						request,
						response,
						passwdCombination1,
						passwdCombination2,
						authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))) {
					passwdCombination1 = null;
					return;
				}
			}
		} else {
			if (isNotNullParam(request, NEW_PASSWORD1)
					&& isNotNullParam(request, NEW_PASSWORD2)
					&& isNotNullParam(request, OLD_PASSWORD)) {
				logger.info("Entering change password stage");
				if (isNotNullSessionVar(request, PASSWORD_C)
						|| isNotNullSessionVar(request, PASSWORD)) {
					if (isNotNullSessionVar(request, PASSWORD_C)) {
						passwdCombination1 = (String) request.getSession()
								.getAttribute(MEMWORD_STRING);
					}
					if (checkNewPassword(
							request,
							response,
							request.getParameter(OLD_PASSWORD),
							authTypeConfig
									.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))) {
						forwardRequest(request, response, CHANGE_SUCCESS_PAGE,
								false);
						return;
					}
				} else {
					forwardRequest(request, response, ERROR_PAGE, true);
				}
			} else if (isNotNullParam(request, NEW_PASSWORD_PIN1)
					&& isNotNullParam(request, NEW_PASSWORD_PIN2)
					&& isNotNullParam(request, OLD_PASSWORD_PIN)) {
				passwdCombination1 = (String) request.getSession()
						.getAttribute(PASSWORD_OTP_C);
				if (checkNewPassword(
						request,
						response,
						request.getParameter(OLD_PASSWORD_PIN),
						authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN))) {
					forwardRequest(request, response, CHANGE_SUCCESS_PAGE,
							false);
					return;
				}
			} else {
				forwardRequest(request, response, ERROR_PAGE, true);
			}
		}
		// if the request is already routed to a page, we should not continue
		// further.
		if (this.promptUser) {
			return;
		}

		try {
			logger.info("Attempting to authenticate: user: " + userId);
			doAuthentication(request, response, userId, passwdCombination1,
					passwdCombination2);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("Authentication Failed in FtressLoginServlet.Error message is"
					+ e.getMessage());
			forwardRequest(request, response, ERROR_PAGE, false);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void checkChannelType(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		channelType = null;
		authCode = null;
		if (request.getParameter(USER_ID) != null) {

			this.removeAttributes(request, response);
			if (isNotNullParam(request, PASSWORD_OTP_C)
					&& isNotNullParam(request, PASSWORD_PIN)) {
				channelType = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_OTPPIN);
				request.getSession().setAttribute(PASSWORD_OTP_C,
						request.getParameter(PASSWORD_OTP_C));
				request.getSession().setAttribute(PASSWORD_PIN,
						request.getParameter(PASSWORD_PIN));
				authCode = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN)
						+ "|"
						+ authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE);

			} else if (isNotNullParam(request, PASSWORD_OTP)) {

				channelType = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_OTP);
				request.getSession().setAttribute(PASSWORD_OTP,
						request.getParameter(PASSWORD_OTP));
				authCode = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE)
						+ "|";

			} else if (isNotNullParam(request, PASSWORD_C)
					&& isNotNullParam(request, MEMWORD_STRING)) {

				channelType = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PWMW);
				request.getSession().setAttribute(PASSWORD_C,
						request.getParameter(PASSWORD_C));
				request.getSession().setAttribute(MEMWORD_STRING,
						request.getParameter(MEMWORD_STRING));
				authCode = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD)
						+ "|"
						+ authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD);

			} else if (isNotNullParam(request, PASSWORD)) {
				channelType = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PW);
				request.getSession().setAttribute(PASSWORD,
						request.getParameter(PASSWORD));
				authCode = authTypeConfig
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD)
						+ "|";

			} else {
				// Not a valid combination. Raise error message.
				logger.error("Error in login process : Invalid Combination Entered");
				forwardRequest(request, response, ERROR_PAGE, false);

			}
			request.getSession().setAttribute(CHANNEL_TYPE, channelType);
			request.getSession().setAttribute(AUTH_CODE, authCode);
			request.getSession().setAttribute(USER_ID,
					request.getParameter(USER_ID));

		} else {
			if (request.getSession().getAttribute(CHANNEL_TYPE) == null
					|| request.getSession().getAttribute(CHANNEL_TYPE)
							.equals("")) {
				logger.error("No user Id in the Request");
				forwardRequest(request, response, ERROR_PAGE, false);

			} else {
				channelType = (String) request.getSession().getAttribute(
						CHANNEL_TYPE);
				authCode = (String) request.getSession()
						.getAttribute(AUTH_CODE);
			}
		}
	}

	/**
	 * Method to check the passed request parameter is not null or empty space
	 * 
	 * @param request
	 * @param param
	 * @return boolean
	 */
	private boolean isNotNullParam(HttpServletRequest request, String param) {
		return request.getParameter(param) != null
				&& !request.getParameter(param).equals("");
	}
	
	/**
	 * @param request
	 * @param param
	 * @return
	 */
	private boolean isNotNullSessionVar(HttpServletRequest request, String param) {
		return request.getSession().getAttribute(param) != null
				&& !request.getSession().getAttribute(param).equals("");
	}
	/**
	 * @param request
	 * @param response
	 * @return boolean
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean checkNewMemWordAuthenticator(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean returnValue = false;
		String newMemWordAuthType = authTypeConfig
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_NEWMEMWORD);
		if (newMemWordAuthType != null) {
			try {
				FtressHelpers70.setConfig(authTypeConfig);
				returnValue = FtressHelpers70.getInstance()
						.isAuthenticationTypeExists(null, newMemWordAuthType,
								util.getUserId(request));
			} catch (AccountExpiredException e) {
				logger.error("Error in login process: Account Expired", e);
				forwardRequest(request, response, ERROR_PAGE, false);
				returnValue = false;
				e.printStackTrace();
			} catch (FailedLoginException e) {
				logger.error("Error in login process: Failed Login", e);
				forwardRequest(request, response, ERROR_PAGE, false);
				returnValue = false;
				e.printStackTrace();
			}
		}
		return returnValue;
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
	private boolean checkPassword(HttpServletRequest request,
			HttpServletResponse response, String password, String password1,
			String authType) throws ServletException, IOException {
		boolean retrunVal = true;
		try {
			FtressHelpers70.setConfig(authTypeConfig);
			FtressHelpers70.getInstance().authenticatePassWord(
					(String) request.getSession().getAttribute(USER_ID),
					password.toCharArray(), authType);
		} catch (AccountExpiredException e) {
			logger.info("password expired");
			retrunVal = false;
			try {
				final boolean isAuthTypePassword = authType
						.equals(authTypeConfig
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD));
				String authCodeLocal = authCode.substring(
						authCode.indexOf("|") + 1, authCode.length());

				if (isNotNullOrEmpty(password1)
						&& isNotNullOrEmpty(authCodeLocal)) {
					logger.info("The Authentication Type '" + authType
							+ "' for the user '" + util.getUserId(request)
							+ "'" + " has expired ");
					logger.info("Trying to Autheticate Pin or Memeorable word");
					FtressHelpers70.getInstance()
							.authenticate(
									(String) request.getSession().getAttribute(
											USER_ID), password1.toCharArray(),
									authCodeLocal);
				}
				forwardToJSP(request, response, isAuthTypePassword);
			} catch (Exception e1) {
				throw new ArcAuthenticationServerException(
						"Authentication failed for Password combination 2");
			}
		} catch (AccountLockedException e) {
			logger.error("Error in login process", e);
			request.getSession().setAttribute(AUTHENTICATOR_LOCKED, "true");
			request.getSession().setAttribute(AUTH_ERROR_MESSAGE,
					"User Locked : Error in login process");
			forwardRequest(request, response, ERROR_PAGE, false);
		} catch (FailedLoginException e) {
			logger.error("Error in login process", e);
			forwardRequest(request, response, ERROR_PAGE, true);
			e.printStackTrace();
			retrunVal = false;
		} catch (ArcAuthenticationServerException e) {
			logger.error(
					"ArcAuthenticationServerException : Error in login process",
					e);
			e.printStackTrace();
			retrunVal = false;
		}
		return retrunVal;

	}

	/**
	 * Loads respective jsp page
	 * 
	 * @param request
	 * @param response
	 * @param isAuthTypePassword
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forwardToJSP(HttpServletRequest request,
			HttpServletResponse response, final boolean isAuthTypePassword)
			throws ServletException, IOException {
		if (isAuthTypePassword) {
			forwardRequest(request, response, CHANGE_PASSWORD_PAGE, true);
		} else {
			forwardRequest(request, response, CHANGE_PIN_PAGE, true);
		}
	}

	/**
	 * Check new password. This method is called after entering the credentials
	 * in change XXXXXXX screens . This method will change the old credentials
	 * with the new credentials. This method returns a boolean value . Based on
	 * the Boolean value, the page will be forwarded to the success page.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param oldPassword
	 *            the old Password
	 * @param authType
	 *            the Authentication Type like "AT_CUSTPW"
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean checkNewPassword(HttpServletRequest request,
			HttpServletResponse response, String oldPassword, String authType)
			throws ServletException, IOException {
		changed = false;
		try {
			FtressHelpers70.setConfig(authTypeConfig);
			final boolean isAuthTypePin = authType
					.equals(authTypeConfig
							.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN));
			if (isAuthTypePin
					&& util.isPasswordValid(
							request.getParameter(NEW_PASSWORD_PIN1),
							request.getParameter(NEW_PASSWORD_PIN2))) {
				changePassword(request, response, oldPassword, authType,
						NEW_PASSWORD_PIN1, CHANGE_PIN_PAGE);
			} else if (util.isPasswordValid(
					request.getParameter(NEW_PASSWORD1),
					request.getParameter(NEW_PASSWORD2))) {
				changePassword(request, response, oldPassword, authType,
						NEW_PASSWORD1, CHANGE_PASSWORD_PAGE);
			} else {
				logger.info("new password is invalid, returning to jsp");
				if (isAuthTypePin) {
					forwardRequest(request, response, CHANGE_PIN_PAGE, true);
				} else {
					forwardRequest(request, response, CHANGE_PASSWORD_PAGE,
							true);
				}
			}
		} catch (Exception e) {
			logger.error("Error in changing password", e);
			forwardRequest(request, response, ERROR_PAGE, false);
		}
		return changed;
	}

	/**
	 * Invokes FtressHelpers70 changeOwnExpiredPassword method and stores the
	 * result
	 * 
	 * @param request
	 * @param response
	 * @param oldPassword
	 * @param authType
	 * @throws ServletException
	 * @throws IOException
	 */
	private void changePassword(HttpServletRequest request,
			HttpServletResponse response, String oldPassword, String authType,
			String newPassword, String errorPage) throws ServletException,
			IOException {
		if (!(changed = FtressHelpers70.getInstance().changeOwnExpiredPassword(
				util.getUserId(request), oldPassword,
				request.getParameter(newPassword), authType))) {
			logger.info("change password failed, returning to jsp");
			forwardRequest(request, response, errorPage, false);
		}
	}

	/**
	 * Method which forwards the request to given response page
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forwardRequest(HttpServletRequest request,
			HttpServletResponse response, String page, boolean isPromptUser)
			throws ServletException, IOException {
		if (isPromptUser) {
			promptUser = true;
		}
		request.getRequestDispatcher(page).forward(request, response);
	}

	/**
	 * This method does the actual authentication of the user in ftress, and
	 * creates alsi from the authentication response. Both the passwords entered
	 * will be authenticated here.
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 * @param passwdPair1
	 * @param passwdPair2
	 * @throws ServletException
	 * @throws IOException
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 */
	private void doAuthentication(HttpServletRequest request,
			HttpServletResponse response, String userId, String passwdPair1,
			String passwdPair2) throws ServletException, IOException,
			ArcAuthenticationServerException, FailedLoginException,
			AccountExpiredException {

		GenericAuthenticationResponse genericResponse1 = null;
		GenericAuthenticationResponse genericResponse2 = null;
		String alsi2 = null;
		String alsi1 = null;

		String authCode1 = authCode.substring(0, authCode.indexOf("|"));
		String authCode2 = authCode.substring(authCode.indexOf("|") + 1,
				authCode.length());
		// when both the password field is null show error page
		if (isNullOrEmpty(passwdPair1) && isNullOrEmpty(passwdPair2)) {
			throw new ArcAuthenticationServerException(
					"Password entered is incorrect");
		}
		// when both the authentication type is null the show error page
		if (isNullOrEmpty(authCode1) && isNullOrEmpty(authCode2)) {
			throw new ArcAuthenticationServerException(
					"Authentication code is not valid");
		}
		if (isNotNullOrEmpty(authCode1) && isNotNullOrEmpty(passwdPair1)) {
			logger.info("Now authenticating the first password ");
			genericResponse1 = FtressHelpers70.getInstance().authenticate(
					userId, passwdPair1.toCharArray(), authCode1);
			alsi1 = genericResponse1.getSession();
		}
		if (isNotNullOrEmpty(authCode2) && isNotNullOrEmpty(passwdPair2)) {
			if (isNotNullOrEmpty(alsi1)) {
				logger.info("Now authenticating the second password");
				authCode2 = authCode2.substring(0, authCode2.length());
				genericResponse2 = FtressHelpers70.getInstance().authenticate(
						userId, passwdPair2.toCharArray(), authCode2);

				if (genericResponse2 != null && !genericResponse2.equals("")) {
					alsi2 = genericResponse2.getSession();
					logger.info("Authentication Successfull for the second password ");
				}
			} else {
				throw new ArcAuthenticationServerException(
						"Authentication failed for the first part.");
			}
		} else {
			logger.info("Getting ALSI from the authentication response ");
			if (isNotNullOrEmpty(alsi1)) {
				alsi2 = alsi1;
				logger.info("Getting alsi from the response.");
			}
		}

		if (isNotNullOrEmpty(alsi2)) {
			logger.info("Authentication Successfull");
			logger.info("ALSI is valid in response ");
		} else {
			throw new ArcAuthenticationServerException("Return alsi is Null.");
		}
		// pass the return alsi to Login module
		request.getSession().setAttribute(j_password, alsi2);
		request.getSession().setAttribute(CHANNEL, channelType);
		request.getSession().setAttribute(j_username, util.getUserId(request));
		// At this stage Authentication is successfull, hence attributes can be
		// removed from request.
		request.getSession().setAttribute(PASSPHRASE, alsi2);
		request.getSession().setAttribute(USER_ID, userId);
		this.removeAttributes(request, response);
		response.addHeader("Content-Type", "text/html");
		response.sendRedirect(request.getContextPath() + homePage);
	}

	/**
	 * Method to check the passed String is not null or empty space
	 * 
	 * @param stringValue
	 * @return boolean
	 */
	private boolean isNotNullOrEmpty(String stringValue) {
		return stringValue != null && !stringValue.trim().equals("");
	}

	/**
	 * Method to check the passed String is null or empty
	 * 
	 * @param stringValue
	 * @return boolean
	 */
	private boolean isNullOrEmpty(String stringValue) {
		return (stringValue == null || stringValue.equals(""));
	}

	/**
	 * This method removes the attributes from request.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void removeAttributes(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute(PASSWORD);
		request.getSession().removeAttribute(NEW_PASSWORD1);
		request.getSession().removeAttribute(NEW_PASSWORD2);
		request.getSession().removeAttribute(NEW_PASSWORD_PIN1);
		request.getSession().removeAttribute(NEW_PASSWORD_PIN2);
		request.getSession().removeAttribute(MEMWORD_STRING);
		request.getSession().removeAttribute(PASSWORD_OTP);
		request.getSession().removeAttribute(PASSWORD_PIN);
	}

}
