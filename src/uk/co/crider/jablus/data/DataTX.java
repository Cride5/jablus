package uk.co.crider.jablus.data;

/** Represents an object with text data */
public interface DataTX extends Data{
	
	/** Returns the data */
	public String getText();
	
	/** Sets the data */
	public void setText(String value);
	
}
