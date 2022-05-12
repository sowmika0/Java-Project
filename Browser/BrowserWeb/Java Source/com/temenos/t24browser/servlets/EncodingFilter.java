package com.temenos.t24browser.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This small 'helper' filter is designed to be called before all other filters so the form submit encoding can be set to 'UTF-8'.
 * The key function this filter executes is 'request.setCharacterEncoding();'
 * From the javadocs for <i>setCharacterEncoding()</i>: <b>This method must be called prior to reading request parameters or reading 
 * input using getReader(). Otherwise, it has no effect.</b>
 * Hence to be useful this filter should be called before any filter or servlet that accesses the request parameters. 
 * If this filter is not used then characters from many character sets may not be handled correctly leading to corrupt data.
 * This filter hard codes the character encoding to 'UTF-8', which is the encoding used on the web pages but it could easily 
 * be modified to use any encoding specified in the <init-param> of the filter in web.xml.
 * 
 * @author agoulding@temenos.com
 */
public class EncodingFilter implements Filter 
{
	private static Logger logger = LoggerFactory.getLogger(EncodingFilter.class);
	private static final String CHARACTER_ENCODING = "UTF-8";
	
	public void init(FilterConfig conf) throws ServletException {
	}

	public void destroy() {
	}

	/**
	 * Sets the encoding of the <i>request</i> to UTF-8.
	 * @param request The http request - encoding set to UTF-8
	 * @param response The http response - not modified
	 * @param filterChain required by the interface - not modified
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// Make sure it's a http request.
		if (!(request instanceof HttpServletRequest)) {
        	if (logger.isErrorEnabled()) logger.error("Filter only supports HTTP requests.");
            throw new ServletException("Filter only supports HTTP requests");
        }

		// Ensure the request parameters are treated as UTF-8 characters
		// Only available on JVM v1.4 onwards so just continue if it fails
		try {
			request.setCharacterEncoding(CHARACTER_ENCODING);
		} catch (java.io.UnsupportedEncodingException e) {
			logger.error("Unable to set request character encoding to " + CHARACTER_ENCODING);
		}

        filterChain.doFilter(request, response);
	}
}