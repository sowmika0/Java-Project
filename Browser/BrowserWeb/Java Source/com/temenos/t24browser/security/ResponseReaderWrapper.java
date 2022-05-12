package com.temenos.t24browser.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
/**
 * Helps to wrap the response object which enables reading the content of the response object
 *
 */
public class ResponseReaderWrapper extends HttpServletResponseWrapper {
	 
	  private static final int BUFFER_SIZE = 500;
	  private StringWriter sw = new StringWriter(BUFFER_SIZE);

	  private boolean flushReq = true;
	  
	  public ResponseReaderWrapper(HttpServletResponse response) {
	    super(response);
	  }

	  public ResponseReaderWrapper(HttpServletResponse response, boolean flushReq) {
		  this(response);
		  this.flushReq=flushReq;
	  }	 
	  
	  public PrintWriter getWriter() throws IOException {
	    return new PrintWriter(sw);
	  }

	  /* Included in order to Work in WAS 8.5 */
	  @Override
	  public void flushBuffer() throws IOException	  {
		  if(flushReq){
			  super.flushBuffer();
		  }
	  }
	  
	  public String toString() {
	    return sw.toString();
	  }
}
