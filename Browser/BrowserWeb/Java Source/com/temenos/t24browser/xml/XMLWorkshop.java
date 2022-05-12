package com.temenos.t24browser.xml;

/*
 *                      XMLWorkshop.java
 *Provides a series of simple proceedures for manipualting an xml doc.  You would think that sun would have thought of this 
 * - obviously not.
 *Enables the user to create a node, add a node, get the value from a node etc.
 */


import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



// TODO: Auto-generated Javadoc
/**
 * The Class XMLWorkshop.
 */
public class XMLWorkshop {

	/**
	 * Constructor for XMLWorkshop.
	 */
	public XMLWorkshop() {
	}
	
	
	/*
     *creates a new node and then returns it
     */
    /**
	 * Creates the node.
	 * 
	 * @param doc the doc
	 * @param name the name
	 * @param value the value
	 * 
	 * @return the node
	 */
	protected Node createNode(Document doc,String name,String value){
    	Element e = doc.createElement(name);
        e.appendChild(doc.createTextNode(value));
        return e;
    }



    /*
     *adds a node to a contextNode:  There is some magic here - the contextNode, somehow, implicitly
     *knows and records updates to it (GLOBAL sun magic).  The context Node therefore does not need to be passed back.
     */
  	/**
     * Adds the node.
     * 
     * @param doc the doc
     * @param contextNode the context node
     * @param path the path
     * @param nodeName the node name
     * @param value the value
     */
    protected void addNode(Document doc,Element contextNode, String path,String nodeName,String value){
    	Node node = getNode((Node)contextNode,path);
    	addNode(doc, node,nodeName,value);
	}



    /*
     *The Parent node is the context document.  Adds a node and a value under a specific node in the contxt doc.
     *New Sun core funtionality takes effect here.
     *You do not need to pass back the parent node.  For some reason the changes take
     *effect globally.
     */
	/**
     * Adds the node.
     * 
     * @param doc the doc
     * @param parentNode the parent node
     * @param nodeName the node name
     * @param nodeValue the node value
     */
    protected void addNode(Document doc,Node parentNode,String nodeName,String nodeValue){
    	Element ele = doc.createElement(nodeName);
        if (nodeValue != null){
        	ele.appendChild( doc.createTextNode(nodeValue) );
        }
        parentNode.appendChild(ele);
	}




    /*
     *adds a node to a contextNode:  There is some magic here - the contextNode, somehow, implicitly
     *knows and records updates to it (GLOBAL sun magic).  The context Node therefore does not need to be passed back.
     */
  	/**
     * Adds the node value.
     * 
     * @param doc the doc
     * @param contextNode the context node
     * @param nodePath the node path
     * @param value the value
     */
    protected void addNodeValue(Document doc,Element contextNode, String nodePath,String value){
    	Node node = getNode((Node)contextNode,nodePath);
        setNodeValue(doc, node,value);
	}



    /*
     *updates the value of a node.  This will instatly take effect in the
     *main root document - new java magic
     */
	/**
     * Sets the node value.
     * 
     * @param doc the doc
     * @param node the node
     * @param nodeValue the node value
     */
    protected void setNodeValue(Document doc, Node node,String nodeValue){
    	Element ele = (Element)node;
        //check to see if there is a text node there already
        if (nodeValue != null){
        	ele.appendChild(doc.createTextNode(nodeValue));
        }
        node = (Node)ele;
	}



    /*
     *replaces the value of a node with another value
     */
 	/**
     * Replace node value.
     * 
     * @param doc the doc
     * @param node the node
     * @param nodeValue the node value
     */
    protected void replaceNodeValue(Document doc, Node node,String nodeValue){
    	try{
        	Element ele = (Element)node;
            //check to see if there is a text node there already
            if (nodeValue != null){
            	ele.replaceChild(doc.createTextNode(nodeValue),ele.getFirstChild());
            }
                node = (Node)ele;
        }
        catch (NullPointerException e){
        	e.toString();
        }
	}



    /*
     *returns a node from a root node(document).  The node needs to be extracted before work
     *can be done on it.  Once extracted, any changes made to this node, will automatically
     *take effect in the main root document
     */
     /**
     * Gets the node.
     * 
     * @param contextNode the context node
     * @param path the path
     * 
     * @return the node
     */
    protected Node getNode(Node contextNode, String path){
        try{
        	Node node = XPathAPI.selectSingleNode((Node)contextNode,path);
            return node;
        }
        catch (TransformerException e){
        	e.printStackTrace();
            return null;
        }
     }



     /*in case the original Node has child nodes
      *that need to be carried over to the new node
      */
     /**
      * Replace node a.
      * 
      * @param node the node
      * @param newNode the new node
      */
     protected void replaceNodeA(Node node,Node newNode){
     	Node parent=node.getParentNode();
        if(node.hasChildNodes()){
        	NodeList nl=node.getChildNodes();
            int n=nl.getLength();
            // note that each child node is removed from node after it's appended to newNode
            for(int i=0;i<n;++i){
            	newNode.appendChild(nl.item(0));
            }
        }
        parent.replaceChild(newNode,node);
     }




     /*
      *returns the value of a node
      */
     /**
      * Gets the node value.
      * 
      * @param node the node
      * 
      * @return the node value
      */
     protected String getNodeValue(Node node){
    	NodeList childNodes = node.getChildNodes();
    	if(childNodes.getLength() == 0) {
    		return null;
    	} else {
            Node requestedNode = childNodes.item(0);
            return requestedNode.getNodeValue();
    	}
     }
     
     
     /*
      * Deletes the node from it's parent node
      */
      /**
      * Removes the node.
      * 
      * @param node the node
      */
     protected void removeNode(Node node){
      	node.getParentNode().removeChild(node);
      } 
	
}

