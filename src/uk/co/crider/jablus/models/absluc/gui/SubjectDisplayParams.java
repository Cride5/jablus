package uk.co.crider.jablus.models.absluc.gui;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.models.basic.Parameters;

import java.awt.Color;
import java.awt.Paint;
import java.util.Hashtable;
import java.util.Map;

/** Constants for controlling the SubjectInterface layout and look */
public class SubjectDisplayParams extends DisplayParams{
	
	public static String EXPT_TITLE = "Experiment";
	public static String DATA_COLLECTION_TITLE = "Your Details";
	
	public static final int WIDTH = 590;
	public static final int HEIGHT = 700;
	public static final int GRID_WIDTH = 280;
	public static final int INPUT_PANEL_WIDTH = 280;
	public static final int INPUT_PANEL_HEIGHT = 240;
	
	// Default display properties
	private final Hashtable<Integer, Integer> displayProperties = new Hashtable<Integer, Integer>();
	// Default display properties
	public final Hashtable<Integer, int[]> displayProperties1D = new Hashtable<Integer, int[]>();
	//	public static boolean GUI_HUMAN_EXPT = true;
 	
	// The order in which DataView items are to appear
	public final Map<Integer, Integer> displayOrder = new Hashtable<Integer, Integer>();

	public SubjectDisplayParams(Parameters params){
		super(params);
		
		Paint[] paints = new Paint[params.LAND_USE_COLOUR.length];
		for(int i = 0; i < paints.length; i++)
			paints[i] = new Color(params.LAND_USE_COLOUR[i]);
		paints1D.put(Constants.OUTPUT_LAND_USE_COUNT, paints);
		
		// Driver dataset properties
		displayProperties.put(Constants.TEMPORAL_TIME, ADD_VALUE | SHOW_VALUE);
		displayProperties.put(Constants.DATASET_LAND_USE, 0);
		displayProperties.put(Constants.DRIVER_LAND_OCCUPANCY, 0);
		displayProperties.put(Constants.DRIVER_LAND_PRODUCTIVITY, 0);
		displayProperties.put(Constants.DRIVER_FARM_LOCATION, 0);
		displayProperties.put(Constants.DRIVER_ROAD_NETWORK, 0);
		displayProperties.put(Constants.DATASET_MARKET, 0);
		displayProperties.put(Constants.DRIVER_MARKET_PRICE, 0); 
		displayProperties.put(Constants.DRIVER_MARKET_DEMAND, 0);

		// Output dataset properties
		displayProperties.put(Constants.OUTPUT_NARRATIVE, SHOW_TEXT);
		displayProperties.put(Constants.OUTPUT_REVENUE, ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		displayProperties.put(Constants.OUTPUT_REVENUE_GAME_TOTAL, ADD_VALUE | SHOW_VALUE);
		displayProperties.put(Constants.BANK_TOTAL, ADD_VALUE | SHOW_VALUE);
		displayProperties.put(Constants.OUTPUT_OPTIMALLITY, 0);
		displayProperties.put(Constants.OUTPUT_CELLS_CHANGED, 0);
		displayProperties.put(Constants.OUTPUT_DISTRIBUTION, 0);
		displayProperties.put(Constants.OUTPUT_LAND_USE_COUNT, 0);
		
		// Driver dataset properties
		displayProperties1D.put(Constants.OUTPUT_LAND_USE_COUNT, new int[] {
				0,
				0
			});
		// Driver dataset properties
		displayProperties1D.put(Constants.OUTPUT_LAND_USE_COUNT, new int[] {
				0,
				0
			});
		
		// Orderings
		displayOrder.put(Constants.TEMPORAL_TIME,              1);
		displayOrder.put(Constants.OUTPUT_REVENUE,             2);
		displayOrder.put(Constants.OUTPUT_REVENUE_GAME_TOTAL,  3);
		displayOrder.put(Constants.BANK_TOTAL,                 4);
	}

	public boolean display(String dataItem, int toDisplay){
		Integer display = displayProperties.get(dataItem);
		if(display == null) return false;
		return (display != null) && ((display & toDisplay) > 0);
	}
	
	public Integer getOrder(String dataItem){
		if(displayOrder.containsKey(dataItem))
			return displayOrder.get(dataItem);
		return dataItem.hashCode();
	}
	
}
