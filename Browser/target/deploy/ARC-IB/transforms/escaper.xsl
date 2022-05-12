<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="escape-apos">
		<xsl:variable name="mne" select='"\"'/>
		<xsl:variable name="mnemonic" select="string(/responseDetails/userDetails/mnemonic)"/>
		<xsl:param name="string"/>
		<xsl:variable name="AccVal">
			<xsl:value-of select="substring-before($string, $mne)"/>
		</xsl:variable>
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
			<!-- check account is internal and add corresponding mnemonic to acc.. -->
			<xsl:when test="(contains($string,$mne))and(string(number(substring($string,1,3)))='NaN') and (not(number (string-length ($AccVal) &gt;= 16))) ">
				<xsl:variable name="mnemo">
					<xsl:value-of select="substring-after($string, $mne)"/>
				</xsl:variable>
				<xsl:choose>
				<!-- if internal acc belongs to same company,not to add mnemonic info.. -->
					<xsl:when test="$mnemonic != $mnemo ">
						<xsl:value-of select="substring-before($string, $mne)"/>
						<xsl:text>\\</xsl:text>
						<xsl:value-of select="substring-after($string, $mne)"/>
					</xsl:when>  
					<xsl:otherwise> 
						<xsl:value-of select="substring-before($string, $mne)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- For customer account,only acc info has to provide.. -->
			<xsl:when test="(contains($string, $mne))and(string(number(substring($string,1,3))) !='NaN')">
				<xsl:value-of select="substring-before($string, $mne)"/>
			</xsl:when>
			<!-- otherwise... -->
			<xsl:otherwise>
				<!-- ... just give the value of the string -->
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
