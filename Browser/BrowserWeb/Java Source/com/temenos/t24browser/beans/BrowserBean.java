package com.temenos.t24browser.beans;

////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BrowserBean
//
//  Description   :   Bean for controlling OFS requests between the Browser
//					  Servlet and the server.
//
//  Modifications :
//
//    17/06/02   -    Initial Version - taken from BrowserServlet.
//	  03/07/02	 -	  Implement GlobusBrowserBean.
//	  05/07/02 	 -	  Added logging of server parameter errors.
//	  05/07/02	 -	  Added use of SocketConnectionBean interface.
//	  07/08/02	 -	  Use web address for SERVLET.CONTEXT substitution.
//	  31/10/02	 -    Added handling of invalid request type exception.
//    05/11/02   -    Check for Null result from connector.
//    15/11/02   -    Removed use of replacing instances of SERVLET.CONTEXT and
//						WEB.SERVER.ADDRESS - now solved in XSLs and JSPs.
//	  15/11/02   -    Added parameter to determine whether to transform on web server.
//    21/11/02 	 -	  Added logging events
//    02/01/03   -    Allow processing of the quickguide command.
//    07/02/03   -    If a user has already signed in in the session, display the
//						login page.  Stops Refresh from re-submitting a sign in request.
//    08/07/03   -    Changed the signature call to XMLRequestManager constructor
//						Now passes in an XMLTemplateManager as well.
//    10/09/03   -    Added a call to setHomeDirectory.  setHomeDirectory has been added to
//						the super class. Checks to see if the xsl file name has a directory
//						structure attached to the front of it.  Needed for the
//						xsl transform process.
//    29/09/03   -    Changed the constructor call for APIResponseManager to pass in all
//						of the xml templates (XMLTemplateManager)
//	  29/10/03   -    Added the directory "jsps" onto the path for calling the quickguide.jsp
//						in processQuickGuide()
//    03/06/04   -    Added code for doing routine validation at the servlet.
//	  03/04/06	 -		ProcessLoginCommand method has been modified to check whether the user
//						is already loged in / not.
//	  24/01/07   -     HTTP header accept value */* is removed. 
//
//    08/10/07   -    Changes to allow passwords with special characters.
//
//    21/05/08   -    Changes done to process downloading of large reports without any error.
////////////////////////////////////////////////////////////////////////////////

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.io.*;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.browser.api.ConnectionNotCreatedException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.branch.BranchConstants;
import com.temenos.t24browser.branch.BranchState;
import com.temenos.t24browser.comms.T24WebConnection;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.graph.Graph;
import com.temenos.t24browser.request.RequestData;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.response.StoreResponse;
import com.temenos.t24browser.servlets.BrowserServlet;
import com.temenos.t24browser.utils.Constants;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.utils.XmlResponse;
import com.temenos.t24browser.validation.ValidatorFactory;
import com.temenos.t24browser.xml.APIResponseManager;
import com.temenos.t24browser.xml.CommandProcessor;
import com.temenos.t24browser.xml.XMLBulkRequest;
import com.temenos.t24browser.xml.XMLBulkResponse;
import com.temenos.t24browser.xml.XMLConstants;
import com.temenos.t24browser.xml.XMLRequestManager;
import com.temenos.t24browser.xml.XMLRequestManagerException;
import com.temenos.t24browser.xml.XMLRequestTypeException;
import com.temenos.t24browser.xml.XMLSubPaneResponse;
import com.temenos.t24browser.xml.XMLTemplateManager;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tsdk.exception.ValidationException;
import com.temenos.tsdk.foundation.T24Connection;
import com.temenos.tsdk.foundation.TContract;
import com.temenos.tsdk.validation.ValidationManager;
import com.temenos.tsdk.validation.ValidationResponse;
import com.temenos.tsdk.validation.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Class BrowserBean.
 */
public class BrowserBean extends TemenosBean implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserBean.class);
	
	/** The iv logged in. */
	private boolean ivLoggedIn = false; // Whether user has logged in in this session

	/** The Constant LOGIN_STYLESHEET. */
	private static final String LOGIN_STYLESHEET = "/transforms/login.xsl"; // Login page stylesheet
	
	/** The Constant ARC_IB_LOGIN_STYLESHEET. */
	private static final String ARC_IB_LOGIN_STYLESHEET = "/modelbank/transforms/login.xsl"; // Login page stylesheet
	
	/** The Constant T24_UPDATES_ADMIN_LOGIN_STYLESHEET. 
	 *  If an Administrator has entered the T24 Updates then show them the admin login page stylesheet */
	private static final String T24_UPDATES_ADMIN_LOGIN_STYLESHEET = "/modelbank/transforms/adminlogin.xsl"; // 	
	
	/** The Constant GET_LOGIN_STYLESHEET. */
	private static final String GET_LOGIN_STYLESHEET = "/transforms/getLogin.xsl"; // Login page stylesheet

	/** The Constant VAL_STYLESHEET_PATH. */
	private static final String VAL_STYLESHEET_PATH = "/transforms/contracts/webServerValidation.xsl"; //The path to the web validation style sheet
	
	/** The Constant PARAM_CALL_CENTRE_CLASS. */
	private static final String PARAM_CALL_CENTRE_CLASS = "CALL_CENTRE_CLASS"; // Parameter Call Centre Class
	
	public static final String LOGIN_PAGE_LINKS = "loginPageLinks";
	
	/** The Constant TOKEN_SEQUENCE_NUMBER. */
	private static final String TOKEN_SEQUENCE_NUMBER = "TokenSequence";

	/** 'graphDisplayType'. Whether graphs will be displayed as svgObjects or pngImages. */
	private static final String PARAM_GRAPH_DISPLAY_TYPE = "graphDisplayType"; 

	/** 'pngImage'. Parameter that indicates graphs should be displayed as .PNG images files. */
	private static final String PARAM_PNG_IMAGE = "pngImage";

	/** 'doNochangeFieldsCheck'. Whether or not to do the nochangeFields check. */
	public static final String PARAM_DO_NOCHANGE_FIELDS_CHECK = "doNochangeFieldsCheck";

	/** 'yes' or 'no'. Whether or not to do the nochangeFields check. */
	public static final Object PARAM_DO_NOCHANGE_FIELDS_CHECK_YES = "yes";
	
	public String CustomXml ="";
	/**
	 * Instantiates a new browser bean.
	 * 
	 * @param config the config
	 * @param xmlTemplates the xml templates
	 * @param clientIP the client IP
	 * @param request the request
	 * 
	 * @throws TBrowserException the t browser exception
	 * @throws SecurityViolationException 
	 */
	public BrowserBean(ServletConfig config, XMLTemplateManager xmlTemplates, String clientIP, HttpServletRequest request) throws TBrowserException
	{
		super(config, xmlTemplates, clientIP, request);

		ivSignOnName = "";
		ivSignOnNameEnabled = "Y";
		ivPassword = "";
		ivoption = "";
		setRequestToken("");
		setRequestCompany("");
		LOGGER.info("Constructor");
	}

	/**
	 * Process request.
	 */
	public void processRequest()
	{
		ivResponseHtml = "";
		ivResponseXml = "";
		ivResponseUrl = "";
		ivRequestXml = "";

		LOGGER.debug("Processing command : " + ivCommand);
		ivRequest.setAttribute("rootPath", ivServletContextPath);
		
		if( ivRequest.bulkRequest())
		{	// Check for AA using the 'appreq' prefix
			ivRequestType = ivRequest.getValue("appreq:requestType");
		}
		else
		{
			ivRequestType = ivRequest.getValue("requestType");
		}
		
		if( ivRequestType == null ) {
			ivRequestType = "";
		}

		// Process the Http Servlet Request

		// If we are in a branch server, check if the connection has changed since
		// the user logged in, if so then save this request and ask them to login in again
		if ( ( ivBranch != null ) &&
		     ( ivBaUser == false ) &&
		     ( ! ivCommand.equals("login") ) &&						// Login
		     ( ! ivCommand.equals("branchadminlogin") ) &&			// Branch Admin login
		     ( ! ivCommand.equals("getlogin") ) &&					// Get login page
		     ( ! ivCommand.equals("getbranchadminlogin") ) &&		// Get Branch Admin login page
		     ( ! ivCommand.equals("changeconnection") ) &&			// Change connection instance
		     ( ! ivCommand.equals("getbranchdetails") ) &&			// Get Branch details
			 ( ! ivCommand.equals("smartclient") ) &&				// Toolbox command
			 ( ! ivInstanceContext.equals( ivInstanceUser ) ) && 
			 ( ! ivCommand.equals("adminlogin")))					// T24 Updates Admin Login
		{
			// Ignore any No Requests - no point unlocking record on local branch as it wasn't locked here
			if ( ! ivRequestType.equals("NO.REQUEST") )
			{
				String sErrorText = ivBranchStrings.getString( BranchConstants.BR_STRING_MESSAGE_STATE_CHANGED );
				
				if ( ( ivInstanceUser == null ) || ( ivInstanceUser.equals("") ) )
				{
					HttpSession session = ivHttpRequest.getSession();
					String sessionId = session.getId();
					
					// This is a new session - possible session timeout so just display the login page
					String svMessage = getSecurityViolationMessage("SV-05");
					LOGGER.debug( svMessage + " - " + sessionId + " - Web Server - BR module Instance User is null" );
					sErrorText = svMessage;
				}
				LOGGER.debug("Re-authentication required.");
				LOGGER.debug("Instance [ Context ] is : " + ivInstanceContext);
				LOGGER.debug("Instance [ Session ] is : " + ivInstanceUser);

				// Make the user re-authenticate themselves
				processReauthentication( sErrorText );
			}
		}
		else if (ivCommand.equals("getlogin"))
		{
			// Process the get login page command
			ivLoginCommand = USER_LOGIN_COMMAND;
			processGetLoginCommand();
		}
		else if (ivCommand.equals("getbranchadminlogin"))
		{
			// Process the get Branch Administration login page command
			ivLoginCommand = BRANCH_ADMIN_LOGIN_COMMAND;
			processGetLoginCommand();
		}
		else if ( ivCommand.equals("login") )
		{
			// Process the login command
			processLoginCommand();
		}
		else if ( ivCommand.equals("branchadminlogin") )
		{
			// Check if we are in a branch server
			if ( ivBranch == null )
			{
				buildErrorResponse( ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_SERVER ) );
			}
			else
			{
				// Process the Branch Administrator login command
				processLoginCommand();
			}
		}
		else if (ivCommand.equals("signon"))
		{
			// Process the sign on command - logoff the current user and display the login page
			processCommand(); // Process the DESTROY.SESSION command - in the request data
            
			// todo: all of this login functionality should be moved out into login modules
            // Redirect the user to the login page
            String req = ivHttpRequest.getRequestURL().toString();
            int index = req.indexOf("servlet");
            ivResponseUrl = req.substring(0, index);
		}
		else if (ivCommand.equals("quickguide"))
		{
			// Process the quick guide command
			processQuickGuide();
		}
		else if (ivCommand.equals("globuscommand") || ivCommand.equals("repeatpassword"))
		{
			// Process the Globus command
			processCommand();
		}
		else if (ivCommand.equals("smartclient"))
		{
			processSmartClientRequest();
		}
		else if (ivCommand.equals("validation"))
		{
			// Process the web validation
			processValidation();
		}
		else if (ivCommand.equals("changeconnection"))
		{
			if ( ivBaUser )
			{
				// State change will be processed in initialiseInstances

				// Use this TC instance from now on - by saving it in the servlet context
				String instance = ivRequest.getValue("instance");

				if ( ( instance == null ) || ( instance.equals("") ) )
				{
					buildErrorResponse("No TC Instance name supplied on Change Connection request");
				}
				else
				{
					ivResponseXml = XML_TAG_RESP_DETAILS + XML_TAG_RESP_DETAILS_C;
					ivResponseXml = addFullBranchDetails( ivResponseXml, XML_TAG_RESP_DETAILS );

					ivServerTransform = false;

					// Save the branch state to the data file
					BranchState bs = new BranchState( ivServletContext );
					bs.saveState( ivBranch.getStatus() );

					// Update the branch with the new status
					branchStatusUpdateRequest();
				}
			}
			else
			{
				buildErrorResponse( ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_ADMIN ) );
			}
		}
		else if (ivCommand.equals("getbranchdetails"))
		{
			// Return the branch details - if we are a branch
			if ( ivBranch == null )
			{
				buildErrorResponse( ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_SERVER ) );
			}
			else
			{
				if ( ivBaUser == false )
				{
					buildErrorResponse( ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_ADMIN ) );
				}
				else
				{
					ivResponseXml = XML_TAG_RESP_DETAILS + XML_TAG_RESP_DETAILS_C;
					ivResponseXml = addFullBranchDetails( ivResponseXml, XML_TAG_RESP_DETAILS );
					ivServerTransform = false;
				}
			}
		}
		else if(ivCommand.equals("t24commandapi"))
		{
			// A request that contains a T24 Command Line
			processCommandLineApi();
		}
		else
		{
			// Build the error result in HTML
			buildErrorResponse("Invalid Command passed " + ivCommand);
		}
	}

	
	/**
	 * Process command line api.
	 */
	private void processCommandLineApi()
	{
		// Extract the command line value from the request
		String CommandLineValue = ivHttpRequest.getParameter("t24command");
		CommandLineValue = CommandLineValue.trim();

		if (CommandLineValue.equals(""))
		{
			buildErrorResponse("No Command Line to Process.");
			return;			
		}
		else
		{
			// Give it the command to processor
			CommandProcessor cp = new CommandProcessor();
			cp.processCommand(CommandLineValue);
			
			// Is the command a Sign On Command?
			if (cp.getIsSignOnCommand())
			{
				// Display the login Page
				initLoginPage();
				//buildLoginPage();
			}
			else
			{
				// Check if it's a 'real' T24 command or something else
				// then process accodingly.
				if (cp.getIsRealT24Command())
				{
					Hashtable commandParams = new Hashtable();  //This will contain all of the parameters derived from the command
					
					// The command params should be set so get them!
					commandParams = cp.getParameters();
			
					// Turn this into requestData
					RequestData rd = new RequestData(commandParams);
			
					// Pass this to T24Request
					ivRequest = new T24Request( rd );
			
					// Execute it!
					processCommand();
				}
				else
				{
					// We don't need to send this command to T24
					// so get the command from the CommandProcessor
					String nonT24Cmd = cp.getNonT24Command();
					
					// Dispatch it!!
					//response.sendRedirect( nonT24Cmd );
					ivResponseUrl = nonT24Cmd;
				}
			}
		}

	}
	
 	/**
	  * Handles Servlet validation for contracts.
	  */
	private void processValidation()
	{

		//get the name of the class that ill be used to run the validation
		//TODO SJP 30/09/2005 The parameter sent to the browser should be
		//decoupled from the class name as this exposes an implementation detail.
		String validationRtnName = ivRequest.getValue("routineName");

		try
		{
			if (validationRtnName != null)
			{
				//Dynamically load up the validation class
				Validator validationRoutine = getValidationClass(validationRtnName);

				//Extract the request info and add it to a TContract
				ValidationManager validationManager = new ValidationManager(ivHttpRequest);

				T24Connection con = new T24WebConnection(this.ivConnection, this.ivClientIP);

				con.setRequestToken(this.getRequestToken());
				con.setRequestCompany(this.getCompanyName());

				//Extract the new TContract from the validation manager and pass it to the
				//the validation class
				ValidationResponse valResponse = validationRoutine.processRequest((TContract) validationManager.getResponse(), validationManager.getSourceProperty(), con);

				String valResponseXml = valResponse.produceXML();

				valResponseXml = DebugUtils.preprocessValidationXml(valResponseXml, ivParameters);

				String logMsg = com.temenos.t24browser.utils.Logger.
					replacePassword( valResponseXml );
				LOGGER.debug("XML Response: " + logMsg);

				//transform the xml result
				transformResult(VAL_STYLESHEET_PATH, valResponseXml);
			}
		}
		catch (ValidationException e)
		{
			// Build the error result in xml
			String errorResponse = "<validationResponse><errors><error>" + e.getMessage() + "</error></errors></validationResponse>";
			transformResult(VAL_STYLESHEET_PATH, errorResponse);
			return;
		}
		catch (Exception e)
		{
			// Build the error result in xml
			String errorResponse = "<validationResponse><error>" + e.getMessage() + "</error></validationResponse>";
			transformResult(VAL_STYLESHEET_PATH, errorResponse);
			return;
		}
	}

	/**
	 * returns the validation class to be used to process the request.
	 * 
	 * @param className the class name
	 * 
	 * @return the validation class
	 * 
	 * @throws ValidationException the validation exception
	 */
	public Validator getValidationClass(String className) throws ValidationException
	{
		return ValidatorFactory.getInstance(className, this);
	}

	/**
	 * Process smart client request.
	 */
	public void processSmartClientRequest()
	{
		// The client should post a content, which is xml request being
		// sent to T24 via TC.
		String sXmlRequest = ivRequest.getValue("content");
		String sAction = ivRequest.getValue("action");
        String sXmlResult = null;

		if ( ( sAction != null ) && ( ! sAction.equals("") ) )
		{
			sAction = sAction.toLowerCase();
		}

		if ("get.channels".equals(sAction)) {
		    // Handle Eclipse plug in request for a list of supported channels - this does not make it to T24
		    ivServerTransform = false;
                
		    try {
		        Set channels = ivConnection.getChannels();
		        
		        StringBuffer buff = new StringBuffer();
		        buff.append("<channels>");
		        for (Iterator it = channels.iterator(); it.hasNext(); ) {
		            buff.append("<channel>");
		            buff.append(it.next());
		            buff.append("</channel>");
		        }
		        buff.append("</channels>");        
		        sXmlResult = buff.toString();      
		    }
		    catch (Exception e) {
		        sXmlResult = "<error>Server error: " + e.getMessage() + "</error>";
		    }
		}
            
		if (sXmlResult == null) {
    		if ((sXmlRequest != null) && (!sXmlRequest.equals("")))
    		{
    			boolean baLoginCmd = false;
    			boolean loginCmd = false;
    			
    			if ( sXmlRequest.indexOf("<requestType>CREATE.SESSION</requestType>") != -1 )
    			{
    				if ( ivAllowToolbox.equals("yes" ) )
    				{
						loginCmd = true;
					}
    				else
    				{
    					String sError = getSecurityViolationMessage("SV-04");
    					buildSmartClientError(sError);
    				}
    			}
    			
    			if ( ! ivError )
    			{
	    			// Check if we are a branch server if the user is attempting to login as the administrator
	    			if ( ( sAction != null ) && ( !sAction.equals("") ) &&  ( sAction.equals("branchadminlogin") ) )
	    			{
	    					baLoginCmd = true;
	    			}
	    			
	    			if ( baLoginCmd && ( ivBranch == null ) )
	    			{
	    				String sError = ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_SERVER );
	    				buildSmartClientError(sError);
	    			}
	    			else
	    			{
	    				// Send the request to server
	    				BrowserResponse browserResult = sendOfsRequestToServer(sXmlRequest);
	    	
	    				String logMsg = com.temenos.t24browser.utils.Logger.
	    					replacePassword( sXmlRequest );
	    				LOGGER.debug("Smart Client XML Response: " + logMsg);
	    	
	    				if (browserResult.isValid() == true)
	    				{
	    					sXmlResult = browserResult.getMsg();
	    					
	    					// Remove any Branch request string from the server as it is not required for Toolbox
	    					int startPos = sXmlResult.indexOf( BranchConstants.BRANCH_OFS_REQ_STRING );
	    					
	    					if ( startPos != -1 )
	    					{
	    						int endTagLen = (BranchConstants.BRANCH_OFS_REQ_STRING_C).length();
	    						int endPos = sXmlResult.indexOf( BranchConstants.BRANCH_OFS_REQ_STRING_C );
	    						sXmlResult = sXmlResult.substring( 0, startPos ) + sXmlResult.substring( endPos + endTagLen, sXmlResult.length() );
	    					}
	    					
	    					// For a branch administration login add the branch details to the response
	    					if ( baLoginCmd )
	    					{
	    						setBranchAdministrator( true );
	    						sXmlResult = addFullBranchDetails( sXmlResult, XML_TAG_RESP_DETAILS );
	    					}
	    					else if ( loginCmd )
	    					{
	    						setBranchAdministrator( false );
	    					}
	    					
	    				}
	    				else
	    				{
	    					String sError = "Smart Client Request failed : " + browserResult.getError();
	    					buildSmartClientError(sError);
	    				}
	    			}
    			}
    		}
    		else //if the the request is empty
    			{
    			String sError = "Empty request to the server.";
    			buildSmartClientError(sError);
    		}
        }
        
		if ( ! ivError )
		{
			ivResponseXml = sXmlResult;
		}
	}

	/**
	 * Builds the smart client error.
	 * 
	 * @param sErrMsg the s err msg
	 */
	private void buildSmartClientError(String sErrMsg)
	{
		// Build error result in HTML
		ivError = true;
		ivRequest.setAttribute("errorText", sErrMsg);
		ivErrorText = sErrMsg;
		ivServerTransform = false;

		String sMessageXML = "<ofsSessionResponse><responseData><responseDetails><error>" + sErrMsg + "</error></responseDetails></responseData></ofsSessionResponse>";
		ivResponseXml = sMessageXML;
	}
	
	
	// Process the get login page
	/**
	 * Process get login command.
	 */
	private void processGetLoginCommand()
	{
		displayLoginPage( ivRequest.getValue("error") );
	}

	// Process the login command, checking whether it is valid or not
	/**
	 * Process login command.
	 */
	private void processLoginCommand()
	{
		// Check if this login request is from a cache request as we don't
		// want them to be able to refresh.  This is determined by the counter
		// supplied in the request (as stored on the login page)
		String reqCounter = ivRequest.getValue("counter");
		String userCounter = getUserLoginCounter();

		Integer tokenSequenceNumber = new Integer(0);
		ivSession.setAttribute( TOKEN_SEQUENCE_NUMBER, tokenSequenceNumber);
		
		setLoginCommand();

		if ( ( reqCounter == null ) || ( reqCounter.equals("") ) )
		{
			LOGGER.debug("Invalid login command - counter not supplied" );
			displayLoginPage();
		}
		else
   		{
			if ( reqCounter.equals( userCounter ) )				// Correct counter supplied
			{
				// This is a valid login request, so process it
				LOGGER.debug("Valid login command - counter is " + reqCounter );
				doLogin();
			}
			else												// Wrong counter supplied
			{
				LOGGER.info("Invalid login command - counter mis-match" );
				LOGGER.info("Request Counter = " + reqCounter );
				LOGGER.info("User Counter = " + userCounter );
				displayLoginPage();	
			}
   		}
		

	}

	// Process a valid login command
	/**
	 * Do login.
	 */
	private void doLogin()
	{
		setSignOnName( ivRequest.getValue("signOnName"));
		setSignOnNameEnabled( ivRequest.getValue("signOnNameEnabled"));
		setPassword( ivRequest.getValue("password"));

		ivRequest.setAttribute( "RememberOption", ivRequest.getValue("RememberOption"));
		ivRequest.setAttribute( "signOnName", ivRequest.getValue("signOnName"));
		ivRequest.setAttribute( "password", ivRequest.getValue("password"));

		processCommand();

		if (ivError)
		{
			// Error occurred during login, display login screen again with error text
			buildLoginPage();

			if ( (ivResponseHtml == null) || (ivResponseHtml.equals("")) &&
	 			 (ivResponseXml == null) || (ivResponseXml.equals("")) )
			{
				LOGGER.error("Error building Login page");
			}
			
			setPassword("");
		}
		else
		{
			// Successful, so indicate that we have logged in in this session
			ivLoggedIn = true;
			setLoggedIn("True");

			// Set the TC instance used for this new session
			if ( ivBranch != null ) 
			{
				setUserInstance( getInstanceName() );
			}

			if ( ivCommand.equals( BRANCH_ADMIN_LOGIN_COMMAND ) )
			{
				// We are a Branch Administrator
				setBranchAdministrator( true );
			}
			else
			{
				setBranchAdministrator( false );
			}

			// Increment our login counter in case anyone tries to refresh a login command
			updateUserLoginCounter();
			
			// Save the IP Address of the login request to check later for hacking attempts
			setClientIpAddress(ivClientIP);

			// If there is a cached request then run it
			runCachedRequest();
		}
	}


	// Display the initial login page (first time it has been run in the session)
	/**
	 * Inits the login page.
	 */
	public void initLoginPage()
	{
		setLoginCommand();
		setSignOnNameEnabled("Y");
		displayLoginPage();
	}

	// Display the login page (as invalid login attempt tried)
	/**
	 * Display login page.
	 */
	private void displayLoginPage()
	{
		displayLoginPage("");
	}

		// Display the login page (as invalid login attempt tried)
	/**
		 * Display login page.
		 * 
		 * @param sErrorText the s error text
		 */
		private void displayLoginPage( String sErrorText )
	{
		// Check if we are in a branch server
		if ( ( ivBranch == null ) && (ivCommand != null) && ( ( ivCommand.equals( BRANCH_ADMIN_LOGIN_COMMAND ) ) ) )
		{
			String sDefaultError = "The Web Server is not running as a Branch Server";
			String sError = sDefaultError;

			if ( ivBranchStrings != null )
			{
				sError = ivBranchStrings.getString( BranchConstants.BR_STRING_ERROR_NOT_BRANCH_SERVER );
			}

			if ( ( sError == null ) || ( sError.equals("") ) )
			{
				sError = sDefaultError;
			}

			buildErrorResponse( sError );
		}
		else
		{
			String sName = getSignOnName();
			String sNameEnabled = getSignOnNameEnabled();
			
			if ( ( sName != null ) && ( ! sName.equals("") ) )
			{
				if ( sNameEnabled.equals("Y") )
				{
					setSignOnName("");
				}
			}
			
			setUserId("");
			setPassword("");
			setBranchAdministrator( false );

			ivErrorText = sErrorText;
			ivError = false;

			ivLoginCounter = "" + this.updateUserLoginCounter();

			ivResponseHtml = "";
			ivResponseXml = "";
			ivResponseUrl = "";

			buildLoginPage();

			ivLoggedIn = false;
			setLoggedIn("");
			ivError = true;		// Mark as an error so we display in HTML format
		}
	}

	// Get the XML for the login page
	/**
	 * Builds the login page.
	 */
	public void buildLoginPage()
	{
		// Check for re-authentication following session timeout.
		// If timedout then ensure that the sign on name field is enabled.
		// Also clear out any cacched request in case they log in as another user.
		if ( ivSignOnName == null || ivSignOnName.equals("") || ivSignOnName.equals("null") )
		{
			ivSignOnName = "";
			setSignOnNameEnabled("Y");
			setCachedRequest( "" );
		}	

		// Check if we have a counter, if not then it's the first time so default it to 0
		String sCounter = ivLoginCounter;

		if ( sCounter.equals( "" ) )
		{
			sCounter = updateUserLoginCounter();
		}
			
		setUserLoginCounter( sCounter );

		// Determine the log in page to use
		boolean isUsingWebAppSecurity = (ivHttpRequest.getUserPrincipal() != null);
		if (isUsingWebAppSecurity) {
			// We are using web app security - redirect to the home page or an error page as appropriate
			if (ivErrorText != null && !ivErrorText.equals("")) {
				if (ivHttpRequest.getAuthType().equals("BASIC")){
					// Attempt to force re-athentication
					this.ivHttpErrorCode = HttpServletResponse.SC_UNAUTHORIZED;
				}
                else if (ivErrorText.toUpperCase().startsWith("SECURITY VIOLATION")){
                    // invalidate the session
                    ivInvalidateSession = true;
                    throw new ArcAuthenticationServerException("SECURITY VIOLATION on login.");
                }
				else {
					this.ivHttpErrorCode = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
				}
			}
			else {
				String req = ivHttpRequest.getRequestURL().toString();
				int index = req.indexOf("servlet");
				ivResponseUrl = req.substring(0, index);
				ivInvalidateSession = true;
			}
		}
		else {
			String sLoginXml = "<responseDetails><login>";
		    sLoginXml += "<command>" + ivLoginCommand + "</command>";
			sLoginXml += "<title>" + ivLoginTitle + "</title>";
			sLoginXml += "<signOnName>" + "" + "</signOnName>";
			sLoginXml += "<showUserName>" + BrowserServlet.showUserName + "</showUserName>";
			sLoginXml += "<signOnNameEnabled>" + ivSignOnNameEnabled + "</signOnNameEnabled>";
			sLoginXml += "<password>" + "" + "</password>";
			sLoginXml += "<error>" + ivErrorText + "</error>";

			if ( ivLoginCommand.equals( BRANCH_ADMIN_LOGIN_COMMAND ) )
			{
				sLoginXml += BranchConstants.XML_BRANCH_ADMIN_LOGIN_TAG + "Y" + BranchConstants.XML_BRANCH_ADMIN_LOGIN_TAG_C;
			}

			sLoginXml += "<counter>" + sCounter + "</counter>";
			
			// Add any links read from the parameter file
			PropertyManager links = (PropertyManager) ivServletContext.getAttribute(LOGIN_PAGE_LINKS);
			
			if ( links != null )
			{
				String linksXml = links.toXml();
				linksXml = Utils.replaceAll( linksXml, "properties>", "links>" );
				linksXml = Utils.replaceAll( linksXml, "property>", "link>" );
				linksXml = Utils.replaceAll( linksXml, "name>", "caption>" );
				linksXml = Utils.replaceAll( linksXml, "value>", "target>" );
				linksXml = Utils.replaceAll( linksXml, "<target>www.", "<target>http://www." );
				linksXml = Utils.replaceAll( linksXml, "<target>WWW.", "<target>http://www." );
				sLoginXml += linksXml;
			}
			
			sLoginXml += "</login></responseDetails>";
			
	   		boolean arcIb =  DebugUtils.getBooleanProperty(ivParameters, 
	   				DebugUtils.USE_NEW_SKIN_NAME);
			if(arcIb) 
			{
	        	// NOTE: For T24-Updates ARC_IB_LOGIN_STYLESHEET has been modified so NOT to
	        	// show the username & password input boxes.
	        	//
	        	// If the User did not come through the UrlLoginFilter OR the session has been
	        	// invalidated.
	        	//
				// Check to see if the User has logged in via the URLLoginFilter

				// Check to see if the User has logged in via the AdminLoginFilter
				HttpSession session = ivHttpRequest.getSession();
				Object attributeObj = session.getAttribute("T24UpdatesURLLogon");
		        attributeObj = session.getAttribute("T24UpdatesAdminLogon");
		        String adminLogin = "false";
		        if (attributeObj != null) {
		        	adminLogin = (String)attributeObj;
		        }
		        
		        if (adminLogin.equalsIgnoreCase("true")){
		        	// Select the T24 Updates Administrator Login Page....
		        	transformResult(T24_UPDATES_ADMIN_LOGIN_STYLESHEET, sLoginXml);
		        }else {
		        	// 'Normal' ARC-IB transform
		        	transformResult( ARC_IB_LOGIN_STYLESHEET, sLoginXml );
		        }
			} else {
				// Use the BrowserWeb transform
				transformResult( LOGIN_STYLESHEET, sLoginXml );
			}
		}
	}

	/**
	 * Process quick guide.
	 */
	private void processQuickGuide()
	{
		// Get the user name
		String sUser = ivRequest.getValue("user");

		// Get the list of valid skin names on the web server from the parameter file
		String sValidSkins = ivWebServerSkinNames;

		// Get the currently selected skin name
		String sSkin = ivRequest.getValue("skin").toLowerCase();

		// Get the URl from the request and remove the servlet/BrowserServlet off the end of it
		String sRequestUrl = ivHttpRequest.getRequestURL().toString();
		int iEndPos = sRequestUrl.indexOf("servlet/BrowserServlet");
		String sUrlPath = sRequestUrl.substring(0, iEndPos);

		ivResponseUrl = sUrlPath + "jsps/quickguide.jsp?user=" + sUser + "&skin=" + sSkin + "&validSkins=" + sValidSkins;
	}

	/**
	 * Process command.
	 */
	private void processCommand()
	{
		// Check if the user running the command is that saved in the session -
		// they may be different if they have run a SIGN.ON command and logged in as someone
		// else but have an old screen on display
		String requestUser = ivRequest.getValue("user");
		String sessionUser = getUserId();
		HttpSession session = ivHttpRequest.getSession();
		String sessionId = session.getId();

		if ((requestUser == null) || (requestUser.equals("")) || ((ivCommand.equals("login") || ivCommand.equals("branchadminlogin") || ivCommand.equals("repeatpassword")) || requestUser.equals(sessionUser)))
		{
			// Process the request by sending it to T24
			processCommandRequest();
		}
		else if ( ( sessionUser != null ) && ( ! requestUser.equals(sessionUser) ) )
		{
			// If the request user is blank then allow the request to be processed by using the user name in the session
			if ( requestUser.equals("") )
			{
				ivRequest.setParameter("user", sessionUser);
				processCommandRequest();
			}
			else if ( sessionUser.equals("") && ivRequestType.equals("DESTROY.SESSION") )
			{
				// The session user is blank and they are logging out so just display the login page
				processDisplayLogin();
			}
			else if ( sessionUser.equals("") )
			{
				// The session user is blank so the session must have timed out
				String svMessage = getSecurityViolationMessage("SV-06");
				buildErrorResponse(svMessage);
				LOGGER.debug(svMessage + " - " + sessionId + " - Web Server - session user is null due to a possible session timeout");
				LOGGER.debug("Request User = " + requestUser + ", Session User = " + sessionUser );
			}
			else
			{
				// The user names don't match, must have done a SIGN.ON and changed user
				String svMessage = getSecurityViolationMessage("SV-02");
				buildErrorResponse(svMessage);
				LOGGER.debug(svMessage + " - " + sessionId + " - Web Server - session user doesnt match request user as user name has changed, probably due to a SIGN.ON command.");
				LOGGER.debug("Request User = " + requestUser + ", Session User = " + sessionUser );
			}
		}
		else
		{
			// Session looks like it timed out or the user logged out, so display the login page

			// If we are in composite screen then force the login page to be
			// displayed in full window mode, rather than in the current frame
			
			// If the user was logging out then do not display a security violation, just display the login page
			if ( ivRequestType.equals("DESTROY.SESSION") )
			{
				ivError = false;
				ivErrorText = "";
			}
			else
			{
				// Checks for invalid user session by having "true" in userTimedOut attribute of request object.
				String userTimedOut = ivRequest.getValue("UserTimedOut"); 
				// By retrieving browser product ensures the message only will be displayed for browser.
				String browserProduct = ivParameters.getParameterValue("Product");
				
				if (userTimedOut.equals("true")&& (!browserProduct.equalsIgnoreCase("arc-ib")))
				{
					// Set error to true.
					ivError = true;
					LOGGER.debug("Error is identified as web session timed out: time out error=" + ivError );
					// Build the session timed out message and return to display reponse.
					try {
						Locale localeob=ivHttpRequest.getLocale();
					 	ResourceBundle reslabel = ResourceBundle.getBundle("errorMessages",localeob);
					    String keyvalue = reslabel.getString("session.timeout");
					    buildMessageResponse( "", keyvalue );
						LOGGER.debug("User’s web session timed out and message is displayed");
						return;
					}
					catch(Exception e) {
						LOGGER.error(e.getMessage()); 
					}
				}
				else
				{
					ivError = true;
					ivErrorText = getSecurityViolationMessage("SV-03");
					LOGGER.debug( ivErrorText + " - " + sessionId + " - Web Server - Session timed out or user logged out.  sessionUser is " + sessionUser + ", requestUser is " + requestUser + ", command is " + ivCommand );
					LOGGER.debug( "Re-displaying login page as session timed out or user logged out" );
					LOGGER.debug( "  Request User = " + requestUser + ", Session User = " + sessionUser );
				}
			}
			
			// Display the login page
			processDisplayLogin();
		}
	}
	
	//Sends a request to the API, and parses the data returned.
	/**
	 * Call API.
	 * 
	 * @param xmlManager the xml manager
	 */
	private void callAPI(XMLRequestManager xmlManager)
	{

		String ofsXmlResult = "";
		String apiResponse = "";
		String callClass = readParameterValue(PARAM_CALL_CENTRE_CLASS);

		//Connect to the API
		APICallCentreBean callcentre = new APICallCentreBean();

		try
		{
			//get the defined call centre class to be used
			callcentre.setupConnection(callClass);
			//pass the request to the API
			apiResponse = callcentre.talkToCallCentre(xmlManager.getXMLResponse());
			//pass the API response to the response manager
			APIResponseManager manager = new APIResponseManager(apiResponse, ivHttpRequest, getXmlTemplates());

			//check to see what type of response we are dealing with
			if ((manager.getResponseType()).equals("HTML"))
			{
				//the response is an html page
				setAPIResponseType("HTML");
				ivResponseHtml = manager.getResponse();
			}
			else if ((manager.getResponseType()).equals("XML"))
			{
				//the response is an xml doc
				//**there needs to be a responsType node in this message
				//if you want it to be processed.
				processOfsResponse(manager.getResponse(), xmlManager);
			}
			else if ((manager.getResponseType()).equals("URL"))
			{
				//the response is a url
				setAPIResponseType("URL");
				ivResponseUrl = manager.getResponse();
			}

			else if ((manager.getResponseType()).equals("MESSAGE"))
			{
				//the response is a message to be returned to the browser
				processOfsResponse(manager.getResponse(), xmlManager);
			}

			else if ((manager.getResponseType()).equals("POLL"))
			{
				//the response is a polling message to be returned to the browser
				processOfsResponse(manager.getResponse(), xmlManager);
			}

			else if ((manager.getResponseType()).equals("COMMAND"))
			{
				//the response is an xml doc that needs to be passed to T24
				BrowserResponse browserResult = sendOfsRequestToServer(manager.getResponse());
				processOfsResult(browserResult, xmlManager);
			}

		}
		catch (ConnectionNotCreatedException e)
		{
			buildErrorResponse("ConnectionNotCreatedException: The API client could not be found.");
			return;
		}
		catch (ClassCastException e)
		{
			buildErrorResponse("ClassCastException: Error constructing the API.");
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Process ofs result.
	 * 
	 * @param browserResult the browser result
	 * @param xmlManager the xml manager
	 * 
	 * @return the string
	 */
	private String processOfsResult(BrowserResponse browserResult, XMLRequestManager xmlManager)
	{
		String ofsXmlResult = "";

		if (browserResult != null)
		{
			if (browserResult.isValid() == true)
			{
				// If we have got multiple responses then merge them in to one response document
				String responseXml = browserResult.getMsg();

				// Check if the xml contains any graphs or charts that need to be replaced with links to images.
				String graphDisplayType = readParameterValue(PARAM_GRAPH_DISPLAY_TYPE);
				if (graphDisplayType != null && graphDisplayType.equals(PARAM_PNG_IMAGE)) {
					Graph graph = new Graph();
					responseXml = graph.createGraphFile(responseXml, ivSession);
				}

				// Strip out any hidden fields that should not be displayed at the client.
				LOGGER.debug("\n -- Full Response before Storing : " + responseXml);
				XmlResponse t24Response = new XmlResponse( responseXml,ivRequest);
				if( t24Response.hasRequestData())
				{
					// Get the new secured resposne
					responseXml = t24Response.getResponseXml();
					// Create a unique id out of Company User Application & Id
					String requestDataId = t24Response.getUniqueId();
					if(t24Response.getTabExpandInfo()){	
						//Check for the expansion of same duration and update the hidden field information accordingly 
						RequestData hiddenValues = (RequestData) ivSession.getAttribute(requestDataId);
						Hashtable commandParams = new Hashtable();
						RequestData copy = new RequestData(commandParams);
						String[] patternArray = t24Response.getRequestData().toString().split("Parameter :");
						String pattern = patternArray[1];
						String fieldName = "";
						String fieldValue = "";
						pattern = pattern.substring(1,pattern.indexOf("fieldName"));
						if(hiddenValues.getSortedNameList().toString().contains(pattern))
						{
							for (int i=0;i<hiddenValues.getSortedNameList().size();i++)
							{
								if(!(hiddenValues.getSortedNameList().get(i).toString().contains(pattern)))
								{									
									fieldName = (String) hiddenValues.getSortedNameList().get(i);
									fieldValue = hiddenValues.getValue(fieldName);
									fieldValue = Utils.decodeHtmlEntities(fieldValue);
									copy.addDataItem(fieldName, fieldValue);
								}
							}
							hiddenValues = copy;
							hiddenValues.addData(t24Response.getRequestData());
							ivSession.setAttribute( requestDataId , hiddenValues);
						} else {	
							//Generate new key for unique future dated condition
							String company = Utils.getNodeFromString( responseXml, Constants.COMPANY);
							String user = Utils.getNodeFromString( responseXml, Constants.USER);
							String application = Utils.getNodeFromString( responseXml, Constants.APP);
							String screenMode = Utils.getNodeFromString( responseXml, Constants.SCREEN_MODE);
							String key = Utils.getNodeFromString( responseXml, Constants.KEY);							
							requestDataId = company + "_" + user + "_" + application + "_" + screenMode + "_" + key;
							ivSession.setAttribute( requestDataId , t24Response.getRequestData());
						}
						
					} else {					
					// Add the hidden fields to the session so that when a request comes back from that window
					// previous hidden parameters can be appended to the request.
					ivSession.setAttribute( requestDataId , t24Response.getRequestData());
					}
					LOGGER.debug("\n -- Store session: " + requestDataId + " ----\n");
					LOGGER.debug( t24Response.getRequestData());
				}
				
				// Extract <docDownload> to identify document download response and store download details in session.
 				// docDownload string will have all drill info and drill items.
				String docDownload = Utils.getNodeFromString( responseXml, "docDownload");
 				if ( !docDownload.equals("") && !docDownload.equals(null))
 				{
					LOGGER.debug("Document download details retrieved to store in session");
 					// Create StoreResponse object to store download details in session.
 					StoreResponse storeResponse = new StoreResponse(responseXml, null, ivRequest, ivSession);
 					// Store document details as key-value pair.
 					storeResponse.storeDocumentIdsInSession(docDownload);
 				}

				// Check if there are any nochangeFields we need to store
				String doNochangeFieldsCheck = readParameterValue(PARAM_DO_NOCHANGE_FIELDS_CHECK);
				if (doNochangeFieldsCheck == null || doNochangeFieldsCheck.equals(PARAM_DO_NOCHANGE_FIELDS_CHECK_YES)) {
					PropertyManager nochangeFields = (PropertyManager) ivServletContext.getAttribute(BrowserServlet.NOCHANGE_FIELDS_INIT_PARAM);
					StoreResponse sr = new StoreResponse(responseXml, nochangeFields, ivRequest, ivSession);
					sr.storeValuesInSession();
				}
				
				if ( RequestUtils.bulkResponse( responseXml ) )
				{
					
					XMLBulkResponse bulkResp = new XMLBulkResponse( responseXml );
					responseXml = bulkResp.toXml();
				}
				else if ( ivRequest.subPaneRequest() )
				{
					// Process the sub-pane response, adding a WS_multiPane to the response
					XMLSubPaneResponse subPaneResp = new XMLSubPaneResponse( responseXml );
					ivWebFields.put( XMLConstants.XML_WS_MULTI_PANE, "true" );
					responseXml = subPaneResp.toXml();
				}
				
				processOfsResponse(responseXml, xmlManager);
				ofsXmlResult = responseXml; //browserResult.getMsg();

				// If responseType is ‘XML.DOCUMENT.SERVICE’ then extract the drill info from request object
 				// and get document ID from stored session.
 				try
 				{
 					String responseType = xmlManager.getNodeValue(ofsXmlResult, "responseType");
 					if ( responseType.equals("XML.DOCUMENT.SERVICE") )
 					{
 						String drillItem = ivRequest.getParameterValue("routineArgs");
 						// Extract routineName to check single or multi download.
 						String multiDownload = ivRequest.getParameterValue("routineName");
 						StoreResponse storeResponse = new StoreResponse(responseXml, null, ivRequest, ivSession);
 						// In case of MULTI.DOWNLOAD get document ID's from session sequence and append it.
 						// Request format will be like ENQNAME+MUTI.DOWNLOAD.FUNCTION+DRILLINFO so extract only the 
 						// drill info and pass to stored session.
 						if ( multiDownload.equals("OS.MULTI.DOWNLOAD") )
 						{
 							LOGGER.debug("Multi document download type");
 							String multiDownloadFunction = "MUTI.DOWNLOAD.FUNCTION";
 							String multiDownloadDocuments = "";
 							int mDownloadLen = multiDownloadFunction.length();
 							int indexInDrillItems = drillItem.indexOf(multiDownloadFunction);
 							int indexOfdrillItem = drillItem.length();
 							String drillInfo = drillItem.substring(indexInDrillItems + mDownloadLen + 1,indexOfdrillItem -1 );
 							String []noOfDrillItems = drillInfo.split("\\|");
 							for ( int k = 0 ; k < noOfDrillItems.length ; k++)
 							{
 								// Get the document ID of corresponding drill info and append to document ID.
 								multiDownloadDocuments += setDocumentServiceId(storeResponse.getDocumentDrilledItem(noOfDrillItems[k]));
 								// Have '|' as delimiter in document ID which will be expected in browserServlet.
 								if ( !( k == noOfDrillItems.length-1))
 									multiDownloadDocuments += "|";
 							}
 							ivDocumentServiceId =  multiDownloadDocuments;
 							// set multi document download as true.
 							ivMultiDownload = true;
 						}
 						else
 						{
 							// In case of single document download drill info will be like 1_1_1.
 							// But stored session drill info (key) as only 1_1 so trim the upto first delimiter.
 							// It makes the same session document details used for both types of downloads.
 							LOGGER.debug("Single document download type");
 							drillItem = drillItem.substring(2);
 							// Get and set the document ID.
 							setDocumentServiceId(storeResponse.getDocumentDrilledItem(drillItem));
 						}
 					}
 				}catch(XMLRequestManagerException e)
 				{
 					LOGGER.debug("Response type couldn't retrieved from XML response." + e );
 				}

				String logMsg = com.temenos.t24browser.utils.Logger.
					replacePassword( ofsXmlResult );
				LOGGER.debug("XML Response: " + logMsg);
			}
			else
			{
				buildErrorResponse(browserResult.getError());
			}
		}
		else
		{
			buildErrorResponse("Error communicating with the T24 server.");
		}

		return ofsXmlResult;
	}

	/**
	 * Gets the xml from file.
	 * 
	 * @param sFileName the s file name
	 * 
	 * @return the xml from file
	 */
	public String getXmlFromFile(String sFileName)
	{
		String sXml = "";

		try
		{
			File f = new File(sFileName);
			FileInputStream inputStream = new FileInputStream(f);
			byte[] bytes = new byte[(int) f.length()];
			inputStream.read(bytes);
			inputStream.close();
			sXml = new String(bytes, "UTF8");
		}
		catch (Exception e)
		{
			System.out.println("Error reading XML from file - " + e.getMessage());
		}

		String logMsg = com.temenos.t24browser.utils.Logger.
			replacePassword( sXml );
		LOGGER.debug("XML from File: " + logMsg);

		return (sXml);
	}

	// Convert the HTTP request in to XML
	/**
	 * Gets the request xml.
	 * 
	 * @return the request xml
	 */
	private String getRequestXml()
	{
		String reqXml = "";

		// Set the token for this request
		ivRequest.setAttribute("token", "BLANK");

		// Convert command HTTP request in to OFS command XML format
		XMLRequestManager xmlManager = null;

		try
		{
			xmlManager = new XMLRequestManager(ivRequest, getXmlTemplates());
			reqXml = xmlManager.getXMLResponse();
		}
		catch (XMLRequestTypeException e)
		{
			// Error so don't save a request
		}

		return( reqXml );
	}

	// Re-authenticate the user
	/**
	 * Process reauthentication.
	 * 
	 * @param sErrorText the s error text
	 */
	public void processReauthentication( String sErrorText )
	{
		// Save the XML request - as long as it was a real Browser request
		saveRequest();
		setSignOnNameEnabled("N");
		setPassword("");
		ivErrorText = sErrorText;
		ivError = true;

		ivResponseHtml = "";
		ivResponseXml = "";
		ivResponseUrl = "";
		String ivGetLoginCommand = "";
		
		// Display the appropriate login page for re-authentication
		if ( ivBaUser )
		{
			ivLoginCommand = BRANCH_ADMIN_LOGIN_COMMAND;
			ivGetLoginCommand = BRANCH_ADMIN_GET_LOGIN_COMMAND;
			ivLoginTitle = ivBranchStrings.getString( BranchConstants.BR_STRING_ADMIN_LOGIN_TITLE );
		}
		else
		{
			ivLoginCommand = USER_LOGIN_COMMAND;
			ivGetLoginCommand = USER_GET_LOGIN_COMMAND;
			ivLoginTitle = USER_LOGIN_TITLE;
		}

		// If we are in composite screen then force the login page to be
		// displayed in full window mode, rather than in the current frame
		String compScreen = ivRequest.getValue("compScreen");

		if ( ( compScreen != null ) && ( ! compScreen.equals("") ) )
		{
			String sLoginXml = "<responseDetails><login>";
			sLoginXml += "<command>" + ivGetLoginCommand + "</command>";
			sLoginXml += "<signOnName>" + ivSignOnName + "</signOnName>";
			sLoginXml += "<showUserName>" + BrowserServlet.showUserName + "</showUserName>";
			sLoginXml += "<signOnNameEnabled>" + ivSignOnNameEnabled + "</signOnNameEnabled>";
			sLoginXml += "<error>" + ivErrorText + "</error>";
			sLoginXml += "</login></responseDetails>";

			// Transform the XML depending on the settings
			transformResult( GET_LOGIN_STYLESHEET, sLoginXml );
		}
		else
		{
			displayLoginPage( ivErrorText );
		}

		ivLoggedIn = false;
		setLoggedIn("");
		setSignOnNameEnabled("Y");
	}

	// Run any cached request following re-authentication
	/**
	 * Run cached request.
	 */
	private void runCachedRequest()
	{
		String cachedReq = getCachedRequest();

		if ( ( cachedReq != null ) && ( ! cachedReq.equals("") ) )
		{
			// Get the new token following successful login and replace the place holder in the cached request
			String oldTokenTag = "<token>BLANK</token>";
			String newTokenTag = "<token>" + getRequestToken() + "</token>";

			ivRequestXml = replaceAll( cachedReq, oldTokenTag, newTokenTag );

			String logMsg = com.temenos.t24browser.utils.Logger.
				replacePassword( ivRequestXml );
			LOGGER.debug("Running cached XML request : " + logMsg);

			processCommand();
			setCachedRequest("");
		}
	}

	// Set the variables for building the login page
	/**
	 * Sets the login command.
	 */
	private void setLoginCommand()
	{
		ivLoginCommand = USER_LOGIN_COMMAND;
		ivLoginTitle = USER_LOGIN_TITLE;

		if ( ( ivCommand != null ) && ( ! ivCommand.equals("") ) )
		{
			if ( ( ivCommand.equals( BRANCH_ADMIN_LOGIN_COMMAND ) && ivBranch != null ) )
			{
				ivLoginCommand = BRANCH_ADMIN_LOGIN_COMMAND;
				ivLoginTitle = ivBranchStrings.getString( BranchConstants.BR_STRING_ADMIN_LOGIN_TITLE );
			}
		}
		else
		{
			// No command supplied, so default to a standard login page
			ivCommand = USER_LOGIN_COMMAND;
		}
	}

	// Save the current Browser request - if required
	/**
	 * Save request.
	 */
	private void saveRequest()
	{
		// Ignore NO.REQUESTs and Toolbox requests
		if ( ( ivBranch != null ) &&
			 ( ! ivRequestType.equals("NO.REQUEST") ) &&
			 ( ! ivCommand.equals("smartclient") ) )
		{
			String savedXml = getRequestXml();
			setCachedRequest( savedXml );
		}
		else
		{
			setCachedRequest( "" );
		}
	}
	
	/**
	 * Process download request.
	 * 
	 * @param response the response
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	public void processDownloadRequest(HttpServletResponse response) throws IOException {
		//filename is formed with enquiry id
		String filename =ivRequest.getParameter("enqid");
		if(filename == null)
		{
			filename = "enquiry.";
		}
		String dotValue=".";
		filename = filename.concat(dotValue);
		String downloadType = (String)ivHttpRequest.getAttribute("downloadType");
		if(downloadType.equals(".txt")) {
 				filename = "Report";
 	 			response.setContentType("text/plain;charset=utf-8");
 		} else if(downloadType.equals("ofx")) {
			response.setContentType("application/x-ofx");
		}else if(downloadType.equals("pdf")){
			response.setContentType("application/pdf;charset=utf-8");
		}else if(downloadType.equals("xls"))
		{
			response.setContentType("html/MS-Excel");
		}else {
			response.setContentType("application/stream");
		}
		byte [] byteArray;
		byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
		//filename = filename.concat(downloadType);
		// we have changed the Content-Disposition of html to “inline”
		if(downloadType.equals("preview-html"))
		{
			// set content type to have a print preview of the result
			response.setContentType("text/html;charset=utf-8");
		} else {
	    filename = filename.concat(downloadType);
		response.setHeader("Content-Disposition","attachment; filename="+filename+";"); 
		}
		if(downloadType.equals("pdf"))
		{
			HttpSession session = ivHttpRequest.getSession(false);
			byteArray=(byte[])session.getAttribute("downloadpdfFile");			
		}
		else
		{
			String source = (String)ivHttpRequest.getAttribute("downloadFile");
			if(downloadType.equals(".txt")) {
				source = Utils.decodeHtmlEntities(source);
			}
			if(source == null)
			{
				// use ivRequest instead
				String source2 = (String)ivRequest.getAttribute("downloadFile");
				source = source2;
			} 
			 source= Utils.decodeOutputEncodingEntities(source,ivServletContextPath, true);
			 	//only for txt file store in utf-8 character
				if(downloadType.equals(".txt"))
				{
					byteArray = source.getBytes("UTF8");
				}
				else
				{
					byteArray = source.getBytes();
				}
		}
		ServletOutputStream out = response.getOutputStream(); 
		out.write(bom);
		out.write(byteArray);
		out.flush();
		out.close();
	}
	
	
	/**
	*  @param req The request
    *  @param resp The response
    *  @param filename The name of the file you want to download.
    *  @param original_filename The name the browser should receive.
    */
  
	public static void doDownload( HttpServletRequest req, HttpServletResponse resp,
            String filename)
throws IOException,FileNotFoundException
{
File                f        = new File(filename);

int                 length   = 0;
ServletOutputStream op       = resp.getOutputStream();
String modFileName="";
String realFilename = "";
String ModRealName =  "";
String  sContentType="";
// get the exact file name from the download path

//If the filePath is relative, then add the servlet context path in front.
// Otherwise indicate that we are using the 'file://' protocol to access the file.
// If the path is relative then displayPath will begin with "./" or "../"
//check the is relative or not
	    if (filename.substring(0,1).equals(".")) {
		int fileNameLen = filename.length();
		int filemodlen = filename.indexOf("/");
		//check for relative path
		if ((filename.startsWith("../")))
		{
			realFilename = filename;
		}
		//get the exact relative path, exclude of "./" and "../"
		else if(filemodlen > 0)
		       {
			      realFilename = filename.substring(filemodlen,fileNameLen);
		       } 
		  //add the contex path in before of the relative path
	        filename =  ivServletContextPath + '/' + realFilename;
		  
		    File  fRel = new File(filename);      
		    int realnameLen = filename.length();
		    int modrealnamelen = filename.lastIndexOf("/");
		    //pick the exact download file name 
		      if ( modrealnamelen > 0 ){
		    	   ModRealName = filename.substring(modrealnamelen+1,realnameLen);
		      }
	         
		     int modRealNameLength = ModRealName.length();
		     int modRealNameLen = ModRealName.lastIndexOf(".");
		     // get the downlaod file extension 
		     if (modRealNameLen > 0){
		    	 sContentType =  ModRealName.substring(modRealNameLen+1 ,modRealNameLength );
		    	  
		     }
		         
		         resp.setContentType(sContentType);
				 resp.setContentLength( (int)fRel.length() );
				 resp.setHeader( "Content-Disposition", "attachment; filename=\"" + ModRealName + "\"" );
				 // Data is read byte by byte
				 byte[] bbufRel = new byte[4*1024];
				 try
				 {
					 DataInputStream inRel = new DataInputStream(new FileInputStream(fRel));
					 while ((inRel != null) && ((length = inRel.read(bbufRel)) != -1))
					 {
						op.write(bbufRel,0,length);
					 }

					 inRel.close();
					 op.flush();
					 op.close();
				 }catch(Exception e){
					 LOGGER.debug( "File Not found exception = " + e.getMessage() );
				 	}
		}else{
				int fileNameLen = filename.length();
				int modFileLength = filename.lastIndexOf("/");
				 //pick the exact download file name 
						if (modFileLength > 0)
						{	
							modFileName = filename.substring(modFileLength+1,fileNameLen);
						}

							// get the exact file extension  from the download file  	
							int  modFileNameLength = modFileName.length();
							int  modFileNamelen = modFileName.lastIndexOf(".");
							// get the downlaod file extension 
							if(modFileNamelen>0){
								sContentType = modFileName.substring(modFileNamelen+1,modFileNameLength);
							}
					

							resp.setContentType(sContentType);
							resp.setContentLength( (int)f.length() );
							resp.setHeader( "Content-Disposition", "attachment; filename=\"" + modFileName + "\"" );
							byte[] bbuf = new byte[4*1024];
							try
							{
								DataInputStream in = new DataInputStream(new FileInputStream(f));
								while ((in != null) && ((length = in.read(bbuf)) != -1))
								{
									op.write(bbuf,0,length);
								}

								in.close();
								op.flush();
								op.close();
							}catch(Exception e){
								 LOGGER.debug( "File Not found exception = " + e.getMessage() );
						 		}
		     }
}
	
	private void processCommandRequest()
	{
		// Set the token for this request
		ivRequest.setAttribute("token", getRequestToken());
	
		// Convert command HTTP request in to OFS command XML format
		XMLRequestManager xmlManager = null;
		String destination = "";
	
		// If we have a request in XML format then no need to convert it to XML format
		if ( ivRequestXml.equals("") )
		{
			try
			{
				if ( ivRequest.bulkRequest() )
				{
					XMLBulkRequest bulkReq = new XMLBulkRequest(ivRequest, getXmlTemplates());
					xmlManager = bulkReq.getRequestManager();
					ivRequestXml = bulkReq.toXml();
				}
				else
				{
					xmlManager = new XMLRequestManager(ivRequest, getXmlTemplates());
					getRequestTimer().setParserTime(xmlManager.getParseTime());
					ivRequestXml = xmlManager.getXMLResponse();
					if(ivRequestType.equals("NO.REQUEST")){
						String wndwName = Utils.getNodeFromString(ivRequestXml, "windowName");
						if(	!(wndwName.equals("") || wndwName.equals(null)) ){
							Enumeration wkeys = ivSession.getAttributeNames();
							while (wkeys.hasMoreElements())
							{
								String wkey = (String)wkeys.nextElement();
								if(wkey.startsWith(wndwName)){
									ivSession.removeAttribute(wkey);
								}
							}
						}
					}
					destination = xmlManager.getDestination();
				}
			}
			catch (XMLRequestTypeException e)
			{
				// Build the error result in HTML as we supplied an invalid request type
				buildErrorResponse(e.getMessage());
				return;
			}
		}
		else
		{
			xmlManager = new XMLRequestManager();
		}
	
		String ofsXmlResult = null;
		BrowserResponse browserResult = null;
	
		// Look to see if the request is going to the API or not
		if ( destination.equals("API") )
		{
			callAPI(xmlManager);
		}
		//Standard server request
		else
		{
			//Before sending Request to server check for custom parameter in login Request
			if (ivRequestType.equals("CREATE.SESSION"))
			{
				ivRequestXml = updateCustomParams();
				
			}
			browserResult = sendOfsRequestToServer( ivRequestXml );
			// Mask or unmask the user name in LOGIN screen displaying after loggoff process			
			String tXmlMsg = browserResult.getMsg();			
			if(tXmlMsg!= null && xmlManager.nodeCheck(tXmlMsg, "responseType")) {
				try {
					String sResTyp;
					sResTyp = xmlManager.getNodeValue(tXmlMsg, "responseType");
					if (sResTyp != null && sResTyp.equals("XML.LOGOFF")&& xmlManager.nodeCheck(tXmlMsg, "responseDetails")) {
						String showUserName;
						if (xmlManager.nodeCheck(tXmlMsg, "login")) {						
							showUserName = "<showUserName>" + BrowserServlet.showUserName + "</showUserName>";
						}
						else {
							showUserName = "<login><showUserName>" + BrowserServlet.showUserName + "</showUserName></login>";
						}							
						tXmlMsg = xmlManager.addFragmentAtNode(showUserName, "responseDetails", tXmlMsg);
						// To fetch the links in the browser home page when sign off is made. 
                        PropertyManager links = (PropertyManager) ivServletContext.getAttribute(LOGIN_PAGE_LINKS);						
						if ( links != null )
						{
							String linksXml = links.toXml();
							linksXml = Utils.replaceAll( linksXml, "properties>", "links>" );
							linksXml = Utils.replaceAll( linksXml, "property>", "link>" );
							linksXml = Utils.replaceAll( linksXml, "name>", "caption>" );
							linksXml = Utils.replaceAll( linksXml, "value>", "target>" );
							linksXml = Utils.replaceAll( linksXml, "<target>www.", "<target>http://www." );
							linksXml = Utils.replaceAll( linksXml, "<target>WWW.", "<target>http://www." );
							int logoffpos = tXmlMsg.indexOf("</logoff>");
							String RequestXml = tXmlMsg.substring(0,logoffpos);
						    RequestXml += linksXml;
							tXmlMsg = RequestXml.concat("</logoff></responseDetails></responseData></ofsSessionResponse>");
						}
						browserResult.setMsg(tXmlMsg);
					}
				} catch (XMLRequestManagerException e) {
					buildErrorResponse(e.getMessage());
					return;
				}						
			}
			ofsXmlResult = processOfsResult(browserResult, xmlManager);
		}
	
		// Save returned token in the session for the next request - if we got one
		if ((ofsXmlResult != null) && (!ofsXmlResult.equals("")))
		{
			boolean nodePresent = xmlManager.nodeCheck(ofsXmlResult, "ofsSessionResponse");
			if (nodePresent)
			{
				try
				{
					// If we logged out then invalidate the session
					String sResponseType = xmlManager.getNodeValue(ofsXmlResult, "responseType");
					if ((sResponseType != null) && (sResponseType.equals("XML.LOGOFF")))
					{
						HttpSession session = ivHttpRequest.getSession();
	
						// If we're in ARC-IB mode, then redirect to its log out page.
						// Note that this page must appear in a directory that is not under declaritive security.
						// Note that this call to getAttribute must be before the invalidate call
						if (Boolean.TRUE.equals(session.getAttribute("ARC-IB"))) {
							String req = ivHttpRequest.getRequestURL().toString();
							int index = req.indexOf("servlet");
							String logOffType = xmlManager.getNodeValue(ofsXmlResult, "msg");
							if (logOffType.equals("PASSWORD CHANGED PLEASE RELOGIN"))
							{
								ivResponseUrl = req.substring(0, index) + "/modelbank/unprotected/loggedout_passwd.jsp";
								
							}else{
								ivResponseUrl = req.substring(0, index) + "/modelbank/unprotected/loggedout.jsp";																	         	
							}														
						}
	
													
						if (ivHttpRequest.isRequestedSessionIdValid())
					    	{
					    	// Increment our login counter in case anyone tries to refresh a login command
					    	updateUserLoginCounter();
					    	}
						 
						if (session != null)
						{
							LOGGER.debug( "Destroying Session on Logoff : Id = " + session.getId() );
							session.invalidate();
						}
					}
					else
					{
						String sNewToken = xmlManager.getNodeValue(ofsXmlResult, "token");
	
						String tokenFlag = xmlManager.getNodeValue(ofsXmlResult, "useTokenSequence");
	
						if ((sNewToken != null) && (!sNewToken.equals("")))
						{
							setRequestToken(sNewToken);
	
							if ((tokenFlag != null) && (!tokenFlag.equals("")))
								{
									//use new token sequence number
									sNewToken = sNewToken + ":";
									setRequestToken(sNewToken);
								}
						}
	
						// Save the user sign on name if we were logging in
						String sUser = xmlManager.getNodeValue(ofsXmlResult, "user");
	
						if ( ( ivCommand.equals("login") && (!sResponseType.equals("ERROR.LOGIN")) ) || ( ivCommand.equals("branchadminlogin") && (!sResponseType.equals("ERROR.LOGIN")) ) || ivCommand.equals("repeatpassword") )
						{
							setUserId(sUser);
						}
					}
				}
				catch (XMLRequestManagerException e)
				{
					buildErrorResponse(e.getMessage());
					return;
				}
			}
			else
			{
				if (!browserResult.isValid())
				{
					buildErrorResponse(browserResult.getError());
				}
				else
				{
					buildErrorResponse("Invalid response received from T24: Please contact your System Administrator.");
				}
	
				return;
			}
		}
	}
	
	
	// Some error may have occurred so display the login screen
	private void processDisplayLogin()
	{
		ivCommand = "login";
		ivSignOnName = "";
		ivPassword = "";

		String compScreen = ivRequest.getParameter("compScreen");

		if ( ( compScreen != null ) && ( ! compScreen.equals("") ) )
		{
			String sLoginXml = "<responseDetails><login>";
			sLoginXml += "<command>" + USER_GET_LOGIN_COMMAND + "</command>";
			sLoginXml += "<signOnNameEnabled>" + ivSignOnNameEnabled + "</signOnNameEnabled>";
			sLoginXml += "<showUserName>" + BrowserServlet.showUserName + "</showUserName>";
			sLoginXml += "<error>" + ivErrorText + "</error>";
			sLoginXml += "</login></responseDetails>";

			// Transform the XML depending on the settings
			transformResult( GET_LOGIN_STYLESHEET, sLoginXml );
		}
		else
		{
			buildLoginPage();
		}
	} 
	//To add custom parameters in login request
	/**
	 * Custom Parameters are defined in Browserparameters.xml
	 * To pass custom parameters as request when user logins
	 * @param param contains list of custom param defined in browserparameters.xml
	 * 
	 * @return ivRequestXml
	 */
	public String updateCustomParams() {
		int	reqlen = ivRequestXml.length();
		int custadd = ivRequestXml.indexOf("</ofsSessionRequest>");
		ivRequestXml = ivRequestXml.substring(0,custadd);
		Hashtable params = ivParameters.getParams();
		Set custpara = params.keySet();
		Iterator it = custpara.iterator();
		while(it.hasNext()) {
			String customparam = (String) it.next();
			if (customparam.startsWith("custom"))
			{
				String param = customparam;
				String value = ivParameters.getParameterValue(param);
				CustomXml += "<"+ param +">"+ value + "</" + param + ">";
			}
		}
		ivRequestXml = ivRequestXml.concat(CustomXml);
		ivRequestXml = ivRequestXml.concat("</ofsSessionRequest>");
		return ivRequestXml;
	}
}
