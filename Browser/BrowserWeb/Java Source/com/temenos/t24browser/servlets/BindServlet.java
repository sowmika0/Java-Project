package com.temenos.t24browser.servlets;

import java.io.IOException;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerConnectionException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.temenos.arc.security.authenticationserver.common.AbstractAuthenticator;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.web.UserManagement;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Requires VM parameters -DARC_CONFIG_PATH="[path to config file - sample in /conf]" -DARC_CONFIG_APP_NAME="[appname in config file]".
 * 
 * @author jannadani
 */
public class BindServlet extends HttpServlet {
	// TODO: These names should not be hard coded!
    /** The Constant USER_ID. */
	public static final String USER_ID = "userId";
    
    /** The Constant DEVICE_SER. */
    public static final String DEVICE_SER = "deviceSer";
    
    /** The Constant PIN. */
    public static final String PIN = "pin";
    
    /** The Constant CONFIRM_PIN. */
    public static final String CONFIRM_PIN = "confirmPin";
    
    /** The Constant MEM_DATA. */
    public static final String MEM_DATA = "memData";
    
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(BindServlet.class);
    
   
   

    /**
     * Helper class to perform the binding for given user request.
     * 
     * @author jannadani
     */
    static class Binder {
        
        /** The user id. */
        private String userId;
        
        /** The device ser. */
        private String deviceSer;
        
        /** The pin. */
        private String pin;
        
        /** The confirm pin. */
        private String confirmPin;
        
        /** The mem data. */
        private String memData;
        
        /** The user manager. */
        private UserManagement userManager = null;
        
        /** The ERROR Information **/
        private int ERROR_CODE;
      
        /**
         * Instantiates a new binder.
         * 
         * @param request the request
         */
        Binder(HttpServletRequest request) {
        	ConfigurationFileParser configFileParser = new ConfigurationFileParser();
        	Map configMap = configFileParser.parse()[0];
        	userManager = new UserManagement(new AuthenticationServerConfiguration(configMap));
            userId = request.getParameter(USER_ID);
            deviceSer = request.getParameter(DEVICE_SER);
            pin = request.getParameter(PIN);
            confirmPin = request.getParameter(CONFIRM_PIN);
            memData = request.getParameter(MEM_DATA);
            logger.debug("User id: " + userId);
            logger.debug("Device Ser: " + deviceSer);
            logger.debug("PIN: " + pin);
            logger.debug("Confirm pin: " + confirmPin);
            logger.debug("Mem Data: " + memData);
                       
        }

        /**
         * Validate.
         * 
         * @return true if the expected parameters are in the request
         */
        boolean validate() {
        	logger.info("Validating data");
            if (userId == null || userId.length() == 0) {
            	logger.error("user id is Empty");
            	ERROR_CODE = 1;
                return false;
            }
            if (deviceSer == null || deviceSer.length() == 0) {
            	logger.error("device serial id is invalid");
            	ERROR_CODE = 2;
                return false;
            }
            if (pin == null || pin.length() == 0) {
            	logger.error("PIN is invalid");
            	ERROR_CODE = 3;
                return false;
            }
            if (!pin.equals(confirmPin)) {
            	logger.error("PIN and confirmation do not match.");
            	ERROR_CODE = 4;
                return false;
            }
            // TODO memdata
            return true;
        }

        /**
         * Checks if is pre check OK.
         * 
         * @return true if the userId and device exist in the auth server
         */
        boolean isPreCheckOK() {
        	logger.info("Doing the pre checks");
            AbstractAuthenticator auth = null;
            boolean shouldProceed2 = false;
            try {
            	logger.info("	getting the authenticator");
            	try{
            		auth = userManager.getAuthenticatorForPreChecks(userId, memData);
            	}catch(Exception e){
            		ERROR_CODE = 6;
            	}
            	logger.info("	is user id valid?");
                boolean shouldProceed1 = userManager.isUserIdValid(auth.getArcSession(), userId);
                logger.info("	is the device serial number valid?");
                shouldProceed2 = userManager.isDeviceSerValid(auth.getArcSession(), deviceSer);
                if(!shouldProceed2){
                	ERROR_CODE = 2;
                }
                return shouldProceed1 & shouldProceed2;
            } finally {
            	if (null != auth) {
            		auth.logoff();
            	}
            }
        }

        /**
         * Checks if is bind OK.
         * 
         * @return true if the userid is bound to the device and the pin is set
         */
        boolean isBindOK() {
            int validityPeriodinYears = 100;   // TODO replace with config param
            // TODO make validity period months rather than years
            AbstractAuthenticator auth = null;
            int stage = 0;
            try {
            	logger.info("About to login with mem data");
            	auth = userManager.loginWithMemData(userId, memData);
                stage = 1;
            	logger.info("About to add pin");
                userManager.addPin(auth.getArcSession(), pin, userId, validityPeriodinYears);
                stage = 2;
            	logger.info("About to bind user");
                userManager.bindUser(deviceSer, auth.getArcSession(), userId, validityPeriodinYears);
                stage = 3;
            	logger.info("About to delete mem data");
                userManager.deleteMemorableData(auth.getArcSession(), userId);
                return true;
            } finally {
            	// if pin authenticator has been added, then remove it
            	if (stage==2) {
                	logger.info("Deleting PIN");
            		userManager.deletePin(auth.getArcSession(), userId);
            	}
            	//then log out
            	if (null != auth) {
                	logger.info("	logging off");
            		auth.logoff();
            	}
            	
            }
        }
        
        
    }   // end class Binder

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Binder binder = new Binder(request);
        try {
                if ( binder.validate()
                  && binder.isPreCheckOK()
                  && binder.isBindOK() ) {
                	// todo: do not explicitly redirect the servlet, redirect via web.xml
                    request.getRequestDispatcher("/modelbank/unprotected/bind_success.jsp").forward(request, response);
                } else {
                    request.setAttribute("Error_code",binder.ERROR_CODE);	 
					// todo: use a more specific error (and don't catch it); present a more specific error via the web.xml
                	request.getRequestDispatcher("/modelbank/unprotected/bind_failure.jsp").forward(request, response);
                }
        }
        catch(ArcAuthenticationServerConnectionException e){
        	binder.ERROR_CODE = 5;
        	request.setAttribute("Error_code",binder.ERROR_CODE);
        	logger.error(" Error in Ftress Communication "+e);
        	request.getRequestDispatcher("/modelbank/unprotected/bind_failure.jsp").forward(request, response);
        }
        catch (Exception e) {
        	request.setAttribute("Error_code",binder.ERROR_CODE);
        	logger.error("Error in binding process: " + e.getMessage());
            request.getRequestDispatcher("/modelbank/unprotected/bind_failure.jsp").forward(request, response);
        	
        }
    }
}
