package com.temenos.arc.security.filter;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * This class  is required for Websphere in order to call the ARC JAAS 
 * login modules.  Websphere requires login modules to be in a proprietary
 * format, hence this filter will mean that the JAAS functionality in
 * Websphere is not required.
 * @author vraghavan
 *
 */
public class JaasFilter implements Filter {
	
	public static final String USERNAME = "UserId";
	public static final String PASSWORD = "Passphrase";

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(JaasFilter.class);
    private FilterConfig filterConfig;
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /**
     * This method will instantiate a LoginContext, get the JAAS properties
     * from the JAAS config file (specified in the ??? system property).
     * 
     * The 
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        logger.info("Entering doFilter in JaasFilter.");
        HttpServletRequest servletRequest = null; 
        HttpServletResponse servletResponse = null;
        
        if (request instanceof HttpServletRequest) {
            servletRequest = (HttpServletRequest) request;
            servletResponse = (HttpServletResponse) response;
        } else {
            logger.error("Must be a HttpServletRequest.");
            throw new ServletException("Must be a HttpServletRequest.");
        }
        // If the session already exists, then continue 
        if (arcSessionExists(servletRequest)) {
            // need to add the ArcUserPrincipal to the Request
            RequestWrapper wrapper = new RequestWrapper(servletRequest);
            wrapper.setUserPrincipal(getUserPrincipal(servletRequest));
            wrapper.setAuthType(HttpServletRequest.FORM_AUTH);
            chain.doFilter(wrapper, response);
            return;
        }

        ServletContext context = filterConfig.getServletContext();
        // We are not logged in, so we must check if there is a username/password 
        // provided.  If not, we redirect to the login page
        if (null == servletRequest.getParameter("username") ||
            null == servletRequest.getParameter("password")) {
            String login_page = filterConfig.getInitParameter("login_page");
            String session_logout_page = filterConfig.getInitParameter("session_logout_page");
            if (login_page != null && !"".equals(login_page)) {
				logger.info("Login Page : " + login_page);
				logger.info("Session Logout Page : "
						+ session_logout_page);
				String compScreen = servletRequest.getParameter("compScreen");
				if ( ( compScreen != null ) && ( ! compScreen.equals("") ) ) {
					// Request is from compScreen and Session is null so forward to dummy page, which will then redirects to actual login page
					servletResponse.reset();
					servletResponse.sendRedirect(servletRequest
							.getContextPath()
							+ session_logout_page);
					
				}
				else{
					//First Login
					context.getRequestDispatcher(login_page).forward(request, response);
					return;
	            }
            }
           
        }
        
        // Now we know that we are not already logged in.  Must check what 
        // is being accessed and redirecct to login page if not  
        
        logger.info("No existing session, proceeding to JAAS authentication.");
        // THIS IS THE POINT WHERE WE PERFORM THE JAAS AUTHENTICATION
        // Create a new Callback Handler which will contain the user id 
        // and password.  These will be returned in the callbacks when
        // the login module is processed.
        
        //CallbackHandler cbh = new T24CallbackHandler(
        //       (String)servletRequest.getParameter("username"),
        //       (String)servletRequest.getParameter("password"));
        
        CallbackHandler cbh = new T24CallbackHandler(
        		(String)servletRequest.getSession().getAttribute(USERNAME),
        		(String)servletRequest.getSession().getAttribute(PASSWORD));
        
        logger.info("Create the LoginContext");
        // Create the login context
        LoginContext lc = null;
        try {
//            // Set the config system property
//            String jaasConfigProperty = "java.security.auth.login.config";
//            String jaasConfigFile = filterConfig.getInitParameter(jaasConfigProperty);
//            logger.info("jaasConfigProperty" + jaasConfigProperty);
//            logger.info("jaasConfigFile" + jaasConfigFile);
//            System.setProperty(jaasConfigProperty, jaasConfigFile);
            // Here we pass in the hardcoded app name and the callback handler.
            // The subject is not passed in, the LC will create an empty one automatically.
            lc = new LoginContext("ARC", cbh);
            logger.info("Created new LoginContext");
        } catch (LoginException e) {
            logger.error("Error logging in.", e);
            throw new ServletException("Error creating LoginContext.", e);
        }
        logger.debug("LoginContext created, now try to log in.");
        try {
            lc.login();
        } catch (LoginException e) {
            logger.error("Error logging in.", e);
            throw new ServletException("Error logging in.", e);
        }
        logger.info("Logged in with no error. Now try to get the subject");
        Subject newSubject = lc.getSubject();
        
        logger.debug("Make sure ARC Session exists in subject.");
        try {
            if (null == arcSessionCredentialFrom(newSubject)) {
                logger.error("ARC Session does not exist in subject.");
                throw new ServletException("ARC Session does not exist in subject.");
            }
        } catch (ServletException e) {
            logger.error("ARC Session does not exist in subject.");
            throw new ServletException("ARC Session does not exist in subject.", e);
        }
        
        // finally add the subject to the ArcUserPrincipal and add to the request
        // Need to wrap the request first so we can add the principal.
        logger.info("Adding the subject to the session.");
        RequestWrapper wrapper = new RequestWrapper(servletRequest);
        wrapper.setUserPrincipal(new ArcUserPrincipal(servletRequest.getParameter("username"),
                                                       newSubject ));
        wrapper.setAuthType(HttpServletRequest.FORM_AUTH);
        chain.doFilter(wrapper, response);
    }   
    
    
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    /**
     * Arc session exists.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if successful
     */
    private boolean arcSessionExists(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            ArcUserPrincipal principal = (ArcUserPrincipal) session.getAttribute(ArcUserPrincipal.class.getName());
            return (principal != null);
        }
        return false;
    }

    /**
     * Get the user principal from the session and put it in the request.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if successful
     */
    private ArcUserPrincipal getUserPrincipal(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            ArcUserPrincipal principal = (ArcUserPrincipal) session.getAttribute(ArcUserPrincipal.class.getName());
            return principal;
        } else {
            return null;
        }
    }
    /**
     * Arc session credential from.
     * 
     * @param subject the subject
     * 
     * @return the arc session
     * 
     * @throws ServletException the servlet exception
     */
    private ArcSession arcSessionCredentialFrom(final Subject subject) throws ServletException {
        if (logger.isDebugEnabled()) logger.debug("Getting ArcSession from subject.");
        // TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(ArcSession.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        ArcSession arcSession =(ArcSession) publicCredentialSet.iterator().next();
        logger.info("arc session: " + arcSession.getSessionObject());
        return arcSession;
    }

}