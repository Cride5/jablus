package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.Parameters;

import java.io.File;

import jwo.landserf.gui.SimpleGISFrame;
import jwo.landserf.process.OpenThread;
import jwo.landserf.process.io.ImageIO;
import jwo.landserf.structure.RasterMap;

/*
	TODO:
	Separate floating point and integer raster data
	Check out alternitive tiff API's for storing double samples
		http://reader.imagero.com/ (looks like it supports 64-bit and LZW compression :-)
		http://xmlgraphics.apache.org/batik/
		http://www.cs.hut.fi/~framling/JVG/
		http://rsb.info.nih.gov/ij/
		http://java.sun.com/products/jimi/
		http://schmidt.devlib.org/jiu/index.html
		http://www.lizardworks.com/libs.html

*/
/** @inheritDoc */
public class RasterData implements Data2D {

    /** Unique class id */
    private static final long serialVersionUID = 5408627128335983404L;
       
	protected int id;
 //   protected float[][] data;
    
	private RasterMap raster;
    protected boolean toShow;
    
// For recreating RevenueGrid
//public boolean loaded;
    
	public Object clone(){
//		MatrixData c = new MatrixData(id, data, toShow); 
// For recreating RevenueGrid
//c.loaded = loaded;
		return new Object();
	}	
   
	public RasterData(int id, Parameters params, boolean toShow){ 
		this.id = id;
//		this.params = params;
		this.toShow = toShow;
	}

	
	public RasterData(int id, String path, Parameters params, boolean toShow){ 
		this.id = id;
//		this.params = params;
		this.toShow = toShow;
//		System.out.println("Reading file:" + path + "  ... success?" + readFile(path));
		this.raster = ImageIO.readRaster(path, 0, 0, new OpenThread(new SimpleGISFrame(), new File[]{}));
		if(raster == null)
			System.out.println("RasterData: ERROR - Couldn't read raster file " + path);
//System.out.println("Read raster with: " + raster.getHeader() + " rows:" + raster.getNumRows() + " cols:" + raster.getNumCols() + " maxAttr:" + raster.getMaxAttribute() + " minAttr:" + raster.getMinAttribute());
	}
	
	public RasterMap getRaster(){
		return raster;
	}
	
	/** Implementing Data2D */
	public int getCols() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public float getData(int x, int y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public float[][] getData() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public String getFormat() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public int getRows() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void readRaster(String file) {
	    // TODO Auto-generated method stub
	    
    }

	public void setData(int x, int y, float value) {
	    // TODO Auto-generated method stub
	    
    }

	public void setData(float[][] data) {
	    // TODO Auto-generated method stub
	    
    }

	public boolean toShow() {
	    return toShow;
    }

	public void writeRaster(String file) {
	    // TODO Auto-generated method stub
	    
    }

	/** @inheritDoc */
	public int getId() {
	    return id;
    }

	/** @inheritDoc */
	public String getName() {
	    return Constants.getName(id);
    }

	public String stringValue() {
	    return "RasterMap(" + getName() + ")";
    }

	
	public static void main(String[] args){
//		RasterData d = new RasterData("test", "/home/crider/uni/phd/code/jablus-0.51/data/crichton_maps2/aerial_data_ex.png", null, false);
//		System.out.println("Read raster with: " + d.getHeader() + " rows:" + d.getNumRows() + " cols:" + d.getNumCols() + " maxAttr:" + d.getMaxAttribute() + " minAttr:" + d.getMinAttribute());
	
//		RasterMap d = ImageIO.readRaster("/home/crider/uni/phd/code/jablus-0.51/data/crichton_maps2/aerial_data_ex.png", 0, 0, new OpenThread(new SimpleGISFrame(), new File[]{}));
		RasterData d = new RasterData(0, "/home/crider/uni/phd/code/jablus-0.51/data/crichton_maps2/aerial_data_ex.png", null, false);
		
	}
}
