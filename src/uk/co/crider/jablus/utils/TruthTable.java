package uk.co.crider.jablus.utils;

import java.util.Hashtable;

/** Represents a table of truth values */
public class TruthTable<K>{
	
	private Hashtable<K, Boolean> truth;
	
	public TruthTable(){
		truth = new Hashtable<K, Boolean>();
	}
	
	/** Returns whether the object is true, returns false unless item exists and has been set to true */
	public boolean isTrue(K o){
		if(o == null || !truth.containsKey(o)) return false;
		return truth.get(o);
	}
	
	public void set(K o, boolean truthValue){
		if(o == null) return; 
		if(truth.contains(o)){
			if(truth.get(o).equals(new Boolean(truthValue)))
				return;
			truth.remove(o);
		}
		truth.put(o, truthValue);
	}

}
