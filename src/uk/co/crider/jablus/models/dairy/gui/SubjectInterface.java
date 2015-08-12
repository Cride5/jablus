package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.SimulationManager;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataNT;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.FileChooser;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.UserInterface;
import uk.co.crider.jablus.gui.data.DataView;
import uk.co.crider.jablus.gui.data.MapView;
import uk.co.crider.jablus.gui.data.PercentBar;
import uk.co.crider.jablus.gui.data.ProfitBar;
import uk.co.crider.jablus.gui.data.TimeProgressBar;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.CowDigestion;
import uk.co.crider.jablus.models.dairy.env.DairyFarm;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.Storage;
import uk.co.crider.jablus.models.dairy.env.Storage.Store;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.workbench.ui.cursortool.AbstractCursorTool.Listener;

/** User Interface for humans acting as land use agents.
 * An instance of this is created for each HumanAgent object */
public class SubjectInterface extends JablusWindow implements UserInterface {

	/** Unique class ID */
	private static final long serialVersionUID = 777035630161642730L;
	
	private static final int SKIP_WEEKS = 13; // 13 weeks = 1 quarter

	/** Icon files */
	public static final String BASE_DIR = "/" + uk.co.crider.jablus.Constants.JABLUS_GRAPHICS_DIR + "/" + "control_icons" + "/";
	public Image BUY_ICON   = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "buy.png"));
	public Image SELL_ICON  = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "sell.png"));
	public Image FEED_ICON  = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cow.png"));
	public Image CROP_ICON  = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "wheat.png"));
	public Image CPLAN_ICON = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "crop_plan.png"));
	public Image FPLAN_ICON = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "feed_plan.png"));
	public Image DEC_ICON   = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "think.png"));
	public Image EVAL_ICON  = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "spyglass.png"));
	public Image TRANS_ICON = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "transactions.png"));
	public Image NEXT_ICON  = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "control_play.png"));
	public Image NEXT2_ICON = Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "control_forward.png"));
	
	
	// Model components
	private DairyAgent agent;
	private SimulationInterface sim;
	private Parameters params;
	private DisplayParams displayParams;
	private Time time;
	private Storage store;
	private Market market;
	private VectorData fieldMap;
	private Data0D.Double balance;
	private Data0D.Double profit;
	private Object stepMonitor = new Object();

	// GUI Components
	private LinkedList<Component> actionControls;
	private LinkedList<Component> fieldControls;
//	private TransactionsPanel marketPanel;
	private LivestockFeedPanel feedPanel;
	private EvaluationPanel evalPanel;
	private MapView maps;
	private JButton[] viewButtons;
	private JButton nextButton;
	private JButton monthButton;
	private CroppingPlanner cropPlanner;
	private GrazingPlanner grazingPlanner;
	private int maxFieldId;
	private JPanel storePanel;
	private DataView graphs;
	private TimeProgressBar timeBar;
	private ProfitBar fundsBar;
	private StorageItem[] storeItems;
	private WeatherView weather;
	private JPanel timeFundPanel;
	private JLabel fundsLabel;
	private PercentBar[] pBars;
	private JMenu groupMenu;
	// Main panels
	private JPanel northPanel;
	private JPanel eastPanel;
	private JPanel southPanel;
	private JPanel centerPanel;

	// State varas
//	private boolean landUseClicked;
//	private boolean justificationMade;
//	private boolean doneClicked;
	private boolean enabled;
	private boolean decOpen;
	private Thread decThread;
	private Hashtable<Object, Integer> prevCrop;
	private File regimeFile;
	private Preferences prefs;
	private JPopupMenu visibleMenu;

	public SubjectInterface(DairyAgent agent, uk.co.crider.jablus.Parameters params){//DisplayParams displayParams) {
		super(SubjectDisplayParams.EXPT_TITLE + " - " + ((uk.co.crider.jablus.models.dairy.Parameters)params).EXPT_TITLE, true, false, params.GUI_HUMAN_CLOSABLE);
//		System.out.println("closable?" + params.GUI_HUMAN_CLOSABLE);
		this.agent = agent;
//		System.out.println("agent:" + agent);
		this.params = (Parameters)params;
		enabled = false;
		decOpen = false;
//		agent.setGUI(this);
//		dataInput = new Hashtable<Integer, JTextField>();
		// Create local display parameters
		displayParams = new SubjectDisplayParams(params);
		actionControls = new LinkedList<Component>();
		fieldControls = new LinkedList<Component>();
		prevCrop = new Hashtable<Object, Integer>();
		prefs = Preferences.userNodeForPackage(Simulation.class);
	}

	/** @inheritDoc */
	public void setSimulation(SimulationInterface sim){
//		System.out.println("SubjectInterface: Setting simulation reference");
		this.sim = sim;
		this.params = (Parameters)sim.getExperiment();
		this.regimeFile = new File(prefs.get(Constants.WORK_DIR_KEY, ".") + File.separator +
				sim.getSimFile().getName().replaceAll(Constants.FILE_EXT_SIM, Constants.FILE_EXT_RGM));
//		textInput = new JTextArea();
		initComponents();
		center();
		updateStatus();
		
		enabled = true;
		redisplay();
//		mapView.redisplay(true);
	}

	/** Initialise display components */
	private void initComponents(){
		
		// -----------------[ menu bar ]----------------
		fileMenu.setText("Simulation");
		quitItem.setMnemonic(0);
		
		JMenu scenarioMenu = new JMenu("New");
		menuBar.add(scenarioMenu);
		for(int i = 0; i < Experiments.SCENARIOS.length; i++){
			JMenuItem scenarioItem = new JMenuItem(Experiments.getName(Experiments.SCENARIOS[i]));
			scenarioItem.setName("" + (Constants.MENU_SNO_LOAD + i));
			scenarioItem.addActionListener(MENU_LISTENER);
			scenarioMenu.add(scenarioItem);
		}
		scenarioMenu.add(new JSeparator());
		JMenuItem genRandomNew = new JMenuItem(Constants.getName(Constants.MENU_SNO_GEN_NEW));
		genRandomNew.setName("" + Constants.MENU_SNO_GEN_NEW);
		genRandomNew.addActionListener(MENU_LISTENER);
		JMenuItem genRandomEst = new JMenuItem(Constants.getName(Constants.MENU_SNO_GEN_EST));
		genRandomEst.setName("" + Constants.MENU_SNO_GEN_EST);
		genRandomEst.addActionListener(MENU_LISTENER);
		scenarioMenu.add(genRandomNew);
		scenarioMenu.add(genRandomEst);
		fileMenu.add(scenarioMenu, 0);
		
		int[] simItems = {
				Constants.MENU_SIM_OPEN,
				Constants.MENU_SIM_SVAS
		};
		for(int i = 0; i < simItems.length; i++){
			JMenuItem item = new JMenuItem(Constants.getName(simItems[i]));
			item.setName("" + simItems[i]);
			item.addActionListener(MENU_LISTENER);
			fileMenu.add(item, i + 1);
		}
		
		
		JMenu regimeMenu = new JMenu("Regime");
//		regimeMenu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(regimeMenu);
		int[] regItems = {
				Constants.MENU_REG_NEW,
				Constants.MENU_REG_OPEN,
				Constants.MENU_REG_SAVE,
				Constants.MENU_REG_SVAS
		};
		for(int i = 0; i < regItems.length; i++){
			JMenuItem item = new JMenuItem(Constants.getName(regItems[i]));
			item.setName("" + regItems[i]);
			item.addActionListener(MENU_LISTENER);
			regimeMenu.add(item, i);
		}
		
		JMenuItem helpItem = new JMenuItem(Constants.getName(Constants.MENU_HELP));
		helpItem.setMaximumSize(new Dimension(60, 50));
		helpItem.addActionListener(MENU_LISTENER);
		helpItem.setName("" + Constants.MENU_HELP);
		menuBar.add(helpItem);

		// scheme, schema, plan, plot, strategy, regieme, programme, schedule, agenda, setup
		
		// ---------------[ component panels ]------------

		feedPanel = new LivestockFeedPanel(displayParams, this, agent);
		evalPanel = new EvaluationPanel(this, displayParams, feedPanel);
		evalPanel.addModelItem(agent);

		// Create main control panel up top
		JPanel controlPanel = new JPanel(new GridLayout(2, 5, 5, 5));
//		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton buyButton = new JButton(new ImageIcon(BUY_ICON));
		buyButton.setBorder(new EmptyBorder(5, 5, 5, 5));
//		buyButton.setBounds(0, 0, 0, 0);
		buyButton.setToolTipText(Constants.TOOL_TIP_BUY);
		buyButton.setName("B");
		buyButton.addMouseListener(SHOW_MENU);
		controlPanel.add(buyButton);
		JButton sellButton = new JButton(new ImageIcon(SELL_ICON));
		sellButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		sellButton.setToolTipText(Constants.TOOL_TIP_SELL);
		sellButton.setName("S");
		sellButton.addMouseListener(SHOW_MENU);
		controlPanel.add(sellButton);
		JButton feedButton = new JButton(new ImageIcon(FEED_ICON));
		feedButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		feedButton.setToolTipText(Constants.TOOL_TIP_FEED);
		feedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				feedPanel.displayWindow(true);
			}
		});
		controlPanel.add(feedButton);
		JButton grazeButton = new JButton(new ImageIcon(FPLAN_ICON));
		grazeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		grazeButton.setToolTipText(Constants.TOOL_TIP_FPLAN);
		final Component iFace = this;
		grazeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				grazingPlanner.showPlanners(iFace);
			}
		});
		controlPanel.add(grazeButton);
		JButton cropButton = new JButton(new ImageIcon(CPLAN_ICON));
		cropButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		cropButton.setToolTipText(Constants.TOOL_TIP_CPLAN);
		cropButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cropPlanner.showPlanners(iFace);
			}
		});
		controlPanel.add(cropButton);
/*
		JButton decButton = new JButton(new ImageIcon(DEC_ICON));
		decButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		decButton.setToolTipText(Constants.TOOL_TIP_DEC);
		//   	decButton.setEnabled(false);
		// Add decision button listener
		decButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				decThread = new Thread(){
					public void run(){
						// Only allow one window at a time
						if(decOpen) return;
						decOpen = true;
						Popup.queryDecisions(null, agent.getNarratives());
						decOpen = false;
					}
				};
				decThread.start();
			}
		});
		controlPanel.add(decButton);
*/
/*		JButton transactionsButton = new JButton(new ImageIcon(TRANS_ICON));
		transactionsButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		transactionsButton.setToolTipText(Constants.TOOL_TIP_TRANS);
		transactionsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				marketPanel.displayWindow(true);
			}
		});
		controlPanel.add(transactionsButton);
*/		JButton evalButton = new JButton(new ImageIcon(EVAL_ICON));
		evalButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		evalButton.setToolTipText(Constants.TOOL_TIP_EVAL);
		evalButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				evalPanel.displayWindow(true);
			}
		});
//		evalButton.setEnabled(false);
		// Add evaluation button listener
		controlPanel.add(evalButton);
		nextButton = new JButton(new ImageIcon(NEXT_ICON));
		nextButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		nextButton.setToolTipText(Constants.TOOL_TIP_NEXT);
		// Add done button listener
		nextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				enableActions(false);
				// Go ahead and complete round
				goForNextStep();
				enableActions(true);
			}
		});
		controlPanel.add(nextButton);

		monthButton = new JButton(new ImageIcon(NEXT2_ICON));
		monthButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		monthButton.setToolTipText(Constants.TOOL_TIP_NEXT2);
		// Add done button listener
		monthButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//System.out.println("Starting new " + SKIP_WEEKS + "-week thread...");
				enableActions(false);
				// Go ahead and complete round
				new Thread(){ public void run(){
				for(int i = 0; i < SKIP_WEEKS; i++){
//System.out.println("Waiting to enter starter block...");
					synchronized(stepMonitor){
						new Thread(){ public void run(){
							synchronized(stepMonitor){
//							Thread.yield();
//System.out.println("Starting step");
//try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
								goForNextStep();
							}
						}}.start();
						try{
//System.out.println("Waiting " + i);
	                        stepMonitor.wait();
                        }catch(InterruptedException e1){ e1.printStackTrace(); }
					}
				}
//System.out.println("Exited starter block");
				enableActions(true);
//System.out.println("Done Thread\n");
				}}.start();
			}
		});
		controlPanel.add(monthButton);




		// Create time/funds panel
		timeFundPanel = new JPanel();
		timeFundPanel.setLayout(new GridBagLayout());
		timeFundPanel.setBorder(new EmptyBorder(0, 15, 0, 15));

		// Create map view
		maps = new MapView();
		maps.setBorder(new EtchedBorder());
		maps.setToolTipText(Constants.TOOL_TIP_MAP);
		// Create map control panel
		int[] mapViews = {
				Constants.INPUT_FIELD_CROP,
				Constants.TEMPORAL_CROP_YIELD,
//				Constants.TEMPORAL_SOIL_TEMP,
				Constants.TEMPORAL_SOIL_WATER,
				Constants.TEMPORAL_SOIL_NITRATE,
				Constants.TEMPORAL_SOIL_LEACHING,
//				Constants.TEMPORAL_CROP_DM,
//				Constants.TEMPORAL_CROP_LAI,
		};
		String[] mapToolTips = {
				"crop type",
				"expected crop yield",
				"soil water content",
				"soil nitrate content",
				"soil nitrate leaching"
		};
		JPanel mapSelector = new JPanel(new GridLayout(1, mapViews.length));
//		mapSelector.add(new JLabel(" View"));
		viewButtons = new JButton[mapViews.length];
		for(int i = 0; i < mapViews.length; i++){
			viewButtons[i] = new JButton(Constants.getName(mapViews[i]));
			viewButtons[i].setPreferredSize(new Dimension(0, 20));
//			viewButtons[i].setBorder(new EmptyBorder(0, 0, 0, 0));
			viewButtons[i].setBorderPainted(false);
//			viewButtons[i].setName("" + mapViews[i]);
			viewButtons[i].setToolTipText("Show " + mapToolTips[i] + " on map");
			mapSelector.add(viewButtons[i]);
			final int v = mapViews[i];
			viewButtons[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
//					System.out.println("arg - " + fieldMap);
					if(fieldMap != null){
						JButton src = (JButton)e.getSource();
						//src.getName
						fieldMap.setVisibleAttribute(v);
//						System.out.println("SET ATTRIBUTE " + v);
						maps.setDataStyle(Constants.STATIC_FIELD_MAP, fieldMap.getStyle(displayParams));
						for(JButton b : viewButtons){
							SubjectDisplayParams.setStyle(b, SubjectDisplayParams.NORMAL);
						}
						SubjectDisplayParams.setStyle(src, SubjectDisplayParams.ACTIVE);
					}
				}
			});
			mapSelector.add(viewButtons[i]);
		}
		// Add map view listener
		final JPopupMenu fieldMenu = new JPopupMenu();
		ActionListener fieldListener = HANDLE_FIELD_CLICK;
		final JLabel titleItem = new JLabel();
		titleItem.setBorder(new EmptyBorder(3, 3, 3, 3));
		titleItem.setMaximumSize(new Dimension(270, 20));
		SubjectDisplayParams.setStyle(titleItem, SubjectDisplayParams.INFO);
		fieldMenu.add(titleItem);
		final JLabel dataItem = new JLabel();
		dataItem.setBorder(new EmptyBorder(3, 3, 3, 3));
		dataItem.setMaximumSize(new Dimension(270, 20));
		SubjectDisplayParams.setStyle(dataItem, SubjectDisplayParams.INFO);
		fieldMenu.add(dataItem);
		// Create open planner item
		JMenuItem openPlanner = new JMenuItem("Open Crop Planner");
		openPlanner.setFont(openPlanner.getFont().deriveFont(Font.BOLD));
		openPlanner.setName("-2");
		openPlanner.addActionListener(fieldListener);
		fieldMenu.add(openPlanner);
		actionControls.add(openPlanner);
		fieldControls.add(openPlanner);
		// Create create planner item
		groupMenu = new JMenu("Add to group");
		JMenuItem newGroup = new JMenuItem("New Group");
		newGroup.setName("-1");
		newGroup.addActionListener(fieldListener);
		groupMenu.add(newGroup);
		groupMenu.add(new JSeparator());
		fieldMenu.add(groupMenu);
		JMenuItem remGroup = new JMenuItem("Remove from groups");
		remGroup.setName("-4");
		remGroup.addActionListener(fieldListener);
		fieldMenu.add(remGroup);
		// Create sub-menu
/*
		JMenuItem createPlanner = new JMenuItem("Add to group");
		createPlanner.setFont(openPlanner.getFont().deriveFont(Font.BOLD));
		createPlanner.setName("-1");
		createPlanner.addActionListener(fieldListener);
		fieldMenu.add(createPlanner);
		actionControls.add(createPlanner);
		fieldControls.add(createPlanner);
*/		// Add other controls
		fieldMenu.add(new JSeparator());
		for(int aId : agent.getActions(DairyAgent.FIELD_ACTION)){
			JMenuItem actionItem = new JMenuItem(Constants.getName(aId));
			actionItem.setName("" + aId);
			actionItem.addActionListener(fieldListener);
			fieldMenu.add(actionItem);
			actionControls.add(actionItem);
			fieldControls.add(actionItem);
		}
		// For left click (or right click when no features were originally selected)
		maps.getSelector().add(new Listener(){
			public void gestureFinished(){
				// Hide last menu if it was visible
				if(visibleMenu != null){
					visibleMenu.setVisible(false);
					visibleMenu = null;
				}
	//			maps.getLayerViewPanel().getSelectionManager().
				// Retrive mouse event
				MouseEvent e = maps.getFieldSelectEvent();
				// If no mouse event associated with it then just return
				if(e == null) return;
				// On left click field should be selected, hide this menu if visible
				if(e.getButton() == MouseEvent.BUTTON1)
					fieldMenu.setVisible(false);
				// On right click bring up popup menu
				else fieldRightClick(e, titleItem, dataItem, fieldMenu);
			}
		});
		// For right click with multiple fields selected
		maps.getLayerViewPanel().addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				// Hide last menu if it was visible
				if(visibleMenu != null){
					visibleMenu.setVisible(false);
					visibleMenu = null;
				}
				if(e.getButton() != MouseEvent.BUTTON1 &&
				maps.getLayerViewPanel().getSelectionManager().getSelectedItems().size() > 1)
					fieldRightClick(e, titleItem, dataItem, fieldMenu);
			}		
		});		

		// Greate graphs panel for visualisaing market data
		graphs = new DataView(3, 1,	displayParams, false);

		// Create store panel
		storePanel = new JPanel(new GridLayout(1, Storage.STORE_BASE.size(), 10, 10));
		storePanel.setBorder(new EmptyBorder(10, 0, 0, 0));


		
		
		// -----------------[ Main panels ]-------------

		// Create north panel - for main model controls and visualisations of time, balance and weather
		northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(0, SubjectDisplayParams.NORTH_HEIGHT));
		northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		northPanel.add(controlPanel, BorderLayout.WEST);
		northPanel.add(timeFundPanel, BorderLayout.CENTER);

		// Create east panel - for market price graphs
		eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(graphs, BorderLayout.CENTER);
		eastPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create south panel - for visualising stores
		southPanel = new JPanel(new BorderLayout());
		southPanel.add(storePanel, BorderLayout.CENTER);
		southPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create west panel - for input data

		// Create centre panel - for map visualisations
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(mapSelector, BorderLayout.NORTH);
		centerPanel.add(maps, BorderLayout.CENTER);
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create main panel to contain all component panels
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(northPanel, BorderLayout.NORTH);
		centerPanel.setMinimumSize(new Dimension(0, 0));
		eastPanel.setMinimumSize(new Dimension(0, 0));
		JSplitPane mapGraphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, eastPanel);
		mapGraphPane.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent e){
				if(maps != null && enabled) maps.revalidate();
			}
		});
		mapGraphPane.setDividerLocation(SubjectDisplayParams.WIDTH - SubjectDisplayParams.EAST_WIDTH);
		mapGraphPane.setContinuousLayout(true);
//		mapGraphPane.setOneTouchExpandable(true);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		mainPanel.add(mapGraphPane, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);


		// --------------[ set window properties ]-------------
		
		// Add close listener for main window
		if(params.GUI_HUMAN_CLOSABLE){
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					((JFrame)e.getSource()).dispose();
					sim.close();
				}
			});
		}

//		pack();
		setResizable(true); // TODO: Set resizable to false for real thing
//		setMinimumSize(new Dimension(SubjectDisplayParams.WIDTH, SubjectDisplayParams.HEIGHT));
		setSize(new Dimension(SubjectDisplayParams.WIDTH, SubjectDisplayParams.HEIGHT));
//		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//		gd.setFullScreenWindow(this);

	}

	/** */
/*	public static JPanel genInputPanel(JPanel panel, String title){
		JLabel titleLabel = new JLabel(" " + title);
//		titleLabel.setPreferredSize(new Dimension(SubjectDisplayParams.WEST_WIDTH - 13, 22));
		SubjectDisplayParams.setStyle(titleLabel, SubjectDisplayParams.TITLE);
		panel.setBorder(new EtchedBorder());
		panel.add(titleLabel);
		return panel;
	}
*/

	/** @inheritDoc */
	public void addDisplayItem(Data item){
		List<Data> time = new LinkedList<Data>();
		addDisplayItem(item, getPastData(sim, item, time, displayParams), time);
	}

	private int recDepth = 0;
	/** @inheritDoc */
	private void addDisplayItem(Data item, Hashtable<String, List<Data>> pastData, List<Data> time){
		recDepth++;
//		System.out.println("SubjectInterface.addDisplayItem(): Adding:" + item);
//		int group = displayParams.getGroup(item.getId());
//		if(group == Constants.DATA_GROUP_WEATHER)

		if(item instanceof Storage){
//			System.out.println("Adding storage");
			store = (Storage)item;
			Store[] stores = store.getStores();
//			System.out.println("Stores:" + stores.length);
			storeItems = new StorageItem[stores.length];
			for(int j = 0; j < stores.length; j++){
				Color[] cols = new Color[stores[j].items.length];
				for(int i = 0; i < stores[j].items.length; i++)
					cols[i] = displayParams.getColour(stores[j].items[i].getId());
				storeItems[j] = new StorageItem(displayParams, Constants.getName(j + Storage.OFFSET),
						stores[j].capacity,
						stores[j].items, cols);
				storeItems[j].setToolTipText(
						Constants.TOOL_TIP_STORES[j] +
						", with capacity of " + stores[j].capacity.getValue() +
						" " + stores[j].capacity.getUnits());
				storePanel.add(storeItems[j]);
			}
//System.out.println("adding store!");
//			marketPanel.setStore(store);
			evalPanel.addModelItem(store);
		}
	
		if(item instanceof DairyFarm){
			DairyFarm farm = (DairyFarm)item;
			weather = new WeatherView(
					farm.getRain(),
					farm.getTemp(),
					farm.getRad(),
					farm.getRadMean());
			northPanel.add(weather, BorderLayout.EAST);
			evalPanel.addModelItem(farm);
		}

		if(item instanceof CowDigestion){
			feedPanel.setCowDigestion((CowDigestion)item);
		}
		
		if(item instanceof Livestock){
			feedPanel.setLivestock((Livestock)item);
			evalPanel.addModelItem(item);
		}
		
		// Add all objects 
		if(item instanceof Data0D || item instanceof Data1D){
			if(item instanceof Time && this.time == null){
				GridBagLayout layout = (GridBagLayout)timeFundPanel.getLayout();
				this.time = (Time)item;
				evalPanel.addModelItem(item);
				final Time t = this.time;
				timeBar = new TimeProgressBar(t, t.intValue(), (int)(t.intValue() + params.TIME_CYCLE));
				timeBar.setToolTipText(Constants.TOOL_TIP_TIME);
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1.0;
				c.gridwidth = GridBagConstraints.REMAINDER;
				layout.setConstraints(timeBar, c);
				timeFundPanel.add(timeBar);
				timeBar.redisplay();
				// Create evaluation bar
				DoubleData[] prf = evalPanel.getPerformance();
				String[] abbr = {"Crp", "Lvs", "Env", "Fnc"};
				c.insets = new Insets(10, 0, 0, 0);
				for(int i = 0; i < prf.length; i++){
					JLabel title = new JLabel(abbr[i]);
//					title.setFont(title.getFont().deriveFont(10f));
					title.setHorizontalAlignment(JLabel.CENTER);
					c.gridwidth = i == prf.length - 1 ? GridBagConstraints.REMAINDER : 1;
					layout.setConstraints(title, c);
					timeFundPanel.add(title);
				}
				c.insets = new Insets(0, 4, 0, 4);
				pBars = new PercentBar[prf.length];
				for(int i = 0; i < prf.length; i++){
					pBars[i] = new PercentBar(prf[i], false, true);
					pBars[i].setPreferredSize(new Dimension(40, 17));
					c.gridwidth = i == prf.length - 1 ? GridBagConstraints.REMAINDER : 1;
					layout.setConstraints(pBars[i], c);
					timeFundPanel.add(pBars[i]);
				}
				c.insets = new Insets(0, 0, 0, 0);
			}
			if(item.getId() == Constants.DERIVED_ECONOMIC_BALANCE
					|| item.getId() == Constants.DERIVED_ECONOMIC_PROFIT){
				if(item.getId() == Constants.DERIVED_ECONOMIC_BALANCE)
					balance = (Data0D.Double)item;
				else
					profit = (Data0D.Double)item;
				if(profit != null && balance != null){
/*					JPanel pn = new JPanel(new BorderLayout());
					fundsLabel = new JLabel("Balance  £0"){
                        private static final long serialVersionUID = 1L;
						public void repaint(){
							if(balance.doubleValue() >= 0){
								setText("Balance  £" + Utils.roundString(Math.abs(balance.doubleValue()), 0));
							}else{
								setText("Balance -£" + Utils.roundString(Math.abs(balance.doubleValue()), 0));
							}
							super.repaint();
						}
					};
					fundsLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
					fundsLabel.setHorizontalAlignment(JLabel.LEFT);
					fundsLabel.setToolTipText(Constants.TOOL_TIP_BALANCE);
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 1.0;
					c.gridwidth = GridBagConstraints.REMAINDER;
					((GridBagLayout)timeFundPanel.getLayout()).setConstraints(pn, c);
					pn.add(fundsLabel, BorderLayout.CENTER);
					timeFundPanel.add(pn);
					fundsBar = new ProfitBar(profit);
					fundsBar.setToolTipText(Constants.TOOL_TIP_PROFITS);
					((GridBagLayout)timeFundPanel.getLayout()).setConstraints(fundsBar, c);
					timeFundPanel.add(fundsBar);
					fundsBar.redisplay();
*/				}
			}
			if(displayParams.display(item.getId(), SimulationDisplayParams.ADD_GRAPH)){
				graphs.addGraphItem(item, pastData.get(item.getId()), time, this.time.getJavaDate());
			}
		}
		else if(item instanceof DataNT && displayParams.display(item.getId(), DisplayParams.ADD_VALUE)){
//			System.out.println("SubjectInterface.addDisplayItem(): Adding:" + item.getName());
			if(item.getId() == Constants.STATIC_FIELD_MAP){
				fieldMap = (VectorData)item;
				Set<Integer> keys = fieldMap.getFeatureKeys();
				// Set maxFieldId
				maxFieldId = 0;
				for(Object k : keys){ if((Integer)k > maxFieldId) maxFieldId = (Integer) k; }
				double[] area = new double[maxFieldId + 1];
//				System.out.println("Maxkey:" + maxKey);
				for(int k : keys){
					area[(Integer)k] = (Double)fieldMap.getAttribute(Constants.STATIC_FIELD_AREA, k);
				}
				cropPlanner = new CroppingPlanner((SubjectDisplayParams)displayParams, groupMenu, fieldMap, maps, agent, this.time, area);
				grazingPlanner = new GrazingPlanner((SubjectDisplayParams)displayParams, agent, this.time, Livestock.COWS3PL + 1 - Livestock.HEIFERS01);
			}
			maps.addDisplayItem((DataNT)item, displayParams);
			viewButtons[0].doClick();
		}
		else if(item instanceof DataSet){
			if(item instanceof Market){
				market = (Market)item;
//				marketPanel.setEconomics((Market)item);
				evalPanel.addModelItem(item);
			}			
			for(Data subItem : ((DataSet)item).getItems()){
				addDisplayItem(subItem, pastData, time);
			}
		}
		if(--recDepth == 0){
			setVisible(true);
			revalidate();
			repaint();
//			System.out.println("REVALIATE");
		}
	}

	/** @inheritDoc */
	public void setDisplayItem(Data item) {
		if(item instanceof Data0D)
//			int group = displayParams.getGroup(item.getId());
		if(item instanceof Data2D)
//			System.out.println("Setting Raster "+ item.getNameId() + ":\n" + Utils.arrayString(((RasterMap)item).getRasterArray()));
			maps.setDisplayItem((Data2D)item);
		if(item instanceof DataTX){
//			Do nothing: we use time to display data. The redisplay process will set new data	
//			texts.setDisplayItem((DataTX)item);
		}
		if(item instanceof DataSet){
			for(Data subItem : ((DataSet)item).getItems())
				setDisplayItem(subItem);
		}
	}

	/** @inheritDoc */
	public void close(){
		/*		double gameTotal = ((Data0D.Double)agent.getItem(Constants.OUTPUT_REVENUE_GAME_TOTAL)).getValue();
		double grandTotal = ((Data0D.Double)agent.getItem(Constants.BANK_TOTAL)).getValue();
		DecisionPopup.inform("You raised a total of £" + Utils.roundString(gameTotal, 2) + " in this game.\n\n" +
				(grandTotal > gameTotal ? "The total amount of money earned so far is £" + Utils.roundString(grandTotal, 2) + "\n\n" : "" +
				"Click OK to continue"),
				"Game Complete");
		 */	    
		if(feedPanel != null) feedPanel.close();
		if(evalPanel != null) evalPanel.close();
		if(cropPlanner != null) cropPlanner.close();
		if(grazingPlanner != null) grazingPlanner.close();
		if(decThread != null) decThread.interrupt();
		dispose();
	}

	public static final int DISABLE_ALL = 1;
	public static final int ENABLE_ALL = 2;	
	public void setItemsEnabled(int status){
		for(Component c : actionControls)
			c.setEnabled(isActionPossible(Integer.parseInt(c.getName())));
	}
	boolean isActionPossible(int aId){
		return isActionPossible(agent.getActionSchema(aId)); }
	boolean isActionPossible(Action a){
		if(a.id == DairyAgent.ACTION_REVERT){
			if(a.args != null && a.args.length > 0){
				int field = ((IntegerData)a.args[0]).intValue();
//				Object attr = fieldMap.getAttribute(Constants.INPUT_FIELD_CROP, field);
//				System.out.println("SubjectInterface.isActionPoss() attr:" + attr + " field:" + field + " fieldMap:" + fieldMap);
				int crop1 = (Integer)fieldMap.getAttribute(Constants.INPUT_FIELD_CROP, field);
				int crop2 = prevCrop.get(field) == null ? Crop.FALLOW : prevCrop.get(field);
				return crop1 != crop2;
			}
			return true;
		}
		return agent.isActionPossible(a); }

	/** @inheritDoc */
	public void notifyStep(double progress){
//System.out.println("SubjectInterface.notifyStep()");
		doPlannedActions();
//System.out.println("notifyStep() 1");
		setProgress((int)(progress * 100));
		// Record all previous crop values
		for(int k : fieldMap.getFeatureKeys())
			prevCrop.put(k, (Integer)fieldMap.getAttribute(Constants.INPUT_FIELD_CROP, k));
//System.out.println("notifyStep() 2");
		
		updateStatus();

		enabled = true;
//		doneButton.setEnabled(true);
		redisplay();

//System.out.println("notifyStep() 3");
		// Notify all other panels
//		marketPanel.notifyStep();
		feedPanel.notifyStep();
		evalPanel.notifyStep();
		
		
/*		try {
			throw new Exception();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
//System.out.println("waiting to enter notify block...");
		synchronized(stepMonitor){
//System.out.println("notify()\n");
			stepMonitor.notify();
		}
	}
		
	private void doPlannedActions(){
//System.out.println("Carrying out planned actions");
		// Carry out planned field actions
		for(int i = 0; i <= maxFieldId; i++){
			Action a = cropPlanner.getAction(i);
//			if(a != null) System.out.println(a + ", possible? " + agent.isActionPossible(a));
			if(a != null && agent.isActionPossible(a))
				agent.performAction(a);
		}
		for(int i = 0; i <= Livestock.COWS3PL - Livestock.HEIFERS01; i++){
			Action a = grazingPlanner.getAction(i);
//				if(a != null) System.out.println(a + ", possible? " + agent.isActionPossible(a));
			if(a != null && agent.isActionPossible(a))
				agent.performAction(a);
		}
	}

	public void goForNextStep(){
//System.out.println("SubjectInterface.goForNextStep()");
		updateStatus();
//System.out.println("goForNextStep() 1");
		enabled = false;
		agent.done();
//System.out.println("goForNextStep() return");
	}
	
	
	/** Updates current status */
	private void updateStatus(){
/*		if(!landUseClicked){
//			setStatus(STATUS_MAKE_SELECTION);
			return;
		}
		if(!justificationMade){
//			setStatus(STATUS_GIVE_REASON);
			return;
		}
		if(!doneClicked){
//			setStatus(STATUS_CLICK_DONE);
			return;
		}
//		setStatus(STATUS_PROCESSING);
*/	}


	/** Refreshes the gui's data values, updating old values */
	public void redisplay(){
//		super.repaint();
		if(maps != null)
			maps.redisplay(true);
//		if(data != null)
//		data.redisplay();
//		if(weatherPanel != null)
//		weatherPanel.redisplay();
		if(weather != null)
			weather.redisplay();
//		if(storePanel != null)
//		storePanel.redisplay();
		updateStores();
		if(graphs != null)
			graphs.redisplay();
//		if(graphs2 != null)
//		graphs2.redisplay();
		if(timeBar != null)
			timeBar.redisplay();
		if(fundsBar != null)
			fundsBar.redisplay();
		if(cropPlanner != null)
			cropPlanner.redisplay();
		if(grazingPlanner != null)
			grazingPlanner.redisplay();
//		timeBar.validate();
//		timeBar.repaint();
//		southPanel.revalidate();
//		southPanel.repaint();
		if(fundsLabel != null)
			fundsLabel.repaint();
		if(pBars != null)
			for(PercentBar b : pBars) b.redisplay();

//		if(texts != null)
//		texts.redisplay();
	}

	/** refreshes the gui's layout/size etc where values have been added or removed */
	public void revalidate(){
		if(maps != null)
			maps.revalidate();
//		if(data != null)
//		data.revalidate();
//		if(weatherPanel != null)
//		weatherPanel.revalidate();
//		if(storePanel != null)
//		storePanel.revalidate();
		if(graphs != null)
			graphs.revalidate2();
//		if(graphs2 != null)
//		graphs2.revalidate();
		if(timeBar != null)
			timeBar.revalidate();
		if(fundsBar != null)
			fundsBar.revalidate();
		if(timeFundPanel != null)
			timeFundPanel.revalidate();
		
//		if(texts != null)
//		texts.revalidate();
//		super.revalidate();
	}

	/** Update storage visualisations */
	void updateStores(){
		if(storeItems != null)
			for(StorageItem s : storeItems) s.redisplay();
	}
	
	private void enableActions(boolean enable){
		nextButton.setEnabled(enable);
		monthButton.setEnabled(enable);
	}
	
	// ======================= Event Handlers ===============================

	/** Handle events from main menu */
	ActionListener MENU_LISTENER = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			JMenuItem src = (JMenuItem)e.getSource();
			int action = Integer.parseInt(src.getName());
			File toOpen;
			File toSave;
System.out.println("Selected menu item: " + Constants.getName(action));
			switch(action){
			case Constants.MENU_SIM_OPEN :
				toOpen = FileChooser.openFile(null, "Open Simulation", prefs.get(Constants.WORK_DIR_KEY, "."), Constants.FILE_EXT_SIM);
				if(toOpen != null) startSim(toOpen);				
				break;
			case Constants.MENU_SIM_SVAS :
					toSave = FileChooser.saveFile(null, "Save Simulation As", prefs.get(Constants.WORK_DIR_KEY, "."),
							sim.getSimFile().getPath(), Constants.FILE_EXT_SIM);
					if(toSave != null) saveSim(toSave);
				break;
			case Constants.MENU_SNO_GEN_NEW :
				new Thread(){
//System.out.println("?" + SwingUtilities.isEventDispatchThread());
//				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						IntegerData d = (IntegerData)Popup.queryData(new IntegerData(Constants.INPUT_RND_SEED, 0, Experiments.RND_SCENARIOS - 1));
						if(d != null)
							startSim(d.intValue() + Experiments.SCENARIOS.length);
//				}});
					}}.start();
				break;
			case Constants.MENU_SNO_GEN_EST :
				new Thread(){
//System.out.println("?" + SwingUtilities.isEventDispatchThread());
//				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						IntegerData d = (IntegerData)Popup.queryData(new IntegerData(Constants.INPUT_RND_SEED, 0, Experiments.RND_SCENARIOS - 1));
						if(d != null)
							startSim(d.intValue() + Experiments.SCENARIOS.length + Experiments.RND_SCENARIOS);
//				}});
					}}.start();
				break;
			case Constants.MENU_REG_NEW :
				newRegime();
				break;
			case Constants.MENU_REG_OPEN :
				toOpen = FileChooser.openFile(null, "Open Regime",
						prefs.get(Constants.WORK_DIR_KEY, "."), Constants.FILE_EXT_RGM);
				if(toOpen != null) openRegime(toOpen);
//System.out.println("Opening file:" + toOpen);
				break;
			case Constants.MENU_REG_SAVE :
				saveRegime(regimeFile);
System.out.println("Saving regime:" + regimeFile);
				break;
			case Constants.MENU_REG_SVAS :
					toSave = FileChooser.saveFile(null, "Save Regime As", prefs.get(Constants.WORK_DIR_KEY, "."),
							regimeFile.getPath(), Constants.FILE_EXT_RGM);
					if(toSave != null) saveRegime(toSave);
				break;
			case Constants.MENU_HELP :
				Popup.inform("For a complete user guide visit:\n" +
						"http://xweb.geos.ed.ac.uk/~s0127633/dairy\n\n" +
						"If there are any further questions\nfeel free to contact the author at:\n" +
						"conrad@crider.co.uk", Constants.getName(Constants.MENU_HELP));
				break;
			}
			if(action >= Constants.MENU_SNO_LOAD && action < Constants.MENU_REG_NEW){
				int scenario = action - Constants.MENU_SNO_LOAD;
				startSim(scenario);
			}
        }
		private void startSim(final int expt){
System.out.println("Starting sim:" + expt);
			new Thread(){
				public void run(){
//					String exptName = null;
					if(SimulationManager.startSimulation(expt) != -1)
						sim.close();
				}
			}.start();
		}
		private void startSim(final File simFile){
			System.out.println("Opening sim:" + simFile);
						new Thread(){
							public void run(){
//								String exptName = null;
								if(SimulationManager.startSimulation(simFile) != -1)
									sim.close();
							}
						}.start();
					}
		private void saveSim(File simFile){
System.out.println("Saving sim:" + simFile);
			sim.saveAs(simFile);
		}
		private void newRegime(){
			if(!Popup.verify("This will clear all existing farm regime data\n" +
					"including feed management, grazing and cropping plans.\n" +
					"Are you sure?", "New Farm Regime")) return;
			feedPanel.clearData();
			grazingPlanner.clearData();
			cropPlanner.clearData();
			regimeFile = new File(sim.getSimFile().getName().replaceAll(
					Constants.FILE_EXT_SIM, Constants.FILE_EXT_RGM));
		}
		private void openRegime(File toOpen){
			try{
				prefs.put(Constants.WORK_DIR_KEY, toOpen.getParent()); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(toOpen)));
				feedPanel.readData(reader);
				grazingPlanner.readData(reader);
				cropPlanner.readData(reader);
				reader.close();
				regimeFile = toOpen;
			}catch(IOException ex){
				ex.printStackTrace();
			}			
		}
		private void saveRegime(File toSave){
			try{
				prefs.put(Constants.WORK_DIR_KEY, toSave.getParent()); 
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toSave)));
				feedPanel.writeData(writer);
				grazingPlanner.writeData(writer);
				cropPlanner.writeData(writer);
				writer.flush();
				writer.close();
				regimeFile = toSave;
			}catch(IOException ex){
				ex.printStackTrace();
			}			
		}
	};
	
	/** Event handler to show buy/sell menus */
	MouseListener SHOW_MENU = new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			boolean buy = ((JButton)e.getComponent()).getName().equals("B");
			final JPopupMenu menu = new JPopupMenu();
			JLabel title = new JLabel(buy ? "Buy" : "Sell");
			title.setBorder(new EmptyBorder(3, 3, 3, 3));
			title.setMaximumSize(new Dimension(270, 20));
			SubjectDisplayParams.setStyle(title, SubjectDisplayParams.INFO);
			title.setPreferredSize(new Dimension(270,20));
			menu.add(title);
			ActionListener marketListener = buy ? HANDLE_MARKET_BUY : HANDLE_MARKET_SELL;
			int[] products = buy ? Market.PURCHASE_PRODUCTS : Market.SALE_PRODUCTS;
			if(!buy){
				int[] products_ = new int[products.length + Livestock.HERD_GROUPS.length - 1];
				for(int i = 0; i < products_.length; i++){
					if(i < products.length - 1)
						products_[i] = products[i];
					else
						products_[i] = Livestock.HERD_GROUPS[i - (products.length - 1)];
				}
				products = products_;
			}
			for(int i = 0; i < products.length; i++){
				Action a = agent.getActionExecutable(buy ? DairyAgent.ACTION_BUY : DairyAgent.ACTION_SELL);
				a.args[0] = new IntegerData(DairyAgent.ACTION_PARAM_PRODUCT, products[i]);
				JMenuItem actionItem = new JMenuItem(Constants.getName(products[i]) + (Livestock.isLivestock(products[i]) ? " : cull" : "") + "                             ");
				actionItem.add(new JLabel("                                    £" +
						Utils.roundString(market.getPrice(Livestock.isLivestock(products[i]) ? Market.CULL_COWS : products[i]), 2) +
						" / " + Market.getUnits(products[i])));
//				PricePanel actionItem = new PricePanel(
//						Constants.getName(Market.SALE_PRODUCTS[i]),
//						"£" + Utils.roundString(market.getUnitPrice(Market.SALE_PRODUCTS[i]), 2));
				actionItem.setName("" + products[i]);
				actionItem.addActionListener(marketListener);
				menu.add(actionItem);
				actionItem.setEnabled(isActionPossible(a));
			}
			menu.show(e.getComponent(), e.getX(), e.getY());	            
        }
		class PricePanel extends JPanel{
			public PricePanel(String name, String price){
				super(new BorderLayout());
				JLabel nameLabel = new JLabel(name);
				add(nameLabel, BorderLayout.CENTER);
				JLabel priceLabel = new JLabel(price);
				add(priceLabel, BorderLayout.EAST);
			}
		}
	};

	/** Event handler to deal with creation of buy actions */
	ActionListener HANDLE_MARKET_BUY = new ActionListener(){
		
		public void actionPerformed(final ActionEvent e){
			new Thread(){
				public void run(){
					handle(e);
				}
			}.start();
		}
		public void handle(ActionEvent e){	
//			JMenuItem item = (JMenuItem)e.getSource();
			int product = Integer.parseInt(((Component)e.getSource()).getName());
			Action action = agent.getActionExecutable(DairyAgent.ACTION_BUY);
			((IntegerData)action.args[0]).setValue(product);
			setQtyParam(action);
			if(isActionPossible(action)){
				// Set up permissable range
				((Data0D)action.args[1]).setRange(0, store.spaceAvailable(product));
				// Get quantity
				action.args[1] = Popup.queryData(null,
						"Buy " + Constants.getName(((IntegerData)action.args[0]).intValue()),
						action.args[1]);
				// Carry out action
				if(action.args[1] != null && ((IntegerData)action.args[1]).intValue() > 0){
					agent.performAction(action);
					evalPanel.getTransPanel().addTransaction();
					evalPanel.updateStats();
					updateStores();
				}
			}
			
		}
	};

	/** Event handler to deal with creation of sell actions */
	ActionListener HANDLE_MARKET_SELL = new ActionListener(){
		
		public void actionPerformed(final ActionEvent e){
			new Thread(){
				public void run(){
					handle(e);
				}
			}.start();
		}
		public void handle(ActionEvent e){	
//			JMenuItem item = (JMenuItem)e.getSource();
			int product = Integer.parseInt(((Component)e.getSource()).getName());
			Action action = agent.getActionExecutable(DairyAgent.ACTION_SELL);
			((IntegerData)action.args[0]).setValue(product);
			setQtyParam(action);
			if(isActionPossible(action)){
				// Set up permissable range
				((Data0D)action.args[1]).setRange(0, store.quantityStored(product));
				// Get quantity
				action.args[1] = Popup.queryData(null, 
						"Sell " + Constants.getName(((IntegerData)action.args[0]).intValue()),
						action.args[1]);
				// Carry out action
				if(action.args[1] != null && ((IntegerData)action.args[1]).intValue() > 0){
					agent.performAction(action);
					evalPanel.getTransPanel().addTransaction();
					evalPanel.updateStats();
					updateStores();
				}
			}
			
		}
	};
	
	/** Sets the quantity parameter of the given action to reflect the product being acted upon */
	private void setQtyParam(Action a){
		int p = ((Data0D.Integer)a.args[0]).intValue();
		if(p == Market.FERTILISER)
			a.args[1] = new IntegerData(
					DairyAgent.ACTION_PARAM_WEIGHT,
					Constants.UNITS_WEIGHT_HIGH, 0, 1);
		else if(
		   p == Market.SILAGE_GRASS
		|| p == Market.SILAGE_WHEAT
		|| p == Market.SILAGE_MAIZE
		|| p == Market.CONCENTRATES
		|| p == Market.HAY)
			a.args[1] = new IntegerData(
					DairyAgent.ACTION_PARAM_WEIGHT,
					Constants.UNITS_WEIGHTFW_HIGH, 0, 1);
		else if(p == Market.STORAGE_LIVESTOCK)
			a.args[1] = new IntegerData(
					DairyAgent.ACTION_PARAM_CAPACITY,
					Constants.UNITS_COWS, 0, 1);
		else if(Market.isStorageBuilding(p))
			a.args[1] = new IntegerData(
					DairyAgent.ACTION_PARAM_CAPACITY,
					Constants.UNITS_VOLUME_HIGH, 0, 1);
	}

	// Field click event handler
	private ActionListener HANDLE_FIELD_CLICK = new ActionListener(){
		public void actionPerformed(final ActionEvent e){
			new Thread(){
				public void run(){
					handle(e);
				}
			}.start();
		}
		public void handle(ActionEvent e){
//			JMenuItem item = (JMenuItem)e.getSource();
			int aId = Integer.parseInt(((Component)e.getSource()).getName());
			if(aId < 0){
				// Construct set of selected fields
				TreeSet<Integer> selected = new TreeSet<Integer>();
				for(Object f : maps.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems(
						maps.getLayerViewPanel().getLayerManager().getLayer(Constants.getName(Constants.STATIC_FIELD_MAP))))
					selected.add(((Feature)f).getID() - maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset());
				if(aId == -1)
					cropPlanner.createGroup(selected);
				else if(aId == -2)
					cropPlanner.showPlanners(maps, selected);
				else if(aId == -4)
					cropPlanner.removeFromGroup(selected);
			}
			else{
				// Calculate total selection area
				double aTotal = 0;
				for(Object f : maps.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems(
						maps.getLayerViewPanel().getLayerManager().getLayer(Constants.getName(Constants.STATIC_FIELD_MAP))))
					aTotal += (Double)fieldMap.getAttribute(Constants.STATIC_FIELD_AREA, ((Feature)f).getID() - maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset());
				// Create action to be carried out
				Action action = agent.getActionExecutable(aId);				
				// Set up parameters for actions requiring further input
				if(aId == DairyAgent.ACTION_FERTILISE || aId == DairyAgent.ACTION_SPREAD_SLURRY){
					// Set permissable range
//					System.out.println(store);
					if(aTotal > 0)
						((Data0D)action.args[1]).setRange(0, aId == DairyAgent.ACTION_FERTILISE ? 1E3 :
								Math.min(1E2, store.quantityStored(Livestock.MANURE_WET) / aTotal));
					// Query quantity
					action.args[1] = Popup.queryData(null,
							Constants.getName(aId),
							action.args[1]);
					// don't continue if canceled
					if(action.args[1] == null || ((IntegerData)action.args[1]).intValue() <= 0)
						return;
				}
				boolean actionPerformed = false;
				for(Object f : maps.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems(
						maps.getLayerViewPanel().getLayerManager().getLayer(Constants.getName(Constants.STATIC_FIELD_MAP)))){
					int selected = ((Feature)f).getID() - maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset();
					// Get a cloned copy of the action 
					Action a = (Action)action.clone();
					// Set field ID
					((Data0D.Integer)a.args[0]).setValue(selected);
					// Carry out action if possible
					if(isActionPossible(a)){
						actionPerformed = true;
						agent.performAction(a);
//						marketPanel.notifyStep();
//						agent.addActionToPerform(action);
					}
				}
				if(actionPerformed){
					maps.repaint();
					updateStores();
					evalPanel.notifyUpdate();
				}
			}
		}
	};

	
	// React to field right-click
	void fieldRightClick(MouseEvent e, JLabel titleItem, JLabel dataItem, JPopupMenu fieldMenu){
		Collection fields = maps.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems(
				maps.getLayerViewPanel().getLayerManager().getLayer(Constants.getName(Constants.STATIC_FIELD_MAP)));
		if(fields.size() == 0) return;
		Feature f = (Feature)fields.iterator().next();
		titleItem.setText(
				"F" + (f.getID()- maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset()) + " " +
				Constants.getName((Integer)f.getAttribute(Constants.getName(Constants.INPUT_FIELD_CROP))) + 
				" (" + Utils.roundString((Double)f.getAttribute(Constants.getName(Constants.STATIC_FIELD_AREA)), 1) + 
				" " + Constants.UNITS_AREA_HIGH + ")");
		int vaId = fieldMap.getVisibleAttribute();
		if(vaId == Constants.INPUT_FIELD_CROP){
			dataItem.setText(null);
		}
		else{
			Data vaSch = fieldMap.getAttributeSchema(vaId);
			Object vaVal = fieldMap.getAttribute(vaId, f.getID()
					- maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset());
			String val = vaVal.toString();
			if(vaVal instanceof Double) val = Utils.roundString((Double)vaVal, 1);
			if(vaSch instanceof Data0D) val = val + " " + ((Data0D)vaSch).getUnits();
			dataItem.setText(Constants.getName(vaId) + ": " + val);
		}
		for(Component c : fieldControls){
			boolean enabled = fields.size() > 0;
			int aId = Integer.parseInt(c.getName());
			if(aId >= 0){
				Action a = agent.getActionExecutable(aId);
				((IntegerData)a.args[0]).setValue(f.getID() -
						maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset());
				if(!isActionPossible(a)) enabled = false;
				c.setEnabled(enabled);
			}
		}
		visibleMenu = fieldMenu;
		fieldMenu.show(maps, e.getX(), e.getY());
		
	}
}
