package com.temenos.arc.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.servlets.BrowserServlet;

public class HTTPHeaderFilter implements Filter{
	
	/** The logger */
    private static Logger logger = LoggerFactory.getLogger(HTTPHeaderFilter.class);
    
    public void destroy() {
    }
    /** For adding header parameters to response */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
    	logger.debug("Entering the HTTPHeaderFilter");
    	filterChain.doFilter(request, response);
    	if (!(response instanceof HttpServletResponse)) {
			logger.error("Must be an HttpServletResponse");
			throw new ServletException("Must be an HttpServletResponse");
		}
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String XFRAME_HEADER_VALUE = BrowserServlet.XFRAME_HEADER_VALUE;
    	if (!(httpResponse.isCommitted())){
    	//Adding header option to avoid click jacking
    	httpResponse.setHeader("X-FRAME-OPTIONS", XFRAME_HEADER_VALUE);
    	logger.debug("X-FRAME-OPTIONS added in header for response to avoid click jacking");
    	
    	String name = BrowserServlet.productName;
    	if(!(name.equals("ARC-IB")))
    	{
    		//This following code is enable cookie to 'httponly'
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpResponse.containsHeader("Set-Cookie")){
             	String sessionid = httpRequest.getSession().getId();
             	logger.debug("Set-Cookie found in servlet, =" + sessionid);
                 if (request.isSecure()) {
                     httpResponse.setHeader("Set-Cookie", "JSESSIONID=" + sessionid + "; Path=/; HttpOnly ; Secure");
                 } else {
                     httpResponse.setHeader("Set-Cookie", "JSESSIONID=" + sessionid + "; Path=/; HttpOnly");
                 }
        	}
		}
		}
    	/*HttpServletRequest httpRequest = (HttpServletRequest) request;
    	logger.debug("Setting the HTTP Only flag for JSESSIONID, if set-cookie available");
    	
        HttpSession httpSession = httpRequest.getSession();
        String t24SessionId = (String)httpSession.getAttribute("T24SESSIONID");
        Cookie t24Cookie, jCookie;
        String sessionid = httpRequest.getSession().getId();
        if (httpResponse.containsHeader("Set-Cookie") && t24SessionId != null){
        	logger.debug("T24SessionId found in header");
        	logger.debug("Clearing the Set-Cookie");
        	httpResponse.setHeader("Set-Cookie", "");
        	
        	logger.debug("Setting T24SessionId and JSessionId in Cookie, with HTTP Only flag");
        	if (request.isSecure()) {
        		t24Cookie = new Cookie ("T24SESSIONID",t24SessionId+"; HttpOnly ; Secure");
        		jCookie = new Cookie ("JSESSIONID",sessionid+"; HttpOnly ; Secure");
        	}else{
        		t24Cookie = new Cookie ("T24SESSIONID",t24SessionId+"; HttpOnly");
        		jCookie = new Cookie ("JSESSIONID",sessionid+"; HttpOnly");
        	}
        	httpResponse.addCookie(t24Cookie);
        	httpResponse.addCookie(jCookie);
        }else if(httpResponse.containsHeader("Set-Cookie")){
        	logger.debug("Clearing the Set-Cookie");
        	httpResponse.setHeader("Set-Cookie", "");
        	logger.debug("Setting JSessionId in Cookie, with HTTP Only flag");
        	if (request.isSecure()) {
        		jCookie = new Cookie ("JSESSIONID",sessionid+"; HttpOnly ; Secure");
        	}else{
        		jCookie = new Cookie ("JSESSIONID",sessionid+"; HttpOnly");
        	}
        	httpResponse.addCookie(jCookie);
        }*/
        logger.debug("Exiting the HTTPHeaderFilter");
    }
   
	public void init(FilterConfig arg0) throws ServletException {		
	}
}
