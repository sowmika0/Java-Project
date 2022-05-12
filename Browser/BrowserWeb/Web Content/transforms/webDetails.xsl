<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- The data in these fields will be stored at the web server and then added to the response -->
	<!-- They should be used for maintaining field data between requests -->
	
	<xsl:template name="webDetails">
	
		<!-- Display loading image-->
		<input type="hidden" name="singleLoadingIcon" id="singleLoadingIcon">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/singleLoadingIcon"/></xsl:attribute>
		</input>
	
		<!-- The name of the parent window (i.e. for dropdowns to be picked to -->
		<input type="hidden" name="WS_parentWindow" id="WS_parentWindow">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_parentWindow"/></xsl:attribute>
		</input>
		
		<!-- Whether this is a parent window or not (i.e. it has child windows) -->
		<input type="hidden" name="WS_parent" id="WS_parent">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_parent"/></xsl:attribute>
		</input>
		
		<!-- To carry the source element form name for pop selection box -->
		<input type="hidden" name="WS_parentFormId" id="WS_parentFormId">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_parentFormId"/></xsl:attribute>
		</input>
		
		<!-- The name of a dropdown field to pick items to -->
		<input type="hidden" name="WS_dropfield" id="WS_dropfield">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/></xsl:attribute>
		</input>
		
		<!-- Whether the window has been resized according to the users preferences yet -->
		<input type="hidden" name="WS_doResize" id="WS_doResize">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_doResize"/></xsl:attribute>
		</input>
		
		<!-- Used to hold the windows initial state i.e. what command was run to invoke it -->
		<input type="hidden" name="WS_initState" id="WS_initState">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_initState"/></xsl:attribute>
		</input>
		
		<!-- Used to hold the pause time value of autorefresh during page navigation -->
		<input type="hidden" name="WS_PauseTime" id="WS_PauseTime">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_PauseTime"/></xsl:attribute>
		</input>
		
		<!-- Whether the window is a multi-pane window (i.e. an AA screen) -->
		<input type="hidden" name="WS_multiPane" id="WS_multiPane">
			<xsl:choose>
				<xsl:when test="count(responseDetails/window/panes/pane) > 1">
					<xsl:attribute name="value">true</xsl:attribute>
				</xsl:when>
				<xsl:when test="/responseDetails/webDetails/WS_multiPane != ''">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_multiPane"/></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="value">false</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</input>
		
		<!-- Whether the window is going to show the tabs for expansion or not if they exists -->
		<input type="hidden" name="WS_showTabs" id="WS_showTabs">
			<xsl:choose>
				<xsl:when test="count(/responseDetails/window/panes/pane[*]/groupPane) > count(/responseDetails/window/panes/pane[*]/groupTab)">
					<xsl:attribute name="value">true</xsl:attribute>
				</xsl:when>
				<xsl:when test="/responseDetails/webDetails/WS_multiPane != ''">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_showTabs"/></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="value">false</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</input>
		
		<!-- 2 Fields to indicate whether any action on this frame/fragment should replace the whole parent composite screen -->
		<!-- Whether to replace all of the parent composite screen -->
		<input type="hidden" name="WS_replaceAll" id="WS_replaceAll">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_replaceAll"/></xsl:attribute>
		</input>
		<!-- The name of the parent composite screen used to replace it's content -->
		<input type="hidden" name="WS_parentComposite" id="WS_parentComposite">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
		</input>
		<!-- whether to display the delete confirmation message or not -->
		<input type="hidden" name="WS_delMsgDisplayed" id="WS_delMsgDisplayed">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_delMsgDisplayed"/></xsl:attribute>
		</input>
		
	</xsl:template>
	
</xsl:stylesheet>