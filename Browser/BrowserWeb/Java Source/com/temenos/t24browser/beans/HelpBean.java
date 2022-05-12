package com.temenos.t24browser.beans;


////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   HelpBean
//
//  Description   :   Bean for controlling Help page requests.
//
//  Modifications :
//
//    27/08/04   -    Initial Version.
// 
////////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.servlets.HelpServlet;
import com.temenos.t24browser.utils.FileManager;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.xml.XMLConstants;
import com.temenos.t24browser.xml.XMLHelpManager;
import com.temenos.t24browser.xml.XMLRequestManager;
import com.temenos.t24browser.xml.XMLRequestManagerException;
import com.temenos.t24browser.xml.XMLRequestTypeException;
import com.temenos.t24browser.xml.XMLTemplateManager;
import com.temenos.tocf.tbrowser.TBrowserException;


// TODO: Auto-generated Javadoc
/**
 * The Class HelpBean.
 */
public class HelpBean extends TemenosBean implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(HelpBean.class);

	/** The iv view locations. */
	private Object[] ivViewLocations;					// Our list of locations to search when viewing
	
	/** The iv edit locations. */
	private Object[] ivEditLocations;					// Our list of locations to search when editing
	
	/** The iv help languages. */
	private Object[] ivHelpLanguages;					// Our list of supported languages
	
	/** The iv allowed languages. */
	private String ivAllowedLanguages;					// The list of possible languages.
	
	/** The iv images dir. */
	private String ivImagesDir = "";					// The images directory
	
	/** The iv pages editable. */
	private boolean ivPagesEditable;					// Whether pages are editable or not
	
	/** The iv development mode. */
	private boolean ivDevelopmentMode;				// Are we in Development Mode or not
	
	/** The iv style sheet. */
	private String ivStyleSheet = "";					// Stylesheet used for transformation
	
	/** The iv saved help xml. */
	private String ivSavedHelpXml = "";				// XML saved as a help file
	
	/** The Constant EDIT_STYLESHEET. */
	private static final String EDIT_STYLESHEET = "/transforms/help/editt24help.xsl";	// Edit stylesheet
	
	/** The Constant VIEW_STYLESHEET. */
	private static final String VIEW_STYLESHEET = "/transforms/help/help.xsl";			// View stylesheet
	
	/** The Constant MISSING_STYLESHEET. */
	private static final String MISSING_STYLESHEET = "/transforms/help/helpMessage.xsl";	// Messages stylesheet
	
	/** The Constant ARCIB_MISSING_PAGE. */
	private static final String ARCIB_MISSING_PAGE = "/modelbank/help/ARC/missing.html";	// Missing Helptext page
	
	/** The Constant UNAVAILABLE_PAGE. */
	private static final String UNAVAILABLE_PAGE = "/html/missinghelp.html";			// Unavailable Helptext page
	
	/** The iv language. */
	private String ivLanguage = DEFAULTLANG;			// Language of the page/request
	
	/** The iv product. */
	private String ivProduct = "";						// Product of page/request
	
	/** The iv application. */
	private String ivApplication = "";					// Application of page/request
	
	/** The iv field. */
	private String ivField = "";						// Field of page/request
	
	/** The iv page. */
	private String ivPage = "";						// A specific page request
	
	/** The iv desktop. */
	private String ivDesktop = "";						// A specific page request - from Desktop
	
	/** The iv path. */
	private String ivPath = "";						// A specific file request
	
	/** The iv item. */
	private String ivItem = "";						// A specific item request
	
	/** The iv path used. */
	private String ivPathUsed = "";					// The path of the file being displayed
	
	/** The iv missing paths. */
	private ArrayList ivMissingPaths;					// List of missing paths
	
	/** The iv missing. */
	private boolean ivMissing = false;				// Whether we have found a page or not
	
	/** The iv commons view. */
	private boolean ivCommonsView = false;			// Whether the common fields page is being viewed
	
	/** The iv display fields. */
	private String ivDisplayFields = "";			// List of fields to display
	
	/** The iv overview required. */
	private boolean ivOverviewRequired = true;		// Whether the overview section is required or not 
	
	/** The iv fields required. */
	private boolean ivFieldsRequired = true;		// Whether the fields are required or not 
	
	/** The iv menu required. */
	private boolean ivMenuRequired = true;			// Whether the menu should be displayed or not 
	
	/** The Constant DEFAULT_IMAGES_DIR. */
	private static final String DEFAULT_IMAGES_DIR = "help/images";		// Images directory
	
	/** The Constant COMMON_FIELDS_PAGE. */
	private static final String COMMON_FIELDS_PAGE = "COMMON.FIELDS";	// The name of the common fields page
	
	/** The Constant TOKEN_SESSION_NAME. */
	private static final String TOKEN_SESSION_NAME = "BrowserToken";		// Used to save the Token in the session
	
	/** The Constant XML_TYPE. */
	private static final String XML_TYPE = "XML";	// File type is XML
	
	/** The Constant HTML_TYPE. */
	private static final String HTML_TYPE = "HTML";	// File type is HTML
	
	/** The Constant CR_PARSER_CODE. */
	private static final String CR_PARSER_CODE = "&amp;cr;";
	
	/** The Constant CR_XML_CODE. */
	private static final String CR_XML_CODE = "&#13;";
	
	/** The Constant LFCR_XML_CODE. */
	private static final String LFCR_XML_CODE = "&#10;&#13;";
	
	/** The Constant XML_HEADER. */
	private static final String XML_HEADER = "<?xml version='1.0' encoding='utf-8' ?>"; 
	
	// Delimiter characters used for parsing display field strings
	private static final String FIELD_DELIMITER = "\\|";
	private static final String FIELD_TEXT_DELIMITER = "_";

	
	/**
	 * Instantiates a new help bean.
	 * 
	 * @param config the config
	 * @param xmlTemplates the xml templates
	 * @param clientIP the client IP
	 * @param request the request
	 * 
	 * @throws TBrowserException the t browser exception
	 * @throws SecurityViolationException 
	 */
	public HelpBean( ServletConfig config, XMLTemplateManager xmlTemplates, String clientIP, HttpServletRequest request ) throws TBrowserException, SecurityViolationException
	{
		super( config, xmlTemplates, clientIP, request );

		setRequestToken( "" );
		LOGGER.info("Constructor");
		
		// Read the help parameters file to get the help locations and languages
		readHelpParameters();
		buildLanguagesXml();
	}
	
    /* (non-Javadoc)
     * @see com.temenos.t24browser.beans.TemenosBean#getResponseType()
     */
    public String getResponseType() {
        // Return the string type directly, based on the population of the corresponding ivResponseXXX 
        // otherwise revert to parent logic
        //
        // This has been overridden as the default parent logic doesn't work when
        // the help response is dependent on availability of the right type of file on the system
        // and not on the state of the ivTransform variable (see TemenosBean)
        //
        //So, for help files:
        //    if a HTML help file is found, then this is returned
        //    if an XML file is found, and transformed at the server, then "HTML" is the return type
        //    if an XML file is found, and no transformation at the server is turned off, then there will
        //       be no HTML in the response, so "XML" will be returned
        
        if ( ! ivResponseHtml.equals("") )
        {
            return ("HTML");
        }
        else if( ! ivResponseXml.equals("") )
        {
            return("XML");
        }
        else
        {
            return super.getResponseType();
        }
    }


    /* (non-Javadoc)
     * @see com.temenos.t24browser.beans.TemenosBean#getResponse()
     */
    public String getResponse()
    {   
        // Return HTML or XML response based on overridden getResponseType(),
        // otherwise revert to parent logic
        String helpResponseType = getResponseType();
        
        if ( helpResponseType.equals("HTML") )
        {
            return( ivResponseHtml );
        }
        else if( helpResponseType.equals("XML") )
        {
            return (ivResponseXml);
        }
        else
        {
            return super.getResponse();
        }
    }
    
    

    // Process a help request
	/**
     * Process request.
     */
    public void processRequest()
	{
		ivResponseHtml = "";
		ivResponseXml = "";
		ivResponseUrl = "";
		ivStyleSheet = "";
		ivSavedHelpXml = "";
		ivMissingPaths = new ArrayList();
			
		ivRequest.setAttribute( "rootPath", ivServletContextPath );
		
		// Process the Http Servlet Request
		ivLanguage = getLanguage();
		ivProduct = ivRequest.getParameter("product");
		ivApplication = ivRequest.getParameter("application");
		ivField = ivRequest.getParameter("field");
		ivPage = ivRequest.getParameter("page");
		ivDesktop = ivRequest.getParameter("desktop");
		ivPath = ivRequest.getParameter("path");
		ivItem = ivRequest.getParameter("item");
		ivDisplayFields = ivRequest.getParameter("displayfields");
		ivOverviewRequired = overviewRequired();
		ivFieldsRequired = fieldsRequired();
		ivMenuRequired = menuRequired();
		
		LOGGER.debug("Using Language : " + ivLanguage);
		
		if(ivField != null)
		{
			if(ivField.equals(ivApplication) || ivField.equals(""))
			{
				ivField = "OVERVIEW";	
			}
		}
		ivCommand = getCommand();
		
		String product = getProductName();
		
		if ( product.equals("ARC-IB") && !ivApplication.startsWith("/"))
		{
			// We don't display the normal Browser HelpTExt for ARC-IB, so just display a missing page that
			// can be customised by the client
			displayMissingPage();
		}
		else
		{
			if ( ( ivCommand != null ) && ( !ivCommand.equals("") ) )
			{
				if ( ivCommand.equals("view") )
				{
					processView();
				}
				else if ( ivCommand.equals("view.common") )
				{
					processViewCommonFields();
				}
				else if ( ivCommand.equals("edit") )
				{
					processEdit();
				}
				else if ( ivCommand.equals("save") )
				{
					processSave();
				}
				else if ( ivCommand.equals("create") )
				{
					processCreate();
				}
				else if ( ivCommand.equals("update.fields") )
				{
					processUpdateFields();
				}
				else
				{
					// Build the error result in HTML
					buildErrorResponse( "Invalid Command passed " + ivCommand);
				}
			}
			else if ( ( ivPage != null ) && ( ! ivPage.equals("") ) )		
			{
				processViewPage();
			}
			else if ( ( ivDesktop != null ) && ( ! ivDesktop.equals("") ) )		
			{
				processViewDesktopPage();
			}
			else
			{
				// Build the error result in HTML
				buildErrorResponse( "Invalid Command passed " + ivCommand);
			}
		}
	}


	// Read the help parameters from the XML file helpParameters.xml
	/**
	 * Read help parameters.
	 */
	private void readHelpParameters()
	{
		String contextPath = ivServletContext.getRealPath("");
		ArrayList aViewLocations;
		ArrayList aEditLocations;
		ArrayList aLanguages;
		ivPagesEditable = false;
		
		try
    	{
			XMLRequestManager xmlManager = new XMLRequestManager(contextPath, "HELP.PARAMETERS", 
					ivServletConfig.getServletContext().getInitParameter(HelpServlet.HELP_PARAMETERS_INIT_PARAM));
			aViewLocations = xmlManager.getViewHelpLocationsTable();
			aEditLocations = xmlManager.getEditHelpLocationsTable();
			aLanguages = xmlManager.getHelpLanguagesTable();
			ivImagesDir = xmlManager.getHelpImagesDirectory();
			ivPagesEditable = xmlManager.helpPagesEditable();
			ivDevelopmentMode = xmlManager.helpDevelopmentMode();
		}
	    catch ( Exception e )
	    {
	    	String sError = "Error reading help parameters - " + e.getMessage();
	    	LOGGER.error(sError);

			// File not found or other file problem, so create list with default settings
			String pathSeparator = System.getProperty("file.separator");
			aViewLocations = new ArrayList();
			aViewLocations.add( ivServletContext.getRealPath("") + pathSeparator + "help" + pathSeparator );
			aEditLocations = new ArrayList();
			aEditLocations.add( ivServletContext.getRealPath("") + pathSeparator + "help" + pathSeparator );
			ivImagesDir = DEFAULT_IMAGES_DIR;			
			aLanguages = new ArrayList();
			aLanguages.add( DEFAULTLANG );
	    }
	    
	    ivViewLocations = aViewLocations.toArray();
   	    ivEditLocations = aEditLocations.toArray();
   	    ivHelpLanguages = aLanguages.toArray();
 
   	    if ( ( ivImagesDir == null ) || ( ivImagesDir.equals("") ) )
   	    {
   	    	ivImagesDir = DEFAULT_IMAGES_DIR;
   	    }
	}


	// Process a view help page command
	/**
	 * Process view.
	 */
	private void processView()
	{	
		processView( ivItem );
	}
	
	
	// Process a view common fields help page command
	/**
	 * Process view common fields.
	 */
	private void processViewCommonFields()
	{	
		ivCommonsView = true;
		processView( COMMON_FIELDS_PAGE );
	}
	

	// Process a view help page command
	/**
	 * Process view page.
	 */
	private void processViewPage()
	{	
		processView( ivPage );
	}
	
	
	// Process a view help page command from Desktop
	/**
	 * Process view desktop page.
	 */
	private void processViewDesktopPage()
	{
		// Strip off the leading "\" as Desktop adds this automatically
		if ( ivDesktop.startsWith("\\") )
		{
			ivDesktop = ivDesktop.substring( 1, ivDesktop.length() );
		}
		
		// Check to see if the desktop page contains a field name (i.e. after an "_" character)
		String sPage = ivDesktop;
		int iFieldPos = ivDesktop.indexOf("_");
		
		if ( iFieldPos != -1 )
		{
			sPage = ivDesktop.substring( 0, iFieldPos );
			ivField = ivDesktop.substring( iFieldPos + 1, ivDesktop.length() );
			
			// See if there was a file suffix - such as .htm - on the page name
			int iSuffixPos = ivField.lastIndexOf( "." );
			
			if ( iSuffixPos > 0 )
			{
				// Remove the suffix
				ivField = ivField.substring( 0, iSuffixPos ); 
			}
		}
		
		processView( sPage );
	}
	
	
	/**
	 * Process view.
	 * 
	 * @param item the item
	 */
	private void processView( String item )
	{
		ivStyleSheet = VIEW_STYLESHEET;
		
		if (item != null) 
		{
			buildMainPage( ivViewLocations, item );
		} 
		else 
		{
			buildMainPage( ivViewLocations, ivProduct, ivApplication );
		}

		if ( ( ivMissing ) && ( ivDesktop != null ) && ( ! ivDesktop.equals("") ) )
		{
			// Re-set the state information and recurse down all sub-directories looking 
			// for the help file as we might not have a product specified for Desktop file
			ivMissing = false;
			ivResponseXml = "";
			ivResponseHtml = "";
			
			getAllDirsViewLocations();

			if (item != null) 
			{
				buildMainPage( ivViewLocations, item );
			} 
			else 
			{
				buildMainPage( ivViewLocations, ivProduct, ivApplication );
			}
		}
		
		if ( ivMissing == false)
		{
			// Check the file found is within the View Locations
			if ( validViewFile() )
			{
				// If pages can be edited and are in XML the add an Edit button
				// Don't allow Desktop users to edit pages as we don't have a product or application
				if ( ( ivPagesEditable ) && ( !ivCommonsView ) && 
				     ( ivDesktop == null ) && ( ! (ivResponseXml.equals("") ) ) )
				{
					// Add an Edit button to the XML as a tool
					addEditTool();
				}
				
				// If a field was specified then add the onload event
				if ( ( ivField != null ) && ( ! (ivField.equals("") ) ) )
				{
					// Add an onload event
					addFieldEvent();
				}
				
				// Indicate whether common fields are being displayed so 
				// that the hyperlink is only displayed when relevant
				if ( ! ivCommonsView )
				{
					addCommonVariable();
				}
				
				// Add the images directory
				addImagesDirectory();
				
				// Check whether the overview section should be removed or not
				if ( !ivOverviewRequired )
				{
					ivResponseXml = Utils.removeNodeFromString( ivResponseXml, XMLConstants.XML_HELPTEXT_OVERVIEW_TAG );
				}
				
				// Check whether we have field prompts and an ordered list in the request
				if ( ( ivDisplayFields != null ) && ( ! (ivDisplayFields.equals("") ) ) )
				{
					addSortAttribute( "false" );
					selectRequiredFields();
				}
				else
				{
					addSortAttribute( "true" );
				}
				
				// Check whether the menu should be displayed or not
				// Don't remove the menu XML as it is needed for the fields, so just add an
				// attribute that will be used by the XSL to remove its display
				if ( ivMenuRequired )
				{
					addDisplayMenuAttribute( "true" );
				}
				else
				{
					addDisplayMenuAttribute( "false" );
				}
				
				// Check whether the fields are required or not
				if ( !ivFieldsRequired )
				{
					ivResponseXml = Utils.removeNodeFromString( ivResponseXml, XMLConstants.XML_HELPTEXT_MENU_TAG );
				}
				
				if ( ! ( ivResponseXml.equals("") ) )
				{
					// Transform XML with stylesheet to HTML
					setHomeDirectory( ivStyleSheet );
					transformResult( ivStyleSheet, ivResponseXml );
				}
			}
			else
			{
				// Invalid file path
				displayUnavailablePage();
			}
		}
	}
	

	// Process an edit help page command
	/**
	 * Process edit.
	 */
	private void processEdit()	
	{
		ivStyleSheet = EDIT_STYLESHEET;
		editHelpFile();
		
		if ( ! (ivResponseXml.equals(""))  )
		{
			// Transform XML with stylesheet to HTML
			setHomeDirectory( ivStyleSheet );
			transformResult( ivStyleSheet, ivResponseXml );
		}
	}
	
	
	// Process a save help page command
	/**
	 * Process save.
	 */
	private void processSave()
	{
		try
		{
			saveHelpFile( ivPath );
			buildMessageResponse( "T24 Message", "Help file saved successfully to " + ivPath );
		}
		catch ( Exception e )
		{
			// Build the error result in HTML
			buildErrorResponse( e.getMessage() );
		}
	}
	
	
	// Process a create help page command
	/**
	 * Process create.
	 */
	private void processCreate()
	{
		// Get the name of the file to create
		String fileName = ivRequest.getParameter("path");
		
		try
		{
			createHelpFile( fileName );

			if ( ! (ivResponseXml.equals(""))  )
			{
				ivStyleSheet = EDIT_STYLESHEET;
				
				// Transform XML with stylesheet to HTML
				setHomeDirectory( ivStyleSheet );
				transformResult( ivStyleSheet, ivResponseXml );
			}
		}
		catch ( Exception e )
		{
			// Build the error result in HTML
			buildErrorResponse( e.getMessage() );
		}
	}
		
		
	// Save a Help File in XML format
	/**
	 * Save help file.
	 * 
	 * @param fileName the file name
	 * 
	 * @throws HelpSaveException the help save exception
	 */
	public void saveHelpFile( String fileName ) 
		throws HelpSaveException
	{
		// Check if the Help Parameters allow saving of Help files
		if ( ! ivPagesEditable )
		{
			throw new HelpSaveException( "Saving of Help files not allowed" );
		}		
		
		// Check if a file name was supplied
		if ( fileName == null )
		{
			throw new HelpSaveException( "Invalid file name for Help file - unable to save to : " + fileName );  
		}
		
		// Check if the path for the save is within the edit locations parameter
		int pathPointer = 0;
		int locationsNo = ivEditLocations.length;
		String sPath = "";
		boolean matchFound = false;
		
		// Convert all paths using the File class to ensure the file separators match in both
		File saveFile = new File ( fileName );
		String saveFilePath = saveFile.getPath();
		
		while ( ( matchFound == false ) && ( pathPointer < locationsNo ) )
		{
			File editPath = new File ( (String) ivEditLocations[pathPointer] );
			sPath = editPath.getPath();
			
			if ( saveFilePath.startsWith( sPath ) )
			{
				matchFound = true;
			}
			else
			{
				pathPointer++;
			}
		}
		
		if ( matchFound == false )
		{
			throw new HelpSaveException( "Invalid file name for Help file - unable to save to : " + fileName );
		}

		// Convert the request help page in to an XML document and save it to a file
		XMLRequestManager xmlManager = null;
		
		try
		{
			xmlManager = new XMLRequestManager( ivRequest, getXmlTemplates() );
		}
		catch ( XMLRequestTypeException e )
		{
			throw new HelpSaveException( "Error saving Help file : Invalid requestType used for Parser" );
		}
	
		String xmlHelpPage = xmlManager.getXMLResponse();

		// Convert any Microsoft Word style double and single quotes
		xmlHelpPage = convertQuotes( xmlHelpPage );
		
		// Convert carriage return and line feeds to separate <p> tags
		xmlHelpPage = convertNewLinesToParas( xmlHelpPage );

		try
		{
			Writer out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( fileName ), "UTF-8" ) );
			out.write( xmlHelpPage );
			out.close();

			// Save the XML that we saved in case it is needed (i.e. for manipulation later)
			ivSavedHelpXml = xmlHelpPage;
		}
		catch (Exception e)
		{
			throw new HelpSaveException( "Error saving Help file to " + fileName );
		}
	}
	
	
	// Create a Help File in XML format with the specified file name
	/**
	 * Creates the help file.
	 * 
	 * @param fileName the file name
	 * 
	 * @throws HelpSaveException the help save exception
	 */
	public void createHelpFile( String fileName ) 
		throws HelpSaveException
	{
		if ( fileName == null )
		{
			throw new HelpSaveException( "No file name specified for Help file" );  
		}
		else
		{
			ivPathUsed = fileName;
			ivResponseXml = "";
			ivResponseXml = "<t24help>";
			ivResponseXml += "<header><product>" + ivProduct + "</product>";
			ivResponseXml += "<application>" + ivApplication + "</application>";
			ivResponseXml += "<user>" + getUserId() + "</user>";
			ivResponseXml += "<title>" + ivApplication + "</title>";
			ivResponseXml += "<application>" + ivApplication + "</application>";
			ivResponseXml += "<table>" + ivApplication + "</table></header>";
			ivResponseXml += "<overview/></t24help>";
			
			// Now add the edit buttons and header info
			addEditToolbarButtons();
		}
	}

	
	// Process a update fields command
	/**
	 * Process update fields.
	 */
	private void processUpdateFields()
	{
		// Save the current document in case it has been updated, then
		// get the list of application fields from T24.
		// Then merge the 2 XML documents - update the XML with any new fields 
		// and remove those no longer in the application.
		
		// Get the name of the file to write to
		String fileName = ivRequest.getParameter("path");
		
		try
		{
			saveHelpFile( fileName );
			ivPathUsed = fileName;
			
			// Get a list of the application fields from T24 so that the page can be updated
			String sXmlRequest = getFieldListRequestXml();
			
			if ( processOfsRequest( sXmlRequest ) )
			{
				processOfsFieldsResponse();
			}
			else
			{
				buildErrorResponse( "Saved Help file, but unable to update list of fields" );
			}
		}
		catch ( Exception e )
		{
			// Build the error result in HTML
			buildErrorResponse( e.getMessage() );
		}
	}
	
	
	// Build XML containing the list of languages supported
	/**
	 * Builds the languages xml.
	 */
	private void buildLanguagesXml()
	{
		ivAllowedLanguages = "<langs>";
		
		for( int i=0; i < ivHelpLanguages.length; i++ )
		{
			ivAllowedLanguages  = ivAllowedLanguages + "<lang>" + (String) ivHelpLanguages[i] + "</lang>";
		} 
		
		ivAllowedLanguages  = ivAllowedLanguages + "</langs>";
	}
	
		
	// Build a page with the missing list of paths
	/**
	 * Builds the missing page.
	 */
	private void buildMissingPage()
	{
		ivMissing = true;
		String sSkinName = ivRequest.getParameter( "skin" );
		if ( ( sSkinName == null ) || ( sSkinName.equals("") ) )
		{
			sSkinName = "default";
		}
		
		String missingXml = "<t24help><header><product>" + ivProduct + "</product>";
		missingXml += "<application>" + ivApplication + "</application>";
		missingXml += "<user>" + getUserId() + "</user>";
		missingXml += "<language>" + ivLanguage + "</language></header>";
		missingXml += "<userDetails><skin>" + sSkinName + "</skin></userDetails><messages>";
		missingXml += "<title>Missing Help File</title>";
		missingXml += "<msgHeader>Cannot find the Help file in the Help parameter path !</msgHeader>";
		missingXml += "<msgFooter><link>";
		missingXml += "<text>For general T24 Help click on the link below :</text>";
		missingXml += "<script>javascript:doutil('T24.HELP')</script>";
		missingXml += "<caption>T24 Help</caption>";
		missingXml += "</link></msgFooter></messages></t24help>";

		// Transform XML with stylesheet to HTML
		setHomeDirectory( MISSING_STYLESHEET );
		transformResult( MISSING_STYLESHEET, missingXml );
	}
	
	
	// Find a relevant help page and load it (XML or HTML)
	/**
	 * Builds the main page.
	 * 
	 * @param aLocations the a locations
	 * @param product the product
	 * @param table the table
	 */
	private void buildMainPage( Object[] aLocations, String product, String table) 
	{
		String pathSeparator = System.getProperty("file.separator");
		String sPath = product + pathSeparator + table;
		buildMainPage(aLocations, sPath);
	}
	
	
	// Find a relevant help page and load it (XML or HTML)
	/**
	 * Builds the main page.
	 * 
	 * @param aLocations the a locations
	 * @param item the item
	 */
	private void buildMainPage(Object[] aLocations, String item)
	{
		/* 
		 * Now we try and load up the help page. We use the paths defined in the XML config in order
		 * and then we try to find the most relevant page (core or local / customised)
		 */
		String sPath;
		int pathPointer = 0;
		int locationsNo = aLocations.length;
		String pathsep = System.getProperty("file.separator");
		
		do
		{
			// Remove a file extension if it exists as we want to search for all types
			int iSuffixPos = item.lastIndexOf( "." );
			
			if ( iSuffixPos > 0 )
			{
				String sPrefix = item.substring( 0, iSuffixPos );
				String sSuffix = item.substring( iSuffixPos + 1, item.length() );
		
				if ( ( sSuffix.equals("xml") ) || ( sSuffix.equals("html") ) || ( sSuffix.equals("htm") ) )
				{
					item = sPrefix;
				}
			}
			
			
			sPath = "";
			
			// Lang specific xml
			sPath = (String) aLocations[pathPointer] + item + "_" + ivLanguage + ".xml";
			if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
			
			// Lang specific html			
			sPath = (String) aLocations[pathPointer] + item + "_" + ivLanguage + ".html";
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
			
			// Lang specific htm
			sPath = (String) aLocations[pathPointer] + item + "_" + ivLanguage + ".htm";
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
			
			// English xml			
			sPath = (String) aLocations[pathPointer] + item + ".xml";
			if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
			
			// English html
			sPath = (String) aLocations[pathPointer] + item + ".html";
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
			
			// English html
			if (ivField != null && ivApplication != null)
			{
				String htmlApp = ivApplication.replace(".", "_");
				String Htmlitem = ivProduct + pathsep + htmlApp;
				sPath = (String) aLocations[pathPointer] + Htmlitem + pathsep + ivApplication + "-" + ivField + ".html";
			}
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
			
			//common html
			String htmlComm = "COMMON_FIELDS\\";
			sPath = (String) aLocations[pathPointer] + htmlComm + "COMMON.FIELDS" + "-" + ivField + ".html";
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
			
			// English htm
			sPath = (String) aLocations[pathPointer] + item + ".htm";
			if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;

			pathPointer++;
			
		} while (pathPointer < locationsNo);
		
		// If we get this far then we have been unable to locate the help page, so return the
		// paths that we have tried to find the item in.

   		boolean suppressMissingHelp = DebugUtils.getBooleanProperty(ivParameters, 
   				DebugUtils.SUPPRESS_MISSING_HELP_NAME);
        
        if (suppressMissingHelp) {
            // try to find a default help page for the requested 'category'
            // note: the final part of the help category is given as /<context>/<helpItemName> 
          
             
                
                // Language- (or rather region-) specific XML/HTML
                sPath = (String) aLocations[pathPointer - 2] + "default_" + ivLanguage + ".xml";
                if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 2] + "default_" + ivLanguage + ".html";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 2] + "default_" + ivLanguage + ".htm";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                // .. or default English xml/html
                sPath = (String) aLocations[pathPointer - 2] + "default.xml";
                if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 2] + "default.html";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 2] + "default.htm";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
            
                 // Language- (or rather region-) specific XML/HTML
                sPath = (String) aLocations[pathPointer - 1] + "default_" + ivLanguage + ".xml";
                if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 1] + "default_" + ivLanguage + ".html";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 1] + "default_" + ivLanguage + ".htm";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                // .. or default English xml/html
                sPath = (String) aLocations[pathPointer - 1] + "default.xml";
                if ( tryHelpTextFile( sPath, XML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 1] + "default.html";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;
                sPath = (String) aLocations[pathPointer - 1] + "default.htm";
                if ( tryHelpTextFile( sPath, HTML_TYPE ) ) return;

			 // otherwise, if suppressing then respond with empty HTML
				ivResponseHtml = "<html/>";
				return;
        }	

        buildMissingPage();
	}


	// Try to read the item from the given path. If it cannot be found an IO exception will be thrown.
	// Otherwise returns a String of the XML document
	/**
	 * Try path.
	 * 
	 * @param sPath the s path
	 * 
	 * @return the string
	 * 
	 * @throws Exception the exception
	 */
	private String tryPath(String sPath) throws Exception
	{
		String sXml = "";

		StringBuffer buf = new StringBuffer();
		File f=new File(sPath);
		BufferedReader i=new BufferedReader( new InputStreamReader( new FileInputStream(f),"UTF-8" ) );
		String s = i.readLine();
		
		while( s != null )
		{
			buf.append(s);
			s = i.readLine();
		}

		sXml = buf.toString().trim();

		// Convert any quote characters
		sXml = convertQuotes( sXml );
		
		// Clean the XML - remove any eroneous characters and add the correct XML header
		sXml = cleanHelpText( sXml );
		
		ivPathUsed = sPath; // Where we found the item, so we can use it to save it back to!

		return (sXml);
	}
	
	// Try to read the item from the given path. If it cannot be found an IO exception will be thrown.
	// Otherwise returns a String of the XML document	
	/**
	 * Try help text file.
	 * 
	 * @param sPath the s path
	 * @param sType the s type
	 * 
	 * @return true, if successful
	 */
	private boolean tryHelpTextFile( String sPath, String sType )
	{
		try 
		{
			String sHelp = tryPath(sPath);
			
			if ( sType.equals( XML_TYPE ) )
			{
				ivResponseXml = sHelp;
			}
			else if ( sType.equals( HTML_TYPE ) )
			{
				ivResponseHtml = sHelp;
			}
			else
			{
				System.out.println( "Invalid file type" );
			}
			
			return( true );
		} 
		catch (Exception e) 
		{
			// Error reading file so ignore, add path searched to the missing message
			ivMissingPaths.add( sPath );
			return( false );
		}
	}
	
	// Edit a help file
	/**
	 * Edits the help file.
	 */
	private void editHelpFile()
	{
		if (ivItem != null) 
		{
			buildMainPage( ivEditLocations, ivItem );
		} 
		else 
		{
			buildMainPage( ivEditLocations, ivProduct, ivApplication );
		}
		
		if ( ! (ivResponseXml.equals("") ) )
		{
			// Convert helptext <p> tags in to new lines for display in HTML textareas
			ivResponseXml = convertParasToNewLines( ivResponseXml );
			addEditToolbarButtons();
		}
	}
		
	
	// Add edit button tool to XML document and the path of the XML file
	/**
	 * Adds the edit tool.
	 */
	private void addEditTool()
	{
		addEditTool( true, "Edit Page" );
	}
	
	// Add edit button tool to XML document and the path of the XML file
	/**
	 * Adds the edit tool.
	 * 
	 * @param enabled the enabled
	 * @param tooltip the tooltip
	 */
	private void addEditTool( boolean enabled, String tooltip )
	{
		String xmlEnabled = "";
		
		if ( enabled == false )
		{
			xmlEnabled = "<enabled>NO</enabled>";
		}
		
		String xmlEditTool = "<toolbars><toolbar><tool>" + xmlEnabled;
		xmlEditTool += "<tip>" + tooltip + "</tip><item>editHelp()</item><ty>JAVASCRIPT</ty><image>tools</image>";
		xmlEditTool += "<cap>Edit</cap></tool></toolbar></toolbars>";
		
		addHelpHeader( xmlEditTool );
	}
	
	
	// Add and edit toolbar to XML document
	/**
	 * Adds the edit toolbar buttons.
	 */
	private void addEditToolbarButtons()
	{
		String xmlTools = "<toolbars><toolbar>";
		xmlTools += "<tool><tip>Save Page</tip><item>saveHelp()</item><ty>JAVASCRIPT</ty><image>tools</image><cap>Save</cap></tool>";
		xmlTools += "<tool><tip>Update page with fields from T24</tip><item>updateHelpFields()</item><ty>JAVASCRIPT</ty><image>refresh</image><cap>Update Fields</cap></tool>";
		xmlTools += "</toolbar></toolbars>";

		addHelpHeader( xmlTools );		
	}
	
	
	// Add an onload event to jump to the field
	/**
	 * Adds the field event.
	 */
	private void addFieldEvent()
	{
		String xmlEvent = "<init>javascript:gotoField('" + ivField + "')</init>";
		
		addHelpHeader( xmlEvent );
	}
	
	
	// Add an indicator that we are viewing the common fields page
	/**
	 * Adds the common variable.
	 */
	private void addCommonVariable()
	{
		String xmlCommon = "<common>no</common>";
		
		addHelpHeader( xmlCommon );
	}
	
	
	// Add the images directory
	/**
	 * Adds the images directory.
	 */
	private void addImagesDirectory()
	{
		String xmlImagesDir = "<imagesDir>" + ivImagesDir + "</imagesDir>";
		
		addHelpHeader( xmlImagesDir );
	}

	/**
	 * Adds sort attribute.
	 * 
	 * @param value the value of the attribute to add
	 */
	private void addSortAttribute( String value )
	{
		addAttribute( "sort", value );
	}
	
	/**
	 * Adds menu attribute.
	 * 
	 * @param value the value of the attribute to add
	 */
	private void addDisplayMenuAttribute( String value )
	{
		addAttribute( "displaymenu", value );
	}	

	/**
	 * Adds an attribute.
	 * 
	 * @param attribute the XML attribute tag
	 * @param value the value of the attribute to add
	 */
	private void addAttribute( String attribute, String value )
	{
		String xml = "<" + attribute + ">" + value + "</" + attribute + ">";
		addHelpHeader( xml );
	}
	
	
	/**
	 * Add help information to the header section of the XML document
	 * 
	 * @param helpHeader the help header
	 */
	private void addHelpHeader( String helpHeader )
	{
		// Add the info to the header of the document
		if ( ! ivResponseXml.equals("") )
		{
			int headerPos = ivResponseXml.indexOf("<header>");
			
			if ( headerPos != -1 )
			{
				int endHeaderPos = headerPos + 8;
				String xmlPath = "<path>" + ivPathUsed + "</path>";
				String xmlItem = "<item>" + ivItem + "</item>";
				String xmlProduct = "<product>" + ivProduct + "</product>";
				String xmlApplication = "<application>" + ivApplication + "</application>";
				String xmlLanguage = "<language>" + ivLanguage + "</language>";
				String xmlUser = "<user>" + getUserId() + "</user>";
				String xmlInfo = xmlPath + xmlItem + xmlProduct + xmlApplication + xmlLanguage + xmlUser + helpHeader;
				
				// Now add the tool, path to the XML page, and language
				ivResponseXml = ivResponseXml.substring(0, endHeaderPos) + xmlInfo + ivResponseXml.substring(endHeaderPos, ivResponseXml.length());				
			}
		}	
	}
	
	
	/**
	 * Gets the field list request xml.
	 * 
	 * @return the field list request xml
	 */
	private String getFieldListRequestXml()
	{
		String dMode = "DEV.MODE.OFF";
		if (ivDevelopmentMode)
		{
			dMode = "DEV.MODE.ON";	
		}
		ivApplication = ivRequest.getParameter("table");
		String sToken = getRequestToken();
		String xmlRequest = "<?xml version='1.0' encoding='UTF-8'?>";
		xmlRequest += "<ofsSessionRequest><requestType>UTILITY.ROUTINE</requestType>";
		xmlRequest += "<token>" + sToken + "</token>";
		xmlRequest += "<requestArguments><routineName>OS.GET.FIELD.NAMES.XML</routineName>";
		xmlRequest += "<routineArgs>" + ivApplication + ":" + dMode + "</routineArgs>";
		xmlRequest += "</requestArguments></ofsSessionRequest>";
		
		return( xmlRequest );
	}
	
	// Convert carriage return and line feed codes in to new <p> tags
	/**
	 * Convert new lines to paras.
	 * 
	 * @param sXml the s xml
	 * 
	 * @return the string
	 */
	private static String convertNewLinesToParas( String sXml )
	{
		String sXmlResult = replaceAll( sXml, CR_XML_CODE, LFCR_XML_CODE );

		XMLHelpManager helpManager = new XMLHelpManager( sXml );
		helpManager.processMultiLineNodes( "SPLIT" );
		helpManager.prepareXMLResult();
		helpManager.prettyPrintXML();
		sXmlResult = helpManager.getHelpTextXml();

		return( sXmlResult );
	}
	
	
	// Convert <p> tags to carriage return and line feed codes
	/**
	 * Convert paras to new lines.
	 * 
	 * @param sXml the s xml
	 * 
	 * @return the string
	 */
	private static String convertParasToNewLines( String sXml )
	{
		String sXmlResult = sXml;
		
		XMLHelpManager helpManager = new XMLHelpManager( sXml );
		helpManager.processMultiLineNodes( "JOIN" );
		helpManager.prepareXMLResult();
		sXmlResult = helpManager.getHelpTextXml();
		
		return( sXmlResult );
	}
	
	
	// Convert Word style quote charaters
	/**
	 * Convert quotes.
	 * 
	 * @param sXml the s xml
	 * 
	 * @return the string
	 */
	private static String convertQuotes( String sXml )
	{
		String sXmlResult = sXml;

		// Change single open quote
		sXmlResult = Utils.replaceAll( sXmlResult, "â&#128;&#152;", "&#39;" );
		
		// Change single open quote
		sXmlResult = Utils.replaceAll( sXmlResult, "&amp;#147;", "&#39;" );

		// Change single close quote
		sXmlResult = Utils.replaceAll( sXmlResult, "â&#128;&#153;", "&#39;" );
		
		// Change single close quote
		sXmlResult = Utils.replaceAll( sXmlResult, "&amp;#148;", "&#39;" );

		// Change double open quote
		sXmlResult = Utils.replaceAll( sXmlResult, "â&#128;&#156;", "&quot;" );

		// Change double close quote
		sXmlResult = Utils.replaceAll( sXmlResult, "â&#128;&#157;", "&quot;" );

		return( sXmlResult );
	}
	
	
	// Send a request to server
	/**
	 * Process ofs request.
	 * 
	 * @param sXmlRequest the s xml request
	 * 
	 * @return true, if successful
	 */
	private boolean processOfsRequest( String sXmlRequest )
	{
		BrowserResponse browserResult = sendOfsRequestToServer( sXmlRequest );

		if ( browserResult != null )
		{
			if ( browserResult.isValid() )
			{
				ivResponseXml = browserResult.getMsg();
				LOGGER.debug("OFS Request XML Response: " + ivResponseXml);
	   			
				return( true );
			}
			else
			{
				return( false );
			}
		}
		else
		{
			return( false );
		}
	}
	
	
	// Process the response containing a list of fields
	/**
	 * Process ofs fields response.
	 */
	private void processOfsFieldsResponse()
	{
		// The response from a get fields request could contain the list
		// of fields, or some sort of error.  
		// If it's a list of fields then manipulate the current page to include the fields
		// Otherwise, an error probably occurred so transform the result
		
		XMLRequestManager xmlManager = new XMLRequestManager();

		try
		{
			// Save the token returned from T24
			String sToken = xmlManager.getNodeValue( ivResponseXml, "token" );
			saveToken( sToken );
			String sFldsXml = xmlManager.getNodeValue( ivResponseXml, "flds" );

			if ( ( sFldsXml != null ) && ( ! sFldsXml.equals("") ) )
			{
				// There is at least one field in the response so process them
				ivResponseXml = applyFieldListChanges( ivResponseXml, ivSavedHelpXml );

				// Convert helptext <p> tags in to new lines for display in HTML textareas
				ivResponseXml = convertParasToNewLines( ivResponseXml );

				addEditToolbarButtons();
				setHomeDirectory( EDIT_STYLESHEET );
				transformResult( EDIT_STYLESHEET, ivResponseXml );
			}
			else
			{
				// Check if there was an error
				String sResponseType = xmlManager.getNodeValue( ivResponseXml, "responseType" );

				if ( sResponseType.equals( "XML.FIELDS" ) )
				{
					// It worked but there are no fields for that application
					buildErrorResponse( "Application has no fields in T24" );
				}
				else
				{
					// Possibly a general error so get the stylesheet name in 
					// the response and transform the result accordingly
					processOfsResponse( ivResponseXml, xmlManager );
				}
			}
		}
		catch(XMLRequestManagerException e)
		{
			return;
		}
		
	}
	
	
	// Save the token from the response Xml in to the session
	/**
	 * Save token.
	 * 
	 * @param sToken the s token
	 */
	private void saveToken( String sToken )
	{
		// Save new token in the session
		// If the token is empty, then there was probably an error at the server - in
		// this case keep hold of the old token as we can try to use it on the next request.
		// If the session was invalidated (i.e. due to a logout) then no need to store the token

		try
		{		
			if ( ( sToken == null ) || ( sToken.equals( "" ) ) )
			{
				// Keep the old token for our next request - it might still be valid
			}
			else
			{
				ivSession.setAttribute( TOKEN_SESSION_NAME, sToken );
			}
		}
		catch ( IllegalStateException ise )
		{
			// Invalidated session - ignore the exception
		}
	}
	
	
	// Apply any field changes to the XML help page
	/**
	 * Apply field list changes.
	 * 
	 * @param sFieldsXml the s fields xml
	 * @param sHelpXml the s help xml
	 * 
	 * @return the string
	 */
	private String applyFieldListChanges( String sFieldsXml, String sHelpXml )
	{
		// If there are any fields in the field list that aren't in the help page, then add them.
		// If there are any fields in the help page that aren't in the field list, then delete them.
		// This is done by the XMLHelpManager parser using DOM objects
		XMLHelpManager helpManager = new XMLHelpManager( sHelpXml );
		helpManager.setXMLFieldList( sFieldsXml );
		helpManager.processHelpTextFields();
		helpManager.prepareXMLResult();
		String sXmlResult = helpManager.getHelpTextXml();
		
		return( sXmlResult );
	}
	
	
	// Expands the list of view locations to include all subdirctories
	/**
	 * Gets the all dirs view locations.
	 * 
	 * @return the all dirs view locations
	 */
	private void getAllDirsViewLocations()
	{
		int iLocationsNo = ivViewLocations.length;
		ArrayList aNewLocations = new ArrayList();
		String pathSeparator = System.getProperty("file.separator");
				
		for ( int iLocationCount = 0; iLocationCount < iLocationsNo; iLocationCount++ )
		{
			String sDirName = (String) ivViewLocations[iLocationCount];
			File dirFile = new File( sDirName );
	
			String[] contents = dirFile.list();
	
			if ( contents != null )
			{
				// Loop through files - if it is a directory then add it to the view list
				for (int i = 0; i < contents.length; i++) 
				{
			         // If the new item is a directory recurse, other check if it is an XML file
					String sFileName = contents[i];
					File child = new File( dirFile, sFileName );
					sFileName = child.getAbsolutePath();
		         
					if ( child.isDirectory() )
					{
						if ( ! sFileName.endsWith( pathSeparator ) )
						{
							sFileName += pathSeparator;
						}
						aNewLocations.add( sFileName );
					}
				}
			}
		}
		
		// If we found any new locations then add them to the standard list
		if ( aNewLocations.size() > 0 )
		{
			ArrayList aFullLocations = new ArrayList();
			
			// Add the existing locations to the new array
			for ( int i = 0; i < ivViewLocations.length; i++ )
			{
				aFullLocations.add( ivViewLocations[i] );
			}
			
			// Add the new locations to the new array			
			for ( int i = 0; i < aNewLocations.size(); i++ )
			{
				aFullLocations.add( aNewLocations.get(i) );
			}
			
			ivViewLocations = null;
			ivViewLocations = aFullLocations.toArray();
		}
	}
	
	/**
	 * Clean the HelpText of any eroneous characters
	 * 
	 * @param sHelpText the cleaned help text
	 * 
	 * @return the string
	 */
	private String cleanHelpText( String sHelpText )
	{
		// Extract the main 't24help' node
		int startPos = sHelpText.indexOf( "<t24help" );
		int endPos = sHelpText.indexOf( "</t24help>") + 10;
		
		if ( startPos != -1 )
		{
			sHelpText = sHelpText.substring( startPos, endPos );

			// Add the XML header
			sHelpText = XML_HEADER + sHelpText;			
		}
		
		return( sHelpText );
	}

	private void displayMissingPage()
	{
		String sFileSeparator = System.getProperty("file.separator");
		String sFileName = ivServletContextPath + sFileSeparator + ARCIB_MISSING_PAGE;
		
		FileManager fm = new FileManager( sFileName );
		ivResponseHtml = fm.readFile();	

		if ( ivResponseHtml.equals("") )
		{
			buildErrorResponse( "Missing HelpText file : " + sFileName );
		}
	}
	
	private void displayUnavailablePage()
	{
		String sFileSeparator = System.getProperty("file.separator");
		String sFileName = ivServletContextPath + sFileSeparator + UNAVAILABLE_PAGE;
		
		FileManager fm = new FileManager( sFileName );
		ivResponseHtml = fm.readFile();	

		if ( ivResponseHtml.equals("") )
		{
			buildErrorResponse( "Invalid HelpText path : " + ivPathUsed );
		}
	}
	
	
	/**
	 * Checks to see if the overview section is required when displaying the HelpText
	 * 
	 * @return Whether the overview is required
	 */
	private boolean overviewRequired()
	{
		// Is the overview section required
		boolean required = true;
		
		String overview = ivRequest.getParameter("overview");
		
		if ( ( overview != null ) && ( !overview.equals("") ) )
		{
			if ( overview.toLowerCase().equals("no") )
			{
				required = false;
			}
		}
		
		return( required );
	}
	
	/**
	 * Checks to see if the fields are required when displaying the HelpText
	 * 
	 * @return Whether the fields are required
	 */	
	private boolean fieldsRequired()
	{
		// Are the fields required or only the overview section
		boolean required = true;
		
		String overview = ivRequest.getParameter("overview");
		
		if ( ( overview != null ) && ( !overview.equals("") ) )
		{
			if ( overview.toLowerCase().equals("only") )
			{
				required = false;
			}
		}
		
		return( required );
	}

	/**
	 * Checks to see if the menu is required when displaying the HelpText
	 * 
	 * @return Whether the menu is required
	 */	
	private boolean menuRequired()
	{
		// Is the menu required
		boolean required = true;
		
		String menu = ivRequest.getParameter("menu");
		
		if ( ( menu != null ) && ( !menu.equals("") ) )
		{
			if ( menu.toLowerCase().equals("no") )
			{
				required = false;
			}
		}
		
		// Check if only the overview is required, if so then we don't want a menu
		String overview = ivRequest.getParameter("overview");
		
		if ( ( overview != null ) && ( !overview.equals("") ) )
		{
			if ( overview.toLowerCase().equals("only") )
			{
				required = false;
			}
		}
		
		return( required );
	}
	
	/**
	 * Create a result document containing only the fields, and the prompt text, given in the request.
	 */
	private void selectRequiredFields()
	{
		// Create a new xml document with only the selected fields in the specified order
		String resultHelpText = ivResponseXml;
		
		// Remove all of the existing fields that are in the <menu> tag
		resultHelpText = Utils.removeNodeFromString( resultHelpText, XMLConstants.XML_HELPTEXT_MENU_TAG );
		
		String menuXml = XMLConstants.XML_HELPTEXT_MENU_TAGGED;
		
		// Build up a new <menu> with each required field stored in ivDisplayFields
		// Fields are in the form "<fieldName>_<fieldPrompt>|..."
		String[] fields = ivDisplayFields.split( FIELD_DELIMITER );
		
		for ( int i = 0; i < fields.length; i++ )
		{
			String field = fields[i];
			String fieldName = "";
			String fieldPrompt = "";
			
			// Check if a field prompt was supplied or not
			if ( field.endsWith( FIELD_TEXT_DELIMITER ) )
			{
				fieldName = field.substring(0, field.length()-1);
				fieldPrompt = fieldName;		// Default prompt to field name in case it's not supplied
			}
			else
			{
				String[] fieldTexts = field.split( FIELD_TEXT_DELIMITER );
				fieldName = fieldTexts[0];
				fieldPrompt = fieldTexts[1];
				
				if ( fieldPrompt.trim().equals("") )
				{
					fieldPrompt = fieldName;		// Default prompt to field name in case it's not supplied
				}
			}
				
			String menuTopic = "";
			
			// Find this field name in the HelpText and add it to the new document with the prompt text
			// Look for a "<field><fieldName>" tag and then extract the tag up to the </t> tag
			String searchField = XMLConstants.XML_HELPTEXT_FIELD_TAGGED + fieldName + XMLConstants.XML_HELPTEXT_FIELD_TAGGED_C;
			
			int fieldPos = ivResponseXml.indexOf( searchField );
			
			if ( fieldPos != -1 )
			{
				// Find the end of the topic (</t>) tag
				int endTopicPos = ivResponseXml.indexOf( XMLConstants.XML_HELPTEXT_TOPIC_TAGGED_C, fieldPos );
				
				// Build up the topic tag
				menuTopic += XMLConstants.XML_HELPTEXT_TOPIC_TAGGED;
				menuTopic += XMLConstants.XML_HELPTEXT_CAPTION_TAGGED + fieldPrompt + XMLConstants.XML_HELPTEXT_CAPTION_TAGGED_C;
				menuTopic += ivResponseXml.substring( fieldPos, endTopicPos );
				menuTopic += XMLConstants.XML_HELPTEXT_TOPIC_TAGGED_C;
				
				menuXml += menuTopic;
			}
		}
		
		menuXml += XMLConstants.XML_HELPTEXT_MENU_TAGGED_C;
		
		// Add the completed menu to the result document after the </header> tag
		menuXml = XMLConstants.XML_HELPTEXT_HEADER_TAGGED_C + menuXml;
		resultHelpText = Utils.replaceAll( resultHelpText, XMLConstants.XML_HELPTEXT_HEADER_TAGGED_C, menuXml );
		
		ivResponseXml = resultHelpText;
	}
	
	private boolean validViewFile()
	{
		// Check if the path of the view file is within the edit locations parameter
		int pathPointer = 0;
		int locationsNo = ivViewLocations.length;
		String sPath = "";
		boolean matchFound = false;
		
		try
		{
			// Convert all paths using the File class to ensure the file separators match in both
			File pathUsed = new File ( ivPathUsed );
			String viewFilePath = pathUsed.getCanonicalPath();
			
			while ( ( matchFound == false ) && ( pathPointer < locationsNo ) )
			{
				File viewPath = new File ( (String) ivViewLocations[pathPointer] );
				sPath = viewPath.getCanonicalPath();    //get the canonical path and then compare.
				
				if ( viewFilePath.startsWith( sPath ) )
				{
					matchFound = true;
				}
				else
				{
					pathPointer++;
				}
			}
		}
		catch (Exception e)
		{
			return false;
		}
		
		return matchFound;
	}
}
