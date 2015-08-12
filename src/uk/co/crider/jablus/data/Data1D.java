package uk.co.crider.jablus.data;


/** Represents data with a series of a values */
public interface Data1D extends Data{
	
	/** Reaturns the Raw header used to represent each item */
	public String[] getRawHeader();

	/** Reaturns the CSV header used to represent each item */
	public String[] getCSVHeader();
	
	/** Returns the data's value as a readable string */
	public String stringValue(int i);
	
	/** Return the length of the data series */
	public int getLength();

	/** Returns the units of this dataset */
	public String getUnits();
	
	public interface Integer extends Data1D{

		/** Set the ith data value */
		public void setValue(int i, int value);

		/** Set all data values */
		public void setValues(int[] values);
		
		/** Get the ith data value */
		public int getValue(int i);

		/** Get the all data values */
		public int[] getValues();
	}
	
	public interface Double extends Data1D{
		
		/** Set the ith data value */
		public void setValue(int i, double value);

		/** Set all data values */
		public void setValues(double[] values);
		
		/** Get the ith data value */
		public double getValue(int i);

		/** Get the all data values */
		public double[] getValues();
	}

}
