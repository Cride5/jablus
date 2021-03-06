/* Author: Conrad Rider ?. /*
 * Date: Nov 15, 2006
 */

package uk.co.crider.jablus.env;
import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Class to represent an environment upon which an agent can act
 * Model environments should extend this class */
public abstract class Environment extends CompoundData
	implements DriverDynamic, DriverEndogeneous{
	
	// Parameters upon which the environment is based
	protected final Parameters params;
	
	// All dynamic environments have time
	protected final Time time;
	
	// Drivers used for key operations
	private List<DriverDynamic> dynamicDrivers;
	private List<DriverEndogeneous> endogeneousDrivers;
	
	/** Constructor, initialises the environment */
	public Environment(int id, Parameters params){
		super(id);
		this.params = params;
		// Create and add compulsory drivers
		time = new Time(params.TIME_STEP, params.START_DAY, params.START_MONTH, params.START_YEAR);
		addItem(time);
		// Create list objects
		dynamicDrivers = new LinkedList<DriverDynamic>();
		endogeneousDrivers = new LinkedList<DriverEndogeneous>();
	}

	/** Causes all environment objects to be placed
	 * activated so that they will run properly. This
	 * should be called by all subclasses of environment
	 * (usually in the constructor) after all initialisation
	 * tasks are complete */
	protected final void init(){
		// Place drivers into categories
		for(Data driver : getItems()){
//System.out.println("Environment: item:" + driver);
			if(driver instanceof DriverDynamic)
				dynamicDrivers.add((DriverDynamic)driver);
			if(driver instanceof DriverEndogeneous)
				endogeneousDrivers.add((DriverEndogeneous)driver);
		}
		// Setup references for endogeneous drivers
		init(this);
		for(DriverEndogeneous driver : endogeneousDrivers){
			driver.init(this);
		}
	}
	
	/** Informs the environment of the arrival of a new agent */
	public abstract void notifyAgent(Agent agent);
	
	/** Informs the environment that an agent wishes to carry out the given action */
	public abstract void notifyAction(Agent agent, Action action);
	
	/** Returns the current time */
	public final Time getTime(){
		return time;
	}
	
	/** Returns true if the action is possible with
	 * the environment in its current state */
	public abstract boolean isActionPossible(Action action);
		
	/** Inform environment of a new step */
	public final void newStep(){
//System.out.println("Environment.newStep()");
		// All dynamic environment objects need to be executed
		initStep();
		for(Iterator i = dynamicDrivers.iterator(); i.hasNext();){
			((DriverDynamic)i.next()).initStep();
		}
		execStep();
		for(Iterator i = dynamicDrivers.iterator(); i.hasNext();){
			((DriverDynamic)i.next()).execStep();
		}
		if(params.LOGGING_TIME)
			System.out.println(time.dateString()); 
	}

}
