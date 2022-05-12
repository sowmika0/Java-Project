
package com.temenos.arc.security.filter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

// TODO: Auto-generated Javadoc
/**
 * This class acts as a holder for the status so we can output it in a getStatus() method.
 */

public class StatusHolder extends HttpServletResponseWrapper {
	
    /** The status. */
    private int status;

    /**
     * Instantiates a new status holder.
     * 
     * @param response the response
     */
    public StatusHolder(ServletResponse response) {
        super((HttpServletResponse) response);
        status=-1;            
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int)
     */
    public void setStatus (int sc) {
    	super.setStatus(sc);
    	status = sc;
    }
    
    /**
     * Gets the status.
     * 
     * @return the status
     */
    public int getStatus() {
    	return status;
    }
}
