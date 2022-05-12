package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class FtressChangePinServlet extends HttpServlet {

	private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	private static final String ARC_CONFIG_APP_NAME = "ARC_CONFIG_APP_NAME";

	/** The Constant Old Password. */
	public static final String OLD_PIN = "OldPassword";

	/** The Constant NEW_PASSWORD1. */
	public static final String NEW_PIN1 = "NewPassword";

	/** The Constant NEW_PASSWORD2. */
	public static final String NEW_PIN2 = "ConfirmPassword";

	/** User id **/
	public static final String USER_ID = "UserId";

	public static final String CHANGE_SUCCESS_PAGE = "/jsps/change_success.jsp";

	public static final String ERROR_PAGE = "/jsps/authentication_error.jsp";

	public static final String ERROR_400_PAGE = "/jsps/400.jsp";

	public static final String CHANGE_PIN_PAGE = "/jsps/changePin.jsp";

	private static Logger logger = LoggerFactory
			.getLogger(FtressChangePinServlet.class);

	private static AuthenticationServerConfiguration deviceConfig;

	private static final CommonServletUtil util = CommonServletUtil
			.getInstance();

	public FtressChangePinServlet() {
		super();
		deviceConfig = util.getConfig(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		logger.info("Entering doPost Method of FtressChangePinServlet");

		response.setContentType("text/html");

		boolean isValidSession = request.isRequestedSessionIdValid();

		if (!isValidSession) {
			logger.info("request.isRequestedSessionIdValid() " + isValidSession
					+ " session timed out");
			util.forwardRequest(request, response, ERROR_400_PAGE);
			return;
		}

		String appName = ARC_CONFIG_APP_NAME;
		if (null == System.getProperty(appName)) {
			String appNameValue = this.getServletConfig().getInitParameter(
					appName);
			if (null != appNameValue) {
				System.setProperty(appName, appNameValue);
			}
			logger.info("app name: " + appNameValue);
		}
		// Getting the username from the userPrincipal object
		HttpSession session = request.getSession(true);
		String userName = (String) session.getAttribute("BrowserSignOnName");
		logger.info(" user Name is " + userName);

		if (userName == null) {
			logger.info("Cannot authenticate User. userName is null");
			util.forwardRequest(request, response, ERROR_PAGE);
			return;
		} else {
			if (isNotNullParam(request, NEW_PIN1)
					&& isNotNullParam(request, NEW_PIN2)
					&& isNotNullParam(request, OLD_PIN)) {
				if (util.isPasswordValid(request.getParameter(NEW_PIN1),
						request.getParameter(NEW_PIN2))) {
					try {
						FtressHelpers70.setConfig(deviceConfig);
						boolean isChanged = FtressHelpers70.getInstance()
								.changeOwnPin(userName,
										request.getParameter(OLD_PIN),
										request.getParameter(NEW_PIN1));
						if (isChanged) {
							logger.info("Pin successfully changed");
							util.forwardRequest(request, response,
									CHANGE_SUCCESS_PAGE);
							return;
						}
					} catch (Exception e) {
						logger.info("Error in changing Pin ");
						util.forwardRequest(request, response, ERROR_PAGE);
						return;
					}
				} else {
					logger.info("Pin and Confirm Pin doen not match");
					request.setAttribute(ERROR_MESSAGE,
							"Pin and Confirm Pin does not match");
					util.forwardRequest(request, response, CHANGE_PIN_PAGE);
					return;
				}
			} else {
				logger.info("Pin Fields cannot be empty");
				request.setAttribute(ERROR_MESSAGE,
						"Pin Fields cannot be empty");
				util.forwardRequest(request, response, CHANGE_PIN_PAGE);
				return;
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
}
