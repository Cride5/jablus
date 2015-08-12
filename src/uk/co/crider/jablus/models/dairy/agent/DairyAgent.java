package uk.co.crider.jablus.models.dairy.agent;

import uk.co.crider.jablus.EnvironmentInterface;
import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.env.Livestock;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/** Represents a human agent, providing a GUI for humans to input land use actions. */
public class DairyAgent extends Agent {
	
//	private Random rand;
//	private LandUseScheme landUse;
	
	public static final int OFFSET           = 1800;
	
	// Action ids
	public static final int ACTION_BUY           = OFFSET +  0;
	public static final int ACTION_SELL          = OFFSET +  1;
	public static final int ACTION_REVERT        = OFFSET +  2;
	public static final int ACTION_MOVE_FORWARD  = OFFSET +  3;
	public static final int ACTION_MOVE_BACK     = OFFSET +  4;
	public static final int ACTION_REMOVE        = OFFSET +  5;
	public static final int ACTION_SEW_GRASS     = OFFSET +  6;
	public static final int ACTION_SEW_MAIZE     = OFFSET +  7;
	public static final int ACTION_SEW_WHEAT     = OFFSET +  8;
	public static final int ACTION_GRAZING_ON    = OFFSET +  9;
	public static final int ACTION_GRAZING_OFF   = OFFSET + 10;
	public static final int ACTION_SPREAD_SLURRY = OFFSET + 11;
//	public static final int ACTION_SPREAD_SLURRY = OFFSET + 12;
	public static final int ACTION_FERTILISE     = OFFSET + 13;
	public static final int ACTION_HARVEST       = OFFSET + 14;
	public static final int ACTION_PLOUGH        = OFFSET + 15;
	// Livestock actions
	public static final int ACTION_SET_GRAZING   = OFFSET + 16;
	public static final int ACTION_SET_CONFINED  = OFFSET + 17;
	public static final int ACTION_FEED_CONFINED = OFFSET + 18;
	public static final int ACTION_FEED_GRAZING  = OFFSET + 19;
	public static final int ACTION_FEED_TYIELD   = OFFSET + 20;

	// Action types
	public static final int MARKET_ACTION        = OFFSET + 50;
	public static final int FIELD_ACTION         = OFFSET + 51;
	public static final int FIELD_SEW            = OFFSET + 52;
	public static final int FIELD_GRASS          = OFFSET + 53;
	public static final int FIELD_REMOVAL        = OFFSET + 54;
	public static final int FIELD_PASTURE        = OFFSET + 55;
	public static final int FIELD_FERTILISE      = OFFSET + 56;
	public static final int LIVESTOCK_ACTION     = OFFSET + 57;
	public static final int MANURE_ACTION        = OFFSET + 58;
	public static final int FEED_BUDGET_ACTION   = OFFSET + 59;
	
	// Action Parameters
	public static final int ACTION_PARAM_QTY     = OFFSET + 90;
	public static final int ACTION_PARAM_FID     = OFFSET + 91;
	public static final int ACTION_PARAM_WEIGHT  = OFFSET + 92;
	public static final int ACTION_PARAM_ANIMAL_TYPE = OFFSET + 93;
	public static final int ACTION_PARAM_PRODUCT = OFFSET + 94;
	public static final int ACTION_PARAM_PERCENT = OFFSET + 95;
	public static final int ACTION_PARAM_VOLUME  = OFFSET + 96;
	public static final int ACTION_PARAM_CAPACITY = OFFSET + 97;
	
	// Decision rationale
	private List<DecisionNarrative> narratives;
	
	public DairyAgent(String name, int id, EnvironmentInterface env, String dataFile, Map<String, Object> args, Parameters params) {
	    super(name, id, env, dataFile, params);
//	    rand = new Random(1);//params.AGENT_RANDOM_SEED);
//	    landUse = new LandUseScheme(env.queryOccupiedLand(this), params);
	    narratives = new LinkedList<DecisionNarrative>();
	    
	    // Schema for all action objects supported by this agent
	    Collection<Action> a = new LinkedList<Action>();
	    a.add(new Action(ACTION_REVERT,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
		a.add(new Action(ACTION_BUY, new Data[]{
		    	new IntegerData(ACTION_PARAM_PRODUCT),
	    		new IntegerData(ACTION_PARAM_QTY, 0, 100)}));
		a.add(new Action(ACTION_SELL, new Data[]{
		    	new IntegerData(ACTION_PARAM_PRODUCT),
	    		new IntegerData(ACTION_PARAM_QTY, 0, 100)}));
	    a.add(new Action(ACTION_SEW_GRASS,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_SEW_WHEAT,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_SEW_MAIZE,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_FERTILISE, new Data[]{
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE),
	    		new IntegerData(ACTION_PARAM_WEIGHT, Constants.UNITS_APPLICATION1, 0, 1000)}));
	    a.add(new Action(ACTION_SPREAD_SLURRY, new Data[]{
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE),
	    		new IntegerData(ACTION_PARAM_VOLUME, Constants.UNITS_APPLICATION2, 0, 100)}));
/*	    a.add(new Action(Constants.ACTION_SPREAD_SLURRY, new Data[]{
	    		new IntegerData(Constants.ACTION_PARAM_FID, 0, Integer.MAX_VALUE),
	    		new IntegerData(Constants.ACTION_PARAM_WEIGHT, 0, 100)}));
*/
	    a.add(new Action(ACTION_PLOUGH,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_HARVEST,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_GRAZING_ON,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
	    a.add(new Action(ACTION_GRAZING_OFF,
	    		new IntegerData(ACTION_PARAM_FID, 0, Integer.MAX_VALUE)));
//	    		new IntegerData(Constants.ACTION_PARAM_QTY, 1, 1000)}));
	    a.add(new Action(ACTION_SET_GRAZING, 
	    		new IntegerData(ACTION_PARAM_ANIMAL_TYPE, 0, Livestock.HERD_GROUPS.length - 1)));
	    a.add(new Action(ACTION_SET_CONFINED,
	    		new IntegerData(ACTION_PARAM_ANIMAL_TYPE, 0, Livestock.HERD_GROUPS.length - 1)));

	    // Add all actions to map
	    for(Action action : a) allActions.put(action.id, action);
	    
	    // Place actions into type groups
	    Hashtable<Integer, int[]> ta = new Hashtable<Integer, int[]>();
	    ta.put(ACTION_BUY,                          new int[]{MARKET_ACTION});
	    ta.put(ACTION_SELL,                         new int[]{MARKET_ACTION});
//	    ta.put(ACTION_REVERT,             new int[]{FIELD_ACTION});
	    ta.put(ACTION_SEW_GRASS,          new int[]{FIELD_ACTION, FIELD_SEW});
	    ta.put(ACTION_SEW_WHEAT,          new int[]{FIELD_ACTION, FIELD_SEW});
	    ta.put(ACTION_SEW_MAIZE,          new int[]{FIELD_ACTION, FIELD_SEW});
	    ta.put(ACTION_SPREAD_SLURRY,      new int[]{FIELD_ACTION, FIELD_GRASS, FIELD_FERTILISE});
//	    ta.put(ACTION_SPREAD_SLURRY,      new int[]{FIELD_ACTION, FIELD_GRASS, FIELD_FERTILISE});
	    ta.put(ACTION_FERTILISE,          new int[]{FIELD_ACTION, FIELD_GRASS, FIELD_FERTILISE});
	    ta.put(ACTION_PLOUGH,             new int[]{FIELD_ACTION, FIELD_GRASS, FIELD_REMOVAL});
	    ta.put(ACTION_HARVEST,            new int[]{FIELD_ACTION, FIELD_GRASS, FIELD_REMOVAL});
	    ta.put(ACTION_GRAZING_ON,         new int[]{FIELD_ACTION, FIELD_GRASS});
	    ta.put(ACTION_GRAZING_OFF,        new int[]{FIELD_ACTION, FIELD_PASTURE});
	    ta.put(ACTION_SET_GRAZING,        new int[]{LIVESTOCK_ACTION});
	    ta.put(ACTION_SET_CONFINED,       new int[]{LIVESTOCK_ACTION});
	    ta.put(ACTION_FEED_CONFINED,      new int[]{FEED_BUDGET_ACTION});
	    ta.put(ACTION_FEED_GRAZING,       new int[]{FEED_BUDGET_ACTION});
	    ta.put(ACTION_FEED_TYIELD,        new int[]{FEED_BUDGET_ACTION});
	    
//System.out.println("BUILDING type maps");
	    // Construct type-mpas from groups
	    for(int aName : ta.keySet()){
//System.out.println("For action: " + aName);
	    	Collection<Integer> tc = new LinkedList<Integer>();
	    	for(int type : ta.get(aName)){
//	System.out.println("\t type=" + type + ", adding this value to typeActions values");
	    		tc.add(type);
	    		if(!actionTypes.containsKey(type))
	    			actionTypes.put(type, new TreeSet<Integer>());
	    		actionTypes.get(type).add(aName);
//	 System.out.println("\tsuccessfully added to list: " + type + " = " + actionTypes.get(type));
	    	}
	    	typeActions.put(aName, tc);	    	
	    }
	    
//System.out.println("actionTypes=" + actionTypes);
//System.out.println("typeActions=" + typeActions);
	}

	
	/** Whether two actions can be peformed on the same round */
	public boolean areActionsCompatible(Action a1, Action a2){
		// If actions have the same name, then they are not compatible
		// because duplicate actions are not allowed
		if(a1.equals(a2)) return false;
		// If the action is to reset then it isn't compatible with any actions
		if(a1.id == ACTION_REVERT
		|| a2.id == ACTION_REVERT)
			return false;
		// Check if they are both field actions
    	if(isActionType(a1, DairyAgent.FIELD_ACTION)
    	&& isActionType(a2, DairyAgent.FIELD_ACTION)){
	    	// If they refer to the same field..
 //System.out.println("compat-next pahse");
		    if(a1.args.length > 0 && a2.args.length > 0
		    && ((IntegerData)a1.args[0]).intValue() == ((IntegerData)a2.args[0]).intValue()){
		    	// If they belong to the same group then they are not compatible
		    	if(isActionType(a1, DairyAgent.FIELD_SEW)
		    	&& isActionType(a2, DairyAgent.FIELD_SEW))
		    		return false;
		    	if(isActionType(a1, DairyAgent.FIELD_GRASS)
	   	    	&& isActionType(a2, DairyAgent.FIELD_GRASS))
		    	    return false;
		    	if(isActionType(a1, DairyAgent.FIELD_REMOVAL)
			   	&& isActionType(a2, DairyAgent.FIELD_REMOVAL))
				    return false;
		    }
    	}
	    return true;
	}
	
	public List<DecisionNarrative> getNarratives(){
		return narratives;
	}
	
	/** @inheritDoc */
	public int getType() {
		return Constants.RANDOM_AGENT;
	}
	
	public void done(){
		super.decisionsMade();
	}

	@Override
	public void notifyStartStep(){
		super.notifyStartStep();
		narratives.clear();
//System.out.println("DairyAgent: Making actions...");
//Utils.awaitEnter();
		// Select random land use
//		for(int y = 0; y < params.ROWS; y++){
//			for(int x = 0; x < params.COLS; x++){
//				landUse.setUse(new LandCell(x, y), (int)(rand.nextDouble() * params.LAND_USES));
//			}
//		}
//		((DataTX)getItem(Constants.OUTPUT_NARRATIVE)).setText("Selecting random land use.");
		
//		if(params.LOGGING_DECISIONS)
//			System.out.println(getLog());
	}
		
	
	/** Returns actions which have been executed */
//	public Hashtable<String, Object[]> getActions

	@Override
    public void notifyEndStep(){
//System.out.println("DairyAgent.notifyEndStep()");
		for(DecisionNarrative n : narratives)
			System.out.println(n);
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
	public Object clone(){ return new DairyAgent(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object*/
	protected DairyAgent(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
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
