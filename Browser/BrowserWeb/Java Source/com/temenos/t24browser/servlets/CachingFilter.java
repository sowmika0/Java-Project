package com.temenos.t24browser.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This filter sets the max-age property of the 'Cache-Control' http header to a value defined in web.xml.
 * This is intended to allow web browsers to cache non-sensitive static content like images and style sheets.
 * 
 * @author agoulding@temenos.com
 */
public class CachingFilter implements Filter {

	// Member variables
	private static Logger logger = LoggerFactory.getLogger(CachingFilter.class);
	private int MaxAge = 0;

	/**
	 * Sets the 'Cache-Control' header if MaxAge is greater than 0.
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) res;

		// Set the caching header
		if (MaxAge > 0) {
			response.setHeader("Cache-Control", "max-age=" + MaxAge);
		}

		// pass the request/response on
		chain.doFilter(req, response);
	}
	

	/**
	 * Parses the MaxAgeInSeconds property in web.xml.
	 */
	public void init(FilterConfig filterConfig) {
		String MaxAgeInSeconds = filterConfig.getInitParameter("MaxAgeInSeconds");
		try {
			MaxAge = Integer.parseInt(MaxAgeInSeconds);
		} catch (NullPointerException e) {
			handleInitError("MaxAgeInSeconds not found, hence not setting Cache-Control. Check web.xml");
		} catch (NumberFormatException e) {
			handleInitError("MaxAgeInSeconds not an Integer, hence not setting Cache-Control. Check web.xml");
		}
		if (MaxAge < 1) {
			handleInitError("MaxAgeInSeconds less than 1, hence not setting Cache-Control. Check web.xml");
		}
	}
	

	/**
	 * Logs the given message as an error and sets MaxAge to zero.
	 * @param message The error message to log.
	 */
	private void handleInitError(String message) {
		logger.error(message);
		MaxAge = 0;
	}

	public void destroy() {
	}
}