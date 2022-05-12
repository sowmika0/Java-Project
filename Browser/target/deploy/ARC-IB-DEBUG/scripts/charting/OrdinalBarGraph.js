//***
// --------- OrdinalLineGraph ------------
//
// 
//****

// Constructor.
function OrdinalBarGraph( area, drawAxis, xAttribute, yAttribute, xAxis, yAxis)
{
// -----------------Variables-----------------------------------------------------
    // The area which the graph occupies in the svg canvas.
    this.area = area;
    // The Y axis object of this graph.
    this.yAxis = yAxis;
    // Area that is occupied for the Y axis.
    this.yAxisArea = null;
    // The X axis object of this graph.
    this.xAxis = xAxis;
    // Area that is occupied for the X axis.
    this.xAxisArea = null;
    // Holds the orientation of the bars. Horisontal or vertical.
    this.isHorisontal = true; 
    // Decides wheather axis should be drawn on screen
    this.drawAxis = drawAxis;
    // Decides if the amounts on top of the bars should be shown.
    this.showAmounts = false;
    // Holds the x attribute to be referenced from the data set.
    this.xAttribute = xAttribute;
    // Holds the y attribute to be referenced from the data set.
    this.yAttribute = yAttribute;
// -----------------Methods--------------------------------------------------------
    this.create = ordinalBarGraphCreate;
    this.plott = ordinalBarGraphPlott; 
    this.paint = ordinalBarGraphPaint;
    this.displayLabels = ordinalBarGraphDisplayLabels;
// -----------------Calls to initial internal routines-----------------------------
    this.create();
}

//
function ordinalBarGraphCreate()
{	
    
    if( this.xAxis == null )
    {
        var minX = 0;
    	var maxX = _dataSet.getMaxSeriesNum();	
    	
	    this.xAxisArea = new Area( _xAxisArea, 
						   _graphCoordinates.xBottomPosX,
						   _graphCoordinates.xBottomPosY,
						   _graphCoordinates.xBottomWidth,
						   _graphCoordinates.xBottomHeight );
						     
    	this.xAxis = new Axis( _axisType.Ordinal, _axisOrientation.horisontalTop , this.xAxisArea, minX, maxX, this.drawAxis); 
    }
    
    if( this.yAxis == null )
    {
	    var minY = _dataSet.getMin( this.yAttribute);
	    var maxY = _dataSet.getMax( this.yAttribute);
	    // Use the custom scale if user has specified it.
	    if( _axisScaleY > maxY)
	    {
	    	maxY = _axisScaleY;
	    }
	    
	    if( minY > 0 )
	    {
	    	minY = 0;
	    }    	
	    
    	this.yAxisArea = new Area( _yAxisArea, 
    							   _graphCoordinates.yLeftPosX, 
    							   _graphCoordinates.yLeftPosY, 
    							   _graphCoordinates.yLeftWidth, 
    							   _graphCoordinates.yLeftHeight);
    							   
   		this.yAxis = new Axis( _axisType.Linear, _axisOrientation.verticalRight , this.yAxisArea, minY, maxY, this.drawAxis); 	    
    }
    

    // Should we draw axis.
    if( this.drawAxis )
    {
    	this.yAxis.draw();
    	this.yAxis.drawGrid( this.xAxis.maxPoint);
    	
    	this.xAxis.draw();
    }
       
}
//
function ordinalBarGraphPlott()
{
    
    segIndex = 0;
    for( var i = 0; i < _dataSet.numOfDataSeries; i++ )
    {
        ds = _dataSet.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
           di = ds.itemAt(j);
           
           x = this.xAxis.listOfSegments[j].midPointStart.x; 
           y = this.yAxis.getPixel( di[ this.yAttribute] * 1 );
           //alert("x :" +x+ ", y :" + y);
           endPoint = new Point2D( x, y );
           startPoint = new Point2D( x, this.yAxis.getZeroCoordinate() );
           barObj = new Bar( startPoint, endPoint, this.xAxis.segmentWidthPxl - 10, false);
           barObj.color = di.color;
           di.tag = barObj;
           
        }
    }
}
//
function ordinalBarGraphPaint()
{
	var di = null;
	var ds = null;
	
    for( var i = 0; i < _dataSet.numOfDataSeries; i++ )
    {
        ds = _dataSet.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
			di = ds.itemAt(j);
			di.tag.draw();
			if( this.showAmounts ) 
			{
				di.tag.showAmount( di[ this.yAttribute]);
			}
        }
    }
    //Only add labels if we are showing the axis.
    if( this.drawAxis )
    {
    	this.displayLabels();
    }

}
// Displays the ordinal labels based on the data
function ordinalBarGraphDisplayLabels()
{
    // 1 degree = 0.0174532925 radian. JavaScript Math functions deal with radians.
    // Use degrees and multiply by this factor to convert to radians.
    var radianFactor = 0.0174532925;
    // Holds the maximum pixel length a label could use in this axis
    // Used to determine the manner in which the labels are displayed.
    var maxTextLength = 0;
    // This type of chart only supports single series display.
    // Hence only obtain the one and only series.
    var ds = _dataSet.seriesAt(0);
    for( var i = 0; i < ds.numOfDataItems; i++ )
    {
        var di = ds.itemAt(i);
        var textNode = _svgDocument.createTextNode( di[ this.xAttribute]);
        var svgLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
        svgLabel.appendChild(textNode);
        var textLength = svgLabel.getComputedTextLength();
        if( textLength > maxTextLength )
            maxTextLength = textLength;
    }
    textNode = null;
    svgLabel = null;
    // Figure out how long is the hypotenuse of the segment width in pixels.
    //segHypotenuseLength = Math.round( this.xAxis.segmentWidthPxl / Math.cos( 45 * radianFactor ) );
    // 
    for( var i = 0; i < ds.numOfDataItems; i++ )
    {
       var di = ds.itemAt(i);
       // Get the next segment.
       var seg = this.xAxis.listOfSegments[i]; 
       //
       var textNode = _svgDocument.createTextNode( di[ this.xAttribute]);
       var svgLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
       svgLabel.setAttributeNS(null, "x", seg.midPointEnd.x );
       svgLabel.setAttributeNS(null, "y", seg.midPointEnd.y + 10 );
       //
       if( maxTextLength < this.xAxis.segmentWidthPxl ) // Display Horizontally.
       {
            svgLabel.setAttributeNS(null, "text-anchor", "middle" ); 

       }
       else
       {
            svgLabel.setAttributeNS(null, "writing-mode", "tb");
      
       }
       
       svgLabel.appendChild(textNode);                   
       svgLabel.setAttributeNS(null, "font-family", "Verdana");
	   svgLabel.setAttributeNS(null, "font-size", "10");  
       svgLabel.setAttributeNS(null, "fill", di.color); 
       //
       this.xAxis.area.svgParent.appendChild(svgLabel);          
    }
}