package com.temenos.t24browser.ofs;

import java.util.Hashtable;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * Class dealing with OFS requests from T24
 */

public class OfsResponse
{
	/** The iv transaction id. */
	private String ivTransactionId = "";
	
	/** The iv message id. */
	private String ivMessageId = "";
	
	/** The iv ofs result. */
	private int ivOfsResult = OFS_SUCCESS;
	
	/** The iv response data. */
	private String ivResponseData = "";
	
	/** The iv errors. */
	private Hashtable ivErrors = new Hashtable();
		
	/** The Constant COMPONENT_NAME. */
	private static final String COMPONENT_NAME = "OfsResponse : ";	// Used for logging
	
	// Ofs Status's
	/** The Constant OFS_SUCCESS. */
	public static final int OFS_SUCCESS = 1;		// Successful response
	
	/** The Constant OFS_ERRORS. */
	public static final int OFS_ERRORS = -1;		// Failure response - errors
	
	/** The Constant OFS_OVERRIDES. */
	public static final int OFS_OVERRIDES = -2;		// Failure response - overrides
	
	/** The Constant OFS_OFFLINE. */
	public static final int OFS_OFFLINE = 3;		// Offline response
	
	
	/** The Constant OFS_PART_DELIMITER. */
	private static final String OFS_PART_DELIMITER = "/";					// Delimiter for OFS strings
	
	/** The Constant OFS_DATA_DELIMITER. */
	private static final String OFS_DATA_DELIMITER = ",";					// Delimiter for OFS response data
	
	/** The Constant OFS_FIELD_DATA_DELIMITER. */
	private static final String OFS_FIELD_DATA_DELIMITER = "=";				// Delimiter for OFS field data

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsResponse.class);
	
		
	/**
	 * Instantiates a new OFS response.
	 * 
	 * @param sOfsResponse The OFS response
	 */
	public OfsResponse( String sOfsResponse )
	{
		// If the OFS response contains a failure then parse the errors/overrides
		parseResponse( sOfsResponse, true );
	}

	/**
	 * Instantiates a new OFS response.
	 * 
	 * @param sOfsResponse The OFS response
	 * @param validResponse Whether the response is valid or not
	 */
	public OfsResponse( String sOfsResponse, boolean validResponse )
	{
		// If the OFS response contains a failure then parse the errors/overrides
		parseResponse( sOfsResponse, validResponse );
	}
	
	// Returns the success/fail flag of the response
	/**
	 * Gets the response status.
	 * 
	 * @return the response status
	 */
	public int getResponseStatus()
	{
		return( ivOfsResult );
	}
	
	// Returns the errors in the response
	/**
	 * Gets the errors.
	 * 
	 * @return the errors
	 */
	public Hashtable getErrors()
	{
		return( ivErrors );
	}
	
	
	// Parse the OFS string setting the instance variables accordingly
	/**
	 * Parses the response.
	 * 
	 * @param sResponse the s response
	 */
	private void parseResponse( String sResponse, boolean validResponse )
	{
		if ( validResponse )
		{
			if ( ( sResponse != null ) && ( ! sResponse.equals("") ) )
			{
				// Get all the header information up to the first "," character
				String header = "";
				String body = "";
				int pos = sResponse.indexOf( OFS_DATA_DELIMITER );
				
				if ( pos == -1 )
				{
					// Invalid response
					ivOfsResult = OFS_ERRORS;
					return;
				}
				
				header = sResponse.substring(0 , pos);
				body = sResponse.substring(pos+1 , sResponse.length());
				String[] headerParts = header.split( OFS_PART_DELIMITER );
				
				for ( int i = 0; i < headerParts.length; i++ )
				{
					if ( i == 0 )
					{
						ivTransactionId = headerParts[i];
					}
					else if ( i == 1 )
					{
						ivMessageId = headerParts[i];
					}
					else if ( i == 2 )
					{
						String[] responseData = headerParts[i].split( OFS_DATA_DELIMITER );
						
						ivOfsResult = Integer.parseInt( responseData[0] );
						
						switch( ivOfsResult )
						{
							case OFS_SUCCESS :
								// Just return as everything was OK
								return;
								
							case OFS_ERRORS :
								processErrors( body );
								break;
								
							case OFS_OVERRIDES :
								// Overrides processed as normal in XMLBulkResponse
								break;
								
							default :
								// Offline, ignore it
								break;
						}
		
					}
				}
			}
			else
			{	
				// Invalid empty response
				ivOfsResult = OFS_ERRORS;
			}
		}
		else
		{
			ivOfsResult = OFS_ERRORS;
		}
	}
	
	// Process the errors and put then in an array
	/**
	 * Process errors.
	 * 
	 * @param responseData the response data
	 */
	private void processErrors( String responseData )
	{
		String[] fields = responseData.split( OFS_DATA_DELIMITER );
		
		for ( int i = 0; i < fields.length; i++ )
		{
			// Split the field data in to a field name and the value
			String[] fieldData = fields[i].split( OFS_FIELD_DATA_DELIMITER );
			
			int fieldParts = fieldData.length ;
			String fieldName = "";
			String fieldValue = "";
			
			switch( fieldParts )
			{
				case 1 :
					// Just a field name
					fieldName = fieldData[0];
					fieldValue = "";
					ivErrors.put( fieldName, fieldValue );
					break;
					
				case 2 :
					// Field name and value
					fieldName = fieldData[0];
					fieldValue = fieldData[1];
					ivErrors.put( fieldName, fieldValue );
					break;

				default :
					// Invalid syntax so don't add anything
					break;
			}
		}
	}
}
