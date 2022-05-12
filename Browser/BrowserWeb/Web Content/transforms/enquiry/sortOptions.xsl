<?xml version="1.0" encoding="UTF-8"?>  

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<!-- s represents the node 'sort'-->
<xsl:template match="s" name="sort_n">

	<!-- Display all of the options selecting the one set in 'sort' field -->
   		<xsl:choose>
          		<xsl:when test="./s = 'none'">
            			<option value='none' selected='selected'>none</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option value='none'>none</option>
          		</xsl:otherwise>
         </xsl:choose>
         	
         <xsl:choose>
         		<xsl:when test="./s = 'A'">
            			<option value='A' selected='selected'>ascend</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option value="A">ascend</option>
          		</xsl:otherwise>
         </xsl:choose>

		<xsl:choose>
          		<xsl:when test="./s = 'D'">
            			<option value='D' selected='selected'>descend</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option value='D' >descend</option>
          		</xsl:otherwise>
       	</xsl:choose>

</xsl:template>

</xsl:stylesheet>