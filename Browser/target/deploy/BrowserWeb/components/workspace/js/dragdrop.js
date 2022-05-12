		var box;
		var deltaX, deltaY;
		var resizeInProgress;
		var workspace_orginalWidth, workspace_originalHeight;
		var workspace_itemName;
		var workspace_zorderCount=0;

function resizePress(evt){
	resizeInProgress=1;
	evt = new Evt(evt);
	box = findDiv(evt.source);
	
	//setOpacity(box,.7);
	hideBoxShowWireFrame(box,evt);

	Evt.addEventListener(document,"mousemove",resizeMove,false);
	Evt.addEventListener(document,"mouseup",resizeRelease,false);	
}

function hideBoxShowWireFrame(box,evt){
		var div_outline = window.document.getElementById("outline");
    
    
	div_outline.style.top = (getElementY(box)+"px");
	div_outline.style.left = (getElementX(box)+"px");
	
	deltaX = evt.x; // original x position of push
	deltaY = evt.y // original y position of push
	workspace_originalWidth = deltaX - getElementX(box)
	workspace_originalHeight = deltaY - getElementY(box)
	
	div_outline.style.width = (workspace_originalWidth + "px");
	div_outline.style.height = (workspace_originalHeight + "px");
	div_outline.style.visibility = "visible";
	box.style.visibility = "hidden"; // Hide the box that we are reszing!
}

function resizeMove(evt){
		    var workspace_maintable = window.document.getElementById("maintable");
		    var div_outline = window.document.getElementById("outline");
			evt = new Evt(evt);
			deltaHeight = evt.y-deltaY
			newHeight = workspace_originalHeight+deltaHeight;
			newHeight = snapToGrid(newHeight);
			if(newHeight > 0) {
                div_outline.style.height = (newHeight+"px");
			}
			deltaWidth = evt.x-deltaX
			newWidth = workspace_originalWidth+deltaWidth;
			newWidth = snapToGrid(newWidth);
			if(newWidth > 0) {
				div_outline.style.width = (newWidth+"px");
			}
				
			evt.consume();
						
}
function resizeRelease(evt){
            resizeInProgress = 0; // No more resizing taking place, used to cancel move events
			evt = new Evt(evt);
			setOpacity(box,1);
			Evt.removeEventListener(document,"mousemove",resizeMove,false);
			Evt.removeEventListener(document,"mouseup",resizeRelease,false);
			
			var div_outline = window.document.getElementById("outline");
			div_outline.style.visibility = "hidden";
			box.style.width = div_outline.style.width;
			box.style.height = div_outline.style.height;
			itemNo = workspace_itemName
			resizeInside(itemNo);
			
             bringToFront(box);
             box.style.visibility = "visible";
}

function resizeInside(itemNo){
            var middleDiv = window.document.getElementById("middle"+itemNo);
		    
		    var div_mover = window.document.getElementById("a_mover_"+itemNo);
			var div_resizer = window.document.getElementById("a_resizer_"+itemNo);	
			
			innerHeightDelta = toInteger(div_mover.style.height) + toInteger(div_resizer.style.height);
						
			if (innerHeightDelta > toInteger(box.style.height)){
			    innerHeightDelta = 0
			}
			
			div_resizer.style.width = box.style.width;
			div_mover.style.width = box.style.width;
		    middleDiv.style.width = box.style.width;
            middleDiv.style.height = (toInteger(box.style.height) - innerHeightDelta)+"px";
            // Now we need to resize the various frames...
            // Total height = top "header" div + middle "content" div + bottom "resize" div..
            var workspace_realiframe = window.document.getElementById("realiframe"+itemNo);
            if (workspace_realiframe!=null){
                workspace_realiframe.style.height = middleDiv.style.height;
                workspace_realiframe.style.width = box.style.width;
                
             }
}
		
	function findDiv(node){
		
		var theDiv = node; 
		if(theDiv!=null){
		workspace_itemName = "";
		    while (workspace_itemName =="" && theDiv !=null)	
		    {
		    
				tmp = theDiv.id;
				if(tmp!=null){
			        if (tmp.substring(0,3) !="div")
			        {
			    
				        theDiv = theDiv.parentNode
				
			        } else {
			    
			            workspace_itemName = theDiv.id.substring(3,99);
			        }
			    
		    	} 
		     
		    }
        	
	    }			
	return theDiv;	
	}
		
		function windowLoaded(evt) {
			// prevent IE text selection while dragging!!! Little-known trick!
			document.body.ondrag = function () { return false; };
			document.body.onselectstart = function () { return false; };
		}
		
		function setOpacity(node,val) {
			if (node.filters) {
				try {
					node.filters["alpha"].opacity = val*100;
				} catch (e) { }
			} else if (node.style.opacity) {
				node.style.opacity = val;
			}
		}
		
		function getX(node) {

		retVal = node.style.left;
		retVal = toInteger(retVal);
		return retVal;
		}
		
		function toInteger(number){
		if(number==""){
				number= 0;
			} else {
				number=parseInt(number);
			}
			return number;
		}
		
		function getY(node) {
			retVal = node.style.top;
		    retVal = toInteger(retVal);
		return retVal;
		}
	
		function getWidth(node) {
			return parseInt(node.style.width);
		}
		
		function getHeight(node) {
			return parseInt(node.style.height);
		}
	
		function setX(node,x) {
			x = snapToGrid(x);
			theDiv = findDiv(node);
			theDiv.style.left = x + "px";
		}
	
		function setY(node,y) {
			y = snapToGrid(y);
		theDiv = findDiv(node);
			theDiv.style.top = y + "px";
		}
	
		function Evt(evt) {
			this.evt = evt ? evt : window.event; 
			this.source = evt.target ? evt.target : evt.srcElement;
			this.x = evt.pageX ? evt.pageX : evt.clientX;
			this.y = evt.pageY ? evt.pageY : evt.clientY;
		}
		
		Evt.prototype.toString = function () {
			return "Evt [ x = " + this.x + ", y = " + this.y + " ]";
		};
		
		Evt.prototype.consume = function () {
			if (this.evt.stopPropagation) {
				this.evt.stopPropagation();
				this.evt.preventDefault();
			} else if (this.evt.cancelBubble) {
				this.evt.cancelBubble = true;
				this.evt.returnValue  = false;
			}
		};
		
		Evt.addEventListener = function (target,type,func,bubbles) {
			if (document.addEventListener) {
				target.addEventListener(type,func,bubbles);
			} else if (document.attachEvent) {
				target.attachEvent("on"+type,func,bubbles);
			} else {
				target["on"+type] = func;
			}
		};
	
		Evt.removeEventListener = function (target,type,func,bubbles) {
			if (document.removeEventListener) {
				target.removeEventListener(type,func,bubbles);
			} else if (document.detachEvent) {
				target.detachEvent("on"+type,func,bubbles);
			} else {
				target["on"+type] = null;
			}
		};
	function bringToFront(element){
			workspace_zorderCount = workspace_zorderCount + 1
			element.style.zIndex=workspace_zorderCount;
	}
		function dragPress(evt) {
			if (resizeInProgress!=1){

			evt = new Evt(evt);

			box = findDiv(evt.source);

			bringToFront(box);
			//hideBoxShowWireFrame(box,evt);
			setOpacity(box,.7);
			
			deltaX = evt.x - getX(box);
			deltaY = evt.y - getY(box);
			
			Evt.addEventListener(document,"mousemove",dragMove,false);
			Evt.addEventListener(document,"mouseup",dragRelease,false);
			}
		}
		
		function dragMove(evt) {
			evt = new Evt(evt);
			var div_outline = window.document.getElementById("outline");
			setX(box,evt.x - deltaX);
			setY(box,evt.y - deltaY);
			evt.consume();
		}
		
		function dragRelease(evt) {
			evt = new Evt(evt);
			setOpacity(box,1);
		//	var div_outline = window.document.getElementById("outline");
		//	box.style.top = div_outline.style.top;
			//box.style.left = div_outline.style.left;
			//div_outline.style.visibility = "hidden";
			//box.style.visibility = "visible"; // Hide the box that we are reszing!
			Evt.removeEventListener(document,"mousemove",dragMove,false);
			Evt.removeEventListener(document,"mouseup",dragRelease,false);
			checkDropTarget(evt);
		}
		
		function checkDropTarget(evt){
		    divId = box.id;
		    for (var tabNo = 1; tabNo < 100; tabNo ++){
		        var hotspot = document.getElementById("workspace_tabdroptarget"+tabNo);
		        if(hotspot!=null){
		            testDrop = droppedOnTab(evt,hotspot)
		            if(testDrop!=false){
		                testResult = workspace_moveTab(tabNo, divId);
		                i = 100;
		             }
		        } else {
		            i=100;
		        }
		    }
		}
		function droppedOnTab(evt, hotspot) {
			
			var x = getElementX(hotspot);
			var y = getElementY(hotspot);
			var width = getWidth(hotspot);
			var height = getHeight(hotspot);
			
			return evt.x > x &&
				   evt.y > y &&
				   evt.x < x + width &&
				   evt.y < y + height;
		}




function getElementY(element){
	var targetTop = 0;
	if (element.offsetParent) {
		while (element.offsetParent) {
			targetTop += element.offsetTop;
            element = element.offsetParent;
		}
	} else if (element.y) {
		targetTop += element.y;
    }
	return targetTop;
}

function getElementX(element){
	var targetLeft = 0;
	if (element.offsetParent) {
		while (element.offsetParent) {
			targetLeft += element.offsetLeft;
            element = element.offsetParent;
		}
	} else if (element.x) {
		targetLeft += element.x;
    }
	return targetLeft;
}