package com.temenos.browser.api;

/**This exception is thrown when it encounters difficulty in creating a connection
 * to the call centre API.
 * <br/>
 * 
 * @author Special Project
 * @version 1.0
 */
public class ConnectionNotCreatedException extends Exception {

	/**
     * Constructs a new exception.
     */
	public ConnectionNotCreatedException() {
		super("Call Centre connection could not be created!");
	}
}
