package uk.co.crider.jablus.gui.data;


import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JSplitPane;

import jwo.landserf.gui.Thumbnail;
import jwo.landserf.gui.ThumbnailViewer;
import jwo.landserf.structure.RasterMap;
import jwo.landserf.structure.VectorMap;

/** RasterMap view for viewing multiple maps,
 * incorperates a raster map view and thumbnail chooser */
public class MapViewThumb extends MapView{
	
	/** Unique class ID */
    private static final long serialVersionUID = -6277872001520857930L;
    private JSplitPane viewPane;
    private ThumbnailViewer mapThumbs;
    
    public MapViewThumb(Frame parent){
    	super();
    	viewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
//		mapThumbs = new ThumbnailViewer(this);  
		viewPane.setLeftComponent(mapThumbs);
//		viewPane.setRightComponent(mapView);
		add(viewPane, BorderLayout.CENTER);
	}
    
    public void addRaster(RasterMap map, int selection){
//    	super.addRaster(map, selection);
    	mapThumbs.addThumbnail(new Thumbnail(map));
    }
    
    public void addVectorMap(VectorMap map, int selection){
 //   	super.addVectorMap(map, selection);
    	mapThumbs.addThumbnail(new Thumbnail(map));
    }

    
	public void redisplay(boolean doRecalc) {
		super.redisplay(doRecalc);
		if(doRecalc)
			mapThumbs.updateView();
		else
			mapThumbs.repaint();
    }
}
