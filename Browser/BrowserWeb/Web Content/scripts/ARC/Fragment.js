/** 
 * @fileoverview This file contains the code for the Fragment class.<br>
 * If log4js.js is also included, then logging will be enabled.
 *
 * @author Dave Burford
 * @version 0.1 
 */


/**
 * Fragment object constructor - THIS SHOULD NOT BE CALLED DIRECTLY - see {@link #getFragment}.<br>
 * The term fragment is used to describe a section of an HTML page, whose contents is
 * loaded from an external URL.<br>
 * Each fragment is associated with an HTML element (typically a div or table cell). 
 * It will obtain and 'inject' HTML into this element.<br>
 * This is like an IFrame, but without the use of frames, and allows us to mimic frame
 * sets on a single HTML page.<br>
 * 
 * <p>Fragments are used heavily in the T24 ARC-IB project (for example, versions and 
 * enquiries are rendered as fragments), but this class should remain generic and not
 * contain any T24 specific code.
 *
 * @param {String} id the id of the HTML element associated with this Fragment
 * @return A new Fragment
 * @type Fragment
 * @private
 */
function Fragment(id) {

	// instance attributes
	this.id = id;
	this.printable = false;
	this.autoHoldTimeoutId = null; // capture current interval id
	this.unloadTasks = null; // set to array of onbeforeunload / onunload strings from response html body

	// check for pre-existing fragments of this id, in case constructor is called by mistake
	var frag = Fragment._fragments[id];
	if (frag != null) {
		Fragment._logger.warn("MULTIPLE_FRAG", id);
	}
}


/**
 * The 'static' fragment name stored for the most recent event.
 * @type String
 */
Fragment.currentFragmentName = "";

/**
 * @final
 * @type String
 */
Fragment.NO_AJAX_SUPPORT="Your browser does not support Ajax (which is required to run this application)";

/**
 * @final
 * @type String
 */
Fragment.FRAG_BAD_REQ_STATUS="Bad HTTP status";

/**
 * The array of fragment instances.
 * @private
 * @type Array
 */
Fragment._fragments = new Array();

/**
 * A logger for this class.
 * Defaults to the basic level defined by the Logging utility object
 * @private
 * @type Logger
 */
Fragment._logger = Logger.getLogger(Logger.DEBUG, "Fragment");

/**
* Variable to holt the parent frame target name
* Will be used at the time of clicking back button
*/
Fragment.parentDataAreaFragment = "";

/**
 * 'Static' function used to get a Fragment.<br>
 * If a fragment already exists for this id, it will be returned.
 * Otherwise a new Fragment will be created and returned.<br>
 * Use this method instead of calling the constructor directly.
 * @param {String} id the id of the HTML element associated with this Fragment
 * @return A new Fragment
 * @type Fragment
 */
Fragment.getFragment = function(id) {
	var frag = Fragment._fragments[id];
	
	if (frag == null) {
		Fragment._logger.info("CREATE_FRAGMENT", id);
		frag = new Fragment(id);
		Fragment._fragments[id] = frag;
	}
	else {
		Fragment._logger.debug("XISTING_FRAG", id);
		return frag;
	}
		
	return frag;
};

/**
 * Getter for Fragment name.
 * @return the name of the current Fragment
 * @type String
 */
Fragment.getCurrentFragmentName = function() {
	// fragment name passed in (by event handler at the fragment element level)
	return Fragment.currentFragmentName;
};

/**
 * Return the current Fragment.
 * @return the current Fragment
 * @type String
 */
Fragment.getCurrentFragment = function() {
	return Fragment.getFragment(Fragment.currentFragmentName);
};

/**
 * Static method to set the current fragment
 * @param {String} fragment name relevant to the context of the request.
 */
Fragment.setCurrentFragmentName = function(fragmentName) {
	// fragment name passed in (by event handler at the fragment element level)
	Fragment.currentFragmentName = fragmentName;
	// set the parent fragment target name only when the frametarget is DataAreas since it is the frame to overwrite all responses in a tab.
	
	if (fragmentName.indexOf("DataAreas") != -1 && Fragment.parentDataAreaFragment =='') 
	{
		Fragment.parentDataAreaFragment = fragmentName;
	}	
	Fragment._logger.debug("SET_CURRENT", fragmentName);
};

// TODO: Doc, tests, etc - override getFragment?
Fragment.locateFragment = function(element) {
    var fragment;
    var ancestor = element;

    while (fragment == null && ancestor != null) { 
        ancestor = ancestor.parentNode;
        fragment = ancestor.getAttribute('fragmentName');
    }
    return fragment;
};


/**
 * Returns the id of the HTML element associated with this Fragment.
 * @return the id of the HTML element associated with this Fragment
 * @type String
 */
Fragment.prototype.getId = function() {
	return this.id;
};

/**
 * Check if this fragment should be printed, if so change its class.
 */
Fragment.prototype.checkPrintable = function() {
	if(this.printable) {
		var element = this._getHTMLElement();
		if(element.className.indexOf("printableFragment") == -1) {
			element.className = element.className.replace(
				"printableFragment","notPrintableFragment");
		}
		for(var p = element.parentNode; p && p.nodeName != "body"; p = p.parentNode) {
			if(p.className != null && p.className.indexOf("fragmentContainer") != -1) {
				if(p.className.indexOf("printableFragment") == -1) {
					p.className = p.className.replace(
						"printableFragment","notPrintableFragment");
				} 
			}
		}
	}
};

/**
 * Fragment alternative to form submit - extract params from form,
 * then populate the fragment using the form action and its fields.
 * @param {Form} form the form object from which the action URL and params are obtained
 */
Fragment.prototype.populateWithForm = function(form, nobusy) {
	
	// TODO: Can we get the URL from the form (target or whatever) - we should be able to!
	var params = FragmentUtil.getFormFieldsAsParams(form);

	params += "&WS_FragmentName=" + this.getId();

	this.populateWithURL(form.action, params, nobusy);
};

/**
 * Populate a Help Fragment with a topic matching help file names depoyed with the system.<br>
 * @param {String} topic specifies the help file name (without suffix).
 */
Fragment.prototype.populateWithHelpText = function(topic) {
	this.populateWithString("");
	this.populateWithURL(FragmentUtil.getHelpTextUrl(topic), null, true);
};

/**
 * Get the new Fragment HTML from a URL request.<br>
 * This uses AJAX to perform the request asynchronously. The result is injected into the 
 * HTML element (by setting its innerHTML). If params is not defined, then 
 * this is done via a GET request to the server. If params is defined, then a POST request is 
 * used with the elements of the params array passed in the body as parameters. The format of 
 * params is a string of formal 'name1=value1&name2=value2...' or an array of "name=value" elements.<br>
 * @param {String} URL specifies the base address if params are supplied otherwise the full URL (can be relative).
 * @param {Array} params contains arguments of the form "name=value" to send in a POST request to the specified URL.
 */
Fragment.prototype.populateWithURL = function(URL, params, noBusy, noRetries) {
	Fragment._logger.group("POP_GOES_THE_URL", this.id);
	Fragment._logger.info("URL", URL);
	Fragment._logger.info("PARAMS", params);
	
	// Reset the automatic hold timer if necessary
	if (this.autoHoldTimeoutId != null) {
		clearTimeout(this.autoHoldTimeoutId);
		this.autoHoldTimeoutId = null;	
	}

	// Usually show the busy gif ..
	if (! noBusy) {
		FragmentUtil.showBusy(this);
	}

	if (typeof(params) != "undefined" && (params instanceof Array)) {		
		// Build the param string
		var str="";
		for (var i = 0; i < params.length; i++) {
    		str += params[i] + "&";
		}
	    params = str.substr(0, str.length-1);	   	
	}
	
	var req = Fragment.getHttpRequest();
	
	if (req) {

		// Keep references to fragment obj and Browser request flag for use in the inner method below		
		var fragment=this;	
		var isRequestForT24 = false;
		
		if (URL.indexOf("BrowserServlet") >= 0) {
			isRequestForT24 = true;
		}

		// The callback method is not called for synchronous calls on Firefox
		req.onreadystatechange = function() {																
			if (req.readyState == 4) {
				if ( (req.status == 12030) && (! noRetries) ) {
					fragment.populateWithURL(URL, params, noBusy, true);
				}
				else {
					Fragment._logger.time("TIME_PROC_REQ");
					Fragment._logger.group("PROC_REQ", fragment.id);
					fragment._populateWithRequest(req);
					if (isRequestForT24) {
						FragmentUtil.keepaliveT24Activity();
					}
					Fragment._logger.timeEnd("TIME_PROC_REQ");
					Fragment._logger.groupEnd();
				}
			}
		};
		
		this.req = req;

		if (params == null) {
			try {
				this.req.open('GET', URL, true);		
				this.req.send(null);	
			}
			catch (exception) {
				Fragment._logger.error("AJAX_SEND_FAIL", exception.message);		
				Fragment._logger.trace();				
				showFragmentError(this);
			}
		}
		else {
			try {
				this.req.open('POST', URL, true);
				this.req.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");// set the charset for encoding to work in IE	
				this.req.setRequestHeader("Content-length", params.length);
				this.req.setRequestHeader("Connection", "close");
				this.req.send(params);	
			}
			catch (exception) {
				Fragment._logger.error("AJAX_SEND_FAIL", exception.message);
				showFragmentError(this);
			}
		}
	} else {
		Fragment._logger.error("NO_AJAX");
		Fragment._logger.trace();
		this.populateWithString(Fragment.NO_AJAX_SUPPORT);
	}
	Fragment._logger.groupEnd();
};

/**
 * Populate the fragment, by setting the innerHTML of its associated element with a String.
 * @param {String} content the text to inject into the HTML element associated with this Fragment
 */
Fragment.prototype.populateWithString = function(content) {

	try
	{
		// Replace the text of the HTML
		// Enable this line to see exactly what each fragment is populated with
		// Fragment._logger.debug("SET_FRAG_TXT", content);
		if (content == null) {
			content = "";
		}	
		// if there are unload tasks for this fragment, then eval them now
		// TODO: this mechanism clearly will not handle the case when the window is closed e.g. composite in popup
		this.runUnloadTasks();
	
		this._getHTMLElement().innerHTML = content;
				
	} catch ( e)
	{
		Fragment._logger.error("MISSING HTML ELEMENT - NOT ABLE TO POPULATE INNER_HTML");
	}
};




/**
 * Helper method which populates the fragment with the responseText of the specified request.
 * @param {XMLHttpRequest} req the request whose responseText to use
 * @private
 */
Fragment.prototype._populateWithRequest = function(req) {
	if (req.status == 200 || req.status == 0) { // status 0 required to support file protocol	

        var htmlText;
		var xmlDoc = req.responseXML;
		
		// check the XMLDocument attribute to see if it contains anything ? XML : HTML
		if (xmlDoc && (xmlDoc.childNodes.length > 0)) {
			// FX was OK with just checking that responseXML was not null, but IE is awkward,
			// so we had to check that it wasn't empty too
			
			// It's an XML response, so transform it client-side with the XSL sheet it specifies
			// internally in the xml-stylesheet processing instruction

			htmlText = this._transformXml(xmlDoc);
		}
		else {
			// It's HTML, transformed server-side or straight from a URL fragment
			htmlText = req.responseText;
		}
		
		// remove any HTML header stuff and load the new HTML into the fragment container element
		var extracts = this._extractBody(htmlText);
		this.populateWithString(extracts.htmlText);
		// If the fragment is printable, change its style and style of the parent element
		this.checkPrintable();
		
		// if there was an onload() handler, then run its javascript now
		if (extracts.onloadText != null) {
			try {
				// todo: get rid of this eval
				var timerId = "TIME_EVAL";
				Fragment._logger.time(timerId);	
						
				var evalText = extracts.onloadText;
				var fragId = this.getId();

				var ifIdx = evalText.indexOf("INIT_FRAGMENTS_ALL");
				if (ifIdx >= 0) {
					Fragment._logger.debug("REPLACE_FRAG");
		            		evalText = evalText.replace("INIT_FRAGMENTS_ALL", fragId);
				}
				
				if (evalText != "") {
					Fragment._logger.debug("EVAL", evalText);
					var origCurrentFrag = Fragment.getCurrentFragmentName();
					Fragment.setCurrentFragmentName(fragId);
					eval(evalText);
					Fragment.setCurrentFragmentName(origCurrentFrag);
				}	
				
				Fragment._logger.timeEnd(timerId);	
			}	
			catch (e) {
				Fragment._logger.error("EVAL_ERROR", e.message);
				throw new Error("Error in eval: " + e.message);
			}
		}

		// populate the fragment unloadTasks, if 'onbeforeunload' or 'onunload' handlers, if present
		this.unloadTasks = new Array();
		if (extracts.onbeforeunloadText != null) {
			this.unloadTasks.push(extracts.onbeforeunloadText);
		}				
		if (extracts.onunloadText != null) {
			this.unloadTasks.push(extracts.onunloadText);
		}
		
	}
	else {
		// If a 403 (unauthorized) is returned, then redirect to the error page
		if (req.status == 403) {					
			var baseURL = window.location.protocol + "//" + location.host + "/" + getWebServerContextRoot();
			window.location = baseURL + "/modelbank/unprotected/403.jsp";
		}
		else {
			showFragmentError(this);
			Fragment._logger.error("FRAG_BAD_REQ_STATUS", req.status, this.id);
			throw new Error(Fragment.FRAG_BAD_REQ_STATUS + " : " + content + " [" + req.status + "]");
		}
	}
};



/**
 * Helper method to get the HTML element associated with this Fragment (i.e. with the same id).
 * If the element does not exist, it will use a window with the same name.
 * @return the HTML element or window associated with this Fragment 
 * @type DOM_Element_or_Window
 * @private
 */
Fragment.prototype._getHTMLElement = function() {
	if (this.element == null) {
		this.element = document.getElementById(this.id);

		if (this.element != null) {
			Fragment._logger.debug("GOT_ELEMENT", this.id);
		}
		else {
			// The fragment is not an HTML element on the page; assume it's a window
			Fragment._logger.info("NO_ELEMENT", this.id);
			this.element = window.open('', this.id);
		}
	}
	
	return this.element;
};

/**
 * Helper method which takes a String and returns only the portion of the string between
 * '&lt;body&gt;' and '&lt;/body&gt;' tags. If the string does not contain these tags, it is returned unchanged.
 * @param {String} htmlString the String to process
 * @return the portion of the string surrounded by &lt;body&gt; and &lt;/body&gt; tags
 * @type String
 * @private
 */
Fragment.prototype._extractBody = function(htmlString) {
	if (htmlString == null) { 
		return ""; 
	}	
	htmlString = htmlString.toString();
	
	// todo: use regular expressions!
	var bodyStartIndex = -1;
	
	var bodyTagStartIndex = htmlString.indexOf("<body");
	if (bodyTagStartIndex < 0) {
		bodyTagStartIndex = htmlString.indexOf("<BODY");
	}

	if (bodyTagStartIndex >= 0) {
		bodyStartIndex = htmlString.indexOf(">", bodyTagStartIndex);
	}
	
	var bodyEndIndex = htmlString.indexOf("</body>");
	if (bodyEndIndex < 0) {
		bodyEndIndex = htmlString.indexOf("</BODY");
	}

	if (bodyStartIndex >= 0 && bodyEndIndex > 0) {
		// scan the body element itself for window load/unload events, extract the text (without any javascript: prefix)
		// and return as on<event>Text attribs in the reply object 
		
		var bodyTag = htmlString.substring(bodyTagStartIndex + 5, bodyStartIndex);
		var jsIndex;
		
		// check for onload
		var onloadText = null;
		var onloadIndex = bodyTag.indexOf("onload");
		if (onloadIndex > 0) {
			var onloadStartIndex = bodyTag.indexOf("\"", onloadIndex);
			var onloadEndIndex = bodyTag.indexOf("\"", onloadStartIndex+1);
			
			onloadText = bodyTag.substring(onloadStartIndex+1, onloadEndIndex);
			
			jsIndex = onloadText.indexOf("javascript:");
			if (jsIndex >= 0) {
					Fragment._logger.debug("REM_JS");					
		            onloadText = onloadText.replace("javascript:", "");
			}			
		}

		// check for onbeforeunload
		var onbeforeunloadText = null;
		var onbeforeunloadIndex = bodyTag.indexOf("onbeforeunload");
		if (onbeforeunloadIndex > 0) {
			var onbeforeunloadStartIndex = bodyTag.indexOf("\"", onbeforeunloadIndex);
			var onbeforeunloadEndIndex = bodyTag.indexOf("\"", onbeforeunloadStartIndex+1);
			
			onbeforeunloadText = bodyTag.substring(onbeforeunloadStartIndex+1, onbeforeunloadEndIndex);

			jsIndex = onbeforeunloadText.indexOf("javascript:");
			if (jsIndex >= 0) {
					Fragment._logger.debug("REM_JS");					
		            onbeforeunloadText = onbeforeunloadText.replace("javascript:", "");
			}
		}

		// check for onunload
		var onunloadText = null;
		var onunloadIndex = bodyTag.indexOf("onunload");
		if (onunloadIndex > 0) {
			var onunloadStartIndex = bodyTag.indexOf("\"", onunloadIndex);
			var onunloadEndIndex = bodyTag.indexOf("\"", onunloadStartIndex+1);
			
			onunloadText = bodyTag.substring(onunloadStartIndex+1, onunloadEndIndex);

			jsIndex = onunloadText.indexOf("javascript:");
			if (jsIndex >= 0) {
					Fragment._logger.debug("REM_JS");					
		            onunloadText = onunloadText.replace("javascript:", "");
			}
		}
		
		// Check if the fragment should be printed. 
		var printIndex = bodyTag.indexOf("notPrintable");
		if(printIndex != -1) {
			this.printable = false;
		}
		else if(bodyTag.indexOf("printable"))
		{
			this.printable = true;
		}
		else
		{
			this.printable = false;
		}

		// extract the body content HTML
		htmlString = htmlString.substring(bodyStartIndex+1, bodyEndIndex);
		// Eval any script tags that might be within the Body element as the browser ignores them.
		Fragment.runScriptTags( htmlString);
	}
	
	return {htmlText: htmlString, onloadText: onloadText, onbeforeunloadText: onbeforeunloadText, onunloadText: onunloadText};
};

/**
 * Helper method which takes a String and evals (JavaScript) between
 * '&lt;evalScript&gt;' and '&lt;/evalScript&gt;' tags. If the string does not contain these tags, nothing is run.
 * @param {String} htmlString the String to process
 * @return void
 * @type Static
 * @private
 */
Fragment.runScriptTags = function( htmlString) {

	// TODO: Need to get this to work with RegExp. Temporary fix for a client. Mat Kusari.
	//var re = new RegExp( "startEvalScript(.*?)endEvalScript", "gim");
	//var match = re.exec( htmlString);
	//while( match != null ) 
	//{
	//	alert( match[1]);
	//}
	var startStr = "startEvalScript";
	var endStr = "endEvalScript";
	var startIndex = htmlString.indexOf( startStr);
	var endIndex = htmlString.indexOf( endStr);
	var evalText = "";

	var startPos =1; // start position of <script>
 	var endPos=1;   // start position of </script>
 
 	while(startPos > -1 && endPos > -1)  
 	{
 		// holds first char position  '<' of the string <script>, </script>.
 		startPos = htmlString.indexOf("<script>",endPos);  
 		endPos = htmlString.indexOf("</script>",startPos);  

 		// proceed only if positions are find
 		if (startPos > -1 && endPos > -1)  
 		{
 				// extract contents between <script> and </script>
 				// split by ";" to indentify end of line
 				evalText = (htmlString.substring( startPos+8, endPos-1)).split(";");
 				// if multi-lines are presented
 				for ( var i =0; i<evalText.length;i++)
 				{
 					try
 						{
 							// execute the script 
 							// function prototype will not get processed anymore from <script> pair tags.
 							eval(evalText[i]);
 						}
 					catch(e){}
 				}
 				// start to search next occurance next position to end position of </script>.
 				endPos = endPos+8;
 		}
 	}

	if( startIndex > -1 && endIndex > -1)
	{
		startIndex += startStr.length;
		evalText = htmlString.substring( startIndex, endIndex);
		eval( evalText);
	}
	
};

/**
 * Helper method to execute the unload tasks registered with this fragment
 */
Fragment.prototype.runUnloadTasks = function() {

	var fragId = this.getId();
	var origCurrentFrag = Fragment.getCurrentFragmentName();
	
	// loop through the unloadTasks array, setting the appropriate fragment context to this fragment temporarily
	if (this.unloadTasks != null) {

		try {
			for (var taskIdx = 0; taskIdx < this.unloadTasks.length; taskIdx++) {
				Fragment._logger.debug("EVAL", this.unloadTasks[taskIdx]);
				Fragment.setCurrentFragmentName(fragId);
				eval(this.unloadTasks[taskIdx]);
				Fragment.setCurrentFragmentName(origCurrentFrag);				
			}			
		}	
		catch (e) {
			Fragment._logger.error("EVAL_ERROR", e.message);
			throw new Error("Error in eval: " + e.message);
		}

		// finally clear the tasks to make way for any contained in the new innerHtml below)
		this.unloadTasks = null;
	}		
};

/**
 * Helper method to get an XMLHttpRequest object (used to make AJAX requests).
 * @return a request object
 * @type XMLHttpRequest
 * @private
 */
Fragment.getHttpRequest = function() {
	// todo: could move to an AjaxUtils class
	// todo: Can / should we cache this?	
	var req;

	if (window.XMLHttpRequest) {
		// For anything that supports XmlHttpRequest, including Safari, Firefox and IE7
		try {
			req = new XMLHttpRequest();
			Fragment._logger.debug("GOT_REQ_1", this.id);
		} catch (e) {
			req = null;
		}
	}
	else if (window.ActiveXObject) {
		// For IE 6 or less on Windows - get the standard v3 MSXML object
		// V6 is the latest, but this MSXML version has issues when doing XSL stuff
        try {
            req = new ActiveXObject("MSXML2.XMLHTTP.3.0");
			Fragment._logger.debug("GOT_REQ_2", "MSXML2.XMLHTTP.3.0"	, this.id);
        } catch (oError) {
			req = null;
        }
	}

    if (req == null) {
		Fragment._logger.error("AJAX_CREATE_FAIL");
    	throw new Error("Failed to create an XML HTTP request for " + this.id);
    }
	
	return req;
};

/**
 * Helper method to transform XML responses returned in XMLHttpRequest object.
 * @param {Object} xmlDocObject the XMLDocument reference containing the document to be transformed.
 * This should contain a processing instruction with the required stylesheet url,
 * otherwise window.xsl will be used.
 * @return the transformed HTML
 * @type String
 * @private
 */
Fragment.prototype._transformXml = function(xmlDocObject) {
	// todo: could move with XmlHttpRequest stuff to an XMLUtils class
	// todo: Maybe cache the XSL doms, and the XSLT processor for non-IE?

	var transformedHtml = "";
	// default to this
	var styleSheetUrl = "../transforms/window.xsl";
	// this is what a stylesheet procesing instruction looks like:
	//  <?xml-stylesheet type='text/xsl' href='..//transforms/help/helpMessage.xsl'?>
	var xmlStylesheetPI = "xml-stylesheet";

	// Run through Node list of the XMLDocument we are given and find the stylesheet processing instruction node	
	for (var nodeIdx = 0; nodeIdx < xmlDocObject.childNodes.length; nodeIdx++) {

		var thisNode = xmlDocObject.childNodes[nodeIdx];
		
		if (thisNode.nodeName == xmlStylesheetPI) {
			// this should have been achievable using Node.attributes[]
			// but sadly that is not populated for this node

			var xmlStyleElement = thisNode.nodeValue;
			var startHrefIdx;
			if ((startHrefIdx = xmlStyleElement.indexOf("href=")) > -1) {
				var endHrefIdx = xmlStyleElement.indexOf(".xsl");
				styleSheetUrl = xmlStyleElement.substring(startHrefIdx + 6, endHrefIdx + 4);

				break;
			}
		}
	}

	// watch out for errors in the main XML doc load and transform functions
	try {					
		if (typeof XSLTProcessor != "undefined") {
		
			// This is Mozilla-only ..
			
			// .. so get the XML document object the standard way for the XSL stylesheet
			var xslDoc = document.implementation.createDocument("", "", null);
			
			// load the stylesheet XML synchronously from the URL in the xml-stylesheet PI above
			xslDoc.async = false;
			xslDoc.load(styleSheetUrl);
			
			// create the XSLT processor object and load the DOM containing the XSL sheet
			var xslProcessor = new XSLTProcessor();
			xslProcessor.importStylesheet(xslDoc);
			
			// run the transform - gives you a XMLDocument object which represents the HTML
			var htmlDoc = xslProcessor.transformToDocument(xmlDocObject);
	
			// tried XMLSerializer on (HTML) doc, as per examples, but this does not like iframes
			// simplest method is to transfer the innerHTML to the target element
			transformedHtml = htmlDoc.documentElement.innerHTML;
		}
		else {	// IE
			
			var xslDoc;
			
			// Create an XMLDocument and load the XSL doc
			// In IE7, we get the XmlHttpRequest above, otherwise we use the same MSXML version suffix to get the XML DOM object
			// Note: The MSXML V3.0 seems to match the native XmlHttpRequest as well
			// Tried using V6.0 in IE 7, but that required extra attributes on the DOM e.g. resolveExternals = true, and maybe more
            try {
                var xslDoc = new ActiveXObject("MSXML2.DOMDocument.3.0");
				Fragment._logger.debug("GOT_XMLDOC", "MSXML2.DOMDocument.3.0", this.id);
            } catch (oError) {
				xslDoc = null;
            }
	        if (xslDoc == null) {
	        	throw new Error("MSXML is not installed on your system.");
	        }

            try {
				// load the stylesheet XML synchronously from the URL in the xml-stylesheet PI above
				xslDoc.async = false;
				xslDoc.load(styleSheetUrl);
		
				// do the transformation - simple in IE as transformNode just returns the output String
				transformedHtml = xmlDocObject.transformNode(xslDoc);
            } catch (err) {
				Fragment._logger.error("FAIL_TRANSFORM", err.message);
            }
		}
	}
	catch (error) {
		Fragment._logger.error("FAIL_TRANSFORM", this.id);
		throw new Error("Failed to transform the XML response.");
	}
	
	return transformedHtml;
};


// TODO - refactor this as it is a duplication of some of the Fragment.populateWithUrl code
/**
 * Create and send an AJAX 'GET' request asynchronously.
 * This uses AJAX to perform the request, with the result returned through the supplied callback.
 * @param {String} URL specifies the URL of the request (can be relative).
 * @param {Function} callbackFunc the callback to run, with 1st arg set to the request object.
 */
Fragment.sendRequest = function(URL, params, callbackFunc, noRetries) {

	// IMPORTANT NOTE: if you call any AJAX functions from a popup, which you then close straight
	// away, then the request will lose its context and request params wil be unavailable
	// So, use the window.opener to run the request from the originating window and use a timeout
	// to separate the AJAX request from the call from the child window (see general.js: sendHiddenCommand)

	var req = Fragment.getHttpRequest();
	
	if (req) {

		if (! callbackFunc) {
			callbackFunc = FragmentUtil.genericAjaxHandler;
		}
		
		req.onreadystatechange = function() {
			// If its ready to call the callback function.
			if (req.readyState == 4)
			{
				if ( (req.status == 12030) && (! noRetries) ) {
					Fragment.sendRequest(URL, params, callbackFunc, true);
				}
				else {
					callbackFunc(req);
				}
			}
		};
		
		Fragment._logger.debug("AJAX_SEND", URL, params);

		try {
			if (params) {
				req.open('POST', URL, true);
				req.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8"); // set the charset for encoding to work in IE		
				req.setRequestHeader("Content-length", params.length);
				req.setRequestHeader("Connection", "close");
				req.send(params);
			}
			else {
				req.open('GET', URL, true);		
				req.send(null);	
			}
		}
		catch (exception) {
			Fragment._logger.error("AJAX_SEND_FAIL", exception.message);
		}
	} else {
		Fragment._logger.error("NO_AJAX");
	}
};

