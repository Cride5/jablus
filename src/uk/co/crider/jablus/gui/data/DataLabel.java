package uk.co.crider.jablus.gui.data;

import java.awt.Color;

import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.utils.Utils;

import javax.swing.JLabel;

/** Extention of JLabel linked to a dynamically changing Data0D object */
public class DataLabel extends JLabel {

	/** Unique class ID */
    private static final long serialVersionUID = -1417748979547839953L;
    
    public static final Color POSITIVE = new Color(0, 128, 0);
    public static final Color NEGATIVE = new Color(192, 0, 0);
    
    private Data0D item;
    private boolean name;
    private boolean units;
    private boolean colours;
    private int rnd;
    public DataLabel(Data0D item){ this(item, false, false, false, 0); }
    public DataLabel(Data0D item, boolean name, boolean units, boolean colours, int rnd){
    	this.item = item;
    	this.name = name;
    	this.units = units;
    	this.colours = colours;
    	this.rnd = rnd;
    	redisplay();
    }
    /** Updates label to reflect data's new value */
    public void redisplay(){
//  	System.out.println("Storage Item:" + item);
    	String val = item.getValue().toString();
		double rVal = Utils.round(item.getValue().doubleValue(), rnd);
    	// Currency is a special case
    	if(units && item.getUnits().equals(Constants.UNITS_CURRENCY)){
	    	setText(
	    			(name ? item.getName() + " " : "") +
	    			(rVal < 0 ? "-" : "") + item.getUnits() +
	    			Utils.roundString(Math.abs(item.getValue().doubleValue()), rnd)
	    	);
    	}
    	else{
        	if(item instanceof Data0D.Double)
        		val = "" + Utils.roundString(item.getValue().doubleValue(), rnd);
	    	setText(
	    			(name ? item.getName() + " " : "") +
	    			val + 
	    			(units && !item.getUnits().equals("") ? " " + item.getUnits() : "")
	    	);
    	}
    	if(colours){
	    	if(rVal > 0)
	    		setForeground(POSITIVE);
	    	if(rVal < 0)
	    		setForeground(NEGATIVE);
    	}
    }
    
    public Data0D getData(){
    	return item;
    }
    
    public void printVal(){
    	System.out.println(item);
    }
}
