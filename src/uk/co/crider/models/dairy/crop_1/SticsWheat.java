package uk.co.crider.models.dairy.crop_1;


public class SticsWheat extends STICS {

	public SticsWheat(
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
		super(
				WHEAT, // Crop type, MAIZE or WHEAT
				IPLT,  // Day in year of planting, 0 = Jan 1st
				// Constant climate parameters
				TETP, //           reference evapotranspiration                                        mm day-l
				// Crop management parameters
				PROFSEM,  // sowing depth                                                      cm
				DENSITE,  // sowing density                                                 pl m-2
				ZLABOUR,  // depth of ploughing                                                  cm
				// Soil parameters
				ALBEDO, // albedo of the barc dry soil                                    sans dinicnsion
				soilType, //           parameter of the soi1 evaporation during the reduction stage
				Q0, //         parameter of the end of the maximum cvaporntion stage             mm
				ARGI //        percentage of clay in the surface layer                        %
		);
	}
	
	@Override
	public void initSpeciesParams() {
		// Shoot growth module
		DLAIMAX = 4.4E-4f; //     maximum rate of the setting up of LAI                          m2 leaves m-2 soil degree days-
		BDENS = 7; //       maximum density above which there is coinpetition between plants                                                         pl m-2
		EXTIN = 0.5f; //       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
		TCMIN = 0; //      minimum temperature of growth                                       OC
		TCMAX = 40; //     maxirnuin temperature of growth                                   O C
		TCOPT = 15; //      optimum temperature of growth                                       OC
		EFCROIVEG = 4.25f;   // maximum radiation use efficiency during the vegetative stage       g MJ-'
		EFCROIREPRO = 4.25f; // maximum radiation use efficiency during the grain filling phase    g MJ-'
		MSAER0 = 1.15f;     // threshold of aboveground dry matter corresponcling to the root allocation at the beginning of the cycle                          t ha-'
		ADENS = -0.5f;       // parameter of compensation between stem number and plant density                                              sans dimension
		// Yield module
		NBJGRAIN = 30; //   period when to compute NBGRAINS                                   number of days before IDRP
		CGRAIN = 1043; //      slope of the relationship bctween grain number and growth rate during the NRJGRAIN before stage IDRP                          grains g dry matter' day
		CGRAINV0 = -4160f; //    nuniber of grains produced when growth rate is zero            grains m-'
		VITIRCARB = 0.011f; //     rate of increase of the carbon harvest index                        g grain g plant-l day-'
		VITIRAZO = 0.013f; //      rate of increase of the nitrogen harvest index                      gN grain gN plant-l day-'
		PGRAINMAXI = 41.5f; // maximum weight of one grain (O % water content)                   g
		// Water balance moudule
		EXTIN = 0.5f; //      extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
		KMAX =  1.2f;  //     maxiinum crop coefficient for water requirements                  sans dimension
		PSISTO = 15; //      absolute value of the potential of stomatal closing               bars
		PSITURG = 4; //     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars
		// Root growth module
		ZPENTE = 100; //      depth where the root density is 112 of the surface root density for the reference profile
		ZPRLIM = 160; //      maximum depth of the root profile for the reference profile
		CROIRAC = 0.12f; //     growth rate of the root front                                  cm degree-day-'
		// Nitrogen Module
		ADIL = 5.35f; //        parameter of the critical curve of nitrogen needs              gN g dry matter-]
		BDIL = 0.442f; //        parameter of the critical curve of nitrogeii needs             sans dimension
		ADILMAX = 8.47f; //    parameter of the maximum curve of nitrogen needs               gN g dry matter'
		BDILMAX = 0.44f; //    Parameter of the maximum curve of nitrogen needs               sans dimension
		VMAX1 = 0.0018f;   //    rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
		VMAX2 = 50;   //    rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
		KMABS1 = 0.050f;  //    constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
		KMABS2 = 25000;  //    constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'
		// Development Module
		TDMIN = 0;        // maximum threshold temperature for development                       OC
		TDMAX = 28;        // minimum threshold temperature for development                       OC
		STDRPSEN = 100;     // sum of development units betweeii the stages DRP and SEN          degree-day
		STSENMAT = 550;     // sum of development units between the stages SEN and MAT           degree-day
		PHOBASE = 8;      // base photoperiod                                                  hour
		PHOSAT = 20;       // saturating photoperiod                                            hour
		TGMIN = 0;        // minimum threshold temperature used in the emergence stage           OC
		STPLTGER = 50;     // sum of development units allowing germination                     degree-day
		BELONG = 0.012f;     // parameter of the curve of coleoptile elongation               degree-day-'
		CELONG = 3.20f;     // parameter of the curve of coleoptile elongation                sans dimension
		H20GRAIN = 0.14f;   // water content of the grain when harvested                          g water g fresh matter grain-'
		DESSECGRAIN = 1.4E-3f;// drying rate of the grain                                       g water g fresh matter' OC-'
		JVC = 40;          // number of vernalizing days
		STLEVAMF = 70;     // sum of development units between the stages LEV and AMF           degree-day
		STAMFLAX = 307;     // sum of development units hetween the stages AMF and LAX           degree-day
		STLAXDRP = 151;     // sum of development units between the stages LAX and DRP           degree-day
		ELMAX = 8;         // maximum elongation of the coleoptile in darkness condition         cm	
	}

	/** Test Model */
	public static void main(String[] args) {
		STICS.testModel(WHEAT);
	}

}
