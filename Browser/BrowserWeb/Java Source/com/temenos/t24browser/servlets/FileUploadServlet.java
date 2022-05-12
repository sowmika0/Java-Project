/**
 * Class: FileUploadServlet<br>
 * Description: For uploading files to the T24 back end server file system.<br>
 *              This servlet is using the com.oreilly.servlet API written by<br>
 *              Jason Hunter.  With his kind permissions the API had been<br> 
 *              amended to work with T24 Browser.<br>
 * Modifications:<br>
 * <ul>
 * <li>13/10/09: Initial Version, based on UploadServlet.java</li>
 * </ul>
 */
package com.temenos.t24browser.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.comms.ConnectionBean;
import com.temenos.t24browser.comms.ConnectionEngine;
import com.temenos.t24browser.comms.T24WebConnection;
import com.temenos.t24browser.exceptions.SecurityViolationException;
import com.temenos.t24browser.ofs.OfsBean;
import com.temenos.t24browser.request.RequestData;
import com.temenos.t24browser.request.SessionData;
import com.temenos.t24browser.security.XssChecker;
import com.temenos.t24browser.utils.BrowserParameters;
import com.temenos.t24browser.utils.PropertyManager;
import com.temenos.t24browser.utils.Utils;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tocf.tcc.TCCFactory;
import com.temenos.tocf.tcc.TCConnection;
import com.temenos.tocf.tcc.TCException;
import com.temenos.tocf.tcc.TCOutputStream;
import com.temenos.tsdk.foundation.T24Connection;
import com.temenos.tsdk.foundation.UtilityRequest;

/**
 * The Class UploadServlet.
 */
public class FileUploadServlet extends HttpServlet {
	
	/* Public constants. */
	/** 'browser.1' which channel to use for file uploads to T24. */
	public static final String FILE_UPLOAD_CHANNEL_PARAM_NAME = "fileUploadChannel";
    public static final String XSS_PATTERN_PARAM = "valid_input_regex";
	
	/* Private static final constants */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServlet.class);
	private static final String docType    = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
	private static final String htmlOpen   = "<html>";
	private static final String htmlHead   = "<head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head>";
	private static final String htmlClose  = "</html>";
	
	/* Object variables */
	private String originalFilename = "";
	private String systemFilename   = "";
	private String uploadType       = "";
	private String uploadDir        = "";
	private String fileExtension    = "";
	private String application      = "";
	private String transactionId    = "";
	private String companyId        = "";
	private String skin             = "";
	private long   maxFileSize      = 0L;
	private long   actualFileSize   = 0L;
    private Pattern validInputRegex = null;
	
	public void init() throws ServletException {
		
    	// Get the pattern to use to check the input
		ServletContext context = getServletContext();
    	String xssPattern     = context.getInitParameter(XSS_PATTERN_PARAM);
        validInputRegex = Pattern.compile(xssPattern);
        if (validInputRegex == null) {
            throw new ServletException(XSS_PATTERN_PARAM + " must be specified in web.xml for FormFieldInputFilter");
        }
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        XssChecker xssChecker = new XssChecker();
		PrintWriter out = response.getWriter();
		try {
			
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request); // A list of the request parameters
			Iterator<FileItem> iter = items.iterator(); // Go through each item (field on the form or file upload)
			while (iter.hasNext()) {
				FileItem item = iter.next();
				String paramName = item.getFieldName();
				if (item.isFormField()) {
					String paramValue = item.getString();
					// Check for xss attacks in the fieldname and value.
					xssChecker.checkAndThrow(validInputRegex, paramName, paramValue);
					if ( paramName.equalsIgnoreCase("uploadType") ) {
						uploadType = getUploadType(paramValue, request);
						getUploadTypeInfoFromT24(uploadType, request);
					} else if (paramName.equalsIgnoreCase("application")) {
						application = paramValue;
					} else if (paramName.equalsIgnoreCase("transactionId")) {
						transactionId = paramValue;
					} else if (paramName.equalsIgnoreCase("companyId")) {
						companyId = paramValue;
					} else if (paramName.equalsIgnoreCase("skin")) {
						skin = paramValue;
					}
					//allow parameter for form token
					else  if (paramName.equalsIgnoreCase("formToken")) {
						continue;
					}else { // Only the checked for fields are expected. Any others are probably hacking attempts!
						throw new SecurityViolationException("Unexpected parameter in request: " + paramName + "=" + paramValue);
					}
				} else {
					// Check for xss attacks in the filename and fieldname.
					xssChecker.checkAndThrow(validInputRegex, paramName, item.getName());

					// it's a file part, check the size is not too big
					actualFileSize = item.getSize();
					if (maxFileSize > 0 && actualFileSize > maxFileSize) {
						throw new IllegalArgumentException("EB-FILE.UPLOAD.FILE.TOO.LARGE|" + maxFileSize + "| For UPLOAD.TYPE=" + uploadType + ", upload size is " + actualFileSize);
					}
					
					// Get the original filename
					originalFilename = getOriginalFilename(item);
					
					// If the UPLOAD.TYPE specifies an extension, then only allow files with that extension to be uploaded.
					if (!originalFilename.toUpperCase().endsWith(fileExtension.toUpperCase())) {
						throw new IllegalArgumentException("EB-FILE.UPLOAD.INVALID.EXTENSION|" + fileExtension + "| For UPLOAD.TYPE=" + uploadType + ", submitted file is " + originalFilename);
					}

					// Generate a system name for the file
					systemFilename = getSystemName(request);
						
					// Upload the file
					int uploadSize = uploadFile(systemFilename, item.getInputStream(), request);
					if (uploadSize != actualFileSize) {
						if (uploadSize == 0) {
							throw new IOException("EB-FILE.UPLOAD.SYSTEM.ERROR|| Unable to upload file (might be permissions).");
						} else {
							throw new IOException("EB-FILE.UPLOAD.TRANSMISSION.ERROR|| uploadSize=" + uploadSize + ", actualFileSize=" + actualFileSize);
						}
					}
				}
			}
			//Inform the user the upload was a success
			String responseHtml = getSuccessHtml();
			out.print(responseHtml);
			out.flush();
		} catch (IllegalArgumentException e) {
			handleError(request, response, e, false);
		} catch (TCException e) {
			handleError(request, response, new Exception("EB-FILE.UPLOAD.SYSTEM.ERROR|| File upload: Could not communicate with TCServer. Check running and config"), false);
		} catch (IOException e) {
			handleError(request, response, e, false);
		} catch (NoClassDefFoundError e) {
			// Probably means browserParameters is set to a JCA connection but they aren't using an application server (like jboss).
			handleError(request, response, new Exception("EB-FILE.UPLOAD.SYSTEM.ERROR|| File upload: 'Server Connection Method' = 'Instance' is not supported by this web container. Check browserParameters.xml",e), true);
		} catch (SecurityViolationException e) {
			LOGGER.error("Security Violation: " + e.getMessage());
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			response.sendError(HttpServletResponse.SC_FORBIDDEN , "");
		} catch (Exception e) { // Unknown exception, log stack trace
			handleError(request, response, new Exception("EB-FILE.UPLOAD.SYSTEM.ERROR|| File upload: Unexpected error:",e), true);
		}
	}
	


	/**
	 * Logs an error and directs the response the jsp to display the upload iframe again. 
	 * @param response The response
	 * @param e The exception to log
	 * @param logStackTrace Unknown / unexpected exceptions should log the stack trace. Others should not.
	 * @throws IOException
	 */
	private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e, boolean logStackTrace) throws IOException {
		
		if (logStackTrace) {
			LOGGER.error("Error during file upload: " + e.getMessage(), e); // Unknown exception, log stack trace
		} else {
			LOGGER.error("Error during file upload: " + e.getMessage());
		}
		String[] errInfo = e.getMessage().split("\\|");
		String ebErrorCode = errInfo[0];
		String errParams = (errInfo.length > 1)?errInfo[1]:"";
		String trans_upload = "Upload";
		String trans_uploading = "Uploading";
		String routineArgs = getTxtXml(ebErrorCode,errParams);
		routineArgs += getTxtXml("Upload","");
		routineArgs += getTxtXml("Uploading","");
		routineArgs = wrapXml(routineArgs,"txtList");
		String enrichment = ebErrorCode;
		
		try {
			String responseXml = sendUtilityRequest("OS.GET.TXT", routineArgs, request);
			ArrayList<String> txtList = Utils.getAllMatchingNodes(responseXml, "txt");
			String ebErrorText = txtList.get(0);
			if (ebErrorText != null && ebErrorText.length() > 0) {
				enrichment = jspEncode(ebErrorText);
			}
			trans_upload = jspEncode(txtList.get(1));
			trans_uploading = jspEncode(txtList.get(2));
		} catch (Exception ex) {
			LOGGER.error("Error translating text",e);
		}
		String jspUrl = "../jsps/fileUpload.jsp?skin="+skin;
		jspUrl += "&enrichment=" + enrichment;
		jspUrl += "&trans_upload=" + trans_upload;
		jspUrl += "&trans_uploading=" + trans_uploading;
        response.sendRedirect(jspUrl);
	}
	
	private String wrapXml(String textToWrap, String xmlNode) {
		return "<" + xmlNode + ">" + textToWrap + "</" + xmlNode + ">";
	}
	
	private String getTxtXml(String id, String param) {
		String txtXml = wrapXml(id,"id");
		if (param.length() > 0) {
			txtXml += wrapXml(param,"param");
		}
		txtXml = wrapXml(txtXml,"txt");
		return txtXml;
	}
	
	private String jspEncode(String in) throws UnsupportedEncodingException {
		String out = in.replaceAll("&apos;", "\\\\&#39;");
		out = URLEncoder.encode(out, "utf-8");
		return out;
	}
	
	/**
	 * Return html to display to the user.
	 * This contains a display value, which is just the original filename
	 * and a return value, which is the original filename plus the new system generated filename.
	 * @return The html to display to the user.
	 */
	private String getSuccessHtml() {
		String html = docType;
		html += htmlOpen;
		html += htmlHead;
		html += "<body onload=\"window.parent.showFileuploadSuccess('" + originalFilename + "','";
		html += systemFilename + "||" + actualFileSize + "||" + originalFilename + "');\">";
		html += "</body>";
		html += htmlClose;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("File upload: successful. html=" + html);
		}
		return html;
	}
	
	/**
	 * Get the filename part of the input filename, which might contain path information and be submitted from unix or windows.
	 * @param fileItem The http filepart
	 * @return The filename without path information. Returns the part after the last \ and /.
	 * @throws IllegalArgumentException If no file was specified before upload.
	 */
	private String getOriginalFilename(FileItem fileItem) {
		String pathname = fileItem.getName();
		if (pathname == null || pathname.length() < 1) {
			throw new IllegalArgumentException("EB-FILE.UPLOAD.NO.FILE.SELECTED||");
		}
		String filename = pathname;
		if (filename.indexOf("\\") > -1) {
			filename = filename.substring(filename.lastIndexOf("\\") + 1);
		}
		if (filename.indexOf("/") > -1) {
			filename = filename.substring(filename.lastIndexOf("/") + 1);
		}
		return filename;
	}

	
	
	/**
	 * Generates a new filename for the file: SignOnName.Timestamp(.optionalExtension).
	 * @return the new filename. Also set in the systemFilename variable.
	 * @throws SecurityViolationException
	 */
	private String getSystemName(HttpServletRequest request) throws SecurityViolationException {
		HttpSession session = request.getSession(false);
		String signOnName = (String) session.getAttribute(SessionData.SESSION_SIGN_ON_NAME);
		if (signOnName == null || signOnName.length() < 1) {
			throw new SecurityViolationException("Sign on name not found in session.");
		}
		String timestamp  = Long.toString(System.currentTimeMillis());
		systemFilename = signOnName + "." + timestamp;
		if (fileExtension.length() > 0) {
			systemFilename += "." + fileExtension;
		}
		LOGGER.debug("File upload: generated systemFilename=" + systemFilename);
		return systemFilename;
	}
	
	
	/**
	 * Gets the uploadType from the session or the request.
	 * @param uploadType May be populated with the value from the request.
	 * @param request Used to search the session for any hidden (AUT.NEW.CONTENT) field values.
	 * @return The upload type from the session, otherwise returns the given value.
	 * @throws IllegalArgumentException If the upload type is not found in the session or the request.
	 */
	private String getUploadType(String uploadType, HttpServletRequest request) {

		// If the uploadType is in the session, then use that value
		HttpSession session = request.getSession(false);
		String user = (String) session.getAttribute(SessionData.SESSION_USER_ID);
		String requestDataId = companyId + "_" + user + "_" + application + "_I_" + transactionId;
		RequestData sessionRequestData = (RequestData)session.getAttribute( requestDataId);
		if (sessionRequestData != null) {
			String uploadTypeTemp = sessionRequestData.getValue("fieldName:UPLOAD.TYPE");
			if (uploadTypeTemp != null && !uploadTypeTemp.equals("")) {
				LOGGER.debug("File upload: UPLOAD.TYPE=" + uploadTypeTemp + " found in session, using this value.");
				uploadType = uploadTypeTemp;
			}
		} else if (uploadType.equals("")) {
			throw new IllegalArgumentException("EB-FILE.UPLOAD.NO.TYPE.SELECTED|| UPLOAD.TYPE not in request or session.");
		}
		return uploadType;
	}


	/**
	 * Runs a UTILITY routine to get information about the file upload type from T24.
	 * @param uploadType The EB.FILE.UPLOAD.TYPE id.
	 * @param request Used for getting the token
	 * @throws TBrowserException If there is an error creating a connection instance
	 */
	private void getUploadTypeInfoFromT24(String uploadType, HttpServletRequest request) throws TBrowserException {
		
		// Send a utility request to T24 to get the upload type info
		String responseXml = sendUtilityRequest("OS.GET.UPLOAD.TYPE.INFO", uploadType, request);

		// Check if any type info was returned
		String uploadTypeInfo = Utils.getNodeFromString(responseXml, "uploadTypeInfo");
		if (LOGGER.isDebugEnabled()) {LOGGER.debug("File upload: uploadTypeInfo=" + uploadTypeInfo);}
		if (!uploadTypeInfo.contains("<maxFileSize>")) {
			throw new IllegalArgumentException("EB-FILE.UPLOAD.TYPE.NOT.FOUND|" + uploadType + "|");
		}
		
		// Get the file size 
		String fileSize = Utils.getNodeFromString(responseXml, "maxFileSize");
		if (fileSize.length() > 0) {
			maxFileSize = Long.parseLong(fileSize);
		} else {
			maxFileSize = 0;
		}

		// Get the upload directory, if present
		uploadDir = Utils.getNodeFromString(responseXml, "uploadDirectory");
		if (uploadDir.equals("")) {
			LOGGER.warn("Warning: uploadDir is empty!");
		}

		// Get the file extension.
		// If the file extension does not begin with a '.' then add one
		fileExtension = Utils.getNodeFromString(responseXml, "fileExtension");
		if (fileExtension.length() > 0 && fileExtension.startsWith(".")) {
			fileExtension = fileExtension.substring(1); // Drop the leading period.
		}
	}
	

	/**
	 * Sends a utility request to T24 and returns the result.
	 * @param utilityName The OS. utility routine name
	 * @param routineArgs The arguments to pass to the routine.
	 * @param request Used for getting the token
	 * @return The xml response from T24
	 * @throws TBrowserException If there is an error creating a connection instance
	 */
	private String sendUtilityRequest(String utilityName, String routineArgs, HttpServletRequest request) throws TBrowserException {
		String responseXml = "";
		long startTime = System.currentTimeMillis();
		HttpSession session = request.getSession(false);
		ServletContext servletContext = session.getServletContext();
		BrowserParameters params = new BrowserParameters( servletContext );
	 	PropertyManager ivParameters = params.getParameters();
		String connMethod = ( (String) ivParameters.getProperty( OfsBean.PARAM_SERVER_CONN_METHOD )).toUpperCase();
		String instanceName = (String) ivParameters.getProperty( OfsBean.PARAM_INSTANCE_NAME );
		ConnectionEngine connEngine = new ConnectionEngine( connMethod, instanceName, request, ivParameters, null ); 
		ConnectionBean connectionBean = connEngine.initialiseConnection();
		String clientIp = Utils.getClientIpAddress(request);
		T24Connection connection = new T24WebConnection(connectionBean,clientIp);
		String token = (String) session.getAttribute(SessionData.TOKEN_SESSION_NAME);
		connection.setRequestToken(token);
		UtilityRequest ur = new UtilityRequest(utilityName, routineArgs, connection);
		responseXml = ur.sendRequest();
		if (LOGGER.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - startTime;
			LOGGER.debug("File upload: Utility request processed. Took " + duration + "mS. Request=" + utilityName + "('" + routineArgs + "'), response=" + responseXml);
		}
		return responseXml;
	}


	
    /**
     * Sends a request to TC with file upload data
     * @param fileName - the filename the uploaded data should be stored in
     * @param fileData - the upload data
     * @param request - the request object, used to get the servletContext.
     * @throws TCException If there was an exception during file upload returned by TC.
     * @throws Exception If there is an error getting a TC Connection Factory instance. 
     */
    private int uploadFile(String fileName, InputStream fileData, HttpServletRequest request) throws TCException, Exception {
    	long startTime = System.currentTimeMillis();
		HttpSession session = request.getSession(false);
		ServletContext servletContext = session.getServletContext();
		String channelName = (String)servletContext.getAttribute(FILE_UPLOAD_CHANNEL_PARAM_NAME);
		TCConnection tcConnection = null;
		int uploadSize = 0;
    	try {
    		TCCFactory tcf = TCCFactory.getInstance();
    		if (tcf.getChannelNames().nextElement() == null) {
    			throw new Exception("EB-FILE.UPLOAD.SYSTEM.ERROR|| File upload: Cannot create TC Connection Factory:");
    		}
    		tcConnection = tcf.createTCConnection(channelName);
    		TCOutputStream tcos = tcConnection.getOutputStream();
    		tcos.setFileName(fileName);
    		tcos.setFilePath(uploadDir);
    		uploadSize = tcos.send(fileData);
    		if (LOGGER.isDebugEnabled()) {
    			long duration = System.currentTimeMillis() - startTime;
    	    	LOGGER.debug("File upload: uploaded " + originalFilename + " to " + uploadDir + "/" + fileName + ". uploadSize=" + uploadSize + ", took " + duration + "mS.");
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

}
