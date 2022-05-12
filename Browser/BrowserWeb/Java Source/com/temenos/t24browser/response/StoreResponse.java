package com.temenos.t24browser.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.request.T24Request;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.Constants;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.xml.XMLConstants;


public class StoreResponse implements Serializable {
	// Needed to satisfy serializable interface.
	public static final long serialVersionUID = 0;
	/** 'duplicatecheck' to check the duplicate response or not. */
	public static final String XML_TAG_DUPLICATE_CHECK = "<duplicatecheck>";
	public static final String XML_TAG_DUPLICATE_CHECK_C = "</duplicatecheck>";
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreResponse.class);

	private String responseXml;
	private T24Request ivRequest;
	private HttpSession ivSession;
	private PropertyManager nochangeFields;
	private Map<String,String> nochangeMap;
	private Set<String> noaddSet;
	// Map to store document download details as key-value pair.
 	// Drill info as key and drill item as value.
 	private Map<String, String> docDrillItem;

	/**
	 * Constructor, just store references to the given parameters.
	 * @param responseXml
	 * @param nochangeFields
	 * @param ivRequest
	 * @param ivSession
	 */
	public StoreResponse( String responseXml, PropertyManager nochangeFields, T24Request ivRequest, HttpSession ivSession) {
		
		// Store a reference to the xml string
		this.responseXml = responseXml;
		this.ivRequest = ivRequest;
		if (nochangeFields == null) {
			// In case of document download nochangeFields will be passed as null explicitly from BrowserBean.java.
 			// Only Type's instance variables needs to be instantiated to store document details in session.
 			String docDownload = Utils.getNodeFromString( responseXml, "docDownload");
 			String responseType = Utils.getNodeFromString(responseXml, "responseType");
 			// Don't throw exception for document download response and nochangeFields will be instantiated.
 			if ( docDownload.equals(null) && docDownload.equals("") && !responseType.equals("XML.DOCUMENT.SERVICE"))
 			{
 				throw new IllegalArgumentException("nochangeFields is null. (check web.xml has nochangeFields context-param)");
 			}
		} else {
			this.nochangeFields = nochangeFields;
		}
		this.ivSession = ivSession;
	}

	/**
 	 * Method to store document details in session.
 	 * @author tskumar
 	 * @param docDownload T24 response to extract download details.
 	 * @return void
 	 */
 	public void storeDocumentIdsInSession(String docDownload)
 	{
 		// Create an instance of type HashMap.
 		docDrillItem = new HashMap<String, String>();
 		// Make array lists for drill info and drill item. 
 		ArrayList docDownloadDrill = new ArrayList();
 		ArrayList docDownloadItem = new ArrayList();
 		// Extract drill info details from response.
 		docDownloadDrill = Utils.getAllMatchingNodes(docDownload, "docDownloadDrill");
 		// Extract drill item details from response.
 		docDownloadItem = Utils.getAllMatchingNodes(docDownload, "docDownloadItem");
 		// Iterator and extract each value.
 		Iterator iterator1 = docDownloadItem.iterator();
 		Iterator iterator = docDownloadDrill.iterator();
 		while (iterator.hasNext()) {
 			String docDrill = (String)iterator.next();
 			String docItem = (String)iterator1.next();
 			// Decode special characters which are encoded in T24 server.
 			docItem = Utils.decodeHtmlEntities(docItem);
 			// put into Map docDrill as key and docItem as value.
 			docDrillItem.put(docDrill, docItem);
 		}
 		// Get the current window name.
 		String windowName = getWindowName("");
 		// Get the session userId to store details
 		// So that internal and external users can be differentiated.
 		String userId = (String) ivSession.getAttribute( "BrowserSignOnName" );
 		// Have key to store document details in session.
 		String docDrillItemMapKey = windowName + "_" + userId + "_docDrillItem";
 		// Add into session by created key.
 		addObjectToSession(docDrillItemMapKey, docDrillItem);
 		LOGGER.debug("Document details are stored in session for user=" + userId);
 	}
 
 	/**
 	 * Method to retrieve document details from session.
 	 * @author tskumar
 	 * @param Key key to get document ID from session.
 	 * @return String document ID will be returned.
 	 */
 	public String getDocumentDrilledItem(String Key)
 	{
 		// Extract the window name to create key.
 		String windowName = getWindowName("");
 		// Extract userId from session so that exact user name will be retrieved.
 		// It may internal or external user.
 		String userId = (String) ivSession.getAttribute( "BrowserSignOnName" );
 		String docDrillItemMapKey = windowName + "_" +  userId + "_docDrillItem";
 		// Retrieve stored Map document download details. 
 		Map docDrillItemMap = (HashMap)ivSession.getAttribute(docDrillItemMapKey);
 		
 		// In case of Multi download window name will be not be same as stored one.
 		// So have parent window as window name.
 		if ( docDrillItemMap == null )
 		{
 			windowName = ivRequest.getParameter("MultiDocumentEnqParentWindow");
 			docDrillItemMapKey = windowName + "_docDrillItem";
 			docDrillItemMap = (HashMap)ivSession.getAttribute(docDrillItemMapKey);
 			if (docDrillItemMap.equals(null))
 			{
 				LOGGER.debug("Requested Document ID couldn't find.");
 				return "";
 			}
 		}
 		// return document ID as string.
 		LOGGER.debug("Picked document ID(s) from session="+ (String)docDrillItemMap.get(Key));
 		return (String)docDrillItemMap.get(Key);
 	}

	/**
	 * Main entry point and process controller for calling applications.
	 *
	 */
	public void storeValuesInSession() {
		
		// Check whether the request is a bulk response or not
		if( RequestUtils.bulkResponse( responseXml)) {
			// It might be a 'single' bulk response which should be processed as a normal contract, so count how many responses there are.
			// Count 'ofsSessionResponse' rather than 'response' since some bulk responses can contain empty responses or overrides.
			ArrayList responses = Utils.getAllMatchingNodes(responseXml, XMLConstants.XML_OFS_SESSION_RESPONSE_TAG);
			if (responses.size() == 1) {
				this.processContract("",responseXml);
			} else {
				this.processBulkResponse(responses);
			}

		} else if( this.isContract(responseXml)) {
			// Process a normal contract response
			this.processContract("",responseXml);

		} else if ( this.isEnquiry(responseXml)) {
			// Process an enquiry
			this.processEnquiry(responseXml);
		} else {
			String transSign = Utils.getNodeFromString( responseXml, "transSign");
			if (!(transSign.equals("")))
			{
				addObjectToSession("transSign", transSign);
			}
			LOGGER.debug(ivSession.getId() + " "  + ivSession.getAttribute("BrowserSignOnName") + " "  + "Nothing to store");
		}
	}
	
//	 Private -------------------------------------------------------------------------------------------------------

	/**
	 * Returns true if the given xml is for a T24 contract.
	 * @param response The response xml to examine.
	 * @return true if the xml is for a T24 contract.
	 */
	private boolean isContract( String response) {
		String application = Utils.getNodeFromString( response, Constants.APP);
        String responseType =Utils.getNodeFromString( response, "responseType");
        //Calendar and Frequency Should not be treated as Contract 
        if (responseType.equals("XML.CALENDAR") || responseType.equals("XML.GET.FREQUENCY"))
        {
            application ="";
        }
		return ( !application.equals( ""));
	}
	
	/**
	 * Returns true if the given xml contains xml for a T24 enquiry selection panel.
	 * @param response The response xml to examine.
	 * @return true if the xml has a T24 enquiry selection panel.
	 */
	private boolean isEnquiry( String response) {
		// Check for an enquiry selection tag which has the fieldsnames.
		return !(Utils.getXpathFromString( response, "enqsel").equals(""));
	}
	

	/**
	 * Main controller for bulk responses.
	 * Works out the 'AA prefix' for each response in the list, then processes each response.
	 * @param responses The list of responses.
	 */
	private void processBulkResponse(ArrayList responses)
	{
		Iterator it = responses.iterator();
		while(it.hasNext()) {
			String response = (String)it.next();
			String application = Utils.getNodeFromString( response, Constants.APP);
			String version = Utils.getNodeFromString( response, Constants.VERSION);
			String key = Utils.getNodeFromString( response, Constants.KEY);
			String prefix = "";
			if (application.equals(Constants.AA_ARRANGEMENT_ACTIVITY) || application.equals(Constants.AA_SIMULATION_CAPTURE)) {
				prefix = Constants.APP_REQ + ":";
			} else {
				prefix = application + version + key + ":";
			}
			this.processContract( prefix, response);
		}
	}
	
	/**
	 * Main processor of single contract xml - stores lists of information that either shouldn't change or be added to.
	 * @param prefix The AA prefix (or "") to apply to fieldnames.
	 * @param response The contract xml.
	 */
	private void processContract( String prefix, String response)
	{
		String windowName = getWindowName(prefix);
		// window name changed for a special case when commit request navigates to confirm version.
		ServletContext servletcontext = ivSession.getServletContext();
		BrowserParameters params = new BrowserParameters( servletcontext );
		PropertyManager ivParameters = params.getParameters();
		String arcIb = ivParameters.getParameterValue("Product");
		if ( (arcIb!=null) && (arcIb.equals("ARC-IB")) ) 
		{
			ArrayList toolitems = new ArrayList();
			toolitems = Utils.getAllMatchingNodes(response, "tool");
			// Extract tool item details from response.
 			Iterator iterator = toolitems.iterator();
			while (iterator.hasNext()) {
				String toolname = (String)iterator.next();
				String toolcap = Utils.getNodeFromString(toolname,"cap");
				// identify commit request which navigates to confirm version to store in appropriate nochangemap
				if (toolcap.equals("Confirm")) {
					if(ivRequest != null) {
						String parentComposite = ivRequest.getParameter("WS_parentComposite");
						String replaceAll = ivRequest.getParameter("WS_replaceAll");
						if(parentComposite != null &&  replaceAll.equals("yes")) {
							windowName = ivRequest.getValue("WS_parentComposite");
						}
					}
				}
			}
		}		
		//Special check for autoHoldDeal for we should not store the response.  It has special value of "SHLD" in GTSControl
 		String autoHoldCheck= ivRequest.getParameter(prefix + "GTSControl");
		if( autoHoldCheck!=null && autoHoldCheck.equals("SHLD") )
 		{ 
 			return;
 		}	
		//To store the no change fields for AAA's new expanded tab.
		String groupTab = Utils.getNodeFromString( response, "groupTab");
		String duplicateCheck="";		// To check the tab response is dupicate or not for AA tabs.
		if ((!groupTab.equals( "")) && (prefix.equals(""))){
			
			String application = Utils.getNodeFromString( responseXml, Constants.APP);
			String version = Utils.getNodeFromString( responseXml, Constants.VERSION);
			String key = Utils.getNodeFromString( responseXml, Constants.KEY);
			prefix = application + version + key + ":";
			int startTagPos = response.indexOf(XML_TAG_DUPLICATE_CHECK);
            int endTagPos = response.indexOf(XML_TAG_DUPLICATE_CHECK_C);
            if((startTagPos != -1) && (endTagPos != -1))
           	{
            	duplicateCheck = response.substring((startTagPos+XML_TAG_DUPLICATE_CHECK.length()),(endTagPos));  //Flag to indicate duplicate tabs response.
           	}
		}
		// If we still haven't found a windowName, then return an error
		if (windowName == null) {
			LOGGER.error("\n'windowName' not found in session, fieldPrefeix=" + prefix);
			return;
		}
		
		

		// Add the fields defined in the nochangeFields.xml file to the nochangeMap.
		nochangeMap = new HashMap<String,String>();
		Enumeration e = nochangeFields.getKeys();
		while (e.hasMoreElements()) {
			String name = (String)e.nextElement();
			String xpath = nochangeFields.getParameterValue(name);
			String value = Utils.getXpathFromString(response,xpath);
			nochangeMap.put(name, value);
		}

		// Loop through each field in the contract and
		// 1) Add the fieldName (without mv or sv information) to the noaddSet - used to check that only these fields 
		//    are submitted back in the subsequent request. Will catch users trying to insert fields that are part of the
		//    application but not part of the VERSION.
		// 2) If the field is a 'noinput' field, then add it and it's value to the nochangeMap.
		ArrayList cellList = Utils.getAllMatchingNodes(response, "ce");
		noaddSet = new HashSet<String>();
		Iterator it = cellList.iterator();
		while (it.hasNext()) {
			String cellXml = (String)it.next();
			String fieldName = Utils.getNodeFromString(cellXml, "fn");
			if (fieldName.length() > 0) {
				// We've found a fieldname, so add it to the list
				noaddSet.add(fieldName);
				
				// If the field is a noinput field, then add it and it's value to the nochangeMap.
				if (cellXml.indexOf("<ty>noinput</ty>") > -1) {
					String mvsvPos = Utils.getNodeFromString(cellXml, "in");
					String name = prefix + "fieldName:" + fieldName + mvsvPos;
					String value = Utils.getNodeFromString(cellXml, "v");
					nochangeMap.put(name, value);
				}
			}
		}
		
		// Create keys for the objects we want to store in the session
		String nochangeMapId = prefix + windowName + "_nochangeMap";
		String noaddSetId = prefix + windowName + "_noaddSet";

		if(duplicateCheck.equals("true"))
		{
			ivSession.removeAttribute(nochangeMapId);	//remove the old data of the duplicate tab.
			ivSession.removeAttribute(noaddSetId);	
		}
		// Store the objects in the session
		addObjectToSession(nochangeMapId, nochangeMap);
		addObjectToSession(noaddSetId, noaddSet);
	}



	/**
	 * Store the list of fields available in an enquiry selection panel.
	 * @param response the enquiry response xml.
	 */
	private void processEnquiry(String response) {
		// Dropdown enquiry response don't show selction fields, even though some do return an @ID field in the selSelection tag.
		// Don't overwrite the parent page details with the @ID field if it's a dropdown enquiry, just return.
		String windowName = null;
		// decides whether noaddset need to be updated
		Boolean updateNoAddSet = false;
		String enqaction =  ivRequest.getValue("enqaction");
		// To store session data when popup dropdown to a new window is processed.
		String winName = ivRequest.getValue("windowName");
		String EnqParentWinName = ivRequest.getValue("EnqParentWindow");
		String selectionDisplay = ivRequest.getValue("routineArgs");
		if (enqaction != null && enqaction.equals("DROPDOWN")  && (winName != null) && (winName.equals(EnqParentWinName)) && !(selectionDisplay.endsWith("_selectionDisplay"))) {
			LOGGER.debug(ivSession.getId() + " "  + ivSession.getAttribute("BrowserSignOnName") + " "  + "Nothing to store - dropdown enquiry");
			return;
		}
		// Dropdown enquiries with build routines don't have the enqaction set, but will have requestType=OFS.OS.CONTEXT.
		// Do not prepare maps to store session data for request types OFS.PRINT.DEAL.SLIP and OFS.GET.DEAL.SLIP.
		// Enquiry fields selection fields should be stored in ‘noaddset’ map which launches from context enquiries.
		// Later when enquiry re-launches by selection screen selection fields should be checked against stored map value.
		// So consider request type OFS.OS.CONTEXT since its result will be an enquiry response.
	
		String requestType =  ivRequest.getValue("requestType");
		if (requestType != null && requestType.equals("OFS.OS.CONTEXT") || requestType.equals("OFS.PRINT.DEAL.SLIP") || requestType.equals("OFS.GET.DEAL.SLIP") ) {
			// Retrieve the window name.
			windowName = getWindowName("");
			// Don't return when the enquiry is launched from a context enquiry.
			if ((!windowName.endsWith("_context_"))|| (selectionDisplay.startsWith("BUILDDROP"))) {
				return;
			}
		}
		// Get window name if null.
		if (windowName == null)
		{
			windowName = getWindowName("");
		}

		// Go through the response extracting out "<f>...</f>" patterns and storing them
		ArrayList fieldNames = Utils.getAllMatchingNodes(response, "f");
		noaddSet = new HashSet<String>();
		Iterator it = fieldNames.iterator();
		while(it.hasNext()) {
			String fieldName = Utils.getNodeFromString( (String)it.next(), "n");
			noaddSet.add(fieldName);
			updateNoAddSet = true; // noaddset loaded so need to be updated
		}
		
		// If it's an application enquiry, then need to add the list of noadd and nochange fields from the enquiry results.
		if (response.contains("<tgtApp>")) { // Means it's an application enquiry.
			nochangeMap = new HashMap<String,String>();
			
			// Add the list of noAdd and noChange fields
			ArrayList<String> rowList = Utils.getAllMatchingNodes(response, "r");
			Pattern contractPattern = Pattern.compile("fieldName:([^:]+)(:.+)*");
			for (int i=0; i<rowList.size(); i++) {
				String rowXml = rowList.get(i);
				ArrayList<String> cellList = Utils.getAllMatchingNodes(rowXml, "c");
				for (int j=0; j<cellList.size(); j++) {
					String cellXml = cellList.get(j);
					String fieldName = Utils.getNodeFromString(cellXml, "n");
					String fieldNameStub = "";
					Matcher contractMatcher = contractPattern.matcher(fieldName);
					if (contractMatcher.find()){
						fieldNameStub = contractMatcher.group(1);
					}
					noaddSet.add(fieldNameStub);
					updateNoAddSet = true; // noaddset loaded so need to be updated
					// If the item is noEdit, then store it's value in the nochangeMap.
					if (cellXml.contains("<noEdit/>")) {
						String value = Utils.getNodeFromString(cellXml, "val");
						nochangeMap.put(fieldName, value);
					}
				}
			}
			String nochangeMapId = windowName + "_nochangeMap";
			addObjectToSession(nochangeMapId, nochangeMap);
		}
		// update noaddset when there is an actual update
		if(updateNoAddSet)
		{
			String noaddSetId = windowName + "_noaddSet";
			addObjectToSession(noaddSetId, noaddSet);
		}
	}

	/**
	 * Gets the windowName attribute, which can have a variety of prefixes.
	 * @param prefix The window prefix to use (mainly for AA screens)
	 * @return The name of the window, usually the windowName parameter but not always.
	 */
	private String getWindowName(String prefix) {
		// Get the windowName = we're going to need it.
		String windowName =  ivRequest.getValue(prefix + "windowName");

		// In AA screens, sometimes the windowName is stored without the prefix, or with an 'appreq:' prefix.
		if (windowName == null) {
			windowName =  ivRequest.getValue("windowName");
		}
		if (windowName == null) {
			windowName =  ivRequest.getValue(Constants.APP_REQ + ":windowName");
		}
		
		// In tabbed screens within composite screens in noframes mode, sometimes the windowname contains
		// the fragment name and the parent fragment name. We only want the immediate fragment name, which is stored in WS_FragmentName.
		String WS_FragmentName = ivRequest.getValue("WS_FragmentName");
		if (WS_FragmentName != null && windowName.endsWith(WS_FragmentName)) {
			windowName = WS_FragmentName;
		}
		return windowName;
	}
	
	/**
	 * Stores the given object in the servlet session.
	 * @param id The id to give the object.
	 * @param o The object to store.
	 */
	private void addObjectToSession(String id, Object o) {
		ivSession.setAttribute( id, o);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(ivSession.getId() + " "  + ivSession.getAttribute("BrowserSignOnName") + " "  + "Storing response: " + id + " = " + o.toString());
		}
	}
}
