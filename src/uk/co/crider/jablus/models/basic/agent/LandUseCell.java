package uk.co.crider.jablus.models.basic.agent;

import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.util.Random;

/** Reprsents a LandCell with a use applied. Keeps track of use histories etc. */
public class LandUseCell extends LandCell {
	
	/** Unique class ID */
    private static final long serialVersionUID = 1L;

	// Params
    private static final double HISTORIC_IMPORTANCE = 0.5;
	private static final double DECAY_COEFFICIENT = 0.8;
	
	public static final int SORT_BY_LOCATION = 0;
	public static final int SORT_BY_ALT_UNCERTAINTY = 1;

	// The impact uncertainty has on percieved advantage
	private static final double UNCERTAINTY_IMPACT = 0.5;

	private Parameters params;
	
	/** The use this cell is applied to */
	private int use;
	private int bestUse;
	private int altUse;
	private int roundsUnchanged;
	private Random random;
	private int sortMode;
	
	private double[] profit;
	private double[] profitU;
	private double[] profitT;
	private double[] profitTU;

	public LandUseCell(int x, int y, Parameters params){ this(x, y, params, 0, null); }	
	public LandUseCell(int x, int y, Parameters params, Random random){ this(x, y, params, 0, random); }	
	public LandUseCell(int x, int y, Parameters params, int use){ this(x, y, params, use, null); }	
	public LandUseCell(int x, int y, Parameters params, int use, Random random){
	    super(x, y);
	    this.params = params;
	    this.use = use;
	    this.random = random;
	    this.bestUse = 0;
	    this.roundsUnchanged = 0;
	    this.sortMode = SORT_BY_LOCATION;
	    this.profit = new double[params.LAND_USES];
	    this.profitU = new double[params.LAND_USES];
	    this.profitT = new double[params.LAND_USES];
	    this.profitTU = new double[params.LAND_USES];
	    // Initialise uncertainties
		for(int i = 0; i < params.LAND_USES; i++){
			profitU[i] = 1;
			profitTU[i] = 1;
		}
	}
	
	/** Set the sort order of this cell */
	public void setSortMode(int mode){
		sortMode = mode;
	}
	
	/** Establishes if two cells are equal */
	public boolean equals(Object o){
		return super.equals(o);
	}
	/** Establishes order of this cell with another */
	public int compareTo(Object o) {
		if(o instanceof LandUseCell && sortMode == SORT_BY_ALT_UNCERTAINTY){
			LandUseCell cell = (LandUseCell)o;
			if(profitU[getAltUse()] > cell.getProfitU()[cell.getAltUse()]) return -1;
			if(profitU[getAltUse()] < cell.getProfitU()[cell.getAltUse()]) return 1;
			// If they are equal then order by random
			if(random.nextDouble() < 0.5) return -1;
			return 1;
		}
		return super.compareTo(o);
	}

	
	/** Returns the current use for this cell */
	public int getUse(){ return use; }
	/** Get the use which provided the most profit in previous rounds */
	public int getBestUse(){ return bestUse; }
	
	/** Returns an alternitive use for this cell */
	public int getAltUse(){ return altUse; }
	/** Sets the use of the cell to use which provided most profit in previous rounds */
	public void setBestUse(){
		setUse(bestUse);
	}
	/** Sets the use of the cell to use which provided most profit in previous rounds */
	public void setAltUse(){
		setUse(altUse);
	}
	/** Sets the use of the cell to the same use as before */
	public void setSameUse(){
		setUse(use);
	}
	/** Sets the use of the cell to the given value,
	 * returning true if this is the best use for the cell */
	public boolean setNewUse(int use){
		setUse(use);
		return use == bestUse;
	}
	/** Set the use of this cell */
	private void setUse(int newUse){
//System.out.println(use + "->" + newUse);
		if(newUse == use)
			roundsUnchanged++;
		else
			roundsUnchanged = 0;
		use = newUse;
	}
	/** Returns whether the last call to setUse changed the use of the cell */
	public boolean useChanged(){
		return roundsUnchanged == 0;
	}
	
	/** Update the profit recieved during the last round */
	public void setProfit(double profitNew, double trendMax){
		
		// Calculate new trend
		double newTrend = 0;
		if(roundsUnchanged > 0){
			newTrend = profitNew - profit[use];
//System.out.println(use + ": \t" + newTrend);
			if(roundsUnchanged > 1)
				newTrend = reenforce(profitT[use], newTrend);
		}
//System.out.println(use + ": \t" + profit[use] +"\t" + + profitNew + "\t" + newTrend + ",\t" + profitU[use] + ",\t" + profitT[use] + "\t" + profitTU[use]);
		profit[use] = profitNew;
		profitT[use] = newTrend;
		
		// Calculate new profit uncertainties
		profitU[use] = 0;
		for(int i = 0; i < params.LAND_USES; i++)
			if(i != use) profitU[i] = decay(profitU[i]);

		// Calculate new trend uncertainties
		double trendDiff = newTrend - profitT[use];
//		double newTU = 1.0;
		if(roundsUnchanged > 0)
		profitTU[use] = reenforce(profitTU[use], trendMax == 0 || roundsUnchanged == 0 ? 1.0 : trendDiff/trendMax);
//		System.out.print(use + ": \t" + newTU);
		for(int i = 0; i < params.LAND_USES; i++)
			if(i != use) profitTU[i] = decay(profitTU[i]);
//System.out.println(use + ": \t" + profit[use] + ",\t" + profitU[use] + ",\t" + profitT[use] + "\t" + profitTU[use]);

		// Calculate the best use and next best uses based on previous profit
		bestUse = 0;
		altUse = 1;
		for(int i = 0; i < params.LAND_USES; i++){
			if(profit[i] > profit[bestUse]){
				altUse = bestUse;
				bestUse = i;
			}
		}
		if(profit[bestUse] < profit[altUse]){
			int tmp = bestUse;
			bestUse = altUse;
			altUse = tmp;
		}
//System.out.println("alt: " + altUse + ", un=" + profitU[altUse]);
	}
	
	/** Returns a value with uncertainty increased with respect to 
	 * the DECAY_COEFFICIENT to simulate decay effect resultinf from 
	 * a lack of belief re-enforcement */
	private double decay(double u){
		return (1 - (1 - u) * DECAY_COEFFICIENT);
	}
	/** Returns the value of a belief re-enforced by new data */
	private double reenforce(double vOld, double vNew){
		return vOld * HISTORIC_IMPORTANCE + vNew * (1 - HISTORIC_IMPORTANCE);
	}
	/** returns the advantage of using this cell for its
	 * best use compared with its next best (between 0 and 1)*/
	public double getAdvantage(){
		// Advantage is the difference between best and next best
		// If uncertainty of next best use is very high,
		// then percieved advantage is less certain and hence lower
		return ((profit[bestUse] - profit[altUse])/profit[bestUse]) * (1 - profitU[altUse] * UNCERTAINTY_IMPACT);
	}
	public double getProfitUAlt(){ return profitU[getAltUse()]; }
	public double[] getProfit(){ return profit; }
	public double[] getProfitT(){ return profitT; }
	public double[] getProfitTU(){ return profitTU; }
	public double[] getProfitU(){ return profitU; }
	
}
