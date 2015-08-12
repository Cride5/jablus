package uk.co.crider.jablus.models.dairy;

import java.io.File;

import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.CowDigestion;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.Storage;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.models.dairy.env.field.Field;
import uk.co.crider.jablus.utils.Utils;

/** Constants for the dairy model */
public class Constants extends uk.co.crider.jablus.Constants{
	
	public static final String FILE_EXT_SIM = ".sim.csv";
	public static final String FILE_EXT_RGM = ".rgm.csv";
	
	// Agent categories
	
	// DataSet names - datasets are data which contains multiple seperate data components
	public static final int DATASET_NITROGEN         = 100;
	public static final int DATASET_FIELDS           = 101;
	public static final int DATASET_STORE            = 102;
	
	// Static data - data which doesn't change, such as fields etc
	public static final int STATIC_ROADS             = 200;
	public static final int STATIC_RIVERS            = 201;
	public static final int STATIC_FIELD_MAP         = 202;
	public static final int STATIC_FIELD_ID          = 203;
	public static final int STATIC_FIELD_AREA        = 204;
	public static final int STATIC_BUILDS            = 205;
	public static final int STATIC_FIELD_OID         = 206;
	
	// Input data - data which comes from data input during run-time, eg from human actions or random generation
	public static final int INPUT_FIELD_CROP         = 300;
	public static final int INPUT_RAIN               = 301;
	public static final int INPUT_TEMP               = 302;
	public static final int INPUT_TEMP_MIN           = 3025;
	public static final int INPUT_RAD                = 303;
	public static final int INPUT_RAD_MEAN           = 3035;
	public static final int INPUT_BARN_CAPACITY      = 3045;
	public static final int INPUT_STEADING_CAPACITY  = 304;
	public static final int INPUT_MILKING_CAPACITY   = 305;
	public static final int INPUT_HEIFERS_CONFINED   = 306;
	public static final int INPUT_COWS_CONFINED      = 307;
	public static final int INPUT_RND_SEED           = 308;
	
	// Driver names - drivers are data which depend on data from the previous step and cannot be derived from else where
	public static final int DRIVER_ECONOMICS         = 405;
	public static final int DRIVER_DAIRY_FARM        = 400;
	public static final int DRIVER_LIVESTOCK         = 401;
	public static final int DRIVER_PASTURE           = 402;
	public static final int DRIVER_CROPS             = 403;
	public static final int DRIVER_COW_DIGESTION     = 404;
	
	// Temporal Variables - values which depend directly on data from previous time steps
	public static final int TEMPORAL_COW_MATRIX        = 500;
	public static final int TEMPORAL_SOIL_NITRATE      = 501;
	public static final int TEMPORAL_SOIL_LEACHING     = 502;
	public static final int TEMPORAL_SOIL_TEMP         = 503;
	public static final int TEMPORAL_SOIL_WATER        = 504;
	public static final int TEMPORAL_CROP_DM           = 505;
	public static final int TEMPORAL_CROP_LAI          = 506;
	public static final int TEMPORAL_CROP_YIELD        = 507;

	// Derived Variables - only depend on other values from the current time step
	public static final int DERIVED_TOTAL_COWS          = 600;
	public static final int DERIVED_BREEDING_COWS       = 601;
	public static final int DERIVED_HEIFERS             = 602;
	public static final int DERIVED_COWS                = 603;
	public static final int DERIVED_DRY_COWS            = 604;
	public static final int DERIVED_MILKING_COWS        = 605;
	public static final int DERIVED_COWS_SOLD           = 606;
	public static final int DERIVED_FEMALE_CALVES_SOLD  = 607;
	public static final int DERIVED_MALE_CALVES_SOLD    = 608;
	public static final int DERIVED_CARCASES_REMOVED    = 609;
	public static final int DERIVED_MANURE_PASTURE      = 610;
	public static final int DERIVED_MANURE_SLURRY       = 611;
	public static final int DERIVED_MANURE_DUNG         = 612;
	public static final int DERIVED_TOTAL_MILK          = 613;
	public static final int DERIVED_NITROGEN_TO_PASTURE = 614;
	public static final int DERIVED_NITROGEN_TO_CROPS   = 615;
	public static final int DERIVED_CROPS_DM            = 616;
	public static final int DERIVED_CROPS_SILAGE        = 617;
	public static final int DERIVED_CROPS_WHEAT         = 618;
	public static final int DERIVED_CROPS_MAIZE         = 619;
	public static final int DERIVED_CROPS_NLEACHED      = 620;
	public static final int DERIVED_ECONOMIC_COSTS      = 621;
	public static final int DERIVED_ECONOMIC_REVENUE    = 622;
	public static final int DERIVED_ECONOMIC_PROFIT     = 623;
	public static final int DERIVED_ECONOMIC_BALANCE    = 624;
	public static final int DERIVED_ECONOMIC_VALUE      = 625;
	public static final int DERIVED_ECONOMIC_GROWTH     = 626;
	public static final int DERIVED_ECONOMIC_TRANS      = 627;
	
	// Derived data which isn't used other than for visualisation
	public static final int OUTPUT_MARKET_GAINS         = 650;
	public static final int OUTPUT_SLURRY_OVERFLOW      = 651;
	public static final int OUTPUT_SURPLUS_FEED         = 661;
	public static final int OUTPUT_NITRATE_LEACHED      = 652;
	public static final int OUTPUT_DEAD_CULLS           = 653;
	public static final int OUTPUT_IMPORTED_FERTILISER  = 654;
	public static final int OUTPUT_IMPORTED_ANIMALS     = 655;
	public static final int OUTPUT_IMPORTED_FEEDS       = 656;
	public static final int OUTPUT_SCORE_CROPPING       = 657;
	public static final int OUTPUT_SCORE_LIVESTOCK      = 658;
	public static final int OUTPUT_SCORE_ENVIRONMENT    = 659;
	public static final int OUTPUT_SCORE_FINANCIAL      = 660;
	
	// Data group names (for graphs)
	public static final int DATA_GROUP_MARKET           = 700;
//	public static final int DATA_GROUP_COWS             = 700;
//	public static final int DATA_GROUP_ECONOMIC         = 701;
//	public static final int DATA_GROUP_WEATHER          = 702;
//	public static final int DATA_GROUP_STORE            = 703;
	public static final int DATA_GROUP_LIVESTOCK        = 704;
	public static final int DATA_GROUP_FEEDS            = 705;
	public static final int DATA_GROUP_MILK             = 706;
		
	// Main menu items in gui
	public static final int MENU_SIM_NEW                = 720;
	public static final int MENU_SIM_OPEN               = 721;
	public static final int MENU_SIM_SVAS               = 722;
	public static final int MENU_SIM_QUIT               = 723;
	public static final int MENU_SNO_GEN_NEW            = 731;
	public static final int MENU_SNO_GEN_EST            = 732;
	public static final int MENU_SNO_LOAD               = 733;
	public static final int MENU_REG_NEW                = 750;
	public static final int MENU_REG_OPEN               = 751;
	public static final int MENU_REG_SAVE               = 752;
	public static final int MENU_REG_SVAS               = 753;
	public static final int MENU_HELP                   = 760;

	// Stores
	public static final int STORE_COW_STEADING          = 900;
	public static final int STORE_BARN                  = 901;
	public static final int STORE_SILAGE_CLAMP          = 902;
	public static final int STORE_SLURRY_PIT            = 903;
	public static final int STORE_SLURRY_LAGOON         = 904;
	

	// Set names for all identifiers
	public static void init(){
		
		// DataSet names - datasets are data which contains multiple seperate data components
		NAMES.put(DATASET_NITROGEN         , "Nitrogen");
		NAMES.put(DATASET_FIELDS           , "FarmFields");
		
		// Static data - data which doesn't change, such as fields etc
		NAMES.put(STATIC_ROADS             , "Roads");
		NAMES.put(STATIC_RIVERS            , "Rivers");
		NAMES.put(STATIC_FIELD_MAP         , "FieldsMap");
		NAMES.put(STATIC_FIELD_ID          , "FieldId");
		NAMES.put(STATIC_FIELD_OID         , "OBJECTID");
		NAMES.put(STATIC_FIELD_AREA        , "Area");
		NAMES.put(STATIC_BUILDS            , "Buildings");
		
		// Input data - data which comes from data input during run-time, eg from human actions or random generation
		NAMES.put(INPUT_FIELD_CROP         , "Crop");
		NAMES.put(INPUT_RAIN               , "Rainfall");
		NAMES.put(INPUT_TEMP               , "Average Tempearture");
		NAMES.put(INPUT_RAD                , "Solar Radiation");
		NAMES.put(INPUT_STEADING_CAPACITY  , "Cow Steading Capacity");
		NAMES.put(INPUT_MILKING_CAPACITY   , "Milking Parlour Capacity");
		NAMES.put(INPUT_HEIFERS_CONFINED   , "Heifers Confined");
		NAMES.put(INPUT_COWS_CONFINED      , "Cows Confined");
		NAMES.put(INPUT_RND_SEED           , "Scenario#");
		
		
		// Driver names - drivers are data which depend on data from the previous step and cannot be derived from else where
		NAMES.put(DRIVER_ECONOMICS         , "Market");
		NAMES.put(DRIVER_DAIRY_FARM        , "DairyFarm");
		NAMES.put(DRIVER_LIVESTOCK         , "LivesStock");
		NAMES.put(DRIVER_PASTURE           , "Pasture");
		NAMES.put(DRIVER_CROPS             , "Crops");
		NAMES.put(DRIVER_COW_DIGESTION     , "Cow Digestion");

		
		// Temporal Variables - values which depend directly on data from previous time steps
		NAMES.put(TEMPORAL_COW_MATRIX        , "Cow Matrix");
		NAMES.put(TEMPORAL_SOIL_NITRATE      , "Nitrates");
		NAMES.put(TEMPORAL_SOIL_LEACHING     , "Leaching");
		NAMES.put(TEMPORAL_SOIL_TEMP         , "Soil Temp");
		NAMES.put(TEMPORAL_SOIL_WATER        , "Water");
		NAMES.put(TEMPORAL_CROP_DM           , "Dry Mass");
		NAMES.put(TEMPORAL_CROP_LAI          , "Leaf Area");
		NAMES.put(TEMPORAL_CROP_YIELD        , "Yield");

		// Derived Variables - only depend on other values from the current time step
		NAMES.put(DERIVED_TOTAL_COWS          , "Cattle");
		NAMES.put(DERIVED_BREEDING_COWS       , "Breeding");
		NAMES.put(DERIVED_HEIFERS             , "Heifers");
		NAMES.put(DERIVED_COWS                , "Cows");
		NAMES.put(DERIVED_DRY_COWS            , "Dry");
		NAMES.put(DERIVED_MILKING_COWS        , "Milking");
		NAMES.put(DERIVED_COWS_SOLD           , "Cows Sold");
		NAMES.put(DERIVED_FEMALE_CALVES_SOLD  , "FCalves Sold");
		NAMES.put(DERIVED_MALE_CALVES_SOLD    , "MCalves Sold");
		NAMES.put(DERIVED_CARCASES_REMOVED    , "Carcases");
		NAMES.put(DERIVED_MANURE_PASTURE      , "Pasture Manure");
		NAMES.put(DERIVED_MANURE_SLURRY       , "Slurry Manure");
		NAMES.put(DERIVED_MANURE_DUNG         , "Dung Manure");
		NAMES.put(DERIVED_TOTAL_MILK          , "Total Milk");
		NAMES.put(DERIVED_NITROGEN_TO_PASTURE , "NitrogenPasture");
		NAMES.put(DERIVED_NITROGEN_TO_CROPS   , "NitrogenCrops");
		NAMES.put(DERIVED_CROPS_DM            , "Grazed DM");
		NAMES.put(DERIVED_CROPS_SILAGE        , "Silage");
		NAMES.put(DERIVED_CROPS_WHEAT         , "WheatCrop");
		NAMES.put(DERIVED_CROPS_MAIZE         , "Maize");
		NAMES.put(DERIVED_CROPS_NLEACHED      , "NLeached");
		NAMES.put(DERIVED_ECONOMIC_COSTS      , "Costs");
		NAMES.put(DERIVED_ECONOMIC_REVENUE    , "Revenue");
		NAMES.put(DERIVED_ECONOMIC_PROFIT     , "Profit");
		NAMES.put(DERIVED_ECONOMIC_BALANCE    , "Balance");
		NAMES.put(DERIVED_ECONOMIC_VALUE      , "Asset Value");
		NAMES.put(DERIVED_ECONOMIC_GROWTH     , "Growth");
		NAMES.put(DERIVED_ECONOMIC_TRANS      , "Transactions");

		NAMES.put(OUTPUT_MARKET_GAINS        , "Total Gains");
		NAMES.put(OUTPUT_SLURRY_OVERFLOW     , "Slurry Overflow");
		NAMES.put(OUTPUT_SURPLUS_FEED        , "Surplus Feed Value");
		NAMES.put(OUTPUT_NITRATE_LEACHED     , "Nitrate leached");
		NAMES.put(OUTPUT_DEAD_CULLS          , "Unsellable Cows");
		NAMES.put(OUTPUT_IMPORTED_FERTILISER , "Imported fertiliser");
		NAMES.put(OUTPUT_IMPORTED_ANIMALS    , "Imported animals");
		NAMES.put(OUTPUT_IMPORTED_FEEDS      , "Impoted feeds");
		NAMES.put(OUTPUT_SCORE_CROPPING      , "Cropping");
		NAMES.put(OUTPUT_SCORE_LIVESTOCK     , "Livestock");
		NAMES.put(OUTPUT_SCORE_ENVIRONMENT   , "Environment");
		NAMES.put(OUTPUT_SCORE_FINANCIAL     , "Financial");
		
		// Data group names (for graphs)
		NAMES.put(DATA_GROUP_MARKET    , "Market Prices");
/*		NAMES.put(DATA_GROUP_COWS      , "Cow Data");
		NAMES.put(DATA_GROUP_ECONOMIC  , "Balance");
		NAMES.put(DATA_GROUP_WEATHER   , "Weather Data");
		NAMES.put(DATA_GROUP_STORE     , "Store Data");
*/		NAMES.put(DATA_GROUP_LIVESTOCK , "Livestock Prices");
		NAMES.put(DATA_GROUP_FEEDS     , "Feed Prices");
		NAMES.put(DATA_GROUP_MILK      , "Milk Price");
		
		NAMES.put(MENU_SIM_NEW                , "New");
		NAMES.put(MENU_SIM_OPEN               , "Open...");
		NAMES.put(MENU_SIM_SVAS               , "Save As...");
		NAMES.put(MENU_SIM_QUIT               , "Quit");
		NAMES.put(MENU_SNO_GEN_NEW            , "Startup Scenario...");
		NAMES.put(MENU_SNO_GEN_EST            , "Established Scenario...");
		NAMES.put(MENU_SNO_LOAD               , "Scenario");
		NAMES.put(MENU_REG_NEW                , "New");
		NAMES.put(MENU_REG_OPEN               , "Open...");
		NAMES.put(MENU_REG_SAVE               , "Save");
		NAMES.put(MENU_REG_SVAS               , "Save As...");
		NAMES.put(MENU_HELP                   , "Help");

		NAMES.put(DairyAgent.ACTION_BUY     , "Buy");
		NAMES.put(DairyAgent.ACTION_SELL    , "Sell");
		NAMES.put(DairyAgent.ACTION_REVERT, "< Reset");
		// Field-based actions
		NAMES.put(DairyAgent.ACTION_GRAZING_ON         , "Set Grazing On");
		NAMES.put(DairyAgent.ACTION_GRAZING_OFF        , "Set Grazing Off");
		NAMES.put(DairyAgent.ACTION_SPREAD_SLURRY      , "Spread Slurry");
//		NAMES.put(DairyAgent.ACTION_SPREAD_SLURRY      , "Spread Liquid Manure");
		NAMES.put(DairyAgent.ACTION_FERTILISE          , "Apply Fertiliser");
		NAMES.put(DairyAgent.ACTION_HARVEST            , "Harvest Crop");
		NAMES.put(DairyAgent.ACTION_PLOUGH             , "Plough Field");
		NAMES.put(DairyAgent.ACTION_SEW_GRASS          , "Sow Grass");
		NAMES.put(DairyAgent.ACTION_SEW_MAIZE          , "Sow Maize");
		NAMES.put(DairyAgent.ACTION_SEW_WHEAT          , "Sow Wheat");
		NAMES.put(DairyAgent.ACTION_MOVE_FORWARD       , ">  Set Later");
		NAMES.put(DairyAgent.ACTION_MOVE_BACK          , "<  Set Earlier");
		NAMES.put(DairyAgent.ACTION_REMOVE             , "x  Remove");
		NAMES.put(DairyAgent.ACTION_SET_GRAZING        , "Set Grazing");
		NAMES.put(DairyAgent.ACTION_SET_CONFINED       , "Set Confined");
		NAMES.put(DairyAgent.ACTION_FEED_CONFINED      , "Confined Budget");
		NAMES.put(DairyAgent.ACTION_FEED_GRAZING       , "Grazing Budget");
		NAMES.put(DairyAgent.ACTION_FEED_TYIELD        , "Target Yield");
		// Action Parameters
		NAMES.put(DairyAgent.ACTION_PARAM_WEIGHT       , "Weight");
		NAMES.put(DairyAgent.ACTION_PARAM_QTY          , "Quantity");
		NAMES.put(DairyAgent.ACTION_PARAM_FID          , "Field ID");
		NAMES.put(DairyAgent.ACTION_PARAM_ANIMAL_TYPE  , "Animal Type");
		NAMES.put(DairyAgent.ACTION_PARAM_PRODUCT      , "Product");
		NAMES.put(DairyAgent.ACTION_PARAM_VOLUME       , "Volume");
		NAMES.put(DairyAgent.ACTION_PARAM_CAPACITY     , "Capacity");
		
		
		NAMES.put(Storage.COW_STEADING      , "Cow Steading");
		NAMES.put(Storage.FEED_BARN         , "Feed Barn");
		NAMES.put(Storage.SILAGE_CLAMP      , "Silage Clamp");
		NAMES.put(Storage.SLURRY_PIT        , "Slurry Pit");
//		NAMES.put(Storage.SLURRY_LAGOON     , "Slurry Lagoon");
		
		NAMES.put(Market.PLOUGH             , "Ploughing");
		NAMES.put(Market.FERTILISE          , "Fertilising");
		NAMES.put(Market.SPREAD             , "Slurry Spreading");
		NAMES.put(Market.GRASS_SOW          , "Grass Cultivation");
		NAMES.put(Market.GRASS_CUT          , "Grass Cutting");
		NAMES.put(Market.GRASS_HARVEST      , "Grass Harvesting");
		NAMES.put(Market.WHEAT_SOW          , "Wheat Cultivation");
		NAMES.put(Market.WHEAT_HARVEST      , "Wheat Harvesting");
		NAMES.put(Market.MAIZE_SOW          , "Maize Cultivation");
		NAMES.put(Market.MAIZE_HARVEST      , "Maize Harvesting");
		NAMES.put(Market.FERTILISER         , PRODUCT_CROPPING_FERTILISER);
		NAMES.put(Market.HAY                , "Hay");
		NAMES.put(Market.STRAW              , "Straw");
		NAMES.put(Market.CONCENTRATES       , "Concentrates");
		NAMES.put(Market.SILAGE_GRASS       , "Grass Silage");
		NAMES.put(Market.SILAGE_WHEAT       , "Wheat Silage" );
		NAMES.put(Market.SILAGE_MAIZE       , "Maize Silage");
		NAMES.put(Market.MILK               , "Milk");
		NAMES.put(Market.LVS_OVERHEADS      , "Livestock Expenses");
		NAMES.put(Market.VETINARY_MEDS      , "Vitinary/Medication");
		NAMES.put(Market.INSEMINATION       , "Artificial Insemination");
		NAMES.put(Market.BULL_CALVES        , "Bull Calves");
		NAMES.put(Market.HEIFER_CALVES      , "Heifer Calves");
		NAMES.put(Market.CALVED_HEIFERS     , "Calved Heifers");
		NAMES.put(Market.FINISHED_HEIFERS   , "Finished Heifers");
		NAMES.put(Market.CULL_COWS          , "Cull Cows");
		NAMES.put(Market.CARCAS_REMOVAL     , "Carcas Removal");
		NAMES.put(Market.INTEREST           , "Loan Interest");
		NAMES.put(Market.FARM_RENT          , "Farm Rent");
		NAMES.put(Market.SFP                , "Single Farm Payment");
		NAMES.put(Market.STORAGE_LIVESTOCK  , "Livestock Steading");
		NAMES.put(Market.STORAGE_SILAGE     , "Silage Clamp");
		NAMES.put(Market.STORAGE_BARN       , "Feed Barn");
		NAMES.put(Market.STORAGE_SLURRY     , "Slurry Pit");
		
		
		NAMES.put(Livestock.HEIFERS01       , "Heifers 0-1 yrs");
		NAMES.put(Livestock.HEIFERS1P       , "Heifers 1+ yrs");
		NAMES.put(Livestock.COWS1ST         , "Cows 1st lac");
		NAMES.put(Livestock.COWS2ND         , "Cows 2nd lac");
		NAMES.put(Livestock.COWS3PL         , "Cows 3rd+ lac");
		NAMES.put(Livestock.STEERS          , "Steers");
		NAMES.put(Livestock.BREEDING        , "Breeding Cows");
		NAMES.put(Livestock.MILKING         , "Milking Cows");
		NAMES.put(Livestock.CARCASES        , "Carcases");
		NAMES.put(Livestock.NEWBORN         , "New Calves");
//		NAMES.put(Livestock.MANURE_DRY      , "Dry Manure");
		NAMES.put(Livestock.MANURE_WET      , PRODUCT_MANURE_SLURRY);

		NAMES.put(CowDigestion.FEED_CONC         , "Concentrates");
		NAMES.put(CowDigestion.FEED_HAY          , "Hay");
		NAMES.put(CowDigestion.FEED_SILAGE_GRASS , "Grass Silage");
		NAMES.put(CowDigestion.FEED_SILAGE_WHEAT , "Wheat Silage");
		NAMES.put(CowDigestion.FEED_SILAGE_MAIZE , "Maize Silage");
		NAMES.put(CowDigestion.FEED_GRASS        , "Pasture Grass");
		NAMES.put(CowDigestion.TARGET_YIELD      , "Target Yield");
		NAMES.put(CowDigestion.SLURRY            , PRODUCT_MANURE_SLURRY);
		NAMES.put(CowDigestion.SLURRY_N          , "Slurry N");

		NAMES.put(Crop.FALLOW               , "Fallow");
		NAMES.put(Crop.GRASS                , "Grass");
		NAMES.put(Crop.PASTURE              , "Pasture");
		NAMES.put(Crop.MAIZE                , "Maize");
		NAMES.put(Crop.WHEAT                , "Wheat");
	
		NAMES.put(STORE_COW_STEADING        , "Cow Steading");
		NAMES.put(STORE_BARN                , "Storage Barn");
		NAMES.put(STORE_SILAGE_CLAMP        , "Silage Clamp");
		NAMES.put(STORE_SLURRY_PIT          , "Slurry Pit");
		NAMES.put(STORE_SLURRY_LAGOON       , "Slurry Lagoon");
	}
	

	// Economic products
	public static final String PRODUCT_ANIMALS_HEIFERS01      = "Heifers 0-1 yrs";
	public static final String PRODUCT_ANIMALS_HEIFERS1P      = "Heifers 1+ yrs";
	public static final String PRODUCT_ANIMALS_COWS1ST        = "Cows 1st lac";
	public static final String PRODUCT_ANIMALS_COWS2ND        = "Cows 2nd lac";
	public static final String PRODUCT_ANIMALS_COWS3PL        = "Cows 3rd+ lac";
	public static final String PRODUCT_ANIMALS_COWS           = "Cows";
	public static final String PRODUCT_ANIMALS_HEIFERS        = "Heifers";
	public static final String PRODUCT_ANIMALS_BULLS          = "Bulls";
	public static final String PRODUCT_ANIMALS_STEERS         = "Steers";
	public static final String PRODUCT_ANIMALS_CARCAS_REMOVAL = "Carcases";
	public static final String PRODUCT_LIVESTOCK_VET_MEDS     = "Vetinary/Medicines";
	public static final String PRODUCT_LIVESTOCK_AI           = "Artificial Insemination";
	public static final String PRODUCT_MANURE_DRY             = "Dry Manure";
	public static final String PRODUCT_MANURE_SLURRY          = "Slurry (" + Utils.roundString(1f / CowDigestion.SLURRY_N_RATIO, 0) + " kgN/m" + '\u00B3' +")";
	public static final String PRODUCT_MILK                   = "Milk";
	public static final String PRODUCT_MILKING_EQIP           = "Milking Equipment";
	public static final String PRODUCT_MILKING_LABOUR         = "Milking Labour";
	public static final String PRODUCT_FEED_CONC              = "Concentrates";
	public static final String PRODUCT_FEED_HAY               = "Hay";
	public static final String PRODUCT_FEED_SILAGE            = "Silage";
	public static final String PRODUCT_FEED_WHEAT             = "WheatCrop";
	public static final String PRODUCT_FEED_MAIZE             = "Maize";
	public static final String PRODUCT_CROPPING_FERTILISER    = "Fertilizer (" + Utils.roundString(Field.FERTILISER_N * 100, 0) + "%N)";
	public static final String PRODUCT_CROPPING_SEEDS         = "Seeds";
	public static final String PRODUCT_CROPPING_LIME          = "Lime";
	public static final String PRODUCT_CULTIVATION            = "Cultivation";
	public static final String PRODUCT_CULTIVATION_EQUIP      = "Cultivation Equipment";
	public static final String PRODUCT_CULTIVATION_LABOUR     = "Cultivation Labour";
	public static final String FARM_RENT                      = "Farm Rent";
	public static final String INTEREST                       = "Loan Interest";
	
	 // Units used
	public static final String UNITS_QTY             = "qty";
	public static final String UNITS_NUMBER          = "number";
	public static final String UNITS_VOLUME_LOW      = "litres";
	public static final String UNITS_VOLUME_HIGH     = "m" + '\u00B3';
	public static final String UNITS_WEIGHT_LOW      = "kg";
	public static final String UNITS_WEIGHT_HIGH     = "t";
	public static final String UNITS_WEIGHTDM_LOW    = "kgDM";
	public static final String UNITS_WEIGHTDM_HIGH   = "tDM";
	public static final String UNITS_WEIGHTFW_LOW    = "kgFW";
	public static final String UNITS_WEIGHTFW_HIGH   = "tFW";
	public static final String UNITS_AREA_LOW        = "m" + '\u00B2';
	public static final String UNITS_AREA_HIGH       = "ha";
	public static final String UNITS_PERCENT         = "%";
	public static final String UNITS_TEMPERATURE     = '\u00B0' + "C";
	public static final String UNITS_RAIN            = "mm";
	public static final String UNITS_RADIATION       = "MJ/m" + '\u00B2';
	public static final String UNITS_COWS            = "head";
	// Combined units
	public static final String UNITS_FEED            = UNITS_WEIGHTFW_LOW + "/" + UNITS_COWS;
	public static final String UNITS_MILK_YIELD      = UNITS_VOLUME_LOW + "/" + UNITS_COWS + "/" + UNITS_YEAR;
	public static final String UNITS_MILK_YIELD_DAY  = UNITS_VOLUME_LOW + "/" + UNITS_COWS;
	public static final String UNITS_NITROGEN        = UNITS_WEIGHT_LOW + "N/" + UNITS_AREA_HIGH;
	public static final String UNITS_APPLICATION1    = UNITS_WEIGHT_LOW + "/" + UNITS_AREA_HIGH;
	public static final String UNITS_APPLICATION2    = UNITS_VOLUME_HIGH + "/" + UNITS_AREA_HIGH;
	public static final String UNITS_YIELD           = UNITS_WEIGHTDM_HIGH + "/" + UNITS_AREA_HIGH;

	// GUI Tool Tops
	public static final String TOOL_TIP_BUY          = "Buy products from market";
	public static final String TOOL_TIP_SELL         = "Sell products to market";
	public static final String TOOL_TIP_FEED         = "Set up livestock feeding regime";
	public static final String TOOL_TIP_FPLAN        = "Livestock grazing weekly planner";
	public static final String TOOL_TIP_CPLAN        = "Crop management weekly planner";
	public static final String TOOL_TIP_TRANS        = "View summary of financial transactions";
	public static final String TOOL_TIP_DEC          = "Provide decision making explanation";
	public static final String TOOL_TIP_EVAL         = "View annual farm evaluation";
	public static final String TOOL_TIP_NEXT         = "Continue to next week";
	public static final String TOOL_TIP_NEXT2        = "Continue to next 4 weeks";
	public static final String TOOL_TIP_TIME         = "Current simulation time";
	public static final String TOOL_TIP_BALANCE      = "Farm's available funds";
	public static final String TOOL_TIP_PROFITS      = "Profit gained last week";
	public static final String TOOL_TIP_MAP          = "Right click over field to manage crops/grazing";
	public static final String[] TOOL_TIP_STORES     = {
		"Closed barn for livestock",
		"Walled clamp for feed silage",
		"Closed barn for animal feeds",
		"Open air slurry pit"
	}; 
}
