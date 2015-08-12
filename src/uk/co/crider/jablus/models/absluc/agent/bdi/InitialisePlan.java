package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.EnvironmentInterface;
import uk.co.crider.jablus.SimulationManager;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;
import jadex.runtime.Plan;


/** Plan to initialise a bdi agent */
public class InitialisePlan extends Plan {
	
	/** Unique class ID */
    private static final long serialVersionUID = -178091939809041442L;

    private String name;
    private int id;
    private String dataFile;
    private int simId;
	
	public InitialisePlan(){
		this.name     = (String) getBeliefbase().getBelief("name"     ).getFact();
		this.id       =((Integer)getBeliefbase().getBelief("id"       ).getFact()).intValue();
		this.dataFile = (String) getBeliefbase().getBelief("data_file").getFact();
		this.simId    =((Integer)getBeliefbase().getBelief("sim_id"   ).getFact()).intValue();
	}
	
	/** Plan code */
	public void body(){
//System.out.println("jablus.agent.InitialisePlan");
		// Retrive environment we are associated with
		EnvironmentInterface env = SimulationManager.getSimulation(simId);
		// Initialise 'grounding' agent used to carry out all primative actions
		// Has side effect of informing environment of new agent
		BDIAgent grounding = new BDIAgent(name, id, env, dataFile, getExternalAccess(), SimulationManager.getSimulation(simId).getExperiment());
		// Get reference to land use
//		LandUseProfile landUse = grounding.getLandUse();
		
		// Save new beliefs
		getBeliefbase().getBelief("grounding").setFact(grounding);
//		getBeliefbase().getBelief("land_use" ).setFact(landUse);

		
		// Apply initial land use on environment
//		env.notifyLandUse(id, landUse);

		// Get initial owned land cells
//		land.addAll(env.queryOccupiedLand(id));

		// Inform GUI of data we will provide
//		if(Constants.GUI_SIMULATION && Constants.TIME){
//			gui.addFundsSeries(id);
//			gui.addProfitsSeries(id);
//		}
		
		// Tell environment this agent has been initialised
	    //notifyDone("agent_initialised_event");
	}
	
}
