/** 
 * @fileoverview This file contains the code for the FieldGroup class.<br>
 *
 * @author Mat Kusari
 * @version 1.0
 */

/**
 * 
 * <p> Field groups are sets of associated fields so called multivalues or subvalues.
 * This class identifies a group based on the id and expands or deletes a group.
 * @param {String} id the id of the group multivalue or subvalue.
 * @constructor
 * @return A new FieldGroup
 * @type FieldGroup
 */
function FieldGroup( id) {

	//------------ Variable declaration and init -------------------------------
	this.id = id;
	//
	this.groupType = "";
	//
	this.startGroupId = "";
	//
	this.endGroupId = "";
	//
	this.startGroupIdArr = null;
	//
	this.endGroupIdArr = null;
	//
	this.instanceNumber = 0;
	//
	this.firstInstanceId = "";
	//
	this.secondInstanceId = "";
	//
	this.isMultiFieldGroupRow = false;
	//
	this.isMultiValue = true;
	//
	this.expansionAttribute = "";
	// This varaible holds the meta information about this field set.
	// This will be cloned every time we perform an expand on this group.
	this.metaGroupSet = new Array();
	// Boolean var holds true if this is the first instance being expanded.
	this.isFirstInstance = true;
	// The current tab to be expanded/deleted
	this.currentTab = "";
	// Helper variable pointing to the current working instance
	this.currentInstance = "";
	//variables to save the last tab and instance that were available for the field
	this.lastAvlTab = "";
	this.lastAvlInstance = "";
	//variable set when first sub value is deleted in tab
	this.deleteFirstSubVal = "";
	//Hidden and No-Input element values initialised
	thishiddenElementId = "";
	thishiddenElement = "";
	nexthiddenElementId = "";
	nexthiddenElement = "";
	//------------ Object init -------------------------------------------------
    this.init(id);
    //
    FieldGroup.languageCodeList = getFormFieldValue( currentForm_GLOBAL, "languages").split(",");
}
/**
 * Initialises this field group object. This method gets called every
 * time we expand or delete to update the current state of the object.
 */
FieldGroup.prototype.init = function( id)
{
	this.currentInstance = "";
	// Split the id in three parts.
	var idArr = id.split("_");
	// Validate id.
	if( idArr.length != 3)
	{
		alert( "Wrong Id being passed @FieldGroup.prototype.init: " + id);
	}
	
	// Store this id for static context reference.
	FieldGroup.currentInstanceId = id;
	// Update object state
	this.groupType = idArr[0];
	this.startGroupId = idArr[1];
	this.endGroupId = idArr[2];
	// Decode start id.
	this.startGroupIdArr = this.startGroupId.split(".");
	this.endGroupIdArr = this.endGroupId.split(".");
	// Is it a multivalue or subvalue.
	if( this.groupType == "M" )
	{
		this.isMultiValue = true;
		// Set reference attribute i.e. mvDetails or svDetails.
		this.expansionAttribute = "mvDetails";
		// Set this as a reference for static methods. 
		FieldGroup.isMultiValue = true;
		// Work out which instance we are dealing with.
		this.instanceNumber = this.startGroupIdArr[1] - 1;
		// Build the string for the first and second instance id. Refernce purposes.		
		this.firstInstanceId = this.groupType + "_" + this.startGroupIdArr[0] + ".2_" + this.endGroupIdArr[0] + ".2";
		this.secondInstanceId = this.groupType + "_" + this.startGroupIdArr[0] + ".3_" + this.endGroupIdArr[0] + ".3";
		
	}
	else 
	{
		this.isMultiValue = false;
		// Set reference attribute i.e. mvDetails or svDetails.
		this.expansionAttribute = "svDetails";
		// Set this as a reference for static methods. 
		FieldGroup.isMultiValue = false;
		// Work out which instance we are dealing with.
		this.instanceNumber = this.startGroupIdArr[2] - 1;
		// Build the string for the first and second instance id. Refernce purposes.
		this.firstInstanceId =   this.groupType + "_" 
							   + this.startGroupIdArr[0] + "." + this.startGroupIdArr[1] + ".2_"
							   + this.endGroupIdArr[0] +   "." + this.endGroupIdArr[1] +   ".2";
		this.secondInstanceId =   this.groupType + "_" 
							   + this.startGroupIdArr[0] + "." + this.startGroupIdArr[1] + ".3_"
							   + this.endGroupIdArr[0] +   "." + this.endGroupIdArr[1] +   ".3";							   
	}
	// Boolean var holds true if this is the first instance being expanded.
	this.isFirstInstance = this.isSingleInstance();

};

/**
 * Creates a new group to be inserted in the screen via DHTML.
 * @param instanceId The id of the instance at which we are expanding.
 * @return void
 * @type void
 */
FieldGroup.prototype.expand = function(instanceId)
{
	// Update action.
	updateExpansionHistory = 1;
	FieldGroup.isExpand = true;
	// Init this object with this instances properties.
	this.init( instanceId);
	if (tabList_GLOBAL == null || tabList_GLOBAL == "undefined" || tabList_GLOBAL.length < 1 )
	{
		tabList_GLOBAL = getDisplayedTabs(currentForm_GLOBAL);
	}
	//Check for AA frames
	var multiRequest = getFormField("generalForm", "multiPane");
	//if from AA frames then,
	
	var appTabList_GLOBAL = "";
	
	if( multiRequest != null )                                                                                  
	{
		appTabList_GLOBAL = getDisplayedTabs(currentForm_GLOBAL);                
	}
	//The list of tabs in an AA frame
	if (appTabList_GLOBAL)
	{                                                                                                                             
		for( var i = 0; i < appTabList_GLOBAL.length; i++)
		{
			this.currentTab = appTabList_GLOBAL[i];                                                                 
	        this.expandTab(instanceId);
		}                                                                                                                               
	}
	else
	{
		for( var i = 0; i < tabList_GLOBAL.length; i++)
		{
			// Set the tab to expand on
			this.currentTab = tabList_GLOBAL[i];
			// Perform the expansion
			this.expandTab( instanceId);
		}
	}
	
	// Set the expansionHistory variable so that T24 adds the group into R.NEW
	var expansionHistory = getFormField( currentForm_GLOBAL, "expansionHistory");
	// The first entry should not start with a double underscore, it is a separator
	if(updateExpansionHistory)
	{
		if (expansionHistory.value == "")
		{
			expansionHistory.value += "ARC_EXPAND_" + instanceId;
		} 
		else 
		{
			expansionHistory.value += "__ARC_EXPAND_" + instanceId;
		}
	}
};
/**
 * Creates a new group to be inserted in the screen via DHTML.
 * Only for the specified tab
 * @param instanceId The id of the instance at which we are expanding.
 * @param tabId The id of the tab we are trying to expand on.
 * @return void
 * @type void
 */
FieldGroup.prototype.expandTab = function( instanceId)
{			
	// Get the current instance of this field group.
	this.currentInstance = this.getInstanceAt( instanceId );
	if( this.currentInstance.length == 0)  // Validation
	{
		//alert("Can't find instance to expand: " + instanceId);
		return;
	}

	// Check if this is a language field and if so check if it can be expanded. only for multivalues.
	if( this.isLanguageExpansion( this.currentInstance[0], instanceId) )
	{
		var lastInstanceNumber =  this.getLastInstanceId( this.firstInstanceId);
		lastInstanceNumber =  FieldGroup.splitAt( lastInstanceNumber , "_", 1);
		if( this.isMultiValue )
		{
			lastInstanceNumber = FieldGroup.splitAt( lastInstanceNumber , ".", 1) - 1;
		}
		else
		{
			lastInstanceNumber = FieldGroup.splitAt( lastInstanceNumber , ".", 2) - 1;
		}
		
		// Have we expanded enough for this languge field?
		if(  lastInstanceNumber > ( FieldGroup.languageCodeList.length - 1 ) )
		{
			alert( getFormFieldValue( currentForm_GLOBAL, "languageUndefined") );
			updateExpansionHistory = 0;
			//Dont expand as we are a language field and have reached the limit.
			return;
		}
	} 
	
	var parentElement = this.currentInstance[0].parentNode;
	var rowToInsertBefore = this.currentInstance[this.currentInstance.length-1].nextSibling;
    
   	// Is this the first instance being expanded
    if( this.isFirstInstance )
    {
   		// Add a delete button.
   		this.addButton( this.currentInstance[0], instanceId, this.isMultiValue, false);
    }

	// Update the instances below if there are any before we insert the new one.
	this.updateInstancesBelow( instanceId, 1);
    
    // Perform a clone on the current row.
	this.cloneMetaGroupSet( instanceId);	

    // Update the clone with new attributes.
	this.updateInstanceAttributes( this.metaGroupSet, instanceId, 1);
	
	// Loop through the clone and insert each element into the document after the cloned node.
	for ( var i = 0; i < this.metaGroupSet.length; i++ ) 
	{
		parentElement.insertBefore( this.metaGroupSet[i], rowToInsertBefore);
	}
	
	// Focus on the right field
	if ( currentTabName_GLOBAL == this.currentTab )
	{
	this.fieldFocus( this.metaGroupSet[0]);
	}
	
};

/**
 *  Deletes a group in the screen via DHTML.
 *  @param instanceId The id of the instance at which we are deleting.
 *  @return void
 *  @type void
 */
FieldGroup.prototype.collapse = function( instanceId)
{
	// Update action.
	FieldGroup.isExpand = false;
	// Store the original id in case its modified.
	var expansionStr = instanceId;
	// Update this object with the current event
	this.init( instanceId);
	//To find whether it is a request from an AA frame
	var multiRequest = getFormField("generalForm", "multiPane");
	if( multiRequest != null )                                                                                  
	{
	   var appTabList_GLOBAL = getDisplayedTabs(currentForm_GLOBAL);                
	}		
	//If AA frame then loop through the tabs of a single AA frame
	if (appTabList_GLOBAL)
	{                                                                                                                             
		for( var i = 0; i < appTabList_GLOBAL.length; i++)
		{
			this.currentTab = appTabList_GLOBAL[i];                                                                 
	        this.collapseTab(instanceId);
		}                                                                                                                               
	}
	else
	{
		for( var i = 0; i < tabList_GLOBAL.length; i++)
		{
			// Set the tab to expand on
			this.currentTab = tabList_GLOBAL[i];
			// Perform the expansion
			this.collapseTab( instanceId);
		}
	}
	//restore the tabname and the instance that were obtained
	this.currentTab = this.lastAvlTab;
	this.currentInstance = this.lastAvlInstance;	
	//dont add the expansion history again
	if(this.deleteFirstSubVal)
	{
		return;
	}
	// Do extra stuff because this is a delete operation.	
	if( this.isMultiValue )
	{
		// If this is a multivalue change '_' to ':' because we are doing
		// a delete and the server expects it in this format.
		expansionStr = expansionStr.replace(/_/g,":");
	}
	else
	{
		
		// If this is a subvalue then we need to build a different string for deleting 
		expansionStr = expansionStr.replace(/_/g,":");
		var mvId = this.currentInstance[0].getAttribute("mvList");
		mvId = mvId.replace(/_/g,":");
		// Build the instance id like so: M:4.2:8.2_S:4.1.2:4.4.2. The server expects it this way!
		expansionStr = mvId + "_" + expansionStr;
	}
	
	// Set the expansionHistory variable so that T24 adds the group into R.NEW
	var expansionHistory = getFormField( currentForm_GLOBAL, "expansionHistory");
	// The first entry should not start with a double underscore, it is a separator
	if (expansionHistory.value == "")
	{
		expansionHistory.value += "ARC_DELETE_" + expansionStr;
	} 
	else 
	{
		expansionHistory.value += "__ARC_DELETE_" + expansionStr;
	}
};


/**
 *  Deletes a group in the screen via DHTML.
 *  @param instanceId The id of the instance at which we are deleting.
 *  @return void
 *  @type void
 */
FieldGroup.prototype.collapseTab = function( instanceId)
{
	// Get current instance of this group
	this.currentInstance = this.getInstanceAt( instanceId );
	// Validation
	if( this.currentInstance[0] == null)
	{
		//alert( "Can't find instance to collapse @FieldGroup.prototype.collapse: " + instanceId);
		return;
	}
	//Save the tab name and instance
	this.lastAvlTab = this.currentTab;
	this.lastAvlInstance = this.currentInstance;
	// Parent to delete from.
	var parentElement = this.currentInstance[0].parentNode;	
	

	if( !this.isMultiValue && instanceId == this.firstInstanceId )
	{
		// Don't do anything if the field has not been expanded at all.
		if( !this.isSingleInstance())
		{
			this.collapseFirstSubValueInstance();
		}
		// This is a special scenario so return.
		return;
	}
/**
 *  while Deleting Multi value field changed values
 *  are added to form
 */
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	var InstBefDel = new Array();
	var InstAftDel = new Array();
	var DelInst = instanceId.split(".");
	for (var i=0 ; i< parentElement.rows.length ;i++)
	{
		var mvRow = parentElement.rows[i];
		if (mvRow)
		{
			if(mvRow.getAttribute('mvlist')){
				var IncInst = mvRow.getAttribute('mvlist').split(".");
				if(IncInst[0] == DelInst[0])
				{
					InstBefDel[InstBefDel.length] = mvRow;
				}
			}
			if(mvRow.getAttribute('svlist'))
			{
				var SvInst = mvRow.getAttribute('svlist').split(".");
				if(SvInst[0] == DelInst[0])
				{
					InstBefDel[InstBefDel.length] = mvRow;
				}	
			}
		}
	}
		
	// Loop through the this.currentInstance array and remove each row from the document.
	for ( var i = 0; i < this.currentInstance.length; i++ ) 
	{
		parentElement.removeChild( this.currentInstance[i] );
	}
	//compare both instance and add changed field to form
	for (var i=0 ; i< parentElement.rows.length ;i++)
	{
		var mvRow1 = parentElement.rows[i];
		if (mvRow1)
		{
			if(mvRow1.getAttribute('mvlist')){
				var IncInst1 = mvRow1.getAttribute('mvlist').split(".");
				if(IncInst1[0] == DelInst[0])
				{
					InstAftDel[InstAftDel.length] = mvRow1;
				}
			}
			if(mvRow1.getAttribute('svlist'))
			{
				var SvInst1 = mvRow1.getAttribute('svlist').split(".");
				if(SvInst1[0] == DelInst[0])
				{
					InstAftDel[InstAftDel.length] = mvRow1;
				}	
			}
		}
	}
	var fnlChgdFlds = "";
	for ( var i = 0,j=0; i<InstBefDel.length && j<InstAftDel.length; i++,j++)
	{
		var rowElmt = InstBefDel[i];
		var inputElmt = rowElmt.getElementsByTagName('input');
		if (InstBefDel[i] != InstAftDel[j])
		{
			for (s = 0; s < inputElmt.length; s++)
			{
				if(inputElmt[s].type == "text"){
				fnlChgdFlds = inputElmt[s].id;
				changedFields = appreqForm.changedFields.value;
				if (changedFields)
				{
					if(changedFields.indexOf(fnlChgdFlds) == -1)
					{
						appreqForm.changedFields.value = changedFields + " " + fnlChgdFlds;
					}
				}
				else
				{
					appreqForm.changedFields.value = fnlChgdFlds;
				}
			}
			}
		}
	}
	
	// Update the instances below if there are any.
	this.updateInstancesBelow( instanceId, -1);
	
	// Is this the last instance left?
	if( this.isSingleInstance() )
	{
		// Get the first instance
		var firstInstance = this.getInstanceAt( this.firstInstanceId);
		// Remove the delete button of the first row.
		if( this.isMultiValue )
		{
			this.removeButton( firstInstance[0], this.firstInstanceId, "mvDeleteClient");	
		}
		else
		{		
			this.removeButton( firstInstance[0], this.firstInstanceId, "svDeleteClient");			
		}
		// 
		if ( currentTabName_GLOBAL == this.currentTab )
		{
			this.fieldFocus( firstInstance[0]);
		}
		
	}
	else
	{
		var aboveInstance = this.getInstanceAt( FieldGroup.getNewId( instanceId, -1));
		if ( currentTabName_GLOBAL == this.currentTab )
		{
			this.fieldFocus( aboveInstance[0]);
		}
	}
	


	// If the user has tried to delete the first instance 
	if( instanceId == this.firstInstanceId )
	{
		var thisInstance = this.getInstanceAt( instanceId);
		if( thisInstance[0] != null)
		{
			var thisRowId = thisInstance[0].getAttribute( "mvList");
			var instanceNumber = FieldGroup.splitAt(  FieldGroup.splitAt( thisRowId, "_", 1), ".", 1);
			if( instanceNumber > 2)
			{
				// Add mv expand button.
				this.addButton( thisInstance[0], thisRowId, true, true);
				// Add mv delete button.
				this.addButton( thisInstance[0], thisRowId, true, false);	
			}
			else
			{
				// Add mv expand button.
				this.addButton( thisInstance[0], thisRowId, true, true);
			}
			
						
			for( var i = 0; i < thisInstance.length; i++) 
			{ 
				var rowElement = thisInstance[i]; 
				var oldRowElement = this.lastAvlInstance[i]; 
				var elementChildren = rowElement.childNodes; 
				var oldElementChildren = oldRowElement.childNodes; 
				
				if(elementChildren.length == oldElementChildren.length)
				{
					for(var j = 0; j < elementChildren.length; j++ ) 
					{ 
						var aChild = elementChildren[j]; 
						if( aChild.nodeType == 1) 
						{ 
							var thisChildAttr = aChild.getAttribute( this.expansionAttribute ); 
							if( thisChildAttr != null && thisChildAttr != instanceId ) 
							{ 
								//Add the remaining field data from old to new one. 
								var oldtdElements = oldElementChildren[j].childNodes; 
								while(oldtdElements.length > 0)	// Keep adding elements until there is none.  
								{ 
								aChild.appendChild( oldtdElements[0]); 
								} 
							} 
						} 
					}
				}
				
				if(elementChildren.length < oldElementChildren.length)		//After validation these lengths will differ.
				{
					var rem = elementChildren.length;
					var firstOne = rowElement.children[0];
					
					while(oldElementChildren.length > rem)
					{
						if((firstOne.className != "empty"))		//To check this MV is first one in tr or not.
						{
								if (isInternetExplorer())		//add all remaining <td> nodes at the end.
								{
								var enChild = oldElementChildren[rem];
								rowElement.appendChild(enChild);
								}
								else
								{
								var enChild = oldElementChildren[rem-1];
								rowElement.insertBefore(enChild,rowElement.lastChild);
								}
						}
						else
						{
							rowElement.removeChild(rowElement.children[0]);	//remove the first blank td.
							var insertNod = rowElement.children[0];			//Pick the node before which we have to add the nodes.
							for(var j = 0; j < oldElementChildren.length; j++ )
							{
								if(isInternetExplorer()){
									var fChild = oldElementChildren[0];
								}
								else{
									var fChild = oldElementChildren[1];
								}
								if( fChild.nodeType == 1) 
								{ 
									var thisChildAttr = fChild.getAttribute( this.expansionAttribute ); 
									if( thisChildAttr != instanceId ) 
									{
										rowElement.insertBefore(fChild,insertNod);
									}
									else
									{
										if((fChild.className == "empty"))
										{
										rowElement.insertBefore(fChild,insertNod);	//for alignment purpose if exist any empty td.
										}
										break;	//added all first coming <td> nodes.
									}
								}

							}
							//------ add remaining nodes if exist in that row.
							var newArr = new Array();	//pick all the remaining field elements to an array.
							for (k in oldElementChildren)
							if(oldElementChildren[k])
							if(oldElementChildren[k].nodeType == 1 && (oldElementChildren[k].getAttribute( this.expansionAttribute ) != instanceId)) 
							newArr.push(oldElementChildren[k]);
							
							var indx = 0;
							while(newArr.length > 0)
							{
								var enChild = newArr[indx];
								
								if(enChild)
								{
									if (isInternetExplorer())
									{
										rowElement.appendChild(enChild);
									}else
									{
										rowElement.insertBefore(enChild,rowElement.lastChild);
									}
								indx++;
								}else
								{break;}
							}
							break;
						}
					}	// for while loop
				}	// < if condition.
			}	//for each row.
		}	
	}	
};

/**
 * This method checks to see if the given row contains a language field
 * @param row The row we want to focus on.
 * @return true if the row contains a langauge field, false otherwise
 * @type Boolean
 */
FieldGroup.prototype.isLanguageExpansion = function( row, instanceId)
{
	var allElements = new Array();
	var inputElements = row.getElementsByTagName( "input");
	var textAreaElements = row.getElementsByTagName( "textarea");
	// Append the inputElements.
	for( var i = 0; i < inputElements.length; i++)
	{
		allElements[ allElements.length] = inputElements[i];
	}
	// Append the possible textarea elements to inputElements as they are input type too.
	for( var i = 0; i < textAreaElements.length; i++)
	{
		allElements[ allElements.length] = textAreaElements[i];
	}	
		
	var isLanguageField = false;
	var inputElement = null;
	var parentElement = null;

	for( var i = 0; i < allElements.length; i++)
	{
		inputElement = allElements[i];
		isLanguageField = ( inputElement.getAttribute( "lng") == "y" );
		parentElement = inputElement.parentNode;

		var inputElementId = parentElement.getAttribute( this.expansionAttribute);
		if( inputElementId == instanceId)
		{
			// Special scenario where this is a multivalue, subvalue and langugage field.
			// In this case if we are a multivalue expansion we don't want to consider it as a language expansion,
			// as the subvalue is really a language field and not the multivalue. 
			if( this.isMultiValue && parentElement.getAttribute( "svDetails") != null && isLanguageField )
			{
				// return false as we are not a language expansion since we are a multivalue.
				return false;	
			}
			else
			{
				if( isLanguageField )
				{
					// If we reached this point return true as the field is marked as a language field.
					return true;
				}
			}	
		}
	}	

	return false;
};

/**
 * This method sets the focus on the first field it comes across in a given row.
 * @param row The row we want to focus on.
 * @return true if possible to set focus, false otherwise.
 * @type Boolean
 */
FieldGroup.prototype.fieldFocus = function( row)
{
	if( row != null)
	{
		var inputElements = row.getElementsByTagName( "input");
		if( inputElements.length > 0)
		{
			// Get the field from the given row and try to focus on it.
			var focusField = inputElements[0];
			var fieldId = "";
			//To hide the empty text box border for enrichment only fields
			if(isInternetExplorer()){			
				fieldId = focusField.name;
			} else {
				fieldId = focusField.id;
			}
			var enriFieldName = fieldId.replace("fieldName:", "enri_");
			var enriObject = document.getElementById(enriFieldName);
			// Only focus on fields that are not hidden.
			if( focusField.type != "hidden" )
			{
				try
				{
					if(isInternetExplorer()) {
						setTimeout(function() { focusField.focus(); }, 100);
					}else {
						focusField.focus();
					}
				}
				catch (e)
				{
					// Ignore if we can't focus on the field. It might be a non displayed field.
				}
			}
			return true;
		} else {
			var selectElements = row.getElementsByTagName( "select");
			if( selectElements.length > 0)
			{	
				// Get the field from the given row and try to focus on it.
				var focusField = selectElements[0];
				var fieldId = "";
				//To hide the empty text box border for enrichment only fields
				if(isInternetExplorer()){			
					fieldId = focusField.name;
				} else {
					fieldId = focusField.id;
				}
				// Only focus on fields that are not hidden.
				if( focusField.type != "hidden" )
				{
					try
					{
						if(isInternetExplorer()) {
							setTimeout(function() { focusField.focus(); }, 100);
						}else {
							focusField.focus();
						}
					}
					catch (e)
					{
						// Ignore if we can't focus on the field. It might be a non displayed field.
					}
				}
				return true;
			}		
		}		
	}	
	return false;
};

/**
 * This method is used to take care of a special scenario where the user
 * is trying to delete the first subvalue in a horisontal row like ENQUIRY,DES for example.
 * We don't actually delete the row itself but we delete the last subvalue
 * and copy across the values at the top.
 * @return void
 * @type void
 */
FieldGroup.prototype.collapseFirstSubValueInstance = function()
{
	var aInstance = this.getInstanceAt( this.firstInstanceId);
	var inputElements = new Array();
	var selectElements = new Array();
	var tmpArr = null;

	
	for( var i = 0; i < aInstance.length; i++)
	{
		tmpArr = aInstance[i].getElementsByTagName( "input");
		for( var j = 0; j < tmpArr.length; j++)
		{
			var type = tmpArr[j].getAttribute( "type");
			if( type == "text" )
			{
				// Updating the subvalue element value on the collapse of first subvalue.
				var subdetails = tmpArr[j].offsetParent;
				var subvalueid = subdetails.getAttribute("svdetails");
				if((subvalueid != null) && (subvalueid != "") && (subvalueid == this.firstInstanceId))
				{
					inputElements[ inputElements.length] = tmpArr[j];
				}
			}
			else
			{
				inputElements[ inputElements.length] = tmpArr[j];
			}
		}
		
		tmpArr = aInstance[i].getElementsByTagName( "select");	
		for( var j = 0; j < tmpArr.length; j++)
		{
			inputElements[ inputElements.length] = tmpArr[j];
		}
	}
	
	for( var i = 0; i < inputElements.length; i++ )
	{
		this.updateInputElementValues( inputElements[i]);
	}
	
	// Now delete the last subvalue
	var lastInstanceId = this.getLastInstanceId( this.firstInstanceId);
	this.collapse( lastInstanceId);
	//sub value deleted . set the variable so that we dont update the expansion history again
	this.deleteFirstSubVal = 1;
};

/**
 * @return void
 * @type void
 */
FieldGroup.prototype.updateInputElementValues = function( element)
{
	// Validation 
	if( element == null) return;
	if( element.nodeType != 1) return;
	
	var elementList = new Array();
	elementList[ elementList.length] = element;
		
	var nextElementName = element.getAttribute( "name");
	var nextElemNameArr = null;
	var nextElement = element;
	while( nextElement != null)
	{	
		nextElemNameArr = nextElementName.split(":");
		nextElemNameArr[3] = ( nextElemNameArr[3] * 1) + 1;
		nextElementName = nextElemNameArr[0] +":"+ nextElemNameArr[1] +":"+ nextElemNameArr[2] +":"+ nextElemNameArr[3];
		nextElement = getFormField( currentForm_GLOBAL, nextElementName);
		if( nextElement != null)
		{
			elementList[ elementList.length] = nextElement;
		}
	}
	
	var thisElement = null;
	var nextElement = null;
	var thishiddenElement = null;
	var nexthiddenElement = null;
	for( var i = 0; ( i < elementList.length - 1 ); i++)
	{
		thisElement = elementList[i];
		var ElemType = thisElement.getAttribute( "type");
		var thisElementId = thisElement.name;
		var thisElemIdArr = thisElementId.split(":");
		if (ElemType == "hidden")
		{
			var thishiddenElementId = "disabled_" + thisElemIdArr[1] + ":" + thisElemIdArr[2]+":"+thisElemIdArr[3];
			thishiddenElement = document.getElementById(thishiddenElementId);
		}
		var thisEnriElementId = "enri_" + thisElemIdArr[1] + ":" + thisElemIdArr[2]+":"+thisElemIdArr[3];
		var thisEnriElement = document.getElementById(thisEnriElementId);
		nextElement = elementList[i + 1];
		var nextElementId = nextElement.name;
		var nextElemIdArr = nextElementId.split(":");
		if (ElemType == "hidden")
		{
			var nexthiddenElementId = "disabled_" + nextElemIdArr[1] + ":" + nextElemIdArr[2]+":"+nextElemIdArr[3];
			nexthiddenElement = document.getElementById(nexthiddenElementId);
		}
		var nextEnriElementId = "enri_" + nextElemIdArr[1] + ":" + nextElemIdArr[2]+":"+nextElemIdArr[3];
		var nextEnriElement = document.getElementById(nextEnriElementId);
		thisElement.value = nextElement.value;
		if (thishiddenElement && nexthiddenElement != null)
		{
			thishiddenElement.innerHTML = nexthiddenElement.innerHTML;
			// to set the visibility based on type(no-input fields)
			if (thishiddenElement.innerHTML == "")
			{
				thishiddenElement.style.visibility = "hidden";
			}
			else
			{
				thishiddenElement.style.visibility = "";
			}
		}
		if (thishiddenElement && nexthiddenElement == null)
		{
			if (isFirefox())
			{
				thishiddenElement.removeChild(thishiddenElement.childNodes[0]);
				thishiddenElement.style.visibility = "hidden";
			}
			else
			{
				thishiddenElement.removeNode(true);
			}
		}
		if (thishiddenElement == null && nexthiddenElement)
		{
			var newelement = document.createElement("span");
			newelement.setAttribute( "id", thishiddenElementId);
			newelement.setAttribute( "class", "disabled_dealbox");
			newelement.style.width = "2.09em";
			newelement.innerHTML = nexthiddenElement.innerHTML;
			thisElement.parentNode.appendChild(newelement);
		}
		if(thisEnriElement && nextEnriElement)
		{
			thisEnriElement.style.visibility= "visible";
			if(thisEnriElement.getAttribute("enrichmentOnly") != null)
			{
				var enriWidth = thisEnriElement.getAttribute("colspan");
				if(enriWidth!=null)
				{
					thisEnriElement.style.width= (enriWidth*7)+"px";
				}
				thisEnriElement.style.display="inline-block";
			}
			thisEnriElement.innerHTML = nextEnriElement.innerHTML;	
		}
	}
};

/**
 * Returns the last instance id for the given id.
 * @param instanceId The id at which to start searching.
 * @return void
 * @type void
 */
FieldGroup.prototype.getLastInstanceId = function( instanceId)
{
	// Find the last instance of this group.
	var lastInstanceId = instanceId;
	var nextInstance = null;
	var moreInstances = true;
	var instanceIndex = instanceId;
	while( moreInstances )
	{
		instanceIndex = FieldGroup.getNewId( instanceIndex, 1);
		nextInstance = this.getInstanceAt( instanceIndex);
		if( nextInstance.length > 0)
		{
			//Keep updatign the id if we find more instnaces.
			lastInstanceId = instanceIndex; 
		}
		else
		{
			moreInstances = false;
		}
	}
	
	return lastInstanceId;
};
/**
 * Updates the attributes of the instances below the given instanceId.
 * @return void
 * @type void
 */
FieldGroup.prototype.updateInstancesBelow = function( instanceId, incrementBy)
{
	
	// Get a list of instances to be updated
	var updateInstanceList = new Array();
	var updateInstanceIdList = new Array();
	var nextInstance = null;
	var moreInstances = true;
	var instanceIndex = instanceId;
	while( moreInstances )
	{
		instanceIndex = FieldGroup.getNewId( instanceIndex, 1);
		nextInstance = this.getInstanceAt( instanceIndex);
		if( nextInstance.length > 0)
		{
			updateInstanceList[updateInstanceList.length] = nextInstance;
			updateInstanceIdList[updateInstanceIdList.length] = instanceIndex;
		}
		else
		{
			moreInstances = false;
		}
	}
	// Update the list of instances with corresponding attributes.
	var idIndex = "";
	var instanceObj = null;
	for( var i = 0; i < updateInstanceList.length; i++ )
	{
		this.updateInstanceAttributes( updateInstanceList[i], updateInstanceIdList[i], incrementBy );
	}
	
};

/**
 * Returns true if a given instance is the first and only multivalue in it's range.
 * The basic idea is that it doesn't matter which instance id gets passed in, we just need to know if there are any
 * instances after the first instance. So get the first instance and check for subsequent instances.
 * @return true or false
 * @type boolean
 */
FieldGroup.prototype.isSingleInstance = function() 
{
	// Get the second instance.
	var secondInstance = this.getInstanceAt( this.secondInstanceId);

	// Does the second isntance exist?
	if( secondInstance.length == 0)
	{
		return true;
	}
	// Return false since there is a second instance.
	return false;	
};
/**
 * Updates the attributes of the current group.
 * @return void
 * @type void
 */
FieldGroup.prototype.updateInstanceAttributes = function( instanceObj, instanceId, incrementBy)
{

	var rowElement = null;
	// Update the current instance for attribute updating.
	FieldGroup.currentInstanceId = instanceId;
	
	for( var i = 0; i < instanceObj.length; i++)
	{
		rowElement = instanceObj[i];
		FieldGroup.renameElementDeeply( rowElement, instanceId, incrementBy);
	}	
};

/**
 *  This method removes any elements in the rows that do not
 *  belong to this group. I.e. 2 subvalue fields spanning accross same row,
 *  we are only interested in the subvalue on this group and not all the elements.
 *  @return void
 *  @type void 
 */
FieldGroup.prototype.initMetaGroupSet = function( instanceId)
{
	var rowElement = null;
	var elementChildren = null;
	
	for( var i = 0; i < this.metaGroupSet.length; i++)
	{
		rowElement = this.metaGroupSet[i];
		elementChildren = rowElement.childNodes;
		// Go through the children removing the ones that do not belong to this row.
		for(var j = 0; j < elementChildren.length; j++ )
		{
			var aChild = elementChildren[j];
			// Nodetype describes a type of html element. We only want nodetype 1 which includes 'td' in the set.
			if( aChild.nodeType == 1)
			{
				var thisChildAttr = aChild.getAttribute( this.expansionAttribute );
				// Clear the content of the td if it does not belong to this group.
				if( thisChildAttr != instanceId )
				{
					//Remove the contents of this td.
					var tdElements = aChild.childNodes;
					// Keep removing elements until there is none.
					while( tdElements.length > 0)
					{
						aChild.removeChild( tdElements[ tdElements.length - 1]);
					}
				}
			}
		}	
		// Remove any values that are in input fields.
		var inputElements = rowElement.getElementsByTagName( "input");
		for( var j = 0; j < inputElements.length; j++)
		{
			inputElements[j].value = "";
			inputElements[j].setAttribute("oldValue","");
			if( inputElements[j].checked == true)
			{
				inputElements[j].checked = false;	
			}
		}
		// Remove any values that are in select fields (comboboxes).
		var selectElements = rowElement.getElementsByTagName( "select");
		for( var j = 0; j < selectElements.length; j++)
		{
			selectElements[j].value = "";
			selectElements[j].setAttribute("oldValue","");
		}
		// Remove any values that are in textArea elements (multiline textfields).
		var textAreaElements = rowElement.getElementsByTagName( "textArea");
		for( var j = 0; j < textAreaElements.length; j++)
		{
			textAreaElements[j].value = "";
			textAreaElements[j].setAttribute("oldValue","");	// old value is cleared for textarea fields.
		}
		// Remove any values that are in enrichments.
		var spanElements = rowElement.getElementsByTagName( "span");
		for( var j = 0; j < spanElements.length; j++)
		{
			var spanElem = spanElements[j];
			var spanId = spanElem.getAttribute( "id");
			if( spanId != null && spanId != "" && spanId.indexOf( "enri_") > -1)
			{
				// Clear out the enrichment.
				if( spanElem.firstChild != null)
				{
					spanElem.firstChild.nodeValue = "";
				}
			}
			else if(spanId != null && spanId != "" && spanId.indexOf("disabled_") > -1 )
            {
                // Clear the noinput fields on expansion
				spanElements[j].innerHTML = "";                            
				spanElements[j].setAttribute("oldValue",""); 
				spanElements[j].style.visibility = "hidden";
            }
			
		}
	}
	
};


/**
 *  This method removes any elements in the rows that do not
 *  belong to this group. I.e. 2 subvalue fields spanning accross same row,
 *  we are only interested in the subvalue on this group and not all the elements.
 *  @return void
 *  @type void
 */
FieldGroup.prototype.cloneMetaGroupSet = function( instanceId)
{
	this.metaGroupSet = new Array();
	var isMetaRow = true;
	// Get the array of rows that constitute this group.
	var groupToClone = this.getInstanceAt( instanceId);
	// Clone each row and update the meta information for this group.
	for (var i = 0; i < groupToClone.length; i++) 
	{
		// Check if this row should be added to the cloned set.		
		isMetaRow = this.isMetaRow( groupToClone[i], instanceId);
		if( isMetaRow )
		{
			// Make a 'deep copy' clone of each element
			this.metaGroupSet[this.metaGroupSet.length] = FieldGroup.cloneNodeDeeply( groupToClone[i]); 
		}
	}

	// Remove elemetns that are not required.
	this.initMetaGroupSet( instanceId);	

	// Remove multi-expand button from the cloned set if we are doing a sub-value expansion
	if( this.isMultiValue )
	{
		for( var i = 0; i < this.metaGroupSet.length; i++)
		{
			// Always remove subvalue delete buttons (if they exist) if we are cloning a multivalue.
			this.removeButton( this.metaGroupSet[i], "", "svDeleteClient");	
		}
	}
	else
	{
		// Get the multivalue association for this sub value expansion
		var multiValueId = FieldGroup.getMvDetails( this.metaGroupSet[0], instanceId);
		// Always remove these buttons because we are doing a subvalue expansion and
		// these buttons are defintatly useless.
		this.removeButton( this.metaGroupSet[0], multiValueId, "mvExpandClient");
		this.removeButton( this.metaGroupSet[0], multiValueId, "mvDeleteClient");
	}

};

/**
 * Returns an array of "<tr>" elements in the document with this id 
 * @param groupId The group set id.
 * @return an array of "<tr>" elements.
 * @type Array
 */
FieldGroup.prototype.getInstanceAt = function( instanceId) 
{	
	// Get all the elements and check for the attribute and value 
	var currFormObj = FragmentUtil.getForm( currentForm_GLOBAL );
	var  elements = currFormObj.getElementsByTagName("tr");
	
	// Is this instance a subvlaue or multivalue
	var instanceAttribute = "svList";
	if( this.isMultiValue )
	{
		instanceAttribute = "mvList";	
	}
	
	// Define the output array
	var out = new Array(); 
	for ( var i = 0; i < elements.length; i++ ) 
	{
		
		var eav = elements[i].getAttribute( instanceAttribute);
		// Only get the tr's that belong to currentTab.
		var trTabId = elements[i].getAttribute( "tabId"); 
		if (eav != null && eav.indexOf( instanceId) > -1 && trTabId == this.currentTab) 
		{
			// Appends the element to the output array.
			out[out.length] = elements[i]; 
		}
	}
	
	return out;
};
/**
 * Checks to see if this row should be added to a meta group.
 * If this row contains a subvalue which is NOT the first instance then
 * we are not interested in it as we only want the meta rows.
 * @param row The row that we want to add the delete button to.
 * @param instanceId The id of the instance we are dealing with.
 * @return True of row is meta, false otherwise.
 * @type Boolean.
 */
FieldGroup.prototype.isMetaRow = function( row, instanceId)
{
	// If we are expanding a subvalue then return as we know
	// that all subvalue rows are meta rows.
	if( !this.isMultiValue ) 
	{
		return true;
	}
	// At this point we are expanding a multivalue.
	// Get the svList attribute. 
	var rowId = row.getAttribute( "svList"); 
	var instanceNumber = "";
	// Does the row contain a subvalue field? If so that means this multivalue set has subvalues within.
	if( rowId != null )
	{
		// Get the instance number this row belongs to.
	 	instanceNumber = FieldGroup.splitAt(  FieldGroup.splitAt( rowId, "_", 1), ".", 2 );
	 	if( instanceNumber > 2)
	 	{
	 		return false;
	 	}
	 	else
	 	{
	 		return true;
	 	}
	}
	
	// If reached this point then the row is a meta row.
	return true;
};

/**
 * Creates a new element that represents a button.
 * @param row The row that we want to add the delete button to.
 * @return void.
 * @type null.
 */
FieldGroup.prototype.addButton = function( row, instanceId, isMultiValue, isExpandButton)
{
	// Create the button that we want to display.
	var aButton = this.createButton( instanceId, isMultiValue, isExpandButton);
	// Only insert the button if it isn't there already.
	if( !this.isButtonInserted( row, aButton) )
	{
		// Find the parent where to insert the new button.
		this.insertButton( aButton, row, instanceId, isMultiValue, isExpandButton);
	}

};

/**
 * Checks if a button, that we want to insert in a row, is already inserted in the given row.
 * @param row The row that we want to check if the button exists on.
 * @param aButton The button itself. Html element. Anchor.
 * @return True if button is in the row somewhere, false otherwise.
 * @type Boolean.
 */
FieldGroup.prototype.isButtonInserted = function( row, aButton)
{
	var hrefStr = "";
	var thisButton = null;
	var argsHref = aButton.getAttribute( "href");
	// Get the firstRow's children
	var aElements = row.getElementsByTagName( "a");
	for( var i = 0; i < aElements.length; i++ )
	{
		thisButton = aElements[i]; 
		hrefStr = thisButton.getAttribute( "href");		
		// Is this an expand button element?
		if ( hrefStr != null && hrefStr.indexOf( argsHref) > -1 ) 
		{
			return true;
		}

	}
	// If we reach this point return false
	return false;
};

/**
 * This method is used to find the parent of a button in the given row.
 * Browser sometimes adds a 'td' for expand/delete buttons and sometimes 
 * it puts the button in the same cell as the field itself. We don't know where
 * the button are going to be. Hence we look through the row to find a button with the
 * same instanceId as the passed id and therefore locate our parent.
 * @param row The given row.
 * @param instanceId The given instanceId.
 * @return The correct parent where to add an expand/delete button.
 * @type Object.
 */
FieldGroup.prototype.insertButton = function( buttonToInsert, row, instanceId, isMultiValue, isExpandButton)
{
	var hrefStr = "";
	var nextSibling = false;
	// Figure out which anchor we want and whether we should insert before or after.
	if( isMultiValue )
	{
		if( isExpandButton )
		{
			hrefStr = "svExpandClient";
			
		}
		else
		{
			hrefStr = "mvExpandClient";
			nextSibling = true;			
		}
	}
	else
	{
		if( isExpandButton )
		{
			hrefStr = "mvDeleteClient";		
		}
		else
		{
			hrefStr = "svExpandClient";
			nextSibling = true;
		}
	}
	
	// Build up the href with the current id included.
	hrefStr = hrefStr + "('" + instanceId + "')";
	
	// Get all anchor elemetns.
	var elements = row.getElementsByTagName( "a");
	var aButton = null;
	var thisHrefStr = "";
	var thisIdStr = "";
	for( var i = 0; i < elements.length; i++)
	{
		aButton = elements[i]; 
		thisHrefStr = aButton.getAttribute( "href");
		
		// Is this an expand button element?
		if ( thisHrefStr != null && thisHrefStr.indexOf( hrefStr ) > -1 ) 
		{
			break;
		}
	}
	
	var parentElement = aButton.parentNode;
	if( nextSibling )
	{
		parentElement.insertBefore( buttonToInsert, aButton.nextSibling);	
	}
	else
	{
		parentElement.insertBefore( buttonToInsert, aButton);
	}
	
};
/**
 * Creates a new button based on the parameters passed.
 * @param row The given row.
 * @return A new 'a' tag element which represents a button.
 * @type Object.
 */
FieldGroup.prototype.createButton = function( instanceId, isMultiValue, isExpandButton)
{
	var gifDirectory = "../plaf/images/" + getSkin() + "/deal/";
	var hrefStr = "";
	var gifStr = "";
	var title = "";
	var focusevt = "focusonKey('inline',event)";
	var blurevt = "hideTooltip(event)";
	// Do we create a multivalue button?
	if( isMultiValue )
	{
		// Is it expand or delete button
		if( isExpandButton )
		{
			hrefStr = "mvExpandClient";
			gifStr = "mvexpansion.gif";
			title = getFormFieldValue( currentForm_GLOBAL, "expandMultiString");
		}	
		else
		{
			hrefStr = "mvDeleteClient";
			gifStr = "mvdelete.gif";
			title = getFormFieldValue( currentForm_GLOBAL, "deleteMultiString");
		}
	}
	else // Create subvalue button.
	{
		// Is it expand or delete button
		if( isExpandButton )
		{
			hrefStr = "svExpandClient";
			gifStr = "svexpansion.gif";
			title = getFormFieldValue( currentForm_GLOBAL, "expandSubString");
		}	
		else
		{
			hrefStr = "svDeleteClient";
			gifStr = "mvdelete.gif";
			title = getFormFieldValue( currentForm_GLOBAL, "deleteMultiString");
		}
	}
	
	// Build up the whole gif path.
	gifStr = gifDirectory + gifStr;
	// Build up the href for this button.
	hrefStr = "javascript:" + hrefStr + "('" + instanceId +"')";
	
	// Create an 'a' element for our button.
	var aTag = document.createElement( "a");
	aTag.setAttribute( "href" , hrefStr);
	aTag.setAttribute("onfocus","focusonKey('inline',event)");
	aTag.setAttribute("onblur","hideTooltip(event)");
	var isIndex = document.getElementById("tabIndexForExpansionIcons");
	if (isIndex && isIndex.value == "NO")
	{
		aTag.setAttribute( "tabIndex" , "-1");
	}
	else
	{	
		aTag.setAttribute( "tabIndex" , "0");
	}
	
	
	// Create an image element.
	var image = document.createElement( "img");
	// TODO: Get these from the server.
	image.setAttribute( "title", title);
	image.setAttribute( "alt", title);
	image.setAttribute( "tabIndex" , "-1");
	// 
	image.setAttribute( "class", "ascell");
	image.setAttribute( "src", gifStr);
	image.style.verticalAlign = "middle";
	image.style.display = "inline-block";
	image.style.marginLeft = "2px";

	
	// Add the image to the anchor tag.
	aTag.appendChild( image);
	// Return the new button. 
	return aTag;
};

/**
 * Deletes an existing delete button.
 * @param row The row that we want to delete the delete button from.
 * @return void.
 * @type null.
 */
FieldGroup.prototype.removeButton = function( row, instanceId, buttonStr)
{
	var aButton = null;
	var hrefStr = "";
	var hrefWithId = buttonStr;
	
	if( instanceId != "")
 	{
 		hrefWithId += "('" + instanceId + "')";	
 	}
 	
	// Get the firstRow's children
	var aElements = row.getElementsByTagName( "a");
	for( var i = 0; i < aElements.length; i++ )
	{
		aButton = aElements[i]; 
		hrefStr = aButton.getAttribute( "href");
		
		// Is this an expand button element?
		if ( hrefStr != null && hrefStr.indexOf( hrefWithId ) > -1 ) 
		{
			// Get the parent of the delete button and remove it.
			var parentElement = aButton.parentNode;
			parentElement.removeChild( aButton);
		}		
	}
};

/****************** STATIC CONTENT OF THE CLASS ***********************/

/**
 * Holds a list of object instances for FieldGroup.
 */
FieldGroup.objectList = new Array();

/**
 * Holds true if the current group instance is a multivalue, false if its a subvalue.
 */
FieldGroup.isMultiValue = true;

/**
 * Holds the current id being expanded or collapsed.
 */
FieldGroup.currentInstanceId = "";

/**
 * Holds the current action i.e is this field group being expanded or collapsed.
 */
FieldGroup.isExpand = true;

/**
 *  Get the langauge list array
 */
FieldGroup.languageCodeList = "";


/**
 * Return a new or existing instance of FieldGroup mapped to an
 * existing group of fields. 
 * @param groupId The group set id.
 * @return a new or existing FieldGroup object.
 * @type FieldGroup
 */
FieldGroup.getFieldGroup = function( groupId) 
{
	// Create and Id which maps to all instances of a group.
	// This way we always get to the object of a group.
//	var indexId = "";
//	var groupArr = groupId.split("_");
//	var firstPart = groupArr[1].split(".");
//	//
//	if( groupArr[0] == "M")
//	{
//		indexId = groupArr[0] + "_" + firstPart[0];
//	}
//	else
//	{
//		indexId = groupArr[0] + "_" + firstPart[0] + "." + firstPart[1];
//	}
//	// Try to get an existing instance.
//	var aFieldGroup = FieldGroup.objectList[indexId];
//	// Is it existing?
//	if( aFieldGroup == null)
//	{
		aFieldGroup = new FieldGroup( groupId);
//		FieldGroup.objectList[indexId] = aFieldGroup;
//	}
	
	return aFieldGroup;
};

/**
 * This does the same as getNewId but does it for id's that
 * are split by comma delimiters. Multiple ids. 
 * @param groupId The group set id.
 * @param incrementBy An incremental or decremental value.
 * @return An updated string.
 * @type String
 */
FieldGroup.getNewIdMulti = function( groupId, incrementBy) 
{
	var tokenArray = groupId.split(",");
	var returnStr = "";
	// Do we have multiple id's split by a ','. If so update them all.
	if( tokenArray.length > 0)
	{
		returnStr = FieldGroup.getNewId( tokenArray[0], incrementBy);
		for( var i = 1; i < tokenArray.length; i++)
		{
			returnStr += "," + FieldGroup.getNewId( tokenArray[i], incrementBy);
		}	
	}
	else // Otherwise update just the one.
	{
		returnStr = FieldGroup.getNewId( groupId, incrementBy);
	}
	
	return returnStr;
};
	
/**
 * Updates the multivalue or subvalue string by the 
 * given paramter value. I.e. M_14.2_34.2 becomes M_14.3_34.3 when incrmentBy is 1.
 * or S_14.2.4_34.2.4 becomes S_14.2.3_34.2.3 when incrmentBy is -1.
 * @param groupId The group set id.
 * @param incrementBy An incremental or decremental value.
 * @return An updated string.
 * @type String
 */
FieldGroup.getNewId = function( groupId, incrementBy) 
{		
	var groupArr = groupId.split("_");
	
	if( groupArr.length != 3 )
    {
    	return groupId; // Return it back as we cant update it.
    }
		
	var startGroupArr = groupArr[1].split(".");
	var endGroupArr = groupArr[2].split(".");
	var startGrpStr = "";
	var endGrpStr = "";

	if( groupArr[0] == "M" )  // Is it a multivalue.
	{
		// Math operation
		startGrpStr = ( startGroupArr[1]*1 ) + incrementBy;
		endGrpStr = ( endGroupArr[1]*1 ) + incrementBy;
		// String manipulation.
		startGrpStr = startGroupArr[0] + "." + startGrpStr.toString();
		endGrpStr = endGroupArr[0] + "." + endGrpStr.toString();
	}
	else // Its a subvalue.
	{
		// Math operation
		startGrpStr = ( startGroupArr[2]*1 ) + incrementBy;
		endGrpStr = ( endGroupArr[2]*1 ) + incrementBy;
		// String manipulation.
		startGrpStr = startGroupArr[0] + "." + startGroupArr[1] + "." + startGrpStr.toString();
		endGrpStr = endGroupArr[0] + "." + endGroupArr[1] + "." + endGrpStr.toString();
	}
	
	return groupArr[0] + "_" + startGrpStr + "_" + endGrpStr;
};

/**
 * This does the same as getNewSubValueId but does it for id's that
 * are split by comma delimiters. Multiple ids. 
 * @param groupId The group set id.
 * @param incrementBy An incremental or decremental value.
 * @return An updated string.
 * @type String
 */
FieldGroup.getNewSubValueIdMulti = function( subValueId, incrementBy) 
{
	var tokenArray = subValueId.split(",");
	var returnStr = "";
	// Do we have multiple id's split by a ','. If so update them all.
	if( tokenArray.length > 0)
	{
		returnStr = FieldGroup.getNewSubValueId( tokenArray[0], incrementBy);
		for( var i = 1; i < tokenArray.length; i++)
		{
			returnStr += "," + FieldGroup.getNewSubValueId( tokenArray[i], incrementBy);
		}	
	}
	else // Otherwise update just the one.
	{
		returnStr = FieldGroup.getNewSubValueId( subValueId, incrementBy);
	}
	
	return returnStr;
};
/**
 * Updates the subvalue string by the 
 * given paramter value. I.e. S_14.2.4_34.2.4 becomes S_14.3.4_34.3.4 when incrmentBy is 1.
 * Notice how the middle part of the string is updated. This is needed for when we expand a 
 * multivalue that has subvalues. We need to update the subvalue ids
 * @param subValueId The subvalue set id.
 * @param incrementBy An incremental or decremental value.
 * @return An updated string.
 * @type String
 */
FieldGroup.getNewSubValueId = function( subValueId, incrementBy) 
{
	var idArr = subValueId.split("_");
	
	if( idArr.length == 1 )
    {
    	return subValueId; // Return it back as we cant update it.
    }
    
	var startIdArr = idArr[1].split(".");
	var endIdArr = idArr[2].split(".");
	// Validate the input. All these arrays should have a length of 3.
	if( idArr.length != 3 || startIdArr.length != 3 || endIdArr.length != 3)
	{
		return "Invalid Id passed @FieldGroup.getNewSubValueId: " + subValueId;
	}
	// Update the ids
	var startIdStr =  ( startIdArr[1]*1 ) + incrementBy;
	startIdStr = startIdArr[0] + "." + startIdStr.toString() + "." + startIdArr[2];
	var endIdStr =  ( endIdArr[1]*1 ) + incrementBy;
	endIdStr = endIdArr[0] + "." + endIdStr.toString() + "." + endIdArr[2];
	// Build up the new Id
	return idArr[0] + "_" + startIdStr + "_" + endIdStr;
};


/**
 * Updates the label of a multivalue or subvalue. I.e
 * Comment.1 becomes Comment.2 if incrementBy is 1 or
 * Comment.1.3 becomes Comment.1.2 if incrementBy is -1.
 * @param promtText The group set id.
 * @param incrementBy An incremental or decremental value.
 * @return An upated string.
 * @type String
 */
FieldGroup.updateLanguageText = function( promtText, incrementBy) 
{
	// Get the array of language codes.
	
	var tokenArray = promtText.split(" ");

	if( tokenArray.length > 1)
	{
		var langCode = tokenArray[0];
		
		if( incrementBy < 0)
		{
			// We are deleting a language field so get the previous lang code.
			for( var i = FieldGroup.languageCodeList.length; i > 0 ; i--)
			{
				if( langCode == FieldGroup.languageCodeList[i])
				{
					// If this code if equal to the langugage code passed then get the next one.
					langCode = FieldGroup.languageCodeList[ i-1];
					promtText = langCode;
					for( var j = 1; j < tokenArray.length; j++ )
					{
						promtText += " " + tokenArray[j];	
					}
					 
					break;
				}	
			}			
		}
		else
		{
			// We are deleting a language field so get the next lang code.
			for( var i = 0; i < ( FieldGroup.languageCodeList.length - 1 ) ; i++)
			{
				if( langCode == FieldGroup.languageCodeList[i])
				{
					// If this code if equal to the langugage code passed then get the next one.
					langCode = FieldGroup.languageCodeList[ i+1];
					promtText = langCode;
					for( var j = 1; j < tokenArray.length; j++ )
					{
						promtText += " " + tokenArray[j];	
					}
					break;
				}	
			}	
		}
		
	}
	
	return promtText;
};


/**
 * Updates the label of a multivalue or subvalue. I.e
 * Comment.1 becomes Comment.2 if incrementBy is 1 or
 * Comment.1.3 becomes Comment.1.2 if incrementBy is -1.
 * @param promtText The group set id.
 * @param incrementBy An incremental or decremental value.
 * @return An upated string.
 * @type String
 */
FieldGroup.updatePrompText = function(promtText, incrementBy) 
{
	// Mask the text 
	promtText = FieldGroup.maskText( promtText);

	var tokenArray = promtText.split(".");

	switch( tokenArray.length )
	{
		case 3:
			// It's a subvalue
			// Are we doing a multivalue expand? If so update the middle part of the subvalue.
			if( FieldGroup.isMultiValue )
			{
				tokenArray[1] = ( tokenArray[1]*1 )+ incrementBy;
				promtText = tokenArray[0].toString() + "." + tokenArray[1].toString() + "." + tokenArray[2].toString();
			}
			else // Update the end part of the subvalue since we are doing a subvalue expand.
		    {
				tokenArray[2] = ( tokenArray[2]*1 )+ incrementBy;
				promtText = tokenArray[0].toString() + "." + tokenArray[1].toString() + "." + tokenArray[2].toString();
		    }
			break;
		case 2:
		    // It's a multivalue
			tokenArray[1] = ( tokenArray[1]*1 )+ incrementBy;
			promtText = tokenArray[0].toString() + "." + tokenArray[1].toString();
			break;
		default: break;	
	}		

	// Un mask the text after the update.
	promtText = promtText.replace( /__/g, ".");
	
	return promtText;
};

/**
 * This method masks the the given text. Replaces the given delimiter with a special character. 
 * Used for subvalue and multivalue renaming. I.e. MY.FIELD.NAME.1.2 becomes MY\FIELD\NAME.1.2
 * @param text The given text to mask.
 * @return An upated string.
 * @type String
 */
FieldGroup.maskText = function( text ) 
{
	var isMultiValue = true;
	var tokenArr = text.split( ".");

	// Figure out if we are masking a subvalue or a multivalue.
	if( tokenArr.length > 1)
	{
		if( !isNaN( tokenArr[ (tokenArr.length - 1)]))
		{
			isMultiValue = true; // Its a multivalue
		}
		
		if( !isNaN( tokenArr[ (tokenArr.length - 2)]))
		{
			isMultiValue = false; // Its a subvalue
		}
	}
	else
	{
		return;// Don't mask anything if we are not a multivalue nor subvalue.
	}
	
	var lastDotIndex = text.lastIndexOf( ".");
	var newFieldName = text.slice( 0, lastDotIndex);
	var originalFieldName = newFieldName;
	
	if( isMultiValue)
	{
		newFieldName = newFieldName.replace( /\./g, "__");	
	}
	else
	{
		lastDotIndex = newFieldName.lastIndexOf( ".");
		newFieldName = newFieldName.slice( 0, lastDotIndex);
		originalFieldName = newFieldName;
		newFieldName = newFieldName.replace( /\./g, "__");
	}
	
	return text.replace( originalFieldName, newFieldName);
	
};
/**
 * @param fieldName The name of the field we want to update.
 * @param incrementBy An incremental or decremental value.
 * @return An upated string.
 * @type String
 */
FieldGroup.updateEnrichmentNameText = function( aElement, incrementBy) 
{
	// Needs to be returned when object is null or undefined otherwise script error as result.
	if (aElement==null || aElement == "undefined")
	{
		return;
	}
	
	var enriName = aElement.getAttribute( "id");
	if( enriName != null && enriName.indexOf("enri_") > -1 )
	{
		var tokenArray = enriName.split(":");
	
		switch( tokenArray.length )
		{
			case 3: // It's a subvalue
				// Are we doing a multivalue expand? If so update the middle part of the subvalue.
				if( FieldGroup.isMultiValue )
				{
					tokenArray[1] = ( tokenArray[1]*1 )+ incrementBy;
					enriName = tokenArray[0].toString() + ":" + tokenArray[1].toString() + ":" + tokenArray[2].toString();
				}
				else // Update the end part of the subvalue since we are doing a subvalue expand.
				{
					tokenArray[2] = ( tokenArray[2]*1 )+ incrementBy;
					enriName = tokenArray[0].toString() + ":" + tokenArray[1].toString() + ":" + tokenArray[2].toString();
				}
				
				// Update the element attributes with the new field name.		
				aElement.setAttribute( "id", enriName);
				aElement.setAttribute( "name", enriName);
				break;
			case 2: // Its a multivalue
				tokenArray[1] = ( tokenArray[1]*1 )+ incrementBy;
				enriName = tokenArray[0].toString() + ":" + tokenArray[1].toString();
				
				// Update the element attributes with the new field name.		
				aElement.setAttribute( "id", enriName);
				aElement.setAttribute( "name", enriName);
				break;
			default: break;
		}
	}
	
	return aElement;
};

/**
 * Update the fieldName's. I.e. fieldName:SEC.TRADE:1:2 becomes fieldName:SEC.TRADE:1:3
 * @param aElement An html element.
 * @param attribute The attribute we want to change in it.
 * @param incrementBy An incremental or decremental value.
 * @return An upated string.
 * @type String
 */
FieldGroup.updateFieldNameText = function( aElement, attribute, incrementBy) 
{	
	// Needs to be returned when object is null or undefined otherwise script error as result.
	if (aElement==null || aElement == "undefined")
	{
		return;
	}
	
	var fieldName = aElement.getAttribute( attribute);
	var fieldPrefix = "";
	var hiddenField = aElement.getAttribute("isHidden");
	
    if (fieldName != null && hiddenField == "YES") 
    {
         return aElement;
    }
	var enrichscript = "";      //variable identifies whether script to clear enrichment is going to be updated with appropriate fieldname or not 

    if (fieldName != null && fieldName.indexOf("javascript:clearField") > -1 )                  
    {
       var fieldName = FieldGroup.splitAt( fieldName, "'", 1);            
       var enrichscript = 1;
    }

	
	if( fieldName != null && fieldName.indexOf( "CheckBox:") == 0)
	{
		fieldName = fieldName.replace("CheckBox:", "");
		fieldPrefix = "CheckBox:";
	}
	
	if( fieldName != null && fieldName.indexOf( "ToggleButton:") == 0)
	{
		fieldName = fieldName.replace("ToggleButton:", "");
		fieldPrefix = "ToggleButton:";
	}
	
	// Check for no-input field values and the corresponding updation of values
	if( (fieldName != null && fieldName.indexOf("fieldName:") == 0 ) || (fieldName != null && fieldName.indexOf("disabled_") == 0 ))
	{   
	    // if span contains disabled as part of its elements. 
		if(fieldName.indexOf("disabled_") == 0 )
		{
			var tokenArray=fieldName.split("_");
		}
		else
		{
			var tokenArray = fieldName.split(":");
		}
				
		switch( tokenArray.length )
		{
			case 4:
				// It's a subvalue
				// Are we doing a multivalue expand? If so update the middle part of the subvalue.
				if( FieldGroup.isMultiValue )
				{
					tokenArray[2] = ( tokenArray[2]*1 )+ incrementBy;
					fieldName = tokenArray[0].toString() + ":" + tokenArray[1].toString() + ":" + tokenArray[2].toString() + ":" + tokenArray[3].toString();
				}
				else // Update the end part of the subvalue since we are doing a subvalue expand.
			    {
			    	tokenArray[3] = ( tokenArray[3]*1 )+ incrementBy;
					fieldName = tokenArray[0].toString() + ":" + tokenArray[1].toString() + ":" + tokenArray[2].toString() + ":" + tokenArray[3].toString();
				}
			    
			    // Add a prefix if there is one.
			    fieldName = fieldPrefix + fieldName;
			   	// Update the element attributes with the new field name.
				if (enrichscript)                          
                {

                    fieldName = "javascript:clearField(" + "'"+ fieldName + "'" + ")";  //update fieldname args 
                    aElement.setAttribute( attribute, fieldName);                                   
                }
                else
                {
                   aElement.setAttribute( attribute, fieldName); 
                }

				// Special case if we are IE and attribute is 'name' then IE doesn't do what it says in the tin!
			    // IE does not update this attribute (and some others) properly if the element in question has been created via 
			    // DOM's 'createElement' method. So hence the below:
			    if( attribute == "name" && isInternetExplorer() )
			    {
			    	aElement = FieldGroup.updateOutHtml( aElement, fieldName);
			    }
				break;
			case 3:
			    if (!FieldGroup.isMultiValue)//Dont increment the multivalue criteria field if its a sub value expansion.
			    {
			       return aElement ;
			    }
			    // It's a multivalue
				tokenArray[2] = ( tokenArray[2]*1 )+ incrementBy;
				fieldName = tokenArray[0].toString() + ":" + tokenArray[1].toString() + ":" + tokenArray[2].toString();
				
			    // Add a prefix if there is one.
			    fieldName = fieldPrefix + fieldName;		
				// Update the element attributes with the new field name.
				if (enrichscript)
                {
                    fieldName = "javascript:clearField(" + "'"+ fieldName + "'" + ")"; // update fieldname args 
                    aElement.setAttribute( attribute, fieldName);
                }
                else
                {
                   aElement.setAttribute( attribute, fieldName);
                }
			
				// Special case if we are IE and attribute is 'name' then IE doesn't do what it says in the tin!
			    // IE does not update this attribute (and some others) properly if the element in question has been created via 
			    // DOM's 'createElement' method. So hence the below:
				if( attribute == "name"  && isInternetExplorer() )
			    {
					aElement = FieldGroup.updateOutHtml( aElement, fieldName);
			    }
				break;
				
			case 2:
				tokenArray = fieldName.split(":");
				tokenArray[2] = ( tokenArray[2]*1 )+ incrementBy;
				fieldName = tokenArray[0].toString() + ":" + tokenArray[1].toString();
				if(!isNaN(Number(tokenArray[2])))
				{
				fieldName += ":" + tokenArray[2].toString();
				}
				
				aElement.setAttribute( attribute, fieldName);
				break;
				
				default: break;	
		}
	}
	
	return aElement;
};

/**
 * This routine is a nody way of getting passed IE's 'createElement' problem.
 * 'createElement' does not create a dynamic element properly and when using 'setAttribute'
 * to set the 'name' attribute, the 'outerHtml' of the dynamic element does not get updated.
 * This routine will update the outerHtml whether IE likes it or not!
 * @param attribute The attribute to be updated.
 * @param aElement Is the html element we want to change the outerHtml attribute.
 * @param newNameStr Is the new string we want to equate 'name' to.
 * @return An element which has the outerHTML attribute updated accordingly.
 * @type void
 */
FieldGroup.updateOutHtml = function( aElement, value) 
{
	var outerHtml = aElement.getAttribute( "outerHTML");
	// Needs to be returned when object is null or undefined otherwise script error as result.
	if (outerHtml==null)
	{
		return;
	}
	value = "name=" + value;
	outerHtml = outerHtml.replace(/name=\S+/, value);
	aElement.setAttribute( "outerHTML", outerHtml);
	return aElement;
};
/**
 * Clones the given element and all of it children
 * using recursion.
 * @param e Is the html element we want to clone.
 * @return An element which is deep clone of the passed argument. 
 * @type Array
 */
FieldGroup.cloneNodeDeeply = function(e) 
{
	var out = FieldGroup.cloneNodeShallowly(e);
	var childList = e.childNodes;
	
	for ( var i = 0; i < childList.length; i++ ) 
	{
		out.appendChild( FieldGroup.cloneNodeDeeply( childList[i] ) );
	}
	
	return out;
};

/**
 * Clones the given element and not its children.
 * @param e Is the html element we want to clone.
 * @return An element which is a shallow clone of the passed argument. 
 * @type Array
 */
FieldGroup.cloneNodeShallowly = function( aElement) 
{
	switch (aElement.nodeType) 
	{
		case 1: return FieldGroup.cloneElement( aElement); break;
		case 3: return document.createTextNode( aElement.nodeValue ); break;
		default: alert("unhandled nodeType detected!"); return null; break;
	}
};

/**
 * This method actually performs the cloning using DOM methods.
 * Also copies the events for IE as there is a bug with IE that misses out the events during the clone.
 * @param e Is the html element we want to clone.
 * @return An element which is a shallow clone of the passed argument. 
 * @type Array
 */
FieldGroup.cloneElement = function(e) 
{
	var out = null;
	out = e.cloneNode(false);

	if (!isFirefox()) 
	{
		var attributeList = e.attributes;
  		for ( var i = 0; i < attributeList.length; i++ ) 
  		{
  			if( attributeList[i].nodeType == 1 )
  			{
	  			var attrName = attributeList[i].getAttribute( "name");
	  			var attrValue = attributeList[i].getAttribute( "value");
	    		if ( attrName.substring(0,2).toLowerCase() == "on" & attrValue != "null") 
	     		{
	     			var command = "";
	     			var listOfEvents = FieldGroup.getListOfEvents(attrValue);
	     			//
	     			for(var j = 0; j < listOfEvents.length; j++)
	     			{
	     				command = "out.attachEvent( '" + attrName + "' , " + listOfEvents[j] + " );";
		        		eval(command);	
	     			}  			 			
	        	}
  			}
    	}	
	}
	
    
	return out;
};

/**
 * Extracts the list of events from the given value.
 * @param attrValue Holds a delimited list event signitures. Comma delimited.
 * @return An array of events that only have the name of the function and not the full event signiture.
 * @type Array
 */
FieldGroup.getListOfEvents = function(attrValue)
{
 	//Get the list of events split by ';'
 	var currEventSigniture = "";
 	var eventArray = attrValue.split(";");
 	var returnArray = new Array();
 	//
 	for(var i = 0; i < eventArray.length; i++)
 	{
 		
 		currEventSigniture = eventArray[i].replace("javascript:", "");
		currEventSigniture = currEventSigniture.split("(");
		// Only add if not empty string.
		if( currEventSigniture[0] != "" )
		{
			// Only interested in the first token. The second token not wanted.
			returnArray[returnArray.length] = currEventSigniture[0];	
		}
 	}
 	
 	//Return a list of function names i.e "myFunc(someParam);" turns into "myFunc"
 	return returnArray;
};

/**
 * Tries to rename the given element and all of its children and grandchildren and so on.
 * @param aElement An html element that we want to try and rename.
 * @return The modified element.
 * @type Object
 */
FieldGroup.renameElementDeeply = function(aElement, instanceId, incrementBy)
{
	// Rename the top element.
	FieldGroup.renameElement( aElement, instanceId, incrementBy);
    // Rename its children
	var childList = aElement.childNodes;
	for( var i = 0; i < childList.length; i++ ) 
	{
		FieldGroup.renameElementDeeply( childList[i], instanceId, incrementBy);
	}
	return aElement;
};
/**
 * Interrogate the elements properties and try to modify if necessary.
 * @param aElement An html element that we want to try and rename.
 * @return The modified element.
 * @type Object
 */
FieldGroup.renameElement = function( aElement, instanceId, incrementBy)
{
	
	switch (aElement.nodeType) 
	{
		case 1:
			// Deal with href's  
			aElement = FieldGroup.updateHref( aElement, incrementBy);
			// Deal with group id's i.e M_4.3_6.3 or S_3.4.2_3.6.2
			aElement = FieldGroup.updateGroupId( aElement, incrementBy);
			// Deal with the name and id of this field i.e. fieldName:SEC.TRADE:1:2
			aElement = FieldGroup.updateFieldNameText( aElement, "name", incrementBy);
			aElement = FieldGroup.updateFieldNameText( aElement, "id", incrementBy);
			// Update with the enricment fieldname 
			aElement = FieldGroup.updateFieldNameText( aElement, "mvTextField", incrementBy);
			// Update the calender drop field name.
			aElement = FieldGroup.updateFieldNameText( aElement, "calendardropfieldname", incrementBy);
			// Update recurrance field name
			aElement = FieldGroup.updateFieldNameText( aElement, "recurrencedropfieldname", incrementBy);
			// Update the frequesncy field name
			aElement = FieldGroup.updateFieldNameText( aElement, "frequencydropfieldname", incrementBy);
			// Update relativecalendar field name
 			aElement = FieldGroup.updateFieldNameText( aElement, "reldropfieldname", incrementBy);
			// Update the checkbox 
			aElement = FieldGroup.updateFieldNameText( aElement, "toggleFieldName", incrementBy);
			// Update the togglebutton 
			aElement = FieldGroup.updateFieldNameText( aElement, "buttonNameFieldName", incrementBy);
			// Update the dropfield
			aElement = FieldGroup.updateFieldNameText( aElement, "dropField", incrementBy);
			// Update the criteria field for multivalue as well as subvalue	
			aElement = FieldGroup.updateFieldNameText( aElement, "criteriaDataField", incrementBy);
			aElement = FieldGroup.updateFieldNameText( aElement, "href", incrementBy);               //Update field names with appropriate MV and SV values
         	// Update the enrichment fields
			aElement = FieldGroup.updateEnrichmentNameText( aElement, incrementBy);

			break;
		case 3:  
			var parentNode = aElement.parentNode;
			var grandParentNode = parentNode.parentNode;
			if(grandParentNode != null)
			{
               grandParentNode = grandParentNode.parentNode;
			}
			var parentId = parentNode.getAttribute( "id");		
			// Try and figure out what to do with this text node based on the parent.
			if( parentId != null && parentId.indexOf( "fieldCaption:") > -1 )
			{
				var isLanguage = parentNode.getAttribute("lng");
				if( isLanguage == "y" )
				{
					// If its a multivalue then only update elements that don;t have a svdetails attribute
					if( FieldGroup.isMultiValue )
					{
						var isSubValuePromt = grandParentNode.getAttribute( "svDetails");	
						if( isSubValuePromt == null)
						{
							// Deal with language prompts
							aElement.nodeValue = FieldGroup.updateLanguageText( aElement.nodeValue, incrementBy);	
						}
						else
						{
							// Deal with the prompts
							aElement.nodeValue = FieldGroup.updatePrompText( aElement.nodeValue, incrementBy);			
						}
					}
					else // Its a subvalue so update it.
					{
						// Deal with language prompts
						aElement.nodeValue = FieldGroup.updateLanguageText( aElement.nodeValue, incrementBy);	
					}
				}
				else
				{
					// Deal with the prompts
					aElement.nodeValue = FieldGroup.updatePrompText( aElement.nodeValue, incrementBy);		
				}
				
			}
			
			break;
		default: break;
	}

	return aElement;
};

/**
 * This updates any elements which have href's that need updating.
 * @param aElement An html element that we want to try and rename.
 * @return The modified element.
 * @type Object
 */
FieldGroup.updateHref = function( aElement, incrementBy)
{
	var hrefStr = aElement.getAttribute("href");
	// If this element does not have a href attribute then just return.
	if( hrefStr == null )
	{
		return aElement;
	}
	
    // Get the instance id from the href.		
	var instanceId = FieldGroup.splitAt( hrefStr, "'", 1);
	// Is this the right href i.e. does it have an id for us to update. If not return.
	if( instanceId == "")
	{
		return aElement;
	}
	
	// Are we a multivalue expansion?
	if( FieldGroup.isMultiValue )
	{
		if( hrefStr.indexOf("javascript:mvExpandClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}
			
		if( hrefStr.indexOf("javascript:mvDeleteClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}
		
		if( hrefStr.indexOf("javascript:svExpandClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewSubValueId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}
			
		if( hrefStr.indexOf("javascript:svDeleteClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewSubValueId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}
	}
	else // We are a subvalue expansion.
	{
		if( hrefStr.indexOf("javascript:svExpandClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}
			
		if( hrefStr.indexOf("javascript:svDeleteClient(") > -1 )
		{
			hrefStr = hrefStr.replace( instanceId, FieldGroup.getNewId( instanceId, incrementBy));
			aElement.setAttribute("href", hrefStr);
		}	
	}
	
	
	
	
	return aElement;	
};

/**
 * This updates any elements which have id's that need updating.
 * @param aElement An html element that we want to try and rename.
 * @param incrementBy How much to increment the id by: -1 , 2, 5.
 * @return The modified element.
 * @type Object
 */
FieldGroup.updateGroupId = function( aElement, incrementBy)
{
	var mvDetails = aElement.getAttribute("mvDetails");
	var mvList = aElement.getAttribute("mvList");
	var svDetails = aElement.getAttribute("svDetails");
	var svList = aElement.getAttribute("svList");
	
	// Only wanna update these id's if we are expanding a multivalue.
	if( FieldGroup.isMultiValue )
	{
		// Deal with mvDetails per cell
		if( mvDetails != null )
		{
			aElement.setAttribute( "mvDetails", FieldGroup.getNewId( mvDetails, incrementBy));
		}
		// Deal with mvList per row
		if( mvList != null )
		{
			aElement.setAttribute( "mvList", FieldGroup.getNewIdMulti( mvList, incrementBy));
		}
		
		// Deal with svDetails per cell
		if( svDetails != null)
		{
			aElement.setAttribute( "svDetails", FieldGroup.getNewSubValueId( svDetails, incrementBy));
		}
		
		// Deal with svList per row
		if( svList != null)
		{
			aElement.setAttribute( "svList", FieldGroup.getNewSubValueIdMulti( svList, incrementBy));
		}
		
	}
	else
	{
		// Deal with svDetails per cell
		if( svDetails != null)
		{
			aElement.setAttribute( "svDetails", FieldGroup.getNewId( svDetails, incrementBy));
		}
		
		// Deal with svList per row
		if( svList != null)
		{
			
			// Don't update all the ids separated by commas, instead get rid of all the id's possible and
			// only add a single id for this row.
			aElement.setAttribute( "svList", FieldGroup.getNewId( FieldGroup.currentInstanceId, incrementBy));
		}		
	}
		
	return aElement;
};

/**
 * This updates any elements which have id's that need updating.
 * @param str String to split
 * @param index Which token to return
 * @return The desired token
 * @type String
 */
FieldGroup.splitAt = function( str, delimiter, index)
{
	var strArr = str.split( delimiter);
	
	if( index < strArr.length)
	{
		return strArr[index];
	}
	else
	{
		return "Invalid index @FieldGroup.splitAt";
	}
};

/**
 * Set given attribute name with the given attribute value in the given element.
 * @param aElement Html element.
 * @param attrName Attribute name.
 * @param attrValue Attribute value.
 * @return The given element.
 * @type Object.
 */
FieldGroup.replaceAttribute = function( aElement, attrName, fromStr, toStr)
{
	// Just a temp var used to modify attributes.
	var tmpVar = aElement.getAttribute( attrName);
	tmpVar = tmpVar.replace( fromStr , toStr);
	image.setAttribute( attrName, tmpVar);
	// Return the changed element.
	return aElement;
};

/**
 * Get the svDetails attribute that is associated with mvDetails
 * I.E. get the subvalue associated multivalue id.
 * @param aElement Html element.
 * @param attrName Attribute name.
 * @param attrValue Attribute value.
 * @return The given element.
 * @type Object.
 */
FieldGroup.getMvDetails = function( row, svDetailsId)
{
	var rowChildren = row.childNodes;
	var thisAttrValue = "";
	var aChild = "";
	
	for( var i = 0; i < rowChildren.length; i++)
	{
		aChild = rowChildren[i];
		// Only test nodetype 1 elements.
		if( aChild.nodeType == 1)
		{
			thisAttrValue = aChild.getAttribute( "svDetails");
			if( thisAttrValue == svDetailsId )
			{
				thisAttrValue = aChild.getAttribute( "mvDetails");
				// If we get a value then break.
				if( thisAttrValue != null )
				{
					break;
				}
			}
			thisAttrValue = "";
		}
	}
	
	return thisAttrValue;
};

/****** These functions are event handlers for expand and delete actions********/

/**
 *	This object holds the types of expansions availbale.
 */
var MvSvExpansionType = {
	AllServer : 1,
	Client : 2
};

/**
 *  This global var holds the current type of expansion used.
 *  Set this accordingly.
 */
var mvClientSideExpansion_GLOBAL = MvSvExpansionType.AllServer;

/**
 * Sets the expansion type based on what comes back from the server. If new server uses cleint 
 * side expansion otherwise uses server side expansion.
 */
function setMvSvExpansionType()
{
	// Use 'appreq' explicitly as opposed to currentForm_GLOBAL as we are only interested in the main app.
	var isClientExpansion = getFormFieldValue( "appreq" , "clientExpansion" );
	if( isClientExpansion == "true")
	{
		mvClientSideExpansion_GLOBAL = MvSvExpansionType.Client;
	}
}

/**
 * Subvalue expand
 * @param instanceId The id of the instance we want to expand.
 */
function svExpandClient( instanceId)
{
	// Do expansion according to what is set in this variable
	switch( mvClientSideExpansion_GLOBAL )
	{
		case MvSvExpansionType.AllServer:
		
		    doMultiValue( 'OFS.OS.EXPAND.VALUE', instanceId);
			break;
		case MvSvExpansionType.Client:
		
			var myObj = FieldGroup.getFieldGroup( instanceId);
			myObj.expand( instanceId);
			break;
		default: 
			alert("Wrong expansion type has been set @mvExpandClient: " + mvClientSideExpansion_GLOBAL);
			break;	
	}
}

/**
 * Subvalue collapse
 * @param instanceId The id of the instance we want to expand.
 */
function svDeleteClient( instanceId)
{
	// Do expansion according to what is set in this variable
	switch( mvClientSideExpansion_GLOBAL )
	{
		case MvSvExpansionType.AllServer:
		
		    doMultiValue( 'OFS.OS.DELETE.VALUE', instanceId);
			break;
		case MvSvExpansionType.Client:
		
			var myObj = FieldGroup.getFieldGroup( instanceId);
			myObj.collapse( instanceId);
			break;		
		default:
		 	alert("Wrong expansion type has been set @mvExpandClient: " + mvClientSideExpansion_GLOBAL);
		 	break;
	}	
}

/**
 * Multivalue expand
 * @param instanceId The id of the instance we want to expand.
 */
function mvExpandClient( instanceId) 
{	
	// Go back to the server if the passed id is not a multi value id
	// This is the case for local ref fields. For now we always go back to the server for lrfs.
	if( !isMultiValueId( instanceId) )
	{
		doMultiValue( 'OFS.OS.EXPAND.VALUE', instanceId);
		return;
	}
	// Do expansion according to what is set in this variable
	switch( mvClientSideExpansion_GLOBAL )
	{
		case MvSvExpansionType.AllServer:
		
			doMultiValue( 'OFS.OS.EXPAND.VALUE', instanceId);
			break;
		case MvSvExpansionType.Client:
		
			var myObj = FieldGroup.getFieldGroup( instanceId);
			myObj.expand( instanceId);			
			break;
		default: 
			alert("Wrong expansion type has been set @mvExpandClient: " + mvClientSideExpansion_GLOBAL);
			break;
	}
}

/**
 * Multivalue delete
 * @param instanceId The id of the instance we want to expand.
 */
function mvDeleteClient( instanceId) 
{
	// Go back to the server if the passed id is not a multi value id
	// This is the case for local ref fields. For now we always go back to the server for lrfs.
	if( !isMultiValueId( instanceId) )
	{
		doMultiValue('OFS.OS.DELETE.VALUE', instanceId);
		return;
	}
	// Do expansion according to what is set in this variable	
	switch( mvClientSideExpansion_GLOBAL )
	{
		case MvSvExpansionType.AllServer:
		
			doMultiValue('OFS.OS.DELETE.VALUE', instanceId);
			break;
		case MvSvExpansionType.Client:
					
			var myObj = FieldGroup.getFieldGroup( instanceId);
			myObj.collapse( instanceId);
			break;
		default: 
			alert("Wrong expansion type has been set @mvExpandClient: " + mvClientSideExpansion_GLOBAL);
			break;
	}
}

/**
 * Checks to see if the given id a multivalue id.
 */
function isMultiValueId( instanceId)
{
	var tokenArray = instanceId.split("_");
	if( tokenArray[0] == "M")
	{
		return true;
	}
	else
	{
		return false;
	}
}
