
                              
       StyleArray = new Array("pie1", 
                             "pie2", 
                             "pie3", 
                             "pie4", 
                             "pie5", 
                             "pie6", 
                             "pie7", 
                             "pie8", 
                             "pie9", 
                             "pie10", 
                             "pie11")  
                             
      ActiveSegment = "Pie"
          
      CurrentStyle = 0;
      
      SVGDocument = null
    
      BarChartHeight = 150
      BarChartWidth  = 250
      
      PieChartSize = 140
      
      MoveDistance = 40
    
      Values = new Array()
      Names = new Array()
      PieElements = new Array()
      BarElements = new Array()
      BarTexts = new Array()
      DeleteList = new Array()
      
      PieLabels = new Array()
      PieBox = new Array()
      DrillData = new Array()
      
      PieTotalSize = 0;
      BarTotalSize = 0;
      MaxSize = 0;
      
      AngleFactor = Math.pow(2, .5)
      
      Removed = true
      
      function ClearChart()
        {
          for (var I = 0; I < PieElements.length; I++)
            {
              ParentGroup1.removeChild(PieElements[I])
              ParentGroup2.removeChild(BarElements[I])
              SVGDocument.getElementById("labels").removeChild(Names[I])
              SVGDocument.getElementById("pielabels").removeChild(Names[I])
            }
            
          PieElements = new Array()
          BarElements = new Array()
          BarTexts = new Array()
          Values = new Array()
          Names = new Array()

	PieLabels = new Array()
      PieBox = new Array()
      DrillData = new Array()
      
          PieTotalSize = 0;
          BarTotalSize = 0;
          MaxSize = 0;

          if (!(Removed))
            {
              Grandparent1.removeChild(ParentGroup1)
              Grandparent2.removeChild(ParentGroup2)
            }
          Removed = true
          
        }
      
      function Initialize(LoadEvent)
        {
          SVGDocument = LoadEvent.target.ownerDocument;
          ParentGroup1 = SVGDocument.getElementById("slices")
          Grandparent1 = SVGDocument.getElementById("piechart")
          ParentGroup2 = SVGDocument.getElementById("bars")
          Grandparent2 = SVGDocument.getElementById("barchart")
        }
      
      function AddChartValue(Value, Name, Repress)
        {
          Value = Value * 1
          if ((Value <= 0) || (isNaN(Value)))
            {
		// I cant handle non-numeric or negative data here, so just get out.
              return;
            }
            
          Style = StyleArray[CurrentStyle]

          // Increment the style, and loop back to 0 if we have bust the limit.

          CurrentStyle++
          if (CurrentStyle >= StyleArray.length) CurrentStyle = 0          
          
          if (!(Removed))
            {
              Grandparent1.removeChild(ParentGroup1)
              Grandparent2.removeChild(ParentGroup2)
            }

          DeleteList[DeleteList.length] = false
          
          Values[Values.length] = Value * 1
          PieLabels[PieLabels.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text")
          Names[Names.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text")
          
          PieElements[PieElements.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","path")
          

          PieElements[PieElements.length - 1].setAttributeNS(null,"class", Style)

          PieElements[PieElements.length - 1].setAttributeNS(null,"onmouseover", "DisplayInfo('" + Name + "', '" + Value + "')")
          PieElements[PieElements.length - 1].setAttributeNS(null,"onmouseout", "DisplayInfo(' ', ' ')")
          ParentGroup1.appendChild(PieElements[PieElements.length - 1])
          
          BarElements[BarElements.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","path")
          BarTexts[BarTexts.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text")
          
          BarElements[BarElements.length - 1].setAttributeNS(null,"class",Style)
          
          BarElements[BarElements.length - 1].setAttributeNS(null,"onmouseover", "BarTexts[" + (BarTexts.length - 1) + "].style.setProperty('visibility', 'visible','')")
          BarElements[BarElements.length - 1].setAttributeNS(null,"onmouseout",  "BarTexts[" + (BarTexts.length - 1) + "].style.setProperty('visibility', 'hidden')")
          ParentGroup2.appendChild(BarElements[BarElements.length - 1])
          
          BarTexts[BarTexts.length - 1].setAttributeNS(null,"style", "text-anchor:end;font-weight:bold;font-size:13;visibility:hidden")
          BarTexts[BarTexts.length - 1].setAttributeNS(null,"x", "40")
          BarTexts[BarTexts.length - 1].appendChild(SVGDocument.createTextNode(Value))
          SVGDocument.getElementById("barsidetext").appendChild(BarTexts[BarTexts.length - 1])

          Names[Names.length - 1].appendChild(SVGDocument.createTextNode(Name + ""))
          Names[Names.length - 1].setAttributeNS(null,"transform", "rotate(45)")
          
	PieLabels[PieLabels.length-1].appendChild(SVGDocument.createTextNode(Name + ""))
	SVGDocument.getElementById("pielabels").appendChild(PieLabels[PieLabels.length - 1])

	PieBox[PieBox.length] = SVGDocument.createElementNS("http://www.w3.org/2000/svg","path")
	SVGDocument.getElementById("pielabels").appendChild(PieBox[PieBox.length - 1])
        PieBox[PieBox.length - 1].setAttributeNS(null,"class",Style)
          
          SVGDocument.getElementById("labels").appendChild(Names[Names.length - 1])
        }

// All done so add the items and display the chart
function Complete(){
	Refresh(); // Draw it!
        Grandparent1.appendChild(ParentGroup1)
        Grandparent2.appendChild(ParentGroup2)
}

// Add drilldata for enquiry drill downs
function AddDrill(Drill) {
	DrillData[DrillData.length]=Drill;
}        
// Actually draw the whole nine yards....
      function Refresh()
        {
          PieTotalSize = 0
          BarTotalSize = 0
          MaxSize = 0
          
          for (var I = 0; I < Values.length; I++)
            {
              PieTotalSize += Values[I]
              BarTotalSize++

              if (Values[I] > MaxSize)
                MaxSize = Values[I]
            }
          
          PieStart = 0
          BarStart = 0
	yStart = 0
	boxHeight = 10
	
          if (PieTotalSize > 0)
            for (var I = 0; I < Values.length; I++)
              {
                PieStart = DrawPieSegment(PieStart, Values[I] / PieTotalSize, PieElements[I], I)
                BarStart = DrawBarSegment(BarStart, Values[I] / MaxSize, BarElements[I], BarTexts[I], Names[I], I)
                
                PieLabels[I].setAttributeNS(null,"y",yStart +10 + (15* I))
                PieLabels[I].setAttributeNS(null,"x",15)
		// Draw the chart legend boxes
                PathData = "M 0 "+ (yStart + (15* I))+" L 0 "+ (boxHeight+yStart + (15* I))+" L 10 "+ (boxHeight+yStart + (15* I))+" L 10 "+ (yStart + (15* I))+"Z"
                PieBox[I].setAttributeNS(null,"d", PathData)

              }
          
          SVGDocument.getElementById("max").replaceChild(SVGDocument.createTextNode(MaxSize + ""), SVGDocument.getElementById("max").firstChild)
          SVGDocument.getElementById("min").replaceChild(SVGDocument.createTextNode("0"), SVGDocument.getElementById("min").firstChild)

        }
        
      
        
      function DrawBarSegment(Start, Height, Element, Text, Label, ID)
        {
          XOffset3D = 10
          YOffset3D = 5
        
          PathData = "M" + (Start + (BarChartWidth / BarTotalSize)) + ",0"
          PathData = PathData + "h" + (BarChartWidth / BarTotalSize * -1)
          PathData = PathData + "v" + (Height * BarChartHeight * -1)
          PathData = PathData + "l" + XOffset3D + ",-" + YOffset3D
          PathData = PathData + "h" + (BarChartWidth / BarTotalSize)
          PathData = PathData + "v" + (Height * BarChartHeight)
          PathData = PathData + "l-" + XOffset3D + "," + YOffset3D
          PathData = PathData + "v" + (Height * BarChartHeight * -1)
          PathData = PathData + "h" + (BarChartWidth / BarTotalSize * -1)
          PathData = PathData + "h" + (BarChartWidth / BarTotalSize)
          PathData = PathData + "l" + XOffset3D + ",-" + YOffset3D
          PathData = PathData + "l-" + XOffset3D + "," + YOffset3D
          
          Element.setAttributeNS(null,"d", PathData)
          Element.setAttributeNS(null,"onclick", "doDrill(" + ID + ")")
          Label.setAttributeNS(null,"x", Start / AngleFactor)
          Label.setAttributeNS(null,"y", Start * -1 / AngleFactor)
          
          Text.setAttributeNS(null,"y", (200 - BarChartHeight * Height))
          
          return Start + BarChartWidth / BarTotalSize
        }
        
      function DrawPieSegment(Start, Size, Element, ID)
        {
          PathData = "M0,0L" 
          PathData = PathData + PieChartSize * Math.sin(Start * Math.PI * 2) + "," + PieChartSize * Math.cos(Start * Math.PI * 2)
	if (Size >= .5) {
            PathData = PathData + "A" + PieChartSize + " " + PieChartSize + " 1 1 0 " + PieChartSize * Math.sin((Start + Size) * Math.PI * 2) + "," + PieChartSize * Math.cos((Start + Size) * Math.PI * 2)
	} else {
            PathData = PathData + "A" + PieChartSize + " " + PieChartSize + " 0 0 0 " + PieChartSize * Math.sin((Start + Size) * Math.PI * 2) + "," + PieChartSize * Math.cos((Start + Size) * Math.PI * 2)
	}
        PathData = PathData + "z"
         
          Element.setAttributeNS(null,"d", PathData)

	  Element.setAttributeNS(null,"onclick", "MoveSegment(evt, " + (Start + Size / 2) + ", true, " + ID + ")")

          if (Start > 0) {
//            Element.setAttributeNS(null,"onclick", "MoveSegment(evt, " + (Start + Size / 2) + ", true, " + ID + ")")
	}  else 
            {
              DeleteList[ID] = true;
//              Angle = Start + Size / 2
  //            X = MoveDistance * Math.sin(Angle * 2 * Math.PI)
    //          Y = MoveDistance * Math.cos(Angle * 2 * Math.PI)
      //        Element.setAttributeNS(null,"transform", "translate(" + X + "," + Y + ")")
        //      Element.setAttributeNS(null,"onclick", "MoveSegment(evt, " + ((Start + Size / 2) * -1) + ", false, " + ID + ")")
            }
          
          return Start + Size
        }
        
        function doDrill(ID){
	        parent.chartdrill(DrillData[ID])
        }
        
      function MoveSegment(MouseEvent, Angle, CanBeDeleted, ID)
        {
          Element = MouseEvent.target

          if (Angle < 0)
            {
              X = 0
              Y = 0
            }
          else
            {
              X = MoveDistance * Math.sin(Angle * 2 * Math.PI)
              Y = MoveDistance * Math.cos(Angle * 2 * Math.PI)
            }
            
          DeleteList[ID] = CanBeDeleted
          
          Element.setAttributeNS(null,"transform", "translate(" + X + "," + Y + ")")
          Element.setAttributeNS(null,"onclick", "MoveSegment(evt, " + (Angle * -1) + ", " + (!CanBeDeleted) + ", " + ID + ")")
          parent.chartdrill(DrillData[ID])
        }
        
      function DisplayInfo(Text, Value)
        {
          if (Text != " ") {
            Text = Text + " : "
            Percent = " (" + Math.round(Value / PieTotalSize * 10000) / 100 + "%)"
            }
          else
          {
            Percent = ""
          }
          NewItem = SVGDocument.createTextNode(Text +Value + Percent)
          firstChild = SVGDocument.getElementById("labelamount").firstChild
          if (firstChild==null){
          SVGDocument.getElementById("labelamount").appendChild(NewItem)
          }
          SVGDocument.getElementById("labelamount").replaceChild(NewItem, SVGDocument.getElementById("labelamount").firstChild)
        }
        
      function SetTitle(Text)
        {
          NewItem = SVGDocument.createTextNode(Text + "")
          firstChild = SVGDocument.getElementById("title").firstChild
          if (firstChild==null){
          SVGDocument.getElementById("title").appendChild(NewItem)
          }
          SVGDocument.getElementById("title").replaceChild(NewItem, SVGDocument.getElementById("title").firstChild)
        }
        
      function SetAxis(Text)
        {
          NewItem = SVGDocument.createTextNode(Text + "")
           firstChild = SVGDocument.getElementById("axis").firstChild
          if (firstChild==null){
          SVGDocument.getElementById("axis").appendChild(NewItem)
          }

          SVGDocument.getElementById("axis").replaceChild(NewItem, SVGDocument.getElementById("axis").firstChild)
          NewItem = SVGDocument.createTextNode(Text + "")
          firstChild = SVGDocument.getElementById("subtitle").firstChild
          if (firstChild==null){
          SVGDocument.getElementById("subtitle").appendChild(NewItem)
          }
          SVGDocument.getElementById("subtitle").replaceChild(NewItem, SVGDocument.getElementById("subtitle").firstChild)
        }
      
      function ShowPie()
        {
          ActiveSegment = "Pie"
          SVGDocument.getElementById("barchart").style.setProperty("visibility", "hidden","");
          SVGDocument.getElementById("subtitle").style.setProperty("visibility", "visible","");
          SVGDocument.getElementById("piechart").style.setProperty("visibility", "visible","");
          SVGDocument.getElementById("pieitemlabel").style.setProperty("visibility", "visible","");
          SVGDocument.getElementById("barsidetext").style.setProperty("visibility", "hidden","");
        }
        
      function ShowBar()
        {
          ActiveSegment = "Bar"  
          SVGDocument.getElementById("barchart").style.setProperty("visibility", "visible","");
          SVGDocument.getElementById("subtitle").style.setProperty("visibility", "hidden","");
          SVGDocument.getElementById("piechart").style.setProperty("visibility", "hidden","");
          SVGDocument.getElementById("pieitemlabel").style.setProperty("visibility", "hidden","");
          SVGDocument.getElementById("barsidetext").style.setProperty("visibility", "visible","");
        }
        
      parent.clearChart = ClearChart
      parent.addChartValue = AddChartValue
      parent.setTitle = SetTitle
      parent.setAxis = SetAxis
      parent.showBar = ShowBar
      parent.showPie = ShowPie    
      parent.addDrill = AddDrill
parent.complete = Complete

    