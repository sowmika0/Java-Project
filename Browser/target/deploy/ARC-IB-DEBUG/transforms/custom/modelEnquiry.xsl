<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../userDetails.xsl"/>
<xsl:import href="../errorMessage.xsl"/>

	<xsl:template match="/">
		<html>
		<head>
			<title>
				
			<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/title" />
			</title>
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/general.js</xsl:attribute></script>
			<script type="text/javascript" > <xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/Deal.js</xsl:attribute></script>
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/enquiry.js</xsl:attribute></script>
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/request.js</xsl:attribute></script>
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/menu.js</xsl:attribute></script>			                
			<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
			<script type="text/javascript" src="../scripts/ARC/Logger.js" />
			<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
			<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />				                
			<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />
		
		</head>
		
			<body bgColor="#e0ebea" topmargin="2" leftmargin="2">
			
				
			
				<xsl:variable name="formFragmentSuffix">
					<xsl:choose>
						<xsl:when test="/responseDetails/userDetails/fragmentName!=''">_<xsl:value-of select="/responseDetails/userDetails/fragmentName"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
						
				<xsl:variable name="dropfield">
					<xsl:choose>
						<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/df">
							<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/df"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
			
				<xsl:if test="/responseDetails/window/init!=''">
					<xsl:attribute name="onload"><xsl:value-of select="/responseDetails/window/init"/></xsl:attribute>
				</xsl:if>
				
				<TABLE cellSpacing="0" cellPadding="0">
					<table>
						<tr>
							<!-- extract the header information -->
							<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/header">
								<xsl:for-each select="r">
									<tr>
										<xsl:for-each select="c">
											<td>
												<b>
													<font color="#FF8400">
														<xsl:value-of select="." />
													</font>
												</b>
											</td>
										</xsl:for-each>
									</tr>
								</xsl:for-each>
							</xsl:for-each>
						</tr>
						
						
						<!-- extract the row data -->
						<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/r">
							<xsl:choose>
								<xsl:when test="position() mod 2 = 0">
									<td>
										<tr>
											<xsl:for-each select="c">
												<td bgColor="white">
													<xsl:value-of select="cap" />
													<pre>
														<xsl:value-of select="rpt"/>
													</pre>
												</td>
											</xsl:for-each>
										</tr>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td>
										<tr>
											<xsl:for-each select="c">
												<td bgColor="#e0ebea">
													<xsl:value-of select="cap" />
													<pre>
														<xsl:value-of select="rpt"/>
													</pre>
												</td>
											</xsl:for-each>
										</tr>
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</table>
				</TABLE>
				
				<form name="enquiry" id="enquiry" method="POST" action="BrowserServlet">
					<input type="hidden" name="enqid" id="enqid">
						<xsl:attribute name="value"><xsl:value-of select="enqid"/></xsl:attribute>
					</input>
					<input type="hidden" name="dropfield" id="dropfield">
						<xsl:attribute name="value"><xsl:copy-of select="$dropfield"/></xsl:attribute>
					</input>
					<input type="hidden" name="previousEnqs" id="previousEnqs">
						<xsl:attribute name="value"><xsl:value-of select="previousEnqs"/></xsl:attribute>
					</input>
					<input type="hidden" name="previousEnqTitles" id="previousEnqTitles">
						<xsl:attribute name="value"><xsl:value-of select="previousEnqTitles"/></xsl:attribute>
					</input>
					<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/control/ty='PRINT.DEAL.SLIP')and	(/responseDetails/window/panes/pane/dataSection/enqResponse/control/printLocation='LOCAL')">
						<input type="hidden" name="dealSlipAction" id="dealSlipAction" value="PRINT.DEAL.SLIP"/>
					</xsl:if>
				</form>
				<xsl:call-template name="generalForm"/>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
