package com.temenos.t24browser.servlets;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class BrowserSessionListener implements HttpSessionListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserSessionListener.class);
	
	/** Session constants */
	private static final String SESSION_SIGN_ON_NAME = "BrowserSignOnName";	// Used to save the user's sign on name in the session
	private static final String SESSION_USER_ID = "BrowserUserId";			// Used to save the user's Id in the session
	private static final String SESSION_PASSWORD = "BrowserPassword";		// Used to save the user's password in the session
	private static final String SESSION_CLIENT_IP_ADDRESS = "BrowserClientIpAddress";	// Used to save the user's IP address in the session
	private static final String SESSION_LOGGEDIN = "LoggedIn";  // LoggedIn Flag
	private static final String SESSION_LOGIN_COUNTER = "LoginCounter";  // LoginCounter stored in the session
	private static final String TOKEN_SESSION_NAME = "BrowserToken";
	private static final String TOKEN_SEQUENCE_NUMBER = "TokenSequence";
	protected static final String SESSION_CACHED_REQUEST = "BrowserCachedRequest";	// Used to save a cached XML request in the user's session
	
	public void sessionCreated(HttpSessionEvent event)
	{

	}

	public void sessionDestroyed(HttpSessionEvent event)
	{
		// Log which session has been invalidated by the servlet or caused by a web server timeout
		HttpSession session = event.getSession();
		
		LOGGER.debug( "Session Destroyed : Id = " + session.getId() );
		
		LOGGER.debug( "Clearing Session Data : Id = " + session.getId() );
		session.setAttribute( SESSION_SIGN_ON_NAME, "" );
		session.setAttribute( SESSION_USER_ID, "" );
		session.setAttribute( SESSION_PASSWORD, "" );
		session.setAttribute( SESSION_LOGGEDIN, "" );
		session.setAttribute( SESSION_LOGIN_COUNTER, "" );
		session.setAttribute( SESSION_CLIENT_IP_ADDRESS, "" );
		session.setAttribute( SESSION_CACHED_REQUEST, "" );
		session.setAttribute( TOKEN_SESSION_NAME, "" );
		Integer tokenSequenceNumber = new Integer(0);
		session.setAttribute( TOKEN_SEQUENCE_NUMBER, tokenSequenceNumber);
	}

}
