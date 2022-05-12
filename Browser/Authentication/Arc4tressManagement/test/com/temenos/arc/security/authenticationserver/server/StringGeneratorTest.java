package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.common.StringGenerator;

import junit.framework.TestCase;

public class StringGeneratorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetRandomAlphaNumericString() {
		String random = StringGenerator.getRandomAlphaNumericString(8);
		if (random.length() != 8) {
			fail("Incorrect length of string generated");
		}
		if (!random.matches("[a-zA-Z0-9]*")) {
			fail("Invalid characters in string");
		}
		
	}
	public void testGetRandomNumericString() {
		String random = StringGenerator.getRandomNumericString(9);
		if (random.length() != 9) {
			fail("Incorrect length of string generated");
		}
		if (!random.matches("[0-9]*")) {
			fail("Invalid characters in string");
		}
		
	}
}
