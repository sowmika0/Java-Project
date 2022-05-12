package com.temenos.t24browser.security.authentication;

import java.io.Serializable;
import java.security.Principal;

/**
 * T24RolePrincipal
 * 
 * @author wzahran
 */
public class T24RolePrincipal implements Principal, Serializable, BasicAuthPrincipal {

    private String name = null;
    private String userName = "";
    private String password = "";

    public T24RolePrincipal() {
        this("");
    }

    public T24RolePrincipal(String name) {
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
        final T24RolePrincipal other = (T24RolePrincipal) obj;
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
