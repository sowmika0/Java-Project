package com.temenos.t24browser.obfuscator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * Parent for obfuscator implementations which shares common original/obfuscated
 * mapping table in addition to their own mapping tables.
 * 
 * @author mludvik
 * 
 * TODO consider design without any direct access to tables fields,
 * and to add access methods. (only getters)
 */
public abstract class AbstractObfuscator implements Obfuscator {
	
	/** The COMMO n_ TABLE. */
	protected static Properties COMMON_TABLE;
	
	/** The COMMO n_ REVERS e_ TABLE. */
	protected static HashMap COMMON_REVERSE_TABLE;
	
	/**
	 * Sets common table which contains mapping of commands to obfuscated
	 * commands. Table is set once and for ever.
	 * 
	 * @param commonTable Properties with mapping of command to obfuscated
	 * commands. This table is to be shared by obfuscators that inherit from
	 * this class.
	 * 
	 * @throws RuntimeException if table was already set.
	 */
	public synchronized static void setCommonTable(Properties commonTable) {
		if(COMMON_TABLE != null)
			throw new RuntimeException("Common obfuscation table is already set!");
		if(commonTable == null)
			throw new NullPointerException();
		COMMON_TABLE = new Properties(); // defensive copy
		for(Enumeration keys = commonTable.keys(); keys.hasMoreElements(); ) {
			String key = (String)keys.nextElement();
			COMMON_TABLE.setProperty(key, commonTable.getProperty(key));
		}
		COMMON_REVERSE_TABLE = new HashMap(COMMON_TABLE.size());
		for(Enumeration keys = COMMON_TABLE.keys(); keys.hasMoreElements(); ) {
			String key = (String)keys.nextElement();
			COMMON_REVERSE_TABLE.put(COMMON_TABLE.getProperty(key), key);
		}
	}
	
	/**
	 * Sets common table which contains mapping of commands to obfuscated
	 * commands. Table is set once and for ever.
	 * 
	 * @param commonTableFile Properties file with mapping of command to obfuscated
	 * commands. This table is to be shared by obfuscators that inherit from
	 * this class.
	 * 
	 * @throws IOException If any I/O exception appeares.
	 * @throws RuntimeException if table was already set.
	 */
	public synchronized static void setCommonTable(File commonTableFile) throws IOException {
		if(COMMON_TABLE != null)
			throw new RuntimeException("Common obfuscation table is already set!");
		if(commonTableFile == null)
			throw new NullPointerException();
		Properties prop = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(
				commonTableFile));
		prop.load(new BufferedInputStream(new FileInputStream(commonTableFile)));
		in.close();
		setCommonTable(prop);
	}
	
	/**
	 * Checks if common table was already loaded.
	 * 
	 * @return true if common table was already loaded, otherwise returns false.
	 */
	public synchronized static boolean commonTableLoaded() {
		return COMMON_TABLE != null;
	}

	/**
	 * Creates AbstractObfuscator.
	 */
	public AbstractObfuscator() {}
}
