/** 
 * @fileoverview Deals with all of the javascript functions used to process tabbed screens
 *
 */

/**
 * Hides all the tab tables on a deal form  TODO: Change func name - this shows the active tab too, and returns its ID!! 
 * @param {String} activeTabName
 * @return activeTableNumber
 */

var currentFragment_GLOBAL = ''; //for use in the function unlockNoFramesDeal
// Declaring valid date character, minimum year and maximum year
var minYear=1000;
var maxYear=2100;
var dtStr1 = "";		//formated date input.
var newDate = "";	//global variable to hold the input date.
var datesList = "";	//list of dates sending to server.
var remcount = 0;	//to count the removed error tabs.
var newOneIndx = 0;  // for sorting the global variable(appFormList_GLOBAL) to send requests in order.

function isInteger(s){
 var i;
    for (i = 0; i < s.length; i++){   
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}


function daysInFebruary (year){
 // February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}
function DaysArray(n) {
	var myArray=new Array();
	for (var i = 1; i <= n; i++) {
		myArray[i] = 31;
 		if (i==4 || i==6 || i==9 || i==11) {myArray[i] = 30;}
 		if (i==2) {myArray[i] = 29;}
 	}
 	return myArray;
}



function isDate(dtStr){
dtStr=dtStr.toUpperCase();
if (dtStr.charAt(0)=="D")
 	{
  		dtStr=dtStr.split("_")[1];
 	}
if (dtStr.charAt(0)=="R")
 	{
  		dtStr1 = dtStr;
  		return true;
 	}
 if (isInteger(dtStr)==false)
 {	
 	
	//check for dates of the form 06 may 2000 else send an alert for the date format
        var strDate=dtStr.split(" ");
        if (strDate.length != 3)
		{
	      return false;
		}         
        if(isInteger(strDate[0])==true && isInteger(strDate[2])==true)
        {
        	var strDay =strDate[0];
			var strYear  =strDate[2];
        }
        else
        {
        	return false;
        }
        var strMonth = isDate.months[strDate[1].toLowerCase()]; 
        if (strMonth==null || strMonth == undefined)
		{ 
              return false;  
        }
 }
 else
 { 
	if (dtStr.length>8)
	{
		// invalid date entered eg.(201001301234567)
		return false;
	} 
	var strMonth=dtStr.substring(4,6);
 	var strDay=dtStr.substring(6,8);
	var strYear=dtStr.substring(0,4);
 }
 var daysInMonth = DaysArray(12);
 strYr=strYear;
 if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1);
 if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1);
 for (var i = 1; i <= 3; i++) 
	{
		 if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1);
	}
 month=parseInt(strMonth);
 day=parseInt(strDay);
 year=parseInt(strYr);
 
 if (strMonth.length<1 || month<1 || month>12){
 //alert("Please enter a valid month");
 return false;
}
 if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
 //alert("Please enter a valid day");
 return false;
}
 if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
// alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear);
 return false;
}
if(strMonth.length == 1) {strMonth = "0"+strMonth;}
if(strDay.length == 1) {strDay = "0"+strDay;}
dtStr="D_"+strYear+strMonth+strDay; 
dtStr1 = dtStr;		//GLOBAL variable to hold converted data after checking whether its date or not.
return true;
}

isDate.months = {jan: "1", feb: "2", mar: "3", apr: "4", may: "5", jun: "6",jul: "7", aug: "8", sep: "9", oct: "10", nov: "11", dec: "12"};

 // function to compare two dates and returns number of days differ.
function compareDates(date1 , date2 )
{
yr1 = date1.substring(0,4);
mon1 = date1.substring(4,6);
dt1 = date1.substring(6,8);

yr2 = date2.substring(0,4);
mon2 = date2.substring(4,6);
dt2 = date2.substring(6,8);

var d1 = new Date(yr1,mon1,dt1);
var d2 = new Date(yr2,mon2,dt2);
return Math.round((d2-d1)/(1000*24*60*60));
}

function hideTabs(activeTabName)
{
	var appreqForm = FragmentUtil.getForm(currentForm_GLOBAL);
	var tables = appreqForm.getElementsByTagName("table");
	var currentTabObj;
	activeTableNumber = 0;
	activeTableCount = 0;
	numberOfTables = tables.length;

	var tableCount = 0;
	// Go through each table....
	for (tableCount = 0; tableCount < numberOfTables; tableCount++)
	{
		tableName = tables[tableCount].getAttribute("name");
		tableId = tables[tableCount].getAttribute("id");

		// If it is a tab table then make it invisible ....
		if (tableName == "visibletab")
		{
			if (tableId == activeTabName)
			{
				FragmentUtil.setDisplay(tables[tableCount], true);
				activeTableNumber = activeTableCount;
			}
			else
			{
				// Save the tab that was visible in case we need to re-show it when we have a main tab
				if ( FragmentUtil.isVisible(tables[tableCount]) )
				{
					currentTabObj = tables[tableCount];
					currentTableNumber = activeTableCount;
				}

				FragmentUtil.setDisplay(tables[tableCount], false);
				activeTableCount += 1;
			}
		}
	}

	// If we are making the mainTab visible then the previous visible tab
	// should also be displayed at the bottom of the screen
	if ( activeTabName == "mainTab" )
	{
		if ( currentTabObj )
		{
			FragmentUtil.setDisplay(currentTabObj, true);
			activeTableNumber = currentTableNumber;
		}
	}

	return activeTableNumber;
}


/**
 * Change to the specified tab on a deal form
 * @param {String} tabname The name of the tab to change to
 * @return {void}
 */ 
function changetab(tabname)
{
	// Check whether a tabname has been supplied or not
	if ( tabname != "" )
	{
		// switch to the specified tab and get its number
		tabnumber = hideTabs(tabname);
		
		var tabHeader;
		
		var multiPaneFormObj = FragmentUtil.getForm("multiPane");
		if( multiPaneFormObj != null && currentForm_GLOBAL != "appreq")
		{
			// AA has more than one headtab, form name has been concated in contract and refered here to avoid the wrong reference of headtab
			tabHeader = FragmentUtil.getElement("headtab_" + currentForm_GLOBAL);
		}else if (tabHeader =="" || tabHeader ==null)
		{
			// there might be only 1 tab with no display name, so no headtab, and no tabs to switch
			tabHeader = FragmentUtil.getElement("headtab");
		}
		
		if (tabHeader) {
			var cells = tabHeader.rows[0].cells;

			//we only want to do this if there is actually one or more tabs
			if (cells.length)
			{
				for (var cellCount = 0; cellCount < cells.length; cellCount++)
				{
					cells[cellCount].firstChild.className = "nonactive-tab";
				}
	
				cells[tabnumber].firstChild.className = "active-tab";
				// take the focus off the tab href
				// this is not particularly friendly on IE as the focus goes to the top of the form
				cells[tabnumber].firstChild.blur();
	
				// Update the form field with the active tab name
				if( multiPaneFormObj != null )
				{
					var activeTab = getFormField( currentForm_GLOBAL, "activeTab");
					activeTab.value = tabname;
				} else
				{
					FragmentUtil.getElement("activeTab").value = tabname;
				}
				
			}
			var addGroupTab = getFormFieldValue(currentForm_GLOBAL,"addGroupTab");
				if(addGroupTab == '')
					{
						if(cells[tabnumber+1] != null && cells[tabnumber+1] != "undefined" && cells[tabnumber+1].firstChild != null)
	     					{
              					cells[tabnumber+1].firstChild.focus(); // s/e
	     					} else if (cells[0] != null && cells[0] != "undefined" && cells[0].firstChild != null)
	      						{
                					cells[0].firstChild.focus();
	      						}		
					}
				}
		// Get coordinates from form, if available..
		windowCoordinates = isWindowSizesPresent(); 
		if ( windowCoordinates != null )
		{
			windowTop = windowCoordinates.windowTop;	
			windowLeft = windowCoordinates.windowLeft;
			windowWidth = windowCoordinates.windowWidth;
			windowHeight = windowCoordinates.windowHeight;
			// init changes tab if any
			initEnquiry('', windowTop, windowLeft, windowWidth, windowHeight);
		}	
		
	}
}



/**
 * Load up a tabbed screen
 * @param {String} routine The name of the routine to run
 * @param {String} cmdname The command name
 * @param {String} tabId The name of the tab
 * @param {String} contentType The contentType of the tab
 * @param {String} formName The actual form
 * @param {String} frameName The name of the frame
 * @return {void}
 */ 
function gettabbedscreen(routine,cmdname, tabId,contentType, formName, frameName)
{
	
	// Ensure the current fragment is present if we really in a fragment of 'noframes' window.
	// This stops to refer the child's fragment, while actually we are in parent frameset.
	currentFragment_GLOBAL = Fragment.getCurrentFragmentName();
	if ( top.noFrames && currentFragment_GLOBAL )
	{
		// give me all the fragment objects. Necessary for when we have nested fragments for NoFrames e.g two tab screens...
		var fragmentObjects = FragmentUtil.getElementsByClassName(FragmentUtil.FRAGMENT_CONTAINER_CLASS, FragmentUtil.FRAGMENT_TAG);

		for (fragCount = 0; fragCount < fragmentObjects.length; fragCount++)
		{
			// first loop through and get the window name that corresponds to the fragment that the user has chosen.
			var fragName = fragmentObjects[fragCount].getAttribute("windowName");

			if (fragName.indexOf(currentFragment_GLOBAL) > -1)
			{
				var windowNameArray = fragName.split("_");
				
				for (windowNameCount = 0 ; windowNameCount < windowNameArray.length; windowNameCount++ )
					{
						if (currentFragment_GLOBAL == windowNameArray[windowNameCount])
						{
							//get the position before last to get the id of the window. This links to the workarea.
							var currentWindowName = windowNameArray[windowNameCount-1];
							break;
						}
					}
			}
		}
		
		//now loop again and get the target to place the fragment..
		currentWindowName = currentWindowName+"_"+"workarea";
		
		for (fragCount = 0; fragCount < fragmentObjects.length; fragCount++)
		{
			// using the window name, loop through and get the workarea.
			// this is so we can set the target properly
			fragName = fragmentObjects[fragCount].getAttribute("windowName");

			if(fragName.match(currentWindowName) != null)
			{
				var workareaFragArray = fragName.split("_");
				//get the last element in the array as this holds the target
				var workareaFragName = workareaFragArray[workareaFragArray.length-1];
			}
		}
		
		Fragment.setCurrentFragmentName(workareaFragName);
		tabNumber = tabId.substring(3);
		var workareaFragment = Fragment.getCurrentFragment();
	
	   	
		var genFormName = '';
		
		if(currentFragment_GLOBAL) {
		//Append the exact fragment name to identify current general form
		genFormName = "generalForm_"+currentFragment_GLOBAL;
		
		}
		var genForm = FragmentUtil.getForm(genFormName);
		
		genForm.requestType.value = "oh1y1haCb}oh16Y";
    	genForm.routineName.value = "}XCKb}eYXXCht8";
    	if(contentType == 'ENQ')
    	  {
    	    //add the contentType to the routineArgs for ARC
    	      genForm.routineArgs.value = cmdname+"_"+tabNumber+"_"+"_"+contentType+"_";
    	  }
    	  else
    	  {
		    genForm.routineArgs.value = cmdname+"_"+tabNumber+"_";
		  }
		
		// set window name so that if it is a contract, it can be unlocked later
		genForm.windowName.value = workareaFragName;

		updatedFormsField( "WS_parentComposite", genForm.windowName.value );
		
		//create this on the fly, so that we can associate with correct forms inside noFrames/composite screens
		//needed for show/hide columns
		var newElement = document.createElement("input");
		newElement.type = "hidden";
		newElement.name = "WS_FragmentName";
		newElement.value = genForm.windowName.value;
		genForm.appendChild(newElement);
		updatedFormsField( "WS_FragmentName", genForm.windowName.value );
		
		var params = FragmentUtil.getFormFieldsAsParams(genForm);
		workareaFragment.populateWithURL(genForm.action,params);
		
		//set the old current fragment back so that the active tab can be correctly highlighted
		Fragment.setCurrentFragmentName(currentFragment_GLOBAL);
		
	}
	else
	{
	try {
		windowName = window.parent.frames[1].name;
				
		if(!SaveWindowChanges(window.parent.frames[1]))
		{
			return;
		} 
	}
	catch (exception) {
     	FragmentUtil._logger.error("EXTERNAL_URL", exception.message);
	}

	workareaFrame = window.parent.frames[1];
	if(windowName==null || windowName=="" )
	{
		windowName = workareaFrame;
	}
	
		//we just want the actual number from the tab id
		tabNumber = tabId.substring(3);
	
		//get the stored transaction id from the main frmae
		workareaTransId = "";
		
		companyId = document.getElementById("companyId").value;
		var contextRoot = getWebServerContextRoot();
		var user = getUser();
		var jspRequest="../jsps/genrequest.jsp?&routineName="+routine+"&skin="+getSkin()+"&compId="+companyId+"&compScreen="+getCompositeScreen()+"&contextRoot=/"+contextRoot+"&windowName="+windowName+"&user="+user;
		if (contentType == "ENQ") 
		{
		//add contentType for tabbed screen enquiry request.
		  jspRequest= jspRequest+"&routineArgs="+cmdname+"_"+tabNumber+"_"+workareaTransId+"_"+contentType;             
		}
		else
		{
		  jspRequest= jspRequest+"&routineArgs="+cmdname+"_"+tabNumber+"_"+workareaTransId;             
		}
		workareaFrame.location=jspRequest;
	}
	//if there are tabs to change, then change it to the tab id
		if (tabId!="")
		{
			changetabETS(tabId);
		}
}

/**
 * Change to the specified tab on an EB.TABBED.SCREEN
 * @param {String} tabname The name of the tab to switch to
 * @return {void}
 */ 
function changetabETS(tabname)
{
	
	//get the number of the selected tab
	tabnumber = tabname.substring(3)-1;
	//now show the selected tab
	var thistable = FragmentUtil.getElement("headtab");
	
	var cells = thistable.rows[0].cells;
	//we only want to do this if there is actually one or more tabs
	
	if (cells.length > 1)
	{
		var cellCount = 0;
		for (cellCount = 0; cellCount < cells.length; cellCount++)
		{
			cells[cellCount].childNodes[0].className = "nonactive-tab";
		}

		cells[tabnumber].childNodes[0].className = "active-tab";
		// take the focus off the tab href
		// this is not particularly friendly on IE as the focus goes to the top of the form
		
		cells[tabnumber].firstChild.blur();

		// Update needs to be done based on current frame or fragment. since same form can be present in multi frame/fragment.
		// It avoids problems like hiding or displaying wrong tabs/fragment tabs in noframes COS.
		if (top.noFrames)
		{
			updateFormFieldValueOnCurrentFragment("activeTab",tabname);
		}else{
			FragmentUtil.getElement("activeTab").value = tabname;
		}		
	}
}

/* Function to hide date selectiong control 
* @param propertyName
*/
function removeNewPropertyDateControl(propertyName)
{
	setFormFieldValue( "newPropertyDateForm_"+propertyName, "fieldName:ID.COMP.6","" );
	var newDateForm = document.getElementById("newPropertyDate_"+propertyName);
	newDateForm.style.display = "none";
	
}

/* Function to Enable control to fetch date needed for new Property
* @param currentTabnumber, propertyName, formName
*/
function showDateControl(currentTabnumber,propertyName,formName)
{
	var conditionForm = formName;	
	var newDateForm = document.getElementById("newPropertyDate_"+propertyName);
	newDateForm.style.display = "block"; //Set the Date control form to be visible to get date from user

	var newPropertyDateGoLink = document.getElementById("newPropertyDateGo_"+propertyName);
	newPropertyDateGoLink.setAttribute("href","javascript:addTabMenu("+currentTabnumber+",'"+propertyName+"','"+ conditionForm + "')"); //Set the script to trigger when date is selected and go button clicked.

}
/* Function to expand tab
* @param currentTabnumber, propertyName, formName
*/
function addTabMenu(currentTabnumber,propertyName,conditionForm)
{
	newDate = getFormFieldValue( "newPropertyDateForm_"+propertyName, "fieldName:ID.COMP.6" );
	if (newDate == "" )
	{
		alert("Please select a date before proceeding.");
		return;
	}
	var effectivedate = getFormFieldValue( "appreq" , "fieldName:EFFECTIVE.DATE");
		if(isDate(effectivedate) == true)
		{
		effectivedate=dtStr1;
		effectivedate = effectivedate.split("_");
		effectivedate = effectivedate[1];
		datesList = effectivedate + "|";	//initialise dates list sending to server
		}
	if (isDate(newDate) == false )
	{
		alert("Please enter a valid date of the format YYYYMMDD or DD MMM YYYY");
		return;
	}
	newDate = dtStr1; //formated date for both R_ and D_ values.
	
	setCurrentForm(conditionForm);
	
	var lastTabIndex = currentTabnumber - 1;
	var nextTabNo = currentTabnumber + 1;

	var currentTabId ='tabMenu' + currentTabnumber; //form current tab menu id
	var maxid = 0;	//initialise variable which is needed to create a new id which is always maximum from others.
	var tabMenuItem = document.getElementById("tabMenuDiv_"+propertyName);
	var  allTabMenuItems= tabMenuItem.getElementsByTagName("span");
	//pick the div after which the new tab menu div needs to be created ( to have insert tab effect on clicking a new tab icon)
	for (tabMenuIndex=0; tabMenuIndex < allTabMenuItems.length ;tabMenuIndex++)
	{
		var currentTabMenuItemId = allTabMenuItems[tabMenuIndex].getAttribute("id");
		if (currentTabMenuItemId == currentTabId)
		{
			var currentTabMenuItem = allTabMenuItems[tabMenuIndex];
		}	
		var idnum = currentTabMenuItemId.charAt(currentTabMenuItemId.length-1);
		if(maxid <= idnum)
		{
			maxid = idnum;	// to get the new tab with always max of all the existing tab no
		}
	}

	var tabPaneItem = document.getElementById("tabFrame_"+propertyName);
	nonactiveTabPane = FragmentUtil.getElementsByClassName("nonactive-tabPane", "div", tabPaneItem );
	tabnumber= nonactiveTabPane.length + 2 + remcount; // To pick a new tab number.
	if (tabnumber <= maxid)
	{
		tabnumber = Number(maxid) + 1;	//max ID is needed to avoid duplication with existing tab id numbers.
	}
//CREATING NEW SPAN	
	var newspan = document.createElement('span');
	var spanIdName = 'tabMenu'+tabnumber;
	newspan.setAttribute('id',spanIdName);
	newspan.setAttribute('className',"nonactive-tab");
	
	tabMenuItem.insertBefore(newspan, currentTabMenuItem.nextSibling);

	var newspan1 = document.createElement('a');	
	var newFunction = "return raisePanel("+tabnumber+",'"+propertyName+"')";
	newspan1.setAttribute('href',"javascript:raisePanel("+tabnumber+",'"+propertyName+"')");
	newspan1.setAttribute('id',"tabEnri"+tabnumber+propertyName);
	newspan1.innerHTML = "Period "+tabnumber;//getTabdate(newDate);
	newspan1.className = 'tab-head';
	newspan.appendChild(newspan1);
	
	var newimg1 = document.createElement('a');
	var imageIdName = 'tabAddMenu'+tabnumber;
	newimg1.setAttribute('id',imageIdName);
	
	var newimg = document.createElement('img');
	newimg.setAttribute('id',"add"+spanIdName);
	newimg.setAttribute('title',"Expand Tab");
	newimg.setAttribute('alt',"Expand Tab");
	newimg.setAttribute('reldropfieldname',"fieldName:ID.COMP.6");
	newimg.setAttribute('src',"../plaf/images/default/deal/addTab.gif");
	newimg1.appendChild(newimg);
	newspan.appendChild(newimg1);

 	var newimg2 = document.createElement('a');	
 	var delImageIdName = 'tabDelMenu'+tabnumber;
 	newimg2.setAttribute('id',delImageIdName);	
	
	var newimgDel = document.createElement('img');
	newimgDel.setAttribute('id',"del"+spanIdName);
	newimgDel.setAttribute('title',"Delete Tab");
	newimgDel.setAttribute('alt',"Delete Tab");
	newimgDel.setAttribute('src',"../plaf/images/default/deal/delTab.gif");
	newimg2.appendChild(newimgDel);
	newspan.appendChild(newimg2);

	
	//add new tab pane content
	var ni = document.getElementById("tabFrame_"+propertyName);
	

	var newdiv = document.createElement('div');
	var divIdName = 'tabPane'+tabnumber+propertyName;
	newdiv.setAttribute('id',divIdName);
	newdiv.setAttribute('className','nonactive-tabPane');
	
	ni.appendChild(newdiv);

	raisePanel(tabnumber,propertyName); //raise the newly expanded tab
	gettabcontent(tabnumber,propertyName); // get the content of newly expanded tab from server.

}
/**
 * Function to handle when a tab is been deleted by user
 */
function delTabMenu(tabnumber,propertyName,conditionForm)
{

	setCurrentForm(conditionForm);
	raisePanel("1",propertyName);
	var spanIdName = 'tabMenu'+tabnumber;
	var oldspan = getChildElementFromParent("tabMenuDiv_"+propertyName, spanIdName, "span");
	oldspan.style.display="none";   
           
	var divIdName = 'tabPane'+tabnumber+propertyName;
	var olddiv = getChildElementFromParent("tabFrame_"+propertyName, divIdName, "div");
	olddiv.style.display = "none";
	
	var form = getChildElementFromParent("tabFrame_"+propertyName, currentForm_GLOBAL.toString(), "form");
	for ( var i = 0; i < form.elements.length; i++ )
	{
		var field = form.elements[i];
		if ( field.name == "deleteGroupTab" )
		{
			field.value = "YES";
		}	
	}       
          
}

/**
 * Utility function just to show an error if I try to get non existen objects
 */
function getItemObj ( itemId )
{
   obj = document.getElementById(itemId);

 //  if ( obj == null ) alert('Script Error: id='+itemId+' does not exist');

   return obj;
}
/*
* Function to raise required tab and let other tab belonging to a tab disappear
* @param tabNumber 
*/
function raisePanel(tabnumber,propertyName)
{
	
	tabMenuItem = document.getElementById("tabMenuDiv_"+propertyName);
	activeTabMenu = FragmentUtil.getElementsByClassName("active-tab", "span", tabMenuItem );	
	if (activeTabMenu != null || activeTabMenu != "" || activeTabMenu != undefined)
	{
		activeTabMenu[0].className = "nonactive-tab";
	}
	tabPaneItem = document.getElementById("tabFrame_"+propertyName);//getItemObj("tabFrame_"+propertyName);
	activeTabPane = FragmentUtil.getElementsByClassName("active-tabPane", "div", tabPaneItem );
	activeTabPane[0].className = "nonactive-tabPane";

	tabMenuName = 'tabMenu' + tabnumber;
	Menuobj = getChildElementFromParent("tabMenuDiv_"+propertyName, tabMenuName, "span");//getItemObj(tabMenuName);
	Menuobj.className = "active-tab";

	tabPaneName = 'tabPane' + tabnumber+propertyName;
	Paneobj = getChildElementFromParent("tabFrame_"+propertyName, tabPaneName, "div");//getItemObj(tabPaneName);
	Paneobj.className = "active-tabPane";

}

function gettabcontent(tabnumber,propertyName)
{
	var conditionForm = currentForm_GLOBAL.toString();
	
	//Clear WS_dropfield if any set
	
	// Check if this request should be a multiple request for AA type applications.
	var multiRequest = getFormField("generalForm", "multiPane");
	if( multiRequest == null || multiRequest == "undefined") {
		alert("not a property!");
		return;
	}

 	var formatedAppList = mainAppForm_GLOBAL;
 	var newElement = null;
 	// Indicate we are unloading the window and not closing it
	closingWindow = false;
 	// Get the multiPane form which we use to submit the whole multi request.
	//multiPaneForm_GLOBAL = FragmentUtil.getForm("multiPane");
	// Need this parameter for the servlet.
	
	var appreqForm = FragmentUtil.getForm("appreq");
	// Create the params out of the multiPane form
	var params = FragmentUtil.getFormFieldsAsParams(appreqForm,false);

	//Fetch previous property condition
	var currentPropertyForm = FragmentUtil.getForm(conditionForm);
	var currentPropertyId = getFormFieldValue( conditionForm, "transactionId" );
	var currentPropertyApp = getFormFieldValue( conditionForm, "application" );
	var currentPropertyVer = getFormFieldValue( conditionForm, "version" );
	
	if (currentPropertyForm == '')
	{

		var currentPropertyForm = getChildElementFromParent("tabFrame_"+conditionForm, conditionForm, "form");
	
		var currentPropertyId = currentPropertyForm.elements["transactionId"];
		var currentPropertyApp = currentPropertyForm.elements["application"];
		var currentPropertyVer = currentPropertyForm.elements["version"];

	}

	setFormFieldValue( "newPropertyDateForm_"+propertyName, "fieldName:ID.COMP.6","" );
//Spilt ID as it will be of form AA.ARR.INTEREST,AA.PERIODIC.PAAA090076SYDP-PRINCIPALINT-20090107.1
	var tempPropertyId = currentPropertyId.split("-");
	var tempPropertyDate = tempPropertyId[2];
//Split third componenet of Id as it will have <date>.<sno>
	var tempPropertySno = tempPropertyDate.split(".");
	//newDate is global variabel which is initialised in addTabMenu().
	var newPropertyId = tempPropertyId[0] + "-" + tempPropertyId[1] + "-" + newDate + "." + tempPropertySno[1];
	// Change the transactionId for this form
	var newConditionId = newPropertyId;// + "_" + newDate;
	
	var updatespan = getChildElementFromParent("tabMenuDiv_"+propertyName, 'tabMenu'+tabnumber, "span");//document.getElementById('tabMenu'+tabnumber);
	updatespan.setAttribute('onmousedown',"setCurrentForm('"+currentPropertyApp+ currentPropertyVer + newConditionId+"')");
	updatespan.setAttribute('propertyname',currentPropertyApp+ currentPropertyVer + newConditionId);
	
	var updateDTag = getChildElementFromParent("tabMenuDiv_"+propertyName, 'tabDelMenu'+tabnumber, "a");//document.getElementById('tabMenu'+tabnumber);
	updateDTag.setAttribute('href',"javascript:delTabMenu("+tabnumber+",'"+propertyName+"','" +currentPropertyApp+ currentPropertyVer + newConditionId + "')");
	
	var tabMenuItem = document.getElementById("tabMenuDiv_"+propertyName);
	var  allTabMenuItems= tabMenuItem.getElementsByTagName("span");
	//Prepare the list of all the dates of tabs to send in the request.
	for (tabMenuIndex=0; tabMenuIndex < allTabMenuItems.length ;tabMenuIndex++)
	{
		var currentTabPropertyname = allTabMenuItems[tabMenuIndex].getAttribute("propertyname");
		var currentTabStyle = allTabMenuItems[tabMenuIndex].getAttribute("style");	//for IE it is always an object.
		var currentClassname = 	allTabMenuItems[tabMenuIndex].getAttribute("classname");	
			
			if (isInternetExplorer())
			{
			 currentTabStyle = currentTabStyle.display;
			 currentClassname = allTabMenuItems[tabMenuIndex].classname;
			}
			if(currentClassname == "error-tab")
			{
				continue;		//to skip the tabs with error.
			}
			//changed the way dates list is sending to T24 (contains sequence number also)
			if(currentTabStyle != null && currentTabStyle != "")
				{
				datesList += "H"; //for the tabs which are deleted by clicking '-' button.
				}
			if(currentTabPropertyname != null)
			{
			var tempPropertyname = currentTabPropertyname.split("-");
			var tempPropertyDate = tempPropertyname[2];
			datesList += tempPropertyDate + "|";  		//changes done to send dates with .1/.2
			}		
	}
//remove the last added |
	datesList = datesList.substring(0, datesList.length-1);		//dates list is ready.
// to pick elements of previous property forms

	  	// Loop round all the fields in the specified form
		var elems = currentPropertyForm.elements;

		for (var i = 0; i < elems.length; i++) 
		{
			// Check if this is a WS_ field, is so don't add it.
			var wsName = elems[i].name + "";
	 		if ( wsName.indexOf("WS_") == 0 )	 // Does it have a "WS_" prefix.
	 		{
	 			continue;
	 		}
			elementName = conditionForm + ":" + elems[i].name;
				
			if ( elems[i].type == "radio" )
			{
				// Just ignore as radios have a hidden field behind them.
				continue;				
			}
			else
			{
				elementValue = elems[i].value;
			}
			
			params += "&" + elementName + "=" + elementValue;
		}


	params += "&" + "newProperty:application="+ currentPropertyApp;
	params += "&" + "newProperty:version="+ currentPropertyVer;
	params += "&" + "newProperty:transactionId="+ newConditionId;
	params += "&" + "newProperty:datesList="+ datesList;	//dates are added to request.
	params +="&" + "propertyList="+currentPropertyApp+currentPropertyVer+currentPropertyId;
	params +="&" + "OldGroupTabID="+tabnumber +"|" +propertyName + "|" +currentPropertyApp+currentPropertyVer+newConditionId;

//	params += "&WS_FragmentName=" + fragmentName; // TODO: Do we need this?


	/**********************************************************************/
	
	var genForm = FragmentUtil.getForm("generalForm");
	var SaveWorkareaFragment = Fragment.getCurrentFragmentName();
	Fragment.setCurrentFragmentName("tabPane"+tabnumber+propertyName);
	var workareaFragment = Fragment.getCurrentFragment();
	

	// Set general form params and submit it
    genForm.requestType.value = "oh1y1haCb}oh16Y";
    genForm.routineName.value = "OS.ADD.NEW.TAB.CONTENT";
	genForm.routineArgs.value = params;
	genForm.transactionId.value = getFormFieldValue( "appreq", "transactionId" );

    var screenModeSet = 0;
  	for ( var i = 0; i < genForm.elements.length; i++ )
  	{
  		var field = genForm.elements[i];
  		if ( field.name == "screenMode" )
  		{
  			screenModeSet = 1;
  			field.value = getFormFieldValue( "appreq", "screenMode" );
  		}	
	}
  	if (screenModeSet == 0)
  	{
  		var newElement = document.createElement("input");
  	    newElement.type = "hidden";
  	    newElement.name = "screenMode";
  	    newElement.value = getFormFieldValue( "appreq", "screenMode" );
  	    genForm.appendChild(newElement);
  	}
    
    var newPropertyApplicationSet = 0;
 
    for ( var i = 0; i < genForm.elements.length; i++ )
    {
    	var field = genForm.elements[i];
     	if ( field.name == "newProperty:application" )
 		{
     		newPropertyApplicationSet = 1;
     		field.value = currentPropertyApp;//getFormFieldValue( "appreq", "screenMode" );;
 		} 
    }
    if (newPropertyApplicationSet == 0)
    {
         var newElement = document.createElement("input");
         newElement.type = "hidden";
         newElement.name = "newProperty:application";
         newElement.value = currentPropertyApp;
         genForm.appendChild(newElement);
    }
    

    
    var currentPropertyListIDSet = 0;

    for ( var i = 0; i < genForm.elements.length; i++ )
    {
    	var field = genForm.elements[i];
    	if ( field.name == "currentPropertyListID" )
    {
    		currentPropertyListIDSet = 1;
    		field.value = currentPropertyId;//getFormFieldValue( "appreq", "screenMode" );;
    } 
    }
    if (currentPropertyListIDSet == 0)
    {
    	var newElement = document.createElement("input");
    	newElement.type = "hidden";
    	newElement.name = "currentPropertyListID";
    	newElement.value = currentPropertyId;
    	genForm.appendChild(newElement);
    }
	// set window name so that if it is a contract, it can be unlocked later
	genForm.windowName.value = window.name;
	
	var params = FragmentUtil.getFormFieldsAsParams(genForm);
	
	workareaFragment.populateWithURL(genForm.action,params);
	
	for (var index=0; index<appFormList_GLOBAL.length; index++)
	{
		
		if (appFormList_GLOBAL[index].indexOf(conditionForm) > -1)
		{
			
			appFormList_GLOBAL.splice(index+1,0,"tabPane" + tabnumber + propertyName + "~" +propertyName + "~" +currentPropertyApp+currentPropertyVer+newConditionId);
		}
	}

	Fragment.setCurrentFragmentName(SaveWorkareaFragment);


}


function getChildElementFromParent(parentId, childId, childTag)
{
	var childElement = '';
	var parentItem = document.getElementById(parentId);
	var  allChildItems= parentItem.getElementsByTagName(childTag); //fetch all child elements containing specified tag
	for (index=0; index < allChildItems.length ;index++)
	{
		var childItemId = allChildItems[index].getAttribute("id");
		
		if (childItemId == childId)
		{
			childElement = allChildItems[index];
			return childElement;
		}	
	}
	return childElement;
}

function getTabdate(tabdate)
{
      var months = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
      var year = tabdate.substring(0,4);
      var month = tabdate.substring(4,6);
      var date = tabdate.substring(6,8);
      var tabdate = date+' '+months[parseInt(month)-1]+' '+year;
      return tabdate;
}

//To remove the error tab from the screen.
function remTabMenu(tabnumber,propertyName,conditionForm)
{
      setCurrentForm(conditionForm);
      raisePanel("1",propertyName);
      var spanIdName = 'tabMenu'+tabnumber;
      var errorspan = getChildElementFromParent("tabMenuDiv_"+propertyName, spanIdName, "span");
      
      var divIdName = 'tabPane'+tabnumber+propertyName;
      var errordiv = getChildElementFromParent("tabFrame_"+propertyName, divIdName, "div");
      errordiv.parentNode.removeChild(errordiv);
      var fragName="tabPane"+tabnumber+propertyName;
      var fragsArray=Fragment._fragments;
     
      updateAppFormList(fragName);
      if (fragsArray[fragName])
              {
              fragsArray[fragName].element = null;
              }
      errorspan.parentNode.removeChild(errorspan);
	  remcount=remcount+1;
}

function updateAppFormList(oldid,newid)
{
	for (var index=0; index<appFormList_GLOBAL.length; index++)
      {
         if (appFormList_GLOBAL[index].indexOf(oldid) > -1)
            {
				  if(newid != "" && newid != null)
				  {
				  appFormList_GLOBAL.splice(index,1,newid);
				  newOneIndx = index;			//it gets updated for every new tab.(for request order)
				  }
				  else
				  {
				  appFormList_GLOBAL.splice(index,1);
				  }
				  break;
            }
      }   
}


function initAddNewpropertyForm(error,duplicateexists,oldID,NewID,tabEnri)
{
      var oldIdData = oldID.split("|");
      var tabnumber = oldIdData[0];
      var propertyName = oldIdData[1];
      var updatespan = getChildElementFromParent("tabMenuDiv_"+propertyName, 'tabMenu'+tabnumber, "span");//document.getElementById('tabMenu'+tabnumber);
      updatespan.setAttribute('onmousedown',"setCurrentForm('"+NewID+"')");
      updatespan.setAttribute('propertyname',NewID);
      var updateATag = getChildElementFromParent("tabMenuDiv_"+propertyName,'tabAddMenu'+tabnumber,"a");
      var updateDTag = getChildElementFromParent("tabMenuDiv_"+propertyName, 'tabDelMenu'+tabnumber, "a");//document.getElementById('tabMenu'+tabnumber);
      //Update the enrichment
	  var textNode = document.getElementById('tabEnri'+tabnumber+propertyName);
	  textNode.innerHTML = tabEnri;
      var fragName="tabPane"+tabnumber+propertyName;
      if(error)
      {
            updateATag.parentNode.removeChild(updateATag);
            updatespan.setAttribute('classname',"error-tab");
            updateDTag.setAttribute('href',"javascript:remTabMenu("+tabnumber+",'"+propertyName+"','" +NewID + "')");
            updateAppFormList( fragName);   
      }
      else
      {
	  updateATag.setAttribute('href',"javascript:relativeCalendarDisplay2('" +NewID + "')");
      updateDTag.setAttribute('href',"javascript:delTabMenu("+tabnumber+",'"+propertyName+"','" +NewID + "')");

	  //check if already with the new date, value is there in appListForm_GLOBAL and having different sequence,then get its index	(for request order)
	  var oldOneIndx = 0; //initiate
	  var newIDdetails = NewID.split("-");
	  var serverDate = newIDdetails[2].split(".")[0];
	  var searchString = propertyName+"-"+serverDate;
	  for (var index=0; index<appFormList_GLOBAL.length; index++)
      {
           if (appFormList_GLOBAL[index].indexOf(searchString) > -1)
            {
			  oldOneIndx = index;	//Need to chage the order of the requests.
            }
      }
      // IF THIS IS A NEW TAB THAT IS ADDED FOR THE FIRST TIME
      oldID = "tabPane" + tabnumber+propertyName+"~"+propertyName+"~"+oldIdData[2];
      var tempNewID = oldID.split("~");
	  tempNewID = tempNewID[0]+"~" +tempNewID[1]+ "~" +NewID;
	  updateAppFormList(oldID,tempNewID);
	  
       if(duplicateexists!='')
        {    
             var propname=NewID.split("-");
             var tabMenuItem = document.getElementById("tabMenuDiv_"+propname[1]);
             var  allTabMenuItems= tabMenuItem.getElementsByTagName("span");
             var count = 0;
             var oldIndex = 0; 
             var newIndex = "";
             //get all the tabs for the current property 
             for (tabMenuIndex = 0; tabMenuIndex < allTabMenuItems.length ;tabMenuIndex++)
               {
                     var currentTabPropertyname = allTabMenuItems[tabMenuIndex].getAttribute("propertyname");
                     var currentTabStyle = allTabMenuItems[tabMenuIndex].getAttribute("style");  //for IE it is always an object.
                     if(isInternetExplorer())
                      {
                          currentTabStyle = currentTabStyle.display;     
                      }
                     if(currentTabPropertyname != null)
                      {
                           var tempPropertyname = currentTabPropertyname.split("-");
                           var tempPropertyDate = tempPropertyname[2];
                           if(compareDates(tempPropertyDate,propname[2]) == 0)
                            {
                                  count = count+1;
                                  if(count == 1)
                                   {
                                      if(currentTabStyle !='' && currentTabStyle != null)	//check for display style 
                                      {
                                         newIndex=tabMenuIndex;
                                      } 
                                   } 
                                   //This is a new tab that is added for which a deleted tab exists in the form 
                                  if (count == 2)
                                   {
                                       if(newIndex)
                                        {
                                           newIndex=newIndex;
                                        }
                                       else
                                        {
                                           newIndex=tabMenuIndex;
                                        }     
                                       //deleted tabs are at the the array --at the end .So locate second occurance
                                       var dispTab=parseInt(allTabMenuItems[newIndex].getAttribute("id").substring(7));
                                       //REMOVE THE old BODY PART
									   var divIdName = 'tabPane'+dispTab+propertyName;
									   var errordiv = getChildElementFromParent("tabFrame_"+propertyName, divIdName, "div");
									   errordiv.parentNode.removeChild(errordiv);
									   if(appFormList_GLOBAL[oldOneIndx].indexOf(searchString) > -1)
										  {
											appFormList_GLOBAL.splice(oldOneIndx,1);
										  }
									   //REMOVE THE old HEAD PART
									   var spanIdName = 'tabMenu'+dispTab;
									   var errorspan = getChildElementFromParent("tabMenuDiv_"+propertyName, spanIdName, "span");
									   errorspan.parentNode.removeChild(errorspan);
									   //REMOVE THE old FRAGMENT
									   fragName="tabPane"+dispTab+propertyName;
                                       var fragsArray=Fragment._fragments;
							           if (fragsArray[fragName])
                                        {
                                            fragsArray[fragName].element = null;
                                        }
                                       break;
                                   }     
                       		}
                      }                 
               }
        }
       	else
			{
			//date same but not duplicate(sequence is different)
			if (oldOneIndx > 0)		// do inter change
				{
				tempID = appFormList_GLOBAL[oldOneIndx];
				appFormList_GLOBAL[oldOneIndx] = appFormList_GLOBAL[newOneIndx];
				appFormList_GLOBAL[newOneIndx] = tempID;
				}
			}
	  sortAppFormList(propertyName);	//To sort(by date) the forward date ids of this property
      }
}

// Function to show the hidden fields if they are in hidden form.
function showHiddenTabs()
{
	var mydivs = FragmentUtil.getElementsByClassName("tabMenu-hide", "div");
	for(var i=0;i<mydivs.length;i++)
	{
		mydivs[i].className = "tabmenu";
	}
	var genForm = FragmentUtil.getForm("generalForm");
	updatedFormsField( "WS_showTabs", "true" );
	
	var imgObject = document.getElementById("showTab");
	if(imgObject)
	{
		imgObject.src = "../plaf/images/"+getSkin()+"/tools/showTab_dis.gif";
		
		var imgParentA = imgObject.parentNode;
		var imgParentTD = imgParentA.parentNode;
		
		imgParentTD.removeChild(imgParentA);
		imgParentTD.appendChild(imgObject);
	}

}

function sortAppFormList(propertyName)
{
var indxArray = appFormList_GLOBAL.find(propertyName);	//to get the indices of the particular property.
var sortArray = [];	//part of global array to be sorted
var datesArray = [];	//dates of the sortArray
for(i=0;i<indxArray.length;i++)
{
	sortArray[i] = appFormList_GLOBAL[indxArray[i]];
	datesArray[i] = sortArray[i].split("-")[2];
}
datesArray.sort();	//sorting the dates.
for(j=0; j<datesArray.length;j++)
{
	appFormList_GLOBAL[indxArray[j]] = sortArray[sortArray.find(datesArray[j])];
}
}
