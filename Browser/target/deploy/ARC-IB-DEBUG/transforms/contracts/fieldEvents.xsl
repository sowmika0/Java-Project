<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:template name="fieldEvents">
    
    <!--  ********************************************************************************************* -->
    <!--  WHEN CHANGING THIS TEMPLATE PLEASE MAKE SURE THAT THE CHANGES ARE REPLICATED IN fieldEvents1. -->
    <!--  JUST BELOW THIS TEMPLATE. fieldEvents1 GETS CALLED ONLY FOR RADIO BUTTON FIELDS.              -->
    <!--  ********************************************************************************************* -->
    
		<!-- Set the field attributes depending on whether it is a hot field or auto field, etc -->
		<!-- Set up field variables - default attributes to N (No) -->
		<xsl:variable name="isHot">
		  <xsl:if test="./hotField='Y' and not (/responseDetails/userDetails/hotsAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isHotVal">
		  <xsl:if test="./hotVal='Y' and not (/responseDetails/userDetails/hotsAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isWebVal">
			<xsl:if test="./wvf='Y' and not (/responseDetails/userDetails/webValAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isAutoEnq">
			<xsl:if test="./autoLaunchEnq!='' and not (/responseDetails/userDetails/autosAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isCheckFileEnri">
			<xsl:if test="./cfe and not(/responseDetails/userDetails/cferiAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="isFrequency">
			<xsl:if test="((./popup='frequency') or (./popup='recurrence')) and not(/responseDetails/userDetails/cferiAllowed)">
				<xsl:value-of select="'Y'"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="isCaseConv">
			<xsl:if test="./CaseConv!=''">
		 		<xsl:value-of select="./CaseConv"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:if test="./CaseConv!=''">
			<xsl:attribute name="CaseConv"><xsl:value-of select="./CaseConv"/></xsl:attribute>
		</xsl:if>

		<xsl:if test="./ebr='Y'">
			<xsl:attribute name="ebr">Y</xsl:attribute>
		</xsl:if>

		<xsl:if test="($isCheckFileEnri='Y') or ($isFrequency='Y')">
			<xsl:attribute name="checkFile">Y</xsl:attribute>
			<xsl:attribute name="vr"><xsl:value-of select="vr" /></xsl:attribute>
			<xsl:attribute name="autocomplete">off</xsl:attribute>		
		</xsl:if>
		<xsl:if test="$isHot='Y'">
			<xsl:attribute name="hot">Y</xsl:attribute>
			<xsl:attribute name="autocomplete">off</xsl:attribute>
		</xsl:if>
		
		<xsl:if test="$isHotVal='Y'">
			<xsl:attribute name="hot">Y</xsl:attribute>
			<xsl:attribute name="hotVal">Y</xsl:attribute>
			<xsl:attribute name="autocomplete">off</xsl:attribute>
		</xsl:if>
		
		<xsl:if test="$isWebVal='Y'">
			<xsl:attribute name="webVal">Y</xsl:attribute>
			<xsl:attribute name="vr"><xsl:value-of select="vr" /></xsl:attribute>
			<xsl:attribute name="autocomplete">off</xsl:attribute>
		</xsl:if>
		
		<xsl:if test="$isAutoEnq='Y'">
			<xsl:attribute name="auto">Y</xsl:attribute>
			<xsl:attribute name="autoEnqName"><xsl:value-of select="autoLaunchEnq" /></xsl:attribute>
			<xsl:attribute name="autocomplete">off</xsl:attribute>
		</xsl:if>
		
		<!-- Set the field events depending on whether it is a hot field or auto field, etc - allow multiple events for fields with more than one attribute -->
		<xsl:choose>
			<xsl:when test="($isHot = 'Y') or ($isHotVal = 'Y') or ($isWebVal = 'Y') or ($isAutoEnq = 'Y') or ($isCheckFileEnri = 'Y') or ($isCaseConv!='') or ($isFrequency = 'Y')">
				<xsl:attribute name="onChange">FragmentUtil.formChangeHandler(); FragmentUtil.fieldChangeHandler( event); doFieldChangeEvent(event);</xsl:attribute>
				<xsl:attribute name="onBlur">FragmentUtil.fieldChangeHandler(event);</xsl:attribute>
				<xsl:attribute name="onFocus">doFieldFocusEvent(event)</xsl:attribute>
				<xsl:attribute name="onKeyUp">invokeHelp(event,'<xsl:value-of select="fn"/>');</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="onChange">clearEnrichment(event); FragmentUtil.formChangeHandler(); FragmentUtil.fieldChangeHandler( event);</xsl:attribute>
         			<xsl:attribute name="onBlur">clearEnrichment(event); FragmentUtil.formChangeHandler(); FragmentUtil.fieldChangeHandler(event);</xsl:attribute>
				<xsl:attribute name="enriFieldName">enri_<xsl:value-of select="fn" /><xsl:value-of select="in" /></xsl:attribute>
				<xsl:attribute name="onKeyUp">invokeHelp(event,'<xsl:value-of select="fn"/>');</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>		

		<!-- Add the helptext event -->
		<xsl:if test="$stripFrameToolbars = 'false'">
		   <xsl:attribute name="ondblclick">javascript:help('<xsl:value-of select="fn"/>')</xsl:attribute>
        </xsl:if>
	</xsl:template>


  <xsl:template name="fieldEvents1">

    <!-- Set the field attributes depending on whether it is a hot field or auto field, etc -->
    <!-- Set up field variables - default attributes to N (No) -->
    <xsl:variable name="isHot">
      <xsl:if test="../../hotField='Y' and not (/responseDetails/userDetails/hotsAllowed)">
        <xsl:value-of select="'Y'"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="isHotVal">
      <xsl:if test="../../hotVal='Y' and not (/responseDetails/userDetails/hotsAllowed)">
        <xsl:value-of select="'Y'"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="isWebVal">
      <xsl:if test="../../wvf='Y' and not (/responseDetails/userDetails/webValAllowed)">
        <xsl:value-of select="'Y'"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="isAutoEnq">
      <xsl:if test="../../autoLaunchEnq!='' and not (/responseDetails/userDetails/autosAllowed)">
        <xsl:value-of select="'Y'"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="isCheckFileEnri">
      <xsl:if test="../../cfe and not(/responseDetails/userDetails/cferiAllowed)">
        <xsl:value-of select="'Y'"/>
      </xsl:if>
    </xsl:variable>

    <xsl:variable name="isCaseConv">
      <xsl:if test="../../CaseConv!=''">
        <xsl:value-of select="../../CaseConv"/>
      </xsl:if>
    </xsl:variable>

    <xsl:if test="../../CaseConv!=''">
      <xsl:attribute name="CaseConv">
        <xsl:value-of select="../../CaseConv"/>
      </xsl:attribute>
    </xsl:if>

    <xsl:if test="../../ebr='Y'">
      <xsl:attribute name="ebr">Y</xsl:attribute>
    </xsl:if>

    <xsl:if test="$isCheckFileEnri='Y'">
      <xsl:attribute name="checkFile">Y</xsl:attribute>
      <xsl:attribute name="vr">
        <xsl:value-of select="vr" />
      </xsl:attribute>
      <xsl:attribute name="autocomplete">off</xsl:attribute>
    </xsl:if>
    <xsl:if test="$isHot='Y'">
      <xsl:attribute name="hot">Y</xsl:attribute>
      <xsl:attribute name="autocomplete">off</xsl:attribute>
    </xsl:if>

    <xsl:if test="$isHotVal='Y'">
      <xsl:attribute name="hot">Y</xsl:attribute>
      <xsl:attribute name="hotVal">Y</xsl:attribute>
      <xsl:attribute name="autocomplete">off</xsl:attribute>
    </xsl:if>

    <xsl:if test="$isWebVal='Y'">
      <xsl:attribute name="webVal">Y</xsl:attribute>
      <xsl:attribute name="vr">
        <xsl:value-of select="vr" />
      </xsl:attribute>
      <xsl:attribute name="autocomplete">off</xsl:attribute>
    </xsl:if>

    <xsl:if test="$isAutoEnq='Y'">
      <xsl:attribute name="auto">Y</xsl:attribute>
      <xsl:attribute name="autoEnqName">
        <xsl:value-of select="autoLaunchEnq" />
      </xsl:attribute>
      <xsl:attribute name="autocomplete">off</xsl:attribute>
    </xsl:if>

    <!-- Set the field events depending on whether it is a hot field or auto field, etc - allow multiple events for fields with more than one attribute -->
    <xsl:choose>
      <xsl:when test="($isHot = 'Y') or ($isHotVal = 'Y') or ($isWebVal = 'Y') or ($isAutoEnq = 'Y') or ($isCheckFileEnri = 'Y') or ($isCaseConv!='')">
        <xsl:attribute name="onclick">
          javascript:doFieldChangeEvent(event)
        </xsl:attribute>
        <xsl:attribute name="onFocus">
          javascript:doFieldFocusEvent(event)
        </xsl:attribute>
        <xsl:attribute name="onKeyUp">invokeHelp(event,'<xsl:value-of select="fn"/>');</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="onChange">
          javascript:clearEnrichment(event);
        </xsl:attribute>
        <xsl:attribute name="onKeyUp">invokeHelp(event,'<xsl:value-of select="fn"/>');</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>

    <!-- Add the helptext event -->
	<xsl:if test="$stripFrameToolbars = 'false'">
       <xsl:attribute name="ondblclick">javascript:help('<xsl:value-of select="fn"/>')</xsl:attribute>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
