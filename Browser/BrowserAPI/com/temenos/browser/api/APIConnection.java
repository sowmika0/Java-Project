package com.temenos.browser.api;

/**Implementations of this interface to get data from the Call Centre API.
 * <br/>
 *
 * @author Special Project
 * @version 1.0
 */
public interface APIConnection {

	/**
	 * Sends and receives data from the call centre API.
	 *
	 * @param data a request to the call centre API.
	 * @return     a response from the call centre API.
	 */
	public String getData(String data);
}
