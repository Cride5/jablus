package uk.co.crider.jablus.data.store;

import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.data.DataSet;

/** Stores all simulation data in temporal snapshots and stores them to disk */
public class History extends DataTable{

	// Current state
	private Simulation currentState;
	private DataSetIO io;
	// Counter to keep track of where we are viewing
	private int i;
	
	/** Create a history object from saved data */
	public History(Simulation sim, DataSetIO io){
		super(sim, io);
		this.currentState = sim;
		this.io = io;
//System.out.println("History: creating history from dataset=" + sim + " read " + rows() + " rows of data");
//		this.snapshots = new Vector<Snapshot>();
		i = rows() == 0 ? -1 : rows();
		if(i > 0){
			currentState.setItem(getLast());
//System.out.println("History: set sim to data in:" + getLast());
		}
	}
	
	/** Load more data into snapshots, returns the loaded data */
/*	public DataSet load(DataSet template){
		DataSet data = null;
		if(snapshots.size() > 0){
			for(Snapshot snapshot : snapshots){
				DataSet newData = io.readDataSet(template);
//System.out.println("History: Data (pre-load)...\n         " + snapshot);
				if(newData != null){
					data = newData;
					snapshot.addItem(data);
				}
				else
					break;
//System.out.println("History: Data (post-load)...\n         " + snapshot);
			}
		}
		currentState.addItem(template);
//System.out.println("Loaded " + currentState);
		return data; 
	}
*/	
	/** Allows history object to save and close any files before simulation closes */
//	public void finalise(){
		// Do nothing, data should already be saved
//	}
	
	public void takeSnapshot(){
		if(!snapshotTaken()){
//System.out.println("History: Taking snaphsot: of " + currentState.getName() + ", " + currentState.getClass());
			io.initialise();
			put(currentState);
			i = rows();
		}
	}
	
	public DataSet getLastSnapshot(){
		if(rows() > 0)
			return getLast();
		return null;
	}

	public DataSet getSnapshot(){
		if(i == rows() || i == -1){
//System.out.println("Returning snapshot current state: i=" + i + ", size()="+snapshots.size());
			return currentState;
		}
//System.out.println("Returning snapshot " + i);
		return get(i);
	}

	public boolean snapshotTaken(){
//System.out.println("History.snapshotTaken: i="+i+", getLast()=" + getLast() + "\n\t currentState.getKey()="+currentState.getKey());
		return i >= 0 && getLast().getKey().stringValue().equals(currentState.getKey().stringValue());
	}
	public boolean canBack(){
		return i > 0 && !(rows() == 1 && snapshotTaken());
	}
	public boolean usingCurrent(){
		return i == rows() || i == -1;
	}
	private boolean lastIndex(){
		return i == rows() - 1;
	}
	
	public void start(){
		if(canBack())
			i = 0;
	}
	public void back(){
		if(canBack()){
			i--;
			if(lastIndex() && snapshotTaken())
				i--;
		}
	}
	public void forward(){
		if(!usingCurrent()){
			i++;
			if(lastIndex() && snapshotTaken())
				i++;
		}
	}
	public void end(){
		if(!usingCurrent())
			i = rows();
	}
}
