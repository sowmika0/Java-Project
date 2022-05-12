package com.temenos.t24browser.branch;

// TODO: Auto-generated Javadoc
/**
 * Contains constant strings used for Branch Resilience.
 */
public class BranchConstants 
{
	// Branch Parameters file constants
	/** The Constant BROWSER_PARAM_BRANCH. */
	public static final String BROWSER_PARAM_BRANCH = "Branch";
	
	/** The Constant SERVLET_CONTEXT_BRANCH. */
	public static final String SERVLET_CONTEXT_BRANCH = "Branch";
	
	/** The Constant BRANCH_PARAM_FILE. */
	public static final String BRANCH_PARAM_FILE = "branchParameters.xml";
	
	/** The Constant BRANCH_STRINGS_FILE. */
	public static final String BRANCH_STRINGS_FILE = "branchStrings.xml";
	
	/** The Constant BRANCH_PARAM_BRANCH_ID. */
	public static final String BRANCH_PARAM_BRANCH_ID = "Branch Id";
	
	/** The Constant BRANCH_PARAM_ONLINE_INSTANCE. */
	public static final String BRANCH_PARAM_ONLINE_INSTANCE = "Online Instance";
	
	/** The Constant BRANCH_PARAM_OFFLINE_INSTANCE. */
	public static final String BRANCH_PARAM_OFFLINE_INSTANCE = "Offline Instance";
	
	/** The Constant BRANCH_STATE_FILE. */
	public static final String BRANCH_STATE_FILE = "branchState.dat";

	// Servlet Context & Session constants
	/** The Constant INSTANCE_CONTEXT. */
	public static final String INSTANCE_CONTEXT = "BrowserContextInstance";		// Used to save the TC instance in the servlet context
	
	/** The Constant INSTANCE_SESSION. */
	public static final String INSTANCE_SESSION = "BrowserSessionInstance";		// Used to save the TC instance in the user's session
	
	/** The Constant BRANCH_STATUS_CONTEXT. */
	public static final String BRANCH_STATUS_CONTEXT = "BrowserBranchStatus";	// Used to save the branch status
	
	/** The Constant SESSION_BA_USER. */
	public static final String SESSION_BA_USER = "BrowserBranchAdminUser";  		// Branch Admin Flag
	
	/** The Constant INSTANCE_CONTEXT_TYPE. */
	public static final String INSTANCE_CONTEXT_TYPE = "CONTEXT";				// Context instance type
	
	/** The Constant INSTANCE_USER_TYPE. */
	public static final String INSTANCE_USER_TYPE = "USER";						// User instance type
	
	/** The Constant INSTANCE_REQUEST_TYPE. */
	public static final String INSTANCE_REQUEST_TYPE = "REQUEST";				// Request instance type
	
	/** The Constant BRANCH_STRINGS_CONTEXT. */
	public static final String BRANCH_STRINGS_CONTEXT = "BrowserBranchStrings";	// Strings used on Branch screens saved in the servlet context
	
	// Branch status's
	/** The Constant BRANCH_ONLINE. */
	public static final String BRANCH_ONLINE  = "ONLINE";						// Online status
	
	/** The Constant BRANCH_OFFLINE. */
	public static final String BRANCH_OFFLINE = "OFFLINE";						// Offline status
	
	// XML Tags
	/** The Constant XML_BRANCH_DETAILS_TAG. */
	public static final String XML_BRANCH_DETAILS_TAG = "<brDetails>";
	
	/** The Constant XML_BRANCH_DETAILS_TAG_C. */
	public static final String XML_BRANCH_DETAILS_TAG_C = "</brDetails>";
	
	/** The Constant XML_BRANCH_NAME_TAG. */
	public static final String XML_BRANCH_NAME_TAG = "<brName>";
	
	/** The Constant XML_BRANCH_NAME_TAG_C. */
	public static final String XML_BRANCH_NAME_TAG_C = "</brName>";
	
	/** The Constant XML_BRANCH_STATUS_TAG. */
	public static final String XML_BRANCH_STATUS_TAG = "<brStatus>";
	
	/** The Constant XML_BRANCH_STATUS_TAG_C. */
	public static final String XML_BRANCH_STATUS_TAG_C = "</brStatus>";
	
	/** The Constant XML_BRANCH_ONLINE_INSTANCE_TAG. */
	public static final String XML_BRANCH_ONLINE_INSTANCE_TAG = "<brOnlineInstance>";
	
	/** The Constant XML_BRANCH_ONLINE_INSTANCE_TAG_C. */
	public static final String XML_BRANCH_ONLINE_INSTANCE_TAG_C = "</brOnlineInstance>";
	
	/** The Constant XML_BRANCH_OFFLINE_INSTANCE_TAG. */
	public static final String XML_BRANCH_OFFLINE_INSTANCE_TAG = "<brOfflineInstance>";
	
	/** The Constant XML_BRANCH_OFFLINE_INSTANCE_TAG_C. */
	public static final String XML_BRANCH_OFFLINE_INSTANCE_TAG_C = "</brOfflineInstance>";
	
	/** The Constant XML_BRANCH_ACTIVE_INSTANCE_TAG. */
	public static final String XML_BRANCH_ACTIVE_INSTANCE_TAG = "<brActiveInstance>";
	
	/** The Constant XML_BRANCH_ACTIVE_INSTANCE_TAG_C. */
	public static final String XML_BRANCH_ACTIVE_INSTANCE_TAG_C = "</brActiveInstance>";
	
	/** The Constant XML_BRANCH_ADMIN_LOGIN_TAG. */
	public static final String XML_BRANCH_ADMIN_LOGIN_TAG = "<branchAdminLogin>";
	
	/** The Constant XML_BRANCH_ADMIN_LOGIN_TAG_C. */
	public static final String XML_BRANCH_ADMIN_LOGIN_TAG_C = "</branchAdminLogin>";
	
	/** The Constant XML_OFS_ERROR_TEXT_TAG. */
	public static final String XML_OFS_ERROR_TEXT_TAG = "<errorText>";
	
	/** The Constant XML_OFS_ERROR_TEXT_TAG_C. */
	public static final String XML_OFS_ERROR_TEXT_TAG_C = "</errorText>";
	
	// Constant strings
	/** The Constant BRANCH_ADMIN_LOGIN_HTML. */
	public static final String BRANCH_ADMIN_LOGIN_HTML = "html/baLogin.html"; 		// Branch Administrator Login page
    
    /** The Constant BRANCH_OFS_REQ_STRING. */
    public static final String BRANCH_OFS_REQ_STRING = "<brRequestString>";			// Start of OFS Request string XML tag
    
    /** The Constant BRANCH_OFS_REQ_STRING_C. */
    public static final String BRANCH_OFS_REQ_STRING_C = "</brRequestString>";		// End of OFS Request string XML tag
	
	/** The Constant BRANCH_REQUEST_ROUTINE. */
	public static final String BRANCH_REQUEST_ROUTINE = "OS.POST.OFS.MESSAGE";			// Utility Request routine run a request locally at the branch
	
	/** The Constant BRANCH_STATUS_UPDATE_ROUTINE. */
	public static final String BRANCH_STATUS_UPDATE_ROUTINE = "OS.UPDATE.BRANCH.STATUS";	// Utility Request routine to update the branch status
	
	/** The Constant BRANCH_PASSWORD_PLACEHOLDER. */
	public static final String BRANCH_PASSWORD_PLACEHOLDER = "<*PASSWORD*>";				// String used as a place holder for the Password in an OFS string
    
    // String constants in the branchStrings.xml file
    /** The Constant BR_STRING_ADMIN_LOGIN_TITLE. */
    public static final String BR_STRING_ADMIN_LOGIN_TITLE = "BRANCH_ADMIN_LOGIN_TITLE";
    
    /** The Constant BR_STRING_MESSAGE_STATE_CHANGED. */
    public static final String BR_STRING_MESSAGE_STATE_CHANGED = "MESSAGE_STATE_CHANGED";
    
    /** The Constant BR_STRING_ERROR_NOT_BRANCH_SERVER. */
    public static final String BR_STRING_ERROR_NOT_BRANCH_SERVER = "ERROR_NOT_BRANCH_SERVER";
	
	/** The Constant BR_STRING_ERROR_NOT_BRANCH_ADMIN. */
	public static final String BR_STRING_ERROR_NOT_BRANCH_ADMIN = "ERROR_NOT_BRANCH_ADMIN";
}
