<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			
	<xsl:variable name="enableTabIndex" select="/responseDetails/tabIndex/enableTabIndex" />
	<xsl:variable name="tabIndexForInfoIcon" select="/responseDetails/tabIndex/tabIndexForInfoIcon" />
	<xsl:variable name="tabIndexForDropFields" select="/responseDetails/tabIndex/tabIndexForDropFields" />
	<xsl:variable name="tabIndexForExpansionIcons" select="/responseDetails/tabIndex/tabIndexForExpansionIcons" />
	
	<xsl:template name="tabIndexElements">
		<!-- Add Tab Index Configurations to HTML elements for processing in javascript-->			
		<input type="hidden" name="enableTabIndex" id="enableTabIndex">
			<xsl:attribute name="value">
				<xsl:value-of select="$enableTabIndex"/>
			</xsl:attribute>	
		</input>		
		<input type="hidden" name="tabIndexForInfoIcon" id="tabIndexForInfoIcon">
			<xsl:attribute name="value">
				<xsl:value-of select="$tabIndexForInfoIcon"/>
			</xsl:attribute>	
		</input>
		<input type="hidden" name="tabIndexForDropFields" id="tabIndexForDropFields">
			<xsl:attribute name="value">
				<xsl:value-of select="$tabIndexForDropFields"/>
			</xsl:attribute>	
		</input>
		<input type="hidden" name="tabIndexForExpansionIcons" id="tabIndexForExpansionIcons">
			<xsl:attribute name="value">
				<xsl:value-of select="$tabIndexForExpansionIcons"/>
			</xsl:attribute>	
		</input>		
	</xsl:template>
	
	<!-- Process tab index values based on the type of element -->
	<xsl:template name="processTabIndex">
		<xsl:param name="type"/>		
		<xsl:choose>
			<xsl:when test="$type = 'enableTabIndex'">
				<xsl:call-template name="setTabIndex">
					<xsl:with-param name="enable" select="$enableTabIndex"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$type = 'tabIndexForInfoIcon'">
				<xsl:call-template name="setTabIndex">
					<xsl:with-param name="enable" select="$tabIndexForInfoIcon"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$type = 'tabIndexForDropFields'">
				<xsl:call-template name="setTabIndex">
					<xsl:with-param name="enable" select="$tabIndexForDropFields"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$type = 'tabIndexForExpansionIcons'">
				<xsl:call-template name="setTabIndex">
					<xsl:with-param name="enable" select="$tabIndexForExpansionIcons"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>		
	</xsl:template>	
	
	<!-- Add Tab attribute to HTML element based on parameter value -->
	<xsl:template name="setTabIndex">
		<xsl:param name="enable"/>		
		<xsl:choose>
			<xsl:when test="$enable = 'NO'">
				<xsl:attribute name="tabindex">-1</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="tabindex">0</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>	
	</xsl:template>	
</xsl:stylesheet>