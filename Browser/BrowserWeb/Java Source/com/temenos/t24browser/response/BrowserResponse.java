package com.temenos.t24browser.response;

import com.temenos.t24browser.security.SecurityUtils;
import com.temenos.t24browser.utils.PropertyManager;
// TODO: Auto-generated Javadoc

/**
 * Simple class to hold the status of a response from the server
 * such that we can determine if the message got through or hit a
 * technical error. Also stores the time spent in OFS - if this info is there!
 */
public class BrowserResponse {
	
	/** The iv msg. */
	private String ivMsg = "";
	
	/** The iv error. */
	private String ivError = "";
	
	/** The iv ofs. */
	private long ivOfs = 0; // Time in ms spent in OFS
	
	/** The iv parameters. */
	private PropertyManager ivParameters;
	
	/**
	 * Instantiates a new browser response.
	 * 
	 * @param ivParameters the iv parameters
	 */
	public BrowserResponse(PropertyManager ivParameters) {
		this.ivParameters = ivParameters;
	}
	
	/**
	 * Gets the msg.
	 * 
	 * @return the msg
	 */
	public String getMsg() {
		return ivMsg;
	}
	
	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public String getError() {
		return ivError;
	}

	/**
	 * Sets the msg.
	 * 
	 * @param myMsg the new msg
	 */
	public void setMsg(String myMsg) {
        // Strip out any 'evil' HTML tags from the response, as a crude first defence against XSS.
        // todo: Validate the XML with a Schema, for better XSS protection
        // todo: get the scripts tag removed on the server
        ivMsg = SecurityUtils.replaceEvilHTMLtags(myMsg, ivParameters);
	}
	
	/**
	 * Sets the error.
	 * 
	 * @param myError the new error
	 */
	public void setError(String myError) {
		ivError = myError;
	}
	
	/**
	 * Checks if is valid.
	 * 
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return (ivError.equalsIgnoreCase(""));
	}

	/**
	 * Sets the ofs time.
	 * 
	 * @param elapsed the new ofs time
	 */
	public void setOfsTime(long elapsed){
		ivOfs = elapsed;	
	}
	
	/**
	 * Gets the ofs time.
	 * 
	 * @return the ofs time
	 */
	public long getOfsTime(){
		return ivOfs;
	}
}
