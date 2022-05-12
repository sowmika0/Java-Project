package com.temenos.arc.security.jaas;

import java.io.Serializable;

/**
 * Encapulates the information required by the authentication server to identify 
 * an authenticated user's session. 
 * @author jannadani
 *
 */
public final class T24ImpersonateCredential implements Serializable {

    private String t24ImpersonateUserCredential = null;    //TODO rename

    /** 
     * Not used?
     *
     */
    public T24ImpersonateCredential() {
        this("");
    }

    /**
     * Overload used when a session already exists.
     * @param id
     */
    public T24ImpersonateCredential(final String credential) {
        super();
        this.t24ImpersonateUserCredential = credential;
    }

    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final T24ImpersonateCredential other = (T24ImpersonateCredential) obj;
        return (this.t24ImpersonateUserCredential == null) ? (other.t24ImpersonateUserCredential == null) : this.t24ImpersonateUserCredential.equals(other.t24ImpersonateUserCredential);
    }

    public final int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((t24ImpersonateUserCredential == null) ? 0 : t24ImpersonateUserCredential.hashCode());
        return result;
    }
    
    public final String getImpersonateCredential() {
    	return t24ImpersonateUserCredential;
    }
}
