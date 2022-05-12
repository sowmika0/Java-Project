/** 
 * @fileoverview Client specific validation JavaScript functions
 *
 */

/**
 * Validate an alpha field value
 * @param (String) thisEvent The current field event
 * @return {boolean} result
 */
function validateAlpha( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	for ( var i=0; i < value.length; i++ )
	{
		  var char = value.charAt(i);
		  var ascii = char.charCodeAt(0);
		  
		  // Check character is 0-9, A-Z or a-z
		  if ( (ascii > 64 && ascii < 91) || (ascii > 96 && ascii < 123) )
		  {
			  // Valid character
		  }
		  else
		  {
			  alert("Invalid Alpha");
			  return false;
		  }
	}
	
	return true;
}

/**
* Validate an numeric field value
* @param (String) thisEvent The current field event
* @return {boolean} result
*/
function validateNumeric( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	for ( var i=0; i < value.length; i++ )
	{
		  var char = value.charAt(i);
		  var ascii = char.charCodeAt(0);
		  
		  // Check character is 0-9, A-Z or a-z
		  if ( ascii > 64 && ascii < 91 )
		  {
			  // Valid character
		  }
		  else
		  {
			  alert("Invalid Numeric");
			  return false;
		  }
	}
	
	return true;
}

/**
* Validate an alpha-numeric field value
* @param (String) thisEvent The current field event
* @return {boolean} result
*/
function validateAlphaNumeric( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	for ( var i=0; i < value.length; i++ )
	{
		  var char = value.charAt(i);
		  var ascii = char.charCodeAt(0);
		  
		  // Check character is 0-9, A-Z or a-z
		  if ( (ascii > 47 && ascii < 58) || (ascii > 64 && ascii < 91) || (ascii > 96 && ascii < 123) )
		  {
			  // Valid character
		  }
		  else
		  {
			  alert("Invalid Alpha Numeric");
			  return false;
		  }
	}
	
	return true;
}

/**
* Validate a phone number field value
* @param (String) thisEvent The current field event
* @return {boolean} result
*/
function validatePhoneNumber( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	for ( var i=0; i < value.length; i++ )
	{
		  var char = value.charAt(i);
		  var ascii = char.charCodeAt(0);
		  
		  // Check character is 0-9, space or +
		  if ( (ascii > 47 && ascii < 58) || (ascii == 32) || (ascii == 43) )
		  {
			  // Valid character
		  }
		  else
		  {
			  alert("Invalid Phone Number");
			  return false;
		  }
	}
	
	return true;
}

/**
* Validate a string character
* @param (String) char The character to check
* @return {boolean} result
*/
function validateStringCharacter( char )
{
	  var ascii = char.charCodeAt(0);
	  
	  // Check character is space, hyphen, double & single quote, full-stop
	  if ( ascii == 32 || ascii == 45 || ascii == 34 && ascii < 39 && ascii > 46 )
	  {
		  // Valid character
		  return true;
	  }
	  else
	  {
		  return false;
	  }	
}

/**
* Validate a string field value
* @param (String) thisEvent The current field event
* @return {boolean} result
*/
function validateString( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	for ( var i=0; i < value.length; i++ )
	{
		  var char = value.charAt(i);
		  var ascii = char.charCodeAt(0);

		  // Check character is 0-9, A-Z or a-z
		  if ( ( (ascii > 47 && ascii < 58) || (ascii > 64 && ascii < 91) || (ascii > 96 && ascii < 123) ) || validateStringCharacter(char) )
		  {
			  // Valid character
		  }
		  else
		  {
			  alert("Invalid String");
			  return false;
		  }
	}
	
	return true;
}

/**
* Validate an email address field value
* @param (String) thisEvent The current field event
* @return {boolean} result
*/
function validateEmailAddress( thisEvent )
{
	var value = getFieldEventValue( thisEvent );
	
	// Check the value has an "@" character in it
	var index = value.indexOf("@");

	if ( index != -1 )
	{
		return true;
	}
	else
	{
		alert("Invalid Email Address");
		return false;
	}
}

/**
* Validate that a field has been filled in
* @param (String) fieldObj The current field object
* @return {boolean} result
*/
function mandatoryCheck( fieldObj )
{
	var value = fieldObj.value;
	
	if ( value == "" )
	{
		var fieldLabel = fieldObj.getAttribute("fieldLabel");
		
		if ( fieldLabel == "" )
		{
			fieldLabel = fieldObj.name;
		}
		
		alert("Field '" + fieldLabel + "' is mandatory");
		return false;
	}
	else
	{
		return true;
	}
}
