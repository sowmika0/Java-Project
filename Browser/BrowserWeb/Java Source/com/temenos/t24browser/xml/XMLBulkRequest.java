////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XmlBulkRequest
//
//  Description   :   Class that deals with HTTP Requests that contain a number
//					  sub-requests.
//					  This class takes multiple requests and splits them out
//					  before converting them in to a single XML document for
//					  transmission to T24.
//					  
//					  The input XML document will contain multiple <response> tags
//					  in the form :-
//							<responses><response><.......></response></response>
//
//					  The output XML document will put each response tag in to a
//					  <pane> tag within a standard Browser response.
//					  The duplicate window tags (e.g. window coordinates, 
//					  translations, etc) will be removed .
//					  The merged output XML document will be in the form :-
//							<ofsSessionResponse><...><pane></pane><pane></pane><...></ofsSessionResponse>
//
//  Modifications :
//
//    14/02/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.xml;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.temenos.t24browser.request.RequestData;
import com.temenos.t24browser.request.T24Request;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLBulkRequest.
 */
public class XMLBulkRequest extends XMLBulk
{
	
	/** The Constant COMPONENT_NAME. */
	private static final String COMPONENT_NAME = "XMLBulkRequest : "; // Used for logging
	
	/** The iv request. */
	private T24Request ivRequest = null;			// The bulk request
	
	/** The iv app list. */
	private ArrayList ivAppList = null;				// List of applications in the request
	
	/** The iv xml requests. */
	private ArrayList ivXmlRequests = null;			// List of requests in XML format
	
	/** The iv xml manager. */
	private XMLRequestManager ivXmlManager = null;	// A XML manager for the XML parser
	
		
	/**
	 * Instantiates a new XML bulk request.
	 * 
	 * @param request the request
	 * @param templates the templates
	 */
	public XMLBulkRequest( T24Request request, XMLTemplateManager templates )
	{
		// If the request contains multiple requests, then we
		// need to split the requests and produce an XML request
		// for each sub-request.
		// The XML requests are then bundled together to be sent
		// to T24
		ivRequest = request;
		setApplicationList();
		ivXml = processRequests( templates );
	}
	
	
	/**
	 * Returns a list of application names in the request.
	 * 
	 * @return String
	 */	
	private void setApplicationList()
	{
		String appList = ivRequest.getParameterValue( XMLConstants.XML_REQUEST_APP_LIST );
		ivAppList = new ArrayList();
		
		StringTokenizer tokens = new StringTokenizer( appList, ":" );

		while ( tokens.hasMoreTokens() )
		{
			ivAppList.add( tokens.nextToken() );
		}
	}
	
	/**
	 * Returns the merged responses in a single XML document.
	 * 
	 * @param sAppName the s app name
	 * 
	 * @return String
	 */	
	private RequestData getAppParameters( String sAppName )
	{
		// Get the parameters for this application, 
		// removing the application prefix as we go
		return( ivRequest.getMatchingParameters( sAppName, true ) );
	}
				
	/**
	 * Returns the XML parser manager used to create requests in XML format.
	 * 
	 * @return String
	 */	
	public XMLRequestManager getRequestManager()
	{
		return( ivXmlManager );
	}
	
	/**
	 * Processes each of the requests, converting in to XML format.
	 * 
	 * @param templates The XML request templates
	 * 
	 * @return String
	 */
	private String processRequests( XMLTemplateManager templates )
	{
		String sRequestsXml = "";
		sRequestsXml += XMLConstants.XML_REQUESTS_TAGGED;

		// Process each application in the list.
		// Get the parameters for the application and call teh parser for that request.
		// Add each request XML to the result inside the request tags.
		for ( int i = 0; i < ivAppList.size(); i++ )
		{
			String appName = (String) ivAppList.get(i);
			String reqXml = "";
			RequestData params = getAppParameters( appName );
			XMLRequestManager xmlManager = null;
			
			try
			{
				T24Request appReq = new T24Request( params );
				appReq.setAttributes( ivRequest.getAttributes() );
				
				// Save the XML parser manager for the first request as we will
				// need this later to process the response
				xmlManager = new XMLRequestManager( appReq, templates );
				
				if ( i == 0 )
				{
					ivXmlManager = xmlManager;
				}
				
				reqXml = xmlManager.getXMLResponse();
				
				if ( ( reqXml != null ) && ( !reqXml.equals("") ) )
				{
					sRequestsXml += XMLConstants.XML_REQUEST_TAGGED + reqXml + XMLConstants.XML_REQUEST_TAGGED_C;
				}
			}
			catch (XMLRequestTypeException e)
			{
				e.printStackTrace( );
			}
		}
		
		sRequestsXml += XMLConstants.XML_REQUESTS_TAGGED_C;
		
		return( sRequestsXml );
	}
}
