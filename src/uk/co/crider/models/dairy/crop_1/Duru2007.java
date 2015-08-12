package uk.co.crider.models.dairy.crop_1;

public class Duru2007 {

/*
	public void model(
			int d,      // day of the year
			// Environmental Parameters
			double T,   // average daily temperature (ranging from 0 to 18 1C)
			double T_,   // PREV average daily temperature (ranging from 0 to 18 1C)
			double Ri,  // incident radiation (MJ mÀ2)
//			double Ra,  // rainfall (mm)
			double Ra_, // PREV rainfall
			double PET, // potential evapo-transpiration (mm)
			// Soil and plant parameters
			double SWC,    // soil water capacity (mm)
			double LLS,    // leaf livespan (in degree-days DD)
			double k,      // coefﬁcient of light extinction, taken to be 0.52 for spring
//			double growth, // and 0.56 for summer regrowth (Varlet-Grancher et al., 1989)
			// Management variables and indicators
			double Dd,  // time elapsed between two grazing events
			int Ncow,   // number of cows
			double n,   // number of paddocks used by the herd in one season (1 ha per paddock)
			double Ni,  // index for sward nitrogen status, ranging from 0.4 to 1
			double Ni_,  // PREV index for sward nitrogen status, ranging from 0.4 to 1
//			double AHV, // available herbage volume per cow
			double AHV5, // available herbage volume per cow, 5 cm basis for calculation
			// Time dependent variables
			double AW_,
			double AET_,
			double LAI_,
			double Wi_,
			double DM_,
			double DMg_,
			double DMs_,
			double remove_me
	){
		// State and computed variables

		double AET;  // actual evapo-transpiration (mm day )
		double AW;   // available water (mm)
		double DM;   // herbage mass (kg DM / ha)
		double DMg;  // herbage growth (kg DM dayÀ1)
		double DMs;  // herbage senescence (kg DM dayÀ1)
		double DMA;  // herbage mass after grazing (kg DM haÀ1)
		double DMB;  // herbage mass before grazing (kg DM haÀ1)
		double LAI;  // leaf area index
//		double LAIr; // residual LAI
		double PARa; // photosynthetic absorbed active radiation (MJ mÀ2)
		double RUE;  // radiation use efﬁciency (g DM MJÀ1)
		double Wi = AET / PET; // (water deﬁcit index)
		double Ds; // grazing duration on a paddock (days)
		double ei;// = 0.95 * (1 - Math.exp(-k * LAI));
//		double eG; // grazing efﬁciency
//		double eN; // N use efﬁciency
//		double pN; // herbage N content (% DM)
		double al; // seasonal factor involved in RUE computation
		double b;  // temperature factor involved in RUE computation
		
		// Hypothesis and ﬁxed parameters
		// Beginning of growth simulation: 1st February
		// N requirement to sustain a given sward N index = N requirement for herbage growth - N excreted
		// DMB herbage mass before grazing is expressed above 5 cm
		// I herbage intake per cow and per day considered as a constant (18 kg DM)
		// M  milk production per cow and per day considered as a constant (24 kg)
		// Volumic herbage mass (above 5 cm): DM / H = 1 kg DM per m3

		// Herbage growth modeling (equations from Cros et al., 2003b, or modiﬁedy)
		AW = Math.min(Ra_ + AW_ - AET_, SWC);
		AET = Math.min(PET, AW);
		LAI = LAI_ + (0.643 - Math.pow(T, 2) - Ni_ - Wi_);
		DM = DM_ + DMg_ - DMs_;
		DMg = RUE - PARa;
		PARa = 0.48 * ei - Ri;
		ei = 0.95 * (1 - Math.exp(-k * LAI));
		RUE = al * b * (0.75 * Ni + 0.25) * Wi;
		b = 0.037 + 0.09 * T - 0.0022 * Math.pow(T, 2);
		al = -0.0033 * d + 2.61;
		DMs = 0.60 * DMA * (T_ / LLS);

		DMB = DM; // and for d varying from 0 to Dd,
		DMA = DMB - Ds * N * 18;
		LAIA = 6 * Math.pow(DMA / DMB, 0.73) - 1.5; // TODO: What is LAIA??
		
		// DMA = 0 if AHV is expressed in 5 cm basis
		// When environmental variables are constant over the
		// grazing season, the stocking rate allows to sustain a
		// constant AHV
		// Calculation
		AHV5 = (H / DMB) * ((n * DMB / 2) / Ncow); // TODO: What is H ??
		// DMB / 2 being the average standing herbage mass on a the set of grazed
		// paddocks at a given date, hypothesized that standing
		// herbage mass is = DMB on to next paddock to be grazed,
		// and ¼ DMA on the last grazed paddock
		DMB = Ds * 18 * Ncow;
		// as Ds = Dd/n, DMB = (Dd/n) * 18 * Ncow
		if(H / DMB == 1)
			AHV5 = 9 * Dd;

		// Nitrogen content herbage of the herbage intake and the excretions (kg/ha/day)
		// Measurement:
		Nup; // N concentration (g 100 gÀ1) in the upper sward layer // TODO: Where does Nup come from
		// (10 cm in average) to compute sward N index; Ni = 0.294
		// Nup - 0.21 (Duru, 2004)
		// Calculation:
		Ni = N / (4.8 * Math.pow(DMB, - 0.32));
		Np = Ni * 4.8 * Math.pow(DMB, -0.32);
		Nexcreted = SR * 18 * N% - N; // in milk yield (constant)
		// N in milk = N herbage requirement - N excreted
		N = Ncow * Np * constant;  // TODO: is this N, milk nitrogen content
		Nexcreted = DMB * Np - N;// in milk
		yield = DMB * N% - SR * milk yield (kg/cow/day) * 0.0051; // (Delaby et al., 1997)

		// Other Calculations
		// Drainage:
		if(AW + Ra > SWC) Drainage = Ra - SWC;
		else Drainage = 0;
		// Stocking rate (SR)
		SR = N/n = DMn / 18;
		// Grazing efﬁciency (eG)
		eG = (DMB - DMA) / (DMg * Dd)
		eG = 1 - (DMs * Dd) / (DMg * Dd);
		eG = 1 - ((DMSA * T) / (LLS * PARa * RUE * Ni * Hi));
		//mN use efﬁciency (eN)
		eN = DMB / N uptake;
		eN = DMB / (Ni * (48 * Math.pow(DMB, 0.64)));
		eN = Math.pow(DMB, 0.32) / (48 * Ni);
		eN = Math.pow(DMB, 0.32) / (14.1 * Nup - 10.1);

	}
	
	
*/	
	
	
	
	
	
	
	
	/** Test the model */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
