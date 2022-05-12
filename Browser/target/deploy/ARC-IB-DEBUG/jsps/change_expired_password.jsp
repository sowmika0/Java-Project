<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction Signing</title>
</head>
<body>
<div id="content">
	<center>
			<form id="FtressPasswordForm" method="post" target="<%=request.getContextPath()%>/servlet/FtressTransactionServlet" action="<%=request.getContextPath()%>/servlet/FtressTransactionServlet">
				<% 
				 		  
				   String errmsg = (String)session.getAttribute("ERR_MSG");
				    if(errmsg != null && errmsg.equals("1"))
				    	out.println(" Password cannot be empty");
				    if(errmsg != null && errmsg.equals("2"))
				    	out.println(" Authentication Error . Please try again");
				    if(errmsg != null && errmsg.equals("5"))
				    	out.println(" Password successfully changed . Please try with new password");
				    session.removeAttribute("ERR_MSG");
				    
				 %>
				
				
				<fieldset>
				<p>Please enter the Old Password</p>
					<table>
						<tr>
							<td>Password:</td><td><input tabindex="1" type="password" name="OldPassword" id="OldPassword"/></td>
						</tr>
					</table>
                 <p>Please enter the New Password</p>
                     <table>
						<tr>
							<td>Password:</td><td><input tabindex="1" type="password" name="NewPassword" id="NewPassword"/></td>
						</tr>
					</table>
                 <p>Please confirm the New Password</p>
                     <table>
						<tr>
							<td>Password:</td><td><input tabindex="1" type="password" name="ConfirmPassword" id="ConfirmPassword"/></td>
						</tr>
					</table>

					<p>
						<a href="javascript:postForm(window.document.forms['FtressPasswordForm'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
                   		<a href="javascript:cancelForm(window.document.forms['FtressPasswordForm'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			</form>
	</center>
	</div>	
</body>
</html>