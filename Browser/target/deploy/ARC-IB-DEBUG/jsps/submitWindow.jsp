<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String contextRoot=request.getContextPath(); %>
<% String requestType=request.getParameter("requestType"); %>
<% String routineArgs=request.getParameter("routineArgs"); %>
<% String companyId=request.getParameter("companyId"); %>
<% String unlock=request.getParameter("unlock"); %>
<% String closing=request.getParameter("closing"); %>
<% String pwprocessid=request.getParameter("pwprocessid"); %>
<% String windowName=request.getParameter("windowName"); %>

<html>

      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

      <body onLoad="document.submitForm.submit();">

		<form name="submitForm" method="POST" action="<%= contextRoot %>/servlet/BrowserServlet">
			<input type="hidden" name="requestType" value="<%= requestType %>"/>
			<input type="hidden" name="routineArgs" value="<%= routineArgs %>"/>
			<input type="hidden" name="companyId" value="<%= companyId %>"/>
			<input type="hidden" name="unlock" value="<%= unlock %>"/>
			<input type="hidden" name="closing" value="<%= closing %>"/>
			<input type="hidden" name="command" value="globusCommand"/>	
			<input type="hidden" name="pwprocessid" value="<%= pwprocessid %>"/>
			<input type="hidden" name="windowName" value="<%= windowName %>"/>
		</form>

	</body>
</html>
