<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="escape-apos">
		<xsl:param name="string"/>
		<!-- create an $apos variable to make it easier to refer to -->
		<xsl:variable name="apos" select='"&apos;"'/>
		<xsl:choose>
			<!-- if the string contains an apostrophe... -->
			<xsl:when test="contains($string, $apos)">
				<!-- ... give the value before the apostrophe... -->
				<xsl:value-of select="substring-before($string, $apos)"/>
				<!-- ... the escaped apostrophe ... -->
				<xsl:text>\'</xsl:text>
				<!-- ... and the result of applying the template to the string after the apostrophe -->
				<xsl:call-template name="escape-apos">
					<xsl:with-param name="string" select="substring-after($string, $apos)"/>
				</xsl:call-template>
			</xsl:when>
			<!-- otherwise... -->
			<xsl:otherwise>
				<!-- ... just give the value of the string -->
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
