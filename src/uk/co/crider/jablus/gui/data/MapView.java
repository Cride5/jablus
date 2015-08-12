package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.AttributeVectorData;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataNT;
import uk.co.crider.jablus.data.RasterData;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jump.KeyRenderer;
import uk.co.crider.jablus.utils.RasterReader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jwo.landserf.gui.SimpleGISFrame;
import jwo.landserf.process.OpenThread;
import jwo.landserf.process.io.ImageIO;
import jwo.landserf.structure.ColourTable;
import jwo.landserf.structure.RasterMap;

import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelContext;
import com.vividsolutions.jump.workbench.ui.cursortool.SelectFeaturesTool;
import com.vividsolutions.jump.workbench.ui.plugin.scalebar.ScaleBarRenderer;
import com.vividsolutions.jump.workbench.ui.renderer.Renderer;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

/** A panel for viewing raster and vector maps */
public class MapView extends JPanel{
	
	/** Unique class ID */
    private static final long serialVersionUID = -6277872001520857930L;
    
//	private Frame parent;
	
	private LayerViewPanel view;
	
	private Hashtable<Integer, Layer> layers;
	
	private Hashtable<Integer, VectorData> vectors;
	
	private Hashtable<Integer, HashSet<Style>> layerStyles;
	
//	private Hashtable<Integer, Listener> listeners;

	private SelectFeaturesTool selector;

	private MouseEvent fieldSelectEvent;
	
	private KeyRenderer key;
	
	public MapView(){
    	super(new BorderLayout());
 //   	this.parent = parent;
    	
		// Initialise panel for displaying layers
		view = new LayerViewPanel(new LayerManager(), new LayerViewPanelContext(){
			public void setStatusMessage(String m){
//				System.out.println("STATUS: " + m); 
				}
			public void warnUser(String m){
				System.out.println("MapView - WARNING: " + m); 
			}
			public void handleThrowable(Throwable t){
				t.printStackTrace(); }
		});

		view.getRenderingManager().putAboveLayerables(
				ScaleBarRenderer.CONTENT_ID,
				new Renderer.Factory(){
					public Renderer create(){
						return new ScaleBarRenderer(view);
					}});
		ScaleBarRenderer.setEnabled(true, view);
		
		key = new KeyRenderer(view);
		view.getRenderingManager().putAboveLayerables(
				KeyRenderer.CONTENT_ID,
				new Renderer.Factory(){
					public Renderer create(){
						return key;
					}});
		KeyRenderer.setEnabled(true, view);

		view.getRenderingManager().render(ScaleBarRenderer.CONTENT_ID);
		// Remove (unregister) mouse motion listeners
		// prevents unhandled events causing exceptions
		MouseMotionListener[] listeners = view.getMouseMotionListeners();
		for(int i = 0; i < listeners.length;  i++){
			view.removeMouseMotionListener(listeners[i]);
		}
		add(view, BorderLayout.CENTER);

/*
		final Coordinate start = new Coordinate();
		view.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				start.x = e.getX();
				start.y = e.getY();
			}
			public void mouseReleased(MouseEvent e) {
				Geometry g = new GeometryFactory().createLinearRing(new Coordinate[]{
						start,
						new Coordinate(e.getX(), start.y),
						new Coordinate(e.getX(), e.getY()),
						new Coordinate(start.x, e.getY()),
						start
				});
				view.getSelectionManager().clear();
//		        if(view.getFence() != null){
			    Map layerToFeaturesInFenceMap = view.visibleLayerToFeaturesInFenceMap(g);
	System.out.println("Selected items:" + layerToFeaturesInFenceMap.keySet());
		        for(Iterator i = layerToFeaturesInFenceMap.keySet().iterator(); i.hasNext();) {
		            Layer layer = (Layer) i.next();
		            if (layer == new FenceLayerFinder(view).getLayer()) {
		                continue;
		            }
//		            if (skipUnselectedLayers && !selectedLayers.contains(layer)) {
//		                continue;
//		            }
		            view.getSelectionManager().getFeatureSelection().selectItems(
		                layer,
		                (Collection) layerToFeaturesInFenceMap.get(layer));
		        }
		       }
		});
*/		
		selector = new SelectFeaturesTool(){
			public void mouseClicked(MouseEvent e){
				fieldSelectEvent = e;
//System.out.println(view.getSelectionManager().getSelectedItems().size());
				if(e.getButton() == MouseEvent.BUTTON1
				|| view.getSelectionManager().getSelectedItems().size() <= 1)
					super.mouseClicked(e);
			}
		};
		view.addMouseListener(selector);
		selector.activate(view);

/*		selector.add(new Listener(){
			public void gestureFinished(){
				for(Object f : view.getSelectionManager().getFeaturesWithSelectedItems()){
					System.out.println("Selected feature:" + ((Feature)f).getID());
				}
				System.out.println("right button used?" + selector.isRightMouseButtonUsed());
			}
		});		
*/
		layers = new Hashtable<Integer, Layer>();
		vectors = new Hashtable<Integer, VectorData>();
//		layerStyles = new Hashtable<String, Style>();
		layerStyles = new Hashtable<Integer, HashSet<Style>>();
	}

	public MouseEvent getFieldSelectEvent(){
		return fieldSelectEvent;
	}
	public SelectFeaturesTool getSelector(){
		return selector;
	}
	public LayerViewPanel getLayerViewPanel(){
		return view;
	}
	public VectorData getVectorData(int id){
		return vectors.get(id);
	}
	public void addDisplayItem(RasterData item){

	}

	public void addDisplayItem(DataNT item){ addDisplayItem(item, null); } 
	public void addDisplayItem(DataNT item, DisplayParams params){ 
//System.out.println("MapView: Adding NT data...");
		Object data = item.getData();
		if(data instanceof FeatureCollection){
			if(item instanceof VectorData)
				vectors.put(item.getId(), (VectorData)item);
			Layer layer = view.getLayerManager().addLayer(
				"Category Name", item.getName(), (FeatureCollection)data);
			layer.setEditable(false);
			layers.put(item.getId(), layer);
//System.out.println("Setting up style for:" + item.getName());						
			// Set up visual attributes
			if(params != null){
				if(item instanceof VectorData){
//System.out.println("Setting up style for (2):" + item.getName());
					setDataStyle(item.getId(), ((VectorData)item).getStyle(params));
				}
				if(item instanceof AttributeVectorData){
					setDataStyle(item.getId(), ((AttributeVectorData)item).getStyle(params));
				}
			}
		}
		revalidate();
	}
	
	public void setDataStyle(int id, Style style){
		Layer layer = layers.get(id);
		HashSet<Style> styles = layerStyles.get(id);
//System.out.println(Constants.getName(id) + " existing styles = " + styles);
		if(style != null && layer != null){
			if(styles == null){
				styles = new HashSet<Style>();
				layerStyles.put(id, styles);
			}
			if(!styles.contains(style)){
				layer.addStyle(style);
				styles.add(style);
//System.out.println(Constants.getName(id) + " adding style: " + style);
			}
			for(Style s : styles){
				s.setEnabled(false);
			}
			style.setEnabled(true);
//System.out.println("Setting style");
			key.setStyle(style);
			layer.fireAppearanceChanged();
		}
	}

	public void revalidate(){
		super.revalidate();
		// Pan and zoom to extent of features
		if(view != null){
			try{ view.getViewport().zoomToFullExtent(); }
			catch(Exception e){ e.printStackTrace(); }
		}
	}

	public void redisplay(boolean doRecalc) {
//		view.revalidate();
		view.repaint();
		
/*		if(doRecalc)
			mapView.displayRast();
		else
			mapView.repaint();
*/    }

	
	/** Called to update an existing map without adding a new one */
	public void setDisplayItem(Data2D item){
/*		RasterMap raster = rasterTable.get(item.getName());
//System.out.println(raster);
		if(raster == null) return;
		// If the item is the same object as original then restore original attributes
		if(item == raster)
			raster.setAttributes(origAttributes.get(item.getName()));
		else
			raster.setAttributes(item.getData());
		redisplay(true);
*/	}

	
	
	/** For testing purposes only */
	public static void main(String[] args){
		JFrame testFrame = new JFrame("Testing Map View");
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.setSize(new Dimension(640, 480));
		testFrame.setVisible(true);
		
		
		
		MapView map = new MapView();
//		map.addDisplayItem(new VectorData_(jablus.models.dairy.Constants.STATIC_FIELD_MAP, "/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/fields.shp", null, true));

		
		
//		testFrame.add(testMapView(testFrame), BorderLayout.CENTER);
	}
	
	
	/** Returns a map view with test data initialised */
	public static MapView testMapView(Frame parent){
		
		// Initialise first test raster
		RasterMap testRaster1 = new RasterMap(10, 10);
		float[][] raster = new float[][]{
				new float[]{1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f},
				new float[]{1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f},
				new float[]{1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
				new float[]{1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
				new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f},
				new float[]{1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f},
				new float[]{1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f}
		};
		testRaster1.setAttributes(raster);
		
		// Initialise second test raster
		RasterMap testRaster2 = RasterReader.correctGIF(
				ImageIO.readRaster(
						Constants.JABLUS_DATA_DIR + File.separator + "suitability_blue_tile1.gif",
						5,
						5,
						new OpenThread(new SimpleGISFrame(), new File[]{new File(Constants.JABLUS_DATA_DIR + File.separator + "suitability_blue_tile1.gif")})
				),
				"test",
				0.4f,
				0.6f
		);
		
		// Setup colour tables
		ColourTable landUseCol = null; //LandUse.getColourTable(Experiments.create(null));
		testRaster2.setColourTable(landUseCol);
		testRaster1.setColourTable(landUseCol);
		
		testRaster2.setColourTable(ColourTable.getPresetColourTable(ColourTable.GREYSCALE));

		// Create the map view
		MapView mapView = new MapView();
/*		mapView.addRaster(testRaster1, GISFrame.PRIMARY);
		mapView.addRaster(testRaster2, GISFrame.PRIMARY);
		mapView.updateInputGUI();
*/		mapView.redisplay(true);
		mapView.repaint();
		
		return mapView;
	}
}
