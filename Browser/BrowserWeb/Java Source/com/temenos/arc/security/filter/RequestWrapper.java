package com.temenos.arc.security.filter;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {
    
    private Principal userPrincipal;
    private String authType;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }
    
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType=authType;
    }
}
