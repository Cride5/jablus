package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.field.Pasture;
import uk.co.crider.jablus.utils.Utils;


/** Cow digestion and milk production model, taken from SEPATOU model 
 * Objects of this class don't record any internal state other than
 * the outputs produced as a result of executing execDay */
public class CowDigestion extends CompoundData{
	
	public static final int OFFSET = 1600;
	public static final int FEED_CONC         = OFFSET + 0;
	public static final int FEED_HAY          = OFFSET + 1;
	public static final int FEED_SILAGE_GRASS = OFFSET + 2;
	public static final int FEED_SILAGE_WHEAT = OFFSET + 3;
	public static final int FEED_SILAGE_MAIZE = OFFSET + 4;
	public static final int FEED_GRASS        = OFFSET + 5;
	public static final int TARGET_YIELD      = OFFSET + 6;
	public static final int ACTUAL_YIELD      = OFFSET + 7;
	public static final int SLURRY            = OFFSET + 7;
	public static final int SLURRY_N          = OFFSET + 8;
	
	private Parameters params;

	// Conversion of british ME units to french UFL units (see DeBrabander1982)
	// Energy requirement or maintainence:
	// ME  = 0.091 W + 8.3
	// UFL = 0.006 W + 1.4
	// Equivalent measures for barley: UFL = 1.61, ME = 18.9
	
	// FIXED PARAMETERS
	private static final boolean GOOD_HAY = true; // Whether the hay used is of good quality

	// Ratio of slurry to nitrogen
	public static final double SLURRY_N_RATIO  = 1/3.0; // m³/KgN
	
	// Ratio of dry manure to nitrogen
	public static final double MANURE_N_RATIO = 1000/6.0; // kg/kgN

	/** Fill per kg consumed of concentrates  (UE/kg) */ 
    private static final double BDc = 0.6;
    /** Energetic value for concentrates  (UFL/kg) */
    private static final double EVc = 1.1; 

    
    /** Fill per kg consumed of silage maize  (UE/kg) */
	private static final double BDm = 1; 
    /** Energetic value for maize  (UFL/kg) */
    private static final double EVm = 0.9;
	
    
    /** Fill per kg consumed of hay (UE/kg) */ 
    private static final double BDa = 1;
    /** Energetic value for hay (UFL/kg) */
    private static final double EVa = GOOD_HAY ?  0.8 : 0.6;
    
    // TODO: These are estimates - need to get solid values for these!

    /** Fill per kg consumed of wheat meal (UE/kg) */ 
    private static final double BDg = 1;
    /** Energetic value for wheat meal (UFL/kg) */
    private static final double EVg = 0.9;
    
    /** Fill per kg consumed of grass silage (UE/kg) */ 
    private static final double BDs = 0.3;
    /** Energetic value for grass silage (UFL/kg) */
    private static final double EVs = 0.85;
   
    
    // DYNAMIC INTERNAL VARIABLES
       
    

    private final IntegerData[] offeredConfined;
    /** Returns daily feed budged for confined cows (kg/head/day) */
    public Data0D.Integer[] getOfferedConfined(){
    	return offeredConfined;
    }
    private final IntegerData[] offeredGrazing;
    /** Returns daily feed budged for grazing cows (kg/head/day) */
    public Data0D.Integer[] getOfferedGrazing(){
    	return offeredGrazing;
    }
    private final DoubleData[] consumedConfined;
    /** Returns daily feed consumption for confined cows (kg/head/day) */
    public Data0D.Double[] getConsumedConfined(){
    	return consumedConfined;
    }
    private final DoubleData[] consumedGrazing;
    /** Returns daily feed consumption for grazing cows (kg/head/day) */
    public Data0D.Double[] getConsumedGrazing(){
    	return consumedGrazing;
    }
    private final DoubleData[] surplusConfined;
    /** Returns daily feed surplus for confined cows (kg/head/day) */
    public Data0D.Double[] getSurplusConfined(){
    	return surplusConfined;
    }
    private final DoubleData[] surplusGrazing;
    /** Returns daily feed surplus for grazing cows (kg/head/day) */
    public Data0D.Double[] getSurplusGrazing(){
    	return surplusGrazing;
    }
    /** Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg */
    private final Data0D.Integer targetYield;
    /** Returns the target yield for milking cows (kg/year) */
    public Data0D.Integer getTargetYield(){
    	return targetYield;
    }
    private final Data0D.Integer actualYield;
    /** Returns the actual yield for milking cows (kg/year) */
    public Data0D.Integer getActualYield(){
    	return actualYield;
    }
    private final Data0D.Double slurry;
    /** Returns slurry produced (m³/day) */
    public final Data0D.Double getSlurry(){
    	return slurry;
    }
    private final Data0D.Double manureN;
    /** Returns slurry Nitrogen produced (kg/day) */
    public final Data0D.Double getManureN(){
    	return manureN;
    }
   
    public CowDigestion(Parameters params){
    	super(Constants.DRIVER_COW_DIGESTION);
    	this.params = params;
    	offeredConfined = new IntegerData[]{
    			new IntegerData(FEED_CONC,          params.INIT_OFFERED_CONC,    Constants.UNITS_FEED, 0,  50),
    			new IntegerData(FEED_HAY,           params.INIT_OFFERED_HAY,     Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_GRASS,  params.INIT_OFFERED_GRASS_SILAGE,  Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_WHEAT,  params.INIT_OFFERED_SILAGE_WHEAT,   Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_MAIZE,  params.INIT_OFFERED_SILAGE_MAIZE,   Constants.UNITS_FEED, 0, 100),
    	};
    	offeredGrazing = new IntegerData[]{
    			new IntegerData(FEED_CONC,          params.INIT_OFFERED_CONC,    Constants.UNITS_FEED, 0,  50),
    			new IntegerData(FEED_HAY,           params.INIT_OFFERED_HAY,     Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_GRASS,  params.INIT_OFFERED_GRASS_SILAGE,  Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_WHEAT,  params.INIT_OFFERED_SILAGE_WHEAT,   Constants.UNITS_FEED, 0, 100),
    			new IntegerData(FEED_SILAGE_MAIZE,  params.INIT_OFFERED_SILAGE_MAIZE,   Constants.UNITS_FEED, 0, 100),
    	};
    	consumedConfined = new DoubleData[]{
    			new DoubleData(FEED_CONC,          0,  Constants.UNITS_FEED, 0,  50),
    			new DoubleData(FEED_HAY,           0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_GRASS,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_WHEAT,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_MAIZE,  0,  Constants.UNITS_FEED, 0, 100),
    	};
    	consumedGrazing = new DoubleData[]{
    			new DoubleData(FEED_CONC,          0,  Constants.UNITS_FEED, 0,  50),
    			new DoubleData(FEED_HAY,           0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_GRASS,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_WHEAT,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_MAIZE,  0,  Constants.UNITS_FEED, 0, 100),
    	};
    	surplusConfined = new DoubleData[]{
    			new DoubleData(FEED_CONC,          0,  Constants.UNITS_FEED, 0,  50),
    			new DoubleData(FEED_HAY,           0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_GRASS,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_WHEAT,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_MAIZE,  0,  Constants.UNITS_FEED, 0, 100),
    	};
    	surplusGrazing = new DoubleData[]{
    			new DoubleData(FEED_CONC,          0,  Constants.UNITS_FEED, 0,  50),
    			new DoubleData(FEED_HAY,           0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_GRASS,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_WHEAT,  0,  Constants.UNITS_FEED, 0, 100),
    			new DoubleData(FEED_SILAGE_MAIZE,  0,  Constants.UNITS_FEED, 0, 100),
    	};
    	targetYield = new IntegerData(TARGET_YIELD, params.INIT_TARGET_YIELD, Constants.UNITS_MILK_YIELD, 0, 20000);
    	actualYield = new IntegerData(ACTUAL_YIELD, 0, Constants.UNITS_MILK_YIELD);
    	slurry = new DoubleData(SLURRY);
    	manureN = new DoubleData(SLURRY_N);
    }
    
    /** Resets management parameters to default */
    public void resetRegime(){
    	offeredConfined[0].setValue(params.INIT_OFFERED_CONC);
    	offeredConfined[1].setValue(params.INIT_OFFERED_HAY);
    	offeredConfined[2].setValue(params.INIT_OFFERED_GRASS_SILAGE);
    	offeredConfined[3].setValue(params.INIT_OFFERED_SILAGE_WHEAT);
    	offeredConfined[4].setValue(params.INIT_OFFERED_SILAGE_MAIZE);
    	offeredGrazing[0].setValue(params.INIT_OFFERED_CONC);
    	offeredGrazing[1].setValue(params.INIT_OFFERED_HAY);
    	offeredGrazing[2].setValue(params.INIT_OFFERED_GRASS_SILAGE);
    	offeredGrazing[3].setValue(params.INIT_OFFERED_SILAGE_WHEAT);
    	offeredGrazing[4].setValue(params.INIT_OFFERED_SILAGE_MAIZE);
		targetYield.setValue(params.INIT_TARGET_YIELD);
    }
    
    /** Sets a feed budget parameter */
    public void setBudget(int budget, int feed, int qty){
		switch(budget){
		case DairyAgent.ACTION_FEED_CONFINED :
			offeredConfined[feed].setValue(qty);
System.out.println(Utils.arrayString(offeredConfined));
			break;
		case DairyAgent.ACTION_FEED_GRAZING : 
			offeredGrazing[feed].setValue(qty);
System.out.println(Utils.arrayString(offeredGrazing));
			break;
		case DairyAgent.ACTION_FEED_TYIELD : 
			targetYield.setValue(qty);
System.out.println(targetYield);
			break;
		}
    }
    
    /** Pasture grass model */
//    private Pasture grass;
    
    /** Set the pasture model */
//    public void setPasture(Pasture grass){
 //   	this.grass = grass; }

    
    // OUTPUTS
    
	private double qic;
	/** Amount of concentrates intake (assumed to equal amount offered) */
    double getConcIntake(){ return qic; }
	
	private double qia;
	/** Amount of hay intake (kgFW) */
//	double getHayIntake(){ return qia; }

	private double qis;
	/** Amount of grass silage intake (kgFW) */
//	double getSilageIntake(){ return qis; }

	private double qig;
	/** Amount of wheat meal intake (kgFW) */
//	double getWheatIntake(){ return qig; }

	private double qim;
	/** Amount of maize intake per cow (kgFW) */
//	double getMaizeIntake(){ return qim; }
	
	/** Amount of grazed herbage intake (kg) */
	private double qih;
//	double getGrassIntake(){ return qih; }
	
	/** Returns the amount of intake of the given feed per cow (kg/head/day) */
    public double getIntake(int feed){
    	switch(feed){
    	case FEED_CONC         : return qic;
    	case FEED_HAY          : return qia;
    	case FEED_SILAGE_GRASS : return qis;
    	case FEED_SILAGE_WHEAT : return qig;
    	case FEED_SILAGE_MAIZE : return qim;
    	case FEED_GRASS        : return qih;
    	}
    	return 0;
    }
    
   /** Energy intake per cow (UFL) */
	private double Ei;
//	double getEnergyIntage(){ return Ei; }

	/** Energy availability index 0 = none, 1 = requiriment fulfilled */
	private double Eavail;
	double getEavailability(){ return Eavail; }
	
    /** Actual daily milk yield per cow (kg) */
	private double yr;
	/** @return actual daily milk yield per cow (kg) */
	double getMilkProduced(){ return yr; }
   
	/** Executes a day of the cow digestion model */
	void execDay(
			Pasture grass,
		    double heiferAge, // Age in weeks of the cow if its a heifer - if not heifer this should be 0
		    double nCalving,  // Number of days spend from the calving day                   day
		    boolean milking  // Whether the cow is milking
	){
		
		boolean grazing = false;
		double qoh = 0;
		double qoh_rec = 0;
		double D_rec = 0;
		if(grass != null){
			grazing = grass.isGrazing();
			qoh = grass.getQoh();
//System.out.println("qoh:" + qoh);
			qoh_rec = grass.getQohRec();
			D_rec = grass.getDRec();
		}
		
		if(grazing){
			qic = offeredGrazing[FEED_CONC   - OFFSET].intValue();
			qia = offeredGrazing[FEED_HAY    - OFFSET].intValue();
			qis = offeredGrazing[FEED_SILAGE_GRASS - OFFSET].intValue();
			qig = offeredGrazing[FEED_SILAGE_WHEAT  - OFFSET].intValue();
			qim = offeredGrazing[FEED_SILAGE_MAIZE  - OFFSET].intValue();
//System.out.println("CowDigestion: Grazing cow's feedbudget:");
//for(DoubleData g : offeredGrazing) System.out.println(g);
		}
		else{
			qic = offeredConfined[FEED_CONC   - OFFSET].intValue();
			qia = offeredConfined[FEED_HAY    - OFFSET].intValue();
			qis = offeredConfined[FEED_SILAGE_GRASS - OFFSET].intValue();
			qig = offeredConfined[FEED_SILAGE_WHEAT  - OFFSET].intValue();
			qim = offeredConfined[FEED_SILAGE_MAIZE  - OFFSET].intValue();
//System.out.println("CowDigestion: Confined cow's feedbudget:");
//for(DoubleData g : offeredConfined) System.out.println(g);
		}

		// POTENTIAL MILK YIELD
		double yp = 0;
		if(milking)
			// This lactation curve has exponential decay, but doesn take account of the initial rise
//			yp = targetYield.intValue() * Math.pow(1 - 0.0035, nCalving - 40) / Time.DAYS_YEAR;
			// Adapted from paper by Wood 1967:  Y = At^b * e^(-ct) where A, b and c are empirically fitted
			// See http://www.jstor.org/pss/2348550 for ref to paper...
			// From spreadsheet: =$B$1 * 0.65 * POWER($A23; 0.17) * EXP(-0.0045*$A23)/$B$2
			yp = targetYield.intValue() * 0.65 * Math.pow(nCalving, 0.17) * Math.exp(-0.0045 * nCalving) / Time.DAYS_YEAR;
//System.out.println("yp:" + yp + "\ttarget:" + targetYield.intValue() + "\tNcalving:" + nCalving);


		// DAILY FEED INTAKE OF MAIZE
		// Maximum feeding capacity of the cow
		double BD_max = 22 - 8.25 * Math.exp(-0.02 * yp);
		// If the cow is a heifer, max intake depends on age,
		// assumption is that it increases linearly with age from
		// 5% at age 0 to 100% at the end of rearing period
		if(heiferAge >= 0){
			BD_max = scaleIntake(BD_max, heiferAge);
		}
//System.out.println("BD_max:" + BD_max + "\typ:" + yp);
		
		// Assumption: That cows will eat all concentrates
		// then all remaining offered roughage is eaten
		// in proportion to amount available
		
		// Fill of consumed concentrates
	    double BDic = BDc * qic;
	    // If too much concentrates then intake = BD_max
	    if(BDic > BD_max){
	    	qic *= BD_max / BDic;
	    	BDic = BD_max;
	    }
	    // Fill remaining after concentrates consumed
	    double BD_remain = BD_max - BDic;
	    
	    // Fill of consumed hay
	    double BDia = BDa * qia;
	    // Fill of consumed grass silage
	    double BDis = BDs * qis;
	    // Fill of consumed wheat meal
	    double BDig = BDg * qig;
	    // Fill of consumed maize silage
	    double BDim = BDm * qim;
	    // Fill achieved if all offered roughage are consumed
	    double BD_avail = BDia + BDis + BDig + BDim;
	    // If there's too much food available then intake
	    // of each is proportional to amount offered
	    if(BD_avail > BD_remain){
	    	double propConsumed = BD_remain / BD_avail;
	    	// Scale fill and intake by amount consumed
		    BDia *= propConsumed;
		    BDis *= propConsumed;
		    BDig *= propConsumed;
		    BDim *= propConsumed;
		    qia *= propConsumed;
		    qis *= propConsumed;
		    qig *= propConsumed;
		    qim *= propConsumed;
	    }
	    
//System.out.println("BD_max=" + BD_max + ", BD_remain=" + BD_remain + ", BD_avail=" + BD_avail + ", yp=" + yp + ", BDic=" + BDic + ", BDc=" + BDc + ", qic=" + qic);
//System.out.println("qia=" + qia + ", qis=" + qis + ", qig=" + qig + ", qim=" + qim);
	    // If there is too little available, then fill is either
	    // gained from grazing, or there will be an energy defficiency
	    
		// Energetic value of herbage
	    double EVh = 0;
	    
	    // DAILY INTAKE WHEN NOT GRAZING
	    
		if(!grazing){
/*			// Fill of consumed maize silage, assuming maximal availability
			BDim = BD_max - BDic - BDia;

			// Silage maize intake demand per cow  (kg/day)
			double qim_dem = BDim / BDm;
			
			// If demand is lower than available then there is a surplus
			double qim_surplus = qim < qim_dem ? 0 : qim - qim_dem;
			
			// Actual consumption equals demand if supply allows
			qim = qim > qim_dem ? qim_dem : qim;
			
			
			// Herbage intake is 0 when not grazing
*/			qih = 0;
		}
		// DAILY FEED INTAKE WHEN GRAZING
		else{
				
			// mximum feed fill for grazed herbage
			double BDih_max = BD_max - (BDic + BDia + BDis + BDig + BDim);
			BDih_max = BDih_max < 0 ? 0 : BDih_max;
	
			// Solution to:
		    // BDih_max = ∫ (1.2117 - 0.0033 * Dlayer(q)) dq   from (qoh - qih_max) to qoh
		    // Full function to be integrated is
			// = 1.2117 - 0.0033 * ((29.25 / qoh_rec) * q + D_rec - 13)
			// = -(0.096525 / qoh_rec) * q - 0.0033 * D_rec + 1.2546
		    // and
		    // = 1.2117 - 0.0033 * (D_ds + 6.5) 
		    // = -(0.0033 * D_ds + 0.02145) + 1.2117 
		    // = -0.0033 * D_ds + 1.19025
		    // Indefinite solutions to integrals are:
			// = q * (-0.0482625 * q / qoh_rec - 0.0033 * D_rec + 1.2546) + C
		    // and
			// = q * (-0.0033 * D_ds + 1.19025) + C
		    // Definite solutions from (qoh - qih_max) to qoh are:
		    // = qih_max * ((2 * qoh - qih_max) * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546)
		    // and
		    // = qih_max * (-0.0033 * D_rec + 1.19025)
		    // If these are equal to BDih_max then
		    // BDih_max = qih_max * ((2 * qoh - qih_max) * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546)
			// ... solving in terms of qih_max gives:
			// 0 = (-0.0482625 / qoh_rec) * qih_max ^ 2 + (2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546) * qih_max + BDih_max
			// ... using the quadratic formula, the solution is:
			// qih_max = (
			//    (2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546)
			//     ± Math.sqrt(Math.pow(2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546, 2)
			//                 - (4 * (-0.0482625 / qoh_rec) * BDih_max))
			//    ) / (2 * (-0.0482625 / qoh_rec))
		    // and
		    // BDih_max = qih_max * (-0.0033 * D_rec + 1.19025)
			// qih_max = BDih_max / (-0.0033 * D_rec + 1.19025)
		    // ! seems this is very hard to do !
	
		    // Maximum herbage intake per cow is
			double qih_max;
	        if(qoh <  (2 / 3) * qoh_rec){
	        	double discriminant = Math.pow(2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546, 2) - (4 * (-0.0482625 / qoh_rec) * BDih_max);
	        	// If there's no solution just set it to qoh
	        	if(discriminant < 0){
	        		System.out.println("WARNING: No solutions to integration - forced to use fall-back");
	        		qih_max = qoh;
	        	}
	        	else if(discriminant > 0){
	        		System.out.println("WARNING: Two solutions to integration - assuming positive solution");
	        		qih_max = ((2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546) + Math.sqrt(discriminant)) / (2 * (-0.0482625 / qoh_rec));
	        	}
	        	else{
	        		qih_max = (2 * qoh * (-0.0482625 / qoh_rec) - 0.0033 * D_rec + 1.2546) / (2 * (-0.0482625 / qoh_rec));
	        	}
	        }else{
	        	qih_max = BDih_max / (-0.0033 * D_rec + 1.19025);
	        }
		    
			// Amount of grazed herbage intake      kg
		    if(qoh >= 20)
		    	qih = qih_max;
		    else
		    	qih = qih_max - 0.3 * (20 - qoh);
		    qih = qih < 0 ? 0 : qih;
//System.out.println("qih:" +qih  + "\tqih_max:" + qih_max + "\tqoh:" + qoh);

		    // In vitro digestibility of herbage
		    // Di = (1 / qih) * ∫ Dlayer(q) dq     for (qoh - qih) to qoh
	        // The indefinite integrals of Dlayer(q) are:
	        // ∫ Dlayer(q) = q * (14.625 * q / qoh_rec + D_rec - 13) + C       for q <  (2 / 3) * qoh_rec
	        // and
	        // ∫ Dlayer(q) = q * (D_rec + 6.5) + C                             for q >= (2 / 3) * qoh_rec
		    // The definite integrals of Dlayer(q) from (qoh - qih) to qoh are
		    // = qoh * (14.625 * qoh / qoh_rec + D_rec - 13) - (qoh - qih) * (14.625 * (qoh - qih) / qoh_rec + D_rec - 13)
		    // = qoh^2 * (14.625 / qoh_rec) + qoh * D_rec - qoh * 13
		    // - (qoh^2 + qih^2 - 2 * qoh * qih) * (14.625 / qoh_rec) - qoh * D_rec + qih * D_rec + qoh * 13 - qih * 13
		    // = (2 * qoh * qih - qih^2) * (14.625 / qoh_rec) + qih * D_rec - qih * 13
		    // = qih * ((2 * qoh - qih) * (14.625 / qoh_rec) + D_rec - 13)
		    // and
		    // = qoh * (D_rec + 6.5) - (qoh - qih) * (D_rec + 6.5)
		    // = qih * (D_rec + 6.5)
		    // Applying the factor of (1 / qih) gives:
		    // = (2 * qoh - qih) * (14.625 / qoh_rec) + D_rec - 13
		    // and
		    // = D_rec + 6.5
		    // so:
		    double Di;
		    if(qih <  (2 / 3) * qoh_rec)
		    	Di = (2 * qoh - qih) * (14.625 / qoh_rec) + D_rec - 13;
		    else
		    	Di = D_rec + 6.5;
//System.out.println("Di:" + Di + ", D_rec:" + D_rec + ", qoh:" + qoh + ", qih:" + qih + ", qoh_rec:" + qoh_rec);
		    
		    // In vivo digestibility of herbage
		    double Dvv = 1.39 * Di - 26.0;
		    
		    // Energetic value of herbage
		    EVh =  0.0108 * Dvv + 0.208;
		}
		
	    // MILK YIELD
	    
	    // Energy supplied for each feed
	    double Eic = EVc * qic;
	    double Eia = EVa * qia;
	    double Eis = EVs * qis;
	    double Eig = EVg * qig;
	    double Eim = EVm * qim;
	    double Eih = EVh * qih;
//System.out.println("EVc :" + EVc + ", EVa:" + EVa + ", EVs:" + EVs + ", EVg:" + EVg + ", EVm:" + EVm + ", EVh:" + EVh);
//System.out.println("qic :" + qic + ", qia:" + qia + ", qis:" + qis + ", qig:" + qig + ", qim:" + qim + ", qih:" + qih);
//System.out.println("Eic :" + Eic + ", Eia:" + Eia + ", Eis:" + Eis + ", Eig:" + Eig + ", Eim:" + Eim + ", Eih:" + Eih);
//System.out.println("BDic:" + Utils.round(BDic) + "\tqic:" + Utils.round(qic) + "\tEic:" + Eic);	    
//System.out.println("BDia:" + Utils.round(BDia) + "\tqia:" + Utils.round(qia) + "\tEia:" + Eia);	    
//System.out.println("BDim:" + Utils.round(BDim) + "\tqim:" + Utils.round(qim) + "\tEim:" + Eim);	    
//System.out.println("BDih:" + Utils.round((BD_max - BDic - BDia - BDim)) + "\tqih:" + Utils.round(qih) + "\tEih:" + Eih);	    

	    // The total available energy per cow
	    Ei = Eic + Eia + Eis + Eig + Eim + Eih;
	    
	    double Emaintenance = grazing ? 6 : 5;
	    
	    // Energy available for milk production
	    double Eproduction = Ei - Emaintenance;
	    
	    // Milk produced per cow per day
	    yr = Math.min(Eproduction / 0.44, yp);
	    yr = yr < 0 ? 0 : yr;
	    
	    // ENERGY STRESS
	    
	    // Energy availabilty index per cow
	    Eavail = Emaintenance > 0 ? Ei / Emaintenance : 1;
	    Eavail = Eavail > 1 ? 1 : Eavail;
//System.out.println("yp=" + yp + ", yr=" + yr + ", Ei=" + Ei + ", Eavail=" + Eavail);
		
		// Daily slurry production (in m³)
		// Manure Production (from DynoFlo)
		// For heifers
		double scaleVal = 0.8; // TODO: This model seems to over estimate production
                               // this value scales it down to a resonable value
		if(heiferAge >= 0){
			double monthsAge = (double)heiferAge * Time.MONTHS_YEAR / Time.WEEKS_YEAR;
			manureN.setValue(((90 + monthsAge * 40) * 0.31) * 1E-3 * scaleVal);
//			slurry.setValue(((90 + monthsAge * 40) * 85) * 1E-3 * scaleVal);
		}
		// For dry cows
		else if(!milking){
			manureN.setValue(0.364 * scaleVal);			
//			slurry.setValue(80 * scaleVal);
		}
		// For milking cows
		else{
			manureN.setValue((-0.0000003 * Math.pow(yr, 3) + 0.00006 * Math.pow(yr, 2) + 0.0024 * yr + 0.364) * scaleVal);
//			slurry.setValue((-0.0003 * Math.pow(yr, 3) + 0.0525 * Math.pow(yr, 2) - 1.3708 * yr + 98.75) * scaleVal);
		}
		slurry.setValue(manureN.doubleValue() * SLURRY_N_RATIO);
//System.out.println("heiferAge=" + heiferAge + ", milking=" + milking + ", milk=" + Utils.roundString(yr) + ", slurry=" + slurry + ", manureN=" + manureN);
	    
	}

	// Scales demand of younger stock according to age (assumed to be proportional to weight)
	public static final double scaleIntake(double intake, double age){
		if(age < 0) return intake;
		double pi = 0.05; // proportion of intake at beginning of rearing period
		if(age < Livestock.PERIOD_REARING)
			intake *= pi + ((1 - pi) * age / Livestock.PERIOD_REARING);
		return intake;
	}
	
}
