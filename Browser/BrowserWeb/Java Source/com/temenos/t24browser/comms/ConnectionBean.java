////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   ConnectionBean
//
//  Description   :   Bean Interface for controlling connections for OFS requests 
//					  between the Browser Servlet and the server.
//
//  Modifications :
//
//    05/07/02   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.comms;

import java.security.Principal;
import java.util.Set;

import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.t24browser.exceptions.ConnectionException;

public interface ConnectionBean
{
	// Sets-up a connection to the server
	public BrowserResponse setupServer( String serverIPAddress, int serverPortNo ) throws TBrowserException;
	
	// Sends an XML request to the server from the specified client IP and returns the reply
	public BrowserResponse talkToServer( String xmlRequest, String clientIP )  throws TBrowserException, ConnectionException;
	
	
	// Sends an XML request to the server from the specified client IP in secure mode and returns the reply
	public BrowserResponse talkToServer( String xmlRequest, String clientIP, Principal principal ) throws TBrowserException, ConnectionException;

	// Sends an XML request to the server from the specified client IP and returns the reply
	public BrowserResponse talkToServerOfs( String xmlRequest, String clientIP )  throws TBrowserException, ConnectionException;
	
	// Sends an XML request to the server from the specified client IP in secure mode and returns the reply
	public BrowserResponse talkToServerOfs( String xmlRequest, String clientIP, Principal principal ) throws TBrowserException, ConnectionException; 
    
    /** 
     * Get the list of available channels.
     * If channels are not supported, throws an UnsupportedOperationException.
     * @return a Set of channel names.
     */
    public Set<?> getChannels() throws UnsupportedOperationException;
    /**
     * Get the timeout value specifying the number of seconds
     * to wait for a response.
     * @return timeout in seconds
     */
    public long getTimeout();
    /**
     * Get the no of times to retry to establish a connection and  
     * wait for a response message.
     * @return timeout in seconds
     */  
    public int getRetryCount();
    /**
     * Get the no of times to wait to establish a connection and  
     * wait for a response message.
     * @return timeout in seconds
     */  
    public int getRetryWait();  
}
