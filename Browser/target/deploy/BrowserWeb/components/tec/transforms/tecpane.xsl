<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template name="tecpane_n">
		<xsl:choose>
			<xsl:when test="/responseDetails/tec/fragment!=''"/>
			<xsl:otherwise>
				<table height="80" cellSpacing="0" cellPadding="0" bgColor="#213a7d" border="0" ID="Table1">
						<tr>
							<td>
								<A href="http://www.temenos.com">
									<IMG height="80" alt="TEMENOS" src="../plaf/images/default/banner_start.gif" width="112" border="0"/>
								</A>
							</td>
							<td width="100%" class="title">T24 Enterprise Console</td>
							<td align="right">
								<IMG height="80" src="../plaf/images/default/banner_end.gif" width="158" border="0"/>
							</td>
						</tr>
						<xsl:call-template name="navbar_n"/>
					
				</table>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="/responseDetails/tec/pane">
			<xsl:call-template name="pane_n"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="pane_n">

		<div style="float: left; width: 250;height:2;">
			<xsl:attribute name="id">div<xsl:value-of select="action"/></xsl:attribute>
<table cellpadding="1">
	<tbody>
		<tr>
			<td>
		<!-- Create the "pretty" table to surround the menu-->
		<table cellpadding="0" cellspacing="0" border="0" style="margin:0px;padding:0px">
		<tr><td></td><td/><td class="formlight" colSpan="3" height="1"></td><td/><td/></tr>
		<tr><td/><td height="1" style="line-height: 0px;"><img align="top"><xsl:attribute name="src">../plaf/tec/tab_tl.gif</xsl:attribute></img></td>
		<td colspan="3" class="formback"/>
		<td style="line-height: 0px;"><img  align="top"><xsl:attribute name="src">../plaf/tec/tab_tr.gif</xsl:attribute></img></td><td/></tr>
		<tr><td class="formlight" rowSpan="3"><img src="../plaf/images/default/block.gif" width="1"/></td><td class="formback"/><td class="formback" colspan="3" nowrap="nowrap"><xsl:call-template name="pane_header"></xsl:call-template></td><td class="formback"/><td class="formdark" rowSpan="3"><img src="../plaf/images/default/block.gif" width="1"/></td></tr>
			<tr>
				<td/>
				<td/>
				<td>
					<div>
						<xsl:attribute name="style">height:170px;overflow:auto;width:250px;</xsl:attribute>
<table width="100%">
										<xsl:for-each select="items/item">
					<xsl:call-template name="pane_item"></xsl:call-template>
					</xsl:for-each>
</table>					
						
					</div>
					<!-- The Footer -->
				</td>
				<td/>
				<td/>
			</tr><tr height="1"><td class="formdark" colSpan="7" height="1"></td></tr></table>
				</td></tr></tbody></table>		
			

		</div>
	</xsl:template>
<xsl:template name="pane_header">
			<table width="100%">
				<tbody>
					<tr>
						<td colspan="3" class="header">
							<table cellspacing="0">
								<tbody>
									<tr>
										<td class="header"><div style="width:64;height:64">
										<img border="0" align="middle">
												<xsl:attribute name="src">../plaf/tec/<xsl:value-of select="image"/></xsl:attribute>
												<xsl:attribute name="alt"><xsl:value-of select="title"/></xsl:attribute>
											</img></div>
										</td>
										<td class="header" width="100%">
											<xsl:value-of select="title"/>
											<br/>
											<xsl:call-template name="panecommand_n"/>
										</td>
										<td valign="top"><!-- dont think we are ready to release th ajax stuff yet...
											<xsl:call-template name="panecontrols_n"/> -->
										</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td height="1" colspan="3" bgcolor="black"/>
					</tr>

	
				</tbody>
			</table>
</xsl:template>
<xsl:template name="pane_item">
						<tr><xsl:attribute name="class">colour<xsl:value-of select="position() mod 2"/></xsl:attribute>
										<td align="left" nowrap="nowrap"><xsl:if test="image!=''">
									<img border="0">
										<xsl:attribute name="src">../plaf/tec/<xsl:value-of select="image"/></xsl:attribute>
										<xsl:attribute name="alt"><xsl:value-of select="name"/></xsl:attribute>
									</img>
									<img width="5" border="0" src="../plaf/default/block.gif"></img>
								</xsl:if>
								<xsl:value-of select="name"/>
							</td>
							<td align="right">
								<xsl:choose>
									<xsl:when test="value!=''">
										<xsl:value-of select="value"/>
									</xsl:when>
									<xsl:when test="percent!=''">
										<img height="12" src="../plaf/tec/progbar_start.gif"/>
										<img height="12" src="../plaf/tec/progbar.gif">
											<xsl:attribute name="width"><xsl:value-of select="percent"/></xsl:attribute>
										</img>
										<img height="12" src="../plaf/tec/progbar_blank.gif">
											<xsl:attribute name="width"><xsl:value-of select="blankpercent"/></xsl:attribute>
										</img>
										<img height="12" src="../plaf/tec/progbar_end.gif"/>
									</xsl:when>
								</xsl:choose>
							</td>
						</tr>
</xsl:template>
	
	
	<xsl:template name="navbar_n">
		<tr>
			<td width="100%" colspan="3" bgcolor="WHITE">
				<img height="1" src="../plaf/images/default/block.gif" width="1" border="0"/>
			</td>
		</tr>
		<tr height="1">
			<td colspan="3" class="c3">
				<table border="0" cellspacing="0" cellpadding="0" class="c3">
					<tr>
						<td width="100">
							<P align="center">
								<table>
									<tbody>
										<tr>
											<td><a class="nav" href="javascript:tecrefresh()"><img align="middle" src="../plaf/tec/tools/refresh.gif" border="0"/></a></td><td><a class="nav" href="javascript:tecrefresh()">Refresh</a></td>
										</tr>
									</tbody>
								</table>
							</P>
						</td>
						<td>
							<IMG height="22" src="../plaf/images/default/nav_separator.gif" width="1" border="0"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="3" bgcolor="WHITE">
				<IMG height="1" src="../plaf/images/default/block.gif" width="1" border="0"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="panecontrols_n">
		<table cellpadding="0" cellspacing="0">
			<tr valign="top">
				<td class="sep">
					<img align="middle" src="../plaf/tec/tools/newsep.gif" alt=""/>
				</td>
				<xsl:variable name="refreshtool">refresh<xsl:value-of select="action"/>
				</xsl:variable>
				<td class="unhighlight">
					<xsl:attribute name="id"><xsl:copy-of select="$refreshtool"/></xsl:attribute>
					<xsl:attribute name="onmouseover">highlight('<xsl:copy-of select="$refreshtool"/>')</xsl:attribute>
					<xsl:attribute name="onmouseout">unhighlight('<xsl:copy-of select="$refreshtool"/>')</xsl:attribute>
					<a>
						<xsl:attribute name="href">javascript:tecaction('<xsl:value-of select="action"/>')</xsl:attribute>
						<img align="middle" src="../plaf/tec/tools/refresh.gif" border="0"/>
					</a>
				</td>
				<td class="sep">
					<img align="middle" src="../plaf/tec/tools/newsep.gif" alt=""/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="panecommand_n">
	<xsl:if test="cmd/helptextmenu/gapplication!=''"><table>
		<tbody>
			<tr>
<td>
	<select><xsl:attribute name="id">select<xsl:value-of select="action"/></xsl:attribute>
		<xsl:for-each select="cmd/helptextmenu/gapplication">
					<option>
			<xsl:attribute name="value"><xsl:value-of select="application"/></xsl:attribute><xsl:value-of select="descript"></xsl:value-of>
			</option>
		</xsl:for-each>
			</select></td><td><xsl:call-template name="gobutton_n"></xsl:call-template></td>
			</tr>
		</tbody>
	</table></xsl:if>
	</xsl:template>
	<xsl:template name="gobutton_n">
		<table cellpadding="0" cellspacing="0">
			<tr valign="top">

				<xsl:variable name="refreshtool">go<xsl:value-of select="action"/>
				</xsl:variable>
				<td class="unhighlight">
					<xsl:attribute name="id"><xsl:copy-of select="$refreshtool"/></xsl:attribute>
					<xsl:attribute name="onmouseover">highlight('<xsl:copy-of select="$refreshtool"/>')</xsl:attribute>
					<xsl:attribute name="onmouseout">unhighlight('<xsl:copy-of select="$refreshtool"/>')</xsl:attribute>
					<a>
						<xsl:attribute name="href">javascript:tecoption('select<xsl:value-of select="action"/>')</xsl:attribute>
						<img align="middle" src="../plaf/tec/tools/go.gif" border="0"/>
					</a>
				</td>

			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
	
