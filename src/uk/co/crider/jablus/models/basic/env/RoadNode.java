/*
 * Created on Feb 16, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.co.crider.jablus.models.basic.env;



import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * @author cride5
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoadNode extends Point {

	/** Unique class ID */
    private static final long serialVersionUID = 2147569527432036906L;
    
	public static final int MARKET = 0;
	public static final int JUNCTION = 1;
	public static final Color MARKET_COLOUR = new Color(255, 255, 255);
	
	/** Constructs a RoadNode of type RoadNode.JUNCTION */
	public RoadNode(Coordinate coord){
		this(coord, JUNCTION);
	}
	public RoadNode(Coordinate coord, int type){
		super(
				new CoordinateArraySequence(new Coordinate[]{ coord }),
				new GeometryFactory()
		);
		if(type == MARKET)
			distFromMarket = 0;
		else
			distFromMarket = -1;
		edges = new LinkedList<RoadEdge>();
	}

	// All edges leading from this node
	private List<RoadEdge> edges;
	
	// Distance from market town, -1.0 if not calculated yet
	private double distFromMarket;
	
	/** Add a rode edge leading from this this node */
	public void addEdge(RoadEdge e){
		edges.add(e);
	}
	
	public void setMarketDist(double distFromMarket){
		this.distFromMarket = distFromMarket;
	}

	/** Returns the distance to the nearest market */
	public double getMarketDist(){
/*		if(distFromMarket < 0){
			// Find closest node
			double minDist = Double.MAX_VALUE;
			for(Iterator i = edges.iterator(); i.hasNext();){
				RoadEdge road = (RoadEdge)i.next();
				RoadNode next = road.adjacentNode(this);
				double dist = this.distance(next) * road.costDistance();
				if(dist < minDist)
					minDist = dist;
			}
			distFromMarket = minDist;
		}
*/		return distFromMarket;
	}
}
