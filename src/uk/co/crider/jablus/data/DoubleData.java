package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.Utils;

/** @inheritDoc */
public class DoubleData implements Data0D.Double {

	protected int id;
	protected double value;
	protected String units;
	protected double min;
	protected double max;
	protected boolean hasRange;
	
	public DoubleData(int id              ){ this(id,     0, null ); }
	public DoubleData(int id, String units){ this(id,     0, units); }
	public DoubleData(int id, double value){ this(id, value, null ); }
	public DoubleData(int id, double value, String units){
		this.id = id;
		this.units = units;
		this.min = -java.lang.Double.MAX_VALUE;
		this.max = java.lang.Double.MAX_VALUE;
		hasRange = false;
		setValue(value);
	}
	public DoubleData(int id, double min, double max){
		this(id, 0, null, min, max); }
	public DoubleData(int id, String units, double min, double max){
		this(id, 0, units, min, max); }
	public DoubleData(int id, double value, double min, double max){
		this(id, value, null, min, max); }
	public DoubleData(int id, double value, String units, double min, double max){
		this.id = id;
		this.units = units;
		this.min = min;
		this.max = max;
		hasRange = true;
		setValue(value);
	}
	
	/** @inheritDoc */
	public int getId() {
	    return id;
    }

	/** @inheritDoc */
	public String getName() {
	    return Constants.getName(id);
    }
	
	/** @inheritDoc */
	public Number getValue() {
	    return value;
    }

	/** @inheritDoc */
	public double doubleValue() {
	    return value;
    }

	/** @inheritDoc */
	public String stringValue() {
	    return Utils.roundString(value);
    }
	
	/** @inheritDoc */
	public void setValue(double value){
		if(hasRange())
			this.value = value < min ? min : value > max ? max : value;
		else
			this.value = value;
	}
	
	/** @inheritDoc */
	public void setValue(Number value){
		setValue(value.doubleValue());
	}
	
	/** @inheritDoc */
	public String getUnits() {
		return units == null ? "" : units;
	}
	
	/** @inheritDoc */
	public boolean hasRange() {
	    return hasRange;
    }

	/** @inheritDoc */
	public void setRange(Number min, Number max){
		this.min = min.doubleValue();
		this.max = max.doubleValue();
		hasRange = true;
		setValue(value);
	}

	/** @inheritDoc */
	public java.lang.Double getMin() {
	    return min;
    }

	/** @inheritDoc */
	public java.lang.Double getMax() {
	    return max;
    }

	/** Equal if they have the same id and value */
	public boolean equals(Object o){
		if(o instanceof IntegerData){
			DoubleData d = (DoubleData)o;
			return getId() == d.getId()
			&& getValue().equals(d.getValue());
		}
		return false;
	}

	/** @inheritDoc */
	public Object clone(){
		return new DoubleData(id, value, units, min, max);
	}

	/** @inheritDoc */
	public String toString(){
		return getName() + "(" + Utils.roundString(value) + ")";
	}
	
}
