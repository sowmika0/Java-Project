<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="fieldEvents.xsl"/>	<!-- Containings event information for inputtable fields -->
	<xsl:import href="../escaper.xsl"/>		<!-- To escape apostrophe's used in javascript parameters -->
	<xsl:import href="../tabIndex.xsl"/>
		
	<!--Tabs transformer -->
	<xsl:template match="tab" name="tab_n">
		<xsl:variable name="addlHeader">
				<xsl:value-of select="addlHeader"/>
		</xsl:variable>
		<xsl:variable name="addlFooter">
				<xsl:value-of select="addlFooter"/>
		</xsl:variable>
		<xsl:variable name="tabHFName">
			<xsl:if test="./tabid='mainTab'">
				<xsl:value-of select="'tab'"/>				
			</xsl:if>
			<xsl:if test="./tabid!='mainTab'">
				<xsl:value-of select="'visibletab'"/>				
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$addlHeader!=''">
			<table>
				<xsl:attribute name="name"><xsl:value-of select="$tabHFName"/></xsl:attribute>				
				<xsl:attribute name="isTab">true</xsl:attribute>
				<xsl:attribute name="style">width:0px;</xsl:attribute>
				<xsl:attribute name="class"><xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'HeaderTabAlign'"/>
				</xsl:call-template></xsl:attribute>				
				<xsl:if test="./tabid!=''">
					<xsl:attribute name="id"><xsl:value-of select="tabid"/></xsl:attribute>
				</xsl:if>
				<tr><td><pre>															
					<xsl:attribute name="class"><xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'addlHeader'"/>
					</xsl:call-template></xsl:attribute>
					<xsl:value-of select="$addlHeader"/>										
				</pre></td></tr>
			</table>
		</xsl:if>
		
		<!--  First we do the Blank table (used to hide the inactive tables) -->
		<!-- Second we do the actual tables tabs-->
		<!--<xsl:for-each select="tab">-->
		<table cellspacing="2" style="white-space: nowrap;" summary="">
			<!-- This flag indicates that this table holds a tab -->
			<xsl:attribute name="isTab">true</xsl:attribute>
			<xsl:attribute name="onmousedown">javascript:setCurrentTabName('<xsl:value-of select="tabid"/>')</xsl:attribute>				
			<xsl:attribute name="onkeydown">javascript:setCurrentTabName('<xsl:value-of select="tabid"/>')</xsl:attribute>
			<xsl:if test="(./tabid = 'tab1') and (../../../groupTab)">
				<xsl:attribute name="style">display:block</xsl:attribute>
			</xsl:if> 
            <xsl:if test="(/responseDetails/userDetails/transSign)">
                 <xsl:attribute name="style">display:block</xsl:attribute>
            </xsl:if>
            
            <xsl:variable name="CustomVersionAlign">
                   <xsl:value-of select="versionalign"/>
            </xsl:variable>
            
            <xsl:variable name="TabVersion">
                   <xsl:value-of select="tabversion"/>
            </xsl:variable>
            <xsl:variable name="TabfullViewMode">
                 <xsl:choose>
                  <!-- Tab Screen is FullView -->
                   <xsl:when test="$TabVersion='' or $TabVersion=','">true</xsl:when>
                   <xsl:otherwise>false</xsl:otherwise>
                 </xsl:choose>
             </xsl:variable>
            
			<xsl:variable name="tabId">
				<xsl:value-of select="tabid"/>
			</xsl:variable>			
		
			<xsl:if test="./tabhref!=''">
				<xsl:attribute name="name">visibletab</xsl:attribute>
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'tab'"/>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="./tabid='mainTab'">
				<xsl:attribute name="name">tab</xsl:attribute>
			</xsl:if>
			<xsl:if test="./tabid!=''">
				<xsl:attribute name="id"><xsl:value-of select="tabid"/></xsl:attribute>
			</xsl:if>
			
			
			<!-- could be a tab that contains changes since last authorisation  if user click action -->
			<xsl:for-each select="dataSection/enqResponse">
				<xsl:call-template name="enqyiryMainTable"/>
			</xsl:for-each>
			
			<xsl:variable name="tempver">
                   <xsl:value-of select="../../version"/>
            </xsl:variable> 
            <xsl:variable name="fullViewMode">
				<xsl:choose>
					<!-- Screen is FullView -->
					<xsl:when test="$tempver='' or $tempver=','">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>	
			
			<!-- for each row -->
			<xsl:for-each select="r">
				<tr tabId="{$tabId}" >
				
					<!-- Store the MV and SV details as attributes in the row. -->
					<xsl:if test="mvList">
						<xsl:attribute name="mvList"><xsl:value-of select="mvList"/></xsl:attribute>
					</xsl:if>
					<!-- Store the MV and SV details as attributes in the row. -->
					<xsl:if test="svList">
						<xsl:attribute name="svList"><xsl:value-of select="svList"/></xsl:attribute>
					</xsl:if>
					<!-- for each cell -->
					<!-- l represents the node maxlen; dd represents the node dropdown; in represents the node 'inpname' -->
					<xsl:for-each select="ce">
						<xsl:choose>
							<!-- comments and comment lines. seperated as they colspan -->
								<!-- comment fields-->
								<xsl:when test="./comment!=''">
									<td>
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'field'"/>
											</xsl:call-template>
										</xsl:attribute>
									    <xsl:if test="mvDetails">
									    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
									    </xsl:if>
   									    <xsl:if test="svDetails">
   									    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
   									    </xsl:if>
   									    									    
										<xsl:attribute name="colspan"><xsl:value-of select="comment/sp"/></xsl:attribute><!-- get the span -->
										<span>
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'comment'"/>
												</xsl:call-template>
											</xsl:attribute>
											<!-- select the comment value -->
											<xsl:choose>
												<xsl:when test="comment/cv!=''">
													<xsl:value-of select="comment/cv"/>
												</xsl:when>
												<xsl:otherwise>
													<!-- no comment so leave a blank line -->
													<br/>
												</xsl:otherwise>
											</xsl:choose>
										</span>
									</td>
								</xsl:when>
								<!-- line -->
								<!-- allows for lines to span across multiple columns -->
								<xsl:when test="./commentline!=''">
									<td>
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'field'"/>
												</xsl:call-template>
											</xsl:attribute>
									    <xsl:if test="mvDetails">
									    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
									    </xsl:if>
   									    <xsl:if test="svDetails">
   									    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
   									    </xsl:if>
   									    
										<xsl:attribute name="colspan"><xsl:value-of select=" sp"/></xsl:attribute>
										<span>
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'comment'"/>
												</xsl:call-template>
											</xsl:attribute>
											<xsl:value-of select="commentline"/>
										</span>
									</td>
							</xsl:when>
							<xsl:when test="./empty!=''">
								<!-- get the span -->
								<td class = "empty">
								    <xsl:if test="mvDetails">
									   	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
									</xsl:if>
   									<xsl:if test="svDetails">
   									   	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
   									</xsl:if>
									<xsl:attribute name="colspan"><xsl:value-of select="sp"/></xsl:attribute>
								</td>
							</xsl:when>
							<!-- caption - i.e. the label for a field-->
							<xsl:when test="./c!=''">
								<td>
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'field'"/>
										</xsl:call-template>
									</xsl:attribute>
								    <xsl:if test="mvDetails">
										<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
									</xsl:if>
   									<xsl:if test="svDetails">
   										<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
   									</xsl:if>
   									<xsl:variable name="fieldCaption">fieldCaption:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:variable>
   									<xsl:variable name="fieldName">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:variable>
									<!-- get the span -->
									<xsl:attribute name="colspan"><xsl:value-of select="sp"/></xsl:attribute>
									<xsl:choose>
										<xsl:when test="./fieldError='Y'">
										   <label> 
											<xsl:attribute name="for"><xsl:value-of select='$fieldName'/></xsl:attribute>
											<a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'error'"/>
													</xsl:call-template>
												</xsl:attribute>
												<xsl:attribute name="name"><xsl:value-of select="$fieldCaption"/></xsl:attribute>
												<xsl:attribute name="id"><xsl:value-of select="$fieldCaption"/></xsl:attribute>
												<xsl:attribute name="tabindex">-1</xsl:attribute>
												<!-- fn represents the node 'fieldname'-->
													<xsl:choose>
														<xsl:when test="./cLink!=''">
												<xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="cLink"/>')</xsl:attribute>
												<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														</xsl:when>
														<xsl:otherwise>
														  <xsl:if test="$stripFrameToolbars = 'false'">
														     <xsl:attribute name="href">javascript:help('<xsl:value-of select="fn"/>')</xsl:attribute>
														  </xsl:if>
														</xsl:otherwise>
													</xsl:choose>
												<xsl:attribute name="title"><xsl:value-of select="tip"/></xsl:attribute>
												<xsl:if test="./ll!=''">
													<xsl:attribute name="lng"><xsl:value-of select="ll"/></xsl:attribute>
												</xsl:if>
												<!--get the caption-->						
												<xsl:value-of select="c"/>
											</a>
										</label>
										</xsl:when>
										<xsl:otherwise>
											<label> 
													<xsl:attribute name="for"><xsl:value-of select='$fieldName'/></xsl:attribute>
											<a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'field'"/>
												</xsl:call-template>
											</xsl:attribute>
												<xsl:attribute name="name"><xsl:value-of select="$fieldCaption"/></xsl:attribute>
												<xsl:attribute name="id"><xsl:value-of select="$fieldCaption"/></xsl:attribute>
												<xsl:attribute name="tabindex">-1</xsl:attribute>
												<!-- fn represents the node 'fieldname'-->
													<xsl:choose>
														<xsl:when test="./cLink!=''">
												<xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="cLink"/>')</xsl:attribute>
												<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														</xsl:when>
														<xsl:otherwise>
														  <xsl:if test="$stripFrameToolbars = 'false'">
														     <xsl:attribute name="href">javascript:help('<xsl:value-of select="fn"/>')</xsl:attribute>
														  </xsl:if> 
														</xsl:otherwise>
													</xsl:choose>
												<xsl:attribute name="title"><xsl:value-of select="tip"/></xsl:attribute>
												<!--get the caption-->
												<xsl:if test="./ll!=''">
													<xsl:attribute name="lng"><xsl:value-of select="ll"/></xsl:attribute>
												</xsl:if>				
												<xsl:value-of select="c"/>	
											</a>
											</label> 
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</xsl:when>
						
							<!-- Enrichment -->
							<!-- check to see if type is equal to enrichment-->
							<xsl:when test="./ty='enrichment'">
								<td nowrap="true">
	   									    <xsl:if test="mvDetails">
										    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
										    </xsl:if>
	   									    <xsl:if test="svDetails">
	   									    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
	   									    </xsl:if>
	   									    
										<!-- get the span -->
										<xsl:attribute name="colspan"><xsl:value-of select="sp" /></xsl:attribute>
										
										<span>
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'enrichment'"/>
												</xsl:call-template>
											</xsl:attribute>
											<xsl:if test="se!=''">
												<xsl:attribute name="style">display:none</xsl:attribute>
											</xsl:if>
											<xsl:attribute name="id">enri_<xsl:value-of select="enriId" /></xsl:attribute>
											<xsl:attribute name="name">enri_<xsl:value-of select="enriId" /></xsl:attribute>
											<!-- v corresponds to the word value -->
											<xsl:value-of select="v" />
										</span>
								</td>
							</xsl:when>
							
							<xsl:otherwise>
							
							 	<!-- In See mode, place the context flow icons on the page -->
							 	<xsl:if test="./ty='viewed'">
								 	<xsl:choose>
								 	<!-- In full screen mode, place the context flow icons on the page -->
										<xsl:when test="$fullViewMode='true' and $TabfullViewMode='true'">
											<td>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'action_container'"/>
													</xsl:call-template>
												</xsl:attribute>
												<xsl:call-template name="field_actions"/>
											</td>
										</xsl:when>	
								 	<!-- In a screen with associated versions, place the context flow icons on the main page -->
										<xsl:otherwise>
										    <xsl:if test="../parent::mainTab and $CustomVersionAlign=''">
												<td>
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'action_container'"/>
														</xsl:call-template>
													</xsl:attribute>
													<xsl:call-template name="field_actions"/>	
												</td>			
											</xsl:if>
											<xsl:if test="../parent::tab and $CustomVersionAlign=''">
												<td>
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'action_container'"/>
														</xsl:call-template>
													</xsl:attribute>
													<xsl:call-template name="field_actions"/>	
												</td>
											</xsl:if>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>

								<!-- Set a variable for overridable dealbox input class -->
								<xsl:variable name="dealboxClass">
									<xsl:choose>
										<xsl:when test="class"><xsl:value-of select="class"/></xsl:when>
										<xsl:otherwise>dealbox</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>		
								
								<xsl:if test="./ty!='viewed'"> 
									<xsl:if test="$fullViewMode='true' and $TabfullViewMode='true'">
										<td>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'action_container'"/>
													</xsl:call-template>
												</xsl:attribute>
										    <xsl:if test="mvDetails">
										    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
										    </xsl:if>
	   									    <xsl:if test="svDetails">
	   									    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
	   									    </xsl:if>										
											<!-- FULL VIEW MODE so add extra cell for the icons -->
											<xsl:call-template name="field_actions"></xsl:call-template>
										</td>										
									</xsl:if>
									
									<xsl:if test= "$CustomVersionAlign=''">
									   <xsl:if test="$fullViewMode='false' and $TabfullViewMode='false'">
										  <td>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'action_container'"/>
													</xsl:call-template>
												</xsl:attribute>
											    <xsl:if test="mvDetails">
											    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
											    </xsl:if>
		   									    <xsl:if test="svDetails">
		   									    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
		   									    </xsl:if>											
												<!-- NOT FULL VIEW MODE & Grid Mode is NOT set so add extra cell for the icons -->
												<xsl:call-template name="field_actions"></xsl:call-template>
										  </td>										
									   </xsl:if>
									</xsl:if>
								</xsl:if>
								
											  
									
									<!-- get the span -->
									<td style="white-space: nowrap;">
									<xsl:choose>
									     <xsl:when test="esp!=''">
									          <xsl:attribute name="colspan"><xsl:value-of select="esp"/></xsl:attribute>
									     </xsl:when>
									     <xsl:otherwise>
									          <xsl:attribute name="colspan"><xsl:value-of select="sp"/></xsl:attribute>
									     </xsl:otherwise>
									</xsl:choose>
									
										    <xsl:if test="mvDetails">
										    	<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
										    </xsl:if>
		   								    <xsl:if test="svDetails">
		   								    	<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
		   								    </xsl:if>
		   							<xsl:if test= "$CustomVersionAlign='true'">
		   								    <xsl:if test="./ty!='viewed' and ($fullViewMode='false') or ($TabfullViewMode='false')">
                                                   <xsl:if test="not(radios)">
                                                	   	
								 					<xsl:if test="not (../grdMode)">
                                                    	<!-- Grid Mode IS set so insert icons into the SAME cell -->
                                                        	  <xsl:call-template name="field_actions"/><xsl:attribute name="nowrap">true</xsl:attribute>
                                            	        </xsl:if>
                      						       </xsl:if>
                                            </xsl:if>
					    				    <xsl:if test="./ty!='viewed' and ($fullViewMode='false') or ($TabfullViewMode='false')">
                                                   <xsl:if test="not(radios)">
                                                	   	
								 					<xsl:if test="../grdMode='Y'">
                                                    	<!-- Grid Mode IS set so insert icons into the SAME cell -->
                                                        	  <xsl:call-template name="field_actions"/><xsl:attribute name="nowrap">true</xsl:attribute>
                                            	        </xsl:if>
                      						       </xsl:if>
                                            </xsl:if>
					   				 </xsl:if>
		   								    													
									<xsl:choose>
										<!-- A list of options as a combo box -->
										<!-- Need to set the node correctly to pick up options here-->
										<xsl:when test="./options!=''">
											<select>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'dealbox'"/>
													</xsl:call-template>
												</xsl:attribute>
												<!-- fn represents the node 'fieldname'-->
												<!-- in represents the node 'inpname'-->
												<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="id">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<!--Take the thisoption to the old value of combo box --> 
												<xsl:choose>
													<xsl:when test="v!=''">
												<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
													</xsl:when>
													<xsl:otherwise>
														<xsl:attribute name="oldValue"><xsl:value-of select="options/option/thisoption[1]"/></xsl:attribute>
													</xsl:otherwise>
												</xsl:choose>
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
												<!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
												<xsl:call-template name="fieldEvents"/>
												<!-- Build the html for the select box options. This is done in one of 2 ways: -->
												<!-- Older T24 systems produce thisoption directly under options, and display the return value -->
												<xsl:for-each select="options/thisoption">
													<option>
														<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
														<!-- v corresponds to the word value -->
														<xsl:if test=".=../../v">
															<xsl:attribute name="selected">true</xsl:attribute>
														</xsl:if>
														<xsl:value-of select="."/>
													</option>
												</xsl:for-each>
												<!-- Newer T24 systems produce thisoption under options/option, and display the 'disp' value -->
												<xsl:if test="($Nonedisplay != 'YES') and (./m = 'yes')">
													<xsl:for-each select="options/option">
														<xsl:if test="position()!=1">
															<option>
																<xsl:attribute name="value"><xsl:value-of select="thisoption"/></xsl:attribute>
																<!-- v corresponds to the word value -->
																<xsl:if test="thisoption=../../v">
																	<xsl:attribute name="selected">true</xsl:attribute>
																</xsl:if>
																<xsl:choose>
																	<xsl:when test="disp!=''"><xsl:value-of select="disp"/></xsl:when>
																	<xsl:otherwise>	<xsl:value-of select="."/></xsl:otherwise>
																</xsl:choose>
															</option>
														</xsl:if>
													</xsl:for-each>
												</xsl:if>
												
												<xsl:if test="not (./m) or (($Nonedisplay = 'YES') and (./m = 'yes'))">
												<xsl:for-each select="options/option">
													<option>
														<xsl:attribute name="value"><xsl:value-of select="thisoption"/></xsl:attribute>
														<!-- v corresponds to the word value -->
														<xsl:if test="thisoption=../../v">
															<xsl:attribute name="selected">true</xsl:attribute>
														</xsl:if>
														<xsl:choose>
															<xsl:when test="disp!=''"><xsl:value-of select="disp"/></xsl:when>
															<xsl:otherwise>	<xsl:value-of select="."/></xsl:otherwise>
														</xsl:choose>
													</option>
												</xsl:for-each>
												</xsl:if>
											</select>
										</xsl:when>

					                    <xsl:when test="./radios!=''">
							                   <!--  Use combo box for multivalue expansion on radio buttons as there is a problem with cloning in IE -->
		  					                    <xsl:choose>
			  							  			<!-- check to see if the new client side multi expansion is present -->
			  							  			<xsl:when test="../mvList!='' or mvExpansion = 'disable' or svExpansion = 'disable'">
			  							  			
			  							  			    <!-- Sub value icon display handled in an associated set -->
			  							  				<!-- <xsl:if test="$fullViewMode='false'">
			  							  					<xsl:if test="../grdMode='Y'">
			  										  			<xsl:call-template name="field_actions"/>
			  										  		</xsl:if>
			  									  		</xsl:if> -->
			  							  			<!-- Do combo box for multivalue expansion only -->
			  									  			<select>
																<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																		<xsl:with-param name="actualclass" select="'dealbox'"/>
																	</xsl:call-template>
																</xsl:attribute>
			  													<!-- fn represents the node 'fieldname'-->
			  													<!-- in represents the node 'inpname'-->
			  													<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
			  													<xsl:attribute name="tabindex">0</xsl:attribute>
			  													<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
			  														<!-- v corresponds to the word value -->
			  													<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
			  														<xsl:if test="./fieldError='Y'">
			  															<xsl:attribute name="fieldError">Y</xsl:attribute>
			  														</xsl:if>
			  													<!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
			  													<xsl:call-template name="fieldEvents"/>
			  													
			  													<!-- Build the html for the select box options. -->
			  													<!-- Blank option for mandatory combo box made customizable -->
			  												
			  													<xsl:if test="(($Nonedisplay != 'YES') or ($Assocdisplay = 'YES')) and (./m = 'yes')">
			  													<xsl:for-each select="radios/rad">
			  															<xsl:if test="val!=''">
			  																<option>
			  																	<xsl:attribute name="value"><xsl:value-of select="val"/></xsl:attribute>
			  																	<!-- v corresponds to the word value -->
			  																	<xsl:if test="val=../../v">
			  																		<xsl:attribute name="selected">true</xsl:attribute>
			  																	</xsl:if>
			  																	<xsl:choose>
			  																	<!--  display the value for selection, normally DISP but special case for radio buttons -->
			  																		<xsl:when test="val!=''"><xsl:value-of select="cap"/></xsl:when>
			  																		<xsl:otherwise>	<xsl:value-of select="."/></xsl:otherwise>
			  																	</xsl:choose>
			  																</option>
			  															</xsl:if> 
			  														</xsl:for-each>
			  													</xsl:if>	
			  													
			    												<xsl:if test="not (./m) or (($Nonedisplay = 'YES') and (./m = 'yes') and ($Assocdisplay != 'YES'))">
			  														<xsl:for-each select="radios/rad">
			  														<xsl:if test="position()=1">
			  															<option>
			  																<!-- Add a 'blank' option -->
			  																<xsl:attribute name="value"></xsl:attribute>
			  															</option>														
			  														</xsl:if>
			  															<xsl:if test="val!=''"> 
			  															<option>
			  																<xsl:attribute name="value"><xsl:value-of select="val"/></xsl:attribute>
			  																	<!-- v corresponds to the word value -->
			  																	<xsl:if test="val=../../v">
			  																		<xsl:attribute name="selected">true</xsl:attribute>
			  																	</xsl:if>
			  																	<xsl:choose>
			  																		<!--  display the value for selection, normally DISP but special case for radio buttons -->
			  																		<xsl:when test="val!=''"><xsl:value-of select="cap"/></xsl:when>
			  																		<xsl:otherwise>	<xsl:value-of select="."/></xsl:otherwise>
			  																	</xsl:choose>
			  															</option>
			  														</xsl:if>
			  													</xsl:for-each>
			  													</xsl:if>
			  												
			  												</select>
			  												<!--  End combo box -->
			  							    			</xsl:when>
			  							    			<xsl:otherwise>
			  					                      		<!--  Create a hidden field to so that the corresponding radio buttons will update it as they are clicked -->
			  					                      		<!--  This field should look like a proper Contract field -->
															<xsl:choose>	
																<xsl:when test="radios/autoField!=''"> <!--Test if it is autoPopulated field -->
																	<input type="hidden" name="fieldName:{fn}{in}" id="fieldName:{fn}{in}" autoPopulated="Y" >
																	<xsl:attribute name="value"><xsl:value-of select="radios/autoField"/></xsl:attribute>
																	<!-- Oldvalue to be updated with auto defaulted value -->
																	<xsl:attribute name="oldValue"><xsl:value-of select="radios/autoField"/></xsl:attribute>
																	</input>
																</xsl:when>
																<!-- TO bring the focus to radios after hot filed validation -->
																<xsl:otherwise> 
																	<input type="hidden" name="fieldName:{fn}{in}" id="fieldName:{fn}{in}" value="" oldValue="" tabname="{$tabId}" />
																</xsl:otherwise>
															</xsl:choose>
 
			  					                      		  
										                      <table summary="">
										                        <tbody>
										                          <xsl:choose>
										                            <xsl:when test="radios/radOrientation='horizontalRadio'">
										                              <tr>
										                              
										                              
										                              	<xsl:if test="../grdMode='Y'">
						 													<td>
						 														<xsl:if test="mvDetails">
															    					<xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
															    				</xsl:if>
							   								    				<xsl:if test="svDetails">
							   								    					<xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
							   								    				</xsl:if>
																				<xsl:call-template name="field_actions"/><xsl:attribute name="nowrap">true</xsl:attribute>
																			</td>
																		</xsl:if>
										                              
										                                <xsl:for-each select="radios/rad">
										                                 <td style="white-space: nowrap;">
										                                    <input>
																				  <xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'radioCheckStyle'"/>
																					</xsl:call-template>
																				  </xsl:attribute>
										                                    	  <!-- Enable use of the 'TAB' key to navigate to radio button -->
										                                    	  <xsl:attribute name="tabindex">0</xsl:attribute>
																				  <xsl:attribute name="tabname"><xsl:value-of select="../../../../tabid"/></xsl:attribute>
																				  <xsl:attribute name="onclick">javascript:toggleRadio(event)</xsl:attribute>
																				  <xsl:attribute name = "onfocus">javascript:enableFocus(this)</xsl:attribute>
																				  <xsl:attribute name = "onblur">javascript:disableFocus(this)</xsl:attribute>																				  
											                                      <xsl:choose>
												                                      	<xsl:when test="selected">
												                                        	<xsl:attribute name="selected">true</xsl:attribute>
											                                      		</xsl:when>
											                                      		<xsl:otherwise>
											                                      			<xsl:attribute name="selected">false</xsl:attribute>
											                                      		</xsl:otherwise>
											                                      </xsl:choose>
											                                      
											                                      <xsl:attribute name="type">radio</xsl:attribute>
											                                      <xsl:attribute name="id">radio:<xsl:value-of select="../../../../tabid"/>:<xsl:value-of select="../../fn"/><xsl:value-of select="../../in"/></xsl:attribute>
											                                      <xsl:attribute name="name">radio:<xsl:value-of select="../../../../tabid"/>:<xsl:value-of select="../../fn"/><xsl:value-of select="../../in"/></xsl:attribute>
											                                      <xsl:attribute name="value"><xsl:value-of select="val" /></xsl:attribute>
											                                      <!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
											                                      <xsl:call-template name="fieldEvents1" />
										                                    </input>
										                                    <span>
																				  <xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'enrichment'"/>
																					</xsl:call-template>
																				  </xsl:attribute>
										                                      <xsl:value-of select="cap"/>
										                                    </span>
										                                  </td>
										                                </xsl:for-each>
										                              </tr>
										                            </xsl:when>
										                            <xsl:otherwise>
										                              <xsl:for-each select="radios/rad">
										                                <tr>
										                                 <td style="white-space: nowrap;">
										                                    <input>

																				  <xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'radioCheckStyle'"/>
																					</xsl:call-template>
																				  </xsl:attribute>
										                                        <!-- Enable use of the 'TAB' key to navigate to radio button -->
										                                    	<xsl:attribute name="tabindex">0</xsl:attribute>
																				<xsl:attribute name="tabname"><xsl:value-of select="../../../../tabid"/></xsl:attribute>
																				<xsl:attribute name="onclick">javascript:toggleRadio(event)</xsl:attribute>
																				  <xsl:attribute name = "onfocus">javascript:enableFocus(this)</xsl:attribute>
																				  <xsl:attribute name = "onblur">javascript:disableFocus(this)</xsl:attribute>																				
										                                      	<xsl:choose>
												                                      	<xsl:when test="selected">
												                                        	<xsl:attribute name="selected">true</xsl:attribute>
											                                      		</xsl:when>
											                                      		<xsl:otherwise>
											                                      			<xsl:attribute name="selected">false</xsl:attribute>
											                                      		</xsl:otherwise>
											                                    </xsl:choose>
										                                        <xsl:attribute name="type">radio</xsl:attribute>
										                                        <xsl:attribute name="id">radio:<xsl:value-of select="../../../../tabid"/>:<xsl:value-of select="../../fn"/><xsl:value-of select="../../in"/></xsl:attribute>
										                                        <xsl:attribute name="name">radio:<xsl:value-of select="../../../../tabid"/>:<xsl:value-of select="../../fn"/><xsl:value-of select="../../in"/></xsl:attribute>
										                                        <xsl:attribute name="value"><xsl:value-of select="val" /></xsl:attribute>
										                                        <!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
										                                        <xsl:call-template name="fieldEvents1" />
										                                    </input>
										                                    <span>
																			  <xsl:attribute name="class">
																					<xsl:call-template name="apply_Style">
																						<xsl:with-param name="actualclass" select="'enrichment'"/>
																					</xsl:call-template>
																			  </xsl:attribute>
																			  
										                                      <xsl:value-of select="cap"/>
										                                    </span>
										                                  </td>
										                                </tr>
										                                </xsl:for-each>
										                             </xsl:otherwise>
										                          </xsl:choose>
										                        </tbody>
										                      </table>
								                     </xsl:otherwise>
			  							     	</xsl:choose>
					                    </xsl:when>
					                    
					                    <!-- Upload iframe -->
					                    <xsl:when test="./ty='file'">
											<input type="hidden" id="fieldName:{fn}{in}" name="fieldName:{fn}{in}" tabname="{../../tabid}" value="{v}" oldValue="{v}"/>
											<xsl:choose>
												<xsl:when test="./v=''">
													<span id="fileUploadSpan" style="display:none">
														
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'disabled_dealbox'"/>
															</xsl:call-template>
														</xsl:attribute>
														
													</span>
													<img id="fileUploadSuccessImg" style="display:none;vertical-align:middle;padding-left:8px;">
														<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/uploadSuccess.gif</xsl:attribute>
													</img>
							                    	<iframe id="fileUploadIframe" name="fileUploadIframe" style="width:300px;height:23px;border:0px;" tabIndex="0">
							                    		<xsl:attribute name="src">../jsps/fileUpload.jsp?fragment=<xsl:value-of select="$formFragmentSuffix"/>&amp;skin=<xsl:copy-of select="$skin"/>&amp;trans_upload=<xsl:value-of select="./trans_upload"/>&amp;trans_uploading=<xsl:value-of select="./trans_uploading"/></xsl:attribute>
							                    	</iframe>
												</xsl:when>
												<xsl:otherwise>
													<span id="fileUploadSpan">
														
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'disabled_dealbox'"/>
															</xsl:call-template>
														</xsl:attribute>
														
														<xsl:value-of select="./v"/>
													</span>
													<img style="vertical-align:middle;padding-left:8px;">
														<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/uploadSuccess.gif</xsl:attribute>
													</img>
												</xsl:otherwise>
											</xsl:choose>
					                    </xsl:when>
					                    
										<!-- Input Box - check to see if type is equal to input -->
										<xsl:when test="./ty='input'">
											<!-- normal input box or password? -->
											<input tabindex="0" size="{sp}" id="fieldName:{fn}{in}" name="fieldName:{fn}{in}" tabname="{../../tabid}" value="{v}" oldValue="{v}">
												<!-- Check if it is noinput dropdown field, if it is so disable the text box -->
											    <xsl:choose>
													<xsl:when test="./dd/ndf!='' and not(/responseDetails/userDetails/allowdropdowns)">
														<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'noinput_dropdown'"/>
														</xsl:call-template>
														</xsl:attribute>
														<xsl:attribute name="readonly">readonly</xsl:attribute>
														<xsl:attribute name="tabindex">-1</xsl:attribute>
														<xsl:attribute name="onkeydown">javascript:doNothingOnbackspace(event)</xsl:attribute>
														<xsl:attribute name="onkeypress">javascript:doNothingOnbackspace(event)</xsl:attribute>
													</xsl:when>
												    <xsl:otherwise>
														<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="$dealboxClass"/>
														</xsl:call-template>
													</xsl:attribute>
													 </xsl:otherwise>
											    </xsl:choose>
												<xsl:attribute name="maxlength"><xsl:value-of select="l"/></xsl:attribute>
												
												<!-- If right alignment is set for the field then set the attribute style as right aligned  -->
												<xsl:if test="align = 'R'"> 
													<xsl:attribute name="style">text-align:right;</xsl:attribute>
												</xsl:if>

												
												
												<!-- If the length of the data in field is greater than the maxlength defined in the IN routines .... -->
												<xsl:if test="string-length(v) &gt; l">
												<!-- Set the maxlength of the string to actual length passed back from the server. This is because Firefox truncates the value to maxlength -->
												<xsl:attribute name="maxlength"><xsl:value-of select="string-length(v)"/></xsl:attribute>
												</xsl:if>
												<xsl:attribute name="type">
													<xsl:choose>
														<xsl:when test="pass!=''">password</xsl:when>
														<xsl:when test="se!=''">hidden</xsl:when>
														<xsl:otherwise>text</xsl:otherwise>
													</xsl:choose>
												</xsl:attribute>
												<xsl:if test="./dd!='' and not (/responseDetails/userDetails/allowdropdowns)">
													<xsl:attribute name="dropdown">Y</xsl:attribute>
												</xsl:if>
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
												<xsl:if test="./ll!=''">
													<xsl:attribute name="lng"><xsl:value-of select="ll"/></xsl:attribute>
												</xsl:if>
												<!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
												<xsl:call-template name="fieldEvents"/>
											</input>
										</xsl:when>
										<!-- No Input (readonly, but submitted with form) - check to see if type is equal to noinput -->
										<xsl:when test="./ty='noinput'">
												<xsl:choose>
													<xsl:when test="se!=''">
													<span>
														<xsl:variable name="position">
															<xsl:value-of select="position()"/>
														</xsl:variable>
														<xsl:if test="../ce[$position+1]/v!=''">
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																	<xsl:with-param name="actualclass" select="'disabled_dealbox'"/>
															</xsl:call-template>
														</xsl:attribute>
														</xsl:if>														
														<xsl:value-of select="../ce[$position+1]/v"/>
													</span>
													<input type="hidden" id="fieldName:{fn}{in}" name="fieldName:{fn}{in}" value="{v}" oldValue="{v}">
															<xsl:if test="./fieldError='Y'">
																<xsl:attribute name="fieldError">Y</xsl:attribute>
															</xsl:if>
														</input>
													</xsl:when>
													
													<xsl:otherwise>
														<!-- Cap tag to be displayed if present -->
														<xsl:choose>
														<xsl:when test="cap!=''">
															<span id="disabled_{fn}{in}">
																<xsl:variable name="disabledText" select="'disabled_'"/>
																<xsl:variable name="className">
																	<xsl:value-of select="$disabledText"/><xsl:value-of select="$dealboxClass"/>
																</xsl:variable>
																<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																			<xsl:with-param name="actualclass" select="$className"/>
																	</xsl:call-template>
																</xsl:attribute>
															    <xsl:attribute name="style">width:<xsl:value-of select="(0.11 * sp)"/>em;</xsl:attribute>
																<xsl:value-of select="cap"/>
															</span>
														</xsl:when>
														<xsl:when test="v!=''">
															<span id="disabled_{fn}{in}">						
																<xsl:variable name="disabledText" select="'disabled_'"/>
																<xsl:variable name="className">
																	<xsl:value-of select="$disabledText"/><xsl:value-of select="$dealboxClass"/>
																</xsl:variable>
																<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																			<xsl:with-param name="actualclass" select="$className"/>
																	</xsl:call-template>
																</xsl:attribute>
															    <xsl:attribute name="style">width:<xsl:value-of select="(0.11 * sp)"/>em;</xsl:attribute>
																<xsl:value-of select="v"/>
															</span>
														</xsl:when>
														</xsl:choose>
														<xsl:if test="not(./idesc)">
															<input type="hidden" id="fieldName:{fn}{in}" name="fieldName:{fn}{in}" value="{v}" oldValue="{v}">
																<xsl:if test="./fieldError='Y'">
																	<xsl:attribute name="fieldError">Y</xsl:attribute>
																</xsl:if>
															</input>
														</xsl:if>
													</xsl:otherwise>
												</xsl:choose>
										</xsl:when>

										<!-- Input field being viewed - check to see if type is equal to viewed -->
										<xsl:when test="./ty='viewed'">
										    <xsl:choose>
										    	<xsl:when test="count(v) > 1">
											         <xsl:choose>
												         <xsl:when test="se!=''">
												         <!-- Show enrichment span only when field attribute is "Enrichment Only" -->
												             <span>
															 	<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																		<xsl:with-param name="actualclass" select="'enrichment'"/>
																	</xsl:call-template>
																 </xsl:attribute>
															
												             	<xsl:variable name="position">
																	<xsl:value-of select="position()"/>
																</xsl:variable>
																<xsl:attribute name="id">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
																<xsl:attribute name="name">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
																<!-- v corresponds to the word value -->
																<xsl:value-of select="../ce[$position+1]/v" />
												            </span>
												         </xsl:when>
												         <xsl:otherwise>
											    	         <span>
											    	         	<xsl:attribute name="style">display: inline-block;</xsl:attribute>																 
																<xsl:variable name="disabledText" select="'disabled_'"/>
																<xsl:variable name="className">
																	<xsl:value-of select="$disabledText"/><xsl:value-of select="$dealboxClass"/>
																</xsl:variable>	 
															 	<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																		<xsl:with-param name="actualclass" select="$className"/>
																	</xsl:call-template>
															 	</xsl:attribute>
														
											    		         <table cellspacing="1" summary="">
														             <xsl:for-each select="v">				
														    	         <tr> 
														    		         <td>
														    		             <xsl:attribute name="style">width:<xsl:value-of select="(0.11 * sp)"/>em;</xsl:attribute>
													    				         <xsl:choose>
																		 			<xsl:when test="cap!=''">
																			 			<xsl:value-of select="cap"/>
																		 			</xsl:when>
																					<xsl:otherwise> 
												    									<xsl:value-of select="."/>
																		  			</xsl:otherwise>
																	 			</xsl:choose>
													    			         </td>
													    		         </tr>
															         </xsl:for-each>
														         </table>
													         </span>
													     </xsl:otherwise>
													</xsl:choose>
										    	</xsl:when>
										    	<xsl:otherwise>
												     <xsl:choose>
												         <xsl:when test="se!=''">
												         <!-- Show enrichment span only when field attribute is "Enrichment Only" -->
												             <span>
																
																<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																		<xsl:with-param name="actualclass" select="'disabled_dealbox'"/>
																	</xsl:call-template>
																</xsl:attribute>
																
												                <xsl:variable name="position">
																	<xsl:value-of select="position()"/>
																</xsl:variable>
																<xsl:attribute name="id">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
																<xsl:attribute name="name">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
																<!-- v corresponds to the word value -->
																<xsl:value-of select="../ce[$position+1]/v" />
												             </span>
												         </xsl:when>
														 <xsl:otherwise>
														     <span>
																<xsl:variable name="disabledText" select="'disabled_'"/>
																<xsl:variable name="className">
																	<xsl:value-of select="$disabledText"/><xsl:value-of select="$dealboxClass"/>
																</xsl:variable> 
																<xsl:attribute name="class">
																	<xsl:call-template name="apply_Style">
																		<xsl:with-param name="actualclass" select="$className"/>
																	</xsl:call-template>
																</xsl:attribute>
																 
										    		             <xsl:attribute name="style">width:<xsl:value-of select="(0.11 * sp)"/>em; padding : 0px;</xsl:attribute>
												    	         <xsl:choose>
												    				<xsl:when test="cap!=''">
												        				<xsl:value-of select="cap"/>
												    				</xsl:when>
												    				<xsl:otherwise>
												    					<xsl:value-of select="v"/>
												    				</xsl:otherwise>
												    			</xsl:choose>
												             </span>
													     </xsl:otherwise>
													 </xsl:choose>
										    	</xsl:otherwise>
										    </xsl:choose>
											
										</xsl:when>

										<!--Invisible field - check to see if type is equal to hidden -->
										<xsl:when test="./ty='hidden'">
											<input type="hidden" name="fieldName:{fn}{in}" value="{v}" oldValue="{v}" isHidden ="YES">
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
											</input>
										</xsl:when>
									
										<!-- Text Box -->
										<!-- check to see if type is equal to text -->
										<xsl:when test="./ty='text'">
											<textarea id="fieldName:{fn}{in}" name="fieldName:{fn}{in}">
												
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'textbox'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="cols"><!-- l represents the node maxlen --><xsl:value-of select="l"/></xsl:attribute>
												<xsl:attribute name="rows">5</xsl:attribute>
												<xsl:attribute name="wrap">physical</xsl:attribute>
												<xsl:attribute name="onblur">javascript:formatTextArea(event, '<xsl:value-of select="l"/>')</xsl:attribute>
												<!-- fn represents the node 'fieldname'-->
												<!-- in represents the node 'inpname'-->		
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
												<xsl:if test="./ll!=''">
													<xsl:attribute name="lng"><xsl:value-of select="ll"/></xsl:attribute>
												</xsl:if>
												<!-- v corresponds to the word value -->
												<xsl:value-of select="v"/>
											</textarea>
										</xsl:when>
										
										<!-- Text Box -->
										<!-- check to see if type is equal to No input text box-->										
										<xsl:when test="./ty='NoinputTextbox'">
										  <xsl:if test="v!=''">
											<textarea>
												
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'textbox'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="cols"><!-- l represents the node maxlen --><xsl:value-of select="l"/></xsl:attribute>
												<xsl:attribute name="rows"><xsl:value-of select="maxrow"/></xsl:attribute>
												<xsl:attribute name="wrap">hard</xsl:attribute>
												<xsl:attribute name="disabled">disabled</xsl:attribute>
												<!-- fn represents the node 'fieldname'-->
												<!-- in represents the node 'inpname'-->
												<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
												<xsl:if test="./ll!=''">
													<xsl:attribute name="lng"><xsl:value-of select="ll"/></xsl:attribute>
												</xsl:if>
												<!-- v corresponds to the word value -->
												<xsl:value-of select="v"/>
											</textarea>
										  </xsl:if>	
										  <input type="hidden" id="fieldName:{fn}{in}" name="fieldName:{fn}{in}" value="{v}" oldValue="{v}"/>
										</xsl:when>
																				
										<!-- Check Box - use a hidden field to store the actual value for the server -->
										<!-- check to see if type is equal to checkbox -->
										<xsl:when test="./ty='checkbox'">
										<xsl:choose>
											<xsl:when test="../mvList!=''">
												<xsl:if test="$fullViewMode='false'">
													<xsl:if test="../grdMode='Y'">
														<xsl:call-template name="field_actions"/>
													</xsl:if>
												</xsl:if>
											<!-- Do combo box for multivalue expansion only -->
												<select>
											
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="dealbox"/>
													</xsl:call-template>
												</xsl:attribute>
												
											<!-- fn represents the node 'fieldname'-->
											<!-- in represents the node 'inpname'-->
												<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
											<!-- v corresponds to the word value -->
												<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
												<xsl:if test="./fieldError='Y'">
													<xsl:attribute name="fieldError">Y</xsl:attribute>
												</xsl:if>
											<!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
												<xsl:call-template name="fieldEvents"/>
											<!-- Build the html for the select box options. -->
												<xsl:if test="position()=1">
													<option>
													<!-- Add a 'blank' option -->
														<xsl:attribute name="value"></xsl:attribute>
													</option>
												</xsl:if>
												<xsl:if test="position()!=1">
													<option>
														<xsl:attribute name="value"><xsl:value-of select="chkValue"/></xsl:attribute>
														<!-- v corresponds to the word value -->
														<xsl:if test="chkValue=v">
															<xsl:attribute name="selected">true</xsl:attribute>
														</xsl:if>
														<xsl:value-of select="chkValue"/>
													</option>
													<option>
														<xsl:attribute name="value"><xsl:value-of select="unchkValue"/></xsl:attribute>
														<xsl:if test="unchkValue=v">
															<xsl:attribute name="selected">true</xsl:attribute>
														</xsl:if>
														<xsl:value-of select="unchkValue"/>
													</option>
												</xsl:if>
												</select>
											</xsl:when>
										<xsl:otherwise>
											<input type="checkbox">
												
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'radioCheckStyle'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<!-- fn represents the node 'fieldname'-->
												<!-- in represents the node 'inpname'-->
												<xsl:attribute name="name">CheckBox:fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="id">CheckBox:fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="onclick">javascript:toggleCheckBox(event)</xsl:attribute>
												<xsl:attribute name = "onfocus">javascript:enableFocus(this)</xsl:attribute>
												<xsl:attribute name = "onblur">javascript:disableFocus(this)</xsl:attribute>												
												<!-- checkedCap holds the other language enrichment if exist -->
												<xsl:attribute name="checkedCap"><xsl:value-of select="chkCap"/></xsl:attribute>
												<xsl:attribute name="checkedValue"><xsl:value-of select="chkValue"/></xsl:attribute>
												<xsl:attribute name="uncheckedValue"><xsl:value-of select="unchkValue"/></xsl:attribute>
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
												<xsl:attribute name="toggleFieldName">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												
												<!-- v corresponds to the word value -->
												<xsl:if test="v=chkValue">
													<xsl:attribute name="checked"/>
												</xsl:if>
                        <!-- Set the field attributes and events depending on whether it is a hot field or auto field, etc -->
                        <xsl:call-template name="fieldEvents" />
											</input>
											<input type="hidden">
												<!-- fn represents the node 'fieldname'-->
												<!-- fn represents the node 'fieldname'-->
												<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="id">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<xsl:choose>
 													<xsl:when test="v=chkValue">
 														<xsl:attribute name="value"><xsl:value-of select="chkValue"/></xsl:attribute>
													</xsl:when>
 													<xsl:otherwise>
														<xsl:attribute name="value"><xsl:value-of select="unchkValue"/></xsl:attribute>
 													</xsl:otherwise>
												</xsl:choose>
												<xsl:choose>
                                                    <xsl:when test="v=chkValue">
                                                       <xsl:attribute name="oldValue"><xsl:value-of select="chkValue"/></xsl:attribute>
                                                    </xsl:when>
                                                    <xsl:otherwise>
														<xsl:attribute name="oldValue"><xsl:value-of select="unchkValue"/></xsl:attribute>
                                                    </xsl:otherwise>
                                                </xsl:choose>
											</input>
										</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
										<!-- Toggle Button - use a hidden field to store the actual value for the server -->
										<!-- check to see if type is equal to toggle -->
										<xsl:when test="./ty='toggle'">
											<input type="button">
												<!-- fn represents the node 'fieldname'-->
												<xsl:attribute name="name">ToggleButton:fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="id">ToggleButton:fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="onclick">javascript:toggleButton(event)</xsl:attribute>
												<xsl:attribute name="selectedValue"><xsl:value-of select="chkValue"/></xsl:attribute>
												<xsl:attribute name="unselectedValue"><xsl:value-of select="unchkValue"/></xsl:attribute>
												<xsl:attribute name="tabindex">0</xsl:attribute>
												<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
												<xsl:attribute name="buttonNameFieldName">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<xsl:if test="v=chkValue">
													<xsl:attribute name="value"><xsl:value-of select="chkValue"/></xsl:attribute>
												</xsl:if>
												<!-- v corresponds to the word value -->
												<xsl:if test="v=unchkValue">
													<xsl:attribute name="value"><xsl:value-of select="unchkValue"/></xsl:attribute>
												</xsl:if>
											</input>
											<input type="hidden">
												<!-- in represents the node 'inpname'-->
												<!-- fn represents the node 'fieldname'-->
												<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<xsl:attribute name="id">fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/></xsl:attribute>
												<!-- v corresponds to the word value -->
												<xsl:if test="v=chkValue">
													<xsl:attribute name="value"><xsl:value-of select="chkValue"/></xsl:attribute>
												</xsl:if>
												<!-- v corresponds to the word value -->
												<xsl:if test="v=unchkValue">
													<xsl:attribute name="value"><xsl:value-of select="unchkValue"/></xsl:attribute>
												</xsl:if>
											</input>
										</xsl:when>
										<!-- an empty cell! -->
										<xsl:otherwise/>
									</xsl:choose>
									<!-- Dropdowns and popups! -->
									<!-- dd represents the node dropdown -->
									<xsl:if test="./dd!='' and not (/responseDetails/userDetails/allowdropdowns)">
									<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/></xsl:attribute> 
										<xsl:choose>
											<!-- when the type = ENQUIRY -->
											<xsl:when test="./dd/ty='ENQUIRY'">
												<xsl:if test="se!=''">
															<xsl:call-template name="show_enrichment_only"/>
												</xsl:if>
												<a>
													<!-- n represents the node 'name'-->
													<!-- dd represents the node dropdown -->
													<!-- df represents the node dropfield -->
													<xsl:choose>
														<xsl:when test="ebr='Y'">
															<xsl:attribute name="onClick">javascript:enquiryBuildDropdown(event)</xsl:attribute>
															<xsl:if test="ebrs='Y'">
																<xsl:attribute name="onClick">javascript:enquiryBuildSelect(event)</xsl:attribute>															
															</xsl:if>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="onClick">javascript:enquiryDropdown(event)</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
													<xsl:attribute name="tabindex">-1</xsl:attribute>
													<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
														
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'dropdown_button'"/>
															</xsl:call-template>
														</xsl:attribute>
													<xsl:choose>
														<xsl:when test="ebr='Y'">
															<xsl:attribute name="onkeypress">enquiryBuildDropdown(event)</xsl:attribute>
															<xsl:if test="ebrs='Y'">
																<xsl:attribute name="onkeypress">enquiryBuildSelect(event)</xsl:attribute>															
															</xsl:if>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="onkeypress">enquiryDropdown(event)</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
													
													<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
						       							 <xsl:choose>
				                                      		<xsl:when test="pdf!=''">
				                                        		<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/popupDropDown.gif</xsl:attribute>
			                                      			</xsl:when>
											           	 	<xsl:otherwise>
											            		<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/dropdown.gif</xsl:attribute>
											            	</xsl:otherwise>
					                                   	</xsl:choose>
					                               		<xsl:call-template name="processTabIndex">
															<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
														</xsl:call-template>
					                               		<xsl:attribute name="enquiryField"><xsl:value-of select="./dd/n"/></xsl:attribute>
														<xsl:attribute name="dropField"><xsl:value-of select="./dd/df"/></xsl:attribute>
														<xsl:attribute name="criteriaField"><xsl:value-of select="./dd/criteriaFld"/></xsl:attribute>
														<xsl:attribute name="criteriaOpField"><xsl:value-of select="./dd/criteriaOp"/></xsl:attribute>
														<xsl:attribute name="criteriaDataField">fieldName:<xsl:value-of select="./dd/criteriaDataFld"/></xsl:attribute>
														<xsl:attribute name="criteriaDataFieldValue"><xsl:value-of select="./dd/criteriaDataFieldValue"/></xsl:attribute>
														<xsl:attribute name="criteriaDataString"><xsl:value-of select="./dd/criteriaDataStr"/></xsl:attribute>
														<xsl:attribute name="enqFieldName">fieldName:<xsl:value-of select="fn"/></xsl:attribute>
														<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
														<xsl:attribute name="popupDropField"><xsl:value-of select="pdf"/></xsl:attribute>
														<xsl:if test="se!=''">
															<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
														</xsl:if>
														<xsl:attribute name="noinputDropField"><xsl:value-of select="./dd/ndf"/></xsl:attribute>
													</img>
												</a>
												<xsl:if test="/responseDetails/userDetails/enquirySelectionBox">
												<a>
												 <img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												    <xsl:call-template name="processTabIndex">
														<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
													</xsl:call-template>
													<xsl:attribute name="title">Selection Criteria</xsl:attribute>		
													<xsl:choose>
														<xsl:when test="ebr='Y'">
															<xsl:attribute name="onClick">javascript:enquiryBuildDropdown(event)</xsl:attribute>
															<xsl:if test="ebrs='Y'">
																<xsl:attribute name="onClick">javascript:enquiryBuildSelect(event)</xsl:attribute>															
															</xsl:if>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="onClick">javascript:enquiryDropdown(event)</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
												    <xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/tools/select.gif</xsl:attribute>
													<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'selection_criteria'"/>
															</xsl:call-template>
													</xsl:attribute>
														<xsl:attribute name="enquiryField"><xsl:value-of select="./dd/n"/></xsl:attribute>
														<xsl:attribute name="criteriaField"><xsl:value-of select="./dd/criteriaFld"/></xsl:attribute>
														<xsl:attribute name="criteriaOpField"><xsl:value-of select="./dd/criteriaOp"/></xsl:attribute>
														<xsl:attribute name="criteriaDataField">fieldName:<xsl:value-of select="./dd/criteriaDataFld"/></xsl:attribute>
														<xsl:attribute name="criteriaDataFieldValue"><xsl:value-of select="./dd/criteriaDataFieldValue"/></xsl:attribute>
														<xsl:attribute name="criteriaDataString"><xsl:value-of select="./dd/criteriaDataStr"/></xsl:attribute>
														<xsl:attribute name="enqFieldName">fieldName:<xsl:value-of select="fn"/></xsl:attribute>
														<xsl:attribute name="tabname"><xsl:value-of select="../../tabid"/></xsl:attribute>
														<xsl:attribute name="dropField"><xsl:value-of select="./dd/df"/></xsl:attribute> 
														<xsl:attribute name="popupDropField"><xsl:value-of select="pdf"/></xsl:attribute>
														<xsl:attribute name="selectionDisplay">Yes</xsl:attribute> 
														<xsl:if test="se!=''">
															<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
														</xsl:if>
														<xsl:attribute name="noinputDropField"><xsl:value-of select="./dd/ndf"/></xsl:attribute>
												 </img>
												</a>
												</xsl:if>
												        <xsl:if test="se!=''">
															<xsl:call-template name="show_enrichment_only_append"/>
														</xsl:if>
												
											</xsl:when>
											<xsl:otherwise>
												<xsl:if test="se!=''">
													<xsl:call-template name="show_enrichment_only"/>
												</xsl:if>
												<a>
													<!-- dd represents the node dropdown -->
													<!-- df represents the node dropfield -->
													<xsl:choose>
														<xsl:when test="ebr='Y'">
															<xsl:attribute name="onClick">javascript:enquiryBuildDropdown(event)</xsl:attribute>
															<xsl:if test="ebrs='Y'">
																<xsl:attribute name="onClick">javascript:enquiryBuildSelect(event)</xsl:attribute>															
															</xsl:if>
														</xsl:when>
														<xsl:otherwise>
															 <xsl:attribute name="onClick">javascript:mvDropDown(event)</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
													<xsl:attribute name="tabindex">-1</xsl:attribute>
													<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
														
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'dropdown_button'"/>
															</xsl:call-template>
														</xsl:attribute>
														<xsl:choose>
														<xsl:when test="ebr='Y'">
															<xsl:attribute name="onkeypress">enquiryBuildDropdown(event)</xsl:attribute>
															<xsl:if test="ebrs='Y'">
																<xsl:attribute name="onkeypress">enquiryBuildSelect(event)</xsl:attribute>															
															</xsl:if>
														</xsl:when>
														<xsl:otherwise>
															 <xsl:attribute name="onkeypress">mvDropDown(event)</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:call-template name="processTabIndex">
														<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
													</xsl:call-template>
													<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>														
												    	<xsl:choose>
				                                      		<xsl:when test="pdf!=''">
				                                        		<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/popupDropDown.gif</xsl:attribute>
			                                      			</xsl:when>
											           	 	<xsl:otherwise>
											            		<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/dropdown.gif</xsl:attribute>
											            	</xsl:otherwise>
					                                   	</xsl:choose>
														<xsl:attribute name="mvApplication"><xsl:value-of select="dd"/></xsl:attribute>
														<xsl:attribute name="mvTextField"><xsl:value-of select="df"/></xsl:attribute>
														<xsl:attribute name="mvFieldName">fieldName:<xsl:value-of select="fn"/></xsl:attribute>
														<xsl:attribute name="enquiryField"><xsl:value-of select="n"/></xsl:attribute>
														<xsl:attribute name="dropField"><xsl:value-of select="df"/></xsl:attribute> 
														<xsl:if test="se!=''">
															<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
														</xsl:if>
														<xsl:attribute name="popupDropField"><xsl:value-of select="pdf"/></xsl:attribute>
														<xsl:attribute name="noinputDropField"><xsl:value-of select="./dd/ndf"/></xsl:attribute>
													</img>
												</a>
												<xsl:if test="/responseDetails/userDetails/enquirySelectionBox">
												<a>
												   <xsl:attribute name="tabindex">-1</xsl:attribute>
													<xsl:attribute name="title">Selection Criteria</xsl:attribute>		
													<xsl:attribute name="onclick">javascript:mvDropDown(event)</xsl:attribute>
												 <img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress = "mvDropDown(event)">
												    <xsl:call-template name="processTabIndex">
															<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
													</xsl:call-template>
												    <xsl:attribute name="title">Selection Criteria</xsl:attribute>
												    <xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/tools/select.gif</xsl:attribute>
													<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'selection_criteria'"/>
															</xsl:call-template>
													</xsl:attribute>
													<xsl:attribute name="mvApplication"><xsl:value-of select="dd"/></xsl:attribute>
														<xsl:attribute name="mvTextField"><xsl:value-of select="df"/></xsl:attribute>
														<xsl:attribute name="mvFieldName">fieldName:<xsl:value-of select="fn"/></xsl:attribute>
														<xsl:attribute name="enquiryField"><xsl:value-of select="n"/></xsl:attribute>
														<xsl:attribute name="dropField"><xsl:value-of select="df"/></xsl:attribute> 
														<xsl:attribute name="selectionDisplay">Yes</xsl:attribute>
														<xsl:if test="se!=''">
															<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
														</xsl:if>
														<xsl:attribute name="noinputDropField"><xsl:value-of select="./dd/ndf"/></xsl:attribute>
												 </img>
												</a>
												</xsl:if>
												<xsl:if test="se!=''">
															<xsl:call-template name="show_enrichment_only_append"/>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:if>
									<xsl:if test="./popup='date' and not (/responseDetails/userDetails/allowcalendar)">
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only"/>
										</xsl:if>
										<a>
											<!-- df represents the node dropfield -->
											<xsl:attribute name="onclick">
												<xsl:choose>
													<xsl:when test="$popupDropDown='true'">javascript:dropDownCalendar(event)</xsl:when>
													<xsl:otherwise>javascript:calendar('<xsl:value-of select="df"/>')</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>						
											<xsl:attribute name="tabindex">-1</xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/calendar"/></xsl:attribute>
											<img  onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/calendar"/></xsl:attribute>
												<xsl:call-template name="processTabIndex">
													<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
												</xsl:call-template>
												<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'dropdown_button'"/>
												</xsl:call-template>
												</xsl:attribute>
												<xsl:attribute name="onkeypress">
												<xsl:choose>
													<xsl:when test="$popupDropDown='true'">javascript:dropDownCalendar(event)</xsl:when>
													<xsl:otherwise>javascript:calendar('<xsl:value-of select="df"/>')</xsl:otherwise>
												</xsl:choose>
												</xsl:attribute>
												<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/calendar.gif</xsl:attribute>
												<xsl:attribute name="calendardropfieldname"><xsl:value-of select="df"/></xsl:attribute>
												<xsl:if test="se!=''">
														<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
												</xsl:if>
											</img>
										</a>
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only_append"/>
										</xsl:if>
										
									</xsl:if>
									<xsl:if test="./popup='upload'">
										<a>
											<!-- df represents the node dropfield -->
											<xsl:attribute name="onclick">javascript:upload('<xsl:value-of select="df"/>')</xsl:attribute>
											<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
											<xsl:attribute name="tabindex">-1</xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/attachment"/></xsl:attribute>
											<img  onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
												<xsl:attribute name="onkeypress">javascript:upload('<xsl:value-of select="df"/>')</xsl:attribute>
												<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/attachment"/></xsl:attribute>
												<xsl:call-template name="processTabIndex">
													<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
												</xsl:call-template>			
												<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'dropdown_button'"/>
												</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/camera.gif</xsl:attribute>
											</img>
										</a>
									</xsl:if>
									
									<!-- Recurrence control icon -->
									<xsl:if test="./popup='recurrence'">
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only"/>
										</xsl:if>
										<a>
											<!--  df represents the node dropfield  -->
											<xsl:attribute name="onClick">javascript:dropRecurrence(event)</xsl:attribute>
											<xsl:attribute name="tabindex">-1</xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrence"/></xsl:attribute>
											<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress ="dropRecurrence(event);">
												<xsl:call-template name="processTabIndex">
													<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
												</xsl:call-template>
												<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrence"/></xsl:attribute>	
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'dropdown_button'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/recurr.gif</xsl:attribute>
												<xsl:attribute name="recurrencedropfieldname"><xsl:value-of select="df"/></xsl:attribute>
												<xsl:if test="se!=''">
														<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
												</xsl:if>
											</img>
										</a>
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only_append"/>
										</xsl:if>
									</xsl:if>
									
									<!-- Frequency control icon -->
									<xsl:if test="./popup='frequency'">
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only"/>
										</xsl:if>
										<a>
											<!--  df represents the node dropfield  -->
											<xsl:attribute name="onClick">javascript:dropFrequency(event)</xsl:attribute>
											<xsl:attribute name="tabindex">-1</xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/frequency"/></xsl:attribute>
											<img  onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress = "dropFrequency(event);"> 
												<xsl:call-template name="processTabIndex">
													<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
												</xsl:call-template>
												<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/frequency"/></xsl:attribute>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'dropdown_button'"/>
													</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/fqu.gif</xsl:attribute>
												<xsl:attribute name="frequencydropfieldname"><xsl:value-of select="df"/></xsl:attribute>
												<xsl:if test="se!=''">
														<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
												</xsl:if>
											</img>
										</a>
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only_append"/>
										</xsl:if>
									</xsl:if>
									
									<!-- Relative Calendar control icon -->
									<xsl:if test="./popup='relDate'">
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only"/>
										</xsl:if>
										<a>
											<!--  df represents the node dropfield  -->
											<xsl:attribute name="id">relative-<xsl:value-of select="df"/></xsl:attribute>
											<xsl:attribute name="onClick">javascript:relativeCalendarDisplay(event);</xsl:attribute>
											<xsl:attribute name="tabindex">-1</xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrence"/></xsl:attribute>
											<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" onkeypress = "relativeCalendarDisplay(event);">
												<xsl:call-template name="processTabIndex">
													<xsl:with-param name="type" select="'tabIndexForDropFields'"/>
												</xsl:call-template>
												<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrence"/></xsl:attribute>
												<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'dropdown_button'"/>
												</xsl:call-template>
												</xsl:attribute>
												
												<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/relDate.gif</xsl:attribute>
												<xsl:attribute name="relativeCalendarDropFieldname"><xsl:value-of select="df"/></xsl:attribute>
												<xsl:if test="se!=''">
														<xsl:attribute name="enrichmentOnly"><xsl:value-of select="se"/></xsl:attribute>
												</xsl:if>
											</img>
										</a>
										<xsl:if test="se!=''">
											<xsl:call-template name="show_enrichment_only_append"/>
										</xsl:if>
									</xsl:if>
									
																										
						    		<!-- buttons and hyperlinks -->
									<xsl:for-each select="tool">	
											<td>
											    <xsl:if test="mvDetails">
 												 <xsl:attribute name="mvDetails"><xsl:value-of select="mvDetails"/></xsl:attribute>
 											    </xsl:if>
 											    <xsl:if test="svDetails">
 												    <xsl:attribute name="svDetails"><xsl:value-of select="svDetails"/></xsl:attribute>
 											     </xsl:if>
												<xsl:apply-templates select="."/>
											</td>
									</xsl:for-each>
									
								</td>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:if test="$addlFooter!=''">		
			<table>												
				<xsl:attribute name="name"><xsl:value-of select="$tabHFName"/></xsl:attribute>				
				<xsl:attribute name="isTab">true</xsl:attribute>
				<xsl:attribute name="style">width:0px;</xsl:attribute>				
				<xsl:attribute name="class"><xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'FooterTabAlign'"/>
				</xsl:call-template></xsl:attribute>
				<xsl:if test="./tabid!=''">
					<xsl:attribute name="id"><xsl:value-of select="tabid"/></xsl:attribute>
				</xsl:if>
				<tr><td><pre>															
					<xsl:attribute name="class"><xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'addlFooter'"/>
					</xsl:call-template></xsl:attribute>
					<xsl:value-of select="$addlFooter"/>										
				</pre></td></tr>
			</table>
		</xsl:if>
	
		
			<!-- Hidden fields --> 
			<xsl:for-each select="hr">
				<xsl:if test="./ty='hidden'">
					<input type="hidden">
						<xsl:attribute name="name">fieldName:<xsl:value-of select="fn"/></xsl:attribute>
						<!-- v corresponds to the word value -->
 						<xsl:attribute name="value"><xsl:value-of select="v"/></xsl:attribute>
 						<xsl:attribute name="oldValue"><xsl:value-of select="v"/></xsl:attribute>
 					</input>
 			</xsl:if>
 		</xsl:for-each>
 		
	</xsl:template>

<xsl:template name="field_actions">
	<!--Do not show Multi-Vlaue and Sub-Value icons in confirm versions-->
	<xsl:if test="not($isConfirm='YES')">
		<xsl:if test="mvExpansion='enable' or svExpansion='enable' or mvDeleteNoExpansion='true'">
				<!-- Should we add a multivalue expand button? -->										
				<xsl:if test="mvExpansion='enable'">
					<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:if test="localMulti">
							<xsl:attribute name="localMulti"><xsl:value-of select="localMulti"/></xsl:attribute>
						</xsl:if>
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForExpansionIcons'"/>
						</xsl:call-template>
						<xsl:attribute name="href">javascript:mvExpandClient('<xsl:value-of select="mvDetails"/>')</xsl:attribute>
						<xsl:choose>
							<xsl:when test="localMulti='1'">
								<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandMulti"/></xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>
						<img>						
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'ascell'"/>
								</xsl:call-template>
							</xsl:attribute>					
							<xsl:choose>
								<xsl:when test="localMulti='1'">
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/svexpansion.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/mvexpansion.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/expandMulti"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandMulti"/></xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</img>
					</a>
					<!-- Should we add a multivalue delete button? -->
					<xsl:if test="mvDelete='true'">
						<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
								<xsl:call-template name="processTabIndex">
									<xsl:with-param name="type" select="'tabIndexForExpansionIcons'"/>
								</xsl:call-template>
							<xsl:attribute name="href">javascript:mvDeleteClient('<xsl:value-of select="mvDetails"/>')</xsl:attribute>
						    <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							<img>
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'ascell'"/>
									</xsl:call-template>
								</xsl:attribute>
								<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/mvdelete.gif</xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							</img>
						</a>
					</xsl:if>
				</xsl:if>
				<!-- Should we add a subvalue expand button? -->
				<xsl:if test="svExpansion='enable'">
					<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
							<xsl:call-template name="processTabIndex">
								<xsl:with-param name="type" select="'tabIndexForExpansionIcons'"/>
							</xsl:call-template>
						<xsl:attribute name="href">javascript:svExpandClient('<xsl:value-of select="svDetails"/>')</xsl:attribute>
				    	<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
						<img> 					
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'ascell'"/>
								</xsl:call-template>
							</xsl:attribute>
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/svexpansion.gif</xsl:attribute>
							<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
							<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/expandSub"/></xsl:attribute>
						</img>
					</a>
					<!-- Should we add a subvalue delete button? -->
					<xsl:if test="svDelete='true'">
						<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
								<xsl:call-template name="processTabIndex">
									<xsl:with-param name="type" select="'tabIndexForExpansionIcons'"/>
								</xsl:call-template>
							<xsl:attribute name="href">javascript:svDeleteClient('<xsl:value-of select="svDetails"/>')</xsl:attribute>
						    <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							<img> 								
								<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'ascell'"/>
										</xsl:call-template>
								</xsl:attribute>
								<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/mvdelete.gif</xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							</img>
						</a>
					</xsl:if>			
				</xsl:if>
				
				<!-- Add just a deletion button, the user cannot expand -->
				<xsl:if test="mvDeleteNoExpansion='true'">
						<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
								<xsl:call-template name="processTabIndex">
									<xsl:with-param name="type" select="'tabIndexForExpansionIcons'"/>
								</xsl:call-template>
							<xsl:attribute name="href">javascript:mvDeleteClient('<xsl:value-of select="mvDetails"/>')</xsl:attribute>
						    <xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							<img>
								
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'ascell'"/>
									</xsl:call-template>
								</xsl:attribute>
								<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/mvdelete.gif</xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							</img>
						</a>
				</xsl:if>	

				<!-- For back compatibility. If old server then consider mvsvDel otherwise use svDelete and mvDelete. Above. -->
				<xsl:if test="mvsvDel='yes' and not( mvDelete or svDelete)">
					<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:attribute name="tabindex">0</xsl:attribute>
							<xsl:choose>
								<xsl:when test="not(svExpansion='enable') or ((svExpansion='enable') and (mvExpansion='enable'))">
									<xsl:attribute name="href">javascript:mvDeleteClient('<xsl:value-of select="mvDetails"/>')</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="href">javascript:svDeleteClient('<xsl:value-of select="svDetails"/>')</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						<img>
							
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'ascell'"/>
								</xsl:call-template>
							</xsl:attribute>
							
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/mvdelete.gif</xsl:attribute>
							<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
							<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/deleteMulti"/></xsl:attribute>
						</img>
					</a>
				</xsl:if>			
		</xsl:if>



											<!-- indent the field markers -->
											<!-- the node m refers to mandatory -->
	<xsl:if test="(./fenqlist!='')or(./m = 'yes') or ( (./hotField='Y' or ./hotVal='Y' or ./wvf='Y') and not (/responseDetails/userDetails/hotsAllowed) ) or (./autoLaunchEnq!='' and not (/responseDetails/userDetails/autosAllowed) ) or (./wvf='Y' and not (/responseDetails/userDetails/webValAllowed) )">
		<!-- Allow all fields to have multiple attributes set (e.g. field can be hot field and have an auto launch enquiry) -->
			<xsl:choose>
				<xsl:when test="./hotField='Y' and not (/responseDetails/userDetails/hotsAllowed)">
					<img onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">Hotfield</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'ascell'"/>
							</xsl:call-template>
						</xsl:attribute>
				
						<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/hot.gif</xsl:attribute>
					</img>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./hotVal='Y' and not (/responseDetails/userDetails/hotsAllowed)">
					<img onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">Hotvalidatefield</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'ascell'"/>
							</xsl:call-template>
						</xsl:attribute>
						
						<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/hotVal.gif</xsl:attribute>
					</img>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./wvf='Y' and not (/responseDetails/userDetails/webValAllowed)">
					<img onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">Webvalidatefield</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'ascell'"/>
							</xsl:call-template>
						</xsl:attribute>
						
						<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/webVal.gif</xsl:attribute>
					</img>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./autoLaunchEnq!='' and not (/responseDetails/userDetails/autosAllowed)">
					<img onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">AutoLaunchEnquiry</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'ascell'"/>
							</xsl:call-template>
						</xsl:attribute>
						
						<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/auto.gif</xsl:attribute>
					</img>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<!-- Mandatory Icon-->
				<!-- the node m refers to mandatory -->
				<xsl:when test="./m = 'yes' and (/responseDetails/window/panes/pane/contract/version) != ''">
					<img onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'ascell'"/>
							</xsl:call-template>
						</xsl:attribute>
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">Mandatory Field</xsl:attribute>
						<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/enquiry/required.gif</xsl:attribute>
					</img>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>		
				<!-- Context Enquiries-->
				<xsl:when test="./fenqlist != '' and not (/responseDetails/userDetails/allowcontext)">
					<a onfocus = "focusonKey('inline',event);" onblur = "hideTooltip(event);">
						<xsl:variable name="fenqdescs">
			                <xsl:call-template name="escape-apos">
			                    <xsl:with-param name="string" select="./fenqdescs"/>
			                </xsl:call-template>
						</xsl:variable>
							<xsl:variable name="rawvalue">
						 <xsl:choose>                           
							<xsl:when test="./rawvalue != ''"> 
									<xsl:call-template name="escape-apos">
										<xsl:with-param name="string" select="./rawvalue"/>
									</xsl:call-template>
							</xsl:when>
							<xsl:otherwise> 
									<xsl:call-template name="escape-apos">
										<xsl:with-param name="string" select="./v"/>
									</xsl:call-template>
							</xsl:otherwise>                   
						 </xsl:choose>                          
						</xsl:variable> 
						<xsl:attribute name="href">javascript:doFieldContext('<xsl:value-of select="./fenqlist"/>','<xsl:value-of select="./fenqnames"/>','<xsl:value-of select="$fenqdescs"/>','<xsl:value-of select="./df"/>','<xsl:copy-of select="$skin"/>','<xsl:value-of select="$rawvalue"/>','<xsl:value-of select="./fn"/>','<xsl:value-of select="./in"/>')</xsl:attribute>	                         
						<xsl:call-template name="processTabIndex">
							<xsl:with-param name="type" select="'tabIndexForInfoIcon'"/>
						</xsl:call-template>
						<xsl:attribute name="title">Context Enquiry</xsl:attribute>					                       
							
						<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
							<xsl:if test="$stripFrameToolbars = 'false'">		
							<img>
								<xsl:attribute name="title">Context Enquiry</xsl:attribute>
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'ascell'"/>
									</xsl:call-template>
								</xsl:attribute>
								
								<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/context.gif</xsl:attribute>
							</img>
						</xsl:if>
					</a>
				</xsl:when>
			</xsl:choose>											
	</xsl:if>
	</xsl:if>	
</xsl:template>

	<xsl:template match="tool">
		<xsl:call-template name="tool_n"/>
	</xsl:template>
	
	<xsl:template name="show_enrichment_only"	>
		<span>
			
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'enrichmentonly'"/>
				</xsl:call-template>
			</xsl:attribute>
			<!-- To display enrichment after validation -->
			<xsl:variable name="position">
				<xsl:value-of select="position()"/>
			</xsl:variable>	

			<xsl:variable name="enriFieldValue">
				<xsl:value-of select="../ce[$position]/v"/>
			</xsl:variable>
			
			
				<xsl:if test="../ce[$position+1]/ty = 'enrichment'">
					<xsl:attribute name="id">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
					<xsl:attribute name="name">enri_<xsl:value-of select="../ce[$position+1]/enriId" /></xsl:attribute>
					
					<xsl:attribute name="enrichmentOnly">Y</xsl:attribute>
					
					<xsl:variable name="enriWidth">
						<xsl:value-of select="../ce[$position+1]/sp"/>
					</xsl:variable>
			
					<xsl:variable name="spanWidth">
						<xsl:value-of select="$enriWidth*7"/>
					</xsl:variable>	
			
					<xsl:attribute name="colspan">
						<xsl:value-of select="$enriWidth"/>
					</xsl:attribute>
			
					<xsl:attribute name="style">display:inline-block;height:14px;vertical-align:middle;width:<xsl:value-of select="$spanWidth"/>px;</xsl:attribute>
					
					<!-- v corresponds to the word value -->
					<xsl:if test="$enriFieldValue!=''">
						<xsl:value-of select="../ce[$position+1]/v" />
					</xsl:if>
				</xsl:if>
				
		</span>
	</xsl:template>	
	
	<xsl:template name="show_enrichment_only_append">
		<a>
         <xsl:attribute name="href">javascript:clearField('fieldName:<xsl:value-of select="fn"/><xsl:value-of select="in"/>')</xsl:attribute>
             <img>
				
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'dropdown_button'"/>
					</xsl:call-template>
				</xsl:attribute>
				
                 <xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/cancel.gif</xsl:attribute>
             </img>
        </a>


	</xsl:template>
	
</xsl:stylesheet>
