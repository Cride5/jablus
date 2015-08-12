package uk.co.crider.jablus.models.dairy;

import java.util.List;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.store.DataSetIO;
import uk.co.crider.jablus.data.store.TupleData;
import uk.co.crider.jablus.utils.Utils;

/** Used to represent specific parameter settings for an experiment */
public class Parameters extends uk.co.crider.jablus.Parameters{
		
	// Logging
	public boolean LOGGING_MARKET;
	public boolean LOGGING_LIVESTOCK;
	public boolean LOGGING_CROPS;
	public boolean LOGGING_SOIL;

	// Experiment properties
	public String EXPT_TITLE;
	public String EXPT_INFO;
	
	// Land Properties
    // Path to base aerial raster map
    public String BASE_RASTER_MAP;
    // Path to shapefiles
    public String SHAPEFILE_WATER;
    public String SHAPEFILE_FIELDS;
    public String SHAPEFILE_ROADS;
    public String SHAPEFILE_BUILDS;
    
    // Market parameters
    public long MARKET_RANDOM_SEED;
	public double[] INIT_PRICES;
    public int[] STATIC_MARKET_PRICES;
    public int INIT_BALANCE;
//    public int LOAN_LIMIT;

		
	// Livestock properties
	public long LIVESTOCK_RANDOM_SEED; // Used to resolve fractional quantities into discrete numbers of cows
	public int[] INIT_LIVESTOCK;
	public int INIT_HEIFERS_CONFINED;
	public int INIT_COWS_CONFINED;
	public int INIT_TARGET_YIELD;
	public int INIT_OFFERED_CONC;
	public int INIT_OFFERED_GRASS_SILAGE;
	public int INIT_OFFERED_HAY;
	public int INIT_OFFERED_SILAGE_WHEAT;
	public int INIT_OFFERED_SILAGE_MAIZE;
	public Action[] INIT_GPLAN_ACTIONS;
	public int[] INIT_GPLAN_SCHEDULE;
	
	// Stores
	public int INIT_STEADING_CAPACITY;
	public int INIT_MILKING_CAPACITY;
	public int INIT_BARN_CAPACITY; // For hay, maize, grain and fertilisers 
	public int INIT_SILAGE_CAPACITY; // 100t (may be a bit low)
	public int INIT_SLURRY_PIT_CAPACITY; // 
	public int INIT_MANURE_LAGOON_CAPACITY; // 
	
	public int INIT_STORED_CONC;
	public int INIT_STORED_HAY;
	public int INIT_STORED_FERTILISER ;
	public int INIT_STORED_SILAGE_GRASS;
	public int INIT_STORED_SILAGE_WHEAT;
	public int INIT_STORED_SILAGE_MAIZE;
	public int iNIT_STORED_SLURRY;
	
	// Field and Crop properties
	public int SOIL_TYPE;
	public long WEATHER_RANDOM_SEED;
	public double FIXED_RAIN;
	public double FIXED_TEMP;
	public double FIXED_RAD;
	public int GRASS_SPECIES;
	public Action[] INIT_CPLAN_ACTIONS;
	public int[] INIT_CPLAN_SCHEDULE;
	
	// 
	
//	/** Constructor */
//	public Parameters(String name){ super(name); }
	
	/** Load parameters from disk */
	public TupleData load(DataSetIO io){
		TupleData paramData = super.load(io);
		if(paramData == null) return null;

		LOGGING_MARKET       = Utils.parseBoolean(paramData.get("LOGGING_MARKET"));
		LOGGING_LIVESTOCK       = Utils.parseBoolean(paramData.get("LOGGING_LIVESTOCK"));
		LOGGING_CROPS           = Utils.parseBoolean(paramData.get("LOGGING_CROPS"));
		// Livestock properties
		LIVESTOCK_RANDOM_SEED = Utils.parseInt    (paramData.get("LIVESTOCK_RANDOM_SEED"));
		return paramData;
	}

	
	public TupleData save(DataSetIO io){
		TupleData paramData = super.save(io);
		if(paramData == null) return null;
		paramData.put("LOGGING_MARKET", ""+LOGGING_MARKET);
		paramData.put("LOGGING_LIVESTOCK", ""+LOGGING_LIVESTOCK);
		paramData.put("LOGGING_CROPS", ""+LOGGING_CROPS);
		
		paramData.put("LIVESTOCK_RANDOM_SEED", ""+LIVESTOCK_RANDOM_SEED);
		return paramData;
	}
	
	
	
}
