<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html"/>
		<title>T24 Browser Error</title>
		<link rel="stylesheet" type="text/css" href="../plaf/style/default/general.css"/>
		<style type="text/css">
			html {
				background-image: none;
			}
			body {
				font: 12px Arial, sans-serif;
			}
			table {
				font: 12px Arial, sans-serif;
			}
			td {
				white-space: nowrap;
				font-weight: bold;
			}
			input {
				border: 1px solid silver;
			}
			label {
				padding: 0 20px 0 10px;
			}
			.input-ro {
				border: 1px solid black;
				background-color: #e0e0e0;
			}
			.navbar{
				border:1px solid white;
				background-color: #8f9cbe;
				padding: 5px 0;
			}
			.nav {
				whitespace: nowrap;
				margin: 5px;
				color: white;
			}
			.error{
				text-align: center;
				color: #ff0000;
			}
			
			#sign-on {
				 margin-left: 4px;
				 border:0;
			}
			#login-window {
				padding: 10px 20px 30px 20px;
			}
			#login-banner {
				width: 100%;
				background-color: #213a7d;
			}
			#sign-on-title {
				padding-top: 5px;
				padding-bottom: 15px;
			}
			#copyright {
				padding-top: 15px;
			}

		</style>
	</head>
	<body>

		<table id="login-banner">
			<tr>
				<td>
					<a href="http://www.temenos.com">
						<img alt="Temenos Web Site" src="../plaf/images/default/banner_start.gif"/>
					</a>
				</td>
				<td/>
				<td align="right">
					<img src="../plaf/images/default/banner_end.gif"  alt=""/>
				</td>
			</tr>
		</table>

							
		<form name="login" method="post" action="BrowserServlet">
			
			<div id="login-window" class="display_box">
				<table>

					
					<tr>
						<td id="sign-on-title" colspan="3"> Unauthorised system access , Please contact system administrator</td>
					</tr>
						</table>
			</div>
		</form>
		<%
			Set errors = (Set) request.getSession().getAttribute("xssErrors");		
			if (null!=errors) {
				String errorMessage = "";
				for (Iterator iter = errors.iterator(); iter.hasNext(); ) {
					errorMessage = (String)iter.next() ;
		%>
		<p><font face="Arial" size="2">Failure - <%= errorMessage %></font></p>
		<%				
				}
			}
		%>
	</body>


</html>
