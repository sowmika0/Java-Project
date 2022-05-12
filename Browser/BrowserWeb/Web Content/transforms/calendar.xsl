<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="userDetails.xsl"/>
	<xsl:import href="tableBorders.xsl"/>
	<xsl:import href="stylingDetails.xsl"/>
	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>
	
	<!-- Extract variable to see if we have popup calenders as opposed to window calenders -->
	<xsl:variable name="popupDropDown"><xsl:value-of select="/responseDetails/userDetails/popupDropDown"/></xsl:variable>
	
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
					<xsl:value-of select="responseDetails/cal/title"/>
				</title>
			</head>
			
			<body>
			
				 <xsl:choose>  
			        	<xsl:when test="$popupDropDown='true'" >
			                <xsl:attribute name="onload">javascript:initCalendarPopup('<xsl:value-of select="responseDetails/cal/df"/>')</xsl:attribute>
			            </xsl:when>
			            <xsl:otherwise>
			                <xsl:attribute name="onload">javascript:initDropdownList()</xsl:attribute>		            
			            </xsl:otherwise>    
	                	</xsl:choose>

				<xsl:for-each select="responseDetails/cal">

					<!-- Process User Details -->
					<xsl:call-template name="userDetails"/>
					<div id="calendar_popup">
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="'calendar_display1'"/>
							</xsl:call-template>
						</xsl:attribute>
					
						<!-- build up the table containing the calendar -->
						<table cellSpacing="0" cellPadding="0" border="0">
	
							<!-- The header, which contains the date, year and dropdown selector boxes -->	
							<tr>
								<td  id="headerData" align="center" nowrap="nowrap" tabIndex="0">
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'caption1'"/>
										</xsl:call-template>
									</xsl:attribute>
									<xsl:call-template name="header_data"/>
								</td>
							</tr>
	
							<!-- The body of the table, which contains the days of the month -->
							<tr>
								<td align="center">
									<br/>
									<xsl:call-template name="body_data"/>
								</td>
							</tr>
	
						</table>
					</div>
			    </xsl:for-each>
			</body>
		</html>
	</xsl:template>

  	<xsl:template name="header_data">
		<!-- Head Title displayed in the tab - Month and Year-->
		<b><xsl:value-of select="/responseDetails/cal/headTitle"/></b>
		<br/>
		<!-- Combo boxes for months and years not used in Phase 1 -->
    <table>
      <tbody>
        <tr>
          <td>
		   <select>
		        <xsl:choose>  
				<xsl:when test="$popupDropDown='true'" >
					<xsl:attribute name="name">monthList<xsl:value-of select="df"/></xsl:attribute>
					<xsl:attribute name="id">monthList<xsl:value-of select="df"/></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="name">monthList</xsl:attribute>
					<xsl:attribute name="id">monthList</xsl:attribute>		            
				</xsl:otherwise>    
	                </xsl:choose>
	                
	                 <xsl:attribute name="calendardropfieldname"><xsl:value-of select="/responseDetails/cal/df"/></xsl:attribute>
		                
            			<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
		             <xsl:if test="$popupDropDown='true'" >
					<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
		                    <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
			       </xsl:if>
	         
				 <xsl:attribute name="onChange">
			              <xsl:choose>
				              <xsl:when test="$popupDropDown='true'" >javascript:dropDownCalendar(event)</xsl:when>
				              <xsl:otherwise>javascript:updateCalendar(event)</xsl:otherwise>
			            	</xsl:choose>
		              </xsl:attribute>	
				<xsl:for-each select="months/month">
					<option>
						<xsl:attribute name="value"><xsl:value-of select="ref"/></xsl:attribute>
						<xsl:if test="./ref=/responseDetails/cal/month">
							<xsl:attribute name="selected">true</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="cap"/>
					</option>
				</xsl:for-each>
		</select>
          </td>
          <td>
		<select name="yearList" id="yearList">
		        <xsl:choose>  
		        	<xsl:when test="$popupDropDown='true'" >
		                <xsl:attribute name="name">yearList<xsl:value-of select="df"/></xsl:attribute>
		                <xsl:attribute name="id">yearList<xsl:value-of select="df"/></xsl:attribute>
		            </xsl:when>
		            <xsl:otherwise>
						<xsl:attribute name="name">yearList</xsl:attribute>
		                <xsl:attribute name="id">yearList</xsl:attribute>	            
		            </xsl:otherwise>    
                </xsl:choose>
                <xsl:attribute name="calendardropfieldname"><xsl:value-of select="/responseDetails/cal/df"/></xsl:attribute>
                
       		<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
	             <xsl:if test="$popupDropDown='true'" >
				<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
	                    <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
		      </xsl:if>
	                    		
			<xsl:attribute name="onChange">
		              <xsl:choose>
			              <xsl:when test="$popupDropDown='true'" >javascript:dropDownCalendar(event)</xsl:when>
			              <xsl:otherwise>javascript:updateCalendar(event)</xsl:otherwise>
		            	</xsl:choose>
	              </xsl:attribute>	   
			<optgroup label="">
				<xsl:for-each select="years/year">
					<option>
			 			<xsl:if test=".=/responseDetails/cal/year">
						<xsl:attribute name="selected">true</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</option>
				</xsl:for-each>
			</optgroup>
		</select>
		<!-- End of combo boxes for months and years -->
          </td>
        </tr>
      </tbody>
    </table>
  	</xsl:template>
  	
	<xsl:template name="body_data">
		<div id="calendar">
		<table>
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'caldata'"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:for-each select="days">
				<tr>
					<xsl:for-each select="day">
						<td>
							<xsl:attribute name="class">
								<xsl:call-template name="apply_Style">
									<xsl:with-param name="actualclass" select="'dayheader'"/>
								</xsl:call-template>
							</xsl:attribute>
							<b><xsl:value-of select="."/></b>
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
			<xsl:for-each select="r">
				<tr>
					<xsl:for-each select="c">
						<td>
						    <xsl:choose>
								<xsl:when test="work!=''">
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'workday'"/>
										</xsl:call-template>
									</xsl:attribute>
									<xsl:call-template name="dateAnchor"/>
								</xsl:when>
								<xsl:when test="holiday!=''">
									<xsl:attribute name="class">
										<xsl:call-template name="apply_Style">
											<xsl:with-param name="actualclass" select="'nonworkday'"/>
										</xsl:call-template>
									</xsl:attribute>
									<xsl:call-template name="dateAnchor"/>
								</xsl:when>
								<xsl:otherwise><!-- Otherwise should be empty cell --></xsl:otherwise>
							</xsl:choose>
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
		</div>
		 <div id="message">
			<xsl:attribute name="class">
				<xsl:call-template name="apply_Style">
					<xsl:with-param name="actualclass" select="'calmessage'"/>
				</xsl:call-template>
			</xsl:attribute>
			<table>
				<tr>
					<td style="text-align:center;"><b><xsl:value-of select = "msg"/></b></td>
				</tr>
			</table>
		</div>
 	</xsl:template>

	<xsl:template name="dateAnchor">
		<a style="color: black">
			<!-- Hightlight Today on calendar -->
			 <xsl:attribute name="onkeypress">
		      setFocusInsideCal(event,this,'headerData')
		   </xsl:attribute>
		   <xsl:attribute name="onkeydown">
		      setFocusInsideCal(event,this,'headerData')
		   </xsl:attribute>
		   <xsl:if test=".=/responseDetails/cal/selectedDay">
				<xsl:attribute name="style">border: 1px solid blue;font-weight: bold;</xsl:attribute>
			</xsl:if> 
			<xsl:if test=".=/responseDetails/cal/day">
				<xsl:attribute name="class">
					<xsl:call-template name="apply_Style">
						<xsl:with-param name="actualclass" select="'today'"/>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:if>
			
			<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
			<xsl:if test="$popupDropDown='true'" >
				<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
	                    <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
	        	</xsl:if>
			
			<!-- df represents the node dropfield -->
			<xsl:attribute name="onclick">
				<xsl:choose>
					<xsl:when test="$popupDropDown='true'" >
						javascript:dropCalendarPickDate('<xsl:value-of select="."/>', '<xsl:value-of select="/responseDetails/cal/month"/>', '<xsl:value-of select="/responseDetails/cal/year"/>', '<xsl:value-of select="/responseDetails/cal/df"/>','<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>')														
					</xsl:when>
					<xsl:otherwise>
						javascript:pickDate('<xsl:value-of select="."/>', '<xsl:value-of select="/responseDetails/cal/month"/>', '<xsl:value-of select="/responseDetails/cal/year"/>', '<xsl:value-of select="/responseDetails/cal/df"/>')
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:attribute>
			<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="apply_Style" name="apply_Style">
	   <xsl:variable name="application"><xsl:value-of select="/responseDetails/cal/app"/></xsl:variable>
	  <xsl:variable name="version"><xsl:value-of select="/responseDetails/cal/version"/></xsl:variable> 
		<xsl:variable name="enquiry"><xsl:value-of select="/responseDetails/cal/enqname"/></xsl:variable>
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
