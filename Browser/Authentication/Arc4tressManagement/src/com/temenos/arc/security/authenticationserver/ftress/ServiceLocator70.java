package com.temenos.arc.security.authenticationserver.ftress;

import java.rmi.RemoteException;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;

/* 4TRESS 7.0 Interface namespace */
import com.aspace.ftress.interfaces70.ejb.Authenticator;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorHome;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorManagerHome;
import com.aspace.ftress.interfaces70.ejb.CredentialManager;
import com.aspace.ftress.interfaces70.ejb.CredentialManagerHome;
import com.aspace.ftress.interfaces70.ejb.DeviceManager;
import com.aspace.ftress.interfaces70.ejb.DeviceManagerHome;
import com.aspace.ftress.interfaces70.ejb.UserManager;
import com.aspace.ftress.interfaces70.ejb.UserManagerHome;
import com.aspace.ftress.interfaces70.ejb.Auditor;
import com.aspace.ftress.interfaces70.ejb.AuditorHome;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.common.EjbHomeFactory;
import com.temenos.arc.security.authenticationserver.common.FtressSoapFactory;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Service locator class, used as a central place to lookup the 4TRESS7.0 EJBs
 * @author pradeep
 *
 */
public class ServiceLocator70 {
	private static String AUTHENTICATOR_JNDI_NAME = "ejb/4TRESSAuthenticator70";
	private static String AUTHENTICATOR_MANAGER_JNDI_NAME = "ejb/4TRESSAuthenticatorManager70";
	private static String USER_MANAGER_JNDI_NAME = "ejb/4TRESSUserManager70";
    private static String DEVICE_MANAGER_JNDI_NAME = "ejb/4TRESSDeviceManager70";
    private static String CREDENTIAL_MANAGER_JNDI_NAME = "ejb/4TRESSCredentialManager70";
    private static String AUDITOR_JNDI_NAME="ejb/4TRESSAuditor70";
    private static Logger logger = LoggerFactory.getLogger(ServiceLocator70.class);
    private static AuthenticationServerConfiguration config;
    private static FtressSoapFactory ftressSoapFactoryInstance = null;
    
    private static String authServerCommunicationType = "";
    private static final String SOAP_COMMUNICATION_TYPE = "SOAP";
    private static final String RMI_COMMUNICATION_TYPE = "RMI";
    
    public ServiceLocator70() {
    	config = getConfig(0);
    	authServerCommunicationType = config.getConfigValue(AuthenticationServerConfiguration.AUTH_SERVER_COMMUNICATION_TYPE);
    	if (authServerCommunicationType == null) {
    		authServerCommunicationType = SOAP_COMMUNICATION_TYPE;
    	}
    	logger.info("AuthenticationServer Communication type is : " + authServerCommunicationType);
    	
    }

    private FtressSoapFactory getftressSoapFactoryInstance() {
    	if (ftressSoapFactoryInstance == null) {
    		ftressSoapFactoryInstance = new FtressSoapFactory(config);
    	}
    	return ftressSoapFactoryInstance;
    }
    
    /* 4TRESS 7.0 lookup methods */
    public final Authenticator lookupAuthenticator() throws ArcAuthenticationServerConnectionException {
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupAuthenticator();
    		} catch (Exception e) {
    			logger.debug("failed to get authenticator", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
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
    }

    public final AuthenticatorManager lookupAuthenticatorManager() throws ArcAuthenticationServerConnectionException {
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupAuthenticatorManager();
    		} catch (Exception e) {
    			logger.debug("failed to get authenticator", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
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
       
    }

    public final CredentialManager lookupCredentialManager() throws ArcAuthenticationServerConnectionException {
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupCredentialManager();
    		} catch (Exception e) {
    			logger.debug("failed to get Credential manager", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
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
    }

    public final UserManager lookupUserManager() throws ArcAuthenticationServerConnectionException {
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupUserManager();
    		} catch (Exception e) {
    			logger.debug("failed to get UserManager", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
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
    }

    public final DeviceManager lookupDeviceManager() throws ArcAuthenticationServerConnectionException {
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupDeviceManager();
    		} catch (Exception e) {
    			logger.debug("failed to get DeviceManager", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
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
    }
    
    public final Auditor lookupAuditor()throws ArcAuthenticationServerConnectionException{
    	if (SOAP_COMMUNICATION_TYPE.equals(authServerCommunicationType)) {
    		try {
    			return getftressSoapFactoryInstance().lookupAuditor();
    		} catch (Exception e) {
    			logger.debug("failed to get Auditor", e);
    			throw new ArcAuthenticationServerConnectionException(e.toString());
    		}
    	} else {
        	AuditorHome home = (AuditorHome)EjbHomeFactory.lookup(AUDITOR_JNDI_NAME,AuditorHome.class);
        	try{
        	    return home.create();
        	}catch(EJBException e){
        		logger.debug("failed to get Auditor manager home.", e);
        		throw new ArcAuthenticationServerConnectionException(e.toString());
        	}catch(RemoteException e){
        		logger.debug("failed to get Auditor manager home.", e);
                throw new ArcAuthenticationServerConnectionException(e.toString());
        	}catch(CreateException e){
        		logger.debug("failed to get Auditor manager home.", e);
                throw new ArcAuthenticationServerConnectionException(e.toString());
        	}
    	}
    }
    
    public static AuthenticationServerConfiguration getConfig(int section) {
		//config file path should be specified in either the below configurations:
		// On WebLayer config file path is specified under the property - java.security.auth.login.config
		// On T24Server (server.config) config file path is specified under the property - ARC_CONFIG_PATH
        String configFile = System.getProperty("java.security.auth.login.config");
        if (configFile == null) {
        	configFile = System.getProperty("ARC_CONFIG_PATH");
        }
        //If ARC_CONFIG_APP_NAME property is not specified then appName value is hard coded to default value ARC
        String appName = System.getProperty("ARC_CONFIG_APP_NAME");
        if (appName == null) {
        	appName = "ARC";
        }
        
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		Map[] configMap = parser.parse();
		return new AuthenticationServerConfiguration(configMap[section]);
    }
    
}
