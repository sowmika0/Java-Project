<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:import href="userDetails.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>
	
	<!-- Extract variable to see if we have popup calenders as opposed to window calenders -->
	<xsl:variable name="popupDropDown"><xsl:value-of select="/responseDetails/userDetails/popupDropDown"/></xsl:variable>
	
	<!-- Get the dropfield ID - this may be prefixed with '|' + the APPLICATION name.  So get the part before the '|' -->
	<xsl:variable name="df">
		<xsl:choose>
			<xsl:when test="contains(/responseDetails/recurControl/df,'|')"><xsl:value-of select="substring-before(/responseDetails/recurControl/df,'|')"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="/responseDetails/recurControl/df"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- Builds up a 'control' for a recurrence field in Browser. -->
	<!-- This allows the user to specify schedules for recurring events like 'montly on the first wednesday' -->
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
					<xsl:value-of select="responseDetails/recurControl/recurTrans/rTitle"/>
				</title>
			</head>
			
			<body>
				<xsl:attribute name="onload">initRecurrence('<xsl:value-of select="$df"/>','<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>');</xsl:attribute>
		<xsl:call-template name="userDetails"/>
		<xsl:for-each select="responseDetails/recurControl">
       	<div id="container" class="customBackground">

       		<div id="toolbar_icons" class="customImage"> 
       		
       			<table style="width:100%; height:20px;">
       			
       				<tr style="border-bottom:solid 1px #d9d9d9;">
       					<td>
			       			<a id ="Recurrence_Pattern" tabindex="0">
			       				<!-- Recurrence Pattern  -->
			       				<xsl:value-of select="recurTrans/rTitle"/>
			       			</a>       					
       					</td>
       					<td align="right">
							<a>
								<xsl:attribute name="tabindex">-1</xsl:attribute>
								<xsl:attribute name="onmousedown">javascript:saveDate("<xsl:value-of select="$df"/>")</xsl:attribute>
								<img class="ascell" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
									<xsl:attribute name="onkeypress">rec_saveDateonKey('<xsl:value-of select="$df"/>',event);</xsl:attribute>
									<xsl:attribute name="tabindex">0</xsl:attribute>
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/save.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="recurTrans/rOk"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="recurTrans/rOk"/></xsl:attribute>
								</img>
							</a>				
							<a>
								<xsl:attribute name="tabindex">-1</xsl:attribute>
								<xsl:attribute name="onmousedown">javascript:hidePopupDropDown('<xsl:value-of select="$df"/>')</xsl:attribute>
								<img class="ascell" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
									<xsl:attribute name="onkeypress">rec_closeonKey('<xsl:value-of select="$df"/>',event);</xsl:attribute>
									<xsl:attribute name="tabindex">0</xsl:attribute>
									<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/cancel.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="recurTrans/rCancel"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="recurTrans/rCancel"/></xsl:attribute>
								</img>
							</a>       					
       					</td>
       				</tr>
       			</table>
	       	</div>

			<!-- Date field and Calendar icon. Only if required in the xml -->
	       	<xsl:if test="recurTrans/rnDate">
		       	<!-- A firefox bug means we need to set the position otherwise the cursor disappears! -->
		       	<div id="next_date" style="display:inline; position:relative;">
		       		<p>
			       	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	   				<!-- Next Date  -->
	   				<xsl:value-of select="recurTrans/rnDate"/>:
	
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						<input type="text" id="fqu:nextDate" value="" size="12"></input>
	
						<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/calendar"/></xsl:attribute>
						<img class="dropdown_button" onfocus ="focusonKey('inline',event);" onblur = "hideTooltip(event);">
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

       		<div id="container_inner">
       			<table>
       				<tr padding="10">
       					<td style="border-right:solid 1px #d9d9d9;padding:3px;">
 				       		<div id="frequency_pattern">
 				       			<xsl:if test="recurTrans/rNone">
					       			<p>
					       				<label for="frequency:N"><input type="radio" name="frequency" id="frequency:N" value="N" onclick="drawSchedulerControl('');"/>
					       					<!-- None -->
					       					<xsl:value-of select="recurTrans/rNone"/>
					       				</label>
					       			</p>
 				       			</xsl:if>
				       			<p>
				       				<label for="frequency:D"><input type="radio" name="frequency" id="frequency:D" value="D" onclick="drawSchedulerControl('');"/>
				       					<!-- Daily -->
				       					<xsl:value-of select="recurTrans/rDaily"/>
				       				</label>
				       			</p>
								<p>
									<label for="frequency:W"><input type="radio" name="frequency" id="frequency:W" value="W" onclick="drawSchedulerControl('');"/>
										<!-- Weekly -->
				       					<xsl:value-of select="recurTrans/rWeekly"/>										
									</label>
								</p>
								<p>
									<label for="frequency:M"><input type="radio" name="frequency" id="frequency:M" value="M" onclick="drawSchedulerControl('');"/>
										<!-- Monthly -->
				       					<xsl:value-of select="recurTrans/rMonthly"/>										
									</label>
								</p>
								<p>	
									<label for="frequency:Y"><input type="radio" name="frequency" id="frequency:Y" value="Y" onclick="drawSchedulerControl('');"/>
										<!-- Yearly -->
				       					<xsl:value-of select="recurTrans/rYearly"/>										
									</label>
								</p>
								<p>
									<label for="frequency:P"><input type="radio" name="frequency" id="frequency:P" value="P" onclick="drawSchedulerControl('');"/>
										<!-- Defined -->
				       					<xsl:value-of select="recurTrans/rPreDef"/>
									</label>
								</p>								
								<p>
									<label for="frequency:A"><input type="radio" name="frequency" id="frequency:A" value="A" onclick="drawSchedulerControl('');"/>
										<!-- Advanced -->
				       					<xsl:value-of select="recurTrans/rAdvanced"/>										
									</label>
								</p>
				       		</div>       						
       					</td>
       					
       					<td>
							<div id="frequency_detail">
								<div id="Invalid_Format" style="display:none;">
									<a>
										<img class="ascell">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/recurr_err.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="/responseDetails/window/translations/recurrerror"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="/responseDetails/window/translations/recurrerror"/></xsl:attribute>
										</img>
										<!-- Invalid Recurring Date Format. Select a new value. -->
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<xsl:value-of select="recurTrans/rInv"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									</a>
								</div>							
							<!-- A firefox bug means we need to set the position otherwise the cursor disappears! -->
								<div id="Recurrance:D" style="display:none; position:relative;">
									<table>
										<tr>
											<td>
											<label>
												<input type="radio" name="rec:D:radio" id="rec:Daily" value="D"/>
												<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												<!-- Every -->
					       						<xsl:value-of select="recurTrans/rEvery"/>
											</label>

					       					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<input id="D:days" type="text" class="scheduler" value="1" size="3"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)"/>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<!-- day(s) -->
	       									<xsl:value-of select="recurTrans/rDays"/>
	       									</td>
										</tr>
										<tr>
											<td>
						       				<label>
						       					<input type="radio" name="rec:D:radio" id="rec:BusinessDay" value="DB"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)"/>
						       					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						       					<!-- Business Day -->
						       					<xsl:value-of select="recurTrans/rBDay"/>
											</label>
											</td>
										</tr>
									</table>
								</div>
								<div id="Recurrance:W" style="display:none;">
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<!--  Recur every -->
			       					<xsl:value-of select="recurTrans/rEvery"/>	
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<input id="W:weeks" type="text" class="scheduler" value="1" size="3"/> 
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<!-- week(s) -->
			       					<xsl:value-of select="recurTrans/rWeeks"/>
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<!-- on: -->
			       					<xsl:value-of select="recurTrans/rOn"/>:									
									<table>
										<tr>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:1"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:1" value="1"/>
													<!-- Monday -->
							       					<xsl:value-of select="recurTrans/rMon"/>
												</label>
											</td>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:2"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:2" value="2"/>
													<!-- Tuesday -->
							       					<xsl:value-of select="recurTrans/rTue"/>
												</label>
											</td>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:3"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:3" value="3"/>
													<!-- Wednesday -->
							       					<xsl:value-of select="recurTrans/rWed"/>													
												</label>
											</td>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:4"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:4" value="4"/>
													<!-- Thursday -->
							       					<xsl:value-of select="recurTrans/rThur"/>													
												</label>
											</td>
										<tr>
										</tr>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:5"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:5" value="5"/>
													<!-- Friday -->
							       					<xsl:value-of select="recurTrans/rFri"/>													
												</label>
											</td>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:6"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:6" value="6"/>
													<!-- Saturday -->
							       					<xsl:value-of select="recurTrans/rSat"/>													
												</label>
											</td>
											<td nowrap="nowrap">
												<label for="W:dayOfWeek:7"><input type="checkbox" name="W:dayOfWeek" id="W:dayOfWeek:7" value="7"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)"/>
													<!-- Sunday -->
							       					<xsl:value-of select="recurTrans/rSun"/>													
												</label>
											</td>
											<td></td>
										</tr>
									</table>
								</div>
			
								<div id="Recurrance:M" style="display:none;">
									<p>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Every -->
										<xsl:value-of select="recurTrans/rEvery"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<input type="text" id="M:dayOfMonth:month" class="scheduler" value="1" size="3"></input>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- months(s) -->
										<xsl:value-of select="recurTrans/rMonths"/>									
									</p>
									<hr>
									</hr>
									<p>
										<label for="Recurrance:M:On"><input type="checkbox" name="Recurrance:M:On" id="Recurrance:M:On" value="o" onclick="showHideMonthDetail('');"  onkeypress="setFocusOnRecurrSaveOn(event,this)"/>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<!-- On -->
											<xsl:value-of select="recurTrans/rOn"/>													
										</label>

										<div id="monthly_details" style="display:none;"> 
											<p>
												<label for="M:RecurType:dayOfMonth">
													<input type="radio" name="M:RecurType" id="M:RecurType:dayOfMonth" value="dayOfMonth" checked="true">
								                	</input>
													<!-- Day -->
													<xsl:value-of select="recurTrans/rDay"/>
													<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												</label>
												<input type="text" id="M:dayOfMonth:day" class="scheduler" value="1" size="3"></input>
											</p>
											<p>
												<label for="M:RecurType:dayOfWeekOfMonth">
													<input type="radio" name="M:RecurType" id="M:RecurType:dayOfWeekOfMonth" value="dayOfWeekOfMonth"></input>
													<!-- The -->
													<xsl:value-of select="recurTrans/rThe"/>
													<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												</label>
												<select id="M:dayOfWeekOfMonth:week">
													<option selected="true" value="1">
														<!-- First -->
														<xsl:value-of select="recurTrans/rFirst"/>
													</option>
													<option value="2">
														<!-- Second -->
														<xsl:value-of select="recurTrans/rSecond"/>
													</option>
													<option value="3">
														<!-- Third -->
														<xsl:value-of select="recurTrans/rThird"/>
													</option>
													<option value="4">
														<!-- Fourth -->
														<xsl:value-of select="recurTrans/rFourth"/>
													</option>
													<option value="L">
														<!-- Last -->
														<xsl:value-of select="recurTrans/rLast"/>
													</option>
												</select>
												<select id="M:dayOfWeekOfMonth:day"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)">
													<option value="1">
														<!-- Monday -->
														<xsl:value-of select="recurTrans/rMon"/>
													</option>
													<option value="2">
														<!-- Tuesday -->
														<xsl:value-of select="recurTrans/rTue"/>
													</option>
													<option value="3">
														<!-- Wednesday -->
														<xsl:value-of select="recurTrans/rWed"/>
													</option>
													<option value="4">
														<!-- Thursday -->
														<xsl:value-of select="recurTrans/rThur"/>
													</option>
													<option value="5">
														<!-- Friday -->
														<xsl:value-of select="recurTrans/rFri"/>
													</option>
													<option value="6">
														<!-- Saturday -->
														<xsl:value-of select="recurTrans/rSat"/>
													</option>
													<option value="7">
														<!-- Sunday -->
														<xsl:value-of select="recurTrans/rSun"/>											
													</option>
												</select>
											</p>
										</div>
									</p>
								</div>
								
								<div id="Recurrance:Y" style="display:none;">
									
									<p>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Every -->
										<xsl:value-of select="recurTrans/rEvery"/>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<input type="text" id="Y:year:year" class="scheduler" value="1" size="3"></input>
										<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										<!-- Year(s) -->
										<xsl:value-of select="recurTrans/rYears"/>									
									</p>
									<hr>
									</hr>
									
										<label for="Recurrance:Y:On"><input type="checkbox" name="Recurrance:Y:On" id="Recurrance:Y:On" value="o" onclick="showHideYearDetail('');"  onkeypress="setFocusOnRecurrSaveOn(event,this)"/>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<!-- On -->
											<xsl:value-of select="recurTrans/rOn"/>													
										</label>
										<div id="yearly_details" style="display:none;"> 
											<p>
																	
										<label for="Y:RecurType:dayOfMonth">
											<input type="radio" name="Y:RecurType" id="Y:RecurType:dayOfMonth" value="dayOfMonth" checked="true"></input>
										</label>
										<select id="Y:dayOfMonth:month">
											<option selected="true" value="1">
												<!-- January -->
												<xsl:value-of select="recurTrans/rJan"/>
											</option>
											<option value="2">
												<!-- February -->
												<xsl:value-of select="recurTrans/rFeb"/>
												</option>
											<option value="3">
												<!-- March -->
												<xsl:value-of select="recurTrans/rMar"/>
											</option>
											<option value="4">
												<!-- April -->
												<xsl:value-of select="recurTrans/rApr"/>
											</option>
											<option value="5">
												<!-- May -->
												<xsl:value-of select="recurTrans/rMay"/>
											</option>
											<option value="6">
												<!-- June -->
												<xsl:value-of select="recurTrans/rJun"/>
											</option>
											<option value="7">
												<!-- July -->
												<xsl:value-of select="recurTrans/rJly"/>
											</option>
											<option value="8">
												<!-- August -->
												<xsl:value-of select="recurTrans/rAug"/>
											</option>
											<option value="9">
												<!-- September -->
												<xsl:value-of select="recurTrans/rSep"/>
											</option>
											<option value="10">
												<!-- October -->
												<xsl:value-of select="recurTrans/rOct"/>
											</option>
											<option value="11">
												<!-- November -->
												<xsl:value-of select="recurTrans/rNov"/>
											</option>
											<option value="12">
												<!-- December -->
												<xsl:value-of select="recurTrans/rDec"/>
											</option>
										</select>
										<input type="text" id="Y:dayOfMonth:day" class="scheduler" value="1" size="3"></input>
									</p>
									<p>
										<label for="Y:RecurType:dayOfWeekOfMonth">
											<input type="radio" name="Y:RecurType" id="Y:RecurType:dayOfWeekOfMonth" value="dayOfWeekOfMonth"></input>
										</label>
										<select id="Y:dayOfWeekOfMonth:week">
											<option selected="true" value="1">
												<!-- First -->
												<xsl:value-of select="recurTrans/rFirst"/>
											</option>
											<option value="2">
												<!-- Second -->
												<xsl:value-of select="recurTrans/rSecond"/>
											</option>
											<option value="3">
												<!-- Third -->
												<xsl:value-of select="recurTrans/rThird"/>
											</option>
											<option value="4">
												<!-- Fourth -->
												<xsl:value-of select="recurTrans/rFourth"/>
											</option>
											<option value="L">
												<!-- Last -->
												<xsl:value-of select="recurTrans/rLast"/>
											</option>										
										</select>
										<select id="Y:dayOfWeekOfMonth:day">
											<option value="1">
												<!-- Monday -->
												<xsl:value-of select="recurTrans/rMon"/>
											</option>
											<option value="2">
												<!-- Tuesday -->
												<xsl:value-of select="recurTrans/rTue"/>
											</option>
											<option value="3">
												<!-- Wednesday -->
												<xsl:value-of select="recurTrans/rWed"/>
											</option>
											<option value="4">
												<!-- Thursday -->
												<xsl:value-of select="recurTrans/rThur"/>
											</option>
											<option value="5">
												<!-- Friday -->
												<xsl:value-of select="recurTrans/rFri"/>
											</option>
											<option value="6">
												<!-- Saturday -->
												<xsl:value-of select="recurTrans/rSat"/>
											</option>
											<option value="7">
												<!-- Sunday -->
												<xsl:value-of select="recurTrans/rSun"/>											
											</option>										
										</select>
										<select id="Y:dayOfWeekOfMonth:month"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)">
											<option selected="true" value="1">
												<!-- January -->
												<xsl:value-of select="recurTrans/rJan"/>
											</option>
											<option value="2">
												<!-- February -->
												<xsl:value-of select="recurTrans/rFeb"/>
												</option>
											<option value="3">
												<!-- March -->
												<xsl:value-of select="recurTrans/rMar"/>
											</option>
											<option value="4">
												<!-- April -->
												<xsl:value-of select="recurTrans/rApr"/>
											</option>
											<option value="5">
												<!-- May -->
												<xsl:value-of select="recurTrans/rMay"/>
											</option>
											<option value="6">
												<!-- June -->
												<xsl:value-of select="recurTrans/rJun"/>
											</option>
											<option value="7">
												<!-- July -->
												<xsl:value-of select="recurTrans/rJly"/>
											</option>
											<option value="8">
												<!-- August -->
												<xsl:value-of select="recurTrans/rAug"/>
											</option>
											<option value="9">
												<!-- September -->
												<xsl:value-of select="recurTrans/rSep"/>
											</option>
											<option value="10">
												<!-- October -->
												<xsl:value-of select="recurTrans/rOct"/>
											</option>
											<option value="11">
												<!-- November -->
												<xsl:value-of select="recurTrans/rNov"/>
											</option>
											<option value="12">
												<!-- December -->
												<xsl:value-of select="recurTrans/rDec"/>
											</option>
										</select>										
									</p>
									</div>
									
								</div>
								
								<div id="Recurrance:P" style="display:none;">
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<xsl:value-of select="recurTrans/rDefFqu"/>:
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									<!-- Add a combobox displaying EB.FREQUENCY Record Ids -->
									<select id="ebFrequencyId" onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)">
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
								</div>
																
								<div id="Recurrance:A" style="display:none;">
									<table>
										<tr>
											<td>
												<!-- Every -->
												<xsl:value-of select="recurTrans/rEvery"/>
												<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											</td>
											<td>
												<!-- On the -->
												<xsl:value-of select="recurTrans/rOn"/>
												<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												<xsl:value-of select="recurTrans/rThe"/>
											</td>
										</tr>
										<tr>
											<td><input type="radio" name="advanced:D" id="every:D" value="e"></input></td>
											<td><input type="radio" name="advanced:D" id="on:D" value="o"></input></td>
											<td><input type="text" name="advanced:Input:D" id="advanced:Input:D"></input></td>
											<td>
												<!-- Day(s)-->
												<xsl:value-of select="recurTrans/rDays"/>
											</td>
										</tr>
										<tr>
											<td><input type="radio" name="advanced:W" id="every:W" value="e"></input></td>
											<td><input type="radio" name="advanced:W" id="on:W" value="o"></input></td>
											<td>
												<div style="overflow:auto"> 
												<input type="text" name="advanced:Input:W" id="advanced:Input:W">
													<xsl:attribute name="tabindex">-1</xsl:attribute>
												</input>
												</div> 
											</td>
											<td>
												<!-- Week(s) -->
												<xsl:value-of select="recurTrans/rWeeks"/>
											</td>
										</tr>
										<tr>
											<td><input type="radio" name="advanced:M" id="every:M" value="e"></input></td>
											<td><input type="radio" name="advanced:M" id="on:M" value="o"></input></td>
											<td><input type="text" name="advanced:Input:M" id="advanced:Input:M"></input></td>											
											<td>
												<!-- Month(s) -->
												<xsl:value-of select="recurTrans/rMonths"/>
											</td>
										</tr>
										<tr>
											<td><input type="radio" name="advanced:Y" id="every:Y" value="e"></input></td>
											<td><input type="radio" name="advanced:Y" id="on:Y" value="o"></input></td>
											<td><input type="text" name="advanced:Input:Y" id="advanced:Input:Y"  onkeypress="setFocusOnRecurrSave(event)" onkeydown="setFocusOnRecurrSave(event)"></input></td>
											<td>
												<!-- Year(s) -->
												<xsl:value-of select="recurTrans/rYears"/>
											</td>
										</tr>																														
									</table>
								</div>
							</div>
       					</td>
       				</tr>
       			</table>
	       	</div>

       	</div>
			</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
