////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   Utils
//
//  Description   :   Deals with the browserParameters.xml file.
//
//  Modifications :
//
//    19/02/08   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.servlets.BrowserServlet;


/**
 * The Class BrowserParameters.
 */
public class BrowserParameters implements Serializable
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserServlet.class);
	
	private ServletContext ivContext = null;		// Servlet Context
	private PropertyManager ivParameters = null;	// The parameters read from file
	
	public static final String BROWSER_PARAMETERS_INIT_PARAM = "browserParameters";
	public static final String DEFAULT_PRODUCT = "BROWSER";
	
	// Parameter names
	public static final String PRODUCT_PARAM = "Product";
	
 	
	/**
	 * Instantiates the browser parameters.
	 */
	public BrowserParameters( ServletContext context )
	{
		// Read the parameters from the XML file browserParameters.xml
		ivContext = context;
		String contextPath = context.getRealPath("");
	 	ivParameters = new PropertyManager( contextPath, context.getInitParameter(BROWSER_PARAMETERS_INIT_PARAM));
	 	initProductParameter();
	}
	
	/**
	 * Returns the value of a parameter.
	 *
	 * @param param the name of the parameter
	 * 
	 * @return the string
	 */
	public String getParameterValue( String param )
	{
		return( ivParameters.getParameterValue( param ) );
	}
	
	/**
	 * Returns the PropertyManager of the parameters file.
	 *
	 * @return the PropertyManager
	 */
	public PropertyManager getParameters()
	{
		return( ivParameters );
	}
	
	
	/**
	 * Reads the "Product" parameter from browserParameters.xml.
	 */
	private void initProductParameter()
	{
	 	String product = ivParameters.getParameterValue( PRODUCT_PARAM );
	 	
	 	if ( ( product == null ) || ( product.equals("") ) )
	 	{
			LOGGER.warn("Parameter '" + PRODUCT_PARAM + "' not found - using default setting = " + DEFAULT_PRODUCT);
			product = DEFAULT_PRODUCT;
	 	}
	 	
	 	LOGGER.debug("Product set to : " + product);
	 	
	 	// Save the parameter to the servlet context
	 	ivContext.setAttribute(PRODUCT_PARAM, product );
	}
}
