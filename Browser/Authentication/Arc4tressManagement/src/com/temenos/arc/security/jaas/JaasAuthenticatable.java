package com.temenos.arc.security.jaas;

import java.security.Principal;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;

/**
 * Adds methods that to support the authentication part of ARC JAAS login modules. 
 * @author jannadani
 *
 */
public interface JaasAuthenticatable extends Authenticatable {
	
    Principal getRolePrincipal();

    Principal getUserPrincipal();

//TODO remove ArcAuth...Exception from throws
    /**
     * &quot;Touches&quot; the session on the authentication server.
     * 
     * @return <tt>true</tt> if the session was successfully refreshed.
     * @throws ArcAuthenticationServerException 
     * @throws LoginModuleConfigurationException
     */    
    boolean refreshSession() throws ArcAuthenticationServerException,
            LoginModuleException;
	

}
