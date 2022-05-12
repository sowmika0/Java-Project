package com.temenos.arc.security.authenticationserver.common;

import java.util.Map;

import com.aspace.ftress.interfaces70.soap.SOAPInterfaceFactory;

import com.aspace.ftress.interfaces70.ejb.Authenticator;
import com.aspace.ftress.interfaces70.ejb.AuthenticatorManager;
import com.aspace.ftress.interfaces70.ejb.DeviceManager;
import com.aspace.ftress.interfaces70.ejb.UserManager;
import com.aspace.ftress.interfaces70.ejb.CredentialManager;
import com.aspace.ftress.interfaces70.ejb.Auditor;

public final class FtressSoapFactory {
	
	private static AuthenticationServerConfiguration config;
	private static SOAPInterfaceFactory soapFactory = null;
	private static String soapURL = "";
	private static String urlDelimeter = "";
	private static String[] soapUrlArray = null;
	private static String currentSoapURL = "";
	
	
	public FtressSoapFactory() {
		super();
	}
	public FtressSoapFactory(AuthenticationServerConfiguration c) {
		super();
		config = c;
	}
	
    private static void initialiseConfigValues() {
    	if (config == null) {
    		config = getConfig(0);
    	}
    	urlDelimeter = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_URL_DELIMETER);
    	soapURL = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTH_SERVER_SOAPURL);
    	soapUrlArray = splitString(soapURL, urlDelimeter);
    	currentSoapURL = soapUrlArray[0];
    	//logger.info("UrlString: "+UrlString + ", DelimeterOfUrl: "+Delimeter);
    }
    
	private SOAPInterfaceFactory getSoapFactoryInstance() {
		if (soapFactory==null) {
			initialiseConfigValues();
			soapFactory = new SOAPInterfaceFactory(currentSoapURL);
		}
		return soapFactory;
	}
	
    private static String[] splitString(String urlString, String delimeter){
    	String[] urlArray = urlString.split("\\"+delimeter);
    	return urlArray;   	
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
	
	public final Authenticator lookupAuthenticator() {
		return (Authenticator) getSoapFactoryInstance().getAuthenticatorEJB();
	}
	
	public final AuthenticatorManager lookupAuthenticatorManager() {
		return (AuthenticatorManager) getSoapFactoryInstance().getAuthenticatorManagerEJB();
	}
	
	public final DeviceManager lookupDeviceManager() {
		return (DeviceManager) getSoapFactoryInstance().getDeviceManagerEJB();
	}
	
	public final UserManager lookupUserManager() {
		return (UserManager) getSoapFactoryInstance().getUserManagerEJB();
	}
	
	public final CredentialManager lookupCredentialManager() {
		return (CredentialManager) getSoapFactoryInstance().getCredentialManagerEJB();
	}
	
	public final Auditor lookupAuditor() {
		return (Auditor) getSoapFactoryInstance().getAuditorEJB();
	}
	
	
	private static void getNewSoapURL() {
		for(int i=0;i<soapUrlArray.length; i++) {
			
		}
		
	}
	
}
