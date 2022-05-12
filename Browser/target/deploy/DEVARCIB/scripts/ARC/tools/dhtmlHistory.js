/** 
   Copyright (c) 2005, Brad Neuberg, bkn3@columbia.edu
   http://codinginparadise.org
   
   Permission is hereby granted, free of charge, to any person obtaining 
   a copy of this software and associated documentation files (the "Software"), 
   to deal in the Software without restriction, including without limitation 
   the rights to use, copy, modify, merge, publish, distribute, sublicense, 
   and/or sell copies of the Software, and to permit persons to whom the 
   Software is furnished to do so, subject to the following conditions:
   
   The above copyright notice and this permission notice shall be 
   included in all copies or substantial portions of the Software.
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
   OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
   IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
   THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/** An object that provides DHTML history, history data, and bookmarking 
    for AJAX applications. */
window.dhtmlHistory = {
   /** Adds a history change listener. Note that
       only one listener is supported at this
       time. */
   /** public */ addListener: function(callback) {
      this.listener = callback;
       if (this.fireOnNewListener == true) {
         this.fireHistoryEvent(this.currentLocation);
         this.fireOnNewListener = false;
      }
   },
   
   /** public */ add: function(newLocation, historyData) {
      // most browsers require that we wait a certain amount of time before changing the
      // location, such as 200 milliseconds; rather than forcing external callers to use
      // window.setTimeout to account for this to prevent bugs, we internally handle this
      // detail by using a 'currentWaitTime' variable and have requests wait in line
      var self = this;
      var addImpl = function() {
         // indicate that the current wait time is now less
         if (self.currentWaitTime > 0)
            self.currentWaitTime = self.currentWaitTime - self.WAIT_TIME;
            
         // remove any leading hash symbols on newLocation
         newLocation = self.removeHash(newLocation);
         
         // IE has a strange bug; if the newLocation
         // is the same as _any_ preexisting id in the
         // document, then the history action gets recorded
         // twice; throw a programmer exception if there is
         // an element with this ID
         var idCheck = document.getElementById(newLocation);
         if (idCheck != undefined || idCheck != null) {
            var message = 
               "Exception: History locations can not have "
               + "the same value as _any_ id's "
               + "that might be in the document, "
               + "due to a bug in Internet "
               + "Explorer; please ask the "
               + "developer to choose a history "
               + "location that does not match "
               + "any HTML id's in this "
               + "document. The following ID "
               + "is already taken and can not "
               + "be a location: " 
               + newLocation;
               
            throw message; 
         }
         
         // store the history data into history storage
         historyStorage.put(newLocation, historyData);
         
         // indicate to the browser to ignore this upcomming 
         // location change
         self.ignoreLocationChange = true;
 
         // indicate to IE that this is an atomic location change
         // block
         this.ieAtomicLocationChange = true;
                 
         // save this as our current location
         self.currentLocation = newLocation;
         
         // change the browser location
         window.location.hash = newLocation;
         
         // change the hidden iframe's location if on IE
         if (self.isInternetExplorer())
            self.iframe.src = "../html/blank.html?" + newLocation;
            
         // end of atomic location change block
         // for IE
         this.ieAtomicLocationChange = false;
      };

      // now execute this add request after waiting a certain amount of time, so as to
      // queue up requests
      window.setTimeout(addImpl, this.currentWaitTime);
   
      // indicate that the next request will have to wait for awhile
      this.currentWaitTime = this.currentWaitTime + this.WAIT_TIME;
   },
   
   /** public */ isInternational: function() {
      return false;
   },
   
   /** public */ getVersion: function() {
      return "0.03";
   },
   
   /** Gets the current hash value that is in the browser's
       location bar, removing leading # symbols if they are present. */
   /** public */ getCurrentLocation: function() {
      var currentLocation = this.removeHash(window.location.hash);
         
      return currentLocation;
   },
   
   
   
   
   
   /** Our current hash location, without the "#" symbol. */
   /** private */ currentLocation: null,
   
   /** Our history change listener. */
   /** private */ listener: null,
   
   /** A hidden IFrame we use in Internet Explorer to detect history
       changes. */
   /** private */ iframe: null,
   
   /** Indicates to the browser whether to ignore location changes. */
   /** private */ ignoreLocationChange: null,
 
   /** The amount of time in milliseconds that we should wait between add requests. 
       Firefox is okay with 200 ms, but Internet Explorer needs 400. */
   /** private */ WAIT_TIME: 200,

   /** The amount of time in milliseconds an add request has to wait in line before being
       run on a window.setTimeout. */
   /** private */ currentWaitTime: 0,
   
   /** A flag that indicates that we should fire a history change event
       when we are ready, i.e. after we are initialized and
       we have a history change listener. This is needed due to 
       an edge case in browsers other than Internet Explorer; if
       you leave a page entirely then return, we must fire this
       as a history change event. Unfortunately, we have lost
       all references to listeners from earlier, because JavaScript
       clears out. */
   /** private */ fireOnNewListener: null,
   
   /** A variable that indicates whether this is the first time
       this page has been loaded. If you go to a web page, leave
       it for another one, and then return, the page's onload
       listener fires again. We need a way to differentiate
       between the first page load and subsequent ones.
       This variable works hand in hand with the pageLoaded
       variable we store into historyStorage.*/
   /** private */ firstLoad: null,
   
   
   /** A variable to handle an important edge case in Internet
       Explorer. In IE, if a user manually types an address into
       their browser's location bar, we must intercept this by
       continiously checking the location bar with an timer 
       interval. However, if we manually change the location
       bar ourselves programmatically, when using our hidden
       iframe, we need to ignore these changes. Unfortunately,
       these changes are not atomic, so we surround them with
       the variable 'ieAtomicLocationChange', that if true,
       means we are programmatically setting the location and
       should ignore this atomic chunked change. */
   /** private */ ieAtomicLocationChange: null,          
   
   /** Creates the DHTML history infrastructure. */
   /** private */ create: function() {
      // get our initial location
      var initialHash = this.getCurrentLocation();
      
      // save this as our current location
      this.currentLocation = initialHash;
      
      // write out a hidden iframe for IE and
      // set the amount of time to wait between add() requests
      if (this.isInternetExplorer()) {
         document.write("<iframe style='border: 0px; width: 1px; "
                               + "height: 1px; position: absolute;"
							   + "left:-1000px; top:-1000px; "
 	                           + "name='DhtmlHistoryFrame' id='DhtmlHistoryFrame' "
                               + "src='../html/blank.html?" + initialHash + "'>"
                               + "</iframe>");
         // wait 400 milliseconds between history
         // updates on IE, versus 200 on Firefox
         this.WAIT_TIME = 400;
      }
      
      // add an unload listener for the page; this is
      // needed for Firefox 1.5+ because this browser caches all
      // dynamic updates to the page, which can break some of our 
      // logic related to testing whether this is the first instance
      // a page has loaded or whether it is being pulled from the cache
      var self = this;
      
      this.ignoreLocationChange = true;
      
      if (this.isInternetExplorer()) {
            this.iframe = document.getElementById("DhtmlHistoryFrame");
      }                                                              

      // other browsers can use a location handler that checks
      // at regular intervals as their primary mechanism;
      // we use it for Internet Explorer as well to handle
      // an important edge case; see checkLocation() for
      // details
      var self = this;
      var locationHandler = function() {
         self.checkLocation();
      };
      setInterval(locationHandler, 100);
   },
   
   /** Notify the listener of new history changes. */
   /** private */ fireHistoryEvent: function(newHash) {
      // extract the value from our history storage for
      // this hash
      var historyData = historyStorage.get(newHash);
      	if (historyData != null && historyData != undefined)
		{
		
      // call our listener      
      this.listener.call(null, newHash, historyData);
      }
   },
   
   /** Sees if the browsers has changed location.  This is the primary history mechanism
       for Firefox. For Internet Explorer, we use this to handle an important edge case:
       if a user manually types in a new hash value into their Internet Explorer location
       bar and press enter, we want to intercept this and notify any history listener. */
   /** private */ checkLocation: function() {
      // ignore any location changes that we made ourselves
      // for browsers other than Internet Explorer
      if (this.isInternetExplorer() == false
         && this.ignoreLocationChange == true) {
         this.ignoreLocationChange = false;
         return;
      }
      
      // if we are dealing with Internet Explorer
      // and we are in the middle of making a location
      // change from an iframe, ignore it
      if (this.isInternetExplorer() == false
          && this.ieAtomicLocationChange == true) {
         return;
      }
      
      // get hash location
      var hash = this.getCurrentLocation();
      
      // see if there has been a change
      if (hash == this.currentLocation)
         return;
         
      // on Internet Explorer, we need to intercept users manually
      // entering locations into the browser; we do this by comparing
      // the browsers location against the iframes location; if they
      // differ, we are dealing with a manual event and need to
      // place it inside our history, otherwise we can return
      this.ieAtomicLocationChange = true;
      
      if (this.isInternetExplorer()
          && this.getIFrameHash() != hash) {
         this.iframe.src = "../html/blank.html?" + hash;
      }
      else if (this.isInternetExplorer()) {
         // the iframe is unchanged
         return;
      }
         
      // save this new location
      this.currentLocation = hash;
      
      this.ieAtomicLocationChange = false;

      // notify listeners of the change
      this.fireHistoryEvent(hash);
   },  

   /** Gets the current location of the hidden IFrames
       that is stored as history. For Internet Explorer. */
   /** private */ getIFrameHash: function() {
      // get the new location
      var historyFrame = document.getElementById("DhtmlHistoryFrame");
      var doc = historyFrame.contentWindow.document;
      var hash = new String(doc.location.search);

      if (hash.length == 1 && hash.charAt(0) == "?")
         hash = "";
      else if (hash.length >= 2 && hash.charAt(0) == "?")
         hash = hash.substring(1); 
    
    
      return hash;
   },          
   
   /** Removes any leading hash that might be on a location. */
   /** private */ removeHash: function(hashValue) {
      if (hashValue == null || hashValue == undefined)
         return null;
      else if (hashValue == "")
         return "";
      else if (hashValue.length == 1 && hashValue.charAt(0) == "#")
         return "";
      else if (hashValue.length > 1 && hashValue.charAt(0) == "#")
         return hashValue.substring(1);
      else
         return hashValue;     
   },          
   
   /** For IE, says when the hidden iframe has finished loading. */
   /** private */ iframeLoaded: function(newLocation) {
      // ignore any location changes that we made ourselves
      if (this.ignoreLocationChange == true) {
         this.ignoreLocationChange = false;
         return;
      }
      
      // get the new location
      var hash = new String(newLocation.search);
      if (hash.length == 1 && hash.charAt(0) == "?")
         hash = "";
      else if (hash.length >= 2 && hash.charAt(0) == "?")
         hash = hash.substring(1);
      
      // move to this location in the browser location bar
      // if we are not dealing with a page load event
      if (this.pageLoadEvent != true) {
         window.location.hash = hash;
      }

      // notify listeners of the change
      this.fireHistoryEvent(hash);
   },
   
   /** Determines if this is Internet Explorer. */
   /** private */ isInternetExplorer: function() {
      var userAgent = navigator.userAgent.toLowerCase();
      if (document.all && userAgent.indexOf('msie')!=-1) {
         return true;
      }
      else {
         return false;
      }
   }
};


/** 
 * This object represents simple storage for history data.
 */    
var historyStorage = new (function(){
	/** Data of the storage.
 	 * @private
 	 */	
	var data = {};
	
	/** Puts new object to the storage.
	 * @param {Object} key the key which identifies the object in the storage 
	 * @param {Object} value the object in the storage 
	 * @public static
	 */
	this.put = function(key, value) {
		data[key] = value;	
	};
	
	/** Checks if the object with particular key is in the storage.
	 * @param {Object} key the key which identifies the object in the storage 
	 * @return true if the object is in the storage, otherwise false
	 * @public static
	 */
	this.hasKey = function(key) {
		return data[key] != null;
	};

	/** Gets the object with particular key from the storage.
	 * @param {Object} key the key which identifies the object in the storage 
	 * @return Object from the storage.
	 * @public static
	 */
	this.get = function(key) {
		return data[key];
	};
})();


window.dhtmlHistory.create();