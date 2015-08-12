package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.utils.Utils;

import java.util.Random;

/** Simulates the change in population of cows and bulls on the farm using probabilities of
 * birth and death, along with heard management policy. Using information on lactation
 * stage of individual cows, and block of they year, the module estimates total milk and manure
 * production.
 * 
 * Cow popoulation is stored as a two dimensional int[] with:
 * first dimension representing lactation number (0 = heifer)
 * second dimension representing age in months for heifer, and months since calving for all cows
 * Cows move up in the lactation stage two months before giving birth, which means that the first
 * two elements in the lactating cow array are dry cows which do not lactate. An example has been 
 * presented for illustrative purposes.
 * 
 * 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0  <- at some point, heifers here concieve, then the move to the next level. If they fall off here they are considered infertile and are culled
 * 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0  <- cows here will at some point become pregnant and move to next stage. If they fall off they are considered infertile and culled 
 * 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 4  <- Once cows fall off the end of this array they are automatically culled 
 * ^  ^ |-> Cows from here are lactating 
 * |  |
 * Cows in these columns are dry and about
 * to give birth
 * 
 * In this example, 2 cows have just been born
 * 6 heifers are 11 months old and need to concieve soon to avoid culling
 * 1 heifer has conceived and is in its last block of pregnancy
 * 3 cows have just given birth and have started lactating
 * 4 cows are at the end of their second lactation and are about to be culled 
 */
public class Livestock extends CompoundData implements DriverEndogeneous{

	public static final int MAX_LACTATIONS = 5;
	// Sources for periods: http://www.arc.agric.za/home.asp?PID=1&ToolID=2&ItemID=1927
	public static final int PERIOD_REARING   = 65; // Weeks to rear heifers (About 15 months)
	public static final int PERIOD_GESTATION = 40; // Weeks of pregnancy
	public static final int PERIOD_DRY       =  8; // Dry weeks at end of pregnancy
	public static final int PERIOD_LACTATION = 42; // Weeks in lactation
	public static final int PERIOD_WAITING   =  8; // Weeks after calving to wait until breeding
	public static final int PERIOD_BREEDING  =  8; // Weeks to attempt insemination for

	public static final int ANNUAL_YIELD = 10000; // Average annual yield per cow
	public static final float PROB_CON_COW = 0.205f; // 60% chance of conception (per month) = 1 - 0.4 ^ (1/4) = 20.4% per week TODO: Check!
	public static final float PROB_CON_HEIFER = 0.474f; // 80% chance of conception (per month) = 47.4% per week TODO: Check!
	public static final float PROB_MORTALITY_ADULT = 0.000291f; // 1.5% chance per year
	public static final float PROB_MORTALITY_CALF = 0.1f; // 10% chance in the first week
	public static final float PROB_CALF_FEMALE = 0.5f; // 50% chance than newborn is female
	public static final float PROB_LIVE_CULL = 0.5f; // 50% chance that culled cows can be sold live
//	public static final int MONTHLY_COW_SILAGE = 9000; // Kg of silage required per cow each block
//	public static final int MONTHLY_COW_CONCENTRATES = 3600; // Kg of concentrates required per cow each block
//	public static final int MONTHLY_COW_GRAZED_DM = 500; // Kg of grazed dm required per cow each block
//	public static final int WEEKLY_MANURE = 250; // Kg of manure produced per cow per week

	// Public identifiers
	public static final int OFFSET     = 1500;
	public static final int HEIFERS01  = OFFSET +  0;
	public static final int HEIFERS1P  = OFFSET +  1;
	public static final int COWS1ST    = OFFSET +  2;
	public static final int COWS2ND    = OFFSET +  3;
	public static final int COWS3PL    = OFFSET +  4;
	public static final int STEERS     = OFFSET +  5;
	public static final int BREEDING   = OFFSET +  6;
	public static final int MILKING    = OFFSET +  7;
	public static final int CARCASES   = OFFSET +  8;
	public static final int NEWBORN    = OFFSET +  9;
//	public static final int MANURE_DRY = OFFSET + 10;
	public static final int MANURE_WET = OFFSET + 11;
//	public static final int BULLS     = OFFSET + ;

	public static final int[] HERD_GROUPS = {
		HEIFERS01,
		HEIFERS1P,
		COWS1ST,
		COWS2ND,
		COWS3PL
	};
	public static final int[] TRADE_GROUPS = {
		Market.FINISHED_HEIFERS,
		Market.CULL_COWS,
		Market.BULL_CALVES,
		Market.HEIFER_CALVES,
		Market.CALVED_HEIFERS
	};
	public static final int[] SALE_GROUPS = {
		HEIFERS01,
		HEIFERS1P,
		COWS1ST,
		COWS2ND,
		COWS3PL,
		STEERS
	};
	public static final int[] COUNTED_GROUPS = {
		HEIFERS01,
		HEIFERS1P,
		COWS1ST,
		COWS2ND,
		COWS3PL,
		STEERS,
		BREEDING,
		MILKING,
		CARCASES,
		NEWBORN
	};
	
	
	public static boolean isLivestock(int id){
		return id >= HEIFERS01 && id <= STEERS;
	}
	
	public static int getType(int marketId){
		switch(marketId){
		case Market.BULL_CALVES      : return STEERS;
		case Market.HEIFER_CALVES    : return HEIFERS01;
		case Market.FINISHED_HEIFERS : return HEIFERS1P;
		case Market.CALVED_HEIFERS   : return COWS1ST;
		case Market.CULL_COWS        : return COWS3PL;
		}
		return marketId;
	}
	
	
/*	private static final double[][] LACTATION_CURVES = new double[][]{
		// Month of calving
		// Jan    Feb    Mar    Apr    May    Jun    Jul    Aug    Sep    Oct    Nov    Dec        MoY  
		{0.123, 0.000, 0.000, 0.047, 0.064, 0.074, 0.085, 0.096, 0.106, 0.118, 0.129, 0.134}, // Jan
		{0.120, 0.110, 0.000, 0.000, 0.045, 0.059, 0.068, 0.077, 0.086, 0.097, 0.108, 0.116}, // Feb
		{0.130, 0.135, 0.124, 0.000, 0.000, 0.053, 0.068, 0.077, 0.086, 0.098, 0.109, 0.120}, // Mar
		{0.119, 0.128, 0.134, 0.124, 0.000, 0.000, 0.053, 0.067, 0.076, 0.086, 0.098, 0.108}, // Apr
		{0.121, 0.132, 0.143, 0.151, 0.141, 0.000, 0.000, 0.060, 0.075, 0.086, 0.097, 0.108}, // May
		{0.104, 0.117, 0.128, 0.138, 0.146, 0.138, 0.000, 0.000, 0.060, 0.073, 0.083, 0.094}, // Jun
		{0.087, 0.099, 0.110, 0.120, 0.132, 0.141, 0.131, 0.000, 0.000, 0.055, 0.069, 0.079}, // Jul
		{0.075, 0.085, 0.094, 0.106, 0.117, 0.130, 0.139, 0.128, 0.000, 0.000, 0.053, 0.066}, // Aug
		{0.067, 0.075, 0.084, 0.095, 0.108, 0.120, 0.134, 0.138, 0.128, 0.000, 0.000, 0.053}, // Sep
		{0.054, 0.068, 0.076, 0.086, 0.097, 0.112, 0.125, 0.136, 0.142, 0.131, 0.000, 0.000}, // Oct
		{0.000, 0.051, 0.061, 0.070, 0.079, 0.091, 0.103, 0.115, 0.125, 0.130, 0.121, 0.000}, // Nov
		{0.000, 0.000, 0.046, 0.063, 0.071, 0.082, 0.094, 0.105, 0.116, 0.126, 0.133, 0.122}, // Dec
	};
*/

	// Input data
	private IntegerData[] propGrazing = new IntegerData[HERD_GROUPS.length];	
	
	/** Returns the group of a cow in state lac, wsc */
	private int getGroup(int lac, int wsc){
		if(lac == 0 && wsc < Time.WEEKS_YEAR)
			return HEIFERS01;
		if((lac == 0 && wsc >= Time.WEEKS_YEAR) || (lac == 1 && wsc < PERIOD_DRY))
			return HEIFERS1P;
		if((lac == 1 && wsc >= PERIOD_DRY) || (lac == 2 && wsc < PERIOD_DRY))
			return COWS1ST;
		if((lac == 2 && wsc >= PERIOD_DRY) || (lac == 3 && wsc < PERIOD_DRY))
			return COWS2ND;
		if((lac == 3 && wsc >= PERIOD_DRY) || lac > 3)
			return COWS3PL;
		return -1;
	}
	
	private int actualConfined;
	void setActualConfined(int n){
		actualConfined = n;
	}
	private int actualGrazing;
	void setActualGrazing(int n){
		actualGrazing = n;
	}
	/** Returns true if all animals are currently confined */
	public boolean allConfined(){
		return actualGrazing == 0;
	}
	/** Returns true if all animals are currently grazing */
	public boolean allGrazing(){
		return actualConfined == 0;
	}
	
	/** Returns the proportion of cows with the given characteristics
	 * which have been assigned to grazing */
	public double getPropGrazing(int lac, int wsc){
		return getPropGrazing(getGroup(lac, wsc));
	}
	
	/** Returns the proportion of cows in the given group
	 * which have been assigned to grazing */
	public double getPropGrazing(int group){
		return (double)propGrazing[group - OFFSET].intValue() / 100;
	}
	
	/** Sets the proportion of cows grazing in the given group */
	public void setPropGrazing(int group, int percent){
		propGrazing[group - OFFSET].setValue(percent);
//System.out.println(Utils.arrayString(propGrazing));
	}

//	private int[] toAdd = new int[HERD_GROUPS.length];
	/** Adds the given number of cattle to the given group, returns the number added or removed */
	int addCattle(int group, int qty){
//System.out.println("Adding " + Constants.getName(group) + ", qty=" + qty);
		if(qty > 0){
			switch(group){
			case Market.HEIFER_CALVES  : cows[0][1]              += qty; break; // Heifer calves from market assumed to be 1 weeks old
			case Market.CALVED_HEIFERS : cows[1][PERIOD_DRY]     += qty; break; // Cows assumed to have just calved
			case HEIFERS01             : cows[0][8]              += qty; break; // 0-1yrs group assumed to be 8 weeks old
			case HEIFERS1P             : cows[0][PERIOD_REARING] += qty; break; // 1+ yrs group assumed ready for breeding
			case COWS1ST               : cows[1][PERIOD_DRY]     += qty; break; // Cows assumed to have just calved
			case COWS2ND               : cows[2][PERIOD_DRY]     += qty; break; // Cows assumed to have just calved
			case COWS3PL               : cows[3][PERIOD_DRY]     += qty; break; // Cows assumed to have just calved		
			}
			updateTotals();
			return qty;
		}
		if(qty < 0){
			int removed = 0;
			switch(group){
			case Market.HEIFER_CALVES    : removed = removeCattle(-qty, 0               , 0, 0                                , 3                  ,  1); break; // No more than 3 weeks old
			case Market.FINISHED_HEIFERS : removed = removeCattle(-qty, 1               , 0, PERIOD_DRY - 1                   , Time.WEEKS_YEAR    , -1); break; // Age of 1 year and above (non calved)
			case Market.CALVED_HEIFERS   : removed = removeCattle(-qty, 1               , 1, PERIOD_DRY + 8                   , PERIOD_DRY         , -1); break; // No more than 8 weeks after calving
			case Market.CULL_COWS        : removed = removeCattle(-qty, cows.length - 1 , 0, cows[cows.length - 1].length - 1 , 0                  , -1); break; // Remove from all groups, starting with oldest
			case HEIFERS01               : removed = removeCattle(-qty, 0               , 0, 0                                , Time.WEEKS_YEAR - 1,  1); break; 
			case HEIFERS1P               : removed = removeCattle(-qty, 1               , 0, PERIOD_DRY - 1                   , Time.WEEKS_YEAR    , -1); break; 
			case COWS1ST                 : removed = removeCattle(-qty, 2               , 1, PERIOD_DRY - 1                   , PERIOD_DRY         , -1); break; 
			case COWS2ND                 : removed = removeCattle(-qty, 3               , 2, PERIOD_DRY - 1                   , PERIOD_DRY         , -1); break; 
			case COWS3PL                 : removed = removeCattle(-qty, cows.length - 1 , 3, cows[cows.length - 1].length - 1 , PERIOD_DRY         , -1); break; 
			}
			updateTotals();
//			System.out.println(Utils.arrayString(cows));
			return removed;
		}
		return 0;
	}
	
	// Internal Variables
	private int[][] cows; // Cow array
	/** Returns current cow population state */
	int[][] getCows(){ return cows; }
	private double Eavail; // Energy stress index
	/** Sets the energy availability of cows - this value affects changes of death */
	void setEavail(double Eavail){ this.Eavail = Eavail; }

	// Staticstics
	private int total = 0;
	/** Returns the total number of cows in the population */
	public int getTotal(){ return total; }
	private int[] cowGroups = new int[COUNTED_GROUPS.length];
	/** Returns the total number of cows in the given group */
	public int getTotal(int group){
//System.out.println("cowgroups=" + Utils.arrayString(cowGroups));
		if(Market.isTradeLivestock(group)){
			switch(group){
			case Market.HEIFER_CALVES    : return count(0, 0, 0, 3);
			case Market.CALVED_HEIFERS   : return count(1, 1, PERIOD_DRY, PERIOD_DRY + 8);
			case Market.FINISHED_HEIFERS : return getTotal(HEIFERS1P);
			case Market.CULL_COWS        : return getTotal();
			}
			return 0;
		}
		return cowGroups[group - OFFSET];
	}

	private IntegerData[] newCalves;
	public IntegerData[] getNewCalves(){ return newCalves; }
	private IntegerData[] carcases;
	public IntegerData[] getCarcases(){ return carcases; }
	
	// OUTPUTS
	private int[] cattleRemoved = new int[SALE_GROUPS.length];
	/** Returns the number of live cattle removed from the poulation
	 * These cattle can be sold to the market, unless they belong to the CARCASES group */
	public int getCattleRemoved(int group){
		return cattleRemoved[group - OFFSET];
	}

	// External drivers
	private Random rand;
	public Random getRand(){ return rand; }
	private Time time;
//	private Pasture pasture;
//	private Market economics;
	private Parameters params;
//	private Agent herdsman;

	public Livestock(Parameters params){
		super(Constants.DRIVER_LIVESTOCK);
		this.params = params;
//		this.herdsman = null;
		for(int i = 0; i < propGrazing.length; i++){
			propGrazing[i] = new IntegerData(i + OFFSET);
		}
		rand = new Random(params.LIVESTOCK_RANDOM_SEED);
		cows = new int[MAX_LACTATIONS + 1][];
		cows[0] = new int[PERIOD_REARING + PERIOD_BREEDING + PERIOD_GESTATION - PERIOD_DRY];
		for(int i = 1; i < cows.length; i++)
			cows[i] = new int[PERIOD_WAITING + PERIOD_BREEDING + PERIOD_GESTATION];
		Eavail = 1;
		
		newCalves = new IntegerData[HERD_GROUPS.length - 1];
		carcases = new IntegerData[HERD_GROUPS.length];
		for(int i = 0; i < HERD_GROUPS.length; i++){
			if(i > 0) newCalves[i - 1] = new IntegerData(0);
			carcases[i] = new IntegerData(0);
		}
	}
	
	/** Set agent in control of the this livestock herd */
	public void newHerdsman(Agent herdsman){
//		this.herdsman = herdsman;
	}

	public void init(DataSet drivers) {
//		this.drivers = drivers;
		time = (Time)drivers.getItem(Constants.TEMPORAL_TIME);
//		economics = (Market)drivers.getItem(Constants.DRIVER_ECONOMICS);
//		pasture = (Pasture)drivers.getItem(Constants.DRIVER_PASTURE);
	}

	/** Executes 1 week by updating cow population */
	public void execWeek(){
//if(params.LOGGING_LIVESTOCK) System.out.println("Livestock: Eavail=" + Eavail);
		// Reset livestock statistics
		for(int i = 0; i < cowGroups.length; i++) cowGroups[i] = 0;
		for(int i = 0; i < cattleRemoved.length; i++) cattleRemoved[i] = 0;
		// Initialise internal variables
		int[] conceptions = new int[cows.length];
		// Set statistics to 0
		for(int i = 0; i < HERD_GROUPS.length; i++){
			if(i > 0) newCalves[i - 1].setValue(0);
			carcases[i].setValue(0);
		}			
		
		// For all cows
		for(int lac = MAX_LACTATIONS; lac >= 0; lac--){
			int newConceptions = 0;
			// Cows which walk off end of array are too old or infertile and sold
			cattleRemoved[COWS3PL - OFFSET] += cows[lac][cows[lac].length - 1];
			// Cows which are 9 months since conception produce new born (2 months into next level)
			if(lac > 0)
				conceptions[lac - 1] += cows[lac][PERIOD_DRY - 1];
			// For each wsc (Weeks Since Calving)
			for(int wsc = cows[lac].length - 1; wsc > 0; wsc--){
				// Increase Weeks Since Calving (WSC)
				cows[lac][wsc] = cows[lac][wsc - 1];
				// Simulate probability of death by natural causes, including effects of energy stress
				double prob = PROB_MORTALITY_ADULT + (1 - Math.pow(Eavail, 0.025)) * PROB_MORTALITY_ADULT * 10;
				int nd = Utils.pRound(cows[lac][wsc] * prob, rand);
//if(nd > 0) System.out.println("cows[lac][wsc]=" + cows[lac][wsc] + ", PROB_MORTALITY_ADULT=" + PROB_MORTALITY_ADULT + ", Eavail=" + Eavail + ", pow=" + Math.pow(Eavail, 0.025) + ", nd=" + nd + ", prob=" + prob);
				cowGroups[CARCASES - OFFSET] += nd;
				int iCAR = getGroup(lac, wsc) - OFFSET;
				carcases[iCAR].setValue(carcases[iCAR].intValue() + nd);
				cows[lac][wsc] -= nd;
				// Simulate probability of re-insemination
				if((lac == 0 && wsc >= PERIOD_REARING + PERIOD_GESTATION - PERIOD_DRY)
				|| (lac >= 1 && wsc >= PERIOD_WAITING + PERIOD_GESTATION && lac < MAX_LACTATIONS)){
					int nc = Utils.pRound(cows[lac][wsc] * (lac == 0 ? PROB_CON_HEIFER : PROB_CON_COW), rand);
					newConceptions += nc;
					cows[lac][wsc] -= nc;
				}
			}
			if(lac < MAX_LACTATIONS) 
				// All cows which have conceived are moved next lactation stage
				cows[lac + 1][0] = newConceptions;
		}
		// Create newborns from pregnant cows
		int conTotal = 0;
		for(int i = 0; i < conceptions.length; i++){
			conTotal += conceptions[i];
			int iNC = i >= newCalves.length ? newCalves.length - 1 : i;
			newCalves[iNC].setValue(
				newCalves[iNC].intValue() + conceptions[i]);
		}
		// Simulate calf mortality
		int nd = Utils.pRound(conTotal * PROB_MORTALITY_CALF, rand);
		cowGroups[CARCASES - OFFSET] += nd;
		carcases[0].setValue(carcases[0].intValue() + nd);
		conTotal -= nd;
		// Split newborns into male/female
		int nc = Utils.pRound(conTotal * PROB_CALF_FEMALE, rand);
		cows[0][0] = nc;
		cowGroups[STEERS - OFFSET] = conTotal - nc;
		cowGroups[NEWBORN - OFFSET] += cows[0][0];
//System.out.println("New total=" + conTotal + " nd = " + nd + ", nc=" + nc);
//		cowGroups[HEIFERS01 - OFFSET] += cows[0][0];
		// Calculate totals
		// TODO: Update totals!
//System.out.println(Utils.arrayString(cows));
//System.out.println(Utils.arrayString(cowGroups));
		// Remove all steers
		cattleRemoved[STEERS - OFFSET] = cowGroups[STEERS - OFFSET];
		
		updateTotals();
		
		// Log output
		if(params.LOGGING_LIVESTOCK)
			System.out.println(getLog());
	}
	
	/** re-counts cattle quantities after changes in herd */
	private void updateTotals(){
//System.out.println("cowGroups (before)=" + Utils.arrayString(cowGroups));
//System.out.println(Utils.arrayString(cows));
		// Reset count vars
		total = 0;
		int[] toReset = {HEIFERS01, HEIFERS1P, COWS1ST, COWS2ND, COWS3PL, BREEDING, MILKING};
		for(int i : toReset) cowGroups[i - OFFSET] = 0;
		// Count groups
		for(int lac = 0; lac < cows.length; lac++){
			for(int wsc = 0; wsc < cows[lac].length; wsc++){
				// Count total herd size
				total += cows[lac][wsc];
				// Count cows in each group
				cowGroups[getGroup(lac, wsc) - OFFSET] += cows[lac][wsc];
//if(getGroup(lac, wsc) == HEIFERS01) System.out.print(cows[lac][wsc] + " ");
				if((lac == 0 && wsc >= PERIOD_REARING + PERIOD_GESTATION - PERIOD_DRY)
				|| (lac >= 1 && wsc >= PERIOD_WAITING + PERIOD_GESTATION && lac < MAX_LACTATIONS))
					cowGroups[BREEDING - OFFSET] += cows[lac][wsc];
				// Calcualte number of milking cows
				if(lac >= 1 && wsc >= PERIOD_DRY && wsc < PERIOD_DRY + PERIOD_LACTATION)
					cowGroups[MILKING - OFFSET] += cows[lac][wsc];
			}
		}
//System.out.println("cowGroups (after)=" + Utils.arrayString(cowGroups));
	}
	
	/** Counts number of livestock in a perticular subsection of the herd */
	private int count(int lFrom, int lTo, int wFrom, int wTo){
		int total = 0;
		for(int l = lFrom; l <= lTo; l++){
			for(int w = (l == lFrom ? wFrom : 0); w < (l == lTo ? wTo + 1 : cows[l].length); w++){
				total += cows[l][w];
			}
		}
		return total;
	}
	
	/** Removes cattle from the given group 
	 * @param toRemove the group identifier
	 * @param lFrom lactation number to remove from
	 * @param lTo lactation number to remove up to
	 * @param wFrom the week (weeks since calving) to remove from
	 * @param wTo the week (weeks since calving to remove to
	 * @param wInc the week incrament 1 to move up array, -1 to move down
	 * @return the number of cows successfully removed */
	private int removeCattle(int toRemove, int lFrom, int lTo, int wFrom, int wTo, int wInc){
		if(toRemove <= 0) return 0;
		int removed = 0;
		int lInc = lFrom <= lTo ? 1 : -1; 
		for(int l = lFrom; (lInc == 1 && l <= lTo) || (lInc == -1 && l >= lTo); l += lInc){
			int wStart = l == lFrom ? wFrom : wInc == 1 ? 0 : cows[l].length - 1;
			int wEnd   = l == lTo   ? wTo   : wInc == 1 ? cows[l].length - 1: 0;
			for(int w = wStart; (wInc == 1 && w <= wEnd) || (wInc == -1 && w >= wEnd); w += wInc){
				if(toRemove < cows[l][w]){
					removed += toRemove;
					cows[l][w] -= toRemove;
					toRemove = 0;
				}
				else{
					removed += cows[l][w];
					toRemove -= cows[l][w];
					cows[l][w] = 0;
				}
			}
		}
		return removed;
	}

	/** Returns a log of the state of the livestock group as a string */
	public String getLog(){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < COUNTED_GROUPS.length; i++){
			buf.append(Constants.getName(i + OFFSET) + " = " + getTotal(i + OFFSET) + "\n");
		}
		buf.append("\nLivestock: markov model:\n" + Utils.arrayString(cows));
		return buf.toString();
//		"Livestock: " + totalCows + " " + heifers01 + " " + milkingCows + " " + breedingCows + " " + 0 + " " + cowsSold + " " + femaleCalvesSold + " " + steersRemoved + " " + carcasesRemoved + "\n" +
//		"Livestock: "
	}

	/** For testing */
	public static void main(String[] args){
	}

	
	/*	public String getStatHeader(){
		return "Herd"
		+ "\t" + "Heifers"
		+ "\t" + "CowsM"
		+ "\t" + "CowsD"
		+ "\t" + "Bulls"
		+ "\t" + "N Spray"
		+ "  \t" + "N Pasture";
	}
	public String getStats(){
		return "" + (cows.size() + bulls.size())
		+ "\t" + totalHeifers
		+ "\t" + totalCowsMilking
		+ "\t" + totalCowsDry
		+ "\t" + bulls.size()
		+ "\t" + Utils.roundString(totalNitrogen)
		+ "     \t" + Utils.roundString(nitrogenPasture);
	}

	public void print(){

/*		System.out.println(
				"Year: " + time.getYear() + ", Month: " + time.getMonthOfYear()
			+ "\nHeifers:   " + totalHeifers + ", of which pregnant: " + pregHeifers
			+ "\nCows:      " + totalCows    + ", of which pregnant: " + pregCows
			+ "\nBulls:     " + bulls.size()
			+ "\nMilking:   " + milking + ", Dry: " + (totalCows - milking)
			+ "\nMilkYield: " + milkYield
			+ "\nNitrogen:  " + manureN
			+ "\nTotal:     " + cows.size() + "\n");
	 *//*	}*/

}
