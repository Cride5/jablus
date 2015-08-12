package uk.co.crider.jablus.utils;

import java.awt.Color;

public final class ColourMixer {

	/** Returns a linearly interpolated colour based on the input array.
	 * the value p (between 0 and 1) represents the position along
	 * the colour ramp to read from. */
	public static final Color interpolate(double p, Color[] col){
		p = p < 0 ? 0 : p > 1 ? 1 : p;
		Color c = p == 0 ? col[0] :
			      p == 1 ? col[col.length - 1] : null;
//if(f.getID() == 4){System.out.println("p=" + p);}
		if(c == null){
			int i = (int)((col.length - 1) * p);
			double p1 = (double)i / (col.length - 1);
			double p2 = (double)(i + 1) / (col.length - 1);
			p = (p - p1) / (p2 - p1);
			c = new Color(
					col[i].getRed()   + (int)((col[i + 1].getRed()   - col[i].getRed())   * p),
					col[i].getGreen() + (int)((col[i + 1].getGreen() - col[i].getGreen()) * p),
					col[i].getBlue()  + (int)((col[i + 1].getBlue()  - col[i].getBlue())  * p)
				);
		}
		return c;
	}

	/** For testing */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
