package com.temenos.t24browser.graph;

/**
 * Basic exception for graphs
 */
public class GraphException extends Exception {
	
	/** Because servlet implements serialisable */
	public static final long serialVersionUID = 1;

	public GraphException() {
	}

	public GraphException(String arg0) {
		super(arg0);
	}

	public GraphException(Throwable arg0) {
		super(arg0);
	}

	public GraphException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
