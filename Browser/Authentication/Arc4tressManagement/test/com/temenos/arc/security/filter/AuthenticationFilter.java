package com.temenos.arc.security.filter;

import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.util.Set;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.T24PasswordCredential;
import com.temenos.arc.security.jaas.T24Principal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Servlet filter to ensure that every request to Browser can only take place if there
 * is a valid session for the user in the authentication server.   This is done in 
 * {@link #doFilter(ServletRequest, ServletResponse, FilterChain)} method.
 * @author jannadani
 *
 */
public class AuthenticationFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
    public AuthenticationFilter() {
        super();
    }

    /**
     * no-op
     */
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        if (!(request instanceof HttpServletRequest)) {
        	if (logger.isErrorEnabled()) logger.error("Filter only supports HTTP requests.");
            throw new ServletException("Filter only supports HTTP requests");
        }
        // Wrap the response.  Wrapper enables getStatus() method
        StatusHolder wResponse = new StatusHolder(response);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (this.arcSessionExists(httpServletRequest)) {
        	if (logger.isDebugEnabled()) logger.debug("session exists.");
            if (this.isLoginRequest(httpServletRequest)) {
                // Possible hack attempt to obtain T24 login page
                // TODO SJP 08/12/2006 Figure out the best way to defend against this, and how to respond
            	if (logger.isErrorEnabled()) logger.error("Attempt to login when we already have a session.");
                throw new ServletException("Failed to complete authentication");
            }
            if (this.isLogoutRequest(httpServletRequest)) {
            	if (logger.isDebugEnabled()) logger.debug("Logout request.");
                this.doT24Logout(request, wResponse, filterChain);
            } else {
            	if (logger.isDebugEnabled()) logger.debug("Refresh request.");
                this.refreshAuthentication(request, wResponse, filterChain);
            }
        } else {
        	if (logger.isDebugEnabled()) logger.debug("Login request.");
            this.doLogin(request, wResponse, filterChain);
        }
        // Is this the T24 login page?  If it is then a 401 should have been set
        // on the response. If so invalidate the session
        if (wResponse.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
        	if (logger.isInfoEnabled()) logger.info("Invalidate the session due to a 401.");
        	invalidateSession(request);
        } 
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }
    
    private ArcLoginModule arcLoginModuleCredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting ArcLoginModule from subject.");
    	// TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(ArcLoginModule.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcLoginModule) publicCredentialSet.iterator().next();
    }

    private ArcSession arcSessionCredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting ArcSession from subject.");
        // TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(ArcSession.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcSession) publicCredentialSet.iterator().next();
    }

    private boolean arcSessionExists(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            ArcUserPrincipal principal = (ArcUserPrincipal) session.getAttribute(ArcUserPrincipal.class.getName());
            return (principal != null);
        }
        return false;
    }

    private ArcUserPrincipal arcUserPrincipalFrom(HttpServletRequest httpServletRequest) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting ArcUserPrincipal from HttpSessionRequest.");
        Principal principal = httpServletRequest.getUserPrincipal();
        logger.debug("Principal class loader: " + principal.getClass().getClassLoader());
        logger.debug("Other class loader: " +ArcUserPrincipal.class.getClassLoader());
        if ((principal == null) || (!(principal instanceof ArcUserPrincipal))) {
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcUserPrincipal) principal;
    }

    private void assertAuthTypeFrom(HttpServletRequest httpServletRequest) throws ServletException {
        String authType = httpServletRequest.getAuthType();
        if (authType == null) {
            // no authentication has occurred; it should have at this point
        	if (logger.isErrorEnabled()) logger.error("Authentication type is null, should have authenticated by now.");
            throw new ServletException("Failed to complete authentication");
        }
        if (!HttpServletRequest.FORM_AUTH.equals(authType)) {
            // FORM authentication method required to obtain username, password
        	if (logger.isErrorEnabled()) logger.error("Not form authentication, needs to be for the whole process to work.");
            throw new ServletException("Failed to complete authentication");
        }
    }

    private void doLogin(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Do the login process.");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        this.assertAuthTypeFrom(httpServletRequest);
        ArcUserPrincipal arcUserPrincipal = this.arcUserPrincipalFrom(httpServletRequest);
        Subject subject = arcUserPrincipal.getSubject();
        // Get the username
        T24Principal t24Principal = this.t24PrincipalFrom(subject);
        // Get the password credential
        T24PasswordCredential t24PasswordCredential = this.t24CredentialFrom(subject);
        LoginParameterisedRequest wrapper = new LoginParameterisedRequest(httpServletRequest);
        AuthenticationServerConfiguration config = getConfig();
    	if (logger.isDebugEnabled()) logger.debug("Decrypt the T24 info.");
    	// only do the stuff if we have a non null user and password
    	if (null != t24Principal.getName() && null != t24PasswordCredential.getPassPhrase()) {
	        CryptographyService cryptService = CryptographyService.getInstance(config);
	        String t24UserName = cryptService.decrypt(t24Principal.getName(), false);
	        String t24PassPhrase = cryptService.decrypt(t24PasswordCredential.getPassPhrase(), true);
	    	if (logger.isDebugEnabled()) logger.debug("Add the login details to the request.");
	        wrapper.put("command", "login");
	        wrapper.put("requestType", "CREATE.SESSION");
	        wrapper.put("signOnName", t24UserName);
	        wrapper.put("password", t24PassPhrase);
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
	        t24PasswordCredential.destroy();
	    	if (logger.isDebugEnabled()) logger.debug("Add T24 login info to chain.");
	        filterChain.doFilter(wrapper, response);
	        // TODO SJP 06/12/2006 Figure out whether it is sufficiently secure to put the principal into the session.
	    	if (logger.isDebugEnabled()) logger.debug("Add user principal to session.");
	        httpSession.setAttribute(ArcUserPrincipal.class.getName(), arcUserPrincipal);
    	} else {
    		// There has been a problem with the t24 user and password
    		if (logger.isErrorEnabled()) logger.error("User or password is null, invalidating session...");
    		invalidateSession(request);
            throw new ServletException("Invalid user or password received.");
    	}
    }

    private AuthenticationServerConfiguration getConfig() {
        String configFile = System.getProperty("java.security.auth.login.config");
        return AuthenticationServerConfiguration.fromConfigFile(configFile);
    }

    // TODO SJP 08/12/2006 The response returned from T24 upon logout probably needs to be discarded...
    private void doT24Logout(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        try {
            // Allow T24 to process the logout
            filterChain.doFilter(request, response);
        } finally {
            invalidateSession(request);
        }
    }

	private void invalidateSession(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession != null) {
		    httpSession.invalidate();
		}
	}

    private boolean isLoginRequest(final HttpServletRequest httpServletRequest) {
        String command = httpServletRequest.getParameter("command");
        return ("login".equalsIgnoreCase(command));
    }

    private boolean isLogoutRequest(final HttpServletRequest httpServletRequest) {
        String command = httpServletRequest.getParameter("command");
        String application = httpServletRequest.getParameter("application");
        return (("globusCommand".equalsIgnoreCase(command)) && ("SIGN.OFF".equalsIgnoreCase(application)));
    }

    private boolean refresh(final ArcLoginModule arcLoginModule, 
    						final ArcSession arcSession, 
    						final HttpServletRequest httpServletRequest) {
        boolean refreshed = true;
        try {
            if (arcLoginModule.isSessionOwner() && null != arcSession) {
                try {
                    SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        // TODO SJP 06/12/2006 Test this with a SecurityManager. Exact context used here is a bit suspicious...
                        securityManager.checkPermission(new AuthPermission("refreshCredential"), AccessController.getContext());
                    }
                    arcLoginModule.refresh(arcSession);
                } catch (IllegalStateException e) {
                    refreshed = false;
                }
            } else {
                refreshed = false;
            }
        } finally {
            if (!refreshed) {
                httpServletRequest.getSession(false).invalidate();
            }
        }
        return refreshed;
    }

    private void refreshAuthentication(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (!this.arcSessionExists(httpServletRequest)) {
            throw new ServletException("Failed to complete authentication");
        }
        ArcUserPrincipal arcUserPrincipal = this.arcUserPrincipalFrom(httpServletRequest);
        Subject subject = arcUserPrincipal.getSubject();
        ArcLoginModule arcLoginModule = this.arcLoginModuleCredentialFrom(subject);
        ArcSession arcSession = this.arcSessionCredentialFrom(subject);
        if (this.refresh(arcLoginModule, arcSession, httpServletRequest)) {
//            PostMethodOnlyRequest wrapper = new PostMethodOnlyRequest(httpServletRequest);
//            filterChain.doFilter(wrapper, response);
            filterChain.doFilter(httpServletRequest, response);
        } else {
            // TODO SJP 06/12/2006 Probably want to redirect to a login page at this point, rather than throwing exceptions!
            throw new ServletException("Failed to complete authentication");
        }
    }

    private T24PasswordCredential t24CredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting T24 credentials from Subject.");
        // TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(T24PasswordCredential.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (T24PasswordCredential) publicCredentialSet.iterator().next();
    }

    private T24Principal t24PrincipalFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting T24Principal from Subject.");
        Set principalSet = subject.getPrincipals(T24Principal.class);
        if (principalSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (T24Principal) principalSet.iterator().next();
    }
}
