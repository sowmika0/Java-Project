package com.temenos.t24browser.exceptions;
/**
 * General exception class for the LDAPAuthenticationFilter class 
 */

public class GenericAuthenticationException  extends RuntimeException{
	
		public GenericAuthenticationException() {
			super();
		}
		
		public GenericAuthenticationException(String msg) {
			super(msg);
		}
}
