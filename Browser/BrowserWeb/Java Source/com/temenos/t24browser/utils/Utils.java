////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   Utils
//
//  Description   :   Provides utilities for Browser.
//
//  Modifications :
//
//    26/04/05   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils implements Serializable
{
	
	/** Because Utils implements Serializable */
	private static final long serialVersionUID = 1L;
	
	public static final String BROWSER_ENCRYPT = "browserEncryption.config";

	 /**
	  * Instantiates a new utils.
	  */
	 public Utils()
	{
	}
	
	
	/**
	 * Replace all.
	 * 
	 * @param source the source
	 * @param searchString the search string
	 * @param replaceWith the replace with
	 * 
	 * @return the string
	 */
	public static String replaceAll( String source, String searchString, String replaceWith )
	{
        int iCurrentPos = 0;
        int iSearchStringPos = 0;
        StringBuffer sbNewText = new StringBuffer();
        int iSearchStringLength = searchString.length();

        while ( ( iSearchStringPos != -1 ) && ( iCurrentPos < source.length() ) )
        {
          iSearchStringPos = source.indexOf( searchString, iCurrentPos );
          if ( iSearchStringPos != -1 )
          {
            // Replace this occurrence of searchString
            sbNewText.append( source.substring( iCurrentPos, iSearchStringPos ) );
            sbNewText.append( replaceWith );
            iCurrentPos = iSearchStringPos + iSearchStringLength;
          }
        }

        if ( iCurrentPos < source.length() )
        {
          // Still some remaining text after the last searchString so append it
          sbNewText.append( source.substring( iCurrentPos, source.length() ) );
        }
        
        return( sbNewText.toString() );
	}

	
	/**
	 * Get the value of an XML node from a String.
	 * 
	 * @param xml the xml to scan
	 * @param nodeName the node name
	 * 
	 * @return the node from string
	 */
	public static String getNodeFromString(String xml, String nodeName)
	{
		if ((  xml == null ) || ( xml.equals("") ) )
		{
			return( "" );
		}
		else
		{
			String sPattern = "<" + nodeName + ">(.*?)</" + nodeName + ">";    // Includes the tag
			
			Pattern pattern = Pattern.compile( sPattern, Pattern.DOTALL );
			Matcher matcher = pattern.matcher( xml );

			if ( matcher.groupCount() > 0 )
			{
				boolean more = matcher.find();
				
				if ( more )
				{
					return( matcher.group(1) );					// Remove the tag
				}
				else
				{
					return( "" );
				}
			}
			else
			{
				return( "" );
			}
		}
	}
	
	/**
	 * Returns the contents of the first node found matching the given xpath in the given xml string.
	 * @param xml The xml fragment to search
	 * @param xpath The xpath expression indicating where the data should be found.
	 * @return The contents of the first mathcing node found, including any child nodes.
	 */
	public static String getXpathFromString(String xml, String xpath) {
		String out = xml;
		String[] inputs = xpath.split("/");
		for (int i=0; i<inputs.length; i++) {
			out = Utils.getNodeFromString(out,inputs[i]);
		}
		return out;
	}
	

	/**
	 * Returns an Arraylist of Strings, one for each matching node found in given xml. 
	 * @param xml The xml to search.
	 * @param node The node to search for.
	 * @return An ordered list of all matching nodes found.
	 */
	public static ArrayList<String> getAllMatchingNodes(String xml, String node) {
		ArrayList<String> out = new ArrayList<String>();
		Pattern pattern = Pattern.compile("<" + node + ">(.*?)</" + node + ">", Pattern.DOTALL);
		Matcher matcher = pattern.matcher( xml );
		while (matcher.find()) {
			out.add(matcher.group(1));
		}
		return out;
	}
	
	/**
	 * Removes the node from a String.
	 * 
	 * @param xml the xml
	 * @param nodeName the node name
	 * 
	 * @return the string
	 */
	public static String removeNodeFromString(String xml, String nodeName)
	{
		if ((  xml == null ) || ( xml.equals("") ) )
		{
			return( null );
		}
		else
		{
			String startTag = "<" + nodeName + ">";
			String endTag = "</" + nodeName + ">";
			int startPos = xml.indexOf( startTag );
			
			if ( startPos != -1 )
			{
				int endTagLen = endTag.length();
				int endPos = xml.indexOf( endTag );
				xml = xml.substring( 0, startPos ) + xml.substring( endPos + endTagLen, xml.length() );
			}
			
			return( xml );
		}
	}
	
	
	/**
	 * Gets a value from a HttpServletRequest either
	 * for an attribute or a parameter, starting from a parameter.
	 * @param request is a HttpServletRequest 
	 * @param name is String representing the name of the value we want.
	 * 
	 * @return the string
	 */
	public static String getValue(HttpServletRequest request, String name)
	{
		String value = null;
		value = request.getParameter( name);
		// If parameter does not exist then try for an attribute
		if( value == null)
		{
			value = (String)request.getAttribute( name);
		}
		
		return value;
	}
	
	/**
	 * Converts HTML entities (like &lt;) into the entity they represent.
	 * Converts &quot; &apos; &amp; &lt; and &gt;
	 * Decodes '&' last, so that '&amp;quot;' will result in '&quot;' not '&"'.
	 * This is the correct behaviour because 'quot;' is not a valid html entity.
	 * @param in The input string to convert.
	 * @return The string with all entities decoded.
	 */
	public static String decodeHtmlEntities(String in) {
		String out = in.replaceAll("&quot;", "\"");
		out = out.replaceAll("&apos;", "'");
		out = out.replaceAll("&lt;", "<");
		out = out.replaceAll("&gt;", ">");
		out = out.replaceAll("&amp;", "&"); // NB: Change '&amp;' last!.
		out = out.replaceAll("&#33;", "!");
		return out;
	}
	
	/**
	 * Converts 'the big 5' HTML characters (", <, ', > and &) into the entity encoded form like '&quot;'.
	 * @param in The input string to convert.
	 * @return The string with all entities encoded.
	 */
	public static String encodeHtmlEntities(String in) {
		String out = in.replaceAll("&", "&amp;"); // NB: Change '&' before any others!
		out = out.replaceAll("\"", "&quot;");
		out = out.replaceAll("'", "&apos;");
		out = out.replaceAll("<", "&lt;");
		out = out.replaceAll(">", "&gt;");
		out = out.replaceAll("!", "&#33;");
		return out;
	}
	
	
	
	
	/**
	 * Converts Output encoded entities based on the items configured under browserEncryption.congig file
	 * 
	 * 
	 * 
	 * @param in The input string to convert.
	 * @param contextRoot The context real path to read browserEncryption.config file
	 * @param nonHTMLCharacters Boolean value;  true- output encoded values will be converted to plain text characters; 
	 * 											false- output encoded values will be converted to HTML entity characters 
	 * @return The string with all entities decoded.
	 * 
	 */
	public static String decodeOutputEncodingEntities(String in, String contextRoot, boolean nonHTMLCharacters) {
		String eachLine = null;
		try {
			if (in != null) {
				String path = contextRoot+File.separator+BROWSER_ENCRYPT;
				
				FileInputStream fstream = new FileInputStream(path);
	            DataInputStream inStream = new DataInputStream(fstream);
	            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
	            
	            HashMap<String,String> charMap = new HashMap<String,String>();
	            while ((eachLine = br.readLine()) != null) {
	            	String valuePair[] = eachLine.split(" ");
	            	charMap.put(valuePair[0], valuePair[1]);
	            }
	            String value;
	            for(String key : charMap.keySet()){
	            	//Condition added to replace the characters only if exists.
	            	if (in.indexOf(key) > 0) {
	            		value = charMap.get(key);
	            		if (nonHTMLCharacters) {
	            			//Convert value to plain character and then Replace
	            			char plainVal = (char)Integer.parseInt(value.substring(3, value.length()-1));
	            			in = in.replaceAll(key, Character.toString(plainVal));	
	            		} else {
	            			//Replace value directly
	            			in = in.replaceAll(key, value);	
	            		}
	            	}
	            	
	            }
			}
		} catch (FileNotFoundException e) {
			//LOGGER.warn("Browser Encryption Configuration file doesn't exist");
		} catch (EOFException e) {
			//logger.warn(e.getMessage());
		} catch (IOException e) {
			//logger.warn("Error While Reading Browser Encryption Config File " + e.getMessage());
		} catch (NullPointerException e) {
			//logger.warn(e.getMessage());
		} catch (Exception e) {
			//logger.warn(e.getMessage());
		}
		
		return in;
	}
	
	


	/**
	 * Gets the IP Address for a HTTP Request
	 * @param req The http request
	 * @return The ip address.
	 */
	public static String getClientIpAddress( HttpServletRequest req )
	{
		String addr = req.getRemoteAddr();

		// If the client is the local host then resolve this to the real IP address		
		if ( addr.equalsIgnoreCase("127.0.0.1") )
		{
			try
			{
            	addr = InetAddress.getLocalHost().getHostAddress();
			}
     		catch ( Exception e )
     		{
     			addr = req.getRemoteAddr();
     		}
		}

		return( addr );
	}
	
	/**
	 * Gets a servlet context parameter
	 * @param servlet The servlet object
	 * @param paramName The name of the parameter
	 * @return The parameter value
	 */
    public static String getServletContextParameter( ServletContext context, String paramName )
    {
		String paramValue = context.getInitParameter(paramName);
		
		return paramValue;
    }
    
	/**
	 * Gets the String of the URL function to be decoded from UTF8 characters
	 * @param req The String
	 * @return Decoded UTF-8 String.
	 */
	public static String decodeUTF8(String s) {	
		StringBuffer sbuf = new StringBuffer () ;
	    int l  = s.length() ;
	    int ch = -1 ;
	    int b, sumb = 0;
	    for (int i = 0, more = -1 ; i < l ; i++) {
	      /* Get next byte b from URL segment s */
	      switch (ch = s.charAt(i)) {
		case '%':
		  ch = s.charAt (++i) ;
		  int hb = (Character.isDigit ((char) ch) 
			    ? ch - '0'
			    : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
		  ch = s.charAt (++i) ;
		  int lb = (Character.isDigit ((char) ch) 
			    ? ch - '0'
			    : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
		  b = (hb << 4) | lb ;
		  break ;
		case '+':
		  b = ' ' ;
		  break ;
		default:
		  b = ch ;
	      }
	    /* Decode byte b as UTF-8, sumb collects incomplete chars */
	    if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
		sumb = (sumb << 6) | (b & 0x3f) ;	// Add 6 bits to sumb
		if (--more == 0) sbuf.append((char) sumb) ; // Add char to sbuf
	      } else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
		sbuf.append((char) b) ;			// Store in sbuf
	      } else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
		sumb = b & 0x1f;
		more = 1;				// Expect 1 more byte
	      } else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
		sumb = b & 0x0f;
		more = 2;				// Expect 2 more bytes
	      } else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
		sumb = b & 0x07;
		more = 3;				// Expect 3 more bytes
	      } else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
		sumb = b & 0x03;
		more = 4;				// Expect 4 more bytes
	      } else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
		sumb = b & 0x01;
		more = 5;				// Expect 5 more bytes
	      }
	      /* No need to test if the UTF-8 encoding is well-formed */
	    }
	    return sbuf.toString() ;
	  }
	  public static String encryptPassword(String str, HttpServletRequest request)
	  {
	  	HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
		ArrayList <Integer> collection = new ArrayList <Integer>();
		char[] charPassString = null;
		char[] charChangedString = null;
		if (str == null || str =="")
		{
			return "";
			}
		// Create a string buffer to reverse the string
		StringBuffer passString = new StringBuffer(str);
		passString = passString.reverse();
		// create char arrays to hold reversed and encrypted chars
		charPassString = (passString.toString()).toCharArray();
		charChangedString = new char[charPassString.length];
		// Instantiate random number type
		Random randomGenerator = new Random();
		for (int k = 0; k < passString.length(); k++)
		{
			// Get the random number with in the specified range of string length
			Integer randomInt = randomGenerator.nextInt(passString.length());
			// Ensures no duplicates are added in the list, so that proper encryption is guaranteed
			if (!collection.contains(randomInt))
			{
				// Add to collection to check for duplicates
				collection.add(randomInt);
				// Extract random number position char and populate into another array
				charChangedString[k] = charPassString[randomInt];
				// Have put the random number with sequence in map to retrieve in decrypt
				hashMap.put(randomInt,k);
				}
			else
			{
				// Incase of duplicate number of random generated loop again with same loop value
				// It ensures random numbers length equal to string length
				--k;
				continue;
				}
			}
		// Store this hashMap in session to retrieve the encrypted string in decryption
		HttpSession session = request.getSession();
		session.setAttribute("LoginEncryptArray", hashMap);
		session.setAttribute("LoginEncryptString",charChangedString);
		return String.valueOf(charChangedString);
	  }
		
	  public static String decryptPassword(HttpServletRequest request)
	  {
		  HttpSession session = request.getSession();
		  if(session.getAttribute("LoginEncryptString")!=null)
		  {
		  	char[] charChangedString = (char[])session.getAttribute("LoginEncryptString");
		  	// return variable as string buffer
		  	StringBuffer returnStr = new StringBuffer();
		  	String charAppend = "";
		  	// Loop through map key set
		  	@SuppressWarnings("unchecked")
		  	HashMap<Integer, Integer> hash = (HashMap<Integer, Integer>)session.getAttribute("LoginEncryptArray");
		  	List<Integer> sortedKeys= new ArrayList<Integer>(hash.keySet());
		  	Collections.sort(sortedKeys);
		  	Iterator<Integer> iterator = sortedKeys.iterator();
		  
		  	while (iterator.hasNext())
		  	{
				// retrieve each char by stored sequence order with random number position
			  	Integer random = iterator.next();
			  	Integer aa = hash.get(random);
			  	charAppend += charChangedString[aa];
			 }
		  
		  	// Remove details from session.
		  	session.removeAttribute("LoginEncryptArray");
		  	session.removeAttribute("LoginEncryptString");
		  	// reverse and return the string.
		  	return returnStr.append(charAppend).reverse().toString();
		  }
		  else
		  {
		  	return "";
		  	}
	  	}
	  }	