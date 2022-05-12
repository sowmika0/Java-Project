//***
// --------- DataSeries ------------
//
// This object definition represents a dataseries which contains a number of 
// data items for that series.
//
//****

// Constructor.
function DataSeries(Title)
{
// Class variables.
    this.title = Title;
    this.dataItemList = new Array();
    this.numOfDataItems = '';
    this.color = '';
// Class methods.
    this.addDataItem = dataSeriesAddDataItem;
    this.itemAt = dataSeriesItemAt;
}
// Add a dataItem to the data series.
function dataSeriesAddDataItem(aDataItem)
{
    aDataItem.Series = this.title;
    this.dataItemList[this.dataItemList.length] = aDataItem;
    this.numOfDataItems = this.dataItemList.length;
}    
// Returns the dataItem at 'itemIndex'
function dataSeriesItemAt(itemIndex)
{
    return this.dataItemList[itemIndex];
}