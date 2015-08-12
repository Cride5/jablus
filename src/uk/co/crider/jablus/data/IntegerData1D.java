package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.Utils;

import java.util.Hashtable;

/** @inheritDoc */
public class IntegerData1D implements Data1D.Integer{

	private int id;
	private int[] values;
	private String[] rawHeader;
	private String[] csvHeader;
	private String units;
	
	public IntegerData1D(int id, int len){ this(id, null, new int[len], null); }
	public IntegerData1D(int id, String[] rawHeader, String units){ this(id, rawHeader, new int[rawHeader.length], units); }
	public IntegerData1D(int id, String[] rawHeader, int[] values){ this(id, rawHeader, values, null); }
	public IntegerData1D(int id, String[] rawHeader, int[] values, String units){
		this.id = id;
		this.rawHeader = rawHeader;
		genHeaders();
		this.values = values;
		this.units = units == null ? "" : units;
	}
	public IntegerData1D(int id, int[] values){
		this.id = id;
		this.values = values;
	}
	public IntegerData1D(int id, String[] rawHeader, Hashtable<String, java.lang.Integer> values){
		this.id = id;
		this.rawHeader = rawHeader;
		genHeaders();
		// Populate values
		this.values = new int[values.size()];
		for(int i = 0; i < this.values.length; i++){
			this.values[i] = values.get(csvHeader[i]);
		}
	}
	private void genHeaders(){
		csvHeader = new String[rawHeader.length];
		for(int i = 0; i < csvHeader.length; i++){
			if(rawHeader[i] == null || rawHeader[i].equals("")){
				rawHeader[i] = "" + i;
				csvHeader[i] = id + Constants.SEPARATOR + i;
			}
			else
				csvHeader[i] = id + Constants.SEPARATOR + rawHeader[i];
		}
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
	public int getLength() {
	    return values.length;
    }
	
	/** @inheritDoc */
	public int getValue(int i){
		if(i >= 0 && i < this.values.length)
			return values[i];
		return 0;
	}

	/** @inheritDoc */
	public int[] getValues(){
		return values;
	}

	/** @inheritDoc */
	public String stringValue() {
	    return Utils.arrayString(values);
    }

	/** @inheritDoc */
	public String stringValue(int i) {
		if(i >= 0 && i < this.values.length)
			return "" + values[i];
		return null;
    }

	/** @inheritDoc */
	public void setValue(int i, int value){
		if(i >= 0 && i < this.values.length)
			values[i] = value;
	}

	/** @inheritDoc */
	public void setValues(int[] values){
		int len = this.values.length > values.length ? values.length : this.values.length;
		for(int i = 0; i < len; i++)
			this.values[i] = values[i];
	}

	/** @inheritDoc */
	public String getUnits() {
	    return units;
    }

	/** @inheritDoc */
	public String[] getRawHeader() {
	    return rawHeader;
    }

	/** @inheritDoc */
	public String[] getCSVHeader() {
	    return csvHeader;
    }

	/** Equal if they have the same id and values for all elements */
	public boolean equals(Object o){
		if(o instanceof IntegerData1D){
			IntegerData1D d = (IntegerData1D)o;
			if(getId() != d.getId()) return false;
			int[] v = d.getValues();
			if(v.length != values.length) return false;
			for(int i = 0; i < v.length; i++)
				if(v[i] != values[i]) return false;
			return true;
		}
		return false;
	}

	/** @inheritDoc */
	public Object clone() {
		int[] values = new int[this.values.length];
		for(int i = 0; i < values.length; i++){
			values[i] = this.values[i];
		}
	    return new IntegerData1D(id, rawHeader, values);
    }

	/** @inheritDoc */
	public String toString(){
		return getName() + "(" + Utils.arrayString(values) + ")";
	}
}
