package com.temenos.arc.security.authenticationserver.common;

import java.util.HashMap;
import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfigurationFactory;

import junit.framework.TestCase;

public class AuthenticationServerConfigurationTest extends TestCase {
	 AuthenticationServerConfigurationFactory factory;
	
	protected void setUp() throws Exception {
		super.setUp();
		// create a map of properties
		Map map = new HashMap();
		map.put(AuthenticationServerConfiguration.ATTRIBUTE_T24PASS, "foo");
		factory = new MockConfigurationFactory(map);
	}

	public void testSimple() {
		
		AuthenticationServerConfiguration config = factory.getConfiguration();
		
		assertEquals("foo", config.getConfigValue(
							AuthenticationServerConfiguration.ATTRIBUTE_T24PASS));
	}
}
