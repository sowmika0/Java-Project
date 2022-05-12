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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.temenos.arc.security.AuthenticationConstants;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.jaas.ArcLoginModuleInterface;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.SeedCredential;
import com.temenos.arc.security.jaas.T24ImpersonateCredential;
import com.temenos.arc.security.jaas.T24PasswordCredential;
import com.temenos.arc.security.jaas.T24Principal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Servlet filter to ensure that every request to Browser can only take place if there
 * is a valid session for the user in the authentication server.   This is done in
 * {@link #doFilter(ServletRequest, ServletResponse, FilterChain)} method.
 * 
 * @author jannadani
 */
public class AuthenticationFilter implements Filter {

	private static final String T24_SESSION_COOKIE = "T24SESSIONID";
    /** The logger. */
	private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	public static final String SESSION_SEED_POSITIONS = "seedPositions";
	public static final String MEMWORD_STRING = "MemWordString";
    public static final String PASSWORD = "Password";
    public static final String PASSPHRASE = "Passphrase";
    public static final String NEW_PASSWORD1 = "NewPassword";
    public static final String NEW_PASSWORD2 = "ConfirmPassword";
	public static final String J_PASSWORD = "p_password";

    
    /**
     * Instantiates a new authentication filter.
     */
    public AuthenticationFilter() {
        super();
    	logger.info("Constructing Filter...");
    }

    /**
     * no-op.
     */
    public void destroy() {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
    	logger.info("Entering doFilter...");
    	if (!(request instanceof HttpServletRequest)) {
        	if (logger.isErrorEnabled()) logger.error("Filter only supports HTTP requests.");
            throw new ServletException("Filter only supports HTTP requests");
        }
        // Wrap the response.  Wrapper enables getStatus() method
        StatusHolder wResponse = new StatusHolder(response);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        /******************************************/
    	HttpSession session = httpServletRequest.getSession(false);
        LoginParameterisedRequest wrapper = new LoginParameterisedRequest(httpServletRequest);
        // channel is already in the http session
    	String instance_name =(String)session.getAttribute("channel");
    	if(instance_name != null){
    		logger.info("Setting Instance Name "+instance_name);
    		wrapper.put("instance",instance_name);	
    	}else{
    		logger.info("Instance Name is Null");
    	}
             
    	
    	/******************************************/
        if (this.arcSessionExists(httpServletRequest)) {
        	if (logger.isDebugEnabled()) logger.debug("session exists.");
            if (this.isLoginRequest(httpServletRequest)) {
                // Possible hack attempt to obtain T24 login page
                // TODO SJP 08/12/2006 Figure out the best way to defend against this, and how to respond
            	if (logger.isErrorEnabled()) logger.error("Attempt to login when we already have a session.");
                throw new ServletException("Failed to complete authentication");
            }
            if (this.isLogoutRequest(httpServletRequest)) {
            	if (logger.isDebugEnabled()) logger.info("Logout request.");
                this.doT24Logout(wrapper, wResponse, filterChain);
            } else {
                        	ArcUserPrincipal principal = (ArcUserPrincipal) session.getAttribute(ArcUserPrincipal.class.getName());
           	 	String commandInAuthFilter = httpServletRequest.getParameter("command");
           	 	if(principal != null ){
                if (commandInAuthFilter == null)
                	commandInAuthFilter = (String) httpServletRequest.getAttribute("command");
                //This kind of request should not reach browserServlet. So we forward it to new home page
                if(commandInAuthFilter == null){
                	this.invalidateSession(httpServletRequest);
                	try{
                	httpServletResponse.reset();
                	String contextPath = httpServletRequest.getContextPath(); 
                	httpServletResponse.sendRedirect(contextPath + "/servlet/BrowserServlet");
                	//this.doLogin(httpServletRequest, httpServletResponse, filterChain);
                	return;
                	}
                	catch(Exception ex){
                		logger.error("Exception in AuthFilter while forwarding request to home page");
                	}
                  }
           	 	}
            	if (logger.isDebugEnabled()) logger.info("Refresh request.");

                this.refreshAuthentication(wrapper, wResponse, filterChain);
            }
        } else {
        	if (logger.isDebugEnabled()) logger.info("Login request.");
            this.doLogin(wrapper, wResponse, filterChain);
        }
        // Is this the T24 login page?  If it is then a 401 should have been set
        // on the response. If so invalidate the session
        if (wResponse.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
        	if (logger.isInfoEnabled()) logger.info("Invalidate the session due to a 401.");
        	invalidateSession(wrapper);
        } 
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    	logger.info("Initialising Filter...");

    }
    
    /**
     * Arc login module credential from.
     * 
     * @param subject the subject
     * 
     * @return the arc login module
     * 
     * @throws ServletException the servlet exception
     */
    private ArcLoginModuleInterface loginModuleCredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting ArcLoginModule from subject.");
    	// TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(ArcLoginModuleInterface.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcLoginModuleInterface) publicCredentialSet.iterator().next();
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
        return (ArcSession) publicCredentialSet.iterator().next();
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
     * Arc user principal from.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return the arc user principal
     * 
     * @throws ServletException the servlet exception
     */
    private ArcUserPrincipal arcUserPrincipalFrom(HttpServletRequest httpServletRequest) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting ArcUserPrincipal from HttpSessionRequest.");
        Principal principal = httpServletRequest.getUserPrincipal();
        if ((principal == null) || (!(principal instanceof ArcUserPrincipal))) {
        	logger.error("Error: principal is not an instance of ArcUserPrincipal");
            throw new ServletException("Failed to complete authentication");
        }
        return (ArcUserPrincipal) principal;
    }

    /**
     * Assert auth type from.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @throws ServletException the servlet exception
     */
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

    /**
     * Do login.
     * 
     * @param request the request
     * @param response the response
     * @param filterChain the filter chain
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    private void doLogin(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
    	AuthenticationServerConfiguration config = getConfig();
    	boolean doNotImpersonate = false;
    	if (logger.isDebugEnabled()) logger.info("Do the T24 login process.");
    	 if("true".equalsIgnoreCase(config.getConfigValue(AuthenticationServerConfiguration.IMPERSONATE_NOT_ALLOWED)))
         	doNotImpersonate = true;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession(false);
        
        this.assertAuthTypeFrom(httpServletRequest);
        ArcUserPrincipal arcUserPrincipal = this.arcUserPrincipalFrom(httpServletRequest);
        Subject subject = arcUserPrincipal.getSubject();

        // Remove the session attributes which is no longer used (particularly the password related attributes)
        session.removeAttribute(PASSWORD);
        session.removeAttribute(NEW_PASSWORD1);
        session.removeAttribute(NEW_PASSWORD2);
        session.removeAttribute(MEMWORD_STRING);
        session.removeAttribute(PASSPHRASE);
        
     // Validate seed credential if this is seeded memorable word authentication, ie., if seedpositions exist in HttpSession
        if (!validateSeedPositions(subject, session)) {
      	  logger.error("Seed positions have been modified.");
          throw new ArcAuthenticationServerException("Authentication Error");
        }
        // Get the username
        T24Principal t24Principal  = null;
        // Get the password credential
        T24PasswordCredential t24PasswordCredential = null;
        T24ImpersonateCredential t24ImpersonateCredtial = null;
        if (!doNotImpersonate) {
        	t24Principal = this.t24PrincipalFrom(subject);
        	t24PasswordCredential = this.t24CredentialFrom(subject);
        } 
        else {
        	t24ImpersonateCredtial = this.t24ImpersonateCredentialFrom(subject);
        }
        LoginParameterisedRequest wrapper = null;
        if (request instanceof LoginParameterisedRequest) {
        	logger.debug("is a wrapper object.");
        	wrapper = (LoginParameterisedRequest) request;
        } else {
        	logger.debug("is not a wrapper object. (Should never get here)");
            wrapper = new LoginParameterisedRequest(httpServletRequest);
        }
        ArcSession arcSession = getArcSession(arcUserPrincipal, httpServletRequest.getSession());
     // No need to check for PasswordCredential if impersonation is NOT allowed.
        if (!doNotImpersonate) {
        if (arcSession.getSessionObject() instanceof AuthSession) {
        	AuthSession rsaSession = (AuthSession) arcSession.getSessionObject();
        	try {
                if (session.getAttribute(AuthenticationConstants.AUTHENTICATION_TYPE).equals(AuthenticationConstants.PASSWORD)) {
                    Object passwordObj = session.getAttribute(AuthenticationConstants.PASSWORD);
                    if (null != passwordObj) {
                        t24PasswordCredential = new T24PasswordCredential((String)passwordObj);
                    } 
                } else if (session.getAttribute(AuthenticationConstants.AUTHENTICATION_TYPE).equals(AuthenticationConstants.PIN)) {
                    t24PasswordCredential = new T24PasswordCredential(rsaSession.getShell());
                } else {
                    // no authentication type supplied
                }
                    
                    
        	} catch (AuthAgentException e) {
        		if (logger.isErrorEnabled()) logger.error("Invalid RSA session.");
        		invalidateSession(request);
                throw new ServletException("Invalid RSA Session.");
        	}
        }else {
            String oldAlsi =(String)session.getAttribute(J_PASSWORD);
            String currentAlsi = (String)arcSession.getSessionObject();
              if(oldAlsi != null && currentAlsi != null ){
               if(!(oldAlsi.equals(currentAlsi))){
            	  logger.error("User Alsi is invalid");
                  throw new ArcAuthenticationServerException("Authentication Error");
               }
              }
        	}
        }
    	if (logger.isDebugEnabled()) logger.debug("Decrypt the T24 info.");
    	// only do the stuff if we have a non null user and password
    	if (doNotImpersonate || (null != t24Principal.getName() && null != t24PasswordCredential.getPassPhrase())) {
	        CryptographyService cryptService = CryptographyService.getInstance(config);
	        String t24UserName= null;
	        String t24PassPhrase = null;
	        
	        if (doNotImpersonate) {
	        	t24UserName = t24ImpersonateCredtial.getImpersonateCredential();
	        } 
	        else {
	        logger.debug("user: " + t24Principal.getName());
	        logger.debug("password: " + t24PasswordCredential.getPassPhrase());
	        t24UserName = cryptService.decrypt(t24Principal.getName(), false);
	        t24PassPhrase = cryptService.decrypt(t24PasswordCredential.getPassPhrase(), true);
	        }
	    	if (logger.isDebugEnabled()) logger.debug("Add the login details to the request.");
	        wrapper.put("command", "login");
	        wrapper.put("requestType", "CREATE.SESSION");
	        wrapper.put("AuthenticationType", "external");
	        wrapper.put("signOnName", t24UserName);
	        if (null != t24PassPhrase) {
	        wrapper.put("password", t24PassPhrase);
	        }
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
	        if(!doNotImpersonate)
	        t24PasswordCredential.destroy();
	    	// Here we need to check if the ArcSession is in the subject as some auth servers
	    	// do the authentication before the JAAS stage.  If not, it should be in the session.
	    	// Otherwise we throw an exception.
            logger.info("does it contain arc session?");
	    	if (!containsArcSession(arcUserPrincipal)) {
	    		logger.info("Does not contain arc session");
	    		addArcSession(arcUserPrincipal, httpSession);
	    	} else {
                logger.info("yes");
            }
	    	// If this is a successful login, add a new T24 session id (T24SESSIONID) to the session
            // and create a cookie.
            // In order to set httpOnly flag, need to create cookie in response header instead of creating
            // the cookie Object
            //Generate new sessionid. Very similar to JSESSIONID (32 character random hex)
            String t24SessionId = getRandomHexString(32);
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (request.isSecure()) {
                httpResponse.setHeader("Set-Cookie", T24_SESSION_COOKIE+"="+t24SessionId+"; HttpOnly ; Secure");
            } else {
                httpResponse.setHeader("Set-Cookie", T24_SESSION_COOKIE+"="+t24SessionId+"; HttpOnly");
            }
            
            // now add the T24 Session Id to the httpSession so it can be compared later
            httpSession.setAttribute(T24_SESSION_COOKIE,t24SessionId);
            
	    	if (logger.isDebugEnabled()) logger.debug("Add T24 login info to chain.");
	        filterChain.doFilter(wrapper, response);
	        // TODO SJP 06/12/2006 Figure out whether it is sufficiently secure to put the principal into the session.
	    	if (logger.isDebugEnabled()) logger.debug("Add user principal to session.");
	    	// Get the httpSession again as it has been refreshed in the Browser login process
	    	httpSession = wrapper.getSession(true);
	    	httpSession.setAttribute(ArcUserPrincipal.class.getName(), arcUserPrincipal);
	    	
    	} else {
    		// There has been a problem with the t24 user and password
    		if (logger.isErrorEnabled()) logger.error("User or password is null, invalidating session...");
    		invalidateSession(request);
            throw new ServletException("Invalid user or password received.");
    	}
    }

    private static String getRandomHexString(int n) {
        char[] returnChars = new char[n];
        int c  = 'A';
        int  r1 = 0;
        for (int i=0; i < n; i++)
        {
            r1 = (int)(Math.random() * 2);
            switch(r1) {
                // 1/2 of characters will be numbers, 1/2 will be letters
                case 0: c = '0' +  (int)(Math.random() * 10); break;
                case 1: c = 'A' +  (int)(Math.random() * 6); break;
                // v small chance that value will be exactly 2
                case 2: c = 'A' +  (int)(Math.random() * 6); break;
            }
            returnChars[i] = (char)c;
        }
        return new String(returnChars);
    }
    /**
     * Gets the config.
     * 
     * @return the config
     */
    private AuthenticationServerConfiguration getConfig() {
        String configFile = System.getProperty("java.security.auth.login.config");
        return AuthenticationServerConfiguration.fromConfigFile(configFile);
    }

    // TODO SJP 08/12/2006 The response returned from T24 upon logout probably needs to be discarded...
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
    private void doT24Logout(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        try {
            // delete the T24SESSIONID
            // Check for the T24 Session Cookie
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            Cookie[] cookies = httpRequest.getCookies();
            for (Cookie cookie:cookies) {
                if (cookie.getName().equals(T24_SESSION_COOKIE)) {
                    Cookie deleteCookie = new Cookie( T24_SESSION_COOKIE, "" );
                    deleteCookie.setMaxAge( 0 );
                    httpResponse.addCookie(deleteCookie);
                }
            }
            // Allow T24 to process the logout
            filterChain.doFilter(request, response);
        } finally {
            invalidateSession(request);
        }
    }

	/**
	 * Invalidate session.
	 * 
	 * @param request the request
	 */
	private void invalidateSession(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession != null) {
		    httpSession.invalidate();
		}
	}

    /**
     * Checks if is login request.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if is login request
     */
    private boolean isLoginRequest(final HttpServletRequest httpServletRequest) {
        String command = httpServletRequest.getParameter("command");
        return ("login".equalsIgnoreCase(command));
    }

    /**
     * Checks if is logout request.
     * 
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if is logout request
     */
    private boolean isLogoutRequest(final HttpServletRequest httpServletRequest) {
        String command = httpServletRequest.getParameter("command");
        String requestType = httpServletRequest.getParameter("requestType");
        return (("globusCommand".equalsIgnoreCase(command)) && ("DESTROY.SESSION".equalsIgnoreCase(requestType)));
    }

    /**
     * Refresh.
     * 
     * @param arcLoginModule the arc login module
     * @param arcSession the arc session
     * @param httpServletRequest the http servlet request
     * 
     * @return true, if successful
     */
    private boolean refresh(final ArcLoginModuleInterface loginModule, 
    						final ArcSession arcSession, 
    						final HttpServletRequest httpServletRequest) {
        boolean refreshed = true;
        try {
            if (loginModule.isSessionOwner() && null != arcSession) {
                try {
                    SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        // TODO SJP 06/12/2006 Test this with a SecurityManager. Exact context used here is a bit suspicious...
                        securityManager.checkPermission(new AuthPermission("refreshCredential"), AccessController.getContext());
                    }
                    loginModule.refresh(arcSession);
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

    /**
     * Refresh authentication.
     * 
     * @param request the request
     * @param response the response
     * @param filterChain the filter chain
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    private void refreshAuthentication(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpSession session = httpServletRequest.getSession(false);
        if (!this.arcSessionExists(httpServletRequest)) {
            throw new ServletException("Failed to complete authentication");
        }
        
        // Check for the T24 Session Cookie
        Cookie[] cookies = httpServletRequest.getCookies();
        boolean foundSession=false;
        String t24SessionId = null; 
        for (Cookie cookie:cookies) {
            if (cookie.getName().equals(T24_SESSION_COOKIE)) {
                foundSession = true;
                t24SessionId = cookie.getValue();
                break;
            }
        }
        
        if (!foundSession) {
            logger.error("Failed to validate session 1");
            session.invalidate();
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/modelbank/unprotected/400.jsp");
            return;
        } 
        
        // check session id against the http session
        Object t24SessionObject = session.getAttribute(T24_SESSION_COOKIE);
        if (null != t24SessionObject) {
            String storedSessionId = (String) t24SessionObject;
            if (t24SessionId.equals(storedSessionId)) {
                logger.info("Successfully validated session cookie");
            } else {
                logger.error("Failed to validate session 2");
                logger.debug(t24SessionId + " : " + storedSessionId);
                session.invalidate();
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/modelbank/unprotected/400.jsp");
                return;
            }
        } else {
            logger.error("Failed to validate session 3");
            session.invalidate();
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/modelbank/unprotected/400.jsp");
            return;
        }        
        
        ArcUserPrincipal arcUserPrincipal = this.arcUserPrincipalFrom(httpServletRequest);
        Subject subject = arcUserPrincipal.getSubject();
        ArcLoginModuleInterface loginModule = this.loginModuleCredentialFrom(subject);
        ArcSession arcSession = this.arcSessionCredentialFrom(subject);
        if (this.refresh(loginModule, arcSession, httpServletRequest)) {
            
            filterChain.doFilter(httpServletRequest, response);
         } else {
            // TODO SJP 06/12/2006 Probably want to redirect to a login page at this point, rather than throwing exceptions!
            throw new ServletException("Failed to complete authentication");
         }
            
    }

    /**
     * T24 credential from.
     * 
     * @param subject the subject
     * 
     * @return the t24 password credential
     * 
     * @throws ServletException the servlet exception
     */
    private T24PasswordCredential t24CredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting T24 credentials from Subject.");
        // TODO SJP 01/12/2006 Should really be private - not public - credential so there's access control (getPrivateCredentials)
        Set publicCredentialSet = subject.getPublicCredentials(T24PasswordCredential.class);
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (T24PasswordCredential) publicCredentialSet.iterator().next();
    }

    
    /**
     * T24 principal from.
     * 
     * @param subject the subject
     * 
     * @return the t24 principal
     * 
     * @throws ServletException the servlet exception
     */
    private T24Principal t24PrincipalFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting T24Principal from Subject.");
        Set principalSet = subject.getPrincipals(T24Principal.class);
        if (principalSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (T24Principal) principalSet.iterator().next();
    }
    /**
     * T24 Impersonate credential from.
     * 
     * @param subject the subject
     * 
     * @return the t24 password credential
     * 
     * @throws ServletException the servlet exception
     */
    private T24ImpersonateCredential t24ImpersonateCredentialFrom(final Subject subject) throws ServletException {
    	if (logger.isDebugEnabled()) logger.debug("Getting T24 impersonate credentials from Subject.");
        Set publicCredentialSet = subject.getPublicCredentials(T24ImpersonateCredential.class);
        logger.info("public Credential Set size = " + publicCredentialSet.size());
        if (publicCredentialSet.size() != 1) {
            throw new ServletException("Failed to complete authentication");
        }
        return (T24ImpersonateCredential) publicCredentialSet.iterator().next();
    }
    
    /**
     * Validating seed position with the one stored in httpsession
         * 
         * @param subject the subject
         * 
         * @return the t24 principal
         * 
         * @throws ServletException the servlet exception
         */
        private boolean validateSeedPositions(final Subject subject, HttpSession httpSession) throws ServletException {
        	if (logger.isDebugEnabled()) logger.debug("Validating Seed Positions.");
        	boolean validateSeeds = false;
        	
        	try {
        		String seedFromCredential = null;
                String seedFromSession = (String) httpSession.getAttribute(SESSION_SEED_POSITIONS);
              //Remove the Session Seed Position value as soon as it is read
                httpSession.removeAttribute(SESSION_SEED_POSITIONS);
                
                logger.info("SeedPosition from Session : " + seedFromSession);           
                Set credentialSet = subject.getPublicCredentials(SeedCredential.class);
                
                if (credentialSet.size() == 1) {
                	SeedCredential seeds = (SeedCredential) credentialSet.iterator().next();
                	seedFromCredential = seeds.getPassPhrase();
                	logger.info("SeedPosition from SeedCredential : " + seedFromCredential);
                }
                if (seedFromSession == null && seedFromCredential == null) {
                	//if both values are null then no need to validate for seed positions.
                	validateSeeds = true;
                }                  
                
                else {
                	// Validate and check whether seed position values from credential is same as the one which is in session
                	//in case if seed position value in session is empty then also validation will be false there by raising exception
                	 if (seedFromCredential != null && seedFromCredential.equals(seedFromSession)) { 
                		 validateSeeds = true;
                      }
                }
        	}
        	catch (Exception ex) {
        		logger.info("Error occurred while validating Seedpositions : " + ex.getMessage());
        	}
        	return validateSeeds;
        }
        
    
    /**
     * Contains arc session.
     * 
     * @param userPrincipal the user principal
     * 
     * @return true, if successful
     */
    private boolean containsArcSession(ArcUserPrincipal userPrincipal) {
    	try {
    		ArcSession arcSession = arcSessionCredentialFrom(userPrincipal.getSubject());
    		
    		if (null == arcSession.getSessionObject() || 
    				(arcSession.getSessionObject() instanceof String && ((String)arcSession.getSessionObject()).equals(""))) {
    			return false;
    		}
    	} catch (ServletException e) {
    		// There is no ArcSession
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Adds the arc session.
     * 
     * @param userPrincipal the user principal
     * @param httpSession the http session
     */
    private void addArcSession(ArcUserPrincipal userPrincipal, HttpSession httpSession) {
    	Object arcSessionObj = httpSession.getAttribute(ArcSession.class.getName());
    	ArcSession arcSession = null;
    	if (null != arcSessionObj) {
    		arcSession = (ArcSession) arcSessionObj;
    	}
    	try {
    		logger.info("Get existing empty arc session and remove it");
    		ArcSession arcSess = this.arcSessionCredentialFrom(userPrincipal.getSubject());
        	userPrincipal.getSubject().getPublicCredentials().remove(arcSess);
            logger.info("removed old session");
    	} catch (ServletException e) {
    		// do nothing, this happens if the arcSession that we want to remove doesn't exist
    	}
		logger.info("Add the new arc session from the http session");
    	userPrincipal.getSubject().getPublicCredentials().add(arcSession);
    	// now remove the arc session from the http session. 
		logger.info("Remove arcSession from http session");
    	httpSession.removeAttribute(ArcSession.class.getName());
		logger.info("Removed arcSession from http session");
    }
    /**
     * Gets the arc session.
     * 
     * @param userPrincipal the user principal
     * @param httpSession the http session
     */
    private ArcSession getArcSession(ArcUserPrincipal userPrincipal, HttpSession httpSession) {
    	ArcSession arcSession = null;
    	if (containsArcSession(userPrincipal)) {
    		// get the arc session
    		Set sessionSet = userPrincipal.getSubject().getPublicCredentials(ArcSession.class);
    		arcSession = (ArcSession) sessionSet.iterator().next();
    		
    	} else {
	    	Object arcSessionObj = httpSession.getAttribute(ArcSession.class.getName());
	    	if (null != arcSessionObj) {
	    		arcSession = (ArcSession) arcSessionObj;
	    	}
    	}
    	return arcSession;
    }
}
