package uk.co.crider.jablus.agent;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.utils.Utils;

public class Action{
	
	public final int id;
	public final Data[] args;
	
	public Action(int id){ this(id, new Data[0]); }
	public Action(int id, Data arg){ this(id, new Data[]{arg}); }
	public Action(int id, Data[] args){
		this.id = id;
		this.args = args;
	}
	
	public String toString(){
		return "Action("+ Constants.getName(id) + ", " + Utils.arrayString(args) + ")";
	}
	
	/** Returns true if two actions are the same and have the same arguements */
	public boolean equals(Object o){
		if(o instanceof Action){
			Action a = (Action)o;
			if(id != a.id)
				return false;
			if(args.length != a.args.length)
				return false;
			for(int i = 0; i < args.length; i++)
				if(!args[i].equals(a.args[i]))
					return false;
			return true;
		}
		return false;
	}
	
	/** Set this so that actions with the same name are given the same hash code */
	public int hashCode(){
		return id;
	}
	
	public static boolean isAction(Action a, int[] actions){
		for(int n : actions)
			if(a.id == n) return true;
		return false;
	}
	
	/** Constructor for clone objects */
	private Action(Action a){
		this.id = a.id;
		this.args = new Data[a.args.length];
		for(int i = 0; i < args.length; i++){
			this.args[i] = (Data)a.args[i].clone();
		}
	}
	/** Carry out a deep clone of the object */
	public Object clone(){
		return new Action(this);
	}
}
