package com.temenos.t24browser.security;

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

import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * This filter detects if a Principal object is available in the HTTP request
 * if it is then construct a CREATE.SESSION request.
 * If it does not detect a Principal then the login page should be shown.
 * 
 * @author wzahran
 */
public class SingleSignOnFilter implements Filter 
{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(SingleSignOnFilter.class);
	
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
        // Check if we have a session
        if (this.SSOsessionExists(httpServletRequest)) 
        {
        	// We already have a session and a principal stored within it
        	// Check if this request is a logout request
        	// if not then continue the chain normally.
	        if (this.isLogoutRequest(httpServletRequest)) 
	        {
	        	if (logger.isDebugEnabled()) logger.debug("Logout request.");
	        	// Construct the logout command and pass to T24
	        	// and invalidate this session
	            this.doT24Logout(request, response, filterChain);
	        }
	        else
	        {
	        	if (logger.isDebugEnabled()) logger.debug("Process SSO secured request");
        		// Continue the chain normally.
        		filterChain.doFilter(request, response);	        	
	        }
        }
        else
        {
	    	// Check if we have a principal if so then create the login request
	    	// If we don't then let the request through anyway as the login page will shown
	    	// as 'normal' in the servlet
	    	Principal SSOPrincipal = httpServletRequest.getUserPrincipal();
	    	if (SSOPrincipal != null)
	    	{
	    		if (logger.isDebugEnabled()) logger.debug("SSOPrincipal Detected: Creating Login Request");
	    		if (logger.isDebugEnabled()) logger.debug("SSOPrincipal: " + SSOPrincipal.toString());
	    		
	    		// Wrap the principal object into our Serializable class
	    		SSOPrincipal ssop = new SSOPrincipal();
	    		ssop.setSSOPrincipal(SSOPrincipal);
	    		// Store this principal in the session!!!
	            HttpSession session = httpServletRequest.getSession();
	           	session.setAttribute("ssoPrincipal", ssop);
	            
	    		// Create the login request
	    	    LoginParameterisedRequest wrapper = new LoginParameterisedRequest(httpServletRequest);
	   	        wrapper.put("command", "login");
		        wrapper.put("requestType", "CREATE.SESSION");
		        // Use a 'dummy' username & password - T24 will disregard these and
		        // replace these withthe details from the security context
		        wrapper.put("signOnName", "SSOT24SON");
		        wrapper.put("password", "SSOT24PW");
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
		        
		        // Now add the SSOPrincipal object into the request as a parameter
		        // so it can be passed to the TCC further down.
		        HttpServletRequest newRequest = wrapper;
		        
		        if (logger.isDebugEnabled()) logger.debug("Process SSO login request");
		        // Continue the chain with the 'new' request
		        filterChain.doFilter(newRequest, response);
	    	}
	    	else
	    	{
	    		// We not have a previous session and we don't have a principal
	    		// in the request so process the request normally
	    		if (logger.isDebugEnabled()) logger.debug("Process vanilla request");
	    		filterChain.doFilter(request, response);
	    	}
        }

 	}

   /**
    * SS osession exists.
    * 
    * @param httpServletRequest the http servlet request
    * 
    * @return true, if successful
    */
   private boolean SSOsessionExists(HttpServletRequest httpServletRequest) 
   {
        HttpSession session = httpServletRequest.getSession(false);
        boolean sessionExists = false;
        if (session != null) 
        {
        	if (session.isNew() == true)
        	{
	    		if (logger.isDebugEnabled()) logger.debug("Initial Session");
        		sessionExists = false;
        	}
        	else
        	{
        		// Check the access count
        		Integer accessCount = (Integer)session.getAttribute("accessCount");
        		if (accessCount == null) 
        		{
        			accessCount = new Integer(0);
        		}
        		
        		if (accessCount.intValue() > 1)
        		{
        			sessionExists = true;
        		}
        		
        		// Does a principal already exits in the session?
        		SSOPrincipal previousSSOPrincipal = (SSOPrincipal)session.getAttribute("ssoPrincipal");
        		if (previousSSOPrincipal != null)
        		{
	        		Principal principal = previousSSOPrincipal.getSSOPrincipal();
	        		if (principal != null)
	        		{
	    	    		if (logger.isDebugEnabled()) logger.debug("Principal found in servlet session: Session Exists");
	        			sessionExists = true;
	        		}
	        		else
	        		{
	        			if (logger.isDebugEnabled()) logger.debug("SSOPrincipal found with a null Principal in servlet session: Extract new Principal");
	        			sessionExists = false;
	        		}
        		}
        		else
        		{
        			if (logger.isDebugEnabled()) logger.debug("SSOPrincipal NOT found in servlet session: Extract new Principal");
        			sessionExists = false;
        		}
        	}
        }
        return sessionExists;
    }
   
    /**
     * Checks if is logout request.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if is logout request
     */
    private boolean isLogoutRequest(final HttpServletRequest httpServletRequest) 
    {
        String command = httpServletRequest.getParameter("command");
        String application = httpServletRequest.getParameter("application");
        return (("globusCommand".equalsIgnoreCase(command)) && ("SIGN.OFF".equalsIgnoreCase(application)));
    }

	
    // The response returned from T24 upon logout probably needs to be discarded...
    /**
     * Do t24 logout.
     * 
     * @param request the request
     * @param response the response
     * @param filterChain the filter chain
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    private void doT24Logout(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException 
    {
        try 
        {
            // Allow T24 to process the logout
            filterChain.doFilter(request, response);
        } 
        finally 
        {
            invalidateSession(request);
        }
    }
    
	/**
	 * Invalidate session.
	 * 
	 * @param request the request
	 */
	private void invalidateSession(ServletRequest request) 
	{
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession != null) 
		{
		    httpSession.invalidate();
		}
	}
}
