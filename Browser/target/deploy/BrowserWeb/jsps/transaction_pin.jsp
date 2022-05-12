<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Your Bank International Online Banking</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta http-equiv="imagetoolbar" content="no" />
</head>
<body>
	<div id="content">
		<center>
			<form id="FtressPasswordForm" method="post" target="<%=request.getContextPath()%>/servlet/FtressTransactionOtpServlet">
					<% 
						String Challenge = (String)session.getAttribute("CHALLENGE"); 
						String transType = (String)session.getAttribute("AUTH_TYPE");
						String message = "";
						if(transType != null && transType.equals("TOKEN"))
						{
							message = "Please enter the one time password generated in your token device";
						}
						if(transType != null && transType.equals("CHRES"))
						{
							message = "Please enter the response code generated in your token device for Challenge ";
							message = message.concat(Challenge);
						}
						if(transType != null && transType.equals("SMS"))
						{
							message = "Please enter the security code received on your mobile or email";
						}
					%>
					<table>
						<tr>
							<td>
								<div style="margin-top: 10px;">
									<div
										style="padding: 20px 0px 0px 0px; white-space: nowrap !important;">
										<strong><%=message%></strong>
									</div>
								</div>
							</td>
						</tr>
						<tr>
							<td><br /></td>
						</tr>
						<tr>
							<td><input tabindex="1" type="numeric" name="transPassword" onKeyPress="javascript: return processKey(event);" id="transPassword"/></td>
						</tr>
					</table>
					<div>
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
					%>
				</div>
				<div>
					<p>
						<a href="javascript:postForm1(window.document.forms['FtressPasswordForm'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
                   		<a href="javascript:cancelForm(window.document.forms['FtressPasswordForm'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</div>
			</form>
		</center>
	</div>	
</body>
</html>