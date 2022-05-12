package com.temenos.arc.security.listener;

import java.io.Serializable;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.temenos.arc.security.authenticationserver.ftress.FtressLoginModule;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.LoginModuleException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * Ensures that the user is logged out of the auth server when the HttpSession is destroyed.
 * e.g. on timeout.  Needs to be configured in the deployment descriptor
 * @author Cerun     
 */
public class FtressAuthenticationListener implements HttpSessionListener, Serializable {

	private static Logger logger = LoggerFactory.getLogger(FtressAuthenticationListener.class);

	public FtressAuthenticationListener() {
        super();
    }

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // no-op
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    	if (logger.isInfoEnabled()) logger.info("destroying session");
        HttpSession httpSession = httpSessionEvent.getSession();
        // Need to check if this is just the change of session id on login
        Object changeSessionObject = httpSession.getAttribute("SESSION_ID_CHANGE");
        
        if (changeSessionObject != null && Boolean.TRUE.equals((Boolean)changeSessionObject)) {
        	// remove the change session attribute and return without logging out the session
        	logger.info("Not logging out via Login Module as this is just a session id change.");
        	httpSession.removeAttribute("SESSION_ID_CHANGE");
        	return;
        }
    	logger.info("Logging out via Login Module.");
        ArcUserPrincipal arcUserPrincipal = (ArcUserPrincipal) httpSession.getAttribute(ArcUserPrincipal.class.getName());
        if (arcUserPrincipal != null) {
            Subject subject = arcUserPrincipal.getSubject();
            Set credentials = subject.getPublicCredentials(FtressLoginModule.class);
            if (credentials.size() == 0) {
                throw new IllegalStateException("Expected at least one ArcLoginModule");
            }
            Iterator modules = credentials.iterator();
            while(modules.hasNext()) {
            	FtressLoginModule lm = (FtressLoginModule) modules.next();
                    logout(lm);
            }
            httpSession.removeAttribute(ArcUserPrincipal.class.getName());
        }            
    }
    private final void logout(final FtressLoginModule lm) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AuthPermission("destroyCredential"), AccessController.getContext());
        }
        
        try {
            if (!lm.logout()) {
                throw new LoginModuleException("Logout failed");
            }
        } catch (LoginException e) {
            throw new LoginModuleException(e.toString());
        }
    }
    
}
