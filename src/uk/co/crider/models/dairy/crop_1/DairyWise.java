package uk.co.crider.models.dairy.crop_1;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.util.Hashtable;

public class DairyWise {

	public static final String SOIL_THICK_SAND = "Thick Sand";
	public static final String SOIL_THIN_SAND  = "Thin Sand";
	public static final String SOIL_CLAY_LOAM  = "Clay Loam";
	public static final String SOIL_LOAM       = "Loam";
	public static final String SOIL_LOESS      = "Loess";
	public static final String SOIL_HEAVY_CLAY = "Heavy Clay";
	public static final String SOIL_CLAY       = "Clay";
	public static final String SOIL_PEAT       = "Peat";
	public static final String SOIL_CLAY_PEAT  = "Clay Peat";
	public static final String SOIL_SAND_PEAT  = "Sand Peat";

	private static final Hashtable<String, Integer> iSoilType = new Hashtable<String, Integer>();
	static{
		iSoilType.put(SOIL_THICK_SAND, 0);
		iSoilType.put(SOIL_THIN_SAND,  1);
		iSoilType.put(SOIL_CLAY_LOAM,  2);
		iSoilType.put(SOIL_LOAM,       3);
		iSoilType.put(SOIL_LOESS,      4);
		iSoilType.put(SOIL_HEAVY_CLAY, 5);
		iSoilType.put(SOIL_CLAY,       6);
		iSoilType.put(SOIL_PEAT,       7);
		iSoilType.put(SOIL_CLAY_PEAT,  8);
		iSoilType.put(SOIL_SAND_PEAT,  9);
	}	
	private static int iWaterTable(int hGW, int lGW){
		if(hGW <= 25){
			if(lGW <= 80)
				return 0;
			if(lGW <= 120)
				return 2;
			// lGW > 120
				return 5;
		}
		if(hGW > 25 && hGW <= 40){
			if(lGW <= 80)
				return 1;
			if(lGW > 80 && lGW <= 120)
				return 3;
			// lGW > 120
				return 6;
		}
		if(hGW > 40 && hGW <= 80){ // hGW > 40 & <= 80
			if(lGW <= 120)
				return 4;
			// lGW > 120
				return 7;
		}
		// hGW > 80, thus lGW must be at least > 80
				return 8;
	}
	
	// DataTable 5
	// Yield reduction due to water limitation                    %
	private static final int[][] DroughtGrassTable = {
	// Soil Thick Thin Clay            Heavy           Clay Sand       Watertable
	// Type Sand  Sand Loam Loam Loess Clay  Clay Peat Peat Peat       Level
		   {  -3,   -1,  -3,  -1,   -3,   0,   -2,  -1,  -1,  -1 }, // II
		   {  -2,   -2,  -3,   0,   -3,   1,   -2,   0,   1,   0 }, // II*
		   {  -2,    2,  -2,   4,   -3,   7,    0,   4,   4,   4 }, // III
		   {  -2,    2,  -2,   4,   -3,   9,    0,   5,   5,   4 }, // III*
		   {   1,    4,  -2,   4,   -4,   9,    1,   4,   4,   4 }, // IV
		   {   2,    8,   1,   8,   -2,  16,    4,  12,  11,  11 }, // V
		   {   5,   10,   2,  11,   -2,  17,    7,  14,  14,  14 }, // V*
		   {  11,   16,   5,  12,   -3,  22,   12,  21,  20,  20 }, // VI
		   {  17,   21,  10,  15,   -3,  26,   17,  29,  29,  29 }, // VII
	};
	
	// DataTable 6
	private static final int[] iSNS = {100, 150, 170, 200, 250, 300};
	private static final int[] iDay = {91, 121, 151, 201, 251};
	// Soil nitrogen supply during growth cycle (DataTable 6) kg N ha 1
	private static final double[][] SNSgrcycleTable = {
	// SNS  50   100   150   170   200   250   300       day
		{ 0.04, 0.14, 0.23, 0.27, 0.33, 0.43, 0.53 }, //  60- 90
		{ 0.08, 0.27, 0.47, 0.54, 0.66, 0.86, 1.05 }, //  91-120
		{ 0.12, 0.41, 0.70, 0.82, 0.99, 1.28, 1.58 }, // 121-150
		{ 0.30, 0.60, 0.90, 1.02, 1.20, 1.50, 1.80 }, // 151-200
		{ 0.20, 0.40, 0.60, 0.68, 0.80, 1.00, 1.20 }, // 201-250
		{ 0.15, 0.30, 0.45, 0.51, 0.60, 0.75, 0.90 }, //    >250
	};

	// DataTable 7
	private static final int[] iDayNres_DM = {141, 166, 191, 221, 245};
	private static final int[] iPrevNapp = {40, 80, 120};
	// Residual effect of previous N application on DM yield
	private static final double[][] NresDMtable = {
	// Day        141-   166-   191-  221-    245-       Previous N
	//     <140    165    190   220    244     280       Application
		{ -15.8, -27.3, -32.7, -33.1, -32.5, -35.0 }, //   0
		{  30.1,  -0.5,  -9.5,  -7.6, -10.0,  -4.4 }, //  40
		{  35.8,  12.8,   2.3,   0.6,   0.0,  -1.6 }, //  80
		{  31.5,  20.6,   7.3,   1.9,  -7.5, -18.6 }, // 120
	};
	
	// DataTable 8
	private static final int[] iDayNC = {105, 136, 166, 196, 226, 255};
	// c coefficint for function that describes nitrogen content in grass
	private static final double[] cNCgrassTable = {
	// Day  <105  105-135  136-165  166-195  196-225  226-255   > 255
		   21.35,   15.61,   10.10,   19.10,   16.29,   18.65,  17.42
	};
	
	// DataTable 9
	// c coefficient for function that describes dilution of nitrogen over time
	private static final double[] cNCdilTable = {
	// Day  <105  105-135  136-165  166-195  196-225  226-255   > 255
		   23.33,   21.26,   36.59,   30.60,   33.42,   35.42,  41.66
	};

	// DataTable 10
	private static final int[] iDayNres_N = {165, 190, 220, 244, 280};
	// Residual effect of previous N application on N yield (DataTable 10) Kg N ha 1
	private static final int[][] NresNtable = {
	// Day  140  165  190  220  244  280       Last fresh N supply
		  {  -7, -18, -33, -50, -50, -50 }, //   0
		  { -14,  -2, -11, -17, -31, -35 }, //  40
		  {  23,  13,   4,   3,   0,   0 }, //  80
		  {  27,  25,  23,  28,  31,  39 }, // 120
	};

	// DataTable 11
	private static final int[] iDayRC = {                166,   196,   226,   256,   285};
	// c1 and c2 coefficient for function that calculates crude fibre content in grass cut   
	private static final double[] c1RCtable = { -7.6,  -10.2,    8.3,  30,     25,    31.2};
	private static final double[] c2RCtable = {199.56, 215.34, 201.5, 172.93, 164.5, 175.52};

	//DataTable 17 Drought Maize
	// Yield reduction due to water limitation                    %
	private static final int[][] DroughtMaizeTable = {
    // Sand_   Sand_  Loam_  Loam  Loess  Clay_   Clay  Peat  Peat_  Peat_
    // thick   thin   clay                heavy                clay   sand     Waterlevel
		{ 0,     2,     0,     1,    0,      2,     0,   100,    0,     1}, // II
		{ 1,     1,     0,     2,    0,      3,     0,   100,    1,     2}, // II* 
		{ 1,     5,     1,     6,    0,      9,     2,   100,    5,     6}, // III
		{ 1,     5,     1,     7,    0,     11,     2,   100,    7,     7}, // III* 
		{ 1,     6,     1,     7,    0,     11,     2,   100,    7,     7}, // IV 
		{ 4,    11,     3,    11,    0,     18,     5,   100,   14,    14}, // V
		{ 5,    14,     4,    12,    0,     20,     7,   100,   16,    17}, // V*
		{ 8,    19,     6,    14,    0,     23,    11,   100,   23,    24}, // VI
		{14,    24,    11,    17,    1,     26,    18,   100,   31,    32}, // VII
	};

	
	// TODO: These enforced ranges are arbitrary, remove if possible
	public double getDMYgrass(){ return DMYgrass < 0 ? 0 : DMYgrass; }
	private double DMYgrass;
	// Digestable organic matter                                 g kg DM 1
	public double getVOS(){ return VOS < 0 ? 0 : VOS; }
	private double VOS;
	// Crude fibre content in grass cut                                        g kg DM 1
	public double getRC(){ return RC < 0 ? 0 : RC > 400 ? 400 : RC; }
	private double RC;
	// Crude protein                                               g kg DM 1
	public double getRE(){ return RE < 0 ? 0 : RE; }
	private double RE;
	// Crude ash content in cut                                      g kg DM 1
	public double getRAS(){ return RAS < 0 ? 0 : RAS; }
	private double RAS;
	/** Dairy Wise grass simulation */
	private void grass(
			int i, // Growth Cycle
			int Napp,                 //   Nitrogen application                                                                      kg N ha 1
			int PrevNapp,				// Previous N application
			int SNS,                 //   Soil nitrogen supply
			double Tstart,         //          Starting day of a growing cycle                                                                          d
			double GRD,              //      Day number from start growing cycle d
			// TODO: Do all references to day the same. Eg harvest day, total days?
			int doy,    // Day of the year
//			int Day,    // Day number of the harvest day
//			int day,    // Day number
			String soilType,     // String describing soil type
			int hGroundWater, // Average highest groundwater level (cm under field level)
			int lGroundWater // Average lowest groundwater level (cm under field level)
		
		){
		
		// OUTPUTS
		// Grass dry matter yield, nitrogen content and feeding value
		
		// Coefficient values, defined for specific equations
		double a, b, c, c1, c2, d, e, f;
		
		// --------------------[ Dry matter yield ]------------------------
		
		double SNSgrcycle;  //   Soil nitrogen supply during growth cycle (DataTable 6)      
//		day                 // Day number
//		SNS                 //   Soil nitrogen supply
		SNSgrcycle = SNSgrcycleTable[Utils.index(SNS, iSNS)][Utils.index(doy, iDay)];
		
		double SNS170grcycle;  //   Soil nitrogen supply during growth cycle (DataTable 6)      
//		day                    // Day number
		SNS170grcycle = SNSgrcycleTable[Utils.index(170, iSNS)][Utils.index(doy, iDay)];

		double Nres_DM;        //   Residual effect of previous N application on DM yield (DataTable 7)                           kg N ha 1
//		day                    // Day number
//		PrevNapp			   // Previous N application
		Nres_DM = NresDMtable[Utils.index(PrevNapp, iPrevNapp)][Utils.index(doy, iDayNres_DM)];
		
		// TODO: Ambigous bracketing, variable name, i presume Nappl = napp
		double DMY_Nfactor;  //   Nitrogen supply factor for DM yield                                                       kg N ha 1
//		SNSgrcycle           //   Soil nitrogen supply during growth cycle (DataTable 6)                                        kg N ha 1
//		SNS170grcycle        //    Soil nitrogen supply from a standard soil during growth cycle                             kg N ha 1
//		Napp                 //   Nitrogen application                                                                      kg N ha 1
//		Nres_DM              //   Residual effect of previous N application on DM yield (DataTable 7)                           kg N ha 1
		a                    = 0.8;
		b                    = 40;
		c                    = 1;
		DMY_Nfactor          = ((SNSgrcycle - SNS170grcycle) / a) + ((Napp + Nres_DM) / b) + c;

		double MaxGrowth;    //          Maximum daily growth                                                                   1000 kg DM ha 1 d 1
		if(i == 0){ // first cut (or growth cycle)
//			DMY_Nfactor    //          Nitrogen supply factor for DM yield                                                              kg N ha 1
			a              =          0.0445;
			b              =          -0.0044;
			c              =          0.0837;
			MaxGrowth      =          a * DMY_Nfactor + b * Math.pow(DMY_Nfactor, 2) + c;
		}
		else{ // later cuts (or grwoth cycles)
			// TODO: Ambigous bracketing: initial open bracket, no close.
			// TODO: Correction factor applied, may not be correct!
//			DMY_Nfactor    //          Nitrogen supply factor for DM yield                                                              kg N ha 1
//			Tstart         //          Starting day of a growing cycle                                                                          d
			a              =          -0.0446;
			b              =          0.0000536;
			c              =          9.86;
			d              =          1.608;
			e              =          -0.1792;
			f              =          -0.00322;
			MaxGrowth      =          0.03 * (c + a * Tstart + b * Math.pow(Tstart, 2) + d * DMY_Nfactor + e * Math.pow(DMY_Nfactor, 2) + f * Tstart * DMY_Nfactor);
		}

		double Limit;        //   Parameter defining the upper yield limit                                                        d1
		if(i == 0){ // first cut (or growth cycle)
//			DMY_Nfactor          //   Nitrogen supply factor for DM yield                                                       kg N ha 1
			a                    =   -0.00811;
			b                    =   0.00080;
			c                    =   0.1017;
			Limit                =   a * DMY_Nfactor + b * Math.pow(DMY_Nfactor, 2) + c;
		}
		else{ // later cuts (or grwoth cycles)
//			DMY_Nfactor          //   Nitrogen supply factor for DM yield                                                       kg N ha 1
			a                    =   0.00699;
			c                    =   0.09684;
			Limit                =   a * DMY_Nfactor + c;
		}
		
		// TODO: is aMaxGrowth == MaxGrowth from above??
		double DMYpot;   //      Potential grass yield without water limitation                                               1000 kg DM ha 1
//		MaxGrowth_i      //      Maximum daily growth in first (i=1) or later (i=r) growth cycles                         1000 kg DM ha 1 d 1
//		T_MaxGrowth_i    //      Day number of maximum daily growth in first (i=1) or later (i=r) growth cycles                             d
//		Limit_i          //      Parameter defining the upper yield limit in first (i=1) or later (i=r) growth cycles                      d1
//		GRD              //      Day number from start growing cycle                                                                        d
		a                =      4;
		b                =      0.4;
		int T_MaxGrowth  =      i == 0 ? 80 : 30;
		DMYpot           =      a * MaxGrowth / (Limit * (1 + Math.exp(Limit * (T_MaxGrowth - GRD)))) - b;

		double Drought; //          Yield reduction due to water limitation (DataTable 5)                                                        %
//		soilType     // String describing soil type
//		hGroundWater // Average highest groundwater level (cm under field level)
//		lGroundWater // Average lowest groundwater level (cm under field level)
		Drought        = DroughtGrassTable[iWaterTable(hGroundWater, lGroundWater)][iSoilType.get(soilType)];

		// TODO: As it stands this totally doesn't work. if Drought is a change in percentage need to change!
//		double DMYgrass; //          Actual grass yield                                                                         1000 kg DM ha 1
//		DMYpot         //          Potential grass yield without water limitation                                             1000 kg DM ha 1
//		Drought        //          Yield reduction due to water limitation (DataTable 5)                                                        %
		DMYgrass       =         DMYpot * ((100 - Drought) / 100);

System.out.println(
		"Tstart     = " + Tstart + "\n" +
		"SNSgrcycle = " + SNSgrcycle + "\n" +
		"Nres_DM    = " + Nres_DM + "\n" +
		"DMY_Nfactor= " + DMY_Nfactor + "\n" +
		"MaxGrowth  = " + MaxGrowth + "\n" +
		"Limit      = " + Limit + "\n" +
		"DMYpot     = " + DMYpot + "\n" +
		"Drought    = " + Drought +  "\n" +
		"DMYgrass   = " + DMYgrass
		);

		//------------------------[ Nitrogen yield ]--------------------------
		
		double Nres_N;      // Residual effect of previous N application on N yield (DataTable 10)                     Kg N ha 1
//		day                 // Day number
//		PrevNapp            // Previous N application
		Nres_N = NresNtable[Utils.index(PrevNapp, iPrevNapp)][Utils.index(doy, iDayNres_N)];
		
		double NY_Nfactor;  //     Nitrogen supply factor for N yield                                                  Kg N ha 1
//		SNSgrcycle          //     Soil nitrogen supply during growth cycle (DataTable 6)                                  Kg N ha 1
//		SNS170grcycle       //     Soil nitrogen supply from a standard soil during growth cycle                       Kg N ha 1
//		Napp                //     Nitrogen application                                                                Kg N ha 1
//		Nres_N              //     Residual effect of previous N application on N yield (DataTable 10)                     Kg N ha 1
		a                   =     0.8;
		b                   =     0.5;
		c                   =     40;
		d                   =     1;
		NY_Nfactor          =     (((SNSgrcycle - SNS170grcycle) / a) + Napp + b * Nres_N) / c + d;		
		
		double NCgrass;   //     Nitrogen content in grass                                                   Kg N 1000 kg DM 1
		if(i == 0){ // first cut (or growth cycle)
//			NY_Nfactor          //     Nitrogen supply factor for N yield                                                  Kg N ha 1
			a                   =     22.49;
			b                   =     2.941;
			NCgrass             =     a + b * NY_Nfactor;
		}
		else{ // later cuts (or grwoth cycles)
//			NY_Nfactor          //     Nitrogen supply factor for N yield                                                  Kg N ha 1
			a                   =     14.58;
			b                   =     -1.517;
			c                   =     cNCgrassTable[Utils.index(doy, iDayNC)];
			NCgrass           =     a * NY_Nfactor + b * Math.pow(NY_Nfactor, 2) + c;
		}
		
		double NCdil;       //     Parameter that describes dilution of nitrogen over time
//		NY_Nfactor          //     Nitrogen supply factor for N yield                                                  Kg N ha 1
		a                   =     4.21;
		b                   =     0.733;
		c                   =     cNCdilTable[Utils.index(doy, iDayNC)];
		NCdil               =     a * NY_Nfactor + b * Math.pow(NY_Nfactor, 2) + c;

		double NYgrass;    //   Nitrogen yield of grass in the first (i=1) or later (i=r) growth cycles                   Kg N ha 1
//		NCgrass            //   Nitrogen content in grass in the first (i=1) or later (i=r) growth cycles        Kg N 1000 kg DM 1
//		NCdil              //   Parameter that describes dilution of nitrogen over time
//		DMYgrass           //   Actual grass yield                                                                  1000 kg DM ha 1
		NYgrass            =   NCgrass + NCdil * Math.log(DMYgrass);

//		DataTable 9 c
//		Day number                  <105           105 135      136 165              166 195     196 225 226 255         > 255
//		c                                  23.33       21.26           36.59            30.60      33.42   35.42         41.66

//		DataTable 10 Nres_N
//		Day number in year    140            165                       190         220            244    280
//		Last fresh N supply
//		0                     7               18                        33          50             50     50
//		40                    14              2                         11          17             31     35
//		80                    23             13                           4           3             0      0
//		120                   27             25                         23          28             31     39

System.out.println("NYgrass:" + NYgrass);

		// -----------------------[ Feeding value ]-----------------------
		// TODO: What is sqr - is it square root or squared?
		// TODO: Is Crude_fibre really RC??
//		double RC; //     Crude fibre content in grass cut                                        g kg DM 1
		if(i == 0){ // first cut (or growth cycle)
//			DMYgrass_1    //     Dry matter yield in the first cut                                           kg DM ha 1
//			SNSgrcycle    //     Soil nitrogen supply during growth cycle (DataTable 6)                           kg N ha 1
//			SNS170grcycle //     Soil nitrogen supply from a standard soil during growth cycle                kg N ha 1
//			Napp          //     Nitrogen application                                                         kg N ha 1
			a             =     0.02564;
			b             =     -0.000653;
			c             =     0.15671;
			d             =     -0.007236;
			e             =     0.8;
			f             =     40;
			RC            =     (c + a * DMYgrass + b * Math.pow(DMYgrass, 2) + d * ((Napp + ((SNSgrcycle - SNS170grcycle) / e)) / f + 1)) * 1000;
		}
		else{ // later cuts (or grwoth cycles)
//			DMYgrass_r    //     Dry matter yield in later cuts                                              kg DM ha 1
//			SNSgrcycle    //     Soil nitrogen supply during growth cycle (DataTable 6)                           kg N ha 1
//			SNS170grcycle //     Soil nitrogen supply from a standard soil during growth cycle                kg N ha 1
//			Napp          //     Nitrogen application                                                         kg N ha 1
			b             =     14.339;
			c1            =     c1RCtable[Utils.index(doy, iDayRC)];
			c2            =     c2RCtable[Utils.index(doy, iDayRC)];
			d             =     -10.292;
			e             =     0.8;
			f             =     40;
			RC            =     (c1 + c2 * DMYgrass + b * Math.pow(DMYgrass, 2) + d* ((Napp + ((SNSgrcycle - SNS170grcycle) / e)) / f + 1)) / DMYgrass;
		}
		
		// TODO: Bracketing problems
//		double RAS;       // Crude ash content in cut                                      g kg DM 1
		if(i == 0){ // first cut (or growth cycle)
//			DMYgrass_1  // Dry matter yield in the first cut                           kg DM ha 1
//			Napp        // Nitrogen application                                         kg N ha 1
			a           = -0.149;
			b           = 3.358;
			c           = 71.2;
			d           = 40;
			RAS         = (c + a * DMYgrass + b *(Napp / d + 1));
		}
		else{ // later cuts (or grwoth cycles)
		// TODO: Is sqr = sqrt or square, ambigous bracketing!
//			DMYgrass_r  // Dry matter yield in later cuts                              kg DM ha 1
//			Napp        // Nitrogen application                                         kg N ha 1
//			DMY_Nfactor // Nitrogen supply factor for DM yield                          kg N ha 1
			a           = 84.42;
			b           = -2.627;
			c           = -10.29;
			d           = 10.496;
			RAS       = c + a * DMYgrass + b * Math.pow(DMYgrass, 2) + d * DMY_Nfactor;
		}
		
		// TODO: Var naming problem, does NYgrass = Nuptake and DMYgrass = DMYield
//		double RE;          // Crude protein                                               g kg DM 1
//		DMYgrass    // Dry matter grass yield                                      Kg DM ha 1
//		NYgrass     // Nitrogen yield of grass                                      Kg N ha 1
		a           = 6.25;
		RE          = (NYgrass / DMYgrass) * a;

		
		double VRE;    //    Digestable crude protein                                            g kg DM 1
//		RE             //    Crude protein                                                       g kg DM 1
//		RAS            //    Crude ash                                                           g kg DM 1
		a              =    0.959;
		b              =    0.04;
		c              =    -40;
		VRE            =    a * RE + b * RAS + c;

//		double VOS;    // Digestable organic matter                                 g kg DM 1
//		RAS    // Crude ash                                                 g kg DM 1
//		day    // Day number
		a      = -0.77;
		b      = -1.12;
		c      = 1029;
		d      = -0.3;
		e      = -90;
		VOS    = a * RC + b * RAS + c + (d * (doy + e));

		// CORRECTION: boolean false = 0, boolean true = 1
		double ME;     //    Metabolizable energy                                                g kg DM 1
//		VOS            //    Digestable organic matter                                           g kg DM 1
//		VRE            //    Digestable crude protein                                            g kg DM 1
		a              =    3.4;
		b              =    3.6;
		c              =    1.4;
		ME             =    a * (VOS / VRE <= 7 ? 1 : 0) * VOS + b * (VOS / VRE > 7 ? 1 : 0) * VOS + (VOS / VRE <= 7 ? 1 : 0) * c * VRE;

		double GE;     //    Gross energy                                                        g  kg DM 1
//		RC             //    Crude fibre                                                         g  kg DM 1
//		RE             //    Crude protein                                                       g  kg DM 1
//		RAS            //    Crude ash                                                           g  kg DM 1
		a              =    5.77;
		b              =    5;
		c              =    349.6;
		d              =    4.06;
		e              =    40;
		GE             =    c + a * RE + b * RC + d * (1000 - RAS - RC - RE - e);

		// CORRECTION: Bracketing resolved
		double VEM;    //    Feed Unit Miilk                                                        kg DM 1
//		ME             //    Metabolizable energy                                                g kg DM 1
//		GE             //    Gross energy                                                        g kg DM 1
		a              =    0.27376521;
		b              =    0.142;
		VEM            =    a * ME + (b / GE) * Math.pow(ME, 2);

		double PCBRE;  // Proportion of undegraded protein                                 %
//		Day    // Day number of the harvest day
//		RE     // Crude protein                                             g kg DM 1
		a      = -0.09;
		b      = 0.04;
		c      = 48.7;
		d      = 90;
		e      = 180;
		PCBRE  = c + a * RE + b * Math.min((doy + d), e);

		// CORRECTION: Bracketing and symbol problems resolved
		double PCDVBE; // Intestinally digestable BRE                                      %
//		PCBRE  // Proportion of undegraded protein                                 %
//		Day    // Day number of the harvest day
//		RE     // Crude protein                                             g kg DM 1
		a      = -100;
		b      = 12;
		c      = 100;
		d      = 0.07;
		e      = 90;
		f      = 180;
		PCDVBE = c * (PCBRE + a * (b + d * Math.min((doy + e), f)) / RE) / PCBRE;

		double DVBE;   // Rumen undegraded protein, absorbed in the small intestine g kg DM 1
//		PCBRE  // Proportion of undegraded protein                                 %
//		PCDVBE // Proportion of Intestinally digestable BRE                        %
//		RE     // Crude protein                                             g kg DM 1
		a      = 1.11;
		DVBE   = a * RE * (PCBRE / c) * (PCDVBE / 100);

		double FOS;    // Fermentable organic matter                                g kg DM 1
//		PCBRE  // Proportion of undegraded protein                                 %
//		VOS    // Digestable organic matter                                 g kg DM 1
//		RE     // Crude protein                                             g kg DM 1
		a      = -40;
		c      = 100;
		FOS    = VOS - (RE * PCBRE / c) + a;

		double DVME;   // Digestible microbial rumen protein                        g kg DM 1
//		FOS    // Fermentable organic matter                                g kg DM 1
		a      = 0.095625;
		DVME   = a * FOS;

		double DVMFE; //     Endogenous protein losses in digestion                                      g kg DM 1
//		RAS           //     Crude ash                                                                   g kg DM 1
//		VOS           //     Digestable organic matter                                                   g kg DM 1
		a             =     0.075;
		b             =     2;
		c             =     60;
		DVMFE         =     a * (1000 - VOS - Math.min(RAS / b, c));

		double MREN;  //     Potential microbial protein synthesis based on available nitrogen           g kg DM 1
//		PCBRE         //     Proportion of undegraded protein                                                    %
//		RE            //     Crude protein
		a             =     1;
		b             =     -1.11;
		c             =     100;
		MREN          =     RE * (a + b * PCBRE / c);

		double MREE;  //     Potential microbial protein synthesis based on available energy             g kg DM 1
//		FOS           //     Fermentable organic matter                                                  g kg DM 1
		a             =     0.15;
		MREE          =     a * FOS;
		
		// TODO: Is the DVMFE here to be fixed at 100 or do we use the calculated value above?
		double DVE;     //   Digestable true protein                                              g  kg DM 1
//		DVBE            //   Rumen undegraded protein, absorbed in the small intestine            g  kg DM 1
//		DVME            //   Digestible microbial rumen protein                                   g  kg DM 1
//		DVMFE           //   Endogenous protein losses in digestion                               g  kg DM 1
//		double DVMFE           =   100;
		DVE             =   DVBE + DVME - DVMFE;
		
		double OEB;     //   Degraded protein balance                                             g kg DM 1
//		MREN            //   Potential microbial protein synthesis based on available nitrogen    g kg DM 1
//		MREE            //   Potential microbial protein synthesis based on available energy      g kg DM 1
		OEB             =   MREN - MREE;		
	}
	
	private void maize(
			double Napp,                 //       Nitrogen application (Nmanure + Nfert)                                                           Kg N ha 1
			double Nupt,                 //       Uptake of nitrogen by the plant                                                 kg
			String soilType,     // String describing soil type
			int hGroundWater, // Average highest groundwater level (cm under field level)
			int lGroundWater // Average lowest groundwater level (cm under field level)
		){
		
		// OUTPUTS
		// Maize dry matter yield, nitrogen content and feeding value
		
		// Coefficient values, defined for specific equations
		double a, b, c;

		//-------------------------[ Nitrogen yield ]-----------------------------
		
		// TODO: Variable naming problem, assuming: Nmanure + Nfert = Napp, Npl = Ngr, Nres = Npast
		// TODO: If considering rotations and fertilisation/plouging strategies then implement tables 12, 14 and 16
		double Nsup;         //       Nitrogen supply factor                                                                Kg N ha 1
//		Nmin                 //       Nitrogen supply from soil                                                             Kg N ha 1
//		Napp                 //       Nitrogen application   (Nmanure + Nfert)                                              Kg N ha 1
		int Npl        = 40; //       Nitrogen supply from ploughed grassland (DataTable 12)                                    Kg N ha 1
		int Ncc        = 20; //       Nitrogen supply from a catch crop (DataTable 14)                                          Kg N ha 1
		int Nres       = 10; //       Nitrogen supply from previous applications (DataTable 16)                                 Kg N ha 1
		double Nmin          =       20; //                                                                                 Kg N ha 1
		Nsup                 =       Napp + Nmin + Npl + Ncc + Nres;

		double NYmaize;      //       Nitrogen yield maize                                                                  Kg N ha 1
//		Nsup                 //       Nitrogen supply factor                                                                Kg N ha 1
		a                    =       61.26;
		b                    =       0.1749;
		c                    =       -0.03812;
		NYmaize              =       a + b * Nsup + c * Nsup * Nsup / 100;   //                                               Kg N ha 1

		//-----------------------[ Dry matter yield ]------------------------

		// TODO: Does YIELDSTm = DMYNitrogen
		double DMYnitrogen;
//		double YIELDSTm;     //       Standard dry matter yield of maize at a certain nitrogen level          kg DM ha 1
//		Nupt                 //       Uptake of nitrogen by the plant                                                 kg
		a                    =       -4.82;
		b                    =       1.1038;
		c                    =       -0.192;
		DMYnitrogen          =       a + b * Nupt + c * Math.pow(Nupt, 2) * 10;
		
		double NitrogenMaize;       //       Factor of nitrogen on yield                                                 kg kg 1
//		DMYnitrogen          //       Standard dry matter yield of maize at the corresponding nitrogen supply kg DM ha 1
//		DMYexp               //       Average DM yield of maize experiments
		double DMYexp               =       14850;//                                                                   kg DM ha 1
		NitrogenMaize       =       DMYnitrogen / DMYexp;

		double DroughtMaize; //          Yield reduction due to water limitation (DataTable 17)                                                        %
//		soilType     // String describing soil type
//		hGroundWater // Average highest groundwater level (cm under field level)
//		lGroundWater // Average lowest groundwater level (cm under field level)
		DroughtMaize        = DroughtMaizeTable[iWaterTable(hGroundWater, lGroundWater)][iSoilType.get(soilType)];

		// TODO: Factors regarding weeding, sowing and rotation not included -could be implemented
		double DMYmaize;       //        Actual maize yield                                                                               kg DM ha 1
//		DroughtMaize           //        Damage by drought (DataTable 17)                                                                              %
//		WeedMaize              //        Factor of weed removal (DataTable 18)                                                                    kg kg 1
//		RotationMaize          //        Factor of crop rotation”(DataTable 19)                                                                   kg kg 1
//		SowMaize               //        Factor of sowing (DataTable 20)                                                                          kg kg 1
//		NitrogenMaize          //        Factor of nitrogen on yield                                                                          kg kg 1
//		DMYpot                 //        Current potential maize yield without water and nitrogen limitations
		double DMYpot                 =        17113;  //                                                                                          kg DM ha 1
		DMYmaize               =        DMYpot * ((100 - DroughtMaize) / 100) * NitrogenMaize;// * WeedMaize * RotationMaize * SowMaize;

		//--------------------------[ Feeding value ]-------------------------

		// TODO: No description of variable DVEsm
		double DVEsm;
//		Nsm;                  //       Nitrogen in silage of maize                                              g kg dm 1
//		Nsup                 //       Nitrogen supply                                                             kg ha 1
		a                    =       43.297;
		b                    =       0.0222;
		DVEsm                =       a + b * Nsup;

		// TODO: No description of variable OEBsm
		double OEBsm;
//		Nsm;                  //       Nitrogen in silage of maize                                              g kg dm 1
//		Nsup                 //       Nitrogen supply                                                             kg ha 1
		a                    =       -40.98;
		b                    =       0.06544;
		OEBsm                =       a + b * Nsup;
	
		System.out.println(
				"DroughtMaize = " + DroughtMaize + "\n" + 
				"DMYpot       = " + DMYpot + "\n" + 
				"DMYmaize     = " + DMYmaize + "\n"
		);
	}
	
	
	
	
	private static final String DISK_MOWER = "Disk Mower";
	private static final String CONDITIONER = "Conditioner";
	private static final String PICK_UP = "Pick Up";
	private static final String CHOPPED = "Chopped";
	static class SilageHarvestIn{
		String cuttingMethod;
		String harvestMethod;
		int waitingPeriod;
		public SilageHarvestIn(String cuttingMethod, int waitingPeriod, String harvestMethod){
			this.cuttingMethod = cuttingMethod;
			this.harvestMethod = harvestMethod;
			this.waitingPeriod = waitingPeriod;
		} 
		public int hashCode(){
			return cuttingMethod.hashCode() +  harvestMethod.hashCode() + waitingPeriod;
		}
	}
	static class SilageHarvestOut{
		int NH3fraction;
		int DMper;
		public SilageHarvestOut(int NH3fraction, int DMper){
			this.NH3fraction = NH3fraction;
			this.DMper = DMper;
		}
	}
	// DMper        : Dry matter fraction of silage (DataTable 30)        %
	// NH3 fraction : Ammonia fraction (DataTable 30)              g kg DM 1
	private static final Hashtable<SilageHarvestIn, SilageHarvestOut> SilageHarvest
		= new Hashtable<SilageHarvestIn, SilageHarvestOut>();
	static{
		//                                    Cutting    Wilting      Harvest
		//                                    Method     Period (d)   Method                NH3fraction  DMper
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      PICK_UP), new SilageHarvestOut(11,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      PICK_UP), new SilageHarvestOut(12,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      PICK_UP), new SilageHarvestOut(13,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      CHOPPED), new SilageHarvestOut( 9,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      CHOPPED), new SilageHarvestOut(10,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      CHOPPED), new SilageHarvestOut(11,  25 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      PICK_UP), new SilageHarvestOut( 9,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      PICK_UP), new SilageHarvestOut(10,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      PICK_UP), new SilageHarvestOut(11,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      CHOPPED), new SilageHarvestOut( 9,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      CHOPPED), new SilageHarvestOut(10,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      CHOPPED), new SilageHarvestOut(11,  40 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      PICK_UP), new SilageHarvestOut( 9,  45 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      PICK_UP), new SilageHarvestOut(10,  45 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      PICK_UP), new SilageHarvestOut(11,  45 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     2,      CHOPPED), new SilageHarvestOut( 9,  45 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     3,      CHOPPED), new SilageHarvestOut(10,  45 ));
		SilageHarvest.put(new SilageHarvestIn(DISK_MOWER,     4,      CHOPPED), new SilageHarvestOut(11,  45 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      PICK_UP), new SilageHarvestOut( 8,  25 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      PICK_UP), new SilageHarvestOut(10,  25 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      CHOPPED), new SilageHarvestOut( 8,  25 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      CHOPPED), new SilageHarvestOut( 9,  25 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      PICK_UP), new SilageHarvestOut( 8,  40 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      PICK_UP), new SilageHarvestOut( 9,  40 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      CHOPPED), new SilageHarvestOut( 8,  40 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      CHOPPED), new SilageHarvestOut( 9,  40 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      PICK_UP), new SilageHarvestOut( 8,  45 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      PICK_UP), new SilageHarvestOut( 9,  45 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    1,      CHOPPED), new SilageHarvestOut( 8,  45 ));
		SilageHarvest.put(new SilageHarvestIn(CONDITIONER,    2,      CHOPPED), new SilageHarvestOut( 9,  45 ));
	}
	
	
	private static final int[][] NH3FractionDMper = {
	};


	private void feedIntake(){
		
		// Inputs
		double Ncow;  // Number of cows
		double Nheif; // Number of animals between 1 and 2 years old
		double Ncalf; // Number of animals between 0 and 1 years old
		double Graz;  // Grazing system (DataTable 1)
		double FIc;   // Concentrate intake                          kg DM cow 1 d 1
		double FIr;   // Forage supplementation during grazing       kg DM cow 1 d 1
		String silageCuttingMethod = DISK_MOWER;
		int wiltingPeriod = 2;
		String silageHarvestMethod = PICK_UP;
		double Day = 0;          // Day number of the harvest day
		
		// From silage crop submodel
		double RE = 0;           // Crude protein                                        g kg DM 1
		double RC = 0;           // Crude fibre                                          g kg DM 1
		double RAS = 0;          // Crude ash                                            g kg DM 1

	}
	
	private void cowModel(
			double RC,   // Crude fibre                                                   g kg DM 1
			double RE,   // Crude protein                                                      g kg DM 1
			double VOS,  // Digestible organic matter                                     g kg DM 1
			double DS,   // Dry matter 			
			double DG,         //  Days of gestation                                                 d
			double DL,     // Days of lactation                                                          d
			double PAR,      // Parity
			double P,           //  Number of parity
			double FIsm, // Feed intake of silage of maize                                     kg DM d 1
			double FIsg, // Feed intake of silage of grass                                     kg DM d 1
			double FIc,   // Feed intake of concentrate                                             kg DM d 1
			double CONCint,     //   Intake of concentrate                                                  kg DM
			double GRint,       //   Intake of grass                                                        kg DM
			double SGint,       //   Intake of silage of grass                                              kg DM
			double SMint,       //   Intake of silage of maize                                              kg DM
			double CONCVEM,     //   Energy value of concentrate                                       VEM kg DM 1
			double GRVEM,       //   Energy value of grass                                             VEM kg DM 1
			double SGVEM,       //   Energy value of silage of grass                                   VEM kg DM 1
			double SMVEM       //   Energy value of silage of maize                                   VEM kg DM 1
	){
		// ===========================[ MAIN COW MODEL ]=========================
		
		// OUTPUTS
		// Intake of grass at grazing
		// Milk Production
		// Protein Requirement
		
		// Coefficient values, defined for specific equations
		double a, a0, a1, a2, a3, a4, b,
		       c, c0, c11, c12, c21, c31, c32, c51,
		       d, e, g, ra, rb, rg;
		
		// -----------------------[ Saturation Values ]--------------------------
		
		double SVc;  // Saturation value of concentrate                              SV kg DM 1
//		RC   // Crude fibre                                                   g kg DM 1
		c0   = -1.1483;
		c31  = 1.335;
		SVc  = Math.exp(c0 + c31 * (RC - 140) / 1000);
		
		// TODO : Does sqr mean squared or square root, disambiguate bracketing
		double SVgr; // Saturation value of grass                                    SV kg DM 1
//		RC   // Crude fibre                                                   g kg DM 1
//		VOS  // Digestible organic matter                                     g kg DM 1
		c0   = -0.08334;
		c31  = -0.206;
		c32  = 0.01419;
		c51  = -0.7443;
		SVgr = Math.exp(c0 + c31 * (RC - 230) / 1000 + c32 * Math.pow(RC - 230, 2) / 1000 + c51 * (VOS - 705) / 1000);     
		
		// TODO: Does sqr men squared or square root?
		double SVsg; // Saturation value of silage of grass                               SV kg DM 1
//		DS   // Dry matter                                                                %
//		RE   // Crude protein                                                      g kg DM 1
//		RC   // Crude fibre                                                        g kg DM 1
		c11  = -1.613;
		c12  = 0.0991;
		c21  = -0.3321;
		c31  = 1.551;
		SVsg = Math.exp(c11 * (DS - 45) / 1000 + c12 * Math.pow(DS - 45, 2) / 1000 + c21 * (RE - 170) / 1000 + c31 * (RC - 240) / 1000);
		
		// TODO: Does sqr men squared or square root?
		double SVsm; // Saturation value of silage of maize                               SV kg DM 1
//		DS   // Dry matter                                                                %
//		VOS  // Digestible organic matter                                          g kg DM 1
		c0   = -0.21658;
		c11  = -2.737;
		c12  = 2.962;
		c51  = -0.559;
		SVsm = Math.exp(c0 + c11 * (DS - 33) / 1000 + c12 * Math.pow(DS - 33, 2) / 1000 + c51 * (VOS - 695) / 1000);
		
		double SVr;  // Saturation value of roughages                                     SV kg DM 1
//		FIsm // Feed intake of silage of maize                                     kg DM d 1
//		FIsg // Feed intake of silage of grass                                     kg DM d 1
//		SVsm // Saturation value of silage of maize                               SV kg DM 1
//		SVsg // Saturation value of silage of grass                               SV kg DM 1
		SVr  = (FIsm * SVsm + FIsg * SVsg) / (FIsm + FIsg);
		
		
		// -------------------------[ Feed Intake ]----------------------
		
		double FICAP; // Feed intake capacity of cows in lactation                                  SV d 1
//		double PAR;   // Parity
//		double DL;    // Days of lactation                                                           days
//		double DG;    // Days of gestation                                                           days
		a0    = 8.0838;
		a1    = 3.2956;
		ra    = 1.2758;
		b     = 0.3983;
		rb    = 0.05341;
		d     = 0.06907;
		FICAP = (a0 + a1 * (1 - Math.exp(-ra * PAR))) * Math.exp(b * (1 - Math.exp(-rb * DL))) * (1 + d * (DG / 220));

		double FIr;   // Feed intake of roughage indoor                                         kg DM d 1
//		double FICAP; // Feed intake capacity of cows in lactation                                  SV d 1
//		double FIc;   // Feed intake of concentrate                                             kg DM d 1
//		double SVc;   // Saturation value of concentrate                                       SV kg DM 1
		FIr   = (FICAP - FIc * SVc) / SVr;

		double FIgr;  // Feed intake of grass at grazing                                        kg DM d 1
//		double FICAP; // Feed intake capacity of cows in lactation                                  SV d 1
//		double FIc;   // Feed intake of concentrate                                             kg DM d 1
//		double FIr;   // Feed intake of roughage besides grass at pasture                       kg DM d 1
//		double SVc;   // Saturation value of concentrate                                       SV kg DM 1
//		double SVr;   // Saturation value of roughage besides grass                            SV kg DM 1
		FIgr = (FICAP - FIc * SVc - FIr * SVr) / SVgr;
				
		// -----------------------[ Production of standard cow ]----------------------
		
		// TODO: Does Exp mean exponent raised to e, does ln mean natural log
		double MLK;     // Milk production                                                           kg
//		PAR     // Parity
//		DL      // Days of lactation                                                          d
		a0      = 142.23;
		a1      = 100.77;
		a2      = 0.3912;
		ra      = 1.0267;
		b       = 0.494;
		rb      = 0.10262;
		rg      = 1.957;
		g       = 5.6304;
		MLK     = (a0 + (a1 - a2 * DL) * (1 - Math.exp(-ra * PAR))) * 0.1 * Math.exp((b * (1 - Math.exp(-rb * DL))) / (1 + Math.exp(rg * (Math.log(DL) - g))));
		
		double MLKfat;  // Fat in milk                                                                %
//		PAR     // Parity
//		DL      // Days of lactation                                                          d
		a0      = 533.01;
		a1      = 27.98;
		ra      = 1.153;
		b       = -0.28574;
		rb      = 0.06502;
		rg      = 3.345;
		g       = 5.6315;
		MLKfat        = (a0 + a1 * (1 - Math.exp(-ra * PAR))) * Math.exp((b * (1 - Math.exp(-rb * DL))) / (1 + Math.exp(rg * (Math.log(DL) - g))));
		
		double MLKprot; // Protein in milk                                                            %
//		PAR     // Parity
//		DL      // Days of lactation                                                          d
		a0      = 471.92;
		a1      = 13.6;
		ra      = 2.21;
		b       = -0.44212;
		rb      = 0.10239;
		rg      = 1.5358;
		g       = 5.6632;
		MLKprot = (a0 + a1 * (1 - Math.exp(-ra * PAR))) * Math.exp((b * (1 - Math.exp(-rb * DL))) / (1 + Math.exp(rg * (Math.log(DL) - g))));
		
		double MMLK;    // Standardized milk production    kg
//		MLK     // Standard production of milk     kg
//		MLKfat  // Fat in milk                      %
//		MLKprot // Protein in milk                  %
		a1      = 0.337;
		a2      = 0.116;
		a3      = 0.060;
		MMLK    = a1 * MLK + a2 * MLKfat + a3 * MLKprot;
		
		// --------------------------[ Energy Requirement ]--------------------------------

		double ENEbasic; // Basic energy requirement      VEM
//		PAR      // Parity
		a0       = 7020;
		a1       = 1116.5;
		ra       = 1.099;
		ENEbasic = a0 + a1 * (1 - Math.exp(-ra * PAR));
		
		double ENEmob;   // Energy mobilized              VEM
//		ENEbasic // Basic energy                  VEM
//		PAR      // Parity
//		DL       // Days of lactation             days
		a3       = 0.8070;
		a4       = -0.05497;
		ra       = 0.024481;
		ENEmob   = ENEbasic * (a3 - a4 * PAR) * Math.exp(-ra * DL);

		double ENEmlk; // Energy requirement milk production         VEM
//		MLK    // Standardized production of milk             kg
		b      = 434.18;
		ENEmlk = b * MMLK;

		double ENEtot;   // Energy requirement total      VEM
//		ENEbasic // Basic energy                  VEM
//		ENEmob   // Energy for mobilization       VEM
//		ENEmlk   // Energy for milk               VEM
		ENEtot   = ENEbasic + ENEmob + ENEmlk;
		
		// ----------------------------[ Realized Milk Production ]-------------------
		
		double ENEintake;   //   Total intake of energy                                                   VEM
		ENEintake   =   CONCint * CONCVEM + GRint * GRVEM + SGint * SGVEM + SMint * SMVEM;

		double FACene;     //   Factor for surplus of energy
//		ENEintake  //   Intake of energy                                                          VEM
//		ENEtot     //   Total requirement of energy                                               VEM
		FACene     =   ENEintake / ENEtot;
		
		// TODO: Does sqr mean Square Root?
		double MLKreal;   //   Milk production realized                                                     kg
//		FACene    //   Factor for surplus of energy
//		MMLK      //   Standardized milk production                                                 kg
		a         =   1.07695;
		b         =   1.292;
		MLKreal   =   (1 + (1 - Math.exp(a * (1 - FACene)) - b * Math.pow(1 - FACene, 2))) * MMLK;
		
		//-----------------------------[ Weight ]----------------------------
		
		// TODO: Does ln mean natural log, ie log to base e
		double WEIGHT; // Weight                                                                    kg
//		PAR    // Parity
//		DL     // Days of lactation                                                          d
		a0     = 594.35;
		a1     = 137.57;
		ra     = 0.3118;
		b      = -0.10314;
		rb     = 0.1206;
		rg     = 9.00;
		g      = 5.3279;
		WEIGHT = (a0 + a1 * (1 - Math.exp(-ra * PAR))) * Math.exp((b * (1 - Math.exp(-rb * DL))) / (1 + Math.exp(rg * (Math.log(DL) - g))));
		
		// --------------------[ Protien Requirement ]--------------
		
		double PROTmnt;   //  Protein requirement maintenance                                g DVE
//		WEIGHT    //  Weight                                                            kg
		a         =  2.75;
		b         =  0.2;
		c         =  0.67;
		PROTmnt   =  (a * Math.pow(WEIGHT, 0.5) + b * Math.pow(WEIGHT, 0.6)) / c;
		
		// TODO: Is P = PAR??
		// CORRECTION: boolean false = 0, true = 1
		double PROTgr;     //  Protein requirement growth                                    g DVE
//		P           //  Number of parity
//		PROTgr     =  37 * (P=1) + 19 * (P=2); // orig
		PROTgr     =  P == 1 ? 37 : P == 2 ? 19 : 0;
		
		double PROTmlk;    //  Protein requirement milk production                           g DVE
//		MLKreal    //  Milk production realized                                         kg
//		MLKprot    //  Protein in milk                                                   %
		a          =  1.396;
		b          =  0.000195;
		PROTmlk    =  (MLKreal * MLKprot * 10) / (100 / (a + b * (MLKreal * MLKprot * 10)));
		
		double PROTgst;    //  Protein requirement gestation                                 g DVE
//		DG         //  Days of gestation                                                 d
		a          =  34.375;
		b          =  8.5357;
		c          =  13.1201;
		d          =  0.00262;
		e          =  0.5;
		PROTgst    =  a * Math.exp(b - c * Math.exp(-d * DG) - d * DG) / e;
		
		// TODO: Variable names don't match defs given (eg a1=b??)
		// CORRECTION: boolean false = 0, true = 1
		double PROTmob;    //  Protein requirement mobilization                              g DVE
//		ENEintake  //  Intake of energy                                                VEM
//		ENEtot     //  Total requirement of energy                                     VEM
		a1         =  57;
		a2         =  45;
//		PROTmob    =  (ENEintake - ENEtot) / 1000 * a * (ENEintake > ENEtot) + (ENEintake - ENEtot) / 1000 * b * (ENEintake < ENEtot);
		PROTmob    =  (ENEintake - ENEtot) / 1000 * a1 * ((ENEintake > ENEtot) ? 1 : 0) + (ENEintake - ENEtot) / 1000 * a2 * ((ENEintake < ENEtot) ? 1 : 0);

		double PROTtot;    //  Protein requirement total                                     G DVE
//		PROTmnt    //  Protein requirement maintenance                               g DVE
//		PROTgr     //  Protein requirement growth                                    g DVE
//		PROTmlk    //  Protein requirement milk production                           g DVE
//		PROTgst    //  Protein requirement gestation                                 g DVE
//		PROTmob    //  Protein requirement mobilization                              g DVE
		PROTtot    =  PROTmnt + PROTgr + PROTmlk + PROTgst + PROTmob;
		
	}

	

	// Growth estimation for young stock (g/day)
	private static double calfGrowth(int age){
		// age                      | growth in g/day
		if(age  <   1 || age >  730) return 0;
		if(age >=   1 && age <=  61) return 575;
		if(age >=  62 && age <= 242) return 825;
		if(age >= 243 && age <= 365) return 700;
		if(age >= 366 && age <= 456) return 700;
		if(age >= 457 && age <= 670) return 625;
		return (730 - age) / 60.0 * 625;
	}

	
	private void calfModel(
			int age, // Age in days (1 to 730)
			double FEEDVEM, // VEM in roughage                                                          VEM kg DM 1
			double CONCVEM, // VEM in concentrate                                    VEM kg DM 1
			double MLKVEM,  // VEM in milk                                              VEM kg 1
			double FIc,     // Concentrate intake                                                            kg DM
			double FImlk,   // Milk intake                                                    kg
			boolean grazing, // Whether the calf is grazing
			double DG      // Days of gestation                                             d
		){
		
		// OUTPUTS
		// Intake of roughage
		// Protein requirement
		
		
		// ===========================[ YOUNG STOCK MODEL ]=========================

		// Coefficient values, defined for specific equations
		double a, b, c, d, e, f, f1, f2, f3;

		double QVALUE = 0;
		if(age >=   1 && age <=  69) QVALUE = 0.70;
		if(age >=  70 && age <= 182) QVALUE = 0.60;
		if(age >= 183 && age <= 730){
			if(grazing)              QVALUE = 0.60;
			else                     QVALUE = 0.55;
		}

		// Growtih estimation converted to kg
		double GROWTH = calfGrowth(age) / 1000;

		// Weight estimation in grams (in kg)
		// TODO: What is the initial weight?
		double WEIGHT = 0; // Initial weight 
		for(int i = 1; i <= age; i++){
			// Add weight in grams
			WEIGHT += calfGrowth(age);
		}
		// Convert to kg
		WEIGHT /= 1000;
		
		// -------------------------[ Energy Requirement ]------------------
		
		double Wfetus;  // Weight foetus (incl. fluid)                    kg
//		DG      // Days of gestation                               d
		a       = -0.6087;
		b       = 0.0174;
		c       = 0.9;
		Wfetus  = (-a + a * Math.exp(b * DG)) * c;
		
		double UFmlk;  // Utilization factor for milk
//		QVALUE // Q value (DataTable 21)
		a      = 0.4632;
		b      = 0.0024;
		UFmlk  = a + b * QVALUE;
	
		double UFmnt;   // Utilization factor for maintenance
		a       = 0.554;
		b       = 0.00287;
		UFmnt   = a + b * QVALUE;
		
		double UFgr;    // Utilization factor for growth
		a       = 0.006;
		b       = 0.0078;
		UFgr    = a + b * QVALUE;
		
		double ENEmnt_;  // Energy for maintenance                        VEM
//		WEIGHT  // Weight animal (DataTable 22)                       kg
//		Wfoetus // Weight foetus                                  kg
//		KJTOVEM // Conversion factor from KJ to VEM
		a       = 330;
		b       = 6.0;
		ENEmnt_  = a * Math.pow(WEIGHT, 0.75) + Wfetus * b;
		
		// Correction made: Original used b * GROWTH, changed to b * WEIGHT
		double ENEgr_;   // Energy for growth                                          VEM
//		GROWTH  // Growth of animal (DataTable 22)                               kg d 1
//		WEIGHT  // Weight animal (DataTable 22)                       kg
//		KJTOVEM // Conversion factor from KJ to VEM
		a       = 500;
		b       = 6.0;
		c       = 4.184;
		d       = 0.3;
		ENEgr_   = (((a + b * WEIGHT) * c * GROWTH) / (1.0 - GROWTH * d));

		// CORRECTION : Enegr and ENEmnt calculated without KJTOVEM
		// to resolve circular dependency
		double APL;     // Animal Production Level
//		ENEmnt  // Energy for maintenance                                     VEM
//		ENEgr   // Energy for growth                                          VEM
		APL     = 1.0 + ENEgr_ / ENEmnt_;
		
		double UFmgr;   // Utilization factor maintenance and growth
//		UFgr    // Utilization factor for growth
//		UFmnt   // Utilization factor for maintenance
//		APL     // Animal Production Level
		UFmgr   = UFgr / (((UFgr - UFmnt) / (UFmnt * APL)) + 1);
		
		double KJTOVEM; // Conversion factor from KJ to VEM
//		UFmlk   // Utilization factor for milk
//		UFmgr   // Utilization factor maintenance and growth
		f1      = 0.9752;
		f2      = 1.15;
		f3      = 6.90;
		KJTOVEM =  (f1 * UFmlk / UFmgr) * f2 / f3;

		double ENEmnt;  // Energy for maintenance                        VEM
//		WEIGHT  // Weight animal (DataTable 22)                       kg
//		Wfoetus // Weight foetus                                  kg
//		KJTOVEM // Conversion factor from KJ to VEM
		a       = 330;
		b       = 6.0;
		ENEmnt  = a * Math.pow(WEIGHT, 0.75) * KJTOVEM + Wfetus * b;
		
		// CORRECTION: Original used b * GROWTH, changed to b * WEIGHT
		double ENEgr;   // Energy for growth                                          VEM
//		GROWTH  // Growth of animal (DataTable 22)                               kg d 1
//		WEIGHT  // Weight animal (DataTable 22)                       kg
//		KJTOVEM // Conversion factor from KJ to VEM
		a       = 500;
		b       = 6.0;
		c       = 4.184;
		d       = 0.3;
		ENEgr   = (((a + b * WEIGHT) * c * GROWTH) / (1.0 - GROWTH * d)) * KJTOVEM;
		
		double ENEgst;  // Energy for gestation                                       VEM
//		DG      // Days of gestation                                             d
		a       = 17.5;
		b       = 0.0174;
		c       = 0.9;
		ENEgst  = a * Math.exp(b * DG) * c;

		double ENEgrz; // Energy for grazing                    VEM
//		WEIGHT // Weight of animal (DataTable 22)            kg
		a      = 150;
		b      = 1.67;
		ENEgrz = a + b * WEIGHT;

		double ENEtot;  // Energy requirement total                      VEM
//		ENEmnt  // Energy for maintenance                        VEM
//		ENEgr   // Energy for growth                             VEM
//		ENEgst  // Energy for gestation                          VEM
//		ENEgrz  // Energy for grazing                            VEM
		ENEtot  = ENEmnt + ENEgr + ENEgst + ENEgrz;
		
		// ---------------------[ Feed Intake ]------------------
		
		double CONCsub; // Substitution factor of concentrate and roughage     kg DM kg DM 1
//		WEIGHT  // Weight of animal (DataTable 22)                                    kg
//		FEEDVEM // VEM in roughage                                       VEM kg DM 1
//		FIc     // Concentrate intake                                         kg DM
		a       = 0.0219;
		b       = 0.000785;
		c       = 0.000608;
		d       = 0.148;
		CONCsub = a * FIc - b * WEIGHT + c * FEEDVEM + d;

		// TODO: problem with vars, does CONCsub = CONCpush
		double FImax;   // Maximal intake of roughage                                                    kg DM
//		WEIGHT  // Weight of animal (DataTable 22)                                                       kg
//		FEEDVEM // VEM in roughage                                                          VEM kg DM 1
//		FIc     // Concentrate intake                                                            kg DM
//		CONCsub // Substitution factor of concentrate and roughage                        kg DM kg DM 1
		double CONCpush = CONCsub;
		a       = -2.14774;
		b       = 0.57851;
		c       = 0.46574;
		d       = 850.0;
		e       = 0.3727;
		f       = 0.000991;
		FImax   = (a + b * Math.pow(WEIGHT, c) - ((d - FEEDVEM) / 100.0) * (e + f * WEIGHT)) - FIc * CONCpush;

		double FIr;     // Feedintake of roughage                                     kg DM
//		ENEtot  // Energy requirement total                                     VEM
//		FImlk   // Milk intake                                                    kg
//		FIc     // Concentrate intake                                         kg DM
//		MLKVEM  // VEM in milk                                              VEM kg 1
//		CONCVEM // VEM in concentrate                                    VEM kg DM 1
//		FEEDVEM // VEM in roughage                                       VEM kg DM 1
		FIr     = (ENEtot - FImlk * MLKVEM - FIc * CONCVEM) / FEEDVEM;
		
		// -------------------[ Protein Requirement ]-------------------
		
		// TODO: is ln log? Ambigous bracketing!
		double LLG;      //  Empty body weight                                     kg
//		WEIGHT   //  Weight of animal (DataTable 22)                           kg
		a        =  -0.2855;
		b        =  1.023;
		LLG      =  Math.exp(a + b * Math.log(WEIGHT));

		// TODO: Does ln mean natural log
		double WEIGHTf;  //  Weight of fat                                         kg
//		LLG      //  Empty body weight                                     kg
		a        =  1.5;
		b        =  -1.680;
		c        =  0.0189;
		d        =  0.1609;
		e        =  2;
		WEIGHTf  =  a * Math.exp(b + c * Math.log(LLG) + d * Math.pow(Math.log(LLG), e));
		
		double GRLLG;   // Growth empty body weight                                             kg
//		LLG     // Empty body weight                                                    kg
//		WEIGHT  // Weight of animal (DataTable 22)                                          kg
//		GROWTH  // Growth of animal (DataTable 22)                                       kg d 1
		a       = 1.023;
		GRLLG   = (LLG / WEIGHT) * a * GROWTH;
		
		double GROWTHf; // Growth of fat                                                        kg
//		WEIGHTf // Weight of fat                                                        kg
//		LLG     // Empty body weight                                                    kg
//		GRLLG   // Growth empty body weight                                             kg
		a       = 0.0189;
		b       = 0.1609;
		c       = 0.76;
		d       = 0.613548;
		e       = 1.78;
		GROWTHf = (WEIGHTf / LLG * (a + 2 * b * Math.log(LLG)) * c) / d * Math.pow(GRLLG, e);
		
		double GROWTHp; // Growth of protein                                                    kg
//		GROWTHf // Growth of fat                                                        kg
//		GRLLG   // Growth empty body weight                                             kg
//		LLG     // Empty body weight                                                    kg
		a       = 1.060;
		b       = 0.1541;
		c       = 0.06;
		GROWTHp = a * b * (GRLLG - GROWTHf) * Math.pow(LLG - WEIGHTf, c);
		
		double PROTgr;  // Protein requirement growth                                        g DVE
//		GROWTHp // Growth of protein                                                    kg
//		WEIGHT  // Weight of animal (DataTable 22)                                          kg
		a       = 0.8;
		b       = 0.0009;
		c       = 0.4;
		PROTgr  = 1000.0 * GROWTHp / Math.max(a - b * WEIGHT, c);
		
		// TODO: Weight tabe (table 22)
		double PROTmnt; //  Protein requirement maintenance                     g DVE
//		WEIGHT  //  Weight of animal (DataTable 22)                            kg
		a       =  2.75;
		b       =  0.2;
		c       =  0.67;
		PROTmnt =  (a * Math.pow(WEIGHT, 0.5) + b * Math.pow(WEIGHT, 0.6)) / c;
		
		double PROTgst; // Protein requirement gestation                                     g DVE
//		DG      // Days of gestation                                                     d
		a       = 34.375;
		b       = 8.5357;
		c       = 13.1201;
		d       = 0.00262;
		e       = 0.5;
		f       = 0.9;
		PROTgst = (a * Math.exp(b - c * Math.exp(-d * DG) - d * DG) / e) * f;

		double PROTtot;  //  Protein requirement total                          g DVE
//		PROTmnt  //  Protein requirement maintenance                    g DVE
//		PROTgr   //  Protein requirement growth                         g DVE
//		PROTgst  //  Protein requirement gestation                      g DVE
		PROTtot  =  PROTmnt + PROTgr + PROTgst;

	}
	
	public void feedSupply(
			String silageCuttingMethod,
			int wiltingPeriod,
			String silageHarvestMethod,
			double VOS, // Digestable organic matter of conserved grass silage g kg DM 1
			double RC,  // Crude fibre                                         g kg DM 1
			double RE,  // Crude protein                                       g kg DM 1
			double RAS, // Crude ash                                           g kg DM 1
			// TODO: Is this number of days since harvest??
			int Day   // Day number of the harvest day
	){
		// OUTPUT
		// Digestable organic matter of grass silage
		// Digestable organic matter of crude protein
		// Fermentable organic matter of silage
		
		// ===============================[ Feed Supply ]============================
		
		// DataTable 28 Grazing losses
		// Grazing system          Losses (%)
		// Zero grazing            7
		// Restricted grazing      16
		// Day and night grazing   22
		
		// DataTable 29 harvest losses
		// Grazing system          Losses (%) per activity
		// Tedding                 1.2
		// Loading                 2.0

		// DataTable 31 feeding losses
		// Feed                    Losses (%)
		// Roughage                5
		// Concentrate             2

		// Coeficient values
		double a, b, c, d, e;
		
		double VOS_cons;     // Digestable organic matter of conserved grass silage  g kg DM 1
//		VOS // Digestable organic matter of conserved grass silage g kg DM 1
//		RC  // Crude fibre                                         g kg DM 1
//		RAS // Crude ash                                           g kg DM 1
		double DMper         // Dry matter fraction of silage (DataTable 30)                    %
		= SilageHarvest.get( new SilageHarvestIn(
					silageCuttingMethod,
					wiltingPeriod,
					silageHarvestMethod)).DMper;
		a            =   -0.77;
		b            =   -1.23;
		c            = 1027;
		d            =   -0.03;
		VOS_cons     = a * RC + b * RAS + c + d * DMper * 10;
		
		double VRE_cons;     // Digestable crude protein of conserved grass silage   g kg DM 1
//		RE  // Crude protein g kg DM 1
//		RAS // Crude ash     g kg DM 1
		double NH3fraction   // Ammonia fraction (DataTable 30)                          g kg DM 1 
		= SilageHarvest.get( new SilageHarvestIn(
					silageCuttingMethod,
					wiltingPeriod,
					silageHarvestMethod)).NH3fraction;
		a            =   0.895;
		b            =   0.04;
		c            = -40;
		d            =   8;
		e            =  -0.7;
		VRE_cons     = a * RE + b * RAS + c + (d + e * NH3fraction);
		
		double PCBRE_cons;   // Proportion of undegraded protein in conserved silage        %
//		RE    // Crude protein                            g kg DM 1
//		DMper // Dry matter fraction of silage (DataTable 30)        %
//		Day   // Day number of the harvest day
		a            = -0.09;
		b            =  0.04;
		c            = 29.5;
		d            =  0.3;
		PCBRE_cons   = c + a * RE + b * Math.min((Day + 90), 180) + d * DMper;

		double FOS_cons;     // Fermentable organic matter of conserved silage                           g kg DM 1
//		VOS          // Digestable organic matter                g kg DM 1
//		RE           // Crude protein                            g kg DM 1
//		DMper        // Dry matter fraction of silage (DataTable 30)        %
//		NH3 fraction // Ammonia fraction (DataTable 30)              g kg DM 1
		a            = -40;
		b            =  -0.5;
		c            =  -3;
		d            =   2;
		e            = 170;
		FOS_cons     = VOS_cons - (RE * PCBRE_cons / 100) + b * (c * DMper + d * NH3fraction + e) + a;
	}	
	
	
	
	
	public static void main(String[] args){
		testGrass();
//		testMaize();
	}
	
	public static void testGrass(){
		
		DairyWise c = new DairyWise();
		
		GraphPanel DMYgraph = new GraphPanel("DMYgrass/time", "Day of Year", "tonnes DM per hectare", true, false);
		DMYgraph.addSeries("DMY", Color.RED.darker());
		DMYgraph.displayWindow(50, 50);

		GraphPanel feedGraph = new GraphPanel("Feeding Value", "Day of Year", "g/KG of DM", true, true);
		feedGraph.addSeries("VOS", Color.GREEN.darker());
		feedGraph.addSeries("RC", Color.YELLOW.darker());
		feedGraph.addSeries("RE", Color.RED.darker());
		feedGraph.addSeries("RAS", Color.GRAY.darker());
		feedGraph.displayWindow(50, 400);
		
		// Feeding value data
//		double VOS, // Digestable organic matter of conserved grass silage g kg DM 1
//		double RC,  // Crude fibre                                         g kg DM 1
//		double RE,  // Crude protein                                       g kg DM 1
//		double RAS, // Crude ash                                           g kg DM 1

		int i = 0;
		int Napp = 120;                 //   Nitrogen application                                                                      kg N ha 1
		int PrevNapp = 120;				// Previous N application
		int SNS = 100;                 //   Soil nitrogen supply
		double Tstart = 0;         //          Starting day of a growing cycle                                                                          d
		double GRD = 0;              //      Day number from start growing cycle d
		int doy = 1;
		// TODO: Do all references to day the same. Eg harvest day, total days?
		int Day = 0;    // Day number of the harvest day
		int day = 0;    // Day number
		String soilType = SOIL_SAND_PEAT;     // String describing soil type
		int hGroundWater = 0; // Average highest groundwater level (cm under field level)
		int lGroundWater = 80; // Average lowest groundwater level (cm under field level)

		for(doy = 1; doy <= 365; doy++, GRD++){
			if(doy == 140) { i = 1; GRD = 0; Tstart = doy; PrevNapp = Napp; Napp = 90; }
			if(doy == 200){ i = 2; GRD = 0; Tstart = doy; PrevNapp = Napp; Napp = 60; }
			if(doy == 260){ i = 3; GRD = 0; Tstart = doy; PrevNapp = Napp; Napp = 60; }
			if(doy == 330){ i = 4; GRD = 0; Tstart = doy; PrevNapp = Napp; Napp = 30; }
//			if(doy == 250){ i = 5; GRD = 0; Tstart = doy; PrevNapp = Napp; Napp = 30; }
			System.out.println("\ni:" + i);
			c.grass(
					i, // Growth Cycle
					 Napp,                 //   Nitrogen application                                                                      kg N ha 1
					PrevNapp,				// Previous N application
					SNS,                 //   Soil nitrogen supply
					Tstart,         //          Starting day of a growing cycle                                                                          d
					GRD,              //      Day number from start growing cycle d
					doy,
					// TODO: Do all references to day the same. Eg harvest day, total days?
//					Day,    // Day number of the harvest day
//					 day,    // Day number
					soilType,     // String describing soil type
					hGroundWater, // Average highest groundwater level (cm under field level)
					lGroundWater // Average lowest groundwater level (cm under field level)
			);
			DMYgraph.addData("DMY", c.getDMYgrass());
			DMYgraph.repaint();
			
			feedGraph.addData("VOS", c.getVOS());
			feedGraph.addData("RC",  c.getRC());
			feedGraph.addData("RE",  c.getRE());
			feedGraph.addData("RAS", c.getRAS());
			feedGraph.repaint();
		}
	}
	
	public static void testMaize(){
		DairyWise c = new DairyWise();
		double Napp = 100;
		double Nupt = 0.0100;
		String soilType = SOIL_SAND_PEAT;
		int hGroundWater = 0;
		int lGroundWater = 80;
		
		c.maize(
				Napp,         //       Nitrogen application (Nmanure + Nfert)                                                           Kg N ha 1
				Nupt,         //       Uptake of nitrogen by the plant                                                 kg
				soilType,     // String describing soil type
				hGroundWater, // Average highest groundwater level (cm under field level)
				lGroundWater  // Average lowest groundwater level (cm under field level)
			);
		
	}

	
}
