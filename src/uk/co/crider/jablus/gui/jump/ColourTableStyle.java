package uk.co.crider.jablus.gui.jump;

import uk.co.crider.jablus.Constants;

import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.Map;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** An extension of Jump's style to represent a colour table.
 * Maps any object to a given style */
public class ColourTableStyle implements Style {
	
	private int attribute;
	private Map<Object, BasicStyle> table;
	private BasicStyle defStyle;
	private boolean enabled;
	
	/** Colours named attribute by a linear interpolation between c1 and c2 */
	public ColourTableStyle(int attribute){ this(attribute, new Hashtable<Object, BasicStyle>(), new BasicStyle()); }
	public ColourTableStyle(int attribute, Map<Object, BasicStyle> table){ this(attribute, table, new BasicStyle()); }
	public ColourTableStyle(int attribute, Map<Object, BasicStyle> table, BasicStyle defStyle){
		this.attribute = attribute;
		this.table = table;
		this.defStyle = defStyle;
		enabled = true;
	}
	
	public void initialize(Layer arg0){ ; }
	
	public boolean isEnabled(){ return enabled; }

	public void paint(Feature f, Graphics2D g, Viewport view)
	        throws Exception {
		Object o = f.getAttribute(Constants.getName(attribute));
//System.out.println("Retrived attribute value: " + o);
		BasicStyle s = table.get(o);
		if(s == null) s = defStyle;
		s.paint(f, g, view);
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public Map<Object, BasicStyle> getTable(){
		return table;
	}
	
	public Object clone(){
		return new ColourTableStyle(attribute, table, (BasicStyle)defStyle.clone());
	}

}
