<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- import all of the child items here -->
	<xsl:import href="workspace_pane.xsl"/>
	<xsl:import href="workspace_header.xsl"/>
	<xsl:import href="workspace_item.xsl"/>
	<xsl:import href="../../../transforms/pane.xsl"/>
    <xsl:import href="../../../transforms/generalForm.xsl"/>
    	<xsl:import href="../../../transforms/userDetails.xsl"/>
    	<xsl:import href="../../../transforms/contracts/multiPane.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>

	<xsl:variable name="atoolcompid">
		<xsl:value-of select="/responseDetails/userDetails/companyId"/>
	</xsl:variable>

    <xsl:variable name="formFragmentSuffix">
        <xsl:choose>
            <xsl:when test="responseDetails/webDetails/WS_FragmentName!=''">_<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="keepaliveTimeout">
        <xsl:choose>
            <xsl:when test="responseDetails/webDetails/WS_KeepaliveTimeout!=''"><xsl:value-of select="/responseDetails/webDetails/WS_KeepaliveTimeout"/></xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="stripFrameToolbars">
        <xsl:choose>
            <xsl:when test="responseDetails/userDetails/stripFrameToolbars='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="noFrames">
        <xsl:choose>
            <xsl:when test="responseDetails/window/noFrames='true'">true</xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<xsl:variable name="menuStyle">
	    <xsl:choose>
	    	<xsl:when test="responseDetails/userDetails/menuStyle"><xsl:value-of select="responseDetails/userDetails/menuStyle"/></xsl:when>
	        <xsl:otherwise>DEFAULT</xsl:otherwise>
	    </xsl:choose>
	</xsl:variable>

    <xsl:variable name="printFeature">
        <xsl:choose>
        	<xsl:when test="responseDetails/userDetails/cleanPrint='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
        
    <xsl:variable name="keepaliveHandling">
        <xsl:choose>
        	<xsl:when test="responseDetails/userDetails/useKeepaliveHandling='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="autoHoldDeals">
        <xsl:choose>
        	<xsl:when test="responseDetails/userDetails/useAutoHoldDeals='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
	
    <xsl:variable name="logging">
        <xsl:choose>
        	<xsl:when test="responseDetails/userDetails/clientLogging='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<xsl:variable name="compScreen">
        <xsl:value-of select="/responseDetails/userDetails/compScreen"/>
	</xsl:variable>

	<xsl:variable name="contextRoot">
		<xsl:value-of select="/responseDetails/contextRoot"/>
	</xsl:variable>

<xsl:variable name="multiPane">
<xsl:choose>
  <xsl:when test="count(responseDetails/window/panes/pane) > 1">true</xsl:when> 
  <xsl:otherwise>false</xsl:otherwise> 
  </xsl:choose>
  </xsl:variable>
	
	
	<xsl:template match="/">

		<html>
		
			<xsl:if test="/responseDetails/userDetails/lngDir!=''">
				<xsl:attribute name="dir"><xsl:value-of select="/responseDetails/userDetails/lngDir"/></xsl:attribute>
			</xsl:if>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<!-- Include the required stylesheets - using a skin version if specified -->
					<script type="text/javascript"><xsl:attribute name="src">../scripts/ARC/Logger.js</xsl:attribute></script> 
				<link rel="stylesheet" type="text/css" href="../components/workspace/css/workspace.css"/>
								<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>

			
				<!-- JavaScript files - the full set -->
				<script type="text/javascript" src="../components/workspace/js/workspace.js"></script>
				<script type="text/javascript" src="../components/workspace/js/dragdrop.js"></script>
				<script type="text/javascript" src="../components/workspace/js/timer.js"></script>
		
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/general.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/custom.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/commandline.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/Deal.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/dropdown.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/dynamicHtml.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/enquiry.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/explorer.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/fastpath.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/help.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/jsp.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/menu.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/request.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/tabs.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/validation.js</xsl:attribute></script>
				<script type="text/javascript" >	<xsl:attribute name="src">../scripts/compositescreen.js</xsl:attribute></script>
<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/tabbedMenu.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentUtil.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Fragment.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentEvent.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/workflow.js</xsl:attribute></script>
    <script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/T24_constants.js</xsl:attribute></script>
    
				<!-- Set a default title if one has not been supplied for whatever reason -->
				<title>
					<xsl:choose>
						<xsl:when test="responseDetails/window/title!=''">
							<xsl:value-of select="responseDetails/window/title"/>
						</xsl:when>
						<xsl:otherwise>Temenos T24</xsl:otherwise>
					</xsl:choose>
				</title>
			</head>
			<!-- Work out whether we are a frame page or a non-frame page -->
<script language="javascript">top.noFrames = <xsl:value-of select="$noFrames"/>; top.keepaliveHandling = <xsl:value-of select="$keepaliveHandling"/>; top.autoHoldDeals = <xsl:value-of select="$autoHoldDeals"/>; top.isWorkspace = true;</script>
					<xsl:element name="body">
					<xsl:attribute name="onload">init()</xsl:attribute>
						<xsl:attribute name="class">workspace</xsl:attribute>
						<div style="height:0">
							<xsl:call-template name="generalForm"/>
						</div>
						<xsl:for-each select="/responseDetails/workspace">
							<xsl:call-template name="workspace_header"/>
						</xsl:for-each>
					</xsl:element>

		</html>
	</xsl:template>


</xsl:stylesheet>
