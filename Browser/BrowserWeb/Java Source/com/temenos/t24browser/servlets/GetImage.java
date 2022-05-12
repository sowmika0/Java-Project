package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.BrowserParameters;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

/**
 * Servlet to process the request for documents in cloud. Will return the requested document in the server response. 
 */
public class GetImage extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadServlet.class);
	private PropertyManager ivParameters = null;
	public static final String AZURE_ACCOUNT_NAME = "azureAccountName";
	public static final String AZURE_ACCOUNT_KEY = "azureAccountKey";
	public static final String AZURE_IMAGE_CONTAINER = "imageContainer";
	public Boolean isOpera = false;
	

	
	private static final String SESSION_LOGGEDIN = "LoggedIn";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetImage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
			doPost(request,response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sLoggedIn = (String) session.getAttribute(SESSION_LOGGEDIN);
		// Display error message for the request submitted after browser session expires  
		if (!((sLoggedIn != null) && (!sLoggedIn.equals("")))) {
			response.setStatus(403);
			displayerrorMessage(response,geterorMessage("unauthorized.Access"));
			return;
		}
		String imageRef = request.getParameter("imageId");
		// check the download path.
		if ((imageRef!= null)&&(!imageRef.equals("")))
		{
			if (session!=null)
			{
                String winName = request.getParameter("windowName");
			    String DownloadpathMapId = winName + "_DownloadpathMap";
				HashMap DownloadpathMap = (HashMap) session.getAttribute(DownloadpathMapId);
				if (DownloadpathMap!=null)
			     {
					List<String> arrayvalue = new ArrayList<String>();
					arrayvalue = (List<String>) DownloadpathMap.get("downloadpath");
					if (arrayvalue.contains(imageRef))
					{
						LOGGER.debug(" The Download path does not change");
					}
					else
					{
						LOGGER.error("Security Violation: " + imageRef );
						  session = request.getSession(false);
						  if (session != null) 
						  {
							session.invalidate();
							response.setStatus(403);
							displayerrorMessage(response,geterorMessage("unauthorized.Access"));
							return;
						  }
					}
			      }
				 else
			      {
				  LOGGER.debug("Requested download path could not find");
			      }
		     }
		}
		String isPopUp = request.getParameter("isPopUp");
		if(request.getParameter("isOpera")!=null && request.getParameter("isOpera").equalsIgnoreCase("YES"))
		{
			isOpera = true;
		}
		
		if (imageRef != null) {		
			int drivePos;
			imageRef.replace("\\", "/");
			while (imageRef.length() != 0) {
				if(imageRef.indexOf(":")!=-1) {
					drivePos = imageRef.indexOf(":");
					imageRef = imageRef.substring(drivePos+1);
				}else if (imageRef.substring(0, 3).equalsIgnoreCase("../")) {
					imageRef = imageRef.substring(3);
				} else if (imageRef.substring(0, 2).equalsIgnoreCase("./")) {
					imageRef = imageRef.substring(2);
				} else if (imageRef.substring(0, 1).equalsIgnoreCase("/")) {
					imageRef = imageRef.substring(1);
				} else {
					break;
				}
			}
			String downloadFileName = imageRef.substring(imageRef.lastIndexOf("/"));
			CloudStorageAccount storageAccount;
			try {
				//Reading cloud account informations from browserParameters.xml
				BrowserParameters params = new BrowserParameters(getServletConfig().getServletContext());
				ivParameters = params.getParameters();
				String azureAcountName = ivParameters.getParameterValue(AZURE_ACCOUNT_NAME);
				String azureAccountKey = ivParameters.getParameterValue(AZURE_ACCOUNT_KEY);
				String azureContainerId = ivParameters.getParameterValue(AZURE_IMAGE_CONTAINER);

				final String storageConnectionString = "DefaultEndpointsProtocol=http;"	+ "AccountName="+ azureAcountName+ ";"+ "AccountKey=" + azureAccountKey;
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
				CloudBlobContainer container = blobClient.getContainerReference(azureContainerId);
				// Check and log the existance of the container configured in browserParameters.xml
				if(container.exists()) {
					CloudBlockBlob blob = container.getBlockBlobReference(imageRef);
					if (blob.exists()) {
						if (isPopUp != null && isPopUp != ""&& isPopUp.equalsIgnoreCase("YES")) {
							// Set the Content-Disposition files other than image
							response.setHeader("Content-Disposition","attachment; filename=" + downloadFileName	+ ";");
							response.setContentType("application/stream;charset=utf-8");
						}
						OutputStream outStream = response.getOutputStream();
						blob.download(outStream);
					} else {
						if (isPopUp != null && isPopUp != ""&& isPopUp.equalsIgnoreCase("YES")) {
							displayerrorMessage(response,geterorMessage("blob.unAvailable"));
						}
						LOGGER.error(" Cloud Account details are correct. But, the requested file is not available");
						LOGGER.error("Or Your proxy may block the request for cloud data. Please check your proxy settings");
					}
				} else {
					if (isPopUp != null && isPopUp != ""&& isPopUp.equalsIgnoreCase("YES")) {
						displayerrorMessage(response,geterorMessage("blob.unAvailable"));
					}
					LOGGER.error(" Either Cloud Account details or the Container name is incorrect ");
					LOGGER.error(" Or Your proxy may block the request for cloud data. Please check your proxy settings");
				}
			} catch (Exception e) {
				if (isPopUp != null && isPopUp != ""&& isPopUp.equalsIgnoreCase("YES")) {
					displayerrorMessage(response,geterorMessage("on.Exception"));
				}
				LOGGER.error(" Either Cloud Account details or the Container name is not correct. Error Occoured : ");
				LOGGER.error(" Or Your proxy may block the request for cloud data. Please check your proxy settings"+e.getMessage());
			}
		}
	}
	
	public String geterorMessage(String errorType) {
		// Method to read the customized errors in cloudErrors.properties file
		ResourceBundle reslabel = ResourceBundle.getBundle("cloudErrors");
		String errorMessage = reslabel.getString(errorType);
		return errorMessage;
	}
	
	public void displayerrorMessage(HttpServletResponse response,String errorOccured)
	{
		// Method to display an alert message with corresponding status messages 
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<HTML>");
			out.println("<HEAD>");
		    out.println("<TITLE>" + "Error in Downloads" + "</TITLE>");
		    out.println("<script type=\"text/javascript\">");  
		    if(errorOccured!=null)
		    	out.println("alert('"+errorOccured+"');");  
		    if(isOpera) {
		    	out.println("window.close();");
		    }
		    out.println("</script>");
		    out.println("</HEAD>");
		    out.println("<HTML>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
