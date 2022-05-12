package com.temenos.arc.security.jaas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.ArcSession;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;

public class CommitterTest extends TestCase {

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCommit() {
		
		ArcLoginModule loginModule = new ArcLoginModule();
        Set credentials = new HashSet();
        // Log in to 4tress using pin authenticator
        
        ArcSession sessionId = new ArcSession("dummysession");
        credentials.add(sessionId);
		Subject subject = new Subject(false,new HashSet(),credentials, new HashSet());
		Map sharedState = new HashMap();
		// Get the map that was used to create the config
		loginModule.initialize(subject, null, sharedState, createOptionsMap());
		ArcUserPrincipal userPrincipal = new ArcUserPrincipal("dummyuser");
		ArcRolePrincipal rolePrincipal = new ArcRolePrincipal("dummyrole");
		Set principals = new HashSet();
		principals.add(userPrincipal);
		principals.add(rolePrincipal);
		JaasCommitter c = new JaasCommitter(null);
		try {
			assertTrue(c.commit(loginModule, 
					principals, 
					loginModule.getSubject().getPublicCredentials()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// Now check the relationships are correct
		// First the subject 
		Subject newSubject = loginModule.getSubject();
		Set newUserPrincipals = newSubject.getPrincipals(ArcUserPrincipal.class);
		assertTrue(newUserPrincipals.size()==1);
		Set newRolePrincipals = newSubject.getPrincipals(ArcRolePrincipal.class);
		assertTrue(newRolePrincipals.size()==1);
		ArcUserPrincipal newUserPrincipal = (ArcUserPrincipal)newUserPrincipals.iterator().next();
		assertTrue(newUserPrincipal.getName().equals("dummyuser"));
		ArcRolePrincipal newRolePrincipal = (ArcRolePrincipal)newRolePrincipals.iterator().next();
		assertTrue(newRolePrincipal.getName().equals("dummyrole"));

		// Now check the credentials
		Set loginCredentials = newSubject.getPublicCredentials(ArcLoginModule.class);
		assertTrue(loginCredentials.size()==1);
		Set sessionCredentials = newSubject.getPublicCredentials(ArcSession.class);
		assertTrue(sessionCredentials.size()==1);
		ArcSession session = (ArcSession) sessionCredentials.iterator().next();
		assertTrue(session.toString().equals("dummysession"));
		
		// Next check the principals
		Subject userSubject = newUserPrincipal.getSubject();
		assertTrue(userSubject.equals(newSubject));

		
		
		
		
		
		
	}

	private Map createOptionsMap() {
		Map properties = new HashMap();
		properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24PASS,
				"ATR_T24PW");
		properties.put(AuthenticationServerConfiguration.ATTRIBUTE_T24USER,
				"ATR_T24UID");
		properties.put(AuthenticationServerConfiguration.UP_AUTH_USER,
				"T24SYSLOG");
		properties.put(AuthenticationServerConfiguration.UP_AUTH_PASSWORD,
                "g0rmenghast");
		properties.put(AuthenticationServerConfiguration.AUTHENTICATION_TYPE,
				"AT_SYSLOG");
		properties.put(AuthenticationServerConfiguration.CHANNEL,
				"CH_DIRECT");
		properties.put(AuthenticationServerConfiguration.CHARSET,
				"UTF-8");
		properties.put(AuthenticationServerConfiguration.DOMAIN,
				"DOMAIN1");		
		properties.put(JaasConfiguration.AUTHENTICATION_DELEGATE,
				"com.temenos.arc.security.jaas.JaasUsernamePasswordAuthenticator");
		properties.put(JaasConfiguration.AUTHENTICATION_COMMITTER,
				"com.temenos.arc.security.jaas.CompositeCommitter");
		properties.put(JaasConfiguration.CALLBACK_OVERRIDE,
				"com.temenos.arc.security.jaas.T24AttributeCallbackHandler");
		return properties;
	}

}
