function AJAXInteraction( data) {
url = data;
    this.url = url;
    var req = init();
    req.onreadystatechange = processRequest;
        
    function init() {
      if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
      }
    }
    
    function processRequest () {
      if (req.readyState == 4) {

        if (req.status == 200) {
          postProcess(req.responseText);
        }
      }
    }
    this.send = function() {
        req.open("GET", url, true);
        req.send(null);
    }
}
function postProcess(response) {

	thediv =  document.getElementById("div"+divId);
	if (thediv==null){
		alert("Can't find the div "+ divId);
	}else{

		thediv.innerHTML = response;
	}
}
//-----------------------------------------------------------------------------------------------------------------------
function tecmin(divId){
	thediv =  document.getElementById("div"+divId);
	thetable =  document.getElementById("table"+divId);
	thediv.style.background="blue";
	
	if(thetable!=null){
	alert(thetable.innerHTML.length)
		if(thetable.innerHTML.length=0){
			tecaction(divId);
		} else {
			thetable.innerHTML="";
			thediv.style.height=50;
		}
	} else {
		alert("Table not found")
	}
}
function highlight(cellid){
	setcellclass(cellid, "highlight");
}
function unhighlight(cellid){
	setcellclass(cellid, "unhighlight");
}
function setcellclass(cellid, classname){
	var cell= window.document.getElementById(cellid);
	
		if (cell != null)
		{
				cell.className = classname;
		}
}

function tecrefresh()
{
routinename="TEC.SUMMARY.XML";
buildUtilityRequest(routinename,"","generalForm",window.name,"","","","","");
}

function tecoption(selectitem){

	// Find the object that holds the options
	var combo = window.document.getElementById(selectitem);

	if (combo==null) 
	{
		// Shouldnt happen but acts as an error trap
	} 
	else 
	{
		var item = combo.selectedIndex;           // The item that is selected
		var itemtext = combo.options[item].value; // And the text
		
		
docommand(itemtext);

		
	}
}


function dotecrequest(action){
buildUtilityRequest("TEC.SUMMARY.XML", action,"generalForm","","","","","","");
}

// ajax stuff here..
//-----------------------------------------------------------------------------------------------------------------------
var divId;
function tecaction(myaction){
divId = myaction;
args = "?method=post&command=globuscommand&requestType=UTILITY.ROUTINE&routineName=TEC.SUMMARY.XML&routineArgs="+myaction
url = "http://localhost:8080/BrowserWeb/servlet/BrowserServlet" + args
        var ajax = new AJAXInteraction( url);
        ajax.send();
}
