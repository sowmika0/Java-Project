////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BranchParameters
//
//  Description   :   Routines to read and use data stored in "branchParameters.xml".
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
import com.temenos.t24browser.utils.PropertyManager;

// TODO: Auto-generated Javadoc
/**
 * The Class BranchParameters.
 */
public class BranchParameters implements Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchParameters.class);

	/** The iv context. */
	private ServletContext ivContext = null;
	
	/** The iv branch parameters. */
	PropertyManager ivBranchParameters = null;								// Read from 'branchParameters.xml'
	
	/** The iv branch. */
	private Branch ivBranch = null;										// Branch object representing XML file
	
	/** The iv instance. */
	private String ivInstance = "";											// Active TC Instance

		
	/**
	 * Instantiates a new branch parameters.
	 * 
	 * @param context the context
	 */
	public BranchParameters(ServletContext context)
	{
		ivContext = context;
		readBranchParameters();
	}

	// Returns the Branch object as created from the parameter file
	/**
	 * Gets the branch.
	 * 
	 * @return the branch
	 */
	public Branch getBranch()
	{
		return( ivBranch );
	}
	
	
	// Read the XML branch parameter file
	/**
	 * Read branch parameters.
	 */
	private void readBranchParameters()
	{
		// We are a Branch, so read the branch parameters from the XML file branchParameters.xml
		LOGGER.debug("Reading " + BranchConstants.BRANCH_PARAM_FILE);

		String sFileSeparator = System.getProperty("file.separator");
		String contextPath = ivContext.getRealPath("") + sFileSeparator;
		contextPath += "WEB-INF" + sFileSeparator + "conf" + sFileSeparator;
		contextPath += "branch";
		
		ivBranchParameters = new PropertyManager( contextPath, BranchConstants.BRANCH_PARAM_FILE );
		
		String branchName = "";
		String branchOnline = "";
		String branchOffline = "";
		
		if ( checkBranchParameters() )
		{
 			ivBranch = new Branch();
 			branchName = ivBranchParameters.getParameterValue( BranchConstants.BRANCH_PARAM_BRANCH_ID );
 			ivBranch.setName( branchName );

  			branchOnline = ivBranchParameters.getParameterValue( BranchConstants.BRANCH_PARAM_ONLINE_INSTANCE );
 			ivBranch.setOnlineInstance( branchOnline );
 			branchOffline = ivBranchParameters.getParameterValue( BranchConstants.BRANCH_PARAM_OFFLINE_INSTANCE );
 			ivBranch.setOfflineInstance( branchOffline );
 			
			// Read the current Branch Status from the data file
			BranchState bs = new BranchState( ivContext );
			String branchState = bs.readState();
 			ivBranch.setStatus( branchState );

			String activeInstance = ivBranch.getActiveInstance();
 			ivInstance = activeInstance;
 			ivContext.setAttribute( BranchConstants.SERVLET_CONTEXT_BRANCH, ivBranch );
 			ivContext.setAttribute( BranchConstants.INSTANCE_CONTEXT, ivBranch.getActiveInstance() );
 			 			
 			LOGGER.info("Branch Details :-   " + ivBranch.toString() );
		}
		else
		{
			LOGGER.error("Branch parameters invalid.  Defaulting set-up to main server.");
			ivInstance = branchOnline;
		}
	}
	
	// Get instance
	/**
	 * Gets the single instance of BranchParameters.
	 * 
	 * @return single instance of BranchParameters
	 */
	public String getInstance()
	{
		return( ivInstance );
	}
	
	
	// Read the XML branch parameter file
	/**
	 * Check branch parameters.
	 * 
	 * @return true, if successful
	 */
	private boolean checkBranchParameters()
	{
		boolean ok = true;
		
		if ( ! ivBranchParameters.checkParameterValue( BranchConstants.BRANCH_PARAM_BRANCH_ID ) )
		{
			LOGGER.error("Branch Id is invalid");
			ok = false;
		}
		
		if ( ! ivBranchParameters.checkParameterValue( BranchConstants.BRANCH_PARAM_ONLINE_INSTANCE ) )
		{
			LOGGER.error("Branch Online Instance is invalid");
			ok = false;
		}
		
		if ( ! ivBranchParameters.checkParameterValue( BranchConstants.BRANCH_PARAM_OFFLINE_INSTANCE ) )
		{
			LOGGER.error("Branch Offline Instance is invalid");
			ok = false;
		}
		
		return( ok );
	}
}
