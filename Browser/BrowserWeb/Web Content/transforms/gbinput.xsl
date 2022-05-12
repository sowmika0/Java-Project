<?xml version="1.0" encoding="UTF-8"?>
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="tableBorders.xsl"/>

<!-- Extract the Skin name for identifying CSS and Images directory -->
<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>
		
<xsl:template match="/">
	<html>
		<head>
			<!-- Include the required stylesheets - using a skin version if specified -->
			<link rel="stylesheet" type="text/css">
				<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
			</link>
			<link rel="stylesheet" type="text/css">
				<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/login.css</xsl:attribute>
			</link>

			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			
			<script src="../scripts/general.js"/>
				<script src="../scripts/ARC/T24_constants.js"/>
				<script src="../scripts/ARC/Logger.js"/>
				<script src="../scripts/ARC/Fragment.js"/>
				<script src="../scripts/ARC/FragmentUtil.js"/>
				<script src="../scripts/version/version.js" />
		</head>
					
		<body class="loginbody" onLoad="javascript:initPassword()" onlotopmargin="0" leftmargin="0">
			<xsl:apply-templates/>
		</body>
	</html>

</xsl:template>
	
<xsl:template match="responseDetails">

	<title>
		<xsl:value-of select="title"/>
	</title>
	
	<form name="gbinput" method="POST" action="BrowserServlet" id="gbinput">

		<input type="hidden" name = "command" value = "repeatpassword"/>
		<input type="hidden" name = "requestType">
            <xsl:attribute name="value"><xsl:value-of select="requestType"/></xsl:attribute>
        </input>
		<input type="hidden" name = "routineName">
             <xsl:attribute name="value"><xsl:value-of select="routineName"/></xsl:attribute>
        </input>
		<input type="hidden" name="routineArgs">
			<xsl:attribute name="value"><xsl:value-of select="routineArgs"/></xsl:attribute>
		</input>
		<input type="hidden" name="signOnName">
			<xsl:attribute name="value"><xsl:value-of select="signOnName"/></xsl:attribute>
		</input>
		<input type="hidden" name="password">
			<xsl:attribute name="value"><xsl:value-of select="password"/></xsl:attribute>
		</input>		
        <input type="hidden" name="random">
			<xsl:attribute name="value"><xsl:value-of select="random"/></xsl:attribute>
		</input>  

		<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center">
			<tr>
				<td width="50%" bgcolor="#dbeaf5">
				
					<!-- Main form table -->
					<table  border="0" cellpadding="0" cellspacing="0" align="center">
						<tr>
							<td width="40%" align="center">
								<table cellSpacing="0" cellPadding="0" border="0" width="50%" align="center">
									<tr><td><br/><br/></td></tr>
									<xsl:call-template name="tableBorder_header_top"/>
									<tr>
										<xsl:call-template name="tableBorder_header_left"/>
										<td class="caption">
											<xsl:attribute name="background">../plaf/images/<xsl:copy-of select="$skin"/>/tab_back.gif</xsl:attribute>
											<table>
												<tr>
													<td class="caption" colSpan="2">
														<xsl:attribute name="background">../plaf/images/<xsl:copy-of select="$skin"/>/tab_back.gif</xsl:attribute>
														<b><xsl:value-of select="title"/></b>
														<img height="2" width="1">
															<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/block.gif</xsl:attribute>
														</img>
													</td>
												</tr>
											</table>
										</td>
										<xsl:call-template name="tableBorder_header_right"/>
									</tr>
									<tr>
										<xsl:call-template name="tableBorder_body_left"/>
										<td>
											<table border="0" cellpadding="0" cellspacing="0" align="center" width="100%">
												<tr>
												    <!-- Now process the login and password fields -->
								    				<!-- for each row -->
		                                            <xsl:for-each select="r">
														<tr>
															<!-- run through each cell; ce represents the node cell -->
															<xsl:for-each select="ce">
																<td>
											                    	<xsl:choose>
																		<!-- caption - i.e. the label for a field-->
																		<xsl:when test="./c!=''">
																			<xsl:value-of select="c"/>
																			<td/>
																		</xsl:when>
																		<!-- Input Box -->
																		<!-- ty represents the word 'type'-->
																		<xsl:when test="./ty='input'">
																			<input type="text" size="30">
																			    <!-- n represents the node 'name'-->
																				<xsl:attribute name="name"><xsl:value-of select="n"/></xsl:attribute>
																				<!-- v corresponds to the word value -->
																				<xsl:attribute name="value"><xsl:value-of select="v"/></xsl:attribute>
																			</input>
																			<td/>
																			<br/><p/>
																		</xsl:when>
																	</xsl:choose>
																</td>
															</xsl:for-each>
														</tr>
												   </xsl:for-each>
												</tr>
												
												<!-- Go button -->
												<tr>
													<td align="center"><br/>
														<INPUT type="image" id="goButton" halign="right" border="0">
															<xsl:attribute name="Onclick"><xsl:value-of select="StartupScript"/></xsl:attribute>
															<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/menu/go.gif</xsl:attribute>
												   		</INPUT>
													</td>
												</tr>
												
												<!-- Error message -->
												<tr>
													<td >
														<br/>
														<p align="center">
															<B>
																<FONT color="red">
																	<xsl:value-of select="errorMessage"/>
																</FONT>
															</B>
														</p>
														<br/>
				                                  	</td>
												</tr>                               
											</table>
										</td>
										<xsl:call-template name="tableBorder_body_right"/>
									</tr>
									<xsl:call-template name="tableBorder_body_bottom"/>
								</table>
							</td>
						</tr>
					</table>
					<br>
					<table border="0" cellpadding="0" cellspacing="0" width="50%" align="center">
						<tr><td><b><xsl:value-of select="FooterMsg"/></b></td></tr>
						<tr><td><br/></td></tr>
					</table> 
					<INPUT type="TEXT" name="hi" STYLE="display:none"/>
					</br>
				</td>
			</tr>
		</table>
                                                    
		<SCRIPT LANGUAGE="JAVASCRIPT">setFocus();</SCRIPT>
      </form>
	
</xsl:template>

</xsl:stylesheet>
