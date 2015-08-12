package uk.co.crider.models.dairy.crop_2;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import uk.co.crider.models.NormalDistribution;
import uk.co.crider.models.dairy.RandomGenerator;

public class CropDriver {
	
	abstract static class Test{
		// Average set-up with no management
		Random rand = new Random(0);
		float rainAvg = 6;  // Average rainfall
		float rainVar = 2;  // Variability of rainfall
		float rainFreq = 0.03f; // Frequency of rain (per day)
		double TRR  = 4;     // Precipitation                                                        mm day-'
		double AIRG = 0;    // Irrigation table                                                      mm day-l
		double ANIT = 0;    // Fertilization table                                                   kg N lia-l day-l
		// See http://www.bom.gov.au/sat/glossary.shtml for explanation
		// 0.1 KW/m2 = 100 W/m2 = 100 J/m2/s = 100 * 3600 * 24 J/m2/d = 8640000/10E6 MJ/m2/d = 8.64
		double TRG = 8.64f;     // Global solar radiation                                              MJ m-2 day-l
		double TMOY = 15;    // Mean temperature                                                      °C
		double TMIN = 12;    // Minimum temperature                                                   °C
		public Test(){ init(); }
		abstract void init();
		abstract void day(int i);
		// Set basic properties
		void setTRRDry(){ TRR = 4; }
		void setTRRWet(){ TRR = 15; }
	}
	
	static Test[] tests = new Test[]{
			new Test(){ // 0 - Control - average params
				void init(){}
				void day(int i){}
			},
			new Test(){ // 1 - Testing random rainfall (dry), no management
				void init(){
					rainAvg = 5; // Average rainfall
					rainVar = 2; // Variability of rainfall
					rainFreq = 0.03f; // Frequency of rain (per day)
				}
				void day(int i){
					TRR = rand.nextFloat() < rainFreq ? (float)Math.pow(rand.nextFloat() * 2, rainVar) * rainAvg : 0; 
				}
			},
			new Test(){ // 2 - Testing random rainfall (wet), no management
				void init(){
					rainAvg = 15; // Average rainfall
					rainVar = 2; // Variability of rainfall
					rainFreq = 0.03f; // Frequency of rain (per day)
				}
				void day(int i){
					TRR = rand.nextFloat() < rainFreq ? (float)Math.pow(rand.nextFloat() * 2, rainVar) * rainAvg : 0; 
				}
			},
			new Test(){ // 3 - Test with real weather data, no management
				void init(){}
				void day(int i){
					TMOY = (float)RandomGenerator.temperature_edinburgh_2007[i % RandomGenerator.temperature_edinburgh_2007.length];
					TMIN = (float)RandomGenerator.mintemp_edinburgh_2007[i % RandomGenerator.mintemp_edinburgh_2007.length];
					TRR = (float)RandomGenerator.rainfall_edinburgh_2007[i % RandomGenerator.rainfall_edinburgh_2007.length];
					TRG = (float)RandomGenerator.solar_rad_edinburgh_2007[i % RandomGenerator.solar_rad_edinburgh_2007.length] * 86.4f; // Conversion from KW/m2 to MJ/m2/d: 1KW/m2 = 1000W/m2 = 1000J/m2/s = 3600000 J/m2/h = 3.6 MJ/m2/h = 86.4 MJ/m2/d
				}
			}
	};
	
	
	/** Class for viewing, testing and stress testing the crop models */
	public static void main(String[] args) {
		
		boolean showGraphs = true;
		int soilType = Soil.CLAY_LOAM; //           soilType
		// See wikipedia
		float ALBEDO = 0.25f; // albedo of the barc dry soil                                    sans dinicnsion
		int Q0 = 11; //         parameter of the end of the maximum cvaporntion stage             mm
		float ARGI = 30; //        percentage of clay in the surface layer                        %
		Soil soil = new Soil(
				soilType, //           soilType
				ALBEDO, // albedo of the barc dry soil                                    sans dinicnsion
				Q0, //         parameter of the end of the maximum cvaporntion stage             mm
				ARGI //        percentage of clay in the surface layer                        %							
		);	

		for(int j = 0; j < 4; j++){
			for(int i = 0; i < tests.length; i++){
//				if(j == 0) testFallow(tests[i], soil, 320, showGraphs);
//				if(j == 1) testCrop(tests[i], soil, 320, showGraphs, Crop.WHEAT);
//				if(j == 2) testCrop(tests[i], soil, 320, showGraphs, Crop.MAIZE);
				if(j == 3) testPasture(tests[i], soil, 320, showGraphs);
				for(int k = 0; k < 100; k++) System.out.println();
			}
		}
	}
	
	public static void testFallow(Test test, Soil soil, int days, boolean showGraphs){
		
		GraphPanel stressGraph = new GraphPanel("Water/Nitrate Stress Indices", "Day", "0 (max stress) --> 1 (no stress)", true, true);
		GraphPanel flowsGraph = new GraphPanel("Water/Nitrate Flows", "Day", "mm  and  kgN/ha", true, true);
		GraphPanel thermGraph = new GraphPanel("Thermal", "Day", "°C and MJ/m²/d", true, true);
		initSoilGraphs(stressGraph, flowsGraph, thermGraph);
		
		for(int i = 0; i < days; i++){
			test.day(i);
			soil.execDay(
					test.TRR, //         precipitation                                                       mm day-'
					test.AIRG, //        irrigation table                                               mm day-l
					test.ANIT,       // fertilization table                                            kg N lia-l day-l
					test.TRG, //         global solar radiation                                              MJ m-2 day-l
					test.TMOY, //       mean temperature                                                    "C
					test.TMIN //       minimum temperature                                                 OC
			);
			if(i == 100) soil.depositResidue(5000);
			if(i == 200) soil.plough();
			soil.printLayers(33);		
			soil.printState();
			System.out.println();
			if(showGraphs){
				updateSoilGraphs(stressGraph, flowsGraph, thermGraph, test, soil);
			}
		}
		JFrame[] frames = new JFrame[3];
		int ox = 150;
		int oy = 200;
		int gap = 50;
		if(showGraphs){
			frames[0] = flowsGraph.displayWindow(ox, oy);
			frames[1] = thermGraph.displayWindow(ox + gap + frames[0].getWidth(), oy);
		}
		Utils.awaitEnter();
		for(JFrame f : frames){ if(f != null) f.dispose(); }
	}
	
	
	public static void testCrop(Test test, Soil soil, int days, boolean showGraphs, int type){
		GraphPanel stressGraph = new GraphPanel("Water/Nitrate Stress Indices", "Day", "0 (max stress) --> 1 (no stress)", true, true);
		GraphPanel flowsGraph = new GraphPanel("Water/Nitrate Flows", "Day", "mm  and  kgN/ha", true, true);
		GraphPanel thermGraph = new GraphPanel("Thermal", "Day", "°C and MJ/m²/d", true, true);
		GraphPanel yieldGraph = new GraphPanel("Yield and Leaf Area", "Day", "t/ha   &   LAI", true, true);
		initSoilGraphs(stressGraph, flowsGraph, thermGraph);
		initYieldGraph(yieldGraph);
		yieldGraph.addSeries("Grain Yield", Color.YELLOW.darker().darker());
	
		// Set up params;
		int IPLT = 0;
		// Crop management parameters
		int DENSITE = 4;  // sowing density                                                 pl m-2
		int ZLABOUR = 0;  // depth of ploughing                                                  cm
		FodderCrop crop;
		if(type == Crop.WHEAT){
			IPLT = 60; // Day in year of planting, 0 = Jan 1st, 245 = winter wheat
			crop = new WheatCrop(
					soil,
					IPLT,  // Day in year of planting, 0 = Jan 1st
					// Crop management parameters
					DENSITE,  // sowing density                                                 pl m-2
					ZLABOUR  // depth of ploughing                                                  cm
			);
		}
		else{ // type == MAIZE
			IPLT = 60; // Day in year of planting, 0 = Jan 1st, 245 = winter wheat
			crop = new MaizeCrop(
					soil,
					IPLT,  // Day in year of planting, 0 = Jan 1st
					// Crop management parameters
					DENSITE,  // sowing density                                                 pl m-2
					ZLABOUR  // depth of ploughing                                                  cm
			);
		}
		// Sew crop
		soil.sewCrop(crop);
		crop.printStateInit();
		for(int i = IPLT; i < days + IPLT; i++){
			test.day(i);
			soil.execDay(
					test.TRR, //         precipitation                                                       mm day-'
					test.AIRG, //        irrigation table                                               mm day-l
					test.ANIT,       // fertilization table                                            kg N lia-l day-l
					test.TRG, //         global solar radiation                                              MJ m-2 day-l
					test.TMOY, //       mean temperature                                                    "C
					test.TMIN //       minimum temperature                                                 OC
			);
			crop.execDay(
					test.TMOY,      // mean temperature                                                    "C
					test.TMIN,      // minimum temperature                                                 OC
					test.TRR,       // precipitation                                                       mm day-'
					test.TRG       // global solar radiation                                              MJ m-2 day-l
			);
			crop.printState();
			crop.printLayers();
			soil.printLayers();
			soil.printState();
			System.out.println();
			if(showGraphs){
				updateSoilGraphs(stressGraph, flowsGraph, thermGraph, test, soil);
				updateYieldGraph(yieldGraph, crop);
				yieldGraph.addData("Grain Yield", crop.getMAGRAIN() / 100);
			}
		}
		JFrame[] frames = new JFrame[4];
		int ox = 150;
		int oy = 50;
		int gap = 50;
		if(showGraphs){
			frames[0] = yieldGraph.displayWindow(ox, oy);
			frames[1] = stressGraph.displayWindow(ox, oy + frames[0].getHeight() + gap);
			frames[2] = thermGraph.displayWindow(ox + frames[0].getWidth() + gap, oy);
			frames[3] = flowsGraph.displayWindow(ox + frames[0].getWidth() + gap, oy + frames[0].getHeight() + gap);
		}
		Utils.awaitEnter();
		for(JFrame f : frames){ if(f != null) f.dispose(); }
	}
			

	public static void testPasture(Test test, Soil soil, int days, boolean showGraphs){
		GraphPanel stressGraph = new GraphPanel("Water/Nitrate Stress Indices", "Day", "0 (max stress) --> 1 (no stress)", true, true);
		GraphPanel flowsGraph = new GraphPanel("Water/Nitrate Flows", "Day", "mm  and  kgN/ha", true, true);
		GraphPanel thermGraph = new GraphPanel("Thermal", "Day", "°C and MJ/m²/d", true, true);
		GraphPanel yieldGraph = new GraphPanel("Yield and Leaf Area", "Day", "t/ha   &   LAI", true, true);
		initSoilGraphs(stressGraph, flowsGraph, thermGraph);
		initYieldGraph(yieldGraph);
		yieldGraph.addSeries("Offered Herbage", Color.ORANGE);

		GraphPanel graphD = new GraphPanel("Digestiability", "day of Year", "g/100g", true, true);
		graphD.addSeries("D", Color.GREEN.darker());
		graphD.addSeries("Dl", Color.BLUE.darker());
		GraphPanel graphM = new GraphPanel("Milk Production/Herbage Consumption", "day of year", "UF", true, true);
		graphM.addSeries("Milk", Color.BLUE.darker());
		graphM.addSeries("Grass Intake", Color.GREEN.darker());
		
		Pasture pasture = new Pasture(
				soil, // Soil model 
				0, // Day of the year to start at
				Pasture.SPECIES_COCKSFOOT, // Grass species
				1 // Area of a plot        
		);
		// Sew the pasture crop
		soil.sewCrop(pasture);
		// Add 50 cows to pasture
		pasture.setCows(50);
		
		CowDigestion cows = new CowDigestion();
		cows.setPasture(pasture);
		
		for(int i = 0; i < days; i++){
			test.day(i);
			soil.execDay(
					test.TRR, //         precipitation                                                       mm day-'
					test.AIRG, //        irrigation table                                               mm day-l
					test.ANIT,       // fertilization table                                            kg N lia-l day-l
					test.TRG, //         global solar radiation                                              MJ m-2 day-l
					test.TMOY, //       mean temperature                                                    "C
					test.TMIN //       minimum temperature                                                 OC		
			);
			pasture.execDay(
//				i == 100 || i == 300 || i == 800 ?
//				i == 100 ? ACTION_CUT_GRASS :
//				i == 3 ? ACTION_START_GRAZE :
				// Weekly schedule
//				i % 7 == 0 ? ACTION_START_GRAZE :
//				(i % 7) - 4 == 0 ? ACTION_STOP_GRAZE : 
				// Monthly schedule
				i % 28 == 0 ? Pasture.ACTION_START_GRAZE :
				(i % 28) - 7 == 0 ? Pasture.ACTION_STOP_GRAZE : 
//				i == 1 ? ACTION_START_GRAZE :
//				i == 5 ? ACTION_STOP_GRAZE : 
//				i == 50 ? ACTION_START_GRAZE :
//				i == 75 ? ACTION_STOP_GRAZE : 
//				i == 100 ? ACTION_START_GRAZE :
//				i == 107 ? ACTION_STOP_GRAZE : 
				Pasture.ACTION_NONE, // Action to carry out
				test.TRG, // Daily incident radiation                     MJ⋅m–2
				test.TMOY, // Average daily temperature                      °C
				// TODO: This should come from cow model
				cows.getGrassIntake() // model.qih // Grazed herbage intake per day and per cow               kg
			);
			pasture.printState();
			pasture.printLayers();
			soil.printLayers();
			soil.printState();
			System.out.println();
//System.out.println("DM:" + sim.DM + " \tD:" +  sim.D + "\t AW:" + sim.AW + "\t AET:" + sim.AET);
			if(showGraphs){
				updateSoilGraphs(stressGraph, flowsGraph, thermGraph, test, soil);
				updateYieldGraph(yieldGraph, pasture);
				yieldGraph.addData("Offered Herbage", pasture.qoh * 0.01);
				graphD.addData("D", pasture.D);
				graphD.addData("Dl", pasture.Dl);
			}
			
		    double Ncalving = 20; // Number of days spend from the calving day                   day
		    double qic = 1;     // Amount of concentrates intake                               kg
		    double qia = 4;     // Amount of hay intake                                        kg
		    double qim = 2;     // Amount of maize offered (required if grazing, calculated if not)    kg
		    // Yield of 6000 litres per year 
		    // TODO: is this max daily yield?? If so 16 is a good value
		    double yp_max = 8;  // Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
		    boolean good_hay = true; // Whether the hay used is of good quality
			cows.execDay(
				    Ncalving , // Number of days spend from the calving day                   day
				    qic,     // Amount of concentrates intake                               kg
				    qia,     // Amount of hay intake                                        kg
				    qim,     // Amount of maize offered (required if grazing, calculated if not)    kg
				    yp_max,  // Highest potential milk yield per cow in the lactation period (genetic characteristic)      kg
				    good_hay // Whether the hay used is of good quality	
			);
			if(showGraphs){
				graphM.addData("Milk", cows.getMilkProduced());
				graphM.addData("Grass Intake", cows.getGrassIntake());
			}
		}
		JFrame[] frames = new JFrame[6];
		int ox = 0;
		int oy = 50;
		int gapx = -40;
		int gapy = 50;
		if(showGraphs){
			frames[0] = yieldGraph.displayWindow(ox, oy);
			frames[1] = stressGraph.displayWindow(ox, oy + frames[0].getHeight() + gapy);
			frames[2] = thermGraph.displayWindow(ox + frames[0].getWidth() + gapx, oy);
			frames[3] = flowsGraph.displayWindow(ox + frames[0].getWidth() + gapx, oy + frames[0].getHeight() + gapy);
			frames[3] = graphD.displayWindow(ox + (frames[0].getWidth() + gapx) * 2, oy);
			frames[4] = graphM.displayWindow(ox + (frames[0].getWidth() + gapx) * 2, oy + frames[0].getHeight() + gapy);
		}		
		Utils.awaitEnter();
		for(JFrame f : frames){ if(f != null) f.dispose(); }
	}

	
	public static void initSoilGraphs(GraphPanel stressGraph, GraphPanel flowsGraph, GraphPanel thermGraph){
		stressGraph.addSeries("Nitrate Stress", Color.YELLOW.darker());
		stressGraph.addSeries("Water Stress", Color.BLUE.darker());
		stressGraph.addSeries("Stomatal Stress", Color.GREEN);
		stressGraph.addSeries("Turgescence Stress", Color.ORANGE);		
		flowsGraph.addSeries("Nitrate Input", Color.MAGENTA.darker());
		flowsGraph.addSeries("Leaching", Color.RED);
		flowsGraph.addSeries("Water Input", Color.CYAN.darker());
		flowsGraph.addSeries("Evaporation", Color.ORANGE);
		flowsGraph.addSeries("Transpiration", Color.GREEN);
		flowsGraph.addSeries("Drainage", Color.BLUE);
		thermGraph.addSeries("Air Temperature", Color.RED);
		thermGraph.addSeries("Soil Temperature", Color.YELLOW.darker().darker());
		thermGraph.addSeries("Solar Radiation", Color.ORANGE);		
	}
	public static void updateSoilGraphs(GraphPanel stressGraph, GraphPanel flowsGraph, GraphPanel thermGraph, Test test, Soil soil){
		stressGraph.addData("Nitrate Stress", soil.getINNS());
		stressGraph.addData("Water Stress", (soil.getSWFAC() + soil.getTURFAC()) / 2);
		stressGraph.addData("Stomatal Stress", soil.getSWFAC());
		stressGraph.addData("Turgescence Stress", soil.getTURFAC());		
		flowsGraph.addData("Water Input", test.TRR + test.AIRG);
		flowsGraph.addData("Evaporation", soil.getES());
		flowsGraph.addData("Transpiration", soil.getEP());
		flowsGraph.addData("Drainage", soil.getDRAIN());
		flowsGraph.addData("Nitrate Input", test.ANIT * soil.EFFN + test.TRR * soil.CONCRR + test.AIRG * soil.CONCIRR);
		flowsGraph.addData("Leaching", soil.getQLES());
		thermGraph.addData("Air Temperature", test.TMOY);
		thermGraph.addData("Soil Temperature", soil.getTCULT());
		thermGraph.addData("Solar Radiation", test.TRG);		
	}
	public static void initYieldGraph(GraphPanel yieldGraph){
		yieldGraph.addSeries("LAI", Color.GREEN);
		yieldGraph.addSeries("Plant Mass", Color.GREEN.darker());
	}
	public static void updateYieldGraph(GraphPanel yieldGraph, Crop crop){
		yieldGraph.addData("LAI", crop.getLAI());
		yieldGraph.addData("Plant Mass", crop.getMASEC());		
	}
	

}
