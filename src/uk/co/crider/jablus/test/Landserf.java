package uk.co.crider.jablus.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.gui.data.MapView;
import uk.co.crider.jablus.gui.data.MapViewThumb;
import jwo.landserf.gui.GISFrame;
import jwo.landserf.gui.SimpleGISFrame;
import jwo.landserf.process.OpenThread;
import jwo.landserf.process.io.ShapefileIO;
import jwo.landserf.structure.AttributeTable;
import jwo.landserf.structure.ColourTable;
import jwo.landserf.structure.VectorMap;

public class Landserf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String fileRiver = "/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/river.shp";
		String fileFields = "/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/fields.shp";
		String fileRoads = "/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/roads.shp";
		String fileBuilds = "/home/crider/uni/phd/code/jablus-0.52/data/crichton_maps3/buildings.shp";
		
		// TODO Auto-generated method stub
		GISFrame gframe = new SimpleGISFrame();
		VectorMap river = readVector(fileRiver, gframe);
		VectorMap fields = readVector(fileFields, gframe);
		VectorMap roads = readVector(fileRoads, gframe);
		VectorMap builds = readVector(fileBuilds, gframe);
//		river.setColourTable();
///		fields.setAttributeTable();
//		fields.setColourTable(ColourTable.getPresetColourTable(ColourTable.IMHOF_L1));
//		roads.setColourTable();
//		builds.setColourTable();
		
		AttributeTable table = fields.getAttributeTable();
		int cols = table.getColumnCount();
		int rows = table.getRowCount();
		for(int i = 0; i < cols; i++){
			System.out.println(table.getColumnName(i));
		}
		table.addColumn("Crop");
		for(int i = 0; i < rows; i++){
			table.setValueAt(Math.random() * 3, i, cols);
		}
		table.setActiveColumn(cols);
		float max = fields.getMaxAttribute();
		float min = fields.getMinAttribute();
		fields.setColourTable(ColourTable.getPresetColourTable(ColourTable.DEFAULT, min, max));
		System.out.println("final col name: " + table.getColumnName(cols) + ", min:" + min + " max:" + max + " activecol:" + table.getActiveColumn() + " isnumeric?" + table.isNumeric());
		VectorMap compbi = new VectorMap();
		
		// Initialise window and add layer view panel
		JFrame frame = new JFrame("Landserf Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(640, 480));

		MapView maps = new MapViewThumb(frame);
//		maps.addVectorMap(fields, GISFrame.PRIMARY);
		
		frame.add(maps, BorderLayout.CENTER);
		frame.setVisible(true);
		
		
//		maps.revalidate();
//		maps.repaint();
//		maps.setVisible(true);
 
 
}
	
	private static VectorMap readVector(String file, GISFrame frame){
		VectorMap vector = ShapefileIO.readVector(file, frame, new OpenThread(frame, new File[]{}));
		if(vector == null)
			System.out.println("VectorData: ERROR - Couldn't read shape file " + file);
		return vector;
	}

}
