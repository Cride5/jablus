package uk.co.crider.jablus.models.basic.env.market;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DoubleData1D;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.LandUse;

/** Represents prices for products resulting from each land use */
public class MarketPrice extends DoubleData1D implements DriverEndogeneous, DriverDynamic{

	private Parameters params;
	
	// External drivers
	private Time time;
	private LandUse landUse;
	
	public MarketPrice(Parameters params){
		super(Constants.DRIVER_MARKET_PRICE, params.LAND_USE_NAMES, "pence");
		this.params = params;
		// Initialise prices
		if(params.MARKET_DEMAND_CYCLIC && params.MARKET_PRICE_CYCLE.length > 0){
			for(int i = 0; i < params.LAND_USES; i++){
				setValue(i, params.MARKET_PRICE_CYCLE[0][i]);
			}			
		}
		else if(params.MARKET_DEMAND_REACTIVE){
			for(int i = 0; i < params.LAND_USES; i++){
				setValue(i, 1.0/params.LAND_USES);
			}
		}
		else
			setValues(params.MARKET_PRICE);
		
	}

	
	// Implementing DriverEndogeneous ------------------------------
	
	/** @inheritDoc */
	public void init(DataSet drivers) {
//System.out.println("drivers="+drivers);
	    time = (Time)drivers.getItem(Constants.TEMPORAL_TIME);
	    landUse = (LandUse)drivers.getItem(Constants.DATASET_LAND_USE);
    }
	
	
	// Implementing DriverDynamic ----------------------------------

	/** Do stuff that depends on old values here */
	public void initStep(){
	    // Nothing to do here
    }

	/** Do stuff that depends on new values here */
	public void execStep(){
		if(params.MARKET_DEMAND_CYCLIC){
			setValues(params.MARKET_PRICE_CYCLE[time.intValue() % params.MARKET_PRICE_CYCLE.length]);
		}
		else if(params.MARKET_DEMAND_REACTIVE){
		    Data1D.Integer useCount = (Data1D.Integer)landUse.getItem(Constants.OUTPUT_LAND_USE_COUNT);
		    double totalCells = params.ROWS * params.COLS;
			for(int i = 0; i < params.LAND_USES; i++){
				setValue(i, 1.0 - (double)useCount.getValue(i) / totalCells);
			}
		}
    }
	
	/** @inheritDoc */
	public Object clone(){ return new MarketPrice(id, values, csvHeader, rawHeader, units); }
	/** Constructor for cloning this object */ 	
	private MarketPrice(int id, double[] values, String[] rawHeader, String[] csvHeader, String units){
		super(id, values, rawHeader, csvHeader, units); }

/*		this.params = params;
		this.prices = new double[prices.length];
		for(int i = 0; i < prices.length; i++)
			this.prices[i] = prices[i];
		this.csvHeader = csvHeader;
		this.rawHeader = rawHeader;
		this.time = time;
		this.landUse = landUse;
	}
*/
}
