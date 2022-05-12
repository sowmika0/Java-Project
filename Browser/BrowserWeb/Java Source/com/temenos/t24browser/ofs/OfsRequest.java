package com.temenos.t24browser.ofs;

import java.util.Enumeration;
import java.util.Hashtable;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.request.T24Request;


/**
 * Class dealing with OFS responses from T24
 */

public class OfsRequest
{
	/** The Constant COMPONENT_NAME. */
	private static final String COMPONENT_NAME = "OfsRequest : ";	// Used for logging
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OfsResponse.class);
	
	private OfsServerConfig ivOfsConfig = null;
	
	/** Default OFS strings. **/
	private static final String DEFAULT_FUNCTION = "I";
	private static final String DEFAULT_PROCESS_FLAG = "PROCESS";
	private static final String DEFAULT_ID = "";			// OFS will treat blank as a F3 (new record)
	private static final String VERSION_DELIMITER = ",";
	
	/** OFS strings for a request. **/
	private String ivApplication = "";
	private String ivVersion = "";
	private String ivFunction = "";
	private String ivProcessFlag = "";
	private String ivUser = "";
	private String ivPassword = "";
	private String ivId = "";
	private Hashtable ivData = null;
	
		
	/**
	 * Instantiates a new OFS request supplying the full set of parameters.
	 * 
	 * @param config The OFS Server Config
	 * @param sApplication The application name
	 * @param sVersion The version name
	 * @param sFunction The function 
	 * @param sProcessFlag The process flag
	 * @param sUser The user name
	 * @param sPassword The password
	 * @param sId The record Id
	 * @param data The field data
	 * 
	 */
	public OfsRequest( OfsServerConfig config, String sApplication, String sVersion, String sFunction, String sProcessFlag, String sUser, String sPassword, String sId, Hashtable data )
	{
		ivOfsConfig = config;
		
		ivApplication = sApplication;
		ivVersion = sVersion;
		ivFunction = sFunction;
		ivProcessFlag = sProcessFlag;
		ivUser = sUser;
		ivPassword = sPassword;
		ivId = sId;
		ivData = data;
		
		defaultOfsData();
	}

	/**
	 * Instantiates a new OFS request supplying a sub-set of parameters.
	 * 
	 * @param config The OFS Server Config
	 * @param sUser The user name
	 * @param sPassword The password
	 * @param data The field data
	 * 
	 */
	public OfsRequest( OfsServerConfig config, String sUser, String sPassword, Hashtable data )
	{
		ivOfsConfig = config;
		
		ivApplication = config.getDefaultApplication();
		ivVersion = config.getDefaultVersion();
		ivFunction = DEFAULT_FUNCTION;
		ivProcessFlag = DEFAULT_PROCESS_FLAG;
		ivUser = sUser;
		ivPassword = sPassword;
		ivId = DEFAULT_ID;
		ivData = data;
		
		defaultOfsData();
	}
	
	/**
	 * Instantiates a new OFS request supplying a T24 Request object.
	 * 
	 * @param request The T24 Request object
	 * @param sUser The user name
	 * @param sPassword The password
	 * 
	 */
	public OfsRequest( OfsServerConfig config, T24Request request, String sUser, String sPassword )
	{
		ivOfsConfig = config;
		
		ivApplication = request.getParameterValue("application");
		ivVersion = request.getParameterValue("version");
		ivFunction = request.getParameterValue("ofsFunction");
		ivProcessFlag = request.getParameterValue("ofsOperation");
		ivUser = sUser;
		ivPassword = sPassword;
		ivId = request.getParameterValue("transactionId");
		ivData = request.getFieldData();
		
		defaultOfsData();
	}
	
	/**
	 * Returns an OFS Request string.
	 * 
	 * @return String OFS Request String
	 * 
	 */
	public String getRequest()
	{
		// OFS Requests are in the form :-
		// OPERATION,OPTIONS,USER INFORMATION,ID INFORMATION,DATA
		// For example :-
		// HELPTEXT.MENU,OFS.DEMO/I/PROCESS,TEST.USER/654321,OFS.TEST,APPLICATION:1:=SECTOR,DESCRIPT:1:=Sector,APPLICATION:2:=INDUSTRY,DESCRIPT:2:=Industry,DESCRIPT:2:2=Industrie

		String sRequest = "";
		
		// Add the header information
		sRequest += ivApplication + "," + ivVersion + "/" + ivFunction + "/" + ivProcessFlag + "," + ivUser + "/" + ivPassword + "," + ivId + ",";
		
		// Add the fields
		Enumeration  fields = ivData.keys();
		
		while ( fields.hasMoreElements() )
		{
			String fieldName = (String) fields.nextElement();
			String fieldValue = (String) ivData.get( fieldName );
			fieldName = fieldName.substring(10);	// Remove the 'fieldName:' prefix
			sRequest += fieldName + "=" + fieldValue + ",";
		}
	
		return sRequest;
	}
	
	/**
	 * Returns the OFS Request object as a String.
	 * 
	 * @return String OFS Request in string format
	 */
	public String toString()
	{
		String sOfsReq = " *** OFS Request Data *** \n";
		sOfsReq += "      - Application : " + this.ivApplication + "\n";
		sOfsReq += "      - Version : " + this.ivVersion + "\n";
		sOfsReq += "      - Function : " + this.ivFunction + "\n";
		sOfsReq += "      - Process Flag : " + this.ivProcessFlag + "\n";
		sOfsReq += "      - User : " + this.ivUser + "\n";
		sOfsReq += "      - Password : " + this.ivPassword + "\n";
		sOfsReq += "      - Id : " + this.ivId + "\n";
		sOfsReq += "      - Field Data : " + this.ivData.toString() + "\n";

		return sOfsReq;
	}
	
	private void defaultOfsData()
	{
		// If any of the OFS data is null then default in the values from the config file
		// User, password and field data must have been set correctly
		
		if ( ( ivApplication == null ) || ( ivApplication.equals("") ) )
		{
			ivApplication = ivOfsConfig.getDefaultApplication();
		}
		
		if ( ( ivVersion == null ) || ( ivVersion.equals("") ) )
		{
			ivVersion = ivOfsConfig.getDefaultVersion();
		}
		
		// Remove any ',' character from the version name
		if ( ivVersion.contains( VERSION_DELIMITER ) )
		{
			ivVersion.replaceAll( VERSION_DELIMITER, "" );
		}
		
		if ( ( ivFunction == null ) || ( ivFunction.equals("") ) )
		{
			ivFunction = DEFAULT_FUNCTION;
		}
		
		if ( ( ivProcessFlag == null ) || ( ivProcessFlag.equals("") ) )
		{
			ivProcessFlag = DEFAULT_PROCESS_FLAG;
		}
		
		if ( ( ivId == null ) || ( ivId.equals("") ) )
		{
			ivId = ivOfsConfig.getDefaultTransactionId();
			
			if ( ( ivId == null ) || ( ivId.equals("") ) )
			{
				ivId = DEFAULT_ID;
			}
		}
	}
}
