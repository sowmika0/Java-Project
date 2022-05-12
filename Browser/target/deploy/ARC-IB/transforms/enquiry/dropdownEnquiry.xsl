<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
<xsl:template name="dropDownEnq_n" match="/">

      <!-- Extract the dropfield -->
      <xsl:variable name="dropfield">
        <xsl:choose>
          <xsl:when test="/responseDetails/window/panes/pane/dataSection/enqResponse/df">
            <xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/df"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="/responseDetails/webDetails/WS_dropfield"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <input type="hidden" name="dropdownEnquiry" id="dropdownEnquiry" value=""/>
      <input type="hidden" name="dropFieldId" id="dropFieldId" value="{$dropfield}"/>
      <input type="hidden" name="fragmentName" id="fragmentName">
         <xsl:attribute name="value"><xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/></xsl:attribute>
      </input>
      <!--*********************************************************************************************-->
      <xsl:if test="/responseDetails/window/panes/pane/dataSection/enqResponse/r[1]" >
          <table id="dropDownTable:{$dropfield}">
              <tbody>
                <!-- Create header for the dropdown. Top row.-->
                <tr id ="rowHeader">
                	<xsl:attribute name="tabindex">0</xsl:attribute>
                  <xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/cols/c">
                    <th scope="col" style="cursor: default;">
					 
					<xsl:attribute name="class">
						<xsl:call-template name="apply_Style">
							<xsl:with-param name="actualclass" select="'columnHeader1'"/>
						</xsl:call-template>
					</xsl:attribute>
					
                      <xsl:value-of select="."/>
                    </th>
                  </xsl:for-each>             
                </tr>
	              <xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/r">
	                <tr>
                    <!--Enrichment variable -->
                    <xsl:variable name="dropEnri">
                      <xsl:call-template name="escape-apos">
                        <xsl:with-param name="string" select="c[2]/cap" />
                      </xsl:call-template>
                    </xsl:variable>
                    <!--Set the id-->
                    <xsl:attribute name="tabindex">0</xsl:attribute>
                    <xsl:attribute name="id">dropDownRow<xsl:value-of select="position()"/></xsl:attribute>
                    <!--Set the event when user hovers over with the mouse pointer-->
                    <xsl:attribute name="onmouseover">tableRowMouseOver("dropDownRow<xsl:value-of select="position()"/>")</xsl:attribute>
                    <!--Set the event when user hovers out with the mouse pointer-->
                    <xsl:attribute name="onmouseout">tableRowMouseOut("dropDownRow<xsl:value-of select="position()"/>")</xsl:attribute>
                    
                    <!--Set the event when user clicks on the table row - either use the target or caption depending on the type of enquiry result -->
			<xsl:choose>
				<xsl:when test="c[1]/cap">
					<xsl:variable name="dropCap">
						<xsl:call-template name="escape-apos">
		                	<xsl:with-param name="string" select="c[1]/cap"/>
                		</xsl:call-template>
					</xsl:variable>
		        	<xsl:attribute name="onmousedown">tableRowClick('<xsl:value-of select="$dropCap"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>', '<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>')</xsl:attribute>
		   			<xsl:attribute name="onkeypress">tableRowkeyPress('<xsl:value-of select="$dropCap"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>', '<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>',event)</xsl:attribute>
		   			<xsl:attribute name="onfocus">tableRowfocus(event)</xsl:attribute>
				   	<xsl:attribute name="onblur">tableRowblur(event)</xsl:attribute>
				   	<xsl:attribute name="onkeydown">tableRowSetFocus(event)</xsl:attribute>
				</xsl:when>
				<xsl:when test="c[1]/tar">
					<xsl:variable name="dropTar">
						<xsl:call-template name="escape-apos">
		               		<xsl:with-param name="string" select="c[1]/tar"/>
                    	</xsl:call-template>
					</xsl:variable>
		            <xsl:attribute name="onmousedown">tableRowClick('<xsl:value-of select="$dropTar"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>', '<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>')</xsl:attribute>
		      		<xsl:attribute name="onkeypress">tableRowkeyPress('<xsl:value-of select="$dropTar"/>', '<xsl:copy-of select="$dropfield"/>', '<xsl:value-of select="$dropEnri"/>', '<xsl:value-of select="/responseDetails/webDetails/WS_FragmentName"/>',event)</xsl:attribute>
		      		<xsl:attribute name="onfocus">tableRowfocus(event)</xsl:attribute>
				   	<xsl:attribute name="onblur">tableRowblur(event)</xsl:attribute>
				   	<xsl:attribute name="onkeydown">tableRowSetFocus(event)</xsl:attribute>		            
					</xsl:when>
			</xsl:choose>

                    <!--Set changing color for rows -->
					<!-- <xsl:attribute name="class">colour<xsl:value-of select="position() mod 2"/></xsl:attribute> -->
					
					<xsl:variable name="classvalue">colour<xsl:value-of select="position() mod 2"/></xsl:variable>
						<xsl:attribute name="class">
							<xsl:call-template name="apply_Style">
								<xsl:with-param name="actualclass" select="$classvalue"/>
							</xsl:call-template>
						</xsl:attribute>
					
                    <!--Display row data--> 
                    <xsl:call-template name="rowData"/>
                  </tr>
                </xsl:for-each>
              </tbody>
            </table>
	  </xsl:if>	
      <!--*********************************************************************************************-->
</xsl:template>


<xsl:template name="rowData" match="r">


  <xsl:for-each select="c">
      <td style="cursor: pointer;">
      	<xsl:attribute name="class">
			<xsl:call-template name="apply_Style">
				<xsl:with-param name="actualclass" select="'coloumntddata'"/>
			</xsl:call-template>
		</xsl:attribute>
      <xsl:if test="i!=''">
         <xsl:attribute name="align">right</xsl:attribute>
      </xsl:if>	
      <xsl:attribute name="nowrap">nowrap</xsl:attribute>
        <xsl:choose>
          <xsl:when test="position() = 1">
            <b><xsl:value-of select="cap"/></b> 
          </xsl:when>
          <xsl:otherwise>
	          <xsl:value-of select="cap"/>
          </xsl:otherwise>
        </xsl:choose>     
      </td>
    </xsl:for-each>
    
  </xsl:template>

</xsl:stylesheet> 
