package uk.co.crider.jablus.gui.sim;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.gui.DisplayParams;

import java.awt.Font;

/** Paramaters for controlling the layout and of the simulation GUI */
public class SimulationDisplayParams extends DisplayParams{
	
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 768;
	
	public static final int BUTTON_HEIGHT = 20;
	public static final int LABEL_HEIGHT = 20;
	
	public static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
	
	public static final int DATA_PANEL_WIDTH = 280;
	public static final int DATA_ITEM_HEIGHT = 20;
	public static final int GRAPH_ITEM_HEIGHT = 200;
	public static final int DATA_NAME_WIDTH = 150;
	public static final int DATA_VALUE_WIDTH = 80;
	public static final int TEXT_ROWS = 100;
	

	public SimulationDisplayParams(Parameters params){
		super(params);

		display.put(Constants.TEMPORAL_TIME,            ADD_VALUE | SHOW_VALUE);
		
		// Output dataset properties
		display.put(Constants.OUTPUT_NARRATIVE,         SHOW_TEXT);
		
		// Ordering of displayed items
//		displayOrder.put(Constants.TEMPORAL_TIME,                 0);

	}
	
}
