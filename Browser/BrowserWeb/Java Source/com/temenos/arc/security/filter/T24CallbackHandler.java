package com.temenos.arc.security.filter;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.temenos.arc.security.jaas.JaasConfiguration;

public class T24CallbackHandler implements CallbackHandler {
    
    private String username;
    private String password;
    
    public T24CallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {

        boolean usernameHandled = false;
        boolean passwordHandled = false;
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                ((NameCallback) callbacks[i]).setName(username);
                usernameHandled = true;
            }
            if (callbacks[i] instanceof PasswordCallback) {
                ((PasswordCallback) callbacks[i]).setPassword(password.toCharArray());
                passwordHandled = true;
            }
        }
        if (!(usernameHandled && passwordHandled)) {
            throw new IOException("Failed to get necessary credentials to handle callbacks");
        }
            
    }

}
