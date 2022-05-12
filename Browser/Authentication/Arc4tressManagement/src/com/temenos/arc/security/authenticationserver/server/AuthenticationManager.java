/**
 * 
 */
    package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.common.AuthenticationResponse;

/**
 * This Interface is common for all Authentication servers.
 * @author Cerun
 * 
 */
public interface AuthenticationManager {
	
	public AuthenticationResponse authenticatePassword(String userId ,String password);
	
	public AuthenticationResponse authenticateMemorableWord(String userId , String memWord);
	
	public AuthenticationResponse authenticateOneTimePassword(String userId, String oneTimePassword);
	
	public AuthenticationResponse authenticateChallengeResponse(String userId, String challenge ,String response);
	
}
