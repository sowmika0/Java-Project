/** 
 * @fileoverview Client specific request JavaScript functions
 *
 */

/**
 * Example - Submits a request to a servlet
 */
function submitRequest()
{

	// Validate fields
	if ( crossValidateFields() )
	{
		// Submit the form request
		document.ofsForm.submit();
	}
}

/**
 * Example - Performs cross-validation between fields
 */ 
function crossValidateFields()
{
	 // Check email Address fields match
	var emailAddr = window.document.getElementById( "fieldName:EMAIL.ADDRESS" ).value;
	var repeatEmailAddr = window.document.getElementById( "repeat:EMAIL.ADDRESS" ).value;
	
	if ( emailAddr != repeatEmailAddr )
	{
		alert("Email Addresses are different");
		return false;
	}
	else
	{
		// Check mandatory fields
		var elements = window.document.ofsForm.getElementsByTagName("input");

		for( var i = 0; i < elements.length; i++)
		{
			var inputElement = elements[i];
	
			if( inputElement.getAttribute("mandatory") == "yes" )
			{
				if (mandatoryCheck( inputElement ) == false)
				{
					return false;
				}
			}
		}
	}
	
	return true;
}

