/**
 * Class to check for cross site scripting errors inside a given name/value pair.
 */
package com.temenos.t24browser.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Class to check for cross site scripting errors inside a given name/value pair.
 * Errors are added to a list which can be retrieved using the getErrors() method.
 * Based on the FormFieldInputFilter
 * @author agoulding
 */
public class XssChecker {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XssChecker.class);

	/** Stores a list of errors */
    private Set<String> errors = new HashSet<String>();

    private static boolean documentXSS = false;
    
    private static String DOCUMENT_REGEX = "document";
    private static String COOKIE_REGEX = "cookie";
    private static String WRITE_REGEX = "write";
    
    private static boolean reqContainsDocumentButNotVulnerable = false;
    private static boolean reqContainsDocumentVulnerable = false;
	private static boolean reqContainsSomeOtherVulnerable = false;
    
	/**
	 * Returns the set of errors
	 * @return The set of errors found.
	 */
	public Set<String> getErrors() {
		return errors;
	}

	/**
	 * Adds an error to the error list if the given parameter name or value 
	 * does not match the given pattern.
	 * @param validInputRegex The pattern to match against.
	 * @param paramName The parameter name
	 * @param paramValue The parameter value
	 */
	public void check(Pattern validInputRegex, String paramName, String paramValue) {

		doNameCheck(validInputRegex, paramName);
    	doValueCheck(validInputRegex, paramName, paramValue);
    	
    	if(paramName.contains("\\")){
    		paramName = paramName.replace("\\", "");
    		doNameCheck(validInputRegex, paramName);
    	}
    	
    	if(paramValue.contains("\\")){
    		paramValue = paramValue.replace("\\", "");
    		doValueCheck(validInputRegex, paramName, paramValue);
    	}
	}

	private void doValueCheck(Pattern validInputRegex, String paramName,
			String paramValue) {
		if ( SecurityUtils.matches(paramValue, validInputRegex) )
    	{
    		// A non valid pattern has been specified in the parameter value
    		String message = "Invalid characters in value of field : " + paramName;
    		errors.add(Utils.encodeHtmlEntities(message));
    		doesParamContainsDocumentXSSAttack(paramValue);
         	if(reqContainsDocumentButNotVulnerable && (!reqContainsDocumentVulnerable) && (!reqContainsSomeOtherVulnerable))
         		errors.remove(Utils.encodeHtmlEntities(message));
         	else
    			LOGGER.error(message + ": " + paramValue);
    	}
	}

	private void doNameCheck(Pattern validInputRegex, String paramName) {
		if  ( SecurityUtils.matches(paramName, validInputRegex) )
    	{
    		// A non valid pattern has been specified in a parameter name 
    		String message = "Invalid characters in parameter name";
    		errors.add(message);
    		doesParamContainsDocumentXSSAttack(paramName);
    		if(reqContainsDocumentButNotVulnerable && (!reqContainsDocumentVulnerable) && (!reqContainsSomeOtherVulnerable))
    			errors.remove(Utils.encodeHtmlEntities(message));
    		else
    			LOGGER.error(message + ": " + paramName);
    	}
	}	
    	

	private void doesParamContainsDocumentXSSAttack(String paramValue){
		reqContainsDocumentButNotVulnerable = false;
		reqContainsDocumentVulnerable = false;
		reqContainsSomeOtherVulnerable = false;
		String newParamValue1 = paramValue.replaceAll("\"", "");
		String newParamValue2 = newParamValue1.replaceAll("\\'", "");
		String newParamValue3 = newParamValue2.replaceAll("\\$", "");
		String whiteSpaceRemovedParam = newParamValue3.replaceAll("\\s", "");
		String loweredParamValue = whiteSpaceRemovedParam.toLowerCase(); 
	
		String regExDocument = DOCUMENT_REGEX;
		Pattern patternDocument = Pattern.compile(regExDocument);
		Matcher docMatcher = patternDocument.matcher(loweredParamValue);
		
		String tempRegEx1 = DOCUMENT_REGEX + "\\." + COOKIE_REGEX;
		String tempRegEx2 = DOCUMENT_REGEX + "\\." + WRITE_REGEX;
		Pattern tempPattern1 = Pattern.compile(tempRegEx1);
		Pattern tempPattern2 = Pattern.compile(tempRegEx2);
		Matcher matcher1 = tempPattern1.matcher(loweredParamValue);
		Matcher matcher2 = tempPattern2.matcher(loweredParamValue);

		if(docMatcher.find() && (!matcher1.find()) && (! matcher2.find())){
		String[] docVarNames = new String[2];
		docVarNames = getVariableNames(loweredParamValue);
		boolean docVulnerableFound = false;
		
		String docVarName = docVarNames[0];
		String cooVarName = docVarNames[1];

		String[] docVariableNames = splitDocumentVariable(docVarName);
		String[] cooVariableNames = splitCookieVariable(cooVarName);
		
		if(docVariableNames != null && cooVariableNames != null && !(docVulnerableFound)){
			for(int i=0; i<docVariableNames.length; i++ ){
				
				for(int j=0; j<cooVariableNames.length; j++){
					docVulnerableFound = findDocumentVulnerable(docVariableNames[i], cooVariableNames[j], loweredParamValue);
				
					if (docVulnerableFound)
					break;
				}
				if(docVulnerableFound)
					break;
			}
			
		}
		else if(docVariableNames !=null && cooVariableNames == null && !(docVulnerableFound)){
			for(int i=0; i<docVariableNames.length; i++ ){
				docVulnerableFound = findDocumentVulnerable(docVariableNames[i], null, loweredParamValue);
				
				if(docVulnerableFound)
					break;
			}
			
		}
		else if(docVariableNames == null && cooVariableNames != null && !(docVulnerableFound)){
			for(int i=0; i<cooVariableNames.length; i++ ){
				docVulnerableFound = findDocumentVulnerable(null, cooVariableNames[i], loweredParamValue);
				
				if(docVulnerableFound)
					break;
			}
			
		}
		if(!docVulnerableFound){
				LOGGER.info("Request Contains document pattern but it's not vulnerable");
				reqContainsDocumentButNotVulnerable = true;
		}
	}
	else{
		reqContainsSomeOtherVulnerable = true;
	}
}	
	/**
	 * Calls check() and throws a SecurityViolationException if any errors were found.
	 * @param validInputRegex The pattern to match against.
	 * @param paramName The parameter name
	 * @param paramValue The parameter value
	 * @throws SecurityViolationException If any errors were found.
	 */
	public void checkAndThrow(Pattern validInputRegex, String paramName, String paramValue) throws SecurityViolationException {
		this.check(validInputRegex, paramName, paramValue);
		if (!errors.isEmpty()) {
			throw new SecurityViolationException("XSS validation failure: " + this.errors.toString());
		}
	}
	/*
	 * 
	 * This method forms a regEx with the values of docVarName and cooVarName.
	 * Then it will find the above formed pattern in the passing loweredParam Value
	 *  
	 */
	private boolean findDocumentVulnerable(String docVarName, String cooVarName, String loweredParamValue){
		//This check is to find the vulnerable like ";a=document;alert(a.cookie|a.write)"
		if((!((docVarName == null) || (docVarName == "")) && ((cooVarName == null) || (cooVarName == "")))) {
				LOGGER.info("docVarName: " + docVarName);
				LOGGER.info("cooVarName: "+ cooVarName);
				String tempRegEx1 = docVarName + "\\." + COOKIE_REGEX;
				String tempRegEx2 = docVarName + "\\." + WRITE_REGEX;
				Pattern tempPattern1 = Pattern.compile(tempRegEx1);
				Pattern tempPattern2 = Pattern.compile(tempRegEx2);
				Matcher matcher1 = tempPattern1.matcher(loweredParamValue);
				Matcher matcher2 = tempPattern2.matcher(loweredParamValue);
				if(matcher1.find()||matcher2.find()){
					LOGGER.info("Request contains vulnerable pattern document.cookie or document.write");
					reqContainsDocumentVulnerable = true;
					return true;
				}
		}
		//This check is to find the vulnerable like ";a=cookie;alert(document.a)"
		else if(((docVarName == null) || (docVarName == "")) && (!((cooVarName == null) || (cooVarName == "")))){
				LOGGER.info("docVarName: " + docVarName);
				LOGGER.info("cooVarName: "+ cooVarName);
				String tempRegEx1 = DOCUMENT_REGEX + "\\." + cooVarName;
				Pattern tempPattern1 = Pattern.compile(tempRegEx1);
				Matcher matcher1 = tempPattern1.matcher(loweredParamValue);
				if(matcher1.find()){
					//this is to find "documentVar.cookie or documentVar.write or documentVar.cookieVar"
					LOGGER.info("Request contains vulnerable pattern document.cookie ");
					reqContainsDocumentVulnerable = true;
					return true;
				}
		}
		//This check is to find the vulnerable like ";a=document;b=cookie;alert(a.b)"
		else if ((!((docVarName == null) || (docVarName == ""))) && (!((cooVarName == null) || (cooVarName == "")))){
				LOGGER.info("docVarName: " + docVarName);
				LOGGER.info("cooVarName: "+ cooVarName);
				String tempRegEx1 = docVarName + "\\." + COOKIE_REGEX;
				String tempRegEx2 = docVarName + "\\." + WRITE_REGEX;
				String tempRegEx3 = docVarName + "\\." + cooVarName;
				Pattern tempPattern1 = Pattern.compile(tempRegEx1);
				Pattern tempPattern2 = Pattern.compile(tempRegEx2);
				Pattern tempPattern3 = Pattern.compile(tempRegEx3);
				Matcher matcher1 = tempPattern1.matcher(loweredParamValue);
				Matcher matcher2 = tempPattern2.matcher(loweredParamValue);
				Matcher matcher3 = tempPattern3.matcher(loweredParamValue);
				if(matcher1.find()||matcher2.find()||matcher3.find()){
					LOGGER.info("Request contains vulnerable pattern document.cookie or document.write");
					reqContainsDocumentVulnerable = true;
					return true;
				}
		}
		return false;

	}
	
	private String[] getVariableNames(String paramValue){
		String[] varName = new String[2];
		boolean docRegExFound = false;
		boolean cooRexExFound = false;
		int docStartIndex = 0;
		int docEndIndex = 0;
		int cooStartIndex = 0;
		int cooEndIndex = 0;
		
		String regExDoc = DOCUMENT_REGEX;
		String regExCoo = COOKIE_REGEX;
		String regExDocument = ";[[\\w\\s]*=]*document";
		String regExCookie = ";[[\\w\\s]*=]*cookie";
		Pattern patternDocument = Pattern.compile(regExDocument);
		Pattern patternCookie = Pattern.compile(regExCookie);
		
		Pattern patternDoc = Pattern.compile(regExDoc);
		Pattern patternCoo = Pattern.compile(regExCoo);
		
		Matcher documentMatcher = patternDocument.matcher(paramValue);
		Matcher cookieMatcher = patternCookie.matcher(paramValue);
		
		Matcher docMatcher = patternDoc.matcher(paramValue);
		Matcher cooMatcher = patternCoo.matcher(paramValue);
		
		/*
		 * RegEx Expansion -- > ;[\w]*=document.
		 * Where \w -> [a-zA-Z_0-9] 
		 * 		  * -> Zero or Many
		 * 		  document and cookie -> As we know, these are the vulnerable patterns
		 * To find the variable name of document and cookie, we have to find the entire vulnerable code's starting position by 
		 * using the regEx ;[a-zA-Z]*=document and ;[a-zA-Z]*=document . Then we have to find the starting position of 
		 * "cookie" and "document".
		 * Once we found these two value then, we just hve to get a substring from the parameter value like substring(stardingIndex,endingIndex)
		 *  
		 */
		
		if(documentMatcher.find()){
			docStartIndex = documentMatcher.start()+1;
			if(docMatcher.find()){
				docEndIndex = docMatcher.start()-1;
				docRegExFound = true;
			}
		}
		if(cookieMatcher.find()){
			cooStartIndex = cookieMatcher.start()+1;
			if(cooMatcher.find()){
				cooEndIndex = cooMatcher.start()-1;
				cooRexExFound = true;
			}
		}
		if(docRegExFound)
			varName[0] = paramValue.substring(docStartIndex, docEndIndex);
		if(cooRexExFound)
			varName[1] = paramValue.substring(cooStartIndex, cooEndIndex);
		
		return varName;
	}

	
	private static String[] splitCookieVariable(String cooVarName) {
		String[] cooVarArray = null;
		try{
			cooVarArray = cooVarName.split("\\=");
		}
		catch(NullPointerException ex){
			LOGGER.debug("document.cookie doesn't contains any variables");			
		}
		return cooVarArray;   	
		
	}
	private static String[] splitDocumentVariable(String docVarName) {
		String[] docVarArray = null;
		try{
		docVarArray = docVarName.split("\\=");
		}
		catch(NullPointerException ex){
			LOGGER.debug("document.cookie doesn't contains any variables");
		}
		return docVarArray;   	
	}
}
