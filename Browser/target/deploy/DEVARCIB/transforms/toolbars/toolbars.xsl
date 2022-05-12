<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:import href="tool.xsl"/>
	<xsl:template match="toolbars" name="toolbars_n">
		<xsl:param name="id" select="''"/>
		<xsl:param name="type"/>
		<xsl:param name="Comptoolbar"/>

		<!-- New logic for toolbar allows id prefix to be specified to restrict toolbars chosen
		     otherwise, all toolbars get chosen: *[blah] chooses evey node with name equal to <id>toolbar
		     If no id set, reverts to old behaviour of iterating through all 'toolbar' children  -->
		<xsl:for-each select="*[name() = concat($id, 'toolbar')]">
			<xsl:if test="$type='dropdown'">
				<ul>
					<xsl:for-each select="tool">
						<xsl:call-template name="dropdowntool"/>
					</xsl:for-each>
				</ul>
			</xsl:if>
		<xsl:variable name="toolbarType"><xsl:value-of select="toolbarDisType"/></xsl:variable>
		<xsl:variable name="toolbarName"><xsl:value-of select="toolbarId"/></xsl:variable>
			<xsl:choose>
				<!-- If Orientation is set for VERTICAL display -->
				<xsl:when test="$toolbarType='VERTICAL'">
					<xsl:variable name="toolBarStyle"><xsl:value-of select="toolbarStyle"/></xsl:variable>
						<div id="{$toolBarStyle}">
							<table class="{$toolBarStyle}">
								<!-- Tools -->
								<xsl:for-each select="tool">
									<xsl:if test="$isArc='false' and ($toolbarName='APPLICATION' or $toolbarName='CONTRACT') and position()=last()">
										<xsl:call-template name="moreactions"/>
									</xsl:if> <!-- so that help is last -->
									<tr>
										<td width="1">
											<xsl:apply-templates select="." >
												<xsl:with-param name="toolposition" select="position()"/>
												<xsl:with-param name="Comptoolbar" select="$Comptoolbar"/>
											</xsl:apply-templates>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<!-- If Orientation is set for HORIZONTAL display -->
						<xsl:when test="$toolbarType=''">
							<xsl:variable name="toolBarStyle"><xsl:value-of select="toolbarStyle"/></xsl:variable>
								<div id="{$toolBarStyle}">
									<table class="{$toolBarStyle}">
										<tr>
											<!-- Tools -->
											<xsl:for-each select="tool">
												<xsl:if test="$isArc='false' and ($toolbarName='APPLICATION' or $toolbarName='CONTRACT') and position()=last()">
													<xsl:call-template name="moreactions"/>
												</xsl:if> <!-- so that help is last -->
												<td width="1">
													<xsl:apply-templates select="." >
														<xsl:with-param name="toolposition" select="position()"/>
														<xsl:with-param name="Comptoolbar" select="$Comptoolbar"/>		
													</xsl:apply-templates>
												</td>
											</xsl:for-each>
										</tr>
									</table>
								</div>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
