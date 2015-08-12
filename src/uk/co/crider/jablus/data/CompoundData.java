package uk.co.crider.jablus.data;


import uk.co.crider.jablus.Constants;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/** @inheritDoc */
public class CompoundData implements DataSet {

	protected int id = uk.co.crider.jablus.Constants.getSerialId();
	public int getId(){ return id; }
	
	protected int key;
	
	protected Map<Integer, Data> dataItems;
	
	protected boolean containsDataSet;
	
	protected boolean hasFile;
   
	public CompoundData(){ this(0, 0, false); }
	public CompoundData(int id){ this(id, 0, false); }
    public CompoundData(int id, boolean hasFile){ this(id, 0, hasFile); }
	public CompoundData(int id, int key){ this(id, key, false); }
    public CompoundData(int id, int key, boolean hasFile){
    	this.id = id;
    	this.key = key;
    	this.hasFile = hasFile;
    	dataItems = new Hashtable<Integer, Data>();
    	containsDataSet = false;
    }
  
    public String getName(){
	    return Constants.getName(id);
    }
    
    public Data getKey(){
    	return key == 0 ? null : getItem(key);
    }
    
    public String stringValue(){
    	return toString();
    }

    public boolean hasFile(){
    	return hasFile;
    }
    
	public Data getItem(int id){
		if(this.id == id) return this;
//System.out.println("CompoundData: Getting " + name + " from... (containsDataSet=" + containsDataSet + ")\n             " + this);
    	Data item = dataItems.get(id);
    	if(item != null) return item;
    	// If not found in immediate data set, search sub-data sets (if they exist)
    	if(containsDataSet){
    		for(Data setItem : getItems()){
    			if(setItem instanceof DataSet){
//System.out.println("CompoundData: found dataSet\n             " + setItem);
    				item = ((DataSet)setItem).getItem(id);
    				if(item != null) return item;
    			}
    		}
    	}
    	// If we reach here, nothing was found, so return null
    	return null;
    }
    
	public Collection<? extends Data> getItems() {
	    return dataItems.values();
    }

	public void addItem(Data data){
    	if(data == null) return;
//System.out.println("CompoundData: Adding item: " + data);
    	// If item is an anonumous (with id=0) dataset, then add each item
    	if(data instanceof DataSet){
    		if(data.getId() == 0){
    			addItems((DataSet)data);
        		return;
    		}
   			containsDataSet = true;
    	}
   		dataItems.put(data.getId(), data);
    }
    
    private void addItems(DataSet items){
		for(Data item : items.getItems()){
			addItem(item);
		}
    }
    
    /** Set the value of a data item, and add it if not present */
    public void setItem(Data value){
    	if(value == null) return;
    	// If the value to be set is a DataSet then set contents of data set
    	if(value instanceof DataSet){
//System.out.println("Setting dataset " + value.getNameId());
    		for(Data item : ((DataSet)value).getItems()){
    			setItem(item);
    		} 
    	}
    	else{
//System.out.println("SataSetImpl: Setting data " + value.getNameId());
    		setValue(value, this);
/*    		// If unable to set the value, then simply add the item
    		if(!setValue(value, this)){
    			addItem(value);
    		}
*/   	}
    }
    // Value may not be a dataset
    private static boolean setValue(Data value, DataSet items){
    	// Get item which needs set
    	Data toSet = null;
//System.out.println("CompoundData: Setting " + value.getNameId() + "," + value);
    	if(value.getId() != 0){
//System.out.println("CompoundData: Retriving " + value.getNameId() + " from " + items);
    		toSet = items.getItem(value.getId());
    	}
    	// If not present then recursivly search DataSets for item 
    	if(toSet == null){
//    		return false;
    		for(Data item : items.getItems()){
    			if(item instanceof DataSet && setValue(value, (DataSet)item))
    				return true;
    		}
    		// If value not found in constituant DataSets then its not present
    		return false;
    	}
//System.out.println("CompoundData: 2");
    	// Item is present, so go ahead with setting its value
    	// Data items must be of compatible type
    	if(value instanceof Data0D.Integer){
//System.out.println("CompoundData: Setting " + value.getNameId() + " to " + ((Data0D.Integer)value).getValue());
    		((Data0D.Integer)toSet).setValue(((Data0D.Integer)value).getValue());
    		return true;
    	}
    	if(value instanceof Data0D.Double){
//  		System.out.println("Setting " + value.getNameId() + " to " + ((Data0D.Double)value).getValue());
    		((Data0D.Double)toSet).setValue(((Data0D.Double)value).getValue());
    		return true;
    	}
    	if(value instanceof Data1D.Integer){
//  		System.out.println("Setting " + value.getNameId() + " to " + ((Data0D.Double)value).getValue());
    		((Data1D.Integer)toSet).setValues(((Data1D.Integer)value).getValues());
    		return true;
    	}
    	if(value instanceof Data1D.Double){
//  		System.out.println("Setting " + value.getNameId() + " to " + ((Data0D.Double)value).getValue());
    		((Data1D.Double)toSet).setValues(((Data1D.Double)value).getValues());
    		return true;
    	}
    	if(value instanceof DataTX){
    		((DataTX)toSet).setText(((DataTX)value).getText());
    		return true;
    	}    		
    	if(value instanceof Data2D)
    		((Data2D)toSet).setData(((Data2D)value).getData());
    	
    	// Value was found so return true
    	return true;
    }
    
    /** Returns a string representation of this data object */
	public String toString(){
		return getName() + dataItems.toString();
	}
	
	/** @inheritDoc */
	public Object clone(){ return new CompoundData(id, key, dataItems, hasFile, containsDataSet); }	
	/** Constructor for cloning this object */ 
	protected CompoundData(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		this.id = id;
		this.key = key;
		this.dataItems = new Hashtable<Integer, Data>();
		this.hasFile = hasFile;
		this.containsDataSet = containsDataSet;
		for(int k : dataItems.keySet()){
			this.dataItems.put(k, (Data)dataItems.get(k).clone());
		}
	}
	
}
