/** 
 * @fileoverview Functions to display HelpText
 *
 */

/**
 * Load the helptext page using the HelpServlet
 * @param {event} thisEvent
 * @return {void}
 */
function help(thisEvent)
{
	//variable not assigned if helptext accessed not from an application
	if (thisEvent != undefined)
	{
	
		// check if string or object
		if (typeof(thisEvent) != "string")
		// target for Firefox and scrElement for IE
		{
			var thisField = thisEvent.target || thisEvent.srcElement;
			var fieldName = thisField.getAttribute("name");
			var _fieldarray = fieldName.split(":");
			_field = _fieldarray[1];
		}
		else
		{
			_field = thisEvent;
		}
	}
	else
	{
		_field = "";	
	}
	
	if( currentForm_GLOBAL == "" )
	{
		setCurrentForm( "appreq" );
	}
	
	_product = getFormFieldValue( currentForm_GLOBAL, "product");

	if(_product=="")
	{
	   _product=_field;
	}

	_application = getFormFieldValue( currentForm_GLOBAL, "application");


	// _product 	- The product of the application	- Optional - if not specified then the main help page is displayed
	// _application	- The application					- Optional - if specified in the product after a '/'
	// _field		- The field for this application	- Optional

	// Set-up the URL of the helptext servlet
	var contextRoot = getWebServerContextRoot();
	var helpServlet = location.protocol + "//" + location.host + "/" + contextRoot + "/servlet/HelpServlet";
	var helpURL = "";
	var language = getLanguage();
	var user = getUser();

	var commandArgs = "command=view&language=" + language + "&user=" + user + "&";
	var args = "";

	// Display the main HelpText page if no product/application was specified
	if ( ( ( _product == "" ) || ( _product == null ) ) && ( ( _application == "" ) || ( _application == null ) ) )
	{	
		// Get the name of the possible product and application from the form
		_product = getFieldValue( "product" );
		_application = getFieldValue( "application" );
		
		if ( ( _product != null ) && ( _product != "" ) && ( _product != "undefined" ) )
		{	
			if ( ( _application != null ) && ( _application != "" ) && ( _application != "undefined" ) )
			{
				args = commandArgs + "product=" + _product + "&application=" + _application;
				helpURL = helpServlet + "?" + args;
			}
		}
	}
	else if ( ( _application == "" ) || ( _application == null ) )
	{
		// Check if the product and application were specified in the product parameter
		if ( _product )
		{
			var pos = _product.indexOf("/");

			if ( pos != -1)
			{
				var fullHelp = _product;
				_product = fullHelp.substring( 0, pos );
				_application = fullHelp.substring( pos + 1, fullHelp.length );
			}
			else
			{
				_application = getFieldValue( "application" );
			}
			
			args = commandArgs + "product=" + _product + "&application=" + _application;
			helpURL = helpServlet + "?" + args;
		}
	}
	else
	{
		// Set-up the arguments to the HelpText servlet
		args = commandArgs + "product=" + _product + "&application=" + _application + "&field=" + _field;
		helpURL = helpServlet + "?" + args;
	}
	
	if ( helpURL == "" )
	{
		// Just display the main help portal page
		helpURL = location.protocol + "//" + location.host + "/" + contextRoot + "/portal/portal.htm";
	}
	
	var windowName = "t24help-" + _application;
	windowName = replaceSpecialChars( windowName, "_" );
	
	var newWindow = window.open(helpURL,windowName,'',true);
	newWindow.focus();
}


/**
 * Edit the helptext page using the HelpServlet
 * @return {void}
 */
function editHelp()
{
	// get the form reference, adjusting for Fragment name as necessary
	var helpForm = FragmentUtil.getForm("help");

	// Set-up the arguments to the HelpText servlet	
	helpForm.value = "HELP.EDIT";
	helpForm.command.value = "edit";

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(helpForm);
}


/**
 * Save the helptext page in XML format using the HelpServlet
 * @return {void}
 */
function saveHelp()
{
	// get the form reference, adjusting for Fragment name as necessary
	var helpForm = FragmentUtil.getForm("help");

	// Set-up the arguments to the HelpText servlet
	helpForm.requestType.value = "HELP.SAVE";
	helpForm.command.value = "save";

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(helpForm);
}


/**
 * Update the list of helptext fields on the page with those in T24 (also saves the current page)
 * @return {void}
 */
function updateHelpFields()
{
	// get the form reference, adjusting for Fragment name as necessary
	var helpForm = FragmentUtil.getForm("help");

	// Set-up the arguments to the HelpText servlet	
	helpForm.requestType.value = "HELP.SAVE";
	helpForm.command.value = "update.fields";

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(helpForm);
}


/**
 * Create a new helptext page
 * @param {String} fileName The name of the file to be created.
 * @return {void}
 */ 
function createHelp( fileName )
{
	// get the form reference, adjusting for Fragment name as necessary
	var helpForm = FragmentUtil.getForm("help");

	// Set-up the arguments to the HelpText servlet
	helpForm.requestType.value = "HELP.CREATE";
	helpForm.command.value = "create";
	helpForm.path.value = fileName;

	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(helpForm);
}


/**
 * Displays the helptext for the standard T24 fields (e.g. Authoriser, Date/Time, etc)
 * @return {void}
 */ 
function viewCommonHelpFields()
{
	// Set-up the arguments to the HelpText servlet
	var contextRoot = getWebServerContextRoot();
	var helpServlet = location.protocol + "//" + location.host + "/" + contextRoot + "/servlet/HelpServlet";
	var helpURL = "";
	var language = getLanguage();
	var commandArgs = "command=view.common&language=" + language;
	
	helpURL = helpServlet + "?" + commandArgs;
	var windowName = "help_commons_lang_" + language;
	windowName = replaceSpecialChars( windowName, "_" );

	var newWindow = window.open(helpURL, windowName);
}


/**
 * Converts character place holders with carriage return and line feeds
 * @return {void}
 */ 
function convertTextAreas()
{
	// get the form reference, adjusting for Fragment name as necessary
	var helpForm = FragmentUtil.getForm("help");

	// Ensure newline place holders are replaced in textareas with correct HTML codes
	for ( var i = 0; i < helpForm.elements.length; i++ )
	{
		var field = helpForm.elements[i];
		
		// Only want to process textarea fields
		if (field.type == "textarea")
		{
			// Encode textarea string's carriage returns
	 		var fieldValue = escape(field.value);

			fieldValue = replaceAll( fieldValue, "_CRLF_", '%0D%0A' );

			// Unescape all other encoded characters
			field.value = unescape(fieldValue);
		}
	}
}


/**
 * Scrolls the given field into view.
 * @param {String} fieldName The name of the field to use as an anchor.
 * @return {void}
 */
function gotoField( fieldName )
{
	// Go to the named field by using the same URL and window name via the field anchor
	windowName = window.name;

	// If the window has no name (because it was started separately 
	// outside Browser) then give it a name
	if ( windowName == "" )
	{
		_application = getFieldValue( "application" );
		var language = getLanguage();

		windowName = "help_" + _application + "_lang_" + language;
		window.name = windowName;
	}
	
	var Url = window.document.URL + "#" + fieldName;
	window.open(Url, windowName);
}
