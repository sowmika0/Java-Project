<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>

<jsp:directive.page session="true" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.temenos.t24browser.servlets.FtressLoginServlet"%>


		<form name="FtresschangePasswordForm" target="<%=request.getContextPath()%>/modelbank/unprotected/FtressLoginServlet" AUTOCOMPLETE="OFF">

			<table class="input">
                        <tr>
					<td colspan="2" class="instruction">
						Enter your old password
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" name="OldPassword" size="20" maxlength="20"/> 
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
						<input type="password" name="NewPassword" size="20" maxlength="20"/> 
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
						<input type="password" name="ConfirmPassword" size="20" maxlength="20"/> 
					</td>
				</tr>
			</table>
		</form>					
					
	<p>
		<a href="javascript:postForm(window.document.forms['FtresschangePasswordForm'])">
			<img src="<%=request.getContextPath()%>/modelbank/unprotected/login.gif" border="0"/>
		</a>
	</p>