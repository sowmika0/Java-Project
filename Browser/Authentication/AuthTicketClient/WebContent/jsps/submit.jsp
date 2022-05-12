<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<script>
function initHiddenValues()
{
document.getElementById("enc_message").value = "<%=request.getSession().getAttribute("encodDataBytes")%>"
document.getElementById("enc_key").value = "<%=request.getSession().getAttribute("encodkeyBytes")%>"
document.getElementById("signature").value = "<%=request.getSession().getAttribute("encodDigestBytes")%>"
}
</script>
</head>
<body onload="initHiddenValues();">
<noscript>
<p>
<strong>Note:</strong> Since your browser does not support JavaScript,
you must press the Continue button once to proceed.
</p>
</noscript>
<form action="/ARC-IB/servlet/BrowserServlet" method="post">
<div>
Click the Continue button to send the encrypted Authentication Ticket
<input type="hidden" id="enc_message" name="enc_message" />
<input type="hidden" id="enc_key" name="enc_key" />
<input type="hidden" id="signature" name="signature" />
</div>
<input type="submit" value="Continue"/>
</form>
</body>
</html>
