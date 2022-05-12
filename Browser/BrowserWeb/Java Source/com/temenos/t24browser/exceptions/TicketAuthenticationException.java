package com.temenos.t24browser.exceptions;

/**
 * General exception class for the TicketAuthenticationFilter class 
 */
public class TicketAuthenticationException extends RuntimeException{
	 public TicketAuthenticationException() {
	        super();
	    }
	 
	    public TicketAuthenticationException(String msg) {
	        super(msg);
	    }
}
