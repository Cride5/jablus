package uk.co.crider.jablus.data.store;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.data.TextData;

public class TupleData {
	
	private DataSet data;
	private DataTX key;
	private DataTX value;
	private DataTable dataTable;
	
	public TupleData(int id, DataSetIO io){
		data = new CompoundData(id, 0, true);
		key = new TextData(0); data.addItem(key);
		value = new TextData(1); data.addItem(value);
		dataTable = new DataTable(data, io);
	}
	
/*	public boolean isSet(String key){
		return dataTable.isSaved(key);
	}
*/	
	public void put(String key, String value){
		this.key.setText(key);
		this.value.setText(value);
		dataTable.put(data);		
	}
	
	public String get(String key){
		DataSet s = dataTable.get(key);
		if(s != null)
			return ((DataTX)s.getItem(1)).getText();
		return null;
	}

	
	public void finalise(){
		dataTable.finalise();
	}
}
