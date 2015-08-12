package uk.co.crider.jablus.models.basic;

/** Constants for the base model */
public class Constants extends uk.co.crider.jablus.Constants{
	
	// Agent categories
	public static final int OPTIMAL_FARMER   = 3;
	public static final int RANDOM_FARMER    = 4;
	public static final int LEARNING_FARMER  = 5;
	public static final int CROP_FARMER      = 6;
	public static final int LIVESTOCK_FARMER = 7;
	public static final int FORESTRY_FARMER  = 8;

	
	// DataSet names - datasets are data which contains multiple seperate data components
	public static final int DATASET_ENVIRONMENT      = 100;
	public static final int DATASET_MARKET           = 101;
	public static final int DATASET_PRODUCTIVITY     = 102;
	public static final int DATASET_LAND_USE         = 103;
	
	// Driver names - drivers are data which depend on data from the previous step and cannot be derived from else where
	public static final int DRIVER_FARM_LOCATION     = 104;
	public static final int DRIVER_LAND_USE          = 105;
	public static final int DRIVER_LAND_OCCUPANCY    = 106;
	public static final int DRIVER_LAND_PRODUCTIVITY = 107;
	public static final int DRIVER_ROAD_NETWORK      = 108;
	public static final int DRIVER_MARKET_PRICE      = 109;
	public static final int DRIVER_MARKET_DEMAND     = 110;
	
	// Output data names
	
	public static final int OUTPUT_LAND_USE_COUNT    = 111;
	
	public static void init(){
		
		// DataSet names - datasets are data which contains multiple seperate data components
		NAMES.put(DATASET_ENVIRONMENT      , "LandUseEnv");
		NAMES.put(DATASET_MARKET           , "MarketData");
		NAMES.put(DATASET_PRODUCTIVITY     , "ProductivityData");
		NAMES.put(DATASET_LAND_USE         , "LandUseData");
		
		// Driver names - drivers are data which depend on data from the previous step and cannot be derived from else where
		NAMES.put(DRIVER_FARM_LOCATION     , "FarmLocation");
		NAMES.put(DRIVER_LAND_USE          , "LandUse");
		NAMES.put(DRIVER_LAND_OCCUPANCY    , "Occupancy");
		NAMES.put(DRIVER_LAND_PRODUCTIVITY , "Productivity");
		NAMES.put(DRIVER_ROAD_NETWORK      , "RoadNetwork");
		NAMES.put(DRIVER_MARKET_PRICE      , "MarketPrice");
		NAMES.put(DRIVER_MARKET_DEMAND     , "MarketDemand");
		
		// Output data names
		NAMES.put(OUTPUT_LAND_USE_COUNT    , "UseCount");
	}
}
