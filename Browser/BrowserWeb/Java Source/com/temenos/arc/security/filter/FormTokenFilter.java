package com.temenos.arc.security.filter;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.security.ResponseReaderWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

public class FormTokenFilter implements Filter {

  //Logger for debugging
  private static Logger logger = LoggerFactory.getLogger(FormTokenFilter.class);
  
  //Jsp Error page
  private static String ERROR_JSP = "/banking/up/SecurityError.jsp";
  
  private static String SESSION_FORMTOKEN_MAP = "formTokenMap";
  
  //Form pattern to be inttialized in init method
  private Pattern formPattern;
  
  /*
   * This filter checks every response for the occurance of form tag, if it contains it adds a unique token to the form, and maintains the unique tokens in a map
   * Also it checks every request except utility, no request, ofs enquiry routines conatins the token, if token not present the error is raised
   * If the token present, it also checks if the token is available in the map, if not error is thrown
   * If the token present and it is in map, then its validity is checked. It if is expired error is thrown
   * Each is token is valid for one hour. This filter is introduces to avoid Cross side request forgery 
   */
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)res;
    HttpSession session = request.getSession(true);
    boolean noCheck = false;

    String reqType = request.getParameter("requestType");
    String enqAction = request.getParameter("enqaction");
    String command = request.getParameter("command");    
    
    //formToken check is not required for UTILITY.ROUTINE Or NO.REQUEST Or OFS.ENQUIRY routines
    if ((reqType != null) && (reqType.equals("UTILITY.ROUTINE") || reqType.equals("CREATE.SESSION") || reqType.equals("NO.REQUEST") || reqType.equals("OFS.ENQUIRY"))) {
      noCheck = true;
      logger.debug("No Check for utility, no request or ofs enquiry routines");
    }
    
    //Check for special case if OFS.ENQUIRY action is ADD:FAVOURITES
    if (reqType != null && enqAction != null  && reqType.equals("OFS.ENQUIRY") && enqAction.startsWith("ADD:FAVOURITES")) {
        noCheck = false;
        logger.debug("Check for OFS.ENQUIRY with add favourites");
    }
    
    if ((reqType == null) && (command != null) && command.equals("getLogin"))
    {
    	noCheck = true;
    	logger.debug("Check for GETLOGIN");
    }
       
    logger.debug("Parameter map size: " + request.getParameterMap().size());
    logger.debug("Parameter map: " + request.getParameterMap());

    if ((((request.getMethod().equals("POST")) || ((null != request.getParameter("method")) && (request.getParameter("method").equalsIgnoreCase("post"))))) && (!(noCheck)) && (0 != request.getParameterMap().size()))
    {
      logger.debug("Post request, checking for form Token");
      String formToken = request.getParameter("formToken");
      int reason = validateFormToken(session, formToken);
      String errorString="";
      if (reason != 0) {
    	  switch (reason) {
    	  case 1: errorString = "formToken not found in the request."; break;
    	  case 2: errorString = "formToken doesnot exist in fromTokenMap"; break;
    	  case 3: errorString = "formToken is expired."; break;
    	  }
    	     	  
    	  handleSecurityViolation(errorString);
          session.invalidate();
          logger.warn("Redirecting to error page...");
          response.sendRedirect(request.getContextPath() + ERROR_JSP); 
      }
    }
    ResponseReaderWrapper responseWrapper = new ResponseReaderWrapper(response,false);
    chain.doFilter(request, responseWrapper);

    if (session == null)
      session = request.getSession(true);
    
    String responseString = responseWrapper.toString();
    Matcher formMatcher = this.formPattern.matcher(responseString);
    
    //Add a unique form token element in all the form in the response
    while (formMatcher.find()) {
      logger.debug("Match found");
      String formStart = formMatcher.group();
      String formToken = null;
      
      formToken = addFormTokentoSession(session);     

      if (formStart != null){
        responseString = responseString.replace(formStart, formStart + "<input type=\"hidden\" name=\"formToken\" value=\"" + formToken + "\">");
        logger.debug("Response string altered");
      }
    }
    
    try{
    	if(!response.isCommitted()){
    		//getWriter should not be called after getOutputStream, Care must be taken to check for response is committed or not
	    	PrintWriter pw = response.getWriter();
	    	pw.write(responseString);
	    	logger.debug("Response String written back to print stream");
    	}
    }catch(IllegalStateException e){
    	logger.debug("Response already committed, Response string is not written to the print stream");
    }catch(Exception e){
    	logger.debug("Error during writing the response");
    }
    
  }
  
  private static int validateFormToken(HttpSession session, String formTokenValue) {
	  int formTokenValid = 0;
	  HashMap<String, Long> formTokenMap = null;
	  
	//Retrieving the Form Token map from session if not create one and set it in session
	  Object formTokenMapObj = session.getAttribute(SESSION_FORMTOKEN_MAP);
	  if (null == formTokenMapObj) {
	      formTokenMap = new HashMap<String, Long>();
	      session.setAttribute(SESSION_FORMTOKEN_MAP, formTokenMap);
	  } else {
	      formTokenMap = (HashMap<String, Long>)formTokenMapObj;
	  }
      
      if (formTokenValue == null) {
    	  formTokenValid = 1; 
      } else if (!(formTokenMap.containsKey(formTokenValue))) {
    	  formTokenValid = 2;
      } else if (((Long)formTokenMap.get(formTokenValue)).longValue() < System.currentTimeMillis()) {
    	  formTokenValid = 3;
      }

	  return formTokenValid;
  }
  
  private static String addFormTokentoSession(HttpSession session) {
	  HashMap<String, Long> formTokenMap = null;
	  boolean newFormToken = false;
	  String formToken = null;
	  
      try {
    	  formTokenMap = (HashMap<String, Long>)session.getAttribute(SESSION_FORMTOKEN_MAP);
      } catch(IllegalStateException e) {
    	  logger.debug("Session is invalidated");
      }
      
      if (formTokenMap == null) {
    	  formTokenMap = new HashMap<String, Long>();
    	  newFormToken = true;
    	  formToken = generateFormToken(50);
      } else {
    	  do {
    		  formToken = generateFormToken(50);
    		  if ((null != formTokenMap) && (!(formTokenMap.keySet().contains(formToken)))) {
    			  newFormToken = true;
    		  }
    	  } while (!(newFormToken));  
      }
      
	  logger.debug("New Form Token created");
	  
      //Set the validity of the token for one hour from the generated time
      long validity = System.currentTimeMillis() + 3600000L;
      formTokenMap.put(formToken, Long.valueOf(validity));
      logger.debug("Form Token set in the map with validity");
      try {
    	  session.setAttribute("formTokenMap", formTokenMap);
      }catch(IllegalStateException e){
    	  logger.debug("Session is invalidated");
      }
      logger.debug("Form Token Map added in the session");
      return formToken;
  }
  
  /*
   * Init method to initialise the form pattern
   */
  public void init(FilterConfig filterConfig) {
    this.formPattern = Pattern.compile("<form.+?>");
  }

  private void handleSecurityViolation(String s) {
    logger.error(s);
  }

  public void destroy() {
  }

  /*
   * To generate the unique Form Token ID
   */
  
  private static String generateFormToken(int n) {
	  String formTokenValue = "";
	  String tempValue = getRandomHexString(n);
	  formTokenValue = getHash(tempValue, getSalt());
	  
	  return formTokenValue;
  }
  
  private static String getRandomHexString(int n) {
    char[] returnChars = new char[n];
    int c = 65;
    int r1 = 0;
    for (int i = 0; i < n; ++i) {
      r1 = (int)(Math.random() * 2.0D);
      switch (r1)
      {
      case 0:
        c = 48 + (int)(Math.random() * 10.0D);
        break;
      case 1:
        c = 65 + (int)(Math.random() * 6.0D);
        break;
      case 2:
        c = 65 + (int)(Math.random() * 6.0D);
      }

      returnChars[i] = (char)c;
    }

    String formTokenValue = new String(returnChars);
       
    return formTokenValue;
  }
  
  private static String getHash(String random, byte[] salt) {
      byte[] input = null;
      String encodedString = null;
      try{
          MessageDigest digest = MessageDigest.getInstance("SHA-256");
          digest.reset();
          digest.update(salt);
          input = digest.digest(random.getBytes("UTF-8"));
          Base64 encoder = new Base64();
          encodedString = new String(encoder.encode(input));
          logger.debug("EncodedString(formToken)" + encodedString);
      }
      catch(NoSuchAlgorithmException ex){
            logger.error("No such algorithm");
      } catch (UnsupportedEncodingException e) {
            logger.error("No such algorithm");
      }
      return encodedString;
    }

    
  private static byte[] getSalt(){
          Random randomGenerator = new Random();

          int salt = 0;
          salt = randomGenerator.nextInt(100);
          
          byte[] value = new byte[4];
          value[0] = (byte) (salt >> 24);
          value[1] = (byte) (salt >> 24);
          value[2] = (byte) (salt >> 24);
          value[3] = (byte) (salt);
          return value;
  }

}