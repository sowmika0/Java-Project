<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<script LANGUAGE="javascript">
					//<![CDATA[
						function getLogin()
						{
							var _winDefs= "toolbar=no, menubar=no, status=yes, resizable=yes, scrollbars=yes, height=500, width=800, screenX=0, screenY=0"; 
							window.open("..//html/processing.html", "_parent", _winDefs );
							window.document.login.target = "_parent";
							window.document.login.submit();
						} 
					//]]>
				</script>
			</head>

			<body onload="javascript:getLogin();">

				<form name="login" method="post" action="BrowserServlet">
				
					<input type="hidden" name="command">
						<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/command"/></xsl:attribute>
					</input>
					
					<input type="hidden" name="signOnName">
						<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/signOnName"/></xsl:attribute>
					</input>

					<input type="hidden" name="signOnNameEnabled">
						<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/signOnNameEnabled"/></xsl:attribute>
					</input>
	
					<input type="hidden" name="error">
						<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/error"/></xsl:attribute>
					</input>
				</form>
				
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
