// This js file is used to store functions that hold the raw data in coming.
// Uses the meta data defined in the xml to pick out the requiered infromation 
// from the raw data. The meta data is defined in xml under the 'Graph' tag. 
// It defines which columns of the enquiry are to be used to populate the DataItem definition. 
// Finally creates the single '_dataSet' object which holds all the enquiry data.
//------------------------------------------------------------------------------------------------------------

//-------------------- GLOBAL VARIABLES ----------------------------------------------------------------------

// This array holds the original rows passed by enquiry.
var _originalRows = "";
// This variable holds the number of DataSeries's.
var _numOfDataSeries = 1;
// Holds the svgDocument root.
var _svgDocument = null;
// Pointer to DataItem function created dynamically by the xsl.
var DataItem_GLOBAL = null;

// Holds the graph coordinates for area plotting.
var _graphCoordinates = {
	xBottomWidth: 0,
	xBottomHeight: 0,
	xBottomPosX: 0,
	xBottomPosY: 0,
	yLeftWidth: 0,
	yLeftHeight: 0,
	yLeftPosX: 0,
	yLeftPosY: 0
};
// Holds the enumeration for axis type.
var _axisType = {
    Linear : 1,
    Ordinal : 2,
    Date : 3
};
// Holds the enumeration for axis orientation.
var _axisOrientation = {
    horisontalTop : 1,
    horisontalBottom : 2,
    verticalLeft : 3,
    verticalRight : 4,
    middle : 5
};
// Holds array of series colours.
var _seriesColors = new Array( "#3399CC", "#FF9966", "#C2A00E", "#71A214", 
                               "#CC99FF", "#FF9999", "#00CC99", "#C2CC0E", 
                               "#71CC14", "#CC68CC", "#66FFCC" ,"#CCCC99",
							   "#33FF99", "#FF00FF", "#CCFF33", "#FFFF99",
							   "#FFCCCC", "#FFCC33", "#FF99CC", "#CC9999",
							   "#FFCC66", "#CCFFCC", "#9999CC", "#99CCCC",
							   "#FF3333");
// Declare references to the static 'g' tags in the svgObject.svg file.
var _titleArea = null;
var _yAxisArea = null;
var _xAxisArea = null;
var _graphArea = null;
var _legendArea = null;

//-------------------- ROUTINE FUNCTIONS ---------------------------------------------------------------------

// Adds a row of data from enquiry response.
function addOriginalRow(row)
{
    _originalRows[_originalRows.length] = row;
}

// Initialise function called as part of initEnquiry.
function initData()
{

    // Do nothin if there is no data.
    if( _originalRows.length == 1 && _originalRows[0] == '' )
    {
        alert( "main.js: No data returned from enquiry" );
        return;
    }

    // Get a reference to the static 'g' tags in the svgObject.svg file.
    _titleArea = _svgDocument.getElementById("titleArea");
    _yAxisArea = _svgDocument.getElementById("yAxisArea");
    _xAxisArea = _svgDocument.getElementById("xAxisArea");
    _graphArea = _svgDocument.getElementById("graphArea");
    _legendArea = _svgDocument.getElementById("legendArea");

    // Calculate graph coordinates
    _graphCoordinates.yLeftPosX = _leftMargin;
    _graphCoordinates.yLeftPosY = _topMargin;
    _graphCoordinates.yLeftHeight = _height - _topMargin - _bottomMargin;
    _graphCoordinates.yLeftWidth = 0;

    _graphCoordinates.xBottomPosX = _leftMargin;
    _graphCoordinates.xBottomPosY = _height - _bottomMargin;
    _graphCoordinates.xBottomHeight = 0;
    _graphCoordinates.xBottomWidth = _width - _leftMargin - _rightMargin;


    // Populate the data set with the enquiry data.
    populateDataSet();
    // Create a legend for this graph if specified.
    createLegend();
    // Display any user defined labels
    displayLabels();
    
}
// displays labels defined in EB.ENQUIRY.GRAPH record.
function displayLabels()
{
	var aLabel = null;
	var textNode = null;
   	var legLabel = null;
   	
	for( var i = 0; i < _labelList.length; i++ )
	{
		aLabel = _labelList[i];
		textNode = _svgDocument.createTextNode( aLabel.caption );
    	legLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
    	legLabel.appendChild(textNode);
    	legLabel.setAttributeNS(null, 'x', aLabel.x );
    	legLabel.setAttributeNS(null, 'y', aLabel.y );
    	// Should this label be displayed vertically?
    	if( aLabel.isVertical )
    	{
    		legLabel.setAttributeNS(null, 'writing-mode', "tb" );
    	}
    	
    	legLabel.setAttributeNS(null, 'font-family', 'Verdana');
    	legLabel.setAttributeNS(null, 'font-size', '10');  
    	legLabel.setAttributeNS(null, 'fill', 'darkblue');
    	// Add label to the main graph.
    	_titleArea.appendChild( legLabel);
	}
}

// Creates a legend for this graph.
function createLegend()
{
	if( _showLegend )
	{
	    var legendArea = new Area( _legendArea, _legendX, _legendY, 0, 0);
	    var myLegend = new Legend(legendArea);
	    // Get the first series.
	    var ds = _dataSet.seriesAt( 0);	
	    var di = null;
	    for( var i = 0 ; i < ds.numOfDataItems; i++)
	    {
	    	di = ds.itemAt( i);
	    	myLegend.addLabel( di[ _legendAttribute], di.color);
	    }
	}
}

// Create the dataset object based on the original
// data passed from the enquiry response.
function populateDataSet()
{
    // Figure out the number of dataseries.
    determineNumOfDataSeries();
    // Process data.
    if( _numOfDataSeries > 1)
    { 
        processMultipleDataSeries();
    }
    else
    {
        _dataSet.isSingleSeries = true;
        processSingleDataSeries();
    }
    // Allocate area for graph.
    graphArea = new Area( _graphArea , 0, 0, 0, 0);
    // Draw graph based on type
    if( _graphType == "BAR.ORDINAL" && _listOfVariables.length > 1)
    {
    	var drawAxis = true;
        var ordinalBarGraph = new OrdinalBarGraph( graphArea, drawAxis, _listOfVariables[0], _listOfVariables[1], null, null);
        ordinalBarGraph.plott();
        ordinalBarGraph.paint();
    }
    else if( _graphType == "LINE.ORDINAL" && _listOfVariables.length > 1)
    {
    	var drawAxis = true;
        var ordinalLineGraph = new OrdinalLineGraph( graphArea, drawAxis, _listOfVariables[0], _listOfVariables[1], null, null);
        ordinalLineGraph.plott();
        ordinalLineGraph.paint();
    }
    else if( _graphType == "BAR.LINE.ORDINAL" && _listOfVariables.length > 2)
    {
    	var drawAxis = true;
        var ordinalBarLine = new OrdinalBarLine(graphArea, drawAxis, _listOfVariables[0], _listOfVariables[1], _listOfVariables[2]);
    }
    else if( _graphType == "MULTI.PIE.CHART" && _listOfVariables.length > 2)
    {
  		initPieMetaData();
    	var multiPieChart = new MultiPieChart( graphArea, _listOfVariables[0], _listOfVariables[1], _listOfVariables[2] );
    }
    else if( _graphType == "PIE.CHART" && _listOfVariables.length > 1)
    {
    	initPieMetaData();
    	var multiPieChart = new MultiPieChart( graphArea, _listOfVariables[0], _listOfVariables[1], null );
    }   
    
 /*  
    str = '';
    for( i = 0; i < xAxis.listOfSegments.length ; i++ )
    {
        mySegment = xAxis.listOfSegments[i];
        str += mySegment.value + "\n";
    }
    alert(str);
  */
    
    
}

// Function used to init the pie chart meta data as global variables.
function initPieMetaData()
{
   CurrentStyle = 0; 	 	// Global counter for Style.
   
   Level = 0; 		 	// Holds the number of levels.
   
   PieSegments = new Array(); 	        // Holds every segment of every piechart initially.
   
   PieSegmentsCopy = new Array();      // Need to keep a copy of PieSegments when drilling back to the original pie.
   
   PieCharts = null;  		// Holds the Pie Charts.
   
   PieChartsLabels = null;  	// Holds the labels for the Pie Charts.
   
   PieChartSize = 0;          // The Pie Chart Max size in pixels.
   
   PieMinSize = 0;             // The Pie Chart Min size in pixels.
   
   PathData = 0;                // Global variable used to pass the svg path data for an element.
   
   Location = 0;                // Global variable used to pass the location where the last pie segment was plotted.
   
   LevelSizes = null;           // A global array that holds the PieChart size for every level i.e 
                            	// LevelSizes[0] holds the size of the PieChart at level 0. 
   
   LevelOpacity = null;         // A global array that holds the PieChart opacity for every level i.e 
                           		// LevelOpacity[0] holds the opacity of the PieChart at level 0. 
   
   Valid = true;                // A global flag which holds true if there are no errors in the processing, otherwise false.
   
   InitialPie = true;           // A global flag which holds true if the data is passed from HTML, false if its passed from a drilldown. 
   
   CurrentLevelTotal = 0;       // A global variable which holds the total of the current first level.

   MultiLevelPieChart = '';     // A global flag indicating whether the Pie is a Multi or Single level chart.

   backButton = '';           	// A global object pointer to the back button.

   comboMain = null;            // A global object pointer to the Main combo box.
   
   PieStartX = 0;               // Starting position of the pie. I.e root of radious. X coordinate.
   
   PieStartY = 0;               // Starting position of the pie. I.e root of radious. Y coordinate.
}

// Process data with multiple dataSeries.
function processMultipleDataSeries()
{
    // Series color counter
    var colorCnt = 0; 
    // Starting dataSeries.
    var ds = new DataSeries(_originalRows[0][_dataBreak]);
    ds.color = _seriesColors[colorCnt];
    colorCnt++;
    for( var i = 0; i < _originalRows.length; i++ )
    {
        var currentDataSeriesName = _originalRows[i][_dataBreak];
        var nextDataSeriesName = '';
        // If we reached the last element then don't do i+1 do just i.
        if( i < (_originalRows.length - 1 ) )
            nextDataSeriesName = _originalRows[i+1][_dataBreak];
        else
            nextDataSeriesName = _originalRows[i][_dataBreak];
        // _listOfColumns holds the columns mapped from the enquiry.
        // Go through it, extract the data from each column and 
        // create a DataItem.
        // Build the eval statement to be executed.
        var evalStatement = "di = new DataItem_GLOBAL(";
        for( var j = 0; j < _listOfColumns.length; j++ )
        {
             // _listOfColumns holds the columns to be extraced from _originalRows i.e array of {1,3,4}.
             evalStatement += "'"+_originalRows[i][_listOfColumns[j]]+"'";
             if( j != (_listOfColumns.length-1) )
                 evalStatement += ", ";
        }
        evalStatement += ");";
        // Execute the statement we built ^.
        eval(evalStatement); 
        ds.addDataItem(di);
        // If the dataseries name changes then create a new dataseries.
        if( currentDataSeriesName != nextDataSeriesName )
        {
            _dataSet.addSeries(ds);
            ds = new DataSeries(nextDataSeriesName);
            ds.color = _seriesColors[colorCnt];
            colorCnt++;
            if( colorCnt > 10) colorCnt = 0;            
        }
        // This statement is needed since the last dataSeries won't be added to 
        // the _dataSet object otherwise.
        if( i == (_originalRows.length-1) )
        {
            _dataSet.addSeries(ds);
        }
    }
}

// Process data with single dataSeries.
function processSingleDataSeries()
{
    // Create one dataseries only.
    var ds = new DataSeries("SingleSeries");
    var colorCnt = 0;
    ds.color = _seriesColors[0];
    // Go through the original rows and create DataItems using eval function.
    for( var i = 0; i < _originalRows.length; i++ )
    {
       // Build the eval statement to be executed.
       var evalStatement = "di = new DataItem_GLOBAL(";
       for( var j = 0; j < _listOfColumns.length; j++ )
       {
            // _listOfColumns holds the columns to be extraced from _originalRows i.e array of {1,3,4}.
            var colIndex = _listOfColumns[j];
            evalStatement += "'" + _originalRows[i][colIndex] + "'";
            if( j != (_listOfColumns.length-1) )
                evalStatement += ", ";
       }
       evalStatement += ");";
       eval(evalStatement);
       // Attach a color to the data item.
       di.color = _seriesColors[colorCnt];
       if( colorCnt > 10) 
            colorCnt = 0;
       else
            colorCnt++;
       // Add data item to the single data series.
       ds.addDataItem(di);
    }
    _dataSet.addSeries(ds);

}

// Goes through the original data and determines
// the total number of dataseries.
// Stores it in global variable _numOfDataSeries.
function determineNumOfDataSeries()
{   
    if( _dataBreak != '' )
    {
        for( var i = 0; i < (_originalRows.length-1); i++ )
        {
           if( _originalRows[i][_dataBreak] != _originalRows[i+1][_dataBreak])
                _numOfDataSeries++;
        }
    }                
}    



//---------------------- HELPER FUNCTIONS -------------------------------------------------------------
// Function used to display original data passed
// from the enquiry.
function displayOriginalData()
{ 
    output = "";
    
    for( var i = 0; i < _originalRows.length; i++ )
    {
        output += "(";
        for(var j = 0; j < _originalRows[0].length; j++)
        {
            if( j == (_originalRows[0].length-1) )
                output += _originalRows[i][j];
            else
                output += _originalRows[i][j] + ", ";
        }    
        output += ")\n" ;
    }
    alert(output);
}
//
function displayDataSet()
{
    result = '';
    for( var i = 0; i < _dataSet.numOfDataSeries; i++ )
    {
        ds = _dataSet.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
            di = ds.itemAt(j);
            result += di.Series + ": (" + di.XPOINT + ", " + di.YPOINT + ")\n";
        }
    }
    alert(result);
}

