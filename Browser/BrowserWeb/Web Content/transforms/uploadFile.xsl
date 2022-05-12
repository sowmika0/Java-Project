<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="userDetails.xsl" />
	<xsl:import href="generalForm.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	
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
				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/menu.css</xsl:attribute>
				</link>

				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

				<script type="text/javascript" src="../scripts/general.js"/>
				<script type="text/javascript" src="../scripts/Deal.js"/>				
				<script type="text/javascript" src="../scripts/menu.js"/>
				<script type="text/javascript" src="../scripts/request.js"/>
				<script type="text/javascript" src="../scripts/dropdown.js"/>
				<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
				<script type="text/javascript" src="../scripts/ARC/Logger.js" />
				<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
				<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />				                
				<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />
				<script type="text/javascript" src="../scripts/version/version.js" />				
				
				<title></title>
			</head>
			
			
			
			
			<body>
				<xsl:choose>
				<xsl:when test="responseDetails/uploadResponse/isServicePack">
					<xsl:attribute name="onload">javascript:docommand('EB.UPDATE.SYSTEM,SERVICE.PACK I <xsl:value-of select="responseDetails/uploadResponse/fileId"/>');</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<form id="fileUploadForm" name="fileUploadForm" method="POST" ENCTYPE="multipart/form-data" action="UploadServlet" target="upload_target">
		
		                    <!-- If there is a file id then add it to the form -->
							<xsl:if test="responseDetails/uploadResponse/fileId">
		                		<input type="hidden" name="fileId" value="{responseDetails/uploadResponse/fileId}"/>
		                	</xsl:if>
		                	
		     				<input type="hidden" name="requestType" value=""/>
							<input type="hidden" name="routineName" value=""/>
							<input type="hidden" name="windowName" value=""/>
							<input type="hidden" name="routineArgs" value=""/>
							<input type="hidden" name="filePath" value=""/>
							<input type="hidden" name="multiPartFormData" value="true"/>
		
							<!-- Process User Details -->
							<xsl:call-template name="userDetails"/>
							
							<xsl:if test="responseDetails/uploadResponse">
								<table cellpadding="10">
									<tr>
										<td>
											<font face="Verdana" color="#666666" size="2" >
											<!-- If there is a file id then we updating and not adding a system -->
											<xsl:choose>
												<xsl:when test="responseDetails/uploadResponse/fileId">
													Please re-enter a new T24 Updates system file to update your system
												</xsl:when>
												<xsl:otherwise>
													Add a GA Release R09 and above (T24 Updates)
												</xsl:otherwise>
											</xsl:choose>
											</font>
										</td>
									</tr>
								</table>
							
								<!-- Build the main view of this utility -->
								<div id="fileUploadDiv" name="fileUploadDiv" class="display_box" >
									<table>
										<xsl:if test="responseDetails/uploadResponse/title">
											<tr>
												<!-- Any messages -->
												<td/>
												<td>
													<div class="dmsg"><xsl:value-of select="responseDetails/uploadResponse/title"/></div>
												</td>
											</tr>
										</xsl:if>
										
										<xsl:if test="responseDetails/uploadResponse/errors">
												<xsl:for-each select="responseDetails/uploadResponse/errors/errMsg">
													<tr>
														<!-- Any errors -->
														<td style="nowrap">
															<img alt="error">
																<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/error.gif</xsl:attribute>
															</img>
														</td>
														<td>	
															<div class="error"><xsl:value-of select="."/></div>
														</td>
													</tr>	 							
							 					</xsl:for-each>
						 				</xsl:if>
										
										
										
										<xsl:if test="responseDetails/uploadResponse/attachField">
											<tr>
												<!-- Attachment Field -->
												<td/>
												<td >
													<input size="35" type="file" name="fileName" tabindex="1" >
														<xsl:attribute name="onchange">javascript:xmlFileUpload( this.value);</xsl:attribute>
													</input>
												</td>
											</tr>
										</xsl:if>
									</table>
								</div>
								
								<xsl:if test="not(responseDetails/uploadResponse/fileId)">
									<table cellpadding="10">
										<tr>
											<td>
									    		<a onclick="javascript:docommand('EB.UPDATE.SYSTEM,SERVICE.PACK I F3');" href="javascript:void(0)">
													<font face="Verdana" color="#336699" size="2" >
														Add a GA Release R08 and below (T24 Service Packs)
													</font>
												</a>
											</td>
										</tr>
									</table>
								</xsl:if>
								
							</xsl:if>
						</form>
						<iframe onload="parent.uploadFinished()" id="upload_target" name="upload_target" src="../html/blank_enrichment.html" style="width:0;height:0;border:0px solid #fff;"></iframe>
					</xsl:otherwise>
					</xsl:choose>
					<xsl:call-template name="generalForm" />
			</body>
		</html>
		
	</xsl:template>

</xsl:stylesheet>
