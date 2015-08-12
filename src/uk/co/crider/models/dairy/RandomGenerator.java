package uk.co.crider.models.dairy;

/*
Animals to Buy:
* For rearing: Heifers in milk, heifers in calf, freshly calved heifers, rearing calves

Animals to trade:
(from finished_livestock_weekly_03-09.xls)
* Finished: (use weight calculation on cows to estimate numbers)
  + Heifers
  + Culled Cows
  (from rearing_livestock_weekly_02-09.xls)
* Rearing:
  + Rearing Bulls Calves (no more than 3 weeks old)
  + Rearing Heifer Calves -> beginning of Heifers01
  + Freshly Calved Heifers -> beginning of Cows1ST
  + Freshly Calved Cows    -> beginning of Cows2ND

Feeds to trade
* Feed Wheat (from feeds_monthly_88-09.xls)
* Hay        (from feeds_monthly_88-09.xls)
* Straw      (from feeds_monthly_88-09.xls)
* Maize (will have to come from farmer's handbook)
* Concentrates   (from concentrates_quarterly_06-08.xls)
* Silage (northern ireland only, from northern_ireland_silage.xls)

Note: wheat yields startedat arount 2 t/ha at 1900, and have since risen to almost 8 t/ha

*/

import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.util.Random;
import java.util.TreeSet;

import uk.co.crider.models.NormalDistribution;

public class RandomGenerator{
	
	private NormalDistribution rand;
	private int seq;
	private double[][] p;
	private int[] fn;
	public RandomGenerator(int dataSet, long seed){
		this(FUNCTIONS[dataSet], Utils.copy(PARAMS[dataSet]), seed);
	}
	private RandomGenerator(int[] fn, double[][] p, long seed){
		this.fn = fn;
		this.p = p;
		seq = 0;
		rand = new NormalDistribution(seed);
	}	
	public void setParam(int i, int j, double val){
//System.out.println("Setting param: " + i + ", " + j + " = " + val);
		p[i][j] = val;
	}
	public double next(){
//		System.out.println(Utils.arrayString(p[1]) + " | " + Utils.arrayString(p[3]));
//		return eval(fn[0], p[0], p[1], rand);
		p[1][0] = seq;
		p[3][0] = seq++;
		p[3][1] = eval(fn[0], p[0], p[1], rand);
		return eval(fn[1], p[2], p[3], rand);
	}
	public double getMean(){
		return p[3][1];
	}
	
	
	// Weather generation functions
	
	// Retuns rainfall in mm
/*	public static double genRain(int doy, Random rand){
		double mean = genSinVarMean(RAIN07[0], doy);
		return genRandRain(RAIN07[1], new double[]{doy, mean}, rand);
	}
	public static double genTemp(int doy, Random rand, double prev){
		double mean = genSinMean(TEMP07[0], doy);
		return genTemp(TEMP07[1], new double[]{doy, mean, prev}, rand);
	}
	public static double genMinTemp(int doy, Random rand, double prev){
		double mean = genSinMean(TMIN07[0], doy);
		return genTemp(TMIN07[1], new double[]{doy, mean, prev}, rand);
	}
	public static double genRad(int doy, Random rand, double prev){
		double mean = genRadMean(doy);
		return genRad(RAD07[1], new double[]{doy, mean, prev}, rand);
	}
	public static double genRadMean(int doy){
		return genSinMean(RAD07[0], doy);
	}
*/
	
	/** Run optimsations */
	public static void main(String[] args) {
		
//		System.out.println(Utils.arrayString(calibrate(milk_price_monthly_70_08)));
//		MultiScalePrice milkGen = new RandomGenerator().new MultiScalePrice(MILK_PARAMS, 12, 0, 0, 679);
//		test(milkGen, milk_price_monthly_70_08);

//		RandomGenerator gent = new RandomGenerator(new int[]{FN_RND_WALK, FN_VPR_NOISE}, PARAMS[SILAGE_MONTHLY], 0);
//		test2(gent, silage_monthly_99_08, 30, "test");
//		if(true) return;

		double[][] data = {
				fillGaps(milk_price_monthly_70_08),
				fillGaps(finished_heifer_weekly_03_09),
				fillGaps(cull_cow_weekly_05_09),
				fillGaps(bull_calves_weekly_02_09),
				fillGaps(heifer_calves_weekly_02_09),
				fillGaps(calfed_heifers_weekly_02_09),
				fillGaps(wheat_monthly_88_09),
				fillGaps(hay_monthly_88_09),
				fillGaps(straw_monthly_88_09),
				fillGaps(concentrates_monthly_97_07),
				fillGaps(silage_monthly_99_08),
				crop(rainfall_edinburgh_2007, 0, 50),
				temperature_edinburgh_2007,
				mintemp_edinburgh_2007,
				scale(solar_rad_edinburgh_2007, 86.4),
		};
		String[] names = {
			"milk_price_monthly_70_08",
			"finished_heifer_weekly_03_09",
			"cull_cow_weekly_05_09",
			"bull_calves_weekly_02_09",
			"heifer_calves_weekly_02_09",
			"calfed_heifers_weekly_02_09",
			"wheat_monthly_88_09",
			"hay_monthly_88_09",
			"straw_monthly_88_09",
			"concentrates_monthly_97_07",
			"silage_monthly_99_08",
			"rainfall_edinburgh_2007",
			"temperature_edinburgh_2007",
			"mintemp_edinburgh_2007",
			"solar_rad_edinburgh_2007",
		};
		int[] periods = {
			12,
			52,
			52,
			52,
			52,
			52,
			12,
			12,
			12,
			12,
			12,
			365,
			365,
			365,
			365,
		};
		int[] offsets = {
			30,
			50,
			100,
			50,
			100,
			500,
			70,
			70,
			30,
			100,
			10,
			30,
			30,
			30,
			20,
		};
//		for(int i = 0; i < data.length; i++){
		{	int i = 0;
//			double[][] p = calibrate(data[i], periods[i], 0, false);
//			System.out.println(names[i] + "=" + Utils.arrayString(p) + "\n");
//			RandomGenerator gen = new RandomGenerator(new int[]{FN_RND_WALK, FN_VPR_NOISE}, p, 0);
			RandomGenerator gen = new RandomGenerator(FUNCTIONS[i], PARAMS[i], 0);
			test(gen, data[i], offsets[i], names[i]);
		}
//		0.0 0.6545066993919362 0.9500000000000003 1.5749999999999993 28.195000000000007 1.109166666666667 
//		0.0 0.0 27.188070950481656 0.02774459786002097 
//		0.6532332264714575 1.2489654669971575 0.18094081781798924 3.7933333333333334 25.976666666666674 1.109166666666667 0.08253487179487173 12.0 0.12368327123659516 0.95 0.2 
//		467.0 27.188070950481656 25.970809935735502 0.0 0.5748285429310754 -0.001901001206695745 

//		0.0 0.6273129957011248 0.95 1.5749999999999993 28.195000000000007 1.109166666666667 
//		0.0 0.0 14.885000000000003 0.0 
//		0.40359892019123844 0.6350995838028446 0.36094708251298574 3.7933333333333334 25.976666666666674 1.109166666666667 0.08253487179487173 12.0 0.1519281752998793 0.95 0.2 
//		0.0 0.0 14.885000000000003 0.0 1.4547678337280003 0.0 


//		System.out.println(Utils.arrayString(calibrate(bull_calves_weekly_02_09, 52, 0)));
//		RandomGenerator gen = new RandomGenerator(BULL_CALF_WEEKLY, 0);
//		test2(gen, bull_calves_weekly_02_09, 50);

//		System.out.println(Utils.arrayString(calibrateTemp(temperature_edinburgh_2007)));
//		System.out.println(Utils.arrayString(calibrateTemp(mintemp_edinburgh_2007)));
//		System.out.println(Utils.arrayString(calibrateRad(solar_rad_edinburgh_2007)));
//		System.out.println(Utils.arrayString(calibrateRain(rainfall_edinburgh_2007)));
		
		if(true) return;
		
		// Convert silage figures from £/bail to £/tonne
		// Assuming average bail weight of 600Kg - see:
		// http://www.ruralni.gov.uk/index/publications/information_booklets/big_bale_silage/principles_of_making_big_bale_silage.htm
		double bailWeight = 0.6;
		double[] silage_monthly_99_08 = new double[RandomGenerator.silage_monthly_99_08.length];
		for(int i = 0; i < silage_monthly_99_08.length; i++)
			silage_monthly_99_08[i] = RandomGenerator.silage_monthly_99_08[i] / 0.6; 
		
		
/*		preview("Finished Cows", "week", "£/kg lw",
				new String[]{"finished heifers", "culled cows"},
				new double[][]{finished_heifer_weekly_03_09,
					cull_cow_weekly_05_09});
		preview("Stock Cows", "week", "£/head",
				new String[]{"bull calves", "heifer calves", "calved heifers"},
				new double[][]{bull_calves_weekly_02_09,
				heifer_calves_weekly_02_09,
				calfed_heifers_weekly_02_09});
*/
//		preview("", "", "", new String[]{}, new double[][]{cattle_weekly_85_09});
		preview("Feed Prices", "block", "£/tonne",
				new String[]{"wheat", "hay", "straw", "concentrates", "silage"},
				new double[][]{wheat_monthly_88_09,
				hay_monthly_88_09,
				straw_monthly_88_09,
				concentrates_monthly_97_07,
				silage_monthly_99_08});
		
		if(true) return;
		
		
		{ // Random walk with varying predictablility and optional upper and lower bounds
			int lines = 5;
			int base_seed = 0;
			Random rand = new Random(base_seed);
			;
/*			double dft = 0.001; // Drift magnitude
			double vol = 0.05; // Volitility
			double pre = 0.99; // Predictability
			double ibn = 0.02; // Influence of bounds
			double min = 0; // min value
			double max = 1; // max value
			double v_init = 0.5;
			double c_init = 0.0;
*/			
			double dft = 0.06; // Drift magnitude
			double vol = 0.9; // Volitility
			double pre = 0.95; // Predictability
			double ibn = 1; // Influence of bounds
			double min = 0; // min value
			double max = Double.MAX_VALUE; // max value
			double v_init = 3;
			double c_init = 0.0;
			
			// Graph of random walk
			GraphPanel graph = new GraphPanel("Random Walk", "time", "value");
			graph.addSeries("Real", Color.RED.darker());
			NormalDistribution[] norm = new NormalDistribution[lines];
			double[][] v = new double[lines][4];
			double[][] pr = new double[lines][];
			for(int i = 0; i < lines; i++){
				norm[i] = new NormalDistribution(rand.nextInt());
//				p[i] = new double[]{norm[i].nextDouble() * dft, vol, pre, ibn, min, max};
				pr[i] = new double[]{dft, vol, pre, ibn, min, max};
	//			p[i] = new double[]{0, vol};
				graph.addSeries("" + i, new Color(
						0.4f + (float)Math.random() * 0.5f,
						0.4f + (float)Math.random() * 0.5f,
						0.4f + (float)Math.random() * 0.5f));
				v[i] = new double[]{0, 0, v_init, c_init};
			}
			System.out.println(Utils.arrayString(pr));
			for(int j = 0; j < 360; j++){
				for(int i = 0; i < lines; i++){
					v[i][0] = genRandWalk(pr[i], v[i], norm[i]);
					v[i][0] = v[i][0] < 0 ? 0 : v[i][0];
					graph.addData("" + i, v[i][0]);
				}
				graph.addData("Real", milk_price_monthly_70_08[j]);
			}
			graph.displayWindow();
		}
		
		// Search for mean value, does it by minimising the
		// standard deviation between the data and its mean curve
				
		// Search for random curve with similar random properties
		// to the data. Namely (1) same standard deviation and
		// (2) same standard deviation of day on day change.
		// Does (1) by taking the square of the real data's standard deviation
		// subtracted from the random data's standard deviation. 
		// (2) is done by taking the square of the real data's standard deviation
		// of change from the random data's standard deviation of change
		// (1) and (2) are simply added together to provide the fitness metric
				
	}
	
	
	
	public static void preview(String title, String x, String y, String[] series, int[][] data){
		double[][] d = new double[data.length][];
		for(int i = 0; i < data.length; i++){
			d[i] = new double[data[i].length];
			for(int j = 0; j < data[i].length; i++)
				d[i][j] = data[i][j];
		}
		preview(title, x, y, series, d);
	}
	public static void preview(String title, String x, String y, double[] data){
		preview(title, x, y, new String[]{""}, new double[][]{data});
	}
	public static void preview(String title, String x, String y, String[] series, double[][] data){
		GraphPanel graph = new GraphPanel(title, x, y);
		int seed = 25;
		Random r = new Random(seed);
		int len = 0;
		for(int i = 0; i < series.length; i++){
			graph.addSeries(series[i], new Color(
					0.0f + r.nextFloat() * 0.9f,
					0.0f + r.nextFloat() * 0.9f,
					0.0f + r.nextFloat() * 0.9f));
			if(data[i].length > len) len = data[i].length;
		}
		for(int j = 0; j < len; j++){
			for(int i = 0; i < series.length; i++)
				if(j < data[i].length)
					graph.addData(series[i], data[i][j]);
		}
		graph.displayWindow();
	}
	
	public static void test(RandomGenerator gen, double[] data, double vOffset, String name){
		// Graph the two to compare
		GraphPanel graph = new GraphPanel(name, "time", "price");
		graph.addSeries("Simulated", Color.RED.darker());
		graph.addSeries("Real", Color.GREEN.darker());
		for(int i = 0; i < data.length; i++){
			graph.addData("Simulated", gen.next() + vOffset);
			graph.addData("Real", data[i]);
		}
		graph.displayWindow();
	}
	
	/** Fill in gaps (0 values) using previous values */
	public static double[] fillGaps(double[] data){
		// Fill in gaps with prev value
		double prev = 0;
		for(int i = 0; i < data.length; i++){
			if(data[i] == 0) data[i] = prev;
			prev = data[i];
		}
		return data;
	}
	
	/** Compresses curve by calculating mean for each span of period length */
	public static double[] compress(double[] in, int period){
		double total = 0;
		double[] out = new double[in.length / period];
		for(int i = 0, block = 0; i < in.length; i++){
			total += in[i];
			if(i % period == period - 1){
				out[block++] = total / period;
				total = 0;
			}
		}
		return out;
	}
	
	/** Expands a curve by given factor by linear interpolation between data points */
	public static double[] expand(double[] in, int factor){
		// Calculate mean value for each week using linear interpolation between datapoints
		double[] out = new double[in.length * factor];
		for(int i = 0, block = 0; i < out.length; i++){
			double prop = ((i - 1 + factor / 2) % factor / (double)factor);
			out[i] = in[Math.max(block - 1, 0)] * (1 - prop) + in[Math.min(block, in.length - 1)] * prop;
			if(i % factor == factor / 2)
				block++;
		}
		return out;
	}
	
	/** Scales data by a factor */
	private static double[] scale(double[] in, double factor){
		for(int i = 0; i < in.length; i++) in[i] = in[i] * factor;
		return in;		
	}
	
	/** Crops data to remove outlying figures */
	private static double[] crop(double[] in, double min, double max){
		for(int i = 0; i < in.length; i++) in[i] = in[i] < min ? min : in[i] > max ? max : in[i];
		return in;		
	}
	
	/** Finds the minimum and maximum values in the data */
	public static double[] findMinMax(double[] in){
		double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
		for(int i = 0; i < in.length; i++){
			if(in[i] < min) min = in[i];
			if(in[i] > max) max = in[i];
		}
		return new double[]{min, max};
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** Calibrates the overall milk price function */
	public static double[][] calibrate(double[] data, int period, int yearoffset, boolean showProgress){
		
		fillGaps(data);
		
		double[][] p_wave = calibrateWave(data, period, showProgress);
//		double[][] p_wave = {
//				{0, 0, 0, 0, 0, 0, -0.08071, 52, 0.5682424753749074, 0.95, 0.2},
//				{0, 0, 0, 0, 8.25540720105, 0}
//		};
		if(showProgress)
			System.out.println("p_wave=" + Utils.arrayString(p_wave));
//		if(true) return null;
		
		// Basic parameters
		int[] fn = null;
		long seed = 0;
		Random rand = new NormalDistribution(seed);
		int metric = 0;
		double[] delta;
		int resolution = 10;
		int depth = 3;
		int attempts = 100000;
		int keep = 3;
		double[][] p = new double[2][];
		double[][] in = new double[2][];
		double e;
		
		/* For each year, find
		  1) mean value
		  2) calibrated sin wave deviating from mean
		  then, for all years find pattern of:
		  1) changing mean
		  2) random variance of changing mean
		  3) changing wave amplitude
		  4) random variance of changing wave amplitude
		  finally output:
		  randomly changing mean + randomly changing wave amplitude
		*/

		int years = data.length / period;
		// Find data min and max
		double[] minMax = findMinMax(data);
		double min = minMax[0], max = minMax[1], var = (max - min) * 0.5;
		// Calculate mean value for each year
		double[] mean = compress(data, period);
		// Find mean min and max
		double[] meanMinMax = findMinMax(mean);
		double meanMin = meanMinMax[0], meanMax = meanMinMax[1], meanVar = (meanMax - meanMin) * 0.5, meanMid = meanMin + meanVar;
//		preview("mean data", "", "", mean);
		if(showProgress)
			System.out.println("meanMin=" + meanMin + ", meanMax=" + meanMax);
		
		double[] meanHR = expand(mean, period);
		
//		if(false){
//		preview("mean data", "", "", new String[]{"real", "mean"}, new double[][]{data, meanHR});
//		if(true) return null;

		
		// CALIBRATION OF INTER-ANNUAL MEAN 
		if(showProgress)
			System.out.println("Finding mean value accross whole dataset");
		// Search params
		metric = STD;
		resolution = 1000;
		depth = 4;
		fn = new int[]{FN_RND_WALK};
		// drift parameter          (-? to ?)
		// disturbance parameter    ( 0 to ?)
		// predictability parameter ( 0 to 1)
		// Minimum value
		// Maximum value
		// Influence of bounds      ( 0 to ?)
		// Init value
		delta = new double[]{0, 0, 0, 0, 0, 0, meanMid};
		p[0] = new double[]{0, 0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 0, meanMid};
//		p[0] = new double[]{0, 0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 37.977072799971154 }; // e=10.152194389434857
		in[0] = new double[]{0, 0, 0, 0};
//		p[0] = new double[]{0.0, 0.0, 1.0, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 28.284148 }; // e=6.114736905989835
//		in[0] = new double[]{0, 0, 28.284148, 0};
//		p[0] = new double[]{0, 0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 37.977072799971154 }; // e=10.152194389434857
//		in[0] = new double[]{0, 0, 37.977072799971154, 0};
		iSearch(metric, null, delta, resolution, depth, fn, p, in, rand, seed, meanHR, null);
		if(showProgress){
			e = error(metric, null, fn, p, in, rand, seed, meanHR, null, true);
			System.out.println("p[0] = new double[]{" + Utils.arrayString(p[0]) + "}; // e=" + e);
		}
//		if(true) return p;
		
		

		// VARIANCE OF INTER-ANNUAL MEAN
		if(showProgress)
			System.out.println("Calculating random variation in mean value");
		// Search params
//		metric = DIF_STD;
//		metric = DIF_DIF;
//		metric = DIF_INT;
		metric = DIF_STD + DIF_DIF;
		seed = 17;
		rand = new NormalDistribution(seed);
		resolution = 1000;
		attempts = 10000;
		fn = new int[]{FN_RND_WALK, FN_RND_WALK};
		// drift parameter          (-? to ?)
		// disturbance parameter    ( 0 to ?)
		// predictability parameter ( 0 to 1)
		// Minimum value
		// Maximum value
		// Influence of bounds      ( 0 to ?)
		delta = new double[]{0, meanVar,  0.0,                 0,                0, 0,  0};
		p[1] = new double[]{ 0, meanVar, 0.95, -Double.MAX_VALUE, Double.MAX_VALUE, 0, p[0][6] };
//		p[1] = new double[]{0, 1.3294081864305105, 0.95, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 37.977072799971154}; // e=0.6452547070976924
//		p[1] = new double[]{0.0, 1.4180949974541404, 0.95, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 37.977072799971154 }; // e=0.02388199384633874
//		p[1] = new double[]{0.0, 0.9101382277251817, 0.95, -Double.MAX_VALUE, Double.MAX_VALUE, 0, 28.284148  }; // e_avg=0.12414899479590259
//		p[1] = new double[]{0.0, 0.9101382277251817, 0.95, 5, 50, 2, 28.284148  };
		in[1] = new double[]{0, 0, p[0][6], 0};
//		iSearch(metric, seed, fn, delta, resolution, depth, p, meanWeekly, null);
//		rSearch(metric, null, attempts, keep, delta, resolution, depth, fn, p, in, rand, seed, meanHR, null);
		resolution = 100; attempts = 1000;
		avgSearch(true, 100, metric, null, attempts, keep, resolution, depth, fn, delta, p, in, rand, seed, meanHR, null, showProgress);
		if(showProgress){
			e = error(metric, null, fn, p, in, rand, seed, meanHR, null, true);
			System.out.println("p[1] = new double[]{" + Utils.arrayString(p[1]) + "}; // e=" + e);
		}
//		if(true) return p;
		// Copy data to final param array
		double[] p_mean = new double[]{0, p[1][1], p[1][2], meanMin - meanVar * 0.2, meanMax + meanVar * 0.2, meanVar * 0.1};
		if(p_mean[3] < 0) p_mean[3] = 0;
		double[] in_mean = new double[]{0, 0, p[0][6], 0};
		
	
		// Calibration of periodic noise on top of mean
		if(showProgress)
			System.out.println("Finding parameters for periodic noise on top of mean");
		// Search params
//		metric = DIF_STD;
//		metric = DIF_DIF;
//		metric = DIF_INT;
//		metric = DIF_STD + DIF_DIF;
		metric = DIF_STD + DIF_DIF + DIF_SEA;
//		metric = DIF_PRD;
//		double[] mWeights = 
//		     {     0,        1,       1,         1,        0,       1,        1,        1,        1,         0};
//	         {     0,        1,       1,      0,        0,       0,        1,        0,        0,        0,         0};
//		metric = STD + DIF_STD + DIF_DIF + DIF_INT + DIF_ZTM + DIF_PRD + DIF_SEA + SEA_STD + SEA_DIF + SEA_INT + SEA_ZTM;
		seed = 0;
		resolution = 1000;
		attempts = 1000;
		// Functions to use - use actual mean data for wave calibration
//		fn = new int[]{FN_RND_WALK, FN_VPR_NOISE};
		fn = new int[]{0, FN_VPR_NOISE};
		// Parameters for mean function
//		p[0] = new double[]{-0.02729, 1.127125098779859, 0.9390459266672877, 2.1984974756931277, 0.0, 1.7976931348623157E308, 33.3992723 }; // e=0.02013565484671037
//		in[0] = new double[]{0, 0, 33.3992723, 0};
		// PARAMETERS
		// Force stability     ( 0 to +?)
		// Force periodic      ( 0 to +?)
		// Force random        ( 0 to +?)
		// Min value           ( ? to  ?)
		// Max value           ( ? to  ?)
		// Influence of bounds ( 0 to +?)
		// Wave offset      (-1 to  +1)
		// Wave period      ( 1 to 365)
		// Wave amplitude variability         ( 0 to ?)
		// Wave amplitude predictability      ( 0 to 1)
		// Wave amplitude Influence of bounds ( 0 to 1)
		// Calibration of forces
		delta = new double[]{ 10, 50, 50,                 0,                0, var,                       0,  0,    0,         0,      0  };
		p[1]  = new double[]{ 10, 50, 50, -Double.MAX_VALUE, Double.MAX_VALUE, 0,    p_wave[0][6], p_wave[0][7],    p_wave[0][8], p_wave[0][9],   p_wave[0][10]};
//		p[1]  = new double[]{ 10, 50, 50,             v_min, Double.MAX_VALUE, var,    p_wave[0][6], p_wave[0][7],    p_wave[0][8], p_wave[0][9],   p_wave[0][10]};
//		p[1]  = new double[]{ 0.5, 6, 4, -Double.MAX_VALUE, Double.MAX_VALUE, 0,    -0.16265857, 52,    0.9530596, 0.95,   0.2};
//		p[1] = new double[]{0.6412822501223597, 6.4821049301845, 4.3179681605136375, -0.16265857, 52, 0.9530596, 0.95, 0.2 }; // e=9.912838576209282E-6
//		p[1] = new double[]{0.7488209587180056, 7.707267649363201, 4.141992255197771, -Double.MAX_VALUE, Double.MAX_VALUE, 0, -0.16265857, 52, 0.9530596, 0.95, 0.2 }; // e=2.0515008332555595E-5
//		p[1] = new double[]{0.7488209587180056, 7.707267649363201, 4.141992255197771, 0, Double.MAX_VALUE, 10, -0.16265857, 52, 0.9530596, 0.95, 0.2 }; // e=2.0515008332555595E-5
//		p[1] = new double[]{0.6851125877803921, 2.0792584487324928, 28.58786803405848, -Double.MAX_VALUE, Double.MAX_VALUE, 70.52819271470995, -0.08071, 52, 0.5682424753749074, 0.95, 0.2}; // e=2.9529215694402176
//		p[1] = new double[]{0.7541312703223237, 2.0792584487324928, 28.58786803405848 0.0 1.7976931348623157E308 11.56823887299702 -0.08071 52.0 0.5682424753749074 0.95 0.2 }; // e=4.656034183651759
		// INPUTS
		// Day of year
		// Mean
		// Previous mean
		// Previous change in mean
		// Previous wave amplitude
		// Previous change in wave amplitude
		in[1] = new double[]{0, 0, p[0][6], 0, p_wave[1][4], 0};
//		iSearch(metric, delta, resolution, depth, fn, p, in, rand, seed, data);
//		rSearch(metric, null, attempts, keep, delta, resolution, depth, fn, p, in, rand, seed, data, meanHR);
		resolution = 100; attempts = 1000;
		avgSearch(true, 100, metric, null, attempts, keep, resolution, depth, fn, delta, p, in, rand, seed, data, meanHR, showProgress);
//		p[1] = new double[]{2.1182234887456897, 2.7849739759474588, 21.439755311073, 0.0, 1.7976931348623157E308, 78.51817165349341, -0.08071, 52, 0.5682424753749074, 0.95, 0.2 }; // e=9.699931992525295
		if(showProgress){
			e = error(metric, null, fn, p, in, rand, seed, data, meanHR, true);
			System.out.println("p[1] = new double[]{" + Utils.arrayString(p[1]) + "}; // e=" + e);	
		}

		// Calibration of Zero-time
		double v_min = min - var * 0.2;
		if(v_min >= 0){
			p[1][3] = v_min;
			p[1][5] = var * 0.1;
		}
		else{
			p[1][3] = 0;
			p[1][5] = var * 0.33;			
		}
/*		else{
//			metric = DIF_STD + DIF_ZTM;
			metric = DIF_STD + DIF_DIF + DIF_ZTM;
			double[] mWeights = new double[11];
			mWeights[Utils.log2(DIF_STD)] = 0;
			mWeights[Utils.log2(DIF_DIF)] = 0;
			mWeights[Utils.log2(DIF_ZTM)] = 1;
			delta = new double[]{      0,       0,       0,  0,                0, var * 10,               0,            0,               0,            0,               0};
			p[1]  = new double[]{p[1][0], p[1][1], p[1][2],  0, Double.MAX_VALUE, var * 10,    p_wave[0][6], p_wave[0][7],    p_wave[0][8], p_wave[0][9],   p_wave[0][10]};
			in[1] = new double[]{0, 0, p[0][6], 0, p_wave[1][4], 0};
//			iSearch(metric, mWeights, delta, resolution, depth, fn, p, in, rand, seed, data, meanHR);
//			resolution = 1000; attempts = 10000;
//			rSearch(metric, mWeights, attempts, keep, delta, resolution, depth, fn, p, in, rand, seed, data, meanHR);
			resolution = 100; attempts = 1000;
			avgSearch(false, 100, metric, null, attempts, keep, resolution, depth, fn, delta, p, in, rand, seed, data, meanHR, showProgress);
			if(showProgress){
				e = error(metric, mWeights, fn, p, in, rand, seed, data, meanHR, true);
				System.out.println("p[1] = new double[]{" + Utils.arrayString(p[1]) + "}; // e=" + e);	
			}
		}
*/		
		
		double[] p_var = {p[1][0], p[1][1], p[1][2], p[1][3], max + var * 0.2, p[1][5], p_wave[0][6], p_wave[0][7], p_wave[0][8], p_wave[0][9], p_wave[0][10]};
		double[] in_var = {0, 0, p[0][6], 0, p_wave[1][4], 0};
		if(showProgress){
			e = error(metric, null, fn, new double[][]{p[0], p_var}, new double[][]{in[0], in_var}, rand, seed, data, meanHR, true);
			System.out.println("p[1] = new double[]{" + Utils.arrayString(p[1]) + "}; // e=" + e);	
		}
		
		// Return all parameters required to recreate random function
		return new double[][]{p_mean, in_mean, p_var, in_var };
	}

	
	public static double[][] calibrateWave(double[] data, int period, boolean showProgress){
		
		fillGaps(data);
		// Find min and max
		double[] minMax = findMinMax(data);
		double min = minMax[0], max = minMax[1], var = (max - min) * 0.5, mid = min + var;
		
		// Basic parameters
		int[] fn = null;
		long seed = 0;
		Random rand = new NormalDistribution(seed);
		int metric = 0;
		double[] delta;
		int resolution = 10;
		int depth = 3;
		int attempts = 100000;
		int keep = 3;
		double[][] p = new double[3][];
		double[][] in = new double[3][];
		double e;
		int years = data.length / period;
		
		// Calculate mean
		double[] mean = compress(data, period);
		
		// Partition data by year
		double[][] dataByYear = new double [years][period];
		for(int yr = 0; yr < years; yr++){
			for(int yb = 0; yb < period; yb++){
				dataByYear[yr][yb] = data[yr * period + yb];
			}
		}

		// SIN WAVE CALIBRATION - FOR EACH YEAR
		if(showProgress)
			System.out.println("Finding best fitting parameters for annual sine curve...");
		// Search params
		metric = STD;
		resolution = 100;
		depth = 3;
		fn = new int[]{FN_SIN};//, FN_RND_WALK};
		// Offset    (-1  to   1)
		// Amplitude ( 0  to  ??)
		// Height    (-?? to +??)
		// Period    (1   to 365)
		delta = new double[]{1, var, 0, 0};
		double[][] p_var = new double[years][delta.length];
		double[] p_var_mean = new double[delta.length];
		double[] p_var_amp = new double[years];
		double p_amp_min = Double.MAX_VALUE;
		double p_amp_max = -Double.MAX_VALUE;
		for(int yr = 0; yr < years; yr++){
			p[0] = new double[]{0, var, mean[yr], period};
			in[0] = new double[4];
			iSearch(metric, null, delta, resolution, depth, fn, p, in, rand, seed, dataByYear[yr], null);
			e = error(metric, null, fn, p, in, rand, seed, dataByYear[yr], null, false);
			if(showProgress)
				System.out.println("p[0] = new double[]{" + Utils.arrayString(p[0]) + "}; // e=" + e);
			Utils.copy(p[0], p_var[yr]);
			p_var_amp[yr] = p[0][1];
			if(p[0][1] < p_amp_min) p_amp_min = p[0][1];
			if(p[0][1] > p_amp_max) p_amp_max = p[0][1];
			for(int i = 0; i < p[0].length; i++)
				p_var_mean[i] += p[0][i];
		}
		// Calculate mean value for seasonal variation parameters
		for(int i = 0; i < p[0].length; i++)
			p_var_mean[i] /= years;
		double p_amp_var = (p_amp_max - p_amp_min) * 0.5, p_amp_mid = p_amp_min + p_amp_var;
		if(showProgress){
			System.out.println("p_var_mean = new double[]{" + Utils.arrayString(p_var_mean) + "};");
			System.out.println("p_amp_min=" + p_amp_min);
			System.out.println("p_amp_max=" + p_amp_max);
			System.out.println("p_amp_var=" + p_amp_var);
			System.out.println("p_amp_mid=" + p_amp_mid);
		}
//		p_var_mean = new double[]{0.13381692307692303, 1.456448717948718, 15.68884615384616, 12.0 };
//		if(true) return p;
		
		// SIN WAVE AMPLIDUDE - Mean
		// Assuming no inter-annual change in mean here...
		// Basic parameters
		if(showProgress)
			System.out.println("Finding mean sine wave amplitude...");
//		preview("annual amplitude variation ", "", "", p_var_amp);
		p_var_amp = expand(p_var_amp, period);
//		preview("weekly amplitude variation ", "", "", p_var_amp);
//		preview("mean data", "", "", new String[]{"real", "mean"}, new double[][]{data, mean});
//		if(true) return null;
		metric = STD;
		resolution = 100;
		depth = 4;
		fn = new int[]{FN_RND_WALK};
		// drift parameter          (-? to ?)
		// disturbance parameter    ( 0 to ?)
		// predictability parameter ( 0 to 1)
		// Influence of bounds      ( 0 to ?)
		// Minimum value
		// Maximum value
		delta = new double[]{0, 0, 0, 0, 0, 0, p_amp_mid}; // Only init value needs to be found here
		p[0] = new double[]{0, 0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 0, p_amp_mid};
		in[0] = new double[]{0, 0, p_amp_mid, 0};
//		attempts = 10000;
//		resolution = 10000;
//		rSearch(metric, null, attempts, keep, delta, resolution, depth, fn, p, in, rand, seed, p_var_amp);
		iSearch(metric, null, delta, resolution, depth, fn, p, in, rand, seed, p_var_amp, null);
		if(showProgress){
			e = error(metric, null, fn, p, in, rand, seed, p_var_amp, null, true);
			System.out.println("p[0] = new double[]{" + Utils.arrayString(p[0]) + "}; // e=" + e);
		}
		// Set up calibrated values for next calibration
		in[0] = new double[]{0, 0, p[0][6], 0};
		p[0] = new double[]{0, 0, 1, -Double.MAX_VALUE, Double.MAX_VALUE, 0};
//		double[] p_mean_amp = Utils.copy(p[0]);
		
//		if(true) return p;	
		
		
		// SIN WAVE AMPLIDUDE - Variability
		// Basic parameters
		if(showProgress)
			System.out.println("Finding random walk parameters for variability in sine wave amplitude");
		metric = DIF_STD + DIF_DIF;
		resolution = 1000;
		attempts = 10000;
		depth = 3;
		seed = 0;
		fn = new int[]{FN_RND_WALK, FN_RND_WALK};
		// drift parameter          (-? to ?)
		// disturbance parameter    ( 0 to ?)
		// predictability parameter ( 0 to 1)
		// Influence of bounds      ( 0 to ?)
		// Minimum value
		// Maximum value

		// Calibration of predictability
//		metric = DIF_PRD;
//		delta = new double[]{0, 0, 0.5, 0, 0, 0, 0};
//		p[1]  = new double[]{0, 5.0, 0.95, 0, -Double.MAX_VALUE, Double.MAX_VALUE, 28.284148};
//		p[1] = new double[]{0, 1, 0.8655458523679854, 0, -Infinity Infinity 28.28414800000001 }; // e_avg=1.4948853698348864E-7
//		p[1] = new double[]{0.0 0.7999999999999992 0.7972716551155146 0.0 -Infinity Infinity 28.284147999999977 }; // e=0.006986750214481863
//		p[1] = new double[]{0.0 1.0 0.6701975 0.0 -1.7976931348623157E308 1.7976931348623157E308 28.284148 }; // e=2.7289333986990627E-11
		// Calibration of variance
		delta = new double[]{0, p_amp_var, 0,                 0,                0, 0};
		p[1]  = new double[]{0, p_amp_var, 0.95, -Double.MAX_VALUE, Double.MAX_VALUE, 0};
//		p[1]  = new double[]{0, 1, 0.95, 2, 11, 42};
//		p[1] = new double[]{0.0, 0.9530596, 0.95, 2.0, 11, 42, 28.284148}; // e=1.724193713046894
//		p[1] = new double[]{0.0, 0.7988819896616793, 0.8655458523679854, 2.0, 11, 42, 28.284148 }; // e_avg=0.12986219105507996
//		p[1] = new double[]{0.0 1.215968 0.6701975 2.0 11.0 42.0 28.284148 }; // e=0.2111716498676699
		// Simultaneous calibration of predictability and variance
//		p[1] = new double[]{0.0, 1.422413369565082, 0.9147144110561841, 2.0, 11.0, 42.0, 28.284148 }; // e=0.0012795197307280404
//		p[1] = new double[]{0.0, 0.8493871107052197, 0.7286099564326088, 2.0, 11, 42, 28.284148 }; // e_avg=0.05367742917305481
//		p[1] = new double[]{0.0, 0.726578139381851, 0.5842597736462546, 2.0, 11, 42, 28.284148000000002 }; // e_avg=0.05163117097550847
		// Calibration of disturbance, predicability and bounds influence simultaneously
//		delta = new double[]{0, 3, 0.5, 2, 0, 0};
//		p[1]  = new double[]{0, 3, 0.5, 2, 11, 42 };
		// Initial input
		in[1] = new double[]{0, 0, in[0][2], 0};
//		iSearch(metric, delta, resolution, depth, fn, p, in, rand, seed, p_var_amp);
//		rSearch(metric, attempts, keep, delta, resolution, depth, fn, p, in, rand, seed, p_var_amp, null);
//		System.out.println("e1=" + e1);
		resolution = 100; attempts = 1000;
		avgSearch(true, 100, metric, null, attempts, keep, resolution, depth, fn, delta, p, in, rand, seed, p_var_amp, null, showProgress);
		if(showProgress){
			e = error(metric, null, fn, p, in, rand, seed, p_var_amp, null, true);
			System.out.println("p[1] = new double[]{" + Utils.arrayString(p[1]) + "}; // e=" + e);
		}
		
		// Return a parameter array suitable for use in with FN_VPR_NOISE
		return new double[][]{
				// PARAMETERS
				// Force stability     ( 0 to +?)
				// Force periodic      ( 0 to +?)
				// Force random        ( 0 to +?)
				// Min value           ( ? to  ?)
				// Max value           ( ? to  ?)
				// Influence of bounds ( 0 to +?)
				// Wave offset      (-1 to  +1)
				// Wave period      ( 1 to 365)
				// Wave amplitude variability         ( 0 to ?)
				// Wave amplitude predictability      ( 0 to 1)
				// Wave amplitude Influence of bounds ( 0 to 1)
				{
					0, 0, 0, 0, 0, 0,
					p_var_mean[0], period,
					p[1][1], p[1][2], 0.2
				},
				// INPUTS
				// Day of year
				// Mean
				// Previous mean
				// Previous change in mean
				// Previous wave amplitude
				// Previous change in wave amplitude
				{
					0, 0, 0, 0, in[0][2], 0
				}
		};

	}
	
	
	/** Calibrates the rainfall function, input data must be in mm
	 * outputs the coeificients */
	public static double[][] calibrateRain(double[] data){
		double[][] p = new double[3][];
		double[][] in = new double[3][];
		
		// Correction to remove anomaly from data, wouldn't be expected in average years
		data = crop(data, 0, 50);
//		double[] rainfall = new double[data.length];
//		for(int i = 0; i < rainfall.length; i++) rainfall[i] = data[i] > 50 ? 50 : data[i];
//		data = rainfall;
		// Basic rainfall parameters
		int[] fn = {FN_SIN_VAR, FN_RAIN_PROB, FN_RAIN_RAND}; 
		int seed = 2;
		int metric = 0;
		double[] delta;
		int resolution = 10;
		int depth = 3;
		int attempts = 100000;
		int keep = 3;

		// RAINFALL MEAN CALIBRATION
		// Search params
		metric = STD;
		resolution = 10;
		depth = 3;
		// Initial parameters
		delta = new double[]{1, 4, 4, 6};
		p[0] = new double[]{0, 4, 4, 6};
//		p[0] = new double[] {-0.3729, 4.0128, 2.2948, 3.0012}; //e=6.037334113106113
//		iSearch(metric, seed, fn, delta, resolution, depth, p, data);
//		System.out.println("e=" + error(metric, null, seed, fn, p, data, true) + " p[0] = new double[]{" + Utils.arrayString(p[0]) + "};");	
		
/*		// RAINFALL PROBABILITY CALIBRATION
		// Search params
		metric = DIF_ZTM + SEA_ZTM;
		resolution = 10;
		depth = 5;
		attempts = 10000;
		keep = 20;
		// Initial parameters
		p[1] = new double[]{0.5, 0.5};
		// Variation in parameters
		delta = new double[]{0.5, 0.5};
		p[1] = new double[]{0.712, 0.405};
//		e=0.0016447744089651134 p[1] = new double[]{0.7155913056301868 0.37826917412673455 }
//		e=0.0016447744089651134 p[1] = new double[]{0.7151271184615412 0.3763066641515146 }
		
//		avgSearch(10, metric, fn, delta, attempts, keep, resolution, depth, p, data);	
//		rSearch(metric, seed, fn, attempts, keep, delta, resolution, depth, p, data);		
//		System.out.println("e=" + error(metric, seed, fn, p, data, true) + " p[1] = new double[]{" + Utils.arrayString(p[1]) + "}");	
//		if(true) return p;
*/
	
		// RAINFALL VARIANCE CALIBRATION
		// Search params
		// TODO: It seems that the metrics still aren't right, need sorting
		double[] mWeights = 
			{1,  0.6,        1,      3.5,      3.5,      0.08,     0.3,      0.15,        0.9};
		metric = DIF_STD + DIF_DIF + DIF_INT + DIF_ZTM + SEA_STD + SEA_DIF + SEA_INT + SEA_ZTM;
		resolution = 100;
		depth = 5;
		attempts = 10000;
		keep = 5;
		// Initial parameters for a full search
//		p[2]  = new double[]{2, 0.5, 5, 0.5, 0.5};		
//		delta = new double[]{2, 0.5, 5, 0.5, 0.5};
		// Initial parameters for a half calibrated search
		p[2]  = new double[]{0.9, 0.7, 5, 0.8, 0.2};		
		delta = new double[]{0.5, 0.4, 2, 0.2, 0.2};
//		p[2] = new double[]{0.9734572007080129, 0.5539750482385839, 4.370285613695183, 0.7733214397173217, 0.22247738264122957 };
//	Using isearch - fairly good result
//		e=3.958511327521613 p[2] = new double[]{1.546434 0.7495 3.67 0.6869999999999999 0.2645 };
// rSearch with half calibrated initial params and metric weights
//		e=1.8993313431794017 p[2] = new double[]{0.4394256484105218 0.9191203704040855 6.873816956115164 0.7966868797437885 0.3916041503059413 };
//		e=3.384598606495803 p[2] = new double[]{0.945005938905615 0.5630332538180242 4.103406417542914 0.786909926139959 0.3737101444957015 };
//		e=1.670263359918092 p[2] = new double[]{0.6430158036587127 0.9458363161547236 6.665242399178882 0.6583857904031729 0.2589248432923995 };
// Generated after an avgSearch for 100 rounds
//		e=7.09622669260384 p[2] = new double[]{0.9734572007080129 0.5539750482385839 4.370285613695183 0.7733214397173217 0.22247738264122957 };
//		resolution = 1000; iSearch(metric, seed, fn, delta, resolution, depth, p, data);
//		rSearch(metric, seed, fn, attempts, keep, delta, resolution, depth, p, data);
//		avgSearch(100, metric, fn, delta, attempts, keep, resolution, depth, p, data);	
		p = Utils.copy(RAIN07);
		System.out.println("e=" + error(metric, mWeights, fn, p, in, new Random(), seed, data, null, true) + " p[2] = new double[]{" + Utils.arrayString(p[2]) + "};");	

/*		int[] metrics = {
//				STD,
//				DIF_STD,
//				DIF_DIF,
//				DIF_INT,
//				DIF_ZTM,
				SEA_STD,
				SEA_DIF,
				SEA_INT,
				SEA_ZTM,
				SEA_STD + SEA_DIF + SEA_INT,
				DIF_STD + DIF_DIF + DIF_INT + SEA_STD + SEA_DIF + SEA_INT,
		};
//		metricSearch(metrics, seed, fn, delta, resolution, depth, p, data);
*/		
		return p;
	}
	
	public static double[][] calibrateTemp(double[] data){
		double[][] p = new double[3][];
		double[][] in = new double[3][];
		
		// Basic temperature parameters
		int[] fn = {FN_SIN, FN_TEMP}; 
		int seed = 0;
		int metric = 0;
		double[] delta;
		int resolution = 100;
		int depth = 5;
		int attempts = 10000;
		int keep = 3;

		// TEMPERATURE MEAN
		// Search params
		metric = STD;
		resolution = 1000;
		// Initial parameters
		p[0] = new double[]{0, 0, 0};
		// Variation in parameters
		delta = new double[]{1, 40, 40};
//		double[] p = {1, 4, 5};
//		double[] p = {0.68654593, 4.44870861, 9.07091928};
//		iSearch(metric, seed, fn, delta, resolution, depth, p, data);
//		System.out.println("e=" + error(metric, null, seed, fn, p, data, true) + " p[0] = new double[]{" + Utils.arrayString(p[0]) + "};");	
		
		// TEMPERATURE VARIANCE
		// Search params
		metric = DIF_STD + DIF_DIF;
		resolution = 100;
		// Initial parameters
		p[2] = new double[]{5, 5};
		// Variation in parameters
		delta = new double[]{5, 5};
//		p[0] = Force towards mean   (0 to +?)
//		p[1] = Force away from mean (0 to +?)
//		double[] p2 = {3, 2};
//		double[] p2 = {4.9411, 2.2457};
//		rSearch(metric, seed, fn, attempts, keep, delta, resolution, depth, p, data);
//		avgSearch(100, metric, fn, delta, attempts, keep, resolution, depth, p, data);	
		p = Utils.copy(TEMP07);
		System.out.println("e=" + error(metric, null, fn, p, in, new Random(), seed, data, null, true) + " p[2] = new double[]{" + Utils.arrayString(p[2]) + "};");	
		
		return p;
	}

	public static double[][] calibrateRad(double[] data){
		double[][] p = new double[3][];
		double[][] in = new double[3][];
		
		/* Conversion from KW/m2 to MJ/m2/d: 1KW/m2 = 1000W/m2 = 1000J/m2/s = 3600000 J/m2/h = 3.6 MJ/m2/h = 86.4 MJ/m2/d */
		data = scale(data, 86.4);
//		double[] solar_rad = new double[data.length];
//		for(int i = 0; i < solar_rad.length; i++) solar_rad[i] = data[i] * 86.4;
//		data = solar_rad;

		// Basic radiation parameters
		int[] fn = {FN_SIN, FN_RAD};
		int seed = 0;
		int metric = 0;
		double[] delta;
		int resolution = 100;
		int depth = 5;
		int attempts = 10000;
		int keep = 3;

		// SOLAR RADIATION MEAN 
		// Search params
		metric = STD;
		resolution = 1000;
		// Initial parameters
		p[0] = new double[]{0, 10, 10};
		// Variation in parameters
		delta = new double[]{1, 10, 10};
//		double[] p = {1, 1, 0}; // Initial values to search from
//		double[] p = {1.7869190105, 5.05709288, 6.25506863};
		p[0] = new double[]{-0.21582104777999997, 5.057829668599998, 6.255065800000001};
//		iSearch(metric, seed, fn, delta, resolution, depth, p, data);
//		System.out.println("e=" + error(metric, null, seed, fn, p, data, true) + " p[0] = new double[]{" + Utils.arrayString(p[0]) + "};");	
		
		// SOLAR RADIATION VARIANCE
		// Search params
		metric = DIF_STD + DIF_DIF + SEA_STD + SEA_DIF;
		resolution = 100;
		// Initial parameters
		p[2] = new double[]{5, 0.5};
		// Variation in parameters
		delta = new double[]{5, 0.5};
//e=0.9866474480791183 p[1] = new double[]{3.1815140740933288 0.3904149428449848 };
//		p[0] = Variability          (0 to +?)
//		p[1] = Seasonal variability (0 to  1)
//		rSearch(metric, seed, fn, attempts, keep, delta, resolution, depth, p, data);
//		avgSearch(100, metric, fn, delta, attempts, keep, resolution, depth, p, data);	
		p = Utils.copy(RAD07);
		System.out.println("e=" + error(metric, null, fn, p, in, new Random(), seed, data, null, true) + " p[2] = new double[]{" + Utils.arrayString(p[2]) + "};");	
		
		return p;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ========================[ REGRESSION ANALYSIS ]=============================
	
	
	
	
	
	/** Because random data must be used for the variance properties, it is 
	 * necessery to carry out a number of searchs using different random
	 * seeds, in order to yield a good average */
	private static double avgSearch(
			boolean rSearch,
			int runs,
			int metric,
			double[] mWeights,
			int attempts,
			int keep,
			int resolution,
			int depth,
			int[] fn,
			double[] delta,
			double[][] pr,
			double[][] in,
			Random rand,
			long seed,
			double[] data,
			double[] meanData,
			boolean showProgress){		
//System.out.println("attempts:" + attempts + "\nres:" + resolution);
		Random seedGen = new Random(seed);
		double[] p = null; int ip = pr.length; while(pr[--ip] == null && ip > 0); p = pr[ip];
		double[] ps = Utils.copy(p);
		double e_avg = 0;
		double[] e_ = new double[runs];
		double[][] p_ = new double[runs][p.length];
		for(int i = 0; i < runs; i++){
			Utils.copy(ps, p); // Restore original value of p
			e_[i] = rSearch ?
					rSearch(metric, mWeights, attempts, keep, delta, resolution, depth, fn, pr, in, rand, seedGen.nextLong(), data, meanData) :
					iSearch(metric, mWeights, delta, resolution, depth, fn, pr, in, rand, seedGen.nextLong(), data, meanData);
			e_avg += e_[i];
			// record p
			Utils.copy(p, p_[i]);
			if(showProgress)
				System.out.println("\te=" + e_[i] + " p=(" + Utils.arrayString(p_[i]) + ")");
		}
		e_avg /= runs;
		// Weight parameter sets according to error
		double ew_sum = 0;
		double[] p_avg = new double[p.length];
		for(int i = 0 ; i < runs; i++){
			// The higher the error, the lower the weight
			double ew = e_avg == 0 ? 1 : 1 - (e_[i] / (e_avg * 2));
			ew = ew < 0 ? 0 : ew;
			ew_sum += ew;
			for(int j = 0; j < p.length; j++)
				p_avg[j] += p_[i][j] * ew;
		}
		for(int j = 0; j < p.length; j++) p_avg[j] /= ew_sum;
		Utils.copy(p_avg, p);
		if(showProgress)
			System.out.println("e_avg=" + e_avg + " p=(" + Utils.arrayString(p) + ")");
		return e_avg;
	}
	
	/** Searches for average, and standard deviation of parameters for multiple data */
	private static double dataSearch(
		int metric,
		double[] mWeights,
		double[] delta,
		int resolution,
		int depth,
		int[] fn,
		double[][] pr,
		double[][] in,
		Random rand,
		long seed,
		double[][] dataSet,
		double[] meanData){
		double[] p = null; int ip = pr.length; while(pr[--ip] == null && ip > 0); p = pr[ip];
		double[][] p_cum = new double[dataSet.length][p.length];
		double[] e = new double[dataSet.length];
		double e_avg = 0;
		double[] p_orig = Utils.copy(p);
		double[] p_max = Utils.copy(p);
		double[] p_min = Utils.copy(p);
		for(int i = 0; i < dataSet.length; i++){
			Utils.copy(p_orig, p);
//System.out.println("searching from:" + Utils.arrayString(pr));
			iSearch(metric, mWeights, delta, resolution, depth, fn, pr, in, rand, seed, dataSet[i], meanData);
			for(int j = 0; j < p.length; j++){
				p_cum[i][j] = p[j];
				p_min[j] = p[j] < p_min[j] ? p[j] : p_min[j];
				p_max[j] = p[j] > p_max[j] ? p[j] : p_max[j];
			}
			e[i] = error(metric, null, fn, pr, in, rand, seed, dataSet[i], meanData, false);
			e_avg += e[i];
			System.out.println("p[0] = new double[]{" + Utils.arrayString(p) + "}; // e=" + e[i]);
		}
		e_avg /= e.length;
		Utils.copy(errorWtAvg(p_cum, e, e_avg), p);
		System.out.println("p[0] = new double[]{" + Utils.arrayString(p) + "}; // e_avg=" + e_avg);
		System.out.println("p_min = new double[]{" + Utils.arrayString(p_min) + "};");
		System.out.println("p_max = new double[]{" + Utils.arrayString(p_max) + "};");
		// Calcualte standard deviation
		double[] p_std = new double[p.length];
		for(int i = 0; i < p_cum.length; i++){
			for(int j = 0; j < p.length; j++){
				p_std[j] += Math.pow(p_cum[i][j] - p[j], 2);
			}
		}
		for(int j = 0; j < p.length; j++)
			p_std[j] = Math.pow(p_std[j] / p_cum.length, 0.5);
		System.out.println("p_std = new double[]{" + Utils.arrayString(p_std) + "};");
		
		return e_avg;
//		for(int j = 0; j < p.length; j++) avp[j] /= dataSet.length;
//		System.out.println("avp = new double[]{" + Utils.arrayString(avp) + "}; // e=" + e);
	}
	
	/** Returns a weighted average according to its error */
	private static double[] errorWtAvg(double[][] d, double[] e, double e_avg){
		// Weight parameter sets according to error
		double ew_sum = 0;
		double[] d_avg = new double[d[0].length];
		for(int i = 0 ; i < d.length; i++){
			// The higher the error, the lower the weight
			double ew = e_avg == 0 ? 1 : 1 - (e[i] / (e_avg * 2));
			ew = ew < 0 ? 0 : ew;
			ew_sum += ew;
			for(int j = 0; j < d[i].length; j++)
				d_avg[j] += d[i][j] * ew;
		}		
		for(int j = 0; j < d_avg.length; j++) d_avg[j] /= ew_sum;
		return d_avg;
	}
	
	/** If unsure about the appropriate metrics to use, a search can
	 * be used to check. This code causes a search to be conducted
	 * with different metrics used to judge fitness of the funciton
	 * Results are graphed to allow visual inspection */
	private static void metricSearch(
			int[] metrics,
			int[] fn,
			double[] delta,
			int resolution,
			int depth,
			double[][] pr,
			double[][] in,
			Random rand,
			long seed,
			double[] data,
			double[] meanData){
			for(int i = 0; i < metrics.length; i++){
				iSearch(metrics[i], null, delta, resolution, depth, fn, pr, in, rand, seed, data, meanData);
				System.out.println("e=" + error(metrics[i], null, fn, pr, in, rand, seed, data, meanData, true) + " p=" + Utils.arrayString(pr));	
			}	
	}
	
	private static double iSearch(
			int metric,
			double[] mWeights,
			double[] delta,
			int resolution,
			int depth,
			int[] fn,
			double[][] pr,
			double[][] in,
			Random rand,
			long seed,
			double[] data,
			double[] meanData){
		double[] p = null; int ip = pr.length; while(pr[--ip] == null && ip > 0); p = pr[ip];
		double e = 1E300;
		double e_ = 1E299;
		// Record original value of p
		double[] p_orig = Utils.copy(p);
		double[] p_max = new double[p.length];
		double[] p_min = new double[p.length];
		for(int i = 0; i < p.length; i++){
			p_max[i] = p_orig[i] + delta[i];
			p_min[i] = p_orig[i] - delta[i];
		}
		// Start with basic iterative sweap on each parameter until error won't get lower
		int si = 0;
		while(e_ < e && si++ < resolution ){
			e = e_;
			e_ = iSearch_(metric, mWeights, delta, resolution, e, fn, pr, p_max, p_min, in, rand, seed, data, meanData);
//			System.out.println("er=" + e_ + "\tp=(" + Utils.arrayString(p) + ")");			
		}
		// Then increase precision by doing hirearchical search
		double[] d = Utils.copy(delta);
		for(int i = 0; i < d.length; i++) d[i] = delta[i] * 0.1;
		// Max depth is limited by precision of p
		// when d is too small dp = p
		int di = 0;
		while(di++ < depth && canRecord(p, d)){
			e = Double.MAX_VALUE;
			si = 0;
			while(e_ < e && si++ < resolution){
				e = e_;
				e_ = iSearch_(metric, mWeights, d, resolution, e, fn, pr, p_max, p_min, in, rand, seed, data, meanData);
//				System.out.println("ed=" + e_ + "\tp=(" + Utils.arrayString(p) + ")");			
			}
			// Drop deaper into search - searches around best found so-far
			for(int i = 0; i < d.length; i++) d[i] *= 0.1;
//			System.out.println("edf=" + e_ + "\tp=(" + Utils.arrayString(p) + ")");
		}
//		System.out.println("e=" + e_ + "\tp=(" + Utils.arrayString(p) + ")");
		return e_;
	}
	/** Iteratively searches each of the parameters for the value which minimises the error */
	private static double iSearch_(
			int metric,
			double[] mWeights,
			double[] delta,
			int intervals,
			double e,
			int[] fn,
			double[][] pr,
			double[] p_max,
			double[] p_min,
			double[][] in,
			Random rand,
			long seed,
			double[] data, 
			double[] meanData){
		double[] p = null; int ip = pr.length; while(pr[--ip] == null && ip > 0); p = pr[ip];
//System.out.println("\ti on: e=" + e + "\tp=(" + Utils.arrayString(p) + "), delta=" + Utils.arrayString(delta) + "..");
		// Record original value of in
		for(int i = 0; i < p.length; i++){
			if(p_min[i] == p_max[i]) continue;
			double ps = p[i]; // Maintains the original value of p[i]
			double pb = p[i]; // Maintains the best value of p[i]
			// Slice parameter space into intervals of size dlt
			double dlt = delta[i] / intervals;
			// Search for value of p[i] which minimises error
			for(int w = 0; w < intervals; w++){
				p[i] = ps + (dlt * w);
				double e_ = error(metric, null, fn, pr, in, rand, seed, data, meanData, false);
				if(e_ < e && p[i] < p_max[i] && p[i] > p_min[i]){
					e = e_;
					pb = p[i];
//System.out.println("\tnew e=" + e + "\tp=(" + Utils.arrayString(p) + ")..");
				}
				p[i] = ps - (dlt * w);
				e_ = error(metric, mWeights, fn, pr, in, rand, seed, data, meanData, false);
				if(e_ < e && p[i] < p_max[i] && p[i] > p_min[i]){
					e = e_;
					pb = p[i];
//System.out.println("\tnew e=" + e + "\tp=(" + Utils.arrayString(p) + ")..");
				}
			}
			// Set p[i] to best found so far
			p[i] = pb;	
		}
//System.out.println("\tbest e=" + e + "\tp=(" + Utils.arrayString(p) + ")..");
		return e;
	}
	// Whether any parameter in array p can record changes of delta d
	private static boolean canRecord(double[] p, double[] d){
		for(int i = 0; i < p.length; i++)
			if((p[i] + d[i]) != p[i]) return true;
		return false;
	}
		
	/** Avoids hitting local maxima by initiating with a random search, then
	 * fine-tunes the results by applying a multi-round iterative search on
	 * the 10 best random results */
	private static double rSearch(
			int metric,
			double[] mWeights,
			int attempts,
			int keep,
			double[] delta,
			int iRes,
			int depth,
			int[] fn,
			double[][] pr,
			double[][] in,
			Random rand,
			long seed,
			double[] data, 
			double[] meanData){
		double[] p = null; int ip = pr.length; while(pr[--ip] == null && ip > 0); p = pr[ip];
		double e = Double.MAX_VALUE;
		double le = Double.MAX_VALUE; // Only for printing lowest val so far
		double[] ps = Utils.copy(p);
		// Record original value of in
		TreeSet<Param> pbs = new TreeSet<Param>();
		pbs.add(new Param(p, e));
		for(int j = 0; j < attempts; j++){
			// Set random parameters ranging from pb - d to pb + d
			for(int i = 0; i < p.length; i++) p[i] = ps[i] + (Math.random() - 0.5) * 2 * delta[i];
			double e_ = error(metric, null, fn, pr, in, rand, seed, data, meanData, false);
			// If error is lower than previous, assign new best found
			if(e_ < e){
				if(e_ < le){
					le = e_;
//System.out.println("e=" + le + "\tdouble[] p2 = {" + Utils.arrayString(p) + "}");
				}
				// Add the new item regardless
				pbs.add(new Param(p, e_));
				// Remove lowest element if too big
				if(pbs.size() > keep){
					Param lowest = pbs.last();
//System.out.println(".. replaces: e=" + lowest.e + "\tp=(" + Utils.arrayString(lowest.p) + ")..");
					pbs.remove(lowest);
				}
				// Set new e to lowest e in set
				e = pbs.last().e;
			}
		}
//System.out.println("e(1)=" + e + "\tp=(" + Utils.arrayString(p) + ")");
//		// Set best found
//		double[] pb = Utils.copy(pbs.first().p);
//		e = pbs.first().e;
		// Now do iterative search on the best values
		double[] pb = new double[p.length];
		e = Double.MAX_VALUE;
		for(Param pbp : pbs){
			Utils.copy(pbp.p, p);
//System.out.println("Iterative search on: e=" + pbp.e + "\tp=(" + Utils.arrayString(pbp.p) + ")..");
			// Ensure delta values don't allow values outside permissable range!
			double[] delta_ = new double[delta.length];
			for(int i = 0; i < p.length; i++){
				double min = ps[i] - delta[i];
				double max = ps[i] + delta[i];
				delta_[i] = p[i] - delta[i] < min ? p[i] - min : delta[i];
				delta_[i] = p[i] + delta_[i] > max ? max - p[i] : delta_[i];
			}
			double e_ = iSearch(metric, mWeights, delta_, iRes, depth, fn, pr, in, rand, seed, data, meanData);
//System.out.println("..yields e=" + e_ + "\tp=(" + Utils.arrayString(p) + ")");
			if(e_ < e){
				e = e_;
//System.out.println("e(x)=" + e + "\tp=(" + Utils.arrayString(p) + ")");
				Utils.copy(p, pb);
			}
		}
		// Copy best into p
		Utils.copy(pb, p);
//System.out.println("e(2)=" + e + "\tp=(" + Utils.arrayString(p) + ")");
		return e;
	}
	
	private static class Param implements Comparable{
		final double[] p;
		final double e;
		public Param(double[] p, double e){
			this.p = Utils.copy(p);
			this.e = e;
		}
		public int compareTo(Object o){
			if(equals(o)) return 0;
			if(e < ((Param)o).e) return -1;
			return 1;
        }
		public boolean equals(Object o){
			return ((Param)o).e == e;
		}
	}
	

	// Single curve metrics
	public static final int STD     = 1 << 0;
	// Curve comparrison metrics
	public static final int DIF_STD = 1 << 1; // Standard deviation
	public static final int DIF_DIF = 1 << 2; // Differential
	public static final int DIF_INT = 1 << 3; // Integral
	public static final int DIF_ZTM = 1 << 4; // Zero-time
	public static final int DIF_PRD = 1 << 5; // Predictability (Differential of differential)
	// Seasonality comparrison metrics
	public static final int DIF_SEA = 1 << 6;
	public static final int SEA_STD = 1 << 7;
	public static final int SEA_DIF = 1 << 8;
	public static final int SEA_INT = 1 << 9;
	public static final int SEA_ZTM = 1 << 10;
	public static double error(
			int metric,
			double[] mWeights,
			int[] fn,
			double[][] p,
			double[][] in,
			Random rand,
			long seed,
			double[] data,
			double[] meanData,
			boolean showGraph){

//	System.out.println("\n-----------------------------------\nCalling error with: p=" + Utils.arrayString(p) + ", seed=" + seed + ", in=" + Utils.arrayString(in));
//if(doOutput) System.out.println("in(1)=" + Utils.arrayString(in));
		double[][] in_orig = Utils.copy(in);
//if(doOutput) System.out.println("in(2)=" + Utils.arrayString(in));
		boolean doRand = fn.length > 1;
		GraphPanel graph = null;
		if(showGraph){
			graph = new GraphPanel("", "x", "y", true, false);
			graph.addSeries("Real", Color.BLUE);
			graph.addSeries("Rand", Color.RED);
			graph.addSeries("Mean", Color.GREEN);
			graph.addSeries("MeaN", Color.GREEN);
		}
		int steps = data.length * 1;
		int offset = 0;
		double[] meanVal = new double[steps];
		double meanMin = Double.MAX_VALUE;
		double meanMax = -Double.MAX_VALUE;
		// Dirty hack to allow calibration of init values
		if(fn[0] == FN_RND_WALK && p[0].length > 6){
			// Copy parameter into input
			in[0][2] = p[0][6];
		}
		rand.setSeed(seed);
		double realMax = -Double.MAX_VALUE;
		double realMin = Double.MAX_VALUE;
		// Calculation of mean
		for(int i = 0, d = offset; i < steps; i++, d++){
			if(data[d % data.length] > realMax) realMax = data[d % data.length];
			if(data[d % data.length] < realMin) realMin = data[d % data.length];
			if(meanData == null){
				in[0][0] = d;
				meanVal[i] = eval(fn[0], p[0], in[0], rand);
			}
			else{
				meanVal[i] = meanData[i < meanData.length ? i : meanData.length - 1];
			}
			meanMin = Math.min(meanVal[i], meanMin);
			meanMax = Math.max(meanVal[i], meanMax);
		}
//if(doOutput) System.out.println("in(3)=" + Utils.arrayString(in));
		double realVal = 0;
		double randVal = in[1] != null && in[1].length > 1 ? in[1][2] : 0;
		double realVal_ = 0;
		double randVal_ = 0;
		double realDif = 0;
		double realDif_ = 0;
		double randDif = 0;
		double randDif_ = 0;
		double varReal = 0;
		double varRand = 0;
		double difReal = 0;
		double difRand = 0;
		double intReal = 0;
		double intRand = 0;
		double ztmReal = 0;
		double ztmRand = 0;
		double prdReal = 0;
		double prdRand = 0;
		double seaReal = 0;
		double seaRand = 0;
		double varRealSea = 0;
		double varRandSea = 0;
		double difRealSea = 0;
		double difRandSea = 0;
		double intRealSea = 0;
		double intRandSea = 0;
		double ztmRealSea = 0;
		double ztmRandSea = 0;
		double season = 0;
		rand.setSeed(seed);
		for(int i = 0, d = offset; i < steps; i++, d++){
			realVal_ = realVal;
			realVal = data[d % data.length];
			varReal += Math.pow(realVal - meanVal[i], 2);
			// Season estimation
			// Returns -1 for low season, 1 for high season and 0 for no season
			if(fn.length > 1 && fn[1] == FN_VPR_NOISE){
				season = genSinMean(new double[]{p[1][6], 1, 0, p[1][7]}, d);
//				System.out.println("d: " + d + ", season:" + season);
			} 
			else if(meanMax - meanMin > 0)
				season = ((meanVal[i] - meanMin) / (meanMax - meanMin)) * 2 - 1;
			if(doRand){
				randVal_ = randVal;
				in[1][0] = d;
				in[1][1] = meanVal[i];
				in[1][2] = randVal;
				randVal = eval(fn[1], p[1], in[1], rand);
				realDif_ = realDif;
				randDif_ = randDif;
				realDif = realVal - realVal_;
				randDif = randVal - randVal_;
				varRand += Math.pow(randVal - meanVal[i], 2);
				difReal += Math.abs(realVal - realVal_);
				difRand += Math.abs(randVal - randVal_);
				intReal += realVal;
				intRand += randVal;
				ztmReal += realVal == 0 ? 1 : 0;
				ztmRand += randVal == 0 ? 1 : 0;
				prdReal += Math.abs(realDif - realDif_);
				prdRand += Math.abs(randDif - randDif_);
//System.out.println("prdReal:" + Math.abs(realDif - realDif_) + ", prdRand:" + Math.abs(randDif - randDif_));
				// Seasonal variation calculations
				// NOTE: This assumes that time in high season = time in low season
				seaReal += realVal * season;
				seaRand += randVal * season;
				varRealSea += varReal * season;
				varRandSea += varRand * season;
				difRealSea += difReal * season;
				difRandSea += difRand * season;
				intRealSea += intReal * season;
				intRandSea += intRand * season;
				ztmRealSea += ztmReal * season;
				ztmRandSea += ztmRand * season;
			}
			if(showGraph){
				if(doRand){
					graph.addData("Mean", meanVal[i] + (realMax - realMin) * 1.5);
					graph.addData("MeaN", meanVal[i]);
					graph.addData("Real", realVal + (realMax - realMin) * 1.5);
					graph.addData("Rand", randVal);
				}
/*				else if(doScnd){
					graph.addData("Mean", meanVal[i] + (meanMax - meanMin) * 3);
					graph.addData("MeaN", meanVal[i]);
					graph.addData("Real", realVal + (meanMax - meanMin) * 3);
					graph.addData("Rand", randVal * meanMax * 2);
				}
*/				else{
					graph.addData("Mean", meanVal[i]);
					graph.addData("Real", realVal);
				}
			}
		}
		// Scale predictability down by differential
		if(difReal > 0)	prdReal /= difReal;
		if(difRand > 0) prdRand /= difRand;
//if(doOutput) System.out.println("in(4)=" + Utils.arrayString(in));
		double stdReal = Math.sqrt(varReal / steps);
		double stdRand = Math.sqrt(varRand / steps);
		difReal /= steps;
		difRand /= steps;
		intReal /= steps;
		intRand /= steps;
		ztmReal /= steps;
		ztmRand /= steps;
		prdReal /= steps;
		prdRand /= steps;
		seaReal /= steps;
		seaRand /= steps;
		double stdRealSea = Math.sqrt(Math.abs(varRealSea) / steps);
		double stdRandSea = Math.sqrt(Math.abs(varRandSea) / steps);
		difRealSea = Math.abs(difRealSea) / steps;
		difRandSea = Math.abs(difRandSea) / steps;
		intRealSea = Math.abs(intRealSea) / steps;
		intRandSea = Math.abs(intRandSea) / steps;
		ztmRealSea = Math.abs(ztmRealSea) / steps;
		ztmRandSea = Math.abs(ztmRandSea) / steps;
		if(mWeights == null){
			mWeights = new double[11];
			for(int i = 0; i < mWeights.length; i++)
				mWeights[i] = 1;
		}
		double difStd = Math.abs(stdReal - stdRand) * mWeights[Utils.log2(DIF_STD)];
		double difDif = Math.abs(difReal - difRand) * mWeights[Utils.log2(DIF_DIF)];
		double difInt = Math.abs(intReal - intRand) * mWeights[Utils.log2(DIF_INT)];
		double difZtm = Math.abs(ztmReal - ztmRand) * mWeights[Utils.log2(DIF_ZTM)];
		double difPrd = Math.abs(prdReal - prdRand) * mWeights[Utils.log2(DIF_PRD)];
		double difSea = Math.abs(seaReal - seaRand) * mWeights[Utils.log2(DIF_SEA)];
		double seaStd = Math.abs(stdRealSea - stdRandSea) * mWeights[Utils.log2(SEA_STD)];
		double seaDif = Math.abs(difRealSea - difRandSea) * mWeights[Utils.log2(SEA_DIF)];
		double seaInt = Math.abs(intRealSea - intRandSea) * mWeights[Utils.log2(SEA_INT)];
		double seaZtm = Math.abs(ztmRealSea - ztmRandSea) * mWeights[Utils.log2(SEA_ZTM)];
		if(showGraph){
//System.out.println("difReal:" + difReal + ", difRand:" + difRand + "prdReal:" + prdReal + ", prdRand:" + prdRand);
//System.out.println("difRealSea:" + difRealSea + ", difRandSea:" + difRandSea);
//System.out.println("seaReal:" + seaReal + ", seaRand:" + seaRand);
//difRealSea = Math.abs(difRealSea) / steps;
//difRandSea = Math.abs(difRandSea) / steps;
			graph.displayWindow();
			if((metric & STD)     != 0) System.out.println("STD:"     + stdReal * mWeights[0]);
			if((metric & DIF_STD) != 0) System.out.println("DIF_STD:" + difStd);
			if((metric & DIF_DIF) != 0) System.out.println("DIF_DIF:" + difDif);
			if((metric & DIF_INT) != 0) System.out.println("DIF_INT:" + difInt);
			if((metric & DIF_ZTM) != 0) System.out.println("DIF_ZTM:" + difZtm);
			if((metric & DIF_PRD) != 0) System.out.println("DIF_PRD:" + difPrd);
			if((metric & DIF_SEA) != 0) System.out.println("DIF_SEA:" + difSea);
			if((metric & SEA_STD) != 0) System.out.println("SEA_STD:" + seaStd);
			if((metric & SEA_DIF) != 0) System.out.println("SEA_DIF:" + seaDif);
			if((metric & SEA_INT) != 0) System.out.println("SEA_INT:" + seaInt);
			if((metric & SEA_ZTM) != 0) System.out.println("SEA_ZTM:" + seaZtm);
		}
		double err = 0;
		if((metric & STD)     != 0) err += stdReal * mWeights[0];
		if((metric & DIF_STD) != 0) err += difStd;
		if((metric & DIF_DIF) != 0) err += difDif;
		if((metric & DIF_INT) != 0) err += difInt;
		if((metric & DIF_ZTM) != 0) err += difZtm;
		if((metric & DIF_PRD) != 0) err += difPrd;
		if((metric & DIF_SEA) != 0) err += difSea;
		if((metric & SEA_STD) != 0) err += seaStd;
		if((metric & SEA_DIF) != 0) err += seaDif;
		if((metric & SEA_INT) != 0) err += seaInt;
		if((metric & SEA_ZTM) != 0) err += seaZtm;

//if(fn[0] == 4 && doOutput) System.out.println("\n-------------------------\nfn=" + Utils.arrayString(fn) + " p=" + Utils.arrayString(p) + ", in=" + Utils.arrayString(in) + ", err=" + err);
//if(fn[0] == 4 && doOutput) System.out.println("err=" + err);
		// Restore input values
//if(doOutput) System.out.println("in(5)=" + Utils.arrayString(in));
		Utils.copy(in_orig, in);
//if(doOutput) System.out.println("in(6)=" + Utils.arrayString(in));
		return err;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ==========================[ FUNCTIONS ]===============================
	
	
	
	
	
	
	
	/** Function IDs */
	private static final int FN_LINE      =  0;
	private static final int FN_SIN       =  1;
	private static final int FN_SIN_VAR   =  2;
	private static final int FN_LINE_SIN  =  3;
	private static final int FN_RND_WALK  =  4;
	private static final int FN_PER_WALK  =  5;
	private static final int FN_PER_NOISE =  6;
	private static final int FN_VPR_NOISE =  7;
	private static final int FN_RAIN_PROB =  8;
	private static final int FN_RAIN_RAND =  9;
	private static final int FN_TEMP      = 10;
	private static final int FN_RAD       = 11;
	
	/** Executes the numbered function */
	private static double eval(int fn, double[] p, double[] in, Random rand){
		switch(fn){
		case FN_LINE      : return genLineMean(p, (int)in[0]);
		case FN_SIN       : return genSinMean(p, (int)in[0]);
		case FN_SIN_VAR   : return genSinVarMean(p, (int)in[0]);
		case FN_LINE_SIN  : return genLineSinMean(p, (int)in[0]);
		case FN_RND_WALK  : return genRandWalk(p, in, (NormalDistribution)rand);
		case FN_PER_WALK  : return genPerRandWalk(p, in, (NormalDistribution)rand);
		case FN_PER_NOISE : return genPeriodicNoise(p, in, (NormalDistribution)rand);
		case FN_VPR_NOISE : return genVarPerNoise(p, in, (NormalDistribution)rand);
		case FN_RAIN_PROB : return genProbRain(p, in, rand);
		case FN_RAIN_RAND : return genRandRain(p, in, rand);
		case FN_TEMP      : return genTemp(p, in, rand);
		case FN_RAD       : return genRad(p, in, rand);
		}
		return 0;
	}
	
	//  CALIBRATED FUNCTIONS
		
	// Basic function for fitting a variable to a straight line
	private static double genLineMean(double[] p, int i){
		double c = p[0]; // Orogin    (-? to +?)
		double m = p[1]; // Gradient  (-? to +?)
		return m * i + c;
	}
	// Basic function for fitting an anually varying mean to a sine curve
	private static double genSinMean(double[] p, int i){
		double of = p[0]; // Offset    (-1  to   1)
		double am = p[1]; // Amplitude ( 0  to  ??)
		double hg = p[2]; // Height    (-?? to +??)
		int pr =          // Period    (1   to 365)
			p.length > 3 ? (int)p[3] : 365;
		return Math.sin((((double)i / pr) + of) * Math.PI * 2) * am + hg;
	}
	// Basic function for fitting a frequency variable mean to a sine curve
	private static double genSinVarMean(double[] p, int i){
		double of = p[0]; // Offset    (-1 to  1)
		double fq = p[1]; // Frequency ( 0 to  ?)
		double am = p[2]; // Amplitude ( 0 to +?)
		double hg = p[3]; // Height    (-? to +?)
		return Math.sin((((double)i / 365) + of) * Math.PI * fq) * am + hg;
	}
	
	// Function for fitting a linear trend combined with a frequency variable sine curve
	private static double genLineSinMean(double[] p, int i){
		double gr = p[0]; // Gradient  (-? to +?)
		double of = p[1]; // Offset    (-1 to  1)
		double fq = p[2]; // Frequency ( 0 to  ?)
		double am = p[3]; // Amplitude ( 0 to +?)
		double hg = p[4]; // Height    (-? to +?)
		return gr * i + Math.sin((((double)i / 365) + of) * Math.PI * fq) * am + hg;
	}

	// Random walk with prediciability parameter, The greater the
	// predictability, the more previous values can be used to predict the future
	// drift idea from: From http://en.wikipedia.org/wiki/Random_Walk_Hypothesis
	private static double genRandWalk(double[] p, double[] in, NormalDistribution norm){
		double r = norm.nextNorm();
		double dft = p[0]; // drift parameter       (-? to +?)
		double dst = p[1]; // disturbance parameter (0 to ?)
		double pre = p[2]; // predictability parameter
		double min = p[3]; // Minimum value
		double max = p[4]; // Maximum value
		double ibn = p[5]; // Influence of bounds
		double prev = in[2];
//System.out.println("p=" + Utils.arrayString(p) + ", in=" + Utils.arrayString(in));
		double delta = in[3];
		delta = pre * delta + (1 - pre) * dst * r;
//		delta = pre * delta + (Math.exp((1 - pre)) - 1) * dst * r;
		// Impose min and max with force of ibn
		if(ibn > 0){
			if(delta < 0) delta *= 1 - Math.exp((min - prev) / ibn);
			if(dft   < 0) dft   *= 1 - Math.exp((min - prev) / ibn);
			if(delta > 0) delta *= 1 - Math.exp((prev - max) / ibn);
			if(dft   > 0) dft   *= 1 - Math.exp((prev - max) / ibn);
		}
		prev += dft + delta;
		prev = prev < min ? min : prev > max ? max : prev;
		in[2] = prev;
		in[3] = delta;
		return prev;
	}
	
	// Second order functions
	
	/** Random walk with periodically varying drift */
	private static double genPerRandWalk(double[] p, double[] in, NormalDistribution norm){
		double r = norm.nextNorm();
		double dft = p[0]; // drift parameter       (-? to +?)
		double dst = p[1]; // disturbance parameter (0 to ?)
		double pre = p[2]; // predictability parameter
		double ibn = p[3]; // Influence of bounds
		double min = p[4]; // Minimum value
		double max = p[5]; // Maximum value
		int doy = (int)in[0];
		double prev = in[2];
		double delta = in[3];
		// Generate random walk for wave amplitude
		double[] p_wk = {
				p[6],   // drift parameter       (-? to +?)
				p[7],   // disturbance parameter (0 to ?)
				p[8],   // predictability parameter
				p[9],   // Minimum value
				p[10],  // Maximum value
				p[11]}; // Influence of bounds
		double[] in_wk = {
				0, 0,
				in[4],  // Previous value
				in[5]}; // Delta
		double am = genRandWalk(p_wk, in_wk, norm);
		in[4] = in_wk[2]; in[5] = in_wk[3];
		// Generate periodic wave to add to mean
		double[] p_wv = {
			p[12],  // Offset    (-1  to   1)
			am,
			p[13],  // Height    (-?? to +??)
			p[14]}; // Period    (1   to 365)
		double wv = genSinMean(p_wv, doy);		
		// Calculate new shift
		delta = pre * delta + (1 - pre) * dst * r + wv;
//		delta = pre * delta + (Math.exp((1 - pre)) - 1) * dst * r;
		// Impose min and max with force of ibn
		if(ibn > 0){
			if(delta < 0) delta *= 1 - Math.exp((min - prev) / ibn);
			if(dft   < 0) dft   *= 1 - Math.exp((min - prev) / ibn);
			if(delta > 0) delta *= 1 - Math.exp((prev - max) / ibn);
			if(dft   > 0) dft   *= 1 - Math.exp((prev - max) / ibn);
		}
		prev += dft + delta;
		prev = prev < min ? min : prev > max ? max : prev;
		in[2] = prev;
		in[3] = delta;
//		return wv;
		return prev;
	}

	/** Generates periodic noise on top of a given mean curve, buy making use of a given force parameter */
	private static double genPeriodicNoise(double[] p, double[] in, NormalDistribution norm){
		int doy = (int)in[0];
		double mean = in[1];
		double prev = in[2];
//		double delta = in[3];
		double fi = p[0]; // Stability force ( 0  to   ?)
		double rd = p[1]; // Randomness      ( 0  to  1)
		double of = p[2]; // Wave Offset     (-1  to   1)
		double am = p[3]; // Wave Amplitude  ( 0  to  ??)
		double pr = p[4]; // Period          ( 1  to ??)
		double fo = genSinMean(new double[]{of, am, 0, pr}, doy);
		double r1 = norm.nextNorm();
		double r2 = norm.nextNorm();
		//             (            Force inwards             )   (     force outwards   )
		return prev +  fi * (mean - prev) * (1 + rd * (r1 - 1)) + fo * (1 + rd * (r2 - 1));
	}

	/** Generates periodic noise on top of a given mean curve, buy making use of a given force parameter */
	private static double genVarPerNoise(double[] p, double[] in, NormalDistribution norm){
		// PARAMETERS
		// Force stability     ( 0 to +?)
		// Force periodic      ( 0 to +?)
		// Force random        ( 0 to +?)
		// Min value           ( ? to  ?)
		// Max value           ( ? to  ?)
		// Influence of bounds ( 0 to +?)
		// Wave offset      (-1 to  +1)
		// Wave period      ( 1 to 365)
		// Wave amplitude variability         ( 0 to ?)
		// Wave amplitude predictability      ( 0 to 1)
		// Wave amplitude Influence of bounds ( 0 to 1)
		// INPUTS
		// Day of year
		// Mean
		// Previous mean
		// Previous change in mean
		// Previous wave amplitude
		// Previous change in wave amplitude
		int doy = (int)in[0];
		double mean = in[1];
		double prev = in[2];
//		double delta = in[3];
		double fs = p[0]; // Force stability ( 0 to ?)
		double fw = p[1]; // Force periodic  ( 0 to ?)
		double fr = p[2]; // Force random    ( 0 to ?)
		double min = p[3]; // Minimum value
		double max = p[4]; // Maximum value
		double ibn = p[5]; // Influence of bounds
		double r1 = norm.nextNorm();
		// Generate random walk for variance of wave amplitude
		double[] p_wk = {
			0, // drift parameter       (-? to +?)
			p[8], // disturbance parameter (0 to ?)
			p[9], // predictability parameter
			0, // Minimum value
			2, // Maximum value
			p[10], // Influence of bounds
		};
		double[] in_wk = {
			0, 0,
			in[4], // Previous value
			in[5]  // Delta
		};
//System.out.println("p_wk=" + Utils.arrayString(p_wk) + ", in_wk=" + Utils.arrayString(in_wk));
		double am = genRandWalk(p_wk, in_wk, norm);
		in[4] = in_wk[2]; in[5] = in_wk[3];
		// Generate periodic wave to add to mean
		double[] p_wv = {
			p[6], // Offset    (-1  to   1)
			am,   // Amplitude
			0,    // Height    (-?? to +??)
			p[7]  // Period    (1   to 365)
		};
		double wv = genSinMean(p_wv, doy);		
		// Generate random fluctuations according to wave
		double delta = fs * (mean - prev)   +   fw * wv    +   fr * r1;
		// Impose min and max with force of ibn
		if(ibn > 0){
			if(delta < 0) delta *= 1 - Math.exp((min - prev) / ibn);
			if(delta > 0) delta *= 1 - Math.exp((prev - max) / ibn);
		}
		prev += delta;
		prev = prev < min ? min : prev > max ? max : prev;
		in[2] = prev;
//		return am;
//		return wv;
		return prev;
	}

	// Calibrated rainfall probability
	private static double genProbRain(double[] p, double[] in, Random rand){
		double mean = in[1];
		double r1 = rand.nextDouble();
		double r2 = rand.nextDouble();
		double zl = p[0]; // Zero-time level         (0 to 1)
		double zs = p[1]; // Zero-time seasonallity  (0 to 1)
		return (r1 * (1 - zs)) + (r2 * mean * zs) > zl ? 1 : 0;
	}

	// Calibrated rainfall curve, based on a random walk with
	// randomly varying influence towards the mean sine curve
	private static double genRandRain(double[] p, double[] in, Random rand){
		double mean = in[1];
		double r1 = rand.nextDouble();
		double r2 = rand.nextDouble();
		double r3 = rand.nextDouble();
		double r4 = rand.nextDouble();
//		if(genProbRain(i, rand, p) == 0) return 0;
		// Sum of a non seasonal exponentially distributed rainfall
		// and seasonal exponentially distributed rainfall
		double zl = p[3]; // Zero-time level         (0 to +?)
		double zs = p[4]; // Zero-time seasonallity  (0 to  1)
		
		double lv = p[0]; // level of rainfall            (0 to +?)
		double lb = p[1]; // Seasonal balance of rainfall (0 to  1)
		double pk = p[2]; // Peakiness                    (0 to +?)
//		double pb = p[3]; // Peakiness seasonal balance   (0 to  1)
		// If no rain today then return 0
		if((r1 * (1 - zs)) + (r2 * mean * zs) < zl) return 0;
		return lv * (1 - lb) * Math.exp(r3 * pk) + lv * lb * mean * r4;

//		return lv * (1 - lb) * Math.exp(r1 * pk) + lv * lb * mean * Math.exp(r2 * mean * pk);
//		return lv * (1 - lb) * Math.exp(r1 * pk * (1 - pb)) + lv * lb * mean * Math.exp(r2 * mean * pk * pb);		
//		System.out.println("i:" + i + "\tmean:" + mean + "\tv:" + v + "\tlv:" + lv + "\tlb:" + lb + "\tpk:" + pk + "\tp1:" + p1 + "\tp2:" + p2 );
	}

	// Calibrated temperature curve, based on a random walk with
	// randomly varying influence towards the mean sine curve °C
	private static double genTemp(double[] p, double[] in, Random rand){
		double mean = in[1];
		double prev = in[2];
		double r1 = rand.nextDouble();
		double r2 = rand.nextDouble();
		double fi = p[0]; // Force towards mean   (0 to +?)
		double fo = p[1]; // Force away from mean (0 to +?)
		//           (     force inwards     ) ( force outwards )
		prev += fi * r2 * (mean - prev) + fo * (0.5 - r1); //NormalDistribution.normi(Math.pow(r, Math.exp((prev - mean) * 0.5))) * dev;
		in[2] = prev;
		return prev;
	}

	// Calibrated solar radiation curve, based on a random walk with
	// randomly varying influence towards the mean sine curve
	// Note: values generated from calibrated parameters are in MJ/m²/day
	private static double genRad(double[] p, double[] in, Random rand){
		double mean = in[1];
		double prev = in[2];
		double r1 = rand.nextDouble();
		double r2 = rand.nextDouble();
		double df = mean - prev;
		double va = p[0]; // Variability          (0 to +?)
		double sv = p[1]; // Seasonal variability (0 to  1)
		//               (  force towards mean  )   (    force away from mean  )
		double r = prev + df * r1 * va * (1 - sv) + (0.5 - r2) * mean * va * sv;
		r = r < 1 ? 1 : r;
		in[2] = r;
		return r;
/*		double va = p2[0]; // Natural variation (0 to +?)
		double sv = p2[1]; // Seasonal variation (0 to +?)
		return prev + df * r2 * va + df * Math.exp(va) + (0.5 - r1) * mean * sv;
*/
//		Other possibilities for the curve - me thinks the one above works best tho
//		return mean + (0.5 - r1) * mean * p2[0];
//		return prev + (0.5 - r1) * mean * p2[0] + df * r2 * p2[1];
//		return prev + (0.5 - r1) * mean * p2[0] + df * r2 * p2[1] + df * Math.exp(p2[1] * p2[2] * Math.abs(df));
//		return prev + (0.5 - r1) * mean * p2[0] + df * r2 * p2[1] + Math.exp(Math.abs(df) * p2[2]) * (df < 0 ? -1 : df > 0 ? 1 : 0);
//		return prev + (0.5 - r1) * mean * p2[0] + df * r2 * p2[1] + Math.exp(Math.abs(df) * r2 * p2[2]);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ===========================[ CALIBRATED FUNCTIONS ]============================

	public static final int MILK_PRICE_MONTHLY     = 0;
	public static final int FINISHED_HEIFER_WEEKLY = 1;	
	public static final int CULL_COW_WEEKLY        = 2;
	public static final int BULL_CALF_WEEKLY       = 3;
	public static final int HEIFER_CALF_WEEKLY     = 4;
	public static final int CALFED_HEIFERS_WEEKLY  = 5;
	public static final int WHEAT_MONTHLY          = 6;
	public static final int HAY_MONTHLY            = 7;
	public static final int STRAW_MONTHLY          = 8;
	public static final int CONCENTRATES_MONTHLY   = 9;
	public static final int SILAGE_MONTHLY         = 10;
	public static final int RAINFALL_DAILY         = 11;
	public static final int TEMPERATURE_DAILY      = 12;
	public static final int TEMPERATURE_MIN_DAILY  = 13;
	public static final int SOLAR_RADIATION_DAILY  = 14;
	private static final int[][] FUNCTIONS = {
		{FN_RND_WALK, FN_VPR_NOISE}, // MILK_PRICE_MONTHLY
		{FN_RND_WALK, FN_VPR_NOISE}, // FINISHED_HEIFER_WEEKLY 
		{FN_RND_WALK, FN_VPR_NOISE}, // CULL_COW_WEEKLY
		{FN_RND_WALK, FN_VPR_NOISE}, // BULL_CALF_WEEKLY
		{FN_RND_WALK, FN_VPR_NOISE}, // HEIFER_CALF_WEEKLY
		{FN_RND_WALK, FN_VPR_NOISE}, // CALFED_HEIFERS_WEEKLY
		{FN_RND_WALK, FN_VPR_NOISE}, // WHEAT_MONTHLY
		{FN_RND_WALK, FN_VPR_NOISE}, // HAY_MONTHLY
		{FN_RND_WALK, FN_VPR_NOISE}, // STRAW_MONTHLY
		{FN_RND_WALK, FN_VPR_NOISE}, // CONCENTRATES_MONTHLY
		{FN_RND_WALK, FN_VPR_NOISE}, // SILAGE_MONTHLY
		{FN_SIN_VAR,  FN_RAIN_RAND}, // RAINFALL_DAILY
		{FN_SIN,      FN_TEMP     }, // TEMPERATURE_DAILY
		{FN_SIN,      FN_TEMP     }, // TEMPERATURE_MIN_DAILY
		{FN_SIN,      FN_RAD      }, // SOLAR_RADIATION_DAILY
	};
	private static final double[][][] PARAMS = {
		//RND WALK PARAMETERS
		// drift parameter       (-? to +?)
		// disturbance parameter (0 to ?)
		// predictability parameter
		// Minimum value
		// Maximum value
		// Influence of bounds
		// INPUTS
		// double prev = in[2];
		// VPR_NOISE PARAMETERS
		// Force stability     ( 0 to +?)
		// Force periodic      ( 0 to +?)
		// Force random        ( 0 to +?)
		// Min value           ( ? to  ?)
		// Max value           ( ? to  ?)
		// Influence of bounds ( 0 to +?)
		// Wave offset      (-1 to  +1)
		// Wave period      ( 1 to 365)
		// Wave amplitude variability         ( 0 to ?)
		// Wave amplitude predictability      ( 0 to 1)
		// Wave amplitude Influence of bounds ( 0 to 1)
		// INPUTS
		// Day of year
		// Mean
		// Previous mean
		// Previous change in mean
		// Previous wave amplitude
		// Previous change in wave amplitude
		{ // MILK_PRICE_MONTHLY ... CHANGED ON 18/11/09, limits and prev added by 5,
			{0.0, 0.6273130108096471, 0.95, 5.575, 33.195, 1.1092 },
			{0.0, 0.0, 19.885, 0.0 },
			{0.8932943180187022, 1.3711172712537711, 0.15044698783477625, 8.793, 30.976, 1.10916, 0.08253487179487173, 12.0, 0.7528396152324022, 0.95, 0.2 },
			{0.0, 0.0, 19.885, 0.0, 15.617743895, 0.0}
		},
		{ // FINISHED_HEIFER_WEEKLY 
			{0, 2.507447880228895, 0.95, 1000.89936538461538, 5000.38544230769227, 2.5214134615384634}, 
			{0, 0, 850, 0},
			{0.29841998741675335, 1.225398757882555, 2.655367887445359, 650, 1050, 3.907, 0.043643, 52, 0.3902869421792849, 0.95, 0.2}, 
			{0, 0, 850, 0, 4.293796172484004, 0.0 }
		},
		{ // CULL_COW_WEEKLY
			{0, 2.7249989317795746, 0.95, 200, 700, 1.1520673076923078},
			{0, 0, 600, 0},
			{0.7845318107958746, 9.379813136137848, 2.2878604917346954, 150, 750, 2.88, 0.06366, 52, 0.9849754460827544, 0.95, 0},
			{0, 0, 600, 0, 12.012625836, 0}
		},
		{ // BULL_CALF_WEEKLY
			{0.0, 0.9101382277251817, 0.95, 5, 50, 2 },
			{0, 0, 28.284148, 0},
			{0.7488209587180056, 7.707267649363201, 4.141992255197771, 0, Double.MAX_VALUE, 10, -0.16265857, 52, 0.9530596, 0.95, 0.2 },
			{0, 0, 28.284148, 0, 0, 0}
		},
		{ // HEIFER_CALF_WEEKLY
			{0, 1.3294081864305105, 0.95, 100, 350, 1.6195288461538462},
			{0, 0, 250, 0},
			{0.6851125877803921, 2.0792584487324928, 28.58786803405848, 50, 400, 48.675, -0.08071, 52, 0.5682424753749074, 0.95, 0.2},
			{0, 0, 250, 0, 8.25540720105, 0}
		},
		{ // CALFED_HEIFERS_WEEKLY
			{0, 30.662828510681685, 0.95, 800, 2000, 40.69884615384619}, 
			{0, 0, 1200, 0},
			{0.4924162463835704, 1.950866801800949, 90.10678432535661, 600, 2200, 115.8445, 0.1812942857142857, 52, 9.110759565615293, 0.95, 0.2}, 
			{0, 0, 1200, 0, 85.05408318304534, 0}
		},
		{ // WHEAT_MONTHLY
			{0, 4.290845344020401, 0.95, 20, 55, 3.785125},
			{0, 0, 35, 0},
			{0.12986148105811324, 0.8394053279336223, 3.6557604125215737, 10, 65, 6.3855, -0.19035761904761905, 12, 1.1141842730342832, 0.95, 0.2},
			{0, 0, 35, 0, 8.452416879591299, 0}
		},
		{ // HAY_MONTHLY
			{0, 1.6209830980869957, 0.95, 34.415167, 82.433167, 2.000750},
			{0, 0, 55.506346934999996, 0},
			{0.09963017730549746, 1.452411773486707, 4.936388207744162, 11.01, 238.89, 9.495, -0.033112857142857145, 12.0, 1.3533587949328727, 0.95, 0.2}, 
			{0, 0, 55.506346934999996, 0, 10.617029786232, 0}
		},
		{ // STRAW_MONTHLY
			{0, 1.2146439980070676, 0.95, 13.149167, 45.459167, 1.34625},
			{0, 0, 27.36491069, 0},
			{0.16856576922758323, 1.8038574474016373, 1.7279883780324352, 7.962, 57.618, 2.069, -0.07573476190476189, 12, 0.5362555148004384, 0.95, 0.2},
			{0, 0, 27.36491069, 0, 4.769338710067499, 0},
		},
		{ // CONCENTRATES_MONTHLY
			{0, 2.0590431915026874, 0.95, 118.6, 151.4, 1.366667},
			{0, 0, 129.418749, 0},
			{0.4187972904194364, 1.6035040643747485, 2.5386908180165317, 115.7, 167.3, 2.15, 0.015134, 12, 1.2259004403230447, 0.95, 0.2},
			{0, 0, 129.418749, 0, 4.5260212343249995, 0}
		},
		{ // SILAGE_MONTHLY
			{0, 0.2986013688109604, 0.95, 20, 55, 0.141458},
			{0, 0, 35, 0},
			{0.25372859592084496, 0.28446863712424025, 0.8351670986861037, 10, 65, 0.53, -0.0875088889, 12, 0.16292501174254678, 0.95, 0.2},
			{0, 0, 35, 0, 1.0259852949359998, 0}
		},
		{ // RAINFALL_DAILY (mm)
			{-0.3729, 4.0128, 2.2948, 3.0012},
			{0},
			{0.9734572007080129, 0.5539750482385839, 4.370285613695183, 0.7733214397173217, 0.22247738264122957},			
			{0, 0}
		}, 
		{ // TEMPERATURE_DAILY (°C)
			{0.18388892, -4.4521296, 9.0690876},
			{0},
			{0.5723173575548485, 5.427710792192619},
			{0, 0, 5}
		},
		{ // TEMPERATURE_MIN_DAILY (°C)
			{0.18672707, -4.1946884, 6.253944},
			{0},
			{0.5472114739300266, 6.108503931651386},
			{0, 0, 1}
		},
		{ // SOLAR_RADIATION_DAILY (MJ/m²)
			{-0.21667269778, 5.0787158686, 6.2370843},
			{0},
			{3.207199968993312, 0.38062002785468374},
			{0, 0, 0}
		}
	};
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ==============================[ DATA ]======================================
	
	
//	* Finished: (use weight calculation on cows to estimate numbers)
//	  + Heifers Light
//	  + Heifers Medium
//	  + Heifers Heavy
//    + Dairy Cull Cow
//	  (from rearing_livestock_weekly_02-09.xls)
//	* Rearing:
//	  + Rearing Bulls Calves (no more than 3 weeks old)
//	  + Rearing Heifer Calves -> beginning of Heifers01
//	  + Yearlings (Heifers)   -> beginning of Heifers1P
//	  + Freshly Calved Heifers -> beginning of Cows1ST
//	  + Freshly Calved Cows    -> beginning of Cows2ND
	
	// LIVESTOCK FOR SALE AS MEAT
	// Source: https://statistics.defra.gov.uk/esg/publications/amr/finished_stock.asp
	public static final double[] finished_heifer_weekly_03_09 = {
		93.67, 95.69, 94.91, 91.78, 91.40, 93.33, 94.37, 92.50, 92.40, 90.57, 91.57, 92.30, 93.71, 94.08, 93.49, 92.10, 92.64, 93.11, 92.99, 93.19, 94.32, 93.38, 95.79, 96.91, 97.77, 97.20, 98.33, 97.83, 97.58, 97.58, 96.91, 96.29, 97.52, 96.88, 97.11, 95.96, 94.19, 94.34, 93.36, 93.00, 94.72, 94.39, 95.09, 95.18, 95.54, 96.05, 98.02, 99.20, 103.23, 104.95, 
		102.11, 101.74, 100.39, 99.30, 99.73, 102.36, 101.52, 100.85, 101.17, 102.06, 101.34, 101.06, 101.13, 103.22, 102.83, 103.18, 102.83, 101.80, 101.40, 101.36, 102.43, 103.45, 105.83, 107.68, 107.47, 108.29, 107.38, 108.19, 110.02, 108.98, 109.05, 110.18, 108.57, 105.00, 105.36, 105.05, 105.69, 103.17, 98.53, 97.07, 98.12, 98.15, 95.66, 94.52, 96.07, 97.12, 95.77, 96.44, 95.74, 102.59, 96.98, 96.80, 96.69,
		102.95, 108.30, 106.65, 104.49, 106.38, 104.56, 103.88, 105.71, 105.66, 104.59, 104.64, 104.60, 104.30, 104.93, 106.15, 107.49, 106.27, 106.41, 105.80, 107.16, 105.52, 107.13, 107.82, 108.22, 107.16, 105.10, 102.41, 102.45, 102.87, 101.66, 99.61, 101.27, 100.15, 98.13, 97.30, 97.75, 97.09, 95.99, 95.29, 94.41, 96.50, 97.56, 99.75, 101.50, 104.09, 104.88, 105.75, 106.09, 108.96, 105.88, 105.77, 109.27,
		106.35, 103.99, 101.80, 101.78, 104.70, 104.56, 104.01, 103.61, 104.67, 106.48, 106.36, 106.98, 109.53, 110.38, 111.25, 111.66, 112.05, 111.40, 109.91, 109.08, 110.49, 112.76, 115.72, 116.89, 114.99, 115.11, 114.21, 113.11, 111.93, 109.55, 107.21, 110.28, 112.46, 113.21, 113.73, 117.86, 116.42, 112.65, 111.93, 110.20, 109.24, 112.30, 110.44, 109.59, 112.28, 112.45, 111.35, 108.66, 113.87, 111.66, 110.30, 118.37,
		115.74, 110.19, 108.49, 110.25, 112.20, 113.73, 114.09, 111.78, 111.19, 110.70, 111.83, 113.62, 116.26, 117.42, 112.95, 112.67, 109.18, 108.65, 110.53, 111.86, 110.58, 112.62, 113.04, 113.08, 112.61, 112.57, 113.06, 114.18, 114.84, 116.20, 116.53, 124.32, 0, 0, 118.29, 119.41, 114.32, 118.45, 110.44, 108.90, 113.85, 112.41, 111.27, 107.24, 110.51, 112.65, 114.14, 112.86, 121.39, 116.54, 115.52, 118.92,
		120.75, 121.08, 121.92, 123.42, 125.69, 130.01, 130.69, 132.50, 134.15, 133.69, 134.03, 134.69, 134.20, 139.80, 139.69, 144.23, 143.69, 144.70, 145.33, 143.96, 147.68, 149.24, 152.62, 154.84, 158.31, 157.48, 153.30, 150.17, 146.96, 147.07, 147.22, 149.67, 152.29, 152.74, 150.46, 155.90, 155.57, 151.64, 155.77, 155.81, 152.59, 148.06, 146.77, 147.30, 151.07, 155.65, 150.91, 146.62, 153.90, 158.01, 156.83, 155.21,
		159.41, 168.71, 164.73, 156.86, 154.81, 160.12, 162.87, 159.55, 156.62
	};


	public static final double[] cull_cow_weekly_05_09 = {
		51.32, 49.38, 49.55, 48.80, 49.13, 48.24, 46.91,
		51.85, 51.06, 49.44, 49.53, 51.29, 53.92, 52.98, 51.51, 52.58, 55.00, 54.12, 57.62, 61.65, 61.97, 63.69, 66.12, 67.38, 66.39, 67.07, 68.11, 68.13, 65.19, 64.44, 65.67, 64.10, 64.62, 61.16, 57.92, 52.45, 50.26, 47.66, 49.87, 52.85, 55.72, 55.06, 60.20, 57.49, 56.08, 53.41, 50.36, 48.66, 53.86, 52.92, 48.13, 46.17, 49.69, 47.99, 46.25, 45.96, 43.63, 45.86, 53.72,
		51.94, 56.61, 57.79, 55.04, 57.55, 60.96, 63.69, 61.94, 62.63, 63.20, 63.46, 65.02, 67.09, 67.63, 65.75, 67.36, 66.45, 63.14, 63.14, 63.74, 62.69, 61.61, 62.85, 63.16, 65.70, 63.30, 60.56, 57.76, 55.18, 58.27, 57.40, 61.28, 0, 0, 54.72, 57.44, 59.43, 57.34, 46.40, 45.14, 49.47, 48.36, 45.50, 42.03, 41.96, 43.49, 47.00, 45.73, 46.45, 47.48, 47.58, 43.90,
		56.80, 61.98, 64.30, 68.68, 71.15, 75.37, 78.82, 81.97, 85.08, 82.14, 81.73, 83.50, 84.40, 88.20, 90.55, 91.49, 93.43, 91.39, 90.80, 94.79, 95.26, 97.23, 97.98, 99.56, 99.09, 95.84, 90.20, 83.37, 80.75, 82.83, 86.09, 88.13, 88.16, 90.30, 84.80, 86.89, 84.54, 81.00, 84.02, 84.24, 82.26, 73.95, 72.40, 73.91, 76.14, 75.56, 74.76, 73.91, 75.92, 77.33, 79.23, 80.40,
		86.32, 95.09, 94.17, 92.25, 92.15, 92.35, 89.28, 93.97, 95.17
	};
	
	// LIVESTOCK FOR REARING
	// Source: https://statistics.defra.gov.uk/esg/publications/amr/livestkweek.xls
	public static final double[] bull_calves_weekly_02_09 = {
		15.72, 16.38, 18.63, 26.20, 21.63, 39.72, 35.85, 28.29, 41.40, 32.06, 38.18, 36.02, 32.78, 32.30, 26.42, 49.64, 35.07, 27.33, 26.52, 29.12, 31.76, 30.40, 35.16, 35.66, 29.75, 31.10, 32.96, 24.67, 23.32, 26.55, 23.62, 23.81, 25.60, 22.89, 22.38, 26.59, 25.66, 27.16, 27.04, 31.52, 32.06, 28.97, 28.55, 22.92,
		31.19, 32.12, 34.80, 40.92, 37.94, 33.46, 41.46, 41.48, 41.82, 43.26, 38.39, 36.65, 39.93, 39.28, 45.64, 53.32, 52.62, 56.60, 60.75, 64.12, 64.88, 61.02, 60.23, 52.20, 50.25, 39.58, 42.25, 40.24, 42.80, 40.41, 58.51, 40.15, 42.04, 30.93, 31.74, 33.84, 30.20, 35.22, 29.85, 26.37, 28.33, 27.33, 29.03, 25.08, 27.14, 25.33, 27.02, 27.01, 27.58, 25.76, 24.27, 25.94,
		26.35, 29.71, 31.33, 35.56, 36.93, 33.18, 37.12, 41.18, 38.33, 38.11, 42.62, 53.37, 42.64, 40.42, 44.80, 53.23, 46.93, 51.96, 58.25, 55.35, 58.38, 59.13, 60.85, 57.20, 54.13, 49.36, 53.42, 49.81, 41.22, 42.46, 40.76, 34.93, 38.95, 28.41, 25.52, 22.43, 19.33, 22.90, 23.76, 18.82, 20.06, 18.91, 18.73, 19.06, 20.92, 18.06, 16.41, 15.82, 20.73, 20.39, 16.57, 18.05, 6.69,
		20.83, 17.79, 20.38, 26.67, 21.76, 19.99, 30.24, 17.72, 20.52, 17.98, 19.03, 17.12, 18.92, 20.92, 22.04, 23.82, 23.01, 19.06, 30.03, 30.01, 27.87, 25.94, 24.48, 21.98, 22.25, 20.19, 19.32, 17.87, 13.45, 14.45, 15.08, 15.50, 13.21, 11.80, 12.66, 10.83, 14.15, 7.95, 9.76, 9.77, 10.92, 9.91, 11.31, 8.97, 9.33, 11.40, 7.58, 9.26, 9.05, 9.03, 7.48, 4.12,
		7.19, 7.67, 6.71, 13.49, 9.98, 12.99, 15.41, 14.25, 12.34, 16.88, 12.89, 12.16, 19.90, 21.93, 26.30, 34.43, 30.09, 27.32, 22.15, 27.25, 25.51, 27.28, 25.56, 24.29, 22.17, 17.93, 20.06, 20.64, 28.45, 24.63, 22.54, 22.58, 21.00, 23.46, 28.04, 23.19, 28.27, 32.54, 29.51, 29.64, 28.44, 29.80, 22.92, 24.20, 33.92, 28.75, 20.97, 24.45, 18.67, 20.50, 17.52, 15.18,
		32.41, 21.59, 17.06, 28.47, 19.17, 21.93, 20.54, 22.43, 21.21, 30.70, 29.66, 27.98, 30.16, 36.23, 47.69, 39.35, 37.89, 36.18, 53.09, 48.94, 41.25, 35.31, 47.33, 32.73, 38.40, 34.66, 30.07, 30.12, 34.77, 28.78, 34.97, 33.83, 0, 0, 0, 15.06, 23.72, 0, 0, 0, 9.08, 12.97, 8.97, 9.32, 8.41, 10.13, 8.21, 11.08, 9.25, 9.85, 10.28, 6.95,
		10.57, 15.48, 11.25, 17.98, 21.05, 15.85, 27.84, 20.39, 22.19, 22.15, 28.08, 27.98, 39.80, 30.28, 36.84, 28.79, 23.43, 32.47, 37.86, 32.36, 38.07, 39.92, 41.22, 48.13, 42.46, 46.14, 50.59, 41.89, 39.16, 23.24, 24.95, 27.25, 30.07, 24.48, 27.35, 30.79, 24.89, 28.32, 19.59, 25.16, 22.99, 27.95, 27.81, 28.41, 26.17, 27.58, 26.18, 25.76, 23.85, 23.30, 24.09, 22.81,
		21.87, 32.55, 27.80, 34.61, 43.68, 40.99, 45.13, 48.11, 46.08
	};
	public static final double[] heifer_calves_weekly_02_09 = {
		44.33, 33.54, 41.00, 30.00, 36.68, 27.00, 28.50, 36.85, 51.09, 63.40, 39.71, 4.00, 29.32, 34.12, 29.08, 124.99, 86.12, 32.18, 93.77, 25.72, 18.44, 23.75, 15.74, 22.33, 15.12, 14.17, 19.90, 11.28, 15.00, 35.00, 10.58, 9.48, 24.03, 41.55, 10.23, 17.89, 24.34, 15.27, 22.12, 19.68, 45.93, 40.91, 20.08, 5.00,
		34.17, 10.69, 28.50, 43.72, 33.50, 35.38, 33.50, 17.77, 23.07, 33.26, 48.18, 37.09, 22.53, 38.03, 20.13, 33.62, 27.00, 37.74, 28.50, 32.33, 74.67, 54.93, 40.17, 64.40, 37.12, 17.47, 29.47, 42.09, 43.92, 25.68, 23.88, 46.91, 28.43, 35.47, 32.77, 19.19, 30.15, 22.55, 27.04, 29.66, 19.85, 44.04, 29.10, 14.31, 26.22, 59.13, 14.17, 27.25, 22.90, 36.36, 24.90, 17.00,
		30.28, 33.20, 33.09, 32.11, 33.67, 23.71, 46.00, 56.00, 28.00, 29.79, 56.42, 18.00, 30.07, 34.72, 41.40, 50.88, 20.19, 18.50, 41.10, 33.86, 38.33, 30.64, 104.92, 24.47, 48.91, 23.84, 30.44, 26.29, 24.89, 19.58, 32.85, 15.50, 81.60, 44.98, 40.33, 20.47, 15.67, 26.10, 14.64, 38.27, 8.45, 44.92, 27.41, 33.50, 62.94, 61.55, 24.25, 16.22, 18.67, 43.90, 25.25, 7.83, 8.00,
		39.29, 9.20, 37.55, 17.20, 14.40, 71.50, 9.80, 27.00, 30.28, 79.57, 24.57, 75.78, 42.67, 120.38, 37.63, 68.20, 25.63, 73.00, 30.24, 71.00, 62.00, 46.67, 26.33, 8.00, 6.00, 20.18, 38.19, 32.50, 79.00, 51.18, 16.08, 6.00, 13.39, 27.45, 23.85, 22.00, 16.11, 62.44, 5.13, 10.18, 19.67, 8.64, 100.00, 28.00, 29.57, 40.33, 21.60, 25.00, 43.10, 48.40, 3.25, 0,
		3.50, 1.40, 3.50, 21.67, 2.88, 4.40, 15.20, 50.60, 3.33, 19.36, 11.14, 12.60, 36.00, 33.25, 47.17, 12.00, 3.71, 30.62, 12.78, 22.12, 40.80, 31.68, 18.00, 3.73, 20.80, 33.67, 39.20, 9.25, 100.00, 32.55, 43.64, 7.05, 5.57, 10.71, 9.72, 6.50, 14.91, 43.86, 10.25, 10.55, 17.43, 9.25, 16.86, 56.82, 64.57, 71.00, 83.50, 14.83, 20.10, 10.00, 63.50, 0,
		36.00, 29.42, 9.33, 42.67, 27.14, 58.00, 48.00, 25.80, 13.54, 17.00, 37.56, 47.00, 29.57, 74.75, 0, 64.00, 143.33, 18.67, 14.00, 8.00, 70.25, 9.33, 35.38, 27.33, 50.86, 27.73, 59.25, 30.83, 79.14, 14.00, 82.25, 13.75, 0, 0, 0, 9.00, 73.25, 0, 0, 0, 10.50, 33.40, 2.67, 79.40, 48.25, 9.67, 1.50, 16.00, 16.00, 111.67, 90.00, 2.00,
		45.00, 91.00, 3.00, 8.00, 103.00, 132.00, 166.00, 2.00, 4.00, 21.00, 15.75, 60.00, 0, 21.00, 1.00, 18.00, 28.75, 82.25, 12.00, 125.00, 37.50, 213.00, 140.87, 92.88, 109.17, 44.00, 59.30, 149.50, 68.92, 14.43, 24.88, 12.67, 76.07, 29.44, 13.45, 87.12, 27.58, 77.89, 60.30, 38.07, 25.50, 21.00, 71.00, 95.72, 12.00, 119.00, 83.40, 27.45, 61.44, 56.30, 55.67, 62.62,
		296.00, 34.33, 60.14, 205.50, 79.83, 9.33, 0, 33.67, 33.00
	};
	public static final double[] calfed_heifers_weekly_02_09 = {
		732.15, 682.60, 701.2, 732.15, 732.15, 720.00, 745.00, 559.39, 642.50, 590.00, 605.63, 614.08, 589.10, 659.09, 556.88, 540.57, 607.76, 671.53, 610.78, 543.85, 636.45, 511.67, 603.14, 600.94, 538.91, 553.79, 663.89, 601.46, 551.97, 624.17, 543.79, 612.22, 610.79, 645.76, 565.13, 617.19, 622.08, 602.00, 671.44, 666.41, 699.59, 674.60, 756.47, 660.00,
		552.44, 559.33, 624.10, 649.53, 626.13, 601.42, 598.20, 635.61, 604.88, 672.96, 562.28, 649.00, 670.28, 621.07, 700.76, 678.15, 772.37, 780.52, 790.00, 464.54, 793.12, 661.50, 706.02, 638.81, 712.56, 675.25, 713.58, 747.17, 608.73, 785.25, 688.22, 788.53, 587.48, 643.61, 696.25, 702.38, 734.52, 751.62, 663.93, 675.24, 693.57, 718.40, 723.09, 625.79, 752.81, 628.35, 672.39, 669.49, 619.00, 565.31, 567.22, 644.00,
		601.78, 559.31, 606.45, 617.70, 552.39, 551.10, 651.94, 559.89, 673.93, 633.23, 664.17, 690.02, 619.62, 631.45, 583.57, 474.72, 608.37, 702.19, 611.19, 510.76, 465.90, 741.67, 680.90, 577.42, 537.11, 629.75, 712.14, 505.80, 614.80, 618.00, 548.00, 664.09, 634.32, 548.65, 519.07, 573.26, 654.20, 554.88, 572.57, 539.84, 544.72, 743.24, 235.19, 661.05, 623.77, 578.75, 577.68, 637.26, 628.25, 708.27, 610.56, 650.00,
		677.84, 541.00, 721.78, 732.86, 578.21, 495.27, 786.15, 622.69, 363.17, 460.00, 625.00, 0, 0, 0, 467.20, 0, 0, 0, 0, 0, 0, 0, 0, 101.67, 0, 625.00, 875.00, 0, 0, 224.17, 550.00, 440.00, 878.35, 627.61, 559.55, 572.65, 496.21, 600.56, 609.44, 620.23, 653.32, 882.57, 574.16, 683.75, 356.84, 681.71, 610.49, 0, 637.60, 624.13, 758.90, 368.25,
		786.66, 746.76, 584.00, 613.24, 648.10, 701.95, 718.92, 627.28, 942.66, 427.09, 819.83, 498.52, 609.54, 1393.67, 735.34, 726.81, 1389.98, 809.68, 561.42, 1015.81, 1253.33, 941.20, 719.13, 733.21, 819.15, 562.57, 1303.25, 646.89, 874.06, 707.22, 815.96, 713.45, 897.79, 784.96, 870.27, 768.03, 785.40, 740.67, 605.60, 696.31, 668.59, 650.22, 851.60, 905.22, 532.58, 758.47, 1100.18, 864.55, 2418.56, 846.83, 552.08, 817.50,
		672.01, 652.13, 487.36, 665.25, 849.23, 557.48, 810.83, 849.68, 765.07, 710.39, 783.46, 887.58, 701.43, 887.01, 517.40, 683.70, 743.48, 1425.35, 1044.82, 683.76, 773.39, 669.59, 902.05, 848.55, 955.10, 845.76, 828.25, 891.62, 701.58, 935.44, 1261.66, 1824.03, 0, 0, 0, 1186.96, 1112.46, 0, 0, 0, 982.27, 1031.06, 971.99, 1159.10, 715.88, 846.82, 1131.61, 1028.90, 1176.87, 1158.30, 1240.08, 1109.51,
		1420.78, 727.93, 986.56, 826.39, 1071.22, 1122.55, 956.49, 1101.92, 959.13, 1210.28, 596.83, 933.58, 747.78, 767.33, 942.45, 817.23, 1023.32, 1996.20, 1252.59, 1116.45, 1397.87, 917.86, 1614.87, 1713.75, 1583.41, 1607.34, 1780.03, 1577.79, 1360.48, 1369.65, 1564.47, 1803.82, 1455.57, 1503.26, 1157.00, 1496.52, 1589.77, 1338.62, 1479.45, 1600.47, 1216.48, 1539.43, 1288.32, 1610.72, 1513.33, 1414.71, 1266.80, 1151.82, 1796.82, 1479.61, 1415.87, 1610.72,
		1473.75, 1438.44, 1786.37, 1528.50, 1436.71, 1831.80, 1622.00, 1658.63, 1690.59
	};
	
	
	
	// CLEAN CATTLE
	public static final double[] cattle_weekly_85_09 = {
		96.9, 96.7, 97.8, 96.3, 95.1, 94.5, 96.0, 95.1, 94.4, 94.7, 94.1, 93.8, 93.8, 93.8, 95.1, 94.4, 94.9, 96.0, 97.4, 99.2, 99.0, 98.9, 97.8, 98.1, 97.8, 96.0, 94.8, 94.9, 92.4, 94.8, 94.8, 95.1, 94.8, 93.3, 92.7, 92.4, 91.4, 91.5, 90.9, 91.8, 91.6, 91.8, 92.5, 92.3, 92.3, 92.3, 93.1, 93.7, 94.5, 99.5, 96.0, 96.0, 94.6,
		94.6, 94.0, 93.0, 92.9, 93.9, 94.7, 95.2, 95.5, 95.9, 95.6, 95.2, 94.9, 95.9, 94.9, 96.4, 96.6, 96.8, 97.9, 99.7, 101.4, 101.9, 112.5, 102.2, 99.7, 97.5, 96.1, 96.2, 96.0, 95.2, 94.6, 93.9, 93.9, 93.5, 93.2, 92.9, 92.8, 91.9, 90.2, 89.7, 90.6, 91.1, 90.5, 89.8, 90.4, 91.0, 90.7, 91.3, 92.3, 96.2, 92.4, 92.4, 92.4,
		93.0, 95.2, 94.4, 92.6, 92.4, 91.8, 91.6, 92.4, 92.5, 93.3, 93.9, 94.3, 94.2, 94.4, 94.6, 96.0, 94.9, 95.2, 96.7, 98.2, 98.1, 97.3, 97.0, 97.5, 96.5, 95.1, 95.9, 95.8, 95.7, 96.6, 97.0, 96.9, 95.8, 95.2, 95.9, 95.5, 95.5, 94.7, 94.5, 94.2, 93.2, 93.0, 94.7, 96.6, 97.5, 97.9, 100.1, 101.9, 105.7, 104.3, 104.3, 103.2,
		103.2, 103.5, 103.6, 103.1, 103.4, 105.4, 105.6, 105.3, 106.6, 107.8, 107.5, 107.6, 107.7, 109.8, 109.4, 108.3, 108.3, 109.3, 109.9, 109.7, 110.7, 112.0, 113.7, 113.0, 111.1, 108.9, 109.1, 111.7, 112.5, 112.2, 113.9, 113.9, 112.4, 112.0, 111.1, 110.7, 110.1, 109.0, 107.5, 107.3, 107.1, 106.5, 106.4, 108.2, 110.7, 111.2, 112.5, 112.1, 113.2, 115.1, 113.0, 113.3,
		112.0, 110.6, 109.5, 108.6, 108.4, 107.4, 107.1, 106.9, 107.5, 107.8, 108.7, 108.2, 109.6, 117.9, 120.3, 117.9, 119.7, 119.9, 118.0, 118.7, 117.7, 119.2, 121.6, 120.7, 117.7, 118.6, 118.6, 119.4, 117.6, 114.9, 115.2, 115.8, 115.2, 116.5, 117.0, 116.4, 114.3, 113.6, 114.9, 114.4, 113.0, 112.3, 112.4, 113.2, 113.3, 113.6, 112.7, 113.0, 113.7, 114.1, 111.3, 113.7,
		111.9, 111.0, 109.8, 110.2, 110.7, 109.4, 108.9, 109.6, 109.7, 110.0, 110.3, 110.8, 112.7, 113.7, 113.0, 112.3, 110.7, 109.4, 109.3, 108.0, 104.2, 106.0, 107.2, 107.4, 105.5, 104.0, 105.2, 105.2, 105.4, 104.0, 102.2, 101.5, 102.3, 102.7, 103.6, 103.4, 102.6, 101.2, 100.5, 99.5, 100.3, 100.5, 101.2, 102.4, 104.2, 104.3, 104.1, 104.8, 105.5, 108.4, 106.5, 109.0,
		106.2, 107.2, 107.8, 107.3, 107.1, 107.7, 109.5, 108.0, 107.4, 108.3, 108.0, 108.4, 108.5, 109.6, 109.3, 110.2, 110.7, 110.3, 110.9, 111.1, 111.1, 112.2, 113.0, 111.4, 109.9, 108.0, 107.2, 107.2, 107.3, 106.2, 105.2, 104.9, 104.4, 104.6, 105.0, 104.2, 102.5, 101.5, 101.8, 102.8, 103.2, 102.9, 103.6, 103.8, 103.3, 103.3, 103.6, 104.3, 105.8, 108.4, 105.8, 108.0,
		105.9, 106.6, 106.6, 105.9, 107.0, 106.8, 106.3, 106.6, 107.7, 108.0, 108.3, 108.2, 109.1, 109.8, 110.7, 110.3, 110.3, 109.2, 108.8, 108.7, 108.2, 109.1, 109.0, 109.6, 111.0, 110.2, 107.8, 108.6, 109.0, 110.4, 110.8, 110.3, 109.4, 109.3, 110.0, 109.5, 108.8, 108.2, 107.3, 107.0, 108.0, 110.1, 108.3, 108.0, 108.8, 110.4, 111.8, 112.6, 113.1, 115.0, 114.3, 115.1, 113.8,
		115.2, 115.9, 116.1, 117.8, 120.7, 122.2, 124.6, 127.5, 133.5, 136.7, 134.7, 132.2, 133.4, 134.7, 135.2, 135.9, 135.9, 136.9, 138.8, 140.2, 141.1, 141.5, 142.3, 141.2, 139.5, 139.2, 137.5, 134.5, 130.4, 129.4, 129.4, 129.3, 129.3, 128.7, 127.3, 125.0, 119.6, 117.5, 118.9, 116.7, 115.4, 118.0, 119.8, 120.5, 118.6, 117.9, 119.1, 119.9, 121.8, 120.7, 121.0, 120.1,
		119.6, 121.8, 122.0, 122.1, 122.6, 122.5, 123.7, 123.9, 125.0, 127.1, 128.4, 129.3, 127.6, 129.2, 128.2, 127.4, 126.8, 127.3, 127.8, 128.2, 128.1, 128.1, 127.4, 125.7, 122.8, 121.0, 119.1, 119.6, 119.4, 117.2, 119.1, 121.2, 120.4, 117.8, 117.0, 117.1, 116.1, 115.7, 115.8, 116.4, 117.5, 117.4, 116.3, 116.3, 116.5, 116.9, 119.0, 120.5, 122.8, 121.4, 0, 122.4,
		121.8, 121.6, 119.4, 119.2, 119.8, 120.3, 121.0, 121.1, 120.5, 120.8, 121.5, 122.5, 124.1, 124.4, 124.2, 123.8, 121.8, 121.1, 120.6, 121.8, 122.0, 123.2, 124.1, 126.0, 124.1, 122.4, 121.7, 120.2, 121.3, 123.9, 122.4, 121.0, 120.1, 118.2, 120.7, 122.6, 125.1, 127.8, 127.9, 125.2, 126.0, 126.0, 125.8, 125.4, 126.3, 124.3, 123.9, 121.6, 119.7, 120.0, 118.7, 120.5,
		120.7, 119.2, 118.3, 119.9, 120.2, 120.7, 120.2, 120.7, 119.7, 118.7, 119.2, 113.4, 97.6, 108.8, 100.2, 104.2, 106.9, 103.0, 101.3, 100.1, 97.1, 99.8, 101.0, 102.6, 100.7, 99.8, 101.1, 101.2, 100.9, 98.7, 94.4, 96.7, 95.1, 94.2, 93.0, 93.9, 94.5, 95.7, 93.4, 93.1, 93.6, 94.7, 95.8, 95.8, 98.6, 102.7, 106.9, 108.6, 105.1, 106.7,
		103.6, 106.0, 106.2, 106.7, 102.7, 100.0, 101.2, 101.5, 100.9, 101.1, 99.8, 97.7, 97.9, 97.4, 94.1, 95.0, 96.0, 92.0, 91.0, 89.7, 90.1, 90.0, 89.6, 90.5, 90.2, 90.7, 92.3, 92.8, 95.6, 98.7, 95.7, 96.8, 96.6, 97.1, 100.0, 98.8, 98.7, 98.7, 98.6, 98.1, 97.0, 95.9, 95.6, 94.4, 94.1, 94.4, 94.2, 92.3, 90.9, 88.8, 89.6, 92.7, 89.1, 91.9,
		90.0, 90.4, 88.9, 88.8, 89.7, 89.6, 88.9, 87.8, 86.4, 85.5, 85.8, 86.2, 86.1, 85.9, 84.9, 85.8, 84.6, 83.2, 84.0, 83.4, 82.5, 83.5, 84.0, 86.6, 88.4, 89.5, 88.9, 88.8, 88.2, 86.9, 86.3, 86.0, 86.3, 84.9, 83.1, 81.4, 79.1, 76.8, 77.4, 78.3, 77.3, 78.4, 77.9, 76.6, 80.2, 81.9, 84.8, 83.4, 83.0, 87.3, 85.6, 89.0,
		87.3, 87.9, 88.9, 89.2, 89.2, 90.0, 92.5, 92.2, 89.9, 88.2, 92.4, 91.1, 91.0, 90.9, 91.5, 91.3, 90.8, 90.8, 91.7, 91.3, 91.7, 92.8, 94.0, 94.2, 94.6, 94.2, 91.7, 90.0, 92.0, 91.7, 91.9, 90.8, 90.1, 89.9, 91.0, 90.8, 90.4, 89.0, 88.6, 86.9, 87.2, 89.1, 89.4, 88.8, 89.2, 90.0, 91.2, 91.1, 91.3, 94.6, 91.7, 94.0, 92.7,
		90.8, 91.3, 92.4, 92.1, 91.0, 91.0, 91.0, 90.0, 88.8, 89.2, 89.1, 89.8, 90.8, 91.5, 90.3, 89.4, 88.6, 88.4, 87.9, 88.5, 88.1, 89.7, 90.4, 91.6, 91.7, 92.0, 91.5, 91.4, 92.5, 91.7, 90.7, 90.5, 90.6, 90.6, 91.5, 90.1, 89.2, 87.6, 85.6, 85.3, 85.8, 86.1, 86.6, 85.9, 85.0, 84.5, 82.2, 83.9, 88.9, 87.8, 87.2, 91.3,
		89.6, 89.2, 90.4, 90.6, 89.6, 88.5, 88.3, 86.5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 98.2, 94.9, 93.6, 92.6, 92.1, 92.7, 92.3, 93.1, 93.2, 92.2, 91.1, 91.3, 92.0, 91.9, 91.2, 91.1, 94.1, 93.1, 92.9, 92.6, 94.8, 93.1, 93.8, 92.6, 92.2, 92.2, 92.5, 94.2, 93.9, 93.6, 91.2, 90.6, 91.3, 89.4, 90.9, 89.7, 90.1, 91.5, 90.6, 91.8, 91.7, 93.8, 97.6, 98.2, 94.5, 97.0,
		94.1, 96.0, 93.7, 91.7, 92.6, 94.3, 92.9, 92.4, 92.3, 91.0, 91.2, 93.1, 93.2, 93.6, 93.3, 91.9, 93.5, 92.1, 92.6, 92.7, 92.7, 94.9, 95.8, 95.4, 96.2, 96.1, 97.2, 96.9, 96.7, 95.7, 95.5, 96.4, 96.1, 96.0, 97.0, 95.1, 93.5, 93.4, 93.0, 92.7, 93.4, 93.8, 94.1, 95.0, 95.6, 96.5, 98.3, 99.3, 101.6, 102.5, 100.4, 103.5,
		101.0, 101.2, 98.6, 98.4, 99.8, 100.4, 100.5, 100.2, 100.8, 100.7, 100.1, 100.2, 100.6, 103.1, 101.7, 103.6, 102.4, 100.8, 101.2, 101.0, 101.8, 103.4, 105.4, 105.8, 106.3, 107.0, 106.8, 107.2, 108.6, 107.6, 108.4, 108.5, 106.6, 105.1, 104.2, 104.7, 104.9, 100.6, 97.5, 96.4, 97.3, 97.2, 94.4, 94.2, 96.0, 95.5, 95.3, 95.3, 95.7, 102.5, 93.7, 95.9, 95.2,
		102.9, 106.7, 104.0, 103.7, 104.5, 103.5, 103.4, 105.0, 104.0, 103.6, 103.8, 104.0, 104.0, 104.8, 106.1, 106.1, 105.5, 105.8, 105.6, 106.0, 104.5, 106.2, 106.1, 105.3, 104.9, 103.2, 99.8, 101.0, 100.0, 99.0, 98.2, 98.9, 98.2, 96.5, 96.8, 95.4, 94.9, 95.7, 93.4, 92.6, 94.0, 95.7, 98.5, 100.7, 103.0, 103.9, 104.3, 104.6, 108.9, 104.7, 105.3, 107.8,
		104.0, 102.6, 100.5, 101.1, 104.7, 104.2, 103.9, 103.6, 105.3, 106.1, 106.1, 107.6, 110.0, 110.3, 110.3, 112.7, 112.2, 110.6, 110.4, 109.7, 111.1, 116.4, 115.7, 115.8, 113.9, 113.5, 113.1, 112.3, 111.4, 107.9, 107.6, 111.1, 113.0, 113.1, 116.3, 118.3, 115.5, 113.2, 112.6, 110.7, 111.4, 113.5, 111.5, 112.0, 115.3, 114.5, 111.7, 110.5, 118.8, 111.3, 114.0, 118.6,
		113.8, 109.6, 108.4, 109.2, 112.7, 113.5, 112.8, 111.7, 110.7, 111.2, 111.6, 113.5, 116.2, 114.5, 114.1, 112.1, 108.7, 108.6, 110.6, 110.3, 109.9, 111.2, 111.7, 111.6, 110.8, 110.8, 110.7, 111.7, 112.8, 113.7, 115.7, 0, 0, 0, 118.0, 116.0, 112.0, 0, 0, 109.8, 112.6, 110.7, 109.9, 108.4, 110.0, 111.9, 112.4, 112.0, 120.3, 113.3, 114.6, 121.8,
		120.7,119.6, 121.1, 122.8, 125.5, 130.2, 130.4, 133.3, 133.9, 133.7, 134.3, 134.6, 138.3, 139.8, 140.2, 142.2, 144.6, 144.3, 145.5, 144.2, 147.7, 148.9, 150.5, 154.4, 157.8, 156.6, 151.5, 149.6, 144.4, 145.9, 147.8, 150.0, 152.2, 149.9, 152.9, 156.1, 152.1, 152.0, 154.9, 154.0, 150.5, 146.0, 145.7, 147.0, 150.7, 152.6, 147.9, 146.9, 156.5, 155.7, 155.1, 153.7,
		158.3, 167.6, 160.2, 153.8, 154.7, 160.3, 
	};
	
	
	// WHEAT Source: https://statistics.defra.gov.uk/esg/publications/amr/feedingstuffs.xls
	public static final double[] wheat_monthly_88_09 = {
		117.61, 118.28, 118.27, 118.20, 115.72, 111.10, 111.48, 106.26, 110.49, 110.10, 114.03, 114.97, 
		121.22, 122.19, 123.64, 127.29, 127.47, 121.48, 118.53, 112.14, 114.45, 116.11, 118.57, 120.38, 
		123.56, 125.47, 124.93, 123.92, 126.30, 129.04, 124.00, 118.27, 114.30, 122.72, 124.06, 127.27, 
		125.73, 127.02, 130.60, 137.37, 131.55, 136.29, 129.57, 111.27, 113.32, 117.73, 120.16, 122.35, 
		115.91, 117.89, 119.42, 119.29, 118.49, 116.03, 116.59, 107.69, 109.39, 113.05, 117.09, 121.95, 
		128.80, 131.84, 133.90, 136.62, 135.37, 126.94, 118.53, 105.43, 100.30, 100.94, 101.44, 101.24, 
		100.48, 100.45, 100.97, 103.64, 106.91, 107.85, 97.04, 100.02, 101.20, 103.80, 104.84, 105.67, 
		107.18, 106.78, 106.63, 109.07, 111.58, 110.83, 105.13, 105.91, 108.66, 113.67, 117.37, 121.00, 
		122.63, 120.35, 118.92, 120.40, 122.98, 118.82, 114.38, 108.91, 103.81, 101.96, 99.75, 98.51, 
		98.51, 96.71, 93.71, 95.18, 96.43, 89.77, 81.79, 87.63, 81.71, 82.93, 82.55, 80.80, 
		79.35, 78.21, 79.58, 75.21, 77.91, 79.15, 76.47, 69.21, 69.08, 72.31, 74.08, 75.90, 
		74.54, 74.45, 73.60, 74.21, 75.97, 76.99, 78.78, 72.00, 69.97, 70.16, 69.94, 70.77, 
		70.77, 70.62, 70.61, 71.45, 71.21, 69.05, 69.09, 60.32, 60.97, 60.32, 60.51, 62.19, 
		65.48, 67.36, 69.48, 70.64, 76.48, 76.56, 75.19, 75.38, 76.71, 76.27, 77.05, 76.75, 
		76.84, 76.23, 72.45, 68.75, 67.46, 63.59, 61.24, 56.12, 55.76, 58.74, 59.60, 59.54, 
		60.35, 59.94, 60.90, 62.28, 64.57, 67.67, 70.06, 72.03, 76.03, 80.98, 82.89, 89.52, 
		91.90, 95.56, 98.08, 97.95, 91.61, 84.09, 77.42, 68.69, 61.67, 61.05, 62.06, 63.62, 
		60.82, 63.40, 66.16, 66.64, 66.49, 66.49, 66.67, 62.94, 62.71, 65.02, 65.08, 65.61, 
		67.06, 68.30, 68.94, 69.93, 71.10, 72.66, 75.00, 71.76, 75.74, 80.98, 83.13, 86.36, 
		89.10, 92.59, 89.89, 90.92, 92.86, 93.31, 93.62, 101.07, 110.60, 108.80, 132.09, 147.03, 
		183.47, 156.75, 151.93, 157.79, 155.10, 152.46, 151.53, 125.95, 112.75, 117.23, 106.60, 113.19, 
		96.60
	};

	// HAY Source: https://statistics.defra.gov.uk/esg/publications/amr/feedingstuffs.xls
	public static final double[] hay_monthly_88_09 = {
		48.65, 47.71, 46.27, 46.23, 45.75, 35.00, 35.00, 40.92, 41.79, 43.11, 47.05, 49.41, 
		47.45, 43.54, 43.43, 43.27, 43.28, 36.63, 42.31, 48.29, 52.71, 58.31, 60.95, 63.58, 
		66.15, 66.20, 67.58, 65.05, 62.35, 57.10, 50.20, 48.86, 52.90, 58.40, 57.70, 59.20, 
		64.30, 64.00, 60.80, 61.10, 61.40, 53.80, 42.80, 37.50, 38.10, 44.40, 45.60, 46.70, 
		45.40, 49.20, 38.80, 36.70, 34.30, 32.70, 30.20, 30.00, 38.00, 41.80, 41.60, 42.30, 
		43.50, 44.60, 43.60, 41.60, 38.90, 39.20, 31.60, 33.70, 37.00, 42.40, 43.50, 43.70, 
		44.40, 48.70, 49.70, 49.40, 44.70, 47.80, 50.00, 57.80, 59.97, 59.35, 63.30, 66.50, 
		55.37, 56.25, 75.75, 74.10, 72.81, 62.65, 71.76, 78.65, 90.83, 102.40, 100.53, 100.08, 
		98.36, 94.72, 91.67, 85.25, 81.55, 68.43, 63.60, 49.44, 52.37, 57.25, 64.15, 69.40, 
		105.10, 102.78, 68.88, 67.07, 65.17, 56.36, 47.55, 41.49, 44.94, 53.30, 57.25, 59.52, 
		61.38, 60.03, 58.55, 57.94, 56.77, 57.50, 43.69, 41.13, 44.45, 50.00, 53.99, 51.62, 
		219.90, 54.14, 53.19, 51.99, 49.45, 31.82, 39.21, 40.20, 42.94, 47.38, 52.06, 52.48, 
		52.48, 54.03, 53.37, 52.59, 52.48, 47.35, 41.46, 40.09, 43.71, 52.55, 59.91, 63.43, 
		67.17, 65.14, 64.99, 72.20, 74.36, 59.76, 50.54, 55.60, 63.44, 72.28, 74.25, 74.12, 
		73.94, 73.83, 72.67, 69.00, 63.84, 58.21, 43.02, 42.46, 44.47, 45.85, 46.42, 46.20, 
		47.63, 46.51, 46.64, 45.38, 44.50, 41.61, 37.53, 37.54, 43.23, 46.31, 47.60, 51.02, 
		53.48, 54.20, 53.83, 52.00, 51.45, 45.16, 43.38, 46.10, 49.70, 52.10, 55.50, 56.80, 
		59.68, 59.33, 58.43, 56.82, 55.65, 49.03, 44.32, 44.40, 44.35, 45.26, 50.05, 51.20, 
		51.30, 50.83, 51.20, 52.78, 53.68, 51.00, 48.02, 49.25, 50.70, 54.90, 57.00, 60.00, 
		61.30, 64.05, 65.05, 64.68, 65.33, 62.44, 60.45, 69.07, 71.76, 71.13, 69.38, 69.17, 
		70.13, 63.75, 63.47, 61.89, 60.56, 60.94, 59.44, 58.39, 60.00, 61.00, 63.00, 66.00, 
		67.04
	};

	// STRAW Source: https://statistics.defra.gov.uk/esg/publications/amr/feedingstuffs.xls
	public static final double[] straw_monthly_88_09 = {
		22.33, 24.33, 25.25, 24.50, 22.88, 21.92, 19.33, 16.56, 16.19, 16.50, 18.12, 19.11, 
		21.34, 21.45, 22.95, 22.10, 22.10, 21.40, 13.50, 14.54, 16.10, 18.88, 21.55, 21.80, 
		23.80, 24.63, 25.53, 24.92, 24.75, 24.30, 23.80, 16.40, 16.60, 19.70, 27.70, 21.40, 
		22.30, 24.00, 25.40, 25.30, 24.50, 22.30, 20.40, 13.30, 14.00, 13.70, 15.40, 16.00, 
		16.40, 15.90, 14.90, 14.60, 14.00, 12.10, 12.40, 12.40, 14.60, 15.60, 19.80, 27.40, 
		32.10, 36.20, 35.20, 33.80, 31.80, 28.00, 22.00, 18.70, 19.30, 25.00, 28.90, 31.00, 
		33.50, 34.90, 35.00, 35.90, 31.80, 31.10, 28.90, 22.00, 20.70, 25.77, 27.40, 30.85, 
		36.56, 31.17, 33.40, 31.90, 28.35, 24.85, 24.85, 24.33, 24.51, 27.56, 26.65, 26.20, 
		26.61, 21.17, 27.05, 26.09, 26.67, 24.81, 23.25, 18.96, 17.15, 17.48, 18.85, 19.60, 
		26.33, 27.18, 19.15, 17.90, 18.23, 17.97, 17.76, 16.19, 16.94, 19.10, 21.98, 22.90, 
		26.00, 27.77, 28.63, 28.99, 30.11, 23.80, 23.58, 17.71, 17.68, 20.01, 22.66, 25.18, 
		27.58, 30.59, 30.44, 28.95, 28.89, 26.78, 24.59, 21.20, 23.59, 26.05, 28.70, 31.68, 
		31.68, 32.01, 31.71, 31.39, 32.30, 31.79, 28.40, 22.71, 20.92, 24.17, 29.90, 33.00, 
		35.14, 35.46, 36.01, 38.27, 36.32, 34.92, 34.5, 30.44, 33.55, 41.14, 46.4, 50.10, 
		52.91, 53.48, 52.89, 50.71, 46.16, 42.74, 39.67, 22.52, 22.37, 22.11, 22.98, 22.93, 
		22.61, 22.69, 27.37, 21.68, 21.60, 21.60, 21.47, 19.44, 20.47, 21.74, 23.23, 24.64, 
		24.80, 24.20, 23.95, 23.76, 24.65, 24.40, 24.32, 23.38, 26.34, 27.46, 30.40, 34.38, 
		36.80, 37.80, 37.58, 37.22, 36.21, 35.29, 32.00, 27.44, 27.70, 28.72, 29.63, 30.23, 
		30.60, 30.20, 31.80, 32.76, 33.28, 33.38, 29.57, 27.63, 28.38, 30.48, 31.15, 33.15, 
		36.43, 37.94, 38.13, 38.03, 38.43, 38.19, 39.41, 34.18, 38.58, 38.66, 39.18, 40.00, 
		40.00, 40.00, 40.29, 42.46, 43.57, 44.35, 40.71, 37.88, 43.50, 44.07, 47.41, 48.96, 
		50.35
	};

	// Concentrates Source: DEFRA
	public static final double[] concentrates_quarterly_06_08 = {
		127, 126, 127, 131,
		134, 138, 147, 165,
		171, 192, 206,
	};
	public static final double[] concentrates_monthly_97_07 = {
		163, 162, 160, 157, 152, 150, 147, 143, 138, 138, 137, 137,
		137, 139, 138, 135, 126, 125, 125, 123, 123, 123, 122, 122,
		122, 123, 123, 121, 120, 121, 120, 120, 120, 122, 122, 122,
		122, 122, 122, 121, 120, 120, 120, 121, 122, 124, 124, 126,
		128, 126, 128, 127, 128, 127, 129, 131, 133, 135, 135, 134,
		135, 135, 135, 133, 129, 128, 127, 129, 125, 126, 123, 126,
		123, 122, 126, 125, 122, 122, 123, 123, 125, 129, 129, 131,
		132, 131, 131, 131, 138, 139, 137, 135, 129, 129, 130, 130,
		130, 130, 129, 128, 126, 125, 124, 124, 125, 126, 126, 126,
		127, 127, 127, 126, 127, 126, 126, 127, 128, 131, 131, 131,
		133, 134, 134, 133, 142, 142
	};
	
	// Grass silage, Source: http://www.dardni.gov.uk/publications-dard-statistics-hayprice
	public static final double[] silage_monthly_99_08 = {
		12.20, 13.33, 13.33, 13.20,
		13.43, 13.14, 12.07, 12.29, 12.17, 11.33, 10.67, 10.00, 9.75, 10.50, 10.60, 12.00,
		11.00, 10.83, 10.71, 11.00, 10.00, 10.00, 10.00, 10.00, 9.57, 9.20, 9.50, 9.80,
		9.80, 10.40, 9.00, 8.10, 7.80, 9.00, 12.43, 13.50, 13.67, 14.60, 13.40, 13.67,
		13.60, 12.20, 11.60, 10.50, 10.50, 10.67, 11.38, 10.43, 10.50, 10.50, 10.67, 10.50,
		10.33, 11.00, 10.50, 9.80, 9.50, 10.60, 10.80, 10.38, 10.40, 10.00, 10.10, 10.30,
		10.30, 9.80, 9.80, 10.00, 8.75, 9.20, 10.00, 10.67, 11.50, 9.67, 10.00, 10.40,
		10.20, 11.00, 11.80, 12.00, 12.20, 11.67, 11.50, 11.60, 11.20, 11.00, 12.20, 11.83,
		13.67, 14.80, 13.33, 13.00, 10.40, 11.40, 12.86, 12.67, 12.33, 12.50, 12.33, 11.50,
		12.00, 11.20, 12.00, 11.33, 11.60, 16.33, 16.33, 12.60, 13.67, 15.00, 17.00, 18.40,
	};
	
	public static final double[] milk_price_2008 = {
		27.27, 26.60, 25.81, 25.63, 25.73, 24.95, 24.49, 25.00, 25.82, 26.29, 27.00, 27.36, 27.08,
	};
	public static final double[] milk_price_monthly_70_08 = {
		// 1970
		4.08, 4.09, 4.07, 3.73, 2.90, 2.86, 3.31, 3.71, 3.92, 4.24, 4.23, 4.38,
		4.35, 4.39, 4.43, 4.10, 3.22, 3.26, 3.73, 4.14, 4.50, 4.56, 4.74, 4.74,
		4.74, 4.82, 4.72, 4.46, 3.59, 3.57, 3.91, 4.30, 4.65, 4.67, 4.74, 4.76,
		4.78, 4.81, 4.75, 4.66, 3.80, 3.81, 4.21, 4.66, 5.08, 5.15, 6.46, 6.46,
		6.53, 6.46, 5.62, 5.50, 4.65, 4.67, 5.05, 5.40, 5.82, 7.61, 7.69, 7.69,
		7.79, 7.76, 7.62, 7.35, 6.47, 6.47, 6.84, 7.27, 8.85, 8.61, 8.69, 8.74,
		8.80, 8.76, 8.63, 9.06, 8.19, 8.16, 8.59, 9.05, 9.82, 9.90, 9.98, 10.03,
		10.15, 10.10, 10.05, 9.80, 8.97, 8.97, 9.35, 9.78, 10.13, 10.70, 10.78, 10.45,
		10.43, 10.25, 10.12, 10.15, 9.44, 9.45, 9.79, 10.22, 10.59, 11.16, 11.26, 11.24,
		11.29, 11.25, 11.12, 11.05, 10.19, 10.17, 10.55, 11.00, 11.35, 11.91, 12.23, 12.26,
		// 1980
		12.41, 12.92, 12.80, 12.47, 11.56, 11.55, 11.93, 12.37, 12.72, 13.29, 13.40, 13.44,
		13.45, 13.41, 13.28, 13.43, 12.60, 12.59, 12.97, 13.40, 13.75, 14.32, 14.42, 14.46,
		14.48, 14.43, 14.30, 14.41, 13.76, 13.58, 13.96, 14.38, 14.73, 15.44, 15.50, 15.54,
		15.52, 15.56, 15.38, 14.28, 13.45, 13.45, 13.74, 14.17, 14.62, 15.27, 15.41, 15.45,
		15.48, 15.50, 15.31, 13.80, 12.17, 12.33, 14.27, 15.41, 15.94, 15.80, 15.48, 15.32,
		15.36, 15.36, 14.98, 14.28, 12.71, 12.87, 14.89, 15.22, 16.46, 16.23, 16.19, 15.76,
		15.73, 15.47, 15.04, 14.88, 13.28, 13.42, 15.53, 16.84, 17.21, 16.97, 16.78, 16.48,
		16.45, 16.33, 16.05, 15.35, 13.89, 14.13, 16.22, 17.33, 17.74, 17.45, 17.06, 16.97,
		16.75, 16.72, 16.52, 16.68, 15.05, 15.60, 18.16, 19.45, 19.28, 18.53, 18.31, 17.81,
		17.58, 17.61, 17.71, 16.44, 14.91, 15.65, 20.74, 22.16, 21.89, 21.44, 18.17, 18.24,
		//1990
		18.30, 18.12, 17.91, 16.68, 14.89, 15.57, 20.83, 21.98, 21.76, 21.20, 18.18, 18.13,
		18.98, 18.95, 18.43, 18.04, 16.22, 16.89, 22.45, 23.99, 23.59, 23.45, 19.98, 19.77,
		19.54, 19.52, 19.30, 18.98, 17.08, 17.54, 23.49, 25.15, 25.04, 24.61, 21.12, 20.99,
		20.69, 20.40, 20.21, 20.00, 18.10, 18.54, 24.54, 26.21, 26.05, 25.60, 21.92, 21.53,
		21.04, 20.79, 20.61, 19.31, 18.92, 20.17, 24.06, 24.27, 24.22, 23.39, 24.99, 24.74,
		24.41, 24.20, 24.32, 22.91, 22.53, 24.01, 26.45, 26.48, 26.52, 26.77, 25.57, 25.49,
		25.01, 25.03, 25.34, 23.63, 22.73, 23.89, 26.75, 26.94, 26.67, 25.70, 24.70, 24.49,
		24.16, 23.79, 23.74, 20.70, 19.76, 20.91, 24.08, 23.89, 22.99, 21.03, 20.60, 20.30,
		20.17, 19.99, 19.90, 17.66, 16.69, 17.74, 20.74, 20.87, 20.06, 20.22, 19.68, 19.30,
		19.31, 19.03, 18.98, 17.56, 16.42, 17.23, 19.79, 19.83, 19.17, 18.12, 17.69, 17.36,
		// 2000
		16.79, 16.62, 16.64, 15.27, 14.63, 15.35, 17.64, 17.93, 17.52, 18.81, 18.60, 18.26,
		18.32, 18.18, 17.88, 18.45, 17.89, 18.70, 20.45, 20.86, 20.70, 20.46, 20.05, 19.60,
		18.79, 18.31, 18.01, 15.99, 14.88, 15.37, 16.58, 17.00, 17.13, 18.15, 18.11, 17.75,
		17.69, 17.66, 17.39, 16.85, 16.00, 16.55, 18.22, 18.96, 19.26, 19.60, 19.83, 19.16,
		18.77, 18.70, 18.48, 17.72, 16.95, 17.30, 18.28, 18.51, 19.49, 19.53, 19.50, 18.94,
		18.54, 18.34, 18.33, 17.59, 17.12, 17.31, 18.06, 18.65, 19.47, 19.71, 19.77, 19.27,
		18.66, 18.35, 18.12, 17.36, 16.83, 16.84, 17.20, 17.61, 18.47, 18.84, 19.01, 18.59,
		18.04, 17.82, 17.69, 17.57, 17.55, 18.09, 19.66, 20.72, 23.16, 26.53, 27.29, 26.61,
		25.82, 25.64, 25.75, 24.97, 24.51, 25.01, 25.83, 26.31, 27.02, 27.38, 27.10, 26.38,
	};

	// Calibrated parameters for rainfall data
	private static final double[][] RAIN07 = {
		{-0.3729, 4.0128, 2.2948, 3.0012},
		{0.9734572007080129, 0.5539750482385839, 4.370285613695183, 0.7733214397173217, 0.22247738264122957}
	};
	public static final double[] rainfall_edinburgh_2007 = {
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

	// Calibrated parametrs for avg temp data
	private static final double[][] TEMP07 = {
		{0.18388892, -4.4521296, 9.0690876},
		{0.5723173575548485, 5.427710792192619}
	};
	// Actual mean temperature data  °C
	public static final double[] temperature_edinburgh_2007 = {
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
	
	// Calibrated parametrs for min temp data
	private static final double[][] TMIN07 = {
		{0.18672707, -4.1946884, 6.253944},
		{0.5472114739300266, 6.108503931651386}
	};
	// Actual mean temperature data  °C
	public static final double[] mintemp_edinburgh_2007 = {
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
	

	// Calibrated parametrs for radiation data
	private static final double[][] RAD07 = {
		{-0.21667269778, 5.0787158686, 6.2370843},
		{3.207199968993312, 0.38062002785468374}
	};
	// Actual solar radiation data
	public static final double[] solar_rad_edinburgh_2007 = {
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

}
