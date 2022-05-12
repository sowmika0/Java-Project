<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- Imports for the other xsl files that we use N.B. don't need to import topic.xsl as it is done in topics for us-->
	<xsl:import href="topics.xsl"/>
	<!-- <xsl:variable name="skin">default</xsl:variable>  Browser complains that this variable is being declared more than once. Hence commented out.-->
	<xsl:template match="categorymenu" name="categorymenu_n">
		<!-- Construct a different view of the menu structure. Work out how many topics we have in total (t-size) and how many items are 
		in each section (half). The number of sections is set by the dividor -->
		<!-- Count the topics -->
		<xsl:variable name="t-size" select="count(ts)"/>
		<!--	<xsl:value-of select="cols"/>-->
		<xsl:variable name="columncount" >1</xsl:variable>
		<xsl:variable name="half" select="ceiling($t-size div $columncount)"/>
		<!-- then loop through each topic, but we only process those items that are less the the section total -->
		<!-- loop through the topics -->
		<xsl:for-each select="ts">
			<xsl:if test="position() &lt;= $half">
		
				<table class="categorymenu">
					<!-- work out where we are in the list -->
					<xsl:variable name="here" select="position()"/>
					<!-- then we have a row for each total rows / sections-->
					<tr>
						<td valign="top">
							<xsl:attribute name="rowspan"><xsl:value-of select="1"/></xsl:attribute>
							<xsl:call-template name="category"/>
						</td>
						<xsl:if test="$columncount &gt; 1">
							<td valign="top">
								<xsl:attribute name="rowspan"><xsl:value-of select="1"/></xsl:attribute>
								<!--loop through each 'topics' node-->
								<xsl:for-each select="../ts[$here+$half]">
									<xsl:call-template name="category"/>
								</xsl:for-each>
							</td>
						</xsl:if>
						<xsl:if test="$columncount &gt; 2">
							<td valign="top">
								<xsl:attribute name="rowspan"><xsl:value-of select="1"/></xsl:attribute>
								<!--loop through each 'topics' node-->
								<xsl:for-each select="../ts[$here+$half+$half]">
									<xsl:call-template name="category"/>
								</xsl:for-each>
							</td>
						</xsl:if>
						<xsl:if test="$columncount &gt; 3">
							<td valign="top">
								<xsl:attribute name="rowspan"><xsl:value-of select="1"/></xsl:attribute>
								<!--loop through each 'topics' node-->
								<xsl:for-each select="../ts[$here+$half+$half+$half]">
									<xsl:call-template name="category"/>
								</xsl:for-each>
							</td>
						</xsl:if>
					</tr>
				</table>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="category">
	<xsl:variable name="menusize" select="count(t)+count(ts)"/>
		<div id="test">
		<xsl:attribute name="class">
<xsl:choose>
	<xsl:when test="ts/t or /t24help/categorymenu">display_box</xsl:when>
	<xsl:otherwise>display_box_none</xsl:otherwise>
</xsl:choose>		
		</xsl:attribute>
		<!-- Create the "pretty" table to surround the menu-->
			
			
					<!-- The actual menu item here -->
					<!-- ts is short for the word 'topics' -->
					<!-- t is short for the word 'topic' -->
					
					<div>
						<!-- Make the thing scrollable if there are more than 7 menu items, or any sub menus-->
						<!-- ts is short for the word 'topics' -->
						<!-- t is short for the word 'topic' -->
						<xsl:if test="count(t) &gt; 7 or count(ts) &gt; 0">
						<xsl:if test="allowscroll!='n'">
							<xsl:attribute name="style">height:150px;overflow:auto;width:250px;</xsl:attribute>
							</xsl:if>
						</xsl:if>
						<table>
							<tr>
								<td>
									<UL style="margin:0px;padding:0px;">
										<xsl:attribute name="class">expand</xsl:attribute>
										<!-- loop through each topic -->
										<xsl:for-each select="t">
											<li>
												<xsl:apply-templates select="."/>
											</li>
										</xsl:for-each>
										<!-- Loop through all the sub menus -->
										<!-- ts is short for the word 'topics' -->
										<xsl:apply-templates select="ts"/>
									</UL>
								</td>
							</tr>
						</table>
					</div>
					<!-- The Footer -->
				
		</div>
	</xsl:template>
</xsl:stylesheet>
