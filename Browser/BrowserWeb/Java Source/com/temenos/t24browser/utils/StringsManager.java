////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   StringsManager
//
//  Description   :   Provides a storage facility for String values indexed by a key.
//                    These are read from a file and stored in a PropertyManager Hashtable.
//
//  Modifications :
//
//    29/06/06   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.Hashtable;

import com.temenos.t24browser.xml.XMLRequestManager;

// TODO: Auto-generated Javadoc
/**
 * The Class StringsManager.
 */
public class StringsManager extends HashTableManager implements Serializable
{
	
	/** The h strings table. */
	private Hashtable hStringsTable;

	/**
	 * Instantiates a new strings manager.
	 * 
	 * @param contextPath the context path
	 * @param fileName the file name
	 */
	public StringsManager(String contextPath, String fileName)
	{
		// Create Hashtable object - either from a XML file or empty
		// Try to read properties from a file
		try
		{
			XMLRequestManager xmlManager = new XMLRequestManager(contextPath, "PARAMETERS", fileName);
			this.setHashTable(xmlManager.getParametersTable());
		}
		catch (Exception e)
		{
			// File not found or other file problem, so create empty properties object
			this.setHashTable(new Hashtable());
		}
	}

	// Retrieve a parameter value from a properties object
	/**
	 * Gets the stringr value.
	 * 
	 * @param sParameter the s parameter
	 * 
	 * @return the stringr value
	 */
	public String getStringrValue( String sParameter )
	{
		return ((String) this.getProperty(sParameter));
	}

	// Check if a parameter value is valid or not
	/**
	 * Check string value.
	 * 
	 * @param sStringKey the s string key
	 * 
	 * @return true, if successful
	 */
	public boolean checkStringValue( String sStringKey )
	{
		String paramValue = (String) this.getProperty(sStringKey);

		if ((paramValue == null) || (paramValue.equals("")))
		{
			return (false);
		}
		else
		{
			return (true);
		}
	}
}