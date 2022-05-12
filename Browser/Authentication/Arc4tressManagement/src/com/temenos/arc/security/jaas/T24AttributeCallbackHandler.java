package com.temenos.arc.security.jaas;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

//TODO YJA do we still need this? 
class T24AttributeCallbackHandler implements CallbackHandler {

    private final JaasConfiguration config;

    public T24AttributeCallbackHandler(final JaasConfiguration config) {
        this.config = config;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        boolean usernameHandled = false;
        boolean passwordHandled = false;
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                String username = config.getConfigValue(JaasConfiguration.UP_AUTH_USER);
                ((NameCallback) callbacks[i]).setName(username);
                usernameHandled = true;
            }
            if (callbacks[i] instanceof PasswordCallback) {
                // TODO SJP 04/12/2006 Storing the password in the JAAS config file *stinks* big time! Encrypt it, or move it.
                String password = config.getConfigValue(JaasConfiguration.UP_AUTH_PASSWORD);
                ((PasswordCallback) callbacks[i]).setPassword(password.toCharArray());
                passwordHandled = true;
            }
        }
        if (!(usernameHandled && passwordHandled)) {
            throw new IOException("Failed to find necessary credentials to handle callbacks");
        }
    }
}
