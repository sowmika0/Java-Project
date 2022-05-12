function LogMessages() {
};

LogMessages["TEMP_MSG"] = "%s"; // Used for temporary messages (dont check them in) ...

// Regular log messages
// Fragment
LogMessages["CREATE_FRAGMENT"] = "Creating new Fragment with id: %s";
LogMessages["XISTING_FRAG"] = "Returning existing Fragment with id: %s";
LogMessages["SET_CURRENT"] = "Setting current fragment to: %s";
LogMessages["POP_GOES_THE_URL"] = "Populate fragment: '%s'";
LogMessages["URL"] = "url: %s";
LogMessages["PARAMS"] = "params: %s";
LogMessages["EXCEPTION"] = "An exception was caught: %s";
LogMessages["SET_FRAG_TXT"] = "Populate with: %s";
LogMessages["REM_JS"] = "Removing javascript: from onload string";
LogMessages["REPLACE_FRAG"] = "Replacing all fragments constant with this container fragment";
LogMessages["FRAG_BAD_REQ_STATUS"] = "Status code %s returned for fragment %s";
LogMessages["EVAL"] = "About to eval: %s";
LogMessages["EVAL_ERROR"] = "Error during eval %s";
LogMessages["MULTIPLE_FRAG"] = "Multiple Fragments exist with id: %s";
LogMessages["GOT_ELEMENT"] = "Got HTML element for fragment: %s";
LogMessages["NO_ELEMENT"] = "No HTML element for fragment %s, using a window instead";
LogMessages["GOT_REQ_1"] = "Got XMLHttpRequest request for fragment %s";
LogMessages["GOT_REQ_2"] = "Got %s request (IE) for fragment %s";
LogMessages["GOT_XMLDOC"] = "Got %s XML doc (IE) for fragment: %s ";	
LogMessages["FAIL_TRANSFORM"] = "Failed to transform the XML response for fragment: %s";	
LogMessages["START_INIT_FRAG"] = "Initializing fragments";
LogMessages["PROC_REQ"] = "Processing request for fragment: %s";
// AJAX
LogMessages["NO_AJAX"] = "Ajax is not supported";
LogMessages["AJAX_CREATE_FAIL"] = "Failed to create an XMLHTTP request";
LogMessages["AJAX_SEND"] = "Invoking general AJAX request for URL: %s";
LogMessages["AJAX_SEND_FAIL"] = "Failed to open/send XMLHTTP request: %s";
LogMessages["AJAX_BAD_REQ_STATUS"] = "AJAX request: HTTP Status code %s returned";
LogMessages["AJAX_REPLY_OK"] = "AJAX response received:\n %s";
LogMessages["AJAX_REPLY_EX"] = "AJAX response - exception in handler: %s";

LogMessages["UNKNOWN_LOC"] = "Unknown location: %s";
LogMessages["NO_FORM_1"] = "Could not find form with name: %s_%s";
LogMessages["NO_FORM_2"] = "Could not find form: %s";
LogMessages["WINDOW_NAME"] = "Window name: %s";
LogMessages["FORMS"] = "Forms: %s";
LogMessages["KA_TIMEOUT"] = "Keepalive - keepalive timeout is: %s";
LogMessages["HIT_SER"] = "You hit the server in the last period - no keepalive necessary";
LogMessages["NO_ACTIVITY"] = "No activity .. do nothing %s";
LogMessages["KA_RET"] = "Keepalive function returned";

// Timer messages
LogMessages["LOAD_MAIN"] = ">> Loaded page";
LogMessages["LOAD_CSS"] = " > (loaded CSS)";
LogMessages["LOAD_SCRIPT"] = " > (Loaded scripts)";
LogMessages["INIT_FRAGMENTS"] = ">> Init Fragments";
LogMessages["INJECT_HTML"] = " > (Inject HTML)";
LogMessages["TIME_EVAL"] = " > (Eval JavaScript)";
LogMessages["TIME_PROC_REQ"] = ">> Process Request";
