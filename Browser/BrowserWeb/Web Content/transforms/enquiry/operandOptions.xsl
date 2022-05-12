<?xml version="1.0" encoding="UTF-8"?>
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- the node op stands for operand -->
<xsl:template match="op" name="operand_n">

	<!-- Display all of the options selecting the one set in 'operand' field -->
   		<xsl:choose>
   				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'EQ'">
            			<option selected="selected">EQ</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>EQ</option>
          		</xsl:otherwise>
         	</xsl:choose>
         	
         	<xsl:choose>
         		<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'NE'">
            			<option selected="selected">NE</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>NE</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'LK'">
            			<option selected="selected">LK</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>LK</option>
          		</xsl:otherwise>
         	</xsl:choose>
         	
         	<xsl:choose>
         		<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'UL'">
            			<option selected="selected">UL</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>UL</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'GT'">
            			<option selected="selected">GT</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>GT</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'LT'">
            			<option selected="selected">LT</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>LT</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'GE'">
            			<option selected="selected">GE</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>GE</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'LE'">
            			<option selected="selected">LE</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>LE</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'RG'">
            			<option selected="selected">RG</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>RG</option>
          		</xsl:otherwise>
         	</xsl:choose>

		<xsl:choose>
				<!-- the node op stands for operand -->
          		<xsl:when test="./op = 'NR'">
            			<option selected="selected">NR</option>
         		</xsl:when>
          		<xsl:otherwise>
           			 <option>NR</option>
          		</xsl:otherwise>
         	</xsl:choose>

</xsl:template>

</xsl:stylesheet>