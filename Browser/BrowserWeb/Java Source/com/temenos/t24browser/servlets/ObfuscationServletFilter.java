package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.temenos.t24browser.obfuscator.ObfuscatedHttpServletRequest;
import com.temenos.t24browser.obfuscator.Obfuscator;

// TODO: Auto-generated Javadoc
/**
 * This filter transformes obfuscated HTTP requests into original ones. All
 * parameters are de-obfuscated. If obfuscation is not used, original parameters
 * are returned. This filter should be applied before any other semantic based
 * operation with HTTP request parameters is required.
 * 
 * @author mludvik
 */
public class ObfuscationServletFilter implements Filter{

	/** The Constant OBFUSCATOR. */
	private static final String OBFUSCATOR = "obfuscator";
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Filter supports only HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
			
		/* 
		 * If obfuscation is used, i.e. Obfuscator exists in the session,
		 * HttpServletRequest is wrapped in ObfuscatedHttpServletRequest and
		 * obfuscated parameters are de-obfuscated.
		 */
		Object obf = session.getAttribute(OBFUSCATOR);
		if(obf != null) {
			request = new ObfuscatedHttpServletRequest(httpRequest, (Obfuscator)obf);
		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}
	
}
