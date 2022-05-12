package com.temenos.arc.security.jaas;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Destroyable;
import javax.security.auth.Refreshable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * JAAS login module implementation that delgates the authenticate and commit phases to 
 * {@link JaasAuthenticatable} and {@link Committable} instances respectively.  
 * @author spark
 *
 */
public final class ArcLoginModule implements LoginModule,ArcLoginModuleInterface, Serializable {

	// Key names for the shared state
	static final String USER_KEY = "userid";
	static final String PW_KEY = "pin";
	
	//
    // Properties initialised by LoginContext.
    //
    private CallbackHandler callbackHandler = null;
    private JaasConfiguration config = null;
    private Map sharedState = null;
    private Subject subject = null;

    //
    // Properties used internally during login/commit/abort phases.
    //
    private boolean authenticated = false;
    private Principal rolePrincipal = null;
    private Principal userPrincipal = null;
    private String password = null;

    /**
	 * sessionId is only set if this login module's authenticator creates an auth server session,
	 * (i.e. if {@link AuthenticationServerConfiguration.CREATE_SESSION} is set to true) 
	 * or, on logout, if one is found in the subject.  
	 */
    private ArcSession sessionId = null;
    private SeedCredential seedCredential = null;

	private static Logger logger=LoggerFactory.getLogger(ArcLoginModule.class);

	public ArcLoginModule() {
        super();
    }

    public final boolean abort() throws LoginException {
        // just reset all state
        this.callbackHandler = null;
        this.config = null;
        this.sharedState = null;
        this.subject = null;
        this.setAuthenticated(false);
        this.setRolePrincipal(null);
        this.setUserPrincipal(null);
        this.setSessionId(null);
        return true;
    }

    /**
     * Associates user and role {@link Principal} objects, and the session identifier, with the {@link Subject}.
     * 
     * @see javax.security.auth.spi.LoginModule#commit()
     * @return boolean <tt>true</tt> if authentication was successful, <tt>false</tt> if this object should be ignored in the
     *         context of <em>stacked</em> LoginModules.
     */
    public final boolean commit() throws LoginException {
    	logger.info("Committing Arc Login Module");
        if (this.isAuthenticated()) {
            Set principals = new HashSet();
            principals.add(this.getUserPrincipal());
            principals.add(this.getRolePrincipal());
            Set credentials = new HashSet();
            credentials.add(this.getSessionId());
            Committable committable = this.createCommitter();
            if (logger.isInfoEnabled()) logger.info("name principal:" + this.getUserPrincipal().getName());
            if (logger.isInfoEnabled()) logger.info("role principal:" + this.getRolePrincipal().getName());
            boolean committed = committable.commit(this, principals, credentials);
            this.setAuthenticated(committed);
        }
        return this.isAuthenticated();
    }

    /**
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     *      javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     * @param subject The subject to add {@link Principal}s and credentials ({@link Refreshable} or {@link Destroyable}) to
     *            during the 'commit' phase.
     * @param callbackHandler
     * @param sharedState Used to pass state between stacked {@link LoginModule}s, especially interesting for the flexibility it
     *            gives between the 'login' and 'commit' phases.
     * @param options The parameters configured in the jaas.config file.
     */
    public final void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState,
            final Map options) {
    	logger.info("Initializing Arc Login Module");
    	this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.config = new JaasConfiguration(options);
        // only create callback handler if exists in configuration
        if (isConfiguredOption(JaasConfiguration.CALLBACK_OVERRIDE)) {
            try {
                this.callbackHandler = createCallbackOverride();
            } catch (LoginModuleException e) {
                throw new IllegalStateException("CallbackHandler override configured, but could not be instantiated");
            }
        }
        this.setAuthenticated(false);
    }

    /**
     * Returns <tt>true</tt> for a successful authentication, otherwise throws an {@link LoginException}.
     * 
     * @see javax.security.auth.spi.LoginModule#login()
     * @return boolean <tt>true</tt> if authentication was successful, <tt>false</tt> if this object should be ignored in the
     *         context of <em>stacked</em> LoginModules.
     */
    public final boolean login() throws LoginException {
        logger.info("***** ArcLoginModule.login ******* ");
        try {
            NameCallback nameCallback = new NameCallback("username"); // "username" is just a label
            PasswordCallback passwordCallback = new PasswordCallback("temenos", false); // "temenos" is just a label
            this.handleCallbacks(nameCallback, passwordCallback);
            if (sharedState.containsKey(USER_KEY)) {
            	nameCallback.setName((String)sharedState.get(USER_KEY));
            } // else the user should already be set
            if (sharedState.containsKey(PW_KEY)) {
            	String pw = (String)sharedState.get(PW_KEY);
            	//If AuthenticationType is seeded then store the seed positions into subject
            	if ("true".equalsIgnoreCase(config.getConfigValue(JaasConfiguration.AUTHTYPE_IS_SEEDED))) {
            		String pwSeedpositions = this.getpassSeedpositions(pw);
            		this.subject.getPublicCredentials().add(new SeedCredential(pwSeedpositions));
            	}
            	passwordCallback.setPassword(pw.toCharArray());
            } else {
            	sharedState.put(USER_KEY, nameCallback.getName());
            	// Now need to parse the password to split the otp and pin
            	String password = new String(passwordCallback.getPassword());
            	int delimPosition = password.indexOf(config.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER));
            	if (delimPosition < 0 || delimPosition > password.length()) {
            		logger.error("No delimiter in password field");
            		throw new LoginModuleException("Error in login module.");
            	}
            	String pwPart2 = password.substring(delimPosition+1);
            	String pwPart1 = password.substring(0, delimPosition);
            	//If AuthenticationType is seeded then store the seed positions into subject
            	if ("true".equalsIgnoreCase(config.getConfigValue(JaasConfiguration.AUTHTYPE_IS_SEEDED))) {
            		String pwSeedpositions = this.getpassSeedpositions(pwPart1);
            		this.subject.getPublicCredentials().add(new SeedCredential(pwSeedpositions));
            	}

            	// this callback needs to be the OTP
            	passwordCallback.setPassword(pwPart1.toCharArray());
            	sharedState.put(PW_KEY, pwPart2);
            }
            if("true".equalsIgnoreCase(config.getConfigValue(JaasConfiguration.IMPERSONATE_NOT_ALLOWED))){
            	logger.info("T24 Impersonate Credential Added : " + nameCallback.getName());
            	this.subject.getPublicCredentials().add(new T24ImpersonateCredential(nameCallback.getName()));
            }
            this.password = new String(passwordCallback.getPassword());           
            FtressHelpers70.setConfig(config);
            GenericAuthenticationResponse authResponse = FtressHelpers70.getInstance().authenticate(
            		nameCallback.getName(),
    				passwordCallback.getPassword(),
    				config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
            
            logger.debug("after authenticate!");
            
            this.setUserPrincipal(new ArcUserPrincipal(nameCallback.getName()));
            this.setRolePrincipal(new ArcRolePrincipal("arcuser"));
            
            if (!config.dontCreateSession()) {
            	this.setSessionId(FtressHelpers70.getInstance().handleResponse(authResponse, config));
            }
            this.setAuthenticated(true); // there may be scenarios wherein we want to catch the preceeding exception and return false
        } catch (LoginException x) {
        	logger.info("caught login exception: " + x.getMessage());
        } 
        return this.isAuthenticated();
    }

    /**
     * Calls the authentication server to enforce the logout and cleanup of session state.
     * <p>
     * The implementation assumes the only state available to it is that set by the
     * {@link ArcLoginModule#initialize(Subject, CallbackHandler, Map, Map)} method.
     * 
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    public final boolean logout() throws LoginException {
    	logger.info("*&*&*&*& ArcLoginModule.logout *&*&*&*& ");
        Subject subject = this.getSubject();
        // 1. Obtain session id
        Set credentialSet = subject.getPublicCredentials(ArcSession.class);        
        if (credentialSet.size() > 1) {
            throw new IllegalStateException("The session identifier credential must be unique");
        }
        if (credentialSet.size() == 1) {                
//            ArcSession sessionId = (ArcSession) credentialSet.iterator().next();
            // 2. Remove ARC credentials from the Subject (inc sessionid)
            credentialSet = subject.getPublicCredentials();
            for (Iterator iter = credentialSet.iterator(); iter.hasNext();) {
                Object credential = (Object) iter.next();
                // All credentials used by this LoginModule should implement the ArcCredential interface
                if (credential instanceof ArcCredential) {
                    iter.remove();
                }
            }
        }
        // 3. Remove ARC Principals from the Subject
        Set principalSet = this.getSubject().getPrincipals();
        for (Iterator iter = principalSet.iterator(); iter.hasNext();) {
            Principal principal = (Principal) iter.next();
            // All principals used by this LoginModule should implement the ArcPrincipal interface
            if (principal instanceof ArcPrincipal) {
                iter.remove();
            }
        }
        // 4. Invalidate login state
        this.setAuthenticated(false);
        
        // 5. Clean up the server
        if (sessionId != null) {
            // we have a session on the server
            return FtressHelpers70.getInstance().logoff(sessionId);
        }
        return true;
    }

    /**
     * 
     * @return  Returns true if the login module contains an ArcSession 
     */
    public boolean isSessionOwner() {
    	if (null == sessionId) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    /**
     * This is public only because we have authentication server specific commmitters
     * @return the subject
     */
    public final Subject getSubject() {
        if (this.subject == null) {
            throw new IllegalStateException("JAAS Subject not initialised");
        }
        return this.subject;
    }

    public final boolean refresh(final ArcSession arcSession) throws LoginModuleException,
            ArcAuthenticationServerException {
    	logger.info("*&*&*&*& ArcLoginModule.refresh *&*&*&*& ");
        return FtressHelpers70.getInstance().refreshSession(getSessionId());
        
    }

    /**
     * @param passwordCallback
     * @return
     * @throws LoginModuleException
     */
    private JaasAuthenticatable createAuthenticatorForLogin(final NameCallback nameCallback, final PasswordCallback passwordCallback)
            throws LoginModuleException {
        return JaasAuthenticatorFactory.createInitial(nameCallback, passwordCallback, config);
    }

    /**
     * @param sessionId
     * @return
     * @throws LoginModuleException
     */
    private Authenticatable createAuthenticatorForLogout(final ArcSession sessionId)
            throws LoginModuleException {
        return JaasAuthenticatorFactory.create(sessionId, config);
    }

    /**
     * @param sessionId
     * @return
     * @throws LoginModuleException
     */
    private JaasAuthenticatable createAuthenticatorForRefresh(final ArcSession sessionId)
            throws LoginModuleException {
        // TODO SJP 06/12/2006 Think about using Java Permissions to protect access to this method
        return JaasAuthenticatorFactory.create(sessionId, config);
    }

    private CallbackHandler createCallbackOverride() throws LoginModuleException {
        return CallbackHandlerFactory.create(config);
    }

    /**
     * @return
     * @throws LoginModuleException
     */
    private Committable createCommitter() throws LoginModuleException {
        return CommitterFactory.create(config);
    }

    /**
     * @return the callbackHandler
     */
    private CallbackHandler getCallbackHandler() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("CallbackHandler required");
        }
        return this.callbackHandler;
    }

    private Principal getRolePrincipal() {
        return this.rolePrincipal;
    }

    private ArcSession getSessionId() {
        return this.sessionId;
    }
    
    private SeedCredential getSeedCredential() {
        return this.seedCredential;
    }

    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }
    
    public String getPassword() {
    	return this.password;
    }

    private void handleCallbacks(NameCallback nameCallback, PasswordCallback passwordCallback) throws LoginException {
        try {
            this.getCallbackHandler().handle(new Callback[] { nameCallback, passwordCallback });
        } catch (IOException e) {
            throw new LoginException(e.getLocalizedMessage());
        } catch (UnsupportedCallbackException e) {
            throw new LoginException(e.getLocalizedMessage());
        }
    }

    /**
     * @return the authenticated
     */
    private boolean isAuthenticated() {
        return this.authenticated;
    }

    private boolean isConfiguredOption(final String key) {
        String optionValue = config.getConfigValue(key);
        return (optionValue == null || !"".equals(optionValue.trim()));
    }

    /**
     * @param authenticated the authenticated to set
     */
    private void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
    }

    private void setRolePrincipal(final Principal rolePrincipal) {
        this.rolePrincipal = rolePrincipal;
    }

    private void setSessionId(final ArcSession sessionId) {
        this.sessionId = sessionId;
    }

    private void setUserPrincipal(final Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }
    
    private String getpassSeedpositions(String requestPassword) {
    	
    	String actualPasswordstring = new String(requestPassword);
    	int noofSeeds = Integer.parseInt(config.getConfigValue(JaasConfiguration.AUTHTYPE_NO_OF_SEEDS));
    	String seedDelimiter = config.getConfigValue(JaasConfiguration.AUTHTYPE_SEED_DELIMETER);
    	
    	
    	String passphraseChars = null;
    	String passSeedPositions = null;
    	
    	if (noofSeeds != 0) {
    		StringTokenizer tokenizer = new StringTokenizer(
    				actualPasswordstring, seedDelimiter);
			int i = 0;
			
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()) {
					if (passSeedPositions == null) {
						passSeedPositions = token;
					}
					else {
						passSeedPositions += seedDelimiter + token;
					}
				} else {
					passphraseChars = token;
				}
				i++;
			}
    	}
    	
    	logger.info("ArcLoginModule : SeedPositions from request : " + passSeedPositions);
    	
    	return passSeedPositions;
    }

}
