package com.temenos.arc.security.jaas;

import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.ftress.FtressLoginModule;

/**
 * Implementatin of the {@link Committable} interface that sets up the desired relationships 
 * between the {@link Subject}, {@link ArcCredential}, {@link ArcUserPrincipal} 
 * and {@link ArcLoginModule} instances.
 * @author jannadani
 *
 */
final class JaasCommitter implements Committable {

    JaasCommitter(JaasConfiguration config) {
        super();
    }

    public boolean commit(LoginModule lm, Set principals, Set credentials) throws ArcAuthenticationServerException,
            FailedLoginException {
        boolean result = true;
        ArcLoginModule loginModule = null;
        if (lm instanceof ArcLoginModule) { 
            loginModule = (ArcLoginModule)lm;
        } else {
            return false;
        }
        if (loginModule == null) {
            throw new IllegalArgumentException("Subject required");
        }
        if (principals == null) {
            throw new IllegalArgumentException("User and role principals required");
        }
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials required");
        }
        // Initial default committing - get the subject and add the principals, credentials
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
        return result;
    }
       
}
