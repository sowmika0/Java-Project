////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   TabIndex
//
//  Description   :   Class for reading params from tabIndexParameter.xml and to 
//					  build XML tags based on the user preference and parameter configurations
//					  
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import javax.servlet.ServletContext;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import java.util.*;

public class TabIndex {
	
	private final String TAB_INDEX_PARAMS = "tabIndexParameters";	
	private final Logger LOGGER = LoggerFactory.getLogger(TabIndex.class);
	
	private static ServletContext ivContext = null;		// Servlet Context
	private static String contextPath = null;
	private static TabIndex tabIndexObj = null;		
	private PropertyManager ivParameters = null;	// The parameters read from file
	private String tabIndexXML = null;
	private final String ENABLE_TAB_INDEX = "enableTabIndex";	
	
	// Restricting the external object creation
	private TabIndex(){
		ivParameters = new PropertyManager( contextPath, ivContext.getInitParameter(TAB_INDEX_PARAMS));				
	}	
	
	// Read tab index using single object
	public static synchronized TabIndex readParams(ServletContext context) {
		if ( tabIndexObj == null){		
			ivContext = context;
			contextPath = context.getRealPath("");
			tabIndexObj = new TabIndex();				
		}
		return tabIndexObj;		
	}	
	
	/* Reads parameter file and build xml tags based on configuration in parameter file */
	
	public String getXML(String userTabEnable) {
		try{
			Enumeration params =  ivParameters.getKeys();
			tabIndexXML = "<tabIndex>";
			while (params.hasMoreElements()) {
				String element = (String) params.nextElement();
				tabIndexXML += "<"  + element + ">";
				//if both parameterization from front and T24 BrowserPreference matches "NO", then enable tab-index for all types of elements
				if ("NO".equalsIgnoreCase((String)ivParameters.getProperty(ENABLE_TAB_INDEX)) && userTabEnable.equalsIgnoreCase("NO")){
					tabIndexXML += "NO";
				}	
				else {
					tabIndexXML +=	((String)ivParameters.getProperty(element)).toUpperCase();
				}
				tabIndexXML += "</"  + element + ">";
			}
			tabIndexXML += "</tabIndex>";
		}
		catch(Exception e)
		{
			System.out.println("Error building tabIndexParameters");
			LOGGER.error("Error building tabIndexParameters", e);
		}		
		return tabIndexXML;
	}
	
	// To change the values permanently in TabIndex object
	public void setTabIndexProperty(String param, String value){
			ivParameters.setProperty(param, value);		
	}
	
	// To fetch the values from specific params of TabIndex object
	public String getTabIndexProperty(String param){
		return (String)ivParameters.getProperty(param);		
	}	
}
