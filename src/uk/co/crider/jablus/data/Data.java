package uk.co.crider.jablus.data;


/** Class to represent any data set */
public interface Data extends Cloneable{
	
	/** Returns the data's unique identifier */
	public int getId();
	
	/** Returns the data's name */
	public String getName();
	
	/** Returns a string representation of the data's value */
	public String stringValue();

	/** Returns a clone of the data */
	public Object clone();
		
}
