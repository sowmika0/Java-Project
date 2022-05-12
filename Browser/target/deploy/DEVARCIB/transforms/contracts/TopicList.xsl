<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--Removed most of the code and now reusing the category menu xsl to do most of the presentation of the left hand pane
Also the contract.xsl now pulls out the items from TopicList as populates the combo boxes-->
<!-- Imports....-->
<xsl:import href="../menu/categorymenu.xsl"/>
<xsl:import href="../menu/topics.xsl"/>
<!-- Template match to actually start the code....-->
	<xsl:template match="/">
		<!-- Our Menu header -->
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="TopicList" name="topicList_n">
		<!-- Add an input  box so that we know we need to show the pane (if they have any content to be displayed). This is tested in the js files -->
		<input id="showinfopane" type="hidden">
			<xsl:if test="MainTopic/ts/browserMessages or MainTopic/ts/errors or MainTopic/ts/warnings or MainTopic/ts/overrides">
				<xsl:attribute name="value">ERRORS</xsl:attribute>
			</xsl:if>
		</input>

	</xsl:template>

	<!-- Loop through each main topic -->
	<xsl:template match="MainTopic">
		<!-- Only show errors and overrides. Everything else is added to the combo box -->
		<xsl:if test="ts/id='errors' or ts/t/ty='ERRORS' or ts/id='messages' or ts/t/ty='WARNINGS'">
		
			<xsl:call-template name="category"/>
			<p/>
		
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
