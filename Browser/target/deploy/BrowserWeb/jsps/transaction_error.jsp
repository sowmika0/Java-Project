<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction Signing Error</title>
</head>

<body>
<div id="content">
	<table style="width: 618px; height: 100px; background-color: #F2F7F5; border: 1px solid #DFDFDF; margin-bottom: 20px !important; margin-left: 20px !important;">
		<tr>
		<td style="text-align: center;">
				<% 
					String authenticatorlocked = (String)session.getAttribute("TRANS_AUTHENTICATOR_LOCKED");
					String authenticatorerror = (String)session.getAttribute("TRANS_AUTHENTICATOR_ERROR");
				    if(authenticatorlocked != null && authenticatorlocked.equals("true")) { %>
					<p>
				    	You have tried 3 times to enter your OTP unsuccessfully. You may continue your session but you will not be able to request anymore OTP tokens. Please contact the bank to get your privileges reset. <br>
					</p>
				<%	} else if (authenticatorerror != null && authenticatorerror.equals("true")) { %>
					<p>
			    	Error occured during authentication process. Please retry later or contact your bank for support <br>
					</p>				
				<% }
				    session.removeAttribute("TRANS_AUTHENTICATOR_LOCKED");
				%>
				
		</td>
		</tr>
	</table>
</div>
</body> 

</html>
