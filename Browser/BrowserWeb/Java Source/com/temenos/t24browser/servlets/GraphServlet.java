package com.temenos.t24browser.servlets;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.graph.Graph;

/**
 * Reads graph images form the web server in a a secure manner.
 */
public class GraphServlet extends AbstractDataServlet {
	
	/** Because servlet implements serialisable */
	public static final long serialVersionUID = 1;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphServlet.class);

	@Override
	/**
	 * Reads the file into a byte array, then deletes it from disk.
	 */
	public byte[] readData(HttpServletRequest request, String dataLocator) {

		// Validate the dataLocator - it must be a 13 digit number and '.PNG'
		if (!dataLocator.matches("\\d{13}\\.PNG")) {
			LOGGER.error("Invalid dataLocator detected: " + dataLocator);
			return new byte[0];
		}
		
		// Add the servlet session ID to the fileName. This will stop users requesting files that don't belong to them.
		HttpSession session = request.getSession();
		dataLocator = session.getId() + dataLocator;
		
		// File will be in the directory specified in browserParameters.xml and prefixed with the users sessionId.
		String graphDirectory = Graph.getWorkDirectory(getServletContext());
		dataLocator = graphDirectory + "/" + dataLocator;
		
		// Read the file into a byte array.
		byte[] data = null;
		File f = new File(dataLocator);
		try {
			int fileSize = (int)f.length();
			data = new byte[fileSize];
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			in.readFully(data);

			// Delete the file, it's only needed once.
			in.close(); // Must close the stream, or file won't be deleted.
			f.delete();
		} catch (Exception e) {
			LOGGER.error("Error displaying Graph, from " + f.getAbsolutePath() + ". Exception is\n" + e.getMessage());
		}
		
		// Finished, return the data.
		return data;
	}
	
}
