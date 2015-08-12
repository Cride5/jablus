/*
 * Created on Feb 16, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.co.crider.jablus.models.basic.env;



import uk.co.crider.jablus.models.basic.Parameters;

import java.awt.Color;


/**
 * @author cride5
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoadEdge {
	
	// Road grades
	public static final int MOTORWAY = 0;
	public static final int A_ROAD   = 1;
	public static final int B_ROAD   = 2;
	public static final int TRACK    = 3;
	public static final int NO_ROAD  = 4;
	public static final int INTERNAL = 5;
	
	// Road colours
	public static final Color[] COLOUR = new Color[]{
			new Color( 64,  64,  64), // Motorway
			new Color( 96,  96,  96), // A_ROAD
			new Color(128, 128, 128), // B_ROAD
			new Color(160, 160, 160), // Track
			new Color(192, 192, 192), // No road
			new Color(224, 224, 224)  // Internal
	};
	private Parameters params;
	private RoadNode n1;
	private RoadNode n2;
	private int grade;
	
	/** Construct a road */
	public RoadEdge(Parameters params, RoadNode n1, RoadNode n2, int grade){
		this.params = params;
		this.n1 = n1;
		this.n2 = n2;
		this.grade = grade;
	}
	
	public RoadNode getN1(){
		return n1;
	}
	
	public RoadNode getN2(){
		return n2;
	}
	
	public int getGrade(){
		return grade;
	}
	
	/** Returns the node adjacent to n */
	public RoadNode adjacentNode(RoadNode n){
		if(n == n1) return n2;
		return n1;
	}
	
	/** Returns the cost factor associated with this road */
	public double costDistance(){
		return n1.distance(n2) / params.roadSpeeds[grade];
	}

}
