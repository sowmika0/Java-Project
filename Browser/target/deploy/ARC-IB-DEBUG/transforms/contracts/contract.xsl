<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="tabData.xsl"/>
	<xsl:import href="TopicList.xsl"/>
	<xsl:import href="../menu/topics.xsl"/>
	<xsl:import href="../messages.xsl"/>
	<xsl:import href="../toolbars/breadCrumbs.xsl"/>
	<xsl:import href="../recurrence.xsl"/> <!-- Draws a recurrence control -->
	<xsl:import href="../frequency.xsl"/> <!-- Draws a frequency control -->
	<xsl:import href="../toolbars/toolbars.xsl"/>

	<!-- Set up variables -->
	<xsl:variable name="app" select="string(/responseDetails/window/panes/pane/contract/app)"/>
	<xsl:variable name="ver" select="string(/responseDetails/window/panes/pane/contract/version)"/>
	<xsl:variable name="product" select="string(/responseDetails/window/panes/pane/contract/product)"/>
	<xsl:variable name="maskedId" select="string(/responseDetails/window/panes/pane/contract/keyMask)"/>
	<xsl:variable name="rawId" select="string(/responseDetails/window/panes/pane/contract/key)"/>
	<xsl:variable name="screenMode" select="string(/responseDetails/screenMode)"/>
	<xsl:variable name="skin" select="string(/responseDetails/userDetails/skin)"/>
	<xsl:variable name="showId" select="string(/responseDetails/userDetails/showId)"/>
	<xsl:variable name="imgRoot" select="concat('../plaf/images/', $skin)"/>
	<xsl:variable name="alphabets" select="'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	
    <xsl:variable name="moreactions">
        <xsl:choose>
            <xsl:when test="/responseDetails/window/translations/moreActions!=''">
                <xsl:value-of select="/responseDetails/window/translations/moreActions"/> ...
            </xsl:when>
            <xsl:otherwise>
                More Actions ...
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<!-- Count how many overrides are there on this contract -->
	<xsl:variable name="noOfOverrides" select="count(/responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/overrides/override)"/>
							
	<xsl:template match="contract" name="contract_n">
			
		<xsl:choose>
			<xsl:when test="../subPane"/>	
			<xsl:otherwise>
				<xsl:call-template name="TopicList"/>	
			</xsl:otherwise>
		</xsl:choose>
		<xsl:variable name="application"><xsl:value-of select="app"/></xsl:variable>				
		<div id="divHeader{$formFragmentSuffix}">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'dheader'"/>
					</xsl:call-template>
				</xsl:attribute>
		
			<!--The actual data!-->
			<form method="POST" onsubmit="javascript:return doDefaultButton()" action="BrowserServlet" AUTOCOMPLETE="{/responseDetails/userDetails/autoComplete}">

                <xsl:choose>
                	<xsl:when test="../subPane">
                		<!-- Add the id, with fragment suffix for noFrames mode ....-->
                		<xsl:attribute name="id"><xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" />
						<xsl:if test="not(contains($application,'AA.ARR'))"><xsl:value-of select="$formFragmentSuffix"/></xsl:if></xsl:attribute> 
                		<!-- Create a unique name for the form. This change is for AA type applications. 
                			Every form now has a unique name. This handles multiple forms in one page -->
                		<xsl:attribute name="name"><xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" /></xsl:attribute>
                		<!-- Add an onmousedown event to this form for setting the current form -->
                		<xsl:attribute name="onmousedown">setCurrentForm('<xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" />')</xsl:attribute>
                		<xsl:attribute name="onkeydown">setCurrentForm('<xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" />')</xsl:attribute>			
						<xsl:attribute name="isArrangement">
							<xsl:if test="contains($application,'AA.ARR')">true</xsl:if>						
						</xsl:attribute>
                	</xsl:when>
                	<xsl:otherwise>
                		<!-- Add the id, with fragment suffix for noFrames mode ....-->
                		<xsl:attribute name="id">appreq<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
                		<xsl:attribute name="name">appreq</xsl:attribute>
                		<!-- Add an onmousedown event to this form for setting the current form -->
                		<xsl:attribute name="onmousedown">setCurrentForm('appreq')</xsl:attribute>
                		<xsl:attribute name="onkeydown">setCurrentForm('appreq')</xsl:attribute>
                	</xsl:otherwise>
                </xsl:choose>
				 
                <xsl:if test="../subPane">
                	<input type="hidden" name="title" value="{../title}"/>
                </xsl:if>
                
				<input type="hidden" name="toggle" id="toggle" value=""/>
				<input type="hidden" name="command" id="command" value="globusCommand"/>
				<input type="hidden" name="requestType" id="requestType" value="{$_OFS__APPLICATION_}"/>
				<input type="hidden" name="ofsOperation" id="ofsOperation" value="{$_PROCESS_}"/>
				<input type="hidden" name="ofsFunction" id="ofsFunction" value=""/>
				<input type="hidden" name="ofsMessage" id="ofsMessage" value=""/>
				<input type="hidden" name="GTSControl" id="GTSControl" value=""/>
				<input type="hidden" name="routineName" id="routineName" value=""/>
				<input type="hidden" name="routineArgs" id="routineArgs" value=""/>
				<input type="hidden" name="cfNavOperation" id="cfNavOperation" value=""/>
				<input type="hidden" name="unlock" id="unlock" value=""/>
				<input type="hidden" name="activeTab" id="activeTab">
					<xsl:attribute name="value"><xsl:value-of select="activetab"/></xsl:attribute>
				</input>
				<input type="hidden" name="SaveChangesText" id="SaveChangesText">
				       <xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/translations/SaveChanges"/></xsl:attribute>
				</input>
				<input type="hidden" name="contextenqdisplay" id="contextenqdisplay">
				       <xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/Autoenqdisplay"/></xsl:attribute>
				</input>
				<input type="hidden" name="RecordDelText" id="RecordDelText">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/RecordDelete"/></xsl:attribute>
				</input>
				<input type="hidden" name="expansionHistory" id="expansionHistory">
					<xsl:attribute name="value"><xsl:value-of select="expansionHistory"/></xsl:attribute>
				</input>
				<input type="hidden" name="version" id="version">
					<xsl:attribute name="value"><xsl:value-of select="version"/></xsl:attribute>
				</input>
				<input type="hidden" name="previousVersion" id="previousVersion">
                	<xsl:attribute name="value"><xsl:value-of select="previousVersion"/></xsl:attribute>
                </input>
				<input type="hidden" name="application" id="application">
					<xsl:attribute name="value"><xsl:value-of select="app"/></xsl:attribute>
				</input>
				
				<input type="hidden" name="existRecPaste" id="existRecPaste">
					<xsl:attribute name="value"><xsl:value-of select="existRecPaste"/></xsl:attribute>
				</input>
				<!-- Add this field to the form if the transaction complete happened. This will be used to fire the transactionComplete event -->
				<xsl:if test="/responseDetails/window/panes/pane/contract/txnComplete">
					<input type="hidden" name="txnComplete" id="txnComplete" value="true" />
				</xsl:if>
				
				
				<input type="hidden" name="decrypt" id="decrypt">
					<xsl:attribute name="value"><xsl:value-of select="decrypt"/></xsl:attribute>
				</input>
				<input type="hidden" name="addGroupTab" id="addGroupTab">
                     <xsl:attribute name="value"><xsl:value-of select="addGroupTab"/></xsl:attribute>
                </input>
				<input type="hidden" name="deleteGroupTab" id="deleteGroupTab">
					<xsl:attribute name="value"><xsl:value-of select="deleteGroupTab"/></xsl:attribute>
				</input>
				<input type="hidden" name="expandableTab" id="expandableTab">
					<xsl:attribute name="value"><xsl:value-of select="expandableTab"/></xsl:attribute>
				</input>
				<input type="hidden" name="tabEnri" id="tabEnri">
					<xsl:attribute name="value"><xsl:value-of select="tabEnri"/></xsl:attribute>
				</input>				
				<input type="hidden" name="product" id="product">
					<xsl:attribute name="value"><xsl:value-of select="product"/></xsl:attribute>
				</input>
				<input type="hidden" name="name" id="name" value=""/>
				<input type="hidden" name="operation" id="operation" value=""/>
				<input type="hidden" name="windowName" id="windowName" value=""/>
				<input type="hidden" name="toolbarTarget" id="toolbarTarget" value=""/>
				<input type="hidden" name="overridesAccepted" id="overridesAccepted" >
					<xsl:attribute name="value"><xsl:value-of select="TopicList/MainTopic/ts/overrides/override/overrideresponse"/></xsl:attribute>
				</input>

				<input type="hidden" name="overridesApproved" id="overridesApproved" >
					<xsl:attribute name="value"><xsl:value-of select="TopicList/MainTopic/ts/overrides/overrideApproved"/></xsl:attribute>
				</input>
				
				<input type="hidden" name="overrideUnApproved" id="overrideUnApproved" >
					<xsl:attribute name="value"><xsl:value-of select="TopicList/MainTopic/ts/overrides/overrideUnApproved"/></xsl:attribute>
				</input>
				
				<input type="hidden" name="focus" id="focus">
					<xsl:attribute name="value"><xsl:value-of select="focus"/></xsl:attribute>
				</input>
				<input type="hidden" name="ContractStatus" id="ContractStatus">
					<xsl:attribute name="value"><xsl:value-of select="ContractStatus"/></xsl:attribute>
				</input>
				<input type="hidden" name="windowUnqRef" id="windowUnqRef">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/windowUnqRef"/></xsl:attribute>
				</input>

			<!-- Context flow changes starts here -->
				<input type="hidden" name="editVersion" id="editVersion">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/editVersion"/></xsl:attribute>
				</input>

				<input type="hidden" name="confirmVersion" id="confirmVersion">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/confirmVersion"/></xsl:attribute>
				</input>

				<input type="hidden" name="previewVersion" id="previewVersion">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/previewVersion"/></xsl:attribute>
				</input>

				<input type="hidden" name="enqname" id="enqname" value=""/>
				<input type="hidden" name="enqaction" id="enqaction" value=""/>
				<input type="hidden" name="dropfield" id="dropfield">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/></xsl:attribute>
				</input>
				<input type="hidden" name="previousEnqs" id="previousEnqs">
					<xsl:attribute name="value"><xsl:value-of select="control/previousEnqs"/></xsl:attribute>
				</input>
				<input type="hidden" name="previousEnqTitles">
					<xsl:attribute name="value"><xsl:value-of select="control/previousEnqTitles"/></xsl:attribute>
				</input>
				<input type="hidden" name="clientStyleSheet" id="clientStyleSheet" value=""/>
				<input type="hidden" name="windowSizes" id="windowSizes">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/windowCoordinates/windowTop"/>:<xsl:value-of select="/responseDetails/windowCoordinates/windowLeft"/>:<xsl:value-of select="/responseDetails/windowCoordinates/windowWidth"/>:<xsl:value-of select="/responseDetails/windowCoordinates/windowHeight"/></xsl:attribute>
				</input>
				<input type="hidden" name="changedFields" id="changedFields">
					<xsl:attribute name="value"><xsl:value-of select="changedFields"/></xsl:attribute>
				</input>
				
				<input type="hidden" name="newCommands" id="newCommands">
					<xsl:choose>
						<xsl:when test="../subPane">
							<xsl:attribute name="value"><xsl:value-of select="../newCmds"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="value"><xsl:value-of select="/responseDetails/newCmds"/></xsl:attribute>		
						</xsl:otherwise>
					</xsl:choose>
				</input>
				
				<input type="hidden" name="screenMode" id="screenMode">
					<xsl:choose>
						<xsl:when test="../subPane">
							<xsl:attribute name="value"><xsl:value-of select="../screenMode"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="value"><xsl:value-of select="/responseDetails/screenMode"/></xsl:attribute>		
						</xsl:otherwise>
					</xsl:choose>
				</input>

				<input type="hidden" name="lockArgs" id="lockArgs">
					<xsl:choose>
						<xsl:when test="../subPane">
							<xsl:attribute name="value"><xsl:value-of select="../lockArgs"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="value"><xsl:value-of select="/responseDetails/lockArgs"/></xsl:attribute>		
						</xsl:otherwise>
					</xsl:choose>
				</input>
				    
				<input type="hidden" name="RecordRead" id="RecordRead">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/RecordRead"/></xsl:attribute>
				</input>
				<input type="hidden" name="PastedDeal" id="PastedDeal">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/panes/pane/contract/PastedDeal"/></xsl:attribute>
				</input>				
				<!-- Process User Details -->
				<xsl:call-template name="userDetails"/>
				
				<!--create the hidden fields to receive the data from the check bxes-->
				<xsl:choose>
					<!-- ts stands for topics -->
					<xsl:when test="TopicList/MainTopic/ts/overrides!=''">
						<xsl:for-each select="TopicList/MainTopic/ts/overrides/override">
							<input type="hidden">
								<xsl:attribute name="value"><xsl:value-of select="overrideresponse"/></xsl:attribute>
								<xsl:attribute name="name">overrideText:<xsl:value-of select="overridetext"/>:value</xsl:attribute>
								<xsl:attribute name="id">overrideText:<xsl:value-of select="overridetext"/>:value</xsl:attribute>
							</input>
						</xsl:for-each>
					</xsl:when>
				</xsl:choose>
				
		        <!--create the hidden fields to receive the data from the password boxes -->
		        <xsl:choose>
		          <!-- ts stands for topics -->
		          <xsl:when test="TopicList/MainTopic/ts/authentications!=''">
		            <xsl:for-each select="TopicList/MainTopic/ts/authentications/authentication">
		              <input type="hidden">
		                <xsl:attribute name="name">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>
		                <xsl:attribute name="id">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>  
		              </input>
		            </xsl:for-each>
		          </xsl:when>
		        </xsl:choose>
				
				<!--create the hidden fields to receive the data from the warning combo boxes-->
				<xsl:choose>
					<!-- ts stands for topics -->
					<xsl:when test="TopicList/MainTopic/ts/warnings!=''">
						<xsl:for-each select="TopicList/MainTopic/ts/warnings/warning">
							<input type="hidden">
								<xsl:attribute name="value"><xsl:value-of select="warningresponse"/></xsl:attribute>
								<xsl:attribute name="name">warningText:<xsl:value-of select="warningtext"/>:value</xsl:attribute>
								<xsl:attribute name="id">warningText:<xsl:value-of select="warningtext"/>:value</xsl:attribute>
							</input>
						</xsl:for-each>
					</xsl:when>
				</xsl:choose>
				
				<!-- create hidden fields to receive the data from the rekey fields in topics   -->
				
				<xsl:choose>
				   <xsl:when test="TopicList/MainTopic/ts/rekeyFields!=''">
				    <xsl:for-each select="TopicList/MainTopic/ts/rekeyFields/rekeyField">  
				      <input type="hidden">
				      	  <xsl:attribute name="name">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/> </xsl:attribute>
				      	  <xsl:attribute name="id">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/> </xsl:attribute>
				      	  <xsl:attribute name="value"><xsl:value-of select="rekeyFieldValue"/></xsl:attribute>
				      </input>
	  		        </xsl:for-each>  
				   </xsl:when>
				</xsl:choose>
				
				<!-- Add transaction id if we are a sub pane since we wont add the id section. -->
				<xsl:if test="../subPane">
					<input type="hidden" name="transactionId" id="transactionId">
						<xsl:attribute name="value"><xsl:value-of select="key"/></xsl:attribute>											
					</input>	
				</xsl:if>
				
				
				<!-- Extract the title -->
				<title>
					<xsl:value-of select="title"/>
					<xsl:value-of select="version"/> - <xsl:value-of select="company"/>
				</title>
				
				<!-- Hidden iframe for submitting warm field requests -->
				<div>
					<iframe id="iframe:validation" name="iframe:validation" src="../html/blank_enrichment.html" width="0" height="0" align="left" frameborder="0"></iframe>
				</div>
				
				<xsl:if test="control/crumbs/tool !='' or (toolbars/toolbar/tool!='' or header!='' and not(contains($application,'AA.ARR')))">
				<div id="toolBar" style="width:99%">
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'position_box'"/>
						</xsl:call-template>
					</xsl:attribute>
				<!-- Show any enquiry breadcrumbs -->
				<xsl:if test="control/crumbs/tool !='' ">
					<xsl:call-template name="breadCrumbs"/>
				</xsl:if>
				
				<!-- Show the toolbars -->
				<table  summary="">

                    <!-- Normally we set up each deal with a toolbar and more actions block -->
                <thead>
                    <xsl:if test="$stripFrameToolbars = 'false'">
    					<tr>
						<td>
							<table  summary="">
								<tr>
									<td>
										<!-- The actions and toolbar buttons go here -->
										<table id="goButton"  summary="">
											<tr>
												<td>
													<!-- Process the actions -->
													<table summary="">
													
															<!--  Process the main toolbars -->
															<xsl:for-each select="toolbars">
																		<td>
																			
																			<xsl:apply-templates select="."/>
																		</td>
																</xsl:for-each>
																		
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
                    </xsl:if>

					<tr>
						<td>
							<xsl:choose>
								<!-- if a fieldset then don't add id section -->
								<xsl:when test="../subPane"></xsl:when>
								<xsl:otherwise>
									<table border="0" cellpadding="0" cellspacing="0" summary=""><!-- application and version as tooltip -->
									<tr>
										<td id="dealtitle" tabindex="-1">
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'dealtitle'"/>
												</xsl:call-template>
											</xsl:attribute>
			                                <xsl:choose>
                                                <xsl:when test="$isArc = 'false'">  <!-- display the version name only if it is internal Browser-->
                                                   <xsl:attribute name="title"><xsl:value-of select="app"/>
                                                   <xsl:value-of select="version"/></xsl:attribute>
                                                   <a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
                                                    <xsl:attribute name="title"><xsl:value-of select="app"/>
                                                    <xsl:value-of select="version"/></xsl:attribute>                                                   	<xsl:attribute name="style">text-decoration:none</xsl:attribute>
                                                   	<xsl:attribute name="href">javascript:help("<xsl:value-of select='app'/>");</xsl:attribute>
                                                   	<xsl:value-of select="header[1]"></xsl:value-of>
                                                   </a>
                                                 </xsl:when>
                                                <xsl:otherwise>
                                                   <xsl:attribute name="title"><xsl:value-of select="header[1]"/></xsl:attribute>
                                                   	<xsl:value-of select="header[1]"/>
                                                </xsl:otherwise>
                                             </xsl:choose>

										</td> <!-- first header -->
	
										<!-- Display the headers -->
	
										<!-- Here is the input box, etc. -->
	
										<xsl:call-template name="idsection"/>
	
	                    		        <xsl:if test="$stripFrameToolbars = 'false'">
	                    		        	<td nowrap="nowrap">
	                    		        		<img width="2" src="../plaf/images/default/block.gif"/>
		 										<xsl:if test="/responseDetails/userDetails/multiCo='Y'">(<xsl:value-of select="/responseDetails/userDetails/company"/>)</xsl:if>
	 										</td>
										</xsl:if>
									</tr>
									<xsl:for-each select="header">
										<xsl:if test="position()>1">
											<tr>
												<td  id="dealtitle" tabindex="0">
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'dealtitle'"/>
														</xsl:call-template>
													</xsl:attribute>
													<xsl:value-of select="."/>
												</td>
											</tr>
										</xsl:if>
									</xsl:for-each>
									<xsl:if test="showMultHeaders = 'yes' ">
										<xsl:for-each select="furtherHeader">
											<tr>
												<td id="dealtitle" tabindex="0">
													<xsl:attribute name="class">
														<xsl:call-template name="apply_Style">
															<xsl:with-param name="actualclass" select="'dealtitle'"/>
														</xsl:call-template>
													</xsl:attribute>
													<xsl:value-of select="."/>
												</td>
											</tr>
										</xsl:for-each>
									</xsl:if>
									</table>							
								</xsl:otherwise>
							</xsl:choose>							
						</td>
					</tr>
					</thead>
				</table>
				
				</div>
				</xsl:if>
<!-- Here we put the div to show the errors, etc. -->
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/contract/thisPreview='1'">	</xsl:when>
			<xsl:otherwise>
			   <xsl:variable name="displayBoxClass">
			        <xsl:choose>
			            <xsl:when test="TopicList/MainTopic/ts/browserMessages or TopicList/MainTopic/ts/errors or TopicList/MainTopic/ts/warnings or TopicList/MainTopic/ts/authentications or TopicList/MainTopic/ts/overrides or TopicList/MainTopic/ts/rekeyFields">display_box</xsl:when>
			            <xsl:otherwise>display_box_none</xsl:otherwise>
			        </xsl:choose>
			    </xsl:variable>
    
				<div id = "error_box">
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="$displayBoxClass"/>
							</xsl:call-template>
						</xsl:attribute>
					<xsl:choose>
						<xsl:when test="../subPane"/>	
						<xsl:otherwise>														
								<table  summary="">
									<tbody>
									<xsl:for-each select="TopicList/MainTopic/ts">
											<xsl:if test="id='errors' or t/ty='ERRORS' or id='messages' or t/ty='WARNINGS' or t/ty='AUTH' or t/ty='REKEYS'">
												<tr>
													<td>
														<xsl:call-template name="topics_n"></xsl:call-template>
													</td>
												</tr>
											</xsl:if>
										</xsl:for-each>
									</tbody>
								</table>
								
							
						</xsl:otherwise>	
					</xsl:choose>					
				</div>
			</xsl:otherwise>	</xsl:choose>
				<!-- Display transaction Messages if deal was completed OK -->
 				<xsl:if test="messages!=''"><div>
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'display_box'"/>
					</xsl:call-template>
				</xsl:attribute>
 				<xsl:for-each select="messages">
 					<xsl:apply-templates select="."/>
 				</xsl:for-each></div></xsl:if>

			<!-- height and width has to be mentioned in min-height,min-width with auto !important style for fire fox to auto adjust the div border in contract screen-->
 			<div onscroll="javascript:testmouse(event);" id="contract_screen_div">
                <xsl:choose>
                     <xsl:when test="$stripFrameToolbars = 'true'">
                         <xsl:attribute name="style">width:99%; height: auto !important</xsl:attribute>
                     </xsl:when>
                     <xsl:otherwise>
                          <xsl:attribute name="style">width:99%;min-height:50px;height: auto !important</xsl:attribute>
                     </xsl:otherwise>
                </xsl:choose>
                  
				<xsl:if test="tabData!=''">
				   
					    <xsl:choose>  <!-- Is this a fieldset or normal contract. If normal contract, add the display_box class. -->
							<xsl:when test="../subPane">

									<xsl:call-template name="buildContractScreen" />		

							</xsl:when>						
							<xsl:otherwise>
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'scroll_contract_screen'"/>
									</xsl:call-template>
								</xsl:attribute>
								<xsl:call-template name="buildContractScreen" />
							</xsl:otherwise>
						</xsl:choose>
					
				</xsl:if>
			
				<!-- Otherwise just the commit button, if present -->
<xsl:if test="$stripFrameToolbars ='true'">						
		<xsl:if test="/responseDetails/window/panes/pane/contract/thisPreview='1'">
				<table class="belowDealButtonBox" cellpadding="5"  summary="">
	                    <tr>
	                        <td>
								<xsl:variable name="displayBoxClass">
									<xsl:choose>
										<xsl:when test="/responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/browserMessages or /responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/errors or /responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/warnings or /responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/authentications or (/responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/overrides and noOfOverrides=1) or /responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts/rekeyFields">display_box</xsl:when>
										 <xsl:otherwise>display_box_none</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<div id = "error_box">
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="$displayBoxClass"/>
									</xsl:call-template>
								</xsl:attribute>									
									<xsl:choose>
										<xsl:when test="../subPane"/>	
										<xsl:otherwise>		
										<table summary="">
											<tbody>
												<xsl:for-each select="/responseDetails/window/panes/pane/contract/TopicList/MainTopic/ts">
													<xsl:if test="id='errors' or t/ty='ERRORS' or id='messages' or t/ty='WARNINGS' or t/ty='AUTH' or t/ty='REKEYS'">
														<tr><td>
															<xsl:call-template name="topics_n"></xsl:call-template>
														</td></tr>
													</xsl:if>
												</xsl:for-each>
											</tbody>
										</table>
										</xsl:otherwise>	
									</xsl:choose>				
								</div>
										
	                            </td>
	                        </tr>
	             </table>
		</xsl:if>
					
       <xsl:choose>
				<!-- if there is a toolbar attached to the version, then add it instead of default tolbar-->
				<xsl:when test="/responseDetails/window/panes/pane/contract/toolbartype='VERSION.TOOLBAR'">
                	<xsl:call-template name="runDefault"/>
					</xsl:when>
				<!-- Add default toolbar -->	
				<xsl:otherwise>				
					<table cellpadding="5"  summary="">
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'belowDealButtonBox'"/>
								</xsl:call-template>
							</xsl:attribute>
							<tr>
							<td>
						
						<xsl:for-each select="toolbars/toolbar/tool">
							<xsl:choose>
                            <!-- Test for 'I' funct, not see mode and enabled (=NO) element missing -->
							<xsl:when test="(((funct = 'I') and ($screenMode!='S') and not(enabled)) or ((funct = 'A') and ($screenMode ='S') and not(enabled)))">
								<xsl:choose>
									<xsl:when test="/responseDetails/window/panes/pane/contract/thisPreview='1'">										
											<td>
												<xsl:apply-templates select="."/>
											</td>
									</xsl:when>
									<xsl:otherwise>
										<!--  add a commit button even if there is only 1 override in noFrames mode -->
									
											<td>
												<xsl:apply-templates select="."/>
											</td>
									
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<!-- Test for 'D' funct, not see mode and enabled (=NO) element missing -->
							<xsl:when test="(funct = 'D') and not (enabled)">												
									<td>
											<xsl:apply-templates select="."/>
									</td>						
							</xsl:when>
							<!-- Test for 'R' funct, not see mode and enabled (=NO) element missing -->
							<xsl:when test="(funct = 'R') and not (enabled)">												
									<td> 
											<xsl:apply-templates select="."/>
									</td>												
							</xsl:when>
							<!-- To enable toolbar during authorizing a record using ARC-IB -->
							<xsl:when test="(funct = 'A') and not (enabled)">												
									<td> 
											<xsl:apply-templates select="."/>
									</td>												
							</xsl:when>												
					    </xsl:choose>
					</xsl:for-each>
					</td>
					</tr>
				</table>				
		       </xsl:otherwise>
       </xsl:choose>
 </xsl:if>	
        </div>
     </form>
			
			<!-- An enquiry form for going back to an enquiry from a drilldown -->
			<form name="enquiry" method="POST" action="BrowserServlet">
                
                <!-- Add the id, with fragment suffix for noFrames mode ....-->
                <xsl:attribute name="id">enquiry<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
                
				<input type="hidden" name="requestType" id="requestType" value="{$_OFS__ENQUIRY_}"/>
				<input type="hidden" name="routineName" id="routineName" value=""/>
				<input type="hidden" name="routineArgs" id="routineArgs"/>
				<input type="hidden" name="command" id="command" value="globusCommand"/>
				<input type="hidden" name="dropfield" id="dropfield">
					<xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/></xsl:attribute>
				</input>
				<input type="hidden" name="previousEnqs" id="previousEnqs">
					<xsl:attribute name="value"><xsl:value-of select="control/previousEnqs"/></xsl:attribute>
				</input>
				<input type="hidden" name="previousEnqTitles" id="previousEnqTitles">
					<xsl:attribute name="value"><xsl:value-of select="control/previousEnqTitles"/></xsl:attribute>
				</input>
			</form>

            <div id="dropDownDiv" style="position:absolute;top:0px;right:0px;width:0px;height:0px;border:none;">
              <!--Have to include an iFrame as IE has a problem with elements overlapping comboboxes.
                  An iFrame solves this issue for IE!!-->
              <iframe id="dropDownIframe" frameborder="0" style="position:absolute;top:0px;left:0px;width:0px;height:0px;" src="../html/blank_enrichment.html" >
	      </iframe>
              <div id="dropDownDivDisplay" style="position:absolute;left:0px;width:0px;height:0px;overflow:auto;border-style:solid;border:solid;border-width:1px;border-color:#7f9db9;background-color:white;" >
              </div> 
            </div>

        </div> <!-- end divHeader -->

		<!-- Display Enquiry Selection if no key came back -->
		<xsl:for-each select="selDets">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="buildContractScreen" >
		<table id="alltab" style="white-space: nowrap;"  summary="">
				<tr>
					<td>						
						<xsl:if test="tabData/mainTab!=''">
							<xsl:for-each select="tabData/mainTab">
								<xsl:apply-templates select="."/>
							</xsl:for-each>
						</xsl:if>
					</td>
				</tr>

				<!-- Show the tabs for the deal - if there are more than 1, but not if there was a maintab -->
				<xsl:variable name="tabCount" select="tabCount"/><!-- warning: result is a node-set -->

				<xsl:if test="( $tabCount > 1 and tabData/tab) or ( $tabCount = 1 and tabData/mainTab='' and tabData/tab/tabname)">
					<tr>
						<td>
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'tab-nav-base'"/>
								</xsl:call-template>
							</xsl:attribute>
							<table  cellpadding="1px" summary="">
								<xsl:attribute name="class">
									<xsl:call-template name="apply_Style">
										<xsl:with-param name="actualclass" select="'tab-nav'"/>
									</xsl:call-template>
								</xsl:attribute>
								<xsl:choose>
									<xsl:when test = "$multiPane!='false' and ../subPane">
										<xsl:attribute name="id">headtab_<xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" /></xsl:attribute>
									</xsl:when>							
									<xsl:otherwise>
										<xsl:attribute name="id">headtab<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>			
									</xsl:otherwise>
								</xsl:choose>
								<tr>
									<xsl:for-each select="tabData/tab">
										<!-- if the tab has no name then don't add it. -->
										<xsl:if test="tabname">
							                		<td>
							                			<a onclick="{tabhref}" href="javascript:void(0)">
														<xsl:attribute name="class">
															<xsl:call-template name="apply_Style">
																<xsl:with-param name="actualclass" select="'nonactive-tab'"/>
															</xsl:call-template>
														</xsl:attribute>
							                			<span><xsl:value-of select="tabname"/></span>
						                				</a>
					                				</td>
										</xsl:if>
									</xsl:for-each>
								</tr>
							</table>
						</td>
					</tr>
				</xsl:if>

				<tr>
					<td>
						<!-- Show tabs -->
						<xsl:for-each select="tabData/tab">
							<xsl:apply-templates select="."/>
						</xsl:for-each>
						</td>
				</tr>

		</table>

	</xsl:template>
	
	<xsl:template match="tab" name="tab">
		<xsl:call-template name="tab_n"/>
	</xsl:template>
	
	<xsl:template match="mainTab" name="mainTab">
		<xsl:call-template name="tab_n"/>
	</xsl:template>
	
	<xsl:template match="TopicList" name="TopicList">
		<div id="divMenu">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'dmenu'"/>
					</xsl:call-template>
				</xsl:attribute>
			<table align="left" width="100%" summary="">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'dmenutable'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:attribute name="align"><xsl:choose><xsl:when test="/responseDetails/userDetails/lngDir='rtl'">right</xsl:when><xsl:when test="/responseDetails/userDetails/lngDir='RTL'">right</xsl:when><xsl:otherwise>left</xsl:otherwise></xsl:choose></xsl:attribute>
				<tr>
					<td valign="top">
						<form name="mainmenu" method="POST" action="BrowserServlet">
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'dealMenu'"/>
								</xsl:call-template>
							</xsl:attribute>

                            <!-- Add the id, with fragment suffix for noFrames mode ....-->
                            <xsl:attribute name="id">mainmenu<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
                
							<input type="hidden" name="requestType" id="requestType" value="{$_UTILITY__ROUTINE_}"/>
							<input type="hidden" name="routineName" id="routineName" value="{$_OS__NEW__DEAL_}"/>
							<input type="hidden" name="routineArgs" id="routineArgs"/>
							<input type="hidden" name="version" id="version">
								<xsl:attribute name="value"><xsl:copy-of select="$ver"/></xsl:attribute>
							</input>
							<input type="hidden" name="application" id="application">
								<xsl:attribute name="value"><xsl:copy-of select="$app"/></xsl:attribute>
							</input>
							<input type="hidden" name="ofsOperation" id="ofsOperation"/>
							<input type="hidden" name="cfNavOperation" id="cfNavOperation" value=""/>
							<input type="hidden" name="ofsFunction" id="ofsFunction"/>
							<input type="hidden" name="transactionId" id="transactionId"/>
							<input type="hidden" name="command" id="command" value="globusCommand"/>
							<input type="hidden" name="name" id="name" value=""/>
							<input type="hidden" name="operation" id="operation" value=""/>
							<input type="hidden" name="windowName" id="windowName" value=""/>
							<input type="hidden" name="apiArgument" id="apiArgument" value=""/>
							<!-- Process User Details -->
							<xsl:call-template name="userDetails"/>
							<xsl:call-template name="topicList_n"/>
						</form>
					</td>
				</tr>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template match="messages" name="messages">
		<!-- id="divMsg" -->
		<table  summary="">
			<!--  Process the context toolbars -->
			<xsl:for-each select="toolbars">
				<td>
					<xsl:apply-templates select="."/>
				</td>
			</xsl:for-each>
			<xsl:if test=". != ''">
				<tr>
					<td>
						<xsl:call-template name="messages_n"/>
					</td>
				</tr>
			</xsl:if>
		</table>
	</xsl:template>
	
	<xsl:template match="toolbars">
		<xsl:call-template name="toolbars_n"/>
	</xsl:template>
	
	<!-- ########################   Add the more actions combo box ##############################-->
	<xsl:template match="moreactions" name="moreactions">

    	<td>
			<select id="moreactions{$formFragmentSuffix}">
			<option><xsl:copy-of select="$moreactions"/></option>
				<xsl:for-each select="../../../TopicList/MainTopic/ts">
				<!-- Exclude status and blank items -->
				<xsl:if test="t/ty='CONTEXT.ENQUIRY' or t/ty='ENQ.LIST' or t/ty='ENQ.SEARCH' or t/ty='ACTION'">
					<!-- Create a group of options...-->
						<optgroup label="{@c}">
					<!-- Then loop through each topic -->
					<xsl:for-each select="t">
								<option value="{ty}|{tar}">
								<xsl:value-of select="c"/>
						</option>
					</xsl:for-each>			
					</optgroup>
				</xsl:if>
			</xsl:for-each>
			</select>
		</td>
	
    	<!-- Now the go button -->
		<td>
			<a onclick="javascript:doMoreActions()" href="javascript:void(0)" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" title="Go">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'iconLink'"/>
					</xsl:call-template>
				</xsl:attribute>
			<img src="../plaf/images/{$skin}/menu/go.gif" alt="Go" title="Go"/></a>
	    </td>
	</xsl:template>

               <!-- ############################ ARC toolbar Section #####################################-->

                             <xsl:template match="runDefault" name="runDefault">
                                      <tr>
						<td>
							<table summary="">
								<tr>
									<td>
										<!-- The actions and toolbar buttons go here -->
										<table id="goButton" summary="">
										
											<tr>
												<td>
													<!-- Process the actions  -->
													<table summary="">
													
															 <!-- Process the main toolbars -->
														
															<xsl:for-each select="toolbars">
																		<td>
																			
                                          <xsl:apply-templates select="."/>
																		</td>
																</xsl:for-each>
														
													</table>
												</td>
											</tr>
										
								</table>
											
									</td>
								</tr>
							</table>
						</td>
					</tr>

                              </xsl:template>
	
		<!-- ############################ ID Section #####################################-->
	<xsl:template match="idsection" name="idsection">
	<td width="1">
		<!-- Input box and drop down -->
		<table border="0" cellpadding="0" cellspacing="0" summary="">
			<tr>
				<td width="2">
					<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
					<xsl:if test="$stripFrameToolbars = 'false'">	
						<img border="0" width="2">
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
						</img>					
					</xsl:if>
				</td>

				<td nowrap="nowrap">											
					<xsl:if test="autos!='' and not (/responseDetails/userDetails/autosAllowed='N')">
						<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
						<xsl:if test="$stripFrameToolbars = 'false'">
							<img border="0">
								<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/auto.gif</xsl:attribute>
							</img>
						</xsl:if>
						<input type="hidden" id="idautosServer">
							<xsl:attribute name="value"><xsl:value-of select="autosSvr"/></xsl:attribute>
						</input>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="key=''">

							<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
							<xsl:choose>
								<xsl:when test="$stripFrameToolbars = 'false'">	
									<input type="text" name="transactionId" id="transactionId">
											<xsl:attribute name="class">
												<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'idbox'"/>
												</xsl:call-template>
											</xsl:attribute>
										<xsl:attribute name="value"><xsl:value-of select="key"/></xsl:attribute>
										<xsl:attribute name="size"><xsl:value-of select="keyLength"/></xsl:attribute>
										<xsl:attribute name="ondblclick">javascript:help('<xsl:value-of select="fn"/>')</xsl:attribute>
									</input>
								</xsl:when>
								<xsl:otherwise>
									<!-- If 'crud' is stripped away, we still need a tarns ID for client JS logic -->
									<input type="hidden" name="transactionId" id="transactionId"/>
								</xsl:otherwise>
							</xsl:choose>
							
							<!-- store the list of auto launch enquiries to run -->
							<input type="hidden" id="idautolaunch">
								<xsl:attribute name="value"><xsl:value-of select="autos"/></xsl:attribute>
							</input>
						</xsl:when>
						<xsl:otherwise>
								<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
								<xsl:if test="$stripFrameToolbars = 'false' or $showId = 'true'">
									<xsl:variable name="Idlen">
										<xsl:choose>
										<xsl:when test="$maskedId!=''">
											<xsl:value-of select="string-length($maskedId)"/>		
										</xsl:when>	
										<xsl:otherwise>
											<xsl:value-of select="string-length($rawId)"/>
										</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>

								    <span  style="font-size: 14px"  tabIndex="0">
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
												<xsl:with-param name="actualclass" select="'iddisplay'"/>
											</xsl:call-template>
										</xsl:attribute>
										 <xsl:attribute name="style">width:<xsl:value-of select="string-length($Idlen)"/></xsl:attribute>
										<xsl:choose>
											<xsl:when test="$maskedId!=''">
												<xsl:value-of select="$maskedId"/>
											</xsl:when>	
											<xsl:otherwise>
												<xsl:value-of select="$rawId"/>
											</xsl:otherwise>
										</xsl:choose>
										 <xsl:attribute name="title"><xsl:value-of select="$rawId"/></xsl:attribute> 
									</span>
							</xsl:if>								

								<!-- If we DON'T have a masked ID then select the Raw ID -->
								<!-- <xsl:value-of select="key"/> -->
								<input type="hidden" name="transactionId" id="transactionId">
									<xsl:choose>
									<xsl:when test="string-length($rawId)='1'">
										<xsl:choose>
											<xsl:when test="translate($rawId, $alphabets, '')"><xsl:attribute name="value"><xsl:value-of select="$rawId"/></xsl:attribute></xsl:when>
											<xsl:otherwise><xsl:attribute name="value"><xsl:value-of select="concat('.',$rawId)"/></xsl:attribute></xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise><xsl:attribute name="value"><xsl:value-of select="key"/></xsl:attribute></xsl:otherwise>
									</xsl:choose>
								</input>

						</xsl:otherwise>
					</xsl:choose>
				</td>
				<xsl:if test="$stripFrameToolbars = 'false'"> 
					<xsl:if test="keyEnri!=''">
						<td nowrap="nowrap" colspan="20"/>
							<td nowrap="nowrap">
								<p>
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'enrichment'"/>
										</xsl:call-template>
									</xsl:attribute>
									<xsl:value-of select="keyEnri"/>
								</p>
							</td>
					</xsl:if>
				</xsl:if>
				<td width="2">
					<!-- todo: Temporary client side 'crud removal' - will not be required when plumbed into the back end -->
					<xsl:if test="$stripFrameToolbars = 'false'">	
						<img border="0" width="2">
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
						</img>
					</xsl:if>
				</td>
				<xsl:if test="$stripFrameToolbars = 'false' and not (/responseDetails/userDetails/allowdropdowns)">	
					<xsl:choose>
						<xsl:when test="key=''">
							<td width="1">
								<a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
									<xsl:attribute name="onclick">javascript:dropdown('<xsl:value-of select="app"/><xsl:value-of select="version"/>', 'transactionId')</xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
									<xsl:attribute name="tabindex">0</xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
									<img valign="bottom" border="0">
										<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/dropdown.gif</xsl:attribute>
										<!-- select the dropdown node (dd represents a dropdown) -->
										<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
										<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/dd"/></xsl:attribute>
									</img>
								</a>
								
							</td>
							<td>
								<xsl:if test="/responseDetails/userDetails/enquirySelectionBox">
								<a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
								   <xsl:attribute name="tabindex">0</xsl:attribute>
								   <xsl:attribute name="title">Selection Criteria</xsl:attribute>
								   <xsl:attribute name="onclick">javascript:dropdown('<xsl:value-of select="app"/><xsl:value-of select="version"/>', 'transactionId','','','Yes')</xsl:attribute>
								   <xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								   <xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'contract_selection_criteria'"/>
										</xsl:call-template>
									</xsl:attribute>
								   <img valign="bottom" border="0">
								        <xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/tools/select.gif</xsl:attribute>
										<!-- select the dropdown node (dd represents a dropdown) -->
										<xsl:attribute name="alt">Selection Criteria</xsl:attribute>
										<xsl:attribute name="title">Selection Criteria</xsl:attribute>
								   </img>
								</a>
								</xsl:if>
							</td>
							<xsl:if test="allowNewDeal='yes'">
								<td width="1">
									<a onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
										<xsl:attribute name="onclick">javascript:doNewDeal()</xsl:attribute>
										<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
										<xsl:attribute name="tabindex">0</xsl:attribute>
										<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/newDeal"/></xsl:attribute>
										<img valign="bottom" border="0">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/menu/new.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/newDeal"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/newDeal"/></xsl:attribute>
										</img>
									</a>
								</td>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:if>
			</tr>
		</table>
	</td>
	</xsl:template>
</xsl:stylesheet>