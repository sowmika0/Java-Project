<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="breadCrumbs">
	
		<xsl:if test="not(control/noCrumbs)">
		
			<table id="enquiryResponseCrumbs" cellpadding="1" cellspacing="1">
				<tr>
							<xsl:if test="control/crumbs/tool !='' ">
								<td tabindex="0">
									<xsl:choose>
										<xsl:when test="/responseDetails/window/translations/jumpTo!=''">
											<xsl:value-of select="/responseDetails/window/translations/jumpTo"/>
										</xsl:when>
										<xsl:when test="control/crumbs/jump!=''">
											<xsl:value-of select="control/crumbs/jump"/>
										</xsl:when>
										<xsl:otherwise>
											Jump to:
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:if>
							<xsl:for-each select="control/crumbs/tool">
								<td>
									
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'crumb'"/>
										</xsl:call-template>
									</xsl:attribute>
									
									<a>
										<xsl:variable name="breadCrumbTitle">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string">
											         <xsl:value-of select="title"/>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:variable>
										<xsl:attribute name="onclick">javascript:dobreadcrumb('<xsl:value-of select="action"/>', '<xsl:value-of select="$breadCrumbTitle"/>')</xsl:attribute>
										<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
										<span tabIndex="-1">
											<xsl:choose>
												<xsl:when test="title">
													<xsl:value-of select="title"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="cap"/>
												</xsl:otherwise>
											</xsl:choose>
										</span>
									</a> > 
								</td>
							</xsl:for-each>						
				</tr>
			</table>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>