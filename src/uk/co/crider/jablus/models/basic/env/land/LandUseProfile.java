package uk.co.crider.jablus.models.basic.env.land;

import uk.co.crider.jablus.models.basic.agent.LandUseCell;

import java.util.Collection;

/** Represents a land use scheme. An object implementing this
 * interface is returned by agents to communicate their land use selection */
public interface LandUseProfile {
	
	/** Returns full land use pattern */
	public Collection<LandUseCell> getLandUse();

	/** Returns the number of cells which changed since last time */
	public int cellsChanged();
	
}
