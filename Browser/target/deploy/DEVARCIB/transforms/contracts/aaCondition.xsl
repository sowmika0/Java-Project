<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:import href="../ARC/T24_constants.xsl"/>
	<xsl:import href="../toolbars/toolbars.xsl"/>
	<xsl:import href="contract.xsl"/>
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	
	<xsl:variable name="multiPane" select="'false'" />
	
	<xsl:variable name="Assocdisplay">
    	<xsl:value-of select="responseDetails/userDetails/Assocdisplay"/>
    </xsl:variable>
    
	<xsl:variable name="Nonedisplay">
    	<xsl:value-of select="responseDetails/userDetails/Nonedisplay"/>
    </xsl:variable>
	
	<xsl:variable name="formFragmentSuffix">
        <xsl:choose>
            <xsl:when test="responseDetails/webDetails/WS_FragmentName!=''">_<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="stripFrameToolbars">
        <xsl:choose>
            <xsl:when test="responseDetails/userDetails/stripFrameToolbars='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>  
    
    
	<!-- Builds up a 'control' for a recurrence field in Browser. -->
	<!-- This allows the user to specify schedules for recurring events like 'montly on the first wednesday' -->
	<xsl:template match="/">
		<html>
			<head>
			
				<!-- Include the required stylesheets - using a skin version if specified -->
	<link rel="stylesheet" type="text/css">
		<xsl:attribute name="href">../../plaf/style/<xsl:value-of select="$skin"/>/general.css</xsl:attribute>
	</link>
	<link rel="stylesheet" type="text/css">
		<xsl:attribute name="href">../../plaf/style/<xsl:value-of select="$skin"/>/custom.css</xsl:attribute>
	</link>
	<title>Prototype</title>
			</head>
			<body>
				<xsl:attribute name="onload">
						javascript:initAddNewpropertyForm('<xsl:value-of select="/responseDetails/window/panes/pane/contract/TopicList/MainTopic[2]/ts/errors/error/errormessage"/>','<xsl:value-of select="/responseDetails/window/panes/pane/duplicatecheck"/>','<xsl:value-of select="responseDetails/window/panes/pane/OldGroupTabID"/>','<xsl:value-of select="responseDetails/window/panes/pane/contract/app" /><xsl:value-of select="responseDetails/window/panes/pane/contract/version" /><xsl:value-of select="responseDetails/window/panes/pane/contract/key" />','<xsl:value-of select="/responseDetails/window/panes/pane/contract/tabEnri"/>');javascript:<xsl:value-of select="responseDetails/webDetails/WS_FragmentName"/>;
				</xsl:attribute>
				<xsl:for-each select="responseDetails/window/panes/pane/contract">
					<xsl:variable name="id">pane_<xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" /><xsl:value-of select="$formFragmentSuffix"/></xsl:variable>
					<xsl:variable name="propertyname"><xsl:value-of select="app" /><xsl:value-of select="version" /><xsl:value-of select="key" /><xsl:value-of select="$formFragmentSuffix"/></xsl:variable>
					<xsl:variable name="taberrors"><xsl:value-of select="/responseDetails/window/panes/pane/contract/TopicList/MainTopic[2]/ts/errors/error/errormessage"/></xsl:variable>
					<xsl:if test="$taberrors=''">
						<div>
							<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
							<xsl:attribute name="name"><xsl:value-of select="$id"/></xsl:attribute>
							<xsl:call-template name="contract_n"/>
						</div>
					</xsl:if>
					<xsl:if test="$taberrors!=''">
						<div>
							<span class="error">
							<xsl:value-of select="$taberrors"/>
							</span>
						</div>						
					</xsl:if>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
