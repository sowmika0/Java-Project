<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="formlaunch">
				<form name="mainmenu" method="POST" action="BrowserServlet">

                    <!-- Add the id, with fragment suffix for noFrames mode ....-->
                    <xsl:attribute name="id">mainmenu<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>

 					<input type="hidden" name="requestType" value="{$_UTILITY__ROUTINE_}"></input>
					<input type="hidden" name="routineName" value="{$_OS__NEW__DEAL_}"></input>
					<input type="hidden" name="routineArgs"></input>
					<input type="hidden" name="application"></input>
					<input type="hidden" name="ofsOperation"></input>
					<input type="hidden" name="ofsFunction"></input>
					<input type="hidden" name="version"></input>
					<input type="hidden" name="transactionId"></input>
					<input type="hidden" name="command" value="globusCommand"></input>
					<input type="hidden" name="operation"></input>
					<input type="hidden" name="windowName" value=""/>
					<input type="hidden" name="apiArgument" value=""/>
					<!-- Process User Details -->
					<xsl:call-template name="userDetails"/>
				</form>
	</xsl:template>
</xsl:stylesheet>
