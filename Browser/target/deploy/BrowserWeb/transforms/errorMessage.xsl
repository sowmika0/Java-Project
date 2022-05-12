<?xml version="1.0" encoding="UTF-8"?>
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="messages.xsl" />
<xsl:import href="generalForm.xsl" />

<!-- Extract the Skin name for identifying CSS and Images directory -->
<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>

<xsl:variable name="formFragmentSuffix">
    <xsl:choose>
        <xsl:when test="responseDetails/userDetails/fragmentName!=''">_<xsl:value-of select="/responseDetails/userDetails/fragmentName"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
    </xsl:choose>
</xsl:variable>
	
	<xsl:variable name="multiPane">
	<xsl:choose>
		<xsl:when test="count(responseDetails/window/panes/pane) > 1">true</xsl:when>
		<xsl:otherwise>false</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:template match="/">
	<html>
		<head>
			<!-- Include the required stylesheets - using a skin version if specified -->
			<link rel="stylesheet" type="text/css">
				<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
			</link>

			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			
			<script src="../scripts/general.js"/>
			<script src="../scripts/help.js"/>
			<script src="../scripts/version/version.js" />
			<script src="../scripts/ARC/Logger.js" />
			<script src="../scripts/ARC/Fragment.js" />
			<script src="../scripts/ARC/FragmentUtil.js" />  
		</head>
		
		<body onLoad="javascript:setWindowStatus()" topmargin="2" leftmargin="2">
			<div class="display_box">
				<xsl:for-each select="responseDetails/messages">
					<xsl:apply-templates select="."/>
				</xsl:for-each>
				<!-- A general form for submitting requests -->
				<xsl:call-template name="generalForm"/>
			</div>
		</body>
	
	</html>
</xsl:template>

<xsl:template match="messages">
		<xsl:call-template name="messages_n"/>
</xsl:template>

</xsl:stylesheet>
