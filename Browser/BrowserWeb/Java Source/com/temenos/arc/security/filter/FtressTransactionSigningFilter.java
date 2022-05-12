package com.temenos.arc.security.filter;

import java.io.IOException;
import java.util.Set;
import java.security.Principal;
import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

import com.temenos.t24browser.servlets.OOBTransactionAuthentication;
import com.temenos.t24browser.servlets.TransactionAuthentication;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.jaas.ArcUserPrincipal;

/**
 * FtressTransactionSigningFilter
 * This class responsoble for handling Transaction signing request.
 * Transaction signing request will be saved in the session object.
 * This class will identify the authentication type and controls the navigation. 
 * 
 * @author karuppiahdas
 */

public class FtressTransactionSigningFilter implements Filter{

	private static final Logger LOGGER = LoggerFactory.getLogger(FtressTransactionSigningFilter.class);
	
	private static String TRANSACTION_PASSWORD = "/jsps/transaction_password.jsp";
	
	private static String TRANSACTION_PIN = "/jsps/transaction_pin.jsp";
		
	private static String OOB_OTP_ERROR = "/jsps/transaction_abort.jsp";
	
	/*
	 * Lifecycle method of Filter 
	 * Called by the web container to indicate to a filter that it is being placed into service.
	 */
	
	public void init(FilterConfig config){
		
	}
	
	/*
	 * doFilter()
	 * The doFilter method of the Filter is called by the container each time a request/response pair is passed through the 
	 * chain due to a client request for a resource at the end of the chain.
	 * Responsibilty of the filter:
	 *   Identify the Authentication Type(TOKEN,SMS,PASSWORD,CHRES)
	 *   Navigate the control to the respective jsp pages
	 *   Saving the request in session object before navigation
	 *   For CHRES authentication type, CHALLENGE will be genrated and stored in the session object.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)throws ServletException,IOException{
		LOGGER.info("Entering  FtressTransactionSigningFilter ");
		if(!(request instanceof HttpServletRequest)){
	    	LOGGER.info("Only HttpServletRequest are supported");
	    }
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(true);
				
		LOGGER.info(" Get the TranssignRequired attribute  value ");
		String transactionSigning = (String)session.getAttribute("transSignRequired");
		//transSign Value is being read from the session instead of request parameter
		String transSignType = (String)session.getAttribute("transSign");
		String tranSignOverride = (String)session.getAttribute("tranSignOverride");
		//Remove the transSignOverride attribute from session immediately after retrieving it
		session.removeAttribute("tranSignOverride");
		
		//if transSignOverride is not Null and user level transSignType <> Override trans sign
		if ((tranSignOverride != null) && (!transSignType.equalsIgnoreCase(tranSignOverride))) {
			transSignType = tranSignOverride;
		}
		session.setAttribute("AUTH_TYPE",transSignType);
		session.removeAttribute("OOB_SEND_FAILURE");
		LOGGER.info(" Transaction signing mechanism : "+transSignType);
					
		if((transactionSigning != null) && ("yes".equalsIgnoreCase(transactionSigning))){
		    LOGGER.info(" Saving the request");
			SavedRequest savedRequest = new SavedRequest(httpRequest);
			session.setAttribute(Constant.SAVED_REQUEST, savedRequest);
			this.saveRequestInformation(httpRequest);
			if(transSignType!= null && transSignType.equalsIgnoreCase("PASSWORD")){ 
			 LOGGER.info("Transaction signing required");
			 RequestDispatcher rd = httpRequest.getRequestDispatcher(TRANSACTION_PASSWORD);
			 rd.forward(request,response);
			 return;
			}
			if(transSignType!= null && transSignType.equalsIgnoreCase("TOKEN")){
				LOGGER.info("Transaction signing required");
				RequestDispatcher rd = httpRequest.getRequestDispatcher(TRANSACTION_PIN);
				rd.forward(request,response);
				return;
			}
            if(transSignType!= null && transSignType.equalsIgnoreCase("CHRES")){
            	LOGGER.info("Transaction signing required");
            	String username = (String) session.getAttribute("BrowserSignOnName");
            	String challenge;
            	challenge = ((String)session.getAttribute("CHALLENGE"));
            	//If CHALLENGE attribute doesn't exist in session then extract by making a call to 4TRESS Server
            	//If CHALLENGE exists in session then it means value is configured under transMessage node under transSignFilter
            	if (challenge == null || challenge =="") {
            		challenge = new TransactionAuthentication().getAuthenticationChallenge(username);
                	session.setAttribute("CHALLENGE", challenge);
            	}
				RequestDispatcher rd = httpRequest.getRequestDispatcher(TRANSACTION_PIN);
				rd.forward(request,response);
				return;
				
			}
            
            if (transSignType != null && transSignType.equalsIgnoreCase("SMS")) {
            	LOGGER.info("Transaction signing required");
            	String username = (String) session.getAttribute("BrowserSignOnName");
            	
            	//Get ALSI for http request object
            	ArcUserPrincipal arcUserPrincipal;
            	
            	Principal principal = httpRequest.getUserPrincipal();
            	if ((principal == null) || (!(principal instanceof ArcUserPrincipal))) {
            		LOGGER.error("Error: principal is not an instance of ArcUserPrincipal");
                    throw new ServletException("Failed to complete authentication");
                }
            	arcUserPrincipal = (ArcUserPrincipal) principal;
            	ArcSession arcSession = getArcSession(arcUserPrincipal, httpRequest.getSession());
            	
            	String currentAlsi = (String)arcSession.getSessionObject();
            	
            	OOBTransactionAuthentication oobObject;
            	oobObject = new OOBTransactionAuthentication();
            	boolean oobOTPSent = false;
            	try{
            	oobOTPSent = oobObject.sendAuthenticationOOBOTPValue(username, currentAlsi, session);
            	}
            	catch(ArcAuthenticationServerException ex){
            		LOGGER.error(ex);            		
            	}
            	catch(Exception ex){
            		LOGGER.error(ex);
            	}
            	if (oobOTPSent) {
            		RequestDispatcher rd = httpRequest.getRequestDispatcher(TRANSACTION_PIN);
					rd.forward(request,response);
					return;
            	}
            	else {
            		session.setAttribute("OOB_SEND_FAILURE", "true");
            		RequestDispatcher rd = httpRequest.getRequestDispatcher(OOB_OTP_ERROR);
					rd.forward(request,response);
					return;
            	}
            }
			
		} else {
			LOGGER.info("Transaction signing not required for the request. ");
		}
		
		
		chain.doFilter(request,response);
	}
		
	/*
	 * saveRequestInformation(HttpServletRequest)
	 * Takes HttpRequest as a parameter 
	 * method to store the request and request URL in the session object.
	 */
	public void saveRequestInformation(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		session.setAttribute(Constant.SAVED_REQUEST_URL, getSaveableURL(request));
		session.setAttribute(Constant.SAVED_REQUEST, new SavedRequest(request));
	}

	/* getSaveableURL(HttpServletRequest)
	 * Takes HttpRequest as a parameter
	 * Retreive and save the url from the query string of the request.
	 * invokes other utility methods getRequestURL() and getFixProtocol()
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
	
	private void fixProtocol(StringBuffer url, HttpServletRequest request) {
		// fix protocol, if needed (since HTTP is the same regardless of whether
		// it runs on TCP or on SSL/TCP)
		if (request.getProtocol().equals("HTTP/1.1") && request.isSecure()
				&& url.toString().startsWith("http://")) {
			url.replace(0, 4, "https");
		}
	}
	
	private ArcSession getArcSession(ArcUserPrincipal userPrincipal, HttpSession httpSession) {
    	ArcSession arcSession = null;
    	if (containsArcSession(userPrincipal)) {
    		// get the arc session
    		Set sessionSet = userPrincipal.getSubject().getPublicCredentials(ArcSession.class);
    		arcSession = (ArcSession) sessionSet.iterator().next();
    		
    	} else {
	    	Object arcSessionObj = httpSession.getAttribute(ArcSession.class.getName());
	    	if (null != arcSessionObj) {
	    		arcSession = (ArcSession) arcSessionObj;
	    	}
    	}
    	return arcSession;
    }
	
	private boolean containsArcSession(ArcUserPrincipal userPrincipal) {
    	try {
    		ArcSession arcSession = arcSessionCredentialFrom(userPrincipal.getSubject());
    		
    		if (null == arcSession.getSessionObject() || 
    				(arcSession.getSessionObject() instanceof String && ((String)arcSession.getSessionObject()).equals(""))) {
    			return false;
    		}
    	} catch (ServletException e) {
    		// There is no ArcSession
    		return false;
    	}
    	
    	return true;
    }
	private ArcSession arcSessionCredentialFrom(final Subject subject) throws ServletException {
    	if (LOGGER.isDebugEnabled()) LOGGER.debug("Getting ArcSession from subject.");
        // TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(ArcSession.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcSession) publicCredentialSet.iterator().next();
    }

	/*
	 * Lifecycle method of Filter
	 * Called by the web container to indicate to a filter that it is being taken out of service.
	 */
	
	public void destroy(){
		
	}
	
}
