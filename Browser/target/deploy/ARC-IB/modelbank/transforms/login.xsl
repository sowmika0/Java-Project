<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd" method="html" />
	<xsl:template match="/">

<html>
<head>
<!-- Forward the user onto the logout page if left inactive for 20 mins, to avoid a session timeout -->
<!-- todo: This value should be based on the session timeout (it could be added to the xsl; or this page converted to a jsp -->
<meta http-equiv="REFRESH" content="1200; url=../modelbank/unprotected/loggedout.jsp"/>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>ARC-IB Sign On</title>
<style type="text/css">

.instruction {
	font-family: Arial;
	margin-left:5px;
	font-size: 12px;
    font-weight: bold;
	color:#000066;
	border:0px;
}
.forgotten {
	font-family: Arial, Courier, monospace;
	font-size: small;
	font-weight: normal;
	color: #FF0000;
}
.reminder {
	font-family: Arial, Times, serif;
	font-size: small;
	font-weight: bold;
	color: #666666;
}
.error {
	
	text-align: left;
	color: #ff0000;
	font-size: smaller;
       }
body {
	background-color: #CCCCCC;
	link:#7B7B7B;
	
}

table.input {

  width:50%;
  padding-top: 1px;
  padding-bottom: 0px;
  border-radius: 10px;
  position:relative;
  behavior:url(../html/PIE.htc);
  -pie-border-radius: 10px;
}
.input_box {
	border: 1px solid #324986;
	
	}

.display_text {
   font-family: Arial;
   font-weight: bold;
   margin-left:5px;
   font-Size: 16px;
   font-weight: bold;
   color: #000066;
}
.instruction_link {
	font-family: Arial;
	margin-left:5px;
	font-size: 11px;    
	color:#000066;
	border:0px;
}
.image {
    position:relative;
 
}
.image_banner{
   position:relative;
   *top: 3px;
}
.image .text {
    position:absolute;
	margin:auto;
	float:right;
    top:10px; /* in conjunction with left property, decides the text position */
    left:490px;
    width:300px; /* optional, though better have one */
	
}
.image_banner .text_banner {
    position:absolute;
	float:right;
    top:50px; /* in conjunction with left property, decides the text position */
    left:490px;
    width:300px; /* optional, though better have one */
	
}
.image .text_footer {
    position:absolute;
	float:center;
    top:10px; /* in conjunction with left property, decides the text position */
    left:250px;
    width:300px; /* optional, though better have one */
	text-align:center;
	
}

</style>
  <script type="text/javascript">
		//<![CDATA[
		function setFocus() {
			// Check where to put the focus
			var nameField = window.document.forms["login"].signOnName;

			if (nameField.getAttribute("readonly")) {
				window.document.forms["login"].password.focus();
			}
			else {
				window.document.forms["login"].signOnName.focus();
			}
			//For IE if user name is visible then set size to equalize the length with other fields   
			if((navigator.userAgent.toLowerCase().indexOf("msie") != -1) && (nameField.type != "password")) {
				nameField.size = "24";	
			}
		}

		function login() {
			window.document.forms["login"].submit();
		}
		//]]>
	</script>
    <script src="../scripts/ARC/javascriptBrowserDetection.js" type="text/javascript"></script>
    <script type="text/javascript">
      var warningMessage = "Sorry, your browser is not officially supported. " 
        + "Only Internet Explorer 6.0 or newer and Firefox 1.5 and newer " 
        + "are supported. Do you want to continue?";
      var redirectionPage = "../modelbank/unprotected/browsersDownload.html";
    </script>
    <script src="../scripts/ARC/unsupportedBrowserCheck.js" type="text/javascript" ></script>
  <script language="JavaScript1.1">
<!--
/*
JavaScript Image slideshow:
*/ //-->
//<![CDATA[
var slideimages=new Array()
var slidelinks=new Array()

var slideshowspeed=4000
var whichlink=0
var whichimage=0
function slideit(){
  if (!document.images)
    return
    document.images.slide.src=slideimages[whichimage].src
    whichlink=whichimage

  if (whichimage<slideimages.length-1)
    whichimage++
  else
    whichimage=0
    setTimeout("slideit()",slideshowspeed)
}

function slideshowimages(){
  for (i=0;i<slideshowimages.arguments.length;i++){
  slideimages[i]=new Image()
  slideimages[i].src=slideshowimages.arguments[i]
  }
}

function slideshowlinks(){
  for (i=0;i<slideshowlinks.arguments.length;i++)
  slidelinks[i]=slideshowlinks.arguments[i]
}

function gotoshow(){
  if (!window.winslide||winslide.closed)
    winslide=window.open(slidelinks[whichlink])
  else
    winslide.location=slidelinks[whichlink]
    winslide.focus()
}
//]]>

</script>
</head>

<body onload="setFocus()" style="margin-left:0;padding-top:0;margin-top:0;">

<table width="60%" align="center" border="0" cellpadding="0" cellspacing="0" bgcolor="#CCCCCC" >
<tr height="50px"></tr>
<tr>
    <td colspan="5">
	<div class="image_banner">

    <img src="../modelbank/unprotected/banner.png" border="0" usemap="#Map" alt="logo" />
    <map name="Map" id="Map">
                <area tabindex="-1" shape="rect" coords="820,4,850,23" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'General', 'height=450,width=450') )"/>
                <area tabindex="-1" shape="rect" coords="870,4,945,22" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Accessibility', 'height=450,width=450') )"/>
                <area tabindex="-1" shape="rect" coords="955,3,1000,22" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'SiteMap', 'height=450,width=450') )"/>
                <area tabindex="-1" shape="rect" coords="1010,2,1050,23" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Legal', 'height=450,width=450') )"/>
                <area tabindex="-1" shape="rect" coords="1060,3,1100,21" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Privacy', 'height=450,width=450') )"/>
    </map>
	<div class="text_banner" >
    <table width="80%" border="0" cellpadding="0" cellspacing="0" align="right" >
	<tr><td colspan="2"><font face="arial">
            <a style="color:#FFFFFF; font-size:11px; TEXT-DECORATION: NONE;" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'General', 'height=450,width=450') )"> Help</a>
			<!--<a style="color:#FFFFFF">|</a>-->
			<a style="color:#FFFFFF; font-size:11px; TEXT-DECORATION: NONE" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Accessibility', 'height=450,width=450') )"> | Accessibility </a>
			<!--<a style="color:#FFFFFF">|</a>-->
			<a style="color:#FFFFFF; font-size:11px; TEXT-DECORATION: NONE" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'SiteMap', 'height=450,width=450') )"> | Site Map </a>
			<!--<a style="color:#FFFFFF">|</a>-->
			<a style="color:#FFFFFF; font-size:11px; TEXT-DECORATION: NONE" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Legal', 'height=450,width=450') )"> | Legal </a>
			<!--<a style="color:#FFFFFF">|</a>-->
			<a style="color:#FFFFFF;  font-size:11px; TEXT-DECORATION: NONE" href="javascript:void (window.open('../modelbank/unprotected/header-help.html', 'Privacy', 'height=450,width=450') )"> | Privacy </a>
	</font></td></tr>
    </table>
	</div></div>
    </td>
</tr>
	
<tr><td><div class="image">
	<img src="../modelbank/unprotected/banner_line.png" name="slide" border="0" style="margin-left:5px;"/> 
</div></td></tr>
<tr>
<td valign="top">
<!--<img src="../modelbank/unprotected/channelBanking.png" name="slide" border="0"/> 
 <script type="text/javascript">
//<![CDATA[
slideshowimages("../modelbank/unprotected/channelBanking.png", "../modelbank/unprotected/security.png", "../modelbank/unprotected/access.png", "../modelbank/unprotected/online.png")
slideit()
//]]>
</script>-->
<div class="image">
  <img alt="" src="../modelbank/unprotected/access.jpg" name="slide" border="0" align = "left" />
  <div class="text">
  <form name="login" method="post" action="BrowserServlet" AUTOCOMPLETE="OFF">
								<input type="hidden" name="command">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/command"/></xsl:attribute>
								</input>

								<input type="hidden" name="requestType" value="CREATE.SESSION"/>

								<input type="hidden" name="counter">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/counter"/></xsl:attribute>
								</input>

								<input type="hidden" name="branchAdminLogin">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/branchAdminLogin"/></xsl:attribute>
								</input>

								<input type="hidden" name="signOnNameEnabled">
									<xsl:attribute name="value"><xsl:value-of select="/responseDetails/login/signOnNameEnabled"/></xsl:attribute>
								</input>

<table class="input" width="50%" align="right"  cellspacing="3" border="0" bgcolor="#EAFAFF" >
<!--<th bgcolor="#3773A7"  align="left" style="color: #3773A7; border=0px;" colspan="2" rowspan="2">.</th>-->
 
<!-- new code-->
   <tr>
    <td colspan="5" style="border:0px;" > <span class="display_text">Online Banking Services</span> </td>
	</tr>
<!--new code-->
<tr>
      <td colspan="5" style="border:0px;" > <span><a href="../../selfregister/html/selfregister/selfRegWelcome.html"  class="instruction_link" >Register for Internet Banking</a></span></td>
      </tr>
  <tr>
    <td style="border:0px;" colspan="2"></td>
  </tr>
  
  <tr><td colspan="2" style="border:0px; font-family: Arial; font-size: 11px; font-color:red;" >

<!-- Error message -->

  <span class="error">
&#160;&#160;<!-- Error message -->
	<xsl:value-of select="/responseDetails/login/error"/>
  </span>
</td></tr>

  <tr>
    <td style="border:0px;"><span class="instruction"> User ID </span></td>
    <td style="border:0px;" width="23%">
    	<input class="input_box" name="signOnName" size="25" STYLE="background-color: #CCCCCC;">
			<xsl:if test="/responseDetails/login/showUserName!='yes'">
				<xsl:attribute name="type">password</xsl:attribute>				
			</xsl:if>	
		</input>
	</td>    
  </tr>
  <tr>
    
  </tr>
  <tr>
    <td style="border:0px;" colspan="2"></td>
  </tr>
  <tr>
    <td style="border:0px;"><span class="instruction">Password </span></td>
  
    <td style="border:0px;" ><input tabindex="0" class="input_box" name="password" size="25"  type="password"  STYLE="background-color: #CCCCCC;"/></td>
    
  </tr>
  <tr>
    
  </tr>
  <tr>
    <td style="border:0px;" colspan="2"></td>
  </tr>
  <tr>
    <td><span class="instruction"> PIN </span></td>
  
    <td style="border:0px;" ><input tabindex="0"  class="input_box" name="pin" size="25"  type="password" STYLE="background-color: #CCCCCC;"/></td>
    
  </tr>
   <tr>
    <td style="border:0px;" colspan="2"></td>
  </tr>
  <tr><td style="border:0px;" ></td><td style="border:0px;" ><input tabindex="0" type="image" src="../modelbank/unprotected/login_new.png" alt="login"/></td></tr>
  <tr>
      <td colspan="5" style="border:0px;" > <span> <a href="../modelbank/unprotected/bind.jsp" class="instruction_link">First time logging in with a secure device</a></span></td>
	  </tr>

 


</table>

</form>
     </div>
</div>
<!--<img src="../modelbank/unprotected/banner2.png" name="slide" border="0"/>-->
</td>

</tr>
<!--<tr><td colspan="5"><hr color="#BCBCBC" size="1"></hr></td></tr>-->
<!--<tr><td colspan="3">-->
<!--<table width="100%" align="center"  cellspacing="5">-->
<!--<table width="80%" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF" >-->
<tr> 
<td>
<div class="image">
<img src="../modelbank/unprotected/footer.png" border="0" style="margin-left:-4px;"/>
<div class="text_footer">
<table width="80%" align="center" border="0" cellpadding="0" cellspacing="0" >
<tr><td>  <font color="#FFFFFF" face="arial" size="2">Copyright &#169; Temenos 2012 </font></td></tr>
</table>
</div>
</div>
</td></tr>

</table>


</body>
</html>

	</xsl:template>

</xsl:stylesheet>
