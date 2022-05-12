package com.temenos.arc.security.authenticationserver.common;

import java.io.Serializable;

/**
 * Encapulates the information required by the authentication server to identify 
 * an authenticated user's session. 
 * @author jannadani
 *
 */
public final class ArcSession implements Serializable {

    private Object sessionId = null;    //TODO rename

    /** 
     * Not used?
     *
     */
    public ArcSession() {
        this("");
    }

    /**
     * Overload used when a session already exists.
     * @param id
     */
    public ArcSession(final Object id) {
        super();
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.sessionId = id;
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
        final ArcSession other = (ArcSession) obj;
        return (this.sessionId == null) ? (other.sessionId == null) : this.sessionId.equals(other.sessionId);
    }

    public final int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }


    public final String toString() {
        return this.sessionId.toString();
    }
    
    public final Object getSessionObject() {
    	return sessionId;
    }
}
