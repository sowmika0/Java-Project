package com.temenos.arc.security.authenticationserver.common;

public class GenericAuthenticationResponse {

	public static final int RESPONSE_AUTHENTICATION_SUCCEEDED = 1;
	public static final int RESPONSE_AUTHENTICATION_FAILED = 2;

 
	public static final int REASON_AUTHENTICATOR_NOT_FOUND = 0;
	public static final int REASON_AUTHENTICATOR_DISABLED = 1;
	public static final int REASON_MAX_CONSECUTIVE_FAILED_REACHED = 2;
	public static final int REASON_MAX_SUCCESSFUL_REACHED = 3;
	public static final int REASON_CHANNEL_BLOCKED_PRIMARY = 4;
	public static final int REASON_CHANNEL_BLOCKED_SECONDARY = 5;
	public static final int REASON_CHANNEL_BLOCKED_PRIMARY_SECONDARY = 6;
	public static final int REASON_AUTHENTICATOR_NOT_YET_VALID = 7;
	public static final int REASON_AUTHENTICATOR_EXPIRED = 8;
	public static final int REASON_MD_ANSWER_MISMATCH = 9;
	public static final int REASON_AUTHENTICATION_CODE_LENGTH_MISMATCH = 12;
	public static final int REASON_PASSWORD_MISMATCH = 13;
	public static final int REASON_MD_ANSWERS_PROVIDED = 14;
	public static final int REASON_USER_NOT_FOUND = 15;
	public static final int REASON_CHALLENGE_MISMATCH = 17;
	public static final int REASON_INCORRECT_RESPONSE = 18;
	public static final int REASON_PASSWORD_MAX_USAGES_REACHED = 19;
	public static final int REASON_DEVICE_NOT_VALID = 20;
	public static final int REASON_CHALLENGE_EXPIRED = 22;
	public static final int REASON_NO_VALID_CREDENTIAL_FOUND = 23;
	public static final int REASON_WRONG_SOFTWARE_PIN_VALUE = 24;
	public static final int REASON_INVALID_EMV_CARD_DATA = 25;
	public static final int REASON_INVALID_AMOUNT = 26;
	public static final int REASON_INVALID_CURRENCY = 27;
	public static final int REASON_INVALID_MASTERKEY_LABEL = 28;
	public static final int REASON_MAXIMUM_ATC_VALUE_REACHED = 29;
	public static final int REASON_CHALLENGE_REQUIRED = 30;
	public static final int REASON_AMOUNT_CURRENCY_REQUIRED = 31;
	public static final int REASON_INVALID_CVN = 32;
	public static final int REASON_FAILURE_CONVERTING_EMVSDB = 33;
	public static final int REASON_FAILURE_PKI_CHALLENGE_SIGNATURE_REQUIRED = 34;
	public static final int REASON_FAILURE_CREDENTIAL_INVALID_AUTHENTICATION_MODE_SYNCH = 35;
	public static final int REASON_FAILURE_CREDENTIAL_INVALID_AUTHENTICATION_MODE_ASYNCH = 36;
	public static final int HASHED_PASSWORD_AUTHENTICATION_NOT_SUCCESS = 39;
	public static final int REASON_FAILURE_LDAP_INVALID_AUTHENTICATION_MODE = 40;
	public static final int REASON_OTP_FOUND = 41;
	public static final int REASON_OTP_NOT_FOUND = 42;
	public static final int REASON_PIN_FOUND = 43;
	public static final int REASON_PIN_NOT_FOUND = 44;
	public static final int REASON_USER_DISABLED = 45;
	public static final int REASON_ACTIVATION_CODE_EXPIRED = 46;
 

	private int responseStatus;
	private int responseReason;
	private String session;
	private String sessionObject;

	/**
	 * @return the responseStatus
	 */
	public int getResponseStatus() {
		return responseStatus;
	}

	/**
	 * @param responseStatus
	 *            the responseStatus to set
	 */
	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	/**
	 * @return the responseReason
	 */
	public int getResponseReason() {
		return responseReason;
	}

	/**
	 * @param responseReason
	 *            the responseReason to set
	 */
	public void setResponseReason(int responseReason) {
		this.responseReason = responseReason;
	}

	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(String session) {
		this.session = session;
	}

	/**
	 * @return the setSessionObject
	 */
	public String getSessionObject() {
		return sessionObject;
	}

	/**
	 * @param setSessionObject
	 *            the setSessionObject to set
	 */
	public void setSessionObject(String sessionObject) {
		this.sessionObject = sessionObject;
	}

}
