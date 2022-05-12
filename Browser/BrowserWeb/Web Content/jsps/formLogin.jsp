<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<title>T24 Sign in</title>
<style type="text/css">
					html {
						background-image: none;
					
						
					}
					body {
						margin: 2px;
						height: 98%;
						font-family: Arial, sans-serif;
						font-size: smaller;
						overflow: auto;
						white-space: nowrap;
					}
					li{
					list-style-type: none;
					}
					a{
						font-size:smaller;
							white-space: nowrap;
						}
					img{
						border: 0px;
						margin-right: 2px;
					}
					input {
						border: 1px solid silver;
					}
					label {
						padding: 0 20px 0 10px;
						font-size: medium;
					}
					.input-ro {
						border: 1px solid black;
						background-color: #e0e0e0;
					}
					.navbar{
						background-color: white;
						padding: 5px 5px;
						float: left;
						width: 30%;
					}
					.error{
						text-align: left;
						color: #ff0000;
						font-size: smaller;
					}
						
					#sign-in
					{
						border:1px double white;
						color: white;
						padding: 0px 10px 0px 10px;
						background-color: #334988;
					}
				
					#login-window {
						padding: 10px 10px 30px 20px;
						float: left;
						width: 68%;
					}
					#logo-window {
						border-left: 2px solid silver;
					}
					#topspacer {
						padding: 50px;
						background-color: white;
					}
					#title {
						padding-top: 5px;
						padding-bottom: 15px;
						font-size:large;
						color: #334988;
					}
					#container{
						width: 100%;
					}
					.spacer{
						padding: 5px;
					}
					#case-sensitive {
						font-size: smaller;
						color: #a1a1a1;
					}
					#copyright {
						color: #a1a1a1;
					}
					</style>
<script LANGUAGE="javascript" type="text/javascript">
					//
						function setFocus()
						{
							// Check where to put the focus
							var nameField = window.document.forms["login"].signOnName;
							if (nameField.getAttribute("readonly"))
							{
								window.document.forms["login"].password.focus();
							}
							else
							{
								window.document.forms["login"].signOnName.focus();
							}
						} 
						
					//</script>
</head>
<body onload="setFocus()">

<div id="topspacer"></div>
<table id="container">
	<tbody>
		<tr>
			<td nowrap width="50%">
			<form name="login" method="post" action="j_security_check">
			<div id="login-window"><span id="title">FORM Login</span> <br>
			<table>
				<tr>
					<td colspan="3"></td>
				</tr>
				<tr>
					<td nowrap><span><label for="signOnName">Username</label></span></td>
					<td nowrap><input type="password" id="signOnName"
						name="j_username" tabindex="1" size="30" value=""></td>
					<td width="25%" rowspan="2"></td>
				</tr>
				<tr>
					<td colspan="3"><img alt=""
						src="../plaf/images/default/block.gif"></td>
				</tr>
				<tr>
					<td nowrap><span><label for="password">Password</label></span></td>
					<td nowrap><input type="password" id="password"
						name="j_password" tabindex="2" size="30" value=""></td>
				</tr>
				<tr>
					<td colspan="3"><img alt=""
					    src="../plaf/images/default/block.gif"></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="Sign in" id="sign-in"
						tabindex="3"></td>
				</tr>
				<tr>
					<td></td>
					<td nowrap><span id="case-sensitive">Usernames and
					passwords are case sensitive.</span></td>
				</tr>
			</table>
			</div>
			</form>
			</td>
			<td nowrap width="50%">
			<div id="logo-window" class="navbar">
			<li><a href="http://www.temenos.com"><img
				alt="Temenos Web Site" src="../plaf/images/default/banner_start.gif"></a></li>
			<li class="spacer"></li>
			<li><a href="../portal/portal.htm">T24 Portal</a></li>
			<li><a href="http://www.temenos.com">Temenos Web Site</a></li>
			</div>
			</td>
		</tr>
	</tbody>
</table>
<div class="spacer"><span id="copyright">Copyright &copy;
Temenos Headquarters SA. All rights reserved.</span></div>
</body>
</html>

