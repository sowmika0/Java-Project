<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:import href="userDetails.xsl"/>
<xsl:import href="stylingDetails.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>
	
	<!-- Extract variable to see if we have popup calenders as opposed to window calenders -->
	<xsl:variable name="popupDropDown"><xsl:value-of select="/responseDetails/userDetails/popupDropDown"/></xsl:variable>

	<!-- Get the dropfield ID - this may be prefixed with '|' + the APPLICATION name.  So get the part before the '|' -->
	<xsl:variable name="df">
		<xsl:choose>
			<xsl:when test="contains(/responseDetails/freqControl/df,'|')"><xsl:value-of select="substring-before(/responseDetails/freqControl/df,'|')"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="/responseDetails/freqControl/df"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
				<!-- Include the required stylesheets - using a skin version if specified -->
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href">../plaf/style/<xsl:copy-of select="$skin"/>/general.css</xsl:attribute>
				</link>

				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				
				<script type="text/javascript" src="../scripts/general.js" />
				<script type="text/javascript" src="../scripts/dropdown.js" />
				<script type="text/javascript" src="../scripts/ARC/T24_constants.js" />
				<script type="text/javascript" src="../scripts/ARC/Logger.js" />
				<script type="text/javascript" src="../scripts/ARC/Fragment.js" />
				<script type="text/javascript" src="../scripts/ARC/FragmentEvent.js" />
				<script type="text/javascript" src="../scripts/ARC/FragmentUtil.js" />

				<title>
					<xsl:value-of select="responseDetails/freqControl/recurTrans/rTitle"/>
				</title>
			</head>
			<body>
				<xsl:attribute name="onload">initFrequency('<xsl:value-of select="$df"/>','<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>');</xsl:attribute>
				<xsl:call-template name="userDetails"/>
				<xsl:for-each select="responseDetails/freqControl">	
     	<div id="fqu:container"> 
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'customBackground'"/>
				</xsl:call-template>
			</xsl:attribute>
       		<div id="fqu:toolbar_icons">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'customImage'"/>
					</xsl:call-template>
				</xsl:attribute>
       			<table style="width:100%; height:20px;">
       			
       				<tr style="border-bottom:solid 1px #d9d9d9;">
       					<td>
							<a id ="Frequency_Pattern" tabindex="0">
			       				<!-- Frequency Pattern  -->
			       				<xsl:value-of select="recurTrans/rFquTitle"/>
							</a>
       					</td>
       					<td align="right">
							<a>
								<xsl:attribute name="tabindex">-1</xsl:attribute>
								<xsl:attribute name="onmousedown">javascript:fqu_saveDate('<xsl:value-of select="$df"/>')</xsl:attribute>
								<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
									<xsl:attribute name="onkeypress">fqu_saveDateonKey('<xsl:value-of select="$df"/>',event);</xsl:attribute>
									<xsl:attribute name="tabindex">0</xsl:attribute>
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'ascell'"/>
											</xsl:call-template>
										</xsl:attribute>
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/save.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="recurTrans/rOk"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="recurTrans/rOk"/></xsl:attribute>
								</img>
							</a>				
							<a>
								<xsl:attribute name="tabindex">-1</xsl:attribute>
								<xsl:attribute name="onmousedown">javascript:fqu_close('<xsl:value-of select="$df"/>')</xsl:attribute>
								<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
									<xsl:attribute name="onkeypress">fqu_closeonKey('<xsl:value-of select="$df"/>',event);</xsl:attribute>
									<xsl:attribute name="tabindex">0</xsl:attribute>
										<xsl:attribute name="class">
											<xsl:call-template name="apply_Style">
													<xsl:with-param name="actualclass" select="'ascell'"/>
											</xsl:call-template>
										</xsl:attribute>
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/cancel.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="recurTrans/rCancel"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="recurTrans/rCancel"/></xsl:attribute>
								</img>
							</a>       					
       					</td>
       				</tr>
       			</table>
	       	</div> <!-- id="fqu:toolbar_icons" -->
	       	<!-- A firefox bug means we need to set the position otherwise the cursor disappears! -->
	       	<xsl:if test="recurTrans/rnDate">
		       	<div id="next_date" style="display:inline; position:relative;">
		       		<p>
				       	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		   				<!-- Next Date  -->
		   				<xsl:value-of select="recurTrans/rnDate"/>:
	
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						<input type="text" id="fqu:nextDate" value="" size="12"></input>
	
						<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/calendar"/></xsl:attribute>
						<img onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'dropdown_button'"/>
								</xsl:call-template>
							</xsl:attribute>
							<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/calendar"/></xsl:attribute>
							<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/calendar.gif</xsl:attribute>
							<xsl:attribute name="calendardropfieldname">fqu:nextDate</xsl:attribute>
							<xsl:attribute name="onClick">
								<xsl:choose>
									<xsl:when test="$popupDropDown='true'">javascript:dropDownCalendar(event);</xsl:when>
									<xsl:otherwise>javascript:calendar('fqu:nextDate');</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:attribute name="onkeypress">
								<xsl:choose>
									<xsl:when test="$popupDropDown='true'">javascript:dropDownCalendar(event);</xsl:when>
									<xsl:otherwise>javascript:calendar('fqu:nextDate');</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:attribute name="tabindex">0</xsl:attribute>						
						</img>
					</p>
					<hr/>
		       	</div>
	       	</xsl:if>

	       	<div id="fqu_pattern">

       			<table>
       				<tr padding="10">
       					<td style="border-right:solid 1px #d9d9d9;padding:3px;">
 				       		<div id="fqu:frequency_pattern">
 				       			<xsl:if test="recurTrans/rNone">
					       			<p>
					       				<label for="fqu:frequency:N"><input type="radio" name="fqu:frequency" id="fqu:frequency:N" value="N" onclick="fqu_swicthTab('N');"/>
					       					<!-- None -->
					       					<xsl:value-of select="recurTrans/rNone"/>
					       				</label>
					       			</p>
					       		</xsl:if>
				       			<p>
				       				<label for="fqu:frequency:D"><input type="radio" name="fqu:frequency" id="fqu:frequency:D" value="D" onclick="fqu_swicthTab('D');"/>
				       					<!-- Daily -->
				       					<xsl:value-of select="recurTrans/rDaily"/>
				       				</label>
				       			</p>
								<p>
									<label for="fqu:frequency:W"><input type="radio" name="fqu:frequency" id="fqu:frequency:W" value="W" onclick="fqu_swicthTab('W');"/>
										<!-- Weekly -->
				       					<xsl:value-of select="recurTrans/rWeekly"/>										
									</label>
								</p>
								<p>
									<label for="fqu:frequency:M"><input type="radio" name="fqu:frequency" id="fqu:frequency:M" value="M" onclick="fqu_swicthTab('M');"/>
										<!-- Monthly -->
				       					<xsl:value-of select="recurTrans/rMonthly"/>										
									</label>
								</p>
								<p>
									<label for="fqu:frequency:P"><input type="radio" name="fqu:frequency" id="fqu:frequency:P" value="P" onclick="fqu_swicthTab('P');"/>
										<!-- Defined -->
				       					<xsl:value-of select="recurTrans/rPreDef"/>
									</label>
								</p>								
				       		</div>       						
       					</td>
      					<td>
							<div id="fqu:frequency_detail">
								<div id="FQU_Invalid_Format" style="display:none;">
									<a>
										<img>
												<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'ascell'"/>
													</xsl:call-template>
												</xsl:attribute>
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/recurr_err.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/recurrerror"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrerror"/></xsl:attribute>
										</img>
										<!-- Invalid Recurring Date Format. Select a new value. -->
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<xsl:value-of select="recurTrans/rInv"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									</a>
								</div>	<!-- Invalid_Format -->						
							
								<div id="Pattern_Details:D" style="display:none;">
									<!-- 2 Radio buttons: Daily & Business Days -->
									<p>
				       				<label>
				       					<input type="radio" name="fqu:D:radio" id="fqu:Daily" value="DAILY"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)"/>
				       					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				       					<!-- Daily -->
				       					<xsl:value-of select="recurTrans/rDaily"/>
									</label>									
									</p>
									<p>
				       				<label>
				       					<input type="radio" name="fqu:D:radio" id="fqu:BusinessDay" value="BSNSS"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)"/>
				       					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				       					<!-- Business Day -->
				       					<xsl:value-of select="recurTrans/rBDay"/>
									</label>
									</p>
								</div> <!-- Pattern_Details:D -->

								<div id="Pattern_Details:W" style="display:none;">
									<p>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Every -->
										<xsl:value-of select="recurTrans/rEvery"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<select id="fqu:weekly"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)">
											<option selected="true" value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
										</select>											
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Weeks(s) -->
										<xsl:value-of select="recurTrans/rWeeks"/>									
									</p>								
								</div> <!-- Pattern_Details:W -->
								
								<div id="Pattern_Details:M" style="display:none;">
									<p>
					       				<label>
					       					<input type="radio" name="fqu:M:radio" id="fqu:M:Twice" value="TWMTH"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)"/>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<!-- Twice Monthly -->	
											<xsl:value-of select="recurTrans/rTMonthly"/>
										</label>									
									</p>
									<p>
										<label>
					       					<input type="radio" name="fqu:M:radio" id="fqu:M:Every" value="EVERY"/>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<!-- Every -->
											<xsl:value-of select="recurTrans/rEvery"/>
										</label>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<input type="text" id="fqu:M:MonthNumber"  value="01" size="2">
											<xsl:attribute name="class">
													<xsl:call-template name="apply_Style">
														<xsl:with-param name="actualclass" select="'scheduler'"/>
													</xsl:call-template>
											</xsl:attribute>
											<xsl:attribute name="onKeyDown">
												javascript:fqu_ValidateMonthInput('fqu:M:MonthNumber', 2)
											</xsl:attribute>
											<xsl:attribute name="onKeyUp">
												javascript:fqu_ValidateMonthInput('fqu:M:MonthNumber', 2)
											</xsl:attribute>																									
										</input>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Months -->
										<xsl:value-of select="recurTrans/rMonths"/>												
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- On -->
										<xsl:value-of select="recurTrans/rOn"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<select id="fqu:M:DayNumber"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)">
											<option selected="true" value="01">1</option>
											<option value="02">2</option>
											<option value="03">3</option>
											<option value="04">4</option>
											<option value="05">5</option>
											<option value="06">6</option>
											<option value="07">7</option>
											<option value="08">8</option>
											<option value="09">9</option>
											<option value="10">10</option>
											<option value="11">11</option>
											<option value="12">12</option>
											<option value="13">13</option>
											<option value="14">14</option>
											<option value="15">15</option>
											<option value="16">16</option>
											<option value="17">17</option>
											<option value="18">18</option>
											<option value="19">19</option>
											<option value="20">20</option>
											<option value="21">21</option>
											<option value="22">22</option>
											<option value="23">23</option>
											<option value="24">24</option>
											<option value="25">25</option>
											<option value="26">26</option>
											<option value="27">27</option>
											<option value="28">28</option>
											<option value="29">29</option>
											<option value="30">30</option>
											<option value="31">31</option>
										</select>										
									</p>
								</div> <!-- Pattern_Details:M -->								

								<div id="Pattern_Details:P" style="display:none;">
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<xsl:value-of select="recurTrans/rDefFqu"/>:
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<!-- Add a combobox displaying EB.FREQUENCY Record Ids -->
									<select id="fqu:ebFrequencyId" name="fqu:ebFrequencyId"  onkeypress="focusOnFreqSave(event)" onkeydown="focusOnFreqSave(event)">
										<option>
											<!-- Add a 'blank' option -->
											<xsl:attribute name="value"></xsl:attribute>
										</option>									
                                        <xsl:choose>
											<xsl:when test="rEbFqu/rFquDetails!=''">
											    <xsl:for-each select="rEbFqu/rFquDetails">
													<option>
														<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
														<xsl:value-of select="."/>
													</option>
										        </xsl:for-each>
											</xsl:when>
											<xsl:otherwise>
											    <xsl:for-each select="rEbFqu/rFquId">
													<option>
														<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
														<xsl:value-of select="."/>
													</option>
											    </xsl:for-each>
											</xsl:otherwise>
										</xsl:choose>															
									</select>								
								</div> <!-- Pattern_Details:P -->

							</div> <!-- fqu:frequency_detail -->
						</td>
       				</tr>
       			</table>
	       	</div> <!-- id="fqu_pattern" -->
       	</div> <!-- id="fqu:container" -->
       	</xsl:for-each>
		</body>
       	</html>
	</xsl:template>
	<xsl:template match="apply_Style" name="apply_Style">
	   <xsl:variable name="application"><xsl:value-of select="/responseDetails/freqControl/app"/></xsl:variable>
	  <xsl:variable name="version"><xsl:value-of select="/responseDetails/freqControl/version"/></xsl:variable>
	  <xsl:variable name="enquiry"><xsl:value-of select="/responseDetails/freqControl/enqname"/></xsl:variable>
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
</xsl:stylesheet>
