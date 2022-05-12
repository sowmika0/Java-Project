package com.temenos.t24browser.graph;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Main controlling class for displaying graphs as images.
 */
public class Graph {

	/** A logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(Graph.class);
	
	/** 'graphWorkDirecotry' holds the location of the temporary work directory */
	public static final String GRAPH_WORK_DIRECTORY_PARAM_NAME = "graphWorkDirectory";

	/** '../work/graphImages' default value incase none is found in browserParameters.xml */
	public static final String GRAPH_WORK_DIRECTORY_DEFAULT_VALUE = "./work/graphImages";

    
	/**
	 * Main controlling method
	 * @param xml The responseXml from T24.
	 * @return The responseXml without the parsed information.
	 */
    public String createGraphFile(String xml, HttpSession session) {
    	
    	String filename = "";
    	try {
    	
	    	// Parse the graph xml
	    	GraphParser graphInfo = new GraphParser();
	    	graphInfo.parse(xml);
	    	
	    	// Return with no changes if the xml is not graph xml
	    	if (graphInfo.getGraphType().equals("")) {
	    		return xml;
	    	}
	    	
	        // Open or create the work directory to make sure it's there
	    	ServletContext servletContext = session.getServletContext();
	    	String graphDirectory = getWorkDirectory(servletContext);
	    	File graphDir = new File(graphDirectory);
	        if (!graphDir.isDirectory()) {
	        	LOGGER.info("Graph work directory not found, attempting to create it (" + graphDir.getAbsolutePath() + ")");
	        	if (!graphDir.mkdirs()) {
	        		// Display only the reason of not creating graph and don't display absolute path of the directory.
	        		// Since its already written in log and user should not know the details of absolute path.
	        		throw new GraphException("Unable to create work directory for graphs.");
	        	}
	        }
	
	        // Write the chart to disk
	        String sessionId = session.getId();
	        filename = System.currentTimeMillis() + ".PNG";
	        String filepath = graphDirectory + "/" + sessionId + filename;
	        JFreeChart chart = graphInfo.getChart();
            ChartUtilities.saveChartAsPNG(new File(filepath), chart, graphInfo.getWidth(), graphInfo.getHeight());
	
    	} catch (NumberFormatException e) {
    		xml = handleError(xml, "Unable to parse amount. Please ensure amounts are in an unmasked decimal format (eg. -12345.678). ", e);
    	} catch (Exception e) {
            xml = handleError(xml, "Error creating chart. ", e);
    	}

    	// Get rid of the graph xml since it has already been processed into the image.
        // It's not longer needed and removing it should improve performance of subsequent processing
        // of the xml these other processes will be working on a shorter xml string.
        xml = xml.replaceAll("<GraphEnq>.*?</GraphEnq>", "");
        xml = xml.replaceAll("<showgraph>.*?</showgraph>", "");
        xml = xml.replaceAll("<graph>.*?</graph>", "");
        xml = xml.replaceAll("<showpie>.*?</showpie>", "");
        xml = xml.replaceAll("<pie>.*?</pie>", "");
        
        // If the graph is a multipie chart (not supported) then convert it back to a normal enquiry response
        xml = xml.replaceAll("<styleSheet>/transforms/enquiry/svgEnqResponse.xsl</styleSheet>", "<styleSheet>/transforms/window.xsl</styleSheet>");
        xml = xml.replaceAll("<mpPie>.*?</mpPie>", "");

        // Get rid of the rows of data. Seems like at least one row with data in it is needed by the xslt though.
        xml = xml.replaceAll("</header>\\s*<r>.*?</r>\\s*<footer>", "</header><r><c><cap>1</cap></c><c><cap>1</cap></c></r><footer>");

        // Add a link to the graph image
        xml = xml.replaceAll("</control>", "<graphImage>" + filename + "</graphImage></control>");
        return xml;
    }
    
    private String handleError(String xml, String msg, Exception e) {
    	// Log the error
    	LOGGER.error(msg, e);
    	// Log the graph exception/error with relevant message.
    	LOGGER.debug(msg + e.getMessage());
    	// Add the error message to the xml so the user finds out about it.
    	// Do not display absolute path of the directory with exact error message.
        xml = xml.replaceAll("</control>", "<graphError>" + msg + "</graphError></control>");
    	return xml;
    }

    /**
     * Utility method to get the work directory from the ServletContext and check it.
     * @param servletContext Where the value is stored.
     * @return The location of the work directory, with relative paths resolved.
     */
	public static String getWorkDirectory(ServletContext servletContext) {

		String graphDirectory = (String)servletContext.getAttribute(GRAPH_WORK_DIRECTORY_PARAM_NAME);
		if (graphDirectory == null || graphDirectory.equals("")) {
			LOGGER.error("Graph directory not defined, check browserParameters.xml. Using default value (" + GRAPH_WORK_DIRECTORY_DEFAULT_VALUE + ").");
			graphDirectory = GRAPH_WORK_DIRECTORY_DEFAULT_VALUE;
		}
    	// It might be a relative path, if so then get the actual path
    	if (graphDirectory.startsWith(".")) {
    		graphDirectory = servletContext.getRealPath(graphDirectory);
    	}

		return graphDirectory;
	}
    
}
