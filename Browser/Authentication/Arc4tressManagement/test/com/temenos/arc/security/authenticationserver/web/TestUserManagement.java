package com.temenos.arc.security.authenticationserver.web;

import java.rmi.RemoteException;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.FailedLoginException;
import junit.framework.TestCase;
import com.aspace.ftress.interfaces.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces.ftress.DTO.ALSI;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.NoFunctionPrivilegeException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ObjectNotFoundException;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.ftress.DeviceAuthenticatorTest;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers;
import com.temenos.arc.security.authenticationserver.ftress.ServiceLocator;
import com.temenos.arc.security.jaas.AuthenticationByDeviceConfigurationFactory;
import com.temenos.arc.security.jaas.JaasConfiguration;

public class TestUserManagement extends TestCase {

    protected AuthenticationServerConfiguration config;
    protected AuthenticationServerConfiguration systemConfig;
    protected AuthenticationServerConfiguration upConfig;
	private static final String userId = "786786786";
	// String is "memData", but we need to pass in 2 characters
	private static final String errorMemData = "1:4:5:mDa";
	private static final String memData = "1:4:mD";
    protected UserManagement toTest;
    
    
    public TestUserManagement() {
        setConfiguration();
    }
    
	protected void setUp() throws Exception {
		super.setUp(); 
	}

	public void testBindUser() {
		// Call bindUser - this method is called when a new user needs to bind
		// their token to their user.  The user should already be logged in
		// using a UPAuthenticator.
		String deviceSerialNumber = addDeviceToUser(userId);
		
		// check that device has been bound to user. by logging into the user
		// using the device.
		DeviceAuthenticatorTest deviceTest = new DeviceAuthenticatorTest();
		AuthenticationServerConfiguration config = AuthenticationByDeviceConfigurationFactory.getInstance().getConfiguration();
		if (config instanceof JaasConfiguration) {
			deviceTest.setConfig((JaasConfiguration)config);
		} else {
			fail("Invalid Configuration");
		}
		deviceTest.setOtpHelper();
		deviceTest.doLogin();
		
		// Now delete the authenticator
		unBind(deviceSerialNumber, userId );
	}

	public String addDeviceToUser(String userId) {
		//  Log in
		AbstractAuthenticator auth = this.initialiseUPAuthenticator(userId, "125412");
    	try {
    		auth.authenticate();
    	} catch(FailedLoginException e) {
    		fail(e.getMessage());
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	} catch (AccountExpiredException e) {
    		fail(e.getMessage());
    	}		

		
		String deviceSerialNumber = "0";
		toTest = new UserManagement();
		toTest.bindUser(deviceSerialNumber, auth.getArcSession(), userId, 100);
		return deviceSerialNumber;
	}

	public void testBindUserNotLoggedIn() {
		// Call bindUser - this method is called when a new user needs to bind
		// their token to their user.  The user should already be logged in
		// using a UPAuthenticator.
		
		String deviceSerialNumber = "0";
		// create an arc session that is not logged in
		ArcSession sessionId = new ArcSession(userId);
		toTest = new UserManagement();
		try {
			toTest.bindUser(deviceSerialNumber, sessionId, userId, 100);
            fail();
		} catch (ArcAuthenticationServerConnectionException e) {
			// We do not want a connection exception to be thrown out
			// as this is the successful case
			System.out.println(e.getMessage());
		}
		
	}

	public void testResetPin() {
		// A logged in user wants to change their PIN
		// Get session
		String oldPin = "125412";
		String newPin = "236523";
		AbstractAuthenticator auth = this.initialiseUPAuthenticator(userId, oldPin);
    	try {
    		auth.authenticate();
    	} catch(FailedLoginException e) {
    		fail(e.getMessage());
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	} catch (AccountExpiredException e) {
    		fail(e.getMessage());
    	}		
    	
    	// Now set the PIN
    	toTest = new UserManagement();
    	toTest.resetPin(auth.getArcSession(), newPin, userId, 100);
    	
    	
    	// test that the PIN has been set by logging in with the new PIN
    	auth.logoff();
    	
		AbstractAuthenticator auth2 = this.initialiseUPAuthenticator(userId, newPin);
    	try {
    		auth2.authenticate();
    	} catch(FailedLoginException e) {
    		fail(e.getMessage());
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	} catch (AccountExpiredException e) {
    		fail(e.getMessage());
    	}		
    	
		// finally put the PIN back
    	toTest.resetPin(auth2.getArcSession(), oldPin, userId, 100);
    	auth2.logoff();
	}

/*	public void testSetSecret() {
		// A logged in user wants to change their user secret
		
		// call: wait until meeting with ActivIdentity for this
		fail("Not yet implemented");
	}*/
    
    public void testUserIdIsValid() {
        toTest = new UserManagement(upConfig);
        try {
        	toTest.getAuthenticatorForPreChecks(userId, errorMemData);
        	fail("Failed to detect invalid memData");
        } catch (ArcAuthenticationServerException e){
        	// Do nothing
        }
        AbstractAuthenticator auth = toTest.getAuthenticatorForPreChecks(userId, memData);
        assertNotNull(auth.getArcSession());
        assertNotNull(auth.getArcSession().toString());
        assertTrue(toTest.isUserIdValid(auth.getArcSession(), userId));
        assertFalse(toTest.isUserIdValid(auth.getArcSession(), "garbageId"));
        auth.logoff();
    }

    /**
     * requires that 0751086745 is a valid device serial number in the auth server 
     *
     */
    public void testDeviceSerIsValid() {
        toTest = new UserManagement(upConfig);
        AbstractAuthenticator auth = toTest.getAuthenticatorForPreChecks(userId, memData);
        assertNotNull(auth.getArcSession());
        assertNotNull(auth.getArcSession().toString());
        assertTrue(toTest.isDeviceSerValid(auth.getArcSession(), "0751086745"));
        assertFalse(toTest.isDeviceSerValid(auth.getArcSession(), "garbageSer"));
        auth.logoff();
    }

    private AbstractAuthenticator initialiseUPAuthenticator(String name, String password) {
    	return FtressHelpers.loginWithUPAuthenticator(name, password, config);
    }

	public void setConfiguration() {
		config = AuthenticationByPinConfigurationFactory.getInstance().getConfiguration();
        System.setProperty(ConfigurationFileParser.FILE_APP_NAME_KEY, "ARC");
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/Arc4tressManagement/conf/static.config");
		systemConfig = AuthenticationServerConfiguration.getStatic();
        System.setProperty(ConfigurationFileParser.FILE_PATH_KEY, "D:/work/temenos/Browser/Codelines/MAIN/Browser/Authentication/ArcBinding/conf/staticup.config");
		upConfig = AuthenticationServerConfiguration.getStatic();		
	}

	private AbstractAuthenticator initialiseSystemUPAuthenticator(String name, String password) {
        return FtressHelpers.loginWithUPAuthenticator(name, password, systemConfig); 
    }

	public void unBind(String deviceSerialNumber, String userId) {
		AbstractAuthenticator auth = this.initialiseSystemUPAuthenticator("T24SYSLOG", "g0rmenghast");
    	try {
    		auth.authenticate();
    	} catch(FailedLoginException e) {
    		fail(e.getMessage());
    	} catch (ArcAuthenticationServerException e) {
    		fail(e.getMessage());
    	} catch (AccountExpiredException e) {
    		fail(e.getMessage());
    	}		
		
		
		UserCode userCode = new UserCode(userId);
		AuthenticationTypeCode typeCode = new AuthenticationTypeCode("AT_AIOTP");
		// Get the home interface
		ServiceLocator sl = new ServiceLocator();
		AuthenticatorManager authenticatorManager= sl.lookupAuthenticatorManager();
		try {
            String channelCode = config.getConfigValue(AuthenticationServerConfiguration.CHANNEL);
			authenticatorManager.deleteDeviceAuthenticator(new ALSI(auth.getArcSession().toString()),
                                                        new ChannelCode(channelCode),
														userCode, 
														typeCode, 
														new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
        } catch (InvalidParameterException e) {
        	// TODO replace with proper logging
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
            throw new ArcAuthenticationServerException(e.getMessage());
        } catch (RemoteException e) {
            throw new ArcAuthenticationServerConnectionException(e.getMessage());
        }  
		
	}
}
