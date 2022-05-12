
package com.temenos.arc.security.filter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This class acts as a holder for the status so we can output it in a getStatus() method.
 */

public class StatusHolder extends HttpServletResponseWrapper {
	
    private int status;

    public StatusHolder(ServletResponse response) {
        super((HttpServletResponse) response);
        status=-1;            
    }
    
    public void setStatus (int sc) {
    	super.setStatus(sc);
    	status = sc;
    }
    
    public int getStatus() {
    	return status;
    }
}
