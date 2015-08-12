package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jump.ColourRampStyle;

import java.util.Hashtable;
import java.util.Set;

import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileReader;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** Represents vector data with attribute values */
public class VectorData implements DataNT {

	private FeatureCollection vector;
	protected boolean toShow;
	private int visibleAttribute;
	private Hashtable<Integer, Feature> keyMap;
	private Hashtable<Integer, Data> attributeSchema;
	private int id;
	private int fidOffset;
	
	public VectorData(int id, String path){
		this.id = id;
		this.toShow = true;
		try{
			vector = new ShapefileReader().read(new DriverProperties(path));
		}catch(Exception e){
			// Report error on read failure
			System.out.println("Error reading features from " + path);
			e.printStackTrace();
		}
		// Set initial visible attribute to the feature ID
		visibleAttribute = Constants.getIdFromName(vector.getFeatureSchema().getAttributeName(1));
		// Build key map
		keyMap = new Hashtable<Integer, Feature>();
		// set fidOffset to lowest feature ID
		fidOffset = Integer.MAX_VALUE;
		for(Object o : vector.getFeatures()){
			if(o instanceof Feature){
				int fid = ((Feature)o).getID();
				if(fid < fidOffset) fidOffset = fid;
			}
		}
		// Assign features to keymap, adjusting id by offset to ensure counting from 0
		for(Object o : vector.getFeatures()){
			if(o instanceof Feature){
				Feature f = (Feature)o;
				keyMap.put(f.getID() - fidOffset, f);
			}
		}
		attributeSchema = new Hashtable<Integer, Data>();
	}

	/** Returns the set of keys used to identify features */
	public Set<Integer> getFeatureKeys(){
		return keyMap.keySet();
	}
	/** Returns the feature associated with this key */
	public Feature getFeature(int key){
		return keyMap.get(key);
	}
	/** Returns the offset between feature IDs and their corresponding local id */
	public int getFidOffset(){
		return fidOffset;
	}
	
	/** Adds an attribute based on this data's properties */
	public void addAttribute(Data attr){
//if(attr instanceof DoubleData) System.out.println("Adding attribute: " + attr + ", max=" + ((DoubleData)attr).getMax());
		attributeSchema.put(attr.getId(), attr);
		AttributeType type = AttributeType.OBJECT;
		Object initVal = null;
		if(attr instanceof Data0D.Integer){
			type = AttributeType.INTEGER;
			initVal = ((Data0D)attr).getValue();
		}
		else if(attr instanceof Data0D.Double){
			type = AttributeType.DOUBLE;
			initVal = ((Data0D)attr).getValue();
		}
		else if(attr instanceof DataTX){
			type = AttributeType.STRING;
			initVal = ((DataTX)attr).getText();
		}
		vector.getFeatureSchema().addAttribute(attr.getName(), type);
		// Workaround for Jumps inability to add attributes bug!
		for(Feature f : keyMap.values()){
			Object[] attrs = f.getAttributes();
			Object[] newAttrs = new Object[attrs.length + 1];
			for(int i = 0; i < attrs.length; i++)
				newAttrs[i] = attrs[i];
			newAttrs[newAttrs.length - 1] = initVal;
			f.setAttributes(newAttrs);
		}
	}
	/** Returns the idd attribute belonging to the given feature */
	public Object getAttribute(int attribute, int featureKey){
		// If this fails, search for the attribute int vector's table
		Feature f = keyMap.get(featureKey);
		if(f == null) return null;
		return f.getAttribute(Constants.getName(attribute));
	}
	/** Sets the idd attribute belonging to the given feature */
	public void setAttribute(int attribute, int featureKey, Object value){
		Feature f = keyMap.get(featureKey);
		if(f == null) return;
//int count = vector.getFeatureSchema().getAttributeCount();
//int i = f.getAttributes().length;
//Object[] attrs = f.getAttributes();
//System.out.println("Len:" + attrs.length + " count:" + count);
		f.setAttribute(Constants.getName(attribute), value);
	}

	/** Sets the attribute currently being visualised */
	public void setVisibleAttribute(int visibleAttribute){
		this.visibleAttribute = visibleAttribute;
	}
		
	/** Returns the attribute currently being visualised */
	public int getVisibleAttribute(){
		return visibleAttribute;
	}
	
	/** Returns the schema for the idd attribute */
	public Data getAttributeSchema(int attr){
		return attributeSchema.get(attr);
	}
	
	/** Constructor for cloned object */
	private VectorData(
			int id,
			FeatureCollection vector,
			boolean toShow,
			int visibleAttribute,
			Hashtable<Integer, Feature> keyMap,
			Hashtable<Integer, Data> attributeSchema
	){
		this.id = id;
		this.vector = vector;
		this.toShow = toShow;
		this.visibleAttribute = visibleAttribute;
		this.keyMap = (Hashtable<Integer, Feature>)keyMap.clone();
		this.attributeSchema = (Hashtable<Integer, Data>)attributeSchema.clone();
	}
	/** @inheritDoc */
	public Object clone(){
		return new VectorData(
				id,
				vector,
				toShow,
				visibleAttribute,
				keyMap,
				attributeSchema
		);
	}
	
	// Implementing Data and DataNT 
	
	/** @inheritDoc */
	public Object getData(){
	    return vector;
    }

	/** @inheritDoc */
	public void setData(Object data) {
		if(data instanceof FeatureCollection){
			vector = (FeatureCollection)data;
		}	    
    }
	
	/** @inheritDoc */
	public String getFormat(){
	    return "shp";
    }

	/** @inheritDoc */
	public boolean toShow() {
	    return toShow();
    }

	/** @inheritDoc */
	public int getId(){ return id; }
	
	/** @inheritDoc */
	public String getName() {
	    return Constants.getName(id);
    }

	/** @inheritDoc */
	public String stringValue() {
	    return vector.toString();
    }
	
	/** @inheritDoc */
	public String toString() {
	    return getName() + "=" + vector.toString();
    }

	/** Returns a style for the currently visualised object by applying the given parameters */
	public Style getStyle(DisplayParams params){
		// Retrive colour ramp if it exists
		Style s = params.getColourRamp(visibleAttribute);
//System.out.println("Attempting to find colour ramp:" + visibleAttribute);
		if(s != null){
			Data d = getAttributeSchema(visibleAttribute);
			// Try to find the min and max values if not present
//System.out.println("attr:" + visibleAttribute + "  schema:" + d + " max:"+ ((DoubleData)d).getMax() + " min:"+ ((DoubleData)d).getMin());
			if(d instanceof Data0D
			&& ((Data0D)d).hasRange()){
				double min = ((Data0D)d).getMin().doubleValue();
				double max = ((Data0D)d).getMax().doubleValue();
//System.out.println("Setting attr min and max");
				((ColourRampStyle)s).setMinMax(min, max);
			}
//System.out.println("RETURNING COLOR RAMP STYLE");
			return s;
		}
		// Retrive colour table if it exists
		s = params.getColourTable(visibleAttribute);
		if(s != null){
//System.out.println("RETURNING COLOR THEME STYLE");
			return s;
		}
//System.out.println("Dataset: " + id + " retriving style for:" + visibleAttribute);
		
		// Fall back to basic style
		Style basic = params.getStyle(visibleAttribute);
		if(basic != null) return basic;
		
		// Default to dataset's style
		return params.getStyle(id);
	}


}
