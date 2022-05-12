////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   SessionData
//
//  Description   :   Stores details about a T24 HTTP Servlet Session.
//
//  Modifications :
//
//    05/02/09   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.request;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * The Class SessionData.
 */
public class SessionData implements Serializable
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger( T24Request.class);
	
	/** The iv attributes. */
	private HttpSession ivSession = null;									// The Http Session object
	
	/** Constants of Session attributes */
	public static final String TOKEN_SESSION_NAME = "BrowserToken";		// T24 Token
	public static final String TOKEN_SEQUENCE_NUMBER = "TokenSequence";	// Sequence Number to add to the Token
	public static final String SESSION_SIGN_ON_NAME = "BrowserSignOnName";	// T24 User's Sign On Name
	public static final String SESSION_USER_ID = "BrowserUserId";			// T24 User's Id
	
	
 	/**
	 * Instantiates a new t24 session.
	 * 
	 * @param request the request
	 */
	public SessionData( HttpSession session )
	{
 		ivSession = session;
	}
	
	/**
	 * Convert a Http Session in to a string showing the T24 attributes.
	 * 
	 * @return String
	 */
	public String toString()
	{
		String sSession = "\n";

		sSession += " *** Session Data *** \n";
		sSession += "      - Id = " + ivSession.getId() + "\n";
		sSession += "      - Sign On Name = " + getAttribute(SESSION_SIGN_ON_NAME) + "\n";
		sSession += "      - User Id = " + getAttribute(SESSION_USER_ID) + "\n";
		sSession += "      - Token = " + getAttribute(TOKEN_SESSION_NAME) + "\n"; 
		sSession += "      - Token Seq No = " + getAttribute(TOKEN_SEQUENCE_NUMBER) + "\n";
		
		return( sSession );
	}
	
	private Object getAttribute( String name )
	{
		Object value = ivSession.getAttribute(name);
		
		if ( value == null )
		{
			value = "";
		}
		
		return( value );
	}
}
