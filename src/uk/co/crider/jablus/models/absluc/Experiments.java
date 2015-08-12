package uk.co.crider.jablus.models.absluc;

/*
EXPERIMENTS:

Parameter Space:

* Temporal Trends
    T0. No trend, uniform value
	T1. Gradual increase and gradual decrease
	T2. Shifting equilibriums
	T3. Reactive, uniform starting value
* Spatial Trends
    S0. No trend, uniform value
	S1. Evan's spatial pattern
	S2. Reactive, uniform starting value
* Reward Mechanism
    R0. Reward directly proportional to revenue ie: Payoff = Rev
    R1. Reward only given if revenue above a threshold T0, ie: Payoff = Rev - T0 if Rev > T0, and 0 otherwise
    R2. Penalty for overproduction - Reward for revenue between T0 and T1, 0 if Rev < T0, T1 - Rev if Rev > T1
	R3. Reward raised to power. Great reward for good performance, but poor reward for anything less
	R4. Reward proportional to sigmoid curve. Reward is high for values above the offset and low for values below
* Visibility of Information
	I0. Information fully visible - Agents have access to revenue, suitability, market price, and previous values as trends
	I1. Partial information - Agents have access to limited information. Eg. Just revenue and previous market price
	I2. Misleading information - Agents have access to limited information, shifted by a random value

Runs with human subjects:

1. T1, S0, R0, V1 (15 steps)
2. T1, S1, R2, V1 (15 steps)
3. T2, S1, R1, V1 (30 steps)
4. T3, S1, R1, V1 (30 steps)
5. T0, S2, R1, V1 (20 steps)

*/

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.models.basic.MathModels;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.awt.Color;
import java.util.Random;

import jwo.landserf.structure.Footprint;

/** Stores default values for various experiments */
public class Experiments {
	
	public static final int CONTRIBUTION = 100; 

	public static final String EVANS_RANDOM = "Evans Random";
	public static final String EVANS_BDI = "Evans BDI";
	public static final String EVANS_S0 = "EvansS0";
	public static final String EVANS_S1 = "EvansS1";
	public static final String HUMAN_BASE     = "HumanBase";
	public static final String HUMAN_PRACTICE = "HumanPractice";
	public static final String TEST_BASE = "TestBase";
	public static final String TEST1     = "Test1";
	public static final String TEST2     = "Test2";
	public static final String TEST3     = "Test3";
	public static final String TEST4     = "Test4";
	public static final String TEST5     = "Test5";
	public static final String TEST6     = "Test6";
	public static final String TEST7     = "Test7";
	public static final String TEST8     = "Test8";
	public static final String HUMAN_EXPT1    = "HumanExpt1";
	public static final String HUMAN_EXPT2    = "HumanExpt2";
	public static final String HUMAN_EXPT3    = "HumanExpt3";
	public static final String HUMAN_T0S1R0 = "T0S1R0";
	public static final String HUMAN_T0S1R4 = "T0S1R4";
	public static final String HUMAN_T0S2R0 = "T0S2R0";
	public static final String HUMAN_T1S0R0 = "T1S0R0";
	public static final String HUMAN_T1S0R4 = "T1S0R4";
	public static final String HUMAN_T1S1R0 = "T1S1R0";
	public static final String HUMAN_T1S1R4 = "T1S1R4";
	public static final String HUMAN_T2S0R0 = "T2S0R0";
	public static final String HUMAN_T2S1R0 = "T2S1R0";
	public static final String HUMAN_T3S0R0 = "T3S0R0";
	public static final String HUMAN_T3S2R0 = "T3S2R0";
	
	// Experiment sets
	public static final String[] FULL_LIST = new String[]{
		EVANS_RANDOM,
		EVANS_BDI,
		TEST1,
		TEST2,
		TEST3,
		TEST4,
		TEST5,
		TEST6,
		TEST7,
		TEST8,
		HUMAN_EXPT1,
		HUMAN_EXPT2,
		HUMAN_EXPT3,
	};	 
	public static final String[] HUMAN_SET1 = new String[]{
		HUMAN_EXPT1,
		HUMAN_EXPT2,
//		HUMAN_EXPT3,
	};
	public static final String[] HUMAN_SET2 = new String[]{
//		HUMAN_BASE,
//		HUMAN_T1S0R0,
		HUMAN_T1S0R0,
//		HUMAN_T1S1R0,
//		HUMAN_T1S1R4,
//		HUMAN_T2S0R0,
		HUMAN_T2S1R0,
//		HUMAN_T3S0R0,
//		HUMAN_T3S2R0,
//		HUMAN_T0S1R0,
		HUMAN_T0S2R0,
//		HUMAN_T0S1R4,
	};
	
	// Calculation of REVENUE_FACTOR
	private static double revenueFactor(Parameters e, double avgProfit){
		return	CONTRIBUTION * 100 / // Contribution in pence (£100)
				(e.ROWS * e.COLS *   // Number of cells
				e.TIME_CYCLE *       // Number of steps
				HUMAN_SET1.length *  // Number of games
				9 *                  // Number of participants
				avgProfit);          // Average profit (between 0 and 1)
			

	}
	
	public static Parameters create(){ return create(null); }
	public static Parameters create(String name){
		// Parameters to return
		Parameters e = new Parameters();
		e.EXPERIMENTS = FULL_LIST;

		// Sets default parameter set
		if(name == null || name.equals("")){
//			name = "null"; Parameters e = new Parameters(name); e.GUI_SIMULATION = true; return e;
//			name = EVANS_RANDOM;
//			name = EVANS_BDI;
//			name = HUMAN_T1S0R0;
//			name = HUMAN_T2S1R0;
			name = HUMAN_T0S2R0;
		}
			
		
		// EXPERIMENT DEFINITIONS =====================================================

		
		// Basic human experiment - no price trend, no change in suitability
		if(name.equals(HUMAN_BASE)){
			// Basic simulation properties
			e.START_STATE = Simulation.RUNNING;
			e.START_STATE = e.START_STATE | Simulation.RECORDING;
			e.GUI_SIMULATION = false;
			e.GUI_HUMAN = true;
			e.GUI_HUMAN_CLOSABLE = false;
			e.TIME = true;
			e.TIME_CYCLE = 10;
			e.SAVE_CYCLE = 1;
			e.ONE_RUN = true;
			// Land area properties
			e.ROWS=5; e.COLS=5;
			e.RASTER_FOOTPRINT = new Footprint(0, 0, 1, 1);
			e.VECTOR_FOOTPRINT = new Footprint(0, 0, e.ROWS, e.COLS);
			e.LAND_USES = 2;
			e.LAND_USE_NAMES = new String[]{
				"Green",
				"Brown"
			};
			e.LAND_USE_COLOUR = new int[]{
	    		new Color(112, 208,  56).getRGB(), // Jablus Green
		    	new Color(164, 103,  22).getRGB()  // Jablus Brown
		    };
			// Agent proerties
			e.AGENT_TYPES = new int[]{
					Constants.HUMAN_AGENT
			};
			e.AGENT_LOCATIONS = new LandCell[]{
					new LandCell( 2,  2)
			};
			e.AGENT_LAND_OCCUPANCY = new MatrixData(0, new float[][]{
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
			}, false);
			// Economic model properties
			e.MARKET = true;
			e.REVENUE_MODEL = MathModels.BASIC_ECONOMIC;
			e.MARKET_PRICE          = new double[]{1.0, 1.0};
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT  = new double[]{1.0, 1.0};
			e.REVENUE_FACTOR = 1;
			return e;
		}
		
		
		// Simple human experiment with spatial variability in suitablity, and no price trends
		if(name.equals(HUMAN_PRACTICE)){
			e = create(HUMAN_BASE);
			e.GUI_HUMAN_CLOSABLE = true;
			e.START_STATE = e.START_STATE & ~Simulation.RECORDING;
			e.LAND_USE_NAMES = new String[]{
					"Light_Yellow",
					"Dark_Yellow"};
			e.LAND_USE_COLOUR = new int[]{
					new Color(248, 244, 128).getRGB(),
					new Color(232, 208,  76).getRGB()};
			e.TIME_CYCLE = 5;
			e.REVENUE_MODEL = MathModels.RANDOM;
			return e;
		}
		// Best I managed is 10.30
		// Gradual price shifts with identical heterogenity
		if(name.equals(HUMAN_EXPT1)){
			// £2.22 > £3.08
			e = create(HUMAN_BASE);
			e.LAND_USE_NAMES = new String[]{
					"Light_Blue",
					"Dark_Blue"};
			e.LAND_USE_COLOUR = new int[]{
					new Color(102, 135, 255).getRGB(),
					new Color( 44,  42, 161).getRGB()};
			e.TIME_CYCLE = 40;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch4(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT = new double[]{0.75, 0.75};
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityMountain2(e);
			e.REVENUE_FACTOR = revenueFactor(e, 0.75) * 0.666;
			return e;
		}
		// Price trend equilibria with simple spatial heterogenity
		if(name.equals(HUMAN_EXPT2)){
			// £3.87 > £7.18
			e = create(HUMAN_BASE);
			e.LAND_USE_NAMES = new String[]{
					"Light_Green",
					"Dark_Green"};
			e.LAND_USE_COLOUR = new int[]{
					new Color(102, 244, 41).getRGB(),
					new Color(70,  148, 37).getRGB()};
			e.TIME_CYCLE = 30;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendShiftingEquilibrium1(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT = new double[]{0.8, 0.8};
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityEvans2(e);
			e.REVENUE_FACTOR = revenueFactor(e, 0.5) * 1.5;
			return e;
		}
		// Reactive spatial heterogenity with steady price
/*		if(name.equals(HUMAN_EXPT3)){
			Parameters e = create(HUMAN_BASE);
//			e.START_STATE = e.START_STATE & ~Simulation.RECORDING;
//			e.GUI_SIMULATION = true;
			e.LAND_USE_NAMES = new String[]{
					"Light_Red",
					"Dark_Red" };
			e.LAND_USE_COLOUR = new int[]{
					new Color(242, 78, 52).getRGB(),
					new Color(186, 30, 22).getRGB()};
			e.TIME_CYCLE = 30;
			e.MARKET = true;
			e.MARKET_PRICE    = new double[]{1.00, 0.50};		
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT = new double[]{ 0.3, 0.0 };
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityEvans1(e);
			e.PRODUCTIVITY_REACTIVE = true;
			// Quite hard - increase growth[0] to make easier. The closer sum(rates) is to 0 the harder the game
			// If sum(rates) is less than 0 then failure is inevitable.
			e.PRODUCTIVITY_MAX = new double[]{ 1.0, 0.2 };
			e.PRODUCTIVITY_GROWTH_RATE  = new double[]{ 0.8, 0.0 };
			e.PRODUCTIVITY_DECLINE_RATE = new double[]{ 0.5, 0.0 };		
			e.REVENUE_FACTOR = revenueFactor(e, 0.75) * 1.5;
			return e;
		}
*/
		
		if(name.equals(TEST_BASE)){
			e = create(HUMAN_BASE);
			e.START_STATE = e.START_STATE & ~Simulation.RECORDING;
			e.GUI_HUMAN_CLOSABLE = true;
			e.LAND_USE_NAMES = new String[]{
					"Light_Green",
					"Dark_Green"};
			e.LAND_USE_COLOUR = new int[]{
					new Color(102, 244, 41).getRGB(),
					new Color(70,  148, 37).getRGB()};
			return e;
		}
		if(name.equals(TEST1)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 40;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch1(e);
			return e;
		}
		if(name.equals(TEST2)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 40;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch1(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityEvans2(e);
			return e;
		}
		if(name.equals(TEST3)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 40;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch1(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityValley(e);
			return e;
		}
		if(name.equals(TEST4)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 15;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch3(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityDiagonal1(e);
			return e;
		}
		if(name.equals(TEST5)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 15;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch3(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityDiagonal3(e);
			return e;
		}
		// One use goes through large cycles, other use slowly increases
		if(name.equals(TEST6)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 37;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendCyclicLinear(e);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT = new double[]{0.8, 0.8};
			e.PRODUCTIVITY_SP_HETRO = false;
			setProductivityMountain1(e);
			return e;
		}	
		// One use goes through large cycles, other through smaller cycles
		if(name.equals(TEST7)){
			e = create(TEST_BASE);
//			e.START_STATE = e.START_STATE & ~Simulation.RECORDING;
//			e.GUI_SIMULATION = true;
			e.TIME_CYCLE = 36;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendDualCyclic1(e);
			return e;
		}
		// Simiulates the presence of a road (the valley) and differeing effects of transport
		if(name.equals(TEST8)){
			e = create(TEST_BASE);
			e.TIME_CYCLE = 20;
			e.MARKET_DEMAND_CYCLIC  = true; 
			setTrendLinearSwitch5(e);
			e.PRODUCTIVITY_SP_HETRO = true;
			setProductivityValley(e);
			return e;
		}

		// Original evans experiments
		if(name.equals(EVANS_S0)){
			// General properties
			e.GUI_SIMULATION = false;
			e.GUI_HUMAN = true;
			e.GUI_HUMAN_CLOSABLE = false;
			e.START_STATE = Simulation.RUNNING;// | Simulation.RECORDING;
			e.TIME = true;
			e.TIME_CYCLE = 40;
			e.SAVE_CYCLE = 1;
			// Land properties
			e.ROWS = 5; e.COLS = 5;
			e.RASTER_FOOTPRINT = new Footprint(0, 0, 1, 1);
			e.VECTOR_FOOTPRINT = new Footprint(0, 0, e.ROWS, e.COLS);
			e.LAND_USES = 2;
			e.LAND_USE_NAMES = new String[]{
					"Green",
					"Blue",
				};
				e.LAND_USE_COLOUR = new int[]{
			    	new Color(  0, 225,   0).getRGB(),  // Green
		    		new Color(  0,   0, 225).getRGB(), // Blue
			    };
			// Agent properties
			e.AGENT_TYPES = new int[]{
					Constants.HUMAN_AGENT
			};
			e.AGENT_LOCATIONS = new LandCell[]{
					new LandCell( 2,  2)
			};
			e.AGENT_LAND_OCCUPANCY = new MatrixData(0, new float[][]{
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
			}, false);
			e.AGENT_RANDOM_SEED = 1;
			// Economic properties
			e.REVENUE_MODEL = MathModels.EVANS;
			e.REVENUE_FACTOR = 0.5;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; 
			e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{0.6500, 0.3500},
				new double[]{0.6425, 0.3575},
				new double[]{0.6350, 0.3650},
				new double[]{0.6275, 0.3725},
				new double[]{0.6200, 0.3800},
				new double[]{0.6125, 0.3875},
				new double[]{0.6050, 0.3950},
				new double[]{0.5975, 0.4025},
				new double[]{0.5900, 0.4100},
				new double[]{0.5825, 0.4175},
				new double[]{0.5750, 0.4250},
				new double[]{0.5675, 0.4325},
				new double[]{0.5600, 0.4400},
				new double[]{0.5525, 0.4475},
				new double[]{0.5450, 0.4550},
				new double[]{0.5375, 0.4625},
				new double[]{0.5300, 0.4700},
				new double[]{0.5225, 0.4775},
				new double[]{0.5150, 0.4850},
				new double[]{0.5075, 0.4925},
				new double[]{0.5000, 0.5000},
				new double[]{0.4925, 0.5075},
				new double[]{0.4850, 0.5150},
				new double[]{0.4775, 0.5225},
				new double[]{0.4700, 0.5300},
				new double[]{0.4625, 0.5375},
				new double[]{0.4550, 0.5450},
				new double[]{0.4475, 0.5525},
				new double[]{0.4400, 0.5600},
				new double[]{0.4325, 0.5675},
				new double[]{0.4250, 0.5750},
				new double[]{0.4175, 0.5825},
				new double[]{0.4100, 0.5900},
				new double[]{0.4025, 0.5975},
				new double[]{0.3950, 0.6050},
				new double[]{0.3875, 0.6125},
				new double[]{0.3800, 0.6200},
				new double[]{0.3725, 0.6275},
				new double[]{0.3650, 0.6350},
				new double[]{0.3575, 0.6425},
				new double[]{0.3500, 0.6500}
			};
			// Land productivity properties
			return e;
		}

		if(name.equals(EVANS_S1)){
			e = create(EVANS_S0);
			e.PRODUCTIVITY = true;
			e.PRODUCTIVITY_DEFAULT  = new double[]{0.8,  0.8};
//			e.PRODUCTIVITY_MIN =  0f;
//			e.PRODUCTIVITY_MAX =  1f;
			e.PRODUCTIVITY_SP_HETRO = true;
			e.PRODUCTIVITY_MAPS = new Data2D[]{
					new MatrixData(1, new float[][]{
							new float[]{1.0f, 0.9f, 0.8f, 0.7f, 0.6f},
							new float[]{0.9f, 0.9f, 0.8f, 0.7f, 0.7f},
							new float[]{0.8f, 0.8f, 0.8f, 0.8f, 0.8f},
							new float[]{0.7f, 0.7f, 0.8f, 0.9f, 0.9f},
							new float[]{0.6f, 0.7f, 0.8f, 0.9f, 1.0f}
					}, false),
					null,
					new MatrixData(2, new float[][]{
							new float[]{0.6f, 0.7f, 0.8f, 0.9f, 1.0f},
							new float[]{0.7f, 0.7f, 0.8f, 0.9f, 0.9f},
							new float[]{0.8f, 0.8f, 0.8f, 0.8f, 0.8f},
							new float[]{0.9f, 0.9f, 0.8f, 0.7f, 0.7f},
							new float[]{1.0f, 0.9f, 0.8f, 0.7f, 0.6f}						
					}, false),
					null,
//					RasterReader.importGIF("suitability_blue_tile1.gif", "P: " + e.LAND_USE_NAMES[0], e.COLS, e.ROWS),
//					RasterReader.importGIF("suitability_blue_tile2.gif", "P: " + e.LAND_USE_NAMES[0], e.COLS, e.ROWS)
			};
			return e;		
		}
		
		
		
		// Simple human experiment with spatial variability in suitablity, and no price trends
		if(name.equals(HUMAN_T0S1R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 5;
			setProductivityEvans3(e);
			return e;
		}
		
		
		// Simple human experiment with spatial variability in suitablity, and no price trends
		if(name.equals(HUMAN_T0S1R4)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 5;
			setProductivityEvans3(e);
			setHumanR4(e);
			return e;
		}
		
		
		// Simple human experiment with reactive variability in suitablity, and no price trends
		if(name.equals(HUMAN_T0S2R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 10;
			e.LAND_USE_NAMES = new String[]{
					"Black",
					"White"
			};
			e.LAND_USE_COLOUR = new int[]{
			    	new Color(  0,   0,   0).getRGB(),     
					new Color(255, 255, 255).getRGB()
			};
/*			e.LAND_USE_NAMES = new String[]{
					"Green",
					"Yellow"
			};
			e.LAND_USE_COLOUR = new Color[]{
					new Color(27,  108, 24),
					new Color(213, 195, 65) 
			};
*/					
			setHumanS2(e);
			e.REVENUE_NOISE = 0.05;
			e.REVENUE_FACTOR = 1.0; //revenueFactor(e, 0.4);
			return e;
		}
		
		
		// Simple human experiment with linear price trends, and no variance in suitability 
		if(name.equals(HUMAN_T1S0R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			setTrendLinearSwitch1(e);
			e.REVENUE_NOISE = 0.01;
			e.REVENUE_FACTOR = revenueFactor(e, 0.8);
			return e;
		}
		
		
		// More comlex experiment with linear price trends and spatial variability in suitablity
		if(name.equals(HUMAN_T1S1R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			e.MARKET_DEMAND_CYCLIC = true;
			setTrendLinearSwitch1(e);
			setProductivityEvans3(e);
			return e;
		}
		
		
		
		// Simple human experiment with linear price trends, and no variance in suitability 
		if(name.equals(HUMAN_T1S0R4)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			e.MARKET_DEMAND_CYCLIC = true;
			setTrendLinearSwitch1(e);
			setHumanR4(e);
			return e;
		}
		
		
		// More comlex experiment with linear price trends and spatial variability in suitablity
		if(name.equals(HUMAN_T1S1R4)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			e.MARKET_DEMAND_CYCLIC = true;
			setTrendLinearSwitch1(e);
			setProductivityEvans3(e);
			setHumanR4(e);
			return e;
		}

		
		// Simple human experiment with low frequency shifting equilibrium price trends, and no variance in suitability 
		if(name.equals(HUMAN_T2S0R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			setTrendRandom(e);
			return e;
		}
		
		
		// Simple human experiment with low frequency shifting equilibrium price trends, and no variance in suitability 
		if(name.equals(HUMAN_T2S1R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			e.LAND_USE_NAMES = new String[]{
					"Red",
					"Orange"
			};
			e.LAND_USE_COLOUR = new int[]{
					new Color(192, 35,  14).getRGB(), // Red
			    	new Color(192, 122, 14).getRGB()  // Jablus Brown
			};
			setTrendRandom(e);
			setProductivityEvans3(e);
			e.REVENUE_NOISE = 0.01;
			e.REVENUE_FACTOR = revenueFactor(e, 0.5);
			return e;
		}
		
		
		// Simple human experiment with reactive price trends, and no variance in suitability 
		if(name.equals(HUMAN_T3S0R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			setTrendReactive(e);
			return e;
		}
		
		
		// More comled experiment with reactive price trends and suitability 
		if(name.equals(HUMAN_T3S2R0)){
			e = create(HUMAN_BASE);
			e.TIME_CYCLE = 20;
			e.LAND_USE_NAMES = new String[]{
					"Black",
					"Red"
			};
			e.LAND_USE_COLOUR = new int[]{
			    	new Color(0, 0, 0).getRGB(),     // Black
					new Color(192, 35,  14).getRGB() // Red
			};
			setTrendReactive(e);
			setHumanS2(e);
			e.REVENUE_NOISE = 0.1;
			e.REVENUE_FACTOR = revenueFactor(e, 0.5);
			return e;
		}		
		
		if(name.equals(EVANS_RANDOM)){
			e = create(EVANS_BDI);
			e.AGENT_TYPES = new int[]{
					Constants.RANDOM_AGENT
			};
			return e;
		}
		if(name.equals(EVANS_BDI)){
			// General properties
			e.GUI_SIMULATION = true;
//			e.START_STATE = Simulation.RUNNING;// | Simulation.RECORDING;
			e.TIME = true;
			e.TIME_CYCLE = 40;
			e.SAVE_CYCLE = 1;
			// Land properties
			e.ROWS = 5; e.COLS = 5;
			e.RASTER_FOOTPRINT = new Footprint(0, 0, 1, 1);
			e.VECTOR_FOOTPRINT = new Footprint(0, 0, e.ROWS, e.COLS);
			e.LAND_USES = 2;
			e.LAND_USE_NAMES = new String[]{
					"Green",
					"Brown"
//					"Blue",
//					"Green"
//					"Pink",
//					"Purple"
				};
				e.LAND_USE_COLOUR = new int[]{
		    		new Color(112, 208,  56).getRGB(), // Jablus Green
			    	new Color(164, 103,  22).getRGB()  // Jablus Brown
//		    		new Color(  0,   0, 225), // Blue
//			    	new Color(  0, 225,   0)  // Green
//		    		new Color(225, 130, 190), // Pink
//		    		new Color(170,   0, 170)  // Purple
			    };
			// Agent properties
			e.AGENT_TYPES = new int[]{
					Constants.BDI_AGENT
			};
			e.AGENT_LOCATIONS = new LandCell[]{
					new LandCell( 2,  2)
			};
			e.AGENT_LAND_OCCUPANCY = new MatrixData(1, new float[][]{
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
			}, false);
			e.AGENT_RANDOM_SEED = 1;
			// Economic properties
			e.REVENUE_MODEL = MathModels.EVANS;
			e.REVENUE_FACTOR = 1;
			e.MARKET = true;
			e.MARKET_DEMAND_CYCLIC  = true; // Changing market price for each product
			e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{0.6500, 0.3500},
				new double[]{0.6425, 0.3575},
				new double[]{0.6350, 0.3650},
				new double[]{0.6275, 0.3725},
				new double[]{0.6200, 0.3800},
				new double[]{0.6125, 0.3875},
				new double[]{0.6050, 0.3950},
				new double[]{0.5975, 0.4025},
				new double[]{0.5900, 0.4100},
				new double[]{0.5825, 0.4175},
				new double[]{0.5750, 0.4250},
				new double[]{0.5675, 0.4325},
				new double[]{0.5600, 0.4400},
				new double[]{0.5525, 0.4475},
				new double[]{0.5450, 0.4550},
				new double[]{0.5375, 0.4625},
				new double[]{0.5300, 0.4700},
				new double[]{0.5225, 0.4775},
				new double[]{0.5150, 0.4850},
				new double[]{0.5075, 0.4925},
				new double[]{0.5000, 0.5000},
				new double[]{0.4925, 0.5075},
				new double[]{0.4850, 0.5150},
				new double[]{0.4775, 0.5225},
				new double[]{0.4700, 0.5300},
				new double[]{0.4625, 0.5375},
				new double[]{0.4550, 0.5450},
				new double[]{0.4475, 0.5525},
				new double[]{0.4400, 0.5600},
				new double[]{0.4325, 0.5675},
				new double[]{0.4250, 0.5750},
				new double[]{0.4175, 0.5825},
				new double[]{0.4100, 0.5900},
				new double[]{0.4025, 0.5975},
				new double[]{0.3950, 0.6050},
				new double[]{0.3875, 0.6125},
				new double[]{0.3800, 0.6200},
				new double[]{0.3725, 0.6275},
				new double[]{0.3650, 0.6350},
				new double[]{0.3575, 0.6425},
				new double[]{0.3500, 0.6500}
			};
			// Land productivity properties
			e.PRODUCTIVITY = false;
//			e.PRODUCTIVITY_DEFAULT  = new double[]{1.0,  1.0};
//			e.PRODUCTIVITY_MIN =  0f;
//			e.PRODUCTIVITY_MAX =  1f;
			e.PRODUCTIVITY_MAPS = new Data2D[]{
					new MatrixData(1, new float[][]{
							new float[]{1.0f, 0.9f, 0.8f, 0.7f, 0.6f},
							new float[]{0.9f, 0.9f, 0.8f, 0.7f, 0.7f},
							new float[]{0.8f, 0.8f, 0.8f, 0.8f, 0.8f},
							new float[]{0.7f, 0.7f, 0.8f, 0.9f, 0.9f},
							new float[]{0.6f, 0.7f, 0.8f, 0.9f, 1.0f}
					}, false),
					new MatrixData(2, new float[][]{
							new float[]{0.6f, 0.7f, 0.8f, 0.9f, 1.0f},
							new float[]{0.7f, 0.7f, 0.8f, 0.9f, 0.9f},
							new float[]{0.8f, 0.8f, 0.8f, 0.8f, 0.8f},
							new float[]{0.9f, 0.9f, 0.8f, 0.7f, 0.7f},
							new float[]{1.0f, 0.9f, 0.8f, 0.7f, 0.6f}						
					}, false)
//					RasterReader.importGIF("suitability_blue_tile1.gif", "P: " + e.LAND_USE_NAMES[0], e.COLS, e.ROWS),
//					RasterReader.importGIF("suitability_blue_tile2.gif", "P: " + e.LAND_USE_NAMES[0], e.COLS, e.ROWS)
			};
			return e;
		}
		
		
		// END OF EXPERIMENT DEFINITIONS ===============================================

		return null;
	}


	// Set linear decrease with linear increase from 0 to 1
	private static void setTrendLinearSwitch1(Parameters e){
		e.MARKET_PRICE_CYCLE = new double[][]{
				new double[]{1.0000, 0.0000},
				new double[]{0.9500, 0.0500},
				new double[]{0.9000, 0.1000},
				new double[]{0.8500, 0.1500},
				new double[]{0.8000, 0.2000},
				new double[]{0.7500, 0.2500},
				new double[]{0.7000, 0.3000},
				new double[]{0.6500, 0.3500},
				new double[]{0.6000, 0.4000},
				new double[]{0.5500, 0.4500},
				new double[]{0.5000, 0.5000},
				new double[]{0.4500, 0.5500},
				new double[]{0.4000, 0.6000},
				new double[]{0.3500, 0.6500},
				new double[]{0.3000, 0.7000},
				new double[]{0.2500, 0.7500},
				new double[]{0.2000, 0.8000},
				new double[]{0.1500, 0.8500},
				new double[]{0.1000, 0.9000},
				new double[]{0.0500, 0.9500},
				new double[]{0.0000, 1.0000},
			};
	}
	// Set linear increase + linear decrease from 0.25 to 0.75
	private static void setTrendLinearSwitch2(Parameters e){
		e.MARKET_PRICE_CYCLE = new double[][]{
				new double[]{0.750, 0.250},
				new double[]{0.725, 0.275},
				new double[]{0.700, 0.300},
				new double[]{0.675, 0.325},
				new double[]{0.650, 0.350},
				new double[]{0.625, 0.375},
				new double[]{0.600, 0.400},
				new double[]{0.575, 0.425},
				new double[]{0.550, 0.450},
				new double[]{0.525, 0.475},
				new double[]{0.500, 0.500},
				new double[]{0.475, 0.525},
				new double[]{0.450, 0.550},
				new double[]{0.425, 0.575},
				new double[]{0.400, 0.600},
				new double[]{0.375, 0.625},
				new double[]{0.350, 0.650},
				new double[]{0.325, 0.675},
				new double[]{0.300, 0.700},
				new double[]{0.275, 0.725},
				new double[]{0.250, 0.750},
			};
	}
	// Set linear switch from 0.5 to 1.0
	private static void setTrendLinearSwitch3(Parameters e){
		e.MARKET_PRICE_CYCLE = new double[][]{
				new double[]{1.000, 0.500},
				new double[]{0.975, 0.525},
				new double[]{0.950, 0.550},
				new double[]{0.925, 0.575},
				new double[]{0.900, 0.600},
				new double[]{0.875, 0.625},
				new double[]{0.850, 0.650},
				new double[]{0.825, 0.675},
				new double[]{0.800, 0.700},
				new double[]{0.775, 0.725},
				new double[]{0.750, 0.750},
				new double[]{0.725, 0.775},
				new double[]{0.700, 0.800},
				new double[]{0.675, 0.825},
				new double[]{0.650, 0.850},
				new double[]{0.625, 0.875},
				new double[]{0.600, 0.900},
				new double[]{0.575, 0.925},
				new double[]{0.550, 0.950},
				new double[]{0.525, 0.975},
				new double[]{0.500, 1.000},
			};
	}
	// Set linear switch from 1.0 to 0.5
	private static void setTrendLinearSwitch4(Parameters e){
		e.MARKET_PRICE_CYCLE = new double[][]{
				new double[]{0.500, 1.000},
				new double[]{0.525, 0.975},
				new double[]{0.550, 0.950},
				new double[]{0.575, 0.925},
				new double[]{0.600, 0.900},
				new double[]{0.625, 0.875},
				new double[]{0.650, 0.850},
				new double[]{0.675, 0.825},
				new double[]{0.700, 0.800},
				new double[]{0.725, 0.775},
				new double[]{0.750, 0.750},
				new double[]{0.775, 0.725},
				new double[]{0.800, 0.700},
				new double[]{0.825, 0.675},
				new double[]{0.850, 0.650},
				new double[]{0.875, 0.625},
				new double[]{0.900, 0.600},
				new double[]{0.925, 0.575},
				new double[]{0.950, 0.550},
				new double[]{0.975, 0.525},
				new double[]{1.000, 0.500},
			};
	}
	// Stable price followed by linear increase + decrease followed by stability
	private static void setTrendLinearSwitch5(Parameters e){
		e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{1.00, 0.50},
				new double[]{1.00, 0.50},
				new double[]{1.00, 0.50},
				new double[]{1.00, 0.50},
				new double[]{1.00, 0.50},
				new double[]{1.00, 0.50},
				new double[]{0.95, 0.55},
				new double[]{0.90, 0.60},
				new double[]{0.85, 0.65},
				new double[]{0.80, 0.70},
				new double[]{0.75, 0.75},
				new double[]{0.70, 0.80},
				new double[]{0.65, 0.85},
				new double[]{0.60, 0.90},
				new double[]{0.55, 0.95},
				new double[]{0.50, 1.00},
				new double[]{0.50, 1.00},
				new double[]{0.50, 1.00},
				new double[]{0.50, 1.00},
				new double[]{0.50, 1.00},
		};			
	}
	// Cycling price, with linear increase
	private static void setTrendCyclicLinear(Parameters e){
		e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{1.00, 0.40},
				new double[]{0.95, 0.41},
				new double[]{0.50, 0.42},
				new double[]{0.05, 0.43},
				new double[]{0.00, 0.44},
				new double[]{0.01, 0.45},
				new double[]{0.04, 0.46},
				new double[]{0.20, 0.47},
				new double[]{0.50, 0.48},
				new double[]{0.80, 0.49},
				new double[]{0.96, 0.50},
				new double[]{0.99, 0.51},
				new double[]{1.00, 0.52},
				new double[]{0.95, 0.53},
				new double[]{0.50, 0.54},
				new double[]{0.05, 0.55},
				new double[]{0.00, 0.56},
				new double[]{0.01, 0.57},
				new double[]{0.04, 0.58},
				new double[]{0.20, 0.59},
				new double[]{0.50, 0.60},
				new double[]{0.80, 0.61},
				new double[]{0.96, 0.62},
				new double[]{0.99, 0.63},
				new double[]{1.00, 0.64},
				new double[]{0.95, 0.65},
				new double[]{0.50, 0.66},
				new double[]{0.05, 0.67},
				new double[]{0.00, 0.68},
				new double[]{0.01, 0.69},
				new double[]{0.04, 0.70},
				new double[]{0.20, 0.71},
				new double[]{0.50, 0.72},
				new double[]{0.80, 0.73},
				new double[]{0.96, 0.74},
				new double[]{0.99, 0.75},
				new double[]{1.00, 0.76},
		};			
	}
	// Large-amplitude cycle, with smaller-amplitude cycle
	private static void setTrendDualCyclic1(Parameters e){
		e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{1.00, 0.00},
				new double[]{0.95, 0.02},
				new double[]{0.50, 0.25},
				new double[]{0.05, 0.48},
				new double[]{0.00, 0.50},
				new double[]{0.01, 0.49},
				new double[]{0.04, 0.45},
				new double[]{0.20, 0.35},
				new double[]{0.50, 0.25},
				new double[]{0.80, 0.15},
				new double[]{0.96, 0.05},
				new double[]{0.99, 0.01},
		};			
	}
	// Two cycles
	private static void setTrendDualCyclic2(Parameters e){
		e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{1.00, 0.60},
				new double[]{0.99, 0.59},
				new double[]{0.95, 0.56},
				new double[]{0.90, 0.55},
				new double[]{0.80, 0.54},
				new double[]{0.70, 0.55},
				new double[]{0.60, 0.57},
				new double[]{0.53, 0.59},
				new double[]{0.47, 0.61},
				new double[]{0.43, 0.64},
				new double[]{0.42, 0.67},
				new double[]{0.41, 0.70},
				new double[]{0.42, 0.71},
				new double[]{0.43, 0.71},
				new double[]{0.47, 0.71},
				new double[]{0.54, 0.69},
				new double[]{0.79, 0.66},
				new double[]{0.94, 0.63},
				new double[]{0.98, 0.61},
				new double[]{1.00, 0.60},
			};			
	}
	// Price from high, to low to very high, with steady price on alternite use
	private static void setTrendShiftingEquilibrium1(Parameters e){
		e.MARKET_PRICE_CYCLE    = new double[][]{
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.36, 0.35},
				new double[]{0.34, 0.35},
				new double[]{0.22, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.20, 0.35},
				new double[]{0.50, 0.35},
				new double[]{0.90, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
				new double[]{1.00, 0.35},
		};			
	}

	// Set low frequency shifting equilibrium
	private static void setTrendRandom(Parameters e){
		e.MARKET_DEMAND_CYCLIC = true; // Changing market price for each product
		e.MARKET_PRICE_CYCLE = genRandomEquilibriumShift(e.TIME_CYCLE, e.LAND_USES, 0.67, 0.5, new Random(200));
	}
	
	// Set high frequency shifting equilibrium
	private static void setTrendReactive(Parameters e){
		e.MARKET_DEMAND_REACTIVE = true; // Market price depends on supply
	}
	
	// Generates a shifting equilibrium pattern with the given frequencies/magnitudes
	private static double[][] genRandomEquilibriumShift(int steps, int uses, double freq, double mag, Random rand){
		double[][] data = new double[steps][uses];
		freq = freq < 0 ? 0 : freq > 1 ? 1 : freq;
		mag = mag < 0 ? 0 : mag > 1 ? 1 : mag;
		double avgStepsSame = (1 - freq) * steps;
//System.out.println("avgStepsSame=" + avgStepsSame);
		for(int use = 0; use < uses; use++){
			int stepsSame = 0;
			double val = rand.nextDouble();
			for(int step = 0; step < steps; step++){
				if(stepsSame == 0){
					stepsSame = (int)(avgStepsSame * (0.5 + rand.nextDouble()) + 0.5);
					val = (val + ((0.5 - rand.nextDouble()) * mag)) % 1.0;
				}
				else
					stepsSame--;
				data[step][use] = val;
			}
		}
		return data;
	}
	
	// Evans tile fom 0.2 to 1
	private static void setProductivityEvans1(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				null, 
				new MatrixData(1, new float[][]{
						new float[]{1.0f, 0.8f, 0.6f, 0.4f, 0.2f},
						new float[]{0.8f, 0.8f, 0.6f, 0.4f, 0.4f},
						new float[]{0.6f, 0.6f, 0.6f, 0.6f, 0.6f},
						new float[]{0.4f, 0.4f, 0.6f, 0.8f, 0.8f},
						new float[]{0.2f, 0.4f, 0.6f, 0.8f, 1.0f}
				}, false)};
	}
	// Evans tile going from 0.5 to 1
	private static void setProductivityEvans2(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				null, 
				new MatrixData(1, new float[][]{
						new float[]{1.000f, 0.875f, 0.750f, 0.625f, 0.500f},
						new float[]{0.875f, 0.875f, 0.750f, 0.625f, 0.625f},
						new float[]{0.750f, 0.750f, 0.750f, 0.750f, 0.750f},
						new float[]{0.625f, 0.625f, 0.750f, 0.875f, 0.875f},
						new float[]{0.500f, 0.625f, 0.750f, 0.875f, 1.000f}
				}, false)};
	}
	// Evans tiles from 0 to 1, alternate tile inverted
	private static void setProductivityEvans3(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.75f, 0.50f, 0.25f, 0.00f},
						new float[]{0.75f, 0.75f, 0.50f, 0.25f, 0.25f},
						new float[]{0.50f, 0.50f, 0.50f, 0.50f, 0.50f},
						new float[]{0.25f, 0.25f, 0.50f, 0.75f, 0.75f},
						new float[]{0.00f, 0.25f, 0.50f, 0.75f, 1.00f}
				}, false),
				new MatrixData(2, new float[][]{
						new float[]{0.00f, 0.25f, 0.5f, 0.75f, 1.00f},
						new float[]{0.25f, 0.25f, 0.5f, 0.75f, 0.75f},
						new float[]{0.50f, 0.50f, 0.5f, 0.50f, 0.50f},
						new float[]{0.75f, 0.75f, 0.5f, 0.25f, 0.25f},
						new float[]{1.00f, 0.75f, 0.5f, 0.25f, 0.00f}						
				}, false)
		};
	}
	// Homogeneous surface + Mountain from 0.7 to 1
	private static void setProductivityMountain1(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				null,
				new MatrixData(1, new float[][]{
						new float[]{0.70f, 0.72f, 0.75f, 0.72f, 0.70f},
						new float[]{0.72f, 0.90f, 0.95f, 0.90f, 0.72f},
						new float[]{0.75f, 0.95f, 1.00f, 0.95f, 0.75f},
						new float[]{0.72f, 0.90f, 0.95f, 0.90f, 0.72f},
						new float[]{0.70f, 0.72f, 0.75f, 0.72f, 0.70f}
				}, false)
		};		
	}
	// Homogeneous surface + Mountain from 0.5 to 1
	private static void setProductivityMountain2(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{0.50f, 0.56f, 0.60f, 0.56f, 0.50f},
						new float[]{0.56f, 0.80f, 0.92f, 0.80f, 0.56f},
						new float[]{0.60f, 0.92f, 1.00f, 0.92f, 0.60f},
						new float[]{0.56f, 0.80f, 0.92f, 0.80f, 0.56f},
						new float[]{0.50f, 0.56f, 0.60f, 0.56f, 0.50f}
				}, false),
				null,
		};		
	}
	// Mountain + hollow
	private static void setProductivityMountain3(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{0.70f, 0.72f, 0.75f, 0.72f, 0.70f},
						new float[]{0.72f, 0.90f, 0.95f, 0.90f, 0.72f},
						new float[]{0.75f, 0.95f, 1.00f, 0.95f, 0.75f},
						new float[]{0.72f, 0.90f, 0.95f, 0.90f, 0.72f},
						new float[]{0.70f, 0.72f, 0.75f, 0.72f, 0.70f}
				}, false),
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.95f, 0.90f, 0.95f, 1.00f},
						new float[]{0.95f, 0.75f, 0.72f, 0.75f, 0.95f},
						new float[]{0.90f, 0.72f, 0.70f, 0.72f, 0.90f},
						new float[]{0.95f, 0.75f, 0.72f, 0.75f, 0.95f},
						new float[]{1.00f, 0.95f, 0.90f, 0.95f, 1.00f}
				}, false)
		};		
	}
	// Two vallies, one steaper than the other
	private static void setProductivityValley(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(0, new float[][]{
						new float[]{0.8f, 0.7f, 0.6f, 0.5f, 0.4f},
						new float[]{0.7f, 0.8f, 0.7f, 0.6f, 0.5f},
						new float[]{0.6f, 0.7f, 0.8f, 0.7f, 0.6f},
						new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.7f},
						new float[]{0.4f, 0.5f, 0.6f, 0.7f, 0.8f}
				}, false),
				new MatrixData(1, new float[][]{
						new float[]{1.0f, 0.8f, 0.6f, 0.4f, 0.2f},
						new float[]{0.8f, 1.0f, 0.8f, 0.6f, 0.4f},
						new float[]{0.6f, 0.8f, 1.0f, 0.8f, 0.6f},
						new float[]{0.4f, 0.6f, 0.8f, 1.0f, 0.8f},
						new float[]{0.2f, 0.4f, 0.6f, 0.8f, 1.0f}
				}, false),
		};
	}
	// Diagonal gradient + Flat surface
	private static void setProductivityDiagonal1(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.97f, 0.93f, 0.80f, 0.70f},
						new float[]{0.97f, 0.93f, 0.80f, 0.70f, 0.62f},
						new float[]{0.93f, 0.80f, 0.70f, 0.62f, 0.58f},
						new float[]{0.80f, 0.70f, 0.62f, 0.58f, 0.55f},
						new float[]{0.70f, 0.62f, 0.58f, 0.55f, 0.52f}
				}, false),
				null
		};
	}
	// Two equal diagonal gradients
	private static void setProductivityDiagonal2(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.97f, 0.93f, 0.80f, 0.70f},
						new float[]{0.97f, 0.93f, 0.80f, 0.70f, 0.62f},
						new float[]{0.93f, 0.80f, 0.70f, 0.62f, 0.58f},
						new float[]{0.80f, 0.70f, 0.62f, 0.58f, 0.55f},
						new float[]{0.70f, 0.62f, 0.58f, 0.55f, 0.52f}
				}, false),
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.97f, 0.93f, 0.80f, 0.70f},
						new float[]{0.97f, 0.93f, 0.80f, 0.70f, 0.62f},
						new float[]{0.93f, 0.80f, 0.70f, 0.62f, 0.58f},
						new float[]{0.80f, 0.70f, 0.62f, 0.58f, 0.55f},
						new float[]{0.70f, 0.62f, 0.58f, 0.55f, 0.52f}
				}, false)};
	}
	// Diagonal gradient + same gradient rotated 90 degrees
	private static void setProductivityDiagonal3(Parameters e){
		e.PRODUCTIVITY_MAPS = new Data2D[]{
				new MatrixData(1, new float[][]{
						new float[]{1.00f, 0.97f, 0.93f, 0.80f, 0.70f},
						new float[]{0.97f, 0.93f, 0.80f, 0.70f, 0.62f},
						new float[]{0.93f, 0.80f, 0.70f, 0.62f, 0.58f},
						new float[]{0.80f, 0.70f, 0.62f, 0.58f, 0.55f},
						new float[]{0.70f, 0.62f, 0.58f, 0.55f, 0.52f}
				}, false),
				new MatrixData(2, new float[][]{
						new float[]{0.70f, 0.80f, 0.93f, 0.97f, 1.00f},
						new float[]{0.62f, 0.70f, 0.80f, 0.93f, 0.97f},
						new float[]{0.58f, 0.62f, 0.70f, 0.80f, 0.93f},
						new float[]{0.55f, 0.58f, 0.62f, 0.70f, 0.80f},
						new float[]{0.52f, 0.55f, 0.58f, 0.62f, 0.70f}
				}, false),
		};
	}
	
	// Set reactive spatial heterogenity with evan's tiles
	private static void setHumanS2(Parameters e){
		e.PRODUCTIVITY_SP_HETRO = true;
		e.PRODUCTIVITY_REACTIVE = true;
		e.PRODUCTIVITY_DEFAULT = new double[]{
				1.0,
				0.2
		};
		// Quite hard - increase growth[0] to make easier. The closer sum(rates) is to 0 the harder the game
		// If sum(rates) is less than 0 then failure is inevitable.
		e.PRODUCTIVITY_GROWTH_RATE = new double[]{
				2.1,
				0.3
		};
		e.PRODUCTIVITY_DECLINE_RATE = new double[]{
				2.0,
				0.3
		};		
	}
	
	// Set thresholds rewards mechanism
	private static void setHumanR2(Parameters e){
		e.REWARD_MODEL = MathModels.REWARD_THRESHOLD;
		e.REWARD_THRESHOLD_MIN = 0.2;
		e.REWARD_THRESHOLD_MAX = 0.8;
	}

	// Set power reward mechanism
	private static void setHumanR3(Parameters e){
		e.REWARD_MODEL = MathModels.REWARD_POWER;
		e.REWARD_POWER_EXPONANT = 5;
	}

	// Set sigmoid curve reward mechanism
	private static void setHumanR4(Parameters e){
		e.REWARD_MODEL = MathModels.REWARD_SIGMOID;
		e.REWARD_SIGMOID_OFFSET = 0.45;
		e.REWARD_SIGMOID_GRADIENT = 15;
	}
	
	/** For testing purposes */
	public static void main(String[] args){
		Parameters e = create(HUMAN_BASE);
		
		// Good uns found
		int[] candidates = new int[]{
				25, 59, 76, 87, 96, 130, 147, 200, 213, 227, 299
		};
		for(int i = 0; i < candidates.length; i++){
			createGraph(20, e.LAND_USES, e.LAND_USE_NAMES, e.LAND_USE_COLOUR, 0.67, 0.5, candidates[i]);
		}
		
		// Search for a good un
//		for(int i = 200; i < 300; i++){
//			createGraph(20, e.LAND_USES, e.LAND_USE_NAMES, e.LAND_USE_COLOUR, 0.67, 0.5, i);
//		}
	}
	private static void createGraph(int steps, int uses, String[] useNames, int[] colours, double freq, double mag, int randomSeed){
		double[][] trendData = genRandomEquilibriumShift(20, uses, freq, mag, new Random(randomSeed));
		GraphPanel g = new GraphPanel("Price Trend " + randomSeed, "time", "price", true, true);
		for(int j = 0; j < uses; j++){
			g.addSeries(useNames[j], new Color(colours[j]));			
		}
		for(int i = 0; i < trendData.length; i++){
			for(int j = 0; j < uses; j++){
				g.addData(useNames[j], trendData[i][j]);
			}
		}
		g.displayWindow();		
	}
}
