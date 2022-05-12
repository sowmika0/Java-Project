////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   PropertyManager
//
//  Description   :   Provides a storage facility for Property Names (keys) and
//                    their corresponding Values.
//                    Names (keys) and Values are stored in a Hashtable
//                    hPropertiesTable.
//                    Property Names are unique.
//                    Properties are stored in a file (browserParameters.xml in
//                    the current directory) when saved.
//                    Used by the Properties classes.
//
//  Modifications :
//
//    01/07/02   -    Initial Version taken from DevStudio product.
//
//    07/07/03   -    Now extends HashTableManager
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import com.temenos.t24browser.xml.XMLRequestManager;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertyManager.
 */
public class PropertyManager extends HashTableManager implements Serializable
{
	
	/** The h properties table. */
	private Hashtable hPropertiesTable;

	/**
	 * Instantiates a new property manager.
	 * 
	 * @param contextPath the context path
	 * @param fileName the file name
	 */
	public PropertyManager(String contextPath, String fileName)
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
	//Get Browser parameter values
	public Hashtable getParams()
	{
		return ((Hashtable)this.getProperties());
	}
	/**
	 * Save.
	 * 
	 * @return the boolean
	 */
	public Boolean save()
	{
		// Save Properties to a file in XML format
		try
		{
			return (Boolean.TRUE);
		}
		catch (Exception e)
		{
			System.out.println("Failed to write properties to file !");
			System.out.println("Exception : " + e);
			return (Boolean.FALSE);
		}
	}

	// Retrieve a parameter value from a properties object
	/**
	 * Gets the parameter value.
	 * 
	 * @param sParameter the s parameter
	 * 
	 * @return the parameter value
	 */
	public String getParameterValue( String sParameter )
	{
		return ((String) this.getProperty(sParameter));
	}

	// Check if a parameter value is valid or not
	/**
	 * Check parameter value.
	 * 
	 * @param sParameter the s parameter
	 * 
	 * @return true, if successful
	 */
	public boolean checkParameterValue( String sParameter )
	{
		String paramValue = (String) this.getProperty(sParameter);

		if ((paramValue == null) || (paramValue.equals("")))
		{
			return (false);
		}
		else
		{
			return (true);
		}
	}
	
	/**
	 * Convert class to XML format.
	 * 
	 * @return the XML
	 */
	public String toXml()
	{
		String xml = "<properties>";
		
		Enumeration keys = this.getKeys();
		
		// Get all the properties
		while ( keys.hasMoreElements() )
		{		   		
			String name = (String)keys.nextElement();
			String value = this.getParameterValue(name);
			xml += "<property>";
			xml += "<name>" + name + "</name>";
			xml += "<value>" + value + "</value>";
			xml += "</property>";
		}

		xml += "</properties>";
		return( xml );
	}
}