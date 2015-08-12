package uk.co.crider.jablus.models.absluc.agent;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.EnvironmentInterface;
import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.models.dairy.gui.SubjectInterface;

import java.util.Map;
import java.util.Random;

/** Represents a human agent, providing a GUI for humans to input land use actions. */
public class HumanAgent extends Agent {
	
//	private Random rand;
//	private LandUseScheme landUse;

	private SubjectInterface gui;
	
	public HumanAgent(String name, int id, EnvironmentInterface env, String dataFile, Map<String, Object> args, Parameters params) {
	    super(name, id, env, dataFile, params);
//	    rand = new Random(1);//params.AGENT_RANDOM_SEED);
//	    landUse = new LandUseScheme(env.queryOccupiedLand(this), params);
	    
	}

	public void setGUI(SubjectInterface gui){
		this.gui = gui;
	}
	
	public boolean areActionsCompatible(Action a1, Action a2){
		return false;
	}
	
	/** @inheritDoc */
	public int getType() {
		return Constants.HUMAN_AGENT;
	}
	
	public boolean isHuman(){
		return true;
	}

//private boolean a = true;
	@Override
	public void notifyStartStep(){
		super.notifyStartStep();

		// Inform gui of start step
//		if(gui != null)
//			gui.notifyNewStep();
		
		//System.out.println("DairyAgent: Making actions...");
//Utils.awaitEnter();
		// Select random land use
//		for(int y = 0; y < params.ROWS; y++){
//			for(int x = 0; x < params.COLS; x++){
//				landUse.setUse(new LandCell(x, y), (int)(rand.nextDouble() * params.LAND_USES));
//			}
//		}
//		((DataTX)getItem(Constants.OUTPUT_NARRATIVE)).setText("Selecting random land use.");
		
		
/*		if(a){
			actions.put(Decision.BUY_COWS, 10);
			a = false;
		}
		
		double d = rand.nextDouble() * 20; // 1 in 20 chance of decision being made
		if(d < 0.25) actions.put(Decision.BUY_FEMALE_CALVES, 2);
		if(d > 0.25 && d < 0.50) actions.put(Decision.BUY_COWS, 1);
		if(d > 0.50 && d < 0.75) actions.put(Decision.SELL_FEMALE_CALVES, 1);
		if(d > 0.75 && d < 1.00) actions.put(Decision.SELL_COWS, 5);
		actions.put(Decision.APPLY_FERTILISER, 10);
		actions.put(Decision.CONFINE_HEIFERS,  50);
		actions.put(Decision.CONFINE_COWS,     50);
		actions.put(Decision.FLUSH_MANURE,     50);
		actions.put(Decision.AREA_PASTURE,     25);
		actions.put(Decision.AREA_SILAGE,      25);
		actions.put(Decision.AREA_WHEAT,       25);
		actions.put(Decision.AREA_MAIZE,       25);
*/
		if(params.LOGGING_ACTIONS)
			System.out.println(getLog());
// Called by interface
//		decisionsMade();
	}

	@Override
    public void notifyEndStep() {
		// Inform gui of end step
//		if(gui != null)
//			gui.notifyEndStep();
		// Inform environment we are done
		doneUpdate();
	}
    
//	@Override
//    public LandUseProfile getLandUse() {
//	    return landUse;
//    }
	
	public String getLog(){
		StringBuffer buf = new StringBuffer("Decisions:");
//		for(String k : actions.keySet()){
//			buf.append(" " + k + ":" + actions.get(k));
//		}
		return buf.toString();
	}
	
	
	// Implementing DataSet ---------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new HumanAgent(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object*/
	protected HumanAgent(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super("" + id, id, key, dataItems, hasFile, containsDataSet);
	}

	/** Constructor for cloned agents */
/*	private HumanAgent(EnvironmentInterface env, Random random, CompoundData dataItems, SubjectInterface gui){
		super(env, random, dataItems);
	    this.gui = gui;		
	}

	@Override
    public Object clone() {
	    return new HumanAgent(env, random, dataItems, gui);
    }
*/

}
