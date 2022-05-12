package com.temenos.arc.security.authenticationserver.common;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;

// TODO Remove ArcAuthenticationServerException from exception specifications!

/**
 * Specifies a single factor authenticator in ARC.  Multiple factor authentication 
 * should be implemented with multiple <code>Authenticatable</code> instances.   
 * @author jannadani
 */
public interface Authenticatable {

    /**
     *  Attempts to authenticate using previously initialised data.
     *  After this method has run, the object should be associated with an {@link ArcSession} instance
     * (i.e. this method should create such an instance unless it was initialised with an existing one)  
     * @throws {@link ArcAuthenticationServerException}
     * @throws {@link FailedLoginException}
     * @throws {@link AccountExpiredException}
     * @throws {@link ArcAuthenticationServerException}
     */
    void authenticate() throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException,
    							ArcAuthenticationServerException;

    
    /** Returns the associated <code>ArcSession</code> instance.  
     *  @return the associated <code>ArcSession</code>, not <code>null</code> if this is called after {@link #authenticate()} 
     */
    ArcSession getArcSession();

    /**  Performs any required logoff for this authenticator. 
     * @return <code>true</code> if the logoff was successful.  
     * @throws ArcAuthenticationServerException
     */
    boolean logoff() throws ArcAuthenticationServerException;
}
