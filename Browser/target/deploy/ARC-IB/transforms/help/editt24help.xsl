<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- import all of the child items here -->
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
	<xsl:variable name="user">
		<xsl:value-of select="/t24help/header/user"/>
	</xsl:variable>
	
	<!-- isArc used to check the product is Browser or ARC-B -->
	<!-- We support help text only for browser so we going to define a variable as false -->
	<xsl:variable name="isArc" value='false'/>

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
			<!-- Work out whether we are a frame page or a non-frame page -->
			<body onload="convertTextAreas()"  topmargin="2" leftmargin="2">
				<form name="help" method="POST" onsubmit="javascript:saveHelp('')" action="HelpServlet">

                    <!-- Add the id, with fragment suffix for noFrames mode ....-->
                    <xsl:attribute name="id">help</xsl:attribute>
                

					<input type="hidden" name="requestType" value=""/>
					<input type="hidden" name="command" value=""/>
					<input type="hidden" name="product">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/product"/></xsl:attribute>
					</input>
					<input type="hidden" name="table">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/table"/></xsl:attribute>
					</input>					
					<input type="hidden" name="language">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/language"/></xsl:attribute>
					</input>					
					<input type="hidden" name="path">
						<xsl:attribute name="value"><xsl:value-of select="t24help/header/path"/></xsl:attribute>
					</input>					

					<xsl:for-each select="/t24help/header/toolbars">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
					
					<table>
						<tbody>
							<tr class="colour1">
								<td>Title</td>
								<td>
									<input type="text" size="50" name="title" id="title">
										<xsl:attribute name="value"><xsl:value-of select="t24help/header/title"/></xsl:attribute>
									</input>
								</td>
							</tr>

							<tr class="colour0">
								<td>Language</td>
								<td>
									<xsl:value-of select="/t24help/header/language"/>
								</td>
							</tr>
							
							<tr class="colour1">
								<td>Path</td>
								<td>
									<xsl:value-of select="/t24help/header/path"/>
								</td>
							</tr>
							
							<tr class="colour0">
								<td>Overview</td>
								<td>
									<textarea class="overviewbox" name="ovdesc" id="ovdesc">
										<xsl:value-of select="/t24help/overview/ovdesc"/>
									</textarea>
								</td>
							</tr>
							<tr class="colour0">
								<td><p align="right">Image</p></td>
								<td>
									<input class="imagebox">
										<xsl:attribute name="name">ovimage</xsl:attribute>
										<xsl:attribute name="id">ovimage</xsl:attribute>
										<xsl:attribute name="value"><xsl:value-of select="/t24help/overview/ovimage"/></xsl:attribute>
									</input> 
								</td>
							</tr>
							<xsl:for-each select="/t24help/menu/t">
								<xsl:sort select="field"/>		<!-- Sort the field names alphabetically -->
								<xsl:if test="field!=''">
									<!-- Set a field id variable - used in the parser to match fieldnames, descs and images -->
									<xsl:variable name="fieldid">
										<xsl:value-of select="position()"/>
									</xsl:variable>

									<tr class="colour{position() mod 2}">
										<td>
											<xsl:value-of select="field"/>
										</td>
										<input type="hidden">
											<xsl:attribute name="name">field:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
											<xsl:attribute name="id">field:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
											<xsl:attribute name="value"><xsl:value-of select="field"/></xsl:attribute>
										</input>
										<td>
											<textarea class="fieldbox">
												<xsl:attribute name="name">desc:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
												<xsl:attribute name="id">desc:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
												<xsl:value-of select="desc"/>
											</textarea> 
										</td>
									</tr>
									<tr class="colour{position() mod 2}">
										<td><p align="right">Image</p></td>
										<td>
											<input class="imagebox">
												<xsl:attribute name="name">image:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
												<xsl:attribute name="id">image:<xsl:copy-of select="$fieldid"/>:1:1</xsl:attribute>
												<xsl:attribute name="value"><xsl:value-of select="image"/></xsl:attribute>
											</input> 
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</tbody>
					</table>
				</form>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="toolbars">
		<xsl:call-template name="toolbars_n"/>
	</xsl:template>
	<xsl:template match="p" name="p">
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
