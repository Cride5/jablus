package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jfreechart.TimeGraphPanel;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;
import uk.co.crider.jablus.utils.TruthTable;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/** A panel holding multiple data values or graphs */
public class DataView extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = -2067507982642022086L;
    
//    int itemWidth;
 //   int itemHeight;
    int rows;
    int cols;
    int dispCount;
    boolean canEdit;
    
    private JPanel dataPanel;
    private JComboBox addList;
    private TreeMap<Integer, DataViewItem> dataItems;
    private TruthTable<Integer> toDisplay;
    private DisplayParams displayParams;
    
    public DataView(int rows, int cols, DisplayParams displayParams, boolean canEdit){ //this(new LinkedList(), new LinkedList(), itemWidth, itemHeight, displayParams, canEdit); }
//    public DataView(List dispItems, int itemWidth, int itemHeight, DisplayParams displayParams, boolean canEdit){ this(dispItems, new LinkedList(), itemWidth, itemHeight, displayParams, canEdit); }
//    public DataView(List dispItems, List candItems, int itemWidth, int itemHeight, DisplayParams displayParams, boolean canEdit){
   	super(new BorderLayout());
    	this.canEdit = canEdit;
    	this.rows = rows;
    	this.cols = cols;
//    	this.itemHeight = itemHeight;
 //   	this.itemWidth = itemWidth;
    	this.dispCount = 0;//dispItems.size();
    	this.dataItems = new TreeMap<Integer, DataViewItem>();
    	this.toDisplay = new TruthTable<Integer>();
    	this.displayParams = displayParams;
    	initComponents();//dispItems, candItems);
    }
    
	/** Initialise graphical components */
    private void initComponents(){
    	dataPanel = new JPanel(new GridLayout(rows, cols));
    	dataPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
	
    	if(canEdit){
	    	JScrollPane scroller = new JScrollPane();
	    	scroller.setViewportView(dataPanel);
	    	add(scroller, BorderLayout.CENTER);
    	}
    	else{
	    	add(dataPanel, BorderLayout.CENTER);	    	
	    }
    	
    	addList = new JComboBox();
    	addList.setFont(SimulationDisplayParams.DEFAULT_FONT);
    	addList.addItem("Add...");
    	addList.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e){
    			JComboBox cb = (JComboBox)e.getSource();
    			Object o = cb.getSelectedItem();
    			if(o instanceof JPanel){
    				JPanel item = (JPanel)o;
    				showItem(item);
    				addList.setSelectedIndex(0);
    			}
    	    }
    	});
    	if(canEdit) add(addList, BorderLayout.SOUTH);

    }
    
    
    public void addDisplayItem(Data dataItem){
    	// Do not add item if not set to display value
    	if(!displayParams.display(dataItem.getId(), SimulationDisplayParams.ADD_VALUE))
    		return;
 //   	if(displayParams.display(dataItem.getName(), SimulationDisplayParams.ADD_VALUE)){
    		DataViewItem item = null;
    		if(dataItem instanceof Data0D)
    		{
    			item = new SimpleItem((Data0D)dataItem, this, canEdit);
// System.out.println("new SimpleItem: " + item);
    		}
    		else if(dataItem instanceof Data1D){
    			item = new SimpleItem1D((Data1D)dataItem, this, canEdit);
    		}
//System.out.println("new item:" + item.dataName());
			if(displayParams.display(dataItem.getId(), SimulationDisplayParams.SHOW_VALUE))
				toDisplay.set(item.dataId(), true);
			else
				toDisplay.set(item.dataId(), false);
			
			dataItems.put(item.dataId(), item);
 //   	}
//	       	revalidate2();
        	repaint();
    }
    public void setDisplayItem(Data dataItem){
//System.out.println("DataView: Setting data " + dataItem + " using\n          " + dataItems);
    	if(dataItems.containsKey(dataItem.getId())){
//System.out.println("DataView: Setting data " + dataItem + ", name: " + dataItem.getNameId() + " - using...\n           " + dataItems.get(dataItem.getNameId()));
    		dataItems.get(dataItem.getId()).setData(dataItem);
//    		dataItems.get(dataItem.getNameId()).redisplay();
			dataPanel.revalidate();
			dataPanel.repaint();
    	}
    	// Poss update graph here too...
    }
//    void addGraphItem(Data dataItem){ addGraphItem (dataItem, null, null); }
    public void addGraphItem(Data dataItem, List<Data> pastValues, List<Data> time, Date startTime){    	
    	// Determine if item belongs to a graph group
    	int id = dataItem.getId();
    	int gid = displayParams.getGroup(id);
    	gid = gid == 0 ? id : gid; // Set group id to item id if no group id present
    	
    	// Do not add item if id or group id set not to show graph
    	if(!displayParams.display(id,  DisplayParams.ADD_GRAPH)
    	|| !displayParams.display(gid, DisplayParams.ADD_GRAPH))
    		return;
//System.out.println("DataView: Adding graph " + dataItem + ", using past data " + pastValues);

    	// Create new graph if doesn't already exist
    	GraphItem item = null;
    	if(!dataItems.containsKey(gid)){
//System.out.println("DataView: Creating graph for item id:" + id + ", group id:" + gid);
    		String units = "";
    		if(dataItem instanceof Data0D) units = ((Data0D)dataItem).getUnits();
    		if(dataItem instanceof Data1D) units = ((Data1D)dataItem).getUnits();
    		item = new GraphItem(gid, units, this, displayParams, startTime, canEdit, false, displayParams.display(gid, DisplayParams.EDITABLE));//id != gid);
//    		item.setPreferredSize(new Dimension(itemWidth, itemHeight));
    		dataItems.put(gid, item);
    	}
    	else
    		item = (GraphItem)dataItems.get(gid);
    	// Add data item to graph group
    	item.addData(dataItem, pastValues, time);
    	if(displayParams.display(gid, SimulationDisplayParams.SHOW_GRAPH))
    		toDisplay.set(gid, true);
    	else{
    		toDisplay.set(gid, false);
    	}
    	// Hide line if set to not show
    	if(!displayParams.display(id, SimulationDisplayParams.SHOW_GRAPH) && item.getGraph() instanceof TimeGraphPanel)
			((TimeGraphPanel)item.getGraph()).show(dataItem.getName(), false);
    		
    	
 /*   	}
    	else{
	     	DataViewItem item = new GraphItem(dataItem, pastValues, time, this, displayParams, canEdit); 
			if(displayParams.display(item.dataName(), SimulationDisplayParams.SHOW_GRAPH))
				toDisplay.set(item.dataName(), true);
			else
				toDisplay.set(item.dataName(), false);
			dataItems.put(displayParams.getOrder(item.dataName()), item);
	    	}
	*/
//       	revalidate2();
//    	repaint();
    }


    void showItem(JPanel item){
    	if(item instanceof DataViewItem){
    		toDisplay.set(((DataViewItem)item).dataId(), true);
//    		dataPanel.setPreferredSize(new Dimension(itemWidth, itemHeight * ++dispCount + 8));
    		revalidate2();
    		repaint();
    	}
    }
    
    void hideItem(JPanel item){
    	if(item instanceof DataViewItem){
    		toDisplay.set(((DataViewItem)item).dataId(), false);
//        	dataPanel.setPreferredSize(new Dimension(itemWidth, itemHeight * --dispCount + 8));
        	revalidate2();
        	repaint();
    	}
    }
    
    /** Update all data fields */
    public void redisplay(){
//System.out.println("DataView: item count=" + dataItems.size());
    	for(DataViewItem i : dataItems.values()){
    		i.redisplay();
    	}
    }
    
    public void revalidate2(){
 //   	if(dataPanel != null){
 //System.out.println("DataView:revalidate() " + dataItems.size());
	    	// Remove all components within the dataPanel
	    	dataPanel.removeAll();
	    	addList.removeAllItems();
	    	addList.addItem("Add...");
	    	// Add them according to order and toDisplay
	    	for(DataViewItem i : dataItems.values()){
		  		if(toDisplay.isTrue(i.dataId()))
		  			{dataPanel.add(i); 
//System.out.println("displaying: " + i.dataName());
		  			}
		  		else
		  			{addList.addItem(i); 
//System.out.println("Adding:" + i.dataName());
		  			}
	    	}
	  		dataPanel.revalidate();
//	  		addList.revalidate();
//	    }
  		revalidate();
  		repaint();
    }
    
	/** For testing purposes */
/*	public static void main(String[] args){

		
		// Display in frame
		JFrame testFrame1 = new JFrame("Testing Simple View");
		testFrame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame1.add(testDataViewData(), BorderLayout.CENTER);
		testFrame1.pack();
		testFrame1.setVisible(true);

		
		// Display in frame
		JFrame testFrame2 = new JFrame("Testing Graph View");
		testFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame2.add(testDataViewGraphs(), BorderLayout.CENTER);
		testFrame2.pack();
		testFrame2.setVisible(true);
	}
*/	
	/** Returns a test DataView initialised with data */
/*	public static DataView testDataViewData(){
		// Initialise test data
		List<TestData> testDisp1 = new LinkedList<TestData>();
		testDisp1.add(new TestData("FirstName"));
		testDisp1.add(new TestData("A", 0.8543));
		testDisp1.add(new TestData("B", 1.0));
		testDisp1.add(new TestData("C", 0.0232));
		testDisp1.add(new TestData("d", 0.0003));
		List<TestData> testCand1 = new LinkedList<TestData>();
		testCand1.add(new TestData("x"));
		testCand1.add(new TestData("y", 6));
		return new DataView(testDisp1, testCand1, SimulationDisplayParams.DATA_ITEM_HEIGHT);
	}
*/	
	
	/** Returns a test DataView initialised with graph data */
/*	public static DataView testDataViewGraphs(){
		// Initialise test graph data
		List<TestGraph> testDisp2 = new LinkedList<TestGraph>();
		testDisp2.add(new TestGraph("FirstName", new GraphPanel()));
		testDisp2.add(new TestGraph("A", new GraphPanel()));
		testDisp2.add(new TestGraph("B", new GraphPanel()));
		testDisp2.add(new TestGraph("C", new GraphPanel()));
		testDisp2.add(new TestGraph("d", new GraphPanel()));
		List<TestGraph> testCand2 = new LinkedList<TestGraph>();
		testCand2.add(new TestGraph("x", new GraphPanel()));
		testCand2.add(new TestGraph("y", new GraphPanel()));
		return new DataView(testDisp2, testCand2, SimulationDisplayParams.GRAPH_ITEM_HEIGHT);
	} 
*/	
	
	/** For testing only! */
/*	static class TestData implements Data0D{
		private String name;
		private double value;
		private TestData(String name){ this(name, 0); }
		private TestData(String name, double value){
			this.name = name;
			this.value = value;
		}
		public void setValue(double value){ this.value = value; }
		public double doubleValue() { return value; }
		public String getName() { return name; }
		public String getValue() { return ""+value;  }
		public int intValue() { return (int) value;  }
	}
*/	
	
	
	/** For testing only! */
/*	static class TestGraph implements Data1D{
		private String name;
		private GraphPanel graph;
//		private TestGraph(String name){ this(name, new GraphPanel()); }
		private TestGraph(String name, GraphPanel graph){
			this.name = name;
			this.graph = graph;
		}
		public String getName() { return name; }
		public GraphPanel getGraph() { return graph;  }
		public void addData(String profit, double lastProfit) {
	        // TODO Auto-generated method stub
	        
        }
		public void addSeries(String name, Paint linePaint) {
	        // TODO Auto-generated method stub
	        
        }
	}
*/	

}
