package com.temenos.t24browser.servlets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.temenos.t24browser.utils.Utils;
import com.temenos.t24browser.servlets.*;
public class BrowserLoginServlet extends HttpServlet implements Serializable
{
	/**
                  * Handles the login request and response.
                  * Stores the login request in session with password encrypted to avoid re-form submission by browser when user clicks refresh.
                  * Sends dummy request back to client and overwrites the browser request history which actually automatically sends request to server.
                  * Retrieve the session stored request details and send to T24 server for login process, then remove the session attribute.
                  */
	private HttpServletRequest Request = null;
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException
	{
		HttpSession session = request.getSession();
		doPost(request, response);
		}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException
	{
		ServletContext cc = getServletContext();
		HttpSession session = request.getSession();
		// If the request type is SESSION.CHECK then its a automated request submission.
		String requestType = Utils.getValue( request, "blankRequestType");
		if (requestType!=null && requestType.equals("SESSION.CHECK"))
		{
			// Retrieve the session stored previous actual request.
			@SuppressWarnings("unchecked")
			HashMap<String, String> hash = (HashMap<String, String>)session.getAttribute("loginRequest");
			RequestDispatcher dispatch = cc.getNamedDispatcher("BrowserServlet");
			if(hash!=null)
			{
				if(hash.containsKey("password"))
				{
					hash.put("password",Utils.decryptPassword(request));
					}
				// Modify the current request with stored actual request so that proper login request can be sent to T24 server
				HttpServletWrappedRequest wrappedRequest = new HttpServletWrappedRequest(request, hash );
				// Remove the attribute from session.
				session.removeAttribute("loginRequest");
				// Forward the control further to BrowserServlet.
				dispatch.forward(wrappedRequest, response);
				}
			else
			{
				// If session doesn't have stored request then invalidate it.
				session.invalidate();
				// Redirect to login screen by BrowserServlet.
				response.sendRedirect(request.getContextPath()+"/servlet/BrowserServlet?");
				}
			}
		else
		{
			// Stores the request parameters in a map as key/value pairs.
			HashMap<String, String> hashMap = new HashMap<String, String>();
			Enumeration Enum = request.getParameterNames();
			String attributeName;
			while(Enum.hasMoreElements())
			{
				attributeName = (String) Enum.nextElement();
				if (attributeName.equalsIgnoreCase("password"))
				{
					hashMap.put(attributeName, Utils.encryptPassword(Utils.getValue( request, attributeName),request));
					}
				else
				{
					hashMap.put(attributeName, Utils.getValue( request, attributeName));
					}
				}
			// Attach this map in current session.
			session.setAttribute("loginRequest", hashMap);
			// SendRedirect to blankPage.jsp which will re-submit automatically.
			response.sendRedirect(request.getContextPath()+"/jsps/blankPage.jsp");
			}
		}
	}