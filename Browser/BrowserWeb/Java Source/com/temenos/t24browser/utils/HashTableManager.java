////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   HashTableManager
//
//  Description   :   Abstract class for managing a hashTable.
//
//  Modifications :
//
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;


// TODO: Auto-generated Javadoc
/**
 * The Class HashTableManager.
 */
public abstract class HashTableManager implements Serializable
{
  
  /** The h properties table. */
  private Hashtable hPropertiesTable;
  
  /**
   * Instantiates a new hash table manager.
   */
  public HashTableManager(){
  	hPropertiesTable = new Hashtable();
  }
  
  /**
   * Sets the hash table.
   * 
   * @param hashtable the new hash table
   */
  protected void setHashTable(Hashtable hashtable){
  	hPropertiesTable = hashtable;
  }

  /**
   * Gets the property.
   * 
   * @param propertyName the property name
   * 
   * @return the property
   */
  public Object getProperty( String propertyName )
  {
    // Find property name and return the value
    if ( hPropertiesTable.containsKey( propertyName ) )
    {
      // Property found, return Object
      return( hPropertiesTable.get( propertyName ) );
    }
    else
    {
      // Property not found
      return( null );
    }
  }
  //get the Parameter Values
  public Object getProperties()
  {    
      return( hPropertiesTable);
  }
  /**
   * Sets the property.
   * 
   * @param propertyName the property name
   * @param propertyValue the property value
   */
  public void setProperty( String propertyName, Object propertyValue )
  {
    // Check is property already stored, if it does remove old value and add new one
    if ( hPropertiesTable.containsKey( propertyName ) )
    {
      // Property exists, replace existing property value
      hPropertiesTable.remove( propertyName );
      hPropertiesTable.put( propertyName, propertyValue );
    }
    else
    {
      // Property not found, add it at the end
      hPropertiesTable.put( propertyName, propertyValue );
    }
  }
  
  /**
   * Gets the property keys.
   * 
   * @return the property keys
   */
  public Enumeration getKeys()
  {
    return( hPropertiesTable.keys() );
  }

}