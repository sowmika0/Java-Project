/** 
 * @fileoverview  Deals with all of the javascript functions used to submit requests
 *
 */


/**
 * Builds a request and then submits it.
 * @param {String} routineName The name of the routine to run - if a utility routine
 * @param {String} cmdName The command to pass into the routine
 * @param {String} formName The name of the form to submit
 * @param {String} windowName The name of the window
 * @param {String} requestType The request type
 * @param {String} title The title for the window
 * @param {int} width The width of the window, in pixels
 * @param {int} height The height of the window, in pixels
 * @param {boolean} clearParams Flag indicating if certain field values should be cleared before the submit.
 * @return {void}
 */
  requestRoutineName = null; //Initialising the request routine variable
function buildUtilityRequest(routineName,cmdName,formName,windowName,requestType,title,width,height,clearParams)
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = null;
	if( formName == "" || formName == "undefined")
	{
		form = FragmentUtil.getForm( "generalForm");
	}
	else
	{
		form = FragmentUtil.getForm( formName);
	}
	
	//check to see if any of the following params are blank
	//and if so then set a default
	if(requestType=="")
	{
		requestType = _UTILITY__ROUTINE_;
	}
	if(title=="")
	{
	    title = "Processing....";
	}
	if(height=="")
	{
		height = "500px";
	}
	if(width=="")
	{
		width = "800px";
	}
	
		
	if(form!=null)
	{
		// Application or application and function
		form.routineName.value=routineName;
		form.routineArgs.value=cmdName;
		form.requestType.value = requestType;
		
		// Multi document download to have parent window name
 		if ( routineName == "OS.MULTI.DOWNLOAD" )
 		{
 			// Create new element and add in the form to send the parent window name to server.
 			var newElement = document.createElement("input");
 			newElement.type = "hidden";
 			newElement.name = "MultiDocumentEnqParentWindow";
 			newElement.value = FragmentUtil.getWindowOrFragmentName(); // Should be actual window or fragment name.
 			form.appendChild(newElement);
 		}
		
		//Fast path enquiry to have parent window name 
  		if (routineName == "OS.FAST.PATH")
  		{
  			//  new element added in the form to send the parent window reference to server while drildown / dropdown
  			if(form.EnqParentWindow != null || form.EnqParentWindow != '')
  			{
				form.EnqParentWindow.value = window.name;	
  			
  			}else
			{
				var newElement = document.createElement("input");
  				newElement.type = "hidden";
  				newElement.name = "EnqParentWindow";
  				newElement.value = window.name;	// Don't use getWindowOrFramentName() in this case we want the containing window.
  				form.appendChild(newElement);
  			  			  
  			}
		}
        	//Parent window name to be updated with TT.PASSBOOK.PRINT window name for passbook printing request
        if ( routineName == "OS.GET.PASSBOOK.PRINT.DATA")
		{
			form.WS_parentWindow.value = window.name; 
		}
	
		// If a toolbar wants to be in the same window, it will set this
		if (getnoNewWindow() == "")
		{	
			
			// Check if the window name GLOBAL has been set, is so then use it
			if (windowName_GLOBAL != "")
			{
				windowName = windowName_GLOBAL;
			}
			//if the window name is blank then create a new window
			if ((windowName == "")||(windowName == "undefined"))
			{
				// Pass the command name in to createResult window so that
				// we can work out the composite screen target.
				// routineName is assigned to the variable.
				requestRoutineName = routineName;
				windowName = createResultWindow( cmdName, width, height );	
			}
			
			if (Backevent == true)
			{
				windowName = backWindow;
			}
			MenuStyle = getFieldValue("Menustyle");
			if (recordHistory_GLOBAL == true && MenuStyle == "POPDOWN" && Backevent == false)
			{
				if(window.BrowserHistory)
				{
					var dataArray = {};
					dataArray.command = recordHistroyDataArray_GLOBAL.menutarget;
					dataArray.type = recordHistroyDataArray_GLOBAL.menutype;
					dataArray.windowname = windowName;
					//recordHistroyDataArray_GLOBAL.windowname = windowName;
					BrowserHistory.recordHistory("popmenu", recordHistroyDataArray_GLOBAL);
				}
			}

			var savedTarget = form.target;
			var savedWindowName = form.windowName.value;
			
			form.windowName.value = windowName;
			form.target=windowName;
		}		
		// Clear the compScreen field if we are not in a composite screen
		var savedCompScreen = form.compScreen.value;
		
		if ( FragmentUtil.isCompositeScreen() == false )
		{
			setFormFieldValue( formName, "compScreen", "" );
		}
		//if from toolbar and contains target of a composite screen
		if((getnoNewWindow()) && (windowName))
		{
			form.target = windowName;
			form.windowName.value = windowName;
		}
		
		// If we are creating a composite screen then set the parent composite screen
		// web server field name as it may be needed later for the REPLACE.ALL frame attribute
		if ( routineName == "OS.GET.COMPOSITE.SCREEN.XML" )
		{
			form.WS_parentComposite.value = windowName;
		}
		
		// Submit the request to the Browser Servlet
		FragmentUtil.submitForm(form);
		if (formName != "")
		{
		   form=FragmentUtil.getForm(formName);
		}		
		// Reset composite screen value
		if (form.compScreen.value == "")
		{
		form.compScreen.value = savedCompScreen;
		}
		
		// IIf a toolbar wants to be in the same window, it will set this
		if (getnoNewWindow() == "")
		{		
			// Reset window name and target
			form.target = savedTarget;
			form.windowName.value = savedWindowName;
		}	
		
		if (clearParams!=false){	
			clearParameters(formName);
		}

		// Clear attributes from current form for furthur requests.
 		// For example after multi download, if single download occurs problem will exist.
 		if ( routineName == "OS.MULTI.DOWNLOAD")
 		{
 			form.routineName.value="";
 			form.routineArgs.value="";
 			form.requestType.value = "";
 		}

		//We only want to do this if we want to store the name of the current window for use later on
		if ( getWindowVariableMarker() == "true" )
		{
			storeWindowVariable(windowName);
		}
	}
}


/**
 * Submits the main menu request
 * @param {String} cmdname The command
 * @return {void}
 */
function submitgeneralForm(cmdname)
{
	doContext = window.document.getElementById("doContext");
	if (doContext == null)
	{
		// Create a new window for the result
		windowName = createResultWindow( cmdname, 250, 250 );

		// get the 'generalForm' reference, adjusting for Fragment name as necessary
		var genForm = FragmentUtil.getForm("generalForm");

		genForm.target=windowName;
		
		// Save the new window name on the form for server locking purposes
		genForm.windowName.value = windowName;
	}
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(genForm);
}



/**
 * Blanks the parameters for the next transaction
 * @param {String} formName Which form to clear the values from.
 * @return {void}
 */
function clearParameters(formName)
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);

	form.routineName.value="";
	form.routineArgs.value="";
	form.transactionId.value = "";
	form.ofsOperation.value = "";
	form.ofsFunction.value = "";
	form.application.value = "";
	form.version.value = "";
	form.requestType.value = "";
}

/**
 * Wrapper for createResultWindow2 that defaults the 'page' parameter to 'processing.html'.
 * @param {String} cmdname The command to process.
 * @param {int} width Desired width of the created window, in pixels.
 * @param {int} height Desired width of the created window, in pixels.
 * @return {String} windowName The name of the created window.
 */
function createResultWindow(cmdname, width, height)
{
	var windowName = createResultWindow2(cmdname, width, height, "../html/processing.html");  // some web servers are unable to process the path if double slash is used
	return( windowName );
}

/**
 * Parses the given command and launches a new window to process it.
 * @param {String} cmdname The command to process.
 * @param {int} width Desired width of the created window, in pixels.
 * @param {int} height Desired width of the created window, in pixels.
 * @param {String} page The name of the page to be launched.
 * @return {String} windowName The name of the created window.
 */
function createResultWindow2(cmdname, width, height, page)
{
	// If we were drilling down remove the DRILL prefix from the command
	var commandTokens = cmdname.split(" ");
	var prefix = commandTokens[0];
	// Retrieve the breadcrumb action if it would have been appended in doEnquiryRequest()
	var breadcrumbAction = commandTokens[2];
	// For Composite Screen commands only create a new window if the target
	// window is not part of the composite screen set
	var compScreen = getCompositeScreen();
	var compTarget = "";
	// Don't search for target, incase of breadcrumb action like dropdown, drill down, appliction level context enquiry.
	//To print the passbook in a separate window, the requestRoutineName variable is checking against "OS.GET.PASSBOOK.PRINT.DATA"
	if ( ( compScreen != "" ) && ( cmdname != _DROPDOWN_ ) && ( prefix != _DRILL_ ) && ( breadcrumbAction !=_BACK_ )&& (enquiryType != _CONTEXT__ENQUIRY_) && (requestRoutineName != "OS.GET.PASSBOOK.PRINT.DATA"))
	{
		compTarget = getCompositeScreenTarget(cmdname);
	}
	// If request type is context enquiry then don't go with COS target frame and open in new window always.
	if (enquiryType == _CONTEXT__ENQUIRY_) 
	{
		// But in case if the request is being triggered from no frames COS then need to get the current fragment to 
		// get the stored session details in web server. The fragment name is used as window name while stored, so 
		// have it any element to check against the stored details in servlet.
		if (top.noFrames )
		{
			var appreqForm=FragmentUtil.getForm(currentForm_GLOBAL);
			// Create an input type element, set its name and value.
			var newElement = document.createElement("input");
			newElement.type = "hidden";
			newElement.name = "currentContextEnquiryFragmentName";
			newElement.value = Fragment.getCurrentFragmentName(); // Populate the current fragment name.
			appreqForm.appendChild(newElement);	
		}
		// Remember we need to display context enquiry response always in new window so clear it.
		compTarget = "";
				
		// Decide the target if parameter for auto launch context enquiry is enabled.
		if (autoLaunch_GLOBAL == "YES")
		{
			compTarget = getCompositeScreenTarget(cmdname);
		}
	}
	if ( prefix == _DRILL_ )
	{		
		if ( compScreen != "" ) 
		{
			// Get the next bit after the prefix for the target.
			compTarget = commandTokens[1];	
		}
	}

	if (breadcrumbAction == _BACK_ && compScreen != ""  )
 	{
		// Set the current fragment or window name as target incase of breadcrumb action.
		// So that it avoids to open in new window when requests from COS window.
		compTarget = FragmentUtil.getWindowOrFragmentName();
 	}
	
	// Get the name of any parent window
	var windowVar = getWindowVariable();
	
	// If a target has been specified then use that, otherwise create a new window
	if ( compTarget && compTarget != "NEW" )
	{
		windowName = compTarget;
		
		if ( (getWindowVariableMarker() == "true") && ( windowVar != "" ) )
		{
			// Add the parent window name to this window name
			updatedFormsField( "WS_parentWindow", windowVar );
		}
		// load the processing page by showstate if it is for the same window/write the html page if it is a diff fragment
	    var currentwindow = FragmentUtil.getWindowOrFragmentName();
		if(windowName == currentwindow)
		{
		  hideEnquiry();
		  showState("busy");	
		}
		else
		{
			// to hide the frame window behind a semi-transparent layer to avoid multiple clicks being posted.
			if (!top.noFrames && FragmentUtil.isCompositeScreen() && prefix != _DRILL_)
		 	{
		 		var translayer = window.document.createElement('div');
		 		translayer.setAttribute("id","overlay");
		 		translayer.style.zIndex="999";
				translayer.style.position="absolute";
				translayer.style.width="100%";
				translayer.style.left="0";
				translayer.style.bottom="0";
				translayer.style.height="100%";
				translayer.style.backgroundColor="rgb(0,0,0)";
				translayer.style.opacity= "0.5";
				translayer.style.filter="alpha(opacity=50)";
				window.document.body.appendChild(translayer);
			}
		var content = '<html><head><title>processing...</title><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></head><body style="background-color: #e0ebea";><div style="width: 100%; height: 100%; background: url(../plaf/images/default/gears.gif) no-repeat center center;"></div></body></html>';
		for(var i=0;i< parent.frames.length;i++)	
		{
			try {		
				if(parent.frames[i].name==windowName){	
		  		parent.frames[i].document.write(content);			   
				}
			}
			catch (exception) {
                 	FragmentUtil._logger.error("EXTERNAL_URL", exception.message);
			}
		}
		}	

	}
	else
	{
		// Create new window with a unique Id and set the target for the reply
		// Use command name as the window name replacing any invalid characters
		// and adding the user name
		windowName = getUniqueWindowName(cmdname);	
		var _winDefs= "toolbar=no, menubar=no, status=yes, resizable=yes, scrollbars=yes, height=" + height + ", width=" + width + ", screenX=0, screenY=0"; 
	
		// If the window variable marker is set (i.e. this window has a parent that we
		// need to know about like where to target a dropdown value to), then add the
		// parent window name to the name of the new window for extraction later
		if ( (getWindowVariableMarker() == "true") && ( windowVar != "" ) )
		{
			// Add the parent window name to this window name
			updatedFormsField( "WS_parentWindow", windowVar );
		}
		
		newWindow = window.open(page, windowName, _winDefs );
		
		if ( newWindow != null )
		{		
			newWindow.focus();
		}
	}
    
	return( windowName );
}


/**
 * Retruns a unique window name
 * @param {String} [cmdname] The command to process - used to create the window name.
 * @return {String} windowName
 */
function getUniqueWindowName(cmdname)
{
	var user = getUser();
	
	if ( ( user == null ) || ( user == "" ) )
	{
		windowName = cmdname;
	}
	else
	{
		windowName = getUser() + "_" + cmdname;
	}
	
	// Add the date/time in milliseconds to window name to make then unique
	dateTime = new Date();
	windowName = windowName + "_" + dateTime.valueOf();
	
	windowName = replaceSpecialChars( windowName, "_" );
    var appreqForm=FragmentUtil.getForm(currentForm_GLOBAL);
    if(appreqForm)
    {
    	if(appreqForm.requestType.value == "OFS.OS.CONTEXT")
    		{
    			windowName=windowName+"_context_";
    		}
    }
	return windowName;
}

/**
 * Opens a URL in a new window.
 * @param {String} url The URL to open.
 * @return {void}
 */
function createWebWindow( url )
{
	var _left="100";
	var _top="100";
	var _winDefs= "toolbar=1,status=1,directories=1,scrollbars=1,resizable=1,menubar=yes,location=yes,left=" +_left+ ",top=" + _top + ",width=800,height=600"; 
    
    // To create a unique window name.
    var currTime = new Date();
	var winName = getUser() + "_" + "Web" + "_" + currTime.valueOf();
	winName = replaceSpecialChars( winName, "_" );

	var myWin=window.open(url, winName, _winDefs);
}