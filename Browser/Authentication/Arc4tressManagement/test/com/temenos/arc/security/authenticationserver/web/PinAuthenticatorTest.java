package com.temenos.arc.security.authenticationserver.web;

import com.temenos.arc.security.authenticationserver.common.AbstractUsernamePasswordAuthenticatorTest;

public class PinAuthenticatorTest extends AbstractUsernamePasswordAuthenticatorTest {

	protected void setUp() throws Exception {
		super.setUp();
		config = AuthenticationByPinConfigurationFactory.getInstance().getConfiguration();       
	}
}
