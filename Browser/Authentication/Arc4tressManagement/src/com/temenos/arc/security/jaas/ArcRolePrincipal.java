package com.temenos.arc.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class ArcRolePrincipal implements Principal, Serializable, ArcPrincipal {

    private String name = null;

    public ArcRolePrincipal() {
        this("");
    }

    public ArcRolePrincipal(String name) {
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
        final ArcRolePrincipal other = (ArcRolePrincipal) obj;
        return (this.name == null) ? (other.name == null) : (this.name.equals(other.name));
    }
}
