package com.temenos.t24browser.utils;

// TODO: Auto-generated Javadoc
/**
 * This class if used to hold the timings throughout the lifetime of the request such
 * that we can tell where the time is being spent.
 */
public class RequestTimer {
	
	/** The iv ofs. */
	private long ivOfs; // Time spent in Ofs
	
	/** The iv transport. */
	private long ivTransport; // Time spent in the connector
	
	/** The iv start time. */
	private long ivStartTime; // The start time of the request
	
	/** The iv parser time. */
	private long ivParserTime; //The time taken in the XML parser
    
    /** The transform time. */
    private long transformTime; //The time taken by the XML transform
	
	/** The TIM e_ TAG. */
	private static String TIME_TAG = "<time>";
	
	/** The TIM e_ TA g_ c. */
	private static String TIME_TAG_C = "</time>";
	
	/** The SEP. */
	private static String SEP = "-";
	
	/**
	 * Sets the ofs.
	 * 
	 * @param elapsed the new ofs
	 */
	public void setOfs(long elapsed){
		ivOfs = elapsed;
	}
	
	/**
	 * Sets the transport.
	 * 
	 * @param elapsed the new transport
	 */
	public void setTransport(long elapsed){
		ivTransport = elapsed;
	}
	
	/**
	 * Sets the start time.
	 * 
	 * @param startTime the new start time
	 */
	public void setStartTime(long startTime){
		ivStartTime = startTime;
	}

    /**
     * Sets the transform time.
     * 
     * @param transformTime the new transform time
     */
    public void setTransformTime(long transformTime){
        this.transformTime = transformTime;
    }
	
	/**
	 * The time taken to pass the http request through the
	 * xml parser.
	 * 
	 * @param time the time
	 */
	public void setParserTime(long time){
		ivParserTime = time;	
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		// A simple string to show the timing details
		long elapsed = System.currentTimeMillis() - ivStartTime;
		long timeInTransport = ivTransport - ivOfs;
		long timeInServlet = elapsed - ivTransport;
		String myReturn = elapsed + SEP + timeInServlet + SEP + timeInTransport + SEP + ivOfs + SEP + ivParserTime;
		
		return myReturn;
	}

	/**
	 * To XML.
	 * 
	 * @return the string
	 */
	public String toXML(){
		// The XML that we will add to the response to show the user

		String myReturn = TIME_TAG + toString() + TIME_TAG_C;
		return myReturn;

	}
    
    /**
     * To long string.
     * 
     * @return the string
     */
    public String toLongString(){
        // A simple string to show the timing details
        long elapsed = System.currentTimeMillis() - ivStartTime;
        long timeInTransport = ivTransport - ivOfs;
        long timeInServlet = elapsed - ivTransport;
        String myReturn = "\n" + "\n" +
                          "  Total: " + elapsed + "\n" +
                          "    Time in Servlet: " + timeInServlet + "\n" +
                          "      XML Parse time: " + ivParserTime + "\n" +
                          "      XML Transform time: " + transformTime + "\n" +
                          "    Time in Connector: " + timeInTransport  + "\n" +
                          "      Time in OFS: " + ivOfs  + "\n" +
                          "\n" + "\n";
        return myReturn;
    }
}
