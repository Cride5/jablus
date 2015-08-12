package uk.co.crider.jablus.models.basic.env;

import uk.co.crider.jablus.models.basic.Constants;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.env.land.LandBid;
import uk.co.crider.jablus.models.basic.env.land.LandCell;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import jwo.landserf.structure.ColourTable;

/** Keeps track of which agents own which land cell. */
public class LandOccupancy extends MatrixData {//, DriverEndogeneous {

//	private Parameters params;
	
	/** Unique class ID */
    private static final long serialVersionUID = -6713787878204635428L;

	private Hashtable<Integer, Set<LandCell>> occupancyList; // Reference by owner id + 1
	
	// Land bids
	private Hashtable<LandCell, LandBid> landBids;
	private Hashtable<Agent, Set<LandBid>> landAuctionWinners;

	// Drivers
//	private LandUse landUse;

	public LandOccupancy(Parameters params){
		super(Constants.DRIVER_LAND_OCCUPANCY, params.ROWS, params.COLS);
//		this.params = params;
		this.occupancyList = new Hashtable<Integer, Set<LandCell>>();
		this.landBids = new Hashtable<LandCell, LandBid>();
		this.landAuctionWinners = new Hashtable<Agent, Set<LandBid>>();

		// Generate ownership map
/*		for(int i = 0; i < params.AGENT_LAND_OCCUPANCY.length; i++){
			Set<LandCell> cells = (Set<LandCell>)params.AGENT_LAND_OCCUPANCY[i]; 
			occupancyList.put(i + 1, cells);
			for(LandCell cell : cells){
				setData(cell.x, cell.y, i + 1);
			}
		}
*/		
		if(params.AGENT_LAND_OCCUPANCY != null){
			for(int y = 0; y < params.AGENT_LAND_OCCUPANCY.getRows(); y++){
				for(int x = 0; x < params.AGENT_LAND_OCCUPANCY.getCols(); x++){
					int owner = (int)params.AGENT_LAND_OCCUPANCY.getData(x, y);
					Set<LandCell> cells = occupancyList.get(owner);
					if(cells == null){
						cells = new TreeSet<LandCell>();
						occupancyList.put(owner, cells);
					}
					cells.add(new LandCell(x, y));
					setData(x, y, owner);
				}
			}
		}
		
		// Initialise list of free cells
		TreeSet<LandCell> freeLand = new TreeSet<LandCell>();
		for(int j = 0; j < params.ROWS; j++){
			for(int i = 0; i < params.COLS; i++){
				LandCell cell = new LandCell(i, j);
				if(isLandFree(cell)){
					freeLand.add(cell);
				}
				freeLand.add(cell);
			}
		}
		occupancyList.put(0, freeLand);
		
	}
/*	private LandOccupancy(
			Data2D occupancyRaster,
			Hashtable<Integer, Set<LandCell>> occupancyList){
		this.occupancyRaster = (Data2D)occupancyRaster.clone();
		this.occupancyList = (Hashtable<Integer, Set<LandCell>>)occupancyList.clone();
		this.landBids = new Hashtable<LandCell, LandBid>();
		this.landAuctionWinners = new Hashtable<Agent, Set<LandBid>>();
	}
	public Object clone(){
		return new LandOccupancy(occupancyRaster, occupancyList);
	}	
*/	
	/** Initialise drivers */
//	public void init(Map drivers){
//		this.landUse = (LandUse)drivers.get(Driver.LAND_USE);
//	}
	
	/** Return all the free (not occupied) land cells */
	public synchronized Set<LandCell> getFreeLand(){
		return getOccupiedLand(0);
	}
	
	/** Returns whether a land cell is free */
	public boolean isLandFree(LandCell cell){
		return getOccupier(cell) == 0;
	}

	/** Return land gells owned by the given agent */
	public Set<LandCell> getOccupiedLand(Agent agent) {
		return getOccupiedLand(agent.getId() + 1);
	}
	
	/** Retrive occupied cells */
	private Set<LandCell> getOccupiedLand(int occupier){
		Set<LandCell> land = occupancyList.get(occupier);
		if(land == null) return new TreeSet<LandCell>();
		return land;
	}
	
	/** Returns whether a land cell is owned by this agent */
	public boolean isLandOccupied(Agent agent, LandCell cell){
		return agent.getId() + 1 == getOccupier(cell);
	}
	
	/** Returns the owner of the land cell */
	private int getOccupier(LandCell cell){
		return (int)getData(cell.x, cell.y);
	}
	
	/** Set occupancy of a land cell */
	public void setOccupancy(Agent farmer, LandCell cell){
		setOccupancy(cell, farmer.getId() + 1);
	}
	
	/** Sets the occupier of a land cell */
	private synchronized void setOccupancy(LandCell cell, int newOwner){
		int currentOwner = getOccupier(cell);
		if(newOwner == currentOwner)
			return;
		// Get lists of owned land
		Set<LandCell> currentOwnersLand = occupancyList.get(currentOwner);
		Set<LandCell> newOwnersLand = occupancyList.get(newOwner);
		// Ensure lists have been initialised
		if(currentOwnersLand == null){
			// Shouldnt be needed, but there for robustness
			currentOwnersLand = new TreeSet<LandCell>();
			occupancyList.put(currentOwner, currentOwnersLand);
		}
		if(newOwnersLand == null){
			newOwnersLand = new TreeSet<LandCell>();
			occupancyList.put(newOwner, newOwnersLand);
		}
		// Set owners
		setData(cell.x, cell.y, newOwner);
		currentOwnersLand.remove(cell);
		newOwnersLand.add(cell);
	}

	
	// Land auction functionallity ------------------------------

	/** Return all land bids won by this agent */
	public Set getBidsWon(Agent agent){
		Set bidsWon = landAuctionWinners.get(agent); 
		// If no bids won then return an empty list
		if(bidsWon == null)
			return new TreeSet();

		return bidsWon;
	}

	public void bidForLand(LandBid bid){
		LandBid existing = landBids.get(bid.getCell());
		// No existing bid then this is the first bid for
		// this cell and can be added to pending bids
		if(existing == null){
			landBids.put(bid.getCell(), bid);
//			System.out.println(bid.getAgent() + ": Is First Bidder for " + bid.getCell());
		}
		else if(bid.higher(existing))
//			{System.out.print("Replacing cell: " + landBids.size() + " ");
			landBids.put(bid.getCell(), bid);
//		System.out.println(landBids.size());}
//		else System.out.println("Bid is too low");	

//		System.out.println("Bids: " + landBids.size());
	}

	/** Finalises the land auction by selecting winning bidders */
	public void closeLandAuction(){
		// Clear old winners
		landAuctionWinners = new Hashtable<Agent, Set<LandBid>>();
//		System.out.println("Total Bids:" + landBids.size());
		// Seperate out winners into seperate lists
		for(Enumeration<LandCell> i = landBids.keys(); i.hasMoreElements();){
			LandCell cell = i.nextElement();
			LandBid bid = landBids.get(cell);
			Set<LandBid> bidsWon = landAuctionWinners.get(bid.getAgent());
			// If this agent hasn't won any cells yet then a new list must be initialised and added
			if(bidsWon == null){
				bidsWon = new TreeSet<LandBid>();
			}
			// Add the winning bid
			bidsWon.add(bid);
			landAuctionWinners.put(bid.getAgent(), bidsWon);
		}

//		System.out.println("Total Winners:" + landAuctionWinners.size());
//		HashSet bidsWon = (HashSet)landAuctionWinners.get(new Integer(1));
//		if(bidsWon != null)
//		System.out.println("1: won " + bidsWon.size() + " bids");
		// Clear the landBids list
		landBids = new Hashtable<LandCell, LandBid>();
	}
	
	// Implementing Data --------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new LandOccupancy(id, data, toShow); }
	/** Constructor for cloning this object */
	private LandOccupancy(int id, float[][] data, boolean toShow){
		super(id, data, toShow); }
	
	
	// Implementing Data2D ---------------------------------
	
/*	public String getNameId() {
		return Constants.DRIVER_LAND_OCCUPANCY;
	}
	public String toString(){
		return Utils.arrayString(occupancyRaster.getRasterArray());
	}
	public RasterMap getRaster() {
	    return occupancyRaster.getRaster();
    }
	public float[][] getRasterArray() {
	    return occupancyRaster.getRasterArray();
    }
	public void setData(Data2D data) {
		occupancyRaster.setData(data);
    }
	public boolean toShow() {
	    return occupancyRaster.toShow();
    }
	public String writeRaster(File path, int time) {
	    return occupancyRaster.writeRaster(path, time);
    }
*/
	public static ColourTable getColourTable(){
		ColourTable col = new ColourTable();
		for(int i = 0; i < Agent.COLOUR.length; i++){
			col.addDiscreteColourRule(i,
					Agent.COLOUR[i].getRed(), 
					Agent.COLOUR[i].getGreen(),
					Agent.COLOUR[i].getBlue()
			);
		}
		return col;
	}

}
