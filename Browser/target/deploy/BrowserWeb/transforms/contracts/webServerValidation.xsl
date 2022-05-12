<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- This transform tranforms a web server validation response -->
	
	<xsl:template match="/validationResponse">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<!-- JavaScript files - the full set -->
                <script type="text/javascript" src="../scripts/ARC/Logger.js"></script>
				<xsl:choose>
                    <xsl:when test="obfuscate">
                        <xsl:choose>
                            <xsl:when test="obfuscate/@type='both'">
                                <script type="text/javascript" src="../scripts/obfuscated/all.js"></script>
                            </xsl:when>
                            <xsl:when test="obfuscate/@type='external'">
                                <script type="text/javascript" src="../scripts/all.js"></script>
                            </xsl:when>
                            <!-- internal -->
                            <xsl:otherwise>
                                <script type="text/javascript" src="../scripts/obfuscated/general.js"></script>
                                <script type="text/javascript" src="../scripts/custom.js"></script>
                                <script type="text/javascript" src="../scripts/obfuscated/Deal.js"></script>
                                <script type="text/javascript" src="../scripts/validation.js"></script>
                                <script type="text/javascript" src="../scripts/dynamicHtml.js"></script>
                                <script type="text/javascript" src="../scripts/ARC/FragmentUtil.js"></script>
                                <script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <script type="text/javascript" src="../scripts/general.js"></script>
                        <script type="text/javascript" src="../scripts/custom.js"></script>
                        <script type="text/javascript" src="../scripts/Deal.js"></script>
                        <script type="text/javascript" src="../scripts/validation.js"></script>
                        <script type="text/javascript" src="../scripts/dynamicHtml.js"></script>
                        <script type="text/javascript" src="../scripts/ARC/FragmentUtil.js"></script>
                        <script type="text/javascript" src="../scripts/ARC/Fragment.js"></script>
					</xsl:otherwise>
				</xsl:choose>										
			</head>
			
			<body onload="processWebValResponse()">
				<form id="validationResult" name="validationResult">

					<!-- Process the general errors -->
					<xsl:for-each select="errors/error">
						<input type="hidden" name="error:{.}" id="error:{.}" value="{.}"/>
					</xsl:for-each>
					
					<!-- Process the general messages -->
					<xsl:for-each select="messages/message">
						<input type="hidden" name="message:{.}" id="message:{.}" value="{.}"/>
					</xsl:for-each>

					<!-- Process each field result -->
					<xsl:for-each select="flds/fld">
						<input type="hidden" name="fieldName:{name}" id="fieldName:{name}" multi="{mv}" sub="{sv}">
							
							<!-- Check for any changes in the field data -->
							<xsl:choose>
								<xsl:when test="./v">
									<xsl:attribute name="value"><xsl:value-of select="v"/></xsl:attribute>
								</xsl:when>	
								<xsl:otherwise>
									<xsl:attribute name="value">undefined</xsl:attribute>
								</xsl:otherwise>	
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="./err">
									<xsl:attribute name="err"><xsl:value-of select="err"/></xsl:attribute>
								</xsl:when>	
								<xsl:otherwise>
									<xsl:attribute name="err">undefined</xsl:attribute>
								</xsl:otherwise>	
							</xsl:choose>	
							<xsl:choose>
								<xsl:when test="./enri">
									<xsl:attribute name="enri"><xsl:value-of select="enri"/></xsl:attribute>
								</xsl:when>	
								<xsl:otherwise>
									<xsl:attribute name="enri" >undefined</xsl:attribute>
								</xsl:otherwise>	
							</xsl:choose>					
						</input>
					</xsl:for-each>
					
					<!-- Process the questions -->
					<xsl:for-each select="questions/qu">
						<input type="hidden" name="question:{qutext}" id="question:{qutext}" value="{qutext}" jsroutine="{qurtn}" fld="{qufld}"/>
					</xsl:for-each>
					
				</form>
			</body>
		</html>
						
	</xsl:template>

</xsl:stylesheet>
