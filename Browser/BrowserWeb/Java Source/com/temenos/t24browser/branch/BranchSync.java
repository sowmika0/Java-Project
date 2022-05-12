////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   BranchSync
//
//  Description   :   Java Thread to synchronising a request to the Branch.
//                    Used when Online to keep the Branch up-to-date.
//
//  Modifications :
//
//    17/08/06   -    Initial Version .
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.branch;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.comms.ConnectionBean;
import com.temenos.t24browser.comms.InstanceConnector;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.response.BrowserResponse;
import com.temenos.t24browser.utils.PropertyManager;


// TODO: Auto-generated Javadoc
/**
 * The Class BranchSync.
 */
public class BranchSync extends Thread
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchSync.class);
	
	/** The iv instance. */
	private String ivInstance = "";					// TC Instance for the branch
	
	/** The iv ofs req. */
	private String ivOfsReq = "";						// The OFS request string to sync
	
	/** The iv log req. */
	private String ivLogReq = "";						// The OFS request string to log - without the password
	
	/** The iv password. */
	private String ivPassword = "";					// The password for the OFS request
	
	/** The iv servlet context. */
	private ServletContext ivServletContext = null;	// The servlet context
	
	/** The iv principal. */
	private Principal ivPrincipal = null;				// The principal for secure comms
	
	/** The iv secure comms. */
	private boolean ivSecureComms = false;			// Whether the channel is secure or not
	
	/** The iv client IP. */
	private String ivClientIP = "";					// The IP address of where the request came from
	
	/** The iv parameters. */
	private PropertyManager ivParameters;
	
	/** The request. */
	private HttpServletRequest ivRequest;


	/**
	 * Instantiates a new branch sync.
	 * 
	 * @param branch the branch
	 * @param sOfsReq the s ofs req
	 * @param password the password
	 * @param context the context
	 * @param principal the principal
	 * @param secureComms the secure comms
	 * @param clientIP the client IP
	 * @param ivParameters the iv parameters
	 * @param request the Http Request
	 */
	public BranchSync( Branch branch, String sOfsReq, String password, ServletContext context, Principal principal, boolean secureComms, String clientIP, PropertyManager ivParameters, HttpServletRequest request )
	{
		ivInstance = branch.getOfflineInstance();
		ivOfsReq = sOfsReq;
		ivServletContext = context;
		ivPrincipal = principal;
		ivSecureComms = secureComms;
		ivClientIP = clientIP;
		ivLogReq = ivOfsReq;
		this.ivParameters = ivParameters;
		ivRequest = request;
			
		// Substitute the password placeholder string with the user's password
		int pos = ivOfsReq.indexOf( BranchConstants.BRANCH_PASSWORD_PLACEHOLDER );
		
		if ( pos != -1 )
		{
			int placeholderLen = (BranchConstants.BRANCH_PASSWORD_PLACEHOLDER).length();
			ivLogReq = ivOfsReq.substring( 0, pos ) + "******" + ivOfsReq.substring( pos + placeholderLen, ivOfsReq.length() );
			ivOfsReq = ivOfsReq.substring( 0, pos ) + password + ivOfsReq.substring( pos + placeholderLen, ivOfsReq.length() );
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		branchSyncRequest();
	}
	
	// Run the ofs request against the local branch
	/**
	 * Branch sync request.
	 */
	private void branchSyncRequest()
	{
		try
		{
			LOGGER.debug("Synchronising request to Branch : " + ivLogReq );

			// Create a TC connection for the offline instance
			BrowserResponse ofsReply = null;
			ConnectionBean branchConnection = new InstanceConnector( ivRequest, ivParameters );
			branchConnection.setupServer( ivInstance, 0);
			
			if ( ivSecureComms )
			{
				LOGGER.info("Request is using a secure channel");
				ofsReply = branchConnection.talkToServerOfs( ivOfsReq, ivClientIP, ivPrincipal );
			}
			else
			{
				ofsReply = branchConnection.talkToServerOfs( ivOfsReq, ivClientIP );
			}

			// Checks and changes the path of XSL file which is referenced in OFS message
			// to the /transforms/...
			DebugUtils.checkXslPath(ofsReply);
			
			String sResponseXml = ofsReply.getMsg();
			LOGGER.debug("Response from Branch Synchronisation : " + sResponseXml );
			
			if ( ( sResponseXml == null || sResponseXml.equals("") || sResponseXml.equals("OFSERROR_PROCESS" ) ) || ( sResponseXml.indexOf( BranchConstants.XML_OFS_ERROR_TEXT_TAG ) != -1 ) )
			{
				LOGGER.info("Failed Branch Sync - Request  : " + ivLogReq );
				LOGGER.info("                   - Response : " + sResponseXml );
			}				
		}
		catch (Exception e )
		{
			LOGGER.error("Error synchronising request to branch" );
		}
	}
}
