<html>

<head>
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Your Bank - Custom Message</title>
</head>

<body topmargin="0" leftmargin="0">
<%  String strErrMsg = null;
	if(session.getAttribute("CUSTOMMSG")==null){
		if(request.getParameter("Message")==null){
			strErrMsg = "";
		}else{
			strErrMsg = request.getParameter("Message");
			strErrMsg = "<span class=\"error\"> &nbsp;&nbsp;"+strErrMsg+"</span>";
		}	
	}else{
		strErrMsg = (String)session.getAttribute("CUSTOMMSG");
		if(strErrMsg.equals("SIGN.OFF")){
			strErrMsg = "Thank you for using Temenos T24";
		}
		session.setAttribute("CUSTOMMSG",null);
	}
%>
<p><img border="0" src="<%=request.getContextPath()%>/plaf/images/default/temenos-icon.gif" width="32" height="32"></p>
<p><font face="Arial" size="1"><%=strErrMsg%></font></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><font face="Arial">Custom Message <a href="#">Custom Link</a></font></p>
<p>&nbsp;</p>

</body> 

</html>
