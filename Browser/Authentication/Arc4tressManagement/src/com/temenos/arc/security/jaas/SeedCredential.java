package com.temenos.arc.security.jaas;

import java.io.Serializable;
import javax.security.auth.Destroyable;

// TODO SJP 01/12/2006 If this class hangs around, probably safest to encrypt and/or obfuscate the password
public final class SeedCredential implements Serializable {

    private String seedPositions = null;

    public SeedCredential() {
        this("");
    }

    public SeedCredential(final String passphrase) {
        super();
        this.seedPositions = passphrase;
    }

    public void destroy() {
        this.seedPositions = null;
    }

    public boolean isDestroyed() {
        return (this.seedPositions == null);
    }

    public String getPassPhrase() {
        return this.seedPositions;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((seedPositions == null) ? 0 : seedPositions.hashCode());
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
        final SeedCredential other = (SeedCredential) obj;
        return (this.seedPositions == null) ? (other.seedPositions == null) : (this.seedPositions.equals(other.seedPositions));
    }
}
