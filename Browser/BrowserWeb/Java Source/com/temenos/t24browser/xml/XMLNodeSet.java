package com.temenos.t24browser.xml;

/*
 *                      XMLNodeSet.java
 *Stores all of the child nodes for a particular node.  
 *
 *
 */



import java.util.ArrayList;
import org.w3c.dom.*;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLNodeSet.
 */
public class XMLNodeSet {
	
	/** The parent name. */
	private String parentName;     	//the name of the parent node
	
	/** The stored nodes. */
	private ArrayList storedNodes; 	//the nodes to be duplicated
	
	
	
	/**
	 * Instantiates a new XML node set.
	 * 
	 * @param node the node
	 */
	public XMLNodeSet(Node node){
		this.parentName  = node.getParentNode().getNodeName();
		storedNodes = new ArrayList();
		addNode(node);
	}


    //returns the name of the parent Node
	/**
     * Gets the parent name.
     * 
     * @return the parent name
     */
    public String getParentName(){
		return parentName;
	}

	
	//returns the stored nodes to be duplicated
	/**
	 * Gets the stored nodes.
	 * 
	 * @return the stored nodes
	 */
	public ArrayList getStoredNodes(){
		storedNodes.trimToSize();
		return storedNodes;
	}

	
	//adds a node to the list of nodes to be duplicated
	/**
	 * Adds the node.
	 * 
	 * @param node the node
	 */
	public void addNode(Node node){
		storedNodes.add(node);
	}
}

