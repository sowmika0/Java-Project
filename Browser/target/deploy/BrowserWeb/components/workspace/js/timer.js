function Timer(){

var timerID = 0;
var tStart  = null;
var sLastDisplay = "";


	
	this.start = function (interval, obj) {
	this.interval = interval;
	this.obj = obj;
	tStart   = new Date();
	  
/* Call the function that will return a reference to the inner function
   object created in its execution context. Passing the parameters that
   the inner function will use when it is eventually executed as
   arguments to the outer function. The returned reference to the inner
   function object is assigned to a local variable:-
*/

	var functRef = callLater(this.obj);
/* Call the setTimeout function, passing the reference to the inner
   function assigned to the - functRef - variable as the first argument:-
*/
	timerID=setTimeout(functRef, this.interval);

	}
	
	this.stopTimer =function () {
	   if(timerID) {
	      clearTimeout(timerID);
	      timerID  = 0;
	   }
	
	   tStart = null;
	}
	this.ping = function(){
		this.obj.ping;
	}
	function callLater(obj){
	    /* Return a reference to an anonymous inner function created
	       with a function expression:-
	    */
	    return (function(){
	        /* This inner function is to be executed with - setTimeout
	           - and when it is executed it can read, and act upon, the
	           parameters passed to the outer function:-
	        */
			obj.ping();
	    });
}
	
	
}

