package com.temenos.arc.security.cookieclient.servlet;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * Servlet implementation class for Servlet: ClientServlet
 *
 */
 public class CookieClientServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	public static final String SUBMIT_PAGE = "/jsps/submit.jsp";
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(CookieClientServlet.class);
	 /** Constructor for the ClientServlet
	  */
	public CookieClientServlet() {
		super();
		logger.info("Constructing the ClientServlet");
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	
	
	/** Creates the XML message (AuthenticationTicket), creates digest for the same
	 *  Encrypts the XML message (1) and the digest(2) using a AES symmetric key 
	 *  Encrypts the symmetric key(3) used, using a RSA public key
	 *  Encodes 1,2,3 in base64 and puts in the session and submits to the submit page
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Retriving the t24userid form request object
		String strMessage = request.getParameter("userName");
		
		if(strMessage!=null && !strMessage.equals(""))
		{
			logger.info("User Name not null");
		}else
		{
			logger.error("User Name is null");
		}
		        
		Cookie myCookie = new Cookie("SMSESSION", strMessage);
		myCookie.setMaxAge(3600);
		myCookie.setPath("/");
		myCookie.setDomain("sara.com");
	    response.addCookie(myCookie);
		
		request.getRequestDispatcher(SUBMIT_PAGE).forward(request, response);
	} 
}