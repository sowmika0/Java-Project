////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   DisplayServerImage
//
//  Description   :   Servlet for Processing Image Request .Its called on when 
//       			  image have to retrieve from filepath(Any data base table)	             
//
//  Modifications :
//
//    14/08/13   -    Initial Version.
//    
//	 	
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.servlets;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import com.temenos.tocf.tbrowser.TBrowserRequestSender;
import com.temenos.tocf.tcc.TCCFactory;
import com.temenos.tocf.tcc.TCClientException;
import com.temenos.tocf.tcc.TCConnection;
import com.temenos.tocf.tcc.TCInputStream;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class DisplayServerImage extends HttpServlet {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayServerImage.class);
	private static final long serialVersionUID = 1L;
	public String docDownloadPath=null;
	public String downloadLocation=null;
	// Upload browser parameter constants channels, instance and download location
	public static final String DB_UP_DOWN_LOAD_CHANNEL_PARAM_NAME = "DBUpDownLoadServiceChannel";
	public static final String DB_UP_DOWN_LOAD_INSTANCE = "Instance";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayServerImage() {
        super();
        // TODO Auto-generated constructor stub        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Initialize all variable to avoid thread sharing of class instances.
		// TODO Auto-generated method stub
		Enumeration<String> parameterNames = request.getParameterNames();
		String paramName="";
		// Extract the download path and location
		while (parameterNames.hasMoreElements()) {
			paramName = parameterNames.nextElement();
			if(paramName.endsWith("docDownloadPath"))
			{
				docDownloadPath = request.getParameter(paramName);
			}else if(paramName.endsWith("downloadLocation"))
			{
				downloadLocation = request.getParameter(paramName);
			}
		}
		try{
			// Download from T24 server Data base
			if(downloadLocation!=null && downloadLocation.equalsIgnoreCase("server"))
			{
				doT24Download(request, response,docDownloadPath);	
			} 
		} catch(Exception e)
		{
			
		}
	}
	
	/**
	 * Function to download files from Any data base table.
	 * @param request actual HTTP request object
	 * @param response actual HTTP response object
	 * @param downloadPath path of the file to be downloaded
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void doT24Download(HttpServletRequest request, HttpServletResponse response, String downloadPath) throws ServletException, IOException
	{
		TCConnection tc = null;
		OutputStream os = null;
		TBrowserRequestSender ivConnection = null;
		String fileName = "";
		String subDirectory = "";
		String sContentType = "";
		// Extract file name
		fileName = downloadPath.substring(downloadPath.lastIndexOf('/') +1,downloadPath.length());
		// Extract sub directory
		subDirectory = downloadPath.substring(1,downloadPath.lastIndexOf('/'));
		
		int modRealNameLen = downloadPath.lastIndexOf(".");
	     // get the downlaod file extension 
	    if (modRealNameLen > 0){
	    	 sContentType =  downloadPath.substring(modRealNameLen+1 ,downloadPath.length() );
	    	  
	    }
	    // Set content type and header 
	    response.setContentType(sContentType);
	    response.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );
		try
		{
			
			HttpSession session = request.getSession(false);
			ServletContext servletContext = session.getServletContext();
			String channelName = (String)servletContext.getAttribute(DB_UP_DOWN_LOAD_CHANNEL_PARAM_NAME);
			String ivDownloadInstance = (String)servletContext.getAttribute(DB_UP_DOWN_LOAD_INSTANCE);
			LOGGER.debug("Trying to connect to the database");
			
			// Connect the T24 server with upload channel
			ivConnection = new TBrowserRequestSender(ivDownloadInstance);
			tc = ivConnection.getConnection(channelName);
			// Get the inpustream of that file content
			TCInputStream tcis = tc.getInputStream();
						
			// Extract current user to send part of the document ID to server.
 			String userId = (String) session.getAttribute( "BrowserSignOnName" );
 			LOGGER.debug("Document download's userId to TCServer=" + userId);
 			//extract filepath 
			tcis.setFileName(fileName);
						
			if (subDirectory != "")
			{
				tcis.setFilePath(subDirectory);
			}						 		 
	    byte[] bytes = tcis.read();
		LOGGER.debug("Requested download file read completed");
		response.setContentLength(bytes.length);
 		os = response.getOutputStream();
 		os.write(bytes);
 		os.close();   
		tc.close();
		ivConnection.close();
									
		}catch ( TCClientException e){
			// TODO Auto-generated catch block
			LOGGER.error("File download Exception:"+e.getMessage());
		}
		catch (Exception tccex)
		{
			// TODO Auto-generated catch block
			LOGGER.error("File download Exception:"+tccex.getMessage());
		}
		
	}
}
