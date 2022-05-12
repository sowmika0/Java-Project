/** 
 * @fileoverview This file contains anything to do with custom events
 *
 * @author Mat Kusari
 * @version 0.1 
 */
/*************************** DECLARE GLOBAL BROWSER CUSTOM EVENTS *********************************************/
// Fires when user hovers over an enquiry row
var onEnquiryLoad = new YAHOO.util.CustomEvent( "onEnquiryLoad");
// Fires when user hovers over an enquiry row
var onEnquiryRowMouseOver = new YAHOO.util.CustomEvent( "onEnquiryRowMouseOver");
// Fires when user hovers out an enquiry row
var onEnquiryRowMouseOut = new YAHOO.util.CustomEvent( "onEnquiryRowMouseOut");
// Fires when user clicks an enquiry row
var onEnquiryRowMouseClick = new YAHOO.util.CustomEvent( "onEnquiryRowMouseClick");
// Fires when user clicks an enquiry row
var onTransactionComplete = new YAHOO.util.CustomEvent( "onTransactionComplete");
// Fires when user clicks an enquiry. The div element of the enquiry
var onEnquiryClick = new YAHOO.util.CustomEvent( "onEnquiryClick");
/***************************  ENQUIRY LOAD EVENT ****************************************************/

/**
 * Registers a custom MouseOver event for an enquiry row.
 * @return {void}
 */
function raiseEnquiryLoad( element, enquiryId)
{
	//alert( "Enquiry Load: " + enquiryId);
	// Create the data required for this event
	var data = {
		enquiryDataTable: element,
		enquiryId: enquiryId
	};
	// Fire the event
	onEnquiryLoad.fire( data);
}
/***************************  ENQUIRY ROW MOUSE OVER EVENT ****************************************************/

/**
 * Registers a custom MouseOver event for an enquiry row.
 * @return {void}
 */
function raiseEnquiryRowMouseOver( event, element, enquiryId)
{
	// Get the actuall event given different browsers.
	event = event ? event : window.event; 
	// Create the data required for this event
	var data = {
		tableRow: element,
		enquiryId: enquiryId,
		event: event
	};
	// Fire the event
	onEnquiryRowMouseOver.fire( data);
}

/***************************  ENQUIRY ROW MOUSE OUT EVENT ****************************************************/

/**
 * Registers a custom MouseOver event for an enquiry row.
 * @return {void}
 */
function raiseEnquiryRowMouseOut( event, element, enquiryId)
{
	// Get the actuall event given different browsers.
	event = event ? event : window.event;
	// Create the data required for this event
	var data = {
		tableRow: element,
		enquiryId: enquiryId,
		event: event
	};
	// Fire the event
	onEnquiryRowMouseOut.fire( data);
}


/************************************ ENQUIRY ROW MOUSE CLICK EVENT *********************************************/

/**
 * Registers a custom MouseOver event for an enquiry row.
 * @return {void}
 */
function raiseEnquiryRowMouseClick( event, element, enquiryId)
{
	// Get the actuall event given different browsers.
	event = event ? event : window.event; 
    // Create the data required for this event
	var data = {
		tableRow: element,
		enquiryId: enquiryId,
		event: event
	};
	// Fire the event
	onEnquiryRowMouseClick.fire( data);
}

/************************************* TRANSACTION COMPLETE EVENT ******************************************************/
/**
 * Loads when a transaction complete happens. It will pass back application,version and the form object that goes with this contract
 * @return {void}
 */
function raiseTransactionComplete( applicationVersion)
{
    // Create the data required for this event
	var data = {
		applicationVersion: applicationVersion
	};
	// Fire the event
	onTransactionComplete.fire( data);
}

/************************************** ENQUIRY CLICK ********************************************************/
/**
 * Loads when user clicks on an enquiry div element
 * @return {void}
 */
function raiseEnquiryClick( event, divElement, enquiryId)
{
    // Create the data required for this event
	var data = {
		enquiryId: enquiryId,
		divElement: divElement,
		event: event
	};
	// Fire the event
	onEnquiryClick.fire( data);
}

/**************************************************************************************************************************/