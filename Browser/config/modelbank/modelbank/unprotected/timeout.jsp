<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.temenos.arc.security.filter.*"%>

<!-- Purpose of this page is to get redirected to login page when timeout, 
However if user wishes to get redirected to a custom page. 
It can be given by replacing the value "request.getContectPath()" to the url(absoulte path) of the custom page in the below line-->
<%
	session = request.getSession(true);
	if(session != null){

		session.invalidate();
		System.out.println("Session Invalidated successfully");
	}

%>


<%response.sendRedirect(request.getContextPath()+"/servlet/BrowserServlet");%>
