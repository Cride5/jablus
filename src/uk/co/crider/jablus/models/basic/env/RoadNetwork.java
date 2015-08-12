/*
 * Created on Feb 16, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.co.crider.jablus.models.basic.env;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.models.basic.Parameters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jwo.landserf.structure.Footprint;
import jwo.landserf.structure.GISVector;
import jwo.landserf.structure.Header;
import jwo.landserf.structure.VectorMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;


/** Represents a network of roads to facilitate transportation */
public class RoadNetwork implements Data{
	
	/** The name of this driver */
	private int id;

	private Parameters params;
	private VectorMap roadNetworkVector;

	/** Junctions/Markets in this road network */
	private List<RoadNode> nodes;
	private List<RoadEdge> edges;
	private List<RoadNode> markets;
	private Random random;
	
	/** Initialise a random road network and markets */
	public RoadNetwork(int id, Parameters params, Footprint vectorFootprint){
		this.id = id;
		this.params = params;

		// Initialise fields
		this.nodes = new LinkedList<RoadNode>();
		this.edges = new LinkedList<RoadEdge>();
		this.markets = new LinkedList<RoadNode>();
		this.random = new Random(params.TRANSPORT_ROADS_SEED);

		// Create markets
		if(params.TRANSPORT_MARKET){
			for(int i = 0; i < params.MARKET_LOCATIONS.length; i++){
				markets.add(new RoadNode(new Coordinate(
						params.MARKET_LOCATIONS[i].x,
						params.MARKET_LOCATIONS[i].y
				), RoadNode.MARKET));
			}
			nodes.addAll(markets);
		}

		// Create road network
		if(params.TRANSPORT_MARKET && params.TRANSPORT_ROADS){
			// Create roads leading from markets, and roads leading from them and so on..
			List<RoadNode> lastNodes = null;
			List<RoadNode> newNodes = markets;
			for(int grade = 0; grade < params.roadSpeeds.length; grade++){
				lastNodes = newNodes;
				newNodes = new LinkedList<RoadNode>();
				for(Iterator i = lastNodes.iterator(); i.hasNext();){
					RoadNode node = (RoadNode)i.next();
					int nodeDegree = (int)(random.nextDouble() * (double)params.MAX_NODE_DEGREE + 1.0);
					for(int j = 0; j < nodeDegree; j++){
						// Create a node at random distance from current node
						double x = node.getX() + (((random.nextDouble() * 2.0) - 1.0) * params.MAX_ROAD_DIST) / (double)((grade + 1)*2);
						double y = node.getY() + (((random.nextDouble() * 2.0) - 1.0) * params.MAX_ROAD_DIST) / (double)((grade + 1)*2);
						// Make sure the new node is within bounds, abandon if not
						if(x >= 0 && x < params.ROWS && y >= 0 && y < params.COLS){
							RoadNode newNode = new RoadNode(new Coordinate(x, y));
							// Add connection from new node to current one and visa versa
							RoadEdge newEdge = new RoadEdge(params, node, newNode, grade);
							node.addEdge(newEdge);
							newNode.addEdge(newEdge);
							newNode.setMarketDist(newEdge.costDistance() + node.getMarketDist());
							edges.add(newEdge);
							newNodes.add(newNode);
						}
					}
				}
				// Add the new set of nodes for this road grade
				nodes.addAll(newNodes);
			}
		}
		
		// Get markets from roadnetwork
		roadNetworkVector = getRoadNetworkVector(vectorFootprint);
	}
	/** Clone the object */
	public Object clone(){
		// Since this is a non-mutable object there is no need to clone!
		return this;
	}
	
	//TODO: Optimise distance lookups using hashtable
	// (will prob need to implement comparable interface for Point class)
	/** Returns the distance to the closest market (through the road network if it exists)*/
	public double distance(Point fromPoint){ return distance(fromPoint, fromPoint); }
	/** Returns the distance to the closest market,
	 *  via this farm (through the road network if it exists)
	 *  if no market exists the distance to the farm is returned
	 */
	public double distance(Point fromPoint, Point farmLocation){
		// Remove farm location from equation if not set
		if(!params.TRANSPORT_FARM) farmLocation = fromPoint;
		double totalDist = 0.0;
		if(params.TRANSPORT_MARKET){
			// Calculate distance to closest market
			totalDist = Double.MAX_VALUE;
			for(Iterator i = nodes.iterator(); i.hasNext();){
				RoadNode junction = (RoadNode)i.next();
				double dist = (farmLocation.distance(junction) / params.roadSpeeds[RoadEdge.NO_ROAD]) + junction.getMarketDist();
				// This market is closer so return it
				if(dist < totalDist)
					totalDist = dist;
			}
		}
		// Add on internal farm transport distance
		if(params.TRANSPORT_FARM){
			totalDist += (fromPoint.distance(farmLocation) / params.roadSpeeds[RoadEdge.INTERNAL]);
		}
		return totalDist;// * Constants.TRANSPORT_DISTANCE_FACTOR;
	}
	
	/** Return a landserf VectorMap object representing the markets */
	public VectorMap getMarkets(Footprint footprint){
		VectorMap marketsVector = new VectorMap(footprint, new Header(""+Constants.DRIVER_ROAD_NETWORK));
		for(Iterator i = markets.iterator(); i.hasNext();){
			RoadNode node = (RoadNode)i.next();
			// Fix coordinates bug
			GISVector m = new GISVector((float)node.getY(), params.ROWS - (float)node.getX(), 17);
			marketsVector.add(m, false);
		}
		return marketsVector;
	}
	
	/** Return a landserf VectorMap object representing the roads */
	public VectorMap getRoads(Footprint footprint){
		VectorMap roadsVector = new VectorMap(footprint, new Header(""+Constants.DRIVER_ROAD_NETWORK));
		for(Iterator i = edges.iterator(); i.hasNext();){
			RoadEdge edge = (RoadEdge)i.next();
			GISVector r = new GISVector(
					// Fix coordinates bug
					new float[]{(float)edge.getN1().getY(), (float)edge.getN2().getY()},
					new float[]{(float)(params.ROWS - edge.getN1().getX()), (float)(params.ROWS - edge.getN2().getX())},
					GISVector.LINE,
					edge.getGrade() + Agent.COLOUR.length);
			roadsVector.add(r, false);
		}
		return roadsVector;
	}
	
	/** Return a landserf VectorMap representing the road network */
	public VectorMap getRoadNetworkVector(Footprint footprint){
		VectorMap roadNetworkVector = getMarkets(footprint);
		roadNetworkVector.add(getRoads(footprint));
		return roadNetworkVector;
	}

	// Implementing Data ----------------------------
	
	public int getId() { return id; }

	public String getName() { return Constants.getName(id); }
	
	public String stringValue(){
		return getName();
	}
	
	public VectorMap getData(){
		return roadNetworkVector;
	}
}
