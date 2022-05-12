package com.temenos.t24browser.comms;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.exceptions.ConnectionException;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tsdk.foundation.T24Connection;
import com.temenos.tsdk.xml.XmlUtilities;

// TODO: Auto-generated Javadoc
/**
 * Provides a connection to T24 from the Web server.
 */
public class T24WebConnection implements T24Connection
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(T24WebConnection.class);
	
	/** The iv connection. */
	private ConnectionBean ivConnection;
	
	/** The iv token. */
	private String ivToken;
	
	/** The iv company id. */
	private String ivCompanyId;
	
	/** The iv client ip. */
	private String ivClientIp;


	/**
	 * Instantiates a new t24 web connection.
	 * 
	 * @param connection the connection
	 * @param clientIp the client ip
	 */
	public T24WebConnection(ConnectionBean connection, String clientIp)
	{
		ivConnection = connection;
		ivClientIp = clientIp;
	}

	/* (non-Javadoc)
	 * @see com.temenos.tsdk.foundation.T24Connection#talkToServer(java.lang.String)
	 */
	public String talkToServer(String xmlRequest)
	{
		try
		{
			BrowserResponse browserResponse;
			browserResponse = this.ivConnection.talkToServer(xmlRequest, ivClientIp);
			
			// Checks and changes the path of XSL file which is referenced in OFS message
			// to the /transforms/...
			DebugUtils.checkXslPath(browserResponse);

			XmlUtilities util = new XmlUtilities();
			String sToken;
			sToken = util.getNodeFromString(browserResponse.getMsg(), "token");

			this.setRequestToken(sToken);

			return browserResponse.getMsg();
		}
		catch (ConnectionException ce)
		{
			LOGGER.info(ce.getMessage());
			return null;
		}
		
		catch (TBrowserException tex)
		{
			LOGGER.info(tex.getMessage());
			return null;
		}
	}

	/**
	 * Sets the token that will be used in the request.
	 * 
	 * @param token the token
	 */
	public void setRequestToken(String token)
	{
		if (token != null)
			this.ivToken = token;
	}

	/**
	 * Gets the token that will be used in the request.
	 * 
	 * @return String
	 */
	public String getRequestToken()
	{
		return this.ivToken;
	}
	
	/**
	 * Sets the company id that will be used in the request.
	 * 
	 * @param company
	 */
	
	public void setRequestCompany(String sCompany) 
	{
		if (sCompany != null)
			this.ivCompanyId = sCompany;
	}

	/**
	 * Gets the company id that will be used in the request.
	 * 
	 * @return String
	 */
	 public String getCompanyName()
	 {
        return this.ivCompanyId;
	 }
	
}
