package uk.co.crider.jablus;

/** Listens for closed simulations */
public interface CloseListener {
	
	/** Informs the listener that this simulation has closed */
	void simluationClosed(Simulation sim);
	
}
