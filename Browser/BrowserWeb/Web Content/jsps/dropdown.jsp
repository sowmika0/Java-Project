<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String titleName=request.getParameter("title"); %>
<% String routArgs=request.getParameter("routineArgs"); %>
<% String routName=request.getParameter("routineName"); %>
<% String searchCriteria=request.getParameter("searchCriteria"); %>
<% String dropfieldName=request.getParameter("dropfield"); %>
<% String contextArg=request.getParameter("context"); %>
<% String compId=request.getParameter("compId"); %>
<% String usrRole=request.getParameter("usrRole"); %>
<% String skin=request.getParameter("skin"); %>
<% String windowName=request.getParameter("windowName"); %>
<% String parentWin=request.getParameter("parentWin"); %>

<%@ page import="com.temenos.t24browser.utils.PropertyManager" %>
<%@ page import="com.temenos.t24browser.servlets.BrowserServlet" %>
<%@ page import="com.temenos.t24browser.debug.DebugUtils" %>

<%-- Browser parameters are needed to decide if obfuscation is on
	(for ARC-IB deployment) or off (for internal Browser deployment).  --%>
<%
		String realPath = application.getRealPath("");
		PropertyManager ivParameters = new PropertyManager(realPath, 
	    application.getInitParameter(BrowserServlet.BROWSER_PARAMETERS_INIT_PARAM));
		ivParameters.getParameterValue("");
   		String value = ivParameters.getParameterValue(DebugUtils.USE_INT_OBFUSCATION_NAME);
   		boolean useIntObfuscation =  Boolean.valueOf(value).booleanValue();
   		value = ivParameters.getParameterValue(DebugUtils.USE_EXT_OBFUSCATION_NAME);
   		boolean useExtObfuscation = Boolean.valueOf(value).booleanValue();
%>

<html>
<head>
<title><%= titleName %></title>
<META name="GENERATOR" content="IBM WebSphere Studio">
<script language="JavaScript">
function requestList()
{
	// Request dropdown list from server
	document.dropdown.submit();
}
</script>
<head>
      <link rel="stylesheet" type="text/css" href="../plaf/style/<%= skin %>/general.css">
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <% if(useIntObfuscation && useExtObfuscation) {%>
		<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
		<script type="text/javascript" src="../scripts/obfuscated/all.js"></script>
	<% } else if(useIntObfuscation) {%>
		<script type="text/javascript" src="../scripts/obfuscated/general.js"></script>
		<script type="text/javascript" src="../scripts/obfuscated/dropdown.js"></script>
	<% } else if(useExtObfuscation) {%>
		<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
		<script type="text/javascript" src="../scripts/all.js"></script>
	<% } else  {%>
		<script type="text/javascript" src="../scripts/general.js"></script>
		<script type="text/javascript" src="../scripts/dropdown.js"></script>
		<script type="text/javascript" src="../scripts/ARC/T24_constants.js"></script>
	<% } %>
</head>
      <body onLoad="requestList()">

<form name="dropdown" method="POST" action="<%= contextArg %>/servlet/BrowserServlet">
	<input type="hidden" name="requestType" value="UTILITY.ROUTINE"/>
	<input type="hidden" name="routineName" value="<%= routName %>"/>
	<input type="hidden" name="routineArgs" value="<%= routArgs %>_<%= searchCriteria %>_<%= dropfieldName %>"/>
	<input type="hidden" name="command" value="globusCommand"/>
	<input type="hidden" name="companyId" value="<%= compId %>"/>	
    <input type="hidden" name="userRole" value="<%= usrRole %>"/>  
	<input type="hidden" name="windowName" value="<%= windowName %>"/>
	<input type="hidden" name="WS_parentWindow" value="<%= parentWin %>"/>

	
</form>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td width="33%"></td>
			<td align="middle" width="34%" height="75"><P align="center"></P>
				<P align="center">
					<img border="0" src="../plaf/images/default/gears.gif">
				</P>
			</td>
			<td width="33%"></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
</body>
</html>
