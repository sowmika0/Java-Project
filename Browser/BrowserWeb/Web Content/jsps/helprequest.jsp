<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.NoSuchElementException" %>
<%@ page import="java.net.InetAddress" %>

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


<% String url=request.getParameter("url"); %>

<% 
   //retrieve the name of the server computer - need this instead of the ip address
   String host = InetAddress.getLocalHost()+"";
   //extract the actual name from the string - it will consist of "name/ipaddress"
   StringTokenizer tokens = new StringTokenizer(host,"/");;
   try{
   	  host = tokens.nextToken();
   }
   catch(NoSuchElementException e){
      e.printStackTrace();
   }
%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <% if(useIntObfuscation && useExtObfuscation) {%>
		<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
		<script type="text/javascript" src="../scripts/obfuscated/all.js"></script>
	<% } else if(useIntObfuscation) {%>
		<script type="text/javascript" src="../scripts/jsp.js"></script>
		<script type="text/javascript" src="../scripts/obfuscated/general.js"></script>
		<script type="text/javascript" src="../scripts/obfuscated/request.js"></script>
	<% } else if(useExtObfuscation) {%>
		<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
		<script type="text/javascript" src="../scripts/all.js"></script>
	<% } else  {%>
		<script type="text/javascript" src="../scripts/jsp.js"></script>
		<script type="text/javascript" src="../scripts/general.js"></script>
		<script type="text/javascript" src="../scripts/request.js"></script>
		<script type="text/javascript" src="../scripts/ARC/T24_constants.js"></script>
	<% } %>
	<title>Processing...</title>
</head>
      <body onLoad="setupHelp('<%= url %>','<%= host %>')">
		<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
			<tr>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td width="33%"></td>
				<td align="middle" bgcolor="#e8ebf2" width="34%" height="75"><P align="center"><b><FONT color="#213a7d">Your 
								request is being processed.</FONT></b></P>
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
