package com.temenos.arc.security.jaas;

import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;

/**
 * Adds JAAS specific config parameters   
 * @author jannadani
 *
 */
public class JaasConfiguration extends AuthenticationServerConfiguration {
	public JaasConfiguration(Map properties) {
		super(properties);
	}
	public static final String AUTHENTICATION_DELEGATE = "temenos.arc.jaas.delegate";
	public static final String AUTHENTICATION_COMMITTER = "temenos.arc.jaas.committer";
	public static final String CALLBACK_OVERRIDE = "temenos.arc.jaas.callback.override";
	public static final String OTP_PIN_DELIMITER = "temenos.arc.security.ftress.delimiter";
	public static final String AUTHTYPE_IS_SEEDED = "temenos.arc.security.ftress.isseeded";
	public static final String AUTHTYPE_SEED_DELIMETER = "temenos.arc.security.ftress.seeddelimiter";
	public static final String AUTHTYPE_NO_OF_SEEDS = "temenos.arc.security.ftress.numseeds";

}
