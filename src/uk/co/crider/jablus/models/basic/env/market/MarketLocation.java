package uk.co.crider.jablus.models.basic.env.market;

import uk.co.crider.jablus.data.Data;
import jwo.landserf.structure.VectorMap;

/** Represents the location of a market */
public class MarketLocation implements Data {

	protected int id = uk.co.crider.jablus.Constants.getSerialId();
	public int getId(){ return id; }
	
	private String name;

	public MarketLocation(String name){
		this.name = name;
	}
	
	public String getName() {
	    return name;
    }
	
	public String stringValue(){
		return getName();
	}

	public VectorMap getData() {
	    return null;
    }
	
	/** @inheritDoc */
	public Object clone(){ return new MarketLocation(name); }
	
}
