package com.temenos.arc.security.authenticationserver.server;

import java.util.Calendar;
import java.util.Map;
/**
 * @author jannadani
 *
 */
public interface ArcUserManagement {
	
	/** Constant user USERMANAGEMENT for the SUCCESSFULL response **/
	public static final String SUCCESS = "0";
	
	/** Constant user USERMANAGEMENT for the FAILURE response **/
	public static final String FAILURE = "1";
	   
    /** 
     * Add a user to the authentication server.  Clients should call {@link ArcUserManagement.userExists} first: 
     * an attempt to add a pre-existing user will throw an {@link ArcUserStateException}    
     * @param t24UserName The username in T24 (EB.EXTERNAL.USER), and must not already exist in the auth server  
     * @param t24Password The (encrypted - NYI) password that is will be used to log into T24
     * @param arcUserId The user id that is given to the end customer that will be used to authenticate to the auth server  
     * @param secret The end-user supplied secret that will be stored in the auth server 
     */
    public String addUser(String t24UserName, String memorableData,Calendar startDate,Map custInfo);

    /** 
     * Remove a user from the authentication server.  Clients should call {@link ArcUserManagement.userExists} first: 
     * an attempt to remove a non-existing user will throw an {@link ArcUserStateException}    
     * @param t24UserName The username in T24 (EB.EXTERNAL.USER), must exist in the auth server
     */
    public void removeUser(String t24UserName);

    /** 
     * Returns true if a user exists in the authentication server.      
     * @param t24UserName The username in T24 (EB.EXTERNAL.USER), must exist in the auth server
     */
    public boolean userExists(String t24UserName);

    /** 
     * Returns the user id with which the end-user authenticates to the auth server.   
     * Clients should call {@link ArcUserManagement.userExists} first: 
     * an attempt to get an id for a non-existing user will throw an {@link ArcUserStateException}    
     * @param t24UserName The username in T24 (EB.EXTERNAL.USER), must exist in the auth server.
     */
    public String getArcUserId(String t24UserName);
    
    /**
     * Returns the failure count of authenticators of the user
     * an attempt to get failure count of non existing user will throw an {@ link ArcUserStateException}
     * @param t24UserName the username in T24 (EB.EXTERNAL.USER).must exist in the auth server.
     */
    
    public Map getUserFailureCounts(String t24UserName);
   
    /**
     * Resets the failure count of all authenticators of the user
     * an attempt to reset failure count of non existing user will throw an {@ link ArcUserStateException}
     * @param t24UserName the username in T24 (EB.EXTERNAL.USER).must exist in the auth server.
     */
      
    public Map resetFailureCounts(String t24UserName);
    
    /**
     * Reset the password of the user  
     * an attempt to reset password for non existing users will throw an {@ link ArcUserStateException}
     * @param t24UserName
     * @return status whether reset is success or failure
     */
    public String resetUserPassword(String t24UserName);
    
    /**
     * Reset the pin of the user  
     * an attempt to reset pin for non existing users will throw an {@ link ArcUserStateException}
     * @param t24UserName
     * @return status whether reset is success or failure
     */
    public String resetUserPin(String t24UserName);
    
    
    /**
     * Get the log of the user  
     * an attempt to get log of non existing users will throw an {@ link ArcUserStateException}
     * @param t24UserName
     * @param fromDate if fromDate is null, creation Date of user will be assigned
     * @param toDate if toDate id null, currentDate will be assigned
     * @return an array AuditLog with all the log information.
     */
    public AuditLog[] getAuditLogs(String t24UserName,String fromDate,String toDate);

    
    /**
     * update the Memorable word for the user  
     * @param t24UserName,memWord
     * @param memWord - clear memWord will be encrypted and added to the Ftress server.
     * @return status whether reset is success or failure
     */
    public String updateMemorableWord(String t24UserName,String memWord);
    
    
    /**
     * update the suthentication server user id  
     * update only external refernece id of the user and not any authenticators username.
     * @param t24UserName,autheServerUserId
     * @param authServerUserId- new Userid for the existing Ftress users.
     * @return status whether reset is success or failure
     */
    public String updateAuthServerUserId(String t24UserName,String authServerUserId);
    

    /**
     * Get all the authenticator status of the user  
     * method will return Authentication Type Code, Start Date ,ExpiryDate and Status of all authenticato
     * available for user.
     * @param t24UserName
     * @return Map key-AuthenticationTypeCode Value-"startDate+endDate+Status"
     */
    public Map getAuthenticatorStatus(String t24UserName);

    
    /**
     * update the authenticator status of the user  
     * method will update the status of Authenticator ENABLED to DISABLED
     * @param t24UserName
     * @param authType
     * @param status
     * @return Get the updated authenticator status of the mentioned user.
     */
    public Map updateAuthenticatorStatus(String t24UserName,String authType,String status);
    
    /**
     * update the customer Date of the User  
     * method will update CUSTOMER Details of the user in Ftress server
     * @param t24UserName
     * @param Map - Customer Details Key-String Value -String 
     * @return status whether update is success or failure
     */
    public String updateCustomerData(String t24UserName,Map customerInfo);
}
 