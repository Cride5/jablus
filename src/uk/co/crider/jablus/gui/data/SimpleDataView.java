package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data0D;

import java.util.LinkedList;
import java.util.List;

/** A panel holding multiple data values or graphs */
public class SimpleDataView extends DataTable {

	/** Unique class ID */
    private static final long serialVersionUID = 8456772746708134399L;
	public List<Data0D> data;
	
	public SimpleDataView(){
		super(new Object[0][0]);
		data = new LinkedList<Data0D>();
	}
	
	public void addDisplayItem(Data0D item){
		data.add(item);
		addAnotherRow(new Object[]{item.getName(), item.getValue(), item.getUnits()});
	}
	
	public void redisplay(){
		removeAll();
		revalidate();
		for(Data0D item : data){
			addAnotherRow(new Object[]{item.getName(), item.getValue(), item.getUnits()});
		}
	}

}
