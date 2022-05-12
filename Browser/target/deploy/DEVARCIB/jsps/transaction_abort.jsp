<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction Abort</title>
</head>
<body>
<%
String OOB_FAILURE = (String)session.getAttribute("OOB_SEND_FAILURE");
if(OOB_FAILURE == "true"){
%>
<p>
Error while sending OOB. Please Try Again....!!!
</p>
<%}
else {%>
<p>
Transaction Aborted.
</p>
<% } %>
</body>
</html>
