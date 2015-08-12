package uk.co.crider.jablus.models.basic;

import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.data.store.DataSetIO;
import uk.co.crider.jablus.data.store.TupleData;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.utils.Utils;

import java.io.File;
import java.util.Vector;

import jwo.landserf.structure.Footprint;

/** Used to represent specific parameter settings for an experiment */
public class Parameters extends uk.co.crider.jablus.Parameters{
	
	// Land Properties
	public int ROWS;
	public int COLS;
	public Footprint RASTER_FOOTPRINT;
	public Footprint VECTOR_FOOTPRINT;
	public int LAND_USES;
	// Land Uses
	public String[] LAND_USE_NAMES = new String[0];
	// Land Use Colours
    public int[] LAND_USE_COLOUR = new int[0];
    	
	// Agent properties
	public double AGENT_INITIAL_FUNDS; // Initial agent bank balance
	public double AGENT_FINANCE_THRESHOLD; // Balance required before agent will sell try to increase financial capital
	public double AGENT_LAND_PURCHASE_THRESHOLD; // Balance required before agent will purchase more land
	public double LEARNING_AGENT_RISK;
	public LandCell[] AGENT_LOCATIONS = new LandCell[0];
	public Data2D AGENT_LAND_OCCUPANCY;
	
	// Economic Properties
	public int ECONOMIC_RANDOM_SEED; // Used to generate noise
	public int    REVENUE_MODEL;
	public double REVENUE_FACTOR; // Provides overall controll over revenues
	public double REVENUE_NOISE; // Level of random noise applied to revenue
	//	 For threshold based revenue transforms
	public int    REWARD_MODEL;
	// For REWARD_MODEL = MathModels.REWARD_THRESHOLD;
	public double REWARD_THRESHOLD_MIN; 
	public double REWARD_THRESHOLD_MAX;
	// For REWARD_MODEL = MathModels.REWARD_POWER;
	public double REWARD_POWER_EXPONANT;
	// For REWARD_MODEL = MathModels.REWARD_SIGMOID;
	public double REWARD_SIGMOID_OFFSET;
	public double REWARD_SIGMOID_GRADIENT;
	
	// Market properites
	public boolean MARKET;                  // Use market related drivers
	public LandCell[] MARKET_LOCATIONS = new LandCell[0];	
	public boolean MARKET_DEMAND_CYCLIC;    // Changing market price for each product
	public double[][] MARKET_PRICE_CYCLE = new double[0][0];
	public boolean MARKET_DEMAND_REACTIVE;  // Changing market price for each product
	public double[] MARKET_REACTIVE_DEMAND = new double[0];
	public double[] MARKET_PRICE = new double[0];
	public double[] productionCostFixed = new double[0];
	public double[] productionCostVar = new double[0];
	public double[] productionCostExp = new double[0];
	public double[] transportCost = new double[0];
	// Effect on relative 'productivities' caused by each land use 
	public double[][] productivityCost = new double[0][0];
	
    // Land Ownership Properties
	public boolean LAND_OWNERSHIP;          // Agents own each land cell used
	public boolean LAND_OWNERSHIP_FRAGMENT; // Whether agents can fragment their land
//	public boolean LAND_OWNERSHIP_RAND_BID; // Use random number to resolve equal bids (more fair, but makes simulation non-deterministic)	
	
	// Land Productivity Properties
	public boolean PRODUCTIVITY;     // Simulate land productivity
	// TODO: SP_HETRO not used anymore to allow mixing of different types
	public boolean PRODUCTIVITY_SP_HETRO;   // Spatial hetreogenety in productivity
	public boolean PRODUCTIVITY_REACTIVE;   // Temporal hetreogenety in productivity
	public double[]    PRODUCTIVITY_DEFAULT = new double[0];
//	public float       PRODUCTIVITY_NO_USE; // Productivity of fallow land
//	public float       PRODUCTIVITY_STDEV; // Standard deviation of productivity where hetreogenity is enabled
//	public float       PRODUCTIVITY_MIN; // Minimum productivity of land (where productivity update is enabled)
	public long[]      PRODUCTIVITY_FRAC_SEED = new long[0];
	public Data2D[]    PRODUCTIVITY_MAPS; 
	// For reactive productivities
	public double[]    PRODUCTIVITY_MAX; // Maximum productivity reactive productivity, where map has not been defined
	public double[]    PRODUCTIVITY_GROWTH_RATE = new double[0];
	public double[]    PRODUCTIVITY_DECLINE_RATE = new double[0];

	// Transport Network properties
	public boolean TRANSPORT;        // Model transport
	public boolean TRANSPORT_MARKET; // Model transport to market 
	public boolean TRANSPORT_FARM;   // Model transport through farm
	public boolean TRANSPORT_ROADS;  // Model transport via roads
	public int MAX_ROAD_DIST;
	public int MAX_NODE_DEGREE;
	public long TRANSPORT_ROADS_SEED; // The pre-set road map to use, good ones include 11, 12, 24, 25, 29, 30
	public double[] roadSpeeds = new double[0];
	
	
	/** Constructor */
//	public Parameters(String name){ super(name); }
	
	/** Load parameters from disk */
	public TupleData load(DataSetIO io){
		TupleData paramData = super.load(io);
		if(paramData == null) return null;

		
		ROWS                    = Utils.parseInt    (paramData.get("ROWS"));
		COLS                    = Utils.parseInt    (paramData.get("COLS"));
		RASTER_FOOTPRINT        = new Footprint(0, 0, 1, 1);
		VECTOR_FOOTPRINT        = new Footprint(0, 0, ROWS, COLS);
		LAND_USES               = Utils.parseInt    (paramData.get("LAND_USES"));
		LAND_USE_NAMES          = Utils.stringArray (paramData.get("LAND_USE_NAMES"));
		LAND_USE_COLOUR         = Utils.intArray    (paramData.get("LAND_USE_COLOUR"));
		// Agent properties
		String[] aLoc           = Utils.stringArray (paramData.get("AGENT_LOCATIONS"));
		AGENT_LOCATIONS         = new LandCell[aLoc.length];
		for(int i = 0; i < aLoc.length; i++) AGENT_LOCATIONS[i] = new LandCell(
				Utils.parseInt(aLoc[i].split("_")[0]),
				Utils.parseInt(aLoc[i].split("_")[1]));
		String occFile          = paramData.get("AGENT_LAND_OCCUPANCY");
		AGENT_LAND_OCCUPANCY    = new MatrixData(0, ROWS, COLS);
		AGENT_LAND_OCCUPANCY.readRaster(io.baseDir + File.separator + occFile);
		// Economic properties
		REVENUE_MODEL           = Utils.parseInt    (paramData.get("REVENUE_MODEL"));
		REVENUE_FACTOR          = Utils.parseDouble (paramData.get("REVENUE_FACTOR"));
		REVENUE_NOISE           = Utils.parseDouble (paramData.get("REVENUE_NOISE"));
		REWARD_MODEL            = Utils.parseInt    (paramData.get("REWARD_MODEL"));
		REWARD_THRESHOLD_MIN    = Utils.parseDouble (paramData.get("REWARD_THRESHOLD_MIN"));
		REWARD_THRESHOLD_MAX    = Utils.parseDouble (paramData.get("REWARD_THRESHOLD_MAX"));
		REWARD_POWER_EXPONANT   = Utils.parseDouble (paramData.get("REWARD_POWER_EXPONANT"));
		REWARD_SIGMOID_OFFSET   = Utils.parseDouble (paramData.get("REWARD_SIGMOID_OFFSET"));
		REWARD_SIGMOID_GRADIENT = Utils.parseDouble (paramData.get("REWARD_SIGMOID_GRADIENT"));		
		MARKET                  = Utils.parseBoolean(paramData.get("MARKET"));
		MARKET_PRICE            = Utils.doubleArray (paramData.get("MARKET_PRICE"));
		MARKET_DEMAND_CYCLIC    = Utils.parseBoolean(paramData.get("MARKET_DEMAND_CYCLIC"));
		int i_ = 0; Vector<String> v = new Vector<String>(); String s;
		while((s = paramData.get("MARKET_PRICE_CYCLE" + i_++)) != null) v.add(s);
		MARKET_PRICE_CYCLE = new double[v.size()][];
		for(i_ = 0; i_ < v.size(); i_++)
			MARKET_PRICE_CYCLE[i_] = Utils.doubleArray((String)v.get(i_));
		MARKET_DEMAND_REACTIVE = Utils.parseBoolean(paramData.get("MARKET_DEMAND_REACTIVE"));
		// Land productivity properties
		PRODUCTIVITY          = Utils.parseBoolean(paramData.get("PRODUCTIVITY"));
		PRODUCTIVITY_DEFAULT  = Utils.doubleArray(paramData.get("PRODUCTIVITY_DEFAULT"));
		PRODUCTIVITY_SP_HETRO = Utils.parseBoolean(paramData.get("PRODUCTIVITY_SP_HETRO"));
		PRODUCTIVITY_MAPS     = new Data2D[LAND_USES];
		for(int i = 0; i < PRODUCTIVITY_MAPS.length; i++){
			String prodFile = paramData.get("PRODUCTIVITY_MAPS" + i);
			if(prodFile != null){
				PRODUCTIVITY_MAPS[i] = new MatrixData(0 +i, ROWS, COLS);
				PRODUCTIVITY_MAPS[i].readRaster(io.baseDir + File.separator + prodFile);
			}
		}
		PRODUCTIVITY_REACTIVE     = Utils.parseBoolean(paramData.get("PRODUCTIVITY_REACTIVE"));
		PRODUCTIVITY_GROWTH_RATE  = Utils.doubleArray(paramData.get("PRODUCTIVITY_GROWTH_RATE"));
		PRODUCTIVITY_DECLINE_RATE = Utils.doubleArray(paramData.get("PRODUCTIVITY_DECLINE_RATE"));
		
		
		return paramData;
	}

	
	public TupleData save(DataSetIO io){
		TupleData paramData = super.save(io);
		if(paramData == null) return null;
		paramData.put("ROWS", ""+ROWS);
		paramData.put("COLS", ""+COLS);
		paramData.put("LAND_USES", ""+LAND_USES);
		paramData.put("LAND_USE_NAMES", Utils.arrayString(LAND_USE_NAMES));
		paramData.put("LAND_USE_COLOUR", Utils.arrayString(LAND_USE_COLOUR));
		// Agent properties
		String[] aLoc = new String[AGENT_LOCATIONS.length];
		for(int i = 0; i < AGENT_LOCATIONS.length; i++)
			aLoc[i] = AGENT_LOCATIONS[i].x + "_" + AGENT_LOCATIONS[i].y;
		paramData.put("AGENT_LOCATIONS", Utils.arrayString(aLoc));
		// Agent Land Occupancy
		String occFile = "OccupancyDefault" + "." + AGENT_LAND_OCCUPANCY.getFormat();
		paramData.put("AGENT_LAND_OCCUPANCY", occFile);
		AGENT_LAND_OCCUPANCY.writeRaster(io.baseDir + File.separator + occFile);
		// Economic properties
		paramData.put("REVENUE_MODEL", ""+REVENUE_MODEL);
		paramData.put("REVENUE_FACTOR", ""+REVENUE_FACTOR);
		paramData.put("REVENUE_NOISE", ""+REVENUE_NOISE);
		paramData.put("REWARD_MODEL", ""+REWARD_MODEL);
		paramData.put("REWARD_THRESHOLD_MIN", ""+REWARD_THRESHOLD_MIN);
		paramData.put("REWARD_THRESHOLD_MAX", ""+REWARD_THRESHOLD_MAX);
		paramData.put("REWARD_POWER_EXPONANT", ""+REWARD_POWER_EXPONANT);
		paramData.put("REWARD_SIGMOID_OFFSET", ""+REWARD_SIGMOID_OFFSET);
		paramData.put("REWARD_SIGMOID_GRADIENT", ""+REWARD_SIGMOID_GRADIENT);			
		paramData.put("MARKET", ""+MARKET);
		paramData.put("MARKET_PRICE", Utils.arrayString(MARKET_PRICE));
		paramData.put("MARKET_DEMAND_CYCLIC", ""+MARKET_DEMAND_CYCLIC);
		for(int i = 0; i < MARKET_PRICE_CYCLE.length; i++)
			paramData.put("MARKET_PRICE_CYCLE" + i, Utils.arrayString(MARKET_PRICE_CYCLE[i]));
		paramData.put("MARKET_DEMAND_REACTIVE", ""+MARKET_DEMAND_REACTIVE);
		// Land productivity properties
		paramData.put("PRODUCTIVITY", ""+PRODUCTIVITY);
		paramData.put("PRODUCTIVITY_DEFAULT", Utils.arrayString(PRODUCTIVITY_DEFAULT));
		paramData.put("PRODUCTIVITY_SP_HETRO", ""+PRODUCTIVITY_SP_HETRO);
		if(PRODUCTIVITY_MAPS != null){
			for(int i = 0; i < PRODUCTIVITY_MAPS.length; i++){
				if(PRODUCTIVITY_MAPS[i] != null){
					String prodFile = "ProductivityDefault" + i + "." + AGENT_LAND_OCCUPANCY.getFormat();
					paramData.put("PRODUCTIVITY_MAPS" + i, prodFile);
					PRODUCTIVITY_MAPS[i].writeRaster(io.baseDir + File.separator + prodFile);
				}
			}
		}
		paramData.put("PRODUCTIVITY_REACTIVE", ""+PRODUCTIVITY_REACTIVE);
		paramData.put("PRODUCTIVITY_GROWTH_RATE", Utils.arrayString(PRODUCTIVITY_GROWTH_RATE));
		paramData.put("PRODUCTIVITY_DECLINE_RATE", Utils.arrayString(PRODUCTIVITY_DECLINE_RATE));
		return paramData;
	}
	
	
	
}
