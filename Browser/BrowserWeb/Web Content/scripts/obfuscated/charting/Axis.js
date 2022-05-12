//***
// --------- Axis ------------
//
// 
// 
// 
//****

// Constructor.
function Axis( type, orientation, area, minimumValue, maximumValue, drawSegments)
{
// -----------------Variables-----------------------------------------------------
    // Represent the axis data type: Linear, Ordinal or Date.
    this.type = type;
    // Represents the axis orientation: horisontalTop, horisontalBottom, verticalLeft and verticalRight.
    this.orientation = orientation;
    // The area object that this axis occupies in the svg canvas
    this.area = area;
    // Holds the visual element of this axis. The line.
    this.axisLine = null;
    // The minimum value in the dataset to be displayed.
    this.minimumValue = minimumValue;
    // Estimated minimumValue.
    this.minValEstimated = 0;
    // The maximum value in the dataset to be displayed.
    this.maximumValue = maximumValue;
    // Estimated maximumValue.
    this.maxValEstimated = 0;
    // The magnitude of the range on the dataset. I.e. for -300 to 500 = 800.
    this.range = Math.abs( maximumValue - minimumValue );
    // Holds the estimated range. I.e. if min is -783 and max is 200 then the estimated range is 1000. Normal range 983.
    this.estimatedRange = 0;
    // The approximate number of segments we wants to show up in the axis scale.
    this.targetSteps = 10;
    // Its the ratio between a real value and a pixel value based on the scale.
    this.valueToPixelRatio = 1;
    // Holds the interval length chosen based on the data.
    this.interval = 0;
    // Exact number of intervals for this axis abject.
    this.numOfIntervals = 0;
    // A list of all the segments for this axis.
    this.listOfSegments = '';
    // Holds the pixel legnth of this axis.
    this.pixelLength = 0;
    // Point object to hold to minimum point of the axis coordinally.
    this.minPoint = null;
    // Point object to hold to maximum point of the axis coordinally.
    this.maxPoint = null;
    // Holds the axis shift if the minimum value is negative. How much should the axis be shifted when least number is negative.
    this.axisShift = 0;
    // Holds the color of the axis
    this.color = "darkblue";
    // Holds the color of the grid.
    this.gridColor = "darkblue";
    // Holds the width of a this axis segment in pixels.
    this.segmentWidthPxl = 0;
    // Determines if the segments of the axis should be drawn on screen
    this.drawSegments = drawSegments;
// -----------------Methods--------------------------------------------------------
    this.getPixel = axisGetPixel;
    this.getValue = axisGetValue;
    this.determineStepSize = axisDetermineStepSize;
    this.determineAxisLength = axisDetermineAxisLength;
    this.createSegments = axisCreateSegments;
    this.determineCoordinates = axisDetermineCoordinates;
    this.draw = axisDraw;
    this.estimate = axisEstimate;
    this.drawGrid = axisDrawGrid;
    this.getZeroCoordinate = axisGetZeroCoordinate;
    this.getZeroSegment = axisGetZeroSegment;
// -----------------Calls to initial internal routines-----------------------------
    // Determine the coordinates of this axis based on the area.
    this.determineCoordinates();
    this.determineStepSize();
    this.createSegments();
}
//
function axisGetPixel(value)
{
    returnValue = 0;
    value += this.axisShift;
    // Set the axis coordinates based on the area and orientation.
    switch (this.orientation)
    {
        case _axisOrientation.horisontalTop: 
        case _axisOrientation.horisontalBottom : 
            
            returnValue = parseInt( value / this.valueToPixelRatio ) + this.minPoint.x;  
            //alert( value +", "+returnValue+", "+this.axisShift )  
            break;
        case _axisOrientation.verticalLeft: 
        case _axisOrientation.verticalRight: 
            returnValue = this.maxPoint.y - parseInt( value / this.valueToPixelRatio ) ;
            break;            
        default : break;
    }   
    return returnValue;
}
//
function axisGetValue(pixel)
{
    return pixel * this.valueToPixelRatio;
}
//
function axisDetermineCoordinates()
{
    // Set the axis coordinates based on the area and orientation.
    switch (this.orientation)
    {
        case _axisOrientation.horisontalTop: 
            this.minPoint = new Point2D( this.area.x, this.area.y);
            this.maxPoint = new Point2D( (this.area.x + this.area.width), this.area.y);
            break;
        case _axisOrientation.horisontalBottom : 
            this.minPoint = new Point2D( this.area.x, (this.area.y + this.area.height) );
            this.maxPoint = new Point2D( (this.area.x + this.area.width), (this.area.y + this.area.height) );
            break;
        case _axisOrientation.verticalLeft: 
            this.minPoint = new Point2D( this.area.x, this.area.y);
            this.maxPoint = new Point2D( this.area.x, (this.area.y + this.area.height) );
            break;
        case _axisOrientation.verticalRight: 
            this.minPoint = new Point2D( (this.area.x + this.area.width) , this.area.y);
            this.maxPoint = new Point2D( (this.area.x + this.area.width), (this.area.y + this.area.height));
            break;            
        default : break;
    }
}
//
function axisDraw()
{
    if( this.axisLine != null ) // Remove existing node if there is one.
    {
        this.area.svgParent.removeChild(this.axisLine);
    }

    this.axisLine = _svgDocument.createElementNS("http://www.w3.org/2000/svg","line");
    this.axisLine.setAttributeNS(null, "x1", this.minPoint.x);
    this.axisLine.setAttributeNS(null, "y1", this.minPoint.y);
    this.axisLine.setAttributeNS(null, "x2", this.maxPoint.x);
    this.axisLine.setAttributeNS(null, "y2", this.maxPoint.y);
    this.axisLine.setAttributeNS(null, "stroke", this.color );
    this.axisLine.setAttributeNS(null, "stroke-width", 1 );
    
    this.area.svgParent.appendChild(this.axisLine);
      
}
// Determine which part of the area we want to display our axis.
function axisDetermineAxisLength()
{
    // Set pixel length based on the orientation of the axis.
    if( this.orientation == _axisOrientation.horisontalTop || this.orientation == _axisOrientation.horisontalBottom )
        this.pixelLength = this.area.width;
    else
        this.pixelLength = this.area.height;
    // Set the valueToPixelRatio variable based the pixel length and estimatedRange.
    this.valueToPixelRatio = this.estimatedRange / this.pixelLength;
}
// This function creates segments objects and stores them
// in a the listOfSegments.
function axisCreateSegments()
{
   // Determine scale estimations
   this.minValEstimated = this.estimate( this.minimumValue, 'min' );
   this.maxValEstimated = this.estimate( this.maximumValue, 'max' );
   this.estimatedRange = Math.abs( this.maxValEstimated - this.minValEstimated );
   
   // Determine the axis length in pixels and the pixel/value ratio.
   this.determineAxisLength();
   // Calculate an axis shift.
   this.axisShift = this.minValEstimated * (-1);
   //
   this.listOfSegments = new Array();
   for( i = this.minValEstimated; i <= this.maxValEstimated; i += this.interval )
   {
        segStartPoint = null;
        segEndPoint = null;
        switch (this.orientation)
        {
            // Case orientation horizontal then calculate start and end of segment horisontally.
            case _axisOrientation.horisontalTop: 
            case _axisOrientation.horisontalBottom : 
                segStartPxl = this.getPixel( i );
                segStartPoint = new Point2D( segStartPxl, this.minPoint.y );
                segEndPxl = this.getPixel( i + this.interval );
                segEndPoint = new Point2D( segEndPxl, this.minPoint.y );
                this.segmentWidthPxl = Math.abs( segEndPxl - segStartPxl );
                break;
            // Case orientation vertical then calculate start and end of segment vertically.                
            case _axisOrientation.verticalLeft: 
            case _axisOrientation.verticalRight:
                segStartPxl = this.getPixel( i );
                segStartPoint = new Point2D( this.minPoint.x, segStartPxl );
                segEndPxl = this.getPixel( i + this.interval );
                segEndPoint = new Point2D( this.minPoint.x, segEndPxl );
                this.segmentWidthPxl = Math.abs( segEndPxl - segStartPxl );
                break;            
            default : break;
        }
        // New segment.
        seg = new Segment(segStartPoint, segEndPoint, i, this);
        // Flag this segment if its the last one.
        if( i == this.maxValEstimated) 
        {
            seg.last = true;
        }
        // Figure out cooridanates for this segment.
        seg.plott();
       	// Should the segment line be drawn?
        if( this.drawSegments )
        {   
        	seg.draw();
	        // Only draw the label if the axis is linear.
	        if( this.type == _axisType.Linear )
	        {
	            seg.drawLabel();
	        }
        }
        // Add our new segment to the list of segments.
        this.listOfSegments[this.listOfSegments.length] = seg;
   } 
} 

// Draws a grid for the axis.
function axisDrawGrid(endPoint)
{
    for( i = 0; i < this.listOfSegments.length; i++)
    {
        seg = this.listOfSegments[i];
        
        seg.segGridLine = _svgDocument.createElementNS("http://www.w3.org/2000/svg","line");	
        seg.segGridLine.setAttributeNS(null,'x1', seg.startPoint.x);
        seg.segGridLine.setAttributeNS(null,'y1', seg.startPoint.y);
        
        switch (this.orientation)
        {
            case _axisOrientation.horisontalTop: 
            case _axisOrientation.horisontalBottom : 
                seg.segGridLine.setAttributeNS(null,'x2', seg.startPoint.x);
                seg.segGridLine.setAttributeNS(null,'y2', endPoint.y);
                break;
            case _axisOrientation.verticalLeft: 
            case _axisOrientation.verticalRight:
                seg.segGridLine.setAttributeNS(null,'x2', endPoint.x);
                seg.segGridLine.setAttributeNS(null,'y2', seg.startPoint.y);
                break;            
            default : break;
        }

        seg.segGridLine.setAttributeNS(null, "stroke", this.gridColor );
        //seg.segGridLine.setAttributeNS(null,"stroke-opacity", 0.5);
        seg.segGridLine.setAttributeNS(null, "stroke-width", 1 );
        if( seg.value != 0  )
        {
            seg.segGridLine.setAttributeNS(null, "stroke-dasharray", "2 6" );
            seg.segGridLine.setAttributeNS(null, "stroke-width", 0.3 );
        }
        this.area.svgParent.appendChild(seg.segGridLine);
    }
    
}

// Determine the interval size or step size of this axis
// based on the data range.
function axisDetermineStepSize()
{
    switch( this.type)
    {
        case _axisType.Linear: 
            // Calculate an initial guess at step size
            tempStep = this.range / this.targetSteps ;
            // Get the magnitude of the step size
            mag = Math.floor( Math.log( tempStep ) / Math.log( 10 ) );
            // 
            magPow = Math.pow( 10, mag );
            // Calculate most significant digit of the new step size
            magMsd = ( parseInt (tempStep / magPow + .5) );
            // promote the MSD to either 1, 2, or 5
            if ( magMsd > 5.0 )
                magMsd = 10.0;
            else if ( magMsd > 2.0 )
                magMsd = 5.0;
            else if ( magMsd > 1.0 )
                magMsd = 2.0;
            //    
            this.interval = magMsd * magPow;
            break;
        case _axisType.Ordinal:
            // Just set it to 1, as the axis type is ordinal, therefore step is 1. 
            this.interval = 1;
            break;
        default: break;
     }
}

// Estimates the number to its nearest scaling segment.
function axisEstimate( number, minOrMax )
{
    estimate = 0;
    
    if( number == 0)
        estimate = 0;
    else if( number < 0 )
    { 
        while( number < estimate )
            estimate -= this.interval;
        
        if( estimate != number && minOrMax == 'max')
            estimate += this.interval;
        
    }
    else
    {
        while( number > estimate )
            estimate += this.interval;
        
        if( estimate != number && minOrMax == 'min')
            estimate -= this.interval;
        
    }   

    return estimate;
}
// Returns the zero segment of this axis, if there exist.
// Returns -1 otherwise, to indicate absence.
function axisGetZeroSegment()
{
    for( i = 0 ; i < this.listOfSegments.length; i++ )
    {
        seg = this.listOfSegments[i];
        if ( seg.value == 0 )
            return seg;
    }
    return null;

}
// Returns the zero coordinate of this axis. 
// If there is no zero coordinate then return the minPoint of this axis.
function axisGetZeroCoordinate()
{
    seg = this.getZeroSegment();
    switch (this.orientation)
    {
        case _axisOrientation.horisontalTop: 
        case _axisOrientation.horisontalBottom : 
            if( seg == null )
                return this.minPoint.x;
            else
                return seg.startPoint.x;
            break;
        case _axisOrientation.verticalLeft: 
        case _axisOrientation.verticalRight:
            if( seg == null )
                return this.minPoint.y;
            else
                return seg.startPoint.y;
            break;            
        default : break;
    }

}


