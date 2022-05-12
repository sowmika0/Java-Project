<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String skin=request.getParameter("skin"); %>

<html>
	<script language="JavaScript">
	
	function theEvent()
	{
		// if the svg plugin has not yet been installed then we came here from the enquiry page.
		// once we have installed the plugin we get redirected to this page but cookie is set,
		// so we won't get caught in a loop!
		var svgCookie=getCookie('SVGCheck');		
		if  (svgCookie == 0)
		{
			setCookie('SVGCheck','1','','/');			
			//svgInstallPage is set in svgcheck.js....
			var newWin = window.open(svgInstallPage,'_self');
		}
		else
		{
			// once we've installed the plugin, just close the window...
			// workaround for IE7 bug to prevent window confirmation popup on window close.
			window.open('','_parent','');
			window.close();
		}
	}
</script>
	<head>
		<link rel="stylesheet" type="text/css" href="../plaf/style/<%= skin %>/general.css">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script src="../scripts/charting/svgcheck.js"></script>
		<title>SVG Plugin Installation</title>
	</head>

	<body topmargin="2" leftmargin="2" onload=theEvent()>
			<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
			<tr>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td align="middle" bgcolor="#e8ebf2" width="100%" height="100%"><P align="center"><b><FONT color="#213a7d">Your 
								request is being processed.</FONT></b></P>
					<P align="center">
						<img border="0" src="../plaf/images/default/gears.gif">
					</P>
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
	</body>
</html>
