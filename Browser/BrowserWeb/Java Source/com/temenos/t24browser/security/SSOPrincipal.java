package com.temenos.t24browser.security;

import java.io.Serializable;
import java.security.Principal;

// TODO: Auto-generated Javadoc
/**
 * The Class SSOPrincipal.
 */
public class SSOPrincipal implements Principal, Serializable {

	/** The iv principal. */
	private Principal ivPrincipal;
	
	/**
	 * Instantiates a new SSO principal.
	 */
	public SSOPrincipal()
	{
	}
	
	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Sets the Single-SignOn Principal Object.
	 * 
	 * @param pricipal the pricipal
	 */
	public void setSSOPrincipal( Principal pricipal )
	{
		ivPrincipal = pricipal;
	}
	
	/**
	 * Get the Single-SignOn Principal Object.
	 * 
	 * @return Principal
	 */
	public Principal getSSOPrincipal()
	{
		return ivPrincipal;
	}

}
