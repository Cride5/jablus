package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Environment;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.models.dairy.env.field.Field;
import uk.co.crider.jablus.utils.Utils;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import uk.co.crider.models.dairy.RandomGenerator;

/** Base object to represent the environment.
 * Includes livestock population/digestion fields, crops, stores and market */
public class DairyFarm extends Environment implements DriverEndogeneous, DriverDynamic {
	
//	private Random rand;
	
	// Drivers used directly in this class
	private Livestock livestock;
	private CowDigestion digest;
//	private Nitrogen nitrogen;
//	private Crops crops;
	private Storage store;
	private Market market;
	private Agent owner;
	private VectorData fieldMap;
	private CompoundData fields;
	private Hashtable<Object, Field> fieldTable;
	private double totalArea;
	public double getFieldArea(){ return totalArea; }
	
	private RandomGenerator genRain;
	private RandomGenerator genTemp;
	private RandomGenerator genTmin;
	private RandomGenerator genRad;

	private DoubleData[] weekRain;
	public  DoubleData[]  getRain(){ return weekRain; }
	private DoubleData[] weekTemp;
	public  DoubleData[]  getTemp(){ return weekTemp; }
	private DoubleData[] weekTmin;
	public  DoubleData[]  getTmin(){ return weekTmin; }
	private DoubleData[] weekRad;
	public  DoubleData[]  getRad(){ return weekRad; }
	private DoubleData[] weekRadMean;
	public  DoubleData[]  getRadMean(){ return weekRadMean; }
	
	// Statistics used for evaluation
	private DoubleData[] cropYield; // Records the total yield of each crop type over the week
	public  DoubleData[] getCropYield(){ return cropYield; }
	private DoubleData[] cropYieldArea; // Records the summed area of specific harvests to calculate average yield
	public  DoubleData[] getCropYieldArea(){ return cropYieldArea; }
	private DoubleData[] cropAreaUsed; // Records the area used by each crop
	public  DoubleData[] getCropAreaUsed(){ return cropAreaUsed; }
	
	private DoubleData[] milkByGroup;
	public DoubleData[] getMilkByGroup(){ return milkByGroup; }
	
	private DoubleData manureOverflow;
	public DoubleData getManureOverflow(){ return manureOverflow; }
	private DoubleData surplusFeed;
	public DoubleData getSurplusFeed(){ return surplusFeed; }
	private DoubleData nitrateLeaching;
	public DoubleData getNitrateLeaching(){ return nitrateLeaching; }
	private DoubleData carcasesRemoved;
	public DoubleData getCarcasesRemoved(){ return carcasesRemoved; }
	
//	private IntegerData totalNewborn;
//	public IntegerData getTotalNewborn(){ return totalNewborn; }
	
	
	// Quantities of items
//	private DoubleData balance;
		
	// External drivers which affect the dairy farm
//	private Time time;

	public DairyFarm(Parameters params){
		super(Constants.DRIVER_DAIRY_FARM, params);
		
		// Add static map objects
		VectorData water = new VectorData(Constants.STATIC_RIVERS, params.SHAPEFILE_WATER);
		addItem(water);
		VectorData roads = new VectorData(Constants.STATIC_ROADS, params.SHAPEFILE_ROADS);
		addItem(roads);
		VectorData builds = new VectorData(Constants.STATIC_BUILDS, params.SHAPEFILE_BUILDS);		
		addItem(builds);

		// Create constituant drivers
		market = new Market(params);
		addItem(market);
		livestock = new Livestock(params);
		store = new Storage(params, livestock);
		addItem(store);
		// Add initial store items
		store.add(Market.CONCENTRATES, params.INIT_STORED_CONC);
		store.add(Market.HAY, params.INIT_STORED_HAY);
		store.add(Market.FERTILISER, params.INIT_STORED_FERTILISER);
		store.add(Market.SILAGE_GRASS, params.INIT_STORED_SILAGE_GRASS);
		store.add(Market.SILAGE_WHEAT, params.INIT_STORED_SILAGE_WHEAT);
		store.add(Market.SILAGE_MAIZE, params.INIT_STORED_SILAGE_MAIZE);
		store.add(Livestock.MANURE_WET, params.iNIT_STORED_SLURRY);
		// Add initial livestock
		if(params.INIT_LIVESTOCK != null){
			for(int i = 0; i < params.INIT_LIVESTOCK.length; i++){
				livestock.addCattle(i + Livestock.OFFSET, params.INIT_LIVESTOCK[i]);
				syncLivestockStore();
			}
		}
		addItem(livestock);
		// Add cow digestion model
		digest = new CowDigestion(params);
		addItem(digest);
		// Create fields
		fields = new CompoundData(Constants.DATASET_FIELDS, Constants.STATIC_FIELD_ID, true);
		addItem(fields);
		fieldTable = new Hashtable<Object, Field>();
		fieldMap = new VectorData(Constants.STATIC_FIELD_MAP, params.SHAPEFILE_FIELDS);
		addItem(fieldMap);
		double maxArea = -Double.MAX_VALUE;
		double minArea = Double.MAX_VALUE;
		totalArea = 0;
		for(int key : fieldMap.getFeatureKeys()){
			double area = fieldMap.getFeature(key).getGeometry().getArea() * 1E-4;
			totalArea += area;
//System.out.println(area);
			if(area < minArea) minArea = area;
			if(area > maxArea) maxArea = area;
		}
//System.out.println("MaxArea:" + maxArea);
		fieldMap.addAttribute(new DoubleData(Constants.STATIC_FIELD_AREA, Constants.UNITS_AREA_HIGH, minArea, maxArea));
		for(int key : fieldMap.getFeatureKeys()){
			double area = fieldMap.getFeature(key).getGeometry().getArea() * 1E-4;
			Field f = new Field(
					params, 
					(Integer)key, 
					new DoubleData(Constants.STATIC_FIELD_AREA, area , Constants.UNITS_AREA_HIGH, minArea, maxArea)
					);
//System.out.println("Creating field object for fid:" + key);
			fieldTable.put(key, f);
			fields.addItem(f);
			fieldMap.setAttribute(Constants.STATIC_FIELD_AREA, key, area);
		}
		
		// Create weather data
		weekRain    = new DoubleData[Time.DAYS_WEEK];
		weekTemp    = new DoubleData[Time.DAYS_WEEK];
		weekTmin	= new DoubleData[Time.DAYS_WEEK];
		weekRad     = new DoubleData[Time.DAYS_WEEK];
		weekRadMean = new DoubleData[Time.DAYS_WEEK];
		for(int i = 0; i < Time.DAYS_WEEK; i++){
			weekRain[i]    = new DoubleData(Constants.INPUT_RAIN,     Constants.UNITS_RAIN);
			weekTemp[i]    = new DoubleData(Constants.INPUT_TEMP,     Constants.UNITS_TEMPERATURE);
			weekTmin[i]    = new DoubleData(Constants.INPUT_TEMP_MIN, Constants.UNITS_TEMPERATURE);
			weekRad[i]     = new DoubleData(Constants.INPUT_RAD,      Constants.UNITS_RADIATION);
			weekRadMean[i] = new DoubleData(Constants.INPUT_RAD_MEAN, Constants.UNITS_RADIATION);
		}
		// Initialise weather generators
		Random rand = new Random(params.WEATHER_RANDOM_SEED);
		genRain    = new RandomGenerator(RandomGenerator.RAINFALL_DAILY       , rand.nextLong());
		genTemp    = new RandomGenerator(RandomGenerator.TEMPERATURE_DAILY    , rand.nextLong());
		genTmin    = new RandomGenerator(RandomGenerator.TEMPERATURE_MIN_DAILY, rand.nextLong());
		genRad     = new RandomGenerator(RandomGenerator.SOLAR_RADIATION_DAILY, rand.nextLong());
		if(!Double.isNaN(params.FIXED_RAIN)){
//System.out.println("FIXED RAIN!" + params.FIXED_RAIN);
			genRain.setParam(2, 0, params.FIXED_RAIN);
			genRain.setParam(2, 1, 0);
			genRain.setParam(2, 2, 0);
			genRain.setParam(2, 3, 0);
			genRain.setParam(2, 4, 0);
		}
		if(!Double.isNaN(params.FIXED_TEMP)){
			genTemp.setParam(2, 0, 0);
			genTemp.setParam(2, 1, 0);
			genTemp.setParam(3, 2, params.FIXED_TEMP);
			genTmin.setParam(2, 0, 0);
			genTmin.setParam(2, 1, 0);
			genTmin.setParam(3, 2, params.FIXED_TEMP - 3);
		}
		if(!Double.isNaN(params.FIXED_RAD)){
			genRad.setParam(0, 1, 0);
			genRad.setParam(0, 2, params.FIXED_RAD - 10);
			genRad.setParam(2, 0, 0);
			genRad.setParam(2, 1, 0);
			genRad.setParam(3, 2, params.FIXED_RAD);
		}
//		for(int i = 0; i < 10; i++){
//			System.out.println("rain:" + genRain.next() + ", temp:" + genTemp.next() + ", tmin:" + genTmin.next() + ", rad:" + genRad.next() + ", radMean:" + genRad.getMean());
//		}
		
		
		cropYield     = new DoubleData[Market.GROWN_FEEDS.length];
		cropYieldArea = new DoubleData[Market.GROWN_FEEDS.length];
		cropAreaUsed  = new DoubleData[Market.GROWN_FEEDS.length];
		for(int i = 0; i < cropYield.length; i++){
			cropYield[i]     = new DoubleData(0);
			cropYieldArea[i] = new DoubleData(0);
			cropAreaUsed[i]  = new DoubleData(0);
		}
		milkByGroup = new DoubleData[Livestock.HERD_GROUPS.length - 2];
		for(int i = 0; i < milkByGroup.length; i++){
			milkByGroup[i] = new DoubleData(0);
		}
		manureOverflow  = new DoubleData(Constants.OUTPUT_SLURRY_OVERFLOW  , Constants.UNITS_VOLUME_HIGH);
		surplusFeed     = new DoubleData(Constants.OUTPUT_SURPLUS_FEED     , Constants.UNITS_CURRENCY);
		nitrateLeaching = new DoubleData(Constants.OUTPUT_NITRATE_LEACHED  , Constants.UNITS_WEIGHT_HIGH);
		carcasesRemoved = new DoubleData(Constants.OUTPUT_DEAD_CULLS);
//		totalNewborn = new IntegerData(0);
		
		init();
	}
		
	/** Set up drivers endogeneous to DairyFarm */
	public void init(DataSet drivers) {
//		time = (Time)drivers.getItem(Constants.TEMPORAL_TIME);
//		System.out.println(getStatHeader());
		for(Data driver : getItems()){
			if(driver instanceof DriverEndogeneous)
				((DriverEndogeneous)driver).init(drivers);
		}
		for(Field f : fieldTable.values()){
			f.init(drivers);
		}
		
		// Initialise map attributes
		fieldTable.values().iterator().next().addMapAttributes();
		// Check attribute table is populated
		//for(Object key : fieldMap.getFeatureKeys())
		//	System.out.println(key + " -> " + fieldMap.getAttribute(Constants.INPUT_FIELD_CROP, key));

		// Sew grass in all fields initially
		for(Field fi : fieldTable.values())
			fi.sewCrop(Crop.GRASS);
		
	    // Generate initial weather forecast
		genWeather();
//		genCropAreaUsage();
	}
	
	/** @inheritDoc */
	public void notifyAgent(Agent owner){
		this.owner = owner;
		livestock.newHerdsman(owner);
//		nitrogen.newManager(owner);
//		crops.newGrower(owner);
	}

	
	/** @inheritDoc */
	public boolean isActionPossible(Action a){
		if(a.id == DairyAgent.ACTION_REVERT)
			return true;
		if((a.id == DairyAgent.ACTION_PLOUGH
		 || a.id == DairyAgent.ACTION_HARVEST)
				&& a.args != null && a.args.length > 0
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() != Crop.FALLOW)
			return true;
		if((a.id == DairyAgent.ACTION_FERTILISE)
				&& a.args != null && a.args.length > 1
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() != Crop.FALLOW)
			return true;
		if(a.id == DairyAgent.ACTION_SPREAD_SLURRY
				&& a.args != null && a.args.length > 1
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() != Crop.FALLOW)
			return store.canRemove(Livestock.MANURE_WET);
/*		if(a.id == DairyAgent.ACTION_SPREAD_SLURRY
				&& a.args != null && a.args.length > 1
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() != Crop.FALLOW)
			return store.canRemove(Livestock.MANURE_WET);
*/		if((a.id == DairyAgent.ACTION_SEW_GRASS
		 || a.id == DairyAgent.ACTION_SEW_WHEAT
		 || a.id == DairyAgent.ACTION_SEW_MAIZE)
				&& a.args != null && a.args.length > 0
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue()))
//				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() == Crop.FALLOW)
			return true;
		if(a.id == DairyAgent.ACTION_GRAZING_ON
				&& a.args != null && a.args.length > 0
				&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
				&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() == Crop.GRASS)
			return true;
		if(a.id == DairyAgent.ACTION_GRAZING_OFF
					&& a.args != null && a.args.length > 0
					&& fieldTable.containsKey(((IntegerData)a.args[0]).getValue())
					&& fieldTable.get(((IntegerData)a.args[0]).getValue()).getCropType() == Crop.PASTURE)
				return true;
		if((a.id == DairyAgent.ACTION_SET_GRAZING
		||	a.id == DairyAgent.ACTION_SET_CONFINED)
				&& a.args != null && a.args.length > 0
				&& ((IntegerData)a.args[0]).intValue() >= 0
				&& ((IntegerData)a.args[0]).intValue() < Livestock.HERD_GROUPS.length)
			return true;
		if(a.id == DairyAgent.ACTION_BUY)
			return store.canAdd(((IntegerData)a.args[0]).intValue());
		if(a.id == DairyAgent.ACTION_SELL)
			return store.canRemove(((IntegerData)a.args[0]).intValue());
		if(owner.isActionType(a, DairyAgent.FEED_BUDGET_ACTION))
			return a.args.length >= 2;
		return false;
	}
	
	/** @inheritDoc */
	public void notifyAction(Agent agent, Action action){
		if(params.LOGGING_ACTIONS)
			System.out.println("DairyFarm: " + action);
		if(owner.isActionType(action, DairyAgent.MARKET_ACTION)){
			int product = ((IntegerData)action.args[0]).intValue();
			double qty = ((Data0D)action.args[1]).getValue().doubleValue();
//	System.out.println("product = " + Constants.getName(product) + ", qty = " + qty);
			if(action.id == DairyAgent.ACTION_BUY){
				market.buy(product, qty);
				if(Livestock.isLivestock(product) || Market.isTradeLivestock(product)){
					livestock.addCattle(product, (int)qty);
					syncLivestockStore();
				}
				else if(Market.isStorageBuilding(product))
					store.addCapacity(product, qty);
				else store.add(product, qty);
			}
			else if(action.id == DairyAgent.ACTION_SELL){
				market.sell(Livestock.isLivestock(product) ?
						Market.CULL_COWS :
						product, qty);
				if(Livestock.isLivestock(product) || Market.isTradeLivestock(product)){
					livestock.addCattle(product, -(int)qty);
					syncLivestockStore();
				}
				else{
					store.remove(product, qty);
				}
			}
		}
		else if(owner.isActionType(action, DairyAgent.FIELD_ACTION)){
			Field f = fieldTable.get(((IntegerData)action.args[0]).intValue());
			double area = f.getArea();
			if(action.id == DairyAgent.ACTION_PLOUGH){
				market.buy(Market.PLOUGH, area);
				f.ploughField();
			}
			else if(action.id == DairyAgent.ACTION_SEW_GRASS){
				market.buy(Market.GRASS_SOW, area);
				f.sewCrop(Crop.GRASS);
			}
			else if(action.id == DairyAgent.ACTION_SEW_WHEAT){
				market.buy(Market.WHEAT_SOW, area);
				f.sewCrop(Crop.WHEAT);
			}
			else if(action.id == DairyAgent.ACTION_SEW_MAIZE){
				market.buy(Market.MAIZE_SOW, area);
				f.sewCrop(Crop.MAIZE);
			}
			else if(action.id == DairyAgent.ACTION_FERTILISE){
				market.buy(Market.FERTILISE, area);
				double toSpread = ((IntegerData)action.args[1]).intValue() * area * 1E-3; // Convert to tonnes
//System.out.println("toSpread=" + toSpread);
				double fromStore = store.remove(Market.FERTILISER, toSpread);
				// Purchase extra fertiliser from market if not enough available
				if(fromStore < toSpread)
					market.buy(Market.FERTILISER, toSpread - fromStore);
				f.spread(Market.FERTILISER, toSpread);
			}
			else if(action.id == DairyAgent.ACTION_SPREAD_SLURRY){
				market.buy(Market.SPREAD, area);
				double toSpread = ((IntegerData)action.args[1]).intValue() * f.getArea(); 
				toSpread = store.remove(Livestock.MANURE_WET, toSpread);
				f.spread(Livestock.MANURE_WET, toSpread);
			}
			else if(action.id == DairyAgent.ACTION_HARVEST){
				int product = f.getCropProduct();
				int i = product - Market.SILAGE_GRASS;
				int harvest =
					product == Market.SILAGE_GRASS ? Market.GRASS_HARVEST :
					product == Market.SILAGE_WHEAT ? Market.WHEAT_HARVEST :
					product == Market.SILAGE_MAIZE ? Market.MAIZE_HARVEST : 0;
				if(harvest != 0) market.buy(harvest, area);
				double yield = f.harvestCrop() * area;
				cropYield[i].setValue(cropYield[i].doubleValue() + yield);
				cropYieldArea[i].setValue(cropYieldArea[i].doubleValue() + area);
				double added = store.add(product, yield);
				if(added < yield) market.sell(product, yield - added);
			}
			else if(action.id == DairyAgent.ACTION_GRAZING_ON)
				f.setGrazing(true);
			else if(action.id == DairyAgent.ACTION_GRAZING_OFF)
				f.setGrazing(false);
		}else if(owner.isActionType(action, DairyAgent.LIVESTOCK_ACTION)){
			if(action.id == DairyAgent.ACTION_SET_GRAZING)
				livestock.setPropGrazing(((IntegerData)action.args[0]).intValue() + Livestock.OFFSET, 100);
			if(action.id == DairyAgent.ACTION_SET_CONFINED)
				livestock.setPropGrazing(((IntegerData)action.args[0]).intValue() + Livestock.OFFSET, 0);
		}
		else if(owner.isActionType(action, DairyAgent.FEED_BUDGET_ACTION)){
			digest.setBudget(action.id, ((Data0D.Integer)action.args[0]).intValue(), ((Data0D.Integer)action.args[1]).intValue());
		}
		
	}
	
/*
	public void performActions(EnvironmentActions actions){
		System.out.println("DairyFarm: Actions: " + actions);
		livestock.performActions(actions);
	}
*/	

	/** Reset stats for interface */
	public void resetStats(){
		// Reset field stats
//		System.out.println("Resetting field stats");
		for(int i = 0; i < Market.GROWN_FEEDS.length; i++){
			cropYield[i].setValue(0);
			cropYieldArea[i].setValue(0);
		}
		for(int i = 0; i < milkByGroup.length; i++){
			milkByGroup[i].setValue(0);
		}
		manureOverflow.setValue(0);
		nitrateLeaching.setValue(0);
		carcasesRemoved.setValue(0);
	}
	
	/** @inheritDoc */
	public void initStep() {
		// Do nothing
		//System.out.println("DairyFarm: FARM");
    }
	
	/** @inheritDoc */
	public void execStep(){
		
		// Process actions
		Collection<Action> actions = owner.getActionsToExecute();
		for(Action action : actions){
			notifyAction(owner, action);
		}
	    
		// Calculate total grazing area
		double grazeAreaTotal = 0;
		for(Field f : fieldTable.values())
			grazeAreaTotal += f.getCropType() == Crop.PASTURE ? f.getArea() : 0;
		// Calculate and set propHerd
		if(grazeAreaTotal > 0){
			for(Field f : fieldTable.values()){
				f.setPropHerd(f.getCropType() == Crop.PASTURE ? f.getArea() / grazeAreaTotal : 0);
			}
		}

		// Calculate new livestock population
		livestock.execWeek();
		
		// Generate input weather data and run crop/cow digestion models
    	int start = time.getDayOfYear();
    	int nDays = 7;//time.getDaysThisMonth() - time.getDayOfMonth();
    	int end = time.getDayOfYear() + nDays;

    	// Assign grazing cows to particular fields (or none if not grazing)
    	Hashtable<int[][], Field> cowGroups = new Hashtable<int[][], Field>();
    	int[][] cows = livestock.getCows();
    	// Create fCows array for each field
    	for(Field f : fieldTable.values()){
    		if(f.getCropType() == Crop.PASTURE){
    			int[][] fCows = new int[cows.length][];
    			for(int i = 0; i < cows.length; i++) fCows[i] = new int[cows[i].length];
    			cowGroups.put(fCows, f);
    		}
    	}
    	int[][] confinedCows = new int[cows.length][];
		for(int i = 0; i < cows.length; i++) confinedCows[i] = new int[cows[i].length];

    	
    	// Prtition cows into grazing groups by field, and a non-grazing group
    	for(int i = 0; i < cows.length; i++){
    		for(int j = 0; j < cows[i].length; j++){
    			int n = cows[i][j];
    			int nLeft = n;
    			int[][] lField = confinedCows;
    			double highestProp = 0;
    			double totalProp = 0;
    			for(int[][] fCows : cowGroups.keySet()){
    				Field f = cowGroups.get(fCows);
    				int cowsToAdd = Utils.pRound(cows[i][j] * livestock.getPropGrazing(i, j) * f.getPropHerd(), livestock.getRand());
    				if(cowsToAdd <= nLeft){
    					fCows[i][j] = cowsToAdd;
    					nLeft -= cowsToAdd;
    				}
    				else{
    					fCows[i][j] = nLeft;
    					nLeft = 0;
    				}
    				if(highestProp < f.getPropHerd()){
    					highestProp = f.getPropHerd();
    					lField = fCows;
    				}
    			}
    			// Add remaining cows
    			if(nLeft > 0){
    				// If more of this cow group are grazing, place remainder in random field
    				if(livestock.getPropGrazing(i, j) > 0.5){
    					lField[i][j] += nLeft;
    				}
        			//  confined group
    				else
    					confinedCows[i][j] = nLeft;
    			}
    		}
    	}
//int i_ = 0;
//for(int[][] fCows : cowGroups.keySet()){i_++;
//	System.out.println("Field group " + i_ + ": " + Utils.sum(fCows));}
//System.out.println("Confined Cows:" + Utils.sum(confinedCows));
    	double[] offeredGrazing = new double[digest.getOfferedGrazing().length];
    	double[] intakeGrazing = new double[digest.getOfferedGrazing().length];
    	double[] surplusGrazing = new double[digest.getSurplusGrazing().length];
    	int nGrazing = 0;
    	double[] offeredConfined = new double[digest.getOfferedConfined().length];
    	double[] intakeConfined = new double[digest.getOfferedConfined().length];
    	double[] surplusConfined = new double[digest.getSurplusConfined().length];
    	int nConfined = 0;
    	// Used to measure starvation stress. If energy intake is always
    	// higher than that required for maintainence then there is no stress
    	// as energy intake drops, stress increases, affecting chances of survival
    	// in the cow population model
    	double Eavail_sum = 0;
    	// Total milk produced
    	double milk = 0;
    	// Number of cows milked
    	int nMilked = 0;
    	// Milk by cow group
    	double[] milkGroup = new double[Livestock.HERD_GROUPS.length - 2];
    	// Total manure produced
    	double manureCollected = 0;
    	// Total manure nitrogen produced
    	double manureNCollected = 0;
	    for(int d = start; d < end; d++){
	    	// Update weather totals
//	    	rainTotal += rain;
//	    	tempTotal += temp;
//	    	radTotal += rad;
	    	// Reset cows milked
	    	nMilked = 0;
	    	// Reset n grazing/confined
//	    	nGrazing = 0;
//	    	nConfined = 0;
	    	// Execute crop day for each field
	    	for(Field f : fieldTable.values())
	    		f.cropDay(
	    				weekRain[d - start].doubleValue(),
	    				weekTemp[d - start].doubleValue(),
	    				weekTmin[d - start].doubleValue(),
	    				weekRad[d - start].doubleValue());
	    	// Execute cow digestion for each cow group
	    	Iterator<int[][]> it = cowGroups.keySet().iterator();
	    	int[][] cowGroup = confinedCows;
	    	Field f = null;
	    	boolean done = false;
//System.out.println(Utils.arrayString(cowGroup));
	    	while(!done){
	    		int nCows = 0; // Number of cows in this field
	    		double qih = 0; // Total herbage intake
	    		boolean grazing = f != null && f.getPasture().isGrazing();
	    		double manurePasture = 0; // Manure excreted onto field (kg)
	    		double manureNPasture = 0; // Manure nitrogen excreted onto field (kgN)
	    		// Iterate through lactation cycles
	    		for(int j = 0; j < cowGroup.length; j++){
	    			// Iterate through weeks
		    		for(int i = 0; i < cowGroup[j].length; i++){
		    		    // The first group is heifers (0-months heifers are on mother's milk so not included)	    		
		    			boolean newBorn = (i == 0 && j == 0);
		    			// don't continue if there aren't any cows or the cows are newborns
		    			if(cowGroup[j][i] == 0 || newBorn) continue;
		    			// Calculate cow state
	    				int heiferAge = j == 0 ? i : -1;
	    				int nCalving = i > Livestock.PERIOD_DRY ? (i - Livestock.PERIOD_DRY) * 7 : 0;
		    			boolean milking = j > 0 && i >= Livestock.PERIOD_DRY;
		    			// Execute digestability model
		    			digest.execDay(f == null ? null : f.getPasture(), heiferAge, nCalving, milking);
		    			// Add group to total cow count
		    			nCows += cowGroup[j][i];
		    			// Add herbage intake
		    			qih += digest.getIntake(CowDigestion.FEED_GRASS) * cowGroup[j][i];
		    			// Update energy availability
	    				Eavail_sum += digest.getEavailability() * cowGroup[j][i];
		    			if(grazing){
		    				// Count grazing cows
		    				nGrazing += cowGroup[j][i];
		    				// Update intake of grazing cows
			    			for(int k = 0; k < intakeGrazing.length; k++){
			    				// If cow is still a heifer, then scale offered herbage down...
			    				double og = CowDigestion.scaleIntake(
			    						digest.getOfferedGrazing()[k].intValue() * cowGroup[j][i], heiferAge);
			    				offeredGrazing[k] += og;
			    				double ig = digest.getIntake(k + CowDigestion.OFFSET) * cowGroup[j][i];
			    				intakeGrazing[k] += ig;
			    				surplusGrazing[k] += og - ig;
			    			}
			    			// Excreted manure to be spread over field
		    				manurePasture += digest.getSlurry().doubleValue() * cowGroup[j][i];
		    				manureNPasture += digest.getManureN().doubleValue() * cowGroup[j][i];
		    			}
		    			else{
		    				// Count confined cows
		    				nConfined += cowGroup[j][i];
		    				// Update intake of confined cows
			    			for(int k = 0; k < intakeConfined.length; k++){
			    				double oc = CowDigestion.scaleIntake(
			    						digest.getOfferedConfined()[k].intValue() * cowGroup[j][i], heiferAge);
			    				offeredConfined[k] += oc;
			    				double ic = digest.getIntake(k + CowDigestion.OFFSET) * cowGroup[j][i];
			    				intakeConfined[k] += ic;
			    				surplusConfined[k] += oc - ic;
			    			}
			    			// Manure can be collected
		    				manureCollected += digest.getSlurry().doubleValue() * cowGroup[j][i];
		    				manureNCollected += digest.getManureN().doubleValue() * cowGroup[j][i];
		    			}
		    			if(milking){ // TODO: Also check space available
		    				nMilked += cowGroup[j][i];
	    					milk += digest.getMilkProduced() * cowGroup[j][i];
	    					milkGroup[j - 1 >= milkGroup.length ? milkGroup.length - 1 : j - 1]
	    							+= digest.getMilkProduced() * cowGroup[j][i];
		    			}
		    		}
	    		}
	    		// Update pasture stats
	    		if(f != null){
	    			f.getPasture().setGrazing(nCows, nCows == 0 ? 0 : qih / nCows);
	    			f.spread(Livestock.MANURE_WET, manurePasture, manureNPasture * 1E-3); // Convert manureN to tonnes
	    		}
	    		// Retrive next item
	    		if(it.hasNext()){
		    		cowGroup = it.next();
		    		f = cowGroups.get(cowGroup);
	    		}
	    		else done = true;
	    	}
	    }

	    // Set energy availabilty value
	    livestock.setEavail(Eavail_sum / (livestock.getTotal() * Time.DAYS_WEEK));
		// Sell all products
		market.sell(Market.MILK, milk);
		for(int i = Livestock.OFFSET; i < Livestock.OFFSET + Livestock.SALE_GROUPS.length; i++){
			int toSell = livestock.getCattleRemoved(i);
			if(i == Livestock.STEERS)
				market.sell(Market.BULL_CALVES, toSell);
			else
				market.sell(Market.CULL_COWS, toSell);
		}
		// Update actual numbers grazed and confined
		livestock.setActualConfined(nConfined);
		livestock.setActualGrazing(nGrazing);
		
		// Update consumed feeds
		for(int i = 0; i < intakeGrazing.length; i++){
			digest.getConsumedGrazing()[i].setValue(nGrazing > 0 ? intakeGrazing[i] / nGrazing : 0);
			digest.getSurplusGrazing()[i].setValue(nGrazing > 0 ? surplusGrazing[i] / nGrazing : 0);
		}
		for(int i = 0; i < intakeConfined.length; i++){
			digest.getConsumedConfined()[i].setValue(nConfined > 0 ? intakeConfined[i] / nConfined : 0);
			digest.getSurplusConfined()[i].setValue(nConfined > 0 ? surplusConfined[i] / nConfined : 0);
		}
		// Update milk production
//System.out.println("MILK:" + milk);
		if(nMilked > 0)
			digest.getActualYield().setValue(milk * Time.WEEKS_YEAR / nMilked);
		else
			digest.getActualYield().setValue(0);
		
		// Convert intakes from kg to tonnes
		for(int i = 0; i < offeredGrazing.length; i++){
			offeredGrazing[i] *= 1E-3;
			intakeGrazing[i] *= 1E-3;
		}
		for(int i = 0; i < offeredConfined.length; i++){
			offeredConfined[i] *= 1E-3;
			intakeConfined[i] *= 1E-3;
		}

		// Calculate total offered
		double qoc = offeredGrazing [CowDigestion.FEED_CONC         - CowDigestion.OFFSET] +
		             offeredConfined[CowDigestion.FEED_CONC         - CowDigestion.OFFSET];
		double qoa = offeredGrazing [CowDigestion.FEED_HAY          - CowDigestion.OFFSET] +
		             offeredConfined[CowDigestion.FEED_HAY          - CowDigestion.OFFSET];
		double qos = offeredGrazing [CowDigestion.FEED_SILAGE_GRASS - CowDigestion.OFFSET] +
                     offeredConfined[CowDigestion.FEED_SILAGE_GRASS - CowDigestion.OFFSET];
		double qog = offeredGrazing [CowDigestion.FEED_SILAGE_WHEAT - CowDigestion.OFFSET] +
                     offeredConfined[CowDigestion.FEED_SILAGE_WHEAT - CowDigestion.OFFSET];
		double qom = offeredGrazing [CowDigestion.FEED_SILAGE_MAIZE - CowDigestion.OFFSET] +
                     offeredConfined[CowDigestion.FEED_SILAGE_MAIZE - CowDigestion.OFFSET];
		
		// Retrive feeds from store, or buy if needs be
		double cStore = store.remove(Market.CONCENTRATES, qoc);
		if(cStore < qoc) market.buy(Market.CONCENTRATES, qoc - cStore);
		double sStore = store.remove(Market.SILAGE_GRASS, qos);
		if(sStore < qos) market.buy(Market.SILAGE_GRASS, (qos - sStore));		
		double aStore = store.remove(Market.HAY, qoa);
		if(aStore < qoa) market.buy(Market.HAY, (qoa - aStore));
		double wStore = store.remove(Market.SILAGE_WHEAT, qog);
		if(wStore < qog) market.buy(Market.SILAGE_WHEAT, (qog - wStore));		
		double mStore = store.remove(Market.SILAGE_MAIZE, qom);
		if(mStore < qom) market.buy(Market.SILAGE_MAIZE, (qom - mStore));
		
		// Calculate total actual intake
		double qic = intakeGrazing [CowDigestion.FEED_CONC         - CowDigestion.OFFSET] +
		             intakeConfined[CowDigestion.FEED_CONC         - CowDigestion.OFFSET];
		double qia = intakeGrazing [CowDigestion.FEED_HAY          - CowDigestion.OFFSET] +
		             intakeConfined[CowDigestion.FEED_HAY          - CowDigestion.OFFSET];
		double qis = intakeGrazing [CowDigestion.FEED_SILAGE_GRASS - CowDigestion.OFFSET] +
                     intakeConfined[CowDigestion.FEED_SILAGE_GRASS - CowDigestion.OFFSET];
		double qig = intakeGrazing [CowDigestion.FEED_SILAGE_WHEAT - CowDigestion.OFFSET] +
                     intakeConfined[CowDigestion.FEED_SILAGE_WHEAT - CowDigestion.OFFSET];
		double qim = intakeGrazing [CowDigestion.FEED_SILAGE_MAIZE - CowDigestion.OFFSET] +
                     intakeConfined[CowDigestion.FEED_SILAGE_MAIZE - CowDigestion.OFFSET];
		
		// Calculate value of surplus feed
		double surplusVal =
			(qoc - qic) * market.getPrice(Market.CONCENTRATES) + 
			(qoa - qia) * market.getPrice(Market.HAY) + 
			(qos - qis) * market.getPrice(Market.SILAGE_GRASS) + 
			(qog - qig) * market.getPrice(Market.SILAGE_WHEAT) + 
			(qom - qim) * market.getPrice(Market.SILAGE_MAIZE);
		surplusFeed.setValue(surplusVal);
		
		// Add manure from cow steading to store, and spread if srore full
//System.out.println("Cows:" + ((nGrazing + nConfined)/7) + " Slurry collected:" + manureCollected);
//		manureCollected *= 1E-3; // Convert to tonnes
		manureNCollected *= 1E-3; // Convert to tonnes
		double mnStore = store.add(Livestock.MANURE_WET, manureCollected); 
		if(mnStore < manureCollected){
			double mLeft = manureCollected - mnStore;
			double mLeftN = manureNCollected * mLeft / manureCollected;
			manureOverflow.setValue(manureOverflow.doubleValue() + mLeft);
//System.out.println("Overflowed manure " + manureOverflow);
			mLeft /= totalArea;
			mLeftN /= totalArea;
			for(Field f : fieldTable.values())
				f.spread(Livestock.MANURE_WET, mLeft * f.getArea(), mLeftN * f.getArea());
		}
		
		// Calculate nitrate leaching
		double totalLeaching = 0;
		for(Field f : fieldTable.values())
			totalLeaching += f.getLeaching().doubleValue() * 1E-3; // Convert to tonnes
		nitrateLeaching.setValue(totalLeaching);
		
		// Purchase livestock consumables
		market.buy(Market.VETINARY_MEDS, livestock.getTotal());
		market.buy(Market.INSEMINATION, livestock.getTotal(Livestock.BREEDING));
//		market.buy(Constants.PRODUCT_ANIMALS_COWS, cowsBought);
//		market.buy(Constants.PRODUCT_ANIMALS_FEMALE_CALVES, femaleCalvesBought);
		market.buy(Market.LVS_OVERHEADS, livestock.getTotal());
		// TODO: What about milking overheads:
//		market.buy(Market.MILKING, nMilked);
				
		// Sell live culled carcases
		int liveCarcases = Utils.pRound(livestock.getTotal(Livestock.CARCASES) * Livestock.PROB_LIVE_CULL);
		market.sell(Market.CULL_COWS  , liveCarcases);	
		// Purchase removal of dead carcases
		int deadCarcases = livestock.getTotal(Livestock.CARCASES) - liveCarcases;
		market.buy(Market.CARCAS_REMOVAL, deadCarcases);
		carcasesRemoved.setValue(carcasesRemoved.doubleValue() + deadCarcases);
		
		// Update livestock stores
		syncLivestockStore();
		// Update newborn var
//		totalNewborn.setValue(totalNewborn.intValue() + livestock.getTotal(Livestock.NEWBORN));
		
		// Pay all necessery bills
		
		// If not possible to pay rent then farm is bankrupt
		market.buy(Market.FARM_RENT, 1);
		
		// Receive farm subsidies
		market.sell(Market.SFP, totalArea);
		
		
		// Reset yearly totals to 0 at the beginning of they year
		if(time.getWeekOfYear() >= time.getWeeksThisYear() - 1){
			manureOverflow.setValue(0);
			nitrateLeaching.setValue(0);
			carcasesRemoved.setValue(0);
			for(Field f : fieldTable.values())
				f.getLeaching().setValue(0);
			for(int i = 0; i < milkByGroup.length; i++)
				milkByGroup[i].setValue(0);
		}
	    for(Field f : fieldTable.values()){
	    	f.updateMapAttributes();
	    }
//	    genCropAreaUsage();
		for(int i = 0; i < milkByGroup.length; i++)
			milkByGroup[i].setValue(milkByGroup[i].doubleValue() + milkGroup[i]);
	    
//System.out.println("Week:" + time.getWeekOfYear() + " area " + Utils.arrayString(cropAreaUsed));

	    // Generate next week's weather for the forecast
	    genWeather();	    
    }
	
	public void genCropAreaUsage(){
		for(int i = 0; i < Market.GROWN_FEEDS.length; i++)
			cropAreaUsed[i].setValue(0);
	    for(Field f : fieldTable.values()){
	    	// Record area dedicated to each crop
	    	int crop = f.getCropProduct();
	    	if(crop != 0)
	    		cropAreaUsed[crop - Market.SILAGE_GRASS].setValue(
	    			cropAreaUsed[crop - Market.SILAGE_GRASS].doubleValue() + f.getArea());
	    }
	}
	
	/** Generate weather */
	private void genWeather(){
    	int d = time.getDayOfYear();
    	for(int i = 0; i < Time.DAYS_WEEK ; d++, i++){
	    	weekRain[i].setValue(genRain.next());
	    	weekTemp[i].setValue(genTemp.next());
	    	weekTmin[i].setValue(genTmin.next());
	    	weekRad[i].setValue(genRad.next());
	    	weekRadMean[i].setValue(genRad.getMean());
    	}
	}
	
	/** Synchronize herd and stored livestock quantities */
	private void syncLivestockStore(){
		// Update livestock store
		int stored = 0;
		for(int i = 0; i < Livestock.HERD_GROUPS.length; i++){
//System.out.println("setting stored " + Constants.getName(i + Livestock.OFFSET) + " to:" + livestock.getTotal(i + Livestock.OFFSET));
			stored += store.set(i + Livestock.OFFSET, livestock.getTotal(i + Livestock.OFFSET));
		}
		// If we couldn't store all the livestock then cull livestock
		if(stored < livestock.getTotal()){
			int toRemove = livestock.getTotal() - stored;
			livestock.addCattle(Market.CULL_COWS, -toRemove);
			market.sell(Market.CULL_COWS, toRemove);
//			for(int i = 0; toRemove > 0 && i < Livestock.HERD_GROUPS.length; i++){
//				int avail = livestock.getTotal(i + Livestock.OFFSET);
//				int sold = 0;
//				if(toRemove < avail){ sold = toRemove; toRemove = 0; }
//				else                { sold = avail; toRemove -= avail; }
//				livestock.buyCattle(i + Livestock.OFFSET, -sold);
//				market.sell(i + Livestock.OFFSET, sold);
//			}
		}			
	}
		
}
