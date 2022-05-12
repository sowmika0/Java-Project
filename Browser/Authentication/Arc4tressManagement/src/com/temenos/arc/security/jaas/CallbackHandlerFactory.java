package com.temenos.arc.security.jaas;

import javax.security.auth.callback.CallbackHandler;

/**
 * Uses {@link DelegateFactory} to construct a CallbackHandler instance from the specified config. 
 * @author jannadani (actually, spark)
 *
 */
final class CallbackHandlerFactory {

    static final CallbackHandler create(final JaasConfiguration config) throws LoginModuleException {
        //
        String className = config.getConfigValue(JaasConfiguration.CALLBACK_OVERRIDE);
        Object obj = DelegateFactory.create(className, new Class[] { JaasConfiguration.class },
                new Object[] { config });
        if (!CallbackHandler.class.isAssignableFrom(obj.getClass())) {
            throw new LoginModuleException("Expected interface not implemented by " + className);
        }
        return (CallbackHandler) obj;
    }

    private CallbackHandlerFactory() {
        super();
    }
}
