<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Need to add since the window.xsl reference for stylingDetails.xsl will not available when the Passbook Print Commit/Validate happens -->
	<!-- Response starightaway will be processed by this xsl -->
	<xsl:import href="../stylingDetails.xsl"/>

	<xsl:variable name="application" select="string(/responseDetails/window/panes/pane/contract/app)"/>
	<xsl:variable name="version" select="string(/responseDetails/window/panes/pane/contract/version)"/>

	<xsl:variable name="enquiry"> 
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid"/>
			</xsl:when>
			<xsl:when test="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname"/>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="/responseDetails/passbookprintdata/title"/>
				</title>
				<style>
					.report
					{
   					   white-space:pre;
					    FONT-SIZE: 10pt;
					    FONT-FAMILY: Courier New;
					    LINE-HEIGHT: 10pt;
					    VERTICAL-ALIGN: bottom;
					}
				</style>
			</head>	

			<body topmargin="0" leftmargin="0">
				<table cellpadding="0" cellspacing="0">
					<xsl:for-each select="/responseDetails/passbookprintdata/line">
						<tr>
							<td>
								
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style_Local">
										<xsl:with-param name="actualclass" select="'report'"/>
									</xsl:call-template>
								</xsl:attribute>
								
								<pre style="display: inline">
									<xsl:if test=".=''"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;</xsl:if>
									<xsl:if test="not(.='')"><xsl:value-of select="."/></xsl:if>
								</pre>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

	<!-- Wrapper template for custom styling its used when Passbook Print Commit/Validate happens. -->
	<!-- In this particular scenario enquiry name will not be available and window.xsl not been loaded for this response, so use this-->

	<xsl:template match="apply_Style_Local" name="apply_Style_Local">
		<xsl:param name="actualclass"/>
		<xsl:choose>
			<xsl:when test="$application!=''">
				<xsl:call-template name="Version-Styling">   
					<xsl:with-param name="application" select="$application"/>
					<xsl:with-param name="version" select="$version"/>
					<xsl:with-param name="actualclass" select="$actualclass"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="Enquiry-Styling">
					<xsl:with-param name="enquiry" select="$enquiry"/>
					<xsl:with-param name="actualclass" select="$actualclass"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>