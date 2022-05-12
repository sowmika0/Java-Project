package com.temenos.arc.security.authenticationserver.ftress;

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
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.jaas.AbstractJaasAuthenticator;
import com.temenos.arc.security.jaas.JaasAuthenticatable;
import com.temenos.arc.security.jaas.JaasConfiguration;

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
    }

    public DeviceAuthenticator(final ArcSession sessionId, final JaasConfiguration config) {
        super(sessionId, config);
        this.oneTimePassword = null;
    }

    public void authenticate() throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException {
        Authenticator authenticator = FtressHelpers.lookupAuthenticator();
        ChannelCode channelCode = new ChannelCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
        SecurityDomain securityDomain = new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
        DeviceAuthenticationRequest authenticationRequest = (DeviceAuthenticationRequest) this.buildRequest();
        AuthenticationResponse authenticationResponse = null;
        try { // TODO SJP 23/11/2006 Sort out crappy, insecure exception handling
            authenticationResponse = authenticator.primaryAuthenticateDevice(channelCode, authenticationRequest, securityDomain);
        } catch (ALSIInvalidException e) {
            throw new ArcAuthenticationServerException(e.toString());
        } catch (DeviceException e) {
            throw new FailedLoginException(e.toString());
        } catch (DeviceAuthenticationException e) {
            throw new FailedLoginException(e.toString());
        } catch (InvalidChannelException e) {
            throw new ArcAuthenticationServerException("Channel incorrectly configured");
        } catch (AuthenticationTierException e) {
            throw new ArcAuthenticationServerException("Authentication Type incorrectly configured");
        } catch (PasswordExpiredException e) {
            throw new AccountExpiredException(e.toString());
        } catch (ObjectNotFoundException e) {
            // TODO SJP 24/11/2006 Possible bug that unknown username causes this exception rather than just returning a response..?
            throw new FailedLoginException(e.toString());
        } catch (SeedingException e) {
            throw new FailedLoginException(e.toString());
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException(e.toString());
        } catch (InternalException e) {
            throw new ArcAuthenticationServerException(e.toString());
        } catch (RemoteException e) {
            throw new ArcAuthenticationServerException(e.toString());
        }
        setSessionId(FtressHelpers.handleResponse(authenticationResponse, config));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.AbstractAuthenticator#buildRequest()
     */
    public final AuthenticationRequest buildRequest() {
        DeviceAuthenticationRequest authenticationRequest = new DeviceAuthenticationRequest();
        AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
        authenticationRequest.setAuthenticationTypeCode(authenticationTypeCode);
        authenticationRequest.setAuthenticateNoSession(false); // TODO SJP 23/11/2006 Confirm what this does!
        authenticationRequest.setUserCode(new UserCode(this.getUserId()));
        authenticationRequest.setOneTimePassword(this.oneTimePassword);
        authenticationRequest.setParameters(new AuthenticationRequestParameter[] {});
        authenticationRequest.setAuthenticationMode(FtressHelpers.to4tressConstant(config, AuthenticationServerConfiguration.DEVICE_MODE));
        return authenticationRequest;
    }
    
    /**
     * Implementation of {@link Authenticatable#logoff()} 
     * that invalidates the <code>ArcSession</code>.
     */
    public final boolean logoff() throws ArcAuthenticationServerException {
        // TODO should we set the session to null after this?
        return FtressHelpers.logoff(getArcSession());
    }
    
    /*
     * (non-Javadoc)
     * TODO get rid of exception specification?
     * @see com.temenos.arc.security.jaas.Authenticatable#refreshSession()
     */
    public final boolean refreshSession() throws ArcAuthenticationServerException {
        return FtressHelpers.refreshSession(getArcSession());
    }
}
