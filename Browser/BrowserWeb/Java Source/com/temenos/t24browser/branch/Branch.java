////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   Branch
//
//  Description   :   Bean holding data about a Branch.  Constructed from data 
//					  stored in "branchParameters.xml".
//
//  Modifications :
//
//    19/04/06   -    Initial Version .
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.branch;

import com.temenos.t24browser.branch.BranchConstants;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class Branch.
 */
public class Branch implements Serializable
{
	
	/** The name. */
	private String name = "";					// Name of this branch - matches the T24 name
	
	/** The status. */
	private String status = "";				// Status of this branch
	
	/** The online instance. */
	private String onlineInstance = "";		// Online TC Instance name of this branch
	
	/** The offline instance. */
	private String offlineInstance = "";		// Offline TC Instance name of this branch
	
	/** The active instance. */
	private String activeInstance = "";		// Active TC Instance name of this branch
		
		
	/**
	 * Returns the name.
	 * 
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the status.
	 * 
	 * @return String
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 * Returns the onlineInstance.
	 * 
	 * @return String
	 */
	public String getOnlineInstance()
	{
		return this.onlineInstance;
	}

	/**
	 * Returns the offlineInstance.
	 * 
	 * @return String
	 */
	public String getOfflineInstance()
	{
		return this.offlineInstance;
	}
	
	
	/**
	 * Returns the activeInstance.
	 * 
	 * @return String
	 */
	public String getActiveInstance()
	{
		return this.activeInstance;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the status.
	 * 
	 * @param status The status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
		
		if ( status.equals( BranchConstants.BRANCH_ONLINE ) )
		{
			this.activeInstance = this.onlineInstance;
		}
		else
		{
			this.activeInstance = this.offlineInstance;
		}
	}

	/**
	 * Sets the onlineInstance.
	 * 
	 * @param onlineInstance The onlineInstance to set
	 */
	public void setOnlineInstance(String onlineInstance)
	{
		this.onlineInstance = onlineInstance;
	}

	/**
	 * Sets the offlineInstance.
	 * 
	 * @param offlineInstance The offlineInstance to set
	 */
	public void setOfflineInstance(String offlineInstance)
	{
		this.offlineInstance = offlineInstance;
	}
	
	/**
	 * Sets the activeInstance.
	 * 
	 * @param activeInstance The activeInstance to set
	 */
	public void setActiveInstance(String activeInstance)
	{
		this.activeInstance = activeInstance;
	}	
		
	/**
	 * Returns the Branch object as a String.
	 * 
	 * @return String
	 */
	public String toString()
	{
		String sBranch = "Name : '" + this.name + "', ";
		sBranch += "Status : '" + this.status + "', ";
		sBranch += "Online Instance : '" + this.onlineInstance + "', ";
		sBranch += "Offline Instance : '" + this.offlineInstance + "', ";
		sBranch += "Active Instance : '" + this.activeInstance + "'";
		return sBranch;
	}
	
	/**
	 * Returns the Branch object as in XML.
	 * 
	 * @return String
	 */
	public String toXml()
	{
		String sBranchXml = BranchConstants.XML_BRANCH_DETAILS_TAG;
		sBranchXml += BranchConstants.XML_BRANCH_NAME_TAG + this.name + BranchConstants.XML_BRANCH_NAME_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_STATUS_TAG + this.status + BranchConstants.XML_BRANCH_STATUS_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_ONLINE_INSTANCE_TAG + this.onlineInstance + BranchConstants.XML_BRANCH_ONLINE_INSTANCE_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_OFFLINE_INSTANCE_TAG + this.offlineInstance + BranchConstants.XML_BRANCH_OFFLINE_INSTANCE_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_ACTIVE_INSTANCE_TAG + this.activeInstance + BranchConstants.XML_BRANCH_ACTIVE_INSTANCE_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_DETAILS_TAG_C;
		return sBranchXml;
	}
	
	/**
	 * Returns the Branch details as in XML.
	 * 
	 * @return String
	 */
	public String toXmlDetails()
	{
		String sBranchXml = BranchConstants.XML_BRANCH_DETAILS_TAG;
		sBranchXml += BranchConstants.XML_BRANCH_NAME_TAG + this.name + BranchConstants.XML_BRANCH_NAME_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_STATUS_TAG + this.status + BranchConstants.XML_BRANCH_STATUS_TAG_C;
		sBranchXml += BranchConstants.XML_BRANCH_DETAILS_TAG_C;
		return sBranchXml;
	}
}
