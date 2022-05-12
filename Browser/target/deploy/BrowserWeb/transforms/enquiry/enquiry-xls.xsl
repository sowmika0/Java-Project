<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java"
	xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns="urn:schemas-microsoft-com:office:spreadsheet">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes"
		encoding="US-ASCII" />

	<xsl:template match="/">
		<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
			xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel"
			xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:html="http://www.w3.org/TR/REC-html40">
			<DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
				<Author>P.Karthikeyan</Author>
				<LastAuthor>P.Karthikeyan</LastAuthor>
				<Created>2012-10-22</Created>
				<LastSaved>2012-10-22</LastSaved>
				<Company></Company>
				<Version>1.0</Version>
			</DocumentProperties>

			<ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
				<WindowHeight>6150</WindowHeight>
				<WindowWidth>8475</WindowWidth>
				<WindowTopX>120</WindowTopX>
				<WindowTopY>30</WindowTopY>
				<ProtectStructure>False</ProtectStructure>
				<ProtectWindows>False</ProtectWindows>
			</ExcelWorkbook>

			<Styles>
				<Style ss:ID="Default" ss:Name="Normal">
					<Alignment ss:Vertical="Bottom" />
					<Borders />
					<Font />
					<Interior />
					<NumberFormat />
					<Protection />
				</Style>
				<Style ss:ID="s21">
					<NumberFormat ss:Format="mmm\-yy" />
				</Style>
				<Style ss:ID="s22">
					<NumberFormat ss:Format="&quot;&quot;#,##0.00" />
				</Style>
			</Styles>

			<Worksheet ss:Name="Sheet1">
				<Table>
					<xsl:choose>
						<!-- Process each enquiry page data -->
						<xsl:when
							test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData !=''">
							<xsl:for-each
								select="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData">
								<xsl:call-template name="Header" />
								<xsl:call-template name="ColumnHeaders" />
								<xsl:call-template name="Datam" />
								<xsl:call-template name="Footer" />
								<Row />
							</xsl:for-each>
						</xsl:when>
						<!-- Process enquiry data -->
						<xsl:otherwise>
							<xsl:call-template name="Header" />
							<xsl:call-template name="ColumnHeaders" />
							<xsl:call-template name="Datam" />
							<xsl:call-template name="Footer" />
							<Row />
						</xsl:otherwise>
					</xsl:choose>
				</Table>
			</Worksheet>

		</Workbook>
	</xsl:template>


	<!-- Template for headers start -->
	<xsl:template name="Header">
		<xsl:choose>
			<xsl:when
				test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./header/r">
					<xsl:if test="count(c)>0">
						<Row>
							<xsl:call-template name="Headers" />
						</Row>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each
					select="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r">
					<xsl:if test="count(c)>0">
						<Row>
							<xsl:call-template name="Headers" />
						</Row>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="Headers">
		<xsl:for-each select="c">
			<xsl:choose>
				<xsl:when test="cap != '' ">
					<xsl:choose>
						<xsl:when test="i!=''">
							<xsl:choose>
								<xsl:when test="string(number(translate(cap,',','')))='NaN'">
									<Cell>
										<Data ss:Type="String">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="contains(cap,',')">
											<Cell ss:StyleID="s22">
												<Data ss:Type="Number">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:when>
										<xsl:otherwise>
											<Cell>
												<Data ss:Type="Number">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<Cell>
								<Data ss:Type="String">
									<xsl:value-of select="cap" />
								</Data>
							</Cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<!--Don't display field display type(ie:ENQ-H-DATA etc..)in the header 
							portion -->
						<xsl:when test="class!=''">
							<xsl:choose>
								<xsl:when test="i!=''">
									<xsl:choose>
										<xsl:when test="string(number(translate(cap,',','')))='NaN'">
											<Cell>
												<Data ss:Type="String">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="contains(cap,',')">
													<Cell ss:StyleID="s22">
														<Data ss:Type="Number">
															<xsl:value-of select="cap" />
														</Data>
													</Cell>
												</xsl:when>
												<xsl:otherwise>
													<Cell>
														<Data ss:Type="Number">
															<xsl:value-of select="cap" />
														</Data>
													</Cell>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<Cell>
										<Data ss:Type="String">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="i!=''">
									<xsl:choose>
										<xsl:when test="string(number(translate(cap,',','')))='NaN'">
											<Cell>
												<Data ss:Type="String">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="contains(cap,',')">
													<Cell ss:StyleID="s22">
														<Data ss:Type="Number">
															<xsl:value-of select="cap" />
														</Data>
													</Cell>
												</xsl:when>
												<xsl:otherwise>
													<Cell>
														<Data ss:Type="Number">
															<xsl:value-of select="cap" />
														</Data>
													</Cell>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<Cell>
										<Data ss:Type="String">
											<xsl:value-of select="." />
										</Data>
									</Cell>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<!-- Template for headers Ends -->


	<!-- Template for Column headers starts -->
	<xsl:template name="ColumnHeaders">
		<Row>
			<xsl:for-each
				select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols">
				<xsl:for-each select="c">
					<Cell>
						<Data ss:Type="String">
							<xsl:value-of select="." />
						</Data>
					</Cell>
				</xsl:for-each>
			</xsl:for-each>
		</Row>
	</xsl:template>
	<!-- Template for Column headers Ends -->


	<!-- Template for data portion starts -->
	<xsl:template name="Datam">
		<xsl:choose>
			<xsl:when
				test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./r">
					<Row>
						<xsl:call-template name="Data" />
					</Row>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each
					select="/responseDetails/window/panes/pane/dataSection/enqResponse/r">
					<Row>
						<xsl:call-template name="Data" />
					</Row>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Template for data -->
	<xsl:template name="Data">
		<xsl:for-each select="c">
			<xsl:choose>
				<xsl:when test="i!=''">
					<xsl:choose>
						<xsl:when test="string(number(translate(cap,',','')))='NaN'">
							<Cell>
								<Data ss:Type="String">
									<xsl:value-of select="cap" />
								</Data>
							</Cell>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="contains(cap,',')">
									<Cell ss:StyleID="s22">
										<Data ss:Type="Number">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:when>
								<xsl:otherwise>
									<Cell>
										<Data ss:Type="Number">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<Cell>
						<Data ss:Type="String">
							<xsl:value-of select="cap" />
						</Data>
					</Cell>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<!-- Template for data portion starts -->


	<!-- Template for footer which decides to extract footer data page by page 
		or whole -->
	<xsl:template name="Footer">
		<xsl:choose>
			<xsl:when
				test="/responseDetails/window/panes/pane/dataSection/enqResponse/pageData!=''">
				<xsl:for-each select="./footer/r">
					<xsl:if test="count(c)>0">
						<Row>
							<xsl:call-template name="Footers" />
						</Row>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each
					select="/responseDetails/window/panes/pane/dataSection/enqResponse/footer/r">
					<xsl:if test="count(c)>0">
						<Row>
							<xsl:call-template name="Footers" />
						</Row>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Template for footer -->
	<xsl:template name="Footers">
		<xsl:for-each select="c">
			<xsl:choose>
				<xsl:when test="i!=''">
					<xsl:choose>
						<xsl:when test="string(number(translate(cap,',','')))='NaN'">
							<Cell>
								<Data ss:Type="String">
									<xsl:value-of select="cap" />
								</Data>
							</Cell>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="contains(cap,',')">
									<Cell ss:StyleID="s22">
										<Data ss:Type="Number">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:when>
								<xsl:otherwise>
									<Cell>
										<Data ss:Type="Number">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="i!=''">
							<xsl:choose>
								<xsl:when test="string(number(translate(cap,',','')))='NaN'">
									<Cell>
										<Data ss:Type="String">
											<xsl:value-of select="cap" />
										</Data>
									</Cell>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="contains(cap,',')">
											<Cell ss:StyleID="s22">
												<Data ss:Type="Number">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:when>
										<xsl:otherwise>
											<Cell>
												<Data ss:Type="Number">
													<xsl:value-of select="cap" />
												</Data>
											</Cell>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<Cell>
								<Data ss:Type="String">
									<xsl:value-of select="." />
								</Data>
							</Cell>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>