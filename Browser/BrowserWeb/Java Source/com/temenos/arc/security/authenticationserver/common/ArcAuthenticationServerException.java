package com.temenos.arc.security.authenticationserver.common;


// TODO Rename to ArcAuthenticationException
/**
 * General exception class for the ARC authentication packages.
 * Comprises mapped authentication server exceptions as well as
 * internal exceptions in the arc.security packages.     
 */
public class ArcAuthenticationServerException extends RuntimeException {

    public ArcAuthenticationServerException() {
        super();
    }
 
    public ArcAuthenticationServerException(String msg) {
        super(msg);
    }
}
