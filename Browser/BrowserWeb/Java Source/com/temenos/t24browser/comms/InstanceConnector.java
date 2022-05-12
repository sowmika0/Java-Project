////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   InstanceConnector
//
//  Description   :   Bean for controlling connections for OFS requests between
//					  the Browser Servlet and the server via Globus Connector.
//
//  Modifications :
//
//    11/10/02   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tocf.tbrowser.TBrowserRequestSender;
import com.temenos.t24browser.request.RequestUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class InstanceConnector.
 */
public class InstanceConnector implements ConnectionBean, Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(InstanceConnector.class);
	
	/** The BROWSE r_ XM l_ HEADER. */
	private String BROWSER_XML_HEADER ;
	
	/** The iv instance. */
	private String ivInstance = "";
	
	/** The request. */
	private HttpServletRequest request;

	/** The iv parameters. */
	private PropertyManager ivParameters;
	
    /** The request fragment name. */
    private String requestFragmentName; 
	
	/** The host name of the client*/
	private String hostName;
	
	/** The host name tag */
	private final String isHostName = "hostName";
	
	/**  The Constant for ARC-IB customised TC message */
	private final String ARC_CUSTOMISED_TCMSG ="Error occurred. Contact System Administrator";
	       
	 /** RetryCount. */
	public static final String PARAM_NO_OF_RETRIES = "RetryCount";

	 /** RetryWait. */
	public static final String PARAM_NO_OF_RETRYWAIT = "RetryWait";

	//Default Constructor
	/**
	 * Instantiates a new instance connector.
	 */
	public InstanceConnector()
	{
	}

	/**
	 * Instantiates a new instance connector.
	 * 
	 * @param context the context
	 * @param ivParameters the iv parameters
	 */
	public InstanceConnector(HttpServletRequest request, PropertyManager ivParameters)
	{
		this.request = request;
		this.ivParameters = ivParameters;
		String reqHostName = ivParameters.getParameterValue(isHostName);
		if(reqHostName.equalsIgnoreCase("hostName") || reqHostName.equalsIgnoreCase("default"))
        {	
			hostName =  RequestUtils.getRequestHostName(request);
			BROWSER_XML_HEADER = "<hostName>" + hostName + "</hostName>" + "BROWSER.XML,,,,,,<"; 
		}
		else
        {
            BROWSER_XML_HEADER = "BROWSER.XML,,,,,,<";
        }
	}

		// Sets-up a connection to the server
	/* (non-Javadoc)
		 * @see com.temenos.t24browser.comms.ConnectionBean#setupServer(java.lang.String, int)
		 */
	public BrowserResponse setupServer(String sInstanceName, int timeOutSecs)
	{
		BrowserResponse myResponse = new BrowserResponse(ivParameters);
		setGCInstance(sInstanceName);
		LOGGER.info("Instance set to " + ivInstance);
		myResponse.setMsg("");
		return myResponse;
	}

	// Talk to the server sending the client IP address for use with telnet debugging
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String, java.lang.String)
	 */
	public BrowserResponse talkToServer(String xmlString, String clientIP)
	{
		return( sendMessage( xmlString, BROWSER_XML_HEADER, clientIP, null ) );
	}

	// Talk to the server sending the principal for security
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String, java.lang.String, java.security.Principal)
	 */
	public BrowserResponse talkToServer(String xmlString, String clientIP, Principal principal)
	{
		return( sendMessage( xmlString, BROWSER_XML_HEADER, clientIP, principal ) );
	}
	
	// Talk to the server (OFS string format) sending the client IP address for use with telnet debugging
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServerOfs(java.lang.String, java.lang.String)
	 */
	public BrowserResponse talkToServerOfs(String ofs, String clientIP) 
	{
		return( sendMessage(ofs, "", clientIP, null) );
	}
	
	// Talk to the server (OFS string format) sending the principal for security
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.comms.ConnectionBean#talkToServerOfs(java.lang.String, java.lang.String, java.security.Principal)
	 */
	public BrowserResponse talkToServerOfs(String ofs, String clientIP, Principal principal) 
	{
		return( sendMessage(ofs, "", clientIP, principal) );
	}
	
	/**
     * Get the timeout value specifying the number of seconds
     * to wait for a response message.
     * @return timeout in seconds
     */
    public long getTimeout() {
    	return 0;
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

    // Talk to the server sending the principal for security
	/**
	 * Send message.
	 * 
	 * @param msg the msg
	 * @param header the header
	 * @param clientIP the client IP
	 * @param principal the principal
	 * 
	 * @return the browser response
	 */
	private BrowserResponse sendMessage(String msg, String header, String clientIP, Principal principal) 
	{
		BrowserResponse myResponse = new BrowserResponse(ivParameters);

		try
		{
			String strOFSRequest = header + msg;
			TBrowserRequestSender gbrs = new TBrowserRequestSender(ivInstance);
			// Get the client IP address, when client having proxy in their network
			String clientIpAddr=RequestUtils.getRequestClientIpAddress(request); 
			String strOFSMLResponse = gbrs.sendRequest(strOFSRequest, clientIpAddr, principal);
			DebugUtils utils = new DebugUtils(ivParameters);
			utils.writeRequestXML( strOFSRequest, request);
			strOFSMLResponse = utils.preprocessT24Xml(strOFSMLResponse, requestFragmentName, request);
            strOFSMLResponse = utils.addMenuProperty(strOFSMLResponse);
			myResponse.setMsg(strOFSMLResponse);
			myResponse.setOfsTime(gbrs.getLastOFSDuration());
			gbrs.close();
		} catch (TBrowserException tex)
		{
			//if product is arc-ib then display meaningful error message to the user.
			String arcIb = ivParameters.getParameterValue("Product");
			if ( (arcIb!=null) && (arcIb.equals("ARC-IB")) ) 
			{
				myResponse.setError(ARC_CUSTOMISED_TCMSG);
				myResponse.setMsg(ARC_CUSTOMISED_TCMSG);				
			}
			else
			{
				myResponse.setError(tex.getMessage());
				myResponse.setMsg(tex.getOfsMessage());
			}
			LOGGER.error(tex.getMessage());
		}
		
		return myResponse;
	}

	/**
	 * Sets the GC instance.
	 * 
	 * @param sInstance the new GC instance
	 */
	private void setGCInstance(String sInstance)
	{
		ivInstance = sInstance;
		LOGGER.info("Creating " + ivInstance);
	}
    
    /**
     * Get the list of available channels.
     * If channels are not supported, throws an UnsupportedOperationException.
     * 
     * @return a Set of channel names.
     * 
     * @throws UnsupportedOperationException the unsupported operation exception
     */
    public Set getChannels() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("InstanceConnector does not support multiple channels");
    }    
    
}
