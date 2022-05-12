package com.temenos.arc.security.jaas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import junit.framework.TestCase;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.authenticationserver.common.FilePathHelper;

public class ArcLoginModuleTest extends TestCase {

	ArcLoginModule loginModules[];
    protected AuthenticationServerConfiguration configs[];
    Map sharedState = new HashMap();

	protected void setUp() throws Exception {
		super.setUp();
        String filePath = FilePathHelper.getPathToSecurityDir() + "jaas/arcLoginModuleTest.config";
        Map configMaps [] = new ConfigurationFileParser(filePath, "TEST").parse();
        configs = new AuthenticationServerConfiguration[configMaps.length];
        loginModules = new ArcLoginModule[configMaps.length];        
        for (int i = 0; i < configMaps.length; ++i) {
            loginModules[i] = new ArcLoginModule();
            configs[i] = new JaasConfiguration(configMaps[i]);
        }        
	}
	
	
	public void testStackedModules() {
        Set credentials = new HashSet();
        Subject subject = new Subject(false,new HashSet(),credentials, new HashSet());        
        loginModules[0].initialize(subject, 
                                    new T24AttributeCallbackHandler((JaasConfiguration)configs[0]), 
                                    sharedState, 
                                    configs[0].getMap());
        loginModules[1].initialize(subject, 
                new NullCallbackHandler(), 
                sharedState, 
                configs[1].getMap());
               
        for (int i = 0; i < loginModules.length; ++i) {
    		try {
    			assertTrue(loginModules[i].login());
    		} catch (LoginException e) {
    			fail(e.getMessage());
    		}
        }
        for (int i = 0; i < loginModules.length; ++i) {
    		try {
    			assertTrue(loginModules[i].commit());
    		} catch (LoginException e) {
    			e.printStackTrace();
    			fail(e.getMessage());			
    		}
        }    
        for (int i = 0; i < loginModules.length; ++i) {
            try {
                assertTrue(loginModules[i].logout());
            } catch (LoginException e) {
                e.printStackTrace();
                fail(e.getMessage());           
            }
        }
	}
}
