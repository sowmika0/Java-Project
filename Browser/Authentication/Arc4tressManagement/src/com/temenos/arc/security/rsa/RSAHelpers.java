package com.temenos.arc.security.rsa;

import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;

import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;
import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.jaas.LoginModuleException;
import com.temenos.arc.security.jaas.T24PasswordCredential;
import com.temenos.arc.security.jaas.T24Principal;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 *  Utility class to wrap authentication server specific code.  
 * @author vraghavan
 *
 */
public class RSAHelpers {
	private static AuthenticationServerConfiguration config = null;
	private static Logger logger = LoggerFactory.getLogger(RSAHelpers.class);

	public static void setConfig(AuthenticationServerConfiguration c) {
		config = c;
	}
    

    /**
     * Gets the ArcSession and credentials from the passed in subject, uses them to retrieve the 
     * T24 credentials from the authentication server and then adds these to the subject
     * @param subject in/out parameter described above.
     * @throws AuthenticationServerException
     */
    public static void addImpersonationCredentialsTo(final Subject subject, final String userId, final String password) throws LoginModuleException, FailedLoginException {
    	logger.debug("Entering addImpersonationCredentialsTo...");
        
        subject.getPrincipals().add(new T24Principal(userId));
        subject.getPublicCredentials().add(new T24PasswordCredential(password));

        if (null == password || password.equals("")) {
        	logger.error("T24 impersonation credentials not available from 4TRESS user");
        	throw new LoginModuleException("T24 impersonation credentials not available from 4TRESS user");
        }
    	logger.debug("Successfully added impersonation credentials...");
    }

    /**
     * Authenticates user and returns the result.
     * @param userId
     * @param pin
     * @param otp
     * @return AuthenticationResponse 
     */
    public static AuthenticationResponse authenticate(String userId, String pin, String otp) {
    	logger.debug("Entering authenticate");
    	AuthSession session = null;
    	int authResponse = 0;
    	try {
        	logger.debug("Getting the auth session factory");
        	String rsaConfig = config.getConfigValue(AuthenticationServerConfiguration.ARC_AUTHENTICATION_SERVER_CONFIG);
        	logger.debug("rsa config file : " + rsaConfig);
    		AuthSessionFactory factory = AuthSessionFactory.getInstance(rsaConfig);
        	logger.debug("Creating the user session");
	    	session = factory.createUserSession();
	    	logger.debug("Checking the user session");
	    	authResponse = session.check(userId,pin.trim() + otp.trim());
	    	logger.debug("Completed authentication");
    	} catch (AuthAgentException e) {
    		logger.error("Failed to login to authentication server" +  e.getMessage());
    		throw new ArcAuthenticationServerException("Failed to login to authentication server");
    	} catch (Exception e) {
    		logger.error("Failed to login to authentication server2" +  e.getMessage());
    		throw new ArcAuthenticationServerException("Failed to login to authentication server 2");
    	}
    	logger.debug("Authentication response: " + authResponse);

    	return new AuthenticationResponse(session, authResponse);
    }
    
    /**
     * @param credentials
     * @return
     */
    private static ArcSession sessionIdentifierFrom(final Set credentials) {
        if (credentials.isEmpty()) {
            throw new IllegalArgumentException("Session Credentials required");
        }
        ArcSession arcSession = null;
        for (Iterator iter = credentials.iterator(); iter.hasNext();) {
            Object obj = (Object) iter.next();
            if (obj instanceof ArcSession) {
                arcSession = (ArcSession) obj;
                break;
            }
        }
        if (arcSession == null) {
            throw new IllegalStateException("ArcSession required");
        }
        return arcSession;
    }
    
//    /**
//     * @return 0 if config says mem word is not seeded, otherwise the number of seeds
//     */
//    private static int getNumSeedsFromConfig() {
//        if (!"true".equalsIgnoreCase(config.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_IS_SEEDED))) {
//            return 0;
//        }
//        
//        String numSeedsParam= config.getConfigValue(AuthenticationServerConfiguration.MEM_WORD_NUM_SEEDS);
//        return Integer.parseInt(numSeedsParam);
//    }
}
