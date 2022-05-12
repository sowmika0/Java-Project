package com.temenos.t24browser.apiclient;

import com.temenos.browser.api.APIConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class TestAPI.
 * 
 * @author temenos
 * 
 * <p>
 * This is a test API client program to demonstrate how a client instance could
 * be set up to use the API.
 * <p>
 * The Client is listening for the following command arguments:
 * 1) return.message
 * 2) return.xml
 * 3) return.message
 * 4) return.deal
 * 5) return.enq
 * <p>
 * e.g.
 * The following could be typed in the TBrowser main menu command line:
 * 
 * API return.message
 * 
 * <p>
 * This message will be packaged up by the servlet and delivered to the client
 * , via the API, as an XML request.
 * <p>
 * The client should then interrogate the request, by extracting the value
 * of the apiArgument node.
 * <p>
 * The client then gathers together the information required and returns it to the
 * API in one of the correct response formats.
 * <p>
 * URL re-direct is not implemented here.
 */
public class TestAPI implements APIConnection {

	/** The count. */
	private static int count;

	/**
	 * Gets the data.
	 * 
	 * @param data the data
	 * 
	 * @return the data
	 * 
	 * @see com.temenos.globusbrowser.api.APIConnection#getData(String)
	 */
	public String getData(String data) {
		
		count++;   //moitors the number of times that the class has been instantiated
				   //Used to test polling. 

//1) extract the value of the argument node from the xml request.
		String argument = getNodeValue (data,"apiArgument");
		String xmlTest = "";
		
//3) parse the information gleaned.
			

//2) send back a response, depending on the argument value		
		if(argument.equals("return.message")){
			//return a message, destined for the users Browser
			xmlTest = "<apiResponse><message>This is an api message.</message></apiResponse>";
		}
		else if (argument.equals("return.html")){
			//return an html doc to be displayed by the users Browser
			xmlTest = "<apiResponse><htmlPage><!CDATA[<html><body>This is an html page.</body></html>]]></htmlPage></apiResponse>";
		}
		else if ((argument.equals("return.deal"))||((argument.equals("return.poll"))&&(count%3==0))){
			//submit a T24 request
			xmlTest = "<apiResponse><command>FOREX,SPOTBUY I F3</command></apiResponse>";
		}
		else if (argument.equals("return.enq")){
			//submit a T24 enquiry request
			xmlTest = "<apiResponse><command>ENQ ACCOUNT-LIST</command></apiResponse>";
		}
		else if (argument.equals("return.xml")){
			//FOR NOW - LEAVE THIS ALONE. The actual architecture of the embedded xml doc needs to be agreed upon.
			//An error will be thrown in the meantime
			xmlTest = "<apiResponse><xml><!CDATA[<myXml><hello/></myXml>]]></xml></apiResponse>";
		}
		else if (argument.equals("return.poll")){
			xmlTest = "<apiResponse><poll><pollMessage>Polling in progress...</pollMessage><time>5</time><pollCommand>return.poll</pollCommand></poll></apiResponse>";
		}
		else{
			xmlTest = xmlTest = "<apiResponse><message>ERROR: The argument was not recognised.</message></apiResponse>";
		}
		return xmlTest;
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
