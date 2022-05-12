<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- All import go here -->
    
    <xsl:variable name="onlyCurrentTabs">
		<xsl:choose>
	    	<xsl:when test="count(/responseDetails/window/panes/pane[*]/groupPane) = count(/responseDetails/window/panes/pane[*]/groupTab)">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
	</xsl:variable>
    
	<xsl:template name="multiPane">
		
	   		<div id="MainApplication">
					
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'topDiv'"/>
							</xsl:call-template>
						</xsl:attribute>
					
		    		<xsl:apply-templates select="responseDetails/window/panes/pane[1]"/>
			</div>
			<div id="topPart">
				<xsl:for-each select="responseDetails/window/panes/pane">
			          <xsl:choose>     
			          	  <xsl:when test="position()=last()"> 
			          	  		<div  nowrap="nowrap">
									
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'bookMarkDiv'"/>
										</xsl:call-template>
									</xsl:attribute>
									
			          	  			&#xA0;&#xA0;
			          	  			<a href="#legend_{title}"><xsl:value-of select="title"/></a>
			          	  		</div>
			          	  </xsl:when> 	
				          <xsl:when test="position()>1">
				          		<div  nowrap="nowrap">
									
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'bookMarkDiv'"/>
										</xsl:call-template>
									</xsl:attribute>
									
				          			&#xA0;&#xA0;
				          			<a href="#legend_{title}"><xsl:value-of select="title"/></a>
				          			&#xA0;&#xA0;|
				          		</div> 
				          </xsl:when>
			          </xsl:choose>				
				</xsl:for-each>
				<br/>
				<br/>
			</div>
			<div onscroll="javascript:testmouse(event);" id="BottomPart">
				
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'display_box_no_border'"/>
					</xsl:call-template>
				</xsl:attribute>
				
			    <!-- Add each iframe to this td. -->
				<xsl:for-each select="responseDetails/window/panes/pane">
			          <xsl:choose>     
			            <xsl:when test="position()>1">
			            	<xsl:variable name="id">pane_<xsl:value-of select="contract/app" /><xsl:value-of select="contract/version" /><xsl:value-of select="contract/key" /><xsl:value-of select="$formFragmentSuffix"/></xsl:variable>
					<xsl:variable name="propertyname"><xsl:value-of select="contract/app" /><xsl:value-of select="contract/version" /><xsl:value-of select="contract/key" /><xsl:value-of select="$formFragmentSuffix"/></xsl:variable>
					<fieldset>                                    
						<a name="legend_{title}"/>                    
						<LEGEND name="legend_{title}">
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'legend-title'"/>
								</xsl:call-template>
							</xsl:attribute>
							<xsl:value-of select="title"/>
						</LEGEND>  
				              	<!-- Apply this form html. -->
								<xsl:apply-templates select="."/>
					</fieldset>		
					<div style="height:10px;"/>				
			            </xsl:when>
			          </xsl:choose>
		          </xsl:for-each>     
			</div>
		
	</xsl:template>
</xsl:stylesheet>
