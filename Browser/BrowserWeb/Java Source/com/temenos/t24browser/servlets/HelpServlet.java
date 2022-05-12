////////////////////////////////////////////////////////////////////////////////
//
//  Class         : HelpServlet
//
//  Description   : Allows the display and modification of T24 helptext.
//					Retrieves the helptext xml (or html) from a list of paths and
//					also allows the helptext to be edited. In addition, also runs
//					a trip to the server to add any fields that are not part of the
//					current xml document.
//
//					Configuration is from an XML config file that should define
//						o The paths to search the documents for
//						o The list of allowed languages
//
//  Modifications :
//
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.servlets;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.beans.HelpBean;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.security.VariableSubstitutionHttpServletRequest;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.RequestTimer;
import com.temenos.t24browser.xml.XMLTemplateManager;
import com.temenos.tocf.tbrowser.TBrowserException;

// TODO: Auto-generated Javadoc
/**
 * The Class HelpServlet.
 */
public class HelpServlet extends HttpServlet
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(HelpServlet.class);
	
	/** The Constant HELP_PARAMETERS_INIT_PARAM. */
	public static final String HELP_PARAMETERS_INIT_PARAM = "helpParameters";
	
	/** The Constant TOKEN_SESSION_NAME. */
	private static final String TOKEN_SESSION_NAME = "BrowserToken";			// Used to save the Token in the session
	
	/** The Constant SESSION_SIGN_ON_NAME. */
	private static final String SESSION_SIGN_ON_NAME = "BrowserSignOnName";	// Used to save the user's sign on name in the session
	
	/** The Constant SESSION_USER_ID. */
	private static final String SESSION_USER_ID = "BrowserUserId";			// Used to save the user's Id in the session
	
	/** The iv template manager. */
	private XMLTemplateManager ivTemplateManager;		// Holds XML templates for parser
	
	/** The browser parameters. */
	private PropertyManager ivParameters;


	// Called on first invocation of Servlet
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init()
		throws javax.servlet.ServletException
	{
		LOGGER.info("startup");

		// Read in and store the XML templates in a manager
		ivTemplateManager = new XMLTemplateManager(this.getServletContext());
		
		// Read the Browser parameters from the XML file browserParameters.xml
		BrowserParameters params = new BrowserParameters( this.getServletContext() );
	 	ivParameters = params.getParameters();
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(
		javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{
		// Process all requests through the doPost method
		doPost( request, response );
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(
		javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{
		long timeInMillis = System.currentTimeMillis(); // Start out timer asap

        // Wrap the request with a wrapper object that will perform variable substitution
        // (e.g. converting "{user_id}" to the actual user id (this enables us to not send
        // the user id to the client for security reasons).
        request = new VariableSubstitutionHttpServletRequest(request);

		try
		{
			request.setCharacterEncoding("UTF8");
		}
		catch ( Exception sce )
		{
		}

		RequestTimer requestTimer = new RequestTimer();
		requestTimer.setStartTime(timeInMillis);
		HelpBean helpBean = null;

		// Get the token from the session and create the bean, setting the token in it
		HttpSession session = request.getSession();
		String token = (String) session.getAttribute( TOKEN_SESSION_NAME );
		String user = (String) session.getAttribute( SESSION_SIGN_ON_NAME );
		String userId = (String) session.getAttribute( SESSION_USER_ID );

		try
		{
			helpBean = new HelpBean( this.getServletConfig(), getTemplateManager(), "", request );
			helpBean.setRequestToken( token );
			helpBean.setSignOnName( user );
			helpBean.setUserId( userId );
			helpBean.setRequestTimer(requestTimer);

			// Process the request
			helpBean.processRequest();
			displayResponse( helpBean.getResponse(), helpBean.getResponseType(), response );
		}
		catch ( TBrowserException bex )
		{
		}
		catch ( SecurityViolationException e )
		{
			displaySecurityViolationException(e, request, response);
		}

		// Delete the bean
		helpBean = null;
	}

	/**
	 * Displays an error to the user and invalidates the session.
	 * @param e The exception - to display the message.
	 * @param request The request - to invalidate the session.
	 * @param response The response - to display the message.
	 */
	private void displaySecurityViolationException( Exception e, HttpServletRequest request, HttpServletResponse response ) {
		// Invalidate the session
		HttpSession session = request.getSession();
		session.invalidate();

		// Log the error
		LOGGER.error(e.getMessage());

		// Send only 'Security Violation'
		displayResponse("SECURITY VIOLATION", "HTTP.ERROR.CODE", response);
	}
	
	/**
	 * Display response.
	 * 
	 * @param sResponseText the s response text
	 * @param sResponseType the s response type
	 * @param response the response
	 */
	private void displayResponse( String sResponseText, String sResponseType, HttpServletResponse response )
	{
		// Display the response HTML or XML
		try
		{
			// Ensure that no caching takes place in the browser
			// If we are logging debug events then allow HTML to be viewed
			//if ( ( paramLogEvent.equals("YES") ) && ( paramLogLevel.equals("DEBUG") ) )
			//{
				// Allow HTML to be viewed
			//}
			//else
			//{
				// Disable caching and HTML view
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache,no-store");
				response.setDateHeader("Expires", 0);
			//}

			// Ensure the response is treated as UTF-8 characters
			response.setContentType("UTF8");

        	if ( sResponseType.equals( "URL" ) )
        	{
        		// Redirect to the supplied URL
				response.sendRedirect( sResponseText );
        	}
        	else if ( sResponseType.equals( "NO.RESPONSE" ) )
        	{
        		// No response to display so just ignore it
        	}
        	else
        	{
        		if ( sResponseType.equals( "XML" ) )
	        	{
		        	response.setContentType( "text/xml;charset=utf-8" );
	        	}
 	       		else
  	      		{
					response.setContentType( "text/html;charset=utf-8" );
  	      		}

	        	PrintWriter writer = response.getWriter();
        		writer.println( sResponseText );
        	}
        }
		catch ( Exception e )
		{
			// Error occurred, but can't write any response
			System.out.println("Error writing response");
			LOGGER.error("Error writing response", e);
		}
	}


	// Returns a clone of the template manager
	/**
	 * Gets the template manager.
	 * 
	 * @return the template manager
	 */
	private XMLTemplateManager getTemplateManager()
	{
		try{
			XMLTemplateManager manager = (XMLTemplateManager)ivTemplateManager.clone();
			return manager;
		}
		catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return null;
	}
}
