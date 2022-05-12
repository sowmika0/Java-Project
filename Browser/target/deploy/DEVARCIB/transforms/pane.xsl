<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- import all of the child items here -->
    <xsl:import href="ARC/T24_constants.xsl"/>
	<xsl:import href="message.xsl"/>
	<xsl:import href="banner.xsl"/>
	<xsl:import href="tabs.xsl"/>
	<xsl:import href="menu/menu.xsl"/>
	<xsl:import href="enquiry/enqsel.xsl"/>
	<xsl:import href="enquiry/enqresponse.xsl"/>
    <xsl:import href="enquiry/dropdownEnquiry.xsl"/>
	<xsl:import href="contracts/contract.xsl"/>
	<xsl:import href="toolbars/toolbars.xsl"/>
	<xsl:import href="userDetails.xsl"/>
	<xsl:import href="escaper.xsl"/>		<!-- To escape apostrophe's used in javascript parameters -->

	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	
	<!-- Extract the dropfield of an enquiry if there is one to figure out if this is a dropdown, otherwise its a normal enquiry -->
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
	
	<!-- Check if this response should be rendered as a popup dropdown. If so then use the relevant transform. Otherwise continue normally. -->
	<xsl:variable name="popupDropDown">
        <xsl:choose>
			<xsl:when test="responseDetails/userDetails/popupDropDown">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>	
	</xsl:variable>
	
	<xsl:variable name="showTabs">
		<xsl:value-of select="/responseDetails/webDetails/WS_showTabs"/>
	</xsl:variable>
	
	<xsl:variable name="pageDropDown">
		<xsl:value-of select="/responseDetails/webDetails/WS_pageDropDown"/>
	</xsl:variable>
	<!-- The actual pane and content creation -->
	<xsl:template match="pane" name="pane_n">
		<!-- handle the positioning of the panes here-->
		<xsl:choose> 
			<xsl:when test="not(groupTab)">
				<div>
					<xsl:variable name="id">pane_<xsl:value-of select="contract/app" /><xsl:value-of select="contract/version" /><xsl:value-of select="contract/key" /><xsl:value-of select="$formFragmentSuffix"/></xsl:variable>
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
					<xsl:attribute name="name"><xsl:value-of select="$id"/></xsl:attribute>
					<!-- Apply this form html. -->
			<!-- If this response is a poageDropDown then do not transform the selection criteria -->
			<xsl:if test="(selSection/selDets/enqsel/attribs/aboveData = 'YES') and ($pageDropDown != 'true')">
				<xsl:for-each select="selSection">
					<xsl:apply-templates select="."/>
				</xsl:for-each>
			</xsl:if>
			<xsl:for-each select="dataSection">
				<xsl:apply-templates select="enqResponse"/>
			</xsl:for-each>
			<xsl:for-each select="message">
				<div class="display_box">
 					<xsl:apply-templates select="."/>
 				</div>
			</xsl:for-each>
			<xsl:for-each select="banner">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:for-each select="tabs">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:for-each select="contract">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:if test="not (selSection/selDets/enqsel/attribs/aboveData = 'YES') and ($pageDropDown != 'true')">
				<xsl:for-each select="selSection">
					<xsl:apply-templates select="."/>
				</xsl:for-each>
			</xsl:if>
 			<xsl:for-each select="menu">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
				</div>      
			</xsl:when>
			<xsl:otherwise>         
				<div id="tabFrame" class="tabbed_pane">
					<xsl:variable name="tabproperty"><xsl:value-of select="groupTab"/></xsl:variable>
					<xsl:attribute name="id">tabFrame_<xsl:value-of select="$tabproperty"/></xsl:attribute>                                                
					<div id="tabMenuDiv" class="tabMenu">
						<xsl:attribute name="id">tabMenuDiv_<xsl:value-of select="groupTab"/></xsl:attribute>
						<!-- Needed to hide the tabs div if there is only one tab -->
						<xsl:if test="$onlyCurrentTabs='true' and $showTabs!='true'">
							<xsl:attribute name="class">tabMenu-hide</xsl:attribute>
						</xsl:if>
						
						
						
						<xsl:for-each select="groupPane">
							<xsl:variable name="application"><xsl:value-of select="contract/app"/></xsl:variable>
							<xsl:variable name="propertyname"><xsl:value-of select="contract/app" /><xsl:value-of select="contract/version" /><xsl:value-of select="contract/key" /><xsl:if test="not(contains($application,'AA.ARR'))"><xsl:value-of select="$formFragmentSuffix"/></xsl:if></xsl:variable>
							<xsl:choose>     
								<xsl:when test="position()=1">
									<span class="active-tab" >
										<xsl:attribute name="id">tabMenu<xsl:value-of select="position()" /></xsl:attribute>
										<xsl:attribute name="onmousedown">setCurrentForm('<xsl:value-of select="$propertyname"/>')</xsl:attribute>
										<a class="tab-head">
											<xsl:attribute name="href">javascript:raisePanel(<xsl:value-of select="position()"/>,"<xsl:value-of select="$tabproperty"/>")</xsl:attribute>
											
											<!-- Current -->
											<xsl:choose>    
												<xsl:when test="(contract/tabEnri != '')">
													<xsl:value-of select="contract/tabEnri"/>
												</xsl:when>
												<xsl:otherwise>
													Period <xsl:value-of select="position()"/>
												</xsl:otherwise>
											</xsl:choose>
											
										</a>
										<xsl:if test=" (contract/expandableTab = 'YES')"> 
										<a onclick="javascript:relativeCalendarDisplay(event)">

											<xsl:attribute name="id">tabAddMenu<xsl:value-of select="position()" /></xsl:attribute>
											<img relativecalendardropfieldname="fieldName:ID.COMP.6" id ="tabAdd" title="Expand Tab" alt="Expand Tab" src="../plaf/images/default/deal/addTab.gif"  >
											</img>
										</a>
										</xsl:if>
									</span>
								</xsl:when>
								<xsl:otherwise>		
									<span class="nonactive-tab">
										<xsl:attribute name="id">tabMenu<xsl:value-of select="position()" /></xsl:attribute>
										<xsl:attribute name="onmousedown">setCurrentForm('<xsl:value-of select="$propertyname"/>')</xsl:attribute>
										<xsl:attribute name="propertyname"><xsl:value-of select="$propertyname"/></xsl:attribute>
										<xsl:if test="(contract/deleteGroupTab = 'YES')">
											<xsl:attribute name="style">display: none;</xsl:attribute>
										</xsl:if>
										<a class="tab-head">
											<xsl:attribute name="href">javascript:raisePanel(<xsl:value-of select="position()"/>,"<xsl:value-of select="$tabproperty"/>")</xsl:attribute>
											<xsl:choose>    
												<xsl:when test="(contract/tabEnri != '')">
													<xsl:value-of select="contract/tabEnri"/>
												</xsl:when>
												<xsl:otherwise>
													Period <xsl:value-of select="position()"/>
												</xsl:otherwise>
											</xsl:choose>
										</a>
										<xsl:if test=" (contract/expandableTab = 'YES')"> 
										<a onclick="javascript:relativeCalendarDisplay(event)">
											<img relativecalendardropfieldname="fieldName:ID.COMP.6" id ="tabAdd" title="Expand Tab" alt="Expand Tab" src="../plaf/images/default/deal/addTab.gif"  >
											</img>
										</a>
										<a>				
											<xsl:attribute name="href">javascript:delTabMenu(<xsl:value-of select="position()"/>,"<xsl:value-of select="$tabproperty"/>","<xsl:value-of select="$propertyname"/>")</xsl:attribute>
											<img id ="tabDel" title="Delete Tab" alt="Delete Tab" src="../plaf/images/default/deal/delTab.gif"  >
											</img>
										</a>
										</xsl:if>
									</span>
									
								</xsl:otherwise>
							</xsl:choose>                                           
						</xsl:for-each>                                 
					</div>
					<div id="newPropertyDate" >
						<xsl:attribute name="id">newPropertyDate_<xsl:value-of select="$tabproperty"/></xsl:attribute>
						<form id="newPropertyDateForm" onmousedown="setCurrentForm('newPropertyDateForm')">
							<xsl:attribute name="id">newPropertyDateForm_<xsl:value-of select="$tabproperty"/></xsl:attribute>
							<xsl:attribute name="onmousedown">setCurrentForm('newPropertyDateForm_<xsl:value-of select="$tabproperty"/>')</xsl:attribute>
							<input type="hidden" name="application" id="application">
								<xsl:attribute name="value"><xsl:value-of select="groupPane/contract/app" /></xsl:attribute>
							</input>
							<input type="hidden" name="enqname" id="enqname">
								<xsl:attribute name="value"></xsl:attribute>
							</input>
							<input type="hidden" name="routineArgs" value=""/>
							<input type="text" name="fieldName:ID.COMP.6" id="fieldName:ID.COMP.6" style="display: none;" value="" oldvalue=""/>
							<input type="hidden" name="savedForm" value=""/>
						</form>
					</div>
					
					<xsl:for-each select="groupPane">
						<div>
							<xsl:choose>     
								<xsl:when test="position()=1">
									<xsl:attribute name="class">active-tabPane</xsl:attribute> 
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="class">nonactive-tabPane</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:variable name="tabPosition"><xsl:value-of select="position()"/></xsl:variable>
							<xsl:attribute name="id">tabPane<xsl:value-of select="position()" /><xsl:value-of select="$tabproperty"/></xsl:attribute>
							<div>
								<xsl:variable name="application"><xsl:value-of select="contract/app"/></xsl:variable>
								<xsl:variable name="id">pane_<xsl:value-of select="contract/app" /><xsl:value-of select="contract/version" /><xsl:value-of select="contract/key"/><xsl:if test="not(contains($application,'AA.ARR'))"><xsl:value-of select="$formFragmentSuffix"/></xsl:if></xsl:variable>
								<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
								<xsl:attribute name="name"><xsl:value-of select="$id"/></xsl:attribute>
								<xsl:attribute name="tabPosition"><xsl:value-of select="$tabPosition"/></xsl:attribute>
								<!-- Apply this form html. -->
								<!-- If this response is a poageDropDown then do not transform the selection criteria -->
								<xsl:if test="(selSection/selDets/enqsel/attribs/aboveData = 'YES') and ($pageDropDown != 'true')">
									<xsl:for-each select="selSection">
										<xsl:apply-templates select="."/>
									</xsl:for-each>
								</xsl:if>
								<xsl:for-each select="dataSection">
									<xsl:apply-templates select="enqResponse"/>
								</xsl:for-each>
								<xsl:for-each select="message">
									<div class="display_box">
										<xsl:apply-templates select="."/>
									</div>
								</xsl:for-each>
								<xsl:for-each select="banner">
									<xsl:apply-templates select="."/>
								</xsl:for-each>					
								<xsl:for-each select="tabs">
									<xsl:apply-templates select="."/>
								</xsl:for-each>
								<xsl:for-each select="contract">
									<xsl:apply-templates select="."/>
								</xsl:for-each>
								<xsl:if test="not (selSection/selDets/enqsel/attribs/aboveData = 'YES') and ($pageDropDown != 'true')">
									<xsl:for-each select="selSection">
										<xsl:apply-templates select="."/>
									</xsl:for-each>
								</xsl:if>
								<xsl:for-each select="menu">
									<xsl:apply-templates select="."/>
								</xsl:for-each>
							</div>      
						</div>
					</xsl:for-each>
				</div>
			</xsl:otherwise>
		</xsl:choose>                      
	</xsl:template>

	<!-- Templates that are called go here...-->
	<xsl:template match="message">
		<xsl:call-template name="message_n"/>
	</xsl:template>
	<xsl:template match="banner">
		<xsl:call-template name="banner_n"/>
	</xsl:template>
	<xsl:template match="tabs">
		<xsl:call-template name="tabs_n"/>
	</xsl:template>
	<xsl:template match="contract">
		<xsl:call-template name="contract_n"/>
	</xsl:template>
	<xsl:template match="menu">
		<xsl:choose>
			<xsl:when test="$menuStyle = 'TABBED'">
				<xsl:call-template name="tabs_menu"/>
			</xsl:when>
			<xsl:when test="$menuStyle = 'POPDOWN'">
				<xsl:call-template name="pop_menu"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="menu_n"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="selDets">
		<xsl:call-template name="selDets_n"/>
	</xsl:template>
	<xsl:template match="enqResponse">
		<xsl:choose>
			<xsl:when test="$popupDropDown = 'true' and  $dropfield != '' and $pageDropDown = 'true'">
				<xsl:call-template name="dropDownEnq_n"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="enqResponse_n"/>			
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>