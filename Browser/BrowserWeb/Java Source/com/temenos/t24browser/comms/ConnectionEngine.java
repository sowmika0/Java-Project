package com.temenos.t24browser.comms;

import javax.servlet.http.HttpServletRequest;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.branch.Branch;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.tocf.tbrowser.TBrowserException;


/**
 * Manages a connection including start-up
 */

public class ConnectionEngine
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionEngine.class);
	
	// Browser Connection Methods
	private static final String CONNECT_METHOD_INSTANCE = "INSTANCE";	// T24 Connector connection method
	private static final String CONNECT_METHOD_AGENT = "AGENT";			// JCA jRemote connection method
	private static final String CONNECT_METHOD_AGENT_JREMOTE = "AGENT_JREMOTE";		// JCA jRemote connection method
	private static final String CONNECT_METHOD_JMS = "JMS";             // JMS connection method
    
    private String ivConnectionMethod = "";								// The Browser connection method
    private String ivInstanceName = "";									// TC Instance name to use
    private HttpServletRequest ivHttpRequest = null;					// Http Request from browser page
    private PropertyManager ivParameters = null;						// The browserParameters.xml parameters
    private Branch ivBranch = null;										// The Branch Config
    protected boolean ivError = false;									// Whether an error occurred
	protected String ivErrorText = "";									// What error occurred
	
	
	/**
	 * Instantiates a new OFS bean.
	 * 
	 * @param connMethod The connection method
	 * @param instanceName The name of a TC instance
	 * @param request HttpRequest
	 * @param browserParams Browser Parameters from browserParameters.xml
	 * @param branchParams Branch details 
	 */
	public ConnectionEngine( String connMethod, String instanceName, HttpServletRequest request, PropertyManager browserParams, Branch branchParams )
	{
		ivConnectionMethod = connMethod;
		ivInstanceName = instanceName;
		ivHttpRequest = request;
		ivParameters = browserParams;
		ivBranch = branchParams;
	}
	
	/**
	 * Initialises a connection
	 * @throws TBrowserException
	 */
	public ConnectionBean initialiseConnection()  throws TBrowserException
	{
		ConnectionBean conn = null;
		
		// Sets-up connection to the server
		// Determine connection method, and whether we are in a branch, and set-up accordingly
		if ( ivBranch != null )
		{
			LOGGER.info("Setting-up Branch Connection");
						
			// We are in a branch web server, so get the current instance name
			conn = new InstanceConnector( ivHttpRequest, ivParameters );
			conn.setupServer( ivInstanceName, 0);
		}
		else
		{
			LOGGER.info("Setting-up Connection using - " + ivConnectionMethod);

			try {
				if ( ivConnectionMethod.equals( CONNECT_METHOD_INSTANCE ) )
				{
					// New Instance method just needs an instance name
					conn = new InstanceConnector( ivHttpRequest, ivParameters );
					conn.setupServer( ivInstanceName, 0);
				}
				else if ( ivConnectionMethod.equals( CONNECT_METHOD_AGENT ) )
				{
			      // This connection type is used for T24 JCA RA (J2EE Connector Architecture)
					conn = new AgentConnector( ivHttpRequest, ivParameters);
				}
				else if ( ivConnectionMethod.equals( CONNECT_METHOD_AGENT_JREMOTE ) )
				{
					// This connection type is used for jRemote/JCA (J2EE Connector Architecture)
					conn = new AgentJRemoteConnector( ivHttpRequest, ivParameters);
				}
	            else if ( ivConnectionMethod.equals( CONNECT_METHOD_JMS ) )
	            {
	                // This connection type is used for JMS
	            	conn = new JMSConnector( ivHttpRequest, ivParameters);
				}
				else
				{
					LOGGER.error("Unknown Connection Method : '" + ivConnectionMethod + "' - using default setting.");
					conn = new InstanceConnector( ivHttpRequest, ivParameters );
					conn.setupServer( ivInstanceName, 0);
				}
			} catch (NoClassDefFoundError e) {
				// Means browserParameters is set to a JCA connection but they aren't using an application server (like jboss).
				LOGGER.error("'Server Connection Method' = " + ivConnectionMethod + " is not supported by this web container. Check browserParameters.xml");
				ivErrorText = "Server Connection Method not supported. Please contact your System Administrator.";
				ivError = true;
			}
		}
		
		return conn;
	}

	/**
	 * Returns error indication
	 * 
	 * @return Whether there is an error
	 */
	public boolean getError()
	{
		return ivError;
	}
	
	/**
	 * Returns error message
	 * 
	 * @return Error text
	 */
	public String getErrorText()
	{
		return ivErrorText;
	}
}
