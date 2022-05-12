package com.temenos.arc.security.jaas;


// TODO SJP 01/12/2006 Consider making this a RuntimeException; login failures may not show up otherwise
public class LoginModuleException extends RuntimeException {

    public LoginModuleException() {
        super();
    }

    public LoginModuleException(String msg) {
        super(msg);
    }
}
