<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Your Bank International Online Banking</title>
</head>
<body>
<%
String OOB_FAILURE = (String)session.getAttribute("OOB_SEND_FAILURE");
if(OOB_FAILURE == "true"){
%>
<div style="margin: 20px;">
<p>&nbsp;</p>
<p>Sorry, request cannot be completed. Please contact us to
     ensure that your details are up to date and to complete this
     transaction.</p>
<br/>
<p>You can call us on NNNN NNN NNNN or +NN (0)NNNN NNNN if
     calling from abroad (available 24 hours).</p>
<br/>
<p>Transaction cancelled. Please select another menu item to continue.</p>
</div>
<%}
else {%>
<div id="first_div">
<div>
   <table
	    style="width: 618px; height: 100px; background-color: #F2F7F5; border: 1px solid #DFDFDF; margin-bottom: 20px !important; margin-left: 20px !important;">
	    <tr>
		     <td style="text-align: center;">
   <span>Transaction cancelled. Please select another menu item.</span></td>
	    </tr>
   </table>
</div>
</div>
<% } %>
</body>
</html>
