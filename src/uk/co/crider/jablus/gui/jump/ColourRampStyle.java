package uk.co.crider.jablus.gui.jump;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.ColourMixer;

import java.awt.Color;
import java.awt.Graphics2D;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** An extension of Jump's style to represent a colour ramp.
 * Maps a numeric value to a colour gradient with any number of colour reference points */
public class ColourRampStyle implements Style {
	
	private static final BasicStyle defStyle = new BasicStyle(Color.WHITE);
	
	private int attribute;
	private String units;
	private Color[] col;
	private double minVal;
	private double maxVal;
	private boolean enabled;
	
	/** Colours named attribute by a linear interpolation between c1 and c2 */
	public ColourRampStyle(int attribute, String units, Color[] col){
		this(attribute, units, col, 0, 1); }
	public ColourRampStyle(int attribute, String units, Color[] col, double minVal, double maxVal){
		this.attribute = attribute;
		this.units = units;
		this.col = col.length < 2 ? new Color[]{Color.WHITE, Color.BLACK} : col;
		this.minVal = minVal;
		this.maxVal = maxVal;
		enabled = true;
	}
	
	public void initialize(Layer arg0){ ; }

	public void setMinMax(double minVal, double maxVal){
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public boolean rangeExists(){ return minVal < maxVal; }
	
	public boolean isEnabled(){ return enabled; }

	public void paint(Feature f, Graphics2D g, Viewport view)
	        throws Exception {
		Object o = f.getAttribute(Constants.getName(attribute));
//System.out.println("Retrived attribute value: " + o);
//System.out.println("min=" + minVal + ", max=" + maxVal);
		if(o instanceof Number && maxVal != minVal){
			double v = ((Number)o).floatValue();
//System.out.println("Painting attribute d: " + v + ", min:" + minVal + ", max: "+ maxVal);
			double p = (v - minVal) / (maxVal - minVal);
//System.out.println("Painting attribute: " + v + ", min:" + minVal + ", max: "+ maxVal + ", percent:" + p);
			new BasicStyle(ColourMixer.interpolate(p, col)).paint(f, g, view);
		}else
			defStyle.paint(f, g, view);
	}
	
	public double getMin(){ return minVal; }
	public double getMax(){ return maxVal; }
	public String getUnits(){ return units; }
	public Color[] getCol(){ return col; }

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public Object clone(){
		return new ColourRampStyle(attribute, units, col, minVal, maxVal);
	}

}
