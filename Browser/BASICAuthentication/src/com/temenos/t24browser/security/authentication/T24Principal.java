package com.temenos.t24browser.security.authentication;

import java.io.Serializable;
import java.security.Principal;

/**
 * T24Principal
 * 
 * @author wzahran
 */

public class T24Principal implements Principal, Serializable, BasicAuthPrincipal {

    private String name;

    private String userName = "";
    private String password = "";
    
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
