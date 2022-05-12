package com.temenos.arc.security.authenticationserver.ftress.server;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;

import com.aspace.ftress.interfaces70.ejb.Auditor;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces70.ejb.CredentialManager;
import com.aspace.ftress.interfaces70.ejb.UserManager;
import com.aspace.ftress.interfaces70.ftress.DTO.ALSI;
import com.aspace.ftress.interfaces70.ftress.DTO.Attribute;
import com.aspace.ftress.interfaces70.ftress.DTO.AttributeTypeCode;
import com.aspace.ftress.interfaces70.ftress.DTO.AuditRecord;
import com.aspace.ftress.interfaces70.ftress.DTO.AuditSearchCriteria;
import com.aspace.ftress.interfaces70.ftress.DTO.AuditSearchResults;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticationTypeCode;
import com.aspace.ftress.interfaces70.ftress.DTO.AuthenticatorStatus;
import com.aspace.ftress.interfaces70.ftress.DTO.ChannelCode;
import com.aspace.ftress.interfaces70.ftress.DTO.FtressAuthenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.GroupCode;
import com.aspace.ftress.interfaces70.ftress.DTO.Password;
import com.aspace.ftress.interfaces70.ftress.DTO.PasswordResetRequest;
import com.aspace.ftress.interfaces70.ftress.DTO.SecurityDomain;
import com.aspace.ftress.interfaces70.ftress.DTO.UPAuthenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.User;
import com.aspace.ftress.interfaces70.ftress.DTO.UserCode;
import com.aspace.ftress.interfaces70.ftress.DTO.device.Authenticator;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ALSIInvalidException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.AuthenticatorException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ConstraintFailedException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.CreateDuplicateException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InternalException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InvalidChannelException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.InvalidParameterException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.NoFunctionPrivilegeException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.ObjectNotFoundException;
import com.aspace.ftress.interfaces70.ftress.DTO.exception.PasswordResetException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.authenticationserver.common.GenericAuthenticationResponse;
import com.temenos.arc.security.authenticationserver.common.StringGenerator;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.arc.security.authenticationserver.ftress.ServiceLocator70;
import com.temenos.arc.security.authenticationserver.server.ArcUserManagement;
import com.temenos.arc.security.authenticationserver.server.ArcUserStateException;
import com.temenos.arc.security.authenticationserver.server.AuditLog;
import com.temenos.arc.security.authenticationserver.server.XmlUserManagement;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * T24 server side user management functions to wrap the authentication server
 * interface
 * 
 * @author jannadani
 * 
 */
public class UserManagement implements ArcUserManagement {
	private static final String DISABLED = "DISABLED";
	private static final String ARC = "ARC";
	private static final String ARC_CONFIG_PATH = "ARC_CONFIG_PATH";
	private static final String ENABLED = "ENABLED";
	private static final String USG_CUST = "USG_CUST";
	private static String passwordAttributeType = "";
	private static String memWordAttributeType = "";
	private AuthenticationServerConfiguration config;
	private CryptographyService crypto;
	private Logger logger;
	private static ServiceLocator70 serviceLocator70Instance = null;
	private ChannelCode channelCode = null;
	private SecurityDomain securityDomain = null;

	private static ServiceLocator70 getserviceLocator70Instance() {
		if (serviceLocator70Instance == null) {
			serviceLocator70Instance = new ServiceLocator70();
		}
		return serviceLocator70Instance;
	}

	public UserManagement(AuthenticationServerConfiguration config) {
		this.config = config;
		logger = LoggerFactory.getLogger(this.getClass());
		logger.info("Entering 4TRESS UserManagement(config).");
		FtressHelpers70.setConfig(this.config);
		logger.debug("setting config.");
		crypto = CryptographyService.getInstance(this.config);
		logger.debug("Got crypto service.");
		passwordAttributeType = config
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD);
		memWordAttributeType = config
				.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD);
		channelCode = getChannel(config);
		securityDomain = new SecurityDomain(
				config.getConfigValue(AuthenticationServerConfiguration.DOMAIN));
	}

	private ChannelCode getChannel(AuthenticationServerConfiguration config) {
		ChannelCode chCode =  new ChannelCode();
		chCode.setCode(config.getConfigValue(AuthenticationServerConfiguration.CHANNEL));
	return chCode;
	}
	

	public UserManagement() {
		this(AuthenticationServerConfiguration.getStatic());
		logger = LoggerFactory.getLogger(this.getClass());
		logger.debug("Entering 4TRESS UserManagement()");
	}

	/**
	 * This method will always create a user and a memorable word authenticator
	 * to go with it. If we are using password/memword authentication then it
	 * will also create a customer password authenticator.
	 * 
	 * Reference SAR-2009-01-14-0007 This method will add the customer details
	 * of the user to the 4TRESS server. The customer details will be stored in
	 * the Map and passwd as an argument to the method. customerInfo Map will be
	 * processed by processCustomerDate method to create attributes.
	 * 
	 * 
	 * It is not compulsory to catch the following exceptions. They can be
	 * caught at the appropriate level: Throws an
	 * ArcAuthenticationServerConnectionException if there are problems
	 * connecting to the Authentication Server Throws an
	 * ArcAuthenticationServerException if there are any other problems in the
	 * Authentication Server
	 * 
	 * @param t24UserName
	 *            T24 username to be encrypted & added to auth server
	 * @param memorableData
	 *            pre-encrypted memorable data for this user
	 * @param startDate
	 *            when this user will become active
	 * @return unencypted password for the user is successful otherwise
	 */
	public String addUser(String t24UserName, String memorableData,
			Calendar startDate, Map custInfo) {
		logger.info("addUser method called");
		// check if T24 user exists in authentication server already
		if (userExists(t24UserName)) {
			throw new ArcUserStateException("User already exists.");
		}

		ArcSession sessionId = loginSystemUser();
		String arcUserId = isARCUserExists(t24UserName, sessionId);

		logger.info("Continue to create new User .." + t24UserName);
		UserCode userCode = new UserCode(arcUserId);
		// create User Record in external authentication server 4TRESS
		String t24Password = null;
		try {
			t24Password = createUserRecord(t24UserName, startDate, custInfo,
					sessionId, arcUserId, channelCode, securityDomain, userCode);
			// Add Memorable word authenticator
			addMemorableData(t24UserName, memorableData, sessionId, arcUserId,
					channelCode, securityDomain, userCode);
			// If this is a password/memword login, then we need to create a
			// custpw authenticator.
			// Get the authType and create Password Authenticator
			doPWAuthCheck(t24UserName, sessionId, arcUserId, userCode);
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return t24Password;
	}

	/**
	 * @param t24UserName
	 * @param sessionId
	 * @return
	 */
	private String isARCUserExists(String t24UserName, ArcSession sessionId) {
		String arcUserId = null;
		boolean arcUserExists = false;
		if (isTrue(AuthenticationServerConfiguration.ARC_USER_ID_SHARED)) {
			arcUserId = t24UserName;
			arcUserExists = checkArcUser(arcUserId, sessionId);
		} else {
			arcUserExists = checkT24User(t24UserName, sessionId);
			arcUserId = generateRandomARCUserID(sessionId);
		}
		// User already exists in authentication server.
		throwARCUserExistError(t24UserName, arcUserExists);
		return arcUserId;
	}

	/**
	 * @param t24UserName
	 * @param sessionId
	 * @param arcUserId
	 * @param userCode
	 * @throws RemoteException
	 * @throws InternalException
	 * @throws InvalidParameterException
	 * @throws ConstraintFailedException
	 * @throws CreateDuplicateException
	 * @throws AuthenticatorException
	 * @throws InvalidChannelException
	 * @throws NoFunctionPrivilegeException
	 * @throws ObjectNotFoundException
	 * @throws ALSIInvalidException
	 */
	private void doPWAuthCheck(String t24UserName, ArcSession sessionId,
			String arcUserId, UserCode userCode) throws ALSIInvalidException,
			ObjectNotFoundException, NoFunctionPrivilegeException,
			InvalidChannelException, AuthenticatorException,
			CreateDuplicateException, ConstraintFailedException,
			InvalidParameterException, InternalException, RemoteException {
		logger.info("Entering PWAuth Invoking Code");
		String[] authMode = XmlUserManagement.loginMode;
		logger.info("The Authmode is :" + authMode);
		int strLen = authMode.length;
		logger.info("The length of AuthMode is :" + strLen);
		for (int i = 0, modeSet = 0; i < strLen; i++) {
			if ((authMode[i] != null) & (modeSet == 0)) {
				String mode = authMode[i];
				logger.info("Check the AuthMode:" + mode);
				if ((null != config
						.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_ARC_PASSWORD))
						&& isPasswordOrMemorableWord(mode)) {
					modeSet = 1;
					logger.info("The PWAuth mode is Set :" + modeSet);
					logger.info("Adding PWAuth authenticator for User : "
							+ t24UserName);
					addPasswordAuthenticator(sessionId, userCode, arcUserId);
					logger.info("successfully added PWAuth authenticator for User : "
							+ t24UserName);
				}
			}
		}
		logger.info("Coming out of PW Auth Check ");
	}

	/**
	 * @param mode
	 * @return Boolean
	 */
	private boolean isPasswordOrMemorableWord(String mode) {
		return (mode
				.equals(config
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PW)))
				|| (mode.equals(config
						.getConfigValue(AuthenticationServerConfiguration.CHANNEL_TYPE_PWMW)));
	}

	/**
	 * @param t24UserName
	 * @param memorableData
	 * @param sessionId
	 * @param arcUserId
	 * @param channelCode
	 * @param secDomain
	 * @param userCode
	 * @throws InternalException
	 * @throws RemoteException
	 * @throws InvalidParameterException
	 * @throws ConstraintFailedException
	 * @throws ObjectNotFoundException
	 * @throws ALSIInvalidException
	 * @throws CreateDuplicateException
	 * @throws NoFunctionPrivilegeException
	 * @throws AuthenticatorException
	 * @throws InvalidChannelException
	 */
	private void addMemorableData(String t24UserName, String memorableData,
			ArcSession sessionId, String arcUserId, ChannelCode channelCode,
			SecurityDomain secDomain, UserCode userCode)
			throws InvalidChannelException, AuthenticatorException,
			NoFunctionPrivilegeException, CreateDuplicateException,
			ALSIInvalidException, ObjectNotFoundException,
			ConstraintFailedException, InvalidParameterException,
			RemoteException, InternalException {
		logger.info("Adding memorable authenticator for User : " + t24UserName);
		// Create a username password authenticator for the memorable data.
		// We need to decrypt the memData before storing in 4TRESS as it will
		// be inaccessible from the UPAuthenticator once stored.
		// All encrypted data must be accessible for the purposes of key
		// rollover.
		String decryptedMemData = crypto.decrypt(memorableData, true);
		AuthenticatorManager authenticatorManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();
		AuthenticationTypeCode typeCode = applyAuthTypeCode(memWordAttributeType);
		authenticatorManager.createUPAuthenticator(
				getALSIValue(sessionId),
				channelCode,
				setUPAuthenticatorAttributes(arcUserId, userCode,
						decryptedMemData, typeCode), secDomain);
		logger.info("successfully created memorable word autheticator for User : "
				+ t24UserName);

	}

	/**
	 * @param sessionId
	 * @return alsi
	 */
	private ALSI getALSIValue(ArcSession sessionId) {
		ALSI alsi = new ALSI();
		alsi.setAlsi(sessionId.toString());
		return alsi;
	}

	/**
	 * @param arcUserId
	 * @param userCode
	 * @param decryptedMemData
	 * @param typeCode
	 * @return upAuth
	 */
	private UPAuthenticator setUPAuthenticatorAttributes(String arcUserId,
			UserCode userCode, String decryptedMemData,
			AuthenticationTypeCode typeCode) {
		UPAuthenticator upAuth = new UPAuthenticator();
		upAuth.setAuthenticationTypeCode(typeCode);
		upAuth.setPassword(decryptedMemData);
		upAuth.setUserCode(userCode);
		upAuth.setUsername(arcUserId);
		upAuth.setStatus(Authenticator.ENABLED);
		upAuth.setValidFrom(Calendar.getInstance());
		upAuth.setStatistics(null);
		return upAuth;
	}

	/**
	 * @param t24UserName
	 * @param startDate
	 * @param custInfo
	 * @param sessionId
	 * @param arcUserId
	 * @param channelCode
	 * @param secDomain
	 * @param userCode
	 * @return t24Password -String
	 * @throws RemoteException
	 * @throws InternalException
	 * @throws InvalidParameterException
	 * @throws ObjectNotFoundException
	 * @throws CreateDuplicateException
	 * @throws NoFunctionPrivilegeException
	 * @throws ALSIInvalidException
	 */
	private String createUserRecord(String t24UserName, Calendar startDate,
			Map custInfo, ArcSession sessionId, String arcUserId,
			ChannelCode channelCode, SecurityDomain secDomain, UserCode userCode)
			throws ALSIInvalidException, NoFunctionPrivilegeException,
			CreateDuplicateException, ObjectNotFoundException,
			InvalidParameterException, InternalException, RemoteException {

		logger.info("Attempting to create new User .." + t24UserName);
		// create user object
		User userRecord = new User();

		userRecord.setCode(userCode);
		GroupCode groupCode = new GroupCode();
		groupCode.setCode(USG_CUST);
		userRecord.setGroupCode(groupCode);
		userRecord.setStatus(ENABLED);
		userRecord.setStartDate(startDate);

		ArrayList<Attribute> userAttributes = processCustomerData(custInfo);

		String t24Password = null;
		if (!isTrue(AuthenticationServerConfiguration.IMPERSONATE_NOT_ALLOWED)) {
			AttributeTypeCode userTypeCode = new AttributeTypeCode(
					config.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_T24USER));
			String encryptedT24UserName = crypto.encrypt(t24UserName, false);
			Attribute userAttr = new Attribute(userTypeCode,
					encryptedT24UserName);
			AttributeTypeCode pwTypeCode = new AttributeTypeCode(
					config.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_T24PASS));
			t24Password = getPassword(AuthenticationServerConfiguration.T24_PASSWORD_LENGTH);
			String encryptedT24Password = crypto.encrypt(t24Password, true);
			Attribute pwAttr = new Attribute(pwTypeCode, encryptedT24Password);

			userAttributes.add(userAttr);
			userAttributes.add(pwAttr);
		}

		userRecord.setAttributes(userAttributes
				.toArray(new Attribute[userAttributes.size()]));

		logger.info("creating user id: " + arcUserId);
		UserManager userManager = getserviceLocator70Instance()
				.lookupUserManager();
		// call create user on user manager
		userManager.createUser(getALSIValue(sessionId), channelCode,
				userRecord, secDomain);
		logger.info("successfully created user id: " + arcUserId);

		return t24Password;
	}

	/**
	 * @return
	 */
	private String getPassword(String passwordLength) {
		return StringGenerator.getRandomAlphaNumericString(Integer
				.parseInt(config.getConfigValue(passwordLength)));
	}

	/**
	 * @param t24UserName
	 * @param arcUserExists
	 */
	private void throwARCUserExistError(String t24UserName,
			boolean arcUserExists) {
		if (arcUserExists) {
			logger.error("User already exists in authentication server : "
					+ t24UserName);
			throw new ArcAuthenticationServerException(
					"User already exists in authentication server : "
							+ t24UserName);
		}
	}

	/**
	 * @param sessionId
	 * @return arcUserId a String
	 */
	private String generateRandomARCUserID(ArcSession sessionId) {
		String arcUserId = StringGenerator
				.getRandomNumericString(Integer.parseInt(config
						.getConfigValue(AuthenticationServerConfiguration.ARC_USER_ID_LENGTH)));
		if (checkArcUser(arcUserId, sessionId)) {
			logger.error("ArcUser already exists in authentication server : "
					+ arcUserId);
			throw new ArcAuthenticationServerException(
					"Arc User already exists in authentication server : "
							+ arcUserId);
		}
		return arcUserId;
	}

	/**
	 * Method extracted from addUser. This adds a password authenticator and the
	 * password attribute to a user.
	 * 
	 * @param sessionId
	 * @param userCode
	 * @param arcUserId
	 * @throws RemoteException
	 * @throws InternalException
	 * @throws InvalidParameterException
	 * @throws NoFunctionPrivilegeException
	 * @throws ObjectNotFoundException
	 * @throws ALSIInvalidException
	 * @throws ConstraintFailedException
	 * @throws CreateDuplicateException
	 * @throws AuthenticatorException
	 * @throws InvalidChannelException
	 */
	private void addPasswordAuthenticator(ArcSession sessionId,
			UserCode userCode, String arcUserId) throws ALSIInvalidException,
			ObjectNotFoundException, NoFunctionPrivilegeException,
			InvalidParameterException, InternalException, RemoteException,
			InvalidChannelException, AuthenticatorException,
			CreateDuplicateException, ConstraintFailedException {

		String arcPassword = addUserAttributes(sessionId, userCode);

		createUPAuthenticator(sessionId, userCode, arcUserId, arcPassword);
		// Resetting the password while creating password authenticator for
		// the user. will reset the password status to PENDING
		if (isTrue(AuthenticationServerConfiguration.RESET_PASSWORD)) {
			logger.info("resetting Password on creation of Password authenticator");
			resetUserPasswordPin(arcUserId, passwordAttributeType, sessionId);
		} else {
			logger.info("No Password Reset");
		}
	}

	/**
	 * @return
	 */
	private boolean isTrue(final String stringToVerify) {
		return "true".equalsIgnoreCase(config.getConfigValue(stringToVerify));
	}

	/**
	 * @param sessionId
	 * @param userCode
	 * @param arcUserId
	 * @param arcPassword
	 * @throws InvalidParameterException
	 * @throws InvalidChannelException
	 * @throws AuthenticatorException
	 * @throws NoFunctionPrivilegeException
	 * @throws RemoteException
	 * @throws CreateDuplicateException
	 * @throws ALSIInvalidException
	 * @throws ObjectNotFoundException
	 * @throws InternalException
	 * @throws ConstraintFailedException
	 */
	private void createUPAuthenticator(ArcSession sessionId, UserCode userCode,
			String arcUserId, String arcPassword)
			throws InvalidParameterException, InvalidChannelException,
			AuthenticatorException, NoFunctionPrivilegeException,
			RemoteException, CreateDuplicateException, ALSIInvalidException,
			ObjectNotFoundException, InternalException,
			ConstraintFailedException {
		// Next add the password authenticator
		AuthenticationTypeCode typeCode = applyAuthTypeCode(passwordAttributeType);

		AuthenticatorManager authenticatorManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();
		authenticatorManager.createUPAuthenticator(
				getALSIValue(sessionId),
				channelCode,
				setUPAuthenticatorAttributes(arcUserId, userCode, arcPassword,
						typeCode), securityDomain);
	}

	/**
	 * @param sessionId
	 * @param userCode
	 * @return
	 * @throws InvalidParameterException
	 * @throws InternalException
	 * @throws ALSIInvalidException
	 * @throws ObjectNotFoundException
	 * @throws RemoteException
	 * @throws NoFunctionPrivilegeException
	 */
	private String addUserAttributes(ArcSession sessionId, UserCode userCode)
			throws InvalidParameterException, InternalException,
			ALSIInvalidException, ObjectNotFoundException, RemoteException,
			NoFunctionPrivilegeException {
		String arcPassword = getPassword(AuthenticationServerConfiguration.ARC_PASSWORD_LENGTH);
		// First add the encrypted passw to the arc password attribute
		AttributeTypeCode pwTypeCode = new AttributeTypeCode(
				config.getConfigValue(AuthenticationServerConfiguration.ATTRIBUTE_ARC_PASSWORD));
		String encryptedArcPassword = crypto.encrypt(arcPassword, true);
		Attribute pwAttr = new Attribute(pwTypeCode, encryptedArcPassword);

		UserManager userManager = getserviceLocator70Instance()
				.lookupUserManager();

		userManager.updateUserAttributes(getALSIValue(sessionId), channelCode,
				userCode, new Attribute[] { pwAttr }, securityDomain);
		return arcPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.arc.security.authenticationserver.server.ArcUserManagement
	 * #removeUser(java.lang.String)
	 */
	public void removeUser(String t24UserName) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(t24UserName, sessionId);

		logger.info("user found continue with removal");
		// get user manager
		UserManager userManager = getserviceLocator70Instance()
				.lookupUserManager();
		UserCode userCode = new UserCode(t24UserName);
		logger.info("user found");
		try {
			userManager.deleteUser(getALSIValue(sessionId), channelCode,
					userCode, securityDomain);
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
	}

	/**
	 * @param t24UserName
	 * @param sessionId
	 */
	private void isUserExists(String t24UserName, ArcSession sessionId) {
		if (!checkArcUser(t24UserName, sessionId)) {
			logger.error("User does not exist in authentication server : "
					+ t24UserName);
			throw new ArcUserStateException(
					"User does not exist in authentication server : "
							+ t24UserName);
		}
	}

	/**
	 * @param users
	 */
	private void checkUsers(User[] users) {
		if (null == users) {
			throw new ArcUserStateException(
					"Found no ARC users when expected exactly 1.");
		} else if (users.length != 1) {
			throw new ArcUserStateException("Found " + users.length
					+ " ARC users when expected exactly 1.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.arc.security.authenticationserver.server.ArcUserManagement
	 * #userExists(java.lang.String)
	 */
	public boolean userExists(String t24UserName) {
		ArcSession sessionId = loginSystemUser();

		String encryptedT24UserName = crypto.encrypt(t24UserName, false);
		boolean exists = FtressHelpers70.getInstance().userExists(sessionId,
				encryptedT24UserName);

		logOffSystemUser(sessionId);

		return exists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.arc.security.authenticationserver.server.ArcUserManagement
	 * #getArcUserId(java.lang.String)
	 */
	public String getArcUserId(String t24UserName) {

		ArcSession sessionId = loginSystemUser();

		String encryptedT24UserName = crypto.encrypt(t24UserName, false);
		User[] users = FtressHelpers70.getInstance().getArcUsers(sessionId,
				encryptedT24UserName);
		checkUsers(users);
		// get user code from Users
		User user = users[0];

		String userId = user.getCode().getCode();

		logOffSystemUser(sessionId);

		return userId;

	}

	/**
	 * @param userId
	 * @param sessionId
	 * @return userExists
	 */
	private boolean checkArcUser(String userId, ArcSession sessionId) {
		boolean userExists = false;
		try {
			UserCode userCode = new UserCode(userId);
			UserManager userManager = getserviceLocator70Instance()
					.lookupUserManager();
			User user = userManager.getUser(getALSIValue(sessionId),
					channelCode, userCode, securityDomain);
			if (user != null)
				userExists = true;
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			logger.warn("User Object doesn't exist");
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		}
		return userExists;

	}

	/**
	 * This method will get the Failure Counts of all the Authenticator of the
	 * user. method returns a map with AuthenticationType Code as Key and
	 * Failure Count as the value.
	 */
	public Map getUserFailureCounts(String userId) {

		ArcSession sessionId = loginSystemUser();

		isUserExists(userId, sessionId);

		AuthenticatorManager authManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();

		ALSI alsi = getALSIValue(sessionId);
		UserCode userCode = new UserCode(userId);

		HashMap<String, String> map = new HashMap<String, String>();

		try {
			try {
				UPAuthenticator up[] = authManager
						.getAllUPAuthenticatorsForUser(alsi, channelCode,
								userCode, securityDomain);
				addCountToMap(map, up);
			} catch (NullPointerException e) {
				logger.debug("no user password authenticators ");
			}
			try {
				Authenticator da[] = authManager.getAllAuthenticatorsForUser(
						alsi, channelCode, userCode, securityDomain);
				addCountToMap(map, da);
			} catch (NullPointerException e) {
				logger.debug(" no device authenticators ");
			}

		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return map;
	}

	/**
	 * This takes FtressAuthenticator as input, this is patent type for both
	 * UPAuthenticator and Authenticator
	 * 
	 * @param map
	 * @param fa
	 */
	private void addCountToMap(HashMap<String, String> map,
			FtressAuthenticator[] fa) {
		for (int i = 0; i < fa.length; i++) {
			int failureCount = fa[i].getStatistics().getConsecutiveFailed();
			Integer fc = new Integer(failureCount);
			map.put((String) fa[i].getAuthenticationTypeCode().getCode(),
					fc.toString());
		}
	}

	/**
	 * This method will reset the Failure Counts of all the Authenticator of the
	 * user. method returns a map with AuthenticationType Code as Key and
	 * Failure Count as the value.
	 */

	public Map resetFailureCounts(String userId) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		HashMap<String, String> authenticatorMap = (HashMap<String, String>) getUserFailureCounts(userId);

		AuthenticatorManager authManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();
		try {
			String configFile = System.getProperty(ARC_CONFIG_PATH);
			ConfigurationFileParser parser = new ConfigurationFileParser(
					configFile, ARC);
			Map[] configMap = parser.parse();
			AuthenticationServerConfiguration config = new AuthenticationServerConfiguration(
					configMap[0]);
		} catch (NullPointerException e1) {
			logger.debug(" Authentication Server Configuration  config is null ");
		}
		UserCode userCode = new UserCode(userId);
		try {
			Set set = authenticatorMap.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String key = (String) me.getKey();
				AuthenticationTypeCode authTypeCode = applyAuthTypeCode(key);
				if (isValidAuthTypeCode(config, key)) {
					if (key.equals(config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE))) {
						authManager.resetAuthenticatorFailedAuthenticationCount(getALSIValue(sessionId), channelCode, userCode, authTypeCode, securityDomain);
					} else {
						authManager.resetUPAuthenticatorFailedAuthenticationCountByUserCode(getALSIValue(sessionId), channelCode, userCode, authTypeCode, securityDomain);	
					}
				}
			}

		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return getUserFailureCounts(userId);
	}

	/**
	 * @param config
	 * @param key
	 * @return
	 */
	private boolean isValidAuthTypeCode(
			AuthenticationServerConfiguration config, String key) {
		return key
				.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN))
				|| key.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))
				|| key.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD))
				|| key.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_LOGINMEMWORD))
				|| key.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE));
	}

	/**
	 * ResetUserPassword method will reset the user password status to PENDING.
	 * method returns a status String to the caller, to determine SUCCESS or
	 * FAILURE.
	 */
	public String resetUserPassword(String userId) {

		boolean resetSuccess = resetUserPasswordPin(
				userId,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD),
				null);
		if (resetSuccess) {
			return SUCCESS;
		} else {
			return FAILURE;
		}
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
	 * resetUserPin method will reset the user pin status to PENDING. method
	 * returns a status String to the caller, to determine SUCCESS or FAILURE.
	 */
	public String resetUserPin(String userId) {

		boolean resetSuccess = false;
		resetSuccess = resetUserPasswordPin(
				userId,
				config.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN),
				null);
		if (resetSuccess) {
			return SUCCESS;
		} else {
			return FAILURE;
		}
	}

	/**
	 * updateMemeorableWord method will update the user memorable word in FTRESS
	 * Server. Clear memData will be encrypted and added to the Ftress Server.
	 */
	public String updateMemorableWord(String userId, String memorableData) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		CredentialManager credentialManager = getserviceLocator70Instance()
				.lookupCredentialManager();

		String decryptedMemData = crypto.decrypt(memorableData, true);

		UserCode userCode = new UserCode(userId);
		Password password = new Password(decryptedMemData);

		try {
			credentialManager
					.changeUserPassword(
							getALSIValue(sessionId),
							channelCode,
							userCode,
							applyAuthTypeCode(config
									.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD)),
							password, securityDomain);
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (InvalidChannelException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (ConstraintFailedException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.arc.security.authenticationserver.server.ArcUserManagement
	 * #updateAuthServerUserId(java.lang.String, java.lang.String)
	 */
	public String updateAuthServerUserId(String userId, String newUserId) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		UserManager userManager = getserviceLocator70Instance()
				.lookupUserManager();

		UserCode oldUserCode = new UserCode(userId);
		UserCode newUserCode = new UserCode(newUserId);
		// Will Update only ExternalUserId not the authenticator Username.
		try {
			userManager.updateUserExternalReference(getALSIValue(sessionId),
					channelCode, oldUserCode, newUserCode, securityDomain);
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (CreateDuplicateException e) {
			throwARCUserExistError(newUserId, true);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return SUCCESS;
	}

	/**
	 * Get the authenticator Status of the user. Returns a Map with AuthTypeCode
	 * as key and status+startDate+endDate+authTypeCode as Value
	 */
	public Map getAuthenticatorStatus(String userId) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		Map map = new HashMap();
		AuthenticatorManager authManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();

		ALSI alsi = getALSIValue(sessionId);
		UserCode userCode = new UserCode(userId);
		try {
			UPAuthenticator up[] = authManager.getAllUPAuthenticatorsForUser(
					alsi, channelCode, userCode, securityDomain);
			Authenticator da[] = authManager.getAllAuthenticatorsForUser(alsi,
					channelCode, userCode, securityDomain);

			if (up != null) {
				addDateAndAuthCodeToMap(map, up);
			}
			if (da != null) {
				addDateAndAuthCodeToMap(map, da);
			}

		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return map;
	}

	/**
	 * Get the calendar and get the DATE,MONTH,YEAR Fileds separately. create a
	 * String with DATE/MONTH/YEAR. Store the
	 * value=status,startDate,endDate,suthTypdeCode order. Store the
	 * AuthTypeCode as Key and Value -String
	 * 
	 * @param map
	 * @param fa
	 */
	private void addDateAndAuthCodeToMap(Map map, FtressAuthenticator[] fa) {
		String startDate = null;
		String endDate = null;
		String status = null;
		String authTypeCode = null;
		String value = null;
		int sDate = 0;
		int sMonth = 0;
		int sYear = 0;
		for (int i = 0; i < fa.length; i++) {
			status = fa[i].getStatus();
			Calendar cal1 = fa[i].getValidFrom();
			Calendar cal2 = fa[i].getValidTo();
			sDate = cal1.get(Calendar.DATE);
			sMonth = (cal1.get(Calendar.MONTH)) + 1;
			sYear = cal1.get(Calendar.YEAR);
			startDate = sDate + "/" + sMonth + "/" + sYear;
			sDate = cal2.get(Calendar.DATE);
			sMonth = (cal2.get(Calendar.MONTH)) + 1;
			sYear = cal2.get(Calendar.YEAR);
			endDate = sDate + "/" + sMonth + "/" + sYear;
			authTypeCode = fa[i].getAuthenticationTypeCode().getCode();
			value = status + ":" + startDate + ":" + endDate + ":"
					+ authTypeCode;
			map.put(authTypeCode, value);
		}
	}

	/**
	 * update the authenticator Status of the user.
	 * AuthenticatorStatus.ENABLED,AuthenticatorStatus.DISABLED, Returns a Map
	 * with updated Authenticator Status
	 */

	public Map<String, String> updateAuthenticatorStatus(String userId,
			String authType, String status) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		Map<String, String> map = new HashMap<String, String>();
		AuthenticatorManager authManager = getserviceLocator70Instance()
				.lookupAuthenticatorManager();

		UserCode userCode = new UserCode(userId);

		AuthenticationTypeCode authTypeCode = applyAuthTypeCode(authType);
		AuthenticatorStatus authStatus = null;

		try {
			if (status.equalsIgnoreCase(ENABLED)) {
				authStatus = new AuthenticatorStatus(DISABLED);
			} else if (status.equalsIgnoreCase(DISABLED)) {
				authStatus = new AuthenticatorStatus(ENABLED);
			}
			updateStatus(authType, authManager, getALSIValue(sessionId),
					userCode, authTypeCode, authStatus);

		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (AuthenticatorException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return getAuthenticatorStatus(userId);
	}

	/**
	 * @param authType
	 * @param authManager
	 * @param alsi
	 * @param userCode
	 * @param authTypeCode
	 * @param authStatus
	 * @throws InvalidParameterException
	 * @throws InternalException
	 * @throws AuthenticatorException
	 * @throws ALSIInvalidException
	 * @throws RemoteException
	 * @throws NoFunctionPrivilegeException
	 * @throws ObjectNotFoundException
	 */
	private void updateStatus(String authType,
			AuthenticatorManager authManager, ALSI alsi, UserCode userCode,
			AuthenticationTypeCode authTypeCode, AuthenticatorStatus authStatus)
			throws InvalidParameterException, InternalException,
			AuthenticatorException, ALSIInvalidException, RemoteException,
			NoFunctionPrivilegeException, ObjectNotFoundException {
		if (authType
				.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PASSWORD))
				|| authType
						.equals(config
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_PIN))
				|| authType
						.equals(config
								.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_MEMWORD))) {
			// Calls updateUPAuthenticatorStatus method of 4Tress
			authManager.updateUPAuthenticatorStatus(alsi, channelCode,
					userCode, authTypeCode, authStatus, securityDomain);

		} else if (authType
				.equals(config
						.getConfigValue(AuthenticationServerConfiguration.AUTH_TYPE_DEVICE))) {
			// Calls updateAuthenticatorStatus method of 4Tress
			authManager.updateAuthenticatorStatus(alsi, channelCode, userCode,
					authTypeCode, authStatus, securityDomain);

		}
	}

	/**
	 * processCustomerData will process the custInfo map object and create
	 * attribute for each Map key and store it in the Attribute array.
	 * 
	 * Map object will have CustomerDetails.
	 * 
	 * AttributeTypeCode
	 * =AuthenticationServerConfiguration.ARC_CUSTOMER_ATTRIBUTE + Map.KEY Key
	 * names are configurable in the server.config file of T24 Server. Value
	 * maps to the attribute used in the Ftress. Returns an attribute array
	 * whihc will be added by addUser() and updateCustomerData().
	 */
	public ArrayList<Attribute> processCustomerData(Map<String, String> custInfo) {

		AttributeTypeCode atcode[] = new AttributeTypeCode[custInfo.size()];
		ArrayList<Attribute> customerAttribute = new ArrayList<Attribute>();

		if (custInfo != null) {
			HashMap<String, String> custDetails = (HashMap<String, String>) custInfo;
			final Set<Entry<String, String>> set = custDetails.entrySet();
			final Iterator<Entry<String, String>> it = set.iterator();

			int i = 0;
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String attribKey = AuthenticationServerConfiguration.ARC_CUSTOMER_ATTRIBUTE
						+ "." + me.getKey();
				String value = (String) me.getValue();
				if (value == null) {
					continue;
				}
				if (i < atcode.length) {
					atcode[i] = new AttributeTypeCode(
							config.getConfigValue(attribKey));
					customerAttribute.add(new Attribute(atcode[i], value));
				}
				i++;
			}
		}
		return customerAttribute;
	}

	/**
	 * update the CustomerInfo for the UserId in the Ftress Server. returns
	 * Status Success or Failure while updating the Ftress Server.
	 */

	public String updateCustomerData(String userId, Map custInfo) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		try {
			UserManager userManager = getserviceLocator70Instance()
					.lookupUserManager();
			UserCode userCode = new UserCode(userId);

			final ArrayList<Attribute> processCustomerData = processCustomerData(custInfo);
			Attribute customerAttribute[] = processCustomerData
					.toArray(new Attribute[processCustomerData.size()]);
			userManager.updateUserAttributes(getALSIValue(sessionId),
					channelCode, userCode, customerAttribute, securityDomain);
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}

		return SUCCESS;
	}

	/**
	 * getAuditLogs() method return the log of the user for the mentioned
	 * fromDate to toDate. If fromDate or toDate is null,
	 * creationDate,currentDate will be assigned to respective fields. Returns
	 * an Array of AuditLog whihc will contain the auditrecord information of
	 * the user.
	 */
	public AuditLog[] getAuditLogs(String userId, String fromDate, String toDate) {

		ArcSession sessionId = loginSystemUser();
		isUserExists(userId, sessionId);

		ALSI alsi = getALSIValue(sessionId);
		UserCode userCode = new UserCode(userId);

		AuditSearchCriteria auditCriteria = new AuditSearchCriteria();
		auditCriteria.setIndirectUserExternalReference(userCode);
		auditCriteria.setVerifyAuditLog(true);

		setDates(fromDate, toDate, alsi, userCode, auditCriteria);
		/**
		 * Get the AuditRecord from auditResults and Create an AuditLog array as
		 * same as AuditRecord and populate the values.
		 */
		AuditSearchResults auditResults = null;
		AuditRecord auditRecord[] = null;
		AuditLog auditLogRecord[] = null;
		Auditor auditor = getserviceLocator70Instance().lookupAuditor();
		try {
			auditResults = auditor.searchAuditLog(alsi, channelCode,
					auditCriteria, securityDomain);
			if (auditResults != null) {
				auditRecord = auditResults.getAuditRecords();
				auditLogRecord = new AuditLog[auditRecord.length];
				if (auditLogRecord.length != 0) {
					for (int i = 0; i < auditLogRecord.length; i++) {
						auditLogRecord[i] = new AuditLog();
						auditLogRecord[i]
								.setTimestamp(calendartoString(auditRecord[i]
										.getTimestamp()));
						auditLogRecord[i].setAction(auditRecord[i]
								.getEventType());
						auditLogRecord[i].setMessage(auditRecord[i]
								.getMessage());
						auditLogRecord[i].setParameter(auditRecord[i]
								.getParameters());
						auditLogRecord[i].setTarget_user(auditRecord[i]
								.getTargetUser().getCode());
						auditLogRecord[i].setStatus(auditRecord[i].getStatus());
						auditLogRecord[i].setResponse(auditRecord[i]
								.getResponse());
						auditLogRecord[i].setChannel(auditRecord[i]
								.getChannel().getCode());
					}
				} else {
					logger.debug(" No Record to Display");
				}
			}

		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		} finally {
			logOffSystemUser(sessionId);
		}
		return auditLogRecord;
	}

	/**
	 * @param fromDate
	 * @param toDate
	 * @param alsi
	 * @param userCode
	 * @param auditCriteria
	 */
	private void setDates(String fromDate, String toDate, ALSI alsi,
			UserCode userCode, AuditSearchCriteria auditCriteria) {
		if (fromDate == null || toDate == null) {
			/**
			 * fromDate is null Get the CreationDate from Ftress Server. Make a
			 * Call to Ftress Server using FtressAPI UserManager.getUSER return
			 * the particular user record. From the received User record get the
			 * Date .getStartDate().
			 * 
			 */

			/**
			 * assign today value to endDate
			 */
			Calendar endDate = Calendar.getInstance();
			auditCriteria.setFrom(getStartDateFromUserData(alsi, userCode));
			auditCriteria.setTo(endDate);
		}
		if (fromDate != null || toDate != null) {
			auditCriteria.setFrom(StringtoCalendar(fromDate));
			auditCriteria.setTo(StringtoCalendar(toDate));

		}
	}

	/**
	 * @param alsi
	 * @param userCode
	 * @return
	 */
	private Calendar getStartDateFromUserData(ALSI alsi, UserCode userCode) {
		Calendar startDate = null;
		try {
			UserManager userManager = getserviceLocator70Instance()
					.lookupUserManager();
			User user = userManager.getUser(alsi, channelCode, userCode,
					securityDomain);
			if (user != null) {
				startDate = user.getStartDate();
			}
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		}
		return startDate;
	}

	/**
	 * Utility method used by getAuditLogs method. Will get
	 * Month,Year,Hour,Minute,Second,Date from the calendar object.
	 * 
	 * @param Calendar
	 *            object
	 * @return String
	 */
	public String calendartoString(Calendar calendar) {
		Calendar date = calendar;

		int year = date.get(Calendar.YEAR);
		int calMonth = date.get(Calendar.MONTH) + 1;
		int calDate1 = date.get(Calendar.DATE);
		int hour = date.get(Calendar.HOUR);
		int minute = date.get(Calendar.MINUTE);
		int second = date.get(Calendar.SECOND);

		String month = "";
		String Date1 = "";
		String hours = "";
		String minutes = "";
		String seconds = "";

		if (calMonth >= 1 && calMonth <= 9) {
			month = "0" + calMonth;
		} else {
			month = new Integer(calMonth).toString();
		}
		if (calDate1 >= 1 && calDate1 <= 9) {
			Date1 = "0" + calDate1;
		} else {
			Date1 = new Integer(calDate1).toString();
		}
		if (hour >= 1 && hour <= 9) {
			hours = "0" + hour;
		} else {
			hours = new Integer(hour).toString();
		}
		if (minute >= 1 && minute <= 9) {
			minutes = "0" + minute;
		} else {
			minutes = new Integer(minute).toString();
		}
		if (second >= 1 && second <= 9) {
			seconds = "0" + second;
		} else {
			seconds = new Integer(second).toString();
		}
		String timeDate = year + "" + month + "" + Date1 + "  " + hours + ":"
				+ minutes + ":" + seconds;

		return timeDate;
	}

	/**
	 * Utility method used by getAuditLogs method Used to convert the String to
	 * Calendar object.
	 * 
	 * @param datestr
	 * @return Calendar
	 */
	public Calendar StringtoCalendar(String datestr) {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = null;
		try {
			Date date = (Date) formatter.parse(datestr);
			cal = Calendar.getInstance();
			cal.setTime(date);
		} catch (ParseException e) {
			logger.error("Exception while parsing Date " + e);
			throw new ArcAuthenticationServerException(e.getMessage());
		}
		return cal;
	}

	/**
	 * @return
	 */
	private ArcSession loginSystemUser() {
		ArcSession session = null;
		try {
			String name = config
					.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_USER);
			String decryptedName = crypto.decrypt(name, true);
			String decryptedPassword = crypto
					.decrypt(
							config.getConfigValue(AuthenticationServerConfiguration.UP_AUTH_PASSWORD),
							true);
			char[] passphrase = null;
			passphrase = decryptedPassword.toCharArray();
			FtressHelpers70.setConfig(config);

			GenericAuthenticationResponse authSysLogin = FtressHelpers70
					.getInstance().authenticateSystemUser(decryptedName,
							passphrase);

			if (authSysLogin != null) {
				
				session = FtressHelpers70.getInstance().handleResponse(
						authSysLogin, config);
				logger.debug("ArcSession value extracted from response");
			} else {
				throw new ArcAuthenticationServerException(
						"NULL response returned");
			}

		} catch (ArcAuthenticationServerException e) {
			throwArcAuthenticationServerException(e);
		} catch (AccountExpiredException e) {
			throwArcAuthenticationServerException(e);
		} catch (FailedLoginException e) {
			throwArcAuthenticationServerException(e);
		} catch (AccountLockedException e) {
			throwArcAuthenticationServerException(e);
		}
		return session;
	}

	/**
	 * @param session
	 */
	private void logOffSystemUser(ArcSession session) {
		try {
			final boolean loggOffSuccess = FtressHelpers70.getInstance()
					.logoff(session);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @param t24UserName
	 * @param sessionId
	 * @return userExists
	 */
	private boolean checkT24User(String t24UserName, ArcSession sessionId) {
		boolean userExists = false;

		try {

			String encryptedT24UserName = crypto.encrypt(t24UserName, false);

			FtressHelpers70.setConfig(config);
			userExists = FtressHelpers70.getInstance().userExists(sessionId,
					encryptedT24UserName);

		} catch (ArcAuthenticationServerConnectionException e) {
			logger.error(e.getMessage(), e);
		} catch (ArcAuthenticationServerException e) {
			logger.error(e.getMessage(), e);
		}

		return userExists;
	}

	/**
	 * @param userId
	 * @param authTypeCode
	 * @param sessionId
	 * @return resetSuccess
	 */
	private boolean resetUserPasswordPin(String userId, String authTypeCode,
			ArcSession sessionId) {
		boolean resetSuccess = false;

		if (sessionId == null) {
			sessionId = loginSystemUser();
			isUserExists(userId, sessionId);
		}

		CredentialManager credentialManager = getserviceLocator70Instance()
				.lookupCredentialManager();

		UserCode userCode = new UserCode(userId);
		AuthenticationTypeCode resetauthTypeCode = new AuthenticationTypeCode();
		resetauthTypeCode.setCode(authTypeCode);
		PasswordResetRequest resetPassword = new PasswordResetRequest();
		resetPassword.setAuthenticationTypeCode(resetauthTypeCode);
		resetPassword.setUserCode(userCode);
		try {
			credentialManager.resetUserPassword(getALSIValue(sessionId),
					channelCode, resetPassword, securityDomain);
			resetSuccess = true;
		} catch (InvalidParameterException e) {
			throwArcAuthenticationServerException(e);
		} catch (ALSIInvalidException e) {
			throwArcAuthenticationServerException(e);
		} catch (PasswordResetException e) {
			throwArcAuthenticationServerException(e);
		} catch (ObjectNotFoundException e) {
			throwArcAuthenticationServerException(e);
		} catch (NoFunctionPrivilegeException e) {
			throwArcAuthenticationServerException(e);
		} catch (InternalException e) {
			throwArcAuthenticationServerConnectionException(e);
		} catch (RemoteException e) {
			throwArcAuthenticationServerConnectionException(e);
		}catch(Throwable t){
			t.printStackTrace();
		} finally {
			logOffSystemUser(sessionId);
		}
		return resetSuccess;
	}

	/**
	 * @param e
	 */
	private void throwArcAuthenticationServerConnectionException(Exception e) {
		logger.error(e.getMessage(), e);
		throw new ArcAuthenticationServerConnectionException(e.getMessage());
	}

	/**
	 * @param e
	 */
	private void throwArcAuthenticationServerException(Exception e) {
		logger.error(e.getMessage(), e);
		throw new ArcAuthenticationServerException(e.getMessage());
	}
}
