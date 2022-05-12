<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.temenos.arc.security.filter.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String eaiURL = request.getContextPath()+ "/servlet/BrowserServlet";
	//Grab user from session
	String username = (String)session.getAttribute(Constant.SESSION_ATTR_USERNAME);

	// If there is no valid username in the session
	if (username == null || username.equals("null")) {
		System.out.println("# [RSANextToken.jsp] Redirecting back to session invalid");
		System.out.println("# [RSANextToken.jsp] No Valid user available");
		session = request.getSession(true);
		if(session !=null)
			session.invalidate();		
	}
	
	//Debug for session
	if(session!=null){
		System.out.println("[RSANextToken.jsp] Current Step : " + session.getAttribute(Constant.SESSION_ATTR_CURRENT_STEP));
	}
%>

<center>
	<div id="error"></div>
			<form id="rsaform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>">
				<fieldset>
				<legend>Next Token</legend>
				<p>Please enter the Next Token</p>
					<% 
					   // Set current step to RSA NEXT TOKEN
						session.setAttribute(Constant.SESSION_ATTR_CURRENT_STEP, Constant.PARAM_RSA_NEXT_TOKEN);
					%>
					<table>
						<tr>
							<td>Next Token:</td><td><input tabindex="1" type="password" name="<%=Constant.PARAM_RSA_NEXT_TOKEN%>" id="<%=Constant.PARAM_RSA_NEXT_TOKEN%>"/></td>
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
			</form>
		<form id="rsacancelform" method="post" target="<%=eaiURL%>" action="<%=eaiURL%>" style="display:none;">
			<input readonly="readonly" type="text" name="rsacancel" value="yes" />
		</form>
</center>
