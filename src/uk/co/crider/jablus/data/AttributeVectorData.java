package uk.co.crider.jablus.data;

import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jump.ColourRampStyle;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileReader;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** Represents vector data with attribute values */
public class AttributeVectorData extends CompoundData implements DataNT {

	private FeatureCollection vector;
	protected boolean toShow;
	private int visibleAttribute;
	/** Data value currently being visualised */
//	private String visualData;
	
	private Hashtable<Integer, CompoundData> attributes; 
	
	public AttributeVectorData(int id, String path, boolean hasFile, boolean toShow){
		super(id, hasFile);
		this.toShow = toShow;
		try{
			vector = new ShapefileReader().read(new DriverProperties(path));
		}catch(Exception e){
			// Report error on read failure
			System.out.println("Error reading features from " + path);
			e.printStackTrace();
		}
		key = Integer.parseInt(vector.getFeatureSchema().getAttributeName(1));
		visibleAttribute = key;
		attributes = new Hashtable<Integer, CompoundData>();
		for(Object o : vector.getFeatures()){
			if(o instanceof Feature){
				attributes.put((Integer)((Feature)o).getAttribute(key), new CompoundData(0));
			}
		}
		

	}

	public Object getData(){
	    return vector;
    }

	public String getFormat() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void setData(Object data) {
		if(data instanceof FeatureCollection){
			vector = (FeatureCollection)data;
		}	    
    }
	
	public Set<Integer> getFeatureKeys(){
		return attributes.keySet();
	}
	
	/** Adds an attribute based on this data's properties */
	public void addAttribute(Data def){
		for(Integer i : attributes.keySet()){
			attributes.get(i).addItem((Data)def.clone());
		}
	}
	
	public Object getAttribute(int id, int feature){
		// Try to retrive attribute from this table
		Data item = attributes.get(feature).getItem(id); 
		if(item != null)
			return item;
		// If this fails, search for the attribute int vector's table
		if(!vector.getFeatureSchema().hasAttribute("" + id))
			return null;
		for(Object o : vector.getFeatures()){
			if(o instanceof Feature){
				Feature f = (Feature)o;
				if(f.getAttribute(key).equals(feature))
					return f.getAttribute(id);
			}
		}
		return null;
	}
	public void setAttribute(String name, int feature, Data value){
		CompoundData row = attributes.get(feature);
		if(row != null){
//System.out.println("Setting attribute:" + name + " feature:" + feature + " value:" + value);
			row.setItem(value);
		}
	}

	public boolean toShow() {
	    return toShow();
    }
	
	// Generates an area attribute
	public void genAreaAttribute(int id){
		DoubleData schema = new DoubleData(id);
		addAttribute(schema);
		for(Object o : vector.getFeatures()){
			if(o instanceof Feature){
				Feature f = (Feature)o;
				int fid = (Integer)f.getAttribute(key);
				setAttribute(""+id, fid, new DoubleData(id, f.getGeometry().getArea())); 
			}
		}
	}
	
	public void setVisibleAttribute(int visibleAttribute){
		this.visibleAttribute = visibleAttribute;
	}
	
	/** Returns a style for the currently visualised object by applying the given parameters */
	public Style getStyle(DisplayParams params){
		ColourRampStyle s = params.getColourRamp(visibleAttribute);
		if(s != null){
			Object o = attributes.elements().nextElement().getItem(id); 
			//Object o = getAttribute(visibleAttribute);
			if(o != null && o instanceof Data0D)
				s.setMinMax(((Data0D)o).getMin().doubleValue(), ((Data0D)o).getMax().doubleValue());
			return s;
		}
System.out.println("Dataset: " + getName() + " retriving style for:" + visibleAttribute);
/*		float[] colourRamp = params.getColourRamp(visibleAttribute);
		if(colourRamp != null){
			return getStyle(visibleAttribute,
					colourRamp[0],
					colourRamp[1],
					colourRamp[2],
					colourRamp[3],
					colourRamp[4],
					colourRamp[5]);
		}
		Map<Integer, Color> colourTable = params.getColourTable(visibleAttribute);
		if(colourTable != null){
			return getStyle(visibleAttribute, colourTable);
		}
		Style basic = params.getStyle(visibleAttribute);
		if(basic != null)
			return basic;
		
*/		// Default to dataset's style
		return params.getStyle(id);
	}
	
	/** Returns a style to display the selected attribute on this vectormap
	 * @param visAttr
	 * @param colourMap
	 * @return a style
	 */
	Style getStyle(int visAttr, Map<Integer, Color> colourMap){
		Hashtable<Integer, BasicStyle> map = new Hashtable<Integer, BasicStyle>();
		for(Integer i : attributes.keySet()){
			Object o = getAttribute(visAttr, i);
			int v = 0;
			if(o == null)
				v = 0;
			else if(o instanceof DoubleData)
				v = (int)((DoubleData)o).doubleValue();
			else if(o instanceof IntegerData)
				v = ((IntegerData)o).intValue();
			else if(o instanceof Integer)
				v = (Integer)o;
			else if(o instanceof Double)
				v = (int)((Double)o).doubleValue();
			else if(o instanceof Float)
				v = (int)((Float)o).floatValue();
			else{
				System.out.println("AttribVectorData: Cannot visualise non numerical values (field:" + visAttr + " in: " + id + ")");
				return null;
			}
			map.put(i, new BasicStyle(colourMap.get(v)));
		}
		BasicStyle def = new BasicStyle(Color.BLACK);
		return new ColorThemingStyle("" + key, map, def); 
	}

	/** Returns a style to display the selected attribute on this vectormap */
	Style getStyle(int visAttr, float r1, float r2, float g1, float g2, float b1, float b2){
		// Gather list of attributes
		Hashtable<Integer, Double> attrib = new Hashtable<Integer, Double>();
		double minAttr = Double.MAX_VALUE;
		double maxAttr = Double.MIN_VALUE;
		for(Integer i : attributes.keySet()){
			Object o = getAttribute(visAttr, i);
			double v = 0;
			if(o == null)
				v = 0;
			else if(o instanceof DoubleData)
				v = ((DoubleData)o).doubleValue();
			else if(o instanceof IntegerData)
				v = ((IntegerData)o).intValue();
			else if(o instanceof Integer)
				v = (Integer)o;
			else if(o instanceof Double)
				v = (Double)o;
			else if(o instanceof Float)
				v = (Float)o;
			else{
				System.out.println("AttribVectorData: Cannot visualise non numerical values (field:" + visAttr + " in: " + id + ")");
				return null;
			}
			attrib.put(i, v);
			if(v > maxAttr)
				maxAttr = v;
			if(v < minAttr)
				minAttr = v;
		}
		Hashtable<Integer, BasicStyle> map = new Hashtable<Integer, BasicStyle>();
		for(Integer id : attrib.keySet()){
			float prop = (float)((attrib.get(id) - minAttr) / (maxAttr - minAttr));
//System.out.println("Dislplaying:" + prop);
			map.put(id, new BasicStyle(new Color(
					r1 + prop * (r2 - r1),
					g1 + prop * (g2 - g1),
					b1 + prop * (b2 - b1)
			)));
		}
		BasicStyle def = new BasicStyle(Color.BLACK);
		return new ColorThemingStyle("" + key, map, def); 
	}
	
}
