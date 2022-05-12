<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
								
<xsl:template match="/">

<HTML>
<HEAD>

  <SCRIPT language="Javascript">
  function mpPopulate()
  {
  	<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/mpPie/mpSlice">
  		<xsl:choose>
  			<xsl:when test="position() != last()">
  			window.mpAddChartValue(<xsl:value-of select="mpAmount" />, "<xsl:value-of select="mpLabel" />",false, "<xsl:value-of select="mpLevel"/>");
  				</xsl:when>
  			<xsl:otherwise>
  			window.mpAddChartValue(<xsl:value-of select="mpAmount" />, "<xsl:value-of select="mpLabel" />",true, "<xsl:value-of select="mpLevel"/>");
	  		</xsl:otherwise>
  		</xsl:choose>
	  </xsl:for-each>

          <xsl:for-each select="/responseDetails/window/panes/pane/message">
		<xsl:for-each select="msg">
			window.addMessage("<xsl:value-of select="." />");
	  	</xsl:for-each>
	  </xsl:for-each>

   }
    
  </SCRIPT>
</HEAD>
<BODY onload="mpPopulate()">

<EMBED src="../plaf/chart/mpChart.svg" type="image/svg+xml" width="500" height="290" pluginspage="http://www.adobe.com/svg/viewer/install/"></EMBED>

</BODY>

</HTML>

</xsl:template>
</xsl:stylesheet>
