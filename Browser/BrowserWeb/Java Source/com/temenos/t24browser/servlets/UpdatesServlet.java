package com.temenos.t24browser.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class UpdatesServlet extends HttpServlet implements Serializable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
	
	// For testing puposes
//	private static String updateXml = "<updates><platform>AIX5.1</platform><update>R09_AA_Customer_1_AIX5.1.tar</" +
//	 "update><update>R09_EB_System_1_AIX5.1.tar</update><update>R09_EB_ArcIbClient_1_ALL.tar</" +
//	 "update><update>R09_AA_Account_1_AIX5.1.tar</update><update>R09_EB_ToolBoxClient_1_ALL.tar</" +
//	 "update><update>R09_EB_Browser_28_AIX5.1.tar</update><update>R09_EB_BrowserClient_1_ALL.tar</" +
//	 "update></updates>";
//	
	private HttpServletRequest internalRequest = null;
    private String updatesDir = null;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws javax.servlet.ServletException, java.io.IOException
	{
		LOGGER.debug( "***** Updates Service : Start Request ***********************************************" );
		long timeInMillis = System.currentTimeMillis(); // Start our timer asap		

		// Ensure the request parameters are treated as UTF-8 characters.
		// This should be done by the EncodingFilter, but just in case it hasn't been used...
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			LOGGER.error("Unable to set request character encoding to UTF8");
		}
		// Get todays date
		String todaysDate = String.valueOf( Calendar.getInstance().get( Calendar.YEAR)) + "-" + 
							String.valueOf( Calendar.getInstance().get( Calendar.MONTH) + 1) +  "-" +
							String.valueOf( Calendar.getInstance().get( Calendar.DAY_OF_MONTH)) +  "-" +
							String.valueOf( Calendar.getInstance().get( Calendar.HOUR)) +
							String.valueOf( Calendar.getInstance().get( Calendar.MINUTE));
 	    // Build the content disposition string
		String contentDisposition = "inline; filename=T24Updates-" + todaysDate + ".zip"; 
		
		
		// Mark up the response
		response.reset();
		response.setContentType( "application/zip");
		response.setHeader("Content-type", "application/zip");
		response.setHeader("Content-disposition",contentDisposition);
		
		
		internalRequest = request;
		
		updatesDir = getServletContext().getInitParameter("updatesDir");
		if( updatesDir == null)
		{
			System.out.println("Specify the updates directory in web.xml");
		}
		else
		{
			String responseXml = (String)request.getParameter( "updateList");
			List< String> files = getUpdatesList( responseXml);
			// Zip the files
			zipFiles( files, response.getOutputStream());
		}
		

	}

	private void setError(String sError)
	{
		internalRequest.setAttribute("errorMessage", sError);
	}
	
	private void zipFiles( List< String> files, OutputStream out)
	{
		try
		{
			BufferedOutputStream bufferedOutStream = new BufferedOutputStream( out);
			ZipOutputStream zipStream = new ZipOutputStream( bufferedOutStream);
			for( String updateId : files)
			{
				addZipEntry( zipStream, updateId);
			}
			
			// Finish up
			zipStream.finish();
			zipStream.close();
			bufferedOutStream.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			setError( "zipFiles(): " + e.getMessage());
		}		
	}
	
	private void addZipEntry( ZipOutputStream zipStream, String updateId) throws Exception
	{
		// Generate id and directory
		String updatePath = generateDir( updateId) + "/" + updateId;
		//System.out.println( updatePath);
		// Create zip entry
		ZipEntry zipEntry = new ZipEntry( updateId);
		zipStream.putNextEntry( zipEntry);
		// Read the file in bytes
		byte[] inFileBytes = read( updatePath);
		// Write the file
		zipStream.write( inFileBytes);
		// Finish this entry
		zipStream.closeEntry();
	}
	
	private byte[] read(String filePath) throws IOException 
	{
		File f = new File( filePath);
		byte[] buf = new byte[(int) f.length()];
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
        int offset = 0;
        while (offset < buf.length) {
            int n = in.read(buf, offset, buf.length - offset);
            if (n < 0)
                throw new EOFException();
            offset += n;
        }
        return buf;
    }
	
	private List<String> getUpdatesList( String xml)
	{
		List<String> updatesList = new ArrayList<String>();
//		String currentUpdate = "";
//		// Match with a pattern of "<update>...</update>" using 'reluctant' qualifier ('?')
//		Pattern respPattern = Pattern.compile( "<update>(.*?)</update>" , Pattern.DOTALL );
//		Matcher respMatcher = respPattern.matcher( xml );
//		while ( respMatcher.find() )
//		{
//			currentUpdate = respMatcher.group( 1);
//			updatesList.add( currentUpdate);
//		}
		String[] tokenArray = xml.split( "[|]");
		for( int i = 0; i < tokenArray.length; i++)
		{
			updatesList.add( tokenArray[ i]);
		}
		return updatesList;
	}

	private String getPlatform( String xml) throws Exception
	{
		String platform = "";
		//		 Match with a pattern of "<platform>...</platform>" using 'reluctant' qualifier ('?')
		Pattern respPattern = Pattern.compile( "<platform>(.*?)</platform>" , Pattern.DOTALL );
		Matcher respMatcher = respPattern.matcher( xml );
		if( respMatcher.find() )
		{
			platform = respMatcher.group( 1);
		}
		
		if ( platform.equals( "NO.PLATFORM") || platform.equals( ""))
		{
			throw new Exception("No platform available");
		}
		
		return platform;
	}
	
	private String generateDir( String updateId) throws Exception
	{
		String dir = "";
		
		updateId = updateId.replaceAll(".tar", "");
		
		String[] result = updateId.split("_");
	    String gaRelease = result[ 0];
	    String product = result[ 1];
	    String component = result[ 2];
	    String version = result[ 3];
	    String platform = result[ 4];
	    // If there are more than 5 elements in the array, assume the platform has an underscore
	    if ( result.length > 5 ) 
	    {
	    	platform = platform + "_" + result[5];
	    	// Go another level in case there is another underscore.
	    	// Shouldn't ever enter this condition. Just in case.
	    	if ( result.length > 6 )
	    	{
	    		platform = platform + "_" + result[6];
	    	}
	    }
	    // Produce the directory
		dir = updatesDir + gaRelease + "/" + product + "_" + component + "/" + platform;
			
		return dir;
	}
	

}
