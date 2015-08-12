package uk.co.crider.models.dairy.crop_2;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.util.Random;

/** Pasture growth model, with leaf/growth and animal aspects based on CowDigestion and soil
 * nitrogen and water aspects based on STICS */
public class Pasture extends Crop {
	
	// Symbols
	// PET             Daily potential evapo-transpiration mm  0–
	// Rain           Daily rainfall           mm  0–
	// WI(d)           Water index               0–1
	// ad             Physiological factor involved in RUE computation
	// al               Seasonal factor involved in RUE computation
	// b              Temperature factor involved in RUE computation
	// Au(d)                          Useful area of a plot                      ha       0–20
	// D(d)                      Average herbage digestibility              g⋅(100 g) –1   40–100
	// Dτ(d)         Decrease of herbage digestibility due to temperature   g⋅(100 g) –1   40–100
	// Dcg(d)          Average herbage digestibility after a defoliation    g⋅(100 g) –1   40–100
	// Dlayer(q)      Herbage digestibility per layer as fraction of mass   g⋅(100 g) –1   40–100
	// Dmax                       Maximum leaf digestibility                g⋅(100 g) –1
	// DM(d)                     Standing herbage dry matter                   g⋅m–2     0–1500
	// DMi(d)                           Herbage intake                         g⋅m–2       0–
	// DMr(d)                           Herbage growth                         g⋅m–2      0–50
	// DMs(d)                         Herbage senescence                       g⋅m–2      0–50
	// K_sward        Constant reflecting the vertical structure of canopy
	// LAI(d)                           Leaf area index                                   0–15
	// LAIr(d)            Increase of leaf area index due to growth                       0–15
	// LAIs(d)          Decrease of leaf area index due to senescence                     0–15
	// LLSmax                Leaf lifespan w.r.t end of senescence          degree days
	// LLSmin                 Leaf lifespan w.r.t end of green life         degree days
	// Ncows                      Number of cows in the mob                              5–300
	// Ndungs(d)                   Number of dungs per cow
	// NI,                          Herbage nitrogen index                               0–100
	// PAR(d)                Intercepted photosynthetic radiation             MJ⋅m–2
	// RG,                       Daily incident radiation                     MJ⋅m–2
	// RUE(d)                       Radiation use efficiency                  g⋅MJ–2
	// T,                          Average daily temperature                      °C
	// WI(d)                              Water index                                      0–1
	// dayBG(d)             The first day in the last grazing episode
	// dayCG(d)       The last day of the last defoliation (cut or grazing)
	// dayDMleft(d)  The day whose DM is taken as reference to compute the daily decrease of DM
	// dayLAIleft(d) The day whose LAI is taken as reference to compute the daily decrease of LAI
	// εi (d)                   Intercepted radiation efficiency
	// qih                Grazed herbage intake per day and per cow               kg
	// q oh                         Offered herbage per cow                       kg      0–2000

    // BDic         Fill of a given amount of concentrates intake               UE
    // BDia         Fill of a given amount of hay intake                        UE
    // BDim         Fill of a given amount of maize silage intake               UE
    // BDa          Fill of hay per kg                                        UE.kg–1
    // BDc          Fill of concentrate per kg                                UE.kg–1
    // BDm          Fill of maize silage per kg                               UE.kg–1
    // BDmax        Maximum feeding capacity of a cow                           UE
    // BDih_max     Maximum fill available for grazed herbage intake            UE
    // Dlayer       Herbage digestibility per layer as fraction of mass     g⋅(100 g)–1
    // Di           Average in vitro digestibility of grass intake          g⋅(100 g)–1
    // Dvv          Average in vivo digestibility of grass intake           g⋅(100 g)–1
    // Ei           Energy intake per cow                                       UFL
    // Eic          Energy from the concentrates intake                         UFL
    // Eia          Energy from the hay intake                                  UFL
    // Eih          Energy from the grazed herbage intake                       UFL
    // Eim          Energy from maize silage intake                             UFL
    // Emaintenance Energy needed for maintenance                               UFL
    // Eproduction  Energy needed for milk production                           UFL
    // EVih         Energetic value of grazed herbage                        UFL.kg–1
    // EVa          Energetic value of hay                                   UFL.kg–1
    // EVc          Energetic value of concentrates                          UFL.kg–1
    // EVm          Energetic value of maize silage                          UFL.kg–1
    // Ncalving     Number of days spend from the calving day                   day
    // qic          Amount of concentrates intake                               kg
    // qia          Amount of hay intake                                        kg
    // qim          Amount of maize intake (required if grazing, calculated if not)    kg
    // qih          Amount of grazed herbage intake                             kg
    // qih_max      Maximum amount of herbage intake                            kg
    // qoh          Amount of offered herbage per cow                           kg     0–2000
    // yp           Daily potential milk yield                                  kg
    // yp_max       Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
    // yr           Actual daily milk yield per cow                             kg
    // good_hay     Whether the hay used is of good quality

	private Soil soil; // Soil model 
	private int Ncows; // Ncows                      Number of cows in the mob                              5–300
	
	// Management actions
	public static final int ACTION_NONE        = 0;
	public static final int ACTION_START_GRAZE = 1;
	public static final int ACTION_STOP_GRAZE  = 2;
	public static final int ACTION_CUT_GRASS   = 3;
	
	// Grass species
	public static final int SPECIES_COCKSFOOT = 0;
	public static final int SPECIES_RYE_GRASS = 1;
	public static final int SPECIES_FESCUE    = 2;
	
	// TODO: Difficult to find info on root depths, a paper entitled:
	// Matching Irrigation to Turfgrass Root Depth 
	// http://ohric.ucdavis.edu/Newsltr/CTC/ctcv35_1234.pdf
	// has some info relating to ryegrass and fescue, but not accurate
	private final float[][] GRASS_SPECIES = {
	//  Leaf lifespan (in °C.days)       Maximum leaf      Constant reflecting        Average
	// for beginning (LLSmin) and       digestibility          the vertical            Root
	// complete (LLSmax) senescence    in g⋅(100 g)–1     structure of the canopy     Depth
	//   LLSmin  LLSmax                   Dmax                    K_sward                        Species
	{     600,    800,                     90,                    1.0f,                30  }, // Cocksfoot
	{     500,    700,                     95,                    1.2f,                30  }, // Perennial rye grass
	{     700,    900,                     85,                    1.1f,                60  }, // Tall fescue
	};

	// Fixed variables
	public static final float GRASS_RES_CARBON = 0.5f; // Wheat residues carbon porportion      gC/g
	
	// Temporal variables
	private int d;        // day of year

	// Run-specific variables
	private final int species;  // Grass species (defined above)
//	private final float SWC;    // Soil water capacity       mm 0–200
	private final float A; // Area of a plot                          ha      0.5–20

	public Pasture(
		Soil soil, // Soil model
		int dStart, // Day of the year to start at
		int species, // Grass species
//		float SWC, // Soil water capacity
		float A // Area of a plot        
	){
		this.soil = soil;
		this.d = dStart;
		this.species = species;
//		this.SWC = SWC;
		this.A = A;
		
		// Initialise CowDigestion state vars
		DM = 125;
		LAI = GRASS_SPECIES[species][3] * 1.9 * Math.pow(0.01 * DM, 0.73);
		DM_rec = DM;
		DM_rec2 = DM;
		LAI_rec = LAI;
		qoh_rec = 10 * (DM - 80) * A / (Ncows <= 0 ? 1 : Ncows);
		D_rec = 0;
		
		// Initialise STICS vars
		ZRAC = PROFSEM; // Sewing depth of 1cm
		LRAC = new double[soil.layers];
		
		initSpeciesParams();
	}
	
	// Parameters to drive the soil submodel
	protected void initSpeciesParams(){

		// Shoot growth parameters
		// Values of 0.5 for grass seem sensible, see:
		// The distribution of leaf photosynthetic activity in a mixed grass-legume pasture canopy
		// http://www.springerlink.com/content/mp65805x5142r823/
		EXTIN = 0.5f; //      extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension

		// TODO: These have been stolen from the wheat parameters!
		TCMIN = 0; //      minimum temperature of growth                                       OC
		TCOPT = 15; //      optimum temperature of growth                                       OC

		// Water parameters
		KMAX =  1.2f;  //     maxiinum crop coefficient for water requirements                  sans dimension
		PSISTO = 15; //      absolute value of the potential of stomatal closing               bars
		PSITURG = 4; //     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars

		// Nitrogen Module
		ADIL = 5.35f; //        parameter of the critical curve of nitrogen needs              gN g dry matter-]
		BDIL = 0.442f; //        parameter of the critical curve of nitrogeii needs             sans dimension
		ADILMAX = 8.47f; //    parameter of the maximum curve of nitrogen needs               gN g dry matter'
		BDILMAX = 0.44f; //    Parameter of the maximum curve of nitrogen needs               sans dimension
		VMAX1 = 0.0018f;   //    rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
		VMAX2 = 50;   //    rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
		KMABS1 = 0.050f;  //    constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
		KMABS2 = 25000;  //    constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'

		// Root growth module
		ZPENTE = 100; //      depth where the root density is 112 of the surface root density for the reference profile
		ZPRLIM = 160; //      maximum depth of the root profile for the reference profile
		CROIRAC = 0.12f; //     growth rate of the root front                                  cm degree-day-'
	}
	
	// Management inputs
	public void setCows(int Ncows){ this.Ncows = Ncows; }
	
	// Values for the soil submodel
	double getLAI(){ return LAI; }
	double getMASEC(){ return DM * 0.01; } // Convsrsion from g/m² to t/ha
	// This is for the benefit of the nitrogen module to calculate uptake
	// so desired growth is what is needed here, rather than actual growth
	double getDLTAMS(){ return DMr_unlimited * 0.01; } // Convsrsion from g/m² to t/ha
	
	// Temporally dependent stock variables
 	double AW;
	double AET;
	double DM;
	double DMr_unlimited; // Growth if not limited by nitrogen
    double DM_rec; // DM recorded at last defoliation or after one 'green leaf period'
    double DM_rec2; // Dm recorded at last defoliation
    double DM_grs; // DM recorded at beginning of grazing period
	double DMs_grs;
	double DMr_cum;
	double DMs_cum;
	double LAI;
    double LAI_rec; // LAI recorded at last defoliation or after one 'green leaf period'
    double LAIr;
    double LAIs;
    double DD_cum;  // Cumulative degree days since last defoliation (add temp each day)
    double DD_cum2;
    double D;       // Average digestability of herbage
    double Dl;		// Digestability as it varies down the layers
	double D_rec;   // Digestability recorded at the end of a grazing period
	double getDRec(){ return D_rec; }
	double qoh; // Amount of offered herbage per cow                           kg     0–2000
	double getQoh(){ return qoh; }
	double qoh_rec; // Offered herbage mass on the last defoliation day
	double getQohRec(){ return qoh_rec; }
	int Ndungs;
	boolean grazing; // Weather grazing is currently in action
	boolean isGrazing(){ return grazing; }
	void execDay(
		int action, // Action to carry out
//		double PET,   // Daily potential evapo-transpiration mm  0–
//		double Rain, // Daily rainfall           mm  0–
//		double NI, // Herbage nitrogen index                               0–100
		double RG, // Daily incident radiation                     MJ⋅m–2
		double T, // Average daily temperature                      °C
		double qih //            Grazed herbage intake per day and per cow               kg
	){
//System.out.println("\nd:" + d + "\tDM:" + DM);
		// Grow roots
		growRoots();
		
		// Carry out actions
		if(action == ACTION_START_GRAZE){
			grazing = true;	
			// Record DM before grazing takes place
			DM_grs = DM;
		}
		if(action == ACTION_STOP_GRAZE){
			grazing = false;
	    	D_rec   = D; // Digestability recorded at the end of a grazing period
		}
        // At end of defoliation record values for DM, LAI etc, and
		// reset cumulative totals back to 0
        if(action == ACTION_STOP_GRAZE  || action == ACTION_CUT_GRASS){
        	DM_rec = DM;
        	DM_rec2 = DM;
        	LAI_rec = LAI;
    		qoh_rec = qoh;
        	DD_cum = 0;
        	DMr_cum = 0;
        	DMs_cum = 0;
       }
		// Season: day (25 to 210)
//		int dSeasonStart = 25;
//		int dSeasonEnd = 210;
		
		// Constants
		float LLSmin  = GRASS_SPECIES[species][0];
		float LLSmax  = GRASS_SPECIES[species][1];
		float Dmax    = GRASS_SPECIES[species][2];
		float K_sward = GRASS_SPECIES[species][3];
	
		// SOIL SUBMODEL
		// Original CowDigestion values
/*		if(false){
			// AW(d)         Available water         mm 0–250
			AW = Math.min(Rain + AW - AET, SWC);
			
			// AET(d)   Actual evapo-transpiration   mm  0–
			AET = Math.min(PET, AW);
			
			// WI Water index is ratio between AET and PET
			// Used in LAI calculation below
			WI = AET / PET;
		}
		// Values from STICS soil submodel
		else{*/
		double WI =  (soil.getSWFAC() + soil.getTURFAC()) / 2;
		double NI = soil.getINNS() * 100;
//System.out.println("WI:" + WI + "\tNI:" + NI);
		
		
		
		// PLANT SUBMODEL
		
		// Previous DM value
		double DM_ = DM;
		// Herbage Growth
		//double DMs = 0;
		// Amount of herbage offered by paddok per cow
		//double qoh = 0;
		
		// HERBAGE GROWTH REGARDLESS OF GRAZING STATE
		
        // µ Varies according to the sun's angle 
        // about 0.57 in may, lower closer to summer 
		double µ = 0.52; // TODO: model sun's angle
        double εi = 0.95 * (1 - Math.exp(-µ * LAI));
        
        // Intercepted photosynthetic radiaion (PAR) is dependent on
        // Incident radiation RG and LAI
        double PAR = 0.48 * εi * RG;
//System.out.println("PAR:" + PAR + "\tεi:" + εi + "\t RG:" + RG);

        
        // For the period before stem elongation
        // TODO: At what time is ad = 1.1??
        // double ad = 1.1;
        
        // ... then for vegetative regrowths
        double ad = 1;
        	
        // Seasonal factor for RUE
        double al = -(0.6 / 180) * d + 2.5 + 32 * (0.6 / 180);
        
        // Effect of temperature of photosynthesis
        double b = 0.037 + 0.09 * Math.min(T, 18) - 0.0022 * Math.pow(Math.min(T, 18), 2);
        
        // Radiation use efficiency
        double RUE_unlimited = ad * al * b * 2.2 * WI;
        double RUE = RUE_unlimited * NI;
//      System.out.println("RUE:" + RUE + "\tal:" + al + "\tb:" + b + "\tNI:" + NI + "\tWI:" + WI);
        
        // Herbage growth is a function of intercepted radiation and radiation use efficiency
        double DMr = RUE * PAR;
        DMr = DMr < 0 ? 0 : DMr > 50 ? 50 : DMr;
        DMr_unlimited = RUE_unlimited * PAR;
        DMr_unlimited = DMr_unlimited < 0 ? 0 : DMr_unlimited > 50 ? 50 : DMr_unlimited;
//System.out.println("\tDMr:" + DMr + "\tRUE:" + RUE + "\tPAR:" + PAR);
        		
        // Herbage sensenence
        double DMs = 0.85 * DM_rec * Math.min(T, 18) / LLSmin;
        DMs = DMs < 0 ? 0 : DMs > 50 ? 50 : DMs;

		// Usable area of pasture (reduced by cowpats :-)
        double Au = A - Ncows * Ndungs * 7E-6;

        // Amount of herbage offered by paddok per cow
        qoh = 10 * (DM - 80) * Au / (Ncows <= 0 ? 1 : Ncows);
        qoh = qoh < 0 ? 0 : qoh;
//System.out.println("qoh:" + qoh + "\tDM:" + DM + "\tAu:" + Au + "\tA:" + A);    

       // HERBAGE DYNAMICS WITH GRAZING AND CUTS
        if(grazing){

	        // Rate of defoliation due to grazing
	        double DMi = (qih * Ncows * 0.1) / Au;

	        // Record DM sensenence at beginning of grazing period
	        if(action == ACTION_START_GRAZE)
	        	DMs_grs = DMs;

	        // Rate of sensenange relates to sensenance rate at beginning of grazing period 
	        DMs = DMs_grs;
	        
	        DM_ = DM;
	        // Change in DM when grazing is occuring
	        DM += DMr - DMs - DMi;
	        
//System.out.println("DMi:" + DMi);
       }		
	    // HERBAGE DYNAMICS IN ABSENCE OF GRAZING
		else{

			// Herbage accumulation is product of growth and sensenance
	        DM += DMr - DMs;

		}
        DM = DM < 0 ? 0 : DM > 1500 ? 1500 : DM;
        
        // Add dying leaf to residue stock
        // Conversion from g/m² to kgC/ha
//        soil.depositResidue(DMs * GRASS_RES_CARBON * 10);
//System.out.println("DM:" + DM + "\tDMr:" + DMr + "\t DMs:" + DMs);
        
        // LEAF GROWTH DYNAMICS 
        if(grazing){
			// Value of LAI immediatly after a defoliation
			LAI = DM_ == 0 ? 0 : K_sward * 1.9 * Math.pow(0.01 * DM, 0.73) * (DM / DM_);
//System.out.println("LAI:" + LAI + "\tK_sward:" + K_sward + "\tDM:" + DM + "\tDM_:" + DM_);
		}
        else{
	        
	        // LAI Increase as a result of new growth
        	// TODO: instead of simply adding growth, I've added a logistic curve
        	// which results slow initial growth, and final growth. Crucially
        	// it doesnt allow LAI to shrink below 0! Ideas from Parsons2001
        	// Instead of estimating sensenence rates directly, the logistic
        	// curve enforces greater sensenence as the plant gets bigger
        	double dLAI = LAIr - LAIs;
			LAI += dLAI;
//System.out.println("LAI:" + LAI + "\tLAIr:" +LAIr  + "\tLAIs:" + LAIs);
				
	        // Increase in LAI
	        // upper temperature threshold of 18 degrees for leaf growth 
			// TODO: The coeficient in paper is 11E-6, but this results
			// in painfully slow growth, I think it's supposed to be 11E-5
	        LAIr = 11E-5 * Math.pow((Math.min(T, 18)), 2) * (NI - 20) * WI;
	        
	        // Degrease in LAI - sensenence
	        LAIs = LAI_rec * Math.min(T, 18) / LLSmin;	        
        }
        LAI = LAI < 0 ? 0 : LAI > 15 ? 15 : LAI;
        LAIr = LAIr < 0 ? 0 : LAIr > 15 ? 15 : LAIr;
        LAIs = LAIs < 0 ? 0 : LAIs > 15 ? 15 : LAIs;

		// Set DM and LAI after a grass cut
        if(action == ACTION_CUT_GRASS){
	        // Amount of dm remaining after a grass cut
	        DM = 125;
			// Initial value of the LAI at the end of winter or after a cut
			LAI = K_sward * 1.9 * Math.pow(0.01 * DM, 0.73);
		}
       

        // DIGESTABILITY OF HERBAGE
        
        // The daily decrease in herbage digestibility due to temperature (Dτ_)) is empirically approximated by:
        // if(30 < d && d < 210)
        double Dτ = 0.0588 * d - 0.5280;
//System.out.println("Dτ:" + Dτ);
        // After a defoliation finishing at dayCG(d),
        // the average digestibility of the new leaves produced is:
        double Dcg = (Dmax - Dτ) * (0.9 + 0.001 * NI) - 0.04 * DMr_cum;
        Dcg = Dcg < 0 ? 0 : Dcg;
//System.out.println("Dcg:" + Dcg);

         
        // residual mass that is still available at day d
        double DMs_res = DM_rec2 - DMs_cum;
//System.out.println("DMs_res:" + DMs_res + "\tDM_rec2:" + DM_rec2 + "\tDMs_cum:" + DMs_cum);

        // Average digestability of the offered herbage
        D = (DMr_cum + DMs_res) == 0 ? (Dcg * DMr_cum + D_rec * DMs_res) :
        	(Dcg * DMr_cum + D_rec * DMs_res) / (DMr_cum + DMs_res);
//System.out.println("D:" + D + "\tDcg:" + Dcg + "\tDMr_cum:" + DMr_cum + "\tD_rec:" + D_rec + "\tDMs_res:" + DMs_res + "\tDM_rec2:" + DM_rec2);
        D = D < 0 ? 0 : D;

        // D(q) expressed as an integral of Dlayer(q) with respect to q is:
        // D(q) = (1 / qoh) * ∫ Dlayer(q) dq
        // Dlayer(q) is defined as follows:
        // Dlayer(q) = (29.25 / qoh_rec) * q + D_rec - 13     for q <  (2 / 3) * qoh_rec
        // and
        // Dlayer(q) = D_rec + 6.5                            for q >= (2 / 3) * qoh_rec
        // The indefinite integrals can be expressed as:
        // ∫ Dlayer(q) = (29.25 * q^2) / (2 * qoh_rec) + D_rec * q - 13 * q + C
        //             = 14.625 * q^2 / qoh_rec + D_rec * q - 13 * q + C
        //             = q * (14.625 * q / qoh_rec + D_rec - 13) + C
        // and
        // ∫ Dlayer(q) = D_rec * q + 6.5 * q + C
        //             = q * (D_rec + 6.5) + C
        // The definite integrals from 0 to qoh are then:
        // = qoh * (14.625 * qoh / qoh_rec + D_rec - 13)
        // and
        // = qoh * (D_rec + 6.5)
        // ... so the final value of D(q) including the (1 / qoh) factor is:
        // = 14.625 * qoh / qoh_rec + D_rec - 13
        // and
        // = D_rec + 6.5
        // so:
        if(qoh <  qoh_rec * 2 / 3)
        	Dl = 14.625 * qoh / qoh_rec + D_rec - 13;
        else
        	Dl = D_rec + 6.5;
        Dl = Dl < 0 ? 0 : Dl;

        
        // Add to cumulative degree days
        if(DD_cum < LLSmin){
            // Cumulative degree days since last defoliation (add temp each day)
        	// T-range limited to: 0 <= T <= 18
            DD_cum += T < 0 ? 0 : T > 18 ? 18 : T;
            // If this addition tips cumulative degree days past LLS_min threshold
            // then one 'leaf green life period' has passed, so set LAI_rec
            if(DD_cum >= LLSmin){
        		LAI_rec = LAI;
        		DM_rec = DM;
            }
        }
        // Add to cumulative growth 
    	DMr_cum += DMr;
    	// Add to cumulative senesence
    	DMs_cum += DMs;

       
       // Next day
        d = d >= 365 ? 0 : d + 1;        
	}
		
	
	// CUMLRACZ    sum of the effective root lengths                              cm root cm-2 soi1
	// ZRAC        root depth
	// LRAC (Z)    cffective root density in the layer Z                             cm root cm-' soi1
	double ZRAC = 0;
	double getZRAC(){ return ZRAC; }
	double CUMLRACZ = 0;
	double getCUMLRACZ(){ return CUMLRACZ; }
	double[] LRAC;
	double[] getLRAC(){ return LRAC; }
	// Root growth is related to water availability and
	// soil temperature
	private void growRoots(){
		float RTmax   = GRASS_SPECIES[species][4];
		// If fully grown, no need to continue
		if(ZRAC >= RTmax) return;
		int PFZ = soil.getPFZ((int)ZRAC);
		double TCULT = soil.getTCULT();		
		double DELTAZ = CROIRAC * (Math.min(TCULT, TCOPT) - TCMIN) * PFZ;
//System.out.println("DELTAZ:" + DELTAZ + "\tCROIRA:" + CROIRAC + "\tTCULT:" + TCULT + "\tTCOPT:" + TCOPT + "\tTCMIN:" + TCMIN + "\tPFZ:" + PFZ + "\tZRAC:" + ZRAC);
		ZRAC += DELTAZ;
		ZRAC = ZRAC > soil.layers - 1 ? soil.layers - 1 : ZRAC > RTmax ? RTmax : ZRAC;
//System.out.println("ZRAC:" + ZRAC + " DELTAZ:" + DELTAZ + " PFZ:" + PFZ);
		CUMLRACZ = 0;
		for(int Z = 0; Z < ZRAC; Z++){
			// Eqution 18
			double S = -4.6 / (ZLABOUR - ZPENTE);
			double ZDEMI = Math.max(ZRAC - ZPRLIM + ZPENTE, 1.4 / S);
			LRAC[Z] = LVOPT / (1 + Math.exp(-S * (-Z - ZDEMI)));
			CUMLRACZ += LRAC[Z];
		}
	}
	
	/** Print one-line representing pasture state */
	public void printState(){
		System.out.println(
				"PASTURE" +
				"\tDay: " + d + 
				"\tDM(t/ha):"   + Utils.roundString(getMASEC()) +
				"\tLAI:"        + Utils.roundString(getLAI()) +
				"\tGrowth:"     + Utils.roundString(getDLTAMS()) +
				"\tOffered DM:" + Utils.roundString(getQoh() * 0.01)
		);
	}
	/** Print layered representation of plant state */
	public void printLayers(){ printLayers(15); }
	public void printLayers(int height){
		int h = (int)(DM * (double)height / 1500) + 1;
		for(int i = 0; i < height - h; i++) System.out.println();
		if(LAI > 0){
			for(int i = 0; i < h; i++){
				int l = (int)(LAI * 2);
				for(int j = 0; j < 66; j++)
					System.out.print(j == 33 ? "#" : i % 2 == 1 || j < 33 - l || j > 33 + l ? " " : "~");
				System.out.println();
			}
		}
		else System.out.println();		
	}
}
