package com.temenos.arc.security.rsa;

import com.rsa.authagent.authapi.AuthSession;
import com.temenos.arc.security.authenticationserver.common.ArcSession;

public class AuthenticationResponse {
	public static final int ACCESS_OK=0;
	public static final int ACCESS_DENIED=1;
	public static final int NEXT_CODE_REQUIRED=2;
	public static final int NEXT_CODE_BAD=4;
	public static final int NEW_PIN_REQUIRED=5;
	public static final int PIN_ACCEPTED=6;
	public static final int PIN_REJECTED=7;

	private AuthSession session;
	private int result;
	
	public AuthenticationResponse(AuthSession session, int result) {
		this.session = session;
		this.result = result;
	}
	
	public ArcSession getArcSession() {
		return new ArcSession(session);
	}
	
	public int getAuthenticationResult() {
		return result;
	}
}
