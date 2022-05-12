package com.temenos.t24browser.exceptions;
/**
 * General exception class for the LDAPAuthenticationFilter class 
 */

public class LDAPAuthenticationException  extends RuntimeException{
	
		public LDAPAuthenticationException() {
			super();
		}
		
		public LDAPAuthenticationException(String msg) {
			super(msg);
		}
}
