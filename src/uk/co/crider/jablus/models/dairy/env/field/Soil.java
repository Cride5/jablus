package uk.co.crider.jablus.models.dairy.env.field;

import java.util.Random;

import uk.co.crider.models.dairy.RandomGenerator;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.utils.Utils;

/** Class representing field soil. Largely based on the STICS soil model */
public class Soil extends CompoundData{
	
	// CONSTANTS	

	// Soil types
	public static final int SAND             =  0;
	public static final int LOAMY_SAND       =  1;
	public static final int SANDY_LOAM       =  2;
	public static final int SANDY_SILT_LOAM  =  3;
	public static final int SANDY_CLAY       =  4;
	public static final int SANDY_CLAY_LOAM  =  5;
	public static final int LOAM             =  6;
	public static final int SILTY_CLAY_LOAM  =  7;
	public static final int SILTY_LOAM       =  8;
	public static final int SILT             =  9;
	public static final int SILTY_CLAY       = 10;
	public static final int CLAY_LOAM	     = 11;
	public static final int CLAY             = 12;
	
	
//	private Parameters params;
	
	// FIXED PARAMETERS

	final int layers = 60; // Numer of soil layers to characterise soil temp (layers are 1cm thick)
//	private final int horizons = 1; // Number of soil horizons to characterise distinct layer features
	private final int INIT_WATER_TABLE = 5; // Initial water table depth (cm)
	private final float[] EPC = { // depth of the horizon H                                         cm
		layers // last horizon must be as deep as number of layers
	};
	
	// Constant climate parameters
	// Reference Evapotranspiration (TETP) can be calculated using the Penman-Monteith equation
	// See http://www.fao.org/docrep/X0490E/x0490e08.htm for method
	private final float TETP = 3.0f; //           reference evapotranspiration                                        mm day-l
	private final int RA = 30; // aerodynamic resistance                                            sm-' (30)
	private final int ACLIM = 20; //       climatic component of A
	// See: http://www3.interscience.wiley.com/journal/113520760/abstract for figures
	final float CONCRR = 0.01f; // nitrogen concentration of precipitation                        kg mm-'

	// Crop management parameters
	// See: http://www.css.cornell.edu/compost/calc/cn_ratio.html
	private float CSURNRES = 10; //    C/N ratio of residues                                          sans dimension
	final float CONCIRR = 0.1f; // (assuming units of kg/mm*ha    nitrogen concentration of irrigating water                     kg mm-'
	final float EFFN = 1.0f; // TODO!        fertilization efficiency                                           sans dimension

	// Soil parameters
	private final float A; //           parameter of the soi1 evaporation during the reduction stage
	// See wikipedia albedo article
	private final float ALBEDO = 0.25f; // albedo of the barc dry soil                                    sans dinicnsion
//	private final float HUCC; // (H)     water content at the field capacity of the horizon H               g water g soil-'
//	private final float HUMIN; // (H)    minimum water content of the horizon H                             g water g soil-'
	private final int Q0 = 11;     //         parameter of the end of the maximum cvaporntion stage             mm
	private final float ARGI = 30; //        percentage of clay in the surface layer                        %
	private final float CALC = 76; //        percentage of limestone in the surface layer                   76
	// Plough depth anything up to 20cm, so 30 allows humification below the plough depth
	private final int PROFHUM = 30; //    humification depth (at least equal to plough depth)                cm
	private final int PROFTRAV = 5; //   depth of residue incorporation                                    cm
	// TODO: What are these values?
	// From a book: Cycles of Soil by F. J. Stevenson, Michael A. Cole (p67)
	// The ratios of C:N:P:S in soil are on average 140:10:1.3:1.3 = so N:C = 10/140 = 0.071428571
	private final float WH = 0.071428571f; //         N/C ratio of the humus                                              gN gc-l
	private final float WR = CSURNRES; //         C/N ratio of the residues remaining in the soi1                     gC gN-'
	private final float DIFN = 0.21f; //       parameter of nitrogen diffusion in the soi1                    cm-2 day-' (0.21)

	// Values for the ASOIL parameter
	private static final float[] ASOIL = {
		0    , // SAND
		0.06f, // LOAMY_SAND
		0.09f, // SANDY_LOAM
		0.10f, // SANDY_SILT_LOAM 
		0.10f, // SANDY_CLAY_LOAM 
		0    , // LOAM
		0.15f, // SILTY_CLAY_LOAM 
		0.12f, // SILTY_LOAM
		0    , // SILT
		0.18f, // SILTY_CLAY
		0.13f, // CLAY_LOAM	
		0.12f, // CLAY 
	};
	
	// Values for HCC and HMIN, found at
	// http://weather.nmsu.edu/MODELS/IRRSCH/SOILTYPE.html
	// http://www.specmeters.com/pdf/water_holding_capacity_chart.pdf
	// Assuming 1 layer
	// Volumetric water content at the field capacity of the horizon H          mm/cm³
	private final double[][] HUCC;
	// water content at the field capacity of the horizon H               g water g soil-'
	private final double[][] HCC = {
		// Top Layer --> Bottom Layer
		{0.091f}, // SAND
		{0.116f}, // LOAMY_SAND
		{0.139f}, // SANDY_LOAM
		{0.f   }, // SANDY_SILT_LOAM 
		{0.231f}, // SANDY_CLAY
		{0.200f}, // SANDY_CLAY_LOAM 
		{0.223f}, // LOAM
		{0.321f}, // SILTY_CLAY_LOAM 
		{0.276f}, // SILTY_LOAM
		{0.332f}, // SILT
		{0.312f}, // SILTY_CLAY
		{0.264f}, // CLAY_LOAM	
		{0.351f}, // CLAY 
	};
	// Volumetric minimum water content of the horizon H                     mm/cm³
	private final double[][] HUMIN;
	// minimum water content of the horizon H                             g water g soil-'
	private final double[][] HMIN = {
		// Top Layer --> Bottom Layer
		{0.039f}, // SAND
		{0.051f}, // LOAMY_SAND
		{0.062f}, // SANDY_LOAM
		{0.f   }, // SANDY_SILT_LOAM 
		{0.113f}, // SANDY_CLAY
		{0.095f}, // SANDY_CLAY_LOAM 
		{0.102f}, // LOAM
		{0.151f}, // SILTY_CLAY_LOAM 
		{0.125f}, // SILT_LOAM
		{0.149f}, // SILT
		{0.150f}, // SILTY_CLAY
		{0.125f}, // CLAY_LOAM	
		{0.172f}, // CLAY 
	};
	// Average bulk density of soils g/cm3
	private final double[] ABD = {
		1.627, // SAND
		1.584, // LOAMY_SAND
		1.533, // SANDY_LOAM
		0    , // SANDY_SILT_LOAM 
		1.444, // SANDY_CLAY
		1.471, // SANDY_CLAY_LOAM 
		1.395, // LOAM
		1.239, // SILTY_CLAY_LOAM 
		1.278, // SILT_LOAM
		1.156, // SILT
		1.279, // SILTY_CLAY
		1.354, // CLAY_LOAM	
		1.262, // CLAY 
	};

	
	// FIXED INTERNAL STATE VARIABLES
	
	private final int soilType;
	private float HA; // residual moisture of the soi1                                      cm3

	
	
	
	// DYNAMIC INTERNAL STATE VARIABLES

	private Crop crop;
	
	public Soil(Parameters params){
//		this.params = params;
		this.soilType = params.SOIL_TYPE;
		
		// Going by the paper entitled:
		// "A semiempirical model of bare soil evaporation for crop simulation models"
		// A is calculated using:
		A = ACLIM * ASOIL[soilType];
		// Set residual soil moisture
		HA = ARGI / 1500;
		// Initialise water stocks
		HUR = new double[layers];
		// Initialise carbon stocks
		CRES = new double[layers];// (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
		CBIO = new double[layers]; //(Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
		CHUM = new double[layers]; // Initialise as 0, should accumulate during model run
		AZO = new double[layers]; 
		// Initialise soil volumetric water content data
		HUCC = new double[HCC.length][HCC[0].length];
		HUMIN = new double[HCC.length][HCC[0].length];
		for(int type = 0; type < HCC.length; type++){
			for(int H = 0; H < HCC[type].length; H++){
				// Multiplying g water/g soil by density of soil in g/cm³
				// gives water capacity in g water / cm³ soil, and we
				// know that 1kg water = 1litre, so 1g water = 1ml
				// 1ml water = 1cm³ and 10mm of rain water will fit into 1cm³
				// thus:          (g/g)         (g/cm³)   (mm)
				HUCC[type][H] = HCC[type][H] * ABD[type] * 10; // mm/cm³
				HUMIN[type][H] = HMIN[type][H] * ABD[type] * 10; // mm/cm³
			}
		}
		// Johnson2006
		// Initialise layered data
		for(int Z = 0; Z < layers; Z++){
			int H = 0; while(Z >= EPC[H]) H++;
			// Initialise soil water with residual moisture above water table
			// or to field capacity below the water table
			HUR[Z] = Z < INIT_WATER_TABLE ? HA : HUCC[soilType][H];
			// Initialise to residue content after typical harvest yielding 10t/ha wheat
			// and divide between layers. Assuming 50% C content of wheat residue stated in Johnson2006
			CRES[Z] = Z >= PROFTRAV ? 0 :  1710 / PROFTRAV; // kgC/ha
			// CHUM (Z)  C content of the humus pool                          kg ha-'cm-'
			// Assuming 10% soil mass is humus, weight in 1cm³ = ABD[soilType] * 0.1
			// see: http://www.stadtentwicklung.berlin.de/umwelt/umweltatlas/ed10605.htm
			// Assuming 50% carbon content of humus, carbon = ABD[soilType] * 0.1 * 0.5
			// see: http://www.rockymtnbioproducts.com/pdf/Understanding%20the%20Carbon-Nitrogen%20Ratio.pdf
			// 1ha = 1cm² * 10000 * 10000
			// 1kg = 1000g
			// Humus = ABD[soilType] * 0.1 * 0.5 * 10000 * 10000 / 1000
			CHUM[Z] = Z >= PROFHUM ? 0 : ABD[soilType] * 5000; // kgC/ha
		}
//System.out.println(Utils.arrayString(HUCC));
//System.out.println(Utils.arrayString(HUMIN));
	}
	
	/** Set crop to be grown */
	public void sowCrop(Crop crop){
		// Deposit residues of old crops
		if(this.crop != null && this.crop instanceof Pasture){
			// In the case of grasses all remaining pasture becomes residues - assuming that ploughing will
			// happen at some point!
			depositResidue(this.crop.getMASEC() * Pasture.GRASS_RES_CARBON * 1000); // Conversion to t/ha
		}
		// In the case of fodder crops, residues were added on harvest day

		// Set new crop
		this.crop = crop;
		
		// Set initial plant nitrogen content if mass already exists
		if(crop != null){
			// For the purposes of visulaisation
			this.LRAC = crop.getLRAC();
			
			QNPLANTE = crop.ADIL * ((crop.getMASEC() < 1) ? 1 : Math.pow(crop.getMASEC(), -crop.BDIL)) / 10;
		}		
	}
	
	/** Soil module needs to be informed when plant mass is removed, so
	 * that plant nitrogen content can be updated */
	public void plantMassRemoved(double massRemoved){
		if(crop == null) return;
		double initMass = crop.getMASEC();
		if(initMass == 0) return;
//System.out.print("QNPlante:" + QNPLANTE + ", initMass:" + initMass + ", massRemoved:" + massRemoved);
		QNPLANTE *= massRemoved > initMass ? 0 : (initMass - massRemoved) / initMass;
//System.out.println(" ... new QNPLaNTE:" + QNPLANTE);
	}
	
	/** Causes residues and humus to become distributed evenly through plough depth */
	public void plough(){
		double AZO_sum = 0;
		double CHUM_sum = 0;
		double CRES_sum = 0;
		double CBIO_sum = 0;
		// Calculate pool totals
		for(int Z = 0; Z < PROFHUM; Z++){
			AZO_sum += AZO[Z];
			CHUM_sum += CHUM[Z];
			CRES_sum += CRES[Z];
			CBIO_sum += CBIO[Z];
		}
		// Distribute evenly among layers up to plough depth
		for(int Z = 0; Z < PROFHUM; Z++){
			AZO[Z] = AZO_sum / PROFHUM;
			CHUM[Z] = CHUM_sum / PROFHUM;
			CRES[Z] = CRES_sum / PROFHUM;
			CBIO[Z] = CBIO_sum / PROFHUM;
		}
		// A ploughed field no longer contains a crop
		sowCrop(null);
	}
	
	/** Causes the stated mass of residue carbon to be deposited in the top layers of the soil
	* Measured in kgC/ha */
	public void depositResidue(double resCarbon){
		for(int Z = 0; Z < PROFTRAV; Z++){
			CRES[Z] += resCarbon / PROFTRAV;
		} 
	}
	
	/** Returns the water content given upper layers of the soil */
	public double getWaterContent(int layers){
		double totalW = 0;
		for(int Z = 0; Z < layers; Z++){
			int H = 0; while(Z >= EPC[H]) H++;
			totalW += HUR[Z] / HUCC[soilType][H];
		}
		return totalW / layers;
	}
	
	/** Returns the nitrogen content given upper layers of the soil */
	public double getNitrogenContent(int layers){
		double totalN = 0;
		for(int Z = 0; Z < layers; Z++){
			totalN += AZO[Z];
		}
		return totalN;
	}
	
	/** Executes a day in the soil model */
	public void execDay(
			double TRR, //         precipitation                                                       mm day-'
			double AIRG, //        irrigation table                                               mm day-l
			double ANIT,       // fertilization table                                            kg N Ha-1 day-1
			double TRG, //         global solar radiation                                              MJ m-2 day-l
			double TMOY, //       mean temperature                                                    "C
			double TMIN //       minimum temperature                                                 OC
			){
		thermal(TRG, TMOY, TMIN);
		water(TRR, AIRG);
		nitrogen(TRR, AIRG, ANIT);

	}
	
	// THERMAL MODULE
	// ---------- Global parameters -------------------------
	// RA          aerodynamic resistance                                            sm-' (30)
	// ---------- Climatic parameters -----------------------
	// TMOY        mean temperature                                                    "C
	// TRG         global solar radiation                                              MJ m-2 day-l
	// ---------- Soil parameters ---------------------------
	// HUMIN       Volumetric minimum water content of the horizon H                     mm/cm³
	// HUCC        Volumetric water content at the field capacity of the horizon H       mm/cm³
	// ALBEDO      albedo of the barc dry soil                                    sans dinicnsion
	// ---------- Shoot growth inputs -]---------------------
	// LAI         leaf area index                                                   mZ leaves       soi1
	// ---------- Water module inputs -----------------------
	// EP          actual transpiration flux                                          mm day-'
	// ES          actual soi1 evaporation flux                                       mm day-l
	// HUR (Z)     volumetric water content of the layer Z                            mm cm-'
	// ---------- Internal variables ------------------------
	// ALBSOL      soi1 albedo                                                    sans dimension
	// RN          net solar radiation                                               MJ m-*
	// AMPLSURF    thermal amplitude at the soi1 surfacc                          O C
	// AMPLZ       thennal amplitude at the depth Z                               O C
	// -----------Temporal variables ------------------------
	// TCULT       surface temperature in daily average                                OC
	// TSOL (Z)    temperature of the soi1 at the depth Z                              OC		
	// ---------- Outputs -----------------------------------

	private double TCULT = 0;
	/** Returns the soil surface temperature */
	public double getTCULT(){ return TCULT; }
	private double[] TSOL = new double[layers]; // Temperature of the soil at plant depth
	/** Returns the soil temparature for each soil layer */
	public double[] getTSOL(){ return TSOL; }	
	/** Executes the thermal soil model component */
	private void thermal(
			double TRG, //         global solar radiation                                              MJ m-2 day-l
			double TMOY, //       mean temperature                                                    "C
			double TMIN //       minimum temperature                                                 OC
		){
		double LAI = crop == null ? 0 : crop.getLAI();
		// Eqution 27
		double ALBSOL = ALBEDO * (0.483 * (HUR[0] - HUMIN[soilType][0]) + (HUCC[soilType][0] - HUR[0]) / (HUCC[soilType][0] - HUMIN[soilType][0]));

		// Eqution 26
		// Daily net radiation
		double RN = (1 - 0.23 - (0.23 - ALBSOL) * Math.exp(-0.75 * LAI)) * TRG * 0.72 - 0.9504;

		// Eqution 25
		// Crop surface temperature
		TCULT = TMOY + (RN - (EP + ES) * 2.46) * RA / (0.0864 * 1200);

		// Eqution 28
		// Daily thermal amplitude at soil surface
		double AMPLSURF = 2 * (TCULT - TMIN);
		for(int Z = 0; Z < TSOL.length; Z++){
			// Equation 29
			// Daily thermal amplitude at layer Z in the soil
			double AMPLZ = AMPLSURF * Math.exp(-8.23 * (Z + 1) / 100);
//System.out.println("AMPLZ:"+AMPLZ);
			// Eqution 30
			// Temperature of soil layer Z
			TSOL[Z] = TSOL[Z] - (AMPLZ / AMPLSURF) * (TCULT - TMIN) + 0.1 * (TCULT - TSOL[Z]) + AMPLZ / 2;
		}

/*
System.out.println(
"THERMAL MODULE" + "\n" +
"ABSOL:" + ALBSOL + "  RN:" + RN + "  ES:" + ES  + "  EP:" + EP + "\n" +
"TRG (radiation):" + TRG +"  TMOY (temp): " + TMOY + "  TMIN:" + TMIN + "\n" + 
"TCULT_:" + TCULT_ + " TSOL_[0]" + TSOL_[0] + "\n" +
"TCULT (surf temp):" + TCULT + "\n" +
"TSOL:" + Utils.arrayString(TSOL)
);
*/
	}		
	
	// WATER BALANCE MODULE
	// ---------- Climatic parameters -----------------------
	// TETP        reference evapotranspiration                                        mm day-l
	// TRR         precipitation                                                       mm day-'
	// ---------- Management parameters ---------------------
	// AIRG        irrigation table                                               mm day-l
	// EFFIRR      irrigation efficiency                                              sans dimension
	// ---------- Soil parameters ---------------------------
	// A           parameter of the soi1 evaporation during the reduction stage
	// EPC (H)     thickness of the horizon H                                         cm
	// HUMIN       Volumetric minimum water content of the horizon H                     mm/cm³
	// HUCC        Volumetric water content at the field capacity of the horizon H       mm/cm³
	// DA (H)      bulk density of horizon H                                      g
	// HA          residual moisture of the soi1                                      cm3
	// Q0          parameter of the end of the maximum cvaporntion stage             mm
	// ---------- Plant parameters --------------------------
	// EXTIN       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
	// KMAX        maxiinum crop coefficient for water requirements                  sans dimension
	// PSISTO      absolute value of the potential of stomatal closing               bars
	// PSITURG     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars
	// ---------- Shoot growth inputs -----------------------
	// LAI         leaf area index                                                   mZ leaves       soi1
	// ---------- Water module inputs -----------------------
	// ---------- Root module inputs ------------------------
	// ZRAC        root depth
	// LRAC (Z)    cffective root density in the layer Z                             cm root cm-' soi1
	// CUMLRACZ    sum of the effective root lengths                              cm root cm-2 soi1
	// -----------Temporal variables ------------------------
	// EOS_sum     sum of potential soil evaporation (EOS)
	// ES_sum      sum of actual soil evaporation (ES)
	// ---------- Internal variables ------------------------		
	// TETA        water content of the soil, available to the plant                   cm3 cm-.'
	// TETSTOMATE  threshold of TETA below which SWFAC decreases                           cm3 cm-.'
	// TETURG      threshold of TETA below which TURFAC decreases                      cm3 cm-?
	// EOP         maximum transpiration flux                                         mm day-'
	// ---------- Outputs -----------------------------------
	// ES          actual soi1 evaporation flux                                       mm day-l
	// EP          actual transpiration flux                                          mm day-'
	// EPZ (Z)     water absorption flux in the layer Z                               mm cm-' day-'
	// SWFAC       stomatal stress index                                             between O and 1
	// TURFAC      turgescence stress index                                            sans dimension between O and
	// DRAIN       water flux drained out of the soi1                                 min day-'
	// HR (H)      water content of the horizon H                                     g water g soil-l
	// HUR         volumetric water content of the layer Z                            mm cm-'
	// XMLCH1      mulch depth                                                         cm
	// not mentioned in paper...
	// RESMES
	// PFZ (Z)     water status of the layer Z (= O if HUMIN, otherwise = 1 )        Oor 1
	// HUMIN       Volumetric minimum water content of the horizon H                   mm/cm³
	// HUCC        Volumetric water content at the field capacity of the horizon H     mm/cm³

	private double EOS_sum; // Sum of potential soil evaporation (EOS)
	private double ES_sum; // Sum of actual soil evaporation (ES)
	private double[] HUR; //         volumetric water content of the layer Z                            mm cm-'
	private double EP;
	/** Returns the actual transpiration flux in mm/day */
	double getEP(){ return EP; }
	private double ES;
	/** Returns the actual evaporation flux in mm/day */
	double getES(){ return ES; }
	private double SWFAC;
	/** Returns the stomatal stress index (0-1) */
	double getSWFAC(){ return SWFAC; }
	private double TURFAC;
	/** Returns the turgesence stress index */
	double getTURFAC(){ return TURFAC; }
	private double DRAIN;
	/** Returns the water flux drained out of the soil mm/day */
	double getDRAIN(){ return DRAIN; }
	private double[] EPZ = new double[layers];
	/** Returns 1 if there is water available at depth Z, 0 otherwise */
	public int getPFZ(int Z){
		int H = 0; while(Z >= EPC[H]) H++;
		return HUR[Z] > HUMIN[soilType][H] ? 1 : 0;
	}
	/** Executes one day of the model's water component */
	private void water(
			double TRR, //         precipitation                                                       mm day-'
			double AIRG //        irrigation table                                               mm day-l		
	){
		double LAI = 0;
		double ZRAC = 0;
		double CUMLRACZ = 0; 
		double[] LRAC = crop == null ? new double[layers] : crop.getLRAC();
		double EXTIN = 0;
		double PSITURG = 0;
		double PSISTO = 0;
		double KMAX = 0;
		double RAYON = 0;
		if(crop != null){
			LAI = crop.getLAI();
			ZRAC = crop.getZRAC();
			CUMLRACZ = crop.getCUMLRACZ(); 
			EXTIN = crop.EXTIN;
			PSITURG = crop.PSITURG;
			PSISTO = crop.PSISTO;
			KMAX= crop.KMAX;
			RAYON = crop.RAYON;
		}
		
		EP = 0;
		ES = 0;
		SWFAC = 0;
		TURFAC = 0;
		DRAIN = 0;
		for(int Z = 0; Z < layers; Z++) EPZ[Z] = 0;
		
		// Eqution 19
		// Potential evaporation from the soil without
		// limiatation by lack of available water
		double EOS = TETP * Math.exp((0.2 - EXTIN) * LAI);

		// If it has just rained, transpiration calculations revert back to first stage
		// Cumulative evaporation must reach Q0 before we start calculating actual evaporation
		if((TRR + AIRG) > 0)
			EOS_sum = -Math.min(Q0, TRR + AIRG); // The value of Q0 can't be more than actual water input
		else
			EOS_sum += EOS;
		
		// Eqution 20
		// Cumulative actual soil evaporation
		double sum = 2 * A * EOS_sum + Math.pow(A, 2);
		sum = sum <= 0 ? 0 : Math.sqrt(sum) - A;
		sum = sum < 0 ? 0 : sum;
		// Actual soi1 evaporation flux calculated by subtracting from yesterday's sum
		ES = sum - ES_sum;
		// Record today's sum
		ES_sum = sum;

//System.out.println("TRR:" + TRR + " LAI:" + LAI + " ES:" + ES + " sum:" + sum + " sqrt:" + (2 * A * EOS_sum + Math.pow(A, 2)) + " A:" + A + " EOS:" + EOS + " EOS_sum:" + EOS_sum);	
		// If not enough evaporation has occured for soil to be dry then we
		// still use the potential evaporation as actual ecaporation
		if(EOS_sum <= 0)
			ES = EOS;
			
		// Eqution 21
		// Intermediaxy variable for the computation of evapotranspiration    mm day-'
		double EO = TETP * (1 + ((KMAX - 1) / (1 + Math.exp(-1.5 * LAI - 3)))); 

		// Only calculate transpiration if roots exist
		double EOP = 0;
//System.out.println("ZRAC_:" + ZRAC_ + " CUMLRACZ_:" + CUMLRACZ_);
		if(ZRAC > 0 && CUMLRACZ > 0){
			// Eqution 22
			// maximum transpiration flux                                         mm day-'
			EOP = (EO - EOS) * (1.4 - 0.4 * ES / EOS);

			// Calculate TETA by averaging water available in each layer
			double TETA = 0;
			for(int Z = 0; Z < ZRAC && Z < layers; Z++){
				int H = 0; while(Z >= EPC[H]) H++;
				double availableWater = HUR[Z] < HUMIN[soilType][H] ? 0 : HUR[Z] - HUMIN[soilType][H];
				TETA += availableWater;
			}
			TETA = 0.1 * TETA / ZRAC;  // Conversion from mm/cm to cm3/cm3
			// Eqution 24
			// Threshold for TETA, above which transpiration is maximal
			double TETSTOMATE = (1.0 / 40) * Math.log((EOP / (2 * Math.PI * CUMLRACZ * PSISTO * 0.001))
										* Math.log(Math.sqrt(Math.PI * CUMLRACZ / ZRAC) / RAYON));
//System.out.println("TETSTOMATE:" + TETSTOMATE + " EOP:" + EOP + " CUMLRACZ:" + CUMLRACZ + " PSISTO:" + PSISTO + " ZRAC:" + ZRAC + " RAYON:" + RAYON);
			// Eqution 23
			// Actual transpiration (as limited by water available)
			EP = TETA > TETSTOMATE || TETSTOMATE <= 0 ? EOP : EOP * (TETA / TETSTOMATE);
//System.out.println("EP:" + EP + " TETA:" + TETA + " TETSTOMATE:" + TETSTOMATE + " EOP:" + EOP);


			// Calcualte water intexes
			SWFAC = EOP <= 0 ? 0 : EP / EOP;
//System.out.println("SWFAC:" + SWFAC + " EOP:" + EOP + " EP:" + EP);

			// Eqution 24
			// threshold of TETA below which TURFAC decreases                      cm3 cm-?
			double TETURG = (1.0 / 40) * Math.log((EOP / (2 * Math.PI * CUMLRACZ * PSITURG * 0.001))
										* Math.log(Math.sqrt(Math.PI * CUMLRACZ / ZRAC) / RAYON));
//System.out.println("TETURG:" + TETURG + " EOP:" + EOP + " CUMLRACZ:" + CUMLRACZ + " PSITURG:" + PSITURG + " ZRAC:" + ZRAC + " RAYON:" + RAYON);
			
			// turgescence stress index                                            sans dimension between O and
			TURFAC = TETA >= TETURG || TETURG <= 0 ? 1 : TETA / TETURG;
//System.out.println("TURFAC:" + TURFAC + " TETA:" + TETA + " TETURG:" + TETURG);

/*
System.out.println(
"EO:" + EO + " EOP:" + EOP + "\n" +
"TETA:" + TETA + " TETSTOMATE:" + TETSTOMATE + "\n");
*/
		}

		// Remove water from 5 layers below XMULCH1 due to evaporation
		{
			double evap = ES; 
			double absorb = 0;
			int XMULCH1 = 0;
			int Z = 0;
//System.out.println("evap:" + evap);
			// calculate total root density
			while((evap > 0 || absorb > 0) && (Z <= XMULCH1 + 5 || Z < ZRAC) && Z < layers){
				// Mulch point defined by number of consecutive dry layers from the top
				if(HUR[Z] <= HA && Z == XMULCH1 + 1)
					XMULCH1 = Z;
				// If layer not dry, then dry it
				else{
					// Drying caused by evaporation
					if(Z <= XMULCH1 + 5){
						if(evap < HUR[Z] - HA){
							HUR[Z] -= evap;
							evap = 0;
						}else{
							// Maximal drying takes layer to its
							// residual moisture content HA
							evap -= HUR[Z] < HA ? 0 : HUR[Z] - HA;
							HUR[Z] = HUR[Z] < HA ? HUR[Z] : HA;
						}
					}
					// Drying caused by transpiration (root absorbtion)
					// TODO: Has this been tested properly
					if(Z < ZRAC){
						// Add root demand from this layer
						absorb += (EP / ZRAC) * (LRAC[Z] / CUMLRACZ) / 10; // conversion to cm
						if(absorb < HUR[Z]){
							EPZ[Z] = absorb;
							HUR[Z] -= EPZ[Z];
							absorb = 0;
						}
						else{
							// Any remaining moisture to absorb
							// will be done by next layer
							EPZ[Z] = HUR[Z] < HA ? 0 : HUR[Z] - HA;
							absorb -= EPZ[Z];
							HUR[Z] = HUR[Z] < HA ? HUR[Z] : HA;
						}
					}
						
				}
				Z++;
			}
		}


		
		
		// Simulate water seepage into soil from rainfall and irrigation
		{
			int Z = 0;
			int H;
			double waterLeft = TRR + AIRG; // Rainfall left in mm
			double layerCapacity;
			// fill successsivly lower layers untill all rainfall is absorbed
			while(waterLeft > 0 && Z < layers){
				H = 0; while(Z >= EPC[H]) H++;
				layerCapacity = HUCC[soilType][H] - HUR[Z];
				if(waterLeft < layerCapacity){
					HUR[Z] += waterLeft;
					// Total absorbtion flux into layer = waterLeft
					EPZ[Z] = waterLeft;
					waterLeft = 0;
				}else{
					HUR[Z] += layerCapacity;
					// Total absorbtion flux into layer is waterLeft, unless waterLeft
					// is too much to fit completely into layer
					EPZ[Z] = (float)waterLeft >= HUCC[soilType][H] ? HUCC[soilType][H] : waterLeft;
//System.out.println("\tEPZ[Z]:" + EPZ[Z] + " and " + (double)0.312 + " and " );
					waterLeft -= layerCapacity;
				}
//System.out.println("Z:" + Z + "\tHUR_[Z]:" + HUR_[Z] + "\tHURZ:" + HUR[Z] + "\tcap:" + layerCapacity + "\tEPZ[Z]:" + EPZ[Z] + "\tHUCC:" + HUCC[soilType][H]);
				Z++;
				// Any additional water falls of end, analogous to runoff
			}
			DRAIN = waterLeft;
		}


//Print percentage water of each layer
/*
System.out.println("TRR (rain):" + TRR);
for(int Z = 0; Z < 10; Z++){
int H = 0; while(Z >= EPC[H]) H++;
float pr = (float)(HUR[Z] / HUCC[soilType][H]);
float p1 = HA / HUCC[soilType][H];
float p2 = HUMIN[soilType][H] / HUCC[soilType][H];

System.out.print("|");
//System.out.print("%:" + pr + "    HUR:" + HUR[Z] + " HUCC:" + HUCC[soilType][H] + " HUMIN:" + HUMIN[soilType][H]  + " SWC:" + (HUCC[soilType][H] - HUMIN[soilType][H]));
for(int i = 0; i < pr * 10; i++) System.out.print(i == (int)(p1 * 10)? "R" : i == (int)(p2 * 10)? "M" : "#");
System.out.println();
}
*/
	
/*
System.out.println(
"WATER BALANCE MODULE" + "\n" +
"TETP:" + TETP + "  LAI:" + LAI + "\n" +
"ES (soil evap):" + ES + "  EP (transpiration):" + EP
);
*/
	}



	// NITROGEN MODULE
	// ---------- Global parameters -------------------------
	// TRR         precipitation                                                       mm day-'
	// ---------- Climatic parameters -----------------------
	// CONCRR      nitrogen concentration of precipitation                        kg mm-'
	// ---------- Management parameters ---------------------
	// ANIT        fertilization table                                            kg N ha-1 day-1
	// AIRG        irrigation table                                               mm day-l
	// CONCIRR     nitrogen concentration of irrigating water                     kg mm-'
	// EFFN        fertilization efficiency                                           sans dimension
	// QRES
	// JULTRAV
	// CSURNRES    C/N ratio of residues                                          sans dimension
	// ---------- Soil parameters ---------------------------
	// NORG
	// ARGI        percentage of clay in the surface layer                        %
	// CALC        percentage of limestone in the surface layer                   76
	// PROFHUM     humification depth                                                cm
	// PROFTRAV    depth of residue incorporation                                    cm
	// NHUM        amount of nitrogen of the humus pool in the soi1                  kg ha-'
	// WH          N/C ratio of the humus                                              gN gc-l
	// WR          C/N ratio of the residues remaining in the soi1                     gC gN-'
	// DIFN        parameter of nitrogen diffusion in the soi1                    cm-2 day-' (0.21)
	// ---------- Plant parameters --------------------------
	// ADIL        parameter of the critical curve of nitrogen needs              gN g dry matter-]
	// BDIL        parameter of the critical curve of nitrogeii needs             sans dimension
	// ADILMAX     parameter of the maximum curve of nitrogen needs               gN g dry matter'
	// BDILMAX     Parameter of the maximum curve of nitrogen needs               sans dimension
	// VMAX1       rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
	// VMAX2       rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
	// KMABS1      constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
	// KMABS2      constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'
	// ---------- Shoot growth inputs -----------------------
	// MASEC       aboveground dry matter                                            t ha-'
	// ---------- Water module inputs -----------------------
	// EP          actual transpiration flux                                          mm day-'
	// HUR         volumetric water content of the layer Z                            mm cm-'
	// DRAIN       water flux drained out of the soi1                                 mm day-'
	// EPZ (Z)     water absorption flux in the layer Z                               mm cm-' day-'
	// ---------- Thermal module inputs ---------------------
	// TSOL (Z)    temperature of the soi1 at the depth Z                              OC
	// ---------- Root module inputs -----------------------
	// LRAC (Z)    cffective root density in the layer Z                             cm root cm-' soi1
	// -----------Temporal variables ------------------------
	// CRES (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
	// CBI0 (Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
	// AZO (Z)     amount of nitrogen in the layer Z                              kg N ha-l cm-'
	// -----------Internal variables ------------------------
	// WB          N/C ratio of the microflora                                         gN gc-'
	// CONCN       nitrogen concentration of the solute of the soi1               kg ha-l mm-'
	// DEMANDE     daily nitrogen need of the plant                               kgN ha-' day-'
	// KR          decaying rate of the residues                                     day-'
	// KB          mortality rate of tlie microilora                                 day-'
	// dCRIO (Z)   daily changes in the C content of the biomass pool             kg ha-' day-'
	// dCHUM (Z)   daily changes in the C content of the humus pool               kg ha-' day-'
	// dCRES (Z)   daily changes in the C content of the residues pool            kg ha-' day-l
	// FTH         temperature correction factor for the computation of humus mineralization                                                     sans dimension
	// FH          water content of the soil, correcting factor for the computation of mineralization                                                  sans dimension
	// FTR         temperature correction factor for the computation of residues mineralization                                                     sans dimension
	// FLUXRAC     biological component of the nitrogen absorption flux               kgN ha-' day-'
	// FLUXSOL     component related to the transport of the nitrogen absorption flux kgN ha-' day-'
	// PRECIPN     supply of fertilizer through precipitation or irrigation          kgN ha-' day-'
	// ---------- Outputs -----------------------------------
	// INNS        nitrogen stress index                                              sans dimension, between O and 1
	// AZ (H)      amount of mineral nitrogen in the horizon H                       kgN ha-'
	// QNPLANTE    amount of nitrogen taken up by the plant                          kgN ha-'
	// QLES        sum of leached nitrogen                                           kgN ha-'
	// QMINH       sum of nitrogen mineralizcd from the humus                        kgN ha-'
	// QMINR       sum of nitrogen mineralized from residues                         kgN ha-l
	private double INNS;
	/** Returns the nitrogen stress index (0-1)*/
	double getINNS(){ return INNS; }
	private double QNPLANTE;
	/** Returns the amount of nitrogen take up by the plant kgN/ha */
	public double getQNPLANTE(){ return QNPLANTE; }
	private double QLES;
	/** Returns the amount of leached nitrogen for the day kgN/ha */
	double getQLES(){ return QLES; }
//	private double QNPLANTE_; //    amount of nitrogen taken up by the plant                          kgN ha-'
	private double[] CRES;// (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
	private double[] CBIO; //(Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
	private double[] CHUM; // amount of nitrogen of the humus pool in the soi1                  kg ha-'
	private double[] AZO; // (Z)    amount of nitrogen in the layer Z                              kg N ha-l cm-'
	
	/** Executes the nitrogen sub-model */
	private void nitrogen(
			double TRR, //         precipitation                                                       mm day-'
			double AIRG,      // irrigation table                                               mm day-l
			double ANIT       // fertilization table                                            kg N ha-1 day-1
	){
		double[] LRAC = crop == null ? new double[layers] : crop.getLRAC();
		double MASEC = 0;
		double DLTAMS = 0;
		double VMAX1 = 0;
		double VMAX2 = 0;
		double KMABS1 = 0;
		double KMABS2 = 0;
		double ADIL = 0;
		double BDIL = 0;
		double ADILMAX = 0;
		double BDILMAX = 0;
		if(crop != null){
			MASEC = crop.getMASEC();
			DLTAMS = crop.getDLTAMS();
			VMAX1 = crop.VMAX1;
			VMAX2 = crop.VMAX2;
			KMABS1 = crop.KMABS1;
			KMABS2 = crop.KMABS2;
			ADIL = crop.ADIL;
			BDIL = crop.BDIL;
			ADILMAX = crop.ADILMAX;
			BDILMAX = crop.BDILMAX;
		}

		INNS = 0;
//		QNPLANTE = 0;
		QLES = 0;

				// TODO: Only mention about this is that it is constant, no idea about the actual value!
			float YRES = 1;//          carbon assimilation yield of the microflora                         g g-'

			// Equation 33
			// daily potential rate of mineralizalion                          day-'
			double K2POT = 0.21 / ((12.5 + ARGI) * (54.5 + CALC));

//System.out.println("ARGI:" + ARGI + "\tCALC:" + CALC + "\tVPOT:" + VPOT + "\tK2POT:" + K2POT + "\tNHUM:" + NHUM);

//			double dCRES_sum = 0;
//			double dCBIO_sum = 0;
//			double dCHUM_sum = 0;
			for(int Z = 0; Z < PROFHUM || Z < PROFTRAV; Z++){
				int H = 0; while(Z >= EPC[H]) H++;
				// Equation 44
				// Water content of the layer
				double FH = 0.2 + 0.8 * (HUR[Z] - HUMIN[soilType][H]) / (HUCC[soilType][H] - HUMIN[soilType][H]);
				FH = FH < 0 ? 0 : FH;
//System.out.println("FH[" + Z + "]:" + FH + "\tHUR[" + Z + "]:" + HUR[Z] + "\tHUMIN:" + HUMIN[soilType][H] + "\tHUCC:" + HUCC[soilType][H]);
				
				
				// Accumulation and mineralisation of crop residues in soil
				if(Z < PROFTRAV){
					// Eqution 43
					// temperature correction factor for the computation of residues mineralization                                                     sans dimension
					double FTR = -0.566 + 0.26 * Math.exp(0.9125 * TSOL[Z] / 15);
					FTR = FTR <= 0 ? 0 : Math.pow(FTR, 1.026);
//	System.out.println("FTR[" + Z + "]:" + FTR + "\tTSOL[" + Z + "]:" + TSOL[Z]);
					
					// Eqution 37
					// decaying rate of the residues                                     day-'
					double KR = (29.6 / (126 + CSURNRES)) * FTR * FH;
//System.out.println("\tKR:" + KR + "\tCSURNRES:" + CSURNRES + "\tFTR:" + FTR + "\tFH:" + FH);
							
					// Equation 38
					// mortality rate of tlie microilora                                 day-'
					double KB = 0.0112 * FTR + FH;

//	System.out.println("KR:" + KR + "\tKB:" + KB);

					// Daily change in three carbon pools
					// Eqution 34
					// dCRES (Z)   daily changes in the C content of the residues pool            kg ha-' day-l
					double dCRES = -KR * CRES[Z];
					CRES[Z] += dCRES;
//					dCRES_sum += dCRES * WR;

					// Eqution 41
					double CSURNBIO = 17.1 * CSURNRES / (12.3 + CSURNRES);
					// TODO: Is this so??
					double WB = CSURNBIO;
					// Eqution 35
					// dCBIO (Z)   daily changes in the C content of the biomass pool             kg ha-' day-'
					double dCBIO = YRES * KR * CRES[Z] - KB * CBIO[Z];
					CBIO[Z] += dCBIO;
//					dCBIO_sum += dCBIO * WB;
//System.out.println("dCBIO:" + dCBIO + "\tYRES:" + YRES + "\tKR:" + KR + "\tCRES[Z]:" + CRES[Z] + "\tKB:" + KB + "\tCBIO[Z]:" + CBIO[Z]);
					// Eqution 39
					// Humification rate
					double HRES = 0.024 * Math.sqrt(CSURNRES);
					// Eqution 36
					// dCHUM (Z)   daily changes in the C content of the humus pool               kg ha-' day-'
					double dCHUM = HRES * KB * CBIO[Z];
					CHUM[Z] += dCHUM;
//					dCHUM_sum += dCHUM * WH;
					
					// TODO: I'm assuming that this fixed nitrogen is added to the pool
//	System.out.print("(1) AZO[" + Z + "] before:" + AZO[Z]);
					double VMINR = -(dCRES / WR + dCBIO / WB + dCHUM * WH);
					// Assuming negative mineralisation is not possible
					VMINR = VMINR < 0 ? 0 : VMINR;
					// TODO: This flux shouldn't result in N values below 0, why is it happening??
					AZO[Z] += VMINR;
//	System.out.println("\tAZO after:" + AZO[Z] + "\tdCRES:" + dCRES + "\tWR:" + WR + "\tdCBIO:" + dCBIO + "\tWB:" + WB + "\tdCHUM:" + dCHUM + "\tWH:" + WH);
//	System.out.println("dCRES:" + dCRES + "\tdCBIO:" + dCBIO + "\tdCHUM" + dCHUM);
//	System.out.println("dCRES:" + dCRES + "\tCRES:" + CRES[Z] + "\tdCBIO:" + dCBIO + "\tCBIO:" + CBIO[Z]);
	/*
	if(Z == 0){
	graph.addData("KB", KB);
	graph.addData("KR", KR);
	}
	*/
				}

				// Simulation of miniralisation of soil humus
				if(Z < PROFHUM){
					// Eqution 42
					// Thermal factors
					double FTH = Math.exp(0.115 * (TSOL[Z] - 15));
//	System.out.println("FTH[" + Z + "]:" + FTH + "\tTSOL[" + Z + "]:" + TSOL[Z]);
					
					double NHUM = CHUM[Z] * WH;
					
					// Eqution 32
					// potential rate of mineralization per layer at the reference temperature and water content                                       kgN ha-' cm-' day-'
					double VPOT = K2POT * NHUM;
//System.out.println("VPOT:" + VPOT + "\tK2POT:" + K2POT + "\tNHUM:" + NHUM);
					
					// Eqution 31
					// rate of humus mineralization per layer                             kgN ha-l cm-' day-'
					// TODO: Should this be added to the N pool??
					double VMINH = VPOT * FTH * FH;
//System.out.println("VMINH[" + Z + "]:" + VMINH + "\tVPOT:" + VPOT + "\tFTH:" + FTH + "\tFH:" + FH);
					// Assuming mineralisation can't be nevative
					VMINH = VMINH < 0 ? 0 : VMINH;
					CHUM[Z] -= VMINH / WH;
					AZO[Z] += VMINH;
					
					
				}
				
			}
//System.out.println();			
			// Eqution 40
			// VMINR       rate of residues organization                                       kgN ha-' day-'
//			double VMINR = -(dCRES_sum + dCBIO_sum + dCHUM_sum);
			// Is this the rate of N production by
			// mineralisation in the soil and should it be added AZO[Z]
			//System.out.println("VMINR:" + VMINR);
			// TODO: What is the use of VMNIR - the layer-by-layer version
			// of this var is added to the carbon pool above
			

			
			
			
			// CALCULATION OF NITROGEN BALANCE, PLANT UPTAKE AND LEACHING
			// Eqution 45
			// PRECIPN     supply of fertilizer through precipitation or irrigation          kgN ha-' day-'
			double PRECIPN = ANIT * EFFN + TRR * CONCRR + AIRG * CONCIRR;
//System.out.println("PRECIPN:" + PRECIPN + "\tANIT:" + ANIT + "\tEFFN:" + EFFN + "\tTRR:" + TRR + "\tCONCRR:" + CONCRR + "\tAIRG:" + AIRG + "\tCONCIRR:" + CONCIRR);
			// Remove any nitrate leached from bottom of profile
			double toDrain = DRAIN;
			int gap = 1; // number of layers removed from bottom
			while(toDrain > 0 && layers - gap >= 0){
				int H = 0; while(layers - gap >= EPC[H]) H++;
				double leached = 0;
				if(toDrain > HUCC[soilType][H]){
					leached = AZO[layers - gap];
//System.out.print("(2) AZO[" + (layers - gap) + "] before:" + AZO[layers - gap]);
					AZO[layers - gap] -= leached;
//System.out.println("\tAZO after:" + AZO[layers - gap] + " leached:" + leached);
					toDrain -= HUCC[soilType][H];
					gap++;
				}
				else{
//System.out.print("(3) AZO[" + (layers - gap) + "] before:" + AZO[layers - gap]);
					leached = AZO[layers - gap] * toDrain / HUCC[soilType][H];
					AZO[layers - gap] -= leached;
//System.out.println("\tAZO after:" + AZO[layers - gap] + " leached:" + leached);
					toDrain = 0;
				}
				QLES += leached;
			}
			// Iterate up through profile, to allow movement of N
			double nSupplyTotal = 0;
			double[] nSupply = new double[layers];
			for(int Z = layers - 1; Z >= 0; Z--){
				int H = 0; while(Z >= EPC[H]) H++;
				if(Z - gap >= 0){
					// Transfer nitrogen from layer above
					double nFlow = AZO[Z - gap] * (EPZ[Z] / HUCC[soilType][H]);
//System.out.println("nFlow:" + nFlow + "\t:AZO[Z]" + AZO[Z] + "\tAZO[Z - gap]:" + AZO[Z - gap] + "\tEPZ[Z]:" + EPZ[Z] + "\tHUCC[soilType][H]" + HUCC[soilType][H] + "\tEPZ/HUCC:" + EPZ[Z] / HUCC[soilType][H]);
//System.out.print("(4) AZO[" + (Z - gap) + "] before:" + AZO[Z - gap]);
					AZO[Z - gap] -= nFlow;
					// TODO: 
//System.out.println("\tAZO after:" + AZO[Z - gap] + "\tnFlow:" + nFlow);
//System.out.print("(5) AZO[" + Z + "] before:" + AZO[Z]);
					AZO[Z] += nFlow;
//System.out.println("\tAZO after:" + AZO[Z] + "\tnFlow:" + nFlow);
				}
				else if(Z - gap == -1){
					// Add nitrogen supplied through fertiliser to
					// highest layer not drained completely
//System.out.print("(6) AZO[" + (gap - 1) + "] before:" + AZO[gap - 1]);
					AZO[gap - 1] += PRECIPN;					
//System.out.println("\tAZO after:" + AZO[gap - 1] + "\tPRECIPN:" + PRECIPN);
//System.out.println("iFlow:" + PRECIPN);
				}
				// Eqution 46
				// CONCN       nitrogen concentration of the solute of the soi1               kg ha-l mm-'
				double CONCN = AZO[Z] / HUR[Z];

//System.out.println("CONCN:" + CONCN + "\tAZO[Z]:" + AZO[Z] + "\tHUR[Z]:" + HUR[Z] + "\tEPZ[Z]:" + EPZ[Z]);
				// Eqution 48
				// Transport of nitrates to sites of root absorption
				// TODO: HUCC may need to be converted from mm to cm
				double FLUXSOL = DIFN * (HUR[Z] / HUCC[soilType][H]) * Math.sqrt(LRAC[Z] + EPZ[Z]) * CONCN;
				// Eqution 49
				// Absorption of nitrates by the roots
				double FLUXRAC = (KMABS2 + CONCN) == 0 ? 0 : CONCN * LRAC[Z] * (VMAX1 / (KMABS1 + CONCN) + VMAX2 / (KMABS2 + CONCN));
				// Maximum potential supply for this layer is the minimum (bottleneck) of the two flows
				nSupply[Z] = Math.min(FLUXSOL, FLUXRAC);
				nSupplyTotal += nSupply[Z];

//System.out.println("Z:" + Z + "\tFLUXSOL:" + FLUXSOL + "\tDIFN:" + DIFN + "\tHUR[Z]:" + HUR[Z] + "\tLRAC[Z]:" + LRAC[Z] + "\tEPZ[Z]:" + EPZ[Z] + "\tCONCN:" + CONCN);
//System.out.println("Z:" + Z + "\tFLUXRAC:" + FLUXRAC + "\tVMAX1:" + VMAX1 + "\tKMABS1:" + KMABS1 + "\tVMAX2:" + VMAX2 + "\tKMABS2:" + KMABS2);

//System.out.println("\t:" +  + );
			}

//System.out.println("FLUX_sum:" + nSupplyTotal + "\tgap:" + gap);

			// Eqution 47
			//  daily nitrogen need of the plant                               kgN ha-' day-'
			double DEMANDE = 10 * ADILMAX * DLTAMS * (MASEC < 1 ? 1 : (1 - BDILMAX) * Math.pow(MASEC, BDILMAX));
			// A change in mass (DLTAMS) of less than 0 results in negative demand
			// This is crazy, so in such cases set demand to 0
			DEMANDE = DEMANDE < 0 ? 0 : DEMANDE; 
//System.out.println("DEMANDE:" + DEMANDE + "\tADILMAX:" + crop.ADILMAX + " DLTAMS:" + DLTAMS + "\tMASEC:" + MASEC + "\tBDILMAX:" + crop.BDILMAX);	
			// Total nitrogen flux as per demand and maximal possible flow
			double nFlow = Math.min(DEMANDE, nSupplyTotal);
			// If demand is too high then supply is limiting factor
			// and so absorbtion will be proportional to supply
			if(nSupplyTotal <= DEMANDE){
				for(int Z = 0; Z < layers; Z++){
//System.out.print("(7) AZO[" + Z + "] before:" + AZO[Z]);
					AZO[Z] -= nSupply[Z];
//System.out.println("\tAZO after:" + AZO[Z] + "\tnSupply:" + nSupply[Z]);
				}
			}
			// Otherwise there is sufficient soil nitrogen to meet
			// demand and so absorbtion in each layer will be proportional
			// to supply, but limited by demand
			else{
				for(int Z = 0; Z < layers; Z++){
//System.out.print("(8) AZO[" + Z + "] before:" + AZO[Z]);
					AZO[Z] -= nSupplyTotal == 0 ? 0 : nSupply[Z] * DEMANDE / nSupplyTotal;
//System.out.println("\tAZO after:" + AZO[Z] + "\tnSupply:" + nSupply[Z] + "\tDEMANDE:" + DEMANDE + "\tnSupplyTotal:" + nSupplyTotal);
				}
			}
//System.out.println("nFlow:" + nFlow + " DEMANDE:" + DEMANDE + " nSupplyTotal:" + nSupplyTotal);	
		
			// amount of nitrogen taken up by the plant                          kgN ha-'
			QNPLANTE += nFlow;
			// Eqution 51
			// Critical nitrogen content below which growth rate decreases
			double NC = ADIL * ((MASEC < 1) ? 1 : Math.pow(MASEC, -BDIL));
			// Eqution 50
			// Plant nitrogen content
			// nitrogen concentration of the plant                            gN g dry matter'
			double CNPLANTE = MASEC <= 0 ? NC : 10 * QNPLANTE / MASEC;
//System.out.println("CNPLANTE:" + CNPLANTE + " MASEC:" + MASEC + " QNPLANTE:" + QNPLANTE + " nFlow:" + nFlow);
			// Eqution 52
			// Nitrogen nutrition index
			// TODO: This small constant prevents INN from being exactly 0, otherwise
			// lack of nigrogen means plants don't grow, lack of growth means lack of nitrogen!
//			double INN = CNPLANTE == 0 ? 0.00001 : NC == 0 ? CNPLANTE : CNPLANTE / NC;
			double INN = NC == 0 ? 1 : CNPLANTE / NC;
			// Nitrogen stress index
			INNS = Math.min(1, INN);
		
//System.out.println("INNS:" + INNS + " INN:" + INN + " CNPLANTE:" + CNPLANTE + " QNPLANTE:" + QNPLANTE + " MASEC:" + MASEC + " NC:" + NC);

//System.out.println("IGER:" + IGER + "\tILEV:" + ILEV + "\tIAMF:" + IAMF + "\tILAX:" + ILAX + "\tIDRP:" + IDRP + "\tISEN:" + ISEN + "\tIMAT:" + IMAT + "\tIREC:" + IREC);
//			if(DEBUG) System.out.println("Day: " + I + " \tYield: " + Utils.roundString(MAGRAIN / 100) + " t/ha\tLeaching: " + Utils.roundString(QLES) + " kgN/ha/day" + "\n");
	}
	
	
	/** Visualise state of the module */
	public void printState(){
//		System.out.println("Leaching (kgN/ha/day): " + Utils.roundString(QLES) + "\tSoil Water Availability: " + Utils.roundString(TURFAC) + "\tTranspiration Index:" + Utils.roundString(SWFAC) + "\tN Stress: " + INNS);		
		printLayers(30);
	}
	private double [] LRAC;
	/** Prints state of the first 17 layers of the soil */
	public void printLayers(){ printLayers(17); }
	/** Prints the state of several soil layers */
	public void printLayers(int depth){
		// Print out soil state
		for(int Z = 0; Z < depth; Z++){
			int H = 0; while(Z >= EPC[H]) H++;
			float pr = (float)(HUR[Z] / HUCC[soilType][H]);
			double p1 = HA / HUCC[soilType][H];
			double p2 = HUMIN[soilType][H] / HUCC[soilType][H];
			float pf = (float)(EPZ[Z] / HUCC[soilType][H]);
			System.out.print("|");
			for(int i = 0; i < 10; i++)
				System.out.print(i >= (pr * 10) ? " " : i == (int)(p1 * 10)? "R" : i == (int)(p2 * 10)? "M" : "#");
			System.out.print("|");
			for(int i = 0; i < 10; i++)
				System.out.print(i >= (pf * 10) ? " " : "v");
			int pl = LRAC == null ? 0 : (int)(LRAC[Z] * 100);
			System.out.print("|");
			for(int i = 0; i < 20; i++)
				System.out.print(i < 10 - pl || i >= 10 + pl ? " " : "¥");
			System.out.print("|");
			for(int i = 0; i < 10; i++)
				System.out.print(i >= (CRES[Z] * 0.01) ? " " : "R");
			System.out.print("|");
			for(int i = 0; i < 3; i++)
				System.out.print(i >= (CBIO[Z] * 0.01) ? " " : "B");
			System.out.print("|");
			for(int i = 0; i < 20; i++)
				System.out.print(i >= (CHUM[Z] * 0.002) ? " " : "H");
			System.out.print("|");
			for(int i = 0; i < AZO[Z]; i++)
				System.out.print("N");
//				System.out.print(AZO[Z]);
			System.out.println();
		}
		System.out.println();
	}
	
	
	/** Test submodel */
	public static void main(String[] args){
		Parameters params = Experiments.create();
		
		Random rand = new Random(params.WEATHER_RANDOM_SEED);
		RandomGenerator genRain    = new RandomGenerator(RandomGenerator.RAINFALL_DAILY       , rand.nextLong());
		RandomGenerator genTemp    = new RandomGenerator(RandomGenerator.TEMPERATURE_DAILY    , rand.nextLong());
		RandomGenerator genTmin    = new RandomGenerator(RandomGenerator.TEMPERATURE_MIN_DAILY, rand.nextLong());
		RandomGenerator genRad     = new RandomGenerator(RandomGenerator.SOLAR_RADIATION_DAILY, rand.nextLong());
		
		Soil s = new Soil(params);
		for(int d = 0; d < 365; d++){
			s.execDay(
					genRain.next(), //         precipitation                                                       mm day-'
					0, //        irrigation table                                               mm day-l
					0,       // fertilization table                                            kg N Ha-1 day-1
					genRad.next(), //         global solar radiation                                              MJ m-2 day-l
					genTemp.next(), //       mean temperature                                                    "C
					genTmin.next() //       minimum temperature                                                 OC
			);
			s.printState();
		}
	}

}
