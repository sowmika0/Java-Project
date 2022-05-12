package com.temenos.arc.security.filter;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

//TODO Do we need this anymore??  DB to check ... 
public class PostMethodOnlyRequest extends HttpServletRequestWrapper implements Serializable {

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
