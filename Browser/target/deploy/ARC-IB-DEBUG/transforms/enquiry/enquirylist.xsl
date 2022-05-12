<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template name="enquiryList">
		<div>
			
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'enquiry_list_container'"/>
					</xsl:call-template>
				</xsl:attribute>
			
			<!-- Each row on our enquiry will show up as an icon and we need to push the whole data row into a text box for use in the detail view -->
			<!-- By convention, we are looking for the following data to display: Text or Title, Image -->
			<xsl:call-template name="header">
		</xsl:call-template>
			<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/r">
				<div>
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enquiry_list_item_container'"/>
						</xsl:call-template>
					</xsl:attribute>
					
					<xsl:attribute name="id">list<xsl:value-of select="position()"/></xsl:attribute>
					<input type="hidden">
						<xsl:attribute name="id">group<xsl:value-of select="position()"/></xsl:attribute>
						<xsl:attribute name="value"><xsl:call-template name="dynamicmatch"><xsl:with-param name="item" select="'Group'"/></xsl:call-template></xsl:attribute>
					</input>
					<table>
						
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'enquiryList'"/>
							</xsl:call-template>
						</xsl:attribute>
						
						<tr>
							<td align="center">
								<xsl:call-template name="dynamicmatch">
									<xsl:with-param name="item" select="'Title'"/>
								</xsl:call-template>
							</td>
						</tr>
						<tr>
							<td align="center">
								<a>
									<xsl:attribute name="onclick"><xsl:call-template name="enquiry_list_drilldown"/></xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
										<xsl:call-template name="dynamicmatch">
											<xsl:with-param name="item" select="'Image'"/>
										</xsl:call-template>
								
								</a>
							</td>
						</tr>
						<tr>
							<td align="center">
								<a>
									<xsl:attribute name="onclick"><xsl:call-template name="enquiry_list_drilldown"/></xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
									<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[1]/cap"/>
								</a>
							</td>
						</tr>
					</table>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template name="dynamicmatch">
		<xsl:param name="item"/>
		<xsl:for-each select="c">
			<xsl:variable name="tmp">
				<xsl:value-of select="position()"/>
			</xsl:variable>
			<xsl:variable name="header">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols/c[$tmp+0]"/>
			</xsl:variable>
			<xsl:if test="$header=$item">
				<xsl:choose>
					<xsl:when test="$item='Image'">
						<img height="48" width="48" align="middle">
							<xsl:attribute name="alt"><xsl:value-of select="title"/></xsl:attribute>
							<xsl:attribute name="src"><xsl:choose><xsl:when test="cap!=''"><xsl:value-of select="cap"/></xsl:when><xsl:otherwise>../components/widgets/images/widget.gif</xsl:otherwise></xsl:choose></xsl:attribute>
						</img>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="cap"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- detail display of an item showing all data... -->
	<xsl:template name="header">
		<div>
		
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enquiry_list_detail_container'"/>
				</xsl:call-template>
			</xsl:attribute>
		
			
			<select id="groupfilter" onchange="listFilterChange()">
				<option>All</option>
				<xsl:for-each select="//responseDetails/window/panes/pane/dataSection/enqResponse/r/c[1][not(.=preceding::r/c[1])]">
					<option>
						<xsl:value-of select="."/>
					</option>
				</xsl:for-each>
			</select>
		</div>
	</xsl:template>
	<xsl:template name="enquiry_list_drilldown">
		<!-- we need to pull out the value of the ID column to use as the key -->
		<xsl:variable name="tmp">
			<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/DrillTypes"/>
		</xsl:variable>
		<xsl:variable name="first" select="substring-before($tmp, 'Item')"/>
		<xsl:variable name="rest" select="substring-after($tmp, 'Item')"/>
		<xsl:value-of select="$first"/>
		<xsl:call-template name="dynamicmatch">
			<xsl:with-param name="item" select="'Item'"/>
		</xsl:call-template>
		<xsl:value-of select="$rest"/>
	</xsl:template>
</xsl:stylesheet>
