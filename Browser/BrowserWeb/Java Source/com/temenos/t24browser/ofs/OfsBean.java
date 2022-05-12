package com.temenos.t24browser.ofs;

import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.comms.ConnectionBean;
import com.temenos.t24browser.comms.ConnectionEngine;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.PropertyManager;


/**
 * Class dealing with the processing of OFS requests
 */

public class OfsBean implements Serializable
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsBean.class);
	
	private HttpServletRequest ivHttpRequest = null;	// The Http Request
	protected ServletConfig ivServletConfig = null;		// Config of Servlet
	private OfsServerConfig ivOfsConfig = null;			// OFS Server configuration data
	private String ivClientIP = null;					// IP Address of the client Browser
	private boolean ivError = false;					// Whether the request worked or not
	private String ivErrorText = "";					// Error message
	
	/** OFS Response Code. **/
	private static final int OFS_SUCCESS = 1;
	private static final int OFS_FAILURE = -1;
	
	// Browser Parameters constants
	public static final String PARAM_SERVER_CONN_METHOD = "Server Connection Method";		// Parameter Server Connection Method
	public static final String PARAM_INSTANCE_NAME      = "Instance";

	
	/**
	 * Instantiates a new OFS bean.
	 * 
	 * @param config the config
	 * @param xmlTemplates the xml templates
	 * @param clientIP the client IP
	 * @param request the request
	 */
	public OfsBean( ServletConfig servletConfig, OfsServerConfig ofsConfig, String clientIP, HttpServletRequest request )
	{
		T24Request ivRequest = new T24Request( request );
		ivHttpRequest = request;
		ivServletConfig = servletConfig;
		ivOfsConfig = ofsConfig;
		ivClientIP = clientIP;
		
		processCommand( ivRequest );
	}
	
	private void processCommand( T24Request request )
	{
		// Generate an OFS Request using unencrypted user name and password
		OfsCrypto crypto = new OfsCrypto( ivOfsConfig );
		String user = crypto.getUnencryptedUser();
		String password = crypto.getUnencryptedPassword();
		
		// Process the request via OFS
		OfsRequest ofsReq = new OfsRequest( ivOfsConfig, request, user, password );
		String reqString = ofsReq.getRequest();

		OfsResponse ofsResp = processOfsRequest( reqString );
		int ofsReturnCode = ofsResp.getResponseStatus();
		
		if ( ofsReturnCode == ofsResp.OFS_SUCCESS )
		{
			ivError = false;
		}
		else
		{
			ivError = true;
		}
	}	
	
	/**
	 * Instantiates a new OFS bean.
	 * 
	 * @param request The OFS request
	 * @return The OFS response
	 */
	private OfsResponse processOfsRequest( String request )
	{
		OfsResponse ofsResponse = null;
		
		try
		{
			BrowserParameters params = new BrowserParameters( ivServletConfig.getServletContext() );
			PropertyManager ivParameters = params.getParameters();

			// Set-up the connection to the server
			String connMethod = ( (String) ivParameters.getProperty( PARAM_SERVER_CONN_METHOD )).toUpperCase();
			String instanceName = (String) ivParameters.getProperty( PARAM_INSTANCE_NAME );
			
			ConnectionEngine connEngine = new ConnectionEngine( connMethod, instanceName, ivHttpRequest, ivParameters, null ); 
			ConnectionBean connection = connEngine.initialiseConnection();
			ivError = connEngine.getError();
			ivErrorText = connEngine.getErrorText();
			
			if ( ! ivError )
			{
				BrowserResponse response = null;
				response = connection.talkToServerOfs( request, ivClientIP );
				ofsResponse = new OfsResponse( response.getMsg(), response.isValid() );
			}
		}
		catch (NoClassDefFoundError ncdf)
		{
			// Means browserParameters is set to a JCA connection but they aren't using an application server (like jboss).
			String ivServerConnectionMethod = "Instance";
			LOGGER.error("'Server Connection Method' = " + ivServerConnectionMethod + " is not supported by this web container. Check browserParameters.xml");
			ivError = true;
		}
		catch (Exception e)
		{
			LOGGER.error("Error running OFS Request");
			ivError = true;
		}

		return ofsResponse;
	}

	/**
	 * Returns the response code.
	 * 
	 * @return The OFS response code
	 */
	public boolean requestError()
	{
		return ivError;
	}
}
