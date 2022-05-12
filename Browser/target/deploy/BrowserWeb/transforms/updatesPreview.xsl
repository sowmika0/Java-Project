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
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<!-- <script type="text/javascript" src="../scripts/general.js"/>
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
		      	<script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/Logger.js</xsl:attribute></script>
				<script type="text/javascript" src="../scripts/all.js" />
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/arc-ib/general.css</xsl:attribute>
				</link>	
				<title>T24 Updates Service</title>	
			</head>
			<style type="text/css">
					
					.Verdana{
						font-face:Verdana;
						size: 19;
					}
			</style>
			
			<body >
			
					<form name="updatesForm" method="POST" action="UpdatesServlet">
		
		            	<!-- Add the id, with fragment suffix for noFrames mode ....-->
		            	<xsl:attribute name="id">updatesForm<xsl:value-of select="$formFragmentSuffix"/></xsl:attribute>
		            	<input type="hidden" name="updateList" value="{responseDetails/updates/delimitedUpdatesList}" />
					</form>
					<br/>
					<div class="enquiry_response display_box" >
						<table>
							<tr>
								<td>
									<p>
										<font face="Verdana" color="#336699" size="2" >		
												These are the selected updates including dependencies
										</font>
									</p>
								</td>
							</tr>
						</table>
					</div>
					<div class="enquiry_response display_box" >
						<table class="enquirydata" >
						    <!-- Create the header part  -->
						    <tr class="colour1" >
						    	<td class="columnHeader" >Update Reference</td>
						    	<td class="columnHeader" >Product</td>
						    	<td class="columnHeader" >Component</td>
						    </tr>
							<xsl:for-each select="responseDetails/updates/update">
								<tr>
									<xsl:attribute name="class">colour<xsl:value-of select="position() mod 2"/></xsl:attribute>
									<td>	
										<xsl:value-of select="id"/>
									</td>
									<td>	
										<xsl:value-of select="product"/>
									</td>
									<td>	
										<xsl:value-of select="component"/>
									</td>														
								</tr>	 							
							</xsl:for-each>
						</table>
					</div>

					<div class="enquiry_response display_box" >
						<table>
							<tr>
								<td >
									<a onclick="javascript:getUpdates()" href="javascript:void(0)" style="cursor: pointer;">
									    	<font face="Verdana" color="#336699" size="2" >		
												Download Updates
											</font>
							        </a>
								</td>
							</tr>
						</table>
					</div>

	
			</body>
		</html>
		
	</xsl:template>

</xsl:stylesheet>
