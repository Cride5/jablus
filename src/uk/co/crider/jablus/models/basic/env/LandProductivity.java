package uk.co.crider.jablus.models.basic.env;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.models.basic.MathModels;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.util.Hashtable;
import java.util.Map;


import jwo.landserf.structure.ColourTable;

/** Represents differing productivity of land cells for any given use */
public class LandProductivity extends CompoundData implements DriverDynamic, DriverEndogeneous {

	// Level of productivity of land for each land use
	private Parameters params;
	// Component drivers
	private Hashtable<Integer, Data2D> productivity;
	// Component drivers
	private Hashtable<Integer, Data2D> productivityMax;
	// External drivers
	private LandUse landUse;

	public LandProductivity(Parameters params){
		super(Constants.DATASET_PRODUCTIVITY);
		this.params = params;
//		GISFrame gisFrame = null;
//		if(params.PRODUCTIVITY_SP_HETRO) gisFrame = new SimpleGISFrame();
		// Initialise productivity maps if in use
		if(params.PRODUCTIVITY_SP_HETRO){
			productivity = new Hashtable<Integer, Data2D>();
			if(params.PRODUCTIVITY_REACTIVE)
				productivityMax = new Hashtable<Integer, Data2D>();
		}
		// Set default productivity values if in use
		if(params.PRODUCTIVITY_SP_HETRO || params.PRODUCTIVITY_REACTIVE){
			// Seperate productivity maps for each use
			for(int i = 0; i < params.LAND_USES; i++){
				if(params.PRODUCTIVITY_SP_HETRO){
					// Construct from default map if available
					if(params.PRODUCTIVITY_MAPS != null && params.PRODUCTIVITY_MAPS[i] != null){
//						productivity.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY + Constants.SEPARATOR + params.LAND_USE_NAMES[i], params.PRODUCTIVITY_MAPS[i].getData(), true));
						productivity.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY, params.PRODUCTIVITY_MAPS[i].getData(), true));
					}
					// Otherwise we simply revert to default productivty
				}
				if(params.PRODUCTIVITY_REACTIVE)
					// If using heterogeneous maps then set max to pre-set map (if available)
					// Otherwise productivityMax is defined by productivity default
					if(params.PRODUCTIVITY_SP_HETRO && params.PRODUCTIVITY_MAPS != null && params.PRODUCTIVITY_MAPS[i] != null)
//						productivityMax.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY + Constants.SEPARATOR + params.LAND_USE_NAMES[i], params.PRODUCTIVITY_MAPS[i].getData(), true));
						productivityMax.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY , params.PRODUCTIVITY_MAPS[i].getData(), true));
					// If using reactive productivity and the basic productivity map does not exist
					// We need to create one with default values
					if(params.PRODUCTIVITY_REACTIVE && productivity.get(i) == null){
						float[][] prod = new float[params.ROWS][params.COLS];
						if(params.PRODUCTIVITY_DEFAULT != null && params.PRODUCTIVITY_DEFAULT[i] != 0){
							for(int y = 0; y < params.ROWS; y++)
								for(int x = 0; x < params.COLS; x++)
									prod[y][x] = (float)params.PRODUCTIVITY_DEFAULT[i];
						}
//						productivity.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY + Constants.SEPARATOR + params.LAND_USE_NAMES[i], prod, true));
						productivity.put(i, new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY , prod, true));
					}
				}
				
/*				
				MatrixData rast;
				// Set to an pre-loaded map
				if(params.PRODUCTIVITY_MAPS != null && params.PRODUCTIVITY_MAPS[i] != null){
					rast = new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY + Constants.SEPARATOR + params.LAND_USE_NAMES[i], params.PRODUCTIVITY_MAPS[i].getData(), true);
				}
				else{
					rast = new MatrixData(Constants.DRIVER_LAND_PRODUCTIVITY + Constants.SEPARATOR + params.LAND_USE_NAMES[i], params);
					// Set to a random fractal
					if(params.PRODUCTIVITY_DEFAULT[i] < params.PRODUCTIVITY_MIN){
						RasterMap map = new RasterMap(params.ROWS, params.COLS);
						FracGenerator.genFractal(
								map,
								gisFrame,
								(float)params.PRODUCTIVITY_DEFAULT[i],
								(float)params.PRODUCTIVITY_DEFAULT[i] / (float) Math.PI,
								params.PRODUCTIVITY_FRAC_SEED[i]);
						rast.setData(map.getRasterArray());
					}
					// Set to the default value for that land use
					else{
						for(int k = 0; k < rast.getRows(); k++){
							for(int j = 0; j < rast.getCols(); j++){
								rast.setData(k, j, (float)params.PRODUCTIVITY_DEFAULT[i]);
							}
						}
					}
				}
				// Add to collection
				productivity.put(i, rast);
				addItem(rast);
//				if(params.PRODUCTIVITY_TM_HETRO)
//				productivityLimit[i] = (RasterMap)landProductivity[i].clone();
			}
			// Dispose of gisFrame
			if(gisFrame != null)
				((SimpleGISFrame)gisFrame).getFrame().dispose();
				*/

			//Add productivity maps where required
			if(productivity != null){
				for(Data2D map : productivity.values()){
					addItem(map);
				}
			}
		}
	}
	
	// Basic driver functions ----------------------------------
	
	public double getProductivity(LandCell cell, int use){
		if(params.PRODUCTIVITY){
			if(params.PRODUCTIVITY_SP_HETRO || params.PRODUCTIVITY_REACTIVE)
				if(productivity.get(use) != null)
					return productivity.get(use).getData(cell.x, cell.y);
			if(params.PRODUCTIVITY_DEFAULT != null)
				return params.PRODUCTIVITY_DEFAULT[use];
		}
		return 0;
	}
	
	
	// Implementing DriverEndogeneous --------------------------
	
	public void init(DataSet drivers) {
	    landUse = (LandUse)drivers.getItem(Constants.DATASET_LAND_USE);
    }
	
	
	// Implementing DriverDynamic ------------------------------
	
	public void initStep() {
		// TODO: Implement proper growth/shrinking functions
		if(params.PRODUCTIVITY_REACTIVE){
			for(int x = 0; x < params.COLS; x++){
				for(int y = 0; y < params.ROWS; y++){
					for(int use = 0; use < params.LAND_USES; use++){
						float prod = productivity.get(use).getData(x, y);
						float prodMax = productivityMax != null && productivityMax.get(use) != null ? 
								productivityMax.get(use).getData(x, y) :
								params.PRODUCTIVITY_MAX != null ?
										(float)params.PRODUCTIVITY_MAX[use] : 1;
						if(landUse.getLandUse(x, y) == use){
							// Deteriorate productivities currently in use
							prod = (float)MathModels.verhulstDecline(prod, prodMax, params.PRODUCTIVITY_DECLINE_RATE[use]);
						}
						else{
							// Grow productivities not in use
							prod = (float)MathModels.verhulstGrowth(prod, prodMax, params.PRODUCTIVITY_GROWTH_RATE[use]);
						}
						productivity.get(use).setData(x, y, prod);
					}
				}
			}
		}
    }
	
	public void execStep() {
	    // Nothing to do here
    }

	
	// Implementing Data --------------------------------
	
	/** @inheridDoc */
	public Object clone(){ return new LandProductivity(id, key, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object */
	private LandProductivity(int id, int key, Map<Integer, Data> dataItems, boolean hasFile, boolean containsDataSet){
		super(id, key, dataItems, hasFile, containsDataSet);
	}

	
/*	public String getNameId(){ return componentDrivers.getNameId(); }
	public Data getItem(String name){ return componentDrivers.getItem(name); }
	public void addItem(Data data){ componentDrivers.addItem(data); }
	public void setItem(Data data){ componentDrivers.setItem(data); }
	public Collection<? extends Data> getItems(){ return componentDrivers.getItems(); }
	public Object clone(){ return new LandProductivity(productivity, landUse); }
	private LandProductivity(Hashtable<Integer, Data2D> productivity, LandUse landUse){		
		this.productivity = new Hashtable<Integer, Data2D>();
		this.componentDrivers = new CompoundData();
		for(Integer key : productivity.keySet()){
			Data2D data = (Data2D)productivity.get(key).clone();
			this.productivity.put(key, data);
			componentDrivers.addItem(data);
		}
		this.landUse = landUse;
	}
*/	
	
	public static ColourTable getColourTable(){
		ColourTable col = new ColourTable();
		col.addContinuousColourRule(0, 0, 0, 0);
		col.addContinuousColourRule(1, 255, 255, 255);
		return col;
	}
}
