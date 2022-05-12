package com.temenos.t24browser.xml;

/*
 *                      XMLHelpManager.java
 * 
 * Manages the parsing of HelpText XML documents.  Provides methods for
 * retrieving the xml created/updated.
 * 
 * Processes an existing XML HelpText string
 *
 * Modifications:
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLHelpManager.
 */
public class XMLHelpManager {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLHelpManager.class);
	
	/** The iv help text doc. */
	private Document ivHelpTextDoc = null; // HelpText XML Document
	
	/** The iv field list doc. */
	private Document ivFieldListDoc = null;
	// Application field list XML Document
	/** The iv xml help text result. */
	private String ivXmlHelpTextResult = "";
	// XML HelpText result after processing

	// Creates an XML document from the helptext XML string
	/**
	 * Instantiates a new XML help manager.
	 * 
	 * @param sXmlHelpText the s xml help text
	 */
	public XMLHelpManager(String sXmlHelpText) {
		ivHelpTextDoc = createXMLDocumentfromString(sXmlHelpText);
	}

	// Sets the list of the fields that the application has
	/**
	 * Sets the XML field list.
	 * 
	 * @param sXmlFieldList the new XML field list
	 */
	public void setXMLFieldList(String sXmlFieldList) {
		ivFieldListDoc = createXMLDocumentfromString(sXmlFieldList);
	}

	// Process the helptext page adding and deleting fields as necessary
	/**
	 * Process help text fields.
	 */
	public void processHelpTextFields() {
		// Get the list of all the field nodes from the helptext
		NodeList helpText_FieldList = null;
		helpText_FieldList = ivHelpTextDoc.getElementsByTagName("field");

		// Get the parent node of the fields - we'll use this to add fields to later
		Node helpText_ParentNode = getNode(ivHelpTextDoc, "//menu");
		
		if ( helpText_ParentNode == null )
		{
			// We haven't got any fields yet, so create a menu node under the t24help top node
			Node helpText_helpNode = getNode(ivHelpTextDoc, "//t24help");
			helpText_helpNode.appendChild(getMenuNode(ivHelpTextDoc));
			helpText_ParentNode = getNode(ivHelpTextDoc, "//menu");
		}

		// Get the list of all the field nodes from the list of fields
		NodeList fields_FieldList = null;
		fields_FieldList = ivFieldListDoc.getElementsByTagName("fld");

		// Get the parent nodes of the fields - we'll use this to delete nodes from later
		Node fields_ParentNode = (Node) ivHelpTextDoc.getElementById("flds");

		// Loop through all the fld tags in fields
		for (int i = 0; i < fields_FieldList.getLength(); i++) {
			boolean fieldFound = false;
			Node field_node = fields_FieldList.item(i);
			String field_nodeValue = getNodeValue(field_node);

			// Check if the field exists in the helptext list
			for (int j = 0; j < helpText_FieldList.getLength(); j++) {
				String currentHelpTextField =
					getNodeValue(helpText_FieldList.item(j));
				if ((field_nodeValue != null)
					&& (field_nodeValue.equals(currentHelpTextField))) {
					// We've found the node so we don't need to do anything - just break out of the loop
					fieldFound = true;
					break;
				}
			}

			// If the field wasn't found in the helptext page then add it to the helptext page
			if (!fieldFound) {
				helpText_ParentNode.appendChild(getTNode(ivHelpTextDoc, field_nodeValue, null));
			}
		}

		// Loop through all of the field nodes in helptext
		// If the node isn't in the fields list then add its parent to an array for deletion
		// Loop through all the fld tags in fields
		ArrayList nodesForDeletion = new ArrayList();

		for (int i = 0; i < helpText_FieldList.getLength(); i++) {
			boolean fieldFound = false;
			Node helpText_node = helpText_FieldList.item(i);
			String helpText_nodeValue = getNodeValue(helpText_node);

			// Check if the field exists in the helptext list
			for (int j = 0; j < fields_FieldList.getLength(); j++) {
				if ((helpText_nodeValue != null)
					&& (helpText_nodeValue
						.equals(getNodeValue(fields_FieldList.item(j))))) {
					// We've found the node so we don't need to do anything - just break out of the loop
					fieldFound = true;
					break;
				}
			}

			// If the field wasn't found in the helptext page then add it to the helptext page
			if (!fieldFound) {
				nodesForDeletion.add(helpText_node);
			}
		}

		// Now delete the nodes
		for (int i = 0; i < nodesForDeletion.size(); i++) {
			Node node = (Node) nodesForDeletion.get(i);
			node.getParentNode().getParentNode().removeChild(
				node.getParentNode());
		}
	}

	// Change multi-line text to separate <p> tags
	/**
	 * Process multi line nodes.
	 * 
	 * @param sAction the s action
	 */
	public void processMultiLineNodes(String sAction) {
		if (sAction.equals("SPLIT")) {
			// Split overview and desc nodes in to <p> nodes for each line
			processMultiLineNode("ovdesc", "overview");
			processMultiLineNode("desc", "t");
		} else if (sAction.equals("JOIN")) {
			// Change multi-line <p> tags to single line text with newlines
			processSingleLineNode("ovdesc", "overview");
			processSingleLineNode("desc", "t");
		}
	}

	// Process the specified tag
	/**
	 * Process multi line node.
	 * 
	 * @param sTagName the s tag name
	 * @param sParentTagName the s parent tag name
	 */
	private void processMultiLineNode(String sTagName, String sParentTagName) {
		final String CR_PARSER_CODE = "&cr;";
		addPNodes(sTagName, sParentTagName, CR_PARSER_CODE);
	}

	/**
	 * Adds the P nodes.
	 * 
	 * @param sTagName the s tag name
	 * @param sParentTagName the s parent tag name
	 * @param sNewLineCode the s new line code
	 */
	private void addPNodes(
		String sTagName,
		String sParentTagName,
		String sNewLineCode) {
		int sNewLineCodeLen = sNewLineCode.length();

		Node nodeList_ParentNode;
		NodeList helpText_NodeList = null;
		helpText_NodeList = ivHelpTextDoc.getElementsByTagName(sTagName);

		// Loop through all the occurrences of the specified node in the list
		ArrayList nodesForAddition = null;

		for (int i = 0; i < helpText_NodeList.getLength(); i++) {
			nodesForAddition = new ArrayList();
			Node tag_node = helpText_NodeList.item(i);
			nodeList_ParentNode = tag_node.getParentNode();
			String tag_nodeValue = getNodeValue(tag_node);

			if (tag_nodeValue == null) {
				// Blank node, so delete it
				nodeList_ParentNode.removeChild(tag_node);
			} else {
				String sNewNodeValue = "";
				StringBuffer sWorkText = new StringBuffer(tag_nodeValue);
				boolean bMoreText = true;

				while (bMoreText) {
					int pos = sWorkText.toString().indexOf(sNewLineCode);
					String sLine = "";

					if (pos == -1) {
						// No more lines such just add the remainder of the string
						sLine = sWorkText.toString();
						bMoreText = false;
					} else {
						// Got another line 
						sLine = sWorkText.substring(0, pos);
						String sNewText =
							sWorkText.substring(
								pos + sNewLineCodeLen,
								sWorkText.length());
						sWorkText = null;
						sWorkText = new StringBuffer(sNewText);
					}
					if(sLine != null)
					{
						if (sLine.startsWith("<table>"))
						{
						 	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        	String wrapped = sLine;
                        	try {			
                        			    //form a list of nodes from the xml string to be added to the xml document.
                        				Document doctest = nodeList_ParentNode.getOwnerDocument();
                        	 			DocumentBuilder builder = factory.newDocumentBuilder();
                                        Document parsed = builder.parse(new InputSource(new StringReader(wrapped)));
                                        Node fragmentNode = parsed.getDocumentElement();
                                        fragmentNode = doctest.importNode(fragmentNode, true);
                                        nodesForAddition.add(fragmentNode);
                            }
                       		 catch (Exception e)
                        	{
                        		if (LOGGER.isDebugEnabled()) {
                        			LOGGER.debug(e.getMessage());
								}
                   			}
						 }
						 else
						{
							nodesForAddition.add(getPNode(ivHelpTextDoc, sLine));
						}
				  	}
				}

				// Delete the current node from the document and add a new one with the child <p> nodes
				nodeList_ParentNode.removeChild(tag_node);
				Node new_node = createNode(ivHelpTextDoc, sTagName, null);

				for (int j = 0; j < nodesForAddition.size(); j++) {
					Node pNode = (Node) nodesForAddition.get(j);
					new_node.appendChild(pNode);
				}

				// Now add the new main node to the parent
				nodeList_ParentNode.appendChild(new_node);
				nodesForAddition = null;
			}
		}
	}

	// Process the specified tag
	/**
	 * Process single line node.
	 * 
	 * @param sTagName the s tag name
	 * @param sParentTagName the s parent tag name
	 */
	private void processSingleLineNode(
		String sTagName,
		String sParentTagName) {
		removePNodes(sTagName, sParentTagName);
	}

	// Remove <p> nodes from tags joining their contents in to a single line for HTML textareas
	/**
	 * Removes the P nodes.
	 * 
	 * @param sTagName the s tag name
	 * @param sParentTagName the s parent tag name
	 */
	private void removePNodes(String sTagName, String sParentTagName) {
		final String NEW_LINE_CODE = "_CRLF_";
		Node nodeList_ParentNode;
		NodeList helpText_NodeList = null;
		String sNodeValue = null;

		if ( ivHelpTextDoc != null )
		{
			helpText_NodeList = ivHelpTextDoc.getElementsByTagName(sTagName);
	
			// Loop through all the occurrences of the specified node in the list
	
			for (int i = 0; i < helpText_NodeList.getLength(); i++) 
			{
				Node tag_node = helpText_NodeList.item(i);
				nodeList_ParentNode = tag_node.getParentNode();
				String tag_nodeValue = getNodeValue(tag_node);
				StringBuffer sNewNodeValue = new StringBuffer();
	
				// Get the list of child <p> nodes for this tag
				NodeList tag_pNodes = tag_node.getChildNodes();
	
				int noOfNodes = tag_pNodes.getLength();
				int pnode_Count = 0;
	
				for (int j = 0; j < noOfNodes; j++) {
					// Append the <p> tag value on to our string with new line characters
					Node tag_pnode = tag_pNodes.item(j);
	
					if (tag_pnode.getNodeType() == Node.ELEMENT_NODE) {
						// We have a child node
						if (pnode_Count != 0) {
							sNewNodeValue.append(NEW_LINE_CODE);
						}
						//Remove the table node string from the help text document and change it as new table node
						String nodeName = tag_pnode.getNodeName();
						if (nodeName.equals("table"))
						{
							try
							{
								Document doctest = tag_pnode.getOwnerDocument();
								NodeList fragmentNode = tag_pnode.getChildNodes();
								DocumentFragment fragment = doctest.createDocumentFragment();
								for (int f = 0; f < fragmentNode.getLength(); f++)
								{
									fragment.appendChild(fragmentNode.item(f).cloneNode(true));
								}
								StringWriter fragmentAsXMLString = new StringWriter();
								Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
								serializer.transform(new DOMSource(fragment), new StreamResult(fragmentAsXMLString));
								String fragmentXML = fragmentAsXMLString.toString();
								//string the <?xml tag from the string 
								int xmlheader = fragmentXML.indexOf(">");
								String tableContent= fragmentXML.substring(xmlheader+1);
								//the tr and td nodes in the table have leading and trailing spaces which needs to be removed.
								String tableXML[]=tableContent.split(">");
								StringBuffer finalTableXMLString = new StringBuffer();
								//append table node the final string and form the final xml string.
								finalTableXMLString.append("<table>");
								for (int t=0;t < tableXML.length;t++)
								{
									finalTableXMLString.append(tableXML[t].trim()+">");
								}
								String tNodeValue = finalTableXMLString.toString();
								sNodeValue = tNodeValue.substring(0,tNodeValue.length()-1)+"</table>";
							}
							catch(Exception e)	
							{
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(e.getMessage());
								}									
							}

					    }
						else
						{	
							sNodeValue = getNodeValue(tag_pnode);
						}
						// If the node is empty then add an empty string
						if (sNodeValue == null) {
							sNodeValue = "";
						}
	
						sNewNodeValue.append(sNodeValue);
						pnode_Count++;
					}
				}
	
				// Delete current node and add a replacement
				tag_node.getParentNode().removeChild(tag_node);
				Node new_node =
					getNewNode(ivHelpTextDoc, sTagName, sNewNodeValue.toString());
				nodeList_ParentNode.appendChild(new_node);
				sNewNodeValue = null;
			}
		}
	}

	// Converts the XML HelpText document into string form
	/**
	 * Prepare XML result.
	 */
	public void prepareXMLResult() {
					
	 try {
			// Use a Transformer for output to ensure UTF-8 characters are used
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			StreamResult htmlStreamResult =
				new StreamResult(new ByteArrayOutputStream());
			DOMSource source1 = new DOMSource(ivHelpTextDoc);
			transformer.transform(source1, htmlStreamResult);
			ByteArrayOutputStream outStream =
				(ByteArrayOutputStream) htmlStreamResult.getOutputStream();
			ivXmlHelpTextResult = outStream.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Pretty-print the XML document to make it easier to read
	/**
	 * Pretty print XML.
	 */
	public void prettyPrintXML()
	{
		// We don't want to strip out spaces and tabs at it will make it easier to read
    	// Pretty-print the output using a serializer
		OutputFormat xmlFormat = new OutputFormat("xml","utf-8",true);
        xmlFormat.setLineWidth(0);
        xmlFormat.setLineSeparator( System.getProperty("line.separator") );
                       
		try
		{
			StringWriter stringWriter = new StringWriter();
            XMLSerializer output = new XMLSerializer(stringWriter, xmlFormat);
            output.serialize(ivHelpTextDoc);
            ivXmlHelpTextResult = stringWriter.toString();
		} 
		catch(Exception e) 
		{
		}   
	}
	
		
	// Return the resulting XML HelpText after processing
	/**
	 * Gets the help text xml.
	 * 
	 * @return the help text xml
	 */
	public String getHelpTextXml() {
		return (ivXmlHelpTextResult);
	}

	// Creates a new DOM document from an xml string
	/**
	 * Creates the XML documentfrom string.
	 * 
	 * @param xmlText the xml text
	 * 
	 * @return the document
	 */
	private Document createXMLDocumentfromString(String xmlText) {
		Document doc = null;
		DocumentBuilder docBuilder = getDocumentBuilder();

		// Create the Document
		try {
			ByteArrayInputStream byteArray =
				new ByteArrayInputStream(xmlText.getBytes("UTF8"));
			doc = docBuilder.parse(byteArray);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return (doc);
	}

	// Returns the document builder to create the document with
	/**
	 * Gets the document builder.
	 * 
	 * @return the document builder
	 */
	private DocumentBuilder getDocumentBuilder() {
		// Step 1: create a DocumentBuilderFactory and configure it
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);

		//Step 2 create the DocumentBuilder
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		return (docBuilder);
	}

	// Returns the value of a node
	/**
	 * Gets the node value.
	 * 
	 * @param node the node
	 * 
	 * @return the node value
	 */
	private String getNodeValue(Node node) {
		NodeList childNodes = node.getChildNodes();
		if(childNodes.getLength() == 0) {
			return null;
		} else {
			Node requestedNode = childNodes.item(0);
			return requestedNode.getNodeValue();
		}
	}

	// Returns a node from a root node(document).  The node needs to be extracted before work
	// can be done on it.  Once extracted, any changes made to this node, will automatically
	// take effect in the main root document
	/**
	 * Gets the node.
	 * 
	 * @param contextNode the context node
	 * @param path the path
	 * 
	 * @return the node
	 */
	protected Node getNode(Node contextNode, String path) {
		try {
			Node node = XPathAPI.selectSingleNode((Node) contextNode, path);
			return node;
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the menu node.
	 * 
	 * @param doc the doc
	 * 
	 * @return the menu node
	 */
	private Node getMenuNode(Document doc) {
		Node node = createNode(doc, "menu", "");
		return node;
	}
	
	/**
	 * Gets the t node.
	 * 
	 * @param doc the doc
	 * @param fieldName the field name
	 * @param desc the desc
	 * 
	 * @return the t node
	 */
	private Node getTNode(Document doc, String fieldName, String desc) {
		Node node = createNode(doc, "t", null);
		Node node2 = addNode(doc, node, "field", fieldName);
		Node node3 = addNode(doc, node, "desc", desc);
		return node;
	}

	/**
	 * Gets the new node.
	 * 
	 * @param doc the doc
	 * @param nodeName the node name
	 * @param value the value
	 * 
	 * @return the new node
	 */
	private Node getNewNode(Document doc, String nodeName, String value) {
		Node node = createNode(doc, nodeName, value);
		return node;
	}

	/**
	 * Gets the p node.
	 * 
	 * @param doc the doc
	 * @param value the value
	 * 
	 * @return the p node
	 */
	private Node getPNode(Document doc, String value) {
		return (getNewNode(doc, "p", value));
	}

	// Creates a new node and then returns it
	/**
	 * Creates the node.
	 * 
	 * @param doc the doc
	 * @param name the name
	 * @param value the value
	 * 
	 * @return the node
	 */
	protected Node createNode(Document doc, String name, String value) {
		Element e = doc.createElement(name);
		if (value != null) {
			e.appendChild(doc.createTextNode(value));
		}
		return (Node) e;
	}

	// The Parent node is the context document.  Adds a node and a value under a specific node in the contxt doc.
	// New Sun core funtionality takes effect here.
	// You do not need to pass back the parent node.  For some reason the changes take
	// effect globally.
	/**
	 * Adds the node.
	 * 
	 * @param doc the doc
	 * @param parentNode the parent node
	 * @param nodeName the node name
	 * @param nodeValue the node value
	 * 
	 * @return the node
	 */
	protected Node addNode(
		Document doc,
		Node parentNode,
		String nodeName,
		String nodeValue) {
		Element ele = doc.createElement(nodeName);
		if (nodeValue != null) {
			ele.appendChild(doc.createTextNode(nodeValue));
		}

		parentNode.appendChild((Node) ele);
		return parentNode;
	}
}