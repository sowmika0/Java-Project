package com.temenos.arc.security.filter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

// TODO: Auto-generated Javadoc
/**
 * Wraps the ServletRequest in order to store parameters.
 * 
 * @author jannadani
 */
public class LoginParameterisedRequest extends HttpServletRequestWrapper implements Serializable {
    
    /** The Constant COMMAND. */
    public static final String COMMAND = "command";
    
    /** The Constant REQUEST_TYPE. */
    public static final String REQUEST_TYPE = "requestType";
    
    /** The Constant SIGNON. */
    public static final String SIGNON = "signOnName";
    
    /** The Constant PASSWORD. */
    public static final String PASSWORD = "password";
    
    /** The Constant COUNTER. */
    public static final String COUNTER = "counter";
    
    /** The Constant LOGIN_COUNTER. */
    public static final String LOGIN_COUNTER = "LoginCounter";

    /** The login parameters. */
    private Map loginParameters = null;

    /**
     * Instantiates a new login parameterised request.
     * 
     * @param request the request
     */
    public LoginParameterisedRequest(HttpServletRequest request) {
        super(request);
        Map map = new HashMap();
        map.putAll(request.getParameterMap());
        this.loginParameters = map;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getMethod()
     */
    public String getMethod() {
        return "POST"; // fools the BrowserServlet code
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
    	String[] paramArray = (String[]) this.loginParameters.get(name);
        return paramArray != null ? paramArray[0] : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameterMap()
     */
    public Map getParameterMap() {
        return Collections.unmodifiableMap(this.loginParameters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletRequestWrapper#getParameterNames()
     */
    public Enumeration getParameterNames() {
        return new Vector(this.loginParameters.keySet()).elements();
    }

    /**
     * Put.
     * 
     * @param name the name
     * @param value the value
     */
    public final void put(final String name, final String value) {
        this.loginParameters.put(name, new String[] {value});
    }
}
