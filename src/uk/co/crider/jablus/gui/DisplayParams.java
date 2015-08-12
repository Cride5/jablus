package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.gui.jump.ColourRampStyle;
import uk.co.crider.jablus.gui.jump.ColourTableStyle;

import java.awt.Color;
import java.awt.Paint;
import java.util.Hashtable;

import javax.swing.JComponent;

import org.jfree.chart.ChartColor;

import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** Used to control general display parameters.
 Display parameters for specific interfaces should extend this class */
public class DisplayParams {
	
	// Possible display properties for data items
	public static final int HIDDEN     = 0;
	public static final int ADD_VALUE  = 1;
	public static final int SHOW_VALUE = 2;
	public static final int ADD_GRAPH  = 4;
	public static final int SHOW_GRAPH = 8;
	public static final int AREA_GRAPH = 16;
	public static final int TIME_GRAPH = 32;
	public static final int SHOW_TEXT  = 64;
	public static final int EDITABLE   = 128;

	/** Current simulation parameters */
	public final Parameters params;
	public DisplayParams(Parameters params){ this.params = params; }
		
	/** Maps item ids to display properties */
	protected final Hashtable<Integer, Integer> display
		= new Hashtable<Integer, Integer>();
	/** Maps item ids to display properties for 1D data */
	protected final Hashtable<Integer, int[]> display1D
		= new Hashtable<Integer, int[]>();
	/** Rerturns true if toDisplay is to be shown */
	public boolean display(int dataItem, int toDisplay){
		Integer d = display.get(dataItem);
//System.out.println("DisplayParams: to display " + Constants.getName(dataItem) + " = " + display);
		if(d != null && ((d & toDisplay) != 0))
			return true;
		// If we reach here then item is not to be displayed
		return false;
	}
	
	/** Default paint used if none specified */
	public static final Paint DEFAULT_PAINT = ChartColor.DARK_RED;
	/** Mapping for item ids to drawing paints */
	protected final Hashtable<Integer, Paint> paints = new Hashtable<Integer, Paint>();
	/** Returns paint for given item id */
	public Paint getPaint(int id){
		Paint p = paints.get(id);
		if(p != null) return p;
		return DEFAULT_PAINT;
	}
	/** Returns colour for given item id */
	public Color getColour(int id){
		return (Color)getPaint(id);
	}
	/** Mapping from 1D Data item ids to drawing paints */
	protected final Hashtable<Integer, Paint[]> paints1D
		= new Hashtable<Integer, Paint[]>();
	/** Returns colours for given 1D data item id */
	public Paint[] getPaint1D(int id){
		return paints1D.get(id);
	}

	/** Mapping from item id to data group */
	protected final Hashtable<Integer, Integer> groups
		= new Hashtable<Integer, Integer>();
	/** Returns display group for given item id */
	public int getGroup(int id){
		Integer group = groups.get(id);
		if(group != null) return group;
		return 0;
	}
	
	/** Mapping from item id to Jump Style */
	protected final Hashtable<Integer, Style> styles
		= new Hashtable<Integer, Style>();
	/** Returns Jump Style for given item id */
	public Style getStyle(int id){
		return styles.get(id);
	}
	
	/** Mapping from item id to colour ramp */
	protected final Hashtable<Integer, ColourRampStyle> colourRamps
		= new Hashtable<Integer, ColourRampStyle>();
	/** Returns the colour ramp used to visualise the given item */
	public ColourRampStyle getColourRamp(int id){
		return colourRamps.get(id);
	}
	
	/** Mapping from item id to colour table */
	protected final Hashtable<Integer, ColourTableStyle> colourTables
		= new Hashtable<Integer, ColourTableStyle>();
	/** Returns the colour table used to visualise the given item */
	public ColourTableStyle getColourTable(int id){
		return colourTables.get(id);
	}
	
	/** Style categories */
	public static final int TITLE     = 0;
	public static final int INFO      = 1;
	public static final int ACTIVE    = 2;
	public static final int NORMAL    = 3;
	public static final int SMALL     = 4;
	public static final int POSITIVE  = 5;
	public static final int NEGATIVE  = 6;
	/** Assigns styles to components according to the style category */
	public static void setStyle(JComponent c, int style){
	}
	
}
