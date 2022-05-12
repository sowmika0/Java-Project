var supported = true;

if(ie) {
	if(nu < 6.0) {
		supported = false;
	}
} else if(moz) {
	if(moz_brow_nu < 1.5) {
		supported = false;
	}
} 
else if(saf){
	if(nu<5.0){
		supported = false;
	}
}
else if(op){
	if(nu<5.0){
		supported = false;
		}
}
else {
	supported = false;
}

if(!supported) {
	var agree = confirm(warningMessage);
	if(!agree) 
		window.location.href = redirectionPage;
}
