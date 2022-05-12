package com.temenos.t24updates.security;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Decoder;

import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

//TODO: Auto-generated Javadoc
/**
 * This filter detects if an Admin login request has been posted.
 * This filter will check a command passed in and sets a flag in the session
 * Latter on in the servlet when we build the login page, if this flag is set we use
 * special xsl to ensure the Admin login page is displayed.
 * This will allow T24 Updates Administrators to login to the T24 Updates system
 * to set up passwords for the users etc..
 * 
 * @author wzahran
 */
public class AdminLoginFilter implements Filter
{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(AdminLoginFilter.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException 
	{
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() 
	{
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException 
	{
       if (!(request instanceof HttpServletRequest)) 
        {
        	if (logger.isErrorEnabled()) logger.error("Filter only supports HTTP requests.");
            throw new ServletException("Filter only supports HTTP requests");
        }
	     
       HttpServletRequest httpServletRequest = (HttpServletRequest) request;
       if (isAdminLoginRequest(httpServletRequest))
       {	   
    	   processFilter(request, response, filterChain);
       }
       else
       {
    	   HttpServletRequest httpRequest = (HttpServletRequest) request;
     	   HttpSession httpSession = httpRequest.getSession(true);
    	   // Store a flag in the session so we know we came in via the Admin Login
    	   httpSession.setAttribute("T24UpdatesAdminLogon", "false");	    	   
    	   
    	   filterChain.doFilter(request, response);
       }
	}
	
	/**
	 * Checks if is logout request.
	 * 
	 * @param httpServletRequest the http servlet request
	 * 
	 * @return true, if is a Administrator login request
	 */
	private boolean isAdminLoginRequest(final HttpServletRequest httpServletRequest) 
    {
		
        String sCommand = httpServletRequest.getParameter("command");
         // if command equals adminlogin then return true
        return ("adminlogin".equalsIgnoreCase(sCommand));
    }	

	/**
	 * Process filter.
	 * 
	 * @param request the request
	 * @param response the response
	 * @param filterChain the filter chain
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	private void processFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
	{
       HttpServletRequest httpServletRequest = (HttpServletRequest) request;
       HttpSession httpSession = httpServletRequest.getSession(true);
       // Store a flag in the session so we know we came in via the Admin Login
       httpSession.setAttribute("T24UpdatesAdminLogon", "true");	       
       
       // Continue
       filterChain.doFilter(request, response);

	}
}
