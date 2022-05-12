package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.temenos.t24browser.request.RequestData;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.request.T24Request;


public class XmlResponse implements Serializable {
	// Needed to satisfy serializable interface.
	public static final long serialVersionUID = 0;
	
	public XmlResponse( String xmlResponse,T24Request ivRequest){
		
		this.requestData = new RequestData( RequestData.PARAMETER_DATA);
		
		if( RequestUtils.bulkResponse( xmlResponse))
		{	
			// Does the bulk response only have one response in it?
			if( isSingleBulkResponse( xmlResponse))
			{
				// Process single response
				this.isSingleBulkResponse = true;
			}
			// Process multiple responses
			this.responseXml = this.processBulkResponse( xmlResponse,ivRequest);
			//Key to check tab expand request
			if(ivRequest != null && ivRequest.getValue("routineName") != null)
			{
				if(ivRequest.getValue("routineName").equalsIgnoreCase("OS.ADD.NEW.TAB.CONTENT"))
				{
					this.isTabExpand=true;
				}
			}	
			// Get only the first response for unique id generation
			this.uniqueId = this.getUniqueId( Utils.getNodeFromString( xmlResponse, Constants.RESPONSE));
		}
		else if( isContract( xmlResponse))
		{	
			
			// assign perfix for OS.ADD.NEW.TAB.CONTENT
			String application = "";
			String version = "";
			String key = "";
			String fieldPrefix = "";
			String AAATxnkey = "";
			// Get key
			AAATxnkey = Utils.getNodeFromString( xmlResponse, Constants.AAA_TXN_ID);
			if (!AAATxnkey.equals(""))
			{
				application = Utils.getNodeFromString( xmlResponse, Constants.APP);
				// Get version
				version = Utils.getNodeFromString( xmlResponse, Constants.VERSION);
				// Get key
				key = Utils.getNodeFromString( xmlResponse, Constants.KEY);
				// Build the field prefix per responses
				fieldPrefix = application + version + key + ":";
					
			}
		
			// Process single response
			this.responseXml = this.createRequestData( fieldPrefix, xmlResponse);
			//Key to check tab expand request
			if(ivRequest != null && ivRequest.getValue("routineName") != null)
			{
				if(ivRequest.getValue("routineName").equalsIgnoreCase("OS.ADD.NEW.TAB.CONTENT"))
				{
					this.isTabExpand=true;
				}
			}			
			// Get the unique id from the response
			this.uniqueId = this.getUniqueId( xmlResponse);

		}
		else
		{	
			// Leave the response untouched 
			this.responseXml = xmlResponse;
		}
		//System.out.println( this.requestData );
	}
	
	public RequestData getRequestData()
	{
		return this.requestData;
	}
	
	public String getResponseXml()
	{
		return this.responseXml;
	}
	
	public String getUniqueId()
	{
		return this.uniqueId;
	}
	
	public boolean hasRequestData(){
		return this.requestData.hasData();
	}
	
	public boolean getTabExpandInfo()
	{
		return this.isTabExpand;
	}

	/**
	 * Is the response a bulk response but with only one response in it.
	 * 
	 * @param xmlResponse String The xml response back from browser.
	 * 
	 * @return true, if successful
	 */
	public boolean isSingleBulkResponse( String xmlResponse)
	{
		// Match with a pattern of "<response>...</response>" using 'reluctant' qualifier ('?')
		Pattern respPattern = Pattern.compile( Constants.RESPONSE_O + "(.*?)" + Constants.RESPONSE_C, Pattern.DOTALL );
		Matcher respMatcher = respPattern.matcher( xmlResponse );
		// Holds the number of contract responses. Not ofs responses.
		int responseCount = 0;
		while ( respMatcher.find() )
		{
			String plainXmlResponse = respMatcher.group( 1);
			if( isContract( plainXmlResponse))
			{
				responseCount++;
			}
		}
		// Return true if only one contract response	
		return ( responseCount == 1);
	}
	
// Private -------------------------------------------------------------------------------------------------------

	private String responseXml;
	private RequestData requestData;
	private String uniqueId;
	private boolean isSingleBulkResponse = false;
	private boolean isTabExpand = false;
	
	private String processBulkResponse( String bulkResponse,T24Request ivRequest)
	{
		// Match with a pattern of "<response>...</response>" using 'reluctant' qualifier ('?')
		Pattern respPattern = Pattern.compile( Constants.RESPONSE_O + "(.*?)" + Constants.RESPONSE_C, Pattern.DOTALL );
		Matcher respMatcher = respPattern.matcher( bulkResponse );
		
		// While loop variables
		String application = "";
		String version = "";
		String key = "";
		String fieldPrefix = "";
		String aResponse = "";
		String responses = "";
		int responseCounter = 1;
		String fragmentSuffix="";
		
		while ( respMatcher.find() )
		{
			aResponse = respMatcher.group( 1);
			// Get application
			application = Utils.getNodeFromString( aResponse, Constants.APP);
			// Get version
			version = Utils.getNodeFromString( aResponse, Constants.VERSION);
			// Get key
			key = Utils.getNodeFromString( aResponse, Constants.KEY);
			// Only add prefix if we are dealing with more the one contract response.
			if( this.isSingleBulkResponse)
			{
				fieldPrefix = "";
			}
			else
			{
				// Check if this application if AA.ARRANGEMENT.ACTIVITY
				if( responseCounter == 1)
				{   // Hardcode the prefix if top AA application. The rest of the code expects it this way.
					//Add the fragmentsuffix when Arrangement is part of composite screen
					fragmentSuffix = ivRequest.getParameterValue("WS_FragmentName");
					 if(fragmentSuffix == null || fragmentSuffix.equals(""))
	                 {     
						 fieldPrefix = Constants.APP_REQ + ":";
	                 }
	                 else
	                 {
	                	 fieldPrefix = Constants.APP_REQ + "_" + fragmentSuffix + ":";                           
	                 }
				}
				else
				{	
					// Build the field prefix per responses
					 if(fragmentSuffix == null || fragmentSuffix.equals(""))
	                 {     
						 fieldPrefix = application + version + key + ":";
	                 }
	                 else
	                 {
	                	 fieldPrefix = application + version +key + "_" + fragmentSuffix + ":";                           
	                 }
				}
			}
			// Remove hidden fields from the response
			aResponse = this.createRequestData( fieldPrefix, aResponse);
			// Increment
			responseCounter++;
			// Add response
			responses += Constants.RESPONSE_O + aResponse + Constants.RESPONSE_C;
		}
		
		// Patch up 
		bulkResponse = Constants.RESPONSES_O + responses + Constants.RESPONSES_C;
		
		return bulkResponse;
	}
	
	private String createRequestData( String fieldPrefix, String response)
	{
		// Create all the patterns needed
		Pattern hiddenFieldPattern = Pattern.compile( Constants.HIDDEN_ROW_O + "(.*?)" + Constants.HIDDEN_ROW_C, Pattern.DOTALL);
		// Declare the matchers to be used
		Matcher hiddenFieldMatcher = hiddenFieldPattern.matcher( response );
		// While loop variables
		String hiddenField = "";
		String fieldName = "";
		String fieldValue = "";
		// Go through the response extracting out "<hr>...</hr>" patterns and storing them in the RequestData 
		while( hiddenFieldMatcher.find() )
		{
			// Get the next hidden field
			hiddenField = hiddenFieldMatcher.group( 1);
			// Get the field name of the hidden field
			fieldName = fieldPrefix + "fieldName:" + Utils.getNodeFromString( hiddenField, Constants.FIELD_NAME);
			// Get the value of the hidden field
			fieldValue = Utils.getNodeFromString( hiddenField, Constants.FIELD_VALUE);
			// Only update request/response if there exists a fieldName, otherwise something is wrong so stay immute.
			if( fieldName != "")
			{
				//decode the html escape sequence characters in field value
				fieldValue = Utils.decodeHtmlEntities(fieldValue);
				// Add the name value pair to the request data
				this.requestData.addDataItem( fieldName, fieldValue);
				//Remove the hidden field from the xml response
				response = Utils.removeNodeFromString( response, Constants.HIDDEN_ROW);
			}	
		}
		return response;
	}
	
	private boolean isContract( String response)
	{
		String application = Utils.getNodeFromString( response, Constants.APP);
		return ( !application.equals( ""));
	}
	
	private String getUniqueId( String response)
	{
		if(getTabExpandInfo())
		{
			//Generate key similar to whole contract for tab expand
			String company = Utils.getNodeFromString( response, Constants.COMPANY);
			String user = Utils.getNodeFromString( response, Constants.USER);
			String application = Utils.getNodeFromString( response, Constants.APP);
			if(application.contains("AA.SIM."))
			{
				application = Constants.AA_SIMULATION_CAPTURE;
			} else if (application.contains("AA.ARR."))
			{
				application = Constants.AA_ARRANGEMENT_ACTIVITY;
			}			
			String screenMode = Utils.getNodeFromString( response, Constants.SCREEN_MODE);
			String key = Utils.getNodeFromString( response, Constants.AAA_TXN_ID);
			return company + "_" + user + "_" + application + "_" + screenMode + "_" + key;
			
		} else {		
			String company = Utils.getNodeFromString( response, Constants.COMPANY);
			String user = Utils.getNodeFromString( response, Constants.USER);
			String application = Utils.getNodeFromString( response, Constants.APP);
			String screenMode = Utils.getNodeFromString( response, Constants.SCREEN_MODE);
			String key = Utils.getNodeFromString( response, Constants.KEY);			
			// Set the id for this response
			return company + "_" + user + "_" + application + "_" + screenMode + "_" + key;
		}
	}

}
