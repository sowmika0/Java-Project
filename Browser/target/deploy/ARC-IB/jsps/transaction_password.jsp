<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Your Bank International Online Banking</title>
</head>
<body>
<div id="content">
	<center>
			<form id="FtressPasswordForm" method="post" target="<%=request.getContextPath()%>/servlet/FtressTransactionServlet" action="<%=request.getContextPath()%>/servlet/FtressTransactionServlet">
				<% 
				 		  
				   String errmsg = (String)session.getAttribute("ERR_MSG");
				    if(errmsg != null && errmsg.equals("1"))
					{
						%>
						<p style="color: red; margin: 10px 20px;">Please enter the authorisation code.</p>
						<%
						session.removeAttribute("ERR_MSG");
					}
				    if(errmsg != null && errmsg.equals("2"))
					{
						%>
						<p style="color: red; margin: 10px 20px;">Sorry, we
						   couldn't recognise your code. Please verify and reenter. 
						   Account will be locked, if your code is entered wrongly for 3 times,
						   and contact bank to unlock.</p>
						<%
						session.removeAttribute("ERR_MSG");
					}
				    if(errmsg != null && errmsg.equals("5")){
				    	out.println(" Password successfully changed . Please try with new password");
				    session.removeAttribute("ERR_MSG");
				    }
				%>
				
				
				<table>
					<tr>
						<td>
							<div style="margin-top: 10px;">
								<div
									style="padding: 20px 0px 0px 0px; white-space: nowrap !important;">
									<strong>Please enter the password</strong>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td><br /></td>
					</tr>
					<tr>
						<td><input tabindex="1" type="password" name="transPassword" onKeyPress="javascript: return processKey(event);" id="transPassword"/></td>
					</tr>
				</table>
				<div>
					<a href="javascript:postForm1(window.document.forms['FtressPasswordForm'])">
						<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
					</a>
	               	<a href="javascript:cancelForm(window.document.forms['FtressPasswordForm'])">
						<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
					</a>
				</div>
			</form>
	</center>
	</div>	
</body>
</html>