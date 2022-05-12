////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   RequestData
//
//  Description   :   Stores details data in T24 Request.
//					  Can be used to hold request attributes or parameters.
//
//  Modifications :
//
//    19/02/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.*;



// TODO: Auto-generated Javadoc
/**	
 * The Class RequestData.
 */
public class RequestData implements Serializable
{
	
	/** The iv data. */
	private Hashtable ivData = null;						// Colelction of request data names and values
	
	/** The iv data type. */
	private int ivDataType = 0;								// Type of data stored  
	
	/** The Constant ATTRIBUTE_DATA. */
	public static final int ATTRIBUTE_DATA = 0;				// Data type of request attributes
	
	/** The Constant PARAMETER_DATA. */
	public static final int PARAMETER_DATA = 1;				// Data type of request parameters
	
	/** The Constant ATTRIBUTE_STRING. */
	private static final String ATTRIBUTE_STRING = "Attribute";
	
	/** The Constant PARAMETER_STRING. */
	private static final String PARAMETER_STRING = "Parameter";
	
	
	/**
	 * Create a request data object.
	 * 
	 * @param dataType Type of data being stored (Attributes or Parameters)
	 */
 	public RequestData( int dataType )
	{
 		ivData = new Hashtable();
 		ivDataType = dataType;
 		
	}
	 	
	/**
	 * Create a request data object.
	 * 
	 * @param requestHashTable the request hash table
	 */
 	public RequestData( Hashtable requestHashTable )
 	{
 		ivData = new Hashtable();
 		ivData = requestHashTable;
 	}
 	
 	/**
	  * Check if there is any data in this object
	  */
	public boolean hasData()
	{
		return ( ivData.size() > 0 );
	}
	
 	/**
	  * Add data from Http request.
	  * 
	  * @param request The Http request
	  */
 	public void addData( HttpServletRequest request )
	{
		Enumeration dataList = null;
		
		if ( ivDataType == ATTRIBUTE_DATA )
		{
			dataList = request.getAttributeNames();
		}
		else
		{
			dataList = request.getParameterNames();
		}
		
		// Get all the data values
		while ( dataList.hasMoreElements() )
		{		   		
			String dataName = (String)dataList.nextElement();
			String dataValue = "";
			if (dataName.equals("javax.servlet.request.cipher_suite") || dataName.equals("javax.net.ssl.cipher_suite") || dataName.equals("javax.servlet.request.key_size") || dataName.equals("javax.net.ssl.peer_certificates") || dataName.equals("javax.servlet.request.X509Certificate"))
			{
				continue;
			}

			if ( ivDataType == ATTRIBUTE_DATA )
			{
                Object val = request.getAttribute( dataName );
                if(val != null) {
                	dataValue = val.toString();
                }
                else {
                	dataValue = null;
                }
			}
			else
			{
				dataValue = (String) request.getParameter( dataName );
				if(dataValue!=null && dataValue.equals(""))         // data value is null then get the value using getParamenterValues method
				{	
					
					String[] dataValueArray =request.getParameterValues(dataName); 
					if(dataValueArray!=null && (dataValueArray.length > 0))                // No need to proceed when value is null and length less than zero.
					{
							for(String ArrayIterator:dataValueArray)
							{
								if(!ArrayIterator.equals(""))
								{
									dataValue =  ArrayIterator;
									break;
								}
							}
						
					}
					
					
				}
			}
						
			if ( dataValue == null )
			{
				dataValue = "";
			}
			
			this.addDataItem( dataName, dataValue );
	 	
		}
	}
 	
 	/**
 	 * Get all data request data as String with Password parameter value as masked.
 	 * 
 	 */
 	public String toStringLog()
	{
		String sRequest = "";
		
		String routineName = this.getValue("routineName");
		
		ArrayList dataList = this.getSortedNameList();
		
		if ( dataList != null )
		{
			for ( int i = 0; i < dataList.size(); i++ )
			{
				String listItem = (String)dataList.get(i); 
				// Don't process when password parameter is encountered since password value is not masked and log file.
				// So that anyone can retrieve the password with user name when to have log file access.
				if (listItem.toLowerCase().contains("password")){
					//Mask the password values in all password parameters
					sRequest += ", "+listItem +"=******";
				}
				else if (listItem.contains("routineArgs") && routineName != null && routineName.contains("OS.PASSWORD")){
					//Mask the password values in the routineArgs parameter
					String arglistItem = this.getDataString(listItem);
					String argList = arglistItem.replaceAll("\n","").replaceAll("Parameter : ", "").replaceAll(", Value : ", "").replaceAll(listItem, "");
					String[] argValue = argList.split(":",-1);
					if(argValue[0].equals("PROCESS.REPEAT") || argValue[0].equals("PROCESS.EXPIRED") ){
						//Mask the OldPassword and Password values in routineArgs parameter
						sRequest += ", "+listItem +"="+argValue[0]+":"+argValue[1]+":******:******:"+argValue[4]+":"+argValue[5];
					}
					else if(argValue[0].equals("PROCESS.CHANGE")){
						//Mask the password, newPassword and confirmNewPassword values in routineArgs parameter
						sRequest += ", "+listItem +"="+ argValue[0]+":"+argValue[1]+":******:******:******";
					}
					else{
						sRequest += ", "+listItem +"="+argList;
					}			
				}
				else{
					sRequest += this.getDataString( listItem );
				}
			}
		}
				
		return( sRequest );
	}
 	
	/**
	  * Add data from a RequestData Object.
	  * 
	  * @param request The Http request
	  */
	public void addData( RequestData reqData )
	{
		// Get a list of keys stored in the request object
		Enumeration nameList = reqData.getNameList();
		// Go through the list adding the items to this requestData object
		while( nameList.hasMoreElements())
		{
			String name = (String)nameList.nextElement();
			String value = (String)reqData.getValue( name);
			addDataItem( name, value);
		}
	}

 	/**
	  * Add a data item.
	  * 
	  * @param name The name of the data item
	  * @param value The value of the data item
	  */
 	public void addDataItem( String name, String value )
	{
		if ( value == null )
		{
			value = "";
		}
		
		ivData.put( name, value );
	}
 	
	/**
	 * Get a data value from the request.
	 * 
	 * @param name The name of the data item
	 * 
	 * @return String
	 */
	public String getValue( String name )
	{
		return( (String) ivData.get( name ) );
	}
	
	/**
	 * Checks whether a data item is in the request.
	 * 
	 * @param name the name
	 * 
	 * @return boolean
	 */
	public boolean dataItemExists( String name )
	{
		return( ivData.containsKey( name ) );
	}
 	
	/**
	 * Get a list of the data names in the request.
	 * 
	 * @return Enumeration
	 */	
	public Enumeration getNameList()
	{
		return( ivData.keys() );
	}	

	/**
	 * Get a sorted list of the data names in the request.
	 * 
	 * @return ArrayList
	 */	
	public ArrayList getSortedNameList()
	{
		ArrayList nameArray = new ArrayList();
		
		if ( ivData != null )
		{
			Enumeration nameList = ivData.keys();
			
	        while( nameList.hasMoreElements() )
	        {
	        	nameArray.add( (String)nameList.nextElement() );
	        }
	     
	        Collections.sort( nameArray );
		}
		
		return( nameArray );
	}
	
	/**
	 * Get the request data as a String.
	 * 
	 * @return String
	 */
	public String toString()
	{
		String sRequest = "";
		
		ArrayList dataList = this.getSortedNameList();
		
		if ( dataList != null )
		{
			for ( int i = 0; i < dataList.size(); i++ )
			{
				sRequest += this.getDataString( (String) dataList.get(i) );
			}
		}
				
		return( sRequest );
	}
	
	/**
	 * Get the request data as a XML.
	 * 
	 * @return String
	 */
	public String toXml()
	{
		String sRequest = "";
		
		ArrayList dataList = this.getSortedNameList();
		
		if ( dataList != null )
		{
			for ( int i = 0; i < dataList.size(); i++ )
			{
				sRequest += this.getDataXml( (String) dataList.get(i) );
			}
		}
				
		return( sRequest );
	}
	
	/**
	 * Formats a data item in to a xml for output.
	 * 
	 * @param name the name
	 * 
	 * @return String
	 */
	private String getDataXml( String name )
	{
		String sType = "";
			
		if ( ivDataType == ATTRIBUTE_DATA )
		{
			sType = ATTRIBUTE_STRING;
		}
		else
		{
			sType = PARAMETER_STRING;
		}
	
		return( "<" + sType + ">" + "<name>" + name + "</name>" +  "<value>" + this.getValue( name ) + "</value>" + "</" + sType + ">" );
	}

	/**
	 * Formats a data item in to a string for output.
	 * 
	 * @param name the name
	 * 
	 * @return String
	 */
	private String getDataString( String name )
	{
		String sType = "";
			
		if ( ivDataType == ATTRIBUTE_DATA )
		{
			sType = ATTRIBUTE_STRING;
		}
		else
		{
			sType = PARAMETER_STRING;
		}
	
		return( sType + " : " + name + ", Value : " + this.getValue( name ) + "\n" );
	}
}
