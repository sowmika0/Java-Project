package com.temenos.arc.security.authenticationserver.ftress;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;

import com.aspace.ftress.interfaces70.ejb.Authenticator;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces70.ejb.CredentialManager;
import com.aspace.ftress.interfaces70.ejb.UserManager;
import com.aspace.ftress.interfaces70.ftress.DTO.ALSI;
import com.aspace.ftress.interfaces70.ftress.DTO.ALSISession;
import com.aspace.ftress.interfaces70.ftress.DTO.Attribute;
import com.aspace.ftress.interfaces70.ftress.DTO.AttributeTypeCode;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationRequest;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationRequestParameter;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationResponse;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationResponseParameter;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces70.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces70.ftress.DTO.Password;
import com.aspace.ftress.interfaces70.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticationRequest;
import com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.User;
import com.aspace.ftress.interfaces70.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces70.ftress.DTO.UserSearchCriteria;
import com.aspace.ftress.interfaces70.ftress.DTO.UserSearchResults;
import com.aspace.ftress.interfaces70.ftress.DTO.constants.AuthenticationResponseConstants;
import com.aspace.ftress.interfaces70.ftress.DTO.device.DeviceSearchCriteria;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.AuthenticationTierException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ChangePasswordFailedException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.DeviceAuthenticationException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.DeviceException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InvalidChannelException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.NoFunctionPrivilegeException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ObjectNotFoundException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.PasswordExpiredException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.SeedingException;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.authenticationserver.server.ArcUserStateException;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.LoginModuleException;
import com.temenos.arc.security.jaas.T24PasswordCredential;
import com.temenos.arc.security.jaas.T24Principal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This is an utility class to wrap methods specific to 4TRESS authentication
 * server by calling 4TRESS7.0 ejb interface methods
 * 
 * @author pradeepm
 * 
 */

public class FtressHelpers70 {

	private static final String TEMPLATE_NAME = "TEMPLATE_NAME";
	private static final String SESSION_BOUND = "SESSION_BOUND";
	private static final String CH_DIRECT = "CH_DIRECT";
	private static final String ASYNCHRONOUS = "ASYNC";
	private static final String SYNCHRONOUS = "SYNC";
	private static final String AUTHENTICATOR_DELIMITER = "\\" + "|";
	private static AuthenticationServerConfiguration config = null;
	private static Logger logger = LoggerFactory
			.getLogger(FtressHelpers70.class);
	private static ServiceLocator70 serviceLocator70Instance = null;
	private static FtressHelpers70 instance = null;
	private ChannelCode channelCode = getChannel();
	private static Authenticator lookupAuthenticator = null;

	private ChannelCode getChannel() {
		ChannelCode chCode = new ChannelCode();
		chCode.setCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		return chCode;
	}

	private SecurityDomain securityDomain = new SecurityDomain(
			config.getConfigValue(AuthenticationServerConfiguration.DOMAIN));

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

	public static FtressHelpers70 getInstance() {
		if (instance == null) {
			instance = new FtressHelpers70();
		}
		return instance;
	}

	public static ServiceLocator70 getServiceLocator70Instance() {
		if (serviceLocator70Instance == null) {
			serviceLocator70Instance = new ServiceLocator70();
		}
		return serviceLocator70Instance;
	}

	public static Authenticator getAuthenticatorFromServiceLocator() {
		if (lookupAuthenticator == null) {
			lookupAuthenticator = new ServiceLocator70().lookupAuthenticator();
		}
		return lookupAuthenticator;
	}

	/**
	 * Uses authentication server's client classes to perform the
	 * username/password authentication. Requires <code>config</code> to have
	 * been set.
	 * 
	 * @param userId
	 *            4tress system userId
	 * @param passphrase
	 *            4tress system password
	 * @return authentication server specific response to be interpreted by the
	 *         caller
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerException
	 */
	public GenericAuthenticationResponse authenticateSystemUser(String userId,
			char[] passphrase) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException {
		logger.debug("Going to get authTypeSystem");
		String authTypeSys = config
				.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE_SYSTEM);
		logger.debug("Got : authTypeSys: " + authTypeSys);
		UPAuthenticationRequest authenticationRequest = buildRequest(userId,
				passphrase, authTypeSys);

		logger.debug("Authenticating system userid and password ...");
		logger.debug("authenticateSystemUser()...");

		return convertToGenericAuthenticationResponse(processUPAuthenticationRequest(authenticationRequest));
	}

	/**
	 * @param authResponse
	 * @return genericResponse
	 */
	private GenericAuthenticationResponse convertToGenericAuthenticationResponse(
			AuthenticationResponse authResponse) {
		GenericAuthenticationResponse genericResponse = null;
		if (authResponse != null) {
			genericResponse = new GenericAuthenticationResponse();
			genericResponse.setResponseStatus(authResponse.getResponse());
			genericResponse.setResponseReason(authResponse.getReason());
			if(null!=authResponse.getAlsi()){
				genericResponse.setSession(authResponse.getAlsi().getAlsi());
				genericResponse.setSessionObject(authResponse.getAlsi().toString());
			}
		}
		return genericResponse;
	}

	/**
	 * @param authenticationRequest
	 * @return
	 * @throws AccountExpiredException
	 * @throws FailedLoginException
	 */
	private AuthenticationResponse processUPAuthenticationRequest(
			UPAuthenticationRequest authenticationRequest)
			throws AccountExpiredException, FailedLoginException {
		AuthenticationResponse authenticationResponse = null;
		try {
			Authenticator authenticator = getServiceLocator70Instance()
					.lookupAuthenticator();
			authenticationResponse = authenticator.primaryAuthenticateUP(
					channelCode, authenticationRequest, securityDomain);
		} catch (InvalidChannelException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Channel "
					+ channelCode.getCode().toString()
					+ " incorrectly configured");
		} catch (AuthenticationTierException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (PasswordExpiredException e) {
			logger.error(e.getMessage(), e);
			throw new AccountExpiredException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return authenticationResponse;
	}

	/**
	 * @param deviceAuthenticationRequest
	 * @return authenticationResponse
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 */
	private AuthenticationResponse processDeviceAuthenticationRequest(
			AuthenticationRequest deviceAuthenticationRequest)
			throws FailedLoginException, AccountExpiredException {
		AuthenticationResponse authenticationResponse = null;
		try {
			Authenticator authenticator = getServiceLocator70Instance()
					.lookupAuthenticator();
			authenticationResponse = authenticator.primaryAuthenticateDevice(
					channelCode, deviceAuthenticationRequest, securityDomain);
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (DeviceException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.toString());
		} catch (InvalidChannelException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Channel "
					+ channelCode.getCode().toString()
					+ " incorrectly configured");
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (PasswordExpiredException e) {
			logger.error(e.getMessage(), e);
			throw new AccountExpiredException(e.getMessage());
		} catch (DeviceAuthenticationException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.toString());
		} catch (AuthenticationTierException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return authenticationResponse;
	}

	/**
	 * @param userId
	 * @param passphrase
	 * @param authType
	 * @return authenticationRequest
	 * @throws ArcAuthenticationServerException
	 */
	private UPAuthenticationRequest buildRequest(String userId,
			char[] passphrase, String authType) {
		UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest();
		String passphraseString = new String(passphrase);
		authenticationRequest
				.setAuthenticationTypeCode(applyAuthTypeCode(authType));
		authenticationRequest.setAuthenticateNoSession(false);
		authenticationRequest.setUsername(userId);
		// This checks if the number of seeds is set and if it is sets the
		// seed positions on the request
		// Also resets the password according to the seeding with a String
		// containing just the seed characters.
		int numSeeds = getNumSeedsFromConfig();
		logger.debug("number of seeds: " + numSeeds);
		if (0 != numSeeds) {
			logger.info("Using seeding, no of seeds are : " + numSeeds);

			String seedDelimiter = config
					.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM);
			logger.debug("delimiter: " + seedDelimiter);
			StringTokenizer tokenizer = new StringTokenizer(passphraseString,
					seedDelimiter);
			int[] positionArray = new int[tokenizer.countTokens() - 1];
			int i = 0;
			String passphraseChars = null;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()) {
					int seedPos = Integer.parseInt(token);
					positionArray[i] = seedPos;
				} else {
					passphraseChars = token;
				}
				i++;
			}
			// if the number of characters passed through is not the
			// expected number of characters then we should fail
			if (passphraseChars.length() != numSeeds
					|| positionArray.length != numSeeds) {
				throw new ArcAuthenticationServerException(
						"Error in authentication.");
			}
			authenticationRequest.setSeedPositions(positionArray);
			authenticationRequest.setPassword(passphraseChars);
		}else{
			authenticationRequest.setPassword(passphraseString);
		}
		return authenticationRequest;
	}

	/**
	 * @param t24UserName
	 * @param ftressALSI
	 * @return boolean
	 * @throws ArcUserStateException
	 * @throws ArcAuthenticationServerException
	 * @throws ALSIInvalidException
	 * @throws ObjectNotFoundException
	 * @throws NoFunctionPrivilegeException
	 * @throws InvalidParameterException
	 * @throws InternalException
	 * @throws RemoteException
	 */
	public String deleteUserAuthenticators(String t24UserName, ALSI alsi)
			throws ArcUserStateException, ArcAuthenticationServerException,
			ALSIInvalidException, ObjectNotFoundException,
			NoFunctionPrivilegeException, InvalidParameterException,
			InternalException, RemoteException {
		logger.info("deleteUserAuthenticators() in FtressHelpers70...");
		try {
			// Read authenticators to be deleted from config file.
			// Split string based on seperator value "|"
			ArrayList<String> configuredAuthenticators = getConfiguredAuthenticatorsList();

			AuthenticatorManager authManager = getServiceLocator70Instance()
					.lookupAuthenticatorManager();
			UserCode userCode = new UserCode(t24UserName);
			com.aspace.ftress.interfaces70.ftress.DTO.device.Authenticator[] allAuthenticators = authManager
					.getAllAuthenticatorsForUser(alsi, channelCode, userCode,
							securityDomain);
			com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator[] upAuthenticators = authManager
					.getAllUPAuthenticatorsForUser(alsi, channelCode, userCode,
							securityDomain);

			logger.info("Got All authenticators");
			ArrayList<String> toBeDeletedAuthenticators = new ArrayList<String>();
			ArrayList<String> toBeDeletedUPAuthenticators = new ArrayList<String>();

			// if delete authenticators are specified in config file then delete
			// only those which are configured
			// Otherwise delete all authenticators.
			final int arrayLength = configuredAuthenticators.size();
			if (arrayLength > 0) {
				addConfiguredAuthentcatorsToDelete(configuredAuthenticators,
						allAuthenticators, upAuthenticators,
						toBeDeletedAuthenticators, toBeDeletedUPAuthenticators,
						arrayLength);
			} else {
				// Delete authenticators are NOT configured so all
				// authenticators should be deleted.
				addAllAuthentcatorsToDelete(allAuthenticators,
						upAuthenticators, toBeDeletedAuthenticators,
						toBeDeletedUPAuthenticators);
			}

			deleteAuthenticators(alsi, authManager, userCode,
					toBeDeletedAuthenticators, toBeDeletedUPAuthenticators);

		} catch (ArcAuthenticationServerException e) {
			logger.error(e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return "SUCCESS";
	}

	/**
	 * @param alsi
	 * @param authManager
	 * @param userCode
	 * @param toBeDeletedAuthenticators
	 * @param toBeDeletedUPAuthenticators
	 * @throws InvalidParameterException
	 * @throws InternalException
	 * @throws ALSIInvalidException
	 * @throws ObjectNotFoundException
	 * @throws RemoteException
	 * @throws NoFunctionPrivilegeException
	 */
	private void deleteAuthenticators(ALSI alsi,
			AuthenticatorManager authManager, UserCode userCode,
			ArrayList<String> toBeDeletedAuthenticators,
			ArrayList<String> toBeDeletedUPAuthenticators)
			throws InvalidParameterException, InternalException,
			ALSIInvalidException, ObjectNotFoundException, RemoteException,
			NoFunctionPrivilegeException {
		if (toBeDeletedAuthenticators.size() > 0) {
			authManager
					.deleteAuthenticators(
							alsi,
							channelCode,
							userCode,
							toBeDeletedAuthenticators
									.toArray(new AuthenticationTypeCode[toBeDeletedAuthenticators
											.size()]), securityDomain);
			logger.info("All allAuthenticators got deleted");
		}
		if (toBeDeletedUPAuthenticators.size() > 0) {
			authManager
					.deleteUPAuthenticators(
							alsi,
							channelCode,
							userCode,
							toBeDeletedUPAuthenticators
									.toArray(new AuthenticationTypeCode[toBeDeletedUPAuthenticators
											.size()]), securityDomain);
			logger.info("All upAuthenticators got deleted ");
		}

		if (toBeDeletedAuthenticators.size() == 0
				&& toBeDeletedUPAuthenticators.size() == 0) {
			logger.error("No Authenticators to delete for this user");
			throw new ArcUserStateException();
		}
	}

	/**
	 * @param allAuthenticators
	 * @param upAuthenticators
	 * @param toBeDeletedAuthenticators
	 * @param toBeDeletedUPAuthenticators
	 */
	private void addAllAuthentcatorsToDelete(
			com.aspace.ftress.interfaces70.ftress.DTO.device.Authenticator[] allAuthenticators,
			com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator[] upAuthenticators,
			ArrayList<String> toBeDeletedAuthenticators,
			ArrayList<String> toBeDeletedUPAuthenticators) {
		logger.info("uA: Since No Authenticators configured to delete, System about to delete all the authenticators");
		// Add all the UP authenticators to hash map
		if (allAuthenticators != null) {
			logger.info("Since No Authenticators configured to delete, System about to delete all the authenticators");
			for (int authCount = 0; authCount < allAuthenticators.length; authCount++) {
				toBeDeletedAuthenticators.add(allAuthenticators[authCount]
						.getAuthenticationTypeCode().getCode().toString());
			}
		} else {
			// logger.debug("no user password authenticators ");
			logger.info("no user password authenticators ");
		}
		// Add all the Device authenticators to hash map
		if (upAuthenticators != null) {
			logger.info("up: Since No Authenticators configured to delete, System about to delete all the authenticators");
			for (int authCount = 0; authCount < upAuthenticators.length; authCount++) {
				toBeDeletedUPAuthenticators.add(upAuthenticators[authCount]
						.getAuthenticationTypeCode().getCode().toString());
			}
		} else {
			// logger.debug(" no device authenticators ");
			logger.info("no user password authenticators ");
		}
	}

	/**
	 * @param configuredAuthenticators
	 * @param allAuthenticators
	 * @param upAuthenticators
	 * @param toBeDeletedAuthenticators
	 * @param toBeDeletedUPAuthenticators
	 * @param arrayLength
	 */
	private void addConfiguredAuthentcatorsToDelete(
			ArrayList<String> configuredAuthenticators,
			com.aspace.ftress.interfaces70.ftress.DTO.device.Authenticator[] allAuthenticators,
			com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator[] upAuthenticators,
			ArrayList<String> toBeDeletedAuthenticators,
			ArrayList<String> toBeDeletedUPAuthenticators, final int arrayLength) {
		logger.info("delete array length: " + arrayLength);
		// load all the delete authenticators from config to an hashmap
		String authToDelete = "";
		for (int i = 0; i < arrayLength; i++) {
			boolean authAdded = false;
			authToDelete = configuredAuthenticators.get(i);
			logger.info("authToDelete: " + authToDelete);
			if (allAuthenticators != null) {
				for (int authCount = 0; authCount < allAuthenticators.length; authCount++) {
					String authTypeCode = allAuthenticators[authCount]
							.getAuthenticationTypeCode().getCode().toString();
					logger.info("authTypeCode: " + authTypeCode
							+ " ; authToDelete: " + authToDelete);
					if (authTypeCode.equalsIgnoreCase(authToDelete)) {
						toBeDeletedAuthenticators.add(authTypeCode);
						authAdded = true;
						break;
					}
				}
			}
			if (!authAdded && upAuthenticators != null) {
				for (int authCount = 0; authCount < upAuthenticators.length; authCount++) {
					String upAuthTypeCode = upAuthenticators[authCount]
							.getAuthenticationTypeCode().getCode().toString();
					logger.info("upAuthTypeCode: " + upAuthTypeCode
							+ "; authToDelete: " + authToDelete);
					if (upAuthTypeCode.equalsIgnoreCase(authToDelete)) {
						toBeDeletedUPAuthenticators.add(upAuthTypeCode);
						break;
					}
				}
			}
		}
	}

	/**
	 * @return
	 * @throws InvalidParameterException
	 */
	private ArrayList<String> getConfiguredAuthenticatorsList()
			throws InvalidParameterException {
		String[] deleteAuthArray = config.getConfigValue(
				AuthenticationServerConfiguration.DELETE_AUTHENTICATORS_LIST)
				.split(AUTHENTICATOR_DELIMITER);

		ArrayList<String> configuredAuthenticators = new ArrayList<String>();
		for (int i = 0; i < deleteAuthArray.length; i++) {
			if (deleteAuthArray[i] != null
					|| !deleteAuthArray[i].trim().equals("")) {
				configuredAuthenticators.add(deleteAuthArray[i]);
			} else {
				logger.error("Configuration for DELETE_AUTHENTICATORS_LIST is Wrong. Plesae Correct it");
				throw new InvalidParameterException();
			}
		}
		return configuredAuthenticators;
	}

	/**
	 * Requires <code>config</code> to have been set.
	 * 
	 * @param session
	 *            The current Session that the calling system is logged in with
	 * @param t24UserName
	 *            The T24 username for which the ARC userid is required
	 * @return An array of ARC users that match the t24 username
	 * @throws ArcAuthenticationServerConnectionException
	 *             if there are problems connecting to the Authentication Server
	 * @throws ArcAuthenticationServerException
	 *             if there are any other problems in the Authentication Server
	 */
	public User[] getArcUsers(ArcSession session, String t24UserName) {
		if (config == null) {
			throw new ArcAuthenticationServerException("config not found");
		}
		User[] users = null;
		AttributeTypeCode typeCode = new AttributeTypeCode(
				config.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_T24USER));
		Attribute attribute = new Attribute(typeCode, t24UserName);
		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setAttributeCriteria(new Attribute[] { attribute });

		UserManager userManager = getServiceLocator70Instance()
				.lookupUserManager();

		try {
			UserSearchResults results = userManager.searchUsers(
					getALSIValue(session.toString()), channelCode, criteria,
					securityDomain);
			if (null != results) {
				users = results.getUsers();
			}
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return users;
	}

	/**
	 * Pre-check to see is the given T24 user has been configured agfainst an
	 * ARC user.
	 * 
	 * @param session
	 *            The current Session that the calling system is logged in with
	 * @param t24UserName
	 *            The T24 username to be searched for
	 * @return true if the t24 user exists in the Authentication Server, false
	 *         otherwise
	 * @throws ArcAuthenticationServerConnectionException
	 *             if there are problems connecting to the Authentication Server
	 * @throws ArcAuthenticationServerException
	 *             if there are any other problems in the Authentication Server
	 */
	public boolean userExists(ArcSession session, String t24UserName) {
		User[] users = getArcUsers(session, t24UserName);
		if (users != null && users.length > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Helper to interpret the 4TRESS server's response to the authentication
	 * attempt. If successful, then the returned <code>ArcSession</code> will be
	 * non-null. If unsuccessful, an exception is thrown.
	 * 
	 * @param authenticationResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws AccountLockedException
	 */
	public ArcSession handleResponse(
			GenericAuthenticationResponse genericResponse,
			AuthenticationServerConfiguration configToUse)
			throws ArcAuthenticationServerException, FailedLoginException,
			AccountExpiredException, AccountLockedException {
		switch (genericResponse.getResponseStatus()) {
		case GenericAuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED:
			if (configToUse.dontCreateSession()) {
				logger.info("Not creating a new session");
				return null;
			} else {
				ALSI alsi = getALSIValue(genericResponse.getSession());
				if (alsi == null) {
					throw new ArcAuthenticationServerException(
							"Session identifier not generated by 4TRESS");
				}
				return new ArcSession(alsi.getAlsi());
			}
		case GenericAuthenticationResponse.RESPONSE_AUTHENTICATION_FAILED:
			handleFailureCases(genericResponse);
		default:
			throw new ArcAuthenticationServerException(
					"Unknown response code: "
							+ genericResponse.getResponseStatus());
		}
	}

	/**
	 * @param genericResponse
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws AccountLockedException
	 */
	private void handleFailureCases(
			GenericAuthenticationResponse genericResponse)
			throws FailedLoginException, AccountExpiredException,
			AccountLockedException {
		int failureReason = genericResponse.getResponseReason();
		switch (failureReason) {
		case AuthenticationResponseConstants.REASON_PASSWORD_MISMATCH:
			throw new FailedLoginException(
					"Authentication failed due to 4TRESS 'reason code': "
							+ failureReason);
		case AuthenticationResponseConstants.REASON_PASSWORD_MAX_USAGES_REACHED:
			throw new AccountExpiredException(
					"Authentication failed due to 4TRESS 'reason code': "
							+ failureReason);
		case AuthenticationResponseConstants.REASON_AUTHENTICATOR_DISABLED:
			throw new AccountLockedException(
					"Authentication failed due to 4TRESS 'reason code': "
							+ failureReason);
		case AuthenticationResponseConstants.REASON_AUTHENTICATOR_EXPIRED:
			throw new AccountExpiredException(
					"Authentication failed due to 4TRESS 'reason code': "
							+ failureReason);
		default:
			throw new ArcAuthenticationServerException(
					"Unknown response code: " + failureReason);
		}
	}

	/**
	 * Common implementation of {@link Authenticatable#logoff()} for all 4TRESS
	 * authenticators (invalidates the <code>ArcSession</code>)
	 * 
	 * @param session
	 * @return boolean
	 * @throws ArcAuthenticationServerException
	 */
	public boolean logoff(ArcSession session)
			throws ArcAuthenticationServerException {
		Authenticator authenticator = getServiceLocator70Instance()
				.lookupAuthenticator();
		try {
			authenticator.logout(getALSIValue(session.toString()), channelCode,
					securityDomain);
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return true;
	}

	/**
	 * Uses authentication server's client classes to perform the
	 * username/password authentication. Requires <code>config</code> to have
	 * been set.
	 * 
	 * @param userId
	 *            ARC user to authenticate
	 * @param passphrase
	 *            password defined in the auth server against the ARC user
	 * @return auth server specific response to be interpreted by the caller
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerException
	 */
	public GenericAuthenticationResponse authenticate(String userId,
			char[] passphrase, String authType)
			throws ArcAuthenticationServerException, FailedLoginException,
			AccountExpiredException {
		logger.debug("Looking up 4TRESS EJB...");
		AuthenticationResponse authenticationResponse = null;
		if (authType.equals(config
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE))) {
			String nullChallengeValue = "";
			AuthenticationRequest deviceAuthenticationRequest = buildAuthRequestForDevice(
					userId, new String(passphrase), nullChallengeValue, false,
					authType, false);
			authenticationResponse = processDeviceAuthenticationRequest(deviceAuthenticationRequest);

		} else {
			UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(
					userId, passphrase, authType);
			authenticationResponse = processUPAuthenticationRequest(authenticationRequest);
		}
		return convertToGenericAuthenticationResponse(authenticationResponse);
	}

	/**
	 * @param name
	 * @param password
	 * @param config
	 * @return AbstractAuthenticator
	 */
	public AbstractAuthenticator loginWithUPAuthenticator(String name,
			String password, AuthenticationServerConfiguration config) {
		NameCallback nameC = new NameCallback("");
		nameC.setName(name);
		PasswordCallback passwordC = new PasswordCallback("", false);
		passwordC.setPassword(password.toCharArray());

		AbstractAuthenticator auth = new UsernamePasswordAuthenticator(nameC,
				passwordC, config);
		try {
			auth.authenticate();
			// AuthenticationResponse response = authenticate(name,
			// password.toCharArray(), config
			// .getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
			// setSessionId(handleResponse(response, config));
		} catch (FailedLoginException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (AccountExpiredException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		return auth;
	}

	/**
	 * @return 0 if config says mem word is not seeded, otherwise the number of
	 *         seeds
	 */
	private int getNumSeedsFromConfig() {
		int numSeeds = 0;
		String sAuthType = config
				.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE);
		if ("true"
				.equalsIgnoreCase(config
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_IS_SEEDED))) {
			String numSeedsParam = config
					.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_NUM_SEEDS);
			logger.info("AuthType : " + sAuthType
					+ " is seeded and No of Seeds = " + numSeedsParam);
			numSeeds = Integer.parseInt(numSeedsParam);
		} else {
			logger.info("AuthType : " + sAuthType + " is not seeded");
		}
		return numSeeds;
	}

	/**
	 * Returns an array of positions of characters to be asked for a future mem
	 * word authentication. The number of these is determined by config.
	 * 
	 * @param userId
	 * @return array of seed positions
	 */
	public int[] getMemWordSeedPositions(String userId) {
		int numSeeds = getNumSeedsFromConfig();
		int result[] = new int[numSeeds];
		logger.info("No Of Seeds : " + numSeeds + "for User ID : " + userId);
		Authenticator authenticator = getServiceLocator70Instance()
				.lookupAuthenticator();
		try {
			result = authenticator
					.getPasswordSeedPositions(
							channelCode,
							new UserCode(userId),
							applyAuthTypeCode(config
									.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE)),
							numSeeds, securityDomain).getPositions();
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return result;
	}

	/**
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return boolean
	 */
	public boolean changeOwnExpiredPassword(String userId, String oldPassword,
			String newPassword) {
		return changeOwnExpiredPassword(
				userId,
				oldPassword,
				newPassword,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD));
	}

	/**
	 * This method changes the expired password in authentication server.
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @param authType
	 * @return boolean
	 */
	public boolean changeOwnExpiredPassword(String userId, String oldPassword,
			String newPassword, String authType) {
		CredentialManager credentialManager = getServiceLocator70Instance()
				.lookupCredentialManager();
		UPAuthenticationRequest request = (UPAuthenticationRequest) buildRequest(
				userId, oldPassword.toCharArray(), authType);
		boolean isChanged = false;
		try {
			isChanged = credentialManager.changeOwnExpiredPassword(request,
					channelCode, new Password(newPassword), securityDomain);
		} catch (ChangePasswordFailedException e) {
			logger
			.info("Error when changing expired password, attempting to change fist use password");
			// try changing a first time login password instead
				isChanged = changeOwnPassword(
						userId,
						oldPassword,
						newPassword,
						authType);
				// if successful password change, then blank the password attribute
				setPasswordAttribute("", userId, newPassword, authType);
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		if (isChanged) {
			logger.info("Successfully changed password");
		} else {
			logger.error("Password Not Changed Try again");
		}
		return isChanged;
	}

	/**
	 * This method changes the password in authentication server. This accepts
	 * authentication type as an extra parameter.
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return boolean
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * 
	 */
	public boolean changeOwnPassword(String userId, String oldPassword,
			String newPassword, String authType){
		CredentialManager cm = getServiceLocator70Instance()
				.lookupCredentialManager();
		GenericAuthenticationResponse response=null;
		boolean returnVal=false;
		try {
			response = authenticate(userId,
					oldPassword.toCharArray(), authType);
			returnVal=true;
		} catch (AccountExpiredException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (FailedLoginException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ArcAuthenticationServerException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		try {
			cm.changeOwnPassword(getALSIValue(response.getSession()),
					channelCode, applyAuthTypeCode(authType), new Password(
							newPassword), new Password(oldPassword),
					securityDomain);
			returnVal=true;
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		logger.info("Successfully changed password");
		return returnVal;
	}

	/**
	 * This method is specific only to change the PIN of a user. Authentication
	 * Type code AT_CUSTPIN will be hardcoded for changing the PIN. Method to
	 * change the pin after the user has logged into the system This method will
	 * be called only from FtressChangePinServlet class. This method will
	 * validate the old pin. If the old pin is valid alsi will be created, using
	 * this alsi new pin will be updated for the user
	 * 
	 * 
	 * @Param userId - External user id of the user
	 * @param oldPassword
	 *            - Old Pin of the user
	 * @Param newPassword - New Pin of the user
	 * @return boolean - true -Pin changed Successfully
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 */

	public boolean changeOwnPin(String userId, String oldPassword,
			String newPassword) throws AccountExpiredException,
			FailedLoginException, ArcAuthenticationServerException {
		return changeOwnPassword(
				userId,
				oldPassword,
				newPassword,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN));
	}

	/**
	 * 
	 * @param attributeValue
	 * @param userId
	 * @param password
	 * @param authType
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 */

	public void setPasswordAttribute(String attributeValue, String userId,
			String password, String authType){
		GenericAuthenticationResponse authenticationResponse;
		try {
			authenticationResponse = authenticate(
					userId, password.toCharArray(), authType);
		} catch (AccountExpiredException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (FailedLoginException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ArcAuthenticationServerException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		UserManager userManager = getServiceLocator70Instance()
				.lookupUserManager();
		Attribute attribute = new Attribute(
				new AttributeTypeCode(
						config.getConfigValue(JaasConfiguration.ATTRIBUTE_ARC_PASSWORD)),
				"");
		Attribute[] attributes = new Attribute[] { attribute };
		try {
			userManager.updateUserAttributes(
					getALSIValue(authenticationResponse.getSession()),
					channelCode, new UserCode(userId), attributes,
					securityDomain);
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}

	}

	/**
	 * Helper used when the config value needs to be mapped from a String.
	 * 
	 * @param key
	 * @return
	 */
	public int to4tressConstant(AuthenticationServerConfiguration config,
			final String key) {
		int reqConstant = 0;
		String allowedConfigValue = config.getConfigValue(key);
		if (SYNCHRONOUS.equalsIgnoreCase(allowedConfigValue)) {
			reqConstant = AuthenticationRequest.SYNCHRONOUS;
		} else if (ASYNCHRONOUS.equalsIgnoreCase(allowedConfigValue)) {
			reqConstant = AuthenticationRequest.ASYNCHRONOUS;
		} else {
			throw new ArcAuthenticationServerException(
					"Device Mode incorrectly configured");
		}
		return reqConstant;
	}

	/**
	 * @param arcSession
	 * @return booolean
	 * @throws ArcAuthenticationServerException
	 */
	public boolean refreshSession(ArcSession arcSession)
			throws ArcAuthenticationServerException {
		if (arcSession == null) {
			throw new IllegalStateException("Session identifier expected");
		}
		Authenticator authenticator = getServiceLocator70Instance()
				.lookupAuthenticator();
		boolean result = true;
		try {
			ALSISession session = authenticator.getSessionData(
					getALSIValue(arcSession.toString()), securityDomain);
			result = (session != null);
		} catch (ALSIInvalidException e) {
			// ALSI has expired, been invalidated, never existed, etc.
			result = false;
		} catch (ObjectNotFoundException e) {
			// Something wasn't found, which indicates something a bit wrong
			result = false;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return result;
	}

	/**
	 * Gets the ArcSession and credentials from the passed in subject, uses them
	 * to retrieve the T24 credentials from the authentication server and then
	 * adds these to the subject
	 * 
	 * @param subject
	 *            in/out parameter described above.
	 * @throws AuthenticationServerException
	 */
	public void addImpersonationCredentialsTo(final Subject subject)
			throws LoginModuleException, FailedLoginException {
		// 1. Find UserCode for the Principal added to the Subject by the
		// LoginModule stacked above the one that invoked this
		// object.
		final Set<ArcSession> publicCredentials = subject
				.getPublicCredentials(ArcSession.class);
		UserCode userCode = retrieveUserCode(publicCredentials);
		// Check name has not been modified
		isDifferentUser(subject, userCode);
		// 2. Use the Principal added to the state of the LoginModule that
		// invoked this object.
		// Switched the credentials so that the T24 user /pw details are
		// obtained using the
		// User user = this.retrieveUser(credentials, userCode);
		User user = retrieveUser(publicCredentials, userCode);
		// 3. Add the attributes that represent the T24 u/n+p/w into the
		// Subject, for use in impersonation later.
		Attribute[] attributes = user.getAttributes();
		boolean userAttributeCommitted = false;
		boolean passwordAttributeCommitted = false;
		for (int i = 0; i < attributes.length; i++) {

			if (isConfiguredAttribute(JaasConfiguration.ATTRIBUTE_T24USER,
					attributes[i].getTypeCode().getCode())) {
				if (logger.isInfoEnabled())
					logger.info("adding t24 user to credentials:"
							+ attributes[i].getValue());
				subject.getPrincipals().add(
						new T24Principal(attributes[i].getValue()));
				userAttributeCommitted = true;
			}
			if (isConfiguredAttribute(JaasConfiguration.ATTRIBUTE_T24PASS,
					attributes[i].getTypeCode().getCode())) {
				if (logger.isInfoEnabled())
					logger.info("adding t24 to credentials:"
							+ attributes[i].getValue());
				subject.getPublicCredentials().add(
						new T24PasswordCredential(attributes[i].getValue()));
				passwordAttributeCommitted = true;
			}
		}
		if (!(userAttributeCommitted && passwordAttributeCommitted)) {
			throw new LoginModuleException(
					"T24 impersonation credentials not available from 4TRESS user");
		}
	}

	/**
	 * @param subject
	 * @param userCode
	 * @throws FailedLoginException
	 */
	private void isDifferentUser(final Subject subject, UserCode userCode)
			throws FailedLoginException {
		final Set<ArcUserPrincipal> principalSet = subject
				.getPrincipals(ArcUserPrincipal.class);
		if (principalSet.size() != 1) {
			throw new LoginModuleException("Cannot find User");
		}
		ArcUserPrincipal aup = (ArcUserPrincipal) principalSet.iterator()
				.next();
		if (!aup.getName().equals(userCode.getCode())) {
			throw new FailedLoginException("Error in authenticating User: "
					+ aup.getName());
		}
	}

	/**
	 * @param authenticator
	 * @param alsi
	 * @param securityDomain
	 * @return ALSISession
	 * @throws FailedLoginException
	 * @throws AuthenticationServerException
	 */
	private UserCode retrieveRemoteUserCode(final Authenticator authenticator,
			final ALSI alsi, final SecurityDomain securityDomain)
			throws FailedLoginException, ArcAuthenticationServerException {
		try {
			return authenticator.getSessionData(alsi, securityDomain)
					.getUserCode();
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
	}

	/**
	 * @param userManager
	 * @param alsi
	 * @param securityDomain
	 * @param channelCode
	 * @param userCode
	 * @return user
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private User retrieveRemoteUser(final UserManager userManager,
			final ALSI alsi, final SecurityDomain securityDomain,
			final ChannelCode channelCode, final UserCode userCode)
			throws ArcAuthenticationServerException, FailedLoginException {
		try {
			return userManager.getUser(alsi, channelCode, userCode,
					securityDomain);
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new FailedLoginException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
	}

	/**
	 * @param credentials
	 * @param userCode
	 * @return user
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private User retrieveUser(final Set<ArcSession> credentials,
			final UserCode userCode) throws ArcAuthenticationServerException,
			FailedLoginException {
		ArcSession arcSession = getSessionFromCredentials(credentials);
		UserManager userManager = getServiceLocator70Instance()
				.lookupUserManager();

		return retrieveRemoteUser(userManager,
				getALSIValue(arcSession.toString()), securityDomain,
				channelCode, userCode);
	}

	/**
	 * @param credentials
	 * @return userCode
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private UserCode retrieveUserCode(final Set<ArcSession> credentials)
			throws ArcAuthenticationServerException, FailedLoginException {
		ArcSession arcSession = getSessionFromCredentials(credentials);
		Authenticator authenticator = getServiceLocator70Instance()
				.lookupAuthenticator();

		return retrieveRemoteUserCode(authenticator,
				getALSIValue(arcSession.toString()), securityDomain);
	}

	/**
	 * @param credentials
	 * @return arcSession
	 */
	private ArcSession getSessionFromCredentials(
			final Set<ArcSession> credentials) {
		if (credentials.isEmpty()) {
			throw new IllegalArgumentException("Session Credentials required");
		}
		ArcSession arcSession = null;
		for (Iterator<ArcSession> iter = credentials.iterator(); iter.hasNext();) {
			final ArcSession nextCredential = iter.next();
			if (nextCredential instanceof ArcSession) {
				arcSession = (ArcSession) nextCredential;
				break;
			}
		}
		if (arcSession == null) {
			throw new IllegalStateException("ArcSession required");
		}
		return arcSession;
	}

	/**
	 * @param key
	 * @param attribute
	 * @return boolean
	 */
	private boolean isConfiguredAttribute(final String key,
			final String attributeCode) {
		return config.getConfigValue(key).equalsIgnoreCase(attributeCode);
	}

	/**
	 * 
	 * Method to check if a specified AuthenticationType exists for a user.
	 * 
	 * @param session
	 *            The current Session that the calling system is logged in with
	 * @param authType
	 *            The authenticatiotype to be searched for
	 * @param UserName
	 *            The username for which authenticationtype mapping is being
	 *            searched
	 * @return true if the user mapped to specified authentication type in the
	 *         Authentication Server, false otherwise
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerConnectionException
	 *             if there are problems connecting to the Authentication Server
	 * @throws ArcAuthenticationServerException
	 *             if there are any other problems in the Authentication Server
	 */

	public boolean isAuthenticationTypeExists(ArcSession session,
			String authType, String userName) throws AccountExpiredException,
			FailedLoginException {
		ALSI alsiValue = null;
		if (session == null) {
			// Get UPAuthenticationRequest,using CryptographyService
			UPAuthenticationRequest upRequest = formUPAuthenticationRequest();
			AuthenticationResponse authenticationResponse = processUPAuthenticationRequest(upRequest);
			if (authenticationResponse != null) {
				alsiValue = authenticationResponse.getAlsi();
			}
		}
		return isAuthTypeExistsInUPAuthenticators(authType, userName, alsiValue);
	}

	/**
	 * @param authType
	 * @param userName
	 * @param alsiValue
	 * @return
	 */
	private boolean isAuthTypeExistsInUPAuthenticators(String authType,
			String userName, ALSI alsiValue) {
		boolean returnValue = false;
		ChannelCode channelCode = new ChannelCode();
		channelCode.setCode(CH_DIRECT);
		try {
			AuthenticatorManager authManager = getServiceLocator70Instance()
					.lookupAuthenticatorManager();
			UPAuthenticator upAuthenticators[] = authManager
					.getAllUPAuthenticatorsForUser(alsiValue, channelCode,
							new UserCode(userName), securityDomain);
			if (upAuthenticators != null) {
				final int length = upAuthenticators.length;
				logger.debug("Number of Authenticators found for User:"
						+ userName + " is " + length);
				for (int i = 0; i < length; i++) {
					UPAuthenticator upAuth = upAuthenticators[i];
					final String code = upAuth.getAuthenticationTypeCode()
							.getCode();
					logger.debug("AutheticationType value : " + code);
					String upAuthTypeValue = code;
					if (upAuthTypeValue.equals(authType)) {
						returnValue = true;
						break;
					}
				}
			}
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(
					"Connection Failure: Problem occured while connecting to server please try later");
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(
					"Connection Failure: Problem occured while connecting to server please try later");
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(
					"Authentication Failure : User Credentials mismatch");
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(
					"Connection Failure: Problem occured while connecting to server please try later");
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(
					"Connection Failure: Problem occured while connecting to server please try later");
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(
					"Connection Failure: Problem occured while connecting to server please try later");
		}
		return returnValue;
	}

	/**
	 * @return
	 */
	private UPAuthenticationRequest formUPAuthenticationRequest() {
		String name = config
				.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER);
		String password = config
				.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
		CryptographyService cryptoService = CryptographyService
				.getInstance(config);
		String ftressUserName = cryptoService.decrypt(name, true);
		String ftressPassword = cryptoService.decrypt(password, true);
		cryptoService.close();

		UPAuthenticationRequest upRequest = new UPAuthenticationRequest();
		upRequest.setUsername(ftressUserName);
		upRequest.setPassword(ftressPassword);

		String systemAuthType = config
				.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE_SYSTEM);
		if (systemAuthType == null || systemAuthType == "") {
			systemAuthType = config
					.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE);
		}
		upRequest.setAuthenticationTypeCode(applyAuthTypeCode(systemAuthType));
		return upRequest;
	}

	/**
	 * @param userId
	 * @param memwordphrase
	 * @return authResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerException
	 * @throws AccountLockedException
	 */
	public GenericAuthenticationResponse authenticateMemWord(String userId,
			char[] memwordphrase) throws FailedLoginException, AccountExpiredException,
			ArcAuthenticationServerException, AccountLockedException {
		GenericAuthenticationResponse authenticationResponse = authenticate(
				userId,
				memwordphrase,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD));
		if (getALSIValue(authenticationResponse.getSession()) == null) {
			int authResponseReason = authenticationResponse.getResponseReason();
			if (authResponseReason == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
				throw new AccountLockedException(
						"Maximum Consecutive Failure Count Reached for MemorableWord with reason : "
								+ authResponseReason);
			}
			if (authResponseReason != AuthenticationResponse.VALUE_NOT_DEFINED) {
				throw new ArcAuthenticationServerException(
						"Authentication failed for MemorableWord with reason : "
								+ authResponseReason);
			}
		}
		return authenticationResponse;
	}

	public GenericAuthenticationResponse authenticatePassWord(String userId,
			char[] password) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			AccountLockedException{
		
		return authenticatePassWord(userId, password, config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD));
	}
	/**
	 * @param userId
	 * @param password
	 * @return authResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws AccountLockedException
	 * @throws ArcAuthenticationServerException
	 */
	public GenericAuthenticationResponse authenticatePassWord(String userId,
			char[] password,String authType) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			AccountLockedException {

		GenericAuthenticationResponse authenticationResponse = authenticate(
				userId,
				password,
				authType);
		final String session = authenticationResponse.getSession();
		if (session == null) {
			int authResponseReason = authenticationResponse.getResponseReason();
			logger.info("Authentication Failed : Returned ALSI is null");
			if (authResponseReason == AuthenticationResponse.REASON_PASSWORD_MAX_USAGES_REACHED) {
				throw new AccountExpiredException(
						"User PassWord Expired : "
								+ authResponseReason);
			} else if (authResponseReason == AuthenticationResponseConstants.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
				throw new AccountLockedException(
						"Maximum Consecutive Failure Count Reached for PassWord with reason : "
								+ authResponseReason);
			} else if (authResponseReason == AuthenticationResponseConstants.REASON_PASSWORD_MISMATCH) {
				logger.debug("Authentication Failed : User entered password mismatch");
				throw new FailedLoginException(
						"Authentication Failed : User Credentials mismatch");
			} else {
				logger.info("Authentication Failed : Returned ALSI is null");
				throw new ArcAuthenticationServerException("Authentication Failed");
			}
		}
		// This is to check the First Time Login
		if (checkArcPasswordAttributeExists(getALSIValue(session), userId)) {
			throw new AccountExpiredException(
					"Password Has been expired: Please change the password");
		}
		logger.info("password authenticated");
		return authenticationResponse;
	}
	
	/**
	 * @param userId
	 * @param otpValue
	 * @return authResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerException
	 * @throws AccountLockedException
	 */
	public GenericAuthenticationResponse authenticateDevice(String userId,
			char[] otpValue) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			AccountLockedException {
		GenericAuthenticationResponse authenticationResponse = authenticate(
				userId,
				otpValue,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE));
		if (getALSIValue(authenticationResponse.getSession()) == null) {
			int authResponseReason = authenticationResponse.getResponseReason();
			if (authResponseReason == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
				throw new AccountLockedException(
						"Maximum Consecutive Failure Count Reached for Device with reason : "
								+ authResponseReason);
			} else {
				throw new ArcAuthenticationServerException(
						"Authentication Failed : User Credentials mismatch");
			}
		}
		return authenticationResponse;
	}

	/**
	 * @param alsi
	 * @param userId
	 * @return attributes
	 */
	private Attribute[] retrieveUserAttributes(ALSI alsi, String userId) {
		UserManager um = getServiceLocator70Instance().lookupUserManager();
		UserCode userCode = new UserCode(userId);
		Attribute[] attributes = null;
		try {
			attributes = um
					.getUser(alsi, channelCode, userCode, securityDomain)
					.getAttributes();
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return attributes;
	}

	/**
	 * @param alsi
	 * @param userId
	 * @return boolean
	 */
	private boolean checkArcPasswordAttributeExists(ALSI alsi, String userId) {

		boolean passwordExists = false;
		Attribute[] attributes = retrieveUserAttributes(alsi, userId);
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				if (isConfiguredAttribute(
						JaasConfiguration.ATTRIBUTE_ARC_PASSWORD, attributes[i]
								.getTypeCode().getCode())) {
					String passwordAttribute = attributes[i].getValue();
					if (logger.isInfoEnabled())
						logger.info("found password attribute:"
								+ passwordAttribute);
					if(passwordAttribute!=null){
						passwordExists = true;		
					}
				}
			}
		}
		return passwordExists;
	}

	/**
	 * @param code
	 * @return authTypeCode
	 */
	private AuthenticationTypeCode applyAuthTypeCode(String code) {
		AuthenticationTypeCode authTypeCode = new AuthenticationTypeCode();
		authTypeCode.setCode(code);
		return authTypeCode;
	}

	/**
	 * @param sessionId
	 * @return alsi
	 */
	private ALSI getALSIValue(String sessionId) {
		ALSI alsi = new ALSI();
		alsi.setAlsi(sessionId);
		return alsi;
	}

	/**
	 * @param username
	 * @param sessionId
	 * @param oobOTPValue
	 * @param sendingRequest
	 * @return authRequest
	 */
	private AuthenticationRequest buildOOBOTPRequest(String username,
			String sessionId, String oobOTPValue, boolean sendingRequest) {
		AuthenticationRequest authRequest = new AuthenticationRequest();
		UserCode oobUser = new UserCode();
		oobUser.setCode(username);
		logger.debug("User name to send OOB OTP : " + username);
		authRequest
				.setAuthenticationTypeCode(applyAuthTypeCode(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_OOB)));
		authRequest.setAuthenticationMode(AuthenticationRequest.SYNCHRONOUS);
		AuthenticationRequestParameter paramSession = new AuthenticationRequestParameter();
		paramSession.setName(SESSION_BOUND);
		paramSession.setValue(sessionId);

		if (sendingRequest) {
			AuthenticationRequestParameter param1 = setRequestParameters(
					AuthenticationResponseConstants.PARAMETER_GENERATE_CREDENTIAL,
					AuthenticationResponseConstants.PARAMETER_SEND_OOB);
			String oobTemplateName = config
					.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_OOBTEMPLATE);
			if (oobTemplateName != null) {
				AuthenticationRequestParameter param2 = setRequestParameters(
						TEMPLATE_NAME, oobTemplateName);
				AuthenticationRequestParameter[] paramArray = new AuthenticationRequestParameter[3];
				paramArray[0] = param1;
				paramArray[1] = param2;
				paramArray[2] = paramSession;
				authRequest.setParameters(paramArray);
			} else {
				AuthenticationRequestParameter[] paramArray = new AuthenticationRequestParameter[2];
				paramArray[0] = param1;
				paramArray[1] = paramSession;
				authRequest.setParameters(paramArray);
			}
			authRequest.setOneTimePassword(null);
			UserSearchCriteria usrCriteria = new UserSearchCriteria();
			usrCriteria.setUserCode(oobUser);
			authRequest.setUserSearchCriteria(usrCriteria);
		} else {
			AuthenticationRequestParameter[] paramArray = new AuthenticationRequestParameter[1];
			paramArray[0] = paramSession;
			authRequest.setParameters(paramArray);
			authRequest.setAuthenticateNoSession(true);
			authRequest.setOneTimePassword(oobOTPValue);
			// Set the OOB Valued entered by user
			DeviceSearchCriteria deviceSearchCriteria = new DeviceSearchCriteria();
			deviceSearchCriteria.setUserCode(oobUser);
			authRequest.setDeviceCriteria(deviceSearchCriteria);
		}
		return authRequest;
	}

	/**
	 * @param name
	 * @param value
	 * @return authReqParam
	 */
	private AuthenticationRequestParameter setRequestParameters(String name,
			String value) {
		AuthenticationRequestParameter authReqParam = new AuthenticationRequestParameter();
		authReqParam.setName(name);
		authReqParam.setValue(value);

		return authReqParam;
	}

	/**
	 * @param userId
	 * @param currentALSI
	 * @param sessionId
	 * @return boolean
	 */
	public boolean sendAuthenticationOOBOTPValue(String userId,
			String currentALSI, String sessionId) {
		boolean oobOTPSent = false;
		logger.info("Sending OOB OTP Value ...");

		// get the login ALSI which is required to send OOB OTP
		// loginALSI
		ALSI loginALSI = new ALSI();
		loginALSI.setAlsi(currentALSI);

		try {
			AuthenticationRequest authSendRequest = buildOOBOTPRequest(userId,
					sessionId, null, true);
			AuthenticationResponse oobSendResponse = getAuthenticatorFromServiceLocator()
					.indirectPrimaryAuthenticateDevice(loginALSI, channelCode,
							authSendRequest, securityDomain);

			int success = oobSendResponse.getResponse();
			if (success != AuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED
					&& (oobSendResponse.getAlsi() == null)) {
				AuthenticationResponseParameter[] authResponseParams = oobSendResponse
						.getParameters();

				for (AuthenticationResponseParameter authResponseParam : authResponseParams) {
					if (authResponseParam
							.getName()
							.equalsIgnoreCase(
									AuthenticationResponseConstants.PARAMETER_AUTHENTICATION_OOB_SENT)
							&& AuthenticationResponseConstants.PARAMETER_VALUE_AUTHENTICATION_OOB_SENT_TRUE
									.equalsIgnoreCase(authResponseParam
											.getValue())) {
						// OTP was sent via OOB
						logger.info("OTP sent successfully via authetication server...");
						oobOTPSent = true;
					} else {
						// OTP was not sent - ERROR
						logger.info("Error in authentication server while sending OTP");
						oobOTPSent = false;
					}
				}
			}
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error while sending OTP to user");
		} catch (InternalException e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error while sending OTP to user");
		} catch (RemoteException e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error while sending OTP to user");
		} catch (NullPointerException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error while sending OTP to user");
		}
		return oobOTPSent;
	}

	/**
	 * Throws the exception
	 * 
	 * @param e
	 */
	private void throwArcAuthenticationServerException(Exception e,
			String message) {
		logger.error(e.getMessage(), e);
		throw new ArcAuthenticationServerException(message);
	}

	/**
	 * @param userId
	 * @param currentALSI
	 * @param sessionId
	 * @param oobOTPValue
	 * @return oobAuthenticationStatus
	 */
	public int doOOBOTPAuthentication(String userId, String currentALSI,
			String sessionId, String oobOTPValue) {
		int oobAuthenticationStatus = 1;
		ALSI loginALSI;

		try {
			logger.info("doOOBAuthentication Called");

			loginALSI = new ALSI();
			loginALSI.setAlsi(currentALSI);
			logger.debug("doOOBAuthentication login ALSI value is : "
					+ currentALSI);

			AuthenticationRequest authRequest = buildOOBOTPRequest(userId,
					sessionId, oobOTPValue, false);

			AuthenticationResponse authenticationResponse = getAuthenticatorFromServiceLocator()
					.indirectPrimaryAuthenticateDevice(loginALSI, channelCode,
							authRequest, securityDomain);

			if (authenticationResponse != null) {
				if (authenticationResponse.getResponse() == AuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED) {
					oobAuthenticationStatus = 0;
					logger.info("User OTP authenticated successfully...");
				} else if (authenticationResponse.getResponse() == AuthenticationResponse.RESPONSE_AUTHENTICATION_FAILED) {
					if (authenticationResponse.getReason() == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
						oobAuthenticationStatus = 2;
						logger.info("User OOB OTP authentication failed, maximum failure count reached");
					} else {
						oobAuthenticationStatus = 1;
					}
				}
			} else {
				oobAuthenticationStatus = 3;
				logger.info("User OTP authentication failed, response is NULL");
			}
		} catch (InternalException e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error occured while authenticating OOB Value");
		} catch (RemoteException e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error occured while authenticating OOB Value");
		} catch (Exception e) {
			throwArcAuthenticationServerException(e,
					"Connection Failure : Error occured while authenticating OOB Value");
		}
		return oobAuthenticationStatus;
	}

	/**
	 * @param userName
	 * @return challenge
	 */
	public String getAuthenticationChallenge(String userName) {

		String challenge = null;
		try {
			UserCode uCode = new UserCode(userName);

			challenge = getAuthenticatorFromServiceLocator()
					.getAuthenticationChallengeByUserCode(
							channelCode,
							uCode,
							applyAuthTypeCode(config
									.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE)),
							securityDomain).getChallenge();

		} catch (InvalidParameterException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (SeedingException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (DeviceAuthenticationException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (DeviceException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ObjectNotFoundException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (PasswordExpiredException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InternalException e) {
			throw new ArcAuthenticationServerConnectionException(e.toString());
		} catch (RemoteException e) {
			throw new ArcAuthenticationServerConnectionException(e.toString());
		}
		return challenge;
	}

	/**
	 * @param userName
	 * @param otpValue
	 * @param challengeValue
	 * @param signMode
	 * @return otpChallengeAuthenticationStatus
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 * @throws AccountExpiredException
	 * @throws ArcAuthenticationServerException
	 */
	public int doOTPChallengeAuthentication(String userName, String otpValue,
			String challengeValue, boolean signMode)
			throws ArcAuthenticationServerException, FailedLoginException,
			AccountExpiredException {
		int otpChallengeAuthenticationStatus = 0;
		try {
			String authenticationTypeCode = config
					.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE);
			if ((isNotNull(challengeValue)) && (signMode)) {
				// Assign the authenticationTypecode with config value set in
				// AUTH_TYPE_SIGNDEVICE
				// if different authentication method is used for Sign
				if (config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_SIGNDEVICE) != null) {
					authenticationTypeCode = config
							.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_SIGNDEVICE);
				}
			}

			AuthenticationRequest authRequest = buildAuthRequestForDevice(
					userName, otpValue, challengeValue, signMode,
					authenticationTypeCode, true);

			AuthenticationResponse authResponse = getAuthenticatorFromServiceLocator()
					.primaryAuthenticateDevice(channelCode, authRequest,
							securityDomain);
			if (authResponse != null) {
				if (authResponse.getResponse() == AuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED) {
					otpChallengeAuthenticationStatus = 0;
					logger.info("User OTP authenticated successfully...");
				} else if (authResponse.getResponse() == AuthenticationResponse.RESPONSE_AUTHENTICATION_FAILED) {
					if (authResponse.getReason() == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
						otpChallengeAuthenticationStatus = 2;
						logger.info("User OTPChallenge authentication failed, maximum failure count reached");
					} else {
						otpChallengeAuthenticationStatus = 1;
					}
				}
			} else {
				otpChallengeAuthenticationStatus = 3;
				logger.error("User OTPChallenge authentication failed, response is NULL");
			}
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
			throw new FailedLoginException(e.toString());
		} catch (SeedingException e) {
			throw new FailedLoginException(e.toString());
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			throw new ArcAuthenticationServerConnectionException(e.toString());
		} catch (RemoteException e) {
			throw new ArcAuthenticationServerConnectionException(e.toString());
		}

		return otpChallengeAuthenticationStatus;
	}

	/**
	 * @param userName
	 * @param otpValue
	 * @param challengeValue
	 * @param signMode
	 * @param authType
	 * @param isSessionAvailable 
	 * @return authRequest
	 */
	private AuthenticationRequest buildAuthRequestForDevice(String userName,
			String otpValue, String challengeValue, boolean signMode,
			String authType, boolean isSessionAvailable) {
		AuthenticationRequest authRequest = new AuthenticationRequest();

		UserCode uCode = new UserCode(userName);
		UserSearchCriteria usrCriteria = new UserSearchCriteria();
		usrCriteria.setUserCode(uCode);
		authRequest.setUserSearchCriteria(usrCriteria);
		authRequest.setAuthenticationTypeCode(applyAuthTypeCode(authType));
		authRequest.setOneTimePassword(otpValue);
		authRequest.setAuthenticateNoSession(isSessionAvailable);

		if (isNotNull(challengeValue)) {
			if (signMode) {
				AuthenticationRequestParameter param1 = new AuthenticationRequestParameter();
				param1.setName("beneficiaryAccount");
				param1.setValue(challengeValue);
				param1.setOrder(0);
				param1.setSign(true);
				authRequest
						.setParameters(new AuthenticationRequestParameter[] { param1 });
				authRequest
						.setAuthenticationMode(AuthenticationRequest.SIGN_SYNCHRONOUS);
			} else {
				authRequest.setChallenge(challengeValue);
				authRequest
						.setAuthenticationMode(AuthenticationRequest.ASYNCHRONOUS);
				authRequest
						.setParameters(new AuthenticationRequestParameter[] {});
			}
		} else {
			authRequest.setParameters(new AuthenticationRequestParameter[] {});
			authRequest.setAuthenticationMode(getDeviceAuthenticationMode());
		}
		return authRequest;
	}

	/**
	 * @param challengeValue
	 * @return boolean
	 */
	private boolean isNotNull(String challengeValue) {
		return (challengeValue != null) && (challengeValue.trim() != "");
	}

	/**
	 * @return
	 */
	private int getDeviceAuthenticationMode() {
		String configValue = config
				.getConfigValue(AuthenticationServerConfiguration.DEVICE_MODE);

		if (SYNCHRONOUS.equalsIgnoreCase(configValue)) {
			return AuthenticationRequest.SYNCHRONOUS;
		} else if (ASYNCHRONOUS.equalsIgnoreCase(configValue)) {
			return AuthenticationRequest.ASYNCHRONOUS;
		} else {
			return AuthenticationRequest.SYNCHRONOUS;
		}
	}

}
