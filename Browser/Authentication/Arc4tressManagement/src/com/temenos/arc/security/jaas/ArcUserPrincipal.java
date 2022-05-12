package com.temenos.arc.security.jaas;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;

public final class ArcUserPrincipal implements Principal, Serializable, ArcPrincipal {

    private String name = null;
    private Subject subject = null;

    public ArcUserPrincipal() {
        this("", new Subject());
    }

    public ArcUserPrincipal(final String name) {
        this(name, new Subject());
    }

    public ArcUserPrincipal(final String name, final Subject subject) {
        super();
        this.name = name;
        this.subject = subject;
    }

    public final String getName() {
        return this.name;
    }

    public final Subject getSubject() {
        return this.subject;
    }

    public final void setSubject(final Subject subject) {
        this.subject = subject;
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
        final ArcUserPrincipal other = (ArcUserPrincipal) obj;
        return ((this.name == null) ? (other.name == null) : (this.name.equals(other.name)))
                && ((this.subject == null) ? (other.subject == null) : (this.subject.equals(other.subject)));
    }
}
