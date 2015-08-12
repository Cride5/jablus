package uk.co.crider.jablus;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;

/** Defines the view of the environment available to all agents.
 * @see uk.co.crider.jablus.agent.Agent */
public interface EnvironmentInterface {

	/** Informs the environment of a new agent's presence */
	void notifyNewAgent(Agent agent);
	
	/** Informs the environment that the agent has made its decision */
	void notifyActionSelectionDone(Agent agent);
	
	/** Informs the environment that the agent has finished updating its knowledge */
	void notifyDoneUpdate(Agent agent);

	/** Informs the environment that the agent has carried out the given action */
	void notifyAction(Agent agent, Action action);
	
	/** Returns whether an action is currently possible */
	boolean isActionPossible(Action action);

}
