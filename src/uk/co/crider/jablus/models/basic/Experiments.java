package uk.co.crider.jablus.models.basic;

import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.awt.Color;

import jwo.landserf.structure.Footprint;

/** Stores default values for various experiments */
public class Experiments {
	
	public static final String ORIG = "Original";
		
	public static Parameters create(String name){
		// Parameters to return
		Parameters e = new Parameters();
		e.EXPERIMENTS = new String[]{ ORIG };

		// Sets default parameter set
		if(name == null || name.equals("")){ name = ORIG; }
		
		
		// EXPERIMENT DEFINITIONS =====================================================				
		
		if(name.equals(ORIG)){
			e.GUI_SIMULATION = true;
			e.SAVE_CYCLE = 1;
			e.ROWS = 32;
			e.COLS = 32;
			e.RASTER_FOOTPRINT = new Footprint(0, 0, 1, 1);
			e.VECTOR_FOOTPRINT = new Footprint(0, 0, e.ROWS, e.COLS);
			e.PRODUCTIVITY_DEFAULT = new double[]{  0.1,  2.0,  4.0,  1.0 };
//			e.PRODUCTIVITY_STDEV = 10.0f; 
//			e.PRODUCTIVITY_MIN =  0.0f; 
//			e.PRODUCTIVITY_MAX =  20.0f; 
			e.PRODUCTIVITY_FRAC_SEED = new long[]{0, 62, 16, 68}; // 0, 2, 4, 7, 12, 16, 21, 46, 53, 59, 62, 63, 64, 67, 68, 69 
			e.AGENT_INITIAL_FUNDS = 200.0; 
			e.AGENT_FINANCE_THRESHOLD = 0.0; 
			e.AGENT_LAND_PURCHASE_THRESHOLD = 10.0; 
			e.LEARNING_AGENT_RISK = 0.1;
			e.AGENT_LOCATIONS = new LandCell[]{
					new LandCell( 8, 8),
					new LandCell(24, 24),
					new LandCell(24, 8),
					new LandCell( 8, 24)};
			e.MARKET_LOCATIONS = new LandCell[]{new LandCell(16,16)};
			e.AGENT_LAND_OCCUPANCY = new MatrixData(0, new float[][]{
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
			}, false);
			e.MAX_ROAD_DIST = 20;
			e.MAX_NODE_DEGREE = 2;
			e.TRANSPORT_ROADS_SEED = 29; // The pre-set road map to use, good ones include 11, 12, 24, 25, 29, 30
			e.LAND_USES = 4;
			e.LAND_USE_NAMES = new String[]{
					"None",
					"Crop",
					"Livestock",
					"Forestry"
				};
			// Land Use Colours
			e.LAND_USE_COLOUR = new int[]{
		    		new Color(237, 237, 237).getRGB(), // Grey for NONE
		    		new Color(242, 225, 148).getRGB(), // Beige for CROP
		    		new Color(152, 212, 126).getRGB(), // Light green for LIVESTOCK
		    		new Color( 92, 190, 145).getRGB(), // Dark green for FORESTRY
		    };
			e.REVENUE_MODEL = MathModels.HOOVER_GIARRATANI;
			e.REVENUE_FACTOR = 1.0; // Provides overall controll over revenues
			e.REVENUE_NOISE = 0.0; // Level of random noise applied to revenue
			e.MARKET_PRICE         = new double[]{  1.0, 20.0, 20.0, 20.0 };
			e.productionCostFixed  = new double[]{  0.0,  0.0,  0.0,  0.0 };
			e.productionCostVar    = new double[]{  1.0,  1.5,  1.5,  1.0 };
			e.productionCostExp    = new double[]{  1.0,  2.0,  2.0,  1.0 };
			e.transportCost        = new double[]{  0.0,  5.0,  5.0,  5.0 };
			e.MARKET_PRICE_CYCLE = new double[][]{
					new double[]{ 0.0, 20.0, 20.0, 10.0 },
					new double[]{ 0.0, 19.0, 19.0, 10.0 },
					new double[]{ 0.0, 18.0, 18.0, 10.0 },
					new double[]{ 0.0, 17.0, 17.0, 10.0 },
					new double[]{ 0.0, 16.0, 18.5, 10.0 },
					new double[]{10.0, 15.0, 20.0, 10.0 },
					new double[]{10.0, 16.0, 19.0, 10.0 },
					new double[]{10.0, 17.0, 18.0, 10.0 },
					new double[]{10.0, 18.0, 17.0, 10.0 },
					new double[]{10.0, 19.0, 18.5, 10.0 }
			};
			e.MARKET_REACTIVE_DEMAND  = new double[]{ 0.0, 0.4, 0.3, 0.3 };
			e.productivityCost    = new double[][]{
					{ 1.00, 1.00, 1.00, 1.00 },
					{ 1.00, 0.99, 1.00, 1.01 },
					{ 1.00, 1.01, 0.99, 1.01 },
					{ 1.00, 1.01, 1.01, 1.00 }
			};
			e.roadSpeeds = new double[]{
					70.0, // For Motorways
					60.0, // For A Roads
					30.0, // For B Roads
					15.0, // For Tracks
					 5.0, // For None
					 5.0  // For internal farm transport
			};
		
			return e;
		}		
		
		// END OF EXPERIMENT DEFINITIONS ===============================================

		return e;
	}

}
