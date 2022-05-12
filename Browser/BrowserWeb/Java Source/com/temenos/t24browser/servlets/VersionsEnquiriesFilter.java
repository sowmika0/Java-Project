package com.temenos.t24browser.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This class filters access to the BrowserServlet. Only HTTP request to the
 * Versions and Enquiries specified in the configuration file are passed through.
 * 
 * @author mludvik
 */
public class VersionsEnquiriesFilter implements Filter{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionsEnquiriesFilter.class);
	
	/** The Constant CONFIG_FILE_PARAM. */
	private static final String CONFIG_FILE_PARAM = "configFile";
	
	/** The constant for init parameter name to define the value of operation tag name */
	private static final String OPERATION_TAG_NAME = "operationtagname";
	
	/** The constant for init parameter name to define list of operations that cannot be passed for Transaction authentication. */
	private static final String UNVERIFIED_OPERATIONS = "unverifiedoperations";
	
	/** The constant for init parameter name to define list of operations that can be passed for Transaction authentication */
	private static final String VERIFIED_OPERATIONS = "verifiedoperations";
	
	/** The Constant FILTER_ITEM. */
	private static final String FILTER_ITEM = "filterItem";
	
	/** The Transaction Signing TRANS_FILTER_ITEM. */
	private static final String TRANS_FILTER_ITEM = "transSignFilter";
	
	/** The Constant MATCH_TAG. */
	private static final String MATCH_TAG = "match";
	
	/** The Constant OPERATOR. */
	private static final String OPERATOR = "operator";
	
	/** The Constant EQUAL_OPERATOR. */
	private static final String EQUAL_OPERATOR = "equal";
	
	/** The Constant STARTS_WITH_OPERATOR. */
	private static final String STARTS_WITH_OPERATOR = "startsWith";
	
	/** The Constant OPERATION DELIMITER. */
	private static final String OPERATION_DELIMITER = "|";
	
	/** The filter items. */
	private NodeList filterItems;

	/** The Transaction Signing filter items. */
	private NodeList transSignItems;
	
	/** The Operation Tag filter items. */
	
	private static final String REQUEST_TYPE_NODE = "requestType";
	private static final String APPLICATION_NODE = "application";
	private static final String VERSION_NODE = "version";
	
	/** The transCriteria sub nodes. */
	private static final String TRANS_CRITERIA = "transCriteria";
	private static final String CONDITION_TAG = "condition";
	private static final String CHALLENGE_TAG = "challenge";
	private static final String PARAMETER_TAG = "parameter";
	private static final String TRANS_SIGN_TYPE = "transSignType";
	private static final String SEQUENCE = "sequence";
	private static final String VALUE = "value";
	private static final String CHALLENGE = "CHALLENGE";
	
	private static final String OPERATOR_EQUAL = "EQ";
	private static final String OPERATOR_GREATER_THAN_OR_EQUAL = "GE";
	private static final String OPERATOR_LESS_THAN_OR_EQUAL = "LE";
	private static final String OPERATOR_NOT_EQUAL = "NE";
	private static final String OPERATOR_GREATER_THAN = "GT";
	private static final String OPERATOR_LESS_THAN = "LT";
	
	private String tranSignOverride = "";
	
	private Set<Element> transSignFiltered;
	
	/** The Operation Tag filter items. */
	private String operationTagName = "";
	/** The Unverified Operation filter items. */
	private String unverifiedOperations = "";
	/** The Verified Operation filter items. */
	private String verifiedOperations = "";
	
	/**
	 * Initialize servlet filter from config file.
	 * 
	 * @param conf the conf
	 * 
	 * @throws ServletException the servlet exception
	 */
	public void init(FilterConfig conf) throws ServletException {
		String configFile = conf.getInitParameter(CONFIG_FILE_PARAM).replace('/', File.separatorChar);
		ServletContext context = conf.getServletContext();
		String configPath = context.getRealPath(configFile);
		LOGGER.warn("Config File Path : " + configPath);
		File f = new File(configPath);
		readFilters(f);
		LOGGER.info("Reading of versionsEnquiryFilters completed");
		
		operationTagName =  conf.getInitParameter(OPERATION_TAG_NAME);
		if (operationTagName != null) {
			LOGGER.info("operationTagName : " + operationTagName);
		}
		
		unverifiedOperations = conf.getInitParameter(UNVERIFIED_OPERATIONS);
		if (unverifiedOperations != null) {
			LOGGER.info("unverifiedOperations : " + unverifiedOperations);
		}
		
		verifiedOperations = conf.getInitParameter(VERIFIED_OPERATIONS);
		if (verifiedOperations != null) {
			LOGGER.info("verifiedOperations : " + verifiedOperations);
		}
	}

	/**
	 * Read the configuration file.
	 * 
	 * @param f the f
	 * 
	 * @throws ServletException the servlet exception
	 */
	private void readFilters(File f) throws ServletException {
		DOMParser parser = new DOMParser();
		InputStream in = null;
		try {
			in = new BufferedInputStream(
					new FileInputStream(f));
			parser.parse(new InputSource(in));
		} catch(IOException e) {
			throw new ServletException(e);
		} catch(SAXException e) {
			throw new ServletException(e);
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch(IOException e){
				throw new ServletException(e);
			}
		}
		Document doc = parser.getDocument();
		filterItems = doc.getElementsByTagName(FILTER_ITEM);
		transSignItems = doc.getElementsByTagName(TRANS_FILTER_ITEM);
		if(filterItems.getLength() == 0)
			LOGGER.warn("Version Enquiries Filter configuration file does not contain any filter items.");
		
		if(transSignItems.getLength() == 0)
			LOGGER.warn("Version Enquiries Filter configuration file does not contain any transSign items.");
	
	}
	
	/**
	 * Checks if all conditions in any filter item are satisfied.
	 * 
	 * @param request servlet request
	 * 
	 * @return true if all conditions in any filter item are satisfied, otherwise
	 * false
	 */
	private boolean checkConditions(ServletRequest request) {
		for(int i = 0; i < filterItems.getLength(); i++) {
			Element filterItem = (Element)filterItems.item(i);
			// conditions are emlements in filter item.
			NodeList conditions = filterItem.getChildNodes();
			// empty filter item passes through all requests!
			if(conditions.getLength() == 0)
				LOGGER.warn("One or more filter item is empty, which allows all requests to pass through!");
			boolean satisfy = true;
			for(int j = 0; j < conditions.getLength(); j++) {
				Node condition = conditions.item(j);
				if(condition.getNodeType() == Node.ELEMENT_NODE) {
					String name = condition.getNodeName();
					String httpValue = request.getParameter(name);
					if(null != httpValue && !checkCondition((Element)condition, httpValue)) {
						// all values need to be equal
						satisfy = false;
						break;
					}
				}
			}
			// if at least one condition is OK, return true
			if(satisfy) {
				return true;
			}
		}	
		// by default return false, for 0 filter items as well
		return false;
	}
	
	/**
	 * Check condition.
	 * 
	 * @param condition the condition
	 * @param httpValue the http value
	 * 
	 * @return true, if successful
	 */
	private boolean checkCondition(Element condition, String httpValue) {
		NodeList matchTags = condition.getElementsByTagName(MATCH_TAG);
		if(matchTags.getLength() == 0) {
			LOGGER.warn("No match tag for element: " + condition.getNodeName());
			return false;
		}
		for(int i = 0; i < matchTags.getLength(); i++) {
			Element matchTag = (Element)matchTags.item(i);
			String operator = matchTag.getAttribute(OPERATOR);
			String value = matchTag.getFirstChild().getNodeValue();
			// no operator attribute means implicitly equal
			if(operator == null || operator.equals(EQUAL_OPERATOR)) {
				if(!httpValue.equals(value))
					return false;
			} else if(operator.equals(STARTS_WITH_OPERATOR)) {
				if(!httpValue.startsWith(value))
					return false;
			} else {
				LOGGER.error("Unrecognized operator: " + operator);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Filters access to the BrowserServlet. If the configuration file
	 * contains a filter item which matches parameters in the HTTP request,
	 * request is passed through. Otherwise standard HTTP 403 Error is returned.
	 * 
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Filter supports only HTTP requests");
        }
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession httpSession = httpRequest.getSession(true);
		tranSignOverride = "";
		if (checkForTransactionSigning(request, httpSession)){
			LOGGER.debug("Set the Transaction Signing flag");
			httpSession.setAttribute("transSignRequired", "yes");
			if (tranSignOverride != null && tranSignOverride != "") {
				LOGGER.debug("TransSignOverride is set value is : " + tranSignOverride);
				httpSession.setAttribute("tranSignOverride", tranSignOverride);
			}
		} else {
			String transValue = (String)httpSession.getAttribute("transSignRequired");
			if(transValue != null){
				httpSession.removeAttribute("transSignRequired");
			}
		}
		
    	// initial GET which results in login page does not have any parameters,
    	// POST request without parameters is save as well 
        if(request.getParameterMap().size() == 0 || checkConditions(request)) { 
        	chain.doFilter(request, response);
        } else {
        	HttpServletResponse resp = (HttpServletResponse)response;
        	// return standard HTTP 403 Error
        	resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        	
        	String message = "\n\nBlocked by servlet filter. It is necessary to add configuration record to the " + 
        			"servletFilterConfig.xml\n\n" +
        	"Request parameters: \n--------------------\n" +
        	"requestType" + ": " + request.getParameter("requestType") + "\n" +
        	"routineName" + ": " + request.getParameter("routineName") + "\n" +
        	"routineArgs" + ": " + request.getParameter("routineArgs") + "\n" +
        	"application" + ": " + request.getParameter("application") + "\n" +
        	"version" + ": " + request.getParameter("version") + "\n" +
        	"ofsFunction" + ": " + request.getParameter("ofsFunction") + "\n" +
        	"ofsOperation" + ": " + request.getParameter("ofsOperation") + "\n" +
        	"enqname" + ": " + request.getParameter("enqname");
        	LOGGER.error(message);
        	return;
        }
	}
	
	/**
	 * Checks if all conditions in any transaction signing filter item are satisfied and also checks for transCriteria.
	 * 
	 * @param request servlet request
	 * @param httpSession HttpSession
	 * 
	 * @return true if all conditions in any filter item are satisfied, otherwise
	 * false
	 */
	private boolean checkForTransactionSigning(ServletRequest request, HttpSession httpSession) {
		
		//Check for navOperation value set in request if Operation is invalid then no need to continue further.
		if (!checkOperation(request)) {
			LOGGER.debug("Operation value in request is invalid Or Null, transactionSigning will not be set.");
			return false;
		}
		
		//Select only the relevant transSignFilter items which satisfies requesttype-application-version condition
		LOGGER.info("Filtering the appropriate items-Start");
		boolean filteredItemsFound = verifyTransSignFilter(request);
		LOGGER.info("Filtering the appropriate items-Complete");
		
		if (filteredItemsFound) {
			if (checkTransCriteria(request, httpSession)) {
				if ((tranSignOverride != null) && (tranSignOverride != "")) {
					httpSession.setAttribute("tranSignOverride", tranSignOverride);
				}
				return true;
			}
		}

		// by default return filteredItemsFound
		return filteredItemsFound ;
	}
	
	/**
	 * Checks for requestType, application, version nodes and selects matching records into a Set.
	 * 
	 * @param request servlet request
	 * 
	 * @return true if matching nodes for requestType-application-version filter items are found, otherwise
	 * false
	 */
	private boolean verifyTransSignFilter(ServletRequest request) {
				
		//Select only requesttype, application and version paramater values from httpRequest.
		String httpRequesttype = request.getParameter(REQUEST_TYPE_NODE);
		String httpApplication = request.getParameter(APPLICATION_NODE);
		String httpVersion = request.getParameter(VERSION_NODE);
		
		LOGGER.info("requestType-Application-Version : " + httpRequesttype + "-" + httpApplication + "-" + httpVersion);
		
		transSignFiltered = new HashSet<Element>();
		
		for(int i = 0; i < transSignItems.getLength(); i++) {
			Element filterItem = (Element)transSignItems.item(i);
			
			if (TRANS_FILTER_ITEM.equals(filterItem.getNodeName())){
				
				// empty filter item passes through all requests!
				if(filterItem.getChildNodes().getLength() == 0)
					LOGGER.warn("One or more filter item is empty, which allows all requests to pass through!");
				
				boolean satisfyConditions = true;
				//Select requesttype node from the filter
				NodeList requesttypeNodeList = filterItem.getElementsByTagName(REQUEST_TYPE_NODE);
				if (requesttypeNodeList.getLength() != 0){
					Element requesttype = (Element)requesttypeNodeList.item(0);
					if (null == httpRequesttype || !checkMatchTags(requesttype, httpRequesttype)) {
						satisfyConditions = false;
						continue;
					}
				}
				//Select application node from the filter
				NodeList applicationNodeList = filterItem.getElementsByTagName(APPLICATION_NODE);
				if (applicationNodeList.getLength() != 0){
					Element application = (Element)applicationNodeList.item(0);
					if (null == httpApplication || !checkMatchTags(application, httpApplication)) {
						satisfyConditions = false;
						continue;
					}
				}
				//Select version node from the filter
				NodeList versionNodeList = filterItem.getElementsByTagName(VERSION_NODE);
				if (versionNodeList.getLength() != 0){
					Element version = (Element)versionNodeList.item(0);
					if (null == httpVersion || !checkMatchTags(version, httpVersion)) {
						satisfyConditions = false;
						continue;
					}
				}
				
				if (satisfyConditions) {
					transSignFiltered.add(filterItem);
				}
			}
		}	
		
		if (transSignFiltered != null && transSignFiltered.size()>0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Checks for conditions, challenge and parameter specified in transCriteria node.
	 * 
	 * @param request servlet request
	 * @param httpSession HttpSession object
	 * 
	 * @return true by default and false if any of specified condition is NOT satisfied
	 * 
	 */
	private boolean checkTransCriteria(ServletRequest request, HttpSession httpSession) {
		
		boolean criteriaConditionsSatisfied = false;
		boolean transSignWithEmptyCriteria = false;
		String transSignTypeOriginal = (String)httpSession.getAttribute("transSign");
		
		LOGGER.info("transSign : number of items filtered - " + transSignFiltered.size());
		
		for (Element transSignItem: transSignFiltered) {

			tranSignOverride = "";
			
			NodeList transSignTypeList = transSignItem.getElementsByTagName(TRANS_SIGN_TYPE);
			
			NodeList transSignCriteriaList = transSignItem.getElementsByTagName(TRANS_CRITERIA);
			
			if (transSignCriteriaList.getLength() != 0) {
				Element transSignCriteria = (Element)transSignCriteriaList.item(0);
				if (transSignCriteria != null) {
					//Checkcriteria conditions
					criteriaConditionsSatisfied = checkTransCriteriaConditions(transSignCriteria, request);
					
					if (criteriaConditionsSatisfied) {
						
						setTransSignOverride(transSignTypeList);
						
						String transType = "";
						if ((tranSignOverride != null) && (tranSignOverride !="")) {
							transType = tranSignOverride;
						} else {
							transType = transSignTypeOriginal;
						}
						
						//Check for challenge
						if ("CHRES".equalsIgnoreCase(transType)) {
							String challenge = checkTransCriteriaChallenge(transSignCriteria, request);
							if (challenge != null) {
								httpSession.setAttribute(CHALLENGE, challenge);
								httpSession.setAttribute("SIGNCHALLENGE", "1");
							}
						}
						//Check for parameter tags
						checkTransCriteriaParameter(transSignCriteria, request, httpSession);
					} else {
						continue;
					}

					if (criteriaConditionsSatisfied)
						return true;
					
				} else {
					setTransSignOverride(transSignTypeList);
					transSignWithEmptyCriteria = true;
				}
			} else {
				setTransSignOverride(transSignTypeList);
				transSignWithEmptyCriteria = true;
			}
		}
		
		if (transSignWithEmptyCriteria) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reusable private method, to set transSignOverride class level variable.
	 * This method extract the tranSignType value from the nodelist and assigns to private variable.
	 * 
	 * @param NodeList transSignTypeList
	 * 
	 * 
	 */
	private void setTransSignOverride(NodeList transSignTypeList) {
		//extract the transSignType node value if specified explicitly at transaction filter level.
		if (transSignTypeList.getLength() != 0){
			Element transSignType = (Element)transSignTypeList.item(0);
			String overrideValue = (String)(transSignType.getFirstChild().getNodeValue());
			
			//if overrideValue is not Null 
			if (overrideValue != null) {
				tranSignOverride = overrideValue;
			}
		}
	}
	 
	/**
	 * Verifies and validates transaction criteria conditions defined in configuration file.
	 * Returns true in following cases;
	 * 		If no condition tags are defined
	 * 		If all conditions are satisfied
	 * 
	 * @param Element transCriteria, ServletRequest request
	 * 
	 * @return Boolean
	 * 
	 */
	private boolean checkTransCriteriaConditions(Element transCriteria, ServletRequest request) {
	    /* Sample transCriteria node is shown below;
			<transCriteria>
			  <!--Condition Nodes -->
			  <condition sequence="1" value="1000" operator="GE">fieldName:DEBIT.AMOUNT</condition>
			  <condition sequence="2" value="1500" operator="LE">fieldName:DEBIT.AMOUNT</condition>
			</transCriteria>
		*/
		
		//Extract all condition tags to a NodeList from transSignCriteria node
		NodeList conditionTags = transCriteria.getElementsByTagName(CONDITION_TAG);
		
		//When No conditions defined return true
		if (conditionTags.getLength() == 0) {
			LOGGER.debug("Condition tags doesnot exist");
			return true;
		}
		
		LOGGER.debug("Condition tags for this criteria : " + conditionTags.getLength());
		//Loop through the condition tags until, all conditions are satisfied OR any one condition is NOT satisfied    

		for (int tagCount = 0; tagCount<conditionTags.getLength(); tagCount++) {
			Element conditionTag = (Element)conditionTags.item(tagCount);
			String conditionValue = conditionTag.getAttribute(VALUE);
			String conditionOperator = conditionTag.getAttribute(OPERATOR);
			String fieldName = conditionTag.getFirstChild().getNodeValue();
			
			String httpRequestValue = request.getParameter(fieldName);
			
			boolean conditionNumeric = isNumeric(conditionValue);
			boolean httpValueNumeric = isNumeric(httpRequestValue);
			boolean numericCheckRequired = false;
			
			//Set the flag for numeric or string comparison
			if (conditionNumeric == httpValueNumeric) {
				if ((conditionNumeric) && (httpValueNumeric)) {
					//Both the values are numeric
					numericCheckRequired = true;
				} else {
					//Both the values are alphanumeric
					numericCheckRequired = false;
				}
			} else {
				//Values are of different types raise warning.
				LOGGER.warn("transaction Criteria condition values are of different types");
			}

			if (numericCheckRequired) {
				//Numeric comparison is applicable for all operators (EQ, NE, LT, LE, GT, GE)
				/*
				 * EQ - Equal to
				 * NE - Not Equal to
				 * LT - Less than
				 * LE - Less than or equal
				 * GT - Greater than
				 * GE - Greater than or equal
				*/
				
				try {
					//Converts String values to Number using current default locale 
					Number numConditionValue = NumberFormat.getInstance().parse(conditionValue);
					Number numhttpRequestValue = NumberFormat.getInstance().parse(httpRequestValue);
					
					if (OPERATOR_EQUAL.equalsIgnoreCase(conditionOperator)) {
						// If values are NOT equal return false
						if (!(numhttpRequestValue.doubleValue() == numConditionValue.doubleValue())) {
							return false;
						}		
					} else if (OPERATOR_NOT_EQUAL.equalsIgnoreCase(conditionOperator)) {
						//If values are equal return false
						if ((numhttpRequestValue.doubleValue() == numConditionValue.doubleValue())) {
							return false;
						}			
					} else if (OPERATOR_GREATER_THAN.equalsIgnoreCase(conditionOperator)) {
						if (!(numhttpRequestValue.doubleValue() > numConditionValue.doubleValue())) {
							return false;
						}
					} else if (OPERATOR_GREATER_THAN_OR_EQUAL.equalsIgnoreCase(conditionOperator)) {
						if (!(numhttpRequestValue.doubleValue() >= numConditionValue.doubleValue())) {
							return false;
						}	
					} else if (OPERATOR_LESS_THAN.equalsIgnoreCase(conditionOperator)) {
						if (!(numhttpRequestValue.doubleValue() < numConditionValue.doubleValue())) {
							return false;
						}
					} else if (OPERATOR_LESS_THAN_OR_EQUAL.equalsIgnoreCase(conditionOperator)) {
						if (!(numhttpRequestValue.doubleValue() <= numConditionValue.doubleValue())) {
							return false;
						}
					}
				} catch (ParseException e) {
					LOGGER.error("Exception occured during numeric conversion for Strings " + conditionValue + " and " + httpRequestValue);
				} catch(NumberFormatException e) {
					LOGGER.error("Exception occured during numeric conversion for Strings " + conditionValue + " and " + httpRequestValue);
				}
				
			} else {
				//Non Numeric comparison is applicable only for EQ and NE operators
				/*
				 * EQ - Equal to
				 * NE - Not Equal to
				*/
				if (OPERATOR_EQUAL.equalsIgnoreCase(conditionOperator)) {
					if (!conditionValue.equalsIgnoreCase(httpRequestValue)) {
						return false;
					}
				} else if (OPERATOR_NOT_EQUAL.equalsIgnoreCase(conditionOperator)) {
					if (conditionValue.equalsIgnoreCase(httpRequestValue)) {
						return false;
					}
				} else {
					LOGGER.warn("transaction Criteria condition operator not valid for String comparison");
				}
			} 	
		}
		
		return true;
	}
	
	/**
	 * Checks for challenge tag and extracts the challenge value based on the configured paramters
	 * 
	 * @param Element transCriteria, ServletRequest request
	 * 
	 * @return String challengeValue
	 * 
	 */
	private String checkTransCriteriaChallenge(Element transCriteria, ServletRequest request) {
		String challengeValue = null;
		//Extract challenge tag from transCriteria node
		NodeList challengeTags = transCriteria.getElementsByTagName(CHALLENGE_TAG);
		if (challengeTags.getLength() !=0) {
			Element challengeTag = (Element)challengeTags.item(0);
			String subStringValue = challengeTag.getAttribute(VALUE);
			String fieldName = challengeTag.getFirstChild().getNodeValue();
			String fieldValue = request.getParameter(fieldName);
			if (!(subStringValue==null) && !(subStringValue=="")) {
				int numberOfChars = Integer.parseInt(subStringValue.substring(1));
				int startPos = 0;
				int lastPos = numberOfChars;
				fieldValue = extractOnlyNumeric(fieldValue);
				
				if (subStringValue.charAt(0) == 'L') {
					if (fieldValue.length() > numberOfChars) {
						startPos = fieldValue.length()-numberOfChars;
					}
					lastPos = fieldValue.length();
				} 
				
				fieldValue = fieldValue.substring(startPos, lastPos);
				
				//Add leading "0" Or X if length of extracted value is less than required characters.
				String formatType = "%"+numberOfChars+"s";
				fieldValue = String.format(formatType, fieldValue).replace(' ', '0');
				
				challengeValue = fieldValue;
			}				
		}
		return challengeValue;
	}
	
	/**
	 * Extracts the parameter tags configured under transCriteria node in configuration file.
	 * paramter tags can be configured to display customised messages on transaction confirmation screen  
	 * 	OR customised message which will be delivered through OOB gateway	
	 * Customised messages will be stored as session variables "transSignpreMessage" and "transSignpostMessage"
	 * these session variables can be used in jsp pages to display customised message
	 * 
	 * @param Element transCriteria, ServletRequest request, HttpSession httpSession
	 * 
	 * @return Boolean
	 * 
	 */
	private void checkTransCriteriaParameter(Element transCriteria, ServletRequest request, HttpSession httpSession) {
		HashMap<String, String> messageMap = new HashMap<String, String>();
		
		//Extract all condition tags to a NodeList from transSignCriteria node
		NodeList paramterTags = transCriteria.getElementsByTagName(PARAMETER_TAG);
		// Loop through all parameter tags and decode the values and set preMessage and postMessage tags to the session
		for (int tagCount = 0; tagCount<paramterTags.getLength(); tagCount++) {
			Element parameterTag = (Element)paramterTags.item(tagCount);
			
			String paramSequence = parameterTag.getAttribute(SEQUENCE);
			String paramValue = parameterTag.getAttribute(VALUE);
			
			String fieldName = parameterTag.getFirstChild().getNodeValue();
			String fieldValue = request.getParameter(fieldName);
			
			// paramValue will be like L4, L99, F5, F99
			if (!(paramValue==null) && !(paramValue=="")) {
				int numberOfChars = Integer.parseInt(paramValue.substring(1));
				int startPos = 0;
				int lastPos = numberOfChars;
				
				if (paramValue.charAt(0) == 'L') {
					if (fieldValue.length() > numberOfChars) {
						startPos = paramValue.length()-numberOfChars;
					}
					lastPos = paramValue.length();
				} 
				fieldValue = fieldValue.substring(startPos, lastPos);
				//Add leading X if length of extracted value is less than required characters.
				String formatType = "%"+numberOfChars+"s";
				fieldValue = String.format(formatType, fieldValue).replace(' ', 'X');
			}
			messageMap.put("\\{"+PARAMETER_TAG+paramSequence+"\\}", fieldValue);
		}
		
		Element preMessageTag = (Element)transCriteria.getElementsByTagName("premessage").item(0);
		Element postMessageTag = (Element)transCriteria.getElementsByTagName("postmessage").item(0);
		String preMessageValue = null;
		String postMessageValue = null;
		
		try {
			preMessageValue = preMessageTag.getFirstChild().getNodeValue();
			postMessageValue = postMessageTag.getFirstChild().getNodeValue();
		} catch(NullPointerException ex) {
			LOGGER.info("Either Pre or Post message tag contains no value");
			if (preMessageValue == null)
				preMessageValue = "";
			if(postMessageValue == null)
				postMessageValue = "";
		}
		StringBuffer buffer1 = new StringBuffer("");
		StringBuffer buffer2 = new StringBuffer("");
		Set messageSet = messageMap.entrySet();
		Iterator i = messageSet.iterator();
		while(i.hasNext()){
			Map.Entry<String, String> me = (Map.Entry<String, String>)i.next();
			String key = me.getKey();
			String value = me.getValue();
			String tempResult1 = preMessageValue.replaceAll(key, value);
			buffer1.replace(0, buffer1.length(), tempResult1);
			preMessageValue = buffer1.toString();
			String tempResult2 = postMessageValue.replaceAll(key, value);
			buffer2.replace(0, buffer2.length(), tempResult2);
			postMessageValue = buffer2.toString();
		}
		httpSession.setAttribute("transSignpreMessage", preMessageValue);
		httpSession.setAttribute("transSignpostMessage", postMessageValue);
		
	}
	
	/**
	 * Check for unverified and verified operations.
	 * 
	 * @param httpOperationValue the http value of operation
	 * 
	 * @return true, if successful
	 */
	private boolean checkOperation(ServletRequest request) {
		
		//Check for operation required if and only if operation is defined as initconfig paramter
		if (operationTagName!=null) {
			String httpOperationValue = request.getParameter(operationTagName);
			LOGGER.debug("Operaion value : " + httpOperationValue);

			//Return false if Operation value is null, coz define operation should have value
			if (httpOperationValue == null) 
				return false;
			
			if (unverifiedOperations != null) {
				String[] unverifiedOperationsArray = unverifiedOperations.split("\\"+OPERATION_DELIMITER);
				
				for(int i=0;i<unverifiedOperationsArray.length; i++) {
					if (unverifiedOperationsArray[i].equalsIgnoreCase(httpOperationValue)) {
						LOGGER.debug("Operation value is in unverified list : " + httpOperationValue);
						//passed operation value is in unverified list 
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	private String extractOnlyNumeric(String alphaNumeric) {
		String numericOnly = "";
		//If value is not numeric then extract only numeric values
		if (!isNumeric(alphaNumeric)) {
			for(int i = 0; i < alphaNumeric.length(); i++) {
				final char charAt = alphaNumeric.charAt(i);
				if (Character.isDigit(charAt)) {
					numericOnly = numericOnly + charAt;
				}
			}
		}else {
			// If value is numeric then return the field value as it is
			numericOnly = alphaNumeric;
		}
		return numericOnly;
	}
	
	private boolean isNumeric(String s) {
	    try { 
	        //Integer.parseInt(s);
	        NumberFormat formatter = NumberFormat.getInstance();
	        ParsePosition pos = new ParsePosition(0);
	        formatter.parse(s,pos);
	       	
	        return s.length() == pos.getIndex();
	        
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(Exception e) { 
	        return false; 
	    }
	}
	
	private boolean checkMatchTags(Element conditionTag, String requestValue) {
		NodeList matchTags = conditionTag.getElementsByTagName("match");
		
		if(matchTags.getLength() == 0) {
			System.out.println("No match tag for element: " + conditionTag.getNodeName());
			return false;
		}
		if (requestValue == null) {
			System.out.println("request value in Http request is empty ");
			return false;
		}
		
		for (int i=0; i<matchTags.getLength(); i++) {
			Element matchTag = (Element)matchTags.item(i);
			String matchOperator = matchTag.getAttribute("operator");
			String matchValue = matchTag.getFirstChild().getNodeValue();
			
			//operator value not specified means equal
			if (matchOperator==null || "equal".equalsIgnoreCase(matchOperator)) {
				if (!matchValue.equals(requestValue))
					return false;
			}else if ("startsWith".equalsIgnoreCase(matchOperator)) {
				if (!requestValue.startsWith(matchValue))
					return false;
			} else {
				return false;
			}
		}
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
}
