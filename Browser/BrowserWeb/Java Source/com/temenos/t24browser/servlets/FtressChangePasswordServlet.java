package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class FtressChangePasswordServlet extends HttpServlet implements Servlet {

	private static final CommonServletUtil util = CommonServletUtil
			.getInstance();
	private static final String ARC_CONFIG_APP_NAME = "ARC_CONFIG_APP_NAME";

	/** The Constant Old Password. */
	public static final String OLD_PASSWORD = "OldPassword";

	/** The Constant NEW_PASSWORD1. */
	public static final String NEW_PASSWORD1 = "NewPassword";

	/** The Constant NEW_PASSWORD2. */
	public static final String NEW_PASSWORD2 = "ConfirmPassword";

	/** User id **/
	public static final String USER_ID = "UserId";

	public static final String CHANGE_SUCCESS_PAGE = "/jsps/change_success.jsp";

	public static final String ERROR_PAGE = "/jsps/authentication_error.jsp";

	public static final String ERROR_400_PAGE = "/jsps/400.jsp";

	public static final String CHANNEL_TYPE = "ChannelType";

	public static final String CHANGE_PASSWORD_PAGE = "/jsps/changePassword.jsp";

	/** Authentication type code */
	private String authCode;

	/** The Constant AUTH TYPE. */
	public static final String AUTH_CODE = "authTypeCode";

	private static Logger logger = LoggerFactory
			.getLogger(FtressLoginServlet.class);

	public FtressChangePasswordServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("Entering doPost Method of FtressChangePasswordServlet");

		boolean isValidSession = request.isRequestedSessionIdValid();
		if (!isValidSession) {
			logger.info("request.isRequestedSessionIdValid() " + isValidSession
					+ " session timed out");
			util.forwardRequest(request, response, ERROR_400_PAGE);
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
		authCode = (String) request.getSession().getAttribute(AUTH_CODE);
		authCode = authCode.substring(0, authCode.indexOf("|"));

		String userId = (String) request.getSession().getAttribute(
				"FTRESS_USER_ID");
		if (userId == null) {
			logger.info("Cannot authenticate User. UserId is null");
			util.forwardRequest(request, response, ERROR_PAGE);
			return;
		} else {
			if (isNotNullParam(request, NEW_PASSWORD1)
					&& isNotNullParam(request, NEW_PASSWORD2)
					&& isNotNullParam(request, OLD_PASSWORD)) {

				if (util.isPasswordValid(request.getParameter(NEW_PASSWORD1),
						request.getParameter(NEW_PASSWORD2))) {
					try {
						boolean isChanged = FtressHelpers70.getInstance()
								.changeOwnPassword(userId,
										request.getParameter(OLD_PASSWORD),
										request.getParameter(NEW_PASSWORD1),
										authCode);
						if (isChanged) {
							logger.info("Password successfullt changed");
							util.forwardRequest(request, response,
									CHANGE_SUCCESS_PAGE);
							return;
						}
					} catch (Exception e) {
						logger.info("Error in changing Password ");
						util.forwardRequest(request, response, ERROR_PAGE);
						return;
					}
				} else {
					logger.info("Password and Confirm Password doen not match");
					util.forwardRequest(request, response, CHANGE_PASSWORD_PAGE);
					return;
				}
			} else {
				logger.info("Password Fields cannot be empty");
				util.forwardRequest(request, response, CHANGE_PASSWORD_PAGE);
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
