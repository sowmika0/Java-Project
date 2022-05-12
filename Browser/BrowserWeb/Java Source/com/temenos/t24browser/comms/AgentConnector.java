package com.temenos.t24browser.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;


import com.jbase.jremote.io.JRemoteSocketConnection;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.exceptions.ConnectionException;
import com.temenos.tocf.t24ra.T24ConnectionFactory;
import com.temenos.tocf.t24ra.T24Exception;

/**
 * <p>
 * Connector which uses the T24 resource adapter to send OFS request
 * messages to T24.
 * </p>
 * 
 * @author wzahran
 */
public class AgentConnector implements ConnectionBean, Serializable 
{
	
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentConnector.class);
    private static final long serialVersionUID = 1L;
    /** The BROWSE r_ XM l_ HEADER. */
    private String BROWSER_XML_HEADER;
    
      /** 'ConnectionTimeout' */
    public static final String PARAM_CONNECT_TIMEOUT = "ConnectionTimeout";    

    /** RetryCount. */
	public static final String PARAM_NO_OF_RETRIES = "RetryCount";

	 /** RetryWait. */
	public static final String PARAM_NO_OF_RETRYWAIT = "RetryWait";
   
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
	
	/** The host name tag */
	private final String isHostName = "hostName";
	
	/** Service Locator **/
    private ServiceLocator serviceLocator = null;
      	  
    // Default Constructor
    /**
     * Instantiates a new instance connector.
     */
    public AgentConnector() 
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
    public AgentConnector(HttpServletRequest request, PropertyManager ivParameters) 
    {
        this.request = request;
        this.ivParameters = ivParameters;
        String reqHostName = ivParameters.getParameterValue(isHostName);
		this.serviceLocator = new ServiceLocator();
		if(!reqHostName.equalsIgnoreCase("no"))
	    {
			BROWSER_XML_HEADER = RequestUtils.setBrowserXmlHeader(reqHostName, request);
	    } else {
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
    public BrowserResponse talkToServer(String xmlString, String clientIP)  throws ConnectionException {
    
        return sendMessage(xmlString, BROWSER_XML_HEADER, null);
    }

    /*
     * Method not required - principal available via container security
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
     *      java.lang.String, java.security.Principal)
     */
    public BrowserResponse talkToServer(String xmlString, String clientIP, Principal principal)  throws ConnectionException {
    
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
     * Send the OFS request message.
     * 
     * @param msg OFS request
     * @param header OFS request header
     * @param principal Principal associated to this request
     * @return the browser response
     * @throws ConnectionException 
     */
    private BrowserResponse sendMessage(String msg, String header, Principal principal) throws ConnectionException {  
    
        BrowserResponse myResponse = new BrowserResponse(ivParameters);
        String strOFSRequest = header + msg;
         String strOFSMLResponse = null; 
        T24ConnectionFactory cxf = null;
        	
        //Obtain the username from the principal
        String t24principal = "";
        if(principal != null) {
           t24principal = principal.getName();
        }

        //Process OFS request
     try 
        {
            long started = System.currentTimeMillis();
            cxf = serviceLocator.lookupT24ConnectionFactory();

            try {
            	strOFSMLResponse = cxf.processOFSRequest(strOFSRequest, t24principal);
            } catch (T24Exception e) {
                if (e.getMessage() != null && e.getMessage().indexOf("ManagedConnectionFactory is null") > 0) {
                    /* datasource settings have changed - Note this message string is a JBoss message, 
                     * but this resetting of the connection factory would only be used in development anyway */
                    serviceLocator.removeT24ConnectionFactory();
                    cxf = serviceLocator.lookupT24ConnectionFactory();
                    strOFSMLResponse = cxf.processOFSRequest(strOFSRequest, t24principal);
                } else {
                    throw e;
                }
            }
                  //Format response       
		    DebugUtils utils = new DebugUtils(ivParameters);
			utils.writeRequestXML( strOFSRequest, request);
			strOFSMLResponse = utils.preprocessT24Xml(strOFSMLResponse, requestFragmentName, request);
            strOFSMLResponse = utils.addMenuProperty(strOFSMLResponse);            
            
            // set the OFS response
            myResponse.setMsg(strOFSMLResponse);
            long elapsed = System.currentTimeMillis() - started;
            myResponse.setOfsTime(elapsed);
        } catch (T24Exception e) {
            //Throw connection exception to trigger re-sending the OFS request
            throw new ConnectionException(e.getMessage());              
        } 

        return myResponse;
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
        throw new UnsupportedOperationException("AgentConnector does not support multiple channels");
    }

}
