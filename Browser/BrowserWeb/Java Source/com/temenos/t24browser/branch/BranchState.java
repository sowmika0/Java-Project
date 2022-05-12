////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BranchState
//
//  Description   :   Routines to read and save the branch state from/to the
//						"branchState.data" file.
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
import com.temenos.t24browser.utils.FileManager;


// TODO: Auto-generated Javadoc
/**
 * The Class BranchState.
 */
public class BranchState implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchState.class);
	
	/** The iv context. */
	private ServletContext ivContext = null;
	
	/** The iv file. */
	private FileManager ivFile = null;
	
		
	/**
	 * Instantiates a new branch state.
	 * 
	 * @param context the context
	 */
	public BranchState(ServletContext context)
	{
		ivContext = context;
		
		String sFileSeparator = System.getProperty("file.separator");
		String sFileName = ivContext.getRealPath("") + sFileSeparator;
		sFileName += "WEB-INF" + sFileSeparator + "conf" + sFileSeparator;
		sFileName += "branch" + sFileSeparator + BranchConstants.BRANCH_STATE_FILE;
		
		ivFile = new FileManager( sFileName );
	}

	
	// Read the branch state from the data file
	/**
	 * Read state.
	 * 
	 * @return the string
	 */
	public String readState()
	{
		// Read the branch state from the date file branchState.dat
		LOGGER.debug("Reading " + BranchConstants.BRANCH_STATE_FILE);
		String state = ivFile.readFile();
		
		// If we couldn't read the file then default to ONLINE (i.e. use the main server)
		if ( ( state == null ) || ( state.equals("") ) )
		{
			LOGGER.debug("No branch status in file.  Default to main server.");
			return( BranchConstants.BRANCH_ONLINE );
		}
		else
		{
			LOGGER.debug("Read Branch State from data file : '" + state + "'");
			return( state );
		}
	}

	// Save the branch state to the data file
	/**
	 * Save state.
	 * 
	 * @param state the state
	 */
	public void saveState( String state )
	{
		// Save the branch state to the date file branchState.dat
		LOGGER.debug("Writing Branch State of '" + state + "' to " + BranchConstants.BRANCH_STATE_FILE);

		if ( ivFile.writeFile( state ) == false )
		{
			LOGGER.error("Error writing Branch State to " + BranchConstants.BRANCH_STATE_FILE);
		}
	}
}
