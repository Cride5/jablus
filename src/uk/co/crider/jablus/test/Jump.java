package uk.co.crider.jablus.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileReader;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelContext;

/** 
 * Test loading and visualising of vector data
 * AUTHOR: Conrad Rider (www.crider.co.uk)
 */
public class Jump {
	
	// Shapefiles to load and display
	public static final String[] SHAPEFILES = new String[]{
		"/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/fields.shp",
		"/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/roads.shp",
		"/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/river.shp",
		"/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/buildings.shp",
	};

	public static void main(String[] args){
		
		// Initialise panel for displaying layers
		LayerViewPanel view = new LayerViewPanel(new LayerManager(), new LayerViewPanelContext(){
			public void setStatusMessage(String m){
				System.out.println("STATUS: " + m); }
			public void warnUser(String m){
				System.out.println("WARNING: " + m); }
			public void handleThrowable(Throwable t){
				t.printStackTrace(); }
		});
		// Remove (unregister) mouse motion listeners
		// prevents unhandled events causing exceptions
		MouseMotionListener[] listeners = view.getMouseMotionListeners();
		for(int i = 0; i < listeners.length;  i++){
			view.removeMouseMotionListener(listeners[i]);
		}

		// Read shape files
		ShapefileReader reader = new ShapefileReader();
		List<FeatureCollection> featureList = new LinkedList<FeatureCollection>();
		for(int i = 0; i < SHAPEFILES.length; i++){
			try{
				FeatureCollection features = reader.read(new DriverProperties(SHAPEFILES[i]));
				// Report size of read data
				System.out.println("Read " + features.size() + " features from " + SHAPEFILES[i]);
				// Add data to list
				featureList.add(features);
			}catch(Exception e){
				// Report error on read failure
				System.out.println("Error reading features from " + SHAPEFILES[i]);
				e.printStackTrace();
			}
		}
		
		// Create and add layer for each feature set (shapefile)
		for(FeatureCollection features : featureList){
			Layer layer = view.getLayerManager().addLayer(
					"Category Name", "Layer Name", features);
			// Set up visualisation style here...
//			layer.getBasicStyle().setFillColor(Color.GREEN);
		}
		
		// Initialise window and add layer view panel
		JFrame frame = new JFrame("Jump Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(640, 480));
		frame.add(view, BorderLayout.CENTER);
		frame.setVisible(true);

		// Try various functions to display data
//		view.fireSelectionChanged();
//		view.getSelectionManager().updatePanel();
//		view.getRenderingManager().render(layer);
//		view.getRenderingManager().renderAll();
//		view.getRenderingManager().repaintPanel();
		// Pan and zoom to extent of features
		try{ view.getViewport().zoomToFullExtent(); }
		catch(Exception e){ e.printStackTrace(); }
//		view.repaint();
//		view.superRepaint();
		
	}
}
