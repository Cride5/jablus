/* ?. /*
 * Created on Nov 15, 2006
 *
 * TODO Move ecomomics related stuff to new object such as Market
 */

package uk.co.crider.jablus.models.basic.env;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.RasterData;
import uk.co.crider.jablus.env.Environment;
import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.models.basic.Parameters;

/** Class to represent land use LandUseEnv, this top-level driver
 * holds references to all environmental drivers */
public class BasicEnv extends Environment{

	// ENVIRONMENTAL ELEMENTS (affect land use output)
	// Road network
	// Rail network
	// River network
	// Mineral Resource (probably for mining)
	// Soil quility
	// Water supply
	// Land Topography
	// Local climate

	// ECONOMIC ELEMENTS
	// Land prices
	// Raw material prices (demand per product)
	// Road transfer costs (per product)
	// Rail transfer costs (per product)
	// Revenue delay (per product) - how many years until harvest, 40 years for forestry for example

	// SOCIAL ELEMENTS
	// Supply of cheap labour
	// Supply of public services

	// OUTPUT
	// Land use intensity
	// Land use


	// Thinking of the farming system as inputs and outputs
	// Inputs are:
	// (non transferable) solar energy, water, organic properties of soil, labour
	// (transferable) fertilisers, pesticides, animal feeds
	// Outputs are: (transferable) crops, (non-transferable) waste

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
	
//	private LandUse landUse;
//	private LandOccupancy occupancy;
	private RasterData aerialPhoto;
	
	private Parameters params;
	
	/** Constructor, initialises the environment */
	public BasicEnv(Parameters params){
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
	
	// Inform environment of new agent
	public void notifyAgent(Agent agent){
		
	}
		
	public boolean isActionPossible(Action action){
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

}
