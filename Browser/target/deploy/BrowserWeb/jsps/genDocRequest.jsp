<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String imageId=request.getParameter("imageId"); %>
<% String isPopUp=request.getParameter("isPopUp"); %>
<% String windowName=request.getParameter("windowName"); %>

<html>
<head>
      <link rel="stylesheet" type="text/css">
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script src="../scripts/jsp.js"></script>
	<title>Processing...</title>
</head>
      <body onLoad="dorequest()">
		<form name="request" method="POST" action="../GetImage">
			<input type="hidden" name="imageId" value="<%= imageId %>"/>
			<input type="hidden" name="isPopUp" value="<%= isPopUp %>"/>
            <input type="hidden" name="windowName" value="<%= windowName %>"/>
		</form>
	</body>
</html>
