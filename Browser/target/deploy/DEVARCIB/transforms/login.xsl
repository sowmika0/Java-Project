<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd" method="html" />
	<xsl:template match="/">
		<xsl:call-template name="T24login"/>
	</xsl:template>
	<xsl:template name="T24login">
		<xsl:param name="msg1"/>
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<title>T24 Sign in</title>
				<style type="text/css">
					body {
						height: 98%;
						font-family: "Trebuchet MS",Tahoma,Arial,Helvetica,sans-serif;
						font-size: small;
						overflow: auto;
						white-space: nowrap;
						background-color:#E0EBEA;
						color:#595856;
						margin:0;
						padding:0;
						text-align:center;
					}
					li {
						list-style-type: none;
					}
					a {						
						white-space: nowrap;
						text-decoration:underline;
						cursor:pointer;
						color: #324986;
					}
					.link {
						vertical-align: top;
						font-size: smaller;
					}
					#logo {
						margin:10px;
					}
					img  {
						border: 0px;
						margin-right: 2px;
					}
					.input_box {
						border: 1px solid #324986;
						width: 200px;
					}
					.input_user{
					    border: 1px solid #324986;
					    width: 200px;
						margin-left: 1px;
						margin-left: 0px \0/IE9; 
					}	
					.sign_in {
					    margin-left: 5px;
						margin-left: 4px \0/IE9; 
					}
					table {
						padding: 0;
						margin: 0;
						empty-cells: show; /* forces borders to be displayed for empty cells,used for tabs */
						border-collapse: collapse; /* equivalent of cellspacing in html */
						border: 0;
					}
					.input-ro {
						border: 1px solid black;
						background-color: #e0e0e0;
					}
					#error {
						text-align: left;
						color: RED;
						font-size: 12px;
					}
					#bottom_text
					{
					height:45px;
					background-color:#3773A7;
					color:white;
					}
					#empty_cell
					{
					visibility: hidden;
					}
					#mydiv {
					height:18px;
					width:800px;
					}
					#logo-window {
						border-bottom: 2px solid #E0EBEA;
					}
					#links {
						text-align:right;
						vertical-align:top;
						width:100%;
					}
					/* notice the use of expression in the b #main to get IE to pretend to support max-width */
					#main {
						background:#FFFFFF none repeat scroll 0 0;
						margin:0 auto;
						max-width:990px;
						min-width:770px;
						padding:0;
						text-align:left;
						*height:535px;
						*overflow:hidden;
						
					}
					#title {
						padding-top: 5px;
						padding-bottom: 15px;
						font-size:large;
						color: #334988;
					}
					span {
						color: #a1a1a1;
					}
					span label, label {
						color:black;
						padding: 0 20px 0 0px;
					}
					.login-window{
					border-radius: 15px;
					position:relative;
					border: 1px solid #324986;
 					padding: 30px;	
					width:500px;
					left:20%;
					maring-left:-250px; 
					margin-top:50px;	
					display: block;
  					behavior:url(../html/PIE.htc);
					-pie-border-radius: 10px;
                    z-index: 0;
					}
					#sign-in {
  					background-color: #C5D6F2;
   					border: 1px solid #324986;
                    border-radius: 5px 5px 5px 5px;
					position:relative;
					behavior:url(../html/PIE.htc);
					-pie-border-radius: 5px;
                    z-index: 0;
					}
					.retail {
						background:transparent url(../plaf/images/default/brands/icon-retail-large.gif) no-repeat scroll left top;
					}
					.corporate {
						background:transparent url(../plaf/images/default/brands/icon-corporate-large.gif) no-repeat scroll left top;
					}					
					.private	{
						background:transparent url(../plaf/images/default/brands/icon-private-large.gif) no-repeat scroll left top;
					}
					.islamic {
						background:transparent url(../plaf/images/default/brands/icon-islamic-large.gif) no-repeat scroll left top;
					}					
					.microfinance {
						background:transparent url(../plaf/images/default/brands/icon-microfinance-large.gif) no-repeat scroll left top;
					}					

					.universal {
						background:transparent url(../plaf/images/default/brands/icon-universal-large.gif) no-repeat scroll left top;
					}	

					
					#markets li {
						display:block;
						line-height:1em;
						float: left;
						margin:0 0 0 1.89%;
						min-height:2em;
						padding:0 0 6px 36px;
						color:#595856;
						white-space:normal;
						font-size:1.3em;
						vertical-align:top;
					}

					#bottom {
						margin: 30px;
					}
				</style>
				<script LANGUAGE="javascript" type="text/javascript">
				  //<![CDATA[
			/*	function error_check()
				{	
					var errObj = document.getElementById("error");
					var isSV = errObj.getAttribute("sv");					
					if ( isSV && isSV == "true" )
					{
					
						document.login.signOnName.value = '';
						window.document.forms["login"].RememberOption.checked=false;
					}
				} */	
//]]>				
				</script>
				
				<script type="text/javascript">

                                //<![CDATA[


			/* function RememberUncheck()
				{
					window.document.forms["login"].RememberOption.checked=false;
				}
		function checkCapsLock(e)
		{
					var ctrl = (document.all) ? event.ctrlKey:e.modifiers & Event.CONTROL_MASK;
				    var kc = e.charCode; 
					var ke = e.keyCode;
		
				if(( ke == 20) || ( kc == 20))
				{
					if(document.login.password.value != '')
						{
							if(document.getElementById('mydiv').style.visibility =='visible')
							document.getElementById('mydiv').style.visibility = 'hidden';
						}
					else
						{
							document.getElementById('mydiv').style.visibility = 'hidden';
						}	
				}
					var evtobj=window.event? event : e
				if (!evtobj.shiftKey)
				{
					if(ke != 8)
						{
							if (kc > 0)
								{
									if(kc >= 97 && kc <= 122)
									{
										 document.getElementById('mydiv').style.visibility= 'hidden';
									 }
									else
									{
									    if(kc >= 65 && kc <= 90)
									    {
									   	  document.getElementById('mydiv').style.visibility= 'visible';
  									    }
									  	else
									  	{
											if(document.getElementById('mydiv').style.visibility == 'visible')
											document.getElementById('mydiv').style.visibility = 'visible';
										}
								   }
					            } 
					    else
					   {
						if(ke >= 97 && ke <= 122)
						{
						  document.getElementById('mydiv').style.visibility= 'hidden';
 						}
						else
						{
					    	if(ke >= 65 && ke <= 90)
						    {
							  document.getElementById('mydiv').style.visibility= 'visible';
  						    }
						  	else
						  	{
								if(document.getElementById('mydiv').style.visibility == 'visible')
								document.getElementById('mydiv').style.visibility = 'visible';
							}
						}
					    }
				        }
				else
				{
					if(document.login.password.value == '')
					document.getElementById('mydiv').style.visibility = 'hidden';
				}
				}
		else
		{
		if(kc >= 97 && kc <= 122)
						    {
						    document.getElementById('mydiv').style.visibility= 'visible';
  						    }
		}
		if (ctrl && e.which)
{
document.getElementById('mydiv').style.visibility = 'hidden';
}
		}
		*/
 				</script>


			<script LANGUAGE="javascript" type="text/javascript">

				
			function readCookie(c_name)
				{
					var i,x,y,nuller='',ARRcookies=document.cookie.split(";");
					for (i=0;i<ARRcookies.length;i++)
						{
							x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
							y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
							x=x.replace(/^\s+|\s+$/g,"");
							if (x==c_name)
								{
									return unescape(y);
								}
							else
								{
									return nuller;
								}
						}

				}			
			</script>

				<script LANGUAGE="javascript" type="text/javascript">
					//<![CDATA[
						function setFocus()
						{
						
				// Check where to put the focus		
				var nameField = window.document.forms["login"].signOnName;
				//document.login.signOnName.value = readCookie("TemCookie");
							if (nameField.getAttribute("readonly"))
							{
								window.document.forms["login"].password.focus();
							}
							/*else if( document.login.signOnName.value != '' )
							{
								window.document.forms["login"].RememberOption.checked=true;
							    window.document.forms["login"].password.focus();
							}*/
							else
							{
								window.document.forms["login"].signOnName.focus();
							}
							//For IE if user name is visible then set size to equalize the length with other fields   
							if((navigator.userAgent.toLowerCase().indexOf("msie") != -1) && (nameField.type != "password")) {
								nameField.size = "29";	
							}
							var container = window.document.getElementById("main");
							container.style.width = (document.body.clientWidth > 990)? "990px" : "auto";
							//error_check();
						} 
						
					//]]>
					</script>
					 	<script src="../scripts/ARC/javascriptBrowserDetection.js" type="text/javascript"></script>
 					    <script type="text/javascript">
 								var warningMessage = "Sorry, your browser is not officially supported. " 
 							    + "Only Internet Explorer 6.0 or newer and Firefox 1.5 and newer " 
 								+ "are supported. Do you want to continue?";
 								var redirectionPage = "../html/browsersDownload.html";
 						</script>
 					    <script src="../scripts/ARC/unsupportedBrowserCheck.js" type="text/javascript" ></script>	
			</head>
			<body onload="setFocus()">
<div id="main">
				<div id="top">
					<xsl:call-template name="logo_panel"/>
				</div>
				<div id="middle">
					<xsl:call-template name="login-form"><xsl:with-param name="msg1"><xsl:value-of select="$msg1"/></xsl:with-param></xsl:call-template>
					<xsl:call-template name="markets"/>
				</div>
				<!--div id="bottom"></div-->
				</div>
			</body>
		</html>
	</xsl:template>
	
<xsl:template name="markets">
					

</xsl:template>
	<xsl:template name="logo_panel">
		<div id="logo-window">
<table id="links">

		<tr>
			<td width="1">
					<img  alt="Temenos" src="../plaf/images/default/banner_start.gif" id="logo" style="text-align:left;"/>
			</td>
<td width="80%"> </td>
	

		</tr>

</table>


		
		</div>
	</xsl:template>
	<xsl:template name="login-form">
		<xsl:param name="msg1"/>
		<form name="login" method="post" action="BrowserLoginServlet" AUTOCOMPLETE="OFF">
			<input type="hidden" name="command">
				<xsl:attribute name="value"><xsl:choose><xsl:when test="/responseDetails/login/command!=''"><xsl:value-of select="/responseDetails/login/command"/></xsl:when><xsl:otherwise>login</xsl:otherwise></xsl:choose></xsl:attribute>
			</input>
			<input type="hidden" name="requestType" value="CREATE.SESSION"/>
			<input type="hidden" name="counter">
				<xsl:attribute name="value"><xsl:choose><xsl:when test="/responseDetails/login/counter!=''"><xsl:value-of select="/responseDetails/login/counter"/></xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:attribute>
			</input>
			<input type="hidden" name="branchAdminLogin" value="{/responseDetails/login/branchAdminLogin}"/>
			<input type="hidden" name="signOnNameEnabled" value="{/responseDetails/login/signOnNameEnabled}"/>
			<div class="login-window">
 
				<span id="title"><b>T24 Sign in</b></span>
				<br/>
				<br>
				<font style="color:#004080; font-size: 12px " face="Arial" align="left">Usernames and passwords are case sensitive.</font>
				</br>
								<br/>

								<br>

				<div id="error">
					<xsl:choose>
						<xsl:when test="/responseDetails/login/error!=''">
							<xsl:attribute name="sv">true</xsl:attribute>	
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="sv">false</xsl:attribute>	
						</xsl:otherwise>
					</xsl:choose>									
					<!-- Error message -->
					<xsl:value-of select="/responseDetails/login/error"/>
					
				
				 
				
				<span class="message">
					<xsl:value-of select="$msg1"/>
				</span>
				</div>
				</br>
				<table>
					<tr>
						<td colspan="3"/>
					</tr>
					<tr>
						<td>
							<span>

								<label for="signOnName"><font style="color:#004080; font-size: 14px ;">Username</font></label>
							</span>
						
							<input class="input_user" id="signOnName" name="signOnName" tabindex="1" size="30" style="background-color: #C5D6F2;"  value="{/responseDetails/login/signOnName}">
							<!-- onchange=RememberUncheck() removed -->
								   <xsl:if test="/responseDetails/login/signOnNameEnabled='N'">
									<xsl:attribute name="readonly">readonly</xsl:attribute>
									<xsl:attribute name="class">input-ro</xsl:attribute>
								</xsl:if>
								<xsl:if test="/responseDetails/login/showUserName!='yes'">
									<xsl:attribute name="type">password</xsl:attribute>		
							</xsl:if>
							</input>
						</td>
						<td width="25%" rowspan="2"/>
					</tr>
					<tr>
						<td colspan="3">
													
												</td>
					</tr>
					<tr>
						<td>
							<span>
								<label for="password"><font style="color:#004080; font-size: 14px">Password </font></label>
							</span>
						
						
	<input class="input_box" type="password" id="password" name="password" tabindex="1" size="30" style="background-color: #C5D6F2;" value="{/responseDetails/login/password}"/>

						</td>
					</tr>
					
					
					<!--
					<tr>
			<td colspan="2">
				<div id="mydiv"  style="visibility:hidden ; color:black; font-size: 12px"><img src="../plaf/images/default/capslockon.gif">Caps lock is on.This may cause you to enter your password incorrectly.</img></div>
			</td>
					</tr>
						
						
						<xsl:if test="/responseDetails/login/showUserName='yes'">
					
					<tr>
     					<td colspan="2">
   							<span>
							<label for="empty" id="empty_cell">Username</label>
							</span>
							
						<input tabindex="1" type="checkbox" name="RememberOption"><font style="color:#004080; font-size: 12px">Remember me </font></input>
							
						</td>

					</tr>
					
						</xsl:if>
					-->
					<tr>
						
						<td colspan="3">
							<br>
							<span>
							<label for="empty" id="empty_cell">Username</label>
							</span>
							<input class="sign_in" type="submit" value="Sign in" id="sign-in" tabindex="1" />
							</br>
						</td>

					</tr>
				</table>

					

			</div>

	<br>
	<blockquote>	
	<xsl:for-each select="/responseDetails/login/links/link">

	<td>
					<a class="link">
							

							<xsl:attribute name="href"><xsl:value-of select="target"/></xsl:attribute>
							<xsl:value-of select="caption"/>
					</a>
					
				</td>
							<td width="30px;"><img alt="" src="../plaf/images/default/block.gif"></img></td>
			</xsl:for-each>
	</blockquote>	
<div id="bottom_text"> 

		<pre  style=" font-color:white; word-wrap: break-word; font-weight:bold; font-size: 12px; font-family: Arial"><BR/>            Copyright &#169; Temenos Headquarters SA. All rights reserved.
     <br/>
</pre>
					
			
	</div>		
	</br>
		</form>
	</xsl:template>
</xsl:stylesheet>
