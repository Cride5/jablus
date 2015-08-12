/*
 * Created on Mar 2, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.co.crider.jablus.models.basic.env.land;


import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.models.basic.Parameters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;


/** Represents a rectangular land cell */
public class LandCell extends Point implements Comparable {

	/** Unique class ID */
    private static final long serialVersionUID = -4186197992632048912L;

	public static final GeometryFactory GEOM_FACTORY = new GeometryFactory(); 
	
	public final int x;
	public final int y;
	
	// Creates a land cell at a random location in the land area
//	public LandCell(){ this((int)(Math.random() * Constants.ROWS), (int)(Math.random() * Constants.COLS)); }
	public LandCell(int x, int y){
		super(
				new CoordinateArraySequence(new Coordinate[]{ new Coordinate(x, y) }),
				GEOM_FACTORY
		);
		this.x = x;
		this.y = y;
	}
	
	/** returns the land cell upon which an agent is located */
	public static LandCell getCellWith(Agent agent, Parameters params){
		return new LandCell(
				params.AGENT_LOCATIONS[agent.getId()].x,
				params.AGENT_LOCATIONS[agent.getId()].y);
	}
	
	public boolean equals(Object o){
//System.out.println("Calling LandCell.equals");
		if(o instanceof LandCell){
			LandCell cell = (LandCell)o;
			return x == cell.x && y == cell.y;
		}
		return false;
	}
	
	public int compareTo(Object o) {
		if(o instanceof LandCell){
			LandCell cell = (LandCell)o;
			if(y > cell.y) return 1;
			if(y < cell.y) return -1;
			if(x > cell.x) return 1;
			if(x < cell.x) return -1;
		}
		return 0;
	}
	
	public int hashCode(){
		return y << 16 + x;
		// Implies a maximum of 2^16-1=65535 for x and y
	}

	public String toString(){
		return "(" + x + "," + y + ")";
	}
	
	public static void main(String[] args) {
		
		// Test data
		LandCell c1 = new LandCell(1, 1);
		LandCell c2 = new LandCell(1, 1);
		System.out.println("c1 & c2 equal:" + c1.equals(c2));
		System.out.println(c1.hashCode());
		System.out.println(c2.hashCode());

		// Test hash set
		java.util.HashSet<LandCell> hashSet = new java.util.HashSet<LandCell>();
		hashSet.add(c1);
		hashSet.add(c1);
		hashSet.add(c2);
		System.out.println("SIZE:" + hashSet.size());

		// Test  TreeSet
		java.util.TreeSet<LandCell> treeSet = new java.util.TreeSet<LandCell>();
		treeSet.add(c1);
		treeSet.add(c1);
		treeSet.add(c2);
		System.out.println("SIZE:" + treeSet.size());
		
		// Submitted bug report to sun as hashset does not work as defined in API
		// It treats objects where o1 == o2 as the same rather than o1.equals(o2)
		Object o1 = new java.io.File("foo");
		Object o2 = new java.io.File("foo");
		System.out.println(o1.hashCode());
		System.out.println(o2.hashCode());
		java.util.HashSet<Object> set = new java.util.HashSet<Object>();
		System.out.println("o1.equals(o2):" + o1.equals(o2) + ", o1==o2:" + (o1 == o2));
		set.add(o1);
		System.out.println("HashSet contains o2: " + set.contains(o2));
		set.add(o2);
		System.out.println("HashSet contains o2: " + set.contains(o2) + " " + set.size());
	}
}
