////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BranchStrings
//
//  Description   :   Routines to read and use data stored in "branchStrings.xml".
//
//  Modifications :
//
//    07/05/06   -    Initial Version .
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.branch;

import java.io.Serializable;

import javax.servlet.ServletContext;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.StringsManager;


// TODO: Auto-generated Javadoc
/**
 * The Class BranchStrings.
 */
public class BranchStrings implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchStrings.class);
	
	/** The iv context. */
	private ServletContext ivContext = null;
	
	/** The iv branch strings. */
	StringsManager ivBranchStrings = null;									// Read from 'branchStrings.xml'

		
	/**
	 * Instantiates a new branch strings.
	 * 
	 * @param context the context
	 */
	public BranchStrings(ServletContext context)
	{
		ivContext = context;
		readBranchStrings();
	}
	
	// Read the XML branch strings file
	/**
	 * Read branch strings.
	 */
	private void readBranchStrings()
	{
		// We are a Branch, so read the branch parameters from the XML file branchParameters.xml
		LOGGER.debug("Reading " + BranchConstants.BRANCH_STRINGS_FILE);

		String sFileSeparator = System.getProperty("file.separator");
		String contextPath = ivContext.getRealPath("") + sFileSeparator;
		contextPath += "WEB-INF" + sFileSeparator + "conf" + sFileSeparator;
		contextPath += "branch";

		ivBranchStrings = new StringsManager( contextPath, BranchConstants.BRANCH_STRINGS_FILE );
	}
	
	// Get string
	/**
	 * Gets the string.
	 * 
	 * @param sKey the s key
	 * 
	 * @return the string
	 */
	public String getString( String sKey )
	{
		if ( ivBranchStrings == null )
		{
			return( "" );
		}
		else
		{
			return( ivBranchStrings.getStringrValue( sKey ) );
		}
	}
}
