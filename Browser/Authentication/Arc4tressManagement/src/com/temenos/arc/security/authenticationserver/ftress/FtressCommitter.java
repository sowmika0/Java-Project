package com.temenos.arc.security.authenticationserver.ftress;

import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.authenticationserver.common.ArcAuthenticationServerException;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.Committable;
import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.authenticationserver.ftress.FtressLoginModule;

/**
 * Implementatin of the {@link Committable} interface that sets up the desired
 * relationships between the {@link Subject}, {@link ArcCredential},
 * {@link ArcUserPrincipal} and {@link FtressLoginModule} instances.
 * 
 * @author Cerun
 * 
 */
public final class FtressCommitter implements Committable {

	public FtressCommitter(JaasConfiguration config) {
		super();
		FtressHelpers70.getInstance().setConfig(config);
	}

	/**
	 * 
	 * 
	 */
	public boolean commit(LoginModule lm, Set principals, Set credentials)
			throws ArcAuthenticationServerException, FailedLoginException {
		boolean result = true;
		FtressLoginModule loginModule = null;
		if (lm instanceof FtressLoginModule) {
			loginModule = (FtressLoginModule) lm;
		} else {
			return false;
		}
		if (loginModule == null) {
			throw new IllegalArgumentException("Subject required");
		}
		if (principals == null) {
			throw new IllegalArgumentException(
					"User and role principals required");
		}
		if (credentials == null) {
			throw new IllegalArgumentException("Credentials required");
		}
		// create Subject
		Subject subject = loginModule.getSubject();
		subject.getPrincipals().addAll(principals);
		subject.getPublicCredentials().addAll(credentials);
		Set arcPrincipals = subject.getPrincipals(ArcUserPrincipal.class);
		if (arcPrincipals.isEmpty()) {
			throw new IllegalStateException(
					"User Principal should have been added to the Subject at this point");
		}
		if (arcPrincipals.size() != 1) {
			throw new IllegalStateException(
					"Only one ARC user Principal expected");
		}
		ArcUserPrincipal principal = (ArcUserPrincipal) arcPrincipals
				.iterator().next();
		principal.setSubject(subject);
		// add public credentials
		Set arcCredentials = subject
				.getPublicCredentials(FtressLoginModule.class);
		if (!arcCredentials.isEmpty()) {
			throw new IllegalStateException(
					"There should be no ArcLoginModule added to the Subject yet");
		}
		// Add the T24 Credentials here
		subject.getPublicCredentials().add(loginModule);
		FtressHelpers70.getInstance().addImpersonationCredentialsTo(subject);
		return result;
	}

}
