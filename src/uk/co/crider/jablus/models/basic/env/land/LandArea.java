package uk.co.crider.jablus.models.basic.env.land;

import java.util.Set;
import java.util.TreeSet;

/** Represents a rectangular area of land */
public class LandArea {
	
	public final int x1, y1, x2, y2;
	
	public LandArea(int x1, int y1, int x2, int y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/** Returns the list of ells contained within the rectangle defined by x1, y1, x2, y2 */
	public static Set<LandCell> getCellsRect(int x1, int y1, int x2, int y2){
		Set<LandCell> cells = new TreeSet<LandCell>();
		for(int j = y1; j <= y2; j++){
			for(int i = x1; i <= x2; i++){
				cells.add(new LandCell(i, j));
			}
		}
		return cells;
	}

}
