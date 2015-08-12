package uk.co.crider.jablus.gui.data;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import uk.co.crider.jablus.data.Data;

/** Represents an item which can be placed into a DataView */
public abstract class DataViewItem extends JPanel{
	
	public DataViewItem(LayoutManager manager){
		super(manager);
	}

	/** Set a new Data object for the view item */
	abstract void setData(Data dataItem);

	/** Re-read and re-display the data item's data */
	abstract void redisplay();
	
	/** The id of the data item displayed */
	public abstract int dataId();

	/** The name of the data item displayed */
//	public abstract String dataName();

}
