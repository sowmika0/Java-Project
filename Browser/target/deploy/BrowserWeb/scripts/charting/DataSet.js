//***
// --------- DataSet ------------
//
// This object definition represents a dataset which contains a number of 
// data series.
//
//****

// Constructor.
function DataSet(Title)
{
// Class variables.
    this.title = Title;
    this.dataSeriesList = new Array();
    this.numOfDataSeries = '';
    this.isSingleSeries = false;
// Class methods.
    this.addSeries = dataSetAddSeries;
    this.seriesAt = dataSetSeriesAt;
    this.getMin = dataSetGetMinimum;
    this.getMax = dataSetGetMaximum;
    this.getMaxSeriesNum = dataSetGetMaxSeriesNum;
}
// Add a dataseries to the dataset.
function dataSetAddSeries(aDataSeries)
{
    this.dataSeriesList[this.dataSeriesList.length] = aDataSeries;
    this.numOfDataSeries = this.dataSeriesList.length;
} 
// Returns the series at 'seriesIndex'
function dataSetSeriesAt(seriesIndex)
{
    return this.dataSeriesList[seriesIndex];
}
// Returns the minimum value for the given variable in this dataset.
// varName is defined in the DataItem definition which comes from the server.
// This method should be used for example: when trying to find the minimum value 
// 'varName' has in this dataset.
function dataSetGetMinimum( varName )
{
    var minimum = 0;
    for( var i = 0; i < this.numOfDataSeries; i++ )
    {
        ds = this.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
            di = ds.itemAt(j);
            value = di[varName] * 1;
            if( isNaN(value) )
            {
                alert("Value should be a number : "+varName+"; "+value);
                return 0;
            }
            // Initial value.
            if( j == 0 && i == 0) 
                minimum = value;
            // Only update minimum if value is less.                            
            if( value < minimum )
                minimum = value;
        }
    }
      
    return minimum;
}
// Returns the maximum value for the given variable in this dataset.
// varName is defined in the DataItem definition which comes from the server.
// This method should be used for example: when trying to find the maximum value 
// 'varName' has in this dataset.
function dataSetGetMaximum( varName )
{
    var maximum = 0;
    for( var i = 0; i < this.numOfDataSeries; i++ )
    {
        ds = this.seriesAt(i);
        for( var j = 0; j < ds.numOfDataItems; j++ )
        {
            di = ds.itemAt(j);
            value = di[varName] * 1;
            if( isNaN(value) )
            {
                alert("Value should be a number : "+varName+"; "+value);
                return 0;
            }
            // Initial value.
            if( j == 0 && i == 0) 
                maximum = value;
            // Only update maximum if value is greater.
            if( value > maximum )
                maximum = value;
        }
    }
    
    return maximum;
}
// This gets the highest number of data points in a series
function dataSetGetMaxSeriesNum()
{
    maxNum = 0;
    for( var i = 0; i < this.numOfDataSeries; i++ )
    {
        ds = this.seriesAt(i);
        if( ds.numOfDataItems > maxNum )
            maxNum = ds.numOfDataItems;
    }
    return maxNum;
}
