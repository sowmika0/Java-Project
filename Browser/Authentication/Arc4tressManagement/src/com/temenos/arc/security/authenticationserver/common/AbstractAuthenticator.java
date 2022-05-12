package com.temenos.arc.security.authenticationserver.common;

import javax.security.auth.callback.NameCallback;
import com.temenos.t24.commons.logging.Logger;

/** 
 * Superclass for all ARC authenticators.
 * @author jannadani
 *
 */
public abstract class AbstractAuthenticator implements Authenticatable {

    private String userId = null;
    private ArcSession sessionId = null;
    
    protected AuthenticationServerConfiguration config;
    protected Logger logger;

    /**
     * Constructor overload used when a session already exists.
     * This is usually when this authenticator is intended to perform second (or additional)
     * authentication (like a refresh)
     * @param sessionId     reference to the already created session 
     * @param config        configuration to be used for connection info, etc.
     */
    public AbstractAuthenticator(final ArcSession sessionId, AuthenticationServerConfiguration config) {
        this.sessionId = sessionId;
        this.config = config;
    }

    /**
     * Constructor overload used when a session needs to be created.
     * This is usually when this authenticator is the first one.
     * @param nameCallback  this object provides the userId for the authentication   
     * @param config        configuration to be used for connection info, etc.
     */
    public AbstractAuthenticator(final NameCallback nameCallback, AuthenticationServerConfiguration config) {
//        this.sharedState = sharedState;
        this.userId = nameCallback.getName();
        this.config = config;
    }

    public final ArcSession getArcSession() {
        return this.sessionId;
    }

    protected final String getUserId() {
        return userId;
    }

    protected final void setSessionId(ArcSession sessionId) {
        this.sessionId = sessionId;
    }
}
