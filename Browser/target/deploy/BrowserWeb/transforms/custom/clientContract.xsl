<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<title>
					<xsl:value-of select="/clientResponseDetails/window/panes/pane/contract/app" /><xsl:value-of select="/clientResponseDetails/window/panes/pane/contract/version" />
			</title>
			<body topmargin="2" leftmargin="2">
			  <table>
			  	<tr>
			  		<td><font color="#FF8400">Contract data for <xsl:value-of select="/clientResponseDetails/window/panes/pane/contract/app" /></font></td>
			  	</tr>
			  	<tr>
			  		<td>
						<table cellSpacing="0" cellPadding="0">
							<tr>
								<!-- The header -->
								<td><font color="#FF8400">Field</font></td><td><font color="#FF8400">Value</font></td>
							</tr>
								
							<!-- build up all the fields from the main tab -->
							<xsl:for-each select="/clientResponseDetails/window/panes/pane/contract/tabData/mainTab">
								<xsl:call-template name="buildRow"/>
							</xsl:for-each>
								
							<!-- build up all the fields from the tabs -->
							<xsl:for-each select="/clientResponseDetails/window/panes/pane/contract/tabData/tab">
								<xsl:call-template name="buildRow"/>
							</xsl:for-each>
						</table>
					</td>
				</tr>
			  </table>
			</body>
		</html>
	</xsl:template>
	
	
	<!-- extracts out the field name and value and adds them to a row in a table. -->
	<xsl:template name="buildRow">
	    <!-- for each row -->
		<xsl:for-each select="r">
			<tr>
				<!-- run through each cell; ce represents the node cell -->
				<xsl:for-each select="ce">
					<!-- check to see if type is equal to input -->
					<xsl:if test="ty='input'">
						<td>
							<b>
								<!-- fn represents the node 'fieldname'-->
								<xsl:value-of select="fn" />
							</b>
						</td>
						<td>
							<b>
								<!-- v represents the node 'value' -->
								<xsl:value-of select="v" />
							</b>
						</td>
					</xsl:if>
				</xsl:for-each>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>