<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<title>T24 Sign On</title>

	<style type="text/css">
		.instruction {
			font-family: Arial, Times, serif;
			font-weight: bold;
		}
		.forgotten {
			font-family: Arial, Courier, monospace;
			font-size: small;
			font-weight: normal;
			color: #FF0000;
		}
		.reminder {
			font-family: Arial, Times, serif;
			font-size: small;
			font-weight: bold;
			color: #666666;
		}
		table.input {
			border:1px solid black; 
			width:50%;
			padding: 10px;
		}		
	</style>

</head>

<body>
	<div id = "content">
 	<table border="0" width="994"> 
	
		 <form name="FtressPasswordForm" target="<%=request.getContextPath()%>/servlet/protected/FtressChangePasswordServlet" AUTOCOMPLETE="OFF"> 
           <table class="input">
                <tr>
					<td colspan="2" class="instruction">
						Enter your old password
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" id="OldPassword" name="OldPassword" size="20" maxlength="20"/> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="reminder">
						(This is your existing password)
					</td>
				</tr>
				<tr>
					<td colspan="2" class="instruction">
						Enter your new password
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" id="NewPassword" name="NewPassword" size="20" maxlength="20"/> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="reminder">
						(This password will be used ID for online and phone banking services)
					</td>
				</tr>
				<tr>
					<td colspan="2">
					</td>
				</tr>				
    			<tr>
					<td colspan="2" class="instruction">
						Confirm your new password
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" id="ConfirmPassword" name="ConfirmPassword" size="20" maxlength="20"/> 
					</td>
				</tr>
				</table>
              </form>
			  <table>
              <tr>
				<td width="23%">
                 <input type="submit" value="submit" onclick="javascript:postForm(window.document.forms['FtressPasswordForm'])" border="0" /> 
			    </td>
			  <tr>
		 </table>	
		 </table>
		</div>
</body>
</html>