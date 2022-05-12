package com.temenos.arc.security.jaas;

import java.io.Serializable;
import javax.security.auth.Destroyable;

// TODO SJP 01/12/2006 If this class hangs around, probably safest to encrypt and/or obfuscate the password
public class T24PasswordCredential implements Serializable, Destroyable, ArcCredential {

    private String passphrase;

    public T24PasswordCredential() {
        this("");
    }

    public T24PasswordCredential(final String passphrase) {
        super();
        this.passphrase = passphrase;
    }

    public void destroy() {
        this.passphrase = null;
    }

    public boolean isDestroyed() {
        return (this.passphrase == null);
    }

    public String getPassPhrase() {
        return this.passphrase;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((passphrase == null) ? 0 : passphrase.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final T24PasswordCredential other = (T24PasswordCredential) obj;
        return (this.passphrase == null) ? (other.passphrase == null) : (this.passphrase.equals(other.passphrase));
    }
}
