package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.utils.Utils;
import jadex.runtime.Plan;


/** Generic plan which holds all beliefs and provides methods to batch save them */
public class GenericPlan extends Plan {
	
	/** Unique class ID */
    private static final long serialVersionUID = -3179616766729516035L;

	public static final int NEW_PATTERN_STRATEGY = 0;
	public static final int CHANGE_AND_COMPARE_STRATEGY = 1;
	public static final int AWAIT_TREND_STRATEGY = 2;
	public static final int RETURN_TO_PREVIOUS_STRATEGY = 3;
	public static final int MAINTAIN_USE_STRATEGY = 4;
	public static final int MAINTAIN_BEST_STRATEGY = 5;
	public static final String[] STRATEGY_NAMES = new String[]{
		"New Pattern",
		"Change and Compare",
		"Await Trend",
		"Return to Previous",
		"Maintain Use",
		"Maintain Best"
	};
    public static final String[] STRATEGY_SHORTHAND = new String[]{
		"NP",
		"CH",
		"TR",
		"RT",
		"KP",
		"BS"
	};
	public static final String[] UNCERTAINTY_DESCRIPTION = new String[]{
		"certain",
		"fairly certain",
		"half certain",
		"failry uncertain",
		"very uncertain"
	};
	public static final String[] SATISFACTION_DESCRIPTION = new String[]{
		"very dissatisfied",
		"fairly dissatisfied",
		"satisfied",
		"very satisfied",
		"extremely satisfied"
	};
	public static final String[] CURIOSITY_DESCRIPTION = new String[]{
		"not at all curious",
		"slightly curious",
		"fairly curious",
		"highly curious"
	};
   
	// Beliefs
	protected BDIAgent grounding;
//	protected Simulation sim;
//	protected EnvironmentInterface env;
	protected LandUseScheme landUse;

	protected double   satisfaction;
	protected double   curiosity;
	protected double   uncertainty;
	protected double   profitUncertainty;
	protected double   trendUncertainty;
	protected double   changeUncertainty;
	protected double   bestProfit;
	protected double   lastProfit;
	
	protected int      strategy; // Last strategy used

	protected double   VERY_HIGH;
	protected double   HIGH;
	protected double   AVERAGE;
	protected double   LOW;
	protected double   VERY_LOW;
	
	public GenericPlan(){
		// Core beliefs
		this.grounding = (BDIAgent)getBeliefbase().getBelief("grounding").getFact();
//		this.sim = (Simulation)getBeliefbase().getBelief("sim").getFact();
//		this.env = (EnvironmentAgent)getBeliefbase().getBelief("env").getFact();
//		this.env = sim.getEnv();
		this.landUse = (LandUseScheme)getBeliefbase().getBelief("land_use").getFact();
//		this.gui = (UserInterface)getBeliefbase().getBelief("gui").getFact();
//		this.land = (Set<LandCell>)getBeliefbase().getBelief("land").getFact();

		// Beliefs updated during revision
		this.satisfaction = ((Double)getBeliefbase().getBelief("satisfaction").getFact()).doubleValue();
		this.curiosity = ((Double)getBeliefbase().getBelief("curiosity").getFact()).doubleValue();
		this.uncertainty = ((Double)getBeliefbase().getBelief("uncertainty").getFact()).doubleValue();
		
		this.bestProfit = ((Double)getBeliefbase().getBelief("best_profit").getFact()).doubleValue();
		this.lastProfit = ((Double)getBeliefbase().getBelief("last_profit").getFact()).doubleValue();
		this.profitUncertainty = ((Double)getBeliefbase().getBelief("uncertainty_profit").getFact()).doubleValue();
		this.trendUncertainty = ((Double)getBeliefbase().getBelief("uncertainty_trend").getFact()).doubleValue();
		this.changeUncertainty = ((Double)getBeliefbase().getBelief("uncertainty_change").getFact()).doubleValue();
		
		// Beliefs updated during land use selection
		this.strategy = ((Integer)getBeliefbase().getBelief("strategy").getFact()).intValue();

		this.VERY_HIGH = ((Double)getBeliefbase().getBelief("VERY_HIGH").getFact()).doubleValue();
		this.HIGH = ((Double)getBeliefbase().getBelief("HIGH").getFact()).doubleValue();
		this.AVERAGE = ((Double)getBeliefbase().getBelief("AVERAGE").getFact()).doubleValue();
		this.LOW = ((Double)getBeliefbase().getBelief("LOW").getFact()).doubleValue();
		this.VERY_LOW = ((Double)getBeliefbase().getBelief("VERY_LOW").getFact()).doubleValue();
	}
	
	public void body(){
		
	}
	
	protected void saveBeliefs(){
		getBeliefbase().getBelief("satisfaction").setFact(Double.valueOf(satisfaction));
		getBeliefbase().getBelief("curiosity").setFact(Double.valueOf(curiosity));
		getBeliefbase().getBelief("uncertainty").setFact(Double.valueOf(uncertainty));
		getBeliefbase().getBelief("best_profit").setFact(Double.valueOf(bestProfit));
		getBeliefbase().getBelief("last_profit").setFact(Double.valueOf(lastProfit));
		getBeliefbase().getBelief("uncertainty_profit").setFact(Double.valueOf(profitUncertainty));
		getBeliefbase().getBelief("uncertainty_trend").setFact(Double.valueOf(trendUncertainty));
		getBeliefbase().getBelief("uncertainty_change").setFact(Double.valueOf(changeUncertainty));

		getBeliefbase().getBelief("strategy").setFact(Integer.valueOf(strategy));
	}
	
	// Inform the environment agent that were done with decision making
/*	protected void notifyDone(String message){
		IMessageEvent me = createMessageEvent(message);
	    me.getParameterSet(SFipa.RECEIVERS).addValue(sim.getAgentIdentifier());
	    sendMessage(me);
	}
*/
	protected String getStatus(){

		return getStatusHeading() +
			SelectLandUsePlan.STRATEGY_SHORTHAND[strategy] + "\t" +
			landUse.getUseCount()[0] + "\t" +
			landUse.getUseCount()[1] + "\t" +
			Utils.round(satisfaction) + "\t" +
			Utils.round(curiosity) + "\t" +
			Utils.round(uncertainty) + "\t" +
			Utils.round(profitUncertainty) + "\t" +
			Utils.round(trendUncertainty) + "\t" +
			Utils.round(changeUncertainty) + "\t" +
			Utils.round(landUse.getAvgProfit()[0]) + "\t" +
			Utils.round(landUse.getAvgProfit()[1]) + "\t" +
			Utils.round(landUse.getAvgProfitT()[0]) + "\t" +
			Utils.round(landUse.getAvgProfitT()[1]) + "\t" +
			Utils.round(bestProfit) + "\t" +
			Utils.round(lastProfit) + "\t";
	}
	
	private static int count = 0;
	private static final int REPEAT = 10;
	private String getStatusHeading(){
		if(count++ % REPEAT != 0) return "";
		return
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"     \t" +
		"\n"+
		"Plan\t" +
		"UC[0]" + "\t" +
		"UC[1]" + "\t" +
		"Sat\t" +
		"Cur\t" +
		"Un\t" +
		"UnP\t" +
		"UnT\t" +
		"UnC\t" +
		"PA[0]\t" +
		"PA[1]\t" +
		"PT[0]\t" +
		"PT[1]\t" +
		"BstPA\t" +
		"LstPA\t" +
		"\n" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"-----\t" +
		"\n";
	}

	/** Return the reason for selection as an english statement */
	public String getNarrative(){
		StringBuffer reason = new StringBuffer();
		switch(strategy){
		case NEW_PATTERN_STRATEGY :
			reason.append("Trying completely new pattern because I'm");
			reason.append(" " + curiosity(curiosity) + " about the resulting profit");
			break;
		case CHANGE_AND_COMPARE_STRATEGY :
			reason.append("Changing the land use pattern because I'm");
			reason.append(" " + certainty(changeUncertainty) + " about which land use gives more profits");
			break;
		case AWAIT_TREND_STRATEGY :
			reason.append("Waiting with the same land use because I'm");
			reason.append(" " + certainty(trendUncertainty) + " about the profit trend of the current use");
			break;
		case RETURN_TO_PREVIOUS_STRATEGY :
			reason.append("Returning to a previous land use because I'm");
			reason.append(" " + certainty(changeUncertainty) + " about which land use gives more profits");
			reason.append(" and");
			reason.append(" " + satisfaction(satisfaction) + " with the current situation");
			break;
		case MAINTAIN_USE_STRATEGY :
			reason.append("Staying with the same land use because I'm");
			reason.append(" " + satisfaction(satisfaction) + " with the current situation");
			break;
		case MAINTAIN_BEST_STRATEGY :
			reason.append("Applying the best known land use");
//			reason.append(" not intersted in any other course of action");
			break;
		}
		reason.append(".");
		return reason.toString();
	}
	
	private String certainty(double value){
		int i = (int)(value * UNCERTAINTY_DESCRIPTION.length);
		i = i < 0 ? 0 : i >= UNCERTAINTY_DESCRIPTION.length ? UNCERTAINTY_DESCRIPTION.length - 1 : i;
		return UNCERTAINTY_DESCRIPTION[i];
	}
	private String curiosity(double value){
		int i = (int)(value * CURIOSITY_DESCRIPTION.length);
		i = i < 0 ? 0 : i >= CURIOSITY_DESCRIPTION.length ? CURIOSITY_DESCRIPTION.length - 1 : i;
		return CURIOSITY_DESCRIPTION[i];
	}
	private String satisfaction(double value){
		int i = (int)(value * SATISFACTION_DESCRIPTION.length);
		i = i < 0 ? 0 : i >= SATISFACTION_DESCRIPTION.length ? SATISFACTION_DESCRIPTION.length - 1 : i;
		return SATISFACTION_DESCRIPTION[i];
	}
		
}
