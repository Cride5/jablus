/* ?. /*
 * Created on Nov 15, 2006
 *
 * TODO Move ecomomics related stuff to new object such as Market
 */

package uk.co.crider.jablus.models.absluc.env;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.env.DriverDynamic;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Environment;
import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.FarmLocation;
import uk.co.crider.jablus.models.basic.env.LandOccupancy;
import uk.co.crider.jablus.models.basic.env.LandProductivity;
import uk.co.crider.jablus.models.basic.env.LandUse;
import uk.co.crider.jablus.models.basic.env.Market;

import java.util.List;

/** Class to represent land use LandUseEnv, this top-level driver
 * holds references to all environmental drivers */
public class LandUseEnv extends Environment{

	// Data
//	private RasterMap landUse;

	// Natural capital
//	private RasterMap ownership;
//	private LinkedList freeCells;

//	private RasterMap slope;
//	private RasterMap soilMoisture;
	// Level of productivity of land for each land use
//	private RasterMap[] landProductivity;
//	private RasterMap[] productivityLimit;

	// Stores number of years until return on land use is gained
//	private RasterMap delayCounter;

	// Physical capital
//	private RoadNetwork roadNetwork;
//	private VectorMap roadNetworkVector;

	// Financial capital
//	private double[] subsidies;
//	private double[] marketPrice;
//	private double[] marketDemand;

	// Agents
	/** All agents which are involved in the system */
//	private List agents;
//	private AgentIdentifier envAgentId;
//	private int agentsInitialised;
//	private int agentsDoneFarming;
//	private int agentsDoneRecievingLand;

	//private VectorMap marketsVector;
	//private VectorMap farmsVector;
	//private VectorMap roadsVector;

//	private Parameters params;
	
	// Drivers used for key operations
	private List<DriverDynamic> dynamicDrivers;
	private List<DriverEndogeneous> endogeneousDrivers;
	
	// Drivers used directly in this class
	private LandUse landUse;
	private LandOccupancy occupancy;
		
	/** Constructor, initialises the environment */
	public LandUseEnv(Parameters params){
		super(Constants.DATASET_ENVIRONMENT, params);
		// Create and add compulsory drivers
//		landUse = new LandUse(params);
//		addItem(landUse);
//		occupancy = new LandOccupancy(params);
//		addItem(occupancy);
		// Create and add optional drivers
		if(params.MARKET)
			addItem(new Market(params, params.VECTOR_FOOTPRINT));
		if(params.PRODUCTIVITY){
			addItem(new LandProductivity(params));
//System.out.println("LandUseEnv: Generated productivity");
		}
		if(params.TRANSPORT_FARM)
			addItem(new FarmLocation(Constants.DRIVER_FARM_LOCATION));
		// Assign drivers to categories
		init();
	}

	/** Constructor for cloned object */
/*	private LandUseEnv(Parameters params, Hashtable<String, Driver> drivers){
		this.params = params;
		this.drivers = new Hashtable<String, Driver>();
		for(String key : drivers.keySet()){
			this.drivers.put(key, (Driver)drivers.get(key).clone());
		}
		this.time = (Time)this.drivers.get(Constants.TEMPORAL_TIME);
		this.landUse = (LandUse)this.drivers.get(Constants.DATASET_LAND_USE);
		this.landOccupancy = (LandOccupancy)this.drivers.get(Constants.DRIVER_LAND_OCCUPANCY);
		initLists();
		initDataSet();
	}
	public Object clone(){
		return new LandUseEnv(params, drivers);
	}
*/
	/** @inheridDoc */
//	public Object clone(){ return new LandUseEnv(name, keyName, dataItems, hasFile, containsDataSet); }
	/** Constructor for cloning this object */
//	private LandUseEnv(String name, String keyName, Map<String, Data> dataItems, boolean hasFile, boolean containsDataSet){
//		super(name, keyName, dataItems, hasFile, containsDataSet);
//	}
		
	// Inform environment of new agent
	public void notifyAgent(Agent agent){

	}
	
	
	/** Get the economics driver */
//	public Market getEconomics(){
//		return economics;
//	}
	
/*	public void performActions(Agent agent, EnvironmentActions actions){
//System.out.println("Performing actions: " + actions);
		// TODO: Sort farmers into farms here
		farm.performActions(actions);
	}
*/	
	/** Get the current land use */
//	public LandUse getLandUse(){
//		return landUse;
//	}

	/** Get the agent land occupancy */
//	public LandOccupancy getOccupancy(){
//		return occupancy;
//	}

	public boolean isActionPossible(Action a){
		return false;
	}

	public void execStep() {
	    // TODO Auto-generated method stub
	    
    }

	public void initStep() {
	    // TODO Auto-generated method stub
	    
    }

	public void init(DataSet drivers) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void notifyAction(Agent agent, Action action) {
	    // TODO Auto-generated method stub
	    
    }
	
}
