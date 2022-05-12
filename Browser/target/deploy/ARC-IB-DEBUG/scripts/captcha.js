/** 
 * @fileoverview Functions related to jCaptcha images/requests
 *
 */

/**
 * Get the position of the click on a Fish Eye Captcha image and set a field accordingly
 */
function getFishEyePosition( thisEvent )
{
	var sender = thisEvent.target || thisEvent.srcElement;
	var pos_x;
	var pos_y;
  
	// Calculatw the clicked point's coordinates
	if ( thisEvent.offsetX ) 	//IE
	{
		pos_x = thisEvent.offsetX;
		pos_y = thisEvent.offsetY;
	}   
	else if ( thisEvent.pageX )	//Firefox, Opera
	{   
		var left = sender.offsetLeft;
		var top = sender.offsetTop;
		var parentNode = sender.offsetParent;

		while ( parentNode != null && parentNode.offsetLeft != null && parentNode.offsetTop != null)
		{
			left += parentNode.offsetLeft;
			top += parentNode.offsetTop;
			parentNode = parentNode.offsetParent;
		}
		
		pos_x = thisEvent.pageX - left;
		pos_y = thisEvent.pageY - top;
	}
	
    var pos = pos_x + "," + pos_y;

    return pos;
}

/**
 * Submits a request to a servlet after getting the click position on the image
 */
function submitFishEyeRequest( thisEvent )
{
	var pos = getFishEyePosition( thisEvent );
	window.document.ofsForm.captchaResponse.value = pos;
	submitRequest();
}

/**
 * Refreshes the Captcha image
 */
function refreshCaptchaImage()
{
	var img = document.getElementById( "captchaImage" );
	
	var oldSrc = img.src;
	var randNo = Math.random();
	
	img.src = oldSrc + "?random=" + randNo;
}