<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="generalForm">

		<form name="generalForm" method="POST" action="BrowserServlet">

            <!-- Add the id, with fragment suffix for noFrames mode ....-->
            <xsl:attribute name="id">generalForm<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
                

            <input type="hidden" name="requestType"></input>
            <input type="hidden" name="drilltarget" value=""></input>
			<input type="hidden" name="routineName"></input>
			<input type="hidden" name="routineArgs"></input>
			<input type="hidden" name="application">
				<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/app"/></xsl:attribute>
			</input>
			<input type="hidden" name="ofsOperation"></input>
			<input type="hidden" name="ofsFunction"></input>
			<input type="hidden" name="ofsMessage"></input>
			<input type="hidden" name="version">
				<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/version"/></xsl:attribute>
			</input>
			<input type="hidden" name="transactionId" id="transactionId" ></input>
			<input type="hidden" name="command" value="globusCommand"></input>
			<input type="hidden" name="operation"></input>
			<input type="hidden" name="windowName" value=""/>
			<input type="hidden" name="apiArgument" value=""/>
			<input type="hidden" name="name" value=""/>
			<input type="hidden" name="enqname" value=""/>
			<input type="hidden" name="enqaction" value=""/>
			<input type="hidden" name="dropfield">
				<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/></xsl:attribute>
			</input>
			<input type="hidden" name="previousEnqs" value=""/>
			<input type="hidden" name="previousEnqTitles" value=""/>
			<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value=""/>
			<input type="hidden" name="unlock" value=""/>
			
			<xsl:if test="$multiPane='true'" >
				<input type="hidden" name="multiPane" id="multiPane" value=""/>
			</xsl:if>
			
			
			<!-- Process User Details -->
			<xsl:call-template name="userDetails"/>
		</form>
		
	</xsl:template>
	
</xsl:stylesheet>
