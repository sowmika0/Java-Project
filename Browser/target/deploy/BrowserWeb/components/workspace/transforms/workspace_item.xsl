<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
	
	
	<!-- the main fragment container is a table -->
	<xsl:template name="fragment_table">
<table>

	<tr class="fragmentContainer">
			<td>		  		<xsl:attribute name="valign">top</xsl:attribute>
		  		<xsl:attribute name="align">left</xsl:attribute><xsl:call-template name="fragment_cmd"></xsl:call-template></td>
		</tr>
	
</table>	
	
	</xsl:template>
	<xsl:template name="fragment_cmd">
			<!--get the target-->
		<!--check to see if an id has been passed down-->
		<xsl:variable name="id">
				<xsl:choose>	
					<xsl:when test="../../id">ws<xsl:value-of select="../../id"/>
					</xsl:when>
					<xsl:otherwise>1</xsl:otherwise>
				</xsl:choose>
		</xsl:variable>

		<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
        <xsl:attribute name="class">fragmentContainer</xsl:attribute>
		<xsl:attribute name="fragmentName"><xsl:value-of select="$id"/></xsl:attribute>
		<xsl:attribute name="nobusy">true</xsl:attribute>
		<xsl:attribute name="onkeydown">FragmentUtil.keydownHandler(event, '<xsl:value-of select="$id"/>')</xsl:attribute>
		<xsl:attribute name="onmousedown">FragmentUtil.mousedownHandler(event, '<xsl:value-of select="$id"/>')</xsl:attribute>
	<xsl:choose>


            <xsl:when test="routine='OS.ENQUIRY.REQUEST'">
                <xsl:message>Frames as Tables XSL - set fragment init enquiry: <xsl:value-of select="routine"/></xsl:message>
                <xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="$id"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;enqaction=SELECTION&amp;requestType=OFS.ENQUIRY&amp;enqname=<xsl:value-of select="args"/>&amp;routineArgs=<xsl:value-of select="parameters"/></xsl:attribute>
            </xsl:when>

            <xsl:when test="routine!=''">
                <xsl:message>Frames as Tables XSL - set fragment init routine: <xsl:value-of select="routine"/></xsl:message>
                <xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="$id"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;requestType=UTILITY.ROUTINE&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/></xsl:attribute>
            </xsl:when>

            <xsl:when test="url!=''">
                <xsl:message>Frames as Tables XSL - set fragment init URL: <xsl:value-of select="url"/></xsl:message>
                <xsl:attribute name="fragmenturl"><xsl:value-of select="url"/></xsl:attribute>
            </xsl:when>	
            	</xsl:choose>
	</xsl:template>
	<xsl:template name="pane_cmd">
<!-- <xsl:call-template name="iframe_cmd"	></xsl:call-template> -->
<xsl:call-template name="fragment_table"	></xsl:call-template>
	</xsl:template>
	<xsl:template name="iframe_cmd">
		<iframe marginwidth="0" marginheight="0" frameborder="0">
			<xsl:attribute name="id">realiframe<xsl:value-of select="../../id"/></xsl:attribute>
			<xsl:attribute name="style"><xsl:choose><xsl:when test="../../top!=''">width: <xsl:value-of select="../../width"/>;height:<xsl:value-of select="../../height"/>;
				</xsl:when><xsl:otherwise>width:300px;height:300px;</xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:choose>
				<xsl:when test="routine='OS.ENQUIRY.REQUEST'">
					<xsl:attribute name="src">../jsps/enqrequest.jsp?&amp;enqaction=SELECTION&amp;enqname=<xsl:value-of select="args"/>&amp;routineArgs=<xsl:value-of select="parameters"/>&amp;skin=<xsl:copy-of select="$skin"/>&amp;compId=<xsl:copy-of select="$companyid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;windowName=<xsl:copy-of select="n"/>&amp;user=<xsl:copy-of select="$user"/></xsl:attribute>
				</xsl:when>
				<xsl:when test="routine!=''">
					<xsl:attribute name="src">../jsps/genrequest.jsp?&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;skin=<xsl:copy-of select="$skin"/>&amp;compId=<xsl:copy-of select="$companyid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;windowName=<xsl:copy-of select="n"/>&amp;user=<xsl:copy-of select="$user"/></xsl:attribute>
				</xsl:when>
				<xsl:when test="cmdline!=''">
					<xsl:attribute name="src">../components/workspace/jsp/cmdrequest.jsp?&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;skin=<xsl:copy-of select="$skin"/>&amp;compId=<xsl:copy-of select="$companyid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;windowName=<xsl:copy-of select="n"/>&amp;user=<xsl:copy-of select="$user"/>&amp;command=<xsl:value-of select="cmdline"/></xsl:attribute>
				</xsl:when>
				<xsl:when test="url!=''">
					<xsl:attribute name="src"><xsl:value-of select="url"/></xsl:attribute>
				</xsl:when>
			</xsl:choose>
		</iframe>
	</xsl:template>
</xsl:stylesheet>
