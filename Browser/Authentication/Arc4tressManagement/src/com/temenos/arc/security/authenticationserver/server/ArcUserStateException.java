package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;

/**
 *  Signals that the authentication server state is not what the user management 
 *  component expects.  This will get returned as an error to T24.   
 * @author jannadani
 *
 */
public class ArcUserStateException extends ArcAuthenticationServerException {
    public ArcUserStateException(String msg) {
        super(msg);
    }
    public ArcUserStateException() {
        super();
    }
}
