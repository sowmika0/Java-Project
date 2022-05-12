//***
// --------- Legend ------------
//
// 
// 
// 
//****

// Constructor.
function Legend(area)
{
// -----------------Variables-----------------------------------------------------
    this.area = area;
    this.x = area.x;
    this.y = area.y;
    this.listOfLabels = new Array();
// -----------------Methods--------------------------------------------------------
    this.Init = legendInit;
    this.addLabel = legendAddLabel;
// -----------------Calls to initial internal routines-----------------------------  
}
//
function legendInit()
{
    var textNode = _svgDocument.createTextNode(_dataSet.title);
    var titleLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
    titleLabel.appendChild(textNode);
    titleLabel.setAttributeNS(null, 'x', this.x );
    titleLabel.setAttributeNS(null, 'y', this.y );
    this.y += 20;
    this.x += 20;
    titleLabel.setAttributeNS(null, 'font-family', 'Verdana');
    titleLabel.setAttributeNS(null, 'font-size', '10');  
    titleLabel.setAttributeNS(null, 'fill', 'darkblue');  
    this.area.svgParent.appendChild(titleLabel);                
}
// Adds a label to the legend.
function legendAddLabel( caption, color )
{
    var circle = _svgDocument.createElementNS("http://www.w3.org/2000/svg","circle");	
    circle.setAttributeNS(null,'cx', (this.x-10) );
    circle.setAttributeNS(null,'cy', this.y-3 );
    circle.setAttributeNS(null,'r', 4);
    circle.setAttributeNS(null,'fill', color);
    circle.setAttributeNS(null,'stroke', color);
            
    var textNode = _svgDocument.createTextNode( caption );
    var legLabel = _svgDocument.createElementNS("http://www.w3.org/2000/svg","text");
    legLabel.appendChild(textNode);
    legLabel.setAttributeNS(null, 'x', this.x );
    legLabel.setAttributeNS(null, 'y', this.y );
    this.y += 15;
    legLabel.setAttributeNS(null, 'font-family', 'Verdana');
    legLabel.setAttributeNS(null, 'font-size', '10');  
    legLabel.setAttributeNS(null, 'fill', 'darkblue'); 
    
    this.area.svgParent.appendChild(circle);               
    this.area.svgParent.appendChild(legLabel);
}