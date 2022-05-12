package com.temenos.t24browser.xml;

/*
 *                      XMLApplication.java
 *Extends XMLTemplate.  Processes a transaction request, passed in via an HTTP request. 
 *
 *The following has to be done in order to process a new type of multiple node:
 *
 *  1)Update the method processParameter for extracting the correct data
 *  2)Update the method insertData for adding the data
 * 
 * Modifications:
 * 
 * 08/07/03 - Added a new parameter to the constructor call of the Super class; now passes in
 * 				in the xml template as a string
 */

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.w3c.dom.Node;

import com.temenos.t24browser.request.T24Request;



// TODO: Auto-generated Javadoc
/**
 * The Class XMLApplication.
 */
public class XMLApplication extends XMLTemplate

{

/** The override value. */
protected String overrideValue = null;       //the value of the override




        //pass the request and the name of the xml template to be used to the super class
        /**
         * Instantiates a new XML application.
         * 
         * @param request the request
         * @param templates the templates
         */
        public XMLApplication(T24Request request, XMLTemplateManager templates){
              super(request, (String)templates.getProperty("ofsmlRequest.xml"));
        }
        
        

        /*
         *1)A data parameter consists of four parts: the name, the instance, the multivalue number, and the sub value
         *number.  Extracts each of these parameters.
  		 *2)Any other params are processed as standard by calling the super method.
         */
        /* (non-Javadoc)
         * @see com.temenos.t24browser.xml.XMLTemplate#processParameter(java.lang.String, com.temenos.t24browser.request.T24Request)
         */
        protected void processParameter(String newParamName,T24Request request){

              StringTokenizer tokens = new StringTokenizer(newParamName,":");
              int tokenCount = tokens.countTokens();
              setMultiValue("");
              setSubValue("");

			  try{
			  	//deal with field type nodes
              	if (getType().equals("field"))
              	{
              		//when there are both multi values and sub values
              		if (tokenCount == 4){
               	      	String fieldToken = (String)tokens.nextElement();
                      	setNewName((String)tokens.nextElement());
                      	setMultiValue((String)tokens.nextElement());
                      	setSubValue((String)tokens.nextElement());
              		}
              		//when multi values only
              		else if (tokenCount == 3){
              			String fieldToken = (String)tokens.nextElement();
                      	setNewName((String)tokens.nextElement());
                      	setMultiValue((String)tokens.nextElement());
              		}
              		//No multi values or subvalues
              		else{              		
              			String fieldToken = (String)tokens.nextElement();
                      	setNewName((String)tokens.nextElement());
              		}
              		
              	}
              	else if (getType().equals("o") || getType().equals("w"))
              	{
              		// An Override or Warning
              		// The text part can contain ":" character so we cannot use the StringTokenizer
              		// Get the text and remove and ":value" string that appears on the end of override field names
	            		String name = newParamName;
	              		int pos = newParamName.indexOf(":");
	              		
	              		if ( pos != -1 )
	              		{
	              			name = name.substring(pos + 1, name.length());
	              			
	              			// Remove any ":value" at the end of the override field if present
	              			if ( name.endsWith(":value") )
	              			{
	              				name = name.substring(0, name.length() - 6);
	              			}
	              		}
	              		
	              		setNewName(name);	// Set the override/warning text
              	}
              	else if (getType().equals("a") || (getType().equals("rekey")))
              	{
              		
              		 String fieldToken = (String)tokens.nextElement();  // Ignore the first element.
                     setNewName((String)tokens.nextElement());  // Add the authentication type value
                     setMultiValue((String)tokens.nextElement());  // Add the response of the authentication.					 
              	}
              	//deal with any other node types
              	
              	else {
              		super.processParameter(newParamName,request);
              	}
			  }
			  catch (Exception e){
			  	System.out.println("Error with processing the Parameter");
			  	e.printStackTrace();
			  }   	
        }



        //enters the paramter values into the applicable nodes.  Deals with the specific input
        //of field data.  Any other param values are processed as standard, via the super method.
        /* (non-Javadoc)
         * @see com.temenos.t24browser.xml.XMLTemplate#insertData(com.temenos.t24browser.request.T24Request, java.lang.String, java.lang.String, java.util.ArrayList)
         */
        protected void insertData(T24Request request, String newParamName, String paramName, ArrayList childList){


				  //deals with data nodes
				  if (getType().equals("field")){
                  	checkSetNodeValue((Node)childList.get(0),getNewName());
                  	checkSetNodeValue((Node)childList.get(1),getMultiValue());
                  	checkSetNodeValue((Node)childList.get(2),getSubValue());
                  	checkSetNodeValue((Node)childList.get(3),request.getParameter(newParamName));
                  	setFieldInstance(getFieldInstance()+1);
        		  }
        		  else if( getType().equals("a") )
        		  {
        		   	checkSetNodeValue((Node)childList.get(0), getNewName() );       		  	
        		   	checkSetNodeValue((Node)childList.get(1), getMultiValue() );  
        		   	checkSetNodeValue((Node)childList.get(2),request.getParameter(newParamName));
        		   	setFieldInstance(getFieldInstance()+1);      		   	
        		  }
        		  //deals with everything else
                  else{	
                  	super.insertData(request,newParamName,paramName,childList);
                  } 
                  
                               
        }


}