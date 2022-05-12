////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   RenderImageServlet
//
//  Description   :   Servlet for Processing Image Request .Its called on when 
//       			  image have to retrieve from filepath(local drive)	             
//
//  Modifications :
//
//    18/02/13   -    Initial Version.
//    
//	 	
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.servlets;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import com.temenos.t24browser.utils.Utils;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class RenderImageServlet extends HttpServlet {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RenderImageServlet.class);
	
	 // called on when Image request from server 
	 /* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException
	{
		LOGGER.debug("Entering RenderImageServlet");
		// Set the Content Type to display image
		response.setContentType("image/jpeg");
		String isLoggedon= "";
		// check whether user logged on
		if(request.getSession().getAttribute("LoggedIn") != null)
		{
			isLoggedon =(String)request.getSession().getAttribute("LoggedIn");
			//if user not logged into Browser then dont display image
			if (isLoggedon.equals("") || isLoggedon.equals("false"))
			{
				return;
			}
		}
		else
		{
			return;
		}
		//extract filepath from the request
		String filePath = Utils.getValue( request, "filePath");	
		LOGGER.debug(" Image File Path "+filePath);
	try
		{
		ServletOutputStream out = response.getOutputStream();	
		 //Retrieve filepath starting with ../	 
		if (filePath.contains("../"))
		{
			String sUrlPath = getServletContext().getRealPath("");
			filePath = sUrlPath + "/" + filePath;
			filePath = filePath.replaceAll("\\\\","/");
			filePath = filePath.replaceAll("/./","/");
		}
		// read the file using filepath and write it to output	
		FileInputStream fin = new FileInputStream(filePath);
		BufferedInputStream bis = new BufferedInputStream(fin);	
		BufferedOutputStream bout = new BufferedOutputStream(out);
		int ch =0; ;
		while((ch=bis.read())!=-1)
			{
			bout.write(ch);
			}
		// Close the Streams
		bis.close();
		fin.close();
		bout.close();
		out.close();
		}
	catch(Exception e)
		{
		LOGGER.error("Unable to read the image"+e.getMessage());
		LOGGER.debug("Unable to read the image"+e.getMessage());
		System.out.println(e.getMessage());
		}
	}
	}