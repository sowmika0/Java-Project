<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- NOTE: below, ty represents type,  n represents the node 'name'-->

	<xsl:template match="tool" name="tool_n">
	   	<xsl:param name="toolposition" />
	   	<xsl:param name="Comptoolbar"/>
		<!-- Use a variable for the href / onclick link, which is different for each type of tool -->
		<xsl:variable name="hlink">
		<xsl:variable name="jsitem"><xsl:value-of select="item"/></xsl:variable>
		<xsl:variable name="enquiryFind">
				<xsl:choose>
						<xsl:when test="$jsitem ='doEnquiry()'">
						   toggleStateButton();<xsl:value-of select="$jsitem"/>
						</xsl:when>
						<xsl:otherwise>
						<xsl:value-of select="$jsitem"/>
						</xsl:otherwise>
				</xsl:choose>
			
			</xsl:variable>
			<xsl:choose>
				<!-- ty represents type -->
				<xsl:when test="ty='JAVASCRIPT'">javascript:<xsl:value-of select="$enquiryFind"/></xsl:when>
				<!-- ty='PAGERANGE' is the same as JAVASCRIPT, except several links are created using {item} with the range index as the function parameter -->
				<xsl:when test="ty='REQUEST'">../jsps/genrequest.jsp?&amp;routineName=<xsl:value-of select="routine"/>&amp;routineArgs=<xsl:value-of select="args"/>&amp;skin=<xsl:copy-of select="$skin"/>&amp;compId=<xsl:copy-of select="$atoolcompid"/>&amp;compScreen=<xsl:copy-of select="$compScreen"/>&amp;contextRoot=<xsl:copy-of select="$contextRoot"/>&amp;user=<xsl:copy-of select="$user"/></xsl:when>
				<xsl:when test="ty='ACTION'">javascript:doToolbar('<xsl:value-of select="item"/>', '<xsl:value-of select="funct"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>')</xsl:when>
				<xsl:when test="ty='DO.DEAL'">
					<xsl:choose>
						<xsl:when test="item!=''">javascript:doToolbardocommand('<xsl:value-of select="item"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>', '<xsl:value-of select="ty"/>')</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="targetselect">1</xsl:variable>javascript:doToolbardocommand('<xsl:value-of select="tar"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>', '<xsl:value-of select="ty"/>')
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="ty='BACK'">javascript:doDealApplication('<xsl:value-of select="item"/>')</xsl:when>
				<xsl:when test="ty='DO.API'">javascript:doToolbardocommand(API '<xsl:value-of select="item"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>', '<xsl:value-of select="ty"/>')</xsl:when>
				<xsl:when test="ty='HYPERTEXT'">javascript:doToolbardocommand('<xsl:value-of select="item"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>', '<xsl:value-of select="ty"/>')</xsl:when>
				<xsl:when test="ty='COMMIT'">
				<xsl:choose>
						<xsl:when test="$isArc='true' and cap='Confirm'">
							javascript:commitOverrides()						
						</xsl:when>
						<xsl:otherwise>
							javascript:doToolbar('<xsl:value-of select="item"/>', '<xsl:value-of select="funct"/>', '<xsl:value-of select="prompt"/>', '<xsl:value-of select="prmText"/>')
						</xsl:otherwise>
				</xsl:choose>
				</xsl:when> 
				<xsl:when test="ty='FIELD.BUTTON'">                                                                                                                                                                                                                                                                                                                                                                                            
					<xsl:choose>                                                                                                                                                                                                                                                                                                                                                                                                        
						<xsl:when test="../ty='viewed'">                                                                                                                                                                                                                                                                                                                                                                                 
							javascript:fieldButton('<xsl:value-of select="item"/>','<xsl:value-of select="../v" />')                                                                                                                                                                                                                                                                                                                 
						</xsl:when>                                                                                                                                                                                                                                                                                                                                                                                                      
						<xsl:otherwise>                                                                                                                                                                                                                                                                                                                                                                                                  
							javascript:fieldButton('<xsl:value-of select="item"/>','fieldName:<xsl:value-of select="../fn" /><xsl:value-of select="../in" />')                                                                                                                                                                                                                                                                       
						</xsl:otherwise>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="imagehome">../plaf/images/<xsl:copy-of select="$skin"/></xsl:variable>

		<xsl:variable name="toolId">
			<xsl:choose>
				<xsl:when test="default='YES'">defaultButton<xsl:value-of select="$formFragmentSuffix"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="n"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Main table to hold _each_ tool -->
	
		    	<xsl:choose>

		    		<!--when we have a combo box to display-->
				    <xsl:when test="ty='COMBO'">
				    	
				    		<select size="1" name="{n}" id="COMBO_{n}">
				    			<xsl:for-each select="options/o">
									<option value="{attrib}">
										<xsl:value-of select="n"/>
									</option>
								</xsl:for-each>
							</select>
						
					
							<xsl:variable name="routine">
							<xsl:value-of select="routine"/>
							</xsl:variable>
						
					       <td>
						   <a onclick="javascript:Identifycombovalue('COMBO_{n}','{$routine}')" href="javascript:void(0)">
								<img src="{$imagehome}/menu/{image}"/>
						   </a>
						   </td>
					
				    </xsl:when>

		    		<!-- when we have a range of numeric links to display-->
				    <xsl:when test="ty='PAGERANGE'">
						<xsl:call-template name="num_range">
							<xsl:with-param name="idx" select="min"/>
							<xsl:with-param name="rangeMax" select="max"/>
							<xsl:with-param name="rangeCurrent" select="cur"/>
						</xsl:call-template>
				    </xsl:when>

				    <!--when we are processing buttons, etc.-->
				    <xsl:otherwise>
						<!-- Check if it is the default button-->
						<!-- Here we have to use <input type="image"></input> to act as the submit button as we have more than one text box on the form-->
						<!-- Also note that we must create this with the id of defaultButton and attribute with defaultFunction-->
						<xsl:if test="default='YES'">
						
								<input type="image" id="{$toolId}" defaultFunction="{funct}" state="ON" tabindex="-1" src="{$imagehome}/tools/blank.gif"/>
						
						</xsl:if>
                        <!-- always use the image if it has been defined -->
							<xsl:choose>  <!--  Text or Image button -->
								<xsl:when test="image !=''">  <!-- Image-based tool buttons -->
								
										<!-- put the correct image to decide if we are enabled or not. Default to enabled-->
										<xsl:choose>
											<xsl:when test="enabled!=''">    <!-- Do the inactive image button -->
												<xsl:if test="image !=''">
													<img src="{$imagehome}/tools/{image}_dis.gif" alt="{tip}" title="{tip}">
														<xsl:if test="n!=''">
															<xsl:attribute name="id"><xsl:value-of select="n"/></xsl:attribute>
														</xsl:if>
														<xsl:if test="n='toggle'">
															<xsl:attribute name="toggleState">HIDE</xsl:attribute>
														</xsl:if>
													</img>
												</xsl:if>
											</xsl:when>
											<xsl:otherwise>
												
												<!-- only add the image if there is the image node present!-->
												<xsl:if test="image !=''">
												
													<xsl:variable name="image"><xsl:value-of select="image"/></xsl:variable>
													<xsl:variable name="check" select="substring-after($image,'.')"/>
													
													<xsl:variable name="ImgSrc">
														<xsl:choose>
															<xsl:when test="$check!=''">
																<xsl:value-of select="image"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="image"/>.gif
															</xsl:otherwise>
														</xsl:choose>
													</xsl:variable>  
													
													<a accesskey="{sKey}" title="{tip}" tabindex="0" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
														<!-- onclick removed for browser tools of type 'REQUEST' since doGenRequest is based on fragments -->
													<xsl:choose>
														<xsl:when test="contains($hlink,'javascript:') and not(contains($hlink,'javascript:action'))">
															<xsl:attribute name="onclick"><xsl:value-of select="$hlink"/></xsl:attribute>
															<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="href"><xsl:value-of select="$hlink"/></xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>	
														<!--get the target-->
														<xsl:choose>
															<xsl:when test="ty='DO.DEAL'">
																<xsl:if test="targetselect=1">
																	<xsl:if test="tar!=''">
																		<xsl:attribute name="target"><xsl:value-of select="tar"/></xsl:attribute>
																	</xsl:if>
																</xsl:if>
															</xsl:when>
															<xsl:otherwise>
																<xsl:if test="tar!=''">
																	<xsl:attribute name="target"><xsl:value-of select="tar"/></xsl:attribute>
																</xsl:if>
															</xsl:otherwise>
														</xsl:choose>
														<img src="{$imagehome}/tools/{$ImgSrc}" alt="{tip}" title="{tip}" >
															<!-- n represents the node 'name'-->
															<xsl:if test="n!=''">
																<xsl:attribute name="id"><xsl:value-of select="n"/></xsl:attribute>
															</xsl:if>
															<xsl:if test="n='toggle'">
																<xsl:attribute name="toggleState">HIDE</xsl:attribute>
															</xsl:if>
														</img>
													</a>
												</xsl:if>
											</xsl:otherwise>
										</xsl:choose>  <!-- enabled / disabled image -->
								
								</xsl:when>
								
								<xsl:otherwise>  <!-- Text-based tool buttons -->
  
									<xsl:choose>
										<xsl:when test="enabled!=''">
											
												<span><xsl:value-of select="cap"/></span>
										
										</xsl:when>
										<xsl:otherwise>
											
												 <a alt="{tip}" title="{tip}" accesskey="{sKey}" class="textbtn" style="white-space:nowrap" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);"> 
												 
												 <!-- Based on display type TEXT.STYLE -->
											
											     <!-- If STYLE field has value then process the below -->
												 	<xsl:if test="toolStyle!=''">
												 		<xsl:attribute name="class">textbtn <xsl:value-of select="toolStyle"/></xsl:attribute>
												 	</xsl:if>
												 	
												 <!-- If the toolbar present in a composite screen then -->
												 	<xsl:if test="$Comptoolbar='true' and $toolposition=1">
												 		<xsl:attribute name="class">textbtn textbtnOn</xsl:attribute>
												 	</xsl:if>
												 	
												 <!-- If the toolbar present in a composite screen and style field has value then -->
												 	<xsl:if test="$Comptoolbar='true' and toolStyle !='' and $toolposition=1">
												 		<xsl:attribute name="class">textbtn <xsl:value-of select="toolStyle"/> textbtnOn</xsl:attribute>
												 	</xsl:if>
												 	
											     	<!-- Text based tools based on types -->
													<xsl:choose>
												    	<xsl:when test="(ty='REQUEST') or (ty='JAVASCRIPT') or (ty='COMMIT')">
															<xsl:choose>
																<xsl:when test="contains($hlink,'javascript:') and not(contains($hlink,'javascript:action'))">
																	<xsl:attribute name="onclick">doHighlight(this);<xsl:value-of select="$hlink"/></xsl:attribute>
																	<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:attribute name="href"><xsl:value-of select="$hlink"/></xsl:attribute>
																	<xsl:attribute name="onclick">doHighlight(this);</xsl:attribute>
																</xsl:otherwise>
															</xsl:choose>	
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="onclick"><xsl:value-of select="$hlink"/></xsl:attribute>
															<xsl:attribute name="onkeypress"><xsl:value-of select="$hlink"/></xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>	
												    <xsl:if test="tar!=''">
														<xsl:attribute name="target"><xsl:value-of select="tar"/></xsl:attribute>
													</xsl:if>
													<xsl:value-of select="cap"/>
												  </a> 
										
										</xsl:otherwise>
									</xsl:choose>
 
								</xsl:otherwise>
								
							</xsl:choose>  <!--  Text or Image button -->
                       
					</xsl:otherwise>
				</xsl:choose>
	
	</xsl:template>
	
	<!-- NEW recursively-called slave transform for numeric ranges of links e.g. page range tool in enq response
		- creates a bunch of <a> elements from min to max, optionally disabling a 'current' link to show the position in the range -->
	<xsl:template name="num_range">
		<xsl:param name="idx"/>
		<xsl:param name="rangeMax"/>
		<xsl:param name="rangeCurrent"/>

		<xsl:if test="$idx &lt; ($rangeMax + 1)">
		  
				<xsl:choose>
					<xsl:when test="$idx = $rangeCurrent">
						<span><xsl:value-of select="$idx"/></span>
					</xsl:when>
					<xsl:otherwise>
						<a href="javascript:{item}('{$idx}')"><xsl:value-of select="$idx"/></a>
					</xsl:otherwise>
				</xsl:choose>
				<span>&#160;</span>

			<xsl:call-template name="num_range">
				<xsl:with-param name="idx" select="$idx + 1"/>
				<xsl:with-param name="rangeMax" select="$rangeMax"/>
				<xsl:with-param name="rangeCurrent" select="$rangeCurrent"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- NEW template for dropdown toolbar -->
	<xsl:template name="dropdowntool">
		<xsl:choose>
		    <xsl:when test="ty='JAVASCRIPT'">
				<li>
					<xsl:choose>
						<xsl:when test="enabled!=''">
							<xsl:attribute name="class">popup-tool-dis</xsl:attribute>
							<xsl:value-of select="cap"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="class">popup-tool</xsl:attribute>
							<a href="javascript:{item}" alt="{tip}" title="{tip}" accesskey="{sKey}" tabindex="0" onfocus ="popupRowfocus(event);focusonKey('inline',event);" onblur = "popupRowblur(event);hideTooltip(event);">
								<xsl:if test="tar!=''">
									<xsl:attribute name="target"><xsl:value-of select="tar"/></xsl:attribute>
								</xsl:if>
								<xsl:value-of select="cap"/>
							</a> 
						</xsl:otherwise>
					</xsl:choose>
				</li>
		    </xsl:when>
		    <xsl:otherwise>
		    	<xsl:message>WARNING: Only tools of type JAVASCRIPT supported in dropdown toolbar</xsl:message>
		    </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>

</xsl:stylesheet>
