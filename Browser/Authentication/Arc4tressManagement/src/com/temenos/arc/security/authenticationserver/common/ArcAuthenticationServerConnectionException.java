package com.temenos.arc.security.authenticationserver.common;

/**
 * Indicates a problem communicating with the authentication server 
 * @author jannadani
 *
 */
public class ArcAuthenticationServerConnectionException extends ArcAuthenticationServerException {

	public ArcAuthenticationServerConnectionException() {
        super();
    }

	public ArcAuthenticationServerConnectionException(String msg) {
        super(msg);
    }
}