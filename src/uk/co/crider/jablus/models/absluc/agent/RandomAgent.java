package uk.co.crider.jablus.models.absluc.agent;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.EnvironmentInterface;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.agent.LandUseScheme;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;

import java.util.Map;
import java.util.Random;

/** Represents a human agent, providing a GUI for humans to input land use actions. */
public class RandomAgent extends Agent {
	
	private Random rand;
	private LandUseScheme landUse;

	
	public RandomAgent(String name, int id, EnvironmentInterface env, String dataFile, Map<String, Object> args, uk.co.crider.jablus.Parameters params) {
	    super(name, id, env, dataFile, params);
	    rand = new Random(params.AGENT_RANDOM_SEED);
//	    landUse = new LandUseScheme(env.queryOccupiedLand(this), params);
    }

	/** @inheritDoc */
	public int getType(){
		return Constants.RANDOM_AGENT;
	}

	/** @inheritDoc */
	public boolean areActionsCompatible(Action a1, Action a2){
		return false;
	}

	@Override
	public void notifyStartStep(){
		// Select random land use
		for(int y = 0; y < ((Parameters)params).ROWS; y++){
			for(int x = 0; x < ((Parameters)params).COLS; x++){
				landUse.setUse(new LandCell(x, y), (int)(rand.nextDouble() * ((Parameters)params).LAND_USES));
			}
		}
		((DataTX)getItem(Constants.OUTPUT_NARRATIVE)).setText("Selecting random land use.");
//		makeDecision(landUse);
//		makeDecision(new DecisionProfile());
		decisionsMade();
	}

	@Override
    public void notifyEndStep() {
		// Inform environment we are done
		doneUpdate();
	}
    
	@Override
//    public LandUseProfile getLandUse() {
//	    return landUse;
//    }
	
	
	
	// Implementing DataSet ---------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new RandomAgent(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object*/
	protected RandomAgent(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
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
