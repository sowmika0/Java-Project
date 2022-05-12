/** 
 * @fileoverview This file contains general utilities.
 *
 * @author Dave Burford, Alan Greasley
 * @version 0.1
 */


/**
 *  @singleton
 * 'Singleton' object for general Fragment utilities.
 */
FragmentUtil = {

    FRAGMENT_CONTAINER_CLASS  : "fragmentContainer",

    FRAGMENT_TAG  : "td",

    // public 'statics' for keepalive
    keepaliveGraceSeconds  : 20, // Allow 20 seconds (see W3C WCAG 2.0, 2.2.1) to refresh session on inactivity
    minSessionTimeout  : 60000,  // Restrict user timeout to no less than 1 minute, otherwise the mechanism will not work properly
    userSessionTimeout  : 1800000,  // default to standard web session timeout - can be updated by initKeepalive()
    keepaliveTimeout : null,  // the delay between keepalive checks
    keepaliveTimeoutId : null,  // start off with no timeout
    userActivity  : false, // true -> user mouse/keyboard activity -> send keepalive if no other T24 requests

    // Note that {user_id} is replaced with the actual user name on the server
    userTimeoutParams  : "command=globusCommand&user={user_id}&requestType=OFS.ENQUIRY&enqname=BROWSER.TIMEOUT&enqaction=RUN&fieldName:1:1:1=@ID&operand:1:1:1=EQ&value:1:1:1={user_id}",

    autoHoldInterval  : 30000,  // Set to 30 seconds
    _logger  : Logger.getLogger(Logger.DEBUG, "FragmentUtils"),

    // TODO: Rename, add logging and tests
    // Submit a form with the specified id ...
    // TODO: Add this method to the form gotten below!
    submitForm  : function(form, windowRef) {
        // We need to add the fragment name so that it is avaliable to the server 
        // (todo: remove when this is passed back in the T24->servlet xml)

        var actualForm; // holds the actual form object
        
        if (typeof form == "string") {
            actualForm = FragmentUtil.getForm(form, windowRef);
        }
        else {
            actualForm = form;
        }	
        
        if (!top.noFrames || (typeof DOC_DOWN_SERVICE != "undefined" && DOC_DOWN_SERVICE)) {
            actualForm.submit();
        }
        else {
            var target = actualForm.target;
            if (target == "") {
                target = Fragment.getCurrentFragmentName();
            }
            if (target != null && target != "") {
                // Create the new fragment instance
                var frag = Fragment.getFragment(target);
                var fragElem = frag._getHTMLElement();
        
                if (FragmentUtil.isWindow(fragElem) || (fragElem.tagName.toLowerCase() == "iframe")) {
                    actualForm.submit();
                }
                else {
                    // Squirt in the contents in lieu of the form.submit()
                    frag.populateWithForm(actualForm);
                }
            }
            else {
                // The target may still be empty (e.g. in a drop-down where we do not have a fragment)
                if (target == "") {
                    // We do not want a fragment - just submit the form and it will open in the same window
                    actualForm.submit();			
                }
            }
        }
    },

    /**
     * Override for a specific case of submitDealForm (in Deal.js)
     * @param {Object form} the Form object from which the request fields are taken
     * @param {String func} the original function being carried out by the form submission e.g. "HLD"
     */
    submitDealForm  : function(form, func) {
        // 
        if (func == "HLDAUTO") {

            // The behaviour of the hold / commit interaction is such that it is not necessary to track
            // field changes such as in checkChangedFields() in Deal.js - if the final commit occurs after
            // a hold record has been created then all the fields will be re-validated anyway

            // post now requires window name (AG p.p. SR)
            form.windowName.value = FragmentUtil.getWindowOrFragmentName();
            var params = FragmentUtil.getFormFieldsAsParams(form);

            Fragment.sendRequest(form.action, params, null);
            
            // TODO - DO WE NEED THIS NOW ?
            // Clear the unlock flag now request has gone to server
            form.unlock.value = "";	
        }
        else {
            // Otherwise, submit the deal (appreq) form as normal
            submitDealForm();
        }
    },

    // TODO - this can be replaced by the more general getElement()
    /**
     * Get the form reference, within the fragment 'namespace' for the given id
     * @param {String formId} the Form name
     * @param {String windowRef} (Optional) the window object, if the required form object is in another window
     * @param {String fragmentName} (Optional) the fragment name, if the required fragment is not the current fragment
     * @return the form reference, constrained by window / fragment
     * @type Object
     */
    getForm  : function(formId, windowRef, fragmentName) {

        // The form name has been suffixed with _<fragmentName>
        var formRef;

        if (!windowRef) {
            windowRef = window;
        }
        
        // Get the current fragment name from the current window usually, but use the target context if supplied
        if (!fragmentName && windowRef.Fragment) {
            fragmentName = windowRef.Fragment.getCurrentFragmentName();
            //To avoid forming the form name using the ID dropdown div as the fragment name
            if((fragmentName!="null") && (fragmentName.substr(0,10) == "fieldName:") && (fragmentName.substr(fragmentName.length-4,fragmentName.length)==":div"))
			{
				fragmentName = currentFragment_GLOBAL;
			}
        }

        if (windowRef.top.noFrames) {	
            formRef = windowRef.document.getElementById(formId + "_" + fragmentName);
            
            if (formRef == null) {
                FragmentUtil._logger.warn("NO_FORM_1",formId, fragmentName);
            }		
        }
        
        if (formRef == null) {
        	if(formId != "" && formId != null)
        	{
            	formRef = windowRef.document.getElementById(formId);
            }
            else
            {
            	FragmentUtil._logger.warn("NO_FORM_1",formId);
            }
        }
        
        if (formRef == null) {
            FragmentUtil._logger.warn("NO_FORM_2", formId);
            FragmentUtil._logger.debug("WINDOW_NAME", windowRef.name);
                
            var forms = "";
            var nForms = windowRef.document.forms.length;
            for (var i = 0; i < nForms; i++) {
                forms += windowRef.document.forms[i].getAttribute("id");
                if (i < nForms-1) {
                    forms += ", ";
                }
            }
            FragmentUtil._logger.debug("FORMS", forms);				
        }
        if(formId != null)
        {
    	  if (formRef == null && formId.match('~') != null)
	  	  {
	  		var tempFormName = formId.split("~");
	  		
	  		var tabFrame = document.getElementById("tabFrame_"+tempFormName[1]);
	  		var tabForm = tabFrame.getElementsByTagName("form");
	  		for (formIndex = 0; formIndex < tabForm.length; formIndex++)
	  		{
	  			var test = tabForm[formIndex].id;
	  			var test2 = tempFormName[2];
	  			if (tabForm[formIndex].id == test2)
	  			{
	  				var formRef = tabForm[formIndex];
	  				//return formRef;
	  			}	  			
	  			
	  		}
	  	  }
        }
        return formRef;	
    },

    /**
     * Get an element object reference, within the fragment 'namespace', for the given id
     * @param {String baseElemId} the element id
     * @param {String windowRef} (Optional) the window object, if the required form object is in another window
     * @param {String fragmentName} (Optional) the fragment name, if the required fragment is not the current fragment
     * @return the element reference, constrained by window / fragment
     * @type Object
     */
    getElement  : function(baseElemId, windowRef, fragmentName) {

        var elemRef;

        if (!windowRef) {
            windowRef = window;
        }
        
        // Get the current fragment name from the current window usually, but use the target context if supplied
        if (!fragmentName && windowRef.Fragment) {
            fragmentName = windowRef.Fragment.getCurrentFragmentName();
        }

        if (top.noFrames) {	
            elemRef = windowRef.document.getElementById(baseElemId + "_" + fragmentName);		
            // move to elemRef = windowRef.document.getElementById(fragmentName + ":" + baseElemId);		
        }
        
        if (elemRef == null) {
            elemRef = windowRef.document.getElementById(baseElemId);
        }

        return elemRef;
    },

    /**
     * Populate an SVG graph / chart, based on the data embedded in the corresponding element
     */
    populateGraphOrChart  : function(frag) {

        // get the embedded HTML data element (not displayed of course) for the appropriate image type
        // 2 types in enqresponse .. graph or pie .. only one per enqresponse (acc. to XSL choose)
        // Also, handles init function for svgObject (in graphEnquiry.xls)
        var dataElem, dataValues;

        dataElem = FragmentUtil.getElement("graphData", window, frag);
        if (dataElem) {
            dataValues = dataElem.firstChild.nodeValue;		

            var dataPoints = dataValues.split("|");
            
            for (idx = 0; idx < dataPoints.length; idx++) {
                var chartVals = dataPoints[idx].split(",");
                // Always called this with Repress value as true in orig XSL
                // (cf. pie below, although for pie, the value seems to be ignored anyway)
                addChartValue(chartVals[0], chartVals[1], chartVals[2], true);
                if (chartVals[3]) {
                    addDrill(chartVals[3]);
                }
            }
            // Call graph.js Finish function to do final work
            finish();
        }
//	Not implementing for "mpPieData" as this has been superseded by svgEnqResponse.xsl
//  - code below was in enqresponse.xsl
//		 		    function mpPopulate() {
//		             <!-- Add in a function call per xml tag(mpPie or mpSlice) -->
//				         <xsl:for-each select="mpPie/mpSlice">
//					        <xsl:choose>
//						        <xsl:when test="position() != last()">window.mpAddChartValue(<xsl:value-of select="mpAmount"/>, "<xsl:value-of 		select="mpLabel"/>",false, "<xsl:value-of select="mpLevel"/>");</xsl:when>
//						        <xsl:otherwise>window.mpAddChartValue(<xsl:value-of select="mpAmount"/>, "<xsl:value-of select="mpLabel"/>",true, "<xsl:value-of 		select="mpLevel"/>");</xsl:otherwise>
//					        </xsl:choose>
//				         </xsl:for-each>
//			     	}
        else {
            dataElem = FragmentUtil.getElement("pieData", window, frag);
            if (dataElem) {
                dataValues = dataElem.firstChild.nodeValue;

                var dataPoints = dataValues.split("|");
                
                for (idx = 0; idx < dataPoints.length; idx++) {
                    var chartVals = dataPoints[idx].split(",");
                    if (idx == (dataPoints.length - 1)) {
                        addChartValue(chartVals[0], chartVals[1], false);
                    }
                    else {
                        //comma is not used since isNaN() will take only number format 
                        chartVals[0]= replaceAll(chartVals[0], "!", "");
                        addChartValue(chartVals[0], chartVals[1], true);
                    }
                    if (chartVals[2]) {
                        addDrill(chartVals[2]);
                    }
                }
            }
        }
    },


    /**
     * Check if composite screen in the Fragment world, else use the general (frames) version.<br>
     * @return true if we are in a composite screen scenario
     * @type boolean
     */
    isCompositeScreen  : function() {

        var frameCount = 0;

        if (top.noFrames) {
            var fragmentObjects = FragmentUtil.getElementsByClassName(FragmentUtil.FRAGMENT_CONTAINER_CLASS, FragmentUtil.FRAGMENT_TAG);
            frameCount = fragmentObjects.length;
        }
        else {	// Use a modified version of the original frames checker (formerly in general.js)
            
            // Test window.parent i.e. the frameset window
           for ( var i = 0; i < window.parent.frames.length; i++ )                          
		   {                                                                                
                 var frame = window.parent.frames[i];
                 // to overcome the permission denied error while lauching external url.
				 try {
				 	 if (frame.frameElement.tagName.toLowerCase() != "iframe")   
                 	 {                                                                            
                     	 return true;                                                            
                 	 }    
				 }
				 catch (exception) {
                 	FragmentUtil._logger.error("EXTERNAL_URL", exception.message);
            
				 }                                                                   
		    } 

        }
                
        // If we have at least one frame or fragment element, we are in a composite screen
        if (frameCount > 0) {
            return true;
        }
        else {
            return false;
        }
        
    },

    /**
     * Get the name of the current composite screen<br>
     * @return the name of the composite screen if we're in a composite screen scenario
     * @type String
     */
    getCompositeScreenName  : function() {
        var compScreen;
        
        if (top.noFrames) {
            // We need to specify the general form, as this value is present but not set on some forms
            compScreen = getFormFieldValue("generalForm", "compScreen");
        }
        else {
            // Use the basic frames version
            compScreen = getFieldValue("compScreen");
        }
        return compScreen;
    },

    /**
     * Get the targets for the current composite screen<br>
     * @return the name of the composite screen if we're in a composite screen scenario
     * @type String
     */
    getCompositeScreenTargets  : function() {
        var compTargets;
        
        if (top.noFrames) {
            // We need to specify the general form, as this value is present but not set on some forms
            if (isFirefox() && currentFragment_GLOBAL != "")
			{
			// to get the exact form name in  fire fox. 
				var fragmentform = "generalForm" +"_"+ currentFragment_GLOBAL;
				compTargets = getFormFieldValue(fragmentform, "compTargets");
			}
			else 
			{
				compTargets = getFormFieldValue("generalForm", "compTargets");
			}
        }
        else {
            // Use the basic frames version
            compTargets = getFieldValue( "compTargets" );
        }
        return compTargets;
    },

    /**
     * Get the 'current' window name (if in frames mode) or fragment name (if not in frames mode)<br>
     * @return the name of the window or fragment
     * @type String
     */
    getWindowOrFragmentName  : function() {
        var name;
        
        // try to set to current Fragment name
        if (top.noFrames) {
            name = Fragment.getCurrentFragmentName();
        }

        // if in frames, or no current fragment (popup window) ..
        if (! name) {
            name = window.name;
        }

        return name;
    },

    /**
     * Check if composite screen in the Fragment world, else use the general (frames) version.<br>
     * @return true if we are in a composite screen scenario
     * @type boolean
     */
    getFrameWindow  : function(target) {
        if (top.noFrames) {
            // TODO: Do this properly, as for frames ..
            return window;
        }
        else {
            // use the standard method of getting the window object of a frame
            return window.open( "", target, "" );
        }
    },

    /**
     * Inject the standard busy HTML content into the fragment.<br>
     */
    showBusy  : function(fragment) {
    	var busyHtml = "";
    	var singleLoadingIcon = document.getElementById("singleLoadingIcon");    	
    	if (singleLoadingIcon && singleLoadingIcon.value == "yes")
    	{    		
    		busyHtml = '<div id="arcloading' + '-' + loadingIconType +'"/>';  	        
   			loadingIconType = "normal";
    	}
    	else
    	{
        	var skin = '';
			var skinobj = ''; 
			var skinpath ='';
			var skinobj = document.getElementById("skin");
			if (skinobj)
			{
				skin = skinobj.value;
			}
			// Load the particular skin if exists
			if (skin)
			{
				skinpath = "../plaf/images/"+skin+"/gears.gif";
			}
			else
			{
				skinpath = "../plaf/images/default/gears.gif";
			}
	        busyHtml = '<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%"><tr><td></td><td></td><td></td></tr><tr><td width="33%"></td><td align="middle" width="34%" height="75"><P align="center"></P><P align="center"><img alt="Loading" title="Loading" border="0" src="'+skinpath+'"/></P></td><td width="33%"></td></tr><tr><td></td><td></td><td></td></tr></table>';	        
	     }
	     fragment.populateWithString(busyHtml);
    },

    isVisible  : function(tabObject) {

        // just return false if display is 'none' otherwise true
        if (tabObject.display == "none") {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * Function which shows or hides an object.
     * When the object is not being shown, it will not play a part in the layout of the document.
     * @param {String or HTML element} obj an HTML element, or its id
     * @param {Boolean} show if true, the element will be shown, otherwise it will be hidden
     * @param {String} moveDirection either "right" or "left" - currently unused
     */
    setDisplay  : function(obj, show) {
        
        // Used for the deal 'menu' (errors/overrides), now in a table cell alongside the deal 'header'
        // Also used in FragmentUtil.showState below, again in deal presentation
        if (typeof obj == "string") {
            obj = FragmentUtil.getElement(obj);
        }

        // Switch between display: none	and block - no need for visibility toggle
        // as display:none will disappear the object - deal.css matches up with this
        if (obj != null) {
            if (show) {
                obj.style.display="block";
            }
            else {
                obj.style.display="none";		
            }
        }
    },

    /**
     * Wrapper for the show state functionality.
     */
    showState : function(state) {

        // for noFrames, the busy screen is shown when the request is posted
        // .. otherwise ..
        if (!top.noFrames) {
            if ( state == "busy" ) {
                FragmentUtil.setDisplay( "goButton", false );
                FragmentUtil.setDisplay( "toolbars", false );
                FragmentUtil.setDisplay( "processingPage", true );
            }
            else {
                FragmentUtil.setDisplay( "goButton", true );
                FragmentUtil.setDisplay( "toolbars", true );
                FragmentUtil.setDisplay( "processingPage", false );
            }
        }
    },

    /**
     * Find a style sheet.
     * @param The selector for the rule to find
     * @type StyleSheet
     */	
     findStyle : function(selector) {
        var targetrule=null;
        for (j=0; j<document.styleSheets.length && targetrule==null; j++) {
            var mysheet=document.styleSheets[j];
            var myrules=mysheet.cssRules? mysheet.cssRules: mysheet.rules;
            for (i=0; i<myrules.length; i++){
                // For media rule (@media) do nothing (if myrules[i].selectorText == null)  
                if(myrules[i].selectorText && myrules[i].selectorText.toLowerCase()==selector.toLowerCase()){
                    targetrule=myrules[i];
                    break;
                }
            }
        }
        return targetrule;
    },

                                


    /**
     * Return a string containing key-value pairs of all of the controls in the specified form.<br>
     * The returned String, with field values encoded, is in a format suitable for an HTML form post (e.g. key1=value1&key2=value2).
     * @param form <FORM> the form whose values to return
     * @return the key value pairs of the specified form, '&' seperated
     * @type String
     */
    getFormFieldsAsParams : function(form) {

        var fields = new String();

        // Loop round all the fields in the specified form
        var elems = form.elements;
        for (i = 0; i < elems.length; i++) 
        {
            // todo: could be more efficient (String addition)
            var curElem = elems[i]; 
            
            // Get name and value
            var curName = curElem.name;
            var curValue = curElem.value;
            
            // need to check for combos as IE (duh) will not give us the value directly
            if (curElem.tagName.toLowerCase() == "select") {
                if (curValue == "") {
                    curValue = curElem.options[curElem.selectedIndex].text;
                }
            }
                    
            //encodeURIComponent in field name as well as value because the field name may contain special charecter like "="
			//which needs to be encoded while passing the parameters in the format of "fieldname = value" in URL
            fields +=encodeURIComponent(curName) + "=" + encodeURIComponent(curValue) + "&";
        }
        
        return fields.substr(0, fields.length - 1);
    },


    /**
     * Wrapper function to provide the same functionality as genrequest.jsp.
     * Returns false if the actual call to genrequest should not be done.
     * 
     * @param paramString the string being passed to genrequest.jsp
     */
    doGenRequest : function(target, html) {
        var doGenRequest = true;
        
        if (top.noFrames) {
            var index = html.indexOf("?");
            var paramString = html.substring(index+1);
            
            paramString += "&requestType=UTILITY.ROUTINE";
            paramString += "&command=globusCommand";		
            paramString += "&fragmentName=" + target;		

            var frag = Fragment.getFragment(target);			
            frag.populateWithURL("BrowserServlet", paramString);
            doGenRequest = false;
        }
        
        return doGenRequest;
    },

    /**
     * Crude but cross browser method to test if the specified object is a Window object.
     * @return true if the specified object is a Window object
     * @type Boolean
     */
    isWindow : function(obj) {
        // Crude implementation - wont work if obj is not a window but has close and resizeBy methods
        // Note that instanceof, typeof, win.prototype.type, win.constructor were all tried, but none
        // would work in all browsers
        return (obj.close != null && obj.resizeBy != null);
    },

    /**
     * Wrapper function to get the parent window name
     */
    getParentWindow : function() {
        if (top.noFrames) {
            return window.opener;
        }
        else {
            // Check if the parent window name is stored on the form (as supplied from the web server)
            var parentWin;
            var parentName = getFieldValue( "WS_parentWindow" );
        
            if ( ( parentName == "" ) || ( parentName == "undefined" ) )
            {
                // We did not save a name, so try if this is a child window
                var opener = window.opener;
        
                if ( opener )
                {
                    // A child window
                    parentWin = opener;
                }
            }
            else
            {
                // By opening the parent window we can get the parent window object
                parentWin = window.open("", parentName, "" );
            }	
            
            return( parentWin );
        }
    },

    /**
     * Get the parent FragmentName, if it has been set in a form's WS_parentWindow field
     */
    getParentFragmentName : function(windowRef) {
        var parentFragmentName;
        
        if (top.noFrames) {
            parentFragmentName = windowRef.Fragment.getCurrentFragmentName();
        }
        else {
            parentFragmentName = getFieldValue( "WS_parentWindow" );
        }
        
        return parentFragmentName;
    },

    /**
     * Send off the fragment requests stored in the COS definition
     * Second and Third arguments are used for keepalive, only in the main (outer) COS - see window.xsl
     */
    initFragments : function(parentFragment, timeout, nobusy) {
        var timerId = "INIT_FRAGMENTS";
        FragmentUtil._logger.group("START_INIT_FRAG");
        FragmentUtil._logger.time(timerId);	
        
        var fragmentObjects;

        if (parentFragment == "INIT_FRAGMENTS_ALL") {
            fragmentObjects = FragmentUtil.getElementsByClassName(FragmentUtil.FRAGMENT_CONTAINER_CLASS, FragmentUtil.FRAGMENT_TAG);
            // if configured, initialize the keepalive interval timer for the main page
            if (top.keepaliveHandling) {
                if (timeout > 0) {			
                    // Convert the user session timeout to number of minutes properly (timeout * 60000  milliseconds)
					FragmentUtil.userSessionTimeout = timeout * FragmentUtil.minSessionTimeout;	
                }

                // Now kick off the keepalive initialisation (will use default if timeout is 0)
                FragmentUtil.initKeepalive();
            }
        }
        else {
            // for relative searches, all frags constant above is replaced by containing fragment id
            // When subsequent ajax request is sent before the previous request is processed completely,the fragment doesn't exist in the document.So just return. 
            if (document.getElementById(parentFragment) == null)
            {
            	return;
            }
            fragmentObjects = FragmentUtil.getElementsByClassName(FragmentUtil.FRAGMENT_CONTAINER_CLASS, FragmentUtil.FRAGMENT_TAG, document.getElementById(parentFragment));
        }


        for (var fragIdx = 0, fragLen = fragmentObjects.length; fragIdx < fragLen; fragIdx++) {
            
            var frag = Fragment.getFragment(fragmentObjects[fragIdx].getAttribute('id'));
            var fragUrl = fragmentObjects[fragIdx].getAttribute('fragmenturl');
            if (fragUrl) {
                 // Identify if it is firefox request and send the firefox parameter so that UTF-8 characters can be decoded properly
            	 if(isFirefox())
                 {
                 var isQ = fragUrl.indexOf("?",1);
					if(isQ != -1)
					{
                     fragUrl = fragUrl+"&"+"isFirefox=true";
                 }
                 else
					{
						fragUrl = fragUrl+"?&"+"isFirefox=true";
					}
		
                 }
                frag.populateWithURL(fragUrl, null, nobusy);
            }
        }
        
        FragmentUtil._logger.timeEnd(timerId);
        FragmentUtil._logger.groupEnd();	
    },


    /**
     * Returns an array of elements that contain the given class name.
     * @param {String} className The name of the class to select on.
     * @param {String} [tagName] Optional filter to restrict the search to certain element types. Defaults to '*'.
     * @param {String} [contElem] Optional filter to restrict search to a certain container element. Defaults to 'document'.
     * @return {element[]} An array of elements that contain the given className as one of it's classes.
     */
    getElementsByClassName : function(className, tagName, contElem) {
      
        // init args
        tagName = tagName || "*";
        contElem = contElem || document;
          
        // get element array
        var elemList = (tagName == '*' && document.all) ? document.all : contElem.getElementsByTagName(tagName);
          
        // return array
        var foundElems = new Array();

        for (var elemIdx = 0, elemLen = elemList.length; elemIdx < elemLen; elemIdx++) {
        
            var elemClasses = elemList[elemIdx].className.split(' ');
        
            for (var classIdx = 0, classLen = elemClasses.length; classIdx < classLen; classIdx++) {
                if (className == elemClasses[classIdx]) {
                    foundElems.push(elemList[elemIdx]);
                }
            }
        }
        return foundElems;
    },

    /**
     * Very simple wrapper to enforce noframes behaviour when command tries to open COS in new window
     */
    checkCommandPrefix : function(commandString, pattern) {
        // This is only ever used in checking the COS target for a COS command prefix,
        // so always return false for 'Fragment' mode so COS screens are displayed in the main screen
        // (if there is a suitable target)
        if (top.noFrames) {
            return false;
        }
        else {  // in frames mode, check the command prefix for the given pattern
            return (commandString == pattern);
        }
    },

    /**
     * Very simple wrapper for commandline util to check/save window changes
     */
    checkWindowChanges : function(cmdname) {
        // Do not save the window changes if we are in no frames mode - even if it is set up to
        // (as it cannot find the form)
        if (top.noFrames) {
            return true;
        }
        
        // if in frames, call the real thing ..
        return checkWindowChanges(cmdname);
    },

        

    /**
     * @todo
     */
    getHelpTextUrl : function(topic) {
        var contextRoot = getWebServerContextRoot();
        var helpServletUrl = location.protocol + "//" + location.host + "/" + contextRoot + "/servlet/HelpServlet";
        var language = getLanguage();
        var user = getUser();
        var commandArgs = "command=view&product=ARC&language=" + language + "&user=" + user + "&application=" + topic;
        return helpServletUrl + "?" + commandArgs;
    },

    /**
     * TODO: THIS IS A REAL TEMP JOB  --  delete when we have proper menu/toolbar in a Fragment
     */
    signOff : function() {

        // We need to borrow general form in another fragment to complete the log off, so ..
        
        // Get all the forms in the window to find a proxy general form
        var nForms = document.forms.length;
        var proxyFormId;
        var proxyFragmentName;
        
        // Loop round the forms, get the general Form, and get the fragment name from it
        for (var formIdx = 0; formIdx < nForms; formIdx++) {
            proxyFormId = document.forms[formIdx].getAttribute("id");
            if (proxyFormId.indexOf("generalForm") == 0) {
                proxyFragmentName = proxyFormId.substring(12);
                break;
            }
        }

        Fragment.setCurrentFragmentName(proxyFragmentName);

        // Now carry on with the real signOff in menu.js
        signOff();
    },

    // TODO: Document, check logging and add tests
    mousedownHandler : function(theEvent, fragmentName) {

		// save the current fragment name when click on any frame in cos.
        currentFragment_GLOBAL = fragmentName;
            
		if (theEvent.type == "mousedown")
        {
              currentEvent_Global = "Mouse";
        }
		               
        if (top.keepaliveHandling) {
            FragmentUtil.keepaliveUserActivity(); 
        }

        // for fragment event handling, stop the event from propagating upwards (to outer fragments)
        if ((fragmentName != null) && (fragmentName != "")) {
            FragmentUtil.killEvent(theEvent);
            Fragment.setCurrentFragmentName(fragmentName);
        }
        
         // when only recurrence frequency window is opened.
		if (freqDropDown_GLOBAL == currentDropDown_GLOBAL || freqDropDown_GLOBAL && !currentDropDown_GLOBAL)
          {
               var dropFieldId = freqDropDown_GLOBAL + ":div";
               var thisField = theEvent.target || theEvent.srcElement;
            // consider this field's id else take parent's id.
               var dropParentNode = thisField.id;
               var found = 0;
			if(dropParentNode == dropFieldId || dropParentNode == "calendar_popup")
				{
					found = 1;
				}
			else
				{
			  while (thisField.parentNode != null)
				  {
					dropParentNode=thisField.parentNode;
					if(dropParentNode.id == dropFieldId || dropParentNode.id == "calendar_popup")
					  {
						 found = 1;
						 break;
					  } 
					thisField = thisField.parentNode;
				  }
				}
	 	// when event occurs out of recurrence frequency window
		    if (found == 0  )
		       {
				// Call to hide calendar or frequency if any when an event occured outside ot these window.
				calendarFrequencyHide(theEvent);
		       }
          }
       else
       	  {
				var EnqactionDiv = "";
				EnqactionDiv = window.document.getElementById("EnquiryactionDiv");
				if (EnqactionDiv != null)
				{
					var currentFocus = "";
					if(theEvent.srcElement) {
						if(theEvent.srcElement.offsetParent != null && theEvent.srcElement.offsetParent != undefined)
						{
							currentFocus = theEvent.srcElement.offsetParent.offsetParent;
						}
					} else {
						currentFocus = theEvent.target.offsetParent;
					}					
					if (currentFocus != EnqactionDiv)
					{
						var divParent = EnqactionDiv.parentNode;
						toolsMenuLeave(theEvent, divParent);
						currentDropDown_GLOBAL = "";
					}
				} else {
					calendarFrequencyHide(theEvent);
				}
			}  	       
        
        },
    //TODO: Handles the right mouse button event
         rightButtonMouseHandler : function(theEvent, fragmentName) {
                
                 var browser = navigator.appName.substring ( 0, 9 );
                 var event_number = 0;
                  if (browser=="Microsoft")
                     {
                        event_number = theEvent.button;
                     }
                   else if (browser=="Netscape")
                         {
                         event_number = theEvent.which;
                        }
                       if ( event_number==2 || event_number==3 )
                             {              
                         trap_page_mouse_key_events ();
                                        
                             }
                             else
                           {
                       FragmentUtil.mousedownHandler(theEvent,fragmentName);
                         }                              
                },

    // TODO: Document, check logging and add tests
    keydownHandler : function(theEvent, fragmentName) {

		if (theEvent.type == "keydown")
        {
               currentEvent_Global = "Keybord";
        }
        if (top.keepaliveHandling) {
            FragmentUtil.keepaliveUserActivity(); 
        }

        // for fragment event handling, stop the event from propagating upwards (to outer fragments)
        if ((fragmentName != null) && (fragmentName != "")) {
            FragmentUtil.killEvent(theEvent);
            Fragment.setCurrentFragmentName(fragmentName);
        }
         // when we press the escape key the calendar,dropdown,frequency fragment has to hide.
        if (theEvent.keyCode == 27)
		{
			hidePopupDropDown(currentDropDown_GLOBAL);
			hidePopupDropDown(freqDropDown_GLOBAL);
			var EnqactionDiv = window.document.getElementById("EnquiryactionDiv");
			if (EnqactionDiv != null)
			{
				var divParent = EnqactionDiv.parentNode; 
				toolsMenuLeave(theEvent, divParent);	
			}
			// Set the focus to the field specified in the XML
			if (top.noFrames) 
			{
           	 	var dealTitle = window.document.getElementById("dealtitle");
				if(dealTitle != null )
			 	{
					dealTitle.focus();
			 	}
        	}
        	else
        	{
				initSetFocus();
			}
			var enqHeadermsg = window.document.getElementById("enqheader-msg");
			if (enqHeadermsg != null)
			{
				enqHeadermsg.focus();
			}
		}

    },

    // TODO: Document, check logging and add tests
    killEvent : function(theEvent) {
        if (theEvent.stopPropagation) { //FX
            theEvent.stopPropagation();
        }
        else { // IE
            theEvent.cancelBubble = true;
        }
    },

    /**
     * Set up the initial keepalive parameters, and kick off the timer task to do the keepalive processing
     */
    initKeepalive : function() { 

        // By now we should have established the correct timeout, based on the smaller of
        //  - the web session timeout, set by default in the web.xml
        // .. failing that, in the default web.xml
        //    (which is retrieved from initial login request / response)
        //  - the T24 user timeout (got from the USER TIME.OUT.MINUTES field in the 
        //    initial AJAX BROWSER.TIMEOUT enquiry request kicked off by initFragments())

        // apply the minimum timeout
        if (FragmentUtil.userSessionTimeout < FragmentUtil.minSessionTimeout) {
            FragmentUtil.userSessionTimeout = FragmentUtil.minSessionTimeout;
        }
        
        // Set the keepalive timeout so a server hit will always fall _well_ within the overall timeout
        // i.e. assuming you have some user activity at the end of the keepalive timeout, then you should have
        //      a reasonable grace period (see top) to make a T24 request and keep the session alive
        FragmentUtil.keepaliveTimeout = FragmentUtil.userSessionTimeout - (FragmentUtil.keepaliveGraceSeconds * 1000);

        FragmentUtil.keepaliveTimeoutId = window.setTimeout(FragmentUtil.checkKeepalive, FragmentUtil.keepaliveTimeout);
    },

    /**
     * Trigger a potential keepalive update with each mouse/key press
     */
    keepaliveUserActivity : function() { 

        Fragment._logger.debug("Keepalive - user activity detected");
        FragmentUtil.userActivity = true;

    },

    /**
     * Trigger a potential keepalive update with each mouse/key press
     */
    keepaliveT24Activity : function() { 
        FragmentUtil._logger.debug("KA_TIMEOUT", FragmentUtil.keepaliveTimeout);	

        FragmentUtil.userActivity = false;
        
        if (FragmentUtil.keepaliveTimeoutId) {
            clearTimeout(FragmentUtil.keepaliveTimeoutId);
            FragmentUtil.keepaliveTimeoutId = null;
        }

        // Check to make sure the timeout has been set ..
        if (FragmentUtil.keepaliveTimeout) {
            FragmentUtil.keepaliveTimeoutId = window.setTimeout(FragmentUtil.checkKeepalive, FragmentUtil.keepaliveTimeout);
        }
    },

    /**
     * Keepalive timeout function to assess whether a keepalive request is necessary, and if so, send one
     * using the static Fragment request function i.e. does not update any fragment contents
     */
    checkKeepalive : function() { 

        // Keep a note of whether there was activity in the last cycle to avoid unenecessary T24 requests
        FragmentUtil.keepaliveTimeoutId = null;
        
       if (! FragmentUtil.userActivity) {
            var startTime=new Date();
            // Call the setSessionExpiryMsg function and assign the SessionExpiryMessage to the variable msg
            var msg = setSessionExpiryMsg(FragmentUtil.keepaliveGraceSeconds);
            //Display confirm box only if useKeepSessionalive is set as "YES"
			if (top.useconfirm)
       		{
            	FragmentUtil.userActivity = confirm(msg);
            	var endTime=new Date();
                var interval = (endTime - startTime);
            	if (interval > (FragmentUtil.keepaliveGraceSeconds * 1000)) {
                // User wanted to keep the session alive - but was too slow ...
                FragmentUtil.userActivity = false;
           		}
           	 }
			else
			{
				FragmentUtil.userActivity = false;
			}            
            if (!FragmentUtil.userActivity) {
                // User has cancelled - redirect to the login page
                var baseURL = window.location.protocol + "//" + location.host + "/" + getWebServerContextRoot()+ "/modelbank/unprotected/timeout.jsp";
                window.location = baseURL;
            }	
        }

        if (FragmentUtil.userActivity) {
            // user activity within the last checking interval, or confirmation from user to keep alive
            FragmentUtil.userActivity = false;

            // i.e. no T24 comms since since last check .. so run keepalive func on T24 and throw away the result
            Fragment.sendRequest("BrowserServlet", FragmentUtil.userTimeoutParams, FragmentUtil.ajaxKeepaliveHandler);
        }
        
    },


    /**
     * Basic keepalive AJAX request handler - records T24 activity, as does a normal T24 request {@see Fragment#populateWithURL(URL, params)}
     * @param {Object req} the AJAX request object
     */
    ajaxKeepaliveHandler : function(req) {
        if (req.readyState == 4) {
            // If this does not make it in time, for some reason, then we will probably get a SECURITY VIOLATION
            FragmentUtil._logger.debug("KA_RET");
            FragmentUtil.keepaliveT24Activity();
        }
    },

    /**
     * Field change handler
     * - for auto hold, flag an update to the Deal, so that the transaction can be saved at the next cycle
     */
    formChangeHandler : function () {
        if (top.autoHoldDeals) {
            var thisFrag = Fragment.getCurrentFragment();

            // For the current fragment, if there is no timer currently running, then start one and store the id
            // the auto hold request will happen when the timeout expires, unless we have reloaded the fragment
            // e.g. if the user commits the deal, for instance
            if (thisFrag.autoHoldTimeoutId == null) {

                thisFrag.autoHoldTimeoutId = setTimeout(FragmentUtil.doAutoHold, FragmentUtil.autoHoldInterval);

            }
        }
    },
    
    /**
     * Field change handler
     * Required for versions displaying the same field in 2 associated versions. 
     * Update all fields on the same form that have the same id.
     * @param {event} the onChange event, used to get the element that fired the event.
     * @return {void}
     */
    fieldChangeHandler : function ( event) {
    	
    	var field = event.target || event.srcElement;
    	
    	var parentForms = getFormContainingField(field);
    	if (parentForms == null) {
    		return;
    	}
    	var parentFormId = parentForms.getAttribute("id");
    	if (parentFormId == null) {
    		return; // Element is not on a form, so nothing to update.
    	}
    	
    	var fieldList = getFormFields( parentFormId, field.name);
    	
    	for( var i = 0; i < fieldList.length; i++)
    	{
    		fieldList[i].value = field.value;
    	}
    },

    /**
     * Interval timer function to assess whether a hold request is necessary, and if so, send one
     * using the static Fragment request function i.e. does not update any fragment contents
     */
    doAutoHold : function() { 

        // this will only be running if we have had changes in the deal since opening the initial transaction
        // or since the last hold request
        // do Deal has been augmented to run the replacement 'submitDealForm'

        doDeal("HLDAUTO");

        Fragment.getCurrentFragment().autoHoldTimeoutId = null;
    },


    /**
     * Basic AJAX request handler using AJAX general request dispatcher {@see Fragment#sendRequest(URL, params, callbackFunc)}
     * This is designed to generically capture the response from 'one-way' requests, mainly, and simply log
     * some basic info to show it worked / failed
     * @param {Object req} the AJAX request object
     */
    genericAjaxHandler : function(req) {

        try {
            if (req.status == 200 || req.status == 0) { // Hurrah, it worked
                FragmentUtil._logger.debug("AJAX_REPLY_OK", req.statusText);
            }
            else {
                FragmentUtil._logger.error("AJAX_BAD_REQ_STATUS", req.status);
            }
        }
        catch (exception) {
            FragmentUtil._logger.error("AJAX_REPLY_EX", exception.message);
        }
    }

};


////////////////////////////////////////////////////////////////////////
// Bootstrap behaviour
// To avoid errors if the top.opener is not available or not accessible
if (!top.noFrames) {
	if (top.opener && (top.opener !== top)) {
		w = top.opener;
		try{
		top.noFrames = w.noFrames;
		top.refreshHandling = w.refreshHandling;
		top.keepaliveHandling = w.keepaliveHandling;
		top.autoHoldDeals = w.autoHoldDeals;
		}catch(e){}
	}
}
