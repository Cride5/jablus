package uk.co.crider.jablus.models.absluc.agent.bdi;

import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.basic.agent.LandUseCell;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.models.basic.env.land.LandUseProfile;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/** Represents a land use arrangement */
public class LandUseScheme implements LandUseProfile{
	
	public static final int SELECT_LAST      = 0;
	public static final int SELECT_MEMORISED = 1;
	public static final int SELECT_BEST      = 2;
	public static final int SELECT_UNCERTAIN = 3;
	public static final int SELECT_RANDOM    = 4;
	
	public static final int NORTH = 0;
	public static final int EAST  = 1;
	public static final int SOUTH = 2;
	public static final int WEST  = 3;
			
	private Parameters params;
	
	/** Random number generator */
	private Random random;

	/** Land use cells */
	private TreeSet<LandUseCell> landUse;
	
	/** Reference to land use cells by location */
	private TreeMap<LandCell, LandUseCell> landUseMap;
	
	/** Memorised previous land use */
	private TreeMap<LandCell, LandUseCell> memorisedUse;
	
	/** Profit of memorised use */
	private double memorisedProfit;
	
	/** Cells applied to best known use */
//	private TreeSet<LandUseCell> bestUseCells;

	/** Cells applied to a use other then the best known use */
//	private TreeSet<LandUseCell> altUseCells;
	
	/** Used for shape-based land uses */ 
//	private List landUseShapes;

	/** Number of cells with each use */
	private int[] useCount;

	/** Number of cells unchanged after a call to change() */
	private int[] changedCells;
	
	/** Advantage using all cells as best use over next best */
	private double advantage;
	
	/** Total profit */
	private double totalProfit;

	/** Average profit uncertainty */
	private double avgProfitU;

	/** Average profit uncertainty of alternitive use */
	private double avgProfitUAlt;

	/** Maximum profit for a single cell so far */
	private double profitMax;

	/** Maximum total trend so far */
	private double totalProfitMax;

	/** Total profit trend */
	private double totalProfitT;

	/** Average profit trend uncertainty */
	private double avgProfitTU;
	
	/** Maximum trend for a single cell so far */
	private double profitTMax;

	/** Maximum total trend so far */
	private double totalProfitTMax;

	/** Average profit by use */
	private double[] profit;
	
	/** Average profit uncertainty by use */
	private double[] profitU;

	/** Average profit trend by use */
	private double[] profitT;

	/** Average profit trend uncertainty by use */
	private double[] profitTU;

	/** Ratio of best to alt land use applied */
//	private double ratio;
	
	/** Distribution of alt land use in random selection alg */
//	private double distribution;

	/** Orientation of land use (applies to patterns and shapes) */
//	private double orientation;
	
	/** Construct a new land use scheme */
	public LandUseScheme(Set<LandCell> land, Parameters params, Random random){
		this.params = params;
		this.random = random;
		this.landUse = new TreeSet<LandUseCell>();
		this.landUseMap = new TreeMap<LandCell, LandUseCell>();
		for(LandCell cell : land){
			LandUseCell useCell = new LandUseCell(cell.x, cell.y, params, random);
			landUse.add(useCell);
			landUseMap.put(cell, useCell);
		}
		this.memorisedUse = null;
		this.memorisedProfit = -Double.MAX_VALUE;
		this.useCount = new int[params.LAND_USES];
		this.changedCells = new int[params.LAND_USES];
		this.advantage = 0;
		this.totalProfit = 0;
		this.avgProfitU = 1;
		this.avgProfitUAlt = 1;
		this.profitMax = 0;
		this.totalProfitMax = 0;
		this.totalProfitT = 0;
		this.avgProfitTU = 1;
		this.profitTMax = 0;
		this.totalProfitTMax = 0;
		this.profit = new double[params.LAND_USES];
		this.profitU = new double[params.LAND_USES];
		this.profitT = new double[params.LAND_USES];
		this.profitTU = new double[params.LAND_USES];
		// Initialise averages
		for(int i = 0; i < params.LAND_USES; i++){
			this.profitU[i] = 1.0;
			this.profitTU[i] = 1.0;
		}
//		this.ratio = 0.0; 
//		this.distribution = 1.0;
	}
	
	/** Construct a completely new land use scheme based on parameters */
/*	public LandUseScheme(int baseLandUse, int altLandUse){
		this(baseLandUse, altLandUse, 0.5,   0.0,          NORTH); }
	public LandUseScheme(int baseLandUse, int altLandUse, double ratio){
		this(baseLandUse, altLandUse, ratio, 0.0,          NORTH); }
	public LandUseScheme(int baseLandUse, int altLandUse, double ratio, double distribution){
		this(baseLandUse, altLandUse, ratio, distribution, NORTH); }
	public LandUseScheme(int baseLandUse, int altLandUse, double ratio, double distribution, double orientation){
//		this.baseLandUse = baseLandUse;
//		this.altLandUse = altLandUse;
		this.landUse = new LandUseCell[params.ROWS][params.COLS];
//		this.oldLandUse = null;
		this.bestUseCells = new TreeSet<LandUseCell>();
		this.altUseCells = new TreeSet<LandUseCell>();
		this.useCount = new int[params.LAND_USES];
		this.changedCells = new int[params.LAND_USES];
		this.advantage = 0;
		this.totalProfit = 0;
		this.avgProfitU = 1;
		this.avgProfitUAlt = 1;
		this.profitMax = 0;
		this.totalProfitMax = 0;
		this.totalProfitT = 0;
		this.avgProfitTU = 1;
		this.profitTMax = 0;
		this.totalProfitTMax = 0;
		this.profit = new double[params.LAND_USES];
		this.profitU = new double[params.LAND_USES];
		this.profitT = new double[params.LAND_USES];
		this.profitTU = new double[params.LAND_USES];
		// Initialise averages
		for(int i = 0; i < params.LAND_USES; i++){
			this.profitU[i] = 1.0;
			this.profitTU[i] = 1.0;
		}
		this.ratio = ratio; 
		this.distribution = distribution;
//		this.orientation = orientation;
		for(int i = 0; i < params.ROWS; i++)
			for(int j = 0; j < params.COLS; j++)
				landUse[i][j] = new LandUseCell(j, i, baseLandUse);
		genLandUse(false);
	}
*/
	
	/** Generate a new land use scheme map based on the parameters, returns the old pattern */
	public void selectUse(){ selectUse(SELECT_LAST, 0.0, 1.0, NORTH); }
	public void selectUse(int scheme){ selectUse(scheme, 0.0, 1.0, NORTH); } 
	public void selectUse(int scheme, double ratio){ selectUse(scheme, ratio, 1.0, NORTH); } 
	public void selectUse(int scheme, double ratio, double distribution){ selectUse(scheme, ratio, distribution, NORTH); } 
	public void selectUse(int scheme, double ratio, double distribution, int orientation){
		
		// Reset changed cells value to 0
		changedCells = new int[params.LAND_USES];

		// If using last scheme or using memorised scheme
		// when no memory exist then use remains the same
		if(scheme == SELECT_LAST || (scheme == SELECT_MEMORISED && memorisedUse == null)){
			for(LandUseCell cell : landUse)
				cell.setSameUse();
			// Go no further if land use unchanged
			return;
		}
		
		// Reset useCount because use is changing..
		useCount = new int[params.LAND_USES];

		// Select use from memory
		if(scheme == SELECT_MEMORISED){
			pickMemorised();
			// Use is selected, no need to go any further
			return;
		}
		
		// Select best known use
		if(scheme == SELECT_BEST){
			setBestUse(landUse);
			// Use is selected, no need to go any further
			return;
		}

		// Ensure ratio is correct for random and uncertian cell selection
		ratio = ratio < 0 ? 0 : ratio > 1 ? 1 : ratio; 
		// If ratio > 0, need to ensure its high enough for 1 cell to be selected
		if(ratio > 0){
			double minRatio = 1.0/landUse.size();
			ratio = ratio < minRatio ? minRatio : ratio;
		}
		
		// Select proportion of alt cells which are most uncertain
		if(scheme == SELECT_UNCERTAIN)
			pickMostUncertain(ratio);

		// Select proportion of alt cells at random
		if(scheme == SELECT_RANDOM)
			pickRandom(ratio, distribution);
//System.out.println("Base use "+ baseLandUse + ", alt use "+altLandUse + ", ratio " + ratio + ", distribution " + distribution + ", alt cells " + altCells);
		
	}
	
	/** Select random cells to be assigned to their alternitive use */
	private void pickRandom(double ratio, double distribution){
		// Calculate number of cells to select alternitive use for
		int cellCount = (int)(landUse.size() * ratio);
		// Assigin cellCount land cells to alternitive use at random
		Vector<LandUseCell> cells = new Vector<LandUseCell>(landUse);
		for(int i = 0; i < cellCount; i++){
			int index = (int)(random.nextDouble() * distribution * cells.size());
			LandUseCell cell = (LandUseCell)cells.get(index);
			cell.setAltUse();
			updateCounters(cell);
			// Remove cell from base list
			cells.remove(cell);
		}
		// Set the best use for remaining cells
		setBestUse(cells);
	}
		
	/** Select alternitive cells for which knowledge of the alternitive use is most uncertain */
	private void pickMostUncertain(double ratio){
		// Calculate number of cells to select alternitive use for
		int cellCount = (int)(landUse.size() * ratio);
		// Create set sorted by uncertainty value
		TreeSet<LandUseCell> sorted = new TreeSet<LandUseCell>();
		for(LandUseCell cell : landUse){
			cell.setSortMode(LandUseCell.SORT_BY_ALT_UNCERTAINTY);
			sorted.add(cell);
		}
		// Remove most uncertain cells and assign to alt-use
		Iterator iter = sorted.iterator();
		for(int i = 0; i < cellCount && iter.hasNext(); i++){
			LandUseCell cell = (LandUseCell)iter.next();
			// Remove cell from base list
			iter.remove();
			cell.setSortMode(LandUseCell.SORT_BY_LOCATION);
			cell.setAltUse();
			updateCounters(cell);
		}
		// Set the best use for remaining cells
		setBestUse(sorted);
	}

	/** Set memorised land use */
	private void pickMemorised(){
		for(LandUseCell cell : landUse){
			if(memorisedUse.containsKey(cell)){
				LandUseCell memCell = memorisedUse.get(cell);
				cell.setNewUse(memCell.getUse());
			}
			else{
				cell.setSameUse();
			}
			updateCounters(cell);
		}			
	}

	/** Set best use for cells in collection */
	private void setBestUse(Collection<LandUseCell> cells){
		for(LandUseCell cell : cells){
			cell.setBestUse();
			updateCounters(cell);
		}
	}

	/** Update use count and changed cells vars */
	private void updateCounters(LandUseCell cell){
		useCount[cell.getUse()]++;
		if(cell.useChanged())
			changedCells[cell.getUse()]++;
	}

	/** Set land use memory */
	public void memorise(){
//System.out.println("Memorising...");
		memorisedUse = new TreeMap<LandCell, LandUseCell>();
		for(LandUseCell cell : landUse)
			memorisedUse.put(cell, new LandUseCell(cell.x, cell.y, params, cell.getUse()));
		memorisedProfit = totalProfit;
	}
	
	/** Clear land use memory */
	public void clearMemory(){
		memorisedUse = null;
		memorisedProfit = -Double.MAX_VALUE;
	}
	
	/** Whether a good use has been memorised */
	public boolean isMemorised(){
		return memorisedUse != null;
	}
	
	public boolean isMemorisedBetter(){
//System.out.println("Is memorised better? " + (memorisedProfit > totalProfit) + "["+memorisedProfit+","+totalProfit+"]");
		return memorisedProfit > totalProfit;
	}

	
	// Accessor methods ----------------------------------
	
	/** Returns full land use pattern */
	public TreeSet<LandUseCell> getLandUse(){
		return landUse;
	}
	public void setUse(LandCell cell, int i) {
	    landUseMap.get(cell).setNewUse(i);
	    updateCounters(landUseMap.get(cell));
    }
		
	
	/** Change this land use scheme, returns the old land use pattern */
/*	public int[][] useChanged(int[][] newUse){
		useCount = new int[params.LAND_USES];
		bestUseCells.clear();
		altUseCells.clear();
		int[][] oldUse = new int[params.ROWS][params.COLS];
		for(int i = 0; i < params.ROWS; i++){
			for(int j = 0; j < params.COLS; j++){
				oldUse[i][j] = landUse[i][j].getUse();
				if(landUse[i][j].setNewUse(newUse[i][j]))
					bestUseCells.add(landUse[i][j]);
				else
					altUseCells.add(landUse[i][j]);
				useCount[landUse[i][j].getUse()]++;
				if(landUse[i][j].useChanged())
					changedCells[landUse[i][j].getUse()]++;
			}
		}	
		return oldUse;
	}
*/
			
	/** Update profits for each cell */
	public void updateProfit(double[][] profitNew){
		advantage = 0;
		avgProfitUAlt = 0;
		// Calculate total profits
		profit = new double[params.LAND_USES];
		profitU = new double[params.LAND_USES];
		profitT = new double[params.LAND_USES];
		profitTU = new double[params.LAND_USES];
//		double[] profitAvg = new double[params.LAND_USES];
		for(LandUseCell cell : landUse){
				cell.setProfit(profitNew[cell.x][cell.y], profitTMax);
				advantage += cell.getAdvantage();
				avgProfitUAlt += cell.getProfitUAlt();
				profit[cell.getUse()] += profitNew[cell.x][cell.y];
				profitU[cell.getUse()] += cell.getProfitU()[cell.getUse()];
				profitT[cell.getUse()] += cell.getProfitT()[cell.getUse()];
				profitTU[cell.getUse()] += cell.getProfitTU()[cell.getUse()];
				if(profitNew[cell.x][cell.y] > profitMax)
					profitMax = profitNew[cell.x][cell.y];
				if(cell.getProfitT()[cell.getUse()] > profitTMax)
					profitTMax = cell.getProfitT()[cell.getUse()];
//System.out.println(i + ": \t" + profitNew[i][j] + ",\t" + profitU[cell.getUse()] + "\t" + profitTU[cell.getUse()]);
		}
		// Get totals
		totalProfit = 0;
		avgProfitU = 0;
		totalProfitT = 0;
		avgProfitTU = 0;
		// Devide by use count
		for(int i = 0; i < params.LAND_USES; i++){
			totalProfit += profit[i];
			avgProfitU += profitU[i];
			totalProfitT += profitT[i];
			avgProfitTU += profitTU[i];
			profit[i] /= useCount[i];
			profitU[i] /= useCount[i];
			profitT[i] /= useCount[i];
			profitTU[i] /= useCount[i];
//System.out.println(i + ": \t" + profitU[i] + ",\t" + useCount[i] + ",\t"+params.TOTAL_CELLS);
		}
		// Calculate global totals and averages
		advantage /= landUse.size();
		avgProfitUAlt /= landUse.size();
		avgProfitU /= landUse.size();
		avgProfitTU /= landUse.size();
		if(totalProfit > totalProfitMax)
			totalProfitMax = totalProfit;
		if(totalProfitT > totalProfitTMax)
			totalProfitTMax = totalProfitT;
	}
	
	// Return land use statistics 
	
	/** return the number of cells which have changed */
	public int[] getChangedCells(){
		return changedCells;
	}
	public int cellsChanged(){
		int total = 0;
		for(int i : changedCells){
			total += i;
		}
		return total;
	}
	public double getDistribution(){
		double distribution = 0;
		
		return distribution;
	}
	
	public int[] getUseCount(){ return useCount; }
	public double getTotalProfit(){  return totalProfit; }
	public double[] getAvgProfit(){  return profit; }
	public double[] getAvgProfitT(){  return profitT; }
	public double getAvgProfitTU(){  return avgProfitTU; }
	public double getAvgProfitU() {  return avgProfitU; }
	public double getAvgProfitUAlt(){return avgProfitUAlt; }
	/** Returns how advantagous the use
	 * of best-cells is over alt-cells between 0 and 1 */
	public double getAdvantage(){ return advantage; }
		
	
	// --------------[ FULL PATTERNS ]----------
	// set land use as horizontal or vertical lines
//	private void setLinesPattern(int separation, int start){
//	}
	
	// Set checkered land use pattern
//	private void setCheckeredPattern(boolean inverted){		
//	}
	
	// Set cross hatched pattern
//	private void setCrossHatchedPattern(){	
//	}
	
	// --------------[ SHAPE PATTERNS ]----------
//	private void setSquareShape(double size, LandCell location){	
//	}
	
	
	
	public static void main(String[] args){
		int totalCells = 10;
		double ratio = 0.75;
		int altCells = (int)(totalCells * ratio + 0.5); 
		System.out.println(altCells);
	}

}
