package uk.co.crider.jablus.models.basic.env;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.IntegerData1D;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.agent.LandUseCell;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import jwo.landserf.structure.ColourTable;

/** Keeps track of what each agent is using each land cell for. */
public class LandUse extends CompoundData implements DriverDynamic {

	private Parameters params;
	
	// Component drivers
	private Data2D landUse;
	private IntegerData1D useCount;

	
	public LandUse(Parameters params){
		super(Constants.DATASET_LAND_USE);
		this.params = params;
		// Create the land use map
		this.landUse = new MatrixData(Constants.DRIVER_LAND_USE, params.ROWS, params.COLS, true);
		// , getColourTable(params)
		addItem(landUse);
		// Set up use count statistics
		useCount = new IntegerData1D(
				Constants.OUTPUT_LAND_USE_COUNT,
				params.LAND_USE_NAMES,
				new int[params.LAND_USES]);
		addItem(useCount);
	}

/*	private LandUse(CompoundData stats, MatrixData landUse, IntegerData1D useCount, Hashtable<String, Data> drivers){
		this.dataItems = (CompoundData)stats.clone();
		this.landUse = (MatrixData)stats.getItem(Constants.DATASET_LAND_USE);
		this.useCount = (IntegerData1D)stats.getItem(Constants.OUTPUT_LAND_USE_COUNT);
		this.drivers = new Hashtable<String, Data>();
		for(String key : drivers.keySet()){
			this.drivers.put(key, (Data)drivers.get(key).clone());
		}
	}
	public Object clone(){
		return new LandUse(dataItems, landUse, useCount, drivers);
	}	
*/	
	
	// Land Use specific functionallity ------------------------
	
	public int getLandUse(LandCell cell){ return getLandUse(cell.x, cell.y); }
	public int getLandUse(int x, int y){
		return (int)landUse.getData(x, y);
	}

	public void useLand(Agent agent, LandCell cell, int use){
		//if(ownership.owns(Agent, cell))
//System.out.println("Agent " + agent + ": using cell " + cell + " as " + use);
			landUse.setData(cell.x, cell.y, use);
	}

	/** Set land use */
	public void useLand(Agent agent, LandUseProfile use){
		// TODO: Need to check for ownership for security, but results in loss of efficiency 
		Collection<LandUseCell> newUse = use.getLandUse();
		// First try to set land use from pattern
		if(newUse != null){
			for(LandUseCell cell : newUse){
				landUse.setData(cell.x, cell.y, cell.getUse());
			}
		}
//System.out.println("Agent " + agent + ": used land:\n " + this);		
	}
	
	public void setUnused(LandCell cell){
		landUse.setData(cell.x, cell.y, 0);
	}
	/** Called by agents wishing to use land for a particular purpose
	 * Method returns profit gained by using land for this use */
/*	public double useLand(LandCell cell, LandCell farm, int use){
		landUse.setAttribute(cell.x, cell.y, use);
		if(params.PRODUCTIVITY && params.PRODUCTIVITY_TM_HETRO){
			// Update productivity raster
			for(int i = 0; i < landProductivity.length; i++)
				landProductivity[i].setAttribute(cell.x, cell.y,
						(float)MathModels.landProductivity(
								landProductivity[i].getAttribute(cell.x, cell.y),    // Current
								params.productivityCost[use][i],                 // Growth
								productivityLimit[i].getAttribute(cell.x, cell.y))); // Maximum
		}
		return getRent(cell, farm, use);
	}*/

	private int[] calcLandUseCount(){
		int[] luCount = new int[params.LAND_USES];
		for(int x = 0; x < params.COLS; x++){
			for(int y = 0; y < params.ROWS; y++)
				luCount[(int)landUse.getData(x, y)]++;
		}
		return luCount;
	}

	
	// Implementing DriverDynamic --------------------------------
	
	public void initStep() {
		// Update use count data
		useCount.setValues(calcLandUseCount());
    }
	
	public void execStep() {
		// Nothing to do here
    }
	
	public static ColourTable getColourTable(Parameters params){
		ColourTable col = new ColourTable();
		for(int i = 0; i < params.LAND_USE_COLOUR.length; i++){
			col.addDiscreteColourRule(i,
					new Color(params.LAND_USE_COLOUR[i]).getRed(), 
					new Color(params.LAND_USE_COLOUR[i]).getGreen(),
					new Color(params.LAND_USE_COLOUR[i]).getBlue()
			);
		}
		return col;
	}
	
	// Implementing Data ------------------------------------
	
	/** @inheridDoc */
	public Object clone(){ return new LandUse(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object */
	private LandUse(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super(id, key, dataItems, hasFile, containsDataSet);
	}

}
