/** 
 * @fileoverview TODO
 *
 * @author XXX
 * @version 0.1 
 */


/**
 * Submit all requests to the servlet
 * @param {TODO} TODO
 */
var enqTimer="";		// Auto refresh timer for enquiry
var pageNavigation = "";	//To indicate page navigation request of enquiry.

// Indicates whether the enquiry window is actually being closed (as opposed to just being unloaded)
var closingEnqWindow = '';

// A global variable indicating whether a user has clicked on a dropdown row. Holds true if so.
var rowClicked_GLOBAL = false;
// Holds the class used by the row that the user is hovering over.
var lastClass_GLOBAL = "";

// holds the Boolean value of secure document down load
var DOC_DOWN_SERVICE = false; 
var windowleft = "";	
var windowtop  = "";


//Hold the current refresh clock value since object not populated in IE
var refreshClockBox = "";

// Holds the id of the refresh timer
var clokBoxId = "";

/**
 * Submit all requests to the servlet
 * @param {TODO} TODO
 */
function doEnquiryRequest(
		_action, 			// e.g. "SELECTION", "RUN", "DRILL", etc. 
		_name, 				// Name of the enquiry or a command like ENQ %ACCOUNT NONE
		_dropField, 		// Name of a dropdown field if required.
		_previousEnqs, 		// Any previous enquiries used as hyperlinks when drilling.
		_previousEnqTitles, // Any previous enquiries used as hyperlinks when drilling (their titles).
		_args, 				// Additional routine arguments (e.g. selection criteria).
		_target,			// Where the result should be directed (e.g. window or NEW).
		_formName,			// Name of the form to submit the request. 
		_destination){		// "DISPLAY" - display enq on browser, "PRINT" - print on server, "DOWNLOAD" - save enq to disk
	// Indicate we are unloading the enquiry window and not closing it
	closingEnqWindow = false;		
	// Check if _name has ENQ on start - if so remove it this is 
	// the case when passing e.g. ENQ ACCOUT-LIST NONE to _name
	var enqName = _name;
	var args = _args;
	var prefix = _name.substring(0,4);
	var queryPrefix = _name.substring(0,6);

	if(FragmentUtil.isCompositeScreen() && _action == "8teP")
	{
		_target = "s";
	}

	if ( prefix == "Y6l" + " ")
	{
		enqName = _name.substring(4, _name.length);
	}
	else
	{
	 	if ( queryPrefix == "loYba" + " ")
	 	{
			enqName = _name.substring(6, _name.length);
		}
	}
	
	// If there is an argment in enqname like "NONE", "LAST" or  a 
	// selection criteria, seperate it from the actual enquiry name.
	if ( ( prefix == "Y6l" + " " ) || ( queryPrefix == "loYba" + " ") ){
		var pos = enqName.indexOf(" ");
	
		if (pos>0){
			var argument = enqName.substring(pos, enqName.length);
			args = stripSpacesFromEnds(argument);
			enqName = enqName.substring(0, pos);
		}
	}


	
	// Set-up parameters for the form to submit - if name is blank use 
	// the generalForm for submitting requests
	if ( _formName == "" )
	{
		_formName = "generalForm";
	}
	
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(_formName);

	form.requestType.value = "}rXCY6lo1ba";
	form.enqname.value = enqName;
	form.enqaction.value = _action;
	form.dropfield.value = _dropField;
	form.WS_dropfield.value = _dropField;
	form.previousEnqs.value = _previousEnqs;
	form.previousEnqTitles.value = _previousEnqTitles;
	form.routineArgs.value = args;
	form.windowName.value = FragmentUtil.getWindowOrFragmentName();
	form.WS_PauseTime.value = '';	//Nullify before initializing this value.
	
	if((_args.indexOf("_selectionDisplay", _args.length - "_selectionDisplay".length))!=-1)
	{
		if(form.WS_parentFormId==null)
		{
			var newElement = document.createElement("input");
			newElement.type = "hidden";
			newElement.name = "WS_parentFormId";
			newElement.value = currentForm_GLOBAL;	// Don't use getWindowOrFramentName() in this case we want the containing window.
			form.appendChild(newElement);
		} else {
			form.WS_parentFormId.value = currentForm_GLOBAL;
		}
	}
		
	//Clear the application name and version name while launching a normal enquiry
	if (!_dropField)
	{
		form.application.value = "";
		form.version.value = "";
	}else {
	//Assign value the application name and version name if null when launch using dropField
		if((form.application.value==""||form.version.value=="") && (currentForm_GLOBAL != "enqsel"))
		{
			form.application.value = getFormFieldValue( currentForm_GLOBAL, "application" );
			form.version.value = getFormFieldValue( currentForm_GLOBAL, "version" );
		}
	}
		
	 //new window needs to be resized
	if(_target == "NEW")
	{
        form.WS_doResize.value="";
	}

	
	if(_action != "0b1yy")
	{
		var clockBox = FragmentUtil.getElement("refreshtime");
		if(clockBox)
		{
			if(pageNavigation && !(clockBox.disabled))
			{
				form.WS_PauseTime.value = clockBox.value; //Save pause value in session.
				pageNavigation = false;
			}
			if((_action.indexOf("FAVOU") == -1) && _destination == "01XKyta" && clockBox.disabled)
			{
			toggleTimer();		//to stop the timer.
			if(_action == "XYyYeh1}6" && (_args.indexOf("#") != -1))
				{
					toggleTimer();		//dont stop timer
			}
			}
			if(!(form.autoRefresh) && (args[0] != "@"))		//not add for favourites
			{
			var tempAuto = FragmentUtil.getElement("autoRefresh");  //Get the autoRefresh variable.
			form.appendChild(tempAuto);	//To add the auto refresh value in the request.
			}
		}

	}
	if(form["value:999:1:1"] != null && clockBox != null && (_action == "bo6" || _action.indexOf("FAVOU") != -1))
	{
		form.autoRefresh.value = form["value:999:1:1"].value;
	}
	if (!(form.EnqParentWindow && form.EnqParentWindow.value != '' && form.EnqParentWindow.value != null && FragmentUtil.isCompositeScreen() && _action=="8teP"))
	{	
		//for newly opened tear off window there is no need to store the parent window information  
		if(args.indexOf("LAST#")!=-1 && _target == "NEW")
			{
        	form.EnqParentWindow.value = '';
        	}
        	else
        	{
        form.EnqParentWindow.value = FragmentUtil.getWindowOrFragmentName();
    }
    }
	else 
	{
		if (!(form.EnqParentWindow))
		{
			//  new element added in the form to send the parent window reference to server while drilldown / dropdown
			if(!(args.indexOf("LAST#")!=-1 && _target == "NEW"))
			{	        	
	        var newElement = document.createElement("input");
			newElement.type = "hidden";
			newElement.name = "EnqParentWindow"; 
			newElement.value = FragmentUtil.getWindowOrFragmentName();
			form.appendChild(newElement); 
		}
	}
	}
	// Merge sorting params into enq selection fields
	if (form["tempSortField:1"]) {

		var isSortOnly = false;

		// for sort only requests - need to substitute sortfields as well as sortdir
		// into the empty hidden fields supplied
		if ( (form["fieldName:1:1:1"]) &&  (form["fieldName:1:1:1"].value == "") ) {
			isSortOnly = true;
		}

		// reset the main selection (hidden) sort fields
		for (var mainSortIdx = 1; mainSortIdx < EnqSel.maxFields; mainSortIdx++) {
			var mainSortName = "sort:" + mainSortIdx + ":1:1";
			if (! form[mainSortName]) {
				break;
			}
			// reset to default
			form[mainSortName].value = "none";
		}

		// process separate sort fields for new enq sel
		for (var tsfIdx = 1; tsfIdx <= EnqSel.maxSortFields; tsfIdx++) {

			var tsfVal = form["tempSortField:" + tsfIdx].value;
			if (tsfVal) {
				// for each populated temp sort field .. get the asc/desc value
				var tsdRadios = form["tempSortDir:" + tsfIdx];
				var tsdVal;
				for (var radIdx=0; radIdx < tsdRadios.length; radIdx++) {
					if (tsdRadios[radIdx].checked) {
						tsdVal = tsdRadios[radIdx].value;
					}
				}

				if (isSortOnly) {
					// Add sort field / dir - no check for duplicates - subsequent val should be ignored
					var mainFieldName = "fieldName:" + tsfIdx + ":1:1";
					form[mainFieldName].value = tsfVal;
					var mainSortName = "sort:" + tsfIdx + ":1:1";
					form[mainSortName].value = tsdVal;
				}
				else {
					// transfer sort box direction value to the main sort value in the main selection box
					// (match sort box enq field name combo value with main field num)
					for (var mainIdx = 1; mainIdx < EnqSel.maxFields; mainIdx++) {
						var mainFieldName = "fieldName:" + mainIdx + ":1:1";
						var mainSortName = "sort:" + mainIdx + ":1:1";
						if (! form[mainFieldName]) {
							break;
						}
						// if duplicate temp sort entries, then last one wins
						if (form[mainFieldName].value == tsfVal) {
							form[mainSortName].value = tsdVal;
						}
					}
				}
			}
		}
	}

	//display, print on server or save enq to disk.

	var destinationType = _destination;
	
	if(destinationType == "XtRY" || destinationType == "XLu]" || destinationType == "3Lu]")
 	{
		// submit the form, using Fragment population method as necessary
 		FragmentUtil.submitForm(form);
 	}
	else if ( destinationType == "Kb16h" || destinationType == "KwISv" || destinationType == "xwISv" )
	{
		form.download.value ="SERVER.PRINT";
		//change the request type to server print
		form.requestType.value = "}rXCY6lo1baCKb16h";	
		submitRequest();
		//return the old request type
		form.requestType.value = "}rXCY6lo1ba"	;
	}
	else if ( destinationType == "01XKyta" || destinationType == "0I3xDLi" || destinationType == "cI3xDLi" )		
	{
		// Create a new window or not
		var targetWin = "";

		if (_target != "")
		{	
			// Always want a dropdown to go in to a new window
			if (_action == "0b}K0}56")
			{
				myarg = _action;
			}
			else if (_action == "0b1yy")
			{
				myarg = "0b1yy" + " " + _target;
			}
			else if (_action=="8teP")
			{
				// _action has to be appended so that breadcrumb response will be displayed in correct fragment or window
				myarg = "Y6l" + " " + enqName + " " + _action;
			}
			else if (_action!="")
			{
				myarg = _target;
			} 
			else 
			{
				myarg = "Y6l" + " " + enqName;
				
			}

			// Check if the window name GLOBAL has been set, is so then use it
			if (windowName_GLOBAL != "")
			{
				windowName = windowName_GLOBAL;
			}
			else
			{
				windowName = createResultWindow(myarg, 500, 600 );
			}
			
			if (Backevent == true)
			{
							windowName = backWindow;
			}
			MenuStyle = getFieldValue("Menustyle");
			if (recordHistory_GLOBAL == true && MenuStyle == "POPDOWN" && Backevent == false && _action != "0b1yy")
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
			
			targetWin = windowName;
			form.target = windowName;
			form.windowName.value = windowName;		// Save the new name on the form
			
		}
		else
		{
		    if(_action != "0b}K0}56") //show busy
			  {
		        // Don't set the status to busy, if the action type is  add or delete the favourite.
				
				if(!( (_action.indexOf("t00CrtR") != 0) || (_action.indexOf("0YyCrtR") != 0) ))
					{
						hideEnquiry();
						showState("busy");
					}
              }
			var windowOrFragmentName = FragmentUtil.getWindowOrFragmentName();
			
			// Check if the window name GLOBAL has been set, is so then use it
			if (windowName_GLOBAL != "")
			{
				windowOrFragmentName = windowName_GLOBAL;
			}
			
			targetWin = windowOrFragmentName;
			form.target = windowOrFragmentName;
		}
		
		// Don't display breadcrumbs if drilling down in the same composite screen
		//if ( ( form.target != "" ) || (form.target != "NEW" ) )
		//{
		//	form.previousEnqs.value = "";
		//	form.previousEnqTitles.value = "";
		//}
	
		// Store the name of the target as this window could be the parent for a dropdown
		storeWindowVariable( targetWin );
			
		// Clear the compScreen field if we are not in a composite screen
		var savedCompScreen = form.compScreen.value;
		
		if ( ! FragmentUtil.isCompositeScreen() )
		{
			setFormFieldValue( _formName, "compScreen", "" );
		}
		
		if ( _action == "0b}K0}56" && (popupDropDown_GLOBAL) )
		{
			// Get the right drop field object
		    var dropFieldObject = getCurrentTabField( currentForm_GLOBAL,_dropField );
		    // Set fixed height/width dimensions - constants in dropdown.js
			var dropWidth = dropWidth_GLOBAL;
			var dropHeight = dropHeight_GLOBAL;
			// Display the popup with given size/position, and run the request given by form
			runPopupDropDown(form, dropFieldObject, dropWidth, dropHeight);			
		}
		// Submit a background request to add an enquiry favourite entry
		else if ( (_action.indexOf("t00CrtR") == 0) || (_action.indexOf("0YyCrtR") == 0) )
		{
            var params = FragmentUtil.getFormFieldsAsParams(form);

            Fragment.sendRequest(form.action, params, EnqFavourites.favouriteHandler);
		}
		else // Submit a request to the Browser Servlet
		{
		    FragmentUtil.submitForm(form);
		    
		    // Reset composite screen value .. TODO: why reset it?? It has gone by now in noFrames / IE
		    if (form.compScreen) {
		    	form.compScreen.value = savedCompScreen;
		    }
		}
	}
	else
	{
		doDownload(destinationType);
	}
}

/**
 * Minimises the dropdown when users click outside it.
 * @param {eventObject} thisEvent The onBlur event
 */
function getDropfieldObject( fieldName)
{
	var dropFieldObject = null;
	var fieldList = getFormFields( currentForm_GLOBAL, fieldName);
	// Is there more then 1 field of the same.
	if ( fieldList.length > 1)
	{
		for( var j = 0; j < fieldList.length; j++)
		{
			var currentField = fieldList[j];
			if( currentField.getAttribute( "tabName") == currentVersionTab_GLOBAL)
			{
				dropFieldObject = currentField;
				break;
			}
		}
	}
	
	if( dropFieldObject == null)
	{
		// Get the drop field
    	dropFieldObject = getFormField( currentForm_GLOBAL, fieldName);
	}
	
	return dropFieldObject;
}

/**
 * Minimises the dropdown when users click outside it.
 * @param {eventObject} thisEvent The onBlur event
 */
function clearDropDownOnBlur(thisEvent)
{
	// Get the ID of the dropdown field from the onblur event
	if (!thisEvent) {
		var thisEvent = window.event; //thisEvent will be null in IE, so use window.event
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var dropFieldId = thisField.getAttribute("id");
	dropFieldId = dropFieldId.substring(0,dropFieldId.length - 4); // strip off ":div" at the end
	clearDropDown(dropFieldId);
}

function toggleStateButton()
 {
   var defaultButton = FragmentUtil.getElement("defaultButton");
 
	if ( defaultButton != null && defaultButton.getAttribute('state') == 'OFF')
		{
		    defaultButton.setAttribute('state','ON');
			return;
		}
 }


/**
 * Minimises the content of the div element.
 * @param {String dropFieldId} The fieldName of the input element associated with the dropdown.
 */
function clearDropDown(dropFieldId)
{
    //reset the hotfield after the dropdown is clicked
    if (dropFieldId == resetHotField_Global) {                                       
           resetHotField_Global = "";          
           setHotField('true', dropFieldId);   
    }
    // If a row has been hovered over then don't clear the dropdown
    // Let the tableRowClick() event execute first then 'clearDropDown()' 
    // will be called again with 'rowClicked_GLOBAL' set to false.
    if ( !rowClicked_GLOBAL ) {
		hidePopupDropDown(dropFieldId);
    }   
}

/**
 * Resizes the div to fit the table returned from the response.
 * @param {TODO} TODO
 */
function styleDropDownDiv(dropField) {

	// Look at the current dropdown for styling it.
	if (( currentDropDown_GLOBAL != "") && (!top.noFrames))
	{
		dropField = getCurrentTabField( currentForm_GLOBAL, currentDropDown_GLOBAL);
	}

    var ddContent = document.getElementById("dropDownTable:" + dropField.id ); 
    var ddOffset = 19;

    // If we don't have a dropdown table check if we have any messages.
    if(ddContent == null) {
    	ddContent = document.getElementById("message");
    	ddOffset += 22;
    }

    // If no table or messages either then not a dropdown, so return.
    if (ddContent == null) {
    	return;	
    }

    var ddWidth = ddContent.offsetWidth + ddOffset;
    var ddHeight = ddContent.offsetHeight + ddOffset;

    if( ddWidth > dropWidth_GLOBAL ) {
		ddWidth = -1;	
	}
    if( ddHeight > dropHeight_GLOBAL ) {
		ddHeight = -1;	
	}

	// resize popup: -1 means ignore, so never set new x/y pos
	setPopupDropDownDimensions(dropField, ddWidth, ddHeight);

    // Set focus / onblur handler.		   
    setPopupDropDownFocus(clearDropDownOnBlur, dropField);
}

/**
 * Changes the class of the row with the given id.
 * @param {TODO} TODO
 */
function tableRowMouseOver(rowID)
{
    var tableRow = document.getElementById(rowID);
    lastClass_GLOBAL = tableRow.getAttribute( "className");
    tableRow.setAttribute( "className" , "colour2" );
    rowClicked_GLOBAL = true;
}

/**
 * Changes the class of the row with the given id.
 * @param {TODO} TODO
 */
function tableRowMouseOut(rowID)
{
    var tableRow = document.getElementById(rowID);
    tableRow.setAttribute( "className" , lastClass_GLOBAL );
    rowClicked_GLOBAL = false;
}

/**
 * Users clicks on the row. Pick the row selected and pass the details on to the field. 
 * Field value and enrichment.
 * @param {TODO} TODO
 */
function tableRowClick( caption, dropFieldId, enrichment, fragmentName)
{ 
    dropDownPick( caption, dropFieldId, enrichment, fragmentName);
    rowClicked_GLOBAL = false;
    clearDropDown(dropFieldId);
}

function tableRowkeyPress( caption, dropFieldId, enrichment, fragmentName, thisEvent)
{ 

	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	tableRowClick( caption, dropFieldId, enrichment, fragmentName);
}
function tableRowfocus(thisEvent)
{
	var focusRow = thisEvent.target || thisEvent.srcElement;
	focusRow.style.backgroundColor = "#EBECE4";
}
function tableRowblur(thisEvent)
{
	var focusRow = thisEvent.target || thisEvent.srcElement;
	focusRow.style.backgroundColor = "";
}
function popupRowfocus(thisEvent)
{
	if(currentEvent_Global == "Mouse")
	{
		return;
	}
	var focusRow = thisEvent.target || thisEvent.srcElement;
	focusRow.style.color = "#cc6600";
}
function popupRowblur(thisEvent)
{
	var focusRow = thisEvent.target || thisEvent.srcElement;
	focusRow.style.color = "";
	
}
function toolsMenuPopuponkey(thisField,thisEvent)
{
	if(thisEvent.keyCode == 13)
	{
		toolsMenuPopup(thisField);
	}
}
/**
 * Get the X position of the given element
 * @param {TODO} TODO
 * @return 'targetLeft;'
 */
function getElementX(element)
{
    var targetLeft = 0;
    var totalScroll = 0;
    var parentNode = element.parentNode;
    
    // Get the offset factor by which the element is offset.        
    if (element.offsetParent) 
    {
        while (element.offsetParent) 
        {
        	targetLeft += element.offsetLeft;
            element = element.offsetParent;
        }
    } 
    // If element offsetParent attribute is null, calculate the left position with parentNode.
 	// In case of element is hidden, some versions of Firefox(3.6) will not have element object
 	else if (!element.offsetParent)
 	{
 		while (parentNode.offsetParent) 
         {
         	targetLeft += parentNode.offsetLeft;
            parentNode = parentNode.offsetParent;
         }
 	}
    else if (element.x)
    {
        targetLeft += element.x;
    }
    
    // Get the total scrollTop of the element and its parents
	if( parentNode)
	{
		while( !isNaN( parentNode.scrollLeft) && parentNode.nodeName != "BODY" )
		{
			totalScroll += parentNode.scrollLeft;
			parentNode = parentNode.parentNode;
		}
	}
	
	// Remove the scrolling from the y coordinate
	targetLeft -= totalScroll;

    return targetLeft;
}

/**
 * Get the Y position of the given element
 * @param {TODO} TODO
 * @return 'targetTop'
 */
function getElementY( element)
{
    var targetTop = 0;
    var totalScroll = 0;
    var parentNode = element.parentNode;
    
    // Get the offset factor by which the element is offset.
    if ( element.offsetParent) 
    {
        while ( element.offsetParent )
        {
           targetTop += element.offsetTop;
           element = element.offsetParent;
        }
    } 
    // If element offsetParent attribute is null, calculate the left position with parentNode.
 	// In case of element is hidden, some versions of Firefox(3.6) will not have element object. KARTHI
 	else if (!element.offsetParent)
 	{
 		while ( parentNode.offsetParent )
        {
           targetTop += parentNode.offsetTop;
           parentNode = parentNode.offsetParent;
        }
 	}
    else if (element.y)
    {
        targetTop += element.y;
    }	 
	
	// Get the total scrollTop of the element and its parents
	if( parentNode )
	{
		while( !isNaN( parentNode.scrollTop)  && parentNode.nodeName != "BODY" )
		{
			totalScroll += parentNode.scrollTop;
			parentNode = parentNode.parentNode;
		}
	}
	
	// Remove the scrolling from the y coordinate
	targetTop -= totalScroll;
	
    return targetTop;
}

/**
 * Get the Y position of the given element
 * @param {TODO} TODO
 */
function action( theaction )
{
	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqname ='' ;        
     var dropfield ='';        
     var previousEnqs ='';     
     var previousEnqTitles ='';
     var enqid = '';
     //  In noFrames mode, enquiry form objects can be retrieved based on fragment name. 
     //  So append the currentfragmentname with form element to be searched
     if (top.noFrames)
     {       
      
        // Get the current fragment name from the current window usually, but use the target context if supplied
         var fragmentName = window.Fragment.getCurrentFragmentName();
         var formRef= window.document.forms["enqsel_"+fragmentName];
         enqid= formRef.elements.namedItem("enqid");
       }
      else
      {
       enqid = FragmentUtil.getElement("enqid");
      }  
     enqname = enqid.value;
     var dropfld = FragmentUtil.getElement("dropfield");
      dropfield = dropfld.value;
     var preEnqs = FragmentUtil.getElement("previousEnqs");
      previousEnqs = preEnqs.value;
     var preEnqTitles = FragmentUtil.getElement("previousEnqTitles");
     previousEnqTitles = preEnqTitles.value;

	if (theaction=="Kb16h")
	{
		if(FragmentUtil.isCompositeScreen() && top.noFrames == true)
			{
				var tdElement= document.getElementById(fragmentName);
				tdElement.className = tdElement.className.replace("notPrintableFragment","printableFragment");
				//get the tbody element
				//form the new html document to be printed
				
				for(var p = tdElement.parentNode; p && p.nodeName != "body"; p = p.parentNode)
				{
					if(p.className != null && p.className.indexOf("fragmentContainer") != -1)
						{
							if(p.className.indexOf("printableFragment") == -1)
								{
									p.className = p.className.replace(
										"notPrintableFragment","printableFragment");
								}
						}
				} 
				print();
				tdElement.className = tdElement.className.replace("printableFragment","notPrintableFragment");
				for(var p = tdElement.parentNode; p && p.nodeName != "body"; p = p.parentNode)
				{
					if(p.className != null && p.className.indexOf("fragmentContainer") != -1)
						{
							if(p.className.indexOf("notPrintableFragment") == -1)
								{
									p.className = p.className.replace(
										"printableFragment","notPrintableFragment");
								} 
						}
				}
			}
		else
		{
			print();
		}
		 
	}
	else if (theaction=="XYyYeh")
	{
		// Check if we are displaying a report, change the enquiry name
		var prefix = enqname.substring(0,5);
		if ( prefix == "R1Y5" )
		{
			PreEnqname = getPreviousEnquiry();
			Enqarr = PreEnqname.split("*");
			enqname = Enqarr[0];
		}
		
		// Request full selection criteria
		doEnquiryRequest("XYyYeh1}6", enqname, dropfield, previousEnqs, previousEnqTitles, "", "", "", "01XKyta");
	}
	else
	{
		pageNavigation = true;
		doEnquiryRequest(theaction, enqname, dropfield, previousEnqs, previousEnqTitles, "", "", "", "01XKyta");
	}
}

/**
 * Breadcrumb functions....
 * @param {TODO} TODO
 */
function dobreadcrumb( mycrumb, mycrumbTitle ) 
{
	var mycrumbref = decodeURI(mycrumb);
	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqForm = FragmentUtil.getForm("enquiry");

	// Request previous enquiry
	// Alter the previous enquiry ids list so that we only send back up to the one that was selected
	var previousEnqs = enqForm.previousEnqs.value;
	var previousEnqTitles = enqForm.previousEnqTitles.value;

	// Find the selected one in the list and remove it plus everything after it
	// Enquiries are separated by '_' characters so check for enquiry at start or end of list too

	var enqPos = previousEnqs.indexOf( "_" + mycrumbref + "_" );

	if ( enqPos != -1 )
	{
		if ( enqPos == 0 )
		{
			previousEnqs = "";
			previousEnqTitles = "";
		}
		else
		{
			previousEnqs = previousEnqs.substring( 0, enqPos + 1 );
			
			if ( ( previousEnqTitles ) && ( mycrumbTitle ) )
			{
				var enqTitlePos = previousEnqTitles.indexOf( "_" + mycrumbTitle + "_" );
				
				if ( enqTitlePos != -1 )
				{
					previousEnqTitles = previousEnqTitles.substring( 0, enqTitlePos + 1 );
				}
			}
		}
	}
	
	var dropField = enqForm.dropfield.value;
	doEnquiryRequest("8teP", mycrumbref, dropField, previousEnqs, previousEnqTitles, "", "", "",	"01XKyta");	
}

/**
 * Drill down on an item
 * @param {TODO} TODO
 */ 
function drilldown( item, ref )
{
	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqForm = FragmentUtil.getForm("enquiry");
	// The target we should drill down to
	var target = "";
	var args = item+"_"+ref;

	// Add the current enquiry to the list of previous enquiries for drilldown
	var currentEnq = enqForm.enqid.value;
	var currentEnqTitle = enqForm.enqtitle.value;
	var previousEnqs = enqForm.previousEnqs.value;
	var previousEnqTitles = enqForm.previousEnqTitles.value;

	if ( previousEnqs == "" )
	{
		previousEnqs = "_" + currentEnq + "_";
		previousEnqTitles = "_" + currentEnqTitle + "_";
	}
	else
	{
		previousEnqs = previousEnqs + currentEnq + "_";
		previousEnqTitles = previousEnqTitles + currentEnqTitle + "_";
	}
	
	var dropField = enqForm.dropfield.value;
	// If we are in a composite screen, then pass in the multi value that
	// we are drilling down to as this controls which part of the composite
	// screen that we are sending the result to.
	
	drilltypes =  enqForm.drilltypes.value;
	drillarr = drilltypes.split("|");
	
	drillData = enqForm.drillData.value;
	drillDataList = drillData.split("|");
		
	// Send down the drill action type so that the server knows what sort of thing we are drilling down to
	drillIndex = item - 1;
	var mydrill = drillarr[drillIndex];
	var form = FragmentUtil.getForm( "generalForm" );
	// To ensure that drillaction value is passed appropriately in request by overwriting if already a value is present
    var drillactionnew = form.elements["drillaction"];
    if (drillactionnew != null && drillactionnew != undefined && drillactionnew != '')
    {
       form.drillaction.value = mydrill;
    }
    else
    {
		var newElement = document.createElement("input");
    	newElement.type = "hidden";
    	// Update Id attribute to new creating element in the current form.
    	// So that in case of removal it can be retrieved easily.
    	newElement.id = "drillaction";
    	newElement.name = "drillaction";
		newElement.value = mydrill;			
		form.appendChild(newElement);
	}
		
	if(FragmentUtil.isCompositeScreen())
	{
		cmdname = drillDataList[ drillIndex];
		// if it is a drill down Enquiry
		if (mydrill == "ENQ")
		{
			cmdname = mydrill + " " + cmdname;
		}
 		checkTargetContent( cmdname, "generalForm" );
 		
 		// Don't display breadcrumbs if drilling down in the same composite screen
 		target = getCompositeScreenTarget( cmdname );
 		
 		//To launch the EFS pdf request in new window
		if(mydrill!="" && mydrill=="PRINT.PDF")
		{
			target="NEW";
		} 		
		// get the current target and ensure to display breadcrumbs if target also same.
		var currentTarget = FragmentUtil.getWindowOrFragmentName(); 
		
		// Drilldown target passed in request to avoid multiple clicks
		var targetsplit = "";
		if ((form.drilltarget.value!="") && (form.drilltarget.value!=null))
		{
			targetsplit = form.drilltarget.value.split("_"); 
			for(i=0;i<targetsplit.length;i++)
			{
				if (target == targetsplit[i])
				{
					return;
				}
			}
		}
		
		// Check for target when place in a COS
		if ( ( target != "" ) && ( target != "NEW" ) && ( target != currentTarget ) && ( ( mydrill == "ENQ" ) || ( mydrill == "QUERY" ) || ( mydrill == "APP" ) ) )
		{
			form.drilltarget.value+=target+"_";
		}
	
        // need to have only the FRAME name.( Target name may be appended to Frame name using "_" as demiliter. Ex: TABNAME_TARGETNAME).
        if ( currentTarget.indexOf("_") != -1 )
        {
            currentTarget = currentTarget.substring(0,currentTarget.indexOf("_"));  
       	}
        // If the target is going to be displayed as a new frame/window then we don't need breadcrumb on it.
		if ( (( target != "" ) && ( target != "NEW" ) && ( target != currentTarget ) || (target == "NEW") ))
		{
			previousEnqs = "";
			previousEnqTitles = "";
		}
	} 
	else 
	{
		target = "";
	}
	
	if (top.isWorkspace==true)
	{
		target = "NEW";
	}
	
 	if (mydrill=="0}56y}t0")
	{
		target = "";
		// Need to set the flag of download otherwise content will be displayed in the same window itself where the trigger happened to download.
		DOC_DOWN_SERVICE = true;    // set to true to send the request via general form
		doEnquiryRequest("0b1yy", currentEnq, dropField, previousEnqs, previousEnqTitles, args, target, "", "XtRY");
		DOC_DOWN_SERVICE = false;      // reset the value to false.
    }
    else if (mydrill=="0}eCXYbR1eY")
    {
		// Set no target. Always download dialogue box will be displayed for document download.
   		// It avoids blank window display after download also.
   		target = "";		
   		DOC_DOWN_SERVICE = true;    // set to true to send the request via general form		
   		
		doEnquiryRequest("0b1yy", currentEnq, dropField, previousEnqs, previousEnqTitles, args, target, "", "01XKyta");

		// Remove the element drillaction in the current form.
 		// It avoids of creating duplicate elements in the form for non document download drilldowns.
 		// And sending the correct drill down action to server for processing. 
        var drillaction = document.getElementById("drillaction");
        form.removeChild(drillaction);
 
   		DOC_DOWN_SERVICE = false;      // reset the value to false. 
    }
    else if (mydrill== "e}X")
    {
		doEnquiryRequest("0b1yy", currentEnq, dropField, previousEnqs, previousEnqTitles, args, target, "", "01XKyta");
    }
    else if (mydrill.substring(0,11) == "javascript:")
	{
    	// They are trying to run a javascript function
    	var evalText = mydrill.substring(11,mydrill.length);
    	eval(evalText);
    }
	else
	{
	 if(target == currentTarget || target == "")          //Show busy to be displayed only when the current target and drill down target are the same.
	   {	
	    busyEnquirySelectionForm(); //To avoid crash for clicking multiple times
	    }
		doEnquiryRequest("0b1yy", currentEnq, dropField, previousEnqs, previousEnqTitles, args, target, "", "01XKyta");
	}	
	
	// TODO: why is this here? unlock was set in generalForm??
    if (enqForm.unlock) {
    	enqForm.unlock.value = "";
    }
}


/**
 * Drill down on an item where the drilldown enquiry has been selected from a combo box
 * @param {TODO} TODO
 */
function drilldownSelection( comboBoxId )
{
	// Get the drilldown item and reference from the selected combo box attribute values
	var comboBox = FragmentUtil.getElement( comboBoxId );

	if ( comboBox != null )
	{
		var selectedIndex = comboBox.selectedIndex;
		var itemNo = comboBox.options[ selectedIndex ].getAttribute( "item" );
		var refNo  = comboBox.options[ selectedIndex ].getAttribute( "ref" );

		drilldown( itemNo, refNo );
	}
}

/**
 * Handle any view differences here, then call drill down
 * @param {TODO} TODO
 */ 
function view(item,ref)
{
	drilldown(item,ref);
}

/**
 * Just redo for the refresh
 * @param {TODO} TODO
 */
function refresh()
{

	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqname = '';
    var dropfield = '';
    var previousEnqs = '';
    var previousEnqTitles = '';
    var enqForm = '';
	var enqid = "";
	if (top.noFrames){
		// Get the current fragment name from the current window usually, but use the target context if supplied
		var fragmentName = window.Fragment.getCurrentFragmentName();
		var formRef= window.document.forms["enqsel_"+fragmentName];
		enqid= formRef.elements.namedItem("enqid");
	} else {
		enqid = FragmentUtil.getElement("enqid");
	}
     enqname = enqid.value;
    var dropfld = FragmentUtil.getElement("dropfield");
     dropfield = dropfld.value;
    var preEnqs = FragmentUtil.getElement("previousEnqs");
     previousEnqs = preEnqs.value;
    var preEnqTitles = FragmentUtil.getElement("previousEnqTitles");
     previousEnqTitles = preEnqTitles.value;
     
     // to load busy to avoid crash by multiple clicking
     busyEnquirySelectionForm();

	doEnquiryRequest("XYyYeh1}6", enqname, dropfield, previousEnqs, previousEnqTitles, "LAST", "", "", "01XKyta");
}

function openNewWindow()
{
	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqname = '';
    var dropfield = '';
    var previousEnqs = '';
    var previousEnqTitles = '';
	var pageNumber = '';
    var enqForm = '';
	if (top.noFrames){       
		 // Get the current fragment name from the current window usually, but use the target context if supplied
         var fragmentName = window.Fragment.getCurrentFragmentName();
         var formRef= window.document.forms["enqsel_"+fragmentName];
         enqid= formRef.elements.namedItem("enqid");
    }else{
		 enqid = FragmentUtil.getElement("enqid");
    }  
    enqname = enqid.value;
	var currentPage = FragmentUtil.getElement("currentPage");
    pageNumber = currentPage.value;
	var dropfld = FragmentUtil.getElement("dropfield");
    dropfield = dropfld.value;
    var preEnqs = FragmentUtil.getElement("previousEnqs");
    previousEnqs = preEnqs.value;
    var preEnqTitles = FragmentUtil.getElement("previousEnqTitles");
    previousEnqTitles = preEnqTitles.value;
	
	newWindowRequest = true;
	doEnquiryRequest("XYyYeh1}6", enqname, dropfield, previousEnqs, previousEnqTitles, "LAST#"+pageNumber, "NEW", "", "01XKyta");
	windowName = ""; //To avoid other new window actions to override this window. 
}
/**
 * Initialises a page drop down.
 */
 function initDropDown()
 {
 	// Get the input element that triggered the dropdown, making sure to get the one in the right fragment
    var fragmentName = document.getElementById("fragmentName").value;
    var dropFieldForm = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
    var dropFieldFormId = dropFieldForm.getAttribute("id");
    var dropFieldId = document.getElementById("dropFieldId").value;
    var dropFieldObject = getCurrentTabField( dropFieldFormId, dropFieldId );
 	// This method gets called for styling of the dropdown. 
	styleDropDownDiv(dropFieldObject);
 }

/**
 * Initialise the enquiry form
 * @param {TODO} TODO
 */
function initEnquiry(enqname, windowTop, windowLeft, windowWidth, windowHeight)
{
	resizeOninit = "1";
	
	//Reset the value of autoLaunch in the response
	if (top.autoLaunch == "Y")
	{
	parentFrame = getFormFieldValue("generalForm","WS_parentWindow");	
    if (parentFrame != null && parentFrame !="")
		{	
    		if(top.noFrames)
			{
    			parentFragmentOrFrame = parentFrame;
			}
			else
			{
				parentFragmentOrFrame = FragmentUtil.getFrameWindow(parentFrame);
			}
			if ( parentFragmentOrFrame != null && parentFragmentOrFrame != "")
			{
				if(parentFragmentOrFrame.top.autoLaunch != "" && parentFragmentOrFrame.top.autoLaunch != "undefined" && parentFragmentOrFrame.top.autoLaunch == "Y" )
				{
					//set autolaunch to No
					parentFragmentOrFrame.top.autoLaunch = "N";
				}
			}		
		}
	}
	if(FragmentUtil.isCompositeScreen())
	{
		// Reset on response to a drilldown is recieved.
		var parenttarget = "";
		var parentwnd = "";
		var parentForm = "";
		var currtarget = "";
		var resetdrilltrg = "";
		var trgid = "";
	
		// Fetching the parent window form from the drilled down window
    	parenttarget = getFormFieldValue("generalForm","EnqParentWindow");
     	currtarget = FragmentUtil.getWindowOrFragmentName();
     	
   		if ( parenttarget != null && parenttarget !="" )
    	{
    		if (top.noFrames)
    		{
    			parentwnd=window;
    		}
    		else
			{
    			if (currtarget.indexOf("NEW")!=-1)
				{
					parentwnd = parent.frames[parenttarget];	
				}
				else
				{
					parentwnd = FragmentUtil.getFrameWindow(parenttarget);
				}
			}
    		if (parentwnd !=null && parentwnd !="")             
			{
		 		parentForm = FragmentUtil.getForm("generalForm", parentwnd, parenttarget);			
    		}
    		if(parentForm !="" && parentForm !=null && parentForm.drilltarget)
 			{
 				trgid = parentForm.drilltarget.value.split("_"); 
				for(i=0;i<trgid.length;i++)
				{
					if ((trgid[i] != "") && (trgid[i] != null) && (currtarget != trgid[i]))
					{
						resetdrilltrg += trgid[i]+"_";
					}
				}
				parentForm.drilltarget.value=resetdrilltrg;				
			}
		}
	}
	
	// Check if we are a page dropdown, if so then call initdropdown. don't wanna do enq init stuff.
	var isDropDownEnquiry = document.getElementById("dropdownEnquiry");
    if(isDropDownEnquiry != null)
    {
		initDropDown();
		return;  
    }
	//Block back button functionality for composite screens for now
	if (!FragmentUtil.isCompositeScreen())
	 {    
		// initialise the history
		initEnquiryHistory();
	 }
	// Set visibility on Go button and Processing message
	showState("ready");

	// Move Processing div off screen so as not to interfere with fields (for Netscape)
	setLeftPosition( "processingPage", 10000 );

	// Move the enquiry message to the left hand side
	setLeftPosition( "divMsg", 1 );

	// Set the cursor to the default for each form - if they exist
	setCursor( "enqsel", "default" );
	setCursor( "enquiry", "default" );

	// Check to see if we are in a frame (i.e. a composite screen)
	// If we are, then do not do the resize
	setWindowPosition = checkresize("enqsel", windowTop, windowLeft, windowWidth, windowHeight);

	// Put the focus on the Go button (normally on the enquiry selection form)
	// so using Enter will submit the request - but only if it is there
	// But only do this if there is no enquiry result on display otherwise
	// screen will scroll down to the Go button

	// Get the Go button
	var defaultButton = FragmentUtil.getElement("defaultButton");

	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqForm = FragmentUtil.getForm("enquiry");

	// If there is a Go button and no enquiry result form then set the focus
	if ( ( defaultButton != null ) && ( enqForm == null ) )
	{
		if ( ( defaultButton.type != "hidden" ) && ( defaultButton.disabled == false ) )
		{
			defaultButton.focus();
		}
	}
	
	// Set closing window flag so we know when we are really closing the
	// enquiry window as opposed to just unloading the window for a result
	closingEnqWindow = true;
	
	// Set the status bar
	setWindowStatus();

	// Find out how many rows we have and whether we want to display the scrollbars
	setscroll("selectiondisplay", "selectiondatascroller");
	
	// Start the refresh timer, if present
	var refTimer = FragmentUtil.getElement("refreshtime");
	var pauseValue = getFormFieldValue("enquiry", "WS_PauseTime");
	if(pauseValue && refTimer)
	{
		refTimer.value = pauseValue;	//assign value where timer previously paused.
		refTimer.disabled = true;	//To pause timer
		saveRefreshTime();	//To initilaise autoRefresh variable of enquiry form.
	}
	toggleTimer();
	
	// Populate the pie chart or graph - does nothing if no data array element present
	// TODO - maybe use SVG object's onload instead of messy timer
	var temp = Fragment.getCurrentFragmentName();

    setTimeout(function() {FragmentUtil.populateGraphOrChart(temp);}, 500);

	//Auto print deal slip reports based on the value of 
	//the hidden field  - dealSlipAction
	reportAction = document.getElementById("dealSlipAction");

	if (reportAction!=null)
	{
		if(reportAction.value == "Kb16hC0YtyCXy1K")
		{
			action("Kb16h");
		}
	}
	
	// Hide any columns that are marked accordingly
	hideEnquiryColumns();

	// Correct the width of enquiry div elements if necessary
	setEnqResponseDivWidth();
	
	// Resize the enquiryDataScroller elements
	resizeEnquiry();
	
	// Check if there are multiple deal slips specified in the enqname variable.
	// If so, then process each of them by calling submitPrintSlips().
	// The multiDealSlipPattern is a regular expression matching at least two numbers each with at least 9 characters, separated by a comma.
	// The first slip in the list will already be printed - it's launching the other requests, so don't request it again.
	var multiDealSlipPattern = /(\d{9,}\,\d{9,})+/;
	if (enqname.match(multiDealSlipPattern)) {
		beginPos = enqname.indexOf(",") + 1;
		endPos = enqname.length;
		
		// Send requests for other than the first deal slip.
		// It avoids the control loss in first deal window itself because of getParentWindow().
		// If control lost in first deal slip window, requests for other deal slips won't be sent.
		// launch deal slip for first.
		var parentWin = FragmentUtil.getParentWindow(); // function call corrected
		var parentApplication = parentWin.document.getElementById("application");
		var parentVersion = parentWin.document.getElementById("version");

		setFormFieldValue( "generalForm", "application", parentApplication.value );
		setFormFieldValue( "generalForm", "version", parentVersion.value );
		submitPrintSlips(enqname.substring(beginPos,endPos),"generalForm");

	}
	var enquiryDataTable = FragmentUtil.getElement( "datadisplay");
	// Raise a enquiry load custom event to alert subscribers. Custom event can be found in customEvents.js
	if (enquiryDataTable != null)
	{
		raiseEnquiryLoad( enquiryDataTable, enqname);
	}
	if (window.screenX > 0) {
		windowleft = window.screenX;	// Works in Firefox
		windowtop  = window.screenY;    // save the window left and top co-ordinates
	} else if (window.screenLeft > 0) {
		windowleft = window.screenLeft;	// Works in IE
		windowtop  = window.screenTop;
	}
	resizeOninit = ""; //clear flag init done	
	//focus on enq favourites for keybord access
	var enqfav = window.document.getElementById("enqfav");
	if(enqfav != null )
	{
	     enqfav.focus();
	}
	else
	{
		var enqSelheader = window.document.getElementById("header-table");
		if (enqSelheader != null)
		{
			enqSelheader.focus();
		}
		else
		{
			var enqResheader = window.document.getElementById("enqheader");
			if(enqResheader!=null && enqResheader!="")
			{
                  try { enqResheader.focus();   } catch( e){/** Do nothing if we cant focus**/};
			}
		}
	}
	var tooltipDiv = window.document.getElementById("tooltipDiv");
	if ( tooltipDiv != null )
		{
			// Found the button, set it
			
			tooltipDiv.style.display = "none";
		
		}
}


/**
 * Initialise the Enquiry History. Required for the back button handling
 */ 
 function initEnquiryHistory()
 {
	if(window.BrowserHistory)
	{
		// Initialise & Record the history
		var dataArray = {};
		// Extract the original command that was run to get here
		dataArray.command = getFormFieldValue("enqsel", "WS_initState");

                if (dataArray.command != "")
                {
			// Store the window name for the target
			dataArray.windowname = window.name;
			BrowserHistory.initialize("deal", dataArray);
		}
	}
 }

/**
 * Kills of the default form submit, before calling doEnquiry
 */
function runEnqRequest()
{

	if (window.event!=null)
	{
		window.event.returnValue=false;
	}
	doEnquiry();

}


/**
 * Submit an enquiry
 */
function doEnquiry(downloadFormat)
{
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");
	var defaultButton = FragmentUtil.getElement("defaultButton");
	if(defaultButton != null)
	{
		var defaultButtonValue = defaultButton.getAttribute('state');
		
		if ( defaultButtonValue == 'OFF')
		{
		    defaultButton.setAttribute('state','ON');
			return;
		}
	}
	stopAnyWindowEvents();
	var downloadType;

	// When request is processing  dont allow to change criteria, show busy form
    if (!downloadFormat)
    {
	     busyEnquirySelectionForm();
	}
	// If downloadFormat is defined, then this is a new-style request from the enq response
	if (typeof(downloadFormat) != 'undefined') {
		enqselForm.downloadType.value = downloadFormat;
		downloadType = downloadFormat;
	}
	else if (enqselForm.downloadType) {
		// If downloadType is an input field, then this is a new-style request from the enq response
		// so reset to 'display' if no downloadFormat
        if (enqselForm.downloadType.tagName.toLowerCase() == "input") {
			enqselForm.downloadType.value = "display";
		}
		downloadType = enqselForm.downloadType.value;
	}
	else {
		downloadType = "display";
	}
	if(downloadType == "pdf" && isOpera())
	{
		enqselForm.target = "NEW";
	}
	var name = enqselForm.enqname.value;
	var dropfield = enqselForm.dropfield.value;
	var previousEnqs = enqselForm.previousEnqs.value;
	var previousEnqTitles = enqselForm.previousEnqTitles.value;
	
	var target = "";
	if( FragmentUtil.isCompositeScreen())
	{
		// Contrstruct the command that is going to run so that we can get the right target.
		var command = "Y6l" + " " + name;
	  	target = getCompositeScreenTarget( command);
	  	// For selection screens don't create a NEW window just use the existing one.
	  	if( target = "NEW")
	  	{
	  		target = "";
	  	}
	}
	
	doEnquiryRequest("bo6", name, dropfield, previousEnqs, previousEnqTitles, "", target, "enqsel", downloadType);
}

/**
 * Redirects the submit response to a new window
 */
function submitRequest()
{
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");

	// Save deal window target for restoration later
	savedWindowName = enqselForm.target;

	// Create a new window for the result
	enquiryWindowName = createResultWindow( "", 300, 150 );
	enqselForm.target = enquiryWindowName;
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(enqselForm);

	// Reset deal target back to orginal deal window
	enqselForm.target = savedWindowName;
}



/**
 * Show processing message on Enquiry Selection form
 */
function busyEnquirySelectionForm()
{
	// Set the cursor to an hourglass
	setCursor( "enqsel", "wait" );
	
	//hide enquiry form
	hideEnquiry();
	showState("busy");

	// Move Processing div back to middle of the screen (for Netscape)
	setLeftPosition( "processingPage", 1 );
}


/**
 * Hide the enquiry form data
 */
function hideEnquiry()
{
	// Hide the enquiry selection, breadcrumbs, enquiry response and any messages tab on display
	hideObject( "enquiryData");
    hideObject( "enquiry_response");
    hideObject( "denqresponse");
    hideObject( "enqsel");
	hideObject( "enquirySelection" );
	hideObject( "enquiryResponseCrumbs" );
	hideObject( "enquiryResponseInfo" );
	hideObject( "enquiryResponseToolbar" );
	hideObject( "enquiryResponseData" );
	hideObject( "messages" );
	
	// Also hide any deal items in case we drilled down to a contract screen and are going back
	hideDeal();
}

/**
 * Save the refresh time on the enquiry selection screen for future requests
 */
function saveRefreshTime() {
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");
	var clockBox = FragmentUtil.getElement("refreshtime");
	
	if (clockBox) {
		enqselForm.autoRefresh.value = clockBox.defaultValue;
	}
}

/**
 * Used to start / stop the refresh timer
 */
function toggleTimer() {
	// Save the start time so we can send it up to the server.
	var clockBox = FragmentUtil.getElement("refreshtime");
    refreshClockBox = clockBox;
	if (clockBox) {
		clokBoxId = clockBox.id;	
		var skin = getSkin();
		// refreshToggle may not be created any more ..
		var imgObject = document.getElementById("refreshToggle");
		if (imgObject) {
			// Check that we have valid input, otherwise out
			if(clockBox.value < 1 || clockBox.value > 999) {
				return;
			}
			imgObject.onclick = toggleTimer;
			//Stop Timer if running
			if (clockBox.disabled) {
				enqTimer = clearInterval(enqTimer);
				imgObject.src = "../plaf/images/" + skin + "/tools/start.gif";
				clockBox.disabled=false;
				TimerStopped = "STOPPED";
			}
			else {
				// Start timer
				saveRefreshTime();
				clockBox.disabled="true";
				enqTimer = setInterval('refreshClock();', 1000);
				TimerStopped = "";	
				
				// Change the image
				imgObject.src = "../plaf/images/" + skin + "/tools/stop.gif";
			}
		}
	}
}

/**
 * refresh the clock
 */
function refreshClock() {
	// Get the value of navigator
	var browser  = navigator.appName;
	var clockBox = "";
	// If ie then restore the value, otherwise take from the fragment since 
	// setInterval erases the current fragment name
	
	clockBox = FragmentUtil.getElement(clokBoxId);
	 
	clockBox.value = clockBox.value - 1;
	if (clockBox.value <= 0) {
	// Updating current fragment id to process the refresh request in that fragment
		Fragment.setCurrentFragmentName(clokBoxId.substring(12,clokBoxId.length));	
		enqTimer = clearInterval(enqTimer);
		doEnquiry();
	}
}

/**
 * TODO
 * @param (TODO) TODO
 */
function chartdrill(arg) {
	// Get the drilldown option from the combo box
	var comboBox = FragmentUtil.getElement("chartdrillbox");

	if (comboBox) {
		var selectedIndex = comboBox.selectedIndex;
		var itemNo = comboBox.options[ selectedIndex ].getAttribute( "value" );
		drilldown( itemNo, arg );
	}
}

/**
 * TODO
 * @param (TODO) TODO
 */
function toggleChart(imgObject){

	var skin = getSkin();
	var test = imgObject.src.substring (imgObject.src.length-7);

	if(test=="pie.gif"){
		imgObject.src = "../plaf/images/" + skin + "/enquiry/bar.gif";
		window.showPie();
	}
	else
	{
		// Change the image
		imgObject.src = "../plaf/images/" + skin + "/enquiry/pie.gif";
		window.showBar();
	}
}

/**
 * TODO
 * @param (TODO) TODO
 */
function doDownload(downloadType)
{
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");
	var isArc = window.document.getElementById("isARC"); 

	enqselForm.enqaction.value="ALL";
	enqselForm.download.value = "Y6lo1baC0}56y}t0";
    enqselForm.downloadType.value = downloadType;
    
    if ((isiPad() && isArc != "" && isArc != null && isArc.value == "true") || (downloadType == "preview-html"))
		{
		// Create a new window for the result
		enquiryWindowName = createResultWindow( "NEW", 900, 600 );
		enqselForm.target = enquiryWindowName; 
		}
		
		
	// Submit the request to the Browser Servlet, "Content-Disposition" of 
	// the HTTP response is set to "attachment" which makes the response
	// downloadable. Ajax cannot be used for download. 
	enqselForm.submit();
	enquiryWindowName ="";
    enqselForm.target ="";

	enqselForm.download.value ="";
}

/**
 * Function for download of the transaction statement in the form of OFX or CSV.
 * TODO: needs to take care of case when downloadType is not a combo!
 * @param (TODO) TODO
 */
function downloadStatement(type, enqName) {
	var enqselForm = FragmentUtil.getForm("enqsel");
	enqselForm.enqname.value = enqName;
	var select = type;
    doEnquiryRequest("bo6", enqName, "", "", "", "", "","enqsel", type);
}


/**
 * TODO
 * @param (TODO) TODO
 */
function startenquiry(){
	initEnquiry("",0,0,200,200);
}

/**
 *  Do final window close/replace behaviour immediately prior to window being disappeared by 
 * the browser copied from general.js (left the general unloadWindow alone - possibly used by 
 * older T24 back-end?)
 */
 function unloadEnquiryWindow() {

	// Save the window positions if not in a composite screen	
	if ( closingEnqWindow && window.enqUnloadArgs && ( ! FragmentUtil.isCompositeScreen() ) ) {
		var transId = getFieldValue("transactionId");

		// Escape any % characters in the args (e.g. enquiry names)
		var args = replaceAll(window.enqUnloadArgs, "%", "%25");
		
		//Save the window name for No frames that are inside another composite screen,
		//where the exact window name will get used
		if(top.noFrames){
		 noFramesDealWindowName_GLOBAL = window.name;
        }
		// Display a hidden popup for the command and submit it
		popupHiddenSubmitWindow( "6}CbYloYXh", args, "Y6l" + " " + transId, "Y", "Unload" );
	}
}
	
/**
 * Run just before an enquiry window unloads
 * @param (TODO) TODO
 */
function beforeUnloadEnquiryWindow() {
	// IMPORTANT NOTE: onbeforeunload does not get fired for frames in IE client-side transformation
	// See http://support.microsoft.com/kb/328807

	// Set the arguments for saving the enquiry window position
	var app = "Y6l";
	var ver = "";
	
	// Get the enquiry name - could be stored in one of 2 fields
	var enqName = getFieldValue( "enqname" );
	var enqId = getFieldValue( "enqid" );
	
	if ( enqName != "" ) {
		ver = enqName;
	}
	else if ( enqId != "" )	{
		ver = enqId;
	}

	// Save the window positions in a global if not in ae screen
		if ( ! FragmentUtil.isCompositeScreen() ) {
		//add the resize flag if resize done
		var wincor = getWindowCoordinates();
		if(resizedone)
		{
			window.enqUnloadArgs = app + ":" + ver + ":" + wincor + ":" + resizedone;
		}
		else
		{
			window.enqUnloadArgs = app + ":" + ver + ":" + wincor;
		}
		
	}
	else {
		window.enqUnloadArgs = "";
	}
}
function DeleteFavourite(ptext,theEvent)
{
  	if (theEvent.keyCode == 13) //check if enter key is pressed.
	{
		EnqFavourites.del(ptext);	
	}
  
}

/**
 * Enquiry results and selection pannels should appear inside a scrollable div element if more than 7 lines.
 * @param (TODO) TODO
 */ 
function setscroll(datatable, container){
	var dataTableElem = FragmentUtil.getElement(datatable);
	var containerElem = FragmentUtil.getElement(container);

	// enquiry data scrolled viewport is clipped by default (vertically and horizontally)
	// remove clipping CSS class settings if smaller than clip width
	if (dataTableElem) {
		if (dataTableElem.rows.length < EnqSel.verticalClipRows) {
			removeStyleClass(containerElem, "enquirydataclipped");
			containerElem.style.height = (dataTableElem.clientHeight + 10) + "px";
		}

		if (containerElem) {
			var widthThresh = EnqSel.getHorizontalClipWidth();
			if (dataTableElem.clientWidth < widthThresh) {
				removeStyleClass(containerElem, "enqsel-clipped-horiz");
			}
			else {
				containerElem.style.height = (dataTableElem.clientHeight + 25) + "px";
			}
		}
	}
}

/**
 * Used in conjunction with client specific style sheets to retrieve enquiry data
 */ 
function retrieveEnquiryData()
{	
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");

	//the name of the enquiry to run
	name = enqselForm.enqname.value;

	doEnquiryRequest("ALL", name, "", "", "", "", "NEW", "enqsel", "display");	

	enqselForm.clientStyleSheet.value = "";
}

/**
 * Used to expand rows in a tree view....
 * @param (TODO) TODO
 */ 
function togglerow(sScope, mystyle) {
	var oTable = FragmentUtil.getElement("r"+sScope);
	if (oTable) {
		oTable.style.display = mystyle;
	}
}

/**
 * use to expand collapse the tree view. Note that this function will 
 * recursively expand or contract all the nodes under the root node that has been clicked.   
 * @param (TODO) TODO
 */    
function expandrow(start, count){
	var skin = getSkin(); // which skin to use

	// Get the display style to apply to the rows
	// See Bugzilla Bug 97506: can't use toggle display:block on <tr> elements in Firefox, use "table-row"
	// Although using "" has been suggested as something that will work in FF and IE, it did not work when tried.
	var startRow = FragmentUtil.getElement("r"+start);
	var displayStyle;
	if (isInternetExplorer()) {
		displayStyle = "block";
	} else {
		displayStyle = "table-row";
	}
	if (startRow && startRow.style.display == displayStyle) {
		displayStyle = "none";
	}

	// Now loop through and toggle each row.
	for( var myrow=start; myrow<count+start; myrow++){
		togglerow(myrow,displayStyle);

		// and ensure that we are showing the correct image for the state!
		var imgObject = FragmentUtil.getElement("treestop"+myrow); // pull the image object out
		if(imgObject){
			if(displayStyle=="none"){
				imgObject.src = "../plaf/images/" + skin + "/menu/menu_down.gif";	// make it a plus
			} else {
				imgObject.src = "../plaf/images/" + skin + "/block.gif";	// make it hide
				imgObject.blur(); // Get rid of the little dotted lines around the now invisible icon.
			}
		}
	}
	resizeEnquiry();
}

/**
 * used to show / hide an item
 * @param (TODO) TODO
 */ 
function toggleitemdisplay(sitem){
	var oitem = FragmentUtil.getElement(sitem);

	if (oitem) {
		if (oitem.style.display == "block") {
			oitem.style.display = "none";
		}
		else {
			oitem.style.display = "block";
		}
	}
}

/**
 * Used for collapsable columns.  Passes in the number of the column to collapse. 
 * @param columnNumber, int identifying the column to be collapsed.
 * @param doResize boolean, indicating whether the containerDiv should be resized after collapsing a column.
 */
function switchColumn(columnNumber,doResize) {
    var fastPathColumns = 2; //account for first two columns for Fast Path - first column is a traffic light whilst the second is a checkbox.
	var skin = getSkin(); // which skin to use
	var expandImage = "../plaf/images/"+ skin +"/tools/expand.gif";
	var collapseImage = "../plaf/images/"+ skin +"/tools/collapse.gif";
	
	// Get the main data table and column header text/image elements
	var data = FragmentUtil.getElement("datadisplay");
	var colHeader = FragmentUtil.getElement("columnHeader"+columnNumber);
	var colHeaderText = FragmentUtil.getElement("columnHeaderText"+columnNumber);
	var colHeaderImage = FragmentUtil.getElement("image"+columnNumber);

	// If this is a fastpath enquiry then there will be an extra two columns in the data to accomodate
	var fastpathvar = FragmentUtil.getElement("fastpath");
	if ( (fastpathvar != null) && (fastpathvar != "undefined") && (fastpathvar == "YES") ) {
		columnNumber += 1;
	} else {
		columnNumber -= 1; // Columns are numbered from 1, but indexed from 0.
	}
	
	// Has to be a header image in a collapsible column otherwise it just will not work
    if (colHeaderImage) {
    	// Check custom attrib for state - first time round this will not be there, so evaluates to false
    	if(colHeaderImage.collapsed == undefined)
    	{
    		var header1=colHeader.getAttribute("hideable");
    		if (header1=="yes")
    			{
    				colHeader.setAttribute("hideable","");
    			}
    		var resizeornot = false;
    	}
		if(colHeaderImage.collapsed) {  // expand it ...
			colHeaderImage.collapsed = false;
			colHeaderImage.src = collapseImage;
			//Alignment in case of enquiries with hidden column.
			var header1=colHeader.getAttribute("hideable");
			if (header1=="yes")
				{
					colHeader.setAttribute("hideable","");
				}	
			// show/hide the column header text
			if (colHeaderText) {
				colHeaderText.style.display ='block';
			}
			if (fastpathvar != null)
				{
				columnNumber = columnNumber + fastPathColumns;
				}
			showCells(data,columnNumber);
		}
		else {  // collapse it ...
			colHeaderImage.collapsed = true;
			colHeaderImage.src = expandImage; 			    
			// show/hide the column header text
			if (colHeaderText) {
				colHeaderText.style.display ='none';
				colHeader.style.width = "1px";
			}
			if (fastpathvar != null)
				{
				columnNumber = columnNumber + fastPathColumns;
				}
			hideCells(data,columnNumber);
			if(!resizeornot)
				{
					doResize=true;
				}
		}
	}
	if (doResize == null || doResize == true) {
		resizeEnquiry();
	}
}


/**
 * 'Expands' the data in a column
 * @param data The 'datadisplay' table object
 * @param columnNumber, int. The column to be displayed.
 */
function showCells(data,columnNumber) {
	var isArc = window.document.getElementById("isARC");
	var dataDiv = FragmentUtil.getElement("enquiryDataScroller");
	var isTree = dataDiv.getAttribute("isTree");
	var i=0;
	if(isTree)
	{
		i=1;
	}
	for(; i < data.rows.length; i++) {
		var row = data.rows[i];
		if (row){
			var item = row.cells[columnNumber];
			if (item.savedinnerHTML) {
				item.innerHTML = item.savedinnerHTML;
				item.style.width = "";
			}
        }
	}
}


/**
 * 'Collapses' the data in a column
 * @param data The 'datadisplay' table object
 * @param columnNumber, int. The column to be hidden.
 */
function hideCells(data,columnNumber) {
	var isArc = window.document.getElementById("isARC");
	var dataDiv = FragmentUtil.getElement("enquiryDataScroller");
	var isTree = dataDiv.getAttribute("isTree");
	var i=0;
	if(isTree)
	{
		i=1;
	}
	for(; i < data.rows.length; i++) {
		var row = data.rows[i];
		if (row){
			var item = row.cells[columnNumber];
			item.savedinnerHTML = item.innerHTML;
			item.innerHTML = "";
			item.style.width = "1px";
        }
	}
}


/**
 * Hides any columns that are 'collapsible' during the initialisation of the page.
 */
function hideEnquiryColumns()
{
	// Get the main data table
	var data = FragmentUtil.getElement("datadisplay");

	if (data) {	
		// Try to switch all header cells - it will only work on the hideable ones i.e. that have an imageN object
		var headerCells = data.rows[0].cells;
		for(var i = 0; i < headerCells.length; i++) {
	        switchColumn(i + 1,false); // cols numbered 1 .. N
		}
	}
}

/**
 * Get the previous enquiry to go back to
 */
function getPreviousEnquiry()
{
	// get the 'enquiry' form reference, adjusting for Fragment name as necessary
	var enqForm = FragmentUtil.getForm("enquiry");

	var previousEnqs = enqForm.previousEnqs.value;

	// Extract the last enquiry name in the list of previous enquiries to go back to
	// Enquiries are separated by '_' characters so check for enquiry at start or end of list too

	var enqPos = previousEnqs.lastIndexOf( "_" );

	// If this is a _ at the end of the line then remove it and work backwards
	if ( enqPos == -1 )
	{
		return( "" );
	}
	else if ( enqPos == previousEnqs.length - 1 )
	{
		previousEnqs = previousEnqs.substring( 0, previousEnqs.length - 1);
		enqPos = previousEnqs.lastIndexOf( "_" );

		if ( enqPos == -1 )
		{
			return( "" );
		}
		else
		{
			return( previousEnqs.substring( enqPos + 1, previousEnqs.length ) );
		}
	}
}

/**
 * Used to add items to a workspace from an enquiry drill down.
 * Invokes add item on the parent window (i.e. the workspace)
 * @param (TODO) TODO
 */
 function addworkspaceitem(itemId){
	try
	{
		window.parent.workspace_addWidget(itemId);
	} catch(err){
		alert ("There is no active workspace to add this widget to.");
	}
}

/**
 * TODO
 */
function listFilterChange(){
	var i = 1;
	var mylist = window.document.getElementById("groupfilter");
	var tomatch = mylist.options[mylist.selectedIndex].text;

	do {
		var item = window.document.getElementById("list"+i);
	
		if (item!=null) {
			var group = window.document.getElementById("group"+i);
	
			if (tomatch==group.value || tomatch=="All") {
				item.style.display = "block";
			}
			else {
				item.style.display = "none";
			}
		}
		i++;
	} while(item != null);
}


/**
 * Changes the width of ENQUIRY div elements to auto for enquiries with fixed column widths.
 * This function supports the COL.WIDTH property in ENQUIRY.
 * Normally the width property of the enquiry_response div is set to 1px wide so they 'shrink' around their content.
 * However this causes fixed width enquiries to be truncated in IE (see screenshot in TTS0800054).
 * These (and only these) elements should have their width set to 'auto'
 * See TTS0755018 and TTS0800054 for a description of the problem.
 */
function setEnqResponseDivWidth() {

	var divList = "";
	// Get the fragment name in case of NO.FRAMES mode.
	var fragmentName = Fragment.getCurrentFragmentName();
	if (fragmentName)
	{
		// Extract the current fragment object.
		fragmentName = document.getElementById(fragmentName);
		// Get the current fragment's display_box class div objects. 
		divList = FragmentUtil.getElementsByClassName("display_box","div",fragmentName);
		// Retrieve the denqresponse div object of current fragment.
		var enqResponseDiv = FragmentUtil.getElementsByClassName("denqresponse","div",fragmentName); 
		// Since getElementsByClassName returns as array need to check the length for furthur process.
		if (enqResponseDiv.length > 0)
		{
			// In case of IE attribute has to be className
			var enqReponseDivId = enqResponseDiv[0].getAttribute("className");
			if (!enqReponseDivId)
				// In case of Firefox attribute has to be class
				enqReponseDivId = enqResponseDiv[0].getAttribute("class"); 
		}
	}
	else{
		// Get all display_box div from window/frames mode.
		divList = FragmentUtil.getElementsByClassName("display_box","div","");
	}
	for (var i=0; i<divList.length; i++) // Loop through each div
	{
		// Check the ID of the element, only reset enquiry_response elements
		var divElem = divList[i];
		var divId = divElem.getAttribute("id");
		if (divId == "enquiry_response")
		{
			var tableElem = divElem.lastChild;
			//check if there is an actual table element if so proceed for checking the colgroup tag
            var checktable = FragmentUtil.getElementsByClassName("wrap_words","table","");
			if (checktable.length > 0)
			{
			    // Check if the table element has a colgroup element anywhere inside it, only reset fixed width enquiries
			    if (tableElem.getElementsByTagName("colgroup").length > 0)
			    {
				    divElem.style.width = "auto";
			    }
			}		
			// If denqresponse class div then check with current width.
			if (enqReponseDivId == "denqresponse")
			{	
				// Width of current enquiry display box div.
				var enqResDisplayboxWidth = divElem.clientWidth + 20;
				// Width of current enquiry response div.
				var enqResDIVWidth = enqResponseDiv[0].clientWidth;
				// In case of non retrieval of correct width.
				if (enqResDIVWidth==0)
					enqResDIVWidth = enqResponseDiv[0].offsetWidth;
				// If the display box div is greater than the denqresponse div then make it as auto.
				// So that border of the display box div will be displayed properly based on actual content width.
				if ( enqResDisplayboxWidth > enqResDIVWidth )
				{
					divElem.style.width = "auto";
				}
			}
		}
	}
}

/**
 * Function to expand or collapse all trees in the document
 */
 function expandCollapseAll(expand) {
	// Get a list of fragments to iterate through. These could be in separate frames, or as fragments
	if (top.noFrames) {
		var fragmentList = FragmentUtil.getElementsByClassName("fragmentContainer","td","");
		for (var i=0; i<fragmentList.length; i++) // Loop through each fragment on the page
		{
			var fragId = fragmentList[i].getAttribute("id");
			Fragment.setCurrentFragmentName(fragId);
			// Each row in the enquiry could be the start of an expand / collapse group, so need to check every row.
			for (var rowIndex=1; FragmentUtil.getElement("r" + rowIndex, "", fragId) != null; rowIndex++) //Loop through each row in the fragment
			{
				// Only expand a collapsed group, or collapse an expanded one
				var treeStart = FragmentUtil.getElement("treestart" + rowIndex, "", fragId);
				// Skip rows that don't have treestart icons.
				if (treeStart == null)
				{
					continue;
				}
				var isExpanded;
				var treeStyle = treeStart.parentNode.parentNode.parentNode.style.display;
				if (treeStyle == "none" || treeStyle == "")
				{
					isExpanded = false;
				}
				else
				{
					isExpanded = true;
				}
				if (isExpanded != expand)
				{
					eval(treeStart.parentNode.getAttribute("href").replace("javascript:", ""));
				}
			}
		}
	}
	else
	{
		var frameList = parent.frames;
		for (var i=0; i<frameList.length; i++) // Loop through each frame in the window
		{
			// Each row in the enquiry could be the start of an expand / collapse group, so need to check every row.
			for (var rowIndex=1; frameList[i].document.getElementById("r" + rowIndex) != null; rowIndex++) //Loop through each row in the fragment
			{
				var treeStart = frameList[i].document.getElementById("treestart" + rowIndex);
				if (treeStart == null)
				{
					continue;
				}
				
				// Only expand a collapsed group, or collapse an expanded one
				var isExpanded;
				var treeStyle = treeStart.parentNode.parentNode.parentNode.style.display;
				if (treeStyle == "none" || treeStyle == "")
				{
					isExpanded = false;
				}
				else
				{
					isExpanded = true;
				}
				if (isExpanded != expand)
				{
					frameList[i].eval(treeStart.parentNode.getAttribute("href").replace("javascript:", ""));
				}
			}
		}
	}
}

function toolsMenuPopup(thisField) {
	
	// thisField is the containing element (td)
	// use display AND visibility styles, in case re-positioning is obvious on slow system
	var menuCanvas = thisField.getElementsByTagName('iframe')[0];
	var menuBox = thisField.getElementsByTagName('div')[0];
	currentDropDown_GLOBAL = menuBox.id;	

	// Set positions of popup menu box - for IE, set top as well
	menuBox.style.display = "block";
	menuBox.style.left = getElementX(thisField) + 'px';
	menuBox.style.top = (getElementY(thisField) + thisField.clientHeight) + 'px';
	menuBox.style.visibility = "visible";

	// Set similar positions, and size from the menu box, for iframe 'screen' which blocks out any combo boxes on IE6
	menuCanvas.style.display = "block";
	menuCanvas.style.left = getElementX(thisField) + 'px';
	menuCanvas.style.top = (getElementY(thisField) + thisField.clientHeight) + 'px';
	menuCanvas.style.width = (menuBox.clientWidth + 4) + 'px';
	menuCanvas.style.height = (menuBox.clientHeight + 4) + 'px';
	menuCanvas.style.visibility = "visible";
}

function toolsMenuPopdown(menuBox) {

	var menuParent = menuBox.parentNode; // td
	var menuCanvas = menuParent.getElementsByTagName('iframe')[0];
	
	menuBox.style.display = "none";
	menuCanvas.style.display = "none";
	menuBox.style.visibility = "hidden";
	menuCanvas.style.visibility = "hidden";
}

function toolsMenuLeave(thisEvent, thisField) {

	var menuBox = thisField.getElementsByTagName('div')[0];
    var toElement = thisEvent.relatedTarget || thisEvent.toElement;
    
	// reset display AND visibility styles - see popup function
    if ((toElement != null) && (! thisField.contains(toElement))) {
		toolsMenuPopdown(menuBox);
    }
}

/**
 *  @singleton
 * 'Singleton' object for enquiry selection utilities (new).
 */
EnqSel = {

/**
 * Notional max number of selection fields (999 reserved for new AUTOREFRESH field).
 * Assuming we will not get anything like this number, otherwise enquiry would be horrendous
 */
	maxFields: 998,

/**
 * Number of separate sort field rows in new enq sel options box
 */
	maxSortFields: 3,

/**
 * Threshold for vertical selection field box overflow
 */
	verticalClipRows: 8,

/**
 * Threshold for horizontal selection field box overflow
 * Should be set from CSS, or defaults to below
 */
	horizontalClipWidth: 0,

/**
 * Default threshold for horizontal selection field box overflow
 */
	horizontalClipWidthDefault: 600,
	
/**
 * Retrieve threshold for horizontal selection field box overflow
 * Bootstraps on first call, getting value from CSS sheets or default
 */
	getHorizontalClipWidth: function() {
		if (this.horizontalClipWidth == 0) {
			this.horizontalClipWidth = parseInt(getCSSEntry('.enqsel-clipped-horiz', 'width', EnqSel.horizontalClipWidthDefault));
		}
		return this.horizontalClipWidth;
	},

/**
 * Toggle selection options (sorting + refresh timer) using CSS class switching
 * Switch link text also, alternative text supplied at transform time
 */
	toggleOptions: function() {

		var optSection = FragmentUtil.getElement("enqselopts");

		// show/hide enqsel options box
		if (hasStyleClass(optSection, 'hidden')) {
			removeStyleClass(optSection, 'hidden');
		}
		else {
			addStyleClass(optSection, 'hidden');
		}

		// switch link text
		var optLink = FragmentUtil.getElement("enqselopts_toggle");
		var tmpText = optLink.getAttribute("alt-text");
		optLink.setAttribute("alt-text", optLink.firstChild.nodeValue);
		optLink.firstChild.nodeValue = tmpText;
	}

};

/**
 *  @singleton
 * 'Singleton' object for favourites handling.
 */
EnqFavourites = {
	
/**
 * Marker text for error message in HTML
 */
	HTML_ERROR_MARKER: "FAVOURITES-REQUEST-ERROR:",

/**
 * Regular expression to check for valid favourite name entry (see also EB.SELECTION.FAVOURITES)
 */
	NAME_REGEX: "^[ a-zA-Z0-9']+$",

/**
 * Max num chars for favourite id field (see also EB.SELECTION.FAVOURITES)
 */
	NAME_MAX_LEN: 35,
	
/**
 * Adds a favourite entry, prompting user for a name used to display / run the favourite
 * Invokes background (AJAX) request to add the favourite entry - response is new favourites
 * panel content, processed by async handler addFavouriteHandler
 */
	add: function(promptText, errText, helpText) {
		var favname = prompt(promptText, "");              //prompt called here to avoid defaultButton state change
		var defaultButton = FragmentUtil.getElement("defaultButton");
		if ( defaultButton != null)
		{
			defaultButton.setAttribute("state","OFF");				//if we tabout from  icon it does not go submit
		}
		
		if (favname == null) {
			return;  // user hit cancel
		}

		// Do not allow whitespaces as a name
		var trimmedFavName = favname.replace(/\s+/g,'');
		
		// RE to check for empty or invalid name
		var pattern = new RegExp(EnqFavourites.NAME_REGEX);

		if ((favname.length <= EnqFavourites.NAME_MAX_LEN) && (pattern.test(favname)) && trimmedFavName.length > 0) {
			var enqselForm = FragmentUtil.getForm("enqsel");
			var curEnqName = enqselForm.enqname.value;
		
			var _favAction_ = "t00CrtR" + ":" + favname;
            var dropField = enqselForm.dropfield.value; // get the dropfield value
			doEnquiryRequest(_favAction_, curEnqName,dropField, "", "", "", "", "enqsel", "01XKyta");
		}
		else {
			alert (errText + '\n\n' + helpText);
		}
	},

/**
 * AJAX request handler - updates the favourites panel, or show error dialog
  * @param {Object req} the AJAX request object
 */
    favouriteHandler: function(req) {
        if (req.readyState == 4) {
            // get HTML text from AJAX request
			var favText = req.responseText;

			// check reply doc for error element
			var errorStartIndex = favText.indexOf(EnqFavourites.HTML_ERROR_MARKER);
			if (errorStartIndex >= 0) {
				var errorEndIndex = favText.indexOf("<", errorStartIndex);
				var favT24Error = favText.substring(errorStartIndex + 25, errorEndIndex);
				
				alert ("Error: " + favT24Error);
			}
			else {
				var favBox = FragmentUtil.getElement("enqselfavs");

				// just using innerHTML on the outer element does not seem to work on the div for Firefox
				// So use this handy technique (as per Javascript anthology, and on the web)
				var favDiv = favBox.getElementsByTagName('div')[0];
				var newDiv = document.createElement('div');
				newDiv.innerHTML = favText;
				newDiv.className = 'display_box';
				favBox.replaceChild(newDiv, favDiv);
				// to set focus on favourites after adding/deleting favourite link
				if (document.getElementById('enqfav') != null)
				{
					document.getElementById('enqfav').focus();
				}
			}
        }
    },


/**
 * Delete a user favourite entry
 * @param {String favname} the name of the favourite entry
 */
	del: function(favname) {
		var defaultButton = FragmentUtil.getElement("defaultButton");
		if ( confirm("Are you sure you want to delete favourite " + favname) ) {
		if ( defaultButton != null) 								//defaultButton state changed here to avoid form submit
		{
			defaultButton.setAttribute("state","OFF");				//if we tabout from  icon it does not go submit
		}
			var enqselForm = FragmentUtil.getForm("enqsel");
			var curEnqName = enqselForm.enqname.value;
			var favAction = "0YyCrtR" + ":" + favname;
			var dropField = enqselForm.dropfield.value;
			doEnquiryRequest(favAction, curEnqName,dropField, "", "", "", "", "", "01XKyta");
		}
	},

/**
 * Run a user favourite entry
 * @param {String id} the name of the favourite entry
 */

	run: function(favname) {
		var enqselForm = FragmentUtil.getForm("enqsel");
		var curEnqName = enqselForm.enqname.value;
		var dropField = enqselForm.dropfield.value;
		doEnquiryRequest("XYyYeh1}6", curEnqName,dropField, "", "", "@" + favname, "", "", "01XKyta");
	}
};


/**
 * Resizes the container div around the enquiry taking into account the window size and content size.
 */
function resizeEnquiry() {

	// Get handles on all the elements we need
	var sizerWidth = '';
	var headingRowWidth = '';
	var dataRowWidth = '';
	var containerDiv = FragmentUtil.getElement("enquiry_response");
	var headingDiv = FragmentUtil.getElement("enquiryHeadingScroller");
	if (headingDiv == null) {
		return; // Means there are no enquiry results, so don't resize the window.
	}
	var headingSizer = FragmentUtil.getElement("enquiryHeadingSizer");
	var dataSizer = FragmentUtil.getElement("enquiryDataSizer");
	var dataDiv = FragmentUtil.getElement("enquiryDataScroller");
	var isTree = dataDiv.getAttribute("isTree"); 
	if (isTree) {
		return;
	}
	var headingRow = headingDiv.getElementsByTagName("TR")[0];
	var dataRow = dataDiv.getElementsByTagName("TR")[0];

	// First set the size of the sizing divs to very large, so there's enough space to size the tables without any wrapping.
	headingSizer.style.width = "10000px";
	dataSizer.style.width = "10000px";

	// Next make sure the column widths in the heading and data line up
	matchSizes(headingRow,dataRow);

	// Set the widths of the headingSizer and dataSizer to the size of the bigger one
	if (isOpera())
	{
	    headingRowWidth = headingRow.offsetWidth;
		dataRowWidth = dataRow.offsetWidth;
	}
	else if(isInternetExplorer() || isFirefox())
	{
		headingRowWidth = headingRow.clientWidth;
		dataRowWidth = dataRow.clientWidth;
	}
	else
	{
	headingRowWidth = headingRow.clientWidth;
	dataRowWidth = dataRow.clientWidth+dataRow.clientHeight;	//Alignment issues with safari and google chrome in ARC-IB.
	}
	
	if (headingRowWidth < dataRowWidth)
	{
		sizerWidth = dataRowWidth;
	}
	else
	{
		sizerWidth = headingRowWidth;
	}
	if (isFirefox()) {sizerWidth += 20;} // Firefox needs extra pixels here
	headingSizer.style.width = sizerWidth;
	dataSizer.style.width = sizerWidth;
	enqdtscroll = window.document.getElementById("EnquiryDataScroll").value;
	if (enqdtscroll == "NO") {
		return;  // Don't want to resize.
	}


	// Set the width of the containerDiv. This must be:
	// a) at least the size of the header (which is distinct from the heading)
	// b) at most the size of the data, or the size of the window, whichever is smaller
	// c) if in noframes mode, then ignore the window size.
	var windowWidth = document.body.parentNode.offsetWidth - 20;
	if (!isFirefox()) {windowWidth -= 20;} // IE needs an extra 20 pixels off
	var containerWidth = 0;
	// Make the sizerWidth slightly larger than necessary, so it can also contain a vertical scrollbar if necessary.
	sizerWidth += 30;
	
	if ( (sizerWidth < windowWidth) || top.noFrames) {
		containerWidth = sizerWidth;
	} else {
		containerWidth = windowWidth;
	}
	
	// The container width should never be less than the header (as opposed to 'heading') size
	var headerWidth = "";
	// In case of noframes mode extract the correct enquiryHeaderContainer object to find out the size of 
	// header which should always greater than div width size.
	if ( top.noFrames ) {
		// Have current fragment name.
		var currentFragmentName = Fragment.getCurrentFragmentName();
		// Get the object of current fragment name.
		var currentFragmentObject = document.getElementById(currentFragmentName);
		// Extract all child tables of current fragment object.
		if (currentFragmentObject)
		{
		    var currentDivObjects = currentFragmentObject.getElementsByTagName("table");
		}
		var tableObject = "";
		if ( currentDivObjects ) {
			for ( var h =0; h < currentDivObjects.length; h++) {
				tableObject = currentDivObjects[h].getAttribute("id");
				// Check the current fragment object's table Id attribute is equal.
				if ( tableObject == "enquiryHeaderContainer" ) {
					// Extract width of the table.
					headerWidth = currentDivObjects[h].clientWidth+10;
				}
			}
		}
	}
	else {
		// In case of frames mode header object can be extracted from document object. 
		// Since it will return only the current frames object width.
		headerWidth = document.getElementById("enquiryHeaderContainer").clientWidth + 10;
	}
	
	if (containerWidth < headerWidth) {
		containerWidth = headerWidth;
	}

	if (containerDiv!=null)
	{
	containerDiv.style.width = containerWidth;
	containerDiv.style.overflow = "visible"; //To get total enquiry result while printing in firefox.
	}
	
	// Set the width of the dataDiv and headingDiv - they depend on the containerWidth
	dataDiv.style.width = containerWidth - 12;
	headingDiv.style.width = containerWidth - 30;

	// Set the height of dataDiv, but only if we're in frames mode.
	if (!top.noFrames) {
	
		var dataHeight = dataSizer.clientHeight;
		// Add 20 pixels, so it can also contain a horizontal scrollbar if necessary.
		dataHeight += 20;
		
		// Get the position of the top of the dataSizer relative to the top of the window.
		var topHeight = totalOffsetTop(dataSizer);
		// Get the height of the bottom of the page
		var enqSelForm = FragmentUtil.getElement("enqsel");
		if (enqSelForm != null)
		{
			var bottomHeight = enqSelForm.offsetHeight;
			bottomHeight += 20; // Add 20 pixels for the horizontal scrollbar on the window.
			var windowHeight = getWindowHeight();
			var availableHeight = windowHeight - topHeight - bottomHeight;
			// In Firefox the scrollbar disappears when there is less than 50 pixels height.
			if (availableHeight < 52) {
				availableHeight = 51; // For Firefox - otherwise the scrollbars disappear
			}
			if (availableHeight > dataHeight) {
				dataDiv.style.height = dataHeight;
			} else {
				dataDiv.style.height = availableHeight;
			}
		}
		
	}
}


/**
 * Gets the position of the given element relative to the window rather than the containing element.
 * @param element The element in question.
 * @return the number of pixels fromt the top of the window to the top of the element.
 */
function totalOffsetTop(element) {
	var totalOffsetTop = 0;
	while (element != null) {
		totalOffsetTop += element.offsetTop;
		element = element.offsetParent;
	}
	return totalOffsetTop;
}

/**
 * Gets the height of the window in pixels handling browser differences.
 * @return The window height in pixels.
 */
function getWindowHeight() {
	var windowHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		windowHeight = window.innerHeight; // Works in Firefox
	} else {
		windowHeight = document.body.parentNode.offsetHeight - 16; // Same thing in IE;
	}
	return windowHeight;
}

/**
 * Resizes two given table rows so that the cells of each are the size of the bigger cell in each row.
 * @param headingRow The 'heading' row of the data
 * @param dataRow The data row.
 */
function matchSizes(headingRow, dataRow) {
	var headingCells = headingRow.cells; // Gets the immediate children only, but can include whitespace nodes.
	var dataCells = dataRow.cells;

	// Get the least of heading columns or data columns (these can vary eg drilldowns don't have headings)
	var columnTotal = headingCells.length;
	if (headingCells.length > dataCells.length) {
		columnTotal = dataCells.length;
	}
	
	// Iterate through the columns and match the sizes
	for (var i=0; i<columnTotal; i++) {
		var headingCell = headingCells[i];
		var dataCell = dataCells[i];
		//Alignment in case of enquiries with hidden columns
		var heading1=headingCell.getAttribute("hideable");
		if(heading1=="yes")
			{
				dataCell.style.width=dataCells.length;
				headingCell.style.width = dataCell.style.width;
			}	
		else
			{
		var headingWidth = headingCell.clientWidth;
		if (headingWidth == '0')
		{
			return;
		}

		var dataWidth = dataCell.clientWidth;

		// We need to know how much padding has been applied to each cell, so we can calculate the width we need to make each cell
		var paddingLeft = readComputedStyle(headingCell,"paddingLeft");
		var paddingRight = readComputedStyle(headingCell,"paddingRight");
		var paddingOffset = paddingLeft.match(/\d*/)*1 + paddingRight.match(/\d*/)*1;

		// Now set the width of each cell to the max width of the two
		if (headingWidth > dataWidth) {
			dataCell.style.width = headingWidth - paddingOffset;
			headingCell.style.width = dataCell.style.width;
		} else {
			headingCell.style.width = dataWidth - paddingOffset;
			dataCell.style.width = headingCell.style.width;
		}
			}
	}
}


/**
 * Gets the style properties of an element, including styles specified in an external stylesheet, not just inline styles
 * @param element The element
 * @param styleName The style property required, eg 'paddingLeft'
 * @return The value of the style
 */
function readComputedStyle(element,styleName) {
	var computedStyle = null;
	if (typeof element.currentStyle != "undefined") {
		computedStyle = element.currentStyle; // This works in IE
	} else {
		computedStyle = document.defaultView.getComputedStyle(element,null); // This works in Firefox
	}
	return computedStyle[styleName];
}


/**
 * Keeps the heading and data columns aligned by scrolling the heading by the amount the data is scrolled by.
 */
function scrollHeading() {
	var headingDiv = FragmentUtil.getElement("enquiryHeadingScroller");
	var dataDiv = FragmentUtil.getElement("enquiryDataScroller");
	headingDiv.scrollLeft = dataDiv.scrollLeft;
}

/**
* Instead of calling enqfavourites.add method on every keypress,
* this has to be checked with the event code and only invoked when 'enter' key is pressed.
*/

function AddFavourite(ptext,etext,htext,event)
 {
 	if (event.keyCode == 13) //check if enter key is pressed.
	{
	EnqFavourites.add(ptext,etext,htext,event);	
	}
 }
function tableRowSetFocus(thisEvent)
{
    if(thisEvent.keyCode == '9')
	{
     var focusRow = thisEvent.target || thisEvent.srcElement;
	  if(focusRow.nextSibling == null)
	  {
	    var focusFirstRow = document.getElementById("rowHeader");
		if(focusFirstRow != null)
		{
			focusFirstRow.focus();
		}
	  }
	 }
}