package uk.co.crider.jablus;

import uk.co.crider.jablus.data.store.DataSetIO;
import uk.co.crider.jablus.data.store.TupleData;
import uk.co.crider.jablus.utils.Utils;

import java.io.File;

/** Used to represent specific parameter settings for an experiment */
public class Parameters {
		
	// All experiment names
	public String[] EXPERIMENTS;
	
	// GUIs to show
	public boolean GUI_SIMULATION; // Show the simulation gui?
	public boolean GUI_HUMAN;      // Show the human agent gui?
	public boolean GUI_HUMAN_CLOSABLE; // Whether the human agent gui can be closed

	// General simulation properties 
	public int START_STATE;        // 0 for paused. Bitwise addition of RUNNING, STEPWISE and RECORDING possible
	public boolean TIME;           // Whether it is a or dynamic simulation
	public int TIME_STEP;          // Time step used 
	public int TIME_CYCLE;         // Length of a simulation run (0 for infinite)
	public int START_YEAR;         // Year to start from
	public int START_MONTH;        // Month to start from
	public int START_DAY;          // Day to start from
	public boolean ONE_RUN;        // Whether to close after 1 run
	public int SAVE_CYCLE;         // When to save (in record mode) (0 for never)
	
	// Logging
	public boolean LOGGING_TIME;
	public boolean LOGGING_ACTIONS;
	
	// Agent properties
	public int[] AGENT_TYPES = new int[0];  
	public int AGENT_RANDOM_SEED;
	
	/** Constructor */
	public Parameters(){ EXPERIMENTS = new String[0]; }
	
	/** Load parameters from disk */
	public TupleData load(DataSetIO io){
		if(!io.isAvailable()) return null;
		TupleData paramData = new TupleData(Constants.PARAMETERS, io);
//System.out.println("Parameters.load: TIME_CYCLE=" + TIME_CYCLE);
		GUI_SIMULATION          = Utils.parseBoolean(paramData.get("GUI_SIMULATION"));
		GUI_HUMAN               = Utils.parseBoolean(paramData.get("GUI_HUMAN"));
		GUI_HUMAN_CLOSABLE      = Utils.parseBoolean(paramData.get("GUI_HUMAN_CLOSABLE"));
		START_STATE             = Utils.parseInt    (paramData.get("START_STATE"));
		TIME                    = Utils.parseBoolean(paramData.get("TIME"));
		TIME_CYCLE              = Utils.parseInt    (paramData.get("TIME_CYCLE"));
		ONE_RUN                 = Utils.parseBoolean(paramData.get("ONE_RUN"));
		SAVE_CYCLE              = Utils.parseInt    (paramData.get("SAVE_CYCLE"));
		LOGGING_TIME            = Utils.parseBoolean(paramData.get("LOGGING_TIME"));
		LOGGING_ACTIONS       = Utils.parseBoolean(paramData.get("LOGGING_ACTIONS"));
		// Agent properties
		AGENT_TYPES             = Utils.intArray    (paramData.get("AGENT_TYPES"));
		AGENT_RANDOM_SEED       = Utils.parseInt    (paramData.get("AGENT_RANDOM_SEED"));
		return paramData;
	}

	
	public TupleData save(DataSetIO io){
		GUI_SIMULATION = true; // Sould always be shown for subsiquant runs to allow review of data
		GUI_HUMAN = false; // Should always be hidden for subsiquant runs
		START_STATE = 0; // Reactivated simulations shouldn't be running or recording
		if(!io.isAvailable() || new File(io.baseDir + File.separator + "Parameters.csv").exists()) return null;
		TupleData paramData = new TupleData(Constants.PARAMETERS, io);
		paramData.put("GUI_SIMULATION", ""+GUI_SIMULATION);
		paramData.put("GUI_HUMAN", ""+GUI_HUMAN);
		paramData.put("GUI_HUMAN_CLOSABLE", ""+GUI_HUMAN_CLOSABLE);
		paramData.put("START_STATE", ""+START_STATE);
		paramData.put("TIME", ""+TIME);
		paramData.put("TIME_CYCLE", ""+TIME_CYCLE);
		paramData.put("ONE_RUN", ""+ONE_RUN);
		paramData.put("SAVE_CYCLE", ""+SAVE_CYCLE);
		paramData.put("LOGGING_TIME", ""+LOGGING_TIME);
		paramData.put("LOGGING_ACTIONS", ""+LOGGING_ACTIONS);
		// Agent properties
		paramData.put("AGENT_TYPES", Utils.arrayString(AGENT_TYPES));
		paramData.put("AGENT_RANDOM_SEED", ""+AGENT_RANDOM_SEED);
		return paramData;
	}
	
	
	
}
