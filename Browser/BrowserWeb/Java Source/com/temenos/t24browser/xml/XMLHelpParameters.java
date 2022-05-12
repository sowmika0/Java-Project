package com.temenos.t24browser.xml;

/*
 *                      XMLHelpParameters.java
 * Extends xmlTemplate : Used by the HelpServlet class to read the Browser
 * Help parameters from the XML file and store them in 3 Arrays :
 * 		hViewLocationsTable	:	Locations to look for help pages when viewing
 * 		hEditLocationsTable	:	Locations to look for help pages when editing
 * 		hLanguagesTable	:		Languages supported for help pages
 *
 *
 */

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLHelpParameters.
 */
public class XMLHelpParameters extends XMLTemplate{

	/** The iv view locations table. */
	private ArrayList ivViewLocationsTable;		// Used to store list of help locations when viewing
	
	/** The iv edit locations table. */
	private ArrayList ivEditLocationsTable;		// Used to store list of help locations when editing
	
	/** The iv languages table. */
	private ArrayList ivLanguagesTable;			// Used to store list of help languages
	
	/** The iv images dir. */
	private String    ivImagesDir;					// Used to store the default images directory
	
	/** The iv editable. */
	private boolean  ivEditable;					// Used to indicate if pages are editable
	
	/** The iv dev mode. */
	private boolean  ivDevMode;					// Used to indicate if we are running in Development

	/** The DEFAULTLANG. */
	private static String DEFAULTLANG = "GB";		// The default language code is GB


	    /*
	     *pass the request and the name of the xml template to be used to the super class
		 */
        /**
    	 * Instantiates a new XML help parameters.
    	 * 
    	 * @param contextPath the context path
    	 * @param fileName the file name
    	 */
    	public XMLHelpParameters( String contextPath, String fileName ){
              super(contextPath, fileName);

        }


		/*
		 * Retrieves the locations and language parameters from an xml document and then adds
		 * them to the relevant array
		 */
		/* (non-Javadoc)
		 * @see com.temenos.t24browser.xml.XMLTemplate#processXMLDoc()
		 */
		protected void processXMLDoc()
		{
			// Get root element
			Node root = (Node) getDocument().getDocumentElement();
            ivViewLocationsTable = new ArrayList();
            ivEditLocationsTable = new ArrayList();
            ivLanguagesTable = new ArrayList();

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

 	                  //go through each of the 'location' & 'language' children in the vector
     	              for (int j=0;j<numberDataOfChildren;j++)
                      {
                    	  Node node = (Node)validDataNodes.get(j);
                    	  String nodeName = node.getNodeName();
                    	  String nodeValue = getXmlWorkshop().getNodeValue(node);

                 		  // Check what node we have and add it to the relevant array
                 		  if ( nodeName.equals( "viewLocation" ) )
                 		  {
                 		  		addArrayElement( ivViewLocationsTable, nodeValue );
                 		  }
                 		  else if ( nodeName.equals( "editLocation" ) )
                 		  {
                 		  		addArrayElement( ivEditLocationsTable, nodeValue );
                 		  }
                 		  else if ( nodeName.equals( "language" ) )
                 		  {
                 		  		addArrayElement( ivLanguagesTable, nodeValue );
                 		  }
                  		  else if ( nodeName.equals( "imagesDir" ) )
                 		  {
                 		  		ivImagesDir = nodeValue;
                 		  }
                 		  else if ( nodeName.equals( "editable" ) )
                 		  {
                 		  		if ( nodeValue.equals( "YES" ) || ( nodeValue.equals( "yes" ) ) )
                 		  		{
                 		  			ivEditable = true;
                 		  		}
                 		  }
                 		  else if ( nodeName.equals( "devMode" ) )
                 		  {
                 		  		if ( nodeValue.equals( "YES" ) || ( nodeValue.equals( "yes" ) ) )
                 		  		{
                 		  			ivDevMode = true;
                 		  		}
                 		  }
                       }
                  }

                  // Now add the default path of the web server
                  String defaultLocation = getRootDirectory() + File.separator + 
                  	"help" + File.separator;
                  addArrayElement( ivViewLocationsTable, defaultLocation );
                  // Now add the default path of the web server, for modelbank,
                  // ARC-IB context help
                  defaultLocation = getRootDirectory() + File.separator + 
                  	"modelbank" + File.separator + "help" + File.separator;
                  addArrayElement( ivViewLocationsTable, defaultLocation );
                  addArrayElement( ivEditLocationsTable, defaultLocation );

                  // Now add the default language of GB, if it's not already there
                  addArrayElement( ivLanguagesTable, DEFAULTLANG );
              }
              catch (Exception n){
					System.out.println("Exception : " + n);
              }
		}

		/*
		 * Add a value to an ArrayList - if it's not already there
		 */
		/**
		 * Adds the array element.
		 * 
		 * @param aTable the a table
		 * @param sValue the s value
		 */
		private void addArrayElement( ArrayList aTable, String sValue )
		{
			if ( ( sValue != null ) && ( !sValue.equals("") ) && ( ! aTable.contains( sValue ) ) )
			{
				aTable.add( sValue );
			}
		}

		/*
		 * Returns the view locations array
		 */
		/**
		 * Gets the help view locations table.
		 * 
		 * @return the help view locations table
		 */
		public ArrayList getHelpViewLocationsTable()
		{
				return( ivViewLocationsTable );
		}

		/*
		 * Returns the view locations array
		 */
		/**
		 * Gets the help edit locations table.
		 * 
		 * @return the help edit locations table
		 */
		public ArrayList getHelpEditLocationsTable()
		{
				return( ivEditLocationsTable );
		}

		/*
		 * Returns the languages array
		 */
		/**
		 * Gets the help languages table.
		 * 
		 * @return the help languages table
		 */
		public ArrayList getHelpLanguagesTable()
		{
				return( ivLanguagesTable );
		}

		/*
		 * Returns the images directory
		 */
		 /**
		 * Gets the help images directory.
		 * 
		 * @return the help images directory
		 */
		public String getHelpImagesDirectory()
		 {
		 		return( ivImagesDir );
		 }

		/*
		 * Returns whether pages are editable or not
		 */
		 /**
		 * Help pages editable.
		 * 
		 * @return true, if successful
		 */
		public boolean helpPagesEditable()
		 {
		 		return( ivEditable );
		 }

		/*
		 * Returns whether we are running in Development Mode
		 * We use this switch to control functionality that must be
		 * disabled or enabled only if we are in Development i.e. not
		 * running on a LIVE site.
		 */
		 /**
		 * Help development mode.
		 * 
		 * @return true, if successful
		 */
		public boolean helpDevelopmentMode()
		 {
		 		return( ivDevMode );
		 }
}

