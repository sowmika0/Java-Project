package com.temenos.arc.security.rsa;

import java.rmi.RemoteException;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import com.aspace.ftress.interfaces.ejb.Authenticator;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationRequest;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationRequestParameter;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationResponse;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces.ftress.DTO.DeviceAuthenticationRequest;
import com.aspace.ftress.interfaces.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.AuthenticationTierException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.DeviceAuthenticationException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.DeviceException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InvalidChannelException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ObjectNotFoundException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.PasswordExpiredException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.SeedingException;
import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.jaas.AbstractJaasAuthenticator;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.JaasAuthenticatable;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * JAAS Authenticator subclass that encapsulates authentication with a 4TRESS device that generates an OTP.  
 * @author jannadani
 *
 */
public final class DeviceAuthenticator extends AbstractJaasAuthenticator implements JaasAuthenticatable {

    private final String oneTimePassword;
    
    public DeviceAuthenticator(final NameCallback nameCallback,
            final PasswordCallback passwordCallback, final JaasConfiguration config) {
        super(nameCallback, config);
        this.oneTimePassword = new String(passwordCallback.getPassword());
    	logger=LoggerFactory.getLogger(this.getClass());
    }

    public DeviceAuthenticator(final ArcSession sessionId, final JaasConfiguration config) {
        super(sessionId, config);
        this.oneTimePassword = null;
    }

    public void authenticate() throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException {
    	// This method does not perform the authentication any more.  If it reaches this stage then it should just return
    	this.setSessionId(new ArcSession());
    	return;
    }

    /**
     * Implementation of {@link Authenticatable#logoff()} 
     * that invalidates the <code>ArcSession</code>.
     */
    public final boolean logoff() throws ArcAuthenticationServerException {
        // TODO should we set the session to null after this?
    	ArcSession session = this.getArcSession();
    	AuthSession authSession = (AuthSession) session.getSessionObject();
    	try {
    		authSession.close();
    	} catch (AuthAgentException e) {
    		throw new ArcAuthenticationServerException("Failed to logout of authentication server");
    	}
    	
        return true;
    }
    
    /*
     * (non-Javadoc)
     * TODO get rid of exception specification?
     * @see com.temenos.arc.security.jaas.Authenticatable#refreshSession()
     */
    public final boolean refreshSession() throws ArcAuthenticationServerException {
    	ArcSession session = this.getArcSession();
    	AuthSession authSession = (AuthSession) session.getSessionObject();
    	int status = 0;
    	try {
    		status = authSession.getAuthStatus();
    	} catch (AuthAgentException e) {
    		throw new ArcAuthenticationServerException("Failed to confirm status Authentication Server.");
    	}
    	if (status == AuthSession.ACCESS_OK ||
    		status == AuthSession.PIN_ACCEPTED) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
