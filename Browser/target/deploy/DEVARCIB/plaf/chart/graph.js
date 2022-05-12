
      debug = true
    
     svgdoc = null
    
      ChartHeight = 250
      ChartWidth  = 350
      
      XArray = new Array()
      YArray = new Array()
      Elements = new Array()
      ElementsArray = new Array()
      ElementsSeries = new Array()
      XSeries = new Array()
      YSeries = new Array()
      MaxX = 0;
      MaxY = 0;
      MinX=-1;
      MinY=0;
      
      ParentGroup = null;
      Grandparent = null;
      ChartLine = null;
      XaxisLine=null;
      
      Removed = false
      
      function Initialize(LoadEvent)
        {
         svgdoc = LoadEvent.target.ownerDocument;

          ParentGroup =svgdoc.getElementById("loadlayer")
          Grandparent =svgdoc.getElementById("chart")
          ChartLine =svgdoc.getElementById("chartline1")
	  XaxisLine =svgdoc.getElementById("xaxisline")
	  YaxisLine =svgdoc.getElementById("yaxisline")

        }
      // Add drilldata for enquiry drill downs
	function AddDrill(Drill) {
	//DrillData[DrillData.length]=Drill;
	}


      function AddChartValue(X, Y, Series,Repress)
        {
        X = X * 1
        Y = Y * 1
        if (!(Removed)){
            Grandparent.removeChild(ParentGroup)
        }

        NewItem = XArray.length
// Pull the array out of the series information
	if (Series >= XSeries.length){
		XArray = new Array()
		YArray = new Array()
		ElementsArray = new Array()
	}else {
		XArray = XSeries[Series]
		YArray = YSeries[Series]
		ElementsArray = ElementsSeries[Series]
	}

          XArray[XArray.length] = X * 1
          YArray[YArray.length] = Y * 1
          ElementsArray[ElementsArray.length] = Elements.length
          
          NewElement =svgdoc.createElementNS("http://www.w3.org/2000/svg","circle")
          
          Elements[Elements.length] = NewElement
          
          Elements[Elements.length - 1].setAttributeNS(null,"class", "dot"+Series)
          Elements[Elements.length - 1].setAttributeNS(null,"onmouseover", "DisplayInfo('" + X + "', '" + Y + "')")
          Elements[Elements.length - 1].setAttributeNS(null,"onmouseout", "DisplayInfo(' ', ' ')")

          
          ChangeScale = false
          
          if (X * 1 > MaxX * 1){
              MaxX = X
              ChangeScale = true
           }
// Set the original low value

	  if (MinX ==-1){
		MinX=X;
		ChangeScale = true;
	    }
          if (X * 1 < MinX * 1)
            {
              MinX = X
              ChangeScale = true
            }
          if (Y * 1 > MaxY * 1)
            {
              MaxY = Y
              ChangeScale = true
            }
          if (Y * 1 < MinY * 1)
            {
              MinY = Y
              ChangeScale = true
            }

          for (var I = 0; I < XArray.length; I++)
            for (var J = I + 1; J < XArray.length; J++)
              if (XArray[J] < XArray[I])
                {
                  Temp = XArray[J]
                  XArray[J] = XArray[I]
                  XArray[I] = Temp

                  Temp = YArray[J]
                  YArray[J] = YArray[I]
                  YArray[I] = Temp

                  Temp = Elements[J]
                  Elements[J] = Elements[I]
                  Elements[I] = Temp
                  
                  if (J == NewItem)
                    NewItem = I
                }
          
          XSeries[Series]=XArray
          YSeries[Series]=YArray         
 	   ElementsSeries[Series]=ElementsArray

         svgdoc.getElementById("maxY").replaceChild(svgdoc.createTextNode(MaxY+" "),svgdoc.getElementById("maxY").firstChild)
         svgdoc.getElementById("maxX").replaceChild(svgdoc.createTextNode(MaxX+" "),svgdoc.getElementById("maxX").firstChild)
         svgdoc.getElementById("min").replaceChild(svgdoc.createTextNode(MinY+" "),svgdoc.getElementById("min").firstChild)

          if (Repress) {
            Removed = true
	  } else {
              	Removed = false
            }
        }
//Actually draw the graph
function Finish(){
              	Removed = false
              	drawGraph()
	//	XaxisLine.setAttributeNS(null,"transform","100,100");
		if(MinY==0){
			zeropos=0;
		} else{
			zeropos= (0 - MinY) / (MaxY-MinY)*ChartHeight;

		}
		XaxisLine.setAttributeNS(null,"d","M0,-"+zeropos+"H"+ChartWidth);
		YaxisLine.setAttributeNS(null,"d","M0,0H-5 0 V 5 -"+ChartHeight+" h -5 10 -5 V 0")
              	Grandparent.appendChild(ParentGroup);
}

function drawGraph() {
//Loop through each series

	for(var k =1; k< XSeries.length;k++){
	ChangeScale=true

 	XArray = XSeries[k]
	YArray = YSeries[k]
	ElementsArray = ElementsSeries[k]
	PathData="M"
          for (var I = 0; I < XArray.length; I++)
            {
              if ((ChangeScale) || (I == NewItem))
                {
                ElementRef = ElementsArray[I]
		  xpos = (XArray[I] - MinX)/ (MaxX-MinX) * ChartWidth
		ypos = 	-1 * (YArray[I]-MinY) / (MaxY-MinY) * ChartHeight	
                  Elements[ElementRef].setAttributeNS(null,"cx", xpos)
                  Elements[ElementRef].setAttributeNS(null,"cy", ypos)
                  Elements[ElementRef].setAttributeNS(null,"class", "dot"+k)
                  Elements[ElementRef].setAttributeNS(null,"r", "5")
                  ParentGroup.appendChild(Elements[ElementRef])
                }
                
              PathData = PathData + " " + (xpos) + " " + ypos
            }

// Clone the node that is the line to create series items
                    ChartLine.setAttributeNS(null,"d", PathData)
			var newnode =ChartLine.cloneNode(false); 
			newnode.setAttributeNS(null,"class", "line"+k);
			ParentGroup.appendChild (newnode); 
			newnode=null;
	}


       

}        
      function DisplayInfo(Text, Value)
        {
          NewItem =svgdoc.createTextNode(Value)
          firstChild = svgdoc.getElementById("labelamount").firstChild
          if (firstChild==null){
          svgdoc.getElementById("labelamount").appendChild(NewItem)
          }
         svgdoc.getElementById("labelamount").replaceChild(NewItem,svgdoc.getElementById("labelamount").firstChild)
          firstChild = svgdoc.getElementById("labelitem").firstChild
          if (firstChild==null){
          svgdoc.getElementById("labelitem").appendChild(NewItem)
          }
          NewItem =svgdoc.createTextNode(Text)
         svgdoc.getElementById("labelitem").replaceChild(NewItem,svgdoc.getElementById("labelitem").firstChild)

          if (Text + Value == "  ")
            NewItem =svgdoc.createTextNode(" ")
          else
            NewItem =svgdoc.createTextNode(":")
                      firstChild = svgdoc.getElementById("labelcolon").firstChild
          if (firstChild==null){
          svgdoc.getElementById("labelcolon").appendChild(NewItem)
          }
         svgdoc.getElementById("labelcolon").replaceChild(NewItem,svgdoc.getElementById("labelcolon").firstChild)
        }
        
      function SetTitle(Text)
        {
          NewItem =svgdoc.createTextNode(Text)
          FirstChild =svgdoc.getElementById("title").firstChild
          if (FirstChild==null){
         svgdoc.getElementById("title").appendChild(NewItem)
          }
         svgdoc.getElementById("title").replaceChild(NewItem,svgdoc.getElementById("title").firstChild)
        }
        
      function SetAxes(YText, XText)
        {
          NewItem =svgdoc.createTextNode(XText)
          FirstChild =svgdoc.getElementById("xaxis").firstChild
          if (FirstChild==null){
         svgdoc.getElementById("xaxis").appendChild(NewItem)
          }
         svgdoc.getElementById("xaxis").replaceChild(NewItem,svgdoc.getElementById("xaxis").firstChild)

          NewItem =svgdoc.createTextNode(YText)
          FirstChild =svgdoc.getElementById("yaxis").firstChild
          if (FirstChild==null){
         svgdoc.getElementById("yaxis").appendChild(NewItem)
          }
         svgdoc.getElementById("yaxis").replaceChild(NewItem,svgdoc.getElementById("yaxis").firstChild)
        }
        
      parent.addChartValue = AddChartValue
      parent.setTitle = SetTitle
      parent.setAxes = SetAxes
      parent.addDrill = AddDrill
      parent.finish = Finish
    
    
   