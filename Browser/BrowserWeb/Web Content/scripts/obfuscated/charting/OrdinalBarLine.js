//***
// --------- OrdinalBarLine ------------
//
// 
//****

// Constructor.
function OrdinalBarLine( area, drawAxis, xAttribute, yAttribute, zAttribute)
{
// -----------------Variables-----------------------------------------------------
    // The area which the graph occupies in the svg canvas.
    this.area = area;
    // Holds the x attribute from the data set
    this.xAttribute = xAttribute;
    // Holds the y attribute from the data set
    this.yAttribute = yAttribute;
    // Holds the z attribute from the data set
    this.zAttribute = zAttribute;
// -----------------Methods--------------------------------------------------------
    this.create = ordinalBarLineCreate;
// -----------------Calls to initial internal routines-----------------------------
    this.create();
}

//
function ordinalBarLineCreate()
{

    var maxX = _dataSet.getMax( this.xAttribute);
    var maxY = _dataSet.getMax( this.yAttribute);
    var minX = _dataSet.getMin( this.xAttribute); 
    var minY = _dataSet.getMin( this.yAttribute);
    
    // Get the highest minimum and maximum for this axis.
    if( maxX > maxY )
    {
    	maxY = maxX;
    }
    
    if( minX < minY)
    {
    	minY = minX;
    }
    
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
    var yAxis = new Axis( _axisType.Linear, _axisOrientation.verticalRight, this.yAxisArea, minY, maxY, true);

    var ordinalBarGraph = new OrdinalBarGraph( graphArea, false, this.zAttribute, this.yAttribute, null, yAxis);
    // Don't show amounts.
    ordinalBarGraph.showAmounts = false;
    ordinalBarGraph.plott();
    ordinalBarGraph.paint();   
    
    var ordinalLineGraph = new OrdinalLineGraph( graphArea, true, this.zAttribute, this.xAttribute, null, yAxis);
    ordinalLineGraph.plott();
    ordinalLineGraph.paint();
    

        
}


