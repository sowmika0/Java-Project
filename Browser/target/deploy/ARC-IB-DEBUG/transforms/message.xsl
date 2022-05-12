<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	<xsl:template match="message" name="message_n">
		<!-- If there is at least one message then display the tab header -->
		<xsl:if test="msg!=''">
			<!--- Start of header-->
			<div class="dmsg" id="divMsg" tabindex="0" onkeypress ="hideDivmessage(event);">
				<table id="message" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td class="caption"  id="caption" tabindex="0">
							<xsl:choose>
								<xsl:when test="title!=''">
									<xsl:value-of select="title"/>
								</xsl:when>
								<xsl:otherwise>Message</xsl:otherwise>
							</xsl:choose>
						</td>
						<!-- end of header-->
					</tr>
					<tr>
						<td>
							<!-- Display all messages in a table -->
							<table>
								<xsl:for-each select="msg">
									<tr>
										<td class="message" id="message" tabindex="0">
											<xsl:value-of select="."/>
										</td>
									</tr>
								</xsl:for-each>
							</table>
							<!-- The Footer -->
						</td>
					</tr>
				</table>
			</div>
		</xsl:if>
		<xsl:if test="fastpath">
			<form name="fastpath">

                <!-- Add the id, with fragment suffix for noFrames mode ....-->
                <xsl:attribute name="id">fastpath<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>

				<input type="hidden" name="rtnRequest" id="rtnRequest">
					<xsl:attribute name="value"><xsl:value-of select="fastpath" /></xsl:attribute>
				</input>
			</form>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
