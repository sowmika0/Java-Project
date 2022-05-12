<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

	<xsl:template match="/">
		<enquiry>
			<title>
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/title" />
			</title>
			
			<xsl:choose>
				<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
					<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
						<header>
							<!-- Process headers for each page -->
							<xsl:call-template name="Header"/>
							<!-- Process column headers for each page -->
							<xsl:call-template name="ColumnHeaders"/>
						</header>
						<data>
							<!-- Process data for each page -->
							<xsl:call-template name="Datam"/>
						</data>
						<footer>
							<!-- Process footers for each page -->
							<xsl:call-template name="Footer"/>
						</footer>
					</xsl:for-each>
				</xsl:when>
				<!-- Process whole enquiry data -->
				<xsl:otherwise>
					<header>
						<xsl:call-template name="Header"/> 
						<xsl:call-template name="ColumnHeaders"/>
					</header>
					<data>
						<xsl:call-template name="Datam"/>
					</data>
					<footer>
						<xsl:call-template name="Footer"/>
					</footer>
				</xsl:otherwise>
			</xsl:choose>
			
		</enquiry>
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
					<xsl:text> </xsl:text>
					<xsl:text>&#xa;</xsl:text>
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
				<xsl:for-each select="./footer">
					<xsl:call-template name="Footers"/>
					<xsl:text> </xsl:text>
					<xsl:text>&#xa;</xsl:text>
				</xsl:for-each>
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
			<row>
				<xsl:for-each select="c">
					<xsl:choose>
                           <xsl:when test="cap != '' ">
                               <caption>
                                   <xsl:value-of select="cap"/>
                               </caption>
                           </xsl:when>
                           <xsl:otherwise>
							<xsl:choose>
                                   <!--Don't display field display type(ie:ENQ-H-DATA etc..)in the header portion -->
                                   <xsl:when test="class!=''">
									<caption>
										<xsl:value-of select="cap" />
									</caption>
									</xsl:when>
									<xsl:otherwise>											
										<caption>
											<xsl:value-of select="." />
										</caption>
									</xsl:otherwise>
								</xsl:choose>	
                            </xsl:otherwise>
                        </xsl:choose>
					</xsl:for-each>
				</row>
	</xsl:template>
	
	
	<!-- Template for column headers -->
	<xsl:template name="ColumnHeaders">	
		<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols">
			<columnheaders>
				<xsl:for-each select="c">
				    <columnheader>
						<xsl:value-of select ="."/>
					</columnheader>
				</xsl:for-each>
			</columnheaders>
		</xsl:for-each>
	</xsl:template>
			
	
	<!-- Template for data -->
	<xsl:template name="Data">
		<row>
			<xsl:for-each select="c">
				<column>
					<xsl:if test="cap">
					<xsl:value-of select="normalize-space(cap)"/>
				</xsl:if>				
				   <xsl:if test="ed">
                      <xsl:for-each select="ed">
                          <xsl:value-of select="val"/>
                     </xsl:for-each>
                     </xsl:if>
				</column>
			</xsl:for-each>
		</row>
	</xsl:template>	
	
	<!-- Template for footers -->
	<xsl:template name="Footers">
		<row>
			<xsl:for-each select="c">
				<xsl:choose>
					<xsl:when test="cap != '' ">
						<caption>
							<xsl:value-of select="cap"/>
						</caption>
					</xsl:when>
					<xsl:otherwise>
						<caption>
							<xsl:value-of select="." />
						</caption>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</row>
	</xsl:template>
</xsl:stylesheet>