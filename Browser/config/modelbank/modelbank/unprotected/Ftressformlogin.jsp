<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setHeader("Content-Type","text/html");
response.setDateHeader ("Expires", 0);
%>

<%@page import="com.temenos.t24browser.servlets.FtressLoginServlet"%>

doFormBasedAuthentication("<%= session.getAttribute(FtressLoginServlet.j_username) %>", "<%= session.getAttribute(FtressLoginServlet.j_password) %>");

