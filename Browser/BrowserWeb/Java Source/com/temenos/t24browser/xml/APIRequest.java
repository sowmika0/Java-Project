package com.temenos.t24browser.xml;

/**
 *                      APIRequest.java
 * 
 *Extends XMLTemplate.  Processes a request destined for the api, passed in via an HTTP request.
 *
 *
 * 
 * Modifications:
 * 
 * 08/07/03 - Added a new parameter to the constructor call of the Super class; now passes in
 * 				in the xml template as a string
 */

import com.temenos.t24browser.request.T24Request;

// TODO: Auto-generated Javadoc
/**
 * The Class APIRequest.
 */
public class APIRequest extends XMLTemplate{
	
	
	    /**
    	 * pass the request and the name of the xml template to be used to the super class.
    	 * 
    	 * @param request the request
    	 * @param templates the templates
    	 */
        public APIRequest(T24Request request, XMLTemplateManager templates){
              super(request,(String)templates.getProperty("apiRequest.xml"));
              setDestination("API");
        }


}

