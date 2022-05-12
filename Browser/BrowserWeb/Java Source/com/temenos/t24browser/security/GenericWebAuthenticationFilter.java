package com.temenos.t24browser.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.exceptions.GenericAuthenticationException;

public class GenericWebAuthenticationFilter implements Filter {
	// Configuration Object for reding the config file
	AuthenticationServerConfiguration ldapConfig = null;

	
	// Session object
	HttpSession session;

	private static Logger logger = LoggerFactory
			.getLogger(GenericWebAuthenticationFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug(" initialising GenericWebAuthenticationFilter ");
	}

	public void destroy() {
		logger.debug(" Destroying GenericWebAuthenticationFilter ");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		//ldapConfig = getConfig(0);
		String userName = null;
		T24Principal t24Principal = null;
		SSOPrincipal ssoPrincipal = null;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		LoginParameterisedRequest requestWrapper = null;
		session = httpServletRequest.getSession(true);
		// Wrapping the request, wrapper enables put() method
		requestWrapper = new LoginParameterisedRequest(httpServletRequest);
		// Wrapping the response, wrapper enables reading the contents of the
		// response
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ResponseReaderWrapper responseWrapper = new ResponseReaderWrapper(
				httpResponse);
		// get the username
		userName = httpServletRequest.getRemoteUser();
		logger.debug("userName " + userName);
		String requestType = httpServletRequest.getParameter("requestType");

		String strAlreadyLogin = (String) session.getAttribute("AlreadyLogin");
		if (strAlreadyLogin != null && !"".equals(strAlreadyLogin)) {
			if (requestType != null && requestType.equals("DESTROY.SESSION")) {
				logger.debug(" Processing Sign Off request");
				filterChain.doFilter(requestWrapper, httpResponse);
				HttpSession session = requestWrapper.getSession();
				session.setAttribute("CUSTOMMSG","SIGN.OFF");
				httpResponse.sendRedirect(httpServletRequest.getContextPath()
						+ "/jsps/customMessage.jsp");
				return;
			}
			logger.debug("Already login, by passing login agian");
			filterChain.doFilter(requestWrapper, httpResponse);
			// checkResponse(request, response, responseWrapper.toString());
			return;
		}
		
		ldapConfig = getConfig(0);
		String impersonate = ldapConfig
				.getConfigValue(AuthenticationServerConfiguration.SECURITY_METHOD);
		logger.debug("impsersonnation " + impersonate);

			requestWrapper.put("command", "login");
			requestWrapper.put("requestType", "CREATE.SESSION");
			// for Form login authenticatiion, authentication type need to be
			// external
			requestWrapper.put("AuthenticationType", "external");
			requestWrapper.put("counter", "0");
			logger.debug("All request parameters set, ready to login");
			if ((userName != null) && (!("".equalsIgnoreCase(userName)))) {
				logger.debug("Already login session is set");

			    session.setAttribute("AlreadyLogin", "Yes");
			}
			
			if (impersonate.equalsIgnoreCase("TRUE")) {
			//session.setAttribute("AlreadyLogin", "Yes");
			// retriving the DN pattern form the config file

			String strDNPattern = ldapConfig
					.getConfigValue(AuthenticationServerConfiguration.DN_PATTERN);
			// replacing the string "userid" to actuall userid in DN
			String strDN = strDNPattern.replaceFirst("<userid>", userName);
			logger.debug("value of strDN is " + strDN);
			// creating the T24Principal by setting the DN in name
			t24Principal = new T24Principal(strDN);
			// Creating the ssoPrinicpal
			ssoPrincipal = new SSOPrincipal();
			logger.debug("sso Principal created");
			// adding the T24Principal into the ssoPrincipal
			ssoPrincipal.setSSOPrincipal(t24Principal);
			if (session != null) {
				session.setAttribute("ssoPrincipal", ssoPrincipal);
				logger.info("ssoPrincipal added to session");
			}
			} else {
				requestWrapper.put("signOnName", userName); 
				// Setting password to be dummy
				requestWrapper.put("password", "dummyvalue");
				
			}
			filterChain.doFilter(requestWrapper, responseWrapper);
			// Checking if the response is T24 Login, if it is redirecting it to
			// the
			checkResponse(request, response, responseWrapper.toString());
		
	}

	private void checkResponse(ServletRequest request,
			ServletResponse response, String responseString) throws IOException {
		// Scanning the response, if it is T24 login page, throw exception to
		// redirect to error page
		logger.debug("Content of the response ********************************"
				+ responseString);
		String errMsg = "";
		if (responseString.indexOf("T24 Sign") > 0
				&& responseString.indexOf("CREATE.SESSION") > 0) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			session = httpServletRequest.getSession(true);
			int startPos = responseString.indexOf("<span class=\"error\">");
			int endPos = responseString.indexOf("</span>", startPos);
			endPos = responseString.indexOf("</span>", endPos + 6);
			if (startPos > 0 && endPos > 0)
				errMsg = responseString.substring(startPos, endPos) + "</span>";
			session.setAttribute("CUSTOMMSG", errMsg);
			throw new GenericAuthenticationException(); // Throwing exception to
														// redirect to the error
														// page
		} else {
			try {
				PrintWriter pw = response.getWriter();
				pw.write(responseString);
			} catch (IllegalStateException e) {
				logger.debug("Response already committed, Response string is not written to the print stream");
			} catch (Exception e) {
				logger.debug("Error during writing the response");
			}

		}
	}

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
