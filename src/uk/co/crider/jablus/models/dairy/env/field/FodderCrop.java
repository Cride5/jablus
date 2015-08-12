package uk.co.crider.jablus.models.dairy.env.field;

import java.util.Random;

import uk.co.crider.models.dairy.RandomGenerator;

import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.utils.Utils;

/** Represents the fodder crops of WheatCrop and Maize. Mostly based on STICS model */
public abstract class FodderCrop extends Crop{

	public static boolean DEBUG = false;

	
	// Symbol list
	
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
	// ANIT        fertilization table                                            kg N Ha-1 day-1
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
	// MAGRAIN     diy matter of grains                                              g m-2
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
	// SWFAC       stomatal stress index (EP/EOP 0 = max stress, 1 = no stress)      between O and 1 
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
	// TURFAC      turgescence stress index (TETA/TETURG 0 = max stress, 1 = no stress) sans dimension between O and 1
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
	
	// CONSTANTS
	
	// FIXED PARAMETERS

	// Crop management parameters
	private final int IPLT; //        date of sowing
	private final int DENSITE = 4;  // sowing density                                                 pl m-2

	
	// Development Species Parameters
	// TODO: Get these valueds right
	private final float WHEAT_RES_CARBON = 0.5f; // Wheat residues carbon porportion      gC/g
	private final float MAIZE_RES_CARBON = 0.5f; // Maize residues carbon porportion      gC/g
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
//	protected float EXTIN; //       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
//	protected float TCMIN; //         minimum temperature of growth                                       OC
	protected float TCMAX; //     maxirnuin temperature of growth                                   O C
//	protected float TCOPT; //         optimum temperature of growth                                       OC
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

	// Root Growth Species Parameters
	protected int ZPENTE; //         depth where the root density is 112 of the surface root density for the reference profile
	protected int ZPRLIM; //         maximum depth of the root profile for the reference profile
	protected float CROIRAC; //     growth rate of the root front                                  cm degree-day-'
//	private final float RAYON = 0.02f; //     average radius of roots                                           cm (0.02)
	private final float LVOPT = 0.5f; //      optimum root density                                              CJII root cm-3 soi] (0.5)


	
	// FIXED INTERNAL STATE VARIABLES


	
	
	// DYNAMIC INTERNAL STATE VARIABLES
	
	// Global	
	private int I; // Time counter
	// From shoot growth module
	private double LAI_; // Yesterday's leaf area index                                                   mZ leaves       soi1
	/** Returns the leaf area index */	
	public double getLAI(){ return LAI_; }
	private double MASEC_; // Yesterday's aboveground dry matter                                            t ha-'
	/** Returns the total aboveground dry matter */
	public double getMASEC(){ return MASEC_; }
	private double DLTAMS_;
	/** Returns growth rate of the plant (change in mass) */
	public double getDLTAMS(){ return DLTAMS_; }
	// From yield module
	private double[] daysGrowth; // Stores the last NBJGRAIN growth rates
	private int iGr; // Stores index into the daysGrowth ringbuffer
	private double NBGRAINS = 0; //  grain number                                                      grains m-2
	private double H20GRMAT; // water content of the grain at physiological maturity               g water g fresh matter grain-'
	private double MAGRAIN; //    dry matter of grains                                             g m-2
	/** Returns MAGRAIN (mass of grains) in t/ha */
	double getMAGRAIN(){
		// (conversion = * 10,000 / 1,000,000 = / 100
		return MAGRAIN * 0.01; }
	// From root module
	private double CUMLRACZ_; //    sum of the effective root lengths                              cm root cm-2 soi1
	/** Returns sum of effective root length (cm root / cm² soil) */
	public double getCUMLRACZ(){ return CUMLRACZ_; }
	private double ZRAC_; // Yesterday's          root depth
	/** Returns root depth (cm) */
	public double getZRAC(){ return ZRAC_; }
	private double[] LRAC_; // Yesterday's    cffective root density in the layer Z                             cm root cm-' soi1
	/** Returns the effective root density in layer Z */
	public double[] getLRAC(){ return LRAC_; }
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


	public FodderCrop(
			Soil soil, // Soil model
			int cropType, // Crop type, MAIZE or WHEAT
			int IPLT  // Day in year of planting, 0 = Jan 1st
			// Crop management parameters
	){
		super(cropType, soil);
		// Set input parameters
		this.IPLT = IPLT < 0 ? 0 : IPLT;
		// Crop management parameters

		initSpeciesParams();

		daysGrowth = new double[NBJGRAIN];

		// Set initial root front (ZRAC) to sew depth (PROFSEM)
		ZRAC_ = PROFSEM;
		
		LRAC_ = new double[soil.layers];

		I = IPLT;

	}
	
	
	/** Executes a day in the crop model */
	public void execDay(
		double TMOY,      // mean temperature                                                    "C
		double TMIN,      // minimum temperature                                                 OC
		double TRR,       // precipitation                                                       mm day-'
		double TRG       // global solar radiation                                              MJ m-2 day-l
	){
		
		double TCULT = soil.getTCULT();
		double[] TSOL = soil.getTSOL();
		double TURFAC = soil.getTURFAC();
		double INNS = soil.getINNS();
		double SWFAC = soil.getSWFAC();
//		double QNPLANTE = soil.getQNPLANTE();
		
		
		
		
		
		
		
		
		
		
		
		
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
				double DELTAI = (DLAIMAX / (1 + Math.exp(5.5 * (2.2 - ULAI)))) * (TCULT - TCMIN) * Math.min(TURFAC, INNS) * EFDENSITE * DENSITE;
				LAI += DELTAI;
				LAI = LAI < 0 ? 0 : LAI;
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
				double f_TCULT = 1 - Math.pow((TCULT - TCOPT) / (((TCULT < TCOPT) ? TCMIN : TCMAX) - TCOPT), 2);
//System.out.println("f_TCULT:" + f_TCULT + "\tTCULT_:" + TCULT_ + "\tTCOPT:" + TCOPT + "\tTCMIN:" + TCMIN + "\tTCMAX:" + TCMAX);
				// Eqution 11
				// DLTAMS      growth rate of the plant                                       t ha-' day-'
				DLTAMS = (EBMAX * RAINT - 0.0815 * Math.pow(RAINT, 2)) * f_TCULT * SWFAC * INNS / 100;
				// TODO: Assuming DLTAMS cannot be less than 0, ie. plant cannot shrink in mass
				DLTAMS = DLTAMS < 0 ? 0 : DLTAMS;
//System.out.println(" DLTAMS:" + DLTAMS + " EBMAX:" + EBMAX + " RAINT:" + RAINT + " f_TCULT:" + f_TCULT + " SWFAC_:" + SWFAC_ + " INNS_:" + INNS_);
				MASEC = MASEC_ + DLTAMS;

				if(IDRP < 0){
					// record this day's growth for benefit of yield module
					daysGrowth[iGr++ % NBJGRAIN] = DLTAMS;
				}
			}
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
		// IRAZO       nitrogen harvest index                                             gN grain gN plant-'
		// IRCARB      carbon harvest index                                               g grain g-' plant

		double PGRAIN; //    weight of one grain                                               g
//		double QNGRAIN; //   amount of nitrogcn in grains                                      kgN ha-'

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
//			double IRAZO = VITIRAZO * (IDRP < 0 ? 0 : IREC >= 0 && I > IREC ? IREC - IDRP : I - IDRP);
	
			// Eqution 15
			// MAGRAIN     dry matter of grains                                             g m-2
			MAGRAIN = IRCARB * MASEC * 100; // Conversion from tonnes to grams, ha to m2)
			// QNGRAIN     amount of nitrogen in grains                                      kgN ha-'
//			QNGRAIN = IRAZO * QNPLANTE;
	
			// Eqution 16
			// PGRAIN      weight of one grain                                               g
			PGRAIN   = NBGRAINS == 0 || IDRP < 0 ? 0 : Math.min(MAGRAIN / NBGRAINS, PGRAINMAXI);
			
			if(IMAT == I){ // TODO: Assuming that the water content of grains is 50% find out exact content
				H20GRMAT = PGRAIN * 0.5; // water content of the grain at physiological maturity               g water g fresh matter grain-'
			}
//System.out.println("MAGRAIN:" + MAGRAIN + " QNGRAIN:" + QNGRAIN + " NBGRAINS:" + NBGRAINS + " PGRAIN:" + PGRAIN);
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
		double[] LRAC = new double[soil.layers];
		for(int Z = 0; Z < soil.layers; Z++) LRAC[Z] = LRAC_[Z];
		{
			// Eqution 17
			if(ILEV >= 0 && ILAX < 0){
				//int H = 0; while((int)ZRAC_ >= EPC[H]) H++;
				int PFZ = soil.getPFZ((int)ZRAC);
				double DELTAZ = CROIRAC * (Math.min(TCULT, TCOPT) - TCMIN) * PFZ;
				DELTAZ = DELTAZ < 0 ? 0 : DELTAZ;
				ZRAC = ZRAC + DELTAZ;
				ZRAC = ZRAC > soil.layers - 1 ? soil.layers - 1 : ZRAC;
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
for(int Z = 0; Z < soil.layers; Z++){
	System.out.print(Z == (int)ZRAC ? "+" : "|");
	int pl = (int)(LRAC[Z] * 300);
	for(int i = 0; i < pl; i++)
		System.out.print("#");
	System.out.println();
//	System.out.println(LRAC[Z]);
}
*/
		}		


	
//System.out.println("INNS:" + INNS + " INN:" + INN + " CNPLANTE:" + CNPLANTE + " QNPLANTE:" + QNPLANTE + " MASEC:" + MASEC + " NC:" + NC);

//System.out.println("IGER:" + IGER + "\tILEV:" + ILEV + "\tIAMF:" + IAMF + "\tILAX:" + ILAX + "\tIDRP:" + IDRP + "\tISEN:" + ISEN + "\tIMAT:" + IMAT + "\tIREC:" + IREC);
//if(DEBUG) System.out.println("Day: " + I + " \tYield: " + Utils.roundString(MAGRAIN / 100) + " t/ha\tLeaching: " + Utils.roundString(QLES) + " kgN/ha/day" + "\n");
			
			
		

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
			// Accumulated vernalization needs to be calculated for 
			// winter wheat between germanation and grain filling
			if(cropType == Crop.WHEAT && IPLT >= 243 && IGER >= 0 && IDRP < 0){
				// Eqution 4
				JVI += 1 - 0.4 * Math.pow(1 - (TCULT / 6.5), 2);
			}

			// Calculate development units incrament
			if(IGER < 0){ // For period up to germanation
				
				int PFZ = soil.getPFZ(PROFSEM);
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
					if(cropType == Crop.WHEAT){
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
				}				
			}
			else if(IAMF < 0){ // Stage ILEV to IAMF
				if(devUnits >= STLEVAMF){
					devUnits = 0;
					IAMF = I;
				}
			}
			else if(ILAX < 0){ // Stage IAMF to ILAX
				if(devUnits >= STAMFLAX){
					devUnits = 0;
					ILAX = I;
				}
			}
			else if(IDRP < 0){ // Stage ILAX to IDRP
				if(devUnits >= STLAXDRP){
					devUnits = 0;
					IDRP = I;
				}
			}
			else if(ISEN < 0){ // Stage IDRP to ISEN
				if(devUnits >= STDRPSEN){
					devUnits = 0;
					ISEN = I;
				}
			}
			else if(IMAT < 0){ // Stage ISEN to IMAT
				if(devUnits >= STSENMAT){
					devUnits = 0;
					IMAT = I;
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
				}
			}
			else{
				// Grain has been harvested
				// TODO: Assuming C consitutes 50% of residue
				if(IREC == I - 1) // If harvest was yesterday
					soil.depositResidue((MASEC - MAGRAIN) *
						(cropType == Crop.WHEAT ? WHEAT_RES_CARBON :
						 cropType == Crop.MAIZE ? MAIZE_RES_CARBON : 0));
			}
		}		
		// Incrament Day
		I++;

		// Set all prev_day vars
		LAI_ = LAI;  
		MASEC_ = MASEC;
		CUMLRACZ_ = CUMLRACZ; //    sum of the effective root lengths                              cm root cm-2 soi1
		ZRAC_ = ZRAC; // Yesterday's          root depth
		Utils.copy(LRAC, LRAC_); // Yesterday's    cffective root density in the layer Z                             cm root cm-' soi1
		DLTAMS_ = DLTAMS;
	}
	
	/** Causes the crop to be harvested, returns the DM yield */
	public double harvest(){
//		devUnits = 0;
//		IREC = I;
//		soil.depositResidue((getMASEC() - getMAGRAIN()) *
//				(cropType.equals(Constants.WHEAT) ? WHEAT_RES_CARBON :
//				 cropType.equals(Constants.MAIZE) ? MAIZE_RES_CARBON : 0));
		// Since the whole crop is harvested for silage we set yield to whole mass
		double yield = getMASEC(); //getMAGRAIN();
		// Inform soil module of plant mass removal
		soil.plantMassRemoved(yield);
		MASEC_ = 0;
		MAGRAIN = 0;
		return yield * ( // Convert DM (dry mass) to FW (fresh weight)
				cropType == Crop.WHEAT ? ((double)1000 / DM_WHEAT) :
				cropType == Crop.MAIZE ? ((double)1000 / DM_MAIZE) : 1);
	}
	
	/** Prints the initial state */
	public void printStateInit(){
		System.out.println("Testing " + cropType + " model");	
	}
	
	/** Print one-line representing pasture state */
	public void printState(){
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
		System.out.println(
				cropType +
				"\tDay: " + I + 
				"\tDM(t/ha): "            + Utils.roundString(getMASEC()) +
				"\tLAI: "               + Utils.roundString(getLAI()) +
				"\tGrowth: "            + Utils.roundString(getDLTAMS()) +
				"\tGrain Yield(t/ha): " + Utils.roundString(MAGRAIN * .01) +
				"Status: " + phase
		);
	}
	/** Prints the first 15 layers */
	public void printLayers(){ printState(15); }
	/** Prints the given number of layers in the model */
	public void printState(int height){
		// ASCII VISUALISATION
		int h = (int)(MASEC_ * height / 10) + 1;
		for(int i = 0; i < height - h; i++) System.out.println();
		if(LAI_ > 0){
			for(int i = 0; i < h; i++){
				int l = (int)(LAI_ * 10);
				for(int j = 0; j < 66; j++)
					System.out.print(j == 33 ? "#" : i % 2 == 1 || j < 33 - l || j > 33 + l ? " " : "~");
				System.out.println();
			}
		}
		else System.out.println();		
	}
	
	/** Test model */
	public static void main(String[] args) {
		
		Parameters params = Experiments.create();
		
		Random rand = new Random(params.WEATHER_RANDOM_SEED);
		RandomGenerator genRain    = new RandomGenerator(RandomGenerator.RAINFALL_DAILY       , rand.nextLong());
		RandomGenerator genTemp    = new RandomGenerator(RandomGenerator.TEMPERATURE_DAILY    , rand.nextLong());
		RandomGenerator genTmin    = new RandomGenerator(RandomGenerator.TEMPERATURE_MIN_DAILY, rand.nextLong());
		RandomGenerator genRad     = new RandomGenerator(RandomGenerator.SOLAR_RADIATION_DAILY, rand.nextLong());
		
		Soil s = new Soil(params);
		WheatCrop w = new WheatCrop(
				s, // Soil model
				65 // Day of the year to start at
		);
		s.sowCrop(w);
		int d = 0;
		for(; d < 65; d++){
			genRain.next();
			genRad.next();
			genTemp.next();
			genTmin.next();
		}
		for(; d < 365; d++){
			double rain = genRain.next();
			double rad = genRad.next();
			double temp = genTemp.next();
			double tmin = genTmin.next();
			s.execDay(
					rain, //         precipitation                                                       mm day-'
					0, //        irrigation table                                               mm day-l
					0,       // fertilization table                                            kg N Ha-1 day-1
					rad, //         global solar radiation                                              MJ m-2 day-l
					temp, //       mean temperature                                                    "C
					tmin //       minimum temperature                                                 OC
			);
			w.execDay(temp, tmin, rain, rad);
			s.printState();
		}

	}

}
