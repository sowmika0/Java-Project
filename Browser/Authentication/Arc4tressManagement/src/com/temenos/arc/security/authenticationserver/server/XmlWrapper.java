package com.temenos.arc.security.authenticationserver.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Superclass for CALLJ/XML entrypoints.  This class provides the common XML parsing
 * functionality required by such classes. 
 * @author jannadani
 *
 */
public class XmlWrapper {

    public static final String RETURN_STATE = "returnState";
    public static final String SUCCESS_STATE = "0";
    public static final String FAILURE_STATE = "1";
    public static final String COMMUNICATION_ERROR="2";
    public static final String OTHER_ERROR = "3";
    public static final String ARGS_START = "<args>";
    public static final String ARGS_END = "</args>";
    
    protected static Logger logger = LoggerFactory.getLogger(XmlWrapper.class);
    private static ClassLoader _savedLoader = null;

    public XmlWrapper() {
        super();
    }

    /**
     * Used by subclasses to construct return XML 
     * @param status    the value to be inserted into the RETURN_STATE tag  
     * @param argsToAdd subclass provided extra return args
     * @return xml string to be sent back to the client
     */
    protected String createReturnXml(String status, StringBuffer argsToAdd) {
        StringBuffer buffer = new StringBuffer();
    	// indentation not strictly to java coding standards, but it is easier to read
    	buffer.append(ARGS_START);
    	    buffer.append(argsToAdd);
           	buffer.append("<" + XmlUserManagement.RETURN_STATE + ">");
    		buffer.append(status);
    		buffer.append("</" + XmlUserManagement.RETURN_STATE + ">");
    	buffer.append(ARGS_END);
    	
    	return buffer.toString();
    }
    
    protected void checkNotEmpty(String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalStateException("CrytographyService does not support empty strings");
        }
    }

    /** Helper used by subclasses
     * @param argVal    value to be inserted into tag
     * @param argName   tag name
     * @return  a StringBuffer containing <code>&ltargName&gtargVal&lt/argName&gt</code> 
     */
    protected StringBuffer getReturnArg(String argVal, String argName) {
        checkNotEmpty(argVal);
        StringBuffer toReturn = new StringBuffer();
        toReturn.append("<" + argName + ">");
        toReturn.append(argVal);
        toReturn.append("</" + argName + ">");
        return toReturn;
    }

    /**
     * helper used by test classes - not intended for normal use 
     * @param xml
     * @param argName
     * @return
     */
    static String getArg(String xml, String argName) {
        Map args = parseXmlIntoArgs(xml);
        String arg = (String) args.get(argName);
        if (logger.isDebugEnabled()) logger.info("returning: " + arg);
        return arg;
    }

    protected static Map parseXmlIntoArgs(String xmlArgs) {        
        Document document = initialiseParser(xmlArgs);
        return getArgsFromDocument(document);
    }

    private static Document initialiseParser(String xmlArgs) {
        // TODO namespace/validation etc? noddy XML 101 settings for now  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        Document document = null;
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse(new ByteArrayInputStream(xmlArgs.getBytes()));
        } catch (ParserConfigurationException e) {
            throw new ArcAuthenticationServerException("Unable to create DOM XML parser.");
        } catch (SAXException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException("Unable to parse xml string: " + xmlArgs + "\n" + e.getMessage());
        } catch (IOException e) {
            throw new ArcAuthenticationServerException("Unable to create ByteArrayInputStream from: " + xmlArgs);
        }
        return document;
    }

    private static Map getArgsFromDocument(Document document) {
        // now parse the DOM document
    	Map argMap = new HashMap();
        Map custInfo = new HashMap();
        NodeList argList = document.getElementsByTagName("args");
        if (argList.getLength() != 1) {
            throw new ArcAuthenticationServerException("should only be one args tag");
        }
        Element args = (Element) argList.item(0);
        NodeList argNodes = args.getChildNodes();
        for (int i=0; i < argNodes.getLength(); ++i) {
            Node arg = argNodes.item(i);
            if(arg.getNodeName().equals("Customer")){
              	NodeList custList = arg.getChildNodes();
            	for(int j =0 ; j < custList.getLength();j++){
            	    Node cust = custList.item(j);
            	    custInfo.put(cust.getNodeName(),cust.getTextContent());
            	}
            	if(i == (argNodes.getLength())-1){
            		argMap.put(arg.getNodeName(),custInfo);
            		break;
            	}
            }
            if(arg.getFirstChild() == null){
            	logger.debug(" arg.getFirstChild() is null");
            	argMap.put(arg.getNodeName(), null);	
            }else{
            	logger.debug(" arg.getFirstChild() is not null");
            	argMap.put(arg.getNodeName(), arg.getFirstChild().getNodeValue());
            }
        }        
        return argMap;
    }
    /**
     * A bug was noticed with CALLJ on Windows, whereby the context classloader
     * was null on subsequent threads. Therefore, this is a workaround to save
     * the classloader to a static variable, and then reset if its gone missing.
     */
    protected void checkClassLoader() {
        if (_savedLoader == null) {
            _savedLoader = Thread.currentThread().getContextClassLoader();
        }
        if (Thread.currentThread().getContextClassLoader() == null && _savedLoader != null) {
            Thread.currentThread().setContextClassLoader(_savedLoader);
        }
    }

}
