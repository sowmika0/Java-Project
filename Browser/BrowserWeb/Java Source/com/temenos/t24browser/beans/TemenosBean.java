////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   TemenosBean
//
//  Description   :   Bean for controlling OFS requests between the Browser
//					  Servlet and the server.
//
//  Modifications :
//
//    17/06/02   -    Initial Version - taken from BrowserServlet.
//	  03/07/02	 -	  Implement GlobusBean.
//	  07/08/02	 -	  Use web address for SERLET.CONTEXT substitution.
//	  09/09/02	 -	  Use messages.xsl for errors.
//    15/11/02   -    Removed use of replacing imstances of SERVLET.CONTEXT and
//						WEB.SERVER.ADDRESS - now solved in XSLs and JSPs.
//	  15/11/02   -    Added parameter to determine whether to transform on web server.
//	  21/11/02   -    Log Event On/Off option
//    22/11/02   -    Compiled Stylesheet
//    02/01/03   -    Allow processing of the quickguide command.
//    17/01/03   -    Process connection errors correctly using skins.
//    08/07/03   -    Changed the signature call to the constructor
//						Now passes in an XMLTemplateManager as well.
//					  Added get method to return the XMLTemplateManager
//	  10/09/03   -    Added the new method setHomeDirectory.  This works out if the
//					    xsl name passed to transform the xml with, has a pre directory structure
//						If it does, then adds it to the variable xslHomeDirectory.
//                      This is then passed into the XT transformer.
//    06/11/03   -    Added the new method "getContextRootName()" returns the first
//						directory name in the context path
//					  Added a call to XMLRequestManager to add the node "contextRoot" to the returned
//                      xml response before transformation
//                    No longer need to add the XML.HEADER the the front of the xml before transform.
//						This is now done automatically by the XMLRequestManager - passing it through DOM
//    03/06/04   -    Added a slash to the start of the download path name.
//    01/06/05   -    EJB and Socket connection methods no longer supported.
//	  03/04/06     -  ivLoggedIn boolean flag has been stored on the HTTP Sesssion.following get and set
//					  methods has been added :  setLoggedIn ; getLoggedIn()r
//    21/08/06			IvResponseTxt, IvResponseType variables added to hold text response.
//					   processOfsResponse,getResponseType,getResponse methods are changed to accomodate
//						the response to be displayed as TEXT.
//		28/09/06	   Changes to download reports.
//	   10/04/07     -  Reports and Deal slips will be transformed only at Browser
//                     even though the Use Transform is set to "YES". This is to
//					   avoid disappearance of blank lines on reports and deal slips.
// 	   27/06/14		-  Added new condition "imgMaxFileSize" to get the values for Maximum size of the uploaded file.
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.beans;

import java.io.Serializable;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.branch.Branch;
import com.temenos.t24browser.branch.BranchConstants;
import com.temenos.t24browser.branch.BranchStrings;
import com.temenos.t24browser.branch.BranchSync;
import com.temenos.t24browser.comms.ConnectionBean;
import com.temenos.t24browser.comms.ConnectionEngine;
import com.temenos.t24browser.comms.InstanceConnector;
import com.temenos.t24browser.comms.T24WebConnection;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.security.SSOPrincipal;
import com.temenos.t24browser.servlets.BrowserServlet;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.RequestTimer;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.utils.TabIndex;
import com.temenos.t24browser.xml.XMLRequestManager;
import com.temenos.t24browser.xml.XMLRequestManagerException;
import com.temenos.t24browser.xml.XMLTemplateManager;
import com.temenos.t24browser.xslt.XMLToHtmlBean;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tsdk.foundation.T24Connection;
import com.temenos.tsdk.foundation.UtilityRequest;
import com.temenos.t24browser.exceptions.ConnectionException;

import java.io.File;
import java.io.StringReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.pdf.PDFEncryptionParams;

import com.temenos.t24browser.request.SessionData;
import java.util.ArrayList;
import java.util.List;

public class TemenosBean implements Serializable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TemenosBean.class);

	protected T24Request ivRequest = null; // Request from browser page
	protected HttpServletRequest ivHttpRequest = null;			// Http Request from browser page
	protected HttpSession ivSession = null; // User's session
	protected String ivProduct = ""; // Whether we are Browser or ARC-IB
	protected String ivRequestType = ""; // Type of request from browser page
	protected String ivSignOnName = ""; // User sign-on name
	protected String ivSignOnNameEnabled = "Y";					// Whether the User sign-on name field is enabled or not on the login screen
	protected String ivUserId; // User Id
	protected String ivPassword; // User password
	protected String ivoption;                                  // Remember me
	protected String ivIpAddress; // User IP address
	protected String ivLoginCounter;							// Counter as on login page to check for refreshes
	protected String ivLoginCommand = ""; // Login command
	protected String ivLoginTitle = ""; // Login title
	protected String ivToken; // Token for next request
	protected String ivCompanyId; // Company id for next request
	protected Branch ivBranch = null; // Branch details if in Branch Server
	protected String ivResponseHtml = null;						// Response HTML for browser page - if transformed
	protected String ivResponseXml = null;						// Response XML for browser page - if not transformed
	protected String ivResponseUrl = null; // Response URL for browser page
	protected String ivRequestXml = null; // Request in Xml format
	protected String ivResponseTxt = null; // Response TEXT for browser page
	protected int ivHttpErrorCode = -1; // Respond with an error code?
	protected boolean ivInvalidateSession = false; // Invalidate the session
	protected Context jndiContext = null; // Context for RMI comms to EJBs
	protected ServletConfig ivServletConfig = null; // Config of Servlet
	protected ServletContext ivServletContext = null; // Context of Servlet
	protected static String ivServletContextPath = "";			// Servlet Context directory path
	protected PropertyManager ivParameters = null;				// Parameters for server connection, etc
	protected boolean ivServerTransform = true; // Transform on Server or not
	protected String ivAllowResize = null; // Toggle resize of windows
	protected String ivAllowToolbox = null; // Toggle allowance of Toolbox
	protected String ivCommand; // Command for this request
	protected boolean ivIgnoreResponse = false;					// Whether to ignore any response
	protected ConnectionBean ivConnection = null; // Connection to the server
	protected HashMap ivWebFields = null;						// Fields to save at the web server and added to the response
	protected String ivCachedRequest = "";						// Cache request for re-authentication
	protected BranchStrings ivBranchStrings = null;				// List of strings used on branch screens
	protected boolean ivBaUser = false;							// Whether the user is a Branch Administrator


	protected boolean XssError = false;	 			//Whether xss error occured
	protected boolean ivError = false; // Whether an error occurred
	protected String ivErrorText; // What error occurred
	protected String ivClientIP = null; // IP Address of the client Browser
	protected String ivDocumentServiceId = "";					// The ID to be used by documentService servlet
	protected String ivDocumentServiceChannel = "";				// The Channel the documentService servlet should connect to.
	protected boolean ivMultiDownload = false;					// Flag to indicate if the response is a multi-download

	private static final String MESSAGES_XSL = "/transforms/errorMessage.xsl";	// Error Messages XSL
	private static final String MESSAGES_XSL_HOME = "";			// Messages XSL home directory
	protected static final String END_TAG = "</";
	private static final String CDATA_START_TAG = "<!CDATA[";
	private static final String CDATA_END_TAG = "]]>";
	private static final String DEFAULT_SKIN_NAME = "default";	// Name of the default skin to use
	private static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";
	private static final String XML_ESCAPED_AMPERSAND = "&amp;";
	private static final String XML_ESCAPED_LESSTHAN = "&lt;";
	private static final String XML_ESCAPED_GREATERTHAN = "&gt;";

	private String ivAPIResponseType = ""; // This will be set by the API

	private XMLTemplateManager ivTemplateManager = null;        //contain all the possible xml templates

	private String xslHomeDirectory = "";                       //Used to change the home directory in the xml Transformer
	private RequestTimer ivRequestTimer; // The request timer
	protected static final String DEFAULTLANG = "GB";			// The default language code is GB

	protected static final String SECURITY_VIOLATION_ERROR = "SECURITY VIOLATION";
	protected static final String SECURITY_VIOLATION_ERROR_ARCIB = "Service is currently Not Available please try again later.";
	private static final String SESSION_SIGN_ON_NAME = "BrowserSignOnName";			// Used to save the user's sign on name in the session
	private static final String SESSION_USER_ID = "BrowserUserId";					// Used to save the user's Id in the session
	private static final String SESSION_PASSWORD = "BrowserPassword";				// Used to save the user's password in the session
	private static final String SESSION_CLIENT_IP_ADDRESS = "BrowserClientIpAddress";	// Used to save the user's IP address in the session
	private static final String SESSION_LOGGEDIN = "LoggedIn"; // LoggedIn Flag
	private static final String SESSION_LOGIN_COUNTER = "LoginCounter";  			// LoginCounter stored in the session
	protected static final String SESSION_CACHED_REQUEST = "BrowserCachedRequest";	// Used to save a cached XML request in the user's session

	// Browser Parameters connection variables
	private String ivServerConnectionMethod = ""; // Server Connection Method
	protected String ivWebServerSkinNames = ""; // Web Server list of Skin Names
	protected String ivResponseType = ""; // Response type

	// Browser Parameters constants
	private static final String PARAM_SERVER_CONN_METHOD = "Server Connection Method";		// Parameter Server Connection Method
	private static final String PARAM_ALLOW_RESIZE       = "Allow Resize";					// Parameter Allow Resize
	private static final String PARAM_SKIN_NAMES         = "Web Server Skins";				// Parameter Web Server Skin Names
	private static final String PARAM_INSTANCE_NAME = "Instance";
	private static final String PARAM_DOCUMENT_SERVICE_CHANNEL      = "Document Service Channel";		// Parameter to hold the Channel for the Document Service
	private static final String PARAM_PRODUCT 			 = "Product"; 						// Parameter defining whether we are Browser or ARC-IB
	private static final String PRODUCT_BROWSER = "BROWSER"; // Browser product
	private static final String PRODUCT_ARC_IB = "ARC-IB"; // ARC-IB product
	private static final String PARAM_ALLOW_TOOLBOX		 = "AllowToolbox";					// Parameter to allow Toolbox login

	// Parameter Defaults
	private static final String DEFAULT_SERVER_CONN_METHOD = "INSTANCE";					// Default Server Connection Method

	// Browser Connection Methods
	private static final String CONNECT_METHOD_INSTANCE = "INSTANCE";						// T24 Connector connection method
	private static final String CONNECT_METHOD_AGENT = "AGENT";								// JCA jRemote connection method
	private static final String CONNECT_METHOD_AGENT_JREMOTE = "AGENT_JREMOTE";				// JCA jRemote connection method
	private static final String CONNECT_METHOD_JMS = "JMS";                                 // JMS connection method

	private static final String KEEP_ALIVE_TIMEOUT = "WS_KeepaliveTimeout";      			// Timeout for keep alive functionality

	// Login data
	protected static final String USER_LOGIN_COMMAND = "login";								// Login command for a normal user
	protected static final String USER_LOGIN_TITLE = "";									// Login title for a normal user
	protected static final String USER_GET_LOGIN_COMMAND = "getLogin";						// Get login page command for a normal user
	protected static final String BRANCH_ADMIN_LOGIN_COMMAND = "branchadminlogin";			// Login command for a Branch Administrator
	protected static final String BRANCH_ADMIN_LOGIN_TITLE = "Branch Administrator Login";	// Login title for a ranch Administrator
	protected static final String BRANCH_ADMIN_GET_LOGIN_COMMAND = "getBranchAdminLogin";	// Get login page command for a Branch Administrator

	// XML Tag constants
	protected static final String XML_TAG_REQ_ARGUMENTS = "<requestArguments>";				// Tag for start of request message
	protected static final String XML_TAG_REQ_ARGUMENTS_C = "</requestArguments>";			// Tag for start of request message - closing tag
	protected static final String XML_TAG_RESP_DETAILS = "<responseDetails>";				// Tag of start of response message
	protected static final String XML_TAG_RESP_DETAILS_C = "</responseDetails>";			// Tag of start of response message - closing tag
	protected static final String XML_TAG_ALLOW_RESIZE = "<allowResize>";					// Tag of start of allow resize
	protected static final String XML_TAG_ALLOW_RESIZE_C = "</allowResize>";				// Tag of start of allow resize - closing tag
    protected static final String XML_TAG_ENQUIRY_NAME = "<enqname>";                       // Enquiry name
    protected static final String XML_TAG_ENQUIRY_NAME_C = "</enqname>";                    // Enquiry Name - closing tag


	// TC Instance variables - helps determine which instance to use to talk to the server
	protected String ivInstanceContext = null;												// Instance stored in the servlet context
	protected String ivInstanceUser = null;													// Instance stored in the user's session
	protected String ivInstanceRequest = null;												// Instance supplied in the request
	protected String userAgent; // identify user agent for UTF-8 decoding

	protected TemenosBean( ServletConfig config, XMLTemplateManager xmlTemplates, String clientIP, HttpServletRequest request ) throws TBrowserException
	{
		ivCommand = "";
		ivError = false;
		ivErrorText = "";
		ivLoginCounter = "";
		ivIgnoreResponse = false;
		ivServletConfig = config;
		this.setServletContext(config.getServletContext());
		ivTemplateManager = xmlTemplates;
		ivClientIP = clientIP;
		userAgent = request.getHeader("user-agent");
		// Read the Browser parameters settings
		readBrowserParameters();

		this.setRequest( request );


		this.XssCharCheck( request );
		// Check if we are in a Branch or not, and get the strings from the XML file for display on screens
		ivBranch = getBranch();

		if ( ivBranch != null )
		{
			ivBranchStrings = getBranchStrings();

			// See whether we are a Branch Administrator or not (and set the common variable)
			initialiseBranchAdministrator();
		}

		// Save any parameters marked as required in the response
		saveWebServerFields();

		// Get the command - as this can determine the instance we use for the connection
		ivCommand = ivRequest.getValue("command");

		if ( ( ivCommand != null ) && ( ! ivCommand.equals("") ) )
		{
			ivCommand = ivCommand.toLowerCase();
		}

		// Initialise instance variables
		initialiseInstances();

		initialiseServerConnection();
	}

	protected void setServletContext(ServletContext context) {
		ivServletContext = context;
		ivServletContextPath = context.getRealPath("");
	}

	// returns the XMLTemplateManager containing all of the xml templates
	protected XMLTemplateManager getXmlTemplates() {
		return ivTemplateManager;
	}

	//Looks to see if the xsl name has an additional directory structure attached to it
	// and if it does it stores it for later use in the XT transformer. It will
	//use this additional path as the basis for the home directory (where the xsl live)
	protected void setHomeDirectory(String xsl) {
		int pathSeparatorPosition = xsl.lastIndexOf("/");
		if (pathSeparatorPosition != -1) {
			xslHomeDirectory = xsl.substring(0, pathSeparatorPosition);
		} else {
			xslHomeDirectory = "";
		}

	}

	private void setRequest( javax.servlet.http.HttpServletRequest request )
	{
		ivRequest = new T24Request(request);
		ivSession = request.getSession();

		// Validate the request variables coming in as much as possible.
		String doNochangeFieldsCheck = readParameterValue(BrowserBean.PARAM_DO_NOCHANGE_FIELDS_CHECK);
		if ((doNochangeFieldsCheck == null || doNochangeFieldsCheck.equals(BrowserBean.PARAM_DO_NOCHANGE_FIELDS_CHECK_YES)) && (String) ivSession.getAttribute( SESSION_USER_ID ) != null) {
			try {
				ivRequest.validateRequestVariables();
			} catch (SecurityViolationException e) {
				LOGGER.error(e.getMessage());
				ivError = true;
				ivErrorText = getSecurityViolationMessage("SV-01");
			}
		}

		// add session data if there is any
		ivRequest.addSessionData();
		// Store the original http request
		ivHttpRequest = request;
	}
	private void XssCharCheck( javax.servlet.http.HttpServletRequest request)
	{
		// Validate the request variables coming in as much as possible.
		String doNochangeFieldsCheck = readParameterValue(BrowserBean.PARAM_DO_NOCHANGE_FIELDS_CHECK);
		if ((doNochangeFieldsCheck == null || doNochangeFieldsCheck.equals(BrowserBean.PARAM_DO_NOCHANGE_FIELDS_CHECK_YES)) && (String) ivSession.getAttribute( SESSION_USER_ID ) != null) {
			try {
				ivRequest.validateInputVariables();
			} catch (SecurityViolationException e) {
				LOGGER.error(e.getMessage());
				 XssError = true;
			}

		}
		// Store the original http request
		ivHttpRequest = request;
	}
	//Return Xss error if any
	public boolean requestErrorText()
	{
		return( XssError );
	}
	public void setRequestToken(String sToken) {
		// Save the token value from the response
		ivToken = sToken;
	}

	public String getRequestToken() {
		// Retrieve the token value for this request
		return (ivToken);
	}


	public void setRequestCompany(String sCompany) {
        // Save the company id value from the response
        ivCompanyId = ivRequest.getParameter("companyId");
	}

	public String getCompanyName(){
		// Retrieve the company id value for this request
        return (ivCompanyId) ;
	}




	public void setSignOnName(String sSignOnName) {
		ivSignOnName = sSignOnName;
		ivSession.setAttribute(SESSION_SIGN_ON_NAME, sSignOnName);
	}

	public String getSignOnName()
	{
		// Check whether it was passed in the request or not - may be part of a getLogin command
		String ivSignOnName = ivRequest.getValue("signOnName");

		if ( ivSignOnName == null )
		{
			// Try the session
			ivSignOnName = (String) ivSession.getAttribute( SESSION_SIGN_ON_NAME );
		}

		if ((ivSignOnName == null) || (ivSignOnName.equals("null"))) {
			ivSignOnName = "";
		}

		return (ivSignOnName);
	}
	public String getoption()
	{
		// Check whether it was passed in the request or not - may be part of a getLogin command
		String ivoption = ivRequest.getValue("RememberOption");

		return( ivoption );
	}

	public void setSignOnNameEnabled(String sSignOnNameEnabled) {
		ivSignOnNameEnabled = sSignOnNameEnabled;
	}

	public String getSignOnNameEnabled()
	{
		// Check whether it was passed in the request or not - may be part of a getLogin command
		String enabled = ivRequest.getValue("signOnNameEnabled");

		if ( ( enabled != null ) && ( ( enabled.equals("Y") ) || ( enabled.equals("N") ) ) )
		{
			ivSignOnNameEnabled = enabled;
		}

		return (ivSignOnNameEnabled);
	}

	public void setUserId( String sUserId )
	{
		if ( (sUserId == null) || sUserId.equals("") )
		{
			LOGGER.debug("*** Clearing Session User Id !!!  Request Type is : " + ivRequest.getParameter("requestType"));
		}

		ivUserId = sUserId;
		ivSession.setAttribute(SESSION_USER_ID, sUserId);
	}

	public String getUserId() {
		ivUserId = (String) ivSession.getAttribute(SESSION_USER_ID);
		return (ivUserId);
	}

	public void setPassword(String sPassword) {
		ivPassword = sPassword;
		ivSession.setAttribute(SESSION_PASSWORD, sPassword);
	}

	public String getPassword() {
		ivPassword = (String) ivSession.getAttribute(SESSION_PASSWORD);
		return (ivPassword);
	}

	public void setClientIpAddress(String sIpAddress) {
		ivIpAddress = sIpAddress;
		ivSession.setAttribute(SESSION_CLIENT_IP_ADDRESS, sIpAddress);
	}

	public void setUserInstance(String sUserInstance) {
		ivInstanceUser = sUserInstance;
		ivSession.setAttribute(BranchConstants.INSTANCE_SESSION, sUserInstance);
	}

	public String getUserInstance()
	{
		ivInstanceUser = (String) ivSession.getAttribute( BranchConstants.INSTANCE_SESSION );
		return (ivInstanceUser);
	}

	public void setCachedRequest( String sRequestXml )
	{
		ivCachedRequest = sRequestXml;
		ivSession.setAttribute(SESSION_CACHED_REQUEST, sRequestXml);

		if ( ( sRequestXml != null ) && ( ! sRequestXml.equals("") ) )
		{
			String logMsg = com.temenos.t24browser.utils.Logger.
				replacePassword( sRequestXml );
			LOGGER.debug("Saving cached XML request : " + logMsg);
		}
	}

	public String getCachedRequest()
	{
		ivCachedRequest = (String) ivSession.getAttribute( SESSION_CACHED_REQUEST );
		return (ivCachedRequest);
	}

	private void initialiseBranchAdministrator()
	{
		// Check if we are a Branch Administrator
		// If it is set in the request then use this, otherwise get it out of the status
		ivBaUser = false;
		String context = ivRequest.getValue("context");

		if ((context == null) || (context.equals(""))) {
			ivBaUser = getBranchAdministratorSession().booleanValue();
		} else {
			context = context.toLowerCase();

			if (context.equals("bradmin")) {
				ivBaUser = true;
			} else {
				ivBaUser = getBranchAdministratorSession().booleanValue();
			}
		}

		LOGGER.debug("Branch Administrator : " + ivBaUser);
	}

	private Boolean getBranchAdministratorSession()
	{
		Boolean baUser = (Boolean) ivSession.getAttribute( BranchConstants.SESSION_BA_USER );

		if ( baUser == null )
		{
			return (new Boolean(false));
		} else {
			return ((Boolean) ivSession
					.getAttribute(BranchConstants.SESSION_BA_USER));
		}
	}

	// Set the branch administrator in the session
	public void setBranchAdministrator(boolean baUser) {
		ivBaUser = baUser;
		ivSession.setAttribute( BranchConstants.SESSION_BA_USER, new Boolean( ivBaUser ) );

		LOGGER.debug("Setting Branch Administrator to : " + ivBaUser);
	}

	// Get the branch administrator setting
	public boolean getBranchAdministrator() {
		return (ivBaUser);
	}

	// Method to set the LoggedIn flag on HTTP Session.
	public void setLoggedIn(String sLoggedIn) {
		ivSession.setAttribute(SESSION_LOGGEDIN, sLoggedIn);
	}

	// Method to retrieve the LoggedIn flag.
	public boolean getLoggedIn() {
		String sLoggedIn = (String) ivSession.getAttribute(SESSION_LOGGEDIN);

		if ((sLoggedIn != null) && (!sLoggedIn.equals(""))) {
			return true;
		} else {
			return false;
		}
	}

	// Get the login counter for this user
	public String getUserLoginCounter()
	{
		String sLoginCounter = (String) ivSession.getAttribute(SESSION_LOGIN_COUNTER);

		if ((sLoginCounter == null) || (sLoginCounter.equals(""))) {
			sLoginCounter = "0";
			setUserLoginCounter(sLoginCounter);
		}

		return (sLoginCounter);
	}

	// Set the login counter for this user
	public void setUserLoginCounter(String counter) {
		ivSession.setAttribute(SESSION_LOGIN_COUNTER, counter);
	}

	// Update the login counter - use a random number to prevent replay attack
	public String updateUserLoginCounter() {
		String newCounter = ("" + (Math.random())).substring(2, 10);

		if (!newCounter.equals(getUserLoginCounter())) {
			setUserLoginCounter(newCounter);
			return newCounter;
		} else {
			return updateUserLoginCounter();
		}
	}

	// set the response type of the message currently returned from the API
	public void setAPIResponseType(String responseType) {
		ivAPIResponseType = responseType;
	}

	public int getHttpErrorCode() {
		return ivHttpErrorCode;
	}

	public boolean getInvalidateSession() {
		return ivInvalidateSession;
	}

	public String getResponseType() {
		// when the API is in operation override responseType
		if (!ivAPIResponseType.equals("")) {
			return ivAPIResponseType;
		}

		// Indicate if the response is in HTML or XML or a URL
		// If a login failed then response is always HTML using static page
		if (ivHttpErrorCode > 0) {
			return ("HTTP.ERROR.CODE");
		} else if ((ivResponseUrl != null) && (!ivResponseUrl.equals(""))) {
			return ("URL");
		} else if (ivIgnoreResponse) {
			return ("NO.RESPONSE");
		} else if (ivResponseType.equals("XML.DOCUMENT.SERVICE")) {
			return (ivResponseType);
		} else if (ivResponseType.equals("XML.T24.UPDATES")) {
			return (ivResponseType);
		} else if (ivResponseType.startsWith("TEXT")) {
			return (ivResponseType);
		} else if (ivResponseType.equals("XML.REPORT")) {
			// set type as html to transform the report in servlet itself
			return ("HTML");
		} else if (ivServerTransform) {
			return ("HTML");
		} else {
			return ("XML");
		}
	}

	public String getResponse() {
		// API overriding
		if (ivAPIResponseType.equals("URL")) {
			return (ivResponseUrl);
		} else if (ivAPIResponseType.equals("HTML")) {
			return (ivResponseHtml);
		}

		// Return HTML or XML or URL response back to Servlet
		// If a login failed then response is always HTML using static page
		if ((ivResponseUrl != null) && (!ivResponseUrl.equals(""))) {
			return (ivResponseUrl);
		} else if (ivResponseType.startsWith("TEXT")) {
			return (ivResponseTxt);
		} else if (ivResponseType.equals("XML.REPORT")) {
			return (ivResponseXml);
		} else if (ivServerTransform) {
			return (ivResponseHtml);
		} else {
			return (ivResponseXml);
		}
	}

	// Add Tab Index configuration in response as XML, where configuration results based on user preference and admin preference
	protected String addTabIndexParams (String ofsXmlResult, String userTabEnable){
		// userTabEnable - Value from T24 BROWSER.PREFERENCE attribute ENABLE.TAB.INDEX
		String tabXML = null;
		int startTagPos = ofsXmlResult.indexOf( XML_TAG_RESP_DETAILS );
		if (userTabEnable == null){
			userTabEnable = "NO"; // If values is null, then consider it as "NO".
		}
		tabXML = TabIndex.readParams(ivServletContext).getXML(userTabEnable); // get the XML from TabIndex Params
		tabXML = ofsXmlResult.substring(0, startTagPos + 17) + tabXML + ofsXmlResult.substring(startTagPos + 17, ofsXmlResult.length());
		return tabXML;
	}


	// Process an OFS XML response
	protected void processOfsResponse( String ofsXmlResult, XMLRequestManager xmlManager )
	{
		// Check command result and display result or error page accordingly
		if ( ofsXmlResult != null )
		{
			try
			{
				String sResponseType = xmlManager.getNodeValue( ofsXmlResult, "responseType" );
				ivResponseType = sResponseType;


				if ( ( sResponseType != null ) && ( !sResponseType.equals("") ) )
				{
					// Add any saved web server fields to the XML string

                    // The user has not yet been set, so pass it on
                    String user = xmlManager.getNodeValue( ofsXmlResult, "user" );
					ofsXmlResult = addWebServerFields( ofsXmlResult, user );


					// Process tab index configuration based on parameter file and user preference
					String userTabEnable = xmlManager.getNodeValue( ofsXmlResult, "enableTab");
					ofsXmlResult = addTabIndexParams(ofsXmlResult, userTabEnable);


                    //add XML tag to allow or disallow resize of window
					ofsXmlResult = addAllowResizeTag(ofsXmlResult, "<userDetails>");


					// Add any branch details to the response - if we are running in a branch server
					if ( ( ivCommand != null ) && ( ! ivCommand.equals("") ) && ( ivCommand.equals( BRANCH_ADMIN_LOGIN_COMMAND ) ) )
					{
						// Branch Administrator login so add the full branch details to the response
						ofsXmlResult = addFullBranchDetails( ofsXmlResult, XML_TAG_RESP_DETAILS );
					}
					else
					{
						ofsXmlResult = addBranchDetails( ofsXmlResult, XML_TAG_RESP_DETAILS );
					}


					if ( sResponseType.equals( "NO.RESPONSE" ) )
					{
						// No response - we just need to save the new token
						ivIgnoreResponse = true;
					}
					else if ( sResponseType.startsWith("ERROR.") )
					{
						// Display error page
						String sError = xmlManager.getNodeValue( ofsXmlResult, "error" );
						buildErrorResponse( sError );
					}
					else if(sResponseType.startsWith("TEXT"))
					{
						if(sResponseType.equals("TEXT.SAVE.REPORT"))
						{
							ivHttpRequest.setAttribute("downloadType",".txt");
							ivHttpRequest.setAttribute("downloadFile",xmlManager.getNodeValue( ofsXmlResult, "txt" ));
						}
						else
						{
							ivResponseTxt = xmlManager.getNodeValue( ofsXmlResult, "txt" );
						}
				    }

					else if ( sResponseType.startsWith("XML.") )
					{
						// Get transform name and convert to HTML
						String sStyleSheet = xmlManager.getNodeValue( ofsXmlResult, "styleSheet" );
						setHomeDirectory(sStyleSheet);


						//Store the filepath and imageid to Hashmap for file upload.
						String filepath = xmlManager.getNodeValue( ofsXmlResult, "filepath" );
						if ((filepath != null)&&(!filepath.equals("")))
						 {

						   HttpSession session = ivHttpRequest.getSession();
						   if (session != null)
						   {
						     String fileid = xmlManager.getNodeValue( ofsXmlResult, "imageid" );
						     String imgMaxFileSize = xmlManager.getNodeValue( ofsXmlResult, "imgMaxFileSize" );
						     String username = (String) session.getAttribute(SessionData.SESSION_USER_ID);
						     HashMap filepathMap = new HashMap();
						     filepathMap.put("filepath", filepath);
						     filepathMap.put("imageid", fileid);
						     filepathMap.put("imgMaxFileSize", imgMaxFileSize);
						     String filepathMapId = username + "_filepathMap";
						     addObjectToSession(filepathMapId, filepathMap);
						   }
						}
						//Store the <document> Tag values to Hashmap for file download.
						String docvalue = xmlManager.getNodeValue(ofsXmlResult, "document");
	                    if ((docvalue != null)&&(!docvalue.equals("")))
	                    {
	                     int doctstartingPos = ofsXmlResult.indexOf("<enqResponse>");
	                     int doctsendingPos = ofsXmlResult.indexOf("</enqResponse>");
	                     if (doctstartingPos != -1 && doctsendingPos != -1)
	                     {
	                    	String doctvalue = ofsXmlResult.substring(doctstartingPos, doctsendingPos + 14);
	                    	StringBuffer s = new StringBuffer(doctvalue);
	                    	String findStr = "<document>";
	                    	int lastIndex = 0;
	                    	int count =0;
	                    	while(lastIndex != -1)
	                    	{
	                         lastIndex = doctvalue.indexOf(findStr,lastIndex);

	                    	       if( lastIndex != -1)
	                    	       {
	                    	    	     count ++;
	                    	             lastIndex+=findStr.length();
	                    	       }
	                    	}
	                    	List<String> arrayvalue = new ArrayList<String>();

	                    	for(int x = 1; x<=count;x = x+1)
	                    	{
	                    		int doctstart = s.indexOf("<document>");
	                        	int endpos = s.indexOf("</document>");
	                        	String values = s.substring(doctstart +10, endpos);
	                        	arrayvalue.add(values);
	                        	s= s.delete(doctstart, endpos +11);
	                    	}
	                    	// Store the download path in to Hash map.
	                    	HttpSession sessionvalue = ivHttpRequest.getSession();
	                    	if (sessionvalue != null)
							{
	                    	   if ((!arrayvalue.equals(null)) && (!arrayvalue.equals("")))
	                    	   {
	                    		   String winName = ivRequest.getParameter("windowName");
	                    		   HashMap DownloadpathMap = new HashMap();
	                    		   DownloadpathMap.put("downloadpath",arrayvalue);
	                    		   String DownloadpathMapId = winName + "_DownloadpathMap";
	                    		   addObjectToSession(DownloadpathMapId,DownloadpathMap);

	                    	   }


							}
	                      }
	                    }
						// Display XML page returned
						String sResponse = xmlManager.getNodeValue( ofsXmlResult, "responseData" );
						sResponse = xmlManager.addFragmentAtNode(getRequestTimer().toXML(), "userDetails", sResponse);


						if ( ( sResponse != null ) && ( !sResponse.equals("") )  &&
						     ( sStyleSheet != null ) && ( !sStyleSheet.equals("") ) )
						{
							String logMsg = com.temenos.t24browser.utils.Logger.
								replacePassword( sResponse );
							LOGGER.debug("XML Response: " + logMsg);
							if((sResponseType.equals("XML.ENQUIRY") || sResponseType.equals("XML.ENQIURY")) && (sResponse.indexOf("<rpt>") > 0 ))
							{
							//	boolean bkivServerTransform = ivServerTransform ;
							//	ivServerTransform = false;
								ivResponseType = "XML.REPORT";
								transformResult( sStyleSheet, sResponse );
							//	ivServerTransform = bkivServerTransform ;
							}
							else
							{
								if( sResponseType.equals("XML.T24.UPDATES"))
								{
									// no need to transform the response
									ivServerTransform = false;
									// set the response type to XML.DOCUMENT.SERVICE
									ivResponseType = sResponseType;
									// Set the response
									ivResponseXml = sResponse;
								}
								else
								{
									transformResult( sStyleSheet, sResponse );
								}
							}


						}
						else
						{
							buildErrorResponse( "Invalid XML Document received from Session request" );
						}

					}
					else if ( sResponseType.equals("CDATA") )
					{
						// Get transform name and convert to HTML
						String sStyleSheet = xmlManager.getNodeValue( ofsXmlResult, "styleSheet" );


						// Remove CDATA tag and display XML
						String sResponse = xmlManager.getNodeValue( ofsXmlResult, "responseData" );
						// Now add in the timing information from the request timer
						sResponse = xmlManager.addFragmentAtNode(getRequestTimer().toXML(), "responseDetails", sResponse);
						String sXmlTemp = sResponse.substring( CDATA_START_TAG.length() );
						String sXmlDoc = sXmlTemp.substring( 0, sXmlTemp.length() - CDATA_END_TAG.length() - 1 );


						// Display XML page returned
						if ( ( sXmlDoc != null ) && ( !sXmlDoc.equals("") )  &&
						     ( sStyleSheet != null ) && ( !sStyleSheet.equals("") ) )
						{
							String logMsg = com.temenos.t24browser.utils.Logger.
								replacePassword( sXmlDoc );
							LOGGER.debug("XML Response: " + logMsg);
							transformResult( sStyleSheet, sXmlDoc );
						}
						else
						{
							buildErrorResponse( "Invalid XML Document received from Session request" );
						}
					}
					else
					{
						buildErrorResponse( "Invalid Response Type received from Session request" );
					}

					// Check if there is any OFS request to be run against the branch -
					// following successful commit on the main server, run the command
					// against the branch server to keep it in sync
						// Check if we are a branch and were in online mode
					if ( ( ivBranch != null ) && ( ivBranch.getStatus().equals( BranchConstants.BRANCH_ONLINE ) ) )
					{
						String sOfsReqString = getOfsStringfromResponse( ofsXmlResult );


						if ( ( sOfsReqString != null ) && ( ! sOfsReqString.equals( "" ) ) )
						{
							// Run the request at the branch
							branchSyncRequest( sOfsReqString );
						}
					}
				}
				else
				{
					ivResponseType = "ERROR.";
					LOGGER.error("Missing responseType tag in XML Response: " + ofsXmlResult);
					buildErrorResponse( "Invalid response received from T24: Please contact your System Administrator." );
				}
			}
			catch(XMLRequestManagerException e){
				buildErrorResponse( e.getMessage());
				return;
			}
		}
	}
	private void addObjectToSession(String id, Object o)
	{
		ivSession.setAttribute( id, o);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(ivSession.getId() + " "  + ivSession.getAttribute("BrowserSignOnName") + " "  + "Storing response: " + id + " = " + o.toString());
		}
	}
	protected void buildErrorResponse(String sErrorMessage) {
		// Build error result in HTML
		ivError = true;

		if ((sErrorMessage == null) || (sErrorMessage.equals(""))) {
			sErrorMessage = "Unknown error occurred : Please contact your System Administrator.";
		}

		ivRequest.setAttribute("errorText", sErrorMessage);
		ivErrorText = sErrorMessage;

		buildMessageResponse(sErrorMessage);
	}

	protected void buildMessageResponse(String sMessage) {
		buildMessageResponse("Error Message", sMessage);
	}

	protected void buildMessageResponse(String sTitle, String sMessage) {
		String sSkinName = getSkinName();

		String sMessageXML = XML_TAG_RESP_DETAILS + "<userDetails><skin>" + sSkinName + "</skin></userDetails><messages><title>" + sTitle + "</title><msg>" + sMessage + "</msg></messages>" + XML_TAG_RESP_DETAILS_C;

		// If we were doing a download ignore it as we have an error
		ivRequest.setAttribute("download", "no");

		// need to reset the context xsl home directory
		setHomeDirectory(MESSAGES_XSL_HOME);

		// Transform XML with stylesheet to HTML
		transformResult(MESSAGES_XSL, sMessageXML);
	}

	protected void buildBasicErrorResponse(String sErrorMessage) {
		ivResponseHtml = "";
		ivResponseHtml = ivResponseHtml + "<html>" + "\n";
		ivResponseHtml = ivResponseHtml + "<head>" + "\n";
		ivResponseHtml = ivResponseHtml + "<title>ERROR</title>" + "\n";
		ivResponseHtml = ivResponseHtml + "</head>" + "\n";
		ivResponseHtml = ivResponseHtml + "<body bgcolor=\"white\">" + "\n";
		ivResponseHtml = ivResponseHtml + "<LI>" + sErrorMessage + "\n";
		ivResponseHtml = ivResponseHtml + "</body>" + "\n";
		ivResponseHtml = ivResponseHtml + "</html>" + "\n";
	}

	public String getErrorText() {
		return (ivErrorText);
	}

	protected void setErrorText(String sError) {
		ivErrorText = sError;
	}

	public boolean requestError() {
		return (ivError);
	}

	protected BrowserResponse sendOfsRequestToServer(String ofsRequest) {
		// Send OFS Request to the server
		BrowserResponse ofsReply = null;

		if ( ivConnection != null )
		{
		try
			{
			    long timeout = ivConnection.getTimeout() * 1000;	//timeout value in milliseconds
				long timeInMillis = System.currentTimeMillis();
				int retries = 1;
				int retryCount = ivConnection.getRetryCount(); //get  retry count
				int retryWait = ivConnection.getRetryWait() * 1000; //get  retry count
				while((System.currentTimeMillis() - timeInMillis < timeout || timeout == 0) && ((retryCount >= retries) || (retryCount == 0))) {
					// while(retryCount > retries) {
					// if(timeout == 0) { //Do not retry if timeout is 0
					// timeout = -1;
					// }
					 if(retryCount == 0)
					    {
						retryCount = -1;
					}
					try {
						// Send request to server
						ofsReply = sendOfsRequest(ofsRequest);

						timeInMillis = System.currentTimeMillis() - timeInMillis;
						getRequestTimer().setOfs(ofsReply.getOfsTime());
						getRequestTimer().setTransport(timeInMillis);

						// Checks and changes the path of XSL file which is referenced in OFS message
						// to the /transforms/...
						DebugUtils.checkXslPath(ofsReply);

						return (ofsReply);
					} catch (ConnectionException ce) {
						LOGGER.warn("Attempting to re-send the request [" + retries + ". retry] due to connection error: " + ce.getMessage());
						retries++;
						if (retries > 1) {
					        try {	//Sleep 2 seconds to give other requests a chance to get processed
								Thread.sleep(retryWait);
							} catch (InterruptedException e2) {
								LOGGER.error("Unable to pause execution after connection error: "
										+ ce.getMessage());
							}
						}
					}
				} // while(retry)

				// If connection timed out ==> return error response
			//	if(timeout > 0 && System.currentTimeMillis() - timeInMillis >= timeout) {
				if((retryCount <= retries) || (timeout >= 0 && System.currentTimeMillis() - timeInMillis >= timeout))
				  {

					String errMsg = "Request has timed out.";
					LOGGER.error(errMsg);
					if (ofsReply == null) {
						ofsReply = new BrowserResponse(ivParameters);
						ofsReply.setError(errMsg);
						ofsReply.setMsg(ofsRequest);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Unable to communicate with server", e);
				return null;
			}
		} // if(ivConnection != null)
		return (ofsReply);
	}

	private BrowserResponse sendOfsRequest(String ofsRequest) throws ConnectionException, TBrowserException
	{
		// Send OFS Request to the server
		BrowserResponse ofsReply = null;
		LOGGER.info("Sending request to server");

		// if it is a smartclient request, do not transform.
		if (ivCommand.equals("smartclient")) {
			ivServerTransform = false;
		}

				//Check for the firefox fragment request, since UTF-8 is handled differently in firefox URL we have to decode the UTF-8 characters in Request
		boolean isIE = (userAgent != null && userAgent.indexOf("MSIE") != -1);
				if (!isIE)
				{
					if(ivHttpRequest.getParameter("isFirefox")!=null && ivHttpRequest.getParameter("isFirefox").equals("true"))
					{
						/*Enquiry name may contains "%" character, which will be removed after the utf decoding. To avoid this problem
                        fetch the enquiry name before decoding and restore it again*/
				String ofsRequestEnqNameBeforeDecode = new String();
				String ofsRequestEnqNameAfterDecode = new String();
				int startPos = ofsRequest.indexOf(XML_TAG_ENQUIRY_NAME);
				int endPos = ofsRequest.indexOf(XML_TAG_ENQUIRY_NAME_C);
                        if (startPos != -1 || endPos != -1)
                        {
	                        ofsRequestEnqNameBeforeDecode = ofsRequest.substring(startPos + XML_TAG_ENQUIRY_NAME.length(), endPos);
    			            ofsRequestEnqNameBeforeDecode = XML_TAG_ENQUIRY_NAME+ofsRequestEnqNameBeforeDecode +XML_TAG_ENQUIRY_NAME_C;
				}
				ofsRequest = Utils.decodeUTF8(ofsRequest);

				startPos = ofsRequest.indexOf(XML_TAG_ENQUIRY_NAME);
				endPos = ofsRequest.indexOf(XML_TAG_ENQUIRY_NAME_C);
                        if (startPos != -1 || endPos != -1)
  						{
	    	                ofsRequestEnqNameAfterDecode = ofsRequest.substring(startPos + XML_TAG_ENQUIRY_NAME.length(), endPos);
    	        	        ofsRequestEnqNameAfterDecode = XML_TAG_ENQUIRY_NAME+ofsRequestEnqNameAfterDecode+XML_TAG_ENQUIRY_NAME_C;
					// replace the exact enquiry name after decoding UTF
                        	ofsRequest = ofsRequest.replace(ofsRequestEnqNameAfterDecode,ofsRequestEnqNameBeforeDecode );
				}
			}
		}

				// Add any branch details to the request - if we are running in a branch server
		ofsRequest = addBranchDetails(ofsRequest, XML_TAG_REQ_ARGUMENTS);

		// Replace all special characters from escape character code
		ofsRequest = Utils.replaceAll(ofsRequest, "%2C", ",");
		ofsRequest = Utils.replaceAll(ofsRequest, "%20", " ");
		ofsRequest = Utils.replaceAll(ofsRequest, "%3A", ":");
		ofsRequest = Utils.replaceAll(ofsRequest, "%7C", "|");

				String logMsg = com.temenos.t24browser.utils.Logger.
					replacePassword( ofsRequest );
		LOGGER.debug("XML Request: " + logMsg);

		// IS there an SSO Principal held in the session?
				SSOPrincipal ssop = (SSOPrincipal)ivSession.getAttribute("ssoPrincipal");
				if (ssop != null)
				{
			Principal ssoPrincipal = ssop.getSSOPrincipal();
					if (ssoPrincipal != null)
					{
				LOGGER.info("Request is using a secure SSO channel");
						ofsReply = ivConnection.talkToServer( ofsRequest, ivClientIP, ssoPrincipal );
					}
			}
				else
				{
					// If we are running in secure mode, then pass the principal object which
					// contains the user's common name in the certificate for the LDAP database
					if ( !ivHttpRequest.isSecure() )
					{
				ofsReply = ivConnection.talkToServer(ofsRequest, ivClientIP);
			} else {
				LOGGER.info("Request is using a secure channel");
				Principal principal = ivHttpRequest.getUserPrincipal();

						if ((ivHttpRequest.getAuthType()!=null) && ivHttpRequest.getAuthType().equals("BASIC"))
						{
					LOGGER.info("Request is using Authentication Type of 'BASIC'");
							// This is BASIC Authentication so the BASIC Authentication filter
							// has already prepared the the CREATE.SESSION url and has inserted
							// the supplied username & password so DO NOT send the principal
					// send a 'normal' request
							ofsReply = ivConnection.talkToServer( ofsRequest, ivClientIP );
						}
						else  {
			                	  LOGGER.info("Request is using a secure channel");
			                      if ((ivHttpRequest.getAuthType()!=null) && ivHttpRequest.getAuthType().equals("BASIC"))
						{
			                         LOGGER.info("Request is using Authentication Type of 'BASIC'");
			                         // This is BASIC Authentication so the BASIC Authentication filter
			                         // has already prepared the the CREATE.SESSION url and has inserted
			                         // the supplied username & password so DO NOT send the principal
			                         // send a 'normal' request
			                      }
			                      ofsReply = ivConnection.talkToServer(ofsRequest, ivClientIP);
				}
			}

					// If we're in ARC-IB mode, check for a security violation and return a error if one is found
					// todo: This is not efficient. ARC-IB should return an explicit error
			boolean isUsingWebAppSecurity = (ivHttpRequest.getUserPrincipal() != null);
			if (isUsingWebAppSecurity) {
						if (ofsReply.getMsg() != null && ofsReply.getMsg().length() < 1500 && ofsReply.getMsg().indexOf("SECURITY.VIOLATION") > 0)
						{
					this.ivInvalidateSession = true;
							if (ivHttpRequest.getAuthType().equals("BASIC"))
							{
						// Instruct the Browser to Re-authenticate...
						this.ivHttpErrorCode = HttpServletResponse.SC_UNAUTHORIZED;
							}
							else
							{
						this.ivHttpErrorCode = HttpServletResponse.SC_FORBIDDEN;
					}
				}
			}
		}

		return (ofsReply);
	}

	@SuppressWarnings("unchecked")
	protected void transformResult(String sStyleSheetFile, String sXml) {

		// Validate whether the request is for downloading enquiry
		boolean isNeedTransform = false;
		String download = (String) ivRequest.getAttribute("download");
		String downloadType = (String) ivRequest.getAttribute("downloadType");

		if (download != null && download.equals("yes")) {
			// Change stylesheet for downloading enquiry results
			// enquiry-csv.xsl or enquiry-html.xsl or enquiry-xml.xml
			sStyleSheetFile = "/transforms/enquiry/enquiry-".concat(downloadType).concat(".xsl");

			// Set flag to transform no matter what
			isNeedTransform = true;
		}

		// Transform result and build the HTML result
		// Check if the transformation it to be done on the web server or from the browser.
		// If at the browser, then add the XSL name to the xml
		if (ivServerTransform || isNeedTransform) {
			String htmlResult = "";
			String xmlForTransform = sXml;

			// We need to transform the XML here in to HTML
			try {
				long timeInMillis = System.currentTimeMillis();
				LOGGER.info("Transform - Start server response transform");

				// At servlet level
				LOGGER.info("Transform - At Web Server");

				//Transform xml via Xalan parser - unblank the following two lines to use it
				//XMLToHtml xmlTranformer = new XMLToHtml( sStyleSheetFileName, (Map) ivServletContext.getAttribute("ivCache") );
				// htmlResult = xmlTranformer.transformXml( sXml );

				//Transform xml via XT parser - blank the following two lines if Xalan is required instead of XT
				//XMLToHtmlXT xmlTranformer = new XMLToHtmlXT( ivServletContext.getRealPath( sStyleSheetFile ), ivServletContext.getRealPath("."),xslHomeDirectory );

				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				if (downloadType != null && downloadType.equals("pdf")) {
					String T24logo = "/plaf/images/default/banner_start.gif";
					File pdfT24logo = new File(ivServletContext.getRealPath(T24logo));
					String pdfImg = "<pdfImage>" + pdfT24logo.getAbsolutePath() + "</pdfImage>";
					int enqResstartingPos = sXml.indexOf("<enqResponse>");
					int enqResendingPos = sXml.indexOf("</enqResponse>");
					String Xmlenqresponse = sXml.substring(enqResstartingPos, enqResendingPos + 14);
					int enqResSpos = Xmlenqresponse.indexOf("<enqResponse>");
					Xmlenqresponse = Xmlenqresponse.substring(0,enqResSpos + 13) + pdfImg + Xmlenqresponse.substring(enqResSpos + 13);
					File xsltfile = new File(ivServletContext.getRealPath( sStyleSheetFile));
					StreamSource xmlSource = new StreamSource(new StringReader(Xmlenqresponse));

					// create an instance of fop factory
					try {
						StreamSource transformSource = new StreamSource(xsltfile);

						FopFactory fopFactory = FopFactory.newInstance();
						// a user agent is needed for transformation
						FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
						//
						Map rendererOptions = foUserAgent.getRendererOptions();
						rendererOptions.put("encryption-params",
								new PDFEncryptionParams(null, "password",
										true, false, true, true));

						foUserAgent.getRendererOptions().putAll(rendererOptions);
						Transformer xslfoTransformer;
						try {
							xslfoTransformer = getTransformer(transformSource);
							// Construct fop with desired output format
							Fop fop;
							try {
								fop = fopFactory.newFop(MimeConstants.MIME_PDF,foUserAgent, outStream);
								// Resulting SAX events (the generated FO)
								// must be piped through to FOP
								Result res = new SAXResult(fop.getDefaultHandler());
								// Start XSLT transformation and FOP processing
								try {
									// everything will happen here..
									xslfoTransformer.transform(xmlSource, res);
									HttpSession session = ivHttpRequest.getSession(false);
									session.setAttribute("downloadpdfFile", outStream.toByteArray());
								} catch (Exception e) {
									LOGGER.error("fop Factory Exception 1: " + e.getMessage());
								}
							} catch (Exception e) {
								LOGGER.error("fop Factory Exception 2: " + e.getMessage());
							}
						} catch (Exception e) {
							LOGGER.error("fop Factory Exception 3: " + e.getMessage());
						}
					} catch (Exception e) {
						LOGGER.error("fop Factory Exception 4: " + e.getMessage());
					}
				} else {

					XMLToHtmlBean xmlTranformer = new XMLToHtmlBean(
							ivServletContext.getRealPath(""));
					htmlResult = xmlTranformer.transformXml(sStyleSheetFile,
							xmlForTransform);

					// Check if htmlResult is empty - indicates an error in the
					// transform
					if (htmlResult.equals("")) {
						htmlResult = handleBlankHtmlResult(sStyleSheetFile,
								sXml);
					}

					timeInMillis = System.currentTimeMillis() - timeInMillis;
					LOGGER.info("Transform - Completed server response transform. Took "
							+ Long.toString(timeInMillis) + " ms.");
					if (getRequestTimer() != null) {
						getRequestTimer().setTransformTime(timeInMillis);
					}

					// Remove all line feeds before end tags
					if (!ivResponseType.equals("XML.APPLICATION")
							&& !ivResponseType.equals("XML.REPORT")) {
						htmlResult = processEndTags(htmlResult);
					}
					// Save the HTML result
					// if its a report populate the html result into
					// ivResponseXml
					if (ivResponseType.equals("XML.REPORT")) {
						ivResponseXml = htmlResult;
					} else {
						ivResponseHtml = htmlResult;
					}
					ivRequest.setAttribute("downloadFile", htmlResult);
				}
			} catch (Exception e) {
				// Build the error result in HTML
				LOGGER.error("transformResult() error : ", e);
				buildErrorResponse("XML Response cannot be transformed to HTML");
				return;
			}
		} else {
			// Don't transform here, let the browser do it - add the stylsheet
			// info to the XML
			LOGGER.info("Transform - NO");

			// Check whether the xml string already contains the XML_HEADER and
			// set a pointer to the first element succeeding it
			int sXmlStartIndex = 0;
			if (sXml.indexOf("<?xml version=") > -1) {
				sXmlStartIndex = sXml.indexOf("<", 10);
			}

			String sXmlHeader = "<?xml-stylesheet type='text/xsl' href='..";

			// Remove the last '/' if it's an error message so it can be
			// resolved by the browser
			if (sStyleSheetFile.equals(MESSAGES_XSL)) {
				sXmlHeader = "<?xml-stylesheet type='text/xsl' href='..";
			}
			String xmlResult = XML_HEADER;
			xmlResult += sXmlHeader + sStyleSheetFile + "'?>";
			xmlResult += sXml.substring(sXmlStartIndex, sXml.length());
			ivResponseXml = xmlResult;
		}

	}

	/**
	 * Generates some html to alert the user that there's an error.
	 *

	 * @param styleSheet
	 *            The stylesheet used to transform the result
	 * @param response
	 *            The xml to transform
	 * @return Some html to tell the user there was an error, better than a
	 *         blank screen.
	 */
	private String handleBlankHtmlResult(String styleSheet, String response) {
		// Log the request and response
		StringBuilder logmsg = new StringBuilder();
		logmsg.append("A blank result was returned from the transform process\n");
		logmsg.append("styleSheet=" + styleSheet + ", applied to xml="
				+ response);
		LOGGER.error(logmsg.toString());

		// Build an error page
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		html.append("<html><head><title>Error</title></head>");
		html.append("<body>A transform error has occured, please see the log files for details.</body></html>");
		return html.toString();
	}

	protected static String replaceAll(String source, String searchString,
			String replaceWith) {
		int iCurrentPos = 0;
		int iSearchStringPos = 0;
		StringBuffer sbNewText = new StringBuffer();
		int iSearchStringLength = searchString.length();

		while ((iSearchStringPos != -1) && (iCurrentPos < source.length())) {
			iSearchStringPos = source.indexOf(searchString, iCurrentPos);
			if (iSearchStringPos != -1) {
				// Replace this occurrence of searchString
				sbNewText.append(source
						.substring(iCurrentPos, iSearchStringPos));
				sbNewText.append(replaceWith);
				iCurrentPos = iSearchStringPos + iSearchStringLength;
			}
		}

		if (iCurrentPos < source.length()) {
			// Still some remaining text after the last searchString so append
			// it
			sbNewText.append(source.substring(iCurrentPos, source.length()));
		}

		return (sbNewText.toString());
	}

	protected static String processEndTags(String source) {
		// Removes end of line characters and space characters before end tags
		// in HTML

		// Process each line removing spaces before end tags
		StringBuffer sbResult = new StringBuffer();
		String sLineSeparator = System.getProperty("line.separator");
		StringTokenizer sTokens = new StringTokenizer(source, sLineSeparator);

		while (sTokens.hasMoreTokens()) {
			String sLine = sTokens.nextToken();
			if (sLine.trim().startsWith(END_TAG)) {
				// Spaces before end tag on this line
				int iEndTagPos = sLine.indexOf(END_TAG);
				sLine = sLine.substring(iEndTagPos, sLine.length());
			}

			sbResult.append(sLine);
			sbResult.append(sLineSeparator);
		}

		// Process result removing end of line characters before end tags
		String sResult = replaceAll(sbResult.toString(), sLineSeparator
				+ END_TAG, END_TAG);

		return (sResult);
	}

	/**
	 * Returns the first directory name in the root context path
	 */
	protected String getContextRootName() {
		String contextRootName = ivHttpRequest.getContextPath();
		return contextRootName;
	}

	public void setRequestTimer(RequestTimer myTimer) {
		ivRequestTimer = myTimer;
	}

	protected RequestTimer getRequestTimer() {
		return ivRequestTimer;
	}

	protected String getSkinName() {
		String sSkinName = ivRequest.getValue("skin");
		if ((sSkinName == null) || (sSkinName.equals(""))) {
			sSkinName = DEFAULT_SKIN_NAME;
		}

		return (sSkinName);
	}

	protected String getCommand() {
		String sCommand = ivRequest.getValue("command");

		if ((sCommand != null) && (!sCommand.equals(""))) {
			sCommand = sCommand.toLowerCase();
		} else {
			sCommand = "";
		}

		return (sCommand);
	}

	protected String getLanguage() {
		String sLanguage = ivRequest.getValue("language");

		if ((sLanguage == null) || (sLanguage.equals(""))) {
			sLanguage = DEFAULTLANG; // Choose the default language if not set
			LOGGER.info("Using Default Language : " + DEFAULTLANG);
		}

		return (sLanguage);
	}

	// Read the Browser Parameters file
	public void readBrowserParameters() {
		// Read the parameters from the XML file browserParameters.xml
		BrowserParameters params = new BrowserParameters(
				ivServletConfig.getServletContext());
		ivParameters = params.getParameters();

		// Get the Server Connection Method parameter
		String connectionMethod = readParameterValue(PARAM_SERVER_CONN_METHOD);

		if ((connectionMethod == null) || (connectionMethod.equals(""))) {
			LOGGER.info("Parameter '" + PARAM_SERVER_CONN_METHOD
					+ "' not found - using default setting.");
			ivServerConnectionMethod = DEFAULT_SERVER_CONN_METHOD;
		} else {
			ivServerConnectionMethod = connectionMethod.toUpperCase();

			if ((!ivServerConnectionMethod.equals(CONNECT_METHOD_INSTANCE))
					&& (!ivServerConnectionMethod.equals(CONNECT_METHOD_AGENT))
					&& (!ivServerConnectionMethod.equals(CONNECT_METHOD_JMS))) {
				LOGGER.error("Invlaid Parameter value for '"
						+ PARAM_SERVER_CONN_METHOD
						+ "' - using default setting.");
				ivServerConnectionMethod = DEFAULT_SERVER_CONN_METHOD;
			}
		}

		// Set the transformer

		String clientTransform = System.getProperty("client.transform");

		if (clientTransform == null) {
			ivServerTransform = true;
		} else {
			ivServerTransform = false;
		}

		// Get the list of valid web server skin names
		String skinNames = readParameterValue(PARAM_SKIN_NAMES);

		if ((skinNames == null) || (skinNames.equals(""))) {
			LOGGER.info("Parameter '" + PARAM_SKIN_NAMES
					+ "' not found - only valid skin is 'default'.");
			ivWebServerSkinNames = DEFAULT_SKIN_NAME;
		} else {
			ivWebServerSkinNames = skinNames;
		}

		// Set the Allow Resize parameter
		String allowResize = readParameterValue(PARAM_ALLOW_RESIZE);

		if ((allowResize == null) || (allowResize.equals(""))) {
			LOGGER.info("Parameter '" + PARAM_ALLOW_RESIZE
					+ "' not found - using default setting.");
			// default to allow resizing
			ivAllowResize = "YES";
		} else {
			ivAllowResize = allowResize;
		}

		ivDocumentServiceChannel = readParameterValue(PARAM_DOCUMENT_SERVICE_CHANNEL);

		ivProduct = (String) ivServletContext.getAttribute(PARAM_PRODUCT);

		// Set the Allow Toolbox parameter
		String allowToolbox = readParameterValue(PARAM_ALLOW_TOOLBOX);

		if ((allowToolbox == null) || (allowToolbox.equals(""))) {
			LOGGER.info("Parameter '" + PARAM_ALLOW_TOOLBOX
					+ "' not found - using default setting.");
			// default to allow Toolbox
			ivAllowToolbox = "yes";
		} else {
			ivAllowToolbox = allowToolbox.toLowerCase();
		}
	}

	protected String readParameterValue(String sParameter) {
		return ((String) ivParameters.getProperty(sParameter));
	}

	// Returns the type of web application being run - BRWOSER or ARC-IB
	public String getProductName() {
		return ivProduct;
	}

	// Initialise the instance variables depending on the status and request
	protected void initialiseInstances() {
		ivInstanceContext = "";
		ivInstanceUser = "";
		ivInstanceRequest = "";

		// Get any instance parameter from the request
		String reqInstance = ivRequest.getValue("instance");

		// If we have been asked to change the connection to an instance then
		// save it in the context
		if ((ivCommand != null) && (!ivCommand.equals(""))) {
			if (ivCommand.equals("changeconnection")) {
				ivServletContext.setAttribute(BranchConstants.INSTANCE_CONTEXT,
						reqInstance);
				ivInstanceContext = reqInstance;

				// If we are in a Branch server, then change the active instance
				// to the new one and save it in the servlet context
				if (ivBranch != null) {
					// Only allowed for Branch Administrators
					if (ivBaUser) {
						ivBranch.setActiveInstance(reqInstance);

						if (reqInstance.equals(ivBranch.getOnlineInstance())) {
							ivBranch.setStatus(BranchConstants.BRANCH_ONLINE);
						} else if (reqInstance.equals(ivBranch
								.getOfflineInstance())) {
							ivBranch.setStatus(BranchConstants.BRANCH_OFFLINE);
						} else {
							LOGGER.error("Invalid instance name supplied for switching connection.");
						}

						ivServletContext.setAttribute(
								BranchConstants.SERVLET_CONTEXT_BRANCH,
								ivBranch);
					} else {
						// Not allowed, error will be flagged up in
						// processRequest
					}
				}
			} else {
				// Fetch the existing context instance if there is one
				ivInstanceContext = (String) ivServletContext
						.getAttribute(BranchConstants.INSTANCE_CONTEXT);

				// Extract any instance in the request
				ivInstanceRequest = reqInstance;
			}

			ivInstanceUser = getUserInstance();

			displayInstancesInfo();
		}
	}

	// Initialise the server connection
	protected void initialiseServerConnection() throws TBrowserException {
		// Set-up the connection to the server
		ConnectionEngine connEngine = new ConnectionEngine(
				ivServerConnectionMethod, getInstanceName(), ivHttpRequest,
				ivParameters, ivBranch);
		ivConnection = connEngine.initialiseConnection();
		// Check if there were any errors during the connection setup, but only
		// if there isn't already an error (eg in validation).
		if (!ivError) {
			ivError = connEngine.getError();
			ivErrorText = connEngine.getErrorText();
		}
		// needlessly each request connection object is stored in servletContext
		// object since it’s not re-used anymore. It causes performance issue
		// due to not removing the context object stored items until the
		// webserver restart.
		// ivServletContext.setAttribute("ivConnection", ivConnection );
	}

	// Get the name of the TC Instance to use for our connection - depending on
	// it's type
	protected String getInstanceName(String type) {
		if (type.equals(BranchConstants.INSTANCE_CONTEXT_TYPE)) {
			return (ivInstanceContext);
		} else if (type.equals(BranchConstants.INSTANCE_USER_TYPE)) {
			return (ivInstanceUser);
		} else if (type.equals(BranchConstants.INSTANCE_REQUEST_TYPE)) {
			return (ivInstanceRequest);
		} else {
			return ("");
		}
	}

	// Get the name of the TC Instance to use for our connection
	protected String getInstanceName() {
		// If an instance name has been passed in the request then use this.
		// If we are a branch web server, and the user is a Branch
		// Administrator, then use the offline instance
		// (i.e. route the request to the branch server )
		// If we are a branch web server, then use the appropriate
		// online/offline status
		// Otherwise use the one defined in the parameter file
		String instanceName = "";
		String paramInstance = readParameterValue(PARAM_INSTANCE_NAME); // Parameter
																		// instance
		String sAction = getAction();

		// Check if we are in a branch or not
		if ((ivInstanceRequest != null) && (!ivInstanceRequest.equals(""))) {
			// Use the request instance passed
			instanceName = ivInstanceRequest;
			LOGGER.info("Using TC Instance Name from the request : "
					+ ivInstanceRequest);
		} else if (ivBranch != null) {
			// We are in a branch so use the status of this branch
			String brInstance = ivBranch.getActiveInstance();
			String brStatus = ivBranch.getStatus();

			if ((ivBaUser)
					|| ((ivCommand != null) && (ivCommand
							.equals("branchadminlogin")))) {
				instanceName = ivBranch.getOfflineInstance();
				LOGGER.info("Branch Administrator request - Using the Branch Offline TC Instance Name : "
						+ instanceName);
			} else if ((ivCommand != null)
					&& (ivCommand.equals("smartclient"))
					&& ((sAction != null) && (!sAction.equals("")) && (sAction
							.equals("branchadminlogin")))) {
				instanceName = ivBranch.getOfflineInstance();
				LOGGER.info("Branch Administrator login request - Using the Branch Offline TC Instance Name : "
						+ instanceName);
			} else if ((brInstance != null) && (!brInstance.equals(""))) {
				instanceName = brInstance;
				LOGGER.info("Using the Branch Active TC Instance Name : "
						+ instanceName);
			} else if ((brStatus != null) && (!brStatus.equals(""))) {
				if (brStatus.equals(BranchConstants.BRANCH_ONLINE)) {
					instanceName = ivBranch.getOnlineInstance();
					LOGGER.info("Using the Branch Online TC Instance Name : "
							+ instanceName);
				} else {
					instanceName = ivBranch.getOfflineInstance();
					LOGGER.info("Using the Branch Offline TC Instance Name : "
							+ instanceName);
				}
			} else {
				instanceName = ivBranch.getOnlineInstance();
				LOGGER.info("No Branch Status, so using the Branch Online TC Instance Name : "
						+ instanceName);
			}
		} else if ((ivServerConnectionMethod.equals(CONNECT_METHOD_INSTANCE))
				&& (paramInstance != null) || (!paramInstance.equals(""))) {
			instanceName = paramInstance;
			LOGGER.info("Using TC Instance Name from the parameter file : "
					+ instanceName);
		} else {
			LOGGER.info("No TC Instance Name to use for connection.  Unable to connect to the server.");
		}

		return (instanceName);
	}

	// Save any web server fields for adding to the response later
	private void saveWebServerFields() {
		ivWebFields = new HashMap();
		Enumeration reqFields = ivRequest.getParameterNames();

		while (reqFields.hasMoreElements()) {
			String fieldName = (String) reqFields.nextElement();

			if (fieldName.startsWith("WS_")) {
				// We have a web server field to save
				String fieldValue = ivRequest.getValue(fieldName);
				ivWebFields.put(fieldName, fieldValue);
			}
		}
	}
	public void setKeepAliveTimeout( int keepAliveTimeout )
	{
		Integer ivKeepAliveTimeout = new Integer(keepAliveTimeout);
		ivSession.setAttribute( KEEP_ALIVE_TIMEOUT, ivKeepAliveTimeout );
	}

	public int getKeepAliveTimeout()
	{
		Integer keepAliveTimeout = (Integer) ivSession.getAttribute(KEEP_ALIVE_TIMEOUT);
		return keepAliveTimeout.intValue();
	}
	// Add the saved web server fields to the response XML
	private String addWebServerFields(String sXml, String userName) {
		String resultXml = sXml;

		if (USER_LOGIN_COMMAND.equals(ivCommand)) {

			// To check time out even if useKeepSessionalive is set as "NO"
			// If it is a login request .. (do the check this way round, just in
			// case ivCommand was null )

			// Get the session timeout, and return it in the web details section
			// note that the value could be negative, meaning "no timeout"
			int timeout = ivSession.getMaxInactiveInterval();
			// Since the getMaxInactiveInterval returns the number of seconds,
			// we have to convert it into minutes
			// to get the proper session timout

			// Check for Chance of session will never be invalidated, rare but
			// can happen
			if (timeout > 0) {
				timeout = timeout / 60;
			}
			try {
				// Get the T24 timeout (note: this requires a round trip to T24)
				XMLRequestManager xmlManager = new XMLRequestManager(ivRequest,
						getXmlTemplates());
				String currentToken = xmlManager.getNodeValue(sXml, "token");
				if (currentToken != null && !currentToken.equals("")) {
					String t24timeoutEnquiryResponse = getT24TimeoutEnquiryResponse(
							userName, currentToken);
					if (t24timeoutEnquiryResponse != null) {
						// Set the new token (into the XML, it is read out and
						// set later)
						// todo: don't use String manipulation (via
						// XMLRequestManager) for this!
						String newToken = xmlManager.getNodeValue(
								t24timeoutEnquiryResponse, "token");
						if ((newToken != null) && (!newToken.equals(""))) {
							int startIndex = sXml.indexOf("<token>");
							int endIndex = sXml.indexOf("</token>");
							sXml = sXml.substring(0, startIndex + 7) + newToken
									+ sXml.substring(endIndex);
						}

						// Parse the response to obtain the timeout value
						// todo: could the query flag the response to make it
						// easier to find?
						String t24TimeoutStr = xmlManager.getNodeValue(
								t24timeoutEnquiryResponse, "r");
						if (t24TimeoutStr != null) {
							int startIndex = t24TimeoutStr.indexOf("<cap>");
							if (startIndex > 0) {
								startIndex = t24TimeoutStr.indexOf("<cap>",
										startIndex + 4) + 5;
								int endIndex = t24TimeoutStr.indexOf("</cap>",
										startIndex);
								if (endIndex > 0) {
									t24TimeoutStr = t24TimeoutStr.substring(
											startIndex, endIndex);
									if (t24TimeoutStr != null
											&& t24TimeoutStr != "") {
										int t24Timeout = Integer
												.parseInt(t24TimeoutStr);

										if (timeout < 0) {
											timeout = t24Timeout;
										} else {
											timeout = Math.min(t24Timeout,
													timeout);
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.warn("Error running BROWSER.TIMEOUT enquiry : ", e);
			}

			ivWebFields.put(KEEP_ALIVE_TIMEOUT, Integer.toString(timeout));
                setKeepAliveTimeout(timeout);
		}
        else
        {
        	if(ivProduct.equals(PRODUCT_ARC_IB))
        	{
        	int timeout = getKeepAliveTimeout();
        	ivWebFields.put( KEEP_ALIVE_TIMEOUT, Integer.toString(timeout));
        }

        }



		// Check we have a valid response, and then add the fields in to the
		// string after the "responseDetails" opening XML tag
		if ((ivWebFields != null) && (ivWebFields.size() != 0)) {
			int startTagPos = sXml.indexOf(XML_TAG_RESP_DETAILS);

			if (startTagPos != -1) {
				// The response string is valid
				String sFieldsXml = "<webDetails>";
				sFieldsXml += "<singleLoadingIcon>"
						+ BrowserServlet.singleLoadingIcon
						+ "</singleLoadingIcon>";
				sFieldsXml += "<clearSelection>" + BrowserServlet.clearSelection + "</clearSelection>";

				Set fields = ivWebFields.entrySet();
				Iterator it = fields.iterator();

				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					String fieldName = (String) entry.getKey();
					String fieldValue = (String) entry.getValue();
					// decode the UTF-8 characters in webserver fields that are
					// sent as part of the encoded URL
					// Should be done only for non IE browser as IE follows
					// different encoding mechanism
					boolean isIE = userAgent.contains("MSIE");
					if (!isIE) {
						fieldValue = Utils.decodeUTF8(fieldValue);
					}
					// Escape out any '&' characters
					fieldValue = replaceAll(fieldValue, "&",
							XML_ESCAPED_AMPERSAND);
					// Escape out any '<' characters
					fieldValue = replaceAll(fieldValue, "<",
							XML_ESCAPED_LESSTHAN);
					// Escape out any '>' characters
					fieldValue = replaceAll(fieldValue, ">",
							XML_ESCAPED_GREATERTHAN);

					sFieldsXml += "<" + fieldName + ">" + fieldValue + "</"
							+ fieldName + ">";
				}

				sFieldsXml += "</webDetails>";

				// Now add the fields to the response XML
				resultXml = sXml.substring(0, startTagPos + 17) + sFieldsXml
						+ sXml.substring(startTagPos + 17, sXml.length());
			}
		}

		return (resultXml);
	}

	// Add XML tag to toggle resizing of windows
	protected String addAllowResizeTag(String sXml, String afterXmlNode) {
		String resultXml = sXml;

		if (ivAllowResize != null) {
			int startTagPos = sXml.indexOf(afterXmlNode);

			if (startTagPos != -1) {
				int tagLen = afterXmlNode.length();
				int endTagPos = startTagPos + tagLen;

				// Now add the <allowResize> tag to the response XML
				resultXml = sXml.substring(0, endTagPos) + XML_TAG_ALLOW_RESIZE
						+ ivAllowResize + XML_TAG_ALLOW_RESIZE_C
						+ sXml.substring(endTagPos, sXml.length());
			}

		}

		return (resultXml);
	}

	// Add the saved branch details to the response XML - if we are a branch
	protected String addBranchDetails(String sXml, String afterXmlNode) {
		String resultXml = sXml;

		// Add the branch details if we are in a branch environment after the
		// XML tag supplied
		Branch branch = getBranch();

		if (branch != null) {
			int startTagPos = sXml.indexOf(afterXmlNode);

			if (startTagPos != -1) {
				int tagLen = afterXmlNode.length();
				int endTagPos = startTagPos + tagLen;

				// Get the branch details in XML format
				String sBranchXml = branch.toXmlDetails();

				// Now add the branch details to the response XML
				resultXml = sXml.substring(0, endTagPos) + sBranchXml
						+ sXml.substring(endTagPos, sXml.length());
			}
		}

		return (resultXml);
	}

	// Add the saved branch details (in full) to the response XML - if we are a
	// branch
	protected String addFullBranchDetails(String sXml, String afterXmlNode) {
		String resultXml = sXml;

		// Add the branch details if we are in a branch environment after the
		// XML tag supplied
		if (ivBranch != null) {
			int startTagPos = sXml.indexOf(afterXmlNode);

			if (startTagPos != -1) {
				int tagLen = afterXmlNode.length();
				int endTagPos = startTagPos + tagLen;

				// Get the branch details in XML format
				String sBranchXml = ivBranch.toXml();

				// Now add the branch details to the response XML
				resultXml = sXml.substring(0, endTagPos) + sBranchXml
						+ sXml.substring(endTagPos, sXml.length());
			}
		}

		return (resultXml);
	}

	// Get the branch details from the servlet context
	public Branch getBranch() {
		return ((Branch) ivServletContext
				.getAttribute(BranchConstants.SERVLET_CONTEXT_BRANCH));
	}

	// Get the branch strings from the servlet context
	public BranchStrings getBranchStrings() {
		return ((BranchStrings) ivServletContext
				.getAttribute(BranchConstants.BRANCH_STRINGS_CONTEXT));
	}

	// Get the Ofs request string from the response XML
	private String getOfsStringfromResponse(String sXml) {
		String sOfsReq = "";
		int startTagPos = sXml.indexOf(BranchConstants.BRANCH_OFS_REQ_STRING);

		if (startTagPos != -1) {
			int startTagLength = BranchConstants.BRANCH_OFS_REQ_STRING.length();
			int endTagPos = sXml
					.indexOf(BranchConstants.BRANCH_OFS_REQ_STRING_C);
			sOfsReq = sXml.substring(startTagPos + startTagLength, endTagPos);

			LOGGER.debug("Extracted Ofs request string : " + sOfsReq);
		}

		return (sOfsReq);
	}

	// Run the ofs request against the local branch via a Utility Request
	private void branchSyncRequest(String sOfsReq) {
		// Synchronise the request to the Branch in the background using a
		// Thread
		BranchSync brSync = new BranchSync(ivBranch, sOfsReq, getPassword(),
				ivServletContext, ivHttpRequest.getUserPrincipal(),
				ivHttpRequest.isSecure(), ivClientIP, ivParameters,
				ivHttpRequest);
		brSync.start();

		// If the request was for a change password the store the new password
		if (ivCommand.equals("repeatpassword")) {
			setPassword(ivRequest.getValue("password"));
		}
	}

	// Change the branch status of the local branch via a Utility Request
	protected void branchStatusUpdateRequest() {
		try {
			// Create a TC connection for the offline instance
			ConnectionBean branchConnection = new InstanceConnector(
					ivHttpRequest, ivParameters);
			branchConnection.setupServer(ivBranch.getOfflineInstance(), 0);

			T24Connection con = new T24WebConnection(branchConnection,
					this.ivClientIP);
			con.setRequestToken(this.getRequestToken());
			con.setRequestCompany(this.getCompanyName());


			String sResponse = null;

			String routineArgs = BranchConstants.XML_BRANCH_NAME_TAG
					+ ivBranch.getName()
					+ BranchConstants.XML_BRANCH_NAME_TAG_C;
			routineArgs += BranchConstants.XML_BRANCH_STATUS_TAG
					+ ivBranch.getStatus()
					+ BranchConstants.XML_BRANCH_STATUS_TAG_C;

			// Build a utility request and run the request against the branch
			// server
			UtilityRequest ur = new UtilityRequest(
					BranchConstants.BRANCH_STATUS_UPDATE_ROUTINE, routineArgs,
					con);
			sResponse = ur.sendRequest();

			LOGGER.debug("Response from Branch Status update : " + sResponse);
		} catch (Exception e) {
			LOGGER.error("Error updating branch status at the branch.  Exception : "
					+ e.getMessage());
		}
	}

	// Get the action string from the request
	protected String getAction() {
		String sAction = ivRequest.getValue("action");

		if ((sAction != null) && (!sAction.equals(""))) {
			sAction = sAction.toLowerCase();
		}

		return (sAction);
	}

	private void displayInstancesInfo() {
		if (ivBranch != null) {
			LOGGER.debug("Instance [ Context ] is : " + ivInstanceContext);
			LOGGER.debug("Instance [ Session ] is : " + ivInstanceUser);
			LOGGER.debug("Instance [ Request ] is : " + ivInstanceRequest);
		}
	}

	/**
	 * Get the T24 timeout for the current user. This involves a round trip to
	 * T24, and for a specific enquiry (BROWSER.TIMEOUT) to be installed on the
	 * server.
	 *

	 * @return the T24 timeout for the current user, or null if it cannot be
	 *         found
	 */
	private String getT24TimeoutEnquiryResponse(String userName,
			String currentToken) {
		String responseXml = null;

		try {
			// Construct the request XML
			// String currentToken = this.getRequestToken();

			StringBuffer getTimoutEnquiryOFS = new StringBuffer();
			getTimoutEnquiryOFS
					.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ofsSessionRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" schemaLocation=\"\\WEB-INF\\xml\\schema\\ofsEnquiryRequest.xsd\"><requestType>OFS.ENQUIRY</requestType><token>");
			getTimoutEnquiryOFS.append(currentToken);
			getTimoutEnquiryOFS
					.append("</token><requestArguments><ofsmlEnquiryRequest><requestType>OFS.ENQUIRY</requestType><enqaction>RUN</enqaction>   <enqname>BROWSER.TIMEOUT</enqname><message><criteria><fieldName>@ID</fieldName><operand>EQ</operand><value>");
			getTimoutEnquiryOFS.append(userName);
			getTimoutEnquiryOFS
					.append("</value></criteria></message></ofsmlEnquiryRequest></requestArguments></ofsSessionRequest>");

			// Invoke the request
			BrowserResponse response = this
					.sendOfsRequestToServer(getTimoutEnquiryOFS.toString());
			String error = response.getError();
			if (error != null && error != "") {
				LOGGER.warn("Error running BROWSER.TIMEOUT enquiry : " + error);
			} else {
				responseXml = response.getMsg();
			}
		} catch (Exception e) {
			LOGGER.warn("Error running BROWSER.TIMEOUT enquiry : ", e);
		}

		return responseXml;
	}

	// Method to retrieve the ID for the DocumentService servlet
	public String getDocumentServiceId() {
		return ivDocumentServiceId;
	}

	// Method to set document ID for document download.
	public String setDocumentServiceId(String DocumentServiceId) {
		return ivDocumentServiceId = "DOC " + DocumentServiceId;
	}

	public String getDocumentServiceChannel() {
		return ivDocumentServiceChannel;
	}

	public String getDocumentServiceInstance() {
		return getInstanceName();
	}

	public boolean getDocumentServiceMultiDownload() {
		return ivMultiDownload;
	}

	public void dispalyErrorMessage(String sErrorMsg) {
		ivServerTransform = true;
		buildErrorResponse(sErrorMsg);
	}

	protected String getSecurityViolationMessage(String errorCode) {
		String message = "";
		String codeMessage = "  (Code: " + errorCode + ")";

		if (ivProduct.equals(PRODUCT_ARC_IB)) {
			message = SECURITY_VIOLATION_ERROR_ARCIB;
		} else {
			message = SECURITY_VIOLATION_ERROR;
		}

		message = message + codeMessage;

		return message;
	}

	private Transformer getTransformer(StreamSource streamSource) {
		// setup the xslt transformer
		TransformerFactoryImpl impl = new TransformerFactoryImpl();

		try {
			return impl.newTransformer(streamSource);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
