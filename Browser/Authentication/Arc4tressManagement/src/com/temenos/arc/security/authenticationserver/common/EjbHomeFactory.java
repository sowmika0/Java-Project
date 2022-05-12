package com.temenos.arc.security.authenticationserver.common;

import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import javax.ejb.EJBHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public final class EjbHomeFactory {
	protected static Logger logger = LoggerFactory.getLogger(EjbHomeFactory.class);
	
	private static AuthenticationServerConfiguration config;
	private static Map<String, Object> ejbContext;
    private static boolean ConnectionMade = false; 
    private static Object obj = null;
    private static Hashtable<String, String> env = new Hashtable<String, String>();
    
    private static String ArcAuthServerContextFactory = "";
    private static String ArcAuthServerNamingPackage = "";
	private static String Delimeter = "";
	private static String UrlString = "";
	private static String currentURL = "";
	
	private static boolean allConnectionsFailed = false;
	
	public static final String CONTEXT_FACTORY = "java.naming.factory.initial";
	public static final String NAMING_PACKAGE = "java.naming.factory.url.pkgs";
	public static final String SERVER_URL = "java.naming.provider.url";
	public static final String CURRENT_CONTEXT = "CURRENT_CONTEXT";
	
    public static final EJBHome lookup(final String jndiName, final Class ejbHomeClass){
    	logger.info("Entering lookup of EJBHome: " + jndiName);
    	boolean lookUpSuccessful = false;
    	allConnectionsFailed = false;
    	currentURL = "";
    	do {
        	try {
        		Context ctx = null;
        		ctx = getCurrentContext();
        		
        		if (ctx == null) {
        			getNewContextandJNDI(jndiName);
        		} else {
        			obj = ctx.lookup(jndiName);
        		}
        		logger.info("lookup successful for EJBHome: " + jndiName);
        		lookUpSuccessful = true;
        	}       
        	catch(NamingException e){
        		ejbContext.remove(CURRENT_CONTEXT);
        		logger.error("error getting ejb : " + jndiName + " for URL : " + currentURL, e);
        	}
        	catch(Exception e){
        		ejbContext.remove(CURRENT_CONTEXT);
        		logger.error("error getting ejb: " + jndiName + " for URL : " + currentURL, e);
        	}
    	} while (!allConnectionsFailed && (!(lookUpSuccessful)));
	
    	if (allConnectionsFailed) {
    		throw new ArcAuthenticationServerConnectionException("Connection attempt to all servers failed");
    	}
    	
    	return (EJBHome) PortableRemoteObject.narrow(obj, ejbHomeClass); 	
    }
    
    private static Context getCurrentContext() {
    	Context ctxValue = null;
		if (ejbContext != null && !ejbContext.isEmpty() && ejbContext.containsKey(CURRENT_CONTEXT)) {
						
			try {
				ctxValue = (Context)ejbContext.get(CURRENT_CONTEXT);
				currentURL = (String) ctxValue.getEnvironment().get(SERVER_URL);
			} catch (NamingException e) {
				logger.error("error while getting current context " + e.getMessage());
			} catch (Exception e) {
				logger.error("error while getting current context " + e.getMessage());
			}
		}		
		return ctxValue;
    }
    
    private static void getNewContextandJNDI(final String jndiName) {
    	initialiseConfigValues();
    	Context ctxValue = null;
    	ConnectionMade = false;
    	ejbContext = new HashMap<String, Object>();
    	String[] UrlArray = splitString(UrlString, Delimeter);
    	
    	env.put("java.naming.factory.initial", ArcAuthServerContextFactory);
        env.put("java.naming.factory.url.pkgs", ArcAuthServerNamingPackage);
        
    	for(int i=0;i<UrlArray.length; i++) {
    		if (!UrlArray[i].equals(currentURL)) {
    			env.put(SERVER_URL, UrlArray[i]);
    			try {
    				ctxValue = new InitialContext(env);
    				obj = ctxValue.lookup(jndiName);
    				ejbContext.put(CURRENT_CONTEXT, ctxValue);
    				logger.info("successfully connected to 4TRESS with URL:  "+ UrlArray[i]);
    				ConnectionMade = true;
    				break;
    			}
    			catch(NamingException e)
        		{
        			logger.error("error in connecting the url "+ UrlArray[i]);
        			env.remove(SERVER_URL);
        			if( i < (UrlArray.length-1))
        				continue;
                    //throw new ArcAuthenticationServerConnectionException(e.toString());
        		}
        		catch (Exception e) {
                	logger.error("error in connecting the url "+ UrlArray[i]);
                	env.remove(SERVER_URL);
                	if( i < (UrlArray.length-1))
            			continue;
                    //throw new ArcAuthenticationServerConnectionException(e.toString());
                }
    		}
    	}
    	//if connection is still not made and currentURL has value then retry again with current URL
    	if (!ConnectionMade) {
        	if (!"".equals(currentURL)) {
        		env.put(SERVER_URL, currentURL);
        		try {
        			ctxValue = new InitialContext(env);
        			obj = ctxValue.lookup(jndiName);
        			ejbContext.put(CURRENT_CONTEXT, ctxValue);
        			ConnectionMade = true;
        			logger.info("successfully connected to FTRESS with URL of UrlString "+ currentURL);
        		} catch(NamingException e) {
        			allConnectionsFailed = true;
        			logger.error("error in connecting the url "+ currentURL);
        		} catch (Exception e) {
        			allConnectionsFailed = true;
        			logger.error("error in connecting the url "+ currentURL);
        		}
        	} else {
        		allConnectionsFailed = true;
        	}
    	}
    	
    }
    
    private static void initialiseConfigValues() {
    	config = getConfig(0);
    	ArcAuthServerContextFactory = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_SERVER_CONTEXTFACTORY);
    	ArcAuthServerNamingPackage = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_SERVER_NAMINGPACKAGE);
    	Delimeter = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_URL_DELIMETER);
    	UrlString = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_SERVER_URL);
    	
    	logger.info("UrlString: "+UrlString + ", DelimeterOfUrl: "+Delimeter);
    	
    	
    }
    
		
	public static AuthenticationServerConfiguration getConfig(int section) {
		
		//config file path should be specified in either the below configurations:
		// On WebLayer config file path is specified under the property - java.security.auth.login.config
		// On T24Server (server.config) config file path is specified under the property - ARC_CONFIG_PATH
        String configFile = System.getProperty("java.security.auth.login.config");
        if (configFile == null) {
        	configFile = System.getProperty("ARC_CONFIG_PATH");
        }
       
        
        //If ARC_CONFIG_APP_NAME property is not specified then appName value is hard coded to default value ARC
        String appName = System.getProperty("ARC_CONFIG_APP_NAME");
        if (appName == null) {
        	appName = "ARC";
        }
        
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		Map[] configMap = parser.parse();
		return new AuthenticationServerConfiguration(configMap[section]);
    }
    
    private EjbHomeFactory() {
        super();
        //config = getConfig(0);
    }
    
    private static String[] splitString(String urlString, String delimeter){
    	String[] urlArray = urlString.split("\\"+delimeter);
    	return urlArray;   	
    }
  
}
