package uk.co.crider.jablus.env;

import uk.co.crider.jablus.data.DataSet;

/** A sub-type of Dynamic driver which are endogeneous (ie, affected by other drivers) */
public interface DriverEndogeneous {

	/** Initialise driver with list of drivers which affect this one */
	public void init(DataSet drivers);

}
