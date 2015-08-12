package uk.co.crider.jablus.models.basic.env;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.util.Hashtable;

import jwo.landserf.structure.VectorMap;

/** Represents the location of a farm */
public class FarmLocation implements Data {

	private int id;
	private Hashtable<Agent, LandCell> farms;
	
	public FarmLocation(int id){
		this.id = id;
		this.farms = new Hashtable<Agent, LandCell>();
	}
	/** Constructor for cloned object */
	private FarmLocation(int id, Hashtable<Agent, LandCell> farms){
		this.id = id;
		this.farms = (Hashtable<Agent, LandCell>)farms.clone();
	}
	/** @inheritDoc */
	public Object clone(){ return new FarmLocation(id, farms); }

	
	public VectorMap getData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Adds a farm to the environment */
	public void addFarm(Agent agent, LandCell farm){
		farms.put(agent, farm);
	}
	
	/** Returns the location of the farm belonging to agent id */
	public LandCell getFarm(Agent agent){
		return farms.get(agent);
	}

	// Implementing Data ------------------------
	
	public int getId() {
		return id;
	}

	public String getName() {
		return Constants.getName(id);
	}
	
	public String stringValue(){
		return getName();
	}
}
