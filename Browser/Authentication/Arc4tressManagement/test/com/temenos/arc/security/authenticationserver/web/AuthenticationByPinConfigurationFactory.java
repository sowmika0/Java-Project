package com.temenos.arc.security.authenticationserver.web;

import java.util.HashMap;
import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfigurationFactory;
import com.temenos.arc.security.jaas.JaasConfiguration;

public class AuthenticationByPinConfigurationFactory implements
		AuthenticationServerConfigurationFactory {

	public static AuthenticationServerConfigurationFactory getInstance() {
		return new AuthenticationByPinConfigurationFactory();
	}
	private Map properties = new HashMap();
	
	private AuthenticationByPinConfigurationFactory() {
		properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24PASS,
				"ATR_T24PW");
		properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24USER,
				"ATR_T24UID");
		properties.put(AuthenticationServerConfiguration.UP_AUTH_USER,
				"786786786");
		properties.put(AuthenticationServerConfiguration.UP_AUTH_PASSWORD,
				"125412");
		properties.put(AuthenticationServerConfiguration.AUTHENTICATION_TYPE,
				"AT_CUSTPIN");
		properties.put(AuthenticationServerConfiguration.CHANNEL,
				"CH_WEB");
		properties.put(AuthenticationServerConfiguration.CHARSET,
				"UTF-8");
		properties.put(AuthenticationServerConfiguration.DOMAIN,
				"DOMAIN1");		
	}
	
	public AuthenticationServerConfiguration getConfiguration() {		
		return new JaasConfiguration(properties);
	}

}
