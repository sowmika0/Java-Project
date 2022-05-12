package com.temenos.arc.security.authenticationserver.ftress;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import com.aspace.ftress.interfaces.ejb.Authenticator;
import com.aspace.ftress.interfaces.ejb.AuthenticatorHome;
import com.aspace.ftress.interfaces.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces.ejb.AuthenticatorManagerHome;
import com.aspace.ftress.interfaces.ejb.CredentialManager;
import com.aspace.ftress.interfaces.ejb.CredentialManagerHome;
import com.aspace.ftress.interfaces.ejb.DeviceManager;
import com.aspace.ftress.interfaces.ejb.DeviceManagerHome;
import com.aspace.ftress.interfaces.ejb.UserManager;
import com.aspace.ftress.interfaces.ejb.UserManagerHome;
import com.aspace.ftress.interfaces.ejb.Auditor;
import com.aspace.ftress.interfaces.ejb.AuditorHome;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.EjbHomeFactory;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Service locator class, used as a central place to lookup the 4TRESS EJBs
 * @author vraghavan
 *
 */
public class ServiceLocator {
	private static String AUTHENTICATOR_JNDI_NAME = "ejb/4TRESSAuthenticator";
	private static String AUTHENTICATOR_MANAGER_JNDI_NAME = "ejb/4TRESSAuthenticatorManager";
	private static String USER_MANAGER_JNDI_NAME = "ejb/4TRESSUserManager";
    private static String DEVICE_MANAGER_JNDI_NAME = "ejb/4TRESSDeviceManager";
    private static String CREDENTIAL_MANAGER_JNDI_NAME = "ejb/4TRESSCredentialManager";
    private static String AUDITOR_JNDI_NAME="ejb/4TRESSAuditor";
    private static Logger logger = LoggerFactory.getLogger(ServiceLocator.class);
	
    public ServiceLocator() {
    }

    public final Authenticator lookupAuthenticator() throws ArcAuthenticationServerConnectionException {
        AuthenticatorHome home = (AuthenticatorHome) EjbHomeFactory.lookup(AUTHENTICATOR_JNDI_NAME, AuthenticatorHome.class);
        try {
            return home.create();
        } catch (EJBException e) {
        	logger.debug("failed to get authenticator home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (RemoteException e) {
        	logger.debug("failed to get authenticator home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (CreateException e) {
        	logger.debug("failed to get authenticator home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        }
    }

    public final AuthenticatorManager lookupAuthenticatorManager() throws ArcAuthenticationServerConnectionException {
        AuthenticatorManagerHome home = (AuthenticatorManagerHome) EjbHomeFactory.lookup(AUTHENTICATOR_MANAGER_JNDI_NAME, AuthenticatorManagerHome.class);
        try {
            return home.create();
        } catch (EJBException e) {
        	logger.debug("failed to get authenticator manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (RemoteException e) {
        	logger.debug("failed to get authenticator manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (CreateException e) {
        	logger.debug("failed to get authenticator manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        }
    }

    public final CredentialManager lookupCredentialManager() throws ArcAuthenticationServerConnectionException {
        CredentialManagerHome home = (CredentialManagerHome) EjbHomeFactory.lookup(CREDENTIAL_MANAGER_JNDI_NAME, CredentialManagerHome.class);
        try {
            return home.create();
        } catch (EJBException e) {
        	logger.debug("failed to get credential manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (RemoteException e) {
        	logger.debug("failed to get credential manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (CreateException e) {
        	logger.debug("failed to get credential manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        }
    }

    public final UserManager lookupUserManager() throws ArcAuthenticationServerConnectionException {
        UserManagerHome home = (UserManagerHome) EjbHomeFactory.lookup(USER_MANAGER_JNDI_NAME, UserManagerHome.class);
        try {
            return home.create();
        } catch (EJBException e) {
        	logger.debug("failed to get user manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (RemoteException e) {
        	logger.debug("failed to get user manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (CreateException e) {
        	logger.debug("failed to get user manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        }
    }

    public final DeviceManager lookupDeviceManager() throws ArcAuthenticationServerConnectionException {
        DeviceManagerHome home = (DeviceManagerHome) EjbHomeFactory.lookup(DEVICE_MANAGER_JNDI_NAME, DeviceManagerHome.class);
        try {
            return home.create();
        } catch (EJBException e) {
        	logger.debug("failed to get device manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (RemoteException e) {
        	logger.debug("failed to get device manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        } catch (CreateException e) {
        	logger.debug("failed to get device manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
        }
    }
    
    public final Auditor lookupAuditor()throws ArcAuthenticationServerConnectionException{
    	AuditorHome home = (AuditorHome)EjbHomeFactory.lookup(AUDITOR_JNDI_NAME,AuditorHome.class);
    	try{
    	    return home.create();	
    	}catch(EJBException e){
    		logger.debug("failed to get device manager home.", e);
    		throw new ArcAuthenticationServerConnectionException(e.toString());
    	}catch(RemoteException e){
    		logger.debug("failed to get device manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
    	}catch(CreateException e){
    		logger.debug("failed to get device manager home.", e);
            throw new ArcAuthenticationServerConnectionException(e.toString());
    	}
    	
    }

}
