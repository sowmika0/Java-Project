<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- import all of the child items here -->
	<xsl:import href="../menu/menu.xsl"/>
	<xsl:import href="../toolbars/toolbars.xsl"/>
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
	<xsl:variable name="imagesDir">
		<xsl:value-of select="/t24help/header/imagesDir"/>
	</xsl:variable>
	<xsl:variable name="user">
		<xsl:value-of select="/t24help/header/user"/>
	</xsl:variable>
	
	<!-- isArc used to check the product is Browser or ARC-B -->
	<!-- We support help text only for browser so we going to define a variable as false -->
	<xsl:variable name="isArc" value='false'/>
	
	<!-- Define menuStyle to avoid topic.xsl from breaking -->
	<xsl:variable name="menuStyle">DEFAULT</xsl:variable>
	
	<xsl:variable name="formFragmentSuffix">
		<xsl:choose>
			<xsl:when test="responseDetails/webDetails/WS_FragmentName!=''">_<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

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
				<xsl:if test="/t24help/header/init!=''">
					<xsl:attribute name="onload"><xsl:value-of select="/t24help/header/init"/></xsl:attribute>
				</xsl:if>
				<form name="help" method="POST" action="HelpServlet">

                    <!-- Add the id, with fragment suffix for noFrames mode ....-->
                    <xsl:attribute name="id">help</xsl:attribute>

					<input type="hidden" name="requestType" value=""/>
					<input type="hidden" name="command" value=""/>
					<input type="hidden" name="language">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/language"/></xsl:attribute>
					</input>
					<input type="hidden" name="product">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/product"/></xsl:attribute>
					</input>
					<input type="hidden" name="application">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/application"/></xsl:attribute>
					</input>
					<input type="hidden" name="path">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/path"/></xsl:attribute>
					</input>
					<xsl:for-each select="/t24help/header/toolbars">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
					<table>
						<tbody>
							<tr>
								<td colspan="2">
									<table class="colour0">
										<tr>
											<td>
												<b>
													<xsl:value-of select="/t24help/header/table"/>
												</b>
											</td>
										</tr>
										<xsl:for-each select="/t24help/overview/ovdesc">
											<xsl:call-template name="processContent"/>
										</xsl:for-each>
										<!-- Now display any image -->
										<xsl:variable name="ovimage">
											<xsl:value-of select="/t24help/overview/ovimage"/>
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="normalize-space($ovimage) != ''">
												<tr>
													<td>
														<xsl:if test="(substring($imagesDir, 2, 1) = ':')">
															<!-- DOS full path name with a drive letter -->
															<img border="0">
																<xsl:attribute name="src"><xsl:copy-of select="$imagesDir"/>/<xsl:value-of select="/t24help/overview/ovimage"/></xsl:attribute>
															</img>
														</xsl:if>
														<xsl:if test="(substring($imagesDir, 2, 1) != ':')">
															<!-- Unix path name relative to the web server root directory -->
															<img border="0">
																<xsl:attribute name="src">../<xsl:copy-of select="$imagesDir"/>/<xsl:value-of select="/t24help/overview/ovimage"/></xsl:attribute>
															</img>
														</xsl:if>
													</td>
												</tr>
											</xsl:when>
										</xsl:choose>
									</table>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<xsl:for-each select="/t24help/menu">
										<xsl:if test="t/field!=''">
											<xsl:apply-templates select="." mode="menu"></xsl:apply-templates>
										</xsl:if>
									</xsl:for-each>
									<xsl:if test="/t24help/header/common='no'">
										<xsl:if test="/t24help/header/displaymenu='true'">
											<table class="colour1" width="100%">
												<tr>
													<td>
														<a onclick="javascript:viewCommonHelpFields()" href="javascript:void(0)">
															<b>T24 Common HelpText Fields</b>
														</a>
													</td>
												</tr>
											</table>
										</xsl:if>
									</xsl:if>
								</td>
								<td>
									<table>
										<xsl:choose>
											<xsl:when test="/t24help/header/sort='true'">
												<!-- Display the fields sorted - using the 'cap' value if supplied first -->
												<xsl:for-each select="/t24help/menu/t">
													<xsl:sort select="cap"/>
													<xsl:sort select="field"/>
													<xsl:apply-templates select="." mode="field">
													</xsl:apply-templates>
												</xsl:for-each>
											</xsl:when>
											<xsl:otherwise>
												<!-- Display the fields unsorted - in the order in which they are in the XML document -->
												<xsl:for-each select="/t24help/menu/t">
													<xsl:apply-templates select="." mode="field">
													</xsl:apply-templates>
												</xsl:for-each>
											</xsl:otherwise>
										</xsl:choose>
									</table>
									<xsl:if test="/t24help/menu/t != ''">
										<xsl:if test="/t24help/header/common='no'">
											<xsl:if test="/t24help/header/displaymenu='true'">
												<table width="100%">
													<tr class="colour1">
														<a onclick="javascript:viewCommonHelpFields()" href="javascript:void(0)">
															<b>T24 Common HelpText Fields</b>
														</a>
													</tr>
												</table>
											</xsl:if>
										</xsl:if>
									</xsl:if>
								</td>
							</tr>
						</tbody>
					</table>
				</form>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="toolbars">
		<xsl:call-template name="toolbars_n"/>
	</xsl:template>
	
	<xsl:template name="processContent">
		<xsl:for-each select="node()">
			<xsl:if test="name()='p'">
				<tr>
					<td>
						<!--<xsl:value-of select="."/>-->
						<xsl:copy-of select="."/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="name()='table'">
				<tr>
					<td>
						<table border="3" align="center">
							<xsl:for-each select="tr">
								<tr>
									<xsl:for-each select="td">
										<td>
											<xsl:if test=".=''"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;</xsl:if>
											<xsl:if test="not(.='')"><xsl:value-of select="."/></xsl:if>
										</td>
									</xsl:for-each>
								</tr>
							</xsl:for-each>
						</table>
					</td>
				</tr>
			</xsl:if>
		</xsl:for-each>	
	</xsl:template>	

	<!-- Display the topics (menus) as a sorted list in a menu on the left hand side of the screen -->
	<xsl:template match="menu" mode="menu">
		<xsl:if test="/t24help/header/displaymenu='true'">
			<xsl:call-template name="menu_n"/>
		</xsl:if>
	</xsl:template>

	<!-- Display the topics (field details) as list with descriptions on the right hand side of the screen -->	
	<xsl:template match="t" mode="field">
		<xsl:if test="field!=''">
			<tr>
				<td>
					<table class="colour{position() mod 2}" width="100%">
						<!-- Now display the field name or caption if supplied -->
						<tr>
							<td>
								<a>
									<xsl:attribute name="name"><xsl:value-of select="field"/></xsl:attribute>
									<xsl:attribute name="id">fieldName:<xsl:value-of select="field"/></xsl:attribute>
									<b>
										<!--  Use the field caption in preference to the field name -->
										<xsl:choose>
											<xsl:when test="cap!=''">
												<xsl:value-of select="cap"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="field"/>
											</xsl:otherwise>
										</xsl:choose>
									</b>
								</a>
							</td>
						</tr>
						<!-- Now display the description lines -->
						<xsl:for-each select="desc">
							<!-- Get the node and format it depending on it's type -->
							<xsl:call-template name="processContent"/>
						</xsl:for-each>
						<!-- Now display any image -->
						<xsl:variable name="fldimage">
							<xsl:value-of select="image"/>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="normalize-space($fldimage) != ''">
								<tr>
									<td>
										<xsl:if test="(substring($imagesDir, 2, 1) = ':')">
											<!-- DOS full path name with a drive letter -->
											<img border="0">
												<xsl:attribute name="src"><xsl:copy-of select="$imagesDir"/>/<xsl:value-of select="image"/></xsl:attribute>
											</img>
										</xsl:if>
										<xsl:if test="(substring($imagesDir, 2, 1) != ':')">
											<!-- Unix path name relative to the web server root directory -->
											<img border="0">
												<xsl:attribute name="src">../<xsl:copy-of select="$imagesDir"/>/<xsl:value-of select="image"/></xsl:attribute>
											</img>
										</xsl:if>
									</td>
								</tr>
							</xsl:when>
						</xsl:choose>
					</table>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
