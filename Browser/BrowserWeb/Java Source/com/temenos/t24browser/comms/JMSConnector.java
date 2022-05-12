package com.temenos.t24browser.comms;

import java.io.Serializable;
import java.security.Principal;
import java.util.Random;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.jbase.jremote.JRemoteException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.exceptions.ConnectionException;
import com.temenos.t24browser.request.RequestUtils;

/**
 * <p>
 * This class looks up the OFS JMS queue, sends OFS messages to T24 via
 * this queue and waits for the reply on a temporary queue.
 * </p>
 */
public class JMSConnector implements ConnectionBean, Serializable {
    private static final long serialVersionUID = 1L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JMSConnector.class);

    private final static String JMS_TYPE = "BROWSER.XML";
    
    /** The BROWSE r_ XM l_ HEADER. */
    private String BROWSER_XML_HEADER = "BROWSER.XML,,,,,,<";

    /** 'ConnectionTimeout'. */
    public static final String PARAM_CONNECT_TIMEOUT = "ConnectionTimeout";
    
    /** The request. */
    private HttpServletRequest _request;

    /** The iv parameters. */
    private PropertyManager ivParameters;

    /** The request fragment name. */
    private String requestFragmentName;
        
    /** The Host name */
    private String hostName;
     
     /** The Ip address*/
    private String ipAddr;
          
    /** The Client Ip address*/
    private String clientIpAddr;

    /** The host name tag */
    private final String isHostName = "hostName";
    
    /** Service Locator **/
    private ServiceLocator _serviceLocator = null;
    
    /** RetryCount. */
	public static final String PARAM_NO_OF_RETRIES = "RetryCount";

	 /** RetryWait. */
	public static final String PARAM_NO_OF_RETRYWAIT = "RetryWait";
    // Default Constructor
    /**
     * Instantiates a new instance connector.
     */
    public JMSConnector() {
    }

    /**
     * Instantiates a new instance connector.
     * 
     * @param context
     *            the context
     * @param ivParameters
     *            the iv parameters
     */
    public JMSConnector(HttpServletRequest request, PropertyManager ivParameters) {
        _request = request;
        this.ivParameters = ivParameters;
        ipAddr = RequestUtils.getRequestIpAddress(request);
        // The client Ip address
        clientIpAddr=RequestUtils.getRequestClientIpAddress(request);
        hostName =  RequestUtils.getRequestHostName(request);
        String reqHostName = ivParameters.getParameterValue(isHostName);
        this._serviceLocator = new ServiceLocator();
        if(!reqHostName.equalsIgnoreCase("no"))
        {     
        	// Get the session object to store and retrieve attributes
        	HttpSession session = request.getSession();
        	ipAddr = (String)session.getAttribute("ipAddr");
        	hostName = (String)session.getAttribute("hostName");
        	// One attribute check is enough to ensure there is no stored attribute exists in session
        	if ((ipAddr == null) || ipAddr.equals("")) 
        	{
        		// Retrieve the details from request and store in session for later retrieval.
        		ipAddr = RequestUtils.getRequestIpAddress(request);
        		session.setAttribute("ipAddr", ipAddr);
        	}if (reqHostName.equalsIgnoreCase("IP")) {
        		BROWSER_XML_HEADER = "<CLIENTIP>" + clientIpAddr + "</CLIENTIP>" + "BROWSER.XML,,,,,,<";
        	} else if(reqHostName.equalsIgnoreCase("hostName")) {
        		if (hostName.equals("") || (hostName == null)) {
        			hostName = RequestUtils.getRequestHostName(request);
        			session.setAttribute("hostName", hostName);
        		}
    			BROWSER_XML_HEADER = "<hostName>" + hostName + "</hostName>" + "BROWSER.XML,,,,,,<";
        	} else if(reqHostName.equalsIgnoreCase("default")) {
        		if (ipAddr.equals("") || (ipAddr == null) || hostName.equals("") || (hostName == null)) {
        			ipAddr = RequestUtils.getRequestIpAddress(request);
            		hostName = RequestUtils.getRequestHostName(request);
            		session.setAttribute("ipAddr", ipAddr);
        			session.setAttribute("hostName", hostName);
        		}
        		BROWSER_XML_HEADER = "<CLIENTIP>" + clientIpAddr + "</CLIENTIP>" + "<hostName>" + hostName + "</hostName>" + "BROWSER.XML,,,,,,<";
        	}			 
		} else {
            BROWSER_XML_HEADER = "BROWSER.XML,,,,,,<";
        }        
        }

    // Sets-up a connection to the server
    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#setupServer(java.lang.String,int)
     */
    public BrowserResponse setupServer(String sInstanceName, int timeOutSecs) {
        BrowserResponse myResponse = new BrowserResponse(ivParameters);
        myResponse.setMsg("");
        return myResponse;
    }

    /*
     * Default implementation - clientIP discarded
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
     */
    public BrowserResponse talkToServer(String xmlString, String clientIP)throws ConnectionException {
        return sendMessage(xmlString, BROWSER_XML_HEADER, null);
    }

    /*
     * Method not required - principal available via container security
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
     *      java.lang.String, java.security.Principal)
     */
    public BrowserResponse talkToServer(String xmlString, String clientIP, Principal principal) throws ConnectionException {
        return sendMessage(xmlString, BROWSER_XML_HEADER, principal);
    }

    /*
     * Default implementation - clientIP discarded
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,java.lang.String)
     */
    public BrowserResponse talkToServerOfs(String ofs, String clientIP) throws ConnectionException {
        return sendMessage(ofs, "", null);
    }

    /*
     * Method not required - principal available via container security
     * 
     * @see com.temenos.t24browser.comms.ConnectionBean#talkToServer(java.lang.String,
     *      java.lang.String, java.security.Principal)
     */
    public BrowserResponse talkToServerOfs(String ofs, String clientIP, Principal principal) throws ConnectionException {
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
     * @throws JRemoteException
     */
    private BrowserResponse sendMessage(String msg, String header, Principal principal) throws ConnectionException {
        BrowserResponse myResponse = new BrowserResponse(ivParameters);
        String strOFSRequest = header + msg;
        ServiceLocator serviceLocator = null;
        ConnectionFactory cxf = null;
        Connection connection = null;
        Session session = null;       

      //Obtain the username from the principal
        String t24principal = "";
        if(principal != null) {
           t24principal = principal.getName();
        }


        // JMS connection
        MessageConsumer receiver = null;
        MessageProducer producer = null;
        try {
            long started = System.currentTimeMillis();

            // lookup JMS factory and create connection to queue
            //serviceLocator = ServiceLocator.getInstance();
            //cxf = serviceLocator.lookupJMSConnectionFactory();
            cxf = this._serviceLocator.lookupJMSConnectionFactory();
            connection = cxf.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();

            Destination replyQueue = null;
            // getting this system property is for debugging only
            if (System.getProperty("use.jms.temp.queue") != null) {
                /*
                 * Create a queue for reply messages, this is destroyed when
                 * we close the session
                 */
                replyQueue = session.createTemporaryQueue();
                receiver = session.createConsumer(replyQueue);
            } else {
                //replyQueue = serviceLocator.lookupOFSReplyDestination();
            	replyQueue = this._serviceLocator.lookupOFSReplyDestination();
            }

            /*
             * Lookup our BrowserML queue, create producer
             */
            //Destination destination = serviceLocator.lookupOFSDestination();
            Destination destination = this._serviceLocator.lookupOFSDestination();
            producer = session.createProducer(destination);
            
            /*
             * Send our OFS request to the destination
             */
            Message jmsMsg = session.createTextMessage(strOFSRequest);
            jmsMsg.setJMSReplyTo(replyQueue);
            // make sure messages get an id
            producer.setDisableMessageID(false);
            /* 
             * WARNING - The mechanism of simply letting the consumer set the correlation id to
             * the value of the message id is not portable.  We must generate and set our own 
             * unique correlation id.  The JMS api states that the message id is an implementation 
             * specific detail that may change.  A quick search of the web shows it does change in 
             * Weblogic depending on the state of the message.  
             */
            String correlationId = getUniqueCorrID();
            jmsMsg.setJMSCorrelationID(correlationId);
            
            //Set the user ID
            jmsMsg.setStringProperty("T24_PRINCIPAL", t24principal);
            
            /* 
             * Set a message type - this may give the consumer the opportunity to
             * decide whether it wants to consume this type of message.
             */
            jmsMsg.setJMSType(JMS_TYPE);
            producer.send(jmsMsg);
        
            // Get the timeout value from browserParameters
            long ConnectionTimeout = 60000L; // Default timeout is 60 seconds
            try {
            	ConnectionTimeout = Long.parseLong(ivParameters.getParameterValue(PARAM_CONNECT_TIMEOUT)) * 1000;
            } catch (RuntimeException e) { // Could be NullPointer or NumberFormat exception, both runtime.
            	LOGGER.error("Invalid ConnectTimeout value specified, using default 60 seconds. Check browserParameters.xml -> "
            			+ PARAM_CONNECT_TIMEOUT + " value. " + e.getMessage());
            }
            
         // Wait for the response message
			long timeInMillis = System.currentTimeMillis();
			int retries = 0;
			int retryWait = getRetryWait() * 1000; //get retry wait time from Browserparameters.xml
            boolean done = false;
            Message reply = null;
        	while(!done && (System.currentTimeMillis() - timeInMillis < ConnectionTimeout))	{
	            try {
	                /*
	                 * Create a receiver that knows the consumer will reply with a correlation ID
	                 * that is equal to our message ID. A JMS connection error will close this consumer
	                 */
	                receiver = session.createConsumer(replyQueue, "JMSCorrelationID='" + correlationId + "'");

	            	//Wait for a response message to arrive
	            	reply = receiver.receive(ConnectionTimeout);
	                done = true;
	            }
	            catch(JMSException je) {
					retries++;
		            LOGGER.warn("JMS connection has been lost due to [" + retries + "# re-connection attempt]: " + je.getMessage());
		            if(retries > 1) {
				        try {	//Sleep 2 seconds to give other requests a chance to get processed
			        	    Thread.sleep(retryWait);
				        } catch (InterruptedException e2) {
							LOGGER.error("Unable to pause execution after connection error: " + e2.getMessage());
				        }
		            }
			        
	            	//Reconnect
	            	try {
		            	try {
		            		connection.close();
		            	}
		            	catch(JMSException je2) {
		            		//Ignore this since consumer may already be closed at this point
		            	}
		                cxf = serviceLocator.lookupJMSConnectionFactory();
		                connection = cxf.createConnection();
		                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		                connection.start();
		                replyQueue = serviceLocator.lookupOFSReplyDestination();
			            LOGGER.info("A new JMS connection has been created. Resuming message consumption from reply queue.");
	            	}
	            	catch(JMSException je2) {
			            LOGGER.error("Unable to re-establish a JMS connection [" + retries + "# re-connection attempt]\n" + je.getMessage());
	            	}
	            }
        	}	//while
        	
        	if (reply != null && reply instanceof TextMessage) {
                String strOFSMLResponse = ((TextMessage) reply).getText();
    			DebugUtils utils = new DebugUtils(ivParameters);
    			utils.writeRequestXML( strOFSRequest, _request);
    			strOFSMLResponse = utils.preprocessT24Xml(strOFSMLResponse, requestFragmentName, _request);
                strOFSMLResponse = utils.addMenuProperty(strOFSMLResponse);            
                // set the OFS response
                myResponse.setMsg(strOFSMLResponse);
            } else {
            	//if product is arc-ib then display meaningful error message to the user.
    			String arcIb = ivParameters.getParameterValue("Product");
    			if ( (arcIb!=null) && (arcIb.equals("ARC-IB")) ) 
    			{
    				// Customised error message when T24 is unavailable
    				try {
    					Locale locale=_request.getLocale();
    				 	ResourceBundle labels = ResourceBundle.getBundle("errorMessages",locale);
    				    String value = labels.getString("error.connectionTimeOut");
    				    myResponse.setError(value);
    				}
    				catch(Exception e) {
    					LOGGER.error(e.getMessage()); 
    				}
    			}
    			else
    			{
    				String errMsg = "Connection timeout or invalid message type returned from JMS queue.";
    				LOGGER.error(errMsg);
    				myResponse.setError(errMsg);
    				myResponse.setMsg(strOFSRequest);
    			}
              }

            long elapsed = System.currentTimeMillis() - started;
            myResponse.setOfsTime(elapsed);
            //Close JMS connection
            closeResource(session);
            closeResource(connection);     
        } catch (JMSException e) {
        	closeResource(session);
            closeResource(connection);
          //Throw connection exception to trigger re-sending the OFS request
            throw new ConnectionException(e.getMessage());
        }

        return myResponse;
    }

    protected String getUniqueCorrID() {
        /* 
         * Using the session id is probably pretty good.  It is going to be guaranteed 
         * unique in the cluster.  We just need to add a random string as more than one
         * request could come from the same browser - provided the request object is not
         * recycled faster than a millisecond we won't get the same seed and all is jubbly.
         */
        Random random = new Random(System.currentTimeMillis() + _request.hashCode());
        return _request.getSession().getId() + Long.toHexString(random.nextLong());
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
    public Set<?> getChannels() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("JMSConnector does not support multiple channels");
    }

    private void closeResource(Connection cx) {
        try {
            if (cx != null) {
                cx.close();
            }
        } catch (JMSException e) {
            /*
             * ignore an exception on close, we do not want to hide the real
             * exception
             */
        }
    }
    
    private void closeResource(Session s) {
        try {
            if (s != null) {
                s.close();
            }
        } catch (JMSException e) {
            /*
             * ignore an exception on close, we do not want to hide the real
             * exception
             */
        }
    }

}
