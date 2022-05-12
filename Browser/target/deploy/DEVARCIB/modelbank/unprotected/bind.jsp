<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<jsp:directive.page session="false" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.temenos.t24browser.servlets.BindServlet"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
	<title>Enter your Bank supplied User Id</title>
	<style type="text/css">

.instruction {
	font-family: Arial, Times, serif;
	font-weight: bold;
	font-size: 12px;
	color:#324986;
	border:0px;
}
.forgotten {
	font-family: Arial, Courier, monospace;
	font-size: 12px;
	font-weight: normal;
	color: #FF0000;
}
.reminder {
	font-family: Arial, Times, serif;
	font-size: small;
	font-size: 12px;
	font-weight: normal;
	color: #324986;
	border:0px;
}
.error {
	
	text-align: left;
	color: #ff0000;
	font-size: smaller;
       }
body {
	background-color: #EAEEF2;
	
}

table.input {
 background-color:#ffffff;
  
  padding-top: 0px;
  padding-bottom: 0px;
  align:center;
  padding-left: 40px;
}
.input_box {
	border: 1px solid #324986;
	
	}
		
		
	</style>
	<script type="text/javascript">
          <!-- 
               function validate_registration(){
                        valid = true;
                        if(document.register_form.userId.value == ""){ 
                              alert("Please fill the Userid");
                              valid =false;
                              return valid;
                        }
                        if(document.register_form.deviceSer.value == ""){ 
                              alert("Please fill the Device Serial No");
                              valid =false;
                              return valid;
                        }
                        if(document.register_form.memData.value == ""){ 
                              alert("Please fill the Memorable Word");
                              valid =false;
                              return valid;
                        }
                       if(document.register_form.pin.value == ""){ 
                              alert("Please fill the pin");
                              valid =false;
                              return valid;
                        }
                       if(document.register_form.confirmPin.value == ""){ 
                              alert("Please repeat the pin");
                              valid =false;
                              return valid;
                        }
                       return valid;
               }   
          //-->
       </script>
	<link rel="stylesheet" type="text/css" href="../../plaf/style/arc-ib/banner_arcib.css"/>

</head>
<body style="margin-left:0;padding-top:0;margin-top:0;">
      
	<form name="register_form" method="post" action="<%=request.getContextPath()%>/unprotected/BindServlet" AUTOCOMPLETE="OFF">
		<table border="0" align="center" class="input" width="80%"  style="padding-left: 0px;">
		<tr>
			<td colspan="5">
			<div class="banner_text">
                <img border="0" style="margin-left:-80px;margin-top:-40px;" src="../../html/ARC/images/Temenos-Logo.png" align="left"/>
                <img border="0" style="margin-right:-80px;margin-top:0px;" src="<%=request.getContextPath()%>/modelbank/unprotected/secure_token.png" align="right"  usemap="#Map"/>
            </div>			
			<map name="Map" id="Map">
				<area tabindex="-1" shape="rect" coords="724,4,752,23" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'General', 'height=450,width=450') )"/>
				<area tabindex="-1" shape="rect" coords="760,4,840,22" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Accessibility', 'height=450,width=450') )"/>
				<area tabindex="-1" shape="rect" coords="848,3,900,22" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'SiteMap', 'height=450,width=450') )"/>
				<area tabindex="-1" shape="rect" coords="910,2,940,23" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Legal', 'height=450,width=450') )"/>
				<area tabindex="-1" shape="rect" coords="946,3,1000,21" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Privacy', 'height=450,width=450') )"/>
			</map>
			</td>
		</tr>
            
            <tr><td><a href="../../index.html">Click here</a> to go back to home</td></tr>
           <tr>
           <td rowspan="5"><img src="./token.png" name="slide" border="0"/> </td>
           <td>
		<table  align="center" class="input width="80%" cellspacing="10" frame="lhs">	
		<th bgcolor="#324986"  align="left" style="color: #ffffff; border=0px;" colspan="2" rowspan="2">Register</th>
		
		<tr>
    		<td style="border:0px;" colspan="2"></td>
  		</tr>
		
			<tr>
				<td class="instruction" colspan="2" >
					User ID
				</td>
			</tr><tr>
				<td style="border:0px;" >
					<input class="input_box" name="<%= BindServlet.USER_ID %>" size="30" type="text" value=""/>
				</td>
			</tr>
			<tr>
				<td colspan="4" class="reminder">
					(This is your nine character ID for online and phone banking services)
				</td>
			</tr>
			<tr>
			<td colspan="2" style="border:0px;" > </td>
			</tr>			
			
			<tr>
				<td class="instruction" colspan="2" >
					Device Serial Number
				</td>
			</tr>
			<tr>
				<td style="border:0px;" >
					<input class="input_box"  name="<%= BindServlet.DEVICE_SER %>" size="40" type="text"/>
				</td>
			</tr>
			<tr>
				<td class="reminder" colspan="4">
					(This is serial number from the back of your token device)
				</td>
			</tr>
			
			<tr>
			<td colspan="2" style="border:0px;" > </td>
			</tr>			
			
			<tr>
				<td class="instruction" colspan="2" >
					Please enter memorable word
				</td>
			</tr><tr>
				<td style="border:0px;" >
					<input class="input_box"  name="<%= BindServlet.MEM_DATA %>" size="40" type="password" value=""/>
				</td>
			</tr>
			<tr>
				<td  class="reminder" colspan="4">
					(This is the memorable word you previously set up)
				</td>
			</tr>
			
			<tr>
			<td colspan="2" style="border:0px;" > </td>
			</tr>			
			
			
			<tr>
				<td class="instruction" colspan="2" >
					Please create your online banking PIN
				</td>
			</tr><tr>
				<td style="border:0px;" >
					<input class="input_box"  name="<%= BindServlet.PIN %>" size="40" type="password" value=""/>
				</td>
			</tr>
			<tr>
				<td  class="reminder" colspan="4">
					(This is the six digit PIN number that you want to use)
				</td>
			</tr>
			
			<tr>
			<td colspan="2" style="border:0px;" > </td>
			</tr>			
			
			<tr>
				<td  class="instruction" colspan="2" >
					Finally, please confirm your online banking PIN
				</td>
			</tr><tr>
				<td style="border:0px;" >
					<input class="input_box"  name="<%= BindServlet.CONFIRM_PIN %>" size="40" type="password" value=""/>
				</td>
			</tr>
			<tr>
				<td class="reminder" colspan="4">
					(Repeat the PIN number)
				</td>
			</tr>
			
			<tr>
			<td colspan="2" style="border:0px;" > </td>
			</tr>			
			
			<tr>
			
			
				<td style="border:0px;" >
					<button type="submit" id="submit" onclick=" return validate_registration();">Register</button>
				</td>
			</tr>
                      </table> </td>                      
                      </tr>     
		</table>
	</form>
</body>
</html>


