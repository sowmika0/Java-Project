package com.temenos.t24browser.security;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.debug.DebugUtils;
import com.temenos.t24browser.utils.PropertyManager;

// TODO: Auto-generated Javadoc
/**
 * Wrapper for HttpServletRequest, which does vaiable substitution (e.g. "{user_id}" is
 * replaced with the actual user id).
 * 
 * @author dburford
 */
public class VariableSubstitutionHttpServletRequest extends HttpServletRequestWrapper {
    
    /** The user id. */
    private String userId;
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VariableSubstitutionHttpServletRequest.class);
    
    /** The iv parameters. */
    private PropertyManager ivParameters;
    
    /**
     * Constructor.
     * 
     * @param request the request
     * @param ivParameters the iv parameters
     */
    public VariableSubstitutionHttpServletRequest(HttpServletRequest request, PropertyManager ivParameters) {
        super(request);
        this.ivParameters = ivParameters;
    }

    /**
     * Constructor.
     * 
     * @param request the request
     */
    public VariableSubstitutionHttpServletRequest(HttpServletRequest request) {
        this(request, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getParameter(String param) {
        String value = super.getParameter(param);
        
        if (ivParameters != null && DebugUtils.getBooleanProperty(ivParameters, DebugUtils.USE_INT_OBFUSCATION_NAME)) {            
            // Ensure we know about transaction id's for application input requests (see DebugUtils.java)
            // This is to block a potential attack
            if (param.equals("transactionId") && value != null && value.length() > 0) {
                String ofsFunction = super.getParameter("ofsFunction");
                String requestType = super.getParameter("requestType"); 
                String ofsOperation = super.getParameter("ofsOperation");
                boolean checkKeys = (ofsFunction != null && ofsFunction.equals("I") &&
                                     requestType != null && requestType.equals("OFS.APPLICATION") &&
                                     ofsOperation != null && ofsOperation.equals("PROCESS"));
                if (checkKeys) {
                    Collection keys = (Collection)getSession().getAttribute("keys");
                    if (keys != null) {
                        if (!keys.contains(value)) {
                            String msg = "Unknown transaction ID [" + value + "] blocked (not in " + keys + ")";
                            LOGGER.warn(msg);
                            throw new RuntimeException(msg);
                        }
                    }
                }
            }
        }
        
        return applySubstitution(value);
    }

    /**
     * {@inheritDoc}
     */
    public Map getParameterMap() {
        // return wrapper for original Map
       	LOGGER.info(" Returning umnodifiable parameter map");
    	return Collections.unmodifiableMap(super.getParameterMap());
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterValues(String name) {
        String[] orig = super.getParameterValues(name);
        String[] subst =null;
        if (orig!=null ) // No need to proceed when Values in null
        {
        	subst = new String[orig.length];
        	for(int i = 0; i < orig.length; i++) {
        		String value = orig[i];
        		value = applySubstitution(value);
        		subst[i] = value;
        		}
        }
        return subst;
    }
    
    /**
     * Replace.
     * 
     * @param str a string
     * 
     * @return the string
     */
    private String applySubstitution(String str) {
        if (userId == null) {
            userId = (String)this.getSession().getAttribute("BrowserUserId");
        }
        return replace(str, "{user_id}", userId);
    }
    
    /**
     * Simple replace method (compatable with Java 1.4)
     * 
     * @param str the string on which to apply the replace
     * @param pattern the pattern to replace
     * @param replace the string to replace the specified pattern with
     * 
     * @return the string
     */
    private static String replace(String str, String pattern, String replace) {
        if(str != null && replace != null) {
            // We can use String.replace if we move to Java 5
            int s = 0;
            int e = 0;
            StringBuffer result = new StringBuffer();
    
            while ((e = str.indexOf(pattern, s)) >= 0) {
                result.append(str.substring(s, e));
                result.append(replace);
                s = e+pattern.length();
            }
            result.append(str.substring(s));
            str = result.toString();
        }
        return str;
    }    
}
