////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XmlBulk
//
//  Description   :   Class that deals with XML messages containing bulk 
//					  XML requests or responses.
//
//  Modifications :
//
//    14/02/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.xml;

import com.temenos.t24browser.utils.Utils;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLBulk.
 */
public class XMLBulk
{
	
	/** The iv xml. */
	protected String ivXml = "";				// XML String
		
	
	/**
	 * Returns an XML document.
	 * 
	 * @return String
	 */
	public String toXml()
	{
		return( ivXml );
	}
	
	// Returns the tagged header information for a pane
	/**
	 * Gets the pane header xml.
	 * 
	 * @param resp the resp
	 * 
	 * @return the pane header xml
	 */
	protected String getPaneHeaderXml( String resp )
	{
		// Get the header information for this pane
		String title = Utils.getNodeFromString( resp, XMLConstants.XML_TITLE_TAG );
		String init = Utils.getNodeFromString( resp, XMLConstants.XML_INIT_TAG );
		String beforeUnload = Utils.getNodeFromString( resp, XMLConstants.XML_BEFORE_UNLOAD_TAG );
		String unload = Utils.getNodeFromString( resp, XMLConstants.XML_UNLOAD_TAG );
		String screenMode = Utils.getNodeFromString( resp, XMLConstants.XML_SCREEN_MODE_TAG );
		String lockArgs = Utils.getNodeFromString( resp, XMLConstants.XML_LOCK_ARGS_TAG );
		String newCmds = Utils.getNodeFromString( resp, XMLConstants.XML_NEW_CMDS_TAG );
		
		String sHeaderXml = "";
		sHeaderXml += XMLConstants.XML_TITLE_TAGGED + title + XMLConstants.XML_TITLE_TAGGED_C;
		sHeaderXml += XMLConstants.XML_INIT_TAGGED + init + XMLConstants.XML_INIT_TAGGED_C;
		sHeaderXml += XMLConstants.XML_BEFORE_UNLOAD_TAGGED + beforeUnload + XMLConstants.XML_BEFORE_UNLOAD_TAGGED_C;
		sHeaderXml += XMLConstants.XML_UNLOAD_TAGGED + unload + XMLConstants.XML_UNLOAD_TAGGED_C;
		sHeaderXml += XMLConstants.XML_SCREEN_MODE_TAGGED + screenMode + XMLConstants.XML_SCREEN_MODE_TAGGED_C;
		sHeaderXml += XMLConstants.XML_LOCK_ARGS_TAGGED + lockArgs + XMLConstants.XML_LOCK_ARGS_TAGGED_C;
		sHeaderXml += XMLConstants.XML_NEW_CMDS_TAGGED + lockArgs + XMLConstants.XML_NEW_CMDS_TAGGED_C;
		
		return( sHeaderXml );
	}
}
