/** 
 * @fileoverview Functions used for manipulating HTML dynamically
 *
 * @author XXX
 * @version 0.1 
 */

/**
 * Clear all objects from a HTML table
 * @param {TODO} TODO
 */
 function clearTable( _tableObj )
{
	if ( _tableObj != null )
	{
		var tbody = _tableObj.getElementsByTagName("TBODY")[0];
		
		while (tbody.firstChild) 
		{
			tbody.removeChild(tbody.firstChild);
		}
	}
}

/**
 * Add a row to a HTML table
 * @param {TODO} TODO
 */
function addTableRow( 
			_tableObj, 			// Table object to add to
			_tdTextArray, 		// Array of strings to put in the TDs
			_tdClassArray, 		// Array of classes for the TDs
			_tdHrefArray		// Array of hrefs for the TDs
		)
{
	var theParent = window.parent;
	var tbody = _tableObj.getElementsByTagName("TBODY")[0];
	var row = theParent.document.createElement("TR");

	// Loop round all of the table datas creating a TD element
	for (var tdNo = 0; tdNo < _tdTextArray.length; tdNo++)
	{	
		var td = theParent.document.createElement("TD");

		if ( _tdClassArray[ tdNo ] != "" )
		{
			td.className = _tdClassArray[ tdNo ];
		}

		if ( ( _tdHrefArray ) && ( _tdHrefArray[ tdNo ] != "" ) )
		{
			// Create a href object with a span inside it for the link
			var a = theParent.document.createElement('A'); 
			a.setAttribute( 'href', _tdHrefArray[ tdNo ] );
			var span = theParent.document.createElement('SPAN');
			span.appendChild( theParent.document.createTextNode( _tdTextArray[ tdNo ] ) ); 
			a.appendChild( span );
			td.appendChild( a );
		}
		else
		{		
			td.appendChild( theParent.document.createTextNode( _tdTextArray[ tdNo ] ) );
		}
		
		row.appendChild( td );
	}

	tbody.appendChild(row);
}

		