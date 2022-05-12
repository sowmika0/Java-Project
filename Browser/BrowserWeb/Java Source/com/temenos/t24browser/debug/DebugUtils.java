package com.temenos.t24browser.debug;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.obfuscator.AbstractObfuscator;
import com.temenos.t24browser.obfuscator.AllTokensObfuscator;
import com.temenos.t24browser.obfuscator.CharMappingObfuscationAlgorithm;
import com.temenos.t24browser.obfuscator.ObfuscationManager;
import com.temenos.t24browser.obfuscator.Obfuscator;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.FileManager;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.xml.XMLRequestManager;
import com.temenos.t24browser.xml.XMLRequestManagerException;
import com.temenos.t24browser.xslt.XMLToHtmlBean;

// TODO: Auto-generated Javadoc
/**
 * This class implements methods to help with debugging and debug utilities.
 * 
 * @author dburford
 */
public class DebugUtils {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DebugUtils.class);

	// Properties - read from a file
	/** The Constant DEBUG_PROPERTIES_FILENAME. */
	private static final String DEBUG_PROPERTIES_FILENAME = "WEB-INF" + File.separator + "conf" + File.separator + "debug" + File.separator + "defaults.prop";
	
	/** The properties. */
	private static Properties properties = new Properties();

    // Cached XML objects
    /** The dbfactory. */
    private static DocumentBuilderFactory dbfactory;
    
    /** The tfactory. */
    private static TransformerFactory tfactory;
    
    /** The Constant PATH_SEPERATOR. */
    private static final String PATH_SEPERATOR = System.getProperty("file.separator");;
    
    /** The Constant TEMPLATE_PATH. */
    private static final String TEMPLATE_PATH = "WEB-INF" + PATH_SEPERATOR + "xml" + PATH_SEPERATOR + "test";
    
    /** The Constant PROTO_PATH. */
    private static final String PROTO_PATH = "WEB-INF" + PATH_SEPERATOR + "xml" + PATH_SEPERATOR + "proto";

    // Directory containing the test files
    /** The xml directory. */
    public static File xmlDirectory;
 
    // Directory containing the more permanent test files for rapid prototyping of UI
    // e.g. once Render XML is selected, quick updates to XSL / JS can be tested with F5
    /** The proto directory. */
    public static File protoDirectory;
 
    /** Use log4js as a logger, only for debugging. */
    public static final String LOGGING_NAME = "clientLogging";

    /** SAVE_XML determines whether xml files will be saved as they are received from T24. */
    public static boolean saveXml;
    
    /** The Constant SAVE_XML_DESC. */
    public static final String SAVE_XML_DESC = "Save XML";
    
    /** The Constant SAVE_XML_NAME. */
    public static final String SAVE_XML_NAME = "SAVE_XML";

    /** The Constant NOFRAMES_NAME. */
    public static final String NOFRAMES_NAME = "noframes";

    /** The Constant NOFRAMES_ENABLED. */
    public static final String NOFRAMES_ENABLED = "True";

    /** useNewSkin determines whether the no new arc-ib skin will be stuffed into the frames xml in place of the specified skin. It will not be required when properly integrated */
    public static final String USE_NEW_SKIN_NAME = "useArcIbSkin";

    /** Currently, the menuStyle parameter in browserParemeters.xml defines the menu style to be displayed to the user. DebugUtils is used to insert this menuStyle into the response for use in the xsl. Ultimately this value should not be specified in browserParameters.xml but rather the value should be inserted into the xml response by T24 server. */
    public static final String MENU_STYLE_NAME = "menuStyle";
    
    /** The Constant MENU_STYLE_DEFAULT. */
    public static final String MENU_STYLE_DEFAULT = "DEFAULT";
    
    /** The Constant MENU_STYLE_TABBED. */
    public static final String MENU_STYLE_TABBED = "TABBED";
    
    /** The Constant MENU_STYLE_POPDOWN. */
    public static final String MENU_STYLE_POPDOWN = "POPDOWN";

    /** useIntObfuscation determines whether obfuscation of T24 commands should be used or not. It will not be required when properly integrated. */
    public static final String USE_INT_OBFUSCATION_NAME = "useInternalObfuscation";

    /** useExtObfuscation determines whether obfuscation and compression of JavaScript should be used or not. It will not be required when properly integrated. */
    public static final String USE_EXT_OBFUSCATION_NAME = "useExternalObfuscation";

	/** stripFrameToolbars determines whether toolbar buttons and dropdown are present in fragments (contract applications). */
    public static final String STRIP_FRAME_TOOLBARS_NAME = "stripFrameToolbars";

    /** suppressMissingHelp stops the standard missing help response when we have no help files, as this fouls up the context-sensitive displays on the page. */
    public static final String SUPPRESS_MISSING_HELP_NAME = "suppressMissingHelp";

    /** print determines whether the printing feature should be active or not. */
    public static final String PRINT_NAME = "cleanPrint";

    /** determines whether to disable hot fields. */
    public static final String HOTS_ALLOWED_NAME = "enableHotfields";

    /** determines whether to enable downloading of OFX statements. */
    public static boolean ofx;
    
    /** The Constant OFX_DESC. */
    public static final String OFX_DESC = "Enable downloading of OFX statements";
    
    /** The Constant OFX_NAME. */
    public static final String OFX_NAME = "ofx";
     
    /** determines whether to enable keepalive. */
    public static final String KEEPALIVE_NAME = "useKeepaliveHandling";
    
	/** To check whether the confirm box after time out should be displayed or not. */
	public static final String KEEPALIVE_CONFIRM = "useconfirm";

    /** determines whether to enable automatic holding of contract transactions. */
    public static final String AUTO_HOLD_DEALS_NAME = "useAutoHoldDeals";
    
    /** FRAGMENT_NAME_ELEMENT_NAME element stuffed into the frames xml for named fragments. As above, it will not be required when support for named fragments is added to T24. */
    public static final String FRAGMENT_NAME_ELEMENT_NAME = "fragmentName";

    /** Name of the obfuscator attribute in the session. */
    private static final String OBFUSCATOR_ATTRIB = "obfuscator";

    /** Name of the tag used to srip user name from the java script. */
    public static final String STRIP_USER_ELEMENT_NAME = "stripUserName";

    /** showStatusInformation determines whether the information is displayed on the browser status bar */
    public static final String SHOW_STATUS_INFO_NAME = "showStatusInfo";
    
    public static final String SHOW_ENQUIRY_SELECTION_BOX = "enquirySelectionBox";

    /** File with T24 command constants. */
    private static final String T24_COMMANDS_XSL_FILE = "transforms"
    	+ File.separator + "ARC" + File.separator + "T24_constants.xsl";

    /** Base path of the Browser servlet. */
    private static String BASE_PATH;

    /** Browser parameters. */
    private PropertyManager ivParameters;
    
    /** Name of the none option attribute in browser parameters */
    public static final String NONE_OPTION = "None option";
    
    public static final String NONE_DISPLAY = "Nonedisplay";
    
    /** Name of the scroll allowed attribute in browser parameters */
    public static final String ENQUIRY_DATA_SCROLL = "EnquiryDataScroll";
          
    /** Name of the associated mandatory combo option attribute in browser parameters */
    public static final String ASSOC_REQ_COMBO_OPTION = "ASSOC_REQ_COMBO";
    
    public static final String ASSOC_REQ_COMBO_DISPLAY = "Assocdisplay";
    
    /** Variables Uses to specify cloud usage */
    public static final String ENABLE_CLOUD = "enableCloud";
    public static final String USE_CLOUD_IMAGE = "<useCloudImage>";
    public static final String USE_CLOUD_IMAGE_C = "</useCloudImage>";
    
    /** Name of the auto launch context enquiry attribute in browser parameters */
    public static final String AUTOLAUNCH_ENQUIRY = "AUTOLAUNCH_CONTEXT_ENQUIRY_DISPLAY";
    
    public static final String CONTEXT_ENQ_DISPLAY = "Autoenqdisplay";
    
    /** showReleaseInfo determines whether the information on release details is displayed on the browser window title bar */
    public static final String SHOW_RELEASE_INFO_NAME = "showReleaseInfo";
    
    /**
     * Private constructor.
     * 
     * @param ivParameters the iv parameters
     */
    public DebugUtils(PropertyManager ivParameters) {
    	this.ivParameters = ivParameters;
    }
    
    /**
     * Helper method which gets a value for the specified property from a system property or the properties file.
     * 
     * @param propertyName the property name
     * 
     * @return the property
     */
    private static boolean getProperty(String propertyName) {
		String propStr = System.getProperty(propertyName);
		if (propStr == null) {
			propStr = properties.getProperty(propertyName);
		}

		return(propStr == null ? false : "true".equalsIgnoreCase(propStr));
	}

    /**
     * This method is called (by InstanceConnector) with all of the XML
     * received from T24 as the user goes about their business.<br>
     * This DebugServlet then gets a chance to save or manipulate the xml.
     * 
     * @param xml the XML that was passed back by T24
     * @param requestFragmentName the request fragment name
     * @param request the request
     * 
     * @return the 'messed with' XML
     */
    public synchronized String preprocessT24Xml(String xml, String requestFragmentName, HttpServletRequest request) {
        try {
        	LOGGER.info("Preprocessing of the XML message for ARC-IB prior to transformation");

            // TODO: Could do all of these by parsing XML etc, but will use Strings for simplicity

            xml = addFragmentName(xml, requestFragmentName);
            xml = addNoFramesParam(xml);
            //	 Removed the code to enable skin to be specified in UI.APPEARANCE for ARC-IB
			updateSkin(request);
			xml = addArcXml(xml);
            xml = addMenuProperty(xml);
            xml = addObfuscatePropertyAndObfuscateInputXML(xml, request);
            xml = addToolbarToggle(xml);
            xml = addPrintToggle(xml);
            xml = addHotfieldToggle(xml);
            xml = addLoggingToogle(xml);
            xml = ofxSupport(xml);
            xml = addKeepaliveToggle(xml);
            xml = addAutoHoldDealsToggle(xml);
            xml = addStatusBarInfoToggle(xml);
            xml = addassocrec(xml);
            xml = addnone(xml);
            xml = addautolaunchenq(xml);
            xml = addscroll(xml);
            xml = addEnquirySelectionDisplayBox(xml); // Add the selection box
            xml = addCloudImageConfigInfo(xml);
            xml = addReleaseInfo(xml);
            xml = removeUser(xml, request);
            locateTransactionIds(xml, request);
            writeXML(xml, null);
        } catch (Exception e) {
            System.out.println("Exception messing with the T24 XML");
            e.printStackTrace();
        }
        return xml;
    }
    
    /**
     * This method is called (by BrowserBean) with web validation XML<br>
     * The xml is then augmented with necessary parameters, such as obfuscation settings.
     * 
     * @param xml the XML generated by the validation process
     * @param params browser params
     * 
     * @return the 'messed with' XML
     */
    public static String preprocessValidationXml(String xml, PropertyManager params) {

        // get both obfuscation params
    	boolean intObfuscation = getBooleanProperty(params, USE_INT_OBFUSCATION_NAME);
    	boolean extObfuscation = getBooleanProperty(params, USE_EXT_OBFUSCATION_NAME);
    	
        String obfElem = null;

        if (intObfuscation && extObfuscation) {
            obfElem = "<obfuscate type=\"both\"/>";
        }
        else if(intObfuscation) {
            obfElem = "<obfuscate type=\"internal\"/>";
        }
        else if(extObfuscation) {
            obfElem = "<obfuscate type=\"external\"/>";
        }

        String newXml = xml.replaceFirst("<validationResponse>", "<validationResponse>" + obfElem);
        return newXml;
    }

    /**
     * Writes the xml request generated by the web server, in the test xml directory.
     * Also, renders the HTTP request parameters as an XML file in the same place.
     * DebugServlet.java will then look at these files and display them.
     * 
     * @param xmlRequest the xml request
     * @param request the request
     */
    public synchronized void writeRequestXML(String xmlRequest, HttpServletRequest request)
    {
    	//Performance problem-to minimize time delay 
    	if(saveXml==false)
        {
    		return;
        }
        T24Request t24Request = new T24Request( request);
        String httpRequest = t24Request.toXml();
        String httpXMLDoc = "<httpParams>" + httpRequest + "</httpParams>";
        String reqSuffix = "_REQUEST";
        String paramSuffix = "_HTTP_PARAMS";

        try {
    		xmlRequest = xmlRequest.replaceFirst("BROWSER.XML,,,,,,<", "");
            String requestType = new XMLRequestManager().getNodeValue(xmlRequest, "requestType");
            if( requestType != null) {
                reqSuffix = requestType + reqSuffix;
                paramSuffix = requestType + paramSuffix;
            }

    		writeXML(xmlRequest, reqSuffix);
            writeXML(httpXMLDoc, paramSuffix);
    	}
    	catch( Exception e) {
    		System.out.println("Exception writing debug request + parameter XML dumps " + e);
            e.printStackTrace();
    	}
    }

    /**
     * If the <code>SAVE_XML</code> constant is <code>true</code>, then the
     * xml is written out to a file in the test directory (with a unique name).
     * It will this be in the list of files that can be selected for test
     * rendering.
     * 
     * @param xml the XML that was passed back by T24
     * @param fileSuffix optional suffix for XML file, overrides default based on embedded response type and/or fragment name
     * 
     * @return <code>false</code> indicates that there was an error saving the
     * file
     * 
     * @throws TransformerException the transformer exception
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XMLRequestManagerException the XML request manager exception
     * @throws TransformerConfigurationException the transformer configuration exception
     */
    private static synchronized boolean writeXML(String xml, String fileSuffix) throws TransformerConfigurationException, XMLRequestManagerException, IOException, SAXException, ParserConfigurationException, TransformerException {
        if (saveXml) {
            if (dbfactory == null) {
                dbfactory = DocumentBuilderFactory.newInstance();
            }
            if (tfactory == null) {
                tfactory = TransformerFactory.newInstance();
            }

            File f = null;
            try {
                // Pretty up the XML and save it to a file

                String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

                XMLRequestManager xmlManager = new XMLRequestManager();

                if (fileSuffix != null) {
                    fileName += "_" + fileSuffix;
                }
                else {
                    String responseType = xmlManager.getNodeValue(xml, "responseType");
                    if (responseType != null) {
                        fileName += "_" + responseType;
                    }
                    
                    String fragName = xmlManager.getNodeValue(xml, "fragmentName");
                    if (fragName != null) {
                        fileName += "_" + fragName;
                    }
                }

                // Create the XML output file
                f = new File(xmlDirectory, fileName + ".xml");
                boolean created = f.createNewFile();
                if (!created) {
                    throw new IOException("Could not create file");
                }

                Document doc = dbfactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
                Transformer transformer;
                Result result = new StreamResult(new FileWriter(f));
                transformer = tfactory.newTransformer();
                // Setup indenting to "pretty print"
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                // Write to file
                transformer.transform(new DOMSource(doc), result);
            } catch (Exception e) {
                // If the pretty print failed - for whatever reason, save
                // the file anyway
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                out.write(xml);
                out.close();
            }

        }
        return true;
    }

    /**
     * Test method called to insert the 'frameless' param into frames xml.
     * This will stop being used once support for no frames is in the
     * platform.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addNoFramesParam(String xml) {
    	boolean noFrames = getBooleanProperty(NOFRAMES_NAME);
    	
		int framesIndex = xml.indexOf("<frames>");
        if (framesIndex > 0) {
	    	if (noFrames) {
	    		int index = xml.indexOf(NOFRAMES_NAME);
		        if (index > 0 && (xml.substring(0, index).indexOf(NOFRAMES_NAME) == -1)) {
		        	LOGGER.info("Overriding the " + NOFRAMES_NAME + " element to the XML");
		            xml = xml.replaceFirst("<" + NOFRAMES_NAME + ">[^<]*</" + NOFRAMES_NAME + ">",
		            		               "<" + NOFRAMES_NAME + ">" + NOFRAMES_ENABLED + "</" + NOFRAMES_NAME + ">");
		        }
		        else {
		        	LOGGER.info("Adding the " + NOFRAMES_NAME + " element to the XML");
		            xml = xml.replaceFirst("<frames>",
                                           "<" + NOFRAMES_NAME + ">" + NOFRAMES_ENABLED + "</" + NOFRAMES_NAME + "><frames>");
		        }
	    	}
    	}
        return xml;
    }

    /**
     * Test method called to insert the 'frameless' param into frames xml.
     * This will stop being used once support for no frames is in the
     * platform.
     * 
     * @param xml the xml
     * @param fragmentName the fragment name
     * 
     * @return the string
     */
    public static String addFragmentName(String xml, String fragmentName) {
        // Only add the fragment name if this is not the high-level frames XML
        if ((fragmentName != null) && (xml.indexOf("<frames>") < 0)) {
        	LOGGER.info("Adding " + FRAGMENT_NAME_ELEMENT_NAME + " element");
            String fragmentNameReplacementStr = "<" + FRAGMENT_NAME_ELEMENT_NAME + ">" + fragmentName + "</" + FRAGMENT_NAME_ELEMENT_NAME + ">";
            xml = xml.replaceFirst("</userDetails>", fragmentNameReplacementStr + "</userDetails>");
        }
        return xml;
    }

    /**
     * Test method used to insert the new skin into the app (using brute force) if specified.
     * 
     * @param xml the xml
     * @param request the request
     * 
     * @return the string
     */
	//  Removed the code to enable skin to be specified in UI.APPEARANCE for ARC-IB

	public void updateSkin(HttpServletRequest request) {
	//Use the ARC-IB product parameter to check if it is arc-ib
    	String arcIb = ivParameters.getParameterValue("Product");
        if ( (arcIb!=null) && (arcIb.equals("ARC-IB")) ) 
 		{
         	request.getSession().setAttribute("ARC-IB", Boolean.TRUE);      
        }
    }

    /**
     * Insert the menuStyle parameter in browserParemeters.xml into the T24 xml response.
     * Currently, the menuStyle parameter in browserParemeters.xml defines the menu style to be displayed to the user.
     * DebugUtils is used to insert this menuStyle into the response for use in the xsl.
     * Ultimately this value should not be specified in browserParameters.xml but rather the value should be inserted into
     * the xml response by T24 server.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addMenuProperty(String xml) {
    	String menuStyle = ivParameters.getParameterValue(MENU_STYLE_NAME);
        String replacementStr;

        // Add the menu style switch based on the debug parameter
        // - only append XML for valid non-default settings
        if ((menuStyle.equalsIgnoreCase(MENU_STYLE_TABBED)) || (menuStyle.equalsIgnoreCase(MENU_STYLE_POPDOWN)) || (menuStyle.equalsIgnoreCase(MENU_STYLE_DEFAULT)) ) {
            replacementStr = "<" + MENU_STYLE_NAME + ">" + menuStyle + "</" + MENU_STYLE_NAME + ">";
        }
        else {
            LOGGER.warn("Invalid value or missing parameter (" + MENU_STYLE_NAME +  ") : " + menuStyle);
            replacementStr = "<" + MENU_STYLE_NAME + ">" + MENU_STYLE_DEFAULT + "</" + MENU_STYLE_NAME + ">";
        }

        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }
    
    /** 
     * The parameter value for ASSOC_REQ_COMBO from browserParameters.xml is inserted into T24 xml response.
     * Based on the setting of this parameter value, blank option for mandatory combo box with less than 3 values is restricted during transformation
     */
    public String addassocrec(String xml) {
    	String assocreqcombostyle = ivParameters.getParameterValue(ASSOC_REQ_COMBO_OPTION);
        String replacecomboStr = "";

        // Add the ASSOC_REQ_COMBO parameter value  
               
        replacecomboStr = "<" + ASSOC_REQ_COMBO_DISPLAY + ">" + assocreqcombostyle + "</" + ASSOC_REQ_COMBO_DISPLAY + ">";
        
        return xml.replaceFirst("</userDetails>", replacecomboStr + "</userDetails>");
    }
    
    /** 
     * The parameter value for none display from browserParameters.xml is inserted into T24 xml response.
     * Based on the setting of this parameter value, none option for mandatory combo box is restricted during transformation
     */
    public String addnone(String xml) {
    	String nonestyle = ivParameters.getParameterValue(NONE_OPTION);
        String replaceStr = "";

        // Add the none option parameter value  
               
        replaceStr = "<" + NONE_DISPLAY + ">" + nonestyle + "</" + NONE_DISPLAY + ">";
        
        return xml.replaceFirst("</userDetails>", replaceStr + "</userDetails>");
    }

    /** 
     * The parameter value for auto launch context enquiry display from browserParameters.xml is inserted into T24 xml response.
     * Based on the setting of this parameter value, auto launch context enquiry display is checked
     */
    public String addautolaunchenq(String xml) {
    	String contextenqstyle = ivParameters.getParameterValue(AUTOLAUNCH_ENQUIRY);
        String contextenqStr = "";

        // Add the auto launch context enquiry display parameter value  
               
        contextenqStr = "<" + CONTEXT_ENQ_DISPLAY + ">" + contextenqstyle + "</" + CONTEXT_ENQ_DISPLAY + ">";
        
        return xml.replaceFirst("</userDetails>", contextenqStr + "</userDetails>");
    }
    
    /** 
     * The parameter value for enquiryscroll from browserParameters.xml is inserted into T24 xml response.
     * Based on the setting of this parameter value, scrolling will be disabled in enquiry data part.
     */
    public String addscroll(String xml) {
    	String enqsdatacroll = ivParameters.getParameterValue(ENQUIRY_DATA_SCROLL);
        String replaceStr = "";

        // Add the none option parameter value  
               
        replaceStr = "<" + ENQUIRY_DATA_SCROLL + ">" + enqsdatacroll + "</" + ENQUIRY_DATA_SCROLL + ">";
        
        return xml.replaceFirst("</userDetails>", replaceStr + "</userDetails>");
    }
    
    /**
     *  To add the cloud info tag to the response to retrieve image from Azure Storage 
     */ 
    public String addCloudImageConfigInfo(String xml) 
    {
    	String isCLoudEnabled = ivParameters.getParameterValue(ENABLE_CLOUD);
    	String replaceStr = "";
    	if(isCLoudEnabled!=null && isCLoudEnabled.equalsIgnoreCase("YES"))
    	{
    		 replaceStr = USE_CLOUD_IMAGE + "YES" + USE_CLOUD_IMAGE_C;
    	}
		return  xml.replaceFirst("</userDetails>", replaceStr + "</userDetails>");
	}
       
    /**
     * Simple replace method (compatable with Java 1.4)
     * 
     * @param str the str
     * @param pattern the pattern
     * @param replace the replace
     * 
     * @return the string
     */
    public static String replace(String str, String pattern, String replace) {
		// We can use String.replace if we move to Java 5
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }


    /**
     * Method used to insert the obfuscation property into input XML and to
     * obfuscate input XML.
     * 
     * @param xml the xml
     * @param request the request
     * 
     * @return the string
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String addObfuscatePropertyAndObfuscateInputXML(String xml, HttpServletRequest request) throws IOException{
    	boolean intObfuscation = getBooleanProperty(USE_INT_OBFUSCATION_NAME);
    	boolean extObfuscation = getBooleanProperty(USE_EXT_OBFUSCATION_NAME);
    	
        if (intObfuscation) {
        	Object obfuscator = request.getSession().getAttribute(OBFUSCATOR_ATTRIB);
        	Obfuscator obf;
            // If Obfuscator is already initialized, nothing happens. 
            // Because of session serialization, Obfuscator can be in the 
            // session, yet obfuscation is not initialized. 
            ObfuscationManager.initialize(BASE_PATH);
        	if(obfuscator == null) {
        		obf = new AllTokensObfuscator(new CharMappingObfuscationAlgorithm());
        		request.getSession().setAttribute(OBFUSCATOR_ATTRIB, obf);
        	} else
        		obf = (AbstractObfuscator)obfuscator;

            int tagStart = xml.indexOf("<skin>");
            if (tagStart > 0) {
            	String pre =  xml.substring(0, tagStart);
                String post = xml.substring(tagStart);
                // add <obfuscate/> tag to the input XML
                if(extObfuscation) {
                	xml = pre + "<obfuscate type=\"both\"/>" + post;
                } else {
                	xml = pre + "<obfuscate type=\"internal\"/>" + post;
                }
            }
        	// obfuscate input XML
            xml = ObfuscationManager.obfuscateInputXML(obf, xml);
        } else if(extObfuscation) {
            int tagStart = xml.indexOf("<skin>");
            if (tagStart > 0) {
            	String pre =  xml.substring(0, tagStart);
                String post = xml.substring(tagStart);
                // add <obfuscate/> tag to the input XML
               	xml = pre + "<obfuscate type=\"external\"/>" + post;
            }
        }
        return xml;
    }


    /**
     * Test method called to add a parameter into the XML for window.xsl to process.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addToolbarToggle(String xml) {
    	boolean stripFrameToolbars = getBooleanProperty(STRIP_FRAME_TOOLBARS_NAME);
        // Add the toolbar switch based on the debug toggle parameter
    	LOGGER.info("Adding stripFrameToolbars element with value: " + Boolean.toString(stripFrameToolbars));
        String replacementStr = "<" + STRIP_FRAME_TOOLBARS_NAME + ">" + Boolean.toString(stripFrameToolbars) + "</" + STRIP_FRAME_TOOLBARS_NAME + ">";

        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }

    /**
     * Test method called to add a parameter into the XML for window.xsl to process.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addPrintToggle(String xml) {
    	boolean print = getBooleanProperty(PRINT_NAME);
    	// Add the print switch based on the debug toggle parameter
        String replacementStr = "<" + PRINT_NAME + ">" + Boolean.toString(print) + "</" + PRINT_NAME + ">";

        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }

    /**
     * Test method called to disable hotfields.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addHotfieldToggle(String xml) {
    	boolean hotsAllowed = getBooleanProperty(HOTS_ALLOWED_NAME);
        // Add the switch
        if (!hotsAllowed) {
	        String replacementStr = "<hotsAllowed>N</hotsAllowed><autosAllowed>N</autosAllowed>";
        	xml = xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
		}

		return xml;
    }

    /**
     * Test method called to enable logging.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addLoggingToogle(String xml) {
    	boolean logging = getBooleanProperty(LOGGING_NAME);
        // Add the switch
        String replacementStr = "<" + LOGGING_NAME + ">" + Boolean.toString(logging) + "</" + LOGGING_NAME + ">";

        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }

    /**
     * Adds the keepalive toggle.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addKeepaliveToggle(String xml) {
    	boolean keepalive = getBooleanProperty(KEEPALIVE_NAME);
        // Add the switch
    	// To check whether the confirm prompt after time out should come or not 
        String replacementStr = "<" + KEEPALIVE_NAME + ">" + "true" + "</" + KEEPALIVE_NAME + ">"+"<" + KEEPALIVE_CONFIRM + ">" + Boolean.toString(keepalive) + "</" + KEEPALIVE_CONFIRM + ">";
        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }  
    
    /**
     * Adds the auto hold deals toggle.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addAutoHoldDealsToggle(String xml) {
    	boolean autoHold = getBooleanProperty(AUTO_HOLD_DEALS_NAME);
        // Add the switch
        String replacementStr = "<" + AUTO_HOLD_DEALS_NAME + ">" + Boolean.toString(autoHold) + "</" + AUTO_HOLD_DEALS_NAME + ">";
        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }
    
    /**
     * Test method called to display status bar information.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addStatusBarInfoToggle(String xml) {
    	boolean showStatus = getBooleanProperty(SHOW_STATUS_INFO_NAME);
        // Add the switch
        if (!showStatus) {
        	String replacementStr = "<" + SHOW_STATUS_INFO_NAME + ">" + "NO" + "</" + SHOW_STATUS_INFO_NAME + ">";
        	xml = xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
		}

		return xml;
    }
    
    /**
     * Test method called to display status information(release details) on the browse window title bar.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public String addReleaseInfo(String xml) {
    	boolean ReleaseStatus = getBooleanProperty(SHOW_RELEASE_INFO_NAME);
        // Add the switch
        if (!ReleaseStatus) {
        	String releaseStr = "<" + SHOW_RELEASE_INFO_NAME + ">" + "NO" + "</" + SHOW_RELEASE_INFO_NAME + ">";
        	xml = xml.replaceFirst("</userDetails>", releaseStr + "</userDetails>");
		}

		return xml;
    }
    
    /**
     * Method to add enquiry Selection display box parameter
     * 
     * @param xml
     * 
     * @return xml string
     */
    public String addEnquirySelectionDisplayBox(String xml){
    	boolean showSelectionBox = getBooleanProperty(SHOW_ENQUIRY_SELECTION_BOX);
    	if(showSelectionBox){
    	String replacementStr = "<"+SHOW_ENQUIRY_SELECTION_BOX+">"+showSelectionBox+"</"+SHOW_ENQUIRY_SELECTION_BOX+">";
    	xml = xml.replaceFirst("</userDetails>", replacementStr+"</userDetails>") ;
    	}
    	return xml;
    }
    
    /**
     * Test method called to enable downloading of OFX statements.
     * 
     * @param xml the xml
     * 
     * @return the string
     */
    public static String ofxSupport(String xml) {
        // Add the switch
    	if(ofx) {
    		int start = xml.indexOf("csv");
    		if(start >= 0) {
    			int end = xml.indexOf("</o>", start) + 4;
                String replacementStr = "csv</attrib></o>" + 
                "<o><n>Save as OFX</n><attrib>ofx</attrib></o>";
                xml = xml.substring(0, start) + replacementStr + xml.substring(end);
    		}
    		start = xml.indexOf("<selSection");
    		if(start >=0) {
    			String replacementStr = "<metadataSection>" + 
    			"<ofxMapping>" +
    			"<TRNTYPE>2</TRNTYPE>" +
    			"<DTPOSTED>1</DTPOSTED>" +
    			"<TRNAMT>5</TRNAMT>" +
    			"<FITID>3</FITID>" +
    			"<NAME>3</NAME>" +
    			"<ACCTID>2</ACCTID>" +
    			"<CURDEF>4</CURDEF>" +
    			"</ofxMapping>" +
    			"</metadataSection>";
    			xml = xml.substring(0, start) + replacementStr + xml.substring(start);
    		}
    		
    	}
    	return xml;
    }

    
    /**
     * Remove all of the files in the test directory (without any warnings).
     */
    public static void removeTestFiles() {
        deleteDirectory(xmlDirectory);
        xmlDirectory.mkdirs();
    }

    /**
     * Generic method to delete a non empty directory.
     * 
     * @param path the path
     * 
     * @return true, if delete directory
     */
    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Save the HTML if requested (before switching the servlet calls).
     * 
     * @param filePath the file path
     * @param htmlResult the html result
     */
    public static void saveHTML(String filePath, String htmlResult) {
        File f = null;
        try {
            f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }
            boolean created = f.createNewFile();
            if (!created) {
                throw new IOException("Could not create file");
            }
            // Write to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(htmlResult);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform XML.
     * 
     * @param xml the xml
     * @param realPath the real path
     * 
     * @return the string
     * 
     * @throws XMLRequestManagerException the XML request manager exception
     * @throws Exception the exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String transformXML(String xml, String realPath) throws XMLRequestManagerException, Exception,
            UnsupportedEncodingException {
        XMLRequestManager xmlManager = new XMLRequestManager();
        String styleSheet = xmlManager.getNodeValue(xml, "styleSheet");
        String responseXml = xmlManager.getNodeValue(xml, "responseData");
        XMLToHtmlBean xmlTranformer = new XMLToHtmlBean(realPath);
        String htmlResult = xmlTranformer.transformXml(styleSheet, responseXml);
        return htmlResult;
    }

    /**
     * Read file.
     * 
     * @param dir the dir
     * @param filePath the file path
     * 
     * @return the string
     */
    public static String readFile(File dir, String filePath) {
        // A file is specified, transform it and return the result
        
        String fullPath = dir + PATH_SEPERATOR + filePath;
        
        StringBuffer strBuff = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fullPath));
            String str;
            while ((str = in.readLine()) != null) {
                strBuff.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return strBuff.toString();
    }

    /**
     * This method must be called to initialize the variables!<br>
     * It is called by the debug servlet.
     * 
     * @param basepath the basepath
     * 
     * @throws ServletException the servlet exception
     */
    public static void init(String basepath) throws ServletException {
		if (BASE_PATH == null) {
			BASE_PATH = basepath;

			xmlDirectory = new File(basepath + PATH_SEPERATOR + TEMPLATE_PATH + PATH_SEPERATOR);
			if (!xmlDirectory.exists()) {
				xmlDirectory.mkdirs();
			}

            protoDirectory = new File(basepath + PATH_SEPERATOR + PROTO_PATH + PATH_SEPERATOR);
            if (!protoDirectory.exists()) {
                protoDirectory.mkdirs();
            }

			try {
				File file = new File(basepath + File.separator + DEBUG_PROPERTIES_FILENAME);
				if (file.exists()) {
					InputStream in = new BufferedInputStream(new FileInputStream(file));
					properties.load(in);
					in.close();

					System.out.println("Loaded properties" + properties);
				}
			}
			catch (Exception e) {
				System.out.println("Exception loading default properties: " + e.getMessage());
				e.printStackTrace();
			}


			saveXml = getProperty("com.temenos.arc.debug.saveXml");
            ofx = getProperty("com.temenos.arc.ofx");
		}
    }

    /**
     * Replaces T24 command files.
     * 
     * @param useObfuscation true if obfuscation is to be used, otherwise false.
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void replaceT24CommandsFile(boolean useObfuscation)
    	throws IOException{

    	String actualXslFilePath = BASE_PATH + File.separator +	T24_COMMANDS_XSL_FILE;
    	File actualXslFile = new File(actualXslFilePath);
    	(new File(actualXslFilePath)).delete();
    	if(useObfuscation) {
    		File origXslFile = new File(actualXslFilePath +
    			ObfuscationManager.OBF_COMMANDS_POSTFIX);
    		if(origXslFile.exists())
    			FileManager.copyFile(origXslFile, actualXslFile);
    	} else {
    		File obfuscatedXslFile = new File(actualXslFilePath +
    			ObfuscationManager.ORIG_COMMANDS_POSTFIX);
    		if(obfuscatedXslFile.exists())
    			FileManager.copyFile(obfuscatedXslFile, actualXslFile);
    	}
    }
    
    /**
     * Gets the boolean property.
     * 
     * @param param the param
     * 
     * @return the boolean property
     */
    private boolean getBooleanProperty(String param) {
    	return getBooleanProperty(ivParameters, param);
    }
    
    /**
     * Gets the boolean property.
     * 
     * @param ivParam the iv param
     * @param param the param
     * 
     * @return the boolean property
     */
    public static boolean getBooleanProperty(PropertyManager ivParam, String param) {
    	String value = ivParam.getParameterValue(param);
    	if(value == null) {
    		LOGGER.warn("Property was not found in browser parameters: " + param);
    		return false;
    	} else if(value.equalsIgnoreCase("YES")) {
    		return true;
    	} else 
    		return false;
    }
     
    /**
     * Changes path of the xsl files in OFS messages to the /transform/...
     * This method was implemented after the .xsl files were moved to the
     * transform directory. When T24 is adjusted to this change, method will
     * not be used any more.
     * 
     * @param ofsReply OFS response
     */
    public static void checkXslPath(BrowserResponse ofsReply) {
    	if(ofsReply == null)
    		return;
		String msg = ofsReply.getMsg();
		if(msg==null)
		   return;
		Matcher m = Pattern.compile("<styleSheet>.*</styleSheet>").matcher(msg);
		if(m.find()) {
			String result = m.group();
			result = result.substring(12,result.length() - 13);
            if((result.indexOf("/transforms/") == -1) && result.endsWith(".xsl")) {
				result = "/transforms/" + result;
				LOGGER.debug("xsl file path in OFS message changed to: " + result);
			}
			StringBuffer buff = new StringBuffer(msg.length() + 20);
			buff.append(msg.substring(0,m.start()));
			buff.append("<styleSheet>");
			buff.append(result);
			buff.append("</styleSheet>");
			buff.append(msg.substring(m.end()));
			ofsReply.setMsg(buff.toString());
		}
    }
    
	/**
	 * Removes the user.
	 * 
	 * @param xml the xml
	 * @param request the request
	 * 
	 * @return the string
	 */
	public String removeUser(String xml, HttpServletRequest request) {
		boolean removeUser = getBooleanProperty(STRIP_USER_ELEMENT_NAME);

        if (removeUser) {
            LOGGER.info("Adding " + STRIP_USER_ELEMENT_NAME + " element");
            String replacementStr = "<"+STRIP_USER_ELEMENT_NAME+">" + removeUser + "</"+STRIP_USER_ELEMENT_NAME+">";
            xml = xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
        }
        return xml;
    }

	/**
	 * Parse any transaction ids from the request, and hold onto them in the session.
	 * This is later checked to ensure that there is not an attempt to save a request with an unknown id.
	 * 
	 * @param xml the xml
	 * @param request the request
	 * 
	 * @see VariableSubstitutionHttpServletRequest
	 */
    public void locateTransactionIds(String xml, HttpServletRequest request) {
        int tagStart = xml.indexOf("<key>");
        if (tagStart > 0) {
            int tagEnd = xml.indexOf("</key>");
            String content  = xml.substring(tagStart + "<key>".length(), tagEnd);
            
            if (content != null && content.length()>0) {
                Collection keys = (Collection)request.getSession().getAttribute("keys");
                if (keys == null) {
                    keys = new HashSet();
                    request.getSession().setAttribute("keys", keys);
                }
                LOGGER.info("Storing transactionid: " + content);
                keys.add(content);
            }
        }
    }	
    
/* Method to test the product type whether it is browser or arc-ib */
	
	public String addArcXml(String xml) {
    	String productTagValue = ivParameters.getParameterValue("Product");
        String replacementStr;
		
        // Add the product type switch based on the debug parameter
        // - only append XML for valid non-default settings
        if ((productTagValue.equalsIgnoreCase("ARC-IB")) ) {
            replacementStr = "<" + "ARCIB" + ">" + "true" + "</" + "ARCIB" + ">";
        }
        else {
            replacementStr = "<" + "ARCIB" + ">" + "false" + "</" + "ARCIB" + ">";
        }

        return xml.replaceFirst("</userDetails>", replacementStr + "</userDetails>");
    }
}
