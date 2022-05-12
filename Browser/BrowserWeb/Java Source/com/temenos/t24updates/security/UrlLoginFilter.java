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
 * This filter detects if a URL login request has been posted.
 * This filter is required by the 'sharepoint' system (the customer support portal)
 * When a user clicks the T24 Updates hyperlink the following URL format will be posted:
 * 
 * http://localhost:8080/T24-Updates/servlet/BrowserServlet?command=portallogin&details=V0VTQU0xOjEyMzQ1Ng== 
 * (WESAM1:123456 is V0VTQU0xOjEyMzQ1Ng== in base64)
 * 
 * http://localhost:8080/T24-Updates/servlet/BrowserServlet?command=portallogin&details=base64username&password
 * 
 * This filter will parse the URL & construct a Login request and pass it to the T24 Updates servlet.
 * 
 * @author wzahran
 */
public class UrlLoginFilter implements Filter
{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(UrlLoginFilter.class);
	
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
       if (isURLLoginRequest(httpServletRequest))
       {	   
    	   processFilter(request, response, filterChain);
       }
       else
       {
    	   filterChain.doFilter(request, response);
       }
	}
	
	/**
	 * Checks if is logout request.
	 * 
	 * @param httpServletRequest the http servlet request
	 * 
	 * @return true, if is a URL login request
	 */
	private boolean isURLLoginRequest(final HttpServletRequest httpServletRequest) 
    {
		
        String sCommand = httpServletRequest.getParameter("command");
        String sUserCredentials = httpServletRequest.getParameter("details");
        // if command equals portallogin AND detials (that hold the users login details) is not empty
        // then return true
        return (("portallogin".equalsIgnoreCase(sCommand)) && (sUserCredentials != null));
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
       
       // Extract the users credentials - username:password
       String sB64UserCredentials = httpServletRequest.getParameter("details");
       
       // Decode from base64
       // Should be in the format of
       // username:password
       sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
       String sUserCredentials = new String(decoder.decodeBuffer(sB64UserCredentials));
       
       // Parse this name to get the username & password
   		int delimPosition = sUserCredentials.indexOf(":");
   		if (delimPosition < 0 || delimPosition > sUserCredentials.length()) {
   			logger.error("No ':' delimiter in user credentails");
	        // Continue the chain with the request - should just get the login screen
	        filterChain.doFilter(request, response);
   		}
   		else
   		{
	   		String sUserName = sUserCredentials.substring(0, delimPosition);
	   		String sPassword = sUserCredentials.substring(delimPosition+1);
	
	   		// Create the login request
		    LoginParameterisedRequest wrapper = new LoginParameterisedRequest(httpServletRequest);
	        wrapper.put("command", "login");
	        wrapper.put("requestType", "CREATE.SESSION");
	        // Set the username & password into the request
	        wrapper.put("signOnName", sUserName);
	        wrapper.put("password", sPassword);
	        // get the session in order to get the counter from it
	        HttpSession httpSession = httpServletRequest.getSession(true);
	        Object loginCounterObj = httpSession.getAttribute("LoginCounter");
	        String loginCounter = null;
	        if (null != loginCounterObj) {
	        	loginCounter = (String)loginCounterObj;
	        } else {
	        	loginCounter = "0";
	        }
	        wrapper.put("counter", loginCounter); 
	        
    		// Store a flag in the session so we know we came in via the URL Login
           	httpSession.setAttribute("T24UpdatesURLLogon", "true");	
           	// Store the user credentials in the session so we can auto-re-login if we need to
           	httpSession.setAttribute("T24UpdatesCredentails", sUserName +":"+sPassword );
	        
	        HttpServletRequest newRequest = wrapper;
	        
	        if (logger.isDebugEnabled())
	        {
	        	logger.debug("Process URL login request");
	        }
	        // Continue the chain with the 'new' request
	        filterChain.doFilter(newRequest, response);
   		}
	}
}
