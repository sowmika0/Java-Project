<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body topmargin="2" leftmargin="2">
				<title>
					<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/title" />
				</title>
				
				<TABLE cellSpacing="0" cellPadding="0">
					<table>
						<xsl:choose>
							<!-- Process each enquiry page data -->
							<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
								<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
									<xsl:call-template name="Header"/> 
								</xsl:for-each>	
							</xsl:when>
							<!-- Process enquiry data -->
							<xsl:otherwise>
								<xsl:call-template name="Header"/> 
							</xsl:otherwise>
						</xsl:choose>
					</table>
					<table>
						<xsl:choose>
							<!-- Process each enquiry page data -->
							<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
								<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
									xsl:call-template name="ColumnHeaders"/>
									<xsl:call-template name="Datam"/>
								</xsl:for-each>	
							</xsl:when>
							<!-- Process enquiry data -->
							<xsl:otherwise>
									<xsl:call-template name="ColumnHeaders"/>
									<xsl:call-template name="Datam"/>
							</xsl:otherwise>
						</xsl:choose>
					</table>
					<table>
						<xsl:choose>
							<!-- Process each enquiry page data -->
							<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
								<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
									<xsl:call-template name="Footer"/>
								</xsl:for-each>	
							</xsl:when>
							<!-- Process enquiry data -->
							<xsl:otherwise>
								<xsl:call-template name="Footer"/>
							</xsl:otherwise>
						</xsl:choose>
					</table>
				</TABLE>
			</body>
		</html>
	</xsl:template>
	
	<!-- Template for Header which decides to extract header data page by page or whole -->
	<xsl:template name="Header">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<tr>
					<xsl:for-each select="./header/r">
						<xsl:call-template name="Headers"/>
					</xsl:for-each>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r">
						<xsl:call-template name="Headers"/>
					</xsl:for-each>
				</tr>
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
				<xsl:for-each select="./footer">
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
	<thead>
					<tr>
						<xsl:for-each select="c">
							<xsl:choose>
                                <xsl:when test="cap != '' ">
                    <td scope="col">             
                                        <b>
											<font color="#FF8400">
                                                <xsl:value-of select="cap"/>
                                            </font>
                                        </b>
                    </td>             
								</xsl:when>
                                <xsl:otherwise>
									<xsl:choose>
										<!--Don't display field display type(ie:ENQ-H-DATA etc..)in the header portion -->
										<xsl:when test="class!=''">
							<td scope="col">	
												<b>
													<font color="#FF8400">
														<xsl:value-of select="cap" />
													</font>
												</b>
							</td>	
										</xsl:when>
										<xsl:otherwise>
							<td scope="col">	
												<b>
													<font color="#FF8400">
														<xsl:value-of select="." />
													</font>
												</b>
							</td>	
										</xsl:otherwise>
									</xsl:choose>	
                                </xsl:otherwise>
                            </xsl:choose>
						</xsl:for-each>
					</tr>
	</thead>
	</xsl:template>
						
	<!-- Template for column headers -->
	<xsl:template name="ColumnHeaders">
		<tr>
			<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols">
				<xsl:for-each select="c">
					<th scope="col">
						<b>
							<font color="#FF8400">
								<xsl:value-of select="." />
							</font>
						</b>
					</th>
				</xsl:for-each>
			</xsl:for-each>
		</tr>
	</xsl:template>
	
	<!-- Template for data -->
	<xsl:template name="Data">
		<xsl:choose>
				<xsl:when test="position() mod 2 = 0">
					<td>
						<tr>
							<xsl:for-each select="c">
								<td bgColor="white">
								<xsl:if test="cap">
					<xsl:value-of select="normalize-space(cap)"/>
				</xsl:if>				
				   <xsl:if test="ed">
                      <xsl:for-each select="ed">
                          <xsl:value-of select="val"/>
                     </xsl:for-each>
                     </xsl:if> 
								</td>
							</xsl:for-each>
						</tr>
					</td>
				</xsl:when>
				<xsl:otherwise>
					<td>
						<tr>
							<xsl:for-each select="c">
								<td bgColor="#eeeeee">
								<xsl:if test="cap">
					<xsl:value-of select="normalize-space(cap)"/>
				</xsl:if>				
				   <xsl:if test="ed">
                      <xsl:for-each select="ed">
                          <xsl:value-of select="val"/>
                     </xsl:for-each>
                     </xsl:if>    
									
								</td>
							</xsl:for-each>
						</tr>
					</td>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<!-- Template for footers -->
	<xsl:template name="Footers">
		<tr>
			<tr>
				<xsl:for-each select="c">
					<xsl:choose>
						<xsl:when test="cap != '' ">
							<td>
								<b>
									<font color="#FF8400">
										<xsl:value-of select="cap"/>
									</font>
								</b>
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td>
								<b>
									<font color="#FF8400">
										<xsl:value-of select="." />
									</font>
								</b>
							</td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</tr>
		</tr>
	</xsl:template>
</xsl:stylesheet>