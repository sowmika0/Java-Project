package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;
import com.temenos.arc.security.AuthenticationConstants;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.rsa.AuthenticationResponse;
import com.temenos.arc.security.rsa.RSAHelpers;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class for Servlet: LoginServlet
 * This servlet deals with input for an RSA Authentication Manager Token / PIN
 * login.  It works for Authentication Manager (ACE) v5.2 and 6.1
 */
public class RsaDevicePinLoginServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	
    
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(RsaDevicePinLoginServlet.class);
    
    /** The config. */
    private static JaasConfiguration config;  

    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	/**
     * Instantiates a new device pin login servlet.
     */
    public RsaDevicePinLoginServlet() {
		super();
		logger.info("Constructing DevicePinLoginServlet");
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entering doPost...");
        if (null == config) {
            // Set up System properties if not already set
            // Get the properties from the init parameters if necessary
           
            String appName = "ARC_CONFIG_APP_NAME";
            if (null == System.getProperty(appName)) {
                String appNameValue = getServletConfig().getInitParameter(appName);
                if (null != appNameValue) {
                    System.setProperty(appName, appNameValue);
                }
                logger.info("app name: " + appNameValue);
            }
            config = getConfig(0);
        }
		// There are 3 possible points at which this method of the servlet could be called.
		// 1. userId is entered and we need to get the seed positions
		// 2. password/seed characters have been entered and either submit to JAAS or goto 
		//    change password screen if expired password or 
		// 3. If password needs to be changed after expiry/first login then need to update
		//    password before submitting to JAAS

		// The user Id must be in upper case as this is the convention used in the
        // T24 external user.  RSA is case insensitive so it does not matter what case the 
        // user id is.
        
        logger.debug("User id: " + request.getParameter(AuthenticationConstants.USER_ID));
        logger.debug("PIN: " + request.getParameter(AuthenticationConstants.PIN));
        logger.debug("OTP: " + request.getParameter(AuthenticationConstants.OTP));
        logger.debug("Next OTP: " + request.getParameter(AuthenticationConstants.NEXT_OTP));
        logger.debug("New Pin1: " + request.getParameter(AuthenticationConstants.NEW_PIN1));
        logger.debug("New Pin2: " + request.getParameter(AuthenticationConstants.NEW_PIN2));
        
        try {
            if (null != request.getParameter(AuthenticationConstants.USER_ID) && null != request.getParameter(AuthenticationConstants.PIN) && null != request.getParameter(AuthenticationConstants.OTP)) {
                logger.info("Now about to attempt authentication...");
            	// clear the session attributes from a failed login
            	request.getSession().removeAttribute(AuthenticationConstants.USER_ID);
            	request.getSession().removeAttribute(AuthenticationConstants.PIN);
            	request.getSession().removeAttribute(AuthenticationConstants.OTP);
            	request.getSession().removeAttribute(AuthenticationConstants.NEXT_OTP);
            	request.getSession().removeAttribute(AuthenticationConstants.NEW_PIN1);
            	request.getSession().removeAttribute(AuthenticationConstants.NEW_PIN2);
            	request.getSession().removeAttribute(AuthenticationConstants.PASSPHRASE);
            	// Need to remove this as otherwise the auth filter will get confused
            	// It could still exist from a previous attempt to log in
            	request.getSession().removeAttribute(ArcUserPrincipal.class.getName());
    
    			request.getSession().setAttribute(AuthenticationConstants.USER_ID, request.getParameter(AuthenticationConstants.USER_ID).toUpperCase());
    			request.getSession().setAttribute(AuthenticationConstants.PIN, request.getParameter(AuthenticationConstants.PIN));
    			request.getSession().setAttribute(AuthenticationConstants.OTP, request.getParameter(AuthenticationConstants.OTP));

                // Now authenticate the user 
                logger.info("Setting the config...");
    			RSAHelpers.setConfig(config);
                logger.info("About to authenticate...");
    
            	logger.info("Entering authenticate");
            	AuthSession rsaSession = null;
            	int authResp = 0;
            	try {
                	logger.info("Getting the auth session factory");
                	String rsaConfig = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTHENTICATION_SERVER_CONFIG);
                	logger.info("rsa config file : " + rsaConfig);
                    logger.debug("AuthSessionFactory class loader: " + AuthSessionFactory.class.getClassLoader());
            		AuthSessionFactory factory = AuthSessionFactory.getInstance(rsaConfig);
                	logger.info("Creating the user session");
        	    	rsaSession = factory.createUserSession();
        	    	logger.info("Checking the user session");
        	    	authResp = rsaSession.check(request.getParameter(AuthenticationConstants.USER_ID).toUpperCase(),request.getParameter(AuthenticationConstants.PIN).trim() + request.getParameter(AuthenticationConstants.OTP).trim());
        	    	logger.info("Completed authentication");
            	} catch (AuthAgentException e) {
            		logger.error("Failed to login to authentication server" +  e.getMessage());
            		throw new ArcAuthenticationServerException("Failed to login to authentication server");
            	} 
            	logger.debug("Authentication response: " + authResp);
    
            	AuthenticationResponse authResponse = new AuthenticationResponse(rsaSession, authResp);
    			request.getSession().setAttribute(AuthenticationConstants.USER_ID, request.getParameter(AuthenticationConstants.USER_ID).toUpperCase());
                
                logger.info("checking authentication...");
    			if (authResponse.getAuthenticationResult() == AuthenticationResponse.ACCESS_OK) {
    				// Go to JAAS
    				// First we need to extract the t24 password 
        	    	logger.info("User " + request.getParameter(AuthenticationConstants.USER_ID).toUpperCase() + " successfully logged into the RSA Server");
    				AuthSession session = (AuthSession) authResponse.getArcSession().getSessionObject();
    				
    				String passphrase = "password" + config.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER);
    	            logger.debug("t24 passphrase: " + passphrase);
    				// need to put the ArcSession into the session so it can be retrieved during the
    				// JAAS login stage
    				request.getSession().setAttribute(ArcSession.class.getName(), new ArcSession(session));
    				request.getSession().setAttribute(AuthenticationConstants.PASSPHRASE, passphrase);
    				request.getSession().setAttribute(AuthenticationConstants.USER_ID, request.getParameter(AuthenticationConstants.USER_ID).toUpperCase());
    				// forward details to the home page
    	        	logger.info("Going to JAAS..." );
    				response.sendRedirect(request.getContextPath() + AuthenticationConstants.FORM_LOGIN_PAGE);
    			} else if (authResponse.getAuthenticationResult() == AuthenticationResponse.NEW_PIN_REQUIRED) {
        	    	logger.info("New PIN required");
    				request.getSession().setAttribute(ArcSession.class.getName(), new ArcSession(rsaSession));
    
    				// Go to new PIN page
    				request.getRequestDispatcher(AuthenticationConstants.CHANGE_PIN_PAGE).forward(request, response);
    			} else if (authResponse.getAuthenticationResult() == AuthenticationResponse.NEXT_CODE_REQUIRED) {
        	    	logger.info("Next OTP required");
    				request.getSession().setAttribute(ArcSession.class.getName(), new ArcSession(rsaSession));
    				request.getSession().setAttribute(AuthenticationConstants.USER_ID, request.getParameter(AuthenticationConstants.USER_ID).toUpperCase());
    
    				// Go to next code screen
    		        request.getRequestDispatcher(AuthenticationConstants.NEXT_OTP_PAGE).forward(request, response);
    			} else {
        	    	logger.warn("User " + request.getParameter(AuthenticationConstants.USER_ID).toUpperCase() + " failed to log into the RSA Server");
    				// Invalid login return to login page
    				throw new ArcAuthenticationServerException("Failed to log in.");
    			}
            } else if (null != request.getParameter(AuthenticationConstants.NEW_PIN1) && null != request.getParameter(AuthenticationConstants.NEW_PIN2)) {
            	// first get the arc session back
            	ArcSession session = (ArcSession) request.getSession().getAttribute(ArcSession.class.getName());
            	AuthSession rsaSession = (AuthSession) session.getSessionObject();
            	
            	if (request.getParameter(AuthenticationConstants.NEW_PIN1).equals(request.getParameter(AuthenticationConstants.NEW_PIN2))) {
    	        	// Now we have to submit the new PIN
            		int pinStatus = 1;
            		try {
            			pinStatus = rsaSession.pin(request.getParameter(AuthenticationConstants.NEW_PIN1));
                		logger.debug("PIN Status: " + pinStatus);
            		} catch (AuthAgentException e) {
                		logger.error("Failed to set new PIN", e);
                		throw new ArcAuthenticationServerException("Failed to set new PIN");
            		}
            		if (pinStatus != AuthenticationResponse.PIN_ACCEPTED) {
                		logger.error("Failed to set new PIN");
                		throw new ArcAuthenticationServerException("Failed to set new PIN, pin status is " + pinStatus);
            		}
            	} else {
            		throw new ArcAuthenticationServerException("PIN and confirmation do not match.");
            	}
            	
    			// return to the login page
            	request.getSession().invalidate();
            	logger.info("Going to login page..." );
    			response.getOutputStream().print("window.location=\"" + request.getContextPath() + AuthenticationConstants.HOME_PAGE + "\"");
    
            } else if (null != request.getParameter(AuthenticationConstants.NEXT_OTP)) {
            	// first get the arc session back
            	ArcSession session = (ArcSession) request.getSession().getAttribute(ArcSession.class.getName());
            	AuthSession rsaSession = (AuthSession) session.getSessionObject();
            	int nextOtpStatus = 1;
            	try {
            		nextOtpStatus = rsaSession.next(request.getParameter(AuthenticationConstants.NEXT_OTP));
            		logger.debug("Next Otp Status: " + nextOtpStatus);
            	} catch (AuthAgentException e) {
            		logger.error("Failed to set new PIN", e);
            		throw new ArcAuthenticationServerException("Failed to set new PIN");
        		}
        		if (nextOtpStatus != AuthenticationResponse.ACCESS_OK) {
            		logger.error("Error with next OTP");
            		throw new ArcAuthenticationServerException("Failed to enter correct next OTP: " + nextOtpStatus);
        		}
    	    	logger.info("User " + (String)request.getSession().getAttribute(AuthenticationConstants.USER_ID) + " successfully logged into the RSA Server");
    			
    //			String passphrase = t24Password + config.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER);
    			String passphrase = "password" + config.getConfigValue(JaasConfiguration.OTP_PIN_DELIMITER);
        		logger.debug("t24 passphrase: " + passphrase);
    			// need to put the ArcSession into the session so it can be retrieved during the
    			// JAAS login stage
    			request.getSession().setAttribute(ArcSession.class.getName(), new ArcSession(rsaSession));
    			request.getSession().setAttribute(AuthenticationConstants.PASSPHRASE, passphrase);
    			// forward details to the home page
            	logger.info("Going to JAAS..." );
    			response.sendRedirect(request.getContextPath() + AuthenticationConstants.FORM_LOGIN_PAGE);
            } else {
                logger.error("Error in data submitted to Authentication Manager.");
                throw new ArcAuthenticationServerException("Error in data submitted to Authentication Manager.");
            }
        } catch (ArcAuthenticationServerException e) {
            // This is a workaround for Websphere.  
            // When testing with Websphere 7.0 user gets a 500 error when exception
            // is thrown instead of being redirected to the authentication error page
            // return to the login page
            request.getSession().invalidate();
            logger.info("Going to login page..." );
            response.getOutputStream().print("window.location=\"" + request.getContextPath() + AuthenticationConstants.AUTH_ERROR_PAGE + "\"");
            
            
        }
        
	}

    /**
     * Gets the config.
     * 
     * @param section the section
     * 
     * @return the config
     */
    public static JaasConfiguration getConfig(int section) {
        String configFile = System.getProperty("java.security.auth.login.config");
        logger.info(configFile);
        String appName = System.getProperty("ARC_CONFIG_APP_NAME");
        logger.info(appName);
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		Map[] configMap = parser.parse();
		return new JaasConfiguration(configMap[section]);
    }
	
}