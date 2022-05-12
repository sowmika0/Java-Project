/** 
 * @fileoverview TODO
 *
 * @author XXX
 * @version 0.1
 */


var recurDropWidth_GLOBAL = 450;

/**
 * Displays a dropdown version of the recurrence.
 * @param {TODO} thisEvent
 */
function dropRecurrenceDisplay(dropFieldId)
{
	// Get the drop field
	var dropFieldObject = getFormField( currentForm_GLOBAL, dropFieldId );

    // Set fixed height/width dimensions
	var dropWidth = recurDropWidth_GLOBAL;
	
    // Set general form params
    var application = getFormFieldValue( currentForm_GLOBAL, "application" );
    var enqname = getFormFieldValue( currentForm_GLOBAL, "enqname" );
    var genForm = FragmentUtil.getForm("generalForm");
    var recField = dropFieldObject.getAttribute("recurrencefieldname");
    genForm.requestType.value = _UTILITY__ROUTINE_;
    genForm.routineName.value = _OS__GET__RECURRENCE_;
	genForm.routineArgs.value = dropFieldId + "|" + application + "|" + enqname + "|" + recField;

	// Display the popup with given size/postion, and run the request given by form
	runPopupDropDown(genForm, dropFieldObject, dropWidth, -1);
}


/**
 * Loads the correct display for the field.
 * @param {String dropFieldId} The ID of the input element for the recurrence control
 * @param {String fragmentName} The suffix of the fragment the 
 */
function initRecurrence(dropFieldId,fragmentName) {
	resetControl();

    // Get the value of the recurrence field
    var dropFieldForm = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
    var dropFieldFormId = dropFieldForm.getAttribute("id");
    var dropFieldObject = getFormField( dropFieldFormId, dropFieldId );
    var currentVal = dropFieldObject.value;
 	//For showing the completed popup window.
 	// Focus set and scroll the entire element into view
 	var recurrencePattern = document.getElementById('Recurrence_Pattern');
	if (recurrencePattern != undefined)
	{
		recurrencePattern.focus();
 	}
	var recurrenceFocus = window.document.getElementById(dropFieldId + ":div");
	if (recurrenceFocus != undefined)
	{
		recurrenceFocus.scrollIntoView(false);
	}
    validateCurrentDate(dropFieldId,currentVal);
}

/**
 * TODO
 * @param {TODO} _textField
 */
function resetControl(_textField)
{
	var errorDiv  = document.getElementById("Invalid_Format");
	errorDiv.style.display = "none";
	// Populate the control with default settings

	// Daily
	drawGivenDate(_textField,"e0Y e0M e0W e1D e0F", false);

	// Weekly
	drawGivenDate(_textField,"e0Y e0M e1W e0D e0F", false);

	// Monthly
	drawGivenDate(_textField,"e0Y e1M o1W o1D e0F", false);
	drawGivenDate(_textField,"e0Y e1M e0W o1D e0F", false);
	// Uncheck the 'On' checkbox
	document.getElementById("Recurrance:M:On").checked = false;
	showHideMonthDetail();	

	// Yearly
	drawGivenDate(_textField,"e1Y o1M o1W o1D e0F", false);		
	drawGivenDate(_textField,"e1Y o1M e0W o1D e0F", false);
	// Uncheck the 'On' checkbox
	document.getElementById("Recurrance:Y:On").checked = false;
	showHideYearDetail();
	

	// Predefined
	drawGivenDate(_textField,"e0Y e0M e0W e1D e0F", false);	
	document.getElementById("ebFrequencyId").value = "";

	// Advanced
	resetAdvanced();
}

/**
 * TODO
 */
function resetAdvanced()
{
	document.getElementById("every:D").checked = true;
	document.getElementById("advanced:Input:D").value = "";
	document.getElementById("every:W").checked = true;
	document.getElementById("advanced:Input:W").value = "";
	document.getElementById("every:M").checked = true;
	document.getElementById("advanced:Input:M").value = "";
	document.getElementById("every:Y").checked = true;
	document.getElementById("advanced:Input:Y").value = "";	
}

/**
 * Splits the string into a datePart and a recurPart
 * @param {HTMLElement} _textField
 * @param {string} currentVal
 */
function validateCurrentDate(_textField,currentVal)
{
	var datePart  = "";		// The date part of the field value
	var recurPart = "";		// The recurrence part of the field value. Can also be in the old 'frequency' style
	var drawPattern = "";	// For converting old style patterns to the new style. This holds the new pattern that must be displayed.

	// Determine the recurrence pattern and transform old style into new style
	var recurPattern   = /e.+Y [eo].+M [eo].+W [eo].+D [eo].+F/;
	var dailyPattern   = /D[AILY]{0,4}$/;
	var bsnssPattern   = /B[SNSS]{0,4}$/;
	var weeklyPattern  = /W[EK]{0,3}(\d)$/;
	var twmthPattern   = /T[WMTH]{0,4}$/;
	var monthlyPattern = /M(\d{2})(\d{2})$/;
	
	// Run through the checks
	if (recurPattern.test(currentVal))
	{
		recurPart = currentVal.match(recurPattern)[0];
		drawPattern = recurPart;
	}
	else if (dailyPattern.test(currentVal))
	{
		recurPart = currentVal.match(dailyPattern)[0];
		drawPattern = "e0Y e0M e0W e1D e0F";
	}
	else if (bsnssPattern.test(currentVal))
	{
		recurPart = currentVal.match(bsnssPattern)[0];
		drawPattern = "e0Y e0M e0W eBD e0F";
	}
	else if (weeklyPattern.test(currentVal))
	{
		recurPart = currentVal.match(weeklyPattern)[0];
		var weeklyAmount = currentVal.match(weeklyPattern)[1];
		drawPattern = "e0Y e0M e" + weeklyAmount + "W e0D e0F";
	}
	else if (twmthPattern.test(currentVal))
	{
		recurPart = currentVal.match(twmthPattern)[0];
		drawPattern = "e0Y e0M e0W e1,15D e0F";
	}
	else if (monthlyPattern.test(currentVal))
	{
		recurPart = currentVal.match(monthlyPattern)[0];
		var monthAmount = currentVal.match(monthlyPattern)[1] * 1;
		var dayAmount = currentVal.match(monthlyPattern)[2] * 1;
		drawPattern = "e0Y e" + monthAmount + "M e0W o" + dayAmount + "D e0F";
	}
	else if (document.getElementById("frequency:N") != null)
	{
		drawPattern = "e0Y e0M e0W e0D e0F";	// The 'none' option is available, draw that
	}
	else
	{
		drawPattern = "e0Y e0M e0W e1D e0F";	// If all else fails, draw 'daily'
	}

	// Check for a datePart
	var shortDatePattern = /\d{8}/;				// A 'YYYYMMDD' pattern
	var longDatePattern  = /\d{2} \w{3} \d{4}/;	// A 'DD MMM YYYY' pattern
	if (recurPart != "")
	{
		datePart = currentVal.replace(recurPart,"");	// Remove any recurrence part
		datePart = datePart.replace(/^\s+|\s+$/g, '');	// Trim any spaces
	}
	else if (shortDatePattern.test(currentVal))
	{
		datePart = currentVal.match(shortDatePattern)[0];
	}
	else if (longDatePattern.test(currentVal))
	{
		datePart = currentVal.match(longDatePattern)[0];
	}
	
	// Populate the control
	setDateField(datePart);
	drawGivenDate(_textField,drawPattern, true);
}


/**
 * Checks if a date field is present, and if so, sets it's value, else does nothing.
 * @param {string} datePart The value to be displayed in the dateField
 */
function setDateField(dateValue) {
	var dateField = document.getElementById("fqu:nextDate");
	if (dateField != null && dateField != undefined) {
		dateField.value = dateValue;
	}
}

/**
 * TODO
 */
function hideDivs()
{
	var freqList = "D;W;M;Y;P;A".split(";");
	for (var i=0; i<freqList.length; i++) 
	{
		var currentDiv  = document.getElementById("Recurrance:" + freqList[i]);
		currentDiv.style.display = "none";
	}	
}

/**
 * Minimises the content of the div element.
 */
function saveDate(fieldName)
{
	var recFieldObj = document.getElementById(fieldName);
	if (recFieldObj != null )
    {
    	try 
    	{
    		recFieldObj.focus(); 
    	} 
    	catch( e)
    	{ 
    		// Do nothing if we cant focus
    	}
	}
	parseDate(fieldName);
	hidePopupDropDown(fieldName);
}
/**
 * Save the content of the div element.
 */
function rec_saveDateonKey(fieldName,thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode!=0)
	{
		return;
	}
	var recSaveObj = document.getElementById(fieldName);
	if (recSaveObj != null )
    {
    	try 
    	{
    		recSaveObj.focus(); 
    	}
    	catch( e)
    	{
    		// Do nothing if we cant focus
    	}
	}
	parseDate(fieldName);
	hidePopupDropDown(fieldName);
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
function rec_closeonKey(fieldName,thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode!=0)
	{
		return;
	}
	hidePopupDropDown(fieldName);
	var tooltipDiv = window.document.getElementById("tooltipDiv");
	if ( tooltipDiv != null )
		{
			// Found the button, set it
			
			tooltipDiv.style.display = "none";
		
		}
}
/**
 * Get the value of a radio button group, or return the empty string
 * @param {TODO} radioGroupName
 */ 
function recurr_getRadioValue(radioGroupName) {
	var radioList = document.getElementsByName(radioGroupName);
	for (var i=0; i<radioList.length; i++) {
		if (radioList[i].checked) {return radioList[i].value;}
	}
	return "";
}

/**
 * Sets the appropriate radio button in a group according to the given value
 * @param {TODO} radioGroupName
 * @param {TODO} checkedValue
 */
function setRadioValue(radioGroupName,checkedValue) {
	var radioList = document.getElementsByName(radioGroupName);
	for (var i=0; i<radioList.length; i++) {
		if (radioList[i].value == checkedValue) {
			radioList[i].checked = true;
			break;
		}
	}
}

/**
 * Hide all the controls, except the checked one
 * @param {TODO} fieldName
 */ 
function drawSchedulerControl(fieldName) {
	
	var errorDiv  = document.getElementById("Invalid_Format");
	errorDiv.style.display = "none";
		
	var freqList = "D;W;M;Y;P;A".split(";");
	for (var i=0; i<freqList.length; i++) {
		var currentRadio = document.getElementById("frequency:" + freqList[i]);
		var currentDiv  = document.getElementById("Recurrance:" + freqList[i]);
		if (currentRadio.checked) {
			currentDiv.style.display = "inline";
		} else {
			currentDiv.style.display = "none";
		}
	}
}

/**
 * Converts the users selected options into a string for the hidden field value
 * @param {TODO} fieldName
 */
function parseDate(fieldName) {
	// Determine which checkbox is actually checked
	var recurType = recurr_getRadioValue("frequency");
	// parse the date...
	var parsedDate = "";
	switch (recurType) {
		case "N": parsedDate = ""; break;
		case "D": parsedDate = getDailyRecurString(fieldName); break;
		case "W": parsedDate = getWeeklyRecurString(fieldName); break;
		case "M": parsedDate = getMonthlyRecurString(fieldName); break;
		case "Y": parsedDate = getYearlyRecurString(fieldName); break;
		case "P": parsedDate = getEbFrquencyRecurString(fieldName); break;
		case "A": parsedDate = getAdvancedRecurString(fieldName); break;
		default: parsedDate = "e0y e0m e0w e0d e0f";
	}
	// Now get the date is it has been given, if there is any
	var nextDate = document.getElementById("fqu:nextDate");
	if (nextDate != null && nextDate != undefined && nextDate.value != "")
	{
		nextDate = nextDate.value;
		nextDate = nextDate.replace(/^\s+|\s+$/g, '');	// Trim any spaces!
		if (parsedDate == "") {
			parsedDate = nextDate;
		} else {
			parsedDate = nextDate + " " + parsedDate;
		}
	}
	// Set the hidden field value
	setFormFieldValue( currentForm_GLOBAL, fieldName, parsedDate);
	
	// Run any field events if the value has changed	
	var dropFieldObj = document.getElementById(fieldName);
	
	// set the enrichment, but only if the field has been hidden
	if ( dropFieldObj.getAttribute( "type") == "hidden" )
		{
		setEnrichment(fieldName, parsedDate);
		}
		
	var oldValue = dropFieldObj.oldValue;
	
	if ( parsedDate != oldValue )
	{
		doFieldChangeEvent( dropFieldObj );
	}
}

/**
 * TODO
 * @param {TODO} fieldName
 */
function getEbFrquencyRecurString(fieldName){
	var frequencyId = document.getElementById("ebFrequencyId").value ;
	
	frequencyId = frequencyId.toString();
	if(frequencyId.indexOf("-") > -1){
		frequencyId = frequencyId.substring(0,frequencyId.indexOf("-"));
	}
			
	var out = "e0Y e0M e0W e0D " + "e" + frequencyId +"F";
	return out.toString();
}

/**
 * TODO
 * @param {TODO} fieldName
 * @return out.toString
 * @type TODO
 */
function getDailyRecurString(fieldName) {
	
	var day = 0;
	if (document.getElementById("rec:BusinessDay").checked == true)
	{
		day = "B";
	}		
	else
	{
		day = document.getElementById("D:days").value;
	}
	var out = "e0Y e0M e0W e" + day + "D" + " e0F";
	return out.toString();
}

/**
 * TODO
 * @param {TODO} fieldName
 */
function getWeeklyRecurString(fieldName) {
	var out = "e0Y e0M e" + document.getElementById("W:weeks").value + "W o";
	var dayList = document.getElementsByName("W:dayOfWeek");
	for (var i=0; i<dayList.length; i++) {
		if (dayList[i].checked) {
			out += dayList[i].value + ",";
		}
	}
	out = out.slice(0,-1); // Strips off any trailing commas, or the 'o' if no days are checked
	if (out.lastIndexOf(" ") == out.length-1) {out += "e0";} // Append "0" if no days are selected
	out += "D";
	out += " e0F";
	return out.toString();
}

/**
 * TODO
 * @param {TODO} fieldName
 * @return out.toString
 * @type TODO
 */
function getMonthlyRecurString(fieldName) {
	
	var out = "e0Y e";
	out += document.getElementById("M:dayOfMonth:month").value;
	out += "M";
	// Only If 'On' is checked
	if (document.getElementById("Recurrance:M:On").checked)	
	{
		var monthlyRecurType = recurr_getRadioValue("M:RecurType");
		if (monthlyRecurType == "dayOfMonth") {
			out += " e0W o" + document.getElementById("M:dayOfMonth:day").value + "D";
		} else {
			out += " o" + document.getElementById("M:dayOfWeekOfMonth:week").value + "W";
			out += " o" + document.getElementById("M:dayOfWeekOfMonth:day").value + "D";
		}
	}
	else
	{
		out += " e0W e0D";
	}

	out += " e0F";
	return out.toString();
}

/**
 * TODO
 * @param {TODO} fieldName
 * @return out.toString
 * @type TODO
 */
function getYearlyRecurString(fieldName) {
	
	var out = "e";
	out += document.getElementById("Y:year:year").value;
	out += "Y";
	
	if (document.getElementById("Recurrance:Y:On").checked)
	{
		// Process Yearly Details
		var yearlyRecurType = recurr_getRadioValue("Y:RecurType");
		if (yearlyRecurType == "dayOfMonth") {
			out += " o" + document.getElementById("Y:dayOfMonth:month").value + "M";
			out += " e0w o" + document.getElementById("Y:dayOfMonth:day").value + "D";
		} else {
			out += " o" + document.getElementById("Y:dayOfWeekOfMonth:month").value + "M";
			out += " o" + document.getElementById("Y:dayOfWeekOfMonth:week").value + "W";
			out += " o" + document.getElementById("Y:dayOfWeekOfMonth:day").value + "D";
		}		
	}
	else
	{
		// Just a Year has been specified so all other details are 0
		out += " e0M e0W e0D";	
	}
	out += " e0F";
	return out.toString();
}

/**
 * TODO
 * @param {date} fieldName
 * @return out.toString
 * @type TODO
 */
function getAdvancedRecurString(fieldName)
{
	var out = "";
	
	// Days
	var dailyRecurType = recurr_getRadioValue("advanced:D");
	var dailyRecurDetails = document.getElementById("advanced:Input:D").value;
	if (dailyRecurDetails == "")
	{
		dailyRecurDetails = "0";
	}
	var dailyRecur = dailyRecurType + dailyRecurDetails + "D";
	
	// Weeks
	var weeklyRecurType = recurr_getRadioValue("advanced:W");
	var weeklyRecurDetails = document.getElementById("advanced:Input:W").value;
	if (weeklyRecurDetails == "")
	{
		weeklyRecurDetails = "0";
	}
	var weeklyRecur = weeklyRecurType + weeklyRecurDetails + "W";	
	
	// Months
	var monthlyRecurType = recurr_getRadioValue("advanced:M");
	var monthlyRecurDetails = document.getElementById("advanced:Input:M").value;
	if (monthlyRecurDetails == "")
	{
		monthlyRecurDetails = "0";
	}
	var monthlyRecur = monthlyRecurType + monthlyRecurDetails + "M";	

	// Years
	var yearlyRecurType = recurr_getRadioValue("advanced:Y");
	var yearlyRecurDetails = document.getElementById("advanced:Input:Y").value;
	if (yearlyRecurDetails == "")
	{
		yearlyRecurDetails = "0";
	}
	var yearlyRecur = yearlyRecurType + yearlyRecurDetails + "Y";	

	out = yearlyRecur + " " + monthlyRecur + " " + weeklyRecur + " " + dailyRecur + " e0F";;

	return out.toString();
}

/**
 * Draws the control and populates it with a specific value
 * @param {TODO} fieldName
 * @param {TODO} inDate
 * @param {TODO} bDrawAdvanced
 */
function drawGivenDate(fieldName,inDate, bDrawAdvanced) {
	
	var inDateList = inDate.split(" ");
	var typeList = new Array(5);
	var valList = new Array(5);
	for (var i=0; i<5; i++) 
	{
		typeList[i] = inDateList[i].slice(0,1);
		valList[i] = inDateList[i].slice(1,-1);
	}

	if (bDrawAdvanced)
	{
		// Draw the advanced section when required
		drawAdvancedDate(inDate);
	}
	
	// Now draw the page...
	// First check the year value, if it's not zero, display the yearly option
	if (valList[0] > 0) {
		drawYearlyDate(fieldName,inDate);
	} else if (valList[1] > 0) {
		drawMonthlyDate(fieldName,inDate);
	} else if (valList[2] > 0) {
		drawWeeklyDate(fieldName,inDate);
	} else if (valList[3] > 0) {
		drawDailyDate(fieldName,inDate, false);
	} else if ((valList[3] == "B") || (valList[3] == "b")) {
		// It's Every Business day
		drawDailyDate(fieldName,inDate, true);
	} else if (valList[4] != "0") {
		var theId = valList[4];
		drawEbFrequency(fieldName, theId);
	} else if (valList[0] == valList [1] == valList [2] == valList [3] == valList[4] == 0 && document.getElementById("frequency:N") != null) {
		// Draw the 'None' pattern if available and all values are zero
		setRadioValue("frequency","N");
		hideDivs();
	} else {
		// Draw the advanced section
		setRadioValue("frequency","A");
	}

	// Finally, refresh the control
	var errorDiv  = document.getElementById("Invalid_Format");
	if (errorDiv.style.display == "none")
	{
		drawSchedulerControl(fieldName);
	}
}

/**
 * TODO
 * @param {TODO} inDate
 * @param {TODO} theID
 */
function drawEbFrequency(inDate, theID)
{
	// Find the ID and set it
	document.getElementById("ebFrequencyId").value = theID;
	setRadioValue("frequency","P");	
}

/**
 * TODO
 */
function showErrorMessage()
{
	var errorDiv  = document.getElementById("Invalid_Format");
	errorDiv.style.display = "inline";
}

/**
 * TODO
 */
function uncheckRadios()
{
	var freqList = "D;W;M;Y;P;A".split(";");
	for (var i=0; i<freqList.length; i++) {
		var currentRadio = document.getElementById("frequency:" + freqList[i]);
		currentRadio.checked = false;
	}
}

/**
 * TODO
 * @param {TODO}inDate
 */
function drawAdvancedDate(inDate)
{
	var inDateList = inDate.split(" ");
	for (var i=0; i<4; i++) 
	{
		var typeList = inDateList[i].slice(0,1);
		var valList = inDateList[i].slice(1,-1);
		var patternList = inDateList[i].slice(-1);
		
		switch (patternList){
			case "D" :
				// Check the correct radio (the 'every:x' should be already checked so
				// just test the 'on:x'
				if (typeList == "o")
				{
					document.getElementById("on:D").checked = true;				
				}
				// Add contents
				document.getElementById("advanced:Input:D").value = valList;
				
		      	break;
		   case "W" :
				// Check the correct radio (the 'every:x' should be already checked so
				// just test the 'on:x'		   
				if (typeList == "o")
				{
					document.getElementById("on:W").checked = true;				
				}
				// Add contents
				document.getElementById("advanced:Input:W").value = valList;
									
		      	break;
		   case "M" :
				// Check the correct radio (the 'every:x' should be already checked so
				// just test the 'on:x'		   
				if (typeList == "o")
				{
					document.getElementById("on:M").checked = true;				
				}
				// Add contents
				document.getElementById("advanced:Input:M").value = valList;
							
		      	break;
		   case "Y" :
				// Check the correct radio (the 'every:x' should be already checked so
				// just test the 'on:x'		   
				if (typeList == "o")
				{
					document.getElementById("on:Y").checked = true;				
				}
				// Add contents
				document.getElementById("advanced:Input:Y").value = valList;
								
		      	break;
		} 
	}
	
}

/**
 * TODO
 * @param {TODO} fieldName
 * @param {TODO} inDate
 */
function drawYearlyDate(fieldName,inDate) {
	setRadioValue("frequency","Y");
	
	var yearVal = getYearlyValue(inDate);
	var monthVal = getMonthlyValue(inDate);
	var weekVal = getWeeklyValue(inDate);
	var dayVal = getDailyValue(inDate);
	
	// Set the Year value
	document.getElementById("Y:year:year").value = yearVal;

	// If ANY of the elements are NOT 0 then set the details
	if ((weekVal != 0) || (dayVal != 0) || (monthVal != 0))
	{
		// Check the 'On'
		document.getElementById("Recurrance:Y:On").checked = true;
		showHideYearDetail();		
		if (getWeeklyValue(inDate) == 0) {
			setRadioValue("Y:RecurType","dayOfMonth");
			document.getElementById("Y:dayOfMonth:day").value = getDailyValue(inDate);
			document.getElementById("Y:dayOfMonth:month").value = getMonthlyValue(inDate);
		} else {
			setRadioValue("Y:RecurType","dayOfWeekOfMonth");
			document.getElementById("Y:dayOfWeekOfMonth:day").value = getDailyValue(inDate);
			document.getElementById("Y:dayOfWeekOfMonth:week").value = getWeeklyValue(inDate);
			document.getElementById("Y:dayOfWeekOfMonth:month").value = getMonthlyValue(inDate);
		}
	}
	
	// Some Defensive Code	
	var day = document.getElementById("Y:dayOfWeekOfMonth:day").value;
	if (day == "") {
		drawGivenDate(fieldName,"e1Y o1M o1W o1D e0F", false);
		setRadioValue("frequency","A");
	}
	
	var week = document.getElementById("Y:dayOfWeekOfMonth:week").value;
	if (week == "") {
		drawGivenDate(fieldName,"e1Y o1M o1W o1D e0F", false);
		setRadioValue("frequency","A");
	}
	
	var month = document.getElementById("Y:dayOfWeekOfMonth:month").value;
	if (month == "") {
		drawGivenDate(fieldName,"e1Y o1M o1W o1D e0F", false);
		setRadioValue("frequency","A");
	}			
}

/**
 * TODO
 * @param {TODO} fieldName
 * @param {TODO} inDate
 */
function drawMonthlyDate(fieldName,inDate) {

	setRadioValue("frequency","M");
	
	var monthStartVal = getMonthlyStartValue(inDate);
	
	if (monthStartVal == 'o')
	{
		uncheckRadios();
		hideDivs();
		showErrorMessage();
	}
	else
	{
		var monthVal = getMonthlyValue(inDate);
		var weekVal = getWeeklyValue(inDate);
		var dayVal = getDailyValue(inDate);
		
		// Set the month value
		document.getElementById("M:dayOfMonth:month").value = monthVal;
		
		// If ANY of the elements are NOT 0 then set the details
		if ((weekVal != 0) || (dayVal != 0))
		{
			// Check the 'On'
			document.getElementById("Recurrance:M:On").checked = true;
			showHideMonthDetail();
			if (getWeeklyValue(inDate) == 0) {
				setRadioValue("M:RecurType","dayOfMonth");
				document.getElementById("M:dayOfMonth:day").value = getDailyValue(inDate);
				document.getElementById("M:dayOfMonth:month").value = getMonthlyValue(inDate);
			} else {
				setRadioValue("M:RecurType","dayOfWeekOfMonth");
				document.getElementById("M:dayOfWeekOfMonth:day").value = getDailyValue(inDate);
				document.getElementById("M:dayOfWeekOfMonth:week").value = getWeeklyValue(inDate);
			}
		}
	
			
		// Some Defensive Code	
		var day = document.getElementById("M:dayOfWeekOfMonth:day").value;
		if (day == "") {
			drawGivenDate(fieldName,"e0Y e1M o1W o1D e0F", false);
			setRadioValue("frequency","A");
		}
		
		var week = document.getElementById("M:dayOfWeekOfMonth:week").value;
		if (week == "") {
			drawGivenDate(fieldName,"e0Y e1M o1W o1D e0F", false);		
			setRadioValue("frequency","A");
		}
		
		var month = document.getElementById("M:dayOfMonth:month").value;
		if (month == "") {
			drawGivenDate(fieldName,"e0Y e1M o1W o1D e0F", false);		
			setRadioValue("frequency","A");
		}
	}		
}

/**
 * TODO
 * @param {TODO} fieldName
 * @param {TODO} inDate
 */
function drawWeeklyDate(fieldName,inDate) {
	setRadioValue("frequency","W");
	
	var weekStartVal = getWeeklyStartValue(inDate);
	
	if (weekStartVal == 'o')
	{
		uncheckRadios();
		hideDivs();
		showErrorMessage();
	}
	else
	{	
		document.getElementById("W:weeks").value = getWeeklyValue(inDate);
		var selectedDays = getDailyValue(inDate);
		var dayList = document.getElementsByName("W:dayOfWeek");
		for (var i=0; i<dayList.length; i++) {
			if (selectedDays.indexOf(dayList[i].value) > -1) {
				dayList[i].checked = true;
			} else {
				dayList[i].checked = false;
			}
		}
	}
}

/**
 * TODO
 * @param {TODO} fieldName
 * @param {TODO} inDate
 * @param {TODO} bBusinessDay
 */
function drawDailyDate(fieldName,inDate,bBusinessDay) {
	
	var dayStartVal = getDailyStartValue(inDate);
	
	if (dayStartVal == 'o')
	{
		uncheckRadios();
		hideDivs();
		showErrorMessage();
	}
	else
	{		
		setRadioValue("frequency","D");
		if (bBusinessDay){
			setRadioValue("rec:D:radio", "DB");
		} else {
			setRadioValue("rec:D:radio", "D");				
			document.getElementById("D:days").value = getDailyValue(inDate);
		}
	}
}

/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString()
 * @type TODO
 */
function getDailyValue(inDate) 
{
	var match = "";
	try
	{
		match =  inDate.match(/ [eo]([\d,L]+)[dD] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString
 * @type TODO
 */
function getDailyStartValue(inDate) 
{
	var match = "";
	try
	{
		match =  inDate.match(/ ([eo])([\d,L]+)[dD] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
     * TODO
     * @param {TODO} inDate
     * @return match.toString
     * @type TODO
 */
function getWeeklyValue(inDate)  
{
	var match = "";
	try
	{
		match =  inDate.match(/ [eo](\d+|L)[wW] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString
 * @type TODO
 */
function getWeeklyStartValue(inDate)  
{
	var match = "";
	try
	{
		match =  inDate.match(/ ([eo])(\d+|L)[wW] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString
 * @type TODO
 */
function getMonthlyValue(inDate) 
{
	var match = "";
	try
	{
		match =  inDate.match(/ [eo](\d+)[mM] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString
 * @type TODO
 */
function getMonthlyStartValue(inDate) 
{
	var match = "";
	try
	{
		match =  inDate.match(/ ([eo])(\d+)[mM] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} inDate
 * @return match.toString
 * @type TODO
 */
function getYearlyValue(inDate)  
{
	var match = "";
	try
	{
		match =  inDate.match(/e(\d+)[yY] /)[1];
	}
	catch(exception_var)
	{
		// Show Advanced Tab
		setRadioValue("frequency","A");
	}
	return match.toString();
}
/**
 * TODO
 * @param {TODO} showHideMonthDetail
 * @return match.toString
 * @type TODO
 */
function showHideMonthDetail()
{
	// Show or hide the month detail
	if (document.getElementById("Recurrance:M:On").checked == true)
	{
		// Show it
		var M_details = document.getElementById("monthly_details");
		M_details.style.display = "inline";
	}
	else
	{
		// Hide it
		var M_details = document.getElementById("monthly_details");
		M_details.style.display = "none";
	}
}
/**
 * TODO
 * @return match.toString
 * @type TODO
 */
function showHideYearDetail()
{
	// Show or hide the month detail
	if (document.getElementById("Recurrance:Y:On").checked == true)
	{
		// Show it
		var Y_details = document.getElementById("yearly_details");
		Y_details.style.display = "inline";
	}
	else
	{
		// Hide it
		var Y_details = document.getElementById("yearly_details");
		Y_details.style.display = "none";
	}	
}

/**
 * Wrapper for recurrence from a contract screen.
 */
function dropRecurrence(thisEvent)
{
 	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode!=0)
	{
		return;
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var dropFieldId = thisField.getAttribute("recurrencedropfieldname");
	// Update the drop field Id.
	freqDropDown_GLOBAL = dropFieldId;
	// special check for Firefox
	// If we are displaying enrichment only (as set in the version record) then set the styles
	// This lets us position the dropdown correctly and the subsequent dropdown
	if (isFirefox())
		{
		var enrichmentOnly = thisField.getAttribute("enrichmentOnly");
		if (enrichmentOnly != null)
		{
			//display enrichment only
			var textFieldId = document.getElementById(dropFieldId);
			textFieldId.style.position="absolute";
			textFieldId.style.display="block";
		}
	}
	
	dropRecurrenceDisplay(dropFieldId);
}

/**
 * Wrapper for recurrence from an enquiry selection screeen.
 * @param {String} dropFieldId the target field for the dropdown result
 */
function enqselDropRecurrence(dropFieldId)
{
	// set the form name global for enq selection context
	setCurrentForm( "enqsel" );
    freqDropDown_GLOBAL = dropFieldId;
	dropRecurrenceDisplay(dropFieldId);
}
function setFocusOnRecurrSave(theEvent)
{
  if(theEvent.keyCode == '9')
  {
	var recurrence = document.getElementById('Recurrence_Pattern');
	recurrence.focus();
  }
}
function setFocusOnRecurrSaveOn(theEvent,eventSource)
{
 if(!eventSource.checked)
 {
    setFocusOnRecurrSave(theEvent);
 }
  
}