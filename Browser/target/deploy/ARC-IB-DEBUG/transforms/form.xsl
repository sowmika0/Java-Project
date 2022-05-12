<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:import href="ARC/T24_constants.xsl"/>
	<xsl:import href="userDetails.xsl"/>
	<xsl:template match="mainform" name="mainform_n">
		<form name="mainform" method="POST" action="BrowserServlet">

            <!-- Add the id, with fragment suffix for noFrames mode ....-->
            <xsl:attribute name="id">mainform<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>

			<input type="hidden" name="requestType" value="{$_UTILITY__ROUTINE_}"/>
			<input type="hidden" name="routineName" value="{$_OS__NEW__DEAL_}"/>
			<input type="hidden" name="routineArgs"/>
			<input type="hidden" name="application"/>
			<input type="hidden" name="ofsOperation"/>
			<input type="hidden" name="ofsFunction"/>
			<input type="hidden" name="version"/>
			<input type="hidden" name="transactionId"/>
			<input type="hidden" name="command" value="globusCommand"/>
			<input type="hidden" name="operation"/>
			<input type="hidden" name="windowName" value=""/>
			<input type="hidden" name="apiArgument" value=""/>
			<!-- Process User Details -->
			<xsl:call-template name="userDetails"/>
		</form>
	</xsl:template>
</xsl:stylesheet>
