//
// SVGComboBox class
// Used to display a button given a list of items and coordinates on the screen.  
//
//
//*********Static Variables*******
// 
        _buttonList = new Array();
//
//********************************

        function SVGButton( name, buttonImage, x, y, width, height)
	{

//*******Variables**************************
                this.ID = _buttonList.length;
		this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.name = name;
                this.buttonImage = buttonImage;
                this.button = null;
                this.tagButton = null;
                this.tagName = null;

//*****Functions****************************

		this.show = buttonShow;
		this.hide = buttonHide;


//*****Constructor**************************
		
                //Create root canvas for the comboBox.
                this.tagButton = SVGDocument.createElementNS("http://www.w3.org/2000/svg","g");  
             
		//Add button image if there is one.
		if( this.buttonImage != '' )
                {
                        this.buttonImage.setAttributeNS(null, "transform", "translate("+x+","+y+")" );
			this.tagButton.appendChild(this.buttonImage);
                }

                //Add button name if there is one.
                if( this.name != '' )
                {
			data = SVGDocument.createTextNode(this.name);
                        this.tagName = SVGDocument.createElementNS("http://www.w3.org/2000/svg","text");
                        this.tagName.setAttributeNS(null, 'font-family', 'Verdana');
                        this.tagName.setAttributeNS(null, 'font-size', '9');  
                        this.tagName.setAttributeNS(null, "transform", "translate("+(x+4)+","+(y+11)+")" );  
			this.tagName.setAttributeNS(null,'fill', 'darkblue');
                        this.tagName.appendChild(data);
			this.tagButton.appendChild(this.tagName);
                }

                // Create a rectangular, in a button shape.
                this.button = SVGDocument.createElementNS("http://www.w3.org/2000/svg","rect");
                this.button.setAttributeNS(null,'x', this.x);
                this.button.setAttributeNS(null,'y', this.y);	
                this.button.setAttributeNS(null,'rx', '3');	 
                this.button.setAttributeNS(null,'width', this.width);
                this.button.setAttributeNS(null,'height', this.height);
                this.button.setAttributeNS(null,'fill', 'darkblue');
                this.button.setAttributeNS(null,'stroke', 'darkblue');
                this.button.setAttributeNS(null,'stroke-width', '0.3');
                this.button.setAttributeNS(null,'fill-opacity', 0.05);
                this.tagButton.appendChild(this.button);

                // Add look and feel
                this.button.setAttributeNS(null,'onmouseover', "ButtonOnMouseOver('"+this.ID+"')");
 		this.button.setAttributeNS(null,'onmouseout', "ButtonOnMouseOut('"+this.ID+"')");
            
                _buttonList[_buttonList.length] = this;
                SVGDocument.documentElement.appendChild(this.tagButton)
        }

//******MouseHandler Definitons***************

	function ButtonOnMouseOver(buttonID)
        {
                _buttonList[buttonID].button.setAttributeNS(null,'stroke-width', '1');
	}
	 
	function ButtonOnMouseOut(buttonID)
        {
		_buttonList[buttonID].button.setAttributeNS(null,'stroke-width', '0.3');		
	}

//*******Function Definitions*****************

	function buttonShow()
        {
		this.tagButton.setAttributeNS(null,'visibility', 'visible');
        }

	function buttonHide()
        {
		this.tagButton.setAttributeNS(null, 'visibility', 'hidden' )
        }