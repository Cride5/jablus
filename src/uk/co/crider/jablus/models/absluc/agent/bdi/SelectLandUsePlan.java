package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.data.DataTX;


/** Agent makes actions, possibly updating the environmental state */
public class SelectLandUsePlan extends GenericPlan {
	
	/** Unique class ID */
    private static final long serialVersionUID = -3161441568766130784L;
    
	public SelectLandUsePlan(int strategy){
		super();
		super.strategy = strategy;
	}
	
	/** Plan code */
	public void body() {
//System.out.println("SelectLandUsePlan: " + STRATEGY_NAMES[strategy]);

		// Update reasoning for land use decision
		((DataTX)grounding.getItem(Constants.OUTPUT_NARRATIVE)).setText(getNarrative());


		double ratio = 0.0;
		double distribution = 0.0;
//		boolean useChanged = false;
		
		switch(strategy){
		case CHANGE_AND_COMPARE_STRATEGY :
			double weightedAdvantage = Math.pow(landUse.getAdvantage(), 0.3); 
			ratio = (1 - weightedAdvantage) * 0.5;
//			ratio = (1 - currentLandUse.getAdvantage()) * 0.5; //changeUncertainty * 0.5;
//System.out.println("adv: " + currentLandUse.getAdvantage() + ", wtdadv: " + weightedAdvantage + ", ratio: " + ratio);
			distribution = 1.0;
			// If this is the first change to compare, or the current scheme proves
			// best then store this scheme for future reference
			if(!landUse.isMemorised() || landUse.isMemorisedBetter()){
				landUse.memorise();
			}
			// Select new scheme (based on most uncertain)
			landUse.selectUse(LandUseScheme.SELECT_UNCERTAIN, ratio, distribution);
//			useChanged = true;
			break;
		case NEW_PATTERN_STRATEGY :
			ratio = 1 - profitUncertainty;
//ratio = 0.5;
//System.out.println("NEW PATTERN BEING SELECTED");
			landUse.selectUse(LandUseScheme.SELECT_RANDOM, ratio);
			landUse.clearMemory();
//System.out.println(landUse);
//			useChanged = true;
			break;
		case RETURN_TO_PREVIOUS_STRATEGY :
			landUse.selectUse(LandUseScheme.SELECT_MEMORISED);
			landUse.clearMemory();
//			useChanged = true;
			break;
		case MAINTAIN_BEST_STRATEGY : 
			landUse.selectUse(LandUseScheme.SELECT_BEST);
			landUse.clearMemory();
//			useChanged = true;
			break;
		case AWAIT_TREND_STRATEGY :
			landUse.selectUse(LandUseScheme.SELECT_LAST);
			break;
		case MAINTAIN_USE_STRATEGY : 
			landUse.selectUse(LandUseScheme.SELECT_LAST);
			landUse.clearMemory();
			break;
		}
		
		// Update beliefs
		saveBeliefs();

//System.out.println("SelectLandUsePlan: BDI agent " + grounding.getNameId() + " making land use decision");		
		// Carry out decision
//		grounding.makeDecision(landUse);
//		grounding.makeDecision(new DecisionProfile());
		grounding.bdiDecisionsMade();
//System.out.println("SelectLandUsePlan: Finishing plan");
		
		// If use was changed, notify environment
//		if(useChanged)
//			env.notifyLandUse(id, landUse);
			

		// Tell the environment that we are done
//		notifyDone("decisions_made_event");

		// The plan has succeeded so update beliefs to reflect this.
//		getBeliefbase().getBelief("land_use_selected").setFact(Boolean.valueOf(true));

	}
	
}
