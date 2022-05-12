package com.temenos.arc.security.jaas;

import javax.security.auth.spi.LoginModule;

import com.temenos.arc.security.authenticationserver.common.ArcSession;

public interface ArcLoginModuleInterface extends LoginModule {

    
    public boolean refresh(ArcSession arcSession) throws LoginModuleException;

    public boolean isSessionOwner() ;
    
}
