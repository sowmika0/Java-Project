<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:import href="tabIndex.xsl"/>
	<xsl:import href="webDetails.xsl"/>

	<xsl:variable name="user">
       		<xsl:choose>
       			<!-- {user_id} will be sent to the client in place of the real user name, and replaced with the user name in the servlet filter -->
           		<xsl:when test="responseDetails/userDetails/stripUser='true'">{user_id}</xsl:when>
           		<xsl:otherwise><xsl:value-of select="/responseDetails/userDetails/user"/></xsl:otherwise>
       		</xsl:choose>
  	</xsl:variable>
  	
	<xsl:template name="userDetails">
		<input type="hidden" name="allowResize" id="allowResize">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/allowResize"/></xsl:attribute>
		</input>
		<input type="hidden" name="companyId" id="companyId">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/companyId"/></xsl:attribute>
		</input>
		<input type="hidden" name="company" id="company">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/company"/></xsl:attribute>
		</input>				
		<input type="hidden" name = "user" id="user">
			<xsl:attribute name="value"><xsl:value-of select="$user"/></xsl:attribute>
		</input>
        <input type="hidden" name = "userRole" id="userRole">
            <xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/userRole"/></xsl:attribute>
        </input>
		<input type="hidden" name="transSign" id="transSign">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/transSign"/></xsl:attribute>
		</input>
		<input type="hidden" name = "skin" id="skin">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/skin"/></xsl:attribute>
		</input>
		<input type="hidden" name = "today" id="today">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/today"/></xsl:attribute>
		</input>
		<input type="hidden" name = "release" id="release">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/release"/></xsl:attribute>
		</input>
		<input type="hidden" name = "compScreen" id="compScreen">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/compScreen"/></xsl:attribute>
		</input>
		<input type="hidden" name = "reqTabid" id="reqTabid">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/reqTabid"/></xsl:attribute>
		</input>
		<input type="hidden" name = "compTargets" id="compTargets">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/compTargets"/></xsl:attribute>
		</input>
		<input type="hidden" name = "attribframes" id="attribframes">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/attribframes"/></xsl:attribute>
		</input>
		<input type="hidden" name = "EnqParentWindow" id="EnqParentWindow">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/EnqParentWindow"/></xsl:attribute>
		</input>		
		<input type="hidden" name = "timing" id="timing">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/time"/></xsl:attribute>
		</input>
		<input type="hidden" name = "pwprocessid" id="pwprocessid">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/pwprocessid"/></xsl:attribute>
		</input>
		<input type="hidden" name = "language" id="language">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/lng"/></xsl:attribute>
		</input>
		<input type="hidden" name = "languages" id="languages">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/lngs"/></xsl:attribute>
		</input>
		<input type="hidden" name="savechanges" id="savechanges">
   			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/SaveChanges"/></xsl:attribute>
		</input>
		<input type="hidden" name = "staticId" id="staticId">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/staticId"/></xsl:attribute>
		</input>
		<input type="hidden" name = "lockDateTime" id="lockDateTime">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/lockDateTime"/></xsl:attribute>
		</input>
		<!-- Add hidden field ti inidicate if this version of T24 supports popup dropdowns and calendars -->
		<xsl:if test="/responseDetails/userDetails/popupDropDown">
			<input type="hidden">
				<xsl:attribute name="name">popupDropDown</xsl:attribute>
				<xsl:attribute name="id">popupDropDown</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/popupDropDown"/></xsl:attribute>
			</input>	
		</xsl:if>

		<input type="hidden" name="allowcalendar" id="allowcalendar">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/allowcalendar"/></xsl:attribute>
		</input>

		<input type="hidden" name="allowdropdowns" id="allowdropdowns">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/allowdropdowns"/></xsl:attribute>
		</input>
		<input type="hidden" name="allowcontext" id="allowcontext">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/allowcontext"/></xsl:attribute>
		</input>

		<input type="hidden" name="nextStage" id="nextStage">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/nextStage"/></xsl:attribute>
		</input>
		
		<input type="hidden" name="maximize" id="maximize" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/maximize" /></xsl:attribute>
		</input>
		
		<input type="hidden" name="showStatusInfo" id="showStatusInfo" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/showStatusInfo" /></xsl:attribute>
		</input>
		
		<input type="hidden" name="languageUndefined" id="languageUndefined">
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/translations/languageUndefined"/></xsl:attribute>
		</input>
		
		<input type="hidden" name="expandMultiString" id="expandMultiString" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/translations/expandMulti" /></xsl:attribute>
		</input>

		<input type="hidden" name="deleteMultiString" id="deleteMultiString" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/translations/deleteMulti" /></xsl:attribute>
		</input>
		
		<input type="hidden" name="expandSubString" id="expandSubString" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/window/translations/expandSub" /></xsl:attribute>
		</input>
		
		<!-- If the server supports client side expansion then add this flag to let the client know. -->
		<xsl:if test="/responseDetails/userDetails/clientExpansion">		
			<input type="hidden" name="clientExpansion" id="clientExpansion" >
				<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/clientExpansion" /></xsl:attribute>
			</input>
		</xsl:if>

		<input type="hidden" name="showReleaseInfo" id="showReleaseInfo" >
			<xsl:attribute name="value"><xsl:value-of select="/responseDetails/userDetails/showReleaseInfo" /></xsl:attribute>
		</input>
		
		<xsl:call-template name="webDetails"/>
		<xsl:call-template name="tabIndexElements"/>
		
	</xsl:template>
	
</xsl:stylesheet>
