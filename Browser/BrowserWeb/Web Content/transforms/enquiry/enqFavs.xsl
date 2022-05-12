<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- Need to add since the window.xsl reference for stylingDetails.xsl will not available when the enquiry favourites add/delete happens -->
	<!-- Response starightaway will be processed by this xsl -->
	<xsl:import href="../stylingDetails.xsl"/>
	<xsl:output method="html"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin" select="string(/responseDetails/userDetails/skin)"/>
	<xsl:variable name="imgRoot" select="concat('../plaf/images/', $skin)"/>

	<xsl:template match="/" name="enqFavsResponse">
		<xsl:for-each select="/responseDetails/enqFav">
			<xsl:call-template name="enqFavourites"/>
		</xsl:for-each>
	</xsl:template>

	<!-- Render favourites block, either as part of the enqsel transformation, or on its own in a response from add / del fav request -->
	<xsl:template name="enqFavourites">
		<xsl:choose>
			<xsl:when test="errorMessage">
				<div>FAVOURITES-REQUEST-ERROR: <xsl:value-of select="errorMessage"/></div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="ptext" select="/responseDetails/window/translations/favNamePrompt"/>
				<xsl:variable name="etext" select="/responseDetails/window/translations/favNameError"/>
				<xsl:variable name="htext" select="/responseDetails/window/translations/favNameHelp"/>
				
				<!-- Extract the enquiry name from the response once again, because there may be a chance the enquiry name variable may not be initialized in window.xsl -->
				<!-- It avoids transformation error of extracting value from non exists variable -->
				<xsl:variable name="enquiry"> 
					<xsl:choose>
						<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid!=''">
							<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid"/>
						</xsl:when>
						<xsl:when test="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname!=''">
							<xsl:value-of select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname"/>
						</xsl:when>
						<xsl:otherwise>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<table cellpadding="3" rules="groups" frame="void"  id="enqfav" tabindex="0">
					<xsl:attribute name="class">
						<xsl:choose>
							<!-- Call appy_Style template when enquiry variable name holds of enquiry name. It happens usually if call made from window.xsl -->
							<xsl:when test="$enquiry!=''">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'enqfav'"/>
								</xsl:call-template>
							</xsl:when>
							<!-- When there is no enquiry name supplied, call apply_Style_Enq_Fav template to not send enquiry details for custom style template-->
							<xsl:otherwise>
								<xsl:call-template name="apply_Style_Enq_Fav">
									<xsl:with-param name="actualclass" select="'enqfav'"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<thead>
						<tr>
							<td>
								<span><xsl:value-of select="title"/></span>
							</td>
							<td>
								<img src="{$imgRoot}/enquiry/favadd.gif" onclick="EnqFavourites.add(&quot;{$ptext}&quot;, &quot;{$etext}&quot;, &quot;{$htext}&quot;)" align="absmiddle" alt="{addTip}" title="{addTip}" tabindex="0" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress="AddFavourite(&quot;{$ptext}&quot;, &quot;{$etext}&quot;, &quot;{$htext}&quot;,event)"/>
							</td>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="favs/fav">
							<tr>
								<td>
									<a onclick="javascript:EnqFavourites.run(&quot;{desc}&quot;)" href="javascript:void(0)" alt="{../../runTip}" title="{../../runTip}" tabindex="0"><xsl:value-of select="desc"/></a>
								</td>
								<td>
									<xsl:if test="type = 'user'">
										<img src="{$imgRoot}/enquiry/favdel.gif" onclick="EnqFavourites.del(&quot;{desc}&quot;)" align="absmiddle" alt="{../../delTip}" title="{../../delTip}" tabindex="0" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress="DeleteFavourite(&quot;{desc}&quot;,event)"/>
									</xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Wrapper template for custom styling its used when favourites are added/deleted in the enquiry screen. -->
	<!-- In this particular scenario enquiry name will not be available and window.xsl not been loaded for this response, so use this-->
	<xsl:template match="apply_Style_Enq_Fav" name="apply_Style_Enq_Fav">
		<xsl:param name="actualclass"/>
			<xsl:call-template name="Enquiry-Styling">
				<xsl:with-param name="enquiry"/>
				<xsl:with-param name="actualclass" select="$actualclass"/>
			</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
		