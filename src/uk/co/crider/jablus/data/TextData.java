package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;

/** @inheritDoc */
public class TextData implements DataTX {

	private int id;
	private String text;
	
	public TextData(int id){ this(id, null); }
	/** Constructor for cloned objects */ 
	public TextData(int id, String text){
		this.id = id;
		if(text == null)
			this.text = "";
		else
			this.text = text;
	}
	/** Equal if they have the same id and value */
	public boolean equals(Object o){
		if(o instanceof TextData){
			TextData d = (TextData)o;
			return getId() == d.getId()
			&& getText().equals(d.getText());
		}
		return false;
	}

	/** Clone the object */
	public Object clone(){
		return new TextData(id, text);
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
	public void setText(String text){
		if(text == null)
			this.text = "";
		else
			this.text = text;
	}
	
	/** @inheritDoc */
	public String stringValue() {
	    return getText();
    }

	/** @inheritDoc */
	public String getText() {
	    return text;
    }

	/** @inheritDoc */
	public String toString(){
		return getName()  + "(" + text + ")";
	}

}
