package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.security.ResponseReaderWrapper;
import com.temenos.t24browser.utils.Utils;

public class CharEncodingFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(CharEncodingFilter.class);

	public static final String BROWSER_ENCRYPT = "browserEncryption.config";

	FilterConfig config=null;
	
	/**
	 * no-op.
	 */
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("Initialising CharEncodingFilter...");
		config = arg0;
	}

	/**
	 * no-op.
	 */
	public void destroy() {
		logger.info("Destroying CharEncodingFilter...");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		  HttpServletResponse httpResponse = (HttpServletResponse) response;
	      ResponseReaderWrapper responseWrapper = new ResponseReaderWrapper(httpResponse);
	      
	      //Call filter chain
	      filterChain.doFilter(request, responseWrapper);
	     
	      String responseString = responseWrapper.toString();
	      String path = config.getServletContext().getRealPath("");
	   // Utils.decodeOutputEncodingEntities (String in, String contextRoot, boolean nonHTMLCharacters)
			// 
			// nonHTMLCharacters = true -- output encoded values will be converted to plain text characters
			// nonHTMLCharacters = false -- output encoded values will be converted to HTML entity characters - Required here
			
			// nonHTMLCharacters should be false since request is being set to Browser, which will handled HTML entities
			responseString = Utils.decodeOutputEncodingEntities(responseString, path, false);
			
			 // getWriter should not be called after getOutputStream, 
			 // getWriter should not be called if response is committed 
			 // these errors need to be handled
			
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
