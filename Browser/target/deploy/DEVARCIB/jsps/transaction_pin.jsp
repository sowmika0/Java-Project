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
			<form id="FtressPasswordForm" method="post" target="<%=request.getContextPath()%>/servlet/FtressTransactionOtpServlet">
				<% 
				   String Challenge = (String)session.getAttribute("CHALLENGE"); 
				    String errmsg = (String)session.getAttribute("ERR_MSG");
				    if(errmsg != null && errmsg.equals("1"))
				    	out.println(" Password cannot be empty <br>");
				    if(errmsg != null && errmsg.equals("2"))
				    	out.println(" Authentication Error . Please try again <br>");
				    session.removeAttribute("ERR_MSG");
				if (Challenge !=  null)   { %>
				      Enter the challenge in your device and Enter the response Displayed in your device in the below text box: <%= Challenge %>
				   <% }
				%>
				
				<fieldset>
				<p>Please enter the Password</p>
					<table>
						<tr>
							<td>Password:</td><td><input tabindex="1" type="password" name="transPassword" onKeyPress="javascript: return processKey(event);" id="transPassword"/></td>
						</tr>
					</table>
					<p>
						<a href="javascript:postForm1(window.document.forms['FtressPasswordForm'])">
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