package com.temenos.t24browser.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.graph.Graph;
import com.temenos.t24browser.servlets.BrowserServlet;
import com.temenos.t24browser.servlets.FileUploadServlet;
import com.temenos.t24browser.servlets.UploadServlet;


/**
 * General class for all actions that should be run once on startup of the application.
 */
public class BrowserContextListener implements ServletContextListener {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserContextListener.class);

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		// Get a handle on the servlet context
		ServletContext servletContext = servletContextEvent.getServletContext();
		
		// Store browserParameters.xml values in the servlet context
		storeBrowserParameters(servletContext);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		// Nothing to do
	}
	
	/**
	 * Reads browserParameters.xml and stores values in the servletContext.
	 * @param servletContext
	 */
	private void storeBrowserParameters(ServletContext servletContext) {
		
		// Log a message to say we're here
		LOGGER.info("Storing values from browserParameters.xml in the servletContext");
		
		// Read the browserParameters file
		String contextPath = servletContext.getRealPath("");
		String fileName = servletContext.getInitParameter(BrowserServlet.BROWSER_PARAMETERS_INIT_PARAM);
		PropertyManager pm = new PropertyManager( contextPath, fileName);
		
		// Store values from the browserParameters.xml in the context.
		// At the moment, just store new ones till the system can be revised, though it would
		// be better to iterate through the lot and add them all. However this requires some 
		// refactoring and retesting eg of branch resilience.
		setContextAttribute(Graph.GRAPH_WORK_DIRECTORY_PARAM_NAME, pm, servletContext);
		setContextAttribute(FileUploadServlet.FILE_UPLOAD_CHANNEL_PARAM_NAME, pm, servletContext);
		setContextAttribute(UploadServlet.DB_UP_DOWN_LOAD_CHANNEL_PARAM_NAME, pm, servletContext);
		setContextAttribute(UploadServlet.DB_UP_DOWN_LOAD_INSTANCE, pm, servletContext);
	}
	
	private void setContextAttribute(String paramName, PropertyManager pm, ServletContext servletContext) {
		String paramValue = pm.getParameterValue(paramName);
		servletContext.setAttribute(paramName, paramValue);
	}

}
