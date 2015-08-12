package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.models.dairy.RandomGenerator;

/** Represents the price of a commodity.
 * The price can be fixed, or vary according to a basic trend with random perturbations. */
public class PriceTrend extends DoubleData{

	private RandomGenerator rand;
	private double scaleFactor = 1;
	
	public PriceTrend(int id, double value, String units){
		this(id, value, units, null);
	}
	public PriceTrend(int id, double initValue, String units, RandomGenerator rand){
		super(id, initValue, Constants.UNITS_CURRENCY + "/" + units);
		this.rand = rand;
	}
	public PriceTrend(int id, double initValue, String units, RandomGenerator rand, double scaleFactor){
		super(id, initValue, Constants.UNITS_CURRENCY + "/" + units);
		this.scaleFactor = scaleFactor;
		this.rand = rand;
	}

	public RandomGenerator getRand(){
		return rand;
	}
	/** Whether this price changes */
	public boolean isStatic(){
		return rand == null;
	}
	
	/** Set price trend to static */
	public void setStatic(){
		rand = null;
	}
	
	/** @inheritDoc */
	public void next(){
		if(rand != null)
			setValue(rand.next() * scaleFactor);
	}

}
