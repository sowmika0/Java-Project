<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template name="workspaceItemPane_n">
		<xsl:variable name="defaultPosition" select="(30 * position()) - 24"/>
		<div class="workspace_PaneDiv">
			<xsl:attribute name="style"><xsl:choose><xsl:when test="top!=''">position:absolute;left:<xsl:value-of select="left"/>;top:<xsl:value-of select="top"/>;width: <xsl:value-of select="width"/>;height:<xsl:value-of select="height"/>;
				</xsl:when><xsl:otherwise>position:absolute; left:<xsl:value-of select="$defaultPosition"/>px; top:<xsl:value-of select="$defaultPosition"/>px; width:300px;height:200px;
				</xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:attribute name="id">div<xsl:value-of select="id"/></xsl:attribute>
			<div onmousedown="dragPress(event);" class="ws_paneMover">
				<xsl:attribute name="id">a_mover_<xsl:value-of select="id"/></xsl:attribute>
				<xsl:attribute name="style">
			<xsl:choose>
			<xsl:when test="top!=''">width: <xsl:value-of select="width"/>;</xsl:when>
			<xsl:otherwise>width:300;</xsl:otherwise></xsl:choose></xsl:attribute>
				<table class="ws_header">
					<tbody>
						<tr>
							<td width="1">
								<img class="ws_header_image">
									<xsl:attribute name="alt"><xsl:value-of select="title"/></xsl:attribute>
									<xsl:attribute name="src"><xsl:choose><xsl:when test="type='HTML.WIDGET'">../components/widgets/<xsl:value-of select="widgetId"/>/images/<xsl:value-of select="image"/></xsl:when><xsl:when test="type='URL'">../components/widgets/images/url.gif</xsl:when><xsl:otherwise>../components/widgets/images/<xsl:value-of select="image"/></xsl:otherwise></xsl:choose></xsl:attribute>
								</img>
							</td>
							<td nowrap="nowrap">
								<xsl:value-of select="title"/>
							</td>
							<td align="right">
								<a class="workspace_sizechanger workspace_close" onclick="evtClose_pane(event)">
									<xsl:attribute name="id">panecloser<xsl:value-of select="id"/></xsl:attribute><xsl:text> </xsl:text>
								</a>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div>
				<xsl:attribute name="id">middle<xsl:value-of select="id"/></xsl:attribute>
				<xsl:attribute name="style"><xsl:choose><xsl:when test="items/cmd!='' and top!=''">overflow:scroll;background:white;width: <xsl:value-of select="width"/>;height:<xsl:value-of select="height"/>;
				</xsl:when><xsl:when test="top!=''">background:white;overflow:scroll;width: <xsl:value-of select="width"/>;height:<xsl:value-of select="height"/>;
				</xsl:when><xsl:otherwise>background:white;overflow:scroll;width:300;height:300;</xsl:otherwise></xsl:choose></xsl:attribute>

				<xsl:for-each select="items/cmd">
					<xsl:call-template name="pane_cmd"/>
				</xsl:for-each>

<xsl:choose>
	<xsl:when test="widgetId='NOTES'"><xsl:for-each select="items"><xsl:call-template name="notes"></xsl:call-template></xsl:for-each></xsl:when>

</xsl:choose>

			</div>
			<div class="workspace_footer">
			<xsl:attribute name="style">
			<xsl:choose>
			<xsl:when test="top!=''">width: <xsl:value-of select="width"/>;height:16px;</xsl:when>
			<xsl:otherwise>width:300px;height:16px;</xsl:otherwise></xsl:choose></xsl:attribute>
				<xsl:attribute name="id">a_resizer_<xsl:value-of select="id"/></xsl:attribute>
				<input type="image" style="border:0px;cursor:nw-resize" align="right" onmousedown="resizePress(event);" src="../components/workspace/images/workspace_resize.gif" alt="Resize Grabber">
					<xsl:attribute name="id">resizer_image<xsl:value-of select="id"/></xsl:attribute>
				</input>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>
