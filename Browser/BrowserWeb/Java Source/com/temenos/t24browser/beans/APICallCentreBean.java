package com.temenos.t24browser.beans;

import com.temenos.browser.api.ConnectionCreator;
import com.temenos.browser.api.ConnectionNotCreatedException;
import com.temenos.browser.api.APIConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class APICallCentreBean.
 */
public class APICallCentreBean extends ConnectionCreator
{

	/** The connection. */
	private APIConnection connection;

	/**
	 * Instantiates a new API call centre bean.
	 */
	public APICallCentreBean()
	{
	}

	/**
	 * Sets the up connection.
	 * 
	 * @param className the new up connection
	 * 
	 * @throws ConnectionNotCreatedException the connection not created exception
	 * @throws ClassCastException the class cast exception
	 */
	public void setupConnection(String className) throws ConnectionNotCreatedException, ClassCastException
	{
       	connection = createConnection( className );
	}

	/**
	 * Talk to call centre.
	 * 
	 * @param data the data
	 * 
	 * @return the string
	 * 
	 * @throws ConnectionNotCreatedException the connection not created exception
	 */
	public String talkToCallCentre(String data) throws ConnectionNotCreatedException
	{
		return connection.getData(data);
	}
}
