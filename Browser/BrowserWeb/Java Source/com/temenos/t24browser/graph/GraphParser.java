package com.temenos.t24browser.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.TableOrder;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.Utils;

/**
 * Main graph class that parses the information in the xml and calls the jFreeChart classes to build a chart.
 */
public class GraphParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphParser.class);
	private int height = 500;
	private int width = 500;
	private double scaleY = 0.0;
	private String title = "";
	private String graphType = "";
	private String enqId = "";
	private JFreeChart chart = null;
	private int xColumn = 0;
	private int yColumn = 1;
	private int zColumn = 0;
	private boolean isLegend = false;
	
	// Getter methods
	public int getHeight() {return height;}
	public int getWidth() {return width;}
	public String getTitle() {return title;}
	public String getGraphType() {return graphType;}
	public String getEnqId() {return enqId;}
	public JFreeChart getChart() {return chart;}

	/**
	 * Main controlling method for the GraphParser. Creates a JFreeChart.
	 * @param xml The entire xml response from T24.
	 * @throws GraphException if the graph type is not recognised.
	 */
	void parse(String xml) throws GraphException {

    	String graphEnqXml = Utils.getNodeFromString(xml, "GraphEnq");
    	ArrayList<GraphSeries> datasetList = new ArrayList<GraphSeries>();

    	// Older type graphs don't have <GraphEnq>, so check for <pie> or <graph> tags.
    	if (graphEnqXml.equals("")) {
    		// Might be an older pie chart
    		if (xml.indexOf("<pie>") > -1) {
    			graphType = "pie";
				GraphSeries graphSeries = parseBasicPieXml(xml);
				datasetList.add(graphSeries);
    		} else if (xml.indexOf("<graph>") > -1) {
    			graphType = "line";
    			datasetList = parseBasicLineXml(xml); // Reassign, don't add, because xml syntax is a bit different.
    		} else if (xml.indexOf("mpShowPie") > -1) {
    			throw new GraphException("Multipie charts are only supported with the SVG chart option enabled. Please switch to this setting to display the chart.");
    		} else {
    			return; // Means the xml isn't a graph, so nothing to do.
    		}
    		
    	} else {
        	// More advanced type of graph, has <GraphEnq>.
        	parseGraphEnqXml(graphEnqXml);

        	String inputGraphType = Utils.getNodeFromString(graphEnqXml, "GraphType");
			if (inputGraphType.equals("PIE.CHART")) {
				graphType = "pie";
				GraphSeries graphSeries = parseEbEnquiryGraphRowsXml(xml,yColumn,xColumn);
				if (graphSeries.containsNegativeValues()) {
					graphType = "bar";
				}
				datasetList.add(graphSeries);
			} else if (inputGraphType.equals("BAR.ORDINAL")) {
				graphType = "bar";
				GraphSeries graphSeries = parseEbEnquiryGraphRowsXml(xml,xColumn,yColumn);
				datasetList.add(graphSeries);
			} else if (inputGraphType.equals("LINE.ORDINAL")) {
				graphType = "line";
				GraphSeries graphSeries = parseEbEnquiryGraphRowsXml(xml,xColumn,yColumn);
				datasetList.add(graphSeries);
			} else if (inputGraphType.equals("BAR.LINE.ORDINAL")) {
				graphType = "barLine";
				GraphSeries lineSeries = parseEbEnquiryGraphRowsXml(xml,zColumn,xColumn);
				GraphSeries barSeries = parseEbEnquiryGraphRowsXml(xml,zColumn,yColumn);
				datasetList.add(lineSeries);
				datasetList.add(barSeries);
			} else {
				throw new GraphException("Chart type not recognised: " + graphType);
			}
    	}
    	
		enqId = Utils.getNodeFromString(xml, "enqId");
    	chart = generateChart(datasetList);

		// Apply the Temenos look and feel to the graph
		ChartTheme ct = new T24ChartTheme();
		ct.apply(chart);
	}
	
	/**
	 * Calls the right chart generator
	 * @param datasetList
	 * @throws GraphException If the graphType is not recognised.
	 */
	private JFreeChart generateChart(ArrayList<GraphSeries> datasetList) throws GraphException {
		JFreeChart jfChart = null;
		if (graphType.equals("pie")) {
			jfChart = generatePieChart(datasetList);
		} else if (graphType.equals("line")) {
			jfChart = generateLineChart(datasetList);
		} else if (graphType.equals("bar")) {
			jfChart = generateBarChart(datasetList);
		} else if (graphType.equals("barLine")) {
			jfChart = generateBarLineChart(datasetList);
		} else {
			throw new GraphException("Chart type not recognised: " + graphType);
		}
		return jfChart;
	}

	/**
	 * Generates a chart with a bar and a line graph in it. 
	 * @param datasetList Expects exactly two GraphSeries in the list.
	 * @return A new jFreeChart
	 */
	private JFreeChart generateBarLineChart(ArrayList<GraphSeries> datasetList) {

		// Make the barRenderer
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		barDataset = addSeriesToCategoryDataset(datasetList.get(1), barDataset);
		BarRenderer barRenderer = new BarRenderer();

		// Make the lineRenderer using a separate dataset
		DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
		lineDataset = addSeriesToCategoryDataset(datasetList.get(0), lineDataset);
		LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
		
		// Add both renderers to the plot.
		CategoryAxis categoryAxis = getCategoryAxis();
		ValueAxis rangeAxis = getNumberAxis("", this.scaleY);
		CategoryPlot plot = new CategoryPlot(barDataset, categoryAxis, rangeAxis, barRenderer);
		plot.setDataset(1,lineDataset);
		plot.setRenderer(1,lineRenderer);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		// Make the chart
		JFreeChart barLineChart = new JFreeChart(this.title, JFreeChart.DEFAULT_TITLE_FONT, plot, isLegend);
        //to set border for chart
		barLineChart.setBorderVisible(true);
		barLineChart.setBorderPaint(Color.LIGHT_GRAY);	 
		return barLineChart;
	}
	
	/**
	 * Makes a bar chart, multiple series can be displayed on one chart.
	 * @param datasetList A list of GraphSeries to display on the chart.
	 * @return A new jFreeChart
	 */
	private JFreeChart generateBarChart(ArrayList<GraphSeries> datasetList) {
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
		for (GraphSeries gs:datasetList) {
			barDataset = addSeriesToCategoryDataset(gs, barDataset);
		}
		CategoryAxis categoryAxis = getCategoryAxis();
		ValueAxis rangeAxis = getNumberAxis("", this.scaleY);
		BarRenderer barRenderer = new BarRenderer();
		CategoryPlot plot = new CategoryPlot(barDataset, categoryAxis, rangeAxis, barRenderer);

		// Make the chart
		JFreeChart barChart = new JFreeChart(this.title, JFreeChart.DEFAULT_TITLE_FONT, plot, isLegend);
        //to set border for chart
		barChart.setBorderVisible(true);
		barChart.setBorderPaint(Color.LIGHT_GRAY);	 
		return barChart;
	}
	
	/**
	 * Makes a line chart, multiple series can be displayed on one chart.
	 * @param datasetList A list of GraphSeries to display on the chart.
	 * @return A new jFreeChart
	 */
	private JFreeChart generateLineChart(ArrayList<GraphSeries> datasetList) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (GraphSeries gs:datasetList) {
			dataset = addSeriesToCategoryDataset(gs, dataset);
		}
		CategoryAxis categoryAxis = getCategoryAxis();
		ValueAxis rangeAxis = getNumberAxis("", this.scaleY);
		LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
		CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, rangeAxis, lineRenderer);
		
		//Make the chart
		JFreeChart lineChart = new JFreeChart(this.title, JFreeChart.DEFAULT_TITLE_FONT, plot, isLegend);
        //to set border for chart
		lineChart.setBorderVisible(true);
		lineChart.setBorderPaint(Color.LIGHT_GRAY);	 
		return lineChart;
	}

	/**
	 * Makes a pie chart.
	 * @param datasetList A list of GraphSeries to display on the chart, though only the first will be displayed.
	 * @return A new jFreeChart
	 */
	@SuppressWarnings("deprecation")
	private JFreeChart generatePieChart(ArrayList<GraphSeries> datasetList) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset = addSeriesToCategoryDataset(datasetList.get(0), dataset);
		PieDataset pieDataset = new CategoryToPieDataset(dataset, TableOrder.BY_ROW, 0);
		JFreeChart chart = ChartFactory.createPieChart(this.title,pieDataset,isLegend,false,false);
		// Get rid of the tool tips.
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelGenerator(null);
		
		// Modify the legend so all items are listed vertically not as a ticker tape		
		//to set border for chart
		chart.setBorderVisible(true);
		chart.setBorderPaint(Color.LIGHT_GRAY);	
		TextTitle chartTitle = chart.getTitle();
		if(chartTitle!=null)
		{
			chartTitle.setPadding(20, 0, 0, 0);
		}		
		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setBorder(BlockBorder.NONE);
	 		legend.setPadding(0, 40, 20, 40);
			legend.setPosition(RectangleEdge.BOTTOM);
			legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
			legend.setVerticalAlignment(VerticalAlignment.BOTTOM);					
		}		
		return chart;
	}
	
	/**
	 * Translates the values in a graphSeries to a new series in a CategoryDataset.
	 * @param graphSeries The series of points to add.
	 * @param dataset The dataset to modify.
	 * @return The modified dataset.
	 */
	private DefaultCategoryDataset addSeriesToCategoryDataset(GraphSeries graphSeries, DefaultCategoryDataset dataset) {
		String legend = graphSeries.getLegend();
		for (int i = 0; i<graphSeries.getSize(); i++) {
			String category = graphSeries.getDomainValue(i);
			double value = graphSeries.getRangeValue(i);
			dataset.setValue(value, legend, category);
		}
		return dataset;
	}
	
	
	/**
	 * Generates a new CategoryAxis with vertical category labels. 
	 * @return A new CategoryAxis with vertical category labels.
	 */
	private CategoryAxis getCategoryAxis() {
		CategoryAxis categoryAxis = new CategoryAxis("");
		categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		return categoryAxis;
	}
	
	
	/**
	 * Generates a new NumberAxis with the range specified in scaleX, if greater than zero.
	 * EB.ENQUIRY.GRAPH only supports upper ranges, so assume lower range is zero.
	 * @param label The label to apply to the axis.
	 * @param axisScale Use 0.0 for graphs that scale to the largest sized element, or specify an absolute limit.
	 * @return The new axis.
	 */
	private ValueAxis getNumberAxis(String label, double axisScale) {
		ValueAxis axis = new NumberAxis(label);
		if (axisScale == 0.0) {
			// Most common case. Do nothing, axisScale of zero means none was provided.
		} else if (axisScale > 0.0) {
			axis.setRange(0.0, axisScale);
		} else {
			axis.setRange(axisScale,0.0);
		}
		return axis;
	}
	
	
	/**
	 * Parses the 'GraphEnq' xml and sets values like height and width of the graph.
	 * @param graphEnqXml The xml to parse.
	 */
	private void parseGraphEnqXml(String graphEnqXml) {
		height = (int)getDoubleValue(graphEnqXml, "graphHeight");
		width = (int)getDoubleValue(graphEnqXml, "graphWidth");
		scaleY = getDoubleValue(graphEnqXml, "scaleY");
		title = Utils.getXpathFromString(graphEnqXml, "labels/cap");
		
		// Parse the DataItems to work out which columns we must use for the different axes
		xColumn = 0; // Default value
		yColumn = 1; // Default value
		zColumn = 0; // Default value
		List dataItemList = Utils.getAllMatchingNodes(graphEnqXml, "DataItem");
		Iterator dataItemIterator = dataItemList.iterator();
		while (dataItemIterator.hasNext()) {
			String dataItemXml = (String)dataItemIterator.next();
			String variableName = Utils.getNodeFromString(dataItemXml,"variableName");
			int colNum = (int)getDoubleValue(dataItemXml,"colNum");
			if (variableName.equals("X")) {
				xColumn = colNum;
			} else if (variableName.equals("Y")) {
				yColumn = colNum;
			} else if (variableName.equals("Z")) {
				zColumn = colNum;
			}
		}
	}

	/**
	 * Parses the 'pie' xml node to get the data for the chart.
	 * @param xml The entire xml string containing a 'pie' node.
	 * @return A dataset to be used for building either bar or line charts, or by conversion pie charts.
	 */
	private GraphSeries parseBasicPieXml(String xml) {
		GraphSeries dataset = new GraphSeries(""); // Pie graphs don't give a legend.
		// legend flag enabled to display legend in pie chart
		isLegend = true;
		// title is displayed for pie chart generated. 
		String titlename = Utils.getXpathFromString(xml, "enqResponse/header/cap");
		title = titlename.replace("&#160;"," ");   
		String pieData = Utils.getNodeFromString(xml, "pie");
    	List rows = Utils.getAllMatchingNodes(pieData, "slice");
        Iterator rowIterator = rows.iterator();
        while (rowIterator.hasNext()) {
        	String rowXml = (String)rowIterator.next();
        	String category = Utils.getNodeFromString(rowXml,"cap");
        	category = Utils.decodeHtmlEntities(category);
        	double value = getDoubleValue(rowXml,"val");
    		dataset.addPoint(category, value);
        }
        return dataset;
	}

	/**
	 * Parses the 'graph' xml node to get the data for the chart, which can contain multiple 'series'.
	 * @param xml The entire xml string containing a 'pie' node.
	 * @return A dataset to be used for building either bar or line charts, or by conversion pie charts.
	 */
	private ArrayList<GraphSeries> parseBasicLineXml(String xml) {
		ArrayList<GraphSeries> datasetList = new ArrayList<GraphSeries>();
		Pattern seriesPattern = Pattern.compile("<series no=\"(.+?)\">(.+?)</series>");
		Pattern pointPattern = Pattern.compile("<pt x=\"(.+?)\" y=\"(.+?)\"/>");

		// Get the xml for each of the series, inside <series no="1"> tags.
		String graphXml = Utils.getNodeFromString(xml, "graph");
		Matcher seriesMatcher = seriesPattern.matcher(graphXml);
		boolean seriesFlag = false;
		while (seriesFlag = seriesMatcher.find()) {
			String legend = seriesMatcher.group(1);
			String seriesXml = seriesMatcher.group(2);
			GraphSeries dataset = new GraphSeries(legend);

			// Get the xml for each of the data points, inside <pt x="123" y="234"/> tags.
			Matcher pointMatcher = pointPattern.matcher(seriesXml);
			boolean pointFlag = false;
			while (pointFlag = pointMatcher.find()) {
	       		String category = pointMatcher.group(1);
	        	category = Utils.decodeHtmlEntities(category);
				double value = Double.parseDouble(pointMatcher.group(2));
	    		dataset.addPoint(category, value);
			}
			datasetList.add(dataset);
		}
		return datasetList;
	}
	
	/**
	 * Parses the multiple 'r' xml nodes to get the data for the chart, which can contain multiple columns.
	 * @param xml The entire xml string containing a 'r' nodes.
	 * @param x which column to use for the domain Axis
	 * @param y which column to use for the range Axis
	 * @return A dataset to be used for building either bar or line charts, or by conversion pie charts.
	 */
	private GraphSeries parseEbEnquiryGraphRowsXml(String xml, int x, int y) {
		// Get the legend
		String legend = "";
		String legendColumn = Utils.getXpathFromString(xml, "legend/attribute");
		if (!legendColumn.equals("")) {
			String colsXml = Utils.getNodeFromString(xml, "cols");
			ArrayList<String> columnHeadingList = Utils.getAllMatchingNodes(colsXml, "c");
			if (columnHeadingList.size() > y) {
				isLegend = true;
				legend = columnHeadingList.get(y);
	        	legend = Utils.decodeHtmlEntities(legend);
			} else if (graphType.equals("pie")) {
				// pie chart legends are different to the other chart types.
				// Pie charts actually use the ordinal series for the legend rather than a group descriptor.
				// This check sets isLegend=true if <graphEnq> tag contains a <legend> element.
				isLegend = true;
			} else {
				LOGGER.warn("Unable to get legend.");
			}
		}

        // Create the dataset out of the data
		GraphSeries dataset = new GraphSeries(legend);
    	List rows = Utils.getAllMatchingNodes(xml, "r");
        Iterator rowIterator = rows.iterator();
        while (rowIterator.hasNext()) {
        	String rowXml = (String)rowIterator.next();
        	// Skip header rows. They shouldn't be there, but they might have been left in by mistake.
        	if (rowXml.indexOf("<ishead>Y</ishead>") > -1) {
        		continue;
        	}
        	ArrayList cells = Utils.getAllMatchingNodes(rowXml, "c");
       		String category = Utils.getNodeFromString((String)cells.get(x),"cap");
        	category = Utils.decodeHtmlEntities(category);
       		double value = getDoubleValue((String)cells.get(y),"cap");
    		dataset.addPoint(category, value);
        }
        return dataset;
	}
	
	/**
	 * Returns the numeric value of a node in the given xml. Returns '0.0' if the node does not exist or is empty.
	 * Throws NumberFormatException (unchecked) if the value is not in the format '12345.678'. This is strict, but
	 * required because while most numbers could be parsed, something like '123,456' could be 123456 (JPY), or 123.456 (EUR).
	 * @param xml The xml containing the node and value.
	 * @param node The node to search for.
	 * @return Returns the numeric value of a node in the given xml.
	 */
	private double getDoubleValue(String xml, String node) {
		Double out = 0.0;
		String value = Utils.getNodeFromString(xml, node);
		if (value.length() > 0) {
			out = Double.parseDouble(value);
		}
		return out;
	}
	
}
