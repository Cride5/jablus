package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;

/** @inheritDoc */
public class IntegerData implements Data0D.Integer {

	protected int id;
	protected int value;
	protected String units;
	protected int min;
	protected int max;
	protected boolean hasRange;
	
	public IntegerData(int id){ this(id, 0, null); }
	public IntegerData(int id, String units){ this(id, 0, units); }
	public IntegerData(int id, int value){ this(id, value, null); }
	public IntegerData(int id, int value, String units){
		this.id = id;
		this.units = units;
		this.min = java.lang.Integer.MIN_VALUE;
		this.max = java.lang.Integer.MAX_VALUE;
		hasRange = false;
		setValue(value);
	}
	public IntegerData(int id, int min, int max){
		this(id, 0, null, min, max); }
	public IntegerData(int id, String units, int min, int max){
		this(id, 0, units, min, max); }
	public IntegerData(int id, int value, int min, int max){
		this(id, value, null, min, max); }
	public IntegerData(int id, int value, String units, int min, int max){
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
	public String getName(){
		return Constants.getName(id);
	}
	
	/** @inheritDoc */
	public Number getValue() {
	    return value;
    }
	
	/** @inheritDoc */
	public int intValue() {
	    return value;
    }
		
	/** @inheritDoc */
	public String stringValue() {
	    return ""+value;
    }

	/** @inheritDoc */
	public void setValue(int value){
		if(hasRange())
			this.value = value < min ? min : value > max ? max : value;
		else
			this.value = value;
	}
	
	/** @inheritDoc */
	public void setValue(Number value){
		setValue(value.intValue());
	}
	
	/** @inheritDoc */
	public String getUnits() {
	    return units == null ? "" : units;
    }	

	/** @inheritDoc */
/*	public String getRangeUnits() {
		if(units == null && !hasRange()) return "";
	    return " [" +
	    (hasRange()                  ? min + "-" + max : "") +
	    (hasRange() && units != null ? " "             : "") +
	    (units != null               ? units           : "") +
	    "]";
    }	
*/
	
	/** @inheritDoc */
	public boolean hasRange() {
	    return hasRange;
    }

	/** @inheritDoc */
	public void setRange(Number min, Number max){
		this.min = min.intValue();
		this.max = max.intValue();
		hasRange = true;
		setValue(value);
	}

	/** @inheritDoc */
	public java.lang.Integer getMin() {
	    return min;
    }

	/** @inheritDoc */
	public java.lang.Integer getMax() {
	    return max;
    }

	/** Equal if they have the same id and value */
	public boolean equals(Object o){
		if(o instanceof IntegerData){
			IntegerData i = (IntegerData)o;
			return getId() == i.getId()
			&& getValue().equals(i.getValue());
		}
		return false;
	}
	
	/** @inheritDoc */
	public Object clone(){
		return new IntegerData(id, value, units, min, max);
	}

	/** @inheritDoc */
	public String toString(){
		return getName() + "(" + value + ")";
	}
}
