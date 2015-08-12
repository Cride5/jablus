package uk.co.crider.jablus.data.store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import uk.co.crider.jablus.Parameters;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Experiments;

/** Used to store and retrive agent actions during a simulation */
public class ActionIO{
	/** Regex representing csv delimiter */
	public static final String CSV_DELIMITER = "\"?\\s*,\\s*\"?";
	
	private Time time;
	private File file;
	private int expt;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	/** Constructor for a new simulation */
	public ActionIO(File file, int expt){
		this.file = file;
		this.expt = expt;
		initialiseAttempted = false;
	}
	
	public static ActionIO loadSaved(File file){
		if(file == null || !file.exists()) return null;
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = reader.readLine();
			if(!line.contains("Scenario")) return null;
			String[] data = line.split(CSV_DELIMITER);
			if(data.length < 2) return null;
			int expt = Integer.parseInt(data[1]);
			// Read down to action headers
			do{ line = reader.readLine(); if(line == null) return null; }
			while(!line.matches("Time" + CSV_DELIMITER + "ActionID" + CSV_DELIMITER + "Action Parameters..."));
			return new ActionIO(file, expt, reader);
		}
		catch(IOException e){ e.printStackTrace(); }		
		return null;
	}
	
	/** Constructor for a loaded simulaiton */
	private ActionIO(File file, int expt, BufferedReader reader){
		this.file = file;
		this.expt = expt;
		this.reader = reader;
		// File is already initialised so no need to re-try
		initialiseAttempted = true;
	}
	
	private boolean initialiseAttempted =  false;
	/** Initialises a new file by creating a new file and writing header info,
	 * returns false if initialisation failed */
	public boolean initialise(){
//System.out.println("intiialising");
		initialiseAttempted = true;
		// If we already have a reader or writer then it is already initialised
		if(reader != null || writer != null) return true;
		try{
			if(file == null || expt == -1 || !file.createNewFile())
				return false;
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write("Scenario, " + expt);
			writer.newLine();
			writer.newLine();
			writer.write("Time, ActionID, Action Parameters...");
			writer.newLine();
			this.writer = writer;
			return true;
		}
		catch(IOException e){ e.printStackTrace(); }		
		return false;
	}
	
	/** Return experiment name */
	public int getExpt(){
		return expt;
	}
	
	/** Set time */
	public void setTime(Time time){
		this.time = time;
	}
	
	/** Saves the action, recording the time of occurance */
	public boolean saveAction(Action a){
		// Cannot save actions until all reading is complete
		if(reader != null) return false;
		if(!initialiseAttempted) initialise();
		if(writer == null || time == null) return false;
//System.out.println("Saving action: " + a + ", using writer:" + writer);
		try{
			writer.write(time.getTime() + ", " + a.id);
			for(int i = 0; i < a.args.length; i++)
				writer.write(", " + ((Data0D)a.args[i]).getValue());
			writer.newLine();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** Keeps track of the last line read while loading actions */
	private String line;
	/** Keeps track of the last time read */
//	private int lastTime;
	/** Returns actions associated with the current time.
	 * Returns null if this ActionIO is not loaded */
	public List<Action> loadActions(){
		if(reader == null || time == null) return null;
		List<Action> actions = new LinkedList<Action>();
		try{
			if(line == null) line = reader.readLine();
			while(line != null){
				String[] data = line.split(CSV_DELIMITER);
				if(data.length < 2) break;
				int[] d = new int[data.length];
				for(int i = 0; i < data.length; i++)
					d[i] = Integer.parseInt(data[i]);
				// Only return actions for current time
				if(d[0] > time.getTime()) break;
				else if(d[0] == time.getTime()){
					Data[] args = new Data[d.length - 2];
					for(int i = 2; i < d.length; i++)
						args[i - 2] = new IntegerData(0, d[i]);
					actions.add(new Action(d[1], args));
				}
//				lastTime = time.getTime();
				line = reader.readLine();
			}
//System.out.println("ActionIO: LINE:" + line);
			// If no more lines then switch from reading to writing
			if(line == null || line.matches("^\\s*$")){
//System.out.println("closing reader");
				reader.close();
				reader = null;
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return actions;
	}
	
	public boolean isCurrent(){
		return reader == null;
	}
	
	/** Copies data to a new file and initialises reading from it */
	public boolean copyTo(File file){
		if(file.equals(this.file)) return false;
//System.out.println("copying sim from file: " + this.file + " to file: " + file);
		try{
			// If file hasn't been initialised yet, or initialise failed,
			// then just create new file
			if(!initialiseAttempted || writer == null){
//System.out.println("Outright initialise ");
				this.file = file;
				return initialise();
			}
			// Otherwise, copy data to new file
			else{
//System.out.println("Beginning copy.... ");
				// Close existing file
				close();
				// Copy data to new file
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				String line = reader.readLine();
//System.out.println(line);
				while(line != null){
					writer.write(line);
					writer.newLine();
					line = reader.readLine();
				}
				this.file = file;
				return true;
			}
		}
		catch(IOException e){ e.printStackTrace(); }
		return false;
	}
	
	/** Closes file resources */
	public void close(){
		if(reader != null){
			try{ reader.close(); }
			catch(IOException e){ e.printStackTrace(); }
		}
		if(writer != null){
			try{ writer.flush(); }
			catch(IOException e){ e.printStackTrace(); }
			try{ writer.close(); }
			catch(IOException e){ e.printStackTrace(); }
		}
	}
}
