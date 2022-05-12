package com.temenos.arc.security.jaas;

import junit.framework.TestCase;

public class OtpFromFileTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testGetOtp() {
		OtpFromFile otpff = new OtpFromFile();
		String blah = otpff.getOtp();
		assertTrue(null != blah);
	}

}
