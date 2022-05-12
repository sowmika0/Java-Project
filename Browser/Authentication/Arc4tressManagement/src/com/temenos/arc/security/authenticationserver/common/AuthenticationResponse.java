/**
 * 
 */
package com.temenos.arc.security.authenticationserver.common;

/**
 * This class is common for authentication response from every Authentication Server specific implementation.
 * @author Cerun
 *
 */
public class AuthenticationResponse {

	private String reason;
	private String code;
/**
 * 
 * @return
 */	
	public String getCode(){
		return this.code;
	}
/**
 * 
 * @return
 */	
	public String getReason(){
		return this.reason;
	}
/**
 * 
 * @param code
 */
	public void setCode(String code){
		this.code = code;
	}
/**
 * 
 * @param code
 */
	public void setReason(String reason){
		this.reason = reason;
	}

}
