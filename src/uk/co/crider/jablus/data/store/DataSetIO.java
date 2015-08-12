package uk.co.crider.jablus.data.store;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.data.Data2D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DataTX;
import uk.co.crider.jablus.utils.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/** Provides methods for loading and saving DataSet objects from disk
 * NOTE:
 * + Any DataSets containing datasets will be written to a different CSV file
 * + Each DataSet is associated with its own CSV file, with the same name as that dataset
 * + DataSets are not allowed to be un-named */
public class DataSetIO implements Closeable{

	public File baseDir;
	public Hashtable<String, DataSetSchema> dataSets;
	public boolean available;
	
	public DataSetIO(File baseDir){
		this.baseDir = baseDir;
		this.dataSets = new Hashtable<String, DataSetSchema>();
		this.available = baseDir.exists();
	}
	
	public boolean isAvailable(){
		return available;
	}
/*	public boolean canRead(DataSet dataSet){
		DataSetSchema schema = getSchema(dataSet);
		if(schema == null) return false;
		return schema.canRead();
	}
*/	
	public void initialise(){
		// Ensure base directory exists
		if(!baseDir.exists()){
			baseDir.mkdirs();
			available = true;
		}
	}
	
	/** Read all data into a vector*/
/*	public Vector<DataSet> readAll(DataSet template){
		Vector<DataSet> data = new Vector<DataSet>();
		if(!available) return data;
		DataSet row = null;
		while((row = readDataSet(template)) != null)
			data.add(row);
		return data;
	}
	
	public Vector<Snapshot> readAllSnapshots(DataSet template) throws IOException{
		Vector<Snapshot> snapshots = new Vector<Snapshot>();
		if(!available) return snapshots;
		DataSet data = null;
//System.out.println("DataSetIO: Reading DataSet with schema: " + template);
		while((data = readDataSet(template)) != null){
//System.out.println("           Read DataSet: " + data);
			snapshots.add(new Snapshot(data));
		}
//System.out.println();
		return snapshots;
	}	
*/
	
	/** Read dataSet from the next line of its file */
	public DataSet readDataSet(DataSet template){
		if(!available) return null;
		// TODO: Need to update schema if not data defined in template aint present
System.out.println("DataSetIO.readDataSet(): Reading from template: " + template);
		DataSetSchema schema = getSchema(template);
//System.out.println("DataSetIO.readDataSet(): Using schema: " + schema);
		DataSet read = schema.read();
//System.out.println("DataSetIO: Line read: " +  read);
//System.out.println();
		return read; //schema.read();
	}
	
	/** Read dataSet from the line identified by the given key string */
	public DataSet readDataSet(DataSet template, String keyValue){
		if(!available) return null;
		// TODO: Need to update schema if not data defined in template aint present
//System.out.println("DataSetIO.readDataSet(): Reading from template: " + template);
		DataSetSchema schema = getSchema(template);
//System.out.println("DataSetIO.readDataSet(): Using schema: " + schema);
		DataSet read = schema.read(keyValue);
//System.out.println("DataSetIO: Line read: " +  read);
//System.out.println();
		return read; //schema.read();
	}

	/** Write dataset */
	public void writeDataSet(DataSet data){
		if(!available) initialise();
//System.out.println("Writing data " + data);
		DataSetSchema schema = getSchema(data);
		schema.write(data);
	}
	
	private DataSetSchema getSchema(DataSet template){
//System.out.println("DataSetIO: Getting schema for " + template.getNameId());
		DataSetSchema schema = dataSets.get(template.getName());
		if(schema == null){
//System.out.println("DataSetIO: Building new schema for " + template.getNameId());
//System.out.println(template);
			schema = new DataSetSchema(template);
//System.out.println(schema);
			dataSets.put(template.getName(), schema);
		}
		return schema;
	}
	

	/** Finalises IO, by saving any remaining data and closing all file handles */
	public void close(){
	   for(DataSetSchema schema : dataSets.values()){
		   schema.close();
	   } 
    }

	/** Represents the layout of each savable dataset */
	class DataSetSchema implements Closeable{
		
		private DataSet template;
		private String keyName;
		private CSVFile csvFile;
		
		/** Creates a new schema from the given template */
		public DataSetSchema(DataSet template){
//System.out.println("DataSetIO.DataSetSchema: Generating schema from " + template + ", hasFile=" + template.hasFile());
			this.template = template;
			Data key = template.getKey();
			this.keyName = key == null ? null : key.getName();
			if(template.hasFile()){
				// If the dataSet has a name, then it has a CSV file to read from
				csvFile = new CSVFile(baseDir + File.separator + template.getName() + ".csv");
				// If the file is new then we will need to generate the header for it
				if(csvFile.isNew()){
					Vector<String> header = new Vector<String>();
					genHeader(template, header);
					csvFile.setHeader(header.toArray(new String[header.size()]));			
				}
			}
		}
//		public boolean canRead(){
//			return csvFile != null && csvFile.exists();
//		}
		
		// Populates header data from the dataset, accumulating values in header
		private void genHeader(DataSet data, Vector<String> header){
			for(Data item : data.getItems()){
				if(keyName != null && item.getName().equals(keyName)){
					header.add(0, keyName);
				}
				else if(item instanceof Data0D.Integer
				|| item instanceof Data0D.Double
				|| item instanceof Data2D
				|| item instanceof DataTX)
					header.add(item.getName());
				else if(item instanceof Data1D){
					for(String hTitle : ((Data1D)item).getCSVHeader())
						header.add(hTitle);
				}
				else if(item instanceof DataSet){
					// If seperate file then create schema for 
					if(((DataSet)item).hasFile())
						getSchema((DataSet)item);
					// Otherwise add component items to header
					else
						genHeader((DataSet)item, header);
				}
			}
		}
		
		/** Read a dataset from a line of CSV */
		public DataSet read(String key){
			// TODO
			return null;
		}
		
		/** Read a dataset from a line of CSV */
		public DataSet read(){
			// Read line from CSV file
			Map<String, ? extends Object> row = null;
			try{ row = csvFile.readLine(); }
			// Return null if exception thrown
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
			// Return null if no data returned 
			if(row == null || row.size() <= 0)
				return null;
			
//System.out.println("DataSetIO: Importing: " + template);
//System.out.println("DataSetIO: Read line...\n           " + row);
//System.out.println("\t from:" + csvFile);
			DataSet r = decode(row, template);
//System.out.println("DataSetIO: Imported: " + r);
			// Other wise, decode dataset
			return r;
		}
		private DataSet decode(Map<String, ? extends Object> input, DataSet template){
//System.out.println("DataSetIO.decode(): Decoding...\n\tTemplate:" + template + "\n\thasFile=" + template.hasFile() + "\n\tInput:"+input);
			// Copy dataset template
			DataSet output = (DataSet)template.clone();
			// Go through template items, setting values from csv data
			for(Data item : output.getItems()){
				if(item instanceof Data0D.Integer)
					((Data0D.Integer)item).setValue(Utils.parseInt((String)input.get(item.getName())));
				if(item instanceof Data0D.Double)
					((Data0D.Double)item).setValue(Utils.parseDouble((String)input.get(item.getName())));
				if(item instanceof Data1D.Integer){
					String[] csvHeader = ((Data1D.Integer)item).getCSVHeader();
//System.out.println("DataSetIO: decoding field " + item.getName() + ", " + jablus.utils.Utils.arrayString(csvHeader));
					int[] data = new int[csvHeader.length];
					for(int i = 0; i < csvHeader.length; i++){
//System.out.println("DataSetIO: headerdata @ " + csvHeader[i]);
						data[i] = Utils.parseInt((String)input.get(csvHeader[i]));
					}
					((Data1D.Integer)item).setValues(data);
				}
				if(item instanceof Data1D.Double){
//System.out.println("DataSetIO: decoding field " + item.getName());
					String[] csvHeader = ((Data1D.Double)item).getCSVHeader();
//System.out.println("  using header: " + Utils.arrayString(csvHeader));
					double[] data = new double[csvHeader.length];
					for(int i = 0; i < csvHeader.length; i++){
//System.out.println("DataSetIO: headerdata @ " + csvHeader[i]);
						data[i] = Utils.parseDouble((String)input.get(csvHeader[i]));
					}
//System.out.println("DataSetIO: decoded data: " + Utils.arrayString(data));
//System.out.println("DataSetIO: old data: " + (Data1D.Double)item);
					((Data1D.Double)item).setValues(data);
//System.out.println("DataSetIO: new data: " + (Data1D.Double)item);
				}
				if(item instanceof Data2D)
					((Data2D)item).readRaster(baseDir + File.separator + ((String)input.get(item.getName())));
				if(item instanceof DataTX)
					((DataTX)item).setText((String)input.get(item.getName()));
				if(item instanceof DataSet){
					// If seperate read from new file
					if(((DataSet)item).hasFile())
						((DataSet)item).setItem(readDataSet((DataSet)item));
					// Otherwise recursive call
					else
						((DataSet)item).setItem(decode(input, (DataSet)item));
				}
			}
			return output;
		}
		
	
		/** Write a dataset out to disk as a line of CSV, with the given time */
		public void write(DataSet data){
			String keyValue = keyName == null ? null : data.getItem(Constants.getIdFromName(keyName)).stringValue();
			// Encode data into a map
			Map<String, ? extends Object> line = encode(data, keyValue, new Hashtable<String, Object>());
//System.out.println("DataIO.write...\n" + data + "\n" + line);
//System.out.println("DataIO.write: csvFile=" + csvFile);
			// Attempt to write it to the CSV file
			try{ if(line != null) csvFile.writeLine(line); }
			// Print out any exceptions 
			catch(Exception e){	e.printStackTrace(); }
//System.out.println("DataSetIO: Wrote DataSet...\n           " + data);
		}
		private Map<String, ? extends Object> encode(DataSet input, String keyValue, Map<String, Object> output){
			// Map dataset objects to CSV data
			for(Data item : input.getItems()){
				if(item instanceof Data0D.Integer)
					output.put(item.getName(), ((Data0D.Integer)item).getValue());
				if(item instanceof Data0D.Double)
					output.put(item.getName(), ((Data0D.Double)item).getValue());
				if(item instanceof Data1D.Integer){
					String[] csvHeader = ((Data1D.Integer)item).getCSVHeader();
					int[] data = ((Data1D.Integer)item).getValues();
					for(int i = 0; i < csvHeader.length; i++){
						output.put(csvHeader[i], data[i]);
					}
				}
				if(item instanceof Data1D.Double){
					String[] csvHeader = ((Data1D.Double)item).getCSVHeader();
					double[] data = ((Data1D.Double)item).getValues();
					for(int i = 0; i < csvHeader.length; i++){
						output.put(csvHeader[i], data[i]);
					}
				}
				if(item instanceof Data2D){
					String fileName = item.getName() + Constants.SEPARATOR + keyValue + "." + ((Data2D)item).getFormat();
					output.put(item.getName(), fileName);
					((Data2D)item).writeRaster(baseDir.getPath() + File.separator + fileName);
				}
				if(item instanceof DataTX)
					output.put(item.getName(), ((DataTX)item).getText());
				if(item instanceof DataSet){
					// If using a seperate file then write new data set
					if(((DataSet)item).hasFile()){
//System.out.println("DataSetIO.encode: creating new schema for dataset " + item + " (hasFile is true)");
						writeDataSet((DataSet)item);
					}
					// Otherwise encode data and add to output
					else{
						encode((DataSet)item, keyValue, output);
					}
				}
			}
			return output;
		}

		/** Close the file associated with this dataset */
		public void close(){
//			System.out.println("DataSetIO: closing CSV file");
			try{ csvFile.close(); }
			catch(IOException e){ e.printStackTrace(); }
        }
		
		public String toString(){
			return template.toString();
		}

	}

}
