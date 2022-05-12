<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="/">
<html><body>					<div class="ws_display_box">
					<table style="border:0px">
						<xsl:for-each select="/responseDetails/tec/items/item">
							<xsl:call-template name="pane_item"/>
						</xsl:for-each>
					</table></div>
					</body></html>
</xsl:template>
	

<xsl:template name="pane_item">
		<tr>
			<xsl:attribute name="class">colour<xsl:value-of select="position() mod 2"/></xsl:attribute>
			<xsl:if test="count(../../items/item/image)!=0">
				<td>
					<xsl:if test="image!=''">
						<img border="0">
							<xsl:attribute name="src">../components/workspace/images/<xsl:value-of select="image"/></xsl:attribute>
							<xsl:attribute name="alt"><xsl:value-of select="name"/></xsl:attribute>
						</img>
						<img width="5" border="0" src="../plaf/default/block.gif" alt=""/>
					</xsl:if>
				</td>
			</xsl:if>
			<td class="ws_link" >
				<xsl:choose>
					<xsl:when test="drill!=''">
						<a>
							<xsl:attribute name="href">javascript:workspace_addContent("<xsl:value-of select="drill"/>","<xsl:value-of select="name"/>")</xsl:attribute>
							<xsl:value-of select="name"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="name"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="ws_item">
				<xsl:value-of select="value"/>
				<xsl:if test="percent!=''">
					<img height="12" src="../components/workspace/images/progbar_start.gif" alt="Progress bar start"/>
					<img height="12" src="../components/workspace/images/progbar.gif" alt="Progess bar value">
						<xsl:attribute name="width"><xsl:value-of select="percent"/></xsl:attribute>
					</img>
					<img height="12" src="../components/workspace/images/progbar_blank.gif" alt="Progress bar blank">
						<xsl:attribute name="width"><xsl:value-of select="100-percent"/></xsl:attribute>
					</img>
					<img height="12" src="../components/workspace/images/progbar_end.gif" alt="progress bar end"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	</xsl:stylesheet>