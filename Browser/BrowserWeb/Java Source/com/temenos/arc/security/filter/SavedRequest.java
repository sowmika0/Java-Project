package com.temenos.arc.security.filter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * SavedRequest represents a request that initiated an authorization sequence.
 * It saves the parameterMap and (HTTP) Method of the original request to be used after the user is authenticated
 * and the original request information is needed for processing.
 * 
 * Source copied from external
 */
public class SavedRequest implements Serializable {
   private Map parameterMap;
   private String method;

   /**
    * Constructor
    *
    * @param request the request to save
    */
   public SavedRequest(HttpServletRequest request) {
      parameterMap = new HashMap(request.getParameterMap());
      method = request.getMethod();
   }

   /**
    * Get a map of parameters (names & values)
    */
   public Map getParameterMap() {
      return parameterMap;
   }

   /**
    * Get a map of parameters (names & values)
    */
   public String getParameter(String key) {
       Object paramObject = parameterMap.get(key);
       if (null!=paramObject)
           return (String)paramObject;
       else
           return null;
   }

   /**
    * Get the HTTP method
    */
   public String getMethod() {
      return method;
   }
}
