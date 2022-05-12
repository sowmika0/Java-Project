<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<jsp:directive.page session="false" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Secure Browser Login</title>
</head>
<body>
<form name="UserIdForm" method="post" action="LoginServlet" AUTOCOMPLETE="OFF">
Please enter your User ID: <input type="text"
	name="UserId" size="20" maxlength="20" /> 
<button type="submit" value="Submit">Submit</button>
</form>
</body>
</html>
