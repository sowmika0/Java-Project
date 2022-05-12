<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="tableBorder_header_top">
		<tr>
			<td colSpan="2"/>
			<td class="formlight">
				<img style="height:1px;width:1px;">
					<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
				</img>
			</td>
			<td colSpan="2"/>
		</tr>
		<tr>
			<td/>
			<td class="imageHolder">
				<img style="height:3px;width:3px;">
					<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/tab_tl.gif</xsl:attribute>
				</img>
			</td>
			<td class="formback"/>
			<td class="imageHolder">
				<img style="height:3px;width:3px;">
					<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/tab_tr.gif</xsl:attribute>
				</img>
			</td>
			<td/>
		</tr>
	</xsl:template>
	
	<xsl:template name="tableBorder_header_left">
		<td class="formlight" rowspan="3">
			<img style="height:1px;width:1px;">
				<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
			</img>
		</td>
		<td class="formback"/>
	</xsl:template>

	<xsl:template name="tableBorder_header_right">
		<td class="formback"/>
		<td class="formdark" rowspan="3">
			<img style="height:1px;width:1px;">
				<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
			</img>
		</td>
	</xsl:template>

	<xsl:template name="tableBorder_body_left">
		<td/>
	</xsl:template>

	<xsl:template name="tableBorder_body_right">
		<td/>
	</xsl:template>

	<!-- add the last two lines to give the table a shadow border -->
	<xsl:template name="tableBorder_body_bottom">
		<tr>
			<td class="imageHolder" colSpan="3">
				<img style="height:5px;width:1;">
					<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
				</img>
			</td>
		</tr>
		<tr>
			<td class="formdark" colSpan="5">
				<img style="height:1px;width:1px;">
					<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
				</img>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>