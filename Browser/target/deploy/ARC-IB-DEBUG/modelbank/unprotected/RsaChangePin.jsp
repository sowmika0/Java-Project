<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>

<jsp:directive.page session="true" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


		<form name="ChangePinForm" target="<%=request.getContextPath()%>/modelbank/unprotected/DevicePinLoginServlet" AUTOCOMPLETE="OFF">

			<table class="input">
				<tr>
					<td colspan="2" class="instruction">
						Enter your new pin
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" name="NewPin" size="20" maxlength="20"/> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="reminder">
						(This pin will be used as ID for online and phone banking services)
					</td>
				</tr>
				<tr>
					<td colspan="2">
					</td>
				</tr>				
				
				<tr>
					<td colspan="2" class="instruction">
						Confirm your new pin
					</td>
				</tr>
				<tr>
					<td width="23%">
						<input type="password" name="ConfirmPin" size="20" maxlength="20"/> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="reminder">
						(This pin will be used as ID for online and phone banking services)
					</td>
				</tr>
			</table>
		</form>					
					
	<p>
		<a href="javascript:postForm(window.document.forms['ChangePinForm'])">
			<img src="<%=request.getContextPath()%>/modelbank/unprotected/login.gif" border="0"/>
		</a>
	</p>