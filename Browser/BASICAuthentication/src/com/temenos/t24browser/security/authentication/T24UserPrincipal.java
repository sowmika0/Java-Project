package com.temenos.t24browser.security.authentication;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;

/**
 * T24UserPrincipal
 * 
 * @author wzahran
 */
public final class T24UserPrincipal implements Principal, Serializable, BasicAuthPrincipal {

    private String name = null;
    private Subject subject = null;
    private String userName = "";
    private String password = "";

    public T24UserPrincipal() {
        this("", new Subject());
    }

    public T24UserPrincipal(final String name) {
        this(name, new Subject());
    }

    public T24UserPrincipal(final String name, final Subject subject) {
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
        final T24UserPrincipal other = (T24UserPrincipal) obj;
        return ((this.name == null) ? (other.name == null) : (this.name.equals(other.name)))
                && ((this.subject == null) ? (other.subject == null) : (this.subject.equals(other.subject)));
    }
    
    public void setPassword(String password)
    {
    	this.password = password;
    }
    
    public void setUserName(String userName)
    {
    	this.userName = userName;
    }
    
    public String getPassword()
    {
    	return this.password;
    }
    
    public String getUserName()
    {
    	return this.userName;
    }
}
