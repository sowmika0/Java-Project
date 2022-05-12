package com.temenos.t24browser.xml;

/*
 *                      XMLParameters.java
 *Extends xmlTemplate : Used by the BrowserBean class to read the Browser
 * parameters from the XML file and store them in a Hashtable.  The Hashtable
 * is stored in the PropertyManager class which the BrowserBean uses to
 * retrieve properties such as Server IP Address, Server Port Number, etc.
 *
 *
 *
 */

import java.util.Hashtable;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLParameters.
 */
public class XMLParameters extends XMLTemplate{
	
  /** The h parameters table. */
  private Hashtable hParametersTable;	                  //used to store the parameter value pairs
  
  
        /*
	     *pass the request and the name of the xml template to be used to the super class
		 */
        /**
         * Instantiates a new XML parameters.
         * 
         * @param contextPath the context path
         * @param paramFileName the param file name
         */
        public XMLParameters( String contextPath, String paramFileName ){
              super(contextPath, paramFileName);
        }


		/*
		 *retrieves the param names and values from an xml document and then adds them to a
		 *hashtable.
		 */
		/* (non-Javadoc)
		 * @see com.temenos.t24browser.xml.XMLTemplate#processXMLDoc()
		 */
		protected void processXMLDoc()
		{
			// Get root element
			Node root = (Node) getDocument().getDocumentElement();
            hParametersTable = new Hashtable();
			
			try{
                  NodeList childList = root.getChildNodes();
                  
                  //strip out all of the text nodes etc (we just want ELEMENTS)
              	  ArrayList validNodes = this.getElementNodes(childList); 
              	  
                  //run each of the childs child nodes to check if they match paramName
                  int numberOfChildren = validNodes.size();

                  //go through each of the 'data' children in the vector - should only be 1
                  for (int i=0;i<numberOfChildren;i++)
                  {
                      NodeList dataList = ((Node)validNodes.get(i)).getChildNodes();
                  
	                  //strip out all of the text nodes etc (we just want ELEMENTS)
 	             	  ArrayList validDataNodes = this.getElementNodes(dataList); 
              	  
	  	              //run each of the childs child nodes to check if they match paramName
   	               	  int numberDataOfChildren = validDataNodes.size();

 	                  //go through each of the 'parameter' children in the vector
     	              for (int j=0;j<numberDataOfChildren;j++)
                      {
                    	  Node node = (Node)validDataNodes.get(j); 
	                	  NodeList paramList = node.getChildNodes();
 	                 	  ArrayList childNodes = this.getElementNodes(paramList); 
                 
  	               	  	  String paramName = getXmlWorkshop().getNodeValue(((Node)childNodes.get(0)));
   	              	  	  String paramValue = getXmlWorkshop().getNodeValue(((Node)childNodes.get(1)));

                 	      hParametersTable.put( paramName, paramValue );
                      }
                  }
              }
              catch (Exception n){
					System.out.println("Exception : " + n);
              }
		}



		/*
		 *returns the hashtable
		 */ 
		/**
		 * Gets the parameters table.
		 * 
		 * @return the parameters table
		 */
		public Hashtable getParametersTable()
		{
				return( hParametersTable );
		}
}

