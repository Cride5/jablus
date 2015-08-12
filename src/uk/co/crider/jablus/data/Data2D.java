package uk.co.crider.jablus.data;


/** Represents an object with raster data.
 * Data is stored in a two-dimensional array,
 * in a column major arrangement - which means that successive
 * columns are stored contigously in memory. */
public interface Data2D extends Data{
	
	/** Returns the data value at the given coordinates */
	float getData(int x, int y);
	
	/** Returns the raster as an array */
	float[][] getData();
	
	/** Returns the highest value in the raster */
//	float getMinValue();

	/** Returns the highest value in the raster */
//	float getMaxValue();
	
	/** Returns the number of rows in this raster */
	int getRows();
	
	/** Returns the number of columns in this raster */
	int getCols();
	
	/** Set the value of a cell */
	void setData(int x, int y, float value);
	
	/** Set the value of a cell */
	void setData(float[][] data);
	
	/** Whether this dataset should be displayed when added */
	boolean toShow();
	
	/** Returns the format of the image file used to represent this data */
	String getFormat();
	
	/** Reads the image file and initialises the raster with the data */
	void readRaster(String file);
	
	/** Writes the contents of this raster to disk, with the given name */
	void writeRaster(String file);
	
}
