
<html>

<head>
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Your Bank - logged out</title>
<script LANGUAGE="javascript">
		//<![CDATA[
function pwdSuccess(contextRoot)
	{
		var _explorerURL = location.protocol + "//" + location.host + "/" + contextRoot +"/modelbank/unprotected/loggedout_passwd.jsp?logged_out=yes";
		window.location = _explorerURL;
	}
		//]]>
	</script>
</head>
<% String param = request.getParameter("logged_out"); 
	String onloadfun = "";
	if(param == "" || param == null ) {	
		onloadfun = "pwdSuccess('" + request.getContextPath() + "')";
	}
%>
<body topmargin="0" leftmargin="10" onload="<%=onloadfun%>" >


<p><img border="0" src="<%=request.getContextPath()%>/modelbank/unprotected/banner_new.png" ></p>
<p><font face="Arial" color="#324986">Your password has changed Successfully!! Please relogin with new password</font></p>
<p><font face="Arial" color="#324986">Click to go back to the <a href="<%=request.getContextPath()%>/servlet/BrowserServlet">login page</a></font></p>

</body>

</html>