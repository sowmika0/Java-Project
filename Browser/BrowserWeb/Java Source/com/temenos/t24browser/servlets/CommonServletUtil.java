/**
 * 
 */
package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;

/**
 * @author kponvel
 * 
 */
public class CommonServletUtil {
	private static final String JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";

	private static final String ARC_CONFIG_APP_NAME = "ARC_CONFIG_APP_NAME";
	public static final String USER_ID = "UserId";

	/**
	 * private constructor to implement singleton
	 */
	private CommonServletUtil() {
	}

	private static CommonServletUtil instance = null;

	/**
	 * singleton method to return the instance
	 * 
	 * @return instance
	 */
	public static CommonServletUtil getInstance() {
		if (instance == null) {
			instance = new CommonServletUtil();
		}
		return instance;
	}

	/**
	 * Takes an int parameter Load the config file to access the constants
	 * defined in the config file Gets the config.
	 * 
	 * @param section
	 *            the section
	 * 
	 * @return the config
	 */
	public AuthenticationServerConfiguration getConfig(int section) {
		String configFile = System.getProperty(JAVA_SECURITY_AUTH_LOGIN_CONFIG);
		ConfigurationFileParser parser = new ConfigurationFileParser(
				configFile, System.getProperty(ARC_CONFIG_APP_NAME));
		Map[] configMap = parser.parse();
		return new AuthenticationServerConfiguration(configMap[section]);
	}

	/**
	 * Checks if is password valid.
	 * 
	 * @param newPassword1
	 *            the new password1
	 * @param newPassword2
	 *            the new password2
	 * 
	 * @return true, if is password valid
	 */
	public boolean isPasswordValid(String newPassword1, String newPassword2) {
		return newPassword1.equals(newPassword2);
	}
	
	/**
	 * This method extracts the userId from the request
	 * 
	 * @param request
	 * @return userId
	 */
	public String getUserId(HttpServletRequest request) {
		String userId = null;
		try {
			userId = request.getParameter(USER_ID);
			if (userId == null) {
				userId = (String) request.getSession().getAttribute(USER_ID);
			}
		} catch (Exception e) {
			// NullPointerException May occur here.
			e.printStackTrace();
		}
		return userId;
	}
	/**
	 * Method which forwards the request to given response page
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forwardRequest(HttpServletRequest request,
			HttpServletResponse response, String page) throws ServletException,
			IOException {
		request.getRequestDispatcher(page).forward(request, response);
	}
}
