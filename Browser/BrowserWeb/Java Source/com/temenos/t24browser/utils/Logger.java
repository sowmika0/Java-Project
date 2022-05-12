////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   Logger
//
//  Description   :   Writes the specified message to a servlet log file, usually an event log. 
//
//  Modifications :
//
//    20/11/02   -    Initial Version.
//	  
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.utils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class Logger.
 */
public class Logger implements Serializable
{
	
	/** The is message log. */
	private boolean isMessageLog = false;	// Whether we are logging messages
	
	/** The log level. */
	private String logLevel = "NONE";			// Level of logging
	
	/** The context. */
	ServletContext context;
	
	/** The Constant LOG_LEVEL_NONE. */
	private static final String LOG_LEVEL_NONE  = "NONE";
	
	/** The Constant LOG_LEVEL_INFO. */
	private static final String LOG_LEVEL_INFO  = "INFO";
	
	/** The Constant LOG_LEVEL_ERROR. */
	private static final String LOG_LEVEL_ERROR = "ERROR";
	
	/** The Constant LOG_LEVEL_DEBUG. */
	private static final String LOG_LEVEL_DEBUG = "DEBUG";

	//Default Constructor
	/**
	 * Instantiates a new logger.
	 */
	public Logger()
	{
	}
	
	/**
	 * Instantiates a new logger.
	 * 
	 * @param context the context
	 */
	public Logger( ServletContext context )
  	{
  		this.context = context;
  	}
  	
	/**
	 * Replace password.
	 * 
	 * @param req the req
	 * 
	 * @return the string
	 */
	public static String replacePassword(HttpServletRequest req) {
		StringBuffer str = new StringBuffer();
		Map map = req.getParameterMap();
		Set set = map.entrySet();
		String routineName = req.getParameter("routineName");
		for(Iterator iter = set.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry)iter.next();
			str.append("Parameter : ");
			String parameter = (String)entry.getKey();
			str.append(parameter);
			str.append(", Value : ");
			if(parameter!=null && parameter.toLowerCase().contains("password")){
				//Mask the password values in all password parameters
				str.append("****** ");
			}
			else if(parameter!=null && parameter.equals("routineArgs") && routineName!=null && routineName.equals("OS.PASSWORD")){
				//Mask the password values in the routineArgs parameter
				String[] argList = (String[]) entry.getValue();
				String[] argValue = argList[0].split(":",-1);
				if(argValue[0].equals("PROCESS.REPEAT") || argValue[0].equals("PROCESS.EXPIRED") ){
					//Mask the OldPassword and Password values in routineArgs parameter
					str.append(argValue[0]+":"+argValue[1]+":******:******:"+argValue[4]+":"+argValue[5]+" ");
				}
				else if(argValue[0].equals("PROCESS.CHANGE")){
					//Mask the password, newPassword and confirmNewPassword values in routineArgs parameter
					str.append(argValue[0]+":"+argValue[1]+":******:******:******"+" ");
				}
				else{
					for(int i = 0; i < argList.length; i++) {
						str.append(argList[i] + " ");
					}
				}
			}			
			else {
				try{
					// may be the request parameter would a type of a string or an array of string. so check needs to be done to avoid cast exception.
					if (entry.getValue().getClass().isArray()==true)
					{
						// string array parameter
				String[] values = (String[]) entry.getValue();
				for(int i = 0; i < values.length; i++) {
					str.append(values[i] + " ");
				}
					}else{
						// string parameter
						String values = (String) entry.getValue();
						str.append(values);
					}
				}catch(Exception e){
					// log the details and catch the exception if some other data type,
					e.printStackTrace(); e.getMessage();
				}
			}
			str.append('\n');
		}
		return str.toString();
	}
	
	/**
	 * Replace password.
	 * 
	 * @param logMessage the log message
	 * 
	 * @return the string
	 */
	public static String replacePassword( String logMessage )
	{
		if (logMessage==null)
			return null;
			
		
		if (logMessage.indexOf("<routineName>OS.PASSWORD</routineName>") > 0)
			logMessage = hideTagContents("routineArgs", logMessage);

		if (logMessage.indexOf("<responseType>XML.REPEAT.PASSWORD</responseType>") > 0)
			logMessage = hideTagContents("responseData", logMessage);

		if (logMessage.indexOf("<application>PASSWORD.RESET</application>") > 0)
			logMessage = hideTagContents("message", logMessage);
			
         if (logMessage.indexOf("<routineName>OS.LICENSE.CHECK</routineName>") > 0)
			logMessage = hideTagContents("routineArgs", logMessage);
         

		logMessage = hideTagContents("password", logMessage);
		
		return logMessage;
	}
	
	/**
	 * Hide tag contents.
	 * 
	 * @param tagName the tag name
	 * @param message the message
	 * 
	 * @return the string
	 */
	private static String hideTagContents(String tagName, String message)
	{
		
		if (message==null)
			return null;
		
		StringBuffer sNewMessage = new StringBuffer();
		
		int iPassword;
		String sTagClose = new String("</" + tagName + ">");
		String sTagOpen = new String("<" + tagName + ">");
		int intPosFullTag = 0;;
		int intPosFullTagClose = 0;
		
		intPosFullTag = message.indexOf(sTagOpen);
		
		if ( intPosFullTag > 0)
		{
			intPosFullTagClose = message.indexOf(sTagClose);
			if (intPosFullTagClose > intPosFullTag)
			{
				int iPasswordLength;

				sNewMessage.append( message.substring(0,intPosFullTag+sTagOpen.length()) );

				iPasswordLength = intPosFullTagClose - (intPosFullTag+sTagOpen.length());

				StringBuffer sPasswordReplacement = new StringBuffer();
				for ( int iChar = 0; iChar < iPasswordLength; iChar++)
					sPasswordReplacement.append("*");

				sNewMessage.append( sPasswordReplacement );
				sNewMessage.append( message.substring(intPosFullTagClose) );
			}
		}
		
		if ( sNewMessage.length() == 0 )
		{
			sNewMessage.append( message );
		}
		
		return sNewMessage.toString();
	}

	/**
	 * Log message.
	 * 
	 * @param logMessage the log message
	 */
	public void logMessage( String logMessage )
	{
		logMessage = replacePassword(logMessage);
		if ( isMessageLog )
		{
			context.log( logMessage );
		}
	}
	
	/**
	 * Log message.
	 * 
	 * @param logMessage the log message
	 * @param messageLevel the message level
	 */
	public void logMessage( String logMessage, String messageLevel )
	{
		logMessage = replacePassword(logMessage);
		if ( ( isMessageLog ) && ( messageLevel.equals( logLevel ) || logLevel.equals(LOG_LEVEL_DEBUG)) )
		{
			context.log( logMessage );
		}
	}
	
	/**
	 * Log message.
	 * 
	 * @param logMessage the log message
	 * @param t the t
	 */
	public void logMessage( String logMessage, Throwable t )
	{
		logMessage = replacePassword(logMessage);
		context.log( logMessage, t );
	}
	
	/**
	 * Log message.
	 * 
	 * @param logMessage the log message
	 * @param messageLevel the message level
	 * @param t the t
	 */
	public void logMessage( String logMessage, String messageLevel, Throwable t )
	{
		logMessage = replacePassword(logMessage);
		if ( ( isMessageLog ) && ( messageLevel.equals( logLevel ) || logLevel.equals(LOG_LEVEL_DEBUG)) )
		{
			context.log( logMessage, t );
		}
	}
	
	/**
	 * Sets the is message log.
	 * 
	 * @param setLog the new is message log
	 */
	public void setIsMessageLog( String setLog )
	{
		this.isMessageLog = (setLog.equalsIgnoreCase("YES"))?true:false;
	}
	
	/**
	 * Sets the log level.
	 * 
	 * @param level the new log level
	 */
	public void setLogLevel( String level )
	{
		if ( ( level == null ) || ( level.equals( "" ) ) )
		{
			context.log( "No log level specified - Setting to NONE" );
		}
		else
		{
			if ( level.equals(LOG_LEVEL_NONE) || 
				 level.equals(LOG_LEVEL_INFO) || 
 				 level.equals(LOG_LEVEL_ERROR) || 
				 level.equals(LOG_LEVEL_DEBUG) )
			{
				this.logLevel = level;
				context.log( "Setting log level to " + level );
			}
			else
			{
				context.log( "Invalid log level - " + level + " - Setting to NONE" );
			}
		}
	}
}