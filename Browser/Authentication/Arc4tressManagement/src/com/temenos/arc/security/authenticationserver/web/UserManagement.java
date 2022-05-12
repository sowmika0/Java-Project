package com.temenos.arc.security.authenticationserver.web;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.aspace.ftress.interfaces70.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces70.ejb.DeviceManager;
import com.aspace.ftress.interfaces70.ejb.UserManager;
import com.aspace.ftress.interfaces70.ftress.DTO.ALSI;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces70.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces70.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.User;
import com.aspace.ftress.interfaces70.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces70.ftress.DTO.device.Authenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.device.Device;
import com.aspace.ftress.interfaces70.ftress.DTO.device.DeviceSearchCriteria;
import com.aspace.ftress.interfaces70.ftress.DTO.device.DeviceSearchResults;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.NoFunctionPrivilegeException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ObjectNotFoundException;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.UserSecret;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.arc.security.authenticationserver.ftress.ServiceLocator70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This class is a facade into the minimal subset of the Auth server management API necessary
 * for the web tier.  This is primarily for binding a user to a token, but may in
 * future encompass re-setting PINs, memorable data, etc.
 * @author vraghavan
 *
 */
public class UserManagement {
	private AuthenticationServerConfiguration config;
	private Logger logger;
	private static ServiceLocator70 serviceLocator70Instance = null;
	ChannelCode channelCode = getChannel();
	
	private ChannelCode getChannel() {
		ChannelCode chCode = new ChannelCode();
		chCode.setCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		return chCode;
	}
	SecurityDomain domain = new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
    /**
     * Overload used in testing.
     * @param config
     */
    public UserManagement(AuthenticationServerConfiguration config) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.config = config;
    }
    
    

    /**
     * overload that uses the default configuration.
     */
	public UserManagement() {
        logger = LoggerFactory.getLogger(this.getClass());
		config = AuthenticationServerConfiguration.getStatic();
	}

	
	private static ServiceLocator70 getserviceLocator70Instance() {
		if (serviceLocator70Instance == null) {
			serviceLocator70Instance = new ServiceLocator70();
		}
		return serviceLocator70Instance;
	}

    /**
     *  Sets up the device authenticator for the user.
     * @param deviceSerialNumber    the serial number (NOT id) of the device.
     * If the device already has an authenticator against any user then the call will fail.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has privileges to setup and delete authenticators for the user on the server.
     * @param userId                the ARC user id to which we are trying to bind this device
     * @param validityPeriod        the validity period of this device [authenticator] in years
     */
    public void bindUser(String deviceSerialNumber, ArcSession sessionId, String userId, int validityPeriod) {
    	// Validate the serial number
//    	if (!deviceSerialNumber.matches("[0-9]*")) {
//    		throw new ArcAuthenticationServerException("Error binding. Token serial number must be numeric");
//    	}
    	// Validate the user id
    	//if (!userId.matches("[0-9A-Za-z]*")) {
    		//throw new ArcAuthenticationServerException("Error binding. User Id must be alphanumeric");
    	//}
		// authenticationTypeCode
		//AuthenticationTypeCode typeCode = new AuthenticationTypeCode("AT_AIOTP");
    	AuthenticationTypeCode typeCode = new AuthenticationTypeCode(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE),false);
    	

		// device search criteria (i.e. serial number)
		DeviceSearchCriteria criteria = new DeviceSearchCriteria();
		criteria.setSerialNumber(deviceSerialNumber);

		// Create the user code
		UserCode userCode = new UserCode(userId);

		// Create the DeviceAuthenticator
		
				
		//DeviceAuthenticator authenticator = new DeviceAuthenticator();
		
		
		Authenticator authenticator = new Authenticator();
        	
		authenticator.setAuthenticationTypeCode(typeCode);
		authenticator.setUserCode(userCode);
		authenticator.setStatus(Authenticator.ENABLED);
		authenticator.setValidFrom(Calendar.getInstance());
		Calendar validTo = Calendar.getInstance();
        //TODO is years right?
		validTo.add(Calendar.YEAR, validityPeriod);
		authenticator.setValidTo(validTo);
		//authenticator.setDeviceId(null);
		authenticator.setStatistics(null);
		// call AuthenticatorManager

		// Get the home interface
		
	
		AuthenticatorManager authenticatorManager= getserviceLocator70Instance().lookupAuthenticatorManager();
		try {
			
			authenticatorManager.createAuthenticator(getALSIValue(sessionId.toString()), channelCode, authenticator, domain);
			
				
			
        } catch (InvalidParameterException e) {
        	// TODO replace with proper logging
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (NoFunctionPrivilegeException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
            // This really should not be thrown by 4TRESS
        	// TODO Report to ActivIdentity
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }


    }
    
	/**
	 * @param sessionId
	 * @return
	 */
	private ALSI getALSIValue(String sessionId) {
		ALSI alsi = new ALSI();
		alsi.setAlsi(sessionId.toString());
		return alsi;
	}

    /**
     *  Resets the pin authenticator for this ARC user, by calling {@link #deletePin(ArcSession, String)}
     *   and {@link #addPin(ArcSession, String, String, int)}.
     *  Currently only used in testing.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has privileges to setup and delete authenticators for the user on the server.
     * @param pin                   the new PIN
     * @param userId                the ARC user id whose PIN we are resetting
     * @param validityPeriod        the validity period of this PIN [authenticator] in years
     */
    public void resetPin(ArcSession sessionId, String pin, String userId, int validityPeriod) {
        deletePin(sessionId, userId);

        addPin(sessionId, pin, userId, validityPeriod);

    }

    /**
     * Creates a pin authenticator for the specified ARC user.
     * Will throw an exception if a pin authenticator already exists for this ARC user.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has privileges to add authenticators for the user on the server.
     * @param pin                   the PIN to be set
     * @param userId                the ARC user id whose PIN is being set
     * @param validityPeriod        the validity period of this PIN [authenticator] in years
     */
    public void addPin(ArcSession sessionId, String pin, String userId, int validityPeriod) {
        UserCode userCode = new UserCode(userId);
        // Get the home interface
       
        AuthenticatorManager authenticatorManager= getserviceLocator70Instance().lookupAuthenticatorManager();
        //AuthenticationTypeCode typeCode = new AuthenticationTypeCode("AT_CUSTPIN");
        AuthenticationTypeCode typeCode = new AuthenticationTypeCode();
        typeCode.setCode(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN));
        
        // Create the UPAuthenticator
        UPAuthenticator upAuth = new UPAuthenticator();
        upAuth.setAuthenticationTypeCode(typeCode);
        upAuth.setPassword(pin);
        upAuth.setUserCode(userCode);
        upAuth.setUsername(userId);
        upAuth.setStatus(Authenticator.ENABLED);
        upAuth.setValidFrom(Calendar.getInstance());
		Calendar validTo = Calendar.getInstance();
		validTo.add(Calendar.YEAR, validityPeriod);
		upAuth.setValidTo(validTo);
		upAuth.setStatistics(null);
		try {
			authenticatorManager.createUPAuthenticator(getALSIValue(sessionId.toString()),
														channelCode,
														upAuth,
														new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
        } catch (InvalidParameterException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (NoFunctionPrivilegeException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
            // This really should not be thrown by 4TRESS
        	// TODO Report to ActivIdentity
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }
    }

    /**
     * Deletes the pin authenticator for this ARC user.
     * Will throw an exception if such an authenticator does not exist.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has privileges to delete authenticators for the user on the server.
     * @param userId                the ARC user id whose PIN is being deleted
     */
    public void deletePin(ArcSession sessionId, String userId) {
        UserCode userCode = new UserCode(userId);
        // Get the home interface
       
        AuthenticatorManager authenticatorManager= getserviceLocator70Instance().lookupAuthenticatorManager();
        //AuthenticationTypeCode typeCode = new AuthenticationTypeCode("AT_CUSTPIN");
        AuthenticationTypeCode typeCode = new AuthenticationTypeCode();
        typeCode.setCode(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN));
                
		try {
			authenticatorManager.deleteUPAuthenticator(getALSIValue(sessionId.toString()),
														channelCode,
														userCode,
														typeCode,
														new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
        } catch (InvalidParameterException e) {
        	// TODO replace with proper logging
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (NoFunctionPrivilegeException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
            // This really should not be thrown by 4TRESS
        	// TODO Report to ActivIdentity
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }
    }

    /**
     * Deletes the "one shot" memorable word authenticator used in binding the device.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has privileges to delete authenticators for the user on the server.
     * @param userId                the ARC user id whose memorable word is being deleted
     */
    public void deleteMemorableData(ArcSession sessionId, String userId) {

    	String deleteMemword = config.getConfigValue(AuthenticationServerConfiguration.DELETE_MEMWORD_AUTHENTICATOR);
    	if(deleteMemword.equals("false")){
    		logger.info("MemWord authenticator will not be deleted.");
    		return;    		    		
    	}
    	UserCode userCode = new UserCode(userId);
        // Get the home interface
        
        AuthenticatorManager authenticatorManager=getserviceLocator70Instance().lookupAuthenticatorManager();
        //AuthenticationTypeCode typeCode = new AuthenticationTypeCode("AT_CUSTMW");
        AuthenticationTypeCode typeCode = new AuthenticationTypeCode();
        typeCode.setCode(config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
               
		try {
			authenticatorManager.deleteUPAuthenticator(getALSIValue(sessionId.toString()),
														channelCode,
														userCode,
														typeCode,
														new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
        } catch (InvalidParameterException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (NoFunctionPrivilegeException e) {
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
            // This really should not be thrown by 4TRESS
        	// TODO Report to ActivIdentity
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        }
    }

    public void setSecret(String arcUserId, UserSecret[] secret) {
    	// TODO once MDAuthenticator work takes place
    }

    /**
     * Pre-check to see if a userId is known by the auth server.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has query users on the server.
     * @param userId                the ARC user id being checked
     * @return true if the userId passed in exists as a UserCode in the auth server
     */
    public boolean isUserIdValid(ArcSession session, String userId) {
        if (config == null) {
            throw new ArcAuthenticationServerException("config not set.");
        }
        User user = null;
        
        UserManager userManager = getserviceLocator70Instance().lookupUserManager();
        try {
            user = userManager.getUser(getALSIValue(session.toString()),
            						channelCode,
                                new UserCode(userId),
                                new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
            if (null != user) {
                return true;
            }
        } catch (InvalidParameterException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
        	logger.error(e);
        	throw new ArcAuthenticationServerException(e.getMessage());
        } catch (NoFunctionPrivilegeException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
          	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        }
        return false;
    }
    /**
     * Pre-check to see is a device is known by the auth server.
     * @param sessionId             the <code>ArcSession</code> that was used to login.
     * This must be a login that has query users on the server.
     * @param deviceSerialNum       the serial number (NOT id) of the device being checked.
     * @return true if the userId passed in exists as a UserCode in the auth server
     */
    public boolean isDeviceSerValid(ArcSession session, String deviceSerialNum) {
        if (config == null) {
            throw new ArcAuthenticationServerException("config not set.");
        }
        
        DeviceManager deviceManager = getserviceLocator70Instance().lookupDeviceManager();
        DeviceSearchCriteria criteria = new DeviceSearchCriteria();
        criteria.setSerialNumber(deviceSerialNum);
        Device[] devices = null;
        try {
            DeviceSearchResults results = deviceManager.searchDevices(getALSIValue(session.toString()),
                                channelCode,
                                criteria,
                                new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
            if (results != null) {
                devices = results.getDevices();
            }
        } catch (InvalidParameterException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (ALSIInvalidException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        } catch (ObjectNotFoundException e) {
        	logger.error(e);
        } catch (NoFunctionPrivilegeException e) {
        	logger.error(e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (InternalException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
        	logger.error(e.getMessage(), e);
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        }
        if (devices != null && devices.length == 1) {
            return true;
        }
        return false;
    }

    /**
     * Login in, based on config, with an login that has privileges to perform pre-checks.
     * Because the login details are in config, it should not have absolute minimum privileges.
     *
     * IS THIS REDUNDANT?
     *
     * @param userId
     * @param memData
     * @return a logged in authenticator based on the config
     */
    public AbstractAuthenticator getAuthenticatorForPreChecks(String userId, String memData) {
        if (config == null) {
            throw new ArcAuthenticationServerException("config not set.");
        }
        // login with encrypted memdata
        return FtressHelpers70.getInstance().loginWithUPAuthenticator(userId, memData, config);
    }
    /**
     * Login in as the user with their one shot memorable word.
     * @param userId
     * @param memData
     * @return
     */
    public AbstractAuthenticator loginWithMemData(String userId, String memData) {
        // TODO replace with proper memdata stuff
        return getAuthenticatorForPreChecks(userId, memData);
    }
}
