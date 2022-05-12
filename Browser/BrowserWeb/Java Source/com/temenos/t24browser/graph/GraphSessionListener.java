package com.temenos.t24browser.graph;

import java.io.File;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;

/**
 * Cleans up any left over Graph image files every time a session ends.
 */
public class GraphSessionListener implements HttpSessionListener {

	/**
	 * Empty implementation, this method not required.
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		// do nothing
	}

	/**
	 * Deletes used graph image files from the web server when a users session expires.
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event) {

		// Get the ID of the session
		HttpSession session = event.getSession();
		String sessionId = session.getId();
		
		// Get the directory the graph images are in from the servlet context
		File graphDir = new File(Graph.getWorkDirectory(session.getServletContext()));
		
		// Loop through the files in the directory and delete the eligible ones.
		File[] filesToDelete = graphDir.listFiles();
		for (File file : filesToDelete) {
			if (shouldDelete(file,sessionId)) {
				file.delete();
			}
		}
	}
	
	/**
	 * Returns true if the file is eligible for deletion.
	 * Files are eligible if:<ul>
	 * <li>They belong to the user (are prefixed with the uses sessionId) since their session is ending, or</li>
	 * <li>They are more than 10 minutes old.</li></ul>
	 * @param file The file to be considered.
	 * @param sessionId The session of the user, used in the file prefix.
	 * @return True if and only if the file is eligible, false otherwise.
	 */
	private boolean shouldDelete(File file, String sessionId) {
		
		// Don't delete the readme file
		if (file.getName().equals("readme.txt")) {
			return false;
		}

		// If the file belongs to the user, then delete it because their session is ending.
		if (file.getName().startsWith(sessionId)) {
			return true;
		}
		
		// If the file is more than 10 minutes old, regardless of who it belongs to then delete it.
		if ((System.currentTimeMillis() - file.lastModified()) > 600000L) {
			return true;
		}
		
		// File not eligible.
		return false;
	}

}
