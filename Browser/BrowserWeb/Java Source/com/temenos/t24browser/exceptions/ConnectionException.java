package com.temenos.t24browser.exceptions;

/**
 * General exception class for connection errors 
 */
public class ConnectionException  extends Exception{
	
		public ConnectionException() {
			super();
		}
		
		public ConnectionException(String msg) {
			super(msg);
		}
}