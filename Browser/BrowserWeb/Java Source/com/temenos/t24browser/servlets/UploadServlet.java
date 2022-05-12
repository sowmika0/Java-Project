////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   UploadServlet
//
//  Description   :   For uploading files to the web server.
//					  This servlet is using the com.oreilly.servlet API written by
//					  Jason Hunter.  With his kind permissions the API had been
//					  amended to work with Globus Browser.
//
//  Modifications :
//
//    09/05/03   -    Initial Version
//	  05/08/03	 -    To accommodate Unix server
//	  27/06/14	 -	  Upload file Size condition added.
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.PropertyManager;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.temenos.tocf.tbrowser.TBrowserRequestSender;
import com.temenos.tocf.tcc.TCCFactory;
import com.temenos.tocf.tcc.TCClientException;
import com.temenos.tocf.tcc.TCConnection;
import com.temenos.tocf.tcc.TCException;
import com.temenos.tocf.tcc.TCInputStream;
import com.temenos.tocf.tcc.TCOutputStream;
import java.util.HashMap;

import com.temenos.t24browser.request.SessionData;



// TODO: Auto-generated Javadoc
/**
 * The Class UploadServlet.
 */
public class UploadServlet extends HttpServlet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadServlet.class);
	// Upload browser parameter constants channels, instance and download location
	public static final String DB_UP_DOWN_LOAD_CHANNEL_PARAM_NAME = "DBUpDownLoadServiceChannel";
	public static final String DB_UP_DOWN_LOAD_INSTANCE = "Instance";
	protected PropertyManager ivParameters = null;
	BrowserParameters params = null;

	/** Variables Used for cloud specify usage */
	public static final String AZURE_ACCOUNT_NAME = "azureAccountName";
	public static final String AZURE_ACCOUNT_KEY = "azureAccountKey";
	public static final String ENABLE_CLOUD = "enableCloud";
	public static final String AZURE_IMAGE_CONTAINER = "imageContainer";

	public static final List<String> unSuppFormats = Arrays.asList( ".EXE", ".PIF", ".APPLICATION", ".GADGET",
			".MSI", ".MSP", ".COM", ".SCR", ".HTA", ".CPL", ".MSC", ".JAR",
			".BAT", ".CMD", ".VB", ".VBS", ".VBE", ".JS", ".JSE", ".WS",
			".WSF", ".WSC", ".WSH", ".PS1", ".PS1XML", ".PS2", ".PS2XML",
			".PSC1", ".PSC2", ".MSH", ".MSH1", ".MSH2", ".MSHXML", ".MSH1XML",
			".MSH2XML", ".SCF", ".LNK", ".INF", ".REG", ".SH", ".DLL", ".CHM",
			".ADE", ".ADP", ".APP", ".BAS", ".CRT", ".CSH", ".FXP", ".HLP",
			".INS", ".ISP", ".KSH", ".MDA", ".MDB", ".MDE", ".MDT", ".MDW",
			".MDZ", ".OPS", ".PCD", ".PIF", ".PRF", ".PRG", ".PST", ".SCT",
			".SHB", ".SHS", ".URL", ".WAR", ".BIN", ".CRX", ".MUI", ".THM",
			".PROFILE" );

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		// Initialize all variable to avoid thread sharing of class instances.
		DocumentRequestContext context = new DocumentRequestContext();
		context.request = request;
		context.response = response;
		//Reading cloud account informations from browserParameters.xml
		params = new BrowserParameters( this.getServletContext() );
		ivParameters = params.getParameters();
		String cloudEnabled = ivParameters.getParameterValue(ENABLE_CLOUD);

		try
		{
			// Actually when to iterat file items in the multi-data form the parser removed the content of the request.
			// So need to have find the file location then iterat.
			context.factory = new DiskFileItemFactory();
			context.upload = new ServletFileUpload(context.factory);
			context.items = context.upload.parseRequest(context.request); // A list of the request parameters
			context.iter = context.items.iterator(); // Go through each item (field on the form or file upload)
		}catch(Exception e){
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage());
			}
		}

		context.out = response.getWriter();

		// Extract the upload destination by get of index in FileItem.
		// Can't iterator further if once done, so index of the upload location has been hardcoded based on the index it has in the upload form.
		context.name = (FileItem) context.items.get(8);
		String  uploadLocation = context.name.getString();
		//Upload to cloud storage if cloud is enabled in browserParameters.xml
		if(cloudEnabled!=null && cloudEnabled.equalsIgnoreCase("YES")) {
			uploadToCloud(context,response);
		} else {
			if (uploadLocation!=null && uploadLocation.equalsIgnoreCase("server")){
				doServerUpload(context);
			}else{
				uploadToLocal(context);
			}
		}
	}

	public void uploadToCloud(DocumentRequestContext context,HttpServletResponse response)
	{
		   HttpSession session = context.request.getSession();
		   String Uploadenable = "YES";
		try{
			while (context.iter.hasNext()) {
				FileItem item = context.iter.next();
				String paramName = item.getFieldName();
				if (item.isFormField()) {
					String paramValue = item.getString();
					if ( paramName.equalsIgnoreCase("filepath") ) {
						// Set the filepath - this is where the file must be saved to.
						context.filePath = paramValue;
						context.displayPath = "";
						// Check the filepath value
						if (session!=null)
						{
						String username = (String) session.getAttribute(SessionData.SESSION_USER_ID);
						String filepathMapId = username + "_filepathMap";
						HashMap filepathMap = (HashMap) session.getAttribute(filepathMapId);
						  if (filepathMap!=null)
						     {
							context.originalpath = (String) filepathMap.get("filepath");
							context.imageid = (String) filepathMap.get("imageid");
							context.imgMaxFileSize = (String) filepathMap.get("imgMaxFileSize");
						     }
						   else
						     {
							LOGGER.debug("Requested filepath ID couldn't find.");
						     }
						  if((!context.filePath.equals(null))&&(!context.filePath.equals(context.originalpath)))
						  {
							  LOGGER.error("Security Violation: " +paramName +  "=" + context.filePath);
							  session = context.request.getSession(false);
							  if (session != null)
							  {
								Uploadenable = "NO";
								session.invalidate();
							  }
							  context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
						  }
					   }
					} else if (paramName.equalsIgnoreCase("imageid")) {
						context.imageName = paramValue;
						// Check the imageid value
					      if ((!context.imageName.equals(null))&&(!context.imageName.equals(context.imageid)))
						   {
						     LOGGER.error("Security Violation: " +paramName +  "=" + context.imageName);
						     session = context.request.getSession(false);
						     if (session != null)
							   {
						      Uploadenable = "NO";
							  session.invalidate();
						       }
						     context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
						   }
					} else if (paramName.equalsIgnoreCase("skin")) {
						context.skin = paramValue;
					} else if (paramName.equalsIgnoreCase("uploadOkMsg")) {
						context.successMessage = paramValue;
					} else if (paramName.equalsIgnoreCase("user")) {
						context.user = paramValue;
					}
				}else{
					// it's a file part
					String modFileName = getOriginalFilenameExt(item);
					String storeFileName = context.imageName;
					if(modFileName!=null && modFileName!="")
					{
						storeFileName = storeFileName + modFileName;
					}

					if(validateFile(item, context) && Uploadenable != "NO"){
						try {
							//Reading cloud account informations from browserParameters.xml
							String azureAcountName =  ivParameters.getParameterValue(AZURE_ACCOUNT_NAME);
							String azureAccountKey = ivParameters.getParameterValue(AZURE_ACCOUNT_KEY);
							String azureContainerId = ivParameters.getParameterValue(AZURE_IMAGE_CONTAINER);
							final String storageConnectionString = "DefaultEndpointsProtocol=http;"	+ "AccountName=" + azureAcountName +";" + "AccountKey=" +azureAccountKey;
							int drivePos;
							//Formatting the id of the file to be stored in cloud
							context.filePath.replace("\\", "/");
							while(context.filePath.length()!=0)	{
								if(context.filePath.indexOf(":")!=-1) {
									drivePos = context.filePath.indexOf(":");
									context.filePath = context.filePath.substring(drivePos+1);
								}else if(context.filePath.substring(0, 3).equalsIgnoreCase("../")){
									context.filePath = context.filePath.substring(3);

								}else if(context.filePath.substring(0, 2).equalsIgnoreCase("./")) {
									context.filePath = context.filePath.substring(2);
								}else if(context.filePath.substring(0, 1).equalsIgnoreCase("/")) {
									context.filePath = context.filePath.substring(1);
								} else  {
									break;
								}
							}
							String imageRef = "";
							imageRef = context.filePath + storeFileName;
							// Getting the reference of the cloud container
							CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
							CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
							CloudBlobContainer container = blobClient.getContainerReference(azureContainerId);
							//New contained will be created at first time only
							container.createIfNotExist();
							CloudBlockBlob blob = container.getBlockBlobReference(imageRef);
							// Writing the file content to cloud storage
							blob.upload(item.getInputStream(),-1);
							context.imageName = "";
							context.displayPath = imageRef;
						}catch(Exception e)	{
							writeCloudErrors(response,context);
							LOGGER.error(" Either Cloud Account details or the Container name is incorrect or ");
							LOGGER.error(" Or Your proxy may block the request for cloud data. Please check your proxy settings");
							LOGGER.error(" Error occured while uploading to cloud : " + e.getMessage());
							return;
						}
						// sucessMessage text should come from the server (OS.ATTACHMENT)
						// but if this browser version is being used on an old
						// server we should default a message. i.e. it will not be
						// translatable until the server is upgraded...
						if ((context.successMessage.equals(null)) || (context.successMessage.equals("")))
						{
							context.successMessage = "Item has been successfully uploaded";
						}
						// Same for the skin
						if ((context.skin.equals(null)) || (context.skin.equals("")))
						{
							context.skin = "default";
						}

						//Inform the user the upload was a success
						String src = context.displayPath;
						successDisplay(src, context.successMessage, context);
					}
				}
			}

		} catch (Exception ex)
		{
			exceptionDisplay(ex, context);
		}
	}

	/**
	 * This function is used to upload files in local system. where the web server is currently running.
	 * @param context  holds the class instance of UploadServlet
	 */
		public void uploadToLocal(DocumentRequestContext context)
		{
		   HttpSession session = context.request.getSession();
		   String Uploadenable = "YES";
		try{
			while (context.iter.hasNext()) {
				FileItem item = context.iter.next();
				String paramName = item.getFieldName();
				if (item.isFormField()) {
					String paramValue = item.getString();
					if ( paramName.equalsIgnoreCase("filepath") ) {
					// Check the filepath value
						if (session!=null)
						{
						String username = (String) session.getAttribute(SessionData.SESSION_USER_ID);
						String filepathMapId = username + "_filepathMap";
						HashMap filepathMap = (HashMap) session.getAttribute(filepathMapId);
						  if (filepathMap!=null)
						     {
							context.originalpath = (String) filepathMap.get("filepath");
							context.imageid = (String) filepathMap.get("imageid");
							context.imgMaxFileSize = (String) filepathMap.get("imgMaxFileSize");
						     }
						   else
						     {
							LOGGER.debug("Requested filepath ID couldn't find.");
						     }
						}
						// Set the filepath - this is where the file must be saved to.
						context.filePath = paramValue;
						context.displayPath = context.filePath;
						if ((!context.filePath.equals(null))&&(!context.filePath.equals(context.originalpath)))
					    {
					      LOGGER.error("Security Violation: " +paramName +  "=" + context.filePath);
						  session = context.request.getSession(false);
						  if (session != null)
						  {
						    Uploadenable = "NO";
							session.invalidate();
						  }
						  context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
					    }
						// Set the display path - this is what the browser needs to display the image correctly
						// The display path should always contain '/' regardless of the platform as the html
						// specification requires '/' as a separator. Firefox can be particular about this.
						//context.displayPath = context.filePath.replace('\\','/');

						// If the filePath is relative, then add the servlet context path in front.
						// Otherwise indicate that we are using the 'file://' protocol to access the file.
						// If the path is relative then displayPath will begin with "./" or "../"
						if (context.displayPath.substring(0,1).equals(".")) {
							context.filePath = getServletContext().getRealPath("") + "/" + context.filePath;
							context.displayPath = "../" + context.displayPath;
						}
					} else if (paramName.equalsIgnoreCase("imageid")) {
						context.imageName = paramValue;
						// Check the imageid value
					      if ((!context.imageName.equals(null))&&(!context.imageName.equals(context.imageid)))
						   {
						     LOGGER.error("Security Violation: " +paramName +  "=" + context.imageName);
						     session = context.request.getSession(false);
						     if (session != null)
							   {
						      Uploadenable = "NO";
							  session.invalidate();
						       }
						     context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
						   }
					} else if (paramName.equalsIgnoreCase("skin")) {
						context.skin = paramValue;
					} else if (paramName.equalsIgnoreCase("uploadOkMsg")) {
						context.successMessage = paramValue;
					} else if (paramName.equalsIgnoreCase("routineArgs")) {
						context.routineArgs = paramValue;
					} else if (paramName.equalsIgnoreCase("fileId")) {
						context.fileId = paramValue;
					} else if (paramName.equalsIgnoreCase("requestType")) {
						context.requestType = paramValue;
						if( context.requestType.equals( "UTILITY.ROUTINE"))
						{
							context.isUtilityRtn = true;
						}
					} else if (paramName.equalsIgnoreCase("user")) {
						context.user = paramValue;
					}else if (paramName.equalsIgnoreCase("routineName")) {
						context.routineName = paramValue;
					}
				}else{
					// it's a file part
					String modFileName = getOriginalFilenameExt(item);
					String storeFileName = null;

					if(validateFile(item, context) && Uploadenable != "NO"){
						// If this request is a utility rtn then we are uploading a file
						// to be sent to a utility routine for procesing.
						if( context.isUtilityRtn )
						{
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							//item.write(item.getOutputStream());
							// Browser Servlet needs this.
							context.request.setAttribute( "command", "globuscommand");
							// Set the user
							context.request.setAttribute( "user", context.user);
							// Set the request type
							context.request.setAttribute( "requestType", context.requestType);
							// Which routine we want to run at T24
							context.request.setAttribute( "routineName", context.routineName);
							// Set the file to routine args
							if( context.fileId != null)
							{
								context.routineArgs = "<xmlRequest><fileId>" + context.fileId + "</fileId><file>" + baos.toString() + "</file></xmlRequest>";
							}
							else
							{
								context.routineArgs = "<xmlRequest><file>" + baos.toString() + "</file></xmlRequest>";
							}
							context.request.setAttribute( "routineArgs", context.routineArgs);

							// Dispatch the request to BrowserServlet
							RequestDispatcher dispatch = getServletContext().getNamedDispatcher("BrowserServlet");
							dispatch.forward(context.request, context.response);
							//System.out.println( "\n--------------------------------------------------");
							//System.out.println( baos.toString());
							//System.out.println( "\n--------------------------------------------------");
						}
						else
						{
							//File have the extension then add along with the file name,it it's not then use only the file name
							if (modFileName == null || modFileName == ""){
								storeFileName = context.imageName ;
							}else{
								storeFileName = context.imageName + modFileName;
							}
							if (storeFileName!=null)
							{
								item.setFormField(true);
								item.setFieldName(storeFileName);
							}
							context.dirs = new File( context.filePath ); // create file under mentioned upload path in local system

							if (!context.dirs.exists()) {
								context.dirs.mkdirs();
								LOGGER.info("Upload servlet: Creating directory: " + context.dirs.getCanonicalPath());
							}
							LOGGER.debug("Upload servlet: Writing " + context.imageName + " to: " + context.dirs.getCanonicalPath());
							// the part actually contained a file
							FileOutputStream fos = new FileOutputStream(context.dirs+"//"+storeFileName);
							context.dirs.setWritable(true);
							fos.write(item.get());	// Write file item in the file
							fos.close();
						}

						// sucessMessage text should come from the server (OS.ATTACHMENT)
						// but if this browser version is being used on an old
						// server we should default a message. i.e. it will not be
						// translatable until the server is upgraded...
						if ((context.successMessage.equals(null)) || (context.successMessage.equals("")))
						{
							context.successMessage = "Item has been successfully uploaded";
						}
						// Same for the skin
						if ((context.skin.equals(null)) || (context.skin.equals("")))
						{
							context.skin = "default";
						}

						//Inform the user the upload was a success
						String src = context.displayPath + item.getFieldName();
						successDisplay(src, context.successMessage, context);
					}
				}
			}

		} catch (IOException lEx) {
			exceptionDisplay(lEx, context);
		}
		catch (Exception ex)
		{
			exceptionDisplay(ex, context);
		}
	}

	/**
	 * Function to display exception/failure message while the upload happens.
	 * @param e  Exception which caught
	 * @param context  class instance reference of UploadServlet
	 */
	public void exceptionDisplay(Exception e, DocumentRequestContext context)
	{
		String failedImgPath = "../plaf/images/" + context.skin + "/failed.gif";
		context.out.print("<html><head><title>Upload Failed</title></head><body bgcolor ='#eaedf4'>");
		context.out.print("<img src='" + failedImgPath + "' />");
		context.out.print("<p style='color: red;font-weight: bold;font-size: 13px;'>");
		context.out.print(e.getMessage());
		context.out.print("</p>");
		context.out.print("</body></html>");
		context.out.flush();
		e.printStackTrace();
		LOGGER.error("Upload servlet: " + e.getMessage());
	}

	/**
	 * Function to display success message to the end user.
	 * @param src file which was uploaded successfully
	 * @param successMessage message to display
	 * @param context class instance reference of UploadServlet
	 */
	public void successDisplay(String src, String successMessage, DocumentRequestContext context)
	{
		//Read the browser parameters to get the product
		String browserProduct = ivParameters.getParameterValue("Product");
		String successImgPath = "../plaf/images/" + context.skin + "/success.gif";
		context.out.print("<html><head><title>Uploaded Sucessfully</title></head><body bgcolor ='#eaedf4'>");
		context.out.print("<p style='color: black;font-weight: bold;font-size: 13px;'>");
		context.out.print(successMessage);
		context.out.print("</p>");
		if(!browserProduct.equalsIgnoreCase("arc-ib")) {
			context.out.print("<img src='" + successImgPath + "'" + " alt='" + successMessage +"' />");
			context.out.print(src);
		}
		context.out.print("</body></html>");
		context.out.flush();
	}

	/**
	 * Function to extract request details to upload files to server.
	 * @param context class instance reference of UploadServlet
	 */

	public void doServerUpload(DocumentRequestContext context)
	{
		long actualFileSize = 0L;
		HttpSession session = context.request.getSession();
		try {
				while (context.iter.hasNext()) {
					FileItem item = context.iter.next();
					String paramName = item.getFieldName();
					if (item.isFormField()) {
						String paramValue = item.getString();
						if ( paramName.equalsIgnoreCase("filepath") ) {
							context.filePath = paramValue;
							// Check the filepath value
						if (session!=null)
						   {
						     String username = (String) session.getAttribute(SessionData.SESSION_USER_ID);
						     String filepathMapId = username + "_filepathMap";
						     HashMap filepathMap = (HashMap) session.getAttribute(filepathMapId);
						     if (filepathMap!=null)
						     {
							    context.originalpath = (String) filepathMap.get("filepath");
							    context.imageid = (String) filepathMap.get("imageid");
							    context.imgMaxFileSize = (String) filepathMap.get("imgMaxFileSize");
						      }
						     else
						      {
							    LOGGER.debug("Requested filepath ID couldn't find.");
						      }
						     if((!context.filePath.equals(null))&&(!context.filePath.equals(context.originalpath)))
								{
								  LOGGER.error("Security Violation: " +paramName +  "=" + context.filePath);
								  session = context.request.getSession(false);
								  if (session != null)
								    {
									  session.invalidate();
								    }
								  context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
							    }
						   }
						} else if (paramName.equalsIgnoreCase("imageid")) {
							context.imageName = paramValue;
							// Check the imageid value
					      if ((!context.imageName.equals(null))&&(!context.imageName.equals(context.imageid)))
						   {
						     LOGGER.error("Security Violation: " +paramName +  "=" + context.imageName);
						     session = context.request.getSession(false);
						     if (session != null)
							  {
						    	session.invalidate();
						      }
						     context.response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
						  }
						} else if (paramName.equalsIgnoreCase("skin")) {
							context.skin = paramValue;
						} else if (paramName.equalsIgnoreCase("uploadOkMsg")) {
							context.successMessage = paramValue;
						} else if (paramName.equalsIgnoreCase("routineArgs")) {
							context.routineArgs = paramValue;
						} else if (paramName.equalsIgnoreCase("fileId")) {
							context.fileId = paramValue;
						} else if (paramName.equalsIgnoreCase("requestType")) {
							context.requestType = paramValue;
							if( context.requestType.equals( "UTILITY.ROUTINE"))
							{
								context.isUtilityRtn = true;
							}
						} else if (paramName.equalsIgnoreCase("user")) {
							context.user = paramValue;
						}else if (paramName.equalsIgnoreCase("routineName")) {
							context.routineName = paramValue;
						}
					}else{
						// it's a file part, check the size is not too big
						actualFileSize = item.getSize();
						// it's file name
						String fileNameExt = getOriginalFilenameExt(item);

						if(validateFile(item, context)){
							if(context.filePath!=null)
							{
								context.filePath = context.filePath.replaceAll("\\\\","");
								context.filePath = context.filePath.replaceAll("/", "");
							}
							// Upload the file
							int uploadSize = uploadToT24Server(context.imageName + fileNameExt, item.getInputStream(), context.request, context.filePath);
							if (uploadSize != actualFileSize) {
								if (uploadSize == 0) {
									throw new IOException("Unable to upload file (might be permissions).");
								} else {
									throw new IOException("uploadSize=" + uploadSize + ", actualFileSize=" + actualFileSize);
								}
							}

							//Inform the user the upload was a sucess
							String src = context.filePath + "/" + context.imageName + fileNameExt;
							successDisplay(src, context.successMessage, context);
						}
					}
				}
			}catch(Exception e){
				exceptionDisplay(e, context);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(e.getMessage());
				}
			}
	}

	/**
	 * Function to return file name of the attachment.
	 * @param fileItem
	 * @return
	 */
	private String getOriginalFilenameExt(FileItem fileItem) {
		String fileExtension = "";
		String fileName = fileItem.getName();
		int fileNameLen = fileName.length();
		// get the exact file extension  from the upload file
		int modFileLength = fileName.lastIndexOf(".");
		if (modFileLength > 0)
		{
			fileExtension = fileName.substring(modFileLength,fileNameLen);
		 }
		return fileExtension;
	}

	private Boolean validateFile(FileItem fileItem, DocumentRequestContext context) {
		Boolean fileFlag = true;
		String flExtn = getOriginalFilenameExt(fileItem);
		String errMsg = "File is not uploaded. ";
		long fileSize = fileItem.getSize();

		if(fileItem.getName().equals("")){
			fileFlag = false;
			errMsg += "Invalid file. File name is empty.";
		}else if (flExtn.equals("") || unSuppFormats.contains(flExtn.toUpperCase()))
		{
			fileFlag = false;
			errMsg += "Unsupported file type.";
		}else if(fileSize <= 0){
			fileFlag = false;
			errMsg += "Invalid file. File size is 0kb.";
		}else if((context.imgMaxFileSize != null) && !context.imgMaxFileSize.equals("")){
			long maxFileSize = Long.parseLong(context.imgMaxFileSize);
			if(fileSize > maxFileSize){
				fileFlag = false;
				errMsg += "File size is greater than the maximum size allowed.";
			}
		}

		if(!fileFlag){
			LOGGER.error(errMsg);
			context.out.print("<html><head><title>Upload Failed</title></head><body bgcolor='#eaedf4' style='text-align: center;'>");
			context.out.print("<p style='color: red;font-weight: bold;font-size: 13px;'>");
			context.out.print(errMsg);
			context.out.print("</p>");
			context.out.print("</body></html>");
			context.out.flush();
		}

		return fileFlag;
	}

	/**
	 * Function to initialise the communication with the server and upload the files by the given path.
	 * @param fileName name of the file which needs to uploaded
	 * @param fileData contents of the file
	 * @param request actual request object
	 * @param upLoadPath path of the T24 server where it needs to be saved
	 * @return
	 * @throws TCException
	 * @throws Exception
	 */
	private int uploadToT24Server(String fileName, InputStream fileData, HttpServletRequest request, String upLoadPath) throws TCException, Exception {
	   	long startTime = System.currentTimeMillis();
	   	// Get the channel details from session to initialise connection with T24 server
		HttpSession session = request.getSession(false);
		ServletContext servletContext = session.getServletContext();
		String channelName = (String)servletContext.getAttribute(DB_UP_DOWN_LOAD_CHANNEL_PARAM_NAME);

		TCConnection tcConnection = null;
		int uploadSize = 0;
		try {
    		TCCFactory tcf = TCCFactory.getInstance();
    		if (tcf.getChannelNames().nextElement() == null) {
    			LOGGER.debug("File upload: Cannot create TC Connection Factory:");
    			throw new Exception("File upload: Cannot create TC Connection Factory:");
    		}
    		tcConnection = tcf.createTCConnection(channelName);
    		TCOutputStream tcos = tcConnection.getOutputStream();
    		// Set file name
    		tcos.setFileName(fileName);
    		// Set upload path
    		tcos.setFilePath(upLoadPath);
    		// Send file content to server
    		uploadSize = tcos.send(fileData);

    		if (LOGGER.isDebugEnabled()) {
    			long duration = System.currentTimeMillis() - startTime;
    	    	LOGGER.debug("File upload: uploaded " + "originalFilename" + " to " + upLoadPath + "/" + fileName + ". uploadSize=" + uploadSize + ", took " + duration + "mS.");
    		}
    	} catch (TCException e) {
    		throw e;
    	} finally {
    		if (tcConnection != null) {
    			tcConnection.close();
    		}
    	}
		return uploadSize;
	}

	/**
	 * Function to download files from T24 server.
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

 			//tcis.setUserName(userId);
			// Set file name
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
			LOGGER.error("File download Exception:"+e.getMessage());
			PrintWriter out = response.getWriter();
			out.print("Not able to download the requested file.");
			out.flush();
		}
		catch (Exception tccex)
		{
			LOGGER.error(tccex.getMessage());
		}
	}

	/**
	 * Class to initialise UploadServlet class instances to avoid thread sharing of instances.
	 * @author tskumar
	 *
	 */
	class DocumentRequestContext
	{
			String imgMaxFileSize=null;
			File dirs;
			String filePath = null;
			String displayPath = null;
			String imageName = null;
			String successMessage = null;
			String skin = null;
			boolean isUtilityRtn = false;
			String requestType = null;
			String routineName = null;
			String routineArgs = null;
			String windowName = null;
			String fileId = null;
			String user = null;
			HttpServletRequest request = null;
			HttpServletResponse response = null;
			PrintWriter out = null;

			FileItemFactory factory = null;
			ServletFileUpload upload = null;
			List<FileItem> items = null;
			Iterator<FileItem> iter = null;
			FileItem  name = null;
			String originalpath=null;
		    String imageid=null;
	}

	public void writeCloudErrors(HttpServletResponse response,DocumentRequestContext context)
	{
		try {
			ResourceBundle reslabel = ResourceBundle.getBundle("cloudErrors");
			String errorMessage = reslabel.getString("upload.error");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.print("<html><head><title>Upload Problem</title></head><body bgcolor =\"#eaedf4\">");
			out.print("<basefont color=\"black\" face=\"arial\" size=\"2\">");
			out.print("<img src=\"../plaf/images/" + context.skin + "/failed.gif\" />  &nbsp;");
			if(errorMessage!=null)
				out.print(errorMessage);
			out.print("</body></html>");
			out.flush();
		} catch (IOException e) {
			LOGGER.error("Upload servlet: " + e.getMessage());
		}
	}
}
