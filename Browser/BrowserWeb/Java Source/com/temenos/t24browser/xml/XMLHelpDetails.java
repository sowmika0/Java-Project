package com.temenos.t24browser.xml;

/*
 *                      XMLHelpDetails.java
 *Extends XMLTemplate.  Processes a save help page request, passed in via an HTTP request. 
 *
 *The following has to be done in order to process a new type of multiple node:
 *
 *  1)Update the method processParameter for extracting the correct data
 *  2)Update the method insertData for adding the data
 *
 * This is based on XMLEnquiry, whereby each field has been given a field Id so that the field name,
 * description and image can be grouped together for make a 't' node.
 * 
 * Help Details contain :-
 * 		field	-	The name of the helptext field
 * 		desc	-	The field description
 * 		image	-	An image associated with the field
 * 
 * Modifications:
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.temenos.t24browser.request.T24Request;



// TODO: Auto-generated Javadoc
/**
 * The Class XMLHelpDetails.
 */
public class XMLHelpDetails extends XMLTemplate

{

/** The list. */
protected List list;					//the entire list of params extracted from the request

/** The next element. */
private String nextElement;			//the next element in the list                          

/** The new param name. */
private String newParamName;			//the actual name of the param, as when extracted from request

/** The id. */
private int id;						//the id of the param

/** The ids to remove. */
private ArrayList idsToRemove;			//the vector to store the ids that need to be removed

/** The stored params. */
private ArrayList storedParams;		//the param list - correlate with the storedNodes   				

/** The stored nodes. */
private ArrayList storedNodes;			//the selection criteria nodes (multiples) that will be used to determine
										//which params are valid (have a value, or rely on another node having a value) - enquiry

        //pass the request and the name of the xml template to be used to the super class
        /**
										 * Instantiates a new XML help details.
										 * 
										 * @param request the request
										 * @param templates the templates
										 */
										public XMLHelpDetails(T24Request request, XMLTemplateManager templates){
              super(request, (String)templates.getProperty("helpDetails.xml"));
        }
        

        /*
         *A field parameter should consist of four parts: the name, the instance, the multivalue number, and the sub value
         *number.  Extracts each of these parameters.
         */
        /* (non-Javadoc)
         * @see com.temenos.t24browser.xml.XMLTemplate#processParameter(java.lang.String, com.temenos.t24browser.request.T24Request)
         */
        protected void processParameter(String newParamName,T24Request request){

              StringTokenizer tokens = new StringTokenizer(newParamName,":");
              
              try{
              	
              	if (getType().equals("t")){
                  	setNewName((String)tokens.nextElement());
              	}
              }
              catch (Exception e){
			  	System.out.println("Error with processing the Parameter");
			  	e.printStackTrace();
			  }   	
        }


        /*gets the parent node from the parentsParentNode passed into the method.  retrieves all of the child nodes
         *from the parent node (done in super), then runs through these nodes to see if any of them match up to the parameter being
         *processed.  When found, the paramter value is then entered into the node.
         */
        /* (non-Javadoc)
         * @see com.temenos.t24browser.xml.XMLTemplate#insertData(com.temenos.t24browser.request.T24Request, java.lang.String, java.lang.String, java.util.ArrayList)
         */
        protected void insertData(T24Request request,String newParamName,String paramName, ArrayList childList){

			  int numberOfChildren = childList.size();
              for (int i=0;i<numberOfChildren;i++){
              	  Node node = (Node)childList.get(i);
              	  if(node.getNodeType()==Node.ELEMENT_NODE){
                  		if (node.getNodeName().equals(paramName)){
                      		checkSetNodeValue(node,request.getParameter(newParamName));
                  		}
                  }
              }

        }   
        
                
        /*
		 *When the node is designated to be a multiple node then this method will be caled to process it.
		 *Runs through a list of nodes and processes them agaist the parms stored in the requestList.  The
		 *type indicates the grandparent nodes that the code is currently dealing with
		 */
		/* (non-Javadoc)
         * @see com.temenos.t24browser.xml.XMLTemplate#processMultipleNode(com.temenos.t24browser.request.T24Request, java.util.ArrayList, java.lang.String)
         */
        public void processMultipleNode(T24Request request, ArrayList nodes, String type){

			setFieldInstance(0);
			setType(type);			
			
			if(type.equals("t")){
				storedNodes = nodes;
				storedNodes.trimToSize();
				addDataNodes(request);
			}
			else{
				//process standard multiple node
				super.processMultipleNode(request,nodes,type);
			}     			       
		}	
        
            
               	
        /*
         *  Deals with the processing and addition of data nodes to the xml doc (tricky) 
         */
         /**
         * Adds the data nodes.
         * 
         * @param request the request
         */
        protected void addDataNodes(T24Request request){
         	
              setParameterList(request);               
         	  setValidParameterList();
              processValidParams(request);        	           	  
         }	

         	
		/*
 		 * create a list of all the parameters in the request
 		 */   
 		 /**
		 * Sets the parameter list.
		 * 
		 * @param request the new parameter list
		 */
		private void setParameterList(T24Request request){    	

         	  //get the list of all the parameters
              Enumeration e = request.getParameterNames();

              //create a list in order to sort the parameters into alphabetical order
              list = new ArrayList();
              while(e.hasMoreElements()){
                      list.add((String)e.nextElement());
              }
              Collections.sort(list);
		 }              


		/*
 		 * retrieve the parameters from the list whose names start with the node names stored in storedNodes
 		 */   
 		 /**
		 * Sets the valid parameter list.
		 */
		private void setValidParameterList(){    
  
 			  storedParams = new ArrayList();
       
              //run through each of the stored nodes          
              for (int i=0;i<storedNodes.size();i++){
              	    //run through each of the request parameters in the list
                    for(int j=0;j<list.size();j++){
                        nextElement = (String)list.get(j);
                        //if the paramter name begins with the node name then add it to the storedParameters
                        if (nextElement.startsWith(((Node)storedNodes.get(i)).getNodeName())){
                            newParamName = nextElement;
                            storedParams.add(newParamName);
                        }
                    }       
              }
              
              storedParams.trimToSize();
		 }
 		  		 
              	             
        /*
 		 * add the stored params that are left in the storedParams list to the xml document
 		 */  
 		 /**
         * Process valid params.
         * 
         * @param request the request
         */
        private void processValidParams(T24Request request){   
  			  
  			  
              //run through each of the nodes that we are looking for        
              for( int j=0; j < storedNodes.size(); j++ )
              {
              	//run through each of the storedParams
              	
              	//reset the field instance for each node type
              	//Because the nodes list of params has been sorted then in theory
              	//each of the params should follow in the same order.
    			setFieldInstance(0);  
    					
   				for( int i=0; i < storedParams.size(); i++ )
   				{
                        nextElement = (String)storedParams.get(i);
                        
                        //if the storedParam name starts with the name of the node
                        if (nextElement.startsWith(((Node)storedNodes.get(j)).getNodeName()))
                        {
                            newParamName = nextElement;
                            
                            // Process field - even if the node value is blank
                        	processField((Node)storedNodes.get(j), ((Node)storedNodes.get(j)).getNodeName(),newParamName,request);
							setFieldInstance(getFieldInstance()+1);
                        }                        
                }
             }
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
        protected void cleanXmlDoc(DocumentBuilder docBuilder)throws SAXException, IOException
        {
        	// We don't want to strip out spaces and tabs so override the default behaviour
        }  
}