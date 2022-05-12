////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   RequestUtils
//
//  Description   :   Provides utilities for Browser Requests.
//
//  Modifications :
//
//    26/04/05   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.request;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.temenos.t24browser.xml.XMLConstants;

import java.net.InetAddress;


// TODO: Auto-generated Javadoc
/**
 * The Class RequestUtils.
 */
public class RequestUtils implements Serializable
{
 	/* Variable for Client ip Open tag */
	private final static String clientIp = "<CLIENTIP>";
	/* Variable for Client ip Close tag */
	private final static String clientIp_c = "</CLIENTIP>";
	/* Variable for Client Host name Open tag */
	private final static String clientHostName = "<hostName>";
	/* Variable for Client Host name close tag */
	private final static String clientHostName_c = "</hostName>";
	/* Variable for Browser header */
	private final static String browserHeader = "BROWSER.XML,,,,,,<";
 	
	 /**
	  * Instantiates a new request utils.
	  */
	 public RequestUtils()
	{
	}
	
	
	// Indicates whether a Http Request contains multiple requests or not.
	/**
	 * Bulk request.
	 * 
	 * @param request the request
	 * 
	 * @return true, if successful
	 */
	public static boolean bulkRequest( HttpServletRequest request )
	{
		// Check if an MultiPaneAppList parameter exists indicating that there
		// is more than one application in the request object
		String appList = request.getParameter( XMLConstants.XML_REQUEST_APP_LIST );
		
		if ( ( appList != null ) && ( !appList.equals("") ) )
		{
			return( true );
		}
		else
		{
			return( false );
		}
	}
	
	// Indicates whether an XML document contains multiple responses or not.
	/**
	 * Bulk response.
	 * 
	 * @param sResponse the s response
	 * 
	 * @return true, if successful
	 */
	public static boolean bulkResponse( String sResponse )
	{
		if ( (  sResponse == null ) || ( sResponse.equals("") ) )
		{
			return( false );
		}
		else
		{
			String bulkTags = XMLConstants.XML_RESPONSES_TAGGED + XMLConstants.XML_RESPONSE_TAGGED;
			int startPos = sResponse.indexOf( bulkTags );
			
			if ( startPos != -1 )
			{
				return( true );
			}
			else
			{
				return( false );
			}
		}
	}
	
	// Returns the client IP address.
	/**
	 * Bulk request.
	 * 
	 * @param request the request
	 * 
	 * @return Ipaddress of the client
	 */
	 
	public static String getRequestIpAddress( HttpServletRequest req )
      {
            String addr = req.getRemoteAddr();
      
            // If the client is the local host then resolve this to the real IP address         
            if ( addr.equalsIgnoreCase("127.0.0.1") )
            {
                  try
                  {
                  addr = InetAddress.getLocalHost().getHostAddress();
                  }
                  catch ( Exception e )
                  {
                        addr = req.getRemoteAddr();
                  }
            }
            
            return( addr );
      }
      
	// Returns the client IP address, when client having proxy in their network 
	public static String getRequestClientIpAddress( HttpServletRequest req )
	
	{
		
		String ipAddress = req.getHeader("X-FORWARDED-FOR");  
		   if (ipAddress == null) 
		   {
			   ipAddress = req.getRemoteAddr(); 
			   if ( ipAddress.equalsIgnoreCase("127.0.0.1") )
	            {
	                  try
	                  {
	                	  ipAddress = InetAddress.getLocalHost().getHostAddress();
	                  }
	                  catch ( Exception e )
	                  {
	                	  ipAddress = req.getRemoteAddr();
	                  }
	            }
		   }
		 return(ipAddress);
	}
    // Returns the client host name.
	/**
	 * Bulk request.
	 * 
	 * @param request the request
	 * 
	 * @return Host name of the client
	 */
      public static String getRequestHostName( HttpServletRequest req )
      {
            String hostName = req.getRemoteHost();
            
            try{
            if ( !hostName.equalsIgnoreCase("127.0.0.1") )
            {
                        hostName = InetAddress.getByName(hostName).getHostName();
                  
            } else {
                  hostName = InetAddress.getLocalHost().getHostName();
            }
            }
            catch ( Exception e )
            {
                  hostName = req.getRemoteHost();
            }
            
            return( hostName );
      }
      
      
      
      public static String setBrowserXmlHeader(String reqHostName,HttpServletRequest request)
      {
    	  	String ipAddr = getRequestIpAddress(request);
    	  	 // The Client Ip address
            String clientIpAddr=getRequestClientIpAddress(request);
			String XML_HEADER = null ;
			String hostName =null;
			if(reqHostName.equalsIgnoreCase("IP"))
			{
				XML_HEADER = clientIp + clientIpAddr + clientIp_c + browserHeader;
			} else if(reqHostName.equalsIgnoreCase("hostName")) {
				hostName =  getRequestHostName(request);
				XML_HEADER = clientHostName + hostName + clientHostName_c + browserHeader;
			} else if(reqHostName.equalsIgnoreCase("default")) {
				hostName =  getRequestHostName(request);
				XML_HEADER = clientIp + clientIpAddr + clientIp_c + clientHostName + hostName + clientHostName_c + browserHeader;
			}
			
			return XML_HEADER;	
      }

}
