<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>

<% String routArgs=request.getParameter("routineArgs"); %>
<% String routName=request.getParameter("routineName"); %>
<% String compId=request.getParameter("compId"); %>
<% String usrRole=request.getParameter("usrRole"); %>
<% String skin=request.getParameter("skin"); %>
<% String server=request.getServerName(); %>
<% int portno=request.getServerPort(); %>
<% String compScreen=request.getParameter("compScreen"); %>
<% String contextRoot=request.getContextPath(); %>
<% String windowName=request.getParameter("windowName"); %>
<% String user=request.getParameter("user"); %>
<% String command=request.getParameter("command"); %>

<html>
<head>
      <link rel="stylesheet" type="text/css" href="../../../plaf/style/<%= skin %>/general.css">
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<script src="../../../scripts/commandline.js"></script>
	<script src="../../../scripts/menu.js"></script> <!-- Needed by commandlines! -->
	<script src="../../../scripts/general.js"></script> <!-- Needed by menu -->
	<script src="../../../scripts/request.js"></script> <!-- Needed by menu -->
	<script> function cmdrequestonload(){
	//alert(window.name);
		//if (window.name=""){
				window.name = getWindowName();
			//	alert("test");
		//}

//alert(window.name);
	
		var compTargets = document.getElementById("compTargets");
		compTargets.value = "ALL_" + window.name + "|";
	
			docommand('<%= command %>');
	
	}
	
	</script>
	<title>Processing...</title>
</head>


      <body onLoad="cmdrequestonload()">
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
						<img border="0" src="../../../plaf/images/default/gears.gif">
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
	<input type="hidden" name="requestType" value="UTILITY.ROUTINE"/>
	<input type="hidden" name="routineName" value="<%= routName %>"/>
	<input type="hidden" name="routineArgs" value="<%= routArgs %>"/>
	<input type="hidden" name="command" value="globusCommand"/>
	<input type="hidden" name="companyId" value="<%= compId %>"/>	
	<input type="hidden" name="userRole" value="<%= usrRole %>"/>
	<input type="hidden" name="compScreen" value="<%= compScreen %>"/>	
	<input type="hidden" name="windowName" value="<%= windowName %>"/>
	<input type="hidden" name="user" value="<%= user %>"/>
</form>
		<form name="generalForm" id="generalForm" method="POST" action="../../../servlet/BrowserServlet">
			<input type="hidden" name="requestType" />
			<input type="hidden" name="routineName" />
			<input type="hidden" name="routineArgs" />
			<input type="hidden" name="application" />
				
			
			<input type="hidden" name="ofsOperation" />
			<input type="hidden" name="ofsFunction" />
			<input type="hidden" name="ofsMessage" />
			<input type="hidden" name="version" />
				
		
			<input type="hidden" name="transactionId" />
			<input type="hidden" name="command" value="globusCommand" />
			<input type="hidden" name="operation" />
			<input type="hidden" name="windowName" value="" />
			<input type="hidden" name="apiArgument" value="" />
			<input type="hidden" name="name" value="" />
			<input type="hidden" name="enqname" value="" />
			<input type="hidden" name="enqaction" value="" />
			<input type="hidden" name="dropfield" />
				
			
			<input type="hidden" name="previousEnqs" value="" />
			<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value="" />
			<input type="hidden" name="unlock" value="" />
			<input type="hidden" id="savechanges" />
			<!-- Process User Details -->
			<input type="hidden" name = "compScreen" id="compScreen" />
			<input type="hidden" name = "compTargets" id="compTargets" />
		</form>
</body>
</html>
