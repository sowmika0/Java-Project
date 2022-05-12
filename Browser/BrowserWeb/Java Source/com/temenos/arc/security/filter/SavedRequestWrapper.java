package com.temenos.arc.security.filter;


import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * SavedRequestWrapper
 *
 * @author Vinod Raghavan
 */
public class SavedRequestWrapper extends HttpServletRequestWrapper {

   private HttpServletRequest currentRequest;
   private SavedRequest savedRequest;
   private String matchableURL;

	/**
    * Construct a new SecurityRequestWrapper.
    *
    * @param request the request to wrap
    * @param realm the SecurityRealmInterface implementation
    * @param savedRequest SavedRequest (usually null, unless this is the request
    * that invoked the authorization sequence)
    */
   public SavedRequestWrapper(
      HttpServletRequest request,
      SavedRequest savedRequest
   ) {
      super(request);
      this.currentRequest = request;
      this.savedRequest = savedRequest;
      initMatchableURL();
   }

	/**
	 * Get the original HttpServletRequest object.
	 */
	public HttpServletRequest getCurrentRequest() {
		return currentRequest;
	}

   /**
    * Get a parameter value by name. If multiple values are available, the first value is returned.
    *
    * @param s parameter name
    */
   public String getParameter(String s) {
      if (savedRequest == null) {
         return currentRequest.getParameter(s);
      } else {
         String value = currentRequest.getParameter(s);
         if (value == null) {
            String[] valueArray = (String[]) savedRequest.getParameterMap().get(s);
            if (valueArray != null) {
               value = valueArray[0];
            }
         }
         return value;
      }
   }

   /**
    * Get a map of parameter values for this request.
    */
   public Map getParameterMap() {
      if (savedRequest == null) {
         return currentRequest.getParameterMap();
      } else {
         Map map = new HashMap(savedRequest.getParameterMap());
//         map.putAll(currentRequest.getParameterMap());
         return Collections.unmodifiableMap(map);
      }
   }

   /**
    * Get an enumeration of paramaeter names for this request.
    */
   public Enumeration getParameterNames() {
      if (savedRequest == null) {
         return currentRequest.getParameterNames();
      } else {
         return Collections.enumeration(getParameterMap().keySet());
      }
   }

   /**
    * Get an array of values for a parameter.
    *
    * @param s parameter name
    */
   public String[] getParameterValues(String s) {
      if (savedRequest == null) {
         return currentRequest.getParameterValues(s);
      } else {
         String[] values = currentRequest.getParameterValues(s);
         if (values == null) {
            values = (String[]) savedRequest.getParameterMap().get(s);
         }
         return values;
      }
   }

   /**
    * Set the request that is to be wrapped.
    *
    * @param request wrap this request
    */
   public void setRequest(ServletRequest request) {
      super.setRequest(request);
      this.currentRequest = (HttpServletRequest) request;
   }

   /**
    * This method is provided to restore functionality of this method in case the wrapper class we are extending
    * has disabled it. This method is needed to process multi-part requests downstream, and it appears that some
    * wrapper implementations just return null. WebLogic 6.1.2.0 is one such implementation.
    *
    * @exception IOException
    */
   public ServletInputStream getInputStream() throws IOException {
      ServletInputStream stream = super.getInputStream();
      if (stream == null) {
         stream = currentRequest.getInputStream();
      }
      return stream;
   }

   /**
    * Returns the HTTP method used to make this request. If the savedRequest is non-null,
    * the HTTP method of the saved request will be returned.
    */
   public String getMethod() {
      if (savedRequest != null) {
         return savedRequest.getMethod();
      } else {
         return super.getMethod();
      }
   }

   /**
    * Get a URL that can be matched against security URL patterns.
    *
    * This is the part after the contextPath, with the pathInfo, but without the query string.
    * http://server:8080/contextPath/someURL.jsp?param=value becomes /someURL.jsp
    */
   public String getMatchableURL() {
      return matchableURL;
   }

   /**
    * Initilize the matchableURL.
    */
   private void initMatchableURL() {
      // extract the servlet path portion that needs to be checked
      matchableURL = currentRequest.getServletPath();
      // add the pathInfo, as it needs to be part of the URL we check
      String pathInfo = currentRequest.getPathInfo();
      if (pathInfo != null) {
         matchableURL = matchableURL + pathInfo;
      }
   }
}
