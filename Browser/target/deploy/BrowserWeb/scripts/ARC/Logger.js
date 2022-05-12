/** 
 * @fileoverview This file contains the definition of the logging utility class.
 * Logging is based on the FireBug framework; for a list of all avaliable methods, 
 * see: http://www.getfirebug.com/logging.html.
 * To disable logging and ensure that log messages are not avaliable in the code, don't
 * ship the file LogMessages.js.
 * Note that the strings passeed to the log methods are not logged themslves, but are lookups
 * to methods in LogMessages. See existing usage for more details.
 *
 * @author Dave Burford, Alan Greasley
 * @version 0.1
 */

/**
 * Logger object constructor.<br>
 * This method does the same thing as the 'static' method {@link #getLogger}, which should be used instead
 * to keep the intention of the code clear.
 * Logging is based on the FireBug framework; for a list of all avaliable methods, 
 * see: http://www.getfirebug.com/logging.html.
 * To disable logging and ensure that log messages are not avaliable in the code, don't
 * ship the file LogMessages.js.
 * @class 
 * The Logger is, well, a logger ..
 * @param {String} logLevel the log level to use (one of: Logger.DEBUG, Logger.INFO, Logger.WARN, 
 * Logger.ERROR. The default will be used if not specified.
 * @param {String} categoryName the category of the logger
 * @constructor
 * @return A new Logger
 * @type Logger
 * @private
 */
function Logger(logLevel, categoryName) {
    // If logging is enabled, then delegate to console class (of FireBug),
    // otherwise null out the methods 
    if (!Logger.prototype.log) {
        if (this._enabled()) {
            // Delegate to console
            Logger.prototype.log = function(messageId) {
                console.log.apply(this, this._lookup(arguments));
            };

            Logger.prototype.debug = function(messageId) {
                if (this._logIt(Logger.DEBUG)) {
                    console.log.apply(this, this._lookup(arguments));
                }
            };

            Logger.prototype.info = function(messageId) {
                if (this._logIt(Logger.INFO)) {
                    console.info.apply(this, this._lookup(arguments));
                }
            };

            Logger.prototype.warn = function(messageId) {
                if (this._logIt(Logger.WARN)) {
                    console.warn.apply(this, this._lookup(arguments));
                }
            };

            Logger.prototype.error = function(messageId) {
                if (this._logIt(Logger.ERROR)) {
                    console.error.apply(this, this._lookup(arguments));
                }
            };

            Logger.prototype.assert = function(messageId) {
                console.assert.apply(this, this._lookup(arguments));
            };    

            Logger.prototype.count = function() {
                console.count.apply(this, arguments);
            };    

            Logger.prototype.dir = function() {
                console.dir.apply(this, arguments);
            };

            Logger.prototype.dirxml = function() {
                console.dirxml.apply(this, arguments);
            };

            Logger.prototype.trace = function() {
                console.trace.apply(this, arguments);
            };
                
            Logger.prototype.group = function() {                       	    
                if (this._logIt(Logger.INFO)) {
                    console.group.apply(this, this._lookup(arguments));
                }
            };

            Logger.prototype.groupEnd = function() {
                console.groupEnd.apply(this, arguments);
            };

            Logger.prototype.time = function() {    
                console.time.apply(this, this._lookup(arguments));
            };

            Logger.prototype.timeEnd = function() {
            	var args = this._lookup(arguments);
                console.timeEnd.apply(this, args);
                console.debug("    ... elapsed: " + (new Date().getTime() - Logger.startTime) + "ms");
            };

            Logger.prototype.profile = function() {
                console.profile.apply(this, arguments);
            };

            Logger.prototype.profileEnd = function() {
                console.profileEnd.apply(this, arguments);
            };
        }
        else {
            // 'null out' all of the methods
            var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
            "group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];

            for (var i = 0; i < names.length; ++i) {
                Logger.prototype[names[i]] = function() {};       
            }
        }
    }

	if (!logLevel) {
		logLevel = Logger.DEFAULT_LEVEL;
	}
	
	if ( logLevel != Logger.DEBUG && 
		 logLevel != Logger.INFO && 
		 logLevel != Logger.WARN && 
		 logLevel != Logger.ERROR) {		
		throw new Error("Invalid log level passed to getLogger");
	}

    this.logLevel = logLevel;
};

/**
 * Return a logger.
 * See the constructor for more details.
 * 
 * @return a logger
 * @type Logger
 */
Logger.getLogger = function(logLevel, categoryName) {	
	if (!logLevel && !categoryName) {
		return Logger.defaultLogger;
	}
	return new Logger(logLevel, categoryName);
};

// Time the first logger was created (used for elapsed timings)
if(!Logger.startTime) {
	Logger.startTime = new Date().getTime();
}

Logger.resetElapsedTimer = function() {
	Logger.startTime = new Date().getTime();
};

Logger.DEBUG = 1; // Start at 1 so log level is never false
Logger.INFO = 2;
Logger.WARN = 3;
Logger.ERROR = 4;
Logger.DEFAULT_LEVEL = Logger.DEBUG;

Logger.prototype._logIt = function(messageLogLevel) {
    return (this.logLevel <= messageLogLevel);    
};

Logger.prototype._enabled = function() {		
    return (typeof(console)!="undefined" && typeof(LogMessages)!="undefined");
};

Logger.prototype._lookup = function(args) {
	var messageId = args[0];
    	var message;
	if (LogMessages) {
        	message = LogMessages[messageId];
	}
	
	if (!message) {
		message = "ERROR: No log message could be found for key: " + messageId;	
	}
	
    args[0] = message;
    return args;
};

Logger.defaultLogger = new Logger();
