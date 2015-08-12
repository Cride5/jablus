package uk.co.crider.jablus;


import java.io.File;
import java.util.Hashtable;
import java.util.Map;

/** Global JABLUS constants */
public class Constants {
	
	private static int idSerialGen = 0;
	public static int getSerialId(){ return idSerialGen++; } 

	protected static Map<Integer, String> NAMES = new Hashtable<Integer, String>();
	
// General settings
//	public static String SUBJECT_GUI_TITLE = "Simulation";
	public static String JABLUS_WINDOW_TITLE = "JABLUS";
	public static String JABLUS_VERSION = "1.0"; // + Constants.class.getPackage().getImplementationVersion();
	public static String JABLUS_CONFIG_NAME = "Config";
	public static String SEPARATOR = "_";
	
	// Files and folders 

//	public static String JABLUS_HOME_DIR     = "/home/crider/uni/phd/code/jablus-" + JABLUS_VERSION;
	public static String JABLUS_DATA_DIR     = "data";
	public static String JABLUS_EXPT_DIR     = "experiments";
	public static String JABLUS_GRAPHICS_DIR = "graphics";
	public static String JABLUS_AGENTS_DIR   = "uk" + File.separator + "co" + File.separator + "crider" + File.separator + "jablus" + File.separator + "agent";

	public static String GUI_ICON_FILE       = "jablus_logo.gif";

	// String for working dir key
	public static final String WORK_DIR_KEY = "working_dir";
	
	// Id for initial simulation parameters dataset
	public static final int PARAMETERS            = 8;
	
	// DataSet names - datasets are data which contains multiple seperate data components
	public static final int DATASET_SIMULATION    = 1;
	public static final int DATASET_AGENT         = 2;
	public static final int DATASET_AGENTS        = 3;
	
	// Agent properties rawHeader
	public static final int AGENT_NAME            = 4;
	public static final int AGENT_ID              = 5;
	public static final int AGENT_TYPE            = 6;
	public static final int AGENT_FILE            = 7;

	// Temporal Variables - values which depend directly on data from previous time steps
	public static final int TEMPORAL_TIME         = 10;
	
	// Output data names
	public static final int OUTPUT_NARRATIVE      = 20;

	static{
		NAMES.put(PARAMETERS         , "Parameters");
		
		NAMES.put(DATASET_SIMULATION , "Simulation");
		NAMES.put(DATASET_AGENT      , "Agent");
		NAMES.put(DATASET_AGENTS     , "Agents");

		NAMES.put(AGENT_NAME         , "Name");
		NAMES.put(AGENT_ID           , "Id");
		NAMES.put(AGENT_TYPE         , "Type");
		NAMES.put(AGENT_FILE         , "DataFile");
		
		NAMES.put(TEMPORAL_TIME      , "Time");
		
		NAMES.put(OUTPUT_NARRATIVE   , "Narrative");
	}


	
	/** Returns the name for a numerical constant */
	public static String getName(int id){
//if(id == 0) throw new ArrayIndexOutOfBoundsException("bad id");
//System.out.println("Constants: retriving name for " + id + ", returned " + NAMES.get(id));
		return NAMES.get(id);
	}
	
	/** Retrives the id for given name (expensive operation) 
	 * Returns the first id found, if not found returns 0 */
	public static int getIdFromName(String name){
		int id = 0;
		for(Map.Entry<Integer, String> e : NAMES.entrySet()){
			if(e.getValue().equals(name)) return e.getKey();
		}
		return id;
	}
	
	// Agent categories - TODO: this needs to me moved out of
	// here and into the right subclass of constants
	// For model.abmluc
	public static final int RANDOM_AGENT = 0;
	public static final int HUMAN_AGENT = 1;
	public static final int BDI_AGENT = 2;
	public static final int VICCI_AGENT = 10;
	// For model.dairy
	public static final int DAIRY_FARMER = 9;

	// Agent rawHeader
	public static final String[] AGENT_NAMES = new String[]{
		"Random",
		"Human",
		"BDI",
		"Optimal",
		"Random",
		"Learning",
		"Crop",
		"Livestock",
		"Forestry",
		"Dairy Farmer",
		"Vicci"
	};
	
	// Units used
	public static final String UNITS_HOUR            = "h";
	public static final String UNITS_DAY             = "d";
	public static final String UNITS_WEEK            = "wk";
	public static final String UNITS_MONTH           = "mth";
	public static final String UNITS_YEAR            = "yr";
	public static final String UNITS_CURRENCY        = "" + '\u00A3';
	
	
	public static final int AGENT_STATUS_NONE    = 0;
	public static final int AGENT_STATUS_DATA    = 1;
	public static final int AGENT_STATUS_ENGLISH = 2;
	public static final int AGENT_STATUS_OUTPUT  = AGENT_STATUS_NONE;

}
