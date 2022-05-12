<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<html>

	<head>
		<meta http-equiv="Content-Language" content="en-gb">
		<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
		<title>Your Bank - error</title>
	</head>
	
	<body topmargin="0" leftmargin="0">
	
		<p><img border="0" src="<%=request.getContextPath()%>/modelbank/unprotected/banner_new.png" width="216" height="41"></p>
		<p><font face="Arial">Sorry - there is an error</font></p>
		<p><font face="Arial">Please go back to the <a href="<%=request.getContextPath()%>/servlet/BrowserServlet">home page</a></font></p>
		<p>&nbsp;</p>
		<p>&nbsp;</p>
		<p>&nbsp;</p>
		<%
			Set errors = (Set) request.getSession().getAttribute("xssErrors");
			if (null!=errors) {
				String errorMessage = "";
				for (Iterator iter = errors.iterator(); iter.hasNext(); ) {
					errorMessage = (String)iter.next() ;
		%>
		<p><font face="Arial" size="1">Failure - <%= errorMessage %></font></p>
		<%				
				}
			}
		%>
	</body>

</html>
