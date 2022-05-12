<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This xsl generates 1-Way OFX message by OFX 2.0 specification. 
1-Way OFX (also caled Active Statements) refers to a mechanism that allows 
Financial Institutions to deliver transaction history (or statement data) 
from Internet banking websites to customers who are using Microsoft Money.  
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!--  all tags are mandatory by spec if not stated otherwise -->
	<xsl:template match="/">
		<!-- processing instruction for OFX processor -->
		<xsl:processing-instruction name="OFX">OFXHEADER="200" VERSION="200" SECURITY="NONE" OLDFILEUID="NONE" NEWFILEUID="NONE"</xsl:processing-instruction>
		<!-- top level element -->
		<OFX>
		  <!-- sign-on message set -->
		  <SIGNONMSGSRSV1>
		  	<!-- sign-on response, both sign-in and banking requests are implicit for 1-Way OFX -->
		    <SONRS>
		      <STATUS>
		      	<!-- success -->
		        <CODE>0</CODE>
		        <SEVERITY>INFO</SEVERITY>
		      </STATUS>
		      <!-- date and time of the server response -->
		      <DTSERVER>
		        <!--  TODO /responseDetails/userDetails/today is probably not the right tag -->
				<xsl:call-template name="ofxDate">
				    <xsl:with-param name="date"><xsl:value-of select="/responseDetails/userDetails/today"/></xsl:with-param>
				</xsl:call-template>	            		
			  </DTSERVER>
			  <!-- language used in text responses -->
			  <!-- TODO template should use user-specified language by ISO-639 -->
		      <LANGUAGE>ENG</LANGUAGE>
		    </SONRS>
		  </SIGNONMSGSRSV1>

		  <!-- banking message set -->		  
		  <BANKMSGSRSV1>
		    <!-- transaction wrapper -->
		    <STMTTRNRS>
		      <!-- client assigned globally-unique ID for this transaction -->
		      <!-- TODO consider rewriting  -->
    		  <TRNUID>1</TRNUID>
		      <STATUS>
			    <!-- success -->
        		<CODE>0</CODE>
		        <SEVERITY>INFO</SEVERITY>
		      </STATUS>
		      <!-- statement response aggregate -->
		      <STMTRS>
		      	<!-- dafault currency for statement -->
		        <CURDEF><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r[4]/c[2]/cap"/></CURDEF>
		        <!--  account-from aggregate -->
		        <BANKACCTFROM>
		          <!-- routing and transit number -->
		          <!-- TODO should be in T24 message -->
		          <BANKID>492900</BANKID>
		          <!-- account number -->
        		  <ACCTID><xsl:value-of select="/responseDetails/window/panes/pane/dataSection/enqResponse/header/r[2]/c[2]/cap"/></ACCTID>
		          <!-- account type -->
		          <!-- TODO should be in T24 message -->
		          <ACCTTYPE>CHECKING</ACCTTYPE>
        		</BANKACCTFROM>
				<!-- transaction data -->
		        <BANKTRANLIST>
				  <!-- start date for transaction -->
        		  <DTSTART><xsl:value-of select="substring-after(/responseDetails/window/panes/pane/selSection/selDets/enqsel/f[2]/d,' ')"/></DTSTART>
				  <!-- end date for transaction -->
		          <DTEND><xsl:value-of select="substring-before(/responseDetails/window/panes/pane/selSection/selDets/enqsel/f[2]/d,' ')"/></DTEND>
		        	<!-- TODO expression [(c[1]/cap[1] != ' ') and (c[1]/cap[1] != '  ')] is necessary only for our testing OFX enquiry -->
		        	<xsl:for-each select="/responseDetails/window/panes/pane/dataSection/enqResponse/r[(c[1]/cap[1] != ' ') and (c[1]/cap[1] != '')]">
					  <!-- statement transaction -->
	        		  <STMTTRN>
						<!-- transaction type -->
						<!-- TODO should be in T24 message -->
						<TRNTYPE>PAYMENT</TRNTYPE>
						<!-- date transaction was posted to the account -->
	            		<DTPOSTED>
							<xsl:call-template name="ofxDate">
							    <xsl:with-param name="date"><xsl:value-of select="c[1]/cap"/></xsl:with-param>
							</xsl:call-template>	            		
	    		        </DTPOSTED>
						<!-- amount of transaction -->
			            <TRNAMT>
			            	<!-- character ',' is not permitted in the amount, it is deleted -->
			            	<xsl:variable name="amount"><xsl:value-of select="translate(c[5]/cap,',','')"/></xsl:variable>
							<xsl:choose>
								<xsl:when test="contains($amount,'-')">-<xsl:value-of select="substring-before($amount,'-')"/></xsl:when>
								<xsl:otherwise><xsl:value-of select="$amount"/></xsl:otherwise>
							</xsl:choose>
		    	        </TRNAMT>
						<!-- transaction ID issued by financial institution -->
        			    <FITID>
							<xsl:value-of select="c[2]/cap"/>
			            </FITID>
						<!-- name of payee or description of the transaction, is optional -->
    	    		    <NAME>
							<xsl:value-of select="c[3]/cap"/>
		    	        </NAME>
                	  </STMTTRN>
					</xsl:for-each>
                	 
                 </BANKTRANLIST>
				<!-- ledger balance aggregate -->
		        <LEDGERBAL>
		          <!-- TODO should be in T24 message -->
		          <BALAMT>0</BALAMT>
		          <!-- TODO should be in T24 message -->
		          <DTASOF>20070116000000</DTASOF>
		        </LEDGERBAL>
		      </STMTRS>
		    </STMTTRNRS>
		 </BANKMSGSRSV1>
		</OFX>		    
		
	</xsl:template>

	<!-- Folowing template is used for transformation of both 29-DEC-2000 and 29 DEC 00 format of date to OFX format 20001229 -->	
	<xsl:template name="ofxDate">
		<xsl:param name="date"/>
		<xsl:value-of select="concat('20',substring($date,string-length($date)-1,2))"/>
		<xsl:variable name="month" select="substring($date,4,3)"/>
		<xsl:choose>
			<xsl:when test="$month='JAN'">01</xsl:when>
			<xsl:when test="$month='FEB'">02</xsl:when>
			<xsl:when test="$month='MAR'">03</xsl:when>
			<xsl:when test="$month='APR'">04</xsl:when>
			<xsl:when test="$month='MAY'">05</xsl:when>
			<xsl:when test="$month='JUN'">06</xsl:when>
			<xsl:when test="$month='JUL'">07</xsl:when>
			<xsl:when test="$month='AUG'">08</xsl:when>
			<xsl:when test="$month='SEP'">09</xsl:when>
			<xsl:when test="$month='OCT'">10</xsl:when>
			<xsl:when test="$month='NOV'">11</xsl:when>
			<xsl:when test="$month='DEC'">12</xsl:when>
		</xsl:choose>
		<xsl:value-of select="substring($date,1,2)"/>
	</xsl:template>
</xsl:stylesheet>