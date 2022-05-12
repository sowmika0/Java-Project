package com.temenos.arc.security.jaas;

/** 
 * Uses {@link DelegateFactory} to construct a {@link Committable} instance from a given config.
 * @author jannadani
 *
 */
public final class CommitterFactory {

    public static final Committable create(final JaasConfiguration config) throws LoginModuleException {
        //
        String className = config.getConfigValue(JaasConfiguration.AUTHENTICATION_COMMITTER);
        Object obj = DelegateFactory.create(className, new Class[] {JaasConfiguration.class},
                new Object[] { config });
        if (!Committable.class.isAssignableFrom(obj.getClass())) {
            throw new LoginModuleException("Expected interface not implemented by " + className);
        }
        return (Committable) obj;
    }

    private CommitterFactory() {
        super();
    }
}
