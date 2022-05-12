package com.temenos.arc.security.authenticationserver.common;

import java.util.HashMap;
import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfigurationFactory;

public class MockConfigurationFactory implements
		AuthenticationServerConfigurationFactory {
	
	private Map properties = new HashMap(); 
	
	public MockConfigurationFactory(Map properties) {
		this.properties = properties;
	}

	public AuthenticationServerConfiguration getConfiguration() {
		return new AuthenticationServerConfiguration(properties);
	}

}
