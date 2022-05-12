package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.temenos.t24browser.xslt.XMLToHtmlBean;

import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.String;

/*
 * This class is reponsible for reading the help files in xml format and convert it to HTML and display it to the user
  */
public class PortalHelpServlet extends HttpServlet {
	
	private static final String Stylesheet = "/transforms/help/helpmenu.xsl";	//  XSL
	private String sXml = "";
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    
        // TODO Auto-generated constructor stub
   

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String FileID= request.getParameter("FileId");
		getXmlFromFile(FileID);
		ServletContext context = this.getServletContext();
		try
		{
		XMLToHtmlBean xmlTranformer = new XMLToHtmlBean(context.getRealPath(""));
		String htmlResult = xmlTranformer.transformXml(Stylesheet,sXml);
		PrintWriter writer = response.getWriter();
		writer.println(htmlResult);
		}
		catch(Exception e)
		{
			System.out.println("Error While transforming XML contents - " + e.getMessage());
		}
	
			
	}

	/**
	 * @FileName contains the actual xml filename
	 * @sXml - xml contents in the file
	 * read the xml file
	 */
	
	public String getXmlFromFile(String FileName)
	{
		try
		{
			
			ServletContext context = this.getServletContext();
			String contextPath = context.getRealPath(""); 
			String ActualPath = contextPath.concat(FileName.substring(FileName.indexOf("/"), FileName.length()));
			StringBuffer buf = new StringBuffer();
			BufferedReader i=new BufferedReader( new InputStreamReader( new FileInputStream(ActualPath))); 
			String s = i.readLine();
			
			while( s != null )
			{
				buf.append(s); // read line by line 
				s = i.readLine();
			}
			
		sXml = buf.toString().trim();
		int index = sXml.indexOf("<");
		int xmllen = sXml.length();
		if (index > 0)
		{	
		    sXml = sXml.substring(index,xmllen);
		}
		}
		catch (Exception e)
		{
			System.out.println("Error reading XML from file - " + e.getMessage());
		}

		return(sXml);
	}

}
