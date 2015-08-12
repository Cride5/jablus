package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.data.Data;

/** Provides methods which the simulation uses to access the interactive user interface.
 * All user interface implementations should implement this class.
 * Any references to the Simulation object should be maintained as SimulationInterface objects.
 * @see SimulationInterface */
public interface UserInterface {
	
	/** Called by the Simulation to pass its reference */
	public void setSimulation(SimulationInterface sim);
	
	/** Called by the Simulation to inform the UserInterface that a simulation step has completed. */
	public void notifyStep(double progress);

	/** Adds new display items to the GUI */
	public void addDisplayItem(Data item);

	/** Updates display items in the GUI */
	public void setDisplayItem(Data items);
	
	/** Informs the gui that the associated simulation is closing */
	public void close();

}
