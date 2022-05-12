package com.temenos.arc.security.authenticationserver.ftress;

import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.jaas.ArcLoginModule;
import com.temenos.arc.security.jaas.ArcUserPrincipal;
import com.temenos.arc.security.jaas.Committable;
import com.temenos.arc.security.jaas.JaasConfiguration;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


public final class T24ImpersonateCommitter implements Committable {

	private static Logger logger = LoggerFactory.getLogger(T24ImpersonateCommitter.class);
	
    /**
     * @param config not used
     */
    public T24ImpersonateCommitter(final JaasConfiguration config) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.temenos.arc.security.jaas.Committable#commit(javax.security.auth.Subject, java.util.Set, java.util.Set)
     */
    public final boolean commit(final LoginModule lm, final Set principals, final Set credentials)
            throws FailedLoginException {
    	logger.info("T24ImpersonateCommitter()...");
    	ArcLoginModule arcloginModule = null;
        FtressLoginModule ftressloginModule = null;
        
          if (lm instanceof ArcLoginModule) {
        	  
        	  logger.info("logger module is ArcLoginModule");
              arcloginModule = (ArcLoginModule)lm;
          } else {
              if (lm instanceof FtressLoginModule) {
            	  	logger.info("logger module is FtressLoginModule");
                    ftressloginModule = (FtressLoginModule)lm;
              } else {
                    return false;
              }
          }
          
          if ((arcloginModule == null) && (ftressloginModule == null)) {
        	  logger.error("Illegal Argument Exception");
              throw new IllegalArgumentException("Subject required");
          }
          
          if (principals == null) {
        	  logger.error("Principal is null");
              throw new IllegalArgumentException("User and role principals required");
          }
          if (credentials == null) {
        	  logger.error("Credential is null");
              throw new IllegalArgumentException("Credentials required");
          }
          
          //For ftressLoginModule get principals and credentials and add to subject
          if (ftressloginModule != null) {
        	  Subject subject = ftressloginModule.getSubject();
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
        	  ArcUserPrincipal principal = (ArcUserPrincipal) arcPrincipals.iterator().next();
        	  principal.setSubject(subject);

        	  // add public credentials
        	  Set arcCredentials = subject.getPublicCredentials(FtressLoginModule.class);
        	  if (!arcCredentials.isEmpty()) {
        		  throw new IllegalStateException(
    					"There should be no ArcLoginModule added to the Subject yet");
        	  }
        	  // Add the T24 Credentials here
        	  subject.getPublicCredentials().add(ftressloginModule);
          }
          return true;
    }
}

