package uk.co.crider.models.dairy.crop_2;

/** Class to represent a generic crop, currently includes maize, wheat and grass */
public abstract class Crop {
	
	// CONSTANTS
	
	public static final int MAIZE = 0;
	public static final int WHEAT = 1;
	public static final int GRASS = 2;

	// Crop management parameters
	protected int ZLABOUR = 30;  // depth of ploughing                                                  cm
	protected final int PROFSEM = 2;  // sowing depth                                                      cm

	// Shoot Growth Species Parameters
	//float DLAIMAX; //     maximum rate of the setting up of LAI                          m2 leaves m-2 soil degree days-
	//float BDENS; //       maximum density above which there is coinpetition between plants                                                         pl m-2
	float EXTIN; //       extinction coefficient of photosynthetic active radiation in the canopy                                                             sans dimension
	protected float TCMIN; //         minimum temperature of growth                                       OC
//	protected float TCMAX; //     maxirnuin temperature of growth                                   O C
	protected float TCOPT; //         optimum temperature of growth                                       OC
/*	protected float EFCROIVEG;   // maximum radiation use efficiency during the vegetative stage       g MJ-'
	protected float EFCROIREPRO; // maximum radiation use efficiency during the grain filling phase    g MJ-'
	protected float MSAER0;     // threshold of aboveground dry matter corresponcling to the root allocation at the beginning of the cycle                          t ha-'
	protected float ADENS;       // parameter of compensation between stem number and plant density                                              sans dimension
*/
	
	// Water Species Parameters
	protected float KMAX; // maxiinum crop coefficient for water requirements                  sans dimension
	protected int PSISTO; //     absolute value of the potential of stomatal closing               bars
	protected int PSITURG; //     absolute value of the potenlia1 of the beginning of decrease of the cellular extension                                         bars
	
	
	// Root Growth Species Parameters
	protected int ZPENTE; //         depth where the root density is 112 of the surface root density for the reference profile
	protected int ZPRLIM; //         maximum depth of the root profile for the reference profile
	protected float CROIRAC; //     growth rate of the root front                                  cm degree-day-'
	protected final float RAYON = 0.02f; //     average radius of roots                                           cm (0.02)
	protected final float LVOPT = 0.5f; //      optimum root density                                              CJII root cm-3 soi] (0.5)

	// Nitrogen Species Parameters
	protected float ADIL; //        parameter of the critical curve of nitrogen needs              gN g dry matter-]
	protected float BDIL; //        parameter of the critical curve of nitrogeii needs             sans dimension
	protected float ADILMAX; //    parameter of the maximum curve of nitrogen needs               gN g dry matter'
	protected float BDILMAX; //    Parameter of the maximum curve of nitrogen needs               sans dimension
	protected float VMAX1;   //    rate of slow nitrogen absorption (high affinity sysiem)             ~imol cm-' h-'
	protected float VMAX2;   //    rate of rapid nitrogen absorption (low affinity system)             pmol cm-' h-l
	protected float KMABS1;  //    constani of nitrogen uptake by roots for the high affinity system pmol cm-' s-'
	protected float KMABS2;  //    constant of nitrogen uptake by roots for the low affinity system  pmol cm-' s-'
	
	abstract double getLAI();
	abstract double getZRAC();
	abstract double getCUMLRACZ();
	abstract double[] getLRAC();
	abstract double getMASEC();
	abstract double getDLTAMS();
	
	/** Initialises parameters specific to the crop species */
	protected abstract void initSpeciesParams();

	
	/** Test model */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
