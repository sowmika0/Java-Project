<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page isErrorPage="true" %>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="plaf/style/default/general.css" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title>Exception</title>
	</head>

	<body topmargin="2" leftmargin="2">
	
		<table id="exception" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td></td>
				<td></td>
				<td colSpan="3"></td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td class="formlight" rowSpan="3">
					<img src="plaf/images/default/block.gif" width="1">
				</td>
				<td class="formback"></td>
				<td class="caption" colspan="3">Exception</td>
				<td class="formback"></td>
				<td class="formdark" rowSpan="3">
					<img src="plaf/images/default/block.gif" width="1">
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>
					<table>
						<tr><br /></tr>
						<tr>
							<td class="message">We're sorry but the request could not be processed.<br /><br /></td>
						</tr>
						<tr></tr>
						<tr>
							<td class="message">The exception raised was :-<br /></td>
						</tr>
						<tr>
							<td class="caption"><%= exception.getMessage() %><br /></td>
						</tr>
					</table>
				</td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td class="formdark" colSpan="7"></td>
			</tr>
		</table>
	</body>
</html>
