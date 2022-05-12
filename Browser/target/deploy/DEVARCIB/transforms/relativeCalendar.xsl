<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<!-- Extract the Skin name for identifying CSS and Images directory -->
	<xsl:variable name="skin"><xsl:value-of select='/responseDetails/userDetails/skin'/></xsl:variable>

	<!-- Extract variable to see if we have popup calenders as opposed to window calenders -->
	<xsl:variable name="popupDropDown"><xsl:value-of select="/responseDetails/userDetails/popupDropDown"/></xsl:variable>
	
		<!-- Get the dropfield ID - this may be prefixed with '|' + the APPLICATION name.  So get the part before the '|' -->
	<xsl:variable name="df">
		<xsl:choose>
			<xsl:when test="contains(/responseDetails/relDate/df,'|')"><xsl:value-of select="substring-before(/responseDetails/relDate/df,'|')"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="/responseDetails/relDate/df"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<!-- Builds up a 'control' for a relative Date field in Browser. -->
	<!-- This allows the user to specify base date with 'allowed options' , 'offset' and 'period' -->
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
				<script type="text/javascript" src="../scripts/relativeCalendar.js" />
				<title>
					<xsl:value-of select="/responseDetails/relDate/relTitle"/>
				</title>
			</head>
			
			<body>
				<xsl:attribute name="onload">initRelativeCalendar('<xsl:value-of select="$df"/>','<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>');</xsl:attribute>

  			<div id="rel:container" class="customBackground" style="height:225;">  

           
       			<!--     <div id="toolbar_icons" class="customImage"> -->
       		     <!--    <span> -->
       					<table style="width:100%; height:20px;">
       				
       						<tr>
       							<td width="80%" align="center"><b style="display:block; text-indent: 60px;"><xsl:value-of select="/responseDetails/cal/headTitle"/></b></td>
       							<td align="right">
									<a>
										<xsl:attribute name="tabindex">-1</xsl:attribute>
										<xsl:attribute name="onmousedown">javascript:rel_save("<xsl:value-of select="$df"/>");</xsl:attribute>
										<img class="ascell">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/save.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="relDate/rOk"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="relDate/rOk"/></xsl:attribute>
										</img>
									</a>				
									<a>
										<xsl:attribute name="tabindex">-1</xsl:attribute>
										<xsl:attribute name="onmousedown">javascript:rel_close('<xsl:value-of select="$df"/>');</xsl:attribute>
										<img class="ascell">
											<xsl:attribute name="src">../plaf/images/<xsl:copy-of select="$skin"/>/deal/cancel.gif</xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="relDate/rCancel"/></xsl:attribute>
											<xsl:attribute name="title"><xsl:value-of select="relDate/rCancel"/></xsl:attribute>
										</img>
									</a>       					
       							</td>
       							
       						</tr>
       						
       					</table>
       			<!-- 	</span>	 -->
       			
					<xsl:for-each select="responseDetails/cal">
					<div id="calendar_popup" align="center">
						<!-- build up the table containing the calendar -->
						<table cellSpacing="0" cellPadding="0" border="0">
							<!-- The header, which contains the date, year and dropdown selector boxes -->	
							<tr>
								<td align="center" nowrap="nowrap">
									<xsl:call-template name="header_data"/>
								</td>
							</tr>
							<!-- The body of the table, which contains the days of the month -->
							<tr>
								<td align="center">
									<xsl:call-template name="body_data"/>
								</td>
							</tr>
							
						</table>
					</div>
					</xsl:for-each>

	       	<xsl:if test="/responseDetails/relDate/rDate">
 				<table style="width:100%; height:20px;">		
	       		 
	       		 		<tr>
							<td colspan="5" style="text-align: center;">
							<b><xsl:value-of select="/responseDetails/relDate/relTitle"/></b>
							</td>
						</tr>
						<!--- Add the relative options which specified in T(Z)<2,1> -->
						<tr align="center">
						<td style="width: 2px;"/>
       			    	<td>   
 					  			<select>
									<xsl:attribute name="id">fieldName:rel_options</xsl:attribute>
									<xsl:attribute name="class">dealbox</xsl:attribute>
									<xsl:attribute name="oldvalue"> </xsl:attribute>
									<xsl:attribute name="name"> fieldName:rel_options</xsl:attribute>
									<xsl:attribute name="onChange">clearDate(event,'<xsl:value-of select="$df"/>');</xsl:attribute>
									<option>
											<xsl:attribute name="value"> </xsl:attribute>
									</option>
					  				<xsl:for-each select="/responseDetails/relDate/relDateTrans/options/option">
										<option>
											<xsl:attribute name="value"><xsl:value-of select="./thisoption"/> </xsl:attribute>
											<xsl:value-of select="./disp"/>
										</option>
									</xsl:for-each>
			  					</select>
			  			</td>
			    	       		
			       		<!--  Add the box to specify the offset -->
						<td><p style="white-space: nowrap;"><xsl:value-of select="/responseDetails/relDate/relOffset/relFieldName"/></p></td>
						
						<td>
						<input id="fieldName:rel_offset" value="" size="1" style="font-size: 11px;">
						
						<xsl:choose>  
							<xsl:when test="/responseDetails/relDate/relOffset/relOffset/relOffNeg='yes'">
								<xsl:attribute name="onkeypress">return isNumberKey(event,'-')</xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="onkeypress">return isNumberKey(event)</xsl:attribute>
							</xsl:otherwise>    
						</xsl:choose>
						
						<xsl:attribute name="onChange">clearDate(event,'<xsl:value-of select="$df"/>');</xsl:attribute>
						<xsl:attribute name="onBlur">clearDate(event,'<xsl:value-of select="$df"/>');</xsl:attribute>
						<xsl:attribute name="onClick">clearDate(event,'<xsl:value-of select="$df"/>');</xsl:attribute>
						</input>
						</td>			       		
      		     		
      		     		
					<!-- Add the duration specified in the T(Z)<2,4>  -->
						
						<td>  
 					  			<select>
									<xsl:attribute name="id">fieldName:rel_duration</xsl:attribute>
									<xsl:attribute name="class">dealbox</xsl:attribute>
									<xsl:attribute name="oldvalue"> </xsl:attribute>
									<xsl:attribute name="name"> fieldname:rel_duration</xsl:attribute>
									<xsl:attribute name="onChange">clearDate(event,'<xsl:value-of select="$df"/>');</xsl:attribute>
									<option>
											<xsl:attribute name="value"> </xsl:attribute>
									</option>
									<xsl:for-each select="/responseDetails/relDate/relDurTrans/options/option">
										<option>
											<xsl:attribute name="value"><xsl:value-of select="./thisoption"/> </xsl:attribute>
											<xsl:value-of select="./disp"/>
										</option>
									</xsl:for-each>
			  					</select>
 						</td>
 						<td style="width: 2px;"/>
 					</tr>
					</table>			
	    	   </xsl:if>
		   </div> 
	 		</body>         		
    	</html>
    </xsl:template>
    
    <xsl:template name="header_data">
		<!-- Combo boxes for months and years not used in Phase 1 -->
    <table>
      <tbody>
        <tr>
          <td>
		   <select>
		        <xsl:choose>  
					<xsl:when test="$popupDropDown='true'" >
						<xsl:attribute name="name">monthList<xsl:value-of select="$df"/></xsl:attribute>
						<xsl:attribute name="id">monthList<xsl:value-of select="$df"/></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="name">monthList</xsl:attribute>
						<xsl:attribute name="id">monthList</xsl:attribute>		            
					</xsl:otherwise>    
				</xsl:choose>
	                
	            <xsl:attribute name="relativecalendardropfieldname"><xsl:value-of select="/responseDetails/cal/df"/></xsl:attribute>
		                
            			<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
		        <xsl:if test="$popupDropDown='true'" >
					<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
		            <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
			    </xsl:if>
	         
				<xsl:attribute name="onChange">
			              <xsl:choose>
				              <xsl:when test="$popupDropDown='true'" >javascript:relativeCalendarDisplay(event)</xsl:when>
				              <xsl:otherwise>javascript:updateCalendar(event)</xsl:otherwise>
			            	</xsl:choose>
		        </xsl:attribute>	
				<xsl:for-each select="/responseDetails/cal/months/month">
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
                <xsl:attribute name="relativecalendardropfieldname"><xsl:value-of select="/responseDetails/cal/df"/></xsl:attribute>
                
       		<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
	             <xsl:if test="$popupDropDown='true'" >
				<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
	                    <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
		      </xsl:if>
	                    		
			<xsl:attribute name="onChange">
		              <xsl:choose>
			              <xsl:when test="$popupDropDown='true'" >javascript:relativeCalendarDisplay(event)</xsl:when>
			              <xsl:otherwise>javascript:updateCalendar(event)</xsl:otherwise>
		            	</xsl:choose>
	              </xsl:attribute>	   
			<optgroup label="">
				<xsl:for-each select="/responseDetails/cal/years/year">
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
			<xsl:for-each select="days">
				<tr>
					<xsl:for-each select="day">
						<td class="dayheader">
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
									<xsl:attribute name="class">workday</xsl:attribute>
									<xsl:call-template name="dateAnchor"/>
								</xsl:when>
								<xsl:when test="holiday!=''">
									<xsl:attribute name="class">nonworkday</xsl:attribute>
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
		 <div id="message" class="calmessage" >
			<table>
				<tr>
					<td style="text-align:center;"><b><xsl:value-of select = "msg"/></b></td>
				</tr>
			</table>
		</div>
 	</xsl:template>

	<xsl:template name="dateAnchor">
		<a>
			<!-- Hightlight Today on calendar -->
			<xsl:if test=".=/responseDetails/cal/day">		
				<xsl:attribute name="class">today</xsl:attribute>
			</xsl:if>
			
			<!-- For calendar popups add mouse over/out events so we can determine if a date has been picked or not for hiding the popup -->
			<xsl:if test="$popupDropDown='true'" >
				<xsl:attribute name="onmouseover">calendarDateMouseOver()</xsl:attribute>
	                    <xsl:attribute name="onmouseout">calendarDateMouseOut()</xsl:attribute>
	        	</xsl:if>
			
			<!-- df represents the node dropfield -->
			<xsl:attribute name="onClick">
				<xsl:choose>
					<xsl:when test="$popupDropDown='true'" >
						javascript:pickDate('<xsl:value-of select="."/>', '<xsl:value-of select="/responseDetails/cal/month"/>', '<xsl:value-of select="/responseDetails/cal/year"/>', '<xsl:value-of select="/responseDetails/cal/df"/>', this)														
					</xsl:when>
					<xsl:otherwise>
						javascript:pickDate('<xsl:value-of select="."/>', '<xsl:value-of select="/responseDetails/cal/month"/>', '<xsl:value-of select="/responseDetails/cal/year"/>', '<xsl:value-of select="/responseDetails/cal/df"/>')
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
</xsl:stylesheet>
	