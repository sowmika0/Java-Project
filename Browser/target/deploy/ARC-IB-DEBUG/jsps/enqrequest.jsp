<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String routArgs=request.getParameter("routineArgs"); %>
<% String compId=request.getParameter("compId"); %>
<% String usrRole=request.getParameter("usrRole"); %>
<% String enqAction=request.getParameter("enqaction"); %>
<% String enqName=request.getParameter("enqname"); %>
<% String skin=request.getParameter("skin"); %>
<% String server=request.getServerName(); %>
<% int portno=request.getServerPort(); %>
<% String compScreen=request.getParameter("compScreen"); %>
<% String contextRoot=request.getContextPath(); %>
<% String windowName=request.getParameter("windowName"); %>
<% String user=request.getParameter("user"); %>
<% String reqTabid=request.getParameter("reqTabid"); %>
<% String WS_replaceAll=request.getParameter("WS_replaceAll"); %>
<% String WS_parentComposite=request.getParameter("WS_parentComposite"); %>

<html>
<head>
	<link rel="stylesheet" type="text/css" href="../plaf/style/<%= skin %>/general.css">
	<script type="text/javascript" src="../scripts/jsp.js"></script>
	<title>Processing...</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
      <body onLoad="dorequest()">
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
		
		<form name="request" method="POST" action="../servlet/BrowserServlet">
			<input type="hidden" name="requestType" value="OFS.ENQUIRY"/>
			<input type="hidden" name="enqname" value="<%= enqName %>"/>
			<input type="hidden" name="enqaction" value="<%= enqAction %>"/>
			<input type="hidden" name="routineArgs" value="<%= routArgs %>"/>
			<input type="hidden" name="command" value="globusCommand"/>
			<input type="hidden" name="companyId" value="<%= compId %>"/>	
            <input type="hidden" name="userRole" value="<%= usrRole %>"/>  
			<input type="hidden" name="compScreen" value="<%= compScreen %>"/>	
			<input type="hidden" name="windowName" value="<%= windowName %>"/>
			<input type="hidden" name="user" value="<%= user %>"/>
			<input type="hidden" name="reqTabid" value="<%= reqTabid %>"/>
			<input type="hidden" name="WS_replaceAll" value="<%= WS_replaceAll %>"/>
			<input type="hidden" name="WS_parentComposite" value="<%= WS_parentComposite %>"/>
		</form>
		
	</body>
</html>
