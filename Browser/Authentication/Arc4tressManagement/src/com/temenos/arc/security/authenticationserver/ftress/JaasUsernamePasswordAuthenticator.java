package com.temenos.arc.security.authenticationserver.ftress;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationResponse;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.jaas.AbstractJaasAuthenticator;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Differs from {@link UsernamePasswordAuthenticator} only in that it uses common JAAS specific code 
 * in {@link AbstractJaasAuthenticator}
 * @author jannadani
 *
 */
public class JaasUsernamePasswordAuthenticator 
		extends AbstractJaasAuthenticator {

	private char[] passphrase;

    public JaasUsernamePasswordAuthenticator(final ArcSession sessionId, JaasConfiguration config)
            throws ArcAuthenticationServerException {
        super(sessionId, config);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public JaasUsernamePasswordAuthenticator(final NameCallback nameCallback,
            final PasswordCallback passwordCallback, 
            JaasConfiguration config) throws ArcAuthenticationServerException {
        super(nameCallback, config);
        this.passphrase = passwordCallback.getPassword();
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public final void authenticate() throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException,
    					ArcAuthenticationServerException {
    	
    	FtressHelpers.setConfig(config);
        AuthenticationResponse response = FtressHelpers.authenticate(this.getUserId(), passphrase);
        setSessionId(FtressHelpers.handleResponse(response, config));
    }
    
    /**
     * Implementation of {@link Authenticatable#logoff()}  
     * that invalidates the <code>ArcSession</code>.
     */
    public final boolean logoff() throws ArcAuthenticationServerException {
        // TODO should we set the session to null after this?
        return FtressHelpers.logoff(getArcSession());
    }
    
    /*
     * (non-Javadoc)
     * TODO get rid of exception specification?
     * @see com.temenos.arc.security.jaas.Authenticatable#refreshSession()
     */
    public final boolean refreshSession() throws ArcAuthenticationServerException {
        return FtressHelpers.refreshSession(getArcSession());
    }    
}
