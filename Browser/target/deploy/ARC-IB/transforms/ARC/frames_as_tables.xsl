<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- import all of the child items here -->
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
	<!-- Disable the right click-->
    <xsl:variable name="NoRightClick">
        <xsl:choose>
            <xsl:when test="responseDetails/NoRightClick">
            <xsl:value-of select="responseDetails/NoRightClick"/>
            </xsl:when>
           <xsl:otherwise>
             <xsl:value-of select="no"/>
            </xsl:otherwise>    
        </xsl:choose>
    </xsl:variable>
	
	<xsl:template match="frames" name="frames_as_tables">			
        
        <xsl:element name="table">
		  
			<xsl:if test="border!=''">
				<xsl:attribute name="border"><xsl:value-of select="border"/></xsl:attribute>
			</xsl:if>		  

            <xsl:choose>
				<xsl:when test="cols!=''">
					<xsl:call-template name="frameset_with_cols"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="frameset_with_rows"/>
				</xsl:otherwise>
            </xsl:choose>
        </xsl:element>
	</xsl:template>
	
	<xsl:template name="frameset_with_rows">

		<xsl:for-each select="frame|frames">
			
			<tr class="fragmentContainer notPrintableFragment">
				<xsl:variable name="rowsize">
                	<xsl:call-template name="split-string">
                    	<xsl:with-param name="str" select="../rows"/>
                        	<xsl:with-param name="index" select="position()-1"/>
                    </xsl:call-template>
                </xsl:variable>                       
                <xsl:if test="$rowsize='0%' or $rowsize='0'">
                <!-- Hide the row content if the row size is 0% -->
                    <xsl:attribute name="style">display:none</xsl:attribute>
                </xsl:if>		
			<td>
			<xsl:attribute name="height">
				<xsl:call-template name="split-string">
					<xsl:with-param name="str" select="../rows"/>
					<xsl:with-param name="index" select="position()-1"/>
				</xsl:call-template>
			</xsl:attribute>			

		  	<xsl:attribute name="valign">top</xsl:attribute>
		  	<xsl:attribute name="align">left</xsl:attribute>
		  
			<xsl:apply-templates select="."/>

			</td>
			</tr>
			</xsl:for-each> 
		</xsl:template>

	<xsl:template name="frameset_with_cols">

		<tr class="fragmentContainer notPrintableFragment">
			<xsl:for-each select="frame|frames">
				<td>
                    <xsl:variable name="columnsize">
                    	<xsl:call-template name="split-string">
                        	<xsl:with-param name="str" select="../cols"/>
                            <xsl:with-param name="index" select="position()-1"/>
                        </xsl:call-template>
                    </xsl:variable>                          
                    <xsl:if test="$columnsize='0%' or $columnsize='0'">
                    <!-- Hide the column content if the column size is 0% -->
                    	<xsl:attribute name="style">display:none</xsl:attribute>
                	</xsl:if>
		  		<xsl:attribute name="valign">top</xsl:attribute>
		  		<xsl:attribute name="align">left</xsl:attribute>
				<xsl:attribute name="width">
					<xsl:call-template name="split-string">
						<xsl:with-param name="str" select="../cols"/>
						<xsl:with-param name="index" select="position()-1"/>
					</xsl:call-template>
				</xsl:attribute>				
				<xsl:apply-templates  select="."/>
				</td>
			</xsl:for-each>
		</tr>
	</xsl:template>
		
	<xsl:template match="frame">

		<!--get the target-->
		<!--check to see if an id has been passed down-->
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

		<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
		<xsl:attribute name="class">fragmentContainer notPrintableFragment</xsl:attribute>
		<xsl:attribute name="fragmentName"><xsl:value-of select="$id"/></xsl:attribute>
		<xsl:attribute name="onkeydown">FragmentUtil.keydownHandler(event, '<xsl:value-of select="$id"/>')</xsl:attribute>
		<xsl:choose>
            <xsl:when test="$NoRightClick = 'yes'">
          <xsl:attribute name="onmousedown">FragmentUtil.rightButtonMouseHandler(event, '<xsl:value-of select="$id"/>')</xsl:attribute>
          </xsl:when>
          <xsl:otherwise> 
		<xsl:attribute name="onmousedown">FragmentUtil.mousedownHandler(event, '<xsl:value-of select="$id"/>')</xsl:attribute>
		</xsl:otherwise> 
           </xsl:choose>

		<xsl:attribute name="windowName"><xsl:value-of select="n"/></xsl:attribute>

        <xsl:choose>
            <xsl:when test="routine='OS.ENQUIRY.REQUEST'">
                <xsl:message>Frames as Tables XSL - set fragment init enquiry: <xsl:value-of select="routine"/></xsl:message>
                <xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="n"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;enqaction=SELECTION&amp;requestType=OFS.ENQUIRY&amp;enqname=<xsl:value-of select="args"/>&amp;routineArgs=<xsl:value-of select="parameters"/>&amp;reqTabid=<xsl:value-of select="reqTabid"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
            </xsl:when>

            <xsl:when test="routine!=''">
				<xsl:message>Frames as Tables XSL - set fragment init routine: <xsl:value-of select="routine"/></xsl:message>
            	<xsl:choose>
            		<xsl:when test="routine='OS.GET.COMPOSITE.SCREEN.XML'">
            			<xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="n"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;requestType=UTILITY.ROUTINE&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="$id"/></xsl:attribute>
            		</xsl:when>
            		<xsl:when test="routine='OS.NEW.DEAL'">
						<xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="n"/>&amp;fragment=<xsl:value-of select="$id"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;requestType=UTILITY.ROUTINE&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
					</xsl:when>
            		<xsl:otherwise>
            			<xsl:attribute name="fragmenturl">BrowserServlet?method=post&amp;user=<xsl:copy-of select="$user"/>&amp;windowName=<xsl:value-of select="n"/>&amp;WS_FragmentName=<xsl:value-of select="$id"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;companyId=<xsl:copy-of select="$companyid"/>&amp;usrRole=<xsl:value-of select="$userrole"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;command=globusCommand&amp;skin=<xsl:copy-of select="$skin"/>&amp;requestType=UTILITY.ROUTINE&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;WS_replaceAll=<xsl:value-of select="replaceAll"/>&amp;WS_parentComposite=<xsl:value-of select="/responseDetails/webDetails/WS_parentComposite"/></xsl:attribute>
            		</xsl:otherwise>
            	</xsl:choose>
            </xsl:when>

            <xsl:when test="url!=''">
                <xsl:message>Frames as Tables XSL - set fragment init URL: <xsl:value-of select="url"/></xsl:message>
                <xsl:attribute name="fragmenturl">
                	<xsl:call-template name="escape-slash">
						<xsl:with-param name="string" select="url" />
					</xsl:call-template>
				</xsl:attribute>
            </xsl:when>

        </xsl:choose>       

	</xsl:template>

	<xsl:template match="rows">
	</xsl:template>
	
	<xsl:template match="cols">
	</xsl:template>
	
	<!-- TODO: Can we do this more efficiently? -->
	<xsl:template name="split-string">
		<xsl:param name="str"/>
		<xsl:param name="index"/>
		<xsl:variable name="normStr" select="concat(normalize-space($str), ',')"/>
		
		<xsl:variable name="first" select="substring-before($normStr, ',')"/>
		<xsl:variable name="rest" select="substring-after($normStr, ',')"/>

		<xsl:choose>
			<xsl:when test="$index = 0">
				<xsl:value-of select="$first"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="split-string">
					<xsl:with-param name="str" select="$rest"/>
					<xsl:with-param name="index" select="$index - 1"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
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
