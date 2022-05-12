/** 
 * @fileoverview Generic functions used by other JavaScript files
 *
 */

var noNewWindow = '';     //forces a tool request into the same window
var TimerStopped = "";

//////////////////////////start of pick list fix//////////////////////////////////////////////

var windowTarget = "";   //the window name that you want to assign as the target to another window
var windowVariableMarker = "false";  //whether or not we want to use the below functionality for the variable windowTarget.
// Back button   variables
 var backWindow = ''; // window name for the target menu when the back button event triggered 
 var MenuStyle = ''; // Menu style of the ARC screen 
 var Backevent = false; //  whether the request is durring the back button event triggered .

/*
 * Holds the current form the user is working on. This is used for
 * a multi-form environment for AA application type screen. In this case
 * there will be 1 or more forms in the screen, this variable gets set
 * when a user mouse-downs on a particular form
 */
 var currentForm_GLOBAL = ""; 
 var currentTabName_GLOBAL = ""; // to hold the current tab name.
 var currentFragment_GLOBAL = ""; // to hold the current fragment name.
 var currentEvent_Global = ""; // To determine the Event from Mouse/ Keybord
 var loadingIconType = "normal";	// To Determine the loading image 
  
 var resizedone = ""; //flag to decide whether resize is done already
 var resizeOninit = ""; //flag set when resizediv is called from init applications
/**
 * Is called onmousedown of a form. So when a user clicks on a form
 * this function is invoked to set the name of that form
 * @param (String) formName Name of the form
 * @return {void}
 */
 function setCurrentForm(formName)
 {
 	currentForm_GLOBAL = formName;
 }
 function getLogin()
 {
	var _winDefs= "toolbar=no, menubar=no, status=yes, resizable=yes, scrollbars=yes, height=500, width=800, screenX=0, screenY=0"; 
	window.open("../html/processing.html", "_parent", _winDefs ); //some web servers are unable to process the path if double slash is used
	window.document.login.target = "_parent";
	window.document.login.submit();
 } 
 
/*
 *The following code is used when dealing with a pick list that needs to send the
 *resultant id chosen, to a window other than the parent window.  For example when
 *entering "CUSTOMER L" in the command line.  This will spawn two new windows: one
 *containing a drop down list of possible ids; the other the deal screen that the selected ID will
 *be written to. What we need to do is store the window name of the deal screen and attach it as a variable 
 *of the dropdown list window.  The variable used is clientInformation.  Dropdown.js will use the contents of
 *this variable to target its pick response to.
 */

/**
 * returns the name of the window that you want to send the response to for a specific window
 * @return tempWindowVariable
 * 
 */
function getWindowVariable(){
	tempWindowVariable = windowTarget;
	windowTarget = "";
	return tempWindowVariable;
}	

/**
 * stores the name of the window that the next response is going to
 * @param (String) name The name of the window
 * @return {void}
 */
function storeWindowVariable(name){
		windowTarget = name;
}


/**
 * Whether we want to store the name of the window that the response to the next request is going to.
 * @return windowVariableMarker
 */
 function getWindowVariableMarker(){
	return windowVariableMarker;	
}


/**
 * sets a boolean to be true or false depending on whether you want to store the 
 * specific window name of the request just done.
 * @param (String) marker set the marker
 * @return {void}
 */
function setWindowVariableMarker(marker){
	windowVariableMarker = marker;
}
/**
 * sets the current Tab name when mousedown in multi-tab version.
 */
 
function setCurrentTabName(TabName)
{
	currentTabName_GLOBAL = TabName;
}
//////////////////////////end of pick list fix//////////////////////////////////////////////

/**
 * Supply a DOM Node 'contains' function (not present in all browsers).
 * e.g. used to check the mouse is still 'in' a popup menu
 * @return false
 */
if (window.Node && Node.prototype && !Node.prototype.contains) {
    Node.prototype.contains = function (arg) {
    	// try catch block - was getting an exception when rapidly moving the mouse from the menu into an adjacent frame.
    	try {
	    	// !! is used to force a boolean value - compareDoc...() method returns an unsigned short.
	        return !!(this.compareDocumentPosition(arg) & 16);
    	} catch (e){
    		return false;
    	}
    };
}

/**
 * Cross browser event handling. Use the event passed in (from Netscape) or the window.event (IE)
 * @param (event) evt The event passed in.
 * @return myevt
 */
function getRealEvent(evt){
	myevt = (evt) ? evt : ((window.event) ? window.event : "");
	return myevt;
}

/**
 * Get the event source
 * @param (event) evt The event passed in.
 * @return 'eSrc' 
 */
function getEventSource(evt){
var eSrc;
	if (evt) {
        	if (evt.target) {

	            if (evt.currentTarget && (evt.currentTarget != evt.target)) {
        	        eSrc = evt.currentTarget;
	            } else {
        	        eSrc = evt.target;
	            } 
        	} else {

		eSrc = evt.srcElement;
		}
	}
return eSrc;
}

 /**
 * Hide a certain object in the document
 * @param (String) objectName The name of the object
 * @return {void}
 */
function hideObject( objectName )
{
	// Defer to the wrapper function
	FragmentUtil.setDisplay(objectName, false);
}


/**
 * Show a certain object in the document
 * @param (String) objectName The name of the object
 * @return {void}
 */
function showObject( objectName )
{
	// Defer to the wrapper function
	FragmentUtil.setDisplay(objectName, true);
}


/**
 * Toggles the screen display depending on the supplied state, 
 * by setting the visibility on Go button, Toolbars and Processing message
 * @param (String) state The state
 * @return {void}
 */
function showState(state)
{
	// Defer to the wrapper function
	FragmentUtil.showState(state);
}


/**
 * Get the value in the specified field
 * @param (String) fieldName The name of the field
 * @return {void}
 */
function getFieldValue( fieldName )
{
	var field = document.getElementById( fieldName );

	if ( field != null )
	{
		// return the stored parent frame target with item ALL so that all response types will be overwrite the composite screen DataAreas frame only when the target is null with back button is initiated this request.
		if (Fragment.parentDataAreaFragment!="" && Backevent == true && field.value == "")
		{
			return("ALL_"+Fragment.parentDataAreaFragment);
		}
		else
		{
			return( field.value );
		}
	}
	else
	{
		return( "" );
	}
}

/**
 * Get the value in the specified field on the specified form
 * @param (String) formName The name of the form
 * @param (String) fieldName The name of the field
 * @return {void}
 */ 
function getFormFieldValue( formName, fieldName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);
	if (form==null) {	// in case the form itself cannot be found...
		return("");
	}
	var field = form.elements[ fieldName ];
	// If the field is on the form more than once (i.e. 'elements' returns an array)
	// then return the first matching field - but not combox boxes as they always have 
	// more than one value
    try
    {
       if ( ( field.length > 1 ) && ( field.type != "select-one" ) )
        {
            field = field[0];
         }
    }
    catch(err)
	{
    }
	
	if ( field != null )
	{
		return( field.value );
	}
	else
	{
		return( "" );
	}
}

/**
 * try and get the field object on the specified form return null if not found.
 * @param (String) formName The name of the form
 * @param (String) fieldName The name of the field
 * @return null
 */ 
function findFormElement(formName, fieldName)
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);

	if (form==null) return null;

	for (var i=0;i<form.elements.length;i++)
	{
		if (form.elements[i].name = fieldName)
			return form.elements[i];
	}
	
	return null;
}

/**
 * Get the field object on the specified form
 * @param (String) formName The name of the form
 * @param (String) fieldName The name of the field
 * @return 'field'
 */ 
function getFormField( formName, fieldName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);
	
	// Return immediately if the named form doesn't exist
	if (form == null) {
		return null;
	}

	var field = form.elements[ fieldName ];

	// If the field is on the form more than once (i.e. 'elements' returns an array)
	// then return the first matching field - but not combox boxes as they always have 
	// more than one value
	if (field != null) {	
		try
		{
			if ( ( field.length > 1 ) && ( field.type != "select-one" ) )
			{
				field = field[0];
			}
		}
		catch(err)
		{
		}
	}
	
	return( field );
}

/**
 * Get all field objects with the specified fielName in formName
 * @param {String} formName The name of the form 
 * @param {String} fieldName The name of the field
 * @return {Array} Array of field objects
 */ 
function getFormFields( formName, fieldName )
{

	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm( formName);
	// Return immediately if the named form doesn't exist
	if (form == null) {
		return null;
	}
	
	var fields = new Array();
	var inputElements = form.getElementsByTagName("input");
	for ( var i = 0; i < inputElements.length; i++ )
	{
		var element = inputElements[i];
		if (element.name == fieldName)
		{
			fields[fields.length] = element;
		}
	}
	
	var selectElements = form.getElementsByTagName("select");
	for ( var i = 0; i < selectElements.length; i++ )
	{
		var element = selectElements[i];
		if (element.name == fieldName)
		{
			fields[fields.length] = element;
		}
	}
	
	var textElements = form.getElementsByTagName("textarea");
    for ( var i = 0; i < textElements.length; i++ )
    {
        var element = textElements[i];
         if (element.name == fieldName)
         {
             fields[fields.length] = element;
         }
    }
	
	return fields;
}

/**
 * Returns the form containing the given element.
 * @param {input element} The element.
 * @return {form or null} The first form containing the given element, or null if no such form exists.
 */
function getFormContainingField(element) {
	var parentElement = element.parentNode;
	while (parentElement != null) {
		if (parentElement.tagName == "FORM") {
			return parentElement;
		} else {
			parentElement = parentElement.parentNode;
		}
	}
	return null;
}


/**
 * Get the field object on the specified form
 * @param (String) formName The name of the form
 * @param (String) fieldName The name of the field
 * @return 'fieldValue'
 */
function getParentFormField( formName, fieldName )
{
	var theParent = window.parent;
	var field = theParent.document.getElementById(fieldName);
	return( field );
}

function getTabEnrichmentSpan(formName,enriFieldName)
{	
	var form = FragmentUtil.getForm(formName);
	if(form == null)
	{
		var parentWin = FragmentUtil.getParentWindow();
		if(parentWin!=null)
		{
			form = parentWin.document.getElementById(formName);
		}
	}
	//If the form element is null find the forms in parent window. In case of processing iFrames
	if(form==null)
	{
		form = FragmentUtil.getForm(formName,window.top);
	}	
	if(form==null)
	{
		if(window.parent)
		{
			form = FragmentUtil.getForm(formName,window.parent);
		}
	}	
	var enriSpanList = form.parentNode.getElementsByTagName("span");
	for(var i=0;i<enriSpanList.length;i++)
	{
		if(enriSpanList[i].id!=null && enriSpanList[i].id == enriFieldName)
		{
			return enriSpanList[i];
			break;
		}
	}	
}

/**
 * Get the value of a radio button given a field object
 * @param (Object) fieldObj The field object reference
 * @return 'fieldValue'
 */
function getRadioValue( fieldObj )
{
	var fieldValue = "";
	
	if ( fieldObj.type == "radio" )
	{
		if (fieldObj.checked)
		{
			fieldValue = fieldObj.value;
		}
	}
	
	return( fieldValue );
}



 /**
 * Move object in a particular position
 * @param (String) dir The direction
 * @param (Object) obj The object name
 * @param (int) pos Number of pixels to move
 * @return {void}
 */
function moveObject( dir, obj, pos )
{
	if ( dir == "left" )
	{
		setLeftPosition( obj, pos );
	}
	else if ( dir == "right" )
	{
		setRightPosition( obj, pos );
	}
}

 /**
 * Set left position of an object
 * @param (Object) obj The object name
 * @param (int) pos Number of pixels to move
 * @return {void}
 */
function setLeftPosition( obj, pos )
{
	var object = document.getElementById( obj);

	if ( object != null )
	{
		object.style.left = pos;
	}
}

 /**
 * Set right position of an object
 * @param (Object) obj The object name
 * @param (int) pos Number of pixels to move
 * @return {void}
 */ 
function setRightPosition( obj, pos )
{
	var object = document.getElementById( obj);

	if ( object != null )
	{
		object.style.right = pos;
	}
}

 /**
 * Disable every element on the form - except the processing message
 * @param (String) formname The name of the form
 * @return {void}
 */ 
function disableForm( formname )
{
	for ( var i = 0; i < formname.elements.length; i++ )
	{
		var element = formname.elements[i];
		
		// Do not disable the processing message page
		if ( element.name != "processingPage" )
		{
			element.disabled="true";
		}
	}
}


 /**
 * Replace all occurrences of a particular string with another string
 * @param (String) sourceString
 * @param (String) searchString
 * @param (String) replaceString
 * @return 'resultString'
 */ 
function replaceAll(sourceString, searchString, replaceString)
{
    // Replaces all occurences of searchString with replaceString in sourceString
    var iCurrentPos = 0;
    var iSearchStringPos = 0;
    var resultString = "";
    var iSearchStringLength = searchString.length;

    while ( ( iSearchStringPos != -1 ) && ( iCurrentPos < sourceString.length ) )
    {
      iSearchStringPos = sourceString.indexOf( searchString, iCurrentPos );
      if ( iSearchStringPos != -1 )
      {
        // Replace this occurrence of searchString
        resultString = resultString + sourceString.substring( iCurrentPos, iSearchStringPos );
        resultString = resultString + replaceString;
        iCurrentPos = iSearchStringPos + iSearchStringLength;
      }
    }

    if ( iCurrentPos < sourceString.length )
    {
      // Still some remaining text after the last searchString so append it
      resultString = resultString + sourceString.substring( iCurrentPos, sourceString.length );
    }

    return( resultString );
}


 /**
 * Replace all occurrences of special (non-alpha/numeric) characters with the specified character
 * @param (String) sourceString
 * @param (String) replaceString
 * @return 'resultString'
 */ 
function replaceSpecialChars(sourceString, replaceString)
{
	// Replaces all occurences of special chars with replaceString in sourceString
	var iCurrentPos = 0;
	var resultString = "";
	var sourceLength = sourceString.length;

	while ( iCurrentPos < sourceLength )
	{
		var currentChar = sourceString.charAt( iCurrentPos );

		if ( ( ( currentChar >= '0' ) && ( currentChar <= '9' ) ) ||
		     ( ( currentChar >= 'A' ) && ( currentChar <= 'Z' ) ) ||
		     ( ( currentChar >= 'a' ) && ( currentChar <= 'z' ) ) )
		{
			// Normal char
			resultString = resultString + currentChar;
		}
		else
		{
			// Special char so replace it
			resultString = resultString + replaceString;
		}

		iCurrentPos++;
	}

	return( resultString );
}


 /**
 * Strips a string of any spaces at the ends
 * @param (String) reference The string to parse
 * @return 'reference'
 * @type TODO
 */ 
function stripSpacesFromEnds(reference)
{
	if ( ( reference == undefined ) || ( reference == null ) )
	{
		reference = "";
	}
	
	if ( reference != "" )
	{
		//check to see if there are any spaces on the end
		while ((reference.length!=0)&&(reference.lastIndexOf(" ")==(reference.length-1)))
		{
				reference = reference.substring(0,reference.lastIndexOf(" "));
		}
		//check to see if there are any spaces at the start
		while ((reference.length!=0)&&(reference.indexOf(" ")==0))
		{
				reference = reference.substring(1,reference.length);
		}
	}
		
	return reference;
}

 /**
 * Set the window status bar
 * @return {void}
 */ 
function setWindowStatus()
{
	var companyName = getFieldValue("company");
	var user = getUser();
	var today = getFieldValue("today");
	var serverrelease = getFieldValue("release");
	var timeinfo = getFieldValue("timing");
	var showStatus = getFieldValue("showStatusInfo");

	if ( ( showStatus == "" ) ||  ( showStatus == "YES" ) || ( showStatus == "undefined" ) )
	{
		// Set the status bar - use minimal real estate
		if (user=="") {
			window.status = "";
		} else{
			window.status = serverrelease + " - " + RELEASE + " | " + user + " | " + companyName + " | " + today+ " |" +timeinfo;
		}
	}
	
	removescreenfreeze();
}

/**
 * Set the window title bar with the release details based on a switching parameter.
 * @return {void}
 */ 
function setReleaseInfo()
{
	var compName = getFieldValue("company");
	var userid = getUser();
	var todaydate = getFieldValue("today");
	var servrelease = getFieldValue("release");
	var timedetail = getFieldValue("timing");
	var showRelease = getFieldValue("showReleaseInfo");

	if ( ( showRelease == "" ) ||  ( showRelease == "YES" ) || ( showRelease == "undefined" ) )
	{
		// Set the window title bar with the release details
		if (userid!="")
		{
			// set the release details as status information in windows title bar based on the showreleaseinfo switch.
			var windowRelstatus = window.top.document.title;
			var statussplit = windowRelstatus.split("|");
			if( statussplit.length > 1 )
			{
				return;
			} else {
				window.top.document.title = windowRelstatus + " - " + servrelease + " - " + RELEASE + " | " + userid + " | " + compName + " | " + todaydate+ " |" +timedetail;
			}
		}
	}
}

 /**
 * Disable the right click button
 * @param (TODO) e
 * @return 'true'
 */
function disable_right_click(e)
{
    var browser = navigator.appName.substring ( 0, 9 );
    var event_number = 0;
    if (browser=="Microsoft")
    {
        event_number = event.button;
    }
    else if (browser=="Netscape")
    {
        event_number = e.which;
    }

    if ( event_number==2 || event_number==3 )
    {
// disable right click when set in BROWSER.PREFRENCES or BROWSER.PARAMETERS.
       alert ( "Right Click Is Disabled" );
       return (false);
    }
    
    return (true);
}

 /**
 * Disable mouse key
 * @return {void}
 */
function check_mousekey (e)
{
    var browser = navigator.appName.substring ( 0, 9 );
    var mouse_key = 93;
    var keycode = 0;
    if (browser=="Microsoft")
    {
         keycode = event.keyCode;
    }
    else if (browser=="Netscape")
    {
         keycode = e.keyCode;
     } 

    if ( keycode == mouse_key )
    {
        alert ( "Mouse Key Is Disabled" );
    }
}

 /**
 * Trap mouse key events
 * @return {void}
 */
function trap_page_mouse_key_events ()
{
    var browser = navigator.appName.substring ( 0, 9 );
  
    if ( browser == "Microsoft" )
    {
        document.onmousedown = disable_right_click;
        document.onkeydown = check_mousekey;
    }
    else if ( browser == "Netscape" )
    {
        document.onclick = disable_right_click;
        document.onkeypress = check_mousekey;
        
    }
}


   
 /**
 * Deactivate Profile
 * @return {void}
 */
function doDeactivation()
{
	if(!FragmentUtil.checkWindowChanges("0Yteh1RYCKb}r1yY"))
	{
		return;
	}
	// Send utility request to server to build the change password form
	buildUtilityRequest("}XCKtXX5}b0","8o1y0C0Yteh1Rth1}6CKb}r1yY","generalForm","","","Deactivate Profile","","","");
}
 
 /**
 * Send Deactivate information to Servlet
 * @return {void}
 */
function SendDeactivateInfo()
{
	var action ="";
	var routineArgs = "";
	var startdate ="";
	var enddate ="";
	   
	// get the form reference, adjusting for Fragment name as necessary
	var gbiForm = FragmentUtil.getForm("gbinput");

	action = gbiForm.routineArgs.value;

	if ((action == "Kb}eYXXC0Yteh1Rth1}6CKb}r1yY" ))
	{
		startdate = gbiForm.fromdate.value;
		enddate = gbiForm.enddate.value;        
		routineArgs = action + ":" + startdate + ":" + enddate;
		
		gbiForm.routineArgs.value = routineArgs;

		// Submit the request to the Browser Servlet
		FragmentUtil.submitForm(gbiForm);
	};
}



 /**
 * Utility commands
 * @param (String) myutil The name of the utility routine
 * @return {void}
 */ 
function doutil(myutil)
{
	
	if(myutil=="ekt6>YCKtXX")
	{
		changePass();
	}
	else if(myutil=="0Yteh1RYCKb}r1yY")
 	{
 		doDeactivation(); 
 	}
	else if(myutil=="01Xt8yYCKb}r1yY")
	{
		disablePass();
	}
	else if(myutil=="lo1ePC>o10Y")
	{
		quickGuide();
	}
	else if(myutil=="h9fCkYyK")
	{
		help( "", "", "" );	
	}
	else if(myutil=="t8}ohC8b}5XYb")
	{
		about();
	}
	else if(myutil=="SIGN.OFF")
	{
		signOff();
	}
	else
	{
		docommand(myutil);
	}
}


 /**
 * Initialise Change Password form
 * @return {void}
 */
function initPassword()
{
	// Set the focus to a password field if it exists on the form
	// Check the routineArgs to determine which field to focus on
	var routineArgs = document.getElementById("routineArgs");

	var fieldName = "";

	if ( routineArgs != null )
	{
		fieldName = "oldPassword";			// The second password field
		var passwordField = document.getElementById( fieldName );


		if ( passwordField != null )
   		{
			passwordField.focus();
		}
	}
}

 /**
 * Set a user's password - either after repeating it, entering a new one when expired, or changing to a new one
 * @return {void}
 */ 
function setPassword()
{
	stopAnyWindowEvents();

	// get the form reference, adjusting for Fragment name as necessary
	var cpForm = FragmentUtil.getForm("changePassword");
	

	// Build the routine arguments for a change password request
	var action = cpForm.routineArgs.value;
	var routineArgs = "";
	var username = "";
	var password = "";

	if ( ( action == "Kb}eYXXCbYKYth" ) || ( action == "Kb}eYXXCYqK1bY0" ) )
	{
		// Process the request to set a new password after repeating it or it expired
		userName = cpForm.signOnName.value;
		var oldPassword = cpForm.oldPassword.value;
		password = cpForm.password.value;
        encUserName = cpForm.encSignOnName.value;
 		edno = cpForm.edno.value;
 		// Changing colon to html entity in password.
 		oldPassword = replaceAll(oldPassword, ":", "&#58;");
		password = replaceAll(password, ":", "&#58;");
  		routineArgs = action + ":" + userName + ":" + oldPassword + ":" + password + ":" + encUserName + ":" + edno;
		
	}
	else if ( action == "Kb}eYXXCekt6>Y" )
	{
		// Process the request to set a new password from scratch
		userName = cpForm.signOnName.value;
		password = cpForm.password.value;
		var newPassword = cpForm.newPassword.value;
		var confirmNewPassword = cpForm.confirmNewPassword.value;
		// Changing colon to html entity in password.
		password = replaceAll(password, ":", "&#58;");
		newPassword = replaceAll(newPassword, ":", "&#58;");
		confirmNewPassword = replaceAll(confirmNewPassword, ":", "&#58;");

		routineArgs = action + ":" + userName + ":" + password + ":" + newPassword + ":" + confirmNewPassword;
	}

	cpForm.routineArgs.value = routineArgs;

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(cpForm);
}


 /**
 * Change of password request - building the form
 * @return {void}
 */ 
function changePass()
{

	if(!FragmentUtil.checkWindowChanges("ekt6>YCKtXX"))
	{
		return;
	}
	// Send utility request to server to build the change password form
	buildUtilityRequest("}XCKtXX5}b0","8o1y0Cekt6>Y","generalForm","","","Change Password","","","");
}


 /**
 * Disable a profile
 * @return {void}
 */ 
function disablePass()
{
	alert("Send disable password request to server");
}


 /**
 * Show the quick guide
 * @return {void}
 */
function quickGuide()
{
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	// Forward to the quick guide via the servlet
	// The servlet will know what the list of skins are
	// Save the old command and set command to quickguide
	var oldCommand = genForm.command.value;
	genForm.command.value = "quickguide";

	// Open a new window and submit the command to the servlet
	submitgeneralForm( "NEW" );

	// Reset the command value
	genForm.command.value = oldCommand;
}

 /**
 * Function to populate skin names combo box on quick guide page
 * @return {void}
 */ 
function initQuickGuideSkins()
{	
	// get the form reference, adjusting for Fragment name as necessary
	var qgForm = FragmentUtil.getForm("quickguide");

	// Get the current skin
	var currentSkin = getSkin();

	// Get the list of valid skin names from the page
	var validSkins = getFieldValue( "validSkins" );

	// Get the combo box object
	var skinsCombo = qgForm.validSkinsList;

	// Add each skin to the combo box
    var iColonPos = 0;
    var iCurrentPos = 0;
	var iOptionIndex = 0;
	var thisSkin = "";

    	while ( ( iColonPos != -1 ) && ( iCurrentPos < validSkins.length ) )
    	{
      		iColonPos = validSkins.indexOf( ":", iCurrentPos );
      		if ( iColonPos != -1 )
      		{
        		// Add this skin to the combo box
				thisSkin = validSkins.substring( iCurrentPos, iColonPos );
				if ( thisSkin == currentSkin )
				{
	        			addComboItem( thisSkin, skinsCombo, iOptionIndex, true );
				}
				else
				{
					addComboItem( thisSkin, skinsCombo, iOptionIndex, false );
				}
	
        		iCurrentPos = iColonPos + 1;
				iOptionIndex = iOptionIndex + 1;
      		}
    	}

    	if ( iCurrentPos < validSkins.length )
    	{
      		// One remaining skin after the last skin so append it
		thisSkin = validSkins.substring( iCurrentPos, validSkins.length );
		if ( thisSkin == currentSkin )
		{
        		addComboItem( thisSkin, skinsCombo, iOptionIndex, true );
		}
		else
		{
			addComboItem( thisSkin, skinsCombo, iOptionIndex, false );
		}
    	}
}

 /**
 * Adds the specified option to the specified combo box
 * @param (TODO) oOption
 * @param (TODO) oComboBox
 * @param (TODO) oIndex
 * @param (TODO) oSelected
 * @return {void}
 */  
function addComboItem( oOption, oComboBox, oIndex, oSelected )
{
	oComboBox.options[oIndex] = new Option( oOption, oOption, false, false );

	if ( oSelected )
	{
		oComboBox.options[oIndex].selected = true;
	}
}


 /**
 * Function to re-show the quick guide page with a new skin
 * @return {void}
 */  
function quickGuideSkinChange()
{
	// get the form reference, adjusting for Fragment name as necessary
	var qgForm = FragmentUtil.getForm("quickguide");

	// Get the new skin name from the combo box
	var selectedSkin = qgForm.validSkinsList.options[qgForm.validSkinsList.selectedIndex].value;
	var user = getUser();

	// Redirect to the same page with new skin parameter - same window will be re-used due to same name
	var validSkins = getFieldValue( "validSkins" );
	var contextRoot = getWebServerContextRoot();

	var quickURL = location.protocol + "//" + location.host + "/" + contextRoot + "/jsps/quickguide.jsp";
	var args = "user=" + user + "&skin=" + selectedSkin + "&validSkins=" + validSkins;
	var fullURL = quickURL + "?" + args;

	var _left="100";
	var _top="200";
	var _winDefs= "toolbar=0,status=1,directories=0,scrollbars=1,resizable=1,menubar=no,left=" +_left+ ",top=" + _top + ",width=300,height=300"; 

	// Change the skin in the same window - use the current window name
	var windowName = window.name;

	var myWin=window.open(fullURL, windowName, _winDefs);
}

 /**
 * Display information about Browser
 * @return {void}
 */  
function about()
{
	// Pass the args to the about page
	var skin = getSkin();
	var contextRoot = getWebServerContextRoot();
	var aboutURL = location.protocol + "//" + location.host + "/" + contextRoot + "/jsps/about.jsp";
	//var args = "release=" + RELEASE + "&build=" + BUILD + "&skin=" + skin;
	var args = "release=" + RELEASE + "&skin=" + skin;
	var fullURL = aboutURL + "?" + args;

	var _left="100";
	var _top="200";
	var _winDefs= "toolbar=0,status=1,directories=0,scrollbars=1,resizable=1,menubar=no,left=" +_left+ ",top=" + _top + ",width=320,height=300"; 

	var winName = getUser() + "_" + "about";
	winName = replaceSpecialChars( winName, "_" );

	var myWin=window.open(fullURL, winName, _winDefs);
}

 /**
 * Return the value of the skin for this user as saved on each document
 * @return skin
 */  
function getSkin()
{
	var skin = getFieldValue( "skin" );

	if ( skin == "" )
	{
		skin = "default";
	}

	return( skin );
}

 /**
 * Return the value of the user as saved on each document
 * @return user
 */  
function getUser()
{
	var user = getFieldValue("user");

	if ( user == "" )
	{
		user = getFormFieldValue( currentForm_GLOBAL, "user" );
		
		if ( user == "" )
		{
			user = getFormFieldValue( "generalForm", "user" );
		}
	}
	
	return(user);
}

 /**
 *  Set the focus on the specified field
 * @param (String) fieldName The name of the field
 * @return {void}
 */ 
function focusField(fieldName)
{
	var notCurrentTab = false;
	var focusFld = getFormField( currentForm_GLOBAL, fieldName );
	//obtain the tab in which the field to be focussed exists
 	var fieldTabName = focusFld.getAttribute("tabname");
 	var fieldType = focusFld.getAttribute("type");	
	if(fieldType=="radio")
	{
		var fieldList = getFormFields( currentForm_GLOBAL, fieldName);                
		var isSelected="";
		        for( var i = 0; i < fieldList.length; i++)
		        {	
					isSelected=fieldList[i].getAttribute("selected");					
					if( isSelected== "true")
					{
						focusFld=fieldList[i];
						i=fieldList.length;
					}
		        }
	} 	
	//obtain the currently active tab
	var activeTab = getFormField( currentForm_GLOBAL, "activeTab");
	activeTab = activeTab.value;
	//check whether the field exists in current tab and can be set focus
	if (( fieldTabName != null ) && (activeTab != null ))
	{
		if (( fieldTabName != activeTab ) && ( fieldTabName != "mainTab"))
		{
			notCurrentTab = true;
		}
	}
	//Obtain the class set for the field
	var focusFldClass = focusFld.className;
	//Add '.' to obtain correct class name
	focusFldClass = "."+focusFldClass;
	//In the given class, check for the 'visibility' attribute value
	//if 'hidden' focus cannot be done
	var focusFldClassAttr = getCSSEntry(focusFldClass,'visibility','');
	// Field must not be hidden or disabled for it to receive the focus
	if ( ( focusFldClassAttr != "hidden" ) && ( focusFld != null ) && ( focusFld.type != "hidden" ) && ( focusFld.disabled == false ) && (notCurrentTab == false))
	{
       //In IE to set the initial cursor position	
	   if( isInternetExplorer() && top.noFrames )
	   {
       		setTimeout(function () { focusFld.focus(); }, 0);
       }
       else
       {
       		focusFld.focus();
       }
	   // Set the value after focussing to position the cursor to the end of text
	   focusFld.value=focusFld.value;
	}
}

 /**
 * Set the focus on the specified deal field - if it's tab is not hidden
 * @param (String) fieldName The name of the field
 * @return {void}
 */ 
function focusDealField(fieldName)
{

	// Get the field to focus on
	var focusFld = getFormField( currentForm_GLOBAL, fieldName );

	// Get the name of the tab that the field is on
	var fieldTabName = focusFld.getAttribute("tabname");
	// To bring focus to the radios after hotfield validation
	if (focusFld.type=="hidden")
	{
		var radioFname=fieldName.split(":");
		if(radioFname[0]=="fieldName")
		{
		fieldName="radio:"+ fieldTabName+":"+radioFname[1];
		}
	}
	if ( fieldTabName != null )
	{
		if (fieldTabName != "")
		{
			// Get the tab object
			var fieldTable = window.document.getElementById( fieldTabName );
	
			// Tab must not receive the focus if it is hidden or disabled
			var fieldTableDisabled = fieldTable.getAttribute("disabled");
			if ((fieldTableDisabled == null) || (fieldTable.disabled == false))
			{
				fieldTableDisabled = "false";
			}			
				var focusSet = false;
			if ( ( fieldTable != null ) && ( fieldTable.style.visibility != "hidden" ) && ( fieldTableDisabled == "false" ))
			{
				var trDetails = focusFld.parentNode.parentNode;
				if(trDetails != '')
				{
					if(isInternetExplorer())
					{
						var actionContainerDets = trDetails.childNodes[1];
					}
					else
					{
						var actionContainerDets = trDetails.childNodes[2];
					}
					if(actionContainerDets.childNodes.length != 0)
					{
						if(actionContainerDets.childNodes[0].tabIndex == 0)
						{
							var actionContainerDets = FragmentUtil.getElementsByClassName("action_container",'td',focusFld.parentNode.parentNode);
					    	for( var containerCnt  = 0; containerCnt < actionContainerDets.length; containerCnt++ )
							{
								var containerElement = actionContainerDets[containerCnt].childNodes;
								if(containerElement.length != '0' && actionContainerDets[containerCnt].nextSibling.childNodes[0].getAttribute("id") == fieldName)
								{
									if(containerElement[0].tabIndex == 0)
									{
										containerElement[0].focus();
										focusSet = true;
									} else {
										if(containerElement[0].childNodes[0] != null)
										{
											containerElement[0].childNodes[0].focus();
											focusSet = true;
										}	 
									}
								}
							}
						}
					}
					if(!focusSet)
					{
						focusField( fieldName );
					}
				}
			}
		}
	}
}
/* Enable the tooltip for an image when focus on img via tabkey */
function focusonKey(tooltip,thisEvent)
{
    if (currentEvent_Global == "Mouse")
       {
         return;
       }
	var tooltipTimer;
	var focusElement = thisEvent.target || thisEvent.srcElement;
	if (currentEvent_Global =="Mouse"){
          return;
    }
    if (focusElement.getAttribute("onkeypress") || focusElement.getAttribute("onclick")){
		var defaultButton = window.document.getElementById("defaultButton");
		if ( defaultButton != null){
			defaultButton.setAttribute("state","OFF");				//if we tabout from  icon it does not go submit
		}
	}	
	var tooltipDiv = (typeof document.createElementNS != 'undefined') ? document.createElementNS('http://www.w3.org/1999/xhtml', 'div') : document.createElement('div');
    tooltipDiv.setAttribute('id','');
    tooltipDiv.id = 'tooltipDiv';
    tooltipDiv.setAttribute('class','');
    tooltipDiv.className = 'tooltipDiv';
    if ( tooltipDiv != null )
    {
    	// Found the button, set it
        if (focusElement.title != "")
        {
        	tooltipDiv.appendChild(document.createTextNode(focusElement.title));
            tooltipDiv.style.display = tooltip;
            document.getElementsByTagName('body')[0].appendChild(tooltipDiv);
        }
	}
	    
    // Set size and position of the popup elements
	// Pass -1 as dimension to accept existing value
	var x = getElementX(focusElement)+2;
	var y = getElementY(focusElement)+21;
	var w = focusElement.title.length;

	if (x >= 0) {
	    tooltipDiv.style.left = x + "px";
	}

	if (y >= 0) {
	    tooltipDiv.style.top =  y + "px";
	   
	}
	if (w >= 0) {
	    tooltipDiv.style.size = w + "px";
	 }
	 tooltipTimer = setTimeout("hideTooltip()",3000);
}
function hideTooltip(thisEvent)
{
	var tooltipDiv = window.document.getElementById("tooltipDiv");
	if ( tooltipDiv != null )
		{
			// Found the button, set it
			
			tooltipDiv.style.display = "none";
			document.getElementsByTagName('body')[0].removeChild(tooltipDiv);		
	} 
	var defaultButton = window.document.getElementById("defaultButton");
	if ( defaultButton != null ){
		defaultButton.setAttribute("state","ON");				//reset the state value
	}
}
function hideDivmessage(thisEvent)
{
	if (thisEvent.keyCode != undefined && thisEvent.keyCode != 13 && thisEvent.keyCode!=0)
	{
		return;
	}
	hidePopupDropDown( currentDropDown_GLOBAL);
}

 /**
 * Starts a pole event by running the function 'clock' every second.
 * @return {void}
 */ 
function startPoll(){
		setWindowStatus();
		var time = document.getElementById("time");
		var storedTime = time.value;
		myInterval = self.setInterval('clock('+storedTime+')', 1000);
}

/**  
 * This function stops the timer when user edits any field value.
 * @return {void}
 */
function stopPoll(){

      if(TimerStopped=="")
      {
            TimerStopped = "STOPPED";
            toggleTimer();
      }
}

 /**
 * Decrements the time counter stored on the poll form. 
 * When the timer reaches zero then submit the poll form.
 * @param (int) storedTime
 * @return {void}
 */ 
function clock(storedTime){ 
	var time = document.getElementById("time");
	time.value=time.value-1;

	if(time.value==0){
		// Submit the request to the Browser Servlet
		FragmentUtil.submitForm("poll");

		//reset the timer.
		time.value = storedTime;
	}
}

 /**
 * Opens a popup window to submit a command - such as a logoff or unlock record command
 * @param (String) _requestType the type of request for the server command
 * @param (String) _routineArgs the arguments for the server command
 * @param (String) _unlock optional command to unlock a record
 * @param (String) _closingWin whether the request window is being closed after the request
 * @param (String) _title window title
 * @param (String) _pwprocessid PW.ACTIVITY.TXN id off the enquiry that is being executed.
 * @return {void} 
 */ 
function popupHiddenSubmitWindow( _requestType, _routineArgs, _unlock, _closingWin, _title, _pwprocessid )
{
	// Parameters are as follows :-
	// _requestType - the type of request for the server command
	// _routineArgs - the arguments for the server command
	// _unlock      - optional command to unlock a record
	// _closingWin  - whether the request window is being closed after the request
	// _title       - window title
	// _pwprocessid - PW.ACTIVITY.TXN id off the enquiry that is being executed.
	// Get the company Id, app, user and key.
	var companyId = getFormFieldValue( currentForm_GLOBAL, "companyId");
	var application = getFormFieldValue( currentForm_GLOBAL, "application" );
	var user = getUser();
	var transactionId = getFormFieldValue( currentForm_GLOBAL, "transactionId" );
	var screenMode = getFormFieldValue( currentForm_GLOBAL, "screenMode" );
	var dealWinName = FragmentUtil.getWindowOrFragmentName();
	
	//Use the exact window name to release the record as the FragmentName can differ in case of nested composite screens	
	if(top.noFrames)
	{
	         dealWinName=noFramesDealWindowName_GLOBAL;
			 noFramesDealWindowName_GLOBAL = "";
    }

	// params as per orig JSP request, without context as it was not used in JSP
	var params  = "command=globusCommand";
	    params += "&requestType=" + _requestType;
	    params += "&routineArgs=" + _routineArgs;
	    params += "&companyId=" + companyId;
	    params += "&application=" + application;
	    params += "&user=" + user;
	    params += "&transactionId=" + encodeURIComponent(transactionId);
	    params += "&unlock=" + _unlock;
	    params += "&closing=" + _closingWin;
	    params += "&pwprocessid=" + _pwprocessid;
	    params += "&windowName=" + dealWinName;
	    params += "&screenMode=" + screenMode;
		
		// To avoid script error while launching ARC-IB from external web applications 
		var isArc = window.document.getElementById("isARC");
		if(isArc == null ||  isArc == "")
		{	
			if (top.opener ) 
				{
					top.opener.sendHiddenCommand(params);
				}	
			else 
				{
					sendHiddenCommand(params);
				}
		} else
		{
				 sendHiddenCommand(params);
		}
}

 /**
 * Send a hidden command
 * @param (String) params The parameters
 * @return {void}
 */ 
function sendHiddenCommand(params) {	
	sendAJAXCommand (params);
}

 /**
 * Send Ajax command
 * @param (String) params The parameters
 * @return {void}
 */ 
function sendAJAXCommand(params) {	
	Fragment.sendRequest("BrowserServlet", params, FragmentUtil.genericAjaxHandler);	
}

 /**
 * Set the specified cursor on the specified form if it exists
 * @param (String) _formName The name of the form
 * @param (String) _cursorName The name of the cursor
 * @return {void}
 */ 
function setCursor( _formName, _cursorName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var formObj = FragmentUtil.getForm(_formName);

	if ( formObj != null )
	{
		formObj.style.cursor = _cursorName;
	}
}

/**
 * Returns the context root part of the web server directory structure
 * @return 'contextRoot'
 */ 
function getWebServerContextRoot()
{
	// Determine the web server context root by extracting the 1st word in the pathname string
	// However, due to the possibility of adding several nests e.g.
	// "/webSealRoot/SecureApps/BrowserWeb/servlet/BrowserServlet" extracting the 1st word will not
	// return a valid ContextRoot.
	// We construct the BrowserWeb application path and extract everything before it to give
	// a valid contextRoot.
	
	// NB Any changes to this function should be reflected in the same function in workflow.js
	
	var path = location.pathname;
	
	//we want to check for OS dependant path separators	
	var OsSeparator = "/";	
	var contextStart = path.indexOf( OsSeparator, 0 );
	
	if (contextStart == -1)
	{
		OsSeparator = "\\";
	}
		
	contextStart = path.indexOf( OsSeparator, 0 );	
	
	// Construct the web app. name - check if the request came from the HelpText portal or the main BrowserServlet
	var browserWebApp = "";
	var contextEnd = 0;
	
	if ( path.indexOf("portal-help-index.xml") != -1 )
	{
		contextEnd = path.lastIndexOf("/help/portal-help-index.xml");
	}
	else if ( path.indexOf("portal-help-menu.xml") != -1 )
	{
		contextEnd = path.lastIndexOf("/help/portal-help-menu.xml");
	}
	else if ( path.indexOf("/jsps/") != -1 )
	{
		contextEnd = path.lastIndexOf("/jsps/");
	}
	else
	{
		contextEnd = path.lastIndexOf("/servlet/");
	}
	
	// Now we have the start & end pos, extract
	var contextRoot = path.substring( contextStart + 1, contextEnd );
	
	return( contextRoot );
}

/**
 * Get the tabbed screen
 * @param (String) itemname
 */ 
function dotabbedscreen(itemname){
	// get the 'enqsel' form reference, adjusting for Fragment name as necessary
	var enqselForm = FragmentUtil.getForm("enqsel");

	// Set-up parameters for a Globus enquiry and submit it
	enqselForm.routineName.value="}XC>YhCht88Y0CXebYY6Cq=y";
	enqselForm.routineArgs.value=cmdname+"|"+itemname;
	enqselForm.requestType.value = "oh1y1haCb}oh16Y";
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm("mainmenu");
}

/**
 * Function to actually do the resize of a window
 * Depends on whether this is the first time we are loading this window, 
 * and whether it is part of a composite screen
 * @param (TODO) formName
 * @param (TODO) windowTop
 * @param (TODO) windowLeft
 * @param (TODO) windowWidth
 * @param (TODO) windowHeight
 * @return 'setWindowPosition'
 */ 
 function checkresize(formName, windowTop, windowLeft, windowWidth, windowHeight)
{
	//Do not resize if parameter value is specifically set to NO 
	if ( getFormFieldValue(formName, "allowResize") == "YES")
	{
		// Check if the window should be maximized
		if ( getFormFieldValue(formName, "maximize") == "true")
		{
			maximizeWindow();
		}
		else
		{
			setWindowPosition = 0;
			// Are we in a composite screen or in no frames mode, then don't resize!
  			if (top.noFrames || FragmentUtil.isCompositeScreen() )	
			{
				// Composite screen - check for Internet Explorer scrollbar bug
				return setWindowPosition;
			}	
			// Check if the window must be resized
			if (getFormFieldValue(formName, "WS_doResize") != "no")
			{
				// window has not been resized by browser yet, so resize it
				// First do the resize as Firefox aborts the move if doing so would
				// result in any part of the window being offscreen.
				try{
				window.resizeTo(windowWidth, windowHeight);
				window.moveTo(windowLeft, windowTop);
				}
				catch(err){
				}
				setWindowPosition = 1;
				// Set the flag so the window is not resized by browser again
				updatedFormsField("WS_doResize", "no");
			}
		}
	}
	return setWindowPosition;

}

/**
 * Function to maximize the current window
 * @return {void}
 */ 
 function maximizeWindow()
{
	try{
	top.window.moveTo(0,0);
	}
	catch(err){
	}

	if (document.all) 
	{
		try{
		top.window.resizeTo(screen.availWidth,screen.availHeight);
	} 
		catch(err){
		}
	} 
	else if (document.layers || document.getElementById) 
	{ 
		if (top.window.outerHeight < screen.availHeight || top.window.outerWidth < screen.availWidth)
		{
			top.window.outerHeight = top.screen.availHeight; 
			top.window.outerWidth = top.screen.availWidth;
		} 
	}
}

/**
 * Return the name of the composite screen - if we are in one
 * @return 'compScreen'
 */ 
function getCompositeScreen()
{
	var compScreen = FragmentUtil.getCompositeScreenName();
	
	if ( ( compScreen == "" ) || ( compScreen == "undefined" ) )
	{
		return( "" );
	}
	else
	{
		return( compScreen );
	}
}

/**
 * NOT ACCESSED by current Browser code - to be removed if there are no dependecies in any supported T24 release
 * Get the window position while we can - before the window is unloaded
 * @return {void}
 */ 

function beforeUnloadWindow()
{
	// Save the window positions in the routineArgs field if 
	// not in a composite screen
	var routineArgs = document.getElementById("routineArgs");
	
	if ( ! FragmentUtil.isCompositeScreen() )
	{
		applicationName = getFieldValue("application");
		versionName = getFieldValue("version");
		//add the resizedone flag to co-ordinates
		if(resizedone)
		{
			routineArgs.value = applicationName + ":" + versionName + ":" + getWindowCoordinates() + ":" + resizedone;
		}
		else
		{
			routineArgs.value = applicationName + ":" + versionName + ":" + getWindowCoordinates();
		}
	}
	else
	{
		routineArgs.value = "";
	}
}

/**
 * NOT ACCESSED by current Browser code - to be removed if there are no dependecies in any supported T24 release 
 * Unload the window sending a command to unlock any record and save the window position
 * @return {void}
 */ 
function unloadWindow()
{
	// Only save window position if we're not in a Composite Screen - routineArgs set up in beforeUnloadWindow accordingly
	// Unlock deals even if in a Composite Screen
	// Logoff if we're the main composite screen
	var app = getFieldValue("application");
	var transId = getFieldValue("transactionId");
	var reqType = "6}CbYloYXh";
	var routineArgs = getFieldValue("routineArgs");
	var closing = "Y";
	var title = "Unload";
	var compScreen = getCompositeScreen();
	var unlock = app + " " + transId;

	// Don't bother sending an enquiry unlock request unless we have window coordinates to save
	if ( ( routineArgs == "" ) && ( app == "Y6l" ) )
	{
		// Do nothing
	}
	else
	{
		// Ensure that if the routineArgs contains any % characters 
		// (e.g. enquiry names) that they are escaped correctly in the URL
		var args = replaceAll(routineArgs, "%", "%25");
		
		// Display a hidden popup for the command and submit it
		popupHiddenSubmitWindow( reqType, args, unlock, closing, title );
	}
}

/**
 * Remove prefix from an enquiry command if present, and any selection criteria
 * @param (String) cmdname the whole command
 * @return 'returnCmd'
 */ 
function extractEnquiryName( cmdname )
{
	var returnCmd = cmdname;
	
	var prefix = cmdname.substring(0,4);
	var queryPrefix = cmdname.substring(0,6);
	var isenquiry = 0;
	
	// Remove any prefix enquiry command
	if ( prefix == "Y6l" + " " )
	{
		isenquiry = 1;
		returnCmd = cmdname.substring(4, cmdname.length);
	}
	else if ( queryPrefix == "loYba" + " ")
	{
		isenquiry = 1;
		returnCmd = cmdname.substring(6, cmdname.length);
	}
	
	// Remove any selection criteria if this was an enquiry command
	if ( isenquiry )
	{
		var nameEndPos = returnCmd.indexOf(" "); 
		
		if ( nameEndPos != -1 )
		{
			returnCmd = returnCmd.substring( 0, nameEndPos );
		}		
	}

	return( returnCmd );
}

/**
 * Updates all fields on all forms with the specified name with the specified value
 * @param (String) _fieldName The name of the field
 * @param (String) _fieldValue The value of the field
 * @return {void}
 */ 
function updatedFormsField( _fieldName, _fieldValue )
{
	var formsNumber = window.document.forms.length;

	for ( var i = 0; i < formsNumber; i++ )
	{
		var formId = window.document.forms[i].id;

		if ( ( formId != null ) && ( formId != "" ) )
		{
			setFormFieldValue( formId, _fieldName, _fieldValue );
		}
	}
}

/**
 * Set the value of a field on the contract to a specified value
 * @param (String) _fieldName The name of the field
 * @param (String) _fieldValue The value of the field
 * @return {void}
 */ 
function setContractFieldValue( _fieldName, _fieldValue )
{
	var field = _fieldName;
	
	if (field.substring(0,10) != "fieldName:" )
	{
		field = "fieldName:" + _fieldName;
	}
	
	field = getBrowserFieldName( field );
	setFormFieldValue( currentForm_GLOBAL, field, _fieldValue );
}

/**
 * Set the value of a field on a form to a specified value
 * @param (TODO) _formId The id of the form
 * @param (String) _fieldName The name of the field
 * @param (String) _fieldValue The value of the field
 * @return {void}
 */
function setFormFieldValue( _formId, _fieldName, _fieldValue )
{
	// Find the field on the form and set it's value
	
	if (_formId == "")
	{
		_formId  = parent.currentForm_GLOBAL;
	}
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(_formId);

	for ( var i = 0; i < form.elements.length; i++ )
	{
		var field = form.elements[i];

		if ( field.name == _fieldName )
		{
			field.value = _fieldValue;
		}
	}
}


/**
 * Set the value of a paragraph on a form to a specified value
 * @param (String) _paraName The paragraph name
 * @param (String) _paraValue The paragraph value
 * @return {void}
 */ 
function setParaValue( _paraName, _paraValue )
{
	// Find the paragraph on the form and set it's value
	var paras = window.document.getElementsByTagName("P");

	for ( var i = 0; i < paras.length; i++ )
	{
		var para = paras[i];

		if ( para.name == _paraName )
		{
			para.innerText = _paraValue;
		}
	}
}

/**
 * Set the value of a paragraph on a form to a specified value
 * @param (TODO) _formId
 * @param (String) _paraName The paragraph name
 * @param (String) _paraValue The paragraph value
 * @return {void}
 */ 
function setParentFormParaValue( _formId, _paraName, _paraValue )
{
	// Find the paragraph on the form and set it's value
	var theParent = window.parent;
	var paras = theParent.document.getElementsByTagName("P");

	for ( var i = 0; i < paras.length; i++ )
	{
		var para = paras[i];

		if ( para.name == _paraName )
		{
			//para.appendChild( theParent.document.createTextNode( _paraValue ) );
			para.innerText = _paraValue;
		}
	}
}

/**
 * Set the class of a paragraph on a form to a specified value
 * @param (String) _paraName The paragraph name
 * @param (String) _paraClass The paragraph class
 * @return {void}
 */  
function setFormParaClass( _paraName, _paraClass )
{
	// Find the paragraph on the form and set it's value
	var paras = window.document.getElementsByTagName("P");

	for ( var i = 0; i < paras.length; i++ )
	{
		var para = paras[i];

		if ( para.name == _paraName )
		{
			para.setAttribute( "class", _paraClass );
		}
	}
}

/**
 * Set the class of a paragraph on a form to a specified value
 * @param (String) _formId
 * @param (String) _paraName The paragraph name
 * @param (String) _paraClass The paragraph class
 * @return {void}
 */ 
function setParentFormParaClass( _formId, _paraName, _paraClass )
{
	// Find the paragraph on the form and set it's value
	var theParent = window.parent;
	var paras = theParent.document.getElementsByTagName("P");

	for ( var i = 0; i < paras.length; i++ )
	{
		var para = paras[i];

		if ( para.name == _paraName )
		{
			para.setAttribute( "class", _paraClass );
		}
	}
}

/**
 * Set the class of a anchor on a form to a specified value
 * @param (String) _formId
 * @param (String) _anchorName The anchor name
 * @param (String) _anchorClass The anchor class
 * @return {void}
 */ 
function setParentFormAnchorClass( _formId, _anchorName, _anchorClass )
{
	// Find the paragraph on the form and set it's value
	var theParent = window.parent;
	var anchors = theParent.document.getElementsByTagName("A");


	for ( var i = 0; i < anchors.length; i++ )
	{
		var anchor = anchors[i];

		if ( anchor.name == _anchorName )
		{
			anchor.setAttribute( "className", _anchorClass );
		}
	}
}

/**
 * stop any automatic window events
 * @return {void}
 */
function stopAnyWindowEvents()
{
	if (window.event!=null)
	{
		window.event.returnValue=false;
	}
}

/**
 * sets the noNewWindow variable
 * @param (String) newValue New value
 * @return {void}
 */
function setnoNewWindow(newValue)
{
	noNewWindow = newValue;
}
/**
 * gets the noNewWindow variable
 * @return 'noNewWindox'
 */
function getnoNewWindow()
{
	return noNewWindow;
}


/**
 * Check the passed function is valid
 * @param (String) ftn The name of the function
 * @return 'TRUE' or 'FLASE'
 */
function functionCheck( ftn )
{
	switch ( ftn )
	{
//
// Whilst all these functions are valid, from
// the command line we only want some to be
// valid
// Commented out functions are considered special
// and must be processed in a special way.  These are
// checked in the specialFunctionCheck routine below.
//	
//		case "A" :
//		case "C" :
//		case "D" :
		case "E" :
//		case "H" :
		case "I" :
		case "L" :
//		case "P" :
//		case "Q" :
//		case "R" :
//		case "S" :
//		case "V" :
			return( true );

		default :
			return( false );
	}		
}


/**
 * Checks whether the passed funtion is a 'special' function
 * @param (String) ftn The name of the function
 * @return 'TRUE' or 'FLASE'
 */
function specialFunctionCheck( ftn )
{
	switch ( ftn )
	{
		case "A" :
		case "C" :
		case "D" :
		case "H" :
		case "P" :
		case "Q" :
		case "R" :
		case "S" :
		case "V" :
			return( true );

		default :
			return( false );
	}		
}

/**
 * Check the passed function is valid for T24 function
 * @param (String) ftn The name of the function
 * @return 'TRUE' or 'FLASE'
 */
function validFunctionCheck( ftn )
{
	switch ( ftn )
	{
		case "A"    :
		case "C"    :
		case "D"    :
		case "E"    :
		case "H"    :
		case "I"    :
		case "L"    :
		case "P"    :
		case "Q"    :
		case "R"    :
		case "S"    :
		case "V"    :
			return( true );

		default :
			return( false );
	}		
}


/**
 * Check if a lock is required for a particular function
 * @param (String) ftn The name of the function
 * @return 'TRUE' or 'FLASE'
 */
function lockRequired( ftn )
{
	switch ( ftn )
	{
		case "A"	:
		case "D"	:
		case "H"	:
		case "I"	:
		case "R"	:
		case "V"	:
			return( true );
			
		default :
			return( false );
	}
}

/**
 * Get the user's language as saved on the page
 * @return 'language'
 */
function getLanguage()
{
	var language = getFieldValue( "language" );

	if ( ( language == "" ) || ( language == "undefined" ) )
	{
		// The language isn't stored on the page, so check whether it was
		// stored in the window name in the form "lang_<language>"
		var windowName = window.top.name;
		var pos = windowName.indexOf("lang_");
		if ( pos != -1 )
		{
			var windowLang = windowName.substring( pos + 5, windowName.length);
			if ( windowLang != "" )
			{
				language = windowLang;
			}
		}
	}

	return( language );
}

/**
 * set companyid in the banner frame e.g. the one with will contain the commandline
 * this need to be done because when you change company the menu is reloaded and contains
 * the correct companyid however the command line wont
 * @param (String) companyid Name of companyid
 * @return {void}
 */
function setBannerCompanyId( companyid )
{
	//loop through frames
	for ( var i = 0; i < window.parent.frames.length; i++ )
	{
		try {
			if (window.parent.frames[i].name == 'banner')
			{
				// found the banner frame
				for ( var x = 0; x < window.parent.frames[i].document.forms.length; x++)
				{
					// loop through the forms and there elements looking for comandid field and set its value
					for ( var z = 0; z < window.parent.frames[i].document.forms[x].elements.length; z++)
					{
						if ( window.parent.frames[i].document.forms[x].elements[z].name == "companyId")
						{
							window.parent.frames[i].document.forms[x].elements[z].value = companyid;
						}
					}
				}
			}
		}
		catch (exception) {
                 	FragmentUtil._logger.error("EXTERNAL_URL", exception.message);
		}
	}
}

/**
 * Using the contract form attempt to convert the field names from OFS format e.g. FIELD.NAME:1:1 to
 * field name of the HTML form as the field may not be multi/sub valued
 * @param (String) ofsFieldName The name of the OFS field
 * @return 'browserField'
 */
function getBrowserFieldName( ofsFieldName )
{
	var browserField = ofsFieldName;
	browserField = findBrowserField(ofsFieldName, "input");
	browserField = findBrowserField(browserField, "select");
	browserField = findBrowserField(browserField, "textarea");

	return( browserField );
}

/**
 * Get the OFS field name, given the type
 * @param (String) ofsFieldName The name of the OFS field
 * @param (String) fieldType The type of the field
 * @return 'ofsFieldName'
 */
function findBrowserField( ofsFieldName, fieldType )
{
	if (currentForm_GLOBAL == "")
	{
		//set it here since value is lost from inline frame
		currentForm_GLOBAL = parent.currentForm_GLOBAL;
	}
	
	// get the 'appreq' form reference, adjusting for Fragment name as necessary	
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL, window.parent);
	
	var fElements = appreqForm.getElementsByTagName(fieldType);
	var i;
	var newFieldName = ofsFieldName;
	var objectName;

	// Check for an exact match on the field name
	var fld = appreqForm.elements[ofsFieldName];
	
	if ( (fld != null ) && (fld.type == fieldType) )
	{
		return ofsFieldName;
	}

	// Loop round all of the fields looking for the field with the appropriate type
	var matchedFieldName = "";
	var matchedFieldLen = 0;

	for (i = 0; i < fElements.length; i++)
	{	
		objectName = fElements[i].name;
		
		if (objectName.substring(0,10) == "fieldName:" )
		{
			if (objectName.length>1)
			{
				var testName = newFieldName.substring(0, objectName.length);
				
				if (testName == objectName)
				{
					// We have a match (or partial match), save it if it is longer than the previous match
					if ( objectName.length > matchedFieldLen )
					{
						matchedFieldName = objectName;
						matchedFieldLen = objectName.length;
					}
				}
			}
		}
	}
	
	if ( matchedFieldLen == 0 )
	{
		return ofsFieldName;
	}
	else
	{
		return matchedFieldName;
	}
 }
 
/**
 * Send maintenance code info
 * @return {void}
 */
function sendMaintenanceInfo()
{
	var action ="";
	var routineArgs = "";
	var username = "";
	var password = "";
	var randomvalue = "";
	var correctioncode ="";
	var maintenancecode ="";
	var genCode = "";

	// get the form reference, adjusting for Fragment name as necessary
	var gbiForm = FragmentUtil.getForm("gbinput");

	action = gbiForm.routineArgs.value;
	userName = gbiForm.signOnName.value;
	password = gbiForm.password.value;
	//Colon is reploaced with a special character since it is used as value seperator in the server logic 
	password = replaceAll(password, ":", "&#58;");
	if ((action == "Kb}eYXXCe}bbYeh1}6CbYKYth" ))
	{
		correctioncode = gbiForm.correctioncode.value;
		gencode = gbiForm.gencode.value;
		randomvalue = gbiForm.random.value;
		routineArgs = action + ":" + userName + ":" + password +":" + randomvalue + ":" + gencode + ":" + correctioncode;
	}
	else
	{
		maintenancecode = gbiForm.maintenancecode.value;
		routineArgs = action + ":" + userName + ":" + password + ":" + maintenancecode;
	}

	gbiForm.routineArgs.value = routineArgs;

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(gbiForm);
}

/**
 * Set the field focus
 * @return {void}
 */
function setFocus()
{
	// get the form reference, adjusting for Fragment name as necessary
	var gbiForm = FragmentUtil.getForm("gbinput");

	var action="";

    action = gbiForm.routineArgs.value;
   
    if (action=="Kb}eYXXCe}bbYeh1}6CbYKYth")
    {
       gbiForm.gencode.focus();
     }
	else if(action=="Kb}eYXXC0Yteh1Rth1}6CKb}r1yY")
 	{
 		gbiForm.fromdate.focus();
 	 }
     else
     {
       gbiForm.maintenancecode.focus();
     }
}  

/**
 * utility function to extract a part of a string based on a delimiter
 * @param (String) source
 * @param (String) searchString
 * @param (String) o
 * @return {void}
 */
function field(source, searchString, o){
	ReturnString = "";
	orig = source;
	itemcount = 0;
	iCurrentPos = 0;
	iSearchStringPos = 0;
    
    	iSearchStringLength = searchString.length;
	
	while ( ( iSearchStringPos != -1 ) && ( iCurrentPos < source.length ) && (itemcount<o))
	{
		thisbit= "";
		iSearchStringPos = source.indexOf( searchString, iCurrentPos );
		if ( iSearchStringPos != -1 )
		{
			itemcount = itemcount + 1;
			thisbit =  source.substring( iCurrentPos, iSearchStringPos );
        		iCurrentPos = iSearchStringPos + iSearchStringLength;
		}
	}
	if (itemcount==o) {
		ReturnString = thisbit;
	}

}

/**
 * Get the parent window name    !TODO: This method does not appear to be used
 * @return 'name'
 */
 // TODO: This method does not appear to be used
function getParentWindowName()
{
	// Check if the parent window name is stored on the form (as supplied from the web server)
	var parentWin;
	var parentName = getFieldValue( "WS_parentWindow" );

	if ( ( parentName == "" ) || ( parentName == "undefined" ) )
	{
		// We didn't save a name, so try if this is a child window
		var opener = window.opener;

		if ( opener )
		{
			// A child window
			parentWin = opener;
		}
	}
	else
	{
		// By opening the parent window we can get the parent window object
		parentWin = window.open("", parentName, "" );
	}	
	
	if ( parentWin )
	{
		return( parentWin.name );
	}
	else
	{
		return( "" );
	}
}

/**
 * Are we a parent window
 * @return 'TRUE' or 'FALSE'
 */
function isParent()
{
	// Check if the parent attribute is stored on the form (as supplied from the web server)
	var parent = getFieldValue( "WS_parent" );
	
	if ( ( parent == "" ) || ( parent == "undefined" ) )
	{
		return( false );
	}
	else
	{
		return( true );
	}
}

/**
 * Return true if the browser is firefox, else return false
 * @return 'TRUE' or 'FALSE'
 */ 
function isFirefox() {
	if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
		return true;
	} else {
		return false;
	}
}

/**
 * Return true if the device is iPad, else return false
 * @return 'TRUE' or 'FALSE'
 */ 
function isiPad() 
  {
	if (navigator.userAgent.toLowerCase().indexOf("ipad") != -1) {
		return true;
	} else {
		return false;
	}
  }
  
/**
 * Return true if the browser is internet explorer, else return false
 * @return 'TRUE' or 'FALSE'
 */ 
function isInternetExplorer(){
	if (navigator.userAgent.toLowerCase().indexOf("msie") != -1){
		return true;
	}
	else{
		return false;
	}
}

/**
 * Return true if the browser is Opera, else return false
 * @return 'TRUE' or 'FALSE'
 */ 
function isOpera(){
	if (navigator.userAgent.toLowerCase().indexOf("opera") != -1){
		return true;
	}
	else{
		return false;
	}
}

/**
 * Get the position of the window which is accessed differently in different browsers
 * @return the position of the window
 */ 
function getWindowCoordinates() {
	var left   = 0;
	var top    = 0;
	var width  = window.document.body.clientWidth;
	var height = window.document.body.clientHeight;
	
	// Get the position of the window
	if (window.screenX > 0) {
		left = window.screenX;	// Works in Firefox
		top  = window.screenY;
	} else if (window.screenLeft > 0) {
		left = window.screenLeft;	// Works in IE
		top  = window.screenTop;
	}
	//check if the window is only moved and not resized 
	if((windowtop != top || windowleft != left) && resizedone != "1")
	{
		resizedone = 2;
	}
	// Window sizing is different in Firefox and IE, but we can be right most
	// of the time by assuming the window has no toolbars and adding an offset
	if (isFirefox()) {
		left   += 4;
		top    += 30;
		width  -= 17;
		height += 1;
	}
	return left + ":" + top + ":" + width + ":" + height;
}


/**
 * Form element reset function
 * Resets combos, radios to first item, blanks text input fields
 * @param {String} formName the form element id
 * @return {void}
 */
function resetForm(formName) {
	var formRef = FragmentUtil.getForm(formName);
	
    // Loop round all the fields in the specified form
    var elems = formRef.elements;
    for (i = 0; i < elems.length; i++) {
        var curElem = elems[i];

        // combos - reset to default (first) option
        if (curElem.tagName.toLowerCase() == "select") {
			curElem.selectedIndex = 0;
        }
        else if (curElem.tagName.toLowerCase() == "input") {
	        // text inputs - set to blank
			if (curElem.type == "text") {
				curElem.value = "";
			}
	        // radio inputs - the form element name is reference to the _array_ of radio fields
			// - select the first (default) value of that array ( several times, just to make sure!)
			else if (curElem.type == "radio") {
				formRef[curElem.name][0].checked = true;
			}
        }
    }
}

/**
 * CSS doc entry retrieval
 * Adapted from 'The Javascript Anthology' - http://www.sitepoint.com/books/jsant1/
 * @param {String} selector the exact selector string
 * @param {String} attrib the exact selector string
 * @param {String} selector the exact selector string
 * @return retVal
 */
function getCSSEntry(selector, attrib, defaultValue) {
	var retVal = defaultValue;
	
	if (typeof document.styleSheets != "undefined") {
		for (var ssIdx = 0; ssIdx < document.styleSheets.length; ssIdx++) {
			var ruleSet = null;

			var ssDoc = document.styleSheets[ssIdx];
			if (typeof ssDoc.rules != "undefined") {
				ruleSet = ssDoc.rules;
			}
			else {
				ruleSet = ssDoc.cssRules;
			}

			for (var ruleIdx = 0; ruleIdx < ruleSet.length; ruleIdx++) {
				if(ruleSet[ruleIdx].selectorText){
					if (ruleSet[ruleIdx].selectorText.toLowerCase() == selector.toLowerCase()) {
						if (ruleSet[ruleIdx].style[attrib]) {
							retVal = ruleSet[ruleIdx].style[attrib];
							break;
						}
					}
				}
				else{
					if(ruleSet[ruleIdx].media && ruleSet[ruleIdx].media.mediaText){
						var mediaRuleSet = new Array();
						if(ruleSet[ruleIdx].cssRules){
							mediaRuleSet = ruleSet[ruleIdx].cssRules;
						}
						else if(ruleSet[ruleIdx].rules){
							mediaRuleSet = ruleSet[ruleIdx].rules;
						}
						for (var mediaRuleIdx=0; mediaRuleIdx < mediaRuleSet.length; mediaRuleIdx++){
							if (mediaRuleSet[mediaRuleIdx] && mediaRuleSet[mediaRuleIdx].selectorText) {						
							if (mediaRuleSet[mediaRuleIdx].selectorText.toLowerCase() == selector.toLowerCase()) {
								if (mediaRuleSet[mediaRuleIdx].style[attrib]) {
									retVal = mediaRuleSet[mediaRuleIdx].style[attrib];
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	}

	return retVal;
}

/**
 * Element CSS class management - testing for a class
 * Adapted from 'The Javascript Anthology' - http://www.sitepoint.com/books/jsant1/
 * @param {Element target} the element reference
 * @param {String classValue} the name of the CSS class to check
 * @return true or false
 */
function hasStyleClass(target, classValue) {
	var pattern = new RegExp("(^| )" + classValue + "( |$)");

	if (target && pattern.test(target.className)) {
		return true;
	}
	else {
		return false;
	}
}

/**
 * Element CSS class management - adding a class
 * Adapted from 'The Javascript Anthology' - http://www.sitepoint.com/books/jsant1/
 * @param {Element target} the element reference
 * @param {String classValue} the name of the CSS class to add
 * @return true or false
 */
function addStyleClass(target, classValue) {

	if (! target) {
		return false;
	}

	var pattern = new RegExp("(^| )" + classValue + "( |$)");
	if (!pattern.test(target.className)) {
		if (target.className == "") {
			target.className = classValue;
		}
		else {
			target.className += " " + classValue;
		}
	}
	return true;
}

/**
 * Element CSS class management - removing a class
 * Adapted from 'The Javascript Anthology' - http://www.sitepoint.com/books/jsant1/
 * @param {Element target} the element reference
 * @param {String classValue} the name of the CSS class to remove
 * @return true or false 
 */
function removeStyleClass(target, classValue) {
	if (! target) {
		return false;
	}
		
	var removedClass = target.className;
	var pattern = new RegExp("(^| )" + classValue + "( |$)");
	
	removedClass = removedClass.replace(pattern, "$1");
	removedClass = removedClass.replace(/ $/, "");
	
	target.className = removedClass;
	
	return true;
}

/**
 * Gets the enrichment span object
 * @param {String spanId} The id of the required enrichment field, eg 'enri_SECTOR'
 * @param {DomElement containerElement} (Optional) The container of the reqired enrichment normally a form or fragment.
 * @return The span object, or null if not found.
 */
function getEnrichmentSpan(spanId, containerElement) {
	if (!containerElement) {
		containerElement = document;
	}
	var spanList = containerElement.getElementsByTagName("span");
	var spanTot = spanList.length;
	for (var i=0; i<spanTot; i++) {
		spanObject = spanList[i];
		if (spanObject.getAttribute("id") == spanId) {
			return spanObject;
		}
	}
	return null;
}

/**
 * Clears an enrichment given the field id for it.
 * @param {String fieldName} The field name to clear the enrichment for.
 * @return void.
 */
function clearEnrichment( fieldName) 
{
	var enriId = fieldName.replace("fieldName:", "enri_");
	var enriObj = getEnrichmentSpan( enriId, "");
	if( enriObj != null)
	{
		enriObj.innerText = "";
	}
}

/**
 * Formats the a textarea element so that a single word cant be greater then the specified
 * size of the textarea (in characters). This should only be done for Firefox as IE takes
 * care of it as anything should.
 * @param {String fieldName} The field name of the textarea tp format.
 * @param {Number size} The character size specified in the textarea.
 * @return void.
 */
function formatTextArea( fieldName, size) 
{

	// The new built array to return.
	var newTokens = new Array();
	var thisfieldName = fieldName.target || fieldName.srcElement; 
	fieldName = thisfieldName.name;
	// Since size variable holds the number as string convert to Integer.
	size = Number(size);
	// Get the value of the field.
	var value = getCurrentTabFieldValue( currentForm_GLOBAL, fieldName );
	// Split the string on spaces (IE does it).
	var tokens = "";
	if (isInternetExplorer() || isOpera() ) {
		// IE and Opera does split the string using carrage return with new line.
		tokens = value.split("\r\n");		
	} 
	else {
		// Other than IE and Opera, all browsers(Chrome, Firefox, Safari) splits the string by new line character alone. 
		tokens = value.split("\n");
	}
	
	// Go through each line and work out if the line has a word greater then texbox size
	var word = "";
	var newWord = "";
	var text ="";
	
	for( var i = 0; i < tokens.length; i++ )
	{
	    //Removing trailing white space to avoid introducing a blank line
	    if(isInternetExplorer())
	    {
			word = tokens[ i];
		}
		else
		{
			word=tokens[i];
			word=word.replace(/\s+$/, '');
		}		
		if( word.length > size )
		{
			while( word.length > size )
			{
				// Split the word to fit in the text area
				// Don't break the words if it doesn't have spaces.
				// have the text by maximum chars (size)
  				text = word.slice(0,size);  
  				// find the last index space of the text. returns -1 if no space
                var textLastSpaceIndex = text.lastIndexOf(" ",size);       
                switch(true)
                {
                    // space in last index of the text or no space
  					case ( textLastSpaceIndex == size-1 ) || ( textLastSpaceIndex == -1 ):  
						newWord = word.slice( 0, size);                    
						break;
                      
  					default:
						// last index of the text is not a space so slice it upto the previous space to avoid the word cutting
						newWord = word.slice(0,textLastSpaceIndex+1);      
						break;  
 				}				
 				// Add to the return array
				newTokens.push( newWord);
				// Get rid of the 'newWord' in 'word'. 
				word = word.replace( newWord, "");
			}
			// Add what's left of word, if anything.
			if( word.length > 0)
			{
				newTokens.push( word);
			}
		}

		else
		{
			newTokens.push( word);
		}
	}
	
	var newValue = "";
	if (isFirefox()) {
		newValue = newTokens.join("\r");
	} 
	else {
		newValue = newTokens.join("\r\n");
	}
	// Update the field value with the new formated value
	setFormFieldValue( currentForm_GLOBAL, fieldName, newValue);
	
	//update the field value across the version with the new formated value
 	if ( tabList_GLOBAL.length > 1)
    {
         var fieldList = getFormFields( currentForm_GLOBAL, fieldName);                
         for( var i = 0; i < fieldList.length; i++)
         {
 			fieldList[i].value = newValue;
         }
    }

}

/**
 * Clears both the enrichment and textfield value.
 * @param {String fieldName} The field name on the form to be cleared.
 * @return void.
 */
function clearField( fieldName)
{
	setFormFieldValue( currentForm_GLOBAL, fieldName, "");
	setEnrichment(fieldName, "");
}

/**
* Get the value in the specified field on the specified tab if it is a textbox
* @param (TODO) formName                                      
* @param (TODO) fieldName                                     
* @return TODO                                                                                                  
*/

function getCurrentTabFieldValue( formName, fieldName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);
	if (form==null) {	// in case the form itself cannot be found...
		return("");
	}
	// If the field is on the form more than once (i.e. 'elements' returns an array)
	// then return the first matching field - but not combox boxes as they always have 
	// more than one value
	
	var formField = form.elements[ fieldName ];
	
	var currentTabFieldValue = "";

	if ( tabList_GLOBAL.length == 1 || ( ! formField.length ) )   // if only one tab in current form.
		{
			currentTabFieldValue = formField.value;
		}
	
	

	// Find the current tab field and get the field value when we are in associated tabbed version.	
	try
		{
			if ( tabList_GLOBAL.length > 1)
				{
					for ( var i = 0; i < formField.length ; i++ )
						{
							if ( formField[i].getAttribute("tabname") == currentTabName_GLOBAL )
							{
							  	var currentElement = formField[i];        //  for IE
							  	if (currentElement.name == fieldName )
								 {
								  	currentTabFieldValue = formField[i].value;
									break;
							     }
							}
						}
				}
		}
	catch(e)
		{}	
	return currentTabFieldValue;
}
 
/**
* Get the field on the specified tab
* @param (TODO) formName                                      
* @param (TODO) fieldName                                     
* @return TODO                                                                                                  
*/

function getCurrentTabField( formName, fieldName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var form = FragmentUtil.getForm(formName);
	if (form==null) {	// in case the form itself cannot be found...
		return("");
	}
	// If the field is on the form more than once (i.e. 'elements' returns an array)
	// then return the first matching field - but not combox boxes as they always have 
	// more than one value
	var formField = form.elements[ fieldName ];

	if ( ! formField )     // incase of frequency pattern, document only holds the element not form.
	{
		return;
	}
	var tokens=fieldName.split(":");
	var currentTabField = null;
	if(tokens[0]=="radio")
	{
		currentTabField = formField[0];
		return currentTabField;
	}
	//  In a no frame COS, some of the script variables are shared across all the window.
	//  In general variable tabList_GLOBAL is initialised in initDeal, is holding the previous frames value as its not over written with the new frame value as initDeal is not get triggred.
	//  Here we are initialising the variable tabList_GLOBAL for the current displaying frame.
	if ( tabList_GLOBAL.length == 0 && top.noFrames )
	{
		tabList_GLOBAL = getDisplayedTabs();
	}
	//When a field instance is found twice in the same tab, take the first copy
    if ( tabList_GLOBAL.length == 1)
    {
            if ( ! formField.length )
            {
                currentTabField = formField;
            }
            else
            {
                try
                 {  
                   if ( ( formField.length > 1 ) && ( formField.type != "select-one" )) //field instance have more than one copy 
                   {
                   formField = formField[0];
                   currentTabField = formField;
                   } 
	             }
	            catch(err)
	            { 
	            }
            }
    }

	if ( tabList_GLOBAL.length == 1 || tabList_GLOBAL.length == 0 || ( ! formField.length ) )  
		{
			currentTabField = formField;
		}
	
	// Find the current tab field when we are in associated tabbed version.	
	try
		{
			if ( tabList_GLOBAL.length > 1)  // In case of multiple Tabs 
 				{					                                            
                  if ( ( formField.length > 1 ) && ( formField.type == "select-one" )) 
                     {
             // Single occurance of list box fld as a hot fld in any one of the tab.  	
                          currentTabField = formField;
                      } 
                     else
                      {
 					
 						for ( var i = 0; i < formField.length ; i++ )
 							{
 								var activeTabField = formField[i];
 								if ( activeTabField.getAttribute("tabname") == currentTabName_GLOBAL )
 								{
 									if (activeTabField.name == fieldName)    //  for IE
 									 {
 										currentTabField = activeTabField;
 										break;
 									 }
 								}
 							}
                      }	
 		}

	}
	catch(e)
		{}	
	return currentTabField;
	
}

/**
* Event handler to clear the status bar
* @param (Event) The event                                                                          
* @return void                                                                                                
*/
function clearStatusBarEvent(evt)
{
	if (typeof evt != 'undefined')
	 {
		evt = window.event;
	}
	
	window.status = "";
	
	if (evt && typeof evt.returnValue != 'undefined') 
	{
		evt.returnValue = true;
	}
	
	if (evt && evt.preventDefault) 
	{
		evt.preventDefault();
	}
	
	return true;
}

/**
* Initialises a window
* @param (Event) The event                                                                          
* @return void
*/
function initWindow()
{
	// Call any window initialise functions
	// current event not required since the below js is only adding the scripts for clearing status bar on 
	// various events like mouseover,mouseout and click
	initialiseWindowEvents();
	// Set the release details as status information in the title bar based on switch.
	setReleaseInfo();
	
	removescreenfreeze();
}

/**
* Restore the freezed window
*/
function removescreenfreeze()
{
	// to release the locked frame window body after the response is obtained
	if (!top.noFrames && FragmentUtil.isCompositeScreen()) 
	{
		var divelement = "";
		var winreference = "";
		
		if(window.top) 
		{
			winreference = window.top;
		}
		
		if(winreference != "" && winreference != null)
		{
			for ( var i = 0; i < winreference.frames.length; i++ )                          
			{                                                                                
				if( (winreference.frames[i].document.body) && (winreference.frames[i].document.body.nodeName == "FRAMESET"))
				{
					// immediate parent window
					if(window.parent)
					{
						for (var j = 0; j< window.parent.frames.length; j++)
						{
							divelement = window.parent.frames[j].document.getElementById("overlay");
							if(divelement !="" && divelement != null)
							{
								window.parent.frames[j].document.body.removeChild(divelement);
							}
						}
					}
				 } 
				 else 
				 {
					divelement = winreference.frames[i].document.getElementById("overlay");
					if(divelement !="" && divelement != null)
					{
						winreference.frames[i].document.body.removeChild(divelement);
					}
				}
			}
		}
	}
}

/**
* Initialises the window events
* @param (Event) The event
* @return void                                                                                                  
*/
function initialiseWindowEvents()
{
	var showStatus = getFieldValue("showStatusInfo");

	if ( showStatus == "NO" )
	{
		// Add events to clear the status bar - take in to account IE and FireFox
		if (document.addEventListener) 
		{
			document.addEventListener('mouseover', clearStatusBarEvent,	false );
			document.addEventListener('mouseout', clearStatusBarEvent,	false );
			document.addEventListener('onclick', clearStatusBarEvent,	false );
		}
		else if (document.attachEvent) 
		{
			document.attachEvent('onmouseover',	clearStatusBarEvent	);
			document.attachEvent('onmouseout',	clearStatusBarEvent	);
			document.attachEvent('onclick',	clearStatusBarEvent	);
		}
	}
}

/* Create a new function for Array() so that we can remove an item from the array list.
 * 
 */
// adding it as a prototype object enables it to be used from any array
Array.prototype.removeItems = function(itemsToRemove) 
{
    if (!/Array/.test(itemsToRemove.constructor)) 
    {
        itemsToRemove = [ itemsToRemove ];
    }

    var j;
    for (var i = 0; i < itemsToRemove.length; i++) 
    {
        j = 0;
        while (j < this.length) 
        {
            if (this[j] == itemsToRemove[i]) 
            {
                this.splice(j, 1);
            } 
            else 
            {
                j++;
            }
        }
    }
};

/* Createa a new function for Array to see if an element exits
 * 
 */
// adding it as a prototype object enables it to be used from any array
Array.prototype.contains = function ( element) 
{
	for (var i = 0; i < this.length; i++) 
    {
    	if (this[i] == element) 
        {
        	return true;
        }
    }
    return false;
};

/* Create a new function for Array() so that we can add unique items to array
 * 
 */
// adding it as a prototype object enables it to be used from any array
Array.prototype.addUniqueItem = function( item) 
{
	if( !this.contains( item))
	{
		this.push( item);
		return true;
	}
	// didnt add it
	return false;
};

/**
* Create a new function for Array() to get the array of indices containing given string
*/
Array.prototype.find = function(searchStr) {
  var returnArray = false;
  for (i=0; i<this.length; i++) {
    if (typeof(searchStr) == 'object') {
      if (this[i].match(searchStr)) {
        if (!returnArray) 
		{ returnArray = []; }
        returnArray.push(i);
      }
    } else {
      if (this[i].indexOf(searchStr)>=0) {
        if (!returnArray) 
		{ returnArray = []; }
        returnArray.push(i);
      }
    }
  }
  return returnArray;
};

/**
* Return the value of a field that an event occurred on
* @param (String) thisEvent The current field event
* @return {String} value
*/
function getFieldEventValue( thisEvent )
{
	// target for Firefox and scrElement for IE
	var thisField = null;
	var thisFieldValue = "";
	
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
		return "";
	}
	
	return thisField.value;
}
/**
     * Gets the current fragment/frame/parent window object.
     * In case of no-frames window, need to have proper drill info for multi document download/fastpath enquiry.
	 * If selection made on enquiry is placed in different frames, selection of drill info
	 * items should be belongs to invoking enquiry.

	 * @param currentFragmentObject -current fragment object to search for div element
	 * @param divClassName - classname attribute of div element.
	 * @param fragmentFormId - form name which has to be returned.
	 * @param parentForm - parent form object, if needs to change any of the attribute from child window.

     * @return requested form object
     * @type object
 */

function getCurrentFragmentEnqDataForm(fragmentName,divClassName,fragmentFormId,parentForm)
{
	var edForm = "";
	// In case of no-frames mode.
	if ( top.noFrames )
	{
		var currentFragmentObject = "";
		// In case of fastpath enquiry, need to have all objects from parent window.
		if ( parentForm )
		{
			// Get the parent window fragment name.
			fragmentName = parentForm.FragmentUtil.getWindowOrFragmentName();
			// Extract the parent window fragment object.
			currentFragmentObject = parentForm.document.getElementById(fragmentName);
		}else{
			// Retrieve current fragment object from document.
			currentFragmentObject = document.getElementById(fragmentName);
		}
		// If search form object resides any DIV element.
		if (divClassName != "")
		{
			// Call method to return exact form object.
			edForm = getFormObjectFromFragmentDiv(currentFragmentObject,divClassName,fragmentFormId);
		}else{
			// Search form object resides any Fragment element.
			edForm = getFormObjectFromFragment(currentFragmentObject,fragmentFormId+"_"+fragmentName);
		}
	}
	else
	{
		// In case of fastpath enquiry result need to have all objects from parent window.
		if ( parentForm )
		{
			// Get parent window form.
			edForm = parentForm.FragmentUtil.getForm(fragmentFormId);
		}else{
			// In case of frame/window based form just call getForm method.
			edForm = FragmentUtil.getForm(fragmentFormId);
		}
	}
	return edForm;
}

/**
     * Returns the exact form object
     * Traverse whole div element and returns the matched form object by checking the classname attribute 
	 * of the div object.

	 * @param currentFragmentObject -current fragment object to search for div element
	 * @param divClassName - classname attribute of div element.
	 * @param fragmentFormId - form name which has to be returned.

     * @return requested form object
     * @type object
 */

function getFormObjectFromFragmentDiv(currentFragmentObject,divClassName,fragmentFormId)
{
		// Get all div elements from the current fragment object.
		var currentDivObjects = currentFragmentObject.getElementsByTagName("div");
		var formToGetDrillDetails = "";
		var foundFormObjectFlag = "";
		for ( var h =0; h < currentDivObjects.length; h++)
		{
			// In case of IE class attribute will be retrieved only by giving "className".
			// but in fire fox "class" is enough.
			formToGetDrillDetails = currentDivObjects[h].getAttribute("className");
			if ( !formToGetDrillDetails )
				formToGetDrillDetails = currentDivObjects[h].getAttribute("class");
			if (formToGetDrillDetails != null)
				{
					formToGetDrillDetails = formToGetDrillDetails.split(" ");
					var actualclass = formToGetDrillDetails[0];
				}
			// If the class name is divClassName get the form.
			if ( actualclass == divClassName )
			{
				// Loop through each form in passed div.
				formToGetDrillDetails = currentDivObjects[h].getElementsByTagName("form");
				for ( var j =0 ; j < formToGetDrillDetails.length; j++)
				{
					// Find the exact form object of current div.
					if (formToGetDrillDetails[j].getAttribute("id") == fragmentFormId );
					{
						formToGetDrillDetails = formToGetDrillDetails[j];
						foundFormObjectFlag = formToGetDrillDetails;
						break;
					}
				}
			}
			if ( foundFormObjectFlag )
				break;
			// Need to clear since this is a return variable. Otherwise last assigned object will return.
			formToGetDrillDetails = "";
		}
		// return the exact form object.
		return formToGetDrillDetails;
}

/**
      * Returns the exact form object
      * Traverse whole each form and returns the matched form object by checking the form id or name attribute
 	 * of the form object.
 
 	 * @param currentFragmentObject -current fragment object to search for div element
 	 * @param fragmentFormId - form name which has to be returned.
 
      * @return requested form object
      * @type object
  */
 
 function getFormObjectFromFragment(currentFragmentObject,fragmentFormId)
 {
 	// Get all form elements from the current fragment object.
 	var currentDivObjects = currentFragmentObject.getElementsByTagName("form");
 	var formToGetDrillDetails = "";
 	var foundFormObjectFlag = "";
 	for ( var h =0; h < currentDivObjects.length; h++)
 	{
 		// Retrieve the Id attribute of the current form.
 		formToGetDrillDetails = currentDivObjects[h].getAttribute("id");
 		// If current form Id attribute matches search from Id, get the form.
 		if ( formToGetDrillDetails == fragmentFormId )
 		{
 			// Assign matched form object.
 			formToGetDrillDetails = currentDivObjects[h];
 			foundFormObjectFlag = formToGetDrillDetails;
 			// return back from this method since search element found.
 			break;
 		}
 		if ( foundFormObjectFlag )
 			break;
 		// Need to clear since this is a return variable. Otherwise last assigned object will return.
 		formToGetDrillDetails = "";
 	}
 	// return the exact form object.
 	return formToGetDrillDetails;
 }
function Identifycombovalue(comboID,routinename)
{
		// Get the option from the combo box
	var comboBox = FragmentUtil.getElement(comboID);

	if (comboBox) {
		var selectedIndex = comboBox.selectedIndex;
		var value = comboBox.options[ selectedIndex ].getAttribute( "value" );
        var pos = routinename.indexOf("(");
        routinename = routinename.substring(0,pos);
        window[routinename](value); //function name available as variable so needs to call the function using this method
        	
	}
}
 
/**
*	Function to update field values on all forms(if exists) which are present in the current fragment.
*   This function helps to update only for the forms which are belongs to current fragment though
*	same form named forms exists in the document.
*
*   fieldName - which elements value has to be updated.
*/

function updateFormFieldValueOnCurrentFragment(field_Name, field_Value)
{
	// Get current fragment name.
	var currentFragment = Fragment.getCurrentFragmentName();
	// Get object of current fragment.
	var currentFragmentObject = window.document.getElementById(currentFragment);
	// Extract all forms exists in current fragment.
	var currentDivObjects = currentFragmentObject.getElementsByTagName("form");
	// Loop through all forms to update field value.
	for ( var h =0; h < currentDivObjects.length; h++)
	{
		for ( var h =0; h < currentDivObjects.length; h++)
		{
			var fieldName = currentDivObjects[h].elements[field_Name];
			// Update field value if and only the field exists.
			if (fieldName!=null)
			{
				// Not quit when to found the field name, because they may be chance of having same fields in
				// more forms. example: associate version in a fragment.
				fieldName.value = field_Value;
			}
		}
	}
}
/**
*	Only to allow numeric values in a particular textbox/textarea fields
*
*/
function isNumberKey(evt,allowed)
{
     var charCode = ((evt.which)>=0) ? evt.which : evt.keyCode;
	 if(allowed)
	 {
	 for(i=0;i<allowed.length;i++)
	 {
		 if(charCode == allowed.charCodeAt(i))
		 {
			return true;
		 }
	 }
	 }
     if (charCode > 31 && (charCode < 48 || charCode > 57))
     {
     return false;
     }
     return true;
}

// Redirect the change password screen for ARC-IB
function pwdSuccess(contextRoot)
	{
		var _explorerURL = location.protocol + "//" + location.host + "/" + contextRoot +"/modelbank/unprotected/loggedout_passwd.jsp?logged_out=yes";
		window.location = _explorerURL;
	}

//to block backspace key functionality on noinput dropdown fields
function doNothingOnbackspace(e)
	{
		if(e.keyCode == 8) 
		{
			e.returnValue = false;
		}	
	}

/**
Invoke help text specific to field when pressing the keys shift+alt+1
*/
function invokeHelp(evt,fieldName)
{
  var charCode = (evt.which) ? evt.which : event.keyCode;
  if(evt.altKey && charCode == 49)
  {
    help(fieldName);
  }
}
// to block ENTER key functionality
function doNothingOnEnterKey(event){
  var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if( keyCode == 13 ) {
    FragmentUtil.killEvent(event);
    if (event.stopPropagation) { //FX
      event.preventDefault();
    }
    else{
      event.returnValue = false;
    }
  }
}
function testmouse(event)
{
if(currentDropDown_GLOBAL)
	{
	if (currentDropDown_GLOBAL=="fqu:nextDate")
		{
		fqu_close(freqDropDown_GLOBAL);
		}
	else
		{
		hidePopupDropDown(currentDropDown_GLOBAL);
		}
	}
}
//Cancel Button-Control moves back to parent screen for ARC-IB 
function doVersioncancel()
	{
		var fragmentName = window.Fragment.getCurrentFragmentName();
		var tdElement= document.getElementById(fragmentName);
		var divElements= tdElement.getElementsByTagName("div");
		
			for (i=0; i<divElements.length; i++)
			{
				divElements[i].style.display="none";
			}
	}