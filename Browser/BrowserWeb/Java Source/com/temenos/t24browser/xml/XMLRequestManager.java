package com.temenos.t24browser.xml;

/*
 *                      XMLRequestManager.java
 *Manages the parsing of an HTTP request into an xml doc.  Provides methods for
 *retrieving the xml created.
 *
 *Also provides methods for retrieving specific details from the xml doc e.g. SchemaLocation, via
 *getter methods.
 *
 *The http request is parserd according to the value set in the attribute "requestType".  
 *
 * Modifications:
 * 
 *  08/07/03 - Added a parameter to the constructor.  Now passes in XMLTemplateManager
 * 			   	Passes this parameter in the Constructor of all the XMLTemplate child classes
 *
 * 
 *  21/08/03 - Changed the request type test to look for anything that starts with
 * 			   	OFS.ENQUIRY.
 * 
 *  05/11/03 - Added the method addNode.  This allows a node and a value to be
 * 			   	added to an existing xml document.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.temenos.t24browser.request.T24Request;
// TODO: Auto-generated Javadoc

/**
 * The Class XMLRequestManager.
 */
public class XMLRequestManager

{

/** The xml. */
private XMLTemplate xml = null;        //creates and populates the correct xml doc from the HTTP request

/** The elapsed time. */
private long elapsedTime;              //the time taken to parser the request
	
	/** The iv request. */
	private T24Request ivRequest = null;	// A T24 request
        /*
         * Used to read in blank XML template
         */
        /**
         * Instantiates a new XML request manager.
         * 
         * @param request the request
         * @param templates the templates
         * 
         * @throws XMLRequestTypeException the XML request type exception
         */
        public XMLRequestManager(HttpServletRequest request, XMLTemplateManager templates)
        	throws XMLRequestTypeException
        {
        	ivRequest = new T24Request( request );
        	processRequest( templates );
        }
        
        /**
         * Instantiates a new XML request manager.
         * 
         * @param request the request
         * @param templates the templates
         * 
         * @throws XMLRequestTypeException the XML request type exception
         */
        public XMLRequestManager(T24Request request, XMLTemplateManager templates)
        	throws XMLRequestTypeException
        {
        	ivRequest = request;
        	processRequest( templates );
        }
        
        /**
         * Process request.
         * 
         * @param templates the templates
         * 
         * @throws XMLRequestTypeException the XML request type exception
         */
        private void processRequest( XMLTemplateManager templates)
    	throws XMLRequestTypeException
    	{
        	  long startTime = System.currentTimeMillis();
              String requestType = ivRequest.getValue("requestType");

              if (requestType.startsWith("OFS.ENQUIRY")){
                xml = new XMLEnquiry(ivRequest,templates);
              }
              else if ((requestType.equals("OFS.APPLICATION"))||(requestType.equals("OFS.OS.CONTEXT"))||(requestType.equals("OFS.OS.COPY"))){
                xml = new XMLApplication(ivRequest, templates);
              }
              else if ((requestType.equals("OFS.OS.EXPAND.VALUE"))||(requestType.equals("OFS.OS.DELETE.VALUE"))){
                xml = new XMLApplication(ivRequest, templates);
              }
              else if ((requestType.equals("OFS.PRINT.DEAL.SLIP"))||(requestType.equals("OFS.GET.DEAL.SLIP"))){
                xml = new XMLApplication(ivRequest, templates);
              }
              else if (requestType.equals("CREATE.SESSION")){
              	xml = new XMLSession(ivRequest, templates);
              }              	
              else if (requestType.equals("DESTROY.SESSION")){
              	xml = new XMLUtilityRoutine(ivRequest, templates);
              }              	
              else if (requestType.equals("UTILITY.ROUTINE")){
              	xml = new XMLUtilityRoutine(ivRequest, templates);
              }
              else if (requestType.equals("CREATE.PARAMETERS")){
              	xml = new XMLCreateParameters(ivRequest, templates);
              }
              else if (requestType.equals("NO.REQUEST")){
                xml = new XMLUtilityRoutine(ivRequest, templates);
              }
              else if (requestType.equals("API.REQUEST")){
              	xml = new APIRequest(ivRequest, templates);
              }
              else if (requestType.equals("HELP.SAVE")){
              	xml = new XMLHelpDetails(ivRequest, templates);
              }
              else
              {
              	// Invalid request type so throw exception
              	throw new XMLRequestTypeException( "Unable to process the request" );
              }
        
              long stopTime = System.currentTimeMillis(); 
              elapsedTime = stopTime - startTime;
        }
        
        /*
         * Used to read in populated XML document
         */
        /**
         * Instantiates a new XML request manager.
         * 
         * @param contextPath the context path
         * @param requestType the request type
         * @param fileName the file name
         * 
         * @throws XMLRequestTypeException the XML request type exception
         */
        public XMLRequestManager( String contextPath, String requestType, String fileName )
        	throws XMLRequestTypeException
        {
        	
        	if (requestType.equals("PARAMETERS"))
        	{
              xml = new XMLParameters( contextPath, fileName );
            }
        	else if (requestType.equals("HELP.PARAMETERS"))
         	{
               xml = new XMLHelpParameters( contextPath, fileName );
            }
        	else
            {
              // Invalid request type so throw exception
              throw new XMLRequestTypeException( "Unable to process the request" );
            }
        }
        
        /**
         * Instantiates a new XML request manager.
         */
        public XMLRequestManager(){
        }


        //returns the xml document created
        /**
         * Gets the XML response.
         * 
         * @return the XML response
         */
        public String getXMLResponse(){
              return xml.getXMLResponse();
        }
        
        /**
         * Gets the parameters table.
         * 
         * @return the parameters table
         */
        public Hashtable getParametersTable()
        {
        	  return( ( (XMLParameters) xml).getParametersTable() );
        }
        
        /**
         * Gets the view help locations table.
         * 
         * @return the view help locations table
         */
        public ArrayList getViewHelpLocationsTable()
        {
        	  return( ( (XMLHelpParameters) xml).getHelpViewLocationsTable() );
        }
        
        /**
         * Gets the edit help locations table.
         * 
         * @return the edit help locations table
         */
        public ArrayList getEditHelpLocationsTable()
        {
        	  return( ( (XMLHelpParameters) xml).getHelpEditLocationsTable() );
        }

		/**
		 * Gets the help languages table.
		 * 
		 * @return the help languages table
		 */
		public ArrayList getHelpLanguagesTable()
        {
        	  return( ( (XMLHelpParameters) xml).getHelpLanguagesTable() );
        }
        
        /**
         * Gets the help images directory.
         * 
         * @return the help images directory
         */
        public String getHelpImagesDirectory()
        {
        	  return( ( (XMLHelpParameters) xml).getHelpImagesDirectory() );
        }
        
        /**
         * Help pages editable.
         * 
         * @return true, if successful
         */
        public boolean helpPagesEditable()
        {
        	  return( ( (XMLHelpParameters) xml).helpPagesEditable() );
        }
        
        /**
         * Help development mode.
         * 
         * @return true, if successful
         */
        public boolean helpDevelopmentMode()
        {
        	  return( ( (XMLHelpParameters) xml).helpDevelopmentMode() );
        }

        //returns the path to the schema of the xml doc created
        /**
         * Gets the schema location.
         * 
         * @return the schema location
         */
        public String getSchemaLocation(){
              return xml.getSchemaLocation();
        }


		//returns the directory structure(starting from the context directory) and the name of the xml file 
        /**
		 * Gets the XML file name.
		 * 
		 * @return the XML file name
		 */
		public String getXMLFileName(){
              return xml.getXMLFileName();
        }
        
		
		/*
		 *returns the root directory that the web application
		 */
		/**
		 * Gets the root directory.
		 * 
		 * @return the root directory
		 */
		public String getRootDirectory(){
			  return xml.getRootDirectory();
		}   
		
		
		/**
		 * returns the intended destination for the xml doc created.
		 * 
		 * @return the destination
		 */
		public String getDestination(){
			  return xml.getDestination();
		}   
		
		
		/*
		 *returns the value of a node in a given xml String
		 */
		/*public String getNodeValue(String xml, String nodeName){
			  XMLTemplate xmlTool = new XMLTemplate();
			  return xmlTool.returnNodeValue(xml,nodeName);
		}*/  
		
		
		/**
		 * returns the value of a node in a given xml String.
		 * 
		 * @param xml the xml
		 * @param nodeName the node name
		 * 
		 * @return the node value
		 * 
		 * @throws XMLRequestManagerException the XML request manager exception
		 */
		public String getNodeValue(String xml, String nodeName) throws XMLRequestManagerException{
			  
			  try{
			  	//check to see if the tag is empty
			  	int emptyTag = xml.indexOf("<"+nodeName+"/>");
			  	if ( emptyTag != -1)
			  	{
			  		//it exists, so the node is blank
			  		return( null );
			  	}
			  	
			  	int nodeLength = nodeName.length();
			  	int startTagPos = xml.indexOf("<"+nodeName+">");
			  	int endTagPos = xml.indexOf("</"+nodeName+">");
			  
			  	if ( ( startTagPos == -1 ) || ( endTagPos == -1 ) )
			  	{
			  		return( null );
			  	}
			  	else
			  	{
			  		String result = xml.substring((startTagPos+nodeLength+2),(endTagPos));
			  		return result;
			  	}
			  }
			  catch(StringIndexOutOfBoundsException e){
			  	throw new XMLRequestManagerException("Invalid response recieved: Could not find the node '"+nodeName+ "' in the response.");  
			  }  	
		} 
	 

		/**
		 * Checks to see if an xml doc contains a valid specific node.
		 * 
		 * @param xml the xml
		 * @param nodeName the node name
		 * 
		 * @return true, if node check
		 */
		public boolean nodeCheck(String xml, String nodeName) {
			  	
			  //check to see if the tag is empty
			  int emptyTag = xml.indexOf("<"+nodeName+"/>");
			  if ( emptyTag != -1)
			  {
			  	//it exists, so the node is blank
			  	return true;
			  }
			  
			  int startTagPos = xml.indexOf("<"+nodeName+">");
			  int endTagPos = xml.lastIndexOf("</"+nodeName+">");
			  
			  if ( ( startTagPos == -1 ) || ( endTagPos == -1 ) )
			  {
			  	return false;
			  }
			  else
			  {
			  	return true;
			  }
		} 
		
		
		/**
		 * Adds a node to an xml document.
		 * 
		 * @param xml the xml
		 * @param nodeName the node name
		 * @param value the value
		 * 
		 * @return the string
		 */
		public String addNode(String xml, String nodeName, String value){	
			//create a new instance of XMLTemplate to add the existing xml to
			XMLTemplate xmlDocument = new XMLTemplate();
			xmlDocument.createXMLDocumentfromString(xml);
			xmlDocument.addNode(nodeName,value);
			//we don't want to remove any nodes so set removeNodes to false
			xmlDocument.setRemoveNodes(false);
			xmlDocument.prepareXMLResponse();
			return xmlDocument.getXMLResponse();		
		}
	 	
	 	/**
	 	 * Add an xml fragment to a xml document after the first instance of a given node.
	 	 * 
	 	 * @param fragment the fragment
	 	 * @param node the node
	 	 * @param doc the doc
	 	 * 
	 	 * @return the string
	 	 */
		public String addFragmentAtNode(String fragment, String node, String doc){

			String myTag = "<" + node +">";		// Create the tag from the node name
			int pos = doc.indexOf(myTag);		// Find the start position of the tag
			if(pos< 0){
				return doc;					// Can't find node - jsut return original
			}
			// Return the doc upto the node, plus the fragment, plus the rest of the doc
			String myReturn = doc.substring(0,pos + myTag.length()) + fragment + doc.substring(pos + myTag.length());
						
			return myReturn;		
				
		}

		/**
		 * Returns the time taken to parse the request.
		 * 
		 * @return the parse time
		 */		
		public long getParseTime(){
			return elapsedTime;
		}

}