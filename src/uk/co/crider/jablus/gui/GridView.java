package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import jwo.landserf.gui.ColourBar;
import jwo.landserf.structure.ColourTable;


/** Represents a grid of values */
public class GridView extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = -3744086710700210447L;
	private int rows;
	private int cols;
	private JLabel[][] cells;
	private ColourTable colourTable;
	private int precision = 1; // 1 decimal place
//	private Frame parent;
	
	/**
	 * Construct a grid view with specified number of rows and columns
	 * all cell vlues are initialised to 0
	 */
	public GridView(int rows, int cols, Frame parent){ this(rows, cols, parent, null); }
	public GridView(int rows, int cols, Frame parent, ColourTable colourTable){ this(new double[rows][cols], parent, colourTable); }
	/** Construct a grid view initialised to data in the array */
	public GridView(double[][] data, Frame parent){ this(data, null, null); }
	public GridView(double[][] data, Frame parent, ColourTable colourTable){
		rows = data.length;
		cols = rows == 0 ? 0 : data[0].length;
//		this.parent = parent;
		this.colourTable = colourTable;
		setLayout(new BorderLayout());
		JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 0, 0));
		JPanel rowLabels = new JPanel(new GridLayout(rows, 1, 0, 0));
		JPanel colLabels = new JPanel(new GridLayout(1, cols, 0, 0));
		cells = new JLabel[rows][cols];
		for(int i = 0; i < rows; i++){
			rowLabels.add(new JLabel("" + (char)(i+65) + " "));
			for(int j = 0; j < cols; j++){
				if(i == 0) {
					JLabel colLabel = new JLabel(""+(j+1));
					colLabel.setHorizontalAlignment(SwingConstants.CENTER);
					colLabels.add(colLabel);
				}
				cells[i][j] = new JLabel(Utils.roundString(data[i][j], precision) + "  ", SwingConstants.CENTER);
				//cells[i][j].setBackground(Color.RED);
				cells[i][j].setOpaque(true);
				cells[i][j].setBorder(new LineBorder(Color.LIGHT_GRAY));
				gridPanel.add(cells[i][j]);
			}
		}
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(colLabels, BorderLayout.SOUTH);
		rightPanel.add(gridPanel, BorderLayout.CENTER);
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(rowLabels, BorderLayout.WEST);
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(0, 20));
		leftPanel.add(spacer, BorderLayout.SOUTH);
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);
		if(colourTable != null){
			JPanel colPanel = new JPanel(new BorderLayout());
			colPanel.add(new JLabel(" " + Utils.roundString(colourTable.getMinIndex(), 1) + " "), BorderLayout.WEST);
			colPanel.add(new JLabel(" " + Utils.roundString(colourTable.getMaxIndex(), 1) + " "), BorderLayout.EAST);
			ColourBar bar = new ColourBar(parent, colourTable, false, false);
			bar.setPreferredSize(new Dimension(0, 20));
			colPanel.add(bar, BorderLayout.CENTER);
			//colPanel.setBorder(new LineBorder(Color.BLACK,1));
			colPanel.add(new JPanel(), BorderLayout.NORTH);
			add(colPanel, BorderLayout.SOUTH);
		}
	}
	/** Set the value of a perticular cell */
	public void setValue(int row, int col, double value){
		if(row >= 0 && row < rows && col >=0 && col < cols)
			cells[row][col].setText(""+Utils.roundString(value, precision));
	}
	
	/** Set the value of a perticular cell */
	public void setColourValue(int row, int col, double value){
		if(colourTable != null && row >= 0 && row < rows && col >=0 && col < cols)
			cells[row][col].setBackground(new Color(colourTable.findColour((float)value)));
	}
	
	/** Set the value of the entire grid.
	 * The array must be the same size as the grid
	 */
	public void setValues(double[][] data){
		if(data.length != rows || data.length <= 0 || data[0].length != cols)
			return;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				cells[i][j].setText(""+Utils.roundString(data[i][j], precision));
			}
		}
	}
	
	/** Set the value of the entire grid.
	 * The array must be the same size as the grid
	 */
	public void setValues(String[][] data){
//System.out.println("GridView: Setting data:" + Utils.arrayString(data));
		if(data.length != rows || data.length <= 0 || data[0].length != cols)
			return;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				cells[i][j].setText(data[i][j]);
			}
		}
	}

	/** Set the value of the entire grid.
	 * The array must be the same size as the grid
	 */
	public void setColourValues(double[][] data){
		if(colourTable == null || data.length != rows || data.length <= 0 || data[0].length != cols)
			return;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++)
//System.out.println("colVal=" + colVal + " redVal=" + (colVal & 0x0F00 >> 2) + ", 0xF00=" + 0xF00);
//				cells[i][j].setBackground(new Color(colVal & 0x0F00 >> 2, colVal & 0x00F0 >> 1, colVal & 0x000F));
				cells[i][j].setBackground(new Color(colourTable.findColour((float)data[i][j])));
		}
	}

	/** For testing */
	public static void main(String[] args){
		ColourTable col = ColourTable.getPresetColourTable(ColourTable.GREYSCALE, -1, 1);
		//col.addDiscreteColourRule(0.5f, 45, 56, 67);
		int val = col.findColour(-10.0f);
		int red = (val & 0xF000) >> 16;
		int blue = (val & 0x0F00) >> 8;
		int green = (val & 0x00F0) >> 4;
		int alpha = (val & 0x000F);
		System.out.println("Colour val=" + val + ", red=" + red + ", blue=" + blue + ", green=" + green + ", alpha=" + alpha);
	}
}
