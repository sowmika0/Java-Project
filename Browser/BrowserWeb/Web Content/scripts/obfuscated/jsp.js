/** 
 * @fileoverview Scripts used by the jsp pages.
 *
 */


/**
 * An onload script to perform the request by submitting the request form.
 * @return {void}
 */
function dorequest()
{
	// Request dropdown list from server
	document.request.submit();
}