package com.temenos.arc.security.filter;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

// TODO: Auto-generated Javadoc
//TODO Do we need this anymore??  DB to check ... 
/**
 * The Class PostMethodOnlyRequest.
 */
public class PostMethodOnlyRequest extends HttpServletRequestWrapper implements Serializable {

    /**
     * Instantiates a new post method only request.
     * 
     * @param request the request
     */
    public PostMethodOnlyRequest(HttpServletRequest request) {
        super(request);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getMethod()
     */
    public String getMethod() {
        return "POST"; // fools the BrowserServlet code
    }
}
