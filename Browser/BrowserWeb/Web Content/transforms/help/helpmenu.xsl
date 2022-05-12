<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- import all of the child items here -->
	<xsl:import href="../menu/menu.xsl"/>
	<xsl:import href="../menu/categorymenu.xsl"/>
	<xsl:import href="../tableBorders.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">default</xsl:variable>
	<xsl:variable name="path">../plaf/style/</xsl:variable>
	
	<xsl:variable name="atoolcompid">
		<xsl:value-of select="/t24help/userDetails/companyId"/>
	</xsl:variable>
	<xsl:variable name="compScreen">
		<xsl:value-of select="/t24help/userDetails/compScreen"/>
	</xsl:variable>
	<xsl:variable name="contextRoot">
		<xsl:value-of select="/t24help/contextRoot"/>
	</xsl:variable>

	<!-- Define menuStyle to avoid topic.xsl from breaking -->
	<xsl:variable name="menuStyle">DEFAULT</xsl:variable>
	<!-- Define formFragmentSuffix for topics.xsl -->
    <xsl:variable name="formFragmentSuffix" select="''"/>
	
	<xsl:variable name="noOfOverrides" select="0"/>

	<xsl:template match="/">
		<xsl:if test="/t24help/menu/t!=''">
			<xsl:variable name="path">../../../plaf/style/</xsl:variable>
		</xsl:if>
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css" href="{$path}{$skin}/general.css"></link>
				<link rel="stylesheet" type="text/css" href="{$path}{$skin}/custom.css"></link>

				<script type="text/javascript" src="../scripts/general.js"></script>
				<script type="text/javascript" src="../scripts/help.js"></script>
				<script type="text/javascript" src="../scripts/menu.js"></script>

				<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
				<script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
				<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js"></script>				

				<!-- Set a default title if one has not been supplied for whatever reason -->
				<title>
					<xsl:choose>
						<xsl:when test="t24help/header/title!=''">
							<xsl:value-of select="t24help/header/title"/>
						</xsl:when>
						<xsl:otherwise>Temenos T24</xsl:otherwise>
					</xsl:choose>
				</title>
			</head>
			<!-- The body of our help document goes here -->
			<body topmargin="2" leftmargin="2">
				<form name="help" method="POST" action="HelpServlet">	

                    <!-- Add the id, with fragment suffix for noFrames mode ....-->
                    <xsl:attribute name="id">help</xsl:attribute>

					<input type="hidden" name="language">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/language"/></xsl:attribute>
					</input>	
					<xsl:choose>
					<xsl:when test="t24help/header/title!=''">
							<table>
								<tbody>
									<tr>
							<td><xsl:value-of select="t24help/header/title"/></td>
									</tr>
								</tbody>
							</table>
						</xsl:when>
					</xsl:choose>

					<xsl:for-each select="/t24help/menu">
							<xsl:apply-templates select="."/>
					</xsl:for-each>
					<xsl:for-each select="/t24help/categorymenu">
							<xsl:apply-templates select="."/>
					</xsl:for-each>

				</form>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="menu">
		<xsl:call-template name="menu_n"/>
	</xsl:template>
	<xsl:template match="categorymenu">
		<xsl:call-template name="categorymenu_n"/>
	</xsl:template>

</xsl:stylesheet>
