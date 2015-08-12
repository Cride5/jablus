package uk.co.crider.jablus.data;


import java.util.Collection;

/** Represents an object using data items */
public interface DataSet extends Data{
	
	/** Returns whether the dataset is contained in its own file */
	public boolean hasFile();
	
	/** Returns they key used to uniquely identify data from this dataSet, may return null */
	public Data getKey();
	
	/** Returns the dataitem with the given name */
	public Data getItem(int id);
	
	/** Adds the item to the dataset if not already present */
	public void addItem(Data item);

	/** Updates the item matching the given item */
	public void setItem(Data item);

	/** Returns all the data items */
	public Collection<? extends Data> getItems();
		
}
