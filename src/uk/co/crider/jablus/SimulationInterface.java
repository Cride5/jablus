package uk.co.crider.jablus;

import java.io.File;

import uk.co.crider.jablus.data.DataSet;

/** Defines funtions available to any simluation user interface.
 * No user interface should reference a Simulation object directly.
 * All user interfaces should implement the UserInterface interface 
 * @see uk.co.crider.jablus.gui.UserInterface */
public interface SimulationInterface {
	
	// Basic stop/start functions
	/** Whether the simulation is paused */
	boolean isPaused();
	/** Whether it is possible to pause the simulation */
	boolean canPause();
	/** Pauses execution before the beginning of the next step */
	void pause();
	/** Whether the simulation can resume */
	boolean canResume();
	/** Informs the simulation to execute continously */
	void resume();

	// History browsing functions
	/** Returns the snapshot */
	DataSet getSnapshot();
	/** Returns whether the simulation can be jumped to the first state snapshot */
	boolean canToStart();
	/** Display the first state snapshot */
	void toStart();
	/** Whether it is possible to load a previous state */
	boolean canStepBack();
	/** Step backwards to previous state */
	void stepBack();
	/** Whether the state history browser is at the last executed state */
	boolean atCurrentState();
	/** Whether the simulation can step forwards to the next state */
	boolean canStepForward();
	/** Step forwards to the next state */
	void stepForward();
	/** Whether the simulation can step to the end */
	boolean canToEnd();
	/** Steps to the latest executed state */
	void toEnd();

	// State saving functions
	/** Whether a snapshot can be taken */
	boolean canTakeSnapshot();
	/** Take snapshot of current frame */
	void takeSnapshot();
	/** Whether the simulation is currently recording */
	boolean isRecording();
	/** Whether it is possible to start recording */
	boolean canRecord();
	/** Sets whether the simulation is recording or not */
	void setRecording(boolean recording);

	// File manipulation
	/** Informs the simulation to close, releasing all resoruces associated */
	void close();

	/** Returns experiment information */
	Parameters getExperiment();
	
	/** Whether the simluation position is at the last executed state */
//	boolean atLatestState();

	/** Returns the current state of the simulation */
//	int getState();
	
	/** Returns the file the simulation is saved as */
	File getSimFile();
	
	/** Saves the simulation using the given file */
	void saveAs(File simFile);

}
