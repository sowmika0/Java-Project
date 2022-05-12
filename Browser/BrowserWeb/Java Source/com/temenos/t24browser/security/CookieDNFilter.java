package com.temenos.t24browser.security;

import java.io.IOException;
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

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class CookieDNFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(CookieDNFilter.class);

	// Configuration Object for reding the config file
	AuthenticationServerConfiguration ldapConfig = null;

	/**
	 * no-op.
	 */
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("Initialising CookiDNFilter...");
	}

	/**
	 * no-op.
	 */
	public void destroy() {
		logger.info("Destroying CookieDNFilter...");
	}

	/**
	 * Retrieves the DN from the cookie and sets in the request
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		String userDN = null;
		LoginParameterisedRequest requestWrapper = null;
		// Wrapping the request, wrapper enables put() method
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		requestWrapper = new LoginParameterisedRequest(httpServletRequest);
		// Retrieving the configuration details from the config file to
		// configurationObject
		ldapConfig = getConfig(0);
		// Retrieving the cookies from the request
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		// Converting the cookie array to hash table
		logger.debug("Converting the Cookie array to has table");
		Hashtable cookieTable = new Hashtable();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookieTable.put(cookies[i].getName(), cookies[i].getValue());
			}
		}
		// Retrieving the cookie name which contains the userDN from the config
		// file
		String CookieName = ldapConfig.getConfigValue(AuthenticationServerConfiguration.COOKIE_NAME);
		// Checking for required cookie
		if (cookieTable.containsKey(CookieName)) {
			logger.debug("Cookie containing the userDN is available");
			userDN = (String) cookieTable.get(CookieName);
			logger.debug("User DN in the Cookie= " + userDN);
			requestWrapper.put("userDN", userDN);
		} else {
			String headerName = ldapConfig.getConfigValue(AuthenticationServerConfiguration.HEADER_NAME);
			if (headerName != null) {
				logger.debug("User Name will be retrieved from the  request Header ");
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				String uName = httpRequest.getHeader(headerName);
				if (uName != null) {
					logger.debug("User DN in the Cookie= " + userDN);
					requestWrapper.put("userDN", "cn=" + uName + ",");
				} else {
					logger.debug("Username is null");
					requestWrapper.put("userDN", "");
				}
			} else {
				logger.debug("Header name is null");
				requestWrapper.put("userDN", "");
			}
		}
		filterChain.doFilter(requestWrapper, response);
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
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		logger.debug("parser object created from configfile");
		Map[] configMap = parser.parse();
		logger.debug("ConfigMap is created by parsing the configfile");
		return new AuthenticationServerConfiguration(configMap[section]);
	}
}
