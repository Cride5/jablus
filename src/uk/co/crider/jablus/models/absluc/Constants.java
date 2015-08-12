package uk.co.crider.jablus.models.absluc;


/** Constants for the abstract land use model */
public class Constants extends uk.co.crider.jablus.models.basic.Constants{
	
	
	// Agent categories
	
	// Output data names
	public static final int OUTPUT_REVENUE             = 100;
	public static final int OUTPUT_REVENUE_GRID        = 101;
	public static final int OUTPUT_REVENUE_GAME_TOTAL  = 102;
	public static final int OUTPUT_OPTIMALLITY         = 103;
	public static final int OUTPUT_CELLS_CHANGED       = 104;	
	public static final int OUTPUT_DISTRIBUTION        = 105;
	
	// Misc data items
	public static final int GAME_TITLE = 106;
	public static final int BANK_TOTAL = 107;
	
	public static final int SUBJECT_DATA = 108;

	public static String HELP_URL = "http://xweb.geos.ed.ac.uk/~s0127633/expt/instructions.html";

	// Data to export
	public static final int[] EXPORT_DATA = {
		TEMPORAL_TIME,
		OUTPUT_REVENUE,
		OUTPUT_OPTIMALLITY,
		OUTPUT_CELLS_CHANGED,
		OUTPUT_NARRATIVE
	};
	
	public static void init(){
		NAMES.put(OUTPUT_REVENUE             , "Revenue");
		NAMES.put(OUTPUT_REVENUE_GRID        , "RevenueGrid");
		NAMES.put(OUTPUT_REVENUE_GAME_TOTAL  , "Game Total");
		NAMES.put(OUTPUT_OPTIMALLITY         , "Optimallity");
		NAMES.put(OUTPUT_CELLS_CHANGED       , "CellsChanged");	
		NAMES.put(OUTPUT_DISTRIBUTION        , "LandUseDistribution");
		
		NAMES.put(GAME_TITLE                 , "Game Title");
		NAMES.put(BANK_TOTAL                 , "Bank");

	}
	

}
