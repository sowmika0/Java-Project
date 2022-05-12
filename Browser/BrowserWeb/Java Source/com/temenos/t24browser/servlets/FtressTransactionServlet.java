package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.filter.Constant;
import com.temenos.arc.security.filter.SavedRequest;
import com.temenos.arc.security.filter.SavedRequestWrapper;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Servlet implementation class for Servlet: FtressTransactionServlet
 * Responsibility of the servlet is to handle the password authentication for
 * Transaction signing authentication mechanism PASSOWORD if the authentication
 * succeeds, Request saved in the Session object will be retrieved and
 * authroised
 * 
 * @author karuppiahdas
 */
public class FtressTransactionServlet extends HttpServlet implements Servlet {

	private static final CommonServletUtil util = CommonServletUtil.getInstance();

	private static final String CHANGE_EXPIRED_PASSWORD_PAGE = "/jsps/change_expired_password.jsp";

	private static final String TXN_PWD_PAGE = "/jsps/transaction_password.jsp";

	private static final String TXN_ABORT_PAGE = "/jsps/transaction_abort.jsp";
	
	public static final String ERROR_503_PAGE = "/modelbank/unprotected/503.jsp";
	
	private static final String CONST_1 = "1";

	private static final String CONST_5 = "5";

	private static final String CONST_2 = "2";

	private static final String SIGN_CHALLENGE = "SIGNCHALLENGE";

	private static final String CHALLENGE = "CHALLENGE";

	private static final String TRAN_SIGN_OVERRIDE = "tranSignOverride";

	private static final String TRANS_SIGN_REQUIRED = "transSignRequired";

	private static final String ERR_MSG = "ERR_MSG";

	private static final String EXPIRED = "EXPIRED";

	private static final String PASS_EXPIRED = "PASS_EXPIRED";

	private static final String OLD_PASSWORD = "OldPassword";

	private static final String CONFIRM_PASSWORD = "ConfirmPassword";

	private static final String NEW_PASSWORD = "NewPassword";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FtressTransactionServlet.class);
	
	private static AuthenticationServerConfiguration authConfig=null;
	public FtressTransactionServlet() {
		super();
		authConfig = util.getConfig(0);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String password = request.getParameter("transPassword");
		HttpSession session = request.getSession(true);
		String username = (String) session.getAttribute("BrowserSignOnName");

		String reqType = request.getParameter("requestType");
		if (reqType != null && reqType.equals("cancel")) {
			clearSessionAttributes(session);
			util.forwardRequest(request, response, TXN_ABORT_PAGE);
			return;
		}
		String status = (String) session.getAttribute(PASS_EXPIRED);
		if (status != null && status.equals(EXPIRED)) {
			if (invokeChangeExpiredPassword(request, response, session,
					username) != 0) {
				return;
			}
		}
		if (isNotNullOrEmpty(password)) {
			LOGGER.info("Authenticate the password and user");
			if (invokePasswordAuthentication(request, response, password,
					session, username) != 0) {
				return;
			}
		} else {
			session.setAttribute(ERR_MSG, CONST_1);
			util.forwardRequest(request, response, TXN_PWD_PAGE);
		}

	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param username
	 * @throws ServletException
	 * @throws IOException
	 */
	private int invokeChangeExpiredPassword(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String username)
			throws ServletException, IOException {
		int returnVal = 0;
		if (isNotNullParam(request, NEW_PASSWORD)
				&& isNotNullParam(request, CONFIRM_PASSWORD)
				&& isNotNullParam(request, OLD_PASSWORD)) {
			if (util.isPasswordValid(
					request.getParameter(NEW_PASSWORD),
					request.getParameter(CONFIRM_PASSWORD))) {
				if (new TransactionAuthentication().changeExpiredPassword(username,
						request.getParameter(OLD_PASSWORD),
						request.getParameter(NEW_PASSWORD))) {
					session.removeAttribute(PASS_EXPIRED);
					session.setAttribute(ERR_MSG, CONST_5);
					util.forwardRequest(request, response, TXN_PWD_PAGE);
					returnVal = 1;
				} else {
					returnVal = forwardToExpiredPasswordJSP(request, response,
							session, CONST_2);
				}
			} else {
				returnVal = forwardToExpiredPasswordJSP(request, response,
						session, CONST_2);
			}
		} else {
			returnVal = forwardToExpiredPasswordJSP(request, response, session,
					CONST_1);
		}
		return returnVal;
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private int forwardToExpiredPasswordJSP(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String errConst)
			throws ServletException, IOException {
		session.setAttribute(ERR_MSG, errConst);
		util.forwardRequest(request, response, CHANGE_EXPIRED_PASSWORD_PAGE);
		return 1;
	}

	/**
	 * @param request
	 * @param response
	 * @param password
	 * @param session
	 * @param username
	 * @return returnVal
	 * @throws IOException
	 * @throws ServletException
	 */
	private int invokePasswordAuthentication(HttpServletRequest request,
			HttpServletResponse response, String password, HttpSession session,
			String username) throws ServletException, IOException {
		int returnVal = 0;
		try {
			GenericAuthenticationResponse authResponse = TransactionAuthentication
					.doPassAuthentication(username, password);
			int responseStatus = authResponse.getResponseStatus();

			if (responseStatus == GenericAuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED) {
				SavedRequest oldRequest = getSavedRequest(request);
				SavedRequestWrapper requestWrapper = new SavedRequestWrapper(
						request, oldRequest);
				clearSessionAttributes(session);
				util.forwardRequest(requestWrapper, response,
						"/servlet/BrowserServlet");
				returnVal = 1;
			}
		} catch (AccountExpiredException e) {
			// set the attribute Password expired
			LOGGER.info("Exception: Password expired");
			session.setAttribute(PASS_EXPIRED, EXPIRED);
			util.forwardRequest(request, response, CHANGE_EXPIRED_PASSWORD_PAGE);
			returnVal = 1;
		} catch (FailedLoginException e) {
			session.setAttribute(ERR_MSG, CONST_2);
			util.forwardRequest(request, response, TXN_PWD_PAGE);
			returnVal = 1;
		} catch (Exception e) {
			LOGGER.info("Exception while authenticating credentials");
			session.setAttribute(ERR_MSG, CONST_1);
			util.forwardRequest(request, response, TXN_PWD_PAGE);
			returnVal = 1;
		}
		return returnVal;
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
	 * Method to check the passed String is not null or empty space
	 * 
	 * @param stringValue
	 * @return boolean
	 */
	private boolean isNotNullOrEmpty(String stringValue) {
		return stringValue != null && !stringValue.trim().equals("");
	}

	/**
	 * Takes HttpSession as the parameter. Will remove the attributes related to
	 * transaction signing
	 * 
	 * @param httpSession
	 */
	private void clearSessionAttributes(HttpSession httpSession) {
		LOGGER.debug("Clearing transaction sign required attribute");
		httpSession.removeAttribute(TRANS_SIGN_REQUIRED);
		httpSession.removeAttribute(TRAN_SIGN_OVERRIDE);
		httpSession.removeAttribute(CHALLENGE);
		httpSession.removeAttribute(SIGN_CHALLENGE);
		httpSession.removeAttribute(Constant.SAVED_REQUEST_URL);
		httpSession.removeAttribute(Constant.SAVED_REQUEST);
	}

	/**
	 * Retrieves the request saved in the FtressTransactionSigningFilter using
	 * session object
	 * 
	 * @param request
	 * @return
	 */
	protected SavedRequest getSavedRequest(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		SavedRequest saved = null;
		if (session.getAttribute(Constant.SAVED_REQUEST_URL) != null) {
			// this is a request for the request that caused the login,
			// get the SavedRequest from the session
			saved = (SavedRequest) session.getAttribute(Constant.SAVED_REQUEST);
			// remove the saved request info from the session
			session.removeAttribute(Constant.SAVED_REQUEST_URL);
			session.removeAttribute(Constant.SAVED_REQUEST);
		}
		return saved;
	}
}