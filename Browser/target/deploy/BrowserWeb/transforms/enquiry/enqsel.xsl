<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="operandOptions.xsl"/>
	<xsl:import href="sortOptions.xsl"/>
	<xsl:import href="enqFavs.xsl"/>
	<xsl:import href="../tabIndex.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin" select="string(/responseDetails/userDetails/skin)"/>
	<xsl:variable name="imgRoot" select="concat('../plaf/images/', $skin)"/>
	
	<!-- Translatable names for new enqsel form -->
	<xsl:variable name="moreOptsCaption" select="/responseDetails/window/translations/moreOptions"/>
	<xsl:variable name="hideOptsCaption" select="/responseDetails/window/translations/hideOptions"/>
	<xsl:variable name="clearSelCaption" select="/responseDetails/window/translations/clearSelection"/>
	<xsl:variable name="sortAscCaption" select="/responseDetails/window/translations/ascending"/>
	<xsl:variable name="sortDescCaption" select="/responseDetails/window/translations/descending"/>
	<xsl:variable name="sortByCaption" select="/responseDetails/window/translations/sortBy"/>
	<xsl:variable name="thenByCaption" select="/responseDetails/window/translations/thenBy"/>
	<xsl:variable name="refreshCaption" select="/responseDetails/window/translations/refreshEvery"/>
	<xsl:variable name="refreshTimeUnitsCaption" select="/responseDetails/window/translations/seconds"/>
	<xsl:variable name="ddCaption" select="/responseDetails/window/translations/dd"/>
	<xsl:variable name="calCaption" select="/responseDetails/window/translations/calendar"/>
	<xsl:variable name="fquCaption" select="/responseDetails/window/translations/frequency"/>
	<xsl:variable name="recCaption" select="/responseDetails/window/translations/recurrence"/>
	<xsl:variable name="previousEnqs">
		<xsl:choose>
	 		<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/previousEnqs!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/previousEnqs"/>
			</xsl:when>
			<xsl:when test="/responseDetails/window/panes/pane/selSection/selDets/enqsel/previousEnqs!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/previousEnqs"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>			
			</xsl:otherwise>
	   </xsl:choose>
	</xsl:variable>
	<xsl:variable name="previousEnqTitles">
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/previousEnqTitles!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/previousEnqTitles"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/previousEnqTitles"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="autoRefreshName" select="'AUTOREFRESH'"/>
	<xsl:variable name="clrSel"><xsl:value-of select="/responseDetails/webDetails/clearSelection"/></xsl:variable>
	<!-- Hide enquiry name when using external user -->
    <xsl:variable name="hideName" select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/hideName"/>

	<!-- If the transaction statement enquiry is designed for OFX download. -->
	<xsl:variable name="ofxSupport" select="boolean(/responseDetails/window/panes/pane/dataSection/enqResponse/header/r/c[starts-with(cap,'OFX')])"/>
	<xsl:variable name="autoRefVal" select="boolean(/responseDetails/window/panes/pane/selSection/selDets/enqsel/f[n = $autoRefreshName]/d)"/>
	<xsl:template match="selDets" name="selDets_n">
		<form name="enqsel" id="enqsel{$formFragmentSuffix}" method="POST" onsubmit="javascript:doEnquiry();return false;" action="BrowserServlet" AUTOCOMPLETE="{/responseDetails/userDetails/autoComplete}">		
	
			<!-- TODO: THIS IS BAD - it hides the form name on IE, can we get rid of this? -->
			<input type="hidden" name="name" id="name"/>
			
			<input type="hidden" name="download" value="NULL" id="download"/>
			<input type="hidden" name="operation" value="{$_ENQUIRY__SELECT_}" id="operation"/>
			<input type="hidden" name="requestType" value="{$_OFS__ENQUIRY_}" id="requestType"/>
			<input type="hidden" name="command" value="globusCommand" id="command"/>
			<input type="hidden" name="routineName" id="routineName" value="{requestArguments/routineName}"/>
			<input type="hidden" name="routineArgs" id="routineArgs" value="{requestArguments/routineArgs}"/>
			<input type="hidden" name="enqname" id="enqname" value="{enqsel/enqname}"/>
			<input type="hidden" name="enqid" id="enqid" value="{/responseDetails/window/panes/pane/dataSection/enqResponse/enqid}"/>
			<input type="hidden" name="enqaction" id="enqaction" value=""/>
			<input type="hidden" name="dropfield" id="dropfield" value="{enqsel/df}"/>
			<input type="hidden" name="previousEnqs" id="previousEnqs" value="{$previousEnqs}"/>
 			<input type="hidden" name="previousEnqTitles" id="previousEnqTitles" value="{$previousEnqTitles}"/>
			<input type="hidden" name="application" id="application" value="{$_ENQ_}" />
			<input type="hidden" name="version" id="version" value="{enqsel/enqname}" />
			<input type="hidden" name="windowName" id="windowName" value=""/>
			<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value="" />

			<input type="hidden" name="autoRefresh" id="autoRefresh" value="" />

			<!-- Process User Details -->
			<xsl:call-template name="userDetails"/>

			<xsl:if test="$ofxSupport">
				<!-- enquiry for OFX download, cannot be obfuscated as long as is not from T24 -->
				<xsl:variable name="ofxCommand"><xsl:value-of 
					select="substring-after(/responseDetails/window/panes/pane/dataSection/enqResponse/header/r/c[starts-with(cap,'OFX')]/cap,'OFX=')"/></xsl:variable>
				<table id="ofx-csv-buttons" summary="">
				
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'noPrint'"/>
						</xsl:call-template>
					</xsl:attribute>
					
					<tr>
						<td  title="OFX" onclick="downloadStatement('ofx', '{$ofxCommand}')">
						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'textbtn'"/>
								</xsl:call-template>
							</xsl:attribute>
							
							<span>Download as OFX</span>
						</td>
						<td>
						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'textbtn-end'"/>
								</xsl:call-template>
							</xsl:attribute>
						
							<span>&#160;</span>
						</td>
						<td id="ofx-csv-spacer"></td>
						<td title="CSV" onclick="downloadStatement('csv','{enqsel/enqname}')">
						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'textbtn'"/>
								</xsl:call-template>
							</xsl:attribute>
						
							<span>Download as CSV</span>
						</td>
						<td>
							
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'textbtn-end'"/>
								</xsl:call-template>
							</xsl:attribute>
							
							<span>&#160;</span>
						</td>
					</tr>
				</table>
			</xsl:if>

			<!-- IMPORTANT: Switch Point for old / new enquiry selection style -->
			<!-- If new operator list present, then transform to new style enq sel -->
			<xsl:choose>
		  		<xsl:when test="enqsel/operators">
		  			<!-- New Download mechanism is through enqresponse menu - so we need a downloadType field here to replace the old combo in enqsel toolbar -->
					<input type="hidden" name="downloadType" id="downloadType" value="display"/>

					<xsl:call-template name="enqselbody_new"/>

				</xsl:when>
		  		<xsl:otherwise>
					<xsl:call-template name="enqselbody"/>
		  		</xsl:otherwise>
		  	</xsl:choose>

		</form>

	</xsl:template>

	<!-- Original enquiry selection header / fields container -->
	<xsl:template name="enqselbody">
		<!-- Only display header if there are some fields in the response -->
 		<!-- f represents the node 'field' -->
 		<xsl:if test="enqsel/f != ''">
			<div id="denqsel">
				<xsl:choose>
			  		<xsl:when test="$ofxSupport">
			  			<!-- When ofx Support is activated, enqsel section should not be visible. -->
			  			<xsl:attribute name="style">display:none</xsl:attribute>
			  		</xsl:when>
			  		<xsl:otherwise>
			  			
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'display_box'"/>
								</xsl:call-template>
							</xsl:attribute>
							
			  		</xsl:otherwise>
			  	</xsl:choose>
			  	<table  summary="">
 					<tr>
						<td>
 							<table  summary="">
								
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'header-table'"/>
									</xsl:call-template>
								</xsl:attribute>
								
 								<tr>
									
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'caption'"/>
										</xsl:call-template>
									</xsl:attribute>
									
									<td>
										
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'caption'"/>
											</xsl:call-template>
										</xsl:attribute>
										
										<img>
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/enquiry/search.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="enqsel/enqname"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="enqsel/enqname"/></xsl:attribute>
										</img>
									</td>
									<xsl:if test="/responseDetails/userDetails/multiCo='Y'">
										<td nowrap="nowrap">
											
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'caption'"/>
												</xsl:call-template>
											</xsl:attribute>
											
											<b><xsl:value-of select="/responseDetails/userDetails/companyId"/> :  </b>
										</td>
									</xsl:if>
									<td  nowrap="nowrap">
										
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'caption'"/>
												</xsl:call-template>
											</xsl:attribute>
										
										<b><xsl:value-of select="enqsel/title"/></b>
										<img style="height:2px;width:1px;">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
										</img>
									</td>
										<td align="right">
										<div align="right">
											<xsl:for-each select="enqsel/toolbars">
												<xsl:call-template name="toolbars_n"/>
											</xsl:for-each>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
 					<tr>
 						<td>
							<div id="selectiondatascroller{$formFragmentSuffix}">
								
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'enquirydatascroller enquirydataclipped'"/>
										</xsl:call-template>
									</xsl:attribute>
								
								<table id="selectiondisplay{$formFragmentSuffix}" cellpadding="2" cellspacing="1" summary="">
									<!-- Headings -->
									<tr>
										<xsl:for-each select="enqsel/header">
											<td>
											
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'columnHeader'"/>
													</xsl:call-template>
												</xsl:attribute>
											
												<xsl:value-of select="."/>
											</td>
										</xsl:for-each>
									</tr>
									<!-- Fields -->
									<!-- f represents the node 'field' -->
									<xsl:for-each select="enqsel/f">
										<tr>
											<td>
												
												<xsl:attribute name="class">
													<xsl:variable name="classvalue">colour{position() mod 2}</xsl:variable>
													<xsl:call-template name="apply_Style">														
														<xsl:with-param name="actualclass" select="$classvalue"/>
													</xsl:call-template>
												</xsl:attribute>
											    <!--get the caption-->
												<xsl:value-of select="c"/>
												<input type="hidden">
													<!-- n represents the node 'name'-->
													<xsl:attribute name="value"><xsl:value-of select="n"/></xsl:attribute>
													<!-- the node i refers to the field ID -->
													<xsl:attribute name="name">fieldName:<xsl:value-of select="i"/>:1:1</xsl:attribute>
													<xsl:attribute name="id">fieldName:<xsl:value-of select="i"/>:1:1</xsl:attribute>
												</input>
											</td>
											<td  width="1">
												
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'oper'"/>
														</xsl:call-template>
													</xsl:attribute>
													
												<select>
													
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'enqsel'"/>
														</xsl:call-template>
													</xsl:attribute>
													
													
													<!-- the node i refers to the field ID -->
													<xsl:attribute name="name">operand:<xsl:value-of select="i"/>:1:1</xsl:attribute>
													<xsl:call-template name="operand_n"/>
												</select>
											</td>
											<!-- Highlight the mandatory fields -->
											<td>
												<xsl:choose>
													<!-- the node m refers to mandatory -->
													<xsl:when test="m = 'Y'">
														<xsl:attribute name="nowrap"/>
														<img>
															<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/enquiry/required.gif</xsl:attribute>
															<xsl:call-template name="processTabIndex">
																<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
															</xsl:call-template>
														</img>
													</xsl:when>
													<xsl:otherwise>
														<xsl:attribute name="align">right</xsl:attribute>
													</xsl:otherwise>
												</xsl:choose>
												<!-- Now the actual box -->
												<input type="text" size="20">
													<!-- d represents the node 'data'-->
													
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'enqseldata'"/>
														</xsl:call-template>
													</xsl:attribute>
												
													<xsl:attribute name="value"><xsl:value-of select="d"/></xsl:attribute>
													<!-- the node i refers to the field ID -->
													<xsl:attribute name="name">value:<xsl:value-of select="i"/>:1:1</xsl:attribute>
													<xsl:attribute name="id">value:<xsl:value-of select="i"/>:1:1</xsl:attribute>
												</input>
											</td>
											<td width="1">
												<select>
													
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'enqsel'"/>
														</xsl:call-template>
													</xsl:attribute>
													
													<xsl:attribute name="name">sort:<xsl:value-of select="i"/>:1:1</xsl:attribute>
													<xsl:attribute name="id">sort:<xsl:value-of select="i"/>:1:1</xsl:attribute>
													<xsl:call-template name="sort_n"/>
												</select>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</xsl:if>
	</xsl:template>


	<!-- Re-styled enquiry selection header / fields container
	     Sort fields separated from enquiry selection fields
	     and new enquiry attributes controlling layout of fields etc.
	 -->
	<xsl:template name="enqselbody_new">

		<!-- Field name constant for existing refresh field (to filter out) -->
		<!-- The field will still be in the XML field list for backward compatibility, but we do not need it for new enqsel -->
		
		<!-- Selection box only displayed if fields (f element) were selected, and are returned in the response,  -->
 		<xsl:if test="(enqsel/f)">

			<!-- Get space-separated list of sort fields / direction -> Var1 -->
			<xsl:variable name="sortfields">
				<xsl:call-template name="get_valid_sort_fields"/>
			</xsl:variable>

			<xsl:variable name="sortStyles">enqsel-opts table-section<xsl:if test="($sortfields = '') and not (enqsel/attribs/sortOnly) and not($autoRefVal)"> hidden</xsl:if></xsl:variable>			
			
			<table summary="">
				
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'enqsel-main'"/>
						</xsl:call-template>
					</xsl:attribute>
					
		  		<xsl:if test="$ofxSupport">
		  			<!-- When ofx Support is activated, enqsel section should not be visible. -->
		  			<xsl:attribute name="style">display:none</xsl:attribute>
		  		</xsl:if>

				<tr>
					<td id="enqselfavs{$formFragmentSuffix}">
						
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'favs'"/>
							</xsl:call-template>
						</xsl:attribute>
						
				 		<xsl:for-each select="enqsel/enqFav">
							<div>
								
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'display_box'"/>
									</xsl:call-template>
								</xsl:attribute>
							
								<xsl:call-template name="enqFavourites"/>
							</div>
						</xsl:for-each>
					</td>
					<td>
						<table summary="">
						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'display_box enqsel-selections'"/>
								</xsl:call-template>
							</xsl:attribute>
						
					  		<xsl:if test="$ofxSupport">
					  			<!-- When ofx Support is activated, enqsel section should not be visible. -->
					  			<xsl:attribute name="style">display:none</xsl:attribute>
					  		</xsl:if>

							<tr>
								<td>			
									<!-- Create the header with enq name, options and 'go' button -->
									<table summary="">
										
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'header-table'"/>
											</xsl:call-template>
										</xsl:attribute>
										
										<tr>
											
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'caption'"/>
												</xsl:call-template>
											</xsl:attribute>
											
											<td>
												<table summary="">
													<tr>
														<td>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'caption'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<xsl:value-of select="enqsel/title"/>
											</td>
													</tr>
													<tr>
														<td>
															<xsl:attribute name="class">
																<xsl:call-template name="apply_Style">
																	<xsl:with-param name="actualclass" select="'caption'"/>
																</xsl:call-template>
															</xsl:attribute>														
															<xsl:value-of select="enqsel/requiredSelInfo"/>
														</td>
													</tr>
												</table>												
											</td>
									<xsl:if test="$clrSel='no'">
											<td width="1">
												<table summary="">
													<xsl:if test="not (enqsel/attribs/sortOnly)">
														<tr>
															<td>
																<xsl:if test="not (enqsel/attribs/noMoreOptions)">
																	<xsl:choose>
																		<xsl:when test="$sortfields = '' and not($autoRefVal)">
																			<a id="enqselopts_toggle{$formFragmentSuffix}" onclick="javascript:EnqSel.toggleOptions()" href="javascript:void(0)" alt-text="{$hideOptsCaption}" tabindex = "0"><xsl:value-of select="$moreOptsCaption"/></a>
																		</xsl:when>
																		<xsl:otherwise>
																			<a id="enqselopts_toggle{$formFragmentSuffix}" onclick="javascript:EnqSel.toggleOptions()" href="javascript:void(0)" alt-text="{$moreOptsCaption}" tabindex = "0"><xsl:value-of select="$hideOptsCaption"/></a>
																		</xsl:otherwise>
																	</xsl:choose>
																</xsl:if>
															</td>
														</tr>
													</xsl:if>
													<xsl:if test="not (enqsel/attribs/noClearSelection)">  
													<tr>
														<td><a onclick="javascript:resetForm('enqsel')" href="javascript:void(0)"><xsl:value-of select="$clearSelCaption"/></a></td>
													</tr>
													</xsl:if>
												</table>
											</td>
											<td width="1">
												<xsl:for-each select="enqsel/toolbars">
													<xsl:call-template name="toolbars_n">
														<xsl:with-param name="id">main</xsl:with-param>
													</xsl:call-template>
												</xsl:for-each>
											</td>
									</xsl:if>
										</tr>
									</table>
								</td>
							</tr>

							<tr>
								<td>
									<!-- Create the sorting block - toggled by option above - initially hidden if no sort options set -->
									<table id="enqselopts{$formFragmentSuffix}" summary="">
										
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="$sortStyles"/>
											</xsl:call-template>
										</xsl:attribute>
										
		
										<xsl:call-template name="display_sort_field">
					 						<xsl:with-param name="fieldlist" select="$sortfields"/>
										</xsl:call-template>
										
 										<xsl:if test="not (/responseDetails/userDetails/noAutoRefresh)">
											<tr>
												<xsl:for-each select="enqsel/f">
												<xsl:if test="n = $autoRefreshName">
												<td colspan="99">
													
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'enqsel-refresh'"/>
														</xsl:call-template>
													</xsl:attribute>
													
													<span tabindex = "0"><strong><xsl:value-of select="$refreshCaption"/></strong></span>
													<!-- Substitute params for old (ignored) AUTOREFRESH field that still comes up in field list XML -->
													<input type="hidden" name="fieldName:999:1:1" value="{$autoRefreshName}"/>
													<input type="hidden" name="operand:999:1:1" value="EQ"/>
													<input size="3" maxlength="3" type="text" onkeypress="return isNumberKey(event)" name="value:999:1:1" value="{d}" onpaste="return false" ondrop="return false" tabindex = "0"/>
													<span tabindex = "0"><xsl:value-of select="$refreshTimeUnitsCaption"/></span>
												</td>
												</xsl:if>
												</xsl:for-each>
											</tr>
										</xsl:if>
									</table>
								</td>
							</tr>			
			
			
							<tr>
								<td>			
									<!-- Create the main enqsel block with field - operator - value -->
									<!-- KEY TO NODES BELOW:
										'f' is the enquiry field itself,
										'c' is field caption,
										'n' is the name,
										'i' is the numeric ID,
										'd' is existing field data -->
									<xsl:choose>
										<xsl:when test="enqsel/attribs/sortOnly">
											<input type="hidden" name="fieldName:1:1:1" id="fieldName:1:1:1" value=""/>
											<input type="hidden" name="sort:1:1:1" id="sort:1:1:1" value="none"/>
											<input type="hidden" name="fieldName:2:1:1" id="fieldName:2:1:1" value=""/>
											<input type="hidden" name="sort:2:1:1" id="sort:2:1:1" value="none"/>
											<input type="hidden" name="fieldName:3:1:1" id="fieldName:3:1:1" value=""/>
											<input type="hidden" name="sort:3:1:1" id="sort:3:1:1" value="none"/>
										</xsl:when>
										<xsl:otherwise>

											<xsl:choose>
												<xsl:when test="enqsel/attribs/horizontal">

													<!-- HORIZONTAL layout -->
													<div id="selectiondatascroller{$formFragmentSuffix}"  style="overflow:auto">
														
															<xsl:attribute name="class">
																<xsl:call-template name="apply_Style">
																	<xsl:with-param name="actualclass" select="'enquirydatascroller enqsel-clipped-horiz table-section'"/>
																</xsl:call-template>
															</xsl:attribute>
													
														<table id="selectiondisplay{$formFragmentSuffix}"  cellpadding="2" cellspacing="1" summary="Enquiry Selection">
															
															<xsl:attribute name="class">
																<xsl:call-template name="apply_Style">
																	<xsl:with-param name="actualclass" select="'enqsel-horiz'"/>
																</xsl:call-template>
															</xsl:attribute>
															
	
															<xsl:variable name="showOps"><xsl:call-template name="is-multiple-ops"/></xsl:variable>

															<tr>
																<xsl:for-each select="enqsel/f">
																	<xsl:if test="n != $autoRefreshName">
																		<td>
																			<span tabindex = "-1"><label for="value:{i}:1:1"><xsl:value-of select="c"/></label></span>
																			<!-- the node m refers to mandatory -->
																			<xsl:if test="m = 'Y'">
																				<img align="absmiddle" src="{$imgRoot}/enquiry/required.gif">
																				<xsl:call-template name="processTabIndex">
																					<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
																				</xsl:call-template>
																				</img>
																			</xsl:if>
																			<input type="hidden" name="fieldName:{i}:1:1" id="fieldName:{i}:1:1" value="{n}"/>
																			<!-- If we are not having an ops row, tag on hidden ops field -->
																			<xsl:if test="$showOps = ''">
																				<input type="hidden" name="operand:{i}:1:1" value="{validOps}"/>
																			</xsl:if>
																		</td>
																	</xsl:if>
																</xsl:for-each>
															</tr>
			
															<xsl:if test="$showOps != ''">
																<tr>
																	<xsl:for-each select="enqsel/f">
																		<xsl:if test="n != $autoRefreshName">
																			<td>
																				
																				<xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'oper'"/>
																					</xsl:call-template>
																				</xsl:attribute>
																				
																				<xsl:call-template name="operator_list"/>
																			</td>
																		</xsl:if>
																	</xsl:for-each>
																</tr>
															</xsl:if>
			
															<tr>
																<xsl:for-each select="enqsel/f">
																	<xsl:if test="n != $autoRefreshName">
																		<td>
																			<!-- Now the actual box -->
																			<input type="text" name="value:{i}:1:1" id="value:{i}:1:1"  value="{d}">
																				
																				<xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'enqseldata'"/>
																					</xsl:call-template>
																				</xsl:attribute>
																			</input>
																			<input type="hidden" name="sort:{i}:1:1" id="sort:{i}:1:1" value="none"/>
																			<!-- Check to see if we need to add a dropdown etc. for this field -->
																			<xsl:call-template name="dropdowns_and_popups"/>
																		</td>
																	</xsl:if>
																</xsl:for-each>
															</tr>
														</table>
													</div>
															
												</xsl:when>
												<xsl:otherwise>

													<!-- VERTICAL (standard) display, always including operator -->
													<!-- To understand the height of the div, min-height attribute has to be mentioned for firefox to avoid overlapping -->
 													<div onscroll="javascript:testmouse(event);" id="selectiondatascroller{$formFragmentSuffix}"  style="overflow:auto">
														
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'enquirydatascroller enquirydataclipped table-section'"/>
															</xsl:call-template>
														</xsl:attribute>
														
														<table id="selectiondisplay{$formFragmentSuffix}"  cellpadding="2" cellspacing="1" summary="Enquiry Selection">
															
															<xsl:attribute name="class">
																<xsl:call-template name="apply_Style">
																	<xsl:with-param name="actualclass" select="'enqsel-vert'"/>
																</xsl:call-template>
															</xsl:attribute>
															
															<!-- Fields -->
															<xsl:for-each select="enqsel/f">
																<xsl:if test="n != $autoRefreshName">
																	<tr>
																		<td>
																			<span tabindex = "-1"><label for="value:{i}:1:1"><xsl:value-of select="c"/></label></span>
																			<input type="hidden" name="fieldName:{i}:1:1" id="fieldName:{i}:1:1" value="{n}"/>
																		</td>
																		<td>
																			
																			<xsl:attribute name="class">
																				<xsl:call-template name="apply_Style">
																					<xsl:with-param name="actualclass" select="'oper'"/>
																				</xsl:call-template>
																			</xsl:attribute>
																			
																			<xsl:call-template name="operator_list"/>
																		</td>
																		<td>
																			<!-- Highlight the mandatory fields -->
																			<xsl:choose>
																				<!-- the node m refers to mandatory -->
																				<xsl:when test="m = 'Y'">
																					<xsl:attribute name="nowrap"/>
																					<img src="{$imgRoot}/enquiry/required.gif">
																						<xsl:call-template name="processTabIndex">
																							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
																						</xsl:call-template>																					
																					</img>
																				</xsl:when>
																				<xsl:otherwise>
																					<xsl:attribute name="align">right</xsl:attribute>
																				</xsl:otherwise>
																			</xsl:choose>
																			<!-- Now the actual box -->
																			<input type="text" name="value:{i}:1:1" id="value:{i}:1:1" size="20"  value="{d}" frequencydropfieldname="value:{i}:1:1"> <!-- frequencydropfieldname tag required for calendarFrequencyHide() script -->
																			
																			<xsl:attribute name="class">
																				<xsl:call-template name="apply_Style">
																					<xsl:with-param name="actualclass" select="'enqseldata'"/>
																				</xsl:call-template>
																			</xsl:attribute>
																			
																			<xsl:if test="./popup='recurrence'">
																			   <xsl:attribute name="recurrencefieldname"><xsl:value-of select="c"/></xsl:attribute>
																			</xsl:if>
																			
																			</input>
																			<input type="hidden" name="sort:{i}:1:1" id="sort:{i}:1:1" value="none"/>
																		</td>
																		<!-- Check to see if we need to add a dropdown etc. for this field -->
																		<td>
																			<xsl:call-template name="dropdowns_and_popups"/>
																		</td>
																	</tr>
																</xsl:if>
															</xsl:for-each>
														</table>
													</div>
											
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</tr>
							<tr>
								<xsl:if test="not ($hideName)">
									<td>
										<div>
											
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'table-section enqsel-ftr'"/>
												</xsl:call-template>
											</xsl:attribute>
											
											<xsl:if test="/responseDetails/userDetails/multiCo='Y'">
												<xsl:value-of select="/responseDetails/userDetails/companyId"/><xsl:text> : </xsl:text>
											</xsl:if><xsl:value-of select="enqsel/enqname"/>
										</div>
									</td>
								</xsl:if>
							</tr>
							<!-- adding the find button below the selection criteria -->
								<xsl:if test="$clrSel='yes'">
									<tr>	
										<td width="1">
											<xsl:for-each select="enqsel/toolbars">
												<xsl:call-template name="toolbars_n">
													<xsl:with-param name="id">main</xsl:with-param>
												</xsl:call-template>
											</xsl:for-each>
										</td>
									</tr>
							  </xsl:if>			
						</table>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>


	<!-- Used to determine whether to show operators in horizontal layout - if only one per-field, then hide -->
	<xsl:template name="is-multiple-ops"> 
	
		<xsl:for-each select="enqsel/f">
			<xsl:if test="not (validOps) or (string-length(normalize-space(validOps)) &gt; 2)">YES</xsl:if>
		</xsl:for-each>
		
	</xsl:template>



	<!-- Show single operators as labels, or
	     a combo of operators from 'op' nodes in each field in the response, or
	     a default combo of all the operators listed at the top of the response
	  -->
	<xsl:template name="operator_list">
   		<xsl:choose>
     		<!-- test for restricted operators in force for this field -->
			<xsl:when test="validOps">
	   			<xsl:variable name="validOpsList" select="normalize-space(validOps)"/>
		   		<xsl:choose>
					<xsl:when test="string-length($validOpsList) = 2">
						<!-- single operator -> display as a label -->
						<input type="hidden" name="operand:{i}:1:1" value="{$validOpsList}"/>
						<!-- Beware - funky XPath below to index into the default ops node list using the selected abbreviation (in validOpsList) -->
						<!-- Basically, this matches the current op to the abbreviation in the main list and 
						     extracts the full name from that list's <n> sub-element value (phew) -->
						<span><xsl:value-of select="../operators/op[abbr = $validOpsList]/n"/></span>
		     		</xsl:when>
		      		<xsl:otherwise><!-- multiple allowed operators, but not the full set -->
						<select  name="operand:{i}:1:1"><!-- child node i is the field ID -->
							
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'enqsel'"/>
								</xsl:call-template>
							 </xsl:attribute>
							
							<xsl:call-template name="valid_ops">
								<xsl:with-param name="validOpString" select="$validOpsList"/>
							</xsl:call-template>
						</select>
		      		</xsl:otherwise>
		     	</xsl:choose>
     		</xsl:when>
      		<xsl:otherwise><!-- display combo of default operator list -->
      			<!--  get selected op value, if present -->
      			<xsl:variable name="opVal" select="op"/>
				<select name="operand:{i}:1:1"><!-- child node i is the field ID -->
				
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'enqsel'"/>
							</xsl:call-template>
						</xsl:attribute>
					
					<xsl:for-each select="../operators/op">
	    				<option value="{abbr}">
							<xsl:if test="$opVal = abbr">
								<xsl:attribute name="selected">selected</xsl:attribute>
							</xsl:if>
	    					<xsl:value-of select="n"/>
	    				</option>
					</xsl:for-each>
				</select>
      		</xsl:otherwise>
     	</xsl:choose>
	</xsl:template>

	<!-- Recursively-called slave transform for operator_list above
	   - creates a bunch of option elements from a space-separated list of valid operators for the current selection field -->
	<xsl:template name="valid_ops">
		<xsl:param name="validOpString"/>

		<xsl:variable name="opAbbr">
			<xsl:choose>
				<xsl:when test="contains($validOpString, ' ')"><xsl:value-of select="substring-before($validOpString, ' ')"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$validOpString"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="../operators/op[abbr = $opAbbr]">
			<option value="{$opAbbr}">
				<xsl:if test="op and (op = $opAbbr)">
					<xsl:attribute name="selected">selected</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="../operators/op[abbr = $opAbbr]/n"/>
			</option>
		</xsl:if>

		<xsl:if test="contains($validOpString, ' ')">
			<xsl:call-template name="valid_ops">
				<xsl:with-param name="validOpString" select="substring-after($validOpString, ' ')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<!-- Harvest selected sort fields from response, as 'A' or 'D', and return space-separated list -->
	<xsl:template name="get_valid_sort_fields">

		<xsl:for-each select="enqsel/f">
			<xsl:if test="s != ''">
				<xsl:value-of select="n"/><xsl:text> </xsl:text><xsl:value-of select="s"/><xsl:text> </xsl:text>
			</xsl:if>
		</xsl:for-each>
		
	</xsl:template>


	<!-- Recursively-called slave transform to list enq fields as a combo in the sorting block -->
	<!-- This is basically a for i = 1 to 3 loop!! -->
	<xsl:template name="display_sort_field">
		<xsl:param name="sortnum" select="1"/>
		<xsl:param name="fieldlist"/>

		<xsl:if test="$sortnum &lt; 4">

			<xsl:variable name="sortfield" select="substring-before($fieldlist, ' ')"/>
			<xsl:variable name="nextfields" select="substring-after($fieldlist, ' ')"/>
			<xsl:variable name="sortdir" select="substring-before($nextfields, ' ')"/>
			<xsl:variable name="remainder" select="substring-after($nextfields, ' ')"/>
			
			<tr>
				<xsl:choose>
					<xsl:when test="$sortnum = 1">
						<td><xsl:value-of select="$sortByCaption"/>:</td>
					</xsl:when>
					<xsl:otherwise>
						<td><xsl:value-of select="$thenByCaption"/>:</td>
					</xsl:otherwise>
				</xsl:choose>

				<td>
					<!-- actual (database) field name is 'n', user-facing name is 'c' -->
					<select name="tempSortField:{$sortnum}">
						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'enqsel'"/>
								</xsl:call-template>
							 </xsl:attribute>
							
						<option></option>
						<xsl:for-each select="enqsel/f">
							<option value="{n}">
								<xsl:if test="n = $sortfield">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>
								<xsl:value-of select="c"/>
							</option>
						</xsl:for-each>
					</select>
					
					<input type="radio" name="tempSortDir:{$sortnum}" value="A">
						<xsl:if test="$sortdir != 'D'">
							 <xsl:attribute name="checked">checked</xsl:attribute>
			     		</xsl:if>
			     		<xsl:value-of select="$sortAscCaption"/>
			   		</input>
					<input type="radio" name="tempSortDir:{$sortnum}" value="D">
						<xsl:if test="$sortdir = 'D'">
							 <xsl:attribute name="checked">checked</xsl:attribute>
			     		</xsl:if>
			     		<xsl:value-of select="$sortDescCaption"/>
					</input>
				</td>
			</tr>
	
			<xsl:call-template name="display_sort_field">
				<xsl:with-param name="sortnum" select="$sortnum + 1"/>
				<xsl:with-param name="fieldlist" select="$remainder"/>
			</xsl:call-template>

		</xsl:if>
		
	</xsl:template>

	<!-- Render dropdown / calendar icons in enquiry selection -->
	<xsl:template name="dropdowns_and_popups">

		<!-- dd represents the node dropdown, df represents the node dropfield -->
		<xsl:if test="./dd!='' and not (/responseDetails/userDetails/allowdropdowns)">
			<a onclick="javascript:enqselDropDown('value:{i}:1:1', '{dd}', '{pdf}')" href="javascript:void(0)" title="{$ddCaption}" popupDropField="{pdf}">
			<xsl:call-template name="processTabIndex">
				<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
			</xsl:call-template>	
			<xsl:choose>
          		<xsl:when test="pdf!=''">
          			<img src="{$imgRoot}/deal/popupDropDown.gif">
					
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'dropdown_button'"/>
							</xsl:call-template>
						</xsl:attribute>
					
					</img>
      			</xsl:when>
	      	 	<xsl:otherwise>
	       			<img src="{$imgRoot}/deal/dropdown.gif">
					
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'dropdown_button'"/>
							</xsl:call-template>
						</xsl:attribute>
					
					</img>
	       		</xsl:otherwise>
            </xsl:choose>
			</a>
			<xsl:if test="/responseDetails/userDetails/enquirySelectionBox">
			<a onclick="javascript:enqselDropDown('value:{i}:1:1', '{dd}', '{pdf}','Yes')" href="javascript:void(0)">
				<xsl:call-template name="processTabIndex">
					<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
				</xsl:call-template>
			  <img src="{$imgRoot}/tools/select.gif">
			     
				<xsl:attribute name="title">Selection Criteria</xsl:attribute>
			     <xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'selection_criteria'"/>
							</xsl:call-template>
				 </xsl:attribute>
			  </img>
			</a>
			</xsl:if>
		</xsl:if>

		<xsl:if test="./popup='date' and not (/responseDetails/userDetails/allowcalendar)">
			<a onclick="javascript:enqselDropDownCalendar('value:{i}:1:1')" href="javascript:void(0)" title="{$calCaption}">
				<xsl:call-template name="processTabIndex">
					<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
				</xsl:call-template>
				<img  src="{$imgRoot}/deal/calendar.gif" calendardropfieldname="{df}">
				
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'dropdown_button'"/>
							</xsl:call-template>
						</xsl:attribute>
					
				</img>
			</a>
		</xsl:if>
		
		<xsl:if test="./popup='frequency'">
			<a onclick="javascript:enqselDropFrequency('value:{i}:1:1')" href="javascript:void(0)" title="{$fquCaption}">
				<xsl:call-template name="processTabIndex">
					<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
				</xsl:call-template>
				<img  src="{$imgRoot}/deal/fqu.gif">
			
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'dropdown_button'"/>
							</xsl:call-template>
						</xsl:attribute>
					
				</img>
			</a>
		</xsl:if>

		<xsl:if test="./popup='recurrence'">
			<a onclick="javascript:enqselDropRecurrence('value:{i}:1:1')" href="javascript:void(0)" title="{$recCaption}">
				<xsl:call-template name="processTabIndex">
					<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
				</xsl:call-template>
				<img  src="{$imgRoot}/deal/recurr.gif">
				
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'dropdown_button'"/>
							</xsl:call-template>
						</xsl:attribute>
					
				</img>
			</a>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>
