package com.temenos.arc.security.jaas;

import java.util.Set;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.authenticationserver.ftress.FtressLoginModule;



import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;

/**
 * Delegate object to implement the commit phase of the JAAS process.
 * @author jannadani
 *
 */
public interface Committable {

    boolean commit(LoginModule loginModule, Set principals, Set credentials) throws ArcAuthenticationServerException,
            FailedLoginException;
   
}
