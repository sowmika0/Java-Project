package com.temenos.t24browser.servlets;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class DirectURLRestrictionServlet extends HttpServlet
    implements Servlet
{

    private static Logger logger = LoggerFactory.getLogger(DirectURLRestrictionServlet.class);;
    public static final String RESTRICT_ERROR_PAGE = "/banking/up/403.jsp";

    public DirectURLRestrictionServlet()
    {
    	super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        logger.info("DirectURLRestrictionServlet doGet called");
        request.getRequestDispatcher(RESTRICT_ERROR_PAGE).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        logger.info("DirectURLRestrictionServlet doPost called");
        logger.info("Context Path: "+request.getContextPath());
        
        if(request.getContextPath() == null){
        	 request.getRequestDispatcher(RESTRICT_ERROR_PAGE).forward(request, response);
        }
        	
    }

}
