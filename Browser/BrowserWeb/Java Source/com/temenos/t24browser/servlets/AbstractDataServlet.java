package com.temenos.t24browser.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

import org.apache.commons.codec.binary.Base64;

/**
 * Abstract class for reading data from a 'location', then streaming it to the user with the given or guessed content type.
 * Includes methods that subclasses of this class may find useful, like base64 decoding.
 * Subclasses of this class are expected to implement the 'readData()' method for each different 'location', for example
 * a FileServlet might read files from the local disk, while an OracleServlet might read the data from Oracle.
 * The following requests parameters are understood by the AbstractDataServlet:<br>
 * <ul>
 * <li>dataLocator: (required) A string that can be used by the implementing class to retrieve the file.</li>
 * <li>contentType: (optional, default='image/png') A content type to apply to the output data.</li>
 * <li>isBase64: (optional, default='false') If 'true' then data will be decoded before being streamed to the user agent.</li>
 * </ul>
 */
public abstract class AbstractDataServlet extends HttpServlet {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataServlet.class);
	
	/**
	 * Just calls doGet()
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Gets the data and streams it to the user.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Get the data
		String dataLocator = getDataLocator(request);
		byte[] data = readData(request,dataLocator);

		// Check whether the data must be decoded from base64 format
		if (isBase64(request)) {
			data = decodeBase64(data);
		}
		
		// Get the content type of the data and display it.
		String contentType = getContentType(request);
		displayBinary(response, data, contentType);
	}

	
	/**
	 * 'Reads' the file data from whatever location it happens to be in.
	 * @param request The http request, incase it's useful to the implementing class (eg for getting the session).
	 * @param dataLocator The location of the data. The dataLocator doesn't have to be an actual location, 
	 * it just enough information for the implementing Servlet to be able to locate the file.
	 * @return The file data
	 */
	public abstract byte[] readData(HttpServletRequest request, String dataLocator);
	
	
	/**
	 * Decodes base64 encoded data.
	 * Note: Use base64 encoding with care, base64 encoding is not standardised.
	 * This implementation relies on the apache.commons.codec defaults, this method
	 * is public so it can be overridden for other implementations.
	 * @param data The data to decode.
	 * @return The decoded data.
	 */
	public byte[] decodeBase64(byte[] data) {
		return Base64.decodeBase64(data);
	}


	/**
	 * Gets the dataLocator parameter from the request, or "" if none is provided.
	 * @param request The request, which must contain a dataLocator.
	 * @return The value of the dataLocator parameter.
	 * @throws ServletException if the request does not contain a 'dataLocator' parameter.
	 */
	private String getDataLocator(HttpServletRequest request) throws ServletException {
		String dataLocator = request.getParameter("dataLocator");
		if (dataLocator == null) {
			throw new ServletException("'dataLocator' not found in request");
		}
		return dataLocator;
	}


	/**
	 * Checks if the request is prescribing a content type in a 'contentType' field, otherwise returns "image/png".
	 * @param request The request.
	 * @return The content type specified in 'contentType' parameter, or "image/png". 
	 */
	private String getContentType(HttpServletRequest request) {
		String contentType = request.getParameter("contentType");
		if (contentType == null || contentType.length() < 1) {
			contentType = "image/png";
		}
		return contentType;
	}
	

	/**
	 * Checks whether the request indicates 'isBase64=true'.
	 * If so, the data returned from readData will be decoded base64.
	 * @param request The request.
	 * @return true if 'isBase64=true' is sent in the request, otherwise false.
	 */
	private boolean isBase64(HttpServletRequest request) {
		String boolParameter = request.getParameter("isBase64");
		if (boolParameter != null && boolParameter.equals("true")) {
			return true;
		}
		return false;
	}


	/**
	 * Streams the given byte[] with the given content type to the requesting user agent.
	 * @param response The response object doing the streaming.
	 * @param output The data to stream
	 * @param contentType The desired content type
	 */
	private void displayBinary(HttpServletResponse response, byte[] output, String contentType) {
		try {
			ServletOutputStream out = response.getOutputStream();
			response.setContentType(contentType);
			out.write(output);
			out.flush();
			out.close();
		} catch (Exception e) {
			// Error occurred, but can't write any response
			LOGGER.error("Error in displayBinary. Exception=" + e.toString());
		}
	}

}
