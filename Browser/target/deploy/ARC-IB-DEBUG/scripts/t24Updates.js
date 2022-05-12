/** 
 * @fileoverview This file contains anything to do with T24 Updates custom java script
 *
 * @author Mat Kusari
 * @version 0.1 
 */

/**
 * Upload a file to the webserver by submitting to a utility routine.
 * @param {String} filePath The path to the file to upload.
 * @return {void}
 *
 */
function xmlFileUpload( filePath)
{
	// The use of getElementById is required here as it is not possible to 
	// attach a fragment sufix to this form during transformation because HTML
	// response did not get transformer via window.xsl
	var form = window.document.getElementById( "fileUploadForm");
	form.requestType.value = _UTILITY__ROUTINE_;
	form.routineName.value = _OS__UPLOAD__EUS__FILE_;
	form.windowName.value = window.name;
	form.routineArgs.value = "";
	form.filePath.value = filePath;
	form.submit();
}

/**
 * Should be called when the upload has finished.
 * @return {void}
 */
function uploadFinished()
{
	try
	{
		var iframeObj = window.document.getElementById( "upload_target");
		var parentFileUploadDiv = window.document.getElementById( "fileUploadDiv");
		var childFileUploadDiv = iframeObj.contentWindow.document.getElementById( "fileUploadDiv");
		// Update the parent div with what is on child div
		parentFileUploadDiv.innerHTML = childFileUploadDiv.innerHTML;
	}catch( e){};
}

/**
 * Only used for T24 Updates
 * Works with the search box to send an enquiry
 * @return {void}
 */
function t24UpdatesSearch()
{
	// Get element by id needs to be here
	var searchBox = document.getElementById("updatesSearchBox");
	// Only run command if there is something in the search box
	var command = "ENQ EB.UPDATE.CLIENT.SEARCH SEARCH EQ '" + searchBox.value + "'";
	t24UpdatesDoCommand( command);
}


/**
 * Only used for T24 Service Packs
 * Works with the search box to send an enquiry
 * @return {void}
 */
function t24ServicePackSearch()
{

	// Get element by id needs to be here
	var searchBox = document.getElementById("updatesSearchBox");
	// Only run command if there is something in the search box
	var command = "ENQ EB.NOFILE.SEARCH.SP SEARCH EQ '" + searchBox.value + "'";
	t24UpdatesDoCommand( command);
}

/**
 * When users clicks on search it clears this value
 * 
 * @return {void}
 */
function searchClick( event)
{
	var field = event.target || event.srcElement;
	field.select();
}

/**
 * When users hits enter key this calls t24UpdatesSearch()
 * @return {void}
 */
function onKeyDownSearchUpdates( event)
{
	if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13))
    {
    	t24UpdatesSearch();
    } 
}

/**
 * When users hits enter key this calls
 * @return {void}
 */
function onKeyDownSearchServicePacks( event)
{
	if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13))
    {
    	t24ServicePackSearch();
    } 
}

/**
 * When users hits enter key this calls
 * @return {void}
 */
function getUpdates()
{
	var updatesForm = FragmentUtil.getForm( "updatesForm");

	FragmentUtil.submitForm( updatesForm, "");
}

/**
 * Command wrapper for T24 Updates
 * @return {void}
 */
function t24UpdatesDoCommand( command)
{
	// Clear out the title area before running a command
    var titleArea = document.getElementById( "titleArea");
    titleArea.innerHTML = "";
    // Now run the command
	docommand( command);
}
/* This array will hold the updates in the shopping cart
 * 
 */
 var updatesCart_GLOBAL = null; 

/**
 * Called when the Uptdate banner is loaded
 * @return {void}
 */
function t24UpdatesOnLoad()
{	
	// Start up a new cart
	updatesCart_GLOBAL = new Array();
	// Get rid of previos event subscriptions
	onEnquiryLoad.unsubscribeAll();
	// Subcribe to enquiry load event
	onEnquiryLoad.subscribe( t24UpdatesEnquiryLoadHandler);
}

/**
 * Handler
 * @return {void}
 */
function t24UpdatesEnquiryLoadHandler( type, args)
{
	var data = args[ 0];
	// Get the table element where the enquiry data is
	var tableElement = data.enquiryDataTable;
	// Only add the below event handlers if we are takling about this enquiry.
	if( data.enquiryId == "EB.UPDATE.CLIENT.SEARCH")
	{
		// Unsubscribe all events as we are just loading now. In case of previous subscriptions added
		onEnquiryRowMouseClick.unsubscribeAll();
		onEnquiryRowMouseClick.subscribe( t24UpdatesItemEnqRowClickHandler);
		//
		onEnquiryRowMouseOver.unsubscribeAll();
		onEnquiryRowMouseOver.subscribe( t24UpdatesItemEnqRowOverHandler);
		//
		onEnquiryRowMouseOut.unsubscribeAll();
		onEnquiryRowMouseOut.subscribe( t24UpdatesItemEnqRowOutHandler);
		
		// Are there any items in the shopping cart
		if( updatesCart_GLOBAL.length > 0)
		{
			updateClientSearchTable( tableElement);
		} 
	}
	// If the enquiry is EB.AVAILABLE.UPDATES then update the check boxes with the selected updates
	if( data.enquiryId == "EB.AVAILABLE.UPDATES")
	{
		// Unsubscribe all events as we are just loading now. In case of previous subscriptions added
		onEnquiryRowMouseClick.unsubscribeAll();
		onEnquiryRowMouseClick.subscribe( t24UpdatesFastPathCheckBoxClickHandler);
		// Unsubscribe all events as we are just loading now. In case of previous subscriptions added
		onEnquiryClick.unsubscribeAll();
		onEnquiryClick.subscribe( t24UpdatesFastPathCheckBoxToggleAllClickHandler);
		// Update the checkboxes if any items are in the cart
		updateAvailableUpdatesTable( tableElement);
	}
	// Update the title area
	updateTitle( data.enquiryId);
}

/**
 * This function will paint the enquiry table with the right rows selected
 * based on the updatesCart_GLOBAL
 * @return {void}
 */
function updateTitle( enquiryId)
{
	var titleArea = null;
	switch( enquiryId)
	{
		case "EB.AVAILABLE.UPDATES":
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Your updates basket. Only the selected updates will be downloaded";
			break; 
        case "EB.UPDATE.CLIENT.SEARCH": 
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Search Results. Select an item of interest and the relevant update will be selected in 'Available Updates'";
        	break;
        case "EB.UPDATE.CLIENT.SYSTEMS":
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Systems registered by your institution. Please select a system to continue";
        	break;
        case "EB.UPDATES.INBOX": 
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Messages from the T24 Updates Team";
            break;            
        case "EB.UPDATE.SYSTEM.LIST": 
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "View a product's component list by expanding the plus icon";
			break;
        case "EB.NOFILE.SERVICE.PACKS": 
		    titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Explore 'Service Pack Content' to find out any fixes for problems in the relevant system release";			
            break;   
        case "EB.SERVICE.PACK.ITEM.SEARCH":
        	titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "The list of problems this Service Pack provides fixes for";			
            break;
        case "EB.NOFILE.SEARCH.SP":
        	titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "Search Results. Each item belongs to a Service Pack shown on the first column";			
            break;
        default : 
			titleArea = document.getElementById( "titleArea");
			titleArea.innerHTML = "";			
        	break;
	}
}

/**
 * This function will paint the enquiry table with the right rows selected
 * based on the updatesCart_GLOBAL
 * @return {void}
 */
function updateClientSearchTable( tableElement)
{
	var rows = tableElement.rows;
	for( var i = 0; i < rows.length; i++)
	{
		var currentRow = rows[ i];
		// For some reason FireFox puts a space " " instead of empty "".
		if( currentRow.cells[ 0].innerHTML != "" && currentRow.cells[ 0].innerHTML != " ")
		{
			var itemName = currentRow.cells[ 1].innerHTML;
			// If the itemName exists in the shopping cart then paint the current row
			if( updatesCart_GLOBAL.contains( itemName))
			{
				currentRow.className = "colour3";
			}
			else
			{ // Otherwise this item should not be selected so unselect it.
				var originalClass = currentRow.getAttribute( "originalClass");
				currentRow.className = originalClass;
			}
		}
		
	}
}

/**
 * This function will tick the checkboxes in enquiry table with the right rows selected
 * based on the updatesCart_GLOBAL
 * @return {void}
 */
function updateAvailableUpdatesTable( tableElement)
{
	// If all the checkboxes are set to true then this should be set to true
	var updateToggleAllButton = true;
	var rows = tableElement.rows;
	for( var i = 0; i < rows.length; i++)
	{
		var currentRow = rows[ i];
		var itemName = currentRow.cells[ 4].innerHTML;
		var currentCheckBox = currentRow.cells[ 1].firstChild;
		// If the itemName exists in the shopping cart then paint the current row
		if( updatesCart_GLOBAL.contains( itemName))
		{
			currentCheckBox.checked = true;
		}
		else
		{ // Otherwise this item should not be selected so unselect it.
	    	currentCheckBox.checked = false;
	    	// If we ever get here then all the checkboxes are not ticked
	    	updateToggleAllButton = false;
		}
	}
	// Update the toggle all button to true 
	if( updateToggleAllButton )
	{
		var toggleAllCheckBox = document.getElementById( "toggleAll");
		toggleAllCheckBox.checked = true;
	}
}

/**
 * Handler
 * @return {void}
 */
function t24UpdatesItemEnqRowClickHandler( type, args)
{
	
	var itemName = "";
	// Get the element in question
	var data = args[ 0];
	var trClicked = data.tableRow;
    // Only do anything if this enquiry is present 
    if( data.enquiryId == "EB.UPDATE.CLIENT.SEARCH" )
    {
    	//				   <TR>      <TBODY>    <TABLE>
    	var tableElement = trClicked.parentNode.parentNode;
    	// For some reason FireFox puts a space " " instead of empty "".
    	if( trClicked.cells[0].innerHTML != "" && trClicked.cells[0].innerHTML != " ")
    	{
			itemName = trClicked.cells[1].innerHTML;
			// If there remove it, otherwise add it
			if( updatesCart_GLOBAL.contains( itemName))
			{
				updatesCart_GLOBAL.removeItems( itemName);	
			}			
			else
			{
				updatesCart_GLOBAL.addUniqueItem( itemName);	
			}
			// Update the screen
			updateClientSearchTable( tableElement);			
    	}
    } 
	
}

/**
 * Handler
 * @return {void}
 */
function t24UpdatesFastPathCheckBoxToggleAllClickHandler( type, args)
{
	// Get the data for the event in question
	var data = args[ 0];
	// Get the target element the user just clicked on.
	var source = data.event.target ? data.event.target : data.event.srcElement;
	// Get the table element of the enquiry
	var divElement = data.divElement;
	// Is it a input box and is it called 'toggleAll', if so then the user checked/unchecked all the rows
	if( source.tagName == "INPUT" && source.id == "toggleAll")
	{
		var tableElement = getEnquiryTableFromDiv( divElement);
		var rows = tableElement.rows;
		var currentRow = null;
		var itemName = null;
		for( var i = 0; i < rows.length; i++)
		{
			currentRow = rows[ i];
			itemName = currentRow.cells[ 4].innerHTML;
			// If there remove it, otherwise add it
			if( source.checked == true)
			{
				updatesCart_GLOBAL.addUniqueItem( itemName);	
			}			
			else
			{
				updatesCart_GLOBAL.removeItems( itemName);
			}
		}
	}
}

/**
 * Handler
 * @return {void}
 */
function getEnquiryTableFromDiv( divElement)
{
	var tableList = divElement.getElementsByTagName( "TABLE");
	
	for( var i = 0; i < tableList.length; i++)
	{
		var currentTable = tableList[ i];
		if( currentTable.id.indexOf( "datadisplay") > -1)
		{
			return currentTable;
		} 
	}
	// else return null
	return null;
}
/**
 * Handler
 * @return {void}
 */
function t24UpdatesFastPathCheckBoxClickHandler( type, args)
{
	
	// Get the data for the event in question
	var data = args[ 0];
	// Get the row of this event
	var clickedRow = data.tableRow;
	// Get the target element the user just clicked on.
	var source = data.event.target ? data.event.target : data.event.srcElement;
	// Is it a input box
	if( source.tagName == "INPUT")
	{
		var itemName = clickedRow.cells[ 4].innerHTML;
		// If there remove it, otherwise add it
		if( source.checked == true)
		{
			updatesCart_GLOBAL.addUniqueItem( itemName);	
		}			
		else
		{
			updatesCart_GLOBAL.removeItems( itemName);
		}
	}
}

/**
 * Handler
 * @return {void}
 */
function t24UpdatesItemEnqRowOverHandler( type, args)
{
	// Get the data for the event in question
	var data = args[ 0];
	var trOver = data.tableRow;
    // Only do anything if one of these enquiries is present 
    if( data.enquiryId == "EB.UPDATE.CLIENT.SEARCH" )
    {
    	// For some reason FireFox puts a space " " instead of empty "".
    	if( trOver.cells[0].innerHTML != "" && trOver.cells[0].innerHTML != " ")
    	{
    		var currentClass = trOver.className;
    		// If the row is selected don't change the class
    		if( currentClass != "colour3")
    		{
    			trOver.className = "colour2";
    		}
    	}
    }
	
}

/**
 * Handler
 * @retur {void}
 */
function t24UpdatesItemEnqRowOutHandler( type, args)
{
	// Get the element in question
	var data = args[ 0];
	var trOut = data.tableRow;
    // Only do anything if one of these enquiries is present 
    if( data.enquiryId == "EB.UPDATE.CLIENT.SEARCH")
    {
    	// For some reason FireFox puts a space " " instead of empty "".
    	if( trOut.cells[0].innerHTML != "" && trOut.cells[0].innerHTML != " ")
    	{
    		var currentClass = trOut.className; 
    		// If the row is selected don't change the class
    		if( currentClass != "colour3")
    		{
    			var originalClass = trOut.getAttribute( "originalClass"); 
				trOut.className = originalClass;
    		}
    	}
    }
	
}