//
// SVGComboBox class
// Used to display a combo box given a list of items and coordinates on the screen.  
//
//
//*********Static Variables*******
// 
//******This array holds a list of all comboBox's.      
        _comboBoxList = new Array();
//
//********************************
//******Constructor args: iList -> list of items to be added to the comboBox.
//***************** args: x     -> the x coordinate on the screen
//***************** args: y     -> the y coordinate on the screen
        function SVGComboBox( iList, x, y)
	{

//*******Variables**************************

		this.itemList = new Array();
                for( i =0; i<iList.length; i++)
                {
			this.itemList[this.itemList.length] = new Array( iList[i], null, null, '');
                }
                
                this.combo = null;                      	// The SVGElement that holds the top rectangle of the combo box.
                this.box = null;                        	// The SVGElement that holds the bottom rectangle of the combo box.
       		this.isExpanded = true;				// Boolean to indicate whether the combo box is expanded.
                this.SelectedComboItem = 'Select...';   	// Current selected combo box item ( a string).
                this.SelectedComboIndex = -1;			// Current selected combo box index ( a number).
                this.x = x;					// X coordinate in the screen.
                this.y = y;					// Y coordinate in the screen
                this.ID = _comboBoxList.length;			// The ID of the comboBox, also used to access this comboBox in the array.
                this.tagComboBox = null;			// SVG <g> tag. The top tag that groups all other elements of the comboBox.
		this.tagCombo = null;				// SVG <g> tag. The combo tag that groups all top combo part elements.
		this.tagArrow = null;				// SVG <g> tag. The combo tag that groups all arrow combo part element.
		this.tagBox = null;		        	// SVG <g> tag. The combo tag that groups all bottom combo part elements.
		this.tagSelectedText = null;			// SVG <g> tag. The combo tag that groups the selectedText in the comboBox.


//*****Functions****************************

                this.expand = comboExpand;                      // Expands the combo box.
                this.collapse = comboCollapse;			// Collapses the combo box.
                this.selectItem = comboSelectItem;		// Selects a given item in the comboBox.
                this.getSelectedItem = comboGetSelectedItem;    // Gets the currently selected item within the combo box.
		this.getSelectedIndex = comboGetSelectedIndex;  // Gets the currently selected index within the combo box(i.e 0,1,2...).
                this.show = comboShow;				// Show the combo box if hidden.
                this.hide = comboHide;				// Hide the combo box if shown.
                this.createArrow = createArrow;			// Creates the two arrows of the combobox. Can be overriden to display something else
                this.greyItem = comboGreyItem;                  // It turns an element grey and disables the onlclick event.
                this.unGreyItem = comboUnGreyItem;              // It turns an element back to darkblue and enables the onlclick event.              

//*****Constructor**************************

                //Create root canvas for the comboBox.
                this.tagComboBox = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");	            

		//Create the arrow component
		this.tagArrow = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");	    	
                this.tagComboBox.appendChild(this.tagArrow);

                //Create the combo component
                this.tagCombo = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");
                this.tagComboBox.appendChild(this.tagCombo);

                //Create the box component
		this.tagBox = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");
                this.tagComboBox.appendChild(this.tagBox);

                //Create the box component
		this.tagSelectedText = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");
                this.tagComboBox.appendChild(this.tagSelectedText);

                this.createArrow('up');
                this.selectItem(this.SelectedComboIndex)
                this.collapse();


		// Create the top part of the combo.
                this.combo = SVGDocument.createElementNS("http://www.w3.org/2000/svg","rect");
                this.combo.setAttributeNS(null,'x', this.x);
                this.combo.setAttributeNS(null,'y', this.y);	
                this.combo.setAttributeNS(null,'rx', '3');	 
                this.combo.setAttributeNS(null,'width', 90);
                this.combo.setAttributeNS(null,'height', 15);
                this.combo.setAttributeNS(null,'fill', 'blue');
                this.combo.setAttributeNS(null,'fill-opacity', 0.05);
                this.combo.setAttributeNS(null,'stroke', 'darkblue');
                this.combo.setAttributeNS(null,'stroke-width', '0.3');

               

		// Create a plain white box that covers the bottom part of the comboBox.
		this.box = SVGDocument.createElementNS("http://www.w3.org/2000/svg","rect");
                this.box.setAttributeNS(null,'x', this.x);
                this.box.setAttributeNS(null,'y', this.y+16);	
                this.box.setAttributeNS(null,'rx', '3');	 
                this.box.setAttributeNS(null,'width', 90);
                this.box.setAttributeNS(null,'height', 15*this.itemList.length);
                this.box.setAttributeNS(null,'fill', 'white');
                this.box.setAttributeNS(null,'stroke', 'none');
                this.tagBox.appendChild(this.box);
               
                // For the number of items, create entries in the bottom part of the combo box.
		for( var i = 0; i < this.itemList.length; i++ )
                {
                        data = SVGDocument.createTextNode(this.itemList[i][0] );	// SVG text node.
                        text = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text");			// SVG node that will hold the SVG text node.
                        this.itemList[i][1] = text;					// Attach this SVG node object to the list of items, for future reference
                        text.setAttributeNS(null, 'font-family', 'Verdana');			// Set the font type.
                        text.setAttributeNS(null, 'font-size', '10');     			// Set font size.
                        text.appendChild(data);						// Attach SVG text node to a DOM node.

                	text.setAttributeNS(null,'x', this.x+5);				// Set X coordinate for this text item of the comboBox.
                        y = this.y + 12 + 15*(i+1);					// Work out where the Y corrdinate should be.
			text.setAttributeNS(null,'y', y);					// Set Y coordinate for this text item of the comboBox.
                	text.setAttributeNS(null,'width', 90);					// Set width of the comboBox.
                	text.setAttributeNS(null,'height', 15 );				// Set height of the comboBox.
                	text.setAttributeNS(null,'fill', 'darkblue');             		// Set the items fill color.    
                                                                	
                	this.tagBox.appendChild(text);					// Attach the DOM node to the bottom part of the comboBox.
 
                        // Add rows to the combo. This rectangle is there to cover the text so that it becomes unselectable.
                        row = SVGDocument.createElementNS("http://www.w3.org/2000/svg","rect");
                        this.itemList[i][2] = row;			
	                row.setAttributeNS(null,'x', this.x);
        	        row.setAttributeNS(null,'y', y-11);	
                	row.setAttributeNS(null,'rx', '3');	 
	                row.setAttributeNS(null,'width', 90);
        	        row.setAttributeNS(null,'height', 15);
                	row.setAttributeNS(null,'fill', 'white');
                        row.setAttributeNS(null,'fill-opacity', '0');
	                row.setAttributeNS(null,'stroke', 'darkblue');
                        row.setAttributeNS(null,'stroke-width', '0.3');

                        this.tagBox.appendChild(row);

			// Add look and feel to the row. When user hovers over the combo item it will change the font of the text.
                        // If user clicks on the text element, it will select the item clicked.
			row.setAttributeNS(null,'onmouseover', "BoxHandlerOnMouseOut('"+this.ID+"', '"+i+"')" ); 
                        row.setAttributeNS(null,'onmouseout', "BoxHandlerOnMouseOut('"+this.ID+"', '"+i+"')" ); 
                        // Save the onclick string so that we can switch it on or off for greying out any items in the list.
                        this.itemList[i][3] = "BoxHandlerOnClick("+this.ID+", "+i+")";
                        row.setAttributeNS(null,'onclick', this.itemList[i][3]);

			// Add a circle to the text element. It creates a bullet point effect.
        	        circle = SVGDocument.createElementNS("http://www.w3.org/2000/svg","circle");	
	                circle.setAttributeNS(null,'cx', this.x);
        	        circle.setAttributeNS(null,'cy', y-3);
	               	circle.setAttributeNS(null,'r', 1.5);
        	        circle.setAttributeNS(null,'fill', 'darkblue');

        	        this.tagBox.appendChild(circle);
                }     



         	// Add look and feel
                this.combo.setAttributeNS(null,'onmouseover', "ComboHandlerOnMouseOver('"+this.ID+"')");
 		this.combo.setAttributeNS(null,'onmouseout', "ComboHandlerOnMouseOut('"+this.ID+"')");
                this.combo.setAttributeNS(null,'onclick', "ComboHandlerOnClick('"+this.ID+"')");
                // Add it to 'combo' canvas
                this.tagCombo.appendChild(this.combo);

                
                _comboBoxList[_comboBoxList.length] = this;
		SVGDocument.documentElement.appendChild(this.tagComboBox);            
        }

//******MouseHandler Definitons***************

//***   Changes the stroke width of the top part of the combo when the user hovers over it.
	function ComboHandlerOnMouseOver(comboID)
	{
		_comboBoxList[comboID].combo.setAttributeNS(null,'stroke-width', '1');
	}

//***   Restores the stroke width of the top part of the combo when the user hovers out.
	function ComboHandlerOnMouseOut(comboID)
	{
		_comboBoxList[comboID].combo.setAttributeNS(null,'stroke-width', '0.3');
	}

//***   Collapses or expands the combo box depending on whether the combobox isExpanded.
	function ComboHandlerOnClick(comboID)
	{
		if( _comboBoxList[comboID].isExpanded )
		{
			_comboBoxList[comboID].collapse();
		}
		else
		{
			_comboBoxList[comboID].expand();
		}
	}

//***   When user clicks on a box element( text element that is part of the comboBox), this function selects the item clicked.
        function BoxHandlerOnClick(comboID, itemNum)
        {
        	_comboBoxList[comboID].selectItem(itemNum);
        }

//***   Changes the font of a item when user hovers over it.
	function BoxHandlerOnMouseOver(comboID, itemNum)
        {
		_comboBoxList[comboID].itemList[itemNum][1].setAttributeNS(null, 'font-size', '11' );
        } 

//***   Restores the font of a item when user hovers out.
	function BoxHandlerOnMouseOut(comboID, itemNum)
        {
		_comboBoxList[comboID].itemList[itemNum][1].setAttributeNS(null, 'font-size', '10' );
        } 
 


//*******Function Definitions*****************

//***   Sets the box or bottom part of the combo box to visible( i.e expands the comboBox) or shows the elements.
	function comboExpand()
	{              
		this.tagBox.setAttributeNS(null,'visibility', 'visible');
		this.createArrow('up');
                this.isExpanded = true;
	}

//***   Sets the box or bottom part of the combo box to hidden( i.e. collapses the comboBox) or hides the elements. 
	function comboCollapse()
	{
		this.tagBox.setAttributeNS(null,'visibility', 'hidden');
		this.createArrow('down');
                this.isExpanded = false;
	}

//***   Replaces the last selected item with the current one that the user has chosen.
	function comboSelectItem(itemNum)
	{
                if( itemNum != -1 )
                {
			this.SelectedComboItem = trimTo10Characters(this.itemList[itemNum][0]);
        		this.SelectedComboIndex = itemNum; 
                }
        	
		selItem = SVGDocument.createTextNode( this.SelectedComboItem );
                text = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text");
            	text.setAttributeNS(null,'x', this.x+5);
            	text.setAttributeNS(null,'y', this.y+12);
            	text.setAttributeNS(null, 'font-family', 'Verdana');
            	text.setAttributeNS(null, 'font-size', 10);
            	text.setAttributeNS(null,'fill', 'darkblue');
            	text.setAttributeNS(null,'width', 90);
            	text.setAttributeNS(null,'height', 15 );
            	text.appendChild(selItem);   
 
            	firstChild = this.tagSelectedText.firstChild;
            
            	if (firstChild==null)
            		this.tagSelectedText.appendChild(text);
            	else
            		this.tagSelectedText.replaceChild(text, this.tagSelectedText.firstChild);
                this.collapse();
	}

//***   Returns the currently selected item.
	function comboGetSelectedItem()
	{
		return this.SelectedComboItem;
	}

//***   Returns the currently selected index.
	function comboGetSelectedIndex()
	{
		return this.SelectedComboIndex;
	}

//***   Show comboBox.
	function comboShow()
	{
		this.tagComboBox.setAttributeNS(null,'visibility', 'visible');
	}

//***   Hide the comboBox.
	function comboHide()
	{
		this.tagComboBox.setAttributeNS(null,'visibility', 'hidden');
	}

//***   Creates the arrows of the comboBox. Can be overriden to diplay a different effect.
        function createArrow(direction)
        {
                // This while loop removes any elements from 'downArrow' <g> tag.
                firstChild = this.tagArrow.firstChild;
                while( firstChild != null  )
                {
                        this.tagArrow.removeChild(firstChild);	
	                firstChild = this.tagArrow.firstChild;                
                }
      
		line1 = SVGDocument.createElementNS("http://www.w3.org/2000/svg","polyline");
                line1.setAttributeNS(null,'x', this.x);
                line1.setAttributeNS(null,'y', this.y);
                line1.setAttributeNS(null,'fill', 'none');
                line1.setAttributeNS(null,'stroke', 'darkblue');
                line1.setAttributeNS(null,'stroke-width', '1'); 

                line2 = SVGDocument.createElementNS("http://www.w3.org/2000/svg","polyline");
                line2.setAttributeNS(null,'x', this.x);
                line2.setAttributeNS(null,'y', this.y);
                line2.setAttributeNS(null,'fill', 'none');
                line2.setAttributeNS(null,'stroke', 'darkblue');
                line2.setAttributeNS(null,'stroke-width', '1'); 

                if( direction == 'down' )
                {
			pointsStr1 = (this.x+78)+","+(this.y+7)+","+(this.x+82.5)+","+(this.y+11)+","+(this.x+87)+","+(this.y+7);
                        pointsStr2 = (this.x+78)+","+(this.y+4)+","+(this.x+82.5)+","+(this.y+8)+","+(this.x+87)+","+(this.y+4);
	                line1.setAttributeNS(null,'points', pointsStr1);
        	        line2.setAttributeNS(null,'points', pointsStr2);
                }
                else
                { 
                        
			pointsStr1 = (this.x+78)+","+(this.y+11)+","+(this.x+82.5)+","+(this.y+7)+","+(this.x+87)+","+(this.y+11);
                        pointsStr2 = (this.x+78)+","+(this.y+8)+","+(this.x+82.5)+","+(this.y+4)+","+(this.x+87)+","+(this.y+8);
	                line1.setAttributeNS(null,'points', pointsStr1);
        	        line2.setAttributeNS(null,'points', pointsStr2);
                }
              
                this.tagArrow.appendChild(line1);
                this.tagArrow.appendChild(line2);  
        }
//***   It turns an element grey and disables the onlclick event.
	function comboGreyItem(itemNum)
	{
 		var elementArr = this.itemList[itemNum];
		elementArr[1].setAttributeNS(null, 'fill', 'grey');
                elementArr[2].setAttributeNS(null, 'onclick', "");		

	}
//***   It turns an element back to darkblue and enables the onlclick event.
	function comboUnGreyItem(itemNum)
	{
 		var elementArr = this.itemList[itemNum];
		elementArr[1].setAttributeNS(null, 'fill', 'darkblue');
                elementArr[2].setAttributeNS(null, 'onclick', elementArr[3]);		

	}














