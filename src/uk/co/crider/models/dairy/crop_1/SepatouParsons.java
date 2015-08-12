package uk.co.crider.models.dairy.crop_1;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

/** This uses the model in CowDigestion, but simulates the effects of mass and LAI sesenence via Parson's logistic curve, rather thatn CowDigestion's system */
public class SepatouParsons {
	
	// Factor used to calibrate growth functions to reflect CowDigestion
	private static final float GF = 6f;
	private static final int DM_MAX = 1500;
	private static final int LAI_MAX = 15;

	
	// Management actions
	public static final int ACTION_NONE        = 0;
	public static final int ACTION_START_GRAZE = 1;
	public static final int ACTION_STOP_GRAZE  = 2;
	public static final int ACTION_CUT_GRASS   = 3;
	
	// Grass species
	public static final int SPECIES_COCKSFOOT = 0;
	public static final int SPECIES_RYE_GRASS = 1;
	public static final int SPECIES_FESCUE    = 2;

	private final float[][] GRASS_SPECIES = {
	//  Leaf lifespan (in °C.days)       Maximum leaf      Constant reflecting
	// for beginning (LLSmin) and       digestibility          the vertical
	// complete (LLSmax) senescence    in g⋅(100 g)–1     structure of the canopy
	//   LLSmin  LLSmax                   Dmax                    K_sward               Species
	{     600,    800,                     90,                    1.0f            }, // Cocksfoot
	{     500,    700,                     95,                    1.2f            }, // Perennial rye grass
	{     700,    900,                     85,                    1.1f            }, // Tall fescue
	};

	int d;        // day of year
	
	// Run-specific variables
	int species;  // Grass species (defined above)
	int Ncows; // Ncows                      Number of cows in the mob                              5–300
	float SWC;    // Soil water capacity       mm 0–200
	float A; // Area of a plot                          ha      0.5–20
	public SepatouParsons(
			int dStart, // Day of the year to start at
			int species, // Grass species
			int Ncows, // Number of cows in the mob  
			float SWC, // Soil water capacity
			float A // Area of a plot        
		){
		this.d = dStart;
		this.species = species;
		this.Ncows = Ncows;
		this.SWC = SWC;
		this.A = A;
		
		// Initialise state vars
		DM = 125;
		LAI = GRASS_SPECIES[species][3] * 1.9 * Math.pow(0.01 * DM, 0.73);
		DM_rec = DM;
		LAI_rec = LAI;
	}
	
//	LinkedList<Double> LAI_ = new LinkedList<Double>();
//	LinkedList<Double> DM_ = new LinkedList<Double>();
//	LinkedList<Float> T_ = new LinkedList<Float>();
	
 	double AW;
	double AET;
	double DM;
    double DM_rec; // DM recorded at last defoliation or after one 'green leaf period'
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
    double D;       // Digestability of herbage
	double D_rec;   // Digestability recorded at the end of a grazing period
	double qoh_rec; // Offered herbage mass on the last defoliation day
	int Ndungs;
	boolean grazing; // Weather grazing is currently in action
	void grassDay(
		int action, // Action to carry out
		float PET,   // Daily potential evapo-transpiration mm  0–
		float Rain, // Daily rainfall           mm  0–
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
		float NI, // Herbage nitrogen index                               0–100
		// PAR(d)                Intercepted photosynthetic radiation             MJ⋅m–2
		float RG, // Daily incident radiation                     MJ⋅m–2
		// RUE(d)                       Radiation use efficiency                  g⋅MJ–2
		float T, // Average daily temperature                      °C
		// WI(d)                              Water index                                      0–1
		// dayBG(d)            The first day in the last grazing episode
		// dayCG(d)       The last day of the last defoliation (cut or grazing)
		// dayDMleft(d)  The day whose DM is taken as reference to compute the daily decrease of DM
		// dayLAIleft(d) The day whose LAI is taken as reference to compute the daily decrease of LAI
		// εi (d)                   Intercepted radiation efficiency
		double qih //            Grazed herbage intake per day and per cow               kg
		// q oh                     Offered herbage per cow                       kg      0–2000
	){
System.out.println("\nd:" + d);
		// Obey actions
		if(action == ACTION_START_GRAZE){
			grazing = true;	
			// Record DM before grazing takes place
			DM_grs = DM;
		}
		if(action == ACTION_STOP_GRAZE){
			grazing = false;
		}
		// Season: day (25 to 210)
//		int dSeasonStart = 25;
//		int dSeasonEnd = 210;
		
		// Constants
		float LLSmin  = GRASS_SPECIES[species][0];
		float LLSmax  = GRASS_SPECIES[species][1];
		float Dmax    = GRASS_SPECIES[species][2];
		float K_sward = GRASS_SPECIES[species][3];

/*		if(action == ACTION_STOP_GRAZE || action == ACTION_CUT_GRASS){
			LAI_.clear();
			T_.clear();
			DD_cum2 = 0;
		}
		if(!grazing){
			LAI_.add(LAI);
			DM_.add(DM);
			T_.add(T);
			DD_cum2 += T < 0 ? 0 : T > 18 ? 18 : T;
			// Shave head of list so that cumulative degree days is <= LLSmin
			while(DD_cum2 > LLSmin){
				DD_cum2 -= T_.removeFirst();
				LAI_.removeFirst();
				DM_.removeFirst();
			}
		}
*/		
		// SOIL SUBMODEL
		
		// AW(d)         Available water         mm 0–250
		AW = Math.min(Rain + AW - AET, SWC);
		
		// AET(d)   Actual evapo-transpiration   mm  0–
		AET = Math.min(PET, AW);
		
		// WI Water index is ratio between AET and PET
		// Used in LAI calculation below
		double WI = AET / PET;
		
		
		// PLANT SUBMODEL
		
		// Previous DM value
		double DM_ = DM;
		// Herbage Growth
//		double DMs = 0;
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
        
//System.out.println("εi:" + εi + "\t RG:" + RG);

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
        double RUE = ad * al * b * 2.2 * NI * WI;

//System.out.println("al:" + al + "\t b:" + b + "\t NI:" + NI + "\t WI:" + WI);
       
        // Herbage growth is a function of intercepted radiation and radiation use efficiency
        double DMr = RUE * PAR;
        DMr = DMr < 0 ? 0 : DMr > 50 ? 50 : DMr;

        // Herbage sensenence
        double DMs = 0.85 * DM_rec * Math.min(T, 18) / LLSmin;
        DMs = DMs < 0 ? 0 : DMs > 50 ? 50 : DMs;

		// Change in DM from grazing as a percentace of previous DM
		double DMch = 0;
		
        // HERBAGE DYNAMICS WITH GRAZING AND CUTS
        if(grazing){

			// Usable area of pasture (reduced by cowpats :-)
	        double Au = A - Ncows * Ndungs * 7E-6;

	        // Amount of herbage offered by paddok per cow
	        qoh = 10 * (DM - 80) * Au / Ncows;
	        
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
	        
System.out.println("DMi:" + DMi);
       }
	    // HERBAGE DYNAMICS IN ABSENCE OF GRAZING
        else{

			// Herbage accumulation is product of growth and sensenance
//	        DM += DMr - DMs;
	        
			// Here we apply parson's logistic curve, using the
			// growth rate from the CowDigestion model to represent max growth
			DM += ((DMr - DMs) * GF / DM_MAX) * DM * (1 - DM_rec/DM_MAX) * (1 - DM/DM_MAX);

		}
        DM = DM < 0 ? 0 : DM > 1500 ? 1500 : DM;
		
System.out.println("DM:" + DM + "\tDM_last:" + DM_rec + "\tDMr:" + DMr + "\t DMs:" + DMs);
        
        // LEAF GROWTH DYNAMICS 
//NI = 100;
//WI = 1;
		
//TODO: There seems to be a wild difference between LAI grazing and LAI normal!
		if(grazing){
			// Value of LAI immediatly after a defoliation
		System.out.println("DM before:" + DM_grs + "DM after:" + DM);
		//	LAI = K_sward * 1.9 * Math.pow(0.01 * DM, 0.73) * (DM / DM_grs);
			//double LAIi = (K_sward * 1.9 * Math.pow(0.01 * DM, 0.73) * (DM / DM_grs)) / LAI_MAX;
			//LAI -= LAI_MAX * DMi / DM_MAX;
			LAI *= DMch;
		//	LAI = LAI < 0 ? 0 : LAI;
			// From Duru2007
		//	LAI = 6 * Math.pow(DM/DM_grs, 0.73) - 1.5;
		}
       {
	        
	        // LAI Increase as a result of new growth
        	// TODO: instead of simply adding growth, I've added a logistic curve
        	// which results slow initial growth, and final growth. Crucially
        	// it doesnt allow LAI to shrink below 0! Ideas from Parsons2001
        	// Instead of estimating sensenence rates directly, the logistic
        	// curve enforces greater sensenence as the plant gets bigger
        	double dLAI = LAIr - LAIs;
			LAI += dLAI;
//        	LAI += (dLAI * GF / LAI_MAX) * LAI * (1 - LAI_rec / LAI_MAX) * (1 - LAI/LAI_MAX);


				
System.out.println("LAI:" + LAI + "\tLAI_last:" + LAI_rec + "\tLAIr:" + LAIr +"\tLAIs:" + LAIs);//"\t LAI_rec:" + LAI_rec);
	        
	        // Increase in LAI
	        // upper temperature threshold of 18 degrees for leaf growth 
	        LAIr = 11E-6 * Math.pow((Math.min(T, 18)), 2) * (NI - 20) * WI;
	        
	        // Degrease in LAI - sensenence
	        LAIs = LAI_rec * Math.min(T, 18) / LLSmin;
//	        LAIs = LAI * Math.min(T, 18) / LLSmin;
//	        LAIs = 0;
	        
	        // From Duru2007
//	        NI = 0.294f;
//	        LAI += 0.643 * Math.pow(T, 2) * (NI/100) * WI;
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
        	
        // After a defoliation finishing at dayCG(d),
        // the average digestibility of the new leaves produced is:
        double Dcg = (Dmax - Dτ) * (0.9 + 0.001 * NI) - 0.04 * DMr_cum;

        // Accumulated mass of new leaves produced
        // since the last defoliation (cumulative DMr)
        if(action == ACTION_STOP_GRAZE  || action == ACTION_CUT_GRASS){
        	DMr_cum = 0;
        	DMs_cum = 0;
    		DM_rec = DM;
    		qoh_rec = qoh;
        }
        else{
        	DMr_cum += DMr;
        	DMs_cum += DMs;
        }
         
        // residual mass that is still available at day d
        double DMs_res = DM_rec - DMs_cum;

        // Average digestability of the offered herbage
        D = (Dcg * DMr_cum + D_rec * DMs_res) / (DMr_cum + DMs_res);
        if(action == ACTION_STOP_GRAZE)
    		D_rec   = D; // Digestability recorded at the end of a grazing period

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
        if(qih <  (2 / 3) * qoh_rec)
        	D = 14.625 * qoh / qoh_rec + D_rec - 13;
        else
        	D = D_rec + 6.5;

        
        // TODO: What's described in the paper causes the reference LAI to suddenly
        // jump from LAI at last defoliation, causing a sudden (unrealistic) jump
        // in sensennence rate. I'm thinking that what the author meant was that
        // the reference LAI should be taken to be the LAI at the last defoliation
        // event if the total degree days between then and now is less than LLS_min
        // otherwise the reference LAI progressively moves to be equal to the LAI
        // taken at exactly LLS_min days ago.
        
        // Record LAI at defoliation event
        if(action == ACTION_STOP_GRAZE || action == ACTION_CUT_GRASS){
        	LAI_rec = LAI;
        	DM_rec = DM;
        	DD_cum = 0;
        }
        else if(DD_cum < LLSmin){
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
//System.out.println("DD_cum:" + DD_cum);

        // Next day
        d = d >= 365 ? 0 : d + 1;        
	}
	
	

	// Fill per kg consumed of concentrates 
    private static final float BDc = 0.6f;
    // Energetic value for concentrates   UFL⋅kg-1
    private static final double EVc = 1.1; 

    // Fill per kg consumed of silage maize
	private static final float BDm = 1; 
    // Energetic value for maize   UF.kg-1
    private static final double EVm = 0.9;
	
    // Fill per kg consumed of hay 
    private static final float BDa = 1;
    
    // Amount of offered herbage per cow                           kg     0–2000
    private double qoh;
    // The total available energy per cow
    double Ei;
    // Milk produced per cow per day
    double yr;
   
	void cowDay(
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
		    double Ncalving, // Number of days spend from the calving day                   day
		    double qic,     // Amount of concentrates intake                               kg
		    double qia,     // Amount of hay intake                                        kg
		    double qim,     // Amount of maize intake (required if grazing, calculated if not)    kg
		    // qih          Amount of grazed herbage intake                             kg
		    // qih_max      Maximum amount of herbage intake                            kg
//		    double qoh,     // Amount of offered herbage per cow                           kg     0–2000
		    // yp           Daily potential milk yield                                  kg
		    double yp_max,  // Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
		    // yr           Actual daily milk yield per cow                             kg
		    boolean good_hay // Whether the hay used is of good quality
	){
		
	    // POTENTIAL MILK YIELD
	    double yp = yp_max * Math.pow(1 - 0.0035, Ncalving - 40);
	    
		// DAILY FEED INTAKE OF MAIZE
	    
		// Maximum feeding capacity of the cow
		double BD_max = 22 - 8.25 * Math.exp(-0.02 * yp);
		
		// Fill of consumed concentrates
	    double BDic = BDc * qic;
	    
	    // Fill of consumed hay
	    double BDia = BDa * qia;
	    // Energetic value for hay
	    double EVa = good_hay ?  0.8 : 0.6;

	    
	    // Fill of consumed maize silage
	    double BDim = BDm * qim;
	    
		// Amount of grazed herbage intake      kg
		double qih = 0;

		// Energetic value of herbage
	    double EVh = 0;
	    
		if(!grazing){
			// Fill of consumed maize silage
			BDim = BD_max - BDic - BDia;

			// Silage maize intake per cow (kg/day)
			qim = BDim / BDm;
		}
		else{
				
			// mximum feed fill for grazed herbage: // TODO: This is always 0??
			double BDih_max = BD_max - BDic - BDia - BDim;
	
			// DAILY FEED INTAKE OF GRAZED HERBAGE
			
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
	        if(qih <  (2 / 3) * qoh_rec){
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
		    
		    // In vivo digestibility of herbage
		    double Dvv = 1.39 * Di - 26.0;
		    
		    // Energetic value of herbage
		    EVh =  0.0108 * Dvv + 0.208;
		}
		
	    // MILK YIELD
	    
	    // Energy supplied for each feed
	    double Eic = EVc * qic;
	    double Eia = EVa * qia;
	    double Eim = EVm * qim;
	    double Eih = EVh * qih;

	    // The total available energy per cow
	    Ei = Eic + Eia + Eim + Eih;
	    
	    double Emaintenance = grazing ? 6 : 5;
	    
	    // Energy available for milk production
	    double Eproduction = Ei - Emaintenance;
	    
	    // Milk produced per cow per day
	    yr = Math.min(Eproduction / 0.44, yp);
	    
	}
	
	/** Test the model */
	public static void main(String[] args) {
		Random rand = new Random(0);
		
		GraphPanel graphDM = new GraphPanel("Herbage Mass", "day of Year", "KG DM / ha", true, false);
		graphDM.addSeries("DM", Color.RED.darker());
		GraphPanel graphLAI = new GraphPanel("Leaf Area", "day of Year", "LAI", true, false);
		graphLAI.addSeries("LAI", Color.GREEN.darker());
		GraphPanel graphD = new GraphPanel("Digestiability", "day of Year", "g/100g", true, false);
		graphD.addSeries("D", Color.GREEN.darker());
		GraphPanel graphM = new GraphPanel("Milk Production", "day of year", "UF", true, false);
		graphM.addSeries("yr", Color.BLUE.darker());
		
		SepatouParsons sim = new SepatouParsons(
				0, // Day of the year to start at
				SPECIES_COCKSFOOT, // Grass species
				50, // Number of cows in the mob  
				100, // Soil water capacity
				1 // Area of a plot        
		);
		float rainAvg = 5; // Average rainfall
		float rainVar = 2; // Variability of rainfall
		float rainFreq = 1;//0.03f; // Frequency of rain (per day)
		int i = 0;
		while(i++ < 150){
			float rain = 10; //float rain = rand.nextFloat() < rainFreq ? (float)Math.pow(rand.nextFloat() * 2, rainVar) * rainAvg : 0; //        precipitation                                                       mm day-'
			sim.grassDay(
//				i == 100 || i == 300 || i == 800 ?
//						ACTION_CUT_GRASS :
//				i % 7 == 0 ? ACTION_START_GRAZE :
//				i % (7 + 3) == 0 ? ACTION_STOP_GRAZE : 
				i == 50 ? ACTION_START_GRAZE :
				i == 75 ? ACTION_STOP_GRAZE : 
				ACTION_NONE, // Action to carry out
				7,   // Daily potential evapo-transpiration mm  0–
				rain, // Daily rainfall           mm  0–
				60, // Herbage nitrogen index                               0–100
				0.4f, // Daily incident radiation                     MJ⋅m–2
				10, // Average daily temperature                      °C
				1 //            Grazed herbage intake per day and per cow               kg
			);
//System.out.println("DM:" + sim.DM + " \tD:" +  sim.D + "\t AW:" + sim.AW + "\t AET:" + sim.AET);
			graphDM.addData("DM", sim.DM);
			graphLAI.addData("LAI", sim.LAI);
			graphD.addData("D", sim.D);
			

			
			sim.cowDay(
				    20, // Number of days spend from the calving day                   day
				    20,     // Amount of concentrates intake                               kg
				    20,     // Amount of hay intake                                        kg
				    20,     // Amount of maize intake (required if grazing, calculated if not)    kg
//				    qoh,     // Amount of offered herbage per cow                           kg     0–2000
				    200,  // Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
				    true // Whether the hay used is of good quality
			);
//System.out.println("Ei:" + sim.Ei + " yr:" + sim.yr);
			graphM.addData("yr", sim.yr);
			//			Utils.awaitEnter();
		}
		graphDM.displayWindow(580, 50);
		graphLAI.displayWindow(580, 400);
//		graphD.displayWindow(580, 50);
//		graphM.displayWindow(580, 400);
		
	}

}
