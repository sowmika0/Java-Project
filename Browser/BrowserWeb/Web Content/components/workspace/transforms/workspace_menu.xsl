<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template name="pane_menu">
		<table class="workspacemenu" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;"> 
			<tr height="30px">
				<xsl:for-each select="ts">
					<xsl:if test="position() &lt; 5">
						<td width="25%">
							<xsl:attribute name="id">themenutab<xsl:value-of select="position()"/></xsl:attribute>
							<xsl:attribute name="class">menutab sub<xsl:value-of select="position()"/>a<xsl:value-of select="position()"/></xsl:attribute>
							<xsl:attribute name="onclick">javascript:showmenutab('<xsl:value-of select="position()"/>')</xsl:attribute>
							<div style="wisth100px;overflow:hidden;">  <div class="roundtop"><img src="../components/widgets/menu/images/tl.gif" alt="" 
	 width="15" height="15" class="corner" 
	 style="display: none" />
   </div>
							<xsl:value-of select="@c"/></div>
						</td>
					</xsl:if>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="ts">
				<xsl:if test="position() &lt; 5">
					<xsl:variable name="test">
						<xsl:value-of select="position()"/>
					</xsl:variable>
					<!-- <xsl:variable name="rowheight"><xsl:value-of select="100 div count(ts)"></xsl:value-of>%</xsl:variable> -->
					<xsl:variable name="rowheight">75px</xsl:variable>
					<xsl:for-each select="ts">
						<tr><xsl:attribute name="id">menurow<xsl:copy-of select="$test"></xsl:copy-of>a<xsl:value-of select="position()"></xsl:value-of></xsl:attribute>
						<xsl:attribute name="height"><xsl:copy-of select="$rowheight"></xsl:copy-of></xsl:attribute>
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="$test!=1">hiddencell</xsl:when>
							</xsl:choose>
						</xsl:attribute>
							<xsl:for-each select="t">
								<xsl:if test="position() &lt; 5">
									<td width="25%">
<!--									<xsl:attribute name="onclick">javascript:workspace_addContent('<xsl:value-of select="tar"/>', '<xsl:value-of select="c"/>')</xsl:attribute> -->
									<xsl:attribute name="class">menuitem sub<xsl:copy-of select="$test"/>a<xsl:value-of select="position()"/></xsl:attribute>
					
		<img class="menuimage">
			<xsl:attribute name="alt"><xsl:value-of select="c"/></xsl:attribute>
			<xsl:attribute name="src">../components/widgets/menu/images/<xsl:choose><xsl:when test="ty='CONTRACT'">contract</xsl:when><xsl:when test="ty='ENQUIRY'">enquiry</xsl:when><xsl:otherwise>other</xsl:otherwise></xsl:choose>.gif
		</xsl:attribute>
		</img>
<a><xsl:attribute name="href">javascript:workspace_addContent('<xsl:value-of select="tar"/>', '<xsl:value-of select="c"/>')</xsl:attribute>
<xsl:attribute name="class">menulink sub<xsl:copy-of select="$test"/>a<xsl:value-of select="position()"/></xsl:attribute>
		<xsl:value-of select="c"/></a>								
									</td>
								</xsl:if>
							</xsl:for-each>
							
								
						
							<xsl:if test="count(t)&lt;1"><td width="25%"><xsl:attribute name="class">menuitem sub<xsl:copy-of select="$test"/>a1</xsl:attribute></td></xsl:if>
							<xsl:if test="count(t)&lt;2"><td width="25%" ><xsl:attribute name="class">menuitemsub<xsl:copy-of select="$test"/>a2</xsl:attribute></td></xsl:if>
							<xsl:if test="count(t)&lt;3"><td width="25%"><xsl:attribute name="class">menuitem sub<xsl:copy-of select="$test"/>a3</xsl:attribute></td></xsl:if>
							<xsl:if test="count(t)&lt;4"><td width="25%" ><xsl:attribute name="class">menuitem sub<xsl:copy-of select="$test"/>a4</xsl:attribute></td></xsl:if>

						</tr>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template name="menu_item">
<!-- 		<img class="menuimage">
			<xsl:attribute name="alt"><xsl:value-of select="c"/></xsl:attribute>
			<xsl:attribute name="src">../components/widgets/menu/images/<xsl:choose><xsl:when test="ty='CONTRACT'">contract</xsl:when><xsl:when test="ty='ENQUIRY'">enquiry</xsl:when><xsl:otherwise>other</xsl:otherwise></xsl:choose>.gif
		</xsl:attribute>
		</img>
<a><xsl:attribute name="href">javascript:workspace_addContent('<xsl:value-of select="tar"/>', '<xsl:value-of select="c"/>')</xsl:attribute>
<xsl:attribute name="class">menuitem sub<xsl:copy-of select="$test"/>a<xsl:value-of select="position()"/></xsl:attribute>
		<xsl:value-of select="c"/></a>
-->
	</xsl:template>
</xsl:stylesheet>
