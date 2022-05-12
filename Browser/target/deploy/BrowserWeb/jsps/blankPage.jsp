<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
function mySubmit()
	{
		var form = document.getElementById("blankPage");
		form.submit();
	}
window.onload = mySubmit; // submit the form as soon it has been completed its content loading
</script>
<title>Login</title>
</head>
<body>
<form id="blankPage" name="blankPage" method="post" action="../servlet/BrowserLoginServlet" >
<input type="hidden" name="blankRequestType" value="SESSION.CHECK"/>
</form>
</body>
</html>