package uk.co.crider.models.dairy.crop_2;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;

import java.awt.Color;
import java.util.Random;

/** Cow digestion and milk production model, taken from SEPATOU model */
public class CowDigestion {
	

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
    
    
    // DYNAMIC INTERNAL VARIABLES
    private Pasture grass;
    
    
    public void setPasture(Pasture grass){
    	this.grass = grass;
    }

    // OUTPUTS
    
	// Amount of grazed herbage intake      kg
	double qih = 0;
	double getGrassIntake(){ return qih; }
    // The total available energy per cow
    double Ei;
    // Milk produced per cow per day
    double yr;
    double getMilkProduced(){ return yr; }
   
	void execDay(
		    double Ncalving, // Number of days spend from the calving day                   day
		    double qic,     // Amount of concentrates intake                               kg
		    double qia,     // Amount of hay intake                                        kg
		    double qim,     // Amount of maize offered (required if grazing, calculated if not)    kg
		    double yp_max,  // Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
		    boolean good_hay // Whether the hay used is of good quality
	){
		
		boolean grazing = false;
		double qoh = 0;
		double qoh_rec = 0;
		double D_rec = 0;
		if(grass != null){
			grazing = grass.isGrazing();
			qoh = grass.getQoh();
			qoh_rec = grass.getQohRec();
			D_rec = grass.getDRec();
		}
		
	    // POTENTIAL MILK YIELD
	    double yp = yp_max * Math.pow(1 - 0.0035, Ncalving - 40);
//System.out.println("yp:" + yp + "\typ_max:" + yp_max + "\tNcalving:" + Ncalving);

		// DAILY FEED INTAKE OF MAIZE
		// Maximum feeding capacity of the cow
		double BD_max = 22 - 8.25 * Math.exp(-0.02 * yp);
//System.out.println("BD_max:" + BD_max + "\typ:" + yp);
		
		// Fill of consumed concentrates
	    double BDic = BDc * qic;
	    
	    // Fill of consumed hay
	    double BDia = BDa * qia;
	    // Energetic value for hay
	    double EVa = good_hay ?  0.8 : 0.6;

	    
	    // Fill of consumed maize silage
	    double BDim = BDm * qim;
	    
		// Energetic value of herbage
	    double EVh = 0;
	    
		if(!grazing){
			// Fill of consumed maize silage, assuming maximal availability
			BDim = BD_max - BDic - BDia;

			// Silage maize intake demand per cow  (kg/day)
			double qim_dem = BDim / BDm;
			
			// If demand is lower than available then there is a surplus
			double qim_surplus = qim < qim_dem ? 0 : qim - qim_dem;
			
			// Actual consumption equals demand if supply allows
			qim = qim > qim_dem ? qim_dem : qim;
			
			
			// Herbage intake is 0 when not grazing
			qih = 0;
		}
		else{
				
			// mximum feed fill for grazed herbage
			double BDih_max = BD_max - BDic - BDia - BDim;
			BDih_max = BDih_max < 0 ? 0 : BDih_max;
	
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
//System.out.println("BDic:" + Utils.round(BDic) + "\tqic:" + Utils.round(qic) + "\tEic:" + Eic);	    
//System.out.println("BDia:" + Utils.round(BDia) + "\tqia:" + Utils.round(qia) + "\tEia:" + Eia);	    
//System.out.println("BDim:" + Utils.round(BDim) + "\tqim:" + Utils.round(qim) + "\tEim:" + Eim);	    
//System.out.println("BDih:" + Utils.round((BD_max - BDic - BDia - BDim)) + "\tqih:" + Utils.round(qih) + "\tEih:" + Eih);	    

	    // The total available energy per cow
	    Ei = Eic + Eia + Eim + Eih;
	    
	    double Emaintenance = grazing ? 6 : 5;
	    
	    // Energy available for milk production
	    double Eproduction = Ei - Emaintenance;
	    
	    // Milk produced per cow per day
	    yr = Math.min(Eproduction / 0.44, yp);
	    yr = yr < 0 ? 0 : yr;
	    
	}
	
}
