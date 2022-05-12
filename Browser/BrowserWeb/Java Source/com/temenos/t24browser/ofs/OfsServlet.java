package com.temenos.t24browser.ofs;

import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.beans.BrowserBean;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.servlets.BrowserServlet;
import com.temenos.t24browser.utils.RequestTimer;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.xml.XMLTemplateManager;


/**
 * Class dealing with OFS requests
 */

public class OfsServlet extends HttpServlet implements Serializable
{	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsServlet.class);
	
	/** Servlet Context Strings **/
	private static final String CONTEXT_OFS_SERVER_CONFIG = "OfsServerConfig";
	
	/** OFS Response Code. **/
	private static final int OFS_SUCCESS = 1;
	private static final int OFS_FAILURE = -1;
	
	
	// Called on first invocation of Servlet
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init()
		throws javax.servlet.ServletException
	{
		LOGGER.debug("OfsServlet : init");
		
		ServletContext context = getServletContext();
		DebugUtils.init(context.getRealPath(""));

		// Read the ofs-server.config file and store in the servlet context
		try
		{
			readParameters();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	// Called on first invocation of Servlet after the init method
	// Builds the login page for display
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(
		javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{
		LOGGER.debug("OfsServlet : doGet");
	}
	
	
	// Called for each Browser command request
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{
		LOGGER.debug("OfsServlet : doPost");
		
		OfsServerConfig ofsConfig = getParameters();
		
		LOGGER.debug( "***** Ofs : Start Request ***********************************************" );
		
		// Validate the request type
		String requestType = Utils.getValue( request, "requestType"); 
		
		// The following code is to invalidate the previous JSESSIONID . Before that take a backup of request attributes and place 
        // all those attributes in the new request object.	
        if ( ( requestType != null ) && ( requestType.equals( "OFS" ) ) )
		{
    		OfsBean ofsBean = null;
    		String clientIP = Utils.getClientIpAddress( request );
    		
			ofsBean = new OfsBean( getServletConfig(), ofsConfig, clientIP, request );
			
			if ( ofsBean.requestError() )
			{
				displayOfsResponse( request, response, ofsConfig, OFS_FAILURE );
			}
			else
			{
				displayOfsResponse( request, response, ofsConfig, OFS_SUCCESS );
			}
		}
        else
        {
        	// Error
        	LOGGER.error("Invalid request type");
        }
		
		LOGGER.debug( "***** Ofs : End Request *************************************************" );
	}
	
	/**
	 * Reads the OFS configuration file and stores it in the servlet context
	 * 
	 */
	private void readParameters()
	{
		// Read the config file
		OfsServerConfig config = new OfsServerConfig( getServletContext() );
		
		// Save it in the servlet context
		getServletContext().setAttribute( CONTEXT_OFS_SERVER_CONFIG, config );
	}

	/**
	 * Retrieves the OFS configuration file from the servlet context
	 * 
	 */
	private OfsServerConfig getParameters()
	{
		return( (OfsServerConfig) getServletContext().getAttribute(CONTEXT_OFS_SERVER_CONFIG) );
	}
	
	/**
	 * Display response.
	 * 
	 * @param request The Http Request
	 * @param response The Http Response
	 * @param config The OFS Server Configuration
	 * @param responseCode The Http Response Code
	 */
	private void displayOfsResponse( HttpServletRequest request, HttpServletResponse response, OfsServerConfig config, int responseCode )
	{
		// Display the response page depending on the result
		try
		{
			if ( responseCode == OFS_SUCCESS )
			{
				// Redirect to the success page
				// Either use the one in the request or the default config success page
				String successPage = Utils.getValue( request, "successPage");
				
				if ( ( successPage == null ) || ( successPage.equals("") ) )
				{
					successPage = config.getDefaultSuccessPage();
				}
				
				response.sendRedirect( successPage );
			}
			else
			{
				// Redirect to the failure page
				// Either use the one in the request or the default config failure page
				String failurePage = Utils.getValue( request, "failurePage");
				
				if ( ( failurePage == null ) || ( failurePage.equals("") ) )
				{
					failurePage = config.getDefaultFailurePage();
				}
				
				response.sendRedirect( failurePage );
			}
		}
		catch ( Exception e )
		{
			// Error occurred, but can't write any response
			System.out.println("Error writing OFS response");
			LOGGER.error("Error writing OFS response", e);
		}
	}
}
