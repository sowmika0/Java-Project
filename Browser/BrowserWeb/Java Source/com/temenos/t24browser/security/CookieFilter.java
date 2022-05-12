package com.temenos.t24browser.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.exceptions.GenericAuthenticationException;

public class CookieFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(CookieFilter.class);

	// Configuration Object for reding the config file
	AuthenticationServerConfiguration servConfig = null;

	// Session object
	HttpSession session;

	/**
	 * no-op.
	 */
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("Initialising LDAPAutenticationFilter...");
	}

	/**
	 * no-op.
	 */
	public void destroy() {
		logger.info("Destroying LDAPAutenticationFilter...");
	}

	/**
	 * Retrieves the value from the cookie, creates a login request to T24 browser
	 */
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain filterChain) throws IOException,ServletException{
    	String userName = null;
    	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    	LoginParameterisedRequest requestWrapper = null;
    	session = httpServletRequest.getSession(true);
	    // Wrapping the request, wrapper enables put() method
	    requestWrapper = new LoginParameterisedRequest(httpServletRequest);
		//Wrapping the response, wrapper enables reading the contents of the response
		   HttpServletResponse httpResponse = (HttpServletResponse) response;
		   ResponseReaderWrapper responseWrapper = new ResponseReaderWrapper(httpResponse);
	    // Cookie contains the userName, get the value form the cookie
	    userName = getCookieValue(request);
	    String strAlreadyLogin= (String)session.getAttribute("AlreadyLogin");
		   if(strAlreadyLogin!=null && !"".equals(strAlreadyLogin))
		   {
			   logger.debug("Already login, by passing login agian");
			   filterChain.doFilter(requestWrapper, responseWrapper);
			   checkResponse(request, response, responseWrapper.toString());
			   return;
		   }
	    requestWrapper.put("command", "login");
	    requestWrapper.put("requestType", "CREATE.SESSION");
	    requestWrapper.put("signOnName", userName);
	    // Setting password to be dummy
	    requestWrapper.put("password", "dummyvalue");
        requestWrapper.put("counter", "0");
	    logger.debug("All request parameters set, ready to login");
        session.setAttribute("AlreadyLogin", "Yes");
        
        filterChain.doFilter(requestWrapper, responseWrapper);
        //Checking if the response is T24 Login, if it is redirecting it to the 
   	   	checkResponse(request, response, responseWrapper.toString());
    }
	
	 /**
     * Checks if the response returned from the servlet is T24 Login page, if so redirect to the error page
     * @param resquest - resquest object
     * @param response - response object
     * @param responseString - response content
     * @throws IOException
     */
	private void checkResponse(ServletRequest request, ServletResponse response, String responseString) throws IOException {
		//Scanning the response, if it is T24 login page, throw exception to redirect to error page
		logger.debug("Content of the response ********************************"+responseString);
		String errMsg = "";
		if(responseString.indexOf("T24 Sign")>0 && responseString.indexOf("CREATE.SESSION")>0){
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			session = httpServletRequest.getSession(true);
			int startPos = responseString.indexOf("<span class=\"error\">");
			int endPos = responseString.indexOf("</span>",startPos);
			endPos = responseString.indexOf("</span>",endPos+6);
			if(startPos > 0 && endPos >0)
				errMsg = responseString.substring(startPos,endPos)+"</span>";
			session.setAttribute("CUSTOMMSG", errMsg);
			throw new GenericAuthenticationException(); //Throwing exception to redirect to the error page
		}else{
			//If not a T24 login page, writing back the response to the printWrite for normal display
			try{
			PrintWriter pw = response.getWriter();
			pw.write(responseString);
			}
			catch(IllegalStateException ex){
				logger.debug("Problem in writing the response to a different response object", ex);
				logger.info("Continuing instead of throwing exception out");
			}
			catch(Exception ex){
				logger.debug("Problem in writing the response to a different response object", ex);
				logger.info("Continuing instead of throwing exception out");
			}
		}
	}
	
	 /**
     * Retrieves the value form the cookie, cookie name is in config file
     * @param resquest - resquest object
     */
	private String getCookieValue(ServletRequest request) {
		String cookieValue = null;
		HttpServletRequest httpRequest;
		// Retrieving the configuration details from the config file to
		// configurationObject
		servConfig = getConfig(0);
		// Retrieving the cookies from the request
		httpRequest = (HttpServletRequest) request;
		Cookie[] cookies = (httpRequest).getCookies();
		// Converting the cookie array to hash table
		logger.debug("Converting the Cookie array to hash table");
		Hashtable<String, String> cookieTable = new Hashtable<String, String>();
		if (cookies != null) {
			logger.debug("Cookie is not null");
			for (int i = 0; i < cookies.length; i++) {
				cookieTable.put(cookies[i].getName(), cookies[i].getValue());
				logger.debug("Cookie: " + i + "Name: " + cookies[i].getName()
						+ "Value: " + cookies[i].getValue());
			}
		}
		// Retrieving the cookie name from the config file, which contains the
		// external system value
		String cookieName = servConfig
				.getConfigValue(AuthenticationServerConfiguration.COOKIE);
		String headerName = servConfig
				.getConfigValue(AuthenticationServerConfiguration.SSO_HEADER_NAME);
		
		logger.debug("CookieName in the config file:" + cookieName);
		// Checking for required cookie
		if (cookieTable.containsKey(cookieName)) {
			logger.debug("Cookie Name in the config file is available");
			if (headerName != null) {
				logger.debug("Header Name in the config file is available");
				cookieValue = (String) httpRequest.getHeader(headerName);
			}
			else {
				logger.debug("Header Name in the config file is NOT available");
				cookieValue = (String) cookieTable.get(cookieName);	
			}
		}
		logger.debug("Value in the Cookie= " + cookieValue);
		return cookieValue;
	}

	/**
	 * Method to initialise the Configuration Object with the values in the
	 * config file
	 * 
	 * @param section -
	 *            integer value to start with
	 * @return Configuration object
	 */
	public AuthenticationServerConfiguration getConfig(int section) {
		String configFile = System.getProperty("ARC_CONFIG_PATH");
		if (configFile != null && !configFile.equals("")) {
			logger.debug("Config file path retrived : " + configFile);
		} else {
			logger.error("ARC_CONFIG_PATH not set in System Property");
		}
		String appName = System.getProperty("ARC_CONFIG_APP_NAME");
		if (appName != null && !appName.equals("")) {
			logger.debug("Application name retrived : " + appName);
		} else {
			logger.error("ARC_CONFIG_APP_NAME not set in System Property");
		}
		ConfigurationFileParser parser = new ConfigurationFileParser(
				configFile, appName);
		logger.debug("parser object created from configfile");
		Map[] configMap = parser.parse();
		logger.debug("ConfigMap is created by parsing the configfile");
		return new AuthenticationServerConfiguration(configMap[section]);
	}

}
