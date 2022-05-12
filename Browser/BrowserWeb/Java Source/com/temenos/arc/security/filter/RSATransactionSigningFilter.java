package com.temenos.arc.security.filter;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rsa.authn.LoginCommand;
import com.rsa.authn.data.AbstractParameterDTO;
import com.rsa.authn.data.FieldParameterDTO;
import com.rsa.command.CommandException;
import com.rsa.command.CommandTarget;
import com.rsa.command.CommandTargetPolicy;
import com.rsa.command.ConnectionFactory;
import com.rsa.common.SystemException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.exceptions.GenericAuthenticationException;
import com.temenos.t24browser.servlets.VersionsEnquiriesFilter;

/**
 * RSATransactionSigningFilter class
 * Responsible for indentifying the request which needs Transaction Signing
 * Initiate the corresponding Transaction Signing process
 * 
 * @Author Sara (6363)
 */
public class RSATransactionSigningFilter implements Filter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RSATransactionSigningFilter.class);

	public void destroy() {
	}

	/**
	 * Handles the saving the request on unauntenticated user
	 * Authenticate user based on the auth type
	 * Redirects to the repective method to perform sms or token authentication
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
		
		if (!(servletRequest instanceof HttpServletRequest)) {
			LOGGER.error("Must be an HttpServletRequest");
			throw new ServletException("Must be an HttpServletRequest");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		if (!(servletResponse instanceof HttpServletResponse)) {
			LOGGER.error("Must be an HttpServletResponse");
			throw new ServletException("Must be an HttpServletResponse");
		}
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

		HttpSession httpSession = httpRequest.getSession(true);
		
		String rsaCancelRequest = httpRequest.getParameter("rsacancel");
		String reqType = httpRequest.getParameter("requestType");
		String currentStep = (String) httpSession.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP);
		if((rsaCancelRequest!=null && rsaCancelRequest.equalsIgnoreCase("yes")) || (reqType!=null && reqType.equalsIgnoreCase("destroy.session") && currentStep!=null)){
			clearSessionAttributes(httpSession);
			httpSession.setAttribute("CUSTOMMSG", "Your request has been cancelled sucessfully");
			throw new GenericAuthenticationException();
		}

		String transSignRequried = (String)httpSession.getAttribute("transSignRequired");

		if(!"Yes".equalsIgnoreCase(transSignRequried)){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		LOGGER.debug("Getting user id from session");
		String ivuser = (String) httpSession.getAttribute("BrowserSignOnName");
		
		String rsaType = httpRequest.getParameter("transSign");
		LOGGER.debug("RSA Authentication Type:" + rsaType);
		
		httpSession.setAttribute(Constant.SESSION_ATTR_USERNAME, ivuser);
		LOGGER.debug("Current USER: " + ivuser);
		String rsaTypeSession = (String)httpSession.getAttribute(Constant.SESSION_ATTR_AUTH_RSA_TYPE);
		if(rsaTypeSession!=null){
			rsaType = rsaTypeSession;
		}else{
			if (rsaType != null && rsaType.equalsIgnoreCase(Constant.SESSION_ATTR_AUTH_RSA_TOKEN))
				rsaType = Constant.SESSION_ATTR_AUTH_RSA_TOKEN;
			else if(rsaType != null && rsaType.equalsIgnoreCase(Constant.SESSION_ATTR_AUTH_RSA_SMS))
				rsaType = Constant.SESSION_ATTR_AUTH_RSA_SMS;
			else 
				rsaType = Constant.SESSION_ATTR_AUTH_RSA_TOKEN;	
		}
		LOGGER.debug("### Current step: " + currentStep);
		LOGGER.debug("### RSAtype (if applicable): " + rsaType);
		try {
			// New user comes in
			if(rsaType!=null && currentStep == null){
				
				// Save the request. It will be used again when the user successfully gets authenticated.
				SavedRequest savedRequest = new SavedRequest(httpRequest);
				httpSession.setAttribute(Constant.SAVED_REQUEST, savedRequest);
				this.saveRequestInformation(httpRequest);
	
				// Redirect to the rsaAuthentication.jsp page
				LOGGER.debug("Save rsatype and current step in session");
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, rsaType);
				httpSession.setAttribute(Constant.SESSION_ATTR_AUTH_RSA_TYPE,rsaType);
				LOGGER.debug(httpRequest.getContextPath() + "/jsps/"+ Constant.RSA_AUTH_JSP);
				httpResponse.sendRedirect(httpResponse.encodeRedirectURL(httpRequest.getContextPath() + "/jsps/"+ Constant.RSA_AUTH_JSP));
				return;
			// SecureID Authentication 
			}else if(currentStep.equalsIgnoreCase(Constant.SESSION_ATTR_AUTH_RSA_TOKEN)){
				LOGGER.debug("do Token authentication");
				doTokenAuthentication(filterChain, httpRequest, httpResponse, httpSession);
				return;
			}
			// OnDemand Authentication
			else if (currentStep.equals(Constant.SESSION_ATTR_AUTH_RSA_SMS)) {
				LOGGER.debug("do sms authentication");
				doSMSAuthentication(filterChain, httpRequest, httpResponse, httpSession);
				return;
			}
			//New PIN for OnDemand Authentication
			else if (currentStep.equals(Constant.STEP_RSA_SMS_NEWPIN)) {
				LOGGER.debug("Do sms pin change");
				String strPin = httpRequest.getParameter(Constant.PARAM_RSA_NEWPIN); // pin from request
				if(strPin !=null)
					LOGGER.debug("New PIN: "+ strPin);
					
				//Get RSA objects from session
				RSAAuthentication auth = (RSAAuthentication) httpSession.getAttribute("authObject");
				LOGGER.debug("Got authentication object and going to perform the PIN change");
				FieldParameterDTO currentField = auth.doNewPIN(strPin,"");
				LOGGER.debug("Save the current field in session");
				httpSession.setAttribute("currentField", currentField);
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP,Constant.STEP_RSA_SMS_TOKEN);
				//Forward to page to change the pin
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_NEWPIN_JSP);
				return;
			//New PIN for OnDemand Authentication
			}else if (currentStep.equals(Constant.STEP_RSA_SMS_TOKEN)){
				LOGGER.debug("do sms token");
				//Get the SMS Token from request
				String smsToken = httpRequest.getParameter(Constant.PARAM_RSA_SMS_TOKEN); // SMS TOKEN from request
				RSAAuthentication auth = (RSAAuthentication) httpSession.getAttribute("authObject");
				
				// Get RSA objects from session
				LOGGER.debug("Getting RSA objects from session");
				FieldParameterDTO currentField = (FieldParameterDTO) httpSession.getAttribute("currentField");
				AuthenticatedTarget currentSession = (AuthenticatedTarget)auth.getSession();
				LoginCommand currentLogin = (LoginCommand)auth.getLoginCommand();
				
				LOGGER.debug("Current Field" +currentField.getPromptKey());
				if (smsToken != null) {
					LOGGER.debug("SMS TOKEN just recieved: " +smsToken);
					currentField.setValue(smsToken); // cannot be null
					// Attempt to execute logon and check if success
					currentLogin.execute(currentSession);
					LOGGER.debug("# currentLogin.checkAuthenticatedState: " + currentLogin.checkAuthenticatedState());

					if (currentLogin.checkAuthenticatedState()) {
						//Authentication successful restore to original request
						restoreOriginalRequest(filterChain, httpRequest, httpResponse, httpSession);
						return;
					}else{
						// Invalid SMS Token display error msg and fwd to RSAAuthentication.jsp
						LOGGER.error("Invalid SMS Token: ["+ smsToken +"]");
						httpRequest.setAttribute("error","Invalid SMS Token: [" + smsToken + "]");
						httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, rsaType);
						forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
					}
				}
			}else if (currentStep.equals(Constant.STEP_RSA_SECUREID_NEWPIN_NEXTTOKEN)){
				LOGGER.debug("do secureID PIN change");
				String strPin = httpRequest.getParameter(Constant.PARAM_RSA_NEWPIN); // PIN from request
				String strNextToken = httpRequest.getParameter(Constant.PARAM_RSA_NEXT_TOKEN); // NextToken from request
				if(strPin !=null && strNextToken !=null){
					LOGGER.debug("New pin"+ strPin);
					LOGGER.debug("Next Token"+ strNextToken);
				}
					
				//Get RSA Authentication object from session
				RSAAuthentication auth = (RSAAuthentication) httpSession.getAttribute("authObject");
				LOGGER.debug("Got authentication object and going to perform the PIN change");		
				auth.doNewPIN(strPin,strNextToken);
				LoginCommand loginCommand = auth.getLoginCommand();
				if(loginCommand.checkAuthenticatedState()){
					//Authentication successful restore to original request
					restoreOriginalRequest(filterChain, httpRequest, httpResponse, httpSession);
				}else {
					// invalid SMS code display error msg and fwd
					LOGGER.error("Invalid NextToken: ["+ strNextToken +"]");
					httpRequest.setAttribute("error","Invalid NextToken or PIN reused: ["+ strNextToken +"]");
					httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, rsaType);
					forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
				}
				return;
			}else if (currentStep.equals(Constant.PARAM_RSA_NEXT_TOKEN)){
				LOGGER.debug("do next token");
				String strNextToken = httpRequest.getParameter(Constant.PARAM_RSA_NEXT_TOKEN); // NextToken from request
				if(strNextToken !=null)
					LOGGER.debug("strNextToken -- "+ strNextToken);
				
				//Get RSA Authentication object from session
				RSAAuthentication auth = (RSAAuthentication) httpSession.getAttribute("authObject");
				
				if(auth.doNextToken(strNextToken)){
					//Authentication successful restore to original request
					restoreOriginalRequest(filterChain, httpRequest, httpResponse, httpSession);
				}else{
					// Invalid NextToken code display error msg and fwd to RSAAuthentication.jsp
					LOGGER.error("Invalid NextToken: ["+ strNextToken +"]");
					httpRequest.setAttribute("error","Invalid NextToken: [" + strNextToken + "]");
					httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, rsaType);
					forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
				}
				return;
			}else {
				// Cannot determine type of auth or step
				LOGGER.error("Cannot determine step, starting over with authentication"); 
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, rsaType);
				forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
				return;
			}
		} catch (CommandException e) {
			LOGGER.error("Command Exception in RSA TransactionSigning Filter");
			clearSessionAttributes(httpSession);
			throw new ArcAuthenticationServerException();
		} catch (SystemException e) {
			LOGGER.error("System Exception in RSA TransactionSigning Filter");
			clearSessionAttributes(httpSession);
			throw new ArcAuthenticationServerException();
		}

	}

	/**
	 * To perform SMS authentication with userID and PIN
	 * Once recieved the token through SMS, authenticates using that as well
	 * Checks for new pin requried
	 * @param filterChain
	 * @param httpRequest
	 * @param httpResponse
	 * @param httpSession
	 * @throws IOException
	 * @throws ServletException
	 * @throws CommandException
	 * @throws SystemException
	 */
	private void doSMSAuthentication(FilterChain filterChain, HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession httpSession) throws IOException, ServletException, CommandException, SystemException {
		String user = (String) httpSession.getAttribute(Constant.SESSION_ATTR_USERNAME); // username in session
		String pin = httpRequest.getParameter(Constant.PARAM_RSA_PIN); // pin from request
		
		LOGGER.debug("Going to establish the connection with RSA server");
		CommandTarget target = ConnectionFactory.getTarget();
		LOGGER.debug("Created the connection Object");
		CommandTargetPolicy.setDefaultCommandTarget(target);
		LOGGER.debug("Set the command target Poilcy");

		// Create auth object
		RSAAuthentication auth = new RSAAuthentication();
		LOGGER.debug("Created the RSA Authentication class object");

		// Authenticate with PIN
		if (user == null || pin == null) {
			LOGGER.debug("@@@ Username: " + user);
			LOGGER.debug("@@@ pin: " + pin);
			//PIN is null
			LOGGER.error("PIN is null");
			httpRequest.setAttribute("error","Enter value for PIN or Cancel the request");
			forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_AUTH_JSP);
		}else{

			FieldParameterDTO currentField = auth.doAuthentication(target, user, pin,Constant.SESSION_ATTR_AUTH_RSA_SMS);
			LoginCommand loginCommand = auth.getLoginCommand();
	
			// If NEW PIN is required
			if (auth.getNewPinRequired()) {
				LOGGER.debug("store authObject in session");
				httpSession.setAttribute("authObject", auth);
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP,Constant.STEP_RSA_SMS_NEWPIN);
				//Forward to NewPIN Change jsp page
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_NEWPIN_JSP);
				return;
			}
			else if (currentField != null && currentField.getPromptKey().equalsIgnoreCase("Tokencode")) {
				LOGGER.debug("store authObject in session");
				httpSession.setAttribute("authObject", auth);
				LOGGER.debug("store currentfield in session");
				httpSession.setAttribute("currentField",currentField);
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP,Constant.STEP_RSA_SMS_TOKEN);
				// Pass back to RSAAuthentication.jsp to get tokencode from user
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_AUTH_JSP);
			}
			// Bad user name and pin
			else if (!loginCommand.checkAuthenticatedState()|| loginCommand.checkFailedState()) {
				LOGGER.error("SMS Authentication failed, Invalid RSA username or PIN");
				httpRequest.setAttribute("error","Invalid RSA username or PIN.");
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_AUTH_JSP);
			}
		}
	}
     
	/**
	 * To perform the Token Authentication, with userid and passcode
	 * Checks for new pin requried
	 * Checks for Next Token requried
	 * @param filterChain
	 * @param httpRequest
	 * @param httpResponse
	 * @param httpSession
	 * @throws CommandException
	 * @throws SystemException
	 * @throws IOException
	 * @throws ServletException
	 */
	private void doTokenAuthentication(FilterChain filterChain, HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession httpSession) throws CommandException, SystemException, IOException, ServletException {
		String user = (String) httpSession.getAttribute(Constant.SESSION_ATTR_USERNAME); // username in session
		String passcode = httpRequest.getParameter(Constant.PARAM_RSA_PASSCODE); // passcode from request
		
		LOGGER.debug("Going to establish the connection with RSA server");
		CommandTarget target = ConnectionFactory.getTarget();
		LOGGER.debug("Created the connection Object");
		CommandTargetPolicy.setDefaultCommandTarget(target);
		LOGGER.debug("Set the command target Poilcy");
		// Create auth object
		RSAAuthentication auth = new RSAAuthentication();
		LOGGER.debug("Created the RSA Authentication class object");

		// Authenticate with Passcode
		if (user == null || passcode == null) {
			LOGGER.debug("@@@ Username: " + user);
			LOGGER.debug("@@@ passcode: " + passcode);
			// Passcode is null
			LOGGER.error("Passcode is null");
			httpRequest.setAttribute("error","Enter value for Passcode or Cancel the request");
			forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
		}else{
			auth.doAuthentication(target, user, passcode,Constant.SESSION_ATTR_AUTH_RSA_TOKEN);
			// Get the current logincmd
			LoginCommand loginCommand = auth.getLoginCommand();
			// if NEW PIN is required
			if (auth.getNewPinRequired()) {
				httpSession.setAttribute("authObject", auth);
				httpSession.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP,Constant.STEP_RSA_SECUREID_NEWPIN_NEXTTOKEN);
				//Forward to NewPIN Change jsp page
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_NEWPIN_JSP);
			//if Next Token required
			}else if(auth.getNextTokenRequired()){
				httpSession.setAttribute("authObject", auth);
				//Forward to NextToken jsp page
				forward(httpRequest, httpResponse, "/jsps/"+ Constant.RSA_NEXTTOKEN_JSP);
			//Authentication successful restore to original request
			}else if(loginCommand.checkAuthenticatedState()){
				restoreOriginalRequest(filterChain, httpRequest, httpResponse, httpSession);
			}else {
				// Invalid Passcode code display error msg and fwd
				LOGGER.error("Token Authentication failed, Invalid Passcode: ["+ passcode + "]");
				httpRequest.setAttribute("error","Invalid RSA username or Passcode");
				forward(httpRequest, httpResponse, "/jsps/" + Constant.RSA_AUTH_JSP);
			}
		}
	}

	/**
	 * Restores the saved request from the session
	 * @param filterChain
	 * @param httpRequest
	 * @param httpResponse
	 * @param httpSession
	 * @throws IOException
	 * @throws ServletException
	 */
	private void restoreOriginalRequest(FilterChain filterChain, HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession httpSession) throws IOException, ServletException {
		// Successful authentication
		// Now put the original request back in place and allow the request to be submitted.
		SavedRequest savedRequest = getSavedRequest(httpRequest);
		SavedRequestWrapper wrappedRequest = new SavedRequestWrapper(httpRequest, savedRequest);
		// Clear session attributes and redirect to login page
		clearSessionAttributes(httpSession);
		LOGGER.debug("Continue down the filter chain to the original request.");
		filterChain.doFilter(wrappedRequest, httpResponse);
		return;
	}

	/**
	 * Clears the RSA session variables
	 * @param httpSession
	 */
	private void clearSessionAttributes(HttpSession httpSession) {
		LOGGER.debug("Clearing all RSA session information");
		httpSession.removeAttribute("currentField");
		httpSession.removeAttribute("currentRSASession");
		httpSession.removeAttribute("loginCommand");
		httpSession.removeAttribute("receivedToken");
		httpSession.removeAttribute(Constant.SESSION_ATTR_AUTH_RSA_SMS);
		httpSession.removeAttribute(Constant.SESSION_ATTR_CURRENT_STEP);
		httpSession.removeAttribute(Constant.SESSION_ATTR_AUTH_RSA_TOKEN);
		httpSession.removeAttribute("currentField");
		httpSession.removeAttribute(Constant.PARAM_RSA_NEWPIN);
		httpSession.removeAttribute("authObject");
		httpSession.removeAttribute("error");
		httpSession.removeAttribute("transSignRequired");
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * Method to forward to current session Requires a path specified
	 * 
	 * @param _req HTTPRequest
	 * @param _resp HTTPResponse
	 * @param context Servlet Context
	 * @param path URL Path
	 */
	public static void forward(HttpServletRequest _req,HttpServletResponse _resp, String path) {
		RequestDispatcher rd = _req.getRequestDispatcher(path);

		try {
			rd.forward(_req, _resp);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If this request matches the one we saved, return the SavedRequest and
	 * remove it from the session.
	 * 
	 * @param request
	 *            the current request
	 * @return usually null, but when the request matches the posted URL that
	 *         initiated the login sequence a SavedRequest object is returned.
	 */
	protected SavedRequest getSavedRequest(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String savedURL = (String) session.getAttribute(Constant.SAVED_REQUEST_URL);
		if (savedURL != null && savedURL.equals(getSaveableURL(request))) {
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

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// The following methods are provided as static utilities for use by
	// SecurityFilter and other classes. //
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the URL to continue to after successful login. This may be the
	 * SAVED_REQUEST_URL if the authorization sequence was initiated by the
	 * filter, or the default URL (as specified in the config file) if a login
	 * request was spontaneously submitted.
	 * 
	 * @param request
	 *            the current request
	 */
	public static String getContinueToURL(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(Constant.SAVED_REQUEST_URL);
	}

	/**
	 * Save request information to re-use when the user is successfully
	 * authenticated.
	 * 
	 * @param request
	 *            the current request
	 */
	public void saveRequestInformation(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute(Constant.SAVED_REQUEST_URL, getSaveableURL(request));
		session.setAttribute(Constant.SAVED_REQUEST, new SavedRequest(request));
	}

	/**
	 * Return a URL suitable for saving or matching against a saved URL.
	 * <p>
	 * 
	 * This is the whole URL, plus the query string.
	 * 
	 * @param request
	 *            the request to construct a saveable URL for
	 */
	private String getSaveableURL(HttpServletRequest request) {
		StringBuffer saveableURL = null;
		try {
			saveableURL = request.getRequestURL();
		} catch (NoSuchMethodError e) {
			saveableURL = getRequestURL(request);
		}
		// fix the protocol
		fixProtocol(saveableURL, request);
		// add the query string, if any
		String queryString = request.getQueryString();
		if (queryString != null) {
			saveableURL.append("?" + queryString);
		}
		return saveableURL.toString();
	}

	/**
	 * Get the requestURL. This method is called when the app server fails to
	 * implement HttpServletRequest.getRequestURL(). Orion 1.5.2 is one such
	 * server.
	 */
	private StringBuffer getRequestURL(HttpServletRequest request) {
		String protocol = request.getProtocol();
		int port = request.getServerPort();
		String portString = ":" + port;

		// todo: this needs to be tested to see if it still an issue; remove it
		// if it is not needed
		// Set the portString to the empty string if the requrest came in on the
		// default port.
		// This will keep Netscape from dropping the session, which happens when
		// the port is added where it wasn't before.
		// This is not perfect, but most requests on the default ports will not
		// be made with an explicit port number.
		if (protocol.equals("HTTP/1.1")) {
			if (!request.isSecure()) {
				if (port == 80) {
					portString = "";
				}
			} else {
				if (port == 443) {
					portString = "";
				}
			}
		}

		// construct the saveable URL string
		return new StringBuffer(protocol + request.getServerName() + portString + request.getRequestURI());
	}

	/**
	 * Fix the protocol portion of an absolute url. Often, the protocol will be
	 * http: even for https: requests.
	 * 
	 * todo: needs testing to make sure this is proper in all circumstances
	 * 
	 * @param url
	 * @param request
	 */
	private void fixProtocol(StringBuffer url, HttpServletRequest request) {
		// fix protocol, if needed (since HTTP is the same regardless of whether
		// it runs on TCP or on SSL/TCP)
		if (request.getProtocol().equals("HTTP/1.1") && request.isSecure()
				&& url.toString().startsWith("http://")) {
			url.replace(0, 4, "https");
		}
	}

}
