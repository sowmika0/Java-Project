<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="messages.xsl" />
	<xsl:import href="tableBorders.xsl"/>
	
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	
	<!-- Define menuStyle to avoid topic.xsl from breaking -->
	<xsl:variable name="menuStyle">DEFAULT</xsl:variable>
	<xsl:variable name="formFragmentSuffix" select="''"/>
	<xsl:variable name="noOfOverrides" select="0"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/menu.css</xsl:attribute>
				</link>

				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

				<xsl:choose>
					<!-- If obfuscation is switched on -->
					<xsl:when test="/responseDetails/userDetails/obfuscate">
						<xsl:choose>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='both'">
								<script type="text/javascript" src="../scripts/version/version.js" />
								<script type="text/javascript" src="../scripts/ARC/Logger.js" />
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/all.js</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
								<script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>	
							</xsl:when>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='external'">
								<script type="text/javascript" src="../scripts/version/version.js" />
								<script type="text/javascript" src="../scripts/ARC/Logger.js" />
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/all.js</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
								<script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>	
							</xsl:when>
							<!-- internal -->
							<xsl:otherwise>
								<script type="text/javascript" src="../scripts/obfuscated/general.js"/>
								<script type="text/javascript" src="../scripts/obfuscated/menu.js"/>
								<script type="text/javascript" src="../scripts/obfuscated/dropdown.js"/>
								<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
								<script type="text/javascript" src="../scripts/ARC/Logger.js" />
								<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
								<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />				                
								<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />
								<script type="text/javascript" src="../scripts/version/version.js" />				
								<script type="text/javascript" src="../scripts/obfuscated/enquiry.js"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<script type="text/javascript" src="../scripts/general.js"/>
						<script type="text/javascript" src="../scripts/menu.js"/>
						<script type="text/javascript" src="../scripts/dropdown.js"/>
						<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
						<script type="text/javascript" src="../scripts/ARC/Logger.js" />
						<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
						<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />				                
						<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />
						<script type="text/javascript" src="../scripts/version/version.js" />				
						<script type="text/javascript" src="../scripts/enquiry.js"/>
					</xsl:otherwise>
				</xsl:choose>				
				
				<title>
					<xsl:for-each select="responseDetails/att/headers">
						<xsl:value-of select="title"/>
					</xsl:for-each>
				</title>
			</head>
			
			
			<body onLoad="javascript:setWindowStatus()">
			
			<xsl:for-each select="responseDetails/att">
				<xsl:if test="messages!=''">			
					<div class="display_box">
						<xsl:for-each select="messages">
							<xsl:apply-templates select="."/>
						</xsl:for-each>
					</div>
				</xsl:if>
			</xsl:for-each>
			
				
			<form name="attachment" method="POST" ENCTYPE="multipart/form-data" action="UploadServlet">

                    <!-- Add the id, with fragment suffix for noFrames mode ....-->
                    <xsl:attribute name="id">attachment<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>

					<xsl:for-each select="responseDetails/att/mainbody">
						<input type="hidden" name="filePath">
							<xsl:attribute name="value"><xsl:value-of select="filepath"/></xsl:attribute>
						</input>
						<input type="hidden" name="imageId">
							<xsl:attribute name="value"><xsl:value-of select="imageid"/></xsl:attribute>
						</input>
						<input type="hidden" name="uploadOkMsg">
							<xsl:attribute name="value"><xsl:value-of select="okMsg"/></xsl:attribute>
						</input>
						<input type="hidden" name="skin">
							<xsl:attribute name="value"><xsl:copy-of select="$skin"/></xsl:attribute>						
						</input>
					</xsl:for-each>
					<input type="hidden" name="requestType" value=""/>
					<input type="hidden" name="routineName" value=""/>
					<input type="hidden" name="windowName" value=""/>
					<input type="hidden" name="routineArgs" value=""/>
					<input type="hidden" name="ImDocLoc">
						<xsl:attribute name="value"><xsl:value-of select='responseDetails/att/mainbody/ImDocLoc'/></xsl:attribute>
					</input>
					<!-- Process User Details -->
					<xsl:call-template name="userDetails"/>
					
					<xsl:for-each select="responseDetails/att/mainbody">
						<table id="attachmentSelection" cellSpacing="0" cellPadding="0" border="0">
							<xsl:call-template name="tableBorder_header_top"/>
							<tr>
								<xsl:call-template name="tableBorder_header_left"/>
								<td class="caption" nowrap="nowrap">
									<xsl:attribute name="background">../plaf/images/<xsl:copy-of select="$skin"/>/tab_back.gif</xsl:attribute>
									<table width="100%">
										<tr>
											<td class="caption" nowrap="nowrap">
												<xsl:attribute name="background">../plaf/images/<xsl:copy-of select="$skin"/>/tab_back.gif</xsl:attribute>
												<b>File Upload</b>
											</td>
											<td align="right" width="100%">
												<p align="right">
													<a>
														<!-- df represents the node dropfield -->
														<xsl:attribute name="onclick">javascript:fileUpload('<xsl:value-of select="df"/>')</xsl:attribute>
														<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														<img border="0">
															<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/menu/go.gif</xsl:attribute>
														</img>
													</a>
												</p>
											</td>
										</tr>
									</table>
								</td>
								<xsl:call-template name="tableBorder_header_right"/>
							</tr>
							<tr>
								<xsl:call-template name="tableBorder_body_left"/>
								<td bgColor="#ffffff">
									<img height="8" width="1">
										<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
									</img>
									<br/>
									<table>
										<tr>
											<!-- Attachment Field -->
											<td height="25" width="205">
												<INPUT size="35" type="file" name="fileName" tabindex="1"/>
											</td>
										</tr>
									</table>
								</td>
								<xsl:call-template name="tableBorder_body_right"/>
							</tr>
							<xsl:call-template name="tableBorder_body_bottom"/>
						</table>
					</xsl:for-each>
				</form>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="messages">
		<xsl:call-template name="messages_n"/>
	</xsl:template>
</xsl:stylesheet>
