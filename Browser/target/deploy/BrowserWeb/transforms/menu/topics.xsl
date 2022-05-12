<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:import href="topic.xsl"/>
	<!-- ts is short for the word 'topics' -->
	<xsl:template match="ts" name="topics_n">
	<!--Declaration of the variable isConfirm is moved to window.xsl-->	
		<xsl:choose>
		<!-- Errors -->
			<xsl:when test="id='errors'">
				<table id="errors" name="errors" >
					<tbody>
						<xsl:for-each select="errors/error">
							<tr>
								<td>
									<xsl:choose>
										<xsl:when test="ofsError">
											<img alt="error" tabindex="0" id="errorImg"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/ofserror.gif</xsl:attribute></img>										
										</xsl:when>
										<xsl:otherwise>
											<img alt="error" tabindex="0" id="errorImg"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/error.gif</xsl:attribute></img>
										</xsl:otherwise>
									</xsl:choose>
								</td>
								<td class="errors" nowrap="nowrap">
									<xsl:choose>
										<xsl:when test="errorhref!=''">
											<a tabindex="0">
												<xsl:attribute name="onclick"><xsl:value-of select="errorhref"/></xsl:attribute>
												<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
												<span>
													<xsl:choose>
														<xsl:when test="errorApp">
															<xsl:value-of select="errorApp"/>&#xA0;-&#xA0;<xsl:value-of select="fn"/>
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="fn"/>
														</xsl:otherwise>
													</xsl:choose>
												</span>
											</a>
										</xsl:when>
										<xsl:otherwise>
										    <xsl:attribute name="tabindex">0</xsl:attribute>
											<!-- fn represents the node 'fieldname'-->
											<xsl:value-of select="fn"/>
										</xsl:otherwise>
									</xsl:choose>
								</td>
								<td class="errorText">
									<span class="captionError" tabindex="0">
										<xsl:value-of select="errormessage"/>
									</span>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:when>
			
			<!-- Messages -->
			<xsl:when test="id='messages'">
				<table id="messages" name ="messages" >
					<tbody>
						<xsl:for-each select="browserMessages/browserMessage">
							<tr><td><img alt="message"  tabindex="0" id="errorImg"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/warning.gif</xsl:attribute></img></td>
								<td class="messageWrap">
									<span tabindex="0">
										<xsl:value-of select="message"/>
									</span>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
				</xsl:when>
			
				<!-- Authentications -->
		   		<xsl:when test="authentications!=''">
		      		<xsl:for-each select="authentications/authentication" >
		            <xsl:if test="authResp='AUTH.NOT.VALID'">
		              <p class="field" tabindex="0">
		                <xsl:value-of select="authLabel"/>
		              </p>
		              <input type="password"   tabindex="0">
		                <xsl:attribute name="name">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>
		                <xsl:attribute name="id">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>
		                <xsl:attribute name="class">dealbox</xsl:attribute>
		                <xsl:attribute name="onChange">onAuthChange(this)</xsl:attribute>
		              </input>
		            </xsl:if>
		     	    </xsl:for-each>
		      </xsl:when>

              <!--  Rekey  -->
              <xsl:when test="rekeyFields!=''">
                <p>      
                 <tr> 
				   <td class="disabled_dealbox">
				       Rekey Fields
				   </td>
                 </tr> 
                </p> 
				<xsl:for-each select="rekeyFields/rekeyField">
				<xsl:if test="rekeyFieldError!='YES' and rekeyFieldCaption!=''">
				  <p>
				  <tr>
				    <td>
					<xsl:choose> 
				    	<xsl:when test="rekeyFieldError!='' and rekeyFieldError!='NO'">
						     <xsl:attribute name="class">error</xsl:attribute>		  
	 				    </xsl:when>  				     
					    <xsl:otherwise>
	  					     <xsl:attribute name="class">field</xsl:attribute>
            			     <xsl:attribute name="colspan">18</xsl:attribute>		  
					    </xsl:otherwise>
				    </xsl:choose>	
					<xsl:value-of select="rekeyFieldCaption"/>
			         </td>	
				   <td>  
				      <xsl:variable name="pwd" select="pass"/>
					<xsl:choose>
					    <xsl:when test="($pwd = 'Y')">
						    <input type="password" tabindex="0">
 				               <xsl:attribute name="class">dealbox</xsl:attribute>  
							   <xsl:attribute name="style">white-space: nowrap;</xsl:attribute>
							   <xsl:attribute name="colspan">18</xsl:attribute>  
				               <xsl:attribute name="name">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/></xsl:attribute>
				               <xsl:attribute name="id">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/>  </xsl:attribute>
				               <xsl:attribute name="value"><xsl:value-of select="rekeyFieldValue"/></xsl:attribute>
				               <xsl:attribute name="onChange">OnRekeyChange(this)</xsl:attribute>				     
				            </input>	
						</xsl:when>
						<xsl:otherwise>  
				    <input type="text" tabindex="0">
 				       <xsl:attribute name="class">dealbox</xsl:attribute>  
				       <xsl:attribute name="style">white-space: nowrap;</xsl:attribute>
                       <xsl:attribute name="colspan">18</xsl:attribute>  
				       <xsl:attribute name="name">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/></xsl:attribute>
				       <xsl:attribute name="id">rekeyFieldName:<xsl:value-of select="rekeyFieldName"/>  </xsl:attribute>
				       <xsl:attribute name="value"><xsl:value-of select="rekeyFieldValue"/></xsl:attribute>
				       <xsl:attribute name="onChange">OnRekeyChange(this)</xsl:attribute>				     
				    </input>
				     </xsl:otherwise> 
					</xsl:choose> 
				  </td>
				  <!-- To display appropriate error message on inputting incorrect data in rekey fields -->
				  <td tabindex="0">
				  	<xsl:if test="rekeyFieldError!='' and rekeyFieldError!='NO'">
				   		<xsl:attribute name="class">error</xsl:attribute>
				   			<xsl:value-of select="rekeyFieldError"/>
				   	</xsl:if>
				  </td>
				  </tr>
				  </p>  
				</xsl:if>
				</xsl:for-each>
              </xsl:when>
	 		
			<!-- Warnings [Overrides with a Type of WARNING] -->
			<xsl:when test="warnings!=''">
				<!-- Mark the fact that we have warnings -->
				<input type="hidden" name="warningsPresent" id="warningsPresent" value="YES"/>
				<table>					
					<xsl:for-each select="warnings/warning">
						<xsl:variable name="warningvalue">
							<xsl:value-of select="warningresponse"/>
						</xsl:variable>
						<tr><td><img alt="warning"  tabindex="0" id="errorImg"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/warning.gif</xsl:attribute></img></td>
							<td   tabindex="0">
								<xsl:attribute name="name">warningCaption:<xsl:value-of select="warningtext"/></xsl:attribute>
								<xsl:attribute name="id">warningCaption:<xsl:value-of select="warningtext"/></xsl:attribute>
								<xsl:value-of select="warningtext"/>
							</td>
							<td class="warningWrap">
								<select class="warningbox"   tabindex="0">
									<xsl:attribute name="name">warningChooser:<xsl:value-of select="warningtext"/></xsl:attribute>
									<xsl:attribute name="id">warningChooser:<xsl:value-of select="warningtext"/></xsl:attribute>
									<xsl:attribute name="warningid"><xsl:value-of select="warningId"/></xsl:attribute>
									<!-- Add an event to fire when value changes -->									
									<xsl:attribute name="onChange">javascript:changeWarning('warningCaption:<xsl:value-of select="warningtext"/>', 'warningChooser:<xsl:value-of select="warningtext"/>')</xsl:attribute>
									<xsl:for-each select="options/thisoption">
										<option>
											<xsl:attribute name="value">
												<xsl:value-of select="." />
											</xsl:attribute>
											<!-- v corresponds to the word value -->
											<xsl:if test=".=$warningvalue">
												<xsl:attribute name="selected">true</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="." />
										</option>
									</xsl:for-each>
								</select>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:when>		
			<!-- Overrides -->
			<xsl:when test="overrides!=''">
			<!-- Special case for finding arc-ib because flow does not goes through window.xsl for helpfiles-->
			<xsl:variable name="isArc">
				<xsl:choose>
					<xsl:when test="/responseDetails/userDetails/ARCIB='true'">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- Overrides Accept All button -->
			<!-- Accept all should not be displayed in confirm version -->
			<xsl:if test="$isConfirm !='YES'"> 
				<table cellpadding="0" cellspacing="0">
					<tr>							
						<td class="textbtn">
							<a onclick="javascript:commitOverrides()" href="javascript:void(0)" tabindex="0" id="errorImg">
								<xsl:value-of select="./overrides/acceptAction"/>
							</a>
						</td>
						<td class="textbtn-end" onclick="javascript:commitOverrides()">
							<span>&#160;</span>
						</td>
					</tr>
				</table>
			</xsl:if>
				
				<!-- Mark the fact that we have overrides -->
				<input type="hidden" name="overridesPresent" id="overridesPresent" value="YES"/>
				<xsl:if test="$isConfirm!='YES'">
						<table>
							<xsl:for-each select="overrides/override">
								<xsl:if test="overrideresponse!='YES'">							
									<tr>
										<td>
											<img alt="override"   tabindex="0"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/override.gif</xsl:attribute></img>
										</td>
										<td class="overrideOn" height="15"   tabindex="0">
											<xsl:attribute name="id"><xsl:value-of select="overrideId"/></xsl:attribute>
											<xsl:value-of select="overridetext"/>
										</td>
									</tr>	
								  </xsl:if>
							</xsl:for-each>
						</table>	
				</xsl:if>				
			</xsl:when>

			<!-- Everything else -->
			<xsl:otherwise>
				<LI class="clsHasKids">
					<span onclick="ProcessMouseClick(event)" onkeypress="ProcessKeyPress(event)">
						<img tabindex="0" id="imgError">
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/menu/menu_down.gif</xsl:attribute>
							<xsl:attribute name="alt"><xsl:value-of select="@c"/></xsl:attribute>
						</img>
						<!--get the attribute caption-->
						<xsl:value-of select="@c"/>
					</span>
					<UL>
						<!-- Check whether topic should be auto-expanded or not -->
						<xsl:if test="@expand='Y'">
							<xsl:attribute name="class">expand</xsl:attribute>
						</xsl:if>

						<!-- Loop through all the sub nodes -->
						<xsl:for-each select="node()">
							<!-- ts is short for the word 'topics' -->
							<xsl:if test="name()='ts'">
								<xsl:call-template name="topics_n"/>
							</xsl:if>
							<!-- ts is short for the word 'topic' -->
							<xsl:if test="name()='t'">
								<li>
									<xsl:apply-templates select="."/>
								</li>								
							</xsl:if>
						</xsl:for-each>
					</UL>
				</LI>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="tabbed_topics">
		<xsl:choose>
			
			<!-- Errors -->
			<xsl:when test="id='errors'">
				<table id="errors" name="errors" >
					<tbody>
						<xsl:for-each select="errors/error">
							<tr><td><img alt="error"   tabindex="0"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/error.gif</xsl:attribute></img></td>
								<td width="1" class="errors">
									<xsl:choose>
										<xsl:when test="errorhref!=''">
											<a   tabindex="0">
												<xsl:attribute name="onclick"><xsl:value-of select="errorhref"/></xsl:attribute>
												<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
												<span>
													<!-- fn represents the node 'fieldname'-->
													<xsl:value-of select="fn"/>
												</span>
											</a>
										</xsl:when>
										<xsl:otherwise>
											<!-- fn represents the node 'fieldname'-->
											<xsl:attribute name="tabindex">0</xsl:attribute>
											<xsl:value-of select="fn"/>
										</xsl:otherwise>
									</xsl:choose>
								</td>
								<td class="errorText">
									<span class="captionError" tabindex="0">
										<xsl:value-of select="errormessage"/>
									</span>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:when>
			
			<!-- Messages -->
			<xsl:when test="id='messages'">
				<table id="messages" name ="messages" >
					<tbody>
						<xsl:for-each select="browserMessages/browserMessage">
							<tr><td><img alt="message"   tabindex="0"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/warning.gif</xsl:attribute></img></td>
								<td class="messageWrap">
									<span   tabindex="0">
										<xsl:value-of select="message"/>
									</span>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:when>
			
			<!-- Authentications -->
			<xsl:when test="authentications!=''">
				<xsl:for-each select="authentications/authentication" >
					<xsl:if test="authResp='AUTH.NOT.VALID'">
						<p class="field" tabindex="0">
							<xsl:value-of select="authLabel"/>
						</p>
						<input type="password"  tabindex="0">
							<xsl:attribute name="name">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>
							<xsl:attribute name="id">authType:<xsl:value-of select="authType"/>:<xsl:value-of select="authResp"/></xsl:attribute>
							<xsl:attribute name="class">dealbox</xsl:attribute>
							<xsl:attribute name="onChange">onAuthChange(this)</xsl:attribute>
						</input>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			
			<!-- Warnings [Overrides with a Type of WARNING] -->
			<xsl:when test="warnings!=''">
				<!-- Mark the fact that we have warnings -->
				<input type="hidden" name="warningsPresent" id="warningsPresent" value="YES"/>
				<table>					
					<xsl:for-each select="warnings/warning">
						<xsl:variable name="warningvalue">
							<xsl:value-of select="warningresponse"/>
						</xsl:variable>
						<tr><td><img alt="warning"  tabindex="0"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/warning.gif</xsl:attribute></img></td>
							<td  tabindex="0">
								<xsl:attribute name="name">warningCaption:<xsl:value-of select="warningtext"/></xsl:attribute>
								<xsl:attribute name="id">warningCaption:<xsl:value-of select="warningtext"/></xsl:attribute>
								<xsl:value-of select="warningtext"/>
							</td>
							<td class="warningWrap">
								<select class="warningbox"   tabindex="0">
									<xsl:attribute name="name">warningChooser:<xsl:value-of select="warningtext"/></xsl:attribute>
									<xsl:attribute name="id">warningChooser:<xsl:value-of select="warningtext"/></xsl:attribute>
									<xsl:attribute name="warningid"><xsl:value-of select="warningId"/></xsl:attribute>
									<!-- Add an event to fire when value changes -->									
									<xsl:attribute name="onChange">javascript:changeWarning('warningCaption:<xsl:value-of select="warningtext"/>', 'warningChooser:<xsl:value-of select="warningtext"/>')</xsl:attribute>
									<xsl:for-each select="options/thisoption">
										<option>
											<xsl:attribute name="value">
												<xsl:value-of select="." />
											</xsl:attribute>
											<!-- v corresponds to the word value -->
											<xsl:if test=".=$warningvalue">
												<xsl:attribute name="selected">true</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="." />
										</option>
									</xsl:for-each>
								</select>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:when>
			<!-- Overrides -->
		<xsl:when test="overrides!=''">
		<!-- Special case for finding arc-ib because flow does not goes through window.xsl for helpfiles-->
		<xsl:variable name="isArc">
			<xsl:choose>
				<xsl:when test="/responseDetails/userDetails/ARCIB='true'">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
				<!-- Overrides Accept All button -->
				<xsl:if test="overrides/acceptAction!=''">
				<table cellpadding="0" cellspacing="0">
					<tr>	
          				<td class="textbtn">
								<a onclick="javascript:commitOverrides()" href="javascript:void(0)" tabindex="0">
									<xsl:value-of select="./overrides/acceptAction"/>
								</a>
						</td>
						<td class="textbtn-end" onclick="javascript:commitOverrides()"   tabindex="0">
								<span>&#160;</span>
						</td>
					</tr>
				</table>
				</xsl:if>
				
				<!-- Mark the fact that we have overrides -->
				<input type="hidden" name="overridesPresent" id="overridesPresent" value="YES"/>				
						<table>
							<xsl:for-each select="overrides/override">	
								<xsl:if test="overrideresponse!='YES'">									
									<tr>
										<td>
											<img alt="override"   tabindex="0"><xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/override.gif</xsl:attribute></img>
										</td>
										<td class="overrideOn" height="15"   tabindex="0">
											<xsl:attribute name="id"><xsl:value-of select="overrideId"/></xsl:attribute>
											<xsl:value-of select="overridetext"/>
										</td>
									</tr>	
							</xsl:if>
							</xsl:for-each>
						</table>
		</xsl:when>
		<!-- Everything else -->
		<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="descendant::t">
						<xsl:for-each select="descendant::t">
							<td onclick="TabbedMenu.showActiveCommandLink({position() - 1});" class="nonactive-command">
								<xsl:apply-templates select="."/>
							</td>
							<xsl:if test="position() &lt; last()"><td>|</td></xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="."/>
					</xsl:otherwise>
				</xsl:choose>
					
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
