package com.temenos.t24browser.servlets;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.ftress.FtressHelpers70;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * This class will send the OTP to OOB users using configured delivery gateway
 * This class will interact with 4tress server 7.0 and perform authentication
 * This class was developed based on the AuthenticationManager class.
 * 
 * @author pradeepm
 *
 */

public class OOBTransactionAuthentication {

	
	private static AuthenticationServerConfiguration config;
	private static final Logger logger = LoggerFactory.getLogger(OOBTransactionAuthentication.class);
	public static final String TRANS_AUTHENTICATOR_LOCKED = "TRANS_AUTHENTICATOR_LOCKED";
	public static final String TRANS_AUTHENTICATOR_ERROR = "TRANS_AUTHENTICATOR_ERROR";
	public static final String TRANS_ERROR_PAGE = "TRANS_ERROR_PAGE";
	
	private static int OOB_OTP_AUTH_SUCCESS = 0;
	private static int OOB_OTP_AUTH_FAILURE = 1;
	private static int OOB_OTP_AUTH_LOCKED = 2;
	private static int OOB_OTP_AUTH_ERROR = 3;

	static {
		config = getconfig(0);
	}
	
	public OOBTransactionAuthentication(){
		super();
	}
	
	public static AuthenticationServerConfiguration getconfig(int section) {
		String configFile = System.getProperty("java.security.auth.login.config");
		String appName = System.getProperty("ARC_CONFIG_APP_NAME");
		ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
		Map[] configMap = parser.parse();
		return new AuthenticationServerConfiguration(configMap[section]);
	}
	
	public boolean sendAuthenticationOOBOTPValue(String username, String currentALSI, HttpSession session){
		boolean OOBOTPSent;
		OOBOTPSent = false;
		logger.info("entering method sendAuthenticationOOAOTPValue ");
		
		try {
			if (currentALSI != null) {
				session.setAttribute("OOB_ALSI",currentALSI);
				FtressHelpers70.setConfig(config);
				OOBOTPSent = FtressHelpers70.getInstance().sendAuthenticationOOBOTPValue(username, currentALSI, session.getId());	
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ArcAuthenticationServerException("Connection Failure : Error while sending OTP to user");
		}
		return OOBOTPSent;
	}
	
	public boolean doOOBAuthentication(String userName, String oobValue, HttpSession session) {
		
		int OOBAuthenticationStatus = OOB_OTP_AUTH_ERROR;
		boolean OOBAuthenticationSuccess = false;
		String currentALSI = (String) session.getAttribute("OOB_ALSI");
		
		try {
			logger.info("doOOBAuthentication Called");
			FtressHelpers70.setConfig(config);
			OOBAuthenticationStatus = FtressHelpers70.getInstance().doOOBOTPAuthentication(userName, currentALSI,  session.getId(), oobValue);
		}
		catch(Exception e) {
			OOBAuthenticationStatus = OOB_OTP_AUTH_ERROR;
			logger.error(e.getMessage(), e);
			//throw new ArcAuthenticationServerException("Connection Failure : Error occured while authenticating OOB Value");
		}
		
			if (OOBAuthenticationStatus == OOB_OTP_AUTH_SUCCESS){
				OOBAuthenticationSuccess = true;
				logger.info("User OOB OTP authenticated successfully...");
			}
			else {
				String transErrorPage = getTransactionErrorPage();
				if (OOBAuthenticationStatus == OOB_OTP_AUTH_LOCKED) {
					session.setAttribute(TRANS_AUTHENTICATOR_LOCKED, "true");
					session.setAttribute(TRANS_ERROR_PAGE,transErrorPage);
					logger.info("User OOB OTP authentication failed, maximum failure count reached");
				} else if (OOBAuthenticationStatus == OOB_OTP_AUTH_ERROR) {
					session.setAttribute(TRANS_AUTHENTICATOR_ERROR, "true");
					session.setAttribute(TRANS_ERROR_PAGE,transErrorPage);
				}
			}
			return OOBAuthenticationSuccess;
	}
	
	private String getTransactionErrorPage() {
		String transErrorPage = "/jsps/transaction_error.jsp";
		if (config.getConfigValue(AuthenticationServerConfiguration.TRANSACTION_ERROR_PAGE) != null) {
			transErrorPage = config.getConfigValue(AuthenticationServerConfiguration.TRANSACTION_ERROR_PAGE);
		}
		return transErrorPage;
	}

}
