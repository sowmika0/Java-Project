<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.temenos.arc.security.filter.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String eaiURL = request.getContextPath()+ "/servlet/BrowserServlet";

	//Grab user from session
	String username = (String)session.getAttribute(Constant.SESSION_ATTR_USERNAME);

	//If there is no valid username in the session
	if (username == null || username.equals("null")) {
		System.out.println("# [RSAActivateSMS.jsp] Redirecting back to session invalid");
		System.out.println("# [RSANextToken.jsp] No Valid user available");
		session = request.getSession(true);
		if (session != null)
			session.invalidate();
	}
	
	// Debug for session
	if(session!=null){
		System.out.println("[RSANewPin.jsp] Current Step : " + session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP));
	}
%>
<center>
	<div id="error"></div>
			<form id="rsaform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>">
			<%
				// 1. SMS Authentication - NEW PIN
				if ( ((String)session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP)).equals(Constant.STEP_RSA_SMS_NEWPIN) ) {
			%>
				<fieldset>
					<legend>New PIN</legend> 
					<p>Please enter your new pin (no reuse)</p>
					<table>
						<tr>
							<td>New PIN:</td><td><input tabindex="1" type="password" name="<%=Constant.PARAM_RSA_NEWPIN%>"  id="<%=Constant.PARAM_RSA_NEWPIN%>" /></td>
						</tr>
						<tr>
							<td>Confirm:</td><td><input tabindex="2" type="password" name="<%=Constant.PARAM_RSA_CONFIRM_NEWPIN%>" id="<%=Constant.PARAM_RSA_CONFIRM_NEWPIN%>" /></td>
						</tr>
					</table>
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
					
			<% 
				// 2. SMS Authentication - Recieved TOKEN
			    } else if ( ((String)session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP)).equals(Constant.STEP_RSA_SMS_TOKEN) ) {
			%>
				<fieldset>
					<legend>New PIN</legend>
					<p>Please enter your passcode from the SMS notification</p>
					<table>
						<tr>
							<td>SMS Passcode: </td><td><input tabindex="1" type="password" name="<%=Constant.PARAM_RSA_SMS_TOKEN%>" id="<%=Constant.PARAM_RSA_SMS_TOKEN%>" /></td>
						</tr>
					</table>
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			
			<%
				// 1. SecureID Authentication- NEW PIN and NEXT TOKEN (from device)
				} else if ( ((String)session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP)).equals(Constant.STEP_RSA_SECUREID_NEWPIN_NEXTTOKEN) ) {
			%>			
				<fieldset>
					<legend>New PIN</legend>
					<p>Please enter your new pin (no reuse) and Next Token</p>
					<table>
						<tr>
							<td>New PIN:</td><td><input tabindex="1" type="password" name="<%=Constant.PARAM_RSA_NEWPIN%>"  id="<%=Constant.PARAM_RSA_NEWPIN%>" /></td>
						</tr>
						<tr>
							<td>Confirm:</td><td><input tabindex="2" type="password" name="<%=Constant.PARAM_RSA_CONFIRM_NEWPIN%>" id="<%=Constant.PARAM_RSA_CONFIRM_NEWPIN%>" /></td>
						</tr>
						<tr>
							<td>Next Token:</td><td><input tabindex="3" type="password" name="<%=Constant.PARAM_RSA_NEXT_TOKEN%>" id="<%=Constant.PARAM_RSA_NEXT_TOKEN%>"/></td>
						</tr>
					</table>						
					<p>
						<a href="javascript:validateForm(window.document.forms['rsaform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/submit.gif" border="0"/>
						</a>
						<a href="javascript:postForm(window.document.forms['rsacancelform'])">
							<img src="<%=request.getContextPath()%>/modelbank/unprotected/cancel.gif" border="0"/>
						</a>
					</p>
				</fieldset>
			<% } %>
			</form>
		<form id="rsacancelform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>" style="display:none;">
			<input readonly="readonly" type="text" name="rsacancel" value="yes" />
		</form>
</center>
