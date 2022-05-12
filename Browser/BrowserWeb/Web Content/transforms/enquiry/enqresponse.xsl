<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="../toolbars/breadCrumbs.xsl"/>
	<xsl:import href="../toolbars/tool.xsl"/>
    <xsl:import href="graphEnquiry.xsl"/>
    <xsl:import href="enquirylist.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin" select="string(/responseDetails/userDetails/skin)"/>
	<xsl:variable name="imgRoot" select="concat('../plaf/images/', $skin)"/>
	<!--  Get the enquiry id for future reference -->
    <xsl:variable name="enquiryId" select="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid"/>
    
	<!-- Extract the company name -->
	<xsl:variable name="companyName" select="string(/responseDetails/userDetails/companyId)"/>
			
	<!-- Extract the dropfield -->
	<xsl:variable name="dropfield">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/df">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/df"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:template match="enqResponse" name="enqResponse_n">

		<!-- The form that we use to submit -->
		<form name="enquiry{$formFragmentSuffix}" id="enquiry{$formFragmentSuffix}" method="POST" action="BrowserServlet">

			<input type="hidden" name="requestType" value="{$_OFS__ENQUIRY_}"/>
			<input type="hidden" name="routineName" value=""/>
			<input type="hidden" name="routineArgs"/>
			<input type="hidden" name="command" value="globusCommand"/>
			<input id ="enqid" type="hidden" name="enqid" value="{enqid}"/>
			<input type="hidden" name="enqtitle" value="{title}"/>
			<input id ="dropfield" type="hidden" name="dropfield" value="{$dropfield}"/>
			<input type="hidden" name="drilltypes" value="{DrillTypes}"/>
			<input type="hidden" name="drillData" value="{DrillData}"/>
			<input id ="previousEnqs" type="hidden" name="previousEnqs" value="{previousEnqs}"/>
			<input id ="previousEnqTitles" type="hidden" name="previousEnqTitles" value="{previousEnqTitles}"/>
			<input type="hidden" name="application" value="{$_ENQ_}"/>
			<input type="hidden" name="version" value="{enqid}"/>
			<input id ="currentPage" type="hidden" name="currentPage" value="{control/toolbars/pagetoolbar/tool[ty = 'PAGERANGE']/cur}"/>
			<!-- if a deal slip is being returned for local printing then add the following field -->
			<!-- ty refers to type -->
			<xsl:if test="(control/ty='PRINT.DEAL.SLIP')and(control/printLocation='LOCAL')">
				<input type="hidden" name="dealSlipAction" id="dealSlipAction" value="{$_PRINT__DEAL__SLIP_}"/>
			</xsl:if>
			<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value=""/>
			<!-- include element to identify fastpath -->                                 
			<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
				<input type="hidden" name="fastpath" id="fastpath" value="YES"/>
			</xsl:if>
			<!-- include element to identify multi-download -->                                 
			<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multiDownload ='YES')">
				<input type="hidden" name="multiDownload" id="multiDownload" value="YES"/>
			</xsl:if>			
			<!-- Process User Details -->
			<xsl:call-template name="userDetails"/>

			<!-- collapsible column numbers -->
			<input type="hidden" id="collapse" name="collapse" value="{collapse}"/>
			<input type="hidden" name="unlock" value=""/>

		</form>

		<xsl:variable name="justdolist">
			<xsl:for-each select="../atts/att"><xsl:if test=".='VIEWLIST'"><xsl:value-of select="."></xsl:value-of></xsl:if></xsl:for-each>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$justdolist!=''"><xsl:call-template name="enquiryList"/></xsl:when>
			<xsl:otherwise><xsl:call-template name="enquiryNormal"/></xsl:otherwise>	
		</xsl:choose>		
	</xsl:template>
		
	<xsl:template name="enquiryNormal">
		<xsl:call-template name="graphandchart"/>

		<!-- Enquiry Toolbar -->

		<div>
		<xsl:attribute name="class">
		  <xsl:call-template name="apply_Style">
		    <xsl:with-param name="actualclass" select="'denqresponse'"/>
		  </xsl:call-template>
		</xsl:attribute>
			<!-- Enquiry Breadcrumbs -->
			<xsl:if test="control/crumbs/tool !='' ">
				<xsl:call-template name="breadCrumbs"/>
			</xsl:if>

			<!-- Enquiry Information messages --><!-- if no toolbar then dont bother -->
			<xsl:if test="(not(control/noToolBar) and ($stripFrameToolbars = 'false') and not(control/toolbars/maintoolbar)) or (control/toolbartype='CUSTOM')">
				<div style="width:98%">
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'position_box'"/>
						</xsl:call-template>
					</xsl:attribute>
					<table id="enquiryResponseInfo"  summary="">
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enqbreadcrumbs'"/>
						</xsl:call-template>
					</xsl:attribute>
						<tr height="15">
							<!-- Any messages -->
							<td height="15" nowrap="nowrap">
								<xsl:value-of select="control/msg"/>
							</td>
							<!-- Tools -->
							<xsl:for-each select="control/toolbars">
								<td>
									<xsl:apply-templates select="."/>
								</td>
							</xsl:for-each>
							<xsl:if test="control/auto!=''">
								<!-- Auto refresh Timer -->
								<td>
									<input id="refreshtime{$formFragmentSuffix}" size="1" readonly="readonly" value="{control/auto}" onkeydown = "doNothingOnEnterKey(event)"/>
								</td>
							</xsl:if>
							<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
								<td>
									<!-- Options and go button - if there are some options -->
									<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d/item != ''">
										<select id="fastPathType">
											<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
												<option value="{item}">
													<xsl:value-of select="cap"/>
												</option>
											</xsl:for-each>
										</select>
										<a onclick="javascript:doFPCommit()" href="javascript:void(0)">
											<img src="{$imgRoot}/menu/go.gif"/>
										</a>
									</xsl:if>
								</td>
							</xsl:if>
							<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multiDownload ='YES')">
								<td>
									<a onclick="javascript:doMDCommit()" href="javascript:void(0)">
										<img src="{$imgRoot}/tools/multidownload.gif"/>
									</a>
								</td>
							</xsl:if>							
							<td>
								<a onclick="javascript:toggleitemdisplay('enqinfo');" href="javascript:void(0)" accesskey="">
									<img src="{$imgRoot}/about.gif" alt="{title} - {enqid} : {$companyName}" title="{title} - {enqid} : {$companyName}"/>
								</a>
							</td>
							<td>
								<div id="enqinfo{$formFragmentSuffix}">
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'enqinfo'"/>
									</xsl:call-template>
								</xsl:attribute>
									<xsl:if test="/responseDetails/userDetails/multiCo='Y'"><xsl:value-of select="$companyName"/> : </xsl:if><xsl:value-of select="title"/>
								</div>
							</td>
						</tr>
					</table>
				</div>
			</xsl:if>

			<!--- End of Toolbar -->


			<xsl:choose>
				
				<xsl:when test="control/toolbars/pagetoolbar/tool[ty = 'PAGERANGE']/max = 0 and (control/customToolbar) and not(control/noToolBar)">
					<div>
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'display_box'"/>
									</xsl:call-template>
								</xsl:attribute>
						<table summary="">
							<tr>
								<xsl:call-template name="custom_toolbar"/>
							</tr>
						</table>
					</div>
				</xsl:when>
				
				<!-- special case for new enquiry layout, if we have no data (current page is 0) then show just the toolbar -->
				<xsl:when test="control/toolbars/pagetoolbar/tool[ty = 'PAGERANGE']/max = 0 and ($stripFrameToolbars = 'false') and not(control/noToolBar)">
					<div>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'display_box'"/>
							</xsl:call-template>
						</xsl:attribute>
						<table summary="">
							<tr>
								<xsl:call-template name="new_main_toolbar"/>
							</tr>
						</table>
					</div>
				</xsl:when>

				<!-- ONLY show the main "form" if there is some data! -->
				<xsl:when test="r!=''">
					<!--- Start of header -->
					<form>
						<xsl:choose>
							<xsl:when test="appEnq='YES'">
								<xsl:attribute name="name">appreq</xsl:attribute>
	
	                            <!-- Add the id, with fragment suffix for noFrames mode ....-->
	                            <xsl:attribute name="id">appreq<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
	                
								<xsl:attribute name="method">POST</xsl:attribute>
								<xsl:attribute name="onsubmit">javascript:doDeal('')</xsl:attribute>
								<xsl:attribute name="action">BrowserServlet</xsl:attribute>
								<input type="hidden" name="command" value="globusCommand"/>
								<input type="hidden" name="requestType" value="{$_OFS__APPLICATION_}"/>
								<input type="hidden" name="ofsOperation" value="{$_VALIDATE_}"/>
								<input type="hidden" name="ofsFunction" value=""/>
								<input type="hidden" name="GTSControl" value=""/>
								<input type="hidden" name="routineName" value=""/>
								<input type="hidden" name="routineArgs" value=""/>
								<input type="hidden" name="unlock" value=""/>
								<input type="hidden" name="activeTab" id="activeTab" value="tab1"/>
								<input type="hidden" name="expansionHistory" id="expansionHistory" value="{expansionHistory}"/>
								<input type="hidden" name="version" value=""/>
								<input type="hidden" name="application" value="{tgtApp}"/>
								<input type="hidden" name="name" value=""/>
								<input type="hidden" name="operation" value=""/>
								<input type="hidden" name="windowName" value=""/>
								<input type="hidden" name="toolbarTarget" value=""/>
								<input type="hidden" name="overridesAccepted" id="overridesAccepted" value=""/>
								<input type="hidden" name="enqname" value=""/>
								<input type="hidden" name="enqaction" value=""/>
								<input type="hidden" name="dropfield" value=""/>
								<input type="hidden" name="warningsPresent" value=""/>
								<input type="hidden" name="previousEnqs" value=""/>
								<input type="hidden" name="previousEnqTitles" value=""/>
								<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value=""/>
								<input type="hidden" name="windowSizes" value="{/responseDetails/windowCoordinates/windowHeight}"/>
								<input type="hidden" name="changedFields" value=""/>
								<input type="hidden" name="PastedDeal" value=""/>   
								<input type="hidden" name="appEnq" value="YES"/>    
	
								<xsl:call-template name="userDetails"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="name">enquiryData<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
								<xsl:attribute name="id">enquiryData<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>
						<table id="enq_toolHeader" summary="">
					    	<xsl:choose>
					    		<xsl:when test="not(control/noToolBar) and (control/customToolbar)">
									<xsl:call-template name="custom_toolbar"/>
								</xsl:when>
						    	<xsl:otherwise>
						    		<xsl:if test="not(control/noToolBar) and ($stripFrameToolbars = 'false') and (control/toolbars/maintoolbar)">
  										<xsl:call-template name="new_main_toolbar"/>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>				    
						</table>

						<div id="enquiry_response{$formFragmentSuffix}">	
							<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/control/graphImage or /responseDetails/window/panes/pane/dataSection/enqResponse/control/GraphEnq or /responseDetails/window/panes/pane/dataSection/enqResponse/control/graphError ">
								<xsl:attribute name="style">border:none;background:none</xsl:attribute>	
							</xsl:if>	
					    	<xsl:choose>
					    		<xsl:when test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'SINGLE.BACKGROUND.COLOUR')">
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'enquiry_response display_box colour0'"/>
											</xsl:call-template>
										</xsl:attribute>
								</xsl:when>
						    	<xsl:otherwise>
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'enquiry_response display_box'"/>
										</xsl:call-template>
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>				    
							<xsl:attribute name="onclick">raiseEnquiryClick( event, this, '<xsl:value-of select="$enquiryId"/>');</xsl:attribute>
							<xsl:if test="appEnq='YES'">
								<div>
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'app-enq-hdr'"/>
										</xsl:call-template>
									</xsl:attribute>
									<p>
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'dealtitle'"/>
											</xsl:call-template>
										</xsl:attribute>
									<xsl:value-of select="tgtApp"/>
									</p>
									<p>
										<xsl:if test="/responseDetails/userDetails/multiCo='Y'">
											<span><xsl:value-of select="$companyName"/>: </span>
										</xsl:if>
										<input  type="text" name="transactionId" id="transactionId" value="{appEnqId}" size="{keyLength}" readonly="readonly">
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'dealbox'"/>
											</xsl:call-template>
										</xsl:attribute>
										</input>
									</p>
								</div>
							</xsl:if>
	
							<table id="enquiryResponseData{$formFragmentSuffix}">
								<xsl:attribute name="summary">
							      <xsl:choose>
							       <xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/enqDesc != ''">
					  				 <xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/enqDesc"/>
					  			   </xsl:when>
					  			   <xsl:otherwise>
					  			      <xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/title"/>
					  			   </xsl:otherwise>	 
					  			  </xsl:choose>	 
							   </xsl:attribute>
								 <tbody>
								<!-- Is there a GraphEnq tag in our response, if so call the Graph template, otherwise proceed normally.-->
								<xsl:choose>
									<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/control/GraphEnq">
										<tr>
											<td>
												<xsl:call-template name="graphEnquiry"/>
											</td>
										</tr>
									</xsl:when>
									<!-- If there's an error generating the graph image, then display the message -->
									<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/control/graphError">
										<tr>
											<td>
												<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/control/graphError"/>
											</td>
										</tr>
									</xsl:when>
									<!-- If PNG images have been selected instead of svg charts, then display the image with the GraphSerlvet. -->
									<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/control/graphImage">
										<tr>
											<td>
												<img src="GraphServlet?dataLocator={/responseDetails/window/panes/pane/dataSection/enqResponse/control/graphImage}"/>
											</td>
										</tr>
									</xsl:when>
									<xsl:otherwise>
										<!-- process enquiry data -->
										<xsl:call-template name="enqyiryMainTable"/>
									</xsl:otherwise>
								</xsl:choose>
								</tbody>					
							</table>
						</div>
				
					</form>
				</xsl:when>
			</xsl:choose>

		</div>

	</xsl:template>
	
	<!-- main enquiry table e.g. no forms, toolbar buttons etc -->
	<xsl:template name="enqyiryMainTable">

		<tr>
			<td colspan="98">
				<xsl:variable name="classname">
					caption<xsl:if test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'SINGLE.BACKGROUND.COLOUR')"> colour0</xsl:if>
				</xsl:variable>
				
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="$classname"/>
						</xsl:call-template>
					</xsl:attribute>
				
				
			
				<table id="enquiryHeaderContainer"  summary="">
					<!-- Main toolbar -->
					         <!-- moved to the top -->
					<!-- Paging toolbar -->
					<thead>
					<xsl:if test="control/setPagingPosition">
					<!-- enquiry attribute has been set to display this inside the enquiry header -->
				    <tr>
				    	<td>
							<xsl:call-template name="addPagingToolbar"/>
						</td>
					</tr>
					</xsl:if>

					<tr>
						<th scope="col">
							<table  id="enqheader" tabindex="-1" summary="">
	                        <!-- Apply a new style for the enquiry header which have the attribute "NO.HEADER.STYLE"-->							
									<xsl:choose>
										<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r/noHeaderStyle"> 
											<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'noenqheaderStyle'"/>
											</xsl:call-template>
											</xsl:attribute>
										</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'enqheader'"/>
											</xsl:call-template>
										</xsl:attribute>
									</xsl:otherwise> 
								</xsl:choose>
								<xsl:for-each select="header">
									<!-- Now the HEADER data for each row - just treat it like a normal row-->
									<xsl:variable name="ofxSupport" select="boolean(/responseDetails/window/panes/pane/dataSection/enqResponse/header/r/c[starts-with(cap,'OFX')])"/>
									<xsl:for-each select="r">
										<!-- Don't show OFX header field, it only represents boolean variable -->
										<xsl:if test="not($ofxSupport) or not(boolean(c[starts-with(cap,'OFX')]))">
											<xsl:call-template name="r_n">
												<xsl:with-param name="headerrow" select="'true'"/>
											</xsl:call-template>
										</xsl:if>
									</xsl:for-each>
								</xsl:for-each>
							</table>
						</th>


					</tr>
					</thead>
				</table>
			</td>
			<xsl:if test="($stripFrameToolbars = 'false') and ($isArc = 'false')">
				<xsl:if test="control/toolbars/newtabtoolbar/tool != ''">  
					<td width="1">
							<xsl:for-each select="control/toolbars/newtabtoolbar/tool">
								<xsl:apply-templates select="."/>
							</xsl:for-each>				
					</td>
				</xsl:if>			
			</xsl:if>
		</tr>
		
		<!--End of header, start of data body -->
		<tr>
			<td/>
			<td>
				<!-- Column headings -->
				<!-- The column headings are in a different container to the data! Complicated, but means they stay static while the data scrolls -->
				<xsl:if test="not(r/tree) and ($isArc = 'false')">
				<div id="enquiryHeadingScroller{$formFragmentSuffix}" style="overflow:hidden;">
					<div id="enquiryHeadingSizer{$formFragmentSuffix}" style="border:0;padding:0;margin:0;">
							<table id="headingdisplay{$formFragmentSuffix}" class="enquirydata"	summary="">
								<xsl:call-template name="enqTableHeads" />	
							</table>
						</div>
					</div>
										</xsl:if>
				<xsl:if test="not(r/tree) and ($isArc = 'true')">
					<div id="enquiryHeadingScroller{$formFragmentSuffix}" style="overflow:hidden;">
						<div id="enquiryHeadingSizer{$formFragmentSuffix}" style="border:0;padding:0;margin:0;">
							<table id="headingdisplay{$formFragmentSuffix}" class="enquirydata" summary="">
								<xsl:call-template name="enqTableHeads"/>
					</table>
					</div>
				</div>
				</xsl:if>

				<!-- And this is the fast path iframe div -->
				<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
					<div id="fpsubmitheader" name="fpsubmitheader" style="visibility:hidden; height:0">
						<iframe id="iframe:fpsubmitwindow" src="../html/blank_enrichment.html" style="visibility:hidden; height:0; width:0" name="iframe:fpsubmitwindow"></iframe>
					</div>
				</xsl:if>

				<!-- And this is the multidownload iframe div -->
				<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multidownload ='YES')">
					<div id="mdsubmitheader" name="mdsubmitheader" style="visibility:hidden; height:0">
						<iframe id="iframe:mdsubmitwindow" src="../html/blank_enrichment.html" style="visibility:hidden; height:0; width:0" name="iframe:mdsubmitwindow"></iframe>
					</div>
				</xsl:if>
				
				<!-- Add a horizontal rule beneath the header if required (attribute in ENQUIRY) -->
				<xsl:if test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'UNDERLINE.HEADER.ROW')">
					<div>
					 
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enq-underlineHeaderRow'"/>
						</xsl:call-template>
					</xsl:attribute>
					
					</div>
				</xsl:if>

				<!-- The data div is here -->
				<div id="enquiryDataScroller{$formFragmentSuffix}" onscroll="scrollHeading();">
					<xsl:if test="(r/tree)"> 
								<xsl:attribute name="isTree">YES</xsl:attribute>
					</xsl:if> 
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enquiryDataScroller'"/>
						</xsl:call-template>
					</xsl:attribute>
					
					<div id="enquiryDataSizer{$formFragmentSuffix}" style="border:0;padding:0;margin:0;">
					<table id="datadisplay{$formFragmentSuffix}" summary="">
						
						<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enquirydata'"/>
						</xsl:call-template>
						</xsl:attribute>
						<xsl:if test="(r/tree)">
								<xsl:call-template name="enqTableHeads"/>
						</xsl:if>
						
						<xsl:call-template name="handleMinWidthColumns"/>
						<xsl:call-template name="addBlankRowForCollapsibleRows"/>

						<!-- Now the data for each row -->
						<xsl:for-each select="r">
							<xsl:call-template name="r_n"/>
						</xsl:for-each>
		
					</table>
					<!-- start of footer -->
					 <xsl:choose>
									<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/footer/r/noFooterStyle"> 
					                <table class="noenqfooterStyle">
					                   <xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/footer/r">
					                     <xsl:call-template name="r_n"/>
					                     </xsl:for-each>
						            </table>
						            </xsl:when>
									<xsl:otherwise>
									   <table class="enqfooter">
					                   <xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/footer/r">
					                     <xsl:call-template name="r_n"/>
					                     </xsl:for-each>
						            </table>
									</xsl:otherwise> 
								</xsl:choose>
					<!-- end of footer -->
					<xsl:choose>
						<xsl:when test="control/setPagingPosition">
						</xsl:when>
						<xsl:otherwise>
						<!-- by default display this toolbar at the bottom of the enquiry -->
							<xsl:call-template name="addPagingToolbar"/>
						</xsl:otherwise>
					</xsl:choose>
					</div>
				</div>
			</td>
			<td/>
		</tr>
		<!--End of body, add a spacer line -->

	</xsl:template>

	<xsl:template name="addPagingToolbar">
		<xsl:if test="control/toolbars/pagetoolbar/tool[ty = 'PAGERANGE']/max &gt; 1">
			<div>
				
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'paging-toolbar'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="control/setPagingPosition">
					</xsl:when>
					<xsl:otherwise>
					<!-- paging toolbar appears at the bottom of the enquiry. Therefore align it center... -->
							<xsl:attribute name="align">center</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:for-each select="control/toolbars">
					<xsl:call-template name="toolbars_n">
						<xsl:with-param name="id" select="'page'" />
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</xsl:if>
  	</xsl:template>

	<xsl:template name="handleMinWidthColumns">
		<!-- Add a 'colgroup' if they are trying to set the column widths on the table -->
		<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/colgroup)">
		
			<!-- Alignment in case of enquiries with hidden columns -->
			<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/collapse='')">
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enquirydata wrap_words'"/>
				</xsl:call-template>
			</xsl:attribute>
			</xsl:if>
			
			<colgroup>
				<xsl:for-each select="colgroup/c">
					<col>
						<xsl:attribute name="width"><xsl:value-of select="."/>px</xsl:attribute>
					</col>
				</xsl:for-each>
			</colgroup>
		</xsl:if>
	</xsl:template>

	<xsl:template name="addBlankRowForCollapsibleRows">
		<!-- The first data row normally contains the information about widths when the enquiry is resized.
			However collapsed rows might be 'hidden' and hence their width information gets hidden too.
			Solution is to insert a blank row that is used to space the cells and always remains 'visible' but is 0px high.
			Only required if there are collapsible rows -->
		<xsl:if test="//tree != ''" >
		<!-- For fastpath enquiry two extra blankrows required -->
			<xsl:choose>
				<xsl:when test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
					<tr>
						<td style="padding-top:0px;padding-bottom:0px;height:0px;"/>
						<td style="padding-top:0px;padding-bottom:0px;height:0px;"/>
							<xsl:for-each select="r[1]/c">
								<td style="padding-top:0px;padding-bottom:0px;height:0px;"/>
							</xsl:for-each>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<xsl:for-each select="r[1]/c">
							<td style="padding-top:0px;padding-bottom:0px;height:0px;"/>
						</xsl:for-each>	
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- build a row -->
	<xsl:template match="r" name="r_n">
		<xsl:param name="headerrow" select="'false'"/>
		
		<!-- Give the row an id so that we can find it to hide / show. Used for the tree processing -->
		<!-- But not when the row is actually in the header! -->
		<tr>
			<!-- Set id unless header/footer row. Need double-negative in case <ishead> not there -->
			<xsl:if test="not (ishead != '') and (name(..) != 'footer')">
				<xsl:attribute name="id">r<xsl:value-of select="position()"/><xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
			</xsl:if>

			<!-- whether or not the row has a drill down link on it -->
			<xsl:variable name="qref">
				<xsl:value-of select="ref"/>
			</xsl:variable>
			
			<!-- whether or not to underline the row based on attribute in enquiry-->
			<xsl:variable name="underlineRow">
				<xsl:choose>
					<xsl:when test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'UNDERLINE.DRILL.ROWS') and ($qref!='')"> enq-underlineDrillRows</xsl:when>
					<xsl:when test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'UNDERLINE.ALL.ROWS')"> enq-underlineAllRows</xsl:when>
				</xsl:choose>
			</xsl:variable>
			
			<!-- set the row colour based on row position or attribute in enquiry -->
			<xsl:variable name="rowColourIndex">
				<xsl:choose>
		    		<!-- If the enquiry has the SINGLE.BACKGROUND.COLOUR atribute set then use colour0 or hidden0 -->
		    		<xsl:when test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'SINGLE.BACKGROUND.COLOUR')">0</xsl:when>
		    		<!-- Otherwise use alternating row colours -->
					<xsl:otherwise><xsl:value-of select="position() mod 2"/></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<!-- If tree node present, set class to hidden for the start and intermediate nodes; set class to colourN if header/footer row -->
	    	<xsl:choose>
				<xsl:when test="(tree='start') or (tree='hide')">

					<xsl:variable name="classname">
					    hidden<xsl:value-of select="$rowColourIndex"/><xsl:value-of select="$underlineRow"/>
					</xsl:variable>
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="$classname"/>
						</xsl:call-template>
					</xsl:attribute>

				</xsl:when>
				<xsl:when test="not (ishead != '') and (name(..) != 'footer')">

					<xsl:variable name="classname">
					    colour<xsl:value-of select="$rowColourIndex"/><xsl:value-of select="$underlineRow"/>
					</xsl:variable>
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="$classname"/>
						</xsl:call-template>
					</xsl:attribute>

					<xsl:attribute name="originalClass">colour<xsl:value-of select="$rowColourIndex"/></xsl:attribute>
				</xsl:when>
			</xsl:choose>
			
			<!-- Fire customer event when user hovers over this enquiry row -->
			<xsl:attribute name="onmouseover">raiseEnquiryRowMouseOver( event, this, '<xsl:value-of select="$enquiryId"/>');</xsl:attribute>
			<!-- Fire customer event when user hovers out this enquiry row -->
			<xsl:attribute name="onmouseout">raiseEnquiryRowMouseOut( event, this, '<xsl:value-of select="$enquiryId"/>');</xsl:attribute>			
			<!-- Fire customer event when user clicks this enquiry row -->
			<xsl:attribute name="onclick">raiseEnquiryRowMouseClick( event, this, '<xsl:value-of select="$enquiryId"/>');</xsl:attribute>			                             

			<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
				<xsl:if test="not (ishead != '')">
					<xsl:choose>
						<xsl:when test="$qref!=''">
							<td width="20"><img id="img{ref}" name="img{ref}" src="{$imgRoot}/amberlight.gif"/></td>
							<td width="20"><input type="checkbox" id="chk{ref}" name="ChckBx" value="{ref}+{chksum}" onclick="Toggle(this)" onkeydown="checkSwitch(this)"/></td>
						</xsl:when>
						<xsl:otherwise>
							<td width="20"/><td width="20"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:if>

			<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multiDownload ='YES')">
				<xsl:if test="not (ishead != '')">
					<xsl:choose>
						<xsl:when test="$qref!=''">
							<td width="20"><img id="img{ref}" name="img{ref}" src="{$imgRoot}/download.gif"/></td>
							<td width="20"><input type="checkbox" id="chk{ref}" name="ChckBx" value="{ref}" onclick="Toggle(this)" onkeydown="return false;"/></td>
						</xsl:when>
						<xsl:otherwise>
							<td width="20"/><td width="20"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:if>

			<!--  non-header -->
			<xsl:if test="not (ishead != '')">
				<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/q">
					<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='NO')">
						<td>
							<xsl:if test="$qref!=''"> <!-- The drilldown icon shouldn't be displayed on blank rows (TTS0707203) -->
								<a onclick="javascript:view('{item}','{$qref}')" href="javascript:void(0)">
									<img src="{$imgRoot}/enquiry/drill.gif"/>
								</a>
							</xsl:if>
						</td>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
	
			<!-- go through each column -->
			<xsl:for-each select="c">
				<td>
				<!--Set the align attribute if the field has right justification. -->
                    <xsl:if test="i!=''">
                        <xsl:attribute name="align">right</xsl:attribute>
                    </xsl:if>
					
					<xsl:if test="class">

							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="class"/>
								</xsl:call-template>
							</xsl:attribute>

					</xsl:if>

					<!-- Add the tree items here! -->
					<xsl:if test="endtree">
						<a onclick="javascript:expandrow({../st},{../no})" href="javascript:void(0)">
							<img alt="Expand group" id="treestop{../st}{$formFragmentSuffix}" src="{$imgRoot}/menu/menu_down.gif"/>
						</a>
						<img alt="" src="{$imgRoot}/block.gif" width="2"/>
					</xsl:if>
					<xsl:if test="starttree">
						<a onclick="javascript:expandrow({../st},{../no})" href="javascript:void(0)">
							<img alt="Collapse group" id="treestart{../st}{$formFragmentSuffix}" src="{$imgRoot}/menu/menu_right.gif"/>
						</a>
						<img alt="" src="{$imgRoot}/block.gif" width="2"/>
					</xsl:if>

					<xsl:if test="showgraph!=''">
						<xsl:attribute name="rowspan">99</xsl:attribute>
						<table summary="">
							<tr>
								<td>
									<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills!=''"> Drilldown to :
										<select id="chartdrillbox{$formFragmentSuffix}">
											<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
												<option value="{item}">
													<xsl:value-of select="cap"/>
												</option>
											</xsl:for-each>
										</select>
									</xsl:if>
								</td>
							</tr>
							<tr>
								<td colspan="99">
									<embed src="../plaf/chart/graph.svg" width="600" height="350" type="image/svg+xml"/>
								</td>
							</tr>
						</table>
					</xsl:if>
					
					<!-- MultiPie chart embed -->
					<xsl:if test="mpShowPie='1'">
						<xsl:attribute name="rowspan">99</xsl:attribute>
						<xsl:attribute name="colspan">99</xsl:attribute>
						<embed src="../plaf/chart/mpChart.svg" width="900" height="400" type="image/svg+xml"/>
					</xsl:if>
					<!-- MultiPie chart embed -->
					
					<xsl:if test="showpie!=''">
						<xsl:attribute name="rowspan">99</xsl:attribute>
						<table summary="">
							<tr>
								<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills!=''"> Drilldown to :
									<td>
										<select id="chartdrillbox{$formFragmentSuffix}">
											<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
												<option value="{item}">
													<xsl:value-of select="cap"/>
												</option>
											</xsl:for-each>
										</select>
									</td>
								</xsl:if>
								<td>
									<img alt="Toggle chart" onclick="toggleChart(this)" src="{$imgRoot}/enquiry/bar.gif">
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'clickable'"/>
											</xsl:call-template>
										</xsl:attribute>
									</img>
								</td>
							</tr>
							<tr>
								<td colspan="99">
									<embed src="../plaf/chart/chart.svg" width="600" height="350" type="image/svg+xml"/>
								</td>
							</tr>
						</table>
					</xsl:if>

					<!-- Bar chart display -->
					<xsl:if test="off!=''">
						<img alt="" src="{$imgRoot}/block.gif" width="{off}" height="15px"/>
					</xsl:if>

					<xsl:choose>
						<!-- EDITABLE data columns -->
						<xsl:when test="ed!=''">
							<xsl:choose>
								<xsl:when test="ed/noEdit">
									<input type="hidden" value="{ed/val}" id="{ed/n}" name="{ed/n}" therow="{../ref}" tabname="tab1"/>
									<xsl:value-of select="ed/val"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
											<xsl:variable name="testRow" select="../ref"/>
											<xsl:variable name="testmvDetails" select="ed/mvDetails"/>
											<xsl:choose>	
												<xsl:when test= "$testRow != ''">
													<input type="text" value="{ed/val}" id="{ed/n}" name="{ed/n}" therow="{../ref}" mvDetails="{ed/mvDetails}" tabname="tab1" onkeyPress="javascript:stopPoll()" onChange="javascript:setchecked('chk{../ref}')"/>		
												</xsl:when>
												<xsl:otherwise>
													<xsl:if test= "$testmvDetails !=''">
														<input type="text" value="{ed/val}" id="{ed/n}" name="{ed/n}" therow="{../ref}" mvDetails="{ed/mvDetails}" tabname="tab1" onkeyPress="javascript:stopPoll()" onChange="javascript:setchecked('chk{../ref}')"/>
													</xsl:if>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:otherwise>
											<input type="text" value="{ed/val}" id="{ed/n}" name="{ed/n}" therow="{../ref}" tabname="tab1" onkeyPress="javascript:stopPoll()" onChange="javascript:setchecked('chk{../ref}')"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						
						<!-- Legend Display -->
						<xsl:when test="legend">
							<span> 
							<xsl:variable name="classname"><xsl:value-of select="barColor"/></xsl:variable>

								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="$classname"/>
									</xsl:call-template>
								</xsl:attribute>
								<img style="visibility:hidden;" width="{legend}" height="13px" src="{$imgRoot}/enquiry/bar_space.gif"/>
							</span>
						</xsl:when>
						
						<!-- BAR display -->
						<xsl:when test="bar != ''">
							<span alt="{cap}" title="{cap}" >
								<xsl:variable name="classname"><xsl:value-of select="barColor"/></xsl:variable>

								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="$classname"/>
									</xsl:call-template>
								</xsl:attribute>
								<img style="visibility:hidden;"  width="{bar}" height="13px" src="{$imgRoot}/enquiry/bar_space.gif"/>
							</span>
							
							<img style="visibility:hidden;" alt="{cap}" title="{cap}" height="15px" src="{$imgRoot}/enquiry/bar_space.gif" >
								<xsl:attribute name="width"><xsl:value-of select="100 - bar"/></xsl:attribute>
							</img>
						</xsl:when>
						
						<!-- NEGATIVE BARS -->
						<xsl:when test="neg != ''">
							<span   alt="{cap}" title="{cap}" >
									<xsl:variable name="classname"><xsl:value-of select="barColor"/></xsl:variable>

								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="$classname"/>
									</xsl:call-template>
								</xsl:attribute>
								<img style="visibility:hidden;" width="{neg}" height="13px" src="{$imgRoot}/enquiry/bar_space.gif"/>
							</span>
							<img style="visibility:hidden;" alt="{cap}" title="{cap}" height="15px" src="{$imgRoot}/enquiry/bar_space.gif"  >
								<xsl:attribute name="width"><xsl:value-of select="100 - neg"/></xsl:attribute>
							</img>
						</xsl:when>
						
						<!-- HYPERLINK display -->
						 <xsl:when test="link != ''">
							
						<xsl:if test="substring-after(link,']')!=''">
							<xsl:if test="substring-after(link,'[')!=''">
							<a>
							<xsl:attribute name="title"><xsl:value-of select="substring-after(substring-before(link,']'),'[')"/></xsl:attribute>
							 <xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="substring-before(link,'[')"/>')</xsl:attribute>
							 <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<img>
									<xsl:attribute name="src"><xsl:value-of select="substring-after(link,']')"/></xsl:attribute>
								</img>
							</a>
							</xsl:if>
							<xsl:if test="substring-after(link,'[')=''">
							<a>
							 <xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="substring-before(link,']')"/>')</xsl:attribute>
							 <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
						 	 	
								<img>
									<xsl:attribute name="src"><xsl:value-of select="substring-after(link,']')"/></xsl:attribute>
								</img>
							</a>
						</xsl:if>
						
						</xsl:if>
						<xsl:if test="substring-after(link,']')=''">					

						 	 <xsl:if test="substring-after(link,'[')!=''">
						 	 <a>
							 <xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="substring-before(link,'[')"/>')</xsl:attribute>
							 <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
						 	 	<xsl:value-of select="substring-after(link,'[')"/>
	
						 	 </a>
							</xsl:if>
							<xsl:if test="substring-after(link,'[')=''">
						 	 <a>
							 <xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="link"/>')</xsl:attribute>
							 <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
						 	 	<xsl:value-of select="link"/>
						 	 </a>
							</xsl:if>
						</xsl:if>
				 </xsl:when>
						  <!-- Audio display -->
                        <xsl:when test="sound !=''">
			
                         <xsl:variable name="jinglePath">
                          <xsl:choose>
                                   <xsl:when test="(substring(sound, 1, 1) = '.')">../<xsl:value-of select="sound"/></xsl:when>
                                    <!-- Path name is absolute, just use it as is -->
                                    <xsl:otherwise><xsl:value-of select="sound"/></xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <!-- Populate URL to cloud location if useCloudImage is set  -->
							<a>
								<xsl:attribute name="onclick">
									<xsl:choose>
										<xsl:when test="$useCloudImage='YES'">javascript:downloadCloudFile('<xsl:value-of select="$jinglePath" />')</xsl:when>
										<xsl:otherwise><xsl:value-of select="$jinglePath" /></xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<xsl:copy-of select="$jinglePath" />
							</a>                        </xsl:when>
					    <!-- video display -->
                        <xsl:when test="video !=''">
			
                         <xsl:variable name="cassette">
                          <xsl:choose>
                                   <xsl:when test="(substring(video, 1, 1) = '.')">../<xsl:value-of select="video"/></xsl:when>
                                    <!-- Path name is absolute, just use it as is -->
                                    <xsl:otherwise><xsl:value-of select="video"/></xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <!-- Populate URL to cloud location if useCloudImage is set  -->
							<a>
								<xsl:attribute name="onclick">
									<xsl:choose>
										<xsl:when test="$useCloudImage='YES'">javascript:downloadCloudFile('<xsl:value-of select="$cassette" />')</xsl:when>
										<xsl:otherwise><xsl:value-of select="$cassette" /></xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<xsl:copy-of select="$cassette"/>
							</a>
                        </xsl:when>
						
						<!-- Show first column as pickable list  -->
						<xsl:when test="(position() = 1) and ( $dropfield != '')  and ($headerrow = 'false')">
							<a>
								<xsl:variable name="dropEnri">
									<xsl:choose>
										<xsl:when test="following-sibling::c[2]/cap">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string" select="following-sibling::c[2]/cap" />
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string" select="following-sibling::c[1]/cap" />
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<xsl:variable name="dropId">
									<xsl:call-template name="escape-apos">
										<xsl:with-param name="string">
											<xsl:call-template name="escape-percent">
												<xsl:with-param name="string">
													<xsl:choose>
														<xsl:when test="tar!=''"><xsl:value-of select="tar"/></xsl:when> <!-- target value, is escaped -->
														<xsl:otherwise><xsl:value-of select="cap"/></xsl:otherwise> <!-- normal value -->
													</xsl:choose>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:variable>
								<xsl:attribute name="onclick">javascript:pick('<xsl:value-of select="$dropId"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>')</xsl:attribute>
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<b><xsl:value-of select="cap"/></b>
							</a>
						</xsl:when>

						<!-- Enquiry links for new drilldowns -->
						<xsl:when test="../ref!=''">
							<xsl:variable name="position" select="position()"/>
								<xsl:variable name="dcheck">
									<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
											<xsl:if test="(drillCol = $position) and not(rowEndDrill)">
												<xsl:value-of select="$position"/>
											</xsl:if>
									</xsl:for-each>
						    	</xsl:variable>
								<xsl:choose>
									<xsl:when test="(($position = $dcheck) or (../c[position()]/drillInfo != ''))">
										<xsl:variable name="drillInfo" select="../c[$position]/drillInfo"/>
										<xsl:choose>
											<xsl:when test="($drillInfo='NO')">
												<xsl:value-of select="cap"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:if test="cap!=''">
									 				<a>
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'enqDrillLink'"/>
															</xsl:call-template>
														</xsl:attribute>
													    <xsl:choose>
													          <xsl:when test="(../c[position()]/drillInfo != '')">
														         <xsl:if test="($drillInfo!='NO')">																					
														             <xsl:attribute name="onclick">javascript:drilldown('<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[position()=$drillInfo]/item"/>','<xsl:value-of select="../ref"/>')</xsl:attribute>
														             <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
 										              			     <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[position()=$drillInfo]/cap"/></xsl:attribute>
													                 <xsl:value-of select="cap"/>
												                 </xsl:if>
														       </xsl:when>	 
															   <xsl:otherwise>	
													                 <xsl:attribute name="onclick">javascript:drilldown('<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[drillCol=$position]/item"/>','<xsl:value-of select="../ref"/>')</xsl:attribute>
 													                 <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
 													                 <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[drillCol=$position]/cap"/></xsl:attribute>
													                 <xsl:value-of select="cap"/>
													           </xsl:otherwise>    
													    </xsl:choose>        
													</a> 	
												</xsl:if>
												<!-- Display image as hyperlink -->
												<xsl:if test="img!=''">
													<a>
														<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'enqDrillImage'"/>
														</xsl:call-template>
														</xsl:attribute>
													<xsl:choose>
													     <xsl:when test="(../c[position()]/drillInfo != '')">
														    <xsl:if test="($drillInfo!='NO')">
															    <xsl:attribute name="onclick">javascript:drilldown('<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[$drillInfo]/item"/>','<xsl:value-of select="../ref"/>')</xsl:attribute>
														        <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														        <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[$drillInfo]/cap"/></xsl:attribute>
				                                            </xsl:if> 	
														</xsl:when>
														<xsl:otherwise>	
														        <xsl:attribute name="onclick">javascript:drilldown('<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[drillCol=$position]/item"/>','<xsl:value-of select="../ref"/>')</xsl:attribute>
														        <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														        <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d[drillCol=$position]/cap"/></xsl:attribute>
														</xsl:otherwise>      
												   </xsl:choose>		  
														<img>
															<xsl:attribute name="src"><xsl:value-of select="img"/></xsl:attribute>
														</img>
													</a>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>																				
									</xsl:when>
									<xsl:otherwise>
										<!-- No hyperlink -->
										<xsl:choose>
											<xsl:when test="img!=''">
												<xsl:variable name="imgPath">
														<xsl:choose>	
															<xsl:when test="(substring(img, 1, 3) = '../')"><xsl:value-of select="img"/></xsl:when>
															<!-- Path begins with '.' so is relative to the web server root and requires an extra '.' -->
															<xsl:when test="(substring(img, 1, 1) = '.')">.<xsl:value-of select="img"/></xsl:when>								
															<xsl:otherwise><xsl:value-of select="img"/></xsl:otherwise>
														</xsl:choose>
												</xsl:variable>
												<xsl:choose>
													<xsl:when test="ImDocLoc!=''">
													<!-- Populate URL to cloud location if useCloudImage is set  -->
													<a>
														<xsl:attribute name="onclick">
															<xsl:choose>
																<xsl:when test="$useCloudImage='YES'">javascript:downloadCloudFile('<xsl:value-of select="$docPath" />')</xsl:when>
																<xsl:otherwise>javascript:fileDownload('<xsl:value-of select="$docPath" />')</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
														<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														<xsl:copy-of select="$docPath" />
													</a>
													</xsl:when>
													<xsl:otherwise>
														<img src="{$imgPath}"> 
															<xsl:attribute name="title">
									 							<xsl:call-template name="image_call">
																	<xsl:with-param name="imgfullPathtest" select="$imgPath"/>
									  							</xsl:call-template> 
								    						</xsl:attribute>
								    					</img>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise>
												<xsl:choose>
													<xsl:when test="cap!=''">
														<xsl:if test="./cap/line!=''">
															<xsl:for-each select="cap/line">
																<xsl:value-of select="."/>
																<xsl:if test="position()!=last()">
																	<br/>
																</xsl:if>
															</xsl:for-each>
														</xsl:if>
														<xsl:if test="not(./cap/line)">
															<xsl:value-of select="cap"/>
														</xsl:if>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="cap"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
						</xsl:when>
						
						<!-- Image display -->
						<xsl:when test="img != ''">
							
								<xsl:choose>
									<xsl:when test="(ImDocLoc='SERVER') and not($useCloudImage='YES')">
										<img>
									    	<!-- To download Images have the path with location SERVER-->
							      		   <xsl:attribute name="src">../servlet/DisplayServerImage?docDownloadPath=<xsl:value-of select="img"/>&amp;downloadLocation=<xsl:value-of select="ImDocLoc"/>
										  </xsl:attribute>
						   			   </img>
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="imgPath">
										<xsl:choose>	
											<!--  Check to see if path already begins with ../ -->
											<!-- if ImgPath from local drive  -->
											<xsl:when test="(substring(img, 1, 8) = '../plaf/')"><xsl:value-of select="img"/></xsl:when>
											<xsl:when test="(substring(img, 1, 3) = '../')">../servlet/RenderImageServlet?filePath=<xsl:value-of select="img"/></xsl:when>
											<!-- Path begins '.' so is relative to the web server root and requires an extra '../' -->
											<xsl:when test="(substring(img, 1, 1) = '.')">../<xsl:value-of select="img"/></xsl:when>								
											<!-- Path name is absolute, just use it as is -->
											<!-- To allow the image to be displayed in FireFox we MUST pre-fix the path
											with 'file://' This is compatible with both FireFox & IE but the Browser web app.
											MUST be added as a 'TRUSTED' site.  In IE use the Options dialog however in Firefox
											the following entries must be put into user.js or prefs.js under:
											C:\Documents and Settings\<user>\Application Data\Mozilla\Firefox\Profiles\	
											entries:
											user_pref("capability.policy.localfilelinks.checkloaduri.enabled", "allAccess");
											user_pref("capability.policy.localfilelinks.sites", "http://localhost:8080");
											user_pref("capability.policy.policynames", "localfilelinks");				     
											for more info see: http://kb.mozillazine.org/Links_to_local_pages_don%27t_work#Disabling_the_Security_Check
											-->
											<xsl:when test="(substring(img, 2, 1) = ':')">../servlet/RenderImageServlet?filePath=<xsl:value-of select="img"/></xsl:when> 
										<xsl:otherwise>file://<xsl:value-of select="img"/></xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
							
								<!-- Populate URL to cloud location if useCloudImage is set  -->
								<img onmousedown = "return false;" onmouseup= "return false;" onclick= "return false;" oncontextmenu="return false;" onerror="this.src='../plaf/images/default/imagenotfound.jpg'">
									<xsl:attribute name="src">
										<xsl:choose>
											<xsl:when test="$useCloudImage='YES'">../GetImage?imageId=<xsl:value-of select="$imgPath"/></xsl:when>
											<xsl:otherwise><xsl:value-of select="$imgPath"/></xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>  
									<xsl:attribute name="title">
										<xsl:call-template name="image_call">
											<xsl:with-param name="imgfullPathtest" select="$imgPath"/>
										</xsl:call-template> 
									</xsl:attribute>
								</img>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>

						
                        <!-- Documents display -->
                        <xsl:when test="document !=''">
                            <xsl:variable name="docPath">
                                <xsl:choose>
                                    <!-- Path begins '.' so is relative to the web server root and  '../' not requires an extra -->
                                    <xsl:when test="(substring(document, 1, 1) = '.')"><xsl:value-of select="document"/></xsl:when>
                                    <!-- Path name is absolute, just use it as is -->
                                    <xsl:otherwise><xsl:value-of select="document"/></xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <!-- Populate URL to cloud location if useCloudImage is set  -->
                            <a>
								<xsl:attribute name="onclick">
									<xsl:choose>
										<xsl:when test="$useCloudImage='YES'">javascript:downloadCloudFile('<xsl:value-of select="$docPath" />')</xsl:when>
										<xsl:otherwise>javascript:fileDownload('<xsl:value-of select="$docPath"/>','<xsl:value-of select="ImDocLoc"/>')</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<xsl:copy-of select="$docPath" />
							</a>
                        </xsl:when>
						<!-- Right Align -->
						<!-- i corresponds to inf -->
						<xsl:when test="i!=''">
							<xsl:attribute name="align">right</xsl:attribute>
							<!-- Show first caption as hyperlink if this was from a dropdown -->
							<xsl:choose>
								<xsl:when test="(position() = 1) and ( $dropfield != '')  and ($headerrow = 'false')">
									<a>
										<xsl:variable name="dropEnri">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string" select="following-sibling::c/cap" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="dropId">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string" select="cap" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:attribute name="onclick">javascript:pick('<xsl:value-of select="$dropId"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>')</xsl:attribute>
										<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
										<b><xsl:value-of select="cap"/></b>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="not(string(number(cap))='sNaN')"> 
										<xsl:attribute name="nowrap">nowrap</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="cap"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<!-- REPORT -->
						<xsl:when test="rpt!=''">
							
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'report'"/>
								</xsl:call-template>
							</xsl:attribute>
							
							<pre>
								<xsl:choose>
									<xsl:when test="./rpt/sp!=''">
										<xsl:for-each select="rpt/sp">
											<xsl:if test="position()!=last()">
												<p class="page_break">
												<xsl:value-of select="."/>
												</p>
											</xsl:if>
											<xsl:if test="position()=last()">
												<p>
												<xsl:value-of select="."/>
												</p>
											</xsl:if>
										</xsl:for-each>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="rpt"/> 
									</xsl:otherwise>
								</xsl:choose>
							</pre>
						</xsl:when>
						
						<!-- DEFAULT DISPLAY TYPE -->
						<xsl:otherwise>
							<!-- Show first caption as hyperlink if this was from a dropdown -->
							<xsl:choose>
								<xsl:when test="(position() = 1) and ( $dropfield != '')  and ($headerrow = 'false')">
									<a>
										<xsl:variable name="dropEnri">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string" select="following-sibling::c/cap" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="dropId">
											<xsl:call-template name="escape-apos">
												<xsl:with-param name="string">
													<xsl:call-template name="escape-percent">
														<xsl:with-param name="string">
															<xsl:choose>
																<xsl:when test="tar!=''"><xsl:value-of select="tar"/></xsl:when> <!-- target value, is escaped -->
																<xsl:otherwise><xsl:value-of select="cap"/></xsl:otherwise> <!-- normal value -->
															</xsl:choose>
														</xsl:with-param>
													</xsl:call-template>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:variable>
										<xsl:attribute name="onclick">javascript:pick('<xsl:value-of select="$dropId"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>')</xsl:attribute>
										<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
										<b><xsl:value-of select="cap"/></b>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="cap!=''">
											<xsl:if test="./cap/line!=''">
												<xsl:for-each select="cap/line">
													<xsl:value-of select="."/>
													<xsl:if test="position()!=last()">
														<br/>
													</xsl:if>
												</xsl:for-each>
											</xsl:if>
											<xsl:if test="not(./cap/line)">
												<xsl:value-of select="cap"/>
											</xsl:if>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
                                            <!--Don't display field display type(ie:ENQ-H-DATA etc..) in the header portion -->
												<xsl:when test="class!=''">
													<xsl:value-of select="cap"/>
                                                </xsl:when>
												<xsl:otherwise>
													<xsl:if test="not(showpie) and not(showgraph)">
														<xsl:value-of select="."/>
													</xsl:if>
                                                </xsl:otherwise>
                                            </xsl:choose>
										</xsl:otherwise>
									</xsl:choose>	
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:for-each>
		   
			<!--Drilldowns -->
			<xsl:if test="ref!=''">
				<!-- Store the reference in a variable as we hand down the drill info only once-->
				<xsl:variable name="myref" select="ref"/>

				<xsl:choose>
					<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/style!=''">
						<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='NO')">
							<!-- Show combo box and Go button when style is combo-->
							<td>
								
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'enqdrilldowncell'"/>
									</xsl:call-template>
								</xsl:attribute>
								
								<table cellSpacing="0" cellPadding="0" border="0" summary="">
									<tr>
									
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'enqdrilldowncell'"/>
										</xsl:call-template>
									</xsl:attribute>
									
										<td>
											<select id="drillbox:{$myref}{$formFragmentSuffix}">
											
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'drillbox'"/>
												</xsl:call-template>
											</xsl:attribute>
											
												<!-- the node d equates to dRow -->
												<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
													<option value="{cap}" item="{item}" ref="{$myref}">
														<xsl:value-of select="cap"/>
													</option>
												</xsl:for-each>
											</select>
										</td>
										<td width="1">
											<a class="iconLink" onclick="javascript:drilldownSelection('drillbox:{$myref}')" href="javascript:void(0)" title="Select Drilldown" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												<img alt="Select Drilldown" src="{$imgRoot}/menu/go.gif"/>
											</a>
										</td>
									</tr>
								</table>
							</td>
						</xsl:if>
					</xsl:when>
					<!-- Otherwise we show the buttons or the images for the drilldown-->
					<xsl:otherwise>
						<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
							<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='NO')">
								<td>
									
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'enqdrilldowncell'"/>
											</xsl:call-template>
										</xsl:attribute>
										
									<xsl:choose>
										<!-- Image when we have it -->
										<xsl:when test="img!=''">
											<a onclick="javascript:drilldown('{item}','{$myref}')" title="{cap}" href="javascript:void(0)" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												<img src="{$imgRoot}/enquiry/drilldown/{img}" alt="{cap}" title="{cap}"/>
											</a>
										</xsl:when>
										<!-- do not display if we have drillLabel -->
									 	<xsl:when test="drillCol!=''">
											<xsl:if test="rowEndDrill!=''">
											<a class="enqDrillLink" onclick="javascript:drilldown('{item}','{$myref}')" href="javascript:void(0)" tabindex="0">
												<xsl:value-of select="cap"/>
											</a>
											</xsl:if>
										</xsl:when>
										<!-- Otherwise the button -->
										<xsl:otherwise>
											<a class="enqDrillLink" onclick="javascript:drilldown('{item}','{$myref}')" href="javascript:void(0)" tabindex="0">
												<xsl:value-of select="cap"/>
											</a>
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:if>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<!--End of Drilldowns -->
		</tr>

		<!-- Add a horizontal rule row if the enquiry has the UNDERLINE.TREE.ROWS attribute set -->
		<xsl:if test="contains(/responseDetails/window/panes/pane/dataSection/atts/.,'UNDERLINE.TREE.ROWS') and (tree='end')">
			<tr><td colspan="100">
			<div>
			
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enq-underlineTreeRows'"/>
				</xsl:call-template>
			</xsl:attribute>
			
			</div></td></tr>
		</xsl:if>
		
	</xsl:template>

	<xsl:template match="toolbars">
		<xsl:call-template name="toolbars_n"/>
	</xsl:template>

	<xsl:template match="collapse" name="columnNo">
		<xsl:value-of select="collapse"/>
	</xsl:template>

	<xsl:template name="graphandchart">
		<xsl:choose>
			<xsl:when test="graph/series/@no!=''">
                <p style="display: none" id="graphData{$formFragmentSuffix}">
					<xsl:for-each select="graph/series">
						<xsl:for-each select="pt"><xsl:value-of select="@x"/>,<xsl:value-of select="@y"/>,<xsl:value-of select="../@no"/>,<xsl:value-of select="drill"/>
							<xsl:if test="position() != last()">|</xsl:if>
						</xsl:for-each>
					</xsl:for-each>
				</p>
			</xsl:when>
			
            <!-- If mpPie tag is not null then generate the following JavaScript -->
            <xsl:when test="mpPie!=''">
                <p style="display: none" id="mpPieData{$formFragmentSuffix}">
					<xsl:for-each select="mpPie/mpSlice"><xsl:value-of select="mpAmount"/>,<xsl:value-of select="mpLabel"/>,<xsl:value-of select="mpLevel"/>
						<xsl:if test="position() != last()">|</xsl:if>
					</xsl:for-each>
				</p>
	            <!-- Create this hidden input field with mpIndicator id. JavaScript will read this field and realise that
	            it has to call mpAddChartValue&nbsp -->
	            <input type="hidden" name="mpInd" id="mpIndicator" value="default" />
            </xsl:when>

			<!--Generate function calls for addChartValue based on the xml data -->			
			<xsl:when test="pie!=''">
                <p style="display: none" id="pieData{$formFragmentSuffix}">
					<xsl:for-each select="pie/slice"><xsl:value-of select="val"/>,<xsl:value-of select="cap"/>,<xsl:value-of select="drill"/>
						<xsl:if test="position() != last()">|</xsl:if>
					</xsl:for-each>
				</p>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="new_main_toolbar">
		<td width="1" id="enqheader-msg" tabindex="-1">
			
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enqheader-msg'"/>
				</xsl:call-template>
			</xsl:attribute>
			
			<span><xsl:value-of select="control/msg"/></span>
		</td>
		<td width="1">
			<xsl:for-each select="control/toolbars">
				<xsl:call-template name="toolbars_n">
					<xsl:with-param name="id" select="'main'"/>
				</xsl:call-template>
			</xsl:for-each>
		</td>
		<xsl:if test="control/auto!=''">
			<!-- Auto refresh Timer -->
			<td width="1">
				<input id="refreshtime{$formFragmentSuffix}" size="1" readonly="readonly" value="{control/auto}" onkeydown = "doNothingOnEnterKey(event)"/>
			</td>
		</xsl:if>
		<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
			<td width="1">
				<!-- Options and go button - if there are some options -->
				<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d/item != ''">
					<table summary="">
						<tr>
							<td>
								<select id="fastPathType">
									<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
										<option value="{item}">
											<xsl:value-of select="cap"/>
										</option>
									</xsl:for-each>
								</select>
							</td>
							<td>
								<a onclick="javascript:doFPCommit()" href="javascript:void(0)">
									<img src="{$imgRoot}/menu/go.gif"/>
								</a>
							</td>
						</tr>
					</table>
				</xsl:if>
			</td>
		</xsl:if>
		<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multiDownload ='YES')">
			<td width="1">
				<!-- Add button to invoke to multi-download -->
				<table summary="">
					<tr>
						<td>
							<a onclick="javascript:doMDCommit()" href="javascript:void(0)">
								<img src="{$imgRoot}/tools/multidownload.gif"/>
							</a>
						</td>
					</tr>
				</table>
			</td>
		</xsl:if>		
		<td width="1" tabindex ="0" onmouseover="toolsMenuPopup(this)" onmouseout="toolsMenuLeave(event, this)" onkeydown="toolsMenuPopuponkey(this,event)">
			
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'popup-tools'"/>
				</xsl:call-template>
			</xsl:attribute>
			
			<img src="{$imgRoot}/tools/tools_menu.gif" alt="Enquiry Actions" title="Enquiry Actions"/>
			<!--Have to include an iFrame for IE6 coz of problem with elements overlapping comboboxes -->
			<iframe frameborder="0" src="../html/blank_enrichment.html" tabindex="-1"></iframe>
			<div id="EnquiryactionDiv" tabindex="0" onclick="toolsMenuPopdown(this)" onkeydown="FragmentUtil.keydownHandler(event)">
				<xsl:for-each select="control/toolbars">
					<xsl:call-template name="toolbars_n">
						<xsl:with-param name="id" select="'action'"/>
						<xsl:with-param name="type" select="'dropdown'"/>
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</td>
		<td>
				<img src="{$imgRoot}/about.gif" alt="{title} - {enqid} : {$companyName}" title="{title} - {enqid} : {$companyName}" tabindex="0" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);"/>
		</td>
	</xsl:template>

	<xsl:template name="custom_toolbar">
	<xsl:if test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
		<td width="1">
		<!-- Options and go button - if there are some options -->
				<xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d/item != ''">
					<table summary="">
						<tr>
							<td>
								<select id="fastPathType">
								<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
								<option value="{item}">
											<xsl:value-of select="cap"/>
										</option>
									</xsl:for-each>
								</select>
							</td>
						</tr>
					</table>
				</xsl:if>
			</td>
		</xsl:if>
		<td width="1">
			<xsl:for-each select="control/customToolbar">
				<xsl:call-template name="toolbars_n">
					<xsl:with-param name="id"/>
				</xsl:call-template>
			</xsl:for-each>
		</td>
		<xsl:if test="control/auto!='' and control/customToolbar/toolbar/tool[item = 'toggleTimer()'] !=''">
			<!-- Auto refresh Timer -->
			<td>
			<input id="refreshtime{$formFragmentSuffix}" size="1" readonly="readonly" value="{control/auto}"/>
			</td>
		</xsl:if>
		<xsl:if test="control/customToolbar/addactiontools !='' ">
		<!-- Show the message for custom toolbar also -->
		<td width="1">
		
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enqheader-msg'"/>
				</xsl:call-template>
			</xsl:attribute>
			<span><xsl:value-of select="control/msg"/></span>
		</td>
		
		<td width="1" onmouseover="toolsMenuPopup(this)" onmouseout="toolsMenuLeave(event, this)">
			
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'popup-tools'"/>
				</xsl:call-template>
			</xsl:attribute>
			
			<img src="{$imgRoot}/tools/tools_menu.gif" alt="Enquiry Actions" title="Enquiry Actions"/>
			<!--Have to include an iFrame for IE6 coz of problem with elements overlapping comboboxes -->
			<iframe frameborder="0" src="../html/blank_enrichment.html"></iframe>
			<div onclick="toolsMenuPopdown(this)">
				<xsl:for-each select="control/toolbars">
					<xsl:call-template name="toolbars_n">
						<xsl:with-param name="id" select="'action'"/>
						<xsl:with-param name="type" select="'dropdown'"/>
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</td>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="image_call">
		<xsl:param name="imgfullPathtest"/>
		
       <xsl:choose>    
			<xsl:when test="contains($imgfullPathtest,'/')">
			 <xsl:call-template name="image_call">
					<xsl:with-param name="imgfullPathtest" select="substring-after($imgfullPathtest,'/')"/>
				</xsl:call-template>
			</xsl:when>
			 <xsl:otherwise>
			    <xsl:value-of select="substring-before($imgfullPathtest,'.')"/>
			</xsl:otherwise> 
		</xsl:choose>   
    </xsl:template>
    
	<xsl:template name="enqTableHeads">
		<thead>
			<xsl:call-template name="handleMinWidthColumns" />
				<tr>
					<xsl:for-each select="cols">
					<!-- Empty column for view icon -->
					<xsl:choose>
						<xsl:when test="(/responseDetails/window/panes/pane/dataSection/enqResponse/fastPath ='YES')">
							<!--- Add a select all checkbox -->
							<td width="10">
								<img name="img{ref}" id="img{ref}" src="{$imgRoot}/amberlight.gif" />
							</td>
							<td>
								<input type="checkbox" name="toggleAll" id="toggleAll" onclick="ToggleAll(this)" background="{$imgRoot}/tab_back.gif" onkeydown="checkSwitch(event);" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="q">
								<td />
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
					<!-- Multi-Download Enquiry -->
					<xsl:choose>
						<xsl:when test="(/responseDetails/window/panes/pane/dataSection/enqResponse/multiDownload ='YES')">
						<!--- Add a select all checkbox -->
							<td width="10">
								<img name="img{ref}" id="img{ref}" src="{$imgRoot}/download.gif" />
							</td>
							<td>
								<input type="checkbox" name="toggleAll" id="toggleAll" onclick="ToggleAll(this)" background="{$imgRoot}/tab_back.gif" onkeydown="return false;" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="q">
								<td />
									</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
					<!-- Then the column headers -->
					<xsl:for-each select="c">
						<th scope="col" class="columnHeader" id="columnHeader{position()}{$formFragmentSuffix}">
						<!-- Alignment in case of enquiries with hidden columns -->
						<xsl:if test="@colapse='true'">
							<xsl:attribute name="hideable">yes</xsl:attribute>
						</xsl:if>
						<table cellpadding="0" cellspacing="0" summary="" class="columnHeader">
							<tr>
								<th scope="col" class="columnHeader" id="columnHeaderText{position()}{$formFragmentSuffix}">
									<xsl:value-of select="." />
								</th>
								<th scope="col">
								<!-- see if the node has the attribute collapse attached -->
									<xsl:if test="@colapse='true'">
									<!-- add the icon for collapsing the column -->
									<a accesskey="" onclick="javascript:switchColumn({position()})" href="javascript:void(0)" title="{.}" tabindex="0" onfocus="focusonKey('inline',event);" onblur="hideTooltip(event);">
										<img id="image{position()}{$formFragmentSuffix}" columnNo="{position()}" hideable="yes" src="{$imgRoot}/tools/collapse.gif" tabindex="-1" alt="{.}" title="{.}" />
									</a>
									</xsl:if>
								</th>
							</tr>
						</table>
					</th>
				</xsl:for-each>
			</xsl:for-each>
			<xsl:choose>
					<xsl:when
						test="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/style='combo'">
						<th class="drillHeader"> </th>
				    </xsl:when>
					<xsl:otherwise>		
			              <xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/drills/d">
	                          <th class="drillHeader"> </th>
			              </xsl:for-each>
		           	</xsl:otherwise>
			</xsl:choose>
			</tr>
			</thead>		
	</xsl:template>
	
	<xsl:template name="escape-percent">
		<xsl:param name="string"/>
		<xsl:variable name="prcntg" select='"&#37;"'/>
		<xsl:variable name="prcntgtf" select="concat($prcntg,'25')" />
		<xsl:choose>
			<xsl:when test="contains($string, $prcntgtf)">
				<xsl:value-of select="concat($prcntg,substring-after($string, $prcntgtf))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
    
</xsl:stylesheet>
