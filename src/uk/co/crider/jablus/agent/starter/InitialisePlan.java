package uk.co.crider.jablus.agent.starter;

import uk.co.crider.jablus.SimulationManager;
import jadex.runtime.Plan;

/** Initialises this starter agent, by supplying the SimulationManager with a callback object */
public class InitialisePlan extends Plan {
	
	/** Unique class ID */
    private static final long serialVersionUID = 576288899040405238L;

    //private AgentIdentifier id;
    //private int simId;
    //public String exptFile;
	
	/** Create a new plan. */
	public InitialisePlan() {
//		this.id = (AgentIdentifier)getBeliefbase().getBelief("id").getFact();
//		this.simId = ((Integer)getBeliefbase().getBelief("sim_id").getFact()).intValue();
//		this.exptFile = (String)getBeliefbase().getBelief("expt_file").getFact();
	}
	
	/** Plan content */
	public void body() {
//System.out.println("InitialisePlan: " + id);

		SimulationManager.setStarterRef(this.getExternalAccess());

/*final int simId = Simulation.startSimulation(exptFile);
Hashtable<String, Object> args = new Hashtable<String, Object>();
args.put("starter_id", id);
args.put("sim_id", simId);
StartAgentInfo info = new StartAgentInfo("jablus/agent/sim/Simulation.agent.xml", "Simulation_" + simId, 0, args);
IGoal start = getExternalAccess().getGoalbase().createGoal("start_agents");
start.getParameterSet("agentinfos").addValue(info);
getExternalAccess().getGoalbase().dispatchTopLevelGoal(start);
*/

//Create internal event to await simulation start request
//final IInternalEvent event = createInternalEvent("await_start_event");
//dispatchInternalEvent(event);

/*			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// Start simulation
					final int simId = SimulationManager.startSimulation(exptFile);
					Hashtable<String, Object> args = new Hashtable<String, Object>();
					args.put("starter_id", id);
					args.put("sim_id", simId);
					StartAgentInfo info = new StartAgentInfo("jablus/agent/sim/Simulation.agent.xml", "Simulation_" + simId, 0, args);
					IGoal start = getExternalAccess().getGoalbase().createGoal("start_agents");
					start.getParameterSet("agentinfos").addValue(info);
					getExternalAccess().getGoalbase().dispatchTopLevelGoal(start);
				}
			});
*/			

	}

}
