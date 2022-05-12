package com.temenos.t24browser.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;

import com.temenos.arc.security.filter.Constant;
import com.temenos.arc.security.filter.SavedRequest;
import com.temenos.arc.security.filter.SavedRequestWrapper;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Servlet implementation class for Servlet: FtressTransactionPinServlet
 * Responsible for the servlet is to handle PIN and CHALLENGE_RESPONSE
 * authentication for Authentication mechanism TOKEN,CHRES
 * 
 * @author karuppiahdas
 * 
 */
public class FtressTransactionOtpServlet extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {

	private static final long serialVersionUID = 68746999938723611L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FtressTransactionOtpServlet.class);
	
	private static final String TRANSACTION_ABORT = "/jsps/transaction_abort.jsp";
	
	private static final String TRANSACTION_PIN = "/jsps/transaction_pin.jsp";
	


	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public FtressTransactionOtpServlet() {
		super();

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");

		String password = request.getParameter("transPassword");
		HttpSession session = request.getSession(true);

		String reqType = request.getParameter("requestType");
		if (reqType != null && reqType.equals("cancel")) {
			clearSessionAttributes(session);
			RequestDispatcher rd = request.getRequestDispatcher(TRANSACTION_ABORT);
			rd.forward(request, response);
			return;
		}
		
		//If entered password value is NULL or empty return with Error.
		if ((password==null) || ("".equals(password))) {
			session.setAttribute("ERR_MSG", "1");
			RequestDispatcher rd = request.getRequestDispatcher(TRANSACTION_PIN);
			rd.forward(request, response);
			return;
		}
		
		String username = (String) session.getAttribute("BrowserSignOnName");

		LOGGER.info(username + " USERNAME");

		boolean OOBAuthenticationSuccess = false;

		String transSignType = (String) session.getAttribute("AUTH_TYPE");
		//If AuthencitationType is SMS, OOBTransactionAuthentication class is called to Authenticate
		//this returns Boolean (interfaces70.DTO.AuthenticationResponse cannot be returned)
		//Existing OTP Authentication is moved to else case
		if (transSignType!= null && transSignType.equalsIgnoreCase("SMS")) {
			try {
				//Remove session attribute before authenticating OOB
				session.removeAttribute(OOBTransactionAuthentication.TRANS_AUTHENTICATOR_LOCKED);
				session.removeAttribute(OOBTransactionAuthentication.TRANS_AUTHENTICATOR_ERROR);
				OOBAuthenticationSuccess = new OOBTransactionAuthentication().doOOBAuthentication(username, password, session);
			
			} catch (ArcAuthenticationServerException e) {
				LOGGER.error("Exception during authentication process"
					+ e.getMessage());
			}
			if (OOBAuthenticationSuccess) {
				SavedRequest oldRequest = getSavedRequest(request);
				SavedRequestWrapper requestWrapper = new SavedRequestWrapper(
						request, oldRequest);
				session.removeAttribute("OOB_ALSI");
				clearSessionAttributes(session);
				RequestDispatcher rd = request
						.getRequestDispatcher("/servlet/BrowserServlet");
				rd.forward(requestWrapper, response);
				return;
			} else {
				String authenticatorLocked = (String) session.getAttribute(OOBTransactionAuthentication.TRANS_AUTHENTICATOR_LOCKED);
				String authenticatorError = (String) session.getAttribute(OOBTransactionAuthentication.TRANS_AUTHENTICATOR_ERROR);
				if (("true".equalsIgnoreCase(authenticatorLocked)) ||("true".equalsIgnoreCase(authenticatorError))) {
					RequestDispatcher rd = request.getRequestDispatcher(session.getAttribute(OOBTransactionAuthentication.TRANS_ERROR_PAGE).toString());
					session.removeAttribute(OOBTransactionAuthentication.TRANS_ERROR_PAGE);
					rd.forward(request, response);
					return;
				} else {
					session.setAttribute("ERR_MSG", "2");
					RequestDispatcher rd = request
						.getRequestDispatcher(TRANSACTION_PIN);
					rd.forward(request, response);
					return;
				}
			}
		} else {
			//OTP, CR Or Sign Authentication
			boolean OTPAuthenticationSuccess = false;
			try {
				//Remove session attribute before authenticating
				session.removeAttribute(TransactionAuthentication.TRANS_AUTHENTICATOR_LOCKED);
				session.removeAttribute(TransactionAuthentication.TRANS_AUTHENTICATOR_ERROR);
				
				OTPAuthenticationSuccess = new TransactionAuthentication()
						.doOTPAuthentication(username, password, session);
				
			} catch (ArcAuthenticationServerException e) {
				LOGGER.error("Exception during authentication process"
						+ e.getMessage());
			} catch (FailedLoginException e) {
				LOGGER.error("Exception during authentication process"
						+ e.getMessage());
			} catch (AccountExpiredException e) {
				LOGGER.error("Exception during authentication process"
						+ e.getMessage());
			}
			
			if (OTPAuthenticationSuccess) {
				SavedRequest oldRequest = getSavedRequest(request);
				SavedRequestWrapper requestWrapper = new SavedRequestWrapper(
						request, oldRequest);
				clearSessionAttributes(session);
				RequestDispatcher rd = request
						.getRequestDispatcher("/servlet/BrowserServlet");
				rd.forward(requestWrapper, response);
				return;
			} else {
				String authenticatorLocked = (String) session.getAttribute(TransactionAuthentication.TRANS_AUTHENTICATOR_LOCKED);
				String authenticatorError = (String) session.getAttribute(TransactionAuthentication.TRANS_AUTHENTICATOR_ERROR);
				if (("true".equalsIgnoreCase(authenticatorLocked)) || ("true".equalsIgnoreCase(authenticatorError))) {
					RequestDispatcher rd = request.getRequestDispatcher(session.getAttribute(TransactionAuthentication.TRANS_ERROR_PAGE).toString());
					session.removeAttribute(TransactionAuthentication.TRANS_ERROR_PAGE);
					rd.forward(request, response);
				} else {
					session.setAttribute("ERR_MSG", "2");
					RequestDispatcher rd = request
							.getRequestDispatcher(TRANSACTION_PIN);
					rd.forward(request, response);
					return;
				}
			}
		}
	}
		
	/*
	 * clearSessionAttributes(HttpSession) Takes HttpSession as the parameter.
	 * Will remove the attributes related to transaction signing.
	 */
	private void clearSessionAttributes(HttpSession httpSession) {
		LOGGER.debug("Clearing transaction sign required attribute");
		httpSession.removeAttribute("transSignRequired");
		httpSession.removeAttribute("CHALLENGE");
		httpSession.removeAttribute("tranSignOverride");
		httpSession.removeAttribute("SIGNCHALLENGE");
		httpSession.removeAttribute(Constant.SAVED_REQUEST_URL);
		httpSession.removeAttribute(Constant.SAVED_REQUEST);
	}

	/*
	 * getSavedRequest(HttpServletRequest) Retrieve the request saved in the
	 * FtressTransactionSigningFilter using session object
	 */
	protected SavedRequest getSavedRequest(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String savedURL = (String) session.getAttribute(Constant.SAVED_REQUEST_URL);
		if (savedURL != null) {
			// this is a request for the request that caused the login,
			// get the SavedRequest from the session
			SavedRequest saved = (SavedRequest) session.getAttribute(Constant.SAVED_REQUEST);
			// remove the saved request info from the session
			session.removeAttribute(Constant.SAVED_REQUEST_URL);
			session.removeAttribute(Constant.SAVED_REQUEST);
			// and return the SavedRequest
			return saved;
		} else {
			return null;
		}
	}
}