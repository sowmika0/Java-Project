package com.temenos.arc.security.filter;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

import com.rsa.authn.AuthenticationCommandException;
import com.rsa.authn.LoginCommand;
import com.rsa.authn.LogoutCommand;
import com.rsa.authn.data.AbstractParameterDTO;
import com.rsa.authn.data.FieldParameterDTO;
import com.rsa.command.CommandException;
import com.rsa.command.CommandTarget;
import com.rsa.command.TargetableCommand;
import com.rsa.common.AuthenticationConstants;
import com.rsa.common.GUID;
import com.rsa.common.SessionAttributeKey;
import com.rsa.common.SystemException;
import com.rsa.common.GUID.UnknownGUIDTypeException;
import com.rsa.security.RSAPrincipal;
import com.rsa.security.SecurityContext;
import com.rsa.session.SetSessionAttributeCommand;

/**
 * Helper class for working with the command API. This wraps some common tasks
 * into a simpler API suitable for most applications.
 */
public class AuthenticatedTarget implements com.rsa.command.ClientSession, Serializable {

	/** value for serialVersionUID */
	private static final long serialVersionUID = 20080322L;

	/** Our own client address */
	private static final InetAddress LOCAL_IP;

	static {
		try {
			LOCAL_IP = InetAddress.getLocalHost();
		} // end try
		catch (UnknownHostException e) {
			// can this be thrown?
			throw new SystemException("Unexpected unknown host exception", e);
		} // end catch
	}

	/**
	 * CommandTarget delegate, we expect this to be a properly initialized,
	 * connected CommandTarget
	 */
	private CommandTarget delegate = null;

	/** The current user's authenticated session. */
	private String sessionID = null;

	/** The current user's GUID. */
	private String userGuid = null;

	/** Subject representing the current user. */
	private Subject subject = SecurityContext.getAnonymousSubject();

	/**
	 * Construct a new AuthenticatedTarget using this delegate.
	 * 
	 * @param delegate
	 *            the connected CommandTarget to use
	 */
	public AuthenticatedTarget(CommandTarget delegate) {
		this.delegate = delegate;
	} // end AuthenticatedTarget()

	/**
	 * Return the session ID for the authenticated session contained within.
	 * 
	 * @return the session ID
	 */
	public final String getSessionId() {
		return sessionID;
	} // end getSessionId()

	/**
	 * Return the user GUID for the logged in user.
	 * 
	 * @return the GUID
	 */
	public final String getUserGuid() {
		return userGuid;
	} // end getUserGuid()

	/**
	 * Execute the given command using this target.
	 * 
	 * @param cmd
	 *            the command to execute
	 * @return the command with results
	 * @throws CommandException
	 *             for business logic command errors
	 * @throws SystemException
	 *             for unexpected system failures
	 */
	public TargetableCommand executeCommand(final TargetableCommand cmd)
			throws CommandException, SystemException {
		try {
			PrivilegedExceptionAction<TargetableCommand> action = new PrivilegedExceptionAction<TargetableCommand>() {
				@SuppressWarnings("synthetic-access")
				public TargetableCommand run() throws Exception {
					return delegate.executeCommand(cmd);
				} // end run()
			} // end new
			;

			return SecurityContext.doAs(subject, action);
		} // end try
		catch (CommandException e) {
			throw e;
		} // end catch
		catch (SystemException e) {
			throw e;
		} // end catch
		catch (Exception e) {
			throw new SystemException(
					"Unexpected exception during command invocation", e);
		} // end catch
	} // end executeCommand()

	/**
	 * Required method by interface. Return "this".
	 * 
	 * @return "this"
	 * @throws SystemException
	 *             not thrown
	 */
	public final CommandTarget generateCacheableTarget() throws SystemException {
		// propagate the generation to the delegate and replace current one
		delegate = delegate.generateCacheableTarget();
		return this;
	} // end generateCacheableTarget()

	/**
	 * Authenticates a user and establishes a session for future operations.
	 * Some caveats:
	 * <ul>
	 * <li>Only password authentications are handled by this method.</li>
	 * <li>Does not accommodate situations where additional prompting for items
	 * such as identity sources in deployments where duplicate user IDs are
	 * allowed.</li>
	 * </ul>
	 * 
	 * @param handler
	 *            the ParameterHandler to get the user details from
	 * @param domain
	 *            The domain the caller is logged in to administer
	 * @param method
	 *            The authentication method. Must be one of these:
	 *            <ul>
	 *            <li>AuthenticationConstants.RSA_PASSWORD_METHOD</li>
	 *            <li>AuthenticationConstants.LDAP_PASSWORD_METHOD</li>
	 *            <li>SecurIDAuthenticationConstants.METHOD_ID</li>
	 *            </ul>
	 * @param policy
	 *            The authentication policy to use for this login. Do not
	 *            specify a policy and a method at the same time. If you do the
	 *            result is undefined.
	 * @param transientSession
	 *            <code>true</code> to use a transient session which does not
	 *            have the overhead of cluster wide replication. This is
	 *            beneficial if the login operation is done as part of an
	 *            application which does not depend on the authenticated session
	 *            for subsequent operations. You should dispose of this session
	 *            after the successful authentication if it is not going to be
	 *            used any more.
	 * @throws CommandException
	 *             if unable to process the login operation
	 */
	public final void login(ParameterHandler handler, String domain,
			String method, String policy, boolean transientSession)
			throws CommandException {
		
		LoginCommand loginCommand = new LoginCommand();
		loginCommand.setNetAddress(LOCAL_IP);
		loginCommand.setAuthenticationMethodId(method);
		loginCommand.setPolicyGuid(policy);
		loginCommand.setUsingTransientSession(transientSession);
		loginCommand.execute(delegate);

		// get the user name from the handler
		String username = "n/a";

		// respond to prompts until either success or failure is reported
		while (!(loginCommand.checkAuthenticatedState() || loginCommand.checkFailedState())) {
			
			AbstractParameterDTO[] params = loginCommand.getParameters();
			
			for (AbstractParameterDTO param : params) {
				
				try {
					System.out.println("Current param: " + param.getPromptKey());
					handler.handleParameter(param);
				} 
				catch (IOException e) {
					AuthenticationCommandException ex = new AuthenticationCommandException("Failed to process required parameter: "	+ param.getPromptKey());
					ex.initCause(e);
					throw ex;
				}
				
				if (AuthenticationConstants.PRINCIPAL_ID.equals(param.getPromptKey())) {
					username = ((FieldParameterDTO) param).getValue();
				} // end if
			}
			loginCommand.execute(delegate);
			
			System.out.println("loginCommand.checkAuthenticatedState(): " + loginCommand.checkAuthenticatedState());
		}

		// if we succeeded finish the task at hand
		if (loginCommand.checkAuthenticatedState()) {
			sessionID = loginCommand.getSessionId();
			userGuid = loginCommand.getPrincipalGuid();

			try {
				GUID guid = GUID.parse(userGuid);
				Principal principal = new RSAPrincipal(username, guid, sessionID, LOCAL_IP);
				subject = SecurityContext.getSubject(principal);
			} // end try
			catch (UnknownGUIDTypeException e) {
				throw new AuthenticationCommandException("Unknown GUID type: "
						+ userGuid);
			} // end catch
		} // end if
		else {
			throw new AuthenticationCommandException("Login failed! State: "
					+ loginCommand.getAuthenticationState());
		} // end else

		// If the active domain is specified (i.e. the realm that the console
		// user is logged in to administer, then set the default realm
		// attribute.
		if (null != domain) {
			SetSessionAttributeCommand setAttribCmd = new SetSessionAttributeCommand(
					null, SessionAttributeKey.PRINCIPAL_REALM_GUID, domain);

			setAttribCmd.execute(this);
		} // end if
	} // end login()

	/**
	 * Force the logged in user off.
	 * 
	 * @see com.rsa.command.ClientSession#logout()
	 */
	public final void logout() throws CommandException {
		Subject anonymous = SecurityContext.getAnonymousSubject();

		if (!subject.equals(anonymous)) {
			LogoutCommand logoutCommand = new LogoutCommand();
			logoutCommand.setSessionId(sessionID);
			logoutCommand.execute(this);
			subject = anonymous;
			sessionID = null;
		} // end if
	} // end logout()

	/**
	 * Interface that must be implemented by the callers of the #login
	 */
	public interface ParameterHandler {
		/**
		 * Handles acquiring and displaying information to the user based on the
		 * supplied parameter (e.g enter new PIN).
		 * 
		 * @param parameter
		 *            ParameterDTO with requested parameter
		 * @throws IOException
		 *             if I/O errors are encountered
		 */
		void handleParameter(AbstractParameterDTO parameter) throws IOException;
	} // end ParameterHandler
} // end AuthenticatedTarget
