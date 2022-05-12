////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BrowserServlet
//
//  Description   :   Servlet for controlling OFS requests between the browser
//                    and the server.
//
//  Modifications :
//
//    30/04/02   -    Initial Version.
//    28/05/02	 -	  Interfaced with EJB.34
//	  06/09/02	 -	  Save the BrowserBean in a session so multiple client
//					  requests are co-ordinated correctly.
//    11/09/02	 -	  Log message if session not found.
//    05/11/02   -    Removed WAR Version.
//	  21/11/02	 -    Log Event - On/Off option
//    22/11/02   -    Store Compiled Stylesheets
//    02/01/03   -    Allow processing of the quickguide command.
//    08/07/03   -    Changed the signature call to BrowserBean constructor
//						Now passes in an XMLTemplateManager as well.
//					  Instantiates the new class XMLTemplateManager in the init
//	  14/01/04   -    Now clone XMLTemplateManager before passing it into BrowserBean
//					  Thus every browser bean has its own copy of the object
//	  21-Aug-06		  Now text documents can be viewed setting content
//					  type as text/plain.
//	  01/09/06	- 	  Changes to download reports.
//	24/09/2007	UTF-8 Changes
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.servlets;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;
import java.util.ResourceBundle;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.beans.BrowserBean;
import com.temenos.t24browser.branch.Branch;
import com.temenos.t24browser.branch.BranchConstants;
import com.temenos.t24browser.branch.BranchParameters;
import com.temenos.t24browser.branch.BranchStrings;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.request.SessionData;
import com.temenos.t24browser.security.VariableSubstitutionHttpServletRequest;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.FileManager;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.RequestTimer;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.xml.XMLTemplateManager;
import com.temenos.tocf.tbrowser.TBrowserException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is the main entrypoint for Browser and ARC-IB requests.
 */
public class BrowserServlet extends HttpServlet implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserServlet.class);
	
	/** The iv war version. */
	private String ivWarVersion = "";								// WAR Version
	
	/** The iv server ofs source. */
	private String ivServerOfsSource = "";							// Server OFS.SOURCE key
 
	/** The Constant BROWSER_PARAMETERS_INIT_PARAM. */
	public static final String BROWSER_PARAMETERS_INIT_PARAM = "browserParameters";
	
	/**The Constant is to read the tag 'Product' from BrowserParameters.xml */
	public static String productName = "";
	
	/**The Constant is to read form BrowserParameters.xml for displaying loading icon*/
	public static String singleLoadingIcon = "";
	
	/**The Constant is to read form BrowserParameters.xml for displaying user name in login screen*/
	public static String showUserName = "";
	
	public static String clearSelection = "";
	
	/**The Constant is to read the tag 'XFRAME_HEADER_VALUE' from BrowserParameters.xml */
	public static String XFRAME_HEADER_VALUE = "SAMEORIGIN";
	
	/** The Constant LOGIN_PAGE_LINKS_PARAM. */
	public static final String LOGIN_PAGE_LINKS_INIT_PARAM = "loginPageLinks";
	
	/** Value='nochangeFields'. The name of the file containing a list of fields who's value should not change between requests */
	public static final String NOCHANGE_FIELDS_INIT_PARAM = "nochangeFields";
	
	/** The Constant TOKEN_SESSION_NAME. */
	private static final String TOKEN_SESSION_NAME = "BrowserToken";			// Used to save the Token in the session
	
	/** The Constant TOKEN_SEQUENCE_NUMBER. */
	private static final String TOKEN_SEQUENCE_NUMBER = "TokenSequence";
	
	/** The Constant TOKEN_DELIMITER. */
	private static final String TOKEN_DELIMITER = ":";
	
	/** The Constant SESSION_SIGN_ON_NAME. */
	private static final String SESSION_SIGN_ON_NAME = "BrowserSignOnName";	// Used to save the user's sign on name in the session
	
	/** The Constant SESSION_USER_ID. */
	private static final String SESSION_USER_ID = "BrowserUserId";			// Used to save the user's Id in the session
	
	/** The Constant PARAM_SERVER_CONN_METHOD. */
	private static final String PARAM_SERVER_CONN_METHOD = "Server Connection Method";
	
	/** The Constant PARAM_SERVER_TRANSFORM. */
	private static final String PARAM_SERVER_TRANSFORM   = "Use Transformer";
	
	/** The iv template manager. */
	private XMLTemplateManager ivTemplateManager = null;       //Stores the xml templates read in from disk
	
	/** The iv parameters. */
	private PropertyManager ivParameters;
	
	/** The request type for this request */
	private String ivRequestType = null;
	
	/** Routine args of current request used to clear token */
	private String ivRoutineArgs=null;
	
	// Delimiter characters used for parsing display field strings
	private static final String FIELD_DELIMITER = "\\|";
	
	/** The name of the file containing the Browser version information */
	private static final String VERSION_FILE = "version.xml";	// The file containing the Browser version
	
	
	// Called on first invocation of Servlet
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init()
		throws javax.servlet.ServletException
	{
		// TODO: Temporarily, set up the debug properties (will be removed when we have no need for the debug servlet)
		ServletContext context = getServletContext();
		DebugUtils.init(context.getRealPath(""));

		LOGGER.info("startup");
		
		// Read the parameters and initialise the GLOBUS environment
		readParameters();
		
		// Read in and store the XML templates in a manager
		ivTemplateManager = new XMLTemplateManager(this.getServletContext());
	}
	
	// Called on first invocation of Servlet after the init method
	// Builds the login page for display
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(
		javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{	
		
		String method = Utils.getValue( request, "method");    //request.getParameter("method");
		
		// If the method is POST then we are coming through a URL so run the doPost code
		if ( ( method != null ) && ( method.toLowerCase().equals( "post") ) )
		{
			doPost( request, response );
			return;
		}
		
		LOGGER.debug("HttpServletRequest: \n" + 
				com.temenos.t24browser.utils.Logger.replacePassword(request));
		
		// if command is set to refresh then re-read system parameters and recompile xsl stle sheets
		checkRefreshCommand( request );
		
		BrowserBean browserBean = null;
		
		//First Servlet request so display login page and create a session
		HttpSession session = request.getSession();

		try
		{
			String sResponse = "";
			String sResponseType = "";
			
			// Get the command - as this can determine the instance we use for the connection
			String command = getCommand( request );
			
			if ( ( command != null ) && ( ! command.equals("") ) && ( command.equals("version") ) )
			{
				// Read the version file
				sResponse = getVersion(); 
				sResponseType = "XML";
			}
			else
			{
				synchronized(session)
				{
					browserBean = new BrowserBean( getServletConfig(), getTemplateManager(), Utils.getClientIpAddress( request ), request );
					if (browserBean.requestError()) {
						throw new SecurityViolationException();
					}
				}
				
				LOGGER.info("doGet() method - build login page");
			
				browserBean.initLoginPage();
						
				if ( browserBean.requestError() )
				{
					// Log an error
					LOGGER.info("doGet() - destroy a session");
				}
	
				sResponse = browserBean.getResponse();
				sResponseType = browserBean.getResponseType();
			}
			
			displayResponse( sResponse, sResponseType, response );
		}
		catch (TBrowserException bex)
		{
			// An error occurred so display the error message
			displayBrowserException( bex, request, response );
		}
		catch (SecurityViolationException e) {
			handleSecurityViolationException(browserBean, request, response);
		}
		finally {
			if (browserBean != null && browserBean.getInvalidateSession() && session != null) {
				try {
					LOGGER.debug( "Destroying Session on display Login page : Id = " + session.getId() );
					session.invalidate();
				}
				catch (Exception e) {
					LOGGER.warn("Exception invalidating session: " + e.getMessage());
				}
			}
		}
	}
	
	// Called for each Browser command request
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException
	{
		LOGGER.debug( "***** Browser : Start Request ***********************************************" );
		long timeInMillis = System.currentTimeMillis(); // Start out timer asap		

		SessionData sessionData = new SessionData(request.getSession());
		LOGGER.debug( sessionData.toString());
		
		// Ensure the request parameters are treated as UTF-8 characters.
		// This should be done by the EncodingFilter, but just in case it hasn't been used...
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			LOGGER.error("Unable to set request character encoding to UTF8");
		}
		
		// Set the request type as is used multiple times 
		ivRequestType = Utils.getValue( request, "requestType"); 
		ivRoutineArgs = Utils.getValue( request, "routineArgs");
		
		// The following code is to invalidate the previous JSESSIONID . Before that take a backup of request attributes and place 
        // all those attributes in the new request object.	
        if( ivRequestType != null )   
		{
        	String authType = Utils.getValue( request, "AuthenticationType");
        	// Added check on authentication type so that if this is ARC-IB with 
        	// an external authentication mechanism, the session Id will not change. 
        	// This is hopefully a temporary measure so that 
            if( ivRequestType.equals("CREATE.SESSION") && !( authType != null &&  authType.equalsIgnoreCase("external")))
            {
                  Hashtable  hsh = new Hashtable();
                  HttpSession sobj = request.getSession();
                  Enumeration e = sobj.getAttributeNames();
                  ArrayList Attr_list = new ArrayList(); 
                
                  while(e.hasMoreElements())
                  {
                        String attribName = (String) e.nextElement();
                        LOGGER.debug("SAVING ATTRIBUTE: " + attribName);
                        Attr_list.add(attribName);
                        if(!attribName.equals(""))
                        {
                              try
                              {
                              Object AttribValue = sobj.getAttribute(attribName);
                              LOGGER.debug("ATTRIBUTE value: " + AttribValue.toString());
                              hsh.put(attribName, AttribValue);
                              }catch(Exception execp)
                              {
                                    System.out.println(execp.getMessage());
                              }
                              
                        }
                  }
                  // Before we invalidate the session we need to add a marker attribute.
                  LOGGER.debug( "Destroying Session on Login : Id = " + sobj.getId() );
                  // true logout.
                  sobj.setAttribute("SESSION_ID_CHANGE", Boolean.TRUE);
                  sobj.invalidate();
                  sobj = null;
                 
                  for (int lcnt=0;lcnt<Attr_list.size();lcnt++)
                  {
                        String attrName = (String) Attr_list.get(lcnt);
                        if(!attrName.equals(""))
                        {
                              Object AttribVal = (Object) hsh.get(attrName);
                              request.getSession().setAttribute(attrName,AttribVal);
                        }
                  }                   
             }
         }else
         {
        	 ivRequestType = "";
         }
        

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("HttpServletRequest: \n" + com.temenos.t24browser.utils.Logger.replacePassword(request));
		}
		
		// This should never occur - if it does then throw an exception
		if( Utils.getValue( request, "command") == null) 
		{
			 try 
             { 
			  
			        HttpSession invalidsession = request.getSession();
               if (invalidsession != null)  {           
            	    LOGGER.debug( "Destroying Session on display Login page : Id = " + invalidsession.getId() );
                   invalidsession.invalidate();
                }						
                   String sUrlPath = request.getContextPath();
                   Locale locale = request.getLocale();
   				   ResourceBundle labels = ResourceBundle.getBundle("errorMessages",locale);
   				   String value = labels.getString("url.loggedout");
                   String ivResponseUrl = sUrlPath + value;
                   response.sendRedirect(ivResponseUrl);
            
             } 
            
             catch (Exception io)
             { 
            
             LOGGER.warn("Exception invalidating session: " + io.getMessage());
             io.printStackTrace(); 
            
             }
             
              return;
			
              /*LOGGER.error("'command' parameter is null! Displaying excpetion. Referer=" + request.getHeader("Referer"));
			LOGGER.error("HttpServletRequest: \n" + com.temenos.t24browser.utils.Logger.replacePassword(request));
			throw new ServletException("POST request received but 'command' is empty. Returning.");*/
		}
		
		String sVersionResponse = "";
		String sVersionResponseType = "";
		
		// Get the command - as this can determine the instance we use for the connection
		String sVersionCommand = getCommand( request );
		
		if ( ( sVersionCommand != null ) && ( ! sVersionCommand.equals("") ) && ( sVersionCommand.equals("version") ) )
		{
			// Read the version file
			sVersionResponse = getVersion(); 
			sVersionResponseType = "XML";
			displayResponse( sVersionResponse, sVersionResponseType, response );
			return;
		}
		
        // Wrap the request with a wrapper object that will perform variable substitution
        // (e.g. converting "{user_id}" to the actual user id (this enables us to not send 
        // the user id to the client for security reasons).
        request = new VariableSubstitutionHttpServletRequest(request, ivParameters);
        
		HttpSession session = request.getSession();
		
		LOGGER.info("doPost() method");
		
		// Checks whether it is a download request
		boolean isDownload = isDownloadRequest( request );
		
		// if command is set to refresh then re-read system parameters and recompile xsl stle sheets
		checkRefreshCommand( request );
		
		BrowserBean browserBean = null;
		RequestTimer requestTimer = new RequestTimer();
		requestTimer.setStartTime(timeInMillis);
		// Get the token from the session and create the bean, setting the token in it
		//HttpSession session = request.getSession();
		String token = getRequestToken( request, session );
		
		// Changes made in condition to nullify token.
		// In case of multi-pane request, parameters needs to be extracted from "appreq:parameterName".
		// Otherwise causes NullPointerException when parameter value is null. 
		if ( ( request.getParameter("requestType")!=null && ivRequestType.equals("CREATE.SESSION"))||( request.getParameter("routineArgs")!=null && ( ivRoutineArgs.startsWith("PROCESS.EXPIRED") ||  ivRoutineArgs.startsWith("PROCESS.REPEAT"))))
		{
				token = "";
		}
		String user = (String) session.getAttribute( SESSION_SIGN_ON_NAME );
		String userId = (String) session.getAttribute( SESSION_USER_ID );
		String ivdocDownloadPath = request.getParameter("DocDownloadPath");
		String uploadLocation = request.getParameter("fileLocation");
		// Execute only IM module related ENQUIRY.
		if(ivRequestType.equals("OFS.ENQUIRY") && ivdocDownloadPath!=null && !(ivdocDownloadPath.equals("")))
		{
			// check the filedownload path values.
			if (session!=null)
			{
                String winName = request.getParameter("windowName");
			    String DownloadpathMapId = winName + "_DownloadpathMap";
				HashMap DownloadpathMap = (HashMap) session.getAttribute(DownloadpathMapId);
				if (DownloadpathMap!=null)
			     {
					List<String> arrayvalue = new ArrayList<String>();
					arrayvalue = (List<String>) DownloadpathMap.get("downloadpath");
					if (arrayvalue.contains(ivdocDownloadPath))
					{
						LOGGER.debug(" The Download path does not change");
					}
					else
					{
						LOGGER.error("Security Violation: " + ivdocDownloadPath );
						  session = request.getSession(false);
						  if (session != null) 
						  {
							session.invalidate();
						  }
						response.sendError(HttpServletResponse.SC_FORBIDDEN , ""); 
					}
			    }
				else
			     {
				LOGGER.debug("Requested download path could not find");
			     }
				
			}
				// Check needs to be done for download location
				if(uploadLocation!=null && uploadLocation.equalsIgnoreCase("server"))
				{
					UploadServlet.doT24Download(request, response,ivdocDownloadPath);	// Download from T24 server Data base
				}else{
					BrowserBean.doDownload(request, response, ivdocDownloadPath);	// Download from local stored system
				}
				// After download the file return, don't proceed further since response has been committed.
				return;
		}
		
		// Check for user's session time out and if it is, set userTimedOut attribute in request object
		// It will be used in browser bean to display time out message instead of login page.
		// This stops to display login page in a COS frame(when request comes after timed out) and allows to re-login.
		if (userId == null && !ivRequestType.equals("CREATE.SESSION") && !ivRequestType.equals(null) )
		{
			LOGGER.debug("User’s web session timed out: UserId= " + userId);
			request.setAttribute("UserTimedOut", "true");
			LOGGER.debug("Web session 'Timedout' Attribute added in request: UserTimedOut= true");
		}
		
		try
		{
			browserBean = new BrowserBean( getServletConfig(),getTemplateManager(), Utils.getClientIpAddress( request ), request );
			if (browserBean.requestError()) {
				// If web session timedout occured show only a timed out message.
				// Incase of ARC-IB display the login page even if timedout happened.
				String userTimedOut = (String)request.getAttribute("UserTimedOut");
				String browserProduct = ivParameters.getParameterValue("Product");
				LOGGER.debug("Browser bean request error occured");
				if (userTimedOut!=null&&!userTimedOut.equals("true")&& (!browserProduct.equalsIgnoreCase("arc-ib")))
				{
					throw new SecurityViolationException();
				}
			}
			if (browserBean.requestErrorText()) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				response.sendRedirect(httpRequest.getContextPath() + "/jsps/FilterError.jsp");
			}
			browserBean.setRequestToken( token );	
			browserBean.setSignOnName( user );
			browserBean.setUserId( userId );
			browserBean.setRequestTimer(requestTimer);
			
			// Process the request
			browserBean.processRequest();
 			String sResponseType = browserBean.getResponseType();			
			if (sResponseType.equals("HTTP.ERROR.CODE")) {
 				response.sendError(browserBean.getHttpErrorCode());
			}
 			else {
				if ((isDownload) ||(sResponseType.equals("TEXT.SAVE.REPORT")))
				{
					browserBean.processDownloadRequest(response);
				}
				else if(sResponseType.equals("XML.T24.UPDATES"))
				{
					// Get the response xml from T24
					String responseXml = browserBean.getResponse();
					// Update the request
					request.setAttribute("updateList", responseXml);
	       			// Reset the response and set relevant attributes
        			response.reset();
        			response.setContentType( "application/zip");
        			response.setHeader("Content-type", "application/zip");
        			response.setHeader("Content-disposition","inline; filename=updates.zip");
					// Get Updates servlet
					RequestDispatcher dispatch = getServletContext().getNamedDispatcher("UpdatesServlet");
					// Forward the control to this servlet so that file downloading can start.
					dispatch.include(request, response);
					
					String sErrorResponse = (String) request.getAttribute("errorMessage");

					if ( sErrorResponse != null && !(sErrorResponse.equals("")))
					{
						browserBean.dispalyErrorMessage(sErrorResponse);
						String sResponse = browserBean.getResponse();
						displayResponse( sResponse, "HTML", response );
						browserBean.setAPIResponseType("");						
					}
				}
				else if(sResponseType.equals("XML.DOCUMENT.SERVICE"))
				{
					// Re-direct to Document Service Servlet!
					
					// Get all the information that the Document Service Servlet needs
					String sDocServiceChannel = browserBean.getDocumentServiceChannel();
					String sFullDrillInfo = browserBean.getDocumentServiceId();
					String sDocServiceInstance = browserBean.getDocumentServiceInstance();

					// Set this information into request attributes
					request.setAttribute("downloadChannel", sDocServiceChannel);
					request.setAttribute("drillDownInfo", sFullDrillInfo);
					request.setAttribute("downloadInstance", sDocServiceInstance);	

					String sFileName = "";
					String sContentType = "";
					String sHeaderFile = "";
					// Check if the request is a multi-download
					boolean bMD = browserBean.getDocumentServiceMultiDownload();
					if (bMD == true)
					{
						// Tell the Document Service servlet
						request.setAttribute("multidownload", "true");
						// We will have a list of full drill down info.
						String[] sDrillParts = sFullDrillInfo.split(FIELD_DELIMITER);
						// Set the content type & header file using the 1st item
						sFileName = getDownloadFileName(sDrillParts[0]);
						sContentType = SetContentType(sFileName);
						sHeaderFile = sFileName;
					}
					else
					{
						// Just a single drill down so
						// determine the file ID that will be downloaded
						sFileName = getDownloadFileName(sFullDrillInfo);
						if (sFileName != "")
						{
							sContentType = SetContentType(sFileName);
							sHeaderFile = sFileName;
						}
					}

					
					if (sFileName != "")
					{
		       			// Set the correct Content Type according to the file extension
						// We MUST Set these here as the dispatcher.include is 
						// unable to set these attributes in the other servlet
						// they are ignored!
	        			response.reset();
	        			response.setContentType(sContentType);
	        			response.setHeader("Content-type",sContentType);
	        			// Always force to display download dialogue box for document download.
						// It avoids blank window display after download also.
	        			response.setHeader("Content-Disposition","attachment; filename="+sHeaderFile);
	        	        // response.setHeader("Content-disposition","inline; filename="+sHeaderFile);					
						
						RequestDispatcher dispatch = getServletContext().getNamedDispatcher("DocumentService");
						//dispatch.forward(request, response);
						dispatch.include(request, response);
						
						String sErrorResponse = (String) request.getAttribute("errorMessage");
						if (sErrorResponse==null)
						{
							sErrorResponse = "";
						}
						if (!(sErrorResponse.equals("")))
						{
							browserBean.dispalyErrorMessage(sErrorResponse);
							String sResponse = browserBean.getResponse();
							displayResponse( sResponse, "HTML", response );
							browserBean.setAPIResponseType("");						
						}
					}
					else
					{
						browserBean.dispalyErrorMessage("Download ID must be specifed");
						String sResponse = browserBean.getResponse();
						displayResponse( sResponse, "HTML", response );
						browserBean.setAPIResponseType("");
					}
					
					
				}
				else
				{
					String sResponse = browserBean.getResponse();
					displayResponse( sResponse, sResponseType, response );
					browserBean.setAPIResponseType("");
				}
				
				// Save new token in the session
				// If the token is empty, then there was probably an error at the server - in
				// this case keep hold of the old token as we can try to use it on the next request.
				// If the session was invalidated (i.e. due to a logout) then no need to store the token
				String sToken = browserBean.getRequestToken();
		
				try
				{		
					if ( ( sToken == null ) || ( sToken.equals( "" ) ) )
					{
						// Keep the old token for our next request - it might still be valid
						// session.removeAttribute( TOKEN_SESSION_NAME );
					}
					else
					{
						session.setAttribute( TOKEN_SESSION_NAME, sToken );
					}
				}
				catch ( IllegalStateException ise )
				{
					// Invalidated session - ignore the exception
				}
 			}
			
			timeInMillis = System.currentTimeMillis() - timeInMillis;		
			LOGGER.info(requestTimer.toString());
			
			// Print the timing info in less cryptic format
			LOGGER.info(requestTimer.toLongString());			
		}
		catch (TBrowserException bex)
		{
			displayBrowserException( bex, request, response );
		}
		catch (SecurityViolationException e) {
			handleSecurityViolationException(browserBean, request, response);
		}
		finally {
			if (browserBean != null && browserBean.getInvalidateSession() && session != null) {
				try {
					session.invalidate();
				}
				catch (Exception e) {
					LOGGER.warn("Exception invalidating session: " + e.getMessage());
				}
			}

			LOGGER.debug( "***** Browser : End Request *************************************************" );

			// Delete the bean
			browserBean = null;

		}		
		
		LOGGER.debug( "***** Browser : End Request *************************************************" );
	}		
	
	/**
	 * Display the login page and invalidate the session.
	 * @param browserBean
	 * @param request
	 * @param response
	 */
	private void handleSecurityViolationException(BrowserBean browserBean, HttpServletRequest request, HttpServletResponse response) {
		
		// Display the login page with 'Security Violation'
		String browserProduct = ivParameters.getParameterValue("Product");
		if(browserProduct.equalsIgnoreCase("arc-ib")) {
			browserBean.processReauthentication(browserBean.getErrorText());
		} else {
			browserBean.buildLoginPage();
		}
		String sResponse = browserBean.getResponse();
		displayResponse( sResponse, "HTML", response );
		
		// Invalidate the session
		HttpSession session = request.getSession();
		LOGGER.debug( "Destroying Session on Security Violation : Id = " + session.getId() );
		session.invalidate();
	}

	/**
	 * Display response.
	 * 
	 * @param sResponseText the s response text
	 * @param sResponseType the s response type
	 * @param response the response
	 */
	private void displayResponse( String sResponseText, String sResponseType, HttpServletResponse response )
	{
		// Display the response HTML or XML
		try
		{
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache,no-store");
			response.setDateHeader("Expires", 0);
			response.setHeader("X-FRAME-OPTIONS", XFRAME_HEADER_VALUE);
			
			// Ensure the response is treated as UTF-8 characters
			response.setContentType("UTF-8");
        	
        	if ( sResponseType.equals( "URL" ) )
        	{
        		// Redirect to the supplied URL
				response.sendRedirect( sResponseText );
        	}
        	else if ( sResponseType.equals( "NO.RESPONSE" ) )
        	{
        		// No response to display so just ignore it
        	}
        	else
        	{	
        		if ( sResponseType.equals( "XML" ) )
	        	{
		        	response.setContentType( "text/xml;charset=utf-8" );
	        	}
 				else if(sResponseType.startsWith("TEXT"))
 				{
 					response.setContentType("text/plain;charset=utf-8");
 				}
 	       		else
  	      		{
					response.setContentType( "text/html;charset=utf-8" );
  	      		}        	

       			PrintWriter writer = response.getWriter();
       			writer.println( sResponseText );
         	}
        }
		catch ( Exception e )
		{
			// Error occurred, but can't write any response
			System.out.println("Error writing response");
			LOGGER.error("Error writing response", e);
		}
	}

	/**
	 * Display browser exception.
	 * 
	 * @param bex the bex
	 * @param request the request
	 * @param response the response
	 */
	private void displayBrowserException( TBrowserException bex, HttpServletRequest request, HttpServletResponse response )
	{
		String sMsg = bex.getMessage();
		String sOfsMsg = bex.getOfsMessage();
				
		if ( ( sOfsMsg != null ) && ( ! sOfsMsg.equals("") ) )
		{
			sMsg = sMsg + " - " + sOfsMsg;
		}

		LOGGER.error( sMsg, bex );
		LOGGER.error( "Please check the T24 Browser and Connector settings" );

		Exception e = new Exception( sMsg );
		request.setAttribute("javax.servlet.jsp.jspException",e); 
        request.setAttribute("javax.servlet.error.status_code",new Integer(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));

      	try 
      	{ 
			getServletContext().getRequestDispatcher("/jsps/error.jsp").forward(request,response); 
      	} 
      	catch (Exception io)
      	{ 
            io.printStackTrace(); 
      	} 
	}
	

	/**
	 * Read the XML parameter files
	 */
	private void readParameters()
	{
		// Read the parameters from the XML file browserParameters.xml
		BrowserParameters params = new BrowserParameters( this.getServletContext() );
	 	ivParameters = params.getParameters();
	 	
	 	String isBranch = ivParameters.getParameterValue( BranchConstants.BROWSER_PARAM_BRANCH );
	 	productName = ivParameters.getParameterValue("Product");
	 	singleLoadingIcon = ivParameters.getParameterValue("Show Single Busy");
	 	showUserName = ivParameters.getParameterValue("Show User Name");
	 	clearSelection = ivParameters.getParameterValue("NewEnquirySelTool");
	 	String xFrameValue = ivParameters.getParameterValue("X-FRAME-OPTIONS");
	 	if( (xFrameValue != null) && ( !xFrameValue.equals("") ))
	 		XFRAME_HEADER_VALUE = ivParameters.getParameterValue("X-FRAME-OPTIONS");
	 	
	 	if (singleLoadingIcon != null) {
	 		singleLoadingIcon = singleLoadingIcon.toLowerCase();
	 		if ((!productName.equalsIgnoreCase("ARC-IB")) && singleLoadingIcon.equals("yes")) {
	 			singleLoadingIcon = "no";
	 		}
	 	}
	 	
	 	if (showUserName != null) {
	 		showUserName = showUserName.toLowerCase();
	 	}
	 	
	 	if (clearSelection != null) {
	 		clearSelection = clearSelection.toLowerCase();
	 		if ((!productName.equalsIgnoreCase("ARC-IB")) && clearSelection.equals("yes")) {
	 			clearSelection = "no";
	 		}
	 	}
	 	
	 	if ( ( isBranch != null ) && ( !isBranch.equals("") ) )
	 	{
	 		if ( ( isBranch.toLowerCase().equals("yes") || ( isBranch.toLowerCase().equals("y") ) ) )
	 		{
	 			LOGGER.info("Web Server running as a Branch");
	 			initialiseBranch();
	 		}
	 		else
	 		{
	 			LOGGER.info("Web Server running as the Main Server");
	 		}
	 	}
	 	else
	 	{
	 		LOGGER.info("Web Server running as main server");
	 	}
	 	
	 	readInitParamFile(LOGIN_PAGE_LINKS_INIT_PARAM);
	 	readInitParamFile(NOCHANGE_FIELDS_INIT_PARAM);
	}
	
	/**
	 * Load compiled XSL.
	 */
	private void loadCompiledXSL()
	{
		/* This is not needed when using XT as the transformer instead of Xalan
		 * as the stylesheets are read in during the transformation processing.
		 * Commented out for now to minimise the size of the session data
		 * stored for session persistence.
		 * 
		if ( (Hashtable) getServletContext().getAttribute("ivCache") == null )
		{	
			XSLBean xslBean = new XSLBean( getServletConfig() );
			getServletContext().setAttribute("ivCache", (Hashtable) xslBean.getXSLCache() );
		}
		*/
	}
	
	
	/**
	 * Checks if is download request.
	 * 
	 * @param request the request
	 * 
	 * @return true, if is download request
	 */
	private boolean isDownloadRequest(HttpServletRequest request)
	{
		String paramValue = Utils.getValue( request, "download");   //request.getParameter("download");
		String downloadType = Utils.getValue( request, "downloadType"); //request.getParameter("downloadType");
				
		if (paramValue != null && paramValue.equals("ENQUIRY.DOWNLOAD"))
		{
			request.setAttribute("download", "yes");
			request.setAttribute("downloadType", downloadType);
			
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Check refresh command.
	 * 
	 * @param request the request
	 */
	private void checkRefreshCommand( HttpServletRequest request )
	{	
		// if command is set to refresh then re-read system parameters and recompile xsl stle sheets
		
		String command = Utils.getValue( request, "command"); //request.getParameter("command");
				
		if ( ( command != null ) && ( command.toLowerCase().equals( "refresh" ) ) )
		{
			//set the xsl compiled cache to null and re-read the parameters
			getServletContext().setAttribute("ivCache", null );			
			readParameters();
			LOGGER.info("Servlet refreshed");
		}
	}
	
	//returns a clone of the template manager
	/**
	 * Gets the template manager.
	 * 
	 * @return the template manager
	 */
	private XMLTemplateManager getTemplateManager(){
		try{
			XMLTemplateManager manager = (XMLTemplateManager)ivTemplateManager.clone();
			return manager;
		}
		catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return null;
	}	

	// Initialise the branch data
	/**
	 * Initialise branch.
	 */
	private void initialiseBranch()
	{
		// Read the parameter file
		ServletContext context = this.getServletContext();
		BranchParameters brParams = new BranchParameters( context );
	 	
		Branch branch = brParams.getBranch();
		context.setAttribute( BranchConstants.SERVLET_CONTEXT_BRANCH, branch );
		context.setAttribute( BranchConstants.BRANCH_STATUS_CONTEXT, branch.getStatus() );
		context.setAttribute( BranchConstants.INSTANCE_CONTEXT, brParams.getInstance() );
		
		// Read the strings file containing display strings not supplied from T24
		BranchStrings brStrings = new BranchStrings( context );
		context.setAttribute( BranchConstants.BRANCH_STRINGS_CONTEXT, brStrings );
	}
	
	/**
	 * Gets the request token.
	 * 
	 * @param request the request
	 * @param session the session
	 * 
	 * @return the request token
	 */
	private String getRequestToken( HttpServletRequest request, HttpSession session )
	{
		String token = (String) session.getAttribute( TOKEN_SESSION_NAME );
	
		int intSequence = 1;
		
		synchronized(session)
		{
			Integer tokenSequence = (Integer)session.getAttribute( TOKEN_SEQUENCE_NUMBER );
			
			if ( tokenSequence != null)
			{
				intSequence = tokenSequence.intValue();
				intSequence++;
			}		
			tokenSequence = new Integer(intSequence);
			session.setAttribute( TOKEN_SEQUENCE_NUMBER, tokenSequence);
		}
		
		// If there is no token in the session then check whether we were given one in the request string
		if ( ( token == null ) || ( token.equals("") ) )
		{
			String content = Utils.getValue( request, "content");  //request.getParameter("content");
		
			if ( ( content != null ) && ( ! content.equals("") ) )
			{
				Utils utils = new Utils();
				token = utils.getNodeFromString( content, "token" );
			}
		}

		if ( ( token != null ) && ( !token.equals("")) )
		{
			// only set if there is a token in the session
			int iTokenFlag = token.indexOf(TOKEN_DELIMITER);
			
			// check for ":", this means we are using new token sequence if present
			if (iTokenFlag != -1)
			{
				String[] splitToken = token.split(TOKEN_DELIMITER);
				token = splitToken[0] + ":" + intSequence;
			}	
		}
		
		return( token );
	}
	
	/**
	 * Attempts to set the correct content type depending on the file exentsion.
	 * 
	 * @param string filename
	 *  
	 * @return string MIME content type
	 */	
	private String SetContentType(String sDocName)
	{
		// Set a default
		String sContentType = "application/octet-stream";
		
		if (sDocName.endsWith(".html") || sDocName.endsWith(".htm"))
		{
			sContentType = "text/html";
		}
		else if(sDocName.endsWith(".txt"))
		{
			sContentType = "text/plain";
		}
		else if(sDocName.endsWith(".gif"))
		{
			sContentType = "image/gif";
		}
		else if(sDocName.endsWith(".jpeg") || sDocName.endsWith(".jpg") || sDocName.endsWith(".jpe"))
		{
			sContentType = "image/jpeg";
		}
		else if(sDocName.endsWith(".tif") || sDocName.endsWith(".tiff"))
		{
			sContentType = "image/tiff";
		}
		else if(sDocName.endsWith(".bmp"))
		{
			sContentType = "image/bmp";
		}
		else if(sDocName.endsWith(".mpeg")|| sDocName.endsWith(".mpg") || sDocName.endsWith(".mpe"))
		{
			sContentType = "video/mpeg";
		}
		else if(sDocName.endsWith(".mpv2") || sDocName.endsWith(".mp2v"))
		{
			sContentType = "video/mpeg-2";
		}
		else if(sDocName.endsWith(".qt") || sDocName.endsWith(".mov"))
		{
			sContentType = "video/quicktime";
		}			
		else if(sDocName.endsWith(".avi"))
		{
			sContentType = "video/x-msvideo";
		}
		else if(sDocName.endsWith(".ai") || sDocName.endsWith(".eps") || sDocName.endsWith(".ps"))
		{
			sContentType = "application/postscript";
		}
		else if(sDocName.endsWith(".rtf"))
		{
			sContentType = "application/rtf";
		}
		else if(sDocName.endsWith(".pdf"))
		{
			sContentType = "application/pdf";
		}			
		else if(sDocName.endsWith(".doc"))
		{
			sContentType = "application/msword";
		}		
		else if(sDocName.endsWith(".gtar"))
		{
			sContentType = "application/x-gtar";
		}
		else if(sDocName.endsWith(".tar"))
		{
			sContentType = "application/x-tar";
		}			
		else if(sDocName.endsWith(".zip"))
		{
			sContentType = "application/zip";
		}		
		else if(sDocName.endsWith(".bin") || sDocName.endsWith(".uu") || sDocName.endsWith(".exe"))
		{
			sContentType = "application/octet-stream";
		}			
		else if(sDocName.endsWith(".xls"))
		{
			sContentType = "application/ms-excel";
		}		
		else if(sDocName.endsWith(".ppt"))
		{
			sContentType = "application/ms-powerpoint";
		}
		
		return sContentType;
	}
	
	private String getDownloadFileName(String sFullDrillDownInfo)
	{
		// This function looks at the drill down info received from T24
		// and removes the Key Word 'DOC' from it.
		// sFullDrillDownInfo my look like:
		// "DOC my document.pdf : Document Sub Folder DEBUG"
		// or
		// "DOC test.pdf myFolder DEBUG"
		String sFileName = "";
		
		if (sFullDrillDownInfo.contains(":"))
		{
			// Resolve the 1st Part
			int iPos = sFullDrillDownInfo.indexOf(":");
			String sPart1 = sFullDrillDownInfo.substring(0, iPos); // e.g. "DOC my document.pdf "
			// Trim the part
			sPart1 = sPart1.trim();
			// To resolve the document ID simply ignore the 'DOC ' directive
			sFileName = sPart1.substring(4);
		}
		else
		{
			// The Drill Down definition has not been specified with a ':'
			// Any spaces in the parameters will cause problems!
			// Split the drill down info
			String[] sCommandParts = sFullDrillDownInfo.split( " " );
			for ( int i = 0; i < sCommandParts.length; i++ )
			{
				// Ignore element 0 as this should contain 'DOC'
				
				// The 1st element should be the Document ID
				if (i == 1)
				{
					sFileName = sCommandParts[i];
				}
			}
		}
		return sFileName;
	}	
	

	/**
	 * Creates a PropertyManager object out of the given parameter file and stores it as an attribute in the servlet context.
	 * @param paramName The name of the parameter file, also used for the attribute name in the servlet context.
	 */
	private void readInitParamFile(String paramName) {
		ServletContext context = this.getServletContext();
		String contextPath = context.getRealPath("");
		String propertiesFile = getServletContext().getInitParameter(paramName);
		
		if ( propertiesFile != null && !propertiesFile.equals("") && !propertiesFile.equals("null") )
		{
			PropertyManager properties = new PropertyManager( contextPath, propertiesFile );
		 	
		 	// Save it in the servlet context to be used by all login pages
		 	getServletContext().setAttribute(paramName, properties );
		}
	}
	
	// Get the command - as this can determine the instance we use for the connection
	private String getCommand( HttpServletRequest request )
	{
		String command = Utils.getValue( request, "command"); 

		if ( ( command != null ) && ( ! command.equals("") ) )
		{
			command = command.toLowerCase();
		}
		
		return( command );
	}
	
	/**
	 * Process the Version command by returning the version.xml file contents
	 * 
	 */
	private String getVersion()
	{
		// Read the version.xml file
		String versionXml = "";
		String sFileSeparator = System.getProperty("file.separator");
		String sFileName = getServletContext().getRealPath("") + sFileSeparator;
		sFileName += VERSION_FILE;
		
		FileManager fm = new FileManager( sFileName );
		String xml = fm.readFile();
		
		if ( ( xml != null ) && !( xml.equals("") ) )
		{
			versionXml = xml;
		}
		else
		{
			versionXml = "<error>Error reading version.xml</error>"; 
		}
		
		return ( versionXml );
	}
}
