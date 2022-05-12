package com.temenos.arc.security.authenticationserver.rsa.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.authenticationserver.common.StringGenerator;
import com.temenos.arc.security.authenticationserver.server.ArcUserManagement;
import com.temenos.arc.security.authenticationserver.server.AuditLog;

public class UserManagement implements ArcUserManagement {
	private Logger logger;
	private AuthenticationServerConfiguration config;
    private CryptographyService crypto;
	
    public UserManagement(AuthenticationServerConfiguration config) {
        this.config = config;
        crypto = CryptographyService.getInstance(config);
        logger = LoggerFactory.getLogger(this.getClass());
    }
	
    public UserManagement() {
		this(AuthenticationServerConfiguration.getStatic());
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
	/**
	 * This method will add a user and its encrypted t24 password to
	 * a file located on the t24 server.  The location of the file is 
	 * specified in the config. 
	 * 
     * @param t24UserName T24 username which will be the same as the authentication
     * server username 
     * @param memorableData Not yet used for RSA
     * @param startDate not yet used for RSA
     * @return unencypted t24 password generated in this method 
	 */
	public String addUser(String t24UserName, String memorableData, Calendar startDate,Map custInfo) {
		// First validate the data passed in.  mem data and start date are ignored for RSA,
		// therefore output a warning message if they are passed in
		if (null!=memorableData && !memorableData.equals("")) {
			logger.warn("Ignoring supplied memorable word. Not required for RSA.");
		}
		if (null!=startDate ) {
			logger.warn("Ignoring supplied start date. Not required for RSA.");
		}
		int minUserIdLength = Integer.parseInt(config.getConfigValue(AuthenticationServerConfiguration.T24_USER_ID_MIN_LENGTH));
		if (null == t24UserName || t24UserName.equals("") || t24UserName.length() < minUserIdLength) {
			logger.error("Invalid user name passed to addUser.");
			throw new ArcAuthenticationServerException("Invalid user name passed to addUser().");
		}
		// Generate the t24 password
		int t24PasswordLength = Integer.parseInt(config.getConfigValue(AuthenticationServerConfiguration.T24_PASSWORD_LENGTH));
		String t24Password = StringGenerator.getRandomAlphaNumericString(t24PasswordLength);

		// encrypt the password and output to a file
        String encryptedT24Password = crypto.encrypt(t24Password, true);

        addToUserFile(t24UserName, encryptedT24Password);
		
		
		// return the unencrypted password to t24
		return t24Password;
	}

	private void addToUserFile(String t24UserName, String encryptedT24Password) {
		File userDetailsFile = new File(config.getConfigValue(AuthenticationServerConfiguration.ARC_USER_DETAILS_FILE));
        FileWriter writer = null;
        
    	try {
    		// This will cause data to be appended to the file
    		writer = new FileWriter(userDetailsFile, true);
    	} catch (FileNotFoundException e) {
    		try {
    			userDetailsFile.createNewFile();
    		} catch (IOException ioe) {
        		logger.error("Error creating new file for user: " + t24UserName, ioe);
        		throw new ArcAuthenticationServerException("Error creating output file for user: " +t24UserName);
    		}
    	} catch (IOException e) {
    		logger.error("Error getting file for user: " + t24UserName, e);
    		throw new ArcAuthenticationServerException("Error getting file for user: " +t24UserName);
    	}
    	BufferedWriter bw = new BufferedWriter(writer);
    	
    	try {
    		bw.write(t24UserName + " " + encryptedT24Password);
    		bw.newLine();
			writer.flush();
    	} catch (IOException e) {
    		logger.error("Error writing to file for user: " + t24UserName, e);
    		throw new ArcAuthenticationServerException("Error writing to file for user: " +t24UserName);
    	} finally {
    		try {
    			bw.close();
    		} catch (IOException e) {
    			logger.warn("Error closing buffered writer");
    		}
    		try {
    			writer.close();
    		} catch (IOException e) {
    			logger.warn("Error closing file writer");
    		}
    			
    	}
	}

	public String getArcUserId(String t24UserName) {
		logger.warn("Called getArcUserId(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}

	public void removeUser(String t24UserName) {
		// This mthod does nothing for RSA
		logger.info("Called removeUser().  This method does nothing for RSA.");
		
	}

	public boolean userExists(String t24UserName) {
		logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}
	
	public Map getUserFailureCounts(String t24UserName){
		logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}
	public Map resetFailureCounts(String t24UserName){
		logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}
	public String resetUserPassword(String t24UserName){
		logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}
	public String resetUserPin(String t24UserName){
		logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
	}
	
    public AuditLog[] getAuditLogs(String t24UserName,String fromDate,String toDate){
    	logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    }
    
    public String updateMemorableWord(String t24UserName,String memWord){
    	logger.warn("Called userExists(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    }
    
    public String updateAuthServerUserId(String t24UserName,String memWord){
    	logger.warn("Called updateAuthServerUserID(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    }
    public Map getAuthenticatorStatus(String t24UserName){
    	logger.warn("Called updateAuthServerUserID(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    	
    }
    public Map updateAuthenticatorStatus(String t24UserName,String authType,String status){
    	logger.warn("Called updateAuthServerUserID(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    	
    }
    public String updateCustomerData(String t24UserName,Map customerInfo){
    	logger.warn("Called updateAuthServerUserID(), however this is not supported for RSA.");
		throw new UnsupportedOperationException("This method is not supported for RSA.");
    }
}
