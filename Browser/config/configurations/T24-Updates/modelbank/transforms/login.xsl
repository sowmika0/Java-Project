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
<title>T24 Update Service Login</title>
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
    <script src="../scripts/ARC/javascriptBrowserDetection.js" type="text/javascript"></script>
    <script type="text/javascript">
      var warningMessage = "Sorry, your browser is not officially supported. " 
        + "Only Internet Explorer 6.0 or newer and Firefox 1.5 and newer " 
        + "are supported. Do you want to continue?";
      var redirectionPage = "../modelbank/unprotected/browsersDownload.html";
    </script>
    <script src="../scripts/ARC/unsupportedBrowserCheck.js" type="text/javascript" ></script>
  
</head>

<body>

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
<form>

<table width="100%" border="0">

  <tr></tr>

  <tr>
	<td colspan="2"></td>
  </tr>

  <tr>
	<td class="information">The T24 Update Service has encountered a Problem.  We are sorry for the inconvenience.</td>
  </tr>

  <tr></tr>

  <tr>
	<td colspan="3" class="information">To continue please close this window and re-visit the link you used to enter the T24 Update Service site.</td>
  </tr> 

  <tr></tr>

  <tr>
	<td colspan="3" class="information">However, if this problem persists please report it to the Temenos Helpdesk where they will be able to assist you.</td>
  </tr>     

  <tr>
	<td colspan="2"></td>
  </tr>

</table>

<!-- Error message -->
<p class="instruction" style="font-weight: bold;color:#ff0000">
	<xsl:value-of select="/responseDetails/login/error"/>
</p>

</form>
</td>
</tr>
</table>

</body>
</html>

</xsl:template>

</xsl:stylesheet>
