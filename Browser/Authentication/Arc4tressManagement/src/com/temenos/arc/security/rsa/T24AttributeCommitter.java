package com.temenos.arc.security.rsa;

import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.authenticationserver.common.CryptographyService;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.Committable;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.arc.security.authenticationserver.ftress.FtressLoginModule;

/**
 * This {@link Committable} retrieves the impersonation credentials from 4TRESS
 * and adds them to the {@link Subject}.  This should therefore be the last <code>Committable</code>
 * to run, i.e. should be configured to be in the last login module. 
 * @author jannadani
 *
 */
public final class T24AttributeCommitter implements Committable {
	private static Logger logger=LoggerFactory.getLogger(T24AttributeCommitter.class);
	private JaasConfiguration config;
    /**
     * @param config not used
     */
    public T24AttributeCommitter(final JaasConfiguration config) {
    	this.config=config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.Committable#commit(javax.security.auth.Subject, java.util.Set, java.util.Set)
     */
    public final boolean commit(final LoginModule lm, final Set principals, final Set credentials)
            throws FailedLoginException {
    	logger.info("Entering RSA T24AttributeCommitter...");
        ArcLoginModule loginModule = null;
        if (lm instanceof ArcLoginModule) { 
            loginModule = (ArcLoginModule)lm;
        } else {
            return false;
        }
        if (loginModule == null) {
        	logger.error("No subject provided.");
            throw new IllegalArgumentException("Subject required");
        }
        if (principals == null) {
        	logger.error("No principals provided.");
            throw new IllegalArgumentException("User and role principals required");
        }
        if (credentials == null) {
        	logger.error("No credentials provided.");
            throw new IllegalArgumentException("Credentials required");
        }
        Subject subject = loginModule.getSubject();
        subject.getPrincipals().addAll(principals);
        // TODO SJP 23/11/2006 Session ID should be a private credential so that we can control access to it via permissions
        subject.getPublicCredentials().addAll(credentials);
        
        // Merge the subjects
        Set arcPrincipals = subject.getPrincipals(ArcUserPrincipal.class);
        if (arcPrincipals.isEmpty()) {
            throw new IllegalStateException(
                    "User Principal should have been added to the Subject at this point");
        }
        if (arcPrincipals.size() != 1) {
            throw new IllegalStateException("Only one ARC user Principal expected");
        }
        ArcUserPrincipal principal = (ArcUserPrincipal) arcPrincipals.iterator().next();
        principal.setSubject(subject);

        // Merge the modules
        // TODO SJP 06/12/2006 Watch out for this public vs private credentials thing!
        Set arcCredentials = subject.getPublicCredentials(ArcLoginModule.class);
        if (!arcCredentials.isEmpty()) {
            throw new IllegalStateException(
                    "There should be no ArcLoginModule added to the Subject yet");
        }
        
        // VR - Original code here put the login module in the arcSession.  This does not really
        // fit in with our class/package structure therefore the login module is now being 
        // put in the subject instead. 
        subject.getPublicCredentials().add(loginModule);

    	logger.info("Getting User id...");
        // arc user id is the same as the t24 user id, however it must be encrypted
        String userId = loginModule.getUserPrincipal().getName();
    	logger.info("User id is: " + userId);
        CryptographyService cryptService = CryptographyService.getInstance(config);
        String encUserId = cryptService.encrypt(userId, false);
    	logger.info("Encrypted user id is: " + encUserId);

        
    	logger.info("Adding impersonation credentials...");
        RSAHelpers.addImpersonationCredentialsTo(subject, encUserId, loginModule.getPassword());

    	logger.info("Returning successfully from commit...");
        return true;
    }

}
