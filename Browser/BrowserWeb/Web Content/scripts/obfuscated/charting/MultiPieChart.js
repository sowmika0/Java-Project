//***
// --------- OrdinalLineGraph ------------
//
//           
//  The Multi Pie Chart
// 
//  It's made out of a number of Pie Charts sitting on top of each other.
//  Each piechart represents a level starting from level 0 up to n.
//  This type of a chart represents data in the same manner as a tree does, in a heirarchichal form.
//  So the first level represents the nodes the level is made from, the next level represents what 
//  each node of the parent level is made of, and it goes on up to the n number of levels.
//  The starting point of this script is at 'mpAddChartValue' where data is passed in a sequential order.
//  The data is gethered into an array structure called 'PieSegments' which holds all segments for further processing.
//   
//  An array structure is used to manage all the data manipulation. 'PieCharts' array is a three dimensional array which holds every segment
//  in its corresponding level. So the first dimension of 'PieCharts' holds the levels, the second holds the segments that belong
//  to that level and the third holds information about the segment itself. I.e. if we wanted to find out what the 
//  percentage value of segment 4 at level 3 is, we would use the following syntax: percVal = PieCharts[3][4][VALUE] 
//  Levels start from 0 being the top level to 1,2,3 and so on.
//  Every segment belongs to a group. At the first level every segment is a parent of any children below it, hence all the 
//  children below are part of the parents group.
//  To display the segments the svg element 'path' is used. This element draws any shape given a path line.
//  
//  SAR Reference: SAR-2005-05-15-0001      
// 
//****

// Constructor.
function MultiPieChart( area, xAttribute, yAttribute, zAttribute)
{
// -----------------Variables-----------------------------------------------------
	this.area = area;
	this.xAttribute = xAttribute;
	this.yAttribute = yAttribute;
	this.zAttribute = zAttribute;
	this.size = 0;
    // The legend of this graph.
    this.myLegend = null; 
// -----------------Methods--------------------------------------------------------
    this.init = multiPieChartInit;	
    this.create = multiPieChartCreate;
// -----------------Calls to initial internal routines-----------------------------
    this.init();
	this.create();    
}

//
function multiPieChartInit()
{
	if( _height > _width)
	{
		this.size = _width;
	}
	else
	{
		this.size = _height;
	}
	
	// Get the highest margin for this type of chart and use that value.
	var overallPieMargin = _topMargin;
	if( overallPieMargin < _bottomMargin ) overallPieMargin = _bottomMargin; 
	if( overallPieMargin < _rightMargin ) overallPieMargin = _rightMargin;
	if( overallPieMargin < _leftMargin ) overallPieMargin = _leftMargin;

	
	PieChartSize = ( this.size - overallPieMargin ) * 0.5;
	PieMinSize = ( this.size - overallPieMargin ) * 0.2;
	
	PieStartX = this.size * 0.5;
	PieStartY = this.size * 0.5;
	
	var translate = "translate(" + PieStartX + "," + PieStartY + ")";
	this.area.svgParent.setAttributeNS( null, "transform", translate);
		
}

//
function multiPieChartCreate()
{  
	var ds = _dataSet.seriesAt(0);
    for( var i = 0; i < ds.numOfDataItems; i++ )
    {
		di = ds.itemAt(i);
		if( di[ this.zAttribute] == null)
		{
			// If no level has been specified, assume the pie is single-level and create the level attr i.e. 1,2,3,4...
			di[ this.zAttribute] = i + 1;
		}
		// Is it the last value.
		if( i == ( ds.numOfDataItems -1 ) )
		{
			mpAddChartValue( di[ this.xAttribute], di[ this.yAttribute], true, di[ this.zAttribute]);  
		}
		else
		{
			mpAddChartValue( di[ this.xAttribute], di[ this.yAttribute], false, di[ this.zAttribute]);       	
		}
    } 
}
   
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
      
		   //// Global indexers for 'PieCharts' and 'PieSegments' array from 0 to 9.//////////////////////////////////

   VALUE = 0;         // Percentage Value (0.2 0r 0.1234 ...)

   LABEL = 1;         // The label ('Bonds' or 'Shares' ...)

   LEVEL = 2;         // The level id ('2.1.1' or '1.1.1' ...)

   LOCATION = 3;      // The accumulated value. I.e. we have a 0.2 segment which is the third segment of the Pie
                      // The first segment of the pie is 0.5 and the second segment is 0.3. The LOCATION value for the
                      // third segment is 0.8.

   PATHDATA = 4;      // The pathLine of the SVG shape.

   AMOUNT = 5;        // The actual amount of the segment (12,000 or 4000 or 100 ...)
        
   PRICOLOR = 6;      // The class used from the CSS ('pie1' or 'pie2' ...)

   PARENTVALUE = 7;   // The segment's parent percentage value (0.4 or 0.1 ...)
   
   RESERVED = 8;      // Reserved field for future use.
          
   GROUPID = 9;       // Segments belong to a group ( '1' or '2' or '3' ... )   

   ELEMENT = 10;      // The svg element pointer. Used to modify the attributes of a segment.

   BLANKLEVEL = 11;   // If this flag is set, the segment belongs to a all blank level, therefore 
     
        // The starting point.
        // This method is called from HTML code but also from within the code. The method adds a data point every time
        // it is called until the last one, and then does the processing. 
        // Arguments passed in: 'amount' is the segments numerical amount.
        //                      'label' is the segments text label. 
        //                      'isLastValue' holds true if it is the last value passed, otherwise false.   
        //                      'level' specifies the level and which parent the segment belongs to.
        //                       i.e. "2.1" represents segment '1' under the parent '2'
        //                            "1.2.2" represents segment '2' under parent '1.2'
        //                            "1" represents segment 1 at level 0. 
        //
        function mpAddChartValue(amount,label,isLastValue, level)
        {
             // Initialise local variables.
             var location = 0; var pathData = 0; var parentValue = 0; var style = ''; element = null;

             // Obtain the primary color for this segment.
             priColor = getColor(level);  
             // Get the first parent of the segment, this will be a group id for this segment.
             groupID = getFirstParent(level);

             // Create a new entry in the array with the new segment.
             PieSegments[PieSegments.length] = new Array(0,label, level, location, pathData, amount, priColor, parentValue,style,groupID,element);

             // The following statement makes a copy of the original elements as they come in.
             // When a drilldown happens, 'PieSegments' gets altered, when drilling back up to
             // the orignial data 'PieSegmentsCopy' is used to recover the original data.
	     if(InitialPie) PieSegmentsCopy[PieSegmentsCopy.length] = new Array(0,label, level, location, pathData, amount, priColor, parentValue,style,groupID,element);
    
                          
                        
             if( isLastValue )
             {
                
                 if(Valid) determineLevel();
              
                 if(Valid) calcPercentageValues();

                 if(Valid) createPieChartsStructure(); 
                
                 if(Valid) calculateLevelSizeAndOpacity();
 
		 		 if(Valid) calculateSvgPaths();            
                
                 if(Valid) paint();

                 if(Valid) calcLevelTotal();                
 
                 if(Valid) displayCircle('white',30, 1, true);

                 InitialPie = false;
             }         
        }
 

		//////////HELPER METHODS///////////////////////////////////////////////////////
         
        // Returns the level number based on the
        // given 'level' string. I.e. for "1.2.4" returns 2 or "1.2.3.1" returns 3 or "1" returns 0.
        function getLevel(level)
        {
             str = new String(level + "");
             levelArray = str.split(".");
             return levelArray.length - 1 ;
        }
    
        // Returns the first parent given
        // the 'level' string. I.e. for "2.3.4"
        // returns "2", for "1" returns "1".
        function getFirstParent(level)
        {
             str = new String(level + "");
             levelArray = str.split(".");
	     return levelArray[0];	
        }

        // Returns the last parent 
        // given the 'level' string.
        // I.e. for "1.2.3" returns "2"
        // for "1.3.4.5" returns "4"
        // if 'level' has no children
        // then returns -1.
        function getLastParent(level)
        {
	     str = new String(level + "");
             levelArray = str.split(".");
             if( levelArray.length > 1 )
                 return levelArray[levelArray.length-2];	
             else
                 return -1;
        }

        // Returns all the above parents
        // given the 'level' string. I.e. 
        // for "1.2.3.4" returns "1.2.3" 
        // for "2.1.3.1" returns "2.1.3"
        function getAboveParents(level)
        {
	     str = new String(level + "");
             levelArray = str.split(".");
             retStr = '';
             for( var i = 0; i < levelArray.length-1; i++ )
             {
                  retStr += levelArray[i];
                  if( i < levelArray.length-2 )
                        retStr += '.';
             } 
             return retStr;
        }

        // Given 'elementLevelStr' returns the 
        // absolute value of segment's 'AMOUNT'.
        function getElementValue( elementLevelStr )
        {
                value = 0;
                for( var i=0; i < PieSegments.length; i++ )
                {
                        if( elementLevelStr == PieSegments[i][LEVEL] )
                        {
                             value = PieSegments[i][AMOUNT];
                             i = PieSegments.length;
                        }
                }
                return abs( value );
        }

        // Given 'elementLevelStr' returns the 
        // segment's 'LABEL'.
        function getElementLabel( elementLevelStr )
        {
                value = '';
                for( var i=0; i < PieSegments.length; i++ )
                {
                        if( elementLevelStr == PieSegments[i][LEVEL] )
                        {
                             value = PieSegments[i][LABEL];
                             i = PieSegments.length;
                        }
                }
                return value;
        }

        // Given 'elementLevelStr' returns the 
        // segment's 'VALUE'.  
        function getElementPerc( elementLevelStr )
        {
                value = 0;
                for( var i=0; i < PieSegments.length; i++ )
                {
                        if( elementLevelStr == PieSegments[i][LEVEL] )
                        {
                             value = PieSegments[i][VALUE];
                             i = PieSegments.length;
                        }
                }
                return  value;
        }

        // Given the 'level' string returns
        // true if 'level' is a child of a parent
        // otherwise returns false. I.e. for "2.3" 
        // returns true, for "2" returns false.
        function isChild(level)
		{
			str = new String(level + "");
            levelArray = str.split(".");
            if( levelArray.length > 1 )
                 return true;
            else
                 return false;
        }

        // Returns the absolute value of 'value'.
        function abs(value)
        {
			if( value < 0 )
                return value * (-1);
            else 
                return value * 1;
        }

        // Returns the next color from _seriesColors if 
        // the segment is a level '0' segment otherwise
        // it is a child of a parent hence we 
        // locate the parent in 'PieSegments' and return
        // the parent's color.
        function getColor(level)
        {
             priColor = '';

             levelNum = getLevel(level);
             if( levelNum == 0 )
             {
                 priColor = _seriesColors[CurrentStyle];
                 CurrentStyle++;
                 if(CurrentStyle == _seriesColors.length ) CurrentStyle = 0;
             }
             else
             {
  		 		 firstParent = getFirstParent(level);
		         for(var i = 0; i < PieSegments.length; i++)
		         {   
		             if( firstParent == PieSegments[i][LEVEL])
		             {
		                priColor = PieSegments[i][PRICOLOR];
		                i = PieSegments.length; //break from the loop
		             }
		         }		 
             }
             return priColor;
		}

        // Removes the first parent from the 'level' string
        function cropFirstParent(level)
        {
                str = new String( level );
                firstDotIndex = str.indexOf(".",0) + 1;
                return str.substring(firstDotIndex,str.length);
        }

		////////////END OF HELPER METHODS/////////////////////////////////////////////////        



		///////////ROUTINE METHODS/////////////////////////////////////////////////////////
        
        
        // Returns all the segments that belong to 
        // 'groupId' except for the top parent.
        // The returned segments, thier first parent is croped off in order to get rid 
        // of the higher level.  
        function getElementGroup(groupId)
        {
               drillDownElements = new Array();
               // Added to ignore blank elements
               drillElemNoBlank = new Array();
               // Go through the currently displayed segments
	       	   for( var i = 0; i < PieSegments.length ; i++ )
               {
                    // If a segment is of 'groupId' and is a child of some parent
                    // add it the list of drilldownelements.
                    if( groupId == PieSegments[i][GROUPID]  && isChild(PieSegments[i][LEVEL]) )
                    {     
                          drillDownElements[drillDownElements.length] = PieSegments[i];
                          drillDownElements[drillDownElements.length-1][LEVEL] = cropFirstParent(drillDownElements[drillDownElements.length-1][LEVEL]);
                    }
               }
              
               // The following section of this function tries to figure out how many levels are there that contain
               // all blank elements. 'blankLevel' is set to that number of levels.
               blankLevel = 0;
               // Start at the last level.
               levelAt =  getLevel( drillDownElements[drillDownElements.length-1][LEVEL] );
               // Go through drilldown elements to find out how many 'all' blank levels there are.
               for( var i = drillDownElements.length-1; i >= 0; i-- )
               {
					levelAtCompare = getLevel( drillDownElements[i][LEVEL] );
                    if( levelAt != levelAtCompare )
                    {
                        levelAt = levelAtCompare;
                            blankLevel += 1;
                    }

					if(drillDownElements[i][LABEL] != "B.L.A.N.K")
                    {
						i = -1;
                    }
               }
	       	   // Highest Level there is.
               highLevel = getLevel( drillDownElements[drillDownElements.length-1][LEVEL] );
               // The level up to which there are real segments and not 'all' blank levels.
               filterLevel = highLevel - blankLevel;
               for( var i = 0; i < drillDownElements.length; i++ )
               {
					levelAt = getLevel( drillDownElements[i][LEVEL] );
                    if( levelAt <= filterLevel )
                         drillElemNoBlank[drillElemNoBlank.length] = drillDownElements[i];
                    else
                        break;

               }                
               return drillElemNoBlank;
        }

        // Calculates the total amount of the first level.
        // Sets the 'CurrentLevelTotal' global variable to the 
        // calculated total.
        function calcLevelTotal()
        {
              levelTotal = 0;
              
              for( var i=0; i < PieCharts[0].length; i++ )
              {
				levelTotal += PieCharts[0][i][AMOUNT];
              }
              CurrentLevelTotal = levelTotal;
              
        }
  
        // Based on the segment's amount and level it belongs to,
        // this routine calculates the segment's percentage within the pie.
        function calcPercentageValues()
        {
             
                firstLevelTotal = 0;
                // Find out what the total is for the first level.
				for( var i=0; i < PieSegments.length; i++ )
                {
                    // If a segment is not a child of a parent, it's a first level segment.
					if( !isChild(PieSegments[i][LEVEL]) )
                    {
                        firstLevelTotal += abs( PieSegments[i][AMOUNT] );
                    }
                }
             
                // Calculate percentages for the first level using the total.
                for( var i=0; i < PieSegments.length; i++ )
                {
                    // If a segment is not a child of a parent, it's a first level segment.
					if( !isChild(PieSegments[i][LEVEL]) )
                    {
                        // 'firstLevelTotal' is total amount of the first level.
                        // To obtain the percentages each first level segment is
                        // divided by the 'firstLevelTotal'.                
		 				PieSegments[i][0] = abs( PieSegments[i][AMOUNT] ) / firstLevelTotal;
                    }
                }

                percValue = 0;
                // Calculate percentages for all level except the first.
				for( var i=0; i < PieSegments.length; i++ )
                {
		        	// If the segment is a child, it's not a first level segment.	
					if( isChild(PieSegments[i][LEVEL]) )
                    {
                        // Retrive the parent's id
						theParent = getAboveParents( PieSegments[i][LEVEL] );

                        // Get the 'AMOUNT' for the retrieved id.
                        parentValue = getElementValue( theParent );

                        // Get the 'VALUE' for the retrieved id.  
                        parentPerc = getElementPerc( theParent );

                        // Formula to calculate the percentage value for this segment.
                        percValue = abs( PieSegments[i][AMOUNT] ) / parentValue;

                        // Calculate the real percentage value for this segment, in relation to it's parent. 
                        PieSegments[i][VALUE] = parentPerc * percValue;			
                    }
                }       
        }   
       
        // This method creates 'PieCharts' from 'PieSegments'
        // It allocates each segment into it's corresponding level.
        function createPieChartsStructure()
        {
            PieCharts = new Array(Level + 1);
            
            for(var i = 0; i < PieCharts.length; i++)
			{
				PieCharts[i] = new Array();
            }  

			for(var i = 0; i < PieSegments.length; i++)
            {
				currLevel = getLevel( PieSegments[i][LEVEL] + "");
                PieCharts[currLevel][ PieCharts[currLevel].length ] = PieSegments[i];
            }			
        } 
 
        // Goes through all the segments and
        // determines which has the highest level
        // hence sets 'Level' to that number. 
        function determineLevel()
        {
            Level = 0;
            localLevel = 0;
            var currLevel = 0;
            for(var i = 0; i < PieSegments.length; i++)
            {
                str = PieSegments[i][LEVEL] + "";
               	currLevel = getLevel(str);
                if( currLevel > Level)
                {
    	          	Level = currLevel;
                    if( PieSegments[i][LABEL] != "B.L.A.N.K" )
                    {
						localLevel = currLevel;   
                    }
				}
            }
            
            // if 'Level' is zero then there is only one Pie chart to display
            // hence the 'PieMinSize' becomes  'PieChartSize'
            if( Level == 0 )
            {
                 PieMinSize = PieChartSize;
                 MultiLevelPieChart = false;
            }
            else
            {
         	     MultiLevelPieChart = true;
            }
        }

        // This routine calculates for each level or pie chart,
        // its diameter and the opacity of the color used.
        function calculateLevelSizeAndOpacity()
        {
             // 'LevelSizes' and 'LevelOpacity' are arrays which
             // hold information about Pie levels.
             // I.e. 'LevelSizes[0]' holds the diameter for Level 0.
             LevelSizes = new Array(Level + 1);
             LevelOpacity = new Array(Level + 1);

             // Formula that calculates how much should the next level diameter be incremented by.
             nextLevel = (PieChartSize - PieMinSize) / Level;

             // Formula that calculates how much should the next level opacity be decremented by.
             nextOpacity = 1 / (Level + 1);

	     	 // Start off at 'PieMinSize'           
             LevelSizes[0] = PieMinSize;

             // Start off at '1' full opacity.
             LevelOpacity[0] = 1;

             // Go through the arrays, populate the arrays with
             // level information.
             for( var i = 1; i < LevelSizes.length; i++ )
             {
				LevelSizes[i] = LevelSizes[i-1] + nextLevel;
                LevelOpacity[i] = LevelOpacity[i-1] - nextOpacity;
             }
        }
        
        // This routine calculates every segment's draw path.
        function calculateSvgPaths()
        {
           // Nested for loop which goes through every PieChart in 'PieCharts' array
           // Then through every segment in a PieChart and calculates where the segment should be drawn.
           for( var p = 0; p < PieCharts.length; p++ )
           {
                Location = 0;
                PathData = 0;
                for(var i = 0; i < PieCharts[p].length; i++)
        		{
                    // 'p' indicates a level, set the 'PieChart' diameter to the level diameter size. 
                    PieChartSize = LevelSizes[p];

                    //'Location' is a variable used to keep track of where the last segment was drawn.
					PieCharts[p][i][LOCATION] = Location;

                    // 'draw' is used to obtain the 'PathData' based on the last location and the percentage value. 
					draw(PieCharts[p][i][LOCATION],PieCharts[p][i][VALUE]);

                    // Set 'PathData' as this segment's draw path.
					PieCharts[p][i][PATHDATA] = PathData;
				}
           }
        }

        // Paint is used to add elemtents to the SVG canvas.
        // Up to this point we processed the data into a data structure.
        // Now we paint based on that information.
        function paint()
        {
        	for( var p = PieCharts.length -1 ; p >= 0; p-- )// p is used to identify the level currently indexed.
          	{
	            // Levelsize fot level p.
	            levelSize = LevelSizes[p];
	
	            // display a white circle with levelsize diameter, for better looking graphics.
	            displayCircle('white', levelSize, 1, false);  
	
	            node = null;
	            // Go through this level's segments and add them to 'pieSegments' canvas. 
	            for(var i = 0 ; i < PieCharts[p].length ; i++)// i is used to identify the segment in the level currently indexed.
	            {
	               
					if(PieCharts[p][i][LABEL] != "B.L.A.N.K") 
	                {
	                       
		                // 'path' element used to draw the segment.
						node = _svgDocument.createElementNS("http://www.w3.org/2000/svg","path");
	
		                // 'd' is where SVG expects to find the pathdata.
	        	        node.setAttributeNS(null,"d", PieCharts[p][i][PATHDATA] + "");
	
		                // Retrieve the level number for this segment.
	        	        levelNum = getLevel( PieCharts[p][i][LEVEL] + "");
	
		                // Obtain an opacity value for this level.
	        	        levelOpacity = LevelOpacity[levelNum];
	
		                node.setAttributeNS(null,"fill", PieCharts[p][i][PRICOLOR] );
		                node.setAttributeNS(null,"stroke", "white" );
		                node.setAttributeNS(null,"stroke-width", "0" );
	        	        node.setAttributeNS(null,"fill-opacity", levelOpacity );

		                // Get the first parent of this segment.
	        	        firstparent = getFirstParent( PieCharts[p][i][LEVEL] + "");
	
		                // Set the event for a onclick to 'determineActionSVG' at level 'p' with parent as 'firstParent' and this segments groupid.
	        	        node.setAttributeNS(null,"onclick", "determineActionSVG('"+p+"', '"+firstparent+"', '"+PieCharts[p][i][GROUPID]+"')" );
		
	        	        // Get 'pieSegment' canvas.    
	                	SVGElement = _svgDocument.getElementById('graphArea');
	
		                // Add the svg element to 'PieCharts' for future reference.
	        	        PieCharts[p][i][ELEMENT] = node;
	
		                // add a node to it.
	        	        SVGElement.appendChild(node);
	               }
	       	    }
          	}
        }
 
        function createComboBox()
		{
                
                itemList = new Array("Zoom In", "Display Legend");
				comboMain = new SVGComboBox( itemList, 0, 5);
                if( !MultiLevelPieChart )
                {
                	comboMain.greyItem(0);
				}
		}		

        // Determines which action to perform when clicking on the chart.
        function determineActionSVG(level, firstParent, groupId)
        {
               	if( comboMain.getSelectedIndex() == 0 )
                {
					doDrillDown(groupId);
                    if( level == 1 )
                    {
						comboMain.greyItem(0);
						comboMain.selectItem(1);
					}
                }
                else if( comboMain.getSelectedIndex() == 1 )
                {
                        dispLegend(level, firstParent);
                }  
        }
 
        // Drill down the multi pie chart based on the given groupid     
        // When drilling down we are actually re-creating the whole multi piechart
        // with only a sub-set of all the elements. Only the ones with groupid.
        function doDrillDown(groupId)
        {
            // If the Pie is Multi Level Chart then do the drillDown.
	    	if( MultiLevelPieChart )
            {

                // Get the elements that will appear on the drill down, based on group id.
                drillDownElements = getElementGroup(groupId);
                // Clear and re-initialise.
                clearPie();
                clearLegend();
                //clearBackButton();
                PieSegments = new Array();
                PieCharts = null;
                PieChartsLabels = null;
                CurrentStyle = 0;
            
                // Go through the drilldownelements, extract the information
                // needed to simulate another multi pie chart.
                for( var i = 0; i < drillDownElements.length ; i++ )
                {
                        amount = drillDownElements[i][5];
                        label = drillDownElements[i][1];
                        if( i == (drillDownElements.length - 1) )
                         	isLastValue = true;
                        else
                                isLastValue = false;
                        level = drillDownElements[i][2];
                        mpAddChartValue(amount,label,isLastValue, level);
                } 
                // This button is there to drill back up to the original Multi Pie Chart.  		               
                createBackButton();
             }
        }

        // Creates a back button when drilling down the Multi Pie Chart.
        // If we click on it, it drills back up to the original Multi Pie Chart.
        function createBackButton()
        {
			if( backButton == '' )
			{
			    	arrow = _svgDocument.createElementNS("http://www.w3.org/2000/svg","path");
			     	arrowPath = "M15,3L7,10L12,10L12,12L18,12L18,10L23,10Z";
	      			arrow.setAttributeNS(null,'d', arrowPath);
			        arrow.setAttributeNS(null,'fill', 'darkblue');
	        		arrow.setAttributeNS(null,'fill-opacity', 0.05);
		       		arrow.setAttributeNS(null,'stroke', 'darkblue');
		        	arrow.setAttributeNS(null,'stroke-width', '1');
	
					backButton = new SVGButton( '', arrow, 475, 5, 30, 15); 
	        	    backButton.button.setAttributeNS(null,'onclick', "drillToOriginalPieChart()");
	        }
			else
            {
				backButton.show();
            }                
        }

        // Clears any nodes in the 'backButton' canvas.
        function clearBackButton()
        {
			backButton.hide();
            comboMain.unGreyItem(0);
        }
        
        // Same funtionality as 'doDrillDown'. Uses all the elements originaly passed 
        // to drill back up to the original Multi Pie Chart.
        function drillToOriginalPieChart()
        {
                // Clear and re-initialise.
                clearPie(); 
                clearLegend();
                clearBackButton();
                PieSegments = new Array();
                PieCharts = null;
                PieChartsLabels = null;
                CurrentStyle = 0;
                PieChartSize = 150;
                PieMinSize = 70;
                PathData = 0;
                Location = 0; 
 
                // Note: Using PieSegmentsCopy to retrieve the original information.
                for( var i = 0; i < PieSegmentsCopy.length ; i++ )
                {
                    amount = PieSegmentsCopy[i][AMOUNT];
                    label = PieSegmentsCopy[i][LABEL];
                    if( i == (PieSegmentsCopy.length - 1) )
                     	isLastValue = true;
                    else
                            isLastValue = false;

                    level = PieSegmentsCopy[i][LEVEL];
					mpAddChartValue(amount,label,isLastValue, level);
                }   
        }
       
        // Clears any nodes from 'pieSegment' canvas.
        function clearPie()
        {
	       // This while loop removes any elements from 'pieSegment' <g> tag.
           firstChild = _svgDocument.getElementById("graphArea").firstChild;
           while( firstChild != null  )
           {
                _svgDocument.getElementById("graphArea").removeChild(firstChild);	
                firstChild = _svgDocument.getElementById("graphArea").firstChild;                
           }
        }
        
       

        // A function that creates and displays a circle.
        // isMidCircle, when set to true, displays the first level legend onclick.
        function displayCircle(color, radious, transparency, isMidCircle)
        {
			circle = _svgDocument.createElementNS('http://www.w3.org/2000/svg', 'circle');	
            circle.setAttributeNS(null,'cx', '0');
            circle.setAttributeNS(null,'cy', '0');
            circle.setAttributeNS(null,'r', radious);
            circle.setAttributeNS(null,'fill', color);
            circle.setAttributeNS(null,'stroke', color);
            circle.setAttributeNS(null,'stroke-width', '0');
            circle.setAttributeNS(null,'fill-opacity', transparency);
            circle.setAttributeNS(null,'stroke-opacity', transparency);
           
            if( isMidCircle )
            {
               //circle.setAttributeNS(null,"onclick", "dispLegend('0','-1')" );
               //circle.setAttributeNS(null,"onmouseover", "DisplayInfo('','Total',1,CurrentLevelTotal, false)");
               //circle.setAttributeNS(null,"onmouseout", "DisplayInfo('','','','', false)");
            }

			SVGElement = _svgDocument.getElementById('graphArea');
            SVGElement.appendChild(circle);
        }
        // Creates the 'PathData' string given 'Start', and
        // 'Size' is the percentage value of the segment. 
        // 'Location' and 'PathData' are global variables used by 'draw' and referenced from outside. 
        // How to use 'draw'?
        // 
        // Eg: 3 segments to display in a pie ( 20%, 30%, 50%). 
        // 
        //   pathDataArray = new Array(3); 
        //   Location = 0; 
        //   PathData = '';
        //   
        //   draw(Location,0.2);
        //   pathdataArray[0] = PathData;
        //   draw(Location,0.3);
        //   pathdataArray[1] = PathData;
        //   draw(Location,0.5);
        //   pathdataArray[2] = PathData;
        //
        // Note: Location is an accumulative variable, keeping track of where the next segment should be plotted. 
        function draw(Start, Size)
        {
        	//Start += PieStartX;
          	PathData = "";
          	//PathData = "M" + PieStartX + "," + PieStartY + "L";
          	PathData = "M0,0L";
          	PathData = PathData + PieChartSize * Math.sin(Start * Math.PI * 2) + "," + PieChartSize * Math.cos(Start * Math.PI * 2);
          	if (Size >= .5)
            	PathData = PathData + "A" + PieChartSize + " " + PieChartSize + " 1 1 0 " + PieChartSize * Math.sin((Start + Size) * Math.PI * 2) + "," + PieChartSize * Math.cos((Start + Size) * Math.PI * 2);
          	else
            	PathData = PathData + "A" + PieChartSize + " " + PieChartSize + " 0 0 0 " + PieChartSize * Math.sin((Start + Size) * Math.PI * 2) + "," + PieChartSize * Math.cos((Start + Size) * Math.PI * 2);
          	PathData = PathData + "z";
          	// Here 'Location' is updated and will be passed as an argument for the next segment.
	  		Location = Start + Size;
        }
    
    
    