package com.temenos.arc.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class T24Principal implements Principal, Serializable, ArcPrincipal {

    private String name;

    public T24Principal() {
        this("");
    }

    public T24Principal(final String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
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
        final T24Principal other = (T24Principal) obj;
        return (this.name == null) ? (other.name == null) : (this.name.equals(other.name));
    }
}
