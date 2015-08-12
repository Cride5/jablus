package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.EnvironmentInterface;
import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;
import jadex.runtime.IExternalAccess;
import jadex.runtime.IGoal;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

/** Subclass of Agent used to allow the use of a JADEX BDI agent. */
public class BDIAgent extends Agent {

	private IExternalAccess agentRef;
	private LandUseScheme landUse;
	
	public static final Map<String, String> AGENT_FILES = new Hashtable<String, String>();
	static{
		AGENT_FILES.put(Constants.AGENT_NAMES[Constants.BDI_AGENT], Constants.JABLUS_AGENTS_DIR + File.separator + "bdi" + File.separator + "Agent.agent.xml");
	}
	
	public BDIAgent(String name, int id, EnvironmentInterface env, String dataFile, IExternalAccess agentRef, Parameters params){
		super(name, id, env, dataFile, params);
		this.agentRef = agentRef;
//	    landUse = new LandUseScheme(env.queryOccupiedLand(this), params, getRandom());
	}
	
	/** @inheritDoc */
	public int getType(){
		return Constants.BDI_AGENT;
	}
	
	/** @inheritDoc */
	public boolean areActionsCompatible(Action a1, Action a2){
		return false;
	}

	/** @inheritDoc */
	public void notifyStartStep(){
//System.out.println("BDIAgent: Notify start step");
		// Start decision making
	    IGoal goal1 = agentRef.createGoal("select_land_use_goal");    
	    agentRef.dispatchTopLevelGoal(goal1);
	}
	
	/** @inheritDoc */
    public void notifyEndStep() {
//		System.out.println("BDIAgent: Notify end step");
    	// Starts belief revision
		agentRef.dispatchInternalEvent(
				agentRef.createInternalEvent("end_step_event")
		);	
    }

//	@Override
//    public LandUseProfile getLandUse() {
//	    return landUse;
//    }
	
    void bdiDoneUpdate(){
    	super.doneUpdate();
    }
    void bdiDecisionsMade(){
    	super.decisionsMade();
    }
   
	// Implementing DataSet ---------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new BDIAgent(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object*/
	protected BDIAgent(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super(""+id, id, key, dataItems, hasFile, containsDataSet);
	}

	/*// Constructor for cloned agents 
	private BDIAgent(EnvironmentInterface env, Random random, CompoundData dataItems){
		super(env, random, dataItems);
		
	}
	// Clone this object 
	public Object clone(){
		return new BDIAgent(env, random, dataItems);
	}
*/

}