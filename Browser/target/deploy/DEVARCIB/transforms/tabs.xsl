<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	<xsl:variable name="companyid">
		<xsl:value-of select="/responseDetails/userDetails/companyId"/>
	</xsl:variable>
	<xsl:template match="tabs" name="tabs_n">
		<!-- Form to store the name of the active tab -->
		<form action="" name="activetabform">
			<!-- Add the id, with fragment suffix for noFrames mode ....-->
			<xsl:attribute name="id">activetabform<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
			<input type="hidden" name="activeTab" id="activeTab">
				<xsl:attribute name="value"><xsl:value-of select="activetab"/></xsl:attribute>
			</input>
			<input type="hidden" name="tabsource" id="tabsource">
				<xsl:attribute name="value"><xsl:value-of select="tabsource"/></xsl:attribute>
			</input>
		</form>
		<!-- Show horizontal scroll bar for the tabs -->
		<div id="tabScreenDiv" class="tabScreenStyle">				
			<!-- Show the tabs for the deal -->
			<table id="alltab" style="white-space: nowrap;">
				<tbody>
					<tr>
						<!-- Underline in tabs to appear while launching a tabbed screen -->
						<td class="tab-nav-base">
							<table id="headtab{$formFragmentSuffix}" class="tab-nav" cellpadding="1">
								<tbody>
									<tr>
										<xsl:for-each select="tab">
											<td>
												<xsl:choose>
													<xsl:when test="tabid=../activetab">
														<a class="active-tab">
															<xsl:attribute name="onclick">javascript:gettabbedscreen('<xsl:value-of select="$_OS__PROCESS__TAB_"/>','<xsl:value-of select="../tabsource"/>','<xsl:value-of select="tabid"/>','<xsl:value-of select="ty"/>','generalForm','workarea')</xsl:attribute>
															<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
															<span>
																<xsl:value-of select="tabname"/>
															</span>
														</a>
													</xsl:when>
													<xsl:otherwise>
														<xsl:choose>
															<xsl:when test="response='XML.TAB'">
																<a class="nonactive-tab">
																	<xsl:attribute name="onclick">javascript:gettabbedscreen('<xsl:value-of select="$_OS__PROCESS__TAB_"/>','<xsl:value-of select="../tabsource"/>','<xsl:value-of select="tabid"/>','<xsl:value-of select="ty"/>','generalForm','workarea')</xsl:attribute>
																	<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
																	<span>
																		<xsl:value-of select="tabname"/>
																	</span>
																</a>
															</xsl:when>
															<xsl:otherwise>
																<a class="nonactive-tab">
																	<xsl:attribute name="onclick"><xsl:value-of select="tabhref"/></xsl:attribute>
																	<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
																	<span>
																		<xsl:value-of select="tabname"/>
																	</span>
																</a>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:otherwise>
												</xsl:choose>
											</td>
										</xsl:for-each>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>
</xsl:stylesheet>
