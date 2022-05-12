package com.temenos.t24browser.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.jbase.jremote.io.JRemoteSocketConnection;
import com.jbase.jremote.JConnection;
import com.jbase.jremote.JConnectionCallbackHandler;
import com.jbase.jremote.JConnectionFactory;
import com.jbase.jremote.JDynArray;
import com.jbase.jremote.JRemoteException;
import com.jbase.jremote.JSubroutineParameters;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.exceptions.ConnectionException;

/**
 * <p>
 * This class can lookup up a JRemote JCA connection to jBASE and send OFS
 * messages to T24 by calling OFS.BULK.MANAGER
 * </p>
 */
public class AgentJRemoteConnector implements ConnectionBean, Serializable 
{
    private static final long serialVersionUID = 1L;

    /** T24 Server Routines to call. */
	private static final String SERVER_REQUESTS_ROUTINE = "OFS.BULK.MANAGER";
//	private static final String SERVER_INITIALISE_ROUTINE = "JF.INITIALISE.CONNECTION";
    private static final String SERVER_SET_SECURITY_CONTEXT_ROUTINE = "EB.SET.SECURITY.CONTEXT";

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentJRemoteConnector.class);
    /** The BROWSE r_ XM l_ HEADER. */
    private String BROWSER_XML_HEADER;

    /** 'ConnectionTimeout' */
    public static final String PARAM_CONNECT_TIMEOUT = "ConnectionTimeout";
    
    /** The request. */
    private HttpServletRequest request;

    /** The iv parameters. */
    private PropertyManager ivParameters;

    /** The request fragment name. */
    private String requestFragmentName;

    /** The Host name */
	private String hostName;

    /** The Ip address*/
    private String ipAddr;
    
    /** The Client Ip address */
    private String clientIpAddr;

	/** The host name tag */
	private final String isHostName = "hostName";
      
	/** The service locator */
    private ServiceLocator serviceLocator = null;

    
    /** RetryCount. */
	public static final String PARAM_NO_OF_RETRIES = "RetryCount";

	 /** RetryWait. */
	public static final String PARAM_NO_OF_RETRYWAIT = "RetryWait";
	
    // Default Constructor
    /**
     * Instantiates a new instance connector.
     */
    public AgentJRemoteConnector() 
    {
    }

    /**
     * Instantiates a new instance connector.
     * 
     * @param context
     *            the context
     * @param ivParameters
     *            the iv parameters
     */
    public AgentJRemoteConnector(HttpServletRequest request, PropertyManager ivParameters) 
    {
        this.request = request;
        this.ivParameters = ivParameters;
        ipAddr = RequestUtils.getRequestIpAddress(request);
        // The Client Ip address
        clientIpAddr=RequestUtils.getRequestClientIpAddress(request); 
		hostName =  RequestUtils.getRequestHostName(request);
		String reqHostName = ivParameters.getParameterValue(isHostName);
		if(reqHostName.equalsIgnoreCase("yes"))
        {		
			BROWSER_XML_HEADER = "<CLIENTIP>" + clientIpAddr + "</CLIENTIP>" + "<hostName>" + hostName + "</hostName>" + "BROWSER.XML,,,,,,<"; 
		}
		else
        {
            BROWSER_XML_HEADER = "BROWSER.XML,,,,,,<";
        }
		serviceLocator = ServiceLocator.getInstance();
    }

    // Sets-up a connection to the server
    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#setupServer(java.lang.String,int)
     */
    public BrowserResponse setupServer(String sInstanceName, int timeOutSecs) 
    {
        BrowserResponse myResponse = new BrowserResponse(ivParameters);
        myResponse.setMsg("");
        return myResponse;
    }

    /*
     * Default implementation - clientIP discarded
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
     */
    public BrowserResponse talkToServer(String xmlString, String clientIP) throws ConnectionException
    {
        return sendMessage(xmlString, BROWSER_XML_HEADER, null);
    }

    /*
     * Method not required - principal available via container security
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
     *      java.lang.String, java.security.Principal)
     */
    public BrowserResponse talkToServer(String xmlString, String clientIP, Principal principal) throws ConnectionException
    {
        return sendMessage(xmlString, BROWSER_XML_HEADER, principal);
    }

    /*
     * Default implementation - clientIP discarded
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
     */
    public BrowserResponse talkToServerOfs(String ofs, String clientIP) throws ConnectionException
    {
        return sendMessage(ofs, "", null);
    }

    /*
     * Method not required - principal available via container security
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
     *      java.lang.String, java.security.Principal)
     */
    public BrowserResponse talkToServerOfs(String ofs, String clientIP, Principal principal) throws ConnectionException 
    {
        return sendMessage(ofs, "", principal);
    }

    /**
     * Get the timeout value specifying the number of seconds
     * to wait for a response message.
     * @return timeout in seconds
     */
    public long getTimeout() {
    	return Long.parseLong(ivParameters.getParameterValue(PARAM_CONNECT_TIMEOUT));
    }
    
    /**
     * Get the no of times to retry to establish a connection and  
     * wait for a response message.
     * @return timeout in seconds
     */    
    public int getRetryCount() {
    	return Integer.parseInt(ivParameters.getParameterValue(PARAM_NO_OF_RETRIES));
    }
    /**
     * Get the no of times to wait to establish a connection and  
     * wait for a response message.
     * @return timeout in seconds
     */    
    public int getRetryWait() {
    	return Integer.parseInt(ivParameters.getParameterValue(PARAM_NO_OF_RETRYWAIT));
    }
    
    /**
     * Send message.
     * 
     * @param msg
     *            the msg
     * @return the browser response
     * @throws ConnectionException 
     */
    private BrowserResponse sendMessage(String msg, String header, Principal principal) throws ConnectionException
    {
        BrowserResponse myResponse = new BrowserResponse(ivParameters);
        String strOFSRequest = header + msg;
        JConnectionFactory cxf = null;

        //Obtain the username from the principal
        String t24Principal = "";
        if(principal != null) {
        	t24Principal = principal.getName();
        }
        
        JConnection connection = null;
        try 
        {
            long started = System.currentTimeMillis();

            // lookup JRemote JCA
            cxf = serviceLocator.lookupJConnectionFactory();

            /*
             * get handle to a connection (container will create connection if
             * none available in pool)
             */
            JConnectionCallbackHandler jch = new T24InitialisationHandler();
            try {
                connection = cxf.getConnection(jch);
            } catch (JRemoteException e) {
                if (e.getMessage() != null && e.getMessage().indexOf("ManagedConnectionFactory is null") > 0) {
                    /*
                     * datasource settings have changed - Note this message
                     * string is a JBoss message, but this resetting of the
                     * connection factory would only be used in development
                     * anyway
                     */
                	serviceLocator.removeJConnectionFactory();
                    // lookup again
                    cxf = serviceLocator.lookupJConnectionFactory();
                    connection = cxf.getConnection(jch);
                } else {
                    throw e;
                }
            }

        	//Call subroutine to set the security context if necessary
        	if(t24Principal != null && t24Principal.length() > 0) {
				LOGGER.debug("Setting security context for user: " + t24Principal);
            	String secCtx = t24Principal + "/SSOPW1";
		        JSubroutineParameters paramsSecCtx = new JSubroutineParameters();
		        paramsSecCtx.add(connection.newJDynArray(strOFSRequest)); 	//OFS Request
		        paramsSecCtx.add(connection.newJDynArray(secCtx)); 		//Security context
		        connection.call(SERVER_SET_SECURITY_CONTEXT_ROUTINE, paramsSecCtx);
        	}
            
            // If we want to call JF.INITIALISE.CONNECTION for
            // each request
            //connection = cxf.getConnection();
            //connection.call("JF.INITIALISE.CONNECTION", null);
            
            // call subroutine to process OFS message
            JSubroutineParameters params = getSubroutineParams(connection, strOFSRequest);
            params = connection.call(SERVER_REQUESTS_ROUTINE, params);
            // Cast would not be necessary if project were JDK 5.0
            JDynArray ofsResponse = (JDynArray) params.get(1);
            // Is all of the message returned as a string in attribute 1?
            String strOFSMLResponse = ofsResponse.get(1);
	        
            //Format response
			DebugUtils utils = new DebugUtils(ivParameters);
			utils.writeRequestXML( strOFSRequest, request);
			strOFSMLResponse = utils.preprocessT24Xml(strOFSMLResponse, requestFragmentName, request);
            strOFSMLResponse = utils.addMenuProperty(strOFSMLResponse);            
            
            // set the OFS response
            myResponse.setMsg(strOFSMLResponse);
            long elapsed = System.currentTimeMillis() - started;
            myResponse.setOfsTime(elapsed);
        } catch (JRemoteException e) {
            //Throw connection exception to trigger re-sending the OFS request
            throw new ConnectionException(e.getMessage());
        } 
        finally 
        {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (JRemoteException e) {
                throw new ConnectionException(e.getMessage());
	        }
        }

        return myResponse;
    }


    /**
     * Adds the parameters needed to call OFS.BULK.MANAGER
     * @param connection
     * @param sRequestParams
     * @return 
     */
    private JSubroutineParameters getSubroutineParams(JConnection connection, String sRequestParams) 
    {
        JSubroutineParameters params = new JSubroutineParameters();
        params.add(connection.newJDynArray(sRequestParams));	// the request(s)
        params.add(connection.newJDynArray());					// the response(s) - so add ''
        params.add(connection.newJDynArray());					// the request committed - so add ''
        return params;
    }
     /**
     * Get the list of available channels. If channels are not supported, throws
     * an UnsupportedOperationException.
     * 
     * @return a Set of channel names.
     * 
     * @throws UnsupportedOperationException
     *             the unsupported operation exception
     */
    public Set<?> getChannels() throws UnsupportedOperationException 
    {
        throw new UnsupportedOperationException("AgentJRemoteConnector does not support multiple channels");
    }

}