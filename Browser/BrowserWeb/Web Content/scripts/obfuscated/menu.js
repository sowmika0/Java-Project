/** 
 * @fileoverview  Deals with the javascript functions related to the displaying and manipulating of a menu
 *
 */

/**
 * Used to stop the automatic sign off when changing company
 */
var noSignOff = '';
var recordHistory_GLOBAL = false;
var recordHistroyDataArray_GLOBAL = {};

/**
 * Return the child element of a clicked menu item
 * @param {HTMLElement} eSrc The parent element.
 * @param {String} sTagName The tag type to match, the first match will be returned.
 * @return {HTMLElement} The first child element that matches the given tagName, or 'false' if no matching child element exists.
 */
function GetChildElem(eSrc,sTagName)
{
	var cKids = eSrc.childNodes;
	for (var i=0;i<cKids.length;i++)
	{
		if (sTagName == cKids[i].tagName)
		{
			return cKids[i];
		}
	}
	return false;
}

/**
* Process menu header text
* @param {headerText} the header text which displays information about this menu.
* @return {void}
*/
function processMenuHeaderText(headerText)
{
	// Pass request to help servlet to display header text information for menu items.
	// Specially for menu styles POPDOWN, DEFAULT.
	var fragment = Fragment.getCurrentFragment();
	// Send request only if fragment exists.
	if ( fragment.id != "" )
	{
		FragmentEvent.raiseEvent(FragmentEvent.TAB_SELECTED, headerText, Fragment.getCurrentFragment()); 
	}
}

/**
 * Exapnds or contracts a menu.
 * @param {event} evt The event object that invokes this function.
 * @return {void}
 */ 
function ProcessMouseClick(evt)
{
	evt=getRealEvent(evt);
	var eSrc = getEventSource(evt);
	var imgObject = null;
	var skin = getSkin();

	// If the event has bubbled up, then throw the source element up to the node in the menu
	// Bubble up from image to span, from span to li.  Twiddle the arrow image.

	if ("IMG" == eSrc.tagName)
	{
		imgObject = eSrc;
		eSrc = eSrc.parentNode;
	}

	if ("SPAN" == eSrc.tagName)
	{
		// Find image object within span object
		if ( imgObject == null )
		{
			images = eSrc.getElementsByTagName( "IMG" );
			if ( images != null)
			{
				imgObject = images.item(0);
			}
		}
		eSrc = eSrc.parentNode;
	}



	if ("LI" == eSrc.tagName && "clsHasKids" == eSrc.className && (eChild = GetChildElem(eSrc,"UL")))
	{
		if ( eChild.style.display == "block" )
		{
			eChild.style.display = "none";		// Hide

			if ( imgObject != null )
			{
				// Change the menu arrow
				imgObject.src = "../plaf/images/" + skin + "/menu/menu_down.gif";
			}
		}
		else
		{
			eChild.style.display = "block"; 	// Show

			if ( imgObject != null )
			{
				// Change the menu arrow
				imgObject.src = "../plaf/images/" + skin + "/menu/menu_right.gif";
			}
		}
	}
}


/**
 * Expand all menus with a particular tag and class name
 * @param {String} tag The element tag name eg 'td', or 'table'.
 * @return {void}
 */  
function expandMenuTag(tag)
{
	// Some browsers (e.g. Netscape) don't have "document.all.tags", so check first
	if ( document.getElementsByTagName )
	{
		cElems = document.getElementsByTagName( tag );
	}
	else if ( document.all )
	{
		cElems = document.all.tags( tag );
	}

	var iNumElems = cElems.length;
  
	for (var i=1;i<iNumElems;i++)
	{
		if ( cElems[i].className == "expand" )
		{
  			cElems[i].style.display = "block";
		}
	}
}


/**
 * Initialises a menu
 * @return {void}
 */  
function initMenu()
{
	// If there is a command line on the form then set the focus on to it
	var commandLine = document.getElementById("commandValue");
	if ( commandLine != null )
	{
		commandLine.focus();
	}

	// Display the status bar information
	setWindowStatus();

	// Expand any menus that are marked accordingly
	expandMenuTag('UL');
}


/**
 * Close the main menu window
 * @return {void}
 */
function closeMainWindow()
{
	if (noSignOff == 1)
	{
		noSignOff = '';
	}
	else
	{
		// Log-off when main window closed
		signOffAndCloseWindow();
	}
}


/**
 * Sign the user off from the main menu - keeping the existing window for the result
 * @return {void}
 */
function signOff()
{
	// Check if user is logged in before performing a sign off command
	var user = getUser();

	if ( user != "" )
	{
		buildUtilityRequest("",user,"generalForm","_parent","0YXhb}aCXYXX1}6","","","","");
		noSignOff = 1;
	}
}


/**
 * Display the sign on screen for a new user - after the user has signed off
 * @return {void}
 */
function signOn()
{
	// Check if user is logged in before performing a sign off command and showing login page
	var user = getUser();

	if ( user != "" )
	{
		// get the 'generalForm' reference, adjusting for Fragment name as necessary
		var genForm = FragmentUtil.getForm("generalForm");

		genForm.command.value = "signon";

		buildUtilityRequest("",user,"generalForm","_parent","0YXhb}aCXYXX1}6","","","","");
	}
}


/**
 * Sign the user off by closing the existing window
 * @return {void}
 */ 
function signOffAndCloseWindow()
{
	// Check if user is logged in before performing a sign off command
	var user = getUser();

	if ( user != "" )
	{
		// Flag for no sign off -  we are signing off so don't want to 
		// do it again when window is unloaded
		noSignOff = 1;

		// Create an invisible window for submitting the request
		var reqType = "0YXhb}aCXYXX1}6";
		var routineArgs = user;
		var unlock = "";
		var closing = "Y";
		var title = "Logoff";
		var pwprocessid = "";

		// Display a hidden popup for the command and submit it
		popupHiddenSubmitWindow( reqType, routineArgs, unlock, closing, title, pwprocessid );
	}
}


/**
 * Calls a utility request to change company
 * @param {String} NewCompanyID The ID of the company to switch to.
 * @return {void}
 */ 
function doloadCompany(NewCompanyID)
{
	
	buildUtilityRequest("}XCy}t0Ce}=Kt6a",NewCompanyID,"generalForm","NEW","","","","","");
	// Flag for no sign off
	noSignOff = 1;
	
}

// Implementation of User Roles functionality in T24 ; Calling the utility request to switch Roles 
function doSwitchRole(NewRole)
{
	
	buildUtilityRequest("}XCy}t0Cb}yY",NewRole,"generalForm","NEW","","","","","");
	// Flag for no sign off
	noSignOff = 1;
	
}

/**
 * Create a unique window name in the form "window_<number 1 to 1000>"
 * @return {String} A new randomly generated window name in the form "window_<number 1 to 1000>"
 */ 
function getWindowName()
{
	randomNumber = Math.random();
	randomNumber = Math.round( randomNumber * 1000 );
	return( "window_" + randomNumber );
}


/**
 * Deprecated, use signOff() instead.
 * Calls the signOff() method.
 * @return {void}
 * @deprecated
 */ 
function signoff(){
	signOff();
}



/**
 * Given the request to run, try and match a target for it.
 * @param {String} request The request to run.
 * @return {String} The name of the target fragment the request should be run against.
 */  
function getCompositeScreenTarget( request)
{
	// Check that we have a request first...
	if( request == null || request == "undefined" )
	{
		return "";
	}
	
	// NEW means ALWAYS appear in a new window, regardless!
	if ( request == "NEW" )
	{
		return '';
	}
	
	// Check if this frame/fragment has a "Replace All" attribute.  If so then any action should
	// replace the whole parent composite screen that this frame/fragment resides in.
	var replaceCompScreen = checkReplaceCompositeScreen();
	
	if ( replaceCompScreen != "" )
	{
		return replaceCompScreen;
	}
	
	// Get the command entity and type
    var commandDetails = getCommandDetails( request);	
	var	requestType = commandDetails.commandType;
	request = commandDetails.commandEntity;

	// Get array of targets and corresponding commands i.e. MY.ENQUIRY_FrameName123455667
	var targetList = FragmentUtil.getCompositeScreenTargets().split("|");
	currentFragment_GLOBAL = "";            
	var commmand = "";
	var target = "";
	var allTarget = "";
	var enqTarget = "";
	var cosTarget = "";		
	var noenqTarget = "";
	
	// Go through the list matching a target for a given command.
	for( i = 0; i < targetList.length; i++)
	{	
		var currentItem = targetList[i];
		// If there is something in the target...
		if( currentItem )
		{
			// Command can be MY.ENQUIRY or MY.APPLICATION or MY.UTILITY.RTN etc Anything specified on the ITEMS field in EB.COMPOSITE.SCREEN.
			command = currentItem.split("_")[0];
			// If the command matches the request then we have landed on our target so break the loop.
			if( command == request )
			{
				// Target is the name of the frame the command should run against.
				target = currentItem.split("_")[1];
			}
			// If command is 'ALL' then keep a tab on it
			if( command == "tyy")
			{
				allTarget = currentItem.split("_")[1];
			}
			// If command is 'ENQ' then keep a tab on it
			if( command == "Y6l")
			{
				enqTarget = currentItem.split("_")[1];
			}
			// If command is 'COS' then keep a tab on it
			if( command == "e}X")
			{
				cosTarget = currentItem.split("_")[1];
			}
			// If command is 'NOENQ' then keep a tab on it
			if( ( command == "6}Y6l") && ( requestType != "Y6l" ) )
			{
				noenqTarget = currentItem.split("_")[1];
			}
		}	
	}
	// The actaull target to return
	var returnTarget = "";
	
	if( target != "")
	{
		returnTarget = target;	
	}
	else
	{
		switch ( requestType)
	    {
	        case "Y6l":
	        	if( enqTarget != "") 
	        	{
	            	returnTarget = enqTarget;
	        	}
	        	else
	        	{
	        		returnTarget = allTarget;
	        	}
	        	if(request.indexOf("0YCKbYR1Y5") == 0)
	        	{
	        		returnTarget = "";	//Delivery preview should be always in new window.
	        	}	    
	        	break; 
	        	
	        case "e}X":
	        	if( cosTarget != "")
	        	{
					returnTarget = cosTarget;	             	            
	        	}
	        	else
	        	{
	        		returnTarget = allTarget;
	        	}
	            break;
	        
	         default : 
	         	if ( noenqTarget )
	         	{
	         		returnTarget = noenqTarget;
	         	}
	         	else
	         	{
	        		returnTarget = allTarget;
	         	}
	        	break;
	    }   
	}

 	// If there is no target specified then target is new window
	if( returnTarget == "")
	{
		returnTarget = "NEW";	
	}
	return returnTarget;
}

//////////////////////////////////////////////////////////////////////
//
//    POPDOWN Menu functions
//
//////////////////////////////////////////////////////////////////////

/**
 * Displays the POPDOWN menu.
 * @param {String} menu_hdr The menu <li> element
 * @param {String} menu_num The position of the menu in the list of menu items
 * @return {void}
 */
function menu_popup(menu_hdr, menu_num) {

    var menu_pane = document.getElementById("menu" + menu_num);
   
    var left_pos = menu_hdr.offsetLeft;
    var top_pos = menu_hdr.offsetTop + menu_hdr.clientHeight;

    var safety_index = 0;
    var temp_elem = menu_hdr;
    while (temp_elem.offsetParent) {
        temp_elem = temp_elem.offsetParent;
        left_pos += temp_elem.offsetLeft;
        top_pos += temp_elem.offsetTop;
        // So we don't get stuck in an infinite loop.
        if (safety_index++ > 100) break;
    }
    
    menu_pane.style.left = left_pos;
    menu_pane.style.top = top_pos;
    menu_pane.style.visibility = "visible";
    
}

/**
 * Hides the POPDOWN menu when the user leaves the menu.
 * @param {String} e The event that invoked this function.
 * @param {String} menu_hdr The menu <li> element
 * @param {String} menu_num The position of the menu in the list of menu items
 * @return {void}
 */
function menu_leave(e, menu_hdr, menu_num) {
    var menu_pane = document.getElementById("menu" + menu_num);
    var toElement = e.relatedTarget ? e.relatedTarget : e.toElement;

    if (! menu_hdr.contains(toElement)) {
        menu_pane.style.visibility = "hidden";
    }

}

/**
 * Hides the POPDOWN menu when the user clicks on an item in the menu.
 * @param {String} menu_num The position of the menu in the list of menu items
 * @return {void}
 */
function menu_popdown(menu_num) {
    var menu_pane = document.getElementById("menu" + menu_num);
    menu_pane.style.visibility = "hidden";
}

/** 
 * Called when the popdown menu is loaded (from window.xsl)
 * and when a menu item is clicked on.
 * Initialises the history listeners
 * @param {String} type The menu type
 * @param {String} [target] The menu target
 * @return {void}
 */
function menu_history(type, target) {
	if(window.BrowserHistory)
	{
		//var dataArray = {};
		recordHistroyDataArray_GLOBAL.menutarget = target;
		recordHistroyDataArray_GLOBAL.menutype = type;
		// Store the window name for the target
		recordHistroyDataArray_GLOBAL.windowname = window.name;
		
		if (type == "##INIT##")
		{
			// Initialise & Record the history
			BrowserHistory.initialize("popmenu", recordHistroyDataArray_GLOBAL);
		}
		else
		{
			recordHistory_GLOBAL = true;
		}
	} 
}

 /** 
  * Check whether the current request should replace all of the
  * composite screen rather than just the frame we are in. 
  * @return {String} The name of the composite screen to replace.
  */
function checkReplaceCompositeScreen()
{
	// Does this frame need to replace it's parent composite screen ?
	var replaceAll = getFormFieldValue( "generalForm", "WS_replaceAll" );

	if ( replaceAll == "yes" )
	{
		// Get the parent composite screen name so we can replace it with this command
		var replaceAllCos = getFormFieldValue( "generalForm", "WS_parentComposite" );
		if (replaceAllCos =="")
		{
			replaceAllCos = "_parent";
		}
		return replaceAllCos;
	}
	else
	{
		// No replace all, so just follow the usual composite screen targets then
		return "";
	}
}
function ProcessKeyPress(evt)
{
   if(evt.keyCode == 13 )
   {
	  ProcessMouseClick(evt);
   }
}