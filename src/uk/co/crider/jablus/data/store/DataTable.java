package uk.co.crider.jablus.data.store;

import uk.co.crider.jablus.data.DataSet;

import java.util.Hashtable;
import java.util.Vector;

/** Stores table-based data, which can be saved directly as CSV */
public class DataTable {

	// TODO: Use SoftReference objects to allow clearing of objects to save memory

	private DataSetIO io;
	private Vector<DataSet> data;
	private Hashtable<DataSet,Boolean> saved;
	private Hashtable<String, Integer> keyMap; // Maps keys to their corresponding row
//	private boolean allowWrite; // Whether the data may be saved to disk
	
	/** Create a history object from saved data */
	public DataTable(DataSet schema, DataSetIO io){
		this.io = io;
		data = new Vector<DataSet>();
		keyMap = new Hashtable<String, Integer>();
		saved = new Hashtable<DataSet, Boolean>();
//		allowWrite = false;
		// If io is available then read in data
		if(io.isAvailable()){
			try{		
				// Read csv data into dataRows 
				DataSet row = null;
				while((row = io.readDataSet(schema)) != null){
					data.add(row);
					saved.put(row, true);
					if(row.getKey() != null)
						keyMap.put(row.getKey().stringValue(), data.size() - 1);
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
	}
	
	/** Inserts data to the end of this table */
	public void put(DataSet row){
		//io.writeDataSet(row, data.size());
		DataSet stored = (DataSet)row.clone();
		data.add(stored);
		saved.put(stored, false);
		if(stored.getKey() != null)
			keyMap.put(stored.getKey().stringValue(), data.size() - 1);
	}
	
	/** Returns the DataSet identified by key k*/
	public DataSet get(String k){
		Integer i = keyMap.get(k);
		if(i != null)
			return get(i);
		return null;
	}
/*	public boolean isSaved(String k){
		Integer i = keyMap.get(k);
		if(i != null)
			return false;
		return saved.get(get(i));
	}
*/	/** Returns the data set at row i */
	public DataSet get(int i){
		if(i < 0 || i >= data.size()) return null;
		return data.get(i);
	}
	
	public DataSet getLast(){
		return rows() == 0 ? null : data.lastElement();
	}
	
	public DataSet getFirst(){
		return rows() == 0 ? null : data.firstElement();
	}
	
	public int rows(){
		return data.size();
	}
	
//	public void allowWrite(){
//		allowWrite = true;
//	}
	
	/** Allows history object to save and close any files before simulation closes */
	public void finalise(){
//System.out.println("Table.finalise: ");
		// If data is not available and write not allowed then just exit
		if(!io.isAvailable())// && !allowWrite)
			return;
		// Save data
		for(DataSet row : data){
//System.out.println("Table.finalise: ...io.availbale?" + io.isAvailable());
			if(!saved.get(row)){
//System.out.println("Table.finalise: calling io.writeDataSet");
				io.writeDataSet(row);
				saved.put(row, true);
			}
		}
	}
}
