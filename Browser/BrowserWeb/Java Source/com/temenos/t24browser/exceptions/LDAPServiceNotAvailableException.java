package com.temenos.t24browser.exceptions;
/**
 * General exception class for the LDAPAuthenticationFilter class 
 */

public class LDAPServiceNotAvailableException  extends RuntimeException{
	
		public LDAPServiceNotAvailableException() {
			super();
		}
		
		public LDAPServiceNotAvailableException(String msg) {
			super(msg);
		}
}
