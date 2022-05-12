<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<jsp:directive.page session="true" contentType="text/html" /><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration"%>

<%@page import="com.temenos.t24browser.servlets.LoginServlet"%>
<%@page import="java.util.StringTokenizer"%>
<%
	String seedPositionsString = (String) session.getAttribute(LoginServlet.SEED_POSITIONS);
	String delimiter = LoginServlet.getConfig(1).getConfigValue(AuthenticationServerConfiguration.MEM_WORD_SEED_DELIM);
	StringTokenizer tokenizer = new StringTokenizer(seedPositionsString,delimiter);
	int[] seedPositions = new int[tokenizer.countTokens()];
	int k = 0;
	while (tokenizer.hasMoreTokens()) {
		String token = tokenizer.nextToken();
		int seedPos = Integer.parseInt(token);
		seedPositions[k] = seedPos;
		k++;
	}            	
	char[] allowedCharacters = LoginServlet.getAllowedCharacters();
%>
 		
<form name="PasswordForm" method="post" target="<%=request.getContextPath()%>/modelbank/unprotected/LoginServlet" AUTOCOMPLETE="OFF">
	<table class="input">

			
		<tr>
			<td colspan="2" class="instruction">
				Enter your password
			</td>
		</tr>
		<tr>
			<td width="23%"> 
				<input type="password" name="Password" onKeyPress="javascript: return disableEnterKey(event);" size="20" maxlength="20"/> 
			</td>
			<td class="forgotten" width="77%">
				Forgotton your password?
			</td>
		</tr>
		<tr>
			<td colspan="2" class="reminder">
				(This is your seven character ID for online and phone banking services)
			</td>
		</tr>

		<% for (int i=0; i < seedPositions.length; ++i) { %>
			<tr>
				<td colspan="2">
				</td>
			</tr>				

			<tr>
				<td colspan="2" class="instruction">
					Enter the character at position <%= seedPositions[i] %> from your memorable word
				</td>
			</tr>
			<tr>
				<td width="23%"> 
				<select name="Character<%=i %>" size="1" maxlength="1">
				<%		for (int j=0; j < allowedCharacters.length; ++j) { %>
						<option><%= allowedCharacters[j] %></option>
				<% }	// end of loop over j %>
				</select>
				
				</td>
				<td class="forgotten" width="77%">
					Forgotton your memorable word?
				</td>
			</tr>
			<tr>
				<td colspan="2" class="reminder">
					(This is the memorable supplied to the bank during registration)
				</td>
			</tr>
		<% } // end of loop over i %>
	</table>
	
	<p>
	<a href="javascript:postForm(window.document.forms['PasswordForm'])">	
		<img src="<%=request.getContextPath()%>/modelbank/unprotected/login.gif" border="0"/>
	</a>
	</p>
</form>