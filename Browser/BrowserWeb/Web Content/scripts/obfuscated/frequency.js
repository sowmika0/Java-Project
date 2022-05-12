/** 
 * @fileoverview TODO
 *
 * @author XXX
 * @version 0.1 
 */

var freqDropWidth_GLOBAL = 450;
var freqDropDown_GLOBAL = "";

/**
 * Displays a dropdown version of the frequency control.
 * @param {TODO} thisEvent
 */ 
function dropFrequencyDisplay(dropFieldId)
{
	// Get the drop field
	var dropFieldObject = getFormField( currentForm_GLOBAL, dropFieldId );

    // Set fixed height/width dimensions
	var dropWidth = freqDropWidth_GLOBAL;
	
    // Set general form params
    var application = getFormFieldValue( currentForm_GLOBAL, "application" );
    var version = getFormFieldValue( currentForm_GLOBAL, "version" );
    var enqname = getFormFieldValue( currentForm_GLOBAL, "enqname" );
    var genForm = FragmentUtil.getForm("generalForm");
    genForm.requestType.value = "oh1y1haCb}oh16Y";
    genForm.routineName.value = "}XC>YhCrbYloY6ea";
	genForm.routineArgs.value = dropFieldId + "|" + application + "|" + enqname + "|" + version;
	freqDropDown_GLOBAL = dropFieldId;
	// Display the popup with given size/position, and run the request given by form
	runPopupDropDown(genForm, dropFieldObject, dropWidth, -1);
}


/**
 * Loads the correct display for the field.
 * @param {String dropFieldId} The ID of the input element for the frequency control
 * @param {String fragmentName} (Optional) The suffix of the fragment
 */
function initFrequency(dropFieldId,fragmentName) {
    // Reset the control
    fqu_resetControl();
    
  	// Get the frequency value from the right frame / fragment
  	var dropFieldForm = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
  	var dropFieldFormId = dropFieldForm.getAttribute("id");
    var dropFieldObject = getFormField( dropFieldFormId, dropFieldId );
    var currentVal = dropFieldObject.value;
    if (currentVal != "")
    {
        // Load the pattern
        fqu_loadPattern(currentVal);    	
    }
    
    // Focus set and scroll the entire element into view
 	var fquPattern =  window.document.getElementById("Frequency_Pattern");
	if (fquPattern != undefined)
	{
		fquPattern.focus();
	}
 	var fquFocus = window.document.getElementById(dropFieldId + ":div");
	if (fquFocus != undefined)
	{
		fquFocus.scrollIntoView(false);
	}
}

/**
 * Minimises the content of the div element.
 */ 
function fqu_close(dropFieldId)
{
	var dropFieldFocus = document.getElementById(dropFieldId);
	if (dropFieldFocus != null)
    {
    	try 
    	{ 
    		dropFieldFocus.focus();
    		dropFieldFocus.value = dropFieldFocus.value; 
    	} 
    	catch( e)
    	{
    		// Do nothing if we cant focus
    	}
    }
	hidePopupDropDown(dropFieldId);
	// The calendar popup might still be open, so try to hide it again, just in case.
	hidePopupDropDown("fqu:nextDate");
}
/**
 * Minimises the content of the div element.
 * when we press enter key.
 */
function fqu_closeonKey(dropFieldId,thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}

	fqu_close(dropFieldId);
	
	var tooltipDiv = window.document.getElementById("tooltipDiv");
	if ( tooltipDiv != null )
		{
			// Found the button, set it
			
			tooltipDiv.style.display = "none";
		
		}
	
}
/**
 * Save the content of the div element.
 * when we press enter key.
 */
function fqu_saveDateonKey(dropFieldId,thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}

	fqu_saveDate(dropFieldId);
	var tooltipDiv = window.document.getElementById("tooltipDiv");
	if ( tooltipDiv != null )
		{
			// Found the button, set it
			
			tooltipDiv.style.display = "none";
		
		}
}
/**
 * Minimises the content of the div element.
 */ 
function fqu_saveDate(dropFieldId)
{
	// Now get the date is it has been given
	var nextDate = document.getElementById("fqu:nextDate").value;
	nextDate = nextDate.replace(/^\s+|\s+$/g, ''); // Trim any spaces

	// Determine which tab is active then build the pattern
	var fquType = recurr_getRadioValue("fqu:frequency");
	// parse the date...
	var fquPattern = "";
	switch (fquType) {
		case "N": fquPattern = ""; break;
		case "D": fquPattern = fqu_getDaily(); break;
		case "W": fquPattern = fqu_getWeekly(); break;
		case "M": fquPattern = fqu_getMonthly(); break;
		case "P": fquPattern = fqu_getEbFrquency(); break;		
		default: fquPattern = "";
	}

	var parsedDate = nextDate;
	if (parsedDate == "") {
		parsedDate = fquPattern;
	} else if (fquPattern != "") {
		parsedDate += fquPattern;
	}
	
	var dropFieldObj = document.getElementById(dropFieldId);
	if (dropFieldObj != null )
    {
    	try 
    	{
    		dropFieldObj.focus(); 
    	}
    	catch( e)
    	{
    		// Do nothing if we cant focus
    	}
	}
	// Save it
	setFormFieldValue( currentForm_GLOBAL, dropFieldId, parsedDate);
	
	// Run any field events if the value has changed
	// set the enrichment, but only if the field has been hidden
	if ( dropFieldObj.getAttribute( "type") == "hidden" )
		{
		setEnrichment(dropFieldId, parsedDate);
		}
	
	var oldValue = dropFieldObj.oldValue;
	
	if ( parsedDate != oldValue )
	{
		doFieldChangeEvent( dropFieldObj );
	}
	
	// close popup
	fqu_close(dropFieldId);
}

/**
 * TODO
 */ 
function fqu_getDaily()
{
	var out = "BSNSS";
	if (document.getElementById("fqu:Daily").checked)
	{
		out = "DAILY";				
	}
	return out.toString();
}

/**
 * TODO
 */ 
function fqu_getWeekly()
{
	// Get the value
	var noOfWeeks = document.getElementById("fqu:weekly").value;
	
	var out = "WEEK" + noOfWeeks;
	
	return out.toString();
}

/**
 * TODO
 * @return TODO
 */ 
function fqu_getMonthly()
{
	var out = "TWMTH";
	if (document.getElementById("fqu:M:Every").checked)
	{
		var month = document.getElementById("fqu:M:MonthNumber").value;
		if (month.substr(0,1) != 0)
		{
			month = fqu_padout(month);
		}
		
		var day = document.getElementById("fqu:M:DayNumber").value;
		
		out = "M" + month + day;
	}
	return out.toString();	
}

/**
 * TODO
 * @param {TODO} number
 */ 
function fqu_padout(number) 
{ 
	return (number < 10) ? '0' + number : number; 
}

/**
 * TODO 
 * @return TODO
 */ 
function fqu_getEbFrquency()
{
	var out = document.getElementById("fqu:ebFrequencyId").value;
	out = out.toString();
	if(out.indexOf("-") > -1){
		out = out.substring(0,out.indexOf("-"));
	}		
	return out;

}

/**
 * Check the given radio button and display it's div, hide all the others
 */ 
function fqu_swicthTab(freqType) {
	
	var errorDiv  = document.getElementById("FQU_Invalid_Format");
	errorDiv.style.display = "none";

	document.getElementById("fqu:frequency:" + freqType).checked = true;
	var freqList = "D;W;M;P".split(";");
	for (var i=0; i<freqList.length; i++) {
		var currentRadio = document.getElementById("fqu:frequency:" + freqList[i]);
		var currentDiv  = document.getElementById("Pattern_Details:" + freqList[i]);
		if (currentRadio.checked) {
			currentDiv.style.display = "inline";
		} else {
			currentDiv.style.display = "none";
		}
	}
}

/**
 * Draws the control in a 'clean' state
 */ 
function fqu_resetControl()
{
	// Set Defaults on all tabs
	fqu_setNextDate("");
	fqu_setEBFrequency("");
	fqu_setMonthly(false, "01", "01");
	fqu_setWeekly(1);
	fqu_setDaily(true);
}

/**
 * TODO
 * @param (TODO) TODO
 */ 
function fqu_setNextDate(date)
{
	// set the date
	document.getElementById("fqu:nextDate").value = date;
}

/**
 * TODO
 * @param (TODO) daily
 */ 
function fqu_setDaily(daily)
{
	fqu_swicthTab("D");
		
	if(daily)
	{
		// Check the radio
		document.getElementById("fqu:Daily").checked = true;
	}
	else
	{
		// Set the value & check the radio
		document.getElementById("fqu:BusinessDay").checked = true;
		
	}
}

/**
 * TODO
 * @param (TODO) noOfWeeks
 */ 
function fqu_setWeekly(noOfWeeks)
{
	fqu_swicthTab("W");
	
	// Set the value
	document.getElementById("fqu:weekly").value = noOfWeeks;
	
}

/**
 * TODO
 * @param (TODO) twice
 * @param {} month
 * @param {} day
 */ 
function fqu_setMonthly(twice, month, day)
{
	fqu_swicthTab("M");
	
	// Set the default values
	document.getElementById("fqu:M:MonthNumber").value = "01";
	document.getElementById("fqu:M:DayNumber").value = "01";
	// Set the values
	// 
	if (twice)
	{
		// Set radio value
		document.getElementById("fqu:M:Twice").checked = true;
	}
	else
	{
		// Set radio
		document.getElementById("fqu:M:Every").checked = true;
		
		// Set the values
		document.getElementById("fqu:M:MonthNumber").value = month;
		document.getElementById("fqu:M:DayNumber").value = day;
	}
}

/**
 * TODO
 * @param (TODO) ebFVal
 * @return bFound
 * @type TODO
 */ 
function fqu_setEBFrequency(ebFVal)
{
	// Search existing values and set
	var idList = document.getElementById("fqu:ebFrequencyId");
	
	var bFound = false;
	
	for (var i=0; i<idList.length; i++) 
	{
		if (document.getElementById("fqu:ebFrequencyId").options[i].value == ebFVal) 
		{
			fqu_swicthTab("P");
		
			document.getElementById("fqu:ebFrequencyId").value = ebFVal;
			bFound = true;
			break;
		}
	}
	
	return bFound;
}

/**
 * Parse a pattern like "20060812 WEEK7" into the date + frequency part.
 * Possible dates that need to be parsed are as follows:
 * M0631
 * 20060812
 * 20060812WEEK7
 * 20060812 WEEK7
 * 20 DEC 2000
 * 20 DEC 2000 M0631
 * @param (String) fieldValue The value of the input field passed in.
 */
function fqu_loadPattern(fieldValue)
{
	// Extract and display the frequency pattern part of the string
	var freqPart = "";
	var tempfieldValue = fieldValue;
	if(tempfieldValue.length > 5)	//take the last 5 characters if its length is more(containing date and frequency).
	{
		tempfieldValue = fieldValue.substring((fieldValue.length-5),fieldValue.length);
	}
	// First check the defined list for a match
	var definedList = document.getElementById("fqu:ebFrequencyId").options;
	for (var i=0; i<definedList.length; i++) {
		if (definedList[i].value.indexOf(tempfieldValue) > -1) {	//check whether it is user defined frequency
			freqPart = definedList[i].value;
		}
	}
	// Most likely freqPart will still be "", so check if it's a normal frequency pattern
	if (freqPart == "") {
		var freqPattern = /[DBWTM][AILYSNEKTWHM1234567890]{0,4}$/;	// Ends with DBWTM followed by up to 4 other characters
		if (freqPattern.test(fieldValue)) {
			freqPart = fieldValue.match(freqPattern)[0];
		}
	}
	fqu_processPattern(freqPart);
	freqPart = freqPart.substring(0,5);		//remove the remaining part if it is User defined frequency

	// Add the date part	
	var datePart = fieldValue.replace(freqPart,"");	// Remove any frequency part
	datePart = datePart.replace(/^\s+|\s+$/g, '');	// Trim any spaces
	fqu_setNextDate(datePart);
}

/**
 * TODO
 * @param (TODO) fquPattern
 */ 
function fqu_processPattern(fquPattern)
{
	// Parse the pattern & populate the control
	
	// check for standard patterns:
	// DAILY D, DA, DAI, DAIL
	// BSNSS B, BS, BSN, BSNS
	// WEEKX (where X is a Numeric) W
	// TWMNT T, TW, TWM, TWNN
	// MXXXX M 

	// If the pattern is not recognised check if it is an EB.FREQUENCY ID
	
	// If no match is found display the error message
	
	var daily = "DAILY";
	var bsnss = "BSNSS";
	var twmnt = "TWMTH";
	var showError = false;
	
	var patternLength = fquPattern.length;
	
	if (patternLength == 0 && document.getElementById("fqu:frequency:N") != null)
	{
		fqu_swicthTab("N");
	}
	else if (fquPattern == (daily.substr(0,patternLength)))
	{
		fqu_setDaily(true);
	}
	else if (fquPattern == (bsnss.substr(0,patternLength)))
	{
		fqu_setDaily(false);
	}
	else if (fquPattern == (twmnt.substr(0,patternLength)))
	{
		fqu_setMonthly(true,01,01);
	}
	else if (fquPattern.substr(0,1) == "W")
	{
		fqu_processWeek(fquPattern);
	}
	else if (fquPattern.substr(0,1) == "M")
	{
		fqu_processMonth(fquPattern);
	}else
	{
		// Check EB.FREQUENCY
		if (fqu_setEBFrequency(fquPattern) == false)
		{
			showError = true;						
		}
	}
	
	if (showError)
	{
		fqu_showHideErrorMessage(true);
	}	
	

}

/**
 * TODO
 * @param (TODO) monthPattern
 */ 
function fqu_processMonth(monthPattern)
{
	// Validation Rules
	// M		Invalid			
	// MX		Invalid			
	// MXX		Invalid			
	// MXXX		Invalid			
	// MXXXX	OK	Where:	1ST XX	01 -- 99
	//						2ND XX	01 -- 31

	var mtLen = monthPattern.length;
	
	if (mtLen != 5)
	{
		// Invalid
		fqu_showHideErrorMessage(true);
	}
	else
	{
		var lastchars = monthPattern.substr(1, mtLen);
		
		if (isNaN(lastchars))
		{
			// Check if it could be an EB.FREQUENCY
			if (fqu_setEBFrequency(monthPattern) == false)
			{
				// Invalid
				fqu_showHideErrorMessage(true);						
			}					
		}
		else
		{
			// Get Month Patter
			var month = lastchars.substr(0,2);
			
			if (month != 00)
			{
				// Get Day Pattern
				var day = lastchars.substr(2,4);
				//
				if ((day < 1)||(day > 31))
				{
					// Invalid
					fqu_showHideErrorMessage(true);					
				}
				else
				{
					// Set
					fqu_setMonthly(false, month, day);			
				}
			}
			else
			{
				// Invalid
				fqu_showHideErrorMessage(true);				
			}
		}
				
	}

}

/**
 * TODO
 * @param (TODO) wkPattern
 */ 
function fqu_processWeek(wkPattern)
{
	// Validation Rules
	// WEEK		Invalid			
	// WX		OK			
	// WEX		OK			
	// WEEX		OK			
	// WEEKX	OK	Where:	X	1 -- 9
	// WXX		Invalid			
	// WEXX		Invalid			
	// WEEXX	Invalid			
	// WEEKXX	Invalid
	
	var wkLen = wkPattern.length;
	if (wkPattern == "WEEK")
	{
		// Invalid
		fqu_showHideErrorMessage(true);
	}
	else if(wkLen == 2)
	{
		fqu_checkWeekNumber(wkPattern);
	}
	else if(wkLen == 3)
	{
		if (wkPattern.substr(0,2) == "WE")
		{
			fqu_checkWeekNumber(wkPattern);						
		}
		else 
		{
			// Invalid
			fqu_showHideErrorMessage(true);			
		}
	}
	else if (wkLen == 4)
	{
		if (wkPattern.substr(0,3) == "WEE")
		{
			fqu_checkWeekNumber(wkPattern);						
		}
		else 
		{
			// Invalid
			fqu_showHideErrorMessage(true);			
		}		
	}
	else if (wkLen == 5)
	{
		if (wkPattern.substr(0,4) == "WEEK")
		{
			fqu_checkWeekNumber(wkPattern);						
		}
		else 
		{
			// Check if it could be an EB.FREQUENCY
			if (fqu_setEBFrequency(fquPattern) == false)
			{
				// Invalid
				fqu_showHideErrorMessage(true);						
			}			
		}		
	}
	else
	{
		// Invalid
		fqu_showHideErrorMessage(true);						
	}
}

/**
 * TODO
 * @param (TODO) wkPattern
 */ 
function fqu_checkWeekNumber(wkPattern)
{
	var wkLen = wkPattern.length;
	var lastchar = wkPattern.substr((wkLen-1), wkLen);

	if (isNaN(lastchar))
	{
		fqu_showHideErrorMessage(true);			
	}
	else
	{
		if (lastchar == 0)
		{
			// Don't allow 0
			fqu_showHideErrorMessage(true);						
		}
		else
		{
			fqu_setWeekly(lastchar);		
		}
	}	
}

/**
 * TODO
 * @param (TODO) bShow
 */ 
function fqu_showHideErrorMessage(bShow)
{
	var errorDiv  = document.getElementById("FQU_Invalid_Format");
	if (bShow)
	{
		fqu_uncheckRadios();
		fqu_hideDivs();
		errorDiv.style.display = "inline";
	}
	else
	{
		errorDiv.style.display = "none";		
	}
}

/**
 * TODO
 */ 
function fqu_hideDivs()
{
	var freqList = "D;W;M;P".split(";");
	for (var i=0; i<freqList.length; i++) 
	{
		var currentDiv  = document.getElementById("Pattern_Details:" + freqList[i]);
		currentDiv.style.display = "none";
	}	
}

/**
 * TODO
 */ 
function fqu_uncheckRadios()
{
	var freqList = "D;W;M;P".split(";");
	for (var i=0; i<freqList.length; i++) {
		var currentRadio = document.getElementById("fqu:frequency:" + freqList[i]);
		currentRadio.checked = false;
	}
}

/**
 * TODO
 */ 
function fqu_ValidateMonthInput(fieldName, maxChars)
{
	var curVal = document.getElementById(fieldName).value;
	// Allow only Numeric 1-99
	var lastchar = curVal.substr((curVal.length -1),curVal.length);
	if (isNaN(lastchar))
	{
		// Not a numeric so get rid of it
		curVal = curVal.substr(0,(curVal.length -1));
		// put it back
		document.getElementById(fieldName).value = curVal;		
	}
	
	// Allow only two chars
	if (curVal.length > maxChars)
	{
		curVal = curVal.substring(0, maxChars);
		// put it back
		document.getElementById(fieldName).value = curVal;
	}
	
}

/**
 * Wrapper for frequency from a contract screen.
 */
function dropFrequency(thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var dropFieldId = thisField.getAttribute("frequencydropfieldname");
	freqDropDown_GLOBAL = dropFieldId;
	// special check for Firefox
	// If we are displaying enrichment only (as set in the version record) then set the styles
	// This lets us position the dropdown correctly and the subsequent dropdown
	if (isFirefox())
		{
		var enrichmentOnly = thisField.getAttribute("enrichmentOnly");
		if (enrichmentOnly != null)
		{
			var textFieldId = document.getElementById(dropFieldId);
			textFieldId.style.position="absolute";
			textFieldId.style.display="block";
		}
	}
	
	dropFrequencyDisplay(dropFieldId);
}

/**
 * Wrapper for frequency from an enquiry selection screeen.
 * @param {String} dropFieldId the target field for the dropdown result
 */
function enqselDropFrequency(dropFieldId)
{
	// set the form name global for enq selection context
	setCurrentForm( "enqsel" );
	var dropFieldObject = getCurrentTabField( currentForm_GLOBAL, dropFieldId ); 
	freqDropDown_GLOBAL = dropFieldObject.getAttribute("frequencydropfieldname"); // set the global variable to be used when hiding the freuqncy dropdown
	dropFrequencyDisplay(dropFieldId);
}

function calendarFrequencyHide(theEvent)
{
	// Proceed only if we have any drop down is being opened.
	var calendarFieldId = '';
	var calendardropfieldname = '';
	if ( currentDropDown_GLOBAL || freqDropDown_GLOBAL )
	{
		// Current object which invoked by us.
		var thisField = theEvent.target || theEvent.srcElement;
			
		// Extract frequency window save and close attribute and if it is processing is required.
		if ( thisField.getAttribute("title") == "Save" || thisField.getAttribute("title") == "Close" )
			return;
		// Extract the Calendar's list box id.
		if (isOpera())
		{
			calendarFieldId = thisField.parentNode.id;
			if (!calendarFieldId)
			{
				calendarFieldId = thisField.parentNode.firstElementChild.id;
				if (!calendarFieldId)
				{
					calendarFieldId = thisField.parentNode.parentNode.id;
				}
			}
		}
		else
		{
			 calendarFieldId = thisField.getAttribute("id");
		}
		// Extract frequency window inner calendar name.
		if (isOpera())
		{
			calendardropfieldname = thisField.parentNode.id;
			if (!calendardropfieldname)
			{
				calendardropfieldname = thisField.parentNode.firstElementChild.id;
				if (!calendardropfieldname)
				{
					calendardropfieldname = thisField.parentNode.parentNode.id;
				}
			}
		}
		else
		{
			calendardropfieldname = thisField.getAttribute("calendardropfieldname");
		}
		// Assign current opened window to hide.
		var divToHide = currentDropDown_GLOBAL ;
		// Flag decides to hide.
		var val = -1;
				
		// when only the normal calendar window is opened.
		if ( currentDropDown_GLOBAL && !freqDropDown_GLOBAL )
		{
			// calendar inner list boxes so don't close it.
			if ( calendarFieldId != null)
			{
				// check for list boxes 
				val = calendarFieldId.indexOf( currentDropDown_GLOBAL );
			}
		}	
		// when only frequency window is opened.
		else if ( freqDropDown_GLOBAL == currentDropDown_GLOBAL || freqDropDown_GLOBAL && !currentDropDown_GLOBAL )
		{
			if ( calendardropfieldname != null )
			{
				// event for frequency window inner calendar to be opened so don't close it.
				val = calendardropfieldname.indexOf("fqu:");
			}
			// event for relative calendar window inner fields clicks so don't close it.
			var Rel_innerfield = thisField.getAttribute("id");
			if (Rel_innerfield != null)
			{
				// check for list box of the relative calendar window 
				val = Rel_innerfield.indexOf("rel_");
			}
			var Rel_offsetfld = thisField.getAttribute("offsetfld");
			if (Rel_offsetfld != null && Rel_innerfield!="" )
			{
				val = Rel_offsetfld.indexOf("rel_");
			}
			// other events in frequency window.
			if ( val == -1 )
			{
				// if any selection or radio button is been invoked on mouse down event.
				// extract tagname to identify its from selection or radio or inner calendar input box.
				if ( validFreTagName == true )
				{
					val = 0;
				}
				else
				{
					// if event occured out side of frequency window just close it.
					divToHide = freqDropDown_GLOBAL;
				}
			}
		}
		// frequency window and inner calendar is being opened.
		if ( currentDropDown_GLOBAL && freqDropDown_GLOBAL && currentDropDown_GLOBAL != freqDropDown_GLOBAL )
		{
			// find out the tagName of current event object so that inner calendar can be closed if already opened.
			if ( validFreTagName == true )
			{
				val = 0;
			}
			
			else
			{	
				// if inner calendar window's list box clicked. 
				if ( calendarFieldId != null ) 
				{
					val = calendarFieldId.indexOf( "fqu:" );
					// if any other selection or radio button clicked when both frequency and inner calendar is being opened.
					if ( calendarFieldId != currentDropDown_GLOBAL ) 
					{
						divToHide = currentDropDown_GLOBAL;
					}
				}
			}
			// need to close both when click event is not made by any controls which belongs to these windows.
			if ( val == -1 && !calDateClicked_GLOBAL && !closeSingleWindow )
			{
				hidePopupDropDown(currentDropDown_GLOBAL);
				hidePopupDropDown(freqDropDown_GLOBAL);
				return;
			}
		}
		// close window.
		if ( val == -1 && !calDateClicked_GLOBAL )
		{
			hidePopupDropDown(divToHide);
		}
	}
}
function focusOnFreqSave(theEvent)
{
  if(theEvent.keyCode == '9')
  {
	var recurrence = document.getElementById('Frequency_Pattern');
	recurrence.focus();
  }
}