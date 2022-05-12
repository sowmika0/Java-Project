<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd" method="html" />
	<xsl:template match="/">

<html>
<head>
<!-- Forward the user onto the logout page if left inactive for 20 mins, to avoid a session timeout -->
<!-- todo: This value should be based on the session timeout (it could be added to the xsl; or this page converted to a jsp -->
<meta http-equiv="REFRESH" content="1200; url=../modelbank/unprotected/loggedout.jsp"/>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>T24 Update Service Login - [Administrator Login]</title>
<style type="text/css">

.instruction {
	font-family: "Trebuchet MS",Tahoma,Arial,Helvetica,sans-serif;
	font-weight: normal;
}
.information {
	font-family: "Trebuchet MS",Tahoma,Arial,Helvetica,sans-serif;
	font-size: small;
	font-weight: normal;
	color: #324986;
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
body {
	background-color: #FFFFFF;
}

table.input {
  width:50%;
  padding: 10px;
}

</style>
  <script type="text/javascript">
		//<![CDATA[
		function setFocus() {
			// Check where to put the focus
			var nameField = window.document.forms["login"].signOnName;

			if (nameField.getAttribute("readonly")) {
				window.document.forms["login"].password.focus();
			}
			else {
				window.document.forms["login"].signOnName.focus();
			}
		}

		function login() {
			window.document.forms["login"].submit();
		}
		//]]>
	</script>
    <script src="../scripts/ARC/javascriptBrowserDetection.js" type="text/javascript"></script>
    <script type="text/javascript">
      var warningMessage = "Sorry, your browser is not officially supported. " 
        + "Only Internet Explorer 6.0 or newer and Firefox 1.5 and newer " 
        + "are supported. Do you want to continue?";
      var redirectionPage = "../modelbank/unprotected/browsersDownload.html";
    </script>
    <script src="../scripts/ARC/unsupportedBrowserCheck.js" type="text/javascript" ></script>
  
</head>

<body onload="setFocus()">

<table width="994" border="0">
    <tr>
        <td>
          <p><img src="../plaf/images/t24-updates/updatesloginbanner.gif" border="0" usemap="#Map" alt="T24 Update Service"/>
        </p>
        <p></p>
        </td>
    </tr>
<tr>
<td>
<form name="login" method="post" action="BrowserServlet" AUTOCOMPLETE="OFF">
								<input type="hidden" name="command">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/command"/></xsl:attribute>
								</input>

								<input type="hidden" name="requestType" value="CREATE.SESSION"/>

								<input type="hidden" name="counter">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/counter"/></xsl:attribute>
								</input>

								<input type="hidden" name="branchAdminLogin">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/branchAdminLogin"/></xsl:attribute>
								</input>

								<input type="hidden" name="signOnNameEnabled">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/signOnNameEnabled"/></xsl:attribute>
								</input>
<!-- Error message -->
<p style="font-weight: bold;color:#ff0000">
	<xsl:value-of select="/responseDetails/login/error"/>
</p>
  <table class="input" width="100%" border="0">
  <tr></tr>
  <tr>
    <td colspan="2" class="instruction">Username</td>
    <td><input tabindex="1" name="signOnName" size="40" type="password"/></td>
  </tr>
  <tr>
    <td colspan="2"></td>
  </tr>
  <tr>
    <td colspan="2" class="instruction">Password</td>
    <td><input tabindex="1" name="password" size="40"  type="password"/></td>
  </tr>
  <tr>
	<td></td>
	<td></td>
	<td colspan="2" class="information">Usernames and passwords are case sensitive.</td>
  </tr>
  <tr></tr>
  <tr>
	<td colspan="3" class="information">Copyright Temenos Headquarters SA. All rights reserved.</td>
  </tr>    
  <tr>
    <td colspan="2"></td>
  </tr>


</table>

<!-- TODO: Hardcoded button (should be part of the style) -->
<p></p>
  <input tabindex="1" type="image" src="../plaf/images/arc-ib/tools/login.gif" alt="login"/>

</form>
</td>
</tr>
</table>

</body>
</html>

	</xsl:template>

</xsl:stylesheet>
