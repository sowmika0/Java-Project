////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   T24Request
//
//  Description   :   Stores details about a T24 HTTP Servlet Request.
//					  The object can be created either from an actual
//					  HttpServletRequest object or from a list of 
//					  parameter names and values stored in a collection.
//
//  Modifications :
//
//    19/02/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.request;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.utils.Constants;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.xml.XMLConstants;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.BrowserParameters;

/**
 * The Class T24Request.
 */
public class T24Request implements Serializable
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger( T24Request.class);
	
	/** The iv attributes. */
	private RequestData ivAttributes = new RequestData( RequestData.ATTRIBUTE_DATA );		// Colelction of attributes names and values
	
	/** The iv parameters. */
	private RequestData ivParameters = new RequestData( RequestData.PARAMETER_DATA );		// Colelction of parameter names and values
	
	/** The iv session id. */
	private String ivSessionId = "";							// The Session Id of a Http Request
	
	/** The http request. */
	private HttpServletRequest ivRequest = null;
	
	/** The client IP address */
	private String ivIpAddress = "";
	
	private static final String SESSION_CLIENT_IP_ADDRESS = "BrowserClientIpAddress";	// Used to save the user's IP address in the session
	
	private PropertyManager ipaddressparam;
	
	private static final String IP_ADDRESS_CHECK = "IP ADDRESS CHECK";
	
	// Create a request object from a HttpServletRequest
 	/**
	 * Instantiates a new t24 request.
	 * 
	 * @param request the request
	 */
	public T24Request( HttpServletRequest request )
	{
 		// Get all of the parameters and attributes in the request
 		ivAttributes.addData( request );
 		ivParameters.addData( request );
		ivSessionId = request.getSession().getId();
		ivRequest = request;
		ivIpAddress = RequestUtils.getRequestIpAddress( request );
	}
	 	

	// Create a request object from a Hashtable of parameters
	/**
	 * Instantiates a new t24 request.
	 * 
	 * @param request the request
	 * @param params the params
	 */
	public T24Request( HttpServletRequest request, RequestData params )
	{
		// Get all of the parameters and attributes in the request
		ivAttributes.addData( request );
		ivParameters = params;
		ivSessionId = request.getSession().getId();
	}

	/**
	 * Tries to make sure the user hasn't intercepted the request and modified the request parameters.
	 * @throws SecurityViolationException If anything suspicious in the the request is detected.
	 */
	public void validateRequestVariables() throws SecurityViolationException {

		String MultiPaneAppList = ivRequest.getParameter("MultiPaneAppList");
		if (MultiPaneAppList != null) {
			String[] prefixList = MultiPaneAppList.split(":");
			for (int i=0; i<prefixList.length; i++) {
				validateRequestVariables(prefixList[i] + ":");
			}
		} else {
			validateRequestVariables("");
		}
	}

	/**
	 * Checks if a submitted request needs to be validated, and if so then makes sure any 'nochange' fields contain the expected values, 
	 * and that no additional fields have been added to the request, including fields part of the application but not part of the version.
	 * @param prefix The AA contract prefix key
	 * @throws SecurityViolationException If a value has been changed that should not have, or if a field has been added.
	 */
	private void validateRequestVariables(String prefix) throws SecurityViolationException {

		// Get the session - we're likely to be checking it soon
		HttpSession session = ivRequest.getSession();
		ServletContext servletcontext = session.getServletContext();
		BrowserParameters paramfile = new BrowserParameters( servletcontext );
		ipaddressparam = paramfile.getParameters();
		String ipaddressValue = ipaddressparam.getParameterValue(IP_ADDRESS_CHECK);
		
		// Tampered protection check is bypassed based on the switch IP ADDRESS CHECK in parameter file.
		// Tampered IP address check will be skipped if IP ADDRESS CHECK is set to "NO".
		if ( ( ipaddressValue != null ) && ( ipaddressValue.equals("YES") ) )
		{
			// Check the client IP address to see if it matches that stored in the session
			String sessionIpAddress = getSessionIpAddress(session);
		
			if ( ( sessionIpAddress != null ) && ( ! ivIpAddress.equals(sessionIpAddress) ) )
			{
				throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + "Tampered IP Address value detected! Expected " + sessionIpAddress + ", but got " + ivIpAddress);
			}
		}
		
		if (LOGGER.isDebugEnabled()) {
			String logMsg = "";
			String routineName = ivRequest.getParameter(prefix + "routineName");
			String routineArgs = ivRequest.getParameter(prefix + "routineArgs");
			logMsg += "Request received, windowName="+ivRequest.getParameter(prefix + "windowName");
			logMsg += ", requestType="+ivRequest.getParameter(prefix + "requestType");
			if(routineName != null && routineName.equals("OS.PASSWORD") && routineArgs !=null)	{
				//Mask all the password values in routineArgs parameter
				String[] argValue = routineArgs.split(":",-1);
				if(argValue[0].equals("PROCESS.REPEAT") || argValue[0].equals("PROCESS.EXPIRED") ){
					//Mask the OldPassword and Password values in routineArgs parameter
					logMsg += ", routineArgs="+argValue[0]+":"+argValue[1]+":******:******:"+argValue[4]+":"+argValue[5];
				}
				else if(argValue[0].equals("PROCESS.CHANGE")){
					//Mask the password, newPassword and confirmNewPassword values in routineArgs parameter
					logMsg += ", routineArgs="+argValue[0]+":"+argValue[1]+":******:******:******";
				}
				else{
					logMsg += ", routineArgs="+routineArgs;
				}
			}
			else{
			    logMsg += ", routineArgs="+routineArgs;
			}
			logMsg += ", routineName="+ivRequest.getParameter(prefix + "routineName");
			logMsg += ", ofsOperation="+ivRequest.getParameter(prefix + "ofsOperation");
			logMsg += ", full request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "=");
			LOGGER.debug(logMsg);
		}
		
		// Get the windowName from the request.
		String windowName = null;
		String requestType = ivRequest.getParameter(prefix + "requestType");
		String routineName = ivRequest.getParameter(prefix + "routineName");
		if (requestType != null && ( requestType.equals("OFS.OS.CONTEXT") || requestType.equals("WEB.VALIDATION") ) || (routineName != null && routineName.equals("OS.GET.PASSBOOK.PRINT.DATA") )) {
			// Autolaunch and context enquiries use the launching window to validate their request, not their own windowName.
			// Both use OFS.OS.CONTEXT, but Autolaunch enquiries provide the windowName in the WS_parentWindow field.
			// So check if there's anything in this field and use that, otherwise use windowName
			String WS_parentWindow = ivRequest.getParameter(prefix + "WS_parentWindow");
			if (WS_parentWindow != null && !WS_parentWindow.equals("")) {
				windowName = WS_parentWindow;
			}
		}
		// Most requests will just use the windowName property.
		if (windowName == null) {
			windowName = ivRequest.getParameter(prefix + "windowName");
		}
		
		// Check the windowName, it might still be null, but this should be allowed in certain circumstances.
		if (windowName == null) {

			// It might be a request for the login screen, in which case there will be no parameters.
			if (!ivRequest.getParameterNames().hasMoreElements()) {
				return;
			}

			// It might be a logging in request, so check for some key values.
			String command =  ivRequest.getParameter("command");
			if (command != null && requestType != null && command.equals("login") && requestType.equals("CREATE.SESSION")) {
				return;
			}
			
			// It might be a logout request
			if (command != null && command.equals("getLogin")) {
				return;
			}
			
			// It might be a login request for Toolbox.
		 	if (command != null && command.equals("smartclient")) {
		 		return;
		 	}

			// It might be a request to update the password (terminated, or new).
			if (command != null && command.equals("repeatpassword")) {
				return;
			}

			// It might be a request by Toolbox for tcsversion.
		 	if (command != null && command.equals("tcsversion")) {
		 		return;
		 	}
			
		 	// It might be a request by T24 Updates Admin login
		 	if (command != null && command.equals("adminlogin")) {
		 		return;
		 	}
		 	
		 	// It might be a command line argument request
		 	if (command != null && command.equals("t24commandapi")) {
		 		return;
		 	}
		 	
			// It might be a request for the helpservlet
			if (ivRequest.getServletPath().contains("servlet/HelpServlet")) {
				return;
			}
		
			// It might be an AA composite contract, with some properties editable, and some in SEE mode.
			// In this case, the SEE mode ones won't submit a windowName, or many other fields, but they should have ofsFunction="S"
			String ofsFunction =  ivRequest.getParameter(prefix + "ofsFunction");
			if (ofsFunction != null && ofsFunction.equals("S")) {
				return;
			}
			
			 			
 			//Check for BROWSER.TIMEOUT enquiry for keepalive, because it will not contain the windowname
  			String enqName = ivRequest.getParameter("enqname");
  			String enqAction = ivRequest.getParameter("enqaction");
  			
  			if (requestType.equals("OFS.ENQUIRY") && enqName.equals("BROWSER.TIMEOUT") && enqAction.equals("RUN")) {
  				return;
  			}

			// If we get here, then it means the windowName is null and we don't know why.
			throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + "windowName is null, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
		} else if (windowName.equals("")) {
			// This should never be allowed.
			throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + "windowName is empty, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
		}
		// Check if it's a context enquiry request, in which case it will have a slightly different name
		// Don't replace the string _context_ at the end of windowname.
		// when an enquiry is really launched from context enquiry.

		// We need to know if we NOT remove the object from session memory in certain circumstances
		boolean doRemoveSessionObject = doRemoveSessionObject(prefix);
		// Set a string used for logging information
		String andRemoving = "";
		if (doRemoveSessionObject) {
			andRemoving = "and removing ";
		}

		// Check for nochange fields - if this method returns true it indicates the record is an open contract and can do something in T24.
		if (doNochangeFieldsCheck(prefix)) {
            String application = (String) ivRequest.getParameter(prefix+"application");
            String nochangeMapId = null;
            Map nochangeMap = null;
            // Avoid null pointer exception
            if ((prefix == "" || prefix == null) && (application != "" || application != null) && application.equals(Constants.AA_ARRANGEMENT_ACTIVITY) || application.equals(Constants.AA_SIMULATION_CAPTURE))
            
            {     
                  nochangeMapId = "appreq:" + windowName + "_nochangeMap";
                  nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
            }
            if(nochangeMap == null)
            {
                  nochangeMapId = prefix + windowName + "_nochangeMap";
                  nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
            }
			// If nochangeMap key is null, form new one by manually because when requests coming 
			// from multipance application the map key will not be the exact one what already been saved.
			// Ex: WEB.VALIDATION request
			if ( nochangeMap == null && (requestType.equals("WEB.VALIDATION")|| requestType.equals ("OFS.OS.CONTEXT"))) 
			{
				// Do map key formation only for request type WEB.VALIDATION
				// _nochangeMap key will be as APPLICATION:VERSION:TRANSACTIONID:WINDOWNAME:"_nochangeMap"
			    application = (String) ivRequest.getParameter("application");
				String transactionId = ivRequest.getParameter("transactionId");
				String version = ivRequest.getParameter("version");
				nochangeMapId = application + version + transactionId + ":" + windowName + "_nochangeMap";
				nochangeMap = (HashMap)session.getAttribute(nochangeMapId);					
			}
			// Build _nochangeMap key when the request is OFS.OS.CONTEXT.
			// When a context enquiry is launched from a multi pane window name will not exact as already stored. 
			// Always window name holds the value of main form application window name.
			// So form key with contextFormId in which form the request is from.
			// Then check the current application fields against stored map values.
			if (nochangeMap == null && requestType != null && requestType.equals("OFS.OS.CONTEXT") && windowName.endsWith("_context_"))
			{                                                                               
			       windowName = windowName.replaceFirst("_context_", "");
			       nochangeMapId = prefix + windowName + "_nochangeMap";
			       nochangeMap = (HashMap)session.getAttribute(nochangeMapId);                             
			}			
			if ( nochangeMap == null && ( requestType.equals("OFS.OS.CONTEXT")))
			{
				// Extract current form id in which the actuall request triggered.
				String ContextFormId = (String) ivRequest.getParameter("ContextFormId");
				nochangeMapId = ContextFormId + ":" + windowName + "_nochangeMap";
				nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
				if (nochangeMap == null) {
					// Finally check if the context enquiry request came from no frame COS, if so then retrieve it.
					// Because while application details are stored in session, fragment name is used as window name.
					windowName = ivRequest.getParameter(prefix + "currentContextEnquiryFragmentName");
					nochangeMapId = prefix + windowName + "_nochangeMap";
					nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
				}
			}
			if (nochangeMap == null) {
				// If the doNochangeFieldsCheck returns true, there MUST be a maching nochangeMap in the session.
				throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + nochangeMapId + " is null, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
			} else {
				// Remove the stored object to conserve memory as it's no longer needed.
				// A new one with updated values will be created for this windowName by the response processing if required.
				if (doRemoveSessionObject) {
					session.removeAttribute(nochangeMapId);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request - Checking " + andRemoving + "nochangeFields: " + nochangeMapId + ": " + nochangeMap);
				}
				
				// Go through each item in the map and make sure they haven't changed
				Iterator parmIterator = nochangeMap.entrySet().iterator();
		        while (parmIterator.hasNext()) {
		            Map.Entry entry = (Map.Entry) parmIterator.next();
		            String fieldName = (String)entry.getKey();
		            String storedValue = (String)entry.getValue();
		            String submittedValue = ivRequest.getParameter(fieldName);
	            	if (isTamperedValue(submittedValue, storedValue, fieldName, nochangeMap)) {
	            		String logMsg = "Tampered value detected! Expected " + fieldName + "=" + storedValue + ", but found " + submittedValue;
	            		logMsg += ". " + nochangeMapId + " = " + nochangeMap;
	            		logMsg += ", received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "=");
	            		throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + logMsg);
	            	}
				}
			}
		}
		
		// Check the noadd Fields
		// To do this, we want to go through all the fields and check for any that contain 'fieldname:'
		// If any are found, we want to check them against the ones we've got in the session.
		boolean doNoaddFieldsCheck = false;
		Enumeration fields = this.getParameterList();
		while (fields.hasMoreElements()) {
			String fieldName = (String)fields.nextElement();
			if (fieldName.contains(prefix + "fieldName:")) {
				doNoaddFieldsCheck = true;
				break;
			}
		}
		if (doNoaddFieldsCheck) {			
			String application = (String) ivRequest.getParameter(prefix+"application");
            String noaddSetId = null;
            HashSet noaddSet = null;
            //Avoid null pointer exception
            if ((prefix == "" || prefix == null) && (application != "" || application != null) && application.equals(Constants.AA_ARRANGEMENT_ACTIVITY) || application.equals(Constants.AA_SIMULATION_CAPTURE))        
            {     
                  noaddSetId = "appreq:" + windowName + "_noaddSet";
                  noaddSet = (HashSet)session.getAttribute(noaddSetId);       
            }
            if(noaddSet == null)
            {
                  noaddSetId = prefix + windowName + "_noaddSet";
                  noaddSet = (HashSet)session.getAttribute(noaddSetId); 
            }
			// If nochangeMap key is null, form new one by manually because when requests coming 
			// from multipance application the map key will not be the exact what already been saved.
			// Ex: WEB.VALIDATION request
            if ( noaddSet == null && (requestType.equals("WEB.VALIDATION")|| requestType.equals ("OFS.OS.CONTEXT"))) 
			{
				// Do map key formation only for request type WEB.VALIDATION
				// _noaddSet key will be as APPLICATION:VERSION:TRANSACTIONID:":":WINDOWNAME:"_noaddSet"
				application = (String) ivRequest.getParameter("application");
				String transactionId = ivRequest.getParameter("transactionId");
				String version = ivRequest.getParameter("version");
				prefix = application + version + transactionId + ":";
				noaddSetId = prefix + windowName + "_noaddSet";
				noaddSet = (HashSet)session.getAttribute(noaddSetId);					
			}
			// Build _noaddSet key when the request is OFS.OS.CONTEXT and window name ends with "_context_".
            if (noaddSet == null && requestType != null && requestType.equals("OFS.OS.CONTEXT") && windowName.endsWith("_context_"))
			{                                                                               
			       windowName = windowName.replaceFirst("_context_", "");
			       noaddSetId = prefix + windowName + "_noaddSet";
			       noaddSet = (HashSet)session.getAttribute(noaddSetId);                             
			}
            
            // Build _noaddSet key when the request is OFS.OS.CONTEXT or OFS.ENQUIRY.
			// When a context enquiry is launched from a multi pane window name will not exact as already stored. 
			// Always window name holds the value of main form application window name.
			// So form key with contextFormId in which form the request is from.
			// Then check the current application fields against stored map values.
			if ( noaddSet == null && ( requestType.equals("OFS.OS.CONTEXT")))// || ( requestType.equals("OFS.ENQUIRY")&& windowName.endsWith("_context_"))))
			{
				// Extract current form id in which the actuall request triggered.
				String ContextFormId = (String) ivRequest.getParameter("ContextFormId");
				noaddSetId = ContextFormId + ":" + windowName + "_noaddSet";
				noaddSet = (HashSet)session.getAttribute(noaddSetId);
				if ( noaddSet == null )
				{
					// Finally check if the context enquiry request came from no frame COS, if so then retrieve it.
					// Because while application details are stored in session, fragment name is used as window name.
					noaddSetId = prefix + windowName + "_noaddSet";
					noaddSet = (HashSet)session.getAttribute(noaddSetId);
				}
			}
			if (noaddSet == null) {
				throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + noaddSetId + " is null, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
			} else {
				// Remove the stored object to conserve memory as it's no longer needed.
				// A new one with updated values will be created for this windowName by the response processing if required.
				if (doRemoveSessionObject) {
					session.removeAttribute(noaddSetId);
				}
				if (LOGGER.isDebugEnabled()) {
				}
				// Go through each field in the request that contains 'fieldname:' and make sure we can find it in the set of allowed fields.
				fields = this.getParameterList(); // Get the list again
				Pattern enquiryPattern = Pattern.compile("fieldName:\\d+:\\d+:\\d+");
				Pattern contractPattern = Pattern.compile(prefix + "fieldName:([^:]+)(:.+)*");
				while (fields.hasMoreElements()) {
					String fieldName = (String)fields.nextElement();
					Matcher enquiryMatcher = enquiryPattern.matcher(fieldName);
					Matcher contractMatcher = contractPattern.matcher(fieldName);
					String fieldNameStub = "";
					if (enquiryMatcher.find()) {
						fieldNameStub = this.getParameter(fieldName);
					} else if (contractMatcher.find()){
						fieldNameStub = contractMatcher.group(1);
					}
					if (fieldNameStub.length() > 0) {
						// Skip the 'AUTOREFRESH' field, which has the same format but might have been added by the xslt.
						if (fieldNameStub.equals("AUTOREFRESH")) {
							continue;
						}
						// Check if the fieldname submitted was ever sent to the user, or if they've invented it.
						if (!noaddSet.contains(fieldNameStub)) {
		            		String logMsg = "Unrecognised fieldName detected: " + fieldNameStub;
		            		logMsg += ". " + noaddSetId + " = " + noaddSet;
		            		logMsg += ", received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "=");
		            		throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " " + logMsg);
						}
					}
				}
			}
		}
		// The new activity from  AA overview COS screen
		
		if (doRemoveSessionObject) {
			
			String nochangeMapId = null;
			Map nochangeMap = null;
			String noaddSetId = null;
			HashSet noaddSet = null;
			
			//when we click the bread crumb link from same window we have to clear the existing session data for this window.
			String enqAction = ivRequest.getParameter("enqaction");
			String drillAction = ivRequest.getParameter("drillaction");
			//When we click the context enquiry from same window again, then we have to clear the existing session data for this window.
			if ((requestType.equals("NO.REQUEST") && windowName.endsWith("_context_")) || (requestType.equals("OFS.ENQUIRY")&& (enqAction.equals("BACK") || (enqAction.equals("DRILL")&& !drillAction.equals("DOC")))))
			{
				nochangeMapId = windowName + "_nochangeMap";
				nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
				if (nochangeMap != null)
				{
					session.removeAttribute(nochangeMapId);
				}
				nochangeMapId = "appreq:" + windowName + "_nochangeMap";
				nochangeMap = (HashMap)session.getAttribute(nochangeMapId);
				if (nochangeMap != null)
				{
					session.removeAttribute(nochangeMapId);
				}
				noaddSetId = windowName + "_noaddSet";
				noaddSet = (HashSet)session.getAttribute(noaddSetId);
				if(noaddSet != null) 
				{
					session.removeAttribute(noaddSetId);
				}
				noaddSetId = "appreq:"+ windowName + "_noaddSet";
				noaddSet = (HashSet)session.getAttribute(noaddSetId);
				if(noaddSet != null) 
				{
					session.removeAttribute(noaddSetId);
				}
				
			}
		}
	}
	

	/**
	 * Determines if a submitted value matches the expected value for a field.
	 * @param submittedValue The value submitted in the request
	 * @param storedValue The expected value for the given field
	 * @param fieldName The fieldName of the submitted value
	 * @param nochangeMap The list of stored fields. The submitted value must be found somewhere in here or the request is suspicious.
	 * @return true if the value has been tampered with, false otherwise.
	 */
	private boolean isTamperedValue(String submittedValue, String storedValue, String fieldName, Map nochangeMap) {

		// First check the submitted vs the expected value
        if (submittedValue == null ||submittedValue.equals("")|| submittedValue.equals(storedValue)) {
        	return false; // No problem, submitted value is null (eg mv deleted), or it equals the expected value. This is the most common case.
        }
        
        // Value might have been html entity encoded, so check for this.
        // Some fields (eg overrides) might have a stored value encoded 'Balance is &quot;0&quot;' but the browser submits 'Balance is "0"'
        if (storedValue.equals(Utils.encodeHtmlEntities(submittedValue))) {
        	return false;
        }

        // The user might have inserted / deleted a multivalue group that contains a noedit field.
		// In this case the values will shift around. For example fieldName:TABLE.LIST:3=17 might change to fieldName:TABLE.LIST:2=17
		// Search through all submitted values with the same fieldNameStub as the given field checking for a tampered value.
		// Start by getting the fieldNameStub, this is the fieldName without multivalue numbers.
		String fieldNameStub = "";
		Pattern contractPattern = Pattern.compile(".*?fieldName:[^:]+");
		Matcher contractMatcher = contractPattern.matcher(fieldName);
		if (contractMatcher.find()) {
			fieldNameStub = contractMatcher.group();
		} else {
			return true; // Means the field isn't a 'fieldName:' and has been modified.
		}
		
		// Iterate through the fieldNames and see if the submitted value can be found in another field with the same stub. 
		for(Iterator it = nochangeMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
			String currentField = (String)entry.getKey();
			if (currentField.startsWith(fieldNameStub)) {
				String currentValue = (String)entry.getValue();
				if (currentValue.equals(submittedValue)) {
					return false;
					}else if(currentValue.equals(Utils.encodeHtmlEntities(submittedValue))){
						return false;
					}
				}
			}
		
		// At this point, the submitted value is not expected.
		return true;
	}

	/**
	 * Returns true if the 'nochange' fields in a request should be validated.
	 * If this method returns true, then the nochangeMap MUST exist in the session for the given windowName, 
	 * otherwise it is a hacking attempt, or there is an error in the StoreResponse class or the xml sent from T24.
	 * While checking just one field (eg activeTab) would be the most efficient, this method checks as many fields as possible, 
	 * to try to catch hackers deliberately deleting certain fields from the request in an attempt to avoid the request variables being validated.
	 * From analysis of the many request parameters from over 40 different request types, only 2 suitable fields were identified for this.
	 * A further 2 fields are almost suitable: screenMode and lockDateTime. screenMode has been used, but lockDateTime requires too many checks.
	 * 
	 * @param prefix The AA Prefix to use when looking for fields.
	 * @return true if the 'nochange' fields in a request should be validated.
	 */
	private boolean doNochangeFieldsCheck(String prefix) throws SecurityViolationException {
		// Field check is not needed for these request types causes security violtaion actually when
		// application session data is been destroyed by commit.
		// Return, if request for any web validation because it will be triggered only when the 
		// field value has been changed.
		String requestType = ivRequest.getParameter(prefix + "requestType");
		if (requestType.equals("NO.REQUEST"))
			return false;
        if (requestType.equals("OFS.OS.CONTEXT") && ivRequest.getParameter(prefix + "routineName") != null && ivRequest.getParameter(prefix + "routineName").equals("com.temenos.t24browser.validation.CheckFileValidator"))
        {
                return false;
        }

		if ( requestType.equals("OFS.GET.DEAL.SLIP") || requestType.equals("OFS.PRINT.DEAL.SLIP") || requestType.equals("WEB.VALIDATION"))
			return false;
		// If activeTab or RecordRead is set to something, this indicates a record has been opened.
		String activeTab =  ivRequest.getParameter(prefix + "activeTab");
		if (activeTab != null && !activeTab.equals("")) {
			return true;
		}
		String RecordRead =  ivRequest.getParameter(prefix + "RecordRead");
		if (RecordRead != null && !RecordRead.equals("")) {
			return true;
		}
		
		// They might have deleted the activeTab and RecordRead fields. If the ofsOperation
		// field is 'PROCESS' this usually means the request should be checked. The only time
		// the request can be PROCESS without having activeTab or RecordRead is during opening of 
		// a new contract by clicking the 'new' button. In this case the requestType and routineName
		// must be present.
		String ofsOperation = ivRequest.getParameter(prefix + "ofsOperation");
		if (ofsOperation != null && ofsOperation.equals("PROCESS")) {
			// The request MUST contain requestType=UTILITY.ROUTINE and routineName=OS.NEW.DEAL
			if (requestType != null && !requestType.equals("UTILITY.ROUTINE")) {
				throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " PROCESS request received but requestType is not UTILITY.ROUTINE, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
			}
			String routineName = ivRequest.getParameter(prefix + "routineName");
			if (routineName != null && !routineName.equals("OS.NEW.DEAL")) {
				throw new SecurityViolationException(ivRequest.getSession().getId() + " " + ivRequest.getSession().getAttribute("BrowserSignOnName") + " PROCESS request received but routineName is not OS.NEW.DEAL, received request is " + ivParameters.toStringLog().replaceAll("\n","").replaceAll("Parameter : ", ", ").replaceAll(", Value : ", "="));
			}
		}
		
		// If screenMode is set, this *usually* means a record has been opened (a bug in T24 means this is not always the case)
		// This check for screenMode is a source of bugs as it doesn't seem consistent from T24. Perhaps this check could be removed.
		// However it does give one extra field a hacker must manipulate to fool the system into not validating the request.  
		String screenMode =  ivRequest.getParameter(prefix + "screenMode");
		if (screenMode != null && !screenMode.equals("")) {
			// There's a special case when an enquiry launches a new record in a drill down, but without an autoIdStart defined.
			// In this case a bug in T24 reports screenMode = 'I', even though there is no open record.
			if (screenMode.equals("I") && ofsOperation.equals("BUILD")) {
				return false;
			}
			// There's another special case when a user clicks on the 'new' icon on a contract screen within a tabbed screen
			// In this case, screenMode = I even though no record is open, but if routineName = 'OS.NEW.DEAL' then don't validate.
			String routineName = ivRequest.getParameter(prefix + "routineName");
			if (screenMode.equals("I") && routineName.equals("OS.NEW.DEAL")) {
				return false;
			}
			// At this stage, screenMode is set and there are no more special cases. The request should be validated.
			// Transaction ID should be checked for a new contract which is not opened yet for which & so needn’t be validate    
            // This change would not have required if screenMode check itself is not available.
             String txnId = ivRequest.getParameter(prefix + "transactionId");
             if ((txnId == null) || (txnId.equals(""))) {
                  return true;
 			}
		}
		// Default: don't validate.
		return false;
	}

	/**
	 * Returns true if the nochangeMap and noaddSet should be removed after being checked.
	 * Normally these objects should be removed to release memory, but sometimes they should persist beyond the current request.
	 * @param prefix A form field prefix
	 * @return
	 */
	private boolean doRemoveSessionObject(String prefix) {
		String requestType =  ivRequest.getParameter(prefix + "requestType");
		//Special check for autoHoldDeal as we should not remove the nochangeMap.  It has special value of "SHLD" in GTSControl
 		String autoHoldCheck= ivRequest.getParameter(prefix + "GTSControl");
 		if (requestType != null && ( requestType.equals("OFS.GET.DEAL.SLIP") || requestType.equals("OFS.PRINT.DEAL.SLIP") || requestType.equals("OFS.OS.COPY") || requestType.equals("OFS.OS.CONTEXT") || requestType.equals("WEB.VALIDATION"))) {
			return false;
		}
		
		if( autoHoldCheck!=null && autoHoldCheck.equals("SHLD") )
		{ 
			return false;
		}
		
		String enqaction =  ivRequest.getParameter(prefix + "enqaction");
		if (enqaction != null && (enqaction.contains(":FAVOURITES:"))) {
			return false;
		}
		String routineName =  ivRequest.getParameter("routineName");
		
		if (routineName!=null && (routineName.equals("OS.ADD.NEW.TAB.CONTENT")))
		{
			return false;// don't remove session object when expand new tab request is been processed
		}
		if (routineName!=null && (routineName.equals("OS.GET.PASSBOOK.PRINT.DATA")))
		{
			return false;// don't remove session object for PASSBOOK printing request. 
		}
		
		return true;
	}

	public void addSessionData()
	{
		// Get the id of this request
		String requestDataId = getUniqueId( ivRequest);
		// Get the request type to determine whether we should remove the session or not.
		String requestType = "";
		String routineName = "";
		if( bulkRequest())
		{
			requestType = this.ivRequest.getParameter("appreq:requestType");
		}
		else
		{
			requestType = this.ivRequest.getParameter("requestType");
		}
		routineName = this.ivRequest.getParameter("routineName");
		
	
		// If the request type is null then try to get the appreq:requestType as we are in AA mode.
		// Get possible session data for this request
		RequestData sessionRequestData = (RequestData)ivRequest.getSession().getAttribute( requestDataId);
		//At the time of choosing relative calendar, the sessionRequestData should be null in case of multi value the property condition in AA
		//(routineName = "OS.GET.RELATIVE.CALENDAR") but should not be null (routineName = "OS.GET.CALENDAR") while choosing the date.
		
		if (routineName !=null && routineName.equals("OS.GET.RELATIVE.CALENDAR") && sessionRequestData != null)			
		{ 			
	         sessionRequestData = null ;		
	    }	

		// Append the stored data to this request if there is any.
		if( sessionRequestData != null)
		{
			//Add the session data to this request
			String propertyList = "";
			if (routineName !=null && routineName.equals("OS.ADD.NEW.TAB.CONTENT"))
			{
				String routineArgs = this.ivRequest.getParameter("routineArgs");
				String[] routineArgsList = routineArgs.split("&");
				String propertyListParams = "";
				for (int i=0; i<routineArgsList.length; i++) 
				{
					propertyList = routineArgsList[i];
					
					if (propertyList.contains("propertyList"))
					{
						String[] propertyListItems = propertyList.split("=");
						//RequestData propertyListParams = new RequestData( RequestData.PARAMETER_DATA);
		
						for(int j=1;j<propertyListItems.length; j++)
						{
							String oldPropertyHiddenFields = "";
							oldPropertyHiddenFields = getMatchingSessionData(  sessionRequestData,propertyListItems[j],false);
							if (oldPropertyHiddenFields.equals(""))
							{
								String ExpandedTabcompany = (String)this.ivRequest.getParameter( Constants.COMPANY);
								String ExpandedTabuser = (String)this.ivRequest.getParameter( Constants.USER);
								String ExpandedTabapplication = (String)this.ivRequest.getParameter( "newProperty:"+Constants.APPLICATION);
								String ExpandedTabscreenMode = "I";//(String)this.ivRequest.getParameter(Constants.SCREEN_MODE);
								String ExpandedTabkey = (String)this.ivRequest.getParameter( "currentPropertyListID");
								
								String ExpandedTabSessionId =  ExpandedTabcompany + "_" + ExpandedTabuser + "_" + ExpandedTabapplication + "_" + ExpandedTabscreenMode + "_" + ExpandedTabkey;
								String temp1 = propertyListItems[j].concat(":application");
								
								RequestData NewPropertysessionRequestData = (RequestData)ivRequest.getSession().getAttribute( ExpandedTabSessionId);
								if( NewPropertysessionRequestData != null)
								{
									oldPropertyHiddenFields = getMatchingSessionData(  NewPropertysessionRequestData,propertyListItems[j],false);
									
								}
							}
							
							propertyListParams += oldPropertyHiddenFields;	
						}
						
						String testing="";
						
		
					}
					
				}
				routineArgs += propertyListParams;
				ivParameters.addDataItem( "routineArgs",routineArgs);
			}
			else
			{
			ivParameters.addData( sessionRequestData);
				// to pick session variables for each of the property defined in AAA record. For Expandable tab requirement
				String appList = this.ivRequest.getParameter( XMLConstants.XML_REQUEST_APP_LIST );
				if (appList != null )
				{
					String[] appListItems = appList.split(":");
					for(int j=1;j<appListItems.length; j++)
					{
						String ExpandedTabcompany = (String)this.ivRequest.getParameter( appListItems[j] + ":" + Constants.COMPANY);
						String ExpandedTabuser = (String)this.ivRequest.getParameter( appListItems[j] + ":" + Constants.USER);
						String ExpandedTabapplication = (String)this.ivRequest.getParameter( appListItems[j] + ":" + Constants.APPLICATION);
						String ExpandedTabscreenMode = "I"; //(String)this.ivRequest.getParameter( "appreq:" + Constants.SCREEN_MODE);
						String ExpandedTabkey = (String)this.ivRequest.getParameter( appListItems[j] + ":" + Constants.TRANS_ID);
						
						// Set the id for this response
						String ExpandedTabSessionId =  ExpandedTabcompany + "_" + ExpandedTabuser + "_" + ExpandedTabapplication + "_" + ExpandedTabscreenMode + "_" + ExpandedTabkey;
						RequestData sessionExpandedTabRequestData = (RequestData)ivRequest.getSession().getAttribute( ExpandedTabSessionId);
						if (sessionExpandedTabRequestData != null)
						{
							ivParameters.addData( sessionExpandedTabRequestData);							
							ivRequest.getSession().removeAttribute(ExpandedTabSessionId); //remove the added tab data which doesn't need to add for second validation.
						}	
					}
				}
				
					
			}
			LOGGER.debug( "\n ----- Retrieve session: " + requestDataId + "----- \n");
			LOGGER.debug( sessionRequestData);
			LOGGER.debug( "\n---------------End--------------\n");
			String autoHoldCheck = this.ivRequest.getParameter("GTSControl");
			if (routineName == null)
			{
				routineName = "";
			}
			
			String routineArgs = ivRequest.getParameter("routineArgs");
			String[] argValue = {""};
			if (routineArgs != null)
			{
				argValue = routineArgs.split(":",-1);
			}			
			
			// Remove session unless we are a context enquiry, in which case leave it as this happens on separate window. 
			if( (!requestType.equals("OFS.OS.CONTEXT")) && (!requestType.equals("WEB.VALIDATION")) && (!requestType.equals("OFS.PRINT.DEAL.SLIP"))&& (!requestType.equals("OFS.GET.DEAL.SLIP")) && (!requestType.equals("OFS.OS.COPY")) && ((requestType.equals("NO.REQUEST")) && (!argValue[0].equals("ENQ"))) && (!routineName.equals("OS.ADD.NEW.TAB.CONTENT")) && (!routineName.equals("OS.GET.CALENDAR")) && !((autoHoldCheck != null) && autoHoldCheck.equals("SHLD")) && (!routineName.equals("OS.GET.RECURRENCE")))
			{
				// Remove the stored data since we have appended it.
				ivRequest.getSession().removeAttribute( requestDataId);
				LOGGER.debug( "\n ----- Removed session: " + requestDataId + "----- \n");
			}
		}
		else
		{
			LOGGER.debug("\n------->>> Nothing to add -----: \n" + requestDataId);
		}
	}

	
	private String getUniqueId( HttpServletRequest request)
	{
		String prefix = "";
		// If we are AA then we need to add the prefix of 'appreq'
		if( RequestUtils.bulkRequest( request))
		{
			prefix = Constants.APP_REQ + ":";
		}
		// Create a unique id out of Company User Application & Id
		String company = (String)request.getParameter( prefix + Constants.COMPANY);
		String user = (String)request.getParameter( prefix + Constants.USER);
		String application = (String)request.getParameter( prefix + Constants.APPLICATION);
		String screenMode = (String)request.getParameter( prefix + Constants.SCREEN_MODE);
		String key = (String)request.getParameter( prefix + Constants.TRANS_ID);
			
		// Set the id for this response
		return company + "_" + user + "_" + application + "_" + screenMode + "_" + key;
	}
	 	
	
	// Create a request object from a Hashtable
	/**
	 * Instantiates a new t24 request.
	 * 
	 * @param params the params
	 */
	public T24Request( RequestData params )
	{
		ivParameters = params;
	}
	
	
	/**
	 * Get an attribute value from the request.
	 * 
	 * @param attrName the attr name
	 * 
	 * @return String
	 */
	public String getAttribute( String attrName )
	{
		return( this.getAttributeValue( attrName ) );
	}
		
	/**
	 * Get an attribute value from the request.
	 * 
	 * @param attrName the attr name
	 * 
	 * @return String
	 */
	public String getAttributeValue( String attrName )
	{
		return( ivAttributes.getValue( attrName ) );
	}
	
	/**
	 * Try to get a value from either attributes or parameters
	 * @param attrName the attr name
	 * @return String
	 */
	public String getValue( String name)
	{
		String value = null;
		value = getParameterValue( name);
		// If there is no parameter, try to get an attribute
		if( value == null)
		{
			value = getAttributeValue( name);
		}
		
		return value;
	}
	
	/**
	 * Gets the list of attributes in the request.
	 * 
	 * @return RequestData
	 */
	public RequestData getAttributes()
	{
		return( ivAttributes );
	}
	
	/**
	 * Get a list of the attribute names in the request.
	 * 
	 * @return Enumeration
	 */	
	public Enumeration getAttributeList()
	{
		return( ivAttributes.getNameList() );
	}	
	
	/**
	 * Adds an attribute in the request.
	 * 
	 * @param attrName The name of the attribute
	 * @param attrValue The value of the attribute
	 */
	public void setAttribute( String attrName, String attrValue )
	{
		ivAttributes.addDataItem( attrName, attrValue );
	}
		
	/**
	 * Adds a parameter in the request.
	 * 
	 * @param paramName The name of the parameter
	 * @param paramValue The value of the parameter
	 */
	public void setParameter( String paramName, String paramValue )
	{
		ivParameters.addDataItem( paramName, paramValue );
	}		
	
	/**
	 * Sets up the list of attributes in the request.
	 * 
	 * @param request The Http request
	 */
	public void setAttributes( HttpServletRequest request )
	{
		ivAttributes.addData( request );
	}
	
	/**
	 * Sets up the list of attributes in the request.
	 * 
	 * @param attrs Attribute request data
	 */
	public void setAttributes( RequestData attrs )
	{
		ivAttributes = attrs;
	}


	/**
	 * Get a sorted list of the attribute names in the request.
	 * 
	 * @return ArrayList
	 */	
	public ArrayList getSortedAttributeList()
	{
		return( ivAttributes.getSortedNameList() );
	}

	/**
	 * Get a parameter value from the request.
	 * 
	 * @param paramName the param name
	 * 
	 * @return String
	 */
	public String getParameter( String paramName )
	{
		return( this.getParameterValue( paramName ) );
	}

	/**
	 * Get a parameter value from the request.
	 * 
	 * @param paramName the param name
	 * 
	 * @return String
	 */
	public String getParameterValue( String paramName )
	{
		return( ivParameters.getValue( paramName ) );
	}	
		
	/**
	 * Get a list of the parameter names in the request.
	 * 
	 * @return Enumeration
	 */	
	public Enumeration getParameterList()
	{
		return( ivParameters.getNameList() );
	}
	
	/**
	 * Get a list of the parameter names in the request.
	 * 
	 * @return Enumeration
	 */	
	public Enumeration getParameterNames()
	{
		return( ivParameters.getNameList() );
	}

	/**
	 * Sets up the list of parameters in the request.
	 * 
	 * @param request The Http request
	 */
	public void setParameters( HttpServletRequest request )
	{
		ivParameters.addData( request );
	}
	
	/**
	 * Get a sorted list of the parameter names in the request.
	 * 
	 * @return ArrayList
	 */	
	public ArrayList getSortedParameterList()
	{
		return( ivParameters.getSortedNameList() );
	}

	/**
	 * Get a table of parameters that start with a specified prefix.
	 * 
	 * @param sPrefix The prefix for matching parameters
	 * @param bRemovePrefix Whether to remove the prefix from the parameter name
	 * 
	 * @return Enumeration
	 */	
	public RequestData getMatchingParameters( String sPrefix, boolean bRemovePrefix )
	{
		RequestData params = new RequestData( RequestData.PARAMETER_DATA);
		Enumeration paramList = ivParameters.getNameList();
		
		while ( paramList.hasMoreElements() )
		{
			String paramName = (String)paramList.nextElement();

			if ( paramName.startsWith( sPrefix ) )
			{
				String paramValue = this.getParameterValue( paramName );
				
				if ( bRemovePrefix )
				{
					//remove the fragment name if present along with fieldName
					String fragmentSuffix = this.getParameterValue("WS_FragmentName");
					if(fragmentSuffix != null && paramName.contains(fragmentSuffix))
					{
						paramName = paramName.substring( sPrefix.length() + fragmentSuffix.length()+ 2 );
					}else
					{
						paramName = paramName.substring( sPrefix.length() + 1 );
					}
				}

				params.addDataItem( paramName, paramValue );
			}
		}
		
		return( params );
	}
		
	public String getSessionValue( RequestData sessionData, String paramName )
	{
		return( sessionData.getValue( paramName ) );
	}
	/**
	 * Get a table of session that start with a specified prefix.
	 * 
	 * @param sPrefix The prefix for matching parameters
	 * @param bRemovePrefix Whether to remove the prefix from the session name
	 * 
	 * @return String
	 */	
	public String getMatchingSessionData( RequestData sessionData, String sPrefix, boolean bRemovePrefix )
	{
		String params = ""; 
		Enumeration paramList = sessionData.getNameList();
		
		while ( paramList.hasMoreElements() )
		{
			String paramName = (String)paramList.nextElement();

			if ( paramName.startsWith( sPrefix ) )
			{
				String paramValue = this.getSessionValue( sessionData,paramName );
				
				if ( bRemovePrefix )
				{
					paramName = paramName.substring( sPrefix.length() + 1 );
				}

				//params.addDataItem( paramName, paramValue );
				params += "&"+paramName+"="+paramValue;
			}
		}
		
		return( params );
	}
	/**
	 * Convert a Http Request in to a string showing the parameters.
	 * 
	 * @return String
	 */
	public String toString()
	{
		String sRequest = "\n";
		sRequest += "Session Id = " + ivSessionId + "\n";

		// Get the attributes
		sRequest += ivAttributes.toString();
		sRequest += ivParameters.toString();
		
		return( sRequest );
	}	
	
	/**
	 * Convert a Http Request in to a string showing the parameters.
	 * 
	 * @return String
	 */
	public String toXml()
	{
		String sRequest = "<sessionId>" + ivSessionId + "</sessionId>";

		// Get the attributes
		sRequest += ivAttributes.toXml();
		sRequest += ivParameters.toXml();
		
		return( sRequest );
	}	
		
	/**
	 * Indicates whether the request contains multiple requests or not.
	 * 
	 * @return boolean
	 */
	public boolean bulkRequest()
	{
		// Use this method to determine if bulk request.
		// Check if an MultiPaneAppList parameter exists indicating that there
		// is more than one application in the request object
		return( ivParameters.dataItemExists( XMLConstants.XML_REQUEST_APP_LIST ) );
	}
	
	/**
	 * Indicates whether the request is for a sub-pane or not.
	 * 
	 * @return boolean
	 */
	public boolean subPaneRequest()
	{
		// Check if a subPane parameter exists indicating that there
		// is more than one application in the request object
		String paramValue = this.getParameterValue( XMLConstants.XML_WS_SUB_PANE );
		
		if ( paramValue != null && paramValue.equals( "Y" ) )
		{
			return( true );
		}
		else
		{
			return( false );
		}
	}
	
	/*Method getRequestIPAddress(request) moved to RequestUtils */
        

	
	public String getSessionIpAddress( HttpSession session )
	{
		String addr = (String) session.getAttribute( SESSION_CLIENT_IP_ADDRESS );
		return (addr);
	}
	
	// Returns all field parameters
	public Hashtable getFieldData()
	{
		// Return all parameters that contain "fieldName:" in their name
		Hashtable fieldData = new Hashtable();
		
		Enumeration fields = this.getParameterList();
		while ( fields.hasMoreElements() )
		{
			String fieldName = (String)fields.nextElement();
			if (fieldName.contains("fieldName:"))
			{
				String fieldValue = this.getParameterValue( fieldName );
				
				if ( fieldValue == null )
				{
					fieldValue = "";
				}
				
				fieldData.put( fieldName, fieldValue);
			}
		}
		
		return fieldData;
	}
	/**
	*Check user hasn't intercepted the request and modified the request parameters coming from enqrequest and genrequest.jsp.
	* @throws SecurityViolationException if suspicious character detected.
	 */
	public void validateInputVariables() throws SecurityViolationException  {
		//get parameters defined in enqrequest.jsp and genrequest.jsp which are not validated in validateRequestVariables
		String routName=ivRequest.getParameter("routineName");
		String compId=ivRequest.getParameter("companyId");
		String reqTabid=ivRequest.getParameter("reqTabid");
		String enqAction =ivRequest.getParameter("enqaction");
		String WS_replaceAll=ivRequest.getParameter("WS_replaceAll");	
		//check routineName,if any suspicious parameter detected throw security violation error
		if (routName != null && !(routName.equals("")))
		{
			Pattern contractPattern = Pattern.compile("^[a-zA-Z0-9.: ]*$");
			Matcher argMatcher = contractPattern.matcher(routName);
			if (!argMatcher.find())
			{
				throw new SecurityViolationException();
			}
		}
		//check enqAction, RUN,SELECTION,DRILL are only possible values.if any suspicious parameter throws security violation
		if(enqAction != null && !(enqAction.equals("")))
		{
			Pattern contractPattern = Pattern.compile("^[a-zA-Z0-9.:_ ]*$");
			Matcher argMatcher = contractPattern.matcher(enqAction);
			if (!argMatcher.find())
			{
				throw new SecurityViolationException();
			}
		}
		//check compId, special characters allowed throw error
		if(compId!=null && !(compId.equals("")))
		{
			Pattern contractPattern = Pattern.compile("^[a-zA-Z0-9]*$");
			Matcher argMatcher = contractPattern.matcher(compId);
			if (!argMatcher.find())
			{
				throw new SecurityViolationException();
			}
		}
		
		//check reqTabid,if any suspicious parameter detected throw security violation error
		if(reqTabid!=null && !(reqTabid.equals("")))
		{
			Pattern contractPattern = Pattern.compile("^[a-zA-Z0-9_]*$");
			Matcher argMatcher = contractPattern.matcher(reqTabid);
			if (!argMatcher.find())
			{
				throw new SecurityViolationException();
			}
		}
		if(WS_replaceAll!=null && !(WS_replaceAll.equals("")))
		{
			Pattern contractPattern = Pattern.compile("^[a-zA-Z ]*$");
			Matcher argMatcher = contractPattern.matcher(WS_replaceAll);
			if (!argMatcher.find())
			{
				throw new SecurityViolationException();
				}
		}
		
	}

}
