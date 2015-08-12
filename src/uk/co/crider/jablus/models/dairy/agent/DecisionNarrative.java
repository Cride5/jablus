package uk.co.crider.jablus.models.dairy.agent;

import uk.co.crider.jablus.data.TextData;
import uk.co.crider.jablus.models.dairy.Constants;

public class DecisionNarrative extends TextData {
	
	public final String what;
	public final String why;
	
	public DecisionNarrative(String what, String why){
		super(Constants.OUTPUT_NARRATIVE);
		this.what = what;
		this.why = why;
	} 
	
	public String getText(){
		return "Decision: " + what + " - Reason: " + why;
	}
	
	public String toString(){
		return "DecisionNarrative(" + getText() + ")";
	}
}
