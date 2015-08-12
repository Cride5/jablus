package uk.co.crider.jablus.gui.sim;

import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.EventHandler;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.UserInterface;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/** User Interface use for controlling simulations. Designed for use by a researcher. */
public class SimulationGUI extends JablusWindow implements UserInterface{

	/** Unique class ID */
    private static final long serialVersionUID = -4566019385554860219L;
    
    private SimulationInterface sim;
    private SimulationPanel simPanel;
    private ParameterPanel paramPanel;
    private DisplayParams localParams;
//    private DisplayParams globalParams;
    private EventHandler eventHandler;

    
    // Initialisation methods -------------------------------------
    
    /** Creates a new un-initialised gui */
    public SimulationGUI(String title, DisplayParams displayParams){
    	super(title, true, true);
    }
    
    /** Initialises the gui using data from the simulation interface */
    public void setSimulation(SimulationInterface sim){
       	this.sim = sim;
   	// Create local [for this gui] display parameters 
    	localParams = new SimulationDisplayParams(sim.getExperiment());
    	// Set global params
//    	globalParams = displayParams;
    	// Add display parameters to global parameters
//    	globalParams.addParams(this.localParams);
     	eventHandler = new EventHandler(this, sim);
    	simPanel = new SimulationPanel(this, localParams, eventHandler);
    	paramPanel = new ParameterPanel();
    	// Initialise GUI
    	initComponents();	
    	// Ensure controls reflect state
    	eventHandler.enableControls();
    	// Show gui
    	center();
		setVisible(true);
    }
    
	/** Initialise GUI components */
	private void initComponents(){
		final JFrame thisFrame = this;
		// Set up file menu
		JMenu newMenu = new JMenu("New");
		for(int i = 0; i < sim.getExperiment().EXPERIMENTS.length; i++){
			JMenuItem exptItem = new JMenuItem(sim.getExperiment().EXPERIMENTS[i]);
			eventHandler.add(exptItem, EventHandler.FILE_NEW + i);
			exptItem.addActionListener(eventHandler);
			newMenu.add(exptItem);
		}
		fileMenu.add(newMenu, 0);
		
		JMenuItem openItem = new JMenuItem("Open...", KeyEvent.VK_O);
		eventHandler.add(openItem, EventHandler.FILE_OPEN);
		openItem.addActionListener(eventHandler);
		fileMenu.add(openItem, 1);
		
		JMenuItem renameItem = new JMenuItem("Rename...", KeyEvent.VK_R);
		eventHandler.add(renameItem, EventHandler.FILE_RENAME);
		renameItem.addActionListener(eventHandler);
		fileMenu.add(renameItem, 2);

		JMenuItem saveAsItem = new JMenuItem("Save As...", KeyEvent.VK_S);
		eventHandler.add(saveAsItem, EventHandler.FILE_SAVE_AS);
		saveAsItem.addActionListener(eventHandler);
		fileMenu.add(saveAsItem, 3);
		
		// Add close listener
    	eventHandler.add(this, EventHandler.FILE_QUIT);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				eventHandler.actionPerformed(new ActionEvent(thisFrame, ActionEvent.ACTION_PERFORMED, ""));
			}
		});
		
		// Set up View menu
		final JPanel panel = new JPanel(new CardLayout());
		panel.add(simPanel, "Simulation");
		panel.add(paramPanel, "Constants");
//		layout.show(panel, simPanel.toString());
		add(panel, BorderLayout.CENTER);

		JMenu viewMenu = new JMenu("View");
		JMenuItem simItem = new JMenuItem("Simulation");
		simItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				((CardLayout)panel.getLayout()).show(panel, "Simulation");
			}
		});
		viewMenu.add(simItem);
		JMenuItem paramItem = new JMenuItem("Constants");
		paramItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				((CardLayout)panel.getLayout()).show(panel, "Constants");
			}
		});
		viewMenu.add(paramItem);
		menuBar.add(viewMenu);
		
		
/*		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.LEFT);
		tabPane.setFont(SimulationDisplayParams.DEFAULT_FONT);
		tabPane.addTab(null, new VTextIcon(tabPane, "Simulation", VTextIcon.ROTATE_LEFT), simPanel);
		tabPane.addTab(null, new VTextIcon(tabPane, "Constants", VTextIcon.ROTATE_LEFT), paramPanel);
//		tabPane.addTab("Simulation", simPanel);
//		tabPane.addTab("Constants", paramPanel);
		add(tabPane, BorderLayout.CENTER);
*/				
		setMinimumSize(new Dimension(SimulationDisplayParams.WIDTH,SimulationDisplayParams.HEIGHT));
		setSize(new Dimension(SimulationDisplayParams.WIDTH,SimulationDisplayParams.HEIGHT));
	}
	
	
	// Implementing UserInterface -------------------------------------------
	
	/** @inheritDoc */
	public void addDisplayItem(Data item){
		// Add item to pastData list
		List<Data> time = new LinkedList<Data>();
		simPanel.addDisplayItem(item, getPastData(sim, item, time, localParams), time);
//		simPanel.addDisplayItem(item);
	}
	
	/** @inheritDoc */
	public void setDisplayItem(Data item){
		simPanel.setDisplayItem(item);
	}
	
	/** @inheritDoc Refreshes the gui to allow it to reflect new simulation state */
	public void notifyStep(double progress){
		setProgress((int)(progress * 100));
//System.out.println("SimulationGUI: canStepForward=" + sim.canStepForward() + ", enabling controls");
		eventHandler.enableControls();
//System.out.println("SimulationGUI: canStepForward=" + sim.canStepForward() + ", controls enabled");
//		super.repaint();
		simPanel.redisplay();
		repaint();
	}
	
	/** @inheritDoc */
	public void close(){
		dispose();
	}
	
	
	// Other non-essential mehtods ---------------------------------------------
	    
	/** For testing purposes */
	public static void main(String[] args){
//		SimulationGUI iface = new SimulationGUI();
	}
    /** Construct a new subject interface (for testing) */
    public SimulationGUI(SimulationPanel simPanel, ParameterPanel paramPanel){
    	super("Simulation", true, true);
    	this.simPanel = simPanel;
    	this.paramPanel = paramPanel;
//    	this.eventHandler = eventHandler;

    	// Initialise GUI
    	initComponents();
		
    	// Show gui
		setVisible(true);
    }
}
