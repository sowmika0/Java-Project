package com.temenos.t24browser.xml;

/**
 *                      APIServerRequest.java
 * Extends XMLTemplate. 
 *<p>
 * Generates an xml file from a given xml template, by parsing an HttpServletRequest.
 * Then updates the doc created by further populating the doc with the parameter values held
 * in a parameters hashtable.
 *
 *
 */

/**
 * Modifications:
 * 
 * 29/09/03
 * 
 * Changed the name of the constructor parameter 'xmlPath' to better suit its new role.  We are now passing the 
 * actual xml template, rather than the path to it.
 */


import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.temenos.t24browser.request.T24Request;

// TODO: Auto-generated Javadoc
/**
 * The Class APIServerRequest.
 */
public class APIServerRequest extends XMLTemplate{
	
		/** The parameters. */
		private Hashtable parameters = new Hashtable();         //contain all of the params to be added to the xml doc
        
        
         /**
          * pass the request and the name of the xml template to be used to the super class.
          * 
          * @param request the request
          * @param parameters the parameters
          * @param xmlTemplate the xml template
          */
        public APIServerRequest(T24Request request, Hashtable parameters, String xmlTemplate){
              super(request,xmlTemplate);
              this.parameters = parameters;
              updateXmlDocument();
              setRemoveNodes(true);
              prepareXMLResponse();
        }
        
        /**
         * Enables the childclass to do some initial processing before the super class begins.
         */        
        public void applyChildInfo(){
        	//we don't want to remove the node until the end
        	setRemoveNodes(false);	
        }
        
        /**
         * Runs through the newly created xml doc and puts forward each node in
         * turn for updating/.
         */
        private void updateXmlDocument(){
        	
        	  // Get root element
			  Element root = getDocument().getDocumentElement();
              
              NodeList xmlNodeList = root.getElementsByTagName("*");
              int listLength = xmlNodeList.getLength();
              
              //run through all of the nodes in the xml document
              for(int i=0;i<listLength;i++){
                       Node node=xmlNodeList.item(i);
                       //we only want element nodes
                       if (node.getNodeType()==node.ELEMENT_NODE){
                       			processNode(node);
                       }
                     
              }    	
     
        }
        
		/**
		 * Compares a given node name with each of the parameters.
		 * If they match, then adds the value of the parameter to the node
		 * 
		 * @param node the node
		 */    
        public void processNode(Node node){

        	  Enumeration e = parameters.keys();
        	  String parameterName = "";
        	   
              while(e.hasMoreElements()){
                      parameterName = (String)e.nextElement();
                      if (parameterName.equals(node.getNodeName())){
                      checkSetNodeValue(node,(String)parameters.get(parameterName));
                      }	
              }
        }
}

