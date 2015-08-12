package uk.co.crider.jablus;

import uk.co.crider.jablus.data.store.ActionIO;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.UserInterface;
import uk.co.crider.jablus.gui.sim.SimulationGUI;
import uk.co.crider.jablus.models.absluc.agent.HumanAgent;
import uk.co.crider.jablus.models.absluc.agent.RandomAgent;
import uk.co.crider.jablus.models.absluc.agent.bdi.BDIAgent;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import jadex.runtime.IExternalAccess;

import java.awt.Insets;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/** Manages simulation objects, allowing the opening and closing of simulations */
public class SimulationManager {
		
	// Simulation housekeeping ------------------------------------------ 
	
	/** Reference to each simulation */
	private static Hashtable<Integer, Simulation> simulations = new Hashtable<Integer, Simulation>();
	private static Hashtable<Integer, CloseListener> closeListeners = new Hashtable<Integer, CloseListener>();
	private static int simCount = 0;
	private static boolean jadexStarted = false;
	private static IExternalAccess starterRef;
	
	/** Starts a new simulation, returning it's unique identifier */
	public static int startSimulation(int expt){ return startSimulation(expt, null, null, null); }
	public static int startSimulation(File simFile){ return startSimulation(0, simFile, null, null); }
	public static int startSimulation(int expt, File simFile){ return startSimulation(expt, simFile, null, null); }
	public static synchronized int startSimulation(int expt, File simFile, CloseListener closeListener, Map<String, Object> args){
		// Dont start if already started
//System.out.println("SimulationManager: starting sim, expt=" + expt + ", file=" + simFile);
		if(simFile != null && simOpen(simFile.getName())) return -1;
//System.out.println("SimulationManager: not already open");
			
//System.out.println("SimulationManager: egg 0");
//System.out.println("Started simulation" + simCount);
		
		// Generate blank file if new
		if(simFile == null) simFile = nextFreeUntitled();

		// Try opening simulation from file
		ActionIO aio = ActionIO.loadSaved(simFile);
		if(aio != null) expt = aio.getExpt();
		// Otherwise, try creating a new sim using supplied file and experiment
		if(aio == null)
			aio = new ActionIO(simFile, expt);
			
		// Initialise parameters
		Parameters params = Experiments.create(expt);
		
		// Inform users that program is starting
		Popup startUp = null;
		if(params.GUI_HUMAN || params.GUI_SIMULATION){
			if(params instanceof uk.co.crider.jablus.models.dairy.Parameters){
				startUp = new Popup(((uk.co.crider.jablus.models.dairy.Parameters)params).EXPT_TITLE, false);
				JTextArea area = new JTextArea(((uk.co.crider.jablus.models.dairy.Parameters)params).EXPT_INFO);
				area.setEditable(false);
//				area.setBackground(new JLabel().getBackground());
				area.setOpaque(false);
//				area.setEnabled(false);
				area.setMargin(new Insets(5, 5, 5, 5));
				startUp.displayPopup(null, area);
			}else{
				startUp = new Popup("", false);
				startUp.displayPopup(null, new JLabel("Starting up, please wait..."));
			}
		}
		
		// Initialise display parameters
		final DisplayParams displayParams = new DisplayParams(params);
		// Initialise gui
		final List<UserInterface> ifaces = new LinkedList<UserInterface>();
//System.out.println("SimulationManager: egg 1");
		if(params.GUI_SIMULATION){
			final String efn = simFile.getName();
			try{ SwingUtilities.invokeAndWait(new Runnable(){ public void run(){
	            ifaces.add(new SimulationGUI(efn, displayParams));
	        }});
			}catch(InterruptedException e){ e.printStackTrace();
            }catch(InvocationTargetException e){ e.printStackTrace(); } 
		}

//System.out.println("SimulationManager: egg 2");
		// Add close listener if present
		if(closeListener != null)
			closeListeners.put(simCount, closeListener);
		// Initialise Simulation
		Simulation sim = new Simulation(simCount, expt, simFile, params, ifaces, aio);
//System.out.println("SimulationManager: simulation created.");
		simulations.put(simCount, sim);
		

		// Close first simulation if open
		if(simCount == 1 && getSimulation(0) != null && getSimulation(0).isNew() && getSimulation(0).getExpt() == 0){
//System.out.println("SimulationManager: Closing simulation 0 because its untitled");
			getSimulation(0).close();
		}

//System.out.println("SimulationManager: loading agents");
		// Loads agents and starts the simulation
		sim.loadAgents(args);

		if(startUp != null && !(startUp.hasBeenClicked())) startUp.dispose();
		return simCount++;
	}
	
	/** Returns a simulation instance, or null if it doesn't exist */
	public static Simulation getSimulation(int i){ //return getSimulation(i, null); }
//	/** Returns a simulation instance, and sets its messaging ID */
//	public static Simulation getSimulation(int i, AgentIdentifier id){
//System.out.println("Setting agent identifier: " + id);
//System.out.println("Simulation: agent: " + id + " requested sim instance " + i + ", recieved:" + simulations.get(i).getAgentIdentifier());
		Simulation sim = simulations.get(i);
//		if(id != null){
//			sim.setAgentIdentifier(id);
//		}
//System.out.println("Agent requested instance " + i);
		return sim;
	}
	
	/** Inform simulation manager that simulation is in the process of closing */
	public static void informClosing(int i){
		// Inform close listener if present
		if(closeListeners.get(i) != null)
			closeListeners.get(i).simluationClosed(simulations.get(i));
	}
	
	/** Stops the given simulation, clearing all resources associated with it*/
	public static void stopSimulation(int i){
		// Remove reference to simulation object, to clear resources
		simulations.remove(i);
System.out.println("\nSimulationManager: Closed simulation " + i);
		// Shutdown platform if done
		if(simulations.size() == 0)
			System.exit(1);
	}
		
	/** Sets reference to allow calls to the starter agent */
	public static void setStarterRef(IExternalAccess starterRef){
		SimulationManager.starterRef = starterRef;
		jadexStarted = true;
	}
	
	/** Starts as new decision making agent */
	public static void startAgent(String name, int id, int type, String dataFile, int simId){ startAgent(name, id, type, dataFile, simId, null); }
	public static void startAgent(String name, int id, int type, String dataFile, int simId, Map<String, Object> args){
//System.out.println("SimulationManager: starting agent " + Constants.AGENT_NAMES[type]);
		// TODO: Check agent type requires BDI start mechanism
		switch(type){
		case Constants.RANDOM_AGENT:
			new RandomAgent(name, id, getSimulation(simId), dataFile, args, getSimulation(simId).getExperiment());
			break;
		case Constants.HUMAN_AGENT:
			new HumanAgent(name, id, getSimulation(simId), dataFile, args, getSimulation(simId).getExperiment());
			break;
		case Constants.BDI_AGENT:
			// Start up the jadex platform if is not running and required
			if(!jadexStarted){
System.out.println("SimulationManager: Starting JADEX Platform...");
				jadex.adapter.standalone.Platform.main(new String[]{
//						"-transport", "jadex.adapter.standalone.transport.nsmt.NSMTransport:" + port,
//						"-platformname", "platform-" + simId,
						"-nogui",
						"Starter:jablus" + File.separator + "agent" + File.separator + "starter" + File.separator + "Starter.agent.xml"});
				// Wait for platform to initialise
				while(!jadexStarted)
					Thread.yield();
			}
			if(starterRef != null){
				starterRef.getBeliefbase().getBelief("agent_name").setFact(name);
				starterRef.getBeliefbase().getBelief("agent_id").setFact(id);
				starterRef.getBeliefbase().getBelief("agent_schema_file").setFact(
						BDIAgent.AGENT_FILES.get(Constants.AGENT_NAMES[Constants.BDI_AGENT]));
				starterRef.getBeliefbase().getBelief("agent_data_file").setFact(dataFile);
				starterRef.getBeliefbase().getBelief("sim_id").setFact(simId);
				starterRef.dispatchInternalEvent(
						starterRef.createInternalEvent("start_simulation_event")
				);
			}		
			break;
		case Constants.DAIRY_FARMER:
			new DairyAgent(name, id, getSimulation(simId), dataFile, args, getSimulation(simId).getExperiment());
			break;
		default:
			System.out.println("SimulationManager: Unable to start agent, unrecognised type " + type);
		}
	}

	// Simulation file management --------------------------------------
	
	/** Returns the next available simulation file */
	public static File nextFreeUntitled(){
		String workDir = Preferences.userNodeForPackage(Simulation.class).get(Constants.WORK_DIR_KEY, ".");
		int i = 0;
		String cand;
		File candFile;
		// Find next available 'untitled' slot
		do{
			i++;
			String zeros = "00";
			if(i >= 10) zeros = "0";
			if(i >= 100) zeros = "";
			cand = "untitled_" + zeros + i + ".sim.csv";
			candFile = new File(workDir + File.separator + cand);
		}while(candFile.exists() || simOpen(cand));
		return candFile;
	}
	
	/** Verifies whether a simulation with the given name is open */
	public static boolean simOpen(String name){
		for(Simulation sim : simulations.values()){
			if(sim.getFileName().equals(name))
				return true;
		}
		return false;
	}	

	/** Start a simulation from the command line */
	public static void main(String[] args){
		// Initialise constants
		uk.co.crider.jablus.models.basic.Constants.init();
		uk.co.crider.jablus.models.absluc.Constants.init();
		uk.co.crider.jablus.models.dairy.Constants.init();
		
		String exptFile = args.length > 0 ? args[0] : null;
		int expt = 0;
		if(args.length > 1){
			try{
				expt = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		if(exptFile != null && exptFile.equals("")) exptFile = null;
		startSimulation(expt, exptFile == null ? null : new File(exptFile), null, null);
	}
}
