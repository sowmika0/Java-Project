package com.temenos.arc.security.filter;


import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.rsa.authmgr.common.OnDemandAuthenticationConstants;
import com.rsa.authmgr.common.SecurIDAuthenticationConstants;
import com.rsa.authn.LoginCommand;
import com.rsa.authn.data.AbstractParameterDTO;
import com.rsa.authn.data.FieldParameterDTO;
import com.rsa.command.CommandException;
import com.rsa.command.CommandTarget;
import com.rsa.common.SystemException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * RSAAuthentication class
 * Responsible for authenticating with SMS and SecureID
 * Responsible for changing the PIN and accepting the Next Token
 * 
 * @Author Sara (6363)
 */
public class RSAAuthentication implements Serializable{
    
	private static final long serialVersionUID = 1L;
	private LoginCommand loginCommand = new LoginCommand();
	private AuthenticatedTarget session = null;
	
	//flag for a new pin
	private boolean newPinRequired = false;
	//flag for next token
	private boolean nextTokenRequired = false;
	//Constant Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAAuthentication.class);

    
	/**
	 * The doAuthentication method is responsible for on-demand & SecureID RSA authentication.
	 * This involves authenticating with username and PIN.
	 * After which the user will receive an SMS/email and then authenticates with the passcode.
	 * Authenticationg with username and passcode(for SecureID)
	 * This method handles the Username and PIN auth and creates a field parameter which then can be used.
	 * This method also handles if Next Token on any logon
	 * 
	 * @param target - Holds the connection to RSA AM server
	 * @param userid - User Id
	 * @param pass  - PIN or Passcode
	 * @param rsaType - Token or SMS
	 * @return
	 * @throws SystemException 
	 * @throws CommandException 
	 * @throws UnknownHostException 
	 * @throws Exception
	 */
    public FieldParameterDTO doAuthentication(CommandTarget target, String userid, String pass, String rsaType) throws CommandException, SystemException, UnknownHostException {
    	
    	session = new AuthenticatedTarget(target);
    	LOGGER.debug("@@@ doAuthentication");
		loginCommand.setNetAddress(InetAddress.getLocalHost());
		//Check the rsaType for TOKEN or SMS and set the authentication method accordingly
		if(rsaType.equalsIgnoreCase(Constant.SESSION_ATTR_AUTH_RSA_TOKEN)){
			LOGGER.debug("Authentication method : Token");
			loginCommand.setAuthenticationMethodId(SecurIDAuthenticationConstants.METHOD_ID);
		}else if(rsaType.equalsIgnoreCase(Constant.SESSION_ATTR_AUTH_RSA_SMS)){
			LOGGER.debug("Authentication method : SMS");
			loginCommand.setAuthenticationMethodId(OnDemandAuthenticationConstants.METHOD_NAME);
		}
		loginCommand.setPolicyGuid(null);
		loginCommand.setUsingTransientSession(true);
		loginCommand.execute(session);
		
		// Loop through the login parameter challenges from RSA AM Server
		while (!(loginCommand.checkAuthenticatedState() || loginCommand.checkFailedState())) {
			
			AbstractParameterDTO[] params = loginCommand.getParameters();

			for (AbstractParameterDTO param : params) {
				
				if( param instanceof FieldParameterDTO ) {
					FieldParameterDTO field = (FieldParameterDTO)param;
					LOGGER.debug("CURRENT PROMPTKEY-------" +field.getPromptKey());
					
					// Username
					if (field.getPromptKey().equalsIgnoreCase("AUTHENTICATIONSERVICE_PRINCIPALID")) {
						field.setValue(userid);
						LOGGER.debug("Executing LoginCommand (AUTHENTICATIONSERVICE_PRINCIPALID)");
						loginCommand.execute(session);
					}
					// PIN
					else if (field.getPromptKey().equalsIgnoreCase("PIN")) {
						field.setValue(pass);
						LOGGER.debug("Executing LoginCommand (PIN)");
						loginCommand.execute(session);
					}
					//New Pin required for SMS Auth
					else if (field.getPromptKey().equalsIgnoreCase("UPDATED_PIN")) {
						LOGGER.debug("Returning field (UPDATED_PIN)");
						this.newPinRequired = true;
						return field;
					}		
					//New Pin required for SecureID Auth
					else if (field.getPromptKey().equalsIgnoreCase("ACEPROXY_NEWPIN")) {
						LOGGER.debug("Returning field (ACEPROXY_NEWPIN)");
						this.newPinRequired = true;
						return field;
					}
					//Tokencode
					else if (field.getPromptKey().equalsIgnoreCase("Tokencode")) {
						LOGGER.debug("Returning field (Tokencode)");
						return field;
					}
					//Passcode
					else if (field.getPromptKey().equalsIgnoreCase("ACEPROXY_PASSCODE")) {
						field.setValue(pass);
						LOGGER.debug("Executing LoginCommand (ACEPROXY_PASSCODE)");
						loginCommand.execute(session);
					}
					//Next Token
					else if (field.getPromptKey().equals("NEXT_TOKENCODE")) {
						LOGGER.debug("Returning field (NEXT_TOKENCODE)");
						this.nextTokenRequired = true;
						return field;
					}
				}
			}
		} 
		
		return null;
    }
   
    /**
     * Changes the PIN for rsa user both for TOKEN and SMS authentication
     * @param pin
     * @param nextTokenCode
     * @return
     * @throws SystemException 
     * @throws CommandException 
     */
    public FieldParameterDTO doNewPIN(String pin, String nextTokenCode) throws CommandException, SystemException {
    	
    	LOGGER.debug("@@@ doNewPin");
    	int reachedNewPin = 0;
    	while (!(loginCommand.checkAuthenticatedState() || loginCommand.checkFailedState())) {
   
			AbstractParameterDTO[] params = loginCommand.getParameters();
			// Loop through all the prompts
			for (AbstractParameterDTO param : params) {
				
				if( param instanceof FieldParameterDTO ) {
					FieldParameterDTO field = (FieldParameterDTO)param;
					LOGGER.debug("# Current field.getPromptKey(): " + field.getPromptKey());
						
						// Set new pin for SMS Auth
						if (field.getPromptKey().equals("UPDATED_PIN")) {
							LOGGER.debug("--> UPDATED_PIN " + pin);
							field.setValue(pin);
						}
						// Verify new pin for SMS Auth
						else if (field.getPromptKey().equals("UPDATED_PIN_CONFIRM")) {
							LOGGER.debug("--> UPDATED_PIN_CONFIRM " + pin);
							field.setValue(pin);
							loginCommand.execute(session);
						}
						// Token code for SMS Auth
						else if (field.getPromptKey().equals("Tokencode")) {
							LOGGER.debug("--> Tokencode (forwarding)");
							this.newPinRequired = false;	// reset this value
							return field;
						}
						//New pin for SecureID auth
						else if (field.getPromptKey().equals("ACEPROXY_NEWPIN")) {
							reachedNewPin++;
							if (reachedNewPin >= 2){
								loginCommand.execute(session);
								return null;
							}
							LOGGER.debug("--> ACEPROXY_NEWPIN " + pin);
							field.setValue(pin);
						}
						//confirm pin for SecureID auth
						else if (field.getPromptKey().equals("ACEPROXY_VERIFY")) {
							LOGGER.debug("--> ACEPROXY_VERIFY " + pin);
							field.setValue(pin);
						}
						//Next Token for SecureID auth
						else if (field.getPromptKey().equals("NEXT_TOKENCODE")) {
							LOGGER.debug("--> NEXT_TOKENCODE " + nextTokenCode);
							field.setValue(nextTokenCode);
							this.newPinRequired = false;    // reset this value
							loginCommand.execute(session);
						}
	
				}
			}
    	}
		return null;
    }

    
    /**
     * This method perform the Next Token mechanism, when user wrongly types passcode for three times(depends)
     * He will be asked to enter the Next Token after entering the correct one
     * @param passcode
     * @return
     * @throws SystemException 
     * @throws CommandException 
     */
    public boolean doNextToken(String strNextToken) throws CommandException, SystemException {
    	
    	LOGGER.debug("@@@ doNextToken");
    	
		// Get params from logincommand
		AbstractParameterDTO[] params = loginCommand.getParameters();

		// Loop through all the prompts
		for (AbstractParameterDTO param : params) {
			
			if( param instanceof FieldParameterDTO ) {
				FieldParameterDTO field = (FieldParameterDTO)param;
				LOGGER.debug("# Current field.getPromptKey(): " + field.getPromptKey());
					// Next Token
					if (field.getPromptKey().equals("NEXT_TOKENCODE")) {
						field.setValue(strNextToken);
						LOGGER.debug("--> NEXT_TOKENCODE:) " + strNextToken);
						loginCommand.execute(session);
					}
			}
		}
		
		return loginCommand.checkAuthenticatedState();
    }
    
    
    /**
     * Getter for loginCommand
     * @return
     */
    public LoginCommand getLoginCommand() {
		return loginCommand;
	}

    /**
     * Setter for loginCommand
     * @param loginCommand
     */
	public void setLoginCommand(LoginCommand loginCommand) {
		this.loginCommand = loginCommand;
	}
    
	/**
	 * Getter for session (AuthenticatedTarget)
	 * @return
	 */
    public AuthenticatedTarget getSession() {
		return session;
	}

    /**
     * Setter for session (AuthenticatedTarget)
     * @param session
     */
	public void setSession(AuthenticatedTarget session) {
		this.session = session;
	}
	
	/**
	 * Getter for getNewPinRequired
	 * @return
	 */
    public boolean getNewPinRequired() {
		return newPinRequired;
	}
    
	/**
	 * Getter for nextTokenRequired
	 * @return
	 */
    public boolean getNextTokenRequired() {
		return nextTokenRequired;
	}	
}

