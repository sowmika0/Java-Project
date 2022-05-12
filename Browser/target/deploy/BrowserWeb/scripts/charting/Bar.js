//***
// --------- Bar ------------
//
// 
// 
// 
//****

// Constructor.
function Bar(startPoint, endPoint, width, isHorisontal)
{
// -----------------Variables-----------------------------------------------------
    this.svgElement = null;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.x = 0;
    this.y = 0;
    this.width = width;
    this.height = 0;
    this.roundFactor = 3;
    this.color = 'darkblue';
    this.barOpacity = 0.3;
    this.isPositiveBar = true;
    
    this.isHorisontal = false;
    if( isHorisontal )
    {
    	this.isHorisontal = true;
    }
// -----------------Methods--------------------------------------------------------
    this.init = barInit;
    this.initHorisontal = barInitHorisontal;
    this.draw = barDraw;
    this.showAmount = barShowAmount;
// -----------------Calls to initial internal routines-----------------------------  
	if( this.isHorisontal )
	{
    	this.initHorisontal(); // Horisontal bar.
	}
	else
	{
		this.init(); // Vertical bar.
	}
}
//
function barInit()
{
    var halfWidth = parseInt( this.width / 2 );
    if( this.startPoint.y < this.endPoint.y ) //Negative Bar.
    {
        this.x = this.startPoint.x - halfWidth;
        this.y = this.startPoint.y;
        this.height = this.endPoint.y - this.startPoint.y;            
        this.isPositiveBar = false;
    }
    else                                      //Positive Bar.
    {
        this.x = this.endPoint.x - halfWidth;
        this.y = this.endPoint.y;
        this.height = this.startPoint.y - this.endPoint.y;
    }
    
}

function barInitHorisontal()
{
	var halfWidth = parseInt( this.width / 2 );
    if( this.startPoint.x > this.endPoint.x ) //Negative Bar.
    {
        this.x = this.endPoint.x;
        this.y = this.endPoint.y - halfWidth;
        this.height = this.width;
        this.width = this.startPoint.x - this.endPoint.x;
        this.isPositiveBar = false;
    }
    else                                      //Positive Bar.
    {
        this.x = this.startPoint.x;
        this.y = this.startPoint.y - halfWidth;
        this.height = this.width;
        this.width = this.endPoint.x - this.startPoint.x;
    }
}
//
function barDraw()
{
      this.svgElement = _svgDocument.createElementNS("http://www.w3.org/2000/svg","rect");
      this.svgElement.setAttributeNS(null,"x", this.x);
      this.svgElement.setAttributeNS(null,"y", this.y );	
      this.svgElement.setAttributeNS(null,"rx", this.roundFactor);	 
      this.svgElement.setAttributeNS(null,"width", this.width);
      this.svgElement.setAttributeNS(null,"height", this.height);
      this.svgElement.setAttributeNS(null,"fill", this.color);
      this.svgElement.setAttributeNS(null,"stroke", this.color);
      this.svgElement.setAttributeNS(null,"fill-opacity", this.barOpacity);
      _graphArea.appendChild( this.svgElement );
}
//
function barShowAmount(amount)
{
      var textNode = _svgDocument.createTextNode(amount);
      var svgAmount = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
      svgAmount.appendChild(textNode);
      if( this.isPositiveBar )
      {
        svgAmount.setAttributeNS(null, "x", this.endPoint.x );
        svgAmount.setAttributeNS(null, "y", this.endPoint.y - 10 );
      }
      else
      {
        svgAmount.setAttributeNS(null, "x", this.endPoint.x );
        svgAmount.setAttributeNS(null, "y", this.endPoint.y + 15 );
      }
      
      svgAmount.setAttributeNS(null, "text-anchor", "middle" ); 
      svgAmount.setAttributeNS(null, 'fill', this.color); 
      _graphArea.appendChild( svgAmount );

}