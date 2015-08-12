package uk.co.crider.jablus.env;

/** Drivers which are updated in time implement this interface */
public interface DriverDynamic {
	
	/** Inform driver that step is about to take place */
	public void initStep();
	
	/** Calculate next state of affairs */
	public void execStep();

}
