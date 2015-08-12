package uk.co.crider.jablus.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.media.jai.JAI;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jwo.landserf.gui.GUIFrame;
import jwo.landserf.process.LSThread;
import jwo.landserf.process.OpenThread;
import jwo.landserf.process.SaveThread;
import jwo.landserf.process.io.FileIO;
import jwo.landserf.structure.ColourTable;
import jwo.landserf.structure.Footprint;
import jwo.landserf.structure.Header;
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
public class MatrixData implements Data2D {

    protected int id;
    protected float[][] data;
    
//	private RasterMap raster;
    protected boolean toShow;
    
	// For recreating RevenueGrid
	public boolean loaded;
    
	public MatrixData(int id, int rows, int cols){ this(id, rows, cols, false); }
	public MatrixData(int id, int rows, int cols, boolean toShow){
		this.id = id;
		this.data = new float[cols][rows];
		
//		raster = new RasterMap(params.ROWS, params.COLS, params.RASTER_FOOTPRINT, new Header(id));
		this.toShow = toShow;
		loaded = false;
	}

	
	/** @inheritDoc */
	public Object clone(){
		MatrixData c = new MatrixData(id, data, toShow); 
// For recreating RevenueGrid
c.loaded = loaded;
		return c;
	}	
	/** Constructor for cloning this object */
	public MatrixData(int id, float[][] data, boolean toShow){
		this.id = id;
		this.data = new float[data.length][];
		for(int y = 0; y < data.length; y++){
		this.data[y] = new float[data[y].length];
			for(int x = 0; x < data[y].length; x++){
				this.data[y][x] = data[y][x];
			}
		}
		this.toShow = toShow;
	}
	
	/** @inheritDoc */
	public int getId() {
	    return id;
    }

	/** Return the raster's id */
	public String getName() {
		return Constants.getName(id);
	}

	public float getData(int x, int y){
		return data[y][x];
	}
	
	public float[][] getData(){
		return data;
	}
	
	/** @inheritDoc */
	public String stringValue() {
	    return toString();
    }

	
/*	public float getMinValue(){
		return raster.getMinAttribute();
	}

	public float getMaxValue(){
		return raster.getMaxAttribute();
	}
	
*/	public int getCols(){
		if(getRows() == 0)
			return 0;
		return data[0].length;
	}
	
	public int getRows(){
		return data.length;
	}
	
	public void setData(int x, int y, float value){
		data[y][x] = value;
	}
	
	public void setData(float[][] data){
		this.data = data;
	}
	
//	public RasterMap getRaster() {
//		return raster;
//	}
	public boolean toShow() {
	    return toShow;
    }
	
	/** Returns the data inverted */
	public static float[][] invert_(float[][] in){
		if(in.length == 0 || in[0].length == 0)
			return new float[0][0];
		float[][] out = new float[in[0].length][in.length];
		for(int i = 0; i < in.length; i++){
			for(int j = 0; j < in[i].length; j++){
				out[i][j] = in[j][i];
			}
		}
		return out;
	}
	
	public String getFormat(){
		return "tif";
	}
	
	public void readRaster(String file){
		if(file == null || !new File(file).exists()){ 
// For recreating RevenueGrid
loaded = false;
//System.out.println("MatrixData.readRaster: not loaded " + id + ", " + file );
			return;
		}
		loaded = true;
		// Use JAI to read tiff file format
		RenderedImage img = (RenderedImage)JAI.create("fileload", file);
		// Extract data from image
		int width = img.getWidth();
		int height = img.getHeight();
		float[] data_ = new float[width * height];
		data_ = img.getData().getPixels(0, 0, width, height, data_);
		data = new float[height][width];
		for(int i = 0; i < data_.length; i++){
			data[i / width][i % width] = data_[i];
		}
//			System.out.println(Utils.arrayString(data));	
	}
	
	public void writeRaster(String file){
		if(file == null) return;
		// Encode data into buffered image
		int width = getCols();
		int height = getRows();
		float[] data_ = new float[width * height];
		for(int i = 0; i < data_.length; i++){
			data_[i] = data[i / width][i % width];
		}
		// Using greyscale to store floating point values.
		// Currently JAI doesn't support writing double precision (64-bit) samples to tiffs
		// so we have to settle for standard 32-bit floating point values, which is crap
		// ... so watch this space!
		ImageTypeSpecifier spec = ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[]{0}, DataBuffer.TYPE_FLOAT, false, false);
		BufferedImage img = spec.createBufferedImage(width, height);
		img.getRaster().setPixels(0, 0, width, height, data_);		
		// Use JAI to write tiff file
		JAI.create("filestore", img, file, "TIFF", null);
	}
	
	public String toString(){
		return Utils.arrayStringR(data);
	}
/*
	public void writeRaster_jav(String file) {
System.out.println("MatrixData: writing following data for " + id);
System.out.println(Utils.arrayString(data));
		int width = getCols();
		int height = getRows();
		ImageTypeSpecifier spec = ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[]{0}, DataBuffer.TYPE_DOUBLE, false, false);
		BufferedImage img = spec.createBufferedImage(width, height);
		float[] data = new float[width * height];
		for(int i = 0; i < data.length; i++){
			data[i] = this.data[i / width][i % width];
		}
		img.getRaster().setPixels(0, 0, width, height, data);
		try{
//			boolean writerFound = 
			ImageIO.write(img, "gif", new File(file));
//System.out.println("MatrixData: writerFound=" + writerFound);
		}catch(IOException e){
			e.printStackTrace();
		}
		// Test its worked
		double[] data2 = new double[width * height];
		data2 = img.getData().getPixels(0, 0, width, height, data2);
		for(int i = 0; i < data2.length; i++){
			this.data[i / width][i % width] = (float)data[i];
		}
System.out.println("MatrixData: dita written was ..");
System.out.println(Utils.arrayString(this.data));
	}
*/	

/*
	public void writeRaster_srf(String file) {
	    // Write out
//		String toWrite = fName(getNameId(), time);
//	    ImageIO.writeImage(toWrite, frame, null);
//System.out.println("Writing raster " + this + ", as " + toWrite);
//		GISFrame gisFrame = new SimpleGISFrame();
//		gisFrame.addRaster(this, GISFrame.PRIMARY);
//		gisFrame.setRaster1(this);
//toWrite = getNameId() + ".png";
//	    BinRasterIO.writeRaster(this, toWrite, FileHandler.IMAGE, gisFrame, new SaveThread(gisFrame, toWrite, FileHandler.IMAGE));
//	    TextRasterIO.writeRaster(this, toWrite, FileHandler.TEXT_R, gisFrame, new SaveThread(gisFrame, toWrite, FileHandler.TEXT_R));
//	    ImageIO.writeImage(toWrite, gisFrame, new SaveThread(gisFrame, toWrite, FileHandler.IMAGE));
//DataBuffer imgData = new DataBuffer();
//Raster rast = new Raster(new SampleModel(), imgData, new Point());
//BufferedImage buf = new BufferedImage(ColorModel cm, rast, boolean isRasterPremultiplied, Hashtable<?,?> properties)
//ImageIO.write(buf, "PNG" ,new File(toWrite));
//System.out.println(FileIO.getErrorMessage());
//		LandSerfIO.write(raster, toWrite);
	    //return new File(toWrite).getName();
    }
*/
	
	
/*
	public void readRaster_jav(String file){
		try{
//System.out.println("MatrixData: reading file " + file);
			BufferedImage img = ImageIO.read(new File(file));
			int width = img.getWidth();
			int height = img.getHeight();
			double[] data = new double[width * height];
			data = img.getData().getPixels(0, 0, width, height, data);
			for(int i = 0; i < data.length; i++){
				this.data[i / width][i % width] = (float)data[i];
			}
System.out.println("MatrixData: read following data for " + id);
System.out.println(Utils.arrayString(this.data));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
*/
	
/*
	public void readRaster_srf(String file){
		// TODO!
//		String toRead = fName(file);
		//String toRead = baseDir.getPath() + File.separator + id;
//System.out.println("Reading raster " + toRead + "\n\n");
//		RasterMap input = BinRasterIO.readRaster(toRead, FileHandler.IMAGE, new SimpleGISFrame(), null); 
//		RasterMap input = LandSerfIO.readRaster(toRead);
//		System.out.println(", read " + input);
		//return new MatrixData(input);
	}
*/
	
//	private static String fName(int id, int time){
//		return fName(id + "-" + time + ".srf");
//	}
	
//	private static String fName(int id){
//	    return Constants.JABLUS_EXPT_DIR + File.separator + Constants.JABLUS_EXPT_NAME + File.separator + id;
//	}

	public static void main(String[] args){
		if(false){
			BufferedImage img = createImage();
			JAI.create("filestore", img, "test.tif", "TIFF", null);
		}
		else{
		     RenderedImage img = (RenderedImage)JAI.create("fileload", "test.tif");
				// Print image
				int width = img.getWidth();
				int height = img.getHeight();
				double[] data_ = new double[width * height];
				data_ = img.getData().getPixels(0, 0, width, height, data_);
				double[][] data = new double[height][width];
				for(int i = 0; i < data_.length; i++){
					data[i / width][i % width] = (float)data_[i];
				}
				System.out.println(Utils.arrayStringR(data));
		}
//		JAI.create("fileStore", new ParameterBlock().add("test.tif").add("TIFF").addSource(img));
		
	     // Encode the file as a PNG image.
	   //  FileOutputStream stream;
      //  try {
	 //       stream = new FileOutputStream("test.png");
	//	     JAI.create("encode", img, stream, "PNG", null);

		     // Store the image in the BMP format.
//		     JAI.create("filestore", img, "test.png", "PNG", null);
      //  } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	 //       e.printStackTrace();
      //  }

	     // Define the source and destination file ids.
/*	     String inputFile = /images/FarmHouse.tif
	     String outputFile = /images/FarmHouse.bmp

	     // Load the input image.
	     RenderedOp src = JAI.create("fileload", inputFile);

	     // Encode the file as a BMP image.
	     FileOutputStream stream =
	         new FileOutputStream(outputFile);
	     JAI.create("encode", src, stream, BMP, null);

	     // Store the image in the BMP format.
	     JAI.create("filestore", src, outputFile, BMP, null);
*/

//		landSerfAPI();
//		javaAPI();
	}
	
	public static void landSerfAPI(){
		RasterMap m;
		GUIFrame f = new GUIFrame("egg");
		String file = "test1.gif";
//		SimpleGISFrame f = new SimpleGISFrame();
		if(false){
			float[][] dataMatrix1 = new float[][]{
					new float[]{0, 0.03f, 0, 0, 0},
					new float[]{0, 1, 0.5f, 1, 0},
					new float[]{0, 0, 1, 0, 0},
					new float[]{1, 0, 0, 0, 1},
					new float[]{0, 1, 1, 1, 0},				
			};
			float[][] dataMatrix = new float[][]{
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},
					new float[]{1, 1, 1, 1, 1},				
			};
			m = new RasterMap(5, 5, new Footprint(0, 0, 1, 1), new Header("head", "f", "d", "e"));
			m.setAttributes(dataMatrix);
			m.setColourTable(ColourTable.getPresetColourTable(ColourTable.GREYSCALE));
	//		f.addRaster(m, GISFrame.PRIMARY);
		//	f.getGraphicsArea().setRaster1(m);
		//	f.getGraphicsArea().displayRast();
			f.setRaster1(m);
			f.redisplay(true);
	//		f.repaint();
			LSThread t = new SaveThread(f, file, FileIO.BIG_ENDIAN);
			//t = null;
	System.out.println("f=" + f + ", t=" + t);
			System.out.println("Image write succeeded?" + jwo.landserf.process.io.ImageIO.writeImage(file, f, t));
			System.out.println(FileIO.getErrorMessage());
			f.redisplay(true);
		}
		else{
			LSThread t = new OpenThread(f, new File[]{new File(file)});//FileIO.BIG_ENDIAN);
			m = jwo.landserf.process.io.ImageIO.readRaster(file, 5, 5, t);
		
		}
		System.out.println(Utils.arrayStringR(m.getRasterArray()));
		f.setRaster1(m);
		f.getFrame().pack();
		f.getFrame().setVisible(true);
		f.redisplay(true);
		
	}
	
	public static void javaAPI(){
//		System.out.println(Utils.arrayString(ImageIO.getReaderFormatNames()));
//		System.out.println(Utils.arrayString(ImageIO.getWriterFormatNames()));
//		raster.setPixels(0, 0, width, height, data);
		BufferedImage img = null;
		String type = "gif";
		
//		if(false){
		if(true){
			img = createImage();
			// Write image to disk
			try {
				System.out.println("Write success?" + ImageIO.write(img, type, new File("test." + type)));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			try {
		        // Read image from disk
	            img = ImageIO.read(new File("test." + type));
	            // TODO: Figure out why read flattens all values
	            //img.getColorModel().getColorSpace()
            } catch (IOException e) {
	            e.printStackTrace();
            }
		}
		
		System.out.println("Image = " + img);
		
		// Print image
		int width = img.getWidth();
		int height = img.getHeight();
		double[] data_ = new double[width * height];
		data_ = img.getData().getPixels(0, 0, width, height, data_);
		double[][] data = new double[height][width];
		for(int i = 0; i < data_.length; i++){
			data[i / width][i % width] = (float)data_[i];
		}
		System.out.println(Utils.arrayStringR(data));
		
		
		// Display Image
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel imgLabel = new JLabel();
		//BufferedImage img = new BufferedImage(spec.getColorModel(), raster, spec.getColorModel().isAlphaPremultiplied(), null);
		imgLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING)));
		f.add(imgLabel);
		f.pack(); //setSize(new Dimension(100, 100));
		f.setVisible(true);
		
	}
	
	public static BufferedImage createImage(){
/*		double[][] dataMatrix1 = new double[][]{
				new double[]{0, 0.03, 0, 0, 0},
				new double[]{0, 1, 0.5, 1, 0},
				new double[]{0, 0, 1, 0, 0},
				new double[]{1, 0, 0, 0, 1},
				new double[]{0, 1, 1, 1, 0},				
		};
*/		double[][] dataMatrix = new double[][]{
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},				
		};
		int height = dataMatrix.length;
		int width = dataMatrix[0].length;
		float[] data = new float[width * height];
		for(int i = 0; i < data.length; i++){
			data[i] = (float)dataMatrix[i / width][i % width];
		}
//		int scanlineStride = width;
//		int pixelStride = 1;
//		int[] bandOffsets = new int[]{0};
//		Point location = null; // new Point(0,0);
		//SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_DOUBLE, width, height, pixelStride, scanlineStride, bandOffsets);
		ImageTypeSpecifier spec = ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[]{0}, DataBuffer.TYPE_FLOAT, false, false);
//		spec.getColorModel();
//		spec.getSampleModel();
//		WritableRaster raster = Raster.createWritableRaster(spec.getSampleModel(), location);
		BufferedImage img = spec.createBufferedImage(width, height);
		
		img.getRaster().setPixels(0, 0, width, height, data);		
		return img;
	}
	
	public static BufferedImage createImage2(){
/*		double[][] dataMatrix1 = new double[][]{
				new double[]{0, 0.03, 0, 0, 0},
				new double[]{0, 1, 0.5, 1, 0},
				new double[]{0, 0, 1, 0, 0},
				new double[]{1, 0, 0, 0, 1},
				new double[]{0, 1, 1, 1, 0},				
		};
*/		double[][] dataMatrix = new double[][]{
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 0.5, 1, 1},
				new double[]{1, 1, 1, 1, 1},
				new double[]{1, 1, 1, 1, 1},				
		};
		int height = dataMatrix.length;
		int width = dataMatrix[0].length;
		int[] data = new int[width * height];
		for(int i = 0; i < data.length; i++){
			data[i] = (int)dataMatrix[i / width][i % width];
		}
//		int scanlineStride = width;
//		int pixelStride = 1;
//		int[] bandOffsets = new int[]{0};
//		Point location = null; // new Point(0,0);
		//SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_DOUBLE, width, height, pixelStride, scanlineStride, bandOffsets);
//		ImageTypeSpecifier spec = ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[]{0}, DataBuffer.TYPE_INT, false, false);
//		spec.getColorModel();
//		spec.getSampleModel();
//		WritableRaster raster = Raster.createWritableRaster(spec.getSampleModel(), location);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
System.out.println("img="+img);
		
//		img.getRaster().setPixels(0, 0, width, height, data);		
		return img;
	}
}
