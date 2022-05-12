/** 
 * @fileoverview Functions used by the deal form
 *
 * @author XXX
 * @version 0.1 
 */

// Indicates whether the window is actually being closed (as opposed to just being unloaded)
var closingWindow = '';
var setWindowPosition = '';
// The id of the deal slip report
var showorhide = '';     //whether to show or hide the menu on init
var autoLaunch = 'NO';
// This variable indicates wheather we are using dropdowns that pop up in the page. 
// If not null then it means we have dropdown on the page.
var popupDropDown_GLOBAL = false;
//This varaible holds a list of all the application forms available in the page. 
var appFormList_GLOBAL = new Array();
// Holds the main form.
var mainAppForm_GLOBAL = "";
// A object pointer to multiPane form.
var multiPaneForm_GLOBAL = "";
//identify temporarily resetted hotfield
var resetHotField_Global = "";
//To identify the radio button field
var hotfieldType = "";
//To check whether context enquiry or not.
var enquiryType = "";
// Enumaration for Browser functions
var BrowserFunction = {
    See : "S",
    Authorise : "A",
    Input : "I",
    Validate : "VAL",
    Hold : "HLD"
};
//Holds the window name needed when the back button is pressed
var windowName_GLOBAL = "";

//Holds the window name for the contracts in no frames deal
var noFramesDealWindowName_GLOBAL = "";

// A global varaible to hold the list of displayed tabs
var tabList_GLOBAL = new Array(); 

// A global variable to decide the target for auto launch context enquiry 
var autoLaunch_GLOBAL = "";

/**
 * Updates navigation operation for ARC
 */
 function doNavigation(navivalue)
{
	var appreqForm = FragmentUtil.getForm("appreq");
	var cfnavoperation = "";
	cfnavoperation = navivalue;

	if (cfnavoperation=="BACK")
	{
		appreqForm.cfNavOperation.value="BACK";
	}
    if (cfnavoperation=="CANCEL")
	{
		appreqForm.cfNavOperation.value="CANCEL";
	}
	doDefaultButton();
}
 /**
 * TODO
 * @param {TODO} TODO
 */
function doFieldContext(ceids,enqids, enqdescs, fname,skin,olddata, realfname,fieldnum)
{
	// Get the current form in which context enquiry action triggered.
	var formName = FragmentUtil.getForm(currentForm_GLOBAL);
	// Extract form id.
	var formId = formName.id;
	
	var contextRoot = getWebServerContextRoot();
	var dropContext = '';
	//Clear WS_dropfield if any set
	updatedFormsField( "WS_dropfield", dropContext ); 
	
    //in fname is null in case of textbox controls
    if (fname=="")
	{
		if((fieldnum!="") || (fieldnum!="undefined") || (fieldnum!="null"))
		{
		fname = "fieldName:"+realfname+fieldnum;
		}
		else
		{
		fname = "fieldName:"+realfname;
		}
	}
	// escape any & for URL encoding...
	olddata = replaceAll(olddata,"'","\\'");
	enqdescs = replaceAll(enqdescs,"'","\\'");
	enqids = replaceAll(enqids,"'","\\'");	
	//Special characters are escaped and non-ascii characters are converted to unicode character
	enqdescs = escape(enqdescs);
	enqids = escape(enqids);
	// escape() replaced by encodeURI() to avoid complexity in encoding unicode characters in websphere
	olddata = encodeURI(olddata); 
	//If unicode character exists then the % prefixing it needs to be converted to '\' as it is the standard
	enqdescs = replaceAll(enqdescs,"%u","\\u");
	enqids = replaceAll(enqids,"%u","\\u");
	olddata = olddata.replace("%u","\\u");
	var quickURL = location.protocol + "//" + location.host + "/" + contextRoot + "/jsps/context.jsp";
	var args = "fname=" + fname + "&skin=" + skin + "&fname=" + fname + "&realfname=" + realfname;
	args = args + "&ceids=" + ceids;
	args = args + "&enqids=" +enqids;
	args = args + "&enqdescs=" +enqdescs;
	args = args + "&olddata=" +olddata;
	// Add current form id with request parameter.
	args = args + "&formid="+formId;
	var fullURL = quickURL + "?" + args;
	var _left="100";
	var _top="200";
	var _winDefs= "toolbar=0,status=1,directories=0,scrollbars=1,resizable=1,menubar=no,left=" +_left+ ",top=" + _top + ",width=300,height=300"; 

	// Change the skin in the same window - use the current window name
	var windowName = window.name + "_context_";

	var myWin=window.open(fullURL, windowName, _winDefs);
	// Need to turn this into a jsp...
	
}
/**
 * Process items from the "more actions" combo box
 * @param {TODO} TODO
 */
function doMoreActions()
{
	// Find the object that holds the options
	var combo = FragmentUtil.getElement("moreactions");

	if (combo==null) 
	{
		// Shouldnt happen but acts as an error trap
	} 
	else 
	{
		var item = combo.selectedIndex;           // The item that is selected
		var itemtext = combo.options[item].value; // And the text

		// We now have the type of item and the target in the form TYPE|ITEM
		// e.g. CONTEXT.ENQUIRY!CUSTOMER_CUSTOMER.POSITION so split this out
	
		sourceString = itemtext;
		iCurrentPos = 0;
		iSearchStringPos = sourceString.indexOf( "|", iCurrentPos );
		
		// If we have found something, then process the item according to the "old" functions
		if ( iSearchStringPos != -1 )
	    	{
			// Set the parent window name to be that of the current window for pickable items on the dropdowns
		    	updatedFormsField( "WS_parentWindow", window.name );

	        itemType = sourceString.substring( iCurrentPos, iSearchStringPos );
	        // Update the request type so that context enquiry response will get opened in new window always
	        // irrespective of COS screen target frame.
	        	
			itemTarget = sourceString.substring( iSearchStringPos+1, sourceString.length );
		
			if(itemType=="e}6hYqhCY6lo1ba")
			{
				enquiryType = itemType; // To indicate its a context enquiry.
				doContextEnquiry(itemTarget);
			}
	
			if(itemType=="Y6lCy1Xh")
			{
				doenqList(itemTarget);
			}
	
			if(itemType=="Y6lCXYtbek")
			{
				doenqSearch(itemTarget);
			}
	
			if(itemType=="ekt>YCe}=Kt6a")
			{
				doloadCompany(itemTarget);
			}
	        // Implementation of User Roles functionality in T24        
			if(itemType=="ekt6>YCb}yY")
			{
				doSwitchRole(itemTarget);
			}
	
			if(itemType=="teh1}6")
			{
				doDeal(itemTarget);
			}
	
			if(itemType=="Y01h")
			{
				doEdit(itemTarget);
			}	
		}	
	}
}
/**
*updates the rekey fields in the form when rekey is keyed in.
*/
function OnRekeyChange(rekeyField)
{
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	var fldName = rekeyField.name;
	var appreqrekeyField = getFormField( currentForm_GLOBAL, fldName );
    appreqrekeyField.value = rekeyField.value;
  
}

/**
 * Changes the focus to a specified error
 * @param {String} TODO
 */
function linkError(tabname, fieldname)
{
	var errorField = '';

	// First we need to change to the tab that has the error
	changetab(tabname);

	//Then get the field and change focus to it - if it is enabled
	errorField = getFormField( currentForm_GLOBAL, fieldname );
	//To enable the focus event for radio button and check box fields
	if (errorField.type=="hidden") 
	{
		var radioFname=fieldname.split(":");
		if(radioFname[0]=="fieldName")
		{
		fieldname="radio:"+ tabname+":"+radioFname[1];
		}
		errorField = getFormField( currentForm_GLOBAL, fieldname );
		if(errorField==null)
		{
			fieldname="CheckBox:"+"fieldName"+":"+radioFname[1];
		}
		errorField = getFormField( currentForm_GLOBAL, fieldname );
	}
	if ( ( errorField != null ) && ( errorField.disabled == false ) && ( errorField.type != "hidden" ) )
	{
		errorField.focus();
	}
}

/**
 * inspect 'elm' and alert if need too
 * @param {TODO} TODO
 */
function inspect(elm)
{
	var str = "";

	for (var i in elm)
	{
		if(""!=elm.getAttribute(i))
		{
			str = i + ": " + elm.getAttribute(i) + "\n";
			  alert(str);
		}
	}
}


/**
 * Initialise the deal form and submi a print slip request
 * @param {TODO} TODO
 */
function initDealAndPrintSlip(windowTop, windowLeft, windowWidth, windowHeight, reportList)
{
	// First initialise the deal in the normal way
	initDeal(windowTop, windowLeft, windowWidth, windowHeight);
	// Then submit any deal slips
	submitPrintSlips(reportList,currentForm_GLOBAL);	
}

/**
 * Checks if there are window sizes specified in the form.
 * @param {TODO} TODO
 * @return window's property 
 */
function isWindowSizesPresent()
{
	var windowTop = windowLeft = windowWidth = windowHeight = endPos = startPos = "";
	// Check if new window positions have been saved on the form
	// We prefer these as the ones passed in may have come from a cached version
	var windowSizes = getFormFieldValue( currentForm_GLOBAL, "windowSizes" );

	if ( windowSizes != "")
	{
		// Window sizes are separated by colons so extract the values for re-sizing
		endPos = windowSizes.indexOf(":", 0);
		windowTop = windowSizes.substring(0,endPos);
		
		startPos = endPos + 1;
		endPos = windowSizes.indexOf(":", startPos);
		windowLeft = windowSizes.substring(startPos,endPos);
		
		startPos = endPos + 1;
		endPos = windowSizes.indexOf(":", startPos);
		windowWidth = windowSizes.substring(startPos,endPos);
		
		startPos = endPos + 1;
		windowHeight = windowSizes.substring(startPos,windowSizes.length);
	}
	
	return { windowTop: windowTop, windowLeft: windowLeft, windowWidth: windowWidth,  windowHeight: windowHeight };		
}

/**
 * This goes through all the forms in the page and gets the form names of only the applicaiton forms. Not general,mainmenu or enquiry.
 */
function getAppFormsNames()
{
	//Clear appFormList_GLOBAL before rebuilding.
	appFormList_GLOBAL = new Array();
	// Set the main app form to be "appreq" always.
	mainAppForm_GLOBAL = "appreq";
	// Go through all the forms in the page
	var currentFragmentName = Fragment.getCurrentFragmentName();
	for(var i = 0; i < document.forms.length ; i++ )
	{
		var aForm = document.forms[i];
		var formName = aForm.getAttribute("id");
		if(aForm.getAttribute("isArrangement") == 'true')
		{	
			currentFragmentName = '';
		}
		// Only extract application forms
		if( formName != "" 
			&& formName.indexOf("generalForm") < 0
			&& formName.indexOf("mainmenu") < 0
			&& formName.indexOf("enquiry") < 0
			&& formName.indexOf("multiPane") < 0
			&& formName.indexOf("appreq") < 0
			&& formName.indexOf(currentFragmentName) > -1
		)
		{
			if ( !contains( appFormList_GLOBAL, formName ) )
			{	
				// Populate global variable with the rest of the forms.
				if (formName.indexOf("newPropertyDateForm_") < 0)
				{
				appFormList_GLOBAL[appFormList_GLOBAL.length] = formName;
				}	
				currentFragmentName = Fragment.getCurrentFragmentName();
			}
		}
	}
}

/**
 * Is an element in an array ?
 * @param {TODO} TODO
 * @return 'true' or 'false'
 */
function contains( arr, elem )
{
	for ( j = 0; j < arr.length; j++ )
	{
		if ( arr[j] == elem )
		{
			return true;
		}
	}
	return false;
}

/**
 * Initialise the deal form
 * @param {TODO} TODO
 */ 
 function initDeal(windowTop, windowLeft, windowWidth, windowHeight)
 {
 	resizeOninit = "1";
	// Get the application forms available in the page.
	getAppFormsNames();
	
	// Update current form
	setCurrentForm( mainAppForm_GLOBAL );
	
	// Get coordinates from form, if available..
	windowCoordinates = isWindowSizesPresent(); 
	if ( windowCoordinates != null )
	{
		windowTop = windowCoordinates.windowTop;	
		windowLeft = windowCoordinates.windowLeft;
		windowWidth = windowCoordinates.windowWidth;
		windowHeight = windowCoordinates.windowHeight;
	}
	// Set closing window flag so we know when we are really closing the
	// window as opposed to   just unloading the window for a result
	closingWindow = true;
	// Check to see if we are in a frame (i.e. a composite screen)
	// If we are, then do not do the resize
	setWindowPosition = checkresize("appreq", windowTop, windowLeft, windowWidth, windowHeight);  // Important
	// Set the status bar
	setWindowStatus();
	// Init tabs
	initTabs();
	// Allign changes tab if any
	resizeOninit = "1";
	initEnquiry('', windowTop, windowLeft, windowWidth, windowHeight);
	resizeOninit = "1";	
	// Init Overrides
	initOverrides();
	// Init warnings
	initWarnings();
	
	// Accumulates commands send from T24.
	var commands = getFormFieldValue( currentForm_GLOBAL, "newCommands" );

	// Set default enrichment for check boxes.
	setCheckBoxEnrichment();

	// If there are more application forms then initialise them as well.
	for(var i = 0; i < appFormList_GLOBAL.length; i++)
	{
		// Set the current form.
		setCurrentForm( appFormList_GLOBAL[i] );
		// Init tabs
		initTabs();

		// Collect any specified commands in to run in new windows
		var moreCommands = getFormFieldValue( appFormList_GLOBAL[i], "newCommands" );
		if( moreCommands != "")
		{
			commands += "_" + moreCommands;	
		}
		// Set default enrichment for check boxes.
		setCheckBoxEnrichment();
	}	
	
	if(appFormList_GLOBAL.length > 0)
	{
		var checkShowtabs = getFormFieldValue("generalForm", "WS_showTabs");
		if(checkShowtabs == "true")
		{
			var imgObject = document.getElementById("showTab");
			if(imgObject)
			{
			imgObject.src = "../plaf/images/"+getSkin()+"/tools/showTab_dis.gif";
			
			var imgParentA = imgObject.parentNode;
			var imgParentTD = imgParentA.parentNode;
			
			imgParentTD.removeChild(imgParentA);
			imgParentTD.appendChild(imgObject);
			}
		}
	}else
	{
		if(getFormFieldValue("generalForm", "WS_showTabs")=="true"){
			updatedFormsField( "WS_showTabs", "false" );}	// To reset the WS variable when coming out of bulk request screen.
	}
	
	// Check if we have dropdowns that appear in the page.
	var testPopup = getFormField( currentForm_GLOBAL, "popupDropDown" );
	if(testPopup != null && testPopup.value != '')
	{
		popupDropDown_GLOBAL = testPopup.value;
	}
	//Block back button functionality for composite screens for now
	if (!FragmentUtil.isCompositeScreen())
	{
		// This is a new contract so initialise the history
		// in case the user clicks back on the browser 
		initDealHistory();
	}
	
	var checkErrors = window.document.getElementById("error_box").className;	//To know wether errors/overrides present in the window.
	// Check whether simulation screnn has any Error/override.If any, it will process the command.
	if ( commands != "" )
	{
		if(!(checkErrors.indexOf("display_box_none")==-1 && commands.indexOf("DE.PREVIEW")==-1 && appFormList_GLOBAL.length>0))   
        {
			processNewCommands( commands);
		}
	}
	//set scrollbar and size on main window
	var eventvalue = "";
	resizeOninit = "1";
	resizeDiv(eventvalue);
	// Set the expansion type based on the server.
	setMvSvExpansionType();
	// Get list of displayed tabs
	tabList_GLOBAL = getDisplayedTabs();
	// Initialise radio buttons
	initRadios();
    // Set focus on the main form.
	setCurrentForm( mainAppForm_GLOBAL );
	initSetFocus();

	// Raise a transaction complete event if it is the case.
	var txnComplete = getFormFieldValue( currentForm_GLOBAL, "txnComplete" );
	if( txnComplete == "true" )
	{
		// Get the current application
		var app = getFormFieldValue( currentForm_GLOBAL, "application" );
		// Get the current version
		var version = getFormFieldValue( currentForm_GLOBAL, "version" );
		// Raise the event
		raiseTransactionComplete( app + version);
	}
	resizeOninit = "";
 }
 
 /**
 * Init radios with their selected value
 */  
 function initRadios()
 {
	// Get all the elements and check for the attribute and value 
	var currFormObj = FragmentUtil.getForm( mainAppForm_GLOBAL );
	initFormRadios( currFormObj );
	
	// If this is an AA multi-pane screen, initialise all of the radio buttons on all of the sub-forms
	var multiRequest = getFormField("generalForm", "multiPane");
	if( multiRequest != null )
	{
		if( appFormList_GLOBAL.length > 0 )
		{
			for(var i = 0; i < appFormList_GLOBAL.length; i++)
			{
				currFormObj = FragmentUtil.getForm( appFormList_GLOBAL[i] );
				initFormRadios( currFormObj );
			}
		}
	}
 }
 
 /**
 * Init radios with their selected value when new form is created on inputting forward dated conditions.
 */
 function initfwddatedradios(fwddateproperty)
 {
 	var arrindx = appFormList_GLOBAL.find(fwddateproperty);
 	if((arrindx != "") && (arrindx != null))
 	{
 		var fwddateformobj = FragmentUtil.getForm( appFormList_GLOBAL[arrindx] );
 		initFormRadios(fwddateformobj);
 	}
 }
 
 /**
 * Init radios with their selected value on a particular form
 */  
 function initFormRadios ( formObj )
 {
 	var elements = formObj.getElementsByTagName("input");

	// Go through the elements
	for( var i = 0; i < elements.length; i++)
	{
		var inputElement = elements[i];
		
		if (inputElement.type == "radio")
		{
			if( inputElement.getAttribute( "selected") == "true" )
			{
				inputElement.setAttribute( "checked", "true");
				// Update the hidden fields
				// Extract the radio id.
				var radioId = inputElement.getAttribute( "id");
				// Get the value
				var value = inputElement.getAttribute( "value");
				// Break it up.
				var tokens = radioId.split(":");
				// 2 being the index at which the field name is.
				var fieldName = tokens[ 2];
				if(tokens[3]!=null )
				{
					fieldName=fieldName+":"+tokens[3];
				}
				var hiddenFieldName = "fieldName:" + fieldName;
				var formId = formObj.id; 
				var hiddenFieldObjects = getFormFields( formId, hiddenFieldName);
				// Go through all the hidden fields on the page with the given id and update their values.
				for( var j = 0; j < hiddenFieldObjects.length; j++)
				{
					var currentHiddenField = hiddenFieldObjects[j];
					currentHiddenField.setAttribute( "value", value);
					var autopop=currentHiddenField.getAttribute("autoPopulated");  // Check if the radio button is auto populated
					if(!autopop)                                                  // if no autoPopulated attribute update the old value
					{
					currentHiddenField.setAttribute( "oldValue", value);
					}
				}
			}
		}
	}
 }
 

/**
  * Initialise the deal History. Required for the back button handling
  */ 
  function initDealHistory()
  {
	if(window.BrowserHistory)
	{
		// Initialise & Record the history
		var dataArray = {};
		// Extract the original command that was run to get here
		dataArray.command = getFormFieldValue(currentForm_GLOBAL, "WS_initState");
		// Store the window name for the target
		dataArray.windowname = window.name;
		BrowserHistory.initialize("deal", dataArray);
	}
  }
  
/**
 * Go through the tables in the page and pick out the ones that 
 * have 'isTab' set to 'true'
 */  
 function getDisplayedTabs()
 {
 	// Define the output array
	var out = new Array(); 

 	// Get all the elements and check for the attribute and value 
	var currFormObj = FragmentUtil.getForm( currentForm_GLOBAL );

	if (currFormObj != null)
	{	
		var elements = currFormObj.getElementsByTagName("table");
 	

		for ( var i = 0; i < elements.length; i++ ) 
		{
		
			var isTab = elements[i].getAttribute( "isTab");
			// Only get the tables that have are tabs
			if( isTab != null && isTab == "true" )
			{
				// Appends the element to the output array.
				out[out.length] = elements[i].id; 
			}
		}
	}
	
	return out;
 }
 
 /**
 * Set the focus
 */ 
 function initSetFocus()
 {
 	// If we are a parent window, the let the focus go to the child window when it appears
	if ( ! isParent() )
	{
		
		var checkError = window.document.getElementById("error_box");
		if(checkError!=null)
		{
			var checkErrors = checkError.className;
			if(checkErrors.indexOf("display_box_none") != 0)
			{
				var errorImg = window.document.getElementById("errorImg");
				if(errorImg != null )
				{
					errorImg.focus();
				}
			}
		else
		{
			// Set focus to transaction Id box if there is one
			var transId= getFormField( currentForm_GLOBAL, "transactionId" );
			if ((transId!= null) && (transId.type!="hidden")&&(transId.value==""))
			{
				transId.focus();
			}
			else
			{
				var focusFieldName = getFormFieldValue( currentForm_GLOBAL, "focus" );
				if ( focusFieldName != "" )
				{
					// Find the field and set the focus on it
					focusDealField( focusFieldName );
				}
			}
	  	}
	  }
	}
 }	

 /**
  * Used to initialise tab applications.
 */ 
 function initTabs()
 {
	// Show the main tab
	var mainTab = getFormField( currentForm_GLOBAL, "mainTab");
	if (mainTab != null)
	{
		mainTab.style.visibility="visible";
	}
	var activeTab = getFormField( currentForm_GLOBAL, "activeTab");
	// Now Show the first tab - if there is one
	if ( ( activeTab != "undefined" ) && ( activeTab != null ) && ( activeTab != "" ) )
	{
		if ( activeTab.value != "" )
		{
			changetab(activeTab.value);
		}
	}
 }

/**
 * Used to initialise overrides.
 */	
 function initOverrides()
 {
 	// Check if any override response if YES then call acceptOverrides to
	// set the colour to green!
	var overridesPresent = getFormFieldValue(currentForm_GLOBAL, "overridesPresent");   //Update getFieldValue to take in a form as a prameter.
	if ( overridesPresent != null )
	{
		var overrideAccepted = getFormFieldValue(currentForm_GLOBAL, "overridesAccepted");
		if (overrideAccepted == "YES")
		{
			acceptOverrides();
		}
	}
  }
/**
 * Used to initialise warnings.
 */
 function initWarnings()
 {
 	// Check if any Warnings have a response if so then call the appropriate calss
	var warningsPresent = getFormFieldValue(currentForm_GLOBAL, "warningsPresent");
	if (warningsPresent != "") {
		// get the 'mainmenu' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		for ( var i = 0; i < appreqForm.elements.length; i++ )
		{
			var field = appreqForm.elements[i];
			// Only want to process combo boxes
			if (field.type == "select-one")
			{
			 	var name = field.name;

		 		// check to see if the combo box is a warning
			 	var pos = name.indexOf("warningChooser");
			 	if (pos==0)
			 	{
			 		var caption = "warningCaption" + name.substring(14,name.length);
			 		changeWarning(caption , name);
				}			
			}
		}
	}
 }

/**
 * Sets the enrichemnt of all the checkboxes to its unchecked value.
 */
function setCheckBoxEnrichment()
{
	// Get the contract form.
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	//added defensive code
	if (appreqForm != null)
	{
	    var thisEnrichment = null;
		// Update the hidden override fields on the deal form to show each is accepted
		for ( var i = 0; i < appreqForm.length; i++ )
		{
			var field = appreqForm.elements[i];
			// Only want to process hidden override fields
			if (field.type == "checkbox")
			{
				if ( field.checked == true )
				{
	    	      	var checkboxcap = null;
					checkboxcap = field.getAttribute("checkedCap");
					if (checkboxcap!="")
						{
						//for showing other language enrichment if exist at loading time of checkbox
						thisEnrichment = checkboxcap;
						}
					else
						{
						thisEnrichment = field.getAttribute("checkedValue");
						}
	    	    }
				else
				{
		 		    thisEnrichment = field.getAttribute( "uncheckedValue");
		 		}
		 		var enriFieldName = field.name.replace("CheckBox:", "");
		 		setEnrichment( enriFieldName, thisEnrichment);
			}	
		}
	}
}
/**
 * Check if we have a defualt button specified to be used if the 
 * user submits using carriage return.
 * @return false is the submit had been cancel
 */
 function doDefaultButton()
 {
 	var ofsFunction = getDefaultButtonFunction();
 	if( ofsFunction != "")
 	{
	 	// Check if this is a multiform contract or just an 'appreq' one.
		var multiRequest = getFormField("generalForm", "multiPane");
		if( multiRequest != null & (ofsFunction == "I" | ofsFunction == "VAL" | ofsFunction == "A" | ofsFunction == "D" | ofsFunction == "R" | ofsFunction == "H"  | ofsFunction == "HLD") )
		{
			doMultiRequest(ofsFunction);
		}
		else
		{
			doDeal(ofsFunction);
		}	
 	}
 	// Cancel this submit, since the deal has already been submitted in one of the methods above.
 	return false;
 }

/**
 * Get the default button function.
 * @return 'ofsFunction'
 */
  function getDefaultButtonFunction()
  {
  	var ofsFunction = "";
  	// This is getting the default action from a global DOM lookup (with fragment suffix, if reqd)
	// Might need to use form-specific lookup if > 1 default button per screen
 	var defaultButton = FragmentUtil.getElement("defaultButton");
 	//			
	if( defaultButton != null )
	{
		// Check if we are already running another event
		var state = defaultButton.getAttribute("state");
		if ( state != "OFF" )
		{
	    	ofsFunction = defaultButton.getAttribute("defaultFunction");	
	    }
	}
	return ofsFunction;
  }

/**
 * Submits a completed deal to the server
 * @param {TODO} TODO
 * @return 'fasle'
 */ 
function doDeal(ofsFunction)
{	
	
	//If the currentForm_GLOBAL is empty then the appreqForm is set as currentForm. 
	if( currentForm_GLOBAL == "")
	{
			currentForm_GLOBAL = "appreq";
			setCurrentForm("appreq");
	}
	
	// if we have a trans id box and it's empty, then we don't
	//want to process the deal
	var transId= getFormField( currentForm_GLOBAL, "transactionId" );
	if (transId == null || transId == '')
	{
		return;
	}
	transId.value = stripSpacesFromEnds(transId.value);
	//checking for autolaunch enquiry before record is getting committed
	if( top.autoLaunch == "Y")
	{
		var dt = new Date();
		//A small delay is introduced to allow autolaunch enquiry to complete
	   	dt.setTime(dt.getTime() + 100);
	   	while (new Date().getTime() < dt.getTime());
	   	//Possibility of Autolaunch to be resetted in the response in enquiry.js under initEnquiry(),hence Return autolaunch if exist
	   	if (top.autoLaunch == "Y")
		{
			return;
		}
	} 
	
	var cmdname = "";
	var app = "";
	var version = "";
	
	if ((transId.type!="hidden") && (transId.value==""))
	{
		//cancel the event
		return false;
	}
		
		var key = "";
		//strip the spaces from the ends of the id
		var idField = getFormField( currentForm_GLOBAL, "transactionId");
		idField.value = stripSpacesFromEnds(idField.value);
		transId = idField.value;
		argsNumber = processCommandString( transId );

		if (argsNumber > 1)
		 {
				transId = transId.split(" ");
				//set key to be last argument..
				key = transId[transId.length-1];
		 }

		// check to see if the key is actually a function 
		if ( validFunctionCheck( key ) )
		{
			var myFunction = stripSpacesFromEnds( transId[0] );
			var myKey = stripSpacesFromEnds( transId[transId.length-1] );
			
			if ( myFunction == "L" && myKey == "L" )
			{
				transId = "L";
				key = "L";
			}
			else if ( myFunction == "E" && myKey == "E" )
			{
				transId = "E";
				key = "E";
			}
			else
			{
				transId = key;
				key = "";
			}
		}
		else if (argsNumber > 2)
		{
			//just take the first argument to be the function
			transId = transId[0];
		}
		else
		{
			//do as normal
			transId = idField.value;
		}
		
		var ftn = ofsFunction;
		
		
		// If we are doing a list then set the function for this, otherwise
		// check if they have only supplied a function in the transaction id field.
		// If so then just ignore the input as the user is just trying to switch function
		// on the application screen (which is not supported in Browser).
		if ( transId == "L" || transId == "E" )
		{
			ftn = transId;
			//Initialise the window target
			windowTarget = "";
		}
		//else if ( ( validFunctionCheck( transId ) && ( idField.type!="hidden") ) )
		//{
			// Cancel the deal event, clear the function from the transaction id field and return
		//	idField.value = "";
		//	return false;
		//}
		
		// Check if a function has been supplied with an Id, if so then we will need to
		// use the OS.NEW.DEAL utility request to bring up the screen in the right mode
		var index = transId.indexOf(" ");

		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		if ( (index != -1) && (index = 1 ) )
		{
			var fn = transId.substring( 0, index ); 					// Get the function letter
			if ( fn != "I" )
			{
			ftn = fn;
			key = transId.substring( index + 1, transId.length );	// Get the key
			
			// Check if the supplied function is valid
			validFunction = functionCheck(ftn);
			validSpecialFunction = specialFunctionCheck(ftn);
		
			if ( validFunction )
			{
				idField.value = transId.substring( index + 1, transId.length );
			}
			else if ( validSpecialFunction )
			{
				// It's a special function (e.g. A,V,D, etc) so we will need to run a utility request
				// Build the command arguments for the request
				var app = appreqForm.application.value;
				version = appreqForm.version.value;
				cmdname = app + version + " " + ftn + " " + key;
			}
			else
			{
				ftn = ofsFunction;
			}
		}
	}

		if ( ftn == "L" || ftn == "E" )
		{
			setWindowVariableMarker("true");
	
			var appTarget = getCompositeScreenTarget( appreqForm.application.value );
	
			if ( appTarget != "" && appTarget != "NEW" )
			{
				storeWindowVariable( appTarget );
			}

			// Set the parent window name to be that of the current window for pickable items on the dropdowns
	    		updatedFormsField( "WS_parentWindow", window.name );
		}
		
		if ( ftn == "L" )
		{
			// Need to list the live file for this application, cancelling the deal event
			app = appreqForm.application.value;
			ver = appreqForm.version.value;
			idField.value = "";
			dropfield = "transactionId";
			
			if ( key == "L" )
			{
				cmdname = "Y6l" + " %" + app + ver;
				
				// Get enquiry selection form
				doEnquiryRequest("XYyYeh1}6", cmdname, dropfield, "", "", "", cmdname, "", "01XKyta");
			}
			else
			{
				// Get enquiry results using the full selection criteria
				doenqDropfieldList( "Y6l" + " %" + app + ver, dropfield );
			}
			
			setWindowVariableMarker("false");
		}
		else if ( ftn == "E" )
		{
			// Need to list the unauthorised file for this application, cancelling the deal event
			app = appreqForm.application.value;
			ver = appreqForm.version.value;
			idField.value = "";
			dropfield = "transactionId";
			
			if ( key == "E" )
			{
				cmdname = "Y6l" + " %" + app + "$NAU" + ver ;
				
				// Get enquiry selection form
				doEnquiryRequest("XYyYeh1}6", cmdname, dropfield, "", "", "", cmdname, "", "01XKyta");
			}
			else
			{
				// Get enquiry results using the full selection criteria
				doenqDropfieldList( "Y6l" + " %" + app + "$NAU" + ver , dropfield );
			}
			
			setWindowVariableMarker("false");
		}
		else
		{
			if ( ofsFunction == "Kb16h" )
			{
				// Print deal locally
				if(FragmentUtil.isCompositeScreen() && top.noFrames == true)
				 {
					var fragmentName = window.Fragment.getCurrentFragmentName();
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
			else
			{
				// Indicate we are unloading the window and not closing it
				closingWindow = false;
				showState("busy");
				//
				processWarnings();
				// Do any auto launch enquiries
				doIdAutoLaunch();
				// Set-up parameters for a Globus command and submit it
				appreqForm.requestType.value="}rXCtKKy1eth1}6";
				appreqForm.ofsOperation.value="Kb}eYXX";
				appreqForm.GTSControl.value="";
				appreqForm.ofsFunction.value=ftn;
				// For non standard functions logic goes here
				if ( ftn == "VAL" )
				{
					appreqForm.ofsOperation.value="Rty10thY";
					appreqForm.ofsFunction.value="I";
				}
				else if ( ftn == "HLD" )
				{
					appreqForm.ofsOperation.value="Kb}eYXX";
					appreqForm.GTSControl.value="4";	// Force record to HOLD - '4' is currently arbitrary
					appreqForm.ofsFunction.value="I";
				}
				else if ( ftn == "HLDAUTO" )
				{
					appreqForm.ofsOperation.value="Kb}eYXX";
					appreqForm.GTSControl.value="SHLD";	// Save Hold - as hold, but status SHLD not IHLD
					appreqForm.ofsFunction.value="I";
				}
				else if ( ftn == "EDIT" )
				{
					appreqForm.ofsOperation.value="8o1y0";
					appreqForm.ofsFunction.value="I";
				}
				else if ( ftn == "VIEW" )
				{
					appreqForm.ofsOperation.value="8o1y0";
					appreqForm.ofsFunction.value="S";
				}
				else if ( ftn == "LOCK" )
				{
					appreqForm.ofsOperation.value="8o1y0";
					appreqForm.ofsFunction.value="S";
					appreqForm.routineArgs.value="y}eP"; // We need to lock the record - for old Server code compatibility
					appreqForm.lockArgs.value="y}eP";    // We need to lock the record
				}
				else if ( cmdname != "" )
				{
					// Command was set to run as a utility request
					appreqForm.ofsFunction.value="S";
					buildUtilityRequest( "}XC6Y5C0Yty", cmdname, currentForm_GLOBAL, window.name, "", "", "", "", "" );
				}
				
				// Submit the request to the Browser Servlet if not already done as a utility request
				if ( cmdname == "" )
				{
					// NEW behaviour to handle automatic background hold requests
					// Add the function parameter, so the new 'HLDAUTO' requests - see above - can be treated specially
					FragmentUtil.submitDealForm(appreqForm, ftn);
				}
			}
		}
	
	// Whether we listed E, L or are editing a record, return false as the default event has already been handled
	return false;
}


/**
 * Processes the request for incresing a multi value set
 * @param {TODO} TODO
 */
function doMultiValue(requestType,mvDetails)
{
	// Check if this is a multiform contract or just an 'appreq' one.
	// We only want to show the busy screen on the whole page for 
	// normal single contract requests, as sub-pane requests will
	// show this as part of teh Fragment code.
	var multiRequest = getFormField("generalForm", "multiPane");

	if ( multiRequest == null )
	{
		showState("busy");
	}
		
	//if there are no overrides or all overrides have been checked
	if (processOverrides(true) == true)
	{
		// Indicate we are unloading the window and not closing it
		closingWindow = false;

		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		// Set-up parameters for a Globus command and submit it
		appreqForm.requestType.value=requestType;
		appreqForm.routineArgs.value=mvDetails;
		appreqForm.ofsFunction.value="I";

		// If deal ID supplied on form then Operation is POPULATE, otherwise Operation is PROCESS
		// Check if there is a transactionId input box
		appreqForm.ofsOperation.value="8o1y0";
		
		// Submit the request to the Browser Servlet
		if ( multiRequest == null )
		{
			submitDealForm();
		}
		else
		{
			doSubPaneMvSvRequest();
		}
	}
	//return to the deal
	else
	{
		if ( multiRequest == null )
		{
			showState("ready");
		}
	}
}	


/**
 * Processes the request for deleting a multi value set
 * @param {TODO} TODO
 */
function deleteMultiValue(requestType,mvDetails,svDetails)
{
	// Check if this is a multiform contract or just an 'appreq' one.
	// We only want to show the busy screen on the whole page for 
	// normal single contract requests, as sub-pane requests will
	// show this as part of teh Fragment code.
	var multiRequest = getFormField("generalForm", "multiPane");
	
	if ( multiRequest == null )
	{
		showState("busy");
	}
		
	//if there are no overrides or all overrides have been checked
	if (processOverrides(true) == true)
	{
		// Indicate we are unloading the window and not closing it
		closingWindow = false;

		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		// Set-up parameters for a Globus command and submit it
		appreqForm.requestType.value=requestType;
		appreqForm.routineArgs.value=mvDetails + ":" + svDetails;

		// If deal ID supplied on form then Operation is POPULATE, otherwise Operation is PROCESS
		// Check if there is a transactionId input box
		appreqForm.ofsOperation.value="8o1y0";
	
		// Submit the request to the Browser Servlet
		if ( multiRequest == null )
		{
			submitDealForm();
		}
		else
		{
			doSubPaneMvSvRequest();
		}
	}
	else
	{
		if ( multiRequest == null )
		{
			showState("ready");
		}
	}
}	

/**
 * Process a new deal request
 * @param {TODO} TODO
 */
function doNewDeal()
{
	showState("busy");

	// Indicate we are unloading the window and not closing it
	closingWindow = false;

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Determine the command to get a new deal
	var app = appreqForm.application.value;
	var version = appreqForm.version.value;
	var cmdname = app + version + " I F3";

	// Record the history
  	var dataArray = {};
  	dataArray.fullapplication = app + version; 
  	if(window.BrowserHistory)
  	{
  		dataArray.windowname = windowName;
  		BrowserHistory.recordHistory("deal", dataArray);
  	}
	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value="oh1y1haCb}oh16Y";
	appreqForm.ofsOperation.value="Kb}eYXX";
	appreqForm.routineName.value="}XC6Y5C0Yty";
	appreqForm.routineArgs.value=cmdname;

	// Submit the request to the Browser Servlet
	submitDealForm();
}

/**
 * Takes the user Back to the deal application form in the same window
 * Unlock any existing record on display
 * @param {TODO} TODO
 */
function doDealApplication( app )
{
	if(!SaveWindowChanges(window))
	{
		return;
	}   
	
	showState("busy");

	// Indicate we are unloading the window and not closing it
	closingWindow = false;

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	setCurrentForm( "appreq" );
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value="oh1y1haCb}oh16Y";
	appreqForm.ofsOperation.value="Kb}eYXX";
	appreqForm.routineName.value="}XC6Y5C0Yty";
	appreqForm.routineArgs.value=app;

	// See if the record should be unlocked
	transId = appreqForm.transactionId.value;

	if ( ( transId != "" ) && ( transId != "NEW" ) )
	{
		// We need to unlock the record supplying application and record key
		appreqForm.unlock.value = app + " " + transId;
	}

	// Submit the request to the Browser Servlet
	submitDealForm();
	
}

/**
 * Process a 'HotValidate'
 * @param {TODO} TODO
 */
function doHotValidate(fieldName)
{
	var hotField = getFormField( currentForm_GLOBAL, fieldName );
	if (hotField.getAttribute("hotVal") == "Y")
	{
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		appreqForm.routineArgs.value= "HVA_" + fieldName;
		doDeal("VAL");
	}

}

/**
 * Processes a Hot Field request - i.e. validate the deal
 * @param {TODO} TODO
 */
function doHotField( _fieldName )
{
	// Excute the block only for no frames mode since frameset will not have fragments in the document.
	if(top.noFrames)
	{
		// Get the current form object to extract form name with fragment.
		var actualForm = FragmentUtil.getForm(currentForm_GLOBAL);	
		// Retrieve current fragment object.
		var currentFragment = Fragment.getCurrentFragmentName();
		// Get object of current fragment.
		var currentFragmentObject = window.document.getElementById(currentFragment);
		// Check current form exists in current fragment, otherwise return it.
		var formToGetDrillDetails = getFormObjectFromFragment(currentFragmentObject,actualForm.id);
		if (formToGetDrillDetails =="")
		{
			// When current form is null in the current fragment the control is moved to any other fragment.
			// So don't proceed further since form object is not available. 
			return;
		}
	}
	
	var hotField = getCurrentTabField( currentForm_GLOBAL,  _fieldName );
                 
     if ( tabList_GLOBAL.length > 1 && hotField.type!="radio")
     {
           var fieldList = getFormFields( currentForm_GLOBAL, _fieldName);  
                 
           for( var i = 0; i < fieldList.length; i++)
           {
                   fieldList[i].value = hotField.value;    // update all tab's field value by current tab field value if duplicate field
           }
 
     }
                 
     if (hotField.getAttribute("hot") == "Y")
     {
           processHotField( _fieldName, hotField );
     }
	 
}

/**
 * do or do not run the validation of the hot fiels contains
 * @param {TODO} TODO
 */
function processHotField( _fieldName, _hotField )
{
	// If the hot field contains "..." and is also a dropdown field, then do not
	// run validation as user is probably about to press the associated dropdown icon
	var hotFieldValue = _hotField.value;
	var dropdown = _hotField.getAttribute("dropdown");

	// Only run the validation if the hotfield has something in it, or
	// had something in it has has now been blanked out
	var hotFieldOldValue = _hotField.getAttribute("oldValue");

	if ( ( hotFieldValue != "" ) || ( ( hotFieldValue == "" ) && ( hotFieldOldValue != "" ) ) )
	{
		var filterPos = hotFieldValue.indexOf( "..." );

		if ( ( filterPos != -1 ) && ( dropdown == "Y" ) )
		{
			// Cancel the event - don't want to run validation
			if (window.event!=null)
			{
				window.event.returnValue=false;
			}
		}
		else
		{
			// Run the validation as no filter specified
			runHotFieldValidation( _fieldName );						
		}
	}
}

/**
 * Runs validation for a hot field
 * @param {TODO} TODO
 */
function runHotFieldValidation( _fieldName )
{
	showState("busy");

	// Indicate we are unloading the window and not closing it
	closingWindow = false;

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value="}rXCtKKy1eth1}6";
	appreqForm.ofsOperation.value="8o1y0";
	appreqForm.ofsFunction.value="I";

	// Set the routine args to indicate the validation was for a hot field
	// including the name of the hotfield
	appreqForm.routineArgs.value= "HOT_" + _fieldName;

	// Submit the request to the Browser Servlet
	submitDealForm();
}

/**
 * Processes a Web Validation Field request - i.e. validate the deal in the background in Java at the web server
 * @param {TODO} TODO
 */
  function doWebValidationField( _fieldName, _validationRoutine )
{
	routineName = _validationRoutine;		// The routine that validates the contract
	cmdName = _fieldName;					// The name of the field that caused the validation
	formName = currentForm_GLOBAL;					// The form we want to submit
	windowName = "iframe:validation";		// The name of the iframe we want the result to be displayed in
	requestType = "5Y8CRty10th1}6";			// The type of the request
	clearParams = false;
	
	// We want to set the command to indicate that this is a web server validation request
	// Save the old command so that we reset it for future processing
	command = getFormField( currentForm_GLOBAL, "command");
	oldCommand = command.value;
	command.value = "validation";
	
	reqType =  getFormField( currentForm_GLOBAL, "requestType" );
	oldReqType = reqType.value;
	
	// Set the windowName to the calling windowName to allow this request past the form field validation in the servlet.
	var WS_parentWindow = getFormField( currentForm_GLOBAL, "WS_parentWindow" );
	var oldWS_parentWindow = WS_parentWindow.value;
	WS_parentWindow.value = FragmentUtil.getWindowOrFragmentName();

	// Submit the request for processing in Java at the web/application server
	buildUtilityRequest( routineName, cmdName, formName, windowName, requestType, "", 0, 0, clearParams );

	// Reset the fields back to their original values.
	command.value = oldCommand;
	reqType.value = oldReqType;
	WS_parentWindow.value = oldWS_parentWindow;
}



/**
 * Check whether overrides have been accepted or not
 * @param {String} TODO
 */
function processOverrides( processingMultiValue )
{
	// No need to process overrides if doing multivalue action	
	if ( processingMultiValue == true )
	{
		//we don't care if some of the overrides have not been checked
		//the overrides will be scrapped due to the deal changing
		return true;
	}

	// See if the accept button was pressed or not
	var overridesPresent = getFieldValue( "overridesPresent");
	var overridesAccepted = getFieldValue("overridesAccepted");
	var overrideWarning = false;                  // if an override has not been accepted
	
	if ( ( overridesPresent == "YES" ) && ( overridesAccepted != "YES" ) )
	{
		overrideWarning = true;
	}
	
	return overrideWarningCheck(overrideWarning);
	
}

/**
 * todo
 */	
function processWarnings()
{
	// Only need to loop round all fields if there are warnings
	var warningsPresent = getFormFieldValue(currentForm_GLOBAL, "warningsPresent");
	if ( warningsPresent != "" )
	{
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		// We have warnings on the form so process them
		// get all of the fields in the form
		for ( var i = 0; i < appreqForm.elements.length; i++ )
		{
			var field = appreqForm.elements[i];
			// Only want to process combo boxes
			if (field.type == "select-one")
			{
			 	var name = field.name;

		 		// check to see if the combo box is a warning
			 	var pos = name.indexOf("warningChooser");
			 	if (pos==0)
			 	{
			 		var idx = field.selectedIndex;
			 		var selected = field.options[idx].value;
			 		
			 		var options = "";
			 		for ( var j = 0; j < field.options.length; j++ )
			 		{
			 			var opt = field.options[j].value;
			 			if (opt != "")
			 			{
			 				options = options + ":" + opt; 
			 			}	
			 		}
			 		
			 		var warningID = field.getAttribute("warningid");
			 		
			 		var targetField = "warningText" + name.substring(14,name.length) + ":value";
			 		
					for ( var k = 0; k < appreqForm.elements.length; k++ )
					{
						var appfield = appreqForm.elements[k];
						
						// Only want to process hidden warning fields
						if ((appfield.type == "hidden") && (appfield.name == targetField)) {
					 		if (selected == "") {
					 			appfield.value = "NOANSWER" + ":" + warningID + options;
					 		}
					 		else {
					 			appfield.value = selected + ":" + warningID + options;
					 		}
						}	
					}
				}			
			}			
		}
	}
}	

/**
 * If the overrides have not been accepted then the user will be prompted to either continue, or return to the deal to recheck overrides.
 * @param {TODO} TODO
 * @return 'true' or 'false'
 */	
function overrideWarningCheck(overrideWarning)
{

	//if one or more of the overrides is not checked, show alert and return to deal
	if(overrideWarning == true)
	{
		showState("ready");
		var response = window.confirm("Warning: Overrides must be accepted before the deal can be submitted. Click Cancel to return to the deal or OK to continue.");
		showState("busy");
		
		if (response==true)
		{
			//commit the deal
			return true;
		}
		else
		{
			//return to the deal page
			return false;
		}
	}
	//process the deal
	else 
	{
		return true;
	}
}

/**
 * Accept all overrides and commit deal
 * @param {TODO} TODO
 */	
function acceptOverrides()
{
	// Check if this is a multiform contract or just an 'appreq' one.
	var multiRequest = getFormField("generalForm", "multiPane");
	if( multiRequest != null )
	{
		//Go through all the forms in the page and accept their 
		setCurrentForm( mainAppForm_GLOBAL );
		acceptFormOverrides();
		if( appFormList_GLOBAL.length > 0 )
		{
			for(var i = 0; i < appFormList_GLOBAL.length; i++)
			{
				setCurrentForm( appFormList_GLOBAL[i] );
				acceptFormOverrides();
			}
		}
	}
	else
	{
		acceptFormOverrides(); //Just accept the 'appreq' overrides.
	}
}

/**
 * Accepts overrides on a form.
 */
 function acceptFormOverrides()
 {
 	overrideCount = 0;
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	// Update the hidden override fields on the deal form to show each is accepted
	for ( var i = 0; i < appreqForm.elements.length; i++ )
	{
		var field = appreqForm.elements[i];
		// Only want to process hidden override fields
		if (field.type == "hidden")
		{
	 		//check to see if the hidden field is an override
		 	var pos = field.name.indexOf("overrideText");

		 	if (pos==0)
		 	{	
				field.value = "YES";
				overrideCount++;
			}
		}	
	}
	//Change the colour of all the overrides
	for (var j=1;j<=overrideCount;j++)
	{
		overrideCaption = window.document.getElementById("OVE"+j);
		if( overrideCaption != null ) {
		//check for arc-ib if so dont add the styling
		var overrideClass = window.document.getElementById("isARC");
		if((overrideClass==null) ||(overrideClass==""))
		    overrideCaption.className = "overrideOff";
	} 
	} 
	appreqForm.overridesAccepted.value = "YES";	
 }
 
/**
 * Accept all overrides and commit deal
 */	
function commitOverrides()
{
	acceptOverrides();

	setCurrentForm( mainAppForm_GLOBAL );
	
	// Commit the deal with the correct function
	var lockArgs = getFormFieldValue(mainAppForm_GLOBAL, "lockArgs");
	var screenMode = getFormFieldValue(mainAppForm_GLOBAL, "screenMode");
	var func = "I";
	
	if ( lockArgs == "" )
	{
		func = screenMode;
	}
	else
	{
		func = lockArgs;
	}
	
	if ( func == "" )
	{
		func = "I";
	}
	
	doToolbar( "", func, "", "" );
}

/**
 * Change the colour of a warning if it has been acknowledged
 */
function changeWarning( _warningCaptionId, _warningComboId ) 
{
	var caption = document.getElementById( _warningCaptionId );
	var combo = document.getElementById( _warningComboId );

	var idx = combo.selectedIndex;
	var value = combo.options[idx].value;

	if ( value == "" )
	{
		caption.className = "warningUnset";
	}
	else
	{
		caption.className = "warningSet";
	}
}

/**
 * Deals with enquiries on special files (e.g. unauthorised, history, etc) from the deal form
 */
function doFileEnquiry()
{	
	// Get the operation and file combo box values
	var searchOp = document.getElementById("search").value;
	var fileOp = document.getElementById("file").value;
	var enqName = "%" + fileOp;

	// Run the Search/List enquiry
	doSearchListEnquiry( searchOp, enqName );	
}



/**
 * Deals with enquiries on special files (e.g. unauthorised, history, etc) from the deal form
 * @param {TODO} TODO
 */
function doSearchListEnquiry( searchOp, enqName )
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Check whether we want to search or just list the file contents
	if ( searchOp == "SEARCH" )
	{
		// Return an enquiry selection form
		appreqForm.requestType.value = "oh1y1haCb}oh16Y";
		appreqForm.routineName.value= "}XCY6lo1baCXYyYeh1}6";
		appreqForm.routineArgs.value = enqName;
	}
	else if ( searchOp == "LIST" )
	{
		// Return a list of the file - a full enquiry result
		appreqForm.requestType.value= "}rXCY6lo1ba";
		appreqForm.operation.value= "Y6lo1baCXYyYeh";
		appreqForm.name.value= enqName;
	}


	// Save deal window target for restoration later
	dealWindowName = appreqForm.target;

	// Create a new window for the result
	enquiryWindowName = createResultWindow( "fileEnq" + enqName, 600, 300 );
	appreqForm.target = enquiryWindowName;
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(appreqForm);

	// Reset deal target back to orginal deal window
	appreqForm.target = dealWindowName;
}


/**
 *  Sets the form fields ready for a context enquiry, and return the enquiry name minus the context enquiry id
 * @param {TODO} TODO
 * @return 'enq'
 */
function setContextEnquiryFields( enqName )
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value="}rXC}XCe}6hYqh";
	appreqForm.ofsOperation.value="K}KoythY";
	appreqForm.routineArgs.value=enqName;

	// Save deal window target for restoration later
	dealWindowName = appreqForm.target;

	// Remove the context enquiry id from the enquiry name for the window name
	// !!! TJC and also the selection criteria
	var enq = enqName;
	var enqNameIndex = enqName.indexOf("_");
	if (enqNameIndex != -1)
	{
		enq = enqName.substring( enqNameIndex + 1, enqName.length);
		enqNameIndex = enq.indexOf("_");
		if (enqNameIndex != -1)
		{
			enq = enq.substring(0,enqNameIndex);
		}		
	}
	
	return( enq );
}

/**
 *  Deals with context enquiries.
 * @param {TODO} TODO
 */ 
function doContextEnquiry( enqName )
{	
	var dropContext = '';
	//Clear WS_dropfield if any set
	updatedFormsField( "WS_dropfield", dropContext );
	var enq = setContextEnquiryFields( enqName );
	
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	// Executing the context enquiry after launching the application alone
	// Enquiry name and the action - SELECTION is passed in request to enable the display of selection screen  
	if ( appreqForm.transactionId.type == "text" )
	{
		appreqForm.enqaction.value = "XYyYeh1}6";
		appreqForm.enqname.value = enq;
	}	
	// Create a new window for the result
	var mycmd = "Y6l" + " " + enq;
	enquiryWindowName = createResultWindow( mycmd, 600, 300 );
	appreqForm.target = enquiryWindowName;

	// Set the windowName to the calling windowName to allow this request past the form field validation in the servlet.
	appreqForm.windowName.value = enquiryWindowName;
	var savedWS_parentWindow = appreqForm.WS_parentWindow.value;
	if (autoLaunch == "Y")
	{
	appreqForm.WS_parentWindow.value = FragmentUtil.getWindowOrFragmentName();
	}
	else
	{
	appreqForm.WS_parentWindow.value = window.name;
	}
	var savedWS_doResizeValue = appreqForm.WS_doResize.value;
	appreqForm.WS_doResize.value="";
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(appreqForm);

	// Reset deal target back to orginal deal window
	appreqForm.target = dealWindowName;
	appreqForm.WS_parentWindow.value = savedWS_parentWindow;
	appreqForm.WS_doResize.value = savedWS_doResizeValue;
}


/**
 *  Deals with edit functions, i.e. copy and past
 * @param {TODO} TODO
 */ 
function doEdit( clipboardFunction )
{
	if(clipboardFunction=="COPY")

	{
		doCopy();
	}
	else
	{
		doPaste(clipboardFunction);
	}
}

/**
 *  TODO
 * @param {TODO} TODO
 */ 
 function doPaste( myItem )
{
	// Indicate we are unloading the window and not closing it
	closingWindow = false;

	showState("busy");

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value = "oh1y1haCb}oh16Y";
	appreqForm.ofsOperation.value = "Kb}eYXX";
	appreqForm.routineName.value = "}XCKtXhYC0Yty";
	
	var transactionId = appreqForm.transactionId.value;
	// Change '_' to ':' as the routineArgs are separated by '_'. The server then changes ':' back to '_'.
	var previousEnqs  = appreqForm.previousEnqs.value.replace( /_/g, ":");
	var existingRecPaste = appreqForm.existRecPaste.value;   //check overwriting existing record
	
	// Update the routine args with the transaction id and the previous enqs
	appreqForm.routineArgs.value = myItem + "_" + transactionId + "_" + previousEnqs;
	if (existingRecPaste) //overwriting existing rec
    {
      appreqForm.routineArgs.value += "_"+existingRecPaste; // append the value with routine args then only t24 knows this is confirmed paste deal
    } 
	

	// Submit the request to the Browser Servlet
	submitDealForm();	
}

/**
 * Launches a new window and retrieve each deal slip specified in reportList
 * @param {TODO} TODO
 */ 
function submitPrintSlips(reportList,formName) {

	var form = FragmentUtil.getForm(formName);
	var savedTransactionId = form.transactionId.value;

	reportList = reportList.split(","); // reportList will be comma delimited
	form.requestType.value = "}rXC>YhC0YtyCXy1K";
	for (var i=0; i<reportList.length; i++)
	{
		form.transactionId.value = reportList[i];
		//IE considers null itself as an object so checked with value attribute
		if ((form.application == null && form.version== null)&&(form.application.value=="" && form.version.value==""))
		{
			var parentWin = FragmentUtil.getParentWindow();//get parent window 
			//parent window would not be set in the case if dealslips are triggered using online method 
			if (parentWin!= null)
			{
				var parentApplication = parentWin.document.getElementById("application"); //from parent window retrieve application and version
				var parentVersion = parentWin.document.getElementById("version");
			}			
			if ((parentApplication != null && parentVersion!= null)&&(parentApplication.value!="" && parentVersion.value!=""))
			{
				form.application.value = parentApplication.value;//append application and version details to the form
				form.version.value = parentVersion.value;
			}
		}
		submitNewWindowRequest(form);
	}
	form.transactionId.value = savedTransactionId;
}



/**
 * Send the deal up to the server and copy it with a description
 * @param {TODO} TODO
 */ 
function doPrintSlip()
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.ofsOperation.value="8o1y0";
	var funcval = getFormFieldValue(currentForm_GLOBAL, "screenMode"); //get the function
	var DealSlipEnable = getFormFieldValue(currentForm_GLOBAL, "enableDealSlip"); // check deal slip icon needs to be enabled
    if (funcval)    // deal slip icon enabled means set the given function otherwise hardcode to "S" function 
	{
	appreqForm.ofsFunction.value = funcval ;
	}
	else{
	appreqForm.ofsFunction.value = "I";
	}
	
	appreqForm.requestType.value="}rXCKb16hC0YtyCXy1K";

	// Set the parent window name to be that of the current window 
	updatedFormsField( "WS_parentWindow", window.name );

	submitNewWindowRequest(appreqForm);

}

/**
 * Redirects the submit response to a new window
 * @param {TODO} TODO
 */
function submitNewWindowRequest(formObject)
{
	// Save the window name on the form for server locking purposes
	formObject.windowName.value = window.name;

	// Save deal window target for restoration later
	dealWindowName = formObject.target;

	// Create a new window for the result
	enquiryWindowName = createResultWindow( "NEW", 900, 600 );
	formObject.target = enquiryWindowName;
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(formObject);

	// Reset deal target back to orginal deal window
	formObject.target = dealWindowName;
}



/**
 * Send the deal up to the server and copy it with a description
 * @return TODO
 */
function doCopy()
{
	var myDesc = "PASTE";
	// Might allow multiples in the future - prompt("Copy this deal to GLOBUS clipboard as...","enter description");
	if(myDesc=="")
	{
		return;
	}

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Set-up parameters for a Globus command and submit it
	appreqForm.requestType.value="}rXC}XCe}Ka";
	appreqForm.ofsOperation.value="K}KoythY";
	appreqForm.routineArgs.value=myDesc;
	appreqForm.ofsFunction.value = getFormFieldValue( currentForm_GLOBAL, "screenMode" );
	appreqForm.windowName.value = window.name;

	// Save deal window target for restoration later
	dealWindowName = appreqForm.target;

	// Create a new window for the result
	enquiryWindowName = createResultWindow( "NEW", 300, 150 );
	appreqForm.target = enquiryWindowName;
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(appreqForm);

	// Reset deal target back to orginal deal window
	appreqForm.target = dealWindowName;
}

/**
 * Send the deal up to the server and copy it with a description
 */
function doIdAutoLaunch()
{
	// Run ID auto launch enquiries - but only if they will not be done by setting
	// a new task at the server
	
	var enqids = getFormFieldValue(currentForm_GLOBAL, "idautolaunch");

	if ((enqids!= null) && (enqids!=""))
	{
		var runEnquiry = "Y";
		
		// Check if the enquiry is going to be run from the server
		var serverAutos = getFormFieldValue(currentForm_GLOBAL, "idautosServer");
		
		if ((serverAutos!= null) && (serverAutos!=""))
		{
			if ( serverAutos == "Y" )
			{
				runEnquiry = "N";
			}
		}
	
		if ( runEnquiry == "Y" )
		{
			fieldName = "id";
			setAutoField("true");
			
			doFieldContextEnquiry(enqids, fieldName);
		}
	}
}

/**
 * Deals with field context enquiries (auto-launch enquiries).
 * @param {TODO} TODO
 */
function doFieldContextEnquiry(enqName, fieldName)
{
	if (autoLaunch == "Y")
	{
		// Substitute the field value in the enquiry data
		// Enquiry data in the form of repeated nodes -  <ContextId>_<EnqName>_<FieldName>_<FieldValue>:
		
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
		
		//Set the variable enquiryType to indicate its a context enquiry.
		enquiryType = "e}6hYqhCY6lo1ba";
		
		// Set the global variable for auto launch context enquiries
		var autolaunchenq = appreqForm.contextenqdisplay;
		if (autolaunchenq != "")
		{
			autoLaunch_GLOBAL = autolaunchenq.value;
		}
		// first clear the enrichment
		enriParagraph = "enri_" + fieldName;
		clearEnrichment(enriParagraph);

		// Get the field value
		if(fieldName=="id") {
			var transId = appreqForm.transactionId;
			fieldValue = transId.value;
		} else {	
			fieldValue = getCurrentTabFieldValue( currentForm_GLOBAL, fieldName );
		}
		// Only run enquiry if the field has a value
		if ( fieldValue != "" )
		{
			// Add field value to enquiry details for each auto-launch enquiry
			if ( enqName != "" )
			{
				enqArgs = "";
				moreEnqs = true;
				startPos = 0;
	
				while ( moreEnqs == true )
				{
					var colonPos = enqName.indexOf( ":", startPos );
					var enq = "";
					if ( colonPos != -1 )
					{
						enq = enqName.substring( startPos, colonPos );
					}
					else
					{
						enq = enqName.substring( startPos, enqName.length );
					}
		
					if ( enq == "" )
					{
						moreEnqs = false;
					}
					else
					{
						// Process the individual field values
						// Context Id
						underscorePos = enq.indexOf( "_" );
						var contextId = enq.substring( 0, underscorePos );
						newStartPos = underscorePos + 1;
		
						// Enquiry Name
						underscorePos = enq.indexOf( "_", newStartPos);
						var enqId = enq.substring( newStartPos, underscorePos );
						newStartPos = underscorePos + 1;
	
						// Field Name
						underscorePos = enq.indexOf( "_", newStartPos );
						var fieldId = "";
						if ( underscorePos == -1 )
						{
							fieldId = enq.substring( newStartPos, enq.length );
						}
						else
						{
							fieldId = enq.substring( newStartPos, underscorePos );
						}
	
						var enqDetails = contextId + "_" + enqId + "_" + fieldId + "_" + fieldValue;
	
	
						if ( colonPos == -1 )
						{
							moreEnqs = false;
						}
						else
						{
							startPos = colonPos + 1;
						}
					}
	
					// Run this context enquiry
					doContextEnquiry( enqDetails );		
				}
			}
	
		}
	}
}



/**
 * Submit the deal to the Browser Servlet
 */
function submitDealForm()
{
	// Set the cursor to an hourglass
	setCursor( currentForm_GLOBAL, "wait" );
	setCursor( "mainmenu", "wait" );
	
	hideDeal();
	showState("busy");

	// Move Processing div back to middle of the screen (for Netscape)
	setLeftPosition( "processingPage", 260 );

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Save the window name on the form for server locking purposes
	appreqForm.windowName.value = FragmentUtil.getWindowOrFragmentName();
	
	// replace all for versions set in composite screen to replace the parent cos screen on commit request
	if ( FragmentUtil.isCompositeScreen() ) 
	{
		var applicationName = getFormFieldValue(currentForm_GLOBAL, "application");
		var applnTarget = getCompositeScreenTarget( applicationName );
		if ( applnTarget != "" && applnTarget != "NEW" )
		{
			appreqForm.target = applnTarget;
		}
	}
	
	// Check if the id supplied is 'A' or 'I'. In this case we should add a '.' to the id
	// as the server gets its nickers in a twist if we give a function character for an id. 
	// In turn the application should remove the '.' from the id, in CHECK.ID part of the template.
	var transactionId = getFormFieldValue( currentForm_GLOBAL, "transactionId");
	transactionId = validateTransId( transactionId);
	setFormFieldValue( currentForm_GLOBAL, "transactionId", transactionId);

	// Check which deal fields have changed
	checkChangedFields();

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(appreqForm);

	// Clear the unlock flag now request has gone to server - check form fields still there if replacing current element
    if (appreqForm.unlock) {
    	appreqForm.unlock.value = "";
    }
}

/**
 * Add a '.' is the transaction id is a reserved function keyword such as: A or I (for now, could add more later).
 * @param (String)transactionId The transaction id to be validated.
 * @return (String) The validated transaction id.
 */
function validateTransId( transactionId)
{
   	if( validFunctionCheck( transactionId))
   	{
        // If a function character then add the '.'
    	transactionId = "." + transactionId;    
    }
	return transactionId;
}

/**
 * Hide the deal elements - now just disappearing the outer deal container
 */
function hideDeal() {
	// if multi-pane, then hide the top-level screen sections (if not present, then no-op)
	hideObject("MainApplication");
	hideObject("topPart");
	hideObject("BottomPart");
	// if no multi-pane, then hide the main deal elements - toolbar, messages and deal tables
	hideObject("divHeader");
}

/**
 * Toggles whether to display the deal menu or not
 */ 
function toggleDealMenu()
{
	var state = '';
	//get the current element shown
	var toggle = getFormField( currentForm_GLOBAL, "toggle");

    //If we are loading the screen for the first time 
    //then showorhide will be populated.  If we're not,
    //then it should be blank.
	if (showorhide!=''){
		state = showorhide;
	}
	else{
		state = toggle.getAttribute("toggleState");
	}

	// Check the direction of the document so we know which way to move the objects
	var dir = window.document.dir;
	var moveDirection = "left";
	
	if ( ( dir == "rtl" ) || ( dir == "RTL" ) )
	{
		moveDirection = "right";
	}

	toggleObject( toggle, state);

	FragmentUtil.setDisplay("divMenu", state == "SHOW", moveDirection);
	
	//reset this to '', we only want this to be set on init
	showorhide = '';
}


/**
 * decides what gif to show and assigns a state to it 
 * used in conjunction with hiding or showing the menu
 * @param (TODO)TODO
 */
function toggleObject(toggle, state)
{
// Catch all check for null toggle objects!
	if (toggle==null){
		return;
	}
	var skin = getSkin();

	if (state=="SHOW")
	{
		toggle.src = "../plaf/images/"+skin+"/tools/left.gif";
		toggle.setAttribute("toggleState","HIDE");
	} 
	else if (state=="HIDE")
	{
		toggle.src = "../plaf/images/"+skin+"/tools/right.gif";
		toggle.setAttribute("toggleState","SHOW");
	}

}

/**
 * Move all the tab tables on a deal form to a particular left position
 * @param (TODO)TODO
 */
function moveDealTabs( pos )
{
	var tables = document.getElementsByTagName("table");
	activeTableNumber = 0;
	activeTableCount = 0;
	numberOfTables = tables.length;

	var tableCount = 0;
	// Go through each table....
	for (tableCount = 0; tableCount < numberOfTables; tableCount++)
	{
		tableName = tables[tableCount].getAttribute("name");
		tableId = tables[tableCount].getAttribute("id");
		
		
		// If it is a tab table then move it
		if (tableName == "visibletab" || tableName == "maintab" )
		{
		
			setLeftPosition( tableId, pos );
		}
		
	}
}

/**
 * TODO
 * @param (TODO)TODO
 */
function doToolbardocommand(action, prompt, promptText, type )
{
	doFunction = '';
	
	// Assign correct form name in noframes mode for tools request.
    var browser = navigator.appName.substring ( 0, 9 );
    if (browser=="Microsoft")
    {
     getCurrentNoframeFormName();
    }
	
	if (prompt == "YES")
	{
		promptResponse = confirm(promptText);
		if (promptResponse == true)
		{
			doFunction = "YES";
		}
	}
	else
	{
		doFunction = "YES";
	}
	if (doFunction == "YES")
	{
		if (type == "DO.DEAL")
		{
			setnoNewWindow("1");
		}
		docommand(action);
	}

}
/**
 * Sends multi T24 requests in one transaction. Go through the list of available app forms 
 * and collect the parameters.
 * @param (TODO) TODO
 */
 function doMultiRequest(ofsFunction)
 {
	var tabbed_panelist = document.getElementsByTagName("span");
	 for ( var i = 0; i < tabbed_panelist.length; i++)
	 {
		paneClass = tabbed_panelist[i].getAttribute("classname");
		if (isInternetExplorer())
    	{
		    paneClass = tabbed_panelist[i].classname;
	    }
		if (paneClass == "error-tab")
		{
			var tabno =parseInt(tabbed_panelist[i].getAttribute("id").substring(7));
			var temppro = tabbed_panelist[i].getAttribute("propertyname");
			temppro1 = temppro.split("-");
			temppro1 = temppro1[1];
			remTabMenu(tabno,temppro1,temppro);	//To remove the error tabs while validating/commiting.
		}
	 }
	showState("busy");
	hideDeal();
 	var formatedAppList = mainAppForm_GLOBAL;
 	var newElement = null;
 	// Indicate we are unloading the window and not closing it
	closingWindow = false;
 	// Get the multiPane form which we use to submit the whole multi request.
	multiPaneForm_GLOBAL = FragmentUtil.getForm("multiPane");
	//Remove child elements if any before rebuilding multiPaneForm_GLOBAL
	if ( multiPaneForm_GLOBAL.hasChildNodes() )
		{
			while ( multiPaneForm_GLOBAL.childNodes.length >= 1 )
    		{
      		  multiPaneForm_GLOBAL.removeChild( multiPaneForm_GLOBAL.firstChild );       
    		}
		}
	// Need this parameter for the servlet.
	addFormElement("command", "globusCommand");
	// Add the webserver fields with WS_ prefix at the top only. The
	// rest of the forms don't need this fields.
	addWSFields();
	// Append the main form
	appendForm(mainAppForm_GLOBAL, ofsFunction);
	
	
	// Go through the rest of the forms and append them as well.
	for( var i = 0; i < appFormList_GLOBAL.length; i++ )
	{
		appendForm(appFormList_GLOBAL[i], ofsFunction);
		// Build app list string that Browser Servlet expects for multi requests.
	  	if (appFormList_GLOBAL[i].match('~') != null)
	  	{
	  		var tempFormName = appFormList_GLOBAL[i].split("~");
	  		formatedAppList += ":" + tempFormName[2];
	  			
	  	}
	  	else
	  	{
		       formatedAppList += ":" + appFormList_GLOBAL[i];
              	}
		
	}
	// Add the list of applications to be processed.
	addFormElement("MultiPaneAppList", formatedAppList);

	// Submit the multi form useing submit deal 
	FragmentUtil.submitForm(multiPaneForm_GLOBAL);
}
/**
 * Add the WS fields to the multiPane form.
 */
 function addWSFields()
 {
 	// Get 'appreq' object. Top application.
	var formObj = FragmentUtil.getForm("appreq");
	var found = 0;
	// Update the hidden override fields on the deal form to show each is accepted
	for ( var i = 0; i < formObj.elements.length; i++ )
	{
		var field = formObj.elements[i];
		var pos = field.name.indexOf("WS_");
	 	if (pos==0)	 // Does it have a "WS_" prefix.
	 	{	
	 		// Add the webserver field to multipane.
			var elms = multiPaneForm_GLOBAL.elements;
            found = 0;
            for (var k = 0; k < elms.length; k++)
            {
				// Check if this is a WS_ field, is so don't add it.
				if (elms[k].name == field.name)
				{
					multiPaneForm_GLOBAL.elements[k].value=field.value;
					found = 1;  
				}
			}
			if (found == 0)
			{
				addFormElement(field.name, field.value);
			}
		}	
	}
 }
/**
 * Appends the given form to the multiPane form.
 * @param (TODO) TODO
 */
function appendForm(formName, ofsFunction)
{
	var elementName = new String();
	var elementValue = new String();
	var found =0;
	var newElement = null;
	
	// Check if this form is a see mode form. If so build a see mode request
	// and add it to the multi-pane form.
	var lockArgs = getFormFieldValue(formName, "lockArgs");
	var screenMode = getFormFieldValue(formName, "screenMode");
	if ( ( lockArgs == BrowserFunction.See ) || ( ( screenMode == BrowserFunction.See ) && ( lockArgs == "" ) ) )
	{
		// Build the see mode function and append it to multiPane form.
		appendSeeModeRequest(formName);
	}
	else
	{
		// Set actions on this form.
		setActionsOnForm(formName, ofsFunction);
	  	// Get formName object.
	  	if (formName.match('~') != null)
	  	{
	  		var tempFormName = formName.split("~");
	  		formName = tempFormName[2];
	  	}
	  	var formObj = FragmentUtil.getForm(formName);
	  	// Loop round all the fields in the specified form
		var elems = formObj.elements;

		for (var i = 0; i < elems.length; i++) 
		{
			// Check if this is a WS_ field, is so don't add it.
			var wsName = elems[i].name + "";
	 		if ( wsName.indexOf("WS_") == 0 )	 // Does it have a "WS_" prefix.
	 		{
	 			continue;
	 		}
			elementName = formName + ":" + elems[i].name;
				
			if ( elems[i].type == "radio" )
			{
				// Just ignore as radios have a hidden field behind them.
				continue;				
			}
			else
			{
				elementValue = elems[i].value;
			}
			
			// Only add if element has a name.
			// add only when the element is not present in the form, otherwise just update the value.
			if(elementName != "" & elementName != null & elementName != "undefined")
			{
				found =0;           
               	var multipanefield = getCurrentTabField( multiPaneForm_GLOBAL.name, elementName); 
               	if(multipanefield != null)
               	{              
					multipanefield.value=elementValue;
                   	found=1; 
               	}
           	}
           	if (found==0)
           	{
	    	    addFormElement(elementName, elementValue);
            }
		}
	}	
}

/**
 * Add an element to multipane form
 * @param (TODO) TODO
 */
 function addFormElement(elementName, elementValue)
 {
	// Create an input type element, set its name and value.
	var newElement = document.createElement("input");
	newElement.name = elementName;
	newElement.value = elementValue;
	newElement.id = elementName;
	multiPaneForm_GLOBAL.appendChild(newElement);	
 }
 
/**
 * Appends a see mode request to multiPane. This is used for forms that are only being viewed.
 * When we send the multirequest we want the see mode forms to come back in see mode.
 * @param (TODO) TODO
 */
 function appendSeeModeRequest(formName)
 {
 	var application = getFormFieldValue(formName, "application");
 	var version = getFormFieldValue(formName, "version");
 	var id = getFormFieldValue(formName, "transactionId");	
 	var title = getFormFieldValue(formName, "title");
	var tabEnri = getFormFieldValue(formName, "tabEnri");
	var expandTab = getFormFieldValue(formName, "expandableTab");
	var groupTab = getFormFieldValue(formName, "addGroupTab");
	
	addFormElement( formName + ":requestType", "oh1y1haCb}oh16Y");
	addFormElement( formName + ":ofsFunction", BrowserFunction.See);
	addFormElement( formName + ":routineName", "}XC6Y5C0Yty");
	addFormElement( formName + ":routineArgs", application + version + " " + "S" + " " + id+"|"+groupTab+"|"+expandTab+"|"+tabEnri);
	addFormElement( formName + ":title", title );
 }
 
/**
 * Depending on what action the user clicked on; we
 * need to set attributes on each form to let the server
 * know if we are doing a commit, validate or an authorise.
 * @param (TODO) TODO
 */
 function setActionsOnForm(formName, ofsFunction)
 {
 	// Get formName object.
  	var formObj = FragmentUtil.getForm(formName);
  	// Set the actions on the form according to ofsFunction.
  	formObj.windowName.value = FragmentUtil.getWindowOrFragmentName();
  	formObj.requestType.value="}rXCtKKy1eth1}6";
  	formObj.GTSControl.value="";
  	formObj.ofsFunction.value= ofsFunction;
  	//
    switch (ofsFunction)
    {
    	case BrowserFunction.See:
    		//
    		break;
        case BrowserFunction.Authorise:
        case BrowserFunction.Input:
        	//
        	formObj.ofsOperation.value="Kb}eYXX";	 
        	break;
        case BrowserFunction.Validate:
        	//
            formObj.ofsOperation.value="Rty10thY";
            formObj.ofsFunction.value= "I";
            break;
        case BrowserFunction.Hold:
        	//
        	formObj.ofsOperation.value="Kb}eYXX";
			formObj.GTSControl.value="4";	// Force record to HOLD - '4' is currently arbitrary
			formObj.ofsFunction.value="I";
			break;		            
        default : break;
    }
    // Set the current form and check which fields have changed.
    setCurrentForm( formName );
    // Process the any possible warnings on the screen.
	processWarnings();
    // Check which deal fields have changed
	checkChangedFields();
 }
 
/**
 * Process a toolbar button press
 * @param (TODO) TODO
 * @return TODO
 */ 
function doToolbar( action, ofsFunction, prompt, promptText )
{
	doFunction = '';
	autoLaunch = 'NO'; /* autoLaunch variable is resetted. (Defect:853067) */
	var dropval = '';
	//Popup message to be displayed before deleting the record
	var retval = "true";
	var delMsgDisplayed = getFormField("generalForm", "WS_delMsgDisplayed");
	// when warnings present,change commit action to related functions
	var warningsPresent = getFormFieldValue(currentForm_GLOBAL, "warningsPresent");
	if (warningsPresent != "")
	{
		var screenMode = getFormFieldValue(currentForm_GLOBAL, "screenMode");
		if (ofsFunction == "I" && screenMode != "I")
		{
			ofsFunction = screenMode ;
		}
	}
	/**
  * check whether we are going to perform delete function and also should not display the delete confirm msg.
  */ 
	if(ofsFunction == "D" && delMsgDisplayed.value != 'Yes')             
	{
		var Delmsgobj =  document.getElementById("RecordDelText");
		var delmsg = Delmsgobj.value;
		retval =  confirm(delmsg);
		if (!retval)
		{
			return;
		}
		else
		{
		updatedFormsField( "WS_delMsgDisplayed", "Yes" );
		}
	}
	//update WS_delMsgDisplayed value to null ,when we are not perfoming "D" function
	if(ofsFunction != "D")
	{
	   updatedFormsField( "WS_delMsgDisplayed", "" );
	}
	//Clear WS_dropfield if any set
	updatedFormsField( "WS_dropfield", dropval );
	// Check if this request should be a multiple request for AA type applications.
	var multiRequest = getFormField("generalForm", "multiPane");
	
	if( multiRequest != null & (ofsFunction == "I" | ofsFunction == "VAL" | ofsFunction == "A" | ofsFunction == "D" | ofsFunction == "R" | ofsFunction == "H"  | ofsFunction == "HLD") )
	{
		doMultiRequest(ofsFunction);
		return;
	}

	if (prompt == "YES")
	{
		promptResponse = confirm(promptText);
		if (promptResponse == true)
		{
			doFunction = "YES";
		}
	}
	else
	{
		doFunction = "YES";
	}

	if (doFunction == "YES")
	{
		var FormObject = FragmentUtil.getForm("appreq");	//To check whether it is appEnq or not
		var appEnqFlag = false;
		if(FormObject.appEnq)
		{
			appEnqFlag = true;	//Itsa an application enquiry.
		}
		// If we dont have a form speicified then use appreq.
		if( currentForm_GLOBAL == "" || appEnqFlag )
		{
			currentForm_GLOBAL = "appreq";
		}
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		// Set target of the toolbar button for server processing
		appreqForm.toolbarTarget.value = action;

		// Process the deal - setting the function to Input if not supplied
		if ( ofsFunction == "" )
		{
			doDeal( "I" );
		}
		else
		{
			doDeal( ofsFunction );
		}
	}
}

/**
 * Called when a check box is clicked - toggle the state of it
 * @param (TODO) TODO
 */ 
function toggleCheckBox(thisEvent)
{
	var thisField = thisEvent.target || thisEvent.srcElement;
	var checkboxName = thisField.getAttribute("toggleFieldName");
	
	// Check box is named "CheckBox:<fieldName>" and a hidden field
	// contains the real value to send to the server with a name "<fieldName>"

	// Get the check box object and the values for when it is checked and unchecked
	var checkbox = getFormField( currentForm_GLOBAL, "CheckBox:" + checkboxName );
	var hiddenCheckbox = getFormField( currentForm_GLOBAL, checkboxName );
	
	// Set the hidden field accordingly
	if ( checkbox.checked == true )
	{
		//for showing other language enrichment if exist while clicking on checkbox
		checkboxcap = checkbox.getAttribute("checkedCap");
		checkbox.value = checkbox.getAttribute("checkedValue");
		hiddenCheckbox.value = checkbox.getAttribute("checkedValue");
		//IE only fires the onchange event when the element loses focus
		checkbox.blur();  
        checkbox.focus();
	}
	else
	{
		checkboxcap = checkbox.getAttribute("uncheckedValue");
		checkbox.value = checkbox.getAttribute("uncheckedValue");
		hiddenCheckbox.value = checkbox.getAttribute("uncheckedValue");
		//IE only fires the onchange event when the element loses focus
		checkbox.blur();  
        checkbox.focus();
	}
	// Set the checkbox enrichment, if an enrichment field exists.
	if (checkboxcap!="")
		{
			setEnrichment( checkboxName, checkboxcap);
		}
		else
		{
			setEnrichment( checkboxName, checkbox.value);
		}
}

/**
 * Called when a toggle button is clicked - toggle the text on it
 * @param (TODO) TODO
 */ 
function toggleButton(thisEvent)
{
	var thisField = thisEvent.target || thisEvent.srcElement;
	var buttonName = thisField.getAttribute("buttonNameFieldName");
	
	// Toggle button is named "ToggleButton:<fieldName>" and a hidden field
	// contains the real value to send to the server with a name "<fieldName>"

	// Get the button object and the values for when it is selected and unselected
	button = getFormField( currentForm_GLOBAL, "ToggleButton:" + buttonName );
	hiddenButton = getFormField( currentForm_GLOBAL, buttonName );
	buttonValue = button.value;
	buttonSelected = button.selectedValue;
	buttonUnselected = button.unselectedValue;

	// Toggle the button value/text and set the hidden field accordingly
	if ( buttonValue == buttonSelected )
	{
		button.value = buttonUnselected;
		hiddenButton.value = buttonUnselected;
	}
	else
	{
		button.value = buttonSelected;
		hiddenButton.value = buttonSelected;
	}
}

/**
 * Do final window close/replace behaviour immediately prior to window being disappeared by the browser
 */
function closeDealWindow() { 

	// If we are closing the window then unlock the current deal if locked
	if ( closingWindow ) {
		if (top.noFrames)
		{
			unlockNoFramesDeal();
		}
		else
		{
			unlockDeal();
		}
	}
}

/**
 * get the window position while we can!!
 */
function beforeCloseDealWindow() {
	// IMPORTANT NOTE: onbeforeunload does not get fired for frames in IE client-side transformation
	// See http://support.microsoft.com/kb/328807

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Save the window positions in the routineArgs field if not in a composite screen
	if ( ! FragmentUtil.isCompositeScreen() ) {
		var applicationName = getFormFieldValue(currentForm_GLOBAL, "application");
		var versionName = getFormFieldValue(currentForm_GLOBAL, "version");
		// add the resize flag if resizedone.
		var wincoord = getWindowCoordinates();
		if(resizedone)
		{
			appreqForm.routineArgs.value = applicationName + ":" + versionName + ":" + wincoord + ":" +resizedone;
		}
		else
		{
		  	appreqForm.routineArgs.value = applicationName + ":" + versionName + ":" + wincoord;

		}
		
	}
	else {
		appreqForm.routineArgs.value = "";
	}
}
/**
 * Builds the unlock command for a multi deal unlock.
 */
 function getUnlockCommand()
 {
 	var unlockCommand = "";
 	var unlockStatus = "";
 	var app = "";
 	var transId = "";
 	var lockDateTime = "";
 
 	// Save the current form so we can go back to it later
	var savedCurrentForm = currentForm_GLOBAL;
	
 	setCurrentForm( mainAppForm_GLOBAL );
 	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
 	
 	unlockStatus = appreqForm.unlock.value;
	app = getFormFieldValue(currentForm_GLOBAL, "application");
	transId = getFormFieldValue(currentForm_GLOBAL, "transactionId");

	if ( ( unlockStatus != "NO.UNLOCK" ) && ( app && transId ) )
	{
		lockDateTime = getFormFieldValue(currentForm_GLOBAL, "lockDateTime");
		unlockCommand = app + " " + transId + ' ' + lockDateTime;
	}
 	
 	for(var i = 0; i < appFormList_GLOBAL.length; i++)
 	{
 		setCurrentForm( appFormList_GLOBAL[i] );
 		appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
 		//
 		unlockStatus = appreqForm.unlock.value;
		app = getFormFieldValue(currentForm_GLOBAL, "application");
		transId = getFormFieldValue(currentForm_GLOBAL, "transactionId");
		if ( ( unlockStatus != "NO.UNLOCK" ) && ( app && transId ) )
		{
			lockDateTime = getFormFieldValue(currentForm_GLOBAL, "lockDateTime");
			if(unlockCommand == "")
			{
				unlockCommand += app + " " + transId + " " + lockDateTime;
			}
			else
			{
				unlockCommand += ":" + app + " " + transId + " " + lockDateTime;	
			}
			
		}
 	}
 	
 	// Reset the current form back to what it was
 	setCurrentForm( savedCurrentForm );
 	 
 	return unlockCommand;
 }
 
/**
 * Send request to server to unlock the record, request submitted in an invisible window
 */
function unlockDeal()
{

	// Only save window position if we're not in a Composite Screen - routineArgs set up in beforeUnloadWindow accordingly
	// Unlock deals even if in a Composite Screen
	// Logoff if we're the main composite screen

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

 	// Only unlock the deal if it is not marked as "NO.UNLOCK" and 
 	//there is a contract in the window
	var unlockStatus = appreqForm.unlock.value;
	var app = getFormFieldValue(currentForm_GLOBAL, "application");
	var transId = getFormFieldValue(currentForm_GLOBAL, "transactionId");

	if ( ( unlockStatus != "NO.UNLOCK" ) && app )
	{
		var reqType = "6}CbYloYXh";
		var routineArgs = getFormFieldValue(currentForm_GLOBAL, "routineArgs");
		var closing = "Y";
		var title = "Unload";
		
		// Check if we are a multi pane contract, if so then do multi unlock.
		var unlock = "";
		
		if ( transId )
		{
			var lockDateTime = getFormFieldValue(currentForm_GLOBAL, "lockDateTime");
			var multiRequest = getFormField("generalForm", "multiPane");
			if(multiRequest != null)
			{
				unlock = getUnlockCommand();
			}
			else
			{
				unlock = app + " " + transId + ' ' + lockDateTime;	
			}
			
			unlock = replaceAll(unlock, "%", "%25");
		}
		
		var pwprocessid = getFormFieldValue(currentForm_GLOBAL, "pwprocessid");
		// Escape any % characters (e.g. enquiry names)
		var args = replaceAll(routineArgs, "%", "%25");
		
		// Submit the unlock request
		popupHiddenSubmitWindow( reqType, args, unlock, closing, title, pwprocessid );
	}
	else
	{
		appreqForm.unlock.value = "";
	}
}

/**
 *  Sets the enrichment of a given field.
 * @param (TODO) TODO
 */
function setEnrichment( _fieldName, _enrichment)
{
	// If an enrichment field exists for this field set it's value too
	var enriFieldName = _fieldName;
	enriFieldName = enriFieldName.replace("fieldName:", "enri_");
	//
	var enriObject = getTabEnrichmentSpan(currentForm_GLOBAL, enriFieldName );
	
	// Check if we have a enrichment field with the correct name
	if ( enriObject != null )
	{	
		if (isOpera())
		{		
			enriObject.innerText = _enrichment;
		}
		else
		{
			enriObject.innerHTML = _enrichment;
		}
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
}

/**
 * This function clears a given enrichment
 * @param (TODO) TODO
 */
function clearEnrichment(enri)
{
	var enriField = "";
	//Is it a string
	if (typeof(enri) == String)
	{
		enriField = enri;
	}
	// Is it a event object.
	if (typeof(enri) == Object)
	{
		enriField = enri.target.enriFieldName || enri.srcElement.enriFieldName;	
	}

	if( enriField != "" )
	{
		if (enriField.substring(0,5) != "enri_" )
		{
			// Find the field in case the multi/sub-value numbers differ
			var fieldName = enri;
		
			if (enriField.substring(0,10) != "fieldName:" )
			{
				fieldName = "fieldName:" + fieldName;
			}
		
			fieldName = getBrowserFieldName( fieldName );
			enriField = "enri_" + fieldName.substring(10, fieldName.length);
		}

		setParaValue( enriField, "" );
					
	}
	
}

/**
 * Used in conjunction with client specific style sheets to retrieve contract data
 */
function retrieveContractData()
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

		if(appreqForm.clientStyleSheet.value != "")
		{
			transactionId = appreqForm.transactionId.value;
		
			savedVersion = appreqForm.version.value;
			savedRoutineArgs = appreqForm.routineArgs.value;
		
			appreqForm.ofsOperation.value="8o1y0";
			appreqForm.ofsFunction.value="I";
		
			//we want all of the fields back for the application so blank the version
			appreqForm.version.value = "";
		
			buildUtilityRequest("","1",currentForm_GLOBAL,"","}rXCtKKy1eth1}6","Client Soecific Print","","",false);

			appreqForm.version.value = savedVersion;
			appreqForm.routineArgs.value = savedRoutineArgs;
			appreqForm.clientStyleSheet.value = "";
		}
		
		
}


/**
 * This is used by dropdowns to disable hotfields from launching
 * automatically after being changed.  On selecting a dropdown, the code 
 * will pass in the command to disable the hotfield.
 * The hotfield will be enabled again on receiving focus.
 * @param (TODO) TODO
 */
function setHotField(status, field)
{
	hotField = getFormField( currentForm_GLOBAL, field);

	if ((hotField != null)&&(hotField.hot != undefined))
	{
		if (status=="true")
		{
			//enable the hotfield
			hotField.hot = "Y";
		}
		else if (status == "false")
		{
			//disable the hotfield       
			hotField.hot = "N";          
			resetHotField_Global = field;
		}
	}	
}

/**
 * Same idea as setHotField above it is applied to auto launch field
 * @param (TODO) TODO
 */
function setAutoField(status)
{
	if (status=="true")
	{
		//enable the auto launch field
		autoLaunch = "Y";
	}
	else if (status == "false")
	{
		//disable the auto launchfield
		autoLaunch = "N";
	}
}

/**
 * Check which deal fields have changed since the last trip to 
 * the server and update the hidden field accordingly
 * Any fields that had an error against them should also be marked as changed
 */
function checkChangedFields()
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	//Get the flag to identify pasted deal
	var pastedDeal = appreqForm.PastedDeal.value;
	var changedFields = '';
	// Loop round all the deal fields checking for changes
	for ( var i = 0; i < appreqForm.elements.length; i++ )
	{

		var fieldObject = appreqForm.elements[i];
		var oldValue = fieldObject.getAttribute("oldValue");
		var newValue = fieldObject.value;
		var fieldInError = fieldObject.getAttribute("fieldError");
		// Only process if this element has a name attribute.
		if( fieldObject.name == null)
		{
			continue;
		}
		
		//Fire fox considers "\r\n" as "\n"
		if((isFirefox()) && (oldValue != null) && (fieldObject.type == "textarea")&& (newValue!=null))
		{
			var s = oldValue.indexOf('\r'); //search carriage return char
	        if (s != -1)                    //carriage return char found
	        {   
              newValue = replaceAll(newValue,"\n","\r\n");//replace newline with carriage return and newline
            }
			
        }
        
        // In case of tomcat installed in UNIX AIX machine IE couldn't able to distinguish old value and new value, even though
 		// same value persists. If no carriage return found in old value, clear all in new value too. 
 		if ((!isFirefox()) && fieldObject.type == "textarea" && (oldValue!=null)&&(newValue!=null))
 		{
 			// Check for carriage return character.
 		 	var carriageReturn = oldValue.indexOf('\r');
 		 	// If not found then replace all occurances of carriage return with empty char.
 			if (carriageReturn == -1)
 			{
 				newValue = replaceAll(newValue,"\r","");
 			}
 		}

		// Only want to process fields with a name that starts with "fieldName:"
		var pos = fieldObject.name.indexOf("fieldName:");

	 	if ( pos == 0 ) 
	 	{
			if ((pastedDeal) && newValue)
			{
				changedFields = appreqForm.changedFields.value;
				appreqForm.changedFields.value = changedFields + " " + fieldObject.name;
				continue;
			}
	 		// Special case for radio buttons.
	 		if( fieldObject.type == "radio")
	 		{
	 			var isChecked = fieldObject.getAttribute( "checked");
	 			// Are we IE? If so, convert true/false literals into strings!
	 			if( !isFirefox())
	 			{
	 				isChecked += "";
	 			}
	 			else
	 			{
	 				if( isChecked == null)
	 				{
	 					isChecked = "false";
	 				}
	 			}
	 			// Add to changed fields if the radio was checked but is not anymore or vise versa.
	 			if( isChecked != oldValue)
	 			{
	 				//alert( isChecked + " " + oldValue)
	 				changedFields = appreqForm.changedFields.value;
	 				if( changedFields )
	 				{
	 					// Only add if the fieldObject has not been added yet.
	 					if( changedFields.indexOf( fieldObject.name) == -1 )
	 					{
	 						appreqForm.changedFields.value = changedFields + " " + fieldObject.name;
	 					}
	 				}
	 				else
	 				{
	 					appreqForm.changedFields.value = fieldObject.name;
	 				}
	 			}		
	 			// Continue to next fieldObject at this point. 
	 			continue;
	 		}
	 		
	 		if ( ( oldValue != newValue ) || ( fieldInError == "Y" ) )
		 	{	
		 		var changedFields = appreqForm.changedFields.value;
		 		
		 		if ( changedFields )
		 		{
					appreqForm.changedFields.value = changedFields + " " + fieldObject.name;
				}
				else
				{
					appreqForm.changedFields.value = fieldObject.name;
				}
			}
		}
	}
}


/**
 * Processe a field change event
 * @param (TODO) TODO
 */
function doFieldChangeEvent(thisEvent)
{
	// Parameters :-
	// _fieldName	- The name of the field the event occurred on
	// _routine		- The name of the validation routine for a web validate field
	// _autoEnq		- The name of an auto launch enquiries for the field
	// isHot		- Whether the field is a Hot field or not
	// isHotVal		- Whether the field is a Hot Validate field or not
	// isWebVal		- Whether the field is a Web Validate field or not
	// isAutoEnq	- Whether the field is an Auto Launch Enquiry or not
	// isCheckFileEnri - Weather the field is an checkfile enrichment or not
    // isCaseConv   -  Whether the case conversion should be applied or not.
	// Run any required validation or auto launch enquiry for the field
	// Allows multiple events to run
	
	// Set the default button state if we don't want them to be able to use 
	// the default button when we are already processing another event.
	// This is used for hot field and hot validate events as we are about to
	// unload the form so don't want to run the default button action as well.
	
	//Get the event object that this event got fired from.
	
	// target for Firefox and scrElement for IE
	var thisField = null;
	
	// Make sure we get an event or a field object. Anything else does not work.
	if( thisEvent.target || thisEvent.srcElement)
	{
		// Get the source element from the event.
		thisField = thisEvent.target || thisEvent.srcElement;
	}
	else if( thisEvent.tagName == "INPUT" )
	{
		// The passed argument is the field itself.
		thisField = thisEvent;
	}
	else
	{
		// Just return as we don't have the right information.
		return;
	}
	_fieldName = thisField.getAttribute("name");
	_routine = thisField.getAttribute("vr");
	_autoEnq = thisField.getAttribute("autoEnqName");
	isHot = thisField.getAttribute("hot");
	isHotVal = thisField.getAttribute("hotVal");
	isWebVal = thisField.getAttribute("webVal");
	isAutoEnq = thisField.getAttribute("auto");
	isCheckFileEnri = thisField.getAttribute("checkFile");
	isCaseConv = thisField.getAttribute("CaseConv");
	
	// Update radios if theField is a radio type.
	if(thisField.getAttribute("type") == "radio")
	{
		updateAllRadios(thisField);
	}
	else if(thisField.getAttribute("type") == "checkbox")
	{
		toggleCheckBox(thisEvent);
	}
	
	if(isCaseConv!="")
	{
		doCaseConversion(_fieldName,isCaseConv);
	}
	
	if ( isAutoEnq == "Y" )
	{
		doFieldContextEnquiry( _autoEnq, _fieldName );
	}
			
	if ( ( isHotVal == "Y" ) || ( isHot == "Y" ) )
	{
		if ( ( isHotVal == "Y" ) && ( isHot == "Y" ) )
		{
			// Disallow default button event while we process the hot validate event
			setDefaultState( "OFF" );
			doHotValidate( _fieldName );
		}
		else
		{
			if ( isHot == "Y" )
			{
				// Disallow default button event while we process the hot field event
				setDefaultState( "OFF" );
				doHotField( _fieldName );
			}
			if ( isHotVal == "Y" )
			{
				// Disallow default button event while we process the hot validate event
				setDefaultState( "OFF" );
				doHotValidate( _fieldName );
			}
		}
	}
	
	if ( isWebVal == "Y" )
	{
		doWebValidationField( _fieldName, _routine );
	}
	
	// If no event needs running then just clear the enrichment as it's no longer valid
	if ( ( isHot == "N" ) && ( isHotVal == "N" ) && ( isWebVal == "N" ) && ( isAutoEnq == "N" ) )
	{
		clearEnrichment( "enri_" + _fieldName );
	}
	
	if ( isCheckFileEnri == "Y" && !( (isHot == "Y") || (isHotVal == "Y") ) )
	{
		var enriFieldName = _fieldName.replace( "fieldName:", "enri_");
		var enriObj = getTabEnrichmentSpan(currentForm_GLOBAL,enriFieldName);
		if (enriObj == null)
		{
		var enriObj = window.document.getElementById( enriFieldName );
		}
		// Check if we have an enrichment tag for this field.
		if( enriObj != null)
		{ 
			doWebValidationField( _fieldName, _routine );

		}
	}
}

/**
 * TODO
 * @param (TODO) TODO
 */
function doCaseConversion(_fieldName,sConversionType)
{
	
// code removed because field values are retrieved from current tab
	if(sConversionType == "UPPERCASE")
    {   /* BUG probably can cause bug if commands are obfuscated, because obfuscated commands contain
    	both upper and lower case characters. */
		
		var convfield = getCurrentTabField( currentForm_GLOBAL, _fieldName ); //retrieve field  from current tab
		var conv =  convfield.value.toUpperCase();  //get the value of the field and convert it to uppercase
		updatedFormsField(_fieldName,conv);// update it in all forms
    }
	else if(sConversionType == "LOWERCASE")
	{
		var convfield = getCurrentTabField( currentForm_GLOBAL, _fieldName ); //retrieve field  from current tab
		var conv = convfield.value.toLowerCase();//get the value of the field and convert it to lowercase
		updatedFormsField(_fieldName,conv);// update it in all forms
	}
	else if(sConversionType =="PROPER CASE")
	{
		var convfield = getCurrentTabField( currentForm_GLOBAL, _fieldName ); //retrieve field  from current tab
		var conv = toProperCase(convfield.value);//get the value of the field and convert it to proper case
		updatedFormsField(_fieldName,conv);// update it in all forms
	}
}

/**
 * TODO
 * @param (TODO) TODO
 * @return TODO
 */
function toProperCase(s)
{
  return s.toLowerCase().replace(/^(.)|\s(.)/g,function($1) { return $1.toUpperCase(); });
}

/**
 * Process a field focus event
 * @param (TODO) TODO
 */
function doFieldFocusEvent(thisEvent)
{
	// Parameters :-
	// _fieldName	- The name of the field the event occurred on
	// isHot		- Whether the field is a Hot field or not
	// isHotVal		- Whether the field is a Hot Validate field or not
	// isWebVal		- Whether the field is a Web Validate field or not
	// isAutoEnq	- Whether the field is an Auto Launch Enquiry or not
	
	// target for Firefox and scrElement for IE
	var thisField = null;
	
	// Make sure we get an event or a field object. Anything else does not work.
	if( thisEvent.target || thisEvent.srcElement)
	{
		// Get the source element from the event.
		thisField = thisEvent.target || thisEvent.srcElement;
	}
	else if( thisEvent.tagName == "INPUT" )
	{
		// The passed argument is the field itself.
		thisField = thisEvent;
	}
	else
	{
		// Just return as we don't have the right information.
		return;
	}
	
	
	_fieldName = thisField.getAttribute("name");
	isHot = thisField.getAttribute("hot");
	isHotVal = thisField.getAttribute("hotVal");
	isWebVal = thisField.getAttribute("webVal");
	isAutoEnq = thisField.getAttribute("auto");
	
	// Set any required variables for the field
	if ( ( isHot == "Y" ) || ( isHotVal == "Y" ) )
	{
		setHotField( 'true', _fieldName );
	}
	
	if ( isAutoEnq == "Y" )
	{
		setAutoField( 'true' );
	}
}

/**
 * Sets the status of the default button - either ON or OFF
 * Determines whether default button events run - another event could already be running
 * @param (TODO) TODO
 */
function setDefaultState( state )
{
  	// This is getting the default action from a global DOM lookup (with fragment suffix, if reqd)
	// Might need to use form-specific lookup if > 1 default button per screen
	var defaultButton = FragmentUtil.getElement("defaultButton");
				
	if ( defaultButton != null )
	{
		defaultButton.setAttribute("state",state);
	}
}

/**
 * Delivery Preview
 * @param (TODO) TODO
 */
function preview()
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);

	// Run the template with MESSAGE set to PREVIEW and with the correct function
	appreqForm.ofsMessage.value = "KbYR1Y5";

	var ftn = "I";			// Default the function to Input
	var screenMode = getFormField( currentForm_GLOBAL, "screenMode" );	

    // Check for screenMode value 
	if( screenMode != null )  
    {
       if ( screenMode.value == "A" )
	   {
       appreqForm.lockArgs.value = screenMode.value;  // to ensure value gets populated with rekey fields
       ftn = screenMode.value;
       }
       else
       {
	   ftn = screenMode.value;
       }
    }
	doDeal( ftn );
}

/**
 * On change event for password fields. As the user types in the characters this method is called.
 * @param (TODO) TODO
 */
  function onAuthChange(authField)
{
    // Get this field's name ( from the side form)
    var authFieldName = authField.name;
    // Get the corresponding field in 'appreq' form
    var appreqAuthField = getFormField( currentForm_GLOBAL, authFieldName );
    // Assign the value to the main appreq form.
    appreqAuthField.value = authField.value;
}

/**
 * Custom Field Buttons
 * @param (TODO) TODO
 */ 
function fieldButton(browserFunction,fieldName)
{
	// get the field input value
		var inputFieldValue = getFormFieldValue("appreq", fieldName);
	    var screenMode = getFormFieldValue("appreq", "screenMode");
        if(BrowserFunction.Input != screenMode)
        {                                              
        var inputFieldValue = fieldName;
        }
        else
        {
        // get the field input value
        var inputFieldValue = getFormFieldValue("appreq", fieldName);
        }
		//pass in only the field name by removing "fieldName:"
		var actualFieldName = fieldName.substring( 10, fieldName.length );
		var browserFunctionCall = browserFunction + "('" + actualFieldName + "', '" + inputFieldValue + "')";
		// call the custom javascript function as defined in the Browser Tool record
		eval(browserFunctionCall);
		
}

/**
 * this is called to adjust the size of the main div if the window is resized.
 */ 
function resizeDiv(event)
{
	//set the flag when the actual resize happens
	if(event != null || event != "")
	{
	 
        if(!resizeOninit && event.type == "resize")
		{
			 resizedone = 1;
			
			
		} 
		
	}
	// Get the multipane form
	var multiRequest = getFormField("generalForm", "multiPane");
	// Apply resize styling for mulitpane forms though it has top.noframes attribute.
 	// Because when the AA is opened by any enquiry drill down this attribute will set.
	if (top.noFrames && ( multiRequest == null || multiRequest == "undefined"))
	{
		return;
	}
	
	var offsetToolBar = 30;
	var offsetErrorDisplay = 10;
	var mainWindow = null;
	var multiAppOffset = 0;
	// Check if we are resizing and AA type application vs a normal one.
	if( multiRequest != null )
	{
		var mainApp = window.document.getElementById("MainApplication");
		var mainWindow = window.document.getElementById("BottomPart");
		var mainAppOffset = window.document.getElementById("MainApplication").offsetHeight;
		var topPartOffset = window.document.getElementById("topPart").offsetHeight;
		var allOffset = window.document.body.parentNode.offsetHeight - mainAppOffset - topPartOffset - offsetErrorDisplay;
		var toolBarObject = window.document.getElementById("toolBar");
		// If top.noframes then always set height as actual allOffset, Because in noframes mode calculation of  
		// allOffset will always has positive so can't have proper scroll view for frames in window.
		if( allOffset > 0 || top.noFrames)
        {
	        mainWindow.style.height = allOffset + "px";       
        }
        else
        {
           mainWindow.style.height = "250px";
        }

		return;
	}
	else
	{
		mainWindow = window.document.getElementById("contract_screen_div");	
	}
	
	// only want this to work in the application screens
	if( mainWindow != null )
	{
		var ieScrollBarWidth = 35;
		var ffScrollBarWidth = 28;
		
		// get height of top toolbar to offset
		var toolBarHeight = window.document.getElementById("toolBar").offsetHeight;
		
		if (toolBarHeight != null) 
		{
			// offset by another 40 pixels so height scrollbar disappears
			var htmlHeight = document.body.parentNode.offsetHeight - toolBarHeight - offsetToolBar - multiAppOffset;
			// for cases where there are composite screens
			if (htmlHeight > 0 )
			{
				mainWindow.style.height = htmlHeight + "px";
			}
			
		
		}
		
		// account for errors on the screen
		var errorDisplay = window.document.getElementById("error_box").offsetHeight;
		// only offset if there are errors
		if (errorDisplay != 0 && htmlHeight > 0)
		{			
			var allOffset = htmlHeight - errorDisplay - offsetErrorDisplay;
			// Only update with positive values.
			if( allOffset >= 0 )
			{
				mainWindow.style.height = allOffset + "px";
			}
		}
		// Set the width to fill the window. (default IE)
		mainWindow.style.width = window.document.body.parentNode.offsetWidth - ieScrollBarWidth;
		
		// Unfortunately different for Firefox so set the width again.
		if (isFirefox())
		{
			mainWindow.style.width = window.document.body.parentNode.offsetWidth - ffScrollBarWidth;
		}
	}
		// If the page is an enquiry in frames mode, then resize it.
		var enquiryDataScroller = window.document.getElementById("enquiryDataScroller");
		if (enquiryDataScroller != null && !top.noFrames)
		{
			resizeEnquiry();
		}
	}

/**
 * Called upon radio click.
 */ 
function toggleRadio( event)
{
	// Get the radio object that came with the event.
	var radioObject = event.target ? event.target : event.srcElement;
	updateAllRadios( radioObject);
}

/** 
* To resolve focus problem in IE6
**/
function enableFocus(current)
{
	current.style.border = "2px solid #C0C0C0" ;
}

function disableFocus(current)
{
	current.style.border = "none" ;
}

/**
 * Go through all the tabs and update the relevant radio buttons.
 */ 
function updateAllRadios( radioObject)
{
	
	// Extract the radio id.
	var radioId = radioObject.getAttribute( "id");
	// Break it up.
	var tokens = radioId.split(":");
	// 2 being the index at which the field name is.
	var fieldName = tokens[ 2];
	// Get the value of the passed in radio so that other radios with that value can be updated.
	var value = radioObject.getAttribute( "value");
	// Go through the list of available tabs
	for( var i = 0; i < tabList_GLOBAL.length; i++)
	{
		// Extract the current tab id.
		var currentTabId = tabList_GLOBAL[ i];
		// Generate the expected id for the radios on the tab that belong to the passed in radio.
		var newRadioId = "radio:" + currentTabId + ":" + fieldName; 
		// Get the radio objects that belong to the tab for the particular field
		var tabRadioList = getFormFields( currentForm_GLOBAL, newRadioId);
		// Go through the list of radios checking which one has the value tha same as the one passed in.
		for( var j = 0; j < tabRadioList.length; j++)
		{
			var tabRadio = tabRadioList[ j];
			if( tabRadio.getAttribute( "value") == value)
			{
				// Toggle radio if value is the same as passed in.
				tabRadio.setAttribute( "checked", "true");
			}
		}
	}
	// append third position for local reference fields
	if(tokens[3]!=null )
	{
		fieldName=fieldName+":"+tokens[3];
	}
	// Update the hidden fields
	var hiddenFieldName = "fieldName:" + fieldName; 
	var hiddenFieldObjects = getFormFields( currentForm_GLOBAL, hiddenFieldName);
	// Go through all the hidden fields on the page with the given id and update their values.
	for( var i = 0; i < hiddenFieldObjects.length; i++)
	{
		var currentHiddenField = hiddenFieldObjects[ i];
		currentHiddenField.setAttribute( "value", value);
	}
}


/**
 * Send request to server to unlock multiple records, each record is a request submitted via
 * a hidden window
 * 
 * @return void
 * @type null
 */
function unlockNoFramesDeal()
{
	//get all the forms on the page...
	for(var i = 0; i < document.forms.length; i++)
	{
		var aForm = document.forms[i];
		var formName = aForm.getAttribute("id");
		//loop through each one checking for contracts etc
		
		setCurrentForm( formName );
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
		
		var app = getFormFieldValue(currentForm_GLOBAL, "application");
		var transId = getFormFieldValue(currentForm_GLOBAL, "transactionId");
	
		if ( app )
		{
			var reqType = "6}CbYloYXh";
			var routineArgs = getFormFieldValue(currentForm_GLOBAL, "routineArgs");
			var closing = "Y";
			var title = "Unload";
			
			var unlock = "";
		// Only unlock the deal if it is not marked as "NO.UNLOCK" and 
	 	// there is a contract in the window			
			if ( transId )
			{
				var lockDateTime = getFormFieldValue(currentForm_GLOBAL, "lockDateTime");	
				unlock = app + " " + transId + ' ' + lockDateTime;		
				unlock = replaceAll(unlock, "%", "%25");
							
				var pwprocessid = getFormFieldValue(currentForm_GLOBAL, "pwprocessid");
				// Escape any % characters (e.g. enquiry names)
				var args = replaceAll(routineArgs, "%", "%25");
				
				// Submit the unlock request
				var nameArray = appreqForm.id.split("_");
				
				//save the window name first.
				var oldWindowName = window.name;
				
				window.name = nameArray[1];
				if (window.name == "undefined")
				{
					// this is only set in tabbed screens so special case here..
					if (currentFragment_GLOBAL != '')
					{
						Fragment.setCurrentFragmentName(currentFragment_GLOBAL);
					}
					else
					{	
						//we have a tabbed screen (noframes) and a window in framesmode. Rare but it can happen.
						window.name = oldWindowName;
					}
				}
				
				//Save the window name for No frames deal that are inside another composite screen, where the exact window name has
				// to be sent to unlock the deal, otherwise the containing composite screen's window name will get used
				
				noFramesDealWindowName_GLOBAL = window.name;    

				popupHiddenSubmitWindow( reqType, args, unlock, closing, title, pwprocessid );

				// Reset the window name to its original name.  This is for when you have nested composite
 				// screens (a noframes COS inside a frames COS).  This causes the last unlock command
 				// to rename the window which is in fact a frame in the outer frames composite screen
 				// causing following commands to go to a new window.
 				window.name = oldWindowName;
			}
		}
	}
}

/**
  * Returns the current object's form name when it is in noframe
  * Can have current object's fragment id, form id also.
  *
  * @return currentNoframeFormName
  * @type null
  */
 
function getCurrentNoframeFormName()
{
 	var windowRef = window;
 	if ( currentForm_GLOBAL && this.noFrames && FragmentUtil.isCompositeScreen())  // process only when noFrames mode with COS screen.
 	{
 		var fragmentName = windowRef.Fragment.getCurrentFragmentName();	// get fragment name
 		var noOfForms = new Array();
         var form_names="";
 
 	    for (var i = 0; i < windowRef.document.forms.length; i++) 
 		{
 			noOfForms[i]= windowRef.document.forms[i].getAttribute("id");  // get all forms id's
 			form_names = noOfForms[i].split("_");
 			//  Check with exact fragment where we are currently (in noFrames mode form name will be "formname_COSNamefield+numeric digits")			
 				if (form_names[1] == fragmentName)
 				{
 					currentForm_GLOBAL = form_names[0];
 					return currentForm_GLOBAL;			// return the current fragment form name
 				}
 			
 		}
 	}
}
 	
/**
 * Dynamically populates the FILE.NAME field with the result of the upload
 * @param (String) originalFilename The display value
 * @param (String) fullInfo The actual value to return to T24, contains original name, system generated file and file size.
 * @return (void)
 */
function showFileuploadSuccess(originalFilename, fullInfo) {
	var fileUploadField      = document.getElementById("fieldName:FILE.NAME");
	var fileUploadSuccessImg = document.getElementById("fileUploadSuccessImg");
	var fileUploadSpan       = document.getElementById("fileUploadSpan");
	var fileUploadIframe     = document.getElementById("fileUploadIframe");
	var enrichmentSpan       = document.getElementById("enri_FILE.NAME");
	fileUploadField.value = fullInfo;
	fileUploadIframe.style.display = "none";
	fileUploadSpan.innerHTML = originalFilename;
	fileUploadSpan.style.display = "inline";
	fileUploadSuccessImg.style.display = "inline";
	enrichmentSpan.innerHTML = "";
}