package uk.co.crider.jablus;

/*

	DONE:
	* Cropping plans grouped
	* Saving and loading of plans/management state
	* Saving simulation run, so it can be re-played
	* Create realistic market data
	* Loading of different scenarios
	* Number fields in visualisation
	* Create web-based instructions
	  - explain purpose of expt, and what decision data will be used for
	  - inc possibilities for decision/reasons (interface, txt file, written, dictated)
	  - explain why there are omissions (eg. spraying)
	* Include steers in evaluation totals
	---- Extra -----
	* Optimise input to make it less sluggish
	* Check underlying models working ok
	  - cow lactation curves should peak around 4-6 weeks, then tail off
	  - check crop growth/pasture growth
	  - check fertilisation and yields
	  - check poor diet results in low milk
	* Split the herd for alternative management
	* Include more detail in running costs, eg cow bedding
	* Check box for confined cows
	* Figure out how to store/write compound datasets reliably
	---- Ideas from simcity/simfarm --------
	* Buy interface should show price
	* Balance Sheet - arranged as expenditures, revinue and assets. Gives annual figures
	------- Fixes from first farmer session ---------
	* Planner selection mechanism (turn off when right-click menu clicked)
	* Field selection, try to click selecting when shift is pressed
	* Target Yield, doesn't allow typing of figure (keeps changing to 5000)
	* Weekly transactions, make resizable for smaller screens
	* Lactation model behaves more realistically. (peak should be around 100-120 days, 3 months) .. all papers say 40 days!
	------ Features from first session ---------
	* Concentrates (allow 0 to 20) ... already does
	* Target Yield (allow increase to 1500 (increased from 0 to 20000)
	* Slurry pit (start half full) .. important to farmers
	* Crops, allow rotation (implement as plan swapping)
	* Removed from purchase/store (poss add later)
	* Allows purchase of extra steading, barn, silage and slurry storage
	* Offered feed scaled by age/weight for heifers growing up (assume offered feeds are for lactating cows)
	* Scenarios updated any seed can be generated in startup or established mode
	
	TODO:
	* Check feeding requirements / energy (ensure lactations are right)
	* Look into issue of scenarios... possibly find some nice seeds?
	*-----
	* Feeding regime (seperate regime for different cow groups)
	  .. also allow year planner to change regime over year
	* Allow control over number lactations to keep cows for
	* Allow control over AI
	* Visualise number of cows grazing on field map
	* Better visualisation for cow herd
	* Allow decimal points in feeding etc
	* Deal with fresh weight of feeds and convert to dry weight as needed
	* Allow purchase of extra land space (land rent based on area) with import of new maps
	* Construct an initial BDI farming agent!
	* Allow multi-year crop plans for rotation
	* Allow buying/selling of buildings
	* Include straw as feed (add to feeding regime)
	* Allow harvesting of wheat/maize as a grain as well as silage
	* Allow farmer to control insemination attempts
	* Allow farmer to control max number of lactations
	* Add milking parlour with fixed capacity
	* Silage clamp doesn't really have capacity because bails can be used
	  - instead visualise amount needed for the following year
	* Simulate fibre requirements, diet should contain at least 45% fibre
	* Visualise crop-type in yield map
	* Allow irrigation of fields
	* Sort out saving/loading of parameters
	* Allow multiple actions in crop plan
	---- Ideas from simcity/simfarm --------
	* DecisionPopup at beginning explaining scenario
	* Display warnings when something is very wrong
	  - when manure overflows and is spread
	* Sale price of product depends on quality
	* Expandable Log - shows activities recently carried out
	* Farm Bearu, like an info manual: Gives info on crops, machinery, sprays/fertilisers, animals etc
	
	Save state of parameters to allow re-load of same experiments
	Use remote object refs to store history data temporarily and save memory
   	Think about adding support for ontologies, where they could be applied
   	Implement basic heuristic and utility max agents
   	Carry out simple experiments to get methodology right, before final experiments

*/


import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.data.TextData;
import uk.co.crider.jablus.data.store.ActionIO;
import uk.co.crider.jablus.data.store.DataSetIO;
import uk.co.crider.jablus.data.store.DataTable;
import uk.co.crider.jablus.data.store.History;
import uk.co.crider.jablus.data.store.TupleData;
import uk.co.crider.jablus.env.Environment;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.UserInterface;
import uk.co.crider.jablus.gui.sim.SimulationGUI;
import uk.co.crider.jablus.models.absluc.agent.HumanAgent;
import uk.co.crider.jablus.models.absluc.env.LandUseEnv;
import uk.co.crider.jablus.models.basic.env.BasicEnv;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.DairyFarm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

/** Top level object, 1-instance per simulation. Provides access to LandUseEnv and GUI objects.
 * Agents and user interfaces should not make direct reference to this object.
 * Instead use EnvironmentInterface for agents and SimulationInterface for user interfaces 
 * @see uk.co.crider.jablus.EnvironmentInterface
 * @see uk.co.crider.jablus.SimulationInterface */
public class Simulation extends CompoundData implements EnvironmentInterface, SimulationInterface{


	/** Unique class ID */
    private static final long serialVersionUID = -6888047359568229716L;
    
    /** Used for logging */
    public static final Logger log = Logger.getLogger("jablus");
    
	private int simId;
//	private String exptDir;
	private int expt;
	private File simFile;
	private Parameters params;
	private List<UserInterface> ifaces;
//	private DataSetIO io;
//    private History history;
    private ActionIO aio;

	// Component data
	private Environment env;        // Data and behaviour associated with the environment
    private CompoundData stats;      // Statistical data
    private DataSet agentDataSchema; // Schema for file storing agent info
    private DataTable agentsTable;   // DataTable used to store agents data
	private CompoundData agents; // Agent data
	
	private Hashtable<Integer, Boolean> agentsDone;
	private boolean agentsLoaded;
	private boolean dataLoaded;
	private boolean loadingSim;
	private int state;

	// State bits
	/** Indicates that the simulation is currently running */
	public static final int RUNNING     = 1 << 0;
	/** Indicates that the simulation is running in step-wise mode */
	public static final int STEPWISE    = 1 << 1;
	/** Indicates that the simulation is currently recording */
	public static final int RECORDING   = 1 << 2;
	/** Indicates that the simulation is accepting input from UserInterface objects */
	public static final int INTERACTIVE = 1 << 3;
	
	
	// Constructors -----------------------------------------------
	
//	public Simulation(int simId                              ){ this(simId, null,   null, null); }
//	public Simulation(int simId, File toOpen                 ){ this(simId, toOpen, null, null); }
//	public Simulation(int simId, File toOpen, Parameters params){ this(simId, toOpen, null, params); }
//	public Simulation(int simId, List<UserInterface> ifaces  ){ this(simId, null, ifaces, null); }
//	public Simulation(int simId, Parameters params             ){ this(simId, null,   null, params); }
	/** Called by Simulation Manager */
	Simulation(int simId, int expt, File simFile, Parameters params, List<UserInterface> ifaces, ActionIO aio){
		super(Constants.DATASET_SIMULATION, Constants.TEMPORAL_TIME, true);
		this.simId = simId;
		this.expt = expt;
		this.simFile = simFile;
		this.ifaces = ifaces;
		this.params = params;
		this.aio = aio;

		// Initialise fields
		agentsDone = new Hashtable<Integer, Boolean>();
		agentsLoaded = false;
		dataLoaded = false;
		loadingSim = aio != null && !aio.isCurrent();
		state = 0; // Start paused
//		exptDir = simFile.getAbsolutePath();
//		exptName = simFile.getName();
		
		// Initialise dataSet IO functionallity
//		io = new DataSetIO(new File(exptDir));
		
		// Load parameters if present
//		params.load(io);
		
		// Initialise agents schema
		agentDataSchema = new CompoundData(Constants.DATASET_AGENTS, true);
		agentDataSchema.addItem(new TextData(Constants.AGENT_NAME));
		agentDataSchema.addItem(new IntegerData(Constants.AGENT_ID));
		agentDataSchema.addItem(new IntegerData(Constants.AGENT_TYPE));
		agentDataSchema.addItem(new TextData(Constants.AGENT_FILE));
	    
	    // Initialise data
		// TODO: This needs to be fixed so that the code isn't dependent on specific models
//	    dataItems = new CompoundData(Constants.JABLUS_CSV_NAME);
		env = params instanceof uk.co.crider.jablus.models.dairy.Parameters ? new DairyFarm((uk.co.crider.jablus.models.dairy.Parameters)params) :
			  params instanceof uk.co.crider.jablus.models.basic.Parameters ? new LandUseEnv((uk.co.crider.jablus.models.basic.Parameters)params) :
			  new BasicEnv((uk.co.crider.jablus.models.basic.Parameters)params);
		addItem(env);
		aio.setTime((Time)env.getItem(Constants.TEMPORAL_TIME));
	    stats = new CompoundData();   addItem(stats);
	    agents = new CompoundData(Constants.DATASET_AGENTS);  addItem(agents);

	}
	
	
	// Initialisation ------------------------------------------------
	
	public void loadAgents(Map<String, Object> args){
//System.out.println("Simulation.loadAgents: entering");
		// Load necessery agents
	
		// Create a new config from stored config data
//		agentsTable = new DataTable(agentDataSchema, io);

		// If the data exists, then load existing agents from data
/*		if(io.isAvailable()){
			DataSet agentData = null;
			for(int i = Agent.OFFSET; (agentData = agentsTable.get(i)) != null; i++){
				agentsDone.put(((Data0D.Integer)agentData.getItem(Constants.AGENT_ID)).intValue(), false);
				SimulationManager.startAgent(
						((DataTX)agentData.getItem(Constants.AGENT_NAME)).getText(),
						((Data0D.Integer)agentData.getItem(Constants.AGENT_ID)).intValue(),
						((Data0D.Integer)agentData.getItem(Constants.AGENT_TYPE)).intValue(),
						exptDir + File.separator + ((DataTX)agentData.getItem(Constants.AGENT_FILE)).getText(),
						simId,
						args
				);
			} 
		}
		// Otherwise generage default agents defined in parameters file
		else{
//System.out.println("Generating agents");
*/			for(int id = Agent.OFFSET; id < params.AGENT_TYPES.length + Agent.OFFSET; id++){
				int type = params.AGENT_TYPES[id - Agent.OFFSET];
				String name = Constants.DATASET_AGENT + Constants.SEPARATOR  + id + Constants.SEPARATOR + Constants.AGENT_NAMES[type];
				agentsDone.put(id, false);
				SimulationManager.startAgent(name, id, type, simFile.getParent() + File.separator + name + ".csv", simId, args);
				// Add agent to agent properties table
				((DataTX)     agentDataSchema.getItem(Constants.AGENT_NAME)).setText (name);
				((IntegerData)agentDataSchema.getItem(Constants.AGENT_ID  )).setValue(id);
				((IntegerData)agentDataSchema.getItem(Constants.AGENT_TYPE)).setValue(type);
				((DataTX)     agentDataSchema.getItem(Constants.AGENT_FILE)).setText (name + ".csv");
//				agentsTable.put((DataSet)agentDataSchema.clone());
			}
//		}
		agentsLoaded = true;
		boolean d = areAllAgentsDone();
//System.out.println("Simulation.loadAgents: ending, agentsDone?" + d);
		if(d){
			loadData();
		}
	}
	
	/** Called once agents have loaded */
	private synchronized void loadData(){
		if(dataLoaded) return;
//System.out.println("Simulation.loadData()");

		// Create history data from dir for temporal data, loading data if present 
//		history = new History(this, io);

		if(ifaces != null && ifaces.size() > 0){ //Constants.GUI_SIMULATION){
//System.out.println("Simulation: Adding simulation reference to interfaces");
			for(final UserInterface iface : ifaces){
				// Initialise interfaces in event dispatch thread
				final Simulation sim = this;
				try{ SwingUtilities.invokeAndWait(new Runnable(){ public void run(){
					// Inform interface of simulation object
					iface.setSimulation(sim);
					// Add all simulation display objects
					iface.addDisplayItem(sim);
		        }});
				}catch(InterruptedException e){ e.printStackTrace();
	            }catch(InvocationTargetException e){ e.printStackTrace(); } 
	        }
			state = params.START_STATE;
//System.out.println("Simulation: state=" + state + ", recording?" + isRecording());
			// If we have a ifaces then interactive mode must be inabled
			addState(INTERACTIVE);
//			ifaces = new SimulationGUI(this);
//System.out.println("Simulation: Initialising ifaces with...\n            " + this);
		}
		else{
			state = params.START_STATE;
			// If no ifaces, then it makes no sense to start paused or use stepwise
			addState(RUNNING);
			remState(STEPWISE);
			// If no ifaces, then inform console of intention
			if(params.TIME_CYCLE == 0)
				System.out.print("Running simulation indefinitely");
			else
				System.out.print("Running simulation for " + params.TIME_CYCLE + " cycles");
		}
		toStart();
		showHistory();
		
		dataLoaded = true;
		start();
//System.out.println("Simulation: Data Loaded, state=" + state);
	}
	
	/** Starts the simulation in a new thread */
	private void start(){
//System.out.println("Simulation.start()");
		// Set agents done to false before start to prevent
		// deadlock issues
		for(Data agent : agents.getItems()){
//System.out.println("Simulation.initStep() Set done to false");
			agentsDone.put(agent.getId(), false);
		}
		
//		final Simulation sim = this;
		new Thread("JABLUS Simulation Loop"){
			public void run() {
//System.out.println("Simulation.start(): Started new loop");
				initStep();
//System.out.println(sim);
//sim.notify();
			}		
		}.start();
	}
	
	
	// Main Simulation Loop ---------------------------------------
	
	private void initStep(){
		// Await user action
//System.out.println("InitStep");
//System.out.println("Simulation: paused? " + isPaused() + ", state=" + state);
		if(isPaused()) return;
		// Notify console of new year
		if(env.getTime().getWeekOfYear() == 0){
			System.out.println();
//			System.out.print(year);
		}
		System.out.print(".");
//System.out.println("Simulation: Notifying agents of new step.\nAgents=" + agents);
		// Fire agents into action
		for(Data agent : agents.getItems()){
			// TODO: This results in sequential execution
			// poss better with some scheduling here
			((Agent)agent).notifyStartStep();
			
			// carry out saved actions
			if(aio != null && !aio.isCurrent()){
				loadingSim = true;
				List<Action> actions = aio.loadActions();
				if(actions == null) continue;
				for(Action a : actions){
					notifyAction((Agent)agent, a);
				}
				// Automatically force agent to say its done
				((DairyAgent)agent).done();				
			}
			else loadingSim = false;
		}
	}
	private void midStep(){
//System.out.println("Simulation.midStep()");
		for(Data agent : agents.getItems()){
//			System.out.println("Simulation: notifying agent " + agent.getName() + " of end step");
			// Set agent to not done
//System.out.println("Simulation.midStep() Set done to false");
			agentsDone.put(agent.getId(), false);
			// Inform agents of ending year
			((Agent)agent).notifyEndStep();
		}
	}
	private void finaliseStep(){
//System.out.println("FinaliseStep");
//System.out.println("Simulation: All agents done.");	
		// Calculate statistics
		for(Data agent : agents.getItems()){
			// Generate agent statistics
			genAgentStats((Agent)agent);
		}
		// Incrament year counter
		env.newStep();
		// Record state at end of step if enabled
		if(isRecording() && params.SAVE_CYCLE > 0 && env.getTime().getTime() % params.SAVE_CYCLE == 0){
			// do record
//			history.takeSnapshot();
		}
		// If stepwise, or at end of time-cycle then either close or pause
		if((state & STEPWISE) != 0
		|| params.TIME_CYCLE > 0 && env.getTime().getTime() % params.TIME_CYCLE == 0){
			if(ifaces == null || ifaces.size() == 0 || params.ONE_RUN){
				close();
			}
			else
//System.out.println("Simulation: paused, TIME_CYCLE= " + params.TIME_CYCLE + ", TIME=" + env.getTime() + ", STEPWISE=" + ((state & STEPWISE) != 0));
				remState(RUNNING);
		}
		// Update interfaces to reflect new state
		if(ifaces != null){
//System.out.println("Simulation: notifying interfaces of end step");
			for(UserInterface iface : ifaces)
				iface.notifyStep(((Data0D.Integer)getTime()).intValue() / (double)params.TIME_CYCLE);
		}
	}
	

	// Finailsation code  ----------------------------------------------- 
	
	/** Ends this simulation, clearing any resources assoicated with it */
	public void close(){
//System.out.println("Simulation: Close called on Simulation " + simId);
		// Inform interfaces of iminant close
		if(ifaces != null)
			for(UserInterface iface : ifaces)
				iface.close();
		SimulationManager.informClosing(simId);
		// Save data
/*		agentsTable.finalise();
		history.finalise();
		TupleData d = params.save(io);
		if(d != null) d.finalise();
		io.close();
*/		aio.close();
		// Stop the simulation
		SimulationManager.stopSimulation(simId);
	}
	

	// Accessor Methods ---------------------------------------------

	/** Returns the current time */
	public Data       getTime()      { return getItem(Constants.TEMPORAL_TIME);  }
	/** Returns the agents in the simulation */
	public Data       getAgents()    { return agents;                          }
	/** Returns the file name this experiment is saved as */
	public String     getFileName()  { return simFile.getName();               }
	/** Returns the id of this experiment */
	public int        getExpt()      { return expt;                            }
	/** Returns the parameters associated with this experiment */
	public Parameters getExperiment(){ return params;                          }
	/** Returns whether this is a new simulation */
	public boolean    isNew()        { return true; }//return io == null || !io.isAvailable(); }
	
	public File getSimFile(){ return simFile; }
		
	// Private helper methods --------------------------------------------
	
	/** Adds state bits to state variable */
	private void addState(int state){
		this.state = this.state | state;  
	}
	/** Removes state bits from state variable */
	private void remState(int state){
		this.state = this.state & (~state);
	}
	/** Displays the currently selected history */
	private void showHistory(){
/*		if(ifaces != null){
			for(UserInterface iface : ifaces)
				iface.setDisplayItem(history.getSnapshot());
		}
*/	}
	
	/** Updates agents statistics */
	private void genAgentStats(Agent agent){
		// Update statistics
//		((Data0D.Double)  agent.getItem(Constants.OUTPUT_REVENUE          )).setValue(agent.getProfits());
//		((Data1D)sim.getStats().getItem(Constants.GRAPH_PROFIT       )).addData(id.getLocalName(), lastProfit);
//		double optimallity = agent.getProfits()/env.getEconomics().getMaxProfit(agent.getLandUse().getLandUse());
//		((Data0D.Double)  agent.getItem(Constants.OUTPUT_OPTIMALLITY     )).setValue(optimallity);
//		((Data1D)sim.getStats().getItem(Constants.GRAPH_OPTIMALLITY  )).addData(id.getLocalName(), optimallity);
//		int cellsChanged = agent.getLandUse().cellsChanged();
//		((Data0D.Integer) agent.getItem(Constants.OUTPUT_CELLS_CHANGED   )).setValue(cellsChanged);
//		((Data1D)sim.getStats().getItem(Constants.GRAPH_CELLS_CHANGED)).addData(id.getLocalName(), cellsChanged);
//		double distribution = landUse.getDistribution();
//		((Data0D) id.getStats().getItem(Constants.OUTPUT_DISTRIBUTION            )).setValue(distribution);
//		((Data1D)sim.getStats().getItem(Constants.GRAPH_DISTRIBUTION )).addData(id.getLocalName(), distribution);
	}
	/** Returns true of all agents have completed their task */
	private synchronized boolean areAllAgentsDone(){
		for(boolean done : agentsDone.values())
			if(!done) return false;
		return true;
	}


	// Implementing EnvironmentInterface ----------------------------------

	/** @inheritDoc */
	public synchronized void notifyNewAgent(Agent agent){
		// If the agent already exists then don't go any further
		if(agents.getItems().contains(agent))
			return;
		// This agent is now done
//System.out.println("Simulation.notifyNewAgent() Set done to true");
		agentsDone.put(agent.getId(), true);
//System.out.println("Simulation: New agent: " + agent);
		// Add key to agent's dataset
		agent.addItem(getKey());
		// Add agent statistics to agent's dataset
		agent.addItem(new TextData(Constants.OUTPUT_NARRATIVE));
		// Add agent to simulation dataset
		agents.addItem(agent);
//System.out.println("Simulation.notifynewagent: adding agent " + agent + " to sim=" + this);
		addItem(agent);
		// Notify environment of new agent
		env.notifyAgent(agent);
//System.out.println("Resulting in sim=" + this);
		// Initialise gui for any human agents
		if(params.GUI_HUMAN){
			if(agent instanceof HumanAgent){
				ifaces.add(new uk.co.crider.jablus.models.absluc.gui.SubjectInterface((HumanAgent)agent,
						(uk.co.crider.jablus.models.basic.Parameters)params));
			}
			if(agent instanceof DairyAgent){
				ifaces.add(new uk.co.crider.jablus.models.dairy.gui.SubjectInterface((DairyAgent)agent,
						(uk.co.crider.jablus.models.dairy.Parameters)params));
			}
		}	
		// If all agents are loaded then load remaining data
		if(areAllAgentsDone() && agentsLoaded)
			loadData();
	}
	
	/** @inheritDoc */
	public synchronized void notifyActionSelectionDone(Agent agent){
//System.out.println("Simulation.notifyActionSelectionDone()");
		// An agent may only set land use once per round
		if(agentsDone.get(agent.getId())){
//System.out.println("Simulation.notifyActionSelectionDone() agent is DONE");
			return;
		}
//System.out.println("Simulation.notifyActionSelectionDone() agent not done");
//System.out.println("Agent isn't done");
		// Agent has had its turn
//System.out.println("Simulation.notifyActionSelectionDone() Set done to true");
		agentsDone.put(agent.getId(), true);
		// Carry out actions on environment
//		env.performActions(agent, actions);
		// Set desired land use
//		env.getLandUse().useLand(agent, actions);
		// If all agents have chosen land use then continue with next step
		if(areAllAgentsDone()){
			// Go to mid step
			midStep();
		}
	}
	
	public synchronized void notifyAction(Agent agent, Action action){
//System.out.println("Simulation: New Action from:" + agent + " = " + action);
		if(aio != null && !loadingSim) aio.saveAction(action);
		env.notifyAction(agent, action);
	}
	
	/** @inheritDoc */
	public synchronized void notifyDoneUpdate(Agent agent){
//System.out.println("Simulation.notifyDoneUpdate()");
		// An agent may only set land use once per round
		if(agentsDone.get(agent.getId()))
			return;
		// Agent has had its turn
//System.out.println("Simulation.notifyDoneUpdate() Set done to true");
		agentsDone.put(agent.getId(), true);
		// If all agents have chosen land use then continue with next step
		if(areAllAgentsDone()){
			// Finalise this step
			finaliseStep();
			// Do not go any further if this is a static simulation
			if(!params.TIME) return;
			// Start next step
//System.out.println("Starting new round...");
			start();
		}
	}
		
	public boolean isActionPossible(Action action){
		return env.isActionPossible(action);
	}

	
	// Implementing Data --------------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new Simulation(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object */
	private Simulation(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super(id, key, dataItems, hasFile, containsDataSet);
	}
	
		
	// Implementing SimulationInterface -------------------------
	
	/** @inheritDoc */
	public boolean isPaused(){
		return (state & RUNNING) == 0 ;
	}
	/** @inheritDoc */
	public boolean canPause(){
		return !isPaused();
	}
	/** @inheritDoc */
	public void pause(){
		if(canPause())
			remState(RUNNING);
	}
	
	/** @inheritDoc */
	public boolean canResume(){
		return canStepForward();
	}
	/** @inheritDoc */
	public void resume(){
		if(canResume()){
			remState(STEPWISE);
			addState(RUNNING); initStep();
		}
	}
		
	/** @inheritDoc */
	public boolean canToStart(){
		return canStepBack();
	}
	/** @inheritDoc */
	public void toStart(){
		if(canToStart()){
//			history.start();
			showHistory();
		}
	}
	
	/** @inheritDoc */
	public boolean canStepBack(){
//		return isPaused() && history.canBack();
		return false;
	}
	/** @inheritDoc */
	public void stepBack(){
		if(canStepBack()){
//			history.back();
			showHistory();
		}
	}
	
	/** @inheritDoc */
	public boolean atCurrentState(){
		return aio.isCurrent(); // history.usingCurrent();
	}
	
	/** @inheritDoc */
	public boolean canStepForward(){
		return isPaused();
	}
	/** @inheritDoc */
	public void stepForward(){
		if(canStepForward()){
			if(aio.isCurrent()){//history.usingCurrent()){
				addState(STEPWISE | RUNNING); initStep();
			}
			else{
//				history.forward();
				showHistory();
			}
		}
	}
	
	/** @inheritDoc */
	public boolean canToEnd(){
		return false; //isPaused() && !history.usingCurrent();
	}
	/** @inheritDoc */
	public void toEnd(){
		if(canToEnd()){
//			history.end();
			showHistory();
		}
	}

	/** @inheritDoc */
	public DataSet getSnapshot(){
		return null;//history.getSnapshot();
	}
	/** @inheritDoc */
	public boolean canTakeSnapshot(){
//		return isPaused() && !history.snapshotTaken() && history.usingCurrent();
		return false;
	}
	/** @inheritDoc */
	public void takeSnapshot(){
		if(canTakeSnapshot()){
//			history.takeSnapshot();
		}
	}
	
	/** @inheritDoc */
	public boolean isRecording(){
		return (state & RECORDING) != 0;
	}
	/** @inheritDoc */
	public boolean canRecord(){
//		return isPaused() && history.usingCurrent();
		return false;
	}
	/** @inheritDoc */
	public void setRecording(boolean record){
		if(isPaused()){
			if(record){
				if(canRecord()){
					addState(RECORDING);
					// Take snapshot of last-year
					takeSnapshot();
				}
			}
			else
				remState(RECORDING);
		}
	}	

	/** Save the simulation under new file */
	public void saveAs(File simFile){
		aio.copyTo(simFile);
	}
	
	// Static methods -------------------------------------------
	
	/** Convenience method for starting the program */
	public static void main(String[] args){
		SimulationManager.main(args);
	}

}
