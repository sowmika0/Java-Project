package com.temenos.arc.security.jaas;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import com.temenos.arc.security.authenticationserver.common.ArcSession;

/**
 * Uses {@link DelegateFactory} to create {@link JaasAuthenticatable} instances based on the specified config. 
 * @author jannadani
 *
 */
 public final class JaasAuthenticatorFactory {

    public static final JaasAuthenticatable createInitial(final NameCallback nameCallback,
            final PasswordCallback passwordCallback, final JaasConfiguration config) throws LoginModuleException {
        //
        String className = config.getConfigValue(JaasConfiguration.AUTHENTICATION_DELEGATE);
        Object obj = DelegateFactory.create(className, 
        										new Class[] { NameCallback.class, PasswordCallback.class, JaasConfiguration.class }, 
        										new Object[] { nameCallback, passwordCallback, config });
        if (!JaasAuthenticatable.class.isAssignableFrom(obj.getClass())) {
            throw new LoginModuleException("Expected interface not implemented by " + className);
        }
        return (JaasAuthenticatable) obj;
    }

    static final JaasAuthenticatable create(final ArcSession sessionId, final JaasConfiguration config)
            throws LoginModuleException {
        //
        String className = config.getConfigValue(JaasConfiguration.AUTHENTICATION_DELEGATE);
        Object obj = DelegateFactory.create(className, new Class[] { ArcSession.class,
                JaasConfiguration.class }, new Object[] { sessionId, config });
        if (!JaasAuthenticatable.class.isAssignableFrom(obj.getClass())) {
            throw new LoginModuleException("Expected interface not implemented by " + className);
        }
        return (JaasAuthenticatable) obj;
    }

    private JaasAuthenticatorFactory() {
        super();
    }
}
