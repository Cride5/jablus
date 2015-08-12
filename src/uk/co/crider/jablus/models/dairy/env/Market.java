package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.TextData;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.utils.Utils;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import uk.co.crider.models.dairy.RandomGenerator;

/** Encapsulates all economics-based behaviour */
public class Market extends CompoundData implements DriverDynamic, DriverEndogeneous{

	
	public static final int OFFSET             = 1000;
	public static final int PLOUGH             = OFFSET +  0;
	public static final int FERTILISE          = OFFSET +  1;
	public static final int SPREAD             = OFFSET +  2;
	public static final int GRASS_SOW          = OFFSET +  3;
	public static final int GRASS_CUT          = OFFSET +  4;
	public static final int GRASS_HARVEST      = OFFSET +  5;
	public static final int WHEAT_SOW          = OFFSET +  6;
	public static final int WHEAT_HARVEST      = OFFSET +  7;
	public static final int MAIZE_SOW          = OFFSET +  8;
	public static final int MAIZE_HARVEST      = OFFSET +  9;
	public static final int MILK               = OFFSET + 10;
	public static final int LVS_OVERHEADS      = OFFSET + 11;
	public static final int INSEMINATION       = OFFSET + 12;
	public static final int VETINARY_MEDS      = OFFSET + 13;
	public static final int CARCAS_REMOVAL     = OFFSET + 14;
	public static final int INTEREST           = OFFSET + 15;
	public static final int FARM_RENT          = OFFSET + 16;
	public static final int SFP                = OFFSET + 17;
	public static final int STORAGE_LIVESTOCK  = OFFSET + 18;
	public static final int STORAGE_SILAGE     = OFFSET + 19;
	public static final int STORAGE_BARN       = OFFSET + 20;
	public static final int STORAGE_SLURRY     = OFFSET + 21;
	public static final int FERTILISER         = OFFSET + 22;
	
	
	public static final int OFFSET_FEEDS       = OFFSET + 23;
	public static final int CONCENTRATES       = OFFSET + 23;
	public static final int HAY                = OFFSET + 24;
	public static final int STRAW              = OFFSET + 25;
	public static final int SILAGE_GRASS       = OFFSET + 26;
	public static final int SILAGE_WHEAT       = OFFSET + 27;
	public static final int SILAGE_MAIZE       = OFFSET + 28;
	
	public static final int OFFSET_LIVESTOCK   = OFFSET + 29;
	public static final int HEIFER_CALVES      = OFFSET + 29;
	public static final int CALVED_HEIFERS     = OFFSET + 30;
	public static final int FINISHED_HEIFERS   = OFFSET + 31;
	public static final int CULL_COWS          = OFFSET + 32;
	public static final int BULL_CALVES        = OFFSET + 33;

	
	public static final int NUM_PRODUCTS = 34;
	
	private static final int[] CAN_BUY = {
		PLOUGH,
		FERTILISE,
		SPREAD,
		GRASS_SOW,
		GRASS_CUT,
		GRASS_HARVEST,
		WHEAT_SOW,
		WHEAT_HARVEST,
		MAIZE_SOW,
		MAIZE_HARVEST,
		LVS_OVERHEADS,
		INSEMINATION,
		VETINARY_MEDS,
		CARCAS_REMOVAL,
		INTEREST,
		FARM_RENT,
		FERTILISER,
		CONCENTRATES,
		HAY,
//		STRAW,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE,
		HEIFER_CALVES,
		CALVED_HEIFERS,
		STORAGE_LIVESTOCK,
		STORAGE_SILAGE,
		STORAGE_BARN,
		STORAGE_SLURRY
	};
	
	private static final int[] CAN_SELL = {
		SFP,
		MILK,
		FERTILISER,
		CONCENTRATES,
		HAY,
//		STRAW,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE,
		HEIFER_CALVES,
		CALVED_HEIFERS,
		FINISHED_HEIFERS,
		CULL_COWS,
		BULL_CALVES,
	};
	
	public static final int[] TRADABLE_PRODUCTS = {
		CONCENTRATES,
		HAY,
//		STRAW,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE,
		HEIFER_CALVES ,
		CALVED_HEIFERS,
		FINISHED_HEIFERS,
		CULL_COWS,
		BULL_CALVES,
	};
	
	public static final int[] SALE_PRODUCTS = {
		FERTILISER,
		CONCENTRATES,
		HAY,
//		STRAW,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE,
		HEIFER_CALVES,
		CALVED_HEIFERS,
		FINISHED_HEIFERS,
		CULL_COWS
	};
	
	public static final int[] PURCHASE_PRODUCTS = {
		FERTILISER,
		CONCENTRATES,
		HAY,
//		STRAW,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE,
		HEIFER_CALVES,
		CALVED_HEIFERS,
		STORAGE_LIVESTOCK,
		STORAGE_SILAGE,
		STORAGE_BARN,
		STORAGE_SLURRY
	};
	
	public static final int[] FEED_PRODUCTS = {
		CONCENTRATES,
		HAY,
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE
	};
	
	public static final int[] GROWN_FEEDS = {
		SILAGE_GRASS,
		SILAGE_WHEAT,
		SILAGE_MAIZE
	};
	
	public static final int[] LIVESTOCK_PRODUCTS = {
		BULL_CALVES,		
		HEIFER_CALVES ,
		FINISHED_HEIFERS,
		CALVED_HEIFERS,
		CULL_COWS,
	};
	

	public static boolean canBuy(int id){
		return Utils.contains(CAN_BUY, id);
	}
	

	public static boolean canSell(int id){
		return Utils.contains(CAN_SELL, id);
	}
	
	public static boolean isTradeLivestock(int id){
		return id >= HEIFER_CALVES && id <= BULL_CALVES;
	}
	
	public static boolean isStorageBuilding(int id){
		return id >= STORAGE_LIVESTOCK && id <= STORAGE_SLURRY;
	}
	
	// Units
	private static final Hashtable<Integer, String> UNITS = new Hashtable<Integer, String>();
	static{
		for(int i = 0; i < Livestock.HERD_GROUPS.length; i++){
			UNITS.put(Livestock.HERD_GROUPS[i]          , Constants.UNITS_COWS);
		}
		UNITS.put(Livestock.STEERS  , Constants.UNITS_COWS);
//		UNITS.put(Livestock.BULLS   , "");
		UNITS.put(FINISHED_HEIFERS  , Constants.UNITS_COWS);
		UNITS.put(CULL_COWS         , Constants.UNITS_COWS);
		UNITS.put(BULL_CALVES       , Constants.UNITS_COWS);
		UNITS.put(HEIFER_CALVES     , Constants.UNITS_COWS);
		UNITS.put(CALVED_HEIFERS    , Constants.UNITS_COWS);
		UNITS.put(CARCAS_REMOVAL    , Constants.UNITS_COWS); // (£/head) Based on conversation with Desere TODO: Check
		UNITS.put(VETINARY_MEDS     , Constants.UNITS_COWS); // (£/head/block) TODO - Check - taken from farm management handbook
		UNITS.put(INSEMINATION      , Constants.UNITS_COWS); // (£/head/block) TODO - Check - taken from farm management handbook
		UNITS.put(MILK              , Constants.UNITS_VOLUME_LOW); // (£/litre) - Data from farmer's weekly
		UNITS.put(LVS_OVERHEADS     , Constants.UNITS_COWS); // TODO - Check
		UNITS.put(CONCENTRATES      , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - taken from farm management handbook
		UNITS.put(HAY               , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - estimated from internet
		UNITS.put(STRAW             , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - estimated from internet
		UNITS.put(SILAGE_GRASS      , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - estimated from internet
		UNITS.put(SILAGE_WHEAT      , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - estimated from internet
		UNITS.put(SILAGE_MAIZE      , Constants.UNITS_WEIGHTFW_HIGH); // (£/t) TODO - Check - estimated from internet
		UNITS.put(FERTILISER        , Constants.UNITS_WEIGHT_HIGH); // TODO - Check
		UNITS.put(STORAGE_LIVESTOCK , Constants.UNITS_COWS);
		UNITS.put(STORAGE_SILAGE    , Constants.UNITS_VOLUME_HIGH);
		UNITS.put(STORAGE_BARN      , Constants.UNITS_VOLUME_HIGH);
		UNITS.put(STORAGE_SLURRY    , Constants.UNITS_VOLUME_HIGH);
//		UNITS.put(SEEDS             , Constants.UNITS_WEIGHT_HIGH); // TODO - Check
//		UNITS.put(LIME              , Constants.UNITS_WEIGHT_HIGH); // TODO - Check
		UNITS.put(PLOUGH            , Constants.UNITS_AREA_HIGH);
		UNITS.put(FERTILISE         , Constants.UNITS_AREA_HIGH);
		UNITS.put(SPREAD            , Constants.UNITS_AREA_HIGH);
		UNITS.put(GRASS_SOW         , Constants.UNITS_AREA_HIGH);
		UNITS.put(GRASS_CUT         , Constants.UNITS_AREA_HIGH);
		UNITS.put(GRASS_HARVEST     , Constants.UNITS_AREA_HIGH);
		UNITS.put(WHEAT_SOW         , Constants.UNITS_AREA_HIGH);
		UNITS.put(WHEAT_HARVEST     , Constants.UNITS_AREA_HIGH);
		UNITS.put(MAIZE_SOW         , Constants.UNITS_AREA_HIGH);
		UNITS.put(MAIZE_HARVEST     , Constants.UNITS_AREA_HIGH);

		UNITS.put(FARM_RENT         , Constants.UNITS_WEEK); // (£/block) Based on farm found on farmproperty.net, TODO - Check
		UNITS.put(SFP               , Constants.UNITS_AREA_HIGH); // (£/ha) http://www.sac.ac.uk/learning/geography/agriculture/thecap/capkeydates
		UNITS.put(INTEREST          , Constants.UNITS_WEEK); // (£/block) Based on farm found on farmproperty.net, TODO - Check
	}
	
	public static String getUnits(int product){
		return UNITS.get(product);
	}
	
	// Prices
	private PriceTrend[] prices;
	private List<PriceTrend> weeklyPrices;
	private List<PriceTrend> monthlyPrices;
	
	// STATIC INTERNAL VARIABLES
	private Random rand;
	private Parameters params;
		
	// EXTERNAL DRIVERS
//	private Livestock livestock;
//	private Crops crops;
	private Time time;
	
	// DYNAMIC INTERNAL VARIABLES 
	private DoubleData costs;
	private DoubleData revenue;
	private DoubleData netProfit;
	private DoubleData balance;
	private DoubleData value;
	private DoubleData growth;
	private TextData transactions;
	private int lastMonth;

	// OUTPUT VARIABLES	
	private List<Object[]> trans;
//	private List<Object[]> soldItems;	
	private StringBuffer bought = new StringBuffer();
	private StringBuffer sold = new StringBuffer();
	
//    private DoubleData[] qtyBought;
//    public DoubleData[] getQtyBought(){ return qtyBought; }
//    private DoubleData[] qtySold;
//    public DoubleData[] getQtySold(){ return qtySold; }
//    private DoubleData[] valBought;
//    public DoubleData[] getValBought(){ return valBought; }
//    private DoubleData[] valSold;
//    public DoubleData[] getValSold(){ return valSold; }
//    private DoubleData valCulled;
//    public DoubleData getValCulled(){ return valCulled; }

    
    private int iTR = 0; // Index into the transaction record ringbuffers
    private int nTR = 1; // number of transaction record entries set. For the purposes of initialisation 

    /** Rolling purchases record */
    private double[][] buyRecord;
    
    /** Value of all products bought each week */
//    private double[] buyValRecord;
    
    /** Rolling sales record */
    private double[][] sellRecord;
    
    /** Value of all products sold each week */
//    private double[] sellValRecord;
    
    /** Rolling price record */
    private double[][] priceRecord;
    
    /** Rolling average price record */
    private double[][] avgPriceRecord;
 
    // Variable price of milk by block
/*	private static final double[] MILK_PRICE_MONTHLY = new double[]{
		16.50, // Jan
		16.20, // Feb 
		16.30, // Mar
		15.60, // Apr
		15.50, // May
		15.80, // Jun
		15.90, // Jul
		16.20, // Aug
		16.70, // Sep
		17.30, // Oct
		18.00, // Nov
		16.40, // Dec
	};
*/
	public Market(Parameters params){
		super(Constants.DRIVER_ECONOMICS);
		this.params = params;
		rand = new Random(params.MARKET_RANDOM_SEED);
		costs = new DoubleData(Constants.DERIVED_ECONOMIC_COSTS, Constants.UNITS_CURRENCY);
		addItem(costs);
		revenue = new DoubleData(Constants.DERIVED_ECONOMIC_REVENUE, Constants.UNITS_CURRENCY);
		addItem(revenue);
		netProfit = new DoubleData(Constants.DERIVED_ECONOMIC_PROFIT, Constants.UNITS_CURRENCY);
		addItem(netProfit);
		balance = new DoubleData(Constants.DERIVED_ECONOMIC_BALANCE, params.INIT_BALANCE, Constants.UNITS_CURRENCY);
		addItem(balance);
		value = new DoubleData(Constants.DERIVED_ECONOMIC_VALUE, Constants.UNITS_CURRENCY);
		addItem(value);
		growth = new DoubleData(Constants.DERIVED_ECONOMIC_GROWTH, Constants.UNITS_CURRENCY);
		addItem(growth);
		transactions = new TextData(Constants.DERIVED_ECONOMIC_TRANS);
		addItem(transactions);
		lastMonth = -1;
		
/*	    qtyBought = new DoubleData[NUM_PRODUCTS];
	    qtySold   = new DoubleData[NUM_PRODUCTS];
	    valBought = new DoubleData[NUM_PRODUCTS];
	    valSold   = new DoubleData[NUM_PRODUCTS];
	    for(int i = 0; i < NUM_PRODUCTS; i++){
		    qtyBought[i] = new DoubleData(0);
		    valBought[i] = new DoubleData(0);
		    qtySold[i]   = new DoubleData(0);
		    valSold[i]   = new DoubleData(0);
	    }
	    valCulled = new DoubleData(0);
*/	    
	    buyRecord = new double[NUM_PRODUCTS][Time.WEEKS_YEAR];
//	    buyValRecord = new double[Time.WEEKS_YEAR];
	    sellRecord = new double[NUM_PRODUCTS][Time.WEEKS_YEAR];
//	    sellValRecord = new double[Time.WEEKS_YEAR];
	    priceRecord = new double[NUM_PRODUCTS][];
	    avgPriceRecord = new double[NUM_PRODUCTS][];
	    iTR = 0;

	    trans = new LinkedList<Object[]>();
//		soldItems = new LinkedList<Object[]>();

		prices = new PriceTrend[]{
				new PriceTrend(PLOUGH             , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(FERTILISE          , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(SPREAD             , 0, Constants.UNITS_AREA_HIGH), // Assuming spread at 3 ha per hour (£21 per hour)
				new PriceTrend(GRASS_SOW          , 0, Constants.UNITS_AREA_HIGH), // Seeds  £12, Sprays £11, Sowing £17.80, Spraying £9 (see: farm management handbook)
				new PriceTrend(GRASS_CUT          , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(GRASS_HARVEST      , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(WHEAT_SOW          , 0, Constants.UNITS_AREA_HIGH), // Seeds  £63, Sprays £92, Sowing £18.60, Spraying £9  (see: farm management handbook)
				new PriceTrend(WHEAT_HARVEST      , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(MAIZE_SOW          , 0, Constants.UNITS_AREA_HIGH), // Seeds £128, Sprays £53, Sowing £40.00, Spraying £9   (see: farm management handbook)
				new PriceTrend(MAIZE_HARVEST      , 0, Constants.UNITS_AREA_HIGH), // see: farm management handbook
				new PriceTrend(MILK               , 0, Constants.UNITS_VOLUME_LOW,  new RandomGenerator(RandomGenerator.MILK_PRICE_MONTHLY, rand.nextLong()), 0.01), // (£/litre) - Data from farmer's weekly
				new PriceTrend(LVS_OVERHEADS      , 0, Constants.UNITS_COWS             ), // (£/head/week) from farme management handbook
				new PriceTrend(INSEMINATION       , 0, Constants.UNITS_COWS             ), // (£/head/week), in reality done once every three weeks, costs about £15 per attmept, see http://www.rbst.org.uk/stock-exchange/artificial-insemination
				new PriceTrend(VETINARY_MEDS      , 0, Constants.UNITS_COWS             ), // (£/head/week) TODO - Check - taken from farm management handbook
				new PriceTrend(CARCAS_REMOVAL     , 0, Constants.UNITS_COWS             ), // (£/head) Based on conversation with Desere TODO: Check
				new PriceTrend(INTEREST           , 0, ""), // Equivalent to about 11% APR
				new PriceTrend(FARM_RENT          , 0, ""), // (£/week) Based on farm found on farmproperty.net, new PriceTrend(, TODO - Check
				new PriceTrend(SFP                , 0, Constants.UNITS_AREA_HIGH ), // £230 / 52
				new PriceTrend(FERTILISER         , 0, Constants.UNITS_WEIGHT_HIGH      ), // (£/t) TODO - Check
				new PriceTrend(STORAGE_LIVESTOCK  , 0, Constants.UNITS_COWS), // (£/head)
				new PriceTrend(STORAGE_SILAGE     , 0, Constants.UNITS_VOLUME_HIGH), // (£/m³)
				new PriceTrend(STORAGE_BARN       , 0, Constants.UNITS_VOLUME_HIGH), // (£/m³)
				new PriceTrend(STORAGE_SLURRY     , 0, Constants.UNITS_VOLUME_HIGH), // (£/m³)
				new PriceTrend(CONCENTRATES       , 0, Constants.UNITS_WEIGHTFW_HIGH, new RandomGenerator(RandomGenerator.CONCENTRATES_MONTHLY, rand.nextLong())), // (£/t) TODO - Check - taken from farm management handbook
				new PriceTrend(HAY                , 0, Constants.UNITS_WEIGHTFW_HIGH, new RandomGenerator(RandomGenerator.HAY_MONTHLY, rand.nextLong())), // (£/t) TODO - Check - estimated from internet
				new PriceTrend(STRAW              , 0, Constants.UNITS_WEIGHTFW_HIGH      ), // (£/t)
				new PriceTrend(SILAGE_GRASS       , 0, Constants.UNITS_WEIGHTFW_HIGH, new RandomGenerator(RandomGenerator.SILAGE_MONTHLY, rand.nextLong())), // (£/t) TODO - Check - estimated from internet
				new PriceTrend(SILAGE_WHEAT       , 0, Constants.UNITS_WEIGHTFW_HIGH, new RandomGenerator(RandomGenerator.WHEAT_MONTHLY, rand.nextLong())), // (£/t) TODO - Check - estimated from internet
				new PriceTrend(SILAGE_MAIZE       , 0, Constants.UNITS_WEIGHTFW_HIGH, new RandomGenerator(RandomGenerator.HEIFER_CALF_WEEKLY, rand.nextLong())), // (£/t) TODO - Check - estimated from internet
				new PriceTrend(HEIFER_CALVES      , 0, Constants.UNITS_COWS, new RandomGenerator(RandomGenerator.HEIFER_CALF_WEEKLY    , rand.nextLong())), // (£/head) Based on weekly average price @ farmer's weekly (11/09/08)
				new PriceTrend(CALVED_HEIFERS     , 0, Constants.UNITS_COWS, new RandomGenerator(RandomGenerator.CALFED_HEIFERS_WEEKLY , rand.nextLong())), // (£/head) Based on weekly average price @ farmer's weekly (11/09/08)
				new PriceTrend(FINISHED_HEIFERS   , 0, Constants.UNITS_COWS, new RandomGenerator(RandomGenerator.FINISHED_HEIFER_WEEKLY, rand.nextLong())), // (£/head)  "
				new PriceTrend(CULL_COWS          , 0, Constants.UNITS_COWS, new RandomGenerator(RandomGenerator.CULL_COW_WEEKLY       , rand.nextLong())), // (£/head)  "
				new PriceTrend(BULL_CALVES        , 0, Constants.UNITS_COWS, new RandomGenerator(RandomGenerator.BULL_CALF_WEEKLY      , rand.nextLong())), // (£/head) Based on weekly average price @ farmer's weekly (11/09/08)
		};
		
		if(params.INIT_PRICES != null){
			for(int i = 0; i < params.INIT_PRICES.length && i < prices.length; i++){
//				System.out.println("Setting price " + params.INIT_PRICES[i]);
				prices[i].setValue(params.INIT_PRICES[i]);
			}
		}
		
		
		weeklyPrices = new LinkedList<PriceTrend>();
		weeklyPrices.add(getPriceTrend(FINISHED_HEIFERS));
		weeklyPrices.add(getPriceTrend(CULL_COWS       ));
		weeklyPrices.add(getPriceTrend(BULL_CALVES     ));
		weeklyPrices.add(getPriceTrend(HEIFER_CALVES   ));
		weeklyPrices.add(getPriceTrend(CALVED_HEIFERS  ));
		
		monthlyPrices = new LinkedList<PriceTrend>();
		monthlyPrices.add(getPriceTrend(MILK        ));
		monthlyPrices.add(getPriceTrend(CONCENTRATES));
		monthlyPrices.add(getPriceTrend(HAY         ));
		monthlyPrices.add(getPriceTrend(STRAW       ));
		monthlyPrices.add(getPriceTrend(SILAGE_WHEAT));
		monthlyPrices.add(getPriceTrend(SILAGE_GRASS));
		
		// Initialise price record
		for(int i = 0; i < priceRecord.length; i++){
			// Only initialise a price record for changing prices
			if(prices[i].getRand() != null){
				priceRecord[i] = new double[Time.WEEKS_YEAR];
				avgPriceRecord[i] = new double[Time.WEEKS_YEAR];
			}
		}
		
//		System.out.println("params st = " + params.STATIC_MARKET_PRICES);
		// Change generators according to params
		if(params.STATIC_MARKET_PRICES != null){
//			for(int i = 0; i < params.STATIC_MARKET_PRICES.length; i++){
//System.out.println("Setting params");
//				getPriceTrend(params.STATIC_MARKET_PRICES[i]).getRand().setParam(0, 2, 1);
//				getPriceTrend(params.STATIC_MARKET_PRICES[i]).getRand().setParam(2, 1, 0);
//				getPriceTrend(params.STATIC_MARKET_PRICES[i]).getRand().setParam(2, 2, 0);
//			}
			for(int i = 0; i < params.STATIC_MARKET_PRICES.length; i++)
				getPriceTrend(params.STATIC_MARKET_PRICES[i]).setStatic();
		}
	
		// Add all price items
		for(PriceTrend p : prices) addItem(p);
						
	}	
	
	/** Returns the current price of the given product */
	public double getPrice(int id){
		return getPriceTrend(id).doubleValue();
	}
	
	/** Returns price trend object for given product */
	public PriceTrend getPriceTrend(int id){
		return prices[id - OFFSET];
	}

	/** Returns the average price calculated over the last 52 weeks */
    public double getAvgPrice(int id){
    	return getAvgPrice(id, Time.WEEKS_YEAR);
    }
    
    /** Returns the rolling average price record for the last given number of weeks */
    public double getAvgPrice(int id, int period){
    	if(priceRecord[id - OFFSET] == null) return getPrice(id);
    	if(period > nTR) period = nTR;
//System.out.println("Getting average price for i=" + iTR + " n=" + period + " \n" + Utils.arrayString(priceRecord[id - OFFSET]));
    	return period <= 0 ? 0 : Utils.sum(priceRecord[id - OFFSET], iTR, period) / period;
    }
    
    public double getAvgPriceHistoric(int id, int period){
    	if(priceRecord[id - OFFSET] == null) return getPrice(id);
    	if(period > nTR) period = nTR - 1;
    	int iHR = iTR - period;
    	iHR = iHR < 0 ? iHR + Time.WEEKS_YEAR : iHR;
    	return avgPriceRecord[id - OFFSET][iHR];
    }
    
    /** Returns the quantity bought over the last year */
    public double getQtyBought(int id){
    	return getQtyBought(id, Time.WEEKS_YEAR);
    }
    /** Returns the quantity bought over the given period */
    public double getQtyBought(int id, int period){
    	if(period > nTR) period = nTR;
    	return Utils.sum(buyRecord[id - OFFSET], iTR, period);
    }
    /** Returns the total value of goods bought over the last year */
    public double getValBought(int id){
    	return getValBought(id, Time.WEEKS_YEAR);
    }
    /** Returns the total value of goods bought over the given period */
    public double getValBought(int id, int period){
//System.out.println("Getting val bought:" + Constants.getName(id) + ", price=" + getPrice(id));
    	if(period > nTR) period = nTR;
    	if(prices[id - OFFSET].isStatic())
    		return getQtyBought(id, period) * getPrice(id);
    	return Utils.sumProd(priceRecord[id - OFFSET], buyRecord[id - OFFSET], iTR, period);
//    	return getTotal(buyValRecord, iTR, period);
    }
    
    /** Returns the quantity sold over the last year */
    public double getQtySold(int id){
    	return getQtySold(id, Time.WEEKS_YEAR);
    }
    /** Returns the quantity sold over the given period */
    public double getQtySold(int id, int period){
    	if(period > nTR) period = nTR;
    	return Utils.sum(sellRecord[id - OFFSET], iTR, period);
    }
    /** Returns the total value of goods sold over the last year */
    public double getValSold(int id){
    	return getValSold(id, Time.WEEKS_YEAR);
    }
    /** Returns the total value of goods sold over the given period */
    public double getValSold(int id, int period){
//System.out.println("Getting val sold: " + Constants.getName(id));
       	if(period > nTR) period = nTR;
    	if(prices[id - OFFSET].isStatic())
    		return getQtySold(id, period) * getPrice(id);
    	return Utils.sumProd(priceRecord[id - OFFSET], sellRecord[id - OFFSET], iTR, period);
    }
   

   
    /** Returns the value of goods */
    
    		
	public void init(DataSet drivers){
		boolean first = time == null;
		time = (Time)drivers.getItem(Constants.TEMPORAL_TIME);
//		livestock = (Livestock)drivers.getItem(Constants.DRIVER_LIVESTOCK);	
//		crops = (Crops)drivers.getItem(Constants.DRIVER_CROPS);
		
		// Set initial prices
		if(first) updatePrices();
    }

	/** @inheritDoc */
	public void initStep() {
	    costs.setValue(0);
	    revenue.setValue(0);
	    netProfit.setValue(0);
	    bought.delete(0, bought.length());
	    sold.delete(0, bought.length());
//System.out.println("Market: clearingTransactionTable");
		trans.clear();
//		buyValRecord[iTR]  = 0;
//		sellValRecord[iTR] = 0;
//		soldItems.clear();
		
	}

	/** @inheritDoc */
	public void execStep(){
//System.out.println("Market:execStep");
		// Charge interest in any debt
		if(balance.doubleValue() < 0){
//System.out.println("Charging interest");
			buy(INTEREST, -balance.doubleValue());
		}

		if(params.LOGGING_MARKET)
			System.out.println(getLog());
		
//System.out.println(Utils.arrayString(valBought));
		// Incrament ringbuffer pointer
		iTR = (iTR + 1) % Time.WEEKS_YEAR;
		if(nTR < Time.WEEKS_YEAR) nTR++;
//System.out.println("UPDATED TRANSACTION RECROD POINTER - " + iTR);

//System.out.println(Utils.arrayString(buyRecord));
//System.out.println("Resetting next years buy record");
		for(int i = 0; i < NUM_PRODUCTS; i++){
			buyRecord[i][iTR]  = 0;
			sellRecord[i][iTR] = 0;
		}
		updatePrices();
//System.out.println(Utils.arrayString(buyRecord));
//System.out.println("Val hay bought: " + getValBought(Market.HAY));
	}
	
	private void updatePrices(){
		// Update prices
		for(PriceTrend p : weeklyPrices)
			p.next();
		if(time.getMonth() > lastMonth){
			lastMonth = time.getMonth();
			for(PriceTrend p : monthlyPrices)
				p.next();
		}
//		System.out.println("Updating price recrods ");
		// Record this year's prices
		for(int i = 0; i < NUM_PRODUCTS; i++){
			int id = i + OFFSET;
			if(priceRecord[i] != null){
				priceRecord[i][iTR] = getPrice(id);
				avgPriceRecord[i][iTR] = getAvgPrice(id);
//System.out.println("Avg Price of: " + Constants.getName(iP + OFFSET) + " = " + Utils.arrayString(avgPriceRecord[iP]));
			}
		}
	}

	/** Buy something 
	 * @param product name of product to buy
	 * @param qty the quantity of the product to buy
	 * @return true if the transaction was possible (ie, funds available and qty > 0) */
	public boolean buy(int product, double qty){
		double total = getPrice(product) * qty;
		if(qty > 0){
//System.out.println("Buying:" + Constants.getName(product) + " x " + qty + " for £" + total);
			costs.setValue(costs.doubleValue() - total);
			netProfit.setValue(netProfit.doubleValue() - total);
			balance.setValue(balance.doubleValue() - total);
			int i = product - OFFSET;
			buyRecord[i][iTR] += qty;
//System.out.println(Utils.arrayString(buyRecord));
//			buyValRecord[iTR] += total;
/*			qtyBought[i].setValue(qtyBought[i].doubleValue() + qty);
			valBought[i].setValue(valBought[i].doubleValue() + price);
			if(product == CARCAS_REMOVAL)
				valCulled.setValue(valCulled.doubleValue() - price);
*/			trans.add(new Object[]{Constants.getName(product), qty, UNITS.get(product), -total});
			bought.append("\n\t" + qty + " x " + Constants.getName(product) + " for £" + total);
			return true;
		}
		return false;
	}
	/** Sells something
	 * @param product name of the product to sell
	 * @param qty quantity to sell
	 * @return true if the sale was successful (ie qty > 0) */
	public boolean sell(int product, double qty){
//System.out.println("Trying to sell:" + product + " (" + Constants.getName(product) + ")" );
		double total = getPrice(product) * qty;
		if(qty > 0){
			revenue.setValue(revenue.doubleValue() + total);
			netProfit.setValue(netProfit.doubleValue() + total);
			balance.setValue(balance.doubleValue() + total);
			int i = product - OFFSET;
			sellRecord[i][iTR] += qty;
//			sellValRecord[iTR] += total;
/*			qtySold[i].setValue(qtySold[i].doubleValue() + qty);
			valSold[i].setValue(valSold[i].doubleValue() + price);
			if(product == CULL_COWS)
				valCulled.setValue(valCulled.doubleValue() + price);
*/			trans.add(new Object[]{Constants.getName(product), qty, UNITS.get(product), total});
			sold.append("\n\t" + qty + " x " + Constants.getName(product) + " for £" + total);
			return true;
		}
		return false;
	}

	/** Returns transaction details for the last week */
	public List<Object[]> getTransactions(){
		return trans;
	}
	
	/** Returns a log of last week's transactions as a string */
	public String getLog(){
		return "Market: bought.. " + bought.toString() + "\n" +
			   "Market: sold.. " + sold.toString() + "\n" +
			   "Market: balance: £" + balance + " profit: £" + netProfit + " revenue: £" + revenue + " costs: £" + costs;
	}
	
}
