package com.temenos.arc.security.jaas;

import java.util.HashMap;
import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfigurationFactory;

public class AuthenticationByDeviceConfigurationFactory implements
		AuthenticationServerConfigurationFactory {

	public static AuthenticationServerConfigurationFactory getInstance() {
		return new AuthenticationByDeviceConfigurationFactory();
	}
	private Map properties = new HashMap();
    private AuthenticationByDeviceConfigurationFactory() {
        properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24PASS,
                "");
        properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24USER,
                "");
        properties.put(AuthenticationServerConfiguration.UP_AUTH_USER,
                "786786786");
        properties.put(AuthenticationServerConfiguration.AUTHENTICATION_TYPE,
                "AT_AIOTP");
        properties.put(AuthenticationServerConfiguration.CHANNEL,
                "CH_WEB");
        properties.put(AuthenticationServerConfiguration.CHARSET,
                "UTF-8");
        properties.put(AuthenticationServerConfiguration.DOMAIN,
                "DOMAIN1");     
        properties.put(AuthenticationServerConfiguration.DEVICE_MODE,
                "SYNC");     
        properties.put(JaasConfiguration.OTP_PIN_DELIMITER,
        		"|");     
    }
	
	public AuthenticationServerConfiguration getConfiguration() {		
		return new JaasConfiguration(properties);
	}

}
