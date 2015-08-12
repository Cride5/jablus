package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.SimulationManager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/** Instances of this class handle all user generated events */
public class EventHandler implements ActionListener{
		
	// Controls
	public static final int SHOW_NARRATIVES = 1;
	public static final int START = 2;
	public static final int BACK = 3;
	public static final int PLAY = 4;
	public static final int PAUSE = 5;
	public static final int FORWARD = 8;
	public static final int END = 9;
	public static final int RECORD = 6;
	public static final int SNAPSHOT = 7;
	public static final int SHOW_OUTPUT = 10;
	
	public static final int FILE_NEW = 100;
	public static final int FILE_OPEN = 22;
	public static final int FILE_RENAME = 23;
	public static final int FILE_SAVE_AS = 24;
	public static final int FILE_QUIT = 25;
	
	private JablusWindow parent;
	private SimulationInterface sim;
	private Hashtable<Integer, Component> controls;
	private Hashtable<Component, Integer> mappings;

	public EventHandler(JablusWindow parent, SimulationInterface sim){
		this.parent = parent;
		this.sim = sim;
		this.controls = new Hashtable<Integer, Component>();
		this.mappings = new Hashtable<Component, Integer>();
	}
	
	/** Add a component to be handled */
	public void add(Component component, int control){
		controls.put(control, component);
		mappings.put(component, control);
		if(component instanceof JButton)
			((JButton)component).addActionListener(this);
	}
	
	/** Reacts to all action events */
	public synchronized void actionPerformed(ActionEvent e) {
		int control = mappings.get(e.getSource());
		switch(control){
		case START :
			sim.toStart();
			enableControls();
			break;
		case BACK :
			sim.stepBack();
			enableControls();
			break;
		case PLAY :
			sim.resume();
			enableControls();
			break;
		case PAUSE :
			sim.pause();
			enableControls();
			break;
		case FORWARD :
			sim.stepForward();
			enableControls();
			break;
		case END :
			sim.toEnd();
			enableControls();
			break;
		case RECORD :
			if(sim.isRecording())
				sim.setRecording(false);
			else
				sim.setRecording(true);
			enableControls();
			break;
		case SNAPSHOT :
			sim.takeSnapshot();
			enableControls();
			break;
/*		case FILE_NEW :
			System.out.println("SimEventHandler: New");
			new Thread(){
				public void run(){
					String exptName = null;
					SimulationManager.startSimulation(null);
				}
			}.start();
			break;
*/		case FILE_OPEN :
			System.out.println("SimEventHandler: Open");
			new Thread(){
				public void run(){
					File exptFile = FileChooser.chooseDir(parent, "Open Simulation Directory", new FileFilter(){
						public boolean accept(File f){
							if(!f.isDirectory())
								return false;
							if(new File(f.getAbsoluteFile() + File.separator + Constants.DATASET_SIMULATION + ".csv").exists())
								return true;
							File[] contents = f.listFiles();
							for(File fi : contents){
								if(fi.isDirectory()) return true;
							}
							return false;
						}
						public String getDescription(){ return "Simulation Folder"; }
					});
					if(exptFile != null){
						if(new File(exptFile.getAbsoluteFile() + File.separator + Constants.DATASET_SIMULATION + ".csv").exists())
							SimulationManager.startSimulation(0, exptFile, null, null);
						else
							parent.setStatus("Open Failed: The folder selected does not contain any simulation data.");
					}
				}
			}.start();
			break;
		case FILE_RENAME :
			System.out.println("SimEventHandler: Rename");
			break;
		case FILE_SAVE_AS :
			System.out.println("SimEventHandler: SaveAS");
			break;
		case FILE_QUIT :
			((JFrame)e.getSource()).dispose();
			sim.close();
			break;
		}
		if(control >= FILE_NEW && control < FILE_NEW + sim.getExperiment().EXPERIMENTS.length){
			System.out.println("SimEventHandler: New");
			final String exptName = sim.getExperiment().EXPERIMENTS[control - FILE_NEW];
			new Thread(){
				public void run(){
					SimulationManager.startSimulation(0, null, null, null);
				}
			}.start();
		}
    }

	/** Resets whether controls are enabled or disabled */
	public synchronized void enableControls(){
//System.out.println("EventHandler: Enabling controls, sim.canStepForward=" + sim.canStepForward());
		if(controls.containsKey(START   )) controls.get(START   ).setEnabled(sim.canToStart());
		if(controls.containsKey(BACK    )) controls.get(BACK    ).setEnabled(sim.canStepBack());
		if(controls.containsKey(PLAY    )) controls.get(PLAY    ).setEnabled(sim.canResume());
		if(controls.containsKey(PAUSE   )) controls.get(PAUSE   ).setEnabled(sim.canPause());
		if(controls.containsKey(FORWARD )) controls.get(FORWARD ).setEnabled(sim.canStepForward());
		if(controls.containsKey(END     )) controls.get(END     ).setEnabled(sim.canToEnd());
		if(controls.containsKey(RECORD  )) controls.get(RECORD  ).setEnabled(sim.canRecord());
		if(controls.containsKey(RECORD  )) ((ToolButton)controls.get(RECORD)).showAltIcon(sim.isRecording());
		if(controls.containsKey(SNAPSHOT)) controls.get(SNAPSHOT).setEnabled(sim.canTakeSnapshot());
//System.out.println("EventHandler: Enabled controls, sim.canStepForward=" + sim.canStepForward());
	}
	
}
