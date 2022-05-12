<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- import all of the child items here -->
	<xsl:import href="ARC/T24_constants.xsl"/>
	
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	<xsl:variable name="companyid">
		<xsl:value-of select="/responseDetails/userDetails/companyId"/>
	</xsl:variable>
    <xsl:variable name="userrole">
        <xsl:value-of select="/responseDetails/userDetails/userRole"/>
    </xsl:variable>
	<xsl:variable name="compScreen">
		<xsl:value-of select="/responseDetails/userDetails/compScreen"/>
	</xsl:variable>
	<xsl:variable name="contextRoot">
		<xsl:value-of select="/responseDetails/contextRoot"/>
	</xsl:variable>
	<xsl:variable name="user">
       		<xsl:choose>
       			<!-- {user_id} will be sent to the client in place of the real user name, and replaced with the user name in the servlet filter -->
           		<xsl:when test="responseDetails/userDetails/stripUser='true'">{user_id}</xsl:when>
           		<xsl:otherwise><xsl:value-of select="/responseDetails/userDetails/user"/></xsl:otherwise>
       		</xsl:choose>
  	</xsl:variable>
	
	<xsl:template match="frames" name="frames_n">
		<frameset>
			<xsl:if test="/responseDetails/window/init!=''">
				<xsl:attribute name="onload"><xsl:value-of select="/responseDetails/window/init"/></xsl:attribute>
				<xsl:attribute name="onbeforeunload">beforeUnloadCompositeScreenWindow('<xsl:value-of select="/responseDetails/userDetails/user"/>');</xsl:attribute>
			</xsl:if>
			<xsl:if test="rows!=''">
				<xsl:attribute name="rows"><xsl:value-of select="rows"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="cols!=''">
				<xsl:attribute name="cols"><xsl:value-of select="cols"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="border!=''">
				<xsl:attribute name="border"><xsl:value-of select="border"/></xsl:attribute>
			</xsl:if>
			
			<xsl:for-each select="frame">
				<frame>
				    <!--get the target-->
					<xsl:if test="tar!=''">
						<xsl:attribute name="target"><xsl:value-of select="tar"/></xsl:attribute>
					</xsl:if>
					
					<!--check to see if an id has been passed down-->
					<xsl:choose>	
						<xsl:when test="id">
							<xsl:attribute name="id"><xsl:value-of select="id"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						    <!-- n represents the node 'name'-->
							<xsl:attribute name="id"><xsl:value-of select="n"/></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					
					<!--now set the id variable accordingly-->
					<xsl:variable name="id">
							<xsl:choose>	
								<xsl:when test="id">
									<xsl:value-of select="id"/>
								</xsl:when>
								<xsl:otherwise>
									<!-- n represents the node 'name'-->
									<xsl:value-of select="n"/>
								</xsl:otherwise>
							</xsl:choose>
					</xsl:variable>
					
					<!-- n represents the node 'name'-->
					<xsl:attribute name="name"><xsl:value-of select="n"/></xsl:attribute>
					
					<xsl:choose>
						<xsl:when test="scroll!=''">
							<xsl:attribute name="scrolling">no</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="scrolling">auto</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<!-- Set the source if it is defined in the XML, or to a valid blank html page (for IE6 on websphere) -->
					<xsl:choose>
						<xsl:when test="routine=$_OS__ENQUIRY__REQUEST_">
							<xsl:attribute name="src">../jsps/enqrequest.jsp?&amp;enqaction=SELECTION&amp;enqname=<xsl:value-of select="args"/>&amp;routineArgs=<xsl:value-of select="parameters"/>&amp;skin=<xsl:value-of select="$skin"/>&amp;compId=<xsl:value-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:value-of select="$compScreen"/>&amp;contextRoot=<xsl:value-of select="$contextRoot"/>&amp;windowName=<xsl:value-of select="n"/>&amp;user=<xsl:value-of select="$user"/>&amp;reqTabid=<xsl:value-of select="reqTabid"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
						</xsl:when>
						<xsl:when test="routine!=''">
							<xsl:choose>
            					<xsl:when test="routine='OS.GET.COMPOSITE.SCREEN.XML'">
									<xsl:attribute name="src">../jsps/genrequest.jsp?&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;skin=<xsl:value-of select="$skin"/>&amp;compId=<xsl:value-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:value-of select="$compScreen"/>&amp;contextRoot=<xsl:value-of select="$contextRoot"/>&amp;windowName=<xsl:value-of select="n"/>&amp;user=<xsl:value-of select="$user"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="$id"/></xsl:attribute>
								</xsl:when>
            					<xsl:otherwise>
            						<xsl:attribute name="src">../jsps/genrequest.jsp?&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;skin=<xsl:value-of select="$skin"/>&amp;compId=<xsl:value-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:value-of select="$compScreen"/>&amp;contextRoot=<xsl:value-of select="$contextRoot"/>&amp;windowName=<xsl:value-of select="n"/>&amp;user=<xsl:value-of select="$user"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
            					</xsl:otherwise>
            				</xsl:choose>
						</xsl:when>
						<xsl:when test="url!=''">
							<xsl:attribute name="src">
								<xsl:call-template name="escape-slash">
									<xsl:with-param name="string" select="url" />
								</xsl:call-template>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="src">../html/blank_enrichment.html</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="frames"/>
				</frame>
			</xsl:for-each>
			<xsl:apply-templates select="frames"/>
		</frameset>
	</xsl:template>
	<xsl:template match="frame"/>
	
	<xsl:template name="escape-slash">
		<xsl:param name="string"/>
		<xsl:variable name="bslash" select='"&#92;"'/>
		<xsl:choose>
			<xsl:when test="contains($string, $bslash)">
				<xsl:value-of select="substring-before($string, $bslash)"/>
				<xsl:text>&#47;</xsl:text>
				<xsl:call-template name="escape-slash">
					<xsl:with-param name="string" select="substring-after($string, $bslash)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
