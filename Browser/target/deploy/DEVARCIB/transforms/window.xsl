<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- import all of the child items here -->
	<xsl:import href="pane.xsl"/>
	<xsl:import href="frames.xsl"/>
	<xsl:import href="ARC/frames_as_tables.xsl"/>
	<xsl:import href="userDetails.xsl"/>
	<xsl:import href="generalForm.xsl"/>
	<xsl:import href="contracts/multiPane.xsl"/>
	<xsl:import href="stylingDetails.xsl"/>

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	
	<xsl:variable name="singleLoadingIcon">
		<xsl:value-of select="/responseDetails/webDetails/singleLoadingIcon"/>
	</xsl:variable>

	<xsl:variable name="atoolcompid">
		<xsl:value-of select="/responseDetails/userDetails/companyId"/>
	</xsl:variable>

    <!-- Disable the right click on browser -->
   <xsl:variable name="NoRightClick">
    <xsl:choose>
       <xsl:when test="responseDetails/NoRightClick">
          <xsl:value-of select="responseDetails/NoRightClick"/>
       </xsl:when>
       <xsl:otherwise>
          <xsl:value-of select="No"/>
       </xsl:otherwise>    
    </xsl:choose>
   </xsl:variable>
   
   <!-- Variable introduced to determine whether it is a composite screen toolbar or not -->
   <xsl:variable name="Comptoolbar">
   	<xsl:choose>
   		<xsl:when test="responseDetails/comptoolbar">true</xsl:when>
   		<xsl:otherwise>false</xsl:otherwise>
   	</xsl:choose>
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
        	<!--  instead of displaying frames, tables will be used -->
        	<xsl:when test="responseDetails/window/noframes='True'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
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
    <xsl:variable name="useconfirm">
        <xsl:choose>
        	<xsl:when test="responseDetails/userDetails/useconfirm='true'">true</xsl:when>
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
	    	<xsl:when test="/responseDetails/webDetails/WS_subPaneRequest!=''">true</xsl:when>
            <xsl:when test="count(responseDetails/window/panes/pane) > 1">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="isArc">
	    <xsl:choose>
	    	<xsl:when test="/responseDetails/userDetails/ARCIB='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="Assocdisplay">
    	<xsl:value-of select="responseDetails/userDetails/Assocdisplay"/>
    </xsl:variable>
    
	<xsl:variable name="Nonedisplay">
    	<xsl:value-of select="responseDetails/userDetails/Nonedisplay"/>
    </xsl:variable>
	
	 <xsl:variable name="EnquiryDataScroll">
    		<xsl:value-of select="responseDetails/userDetails/EnquiryDataScroll"/>
	</xsl:variable>

	<!-- Variable to process the images and document in cloud-->
	<xsl:variable name="useCloudImage">
    	<xsl:value-of select="responseDetails/userDetails/useCloudImage"/>
    </xsl:variable>
    	
   	<!-- Check for the confirm version -->
	<xsl:variable name="isConfirm">
		<xsl:choose>
			<xsl:when test="$isArc='true'">
				<xsl:if test="/responseDetails/window/panes/pane/contract/toolbars/toolbar/tool">
					<xsl:for-each select="/responseDetails/window/panes/pane/contract/toolbars/toolbar/tool"> 
						<xsl:if test="cap ='Confirm'">
							<xsl:value-of select="'YES'"/>
							<break/>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'NO'"/>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:variable>
	<xsl:template match="/">

        <xsl:message>Window XSL - top level template - menu style: <xsl:value-of select="$menuStyle"/></xsl:message>
         <xsl:variable name="language">
          	<xsl:value-of select="/responseDetails/userDetails/lng"/>
         </xsl:variable>
		<html>
			<xsl:attribute name="xml:lang">
				<xsl:call-template name="setLanguage">
					<xsl:with-param name="language" select="$language"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="lang">
		       <xsl:call-template name="setLanguage">
					<xsl:with-param name="language" select="$language"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:if test="/responseDetails/userDetails/lngDir!=''">
				<xsl:attribute name="dir"><xsl:value-of select="/responseDetails/userDetails/lngDir"/></xsl:attribute>
			</xsl:if>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

				<!-- Load the logging script first: NOTE that this is not obfuscated -->      
				<xsl:if test="$logging = 'true'">
				      	<script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/debug_only/firebug/firebug.js</xsl:attribute></script>		                	
				      	<script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/debug_only/LogMessages.js</xsl:attribute></script>
				</xsl:if >
				<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Logger.js</xsl:attribute></script>                
				
				<script type="text/javascript">if(typeof(Logger) != "undefined"){Logger.getLogger().time("load_main");}</script>
				<script type="text/javascript">if(typeof(Logger) != "undefined"){Logger.getLogger().time("load_css");}</script>
				<script>
				try{
					top.document.domain
				}catch(e){
					var clear = function() { document.body.innerHTML = '';} 
					var delay = function() { setInterval(clear, 1); }
					setTimeout("delay()",100);
					if (document.body) 
					{ 
						document.body.onload = f; 
					} 
				}
				</script>

				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/custom.css</xsl:attribute>
				</link>
				<link rel="stylesheet" type="text/css">
				    <xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/banner_arcib.css</xsl:attribute>
                </link>
				
				<!--TODO Dave: IE needs all of the stylesheets. Perhaps we could inject them as and when?-->

				<xsl:if test="$printFeature = 'true'">
					<link rel="stylesheet" type="text/css">
						<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/print.css</xsl:attribute>
					</link>
				</xsl:if>
								
				<script type="text/javascript">if(typeof(Logger) != "undefined"){Logger.getLogger().timeEnd("load_main");}</script>
				<script type="text/javascript">if(typeof(Logger) != "undefined"){Logger.getLogger().time("load_script");}</script>				

				<!-- JavaScript files - the full set -->
				<xsl:choose>
					<!-- If obfuscation is switched on -->
					<xsl:when test="/responseDetails/userDetails/obfuscate">
						<xsl:choose>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='both'">
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/all.js</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/custom.js</xsl:attribute></script>
				        		<script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>
							</xsl:when>
							<xsl:when test="/responseDetails/userDetails/obfuscate/@type='external'">
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/all.js</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/custom.js</xsl:attribute></script>
				        		<script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>
							</xsl:when>
							<!-- internal -->
							<xsl:otherwise>
				                <!-- 3 new js's added to accompany the javascript custom event handling capabilities -->
				                <script type="text/javascript">	<xsl:attribute name="src">../scripts/yahoo.js</xsl:attribute></script>
				                <script type="text/javascript">	<xsl:attribute name="src">../scripts/event.js</xsl:attribute></script>
				                <script type="text/javascript">	<xsl:attribute name="src">../scripts/customEvents.js</xsl:attribute></script>							
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/general.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/custom.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/commandline.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/Deal.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/dropdown.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/dynamicHtml.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/enquiry.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/explorer.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/fastpath.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/help.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/jsp.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/menu.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/request.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/tabs.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/validation.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/compositescreen.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/tabbedMenu.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentUtil.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Fragment.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentEvent.js</xsl:attribute></script>	
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/workflow.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/recurrence.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/obfuscated/frequency.js</xsl:attribute></script>
								<script type="text/javascript">	<xsl:attribute name="src">../scripts/t24Updates.js</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/History.js</xsl:attribute></script>
															                

								<xsl:if test="$printFeature = 'true'">
									<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Print.js</xsl:attribute></script>
								</xsl:if>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
				                <script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>
								<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/DataSet.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/DataSeries.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/main.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Area.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Axis.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Point2D.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Segment.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalLineGraph.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalBarGraph.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalBarLine.js</xsl:attribute></script>				                
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/MultiPieChart.js</xsl:attribute></script>				                
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Legend.js</xsl:attribute></script>
				                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Bar.js</xsl:attribute></script>
				                <script type="text/javascript">	<xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
				        <!-- 3 new js's added to accompany the javascript custom event handling capabilities -->
				        <script type="text/javascript">	<xsl:attribute name="src">../scripts/yahoo.js</xsl:attribute></script>
				        <script type="text/javascript">	<xsl:attribute name="src">../scripts/event.js</xsl:attribute></script>
				        <script type="text/javascript">	<xsl:attribute name="src">../scripts/customEvents.js</xsl:attribute></script>		                
					
						<script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/svgcheck.js</xsl:attribute></script>
				        <script type="text/vbscript"> <xsl:attribute name="src">../scripts/charting/svgcheck.vbs</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/general.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/custom.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/commandline.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/Deal.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/dropdown.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/dynamicHtml.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/enquiry.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/explorer.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/fastpath.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/help.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/jsp.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/menu.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/FieldGroup.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/request.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/tabs.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/validation.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/compositescreen.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/tabbedMenu.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentUtil.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Fragment.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/FragmentEvent.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/workflow.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/recurrence.js</xsl:attribute></script>								
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/frequency.js</xsl:attribute></script>
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/relativeCalendar.js</xsl:attribute></script>								
						<script type="text/javascript">	<xsl:attribute name="src">../scripts/t24Updates.js</xsl:attribute></script>
						<script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/History.js</xsl:attribute></script>
						
						<xsl:if test="$printFeature = 'true'">
							<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/Print.js</xsl:attribute></script>
						</xsl:if>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/DataSet.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/DataSeries.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/main.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Area.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Axis.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Point2D.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Segment.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalLineGraph.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalBarGraph.js</xsl:attribute></script>
				        <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/OrdinalBarLine.js</xsl:attribute></script>		                
				        <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/MultiPieChart.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Legend.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/charting/Bar.js</xsl:attribute></script>
		                <script type="text/javascript"> <xsl:attribute name="src">../scripts/ARC/T24_constants.js</xsl:attribute></script>
		                <script type="text/javascript">	<xsl:attribute name="src">../scripts/version/version.js</xsl:attribute></script>
					</xsl:otherwise>
				</xsl:choose>										
				<script type="text/javascript">if(typeof(Logger) != "undefined"){Logger.getLogger().timeEnd("load_script");}</script>

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
			<xsl:choose>
				<xsl:when test="responseDetails/window/frames!=''">
                  	<script language="javascript">top.noFrames = <xsl:value-of select="$noFrames"/>; top.keepaliveHandling = <xsl:value-of select="$keepaliveHandling"/>;top.useconfirm=<xsl:value-of select="$useconfirm"/>;top.autoHoldDeals = <xsl:value-of select="$autoHoldDeals"/>;</script>
					<!-- Now go through the contents of each frame -->
					<xsl:choose>
						<xsl:when test="$noFrames = 'true'">
							<xsl:element name="body">
							  <xsl:choose>
								<xsl:when test="/responseDetails/window/init!=''">
										<xsl:attribute name="onbeforeunload">beforeUnloadCompositeScreenWindow('<xsl:value-of select="/responseDetails/userDetails/user"/>');</xsl:attribute>
			                            <xsl:attribute name="onload">FragmentUtil.initFragments('INIT_FRAGMENTS_ALL', <xsl:value-of select="$keepaliveTimeout"/>); initWindow();<xsl:value-of select="/responseDetails/window/init"/></xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
								         <xsl:attribute name="onload">FragmentUtil.initFragments('INIT_FRAGMENTS_ALL', <xsl:value-of select="$keepaliveTimeout"/>); initWindow();</xsl:attribute>
								</xsl:otherwise>
							  </xsl:choose>					
								<!-- General mouse handler for keepalive etc. -->
								                                <xsl:choose>
                                        <xsl:when test="$NoRightClick = 'yes'">
                                            <xsl:attribute name="onmousedown">FragmentUtil.rightButtonMouseHandler(event);</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise> 
							<xsl:attribute name="onmousedown">FragmentUtil.mousedownHandler(event);</xsl:attribute>
							        </xsl:otherwise>
                                </xsl:choose>
								<!-- General key handler for refresh, keepalive etc. -->
								<xsl:attribute name="onkeydown">FragmentUtil.keydownHandler(event);</xsl:attribute>
								<xsl:attribute name="onresize">resizeDiv(event);</xsl:attribute>
								<xsl:attribute name="onunload">closeDealWindow();</xsl:attribute>
								
								<xsl:for-each select="responseDetails/window/frames">
									<!-- Use table layout in place of a Frameset -->
									<xsl:call-template name="frames_as_tables"/>
								</xsl:for-each>
								<xsl:choose>
                                        <xsl:when test="$isArc = 'true'">
                                            <input type="hidden" name="isARC" id="isARC" value="true"/>
                                           	<input type="hidden" name="Menustyle" id="Menustyle">
												<xsl:attribute name="value"><xsl:value-of select="$menuStyle"/></xsl:attribute>
											</input>                                            
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <input type="hidden" name="isARC" id="isARC" value=""/>
                                        </xsl:otherwise>
                                </xsl:choose>
                                <input type="hidden" name="skin" id="skin">
                                      <xsl:attribute name="value"><xsl:copy-of select="$skin"/></xsl:attribute>
                                </input>
                                 <input type="hidden" name="EnquiryDataScroll" id="EnquiryDataScroll">
                                      <xsl:attribute name="value"><xsl:copy-of select="$EnquiryDataScroll"/></xsl:attribute>
                                </input>
                                 <input type="hidden" name="singleLoadingIcon" id="singleLoadingIcon">
                                      <xsl:attribute name="value"><xsl:copy-of select="$singleLoadingIcon"/></xsl:attribute>
                                </input>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="responseDetails/window/frames">
			 					<xsl:apply-templates select="."/>
			 				</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
				<script language="javascript">top.noFrames = <xsl:value-of select="$noFrames"/>;</script>
					<xsl:element name="body">
						<xsl:attribute name="class">window</xsl:attribute>
						<!-- mouse down event to be triggered to hide frequency and calendar window -->
						<xsl:attribute name="onmousedown">FragmentUtil.mousedownHandler(event);</xsl:attribute>
						<!-- after changeing company the command line pane will have the old company id, so call setBannerCompanyId when we have come back from a menu which will assign the 	correct 	company id -->
						<xsl:attribute name="onresize">resizeDiv(event);</xsl:attribute>
						<xsl:attribute name="onmousedown">FragmentUtil.mousedownHandler(event);</xsl:attribute>
						<xsl:attribute name="onkeydown">FragmentUtil.keydownHandler(event);</xsl:attribute>
						<!-- Disable the right click on browser -->
                        <xsl:choose>
                           <xsl:when test="$NoRightClick='yes'">
                                <xsl:attribute name="onmousedown">javascript:trap_page_mouse_key_events();</xsl:attribute> 
                           </xsl:when>
                        </xsl:choose>
						
						<!-- Event for handling resize div in contract screen -->
						<xsl:if test="responseDetails/window/panes/pane/menu!=''">
							<xsl:attribute name="onload">
								<xsl:if test="$menuStyle = 'TABBED'">
									TabbedMenu.invokeFirstTask();
								</xsl:if>
								<xsl:if test="$menuStyle != 'TABBED'">
									menu_history('##INIT##');
								</xsl:if>										
								setBannerCompanyId('<xsl:value-of select="/responseDetails/userDetails/companyId"/>');
								initWindow()
							</xsl:attribute>
						</xsl:if>
	
						<!-- Support for print, only enquiry, enquiry response and version are printed -->
						<xsl:if test="responseDetails/window/panes/pane/dataSection or responseDetails/window/panes/pane/selSection or responseDetails/window/panes/pane/contract">
							<xsl:attribute name="printable">false</xsl:attribute>
						</xsl:if>
						
						<!-- Now go through the contents of each pane -->
						<xsl:if test="/responseDetails/window/init!=''">
							<xsl:choose>
								<xsl:when test="/responseDetails/webDetails/WS_subPaneRequest!=''">
									<xsl:attribute name="onload"><xsl:value-of select="/responseDetails/window/init"/>; setSubPaneFocus('<xsl:value-of 		select="/responseDetails/webDetails/WS_subPaneForm"/>')</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="onload"><xsl:value-of select="/responseDetails/window/init"/>; initWindow()</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="/responseDetails/window/beforeUnload!=''">
							<xsl:attribute name="onbeforeunload"><xsl:value-of select="/responseDetails/window/beforeUnload"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="/responseDetails/window/unload!=''">
							<xsl:attribute name="onunload"><xsl:value-of select="/responseDetails/window/unload"/></xsl:attribute>
						</xsl:if>
						<!-- Processing page reference -->
                        			<xsl:if test="$noFrames = 'false'">
							<div class="dprocessing" id="processingPage">
								<p>Your request is being processed.</p>
								<img border="0" src="../plaf/images/default/gears.gif"/>
							</div>
						</xsl:if>
						
						<!-- Add the multi-pane form for multiple application requests -->
						<div style="display:none;">
							<form name="multiPane" id="multiPane" method="POST" action="BrowserServlet" />
						</div>
												
						<xsl:choose>
							<xsl:when test="$multiPane = 'false' or /responseDetails/webDetails/WS_subPaneRequest!=''">
									  <xsl:apply-templates select="/responseDetails/window/panes"/>	
							</xsl:when>
							<xsl:otherwise>
							       	  <xsl:call-template name="multiPane"/>
							</xsl:otherwise>
						</xsl:choose>
					
						<!-- A general form for submitting requests - in a div so it takes up no room -->
						<div style="height:0">
							<xsl:call-template name="generalForm"/>
						</div>
						
						<!-- Add any toolbars -->
						<xsl:if test="responseDetails/comptoolbar!=''">
							<xsl:for-each select="responseDetails/comptoolbar/toolbars">
								<xsl:apply-templates select="."/>
							</xsl:for-each>
						</xsl:if>
						<input type="hidden" name="EnquiryDataScroll" id="EnquiryDataScroll">
                                      <xsl:attribute name="value"><xsl:copy-of select="$EnquiryDataScroll"/></xsl:attribute>
                        </input>
					</xsl:element>									
					
				</xsl:otherwise>
			</xsl:choose>
			
			<script type="text/javascript">Logger.getLogger().timeEnd("LOAD_MAIN");</script>	
			<!-- Since all.js having these scripts, add only for browser -->
			<script type="text/javascript">	<xsl:attribute name="src">../scripts/ARC/tools/dhtmlHistory.js</xsl:attribute></script>
					                                
		</html>
	</xsl:template>

	<xsl:template match="pane">
		<xsl:call-template name="pane_n"/>
	</xsl:template>

	<xsl:template match="toolbars">
		<xsl:call-template name="toolbars_n">
			<xsl:with-param name="Comptoolbar" select="$Comptoolbar"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="frames">
		<xsl:choose>
            <xsl:when test="$noFrames = 'true'">
				<!-- Use table layout in place of a Frameset -->
				<xsl:call-template name="frames_as_tables"/>
			</xsl:when>
			<xsl:otherwise>	
				<!-- Use frameset to render frames -->
				<xsl:call-template name="frames_n"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="panes">
		<xsl:apply-templates select="pane"/>
	</xsl:template>
	<xsl:variable name="application" select="string(/responseDetails/window/panes/pane/contract/app)"/>
	<xsl:variable name="version" select="string(/responseDetails/window/panes/pane/contract/version)"/>
	
	<xsl:variable name="enquiry"> 
		<xsl:choose>
			<xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/enqid"/>
			</xsl:when>
			<xsl:when test="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname!=''">
				<xsl:value-of select="/responseDetails/window/panes/pane/selSection/selDets/enqsel/enqname"/>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:template match="apply_Style" name="apply_Style">
		<xsl:param name="actualclass"/>
		<xsl:choose>
			<xsl:when test="$application!=''">
				<xsl:call-template name="Version-Styling">   
					<xsl:with-param name="application" select="$application"/>
					<xsl:with-param name="version" select="$version"/>
					<xsl:with-param name="actualclass" select="$actualclass"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="Enquiry-Styling">
					<xsl:with-param name="enquiry" select="$enquiry"/>
					<xsl:with-param name="actualclass" select="$actualclass"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	 <xsl:template name="ToLower">
		<xsl:param name="inputString"/>
		<xsl:variable name="smallCase" select="'abcdefghijklmnopqrstuvwxyz'"/>
		<xsl:variable name="upperCase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
		<xsl:value-of select="translate($inputString,$upperCase,$smallCase)"/>
	 </xsl:template>
	 <xsl:template name="setLanguage">
	    <xsl:param name="language"/>
	    <xsl:choose>
			<xsl:when test="$language='GB'">en</xsl:when>
			<xsl:otherwise>
			<xsl:call-template name="ToLower">
				<xsl:with-param name="inputString" select="$language"/>
			</xsl:call-template>		
			</xsl:otherwise>
		</xsl:choose>
	 </xsl:template>
</xsl:stylesheet>
