
    package com.temenos.arc.security.authenticationserver.server;


/**
 * This class defines all the constants used in XmlAuthenticationMAnager
 * 
 * @author Cerun
 *
 */
public final class XmlAuthenticationManagerConstants {

    // T24 user name */
    public static String userName = "t24UserName";
    //Authentication type */
    public static String authenticationType = "authenticationType";
    //Password tag. This is for all auth type except for CR
    public static String password = "password";
    //Challenge tag. This tag is only for CR authentication
    public static String challenge = "challenge";   
    //  Challenge tag. This tag is only for CR authentication
    public static String response = "response";
/** The following is the return tag from XmlAuthenticationManager **/
    public static String args = "args";
    //This tag gives the reason.
    public static String returnComment= "returnComment";
    // This tag gives the return status.
    public static String returnState = "returnState";
}
