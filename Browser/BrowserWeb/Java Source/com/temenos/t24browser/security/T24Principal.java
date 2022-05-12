package com.temenos.t24browser.security;

import java.io.Serializable;
import java.security.Principal;

//T24Principal for TicketAuthenticationFilter
public class T24Principal implements Principal, Serializable{
	
	private String name;
	
	public T24Principal() {
        this("");
    }

    public T24Principal(String name) {
        super();
        this.name = name;
    }
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
}
