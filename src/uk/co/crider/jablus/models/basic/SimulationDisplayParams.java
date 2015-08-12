package uk.co.crider.jablus.models.basic;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.models.basic.Parameters;

public class SimulationDisplayParams extends
        uk.co.crider.jablus.gui.sim.SimulationDisplayParams {
	
	public SimulationDisplayParams(Parameters params){
		super(params);

		/*		colourTables.put(
		Constants.DRIVER_LAND_USE,
		LandUse.getColourTable(params)
);
colourTables.put(
		Constants.DRIVER_LAND_OCCUPANCY,
		LandOccupancy.getColourTable());
colourTables.put(
		Constants.DRIVER_LAND_PRODUCTIVITY,
		LandProductivity.getColourTable());
//colourTables.put(
//		Constants.OUTPUT_REVENUE_GRID,
//		Market.getColourTable(params));
*/

		// Driver dataset properties
		//display.put(Constants.STATIC_FIELD_MAP,            ADD_VALUE | SHOW_VALUE);
		
		display.put(Constants.DATASET_LAND_USE,         HIDDEN);
		display.put(Constants.DRIVER_LAND_OCCUPANCY,    HIDDEN);
		display.put(Constants.DRIVER_LAND_PRODUCTIVITY, HIDDEN);
		display.put(Constants.DRIVER_FARM_LOCATION,     HIDDEN);
		display.put(Constants.DRIVER_ROAD_NETWORK,      HIDDEN);
		display.put(Constants.DATASET_MARKET,           HIDDEN);
		display.put(Constants.DRIVER_MARKET_PRICE,      HIDDEN); 
		display.put(Constants.DRIVER_MARKET_DEMAND,     HIDDEN);
		
		// Output dataset properties
//		display.put(Constants.OUTPUT_REVENUE,           ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.OUTPUT_REVENUE_GRID,      HIDDEN);
//		display.put(Constants.OUTPUT_OPTIMALLITY,       ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.OUTPUT_CELLS_CHANGED,     ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.OUTPUT_DISTRIBUTION,      HIDDEN);
//		display.put(Constants.OUTPUT_LAND_USE_COUNT,    ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
		
		// Driver dataset properties
		display1D.put(Constants.OUTPUT_LAND_USE_COUNT, new int[] {
				ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH,
				ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH
			});

	
		
		
	}
}
