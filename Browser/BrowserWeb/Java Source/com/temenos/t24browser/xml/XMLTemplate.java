package com.temenos.t24browser.xml;

/*
 *                      XMLTemplate.java
 *
 *This class provides the methods required for converting the data
 *stored in an HTTP request into an XML document.  It does this by reading in an XML template file
 *and then comparing and processing each of its nodes against the parameter data stored in the HTTP request passed in.
 *In general, the parameter names in the http request should correlate with the node names in the XML template file.
 *Initially a new (blank) XML document is created.  Each of the empty nodes are parsed against the HTTP request.  Once processed,
 *the XML document is stored in a string.  The xml doc is then accessed via the public method getXMLResponse
 *
 *THE FOLLOWING METHODS ARE RECCOMENDED FOR OVER RIDING WHEN CREATING A NEW CHILD CLASS:
 *1) processParameter - splits the parameters value into the required parts e.g. Name, multiValue and subValue
 *2) insertData - In the case of multiple instances of a node, use this to insert the data into the children
 *
 *A direct instance can be instantiated with a blank constructor for the purposes of extracting the value of a node
 *from a given XML string.  This is done using the method returnNodeValue(String Xml, String nodeName)
 *
 * 
 * 
 *  * Modifications:
 * 
 * 08/07/03 - Modified the structure of the method "createXmlDocument" and "createXmlDocumentfromString"
 * 				They now call "getDocumentBuilder" and "cleanXmlDoc" - both new methods
 * 			  Added an extra signature call method fo createXmlDocument - passes in a InputStream (XML)
 *			  The main constructor now calls createXmlDocumentfromString instead of createXmlDocument
 *
 * 05/11/03 - Added a blank constructor for lone manipulation e.g. adding an already existing
 * 			  	xml document for manipulation.
 * 			  Added two new methods: addNode and setRemoveNodes.  Add Nodes provides the ability
 * 			  	to add a node to an xml document
 *            setRemoveNodes enables external classes to control whether or not the xml doc
 * 	          	will have it's blank nodes removed.  
 * 			  
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLTemplate.
 */
public class XMLTemplate

{

/** The doc. */
protected Document doc = null;               //The xml document formed from the xml template file read in.

/** The XML response. */
protected String XMLResponse = "";           //The processed XML document to be returned

/** The request type. */
private String requestType = "";           //The type of template being processed

/** The cloned node. */
private Node clonedNode;                   //a clone of the child belonging to the data node

/** The new name. */
private String newName = "";               //The name of the field being processed

/** The multi value. */
private String multiValue = "";            //The multi value of the field being processed

/** The sub value. */
private String subValue = "";              //The sub value of the field being processed

/** The field instance. */
private int fieldInstance;                 //the instance of the node being processed

/** The child list. */
private ArrayList childList;                  //used in the routine checkValue - contain all element child nodes for a node

/** The schema path. */
private String schemaPath = "";            //the path to the schema

/** The xml file name. */
private String xmlFileName = "";           //the name of the xml template

/** The root directory. */
private String rootDirectory = "";         //the root directory of the web application (context directory)

/** The data node. */
private Node dataNode;                     //the node containing the repeating data fields

/** The xml workshop. */
private XMLWorkshop xmlWorkshop;           //provides a number of basic utilities required to manipulate an xml doc

/** The stored node sets. */
private ArrayList storedNodeSets;             //a list of the nodes that may have multiple instances

/** The request list. */
private ArrayList requestList;                  //A list of the parameters stored in the request

/** The type. */
private String type;                       //the type of the multiple node being processed

/** The nodes with attribs. */
private ArrayList nodesWithAttribs = new ArrayList();  //Stores nodes that need to have attributes deleted 

/** The attributes. */
private ArrayList attributes = new ArrayList();  //stores attributes for deletion 

/** The destination. */
private String destination = "";              //The intended destination for the xml doc created

/** The remove nodes. */
private boolean removeNodes = true;       //whether we want to remove blank nodes or not

/** Logger object */
private static final Logger LOGGER = LoggerFactory.getLogger(XMLTemplate.class);

        /*
         *The request contains all of the parameter info.  The xmlFileName is the name of the
         *xml template file to be used
         */
        /**
         * Instantiates a new XML template.
         * 
         * @param request the request
         * @param xmlFileName the xml file name
         */
        protected XMLTemplate(T24Request request, String xmlFileName)
        {
        	  applyChildInfo();
        	  storedNodeSets = new ArrayList();
        	  this.xmlFileName = xmlFileName;
        	  xmlWorkshop = new XMLWorkshop();
        	  rootDirectory = request.getAttribute("rootPath");
              requestType = request.getValue("requestType");
              createXMLDocumentfromString(xmlFileName);
              addDataToXMLDoc(request);            
              prepareXMLResponse();  
        }	
        
        /**
         * The request contains all of the parameter info.  The input stream contains the xml template
         * 
         * @param request the request
         * @param xml the xml
         */
        protected XMLTemplate(T24Request request, InputStream xml)
        {
        	  applyChildInfo();
        	  storedNodeSets = new ArrayList();
        	  xmlWorkshop = new XMLWorkshop();
              requestType = request.getValue("requestType");
              createXMLDocument(xml);
              addDataToXMLDoc(request);
              prepareXMLResponse();
       
        }					
			
		//what does this do? Does it extract data?
		/**
		 * Instantiates a new XML template.
		 * 
		 * @param contextPath the context path
		 * @param xmlFileName the xml file name
		 */
		protected XMLTemplate(String contextPath, String xmlFileName)
		{
			  applyChildInfo();
			  this.xmlFileName = xmlFileName;
        	  xmlWorkshop = new XMLWorkshop();
        	  rootDirectory = contextPath;
        	  
              createXMLDocument(xmlFileName);
              processXMLDoc();
              prepareXMLResponse();
		}
			
		/*
		 *Used to construct an instance for the sole purpose of extracting the value from a node
		 */
		/**
		 * Instantiates a new XML template.
		 */
		public XMLTemplate()
		{
			xmlWorkshop = new XMLWorkshop();
		}	
			
			
		/*
		 *Enables the child class to instantiate variables
		 */
		/**
		 * Apply child info.
		 */
		protected void applyChildInfo(){}
		 
			
			
        /*
         *creates a new DOM document from an file
         */
        /**
         * Creates the XML document.
         * 
         * @param docName the doc name
         */
        protected void createXMLDocument(String docName){

              String filename = getRootDirectory()+ File.separator + getXMLFileName();

              DocumentBuilder docBuilder = getDocumentBuilder();
              
              //create the Document
              try {
                  doc = docBuilder.parse(new File(filename));
                  cleanXmlDoc(docBuilder);
                  
              } catch (SAXException se) {
            	  LOGGER.error(filename, se);
              } catch (IOException ioe) {
            	  LOGGER.error(filename, ioe);
              }


        }
        
        
                
        /**
         * creates a new DOM document from an xml string.
         * 
         * @param xmlText the xml text
         */
        public void createXMLDocumentfromString(String xmlText){
 		
			  DocumentBuilder docBuilder = getDocumentBuilder();
              //create the Document
              try {
        
                  ByteArrayInputStream byteArray = new ByteArrayInputStream(xmlText.getBytes("UTF8"));
                  doc = docBuilder.parse(byteArray);
               
                  cleanXmlDoc(docBuilder);
                  
              } catch (SAXException se) {
            	  LOGGER.error(xmlText, se);
              } catch (IOException ioe) {
            	  LOGGER.error(xmlText, ioe);
              }
        }
        
        

        
        
        /**
         * creates a new DOM document from an input Stream.
         * 
         * @param xml the xml
         */
        protected void createXMLDocument(InputStream xml){

              DocumentBuilder docBuilder = getDocumentBuilder();
              
              //create the Document
              try {
        
                  doc = docBuilder.parse(xml);
                
 				  cleanXmlDoc(docBuilder);
                  
              } catch (SAXException se) {
            	  LOGGER.error(xml.toString(), se);
              } catch (IOException ioe) {
            	  LOGGER.error(xml.toString(), ioe);
              }
        }
        
        
        /**
         * returns the document builder to create the document with.
         * 
         * @return the document builder
         */
        private DocumentBuilder getDocumentBuilder(){
        	  // Step 1: create a DocumentBuilderFactory and configure it
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			  factory.setIgnoringElementContentWhitespace(true);

              //Step 2 create the DocumentBuilder
              DocumentBuilder docBuilder = null;
              try {
                  docBuilder = factory.newDocumentBuilder();
              } catch (ParserConfigurationException pce) {
            	  LOGGER.error(pce.getMessage(), pce);
              }
              return docBuilder;
        }
        
        
        /**
         * Strips out unwanted spaces from the document and then recreates the
         * new doc from it.
         * 
         * @param docBuilder the doc builder
         * 
         * @throws SAXException the SAX exception
         * @throws IOException Signals that an I/O exception has occurred.
         */
        protected void cleanXmlDoc(DocumentBuilder docBuilder)throws SAXException, IOException{
        	//we want to compress the xml doc into a less
            //verbose DOM doc - get rid of unwanted spaces.
            StringWriter stringWriter = new StringWriter();
            XMLSerializer output = new XMLSerializer(stringWriter, null);
            output.serialize(doc);
            XMLResponse = stringWriter.toString();

            //now that we have an xml string with no irrelevant spaces
            //create the actual doc we will use for adding the nodes to
            ByteArrayInputStream b = new ByteArrayInputStream(XMLResponse.getBytes("UTF8"));
            doc = docBuilder.parse(b);        
        }  



        /**
         * Creates a list of all of the root nodes, childnodes.  runs
         * through this list and checks if the childnode has any childNodes.
         * If it does then it leaves it for later processing, otherwise processes the node.
         * 
         * @param request the request
         */
        protected void addDataToXMLDoc(T24Request request){
        	
        	
              Element root = doc.getDocumentElement();
              
              schemaPath = root.getAttribute("schemaLocation");
              NodeList xmlNodeList = root.getElementsByTagName("*");
              int listLength = xmlNodeList.getLength();
              for(int i=0;i<listLength;i++){
                       Node node=xmlNodeList.item(i);
                       if (node.getNodeType()==node.ELEMENT_NODE){
                            processNode(node,request);
                       }
              }
              
              //now process multiple nodes
              for (int i=0; i<storedNodeSets.size();i++){
				XMLNodeSet nodeSet = (XMLNodeSet)storedNodeSets.get(i);
				processMultipleNode(request,nodeSet.getStoredNodes(),nodeSet.getParentName());
              }
              
        }


		/*
		 *used to process XML document.
		 */
		/**
		 * Process XML doc.
		 */
		protected void processXMLDoc()
		{
		}


        /*
         *Attempts to extract the parameter value from the http request.
         *The name of the paramter will be the same as the name of the node.
         *Then attempts to set the value of the parameter to the node.
         *
         *If the node has the potential to be a multiple node, then the node
         *is stored in a vector for processing later
         */
        /**
         * Process node.
         * 
         * @param node the node
         * @param request the request
         */
        protected void processNode(Node node,T24Request request){

              String paramName = node.getNodeName();
              String nextElement;
              String newParamName;
              if (isMultipleNode(node)){
              	storeNode(node);
              }
              else if (request.getValue(paramName)!=null){
					// Clean the parameter value in case it was a text field with carriage returns
					String paramValue = cleanXML( request.getValue(paramName) );
                    xmlWorkshop.setNodeValue(doc,node,paramValue);
              }
              //this is to catch parameters that have been added in the servlet to the 
              //request (can only be set as attributes)
              else if(request.getAttribute(paramName)!=null){
                	xmlWorkshop.setNodeValue(doc,node,(String)request.getAttribute(paramName));
              }
        }



		/*
		 * Stores the node for processing later
		 *
		 */
		/**
		 * Store node.
		 * 
		 * @param node the node
		 */
		protected void storeNode(Node node){

			boolean nodeSetFound = false;
			String parentName = node.getParentNode().getNodeName();
			
				
			//check to see if there are any node sets
			if (storedNodeSets.size()>0){
				//run through each of the node sets
				for (int i=0; i<storedNodeSets.size();i++){
					XMLNodeSet nodeSet = (XMLNodeSet)storedNodeSets.get(i);
					//if the parent name of the node set = the nodes parent name
					if(nodeSet.getParentName().equals(parentName)){
						//add the node to the nodes set
						((XMLNodeSet)(storedNodeSets.get(i))).addNode(node);
						nodeSetFound = true;
						break;
					}
				}
			}
			else if (nodeSetFound == false){
				storedNodeSets.add(new XMLNodeSet(node));
				nodeSetFound = true;
			}
			
			
			//check to see if the node has been added to a node set yet.  if
			//not then add another node set
			if (nodeSetFound == false){
				storedNodeSets.add(new XMLNodeSet(node));
			}
		}



		/*
         *returns whether the parent node is a multiple node
         *in the xml document
         */
        /**
		 * Checks if is multiple node.
		 * 
		 * @param node the node
		 * 
		 * @return true, if is multiple node
		 */
		protected boolean isMultipleNode(Node node){
              Node parentNode = node.getParentNode();
              if (parentNode.hasAttributes()==true){
              	NamedNodeMap map = parentNode.getAttributes();
              	for (int i=0;i<map.getLength();i++){
              		if(map.item(i).getNodeName().equals("multiple")){
                		return true;
                	}
              		else{
                		return false;
        			}
              	}
              }
              return false;
        }



		/*
		 *When the node is designated to be a multiple node then this method will be called to process it.
		 *Runs through a list of nodes and processes them agaist the parms stored in the requestList.  The
		 *type indicates the grandparent nodes that the code is currently dealing with
		 */
		/**
		 * Process multiple node.
		 * 
		 * @param request the request
		 * @param nodes the nodes
		 * @param type the type
		 */
		public void processMultipleNode(T24Request request, ArrayList nodes, String type){

			//get the list of all the parameters from the request
                        requestList = request.getSortedParameterList();

			fieldInstance = 0;
			this.type = type;
			String paramName;
			String nextElement;
			String newParamName;

			for(int j=0;j<nodes.size();j++){
			  
			  Node node = (Node)nodes.get(j);
			  paramName = node.getNodeName();
              for(int i=0;i<requestList.size();i++){
                        nextElement = (String)requestList.get(i);
                        if (nextElement.startsWith(paramName)){
                            newParamName = nextElement;             
                            processField(node, paramName,newParamName,request);
                        }
              }  
			} 
			     			       
		}	
		

        /*
         *processes each field in turn.
         */
        /**
         * Process field.
         * 
         * @param node the node
         * @param paramName the param name
         * @param newParamName the new param name
         * @param request the request
         */
        protected void processField(Node node, String paramName,String newParamName,T24Request request){
              String value = "";

              //creates a clone of the data node
              Node clonedNode = node.getParentNode().cloneNode(true);
              
              
              dataNode = getDataNode(node);
              NodeList children = dataNode.getChildNodes();

              processParameter(newParamName, request);
              value = checkValue(dataNode,paramName);


              if ((value == null)||(value.equals(""))){
                  processData(request,dataNode,paramName,newParamName);
              }
              else{
                  dataNode.appendChild(clonedNode);
                  processData(request,dataNode,paramName,newParamName);

              }
        }



        /*
         *use this method to process the parameter passed in via the HTTP request.  All standard 
         *multiple nodes will use this.  The code expects the parameter to be made up (at least) of the 
         *following - "The name of the node applicable":"The text to add to that node"
         */
        /**
         * Process parameter.
         * 
         * @param newParamName the new param name
         * @param request the request
         */
        protected void processParameter(String newParamName,T24Request request){

              StringTokenizer tokens = new StringTokenizer(newParamName,":");
			  try{
			  		  //the node name
                      String parameterToken = (String)tokens.nextElement();
                      //the text to be added 
                      newName = (String)tokens.nextElement();
			  }
			  catch(Exception e){
		              e.getMessage();
			  		  e.printStackTrace();
              }

        }
        

        /*
         *Checks the value of a specified instance of a node.  If no node is found at the
         *specified position then throws and catches a nullPointerException and returns the
         *string "New node required".  Returns null if the value of the node is null.  The
         *end return should in theory never be reached.
         */
        /**
         * Check value.
         * 
         * @param parentsParentNode the parents parent node
         * @param paramName the param name
         * 
         * @return the string
         */
        protected String checkValue(Node parentsParentNode, String paramName){
              try{
                  //gets all of the child nodes of the data node
                  NodeList list = parentsParentNode.getChildNodes();         
	              //strip out all of the text nodes etc (we just want ELEMENTS)
              	  ArrayList validNodes = this.getElementNodes(list); 
                  //get the child instance from the now clean list of nodes
                  //this will throw an arrayindexoutofbounds Exception if there is no childnode at this instance
                  Node childNode = (Node)validNodes.get(fieldInstance);
                  //get a list of all the child nodes for the child node.
                  childList = this.getElementNodes(childNode.getChildNodes());
                  //run each of the childs child nodes to check if they match paramName
                  int numberOfChildren = childList.size();
                  //go through each of the children in the vector
                  for (int i=0;i<numberOfChildren;i++){
                  	  Node node = ((Node)childList.get(i));
                      if (node.getNodeName().equals(paramName)){
                          if(xmlWorkshop.getNodeValue(node)==null){
                              return null;
                          }
                      }
                  }
              }
              catch (IndexOutOfBoundsException n){
                  return "New node required";
              }
              return "New node required";
        }




		/*
		 *returns all of the element nodes from a nodelist in the form of a Vector
		 */
		 /**
		 * Gets the element nodes.
		 * 
		 * @param tempList the temp list
		 * 
		 * @return the element nodes
		 */
		protected ArrayList getElementNodes(NodeList tempList){
		 	
		 		//get rid of all nodes from the list that are not ELEMENT_NODEs
                int listLength = tempList.getLength();
                ArrayList validNodes = new ArrayList();
                for(int i=0;i<=(listLength-1);i++){
                	if (tempList.item(i).getNodeType()==Node.ELEMENT_NODE){ 
                  		validNodes.add(tempList.item(i));
                 	}
                }
                
                return validNodes;
		 }
                  	


        /*
         *gets the parent node from the parentsParentNode passed into the method.  retrieves all of the child nodes
         *from the parent node, then runs through these nodes to see if any of them match up to the parameter being
         *processed. Finally, tries to insert data. 
         */
        /**
         * Process data.
         * 
         * @param request the request
         * @param parentsParentNode the parents parent node
         * @param paramName the param name
         * @param newParamName the new param name
         */
        protected void processData(T24Request request, Node parentsParentNode, String paramName, String newParamName){
              //gets all of the child nodes of the data node
              NodeList list = parentsParentNode.getChildNodes();
              //strip out all of the text nodes etc(we just want ELEMENTS)
              ArrayList validNodes = this.getElementNodes(list);           
              //get an element in the list.  The very last one is ignored because it is #text
              Node childNode = (Node)validNodes.get(fieldInstance);
              //get a list of all the child nodes for the child node.
              childList = this.getElementNodes(childNode.getChildNodes());
              //run each of the childs child nodes
              insertData(request,newParamName,paramName,childList);
        }

        
        
        //enters the parameter values into applicable node. paramName is not used here.
        /**
         * Insert data.
         * 
         * @param request the request
         * @param newParamName the new param name
         * @param paramName the param name
         * @param childList the child list
         */
        protected void insertData(T24Request request,String newParamName, String paramName, ArrayList childList){
          
              checkSetNodeValue((Node)childList.get(0),newName);
              checkSetNodeValue((Node)childList.get(1),request.getValue(newParamName));

              fieldInstance = fieldInstance+1;
        }



        /*
         *checks to see if a node is empty before adding the value.
         *if it already has a value then it replaces it
         */
        /**
         * Check set node value.
         * 
         * @param node the node
         * @param value the value
         */
        protected void checkSetNodeValue(Node node, String value){
        	
        	  
              value = cleanXML(value);
       		
              if(xmlWorkshop.getNodeValue(node)==null){
                  xmlWorkshop.setNodeValue(doc,node,value);
              }
              else{
                  //if there is already a value present in the node
                  xmlWorkshop.replaceNodeValue(doc,node,value);
              }
        }


		//Deals with values with carriage returns.  Replaces cr with xml specific code.
		/**
		 * Clean XML.
		 * 
		 * @param value the value
		 * 
		 * @return the string
		 */
		protected String cleanXML(String value)
		{
			if ( value != null )
			{
				String crChar = "" + (char)13;
				String newLineChar = "" + (char)10;
				String newLineCarriageChar = "" + (char)13 + (char)10;
				String crXml = "&cr;";
			
				String escapedValue = Utils.replaceAll( value, newLineCarriageChar, crXml );
				escapedValue = Utils.replaceAll( escapedValue, newLineChar, crXml );
				escapedValue = Utils.replaceAll( escapedValue, crChar, crXml );
				return( escapedValue );
			}
			else
			{
				return value;	
			}
		}


        /*
         *returns whether the parent of the parents node is the data node
         *in the xml document
         */
        /**
         * Checks if is data node child.
         * 
         * @param node the node
         * 
         * @return true, if is data node child
         */
        protected boolean isDataNodeChild(Node node){
              Node contextNode = getDataNode(node);
              if (contextNode.getNodeName().equals("messageData"))
                return true;
              else
                return false;
        }


		/*
		 *sets the dataNode
		 */
		/**
		 * Sets the data node.
		 * 
		 * @param dataNode the new data node
		 */
		protected void setDataNode(Node dataNode){
			this.dataNode = dataNode;
		}

        /*
         *calculates the data node from a node
         */
        /**
         * Gets the data node.
         * 
         * @param node the node
         * 
         * @return the data node
         */
        protected Node getDataNode(Node node){
              Node parentNode = node.getParentNode();
              Node parentsParentNode = parentNode.getParentNode();
              return parentsParentNode;
        }

        
        /*
         * returns the data node
         */
         /**
         * Gets the data node.
         * 
         * @return the data node
         */
        protected Node getDataNode(){
         	return dataNode;
         }


        /*
         *converts the xml doc into string form for processing and printing
         */
        /**
         * Prepare XML response.
         */
        public void prepareXMLResponse()
        {	
			try
			{
				if (removeNodes == true)
				{	             
           			extractBlankNodes();					
          	  	}
				
				// Use a Transformer for output to ensure UTF-8 characters are used
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				StreamResult htmlStreamResult = new StreamResult( new ByteArrayOutputStream() );
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, htmlStreamResult);
				ByteArrayOutputStream outStream = (ByteArrayOutputStream) htmlStreamResult.getOutputStream();
				XMLResponse = outStream.toString( "UTF-8" );
    		}
            catch(Exception e)
            {
            	e.printStackTrace();
            }
        }


        //returns the name of the xml file
        /**
         * Gets the XML file name.
         * 
         * @return the XML file name
         */
        public String getXMLFileName(){
            return xmlFileName;
        }



        //returns the path to the schema
        /**
         * Gets the schema location.
         * 
         * @return the schema location
         */
        public String getSchemaLocation(){
            return schemaPath;
        }



        /*
         *returns the xml response
         */
        /**
         * Gets the XML response.
         * 
         * @return the XML response
         */
        public String getXMLResponse(){
              return XMLResponse;
        }



		/*
		 *returns the root directory of the web application
		 */
		/**
		 * Gets the root directory.
		 * 
		 * @return the root directory
		 */
		public String getRootDirectory(){
			  return rootDirectory;
		} 
		
		
		

		/*
		 * Runs through the doc and extracts any blank nodes and attributes
		 */
		 /**
		 * Extract blank nodes.
		 */
		protected void extractBlankNodes(){
		 	
		 	  ArrayList nodes = getBlankNodes();
		 	  removeBlankNodes(nodes);
		 	  removeUnwantedSpaces();
		 }
		 	  
		
		
		
		/*
		 * Returns a list of all the nodes with blank values.
		 * Also removes any attributes from the nodes with values
		 */
		 /**
		 * Gets the blank nodes.
		 * 
		 * @return the blank nodes
		 */
		protected ArrayList getBlankNodes(){
		 	
			  //create the vector to store nodes with no values in 
		 	  ArrayList nodes = new ArrayList();
		 	  int nodeCount = 0;
		 	
		 	  //get the root element of the xml doc
		 	  Element root = doc.getDocumentElement();
		 	  
		 	  //get a list of all the elements under that root node
              NodeList xmlNodeList = root.getElementsByTagName("*");
              int listLength = xmlNodeList.getLength();
              
              //run through each of the elements in the list
              for(int i=0;i<listLength;i++){
                       Node node=xmlNodeList.item(i);
                       if (node.getNodeType()==node.ELEMENT_NODE){
                       		//get the value of the node
                       		String value = xmlWorkshop.getNodeValue(node);
                       		//store all the nodes with blank values for processing later - attributes
                       		//will be removed later in the code
                            if((value==null)||(value.equals(""))){
                            	if(nodeCount==0)
                            	{
                            		nodes.add(node);
                            	}
                            }
                            //clean up - remove all of the attributes from the xml doc for brevity's sake
                            //we no longer need them for nodes that we are keeping at this point
                            else{
              					if (node.hasAttributes()==true){
              						NamedNodeMap map = node.getAttributes();
              						for (int j=0;j<map.getLength();j++){	
              							removeAttribute(map.item(j),node);
              						}
                				}
              				}
                       }
              } 
              
              return nodes;
		 }
		 
		 
		 
              
         /*
          *runs through each of the stored nodes and deletes them, if not required.
          *if the node is blank and required then it just removes the node's attributes;
          *we no longer need the attributes after this point
          */
          /**
          * Removes the blank nodes.
          * 
          * @param nodes the nodes
          */
         protected void removeBlankNodes(ArrayList nodes){
               
              try{
              	//run through each stored node
              	for (int i=0;i<nodes.size();i++){
     
              		boolean removeNode = true;
              		Node node = (Node)nodes.get(i);
	
					//when the node has attributes
              		if (node.hasAttributes()==true){
              			
              			//get all of the nodes attributes
              			NamedNodeMap attrList = node.getAttributes();
              			int attrListLength = attrList.getLength();
    
    					//run through each of the attributes
              			for (int j=0;j<attrListLength;j++){
          					
          					Node attribute = attrList.item(j);
          					
          					//if the node is "required" then we don't want to remove it
              				if(attribute.getNodeName().equals("required")){
                				removeNode = false;
                				//but we want to remove the attribute for brevity's sake
                				//so store it for later deletion - we need to keep this 
                				//attribute for the processing of parent node deletion
                				//we need to know if the parent children are required.
              					storeAttributeForRemoval(attribute,node);
                			}
                			else{
                				//all attributes should be removed
                				removeAttribute(attribute,node);
                			}
              			}
              			if(removeNode){
              				nodes = removeNodeAndCheckParent(nodes,node);
              			}
              		}
              		//when the node doesn't have attributes
              		else{
              			nodes = removeNodeAndCheckParent(nodes,node);
              		}
              	}
              	
              	//All attributes need to be removed from xml doc, so get rid of the required attribute
              	removeAttributes();
              	
              }
              catch(Exception e){
              	e.printStackTrace();
              }
		 	
		 }


		/**
		 * removes any null text nodes left behind by node deletion.
		 */
		public void removeUnwantedSpaces(){
			
				if ( doc != null )
				{
					Element root = doc.getDocumentElement();

					if ( root != null )
					{
						root.normalize();
						
						//need to get all of the child nodes for this document
						NodeList list= root.getChildNodes();
						
						//go through each of the child nodes to see if it is a null TEXT_NODE
						for (int i=0;i<list.getLength();i++){
							Node node = list.item(i);
							if ((node.getNodeType()==Node.TEXT_NODE)&&((xmlWorkshop.getNodeValue(node)==null)||(xmlWorkshop.getNodeValue(node).equals("")))){
								//delete the null text node
								xmlWorkshop.removeNode(node);
							}
						}
					}
				}
		}
	
	
	
		/*
		 *removes the node and checks to see if its parent should
		 *be removed i.e. has only empty child nodes that are not required.
		 *if the parent node is to be deleted then it adds it to the Vector 
		 *nodes.  Returns that vector.
		 */
		/**
		 * Removes the node and check parent.
		 * 
		 * @param nodes the nodes
		 * @param node the node
		 * 
		 * @return the array list
		 */
		public ArrayList removeNodeAndCheckParent(ArrayList nodes, Node node){
					
			if ( node != null )
			{
				Node parentNode = node.getParentNode();
				
				if ( parentNode != null )
				{
					//first of all remove the node in question
		            parentNode.removeChild(node);
		            //check to see if the parent node's children are now empty
		            //and if so add to the vector nodes for later deletion
		            if(parentNodeDeletionCheck(parentNode))
		            {
		            	if ( nodes != null )
		            	{
			              	if(!nodes.contains(parentNode))
			              	{
			              		nodes.add(parentNode);
		    	          	}
		            	}
		            }
				}
			}
            return nodes;
		}
	
	
	
	
		//stores an attribute for removal, along with the node to remove it from
		/**
		 * Store attribute for removal.
		 * 
		 * @param attribute the attribute
		 * @param node the node
		 */
		protected void storeAttributeForRemoval(Node attribute, Node node){
			nodesWithAttribs.add(node);
			attributes.add(attribute);
		}
		
		
		
				
		//removes the attributes from node stored earlier
		/**
		 * Removes the attributes.
		 */
		protected void removeAttributes(){
			int size = nodesWithAttribs.size();
			for (int i=0;i<size;i++){
            	removeAttribute((Node)attributes.get(i),(Node)nodesWithAttribs.get(i));
			}
		}

	
	

		//removes an attribute from a node
		/**
		 * Removes the attribute.
		 * 
		 * @param attribute the attribute
		 * @param node the node
		 */
		protected void removeAttribute(Node attribute, Node node){
			Element nodeEle = (Element)node;
            nodeEle.removeAttribute(attribute.getNodeName());
		}
		
		
				
		
		/*
		 *Checks to see if the parent nodes children are all empty and if
		 *so then returns true.
		 */
		/**
		 * Parent node deletion check.
		 * 
		 * @param node the node
		 * 
		 * @return true, if successful
		 */
		protected boolean parentNodeDeletionCheck(Node node){
			  //gets all of the child nodes of the  node
              NodeList list = node.getChildNodes();
              //strip out all of the text nodes etc(we just want ELEMENTS)
              ArrayList validNodes = this.getElementNodes(list);
              //for each valid child node
              for(int i=0;i<validNodes.size();i++){
              	Node childNode = (Node)validNodes.get(i);
              	String value = xmlWorkshop.getNodeValue(childNode); 
              	if((value!="")&&(value!=null)){
              		return false;
              	}
              	//if the parent has a child with the attribte "required" then we don't 
              	//want to delete it 
              	else if(((Element)childNode).getAttribute("required").equals("true")){
              		return false;
              	}
              }  
              
              return true;               
		} 



		/**
		 * sets the destination for the xml document being created.
		 * 
		 * @param destination the destination
		 */
		protected void setDestination(String destination){
			this.destination = destination;
		}
		
		
		/**
		 * sets the destination for the xml document being created.
		 * 
		 * @return the destination
		 */
		public String getDestination(){
			return destination;
		}
		
		
		

//---------------------------------------------------------------------------------------
//METHODS USED TO RETRIEVE THE VALUE OF A NODE
//these methods are still work in progress.  Perhaps should be moved to XMLWorkshop
//---------------------------------------------------------------------------------------

		/*
		 *passes in an xml document and a path to a node; returns the value of the node
		 */
		/**
 * Return node value.
 * 
 * @param xml the xml
 * @param nodeName the node name
 * 
 * @return the string
 */
public String returnNodeValue(String xml, String nodeName){
			
			String value = "";
			generateXMLDocument(xml);
			//String value = xmlWorkshop.getNodeValue(nodeName); 
			
			return value;		
			
		}
		
		
		/*
         *creates a new DOM document from an actual xml string
         */
        /**
		 * Generate XML document.
		 * 
		 * @param xml the xml
		 */
		protected void generateXMLDocument(String xml){

              // Step 1: create a DocumentBuilderFactory and configure it
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

              //Step 2 create the DocumentBuilder
              DocumentBuilder docBuilder = null;
              try {
                  docBuilder = factory.newDocumentBuilder();
              } catch (ParserConfigurationException pce) {
            	  LOGGER.error(xml, pce);
              }

              // Step 3: create the Document
              try {
              	  InputSource is = new InputSource(new StringReader(xml));
                  doc = docBuilder.parse(is);
              } catch (SAXException se) {
            	  LOGGER.error(xml,se);
              } catch (IOException ioe) {
            	  LOGGER.error(ioe.getMessage(),ioe);
              }

        }			
			
			
		/*
         *creates a list of all of the root nodes childnodes.  runs
         *through this list and checks id the childnode has childNodes.
         *If it does then it leaves it, otherwise processes the node.
         */
        /**
		 * Gets the value.
		 * 
		 * @param nodeName the node name
		 * 
		 * @return the value
		 */
		protected String getValue(String nodeName){

			  String value = "";
              Element root = doc.getDocumentElement();
              schemaPath = root.getAttribute("schemaLocation");
              NodeList xmlNodeList = root.getElementsByTagName("*");
              int listLength = xmlNodeList.getLength();
              for(int i=0;i<listLength;i++){
                       Node node=xmlNodeList.item(i);
                       if ((node.getNodeType()==node.ELEMENT_NODE)&&(node.getNodeName().equals(nodeName))){
                            value = xmlWorkshop.getNodeValue(node);
                       }
              }
              
              return value;
        }
        
        /**
         * Adds a node to the existing xml document.
         * 
         * @param nodeName the node name
         * @param nodeValue the node value
         */
        public void addNode(String nodeName, String nodeValue){
        	xmlWorkshop.addNode(doc,doc.getDocumentElement(),nodeName,nodeValue);
        }

		
		/**
		 * Enables the user to switch the remove node processing indicator
		 * on or off.
		 * 
		 * @param state the state
		 */        
        public void setRemoveNodes(boolean state){
        	removeNodes = state;
        }
        
        
        /**
         * Returns the existing xml document.
         * 
         * @return the document
         */
        protected Document getDocument(){
        	return doc;
		}
		
		/**
		 * Returns the new name of the field being processed.
		 * 
		 * @return the new name
		 */
        protected String getNewName(){
        	return newName;
		}
		
		/**
		 * Sets the new name of the field being processed.
		 * 
		 * @param newName the new name
		 */
        protected void setNewName(String newName){
        	this.newName = newName;
		}
		
		/**
		 * Returns the multi value of the field.
		 * 
		 * @return the multi value
		 */
        protected String getMultiValue(){
        	return multiValue;
		}
		
		/**
		 * Sets the multi value of the field.
		 * 
		 * @param multiValue the multi value
		 */
        protected void setMultiValue(String multiValue){
        	this.multiValue = multiValue;
		}
		
		/**
		 * Returns the sub value of the field.
		 * 
		 * @return the sub value
		 */
        protected String getSubValue(){
        	return subValue;
		}
		
		/**
		 * Sets the sub value of the field.
		 * 
		 * @param subValue the sub value
		 */
        protected void setSubValue(String subValue){
        	this.subValue = subValue;
		}
		
		/**
		 * Returns the field instance.
		 * 
		 * @return the field instance
		 */
        protected int getFieldInstance(){
        	return fieldInstance;
		}
		
		/**
		 * Sets the field instance.
		 * 
		 * @param fieldInstance the field instance
		 */
        protected void setFieldInstance(int fieldInstance){
        	this.fieldInstance = fieldInstance;
		}
		
		/**
		 * Returns the xml workshop.
		 * 
		 * @return the xml workshop
		 */
        protected XMLWorkshop getXmlWorkshop(){
        	return xmlWorkshop;
		}
		
		/**
		 * Returns the type of the field.
		 * 
		 * @return the type
		 */
        protected String getType(){
        	return type;
		}
		

		/**
		 * Sets the field type.
		 * 
		 * @param type the type
		 */
        protected void setType(String type){
        	this.type = type;
		}
}
	
	
		