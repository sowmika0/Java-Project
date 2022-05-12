/** 
 * @fileoverview TODO
 *
 * @author XXX
 * @version 0.1 
 */


var ReturnString = "";
var ivSkin = "";

/**
 * TODO
 * @param {TODO} desc
 * @param {TODO} enqid
 * @param {TODO} ceid
 * @param {TODO} realfname
 * @param {TODO} olddata
 * @param {TODO} fname
 */
function addContextEnquiry(desc, enqid,ceid, realfname,olddata,fname,formId)
{
	olddata = replaceAll(olddata,"'","\\'");
	relatedTable = ceid; // Set this to the CONTEXT.ENQUIRY id. Forces S.RUN.CONTEXT.ENQUIRY to use supplied data
	relatedTableArg = ",'"+relatedTable+"'";
	myarg = "'"+ceid+'_'+enqid+'_'+realfname+'_'+ "'";
	myotherargs  = ",'"+realfname+"','"+olddata+"','"+fname+"','"+formId+"'";

	mainarg = myarg+relatedTableArg+myotherargs;
	
	row = document.getElementById('myTable').rows.length; // Add to the end!

	var x=document.getElementById('myTable').insertRow(row); // Insert a row...
	colcount = 1;

	for(var i = 0; i<colcount;i++)
	{
		var y=x.insertCell(i);	
		if(i==0){
			y.innerHTML='<a href = "javascript:doThisContextEnquiry('+mainarg+')">' + desc + '</a>';
		}
	}
}


/**
 * Deals with context enquiries.
 * @param {TODO} enqName
 * @param {TODO} relatedTable
 * @param {TODO} realfname
 * @param {TODO} olddata
 * @param {TODO} fname
 */ 
function doThisContextEnquiry( enqName, relatedTable,realfname,olddata,fname,formId)
{
// Get the data from the text box...
// Since context enquiries always open in a new window, use window.opener
	// Always have fieldData from current form.
	var fieldData = window.opener.getFormFieldValue( formId, fname);

	thedata = "";
	if (fieldData != ""){
		thedata = fieldData;  // This is ok for a text box. TODO select boxes...
	} else {
		thedata = olddata; // or from the nmo input data...
	}
	
	// Set the argument ready to go...
	enqName = enqName + thedata+'_'+relatedTable;
	// get the 'appreq' form reference, adjusting for Fragment name as necessary
	var appreqForm = FragmentUtil.getForm(formId, window.opener);
	// Set-up parameters for a command and submit it on the parent form...
	appreqForm.requestType.value=_OFS__OS__CONTEXT_;
	appreqForm.ofsOperation.value=_POPULATE_;
	appreqForm.routineArgs.value=enqName;
	appreqForm.windowName.value = window.name;

	// Create an element value as current form id to send with request.
	// So that servlet will know from which form the action has been initiated.
	// In case of multi pane application exists.
	var newElement = window.opener.document.createElement("input");
	newElement.type = "hidden";
	newElement.id = "ContextFormId";
	newElement.name = "ContextFormId";
	newElement.value = appreqForm.id;			
	appreqForm.appendChild(newElement);
	
	// Save deal window target for restoration later
	dealWindowName = appreqForm.target;
	
	// Open the result in this window 
	enquiryWindowName = window.name;
	appreqForm.target = enquiryWindowName;
	
	//Parent window should always point to the window from which it is launched.
	var openerWS_parentWindow = window.opener.getFieldValue("WS_parentWindow");
	window.opener.updatedFormsField("WS_parentWindow", enquiryWindowName);
	
	// Set the WS_doResize on the opening window to 'yes' so when it submits, the context enquiry is resized
	var openerWS_doResize = window.opener.getFieldValue("WS_doResize");
	window.opener.updatedFormsField("WS_doResize", "yes");
	
	// Submit the request to the Browser Servlet
	FragmentUtil.submitForm(appreqForm);

	// Remove the element from the form.
	// Helps to avoid duplicate elements inserted in form.
	appreqForm.removeChild(newElement);
	
	// Return the WS_doResize in the opening window to it's original value
	window.opener.updatedFormsField("WS_doResize", openerWS_doResize);
	
	// Return the WS_parentWindow in the opening window to it's original value
	window.opener.updatedFormsField("WS_parentWindow", openerWS_parentWindow);

	// Reset parent target back to orginal deal window
	appreqForm.target = dealWindowName;
}



/**
 * Use dynamic Html to add a row for each possible enquiry..
 * @param {TODO} ceids
 * @param {TODO} enqids
 * @param {TODO} enqdescs
 * @param {TODO} ivSkin
 * @param {TODO} realfname
 * @param {TODO} olddata
 * @param {TODO} fname
 */ 
function runContext(	ceids,enqids,enqdescs,	ivSkin, realfname,olddata,fname,formId){



// Loop through each item...

	enqCount = 0;
	enqDescCount = enqdescs.split("_");
	
	while (enqCount!=-1 && enqCount <enqDescCount.length)
	{
		enqCount = enqCount +1;

	
		field(ceids,'_',enqCount);
		ceid = ReturnString;

		field(enqids,'_',enqCount);
		enqid = ReturnString;

		field(enqdescs,'_',enqCount);
		enqdesc = ReturnString;

		if (ceid=="") {
			enqCount=-1 ; // All done so get out
		} else {
			addContextEnquiry(enqdesc, enqid,ceid,realfname,olddata,fname,formId) ; // Add the item!
		}
	}
}


