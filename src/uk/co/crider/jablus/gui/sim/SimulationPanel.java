package uk.co.crider.jablus.gui.sim;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataNT;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.data.RasterData;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.EventHandler;
import uk.co.crider.jablus.gui.data.DataView;
import uk.co.crider.jablus.gui.data.MapView;
import uk.co.crider.jablus.gui.data.TextView;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/** JPanel to house all display objects on main simulation interface */
public class SimulationPanel extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = -585555021186291785L;

    private MapView maps;
    private DataView data;
    private DataView graphs;
    private TextView texts;
    private DisplayParams displayParams;
    private EventHandler eventHandler;
    
    /** Construct a new panel */
	public SimulationPanel(Frame parent, DisplayParams displayParams, EventHandler eventHandler){
    	super(new BorderLayout());
    	// Initialise fields
    	this.maps = new MapView();
    	this.data = new DataView(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.DATA_ITEM_HEIGHT, displayParams, true);
    	this.graphs = new DataView(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.GRAPH_ITEM_HEIGHT, displayParams, true);
    	this.texts = new TextView();
    	this.displayParams = displayParams;
    	this.eventHandler = eventHandler;
    	// Initialise GUI components
    	initComponents();
		// Show gui
		setVisible(true);
    }

	/** Initialise GUI components */
	private void initComponents(){
		
		JSplitPane topLevelPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
	    topLevelPane.setResizeWeight(0.9);
		
		JSplitPane subLevelPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		subLevelPane.setResizeWeight(1.0);
//	    subLevelPane.setMinimumSize(new Dimension(500,0));
		
		JPanel centerView = new JPanel(new BorderLayout());
	    centerView.add(maps, BorderLayout.CENTER);
	    ControlPanel controlPanel = new ControlPanel(eventHandler);
	    centerView.add(controlPanel, BorderLayout.SOUTH);
	    subLevelPane.setLeftComponent(centerView);
	    topLevelPane.setTopComponent(subLevelPane);
	    
	    JSplitPane dataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
	    dataPanel.setResizeWeight(0.35);
	    dataPanel.setLeftComponent(data);
	    dataPanel.setRightComponent(graphs);
	    subLevelPane.setRightComponent(dataPanel);
	    	    
		topLevelPane.setBottomComponent(texts);
//		topLevelPane.resetToPreferredSizes();
				
		add(topLevelPane, BorderLayout.CENTER);
	}
	
	/** Adds an object to display */
	public void addDisplayItem(Data item){ addDisplayItem(item, null, null); }
	public void addDisplayItem(Data item, Hashtable<String, List<Data>> pastData){ addDisplayItem(item, pastData, null); }
	public void addDisplayItem(Data item, Hashtable<String, List<Data>> pastData, List<Data> time){ addDisplayItem(item, pastData, time, true); }
	public void addDisplayItem(Data item, Hashtable<String, List<Data>> pastData, List<Data> time, boolean toDisplay){
//System.out.println("SimulationPanel: adding display item " + item + "\n\t with past values " + pastData);
		if(item instanceof Data0D || item instanceof Data1D){
			if(displayParams.display(item.getId(), SimulationDisplayParams.ADD_VALUE))
				data.addDisplayItem(item);
			if(displayParams.display(item.getId(), SimulationDisplayParams.ADD_GRAPH)){
				graphs.addGraphItem(item, pastData.get(item.getId()), time, null);
			}
		}
		if(item instanceof Data2D){
			Data2D raster = (Data2D)item;
//System.out.println("SimulationPanel: Adding 2D data " + item.getName());
//System.out.println("SimulationPanel: itemData=" + jablus.utils.Utils.arrayStringR(((Data2D)item).getData()));
			if(raster instanceof RasterData){
				//((RasterData)item).getRaster().setColourTable(displayParams.getColourTable(item.getName()));
				maps.addDisplayItem(((RasterData)item));
			}
/*			else if(raster.getRows() > 0 && raster.getCols() > 0){
				RasterMap map = new RasterMap(raster.getRows(), raster.getCols(), new Footprint(0, 0, 1, 1), new Header(raster.getName()));
				map.setAttributes(((Data2D)item).getData());
				map.setColourTable(displayParams.getColourTable(item.getName()));
				maps.addRaster(map, ((Data2D)item).toShow() ? MapViewThumb.PRIMARY : MapViewThumb.SECONDARY);
			}
*/		}
		if(item instanceof DataNT && displayParams.display(item.getId(), DisplayParams.ADD_VALUE)){
				maps.addDisplayItem((DataNT)item, displayParams);
/*System.out.println("SimulationPanel: Added VectorMap " + item.getName());
				if(item instanceof AttributeVectorData){
//					maps.setDataStyle(item.getName(), ((AttributeVectorData)item).getStyle(
//							Constants.STATIC_FIELD_AREA, 0.8f, 0.1f, 1f, 0.5f, 0.8f, 0.1f));
					Hashtable<Integer, Color> colourMap = new Hashtable<Integer, Color>();
					colourMap.put(0, new Color(0.5f, 0.3f, 0f));
					colourMap.put(1, Color.GREEN.darker());
					colourMap.put(2, Color.YELLOW);
					maps.setDataStyle(item.getName(), ((AttributeVectorData)item).getStyle(
							Constants.INPUT_FIELD_CROP, colourMap));
				}
//				maps.setDataStyle(item.getName(), displayParams.getStyle(item.getName()));
*/		}
		if(item instanceof DataTX && displayParams.display(item.getId(), SimulationDisplayParams.SHOW_TEXT))
			texts.addDisplayItem((DataTX)item, pastData.get(item.getId()), time);
		if(item instanceof DataSet){
			for(Data subItem : ((DataSet)item).getItems())
				addDisplayItem(subItem, pastData, time, false);
		}
		if(toDisplay){
			data.revalidate2();
			graphs.revalidate2();
			data.repaint();
			graphs.repaint();
		}
	}
	
	/** Sets the values of objects on display */
	public void setDisplayItem(Data item){
		if(item instanceof Data0D){
			data.setDisplayItem((Data0D)item);
			if(item.getId() == Constants.TEMPORAL_TIME)
				texts.scrollTo(((Data0D.Integer)item).intValue());
		}
		if(item instanceof Data1D)
			data.setDisplayItem((Data1D)item);
		// Graphs do not get updated
//			graphs.setDisplayItem((Data1D)item);
		if(item instanceof Data2D)
//System.out.println("Setting Raster "+ item.getNameId() + ":\n" + Utils.arrayString(((RasterMap)item).getRasterArray()));
			maps.setDisplayItem((Data2D)item);
		if(item instanceof DataTX){
// Do nothing: we use time to display data. The redisplay process will set new data	
//			texts.setDisplayItem((DataTX)item);
		}
		if(item instanceof DataSet)
			for(Data subItem : ((DataSet)item).getItems())
				setDisplayItem(subItem);
	}

	/** Refreshes the gui's data values, updating old values */
	public void redisplay(){
//		super.repaint();
		if(maps != null)
			maps.redisplay(true);
		if(data != null)
			data.redisplay();
		if(graphs != null)
			graphs.redisplay();
		if(texts != null)
			texts.redisplay();
	}
    
	/** refreshes the gui's layout/size etc where values have been added or removed */
	public void revalidate(){
		if(maps != null)
			maps.revalidate();
		if(data != null)
			data.revalidate();
		if(graphs != null)
			graphs.revalidate();
		if(texts != null)
			texts.revalidate();
		super.revalidate();
	}
   
	/** For testing purposes only */
/*	public static void main(String[] args){
		JFrame testFrame = new JFrame("Testing SimulationGUI");
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.add(testSimulationPanel(testFrame), BorderLayout.CENTER);
		testFrame.setSize(new Dimension(600,430));
		testFrame.setVisible(true);
	}
*/	
	/** Returns a SimulationPanel initialised with test data */
/*	public static SimulationPanel testSimulationPanel(Frame parent){
		return new SimulationPanel(
				parent,
				MapViewThumb.testMapView(parent),
				DataView.testDataViewData(),
				DataView.testDataViewGraphs(),
				new TextScroller(new LinkedList<DataTX>()),
				new SimEventHandler(null)
				);
	}
*/

}
