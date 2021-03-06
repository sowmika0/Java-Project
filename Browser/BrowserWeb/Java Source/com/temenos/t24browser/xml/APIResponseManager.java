package com.temenos.t24browser.xml;

/**
 *                      APIResponseManager.java
 *Manages the parsing of an API response.  
 *Does the either one of the following:
 * 1) Extracts html from a specific node in the xml doc and stores it
 * 2) Extracts xml from a specific node and stores it
 * 3) Extracts a url from a specific node ??????
 * 4) Extracts a command and processes it to retrieve required variables 
 * 5) Generates a new APICommandRequest and hands the parameter list, gleaned from the command processor.
 *          - This reads in a given template and generates a new xml doc, depending on the template
 * 			- Then goes through this new xml doc and updates relevant nodes based on the parameters generated by the command processor
 *<p>
 *Also provides methods for retrieving specific details from the xml doc e.g. the response
 *<p>
 *The request is parserd according to the value set in the attribute "requestType".  
 */

/**
 * 
 * Modifications:
 * 
 * 29/09/03
 * 
 * Changed the constructor call to also take in an instance of XMLTemplateManager.
 * This instance is then used to pass to APIServerRequest.  This supplies APIServerRequest
 * with an actual xml template, as opposed to the path to the template.
*/

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.temenos.t24browser.request.T24Request;
// TODO: Auto-generated Javadoc

/**
 * The Class APIResponseManager.
 */
public class APIResponseManager

{

/** The xml. */
private XMLTemplate xml = null;        			//creates and populates the correct xml doc from the HTTP request

/** The response. */
private String response = "";          			//The document to return

/** The response type. */
private String responseType = "";      			//The type of response

/** The CDAT a_ STAR t_ TAG. */
private final String CDATA_START_TAG = "<!CDATA["; //used to start a wrap for a section of xml or html

/** The CDAT a_ EN d_ TAG. */
private final String CDATA_END_TAG = "]]>"; 		//used to end a wrap for a section of xml or html

/** The templates. */
private XMLTemplateManager templates = null;     	//contains all of the xml templates 


		/**
		 * Extracts the responseType from the xml document.  Processes the document based on the responseType.
		 * 
		 * @param xmlDocument the xml document
		 * @param request the request
		 * @param templates the templates
		 * 
		 * @throws XMLRequestTypeException the XML request type exception
		 */
        public APIResponseManager(String xmlDocument, HttpServletRequest request, XMLTemplateManager templates)
        	throws XMLRequestTypeException
        {
        	
        	  this.templates = templates;     //store the xml templates
        	  responseType = calculateResponseType(xmlDocument);
              
              //if the desired response is HTML
              if (responseType.equals("HTML")){
                processHtml(xmlDocument);
              }
              
              //if the desired response is xml
              else if (responseType.equals("XML")){
                processXml(xmlDocument);
              }
              
              //if the desired response is a message back to the browser 
              else if (responseType.equals("MESSAGE")){
              	processMessage(xmlDocument, request);
              }
              
              //if the desired response is a message poll 
              else if (responseType.equals("POLL")){
              	processPoll(xmlDocument, request);
              }
              
              //if the desired response is a URL 
              else if (responseType.equals("URL")){
              	processUrl(xmlDocument);
              }
              
              //if the desired response is a command and meant for T24
              else if (responseType.equals("COMMAND")){
              	processCommand(xmlDocument, request);
              	
              }
           
              else{
              		throw new XMLRequestTypeException("APIResponseManager: Invalid Response Type from API - " + responseType);
              }
        }




		/**
		 * Determines the type of response that we are dealing with.
		 * 
		 * @param xmlDocument the xml document
		 * 
		 * @return the string
		 */
		private String calculateResponseType(String xmlDocument){
			try{
				if(xmlDocument.indexOf("<command>")>0){
					 return "COMMAND";
				}

				if(xmlDocument.indexOf("<htmlPage>")>0){
					 return "HTML";
				}
			
				if(xmlDocument.indexOf("<xml>")>0){
					 return "XML";
				}	
			
				if(xmlDocument.indexOf("<url>")>0){
					 return "URL";
				}	
			
				if(xmlDocument.indexOf("<message>")>0){
					 return "MESSAGE";
				}	
				
				if(xmlDocument.indexOf("<poll>")>0){
					 return "POLL";
				}
				
				return "";
			
			}
			catch(NullPointerException e){
				return "";
			}
		}
		
		


		/**
		 * Processes an embedded html response.
		 * 
		 * @param xmlDocument the xml document
		 */
		private void processHtml(String xmlDocument){
			 response = getNodeValue(xmlDocument,"htmlPage");
             cleanResponse();
		}
		
		
		
		/**
		 * Processes an embedded xml response.
		 * 
		 * @param xmlDocument the xml document
		 */
		private void processXml(String xmlDocument){
			response = getNodeValue(xmlDocument,"xml");
            cleanResponse();
		}
		
		
		
		/**
		 * processes and embedded url response.
		 * 
		 * @param xmlDocument the xml document
		 */
		private void processUrl(String xmlDocument){
			response = xmlDocument;
		}
		
		
		
		/**
		 * processes an embedded command response.
		 * 
		 * @param xmlDocument the xml document
		 * @param request the request
		 */
		private void processCommand(String xmlDocument, HttpServletRequest request){
			//retrieve the desired parameters from the command
            CommandProcessor commandProcessor = new CommandProcessor(xmlDocument);

			//if the xml doc generated is a UTILITY.ROUTINE
            if (commandProcessor.getRequestType().equals("UTILITY.ROUTINE")){
            	//retrieve the correct template for building the request	
              	String xmlTemplate = (String)templates.getProperty("ofsUtilityRoutineRequest.xml");
              	T24Request req = new T24Request(request);
              	xml = new APIServerRequest(req,commandProcessor.getParameters(),xmlTemplate);
            }
            //if the xml doc generated is an OFS.APPLICATION
            else if (commandProcessor.getRequestType().equals("OFS.APPLICATION")){
            	//retrieve the correct template for building the request
            	String xmlTemplate = (String)templates.getProperty("ofsmlRequest.xml");
            	T24Request req = new T24Request(request);
              	xml = new APIServerRequest(req,commandProcessor.getParameters(),xmlTemplate);
            }
		}


		/**
		 * Processes an embedded message response.
		 * 
		 * @param xmlDocument the xml document
		 * @param request the request
		 */
		private void processMessage(String xmlDocument,HttpServletRequest request){
			String message = getNodeValue(xmlDocument,"message");
            Hashtable messageStore = new Hashtable();
            messageStore.put("title","API message");
            messageStore.put("msg",message);
            //retrieve the correct template for building the request
            String xmlTemplate = (String)templates.getProperty("apiMessage.xml");
            T24Request req = new T24Request(request);
            xml = new APIServerRequest(req,messageStore,xmlTemplate);
		}
		
		
		/**
		 * Processes an embedded message response with added poll functionality.
		 * 
		 * @param xmlDocument the xml document
		 * @param request the request
		 */
		private void processPoll(String xmlDocument,HttpServletRequest request){
			String message = getNodeValue(xmlDocument,"pollMessage");
			String time = getNodeValue(xmlDocument,"time");
			String command = getNodeValue(xmlDocument,"pollCommand");
            Hashtable messageStore = new Hashtable();
            messageStore.put("doPoll","true");
            messageStore.put("command",command);
            messageStore.put("time",time);
            messageStore.put("title","API message");
            messageStore.put("msg",message);
            //retrieve the correct template for building the request
            String xmlTemplate = (String)templates.getProperty("apiMessage.xml");
            T24Request req = new T24Request(request);
            xml = new APIServerRequest(req,messageStore,xmlTemplate);
		}



        /**
         * returns the final document created by the manager.
         * 
         * @return the response
         */
        public String getResponse(){
        	
        	if (response.equals("")){
            	return xml.getXMLResponse();
        	}
            else{
            	return response;
            }
        } 
		

		/**
		 * returns the type of document we are dealing with.
		 * 
		 * @return the response type
		 */
		public String getResponseType(){
			return responseType;
		}
		
		
		
		/**
		 * Removes the start and end CDATA tags from the response.
		 */
		public void cleanResponse(){
			if(response.startsWith(CDATA_START_TAG)){
				response = response.substring(8);
				if (response.endsWith(CDATA_END_TAG)){
					response = response.substring(0,(response.length()-3));
				}
			}
		}
		
		
		
		/**
		 * returns the value of a node in a given xml String.
		 * 
		 * @param xml the xml
		 * @param nodeName the node name
		 * 
		 * @return the node value
		 */
		public String getNodeValue(String xml, String nodeName){
			  
			  int nodeLength = nodeName.length();
			  int startTagPos = xml.indexOf(nodeName);
			  int endTagPos = xml.lastIndexOf(nodeName);
			  
			  if ( ( startTagPos == -1 ) || ( endTagPos == -1 ) )
			  {
			  	return( null );
			  }
			  else
			  {
			  	String result = xml.substring((startTagPos+nodeLength+1),(endTagPos-2));
			  	return result;
			  }
		} 
	 

}