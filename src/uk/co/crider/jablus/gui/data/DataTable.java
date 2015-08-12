package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** A basic table implementation for visuialising arbtrary data stored in Object arrays */
public class DataTable extends JPanel {

	/** Unique ID */
    private static final long serialVersionUID = 4021744340244138148L;
    
    // Cell types
    public static final int NORMAL = 0;
    public static final int BALANCE = 1;
    
 	private int[] weights;
	private int[] cTypes;
	private GridBagConstraints c;
	private GridBagLayout layout;
	private int prcn = 2;
	
	private JLabel spacer;
	
//	private static final JLabel refPanel = new JLabel(".00");
	
//	private ArrayList<JLabel[]> cells;
	private List<DataLabel> dynamicCells;
    
	public DataTable(Object[][] data){
		this(data, new int[]{1}, new int[]{0}); }
	public DataTable(Object[][] data, int[] weights){
		this(data, weights, new int[]{0}); }
	public DataTable(Object[][] data, int[] weights, int[] cTypes){
		this(toList(data), weights, cTypes); }
	private static List<Object[]> toList(Object[][] data){
		// Prepare data for table
		List<Object[]> list = new LinkedList<Object[]>();
		for(int i = 0; i < data.length; i++){
			list.add(data[i]);
		}
		return list;
	}
	public DataTable(List<Object[]> data, int[] weights){
		this(data, weights, new int[]{0}); }
	public DataTable(List<Object[]> data, int[] weights, int[] cTypes){
//		this.data = data;
		this.weights = weights;
		this.cTypes = cTypes;
//		cells = new ArrayList<JLabel[]>();
		layout = new GridBagLayout();
		setLayout(layout);
		c = new GridBagConstraints();
		c.ipady = 5;
		c.weighty = 0.0;
		dynamicCells = new LinkedList<DataLabel>();
		spacer = new JLabel();
//		c.fill = GridBagConstraints.BOTH;
		for(Object[] row : data){
			addRow(row);
//			c.gridwidth = GridBagConstraints.REMAINDER; //end row
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		layout.setConstraints(spacer, c);
		add(spacer);
	}
	
	public void addAnotherRow(Object[] data){
		remove(spacer);
		c.weighty = 0;
		addRow(data);
		c.weighty = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(spacer, c);
		add(spacer);
	}
	private void addRow(Object[] data){
		for(int col = 0; col < data.length; col++){
			addCol(data[col], data.length, col);
		}		
	}
	
	public void addCol(Object item, int cols, int col){
		if(item instanceof Number)
			genCell((Number)item, cols, col);
		else if(item instanceof Data0D)
			genCell((Data0D)item, cols, col);
		else if(item instanceof DataLabel)
			genCell((DataLabel)item, cols, col);
		else
			genCell(item, cols, col);
	}
	
	public void genCell(Object item, int cols, int col){
		c.ipadx = 5;
		c.weightx = weights[col % weights.length];
		c.gridwidth = col == cols - 1 ?
				GridBagConstraints.REMAINDER : 2;	
		c.anchor = GridBagConstraints.WEST;
		JLabel cell = new JLabel(item.toString());
		//cell.setBorder(new LineBorder(Color.BLACK, 1));
//		c.anchor = ITEM_ALIGN[col % ITEM_ALIGN.length];
//		c.gridheight = row == data.length - 1 ?
//				GridBagConstraints.RELATIVE : 1;	
		layout.setConstraints(cell, c);
		add(cell);
	}
	
	public void genCell(Data0D item, int cols, int col){
		genCell(new DataLabel(item), cols, col);
	}
	public void genCell(DataLabel cell, int cols, int col){
		c.ipadx = 5;
		c.weightx = weights[col % weights.length];
		c.gridwidth = col == cols - 1 ?
				GridBagConstraints.REMAINDER : 2;	
		c.anchor = GridBagConstraints.WEST;
		//cell.setBorder(new LineBorder(Color.BLACK, 1));
//		c.anchor = ITEM_ALIGN[col % ITEM_ALIGN.length];
//		c.gridheight = row == data.length - 1 ?
//				GridBagConstraints.RELATIVE : 1;	
		layout.setConstraints(cell, c);
		dynamicCells.add(cell);
		add(cell);
	}
	
	public void genCell(Number item, int cols, int col){
		c.weightx = weights[col % weights.length] / 2;
		c.ipadx = 0;
		JLabel c1, c2;
		String currency = cTypes[col % cTypes.length] == BALANCE ? Constants.UNITS_CURRENCY : "";
		if(item instanceof Double){
			double d = ((Double)item).doubleValue();
			int id = (int)d;
			String dd = "" + d;
			StringBuffer dds = new StringBuffer(dd.substring(dd.indexOf('.') + 1, dd.length()));
//System.out.print("dds=" + dds + " ");
			if(cTypes[col % cTypes.length] == BALANCE && (int)d != d){
				while(dds.length() < 2)
					dds = dds.append("0");
				while(dds.length() > 2)
					dds.deleteCharAt(dds.length() - 1);
			}
			else{
				while(dds.length() > 0 && (dds.length() > prcn || dds.charAt(dds.length() - 1) == '0'))
					dds.deleteCharAt(dds.length() -1);
			}
//System.out.println("data=" + d + " dds=" + dds);
			c1 = new JLabel((d < 0 ? "-" : "") + currency + Math.abs(id));
			if(cTypes[col % cTypes.length] == BALANCE && dds.length() == 0){
				c2 = new JLabel(".00");
				c2.setForeground(c2.getBackground());
//				c2.setVisible(false);
//				c2.setMaximumSize(refPanel.getPreferredSize());
			}
			else
				c2 = new JLabel(dds.length() > 0 ? "." + dds : "");
		}
		else{
			c1 = new JLabel(currency + item.toString());
			c2 = new JLabel();
		}
		if(cTypes[col % cTypes.length] == BALANCE){
			c1.setFont(c1.getFont().deriveFont(Font.BOLD));
			c2.setFont(c2.getFont().deriveFont(Font.BOLD));
			if(((Number)item).doubleValue() < 0){
				c1.setForeground(Color.RED.darker());
				if(!c2.getText().equals(".00"))
					c2.setForeground(Color.RED.darker());
			}else{
				c1.setForeground(Color.GREEN.darker());
				if(!c2.getText().equals(".00"))
					c2.setForeground(Color.GREEN.darker());
			}
		}
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(c1, c);
		add(c1);
		c.gridwidth = col == cols - 1 ?
				GridBagConstraints.REMAINDER : 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(c2, c);
		add(c2);
	}

	/** Updates any data labels to reflect new values */
	public void redisplay(){
		for(DataLabel l : dynamicCells){
//			l.printVal();
//System.out.println("repainting cell");
			l.redisplay();
//			l.repaint();
		}
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		f.add(new DataTable(new Object[][]{{"1", 2}, {new DataLabel(new DoubleData(0, 3)), 4}}));
		f.pack();
		f.setVisible(true);
	}
}
