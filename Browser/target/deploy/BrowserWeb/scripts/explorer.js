/** 
 * @fileoverview Deals with the javascript functions used in processing the Globus Explorer 
 *
 * @author XXX
 * @version 0.1 
 */

/**
 * TODO
 */
function doexplorer()
{
	// Display the Globus Explorer in a new window
	var skin = getSkin();
	var contextRoot = getWebServerContextRoot();

	var _explorerURL = location.protocol + "//" + location.host + "/" + contextRoot + "/globusExplorer.jsp?context=" + location.protocol + "//" + location.host;
	var args = "&skin=" + skin;
	
	var _left="100";
	var _top="200";
	var _winDefs= "toolbar=0,status=1,directories=0,scrollbars=1,resizable=1,menubar=no,left=" +_left+ ",top=" + _top + ",width=310,height=360"; 

	var myWin=window.open(_explorerURL + args,"_blank",_winDefs);
}


/**
 * Bring up the GLOBUS Explorer Search window depending on the type supplied
 * @param {TODO} TODO
 */
function doSearch( type )
{
	if ( type == "SELECTIONS" )
	{
		doexplorerSearch();
	}
	else if ( type == "KEYWORDS" )
	{
		doexplorerKeyWord();
	}
	else if ( type == "ALPHA" )
	{
		doexplorerAlpha();
	}
}

/**
 * TODO
 */
function doexplorerAlpha()
{
	buildUtilityRequest(_OS__GET__MENU__EXPLORER_,"ALPHA","generalForm","",_UTILITY__ROUTINE_,"T24 Explorer",180,180,"");
}

/**
 * TODO
 */
function doexplorerKeyWord()
{
	buildUtilityRequest(_OS__GET__MENU__EXPLORER_,"KEYWORD","generalForm","",_UTILITY__ROUTINE_,"T24 Explorer",310,140,"");
}

/**
 * TODO
 */
function doexplorerSearch()
{
	buildUtilityRequest(_OS__GET__MENU__EXPLORER_,"PRODUCT","generalForm","",_UTILITY__ROUTINE_,"T24 Explorer",290,165,"");
}


/**
 * TODO
 */
function explorer()
{

	var args = "GET_PRODUCT_"+window.document.geProduct.Product.value+"_"+window.document.geProduct.Section.value+"_"+window.document.geProduct.Type.value;
	requestType = window.document.geProduct.requestType.value;
	buildUtilityRequest(_OS__GET__GLOBUSEXPLORER_,args,"geProduct","",requestType,"T24 Explorer",600,500,"");

}

/**
 * TODO
 * @param (TODO) cmdname
 */
function explorerAlpha(cmdname)
{	

	var args = "GET_ALPHA_"+cmdname+"_ALL_ALL";
	requestType = window.document.getElementById("requestType").value;
	buildUtilityRequest(_OS__GET__GLOBUSEXPLORER_,args,"geAlpha","",requestType,"Alpha Search",600,500,"");
}


/**
 * TODO
 */
function explorerSearch()
{
	var _keyword = window.document.geKeywordSub.keyword.value;
	var _type = window.document.geKeywordSub.Type.value;
	
	if (_keyword != "" )
	{
	
		var args = "SEARCH_ALPHA_"+ _keyword + _ALL_ + _type;
		requestType = window.document.getElementById("requestType").value;
		buildUtilityRequest(_OS__GET__GLOBUSEXPLORER_,args,"geKeywordMain","",requestType,"Explorer Search",600,500,"");
	
	}
}

/**
 * TODO
 * @param (TODO) fieldName
 */
function initgeKeyword(fieldName)
{
	setWindowStatus();
	
	focusField(fieldName);
}



