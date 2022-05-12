////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   FileManager
//
//  Description   :   Provides facilities to read and write files.
//
//  Modifications :
//
//    25/08/04   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class FileManager.
 */
public class FileManager implements Serializable
{
	
	/** The iv file name. */
	private String ivFileName = "";			// File to read/write
	
	
  	/**
	   * Instantiates a new file manager.
	   * 
	   * @param sFileName the s file name
	   */
	  public FileManager( String sFileName )
	{
    	ivFileName = sFileName;
	}
	
	
	// Reads in the contents of a text file
	/**
	 * Read file.
	 * 
	 * @return the string
	 */
	public String readFile()
	{
		String fileContents = "";
		
		try
		{
			File file = new File( ivFileName );
			InputStreamReader is = new InputStreamReader(new FileInputStream(file));
			BufferedReader br = new BufferedReader(is);

			String s = "";

			while ((s = br.readLine()) != null) {
				fileContents += s;
			}
			is.close();
			is = null;
			br = null;
		}
		catch (Exception e) 
		{
			System.out.println("FileManager : readFile() exception : " + e.getMessage() );
			fileContents = "";
		}
		
		return( fileContents );
	}
	
	
	/**
	 * Write file.
	 * 
	 * @param sText the s text
	 * 
	 * @return true, if successful
	 */
	public boolean writeFile( String sText )
	{
		try
		{
			BufferedWriter bWriter = new BufferedWriter( new FileWriter( ivFileName ) );
			bWriter.write( sText );
			bWriter.flush();
			bWriter.close();
			return( true );
		}
		catch ( Exception e )
		{
			// Error writing file so display error will be displayed on separate page
			System.out.println("FileManager : writeFile() exception : " + e.getMessage() );
			return( false );
		}
	}
	
    /**
     * Copy file.
     * 
     * @param src the src
     * @param dst the dst
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(src));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(dst));
        copyFile(in, out);
        in.close();
        out.close();
    }

    /**
     * Copy file.
     * 
     * @param in the in
     * @param out the out
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    /**
     * Reads file from the input stream. Works only if the char set used in the
     * input stream is encoded in the default platform charset.
     * 
     * @param in Input stream
     * 
     * @return Content of the file
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static StringBuffer readFileFromInputStream(InputStream in) throws IOException {
    	byte[] buffer = new byte[1024];
    	StringBuffer buff = new StringBuffer(2048);
    	for(int i = in.read(buffer); i != -1; i = in.read(buffer)) {
    		buff.append(new String(buffer, 0, i));
    	}
    	in.close();
    	return buff;
    }
    
}