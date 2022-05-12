package com.temenos.t24browser.xslt;
/*
 * xt.java (c) 2001 Sarvega Inc.
 * Test driver for xt
 *
 * $Header: /home/cvs/Sarvega/Benchmark/drivers/xt/xt.java,v 1.1.1.1 2002/03/22 16:44:36 Administrator Exp $
 */

import java.io.OutputStream;

import com.jclark.xsl.sax.OutputStreamDestination;

// TODO: Auto-generated Javadoc
/**
 * Custom OutputStream for XT that fixes a known bug
 * in setting the encoding.
 */
class XTOutputStream extends OutputStreamDestination {

	/**
	 * Instantiates a new XT output stream.
	 * 
	 * @param outputStream the output stream
	 */
	public XTOutputStream(OutputStream outputStream) {
		super(outputStream);
	}

	/* (non-Javadoc)
	 * @see com.jclark.xsl.sax.OutputStreamDestination#getOutputStream(java.lang.String, java.lang.String)
	 */
	public OutputStream getOutputStream(String contentType, String encoding) {
		super.setEncoding(encoding);
		return super.getOutputStream(contentType,encoding);
	}
}

