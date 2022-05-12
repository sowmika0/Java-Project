package com.temenos.arc.security.authenticationserver.ftress;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;

import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Extends AbstractAuthenticator with a password callback 
 * @author jannadani
 *
 */
public class UsernamePasswordAuthenticator extends AbstractAuthenticator implements Authenticatable {

    private final char[] passphrase;

    //TODO is this overload used?
    /**
     * Overload for pre-existing session.
     * @param sharedState
     * @param sessionId
     * @param config
     * @throws ArcAuthenticationServerException
     */
    public UsernamePasswordAuthenticator(final ArcSession sessionId, AuthenticationServerConfiguration config)
            throws ArcAuthenticationServerException {
        super(sessionId, config);
        this.passphrase = null;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * @param sharedState
     * @param nameCallback      provides the username
     * @param passwordCallback  provides the password
     * @param config            provides connection info, etc. 
     * @throws ArcAuthenticationServerException
     */
    public UsernamePasswordAuthenticator(final NameCallback nameCallback,
            final PasswordCallback passwordCallback, 
            AuthenticationServerConfiguration config) throws ArcAuthenticationServerException {
        super(nameCallback, config);
        this.passphrase = passwordCallback.getPassword();
        logger = LoggerFactory.getLogger(this.getClass());
    }

	/**
	 * Performs the U/P authentication by delegating to
	 * {@link FtressHelpers70#authenticate}.
	 */
	public final void authenticate() throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			ArcAuthenticationServerException {
		logger.debug("Entering UP authenticate...");
		try {
			FtressHelpers70.setConfig(config);
			GenericAuthenticationResponse response = FtressHelpers70
					.getInstance()
					.authenticate(
							this.getUserId(),
							passphrase,
							this.config
									.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));

			setSessionId(FtressHelpers70.getInstance().handleResponse(response,
					config));
		} catch (AccountLockedException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Implementation of {@link Authenticatable#logoff()} 
     * that invalidates the <code>ArcSession</code>.
     */
    public final boolean logoff() throws ArcAuthenticationServerException {
        // TODO should we set the session to null after this?
    	logger.info("Into the Logoff Method");
        return FtressHelpers70.getInstance().logoff(getArcSession());
    }    
}
