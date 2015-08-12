package uk.co.crider.jablus.utils;

import uk.co.crider.jablus.models.dairy.Constants;

import java.io.File;

import jwo.landserf.gui.SimpleGISFrame;
import jwo.landserf.process.OpenThread;
import jwo.landserf.process.io.ImageIO;
import jwo.landserf.structure.RasterMap;

/** Used for filtering rasters */
public class RasterReader {
	
	public static RasterMap importGIF(String fileName, String name, int rows, int cols){
		return RasterReader.correctGIF(
				ImageIO.readRaster(
						Constants.JABLUS_DATA_DIR + File.separator + fileName,
						cols,
						rows,
						new OpenThread(new SimpleGISFrame(), new File[]{new File(Constants.JABLUS_DATA_DIR + File.separator + "suitability_blue.gif")})
				),
				name,
				1.0f,
				0.0f
		);
	}
	
	public static RasterMap correctGIF(RasterMap raster, String name, float mult, float add) {
		raster.getHeader().setTitle(name);
	    return correctGIF(raster, mult, add);
    }
	public static RasterMap correctGIF(RasterMap raster){ return correctGIF(raster, 1, 0); }	
	public static RasterMap correctGIF(RasterMap raster, float mult){ return correctGIF(raster, mult, 0); }
	public static RasterMap correctGIF(RasterMap raster, float mult, float add){
		return filter(raster, mult / 16800000f, 1f * mult + add);
	}
	
	public static RasterMap filter(RasterMap raster, float mult){ return filter(raster, mult, 0); }
	public static RasterMap filter(RasterMap raster, float mult, float add){
		for(int i = 0; i < raster.getNumCols(); i++){
			for(int j = 0; j < raster.getNumRows(); j++){
				raster.setAttribute(i, j, raster.getAttribute(i, j) * mult + add);
			}
		}
		return raster;
	}
	
}
