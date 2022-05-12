<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
	<xsl:template name="workspace_header">
	<input type="hidden" id="workspaceid"><xsl:attribute name="value"><xsl:value-of select="/responseDetails/workspace/id"/></xsl:attribute></input>
		<table cellpadding="0" cellspacing="0" id="thetabs">
			<tbody>
				<tr>
					<td class="tabspacer">
						<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<xsl:for-each select="/responseDetails/workspace/tab">
						<td nowrap="nowrap">
						<xsl:attribute name="onclick">javascript:showtab('<xsl:value-of select="position()"/>')</xsl:attribute>
							<xsl:attribute name="id">theworkspacetab<xsl:value-of select="position()"/></xsl:attribute>
							<xsl:attribute name="class"><xsl:choose><xsl:when test="active='Y'">tab selectedtab</xsl:when><xsl:otherwise>tab unselectedtab</xsl:otherwise></xsl:choose></xsl:attribute>
							<div style="width:100;height:15;">
								<xsl:attribute name="id">workspace_tabdroptarget<xsl:value-of select="position()"/></xsl:attribute>
								<xsl:attribute name="onclick">javascript:showtab('<xsl:value-of select="position()"/>')</xsl:attribute>
								<a >
								<xsl:attribute name="title"><xsl:value-of select="../rename"/></xsl:attribute>
									<xsl:attribute name="id">tabhref<xsl:value-of select="position()"/></xsl:attribute>
									<xsl:attribute name="href">javascript:startTabRename('<xsl:value-of select="position()"/>')</xsl:attribute>
									<xsl:choose>
										<xsl:when test="name!=''">
											<xsl:value-of select="name"/>
										</xsl:when>
										<xsl:otherwise>Tab<xsl:value-of select="position()"/>
										</xsl:otherwise>
									</xsl:choose>
								</a><span>
<a class="deleteTab" ><xsl:attribute name="href">javascript:deleteTab('<xsl:value-of select="position()"/>')</xsl:attribute><img alt="" src="../components/workspace/images/close.gif"></img></a></span>
							</div>
						</td>
				

					</xsl:for-each>
				
												<td class="tab notatab" nowrap="nowrap">	<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/><a  href="javascript:workspace_addTab()">
<xsl:choose>
	<xsl:when test="addTab!=''"><xsl:value-of select="addTab"></xsl:value-of></xsl:when>
	<xsl:otherwise>Add a Tab</xsl:otherwise>
</xsl:choose>						
					</a>
													<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
						</td>
				
					<td class="tabspacer">
												<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<td class="tab notatab" nowrap="nowrap">
						<a href="javascript:workspace_refresh('')"><xsl:value-of select="refresh"></xsl:value-of><img id="refresh_display" alt="" src="../components/workspace/images/refresh/clock04.gif"></img></a>
					</td>
					<td class="tabspacer">
												<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<td class="tab notatab" nowrap="nowrap">
						<a  href="javascript:workspace_addWidget('WIDGET.LIST')"><xsl:value-of select="addStuff"></xsl:value-of></a>
					</td>
										<td class="tabspacer">
												<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<td class="tab notatab" nowrap="nowrap">
						<a href="javascript:workspace_help()"><xsl:value-of select="help"></xsl:value-of></a>
					</td>
<td class="tabspacer">
												<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<td class="tab notatab" nowrap="nowrap">
						<a href="javascript:workspace_saveLayout()"><xsl:value-of select="save"></xsl:value-of></a>
					</td>
					<td class="tabspacer">
												<img width="4" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
					<td class="tab notatab" nowrap="nowrap">
						<span id="ws_loading">Loading</span>
					</td>
					<td class="tabspacerend">
												<img width="1" height="1" alt="" src="../plaf/images/default.block.gif"/>
					</td>
				</tr>
			</tbody>
		</table>
		<xsl:for-each select="/responseDetails/workspace/tab">
			<div>
				<xsl:attribute name="id">workspacetab<xsl:value-of select="position()"/></xsl:attribute>
				<xsl:choose>
					<xsl:when test="active='Y'">
						<xsl:attribute name="class">workspaceshow</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">workspacehidden</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:variable name="tabId">
					<xsl:value-of select="position()"/>
				</xsl:variable>
				<xsl:for-each select="pane">
					<xsl:call-template name="workspaceItemPane_n"/>
					<input type="hidden">
						<xsl:attribute name="id">saveTab<xsl:value-of select="$tabId"/>Pane<xsl:value-of select="position()"/></xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="id"/></xsl:attribute>
					</input>
					<input type="hidden">
						<xsl:attribute name="id">Actiondiv<xsl:value-of select="id"/></xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="widgetId"/></xsl:attribute>
					</input>
					<input type="hidden">
						<xsl:attribute name="id">Titlediv<xsl:value-of select="id"/></xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="title"/></xsl:attribute>
					</input>
				</xsl:for-each>
			</div>
		</xsl:for-each>
		<div id="outline" style="left:-10px;top:-10px;width:10px;height:10px;position:absolute;border:3px dashed silver;visibility:hidden;"/>
		<div id="divTabRename" style="left:0px;top:0px;position:absolute;border:0px;visibility:hidden;width:100px;" onmouseover="disableOnBlur()" onmouseout="enableOnBlur()">
			<input size="10" type="text" id="inpTabRename" onblur="setTabText()" class="noborder"/>
		</div>
		
	</xsl:template>
</xsl:stylesheet>
