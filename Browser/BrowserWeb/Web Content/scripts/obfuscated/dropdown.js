/** 
 * @fileoverview TODO
 *
 * @author XXX
 * @version 0.1 
 */

var currentDropDown_GLOBAL = "";
var savepopupDropDown_GLOBAL = "";

var dropWidth_GLOBAL = 400;
var dropHeight_GLOBAL = 200;

var calDropWidth_GLOBAL = 220;
var calDropHeight_GLOBAL = 250;
var calDateClicked_GLOBAL = false;		// Whether a date has been picked on a calendar
// Variable that holds the current tab user is on ( version tabs)
var currentVersionTab_GLOBAL = ""; 
var pickedDate = "";	//To maintain the picked date untill its get populated to the textbox.
var closeSingleWindow = false;  //variable maintained for closing the inner calendar window.
var validFreTagName = false;  //variable maintained to retain the status of the click event in an inner calendar window.

/**
 * Displays a dropdown page
 * @param {String} TODO
 * @optional { selectionDisplay }
 */
function dropdown(_application, _textField, _fieldName, _popupDropField,selectionDisplay)
{
	//If a hotfield then disable it for now.  This will stop the hotfield from running
	//automatically if on change.
	setHotField('false', _fieldName);
	//n.b. the hotfield will be enabled again on receiving focus
	
	// auto launch field
	setAutoField('false', _fieldName);
	
	// Set-up the args including search criteria - add "..." to any search criteria in the field
	var searchCriteria = '';
	// selection criteria not required for selectionDisplay type field
    if(selectionDisplay != "Yes")
	{
		searchCriteria = getCurrentTabFieldValue( currentForm_GLOBAL, _textField );
		searchCriteria = stripSpacesFromEnds(searchCriteria);
	
		if ( ( searchCriteria == undefined ) || ( searchCriteria == null ) )
		{
			searchCriteria = "";
		}
	
		if ( searchCriteria != "" )
		{
			// Add wildcard suffix if no dots already at the end of the string
			if ( searchCriteria.charAt( searchCriteria.length - 1 ) != "." )
			{
				searchCriteria = searchCriteria + "...";
			}
		}
    }
	var transId = getFormFieldValue( currentForm_GLOBAL, "transactionId" );
	var routineArgs = "RELATED_" + searchCriteria + "_" + transId;
	
	setWindowVariableMarker("true");
	storeWindowVariable( window.name );

	if ( ( _popupDropField == undefined ) || ( _popupDropField == null ) )
	{
		_popupDropField = "";
	}
	
	if (_popupDropField != "")
	{
		// save global variable to be restored later
		savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
		//To restore hot field attribute for IE 
		if(isInternetExplorer())
		{
		setHotField('true', _fieldName);
		}
		// change to null so that new window pops up instead of dropdown
		popupDropDown_GLOBAL = "";
	}
	// selection Display request
	if(selectionDisplay)
	{
	    savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
	    popupDropDown_GLOBAL = "";
	    routineArgs = routineArgs + '_selectionDisplay';
	    
	    doEnquiryRequest("0b}K0}56", _application, _textField, "", "", routineArgs, "NEW", "", "01XKyta");
		popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
		
	}
	else
	{
		if(popupDropDown_GLOBAL)
		{
			// Display the dropdown page as a fragment
			doEnquiryRequest("0b}K0}56", _application, _textField, "", "", routineArgs, "", "", "01XKyta");
		}
		else
		{
			// Do a form submit like the old way and display in the new window.
			doEnquiryRequest("0b}K0}56", _application, _textField, "", "", routineArgs, "NEW", "", "01XKyta");
			if (savepopupDropDown_GLOBAL)
			{
				//restore the state otherwise all future dropdowns will popup
				popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
			}
		}
	}
	setWindowVariableMarker("false");
}

/**
 * Displays an enquiry dropdown page
 * @param {TODO} TODO
 */ 
function enquiryDropdown(thisEvent)
{
	if(thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	var dropDownImage = thisEvent.target || thisEvent.srcElement;
	
	// Set the currentVersionTab_GLOBAL to the tab of the target that just got clicked
	currentVersionTab_GLOBAL = dropDownImage.getAttribute( "tabName");
	loadingIconType = "dropdown";
	var _enquiry = dropDownImage.getAttribute( "enquiryField");
	var _dropField = dropDownImage.getAttribute( "dropField");
	var _criteriaField = dropDownImage.getAttribute( "criteriaField");
	var _criteriaOp = dropDownImage.getAttribute( "criteriaOpField");
	var _criteriaDataField = dropDownImage.getAttribute( "criteriaDataField");
	var _criteriaDataFieldValue = dropDownImage.getAttribute("criteriaDataFieldValue");
	var _criteriaDataString = dropDownImage.getAttribute( "criteriaDataString");
	var _fieldName = dropDownImage.getAttribute( "enqFieldName");
	var _noinputDropField = dropDownImage.getAttribute("noinputDropField");
	var selectionDisplay = dropDownImage.getAttribute("selectionDisplay");
	
	//do we use popup windows? This will be set to "Y" if true
	var popupDropField = dropDownImage.getAttribute("popupDropField");
	
	// special check for Firefox
    // If we are displaying enrichment only (as set in the version record) then set the styles
    // This lets us position the dropdown correctly and the subsequent dropdown
    if (isFirefox())
    {
       var enrichmentOnly = dropDownImage.getAttribute("enrichmentOnly");
       if (enrichmentOnly != null)
        {
        //display enrichment only
        var textFieldId = document.getElementById(_dropField);
        textFieldId.style.position="absolute";
        textFieldId.style.display="block";
        }
    }

	//disable the hotfield for now.  This will stop hotfields from running
	//automatically if changed.
	setHotField("false", _fieldName);
	//n.b. the hotfield will be enabled again on receiving focus
	
	// for auto launch field
	setAutoField("false", _fieldName);
	
	// Set-up any arguments for the enquiry dropdown page
	// For enquiry dropdowns need to extract the field value of the enquiry field,
	// otherwise use the text field value as the selection criteria
	var skin = getSkin();

	// Check for special % character in enquiry name  - need to escape it
	var enqName = _enquiry;
	var enqPrefix = enqName.substring(0,3);
	
	if ( (enqPrefix.substring(0,1) == "%") && (enqPrefix.substring(0,3)!="%25") )
	{
		enqName = '%25' + enqName.substring(1, enqName.length);
	}
	
	// Dropdown based on an Enquiry - get field value of field specified in criteria
	// Criteria specified in the form "<enqFieldName> <operator> <dealFieldName>"
	// Criteria is optional
	var searchCriteria = "";
	if ( _criteriaField != "" )
	{
		var criteriaData = "";

		// Check if criteria needs a field value or is a string literal
		if ( _criteriaDataString != "" )
		{
			// Use the string literal
			criteriaData = _criteriaDataString;
                                               if (_criteriaOp=="RG")
              			{
		                             criteriaData = "<value>" + criteriaData + "</value>";
		                 }
		}
		else
		{
			//if the criteria field is NODISPLAY, then we need to check with _criteriaDataFieldValue
			if (_criteriaDataFieldValue != "")
			{	
				//get the nodisplay field's value
                 criteriaData = _criteriaDataFieldValue;
            }
			else
			{	
				// Get the field value 
				criteriaData = getFieldValue( _criteriaDataField );
			}

			// Check we got a value, if this is a multi-value field we may need to
			// take the value from the first multi-value/sub-value, so try these
			if ( criteriaData == "" )
			{
				// Check for first multi-value of this field
				criteriaData = getFieldValue( _criteriaDataField + ":1" );

				if ( criteriaData == "" )
				{
					// Check for the first sub-value of this field
					criteriaData = getFieldValue( _criteriaDataField + ":1.1" );
				}
			}
		}
		
		    // Display records if criteria passed otherwise display no records
			searchCriteria = _criteriaField + " " + _criteriaOp + " " + criteriaData;
		
	}
	else
	{
		// No criteria data has been set, however check if they have added a filter for this field
		// Set-up the args including search criteria - add "..." to any search criteria in the field
		var criteriaData = getFormFieldValue( currentForm_GLOBAL, _dropField );
		criteriaData = stripSpacesFromEnds(criteriaData);
		
		if ( ( criteriaData == undefined ) || ( criteriaData == null ) )
		{
			criteriaData = "";
		}
		
		if ( criteriaData != "" )
		{
			// Add wildcard suffix if no dots already at the end of the string
			if ( criteriaData.charAt( criteriaData.length - 1 ) != "." )
			{
				criteriaData = criteriaData + "...";
			}
			
			searchCriteria = "@ID LK " + criteriaData;
		}
		
		if(!(_noinputDropField==null || _noinputDropField==""))
		{
	      searchCriteria = "";
		}
	}

	// Set-up the args including search criteria
  	var routineArgs = "ENQUIRY_" + searchCriteria;
  	  	
  	setWindowVariableMarker("true");
	storeWindowVariable( window.name );
	
	// we want to popup new windows
	if (popupDropField != "")
 	{
 		// save global variable to be restored later
 		savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
 		//To restore hot field attribute for IE 
		if(isInternetExplorer())
		{
		setHotField('true', _fieldName);
		}
 		
		// change to null so that new window pops up instead of dropdown 		
		popupDropDown_GLOBAL = "";
	}
	// selection Display box request
	if(selectionDisplay)
	{
	    savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
	    popupDropDown_GLOBAL = "";
	    routineArgs = routineArgs + '__selectionDisplay';
	    doEnquiryRequest("0b}K0}56", enqName, _dropField, "", "", routineArgs, "NEW", "", "01XKyta");
		popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
	}
	else
	{
	
	// Are page dropdowns are allowed?
	if( popupDropDown_GLOBAL )
	{
		// Display the dropdown page as a fragment
		doEnquiryRequest("0b}K0}56", enqName, _dropField, "", "", routineArgs, "", "", "01XKyta");
	}
	else
	{
		// Display normal new window dropdown page
		doEnquiryRequest("0b}K0}56", enqName, _dropField, "", "", routineArgs, "NEW", "", "01XKyta");
		if (savepopupDropDown_GLOBAL)
 		{
 			//restore the state otherwise all future dropdowns will popup
			popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
		}
	}
}	
  	
  	

	setWindowVariableMarker("false");
}

/**
 * Deals with dropdowns that have build routines on them and treats them like context enquiries.
 * @param {TODO} TODO
 */
function enquiryBuildDropdown(thisEvent)
{


	if(thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	
	var thisField = thisEvent.target || thisEvent.srcElement;
	loadingIconType = "dropdown";
	var selectionDisplay = thisField.getAttribute("selectionDisplay");
	var enqName = thisField.getAttribute("enquiryField");
	var _dropField = thisField.getAttribute("dropField");
	
	var _popupDropField = thisField.getAttribute("popupdropfield");               
     if ( ( _popupDropField == undefined ) || ( _popupDropField == null ) )        
     {                                                                             
            _popupDropField = "";                                                     
     }                                                                                                    
     if (_popupDropField != "")                                                    
     {                                                                             
             // save global variable to be restored later                              
             savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;                          
             // change to null so that new window pops up instead of dropdown          
             popupDropDown_GLOBAL = "";                                                
     }                                                                             
 
	
	var enq = setContextEnquiryFields( enqName );
    updatedFormsField( "WS_dropfield", _dropField );
	
	if( currentForm_GLOBAL == "" )
	{
		currentForm_GLOBAL = "generalForm";
	}
	
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

    // new element added in the form to send the parent window reference to server while dropdown with build rtn is clicked.
    var newElement = document.createElement("input");
	newElement.type = "hidden";
	newElement.name = "EnqParentWindow";
	// selection Display box request
	if(selectionDisplay)
	{
		savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
		popupDropDown_GLOBAL = "";
		var saveRoutineArgs = appreqForm.routineArgs.value;
		var saveTarget = appreqForm.target;
		var saveDropField = appreqForm.dropfield.value;
		routineArgs = "BUILDDROP"+ "_" +enqName + "_ENQUIRY_"+"__selectionDisplay";		
		var mycmd = "0b}K0}56";
 		enquiryWindowName = createResultWindow( mycmd, 600, 300 );
 		updatedFormsField( "WS_parentWindow",window.name );
		appreqForm.routineArgs.value = routineArgs;
		appreqForm.dropfield.value = _dropField;
		appreqForm.target = enquiryWindowName;
 		newElement.value = enquiryWindowName;
 		appreqForm.appendChild(newElement);
 		FragmentUtil.submitForm(appreqForm);
 		if (savepopupDropDown_GLOBAL)                                     
		{                                                                 
			//restore the state otherwise all future dropdowns will popup 
			popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;              
		}   
		appreqForm.routineArgs.value = saveRoutineArgs;
		appreqForm.dropfield.value = saveDropField;
		appreqForm.target = saveTarget;
	}
	else
	{
		if(popupDropDown_GLOBAL)
		{
			newElement.value = window.name;			
			appreqForm.appendChild(newElement);

			var windowOrFragmentName = FragmentUtil.getWindowOrFragmentName();
	    	targetWin = windowOrFragmentName;                                 
	    	appreqForm.target = windowOrFragmentName;                         

			// Store the name of the target as this window could be the parent for a dropdown
			storeWindowVariable( targetWin );                                                
			var savedCompScreen = appreqForm.compScreen.value; 
		
	    	// Get the drop field                                               
			var dropFieldObject = getFormField( currentForm_GLOBAL, _dropField);

	   		 // Set fixed height/width dimensions - constants in dropdown.js
			var dropWidth = dropWidth_GLOBAL;                          
	    	var dropHeight = dropHeight_GLOBAL;   
	
			// Display the popup with given size/position, and run the request given by form
	    	runPopupDropDown(appreqForm, dropFieldObject, dropWidth, dropHeight);		
		}
		else
		{
			var mycmd = "0b}K0}56";
			//To identify Build Routine With Dropdown
			var routineArgs = "BUILDDROP"+ "_" +enqName+ "_ENQUIRY_";
			enquiryWindowName = createResultWindow( mycmd, 600, 300 );
			updatedFormsField( "WS_parentWindow",window.name );
			//window name information sent to T24 for popdown with build Routine,so F.ENQUIRY.LEVEL will write with proper key 
			appreqForm.windowName.value = enquiryWindowName; 
			appreqForm.dropfield.value = _dropField;
			appreqForm.routineArgs.value= routineArgs;
			appreqForm.target = enquiryWindowName;
			newElement.value = enquiryWindowName;
			appreqForm.appendChild(newElement);
			FragmentUtil.submitForm(appreqForm);
			// Reset deal target back to orginal deal window
			appreqForm.target = dealWindowName;
			if (savepopupDropDown_GLOBAL)                                     
       	 	{                                                                 
          		//restore the state otherwise all future dropdowns will popup 
          		popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;              
       		} 
		}
	}
	loadingIconType = "normal";

}

function enquiryBuildSelect(thisEvent)
{

	if(thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var _enquiry = thisField.getAttribute("enquiryField");
	var _dropField = thisField.getAttribute("dropField");
	var _criteriaField = thisField.getAttribute("criteriaField");
	var _criteriaOp = thisField.getAttribute("criteriaOpField");
	var _criteriaDataField = thisField.getAttribute("criteriaDataField");
	var _criteriaDataString = thisField.getAttribute("criteriaDataString");
	var _fieldName = thisField.getAttribute("enqFieldName");
	var _popupDropField = thisField.getAttribute("popupdropfield");               
	var _noinputDropField = thisField.getAttribute("noinputDropField");           
	loadingIconType = "dropdown";
    if ( ( _popupDropField == undefined ) || ( _popupDropField == null ) )        
    {                                                                             
             _popupDropField = "";                                                     
    }                                                                                                    
    if (_popupDropField != "")                                                    
    {                                                                             
              // save global variable to be restored later                              
              savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;                          
              // change to null so that new window pops up instead of dropdown          
              popupDropDown_GLOBAL = "";                                                
    }
	
	var enq = setContextEnquiryFields( _enquiry );
    updatedFormsField( "WS_dropfield", _dropField );

    var searchCriteria = "";
	if ( _criteriaField != "" )
	{
		var criteriaData = "";
		if(_criteriaDataString != "")
		{
 			//get the criteriadata from criteriaDataString
 			criteriaData = _criteriaDataString;
 			if (_criteriaOp=="RG")
            {
		    	criteriaData = "<value>" + criteriaData + "</value>";
		     }
 		}
 		else
 		{
			// Get the field value
			criteriaData = getFieldValue( _criteriaDataField );
			// Check we got a value, if this is a multi-value field we may need to
			// take the value from the first multi-value/sub-value, so try these
			if ( criteriaData == "" )
			{
				// Check for first multi-value of this field
				criteriaData = getFieldValue( _criteriaDataField + ":1" );

				if ( criteriaData == "" )
				{
					// Check for the first sub-value of this field
					criteriaData = getFieldValue( _criteriaDataField + ":1.1" );
				}
			}

		}
		searchCriteria = _criteriaField + " " + _criteriaOp + " " + criteriaData;
		
		// if _noinputDropField variable holds the ("") empty value, the condition should fail
		//if(!(_noinputDropField==null || _noinputDropField==""))
		//{
	      //searchCriteria = "";
		//}
	}
	else
	{
		// No criteria data has been set, however check if they have added a filter for this field
		// Set-up the args including search criteria - add "..." to any search criteria in the field
		var criteriaData = getFormFieldValue( "appreq", _dropField );
		criteriaData = stripSpacesFromEnds(criteriaData);
		
		if ( ( criteriaData == undefined ) || ( criteriaData == null ) )
		{
			criteriaData = "";
		}
		
		if ( criteriaData != "" )
		{
			// Add wildcard suffix if no dots already at the end of the string
			if ( criteriaData.charAt( criteriaData.length - 1 ) != "." )
			{
				criteriaData = criteriaData + "...";
			}
			
			searchCriteria = "@ID LK " + criteriaData;
		}
		if(!(_noinputDropField==null || _noinputDropField==""))
		{
	      searchCriteria = "";
		}
	}

    var routineArgs = "BUILDDROP"+ "_" +_enquiry + "_ENQUIRY_" + searchCriteria;

	if( currentForm_GLOBAL == "" )
	{
		currentForm_GLOBAL = "generalForm";
	}
	
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
    appreqForm.routineArgs.value= routineArgs;	
    // add the dropfield name with application since it's enquiry but sends as context request type for popupdropdown.
	if ( _dropField !="" )
	{
		appreqForm.dropfield.value = _dropField;
	}
	var selectionDisplay = thisField.getAttribute("selectionDisplay");
    // new element added in the form to send the parent window reference to server while dropdown with build rtn is clicked.
    var newElement = document.createElement("input");
	newElement.type = "hidden";
	newElement.name = "EnqParentWindow";
	// selection display box request
	if(selectionDisplay)
	{	
		
		var mycmd = "0b}K0}56";
 		enquiryWindowName = createResultWindow( mycmd, 600, 300 );
 		updatedFormsField("WS_parentWindow",window.name);
 		appreqForm.routineArgs.value= routineArgs+"_selectionDisplay";
 		appreqForm.target = enquiryWindowName;
 		newElement.value = enquiryWindowName;
 		appreqForm.appendChild(newElement);
 		FragmentUtil.submitForm(appreqForm);
 		// Reset deal target back to orginal deal window
 		appreqForm.target = dealWindowName;
 		if (savepopupDropDown_GLOBAL)                                     
         {                                                                 
          //restore the state otherwise all future dropdowns will popup 
          popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;              
         }   
	}
	else
	{
		if(popupDropDown_GLOBAL)
		{
		newElement.value = window.name;			
		appreqForm.appendChild(newElement);

		var windowOrFragmentName = FragmentUtil.getWindowOrFragmentName();
		targetWin = windowOrFragmentName;                                 
		appreqForm.target = windowOrFragmentName;                         

		// Store the name of the target as this window could be the parent for a dropdown
		storeWindowVariable( targetWin );                                                
		var savedCompScreen = appreqForm.compScreen.value; 
		
		// Get the drop field                                               
		var dropFieldObject = getFormField( currentForm_GLOBAL, _dropField);

		// Set fixed height/width dimensions - constants in dropdown.js
		var dropWidth = dropWidth_GLOBAL;                          
		var dropHeight = dropHeight_GLOBAL;   
		
		// Display the popup with given size/position, and run the request given by form
		runPopupDropDown(appreqForm, dropFieldObject, dropWidth, dropHeight);
	}
	else
	{
 		var mycmd = "0b}K0}56";
 		enquiryWindowName = createResultWindow( mycmd, 600, 300 );
 		updatedFormsField( "WS_parentWindow",window.name );
 		updatedFormsField("windowName",enquiryWindowName);
 		appreqForm.target = enquiryWindowName;
 		newElement.value = enquiryWindowName;
 		appreqForm.appendChild(newElement);
 		FragmentUtil.submitForm(appreqForm);
 		// Reset deal target back to orginal deal window
 		appreqForm.target = dealWindowName;
 		if (savepopupDropDown_GLOBAL)                                     
         {                                                                 
          //restore the state otherwise all future dropdowns will popup 
          popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;              
         }   
	}
	}
	loadingIconType = "normal";
	
}

/**
 * TODO
 * @param {TODO} TODO
 */
function dropDownPick( _value , _fieldName, _enrichment, fragmentName)
{
    // Get the fields objects.
  	var dropFieldForm = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
  	var dropFieldFormId = dropFieldForm.getAttribute("id");
    var dropFieldObject = getCurrentTabField( dropFieldFormId, _fieldName );
    
    // Set the field's value.
    if (dropFieldObject != null )
    {
    	try { dropFieldObject.focus(); } catch( e){/** Do nothing if we cant focus**/};
    	doFieldFocusEvent( dropFieldObject);
    	dropFieldObject.value = _value;
    	if( isFirefox())
    	{
    		// Call the field change event as this has cuased a change on the field
    		doFieldChangeEvent( dropFieldObject);
    	}
    }
        
    // Set the field's enrichment
    // If the field is a CHECKFILE field, then don't set it's enrichment as this will already have been done by the doFieldChangeEvent()
	var checkFileEnri = dropFieldObject.getAttribute("checkfile");
			

	//If the field is a check file run the web checkfile enrichment routine
	var validationRoutine = dropFieldObject.getAttribute("vr");
	if (checkFileEnri != undefined && validationRoutine != undefined)
	{
		doWebValidationField( _fieldName, validationRoutine );
	}
	
	// if the field is web validate run the web validation
	var webVal = dropFieldObject.getAttribute("webVal");
	var enriFieldName = _fieldName.replace("fieldName:", "enri_");
	var enriObject = document.getElementById( enriFieldName );
	if(enriObject != null) {
		enriObject.style.visibility= "visible";
		if(enriObject.getAttribute("enrichmentOnly") != null)
		{
			var enriWidth = enriObject.getAttribute("colspan");
			if(enriWidth!=null)
			{
				enriObject.style.width= (enriWidth*7)+"px";
			}
			enriObject.style.display="inline-block";
		}
	}
	if (webVal != undefined && validationRoutine != undefined)
	{
		doWebValidationField( _fieldName, validationRoutine );
	}
	
	
	// If the field is an auto launch field then run it
	var autofield = dropFieldObject.getAttribute("auto");
	var autoLaunch = dropFieldObject.getAttribute("autoEnqName");
	if (autofield != undefined )
	{
		doFieldContextEnquiry(autoLaunch, _fieldName);
	}
		
	// If the field is a hot field then run the validation
	var hotfield = dropFieldObject.getAttribute("hot");
	var hotval = dropFieldObject.getAttribute("hotVal");
	
	// If invoked from a drop down, then will be set to 'N'.
	// Run it anyway - the hotfield will be enabled on receiving focus.
	if ( hotfield != undefined )
	{
		if(hotval=="Y")
		{
			setHotField( _fieldName,true);
			doHotValidate( _fieldName);
		}
		else
		{
			processHotField( _fieldName, dropFieldObject );
		}
	}    
	   
}

/**
 * Run when a dropdown value is selected
 * @param {TODO} TODO
 */ 
function pick(_v, _fieldName, _enrichment)
{
    if(top.opener == null || top.opener.closed) // parent window closed ,don't try to populate the value
	{
		if(FragmentUtil.getParentWindow()==null || FragmentUtil.getParentWindow().closed)
		{
		  if ( ! FragmentUtil.isCompositeScreen() )
			{
				//prevent popup confirmation for IE7
				window.open('','_parent','');
				window.close();
		        return;
			}
		}
	}
    // Set the current fragment name
    Fragment.setCurrentFragmentName("dropDownDivDisplay");
    
	// Find the field on the parent form and set it's value
	if (_fieldName.indexOf("value:") == 0)
	{
		//setting value from enquiry selection screen
		var dealField = setFieldValue( _v, _fieldName,"enqsel" );
	}
	else
	{
		//normal contract screen
		var dealField = setFieldValue( _v, _fieldName,"appreq" );
	}
	 if ( dealField != null )
	{
		// If an enrichment field exists for this field set it's value too
	    var parentWin = FragmentUtil.getParentWindow();
	    	
		//If the field is a check file run the web checkfile enrichment routine
		var checkFileEnri = dealField.getAttribute("checkfile");
		var validationRoutine = dealField.getAttribute("vr");
		
		if (checkFileEnri != undefined && validationRoutine != undefined)
		{
			parentWin.doWebValidationField( _fieldName, validationRoutine );
		} else{
			// if the field is web validate run the web validation
			var webVal = dealField.getAttribute("webVal");
			var enriFieldName = _fieldName.replace("fieldName:", "enri_");
			var enriObject = parentWin.document.getElementById( enriFieldName );
			if(enriObject != null) {
				enriObject.style.visibility= "visible";
			}
			if (webVal != undefined && validationRoutine != undefined)
			{
				parentWin.doWebValidationField( _fieldName, validationRoutine );
			}
			/**
            *Call to setDropdownEnrichment is removed to avoid populating enrichment to a filed
            *that doesn't contains any check file or web Validation configured.
            */
		}
		// If the field is an auto launch field then run it
		var autofield = dealField.getAttribute("auto");
		var autoLaunch = dealField.getAttribute("autoEnqName");
					
		if (autofield != undefined )
		{
			parentWin.doFieldContextEnquiry(autoLaunch, _fieldName);
		}
			
		// If the field is a hot field then run the validation
		var hotfield = dealField.getAttribute("hot");
		var hotval = dealField.hotVal;
		
		// If invoked from a drop down, then will be set to 'N'.
		// Run it anyway - the hotfield will be enabled on receiving focus.
		if ( hotfield != undefined )
		{
			if(hotval=="Y")
			{
				parentWin.setHotField( _fieldName,true);
				parentWin.doHotValidate( _fieldName);
			}
			else
			{
				parentWin.processHotField( _fieldName, dealField );
			}
		}
		// Close the dropdown window - if it's not in a composite screen
		if ( ! FragmentUtil.isCompositeScreen() )
		{
			//prevent popup confirmation for IE7
			window.open('','_parent','');
			window.close();
		}
	}
}

/**
 * Set the enrichment for a field from the dropdown
 * @param {TODO} TODO
 */
function setDropdownEnrichment( _fieldName, _enrichment)
{
	if ( _enrichment != "" )
	{
		var parentWin = FragmentUtil.getParentWindow();

		// If an enrichment field exists for this field set it's value too
		var enriFieldName = _fieldName;
		enriFieldName = enriFieldName.replace("fieldName:", "enri_");
		var enqForm = FragmentUtil.getForm("enquiry");
		if(enqForm!=null)
		{
			var parentFormId = FragmentUtil.getForm("enquiry").WS_parentFormId.value;
		}
		
		// Check if we have a enrichment field with the correct name
		if ( _fieldName != enriFieldName )
		{
			var enriParagraph = "";
			if(parentFormId!=null && parentFormId!="" && parentFormId!=undefined)
			{
				if((top.opener != null) && !(top.opener.closed))
				{
					enriParagraph = getTabEnrichmentSpan(parentFormId, enriFieldName );
				} else if ( (FragmentUtil.getParentWindow()!=null) && !(FragmentUtil.getParentWindow().closed))
				{		
					enriParagraph  = getTabEnrichmentSpan(parentFormId, enriFieldName );
				}else {
					enriParagraph = parentWin.document.getElementById( enriFieldName );
				}				
			}else {
				enriParagraph = parentWin.document.getElementById( enriFieldName );
			}
			if (enriParagraph != null)
			{
				enriParagraph.innerHTML = _enrichment;
			}
		}
	}
}

/**
 * Run when a calendar value is selected
 * @param {TODO} TODO
 */
function pickDate(_day, _month, _year, _fieldName, thisEvent)
{
	// Find the field on the parent form and set it's value
	// Ensure days and months are 2 digits
	var seldates = FragmentUtil.getElementsByClassName("selectday","a","");
	if(seldates.length > 0)
	{
		if(seldates[0].className.indexOf("today") == 0)
		{
		seldates[0].className = "today";
		}else
		{
		seldates[0].className = "";
		}
	}
	
	if(thisEvent.className)
	{
		thisEvent.className = thisEvent.className + " selectday";
		}
		else
		{
		thisEvent.className = "selectday";
	}
	
	if ( _day.length == 1 )
	{
		_day = "0" + _day;
	}
	
	if ( _month.length == 1 )
	{
		_month = "0" + _month;
	}
	if( currentForm_GLOBAL == "" )
	{
		currentForm_GLOBAL = "appreq";
	}
	
	pickedDate = _year + _month + _day;
	
	if(freqDropDown_GLOBAL != currentDropDown_GLOBAL)	//Other than relative calendar like popups.
	{
		var dealField = setFieldValue( _year + _month + _day, _fieldName , currentForm_GLOBAL);
		setFormFieldValue( currentForm_GLOBAL, _fieldName, _year + _month + _day); 
		if ( dealField != null )
		{
			//prevent popup confirmation for IE7
			window.open('','_parent','');
			window.close();
		
			// If the field is an auto launch field then run it
			var autofield = dealField.auto;
			var autoLaunch = dealField.autoEnqName;
			
			if (autofield != undefined )
			{
				var parentWin = FragmentUtil.getParentWindow();
				parentWin.doFieldContextEnquiry(autoLaunch, _fieldName);
			}
		
			// If the field is a hot field then run the validation
			var hotfield = dealField.getAttribute("hot");
	
			//if invoked from a drop down, then will be set to 'N'.
			//Run it anyway - the hotfield will be enabled on receiving focus.
			 
			if ( hotfield != undefined )
			{
				var parentWin = FragmentUtil.getParentWindow();
				parentWin.processHotField( _fieldName, dealField );
			}
		}
	}
}

/**
 * Sets the value of a field on a deal form
 * @param {TODO} TODO
 * @return the form field reference
 * @type Object
 */
function setFieldValue( _value, _fieldName, _form )
{	
	var form;
	var field;
	
	// Get the form that the field is on from the parent window and
	// copy the value to the field on the parent window
	var parentWin = FragmentUtil.getParentWindow();
	var enqForm = FragmentUtil.getForm("enquiry");
	if(enqForm!=null)
	{
		var parentFormId = FragmentUtil.getForm("enquiry").WS_parentFormId.value;
	}
	if ( ( parentWin != undefined ) && ( parentWin != null ) )
	{
		// get the form reference, adjusting for Fragment name as necessary
		var form = '';
		if(parentFormId!=null && parentFormId!="" && parentFormId!=undefined)
		{
			if((top.opener != null) && !(top.opener.closed))
			{
				form = top.opener.document.getElementById(parentFormId);
			} else if ( (FragmentUtil.getParentWindow()!=null) && !(FragmentUtil.getParentWindow().closed))
			{
				form = FragmentUtil.getParentWindow().document.getElementById(parentFormId);
			}else {
				form = 	FragmentUtil.getForm(_form, parentWin);
			}				
		}else {
			form = 	FragmentUtil.getForm(_form, parentWin);
		}
   
		if ( form != null )
		{
			found  = false;
			//loop through all the elements on the form
			for ( var i = 0;(i < form.elements.length) && (!found); i++ )
			{
				field = form.elements[i];
				if ((field != null) && (field.name == _fieldName))
				{   				   
					var formField = form.elements[ _fieldName ];
					var enriFieldName = _fieldName.replace("fieldName:", "enri_");
	    			var dropEnriObject = getEnrichmentSpan(enriFieldName, form);
					if ( (field.disabled == false  &&  field.type != "hidden") || (dropEnriObject.className != null && dropEnriObject.className != '' && dropEnriObject.className.indexOf("enrichmentonly") != -1) )
					{
						// Set the field value and move the focus from dropdown button to the field
						field.value = _value;
						if(field.type != "hidden" && field.disabled == false) {
							if(_form != "enqsel")
						{
							var activeTab = form.elements["activeTab"];
							if (field.getAttribute("tabname") == activeTab.value)
							{
								field.focus();
							}
						} else
						{
							field.focus();
						}
						}
							if (!formField.length)
							{
								found = true;
							}
							else
							{
						  	for (i=1; i<formField.length ; i++ )
						   	  {
						 		 field = formField[i];
								 if (field.name == _fieldName )
						          {
						 			 field.value = _value;
									 if(_form != "enqsel")
									{
										if (field.getAttribute("tabname") == activeTab.value)
										{
											field.focus();
										}
									} else
									{
										field.focus();
									}
						          }
						      }
						 	  found = true;
						  }
					 }
				     else
					 {
						 field = null;
					 }
				  }
			  }
		  }
	  }
	   	
	return( field );
}

/**
 * Initialises a dropdown list
 * @param {TODO} TODO
 */
function initDropdownList()
{
	// Set the status bar
	setWindowStatus();
}

/**
 * Initialises a calendar popup.
 */
function initCalendarPopup(dropDownId)
{
	// Set the focus and onblur event
    var calmonthList = document.getElementById( "monthList"+dropDownId);
	calmonthList.focus();
	var calendarDiv = document.getElementById(dropDownId + ":div");
	calendarDiv.scrollIntoView(false);
}

/**
 * Mouse Over event on a calendar popup
 */
function calendarDateMouseOver()
{
	// Calendar date could have been picked - needed to determine whether to hide the popup control or not
	calDateClicked_GLOBAL = true;
}

/**
 * Mouse Out event on a calendar popup
 */
function calendarDateMouseOut()
{
	// Calendar date could not have been picked - needed to determine whether to hide the popup control or not
	calDateClicked_GLOBAL = false;
}


/**
 * Wrapper for popup (dropdown) calendar from a contract screeen, using event to determine field.
 * @param {TODO} TODO
 */
function dropDownCalendar(thisEvent)
{
	if(thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var _textField = thisField.getAttribute("calendardropfieldname");
	loadingIconType = "dropdown";
	displayDropDownCalendar(_textField);
	calDateClicked_GLOBAL = false; // reset the variable 
}

/**
 * Wrapper for popup (dropdown) calendar from an enquiry selection screeen.
 * @param {String} dropFieldId the target field for the dropdown result
 */
function enqselDropDownCalendar(dropFieldId)
{
	// set the form name global for enq selection context
	setCurrentForm( "enqsel" );
	loadingIconType = "dropdown";
	displayDropDownCalendar(dropFieldId);
}

/**
 * Displays a dropdown version of the calendar.
 * Uses generalForm, generic dropdown elements, Fragment request ..
 * @param {String} dropField the target field for the dropdown result
 */
function displayDropDownCalendar(dropFieldId) {	
	// Get the drop field
	var dropFieldObject = getCurrentTabField( currentForm_GLOBAL, dropFieldId );
	// If the calendar has been opened from a frequency control, the field won't be in the appreq form
	// So if it's null just try a getElementById()
	if (!dropFieldObject) {
		dropFieldObject = document.getElementById(dropFieldId);
	}

    // Set fixed height/width dimensions
	var dropWidth = calDropWidth_GLOBAL;
	var dropHeight = calDropHeight_GLOBAL;
	
    // Set general form params
    var genForm = FragmentUtil.getForm("generalForm");
    genForm.requestType.value = "oh1y1haCb}oh16Y";
    genForm.routineName.value = "}XC>YhCetyY60tb";
    genForm.dropfield.value = dropFieldId;
	var enqselForm = FragmentUtil.getForm("enqsel");
	
    // Get selected month and year values, if present
	var monthYearArgs = "";
	var monthList = document.getElementById( "monthList"+dropFieldId );
	var yearList = document.getElementById( "yearList"+dropFieldId );
	// send the application,version and enquiry name in the request
	var app = genForm.application.value;
	var version = genForm.version.value;
	var enqname = '';
	if (enqselForm != null)
	{
		enqname = enqselForm.enqname.value;
	}
	if( monthList != null && yearList != null )	{
	    var month = monthList.options[monthList.selectedIndex].value;
	    var year = yearList.options[yearList.selectedIndex].text;
	    monthYearArgs = month + ":" + year;
	}
	
	if(app != null && app != '')
	{
		genForm.routineArgs.value = "_" + monthYearArgs + "_" + dropFieldId + "_" +app;
		if(version != null && version != '')
		{
	  		genForm.routineArgs.value = "_" + monthYearArgs + "_" + dropFieldId + "_" + app + "_" + version;	
		}	
	}
	else if (enqname != null && enqname != '')
	{
		genForm.routineArgs.value = "_" + monthYearArgs + "_" + dropFieldId + "_" +"_"+"_"+enqname;
	}
	else
	{
		genForm.routineArgs.value = "_" + monthYearArgs + "_" + dropFieldId;
	}
	// Display the popup with given size/position, and run the request given by form
	runPopupDropDown(genForm, dropFieldObject, dropWidth, dropHeight);			
}

/**
 * Initialize the size and position of the general popup elements.
 * @param {TODO} TODO
 */
function runPopupDropDown(formRef, dropField, w, h) {
    
	//defensive coding since there are problems with cloning in I.E
	if (dropField.name != "")
	{
		if (dropField.id != dropField.name)
			{
				dropField.setAttribute("id",dropField.name);
			}
	}
	
	if( currentDropDown_GLOBAL != "" )
	{
		// Crude way of getting around the problem with frequency control
		// because it has a control within a control we dont want to hide the parent
		// dropdown in this case.
		if ( dropField.id != "fqu:nextDate")
		{
			hidePopupDropDown( currentDropDown_GLOBAL);
		}
	}
	// Update UI model
	currentDropDown_GLOBAL = dropField.id;
	
	// Create the container for the dropdown display
	createPopupDropDownObjects(dropField,w,h);

    // Create a Fragment for the dropdown view
    var dropDownFragment = Fragment.getFragment(dropField.id + ":div");
    
    // Set the windowName to the calling windowName to allow this request past the form field validation in the servlet.
	var fragmentName = FragmentUtil.getWindowOrFragmentName();
    formRef.windowName.value = fragmentName;

	// issue the request built up in the form
	// and populate the view element with the result
	// .. as Fragment.populateWithForm,
	// .. plus extra param, to indicate that the response should be transformed as a page dropdown.
	var params = FragmentUtil.getFormFieldsAsParams(formRef);
	params += "&WS_FragmentName=" + fragmentName + "&WS_pageDropDown=true";
	dropDownFragment.populateWithURL(formRef.action, params);
}

/**
 * Creates a new iframe and div element of the specified size for displaying the dropdown object
 * @param {InputElement} dropField The field the dropdown display is linked to.
 * @param {int} w The desired width of the popup, in pixels.
 * @param {int} h The desired height of the popup, in pixels.
 */
function createPopupDropDownObjects(dropField,w,h) {

	var dropFieldId = dropField.getAttribute("id");
	var ddContentBox = document.getElementById(dropFieldId + ":div");
	var ddBackground = document.getElementById(dropFieldId + ":iframe");
	
	// If the popup elements don't exist, create them, otherwise just make them visible
	if (ddContentBox == null) {
		var body = document.getElementsByTagName("body")[0];
		var fquBackground = document.getElementById("fqu:nextDate:iframe");
		ddContentBox = document.createElement("div");
		ddBackground = document.createElement("iframe");
		ddBackground.setAttribute("src","../html/blank_enrichment.html"); // Required for IE6 on Win2000 machines.
		body.insertBefore(ddBackground,fquBackground);
		body.insertBefore(ddContentBox,fquBackground);
		// Why use insertBefore and fquBackground?
		// Because there is only one calendar popup for all frequency controls, which needs to remain at the bottom of the document.
		// This way when visible it will appear 'on top' of the frequency control without having to calculate z-index values.
		// If the page has no frequency controls then fquBackground = null, and insertBefore behaves like 'appendChild'
		
		// Set properties on the new elements
		ddContentBox.setAttribute("id",dropFieldId + ":div");
		ddContentBox.setAttribute("tabindex","0");
		ddContentBox.className = "abs popup-view";
		ddBackground.setAttribute("id",dropFieldId + ":iframe");
		ddBackground.className = "abs";
		ddBackground.setAttribute("frameborder","0");
		
		// event triggered when mouse down and mouse out happens on the outer container. 
		// The value of the status variable is set and unset when the corresponding events occur.
		if (freqDropDown_GLOBAL!="")
		{
			ddContentBox.onmousedown = function(){OutercontainerMousedown();};
			ddContentBox.onmouseout = function(){OutercontainerMouseOut();};
		}
		
		// event triggered when mouse down and mouse out happens in the inner calendar window 
		// The value of the status variable is set and unset when the corresponding events occur.
		if(freqDropDown_GLOBAL!="" && currentDropDown_GLOBAL!="" && currentDropDown_GLOBAL!=freqDropDown_GLOBAL)
		{
		 	ddContentBox.onmousedown = function(){innercalendarMousedown();};
		 	ddContentBox.onmouseout = function(){innercalendarMouseOut();};
		}
		
	} else {
		removeStyleClass(ddContentBox, "hidden");
		removeStyleClass(ddBackground, "hidden");
	}
	
	// Set the dimensions of the new objects
	setPopupDropDownDimensions(dropField,w,h);
}

/**
 * Mouse down event on an outer container
 */
function OutercontainerMousedown()
{
	// Needed to determine whether to hide the inner calendar or not when clicked in the outer container
	closeSingleWindow = true;
}

/**
 * Mouse Out event on an outer container
 */
function OutercontainerMouseOut()
{
	// Needed to determine whether to hide the inner calendar or not when clicked in the outer container
	closeSingleWindow = false;
}

/**
 * Mouse down event on an inner calendar
 */
function innercalendarMousedown()
{
	// Needed to determine the availability of the inner calendar inside a container
	validFreTagName = true;
}

/**
 * Mouse out event on an inner calendar
 */ 
function innercalendarMouseOut()
{
	// Needed to determine the availability of the inner calendar inside a container
	validFreTagName = false;
}

/**
 * Set the general popup size/position attributes.
 * @param {InputElement} dropField The field the dropdown display is linked to.
 * @param {int} w The desired width of the popup, in pixels.
 * @param {int} h The desired height of the popup, in pixels.
 */
function setPopupDropDownDimensions(dropField, w, h) {

    // Set size and position of the popup elements
	// Pass -1 as dimension to accept existing value
	var dropFieldId = dropField.getAttribute("id");
	var ddContentBox = document.getElementById(dropFieldId + ":div");
	var ddBackground = document.getElementById(dropFieldId + ":iframe");

	var x = "";
	var y = "";
	if(isInternetExplorer())
	{
		x = getElementX(dropField)+2;
		y = getElementY(dropField)+21;
		
	} else {
		var enrihmentId = dropField.id.replace("fieldName:", "enri_");
		var enriObject = getTabEnrichmentSpan(currentForm_GLOBAL,enrihmentId);
		if(enriObject!=null && enriObject.getAttribute("enrichmentOnly") != null)
		{
			x = getElementX(enriObject)+2;
			y = getElementY(enriObject)+21;
		}else{
			x = getElementX(dropField)+2;
			y = getElementY(dropField)+21;
		}
	}

	if(dropField.style.display == "none")
	{
	dropField.style.display = "";
	x = getElementX(dropField)+2;
	y = getElementY(dropField);
	dropField.style.display = "none";
	}
	
	if (x >= 0) {
	    ddContentBox.style.left = x + "px";
	    ddBackground.style.left = x + "px";
	}

	if (y >= 0) {
	    ddContentBox.style.top =  y + "px";
	    ddBackground.style.top =  y + "px";
	}

	if (w >= 0) {
	    ddContentBox.style.width = w + "px";
	    ddBackground.style.width = w + "px";
	}

	if (h >= 0) {
		ddContentBox.style.height = h + "px";
		ddBackground.style.height = h + "px";
	}
}


/**
 * Hide the calendar popup elements.
 * @param (event) thisEvent In Firefox, the event that triggered the method call, null in IE
 */
function hideCalendarPopup(thisEvent)
{
	// Get the ID of the calendar field from the onblur event
	if (!thisEvent) {
		var thisEvent = window.event; //thisEvent will be null in IE, so use window.event
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	var calendarFieldId = thisField.getAttribute("id");

	// For some reason, sometimes the onblur event fires again, after the user has selected an item and closed the calendar.
	if (calendarFieldId == null) {
		return;
	}
	calendarFieldId = calendarFieldId.substring(0,calendarFieldId.length - 4); // strip off ":div" at the end

	// If a date has been hovered over then don't hide the calendar popup
    // Let the dropCalendarPickDate() event execute first then 'hidePopupDropDown()'
    // will be called again with 'calDateClicked_GLOBAL' set to false.
	if ( !calDateClicked_GLOBAL)
	{
		hidePopupDropDown(calendarFieldId);
	}
	var focusfield = document.getElementById(calendarFieldId);
	focusfield.focus();
}

/**
 * Hide the general popup elements.
 * @param (String) dropFieldId the field on which the dropdown has been called.
 */
function hidePopupDropDown(dropFieldId) {

	var dropFieldObjFoc = document.getElementById(dropFieldId);
	if (dropFieldObjFoc != null )
    {
    	try 
    	{ 
    		dropFieldObjFoc.focus();
    		dropFieldObjFoc.value = dropFieldObjFoc.value; 
    	}
    	catch( e)
    	{
    		// Do nothing if we cant focus
    	}
	}
	// Make the popup elements hidden again
	var ddContentBox = document.getElementById(dropFieldId + ":div");
	var ddBackground = document.getElementById(dropFieldId + ":iframe");
	
	if (ddContentBox != null) {
		// Hide the elements
		addStyleClass(ddContentBox, "hidden");
		addStyleClass(ddBackground, "hidden");
		
		// Remove any fields on the div so there is no possiblity of duplicate id's from them.
		ddContentBox.innerHTML = "";
		
		// reset when recurrence dropdown closes
		if (currentDropDown_GLOBAL!="fqu:nextDate")
		{
			freqDropDown_GLOBAL = "";
		}
		
		// Update UI model
		currentDropDown_GLOBAL = "";
		updatedFormsField( "WS_dropfield", "" );

	}
	
}

/**
 * Set focus, and optionally a blur handler (to close the popup)
 * @param {TODO} TODO
 */
function setPopupDropDownFocus(blurFunc, dropField) {

	var dropFieldId = dropField.getAttribute("id");
	var ddContentBox = document.getElementById(dropFieldId + ":div");
	var dropDownendRow = document.getElementById("rowHeader");
	
	// Focus set on the row and scrolls the entire element into view
		if(dropDownendRow!=null && dropDownendRow!="")
		{
			try { dropDownendRow.focus();	} catch( e){};
		}
	if(ddContentBox!=null && ddContentBox!="")
	{
		try { ddContentBox.scrollIntoView(false);	} catch( e){};		
    }	

	if ((typeof blurFunc) != "undefined") {
    	ddContentBox.onblur = blurFunc;
	}
}


/**
 * Picks a date from the calendar.
 * @param {_day} TODO
 * @param {_month} TODO
 * @param {_fieldName} TODO
 * @param {String fragmentName} The suffix of the fragment that launched the popup.
 */
function dropCalendarPickDate(_day, _month, _year, _fieldName, fragmentName)
{
	var dropFieldObject;
	if (_fieldName == "fqu:nextDate") {
		dropFieldObject = document.getElementById(_fieldName);
    } else {
        var dropFieldForm = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
        var dropFieldFormId = dropFieldForm.getAttribute("id");
		var dropFieldObject = getCurrentTabField( dropFieldFormId, _fieldName);
		var dropFieldImg = document.getElementById("relative-"+_fieldName);
    }
	if (dropFieldObject != null )
    {
    	try { dropFieldObject.focus(); } catch( e){/** Do nothing if we cant focus**/};
    
		// Find the field on the parent form and set it's value
		// Ensure days and months are 2 digits
		if ( _day.length == 1 )
		{
			_day = "0" + _day;
		}
	
		if ( _month.length == 1 )
		{
			_month = "0" + _month;
		}
		// Update the field
		dropFieldObject.value =  _year + _month + _day;
		if(dropFieldImg != null)
		{
		dropFieldObject.value =  "D_"+_year + _month + _day;		//add D_ before date.
		}
    }
	
	calDateClicked_GLOBAL = false;
	
	// Clear the calendar
	hidePopupDropDown(_fieldName);
	//
	// If the field is an auto launch field then run it
	var autofield = dropFieldObject.auto;
	var autoLaunch = dropFieldObject.autoEnqName;
		
	if (autofield != undefined )
	{
        doFieldContextEnquiry(autoLaunch, _fieldName);
	}
	
	// If the field is a hot field then run the validation
	var hotfield = dropFieldObject.getAttribute("hot");

	//if invoked from a drop down, then will be set to 'N'.
	//Run it anyway - the hotfield will be enabled on receiving focus.
	
	if ( tabList_GLOBAL.length > 1 && dropFieldObject.type!="radio")
    {
         var fieldList = getFormFields( currentForm_GLOBAL, _fieldName);  
                 
         for( var i = 0; i < fieldList.length; i++)
         {
             fieldList[i].value = dropFieldObject.value;    // update all tab's field value by current tab field value if duplicate field
         }
 
    }

	 
	if ( hotfield != undefined )
	{
		processHotField( _fieldName, dropFieldObject );
	}

	if ( _fieldName != "fqu:nextDate" && _fieldName.slice(0,6) != "value:" )  // to display enrichments for date fields as formatted date. Ex: 01 JAN 2009(not for enquiry selection page)       
 	{                                                                                                                       
         var monthArray = new Array("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC");                
         var month = dropFieldObject.value.slice(4,6);                                                                       
         month = monthArray[month -1];                                                                                       
         var calendarEnrichment = dropFieldObject.value.slice(6,8) + " " + month + " " + dropFieldObject.value.slice(0,4);   
         setEnrichment( _fieldName, calendarEnrichment);                                                                     
 	}

} 
/**
 * Displays a calendar page
 * @param {TODO} TODO
 */
function calendar(_textField)
{
	//disable the hotfield for now.  This will stop hotfields from running
	//automatically if changed.
	setHotField("false", _textField);
	//n.b. the hotfield will be enabled again on receiving focus
	
	// for auto launch field
	setAutoField("false", _textField);
	
	// Set-up any arguments for the calendar page
	var skin = getSkin();

	var parentWin = window.name;
	var popupArgs = "title='Calendar Popup' &routineArgs=" + "&routineName=" + "}XC>YhCetyY60tb" + "&searchCriteria=&dropfield=" + _textField + "&skin=" + skin + "&parentWin=" + parentWin;

	// Display the calendar page
	displayPopup( "Calendar", popupArgs );
}

/**
 * Updates a calendar page with a new month/year on display
 * @param {TODO} TODO
 */
function updateCalendar(thisEvent)
{
	var thisField = thisEvent.target || thisEvent.srcElement;
	var _textField = thisField.getAttribute("calendardropfieldname");
	
	// Get new calendar page for selected month and year
	var skin = getSkin();

	// Get the selected month - get the number value not the caption
	var monthList = document.getElementById( "monthList" );
	var month = monthList.options[monthList.selectedIndex].value;

	// Get the selected year 
	var yearList = document.getElementById( "yearList" );
	var year = yearList.options[yearList.selectedIndex].text;

	var monthYearArgs = month + ":" + year;

	var parentWin = getFieldValue( "WS_parentWindow" );
	var popupArgs = "title='Calendar Popup' &routineArgs=" + "&routineName=" + "}XC>YhCetyY60tb" + "&searchCriteria=" + monthYearArgs + "&dropfield=" + _textField + "&skin=" + skin + "&parentWin=" + parentWin;
	loadingIconType = "dropdown";
	// Display the calendar page
	displayPopup( "Calendar", popupArgs );
	loadingIconType = "normal";
}

/**
 * Displays a popup window and forwards to a page according to the arguments
 * @param {TODO} TODO
 */
function displayPopup( _windowName, _args )
{
	var _companyId = getFieldValue( "companyId" );
	var contextRoot = getWebServerContextRoot();
	var contextFull = location.protocol + "//" + location.host + "/" + contextRoot;
	//Pass the corresponding window name in the request
    var windowName = FragmentUtil.getWindowOrFragmentName();
	// Pass the args to the dropdown page
	var dropdownURL = contextFull + "/jsps/dropdown.jsp";
	var fullArgs = _args + "&compId=" + _companyId + "&context=" + contextFull+"&windowName="+windowName;
	var fullURL = dropdownURL + "?" + fullArgs;

	var _left="100";
	var _top="200";
	var _winDefs= "toolbar=0,status=1,directories=0,scrollbars=1,resizable=1,menubar=no,left=" +_left+ ",top=" + _top + ",width=340,height=200"; 

	var winName = getUser() + "_" + _windowName;
	winName = replaceSpecialChars( winName, "_" );
	
	var myWin=window.open(fullURL, winName, _winDefs);
}

/**
 * Displays attachment page
 * @param {TODO} TODO
 */
function upload(_textField)
{

	var _imageID = getFieldValue( "fieldName:UPLOAD.ID" );

	if (_imageID != "")
	{
		// Set-up any arguments for the calendar page
		var skin = getSkin();
		var popupArgs = "title='Attachment Popup' &routineArgs=BUILD_" + _imageID + "&routineName=" + "}XCthhtek=Y6h" + "&searchCriteria=&dropfield=" + _textField + "&skin=" + skin;
	
		// Display the upload page
		// Window name has been changed to create individual window for each contract screen		
		var popWindowName = FragmentUtil.getWindowOrFragmentName();
		if(popWindowName !=null)
		{
			popWindowName = popWindowName + "_uploadWindow";
		} else {
			popWindowName = "uploadWindow";
		}
		displayPopup(popWindowName, popupArgs );
	}
}

/**
 * Upload File page
 * @param {TODO} TODO
 */
function fileUpload(_textField)
{
	// get the 'attachment' form reference, adjusting for Fragment name as necessary
	var attForm = FragmentUtil.getForm("attachment");
    var _fileNameExtn = "";
    
    var unSuppFormats = new Array(".EXE", ".PIF", ".APPLICATION", ".GADGET",
			".MSI", ".MSP", ".COM", ".SCR", ".HTA", ".CPL", ".MSC", ".JAR",
			".BAT", ".CMD", ".VB", ".VBS", ".VBE", ".JS", ".JSE", ".WS",
			".WSF", ".WSC", ".WSH", ".PS1", ".PS1XML", ".PS2", ".PS2XML",
			".PSC1", ".PSC2", ".MSH", ".MSH1", ".MSH2", ".MSHXML", ".MSH1XML",
			".MSH2XML", ".SCF", ".LNK", ".INF", ".REG", ".SH", ".DLL", ".CHM",
			".ADE", ".ADP", ".APP", ".BAS", ".CRT", ".CSH", ".FXP", ".HLP",
			".INS", ".ISP", ".KSH", ".MDA", ".MDB", ".MDE", ".MDT", ".MDW",
			".MDZ", ".OPS", ".PCD", ".PIF", ".PRF", ".PRG", ".PST", ".SCT",
			".SHB", ".SHS", ".URL", ".WAR", ".BIN", ".CRX", ".MUI", ".THM",
			".PROFILE");	
	
	attForm.routineName.value="}XCthhtek=Y6h";
	attForm.routineArgs.value="oKy}t0";
	// get the upload Document name 	
	var _fileName = attForm.fileName.value;
	var _fileExtnLength = _fileName.lastIndexOf(".");
	//get the file extension
	if (_fileExtnLength > 0){
           _fileNameExtn = _fileName.substring(_fileExtnLength,_fileName.length);
           
           if (searchStringInArray(_fileNameExtn.toUpperCase(), unSuppFormats) == -1) {
                if (_fileName != "" )
                {
                     //the user has entered a file name
                     var _filePath = attForm.filePath.value;
                     var _imageId = attForm.imageId.value;
                     // The file extension appended with the file name
                     var _storeFileName = _imageId + _fileNameExtn;
                     window.opener.document.getElementById(_textField).value = _storeFileName;

                     // Submit the form
                     FragmentUtil.submitForm(attForm);
                }
           } else {
                alert("Unsupported file type.");
           }
     }else {
           alert("Unsupported file type.");
     }
}


/**
 * Download File doc
 * @param {TODO} TODO
 */
function fileDownload(docPath, fileLocation)
{
	// get the 'Download Document' form reference
	var FileDownloadform = FragmentUtil.getForm("enquiry");
	var newElement = document.createElement("input"); 
	//introduce the hidden fields
		newElement.type = "hidden";
		newElement.name = "DocDownloadPath";
		//assign the download path to hidden field value
		newElement.value = docPath;	
		FileDownloadform.appendChild(newElement);
		
		var fileElement = document.createElement("input"); 
		//introduce the hidden fields
		fileElement.type = "hidden";
		fileElement.name = "fileLocation";
		//assign the file location to hidden field value
		fileElement.value = fileLocation;	
		FileDownloadform.appendChild(fileElement);
		
		// Add the window name.
		var windowElement = document.createElement("input"); 
		 //introduce the hidden fields
		windowElement.type = "hidden";
		windowElement.name = "windowName";
		//assign the windowname
		windowElement.value = FragmentUtil.getWindowOrFragmentName();
		FileDownloadform.appendChild(windowElement);
		
		FragmentUtil.submitForm(FileDownloadform);
		FileDownloadform.removeChild(newElement);
		FileDownloadform.removeChild(fileElement);
		FileDownloadform.removeChild(windowElement);
}

/**
 * To upload a file to cloud
 * @param {TODO} TODO
 */
function downloadCloudFile(fileReference)
{
	var FileDownloadform = FragmentUtil.getForm("enquiry");
	var fragmentId = FragmentUtil.getWindowOrFragmentName();
	
	//Opera browser need to be handled differently
	// Downloaded document will be opened in a new window
	if (navigator.userAgent.toLowerCase().indexOf("opera") != -1)
	{
		//	FileDownloadform.setAttribute("method","get");
		FileDownloadform.setAttribute("action","../GetImage/image");	
	
		//introduce the hidden fields		
		if(fragmentId!=null && fragmentId!=window.name)
		{
			frameId = frameId + fragmentId; 
		}
		if(FileDownloadform.imageId==null || FileDownloadform.imageId==undefined )
		{
			var imageId = document.createElement("input");	
			imageId.type = "hidden";
			imageId.name = "imageId"; 
			imageId.value = fileReference;
			FileDownloadform.appendChild(imageId);	
		
			var isPopUp = document.createElement("input");
			isPopUp.type = "hidden";
			isPopUp.name = "isPopUp";
			isPopUp.value = "YES";
			FileDownloadform.appendChild(isPopUp);
			
			var isOpera = document.createElement("input");
			isOpera.type = "hidden";
			isOpera.name = "isOpera";
			isOpera.value = "YES";
			FileDownloadform.appendChild(isOpera);
			
			var windowname = document.createElement("input");	
			windowname.type = "hidden";
			windowname.name = "windowName"; 
			windowname.value = fragmentId;
			FileDownloadform.appendChild(windowname);	
			
		} else {
			FileDownloadform.imageId.value=fileReference;
			FileDownloadform.isPopUp.value="YES";		
		}
	
		FileDownloadform.target = frameId;		
		FileDownloadform.submit();
		return;
	} else {
		//to submit the request to genDocRequest.jsp with required information
		var requestUrl = "../jsps/genDocRequest.jsp?";
		requestUrl = requestUrl+"imageId="+fileReference+"&isPopUp=YES&windowName="+fragmentId;
	
		var frameId = "resultframe_";
		if(fragmentId!=null && fragmentId!=window.name)
		{
			frameId = frameId + fragmentId; 
		}
		// Create a new iFrame to handle errors in downloading process
		//iFrame is used to display the response in alert boxes the same window
		if(document.getElementById(frameId)==null || document.getElementById(frameId)==undefined )
		{
			var divElement= document.createElement("div");
			var frameElement = document.createElement("iFrame");
			frameElement.name= frameId;
			frameElement.id = frameId;
			frameElement.src = requestUrl;
			divElement.appendChild(frameElement);	
			divElement.style.height="20px";
			divElement.style.display="none";
	
			if(FileDownloadform.parentNode)
			{
				FileDownloadform.parentNode.appendChild(divElement);
			}
		}else {
		//Use the existing iFrame 
			var oldFrameElement=document.getElementById(frameId);
			oldFrameElement.src = requestUrl;		
		}
		return;
	}
}


/**
 * setting visibility of message for calendar.
 * @param {TODO} TODO
 */
function MM_findObj(n, d)
 { 
  var p,i,x;  
   if(!d) d=document; 
    if((p=n.indexOf("?"))>0&&parent.frames.length) 
     {
      d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
     }
    if(!(x=d[n])&&d.all) x=d.all[n];
     for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
      for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
       if(!x && d.getElementById) x=d.getElementById(n);
        return x;
   }

/**
 * TODO
 * @param {TODO} TODO
 */
function showHideLayers() 
{ 
	var i,p,v,obj,args=showHideLayers.arguments;
	
	for (i=0; i<(args.length-2); i+=3)
	{
		if ((obj=MM_findObj(args[i]))!=null)
		{ 
			v=args[i+2];
			if (obj.style)
			{
				obj=obj.style;
				v=(v=='show')?'block':(v=='hide')?'none':v;
			}
			obj.display=v;
		}
	}
}

/**
 * Opens a dropdown for a checkfile type field
 * @param {event thisEvent} The event that triggers this function.
 */
function mvDropDown(thisEvent)
{
	if(thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode != 0)
	{
		return;
	}
	var thisField = thisEvent.target || thisEvent.srcElement;
	if( currentForm_GLOBAL == "" )
	{
		currentForm_GLOBAL = "appreq";
	}

	var application = thisField.getAttribute("mvApplication");
	var textField = thisField.getAttribute("mvTextField");
	var fieldName = thisField.getAttribute("mvFieldName");
	
	var popupDropField = thisField.getAttribute("popupDropField");
	var selectionDisplay = thisField.getAttribute("selectionDisplay");
	
	// special check for Firefox
	// If we are displaying enrichment only (as set in the version record) then set the styles
	// This lets us position the dropdown correctly and the subsequent dropdown
	if (isFirefox())
		{
		var enrichmentOnly = thisField.getAttribute("enrichmentOnly");
		if (enrichmentOnly != null)
		{
			//display enrichment only
			var textFieldId = document.getElementById(textField);
			textFieldId.style.position="absolute";
			textFieldId.style.display="block";
		}
	}
	loadingIconType = "dropdown";	
	dropdown(application, textField, fieldName, popupDropField,selectionDisplay);
}


/**
 * dropdown command for enqsel screen
 * @param {String} _textField associated drop field
 * @param {String} _enq enquiry name
 * @param {Optional} _selectionDisplay
 */
function enqselDropDown(_textField, _enq, _popupDropField,_selectionDisplay) {

	// Set-up the args including search criteria from text already in the target field
	var searchCriteria = getFormFieldValue( "enqsel", _textField );
	searchCriteria = stripSpacesFromEnds(searchCriteria);
	// If text present, try to add "..." to wildcard the search
	if ( searchCriteria ) {
		// Add wildcard suffix if no dots already at the end of the string
		if ( searchCriteria.charAt( searchCriteria.length - 1 ) != "." ) {
			searchCriteria = searchCriteria + "...";
		}
	}
	var routineArgs = "RELATED_" + searchCriteria;
	
	popupDropDown_GLOBAL = 'true';

	if ( ( _popupDropField == undefined ) || ( _popupDropField == null ) )
	{
		_popupDropField = "";
	}
	
	if (_popupDropField != "" && _selectionDisplay != "Yes")
	{
		// save global variable to be restored later
		savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
		// change to null so that new window pops up instead of dropdown
		popupDropDown_GLOBAL = "";
		//send down a flag to indicate this dropdown needs to popup
		routineArgs += "_"+_popupDropField;
	}
	
	setWindowVariableMarker("true");
	storeWindowVariable( window.name );
	
 	// set the form name global for enq selection context
	setCurrentForm( "enqsel" );
	loadingIconType = "dropdown";
	// selection Display box request
	if(_selectionDisplay == "Yes")
	{
			// save global variable to be restored later
			savepopupDropDown_GLOBAL = popupDropDown_GLOBAL;
			// change to null so that new window pops up instead of dropdown
			popupDropDown_GLOBAL = "";
			routineArgs += "__selectionDisplay";
			var saveParentWindow = getFieldValue( "WS_parentWindow" );
			doEnquiryRequest("0b}K0}56", _enq, _textField, "", "", routineArgs, "NEW", "", "01XKyta");
			if (savepopupDropDown_GLOBAL)
			{
				//restore the state otherwise all future dropdowns will popup
				popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
			}
			updatedFormsField( "WS_parentWindow", saveParentWindow );
	}
	else
	{
		if(popupDropDown_GLOBAL )
		{
			doEnquiryRequest("0b}K0}56", _enq, _textField, "", "", routineArgs, "", "", "01XKyta");
		}
		else
		{
			doEnquiryRequest("0b}K0}56", _enq, _textField, "", "", routineArgs, "NEW", "", "01XKyta");
			if (savepopupDropDown_GLOBAL)
			{
				//restore the state otherwise all future dropdowns will popup
				popupDropDown_GLOBAL = savepopupDropDown_GLOBAL;
			}
		}
	}
	loadingIconType = "normal";
	setWindowVariableMarker("false");
}
function setFocusInsideCal(theEvent,currentElement,focusElement)
{
  if(theEvent.keyCode == 9)
  {
    var tdElement = currentElement.parentElement;
	if(tdElement.nextSibling == null)
	{
	  // check whether we are in the last element of calendar
	  if(tdElement.parentElement.nextSibling == null)
	  {
	    var focusElementObj = document.getElementById(focusElement);
		focusElementObj.focus();
	  }
	}
  }
 }
function searchStringInArray (str, strArray) {
	for (var j=0; j<strArray.length; j++) {
    	if (strArray[j].match(str)) return j;
    }
    return -1;
}

