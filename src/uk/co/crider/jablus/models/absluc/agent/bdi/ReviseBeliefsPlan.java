package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.models.absluc.Constants;



/** Allows agent to revise beliefs while environment stays in a static state */
public class ReviseBeliefsPlan extends GenericPlan {
		
	/** Unique class ID */
    private static final long serialVersionUID = 1546150629970477859L;

    /** Importance of profit uncertainty in finding out overall uncertainty */
	double PROFIT_WEIGHT = 0.5;
	double TREND_WEIGHT = 0.2;
	double CHANGE_WEIGHT = 0.3;
	
	/** Constructor */
	public ReviseBeliefsPlan(){ super(); }
	
	/** Plan code */
	public void body() {
//System.out.println("ReviseBeliefsPlan");
	
/// TODO: needs to be updated
//		landUse.updateProfit(grounding.getProfitsDetail());
//		lastProfit = grounding.getProfits();
//System.out.println("ReviseBeliefsPlan: Last profit = " + lastProfit);
		
		profitUncertainty = landUse.getAvgProfitU();
		trendUncertainty = landUse.getAvgProfitTU();
		changeUncertainty = landUse.getAvgProfitUAlt();
		uncertainty = (profitUncertainty * PROFIT_WEIGHT) + 
		              (trendUncertainty  * TREND_WEIGHT) +
		              (trendUncertainty  * CHANGE_WEIGHT);
//System.out.println(uncertainty);
		satisfaction = (1-uncertainty);
		if(bestProfit != 0)
			satisfaction = (lastProfit/bestProfit)*(1-uncertainty);
		curiosity = uncertainty;
		if(lastProfit > bestProfit)
			bestProfit = lastProfit;
		
		// TODO: Not using prices at the moment, this is key information
		// but because vicci didn't mention it during simulation, she neglected to use it
		
		// Update beliefs
		saveBeliefs();

		// Print out new belief status
		if(Constants.AGENT_STATUS_OUTPUT == Constants.AGENT_STATUS_DATA)
			System.out.println(getStatus());
		
//System.out.println("ReviseBeliefsPlan: done update");
		grounding.bdiDoneUpdate();
	}
	

}
