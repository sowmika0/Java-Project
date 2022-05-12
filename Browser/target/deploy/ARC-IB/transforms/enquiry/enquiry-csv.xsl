<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<xsl:choose>
			<!-- Process each enquiry page data -->
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
				<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
					<xsl:call-template name="Header"/>
					<xsl:call-template name="ColumnHeaders"/>
					<xsl:call-template name="Datam"/>
					<xsl:call-template name="Footer"/>
				</xsl:for-each>
			</xsl:when>
			<!-- Process enquiry data -->
			<xsl:otherwise>
				<xsl:call-template name="Header"/> 
				<xsl:call-template name="ColumnHeaders"/>
				<xsl:call-template name="Datam"/>
				<xsl:call-template name="Footer"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template for Header which decides to extract header data page by page or whole -->
	<xsl:template name="Header">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./header/r">
					<xsl:call-template name="Headers"/>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r">
					<xsl:call-template name="Headers"/>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template for datam which decides to extract data page by page or whole -->
	<xsl:template name="Datam">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./r">
					<xsl:call-template name="Data"/>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/r">
					<xsl:call-template name="Data"/>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template for footer which decides to extract footer data page by page or whole -->
	<xsl:template name="Footer">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./footer/r">
					<xsl:call-template name="Footers"/>
				</xsl:for-each>
				<xsl:text> </xsl:text>
				<xsl:text>&#xa;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/footer/r">
					<xsl:call-template name="Footers"/>
				</xsl:for-each>
				<xsl:text> </xsl:text>
				<xsl:text>&#xa;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template for headers -->
	<xsl:template name="Headers">
		<xsl:for-each select="c">
			<xsl:text>&#34;</xsl:text>
				 <xsl:choose>
                     <xsl:when test="cap != '' ">
                        <xsl:value-of select="translate(cap,'&#160;',' ')" />	<!-- headers some time having space chars -->
                     </xsl:when>
                     <xsl:otherwise>
						<xsl:choose>
                        <!--Don't display field display type(ie:ENQ-H-DATA etc..)in the header portion -->
                            <xsl:when test="class!=''">
                                <xsl:value-of select="translate(cap,'&#160;',' ')" />	<!-- headers some time having space chars -->
                            </xsl:when>
                            <xsl:otherwise>
								<xsl:value-of select="translate(.,'&#160;',' ')" />
							</xsl:otherwise>
						</xsl:choose>	
                     </xsl:otherwise>
                 </xsl:choose>
				<xsl:text>&#34;</xsl:text>
				<xsl:if test="position() != last()">
       				<xsl:value-of select=" ',' "/>
    			</xsl:if>
			</xsl:for-each>
			<xsl:text>&#xa;</xsl:text>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
		
	<!-- Template for Column headers -->
	<xsl:template name="ColumnHeaders">
		<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols">
			<xsl:for-each select="c">
				<xsl:text>&#34;</xsl:text>
				<xsl:value-of select="." />
				<xsl:text>&#34;</xsl:text>
				<xsl:if test="position() != last()">
       				<xsl:value-of select=" ',' "/>
    			</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
	
	<!-- Template for data -->
	<xsl:template name="Data">
		<xsl:for-each select="c">
			<xsl:text>&#34;</xsl:text>
			<xsl:if test="cap">
					<xsl:value-of select="normalize-space(cap)"/>
				</xsl:if>				
				   <xsl:if test="ed">
                      <xsl:for-each select="ed">
                          <xsl:value-of select="val"/>
                     </xsl:for-each>
                     </xsl:if>    
			
			<xsl:text>&#34;</xsl:text>
			<xsl:if test="position() != last()">
       			<xsl:value-of select=" ',' "/>
    		</xsl:if>
		</xsl:for-each>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
	
	<!-- Template for footer -->
	<xsl:template name="Footers">
		<xsl:for-each select="c">
			<xsl:text>&#34;</xsl:text>			 
			<xsl:choose>
			   <xsl:when test="cap != '' ">
				<xsl:value-of select="translate(cap,'&#160;',' ')" />	<!-- footers some time having space chars -->
			  </xsl:when>
			  <xsl:otherwise>
				<xsl:value-of select="translate(.,'&#160;',' ')" />
			  </xsl:otherwise>
			</xsl:choose>
			<xsl:text>&#34;</xsl:text>
			<xsl:if test="position() != last()">
   				<xsl:value-of select=" ',' "/>
   			</xsl:if>
		</xsl:for-each>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
</xsl:stylesheet>