package uk.co.crider.jablus.models.basic.agent;

import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

/** Represents a land use arrangement */
public class LandUseScheme implements LandUseProfile{
			
	/** Reference to land use cells by location */
	private TreeMap<LandCell, LandUseCell> landUseMap;

	/** Construct a new land use scheme */
	public LandUseScheme(Set<LandCell> land, Parameters params){
		this.landUseMap = new TreeMap<LandCell, LandUseCell>();
		for(LandCell cell : land){
			LandUseCell useCell = new LandUseCell(cell.x, cell.y, params, null);
			landUseMap.put(cell, useCell);
		}

//		this.ratio = 0.0; 
//		this.distribution = 1.0;
	}

	public void setUse(LandCell cell, int use){
		landUseMap.get(cell).setNewUse(use);
	}

	// Implementing LandUseProfile ---------------------------
	
	/** @inheritDoc */
	public Collection<LandUseCell> getLandUse(){
	    return landUseMap.values();
    }

	/** @inheritDoc */
	public int cellsChanged(){
		int changed = 0;
		for(LandUseCell cell : landUseMap.values()){
			if(cell.useChanged())
				changed++;
		}
		return changed;
    }
	


}
