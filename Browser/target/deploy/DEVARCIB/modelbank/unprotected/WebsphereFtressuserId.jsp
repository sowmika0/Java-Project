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


	<script LANGUAGE="javascript">
		//<![CDATA[
			function setFocus() {
				window.document.forms["FtressUserIdForm"].UserId.focus();
			}

			function getFormFieldsAsParams(form) {
				var fields=new String();

				var elems = form.elements;
				for (i = 0; i < elems.length; i++) {
				// todo: could be more efficient (String addition)

					var curElem = elems[i];	
					var curName = curElem.name;
					var curValue = curElem.value;
					// need to check for combos as IE (duh) will not give us the value directly
	
					if (curElem.tagName.toLowerCase() == "select") {
						if (curValue == "") {
							curValue = curElem.options[curElem.selectedIndex].text;
						}
					}
				
					fields += curName + "=" + curValue + "&";        
				}
	
				return fields.substr(0, fields.length-1);
			};

			function postForm(form) {
				var http = false;
				
				if(navigator.appName == "Microsoft Internet Explorer") {
					http = new ActiveXObject("Microsoft.XMLHTTP");
				} else {
					http = new XMLHttpRequest();
				}
				
				var URL = form.target;
				var params = getFormFieldsAsParams(form);


				http.open('POST', URL, true);
				http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");	
				http.setRequestHeader("Content-length", params.length);
				http.setRequestHeader("Connection", "close");
				http.send(params);
				
				http.onreadystatechange=function() {
					if(http.readyState == 4) {
						if (http.responseText.indexOf("doFormBasedAuthentication") > 0 ) {
							eval(http.responseText);
						} else {
							document.getElementById("content").innerHTML = http.responseText;
						}
					}
				}
			};
			
			function doFormBasedAuthentication(username, password) {
				var authForm = window.document.forms["AuthForm"];
				authForm.username.value = username;
				authForm.password.value = password;
				authForm.submit();
			};
		//]]>
	</script>
</head>

<body onload="setFocus()">
	<form name="AuthForm" method=post action="<%=request.getContextPath()%>/servlet/BrowserServlet" AUTOCOMPLETE="OFF">
		<input name="password" type="hidden"/>
		<input name="username" type="hidden"/>
	</form>
	
	<table border="0" width="994">
		<tbody><tr><td><p>
		<img src="<%=request.getContextPath()%>/modelbank/unprotected/yourbank.jpg" usemap="#Map" border="0">
		<map name="Map" id="Map">
			<area shape="rect" coords="691,4,737,24" href='javascript:alert("logoff")'>
			<area shape="rect" coords="739,4,773,23" href='javascript:alert("help")'>
			<area shape="rect" coords="776,4,857,22" href='javascript:alert("accessibility")'>
			<area shape="rect" coords="862,3,920,22" href='javascript:alert("sitemap")'>
			<area shape="rect" coords="922,2,962,23" href='javascript:alert("legal")'>
			<area shape="rect" coords="964,3,1029,21" href='javascript:alert("privacy")'>
		</map>
		</p>		
		<div id = "content">
			<form name="FtressUserIdForm" target="<%=request.getContextPath()%>/modelbank/unprotected/FtressLoginServlet" AUTOCOMPLETE="OFF">
			
				<p><p style="font-weight: bold;color:#ff0000"/><p class="instruction">
				    Not registered yet? Please register <a href="<%=request.getContextPath()%>/modelbank/unprotected/bind.jsp">here</a></p>
			
				<table class="input">
					<tr>
						<td colspan="2" class="instruction">
							Enter your User ID
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="UserId" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your User ID?
						</td>
					</tr>
					<tr>
						<td colspan="2" class="reminder">
							(This is your seven character ID for online and phone banking services)
						</td>
					</tr>
				</table>
				
				<table class="input">
					<tr>
						<td colspan="2" class="instruction">
							Enter your OTP
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="OneTimePassword_combination" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your OTP?
						</td>
					</tr>
					<tr>
						<td colspan="2" class="instruction">
							Enter your PIN
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="Pin" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your PIN?
						</td>
					</tr>									
				</table>			
					
				<table class="input">
					<tr>
						<td colspan="2" class="instruction">
							Enter your OTP
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="OneTimePassword" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your OTP?
						</td>
					</tr>
				</table>		
						
				<table class="input">
					<tr>
						<td colspan="2" class="instruction">
							Enter your Password
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="Password_combination" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your Password?
						</td>
					</tr>
					<tr>
						<td colspan="2" class="instruction">
							Enter your MemWord
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="MemWordString" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your MemWord?
						</td>
					</tr>														
				</table>		
						
				<table class="input">
					<tr>
						<td colspan="2" class="instruction">
							Enter your Password
						</td>
					</tr>
					<tr>
						<td width="23%">
							<input name="Password" type="password" size="20"" maxlength="20" />
						</td>
						<td class="forgotten" width="77%">
							Forgotton your Password?
						</td>
					</tr>
				</table>
				
			</form>					
					
			<p>
				<a href="javascript:postForm(window.document.forms['FtressUserIdForm'])">
					<img src="<%=request.getContextPath()%>/modelbank/unprotected/login.gif" border="0"/>
				</a>
			</p>
		</div>
		
		</td></tr><tr><td align="right">
			<img src="<%=request.getContextPath()%>/modelbank/unprotected/double%20eye.gif">
		</td></tr></tbody>
	</table>
</body>
</html>