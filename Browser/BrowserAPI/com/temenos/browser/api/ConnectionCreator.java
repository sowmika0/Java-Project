package com.temenos.browser.api;

/**Provides an abstract class to be subclassed to create a class instance for the use
 * of dynamic class loading in Java.
 * <br/>
 *
 * @author Special Project
 * @version 1.0
 */
public abstract class ConnectionCreator {

	/**
     * This method loads the class that to be instantiated it.
     * Throws ConnectionNotCreatedException if the call centre conection could 
     * not be created.
     *
     * @param name the name of the class that wishes to be instantiated.
     * @throws ConnectionNotCreatedException indicates the call centre connection 
     * could not be created.
     * @see com.temenos.globusbrowser.api.ConnectionNotCreatedException
     */
	protected static APIConnection createConnection(String name)
		throws ConnectionNotCreatedException
	{
		APIConnection apiConnection;
		
		try
		{
			Class connectionClass = Class.forName("com.temenos.browser.apiclient." + name);
			apiConnection = (APIConnection) connectionClass.newInstance();
		}
		catch(ClassNotFoundException e)
		{
			throw (new ConnectionNotCreatedException());
		}
		catch(IllegalAccessException e)
		{
			throw (new ConnectionNotCreatedException());
		}
		catch(InstantiationException e)
		{
			throw (new ConnectionNotCreatedException());
		}
		
		return apiConnection;
		
	}
}
