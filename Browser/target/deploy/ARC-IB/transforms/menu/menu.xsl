<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- Imports for the other xsl files that we use N.B. don't need to import topic.xsl as it is done in topics for us-->
	<xsl:import href="topics.xsl"/>
	<xsl:template match="menu" name="menu_n">
		<!-- If there is a main topics section, then build up the menu. Note tha the whole thing is built within a table -->

			<xsl:for-each select="node()">
				<!-- Sort the field names alphabetically -->
				<xsl:sort select="cap"/>
				<xsl:sort select="field"/>
											
				<xsl:if test="name()!=''">

							<!-- ts is short for the word 'topics' -->
							<xsl:if test="name()='ts'">
								<ul class="menuMargin">
									<xsl:call-template name="topics_n"/>
								</ul>
							</xsl:if>	
							<!-- Then go through any top level items 	-->
							<!-- for each topic -->					
							<xsl:if test="name()='t'">
								<ul>
									<li>
										<xsl:apply-templates select="."/>
									</li>
								</ul>	
							</xsl:if>
	
				</xsl:if>
				
			</xsl:for-each>
	
	</xsl:template>

	<!-- Template for the TABBED menu -->
	<xsl:template match="menu" name="tabs_menu">
		<!-- If there is a main topics section, then build up the menu. Note tha the whole thing is built within a table -->
		<xsl:choose>
			<xsl:when test="ts">
				<div class="extra-nav">
					<ul id="nav-menu">
						<xsl:for-each select="ts">
							<!-- Sort the field names alphabetically -->
							<xsl:sort select="cap"/>
							<xsl:sort select="field"/>
																		
							<xsl:if test="name()='ts'">
								<li>
									<xsl:attribute name="class"><xsl:choose><xsl:when test="position() = 1">active-tab</xsl:when><xsl:otherwise>nonactive-tab</xsl:otherwise></xsl:choose></xsl:attribute>
									<a onclick="javascript:TabbedMenu.showTab({position() - 1})" href="javascript:void(0)">
										<xsl:value-of select="@c"/>
									</a>
								</li>
							</xsl:if>
							
						</xsl:for-each>
					</ul>
				</div>
				<div id="commandsViewport">
					<table cellpadding="0" cellspacing="0" border="0" id="menu-table">
						<xsl:for-each select="ts">
							<!-- Sort the field names alphabetically -->
							<xsl:sort select="cap"/>
							<xsl:sort select="field"/>
											
							<!-- ts is short for the word 'topics' -->
							<xsl:if test="name()='ts'">
								<tr>
									<xsl:attribute name="class"><xsl:choose><xsl:when test="position() = 1">visible-row</xsl:when><xsl:otherwise>hidden-row</xsl:otherwise></xsl:choose></xsl:attribute>
									<!-- xsl:attribute name="id">row_<xsl:value-of select="position()"/></xsl:attribute-->
									<xsl:call-template name="tabbed_topics"/>
								</tr>
							</xsl:if>
							<!-- Then go through any top level items 	-->
							<!-- for each topic -->
							<!-- xsl:if test="name()='t'">
								<xsl:apply-templates select="."/>
								</xsl:if-->
						</xsl:for-each>
					</table>
				</div>		
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="commandsViewport"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Template for commandViewport -->
	<!-- If single level, just render a simple pipe-symbol-separated display -->
	<xsl:template name="commandsViewport">
		<div id="commandsViewport">
			<table cellpadding="0" cellspacing="0" border="0" id="menu-table">
				<tr>
					<xsl:for-each select="t">
						<td onclick="TabbedMenu.showActiveCommandLink({position() - 1});" class="nonactive-command">
							<xsl:call-template name="tabbed_topics"/>
						</td>
						<xsl:if test="position() &lt; last()"><td>|</td></xsl:if>
					</xsl:for-each>
				</tr>
			</table>
		</div>
	</xsl:template>

<!-- Template for the POPDOWN menu -->
<xsl:template match="menu" name="pop_menu">
	<!-- If there is a main topics section, then build up the menu. -->
	<!-- Build a popdown menu structure - up to 2 LEVELS ONLY -->
	<xsl:choose>
		<xsl:when test="ts">
			<div class="sec-nav">
				<ul class="menu-nav">
					<xsl:for-each select="ts">
						<li onmouseover="menu_popup(this, {position()})" onmouseout="menu_leave(event, this, {position()})">
							<xsl:if test="position() = 1">
								<xsl:attribute name="class">first</xsl:attribute>
							</xsl:if>
							<a href="#"><xsl:value-of select="@c"/></a>
							<ul id="menu{position()}" class="pop-menu" onclick="menu_popdown({position()})"><xsl:for-each select="descendant::t">
								<li><xsl:apply-templates select="."/></li>
							</xsl:for-each></ul>
						</li>
					</xsl:for-each>
				</ul>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="commandsViewport"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>	

</xsl:stylesheet>