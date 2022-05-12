////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XmlBulkResponse
//
//  Description   :   Class that deals with XML messages containing a sub-pane 
//					  XML response.
//					  This class takes a sub-pane result and removes any data
//					  not required for displaying in a sub-pane.
//
//  Modifications :
//
//    28/06/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.xml;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.Utils;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLSubPaneResponse.
 */
public class XMLSubPaneResponse extends XMLBulk
{
	
	/** The Constant COMPONENT_NAME. */
	private static final String COMPONENT_NAME = "XMLSubPaneResponse : ";	// Used for logging
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLSubPaneResponse.class);
	
		
	/**
	 * Instantiates a new XML sub pane response.
	 * 
	 * @param sXmlResponse the s xml response
	 */
	public XMLSubPaneResponse( String sXmlResponse )
	{
		// Clean the response of some of the unwanted tags
		ivXml = cleanResponse( sXmlResponse );
	}
	
		
	/**
	 * Returns the cleaned response.
	 * 
	 * @param sResponse The sub-pane response in XML format
	 * 
	 * @return String
	 */
	private String cleanResponse( String sResponse )
	{
		String sCleanedXml = "";
		
		// Remove unwanted tags (e.g. the toolbar)
		sCleanedXml = Utils.removeNodeFromString( sResponse, XMLConstants.XML_TOOLBARS_TAG );
		
		// Add any extra tags required inside the pane
		// Add the title, screenMode, etc
		String sHeaderXml = getPaneHeaderXml( sResponse );
		sHeaderXml += XMLConstants.XML_SUB_PANE_TAGGED + "Y" + XMLConstants.XML_SUB_PANE_TAGGED_C;
		sCleanedXml = Utils.replaceAll( sCleanedXml, XMLConstants.XML_PANE_TAGGED, XMLConstants.XML_PANE_TAGGED + sHeaderXml );
		
		return( sCleanedXml );
	}
}
