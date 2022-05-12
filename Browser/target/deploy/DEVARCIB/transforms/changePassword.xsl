<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="ARC/T24_constants.xsl"/>
	<xsl:import href="tableBorders.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="responseDetails/title"/>
				</title>
				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/login.css</xsl:attribute>
				</link>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				
				<!-- JavaScript files - the full set -->
				<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
				<xsl:choose>
					<!-- If obfuscation is switched on -->
					<xsl:when test="/responseDetails/userDetails/obfuscate">
						<xsl:choose>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='both'">
								<script type="text/javascript"><xsl:attribute name="src">../scripts/obfuscated/all.js</xsl:attribute></script>
							</xsl:when>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='external'">
								<script type="text/javascript"><xsl:attribute name="src">../scripts/all.js</xsl:attribute></script>
							</xsl:when>
							<!-- internal -->
							<xsl:otherwise>
								<script type="text/javascript"><xsl:attribute name="src">../scripts/obfuscated/general.js</xsl:attribute></script>
								<script type="text/javascript"><xsl:attribute name="src">../scripts/T24_constants.js</xsl:attribute></script>
								<script type="text/javascript"><xsl:attribute name="src">../scripts/ARC/Fragment.js</xsl:attribute></script>
								<script type="text/javascript"><xsl:attribute name="src">../scripts/ARC/FragmentUtil.js</xsl:attribute></script>
								<script type="text/javascript"><xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>

							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<script src="../scripts/general.js"/>
						<script src="../scripts/ARC/T24_constants.js"/>
						<script src="../scripts/ARC/Fragment.js"/>
						<script src="../scripts/ARC/FragmentUtil.js"/>
						<script src="../scripts/version/version.js" />
					</xsl:otherwise>
				</xsl:choose>
				
				<style>
				html,body,form {
					width: 100%;
					height: 100%;
					padding:0px;
					margin:0px;
				}
			</style>
			</head>
			<body onLoad="javascript:initPassword()">
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="responseDetails">
		<form name="changePassword" id="changePassword" method="POST" action="BrowserServlet" AUTOCOMPLETE="OFF">
			<input type="hidden" name="command" value="repeatpassword"/>
			<input type="hidden" name="requestType" value="UTILITY.ROUTINE"/>
			<input type="hidden" name="routineName" value="OS.PASSWORD"/>
			<input type="hidden" name="routineArgs" id="routineArgs">
				<xsl:attribute name="value"><xsl:value-of select="routineArgs"/></xsl:attribute>
			</input>
			<input type="hidden" name="signOnName">
				<xsl:attribute name="value"><xsl:value-of select="signOnName"/></xsl:attribute>
			</input>
			<input type="hidden" name="encSignOnName">
    				<xsl:attribute name="value"><xsl:value-of select="encValue"/></xsl:attribute>
    			</input>			
  			<input type="hidden" name="edno">
                  		<xsl:attribute name="value"><xsl:value-of select="edno"/></xsl:attribute>
            	        </input>

			<div class="display_box">
				<!-- Main form table -->
				<table cellSpacing="0" cellPadding="0" border="0" align="center" style="background-color:#ffffff;">
					<tbody>
						<tr>
							<td class="caption" colspan="99">
								<br/>
							</td>
						</tr>
						<tr>
							<td class="caption" colspan="99">
								<xsl:value-of select="title"/>
							</td>
						</tr>
						<tr>
							<td class="caption" colspan="99">
								<br/>
							</td>
						</tr>
						<!-- Now process the login and password fields -->
						<!-- for each row -->
						<xsl:for-each select="r">
							<tr style="text-align:left;">									<td>
								<!-- run through each cell; ce represents the node cell -->
								<xsl:for-each select="ce">

										<xsl:choose>
											<!-- caption - i.e. the label for a field-->
											<xsl:when test="./c!=''">
												<xsl:value-of select="c"/>
											</xsl:when>
											<!-- Input Box -->
											<!-- ty represents the word 'type'-->
											<xsl:when test="./ty='input'">
												<input type="password" size="20">
													<!-- n represents the node 'name'-->
													<xsl:attribute name="name"><xsl:value-of select="n"/></xsl:attribute>
													<xsl:attribute name="id"><xsl:value-of select="n"/></xsl:attribute>
													<!-- v corresponds to the word value -->
													<xsl:attribute name="value"><xsl:value-of select="v"/></xsl:attribute>
												</input>
											</xsl:when>
										</xsl:choose>
									
								</xsl:for-each>
								<!-- Go button -->

									<xsl:if test="position()=last()">
										<INPUT type="image" id="goButton" halign="right" border="0" onclick="javascript:setPassword()">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/menu/go.gif</xsl:attribute>
										</INPUT>
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
						<!-- Error message -->
						<tr>
							<td class="caption" colspan="99">
								<p align="center">
									<B>
										<FONT color="red">
											<xsl:value-of select="errorMessage"/>
										</FONT>
									</B>
								</p>
								<br/>
								<br/>
							</td>
						</tr>
						<!-- Footer tables -->
						<tr>
							<td class="caption" colspan="99">
								<b>Sign on names and passwords are case sensitive.</b>
							</td>
						</tr>
						<tr>
							<td class="caption" colspan="99">
								<br/>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
