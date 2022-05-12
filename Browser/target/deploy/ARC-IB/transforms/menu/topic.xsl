<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- match on topic -->
	<xsl:template match="t">
		<xsl:choose>
			<xsl:when test="ty='DEALFIND'"/><!-- Do nothing if type is equal to DEALFIND -->
			<xsl:when test="tar='QUICK.GUIDE'">
				<!--  Don't display the quick guide any more as no longer required and a security risk -->
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:choose>
						<!-- Command Line -->
						<!-- Quick Search -->
						<!-- check to see if the type (ty) is equal to SEARCH -->
						<xsl:when test="ty='SEARCH'">
						    <!--place the target (tar) into the javascript call-->
							<xsl:attribute name="onclick">javascript:doSearch('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>							
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/search.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!--Context Enquiry -->
						<!-- check to see if the type (ty) is equal to CONTEXT.ENQUIRY -->
						<xsl:when test="ty='CONTEXT.ENQUIRY'">
						    <!--place the target into the javascript call-->
							<xsl:attribute name="href">javascript:doContextEnquiry('<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:attribute name="onclick">javascript:menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/enquiry.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Enquiry- Display enquiry selection -->
						<xsl:when test="ty='ENQUIRY'">
						<xsl:variable name="EscapeChars">
                        <xsl:call-template name="escape-apos">
                               <xsl:with-param name="string" select="c"/>
                        </xsl:call-template>
                        </xsl:variable>
						<!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doenq('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>');processMenuHeaderText('<xsl:value-of select="$EscapeChars"/>')</xsl:attribute>
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/enquiry.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Enquiry Launch- Display enquiry result -->
						<xsl:when test="ty='ENQ.LAUNCH'">
						    <!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doenqRun('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/runenquiry.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Enquiry List (of Live, Unauthorised, etc files) - Display enquiry result - in a new window -->
						<xsl:when test="ty='ENQ.LIST'">
						    <!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doenqList('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>							
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/runenquiry.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Enquiry Search (of Live, Unauthorised, etc files) - Display enquiry selection - in a new window -->
						<xsl:when test="ty='ENQ.SEARCH'">
						    <!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doenqSearch('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/runenquiry.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Contract -->
						<xsl:when test="ty='CONTRACT'">
						<xsl:variable name="EscapeChars">
                        <xsl:call-template name="escape-apos">
                               <xsl:with-param name="string" select="c"/>
                        </xsl:call-template>
                        </xsl:variable>
							<!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>');processMenuHeaderText('<xsl:value-of select="$EscapeChars"/>')</xsl:attribute>							
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/contract.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Change Company -->
						<xsl:when test="ty='CHANGE.COMPANY'">
							<!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doloadCompany('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>							
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/company.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Switch Roles-->
						<xsl:when test="ty='CHANGE.ROLE'">
							<!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:doSwitchRole('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>							
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/role.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>


						<!-- New Contract -->
						<xsl:when test="ty='NEW'">
						<xsl:variable name="EscapeChars">
                        <xsl:call-template name="escape-apos">
                               <xsl:with-param name="string" select="c"/>
                        </xsl:call-template>
                        </xsl:variable>
							<!--place the target into the javascript call-->
							<xsl:attribute name="onclick">javascript:docommand('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>');processMenuHeaderText('<xsl:value-of select="$EscapeChars"/>')</xsl:attribute>
							<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/menu/new.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Helptext view (new XML helptext item) -->
						<xsl:when test="ty='HELP'">
							<xsl:attribute name="href">javascript:help('<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:attribute name="onclick">javascript:menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/help/help.gif</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- Helptext field - make an anchor that jumps to the relevant section in the helptext -->
						<xsl:when test="ty='HELP.FIELD' or field!=''">
							<xsl:attribute name="href">#<xsl:value-of select="field"/></xsl:attribute>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/help/help.gif</xsl:with-param>
							</xsl:call-template>
							<!--  Use the field caption in preference to the field name -->
							<xsl:choose>
								<xsl:when test="cap!=''">
									<xsl:value-of select="cap"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="field"/>
								</xsl:otherwise>
							</xsl:choose>							
						</xsl:when>
						<xsl:when test="ty='URL'">
							<!-- for portal items, allow a base destination to be defined-->
							<xsl:if test="/t24help/basedest!=''">
								<xsl:attribute name="target"><xsl:value-of select="/t24help/basedest"/></xsl:attribute>
							</xsl:if>
							<!-- Confusingly, the target of the menu item is used as the href attribute - i.e. what to run
							While the dest part of the menu item sets the target - i.e. which frame to target--> 
							<xsl:choose>
								<xsl:when test="contains(tar,'xml')">
									<xsl:attribute name="href">../servlet/PortalHelpServlet?FileId=<xsl:value-of select="tar"/></xsl:attribute>					
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="href">../portal/<xsl:value-of select="tar"/></xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:if test="dest!=''">
								<xsl:attribute name="target"><xsl:value-of select="dest"/></xsl:attribute>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="image">
									<xsl:call-template name="generateImage">
										<xsl:with-param name="imgSource">/menu/<xsl:value-of select="image"></xsl:value-of></xsl:with-param>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="generateImage">
										<xsl:with-param name="imgSource">/menu/blank.gif</xsl:with-param>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
			
						<!-- Anything else gets put here - no need to add extra when clauses....Action, Edit and Profile items... -->
						<xsl:otherwise>
							<!-- Use the image defined, the image for the type, or if no image just show the default icon -->
							<xsl:variable name="myImage">
								<xsl:choose>
									<xsl:when test="./image!=''">
										<xsl:value-of select="image"/>
									</xsl:when>
									<!-- ty represents type -->
									<xsl:when test="ty='PROFILE'">menu/tool.gif</xsl:when>
									<xsl:when test="ty='WEB'">menu/web.gif</xsl:when>
									<xsl:otherwise>menu/blank.gif</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<!-- ty represents type -->
								<xsl:when test="ty='ACTION'">
								    <!--place the target into the javascript call-->
									<xsl:attribute name="onclick">javascript:doDeal('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								</xsl:when>
								<xsl:when test="ty='EDIT'">
									<!--place the target into the javascript call-->
									<xsl:attribute name="onclick">javascript:doEdit('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<!--place the target into the javascript call-->
									<xsl:attribute name="onclick">javascript:doutil('<xsl:value-of select="tar"/>');menu_history('<xsl:value-of select="ty"/>','<xsl:value-of select="tar"/>')</xsl:attribute>
									<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:call-template name="generateImage">
								<xsl:with-param name="imgSource">/<xsl:copy-of select="$myImage"/></xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="c"/><!-- Finally get the caption, the text displayed in the link. -->
					<xsl:text> </xsl:text><!-- Adds a trailing space to defeat the 'IE whitespace bug'. This can be removed when browser uses a strict doctype -->
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="generateImage">
		<xsl:param name="imgSource"/>
		<xsl:if test="$menuStyle = 'DEFAULT'">
			<img border="0">
				<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/><xsl:value-of select="$imgSource"/></xsl:attribute>
			</img>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
