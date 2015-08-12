package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.Utils;

import java.util.Hashtable;

/** @inheritDoc */
public class DoubleData1D implements Data1D.Double{

	protected int id;
	protected double[] values;
	protected String[] rawHeader;
	protected String[] csvHeader;
	protected String units;
	
	public DoubleData1D(int id, int len){ this(id, null, new double[len], null); }
	public DoubleData1D(int id, String[] rawHeader, String units){ this(id, rawHeader, new double[rawHeader.length], units); }
	public DoubleData1D(int id, String[] rawHeader, double[] values){ this(id, rawHeader, values, null); }
	public DoubleData1D(int id, String[] rawHeader, double[] values, String units){
		this.id = id;
		this.rawHeader = rawHeader;
		genHeaders();
		this.values = values;
		this.units = units == null ? "" : units;
	}
	public DoubleData1D(int id, double[] values){
		this.id = id;
		this.values = values;
	}
	public DoubleData1D(int id, String[] rawHeader, Hashtable<String, java.lang.Integer> values){
		this.id = id;
		this.rawHeader = rawHeader;
		genHeaders();
		// Populate values
		this.values = new double[values.size()];
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
	public double getValue(int i){
		if(i >= 0 && i < this.values.length)
			return values[i];
		return 0;
	}

	/** @inheritDoc */
	public double[] getValues(){
		return values;
	}

	/** @inheritDoc */
	public String stringValue() {
	    return Utils.arrayStringR(values);
    }

	/** @inheritDoc */
	public String stringValue(int i) {
		if(i >= 0 && i < this.values.length)
			return "" + Utils.round(values[i]);
		return null;
    }

	/** @inheritDoc */
	public void setValue(int i, double value){
		if(i >= 0 && i < this.values.length)
			values[i] = value;
	}

	/** @inheritDoc */
	public void setValues(double[] values){
		int len = this.values.length > values.length ? values.length : this.values.length;
		for(int i = 0; i < len; i++){
			this.values[i] = values[i];
		}
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
		if(o instanceof DoubleData1D){
			DoubleData1D d = (DoubleData1D)o;
			if(getId() != d.getId()) return false;
			double[] v = d.getValues();
			if(v.length != values.length) return false;
			for(int i = 0; i < v.length; i++)
				if(v[i] != values[i]) return false;
			return true;
		}
		return false;
	}

	/** @inheritDoc */
	public Object clone(){ return new DoubleData1D(id, values, rawHeader, csvHeader, units); }
	/** Constructor for cloning this object */ 	
	protected DoubleData1D(int id, double[] values, String[] rawHeader, String[] csvHeader, String units){
		this.id = id;
		this.values = new double[values.length];
		for(int i = 0; i < values.length; i++)
			this.values[i] = values[i];
		this.rawHeader = rawHeader;
		this.csvHeader = csvHeader;
		this.units = units;
	}

	/** @inheritDoc */
	public String toString(){
		return getName() + "(" + Utils.arrayStringR(values) + ")";
	}
}
