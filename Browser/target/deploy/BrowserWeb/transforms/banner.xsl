<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="formlaunch.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin">
		<xsl:value-of select="/responseDetails/userDetails/skin"/>
	</xsl:variable>
	<xsl:variable name="companyid">
		<xsl:value-of select="/responseDetails/userDetails/companyId"/>
	</xsl:variable>
	<xsl:template match="banner" name="banner_n">
		<div class="banner">
		
			<xsl:call-template name="formlaunch"/>
			<span class="bannertxt">
				<xsl:for-each select="txt">
					<xsl:value-of select="."/><xsl:text> </xsl:text>
				</xsl:for-each>
			</span>

	           <br>
				<xsl:for-each select="toolbars">
					<xsl:call-template name="toolbars_n"/>
				</xsl:for-each>
		       </br>  
		
			</div>
			
			<div>
	
				<xsl:for-each select="cmdline">
					<xsl:call-template name="cmdline_n"/>
				</xsl:for-each>
				
			</div>
		
	</xsl:template>
	<xsl:template name="cmdline_n">
		<form id="commandLineForm{$formFragmentSuffix}" name="commandLineForm" class="commandline" action="javascript:docommandLine()" autocomplete="{/responseDetails/userDetails/autoComplete}">
			<input title="Command line for input text" name="commandValue" id="commandValue" size="35" tabindex="0"/>
			<a onclick="javascript:docommandLine()" href="javascript:void(0)" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);" title="Go">
				<img id="cmdline_img" src="../plaf/images/{$skin}/menu/go.gif" alt="Go" title="Go" />
			</a>
		</form>
	</xsl:template>
</xsl:stylesheet>
