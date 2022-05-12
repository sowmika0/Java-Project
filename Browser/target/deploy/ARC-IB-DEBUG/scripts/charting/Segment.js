//***
// --------- Segment ------------
//  This class represents a segment in a axis.
//  Holds related information such as type of a segment
//  i.e Number, Text or Date. The location where it should be drawn etc. 
// 
//****

function Segment(startPoint, endPoint, value, axis)
{
// -----------------Variables-----------------------------------------------------
    // The axis object this segment belongs to.
    this.axis = axis;
    // The visual svg element handle for the segment line.
    this.segLine = null;
    // The visual svg element handle for the mid segment line.
    this.segMidLine = null;
    // The visual svg element handle for the segment label.
    this.segLabel = null;
    // Holds the grid line element for this segment.
    this.segGridLine = null;
    // A Point2D object of the starting point of the segment.
    this.startPoint = startPoint;
    // A Point2D object of the start mid point of the segment.
    this.midPointStart = null;
    // A Point2D object of the end mid point of the segment.
    this.midPointEnd = null;
    // A Point2D object of the ending point of the segment.    
    this.endPoint = endPoint;
    // A Point2D object holding the end point of the segment line that sticks out of the axis. 
    this.segEndPoint = null;
    // A Point2D object that holds the coordinate of the segment label
    this.segLabelPoint = null;
    // The preferred segment line size. [Should be set from a Config object].
    this.segLineSize = 7;
    // The preferred label from segment distance. [Should be set from a Config object].
    this.labelSegmentDistance = 5;  
    // Holds the text anchor for the label of this axis.
    this.labelTextAnchor = "start";     
    // The value of the segment
    this.value = value;
    // If set to true, its the last segment.
    this.last = false;
    
// -----------------Methods--------------------------------------------------------
    this.plott = segmentPlott;
    this.draw = segmentDraw;
    this.drawLabel = segmentDrawLabel;

// -----------------Calls to initial internal routines-----------------------------  

}
//
function segmentPlott()
{
// Determine which direction the segment lines are going to be pointing.
    // Depending on the axis orientation.
    switch (this.axis.orientation)
    {
        case _axisOrientation.horisontalTop:
            this.segEndPoint = new Point2D( this.startPoint.x,  (this.startPoint.y + this.segLineSize) );
            this.segLabelPoint = new Point2D( this.startPoint.x, (this.startPoint.y + this.segLineSize + this.labelSegmentDistance*2) );
            this.midPointStart = new Point2D( this.startPoint.x + parseInt((this.endPoint.x - this.startPoint.x) / 2 ), this.startPoint.y );
            this.midPointEnd = new Point2D( this.midPointStart.x , (this.startPoint.y + parseInt( this.segLineSize / 2 ) ) );
            this.labelTextAnchor = 'middle';
            break;
        case _axisOrientation.horisontalBottom : 
            this.segEndPoint = new Point2D( this.startPoint.x,  (this.startPoint.y - this.segLineSize) );
            this.segLabelPoint = new Point2D( this.startPoint.x, (this.startPoint.y - this.segLineSize - this.labelSegmentDistance*2) );
            this.midPointStart = new Point2D( this.startPoint.x + parseInt((this.endPoint.x - this.startPoint.x) / 2 ), this.startPoint.y );
            this.midPointEnd = new Point2D( this.startPoint.x + parseInt((this.endPoint.x - this.startPoint.x) / 2 ), (this.startPoint.y - parseInt( this.segLineSize / 2 )) );
            this.labelTextAnchor = 'middle';
            break;
        case _axisOrientation.verticalLeft: 
            this.segEndPoint = new Point2D( (this.startPoint.x + this.segLineSize) , this.startPoint.y );
            this.segLabelPoint = new Point2D( (this.startPoint.x + this.segLineSize + this.labelSegmentDistance) , this.startPoint.y + 3 );
            this.midPointStart = new Point2D( this.startPoint.x , this.startPoint.y + parseInt((this.endPoint.y - this.startPoint.y) / 2 ));
            this.midPointEnd = new Point2D( (this.startPoint.x + parseInt( (this.segLineSize / 2)) ) , this.startPoint.y + parseInt((this.endPoint.y - this.startPoint.y) / 2 ));
            this.labelTextAnchor = 'start';
            break;
        case _axisOrientation.verticalRight: 
            this.segEndPoint = new Point2D( (this.startPoint.x - this.segLineSize) , this.startPoint.y );
            this.segLabelPoint = new Point2D( (this.startPoint.x - this.segLineSize - this.labelSegmentDistance) , this.startPoint.y + 3 );
            this.midPointStart = new Point2D( this.startPoint.x , this.startPoint.y + parseInt((this.endPoint.y - this.startPoint.y) / 2 ));
            this.midPointEnd = new Point2D( (this.startPoint.x - parseInt( (this.segLineSize / 2)) ) , this.startPoint.y + parseInt((this.endPoint.y - this.startPoint.y) / 2 ));
            this.labelTextAnchor = 'end';
            break;            
        default : break;
    }
    
    //alert( "midPointStart.x :" +this.midPointStart.x+ ", midPointStart.y: " + this.midPointStart.y)
    //alert( "midPointEnd.x :" +this.midPointEnd.x+ ", midPointEnd.y: " + this.midPointEnd.y)	
}
//
function segmentDraw()
{
    // Remove existing node if there is one.
    if( this.segLine != null ) 
    {
        this.axis.area.svgParent.removeChild(this.segLine);
    }
    // Remove existing node if there is one.
    if( this.segMidLine != null ) 
    {
        this.axis.area.svgParent.removeChild(this.segMidLine);
    }
    

    this.segLine = _svgDocument.createElementNS("http://www.w3.org/2000/svg","line");
    this.segLine.setAttributeNS(null, "x1", this.startPoint.x);
    this.segLine.setAttributeNS(null, "y1", this.startPoint.y);
    this.segLine.setAttributeNS(null, "x2", this.segEndPoint.x);
    this.segLine.setAttributeNS(null, "y2", this.segEndPoint.y);
    this.segLine.setAttributeNS(null, "stroke", this.axis.color );
    this.segLine.setAttributeNS(null, "stroke-width", 1 );
    
    this.axis.area.svgParent.appendChild(this.segLine);   
    
    if( !this.last && this.axis.type == _axisType.Linear )
    {
        this.segMidLine = _svgDocument.createElementNS("http://www.w3.org/2000/svg","line");
        this.segMidLine.setAttributeNS(null, "x1", this.midPointStart.x);
        this.segMidLine.setAttributeNS(null, "y1", this.midPointStart.y);
        this.segMidLine.setAttributeNS(null, "x2", this.midPointEnd.x);
        this.segMidLine.setAttributeNS(null, "y2", this.midPointEnd.y);
        this.segMidLine.setAttributeNS(null, "stroke", this.axis.color );
        this.segMidLine.setAttributeNS(null, "stroke-width", 1 );
    
        this.axis.area.svgParent.appendChild(this.segMidLine);
    }   
     
}
//
function segmentDrawLabel()
{
    // Remove existing node if there is one.
    if( this.segLabel != null ) 
    {
        this.axis.area.svgParent.removeChild(this.segLabel);
    }  
    
    //this.segLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
    
    textNode = _svgDocument.createTextNode(this.value);
    this.segLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
    this.segLabel.appendChild(textNode);
    
    this.segLabel.setAttributeNS(null, 'x', this.segLabelPoint.x );
    this.segLabel.setAttributeNS(null, 'y', this.segLabelPoint.y );
    this.segLabel.setAttributeNS(null, 'font-family', 'Verdana');
	this.segLabel.setAttributeNS(null, 'font-size', '10');  
    this.segLabel.setAttributeNS(null, 'text-anchor', this.labelTextAnchor );        
    this.segLabel.setAttributeNS(null, 'fill', this.axis.color); 
    this.axis.area.svgParent.appendChild(this.segLabel);
}