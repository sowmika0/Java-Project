<?xml version="1.0" encoding="UTF-8"?>
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="ARC/T24_constants.xsl"/>
<xsl:import href="messages.xsl" />
<xsl:import href="userDetails.xsl" />


<!-- Extract the Skin name for identifying CSS and Images directory -->
<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>

<xsl:template match="/">
	<html>
		<head>
			<!-- Include the required stylesheets - using a skin version if specified -->
			<link rel="stylesheet" type="text/css">
				<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
			</link>
			
			<script src="../scripts/general.js"/>
			<script src="../scripts/Deal.js"/>
			<script src="../scripts/version/version.js" />

			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		</head>
		
		<body topmargin="2" leftmargin="2">
		    <xsl:choose>
		    	<!-- When the response is currently polling -->
				<xsl:when test="responseDetails/messages/poll/doPoll='true'">
					<xsl:attribute name="onLoad">javascript:startPoll()</xsl:attribute>
					<form name="poll" method="POST" action="BrowserServlet" >

                        <!-- Add the id, with fragment suffix for noFrames mode ....-->
                        <xsl:attribute name="id">poll<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>

						<!-- The command to re-send to the API -->
						<xsl:call-template name="userDetails"/>
						<input type="hidden" name="apiArgument">
							<xsl:attribute name="value"><xsl:value-of select="/responseDetails/messages/poll/command"/></xsl:attribute>
						</input>
						<!-- the time to wait before next poll -->
						<input type="hidden" name="time">
							<xsl:attribute name="value"><xsl:value-of select="/responseDetails/messages/poll/time"/></xsl:attribute>
						</input>	
						<input type="hidden" name="requestType" value = "{$_API__REQUEST_}"/>
						<input type="hidden" name="command" value = "globuscommand"/>	
					</form>
				</xsl:when>
			</xsl:choose>
			<xsl:for-each select="responseDetails/messages">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</body>
	
	</html>
</xsl:template>

<xsl:template match="messages">
		<xsl:call-template name="messages_n"/>
</xsl:template>

</xsl:stylesheet>
