/** 
 * @fileoverview Javascript for Enquiry Fastpath action
 *
 * @author XXX
 * @version 0.1 
 */
 
 // Variables to store information across functions
var itemArray;
var itemTotal=0;
var enqString;
var windowName;
var myInterval;
var countItem=1;
var timer = 0;
var windowName="";
var inline =0; // Show progress in the iframe or a separate window?
var updatesFlag_GLOBAL = false;

/**
 * get the form reference, adjusting for Fragment name as necessary
 * @param {String} e
 */
function Toggle(e)
{
	// Get the current fragment/frame enquiryData form to process about check box.
 	var edForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"denqresponse","enquiryData","");

	if (e.checked) 
	{
		// Pass form object to all check boxes to be checked.
	 	edForm.toggleAll.checked = AllChecked(edForm);

	}
	else 
	{
		edForm.toggleAll.checked = false;
	}
}

/**
 * TODO
 * @param {String} e
 */
function ToggleAll(e)
{
	// Get the current fragment/frame enquiryData form to process about check box.
 	var currentEnqDataForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"denqresponse","enquiryData","");

	if (e.checked) 
	{
		// Pass form object, check boxes to be checked.
 		CheckAll(currentEnqDataForm);
	}
	else 
	{
		// Pass form object, check boxes to be cleared. 
 		ClearAll(currentEnqDataForm);
	}
}

/**
* Switch to the next element for tab key press
*/
function checkSwitch(e)
{
	if(e.keyCode == 9) {
		return true;
	}
	else {
		return false;
	}
}

/**
 * TODO
 * @param {String} e
 */
function Check(e)
{
	if (!e.disabled)
	{
		e.checked = true;
	}
}

/**
 * TODO
 * @param {String} e
 */
function Clear(e)
{
	if (!e.disabled)
	{
		e.checked = false;
	}
}

/**
 * Checks all check boxes on the page
 */
function CheckAll(enqDataForm)
{
	var edForm = enqDataForm;

	var elementList = edForm.elements;
	var len = elementList.length;
	for (var i = 0; i < len; i++) 
	{
		var e = elementList[i];
		if (e.name == "ChckBx") 
		{
			Check(e);
		}
	}
	edForm.toggleAll.checked = true;
}

/**
 * Clears all check boxes on the page
 */
function ClearAll(enqDataForm)
{
	var edForm = enqDataForm;

	var elementList = edForm.elements;
	var len = elementList.length;
	for (var i = 0; i < len; i++) 
	{
		var e = elementList[i];
		if (e.name == "ChckBx") 
		{
			Clear(e);
		}
	}
	edForm.toggleAll.checked = false;
}

/**
 * Returns true if all check boxes on the page are checked.
 * @return true if all check boxes are checked.
 */
function AllChecked(enqDataForm)
{
	var edForm = enqDataForm;

	var elementList = edForm.elements;
	var len = elementList.length;
	for(var i = 0 ; i < len ; i++) 
	{
		if (elementList[i].name == "ChckBx" && !elementList[i].checked) 
		{
			return false;
		}
	}
	return true;
}

/**
 * get the form reference, adjusting for Fragment name as necessary
 */
function doServerCommit(enqDataForm)
{
	var enqForm = FragmentUtil.getForm("enquiry");

	// Get the fastPathType element from current enquiryData form.
 	var fpTypeBox =enqDataForm.elements["fastPathType"];
	var fpFunction = fpTypeBox.value;
	var enqName = enqForm.enqid.value;
	var isArc = window.document.getElementById("isARC");
	
	enqString = enqName + "+" + fpFunction + "_";
	cmdName = enqString + itemArray[0];
	
	//If fastpath is from arc-ib, then enclose all the data in to single argument and pass it in single request
	
	if( isArc != "" && isArc != null && isArc.value == "true") 
	{
		cmdName = cmdName + getFieldData(itemArray[0],enqDataForm);
		for(var i = 1 ; i < itemTotal; i++) 
		{
			cmdName = cmdName + "^" + enqString + itemArray[i];			
			cmdName = cmdName + getFieldData(itemArray[i],enqDataForm);
		}
		doSubmitRequest(cmdName, enqDataForm);
	} 
	else
	{
		doSubmitRequest(cmdName, enqDataForm);
		if (itemTotal > 1)
		{
			myInterval = self.setInterval('clock()', 1000);
		}
	}
}

/**
 * TODO
 */
function commitNext(parentEdForm){

	if (itemTotal > countItem)
	{
		timer = 0;		// reset my time out item...
		cmdName = enqString + itemArray[countItem];
		++countItem;
		doSubmitRequest(cmdName,parentEdForm);
	}
}

/**
 * get the form reference, adjusting for Fragment name as necessary
 */
function doFPCommit()
{
	// Get the current fragment/frame enquiryData form to process about commit the selected records.
 	var edForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"denqresponse","enquiryData","");

	var count = 0;
	
	var chckList="";

	countItem=1; //Reinitialising selected item count

	// Check we have items in the dropdown list - could be empty if records found
	if ( edForm != null )
	{
		var elementList = edForm.elements;
		var len = elementList.length;
		
		itemArray = new Array(len);
		// Build an array of all of the checked items.
		for(var i = 0 ; i < len ; i++) 
		{		
		 	if (elementList[i].name == "ChckBx")
		 	{ 	
		 		if ( (elementList[i].checked) && !(elementList[i].disabled) )
				{
					itemArray[count] = elementList[i].value;
					elementList[i].disabled="true";
					++count;
				}
			}
		}
		
		if (inline==1)
		{
	 		var fsheader = window.document.getElementById("fpsubmitheader");
	 		var fswindow = window.document.getElementById("fpsubmitheader");
	 		fsheader.style.height="50px";
	 		fswindow.style.height="50px";
		}	
		
		itemTotal = count;			//number of checked checkboxes
		
		if(itemTotal > 0){			// Only if we have something to do.
			doServerCommit(edForm);
		}
	}
}

/**
 * TODO
 * @param (TODO) imgField
 * @param (TODO) status
 */
function initFastPath(imgField, status)
{
	var contextRoot = getWebServerContextRoot();
	var browserURL = location.protocol + "//" + location.host + "/" + contextRoot + "/plaf/images/" + getSkin();
	var imgDisplay = "";
	var isArc = window.document.getElementById("isARC");
	//Dont display flag, if the response is for ARC-IB
	if( isArc != "" && isArc != null && isArc.value == "true") 
	{
		return;
	}
	if (status == "OK")
	{
		imgDisplay = browserURL + "/greenlight.gif";
	}
 	else
 	{
 		imgDisplay = browserURL + "/redlight.gif";
 	}
 	
 	//change image
 	// determine if the progress window is an iframe or a separate
 	// window - we might do either and this makes the development
 	// easier.
 	if (inline==1){
 		var parentWin = window.parent;
 	} else {
 		var	parentWin = window.opener;
 	}
	if(parentWin!=null){
		// get the parent enquiryData form reference, adjusting for Fragment name as necessary
		// Send the parent form object to have current enquiryData form.
 		var parentEdForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"denqresponse","enquiryData",parentWin); 

		imgControl = parentEdForm[imgField];
		if (imgControl!=null){
			imgControl.src = imgDisplay;
			imgControl.alt =status;
			parentWin.commitNext(parentEdForm);
		}
 	} else {
 		handleError("No Parent window");
 	}
}

/**
 * function to handle errors and stop processing
 * @param (todo) myMsg
 */
function handleError(myMsg){
	alert(myMsg);
}

/**
 * Check to see if we have a response - if not set a new window in case one has crashed
 */
function clock()
{
	if (itemTotal > countItem)
	{
		if(timer > 2) {
			//My existing window has died!
			// !!! Add the timeout and retry processing here.
			alert("Committing window has timed out");
		}
	}
	else
	{
		self.clearInterval(myInterval);
	}
}

/**
 * Setup buildUtilityRequest arguments
 * @param (TODO) argNames
 */
function doSubmitRequest(argNames,enqDataForm)
{
	var isArc = window.document.getElementById("isARC");
	routineName = _OS__FAST__PATH_;
	formName = "generalForm";
	requestType = _UTILITY__ROUTINE_;
	if( isArc == "" || isArc == null || isArc.value != "true")
	{ 
		data = getFieldData(itemArray[countItem-1],enqDataForm);
		argNames = argNames + data;
	}
	title = "";
	width = "600";
	height = "400";
	if(windowName==""){
		if(inline==1){
			windowName ="iframe:fpsubmitwindow";  // The name of the iframe on the enquiry page
		} else {
			windowName = createResultWindow("NEW", width, height);		// Or create a new window
		}
	}
	if(windowName==""){
		window.document.getElementById("iframe:fpsubmitwindow").name = "iframe:fpsubmitwindow";
		// And send the request	
		buildUtilityRequest(routineName, argNames, formName, windowName, requestType, title, width, height,"");
		window.document.getElementById("iframe:fpsubmitwindow").name = "iframe:fpsubmitwindow";
	}else{
		buildUtilityRequest(routineName, argNames, formName, windowName, requestType, title, width, height,"");				
     }		

}

/**
 * TODO
 * @param (TODO) itemref
 */
function setchecked(itemref){
	var thisbox = window.document.getElementById(itemref);
	if (thisbox != null )
	{
		thisbox.checked=true;
	}
}

/**
 *  Loop through all the input boxes and build up the data for the OFS statement
 * @param (TODO) item
 * @return TODO
 * @type TODO
 */
function getFieldData(item,enqDataForm){
// Loop through all the input boxes and build up the data for the OFS statement
// for this row...

	if(item==null){
		return "";			// Just in case.
	}

	//extracts the items before the plus sign  
   	var plusPos = item.indexOf("+");
   	var xtItem = item.substring(0, plusPos);
      	
	// Extract all input elements from current enquiryData form.
 	var inputs = enqDataForm.getElementsByTagName("input");
		
	activeinputNumber = 0;
	activeinputCount = 0;
	numberOfinputs = inputs.length;
		
	var data = "+";
	var inputCount = 0;
	// Go through each input box on the page...is there a better way of doing this?
	for (inputCount = 0; inputCount < numberOfinputs; inputCount++)
	{
		// Modified to use .value for compability with Firefox
		inputValue = inputs[inputCount].value ;
		inputId = inputs[inputCount].getAttribute("id");
		thisitem = inputs[inputCount].getAttribute("therow");
		mvDetails= inputs[inputCount].getAttribute("mvdetails");	
		// If the row is the same as the item that we are interested in then add it to the string
		if (thisitem==xtItem)
		{
        	mvpos=1;
         	svpos=1; 
            if (mvDetails!='')
			{
				data = data + "*"+inputId+":"+mvDetails+"*"+inputValue;
			}
			else
			{ 
				data = data + "*"+inputId+":"+mvpos+":"+svpos+"*"+inputValue;
			}
			var nextinputitem = inputs[inputCount+1];
			if (nextinputitem && nextinputitem!= 'undefined')
			{
				nextitem = nextinputitem.getAttribute("therow");
			// This a field with multivalues.So loop and get all the data for the field which is present in multiple rows.
               while(nextitem=="") 
                { 
            		inputId=inputs[inputCount+1].getAttribute("id");
            		inputValue = inputs[inputCount+1].value ;
                	mvDetails= inputs[inputCount+1].getAttribute("mvdetails");
                	if (mvDetails!='')
					{
						data = data + "*"+inputId+":"+mvDetails+"*"+inputValue;
					}
                	else
					{				
 						data = data + "*"+inputId+":"+mvpos+":"+svpos+"*"+inputValue;
					}
					inputCount=inputCount+1;
					nextinputitem = inputs[inputCount+1];
					if (nextinputitem && nextinputitem!= 'undefined')
					{
						nextitem = nextinputitem.getAttribute("therow");
					}
					else
					{
						nextitem=null;
					}
			  	};
			}	                            
		}	
	}
	return data;	
}


function doMDCommit()
{
	var edForm = "";
 	edForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"denqresponse","enquiryData","");
 
	var count = 0;
	var drillRefs = "";
	
	// Check we have items in the dropdown list - could be empty if no records found
	if ( edForm != null )
	{
		var elementList = edForm.elements;
		var len = elementList.length;
		itemArray = new Array(len);
		// Build an array of all of the checked items.
		for(var i = 0 ; i < len ; i++) 
		{		
		 	if (elementList[i].name == "ChckBx")
		 	{ 	
		 		if ( (elementList[i].checked) && !(elementList[i].disabled) )
				{
					itemArray[count] = elementList[i].value;
					drillRefs = drillRefs + itemArray[count] + "|";
					++count;
				}
			}
		}
		itemTotal = count;			//number of checked checkboxes
		if(itemTotal > 0){			// Only if we have something to do.
			doMDServerCommit(drillRefs,edForm);
		}
	}	
}

/**
 * get the form reference, adjusting for Fragment name as necessary
 */
function doMDServerCommit(drillRefs,enqDataForm)
{
	var enqForm = enqDataForm;
	var mdFunction = "MUTI.DOWNLOAD.FUNCTION";
	// Since enqid presents only in the enquiry form do not process enquiry data form here.
	enqForm = getCurrentFragmentEnqDataForm(FragmentUtil.getWindowOrFragmentName(),"","enquiry","");
	var enqName = enqForm.enqid.value;
	
	enqString = enqName + "+" + mdFunction + "+";
	//cmdName = enqString + itemArray[0];
	cmdName = enqString + drillRefs;
	
	doMDSubmitRequest(cmdName,enqDataForm);
}

/**
 * Setup buildUtilityRequest arguments
 * @param (TODO) argNames
 */
function doMDSubmitRequest(argNames,enqDataForm)
{
	if ( updatesFlag_GLOBAL )
	{
		routineName = "OS.UTIL.GET.UPDATES";
	}
	else
	{
		routineName = _OS__MULTI__DOWNLOAD_;	
	}
	
	formName = "generalForm";
	//windowName = windowName_GLOBAL;
	requestType = _UTILITY__ROUTINE_;
	data = getFieldData(itemArray[countItem-1],enqDataForm);
	argNames = argNames + data;

	// Indicate this request to download documents.
 	DOC_DOWN_SERVICE = true;
 
 	// Assign current window or fragment name.
 	windowName = FragmentUtil.getWindowOrFragmentName();

	//window.document.getElementById("iframe:mdsubmitwindow").name = "iframe:mdsubmitwindow";
	// And send the request	
	// Document downloads are forced to open in dialog box so that window attributes are not required like title etc.
 	 buildUtilityRequest(routineName, argNames, formName, windowName, requestType, "", "", "","");
 	 DOC_DOWN_SERVICE = false;  // reset the document download.
	//window.document.getElementById("iframe:mdsubmitwindow").name = "iframe:mdsubmitwindow";
}

/**
 * Run fastpath utility to go back to T24, resolve 'checked' drilldowns, get the dependency list and return
 * a list of Update Id's to be tar up and passed back to the user.
 */
function downloadUpdates()
{
	updatesFlag_GLOBAL = true;
	doMDCommit();
	updatesFlag_GLOBAL = false;
}