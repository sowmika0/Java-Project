/**
 * This Class provides support for print.
 */
var Print = new (function (){
	var PRINT_HEADER_ID = "printHeader";
	var PRINT_FOOTER_ID = "printFooter";

	/** This function initializes printing feature. Its purpose is to copy header
	 * and footer to the right places in DOM.
     * @public static
	 */
	this.initialize = function() {
		var headerData = document.getElementById(PRINT_HEADER_ID);
		if(headerData != null) {
			// create header and footer 
			// TODO to depend on the order of elements can be danger
			var tbody = document.getElementsByTagName("body")[0];
			var footerData = document.getElementById(PRINT_FOOTER_ID);
			headerData.parentNode.removeChild(headerData);
			footerData.parentNode.removeChild(footerData);
			tbody.insertBefore(headerData,tbody.firstChild);
			tbody.appendChild(footerData);
		}
	};
})();