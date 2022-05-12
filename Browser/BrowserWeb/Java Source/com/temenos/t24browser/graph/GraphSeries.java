package com.temenos.t24browser.graph;

import java.util.ArrayList;

/**
 * Class that stores a series of datapoints.<br>
 * Frustratingly the jFreeChart pieDataset and categoryDataset do not inherit from a common 
 * superclass / interface. This class has been created to enable dataset type information from 
 * T24 to be handled the same way regardless of whether the output will eventually be a pie or bar graph. 
 */
public class GraphSeries {

	/** The lable of the series, for display in a legend */
	private String legend;
	
	/** The list of domain values for the series */
	private ArrayList<String> domainValues;
	
	/** The list of range values for the series */
	private ArrayList<Double> rangeValues;

	/** The number of elements in the series */
	private int size;

	/** Whether there are negative values in the dataset */
 	private boolean containsNegativeValues;

	/**
	 * Creates a new graph series with the given legend.
	 * @param legend
	 */
	public GraphSeries(String legend) {
		this.legend = legend;
		this.size = 0;
		domainValues = new ArrayList<String>();
		rangeValues = new ArrayList<Double>();
		containsNegativeValues = false;
	}
	
	/**
	 * Gets the number of data points
	 * @return The number of data points
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Gets the legend for the series
	 * @return The legend label.
	 */
	public String getLegend() {
		return this.legend;
	}
	
	/**
	 * Adds a new point to the series.
	 * @param category The category the datapoint should be added to.
	 * @param value The value of the datapoint.
	 */
	public void addPoint(String category, double value) {
		domainValues.add(category);
		rangeValues.add(value); // Autoboxing
		size++;
		if (value < 0) {
 			containsNegativeValues = true;
 		}
	}
	
	/**
	 * Gets the domain value of the datapoint (the 'category' for categorical graphs).
	 * @param index The index to the datapoint.
	 * @return The category.
	 */
	public String getDomainValue(int index) {
		return domainValues.get(index);
	}

	/**
	 * Gets the value of the given datapoint.
	 * @param index The index to the datapoint.
	 * @return The value.
	 */
	public double getRangeValue(int index) {
		return rangeValues.get(index); // Autounboxing
	}

	/**
 	* Returns true if the dataset contains a negative value.
 	* @return true if the dataset contains at least 1 negative value, false otherwise.
 	*/
 	public boolean containsNegativeValues() {
 		return containsNegativeValues;
 	}
}
