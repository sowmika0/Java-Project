package com.temenos.arc.security.jaas;

import java.security.Principal;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;

/**
 * Superclass for all JAAS compliant ARC authenticator classes. Provides common implementations
 * so subclasses only need to implement {@link Authenticatable#authenticate()}.
 * @author jannadani
 *
 */
public abstract class AbstractJaasAuthenticator extends AbstractAuthenticator
		implements JaasAuthenticatable {
	

	public AbstractJaasAuthenticator(final ArcSession sessionId, JaasConfiguration config) {
		super(sessionId, config);
    }

    public AbstractJaasAuthenticator(final NameCallback nameCallback, JaasConfiguration config) {
		super(nameCallback, config);
    }


	public abstract void authenticate() throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException,
    ArcAuthenticationServerException;

	/*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.Authenticatable#getRolePricipal()
     */
    public final Principal getRolePrincipal() {
        // TODO SJP 24/11/2006 Maybe grab the role from 4TRESS
        return new ArcRolePrincipal("arcuser");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.Authenticatable#getUserPrincipal()
     */
    public final Principal getUserPrincipal() {
        return new ArcUserPrincipal(this.getUserId());
    }
}
