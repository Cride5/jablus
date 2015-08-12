package uk.co.crider.jablus.models.absluc.gui;

import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.MatrixData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.GridView;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.UserInterface;
import uk.co.crider.jablus.gui.data.DataView;
import uk.co.crider.jablus.gui.data.MapView;
import uk.co.crider.jablus.gui.data.MapViewSimple;
import uk.co.crider.jablus.gui.data.MapViewThumb;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;
import uk.co.crider.jablus.models.basic.Parameters;
import uk.co.crider.jablus.models.absluc.agent.HumanAgent;
import uk.co.crider.jablus.models.basic.env.LandUse;
import uk.co.crider.jablus.models.basic.env.land.LandCell;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import jwo.landserf.structure.ColourTable;
import jwo.landserf.structure.Header;
import jwo.landserf.structure.RasterMap;
import jwo.utils.gui.JWRubberband;

/** User Interface for humans acting as land use agents.
 * An instance of this is created for each HumanAgent object */
public class SubjectInterface extends JablusWindow implements UserInterface {

	/** Unique class ID */
    private static final long serialVersionUID = 777035630161642730L;
    
    // Text data
    private static final String INITIAL_REASON        = "Enter your reasoning here...";
    private static final String DEFAULT_REASON        = "Same reason as last round.";
    private static final String NO_REASON_STOP        = "No reason was given.\n\nPlease provide a reason to\ncontinue to the next round.";
    private static final String NO_REASON_VERIFY      = "No reason was given.\n\nAre you sure you would like to\ncontinue to the next round?";
    private static final String STATUS_MAKE_SELECTION = "1. Make your land use selection";
    private static final String STATUS_GIVE_REASON    = "2. Give the reasoning behind your decision";
    private static final String STATUS_CLICK_DONE     = "3. Click Next Round to continue";
    private static final String	STATUS_PROCESSING     = "Calculating revenue...";
    
    private HumanAgent agent;
    private SimulationInterface sim;
    private Parameters params;
//    private EventHandler eventHandler;
    private DisplayParams localParams;
    private Time time;

    private MapView mapView;
    private RasterMap landUse;
    private JTextArea textInput;
    private JButton doneButton; 
    private GridView gridView;
    private DataView graphs;
	private DataView data;
	private boolean enabled;
	private double[][] lastProfit;
	
	private boolean landUseClicked;
	private boolean justificationMade;
	private boolean doneClicked;
	private int useToSet;
    
	public SubjectInterface(HumanAgent agent, Parameters params){//DisplayParams displayParams) {
	    super(SubjectDisplayParams.EXPT_TITLE, false, params.GUI_HUMAN_CLOSABLE);
System.out.println("closable?" + params.GUI_HUMAN_CLOSABLE);
	    this.agent = agent;
	    this.params = params;
	    enabled = false;
//	    agent.setGUI(this);
	    // Create local display parameters
	    localParams = new SubjectDisplayParams(params);
//	    globalParams = displayParams;
	    // Add to global display parameters
//	    globalParams.addParams(localParams);
    }

	/** @inheritDoc */
	public void setSimulation(SimulationInterface sim){
//System.out.println("SubjectInterface: Setting simulation reference");
		this.sim = sim;
		this.params = (Parameters)sim.getExperiment();
//		eventHandler = new EventHandler(this, sim);
	    mapView = new MapViewSimple(this);
		landUse = new RasterMap(params.ROWS, params.COLS, params.RASTER_FOOTPRINT, new Header("Land Use"));
		// Set up colour table
		landUse.setColourTable(LandUse.getColourTable(params));
//		mapView.addDisplayItem(landUse);
		textInput = new JTextArea();
		// Colour table representing earnings
//		ColourTable col = Market.getColourTable(params);
		ColourTable col = new ColourTable();
		col.addContinuousColourRule( 0.0f, 255, 0, 0);
		col.addContinuousColourRule( (float)params.REVENUE_FACTOR * 100/ 2, 255, 255, 140);
		col.addContinuousColourRule( (float)params.REVENUE_FACTOR * 100, 31, 181, 15);
//		Color bk = new JLabel().getBackground();
//		col.addContinuousColourRule( 0, bk.getRed(), bk.getGreen(), bk.getBlue());
//		col.addContinuousColourRule( 1, 122, 179, 0);
//		col.addContinuousColourRule( 1, 85, 168, 0);
//		col.addContinuousColourRule( 1, 103, 207, 5);
//		col.addContinuousColourRule( 1, 5, 109, 207);
//		col.addContinuousColourRule( 0, 0, 0, 0);
//		col.addContinuousColourRule( 1, 255, 255, 255);
		// Red-orange-green (bold)
//		col.addContinuousColourRule( 0.0f, 255, 0, 0);
//		col.addContinuousColourRule( 0.5f, 255, 191, 0);
//		col.addContinuousColourRule( 1.0f, 122, 224, 13);
		// Red-yellow-green (bold)
		// Red-orange-green (washed)
//		col.addContinuousColourRule( 0.0f, 241, 129, 129);
//		col.addContinuousColourRule( 0.5f, 240, 215, 126);
//		col.addContinuousColourRule( 1.0f, 182, 233, 127);
		// Red-orange-green (washed2)
//		col.addContinuousColourRule( 0.0f, 243, 91, 106);
//		col.addContinuousColourRule( 0.5f, 250, 250, 108);
//		col.addContinuousColourRule( 1.0f, 31, 181, 15);
		
		// Colour table representing change
/*		ColourTable col = new ColourTable();
		col.addContinuousColourRule(-1, 255, 0, 0);
		Color bk = new JLabel().getBackground();
		col.addContinuousColourRule( 0, bk.getRed(), bk.getGreen(), bk.getBlue());
		col.addContinuousColourRule( 1, 0, 0, 255);
*/		gridView = new GridView(params.ROWS, params.COLS, this, col);
    	data = new DataView(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.DATA_ITEM_HEIGHT, localParams, false);
    	graphs = new DataView(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.GRAPH_ITEM_HEIGHT, localParams, false);
    	lastProfit = new double[params.ROWS][params.COLS];
		initComponents();
		enabled = true;
	    center();
		landUseClicked = false;
		justificationMade = false;
		doneClicked = false;
		useToSet = 0;
		updateStatus();
	    setVisible(true);
	    mapView.redisplay(true);
	    
    }
	
	/** Initialise display components */
	private void initComponents(){
//		final JFrame thisFrame = this;
		
/*	    // Create map view
		JPanel mapViewPanel = new JPanel(new BorderLayout());
		JLabel mapViewLabel = new JLabel(" 1. Select Land Use");
		mapViewPanel.add(mapViewLabel, BorderLayout.NORTH);
		mapView.getGraphicsArea().setCursor(Cursor.getDefaultCursor());
//		mapView.getGraphicsArea().removeRubberbandListener(null);
		mapView.getGraphicsArea().setRubberbandType(JWRubberband.LINE);
		MouseListener[] listeners = mapView.getGraphicsArea().getMouseListeners();
		for(MouseListener listener : listeners){
			mapView.getGraphicsArea().removeMouseListener(listener);
		}
		MouseMotionListener[] listeners2 = mapView.getGraphicsArea().getMouseMotionListeners();
		for(MouseMotionListener listener : listeners2){
			mapView.getGraphicsArea().removeMouseMotionListener(listener);
		}
//		mapView.getGraphicsArea().setDisplayMode(GraphicsArea.NORMAL);
		mapViewPanel.add(mapView, BorderLayout.CENTER);
		mapViewPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// Create text input view
		JPanel textButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel textLabel = new JLabel("2. Reason for Selection");
		textButtonPanel.add(textLabel);
		textLabel.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH - 10, 15));
		textInput.setText(INITIAL_REASON);
		textInput.setLineWrap(true);
		textInput.setWrapStyleWord(true);
		JScrollPane textScroller = new JScrollPane(textInput);
		textScroller.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH - 10, SubjectDisplayParams.INPUT_PANEL_HEIGHT - 30));
		textButtonPanel.add(textScroller);
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH - 10, 20));
		textButtonPanel.add(spacer);
		doneButton = new JButton("3. Next Round >");
		textButtonPanel.add(doneButton);
		textButtonPanel.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, SubjectDisplayParams.INPUT_PANEL_HEIGHT));

		// Create output data view
		JPanel dataViewPanel = new JPanel(new BorderLayout());
		dataViewPanel.add(data, BorderLayout.NORTH);
		dataViewPanel.add(graphs, BorderLayout.CENTER);
		data.setPreferredSize(new Dimension(140, 100));
		dataViewPanel.setBorder(new EmptyBorder(20, 5, 5, 5));
		
		// Create grid output data view
		JPanel gridViewPanel = new JPanel(new BorderLayout());
		JLabel gridViewLabel = new JLabel("    Cell-by-Cell Revenue [pence/100]");
		gridViewPanel.add(gridViewLabel, BorderLayout.NORTH);
		gridViewPanel.add(gridView, BorderLayout.CENTER);
		gridView.setPreferredSize(new Dimension(SubjectDisplayParams.GRID_WIDTH, SubjectDisplayParams.GRID_WIDTH + 20));
		gridViewPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// Create input panel
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(mapViewPanel, BorderLayout.CENTER);
		inputPanel.add(textButtonPanel, BorderLayout.EAST);
		inputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
//		inputPanel.setBorder(new EtchedBorder());
		
		// Create output panel
		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(dataViewPanel, BorderLayout.CENTER);
		outputPanel.add(gridViewPanel, BorderLayout.WEST);
//		outputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
//		outputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		// Create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		JTextField helpUrl = new JTextField("Instructions:   " + Constants.HELP_URL);
		helpUrl.setEditable(false);
		helpUrl.setBorder(new LineBorder(new Color(UIManager.getColor("Panel.background").getRGB()), 2));
		helpUrl.setBackground(new Color(UIManager.getColor("Panel.background").getRGB()));
		mainPanel.add(helpUrl, BorderLayout.NORTH);
		mainPanel.add(inputPanel, BorderLayout.CENTER);
		mainPanel.add(outputPanel, BorderLayout.SOUTH);
		add(mainPanel);

		// Add map view listener
		mapView.getGraphicsArea().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				final MouseEvent event = e;
				new Thread(){
					public void run(){
						if(!enabled) return;
//						if(!history.canContinue()) return;
						LandCell clicked = new LandCell(
								event.getY() * params.ROWS / mapView.getHeight(),
								event.getX() * params.COLS / mapView.getWidth());
						int curUse = (int)landUse.getAttribute(clicked.x, clicked.y);
						int newUse = (curUse + 1) % params.LAND_USES;
						useToSet = newUse;
						landUse.setAttribute(clicked.x, clicked.y, newUse);
						mapView.redisplay(true);
						mapView.repaint();
						landUseClicked = true;
						updateStatus();
//System.out.println("SubjectInterface: Clicked (" + clicked.x + "," + clicked.y + "), use=" + curUse);
//						if(reasonText.getText().equals(SAME_REASON_TEXT)) reasonText.setText("");
					}
				}.start();
			}
		});
		mapView.getGraphicsArea().addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				LandCell clicked = new LandCell(
						e.getY() * params.ROWS / mapView.getHeight(),
						e.getX() * params.COLS / mapView.getWidth());
				landUse.setAttribute(clicked.x, clicked.y, useToSet);
				mapView.redisplay(true);
				mapView.repaint();
				landUseClicked = true;
				updateStatus();
            }
		});
		
		// Add text input key listener
		textInput.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				justificationMade = true;
				updateStatus();
			}
		});
		
		// Add text input focus listener
		textInput.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent arg0) {
	            if(!justificationMade)
	            	textInput.setText("");
            }
			public void focusLost(FocusEvent arg0) {
	            if(!justificationMade)
	            	textInput.setText(time == null || time.getValue() == 0 ? INITIAL_REASON : DEFAULT_REASON);
            }
		});
		
		// Add done button listener
		doneButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// Check if user really means to continue
				if(!justificationMade){
					if(time != null && time.getValue() == 0){
						DecisionPopup.inform(NO_REASON_STOP, "Reason Required");
						return;
					}
					if((time == null || time.getValue() == 0 || landUseClicked) && !DecisionPopup.verify(NO_REASON_VERIFY, "Are you sure?"))
						// In this case simply return to land use selection
						return;
					// If no reasoning given then set reason to blank
					textInput.setText("");
				}
				
				// Go ahead and complete round
				new Thread(){
					public void run(){
						landUseClicked = true;
						justificationMade = true;
						doneClicked = true;
						updateStatus();
						enabled = false;
						doneButton.setEnabled(false);
						textInput.setEnabled(false);
//System.out.println("SubjectInterface: Setting text " + textInput.getText());
						//agent.selectLandUse(landUse, textInput.getText());
					}
				}.start();
            }
		});
		
		// Add close listener
		if(params.GUI_HUMAN_CLOSABLE){
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					((JFrame)e.getSource()).dispose();
					sim.close();
				}
			});
		}
		
//		pack();
		setResizable(false);
		setMinimumSize(new Dimension(SubjectDisplayParams.WIDTH, SubjectDisplayParams.HEIGHT));
		setSize(new Dimension(SubjectDisplayParams.WIDTH, SubjectDisplayParams.HEIGHT));

*/	
	}
	
	/** @inheritDoc */
	public void addDisplayItem(Data item){
		List<Data> time = new LinkedList<Data>();
		Hashtable<String, List<Data>> pastData = getPastData(sim, item, time, localParams);
		// TODO: 
//		 Re-create RevenueGrid
		DataSet snapshot = sim.getSnapshot();
		Data pastItem = snapshot.getItem(item.getId());
		
		Data revenue = snapshot.getItem(Constants.OUTPUT_REVENUE_GRID);
		if(revenue == null || !((MatrixData)revenue).loaded){
//			System.out.println("Revenue not available, regenerating...");
			((DataSet)pastItem).setItem(genRevenueGrid((DataSet)pastItem, params));
//			System.out.println("generated revenue: " + ((DataSet)pastItem).getItem(Constants.OUTPUT_REVENUE_GRID));
		}
		else{
//			System.out.println("Revenue is available: " );
		}
		addDisplayItem(item, pastData, time);
	}
	private void addDisplayItem(Data item, Hashtable<String, List<Data>> pastData, List<Data> time){
		if(item instanceof Data0D || item instanceof Data1D){
			if(localParams.display(item.getId(), SimulationDisplayParams.ADD_VALUE))
				data.addDisplayItem(item);
			if(localParams.display(item.getId(), SimulationDisplayParams.ADD_GRAPH)){
				graphs.addGraphItem(item, pastData.get(item.getId()), time, null);
			}
			if(item instanceof Time){
				this.time = (Time)item;
			}
//			if(SimulationDisplayParams.display(item.getNameId(), SimulationDisplayParams.ADD_GRAPH)){
//				graphs.addGraphItem(item);
//			}
		}
//		if(item instanceof Data1D)
//			data.addDisplayItem((Data1D)item);
//		if(item instanceof Data2D){
//		}
//		if(item instanceof DataTX && localParams.display(item.getNameId(), SimulationDisplayParams.SHOW_TEXT))
//			textInput.addDisplayItem((DataTX)item, pastData.get(item.getNameId()));
//			textInput.addDisplayItem((DataTX)item);
		if(item instanceof DataSet)
			for(Data subItem : ((DataSet)item).getItems())
				addDisplayItem(subItem, pastData, time);
//				addDisplayItem(subItem);
	    // TODO Auto-generated method stub
	    
    }

	/** @inheritDoc */
	public void close(){
		double gameTotal = ((Data0D.Double)agent.getItem(Constants.OUTPUT_REVENUE_GAME_TOTAL)).doubleValue();
		double grandTotal = ((Data0D.Double)agent.getItem(Constants.BANK_TOTAL)).doubleValue();
		Popup.inform("You raised a total of £" + Utils.roundString(gameTotal, 2) + " in this game.\n\n" +
				(grandTotal > gameTotal ? "The total amount of money earned so far is £" + Utils.roundString(grandTotal, 2) + "\n\n" : "" +
				"Click OK to continue"),
				"Game Complete");
	    dispose();
    }

	/** @inheritDoc */
	public void notifyStep(double progress) {
		setProgress((int)(progress * 100));
    }
	/** @inheritDoc */
	public void notifyNewStep(){
		landUseClicked = false;
		justificationMade = false;
		doneClicked = false;
		updateStatus();
		//textInput.setRequestFocusEnabled(false);
		doneButton.requestFocus();
		textInput.setText(time == null || time.intValue() == 0 ? INITIAL_REASON : DEFAULT_REASON);
		enabled = true;
		textInput.setEnabled(true);
		doneButton.setEnabled(true);
		redisplay();
	}
	/** @inheritDoc */
	public void notifyEndStep(){
		// Calculate change values
		double[][] newProfit = new double[0][0];//agent.getProfitsDetail();
		double[][] scaleProfit = new double[newProfit.length][newProfit[0].length];
		double[][] change = new double[newProfit.length][newProfit[0].length];
		String[][] vals = new String[newProfit.length][newProfit[0].length];
/*		for(int i = 0; i < newProfit.length; i++){
			for(int j = 0; j < newProfit[i].length; j++){
				change[i][j] = newProfit[i][j] - lastProfit[i][j];
				// Multiply values to give round numbers
				scaleProfit[i][j] = newProfit[i][j] * 100;
			}
		}
*/		for(int i = 0; i < newProfit.length; i++){
			for(int j = 0; j < newProfit[i].length; j++){
				change[i][j] = newProfit[i][j] - lastProfit[i][j];
				// Multiply values to give round numbers
				scaleProfit[i][j] = newProfit[i][j] * 100;
				vals[i][j] = Utils.roundString(scaleProfit[i][j], 1) + (change[i][j] > params.REVENUE_NOISE ? " +" : change[i][j] < -params.REVENUE_NOISE ? " -" : "  ");
			}
		}
		// Update grid values
		lastProfit = newProfit;
//		gridView.setColourValues(change);
		gridView.setColourValues(scaleProfit);
//		gridView.setValues(scaleProfit);
		gridView.setValues(vals);
	}
	private void updateStatus(){
		if(!landUseClicked){
			setStatus(STATUS_MAKE_SELECTION);
			return;
		}
		if(!justificationMade){
			setStatus(STATUS_GIVE_REASON);
			return;
		}
		if(!doneClicked){
			setStatus(STATUS_CLICK_DONE);
			return;
		}
		setStatus(STATUS_PROCESSING);
	}

	public void redisplay(){
//		gridView.repaint();
//		super.repaint();
		if(mapView != null)
			mapView.redisplay(true);
		if(data != null)
			data.redisplay();
		if(graphs != null)
			graphs.redisplay();
//else System.out.println("SimulationPanel: Graphs is null");

	}


	/** @inheritDoc */
	public void setDisplayItem(Data item) {
		if(item instanceof Data0D)
			if(data != null) data.setDisplayItem((Data0D)item);
		if(item instanceof Data1D)
			if(data != null) data.setDisplayItem((Data1D)item);
		// Graphs do not get updated
//			graphs.setDisplayItem((Data1D)item);
/*		if(item instanceof Data2D)
//System.out.println("Setting Raster "+ item.getNameId() + ":\n" + Utils.arrayString(((RasterMap)item).getRasterArray()));
			maps.setDisplayItem((Data2D)item);
		if(item instanceof DataTX)
			texts.setDisplayItem((DataTX)item);
*/		if(item instanceof DataSet)
			for(Data subItem : ((DataSet)item).getItems())
				setDisplayItem(subItem);
    }

	
	
//  rebuild RevenueGrid
//	private Data2D revenue;
	public static Data2D genRevenueGrid(DataSet pastData, Parameters params){
//		if(revenue == null)
//			revenue = new MatrixData(Constants.OUTPUT_REVENUE_GRID, params, true);
//	System.out.println("Simulation.genRevenueGrid: Generating past data from:\n"+pastData);
		Data2D revenueNew = new MatrixData(Constants.OUTPUT_REVENUE_GRID, params.ROWS, params.COLS, true);
		Random random = new Random(0);
		// Calculate the distance the product must travel (weighted by road type)
		double distance = 0;
//		if(params.TRANSPORT)
//			distance = roadNetwork.distance(cell, farm);
		double[] price = new double[params.LAND_USES];
		if(params.MARKET){
//	System.out.println("Market price = " + pastData.getItem(Constants.DRIVER_MARKET_PRICE));
			price = ((Data1D.Double)pastData.getItem(Constants.DRIVER_MARKET_PRICE)).getValues();
		}
		for(int y = 0; y < params.ROWS; y++){
			for(int x = 0; x < params.COLS; x++){
				int use = (int)((Data2D)pastData.getItem(Constants.DRIVER_LAND_USE)).getData(x, y);
				// Calculate output for this land parcel based on environmental characteristics
				double output = 0;
				if(params.PRODUCTIVITY){
					Data2D productivity = (Data2D)pastData.getItem(Constants.DRIVER_LAND_PRODUCTIVITY);// + Constants.SEPARATOR + params.LAND_USE_NAMES[use]);			
					if(productivity != null && (params.PRODUCTIVITY_SP_HETRO || params.PRODUCTIVITY_REACTIVE)){
						output = productivity.getData(x, y);
					}
					else if(params.PRODUCTIVITY_DEFAULT != null)
						output = params.PRODUCTIVITY_DEFAULT[use];
				}
//				revenueNew.setData(x, y, (float)Market.getRevenue(use, price, output, distance, random, params));
			}
		}
//		Data2D revenueOld = revenue;
//		revenue = revenueNew;
//		System.out.println("Generated revenue: " + revenue);
		return revenueNew;
//		return revenueOld;
	}

}
