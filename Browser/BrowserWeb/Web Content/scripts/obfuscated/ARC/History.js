/** 
 * Class that implements functionality for recording and restoring history.
 */
var BrowserHistory = new (function(){
	/**
     * A history state.
     * @private static
     */
	var state = 1;

	/**
	 * This function register listener for history events. History event happens
	 * when user press back of forward.
     * @public static
	 */

	this.initialize = function(type, dataArray) {
		dhtmlHistory.addListener(handleHistoryChange);
		dataArray.windowname = windowName;
		this.recordHistory(type, dataArray);
	};

	/**
	 *  This function handles history events when back of forward is pressed.
     *  @param {String} newLocation the anchor which is used as name of history 
     *	state
     *  @param historyData data associated with history state
     *  @private static
	 */
	function handleHistoryChange(newLocation, historyData) {
		
		if(newLocation == "" && MenuStyle != "POPDOWN") { // initial state
			var baseURL = window.location.protocol + "//" + location.host + "/" + getWebServerContextRoot();
			window.location = baseURL + "/servlet/BrowserServlet";	//Navigate to login page after #1.
		 	return;
		} else if(historyData == null && MenuStyle != "POPDOWN") { // unknown location
			Logger.getLogger().error("UNKNOWN_LOC", newLocation);		
			return;
		}
		
		var type = historyData.type;
		var dataArray = historyData.dataArray;
				
  		if(type == "menu") { // event initiated by changing tab or active link
  			Backevent = true ;
   			var rowNumber = dataArray.rowNumber;
   			var cellNumber = dataArray.cellNumber;
   			backWindow = dataArray.windowname;
   			TabbedMenu.showCell(rowNumber, cellNumber);
   			Backevent = false;
   		}
  		
  		if(type == "deal"){
   			// Extract the elements that were stored in the history
  			var windowName = dataArray.windowname;
  			var cmd = dataArray.command;
  			// Set the global window name
  			windowName_GLOBAL = windowName;
  			// Run the stored command
  			docommand(cmd);
  			// reset the window name
  			windowName_GLOBAL = "";
  		}
  		
  		if (type == "popmenu"){
   			// Extract the elements that were stored in the history
  			var windowName = dataArray.windowname;
  			// Set the global window name
  			windowName_GLOBAL = windowName;
  			backWindow = dataArray.windowname;
  			
  			var target = dataArray.command;
  			var type = dataArray.type;
  			
  			if (type == "CONTRACT" || type == "NEW")
  			{
  				Backevent = true ;
  				// Run the stored command
  				docommand(target);
  				Backevent = false ;
  			}
  			
  			if (type == "ENQUIRY")
  			{
  				Backevent = true ;
  				doenq(target);
  				Backevent = false ;
  			}
  			// reset the window name
  			windowName_GLOBAL = "";  			
  		}
  			
	}
    
    /**
     * Records new history state.
     * @param {String} type the type of new history state
     * @param dataArray data associated with this state
     * @public static
     */
	this.recordHistory = function(type, dataArray) {
	
		var historyData = {};
		historyData.type = type;
		historyData.dataArray = dataArray;
		dhtmlHistory.add(state, historyData);
    	state++;
	};
	
})(); 