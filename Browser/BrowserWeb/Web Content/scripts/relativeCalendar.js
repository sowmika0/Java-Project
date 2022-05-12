/** 
 * @fileoverview Deals with all of the javascript functions used for relative calendar popup control.
 *
 * @author Cerun
 * @version 0.1 
 */

 var relCalDropWidth_GLOBAL = 325;
 var relCalDropHeight_GLOBAL = 230;
 
 
 function initRelativeCalendar(dropFieldId, fragmentName)
 {	
 	var dropformName = FragmentUtil.getForm(currentForm_GLOBAL, "",  fragmentName);
  	var dropFieldFormId = dropformName.getAttribute("id");
 	var dropField = getFormField( dropFieldFormId, dropFieldId );
 	var dropFieldValue = dropField.value;
 	pickedDate = "";	//Reset previous value while loading agian.
	
		
 	if (dropFieldValue != ""  && dropFieldValue != "undefined" ){
	       populate_reldate(dropFieldValue);
 	}
 	//For showing the completed popup window.
 	var fquFocus = window.document.getElementById(dropFieldId + ":div");
	if (fquFocus != undefined)
	{
		fquFocus.focus();
	}
 }
 
 function relativeCalendarDisplay2(tabForm)
 {
	// Get the drop field
	 //var thisField = dropFieldId.target || dropFieldId.srcElement;
	 currentForm_GLOBAL = tabForm;
	var relCalFieldObject = "fieldName:ID.COMP.6";
	freqDropDown_GLOBAL = relCalFieldObject;
	var test = document.getElementsByTagName("img");
	for (i=1; i < test.length; i++){
		if ("fieldName:expansionField" == test[i].getAttribute("id")){
					relCalFieldObject = test[i];
		break;
		}
	}
	dropRelativeCalendar(relCalFieldObject);
 }
 
 /**
 * Invoked from the contract screen of the relative field control.
 * @param {TODO} thisEvent
 */ 
 function relativeCalendarDisplay(dropFieldId)
 {
	// Get the drop field
	 var thisField = dropFieldId.target || dropFieldId.srcElement;
	var relCalFieldObject = thisField.getAttribute("reldropfieldname");
	freqDropDown_GLOBAL = relCalFieldObject;
	if(dropFieldId.type == "change")
	{
		currentDropDown_GLOBAL = "";
	}
	
	var test = document.getElementsByTagName("img");
	for (i=1; i < test.length; i++){

		if ("fieldName:expansionField" == test[i].getAttribute("id")){
					relCalFieldObject = test[i];
		break;
		}
	}
	dropRelativeCalendar(relCalFieldObject);
	//CODE SHOULD BE HERE, AFTER LOADING ONLY IT WILL WORK
	
 }	
 
 /**
 * Invoked from the contract screen of the relative field control.
 * @param {TODO} thisEvent
 */ 
 function dropRelativeCalendar(dropFieldId)
 {
    // Set general form params
   	var dropWidth = relCalDropWidth_GLOBAL;
   	var dropHeight = relCalDropHeight_GLOBAL;
   	var multiRequest = getFormField("generalForm", "multiPane"); 
	
	if(multiRequest && currentForm_GLOBAL && currentForm_GLOBAL.indexOf("newPropertyDateForm_") == -1)
	{
		var oldform = currentForm_GLOBAL;
		var tempvar = currentForm_GLOBAL.split("-")[1];
		var tempvar2 = "newPropertyDateForm_" + tempvar;
		var newForm = FragmentUtil.getForm(tempvar2);
		//Do the save only when the form exist.
		if(newForm)
		{
		setFormFieldValue( tempvar2,"savedForm",currentForm_GLOBAL);
		currentForm_GLOBAL = tempvar2;
		}
	}
    var application = getFormFieldValue( currentForm_GLOBAL, "application" );
	var enqname = getFormFieldValue( currentForm_GLOBAL, "enqname" );
	var genForm = FragmentUtil.getForm("generalForm");
	var dropFieldObject = getFormField( currentForm_GLOBAL, dropFieldId );
	if(dropFieldObject == undefined)
	{
		dropFieldObject = getFormField( oldform, dropFieldId );
		currentForm_GLOBAL = oldform;
	}
	
	genForm.requestType.value = _UTILITY__ROUTINE_;
	genForm.routineName.value = _OS__GET__REL__CAL_;
	var monthYearArgs = "";
	var monthList = document.getElementById( "monthList"+dropFieldId );
	var yearList = document.getElementById( "yearList"+dropFieldId );
	if( monthList != null && yearList != null )	{
	    var month = monthList.options[monthList.selectedIndex].value;
	    var year = yearList.options[yearList.selectedIndex].text;
	    monthYearArgs = month + ":" + year;
	}
	genForm.routineArgs.value = monthYearArgs + "|" + dropFieldId + "|" + application + "|" + enqname;
	runPopupDropDown(genForm, dropFieldObject, dropWidth, dropHeight);
 	
 }
 
 
 function rel_close(dropFieldId)
 {
 	hidePopupDropDown(dropFieldId);	
 }
  
 
 function rel_save(dropFieldId)
 {
 	
 	var relOptions = document.getElementById("fieldName:rel_options").value;
 	var relOffset = document.getElementById("fieldName:rel_offset").value;
	var  relDuration = document.getElementById("fieldName:rel_duration").value;
 	var relativeDate = "";
 	
 	//If the next date is not specified, then all the options have to be inputted
 	if ((relOptions == "" &&((relOffset!="") || (relDuration!=""))) && (pickedDate == ""))
	{
	    alert("relOptions should be inputted if date not specified");
		return;
	}
	var reloff = relOffset.indexOf("-");
	if (reloff == "-1"){
		var relOffset = "+\ "+relOffset;
	}else{
		var relOffset = relOffset.replace("-","-\ ");
	}
 	relativeDate = "R_" + relOptions + "\ " + relOffset +relDuration;
	 if(relOptions  == '' && relOffset == "+ " && relDuration == ''){
	 relativeDate = "";
	 }
 	// Save it
 	//var dateValue = getFormFieldValue( currentForm_GLOBAL, dropFieldId);
	 if(pickedDate && !isNaN(pickedDate))
	 {
		relativeDate = "D_"+pickedDate;
		pickedDate = "";
	 }
	setFormFieldValue( currentForm_GLOBAL, dropFieldId, relativeDate); 
	
	 // Run any field events if the value has changed
	var dropFieldObj = document.getElementById(dropFieldId);
	var oldValue = dropFieldObj.oldValue;

	if ( relativeDate != oldValue && relativeDate != "")
	{
		doFieldChangeEvent( dropFieldObj );
	 }

	// close popup
	 fqu_close(dropFieldId);
	 var propertyName = currentForm_GLOBAL.substring(currentForm_GLOBAL.indexOf('_')+1);
	 if(propertyName != null && propertyName != '')
	 {
	    var goIcon = document.getElementById('newPropertyDateGo_'+propertyName);
		if(goIcon != null)
		{
		   goIcon.focus();
		}
	 }
	 //call the function to add new tab here
	 if(getFormFieldValue( currentForm_GLOBAL, "savedForm"))
 	 {
		currentForm_GLOBAL = getFormFieldValue( currentForm_GLOBAL, "savedForm");
		var currenttabNum = FragmentUtil.getForm(currentForm_GLOBAL).parentNode.parentNode.parentNode.id;
		currenttabNum = currenttabNum.substring(7,9);
		if(isNaN(currenttabNum))
		{
			currenttabNum = currenttabNum.charAt(0);
		}
		addTabMenu(currenttabNum,currentForm_GLOBAL.split("-")[1],currentForm_GLOBAL);
	 }
 } 
 
 function populate_reldate(reldate) 
 {
 var nextDate = "";
 var relOfsetSign = "";
 pre_reldate  = ( reldate .substring(0,1));

		if(pre_reldate  == "R"){
				reldate = ( reldate.slice(2));
            		        reldate = reldate.split(" ");
            		        var relOptions = document.getElementById("fieldName:rel_options");
				relOptions.value = reldate[0] ;
				if (reldate[1] != "" && reldate[2] != ""){
					if (reldate[1] != "+") {
						relOfsetSign = reldate[1];
						var relOfset = reldate[2];
					}
					else{
						var relOfset = reldate[2];
					}
						var rDuration = relOfset.substr((relOfset.length)-1,1);
						relDuration = document.getElementById("fieldName:rel_duration");
							switch ( rDuration )
								{
									case "D" : 
										relDuration.value= "D" ;
										break;
									case "W" :
										relDuration.value= "W" ;
										break;
									case "M" :
										relDuration.value= "M" ;
										break;
									case "Y" :
										relDuration.value= "Y" ;
										break;
									default :
										return( false );
								}
		
									var relOffset = document.getElementById("fieldName:rel_offset");
									relOffset.value  = relOfsetSign + relOfset.substr(0,(relOfset.length)-1);
				}					
			}
			
 }
 /**
 *  Function to clear the date field if relative field is selected.
 **/
 
 function clearDate(thisEvent , dropFieldId)
 {

        Myfield = thisEvent.target || thisEvent.srcElement ;
        myname =  Myfield.id ;
 	var relOptions = document.getElementById("fieldName:rel_options");
 	var relOffset = document.getElementById("fieldName:rel_offset");
	var relDur = document.getElementById("fieldName:rel_duration");
	
	 if (myname == relOptions.id || myname == relOffset.id || myname == relDur.id)
	{	
		pickedDate = "";	//Clear the background maintained picked date value.
  	}
 }
 
 function rel_savekeyboard(fieldName,theEvent)
 {
   if(theEvent.keyCode == 13)
   {
     rel_save(fieldName);
   }
 } 	
 function rel_closekeyboard(fieldName,theEvent)
 {
   if(theEvent.keyCode == 13)
   {
     rel_close(fieldName);
   }
 } 	
 function setFocusOnSave(theEvent)
{
  if(theEvent.keyCode == '9')
  {
  var relativeSaveObject = getCurrentTabField( currentForm_GLOBAL, "relDateTitle" );
	// If the field is not in the form just try a getElementById()
	
	if (!relativeSaveObject) {
		relativeSaveObject = document.getElementById("relDateTitle");
		}
		if(relativeSaveObject != null) {
		  relativeSaveObject.focus();
		}
	}
} 