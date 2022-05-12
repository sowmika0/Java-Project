/** 
 * @fileoverview Utility methods for composite screens.
 *
 */
var coordinatesId_GLOBAL = "";
 
/**
 * Sets the size and positions of a composite screen, in addition the ID of
 * the coordinates that will be sent back to the server
 * @param {int} windowTop The top position of the window, in pixels
 * @param {int} windowLeft The left position of the window, in pixels
 * @param {int} windowWidth The width of the window, in pixels
 * @param {int} windowHeight The height of the window, in pixels
 * @param {String} coordinatesId Window object to check
 * @return {void}
 * @type 
 */
 function initCompositeScreen(windowTop, windowLeft, windowWidth, windowHeight, coordinatesId)
{
	 //Do not resize if parameter value is specifically set to NO  
	if (getFieldValue("allowResize") != "NO")
	{
		// If this composite screen is a Tabbed Screen then we don't
		// want to resize the frame if we are in a composite screen
		if ( ( (coordinatesId == _COS_TABS_) || (coordinatesId == _TABS_) ) && FragmentUtil.isCompositeScreen() )
		{
			return;
		}

		// Check if the window should be maximized
		if ( getFieldValue("maximize") == "true")
		{
			maximizeWindow();
		}
		else
		{
		// set the name of the composite screen so it can be store the window positions in the server
			coordinatesId_GLOBAL = coordinatesId;
			window.resizeTo(windowWidth, windowHeight);
			window.moveTo(windowLeft, windowTop);
		}
		
		// attach beforeUnloadCompositeScreenWindow to the onbeforeunload event on the window
		// as the frameset onunload event will return incorrect coordinates.
		
		//Firefox compliant
		if (!isInternetExplorer() && window.attachEvent)	//avoid this for IE
		{
			window.attachEvent("onbeforeunload", beforeUnloadCompositeScreenWindow);
		}
	}
}

/**
 * Sends composite coordinates to the server.
 * @return {void}
 */
function beforeUnloadCompositeScreenWindow(theUser)
{	
	//set from initCompositeScreen
	var cosRoutineArgs = coordinatesId_GLOBAL + "::" + getWindowCoordinates();

	var app = "";
	var transId = "";
	var reqType = _NO__REQUEST_;
	var closing = "Y";
	var title = "Unload";
	var unlock = app + " " + transId;
	var pwprocessid = "";
	// this needs to be set for the request to be valid
	var user = theUser;

// build up request parameters
	var params  = "command=globusCommand";
	    params += "&requestType=" + reqType;
	    params += "&routineArgs=" + cosRoutineArgs;
	    params += "&user=" + user;
	    params += "&unlock=" + unlock;
	    params += "&closing=" + closing;
        params += "&windowName=" + name;
		
	if (top.opener) {
		top.opener.sendHiddenCommand(params);
	}	
	else {
		sendHiddenCommand(params);
	}
}