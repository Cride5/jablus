package uk.co.crider.jablus.models.dairy.env.field;

/** Class to represent a generic crop, currently includes maize, wheat and grass */
public abstract class Crop {

	// Crop types
	public static final int OFFSET = 1900;
	public static final int FALLOW  = OFFSET + 0;
	public static final int GRASS   = OFFSET + 1;
	public static final int PASTURE = OFFSET + 2;
	public static final int WHEAT   = OFFSET + 3;
	public static final int MAIZE   = OFFSET + 4;
	
	// Dry matter content of crops g/Kg (dry matter/fresh weight) see Farm Management Handbook p109
	public static final int DM_SILAGE = 250;
	public static final int DM_WHEAT = 320;
	public static final int DM_MAIZE = 270;
	
	public static final int[] MAX_YIELD = {
		15, // Grass
		5, // Wheat
		5}; // Maize
	
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
	
	protected final int cropType;
	protected final Soil soil;
	
	public Crop(int cropType, Soil soil){
		this.cropType = cropType;
		this.soil = soil;
	}

	/** Initialises parameters specific to the crop species */
	protected abstract void initSpeciesParams();

	/** Returns the crop type */
	public int getType(){ return cropType; }
	
	/** Causes the crop to be harvested, returning the yield */
	public abstract double harvest();
	
	/** Prints the current state to the console */
	public abstract void printState();
	
	/** Test model */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
