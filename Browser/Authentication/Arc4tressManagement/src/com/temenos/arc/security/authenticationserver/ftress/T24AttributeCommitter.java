package com.temenos.arc.security.authenticationserver.ftress;

import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.Committable;
import com.temenos.arc.security.jaas.JaasConfiguration;

/**
 * This {@link Committable} retrieves the impersonation credentials from 4TRESS
 * and adds them to the {@link Subject}.  This should therefore be the last <code>Committable</code>
 * to run, i.e. should be configured to be in the last login module. 
 * @author jannadani
 *
 */
public final class T24AttributeCommitter implements Committable {

    /**
     * @param config not used
     */
    public T24AttributeCommitter(final JaasConfiguration config) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.Committable#commit(javax.security.auth.Subject, java.util.Set, java.util.Set)
     */
    public final boolean commit(final LoginModule lm, final Set principals, final Set credentials)
            throws FailedLoginException {
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
        Subject subject = loginModule.getSubject();
        FtressHelpers70.getInstance().addImpersonationCredentialsTo(subject);
        return true;
    }
}
