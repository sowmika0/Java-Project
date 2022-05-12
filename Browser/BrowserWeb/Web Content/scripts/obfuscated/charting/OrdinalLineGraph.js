//***
// --------- OrdinalLineGraph ------------
//
// 
//****

// Constructor.
function OrdinalLineGraph( area, drawAxis, xAttribute, yAttribute, xAxis, yAxis)
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
    this.create = ordinalLineGraphCreate;
    this.plott = ordinalLineGraphPlott; 
    this.paint = ordinalLineGraphPaint;
    this.join = ordinalLineGraphJoin;
    this.displayLabels = ordinalLineGraphDisplayLabels;
// -----------------Calls to initial internal routines-----------------------------
    this.create();
}

//
function ordinalLineGraphCreate()
{  
	// If the y axis is not null then we must have passed it in as a param.
    if( this.yAxis == null)
    {
	    var minY = _dataSet.getMin( this.yAttribute);
		var maxY = _dataSet.getMax( this.yAttribute);    	
	    // Use the custom scale if user has specified it.
	    if( _axisScaleY > maxY)
	    {
	    	maxY = _axisScaleY;
	    }
	    // Use 0 is the minimum is greater then 0.
	    if( minY > 0)
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
	// If the x axis is not null then we must have passed it in as a param.
    if( this.xAxis == null)
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
       
    // Should we draw axis.
    if( this.drawAxis )
    {
    	this.yAxis.draw();
    	this.yAxis.drawGrid( this.xAxis.maxPoint);
    	
    	this.xAxis.draw();
    }

     
}
//
function ordinalLineGraphPlott()
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
           di.plottPoint = new Point2D( x, y );
           
        }
    }
}
//
function ordinalLineGraphPaint()
{
    for( var i = 0; i < _dataSet.numOfDataSeries; i++ )
    {
        var ds = _dataSet.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
           var di = ds.itemAt(j);
           di.svgElement = _svgDocument.createElementNS("http://www.w3.org/2000/svg","circle");	
           di.svgElement.setAttributeNS(null,'cx', di.plottPoint.x);
           di.svgElement.setAttributeNS(null,'cy', di.plottPoint.y);
           di.svgElement.setAttributeNS(null,'r', 3);
           di.svgElement.setAttributeNS(null,'fill', "darkblue");
           di.svgElement.setAttributeNS(null,'stroke', "darkblue");
           di.svgElement.setAttributeNS(null,'stroke-width', 0);
           this.area.svgParent.appendChild(di.svgElement);  
           
           if( this.showAmounts )
           {
	           var textNode = _svgDocument.createTextNode( di[ this.yAttribute]);
	           var svgAmount = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
	           svgAmount.appendChild(textNode);
	           svgAmount.setAttributeNS(null, "x", di.plottPoint.x - 4);
	           svgAmount.setAttributeNS(null, "y", di.plottPoint.y - 6 );
	           svgAmount.setAttributeNS(null, 'fill', "darkblue"); 
	           _graphArea.appendChild( svgAmount );               
           }
        }
    }
    
    this.join();
    this.displayLabels();

}
//
function ordinalLineGraphJoin()
{
    for( var i = 0; i < _dataSet.numOfDataSeries; i++ )
    {
        var ds = _dataSet.seriesAt(i);
        for( var j = 0; j < (ds.numOfDataItems-1); j++ )
        {
           var firstPoint = ds.itemAt(j);
           var secondPoint = ds.itemAt(j+1);
           firstPoint.tag = _svgDocument.createElementNS("http://www.w3.org/2000/svg","line");	
           firstPoint.tag.setAttributeNS(null,"x1", firstPoint.plottPoint.x);
           firstPoint.tag.setAttributeNS(null,"y1", firstPoint.plottPoint.y);
           firstPoint.tag.setAttributeNS(null,"x2", secondPoint.plottPoint.x);
           firstPoint.tag.setAttributeNS(null,"y2", secondPoint.plottPoint.y);
           firstPoint.tag.setAttributeNS(null,"fill", ds.color);
           firstPoint.tag.setAttributeNS(null, "stroke", "darkblue" );
           firstPoint.tag.setAttributeNS(null, "stroke-width", 0.5 );
           this.area.svgParent.appendChild(firstPoint.tag);                 
        }
    }   
}
//
// Displays the ordinal labels based on the data
function ordinalLineGraphDisplayLabels()
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
    var textNode = null;
    var svgLabel = null;
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
       //
       svgLabel.appendChild(textNode);
       svgLabel.setAttributeNS(null, "font-family", "Verdana");
	   svgLabel.setAttributeNS(null, "font-size", "10");  
       svgLabel.setAttributeNS(null, "fill", di.color); 
       //
       this.xAxis.area.svgParent.appendChild(svgLabel);          
    }
}

