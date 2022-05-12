/** 
 * @fileoverview TODO
 *
 */


/**
 * Returns the context root part of the web server directory structure
 * @return {String} The contextRoot
 */ 
function getWebServerContextRoot()
{
	// Determine the web server context root by extracting the 1st word in the pathname string
	// However, due to the possibility of adding several nests e.g.
	// "/webSealRoot/SecureApps/BrowserWeb/servlet/BrowserServlet" extracting the 1st word will not
	// return a valid ContextRoot.
	// We construct the BrowserWeb application path and extract everything before it to give
	// a valid contextRoot.
	
	// NB Any changes to this function should be reflected in the same function in workflow.js
	
	var path = location.pathname;
	
	//we want to check for OS dependant path separators	
	var OsSeparator = "/";	
	var contextStart = path.indexOf( OsSeparator, 0 );
	
	if (contextStart == -1)
	{
		OsSeparator = "\\";
	}
		
	contextStart = path.indexOf( OsSeparator, 0 );	
	
	// Construct the web app. name - check if the request came from the HelpText portal or the main BrowserServlet
	var browserWebApp = "";
	var contextEnd = 0;
	
	if ( path.indexOf("portal-help-index.xml") != -1 )
	{
		contextEnd = path.lastIndexOf("/help/portal-help-index.xml");
	}
	else if ( path.indexOf("portal-help-menu.xml") != -1 )
	{
		contextEnd = path.lastIndexOf("/help/portal-help-menu.xml");
	}
	else if ( path.indexOf("/jsps/") != -1 )
	{
		contextEnd = path.lastIndexOf("/jsps/");
	}
	else
	{
		contextEnd = path.lastIndexOf("/servlet/");
	}
	
	// Now we have the start & end pos, extract
	var contextRoot = path.substring( contextStart + 1, contextEnd );
	
	return( contextRoot );
}

/**
 * Launches continue.jsp.
 * @param {String} skin The skin to use
 * @param {String} compId
 * @param {String} user The userId
 * @param {String} cfwstage
 * @param {String} windowName
 * @return {void}
 */ 
function continueWindow(skin,compId,user,cfwstage,windowName)
{	
	var contextRoot = getWebServerContextRoot();
	var quickURL = location.protocol + "//" + location.host + "/" + contextRoot + "/jsps/continue.jsp";
	var args = "&skin=" + skin;
	args = args + "&compId=" + compId;
	args = args + "&user=" + user;
	args = args + "&cfwstage=" + cfwstage;
	args = args + "&windowName=" + windowName;
	var fullURL = quickURL + "?" + args;
	var myWin=window.open(fullURL, windowName,"");
}

/**
 * Calls 'continueWindow' but with all parameters defaulted to the current pages values.
 * @return {void}
 */ 
function continueFromT24()
{

	var skin = getFormFieldValue("generalForm","skin");
	var compId = getFormFieldValue("generalForm","companyId");
	var user = getFormFieldValue("generalForm","user");
	var cflowstage = getFormFieldValue("generalForm","nextStage");
	if((cflowstage!='') && (cflowstage!=undefined))
		continueWindow(skin,compId,user,cflowstage,"_parent");
}

/**
 * Temporary method, might be possible to delete.
 * @return {void}
 */ 
function continueFromStatic()
{
	alert(window.top.document.forms["generalForm"].skin.value);
}