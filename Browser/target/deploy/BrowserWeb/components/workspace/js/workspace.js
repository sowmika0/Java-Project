var c_renameAllowed = 1;
var c_activeTab;
var c_deletingTab = -1;
var GRID_SIZE = 30;
var REFRESH_SEGMENTS = 4;
var REFRESH_TIME = 60;
function workspace_help(){
    help("GB","workspace","");
}

function getRefreshInterval(){
	var tmp = (REFRESH_TIME/REFRESH_SEGMENTS)*1000;
	return (Math.round(tmp));
}

// Main function to actually save the screen layout.
// additionalItem allows other funtions to add items to the screen!

function snapToGrid(value){
	tmp = Math.round(value/GRID_SIZE);
	return (tmp*GRID_SIZE);
}

function workspace_saveLayout(additionalItem){
    var toSave = "";
    var tabNames = "";
    // Loop through each tab
    for (var tabNo = 1; tabNo < 100; tabNo ++){
        
        
		    itemname = "workspacetab" + tabNo;
		    var item = window.document.getElementById(itemname);
		    if(item!=null){
    // For each tab, find the items that are there and their positions		
                if (tabNo!=c_deletingTab){
                    toSave = toSave + "*"+workspace_saveTab(tabNo);
                    tabNames = tabNames + getTabName(tabNo)+ "|";
                }
		    } else {
		        tabNo = 100;
		    }
		 
    }
    var wsId = document.getElementById("workspaceid");
    var workspaceId = wsId.value;
    activeTab=getActiveTab();
    toSave = tabNames  +"*"+ activeTab+"*"+workspaceId + toSave +additionalItem;
    
    buildUtilityRequest("OS.GET.WORKSPACE.XML", toSave,"generalForm",window.name,"","","","","");
}
function workspace_refresh(){

		ws_rename_containers()
		FragmentUtil.initFragments('INIT_FRAGMENTS_ALL', "60000",true);
}


function ws_rename_containers(){
	// Reset the class names for our containers for the refresh works!

		for(var i=2;i<100;i++){		
			var ws = window.document.getElementById("ws"+i);
			if (ws!=null){
				ws.className = "fragmentContainer";
			} else {
				i=100;
			}
		}
}


// Moves an item onto a different tab
function workspace_moveTab(targetTabNo, divId){

    var new_data = targetTabNo + "|" + workspace_getPaneData(divId);
    closepane(divId);
    workspace_saveLayout(new_data);
    
}
// Returns the name of a specified tab
function getTabName(tabNo){
           var tabhref = window.document.getElementById("tabhref"+tabNo);
           return tabhref.innerHTML;
}

function disableOnBlur(){
    c_renameAllowed = 0;
}

function enableOnBlur(){
    c_renameAllowed = 1;
}

// Delete the active tab...
function deleteTab(){
    c_deletingTab = c_activeTab;
    workspace_saveLayout("");
}

// Show the tab rename input box and set the focus to it
function startTabRename(tabNo){
  //   if(c_activeTab == tabNo) {
            var divtabrename = window.document.getElementById("divTabRename");
            var tabhref = window.document.getElementById("tabhref"+tabNo);
            var inpTabRename = window.document.getElementById("inpTabRename");
            var divTarget = window.document.getElementById("workspace_tabdroptarget"+tabNo);
            divtabrename.style.top = getElementY(divTarget);
            divtabrename.style.left = getElementX(divTarget);
            divtabrename.style.visibility = "visible";
            inpTabRename.value = tabhref.innerText;
            inpTabRename.tabno = tabNo;
            inpTabRename.focus();
            enableOnBlur();
   // }        
}

//Once the tab rename is complete then set the name of the tab
function setTabText(){

    if (c_renameAllowed==1) {
           var inpTabRename = window.document.getElementById("inpTabRename");
           tabNo = inpTabRename.tabno;
           
            var divtabrename = window.document.getElementById("divTabRename");
            var tabhref = window.document.getElementById("tabhref"+tabNo);
            
            tabhref.innerText = inpTabRename.value;
            divtabrename.style.visibility = "hidden";
            disableOnBlur();
    }
}
//Hides a given pane
function evtClose_pane(evt){
  //  findDiv(evt.source);
    box.style.visibility = "hidden";
}
//Hides a given pane
function closepane(divId){
    var divPane = window.document.getElementById(divId);
    divPane.style.visibility = "hidden";
}

function change_size(evt){
	evt = new Evt(evt);
	var	theDiv = findDiv(evt.source);
    var newHeight=1;
    if (evt.source.className=="workspace_sizechanger workspace_min"){
        evt.source.className="workspace_sizechanger workspace_restore"
        evt.source.customTag = theDiv.style.height;
        
    } else {
        evt.source.className="workspace_sizechanger workspace_min"
        newHeight = evt.source.customTag
    }
    
    theDiv.style.height = newHeight;
    box = theDiv;
    var divId = theDiv.id.substring(3);
  
    resizeInside(divId);
}

// Returns the save data for a given tab
function workspace_saveTab(tabNo){


//tabNo|ROUTINE|TEC.SUMMARY.XML|SUMMARY|*tabNo|CMD|ENQ TEC.TXN.SUMMARY NONE|
//tabNo|ROUTINE|TEC.SUMMARY.XML|SUMMARY|windowpositions*tabNo|CMD|ENQ TEC.TXN.SUMMARY NONE|
     var tabData ="";
     for (var paneNo = 1; paneNo < 100; paneNo ++){
        var paneId = "saveTab" + tabNo + "Pane" + paneNo;
        
        var item = window.document.getElementById(paneId);
            noToUse = tabNo;
            
            if (tabNo > c_deletingTab && c_deletingTab > 0){
                noToUse = noToUse - 1;
            }
        if(item!=null){
   
        // Now get the div itself to find the position
            divId = "div" + item.value;
            paneData = workspace_getPaneData(divId);

            tabData = tabData + noToUse + "|" + paneData + "*";
            
        } else {
            if (paneNo==1){
                paneData = noToUse +"|BLANK|||";
                tabData = tabData + paneData + "*";
            }
            paneNo = 100;
         }
     }
     
     return tabData;
}
// returns the tab data for a given pane
function workspace_getPaneData(divId){
            var theDiv = window.document.getElementById(divId);
            var paneData = ""
            if (theDiv.style.visibility != "hidden") {
                var windowPos = theDiv.style.top + "_" + theDiv.style.left + "_" + theDiv.style.width + "_" + theDiv.style.height;
                
                var actionId = "Action" + divId; // holds the widget id
                var actionData = window.document.getElementById(actionId);
                var titleId = "Title" + divId; // or the routine, type and args when not a widget
                var titleData = window.document.getElementById(titleId).value;    

                var widgetId = actionData.value;

                    paneData = widgetId + "|"+titleData+"||"+ windowPos;
              }
              
              return paneData;
}
// Work out the number of tabs that we have
function find_noOfTabs(){
    var table_theTabs = window.document.getElementById("thetabs");
    var row_firstRow = table_theTabs.rows[0];
    var insertPos = row_firstRow.cells.length-6
    
    noOfTabs = (insertPos-1)/2;
    noOfTabs = noOfTabs+1;
    return noOfTabs;
}

// Work out which is the active tab by checking the class name...
function getActiveTab(){
   var table_theTabs = window.document.getElementById("thetabs");
    var row_firstRow = table_theTabs.rows[0];
    var length = row_firstRow.cells.length
    for (var i=0;i<length;i++){
        if (row_firstRow.cells[i].className=="tab selectedtab"){
            active = i;
            i=100;
        }
    }
    
    
    return active;
}
// Add a new tab
function workspace_addTab(){

    noOfTabs = find_noOfTabs();
    
    toAdd = (noOfTabs-1) + "|BLANK"
    
    workspace_saveLayout(toAdd);
    
}

//
function workspace_addWidget(widget){
    activeTab = getActiveTab();
    workspace_saveLayout(activeTab+"|"+widget);  
}
function workspace_addContent(widget, title){
    activeTab = getActiveTab();
    workspace_saveLayout(activeTab+"|"+widget+"|"+title);  
}
// init is called on page load....
function init(){

	var wo = new workspaceObject();
	var timer = new Timer();
	var interval = getRefreshInterval();
	timer.start(interval,wo);
	FragmentUtil.initFragments('INIT_FRAGMENTS_ALL', "60000");

}

function setClockImage(count){
	var clockImg = window.document.getElementById("refresh_display");

	var src = "../components/workspace/images/refresh/clock"+ count;
	var src = src + REFRESH_SEGMENTS+".gif";
	

	clockImg.src = src;
}

function workspaceObject(){
 var refreshCount = 0;
 
 this.ping = function(){
 	refreshCount = refreshCount +1;
 	setClockImage(refreshCount)
 	if(refreshCount==REFRESH_SEGMENTS){
 		refreshCount = 1;
 		workspace_refresh();
 	}

	var timer = new Timer();
		var interval = getRefreshInterval();
	timer.start(interval,this);
 	
 }
}

// Shows a given tab, and hides all the others
function show_atab(tabno, name){
c_activeTab = tabno;
for ( var i = 1; i < 100; i++ )
	{
		itemname = name + i;
		var item = window.document.getElementById(itemname);
		if(item!=null){
		tabname = "the"+name+i
			var thetab = window.document.getElementById(tabname);
			if(i==tabno){
				item.style.visibility="visible";
				thetab.className = "tab selectedtab";
			} else {
				item.style.visibility="hidden";
				thetab.className = "tab unselectedtab";
			}
		}	else {
			i= 100;
		}
	
	}		

}

function showmenutab(tabno){

    for (var i=1;i<5;i++){
        for (var j=1 ; j < 5; j++){
            rowid = "menurow"+i +"a"+j;
            
            var row = document.getElementById(rowid);
            if(row!=null){
        
                if (i==tabno){
                    row.style.display = "block";
                } else {
                    row.style.display = "none";
                }
            }
        }
    }
}

function showtab(tabno){
show_atab(tabno,"workspacetab")
}

// Used to start / stop the refresh timer
function startTimer(){
// Save the start time so we can send it up to the server.

	var clockBox = document.getElementById("refreshtime");
	if(clockBox==null){
				return;
	}
// Check that we have valid input, otherwise out
	if(clockBox.value > 0 && clockBox.value < 600){
	}
	else
	{
		return;
	}

// Start timer
	{
		
		clockBox.disabled="true";
		enqTimer = setInterval('refreshworkspaceClock();', 1000);
		
	}
}




// Add an item to the current tab
function addme(itemname){
    //alert(itemId);

			var item = window.document.getElementById(itemname);
			if(item!=null){
			    workspace_saveLayout(item.value);
				
					}
					
}

  function togglerow(sScope, mystyle)
      {
        var oTable = window.document.getElementById("r"+sScope);
        if (oTable)
        {
            oTable.style.display = mystyle;
        }
      }

// use to expand collapse the tree view. Note that this function will 
// recursively expand or contract all the nodes under the root node that
// has been clicked.      
function expandrow(start, count){
	//Work out whether we are showing or hiding...
	var oTable = window.document.getElementById("r"+start);
	var mystyle="block";
	var skin = getSkin(); // which skin to use
    if (oTable && oTable.style.display == "block")
    {
        mystyle = "none";
    }
//
// Now loop through and toggle each row. 
//
 	for( var myrow = start;myrow<count+start;myrow++){
		togglerow(myrow,mystyle);
//
// and ensure that we are showing the correct image for the state!
//
		var imgObject = window.document.getElementById("treestop"+myrow); // pull the image object out
		if(imgObject){
			if(mystyle=="none"){
				imgObject.src = "../plaf/images/" + skin + "/deal/mvexpansion.gif";	// make it a plus
			} else {
				imgObject.src = "../plaf/images/" + skin + "/block.gif";	// make it hide
			}
		}
	}
}