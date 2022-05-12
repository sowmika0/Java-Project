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
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Your Bank - error</title>
</head>

<body topmargin="0" leftmargin="0">

<%
String AuthenticatorLocked = (String)session.getAttribute("AUTHENTICATOR_LOCKED"); 
String errorMessage = "";
if(AuthenticatorLocked == "true" || AuthenticatorLocked == "TRUE") {
	errorMessage = "Password Authenticator is locked. Please Contact System Administrator or Bank";
}
else
{
	errorMessage = "Authentication Error : User credentials mismatch";
}
%>
<p><img border="0" src="<%=request.getContextPath()%>/modelbank/unprotected/yourbankreducedlogo.jpg" width="216" height="41"></p>
<p><font face="Arial">Sorry - there is an error</font></p>
<p><font face="Arial">Please go back to the <a href="<%=request.getContextPath()%>/servlet/BrowserServlet">home page</a></font></p>
<p><font face="Arial"><%= errorMessage %></a></font></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</body> 

</html>
