////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XSLBean
//
//  Description   :   Bean for caching XSL as servlet container is loading up
//
//  Modifications :
//
//    22/11/02   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.xslt;

import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

// TODO: Auto-generated Javadoc
/**
 * The Class XSLBean.
 */
public class XSLBean implements Serializable
{
	
	/** The cache table. */
	Hashtable cacheTable = new Hashtable();		// Cache of compiled stylesheets

	/**
	 * Instantiates a new XSL bean.
	 */
	public XSLBean()
	{
	}
	
	/**
	 * Instantiates a new XSL bean.
	 * 
	 * @param config the config
	 */
	public XSLBean(ServletConfig config)
	{	
		ServletContext context = config.getServletContext();
		TransformerFactory factory = TransformerFactory.newInstance();
		
		Enumeration initParam = config.getInitParameterNames();
		
		while (initParam.hasMoreElements() )
		{
			String paramName = (String) initParam.nextElement();
			String paramValue = config.getInitParameter(paramName);
		
			try
			{
				String tempLocation = context.getRealPath( paramValue );
			
				File xslFile = new File( tempLocation );
			
 	 			Templates templates = factory.newTemplates( new StreamSource( xslFile ) );
 	   			XSLTemplateWrapper wrapper = new XSLTemplateWrapper( templates, xslFile );
 	   			cacheTable.put( tempLocation, wrapper );
			}
			catch (javax.xml.transform.TransformerConfigurationException te) 
			{
				System.out.println("Can't find the xsl file " + te.getMessage() );
				context.log("Can't find the xsl file " + te.getMessage());
			}
		}
	}

	/**
	 * Gets the XSL cache.
	 * 
	 * @return the XSL cache
	 */
	public Hashtable getXSLCache()
	{
		return cacheTable;
	}
}

