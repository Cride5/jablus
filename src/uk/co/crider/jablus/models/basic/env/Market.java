package uk.co.crider.jablus.models.basic.env;

/* 
	INCLUDES:
 	* Market location
 	* Market price
 	* Market demand
 	* Road networks
 */

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.market.MarketPrice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jwo.landserf.structure.Footprint;

/** Represents a market located somewhere in the environment,
 * for trading outputs and providing materials. */
public class Market extends CompoundData implements DriverEndogeneous, DriverDynamic {

//	private String name;
//	private Parameters params;
	
	/** Component dynamic drivers */
	private List<DriverDynamic> dynamicDrivers;

	/** Create market driver and all sub-drivers */
	public Market(Parameters params, Footprint vectorFootprint){
	    super(Constants.DATASET_MARKET);
//		this.params = params;
	    
	    MarketPrice p = new MarketPrice(params);
	    addItem(p);
	    
/*		if(params.MARKET_DEMAND_REACTIVE){
			double[] marketPrice = new double[params.LAND_USES];
			double[] marketDemand = new double[params.LAND_USES];
			for(int i = 0; i < params.LAND_USES; i++){
				marketPrice[i] = params.MARKET_PRICE[i];
				marketDemand[i] = (double)params.ROWS * (double)params.COLS * params.MARKET_REACTIVE_DEMAND[i];
			}
			drivers.put(Constants.DRIVER_MARKET_PRICE, new GenericDynamicDriver(Constants.DRIVER_MARKET_PRICE, marketPrice));
			drivers.put(Constants.DRIVER_MARKET_DEMAND, new GenericDynamicDriver(Constants.DRIVER_MARKET_DEMAND, marketDemand)); 
		}
*/
		// Create new random road network
	    if(params.TRANSPORT){
	    	addItem(new RoadNetwork(Constants.DRIVER_ROAD_NETWORK, params, vectorFootprint));
	    	// Add to componentDrivers
	    }
	    initLists();
	}
/*	private Market(DataSet dataItems){	
		this.componentDrivers = (DataSet)dataItems.clone();
		initLists();
	}
	public Object clone(){
		return new Market(componentDrivers);
	}
*/
	
	private void initLists(){
		// Construct list objects
	    dynamicDrivers = new LinkedList<DriverDynamic>();
	    // Place drivers into categories
	    for(Data driver : getItems()){
			if(driver instanceof DriverDynamic)
			    dynamicDrivers.add((DriverDynamic)driver);
	    }
	}

	
	/*
	private void calcMarketPrices(){
		// Count quantities of each land use
		double[] luCount = calcLandUseCount();
//		System.out.print("Price");
		// Calculate new market demand based on supply
		for(int i = 0; i < marketPrice.length; i++){
			// Number of cells required to supply the demand
			if(luCount[i] > 1)
				marketPrice[i] = params.MARKET_PRICE[i] * marketDemand[i] / luCount[i];
			else
				marketPrice[i] = params.MARKET_PRICE[i] * marketDemand[i];
//			System.out.print("|" + i + "=" + marketPrice[i]);
		}
//		System.out.println();
	}
*/
	
	// Implementing DriverEndogeneous ---------------------
	
	/** @inheritDoc */
	public void init(DataSet drivers) {
		// Initialise all compoenent drivers
	    for(Data driver : getItems())
	    	if(driver instanceof DriverEndogeneous)
	    		((DriverEndogeneous)driver).init(drivers);
    }

	
	// Implementing DriverDynamic ---------------------------------
	
	/** @inheritDoc */
	public void initStep(){
	    for(DriverDynamic driver : dynamicDrivers)
			driver.initStep();
	}
	
	/** @inheritDoc */
	public void execStep(){
	    for(DriverDynamic driver : dynamicDrivers)
			driver.execStep();
	}
	
	// Implementing Data -----------------------------------------
	
	/** @inheridDoc */
	public Object clone(){ return new Market(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object */
	private Market(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super(id, key, dataItems, hasFile, containsDataSet);
	}

	
}
