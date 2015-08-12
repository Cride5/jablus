package uk.co.crider.jablus.models.dairy;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JInternalFrame;

import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.field.Pasture;
import uk.co.crider.jablus.models.dairy.env.field.Soil;
import uk.co.crider.jablus.models.dairy.gui.GrazingPlanner;
import uk.co.crider.jablus.utils.Utils;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SEW_GRASS;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SEW_MAIZE;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SEW_WHEAT;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_GRAZING_ON;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_GRAZING_OFF;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SPREAD_SLURRY;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_FERTILISE;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_HARVEST;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_PLOUGH;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SET_GRAZING;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_SET_CONFINED;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_PARAM_ANIMAL_TYPE;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_PARAM_FID;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_PARAM_PERCENT;
import static uk.co.crider.jablus.models.dairy.agent.DairyAgent.ACTION_PARAM_WEIGHT;
import static uk.co.crider.jablus.models.dairy.env.Livestock.HEIFERS01;
import static uk.co.crider.jablus.models.dairy.env.Livestock.HEIFERS1P;
import static uk.co.crider.jablus.models.dairy.env.Livestock.COWS1ST;
import static uk.co.crider.jablus.models.dairy.env.Livestock.COWS2ND;
import static uk.co.crider.jablus.models.dairy.env.Livestock.COWS3PL;

/** Stores default values for various experiments */
public class Experiments {

	public static final int STATIC_TRAIN = 0; //"Training Scenario";
	public static final int NORMAL_NEW   = -1; // Normal - rolling start;
	public static final int NORMAL_EST = -2; // Normal - starting fresh
	public static final String SCENARIO_1 = "Realistic";
	public static final String SCENARIO_2 = "Climate Change";
	public static final String SCENARIO_3 = "Economic Change";
	public static final String SCENARIO_4 = "Ecological Change";
	public static final String SCENARIO_5 = "General Instability";
	
	public static final String[] EXPT_NAMES = {
		"Training Scenario",
		"Realistic Startup",
		"Realistic Established",
		"Climate Change",
		"Economic Change",
		"Ecological Change",
		"General Instability"
	};
	
	public static final int DEFAULT_EXPT = NORMAL_NEW;
	
	public static final int[] SCENARIOS = {
		STATIC_TRAIN,
//		NORMAL_NEW,
//		NORMAL_EST,
//		SCENARIO_1,
//		SCENARIO_2,
//		SCENARIO_3,
//		SCENARIO_4,
//		SCENARIO_5,
	};
	
	public static final int RND_SCENARIOS = (Integer.MAX_VALUE - Experiments.SCENARIOS.length - 1) / 2;
	
/*	public static boolean exists(String name){
		for(String expt : SCENARIOS)
			if(name.equals(expt)) return true;
		return name.equals("DairyBase");
	}
	
*/	public static String getName(int expt){
		if(expt >= 0 && expt < EXPT_NAMES.length)
			return EXPT_NAMES[expt];
		return null;
	}
	
	public static Parameters create(){ return create(DEFAULT_EXPT); }
	public static Parameters create(int expt){
		int seed = expt - SCENARIOS.length;
		if(seed >= 0){
			if(seed < RND_SCENARIOS){
				expt = NORMAL_NEW;
			}
			else{
				expt = NORMAL_EST;
				seed -= RND_SCENARIOS;
			}
		}else{ 
			seed = 0;
		}
		
//System.out.println("Creating Experiment: " + (expt >= 0 ? EXPT_NAMES[expt] :
//expt == NORMAL_NEW ? "Startup Scenario# " + seed :
//"Established Scenario# " + seed));

		// Parameters to return
		Parameters e = null;

		// EXPERIMENT DEFINITIONS =====================================================

		if(expt == NORMAL_NEW){
			e = new Parameters();

			// Random seeds
			Random gen = new Random(seed);
			e.MARKET_RANDOM_SEED = gen.nextLong();
			e.WEATHER_RANDOM_SEED = gen.nextLong();
			e.LIVESTOCK_RANDOM_SEED = gen.nextLong();

			// Logging
			e.LOGGING_TIME            = false;
			e.LOGGING_ACTIONS         = false;
			e.LOGGING_MARKET          = false;
			e.LOGGING_LIVESTOCK       = false;
			e.LOGGING_CROPS           = false;
			e.LOGGING_SOIL            = false;
			
			// Description
//			e.EXPT_TITLE = "Loading Default Scenario";
			e.EXPT_TITLE = "Scenario# " + seed + " (startup)";
			e.EXPT_INFO = "This is the default scenario with market\r\n" +
					      "and weather trends similar to current reality.";
			
			// Basic simulation properties
			//e.START_STATE = Simulation.STEPWISE;
			e.START_STATE = Simulation.RUNNING;
			//e.START_STATE = e.START_STATE | Simulation.RECORDING;
			e.GUI_SIMULATION = false;
			e.GUI_HUMAN = true;
			e.GUI_HUMAN_CLOSABLE = true;
			e.TIME = true;
			e.TIME_STEP = Time.WEEK;
			e.TIME_CYCLE = 3653; // 10 years
			e.START_YEAR = 2000; 
			e.START_MONTH = 0; // Jan (counting from 0)
			e.START_DAY = 0;
			e.SAVE_CYCLE = 1;
			e.ONE_RUN = false;
			
			// Maps for visualisation
			String sfDir = Constants.JABLUS_DATA_DIR + "/" + "crichton_maps3" + "/";
			e.SHAPEFILE_WATER =  sfDir + "river.shp";
			e.SHAPEFILE_FIELDS = sfDir + "fields.shp";
			e.SHAPEFILE_ROADS = sfDir + "roads.shp";
			e.SHAPEFILE_BUILDS = sfDir + "buildings.shp";
			
			// Agent proerties
			e.AGENT_TYPES = new int[]{
					Constants.DAIRY_FARMER
//					Constants.HUMAN_AGENT
			};
			
			// Market conditions
			e.INIT_BALANCE = 280000; // Start with £280K (same as asset value of established)
//			e.LOAN_LIMIT = 20000; // Resonable loan
			e.INIT_PRICES = new double[]{
					 38.30, // PLOUGH - see: farm management handbook
					  6.90, // FERTILISE - see: farm management handbook
					  7.00, // SPREAD
					 49.80, // GRASS_SOW
					 17.70, // GRASS_CUT - see: farm management handbook
					 71.90, // GRASS_HARVEST - see: farm management handbook
					182.60, // WHEAT_SOW 
					155.00, // WHEAT_HARVEST - see: farm management handbook
					230.00, // MAIZE_SOW
					124.00, // MAIZE_HARVEST - see: farm management handbook
					  0.22, // MILK - Data from farmer's weekly
					  1.25, // LVS_OVERHEADS- from farme management handbook
					 20.00, // INSEMINATION - in reality done once every three weeks, costs about £15 per attmept, see http://www.rbst.org.uk/stock-exchange/artificial-insemination
					  0.92, // VETINARY_MEDS - TODO - Check - taken from farm management handbook
					 60.00, // CARCAS_REMOVAL - Based on conversation with Desere TODO: Check
					  0.002, // INTEREST - Equivalent to about 11% APR
					  1000, // FARM_RENT (£/week) - 4K per month, 52k per year, Based on farm found on farmproperty.net, , TODO - Check
					  4.42, // Single Farm Payment £230/52 (per hectare) http://www.sac.ac.uk/learning/geography/agriculture/thecap/capkeydates
					  1000, // STORAGE_LIVESTOCK £/head
					    30, // STORAGE_SIALGE £/m³
					    25, // STORAGE_BARN £/m³
					    20, // STORAGE_SLURRY £/m³
					150.00, // FERTILISER - TODO - Check
					150.00, // CONCENTRATES - TODO - Check - taken from farm management handbook
					 67.00, // HAY -  TODO - Check - estimated from internet
					 30.00, // STRAW
					 30.00, // SILAGE_GRASS - TODO - Check - estimated from internet
					110.00, // SILAGE_WHEAT - TODO - Check - estimated from internet
					 40.00, // SILAGE_MAIZE - TODO - Check - estimated from internet
					 37.98, // HEIFER_CALVES
					812.50, // CALVED_HEIFERS
					111.69, // FINISHED_HEIFERS
					 63.71, // CULL_COWS
					 28.28, // BULL_CALVES				
			};
			
			// Weather conditions
			e.FIXED_RAIN = Double.NaN;
			e.FIXED_TEMP = Double.NaN;
			e.FIXED_RAD  = Double.NaN;
			
			// Livestock properties
			e.INIT_TARGET_YIELD = 6000;

			// Stores
			e.INIT_STEADING_CAPACITY = 400;
			e.INIT_MILKING_CAPACITY = 400;
			e.INIT_BARN_CAPACITY = 2000; // For hay, maize, grain and fertilisers 
			e.INIT_SILAGE_CAPACITY = 8000; // 8Kt 
			e.INIT_SLURRY_PIT_CAPACITY = 5000; // m³ (20m x 20m x 2m)
			e.INIT_MANURE_LAGOON_CAPACITY = 100; // 
			
			// Cropping properties
			e.SOIL_TYPE = Soil.CLAY_LOAM;
			e.GRASS_SPECIES = Pasture.SPECIES_RYE_GRASS;

			extractMapData(e);
			return e;
		}
		else if(expt == NORMAL_EST){
			e = create(NORMAL_NEW);
			
			e.EXPT_TITLE = "Scenario# " + seed + " (established)";

			// Random seeds
			Random gen = new Random(seed);
			e.MARKET_RANDOM_SEED = gen.nextLong();
			e.WEATHER_RANDOM_SEED = gen.nextLong();
			e.LIVESTOCK_RANDOM_SEED = gen.nextLong();

			// Start with 100K (gives total assets of around 280K)
			e.INIT_BALANCE = 100000; 

			// Livestock properties
			e.INIT_LIVESTOCK = new int[]{20, 40, 80, 60, 20};
			e.INIT_HEIFERS_CONFINED = 100;
			e.INIT_COWS_CONFINED = 0;
			e.INIT_TARGET_YIELD = 6000;
			e.INIT_OFFERED_CONC = 3;
			e.INIT_OFFERED_HAY = 0;
			e.INIT_OFFERED_GRASS_SILAGE = 20;
			e.INIT_OFFERED_SILAGE_WHEAT = 0;
			e.INIT_OFFERED_SILAGE_MAIZE = 0;
/*			e.INIT_GPLAN_ACTIONS = new Action[]{
					new Action(ACTION_SET_GRAZING, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, HEIFERS1P - Livestock.OFFSET)),
					new Action(ACTION_SET_CONFINED, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, HEIFERS1P - Livestock.OFFSET)), // Heifers1P
					new Action(ACTION_SET_GRAZING, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS1ST - Livestock.OFFSET)),
					new Action(ACTION_SET_CONFINED, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS1ST - Livestock.OFFSET)), // Cows1st
					new Action(ACTION_SET_GRAZING, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS2ND - Livestock.OFFSET)),
					new Action(ACTION_SET_CONFINED, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS2ND - Livestock.OFFSET)), // Cows2nd
					new Action(ACTION_SET_GRAZING, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS3PL - Livestock.OFFSET)),
					new Action(ACTION_SET_CONFINED, new IntegerData(ACTION_PARAM_ANIMAL_TYPE, COWS3PL - Livestock.OFFSET)), // Cows3pl
			};
			e.INIT_GPLAN_SCHEDULE = new int[]{
					20, 30,
					13, 37,
					15, 35,
					15, 35,					
			};
*/			// If a 100ha farm had max grass yield over entire area
			// they would require a storage spce of 100ha * 20t/ha = 2,000 tonnes
			// In practice yields won't be as high and only 20% to 50% of the area
			// will be used

			// Stores
			e.INIT_STORED_CONC = 200;
			e.INIT_STORED_HAY = 200;
			e.INIT_STORED_FERTILISER = 40;
			e.INIT_STORED_SILAGE_GRASS = 2000;
			e.INIT_STORED_SILAGE_WHEAT = 200;
			e.INIT_STORED_SILAGE_MAIZE = 200;
			e.iNIT_STORED_SLURRY = 2500;
			
			// Cropping properties
/*			e.INIT_CPLAN_ACTIONS = new Action[]{
					// Silage fields
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 20)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 24)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 25)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 28)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 29)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 31)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 37)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 43)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 59)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 20)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 24)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 25)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 28)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 29)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 31)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 37)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 43)),
					new Action(ACTION_SEW_GRASS    , new IntegerData(ACTION_PARAM_FID, 59)),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 20), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 24), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 25), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 28), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 29), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 31), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 37), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 43), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 59), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 20)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 24)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 25)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 28)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 29)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 31)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 37)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 43)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 59)),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 20), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 24), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 25), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 28), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 29), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 31), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 37), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 43), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 59), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 20)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 24)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 25)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 28)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 29)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 31)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 37)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 43)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 59)),
					// Grazing fields
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 19)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 27)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 33)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 40)),
					new Action(ACTION_GRAZING_ON   , new IntegerData(ACTION_PARAM_FID, 19)),
					new Action(ACTION_GRAZING_ON   , new IntegerData(ACTION_PARAM_FID, 27)),
					new Action(ACTION_GRAZING_ON   , new IntegerData(ACTION_PARAM_FID, 33)),
					new Action(ACTION_GRAZING_ON   , new IntegerData(ACTION_PARAM_FID, 40)),
					new Action(ACTION_GRAZING_OFF  , new IntegerData(ACTION_PARAM_FID, 19)),
					new Action(ACTION_GRAZING_OFF  , new IntegerData(ACTION_PARAM_FID, 27)),
					new Action(ACTION_GRAZING_OFF  , new IntegerData(ACTION_PARAM_FID, 33)),
					new Action(ACTION_GRAZING_OFF  , new IntegerData(ACTION_PARAM_FID, 40)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 19)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 27)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 33)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 40)),
					// Wheat fields
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 41)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 42)),
					new Action(ACTION_PLOUGH       , new IntegerData(ACTION_PARAM_FID, 46)),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 41), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 42), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 46), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SEW_WHEAT    , new IntegerData(ACTION_PARAM_FID, 41)),
					new Action(ACTION_SEW_WHEAT    , new IntegerData(ACTION_PARAM_FID, 42)),
					new Action(ACTION_SEW_WHEAT    , new IntegerData(ACTION_PARAM_FID, 46)),
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 41)),					
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 42)),					
					new Action(ACTION_HARVEST      , new IntegerData(ACTION_PARAM_FID, 46)),					
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 41), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 42), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
					new Action(ACTION_SPREAD_SLURRY  , new Data[]{new IntegerData(ACTION_PARAM_FID, 46), new IntegerData(ACTION_PARAM_WEIGHT, 10)}),
			};
			e.INIT_CPLAN_SCHEDULE = new int[]{
					// For silage fields
					 5,  5,  5,  5,  5,  5,  5,  5,  5,
					 8,  8,  8,  8,  8,  8,  8,  8,  8,
					11, 11, 11, 11, 11, 11, 11, 11, 11,
					21, 21, 21, 21, 21, 21, 21, 21, 21,
					22, 22, 22, 22, 22, 22, 22, 22, 22,
					31, 31, 31, 31, 31, 31, 31, 31, 31,
					// For grazing fields
					13, 13, 13, 13,
					15, 15, 15, 15,
					35, 35, 35, 35,
					43, 43, 43, 43,
					// For wheat fields
					 4,  4,  4,
					 7,  7,  7,
					11, 11, 11,
					34, 34, 34,
					37, 37, 37,
			};
*/			return e;
		}
		else if(expt == STATIC_TRAIN){
			e = create(NORMAL_NEW);
			e.EXPT_TITLE = "Training Scenario";
			e.EXPT_INFO = "This training scenario maintains steady and\r\n" +
					      "forgiving market and weather conditions to\r\n" +
					      "enable easy exploration of the simulator.";
			e.INIT_BALANCE = 100000; // Start with £100K
			// Half all cultivation costs
			for(int i = Market.PLOUGH; i <= Market.MAIZE_HARVEST; i++)
				e.INIT_PRICES[i - Market.OFFSET] *= 0.5;
			// Half livestock overheads
			e.INIT_PRICES[Market.INSEMINATION - Market.OFFSET] *= 0.5;
			e.INIT_PRICES[Market.VETINARY_MEDS - Market.OFFSET] *= 0.5;
			
			// Half farm rent and subsidies
			e.INIT_PRICES[Market.FARM_RENT - Market.OFFSET] *= 0.5;
			e.INIT_PRICES[Market.SFP - Market.OFFSET] *= 0.5;
			
			// Prevent fluctuation in market prices
			e.STATIC_MARKET_PRICES = new int[]{
				Market.MILK,
				Market.CONCENTRATES,
				Market.HAY,
				Market.SILAGE_GRASS,
				Market.SILAGE_WHEAT,
				Market.HEIFER_CALVES,
				Market.CALVED_HEIFERS,
				Market.FINISHED_HEIFERS,
				Market.CULL_COWS,
				Market.BULL_CALVES,
			};
			e.FIXED_RAIN = 3;
			e.FIXED_TEMP = 15;
			e.FIXED_RAD  = 15;
			e.INIT_LIVESTOCK = new int[]{0, 0, 0, 0, 0};
			e.INIT_TARGET_YIELD = 6000;
			e.INIT_OFFERED_CONC = 0;
			e.INIT_OFFERED_HAY = 0;
			e.INIT_OFFERED_GRASS_SILAGE = 0;
			e.INIT_OFFERED_SILAGE_WHEAT = 0;
			e.INIT_OFFERED_SILAGE_MAIZE = 0;
			e.INIT_STORED_CONC = 0;
			e.INIT_STORED_HAY = 0;
			e.INIT_STORED_FERTILISER = 0;
			e.INIT_STORED_SILAGE_GRASS = 0;
			e.INIT_STORED_SILAGE_WHEAT = 0;
			e.INIT_STORED_SILAGE_MAIZE = 0;
//System.out.println("setting " + Utils.arrayString(e.STATIC_MARKET_PRICES));
			return e;
		}
		
		return create();
	}
	
	/** Extracts map data from archive if not present */
	public static void extractMapData(Parameters params){
		String exDir = "examples";
		String[] toExtract = {
				params.SHAPEFILE_WATER,
				params.SHAPEFILE_FIELDS,
				params.SHAPEFILE_ROADS,
				params.SHAPEFILE_BUILDS,
//				params.SHAPEFILE_WATER.replaceAll(".shp$", ".prj"),
//				params.SHAPEFILE_FIELDS.replaceAll(".shp$", ".prj"),
//				params.SHAPEFILE_ROADS.replaceAll(".shp$", ".prj"),
//				params.SHAPEFILE_BUILDS.replaceAll(".shp$", ".prj"),
				params.SHAPEFILE_WATER.replaceAll(".shp$", ".dbf"),
				params.SHAPEFILE_FIELDS.replaceAll(".shp$", ".dbf"),
				params.SHAPEFILE_ROADS.replaceAll(".shp$", ".dbf"),
				params.SHAPEFILE_BUILDS.replaceAll(".shp$", ".dbf"),
				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "example.rgm.csv",
				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "tutorial.sim.csv",
				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "tutorial.rgm.csv",
//				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "training_02.rgm.csv",
//				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "training_03.rgm.csv",
//				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "training_grazing_only.sim.csv",
//				Constants.JABLUS_DATA_DIR + "/" + exDir + "/" + "silage_growth.rgm.csv",
		};
		// Check that all needed shapefiles exist
		boolean exists = true;
		for(String s : toExtract){
			if(!new File(s).exists()){
				exists = false;
				break;
			}
		}
		if(exists) return;
		// Extract files to data dir
		System.out.println("Extracting map files from archive");
		try{
			// Create examples directory
			new File(Constants.JABLUS_DATA_DIR + File.separator + exDir).mkdirs();
			// Create data directory
			new File(Constants.JABLUS_DATA_DIR + File.separator + "crichton_maps3").mkdirs();
			// Extract shapefiles to directory
			JarFile jar = new JarFile("jablus-" + Constants.JABLUS_VERSION + ".jar");
			for(String s : toExtract){
				JarEntry file = jar.getJarEntry(s);
//				jar.getInputStream()
				System.out.println(file.getName());
//				File f = new File(destDir + java.io.File.separator + file.getName());
//				if (file.isDirectory()) { // if its a directory, create it
//				f.mkdir();
//				continue;
//				}
				InputStream is = jar.getInputStream(file); // get the input stream
				FileOutputStream fos = new java.io.FileOutputStream(new File(file.getName()));
				while(is.available() > 0){  // write contents of 'is' to 'fos'
					fos.write(is.read());
				}
				fos.close();
				is.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
