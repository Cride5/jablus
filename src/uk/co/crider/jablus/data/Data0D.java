package uk.co.crider.jablus.data;



/** Represents an object with individual-valued data */
public interface Data0D extends Data {
	
	/** Returns the units of this dataset */
	public String getUnits();
	
	/** Returns the minimum possible value of this data */
	public Number getMin();
	
	/** Returns the maximum posible value of this data */
	public Number getMax();
	
	/** Set's the new permissable range for this data value */
	public void setRange(Number min, Number max);

	/** Returns true if the number has a range defined */
	public boolean hasRange();
	
	/** Sets the data value */
	public void setValue(Number value);

	/** Sets the data value */
	public Number getValue();

	public interface Double extends Data0D {
		
		/** Sets the data value */
		public void setValue(double value);

		/** Returns the data value */
		public double doubleValue();

	}
	
	public interface Integer extends Data0D {
		
		/** Sets the data value */
		public void setValue(int value);

		/** Returns the data value */
		public int intValue();

	}
}
