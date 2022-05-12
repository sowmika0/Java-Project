<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" pageEncoding="utf-8" contentType="text/html;UTF-8"%>
<%@ page import="com.temenos.t24browser.utils.PropertyManager" %>
<%@ page import="com.temenos.t24browser.servlets.BrowserServlet" %>
<%@ page import="com.temenos.t24browser.debug.DebugUtils" %>

<%-- Browser parameters are needed to decide if obfuscation is on
	(for ARC-IB deployment) or off (for internal Browser deployment).  --%>
<%
		
		String realPath = application.getRealPath("");
		PropertyManager ivParameters = new PropertyManager(realPath, 
	     application.getInitParameter(BrowserServlet.BROWSER_PARAMETERS_INIT_PARAM));
		ivParameters.getParameterValue("");
   		String value = ivParameters.getParameterValue(DebugUtils.USE_INT_OBFUSCATION_NAME);
   		boolean useIntObfuscation =  Boolean.valueOf(value).booleanValue();
   		value = ivParameters.getParameterValue(DebugUtils.USE_EXT_OBFUSCATION_NAME);
   		boolean useExtObfuscation = Boolean.valueOf(value).booleanValue();
%>


<% System.out.println("HTTP request: " + request); %>

<% request.setCharacterEncoding("UTF-8"); %>

<% String realfname=request.getParameter("realfname"); %>
<% String fname=request.getParameter("fname"); %>
<% String ceids=request.getParameter("ceids"); %>
<% String enqids=request.getParameter("enqids"); %>
<% String enqdescs = new String(request.getParameter("enqdescs").getBytes("UTF-8"), "UTF-8"); %>
<% String olddata=request.getParameter("olddata"); %>
<% String skin=request.getParameter("skin"); %>
<% String formId = request.getParameter("formid"); %>

<html>
<head>
				  <link rel="stylesheet" type="text/css" href="../plaf/style/<%= skin %>/general.css"/>
        
        			<% if(useIntObfuscation && useExtObfuscation) {%>
						<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
						<script type="text/javascript" src="../scripts/obfuscated/all.js"></script>
					<% } else if(useIntObfuscation) {%>
				        <script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
						<script type="text/javascript" src="../scripts/obfuscated/general.js"></script>
						<script type="text/javascript" src="../scripts/obfuscated/fieldcontext.js"></script>
			        	<script type="text/javascript" src='../scripts/ARC/FragmentUtil.js'></script>	
				        <script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
					<% } else if(useExtObfuscation) {%>
						<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
						<script type="text/javascript" src="../scripts/all.js"></script>
					<% } else  {%>
				        <script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
						<script type="text/javascript" src="../scripts/general.js"></script>
						<script type="text/javascript" src="../scripts/fieldcontext.js"></script>
			        	<script type="text/javascript" src='../scripts/ARC/FragmentUtil.js'></script>	
				        <script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
						<script type="text/javascript" src="../scripts/ARC/T24_constants.js"></script>
					<% } %>
				<title>Context Enquiries</title>
</head>

<body onload="runContext( '<%= ceids %>','<%= enqids %>', '<%= enqdescs %>','<%= skin %>', '<%= realfname %>','<%= olddata %>','<%= fname %>','<%= formId %>')">

				
							<!-- end of header-->
							<!-- Display all Context Enquiries in a table -->
							<table id="myTable"></table>
							<!-- The Footer -->
			


</body>

</html>