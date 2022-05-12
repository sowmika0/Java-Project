<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="userDetails.xsl"/>
	<xsl:import href="generalForm.xsl"/>
	
	
	<xsl:variable name="formFragmentSuffix">
        <xsl:choose>
            <xsl:when test="responseDetails/webDetails/WS_FragmentName!=''">_<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="multiPane"/>
    
    
	<xsl:template match="/">
	  
		<html>
			<head>
			    <!-- 
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<script type="text/javascript" src="../scripts/general.js"/>
				<script type="text/javascript" src="../scripts/request.js"/>
				<script type="text/javascript" src="../scripts/commandline.js"/>
				<script type="text/javascript" src="../scripts/enquiry.js"/>								
				<script type="text/javascript" src="../scripts/Deal.js"/>
				<script type="text/javascript" src="../scripts/menu.js"/>
				<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
				<script type="text/javascript" src="../scripts/ARC/Logger.js" />
				<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
				<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />				                
				<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />
				<script type="text/javascript" src="../scripts/version/version.js" />				
				  -->
			</head>
			<style type="text/css">
					
					.Verdana{
						font-face:Verdana;
						size: 19;
					}
			</style>
			
			<body >
					<xsl:choose>
       					<xsl:when test="responseDetails/banner/selectedSystemId">
       						<xsl:choose>
	       						<xsl:when test="responseDetails/banner/isUpdates">
									<xsl:attribute name="onload">docommand('ENQ EB.UPDATE.SYSTEM.LIST SYSTEM.ID EQ <xsl:value-of select="responseDetails/banner/selectedSystemId"/>'); t24UpdatesOnLoad();</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="onload">docommand('QUERY EB.NOFILE.SERVICE.PACKS'); t24UpdatesOnLoad();</xsl:attribute>
								</xsl:otherwise>
						    </xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="onload">t24UpdatesOnLoad();</xsl:attribute>						
						</xsl:otherwise>
					</xsl:choose>
			
					<xsl:call-template name="generalForm" />
					<table width="738px">
						<tr>
							<td class="Verdana" width="40%">
								<img src="../plaf/images/arc-ib/enquiry/updatesLogo.gif" />
							</td>
							
							<xsl:if test="responseDetails/banner/selectedSystemId">
								<td class="Verdana" width="60%" nowrap="nowrap" align="center">
									<table>
									    <tr align="right">
									    	<td>
									    		<font face="Verdana" color="#666666" size="2" >
	            									Search for Symptoms or Problems
	            								</font>
									    	</td>
									    </tr>
										<tr align="right">
											<td width="100%">
												<input type="text" id="updatesSearchBox" name="updatesSearchBox" size="50" >
													<xsl:attribute name="style">font-face: Verdana; color: #8BB2D9; size: 2;</xsl:attribute>
													<xsl:choose>
	       												<xsl:when test="responseDetails/banner/isUpdates">
	       													<xsl:attribute name="onkeydown">onKeyDownSearchUpdates( event)</xsl:attribute>
							        					</xsl:when>
							        					<xsl:otherwise>
	            											<xsl:attribute name="onkeydown">onKeyDownSearchServicePacks( event)</xsl:attribute>
	            										</xsl:otherwise>
							        				</xsl:choose>
													
												</input>
											</td>
											<td>
												<img src="../plaf/images/arc-ib/enquiry/search.gif" >
												    <xsl:attribute name="style">cursor: pointer;</xsl:attribute>
													<xsl:choose>
	       												<xsl:when test="responseDetails/banner/isUpdates">
	       													<xsl:attribute name="onclick">t24UpdatesSearch()</xsl:attribute>
							        					</xsl:when>
							        					<xsl:otherwise>
	            											<xsl:attribute name="onclick">t24ServicePackSearch()</xsl:attribute>
	            										</xsl:otherwise>
							        				</xsl:choose>
												</img>
											</td>
										</tr>
									</table>
								</td>
							</xsl:if>
						</tr>
					</table>
					
					<br />
					
					<table width="738px">
						<tr>
						    <td width="1%" />
							<td width="69%">
								<p>
									<font face="Verdana" color="#336699" size="3" >	
										<xsl:value-of select="responseDetails/banner/welcomeMessage"/>
									</font>
								</p>
							</td>
							<td width="30%">
								<p>
									<font face="Verdana" color="#336699" size="2" >	
										<xsl:value-of select="responseDetails/banner/lastLoginText"/>
									</font>
								</p>
							</td>
						</tr>
						<tr ><td><br/></td></tr>
						<tr>
							<td width="1%" />
							<td width="99%" colspan="2">
								<p>
									<font face="Verdana" color="#666666" size="2" >
										<xsl:choose>
            								<xsl:when test="responseDetails/banner/selectedSystemText">
            									System Description: <font face="Verdana" color="#336699" size="2" >
            															<b><xsl:value-of select="responseDetails/banner/selectedSystemText"/></b>
            														</font>
            								</xsl:when>
            								<xsl:otherwise>
            									<a onclick="javascript:t24UpdatesDoCommand('QUERY EB.UPDATE.CLIENT.SYSTEMS')" href="javascript:void(0)">
            											Please select a system to get updates for
            									</a>
            								</xsl:otherwise>
						        		</xsl:choose>
						        	</font>
								</p>
							</td>
						</tr>
						<xsl:if test="responseDetails/banner/systemRelease">
	    					<tr>
								<td width="1%" />
								<td width="99%" colspan="2">
									<p>
										<font face="Verdana" color="#666666" size="2" >
											System Release: <font face="Verdana" color="#336699" size="2" >
																<b><xsl:value-of select="responseDetails/banner/systemRelease"/></b>
															</font>
							        	</font>
									</p>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="responseDetails/banner/systemPlatform">
							<tr>
								<td width="1%" />
								<td width="99%" colspan="2">
									<p>
										<font face="Verdana" color="#666666" size="2" >
											System Platform: <font face="Verdana" color="#336699" size="2" > 
																<b><xsl:value-of select="responseDetails/banner/systemPlatform"/></b>
															 </font>
							        	</font>
									</p>
								</td>
							</tr>
						 </xsl:if>	
					</table>
					
					<br />
					
					<table >
						<tr >

							<td width="3%" />
							<td >
								<a onclick="javascript:t24UpdatesDoCommand('QUERY EB.UPDATES.INBOX')" href="javascript:void(0)">
							    	<font face="Verdana" color="#336699" size="2" >		
										Messages
									</font>
						        </a>
							</td>

							<td width="3%" />
							<td >
								<a onclick="javascript:t24UpdatesDoCommand('QUERY EB.UPDATE.CLIENT.SYSTEMS')" href="javascript:void(0)">
							    	<font face="Verdana" color="#336699" size="2" >		
										Systems
									</font>
						        </a>
							</td>
							
							<td width="3%" />
							<td >
								<a onclick="javascript:t24UpdatesDoCommand('UTIL OS.UPLOAD.EUS.FILE');" href="javascript:void(0)">
							    	<font face="Verdana" color="#336699" size="2" >		
										Add System
									</font>
						        </a>
							</td>

							<td width="3%" />
							<td >
								<xsl:choose>
       								<xsl:when test="responseDetails/banner/selectedSystemText">
       									<xsl:choose>
       										<xsl:when test="responseDetails/banner/isUpdates">
       											<a onclick="javascript:t24UpdatesDoCommand('QUERY EB.AVAILABLE.UPDATES')" href="javascript:void(0)">
							    					<font face="Verdana" color="#336699" size="2" >		
														Available Updates
													</font>
						        				</a>
						        			</xsl:when>
						        			<xsl:otherwise>
            									<a onclick="javascript:t24UpdatesDoCommand('QUERY EB.NOFILE.SERVICE.PACKS')" href="javascript:void(0)">
							    					<font face="Verdana" color="#336699" size="2" >		
														Available Service Packs
													</font>
						        				</a>
            								</xsl:otherwise>
						        		</xsl:choose>
       								</xsl:when>
				        		</xsl:choose>
								
							</td>							
							
						</tr>
					</table>
					
					<br />
					
					<table  >
						<tr>
						    <td width="10%" />
							<td>
								<font id="titleArea" name="titleArea" face="Verdana" color="#666666" size="2" >
									
								</font>
							</td>
						</tr>
					</table>
			</body>
		</html>
		
	</xsl:template>

</xsl:stylesheet>
