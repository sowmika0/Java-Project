/**
 * Class TabbedMenu manages tabbed menu.
 */
var TabbedMenu = new (function(){
	/**
	 * Number of the active tab.
	 * @private static
	 */
	var activeTabNumber = 0;

	/**
	 * Array of the active command links.
	 * @private static
	 */
	var activeCommandLinks = [];
	
	/**
	 * Shows actual command group (tab) whis has been activated.
	 * 
	 * @param {int} rowNumber Number of active tab
	 * @public static
	 */
	this.showTab = function(rowNumber) {	
		// Reset the elapsed timer for logging
		Logger.resetElapsedTimer();
	
	  	var dataArray = {};
	  	dataArray.rowNumber = rowNumber;
	  	dataArray.cellNumber = activeCommandLinks[rowNumber];
	  	if(!dataArray.cellNumber) {
	  		dataArray.cellNumber = 0;
	  	}
	  	TabbedMenu.showCell(rowNumber);
	  	if(window.BrowserHistory)
	  	{
	  		// store the dataArray into  historydata Array with window name for the active tab in  tabbed menu style.
			dataArray.windowname = windowName;
	  		BrowserHistory.recordHistory("menu", dataArray);
	  	}
	};
	
	/**
	  * Highlights actual command link and un-highlights last invoked 
	  * command link.
	  * 
	  * @param {int} position Position of active command 
	  * command.
      * @public static
	  */
	this.showActiveCommandLink = function(position){
	  	var dataArray = {};
	  	dataArray.rowNumber = activeTabNumber;
	  	dataArray.cellNumber = position;
		showLink(position);
	  	if(window.BrowserHistory)
		{
		// store the dataArray into  historydata Array with window name for the active menu inside the active tab in  tabbed menu style.
			dataArray.windowname = windowName;
			BrowserHistory.recordHistory("menu", dataArray);	
		}	  	
	};
	  
	/**
	 * Invokes and highlights initial command.
	 * 
	 * This function is invoked once when a page with menu is loaded. It 
	 * is presently placed in the window.xsl, onload attribute.
	 * @public static
	 */
	this.invokeFirstTask = function(){
	  	activeCommandLinks[0] = 0; // initial active position for first tab
		showLink(0);
		var menuTable = FragmentUtil.getElement('menu-table');
		if (menuTable && menuTable.rows && menuTable.rows[0].cells) {
			autoRunMenuCommand(menuTable.rows[0].cells[0]);
		}
	  	if(window.BrowserHistory){
			var dataArray = {};
		  	dataArray.rowNumber = 0;
		  	dataArray.cellNumber = 0;
			// Initialise & Record the history 
			BrowserHistory.initialize("menu", dataArray);
	  	}
	};
	
	/**
	 * Actually invokes the command.
	 * 
	 * Gets the link text by navigating to the <a> from the parent cell and evals it
	 * @private static
	 */
	function autoRunMenuCommand(parentElem){
	
		var a = parentElem.getElementsByTagName('a')[0];
		if(a.attributes.getNamedItem("onclick") != null)
			{
				var clickref = a.attributes.getNamedItem("onclick").value;
				if(clickref.indexOf("javascript:") != -1)
					{
						var f = clickref.replace('javascript:','');
					}
			}
		else
			{
				var f = a.href.replace('javascript:','');
			}
	
		eval(decodeURI(f)); // invokes command  
	}
	
	/**
	 * Shows particular tab and link.
	 * @param {int} rowNumber position of the tab
	 * @param {int} cellNumber position of the link
	 * @public static
	 */
	this.showCell = function(rowNumber, cellNumber) {
		if (rowNumber == undefined)
			{
				rowNumber = 0;
			}
		var navMenuBlock = FragmentUtil.getElement('nav-menu');
		if(navMenuBlock != null) {  
			var navMenu = navMenuBlock.getElementsByTagName("li");
			var oldTab = navMenu[activeTabNumber];
			var activeTab = navMenu[rowNumber];
			oldTab.className = 'nonactive-tab';
		  	activeTab.className = 'active-tab';
		}
		var menuTable = FragmentUtil.getElement('menu-table');
		var oldRow = menuTable.rows[activeTabNumber];
		var activeRow = menuTable.rows[rowNumber];
		activeTabNumber = rowNumber;
	  	oldRow.className = 'hidden-row';
	  	activeRow.className = 'visible-row';
	
		/* Show last active command in the row */
		var activePosition;
		if(cellNumber == null) { 
	  		if(activeCommandLinks[rowNumber] == null) {
		  		activeCommandLinks[rowNumber] = 0;
		  	}
	  		activePosition = activeCommandLinks[rowNumber];
	  	} else {
	  		activePosition = cellNumber;
	  	}
		showLink(activePosition);
	
		var td = activeRow.cells[activePosition * 2];
		autoRunMenuCommand(td);
	};
	
	/** 
	 * Highlightes command (link) from active tab. 
	 * @param {int} activeTabNumber position of the command
	 * @private static
	 */
	function showLink(position) {
	  	var oldPosition = activeCommandLinks[activeTabNumber];
		var actualRow = FragmentUtil.getElement('menu-table').rows[activeTabNumber];
	  	var oldLink = actualRow.cells[oldPosition * 2];
	  	var newLink = actualRow.cells[position * 2];
		
		// Get the name of the link, and pass it to the event framework
		var itemName = newLink.getElementsByTagName("a")[0].firstChild.nodeValue;		
		FragmentEvent.raiseEvent(FragmentEvent.TAB_SELECTED, itemName, Fragment.getCurrentFragment());

		oldLink.className = 'nonactive-command';
		newLink.className = 'active-command';
		activeCommandLinks[activeTabNumber] = position; // new position
	}
})();
