package com.temenos.t24browser.obfuscator;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This class represents T24 obfuscation manager.
 * 
 * @author mludvik
 */
public class ObfuscationManager {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObfuscationManager.class);
	//	 Used for logging
	/** The Constant OBF_COMMANDS_POSTFIX. */
	public static final String OBF_COMMANDS_POSTFIX = ".OBFUSCATED";
	
	/** The Constant ORIG_COMMANDS_POSTFIX. */
	public static final String ORIG_COMMANDS_POSTFIX = ".ORIGINAL";
    
    /** The Constant OBFUSCATOR. */
    private static final String OBFUSCATOR = "obfuscator";
    
    /** The Constant OBFUSCATION_DIR. */
    private static final String OBFUSCATION_DIR = "WEB-INF" + 
	File.separator + "conf" + File.separator + OBFUSCATOR;
    
    /** The Constant COMMON_OBFUSCATION_TABLE_FILE. */
    private static final String COMMON_OBFUSCATION_TABLE_FILE = OBFUSCATION_DIR + 
    	File.separator + "commonCommands.prop";
    
    /** The Constant OBFUSCATION_TAGS. */
    private static final String OBFUSCATION_TAGS = OBFUSCATION_DIR + 
	File.separator + "tags.prop";
    
    /** The Constant T24_RESPONSE_TYPE_XPATH. */
    private static final String T24_RESPONSE_TYPE_XPATH = "/ofsSessionResponse/responseType";
    
    /** The OBFUSCATIO n_ MAP. */
    private static HashMap OBFUSCATION_MAP;

    /**
     * Instantiates a new obfuscation manager.
     */
    private ObfuscationManager(){};

    /**
     * Initializes static obfuscation table from property file. If the manager is
     * already initialized, nothing happens.
     * 
     * @param basePath base path of the BrowserServlet
     * 
     * @throws IOException if any I/O exception appears
     */
    public synchronized static void initialize(String basePath) throws IOException {
    	if(OBFUSCATION_MAP != null)
    		return;
		if(!AbstractObfuscator.commonTableLoaded())
			AbstractObfuscator.setCommonTable(new File(basePath + 
				File.separator + COMMON_OBFUSCATION_TABLE_FILE));
		// initialize OBFUSCATION_MAP
		File obfPropsFile = new File(basePath + 
				File.separator + OBFUSCATION_TAGS); 
		OBFUSCATION_MAP = new HashMap();
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(obfPropsFile));
		// lead properties file
		prop.load(in);
		in.close();
		/* 
		 * Create map which maps list of XPaths to the associated message type. 
		*/
		for(Enumeration keys = prop.keys(); keys.hasMoreElements();) {
			String key = (String)keys.nextElement();
			String prefix = key.substring(0, key.lastIndexOf('.'));
			List xpaths;
			if(OBFUSCATION_MAP.get(prefix) == null) {
				xpaths = new LinkedList();
				OBFUSCATION_MAP.put(prefix, xpaths);
			} else 
				xpaths = (List)OBFUSCATION_MAP.get(prefix);
			xpaths.add(prop.getProperty(key));
		}
    }
    
    /**
     * Obfuscates input XML.
     * All tags in input XML which are identified by XPath are obfuscated. XPath
     * are loaded from property file,e.g. "tags.prop".
     * 
     * @param obf Obfuscator used for obfuscation
     * @param xml Input XML
     * 
     * @return obfuscated XML
     * 
     * @throws IOException if any I/O exception appears
     */
    public static String obfuscateInputXML(Obfuscator obf, String xml) throws IOException{
    	/* Otherwise does not work, input XML from T24 can contain strange characters
    	 * like ý (between tags!) and Xerxes is cripled.  
    	*/
    	xml = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"no\"?>" + xml;
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
		} catch(SAXException e) {
			LOGGER.error(e.getMessage());
        	
		}
		Document document = parser.getDocument();
		
		obfuscateInputXML(obf, document);

		OutputFormat format = new OutputFormat(document);
		ByteArrayOutputStream obfuscatedXML = new ByteArrayOutputStream();
		/*
		 * XMLSerializer is part of Xerces library.
		 */
		XMLSerializer serializer = new XMLSerializer(obfuscatedXML, format);
		serializer.asDOMSerializer();
		serializer.serialize(document);
		return obfuscatedXML.toString();
    }
    
    /**
     * Obfuscates input document.
     * All tags in input document which are identified by XPath are obfuscated.
     * XPath are loaded from property file,e.g. "tags.prop".
     * 
     * @param obf Obfuscator used for obfuscation
     * @param document Input document
     * 
     * @throws IOException if any I/O exception appears
     */ 
    public static void obfuscateInputXML(Obfuscator obf, Node document) throws IOException{
		try {
			/*
			 * XPathAPI is class from Xalan library.
			 */
			Node responseTypeNode = XPathAPI.selectSingleNode(document, T24_RESPONSE_TYPE_XPATH);
			String responseType = null;
			if(responseTypeNode != null) {
				Node data = responseTypeNode.getFirstChild();
				if(data != null) {
					responseType = data.getNodeValue();
				}
			}
			if(responseType != null) {
				/* 
				 * Get all XPaths wich are applicable for given response type,
				 * as "//tar" for MENU response.
				 */
				Object xpaths =  OBFUSCATION_MAP.get(responseType); 
				if(xpaths != null) {
					for(Iterator iter = ((List)xpaths).iterator(); iter.hasNext();) {
						String xpath = (String)iter.next();
						// select tags identified by XPath
						NodeList list = XPathAPI.selectNodeList(document,xpath);
						// obfuscate selected tags
			    		for(int i = 0; i < list.getLength(); i++) {
			    			Node node = list.item(i);
			    			Node data = node.getFirstChild();
			    			if(data != null)
			    				data.setNodeValue(obf.transformCompoundCommand(data.getNodeValue()));
			    		}
					}
				}
			}
		} catch(TransformerException e) {
			LOGGER.error(e.getMessage());
		}
    }
}
