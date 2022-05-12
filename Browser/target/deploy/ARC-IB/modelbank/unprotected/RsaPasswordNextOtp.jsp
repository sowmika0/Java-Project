<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>

<jsp:directive.page session="true" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.temenos.t24browser.servlets.LoginServlet"%>


		<form name="NextOtpForm" target="<%=request.getContextPath()%>/modelbank/unprotected/RsaDevicePasswordLoginServlet" AUTOCOMPLETE="OFF">

			<table class="input">
				<tr>
					<td colspan="2" class="instruction">
						Enter your next One Time Password
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" name="NextOTP" size="20" maxlength="20"/> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="reminder">
						(This is the next code that appears on your SecurID Token)
					</td>
				</tr>
				<tr>
					<td colspan="2">
					</td>
				</tr>				
			</table>
		</form>					
					
	<p>
		<a href="javascript:postForm(window.document.forms['NextOtpForm'])">
			<img src="<%=request.getContextPath()%>/modelbank/unprotected/login.gif" border="0"/>
		</a>
	</p>