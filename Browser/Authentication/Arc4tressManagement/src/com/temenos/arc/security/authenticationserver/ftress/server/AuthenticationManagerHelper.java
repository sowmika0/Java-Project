/**
 * @author Cerun
 */
   package com.temenos.arc.security.authenticationserver.ftress.server;

import java.rmi.RemoteException;

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
import com.aspace.ftress.interfaces.ftress.DTO.UPAuthenticationRequest;
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
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.ftress.ServiceLocator;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This is a Helper class for Authentication Manager. This helper talk to Ftress server and authenticates the user I/p. 
 * @author Cerun
 *
 */
public final class AuthenticationManagerHelper {
	
	private static AuthenticationServerConfiguration config = null;
	private static Logger logger = LoggerFactory.getLogger(AuthenticationManagerHelper.class);
	
	public static void setConfig(AuthenticationServerConfiguration c) {
		logger.debug("setting config");
		config = c;
		logger.debug("config set");
	}

	public static void setConfig(AuthenticationServerConfiguration c, Logger l) {
		logger = l;
		logger.debug("setting config");
		config = c;
		logger.debug("config set");
	}
/**
 * This method authenticates the user i/p, based on the authentication Type passed.
 * @param user
 * @param requesttype
 * @param passwd
 * @param challenge
 * @param response
 * @param authType
 * @return
 * @throws ArcAuthenticationServerException
 * @throws FailedLoginException
 * @throws AccountExpiredException
 */	
	
	public static AuthenticationResponse authenticate(String user, String requesttype, String passwd , String challenge, String response, String authType) throws ArcAuthenticationServerException,FailedLoginException, AccountExpiredException {
	
		Authenticator authenticator = lookupAuthenticator();
		ChannelCode channelCode = new ChannelCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		logger.info("Channel from configuration file is : " + channelCode);
		SecurityDomain securityDomain = new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		logger.info("SecurityDomain from configuration file is : " + securityDomain);
		AuthenticationResponse authenticationResponse = null;

		try {
			if (requesttype.equals("CR") || requesttype.equals("OTP")) {
				logger.info("Preparing Device Authentication Request ");
				DeviceAuthenticationRequest authenticationRequest = (DeviceAuthenticationRequest) buildRequest(user, requesttype, passwd,challenge,response,authType);
				authenticationResponse = authenticator.primaryAuthenticateDevice(channelCode,authenticationRequest, securityDomain);
			} else {
				UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(user, requesttype, passwd, challenge,response,authType);
				logger.info("Now authenticating ");
				authenticationResponse = authenticator.primaryAuthenticateUP(channelCode, authenticationRequest, securityDomain);
				logger.info("Successfully authenticated for user: " + user);
			}
			return authenticationResponse;

		} catch (ALSIInvalidException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (DeviceException e) {
			throw new FailedLoginException(e.toString());
		} catch (DeviceAuthenticationException e) {
			throw new FailedLoginException(e.toString());
		} catch (InvalidChannelException e) {
			throw new ArcAuthenticationServerException(
					"Channel incorrectly configured");
		} catch (AuthenticationTierException e) {
			throw new ArcAuthenticationServerException(
					"Authentication Type incorrectly configured");
		} catch (PasswordExpiredException e) {
			throw new AccountExpiredException(e.toString());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
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
	}

	/*
	 * This method builds the authentication request based based on the
	 * authentication type supplied (non-Javadoc)
	 * 
	 * @see com.temenos.arc.security.jaas.AbstractAuthenticator#buildRequest()
	 */
	public static final AuthenticationRequest buildRequest(String userId,String requestType, String passwd,String challenge ,String response,String authType) {

		logger.info("Building authentication Request ");
		AuthenticationRequest authenticationRequest = buildRequestType(userId,requestType,passwd, challenge,response);
		AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(authType);
		authenticationRequest.setAuthenticationTypeCode(authenticationTypeCode);
		logger.info("Setting No session option for authenticating ");
		authenticationRequest.setAuthenticateNoSession(true); // donot create session since a valid session is already there.
		authenticationRequest.setParameters(new AuthenticationRequestParameter[] {});
		logger.info("Finished building authentication Request ");
		return authenticationRequest;
	}
	
	/**
	 * This method builds AuthenticationRequest object based on authentication
	 * type
	 * 
	 * @param userId
	 * @param auth_Type
	 * @param passwd
	 * @return authenticationRequest
	 */
	public static AuthenticationRequest buildRequestType(String userId,String requestType, String passwd, String challenge, String response) {

		if (requestType.equals("CR") || requestType.equals("OTP")){
			DeviceAuthenticationRequest authenticationRequest = new DeviceAuthenticationRequest();
			logger.info("Setting User code to the request. UserId is " + userId);
			authenticationRequest.setUserCode(new UserCode(userId));
			if(requestType.equals("OTP")){
				authenticationRequest.setOneTimePassword(passwd);
				authenticationRequest.setAuthenticationMode(to4tressConstant(config, AuthenticationServerConfiguration.DEVICE_MODE));
			}else{
				authenticationRequest.setChallenge(challenge);
				authenticationRequest.setOneTimePassword(response);
				// when the authentication type is "CR". The device mode should be "ASYNC", hence its not configured.
				logger.info("Setting device mode to ASYNC for Challenge Response.");
				authenticationRequest.setAuthenticationMode(DeviceAuthenticationRequest.ASYNCHRONOUS);
			}			
			return authenticationRequest;
		} else {
			UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest();
			authenticationRequest.setUsername(userId);
			authenticationRequest.setPassword(passwd);
			return authenticationRequest;
		}
	}
	
	
	public static Authenticator lookupAuthenticator()throws ArcAuthenticationServerConnectionException {
			return new ServiceLocator().lookupAuthenticator();
	}

	
	/**
	 * Helper used when the config value needs to be mapped from a String.
	 * 
	 * @param key
	 * @return
	 */
	public static int to4tressConstant(
			AuthenticationServerConfiguration config, final String key) {
		String allowedConfigValue = config.getConfigValue(key);
		if ("SYNC".equalsIgnoreCase(allowedConfigValue)) {
			return DeviceAuthenticationRequest.SYNCHRONOUS;
		}
		if ("ASYNC".equalsIgnoreCase(allowedConfigValue)) {
			return DeviceAuthenticationRequest.ASYNCHRONOUS;
		}
		throw new ArcAuthenticationServerException(
				"Device Mode incorrectly configured");
	}



}
