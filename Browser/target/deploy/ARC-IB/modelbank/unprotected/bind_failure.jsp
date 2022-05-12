<html>

<head>
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Your Bank - failure</title>
</head>

<body topmargin="0" leftmargin="0">

<p><img border="0" src="<%=request.getContextPath()%>/modelbank/unprotected/banner_new.png" width="216" height="41"></p>
<p><font face="Arial">Sorry - There was an error during Registration </font></p>
<p><font face="Arial">Please go to the <a href="<%=request.getContextPath()%>/modelbank/unprotected/bind.jsp">Registration page</a> to register</font></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<%
   int error_code = (Integer)request.getAttribute("Error_code");
   String Error_msg = "";
   switch(error_code){
        case 1 : Error_msg = "User Id is empty"; break;
        case 2 : Error_msg = "Device Serial id is invalid"; break;
        case 3 : Error_msg = "Pin is invalid"; break;
        case 4 : Error_msg = "Pin and Pin Confirmation do not match"; break;
        case 5 : Error_msg = "Service Unavailable";break;
        case 6 : Error_msg = "User Id is not available or Memorable word is invalid/expired";break;
        default :Error_msg = "Binding Incomplete";break;
   }
%>
<p><font face="Arial" size="1">Failure - <%= Error_msg %></</font></p>

</body> 

</html>
