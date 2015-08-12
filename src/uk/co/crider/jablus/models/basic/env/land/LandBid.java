/*
 * Created on Mar 19, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.co.crider.jablus.models.basic.env.land;


import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.models.basic.Parameters;

import java.util.Random;




/** Represents an agent's bid for a land cell */
public class LandBid implements Comparable{
	
	private final Agent agent;
	private final Parameters params;
	private final LandCell cell;
	private final double amount;
	
	public LandBid(Agent agent, Parameters params, LandCell cell, double amount){
		this.agent = agent;
		this.params = params;
		this.cell = cell;
		this.amount = amount;
	}
	
	public Agent getAgent(){
		return agent;
	}
	
	public LandCell getCell(){
		return cell;
	}

	public double getAmount(){
		return amount;
	}
	
	public boolean higher(LandBid bid){
		if(amount > bid.getAmount()) return true;
		// If not then resolve with random number dependent on cell for seed
		// ensures that the resoultion decision is repeatable
		if(amount == bid.getAmount()
//				&& params.LAND_OWNERSHIP_RAND_BID
				&& new Random(bid.getCell().x + params.ROWS * bid.getCell().y).nextDouble() >= 0.5) return true;
		return false;
	}
		
	public boolean equals(Object o){
		if(o instanceof LandBid){
			LandBid bid = (LandBid)o;
			return agent.equals(bid.getAgent()) && cell.equals(bid.getCell()) && amount == bid.getAmount();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(o instanceof LandBid){
			LandBid bid = (LandBid)o;
			int cmp = cell.compareTo(bid.getCell());
			if(cmp != 0) return cmp;
			double diff = amount - bid.getAmount();
//System.out.println(diff);
			if(diff > 0)
//{System.out.println("diff > 0");
				return 1;
//}
			if(diff < 0)
//{System.out.println("diff < 0");
				return -1;
//}
			if(agent.equals(bid.getAgent())) return 0;
		}
		return 1;
	}
	
	public String toString(){
		return "Bid(" + agent + ", " + cell + "," + amount + ")";
	}

	public static void main(String[] args) {
		
		// Test data
//		Comparable o1 = new LandBid(new FarmerAgent("John"), new LandCell(1,1), 1.0);
//		Comparable o2 = new LandBid(new FarmerAgent("John"), new LandCell(1,1), 1.0);
//		System.out.println("o1.equals(o2):" + o1.equals(o2));
//		System.out.println("o1==o2:" + (o1 == o2));
//		System.out.println("o1.hashCode()==o2.hashCode():" + (o1.hashCode()==o2.hashCode()));
//		System.out.println("o1.compareTo(o2):" + o1.compareTo(o2));
	}

}
