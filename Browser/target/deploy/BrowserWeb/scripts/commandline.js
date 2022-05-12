/** 
 * @fileoverview Deals with the javascript functions used in processing command line requests
 *
 * @author XXX
 * @version 0.1 
 */
//Identify Application level autoLaunch enquiries
	var isAppAutoLaunch = '';
/**
 * Checks the changed fields on specific window
 * @param {Window} wnd Window object to check
 */
function WindowChangedFields(wnd)
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL, wnd);

	// Loop round all the deal fields checking for changes
	for ( var i = 0; i < appreqForm.elements.length; i++ )
	{
		var field = appreqForm.elements[i];
		var fieldName = field.name;
		var oldValue = field.getAttribute("oldValue");
		var newValue = field.value;
		var fieldInError = field.fieldError;

		// Only want to process fields with a name that starts with "fieldName:"
		var pos = fieldName.indexOf("fieldName:");

	 	if ( pos == 0 ) 
	 	{
	 		if ( (( oldValue != null ) && ( oldValue != "undefined" ) && ( oldValue != newValue ))  || ( fieldInError == "Y" ))
		 	{	
		 		var changedFields = appreqForm.changedFields.value;
		 		
		 		if ( changedFields )
		 		{
					appreqForm.changedFields.value = changedFields + " " + fieldName;
				}
				else
				{
					appreqForm.changedFields.value = fieldName;
				}
			}

		}
	}
}

/**
 * Prompts the user if fields have changed on specific window
 * @param {Window} wnd Window object to check
 * @return 'true' or 'false' if changes should be saved or not
 * @type String
 */
function SaveWindowChanges(wnd)
{
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	
	if( currentForm_GLOBAL == "" )
	{
		setCurrentForm( "appreq" );
	}
	
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL, wnd);

	var retval = "true";
	
	if ( appreqForm != null)
	{
		var sparam = appreqForm.savechanges.value;
		if(sparam=="YES")
		{
			var cstatusObj = appreqForm.ContractStatus;
			
			if ( ( cstatusObj != null ) && ( cstatusObj != "undefined" ) )
			{
				var cstatus = cstatusObj.value;
			
				WindowChangedFields(wnd);
				if((appreqForm.changedFields.value != "") || (cstatus=="CHG"))
				{
					appreqForm.changedFields.value = "";
					retval =  confirm(appreqForm.SaveChangesText.value);
				}
			}
		}
	}
	return retval;
}
/**
 * TODO
 * @param {TODO} wnd Window object to check
 * @return a function or 'true'
 * @type Object
*/
function checkWindowChanges(cmdname)
{
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");
	// In case there is no general form there is no point doing this.
	if( genForm == null || genForm == "undefined" || genForm == "")
	{
		return true;
	}
	
	var sparam = genForm.savechanges.value;

	var targt = getCompositeScreenTarget(cmdname);
	if(sparam=="YES")
	{
		if((targt!="") && (targt!="NEW"))
		{
			var wnd = FragmentUtil.getFrameWindow(targt);

			// get the 'appreq' form reference, adjusting for Fragment name as necessary
			var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL, wnd, targt);

			if(appreqForm!=undefined)
			{
				return SaveWindowChanges(wnd);
			}
			else 
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	else
	{
		return true;
	}
}


/**
 * Returns command structure details
 * @param command String The given command
 * @return target String An object representing parts of the command
 * @type String
 */  
function getCommandDetails( command)
{
	commandType = "";
	commandEntity = "";
	// Get the tokens split by a space
	commandTokens = command.split(" ");
	// Count them
	numberOfTokens = commandTokens.length;
	// Get a command type i.e. ENQ APP COS etc
	// Get a command entity i.e CUSTOMER, STMT.ENTRY,ACCOUNT etc.
	if( numberOfTokens == 1)
	{
		commandType = _APP_;
		commandEntity = command;	    
	}
	else if( numberOfTokens > 1)
	{
		if( isT24Prefix( commandTokens[0]))
		{
			commandType = commandTokens[0];
			commandEntity = commandTokens[1];
		}
		else if( validFunctionCheck( commandTokens[1]))
		{
			commandType = _APP_;
			commandEntity = commandTokens[0];
		}
	}
	// Return an object with 2 parts
	return { commandType: commandType, commandEntity: commandEntity};
}

/**
 * Returns true if the given 'prefix' is a valid T24 prefix. I.e. API ENQ TAB WSP COS MENU PW QUERY UTIL 
 * @param command String The given command
 * @return target String An object representing parts of the command
 * @type String
 */  
function isT24Prefix( prefix)
{
	switch( prefix)
	{
		case _API_:
		case _ENQ_:
		case _TAB_:
		case _WSP_: 
		case _COS_:
		case _MENU_:
		case _PW_:
		case _QUERY_:
		case _UTIL_:
			return true;
		default : 
			return false;   
	}
}

/**
 * Runs a check to see if the typed command is a valid expression.
 * This routine should be used to add extra validation rules if required.
 * @param {String} cmd The command string 
 */
function validateCommand( cmd)
{
	var validatedCmd = cmd;
	// If there is a comma in a command, check if the first part of the command does not 
	// contain the following reserved keywords: API, ENQ, TAB, WSP, COS, MENU, PW and QUERY. 
	var tokenList = cmd.split(",");
	// Is there a comma in the command? should produce two tokens ...
	if( tokenList.length > 1 )
	{
		switch( tokenList[0] )
		{
			case "API":
			case "ENQ":
			case "TAB":
			case "WSP": 
			case "COS":
			case "MENU":
			case "PW":
 			case "QUERY":
 			case "UTIL": 
				validatedCmd = tokenList[0] + tokenList[1];
				// get the form reference, adjusting for Fragment name as necessary
				var clForm = FragmentUtil.getForm("commandLineForm");
				// Set the validated command. Updates the form so that user see's how the command has changed.
				clForm.commandValue.value = validatedCmd; 
				break;
			default : break;   
		}
	}
	
	return validatedCmd;
}
/**
 * To run a menu command
 * @param {String} wnd Window object to check 
 */
function docommand(cmdname)
{
	var browser = navigator.appName.substring ( 0, 9 );	// Get the browser name
	
	if(!FragmentUtil.checkWindowChanges(cmdname))
	{
		return;
	}
	
	cmdname = stripSpacesFromEnds(cmdname);
	cmdname = validateCommand( cmdname);
	
	updatedFormsField( "WS_initState", cmdname );	
	
	// Check whether this is a command line enquiry or not
	var prefix = cmdname.substring(0,4);
	var queryPrefix = cmdname.substring(0,6);
	var menuPrefix = cmdname.substring(0,5);
	var startChar = cmdname.substring(0,1);
	
	if ( (prefix == _API_ + " ")||(cmdname == _API_))
	{
		//API processing
		doapi(cmdname);
		return;
	}
	if ( prefix == _TAB_ + " ")
	{
		// Process the TAB
		dotab(cmdname);
		return;
	}
	if ( prefix == _WSP_ + " ")
	{
		// Process the worksapce
		doworkspace(cmdname);
		return;
	}

	if ( prefix == _ENQ_ + " " )
	{
		// If this is an enquiry on an application screen that has an editable transactionId box,
		// then set the dropdown field so we can pick from the result to that id box
		var dropfield = '';
		if(isAppAutoLaunch)
		{
			dropfield = '';
			isAppAutoLaunch = '';
		}
		else
		{
			dropfield = getEnquiryDropfield();
		}
	
		// Process the enquiry
		doEnquiryRequest(_SELECTION_, cmdname, dropfield, "", "", "", cmdname, "", _DISPLAY_);
	}
	else if ( prefix == _COS_ + " ")
	{
		// Process the composite screen
		docompositescreen(cmdname);
	}
	else if ( ( menuPrefix == _MENU_ + " ") || ( startChar == "?" ) )
	{
		// Process the menu
		domenu(cmdname);
	}
	else if ( queryPrefix == _QUERY_ + " ")
	{
		// Run the enquiry with no selection criteria
		doenqRun(cmdname);
	}
	else if ( prefix == _UTIL_)
	{
		// Get the routine name to run
		var routineName = cmdname.split(" ")[1];
		// Clean up the command to just leave the arguments supplied
		var args = cmdname.replace( _UTIL_ + " ", "");
		args = args.replace( routineName, "");
		// Remove any leading spaces from args.
		args = args.replace(" ", "");
		// Send utility request.
		buildUtilityRequest(routineName, args, "", "", "", "", "", "", "");
	}
	else if ( ( menuPrefix == "https" ) || ( menuPrefix == "HTTPS" ) )
	{
		 createWebWindow( cmdname );
	}
	else if ( ( prefix == "http" ) || ( prefix == "HTTP" ) )
	{
		 createWebWindow( cmdname );
	}
	else if ( ( menuPrefix == "file:" ) || ( menuPrefix == "FILE:" ) )
	{
		 createWebWindow( cmdname );
	}
	else if ( ( prefix == "www." ) || ( prefix == "WWW." ) )
	{
		createWebWindow( "http://" + cmdname );
	}	
	else if ( ( prefix == "url " ) || ( prefix == "URL " ) )
	{
		var url = cmdname.substring(4, cmdname.length);
		docommand( url );
	}
	else if ( (prefix.substring(0,3) ) == _PW_ + " ")
	{
		createPwProcess(cmdname);		  
    }
	else
	{
		processGeneralCommand(cmdname,browser);
	}

}

/**
 * processes general command line parameters: e.g. Application and list commands
 * @param {TODO} wnd Window object to check
 */
function processGeneralCommand(cmdname,browser)
{
		if(cmdname=='' || cmdname == null)
		{
			// To Block the request with null command
			return false;
		}
		// Set the 3 command parameters in the form variables and get the number of arguments
		argsNumber = processCommandString( cmdname );

		// get the 'generalForm' reference, adjusting for Fragment name as necessary
		var genForm = FragmentUtil.getForm("generalForm");
		
		var keyIsFunction = '';
		var keyIsSpecialFunction = '';

		// Retrieve the 3 command parameters from the form variables for processing
		app = genForm.application.value;
		ftn = genForm.ofsFunction.value;
		key = genForm.transactionId.value;

		setApplicationAndVersion(app);

		// Check if the supplied function is valid
		validFunction = functionCheck(ftn);
		validSpecialFunction = specialFunctionCheck(ftn);

		if ((argsNumber > 2)  && (validFunction || validSpecialFunction)) //second agrs in command line is valid function then proceed
		{
		// Check if the assumed 'key' is actually a function if there are 3 arguments
			 keyIsFunction = functionCheck(key);
			 keyIsSpecialFunction = specialFunctionCheck(key);
		}
		
		if ( keyIsFunction || keyIsSpecialFunction )
		{
			// key is a function so just take the third parameter as the function
			argsNumber = 2;
			cmdname = cmdname.split(" ");
			cmdname = cmdname[0] + " " + cmdname[2];
			if (key != "L")
			{
				// set the function here if listing
				ftn = key;
				// so that listing box is brought up correctly
				key = '';
			}
		}
		
		// Check if we are replacing a window's content which contains a contract.  If so,
		// then run the unlock command as part of this next request
 		checkTargetContent( cmdname, "generalForm" );

		// Check for a sign off command or abbreviation, and sign on command
		if ( checkSignOffCommand( app ) )
		{
			signOff();
		}
		else if ( checkSignOnCommand( app ) )
		{
			signOn();
		}
		else if ( (ftn == "E")||(ftn=="L") )
		{
			processList(ftn,key,app,browser);
			
		}
		else if ( ( argsNumber == 2 ) && ( F3Check(cmdname) == false ) && ( !validFunction )  && ( !validSpecialFunction ) )
		{
			// Application with key
			genForm.ofsFunction.value = "X";	// Default to Input for existing record
			genForm.transactionId.value = ftn;	// Second line arg was the key, not a function
			genForm.ofsOperation.value = _BUILD_;
			buildUtilityRequest("",cmdname,"generalForm","",_OFS__APPLICATION_,"","250","250","");	
		}
		else if ( ( argsNumber == 3 ) && ( F3Check(cmdname) == false ) && ( validFunction ) )
		{
			// Application with function and key
			genForm.ofsOperation.value = _BUILD_;
			buildUtilityRequest("",cmdname,"generalForm","",_OFS__APPLICATION_,"","250","250","");
		}
		else
		{
			buildUtilityRequest(_OS__NEW__DEAL_,cmdname,"generalForm","","","","","","");
		}

		genForm.unlock.value = "";
}

/**
 * processes Exception list and Live list enquiries 
 * @param {TODO} wnd Window object to check
 */
function processList(ftn,key,app,browser)
{
		// get the 'generalForm' reference, adjusting for Fragment name as necessary
		var genForm = FragmentUtil.getForm("generalForm");

		// Remove any version name from the enquiry name for the exception and live lists
		appName = genForm.application.value;
		version  = genForm.version.value;

		//This is found in general.js.  We want to capture the name of the following window 
		// created to be used as the target in the the window created below.  
		setWindowVariableMarker("true");
		
		// Bring up the application first ..
		if ( ftn == "E" || ftn == "L" )
		{
			updatedFormsField( "WS_parent", "Y" );
		}

		// Run the command	
		docommand( app );
		
		updatedFormsField( "WS_parent", "" );
		
		// If we are in a composite screen get the target for the command and
		// save the target in case it is needed as the target for a dropdown.
		// If it's not a compoiste screen then we will have already saved the 
		// name of the parent window when the command was submitted
		var appTarget = getCompositeScreenTarget( app );

		if ( appTarget != ""  && appTarget != "NEW" )
		{
			storeWindowVariable( appTarget );
		}
			
		if ( ftn == "E" )
		{
			// Set the dropdown field so we can pick from the result to the id box on the
			// application screen after the user has selected their enquiry selection criteria
			dropfield = "transactionId";
			
			// Display the Exception list
			if ( key == "E" )
			{
				cmdname = _ENQ_ + " %" + appName;
				if(version!="")
				{
					cmdname = cmdname + "$NAU" + version;
				}
				else
				{
					cmdname = cmdname + "$NAU";
				}
				
				// Get enquiry selection form
				doEnquiryRequest(_SELECTION_, cmdname, dropfield, "", "", "", cmdname, "", _DISPLAY_);
			}
			else
			{
				cmdname  = _ENQ_ + " %" +appName;
				if(version!="")
				{
					cmdname = cmdname + "$NAU"+version;
				}
				else
				{
					cmdname = cmdname + "$NAU";
				}

				// Get enquiry results using the full selection criteria
				doenqDropfieldList( cmdname, dropfield );
			}
		}
		else if ( ftn == "L" )
		{
			// Set the dropdown field so we can pick from the result to the id box on the
			// application screen after the user has selected their enquiry selection criteria
			dropfield = "transactionId";

			// Display the Live list
			if ( key == "L" )
			{
				cmdname = _ENQ_ + " %" + app;
				// Get enquiry selection form
				doEnquiryRequest(_SELECTION_, cmdname, dropfield, "", "", "", cmdname, "", _DISPLAY_);
			}
			else
			{
				// Get enquiry results using the full selection criteria
				doenqDropfieldList( _ENQ_ + " %" + app, dropfield );
			}
		}
		
		if ( ftn == "E" || ftn == "L" )
		{
		  updatedFormsField( "WS_dropfield", "" );   
		}
		commandLine_windowName = "";
		
		//Now switch this functionality off.
		setWindowVariableMarker("false");
}

/**
 * Run a request for a tabbed screen and display the resultant tabs in a new window
 * @param {TODO} wnd Window object to check
 */
function dotab(cmdname)
{
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	cmdLength = cmdname.length;
	
	//if we don't have an argument after the tab then just get out
	if (cmdLength<5){
		return; 
	}
	
	// Check if the tabbed screen is going to a new window or not -
	// we need to know this to determine whether the screen should
	// be resized when displayed (as determined in the server code)
	//var target = getCompositeScreenTarget( cmdname );
	//target will always comes as NEW for this cmdname which causes to open all TAB related commands in the same window.
	//So don't pass the target argument to buildUtilityRequest().
	//var savedCompScreen = genForm.compScreen.value;
	
	//if ( target != "" )
	//{
		// Tabbed screen is going in to a frame
		//genForm.compScreen.value = "";
	//}
		
	buildUtilityRequest(_OS__GET__TAB__FRAMES_, cmdname,"generalForm","","","","","","");

	//genForm.compScreen.value = savedCompScreen;
}
/**
 * get the 'generalForm' reference, adjusting for Fragment name as necessary
 * @param {TODO} wnd Window object to check
 */
function doworkspace(cmdname)
{
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	cmdLength = cmdname.length;
	
	//if we don't have an argument after the tab then just get out
	if (cmdLength<5){
		return; 
	}
	
	// Always open a workpsace in a new window.
	var target = "";
	var savedCompScreen = genForm.compScreen.value;
	
	if ( target != "" )
	{
		// Tabbed screen is going in to a frame
		genForm.compScreen.value = "";
	}

	buildUtilityRequest(_OS__GET__WORKSPACE__XML_, cmdname.substring(4,99),"generalForm",target,"","","","","");

	genForm.compScreen.value = savedCompScreen;
}

/**
 * Checks if the supplied command is a SIGN.OFF command or abbreviation
 * @param {Boolean} wnd Window object to check
 * @return 'true' or 'false'
 */
function checkSignOffCommand( command )
{
	switch ( command )
	{
		case "SIGN.OFF" :
		case "SO"       :
		case "LO"       :
			return( true );

		default :
			return( false );
	}		
}

/**
 * Checks if the supplied command is a SIGN.ON command or abbreviation
 * @param {Boolean} wnd Window object to check
 * @return 'true' or 'false'
 */
function checkSignOnCommand( command )
{
	switch ( command )
	{
		case "SIGN.ON" :
		case "SON"     :
			return( true );

		default :
			return( false );
	}		
}

/**
 * check to see if the last section is equal to F3. If it is then return true
 * @param {Boolean} wnd Window object to check
 * @return found: 'true' or 'false'
 */
function F3Check(cmdname)
{		
	var found = false;
	var index = cmdname.lastIndexOf(" ");
	var operation = cmdname.substring(index+1,cmdname.length);
	
	if (operation==_F3_)
	{
		found = true;
	}
	else
	{
		found = false;
	}
	
	return found;
}

/**
 * Split the command string into application, function and transaction Id 
 * Set the values in the relevant form variables, returning the number of arguments
 * @param {String} wnd Window object to check
 * @return 'referenceCount'
 */
function processCommandString(cmdname)
{
	
	var moreReferences = true; 
	var referenceCount = 0;
	var tempString = stripSpacesFromEnds( cmdname );

	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	genForm.application.value = "";
	genForm.ofsFunction.value = "";
	genForm.transactionId.value = "";
	
	while (moreReferences == true)
	{
		var index = tempString.indexOf(" ");

		if (index!=-1)
		{
			// Found another argument
			referenceCount++;
			value = tempString.substring(0, index);
			// Set the form variable
			setCommandArg( referenceCount, value );
			tempString = stripSpacesFromEnds( tempString.substring(index+1,tempString.length) );
		}
		else
		{
			// Process the end of the command string
			referenceCount++;
			value = tempString;
			// Set the form variable
			setCommandArg( referenceCount, value );
			moreReferences = false;
		}		
	}

	return( referenceCount );
}



/**
 * Sets the specified form variable to the supplied value
 * @param {TODO} wnd Window object to check
 */
function setCommandArg( argNumber, argValue )
{	
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	if ( argNumber == 1 )
	{
		genForm.application.value = argValue;
	}
	else if ( argNumber == 2 )
	{
		genForm.ofsFunction.value = argValue;
	}
	else if ( argNumber == 3 )
	{
		genForm.transactionId.value = argValue;
	}
}

/**
 * extracts the application and version from a string and sets them appropriately
 * @param {TODO} wnd Window object to check
 */
function setApplicationAndVersion(application)
{
	// get the 'generalForm' reference, adjusting for Fragment name as necessary
	var genForm = FragmentUtil.getForm("generalForm");

	var index = application.indexOf(",");
	if (index!=-1)
	{
		genForm.application.value = application.substring(0,index);
		genForm.version.value = application.substring(index,application.length);
	}
	else
	{
		genForm.application.value = application;
		genForm.version.value = '';
	}
}

/**
 * Initialise the command line
 * @param {TODO} wnd Window object to check
 */ 
function initCmdLine()
{
	// get the form reference, adjusting for Fragment name as necessary
	var clForm = FragmentUtil.getForm("commandLineForm");
	
	// Set the focus on the command line field
	clForm.commandValue.focus();
}

/**
 * Run the command entered on the command line
 * @param {TODO} wnd Window object to check
 */
function docommandLine()
{
	// get the form reference, adjusting for Fragment name as necessary
	var clForm = FragmentUtil.getForm("commandLineForm");
	updatedFormsField( "WS_parentWindow", "" );
	windowTarget = "";		
	
	// Set-up parameters for a Globus command and submit it
	var cmdname = clForm.commandValue.value;

	// Check if there is anything to do
	if ( cmdname != "" )
	{
		docommand( cmdname );
	}
}


/**
 * Run an enquiry - this is also duplicated in menu.js. left for now but should be removed
 * @param {TODO} wnd Window object to check
 */
function doenq(cmdname)
{
	docommand(cmdname);
}



/**
 * Run an API command
 * @param {TODO} wnd Window object to check
 */
function doapi(cmdname)
{

	if (cmdname == _API_){
		return;
	}

	if (processAPIString(cmdname) == 2){	
		buildUtilityRequest("","","generalForm","",_API__REQUEST_,cmdname,"","","");
	}
}



/**
 * API split of command parameters
 * @param {TODO} wnd Window object to check
 * @return 'referenceCount'
 */
function processAPIString(cmdname)
{
	var moreReferences = true; 
	var referenceCount = 0;
	var tempString = stripSpacesFromEnds( cmdname );
	
	while (moreReferences == true)
	{
		var index = tempString.indexOf(" ");

		if (index!=-1)
		{
			// Found another argument
			referenceCount++;
			value = tempString.substring(0, index);
			// Set the form variable
			setAPIArg( referenceCount, value );
			tempString = stripSpacesFromEnds( tempString.substring(index+1,tempString.length) );
		}
		else
		{
			// Process the end of the command string
			referenceCount++;
			value = tempString;
			// Set the form variable
			setAPIArg( referenceCount, value );
			moreReferences = false;
		}		
	}
	
	return( referenceCount );
}


/**
 * Sets the specified form variable to the supplied value
 * @param {TODO} wnd Window object to check
 */
function setAPIArg( argNumber, argValue )
{
	if ( argNumber == 2 )
	{
		// get the 'generalForm' reference, adjusting for Fragment name as necessary
		var genForm = FragmentUtil.getForm("generalForm");

		genForm.apiArgument.value = argValue;
	}
}



/**
 * Run an enquiry and display result using full selection criteria
 * @param {TODO} wnd Window object to check
 */
function doenqRun(cmdname)
{
	// If this is an enquiry on an application screen that has an editable transactionId box,
	// then set the dropdown field so we can pick from the result to that id box
	var dropfield = getEnquiryDropfield();

	// Construct the enquiry command to ensure the enquiry is directed to the correct frame
	var queryPrefix = cmdname.substring(0,6);
	var theEnqName = cmdname.substring(6, cmdname.length);
	
 	if ( queryPrefix == _QUERY_ + " ")
 	{
		var displayLastEnquiry = cmdname.replace("QUERY","ENQ");
		var cmdname = displayLastEnquiry+(" NONE");
		doEnquiryRequest(_SELECTION_, cmdname, dropfield, "", "", "", _ENQ_ + " " + theEnqName, "generalForm", _DISPLAY_);
	}
	else
	{
		doEnquiryRequest(_RUN_, cmdname, dropfield, "", "", "", _ENQ_ + " " + theEnqName, "generalForm", _DISPLAY_);
	}
}
/**
 * Run a list enquiry (on the live, unauthorised, etc files)
 * and display result using full selection criteria in a new window
 * @param {TODO} wnd Window object to check
 */
function doenqList(cmdname)
{
	// If this is an enquiry on an application screen that has an editable transactionId box,
	// then set the dropdown field so we can pick from the result to that id box
	var dropfield = getEnquiryDropfield();
	
	doEnquiryRequest(_RUN_, cmdname, dropfield, "", "", "", "NEW", "generalForm", _DISPLAY_);
}

/**
 * Run a list enquiry (on the live, unauthorised, etc files)
 * and display result using full selection criteria in a new window with a dropfield specified
 * @param {TODO} wnd Window object to check
 */
function doenqDropfieldList(cmdname, dropfield)
{
	doEnquiryRequest(_RUN_, cmdname, dropfield, "", "", "", "NEW", "generalForm", _DISPLAY_);
}

/**
 * Display a selection criteria screen for searching an enquiry (on the live, unauthorised,
 * etc files) in a new window
 * @param {TODO} wnd Window object to check
 */
 function doenqSearch(cmdname)
{
	// If this is an enquiry on an application screen that has an editable transactionId box,
	// then set the dropdown field so we can pick from the result to that id box
	var dropfield = getEnquiryDropfield();
	
	doEnquiryRequest(_SELECTION_, cmdname, dropfield, "", "", "", "NEW", "generalForm", _DISPLAY_);
}

/**
 * Run an enquiry with an action
 * Action "back" uses previous enquiry selection criteria
 * @param {TODO} wnd Window object to check
 */
 function doenqAction(enqname, action)
{
	doEnquiryRequest(action, enqname, "transactionId", "", "", "", "NEW", "generalForm", _DISPLAY_);
}

/**
 * Load up a composite screen via OS.GET.COMPOSITE.SCREEN.XML
 * @param {TODO} wnd Window object to check
 */
function docompositescreen(cmdname){

	buildUtilityRequest(_OS__GET__COMPOSITE__SCREEN__XML_,cmdname,"generalForm","","","","","","");

}

/**
 * Load up a menu via OS.GET.MENU.XML
 * @param {TODO} wnd Window object to check
 */
function domenu(cmdname)
{
	var menuCmd = cmdname;
	var startChar = cmdname.substring(0,1);
	//if from toolbar command and from compositescreen then get the target frame
	if ((getnoNewWindow()) && (FragmentUtil.isCompositeScreen()))
	{
				var menuTarget = getCompositeScreenTarget(cmdname);
	}	
	if ( startChar == "?" )
	{
		menuCmd = cmdname.substring(1, cmdname.length );
		menuCmd = stripSpacesFromEnds(menuCmd);
		menuCmd = _MENU_ + " " + menuCmd;
	}
	//if target frame is already set, pass it as window name in the request
	if (menuTarget)
	{
		buildUtilityRequest(_OS__GET__MENU__XML_,menuCmd,"generalForm",menuTarget,"","","","","");
	}
	else
	{
		buildUtilityRequest(_OS__GET__MENU__XML_,menuCmd,"generalForm","","","","","","");
	}
}

/**
 * Get the name of a possible dropfield for an enquiry
 * @param {TODO} wnd Window object to check
 * @return 'dropfield'
 */
function getEnquiryDropfield()
{
	// 
	var dropfield = "";

	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	
	if ( appreqForm != null )
	{
		var transId = appreqForm.transactionId;
		
		if ( ( transId != null ) && ( transId.type != "hidden" ) )
		{
			dropfield = "transactionId";
		}
	}
	
	return( dropfield );
}

/**
 * Process any new commands - each in a new window
 * @param {TODO} wnd Window object to check
 */
function processNewCommands( commands )
{
	// Get each command separated by a "_" and run docommand
	var cmd = "";		
	var moreCommands = true; 
	var tempString = commands;

	// Loop round all commands calling docommand
	while (moreCommands == true)
	{
		var commandIndex = tempString.indexOf("_");
		isAppAutoLaunch = '';
		if (commandIndex != -1)
		{
			// Found another command
			cmd = tempString.substring(0, commandIndex);
			
			var utilityRoutineIndex = cmd.indexOf("##UTILITY.ROUTINE##");
			var contextEnquiryIndex = cmd.indexOf("##CONTEXT.ENQUIRY##");

			if (utilityRoutineIndex != -1)
			{
				// This command is a UTILITY ROUTINE
				doUtilityCommand( cmd );
			}
			else if (contextEnquiryIndex != -1)
			{
				// This command is a Context Enquiry
				doContextEnquiryCommand( cmd );
			}
			else
			{
				//For ENQ type commands to be identified as AutoLaunch enquiries
				isAppAutoLaunch = 1;
				// Process a 'normal' command-line type command
				docommand( cmd );
			}

			// Move on to next command and target
			tempString = tempString.substring( commandIndex + 1, tempString.length );
		}
		else
		{
			if ( tempString.length > 0 )
			{
				if ( tempString.indexOf("_") == -1 )
				{
					// There's another command in the string but so "_" so add one
					tempString = tempString + "_";
				}
			}
			else
			{
				// End of the commands
				moreCommands = false;
			}
		}		
	}
}

/**
 * TODO
 * @param {TODO} wnd Window object to check
 */
function doUtilityCommand( utiltiyCommand )
{
	// Get each parameter separated by a ":" and run buildUtilityRequest
	var cmdType = "";		
	var routineName = "";
	var title = "";
	var args = "";
	
	var tempString = utiltiyCommand;
	
	// Pull Out the Type - should be "##UTILTIY.ROUTINE##"
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		cmdType = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// Pull Out the Utility Request Name
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		routineName = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// Pull Out the Window Title
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		title = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// The Utility Request Arguments should be left
	args = tempString;

	// We can now invoke the Utiltiy Routine Request
	buildUtilityRequest(routineName, args, currentForm_GLOBAL, "", "", title, "", "", "");
}

/**
 * TODO
 * @param {TODO} wnd Window object to check
 */
function doContextEnquiryCommand( enquiryCommand )
{
	// Get each parameter separated by a ":" and run doContextEnquiry
	var cmdType = "";		
	var contextEnqId = "";
	var enqName = "";
	var fieldName = "";
	var fieldValue = "";
	
	var tempString = enquiryCommand;
	
	// Pull Out the Type - should be "##CONTEXT.ENQUIRY##"
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		cmdType = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// Pull Out the Context Enquiry Name
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		contextEnqId = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// Pull Out the Enquiry Name
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		enqName = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// Pull Out the Field Name
	var paramIndex = tempString.indexOf(":");
	if (paramIndex != -1)
	{
		fieldName = tempString.substring(0, paramIndex);
		// Move on to next command and target
		tempString = tempString.substring( paramIndex + 1, tempString.length );
	}
	
	// The Context Enquiry Field Value should be left
	fieldValue = tempString;

	var cmd = contextEnqId + "_" + enqName + "_" + fieldName + "_" + fieldValue;
	
	// We can now invoke the Utiltiy Routine Request
	doContextEnquiry( cmd );
}

/**
 * Process workflow support in browser SAR-2005-09-27-0005
 * @param {TODO} wnd Window object to check
 */
function createPwProcess(cmdname)
{
	buildUtilityRequest(_OS__RUN__PW__PROCESS_,cmdname,"generalForm","","","","","","");
}

/**
 * Check the contents of the target destination before we run the current command
 * to see if we should unlock the current frames content
 * @param {TODO} wnd Window object to check
 */
function checkTargetContent( 
			cmdname, 			// The command we are about to run
			formName )			// The form we are about to run the command from

{
	// If the new command is going to an existing frame, then check if a contract
	// is in the frame.  If so, then set the unlock command for that contract as it
	// is about to be replaced

	// Get the target for the command to check if we are in a composite screen
	var target = getCompositeScreenTarget( cmdname );

	if ( ( target != "" ) && ( target != "NEW" ) )
	{
		// The command is going to a frame in our composite screen
		// Check what is in the current destination frame to see if it is
		// a contract that needs unlocking

		var wnd = FragmentUtil.getFrameWindow(target);
		if (formName !="")
		{
			setCurrentForm(formName);
		}
		// get the 'appreq' form reference, adjusting for Fragment name as necessary
		var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL, wnd, target);

		if ( appreqForm != undefined )
		{
			// There is a contract form in the target window
			// Set the unlock command on the form we are about to run the command from
			var targetApp = appreqForm.application.value;
			var targetKey = appreqForm.transactionId.value;		
			var targetLockTime = appreqForm.lockDateTime.value;

			if ( targetApp && targetKey )
			{
				var unlock = targetApp + " " + targetKey + ' ' + targetLockTime;
				unlock = replaceAll(unlock, "%", "%25");

				var formObj = FragmentUtil.getForm(formName);
				formObj.unlock.value = unlock;

				appreqForm.unlock.value = _NO__UNLOCK_;
			}
		}
	}
}
