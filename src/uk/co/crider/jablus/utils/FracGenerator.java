package uk.co.crider.jablus.utils;

import jwo.landserf.gui.GISFrame;
import jwo.landserf.process.FracSurfaceThread;
import jwo.landserf.structure.RasterMap;

/** Generates a fractal surface */
public class FracGenerator {
	
	/** Generate fractal surface */
	public static void genFractal(RasterMap rasterMap, GISFrame gisFrame, float mean, float stdev, long seed){
		gisFrame.addRaster(rasterMap,GISFrame.PRIMARY);

		// Create and start threaded process to generate fractal surface.
		FracSurfaceThread fracThread = new FracSurfaceThread(gisFrame, 2.1f, mean, stdev, seed);
		fracThread.start();
		try
		{
			fracThread.join();    // Join thread (i.e. wait until it is complete).
		}
		catch (InterruptedException e)
		{
			System.err.println("Error: Fractal generation thread interrupted.");
			return;
		}
		System.out.println("Fractal surface created.");
	}

}
