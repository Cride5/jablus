package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jump.ColourRampStyle;
import uk.co.crider.jablus.gui.jump.ColourTableStyle;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.models.dairy.Constants;

import java.awt.Color;
import java.awt.Font;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;

/** Constants for controlling the SubjectInterface layout and look */
public class SubjectDisplayParams extends DisplayParams{
	
	public static final int WIDTH = 850;
	public static final int HEIGHT = 700;
	
	public static final int NORTH_HEIGHT = 100;
	public static final int SOUTH_HEIGHT = 210;
	public static final int EAST_WIDTH = 350;
//	public static final int WEST_WIDTH = 350;
		
	// -----------[ old settings ]---------------
	public static String EXPT_TITLE = "Dairy Farm Simulator";
	public static String DATA_COLLECTION_TITLE = "Your Details";
	
	public static final int GRID_WIDTH = 280;
	public static final int INPUT_PANEL_WIDTH = 280;
	public static final int INPUT_PANEL_HEIGHT = 140;
	
	
	// Default display properties
//	private final Hashtable<Integer, Integer> display = new Hashtable<Integer, Integer>();
	// Default display properties
//	public final Hashtable<Integer, int[]> display1D = new Hashtable<Integer, int[]>();
	//	public static boolean GUI_HUMAN_EXPT = true;
 	
	// The order in which DataView items are to appear
//	public final Map<Integer, Integer> displayOrder = new Hashtable<Integer, Integer>();

	public SubjectDisplayParams(Parameters params){
		super(params);
		// Driver dataset properties
		display.put(Constants.STATIC_ROADS,                ADD_VALUE | SHOW_VALUE);
		display.put(Constants.STATIC_RIVERS,               ADD_VALUE | SHOW_VALUE);
		display.put(Constants.STATIC_FIELD_MAP,            ADD_VALUE | SHOW_VALUE);
		display.put(Constants.STATIC_BUILDS,               ADD_VALUE | SHOW_VALUE);

		display.put(Constants.INPUT_RAIN,                  ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.INPUT_TEMP,                  ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.INPUT_RAD,                   ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.DATA_GROUP_WEATHER,                                   HIDDEN);

//		display.put(Constants.STORE_MAIZE,                 ADD_VALUE | SHOW_VALUE);
//		display.put(Constants.STORE_SILAGE,                ADD_VALUE | SHOW_VALUE);
//		display.put(Constants.STORE_WHEAT,                 ADD_VALUE | SHOW_VALUE);

		// Driver dataset properties
		display.put(Constants.TEMPORAL_TIME,               ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DATASET_FIELDS,              ADD_VALUE | SHOW_VALUE);

		display.put(Constants.DERIVED_TOTAL_COWS,          ADD_VALUE | SHOW_VALUE);// | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.DERIVED_BREEDING_COWS,       ADD_VALUE | SHOW_VALUE);// | ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
/*		display.put(Constants.DERIVED_HEIFERS,             ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
		display.put(Constants.DERIVED_DRY_COWS,            ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
		display.put(Constants.DERIVED_MILKING_COWS,        ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
		display.put(Constants.DATA_GROUP_COWS,                                      ADD_GRAPH | SHOW_GRAPH | AREA_GRAPH);
*/		display.put(Constants.DERIVED_COWS_SOLD,           ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_FEMALE_CALVES_SOLD,  ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_MALE_CALVES_SOLD,    ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_CARCASES_REMOVED,    ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_MANURE_PASTURE,      ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_MANURE_SLURRY,       ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_MANURE_DUNG,         ADD_VALUE | SHOW_VALUE);
//		display.put(Constants.DERIVED_TOTAL_MILK,          ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.DERIVED_NITROGEN_TO_PASTURE, ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_NITROGEN_TO_CROPS,   ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_CROPS_DM,            ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_CROPS_SILAGE,        ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_CROPS_WHEAT,         ADD_VALUE | SHOW_VALUE);
		display.put(Constants.DERIVED_CROPS_MAIZE,         ADD_VALUE | SHOW_VALUE);
//		display.put(Constants.DERIVED_CROPS_NLEACHED,      ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.DERIVED_ECONOMIC_COSTS,      ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.DERIVED_ECONOMIC_REVENUE,    ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
//		display.put(Constants.DERIVED_ECONOMIC_PROFIT,     ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.DERIVED_ECONOMIC_BALANCE,    ADD_VALUE | SHOW_VALUE );
//		display.put(Constants.DATA_GROUP_ECONOMIC,                                  ADD_GRAPH | SHOW_GRAPH | TIME_GRAPH);
		display.put(Market.MILK,                                                    ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.DATA_GROUP_MILK,                                      ADD_GRAPH | SHOW_GRAPH | TIME_GRAPH);
		display.put(Market.FINISHED_HEIFERS,                                        ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.CULL_COWS,                                               ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.BULL_CALVES,                                             ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.HEIFER_CALVES,                                           ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.CALVED_HEIFERS,                                          ADD_GRAPH);
		display.put(Constants.DATA_GROUP_LIVESTOCK,                                 ADD_GRAPH | SHOW_GRAPH | TIME_GRAPH | EDITABLE);
		display.put(Market.HAY,                                                     ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.CONCENTRATES,                                            ADD_GRAPH);
		display.put(Market.SILAGE_MAIZE,                                            ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.SILAGE_GRASS,                                            ADD_GRAPH | SHOW_GRAPH);
		display.put(Market.SILAGE_WHEAT,                                            ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.DATA_GROUP_FEEDS,                                     ADD_GRAPH | SHOW_GRAPH | TIME_GRAPH | EDITABLE);
//		display.put(Constants.DATA_GROUP_MARKET,                                    ADD_GRAPH | SHOW_GRAPH | TIME_GRAPH);
		
		// Display data groups
/*		groups.put(Constants.DERIVED_HEIFERS,          Constants.DATA_GROUP_COWS);
		groups.put(Constants.DERIVED_DRY_COWS,         Constants.DATA_GROUP_COWS);
		groups.put(Constants.DERIVED_MILKING_COWS,     Constants.DATA_GROUP_COWS);
		groups.put(Constants.DERIVED_ECONOMIC_COSTS,   Constants.DATA_GROUP_ECONOMIC);
		groups.put(Constants.DERIVED_ECONOMIC_REVENUE, Constants.DATA_GROUP_ECONOMIC);
		groups.put(Constants.DERIVED_ECONOMIC_PROFIT,  Constants.DATA_GROUP_ECONOMIC);
		groups.put(Constants.DERIVED_ECONOMIC_BALANCE, Constants.DATA_GROUP_ECONOMIC);
		groups.put(Constants.INPUT_RAIN,               Constants.DATA_GROUP_WEATHER);
		groups.put(Constants.INPUT_TEMP,               Constants.DATA_GROUP_WEATHER);
		groups.put(Constants.INPUT_RAD,                Constants.DATA_GROUP_WEATHER);
		groups.put(Livestock.HEIFERS01,                Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Livestock.HEIFERS1P,                Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Livestock.COWS1ST,                  Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Livestock.COWS2ND,                  Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Livestock.COWS3PL,                  Constants.DATA_GROUP_LIVESTOCK);
*/		groups.put(Market.FINISHED_HEIFERS,            Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Market.CULL_COWS,                   Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Market.BULL_CALVES,                 Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Market.HEIFER_CALVES,               Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Market.CALVED_HEIFERS,              Constants.DATA_GROUP_LIVESTOCK);
		groups.put(Market.MILK,                        Constants.DATA_GROUP_MILK);
		groups.put(Market.CONCENTRATES,                Constants.DATA_GROUP_FEEDS);
		groups.put(Market.HAY,                         Constants.DATA_GROUP_FEEDS);
		groups.put(Market.SILAGE_MAIZE,                Constants.DATA_GROUP_FEEDS);
		groups.put(Market.SILAGE_GRASS,                Constants.DATA_GROUP_FEEDS);
		groups.put(Market.SILAGE_WHEAT,                Constants.DATA_GROUP_FEEDS);

/*		groups.put(Market.MILK,                        Constants.DATA_GROUP_MARKET);
		groups.put(Market.FINISHED_HEIFERS,            Constants.DATA_GROUP_MARKET);
		groups.put(Market.CULL_COWS,                   Constants.DATA_GROUP_MARKET);
		groups.put(Market.BULL_CALVES,                 Constants.DATA_GROUP_MARKET);
		groups.put(Market.HEIFER_CALVES,               Constants.DATA_GROUP_MARKET);
		groups.put(Market.CALVED_HEIFERS,              Constants.DATA_GROUP_MARKET);
		groups.put(Market.HAY,                         Constants.DATA_GROUP_MARKET);
		groups.put(Market.CONCENTRATES,                Constants.DATA_GROUP_MARKET);
		groups.put(Market.SILAGE_MAIZE,                Constants.DATA_GROUP_MARKET);
		groups.put(Market.SILAGE_GRASS,                Constants.DATA_GROUP_MARKET);
		groups.put(Market.SILAGE_WHEAT,                Constants.DATA_GROUP_MARKET);
*/

		// Graph group colours
		paints.put(Constants.DERIVED_ECONOMIC_BALANCE, Color.GREEN.darker());
		paints.put(Constants.INPUT_RAIN, Color.BLUE);
		paints.put(Constants.INPUT_TEMP,     Color.RED);
		paints.put(Constants.INPUT_RAD, Color.ORANGE);
		
		// Output dataset properties
/*		display.put(Constants.OUTPUT_NARRATIVE, SHOW_TEXT);
		display.put(Constants.OUTPUT_REVENUE, ADD_VALUE | SHOW_VALUE | ADD_GRAPH | SHOW_GRAPH);
		display.put(Constants.OUTPUT_REVENUE_GAME_TOTAL, ADD_VALUE | SHOW_VALUE);
		display.put(Constants.BANK_TOTAL, ADD_VALUE | SHOW_VALUE);
		display.put(Constants.OUTPUT_OPTIMALLITY, 0);
		display.put(Constants.OUTPUT_CELLS_CHANGED, 0);
		display.put(Constants.OUTPUT_DISTRIBUTION, 0);
		display.put(Constants.OUTPUT_LAND_USE_COUNT, 0);
*/		
		
		paints.put(Livestock.HEIFERS01 , new Color(224, 219,  69));
		paints.put(Livestock.HEIFERS1P , new Color(100, 188,  41));
		paints.put(Livestock.COWS1ST   , new Color( 89, 227, 209));
		paints.put(Livestock.COWS2ND   , new Color( 54, 113, 193));
		paints.put(Livestock.COWS3PL   , new Color(168, 158, 219));
		paints.put(Market.FINISHED_HEIFERS , new Color(224, 219,  69));
		paints.put(Market.CULL_COWS        , new Color(100, 188,  41));
		paints.put(Market.BULL_CALVES      , new Color( 89, 227, 209));
		paints.put(Market.HEIFER_CALVES    , new Color( 54, 113, 193));
		paints.put( Market.CALVED_HEIFERS  , new Color(168, 158, 219));
		paints.put(Market.CONCENTRATES , new Color(150,  80,  29));
		paints.put(Market.HAY          , new Color(227, 175,  15));
		paints.put(Market.SILAGE_GRASS , new Color( 62, 123,  73));
		paints.put(Market.SILAGE_WHEAT , new Color(214, 204,  95));
		paints.put(Market.SILAGE_MAIZE , new Color(142, 162,  59));
		paints.put(Market.FERTILISER   , new Color(121, 116, 210));
		paints.put(Livestock.MANURE_WET,     Color.ORANGE.darker());
//		paints.put(Livestock.MANURE_WET,     Color.YELLOW.darker());
		paints.put(Crop.FALLOW         , new Color(135, 112,  64));
		paints.put(Crop.WHEAT          , new Color(214, 204,  95));
		paints.put(Crop.MAIZE          , new Color(142, 162,  59));
		paints.put(Crop.GRASS          , new Color( 62, 123,  73));
		paints.put(Crop.PASTURE        , new Color( 88, 177,  66));
		paints.put(Constants.OUTPUT_SCORE_CROPPING,    new Color( 12,  99,  88));
		paints.put(Constants.OUTPUT_SCORE_LIVESTOCK,   new Color(133, 103,   0));
		paints.put(Constants.OUTPUT_SCORE_FINANCIAL,   new Color( 44,  44, 186));
		paints.put(Constants.OUTPUT_SCORE_ENVIRONMENT, new Color(175,  52, 227));
		
		
	
		BasicStyle roadStyle = new BasicStyle(Color.RED);
		roadStyle.setLineWidth(2);
		styles.put(Constants.STATIC_ROADS, roadStyle);
		
		BasicStyle riverStyle = new BasicStyle(new Color(81, 177, 198));
		riverStyle.setLineColor(riverStyle.getFillColor().darker());
		styles.put(Constants.STATIC_RIVERS, riverStyle);
		
		BasicStyle buildingStyle = new BasicStyle(Color.LIGHT_GRAY);
		buildingStyle.setLineColor(Color.GRAY);
		styles.put(Constants.STATIC_BUILDS, buildingStyle);
		
		BasicStyle fieldStyle = new BasicStyle(Color.GREEN);
		fieldStyle.setLineColor(Color.GREEN.darker());
		styles.put(Constants.DATASET_FIELDS, fieldStyle);
		
		colourRamps.put(Constants.STATIC_FIELD_AREA, new ColourRampStyle(
				Constants.STATIC_FIELD_AREA,
				Constants.UNITS_AREA_HIGH,
				new Color[]{Color.RED,
					    Color.WHITE,
						Color.BLUE}));
		colourRamps.put(Constants.TEMPORAL_SOIL_NITRATE, new ColourRampStyle(
				Constants.TEMPORAL_SOIL_NITRATE,
				Constants.UNITS_NITROGEN,
				new Color[]{Color.WHITE, Color.ORANGE.darker().darker()}));
		colourRamps.put(Constants.TEMPORAL_SOIL_LEACHING, new ColourRampStyle(
				Constants.TEMPORAL_SOIL_LEACHING,
				Constants.UNITS_NITROGEN,
				new Color[]{Color.WHITE, Color.ORANGE.darker().darker()}));
		colourRamps.put(Constants.TEMPORAL_SOIL_TEMP, new ColourRampStyle(
				Constants.TEMPORAL_SOIL_TEMP,
				Constants.UNITS_TEMPERATURE,
				new Color[]{new Color( 35,  19, 189),
						    new Color(  7, 159, 229),
						    Color.WHITE,
						    new Color(117, 243,  62),
						    new Color(244, 210,  21),
						    new Color(227, 141,  18),
							new Color(198,  32,   9)}));
		colourRamps.put(Constants.TEMPORAL_SOIL_WATER, new ColourRampStyle(
				Constants.TEMPORAL_SOIL_WATER,
				Constants.UNITS_PERCENT,
				new Color[]{Color.WHITE, Color.BLUE.darker()}));
		colourRamps.put(Constants.TEMPORAL_CROP_DM, new ColourRampStyle(
				Constants.TEMPORAL_CROP_DM,
				Constants.UNITS_YIELD,
				new Color[]{Color.WHITE, Color.GREEN.darker().darker()}));
		colourRamps.put(Constants.TEMPORAL_CROP_LAI, new ColourRampStyle(
				Constants.TEMPORAL_CROP_LAI,
				"",
				new Color[]{Color.WHITE, Color.GREEN.darker().darker()}));
		colourRamps.put(Constants.TEMPORAL_CROP_YIELD, new ColourRampStyle(
				Constants.TEMPORAL_CROP_YIELD,
				Constants.UNITS_YIELD,
				new Color[]{Color.WHITE, Color.GREEN.darker().darker()}));

		Map<Object, BasicStyle> cropMap = new Hashtable<Object, BasicStyle>();
		cropMap.put(Crop.FALLOW,  new BasicStyle(getColour(Crop.FALLOW )));
		cropMap.put(Crop.WHEAT,   new BasicStyle(getColour(Crop.WHEAT  )));
		cropMap.put(Crop.MAIZE,   new BasicStyle(getColour(Crop.MAIZE  )));
		cropMap.put(Crop.GRASS,   new BasicStyle(getColour(Crop.GRASS  )));
		cropMap.put(Crop.PASTURE, new BasicStyle(getColour(Crop.PASTURE)));
		colourTables.put(Constants.INPUT_FIELD_CROP,
				new ColourTableStyle(Constants.INPUT_FIELD_CROP, cropMap, new BasicStyle()));

	
	}
/*
	public boolean display(String dataItem, int toDisplay){
		Integer display = display.get(dataItem);
		if(display == null) return false;
		return (display != null) && ((display & toDisplay) > 0);
	}
	
	public Integer getOrder(String dataItem){
		if(displayOrder.containsKey(dataItem))
			return displayOrder.get(dataItem);
		return dataItem.hashCode();
	}
*/
	// Styles for labels
	public static final JComponent refStyle = new JLabel();
	/** @inheritDoc */
	public static void setStyle(JComponent c, int style){
		switch(style){
		case TITLE :
			c.setBackground(Color.LIGHT_GRAY);
			c.setForeground(Color.WHITE);
			c.setOpaque(true);
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			break;
		case INFO :
			c.setBackground(Color.LIGHT_GRAY);
			c.setForeground(Color.GREEN.darker().darker());
			c.setOpaque(true);
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			break;
		case ACTIVE :
			c.setBackground(Color.LIGHT_GRAY);
			c.setForeground(Color.GREEN.darker().darker());
			c.setOpaque(true);
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			break;
		case NORMAL :
			c.setBackground(refStyle.getBackground());
			c.setForeground(refStyle.getForeground());
			c.setOpaque(false);
			c.setFont(c.getFont().deriveFont(Font.PLAIN));
//			c.setFont(refStyle.getFont());
			break;
		case SMALL :
			c.setFont(c.getFont().deriveFont((float)c.getFont().getSize() - 2));
			break;
		case POSITIVE :
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			c.setForeground(Color.GREEN.darker());
			break;
		case NEGATIVE :
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			c.setForeground(Color.RED.darker());
			break;
		}
	}	
	
}
