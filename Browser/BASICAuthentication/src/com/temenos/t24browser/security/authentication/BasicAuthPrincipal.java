package com.temenos.t24browser.security.authentication;

/**
 * BasicAuthPrincipal
 * 
 * @author wzahran
 */
interface BasicAuthPrincipal {
	
	void setUserName(String userName);
	
	void setPassword(String password);
	
	String getUserName();
	
	String getPassword();
}
