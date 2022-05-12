package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.security.XssChecker;

/**
 * A servlet filter which rejects requests containing cross site scripting attacks.
 * 
 * @author dburford
 */
public class FormFieldInputFilter implements Filter {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FormFieldInputFilter.class);

    /** The Constant XSS_PATTERN_NAME_PARAM. */
    private static final String XSS_PATTERN_NAME_PARAM = "xssPatternName";
    
    /** The Constant REDIRECT_PAGE_PARAM. */
    private static final String REDIRECT_PAGE_PARAM = "redirectPage";
    
    /** The valid input regex. */
    private Pattern validInputRegex;

    /** The page to redirect errors to. */
    private String redirectPage;

    /**
     * Initialize servlet filter from config file.
     * @param conf the conf
     * @throws ServletException the servlet exception
     */
    public void init(FilterConfig conf) throws ServletException {
    	
    	// Get the page to redirect suspicious requests to
    	redirectPage = conf.getInitParameter(REDIRECT_PAGE_PARAM);
    	
    	// Get the pattern to use to check the input
    	String xssPatternName = conf.getInitParameter(XSS_PATTERN_NAME_PARAM);
    	String xssPattern     = conf.getServletContext().getInitParameter(xssPatternName);
        validInputRegex = Pattern.compile(xssPattern);
        if (validInputRegex == null) {
            throw new ServletException(XSS_PATTERN_NAME_PARAM + " must be specified in web.xml for FormFieldInputFilter");
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Filter supports only HTTP requests");
        }

        XssChecker xssChecker = new XssChecker();
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            String paramValue = request.getParameter(paramName);
            xssChecker.check(validInputRegex, paramName, paramValue);
        }
        Set<String> errors = xssChecker.getErrors();
        if (errors.isEmpty()) {
            chain.doFilter(request, response);
        } else {
            // Add the errors to the session
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpRequest.getSession().setAttribute("xssErrors", errors);
          
            // now redirect to the filter errors page which will read the xssErrors from the session.
            LOGGER.warn("Redirecting to " + redirectPage);
            httpResponse.sendRedirect(httpRequest.getContextPath() + redirectPage);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }
}
