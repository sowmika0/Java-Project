<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.temenos.arc.security.filter.*"%>

<%
	String eaiURL = request.getContextPath()+ "/servlet/BrowserServlet";
	//Grab user from session
	String username = (String) session.getAttribute(Constant.SESSION_ATTR_USERNAME);
	
	//If there is no valid username in the session
	if (username == null || username.equals("null")) {
		System.out.println("# [RSAAuthentication.jsp] Redirecting back to logon username/session invalid");
		System.out.println("# [RSANextToken.jsp] No Valid user available");
		session = request.getSession(true);
		if (session != null)
			session.invalidate();
	}	
	String authType = (String) session.getAttribute(Constant.SESSION_ATTR_AUTH_RSA_TYPE);	
	//Debug for session
	if(session!=null){
		System.out.println("[RSAAuthentication.jsp] Current Step : " + session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP));
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta http-equiv="Content-Style-Type" content="text/css">
	<title>RSA Authentication</title>

	<style type="text/css">
			body,p,a,span,div,input,legend,h1,h2,h3,h4,h5,h6,li,dd,dt,th,td{
			font-family:Arial, Helvetica, sans-serif;
			}
			body,p,a,span,div,input,legend,li,dd,dt,th,td{
			font-size:10pt;
			}
			#loginform {
			width:300px;
			margin:auto;
			}
			#loginform fieldset{
			padding:10px;
			}
			#loginform legend{
			font-weight:bold;
			font-size:9pt;
			}
			#loginform label{
			display:block;
			height:2em;
			background-color:#E7E7E7;
			padding:10px 10px 0;
			}
			#loginform input {
			margin-right:20px;
			border:1px solid #999999;
			float:right;
			clear:right;
			background:#CCCCCC;
			}
			#loginform input:focus,#loginform input:hover {
			border:1px solid #333333;
			}
			.error{
			color:red;
			font-weight:bold;
			}
		</style>

	</head>
	<body>
	<div id = "content">
		<!-- Display error message -->
		<%
			String error = (String) request.getAttribute("error") + "";
			if (error != null && !error.equals("null")) {
		%>
		<%=error%>
		<%
		}
		// Token Authentication, Display passcode prompt
		if (authType != null && authType.equals(Constant.SESSION_ATTR_AUTH_RSA_TOKEN)) {
		%>
		<center>
			<div id="error"></div>
			<form id="rsaform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>">
				<fieldset>
					<legend>RSA Token Authentication</legend>
					<p>Please enter your RSA Passcode(PIN + Token)</p>
					<table>
						<tr>
							<td>Username: </td><td><input tabindex="1" readonly="readonly" type="text" name="<%=Constant.SESSION_RSA_USERID%>" value="<%=username%>" /></td>
						</tr>
						<tr>  
							<td>Passcode: </td><td><input tabindex="2" type="password" name="<%=Constant.PARAM_RSA_PASSCODE%>" id="<%=Constant.PARAM_RSA_PASSCODE%>"/> </td>
						</tr>
					</table>
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			</form>
		</center>
		<%
		}
		// SMS (On-Demand) Authentication, Display passcode prompt
		else if (authType != null && authType.equals(Constant.SESSION_ATTR_AUTH_RSA_SMS)) {
			if ( ((String)session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP)).equals(Constant.STEP_RSA_SMS_TOKEN) ){
		%>
		<center>
			<div id="error"></div>
			<form id="rsaform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>">
				<fieldset>
					<legend>RSA SMS Authentication</legend>
					<p>Please enter your passcode from the SMS notification</p>
					<table>
						<tr>
							<td>SMS Passcode: </td><td><input tabindex="1" type="password" name="<%=Constant.PARAM_RSA_SMS_TOKEN%>" id="<%=Constant.PARAM_RSA_SMS_TOKEN%>"/></td>
						</tr>
					</table>
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			</form>
		</center>
		<%
		}
		// SMS (On-Demand) Authentication, Display the PIN prompt
		else {
		%>
		<center>
			<div id="error"></div>
			<form id="rsaform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>">
				<fieldset>
					<legend>RSA SMS Authentication</legend>
					<p>Please enter your RSA PIN</p>
					<table>
						<tr>
							<td>Username:</td><td><input tabindex="1" readonly="readonly" type="text" name="<%=Constant.SESSION_RSA_USERID%>" value="<%=username%>" /></td>
						</tr>
						<tr>
							<td>PIN:</td><td><input tabindex="2" type="password" name="<%=Constant.PARAM_RSA_PIN%>" id="<%=Constant.PARAM_RSA_PIN%>" /></td>
						</tr>
					</table>
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			</form>
		</center>
		<%
			}
		}
		%>
		<form id="rsacancelform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>" style="display:none;">
			<input readonly="readonly" type="text" name="rsacancel" value="yes" />
		</form>
	</div>
	</body>
</html>
