<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="userDetails.xsl"/>
<xsl:import href="tableBorders.xsl"/>

<xsl:template match="/">
	<html>
		<head>
			<!-- Include the required stylesheets - using a skin version if specified -->
			<link rel="stylesheet" type="text/css">
				<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
			</link>

			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

			<script language="JavaScript1.2" src="../scripts/general.js"/>
		</head>
		<body onLoad="javascript:setWindowStatus()">
			<xsl:apply-templates/>
  		</body>
	  </html>
</xsl:template>
	
<xsl:template match="messages" name="messages_n">
	<!-- Where a title is handed down, use this. Usually means an error -->
	<xsl:if test="title!=''">
		<title>
			<xsl:value-of select="title"/>
		</title>
	</xsl:if>

	<!-- Process User Details -->
	<xsl:call-template name="userDetails"/>
		
	<!-- If there is at least one message then display the tab header -->
	<xsl:if test="msg!=''">
		<!--- Start of tab header-->
		<table id="messages" cellpadding="0" cellspacing="0" border="0">

			<tr>

				<td class="caption" colspan="3">
							<xsl:value-of select="title"/>
				</td>

				<!-- end of tab header-->
			</tr>
			<tr>
				<td/>
				<td>
					<!-- Display any message headers-->
					<table>
						<xsl:for-each select="msgHeader">
							<tr>
								<td class="message">
									<xsl:value-of select="."/>
								</td>
							</tr><tr/>
						</xsl:for-each>
					</table>

					<!-- Display all messages in a table -->
					<table>
						<xsl:for-each select="msg">
							<tr>
								<td class="message">
									<xsl:value-of select="."/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					
					<!-- Display table IO information -->
					<table style="width:100%">
						<xsl:if test="ios!=''">
							  <xsl:if test="ios/tbl!=''">
								<tr>
									<td class="message">
										<b>Table Name</b>
									</td>
									<td style="text-align:center">
										<b>Accessed</b>
									</td>
								</tr>
								<xsl:for-each select="ios">
									<tr>
										<td class="message">
											<xsl:value-of select="tbl"/>
										</td>
										<td style="text-align:center">
											<xsl:value-of select="accessed"/>
										</td>
									</tr>
								</xsl:for-each>
							 </xsl:if>
						</xsl:if>
					</table>

					<!-- Display any message footers - including those with hyperlinks -->
					<table>
						<xsl:for-each select="msgFooter">
							<tr/>
							<tr/>
							<xsl:for-each select="link">
								<tr>
									<td class="message">
										<xsl:value-of select="text"/>
									</td>
								</tr>
								<tr>
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
				</td>
				<td/>
			</tr>

		</table>
	</xsl:if>

</xsl:template>

</xsl:stylesheet>
