package uk.co.crider.jablus.data.store;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/** A file to read and write CSV data. */
public class CSVFile implements Closeable{
	
	private String fileName;
	private String[] header;
	private CellProcessor[] processors;
	private CsvMapReader reader;
	private CsvMapWriter writer;

	public CSVFile(String fileName){
		try{
			this.fileName = fileName;
			File csvFile = new File(fileName);
			// Check whether the file exists
			boolean exists = csvFile.exists();
			// Creater writer and reader objects
			RandomLineAccessFile file = new RandomLineAccessFile(csvFile);
			reader = new CsvMapReader(file.getReader(), CsvPreference.STANDARD_PREFERENCE);
			writer = new CsvMapWriter(file.getWriter(), CsvPreference.STANDARD_PREFERENCE);
			// If the file exists then extract the header and cell processors 
			if(exists){
				header = reader.getCSVHeader(false);
				if(header != null)
					genProcessors();
			}
		}catch(Exception e){
			e.printStackTrace();
			header = null;
		}
	}
	
	/** Whether this is a new file */
	public boolean isNew(){
		return header == null;
	}
	
//	public boolean exists(){
//		return new File(fileName).exists();
//	}
	
	/** Sets the header for new CSV files */
	public void setHeader(String[] header){
		try{
			if(this.header == null){
				this.header = header;
				writer.writeHeader(header);
			}
		}catch(Exception e){
			e.printStackTrace();
			header = null;
		}
	}

	/** Generates the cell processors used to parse the CSV file */
	private void genProcessors(){
		// Generate cell processors from class types
		processors = new CellProcessor[header.length];
		for(int i = 0; i < header.length; i++){
/*			if(Constants.DATA_TYPES.get(header[i]) == Data0D.Integer.class)
				processors[i] = new ParseInt();
			else if(Constants.DATA_TYPES.get(header[i]) == Data0D.Double.class)
				processors[i] = new ParseDouble();
			else if(Constants.getType(header[i]) == Data1D.Integer.class)
				processors[i] = new ParseInt();
			else if(Constants.getType(header[i]) == Data1D.Double.class)
				processors[i] = new ParseDouble();
			// All other data types get String processors
			else
*/				processors[i] = new Optional();
		}
	}
	
	
	/** Reads a single row of the CSV file */
	public Map<String, ? extends Object> readLine(){
		// Cannot read without a header
		if(header == null) return null;
		try{
			Map<String, ? extends Object> read = reader.read(header, processors); 
//System.out.println("Reading:\n" + read + "\n" + Utils.arrayString(header));
			return read;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	/** Writes a single row to the CSV file */
	public void writeLine(Map<String, ? extends Object> output){
		// Cannot write without a header
		if(header == null) return;
//System.out.println("Writing:\n\t" + output + "\n Using header:\n\t" + Utils.arrayString(header));
		try{
			writer.write(output, header);
		}
		catch(IOException e){
//System.out.println("Could not write data item ");
			e.printStackTrace();
		}
	}
	
	/** Reads all the data into a vector of snapshot objects */
/*	public Vector<Snapshot> readAll() throws IOException{
		Vector<Snapshot> snapshots = new Vector<Snapshot>();
		Map<String, ? extends Object> row;
		while((row = reader.read(header, processors)) != null){
			snapshots.add(new Snapshot(row));
		}
		return snapshots;
	}
*/	
/*	public DataSet readData(int i){
		Map<String, ? extends Object> row = reader.read(int i);
		return genData(row);
		return null;
	}
*/	
	/** Decode objects from CSV data values and return a DataSet containing these values*/
/*	public static DataSet decode(Map<String, ? extends Object> data){
		CompoundData decoded = new CompoundData("CSV Data");
		for(String iname : data.keySet()){
			Object o = data.get(iname);
			if(Constants.DATA_TYPES.get(iname) == Data0D.Integer.class)
				decoded.addItem(new IntegerData(iname, ((Integer)o)));
			if(Constants.DATA_TYPES.get(iname) == Data0D.Double.class)
				decoded.addItem(new DoubleData(iname, ((Double)o)));
			if(Constants.DATA_TYPES.get(iname) == DataTX.class)
				decoded.addItem(new TextData(iname, ((String)o)));
		}
		return decoded;
	}
*/	
/*	public void writeData(DataSet data){
		try{
			Hashtable<String, Object> output = new Hashtable<String, Object>();
			encode(data, output);
System.out.println("Output:" + output + "\nHeader:\n"+ Utils.arrayString(header));
			writer.write(output, header);
		}
		catch(IOException e){
//System.out.println("Could not write data item ");
			e.printStackTrace();
		}
	}
*/	     

	/** Encode objects into CSV writable values and store in hashtable hashtable */
/*	public static void encode(Data item, Hashtable<String, Object> encoded){
		if(item instanceof Data0D.Integer)
			encoded.put(item.getNameId(), ((Data0D.Integer)item).getValue());
		if(item instanceof Data0D.Double)
			encoded.put(item.getNameId(), ((Data0D.Double)item).getValue());
		if(item instanceof Data1D);
			// Encode Data1D here
		if(item instanceof Data2D);
			// Encode Data2D here
		if(item instanceof DataTX){
			String str = ((DataTX)item).getText();
			encoded.put(item.getNameId(), str != null ? str.trim() : "");
		}
		if(item instanceof DataSet)
			for(Data subItem : ((DataSet)item).getItems())
				encode(subItem, encoded);
	}
*/

	public void close() throws IOException {
//	    reader.close();
	    writer.close();
    }
	
	public String toString(){
		return "CSVFile(" + fileName + ")";
	}
}
