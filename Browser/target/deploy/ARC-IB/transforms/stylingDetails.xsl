<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- User defined styles will be override default styles when style properly ordered in custom.css -->
<!-- Template for enquiry styling by default styles along with user defined styles. -->
	<xsl:template match= "Enquiry-Styling" name="Enquiry-Styling">
		<xsl:param name="enquiry"/>
		<xsl:param name="actualclass"/>
		 
		   <xsl:choose>
				<xsl:when test="contains(normalize-space($actualclass), ' ')">
					 <xsl:call-template name="multipleclassesenq">
						 <xsl:with-param name="string" select="$actualclass"/> 
					</xsl:call-template> 	
				</xsl:when>
				 <xsl:otherwise>
				 <xsl:variable name="actualStyle">
					<xsl:choose>
					<xsl:when test="$enquiry!=''">
						<xsl:value-of select="$actualclass"/><xsl:text> </xsl:text><xsl:value-of select="$actualclass"/>_<xsl:value-of select="translate($enquiry,'%.,-','E')" />
					</xsl:when>
					<!-- Rest of the thing goes here as actualclass -->
					<xsl:otherwise>
						<xsl:value-of select="$actualclass"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:variable> 
				<xsl:value-of select="$actualStyle"/>
				</xsl:otherwise> 
			 </xsl:choose> 
		<!-- Return the stored class name variable -->
		
	</xsl:template>
	
	
	<!-- Template for version styling by default styles along with user defined styles. -->
	<xsl:template match= "Version-Styling" name="Version-Styling">
	<!-- Tempate incoming parameters are application name, version name, actual style class name -->
		<xsl:param name="application"/>
		<xsl:param name="version"/>
		<xsl:param name="actualclass"/>
		<xsl:variable name="application">
		  <xsl:value-of select="translate($application,'.,-','')"/>
		</xsl:variable>
		<xsl:variable name="version">
		  <xsl:value-of select="translate($version,'.,-','')"/>
		</xsl:variable>
		<!-- Have class name in a variable -->
		<xsl:choose>
			<xsl:when test="contains(normalize-space($actualclass), ' ')">
				 <xsl:call-template name="multipleclasses">
					<xsl:with-param name="string" select="$actualclass"/>
				</xsl:call-template> 
			</xsl:when>
		    <xsl:otherwise>
			<xsl:variable name="actualStyle">
				<xsl:choose>
					<!-- If version is present then add the class name as the following format -->
					<!-- actualclass actualclass_application actualclass_application_version -->
					<xsl:when test="$version!=''">					
						<xsl:value-of select="$actualclass"/><xsl:text> </xsl:text><xsl:value-of select="$actualclass"/>_<xsl:value-of select="$application"/><xsl:text> </xsl:text><xsl:value-of select="$actualclass"/>_<xsl:value-of select="$application"/>_<xsl:value-of select="$version"/>
					</xsl:when>
					<!-- If application present and version not present then the format -->
					<!-- actualclass actualclass_application -->
					<xsl:when test="$application!=''">
						<xsl:value-of select="$actualclass"/><xsl:text> </xsl:text><xsl:value-of select="$actualclass"/>_<xsl:value-of select="$application"/>
					</xsl:when>
					<!-- Rest of the thing goes here as actualclass -->
					<xsl:otherwise>
						<xsl:value-of select="$actualclass"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable> 
			<!-- Return the stored class name variable -->
			<xsl:value-of select="$actualStyle"/>
		   </xsl:otherwise>
		</xsl:choose>
	 </xsl:template>
	
	 <xsl:template name="multipleclasses">  	
	<xsl:param name="string"/> 	
	 <xsl:choose>
		<xsl:when test='contains($string, " ")'>
			 <xsl:variable name="styleValue">
				 <xsl:choose>	
				 <xsl:when test="$version!=''">					
					<xsl:value-of select="substring-before($string,' ')" /><xsl:text> </xsl:text><xsl:value-of select="substring-before($string,' ')" />_<xsl:value-of select="$application"/><xsl:text> </xsl:text><xsl:value-of select="substring-before($string,' ')" />_<xsl:value-of select="$application"/>_<xsl:value-of select="$version"/>
				 </xsl:when>	
				<xsl:when test="$application!=''">
					<xsl:value-of select="substring-before($string,' ')" /><xsl:text> </xsl:text><xsl:value-of select="substring-before($string,' ')" />_<xsl:value-of select="$application"/>
				</xsl:when>	
				<xsl:otherwise>
					<xsl:value-of select="substring-before($string,' ')" />
				</xsl:otherwise> 
				</xsl:choose> 
			</xsl:variable>
			<xsl:text> </xsl:text><xsl:value-of select="$styleValue"/> 
		</xsl:when> 
		 <xsl:when test='$string !=""'>
		
			 <xsl:variable name="styleValue">
				 <xsl:choose>
				 <xsl:when test="$version!=''">					
					<xsl:value-of select="$string"/><xsl:text> </xsl:text><xsl:value-of select="$string"/>_<xsl:value-of select="$application"/><xsl:text> </xsl:text><xsl:value-of select="$string"/>_<xsl:value-of select="$application"/>_<xsl:value-of select="$version"/>
				 </xsl:when> 	
				<xsl:when test="$application!=''">
					<xsl:value-of select="$string"/><xsl:text> </xsl:text><xsl:value-of select="$string" />_<xsl:value-of select="$application"/>
				</xsl:when>	
				<xsl:otherwise>
					<xsl:value-of select="$string"/>
				</xsl:otherwise> 
				</xsl:choose> 
			</xsl:variable>
			<xsl:text> </xsl:text><xsl:value-of select="$styleValue"/> 
		</xsl:when>   
		
	</xsl:choose>
	<xsl:if test='$string != ""'>
		<xsl:call-template name="multipleclasses">
			<xsl:with-param name="string"
                            select="substring-after($string,' ')" />
		</xsl:call-template>
	</xsl:if> 
	 </xsl:template>  
	 
	 <xsl:template name="multipleclassesenq">
		<xsl:param name="string"/> 
		<!-- <xsl:if test="not(starts-with($string,' '))"> -->
		<xsl:choose>
		 <xsl:when test='contains($string, " ")'>
			  <xsl:variable name="styleValue">
				 <xsl:choose> 
				 <xsl:when test="$enquiry!=''">
					<xsl:value-of select="substring-before($string,' ')" /><xsl:text> </xsl:text><xsl:value-of select="substring-before($string,' ')" />_<xsl:value-of select="translate($enquiry,'%.,-','E')" />
				</xsl:when> 	
				<xsl:otherwise>
					<xsl:value-of select="substring-before($string,' ')" />
				</xsl:otherwise> 
				</xsl:choose>
			</xsl:variable> 
				<xsl:text> </xsl:text><xsl:value-of select="$styleValue"/>
		</xsl:when> 
		<xsl:when test='$string !=""'>
			<xsl:variable name="styleValue">
				<xsl:choose> 
				 <xsl:when test="$enquiry!=''">
					<xsl:value-of select="$string"/><xsl:text> </xsl:text><xsl:value-of select="$string" />_<xsl:value-of select="translate($enquiry,'%.,-','E')" />
				</xsl:when> 	
				<xsl:otherwise>
					<xsl:value-of select="$string" />
				</xsl:otherwise> 
				</xsl:choose>
			</xsl:variable> 
				<xsl:text> </xsl:text><xsl:value-of select="$styleValue"/>
		</xsl:when>
		</xsl:choose>
			<xsl:if test='$string != ""'>
				<xsl:call-template name="multipleclassesenq">
					<xsl:with-param name="string"
                            select="substring-after($string,' ')" />
				</xsl:call-template>
			</xsl:if> 
	 </xsl:template> 	
</xsl:stylesheet>


<!-- These templates can be used for custom xsl templates also and use the following syntax.
	 param name is case sensitive and param inputs are mantatory.
For Version,
	<xsl:call-template name="Version-Styling">
		<xsl:with-param name="application" select="application name"/>
		<xsl:with-param name="version" select="version name"/>
		<xsl:with-param name="actualclass" select="actualclass name"/>
	</xsl:call-template>
	
For Enquiry,
	<xsl:call-template name="Enquiry-Styling">
		<xsl:with-param name="enquiryName" select="enquiry name"/>
		<xsl:with-param name="actualclass" select="actualclass name"/>
	</xsl:call-template>
-->
