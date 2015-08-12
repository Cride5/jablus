package uk.co.crider.models.dairy.crop_1;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.util.Random;

public abstract class STICS {

	private static final boolean DEBUG = false;
	
	// CONSTANTS	
	
	public static final int MAIZE = 0;
	public static final int WHEAT = 1;
	
	// SOIL HERD_GROUPS
	public static final int SAND             =  0;
	public static final int LOAMY_SAND       =  1;
	public static final int SANDY_LOAM       =  2;
	public static final int SANDY_SILT_LOAM  =  3;
	public static final int SANDY_CLAY  =  3;
	public static final int SANDY_CLAY_LOAM  =  4;
	public static final int LOAM             =  5;
	public static final int SILTY_CLAY_LOAM  =  6;
	public static final int SILTY_LOAM       =  7;
	public static final int SILT             =  8;
	public static final int SILTY_CLAY       =  9;
	public static final int CLAY_LOAM	     = 10;
	public static final int CLAY             = 11;


	

	
	
	
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
	
	private final int layers = 60; // Numer of soil layers to characterise soil temp (layers are 1cm thick)
	private final int horizons = 1; // Number of soil horizons to characterise distinct layer features
	private final float[] EPC = { // depth of the horizon H                                         cm
		layers // last horizon must be as deep as number of layers
	};
	// Values for HCC and HMIN, found at
	// http://weather.nmsu.edu/MODELS/IRRSCH/SOILTYPE.html
	// http://www.specmeters.com/pdf/water_holding_capacity_chart.pdf
	// Assuming 1 layer
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
	

	// FIXED PARAMETERS

	// Constant climate parameters
	private final float TETP; //           reference evapotranspiration                                        mm day-l
	private final int RA = 30; // aerodynamic resistance                                            sm-' (30)
	private final int ACLIM = 20; //       climatic component of A
	// See: http://www3.interscience.wiley.com/journal/113520760/abstract for figures
	private final float CONCRR = 0.01f; // nitrogen concentration of precipitation                        kg mm-'

	// Crop management parameters
	private final int IPLT; //        date of sowing
	private final int PROFSEM;  // sowing depth                                                      cm
	private final int DENSITE;  // sowing density                                                 pl m-2
	private final int ZLABOUR;  // depth of ploughing                                                  cm
	private final float CONCIRR = 0.1f; // (assuming units of kg/mm*ha    nitrogen concentration of irrigating water                     kg mm-'
	private final float EFFN = 0.5f; // TODO!        fertilization efficiency                                           sans dimension
	// See: http://www.css.cornell.edu/compost/calc/cn_ratio.html
	private float CSURNRES = 10; //    C/N ratio of residues                                          sans dimension

	// Soil parameters
	private final float A; //           parameter of the soi1 evaporation during the reduction stage
	private final float ALBEDO; // albedo of the barc dry soil                                    sans dinicnsion
	private final int soilType;
//	private final float HCC; // (H)     water content at the field capacity of the horizon H               g water g soil-'
//	private final float HMIN; // (H)    minimum water content of the horizon H                             g water g soil-'
	private final int Q0;     //         parameter of the end of the maximum cvaporntion stage             mm
	private final float ARGI; //        percentage of clay in the surface layer                        %
	private final float CALC = 76; //        percentage of limestone in the surface layer                   76
	// TODO: Find a local figure for this. 150 is just a ball-park figure 
	// http://barryryan.com/PDFfiles/Ag/LCMR94.pdf
	private final int NHUM = 150; //       amount of nitrogen of the humus pool in the soi1                  kg ha-'
	// Plough depth anything up to 20cm, so 30 allows humification below the plough depth
	private final int PROFHUM = 30; //    humification depth (at least equal to plough depth)                cm
	// TODO: What are these values?
	private final int WH = 0; //         N/C ratio of the humus                                              gN gc-l
	private final int WR = 0; //         C/N ratio of the residues remaining in the soi1                     gC gN-'
	private final float DIFN = 0.21f; //       parameter of nitrogen diffusion in the soi1                    cm-2 day-' (0.21)

	
	// Development Species Parameters
	protected int TDMIN;        // minimum threshold temperature for development                       OC
	protected int TDMAX;        // maximum threshold temperature for development                       OC
	protected int STDRPSEN;     // sum of development units betweeii the stages DRP and SEN          degree-day
	protected int STSENMAT;     // sum of development units between the stages SEN and MAT           degree-day
	protected int PHOBASE;      // base photoperiod                                                  hour
	protected int PHOSAT;       // saturating photoperiod                                            hour
	protected int TGMIN;        // minimum threshold temperature used in the emergence stage           OC
	protected int STPLTGER;     // sum of development units allowing germination                     degree-day
	protected float BELONG;     // parameter of the curve of coleoptile elongation               degree-day-'
	protected float CELONG;     // parameter of the curve of coleoptile elongation                sans dimension
	protected float H20GRAIN;   // water content of the grain when harvested                          g water g fresh matter grain-'
	protected float DESSECGRAIN;// drying rate of the grain                                       g water g fresh matter' OC-'
	protected int JVC;          // number of vernalizing days
	protected int STLEVAMF;     // sum of development units between the stages LEV and AMF           degree-day
	protected int STAMFLAX;     // sum of development units hetween the stages AMF and LAX           degree-day
	protected int STLAXDRP;     // sum of development units between the stages LAX and DRP           degree-day
	protected int ELMAX;         // maximum elongation of the coleoptile in darkness condition         cm	

	// Shoot Growth Species Parameters
	protected float DLAIMAX; //     maximum rate of the setting up of LAI                          m2 leaves m-2 soil degree days-
	protected float BDENS; //       maximum density above which there is coinpetition between plants                                                         pl m-2
	protected float EXTIN; //       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
	protected float TCMIN; //         minimum temperature of growth                                       OC
	protected float TCMAX; //     maxirnuin temperature of growth                                   O C
	protected float TCOPT; //         optimum temperature of growth                                       OC
	protected float EFCROIVEG;   // maximum radiation use efficiency during the vegetative stage       g MJ-'
	protected float EFCROIREPRO; // maximum radiation use efficiency during the grain filling phase    g MJ-'
	protected float MSAER0;     // threshold of aboveground dry matter corresponcling to the root allocation at the beginning of the cycle                          t ha-'
	protected float ADENS;       // parameter of compensation between stem number and plant density                                              sans dimension

	// Yield Species Parameters
	protected int NBJGRAIN; //   period when to compute NBGRAINS                                   number of days before IDRP
	protected float CGRAIN; //      slope of the relationship bctween grain number and growth rate during the NRJGRAIN before stage IDRP                          grains g dry matter' day
	protected float CGRAINV0; //    nuniber of grains produced when growth rate is zero            grains m-'
	protected float VITIRCARB; //     rate of increase of the carbon harvest index                        g grain g plant-l day-'
	protected float VITIRAZO; //      rate of increase of the nitrogen harvest index                      gN grain gN plant-l day-'
	protected float PGRAINMAXI; // maximum weight of one grain (O % water content)                   g

	// Water Species Parameters
	protected float KMAX; // maxiinum crop coefficient for water requirements                  sans dimension
	protected int PSISTO; //     absolute value of the potential of stomatal closing               bars
	protected int PSITURG; //     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars
	
	// Root Growth Species Parameters
	protected int ZPENTE; //         depth where the root density is 112 of the surface root density for the reference profile
	protected int ZPRLIM; //         maximum depth of the root profile for the reference profile
	protected float CROIRAC; //     growth rate of the root front                                  cm degree-day-'
	private final float RAYON = 0.02f; //     average radius of roots                                           cm (0.02)
	private final float LVOPT = 0.5f; //      optimum root density                                              CJII root cm-3 soi] (0.5)


	// Nitrogen Species Parameters
	protected float ADIL; //        parameter of the critical curve of nitrogen needs              gN g dry matter-]
	protected float BDIL; //        parameter of the critical curve of nitrogeii needs             sans dimension
	protected float ADILMAX; //    parameter of the maximum curve of nitrogen needs               gN g dry matter'
	protected float BDILMAX; //    Parameter of the maximum curve of nitrogen needs               sans dimension
	protected float VMAX1;   //    rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
	protected float VMAX2;   //    rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
	protected float KMABS1;  //    constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
	protected float KMABS2;  //    constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'
	
	// DYNAMIC DAILY PARAMETERS
	
	// TMOY        mean temperature                                                    "C
	// TMIN        minimum temperature                                                 OC
	// TRR         precipitation                                                       mm day-'
	// TRG         global solar radiation                                              MJ m-2 day-l
	// AIRG        irrigation table                                               mm day-l
	// ANIT        fertilization table                                            kg N lia-l day-l
	
	
	// FIXED INTERNAL STATE VARIABLES
	
	private final int cropType;
	private float HA; // residual moisture of the soi1                                      cm3

	// DYNAMIC INTERNAL STATE VARIABLES
	
	// Global	
	private int I; // Time counter
	// From shoot growth module
	private double LAI_; // Yesterday's leaf area index                                                   mZ leaves       soi1
	private double MASEC_; // Yesterday's aboveground dry matter                                            t ha-'
	// From yield module
	private double[] daysGrowth; // Stores the last NBJGRAIN growth rates
	private int iGr; // Stores index into the daysGrowth ringbuffer
	private double NBGRAINS = 0; //  grain number                                                      grains m-2
	private double H20GRMAT; // water content of the grain at physiological maturity               g water g fresh matter grain-'
	// From root module
	private double CUMLRACZ_; //    sum of the effective root lengths                              cm root cm-2 soi1
	private double ZRAC_; // Yesterday's          root depth
	private double[] LRAC_ = new double[layers]; // Yesterday's    cffective root density in the layer Z                             cm root cm-' soi1
	// From water module
	private double[] HUR_ = new double[layers]; //         volumetric water content of the layer Z                            mm cm-'
	private double EOS_sum; // Sum of potential soil evaporation (EOS)
	private double ES_sum; // Sum of actual soil evaporation (ES)
	private double SWFAC_; //  stomatal stress index                                             between O and 1
	private double TURFAC_; //  turgescence stress index                                            sans dimension between O and

//	private double EP_; //          actual transpiration flux                                          mm day-'
//	private double ES_; //         actual soi1 evaporation flux                                       mm day-l	
//	private float[] HUMIN_; //       minimum volumetric water content of the layer Z                   mm cm-'
//	private float[] HUCC_; // (Z)    volumetric water content of the layer Z at the field capacity      mm cm-'
	// From Thermal Module
	private double TCULT_; // Yesterday's TCULT on the day before                              °C
	private double[] TSOL_ = new double[layers]; // Yesterday's temperature of the soi1 at the depth Z              °C
	// From Nitrogen Module
	private double INNS_; // Yesterday's nitrogen stress index                                              sans dimension, between O and
	private double QNPLANTE_; //    amount of nitrogen taken up by the plant                          kgN ha-'
	private double[] CRES;// (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
	private double[] CBIO; //(Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
	private double[] AZO; // (Z)    amount of nitrogen in the layer Z                              kg N ha-l cm-'
	// From Development Module
	private double devUnits = 0;   // Generic units of development
	private double JVI = 0;        // Cumulutive vernalizing days 
	// Plant growth phases (-1 indicates phase not reached yet)
	private int IGER = -1; //        day of the stage GER: germanation
	private int ILEV = -1; //        day of the stage LEV: emergence
	private int IAMF = -1; //        day of the stage AMF : maximal acceleration of leaf growth, end of juvenile phase
	private int ILAX = -1; //        day of the stage LAX: maximal leaf area index
	private int ISEN = -1; //        day of the stage SEN: beginning of net senescence
	private int IDRP = -1; //        day of the stage DRP: beginning of grain filling
	private int IMAT = -1; //        day of the stage MAT: pliysiological maturity
	private int IREC = -1; //        day of the stage REC: harvest

	public STICS(
			int cropType, // Crop type, MAIZE or WHEAT
			int IPLT,  // Day in year of planting, 0 = Jan 1st
			// Constant climate parameters
			float TETP, //           reference evapotranspiration                                        mm day-l
			// Crop management parameters
			int PROFSEM,  // sowing depth                                                      cm
			int DENSITE,  // sowing density                                                 pl m-2
			int ZLABOUR,  // depth of ploughing                                                  cm
			// Soil parameters
			float ALBEDO, // albedo of the barc dry soil                                    sans dinicnsion
			int soilType, //           soilType
			int Q0, //         parameter of the end of the maximum cvaporntion stage             mm
			float ARGI //        percentage of clay in the surface layer                        %
		){
		// Set input parameters
		this.cropType = cropType;
		this.soilType = soilType;
		this.IPLT = IPLT < 0 ? 0 : IPLT;
		this.TETP = TETP;
		// Crop management parameters
		this.PROFSEM = PROFSEM;
		this.DENSITE = DENSITE;
		this.ZLABOUR = ZLABOUR;
		// Soil parameters
		this.ALBEDO = ALBEDO;
		this.Q0 = Q0;
		this.ARGI = ARGI;

		initSpeciesParams();

		daysGrowth = new double[NBJGRAIN];

		// Set initial root front (ZRAC) to sew depth (PROFSEM)
		ZRAC_ = PROFSEM;

		// Going by the paper entitled:
		// "A semiempirical model of bare soil evaporation for crop simulation models"
		// A is calculated using:
		A = ACLIM * ASOIL[soilType];
		// Set residual soil moisture
		HA = ARGI / 1500;
		// Initialise carbon stocs
		CRES = new double[layers];// (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
		CBIO = new double[layers]; //(Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
		AZO = new double[layers]; 
		// Initialise layered data
		for(int Z = 0; Z < layers; Z++){
			// Initialise soil water with residual moisture
			HUR_[Z] = HA;
			CRES[Z] = 1;
			CBIO[Z] = 1;
		}
		I = IPLT;
		
/*		// Start counting from planting day
		I    = this.IPLT;
		int layers = 60; // 60 cm soil depth
		TSOL = new double[layers];
		PFZ = new double[layers];
		
		MASEC = -MSAER0; // aboveground dry matter                                            t ha-'
		daysGrowth = new double[NBJGRAIN]; 
		iGr = 0;
		ZRAC = PROFSEM;
*/

System.out.println("Testing " + (cropType == WHEAT ? "wheat" : "maize") + " model");	

laiGraph = new GraphPanel("Leaf Growth", "Day", "LAI", true, false);
laiGraph.addSeries("LAI", Color.GREEN);
dmGraph = new GraphPanel("Mass", "Day", "t/ha", true, true);
dmGraph.addSeries("Plant Mass", Color.GREEN.darker());
dmGraph.addSeries("Grain Yield", Color.YELLOW.darker().darker());
nitrateGraph = new GraphPanel("Nitrate Flows", "Day", "kgN/ha", true, true);
nitrateGraph.addSeries("Nitrate Input", Color.BLUE.darker());
nitrateGraph.addSeries("Leaching", Color.MAGENTA.darker());
waterGraph = new GraphPanel("Water Flows", "Day", "mm", true, true);
waterGraph.addSeries("Rainfall", Color.CYAN.darker());
waterGraph.addSeries("Evaporation", Color.ORANGE);
waterGraph.addSeries("Transpiration", Color.GREEN);
waterGraph.addSeries("Drainage", Color.BLUE.darker());
thermGraph = new GraphPanel("Thermal", "Day", "°C and MJ/m²/d", true, true);
thermGraph.addSeries("Air Temperature", Color.RED);
thermGraph.addSeries("Soil Temperature", Color.YELLOW.darker().darker());
thermGraph.addSeries("Solar Radiation", Color.ORANGE);
if(!DEBUG){
laiGraph.displayWindow(20, 40);
dmGraph.displayWindow(20, 390);
nitrateGraph.displayWindow(560, 40);
waterGraph.displayWindow(560, 390);
thermGraph.displayWindow(1120, 40);
}
	}
GraphPanel laiGraph;
GraphPanel dmGraph;
GraphPanel nitrateGraph;
GraphPanel waterGraph;
GraphPanel thermGraph;

	
	/** Initialises parameters specific to the crop species */
	protected abstract void initSpeciesParams();

	public final void execDay(
			// DYNAMIC DAILY PARAMETERS
			float TMOY,      // mean temperature                                                    "C
			float TMIN,      // minimum temperature                                                 OC
			float TRR,       // precipitation                                                       mm day-'
			float TRG,       // global solar radiation                                              MJ m-2 day-l
			float AIRG,      // irrigation table                                               mm day-l
			float ANIT       // fertilization table                                            kg N lia-l day-l
	){
//System.out.println("\nDay:" + I + " \tdevUnits:" + devUnits);

		// Variable list
		
		// A           parameter of the soi1 evaporation during the reduction stage
		// ACLIM       climatic component of A
		// ADENS       parameter of compensation between stem number and plant density                                              sans dimension
		// ADIL        parameter of the critical curve of nitrogen needs              gN g dry matter-]
		// ADILMAX     parameter of the maximum curve of nitrogen needs               gN g dry matter'
		// AIRG        irrigation table                                               mm day-l
		// ALBEDO      albedo of the barc dry soil                                    sans dinicnsion
		// ALBSOL      soi1 albedo                                                    sans dimension
		// AMPLSURF    thermal amplitude at the soi1 surfacc                          O C
		// AMPLZ       thennal amplitude at the depth Z                               O C
		// ANIT        fertilization table                                            kg N lia-l day-l
		// ARGI        percentage of clay in the surface layer                        %
		// AZ (H)      amount of mineral nitrogen in the horizon H                    kg N ha-'
		// AZO (Z)     amount of nitrogen in the layer Z                              kg N ha-l cm-'
		// BDENS       maximum density above which there is coinpetition between plants                                                         pl m-2
		// BDIL        parameter of the critical curve of nitrogeii needs             sans dimension
		// BDILMAX     Parameter of the maximum curve of nitrogen needs               sans dimension
		// BELONG      paranieter of the curve of coleoptile elongation               degree-day-'
		// CALC        percentage of limestone in the surface layer                   76
		// CBI0 (Z)    amount of carbon in the biomass of the layer Z                 kg ha-' cm-'
		// CELONG      parameter of the curve of coleoptile elongation                sans dimension
		// CEP         transpiration integrated over the cropping season              mm
		// CGRAIN      slope of the relationship bctween grain number and growth rate during the NRJGRAIN before stage IDRP                          grains g dry matter' day
		// CGRAINV0    nuniber of grains produced when growth rate is zero            grains m-'
		// CNGRAIN     nitrogen concentration of grains                               gN g dry matter grain-'
		// CNPLANTE    nitrogen concentration of the plant                            gN g dry matter'
		// CONCIRR     nitrogen concentration of irrigating water                     kg mm-'
		// CONCN       nitrogen concentration of the solute of the soi1               kg ha-l mm-'
		// CONCRR      nitrogen concentration of precipitation                        kg mm-'
		// CRES (Z)    amount of carbon in the residues of the layer Z                kg ha-l cm-'
		// CROIRAC     growth rate of the root front                                  cm degree-day-'
		// CSURNRES    C/N ratio of residues                                          sans dimension
		// CUMLRACZ    sum of the effective root lengths                              cm root cm-2 soi1
		// DA (H)      bulk density of horizon H                                      g
		// dCRIO (Z)   daily changes in the C content of the biomass pool             kg ha-' day-'
		// dCHUM (Z)   daily changes in the C content of the humus pool               kg ha-' day-'
		// dCRES (Z)   daily changes in the C content of the residues pool            kg ha-' day-l
		// DELTA1      daily increase of the leaf index                               m2 leaves m-2 soi1 day-'
		// DELTAZ      deepeiiing of the root front                                   cm
		// DEMANDE     daily nitrogen need of the plant                               kgN ha-' day-'
		// DENSITE     sowing density                                                 pl m-2
		// DESSECGRAIN drying rate of the grain                                       g water g fresh matter' OC-'
		// DIFN        parameter of nitrogen diffusion in the soi1                    cm-2 day-' (0.21)
		// DLAIMAX     maximum rate of the setting up of LAI                          m2 leaves m-2 soil degree days-
		// DLTAMS      growth rate of the plant                                       t ha-' day-'
		// DRAIN       water flux drained out of the soi1                                 min day-'
		// EBMAX       maximum radiation use efficiency = EFCROIVEG or EFCROTREPRO, according to the stage of the crop                    t ha-' MJ-' m'
		// EFCROIREPRO maximum radiation use efficiency during the grain filling phase    g MJ-'
		// EFCROIVEG   maximum radiation use efficiency during the vegetative stage       g MJ-'
		// EFDENSITE   density effect acting on the setting up of LAI                     sans dimension
		// EFFIRR      irrigation efficiency                                              sans dimension
		// EFFN        fertilization efficiency                                           sans dimension
		// ELMAX       maximum elongation of the coleoptile in darkness condition         cm
		// ELONG       coleoptile elongation                                              cm
		// EO          intermediaxy variable for the computation of evapotranspiration    mm day-'
		// EOP         maximum transpiration flux                                         mm day-'
		// EOS         maximum evaporation flux                                           mm day-'
		// EP          actual transpiration flux                                          mm day-'
		// EPC (H)     thickness of the horizon H                                         cm
		// EPZ (Z)     water absorption flux in the layer Z                               mm cm-' day-'
		// ES          actual soi1 evaporation flux                                       mm day-l
		// EXTIN       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
		// FH          water content of the soil, correcting factor for the computation of mineralization                                                  sans dimension
		// FLUXRAC     biological component of the nitrogen absorption flux               kgN ha-' day-'
		// FLUXSOL     component related to the transport of the nitrogen absorption flux kgN ha-' day-'
		// FTH         temperature correction factor for the computation of humus mineralization                                                     sans dimension
		// FTR         temperature correction factor for the computation of residues mineralization                                                     sans dimension
		// GPRECO      variety identification number
		// H20GRAIN    water content of the grain when harvested                          g water g fresh matter grain-'
		// H20GRMAT    water content of the grain at physiological maturity               g water g fresh matter grain-'
		// HA          residual moisture of the soi1                                      cm3
		// HCC (H)     water content at the field capacity of the horizon H               g water g soil-'
		// HMIN (H)    minimum water content of the horizon H                             g water g soil-'
		// HR (H)      water content of the horizon H                                     g water g soil-l
		// HUCC (Z)    volumetric water content of the layer Z at the field capacity      mm cm-'
		// HUMIN       minimum volumetric water content of the layer Z                   mm cm-'
		// HUR         volumetric water content of the layer Z                            mm cm-'
		// I           running day
		// IAMF        day of the stage AMF: maximal acceleration of leaf growth, end of juvenile phase
		// IDRP        day of the stage DRP: beginning of grain filling
		// IGER        day of the stage GER: germanation
		// ILAX        day of the stage LAX: maximal leaf area index
		// ILEV        day of the stage LEV: emergence
		// IMAT        day of the stage MAT: pliysiological maturity
		// INN         satisfaction of nitrogen needs                                     sans dimension
		// INNS        nitrogen stress index                                              sans dimension, between O and
		// IPLT        date of sowing
		// IRAZO       nitrogen harvest index                                             gN grain gN plant-'
		// IRCARB      carbon harvest index                                               g grain g-' plant
		// IREC        day of the stage REC: harvest
		// ISEN        day of the stage SEN: beginning of net senescence
		// JVC         number of vernalizing days
		// JVI         daily contribution to the vemalization
		// K2POT       daily potential rate of mineralizalion                          day-'
		// KB          mortality rate of tlie microilora                                 day-'
		// KMABS1      constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
		// KMABS2      constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'
		// KMAX        maxiinum crop coefficient for water requirements                  sans dimension
		// KR          decaying rate of the residues                                     day-'
		// LAI         leaf area index                                                   mZ leaves       soi1
		// LATITUDE    latitudinal position of the crop                                  degrees and tenths
		// LRAC (Z)    cffective root density in the layer Z                             cm root cm-' soi1
		// LVOPT       optimum root density                                              CJII root cm-3 soi] (0.5)
		// MAGRAIN     diy inatter of grains                                             g m-2
		// MASEC       aboveground dry matter                                            t ha-'
		// MSAER0      threshold of aboveground dry matter corresponcling to the root allocation at the beginning of the cycle                          t ha-'
		// NBGRAINS    grain number                                                      grains m-2
		// NBJGRAIN    period when to compute NBGRAINS                                   number of days before IDRP
		// NHUM        amount of nitrogen of the humus pool in the soi1                  kg ha-'
		// PFZ (Z)     water status of the layer Z (= O if HUMIN, otherwise = 1 )        Oor 1
		// PGRAIN      weight of one grain                                               g
		// PGRAINMAXI  maximum weight of one grain (O % water content)                   g
		// PHOBASE     base photoperiod                                                  hour
		// PH01        running photoperiod                                               hour
		// PHOSAT      saturating photoperiod                                            hour
		// PRECIPN     supply of fertilizer through precipitation or irrigation          kgN ha-' day-'
		// PROFHUM     humification depth                                                cm
		// PROFSEM     sowing depth                                                      cm
		// PROFSOL     soi1 depth                                                        cm
		// PROFTRAV    depth of residue incorporation                                    cm
		// PSISTO      absolute value of the potential of stomatal closing               bars
		// PSITURG     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars
		// Q0          parameter of the end of the maximum cvaporntion stage             mm
		// QLES        sum of leached nitrogen                                           kgN ha-'
		// QMINH       sum of nitrogen mineralizcd from the humus                        kgN ha-'
		// QMINR       sum of nitrogen mineralized from residues                         kgN ha-l
		// QNGRAIN     amount of nitrogcn in grains                                      kgN ha-'
		// QNPLANTE    amount of nitrogen taken up by the plant                          kgN ha-'
		// RA          aerodynamic resistance                                            sm-' (30)
		// RAINT       photosynthetic active radiation intercepted by the canopy         MJ m-2
		// RAYON       average radius of roots                                           cm (0.02)
		// RFPI        slowing effect of the photoperiod on plant development            between O and 1
		// RFVI        slowing effect of the vernalization on plant development          between O and 1
		// RN          net solar radiation                                               MJ m-*
		// S           parameter for the calculation of root density                     cm-'
		// STAMFLAX    sum of development units hetween the stages AMF and LAX           degree-day
		// STDRPSEN    sum of development units betweeii the stages DRP and SEN          degree-day
		// STLAXDRP    sum of development units between the stages LAX and DRP           degree-day
		// STLEVAMF    sum of development units between the stages LEV and AMF           degree-day
		// STMATREC    sum of development units between the stages MAT and REC           degree-day
		// STPLTGER    sum of development units allowing germination                     degree-day
		// STSENMAT    sum of development units between the stages SEN and MAT           degree-day
		// SWFAC       stomatal stress index                                             between O and 1
		// TCMAX       maxirnuin temperature of growth                                   O C
		// TCMIN       minimum temperature of growth                                       OC
		// TCOPT       optimum temperature of growth                                       OC
		// TCULT       surface temperature in daily average                                OC
		// TCULTVEILLE TCULT on the day before                                                OC
		// TDMAX       maximum threshold temperature for development                       OC
		// TDMIN       minimum threshold temperature for development                       OC
		// TEAUGRAIN   water content of the grain                                          g water g fresh grain
		// TETA        water content of the soil, available to the plant                   cm3 cm-.'
		// TETP        reference evapotranspiration                                        mm day-l
		// TETSTOMATE  threshold of TETA below which SWFAC decreases                           cm3 cm-.'
		// TETURG      threshold of TETA below which TURFAC decreases                      cm3 cm-?
		// TGMIN       minimum threshold temperature used in the emergence stage           OC
		// TMIN        minimum temperature                                                 OC
		// TMOY        mean temperature                                                    "C
		// TRG         global solar radiation                                              MJ m-2 day-l
		// TRR         precipitation                                                       mm day-'
		// TSOL (Z)    temperature of the soi1 at the depth Z                              OC
		// TSOLVEILLE (Z) temperature of the soi1 at the depth Z, on the day before           OC
		// TURFAC      turgescence stress index                                            sans dimension between O and
		// UDEVCULT    effective temperature for the development, computed with TCULT
		// ULAI        physiological time units for the calculation of the leaf area index hetween ILEV and ILAX                                               sans dimension between 1 and
		// VITIRAZO    rate of increase of the nitrogen harvest index                      gN grain gN plant-l day-'
		// VITIRCARB   rate of increase of the carbon harvest index                        g grain g plant-l day-'
		// VITMOY      mean daily growth rate during the setting up of the grain number (NBJGRAIN before the stage IDRP)                                    g m-= day-'
		// VMAX1       rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
		// VMAX2       rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
		// VMINH (Z)   rate of humus mineralization per layer                              kgN ha-l cm-' day-'
		// VMINR       rate of residues organization                                       kgN ha-' day-'
		// VPOT (Z)    potential rate of niineralization per layer at the reference temperature and water content                                       kgN ha-' cm-' day-'
		// WB          N/C ratio of the microflora                                         gN gc-'
		// WH          N/C ratio of the humus                                              gN gc-l
		// WR          C/N ratio of the residues remaining in the soi1                     gC gN-'
		// XMLCH1      mulch depth                                                         cm
		// YRES        carbon assimilation yield of the microflora                         g g-'
		// ZDEMI       depth where the root density is 112 of the surface root density     cm
		// ZLABOUR     depth of ploughing                                                  cm
		// ZPENTE      depth where the root density is 112 of the surface root density for the reference profile
		// ZPRLIM      maximum depth of the root profile for the reference profile
		// ZRAC        root depth
		// Z           layer number, starting from 1 (top layer), all 1cm thick

		// ---------- Global parameters -------------------------
		// ---------- Climatic parameters -----------------------
		// ---------- Management parameters ---------------------
		// ---------- Soil parameters ---------------------------
		// ---------- Plant parameters --------------------------
		// ---------- Shoot growth inputs -----------------------
		// ---------- Yield module inputs -----------------------
		// ---------- Water module inputs -----------------------
		// ---------- Root module inputs ------------------------
		// ---------- Thermal module inputs ---------------------
		// ---------- Nitrogen module inputs --------------------
		// ---------- Development inputs ------------------------
		// ---------- Internal variables ------------------------
		// -----------Temporal variables ------------------------
		// ---------- Outputs -----------------------------------
		

		// SHOOT GROWTH MODULE ----------------------------------------------
		// ---------- Climatic parameters -----------------------
		// TRG         global solar radiation                                              MJ m-2 day-l
		// ---------- Management parameters ---------------------
		// DENSITE     sowing density                                                 pl m-2
		// ---------- Plant parameters --------------------------
		// DLAIMAX     maximum rate of the setting up of LAI                          m2 leaves m-2 soil degree days-
		// BDENS       maximum density above which there is coinpetition between plants                                                         pl m-2
		// EXTIN
		// TCMIN       minimum temperature of growth                                       OC
		// TCMAX       maxirnuin temperature of growth                                   O C
		// TCOPT       optimum temperature of growth                                       OC
		// EFCROIVEG   maximum radiation use efficiency during the vegetative stage       g MJ-'
		// EFCROIREPRO maximum radiation use efficiency during the grain filling phase    g MJ-'
		// MSAER0      threshold of aboveground dry matter corresponcling to the root allocation at the beginning of the cycle                          t ha-'
		// ADENS       parameter of compensation between stem number and plant density                                              sans dimension
		// ---------- Thermal module inputs ---------------------
		// TCULT       surface temperature in daily average                                OC
		// ---------- Nitrogen module inputs --------------------
		// INNS        nitrogen stress index                                              sans dimension, between O and
		// ---------- Development inputs ------------------------
		// ILEV        day of the stage LEV: emergence
		// IAMF        day of the stage AMF: maximal acceleration of leaf growth, end of juvenile phase
		// ILAX        day of the stage LAX: maximal leaf area index
		// ISEN        day of the stage SEN: beginning of net senescence
		// IMAT        day of the stage MAT: pliysiological maturity
		// ---------- Internal variables ------------------------
		// -----------Temporal variables ------------------------
		// LAI         leaf area index                                                   mZ leaves       soi1
		// ---------- Outputs -----------------------------------
		// MASEC       aboveground dry matter                                            t ha-'
		// DLTAMS      growth rate of the plant                                       t ha-' day-'
		
		// TODO: Check
		// SWFAC       stomatal stress index                                             between O and 1
		// TURFAC      turgescence stress index                                            sans dimension between O and
		// EFDENSITE   density effect acting on the setting up of LAI                     sans dimension

		// Outputs
		double MASEC = MASEC_; // aboveground dry matter                                            t ha-'
		double LAI = LAI_; // leaf area index                                                   mZ leaves       soi1
		double DLTAMS = 0; //     growth rate of the plant                                       t ha-' day-'

		{

			// Other state variables this module depends on

			// Internal state variables used by this module
			double laiSen = -1; // LAI at the beginning of the sensenence stage
			// Output variables from this module

			// Growth of LAI - between stages ILEV and ILAX
			if(ILEV >= 0 && ILAX < 0){
				double ULAI = 0;
				if(IAMF < 0) // Stage ILEV to IAMF
					ULAI = 1 + (2.2 - 1) * devUnits / STLEVAMF;
				else // Stage IAMF to ILAX
					ULAI = 2.2 + (3 - 2.2) * devUnits / STAMFLAX;

				// Eqution 9
				double EFDENSITE = Math.pow(DENSITE/BDENS, ADENS);

				// Eqution 8
				double DELTAI = (DLAIMAX / (1 + Math.exp(5.5 * (2.2 - ULAI)))) * (TCULT_ - TCMIN) * Math.min(TURFAC_, INNS_) * EFDENSITE * DENSITE;

				LAI += DELTAI;
/*
System.out.println(
"SHOOT GROWTH MODULE" + "\n" +
"DLAIMAX:" + DLAIMAX + "\tULAI:" + ULAI + "\tTCULT_:" + TCULT_ + "\tTCMIN:" + TCMIN + "\tTURFAC_:" + TURFAC_ + "\tINNS_:" + INNS_ + "\tEFDENSITE:" + EFDENSITE + "\tDENSITE:" + DENSITE + "\n" +
"DELTAI:" + DELTAI + " LAI:" + LAI
);
*/
			}

			// Sensenence of LAI
			if(ISEN >= 0 && IMAT < 0){
				if(laiSen == -1)
					laiSen = LAI;
				LAI = laiSen * (1 - devUnits / STSENMAT);
			}

			// Growth of mass
			if(ILEV >= 0 && IMAT < 0){
				// Eqution 10
				double RAINT = 0.95 * 0.5 * TRG * (1 - Math.exp(-EXTIN * LAI));
//System.out.println(" RAINT:" + RAINT + " TRG:" + TRG + " EXTIN:" + EXTIN + " LAI:" + LAI);
				float EBMAX = 0;			
				if(IDRP < 0) // Vegitative stage
					EBMAX = EFCROIVEG;
				else // Grain filling stage
					EBMAX = EFCROIREPRO;
				// Eqution 12
				double f_TCULT = 1 - Math.pow((TCULT_ - TCOPT) / (((TCULT_ < TCOPT) ? TCMIN : TCMAX) - TCOPT), 2);
//System.out.println("f_TCULT:" + f_TCULT + "\tTCULT_:" + TCULT_ + "\tTCOPT:" + TCOPT + "\tTCMIN:" + TCMIN + "\tTCMAX:" + TCMAX);
				// Eqution 11
				// DLTAMS      growth rate of the plant                                       t ha-' day-'
				DLTAMS = (EBMAX * RAINT - 0.0815 * Math.pow(RAINT, 2)) * f_TCULT * SWFAC_ * INNS_ / 100;
				// TODO: Assuming DLTAMS cannot be less than 0, ie. plant cannot shrink in mass
				DLTAMS = DLTAMS < 0 ? 0 : DLTAMS;
//System.out.println(" DLTAMS:" + DLTAMS + " EBMAX:" + EBMAX + " RAINT:" + RAINT + " f_TCULT:" + f_TCULT + " SWFAC_:" + SWFAC_ + " INNS_:" + INNS_);
				MASEC = MASEC_ + DLTAMS;

				if(IDRP < 0){
					// record this day's growth for benefit of yield module
					daysGrowth[iGr++ % NBJGRAIN] = DLTAMS;
				}
			}
laiGraph.addData("LAI", LAI);
dmGraph.addData("Plant Mass", MASEC);
		}
		
		// YIELD MODULE ------------------------------------------------	
		// ---------- Plant parameters --------------------------
		// NBJGRAIN    period when to compute NBGRAINS                                   number of days before IDRP
		// CGRAIN      slope of the relationship bctween grain number and growth rate during the NRJGRAIN before stage IDRP                          grains g dry matter' day
		// CGRAINV0    nuniber of grains produced when growth rate is zero            grains m-'
		// PGRAINMAXI  maximum weight of one grain (O % water content)                   g
		// VITIRCARB   rate of increase of the carbon harvest index                        g grain g plant-l day-'
		// VITIRAZO    rate of increase of the nitrogen harvest index                      gN grain gN plant-l day-'
		// ---------- Shoot growth inputs -----------------------
		// MASEC      aboveground dry matter                                            t ha-'
		// ---------- Nitrogen module inputs --------------------
		// QNPLANTE    amount of nitrogen taken up by the plant                          kgN ha-'
		// ---------- Internal variables ------------------------
		// -----------Temporal variables ------------------------
		// NBGRAINS    grain number                                                      grains m-2
		// ---------- Outputs -----------------------------------
		// MAGRAIN     dry matter of grains                                             g m-2
		// PGRAIN      weight of one grain                                               g
		// QNGRAIN     amount of nitrogcn in grains                                      kgN ha-'

		double MAGRAIN; //    dry inatter of grains                                             g m-2
		double PGRAIN; //    weight of one grain                                               g
		double QNGRAIN; //   amount of nitrogcn in grains                                      kgN ha-'

		{		
			// If just entered the DRP stage
			// calculate mean growth rate and
			// number of grains
//System.out.println("I:"  + I + " IDRP:" + IDRP);
			if(IDRP >= 0 && IDRP == I - 1){
				// mean daily growth rate during the setting up of the
				// grain number (NBJGRAIN before the stage IDRP)                                    g m-= day-'
				double VITMOY = 0;
				for(double g : daysGrowth)
					VITMOY += g;
				// VITMOY      mean daily growth rate during the setting up of the grain number (NBJGRAIN before the stage IDRP)                                    g m-= day-'
				VITMOY /= NBJGRAIN;
				// Eqution 13
				NBGRAINS = CGRAIN * VITMOY * 100 + CGRAINV0;  // Conversion from tonnes/ha to grams/m2
//System.out.println("daysGrowth:" + Utils.arrayString(daysGrowth));
//System.out.println("NBGRAINS:" + NBGRAINS + " VITMOY:" + VITMOY + " iGr:" + iGr);
			}
			// Eqution 14
			double IRCARB = VITIRCARB * (IDRP < 0 ? 0 : IREC >= 0 && I > IREC ? IREC - IDRP : I - IDRP);
			double IRAZO = VITIRAZO * (IDRP < 0 ? 0 : IREC >= 0 && I > IREC ? IREC - IDRP : I - IDRP);
	
			// Eqution 15
			// MAGRAIN     dry matter of grains                                             g m-2
			MAGRAIN = IRCARB * MASEC * 100; // Conversion from tonnes to grams, ha to m2)
			// QNGRAIN     amount of nitrogen in grains                                      kgN ha-'
			QNGRAIN = IRAZO * QNPLANTE_;
	
			// Eqution 16
			// PGRAIN      weight of one grain                                               g
			PGRAIN   = NBGRAINS == 0 || IDRP < 0 ? 0 : Math.min(MAGRAIN / NBGRAINS, PGRAINMAXI);
			
			if(IMAT == I){ // TODO: Assuming that the water content of grains is 50% find out exact content
				H20GRMAT = PGRAIN * 0.5; // water content of the grain at physiological maturity               g water g fresh matter grain-'
			}
//System.out.println("MAGRAIN:" + MAGRAIN + " QNGRAIN:" + QNGRAIN + " NBGRAINS:" + NBGRAINS + " PGRAIN:" + PGRAIN);
dmGraph.addData("Grain Yield", MAGRAIN / 100);
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
		// HCC (H)     water content at the field capacity of the horizon H               g water g soil-'
		// HMIN (H)    minimum water content of the horizon H                             g water g soil-'
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
		// HUCC (Z)    volumetric water content of the layer Z at the field capacity      mm cm-'
		// HUMIN       minimum volumetric water content of the layer Z                   mm cm-'

//		double[] HUMIN = new double[horizons]; //       minimum volumetric water content of the layer Z                   mm cm-'
//		double[] HUCC = new double[layers]; // (R)    volumetric water content of the layer Z at the field capacity      mm cm-'
		double[] HUR = new double[layers]; //         volumetric water content of the layer Z                            mm cm-'
//		double[] PFZ = new double[horizons]; // Yesterday's water status of the soil at plant depth
		double EP = 0;
		double ES = 0;
		double SWFAC = 0;
		double TURFAC = 0;
		double DRAIN = 0;
		double[] EPZ = new double[layers];
		{
			for(int Z = 0; Z < layers; Z++)
				HUR[Z] = HUR_[Z];
	
			// Simulate water seepage into soil from rainfall
			// TODO: Need to also include irrigation here
			{
				int Z = 0;
				int H;
				double TRR_left = TRR / 10; // Rainfall left in cm (1cm = 10mm)
				double layerCapacity;
				// fill successsivly lower layers untill all rainfall is absorbed
				while(TRR_left > 0 && Z < layers){
					H = 0; while(Z >= EPC[H]) H++;
					layerCapacity = HCC[soilType][H] - HUR[Z];
					if(TRR_left < layerCapacity){
						HUR[Z] += TRR_left;
						// Total absorbtion flux into layer = TRR_left
						EPZ[Z] = TRR_left;
						TRR_left = 0;
					}else{
						HUR[Z] += layerCapacity;
						// Total absorbtion flux into layer is TRR_left, unless TRR_left
						// is too much to fit completely into layer
						EPZ[Z] = (float)TRR_left >= HCC[soilType][H] ? HCC[soilType][H] : TRR_left;
//System.out.println("\tEPZ[Z]:" + EPZ[Z] + " and " + (double)0.312 + " and " );
						TRR_left -= layerCapacity;
					}
//System.out.println("Z:" + Z + "\tHUR_[Z]:" + HUR_[Z] + "\tHURZ:" + HUR[Z] + "\tcap:" + layerCapacity + "\tEPZ[Z]:" + EPZ[Z] + "\tHCC:" + HCC[soilType][H]);
					Z++;
					// Any additional water falls of end, analogous to runoff
				}
				DRAIN = TRR_left;
			}

			// Eqution 19
			// Potential evaporation from the soil without
			// limiatation by lack of available water
			double EOS = TETP * Math.exp((0.2 - EXTIN) * LAI);

			// If it has just rained, transpiration calculations revert back to first stage
			// Cumulative evaporation must reach Q0 before we start calculating actual evaporation
			if(TRR > 0)
				EOS_sum = -Q0;
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
			if(ZRAC_ > 0 && CUMLRACZ_ > 0){
				// Eqution 22
				// maximum transpiration flux                                         mm day-'
				EOP = (EO - EOS) * (1.4 - 0.4 * ES / EOS);
	
				// Calculate TETA by averaging water available in each layer
				double TETA = 0;
				for(int Z = 0; Z < ZRAC_ && Z < layers; Z++){
					int H = 0; while(Z >= EPC[H]) H++;
					double availableWater = HUR[Z] < HMIN[soilType][H] ? 0 : HUR[Z] - HMIN[soilType][H];
					TETA += availableWater;
				}
				TETA /= ZRAC_;
				// Eqution 24
				// Threshold for TETA, above which transpiration is maximal
				double TETSTOMATE = (1 / 40) * Math.log((EOP / (2 * Math.PI * CUMLRACZ_ * PSISTO * 10))
											* Math.log(Math.sqrt(Math.PI * CUMLRACZ_ / ZRAC_) / RAYON));
				// Eqution 23
				// Actual transpiration (as limited by water available)
				EP = TETA > TETSTOMATE || TETSTOMATE <= 0 ? EOP : EOP * (TETA / TETSTOMATE);

//System.out.println("TETSTOMATE:" + TETSTOMATE);

				// Calcualte water intexes
				SWFAC = EOP <= 0 ? 0 : EP / EOP;
//System.out.println("SWFAC:" + SWFAC + " EOP:" + EOP + " EP:" + EP);

				// Eqution 24
				// threshold of TETA below which TURFAC decreases                      cm3 cm-?
				double TETURG = (1 / 40) * Math.log((EOP / (2 * Math.PI * CUMLRACZ_ * PSITURG * 10))
											* Math.log(Math.sqrt(Math.PI * CUMLRACZ_ / ZRAC_) / RAYON));
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
				double evap = ES / 10; // Conversion from mm to cm		
				double absorb = 0;
				int XMULCH1 = 0;
				int Z = 0;
//System.out.println("evap:" + evap);
				// calculate total root density
				while((evap > 0 || absorb > 0) && (Z <= XMULCH1 + 5 || Z < ZRAC_) && Z < layers){
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
	/*					if(Z < ZRAC_){
							// Add root demand from this layer
							absorb += (EP / ZRAC_) * (LRAC_[Z] / CUMLRACZ_) / 10; // conversion to cm
							if(absorb < HUR[Z]){
								EPZ[Z] = absorb;
								HUR[Z] -= EPZ[Z];
								absorb = 0;
							}
							else{
								// Any remaining moisture to absorb
								// will be done by next layer
								EPZ[Z] = HUR[Z] < HA ? 0 : HUR[Z] - HA
								absorb -= EPZ[Z];
								HUR[Z] = HUR[Z] < HA ? HUR[Z] : HA;
							}
						}
*/						
					}
					Z++;
				}
			}

		
// Print percentage water of each layer
/*
System.out.println("TRR (rain):" + TRR);
for(int Z = 0; Z < 10; Z++){
	int H = 0; while(Z >= EPC[H]) H++;
	float pr = (float)(HUR[Z] / HCC[soilType][H]);
	float p1 = HA / HCC[soilType][H];
	float p2 = HMIN[soilType][H] / HCC[soilType][H];
	
	System.out.print("|");
//	System.out.print("%:" + pr + "    HUR:" + HUR[Z] + " HCC:" + HCC[soilType][H] + " HMIN:" + HMIN[soilType][H]  + " SWC:" + (HCC[soilType][H] - HMIN[soilType][H]));
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
waterGraph.addData("Rainfall", TRR);
waterGraph.addData("Evaporation", ES);
waterGraph.addData("Transpiration", EP);
waterGraph.addData("Drainage", DRAIN);
		}

		
		// THERMAL MODULE
		// ---------- Global parameters -------------------------
		// RA          aerodynamic resistance                                            sm-' (30)
		// ---------- Climatic parameters -----------------------
		// TMOY        mean temperature                                                    "C
		// TRG         global solar radiation                                              MJ m-2 day-l
		// ---------- Soil parameters ---------------------------
		// HMIN (1)    minimum water content of the horizon H                             g water g soil-'
		// HCC (1)     water content at the field capacity of the horizon H               g water g soil-'
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

		double TCULT = 0;
		double[] TSOL = new double[layers]; // Temperature of the soil at plant depth
		{
			// Eqution 27
			double ALBSOL = ALBEDO * (0.483 * (HUR[0] - HMIN[soilType][0]) + (HCC[soilType][0] - HUR[0]) / (HCC[soilType][0] - HMIN[soilType][0]));
	
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
				TSOL[Z] = TSOL_[Z] - (AMPLZ / AMPLSURF) * (TCULT_ - TMIN) + 0.1 * (TCULT_ - TSOL_[Z]) + AMPLZ / 2;
			}
thermGraph.addData("Air Temperature", TMOY);
thermGraph.addData("Soil Temperature", TCULT);
thermGraph.addData("Solar Radiation", TRG);

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

		// ROOT GROWTH MODULE ---------------------------------------------		
		// ---------- Global parameters -------------------------
		// ---------- Climatic parameters -----------------------
		// ---------- Management parameters ---------------------
		// IPLT        date of sowing
		// ZLABOUR     depth of ploughing                                                  cm
		// PROFSEM     sowing depth                                                      cm
		// ---------- Soil parameters ---------------------------
		// EPC (H)     thickness of the horizon H                                         cm
		// ---------- Plant parameters --------------------------
		// ZPENTE      depth where the root density is 112 of the surface root density for the reference profile
		// ZPRLIM      maximum depth of the root profile for the reference profile
		// CROIRAC     growth rate of the root front                                  cm degree-day-'
		// TCMIN       minimum temperature of growth                                       OC
		// TCOPT       optimum temperature of growth                                       OC
		// LVOPT       optimum root density                                              CJII root cm-3 soi] (0.5)
		// ---------- Shoot growth inputs -----------------------
		// ---------- Water module inputs -----------------------
		// HUR (Z)     volumetric water content of the layer Z                            mm cm-'
		// ---------- Thermal module inputs ---------------------
		// TCULT        surface temperature in daily average                                OC
		// ---------- Development inputs -----------------------
		// ILEV        day of the stage LEV: emergence
		// ILAX        day of the stage LAX: maximal leaf area index
		// ---------- Internal variables ------------------------
		// SWFAC       stomatal stress index                                             between O and 1
		// ZDEMI       depth where the root density is 112 of the surface root density     cm
		// -----------Temporal variables ------------------------
		// CUMLRACZ    sum of the effective root lengths                              cm root cm-2 soi1
		// ZRAC        root depth
		// LRAC (Z)    cffective root density in the layer Z                             cm root cm-' soi1
		// ---------- Outputs -----------------------------------
		
		double ZRAC = ZRAC_;
		double CUMLRACZ = CUMLRACZ_;
		double[] LRAC = new double[layers];
		for(int Z = 0; Z < layers; Z++) LRAC[Z] = LRAC_[Z];
		{
			// TODO: Where do these come from?

			// Eqution 17
			if(ILEV >= 0 && ILAX < 0){
				int H = 0; while((int)ZRAC_ >= EPC[H]) H++;
				int PFZ = HUR[(int)ZRAC_] > HMIN[soilType][H] ? 1 : 0;
				double DELTAZ = CROIRAC * (Math.min(TCULT, TCOPT) - TCMIN) * PFZ;
				ZRAC = ZRAC + DELTAZ;
				ZRAC = ZRAC > layers - 1 ? layers - 1 : ZRAC;
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
/*
System.out.println("ROOT MODULE");
for(int Z = 0; Z < layers; Z++){
	System.out.print(Z == (int)ZRAC ? "+" : "|");
	int pl = (int)(LRAC[Z] * 300);
	for(int i = 0; i < pl; i++)
		System.out.print("#");
	System.out.println();
//	System.out.println(LRAC[Z]);
}
*/
		}		


		// NITROGEN MODULE
		// ---------- Global parameters -------------------------
		// TRR         precipitation                                                       mm day-'
		// ---------- Climatic parameters -----------------------
		// CONCRR      nitrogen concentration of precipitation                        kg mm-'
		// ---------- Management parameters ---------------------
		// ANIT        fertilization table                                            kg N lia-l day-l
		// CONCIRR     nitrogen concentration of irrigating water                     kg mm-'
		// EFFN        fertilization efficiency                                           sans dimension
		// QRES
		// JULTRAV
		// PROFTRAV    depth of residue incorporation                                    cm
		// CSURNRES    C/N ratio of residues                                          sans dimension
		// ---------- Soil parameters ---------------------------
		// NORG
		// ARGI        percentage of clay in the surface layer                        %
		// CALC        percentage of limestone in the surface layer                   76
		// PROFHUM     humification depth                                                cm
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

		double INNS = 0;
		double QNPLANTE = 0;
		double QLES = 0;
		{
			
			// TODO: Only mention about this is that it is constant, no idea about the actual value!
			float YRES = 1;//          carbon assimilation yield of the microflora                         g g-'

			// Equation 33
			// daily potential rate of mineralizalion                          day-'
			double K2POT = 0.21 / ((12.5 + ARGI) * (54.5 + CALC));

			// Eqution 32
			// potential rate of mineralization per layer at the reference temperature and water content                                       kgN ha-' cm-' day-'
			double VPOT = K2POT * NHUM;
			
//System.out.println("ARGI:" + ARGI + "\tCALC:" + CALC + "\tVPOT:" + VPOT + "\tK2POT:" + K2POT + "\tNHUM:" + NHUM);

			double[] VMINH = new double[TSOL.length];
			double dCRES_sum = 0;
			double dCBIO_sum = 0;
			double dCHUM_sum = 0;
			for(int Z = 0; Z < PROFHUM; Z++){
				int H = 0; while(Z >= EPC[H]) H++;
				// Eqution 42
				// Thermal factors
				double FTH = Math.exp(0.115 * (TSOL[Z] - 15));
//System.out.println("FTH[" + Z + "]:" + FTH + "\tTSOL[" + Z + "]:" + TSOL[Z]);
				// Equation 44
				// Water content of the layer
				double FH = 0.2 + 0.8 * (HUR[Z] - HMIN[soilType][H]) / (HCC[soilType][H] - HMIN[soilType][H]);
				FH = FH < 0 ? 0 : FH;
//System.out.println("FH[" + Z + "]:" + FH + "\tHUR[" + Z + "]:" + HUR[Z] + "\tHMIN:" + HMIN[soilType][H] + "\tHCC:" + HCC[soilType][H]);
				
				// Eqution 43
				// temperature correction factor for the computation of residues mineralization                                                     sans dimension
				double FTR = -0.566 + 0.26 * Math.exp(0.9125 * TSOL[Z] / 15);
				FTR = FTR <= 0 ? 0 : Math.pow(FTR, 1.026);
//System.out.println("FTR[" + Z + "]:" + FTR + "\tTSOL[" + Z + "]:" + TSOL[Z]);

				// Eqution 31
				// rate of humus mineralization per layer                             kgN ha-l cm-' day-'
				VMINH[Z] = VPOT * FTH * FH;
//System.out.println("TSOL[Z]:" + TSOL[Z] + "\tVMINH[" + Z + "]:" + VMINH[Z]);
				// Eqution 37
				// decaying rate of the residues                                     day-'
				double KR = (29.6 / (126 + CSURNRES)) * FTR * FH;

				// Equation 38
				// mortality rate of tlie microilora                                 day-'
				double KB = 0.0112 * FTR + FH;

//System.out.println("KR:" + KR + "\tKB:" + KB);

				// Daily change in three carbon pools
				// Eqution 34
				// dCRES (Z)   daily changes in the C content of the residues pool            kg ha-' day-l
				double dCRES = -KR * CRES[Z];
				CRES[Z] += dCRES;
				dCRES_sum += dCRES * WR;

				// Eqution 41
				double CSURNBIO = 17.1 * CSURNRES / (12.3 + CSURNRES);
				// TODO: Is this so??
				double WB = CSURNBIO;
				// Eqution 35
				// dCBIO (Z)   daily changes in the C content of the biomass pool             kg ha-' day-'
				double dCBIO = YRES * KR * CRES[Z] - KB * CBIO[Z];
				CBIO[Z] += dCBIO;
				dCBIO_sum += dCBIO * WB;
//System.out.println("dCBIO:" + dCBIO + "\tYRES:" + YRES + "\tKR:" + KR + "\tCRES[Z]:" + CRES[Z] + "\tKB:" + KB + "\tCBIO[Z]:" + CBIO[Z]);
				// Eqution 39
				// Humification rate
				double HRES = 0.024 * Math.sqrt(CSURNRES);
				// Eqution 36
				// dCHUM (Z)   daily changes in the C content of the humus pool               kg ha-' day-'
				double dCHUM = HRES * KB * CBIO[Z];
				dCHUM_sum += dCHUM * WH;
				
				// TODO: I'm assuming that this fixed nitrogen is added to the pool
//System.out.print("(1) AZO[" + Z + "] before:" + AZO[Z]);
				AZO[Z] = -(dCRES * WR + dCBIO * WB + dCHUM * WH);
				// TODO: This flux shouldn't result in N values below 0, why is it happening??
				AZO[Z] = AZO[Z] < 0 ? 0 : AZO[Z];
//System.out.println("\tAZO after:" + AZO[Z] + "\tdCRES:" + dCRES + "\tWR:" + WR + "\tdCBIO:" + dCBIO + "\tWB:" + WB + "\tdCHUM:" + dCHUM + "\tWH:" + WH);
//System.out.println("dCRES:" + dCRES + "\tdCBIO:" + dCBIO + "\tdCHUM" + dCHUM);
//System.out.println("dCRES:" + dCRES + "\tCRES:" + CRES[Z] + "\tdCBIO:" + dCBIO + "\tCBIO:" + CBIO[Z]);
/*
if(Z == 0){
graph.addData("KB", KB);
graph.addData("KR", KR);
}
*/
			}
//graph.addData("CRES", CRES[0]);
//graph.addData("CBIO", CBIO[0]);
//System.out.println();		
			// Eqution 40
			// VMINR       rate of residues organization                                       kgN ha-' day-'
			double VMINR = -(dCRES_sum + dCBIO_sum + dCHUM_sum);
			// Is this the rate of N production by
			// mineralisation in the soil and should it be added AZO[Z]
			//System.out.println("VMINR:" + VMINR);
			// TODO: What is the use of VMNIR - the layer-by-layer version
			// of this var is added to the carbon pool above
			
			
			
			
			
			
			// CALCULATION OF NITROGEN BALANCE, PLANT UPTAKE AND LEACHING
			// Eqution 45
			// PRECIPN     supply of fertilizer through precipitation or irrigation          kgN ha-' day-'
			double PRECIPN = ANIT * EFFN + TRR * CONCRR + AIRG * CONCIRR;
			// Remove any nitrate leached from bottom of profile
			double toDrain = DRAIN;
			int gap = 1; // number of layers removed from bottom
			while(toDrain > 0 && layers - gap >= 0){
				int H = 0; while(layers - gap >= EPC[H]) H++;
				double leached = 0;
				if(toDrain > HCC[soilType][H]){
					leached = AZO[layers - gap];
//System.out.print("(2) AZO[" + (layers - gap) + "] before:" + AZO[layers - gap]);
					AZO[layers - gap] -= leached;
//System.out.println("\tAZO after:" + AZO[layers - gap] + " leached:" + leached);
					toDrain -= HCC[soilType][H];
					gap++;
				}
				else{
//System.out.print("(3) AZO[" + (layers - gap) + "] before:" + AZO[layers - gap]);
					leached = AZO[layers - gap] * toDrain / HCC[soilType][H];
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
					double nFlow = AZO[Z - gap] * (EPZ[Z] / HCC[soilType][H]);
//System.out.println("nFlow:" + nFlow + "\t:AZO[Z]" + AZO[Z] + "\tAZO[Z - gap]:" + AZO[Z - gap] + "\tEPZ[Z]:" + EPZ[Z] + "\tHCC[soilType][H]" + HCC[soilType][H] + "\tEPZ/HCC:" + EPZ[Z] / HCC[soilType][H]);
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
				// TODO: HCC may need to be converted from mm to cm
				double FLUXSOL = DIFN * (HUR[Z] / HCC[soilType][H]) * Math.sqrt(LRAC[Z] + EPZ[Z]) * CONCN;
				// Eqution 49
				// Absorption of nitrates by the roots
				double FLUXRAC = CONCN * LRAC[Z] * (VMAX1 / (KMABS1 + CONCN) + VMAX2 / (KMABS2 + CONCN));
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
//System.out.println("DEMANDE:" + DEMANDE + "\tADILMAX:" + ADILMAX + " DLTAMS:" + DLTAMS + "\tMASEC:" + MASEC + "\tBDILMAX:" + BDILMAX);	
			// Total nitrogen flux as per demand and maximal possible flow
			double nFlow = Math.min(DEMANDE, nSupplyTotal);
			// If demand is too high then supply is limiting factor
			// and so absorbtion will be proportional to supply
			if(nSupplyTotal <= DEMANDE){
				for(int Z = 0; Z < layers; Z++){
					AZO[Z] -= nSupply[Z];
				}
			}
			// Otherwise there is sufficient soil nitrogen to meet
			// demand and so absorbtion in each layer will be proportional
			// to supply, but limited by demand
			else{
				for(int Z = 0; Z < layers; Z++){
					AZO[Z] -= nSupply[Z] * DEMANDE / nSupplyTotal;
				}
			}
//System.out.println("nFlow:" + nFlow + " DEMANDE:" + DEMANDE + " nSupplyTotal:" + nSupplyTotal);	
		
			// amount of nitrogen taken up by the plant                          kgN ha-'
			QNPLANTE = QNPLANTE_ + nFlow;
			// Eqution 50
			// Plant nitrogen content
			// nitrogen concentration of the plant                            gN g dry matter'
			double CNPLANTE = MASEC == 0 ? 10 * QNPLANTE : 10 * QNPLANTE / MASEC;
			// Eqution 51
			// Critical nitrogen content below which growth rate decreases
			double NC = ADIL * ((MASEC < 1) ? 1 : Math.pow(MASEC, -BDIL));
			// Eqution 52
			// Nitrogen nutrition index
			// TODO: This small constant prevents INN from being exactly 0, otherwise
			// lack of nigrogen means plants don't grow, lack of growth means lack of nitrogen!
			double INN = CNPLANTE == 0 ? 0.00001 : NC == 0 ? CNPLANTE : CNPLANTE / NC;
			// Nitrogen stress index
			INNS = Math.min(1, INN);
	
//System.out.println("INNS:" + INNS + " INN:" + INN + " CNPLANTE:" + CNPLANTE + " QNPLANTE:" + QNPLANTE + " MASEC:" + MASEC + " NC:" + NC);

//System.out.println("IGER:" + IGER + "\tILEV:" + ILEV + "\tIAMF:" + IAMF + "\tILAX:" + ILAX + "\tIDRP:" + IDRP + "\tISEN:" + ISEN + "\tIMAT:" + IMAT + "\tIREC:" + IREC);
if(DEBUG) System.out.println("Day: " + I + " \tYield: " + Utils.roundString(MAGRAIN / 100) + " t/ha\tLeaching: " + Utils.roundString(QLES) + " kgN/ha/day" + "\n");
nitrateGraph.addData("Nitrate Input", PRECIPN);
nitrateGraph.addData("Leaching", QLES);
			
			
			
// ASCII VISUALISATION
if(!DEBUG){
String phase = "";			
if(IGER < 0) phase = "planted, awaiting germanation";
else if(ILEV < 0) phase = "germanated, epicotile extension";
else if(IAMF < 0) phase = "emergence, initial leaf growth";
else if(ILAX < 0) phase = "maximal leaf growth";
else if(IDRP < 0) phase = "leaf maturity, shoot elongation";
else if(ISEN < 0) phase = "grain filling";
else if(IMAT < 0) phase = "leaf sensenence";
else if(IREC < 0) phase = "grain drying";
else phase = "ready for harvest";
System.out.println("Day: " + I + " \tYield: " + Utils.roundString(MAGRAIN / 100) + " t/ha\tLeaching: " + Utils.roundString(QLES) + " kgN/ha/day\tStatus: " + phase);
int h = (int)(MASEC * 1.2) + 1;
for(int i = 0; i < 15 - h; i++) System.out.println();
if(LAI > 0){
	for(int i = 0; i < h; i++){
		int l = (int)(LAI * 10);
		for(int j = 0; j < 66; j++)
			System.out.print(j == 33 ? "#" : i % 2 == 1 || j < 33 - l || j > 33 + l ? " " : "~");
		System.out.println();
	}
}
else System.out.println();
for(int Z = 0; Z < 15; Z++){
	int H = 0; while(Z >= EPC[H]) H++;
	float pr = (float)(HUR[Z] / HCC[soilType][H]);
	double p1 = HA / HCC[soilType][H];
	double p2 = HMIN[soilType][H] / HCC[soilType][H];
	float pf = (float)(EPZ[Z] / HCC[soilType][H]);
//	System.out.print("%:" + pr + "    HUR:" + HUR[Z] + " HCC:" + HCC[soilType][H] + " HMIN:" + HMIN[soilType][H]  + " SWC:" + (HCC[soilType][H] - HMIN[soilType][H]));
	System.out.print("|");
	for(int i = 0; i < 10; i++)
		System.out.print(i >= (pr * 10) ? " " : i == (int)(p1 * 10)? "R" : i == (int)(p2 * 10)? "M" : "#");
	System.out.print("|");
	for(int i = 0; i < 10; i++)
		System.out.print(i >= (pf * 10) ? " " : "v");
	int pl = (int)(LRAC[Z] * 100);
	System.out.print("|");
	for(int i = 0; i < 20; i++)
		System.out.print(i < 10 - pl || i >= 10 + pl ? " " : "¥");
	System.out.print("|");
	for(int i = 0; i < AZO[Z]; i++)
		System.out.print("N");
//		System.out.print(AZO[Z]);
	System.out.println();
}
System.out.println();
}
/*
System.out.println("TRR (rain):" + TRR);
System.out.println("INNS (n stress):" + INNS);
System.out.println("Leached Nitrate:" + QLES);
System.out.println("MASEC:" + MASEC);
*/
		}

		// DEVELOPMENT MODULE
		// Establishes the current stage of development
		// ---------- Climatic parameters -----------------------
		// LATITUDE    latitudinal position of the crop                                  degrees and tenths
		// ---------- Management parameters ---------------------
		// IPLT        date of sowing
		// ---------- Plant parameters ---------------------------
		// TDMIN       minimum threshold temperature for development                       OC
		// TDMAX       maximum threshold temperature for development                       OC
		// STAMFLAX    sum of development units hetween the stages AMF and LAX           degree-day
		// STDRPSEN    sum of development units betweeii the stages DRP and SEN          degree-day
		// STLAXDRP    sum of development units between the stages LAX and DRP           degree-day
		// STLEVAMF    sum of development units between the stages LEV and AMF           degree-day
		// STMATREC    sum of development units between the stages MAT and REC           degree-day
		// STPLTGER    sum of development units allowing germination                     degree-day
		// STSENMAT    sum of development units between the stages SEN and MAT           degree-day
		// PHOSAT      saturating photoperiod                                            hour
		// PHOBASE     base photoperiod                                                  hour
		// TGMIN       minimum threshold temperature used in the emergence stage           OC
		// BELONG      paranieter of the curve of coleoptile elongation               degree-day-'
		// CELONG      parameter of the curve of coleoptile elongation                sans dimension
		// H20GRAIN    water content of the grain when harvested                          g water g fresh matter grain-'
		// DESSECGRAIN drying rate of the grain                                       g water g fresh matter' OC-'
		// JVC         number of vernalizing days
		// ELMAX       maximum elongation of the coleoptile in darkness condition         cm
		// ---------- Soil parameters ---------------------------
		// ALBEDO      albedo of the barc dry soil                                    sans dinicnsion
		// ---------- Shoot growth inputs -]---------------------
		// ---------- Water module inputs -----------------------
		// H20GRMAT    water content of the grain at physiological maturity               g water g fresh matter grain-'
		// ---------- Thermal module inputs ---------------------
		// TSOL (Z)    temperature of the soi1 at the depth Z                              OC		
		// TCULT       surface temperature in daily average                                OC
		// -----------Temporal variables ------------------------
		// devUnits    generic units of development
		// JVI         cumulutive vernalizing days
		// ---------- Outputs -----------------------------------
		// IGER        day of the stage GER: germanation
		// ILEV        day of the stage LEV: emergence
		// IAMF        day of the stage AMF: maximal acceleration of leaf growth, end of juvenile phase
		// ILAX        day of the stage LAX: maximal leaf area index
		// IDRP        day of the stage DRP: beginning of grain filling
		// ISEN        day of the stage SEN: beginning of net senescence
		// IMAT        day of the stage MAT: pliysiological maturity
		// IREC        day of the stage REC: harvest

		{		
boolean PRINT_PHASE = false;

			// Accumulated vernalization needs to be calculated for 
			// winter wheat between germanation and grain filling
			if(cropType == WHEAT && IPLT >= 243 && IGER >= 0 && IDRP < 0){
				// Eqution 4
				JVI += 1 - 0.4 * Math.pow(1 - (TCULT / 6.5), 2);
			}

			// Calculate development units incrament
			if(IGER < 0){ // For period up to germanation
				int H = 0; while(PROFSEM >= EPC[H]) H++;
				int PFZ = HUR[PROFSEM] > HMIN[soilType][H] ? 1 : 0;
//System.out.println("TSOL[PROFSEM]:" + TSOL[PROFSEM] + " TGMIN:" + TGMIN + " PFZ:" + PFZ + " HUR[PROFSEM]:" + HUR[PROFSEM] + " HMIN[H]:" + HMIN[soilType][H] + " devUnits:" + devUnits);
				devUnits += (TSOL[PROFSEM] - TGMIN) * PFZ;
			}
			else if(ILEV < 0) // Period up to emergence
				devUnits += TSOL[PROFSEM] - TGMIN;
			else if(IREC < 0){ // Rest of development up to harvest
				// Calculation of basic development unit (degrees) for this day
				// Eqution 3
				double UDEVCULT = TCULT < TDMIN ? 0 : TCULT < TDMAX ? TCULT - TDMIN : TDMAX - TDMIN;

				if(IDRP < 0){ // Period between emergence and grain filling
					// Development for wheat
					if(cropType == WHEAT){
						// develipment incrament
						double inc = UDEVCULT;

						// Photoperiod PHOI is related to day of year and latitude
						// Can be calculated here: http://www.sci.fi/~benefon/sol.html
						// But should be calculated  properly - TODO
						double PHOI = 12;
						// Eqution 6
						double RFPI = (PHOI - PHOBASE) / (PHOSAT- PHOBASE);
						RFPI = RFPI < 0 ? 0 : RFPI > 1 ? 1 : RFPI;	
						inc *= RFPI;

						// Wheats planted on or after 1st September are
						// assumed to be winter wheat
						if(IPLT >= 243){
							// Eqution 5
							// Vernalizing status of crop, reches 1 when
							// days since germanation reaches JVC
							double RFVI =
								JVI < 7 ? 0 :
									JVI > JVC ? 1 :
										(JVI - 7) / (JVC - 7);
							inc *= RFVI;
						}
						devUnits += inc;
					}
					else // Development for MAIZE
						// If this is wheat then just incrament by UDEVCULT
						devUnits += UDEVCULT;
				}
				else // Period after grain filling starts
					// Then simply incrament by UDEVCULT
					devUnits += UDEVCULT;


			}
		
			// Incrament phase if thresholds reached
			if(IGER < 0){ // Sgate IPLT to IGER
				if(devUnits >= STPLTGER){
					devUnits = 0;
					IGER = I;
//System.out.println("devUnits:" + devUnits + " STPLTGER:" + STPLTGER);
if(PRINT_PHASE) System.out.println("Phase: GER");
				}
			}
			else if(ILEV < 0){ // Stage IGER to ILEV
				// Calculate current length
				// Eqution 2
				// ELONG       coleoptile elongation                                              cm
				double ELONG = ELMAX * (1 - Math.exp(-Math.pow(BELONG * devUnits, CELONG)));
				// Wait for coleoptile to break through the surface
				if(ELONG >= PROFSEM){
					devUnits = 0;
					ILEV = I;
if(PRINT_PHASE) System.out.println("Phase: LEV");
				}				
			}
			else if(IAMF < 0){ // Stage ILEV to IAMF
				if(devUnits >= STLEVAMF){
					devUnits = 0;
					IAMF = I;
if(PRINT_PHASE) System.out.println("Phase: AMF");
				}
			}
			else if(ILAX < 0){ // Stage IAMF to ILAX
				if(devUnits >= STAMFLAX){
					devUnits = 0;
					ILAX = I;
if(PRINT_PHASE) System.out.println("Phase: LAX");
				}
			}
			else if(IDRP < 0){ // Stage ILAX to IDRP
				if(devUnits >= STLAXDRP){
					devUnits = 0;
					IDRP = I;
if(PRINT_PHASE) System.out.println("Phase: DRP");
				}
			}
			else if(ISEN < 0){ // Stage IDRP to ISEN
				if(devUnits >= STDRPSEN){
					devUnits = 0;
					ISEN = I;
if(PRINT_PHASE) System.out.println("Phase: SEN");
				}
			}
			else if(IMAT < 0){ // Stage ISEN to IMAT
				if(devUnits >= STSENMAT){
					devUnits = 0;
					IMAT = I;
if(PRINT_PHASE) System.out.println("Phase: MAT");
				}
			}
			else if(IREC < 0){ // Stage IMAT to IREC
				// Describes the grain drying process after maturity
				// Eqution 7
				// Water content of grain
				double TEAUGRAIN = H20GRMAT - DESSECGRAIN * devUnits;
				// TODO: This enforces harvest when sufficient drying
				// has occured, but we could possibly let the farmer decide
				if(TEAUGRAIN <= H20GRAIN){
					devUnits = 0;
					IREC = I;
if(PRINT_PHASE) System.out.println("Phase: REC");
				}
			}
			else{
				// Grain has been harvested
			}
		}
		// Incrament Day
		I++;

		// Set all prev_day vars
		LAI_ = LAI;  
		MASEC_ = MASEC;
		HUR_ = HUR; //         volumetric water content of the layer Z                            mm cm-'
		SWFAC_ = SWFAC;
		TURFAC_ = TURFAC;
		CUMLRACZ_ = CUMLRACZ; //    sum of the effective root lengths                              cm root cm-2 soi1
		ZRAC_ = ZRAC; // Yesterday's          root depth
		LRAC_ = LRAC; // Yesterday's    cffective root density in the layer Z                             cm root cm-' soi1
		TSOL_ = TSOL;
		TCULT_ = TCULT;
		INNS_ = INNS;
		QNPLANTE_ = QNPLANTE;
	}

	public static void testModel(int type){
		Random rand = new Random(0);
		
		double[] rainfall_edinburgh_2007 = {
			8.8, 4.4, 7, 19, 1.4, 1.8, 12.6, 2, 12.2, 3.2, 35.2, 150.6, 13.8, 18.6, 1.6, 0.6, 1.6, 12.6, 8, 15.6, 3, 0.6, 0, 0, 0, 0, 0, 0.2, 0, 0, 0, 
			0, 0, 0.2, 0, 0, 0, 0, 0.6, 0.8, 1.8, 4, 1.2, 1.8, 2, 0, 0, 0, 0, 3.8, 0.4, 2.4, 7.4, 0, 0.8, 7, 0, 21.4, 2.2, 
			0.6, 7.6, 1.2, 1.2, 0.4, 2.4, 0, 1.4, 1.2, 0, 12, 8.2, 0, 0, 0.4, 1, 9, 3, 1, 0, 0, 0.4, 0, 0, 0.4, 0, 0, 0, 18.4, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2, 0, 0, 0.8, 0, 0, 0, 0.2, 0, 7.8, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 5, 3.4, 0, 5.4, 10.4, 0.4, 9.2, 4.8, 0.6, 0, 1.8, 1.2, 6.6, 2, 0.2, 0.2, 0, 0, 1.6, 0.2, 1, 5.2, 0, 6, 4.8, 17, 
			0, 0, 1.6, 0, 0, 0, 1.8, 3.2, 0, 0, 0, 1, 13.4, 1.4, 6.4, 7, 4.2, 0, 0.2, 34.6, 0.2, 2.4, 24.6, 10, 5, 0.4, 17.2, 5.4, 1.4, 6.4, 
			27.6, 14.4, 3, 1.4, 5.2, 12.6, 4.2, 0, 0, 0, 0, 1, 13.4, 5.4, 0, 16, 2.4, 0, 2.4, 0, 12.6, 10.4, 0, 0, 0.4, 14, 1.2, 0.2, 0.4, 0, 0, 
			0.2, 0.4, 0, 1.4, 12.8, 5.4, 0.4, 0, 0, 0, 8.6, 6.6, 2.2, 10.2, 3, 0, 0, 0, 1.6, 0, 0, 0, 0, 0, 0.2, 1.2, 0, 0, 0.4, 0.4, 0, 
			0, 0, 0, 0, 0, 0, 0, 1.2, 0, 0, 0.6, 0.2, 0.6, 21, 0, 0.4, 0, 1.2, 0.6, 0, 0.8, 19.8, 0.6, 0, 0, 3, 0, 0, 0, 0, 
			9, 0, 0, 0, 0, 0, 17.4, 0, 0, 0.2, 0.4, 0, 1.2, 2.4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2, 0, 15, 1.8, 1.2, 0.2, 0.6, 0, 
			0, 0, 0, 0, 0, 8.4, 0.6, 0.2, 0.4, 4.4, 0.4, 0, 0, 0, 4.2, 9.2, 9.4, 3.8, 39.6, 0.4, 0, 1.6, 0, 0, 0.4, 3.6, 2.4, 7.8, 1.2, 1.2, 
			0, 2.4, 8.4, 6.2, 0.4, 20.4, 1.2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2, 0, 0, 0.6, 0, 3, 0.8, 0, 10, 22.6, 0, 1.2, 3
		};

		double[] temperature_edinburgh_2007 = {
			3.95, 5.17, 9.43, 7.77, 7.62, 6.46, 7.37, 6.02, 8.4, 4.6, 6.03, 9.06, 7.39, 6.29, 7.73, 2.76, 3.83, 2.61, 6.55, 4.47, 2.47, 2.14, 1.27, 3.08, 2.77, 6.2, 6.13, 8.58, 7.43, 6.6, 9.93, 
			9.12, 7.02, 5.42, 4.49, 1.9, 0.11, 0.28, 0.58, 0.36, 2.07, 4.17, 5.12, 5.97, 5.19, 8.32, 7.67, 6.46, 3.99, 5.64, 8.02, 5.45, 7.07, 8.3, 7.78, 5.93, 4.56, 5.63, 6.75, 
			5.02, 4.06, 5.76, 5.55, 6.72, 7.55, 6.89, 6.91, 5.21, 9.51, 8.2, 6.36, 6.8, 8.61, 7.64, 6.6, 8.63, 2.27, 2.18, 1.14, 1.94, 6.54, 4.95, 4.35, 5.57, 6.31, 4.72, 5.98, 5.23, 6.32, 5.87, 
			5.44, 5.26, 6.19, 9.93, 10.76, 8.4, 8.96, 8.67, 10.75, 10.59, 10.62, 11.17, 7.5, 10.43, 13.44, 10.3, 8.06, 7.72, 8.32, 6.81, 11.97, 12.87, 13, 14.37, 12.06, 9.96, 8.63, 8.22, 7.32, 9.02, 
			7.43, 7.56, 7.36, 7.76, 11.25, 9.9, 9.41, 10.2, 9.92, 8.59, 7.3, 7.28, 6.99, 8.36, 8.89, 9.14, 11.74, 11.68, 10.15, 9.87, 11, 10.92, 12.67, 12.83, 9.01, 8.5, 7.75, 7.81, 7.29, 9.55, 12.26, 
			11.87, 14.84, 14.62, 11.6, 10.48, 10.35, 10.74, 12.21, 14.71, 13.87, 15.95, 10.96, 9.38, 9.22, 9.74, 9.44, 12.68, 11.68, 12.73, 14.72, 12.36, 11.95, 12.4, 10.88, 10.74, 10.82, 9.58, 9.51, 11.14, 12, 
			13.93, 13.85, 14.01, 14.66, 13.89, 12.8, 13.46, 13.35, 13.69, 12.82, 13.76, 14.03, 13.5, 14.63, 14.59, 14.69, 15.41, 12.95, 11.14, 11.66, 10.55, 12.47, 13.98, 15.11, 14.98, 13.58, 13.81, 13.22, 12.02, 12.93, 14.83, 
			15.18, 13.84, 14.94, 16.69, 12.39, 13.18, 11.71, 13.89, 13.98, 15.04, 14.46, 13.41, 12.95, 12.43, 13.32, 11.72, 12.62, 13.91, 13.52, 13.72, 15.45, 15.7, 15.46, 13.39, 12.71, 11.95, 12.73, 15.2, 14.7, 13.99, 13.78, 
			11, 11.61, 16.34, 15.65, 14.89, 14.9, 13.8, 13.88, 13.07, 15.09, 14.85, 12.25, 10.61, 13.52, 8.47, 7.59, 12.39, 11.62, 11.36, 12.49, 14.71, 10.89, 9.94, 7.11, 6.82, 8.86, 10.13, 10.8, 11.53, 11, 
			12.15, 11.16, 10.83, 12.26, 11.51, 10.48, 10.18, 11.67, 14.77, 14.35, 13.67, 13, 11.8, 10.05, 8.16, 6.52, 9.96, 10.32, 9.63, 9.59, 7.49, 5.42, 8.37, 10.16, 11.75, 10.92, 7.29, 8.84, 12.56, 14.16, 13.49, 
			9.42, 7.82, 9.22, 8.02, 10.95, 7.66, 6.28, 11.05, 5.64, 2.35, 6.88, 6.45, 5.69, 7.67, 9.19, 5.04, 6.36, 6.42, 6.1, 4.63, 1.12, 6.16, 4.62, 4.69, 9.1, 8.71, 5.78, 8.38, 5.35, 4.77, 
			4.14, 8.94, 10.31, 8.28, 5.24, 2.72, 5.09, 3.52, 4.06, 6.72, 6.37, 5.18, 2.79, -0.73, -0.26, 3.07, -0.47, 0.34, -2.55, 1.38, 5.07, 6.05, 2.23, 7.57, 8.81, 8.78, 3.88, 3.9, 7.09
		};
		
		double[] mintemp_edinburgh_2007 = {
			2.04, 2.73, 5.41, 6.42, 6.36, 5.57, 4.1, 3.84, 5.15, 2.01, 3.68, 5.57, 3.48, 2.93, 4.13, 0.6, 1.58, -0.13, 4.82, 1.35, 1.42, -0.12, -0.68, 2.11, 0.79, 4.3, 5.14, 6.75, 5.77, 4.75, 7.87, 
			8.43, 5.31, 1.32, 2.4, -0.42, -2.15, -1.6, -1.69, -2.35, 0.96, 2.76, 3.52, 3.68, 3.29, 5.35, 5.21, 3.65, 0.01, 3.29, 3.77, 3.77, 4.46, 5.7, 6.06, 5.11, 1.88, 1.58, 3.84, 
			2.53, -0.35, 3.64, 0.43, 4.33, 4.29, 4.95, 4.89, 3.31, 6.62, 5.28, 4.52, 4.86, 5.97, 3.71, 4.04, 5.37, -0.09, 0.01, -0.97, -2.09, 3.98, 2.57, -0.58, 4.17, 3.72, 2.04, 4.59, 3.05, 5.54, 4.59, 
			2.93, -0.35, 3.94, 2.01, 6.1, 5.09, 5.54, 6.36, 8.75, 8.49, 7.67, 5.08, 5.97, 5.6, 7.15, 6.69, 5.05, 4.36, 4.85, 4.3, 9.11, 10.61, 11.43, 11.79, 8.23, 6.59, 5.83, 4.93, 5.97, 5.32, 
			3.77, 4.16, 4.39, 6.69, 7.21, 7.66, 7.34, 7.44, 6.46, 6.46, 5.64, 6.69, 6.26, 4.82, 4.96, 7.24, 8.36, 7.93, 7.05, 7.11, 4.69, 8.33, 8.29, 9.08, 5.77, 5.44, 5.37, 4.73, 6.03, 6.92, 8.59, 
			8.69, 10.49, 11.3, 10.21, 8.92, 8.95, 9.15, 9.9, 12.18, 12.28, 12.71, 9.64, 7.31, 8.33, 8.13, 8.62, 9.54, 9.31, 10.62, 11.98, 10.57, 10.8, 10.62, 9.93, 9.18, 7.64, 6.71, 7.6, 7.34, 6.79, 
			12.18, 11.66, 11.21, 12.16, 11.34, 10.66, 11.02, 9.8, 9.28, 8.36, 11.08, 10.79, 10.85, 12.44, 9.08, 11.69, 11.44, 10.13, 10.07, 10.16, 9.54, 9.31, 7.93, 11.41, 12.05, 11.36, 10.85, 10.36, 7.93, 8.23, 10.46, 
			11.56, 10.66, 8.92, 13.53, 11.26, 10.23, 8.75, 9.08, 8.65, 10.72, 13.36, 11.79, 10.29, 9.25, 10.59, 9.38, 9.21, 11.31, 12.02, 9.51, 7.93, 12.44, 11.89, 10.72, 9.77, 9.38, 9.9, 12.77, 11.95, 10.89, 10.52, 
			6.69, 6.06, 14.02, 13.36, 11.98, 11.27, 12.25, 10.59, 7.87, 12.9, 12.97, 6.75, 3.32, 9.74, 5.18, 3.03, 9.9, 9.77, 9.57, 10.39, 11.75, 9.57, 7.67, 4.36, 3.65, 6.98, 8.72, 8.06, 6.13, 6.49, 
			9.08, 7.34, 6.06, 6.95, 9.02, 7.93, 8.95, 6.95, 10.89, 12.94, 11.95, 11, 10.1, 7.87, 6.1, 1.48, 5.87, 7.97, 7.57, 5.41, 3.09, 0.99, 4.1, 8.92, 7.01, 7.44, 6.03, 5.25, 11.2, 12.94, 12.38, 
			5.64, 4.13, 6.1, 5.38, 9.74, 2.7, 2.93, 8.59, 1.22, 0.43, 3.87, 4.13, 3.38, 4.06, 6.69, 3.64, 4.43, 5.14, 5.18, 0.83, -1.14, 1.91, 3.25, 2.3, 6.42, 5.51, 4.43, 4.46, 3.97, 3.41, 
			2.83, 3.08, 7.05, 6.23, 3.09, 0.76, 3.58, 1.62, 1.42, 4.7, 4.86, 4.2, -0.84, -2.94, -3.99, -0.97, -3.33, -2.45, -6.18, -5.72, 1.42, 1.02, -0.68, 4.08, 6.52, 5.31, 2.57, 0.93, 4.59 
		};
		
		double[] solar_rad_edinburgh_2007 = {
			0.01, 0.02, 0.01, 0.02, 0.02, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.01, 0.02, 0.02, 0.02, 0.02, 0.02, 0.01, 0.01, 0.02, 0.01, 0.03, 0.02, 0.02, 0.03, 0.03, 0.03, 0.02, 0.03, 0.03, 0.02, 
			0.02, 0.04, 0.04, 0.03, 0.04, 0.04, 0.04, 0.03, 0.02, 0.01, 0.02, 0.02, 0.04, 0.05, 0.02, 0.02, 0.06, 0.06, 0.01, 0.05, 0.03, 0.01, 0.06, 0.02, 0.01, 0.05, 0.02, 0.06, 
			0.04, 0.06, 0.08, 0.02, 0.07, 0.08, 0.08, 0.07, 0.08, 0.05, 0.03, 0.09, 0.08, 0.05, 0.06, 0.06, 0.06, 0.09, 0.07, 0.08, 0.09, 0.05, 0.08, 0.12, 0.06, 0.09, 0.06, 0.02, 0.02, 0.02, 0.1, 
			0.12, 0.12, 0.1, 0.15, 0.15, 0.1, 0.15, 0.09, 0.1, 0.09, 0.1, 0.15, 0.07, 0.13, 0.1, 0.16, 0.13, 0.1, 0.09, 0.06, 0.07, 0.11, 0.06, 0.16, 0.09, 0.16, 0.15, 0.17, 0.1, 0.18, 
			0.16, 0.17, 0.17, 0.1, 0.12, 0.15, 0.14, 0.17, 0.13, 0.1, 0.04, 0.05, 0.04, 0.17, 0.22, 0.06, 0.07, 0.14, 0.18, 0.14, 0.18, 0.18, 0.15, 0.1, 0.18, 0.18, 0.15, 0.12, 0.07, 0.08, 0.13, 
			0.14, 0.11, 0.1, 0.06, 0.08, 0.14, 0.11, 0.07, 0.17, 0.06, 0.18, 0.03, 0.09, 0.05, 0.06, 0.02, 0.11, 0.13, 0.04, 0.12, 0.11, 0.07, 0.06, 0.04, 0.07, 0.15, 0.14, 0.12, 0.13, 0.09, 
			0.1, 0.09, 0.12, 0.15, 0.11, 0.09, 0.15, 0.2, 0.22, 0.12, 0.21, 0.1, 0.08, 0.14, 0.15, 0.1, 0.16, 0.12, 0.03, 0.07, 0.06, 0.12, 0.17, 0.15, 0.1, 0.09, 0.19, 0.17, 0.18, 0.17, 0.18, 
			0.09, 0.14, 0.09, 0.18, 0.04, 0.12, 0.14, 0.17, 0.14, 0.09, 0.04, 0.09, 0.14, 0.06, 0.12, 0.13, 0.14, 0.17, 0.02, 0.17, 0.15, 0.13, 0.11, 0.11, 0.12, 0.05, 0.1, 0.13, 0.11, 0.11, 0.06, 
			0.09, 0.1, 0.13, 0.11, 0.13, 0.13, 0.05, 0.13, 0.08, 0.07, 0.07, 0.1, 0.06, 0.04, 0.06, 0.07, 0.13, 0.08, 0.08, 0.08, 0.07, 0.05, 0.08, 0.08, 0.04, 0.02, 0.05, 0.06, 0.1, 0.09, 
			0.03, 0.1, 0.08, 0.08, 0.06, 0.05, 0.02, 0.06, 0.06, 0.06, 0.04, 0.04, 0.04, 0.06, 0.07, 0.05, 0.05, 0.04, 0.03, 0.03, 0.06, 0.06, 0.03, 0.02, 0.03, 0.05, 0.05, 0.03, 0.02, 0.03, 0.03, 
			0.03, 0.02, 0.03, 0.03, 0.04, 0.04, 0.04, 0.02, 0.03, 0.03, 0.03, 0.03, 0.02, 0.02, 0.02, 0.01, 0, 0.01, 0, 0.01, 0.03, 0.02, 0.01, 0.01, 0.01, 0.01, 0.02, 0.01, 0.01, 0.01, 
			0.02, 0.01, 0.01, 0.01, 0.02, 0.01, 0, 0.02, 0.02, 0.01, 0.01, 0.01, 0.01, 0.01, 0.02, 0.01, 0.02, 0.02, 0.01, 0.01, 0.02, 0.01, 0.02, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01
		};
		
		float rainAvg = 5; // Average rainfall
		float rainVar = 2; // Variability of rainfall
		float rainFreq = 1;//0.03f; // Frequency of rain (per day)
		
		// Set up params;
		int IPLT = 120; // Day in year of planting, 0 = Jan 1st, 245 = winter wheat
		// Reference Evapotranspiration (TETP) can be calculated using the Penman-Monteith equation
		// See http://www.fao.org/docrep/X0490E/x0490e08.htm for method
		float TETP = 3.0f; //           reference evapotranspiration                                        mm day-l
		// Crop management parameters
		int PROFSEM = 2;  // sowing depth                                                      cm
		int DENSITE = 4;  // sowing density                                                 pl m-2
		int ZLABOUR = 0;  // depth of ploughing                                                  cm
		// Soil parameters
		float ALBEDO = 0; // albedo of the barc dry soil                                    sans dinicnsion
		int soilType = CLAY_LOAM; //           soilType
//		float HCC = 0; // (H)     water content at the field capacity of the horizon H               g water g soil-'
//		float HMIN = 0; // (H)    minimum water content of the horizon H                             g water g soil-'
		int Q0 = 11; //         parameter of the end of the maximum cvaporntion stage             mm
		float ARGI = 30; //        percentage of clay in the surface layer                        %
		float CALC = 0; //        percentage of limestone in the surface layer                   76			
		STICS model;
		if(type == WHEAT){
			model = new SticsWheat(
					IPLT,  // Day in year of planting, 0 = Jan 1st
					// Constant climate parameters
					TETP, //           reference evapotranspiration                                        mm day-l
					// Crop management parameters
					PROFSEM,  // sowing depth                                                      cm
					DENSITE,  // sowing density                                                 pl m-2
					ZLABOUR,  // depth of ploughing                                                  cm
					// Soil parameters
					ALBEDO, // albedo of the barc dry soil                                    sans dinicnsion
					soilType, //           soilType
					Q0, //         parameter of the end of the maximum cvaporntion stage             mm
					ARGI //        percentage of clay in the surface layer                        %
			);
		}
		else{ // type == MAIZE
			model = new SticsMaize(
					IPLT,  // Day in year of planting, 0 = Jan 1st
					// Constant climate parameters
					TETP, //           reference evapotranspiration                                        mm day-l
					// Crop management parameters
					PROFSEM,  // sowing depth                                                      cm
					DENSITE,  // sowing density                                                 pl m-2
					ZLABOUR,  // depth of ploughing                                                  cm
					// Soil parameters
					ALBEDO, // albedo of the barc dry soil                                    sans dinicnsion
					soilType, //           soilType
					Q0, //         parameter of the end of the maximum cvaporntion stage             mm
					ARGI //        percentage of clay in the surface layer                        %
			);
		}
		float TMOY = 10;     // mean temperature                                                    "C
		float TMIN = TMOY - 2;    // minimum temperature                                                 OC
		float TRR = 0; //        precipitation                                                       mm day-'
		// Constant climate parameters
		// See http://www.bom.gov.au/sat/glossary.shtml for explanation
		// 0.1 KW/m2 = 100 W/m2 = 100 J/m2/s = 100 * 3600 * 24 J/m2/d = 8640000/10E6 MJ/m2/d = 8.64
		float TRG = 8.64f; //            global solar radiation                                              MJ m-2 day-l
		float AIRG = 0; //       irrigation table                                               mm day-l
		float ANIT = 0; //        fertilization table (daily fertilisation rate)                 kg N lia-l day-l
		for(int i = IPLT; i < 365; i++){
			// Temp slowly raises from 10° to 20°
			//TMOY = 15 + (float)i * 10 / 300; // mean temperature  "C
			TMOY = (float)temperature_edinburgh_2007[i % temperature_edinburgh_2007.length];
			//TMIN = TMOY - 2;    // minimum temperature                                                 OC
			TMIN = (float)mintemp_edinburgh_2007[i % mintemp_edinburgh_2007.length];
//			TRR = rand.nextFloat() < rainFreq ? (float)Math.pow(rand.nextFloat() * 2, rainVar) * rainAvg : 0; //        precipitation                                                       mm day-'
			TRR = (float)rainfall_edinburgh_2007[i % rainfall_edinburgh_2007.length];
			TRG = (float)solar_rad_edinburgh_2007[i % solar_rad_edinburgh_2007.length] * 86.4f; // Conversion from KW/m2 to MJ/m2/d: 1KW/m2 = 1000W/m2 = 1000J/m2/s = 3600000 J/m2/h = 3.6 MJ/m2/h = 86.4 MJ/m2/d
			// AIRG        irrigation table                                               mm day-l
			ANIT = 13; // kg fertiliser per day
			model.execDay(
					TMOY,      // mean temperature                                                    "C
					TMIN,      // minimum temperature                                                 OC
					TRR,       // precipitation                                                       mm day-'
					TRG,       // global solar radiation                                              MJ m-2 day-l
					AIRG,      // irrigation table                                               mm day-l
					ANIT       // fertilization table                                            kg N lia-l day-l
			);
		}
		model.laiGraph.repaint();
		model.dmGraph.repaint();
		model.nitrateGraph.repaint();
		model.waterGraph.repaint();
		model.thermGraph.repaint();
	}
	
}
