package com.temenos.arc.security.authenticationserver.server;


/**
 * AuditLog object stores the AuditRecord of the User.
 * AuditLog has got 11 states stores the state of each Auditrecord of the User.
 * For all the States, getter and setter methods are provided.
 * AuditLog Object will be set in UserManagement.
 * AuditLog Object will be accessed by XmlUserManagement. 
 */

public class AuditLog {
	
    private String timestamp;
    private String message;
    private String action;
    private String parameter;
    private String target_user;
    private String status;
    private boolean tainted;
    private String response;
    private String channel;
    
    public AuditLog(){
	
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTarget_user() {
		return target_user;
	}

	public void setTarget_user(String target_user) {
		this.target_user = target_user;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isTainted() {
		return tainted;
	}

	public void setTainted(boolean tainted) {
		this.tainted = tainted;
	}
	/**
	 * @Override toString() method
	 * @return all the states of the AuditLog Object. 
	 */
	public String toString(){
		return this.action+" "+this.channel+" "+this.message+" "+this.parameter+" "+this.response+"  "+this.status+"  "+this.target_user+" "+this.timestamp+"  "+this.tainted;
	}
}
