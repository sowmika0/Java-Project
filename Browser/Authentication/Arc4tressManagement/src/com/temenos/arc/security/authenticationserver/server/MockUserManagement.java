package com.temenos.arc.security.authenticationserver.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.authenticationserver.common.StringGenerator;
import com.temenos.arc.security.authenticationserver.server.ArcUserManagement;


/*TODO this is currently under src as XmlUserManagement's no-arg ctor requires it.
 * We should refactor this.
*/
public class MockUserManagement implements ArcUserManagement {
    Map t24UserToArcUserMap = new HashMap();
    final static String FAILURE = "TEMENOS";
    final static String UNEXPECTED_USER_EXISTENCE = "USER";

    private void fakeFailureBasedOn(String t24UserName) {
        String ignoreCaps = t24UserName.toUpperCase();
        if (FAILURE.indexOf(ignoreCaps) != -1) {
            throw new ArcAuthenticationServerException("mock failure"); 
        }
        if (UNEXPECTED_USER_EXISTENCE.indexOf(ignoreCaps) != -1) {
            throw new ArcUserStateException("mock unexpected user state");
        }         
    }
    
    public String addUser(String t24UserName, String memorableData,Calendar startDate,Map custInfo) {        
        System.out.println("MockUserManagement.addUser(" 
                         + t24UserName + ", " 
                         + memorableData + ", "
                         + startDate.getTime().toString() + ")"                          
                         );
        fakeFailureBasedOn(t24UserName);
        
        if (userExists(t24UserName)) {
            throw new ArcUserStateException();
        }
        String arcUserId = StringGenerator.getRandomNumericString(9);
        t24UserToArcUserMap.put(t24UserName, arcUserId);
        
        return StringGenerator.getRandomAlphaNumericString(8);
    }

    public String getArcUserId(String t24UserName) {
        System.out.println("MockUserManagement.getArcUserId(" 
                + t24UserName + ")"                          
                );
        if (!userExists(t24UserName)) {
            throw new ArcUserStateException();
        }        
        return (String) t24UserToArcUserMap.get(t24UserName);
    }

    public void removeUser(String t24UserName) {
        System.out.println("MockUserManagement.removeUser(" 
                + t24UserName + ")"                          
                );
        fakeFailureBasedOn(t24UserName);
        if (t24UserToArcUserMap.remove(t24UserName) == null) {
            throw new ArcUserStateException();
        }
    }

    public boolean userExists(String t24UserName) {
        System.out.println("MockUserManagement.userExists(" 
                + t24UserName + ")"                          
                );
        return t24UserToArcUserMap.containsKey(t24UserName);
    }
    
    public Map getUserFailureCounts(String t24UserName){
    	return null;
    }
    
    public Map resetFailureCounts(String t24UserName){
    	return null;
    }
    
    public String resetUserPassword(String t24UserName){
    	return null;
    }
    public String resetUserPin(String t24UserName){
    	return null;
    }
    public AuditLog[] getAuditLogs(String t24UserName,String fromDate,String toDate){
     	return null;
    }
    
    public String updateMemorableWord(String t24UserName,String memWord){
    	return null;
    }
    public String updateAuthServerUserId(String t24UserName,String authServerUserId){
    	return null;
    }
    
    public Map getAuthenticatorStatus(String t24UserName){
    	return null;
    }
    public Map updateAuthenticatorStatus(String t24UserName,String authType,String status){
    	return null;
    }
    
    public String updateCustomerData(String t24UserName,Map customerInfo){
    	return null;
    }
}
