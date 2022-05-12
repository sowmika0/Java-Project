<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="../userDetails.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/t24help/userDetails/skin"/>
	</xsl:variable>
	
	<xsl:variable name="noOfOverrides" select="0"/>

<xsl:template match="/">
	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

			<!-- Include the required stylesheets - using a skin version if specified -->
			<link rel="stylesheet" type="text/css" href="../plaf/style/{$skin}/general.css"></link>
			<link rel="stylesheet" type="text/css" href="../plaf/style/{$skin}/custom.css"></link>

			<script type="text/javascript" src="../scripts/general.js"></script>
			<script type="text/javascript" src="../scripts/help.js"></script>
			<script type="text/javascript" src="../scripts/menu.js"></script>

			<script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
			<script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
			<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js"></script>				
			
			<title>
				<xsl:value-of select="t24help/messages/title"></xsl:value-of>
			</title>
		</head>
		<body onLoad="javascript:setWindowStatus()" style="MARGIN: 5px" vLink="#213a7d" aLink="#213a7d" link="#213a7d" bgColor="#b1c4de"
		leftMargin="5" topMargin="5" marginheight="5" marginwidth="5">
		
			<form name="help" method="POST" action="HelpServlet">

                <!-- Add the id, with fragment suffix for noFrames mode ....-->
                <xsl:attribute name="id">help</xsl:attribute>

				<input type="hidden" name="requestType" value=""/>
				<input type="hidden" name="command" value=""/>
				<input type="hidden" name="path">
					<xsl:attribute name="value"><xsl:value-of select="t24help/header/path"/></xsl:attribute>
				</input>					
				<input type="hidden" name="language">
					<xsl:attribute name="value"><xsl:value-of select="t24help/header/language"/></xsl:attribute>
				</input>					
				<input type="hidden" name="product">
					<xsl:attribute name="value"><xsl:value-of select="t24help/header/product"/></xsl:attribute>
				</input>
				<input type="hidden" name="application">
					<xsl:attribute name="value"><xsl:value-of select="t24help/header/application"/></xsl:attribute>
				</input>
		
				<xsl:for-each select="t24help/messages">
					<xsl:apply-templates select="."/>
				</xsl:for-each>
			</form>

		</body>
	  </html>
</xsl:template>
	
<xsl:template match="messages" name="messages_n">

	<table align="center" border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#5e83b6">
				<tr>
					<td height="20">
						<font face="Arial" color="#ffffff" size="3">
							<xsl:value-of select="title"/>
						</font>
					</td>
				</tr>
				<tr/>
				<!-- Display any message headers -->
				<xsl:for-each select="msgHeader">
					<tr>
						<td>
							<font face="Arial" color="#ffffff" size="2">
								<xsl:value-of select="."/>
							</font>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</tr>
		<tr>
			<td width="100%" bgcolor="#5e83b6">
				<div align="center">
					<center>
						<table border="0" width="100%" cellpadding="2" bgcolor="#ffffff">
							<!-- Display any message text -->
							<xsl:for-each select="msg">
								<tr>
									<td width="63%">
										<font face="Arial" color="#213a7d" size="2">
											<xsl:value-of select="."/>
										</font>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</center>
				</div>
			</td>
		</tr>
		
		<tr>
			<td width="100%" bgcolor="#5e83b6">
				<div align="center">
					<center>
						<table border="0" width="100%" cellpadding="2" bgcolor="#ffffff">
							<!-- Display any messages that have a image link -->
							<xsl:for-each select="msgData/msgLink">
								<tr>
									<td width="1">
										<xsl:if test="img!=''">
											<a>
												<xsl:attribute name="href"><xsl:value-of select="script"/></xsl:attribute>
												<xsl:attribute name="tabindex">-1</xsl:attribute>
												<img valign="bottom" border="0">
													<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/><xsl:value-of select="img"/></xsl:attribute>
													<xsl:attribute name="alt"><xsl:value-of select="tip"/></xsl:attribute>
													<xsl:attribute name="title"><xsl:value-of select="tip"/></xsl:attribute>
												</img>
											</a>
										</xsl:if>
									</td>
									<td>
										<font face="Arial" color="#213a7d" size="2">
											<xsl:value-of select="caption"/>
										</font>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</center>
				</div>
			</td>
		</tr>

		<tr>
			<td width="100%" height="5%"></td>
		</tr>
		<tr valign="bottom">
			<table border="0" width="100%" bgColor="#b1c4de" cellspacing="1">
				<!-- Display any message footers -->
				<xsl:for-each select="msgFooter">
					<tr/>
					<xsl:for-each select="link">
						<tr bgColor="#b1c4de">
							<font face="Arial" color="#213a7d" size="2">
								<xsl:value-of select="text"/>
							</font>
						</tr>
						<tr bgColor="#b1c4de">
							<td>
								<a>
									<xsl:attribute name="href"><xsl:value-of select="script" /></xsl:attribute>
									<xsl:value-of select="caption" />
								</a>
							</td>
						</tr>
					</xsl:for-each>
				</xsl:for-each>
			</table>
		</tr>
		<tr>
			<td width="100%" height="10%"></td>
		</tr>
		<tr>
			<td width="100%">
				<table border="0" width="100%" bgcolor="#5e83b6" cellspacing="1">
					<tr>
						<td width="100%" valign="bottom"><font face="Arial" size="1" color="#e8ebf2">Copyright 
								© 2004 TEMENOS</font></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</xsl:template>

</xsl:stylesheet>
