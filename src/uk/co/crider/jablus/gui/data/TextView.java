package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.DataTX;

import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;

/** Represents a scrollable text view */
public class TextView extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = 5984020613312682854L;

    private Hashtable<Integer, TextScroller> textItems;
	
	public TextView(){
		super(new GridLayout());
		textItems = new Hashtable<Integer, TextScroller>();
	}
	
	/** Adds text data to the scroller */
	public void addDisplayItem(DataTX data){ addDisplayItem(data, null, null); }
	/** Adds text data to the scroller, along with previous data */
	public void addDisplayItem(DataTX data, List<Data> pastValues, List<Data> time){
		TextScroller scroller = new TextScroller(data, pastValues, time);
		textItems.put(data.getId(), scroller);
		add(scroller);
		revalidate();
	}
	
	/** Causes the scroller to scroll to the given line */
	public void scrollTo(int i) {
	    for(TextScroller s : textItems.values())
	    	s.scrollTo(i);
    }
	
    /** Update all textareas */
    public void redisplay(){
    	for(TextScroller scroller : textItems.values()){
    		scroller.redisplay();
    	}
    }


}
