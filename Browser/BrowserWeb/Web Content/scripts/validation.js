/** 
 * @fileoverview Functions used for validation - includes Web Validation response processing
 *
 */

var displayMenu = false;	// Whether to display the deal menu (error/message)

/**
 * Process the response from a web validation field validation request
 * @return {void}
 */
function processWebValResponse()
{
	// Loop round all contract errors, messages, and field data
	// updating the contract on display
	// But first clear the existing errors and messages from the deal menu
	displayMenu = false;

	// if its a check file enrichment dont clear the errors or message 	etc
	if (!isCheckFile())
	{
		clearErrorsMenu();
		clearFieldErrors();
		clearMessagesMenu();
	}

	// Now process the response data
	processErrors();
	processMessages();
	processFieldData();
	processQuestions();

	// If there were any errors or messages then display the deal menu
	if ( displayMenu )
	{
		var toggle = getParentFormField( currentForm_GLOBAL, "toggle" );
		toggle.setAttribute("toggleState","SHOW");
		window.parent.toggleDealMenu();	
	}	
}


/**
 * Function to determine if this is a response to a checkfile enrichment (CheckFileValidator class)
 * @return 'TRUE' or 'FALSE'
 */ 
function isCheckFile()
{
	var errorCount = countElementsStartingWith("error:");
	var fieldCount = countElementsStartingWith("fieldName:");
	if (fieldCount == 1)
	{
		// get the 'validationResult' form reference, adjusting for Fragment name as necessary
		var valForm = FragmentUtil.getForm("validationResult");

		for ( var i = 0; i < valForm.elements.length; i++ )
		{
			var object = valForm.elements[i];
		 	if ( object.name.indexOf("fieldName:") == 0 )
			{
				var parentField;
				var webValFieldName;
				webValFieldName = object.name;

				parentField = getParentFormField(currentForm_GLOBAL, webValFieldName);

				if (parentField == null )
					parentField = getParentFormField(currentForm_GLOBAL, webValFieldName + ":" + object.getAttribute("multi"));

				if (parentField == null )
					parentField = getParentFormField(currentForm_GLOBAL, webValFieldName + ":" + object.getAttribute("multi") + ":" + object.getAttribute("sub"));

				if (parentField != null)
				{
					if (parentField.getAttribute("vr") == "com.temenos.t24browser.validation.CheckFileValidator")
						return true;
				
				}
			}
		}
	}
	return false;
}

/**
 * Clear the errors menu
 * @return {void}
 */ 
function clearErrorsMenu()
{
	// Get a handle on the errors table on the deal menu
	var errorsMenu = getParentFormField( "appreq", "errors" );

	// Delete every table row in the table
	clearTable( errorsMenu );
}

/**
 * Clear the field errors
 * @return {void}
 */  
function clearFieldErrors()
{
	// Loop through all field captions that have an error marked against
	// then and change their class back to a normal field colour
	var theParent = window.parent;
	var anchors = theParent.document.getElementsByTagName("A");

	for ( var i = 0; i < anchors.length; i++ )
	{
		var anchor = anchors[i];
		var anchorName = anchor.name;
		
		var pos = anchorName.indexOf("fieldCaption:");

	 	if ( pos == 0 ) 
		{
			anchors[i].setAttribute( "className", "field" );
		}
	}
	
	// Hide the errors div
	var errorBoxObj = getParentFormField( "appreq", "error_box");
	errorBoxObj.setAttribute( "className", "display_box_none" );
}

/**
 * Clear the messages menu
 * @return {void}
 */
function clearMessagesMenu()
{
	// Get a handle on the messages table on the deal menu
	var messagesMenu = getParentFormField( "appreq", "messages" );

	// Delete every table row in the table
	clearTable( messagesMenu );
}

/**
 * Count the number of field on the page with an id starting with
 * @param (String) startingWith
 * @return iCount
 */
function countElementsStartingWith(startingWith)
{
	// get the 'validationResult' form reference, adjusting for Fragment name as necessary
	var valForm = FragmentUtil.getForm("validationResult");

	var iCount = 0;

	for ( var i = 0; i < valForm.elements.length; i++ )
	{
		var obj = valForm.elements[i];
		var objName = obj.name;
		
		var pos = objName.indexOf(startingWith);

	 	if ( pos == 0 ) 
		{
			iCount ++;
		}
	}
	return iCount;
}

/**
 * Update the errors on the deal menu with those in the response
 * @return {void}
 */ 
function processErrors()
{
	// get the 'validationResult' form reference, adjusting for Fragment name as necessary
	var valForm = FragmentUtil.getForm("validationResult");
	
	// For each error in our response add a row to the table
	for ( var i = 0; i < valForm.elements.length; i++ )
	{
		var obj = valForm.elements[i];
		var objName = obj.name;
		
		var pos = objName.indexOf("error:");

	 	if ( pos == 0 ) 
		{
			// Add the error to the general errors menu
			addError( "", obj.value );
		}
	}
}

/**
 * Update the messages on the deal menu with those in the response
 * @return {void}
 */ 
function processMessages()
{
	// get the 'validationResult' form reference, adjusting for Fragment name as necessary
	var valForm = FragmentUtil.getForm("validationResult");

	// For each error in our response add a row to the table
	for ( var i = 0; i < valForm.elements.length; i++ )
	{
		var obj = valForm.elements[i];
		var objName = obj.name;
		
		var pos = objName.indexOf("message:");

	 	if ( pos == 0 ) 
		{
			// Add the message to the contract errors menu
			addMessage( obj.value );
		}
	}

}

/**
 * Update the fields on the deal with those in the response
 * @return {void}
 */  
function processFieldData()
{
	// For each field in our response update the field details on 
	// the appreq contract form
	
	// get the 'validationResult' form reference, adjusting for Fragment name as necessary
	var valForm = FragmentUtil.getForm("validationResult");

	for ( var i = 0; i < valForm.elements.length; i++ )
	{
		var field = valForm.elements[i];
		var fieldName  = field.getAttribute('name');
		var fieldMulti = field.getAttribute('multi');
		var fieldSub   = field.getAttribute('sub');
		var fieldValue = field.getAttribute('value');
		var fieldEnri  = field.getAttribute('enri');
		var fieldErr   = field.getAttribute('err');

		var pos = fieldName.indexOf("fieldName:");

	 	if ( pos == 0 ) 
	 	{
	 		// Determine the full field name (with the multi/sub value numbers)
			var fullFieldName = getFullFieldName( fieldName, fieldMulti, fieldSub );
			
			// Determine the actual field name (minus the "fieldName:" at the start)
			var actualFieldName = fullFieldName.substring( 10, fullFieldName.length );

			// Check for changes in the field data
			if ( fieldValue != "undefined" )
			{
				window.parent.setFormFieldValue( currentForm_GLOBAL, fullFieldName, fieldValue );
			}
			
			if ( fieldEnri != "undefined")
			{
				createEnrichment( "enri_" + actualFieldName, fieldEnri );
			}
			
			if ( fieldErr != "undefined")
			{
				// Add the error to the contract errors menu
				addError( actualFieldName, fieldErr );
				
				// Change the class of the field caption on the deal so that it is red
				var captionName = "fieldCaption:" + actualFieldName;
				setFieldCaptionClass( captionName, "error" );
			}
		}		 		
	}
}



/**
 * Ask any questions that were in the response
 * @return {void}
 */   
function processQuestions()
{
	// get the 'validationResult' form reference, adjusting for Fragment name as necessary
	var valForm = FragmentUtil.getForm("validationResult");

	// For each question in our response ask the question and send the response to their JavaScript function

	for ( var i = 0; i < valForm.elements.length; i++ )
	{
		var obj = valForm.elements[i];
		var objName = obj.name;
		
		var pos = objName.indexOf("question:");

	 	if ( pos == 0 ) 
		{
			var quText = obj.value;
			var quRoutine = obj.getAttribute("jsroutine");
			var quField = obj.getAttribute("fld");
			var answer = confirm( quText ); // Display the question
			var fn = eval(quRoutine);
			fn(quField, answer);						// Call the answer routine
		}
	}

}

/**
 * Set the specified class on the field caption
 * @param (String) _captionName
 * @param (String) _class 
 */   
function setFieldCaptionClass( _captionName, _class )
{
	setParentFormAnchorClass( currentForm_GLOBAL, _captionName, _class );
}

/**
 * Determine the full field name from the multi sub-value numbers
 * @param (String) _fieldName
 * @param (String) _fieldMulti
 * @param (String) _fieldSub
 * @return fullFieldName
 */    
function getFullFieldName( _fieldName, _fieldMulti, _fieldSub )
{
	var fullFieldName = _fieldName;
	
	if ( _fieldMulti != "" )
	{
		fullFieldName = fullFieldName + ":" + _fieldMulti;
		
		if ( _fieldSub != "" )
		{
			fullFieldName = fullFieldName + ":" + _fieldSub;
		}
	}
	
	fullFieldName = getBrowserFieldName(fullFieldName);
	
	return( fullFieldName );
}

/**
 * Add a error to the errors section of the deal menu
 * @param (String) _fieldName
 * @param (String) _fieldError
 * @return {void}
 */ 
function addError( _fieldName, _fieldError )
{
	// Get a handle on the errors table on the deal menu
	var errorsMenu = getParentFormField( "appreq", "errors" );
	
	// Create the arrays for defining the table data cells
	var tdTextArray = new Array();
	var tdClassArray = new Array();
	var tdHrefArray = new Array();
	var arrayIndex = 0;
	
	// Set-up the field name TD - if there is a field name
	if ( _fieldName )
	{
		tdTextArray[arrayIndex] = _fieldName;
		tdClassArray[arrayIndex] = "errors";
		tdHrefArray[arrayIndex] = "";
		
		// Add a href to the field on the form - if the field is editable
		var formFieldName = "fieldName:" + _fieldName ;
		var tabName = getFieldTabName( formFieldName );
		
		if ( tabName != "" )
		{
			tdHrefArray[arrayIndex] = "javascript:linkError('" + tabName + "', '" + formFieldName + "')";
		}
		
		arrayIndex++;
	}
	
	// Set-up the error text TD
	tdTextArray[arrayIndex] = _fieldError;
	tdClassArray[arrayIndex] = "captionError";
	tdHrefArray[arrayIndex] = "";
	
	addTableRow( errorsMenu, tdTextArray, tdClassArray, tdHrefArray );
	
	displayMenu = true;
		
	// Show the errors div
	var errorBoxObj = getParentFormField( "appreq", "error_box");
	errorBoxObj.setAttribute( "className", "display_box" );
}

/**
 * Add a general message to the messages section of the deal menu
 * @param (String) _message
 * @return {void}
 */  
function addMessage( _message )
{
	// Get a handle on the messages table on the deal menu
	var messagesMenu = getParentFormField( "appreq", "messages" );
	
	// Create the arrays for defining the table data cells
	var tdTextArray = new Array(1);
	var tdClassArray = new Array(1);
	
	tdTextArray[0] = _message;
	tdClassArray[0] = "captionError";
	
	addTableRow( messagesMenu, tdTextArray, tdClassArray );
	
	displayMenu = true;
}

/**
 * Set the enrichment against a field
 * @param (String) _enriName
 * @param (String) _enriValue
 * @return {void}
 */   
function createEnrichment( _enriName, _enriValue)
{
	var enriVar = getTabEnrichmentSpan(currentForm_GLOBAL, _enriName );
	if (enriVar != null)
	{
		if (enriVar.childNodes[0])
		{
			enriVar.replaceChild(parent.document.createTextNode(_enriValue),enriVar.childNodes[0]);
		}
		else
		{
			enriVar.appendChild(parent.document.createTextNode(_enriValue));
		}
	}
}

/**
 * Gets name of the tab that a field is on - if any
 * @param (String) _fieldName
 * @return 'tabName'
 */
function getFieldTabName( _fieldName )
{
	var tabName = "";
	var found = false;
	var tabNo = 1;
	var moreTabs = true;
	
	// First check if we have a main tab
	tabName = "mainTab";
	
	if ( fieldOnTab( _fieldName, tabName ) == 1 )
	{
		found = true;
	}
	
	// Now check the other tabs - if the field wasn't on the main tab
	while ( ( found == false ) && ( moreTabs ) )
	{
		tabName = "tab" + tabNo;
		var tabCheck = fieldOnTab( _fieldName, tabName );

		if ( tabCheck == 0 )
		{
			// Move on to next tab
			tabNo++;
		}
		else if ( tabCheck == 1 )
		{
			// Found it on this tab
			found = true;
		}
		else
		{
			// No more tabs to check
			moreTabs = false;
		}
	}
	
	if ( !found )
	{
		tabName = "";
	}
	
	return( tabName );
}

/**
 * Checks if a particular field is on a particular tab
 * @param (String) _fieldName
 * @param (String) _tabName
 * @return 1, 0 or -1
 */ 
function fieldOnTab ( _fieldName, _tabName )
{
	// Returns  0 if the field is not on the tab
	// Returns  1 if the field is on the tab
	// Returns -1 if tab does not exist
	
	var tab = getParentFormField( currentForm_GLOBAL, _tabName );
	
	if ( tab )
	{
		var fields = tab.getElementsByTagName("INPUT");

		for ( var i = 0; i < fields.length; i++ )
		{
			var field = fields[i];
			
			if ( ( field.name == _fieldName ) && ( field.disabled == false ) && ( field.type != "hidden" ) )
			{
				return( 1 );
			}
		}
	}
	else
	{
		// Tab doesn't exist
		return( -1 );
	}
	
	return( 0 );
}
	
