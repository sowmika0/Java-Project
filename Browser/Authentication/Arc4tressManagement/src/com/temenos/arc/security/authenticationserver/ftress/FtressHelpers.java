package com.temenos.arc.security.authenticationserver.ftress;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;

import com.aspace.ftress.interfaces.ejb.Authenticator;
import com.aspace.ftress.interfaces.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces.ejb.CredentialManager;
import com.aspace.ftress.interfaces.ejb.UserManager;
import com.aspace.ftress.interfaces.ftress.DTO.ALSI;
import com.aspace.ftress.interfaces.ftress.DTO.ALSISession;
import com.aspace.ftress.interfaces.ftress.DTO.Attribute;
import com.aspace.ftress.interfaces.ftress.DTO.AttributeTypeCode;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationRequest;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationResponse;
import com.aspace.ftress.interfaces.ftress.DTO.constants.AuthenticationResponseConstants;
import com.aspace.ftress.interfaces.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces.ftress.DTO.DeviceAuthenticationRequest;
import com.aspace.ftress.interfaces.ftress.DTO.Password;
import com.aspace.ftress.interfaces.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces.ftress.DTO.SeedPositions;
import com.aspace.ftress.interfaces.ftress.DTO.UPAuthenticationRequest;
import com.aspace.ftress.interfaces.ftress.DTO.UPAuthenticator;
import com.aspace.ftress.interfaces.ftress.DTO.User;
import com.aspace.ftress.interfaces.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces.ftress.DTO.UserSearchCriteria;
import com.aspace.ftress.interfaces.ftress.DTO.UserSearchResults;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.AuthenticationTierException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.AuthenticatorException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ChangePasswordFailedException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ConstraintFailedException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InvalidChannelException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.NoFunctionPrivilegeException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.ObjectNotFoundException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.PasswordExpiredException;
import com.aspace.ftress.interfaces.ftress.DTO.exception.SeedingException;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.Authenticatable;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.authenticationserver.ftress.ServiceLocator;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.LoginModuleException;
import com.temenos.arc.security.jaas.T24PasswordCredential;
import com.temenos.arc.security.jaas.T24Principal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Utility class to wrap authentication server specific code.
 * 
 * @author jannadani
 * 
 */
public class FtressHelpers {
	private static AuthenticationServerConfiguration config = null;

	private static Logger logger = LoggerFactory.getLogger(FtressHelpers.class);

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
	public static boolean userExists(ArcSession session, String t24UserName) {
		User[] users = getArcUsers(session, t24UserName);
		if (users != null && users.length > 0) {
			return true;
		} else {
			return false;
		}

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
	public static User[] getArcUsers(ArcSession session, String t24UserName) {
		if (config == null) {
			throw new ArcAuthenticationServerException("config not found");
		}
		User[] users = null;
		AttributeTypeCode typeCode = new AttributeTypeCode(
				config
						.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_T24USER));
		Attribute attr = new Attribute(typeCode, t24UserName);
		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setAttributeCriteria(new Attribute[] { attr });

		ServiceLocator sl = new ServiceLocator();
		UserManager userManager = sl.lookupUserManager();
		try {
			UserSearchResults results = userManager
					.searchUsers(
							new ALSI(session.toString()),
							new ChannelCode(
									config
											.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),
							criteria,
							new SecurityDomain(
									config
											.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
			if (null != results) {
				users = results.getUsers();
			}
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		} catch (ObjectNotFoundException e) {
		} catch (NoFunctionPrivilegeException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			// TODO!!!!!!!!!!!!
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException(e.getMessage());
		}
		return users;
	}

	// TODO remove ArcAuthe...Exception from throws clause
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
	 * @Deprecated
	 */ 
	public final static AuthenticationResponse authenticate(String userId,
			char[] passphrase ) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			ArcAuthenticationServerException {
		logger.debug("Looking up 4TRESS EJB...");
		Authenticator authenticator = new ServiceLocator()
				.lookupAuthenticator();
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(
				userId, passphrase);
		AuthenticationResponse authenticationResponse = null;
		try { // TODO SJP 23/11/2006 Sort out crappy, insecure exception
				// handling
			// This checks if the number of seeds is set and if it is sets the
			// seed positions on the request
			// Also resets the password according to the seeding with a String
			// containing just the seed characters.
			logger.info("Attempting to authenticate: user: " + userId);
			int numSeeds = getNumSeedsFromConfig();
			logger.debug("number of seeds: " + numSeeds);
			if (0 != numSeeds) {
				logger.info("Using seeding, no of seeds are : " + numSeeds);
				String passphraseString = new String(passphrase);
				String seedDelimiter = config
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM);
				if (logger.isDebugEnabled())
					logger.debug("delimiter: " + seedDelimiter);

				StringTokenizer tokenizer = new StringTokenizer(
						passphraseString, seedDelimiter);
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
			}
			logger.info("Now authenticating ");
			authenticationResponse = authenticator.primaryAuthenticateUP(
					channelCode, authenticationRequest, securityDomain);
			logger.info("Successfully authenticated for user: " + userId);
		} catch (InvalidChannelException e) {
			throw new ArcAuthenticationServerException(
					"Channel "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.CHANNEL)
							+ " incorrectly configured");
		} catch (AuthenticationTierException e) {
			throw new ArcAuthenticationServerException(
					"Authentication Type "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE)
							+ " incorrectly configured");
		} catch (PasswordExpiredException e) {
			throw new AccountExpiredException(e.toString());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new FailedLoginException(e.toString());
		} catch (SeedingException e) {
			throw new FailedLoginException(e.toString());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}

		return authenticationResponse;
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
	public final static AuthenticationResponse authenticate(String userId,
			char[] passphrase , String authType) throws ArcAuthenticationServerException,
			FailedLoginException, AccountExpiredException,
			ArcAuthenticationServerException {
		logger.debug("Looking up 4TRESS EJB...");
		Authenticator authenticator = new ServiceLocator()
				.lookupAuthenticator();
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(
				userId, passphrase, authType);
		AuthenticationResponse authenticationResponse = null;
		try { // TODO SJP 23/11/2006 Sort out crappy, insecure exception
				// handling
			// This checks if the number of seeds is set and if it is sets the
			// seed positions on the request
			// Also resets the password according to the seeding with a String
			// containing just the seed characters.
			logger.info("Attempting to authenticate: user: " + userId);
			int numSeeds = getNumSeedsFromConfig();
			logger.debug("number of seeds: " + numSeeds);
			if (0 != numSeeds) {
				logger.info("Using seeding, no of seeds are : " + numSeeds);
				String passphraseString = new String(passphrase);
				String seedDelimiter = config
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM);
				if (logger.isDebugEnabled())
					logger.debug("delimiter: " + seedDelimiter);

				StringTokenizer tokenizer = new StringTokenizer(
						passphraseString, seedDelimiter);
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
			}
			logger.info("Now authenticating ");
			authenticationResponse = authenticator.primaryAuthenticateUP(
					channelCode, authenticationRequest, securityDomain);
			logger.info("Successfully authenticated for user: " + userId);
		} catch (InvalidChannelException e) {
			throw new ArcAuthenticationServerException(
					"Channel "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.CHANNEL)
							+ " incorrectly configured");
		} catch (AuthenticationTierException e) {
			throw new ArcAuthenticationServerException(
					"Authentication Type "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE)
							+ " incorrectly configured");
		} catch (PasswordExpiredException e) {
			throw new AccountExpiredException(e.toString());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new FailedLoginException(e.toString());
		} catch (SeedingException e) {
			throw new FailedLoginException(e.toString());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}

		return authenticationResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.temenos.arc.security.jaas.AbstractAuthenticator#buildRequest()
	 */ 
	private static final AuthenticationRequest buildRequest(String userId,
			char[] passphrase) throws ArcAuthenticationServerException {
		UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest();
		AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(
				config
						.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
		authenticationRequest.setAuthenticationTypeCode(authenticationTypeCode);
		authenticationRequest.setAuthenticateNoSession(config
				.dontCreateSession()); // TODO SJP 23/11/2006 Confirm what this
										// does!
		authenticationRequest.setUsername(userId);
		authenticationRequest.setPassword(encodedPassphrase(passphrase));
		return authenticationRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.temenos.arc.security.jaas.AbstractAuthenticator#buildRequest()
	 */
	private static final AuthenticationRequest buildRequest(String userId,
			char[] passphrase, String authType)
			throws ArcAuthenticationServerException {
		UPAuthenticationRequest authenticationRequest = new UPAuthenticationRequest();
		AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(
				authType);
		authenticationRequest.setAuthenticationTypeCode(authenticationTypeCode);
		authenticationRequest.setAuthenticateNoSession(false); 
		authenticationRequest.setUsername(userId);
		authenticationRequest.setPassword(encodedPassphrase(passphrase));
		return authenticationRequest;
	}

	// TODO SJP 24/11/2006 Is this redundant, or is character encoding something
	// to worry about?
	private static String encodedPassphrase(char[] passphrase) {
		// String charset = this.jaasOption(LoginModuleConstant.CHARSET);
		return new String(passphrase);
	}

	public static AbstractAuthenticator loginWithUPAuthenticator(String name,
			String password, AuthenticationServerConfiguration config) {
		NameCallback nameC = new NameCallback("not used");
		nameC.setName(name);
		boolean dummy = false;
		PasswordCallback passwordC = new PasswordCallback("not used", dummy);
		passwordC.setPassword(password.toCharArray());

		AbstractAuthenticator auth = null;
		auth = new UsernamePasswordAuthenticator(nameC, passwordC, config);
		try {
			auth.authenticate();
		} catch (FailedLoginException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (AccountExpiredException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		return auth;
	}

	/**
	 * Returns an array of positions of characters to be asked for a future mem
	 * word authentication. The number of these is determined by config.
	 * 
	 * @param userId
	 * @return array of seed positions
	 */
	public static int[] getMemWordSeedPositions(String userId) {
		int numSeeds = getNumSeedsFromConfig();
		int result[] = new int[numSeeds];
		logger.info("No Of Seeds : " + numSeeds + "for User ID : " + userId);
		Authenticator authenticator = new ServiceLocator()
				.lookupAuthenticator();
		try {
			SeedPositions seeds = authenticator
					.getPasswordSeedPositions(
							new ChannelCode(
									config
											.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),
							new UserCode(userId),
							new AuthenticationTypeCode(
									config
											.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE)),
							numSeeds,
							new SecurityDomain(
									config
											.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
			result = seeds.getPositions();
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (PasswordExpiredException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		return result;
	}

	public static boolean changeOwnExpiredPassword(String userId,
			String oldPassword, String newPassword) {
		CredentialManager cm = new ServiceLocator().lookupCredentialManager();
		UPAuthenticationRequest request = (UPAuthenticationRequest) buildRequest(
				userId,oldPassword.toCharArray());
		boolean changed;
		try {
			
			changed = cm.changeOwnExpiredPassword(request,new ChannelCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),new Password(newPassword),
							new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
			if(!changed){
				logger.error("Password Not Changed");
				return false;
			}
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ConstraintFailedException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidChannelException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ChangePasswordFailedException e) {
			logger
					.info("Error when changing expired password, attempting to change fist use password");
			// try changing a first time login password instead
			boolean returnValue = changeOwnPassword(userId, oldPassword,
					newPassword);
			// if successful password change, then blank the password attribute
			setPasswordAttribute("", userId, newPassword);
			return returnValue;
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		logger.info("Successfully changed password");
		return true;
	}
/**
 * This method changes the expired password in authentication server.
 * @param userId
 * @param oldPassword
 * @param newPassword
 * @param authType
 * @return
 */	
	public static boolean changeOwnExpiredPassword(String userId, String oldPassword, String newPassword, String authType){
		CredentialManager cm = new ServiceLocator().lookupCredentialManager();
		UPAuthenticationRequest request = (UPAuthenticationRequest) buildRequest(
				userId, oldPassword.toCharArray(),authType);
		boolean changed;
		try {
			changed = cm.changeOwnExpiredPassword( request, new ChannelCode( config.getConfigValue(AuthenticationServerConfiguration.CHANNEL)), new Password(newPassword), new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
			if(!changed){
				logger.error("Password Not Changed Try again");
				return false;
			}
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ConstraintFailedException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidChannelException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ChangePasswordFailedException e) {
			logger
					.info("Error when changing expired password, attempting to change fist use password");
			// try changing a first time login password instead
			boolean returnValue = changeOwnPassword(userId, oldPassword,
					newPassword, authType);
			// if successful password change, then blank the password attribute
			setPasswordAttribute("", userId, newPassword, authType);
			return returnValue;
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		logger.info("Successfully changed password");
		return true;
	}
	
/**
 * 
 * @param userId
 * @param oldPassword
 * @param newPassword
 * @return
 * @deprecated
 */
	public static boolean changeOwnPassword(String userId, String oldPassword,
			String newPassword) {
		CredentialManager cm = new ServiceLocator().lookupCredentialManager();
		AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(
				config
						.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE));
		AuthenticationResponse response = null;
		try {
			response = authenticate(userId, oldPassword.toCharArray());
		} catch (FailedLoginException e) {
			logger.error(e.getMessage());
			return false;
		} catch (AccountExpiredException e) {
			logger.error(e.getMessage());
			return false;
		}
		try {
			cm
					.changeOwnPassword(
							response.getAlsi(),
							new ChannelCode(
									config
											.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),
							authenticationTypeCode,
							new Password(newPassword),
							new Password(oldPassword),
							new SecurityDomain(
									config
											.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ConstraintFailedException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidChannelException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
			// } catch (ChangePasswordFailedException e) {
			// logger.info("Error when changing expired password");
			// logger.error(e.getMessage(),e);
			// // try changing a max usage password instead
			//        	
			// return false;
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (PasswordExpiredException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (AuthenticatorException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		logger.info("Successfully changed password");
		return true;
	}

	
	/**
	 * This method changes the password in authentication server.
	 * This accepts authentication type as an extra parameter.
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 *
	 */
		public static boolean changeOwnPassword(String userId, String oldPassword,
				String newPassword, String authType) {
			CredentialManager cm = new ServiceLocator().lookupCredentialManager();
			AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(authType);
			AuthenticationResponse response = null;
			try {
				response = authenticate(userId, oldPassword.toCharArray(), authType);
			} catch (FailedLoginException e) {
				logger.error(e.getMessage());
				return false;
			} catch (AccountExpiredException e) {
				logger.error(e.getMessage());
				return false;
			}
			try {
				cm
						.changeOwnPassword(
								response.getAlsi(),
								new ChannelCode(
										config
												.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),
								authenticationTypeCode,
								new Password(newPassword),
								new Password(oldPassword),
								new SecurityDomain(
										config
												.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
			} catch (ObjectNotFoundException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (NoFunctionPrivilegeException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.getMessage());
			} catch (ALSIInvalidException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.getMessage());
			} catch (InvalidParameterException e) {
				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.toString());
			} catch (InternalException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (RemoteException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (ConstraintFailedException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (InvalidChannelException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
				// } catch (ChangePasswordFailedException e) {
				// logger.info("Error when changing expired password");
				// logger.error(e.getMessage(),e);
				// // try changing a max usage password instead
				//        	
				// return false;
			} catch (SeedingException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (PasswordExpiredException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			} catch (AuthenticatorException e) {
				logger.error(e.getMessage(), e);
				throw new ArcAuthenticationServerException(e.toString());
			}
			logger.info("Successfully changed password");
			return true;
		}

		
		/**
	     * This method is specific only to change the PIN of a user.
	     * Authentication Type code AT_CUSTPIN will be hardcoded for changing the PIN.
	     * Method to change the pin after the user has logged into the system
	     * This method will be called only from FtressChangePinServlet class.
	     * This method will validate the old pin. If the old pin is valid
	     * alsi will be created, using this alsi new pin will be updated for 
	     * the user
	     *
	     * 
	     * @Param userId - External user id of the user
	     * @param oldPassword - Old Pin of the user
	     * @Param newPassword - New Pin of the user
	     * @return boolean - true -Pin changed Successfully  
	     */
	    
	    public static boolean changeOwnPin(String userId, String oldPassword, String newPassword) {
	        CredentialManager cm = new ServiceLocator().lookupCredentialManager();
	        String authType = config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN);
	        AuthenticationTypeCode authenticationTypeCode = new AuthenticationTypeCode(authType);
	        AuthenticationResponse response = null;
	        try {
	        	response = authenticate(userId, oldPassword.toCharArray(), authType);
	        } catch (FailedLoginException e) {
	        	logger.error(e.getMessage());
	        	return false;
	        } catch (AccountExpiredException e) {
	        	logger.error(e.getMessage());
	        	return false;
	        } 
	        try {
	             	cm.changeOwnPassword(response.getAlsi(), 
	            		new ChannelCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL)),
	            		authenticationTypeCode,
	                    new Password(newPassword), 
	                    new Password(oldPassword), 
	                    new SecurityDomain(config.getConfigValue(AuthenticationServerConfiguration.DOMAIN)));
	             	
	        } catch (ObjectNotFoundException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (NoFunctionPrivilegeException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.getMessage());
	        } catch (ALSIInvalidException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.getMessage());
	        } catch (InvalidParameterException e) {
	            logger.error(e.getMessage(), e);
	            throw new IllegalArgumentException(e.toString());
	        } catch (InternalException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (RemoteException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (ConstraintFailedException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (InvalidChannelException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (SeedingException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (PasswordExpiredException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        } catch (AuthenticatorException e) {
	            logger.error(e.getMessage(), e);
	            throw new ArcAuthenticationServerException(e.toString());
	        }
	        logger.info("Successfully changed password");
	        return true;
	    }
	    
	/**
	 * Method to check if a password for a user has expired.
	 * 
	 * For 4TRESS this could either be that the user is logging in for the 1st
	 * time, or that the password has actually expired.
	 * 
	 * If logging in for first time, this method checks if the password field
	 * contains a value in the ATR_CUSTPW field.
	 * @deprecated
	 * 
	 */
	public static boolean hasPasswordExpired(String userId, String password) {

		boolean returnValue = false;
		ServiceLocator sl = new ServiceLocator();
		Authenticator authenticator = sl.lookupAuthenticator();
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(
				userId, password.toCharArray());
		AuthenticationResponse authenticationResponse = null;
		try {
			authenticationResponse = authenticator.primaryAuthenticateUP(
					channelCode, authenticationRequest, securityDomain);
		} catch (InvalidChannelException e) {
			throw new ArcAuthenticationServerException(
					"Channel "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.CHANNEL)
							+ " incorrectly configured");
		} catch (AuthenticationTierException e) {
			throw new ArcAuthenticationServerException(
					"Authentication Type "
							+ config
									.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE)
							+ " incorrectly configured");
		} catch (PasswordExpiredException e) {
			logger.info("Password has expired, go to change password screen");
			returnValue = true;
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new ArcAuthenticationServerException(e.toString());
		} catch (SeedingException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		if (null != authenticationResponse) {
			if (authenticationResponse.getReason() == AuthenticationResponseConstants.REASON_PASSWORD_MAX_USAGES_REACHED) {
				returnValue = true;
			}
		}
		//If User Entered Password is mismatch then Authentication Error will be raised.
		if(authenticationResponse.getReason() == AuthenticationResponseConstants.REASON_PASSWORD_MISMATCH){
			logger.debug("Authentication Failed : Invalid password");
			returnValue = false;
			throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
			
		}
	    //If the ALSI is NULL then this next step will fail.  Check if the ALSI is null before continuing.
		ALSI alsi = authenticationResponse.getAlsi();
		if (null==alsi) {
			String e="Authentication Failed : Returned ALSI is null";
			logger.info(e);
			throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
		}
		return checkArcPasswordAttribute(alsi,userId);
	}

	/**
	 * Method to check if a password for a user has expired.
	 * 
	 * For 4TRESS this could either be that the user is logging in for the 1st
	 * time, or that the password has actually expired.
	 * 
	 * If logging in for first time, this method checks if the password field
	 * contains a value in the ATR_CUSTPW field.
	 * 
	 * 
	 */
	public static boolean hasPasswordExpired(String userId, String password,
			String authType) {

		boolean returnValue = false;
		ServiceLocator sl = new ServiceLocator();
		Authenticator authenticator = sl.lookupAuthenticator();
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		UPAuthenticationRequest authenticationRequest = (UPAuthenticationRequest) buildRequest(
				userId, password.toCharArray(), authType);
		AuthenticationResponse authenticationResponse = null;
		try {
			authenticationResponse = authenticator.primaryAuthenticateUP(
					channelCode, authenticationRequest, securityDomain);
		} catch (InvalidChannelException e) {
			logger.error("Channel "
					+ config
					.getConfigValue(AuthenticationServerConfiguration.CHANNEL)
			+ " incorrectly configured");
			throw new ArcAuthenticationServerException(
					"Channel incorrectly configured");
		} catch (AuthenticationTierException e) {
			logger.error("Authentication Type "
					+ authType + " incorrectly configured");
			throw new ArcAuthenticationServerException("Authentication Type incorrectly configured");
		} catch (PasswordExpiredException e) {
			logger.info("Password has expired, go to change password screen");
			return true;
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
		} catch (SeedingException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		}
		if (null != authenticationResponse) {
			if (authenticationResponse.getReason() == AuthenticationResponseConstants.REASON_PASSWORD_MAX_USAGES_REACHED) {
				returnValue = true;
			}
			//If User Entered Password is mismatch then Authentication Error will be raised.
			if(authenticationResponse.getReason() == AuthenticationResponseConstants.REASON_PASSWORD_MISMATCH){
				logger.debug("Authentication Failed : User entered password mismatch");
				returnValue = false;
				throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
			}
		}
		
		//If the ALSI is NULL then this next step will fail.  Check if the ALSI is null before continuing.
		ALSI alsi = authenticationResponse.getAlsi();
		if (null==alsi) {
			String e="Authentication Failed : Returned ALSI is null";
			logger.info(e);
			throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
		}
		
		if (!authType.equals(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))){
			return returnValue;			
		}
	return checkArcPasswordAttribute(alsi,userId);
	}

	/**
	 * @deprecated
	 * 
	 */
	
	public static void setPasswordAttribute(String attributeValue,
			String userId, String password) {
		AuthenticationResponse authenticationResponse = null;
		try {
			authenticationResponse = authenticate(userId, password
					.toCharArray());
		} catch (FailedLoginException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (AccountExpiredException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		ServiceLocator sl = new ServiceLocator();
		UserManager um = sl.lookupUserManager();
		ALSI alsi = authenticationResponse.getAlsi();
		UserCode userCode = new UserCode(userId);
		Attribute attribute = new Attribute(new AttributeTypeCode(config
				.getConfigValue(JaasConfiguration.ATTRIBUTE_ARC_PASSWORD)), "");
		Attribute[] attributes = new Attribute[] { attribute };
		try {
			um.updateUserAttributes(alsi, channelCode, userCode, attributes,
					securityDomain);
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}

	}
/**
 * 
 * @param attributeValue
 * @param userId
 * @param password
 * @param authType
 */

	public static void setPasswordAttribute(String attributeValue,
			String userId, String password , String authType) {
		AuthenticationResponse authenticationResponse = null;
		try {
			authenticationResponse = authenticate(userId, password
					.toCharArray(), authType);
		} catch (FailedLoginException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (AccountExpiredException e) {
			logger.info(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		ServiceLocator sl = new ServiceLocator();
		UserManager um = sl.lookupUserManager();
		ALSI alsi = authenticationResponse.getAlsi();
		UserCode userCode = new UserCode(userId);
		Attribute attribute = new Attribute(new AttributeTypeCode(config
				.getConfigValue(JaasConfiguration.ATTRIBUTE_ARC_PASSWORD)), "");
		Attribute[] attributes = new Attribute[] { attribute };
		try {
			um.updateUserAttributes(alsi, channelCode, userCode, attributes,
					securityDomain);
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}

	}

	/**
	 * Common implementation of {@link Authenticatable#logoff()} for all 4TRESS
	 * authenticators (invalidates the <code>ArcSession</code>)
	 */
	public static boolean logoff(ArcSession session)
			throws ArcAuthenticationServerException {
		Authenticator authenticator = lookupAuthenticator();
		ALSI alsi = new ALSI(session.toString());
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		try {
			authenticator.logout(alsi, channelCode, securityDomain);
		} catch (NoFunctionPrivilegeException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ALSIInvalidException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (ObjectNotFoundException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		return true;
	}

	/**
	 * Helper to interpret the 4TRESS server's response to the authentication
	 * attempt. If successful, then the returned <code>ArcSession</code> will
	 * be non-null. If unsuccessful, an exception is thrown.
	 * 
	 * @param authenticationResponse
	 * @throws ArcAuthenticationServerException
	 * @throws FailedLoginException
	 */
	public static ArcSession handleResponse(
			AuthenticationResponse authenticationResponse,
			AuthenticationServerConfiguration configToUse)
			throws ArcAuthenticationServerException, FailedLoginException {
		switch (authenticationResponse.getResponse()) {
		case AuthenticationResponse.RESPONSE_AUTHENTICATION_SUCCEEDED:
			// TODO check that this helper is necessary:
			// could we not infer this from the presence of session?
			if (configToUse.dontCreateSession()) {
				logger.info("Not creating a new session");
				return null;
			} else {
				ALSI alsi = authenticationResponse.getAlsi();
				if (alsi == null) {
					throw new ArcAuthenticationServerException(
							"Session identifier not generated by 4TRESS");
				}
				return new ArcSession(alsi.getAlsi());
			}
		case AuthenticationResponse.RESPONSE_AUTHENTICATION_FAILED:
			throw new FailedLoginException(
					"Authentication failed due to 4TRESS 'reason code': "
							+ authenticationResponse.getReason());
		default:
			throw new ArcAuthenticationServerException(
					"Unknown response code: "
							+ authenticationResponse.getResponse());
		}
	}

	public static Authenticator lookupAuthenticator()
			throws ArcAuthenticationServerConnectionException {
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

	public static boolean refreshSession(ArcSession arcSession)
			throws ArcAuthenticationServerException {
		if (arcSession == null) {
			throw new IllegalStateException("Session identifier expected");
		}
		Authenticator authenticator = FtressHelpers.lookupAuthenticator();
		ALSI alsi = new ALSI(arcSession.toString());
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		boolean result = true;
		try {
			ALSISession session = authenticator.getSessionData(alsi,
					securityDomain);
			// TODO SJP 06/12/2006 Probably worth doing a more rigorous check
			// for session validity
			result = (session != null);
		} catch (ALSIInvalidException e) {
			// ALSI has expired, been invalidated, never existed, etc.
			result = false;
		} catch (ObjectNotFoundException e) {
			// Something wasn't found, which indicates something a bit wrong
			result = false;
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
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
	public static void addImpersonationCredentialsTo(final Subject subject)
			throws LoginModuleException, FailedLoginException {
		// 1. Find UserCode for the Principal added to the Subject by the
		// LoginModule stacked above the one that invoked this
		// object.
		UserCode userCode = retrieveUserCode(subject
				.getPublicCredentials(ArcSession.class));
		//Check name has not been modified
		Set principalSet = subject.getPrincipals(ArcUserPrincipal.class);
		if(principalSet.size()!= 1){
			throw new LoginModuleException(
			"Cannot find User");
		}
		ArcUserPrincipal aup = (ArcUserPrincipal)principalSet.iterator().next();
		if (!aup.getName().equals(userCode.getCode())) {
			throw new FailedLoginException("Error in authenticating User: " + aup.getName());
		}
		// 2. Use the Principal added to the state of the LoginModule that
		// invoked this object.
		// Switched the credentials so that the T24 user /pw details are
		// obtained using the
		// User user = this.retrieveUser(credentials, userCode);
		User user = retrieveUser(
				subject.getPublicCredentials(ArcSession.class), userCode);
		// 3. Add the attributes that represent the T24 u/n+p/w into the
		// Subject, for use in impersonation later.
		Attribute[] attributes = user.getAttributes();
		boolean userAttributeCommitted = false;
		boolean passwordAttributeCommitted = false;
		for (int i = 0; i < attributes.length; i++) {

			if (isConfiguredAttribute(JaasConfiguration.ATTRIBUTE_T24USER,
					attributes[i])) {
				if (logger.isInfoEnabled())
					logger.info("adding t24 user to credentials:"
							+ attributes[i].getValue());
				subject.getPrincipals().add(
						new T24Principal(attributes[i].getValue()));
				userAttributeCommitted = true;
			}
			if (isConfiguredAttribute(JaasConfiguration.ATTRIBUTE_T24PASS,
					attributes[i])) {
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
	 * @param authenticator
	 * @param alsi
	 * @param securityDomain
	 * @return
	 * @throws FailedLoginException
	 * @throws AuthenticationServerException
	 */
	private static ALSISession retrieveRemoteSession(
			final Authenticator authenticator, final ALSI alsi,
			final SecurityDomain securityDomain) throws FailedLoginException,
			ArcAuthenticationServerException {
		try {
			return authenticator.getSessionData(alsi, securityDomain);
		} catch (ALSIInvalidException e) {
			throw new FailedLoginException(e.toString());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 04/12/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new FailedLoginException(e.toString());
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			throw new ArcAuthenticationServerException(e.toString());
		}
	}

	/**
	 * @param userManager
	 * @param alsi
	 * @param securityDomain
	 * @param channelCode
	 * @param userCode
	 * @return
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private static User retrieveRemoteUser(final UserManager userManager,
			final ALSI alsi, final SecurityDomain securityDomain,
			final ChannelCode channelCode, final UserCode userCode)
			throws ArcAuthenticationServerException, FailedLoginException {
		try {
			return userManager.getUser(alsi, channelCode, userCode,
					securityDomain);
		} catch (ALSIInvalidException e) {
			throw new FailedLoginException(e.toString());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 04/12/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new FailedLoginException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException(e.toString());
		} catch (InternalException e) {
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			throw new ArcAuthenticationServerException(e.toString());
		}
	}

	/**
	 * @param credentials
	 * @param userCode
	 * @return
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private static User retrieveUser(final Set credentials,
			final UserCode userCode) throws ArcAuthenticationServerException,
			FailedLoginException {
		ArcSession identifier = sessionIdentifierFrom(credentials);
		UserManager userManager = new ServiceLocator().lookupUserManager();
		ALSI alsi = new ALSI(identifier.toString());
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(JaasConfiguration.DOMAIN));
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(JaasConfiguration.CHANNEL));
		return retrieveRemoteUser(userManager, alsi, securityDomain,
				channelCode, userCode);
	}

	/**
	 * @param credentials
	 * @return
	 * @throws AuthenticationServerException
	 * @throws FailedLoginException
	 */
	private static UserCode retrieveUserCode(final Set credentials)
			throws ArcAuthenticationServerException, FailedLoginException {
		ArcSession identifier = sessionIdentifierFrom(credentials);
		Authenticator authenticator = new ServiceLocator()
				.lookupAuthenticator();
		ALSI alsi = new ALSI(identifier.toString());
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(JaasConfiguration.DOMAIN));
		ALSISession session = FtressHelpers.retrieveRemoteSession(
				authenticator, alsi, securityDomain);
		return session.getUserCode();
	}

	/**
	 * @param credentials
	 * @return
	 */
	private static ArcSession sessionIdentifierFrom(final Set credentials) {
		if (credentials.isEmpty()) {
			throw new IllegalArgumentException("Session Credentials required");
		}
		ArcSession arcSession = null;
		for (Iterator iter = credentials.iterator(); iter.hasNext();) {
			Object obj = (Object) iter.next();
			if (obj instanceof ArcSession) {
				arcSession = (ArcSession) obj;
				break;
			}
		}
		if (arcSession == null) {
			throw new IllegalStateException("ArcSession required");
		}
		return arcSession;
	}

	private static boolean isConfiguredAttribute(final String key,
			final Attribute attribute) {
		AttributeTypeCode atCode = attribute.getTypeCode();
		String atString = atCode.getCode();
		String optionValue = config.getConfigValue(key);
		return optionValue.equals(atString);
	}

	/**
	 * @return 0 if config says mem word is not seeded, otherwise the number of
	 *         seeds
	 */
	private static int getNumSeedsFromConfig() {
		
		String sAuthType = config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE);
		
		if (!"true"
				.equalsIgnoreCase(config
						.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_IS_SEEDED))) {
			logger.info("AuthType : " + sAuthType + "is not seeded");
			return 0;
		}

		String numSeedsParam = config
				.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_NUM_SEEDS);
		logger.info("AuthType : " + sAuthType + "is not seeded and No of Seeds = " + numSeedsParam);
		return Integer.parseInt(numSeedsParam);
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
	 *            The username for which authenticationtype mapping is being searched
	 * @return true if the user mapped to specified authentication type in the Authentication Server, false
	 *         otherwise
	 * @throws ArcAuthenticationServerConnectionException
	 *             if there are problems connecting to the Authentication Server
	 * @throws ArcAuthenticationServerException
	 *             if there are any other problems in the Authentication Server
	 */
	
	public static boolean isAuthenticationTypeExists(ArcSession session, String authType, String userName) {
		boolean returnValue = false ;
		ALSI alsiValue = null;
		try {
			String ftressUserName = "";
			String ftressPassword = "";
			CryptographyService  cryptoService;
			
			ServiceLocator sl = new ServiceLocator();
			
			ChannelCode channelCode = new ChannelCode("CH_DIRECT");
			SecurityDomain securityDomain = new SecurityDomain(config
					.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
			
			if (session == null) {
				String name = config
				.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER);
				
				String password = config
				.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD);
				
				cryptoService = CryptographyService.getInstance(config);
				
				ftressUserName = cryptoService.decrypt(
						name, true);			
				
				ftressPassword = cryptoService.decrypt(
						password, true);
				
				cryptoService.close();
				
				Authenticator auth = sl.lookupAuthenticator();
				
				UPAuthenticationRequest upRequest = new UPAuthenticationRequest();
				upRequest.setUsername(ftressUserName);
				upRequest.setPassword(ftressPassword);
				
				String systemAuthType  = config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE_SYSTEM);
				if( systemAuthType == null || systemAuthType == "" ){
					systemAuthType = config.getConfigValue(AuthenticationServerConfiguration.AUTHENTICATION_TYPE);
				}
				AuthenticationTypeCode atTypeCode = new AuthenticationTypeCode(systemAuthType);
				upRequest.setAuthenticationTypeCode(atTypeCode);
				
				AuthenticationResponse authResponse;
				
				try {
					authResponse = auth.primaryAuthenticateUP(channelCode, upRequest, securityDomain);
				} catch (InvalidChannelException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				} catch (AuthenticationTierException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				} catch (PasswordExpiredException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Authentication Failure : User Credentials mismatch");
				} catch (ObjectNotFoundException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Authentication Failure : User Credentials mismatch");
				} catch (SeedingException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				} catch (InvalidParameterException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				} catch (InternalException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				} catch (RemoteException e) {
					logger.error(e.getMessage(), e);
					throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
				}

				if (authResponse != null) {
					alsiValue = authResponse.getAlsi();
				}
			}
			
			AuthenticatorManager authManager = sl.lookupAuthenticatorManager();

			UPAuthenticator upAuthenticators[] = authManager.getAllUPAuthenticatorsForUser(alsiValue, channelCode, new UserCode(userName), securityDomain);
			
			if (upAuthenticators != null) {
				logger.debug("Number of Authenticators found for User:" + userName + " is " + upAuthenticators.length);
				
				for (int i =0; i<upAuthenticators.length; i++) {
					UPAuthenticator upAuth = upAuthenticators[i];
					logger.debug("AutheticationType value : " + upAuth.getAuthenticationTypeCode().getCode());
					String upAuthTypeValue = upAuth.getAuthenticationTypeCode().getCode();
					if (upAuthTypeValue.equals(authType)) {
						returnValue = true;
						break;
					}
				}
			}
		}
		catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Authentication Failure : User Credentials mismatch");
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure: Problem occured while connecting to server please try later");
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerConnectionException("Connection Failure: Problem occured while connecting to server please try later");
		}
		return returnValue;
	}
	public final static AuthenticationResponse authenticateMemWord(String userId, char[] memwordphrase) throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException,
    ArcAuthenticationServerException, AccountLockedException { 
    	AuthenticationResponse authResponse = authenticate(userId, memwordphrase);
    	if (authResponse.getAlsi()== null) {
    		int authResponseReason = authResponse.getReason();
    		if (authResponseReason == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
    			throw new AccountLockedException("Maximum Consecutive Failure Count Reached for MemorableWord with reason : " + authResponseReason);
    		}
    		if (authResponseReason != AuthenticationResponse.VALUE_NOT_DEFINED) {
    			throw new ArcAuthenticationServerException("Authentication failed for MemorableWord with reason : " + authResponseReason);
    		}
    	}
    	return authResponse;
    }
	
	public final static AuthenticationResponse authenticatePassWord(String userId, char[] password) throws ArcAuthenticationServerException, FailedLoginException, AccountExpiredException, AccountLockedException,
    ArcAuthenticationServerException { 
		AuthenticationResponse authResponse = null;
		try{
			authResponse = authenticate(userId, password);
			ALSI alsi = authResponse.getAlsi();
			if (alsi == null) {
	    		int authResponseReason = authResponse.getReason();
	    		logger.info("Authentication Failed : Returned ALSI is null");
	    		if (authResponseReason == AuthenticationResponse.REASON_MAX_CONSECUTIVE_FAILED_REACHED) {
	    			throw new AccountLockedException("Maximum Consecutive Failure Count Reached for PassWord with reason : " + authResponseReason);
	    		}
	    		else{
	    			throw new ArcAuthenticationServerException("Authentication Failed : User Credentials mismatch");
	    		}
	    	} 
			//This is to check the First Time Login
			if(checkArcPasswordAttribute(alsi,userId)){
				throw new AccountExpiredException("Password Has been expired: Please change the password");
			}
		}
		catch(AccountExpiredException ex){
			logger.error(ex);
			throw new AccountExpiredException("Password Has been expired: "+ ex);
		}
    	return authResponse;
    }
	
	private static Attribute[] retrieveUserAttributes(ALSI alsi, String userId) {
		ServiceLocator sl = new ServiceLocator();
		SecurityDomain securityDomain = new SecurityDomain(config
				.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
		ChannelCode channelCode = new ChannelCode(config
				.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
		UserManager um = sl.lookupUserManager();
		UserCode userCode = new UserCode(userId);
		User user = null;
		Attribute[] attributes = null;
		try {
			user = um.getUser(alsi, channelCode, userCode, securityDomain);
			attributes = user.getAttributes();
		} catch (InvalidParameterException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.toString());
		} catch (ALSIInvalidException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			// TODO SJP 24/11/2006 Possible bug that unknown username causes
			// this exception rather than just returning a response..?
			throw new ArcAuthenticationServerException(e.toString());
		} catch (NoFunctionPrivilegeException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.getMessage());
		} catch (InternalException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		return attributes;
	}
	
	private static boolean checkArcPasswordAttribute(ALSI alsi, String userId) {
		
		String passwordAttribute = "";
		boolean passwordExists = false;
		
		Attribute[] attributes = retrieveUserAttributes(alsi, userId);
		
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				if (isConfiguredAttribute(JaasConfiguration.ATTRIBUTE_ARC_PASSWORD,
						attributes[i])) {
					if (logger.isInfoEnabled())
						logger.info("found password attribute:"
								+ attributes[i].getValue());
					passwordAttribute = attributes[i].getValue();
					}
			}
			
			if(null == passwordAttribute || passwordAttribute.equals(""))
	        {
				passwordExists = false;
	        } else
	        {
	        	passwordExists = true;
	        } 
		}
	return passwordExists;
	}
	

}
