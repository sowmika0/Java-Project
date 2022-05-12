package com.temenos.t24browser.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.tocf.tbrowser.TBrowserException;
import com.temenos.tocf.tbrowser.TBrowserRequestSender;
import com.temenos.tocf.tcc.TCCFactory;
import com.temenos.tocf.tcc.TCClientException;
import com.temenos.tocf.tcc.TCConnection;
import com.temenos.tocf.tcc.TCException;
import com.temenos.tocf.tcc.TCInputStream;

public class DocumentService extends HttpServlet implements Serializable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
	private String ivDocumentID = "";
	private String ivDownloadChannel = "";
	private String ivDownloadInstance = "";
	private String ivDrillDownInfo = "";
	private String ivMultiDownload = "";
	private String ivSubDirectory = "";
	private String ivMultiDocumentIDs = "";
	private String ivMultiSubDirectories = "";
	private TBrowserRequestSender ivConnection = null;
	private HttpServletResponse ivResponse;
	private HttpServletRequest ivRequest;
	private boolean ivDEBUG = false;
	// Delimiter characters used for parsing multi-download items
	private static final String FIELD_DELIMITER = "\\|";

	// Called for each Browser command request
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws javax.servlet.ServletException, java.io.IOException
	{
		LOGGER.debug( "***** Document Service : Start Request ***********************************************" );
		long timeInMillis = System.currentTimeMillis(); // Start our timer asap		

		// Ensure the request parameters are treated as UTF-8 characters.
		// This should be done by the EncodingFilter, but just in case it hasn't been used...
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			LOGGER.error("Unable to set request character encoding to UTF8");
		}


		ivRequest = request;
		ivResponse = response;
		ivDownloadChannel = (String) request.getAttribute("downloadChannel");
		ivDownloadInstance = (String) request.getAttribute("downloadInstance");
		ivDEBUG = false;
		
		// Extract the Drill Down Info
		ivDrillDownInfo = (String) request.getAttribute("drillDownInfo");
		
		// Check if it's a Multi-Download
		ivMultiDownload = (String) request.getAttribute("multidownload");
		if (ivMultiDownload == "true")
		{
			// We will have a list of full drill down info.
			// so we need 'clean' them.
			String[] sDrillParts = ivDrillDownInfo.split( FIELD_DELIMITER );
			for ( int i = 0; i < sDrillParts.length; i++ )
			{
				// Extract the ID
				String sDDFileName = (String) sDrillParts[i];
				
				if (sDDFileName != "")
				{
					parseDrillInfo(sDDFileName);
					
					if (ivSubDirectory == "")
					{
						ivSubDirectory = "n-a";
					}	
					
					if (ivDocumentID != "")
					{
						if (i == 0)
						{
							ivMultiDocumentIDs = "[[_MULTI_DOWNLOAD_]]" + "|" + ivDocumentID + "|";
							ivMultiSubDirectories = "[[_MULTI_DOWNLOAD_]]" + "|" + ivSubDirectory + "|";
						}
						else
						{
							ivMultiDocumentIDs += ivDocumentID + "|";
							ivMultiSubDirectories += ivSubDirectory + "|";
						}
					}
				}
			}
			ivDocumentID = ivMultiDocumentIDs;
			ivSubDirectory = ivMultiSubDirectories;
		}
		else
		{
			parseDrillInfo(ivDrillDownInfo);
		}
		
		// Check if we have received the required Parameters
		if ((ivDownloadChannel == null) ||  (ivDocumentID == null) || (ivDownloadChannel.equals("")) || (ivDocumentID.equals("")))
		{
			setError("No Channel or Download item");
			return;
		}
		
		if ((ivDownloadInstance == null) || (ivDownloadInstance.equals("")))
		{
			setError("Use Instance Connection");
			return;			
		}
				
		// We have get this far so all good
		// So Create an Instance of the TCS
		
		// Invoke the Download
		downloadDocument();
		
		//downloadDocumentTester();
		
		// Forward back to the Browser Servet
		//RequestDispatcher dispatch = getServletContext().getNamedDispatcher("BrowserServlet");
		//dispatch.include(request, response);		
	}

	private void setError(String sError)
	{
		if (sError==null)
		{
			sError = "Un-Known Error!";
		}
		if (ivDEBUG)
		{
			String sDetails = "  \n[Drill Down Info:" + ivDrillDownInfo + "]";
			sDetails = sDetails + "  \n[Document ID: " + ivDocumentID + "]";
			sDetails = sDetails + "  \n[Sub-Directory: " + ivSubDirectory + "]";
			sDetails = sDetails + "  \n[Instance: " + ivDownloadInstance + "]";
			sDetails = sDetails + "  \n[Channel: " + ivDownloadChannel + "]";
			sError = sError + sDetails;
		}
		ivRequest.setAttribute("errorMessage", sError);
	}
	
	private void parseDrillInfo(String sFullDrillDownInfo)
	{
		// This function looks at the drill down info received from T24
		// and removes the Key Word 'DOC' from it, it also extracts the 
		// Document ID and the sub directory if specified
		
		ivSubDirectory = "";
		ivDocumentID = "";
		ivDEBUG = false;
		
		// Split the drill down info
		// In the scenario that the doc ID or path param contain spaces:
		// Check if the drill info has a ':' delimiter e.g.
		// <itemDownload>DOC my document.pdf : Document Sub Folder DEBUG</itemDownload>
		if (sFullDrillDownInfo.contains(":"))
		{
			// Resolve the 1st Part
			int iPos = sFullDrillDownInfo.indexOf(":");
			String sPart1 = sFullDrillDownInfo.substring(0, iPos); // e.g. DOC my document.pdf
			
			// Resolve the 2nd Part
			iPos = iPos + 2;
			String sPart2 = sFullDrillDownInfo.substring(iPos); // e.g. 'Document Sub Folder DEBUG' or 'Document Sub Folder' or 'DEBUG'

			// Trim both
			sPart2 = sPart2.trim();
			sPart1 = sPart1.trim();
			
			// To resolve the document ID simply ignore the 'DOC ' directive
			ivDocumentID = sPart1.substring(4);
		
			// Parse the 2nd Part
			if (sPart2.equals("DEBUG"))
			{
				ivDEBUG = true;
			}
			else
			{
				int iEndPos = sPart2.length();
				// Check if the DEBUG directive has been specified
				// as the last parameter
				String[] sDebugPart = sPart2.split(" ");
				int ilen = 0;
				if (sDebugPart.length > 0)
				{
					ilen = sDebugPart.length - 1;
				}
				if (sDebugPart[ilen].trim().equals("DEBUG"))
				{
					ivDEBUG = true;
					iEndPos = (iEndPos - " DEBUG".length());
				}

				ivSubDirectory = sPart2.substring(0, iEndPos);
			}
			
		}
		else
		{
			String[] sCommandParts = sFullDrillDownInfo.split( " " );
			for ( int i = 0; i < sCommandParts.length; i++ )
			{
				// Ignore element 0 as this should contain 'DOC'
				
				// The 1st element should be the Document ID
				if (i == 1)
				{
					ivDocumentID = sCommandParts[i];
				}
				
				// The 2nd element should be the Sub-Directory (if specified)
				if (i == 2)
				{
					ivSubDirectory = sCommandParts[2];
					if (ivSubDirectory.equals("DEBUG"))
					{
						ivDEBUG = true;
						ivSubDirectory = "";
					}
				}
				
				// Check if the DEBUG command has been specified
				if (i == 3)
				{
					if (sCommandParts[3].equals("DEBUG"))
					{
						ivDEBUG = true;
					}
				}
			}
		}
		
		// Check if we have the minimum
		if (ivDocumentID == "")
		{
			// No Document ID has been specified return an error
			setError("No Download Item ID Specifed");
			return;			
		}
	}
	
	private void downloadDocument()
	{
		TCConnection tc = null;
		OutputStream os = null;

		try
		{
			TCConnection tcConnection = null;
			
			TCCFactory tcf = TCCFactory.getInstance();
			tcConnection = tcf.createTCConnection(ivDownloadChannel);
			TCInputStream tcis = tcConnection.getInputStream();
			
			// Extract current user to send part of the document ID to server.
 			HttpSession session = ivRequest.getSession();
 			String userId = (String) session.getAttribute( "BrowserSignOnName" );
 			LOGGER.debug("Document download's userId to TCServer=" + userId);
 			tcis.setUserName(userId);
			
			tcis.setFileName(ivDocumentID);
			if (ivSubDirectory != "")
			{
				tcis.setFilePath(ivSubDirectory);
			}
			
			byte[] bytes = tcis.read();
			
			ivResponse.setContentLength(bytes.length);
	        
	        os = ivResponse.getOutputStream();
	        os.write(bytes);
	        os.close();
	        
	        tc.close();
			ivConnection.close();
			
		} catch (TCClientException tccex)
		{
			//LOGGER.error(sError);
			setError(tccex.getMessage());
		}	
		catch(IOException ioex)
		{
			setError(ioex.getMessage());
		}
		catch(TCException tcex)
		{
			setError(tcex.getMessage());
		}
		
	}
	
	private void downloadDocumentTester()
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream(readFile());
	        byte[] bytes = new byte[bis.available()];
	        //response.setContentType(contentType);
	        OutputStream os = ivResponse.getOutputStream();
	        bis.read(bytes);
	        os.write(bytes);  
	        
	    } 
	    catch(IOException e){
	    	
	    }           		
	}
	private InputStream readFile()
	{
		File f = null;
		FileInputStream fis = null;
		try
		{
			f = new File("C:\\Work\\Downloads", "");
			f = new File(f.getAbsolutePath(), "test.pdf");
			fis = new FileInputStream(f);
		}
		catch(Exception x)
		{
			
		}
		return fis;
	}
}
