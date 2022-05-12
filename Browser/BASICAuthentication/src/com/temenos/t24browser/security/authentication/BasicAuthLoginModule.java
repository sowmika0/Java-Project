package com.temenos.t24browser.security.authentication;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;


/**
 * Login Module to implement BASIC Authentication in Browser
 * 
 * @author wzahran
 */

public class BasicAuthLoginModule implements LoginModule
{
	private Subject subject = null;
    private CallbackHandler callbackHandler = null;
    private Map sharedState = null;
    private Principal rolePrincipal = null;
    private Principal userPrincipal = null;
    
	private String userName = "";
	private String passWord = "";
	
    public final void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState,  final Map options) 
    {
    	this.subject = subject;
    	this.callbackHandler = callbackHandler;
    	this.sharedState = sharedState;
    }	
	
    public final boolean abort() throws LoginException 
    {
        this.callbackHandler = null;
        this.sharedState = null;
        this.subject = null;    	
        return true;
    }	

    public final boolean commit() throws LoginException 
    {
    	// We know the username & password that the user supplied
    	// set these ino the principal object so that the BASIC Authentication filter
    	// can extract & insert them into the sign-on URL.
    	
    	String principalName = this.userName + ":" + this.passWord;
		// Create a principal & add it to the subject
		T24UserPrincipal t24up = new T24UserPrincipal(principalName);
		
		// Set the username & password incase...
		t24up.setUserName(this.userName);
		t24up.setPassword(this.passWord);
		
		this.setUserPrincipal(t24up);
		
		// Set the role name as 't24user' as defined in web.xml:
		//     	<auth-constraint>
		//			<role-name>t24user</role-name>
    	//		</auth-constraint>
		T24RolePrincipal t24rp = new T24RolePrincipal("t24user");
		// Also set the username & password
		t24rp.setUserName(this.userName);
		t24rp.setPassword(this.passWord);
		
		this.setRolePrincipal(t24rp);
		
        Set principals = new HashSet();
        principals.add(t24up);
        principals.add(t24rp);
        
        // Add both principals into the subject
		subject.getPrincipals().addAll(principals);
		//subject.getPublicCredentials().addAll(principals);
	
		return true;
	}

    public final boolean login() throws LoginException 
    {
    	try
    	{
    		// Create callback handlers to get the username & passwords
    		// that have been supplied
            NameCallback nameCallback = new NameCallback("username"); // "username" is just a label
            PasswordCallback passwordCallback = new PasswordCallback("temenos", false); // "temenos" is just a label
            this.handleCallbacks(nameCallback, passwordCallback);
            
            String password = new String(passwordCallback.getPassword());
            String username = new String(nameCallback.getName());
            
            // Put the username & password into the sharedState
            this.sharedState.put("PW_KEY", password);
            this.sharedState.put("USER_KEY", username);
            setPassword(password);
            setUserName(username);
    	}
    	catch(LoginException x)
    	{
    		
    	}
    	
         return true;
    }	
	
	public final boolean logout() throws LoginException 
	{
		return true;
	}
	

    /**
     * @return the callbackHandler
     */
    private CallbackHandler getCallbackHandler() throws LoginException 
    {
        if (this.callbackHandler == null) 
        {
            throw new LoginException("CallbackHandler required");
        }
        return this.callbackHandler;
    }
	
    private void handleCallbacks(NameCallback nameCallback, PasswordCallback passwordCallback) throws LoginException 
    {
        try 
        {
            this.getCallbackHandler().handle(new Callback[] { nameCallback, passwordCallback });
        } 
        catch (IOException e) 
        {
            throw new LoginException(e.getLocalizedMessage());
        } 
        catch (UnsupportedCallbackException e) 
        {
            throw new LoginException(e.getLocalizedMessage());
        }
    }	
	
    private void setRolePrincipal(final Principal rolePrincipal) 
    {
        this.rolePrincipal = rolePrincipal;
    }

     private void setUserPrincipal(final Principal userPrincipal) 
     {
        this.userPrincipal = userPrincipal;
     }
     
     private void setUserName(final String userName)
     {
    	 this.userName = userName;
     }
     
     private void setPassword(final String passWord)
     {
    	 this.passWord = passWord;
     }
}
